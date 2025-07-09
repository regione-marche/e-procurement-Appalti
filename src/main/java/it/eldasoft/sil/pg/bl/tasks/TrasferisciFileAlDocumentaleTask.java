/*
 * Created on 09/08/2024
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.tasks;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

public class TrasferisciFileAlDocumentaleTask {

  static Logger           logger = Logger.getLogger(TrasferisciFileAlDocumentaleTask.class);

  private SqlManager      sqlManager;

  private ArchiviazioneDocumentiManager archiviazioneDocumentiManager;

  public void setArchiviazioneDocumentiManager(ArchiviazioneDocumentiManager archiviazioneDocumentiManager) {
    this.archiviazioneDocumentiManager = archiviazioneDocumentiManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void trasferisciFileAlDocumentale() throws GestoreException {
    if (WebUtilities.isAppNotReady()) return;

    if (logger.isDebugEnabled())
      logger.debug("trasferisciFileAlDocumentale: inizio metodo");

    String esito = "";
    try {
      esito += processGare();
      //esito += processG1Stipula();
    } catch (Exception e) {
      throw new GestoreException("Errore nel trasferimento dei file", null, e);
    } finally {
      logEvent(esito);
    }

    if (logger.isDebugEnabled())
      logger.debug("trasferisciFileAlDocumentale: fine metodo");
  }

  //genere < 10 per evitare di prendere anche gli elenchi e gli avvisi (rivisitato con Sara)
  private String processGare() throws SQLException {
    String esito = "";
    String listaGareQuery = "select t.codgar, g.ngara, v.genere, u.codein "
        + "from torn t inner join gare g on g.codgar1 = t.codgar "
        + "inner join v_gare_genere v on g.codgar1 = v.codgar and g.ngara = v.codice "
        + "inner join uffint u on t.cenint = u.codein "
        + "inner join wsfascicolo w on w.key1 = REPLACE(t.codgar, '$', '') "
        + "where t.doc_da_arch = '1' "
        + "and v.genere < 10 ";

    List<?> listaDatiGare = this.sqlManager.getListVector(listaGareQuery, new Object[]{});
    if (listaDatiGare != null && !listaDatiGare.isEmpty()) {
      for (int x = 0; x < listaDatiGare.size(); x++) {
        esito += processSingleGara((Vector)listaDatiGare.get(x));
      }
    }
    return esito;
  }

  private String processG1Stipula() throws SQLException {
    String esito = "";
    //String listaG1stipulaQuery = "select id, codStipula from g1stipula where doc_da_arch = '1'";
    String listaG1stipulaQuery = "select s.id, s.codStipula, u.codein "
        + "from g1stipula s "
        + "inner join gare g on s.ngara = g.ngara "
        + "inner join torn t on g.codgar1 = t.codgar "
        + "inner join uffint u on t.cenint = u.codein "
        + "where s.doc_da_arch = '1' ";

    List<?> listaDatiG1stipula = this.sqlManager.getListVector(listaG1stipulaQuery, new Object[]{});
    if (listaDatiG1stipula != null && !listaDatiG1stipula.isEmpty()) {
      for (int x = 0; x < listaDatiG1stipula.size(); x++) {
        esito += processSingleStipula((Vector)listaDatiG1stipula.get(x));
      }
    }
    return esito;
  }

  private String processSingleGara(Vector garaData) {
    String esito = "";
    try {
      String codgar = (String) SqlManager.getValueFromVectorParam(garaData, 0).getValue();
      String ngara = (String) SqlManager.getValueFromVectorParam(garaData, 1).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(garaData, 2).getValue();
      String uffint = (String) SqlManager.getValueFromVectorParam(garaData, 3).getValue();

      String genereStr = genere != null ? String.valueOf(genere) : null;
      String entita = "1".equals(genereStr) ? "TORN" : "GARE";
      Long idRichiesta = createArchiviazioneJob(codgar, entita, uffint);

      int recordDaArchiviareDocumentale = processDocuments(codgar, genereStr, ngara, "false", idRichiesta);
      esito += String.format("(%s) Archiviati %d Documenti%n", codgar, recordDaArchiviareDocumentale);
    } catch (Exception e) {
      logger.error("Errore archiviazione doc per la gara " + garaData, e);
      esito += "Errore archiviazione doc per la gara " + garaData + "\n";
    }
    return esito;
  }

  private String processSingleStipula(Vector stipulaData) {
    String esito = "";
    try {
      Long idStipula = (Long) SqlManager.getValueFromVectorParam(stipulaData, 0).getValue();
      String codStipula = (String) SqlManager.getValueFromVectorParam(stipulaData, 1).getValue();
      String uffint = (String) SqlManager.getValueFromVectorParam(stipulaData, 2).getValue();

      Long idRichiesta = createArchiviazioneJob(String.valueOf(idStipula), "G1STIPULA", uffint);

      int recordDaArchiviareDocumentale = processDocuments("", "", String.valueOf(idStipula), "true", idRichiesta);
      esito += String.format("(%s) Archiviati %d Documenti%n", codStipula, recordDaArchiviareDocumentale);
    } catch (Exception e) {
      logger.error("Errore archiviazione doc per il codice stipula " + stipulaData, e);
      esito += "Errore archiviazione doc per il codice stipula " + stipulaData + "\n";
    }
    return esito;
  }

  private Long createArchiviazioneJob(String codice, String entita, String mittenteInterno) throws Exception {
    String tipodocumento = (String) this.sqlManager.getObject("select valore from WSDMTAB where codice='tipodocumentomassivo' and sistema='ARCHIFLOW' order by id", new Object[]{});
    String mezzo = (String) this.sqlManager.getObject("select valore from WSDMTAB where codice='mezzomassivo' and sistema='ARCHIFLOW' order by id", new Object[]{}); 
    Long syscon = Long.valueOf(-1);
    Long tipo_archiviazione = Long.valueOf(2);
    return archiviazioneDocumentiManager.insertJobArchiviazioneDocumenti(
        syscon, codice, entita, null, null, null, tipodocumento, mittenteInterno, null, null, null, mezzo, null, null, tipo_archiviazione, null, null);
  }

  private int processDocuments(String codgar, String genereStr, String key1, String isStipula, Long idRichiesta) throws Exception {
    JSONObject data = archiviazioneDocumentiManager.getListaDocumenti(codgar, genereStr, key1, isStipula, null, false,false);
    if (data == null || data.isEmpty()) return 0;

    int recordDaArchiviareDocumentale = (int) data.get("documentiDaArchiviareDocumentale");
    if (recordDaArchiviareDocumentale <= 0) return 0;

    JSONArray hmDocGara = (JSONArray) data.get("data");
    if (hmDocGara != null && !hmDocGara.isEmpty()) {
      for (int i = 0; i < hmDocGara.size(); i++) {
        processSingleDocument(hmDocGara.getJSONObject(i), idRichiesta);
      }
    }
    return recordDaArchiviareDocumentale;
  }

  private void processSingleDocument(JSONObject hm, Long idRichiesta) throws Exception {
    String idwsdoc = hm.getJSONObject("IDWSDOC").getString("stringValue");
    String stato = hm.getJSONObject("STATO").getString("stringValue");

    if ("".equals(idwsdoc) && ("".equals(stato) || "2".equals(stato) || "23".equals(stato))) {
      String provenienza = hm.getJSONObject("PROVENIENZA").getString("stringValue");
      String key1 = hm.getJSONObject("IDPRG").getString("stringValue");
      String key2 = hm.getJSONObject("IDDOCDIG").getString("stringValue");

      String documentiAssociatiDB = ConfigManager.getValore("it.eldasoft.documentiAssociatiDB");
      String selectWSALLEGATI = "select count(*) from wsallegati where entita = ? and key1 = ? and key2 = ?";
      String entita = "4".equals(provenienza) && !"1".equals(documentiAssociatiDB) ? "C0OGGASS" : "W_DOCDIG";

      String idRichiestaStr = UtilityStringhe.convertiNullInStringaVuota(String.valueOf(idRichiesta));
      stato = UtilityStringhe.convertiNullInStringaVuota(stato);

      if (!"".equals(idRichiestaStr) && !"21".equals(stato) && !"23".equals(stato)) {
        Long cnt = (Long) this.sqlManager.getObject(selectWSALLEGATI, new Object[]{entita, key1, key2});
        if (cnt == null || cnt.longValue() == 0) {
          archiviazioneDocumentiManager.insertDettJobArchiviazioneDocumenti(-1L, idRichiestaStr, key1, key2, provenienza, documentiAssociatiDB);
        }
      }
    }
  }

  private void logEvent(String esito) {
    try {
      LogEvento logEvento = new LogEvento();
      logEvento.setCodApplicazione("PG");
      logEvento.setOggEvento("Trasferimento al documentale");
      logEvento.setLivEvento(1);
      logEvento.setCodEvento("GA_ARCHIVIA_MASSIVO_ARCHIFLOW");
      logEvento.setDescr("Trasferimento file delle gara al documentale ");
      logEvento.setErrmsg(esito);
      LogEventiUtils.insertLogEventi(logEvento);
    } catch (Exception le) {
      logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
    }
  }






}


