package it.eldasoft.sil.pg.bl.tasks;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import net.sf.json.JSONObject;

/**
 * Task per la marcatura delle gare aggiudicate con documenti non archiviati
 */
public class AnalizzaArchiviazioneDocumentiManager {

  private static final String PROP_WSDMCONFIGURAZIONE_DOCUMENTALE_URL         = "wsdmconfigurazione.documentale.url.";

  static Logger                         logger = Logger.getLogger(AnalizzaArchiviazioneDocumentiManager.class);

  private SqlManager                    sqlManager;

  private TabellatiManager              tabellatiManager;

  private ArchiviazioneDocumentiManager archiviazioneDocumentiManager;

  private GestioneWSDMManager             gestioneWSDMManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  public void setArchiviazioneDocumentiManager(ArchiviazioneDocumentiManager archiviazioneDocumentiManager) {
    this.archiviazioneDocumentiManager = archiviazioneDocumentiManager;
  }

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  public String getQueryDocGara(String beforeDate, String afterDate, String filtroCodgar) {
    return "select g.codgar1,vgg.genere,vgg.codice from  gare g join V_GARE_GENERE vgg on g.codgar1=vgg.codgar and vgg.genere <100 left join V_GARE_STATOESITO v on (v.codgar = g.codgar1 and codiga is null) "
            + " left join torn t on t.codgar = v.codgar and vgg.genere in (1,2,3) left join pubbli p on p.codgar9=g.codgar1  and p.tippub=11 and (vgg.genere=11 or vgg.genere=10 or vgg.genere=20) left join garealbo ga on ga.codgar=g.codgar1 and vgg.genere=10 "
            //queste join sotto per recuperare le occorrenze padri dei rilanci
            + " left join gare gara_preced on (gara_preced.ngara=g.preced or gara_preced.ngara=g.ngarapa) left join V_GARE_STATOESITO v_preced on (v_preced.codgar = gara_preced.codgar1) left join torn torn_preced on torn_preced.codgar = v_preced.codgar"
            + " where ("//condizioni per gare
            + "(vgg.genere in (1,2,3) and v.codstato = 3 and ("
            + "   (g.dattoa > "+sqlManager.getDBFunction("stringtodate", new String[] { afterDate })+ " and g.dattoa < "+sqlManager.getDBFunction("stringtodate", new String[] { beforeDate })+ ") "
            +" or (g.datneg > "+sqlManager.getDBFunction("stringtodate", new String[] { afterDate })+ " and g.datneg < "+sqlManager.getDBFunction("stringtodate", new String[] { beforeDate })+ ") "
            +" or (t.datneg > "+sqlManager.getDBFunction("stringtodate", new String[] { afterDate })+ " and t.datneg < "+sqlManager.getDBFunction("stringtodate", new String[] { beforeDate })+ ") "
            +" or ((t.iterga <> 10 or t.iterga is null) and g.datconclusa > "+sqlManager.getDBFunction("stringtodate", new String[] { afterDate })+ " and g.datconclusa < "+sqlManager.getDBFunction("stringtodate", new String[] { beforeDate })+ ")"
            + ")) or (" //condizioni per rilanci
            + "vgg.genere = 2 and v_preced.codstato = 3 and ("
            + "   (gara_preced.dattoa > "+sqlManager.getDBFunction("stringtodate", new String[] { afterDate })+ " and gara_preced.dattoa < "+sqlManager.getDBFunction("stringtodate", new String[] { beforeDate })+ ") "
            +" or (gara_preced.datneg > "+sqlManager.getDBFunction("stringtodate", new String[] { afterDate })+ " and gara_preced.datneg < "+sqlManager.getDBFunction("stringtodate", new String[] { beforeDate })+ ") "
            +" or (torn_preced.datneg > "+sqlManager.getDBFunction("stringtodate", new String[] { afterDate })+ " and torn_preced.datneg < "+sqlManager.getDBFunction("stringtodate", new String[] { beforeDate })+ ") "
            +" or (gara_preced.datconclusa > "+sqlManager.getDBFunction("stringtodate", new String[] { afterDate })+ " and gara_preced.datconclusa < "+sqlManager.getDBFunction("stringtodate", new String[] { beforeDate })+ ")"
            + ")) or(" //condizione per gli avvisi
            + "vgg.genere=11 and p.tippub=11 and (p.datpub > "+sqlManager.getDBFunction("stringtodate", new String[] { afterDate })+ " and p.datpub < "+sqlManager.getDBFunction("stringtodate", new String[] { beforeDate })+ ") "
            + ") or (" //condizione per gli elenchi
            +" (vgg.genere=10 or vgg.genere=20) and p.tippub=11 and (ga.dtermval is NULL or (ga.dtermval > "+sqlManager.getDBFunction("stringtodate", new String[] { afterDate })+ " and ga.dtermval < "+sqlManager.getDBFunction("stringtodate", new String[] { beforeDate })+ ")"
            + "))) and (t.iterga not in (9,10) or t.iterga is null or t.isfineconc = '1') "+filtroCodgar+" order by vgg.genere, g.codgar1";
  }

  public String getQueryDocStipula(String beforeDate, String afterDate, String filtroStipula) {
    return "select id, codstipula from v_gare_stipula where stato in (5,6)  and "
            + "(daatto > "+sqlManager.getDBFunction("stringtodate", new String[] { afterDate })+ " and daatto < "+sqlManager.getDBFunction("stringtodate", new String[] { beforeDate })+ ") "
            + filtroStipula+" order by id";
  }

  /**
   * archiviazione ed esportazione dei documenti di gara/elenco/catalogo
   *
   * @throws GestoreException
   */
  public void analizzaEntita() throws GestoreException {
    if (!WebUtilities.isAppNotReady()) {
      this.analizzaEntita(null,null);
    }
  }

  public void analizzaEntita(String codicegara, Long idstipula) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("Inizio analisi e marcatura gare aggiudicate");

    String valoreTabellato = this.tabellatiManager.getDescrSupplementare("A1z17", "1");
    long profondita = 0L;
    try {
      profondita = Long.parseLong(valoreTabellato);
    }catch(Exception e){
      logger.error("Il valore impostato per la profondità dell'analisi dello stato di archiviazione dei documenti non è valido");
    }

    String esito = "";
    String filtroCodgar = "";
    List parametriGara = new ArrayList<Object>();
    if(codicegara!=null) {
      filtroCodgar = " and g.codgar1=? ";
      parametriGara.add(codicegara);
    }
    String filtroStipula = "";
    List parametriStipula = new ArrayList<Object>();
    if(idstipula!=null) {
      filtroStipula = " and id=? ";
      parametriStipula.add(idstipula);
    }

    String codgarAnalisiPuntuale = "";
    String codstipulaAnalisiPuntuale = "";
    try {
      String beforeDate = UtilityDate.convertiData(new Date(System.currentTimeMillis()+24*60*60*1000), UtilityDate.FORMATO_GG_MM_AAAA);
      String afterDate = UtilityDate.convertiData(new Date(System.currentTimeMillis()-24*60*60*1000*profondita*30), UtilityDate.FORMATO_GG_MM_AAAA);

      if(idstipula==null) {//se non stiamo analizzando una stipula puntualmente
        String resetUpdate = "update torn set doc_da_arch=null,cos_da_arch=null";
        if(codicegara!=null)//se stiamo analizzando puntualmente una gara resettiamo solo quella gara
          resetUpdate+= " where codgar=?";
        sqlManager.update(resetUpdate, parametriGara.toArray());
        
        //GARE
        List<?> datiGara = sqlManager.getListVector(getQueryDocGara(beforeDate,afterDate,filtroCodgar), parametriGara.toArray() );
        if(datiGara!=null) {
          for (int i=0; i< datiGara.size(); i++){
            String codgar = SqlManager.getValueFromVectorParam(datiGara.get(i), 0).getStringValue();
            String genere = SqlManager.getValueFromVectorParam(datiGara.get(i), 1).getStringValue();
            String codice = SqlManager.getValueFromVectorParam(datiGara.get(i), 2).getStringValue();
            esito +=analizzaEMarcaEntitaSingola(codgar, genere, codice, false);
            if(codicegara!=null)
              codgarAnalisiPuntuale = codice;
          }
        }
      }
      if(codicegara==null) {//se non stiamo analizzando una gara puntualmente
        String resetUpdate = "update g1stipula set doc_da_arch=null,cos_da_arch=null";
        if(idstipula!=null)//se stiamo analizzando puntualmente una stipula resettiamo solo quella stipula
          resetUpdate+= " where id=?";
        sqlManager.update(resetUpdate, parametriStipula.toArray());
        //STIPULE
        List<?> datiStipula = sqlManager.getListVector(getQueryDocStipula(beforeDate,afterDate,filtroStipula), parametriStipula.toArray());

        if(datiStipula!=null) {
          for (int i=0; i< datiStipula.size(); i++){
            String codice = SqlManager.getValueFromVectorParam(datiStipula.get(i), 0).getStringValue();
            String codstipula = SqlManager.getValueFromVectorParam(datiStipula.get(i), 1).getStringValue();
            esito +=analizzaEMarcaEntitaSingola(codstipula, null, codice, true);
            if(idstipula!=null)
              codstipulaAnalisiPuntuale = codstipula;
          }
        }
      }


    }catch(SQLException e) {
      esito+="Errore nella lettura dei dati delle gare durante l'analisi dei documenti da archiviare";
      logger.debug("Errore nella lettura dei dati delle gare durante l'analisi dei documenti da archiviare");
    }

    if((codicegara==null && idstipula==null) || !"".equals(codgarAnalisiPuntuale) || !"".equals(codstipulaAnalisiPuntuale)) {//vogliamo produrre log solo se il task è in modalità globale oppure se l'analisi puntuale è eseguita su una gara idonea
      //INSERIMENTO OCCORRENZA UNICA IN LOGEVENTI
      String tipoEvento="(massivo)";
      if((codgarAnalisiPuntuale+codstipulaAnalisiPuntuale).length()>0)
        tipoEvento="(puntuale)";
      try {
        LogEvento logEvento = new LogEvento();
        logEvento.setCodApplicazione("PG");
        logEvento.setOggEvento(codgarAnalisiPuntuale+codstipulaAnalisiPuntuale);
        logEvento.setLivEvento(1);
        logEvento.setCodEvento("GA_ANALISI_ARCHIVIAZIONE");
        logEvento.setDescr("Analisi delle procedure concluse con documenti da archiviare "+tipoEvento);
        logEvento.setErrmsg(esito);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
      }
    }

    if (logger.isDebugEnabled())
      logger.debug("Fine analisi e marcatura gare aggiudicate");
  }

  private String analizzaEMarcaEntitaSingola(String identificativo, String genere, String codice, boolean isStipula){

    if (logger.isDebugEnabled()) logger.debug("Inizio analisi e marcatura gara "+identificativo);

    String identificativoNoDollaro = identificativo.replace("$", "");
    String documentiDaArchiviare="";
    String tabella="TORN";
    String chiave="CODGAR";
    String integrazioneConservazione = "0";
    try {
      Long idconfi = null;
      JSONObject data = null;
      if(isStipula) {
        idconfi = this.gestioneWSDMManager.getWsdmConfigurazioneFromIdStipula(new Long(codice), "PG");
        tabella = "G1STIPULA";
        chiave = "CODSTIPULA";
      } else {
        idconfi = this.gestioneWSDMManager.getWsdmConfigurazioneFromCodgar(identificativo, "PG");
        integrazioneConservazione = archiviazioneDocumentiManager.getSistemaConservazione(identificativo);
      }
      String urlDocumentale = ConfigManager.getValore(PROP_WSDMCONFIGURAZIONE_DOCUMENTALE_URL + idconfi);
      if (!"".equals(StringUtils.stripToEmpty(urlDocumentale)) || !"0".equals(integrazioneConservazione)){
        if(isStipula)
          data = archiviazioneDocumentiManager.getListaDocumenti("", "", codice, "true", null, false,false);
        else
          data = archiviazioneDocumentiManager.getListaDocumenti(identificativo, genere, codice, "false", null, false,false);
      }
      if(data!=null) {
        if(!"".equals(StringUtils.stripToEmpty(urlDocumentale))){//analisi documentale
          int recordDaArchiviareDocumentale = (int) data.get("documentiDaArchiviareDocumentale");
          if(recordDaArchiviareDocumentale>0){
            if (logger.isDebugEnabled()) logger.debug("Marcatura della gara "+identificativoNoDollaro+": "+recordDaArchiviareDocumentale+" documenti da archiviare (documentale)");
            documentiDaArchiviare = "\r\n("+identificativoNoDollaro+") Documenti da trasferire al documentale: "+recordDaArchiviareDocumentale;
            sqlManager.update("update "+tabella+" set doc_da_arch='1' where "+chiave+"=?", new Object[] {identificativo} );
          }else {
            sqlManager.update("update "+tabella+" set doc_da_arch='2' where "+chiave+"=?", new Object[] {identificativo} );
          }
        }else {
          if(!"0".equals(integrazioneConservazione)) {//analisi conservazione
            String sistema = "2".equals(integrazioneConservazione)? "DigiP":"COS";
            int documentiDaArchiviareCOS = (int) data.get("documentiDaArchiviareCOS");
            if(documentiDaArchiviareCOS>0){
              if (logger.isDebugEnabled()) logger.debug("Marcatura della gara "+identificativoNoDollaro+": "+documentiDaArchiviareCOS+" documenti da archiviare ("+sistema+")");
              documentiDaArchiviare = "\r\n("+identificativoNoDollaro+") Documenti da trasferire a "+sistema+": "+ documentiDaArchiviareCOS;
              sqlManager.update("update "+tabella+" set cos_da_arch='1' where "+chiave+"=?", new Object[] {identificativo} );
            }else {
              sqlManager.update("update "+tabella+" set cos_da_arch='2' where "+chiave+"=?", new Object[] {identificativo} );
            }
            int documentiNonArchiviabiliCOS = (int) data.get("documentiNonArchiviabiliCOS");
            if(documentiNonArchiviabiliCOS>0){
              if("".equals(documentiDaArchiviare))
                documentiDaArchiviare+="\r\n("+identificativoNoDollaro+") ";
              else
                documentiDaArchiviare+="; ";
              documentiDaArchiviare += "Documenti non trasferibili a "+sistema+": "+documentiNonArchiviabiliCOS;
            }
          }else {//non si dovrebbe più entrare qui dentro, in quanto si controlla che esista integrazione documentale o cos prima
            if (logger.isDebugEnabled()) logger.debug("Marcatura della gara "+identificativoNoDollaro+": non risulta configurata l'archiviazione verso WSDM o COS");
            documentiDaArchiviare = "\r\n("+identificativoNoDollaro+") Non risulta configurata l'archiviazione verso WSDM o COS";
          }
        }
      }

    }catch(SQLException | GestoreException e) {
      String msg="Errore nella lettura dei dati durante l'analisi dei documenti da archiviare ("+identificativoNoDollaro+")";
      documentiDaArchiviare += "\r\n"+msg;
      logger.error(msg);
    }
    if (logger.isDebugEnabled())
      logger.debug("Fine analisi e marcatura gara/stipula "+identificativo);
    return documentiDaArchiviare;

  }
}
