package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import net.sf.json.JSONObject;

public class GetW_INVCOMAction extends Action {

  /**
   * Manager per la gestione delle interrogazioni di database.
   */
  private SqlManager sqlManager;

  /**
   * @param sqlManager
   *        the sqlManager to set
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  private GestioneWSDMManager gestioneWSDMManager;
  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  private PgManagerEst1 pgManagerEst1;
  public void setPgManagerEst1(PgManagerEst1 pgManagerEst1) {
    this.pgManagerEst1 = pgManagerEst1;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();
    int totalW_INVCOM = 0;
    int totalW_INVCOMDES = 0;
    int totalW_INVCOMDES_NOCF = 0;
    int totalW_INVCOMDES_NOPEC = 0;
    int totalW_DOCDIG = 0;
    int TotalRecordsW_DOCDIG_NODESCRIZIONE =0;
    int TotalRecordsW_DOCDIG_ATTESAFIRMA = 0;
    List<HashMap<String, Object>> hMapW_INVCOM = new ArrayList<HashMap<String, Object>>();
    List<HashMap<String, Object>> hMapW_INVCOMDES = new ArrayList<HashMap<String, Object>>();
    List<HashMap<String, Object>> hMapW_DOCDIG = new ArrayList<HashMap<String, Object>>();
    boolean tuttiAllegatiFormatoValido=true;

    try {
      String idprg = request.getParameter("idprg");
      Long idcom = null;
      if (request.getParameter("idcom") != "" && !"".equals(request.getParameter("idcom"))) {
        idcom = new Long(request.getParameter("idcom"));
      }

      if (idprg != null && idcom != null) {
        // Lettura delle informazioni generali
        String selectW_INVCOM = "select commsgogg, comdatins, commsgtes, commodello,  comdatsca, comorasca from w_invcom where idprg = ? and idcom = ?";
        List<?> datiW_INVCOM = sqlManager.getVector(selectW_INVCOM, new Object[] { idprg, idcom });
        if (datiW_INVCOM != null && datiW_INVCOM.size() > 0) {
          totalW_INVCOM = datiW_INVCOM.size();
          HashMap<String, Object> hMap = new HashMap<String, Object>();
          hMap.put("commsgogg", SqlManager.getValueFromVectorParam(datiW_INVCOM, 0).getValue());
          Date comdatins = (Date) SqlManager.getValueFromVectorParam(datiW_INVCOM, 1).getValue();
          hMap.put("comdatins", UtilityDate.convertiData(comdatins, UtilityDate.FORMATO_GG_MM_AAAA));
          hMap.put("commsgtes", SqlManager.getValueFromVectorParam(datiW_INVCOM, 2).getValue());
          hMap.put("commodello", SqlManager.getValueFromVectorParam(datiW_INVCOM, 3).getValue());
          Timestamp comdatsca = SqlManager.getValueFromVectorParam(datiW_INVCOM, 4).dataValue();
          String comdatscaString = null;
          if(comdatsca!=null)
            comdatscaString = UtilityDate.convertiData(new Date(comdatsca.getTime()), UtilityDate.FORMATO_GG_MM_AAAA);
          hMap.put("comdatsca", comdatscaString);
          hMap.put("comorasca", SqlManager.getValueFromVectorParam(datiW_INVCOM, 5).getValue());
          hMapW_INVCOM.add(hMap);
        }

        // Lettura dei destinatari. Deve essere controllato anche il codice fiscale poiche' per l'invio
        // al sistema remoto e' obbligatorio.
        String selectW_INVCOMDES = "select desintest, desmail, descodent, descodsog, comtipma from w_invcomdes where idprg = ? and idcom = ?";
        List<?> datiW_INVCOMDES = sqlManager.getListVector(selectW_INVCOMDES, new Object[] { idprg, idcom });
        if (datiW_INVCOMDES != null && datiW_INVCOMDES.size() > 0) {
          totalW_INVCOMDES = datiW_INVCOMDES.size();
          Long comtipma = null;
          for (int i = 0; i < datiW_INVCOMDES.size(); i++) {
            String desintest = (String)SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 0).getValue();
            HashMap<String, Object> hMap = new HashMap<String, Object>();
            hMap.put("desintest", desintest);
            hMap.put("desmail", SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 1).getValue());

            String descodent = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 2).getValue();
            String descodsog = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 3).getValue();
            hMap.put("descodent", descodent);
            hMap.put("descodsog", descodsog);
            comtipma = (Long) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 4).getValue();

            String codiceFiscale = null;
            if ("TECNI".equals(descodent)) {
              codiceFiscale = (String) this.sqlManager.getObject("select cftec from tecni where codtec = ?",
                  new Object[] { descodsog });
            } else if ("IMPR".equals(descodent)) {
              //Si deve controllare se l'impresa è un raggruppamento, in quel caso si deve prendere il codice fiscale delle componenti
              Vector<?> datiImpr = this.sqlManager.getVector("select tipimp,cfimp from impr where codimp = ?", new Object[] { descodsog });
              if(datiImpr!=null && datiImpr.size()>0){
                Long tipimp = (Long) SqlManager.getValueFromVectorParam(datiImpr, 0).getValue();
                if(tipimp!= null && (tipimp.longValue()==3 || tipimp.longValue()==10)){
                  //Si deve cercare il codice fiscale a partire dal valore del campo desintest
                  //Il campo è così formato: Ragione sociale del raggruppamento - Ragione sociale componente - indicazione sulla tipologia di componente
                  /*
                  String datiRagioneSociale[] = desintest.split("-");
                  if(datiRagioneSociale!= null && datiRagioneSociale.length>2){
                    String ragSocialeComponente = datiRagioneSociale[1];
                    if (ragSocialeComponente!= null){
                      ragSocialeComponente = ragSocialeComponente.trim();
                      codiceFiscale = (String)this.sqlManager.getObject("select cfimp from impr,ragimp where codimp=coddic and codime9=? and nomdic=?", new Object[]{descodsog, ragSocialeComponente});
                    }
                  }
                  */
                  String datiAnagraficiComponente[] = this.gestioneWSDMManager.getDatiImpresaComponente(idprg, idcom, descodsog, desintest);
                  if(datiAnagraficiComponente!=null && datiAnagraficiComponente.length>0){
                    codiceFiscale = datiAnagraficiComponente[1];
                  }
                }else{
                  codiceFiscale = SqlManager.getValueFromVectorParam(datiImpr, 1).getStringValue();
                }
              }

            }
            if (codiceFiscale == null || (codiceFiscale != null && "".equals(codiceFiscale))) {
              totalW_INVCOMDES_NOCF++;
            }
            if(comtipma == null || !(new Long(1)).equals(comtipma)){
              totalW_INVCOMDES_NOPEC++;
            }

            hMap.put("codicefiscale", codiceFiscale);
            hMapW_INVCOMDES.add(hMap);
          }
        }

        // Lettura degli allegati
        String richiestaFirma = ConfigManager.getValore("documentiDb.richiestaFirma");
        String formatoAllegati = ConfigManager.getValore(CostantiAppalti.FORMATO_ALLEGATI);
        String selectW_DOCDIG = "select digdesdoc, dignomdoc, digfirma from w_docdig where digent = ? and digkey1 = ? and digkey2 = ?";
        List<?> datiW_DOCDIG = sqlManager.getListVector(selectW_DOCDIG, new Object[] { "W_INVCOM", idprg, idcom.toString() });
        if (datiW_DOCDIG != null && datiW_DOCDIG.size() > 0) {
          totalW_DOCDIG = datiW_DOCDIG.size();
          String digfirma =null;
          for (int i = 0; i < datiW_DOCDIG.size(); i++) {
            HashMap<String, Object> hMap = new HashMap<String, Object>();
            String  desc = (String)SqlManager.getValueFromVectorParam(datiW_DOCDIG.get(i), 0).getValue();
            hMap.put("digdesdoc", SqlManager.getValueFromVectorParam(datiW_DOCDIG.get(i), 0).getValue());
            hMap.put("dignomdoc", SqlManager.getValueFromVectorParam(datiW_DOCDIG.get(i), 1).getValue());
            hMapW_DOCDIG.add(hMap);
            if(desc==null)
              TotalRecordsW_DOCDIG_NODESCRIZIONE++;
            if("1".equals(richiestaFirma)){
              digfirma = SqlManager.getValueFromVectorParam(datiW_DOCDIG.get(i), 2).stringValue();
              if("1".equals(digfirma))
                TotalRecordsW_DOCDIG_ATTESAFIRMA++;
            }
          }
          if(formatoAllegati!=null && !"".equals(formatoAllegati)){
            if(!pgManagerEst1.controlloAllegatiFormatoValido(datiW_DOCDIG,1,formatoAllegati))
              tuttiAllegatiFormatoValido=false;
          }
        }
      }

      result.put("iTotalRecordsW_INVCOM", totalW_INVCOM);
      result.put("iTotalDisplayRecordsW_INVCOM", totalW_INVCOM);
      result.put("iTotalRecordsW_INVCOMDES", totalW_INVCOMDES);
      result.put("iTotalRecordsW_INVCOMDES_NOCF", totalW_INVCOMDES_NOCF);
      result.put("iTotalRecordsW_INVCOMDES_NOPEC", totalW_INVCOMDES_NOPEC);
      result.put("iTotalDisplayRecordsW_INVCOMDES", totalW_INVCOMDES);
      result.put("iTotalRecordsW_DOCDIG", totalW_DOCDIG);
      result.put("iTotalDisplayRecordsW_DOCDIG", totalW_DOCDIG);
      result.put("iTotalRecordsW_DOCDIG_NODESCRIZIONE", TotalRecordsW_DOCDIG_NODESCRIZIONE);
      result.put("iTotalRecordsW_DOCDIG_ATTESAFIRMA", TotalRecordsW_DOCDIG_ATTESAFIRMA);
      result.put("dataW_INVCOM", hMapW_INVCOM);
      result.put("dataW_INVCOMDES", hMapW_INVCOMDES);
      result.put("dataW_DOCDIG", hMapW_DOCDIG);
      result.put("tuttiAllegatiFormatoValido", tuttiAllegatiFormatoValido);

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura della comunicazione", e);
    }

    out.println(result);
    out.flush();

    return null;

  }

  /**
   * Ricava la descrizione del tabellato TAB1
   *
   * @param tab1cod
   * @param tab1tip
   * @return
   * @throws Exception
   */
  private String getDescrizione1(String tab1cod, Long tab1tip) throws Exception {
    String descrizione = null;
    if (tab1tip != null) {
      descrizione = (String) sqlManager.getObject("select tab1desc from tab1 where tab1cod = ? and tab1tip = ?", new Object[] { tab1cod,
          tab1tip });
    }
    return descrizione;
  }

  /**
   * Ricava la descrizione del tabellato TAB2
   *
   * @param tab2cod
   * @param tab2tip
   * @return
   * @throws Exception
   */
  private String getDescrizione2(String tab2cod, String tab2tip) throws Exception {
    String descrizione = null;
    if (tab2tip != null) {
      descrizione = (String) sqlManager.getObject("select tab2d2 from tab2 where tab2cod = ? and tab2tip = ?", new Object[] { tab2cod,
          tab2tip });
    }
    return descrizione;
  }

}
