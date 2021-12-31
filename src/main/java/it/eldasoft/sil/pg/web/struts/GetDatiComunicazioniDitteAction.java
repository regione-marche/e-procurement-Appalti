package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.GestoreProfili;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.utility.UtilityDate;
import net.sf.json.JSONObject;

public class GetDatiComunicazioniDitteAction extends Action {


  private SqlManager sqlManager;
  private PgManagerEst1 pgManagerEst1;
  private GeneManager geneManager;
  private TabellatiManager tabellatiManager;

  /**
   * @param sqlManager
   *        the sqlManager to set
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   * @param pgManagerEst1
   *        the sqlManager to set
   */
  public void setPgManagerEst1(PgManagerEst1 pgManagerEst1) {
    this.pgManagerEst1 = pgManagerEst1;
  }

  /**
   * @param geneManager
   *        the sqlManager to set
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();
    List<HashMap<String, Object>> hMap = new ArrayList<HashMap<String, Object>>();

    String ngara = request.getParameter("ngara");
    String codgar = request.getParameter("codgar");
    String ditta = request.getParameter("ditta");
    String genereGara = request.getParameter("genereGara");
    String riceviComunicazioni = request.getParameter("riceviComunicazioni");
    String soccorsoIstruttorio = request.getParameter("soccorsoIstruttorio");
    String whereBusteAttiveWizard = request.getParameter("whereBusteAttiveWizard");

    String entita = "GARE";
    if ("3".equals(genereGara)) {
      entita = "TORN";
      ngara= codgar;
    }



    try {

      //Nella lista prodotta le colonne saranno le seguenti
      // 0 - Oggetto
      // 1 - Data invio / inserimento
      // 2 - Testo
      // 3 - Idprg
      // 4 - Idcom
      // 5 - Tipo
      // 6 - Comtipo
      // 7 - Allegati presenti?
      // 8 - MsgErroreStato
      // 9 - descrizione busta
      String dittaMandanteRTOfferta = null;

      //Si deve determinare se la ditta è un RT
      String dittaGara = ditta;
      String mandataria = null;
      boolean mandatariaIngara=true;
      Long tipimp = (Long)this.sqlManager.getObject("select tipimp from impr where codimp=?", new Object[]{ditta});
       if(tipimp != null && (tipimp.longValue()==3 || tipimp.longValue()==10)){
        mandataria = (String)this.sqlManager.getObject("select coddic from ragimp where codime9=? and impman=?", new Object[]{ditta, "1"});
        dittaGara= mandataria;
        Long conteggio = (Long)this.sqlManager.getObject("select count(*) from ditg where codgar5=? and dittao=?", new Object[]{codgar,mandataria});
        if(conteggio==null || new Long(0).equals(conteggio))
          mandatariaIngara=false;
      }

      //Comunicazioni inviate
      String desdatinvString = sqlManager.getDBFunction("DATETIMETOSTRING",
          new String[] { "DESDATINV" });

      Object parametri[] = null;

      String select = "select COMMSGOGG, "  //0
          + desdatinvString + ","           //1
          + "COMMSGTES, "                   //2
          + "a.IDPRG, "                     //3
          + "a.IDCOM, "                     //4
          + "a.COMTIPO, "                   //5
          + "b.DESSTATO, "                  //6
          + "a.COMNUMPROT, "                //7
          + "a.COMTIPMA, "                  //8
          + "a.COMDATSCA, "                 //9
          + "a.COMORASCA "                  //10
          + "from W_INVCOM a, W_INVCOMDES b where coment=? and comkey1=? and comstato !=1 and"
          + " a.idprg = b.idprg and a.idcom = b.idcom and (descodsog= ? or COND1 or COND2) and b.idcomdes= "
          + "(select min(c.idcomdes) from W_INVCOMDES c where c.idprg=a.idprg and c.idcom = a.idcom and (c.descodsog=? or COND1.1 or COND2.1)) ";

      if ("1".equals(soccorsoIstruttorio)) {
        select += " and a.commodello = 1";
        if (whereBusteAttiveWizard != null && !"".equals(whereBusteAttiveWizard)) {
          whereBusteAttiveWizard = whereBusteAttiveWizard.replaceAll("W_INVCOM", "a");
          select += " and (" + whereBusteAttiveWizard + ")";
        }
      } else {
        select += " and (a.commodello is null or a.commodello <> 1)";
      }
      //Si deve controllare se vi sono ditte che hanno presentato ditte in RT, in questo caso si deve visualizzare la comunicazione
      //sia per il raggruppamento che per la ditta singola
      Long acquisizione = (Long)this.sqlManager.getObject("select acquisizione from ditg where ngara5=? and dittao=?", new Object[]{ngara,ditta});
      if(new Long(5).equals(acquisizione)){
         dittaMandanteRTOfferta = (String)this.sqlManager.getObject("select dittao from ditg where ngara5=? and rtofferta=? ", new Object[]{ngara,ditta});
        select=select.replaceFirst("COND1", "descodsog='" + dittaMandanteRTOfferta + "'");
        select=select.replaceFirst("COND1.1", "c.descodsog='" + dittaMandanteRTOfferta + "'");
      }else{
        select=select.replaceFirst("or COND1", "");
        select=select.replaceFirst("or COND1.1", "");
      }

      //Se la ditta è un RT e la mandataria non è presente in gara
      if(!mandatariaIngara){
        select=select.replaceFirst("COND2", "descodsog='" + mandataria + "'");
        select=select.replaceFirst("COND2.1", "c.descodsog='" + mandataria + "'");
      }else{
        select=select.replaceFirst("or COND2", "");
        select=select.replaceFirst("or COND2.1", "");
      }
      parametri = new Object[]{entita, ngara, ditta, ditta};

      List<?> comunicazioneInviate = this.sqlManager.getListVector(select, parametri);
      if (comunicazioneInviate != null && comunicazioneInviate.size() > 0) {
        Date data = null;
        Long dataLong=null;
        String idprg =null;
        Long idcom = null;
        Long comtipma = null;
        String descBusta = null;
        String testo = null;
        Timestamp comdatsca = null;
        String comorasca = null;
        for (int i = 0; i < comunicazioneInviate.size(); i++) {
          HashMap<String, Object> hMapComunicazioneInviata = new HashMap<String, Object>();
          hMapComunicazioneInviata.put("oggetto", SqlManager.getValueFromVectorParam(comunicazioneInviate.get(i), 0).getValue());
          desdatinvString = SqlManager.getValueFromVectorParam(comunicazioneInviate.get(i), 1).stringValue();
          data = UtilityDate.convertiData(desdatinvString, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
          dataLong = null;
          if (data != null)
            dataLong = data.getTime();
          hMapComunicazioneInviata.put("dataInvioInserimento", dataLong);
          testo = SqlManager.getValueFromVectorParam(comunicazioneInviate.get(i), 2).getStringValue();
          if ("1".equals(soccorsoIstruttorio) && testo != null) {
            comdatsca = SqlManager.getValueFromVectorParam(comunicazioneInviate.get(i), 9).dataValue();
            comorasca = SqlManager.getValueFromVectorParam(comunicazioneInviate.get(i), 10).getStringValue();
            if (comdatsca != null && comorasca != null) {
              testo += "<br>";
              testo += "Termine di presentazione dei documenti:" + UtilityDate.convertiData(comdatsca, UtilityDate.FORMATO_GG_MM_AAAA) + " " + comorasca;
            }
          }
          hMapComunicazioneInviata.put("testo", testo);
          idprg = SqlManager.getValueFromVectorParam(comunicazioneInviate.get(i), 3).stringValue();
          hMapComunicazioneInviata.put("idprg", idprg);
          idcom = SqlManager.getValueFromVectorParam(comunicazioneInviate.get(i), 4).longValue();
          hMapComunicazioneInviata.put("idcom", idcom);
          hMapComunicazioneInviata.put("tipo", "inv");
          hMapComunicazioneInviata.put("comtipo", SqlManager.getValueFromVectorParam(comunicazioneInviate.get(i),5).getValue());
          //Controllo sull'esistenza di allegati
          Long conteggioAllegati = (Long)this.sqlManager.getObject("select count(IDDOCDIG) from W_DOCDIG "
              + "where DIGENT = ? AND DIGKEY1 = ? AND DIGKEY2 = ?", new Object[]{"W_INVCOM", idprg, idcom.toString()});
          if(conteggioAllegati!=null && conteggioAllegati.longValue()>0)
            hMapComunicazioneInviata.put("allegati", "si");
          else
            hMapComunicazioneInviata.put("allegati", "no");
          hMapComunicazioneInviata.put("stato", SqlManager.getValueFromVectorParam(comunicazioneInviate.get(i),6).getStringValue());
          hMapComunicazioneInviata.put("protocollo", SqlManager.getValueFromVectorParam(comunicazioneInviate.get(i),7).getStringValue());
          if ("1".equals(soccorsoIstruttorio)) {
            comtipma = SqlManager.getValueFromVectorParam(comunicazioneInviate.get(i), 8).longValue();
            if (comtipma != null) {
              descBusta = this.tabellatiManager.getDescrTabellato("A1013", Long.toString(comtipma.longValue()));
            } else {
              descBusta = "";
            }
          }
          hMapComunicazioneInviata.put("descBusta", descBusta);
          hMapComunicazioneInviata.put("comtipma", comtipma);
          hMap.add(hMapComunicazioneInviata);
        }
      }

      if("true".equals(riceviComunicazioni)){
        //Comunicazioni ricevute

         String comdatinsString = sqlManager.getDBFunction("DATETIMETOSTRING",
             new String[] { "COMDATINS" });

         //Nel caso di offerte distinte le comunicazioni ricevute sono riferite alla gara e non al lotto
         String chiaveGaraComunicazioniRicev = ngara;
         if("1".equals(genereGara)){
           chiaveGaraComunicazioniRicev= codgar;
         }

        //Selezione dell comunicazioni ricevute escludendo quelle di risposta
        String selectCampi="select COMMSGOGG, "     //0
            + comdatinsString + ", "                //1
            + "COMMSGTES, "                         //2
            + "IDPRG, "                             //3
            + "IDCOM, "                             //4
            + "COMTIPO, "                           //5
            + "COMKEY1, "                           //6
            + "COMNUMPROT, "                        //7
            + "COMTIPMA, "                           //8
            + "COMDATSCA, "                         //9
            + "COMORASCA ";                         //10;

        String selectJoin = " from W_INVCOM,  W_PUSER  where comtipo=? and comstato = ? and coment is null and comkey2=? "
            + "and comkey1=usernome and userkey1=?";

        if ("1".equals(soccorsoIstruttorio)) {
          selectJoin += " and commodello = 1";
          if (whereBusteAttiveWizard != null && !"".equals(whereBusteAttiveWizard)) {
            whereBusteAttiveWizard = whereBusteAttiveWizard.replaceAll("a.", "");
            selectJoin += " and (" + whereBusteAttiveWizard +")";
          }
          GestoreProfili gestoreProfili = geneManager.getProfili();
          String profiloAttivo = (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);
          String profilo ="1";
          if (gestoreProfili.checkProtec(profiloAttivo, "FUNZ", "VIS", "ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare"))
            profilo ="2";
          else if (gestoreProfili.checkProtec(profiloAttivo, "FUNZ", "VIS", "ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare"))
            profilo ="3";
          selectJoin += this.pgManagerEst1.getFiltroComunicazioniSoccorsoIstruttorio(profilo);
        } else
          selectJoin += " and (commodello is null or commodello <> 1)";

        String ulterioriFiltri = " and (IDPRGRIS is null or IDCOMRIS is null)";
        select = selectCampi +selectJoin + ulterioriFiltri;
        List<?> comunicazioneRicevute = this.sqlManager.getListVector(select, new Object[]{"FS12", new Long(3), chiaveGaraComunicazioniRicev, dittaGara});
        if (comunicazioneRicevute != null && comunicazioneRicevute.size() > 0) {
          Date data = null;
          Long dataLong=null;
          Long idcom = null;
          String comkey1 = null;
          Long comtipma = null;
          String descBusta = null;
          String testo = null;
          Timestamp comdatsca = null;
          String comorasca = null;
          for (int i = 0; i < comunicazioneRicevute.size(); i++) {
            HashMap<String, Object> hMapComunicazioneRicevuta = new HashMap<String, Object>();
            hMapComunicazioneRicevuta.put("oggetto", SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 0).getValue());
            comdatinsString = SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 1).stringValue();
            data = UtilityDate.convertiData(comdatinsString, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
            dataLong =null;
            if(data!=null)
              dataLong = data.getTime();
            hMapComunicazioneRicevuta.put("dataInvioInserimento", dataLong);
            testo = SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 2).getStringValue();
            if ("1".equals(soccorsoIstruttorio) && testo != null) {
              comdatsca = SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 9).dataValue();
              comorasca = SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 10).getStringValue();
              if (comdatsca != null && comorasca != null)
                testo += " Termine di presentazione dei documenti:" + UtilityDate.convertiData(comdatsca, UtilityDate.FORMATO_GG_MM_AAAA) + " " + comorasca;
            }
            hMapComunicazioneRicevuta.put("testo", testo);
            hMapComunicazioneRicevuta.put("idprg", SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 3).getValue());
            idcom = SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 4).longValue();
            hMapComunicazioneRicevuta.put("idcom", idcom);
            hMapComunicazioneRicevuta.put("tipo", "ric");
            hMapComunicazioneRicevuta.put("comtipo", SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i),5).getValue());
            //Controllo sull'esistenza di allegati
            Long conteggioAllegati = (Long)this.sqlManager.getObject("select count(IDDOCDIG) from W_DOCDIG "
                + "where DIGENT = ? AND DIGKEY1 = ?", new Object[]{"W_INVCOM", idcom.toString()});
            if(conteggioAllegati!=null && conteggioAllegati.longValue()>0)
              hMapComunicazioneRicevuta.put("allegati", "si");
            else
              hMapComunicazioneRicevuta.put("allegati", "no");
            hMapComunicazioneRicevuta.put("stato","");
            if(comkey1 == null)
              comkey1 = SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 6).getStringValue();
            String commnumprot = SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 7).getStringValue();
            hMapComunicazioneRicevuta.put("protocollo",commnumprot);
            if ("1".equals(soccorsoIstruttorio)) {
              comtipma = SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 8).longValue();
              if (comtipma!=null) {
                descBusta = this.tabellatiManager.getDescrTabellato("A1013", comtipma.toString());
              } else {
                descBusta = "";
              }
            }
            hMapComunicazioneRicevuta.put("descBusta", descBusta);
            hMapComunicazioneRicevuta.put("comtipma", comtipma);
            hMap.add(hMapComunicazioneRicevuta);
          }
          result.put("comkey1", comkey1);
        }
        //Gestione delle comunicazioni ricevute che sono risposta
        //Si devono considerare solo le risposte a comunicazioni inviate alla ditta corrente (DESCODSOG.W_INVCOMDES = DITTAO.DITG)
        //o alla ditta da cui è derivata la ditta corrente (DESCODSOG.W_INVCOMDES = DITTAINV.DITG)
        String ulterioriCampi = ", IDPRGRIS, "  //11
            + "IDCOMRIS";                       //12
        ulterioriFiltri = " and (IDPRGRIS is not null and IDCOMRIS is not null)";
        select = selectCampi + ulterioriCampi + selectJoin + ulterioriFiltri;
        comunicazioneRicevute = this.sqlManager.getListVector(select, new Object[]{"FS12", new Long(3), chiaveGaraComunicazioniRicev, dittaGara});
        if (comunicazioneRicevute != null && comunicazioneRicevute.size() > 0) {
          Date data = null;
          Long dataLong=null;
          Long idcom = null;
          String comkey1 = null;
          String idprgris = null;
          Long idcomris = null;
          String selectDestinatari="select count(*) from w_invcomdes where idprg=? and IDCOM=? and (descodsog=?";
          if(dittaMandanteRTOfferta != null && !"".equals(dittaMandanteRTOfferta))
            selectDestinatari += " or descodsog='" + dittaMandanteRTOfferta + "'";
          if(mandataria != null && !"".equals(mandataria)){
            //Si devono considerare anche le risposte alla mandataria, ma solo se non è presente in gara

            if(!mandatariaIngara)
              selectDestinatari += " or descodsog='" + mandataria + "'";
          }
          selectDestinatari += ")";
          Long numDestinatari = null;
          Long comtipma = null;
          String descBusta = null;
          String testo = null;
          Timestamp comdatsca = null;
          String comorasca = null;
          for (int i = 0; i < comunicazioneRicevute.size(); i++) {
            idprgris = SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 11).getStringValue();
            idcomris = SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 12).longValue();
            numDestinatari = (Long)this.sqlManager.getObject(selectDestinatari, new Object[]{idprgris,idcomris,ditta});
            if(numDestinatari!=null && numDestinatari.longValue()>0){
              HashMap<String, Object> hMapComunicazioneRicevuta = new HashMap<String, Object>();
              hMapComunicazioneRicevuta.put("oggetto", SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 0).getValue());
              comdatinsString = SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 1).stringValue();
              data = UtilityDate.convertiData(comdatinsString, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
              dataLong =null;
              if(data!=null)
                dataLong = data.getTime();
              hMapComunicazioneRicevuta.put("dataInvioInserimento", dataLong);
              testo = SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 2).getStringValue();
              if ("1".equals(soccorsoIstruttorio) && testo != null) {
                comdatsca = SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 9).dataValue();
                comorasca = SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 10).getStringValue();
                if (comdatsca != null && comorasca != null)
                  testo += " Termine di presentazione dei documenti:" + UtilityDate.convertiData(comdatsca, UtilityDate.FORMATO_GG_MM_AAAA) + " " + comorasca;
              }
              hMapComunicazioneRicevuta.put("testo", testo);
              hMapComunicazioneRicevuta.put("idprg", SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 3).getValue());
              idcom = SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 4).longValue();
              hMapComunicazioneRicevuta.put("idcom", idcom);
              hMapComunicazioneRicevuta.put("tipo", "ric");
              hMapComunicazioneRicevuta.put("comtipo", SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i),5).getValue());
              //Controllo sull'esistenza di allegati
              Long conteggioAllegati = (Long)this.sqlManager.getObject("select count(IDDOCDIG) from W_DOCDIG "
                  + "where DIGENT = ? AND DIGKEY1 = ?", new Object[]{"W_INVCOM", idcom.toString()});
              if(conteggioAllegati!=null && conteggioAllegati.longValue()>0)
                hMapComunicazioneRicevuta.put("allegati", "si");
              else
                hMapComunicazioneRicevuta.put("allegati", "no");
              hMapComunicazioneRicevuta.put("stato","");
              if(comkey1 == null)
                comkey1 = SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 6).getStringValue();
              String commnumprot = SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 7).getStringValue();
              hMapComunicazioneRicevuta.put("protocollo",commnumprot);
              if ("1".equals(soccorsoIstruttorio)) {
                comtipma = SqlManager.getValueFromVectorParam(comunicazioneRicevute.get(i), 8).longValue();
                if (comtipma!=null) {
                  descBusta = this.tabellatiManager.getDescrTabellato("A1013", comtipma.toString());
                } else {
                  descBusta = "";
                }
              }
              hMapComunicazioneRicevuta.put("descBusta", descBusta);
              hMapComunicazioneRicevuta.put("comtipma", comtipma);
              hMap.add(hMapComunicazioneRicevuta);
            }
          }
          result.put("comkey1", comkey1);
        }
      }
      result.put("data", hMap);



    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura delle informazioni relative alle comunicazioni.", e);
    }

    out.println(result);
    out.flush();

    return null;

  }
}
