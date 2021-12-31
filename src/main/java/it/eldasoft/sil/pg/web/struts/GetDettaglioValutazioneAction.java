package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.profiles.FiltroLivelloUtente;
import it.eldasoft.utils.profiles.cache.DizionarioLivelli;
import it.eldasoft.utils.profiles.domain.Livello;
import net.sf.json.JSONObject;

public class GetDettaglioValutazioneAction extends Action {

  private SqlManager sqlManager;

  private GeneManager geneManager;

  private TabellatiManager tabellatiManager;

  private PgManagerEst1 pgManagerEst1;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

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

    //JSONArray jsonArray = new JSONArray();
    JSONObject result = new JSONObject();
    List<HashMap<String, Object>> hMap = new ArrayList<HashMap<String, Object>>();

    //l'oggetto hMap restituito dalla action conterrà i seguenti dati:
    // riptec
    // ripeco
    // tabA1049
    // tabA1z07
    // saltareControlloSogliaMinima
    // una lista contentente i seguenti dati
    //      norpar
    //      norpar1
    //      despar
    //      maxpun
    //      minpun
    //      puntegDpun
    //      puntegrip
    //      livpar
    //      maxpunCridef
    //      modpunti
    //      descModpunti
    //      coeffi
    //      punteg
    //      assegnazioneCoeffAbilitata
    //      idCridef
    //      necvan
    //      modmanu


    String ngara = request.getParameter("ngara");
    String codgar = request.getParameter("codiceGara");
    String ditta = request.getParameter("ditta");
    String tipoDettaglio = request.getParameter("tipoDettaglio");
    boolean saltareControlloSogliaMinima = false;

    try {
      Long riptec = null;
      Long ripeco = null;
      Long fasgar = null;
      String gartel = null;
      Long ripcritec = null;
      Long ripcrieco = null;

      Vector<?> datiGara1 = sqlManager.getVector("select riptec, ripeco, fasgar,gartel, ripcritec, ripcrieco from gare1 g1, gare g, torn where g1.ngara=g.ngara and g1.ngara=? and g.codgar1=codgar", new Object[]{ngara});
      if(datiGara1!=null && datiGara1.size()>0){
        riptec = SqlManager.getValueFromVectorParam(datiGara1, 0).longValue();
        ripeco = SqlManager.getValueFromVectorParam(datiGara1, 1).longValue();
        fasgar = SqlManager.getValueFromVectorParam(datiGara1, 2).longValue();
        gartel = SqlManager.getValueFromVectorParam(datiGara1, 3).stringValue();
        ripcritec = SqlManager.getValueFromVectorParam(datiGara1, 4).longValue();
        ripcrieco = SqlManager.getValueFromVectorParam(datiGara1, 5).longValue();
        //Nel caso di di gara a lotti offerta unica (BUSTALOTTI=2) si deve considerare la fase della gara, non quella del lotto
        Vector<?> datiGara = sqlManager.getVector("select bustalotti,fasgar from gare where codgar1=? and ngara=codgar1", new Object[]{codgar});
        if(datiGara!=null && datiGara.size()>0){
          Long bustalotti = SqlManager.getValueFromVectorParam(datiGara, 0).longValue();
          if(new Long(2).equals(bustalotti))
            fasgar = SqlManager.getValueFromVectorParam(datiGara, 1).longValue();
        }
        if((new Long(2).equals(riptec) && new Long(1).equals(ripcritec)) || (new Long(2).equals(ripeco) && new Long(1).equals(ripcrieco)))
          saltareControlloSogliaMinima= true;
      }

      //Caricamento del tabellato A1049
      String tabA1049 = (String)sqlManager.getObject("select tab1desc from tab1 where tab1cod=? and tab1tip=?", new Object[]{"A1049",new Long(1)});

      //Caricamento del tabellato A1z07
      String tabA1z07="";
      List<?> valoriTabellatoA1z07 = sqlManager.getListVector("select tab2d1, tab2d2 from tab2 where tab2cod=? ", new Object[]{"A1z07"});
      if(valoriTabellatoA1z07!=null && valoriTabellatoA1z07.size()>0){
        for(int i=0;i<valoriTabellatoA1z07.size();i++){
          tabA1z07 += SqlManager.getValueFromVectorParam(valoriTabellatoA1z07.get(i), 0).stringValue() + ":" + SqlManager.getValueFromVectorParam(valoriTabellatoA1z07.get(i), 1).stringValue() + ";";
        }
        tabA1z07 = tabA1z07.substring(0, tabA1z07.length()-1);
      }

      // 0 - Numero ordine criterio
      // 1 - Numero ordine sottocriterio
      // 2 - Descrizione
      // 3 - Punteggio massimo
      // 4 - Punteggio minimo
      // 5 - Punteggio assegnato alla ditta
      // 6 - Punteggio riparametrato
      // 7 - LIVPAR
      // 8 - NECVAN
      // 9 - SEZTEC
      String select = "select G.NORPAR, " //0
          + "G.NORPAR1, " //1
          + "G.DESPAR, " //2
          + "G.MAXPUN, " //3
          + "G.MINPUN, " //4
          + "D.PUNTEG, " //5
          + "D.PUNTEGRIP, " //6
          + "G.LIVPAR, " //7
          + "G.NECVAN, " //8
          + "G.SEZTEC " //9
          + "FROM GOEV G LEFT JOIN DPUN D ON G.NGARA=D.NGARA and G.NECVAN=D.NECVAN and D.DITTAO=? "
          + " WHERE G.NGARA =? AND G.TIPPAR=? "
          + " order by G.NORPAR  asc, G.NECVAN1  asc,G.NORPAR1  asc,G.NECVAN  asc";

      boolean assegnazioneCoeffAbilitata= true;
      boolean assegnazioneCoeffAbilitataCommissione= true;

      //Controllo dei diritti dell'utente sulla gara
      Long autori=null;
      ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      if (DizionarioLivelli.getInstance().isFiltroLivelloPresente("V_GARE_TORN")) {
        // in caso affermativo, si costruisce il filtro sulla base dell'utente
        FiltroLivelloUtente filtroUtente = profiloUtente.getFiltroLivelloUtente();
        // si indica inoltre l'entità per cui eseguire il filtro
        Livello livello = DizionarioLivelli.getInstance().get("V_GARE_TORN");
        filtroUtente.setLivello(livello, "V_GARE_TORN");
        // si genera la stringa da aggiungere alla clausola where opportunamente
        // valorizzata con il valore del filtro da applicare
        if (filtroUtente.getCondizione() != null) {
         autori = (Long)this.sqlManager.getObject("select autori from g_permessi where codgar = ? and g_permessi.syscon = ?", new Object[]{codgar, new Long(profiloUtente.getId())});
        }
      }
      String controllo="TEC";
      if("2".equals(tipoDettaglio))
        controllo="ECO";
      boolean esistonoDitteConPunteggio = this.pgManagerEst1.esistonoDittePunteggioValorizzato(ngara, controllo);
      //Condizioni per cui si blocca la possibilità di modificare il coefficente
      // - Abilitazione da profilo della funzione
      // - Gara aggiudicata (fasgar >=7)
      // - Esistenza di ditte in gara con il punteggio tecnico totale valorizzato
      // - Diritti di modifica dell'utente
      if (!geneManager.getProfili().checkProtec(
          (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS", "ALT.GARE.GOEV-dettaglioValutazioneTecEco-lista.AssegnaCoefficente") || (fasgar != null && fasgar.longValue()>=7)
          || esistonoDitteConPunteggio || (autori!=null && autori.longValue()==2))
        assegnazioneCoeffAbilitata= false;
      else if("1".equals(gartel)){
        //Si deve controllare che la fase della gara sia la stessa di quella del dettaglio apero
        if(("1".equals(tipoDettaglio) && !(new Long(5)).equals(fasgar)) || ("2".equals(tipoDettaglio) && !(new Long(6)).equals(fasgar)))
          assegnazioneCoeffAbilitata= false;
      }

      boolean attivaCommissione = geneManager.getProfili().checkProtec(
          (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS", "ALT.GARE.AttivaCommissione");

      if (!geneManager.getProfili().checkProtec((String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS", "ALT.GARE.GOEV-dettaglioValutazioneTecEco-lista.AssegnaCoefficenteCommissione")
          || (fasgar != null && fasgar.longValue()>=7)
          || esistonoDitteConPunteggio || (autori!=null && autori.longValue()==2))
        assegnazioneCoeffAbilitataCommissione= false;
      else if("1".equals(gartel)){
        //Si deve controllare che la fase della gara sia la stessa di quella del dettaglio apero
        if(("1".equals(tipoDettaglio) && !(new Long(5)).equals(fasgar)) || ("2".equals(tipoDettaglio) && !(new Long(6)).equals(fasgar)))
          assegnazioneCoeffAbilitataCommissione= false;
      }

      String commissarioAttivo = null;
      if(attivaCommissione){
        List<?> commissarioAssAccount = sqlManager.getListVector("select tecni.codtec from tecni, usrsys where tecni.cftec = usrsys.syscf and usrsys.syscon = ?", new Object[] { new Long(profiloUtente.getId()) });
        if (commissarioAssAccount != null && commissarioAssAccount.size() == 1) {
          commissarioAttivo = SqlManager.getValueFromVectorParam(commissarioAssAccount.get(0), 0).getStringValue();
        }
      }

      List<?> datiCriteri = sqlManager.getListVector(select, new Object[] { ditta,ngara, new Long(tipoDettaglio) });
      if (datiCriteri != null && datiCriteri.size() > 0) {
        String despar=null;
        Long livpar=null;
        Long necvan = null;
        Long modpunti = null;
        Double coeffi= null;
        Double punteg = null;
        Double maxpun = null;
        Long id = null;
        String descModpunti = null;
        Long modmanu=null;
        Long formato = null;
        Long formula = null;
        String descFormula = null;
        String valstg = null;
        Date valdat = null;
        Double valnum = null;
        Long numdeci = null;
        Long idcrival = null;
        Long seztec = null;
        String seztecstring = null;
        for (int i = 0; i < datiCriteri.size(); i++) {
          HashMap<String, Object> hMapCriterio = new HashMap<String, Object>();
          hMapCriterio.put("norpar", SqlManager.getValueFromVectorParam(datiCriteri.get(i), 0).getValue());
          hMapCriterio.put("norpar1", SqlManager.getValueFromVectorParam(datiCriteri.get(i), 1).getValue());
          despar= SqlManager.getValueFromVectorParam(datiCriteri.get(i), 2).getStringValue();
          hMapCriterio.put("despar", despar);
          hMapCriterio.put("maxpun", SqlManager.getValueFromVectorParam(datiCriteri.get(i), 3).getValue());
          hMapCriterio.put("minpun", SqlManager.getValueFromVectorParam(datiCriteri.get(i), 4).getValue());
          hMapCriterio.put("puntegDpun", SqlManager.getValueFromVectorParam(datiCriteri.get(i), 5).getValue());
          hMapCriterio.put("puntegrip", SqlManager.getValueFromVectorParam(datiCriteri.get(i), 6).getValue());
          livpar=(Long)SqlManager.getValueFromVectorParam(datiCriteri.get(i), 7).getValue();
          hMapCriterio.put("livpar", livpar);
          maxpun = null;
          modpunti = null;
          coeffi= null;
          punteg = null;
          descModpunti = null;
          necvan = null;
          modmanu=null;
          formato=null;
          formula=null;
          descFormula = null;
          valstg = null;
          valdat = null;
          valnum = null;
          idcrival = null;
          seztecstring = "";
          if ((new Long(1)).equals(livpar) || (new Long(3)).equals(livpar)) {
            seztec=(Long)SqlManager.getValueFromVectorParam(datiCriteri.get(i), 9).getValue();
            if (seztec != null) {
              seztecstring = tabellatiManager.getDescrTabellato("A1168", seztec.toString());
            } else {
              seztecstring = "";
            }
          }
          hMapCriterio.put("seztec", seztecstring);
          if((new Long(1)).equals(livpar) || (new Long(2)).equals(livpar)){
            necvan=(Long)SqlManager.getValueFromVectorParam(datiCriteri.get(i), 8).getValue();


            Vector<?> sottoDati = sqlManager.getVector("select d.maxpun, d.modpunti, v.coeffi, v.punteg, d.id, d.modmanu, d.formato, d.formula, v.valstg, v.valdat, v.valnum, v.id from g1cridef d LEFT JOIN G1CRIVAL v on d.id=v.idcridef and v.dittao=? "
                + "where d.ngara=?  and d.necvan=? ", new Object[]{ditta, ngara, necvan});
            if(sottoDati!=null && sottoDati.size()>0){
              maxpun= (Double)SqlManager.getValueFromVectorParam(sottoDati, 0).getValue();
              modpunti= (Long)SqlManager.getValueFromVectorParam(sottoDati, 1).getValue();
              descModpunti = (String)sqlManager.getObject("select tab1desc from tab1 where tab1cod=? and tab1tip=?", new Object[]{"A1141",modpunti});
              coeffi= (Double)SqlManager.getValueFromVectorParam(sottoDati, 2).getValue();
              punteg= (Double)SqlManager.getValueFromVectorParam(sottoDati, 3).getValue();
              id = (Long)SqlManager.getValueFromVectorParam(sottoDati, 4).getValue();
              modmanu= (Long)SqlManager.getValueFromVectorParam(sottoDati, 5).getValue();
              formato= (Long)SqlManager.getValueFromVectorParam(sottoDati, 6).getValue();
              formula= (Long)SqlManager.getValueFromVectorParam(sottoDati, 7).getValue();
              descFormula = (String)sqlManager.getObject("select tab1desc from tab1 where tab1cod=? and tab1tip=?", new Object[]{"A1147",formula});
              valstg = (String)SqlManager.getValueFromVectorParam(sottoDati, 8).getValue();
              valdat = (Date)SqlManager.getValueFromVectorParam(sottoDati, 9).getValue();
              valnum = (Double)SqlManager.getValueFromVectorParam(sottoDati, 10).getValue();
              idcrival = (Long)SqlManager.getValueFromVectorParam(sottoDati, 11).getValue();
            }

          }


          if(modpunti != null && maxpun !=null && modpunti.intValue() == 1 && maxpun.doubleValue()>0){

            String codice = ngara;
            Long genere = (Long)sqlManager.getObject("select genere from v_gare_genere where codgar=?", new Object[]{codgar});
            if(genere != null && genere.intValue()==3){
              codice = codgar;
            }

            String selectComm;
            List<?> datiVal;
            if(idcrival == null || idcrival.equals("null")){
              selectComm = "select G.ID, " //0
                + "G.CODFOF, " //1
                + "G.NOMFOF " //2
                + "FROM GFOF G "
                + "WHERE G.NGARA2 = ? AND G.ESPGIU = '1' order by id asc";
              datiVal = sqlManager.getListVector(selectComm, new Object[] { codice });
            }else{
              selectComm = "select G.ID, " //0
              + "G.CODFOF, " //1
              + "G.NOMFOF, " //2
              + "V.ID AS IDCRIVAL, " //3
              + "V.COEFFI " //4
              + "FROM GFOF G LEFT JOIN G1CRIVALCOM V ON G.ID=V.IDGFOF AND V.IDCRIVAL = ? "
              + "WHERE G.NGARA2 = ? AND G.ESPGIU = '1' order by id asc";
              datiVal = sqlManager.getListVector(selectComm, new Object[] { idcrival,codice });
            }


            if (datiVal != null && datiVal.size() > 0) {
              List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
              for (int j = 0; j < datiVal.size(); j++) {
                HashMap<String, Object> hMapCommissione = new HashMap<String, Object>();
                hMapCommissione.put("id", SqlManager.getValueFromVectorParam(datiVal.get(j), 0).getValue());
                hMapCommissione.put("codfof", SqlManager.getValueFromVectorParam(datiVal.get(j), 1).getValue());
                hMapCommissione.put("nomfof", SqlManager.getValueFromVectorParam(datiVal.get(j), 2).getValue());
                if(idcrival == null || idcrival.equals("null")){
                  hMapCommissione.put("idcrivalcom", null);
                  hMapCommissione.put("coeffi", null);
                }else{
                  hMapCommissione.put("idcrivalcom", SqlManager.getValueFromVectorParam(datiVal.get(j), 3).getValue());
                  hMapCommissione.put("coeffi", SqlManager.getValueFromVectorParam(datiVal.get(j), 4).getValue());
                }
                list.add(hMapCommissione);
              }
              hMapCriterio.put("dataCommissione", list);
            }
          }

          hMapCriterio.put("maxpunCridef",maxpun );
          hMapCriterio.put("modpunti",modpunti );
          hMapCriterio.put("descModpunti",descModpunti );
          hMapCriterio.put("coeffi",coeffi );
          hMapCriterio.put("punteg",punteg );
          hMapCriterio.put("assegnazioneCoeffAbilitata",new Boolean(assegnazioneCoeffAbilitata) );
          hMapCriterio.put("assegnazioneCoeffAbilitataCommissione",new Boolean(assegnazioneCoeffAbilitataCommissione) );
          hMapCriterio.put("profiloCommissione",new Boolean(attivaCommissione) );
          hMapCriterio.put("commissarioAttivo",commissarioAttivo );
          hMapCriterio.put("idCridef",id );
          hMapCriterio.put("necvan",necvan );
          hMapCriterio.put("modmanu",modmanu );
          hMapCriterio.put("formato",formato );
          hMapCriterio.put("formula",formula );
          hMapCriterio.put("descFormula",descFormula );
          hMapCriterio.put("idCrival",idcrival );
          if(valstg != null){
            hMapCriterio.put("valstg",valstg );
          }
          if(valdat != null){
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String dateStg = sdf.format(valdat);
            hMapCriterio.put("valdat",dateStg );
          }
          if(valnum != null){
            hMapCriterio.put("valnum",valnum );
          }
          hMap.add(hMapCriterio);
        }
        result.put("riptec", riptec);
        result.put("ripeco", ripeco);
        result.put("ripcritec", ripcritec);
        result.put("ripcrieco", ripcrieco);
        result.put("tabA1049", tabA1049);
        result.put("tabA1z07", tabA1z07);
        result.put("data", hMap);
        result.put("saltareControlloSogliaMinima", new Boolean(saltareControlloSogliaMinima));
      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura delle informazioni del dettaglio di valutazione della ditta " + ditta, e);
    }

    out.println(result);
    out.flush();

    return null;

  }

}
