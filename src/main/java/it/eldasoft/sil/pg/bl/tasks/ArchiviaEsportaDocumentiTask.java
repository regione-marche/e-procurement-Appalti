package it.eldasoft.sil.pg.bl.tasks;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Task per l'inoltro delle comunicazioni al protocollo: task diviso in due fasi: 1. firma digitale massiva delle comunicazioni in stato 13
 * (autorizzata alla firma); 2. inoltro al protocollo delle comunicazioni in stato 14 (da inoltrare al protocollo).
 *
 * @author Luca.Giacomazzo
 */
public class ArchiviaEsportaDocumentiTask {

  static Logger                                 logger = Logger.getLogger(ArchiviaEsportaDocumentiTask.class);

  private CreazioneArchivioDocumentiGaraManager creazioneArchivioDocumentiGaraManager;

  private ArchiviazioneDocumentiManager         archiviazioneDocumentiManager;

  private SqlManager                            sqlManager;

  private GestioneWSDMManager      gestioneWSDMManager;

  public void setCreazioneArchivioDocumentiGaraManager(CreazioneArchivioDocumentiGaraManager creazioneArchivioDocumentiGaraManager) {
    this.creazioneArchivioDocumentiGaraManager = creazioneArchivioDocumentiGaraManager;
  }

  public void setArchiviazioneDocumentiManager(ArchiviazioneDocumentiManager archiviazioneDocumentiManager) {
    this.archiviazioneDocumentiManager = archiviazioneDocumentiManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  /**
   * archiviazione ed esportazione dei documenti di gara/elenco/catalogo
   *
   * @throws GestoreException
   * @throws JspException
   */
  public void archiviaEsportaDocumenti() throws GestoreException, IOException, JspException {

    if (WebUtilities.isAppNotReady()) {
      ;
    } else {
      if (logger.isDebugEnabled())
        logger.debug("Avvio archiviazione dei documenti di gara/elenco/catalogo");

      // Esportazione documentazione di gara/elenco/catalogo
      creazioneArchivioDocumentiGaraManager.creazioneArchivioDocumentiGara();

      // Archiviazione documentazione di gara/elenco/catalogo
      Long idArchiviazione = null;
      Long sysconRichiesta = null;
      Long idDoc = null;
      String codgara = null;
      String codice = null;
      String classificadocumento = null;
      String codiceregistrodocumento = null;
      String tipodocumento = null;
      String mittenteInterno = null;
      String idindice = null;
      String idtitolazione = null;
      String idunitaoperativadestinataria = null;
      String key1Doc = null;
      String key2Doc = null;
      String provenienza = null;
      String entita = null;
      Long genere = null;
      String username = null;
      String password = null;
      String ruolo = null;
      String nome = null;
      String cognome = null;
      String codiceuo = null;
      String idutente = null;
      String idutenteunop = null;
      String mezzo = null;
      String struttura = null;
      String supporto = null;
      String sottotipo = null;

      boolean tabellatiInDb= this.gestioneWSDMManager.isTabellatiInDb();

      String selRichiesteDaElaborare = "select j.id_archiviazione, j.codgara, j.syscon, j.classifica, j.cod_reg,"
          + " j.tipo_doc, j.mitt_int, j.indice, j.classifica_tit, j.uo_mittdest, j.mezzo, j.struttura, j.supporto, j.sottotipo "
          + " from gardoc_jobs j"
          + " where j.tipo_archiviazione = ? and j.da_processare = ?  ";

      String selectWslogin = "select l.username, l.password, l.ruolo, l.nome, l.cognome,l.codiceuo,l.idutente,l.idutenteunop from wslogin l" +
      		" where idconfiwsdm = ? and servizio= ?";

      String selDocumentiDaElaborare = "select w.id, w.key1, w.key2, w.provenienza"
          + " from gardoc_wsdm w where w.id_archiviazione = ? and (w.stato_archiviazione = ? ) ";

      List listaRichiesteDaElaborare = null;
      List listaDocumentiDaElaborare = null;
      try {
        listaRichiesteDaElaborare = sqlManager.getListVector(selRichiesteDaElaborare,
            new Object[] {new Long(2), new Long(1) });

        if (listaRichiesteDaElaborare != null && listaRichiesteDaElaborare.size() > 0) {
          for (int h = 0; h < listaRichiesteDaElaborare.size(); h++) {

            idArchiviazione = SqlManager.getValueFromVectorParam(listaRichiesteDaElaborare.get(h), 0).longValue();
            codgara = SqlManager.getValueFromVectorParam(listaRichiesteDaElaborare.get(h), 1).getStringValue();
            sysconRichiesta = SqlManager.getValueFromVectorParam(listaRichiesteDaElaborare.get(h), 2).longValue();
            classificadocumento = SqlManager.getValueFromVectorParam(listaRichiesteDaElaborare.get(h), 3).getStringValue();
            codiceregistrodocumento = SqlManager.getValueFromVectorParam(listaRichiesteDaElaborare.get(h), 4).getStringValue();
            tipodocumento = SqlManager.getValueFromVectorParam(listaRichiesteDaElaborare.get(h), 5).getStringValue();
            mittenteInterno = SqlManager.getValueFromVectorParam(listaRichiesteDaElaborare.get(h), 6).getStringValue();
            idindice = SqlManager.getValueFromVectorParam(listaRichiesteDaElaborare.get(h), 7).getStringValue();
            idtitolazione = SqlManager.getValueFromVectorParam(listaRichiesteDaElaborare.get(h), 8).getStringValue();
            idunitaoperativadestinataria = SqlManager.getValueFromVectorParam(listaRichiesteDaElaborare.get(h), 9).getStringValue();

            mezzo = SqlManager.getValueFromVectorParam(listaRichiesteDaElaborare.get(h), 10).getStringValue();
            struttura = SqlManager.getValueFromVectorParam(listaRichiesteDaElaborare.get(h), 11).getStringValue();
            supporto = SqlManager.getValueFromVectorParam(listaRichiesteDaElaborare.get(h), 12).getStringValue();
            sottotipo= SqlManager.getValueFromVectorParam(listaRichiesteDaElaborare.get(h), 13).getStringValue();
            String selectCodiceGara = "select codice,genere from v_gare_genere where codgar = ? and genere < 100";
            Vector<?> datiGareGenere = sqlManager.getVector(selectCodiceGara, new Object[] {codgara });
            if (datiGareGenere != null && datiGareGenere.size() > 0) {
              codice = SqlManager.getValueFromVectorParam(datiGareGenere, 0).stringValue();
              codice = UtilityStringhe.convertiNullInStringaVuota(codice);
              genere = (Long) SqlManager.getValueFromVectorParam(datiGareGenere, 1).getValue();
            }

            Long idconfi = this.gestioneWSDMManager.getWsdmConfigurazioneFromCodgar(codgara, "PG");
            if(idconfi!=null){
              Vector<?> datiWslogin = sqlManager.getVector(selectWslogin, new Object[] {idconfi, "DOCUMENTALE" });
              if (datiWslogin != null && datiWslogin.size() > 0) {
                username = (String) ((JdbcParametro) datiWslogin.get(0)).getValue();
                password = (String) ((JdbcParametro) datiWslogin.get(1)).getValue();
                ruolo = (String) ((JdbcParametro) datiWslogin.get(2)).getValue();
                nome = (String) ((JdbcParametro) datiWslogin.get(3)).getValue();
                cognome = (String) ((JdbcParametro) datiWslogin.get(4)).getValue();
                codiceuo = (String) ((JdbcParametro) datiWslogin.get(5)).getValue();
                idutente = (String) ((JdbcParametro) datiWslogin.get(6)).getValue();
                idutenteunop = (String) ((JdbcParametro) datiWslogin.get(7)).getValue();
              }

              listaDocumentiDaElaborare = sqlManager.getListVector(selDocumentiDaElaborare, new Object[] {idArchiviazione, new Long(1) });

              int numDocEsitoNegativo = 0;
              String messaggioApp = null;
              String statoGardocJobs = "2";

              String tipoCollegamento = this.gestioneWSDMManager.getcodiceTabellato("DOCUMENTALE", "tipocollegamento",idconfi.toString(),tabellatiInDb);

              if (listaDocumentiDaElaborare != null && listaDocumentiDaElaborare.size() > 0) {
                for (int p = 0; p < listaDocumentiDaElaborare.size(); p++) {
                  HashMap<String, Object> datiWSDM = new HashMap<String, Object>();
                  idDoc = SqlManager.getValueFromVectorParam(listaDocumentiDaElaborare.get(p), 0).longValue();
                  key1Doc = SqlManager.getValueFromVectorParam(listaDocumentiDaElaborare.get(p), 1).getStringValue();
                  key1Doc = UtilityStringhe.convertiNullInStringaVuota(key1Doc);
                  key2Doc = SqlManager.getValueFromVectorParam(listaDocumentiDaElaborare.get(p), 2).getStringValue();
                  key2Doc = UtilityStringhe.convertiNullInStringaVuota(key2Doc);
                  provenienza = SqlManager.getValueFromVectorParam(listaDocumentiDaElaborare.get(p), 3).getStringValue();
                  provenienza = UtilityStringhe.convertiNullInStringaVuota(provenienza);
                  datiWSDM.put("idArchiviazione", idArchiviazione);
                  datiWSDM.put("codgara", codgara);
                  datiWSDM.put("sysconRichiesta", sysconRichiesta);
                  datiWSDM.put("classificadocumento", classificadocumento);
                  datiWSDM.put("codiceregistrodocumento", codiceregistrodocumento);
                  datiWSDM.put("tipodocumento", tipodocumento);
                  datiWSDM.put("mittenteInterno", mittenteInterno);
                  datiWSDM.put("idindice", idindice);
                  datiWSDM.put("idtitolazione", idtitolazione);
                  datiWSDM.put("idunitaoperativadestinataria", idunitaoperativadestinataria);
                  datiWSDM.put("username", username);
                  datiWSDM.put("password", password);
                  datiWSDM.put("ruolo", ruolo);
                  datiWSDM.put("nome", nome);
                  datiWSDM.put("cognome", cognome);
                  datiWSDM.put("codiceuo", codiceuo);
                  datiWSDM.put("idutente", idutente);
                  datiWSDM.put("idutenteunop", idutenteunop);
                  datiWSDM.put("mezzo", mezzo);
                  datiWSDM.put("struttura", struttura);
                  datiWSDM.put("supporto", supporto);
                  datiWSDM.put("tipoCollegamento", tipoCollegamento);
                  datiWSDM.put("idconfi", idconfi.toString());
                  datiWSDM.put("sottotipo", sottotipo);
                  int esitoTrasferimento = archiviazioneDocumentiManager.trasferisciAlDocumentale(codice, genere, idDoc, key1Doc, key2Doc,
                      provenienza, datiWSDM);
                  if (esitoTrasferimento < 0) {
                    numDocEsitoNegativo = numDocEsitoNegativo + 1;
                  }

                }// for documenti da elaborare

              }// if documenti da elaborare

              if (numDocEsitoNegativo > 0) {
                // aggiungere altre precisazioni
                messaggioApp = "La richiesta non risulta elaborata correttamente";
              }

              // aggiorno la riga di archiviazione
              archiviazioneDocumentiManager.updateJobArchiviazioneDocumenti(idArchiviazione, statoGardocJobs, numDocEsitoNegativo,
                  messaggioApp, codgara, codice, genere, sysconRichiesta);
            }
          }// for listaRichiesteDaElaborare

        }// if listaRichiesteDaElaborare

      } catch (SQLException e) {
        throw new GestoreException("Errore nella lettura della lista dei documenti da trasferire al documentale", null, e);
      }
      if (logger.isDebugEnabled())
        logger.debug("Fine del trasferimento al documentale");

      // INIZIO GESTIONE TRASFERIMENTO A COS
      if (logger.isDebugEnabled())
        logger.debug("Gestione trasferimento a COS: avvio trasferimento ad area FTP");

      // Esportazione documentazione di gara/elenco/catalogo
      // creazioneArchivioDocumentiGaraManager.creazioneArchivioDocumentiGara();
      // Archiviazione documentazione di gara/elenco/catalogo

      String cos_selRichiesteDaElaborare = "select j.id_archiviazione, j.codgara, j.syscon, j.classifica, j.cod_reg,"
          + " j.tipo_doc, j.mitt_int, j.indice, j.classifica_tit, j.uo_mittdest "
          + " from gardoc_jobs j"
          + " where j.tipo_archiviazione = ? and j.da_processare = ?  ";

      String cos_selDocumentiDaElaborare = "select w.id, w.key1, w.key2, w.provenienza, w.entita"
          + " from gardoc_wsdm w where w.id_archiviazione = ? and (w.stato_archiviazione = ? ) ";

      List cos_listaRichiesteDaElaborare = null;
      List cos_listaDocumentiDaElaborare = null;
      try {
        cos_listaRichiesteDaElaborare = sqlManager.getListVector(cos_selRichiesteDaElaborare, new Object[] {new Long(3), new Long(1) });

        if (cos_listaRichiesteDaElaborare != null && cos_listaRichiesteDaElaborare.size() > 0) {
          for (int h = 0; h < cos_listaRichiesteDaElaborare.size(); h++) {

            idArchiviazione = SqlManager.getValueFromVectorParam(cos_listaRichiesteDaElaborare.get(h), 0).longValue();
            codgara = SqlManager.getValueFromVectorParam(cos_listaRichiesteDaElaborare.get(h), 1).getStringValue();
            sysconRichiesta = SqlManager.getValueFromVectorParam(cos_listaRichiesteDaElaborare.get(h), 2).longValue();

            String selectCodiceGara = "select codice,genere from v_gare_genere where codgar = ? and genere < 100";
            Vector<?> datiGareGenere = sqlManager.getVector(selectCodiceGara, new Object[] {codgara });
            if (datiGareGenere != null && datiGareGenere.size() > 0) {
              codice = SqlManager.getValueFromVectorParam(datiGareGenere, 0).stringValue();
              codice = UtilityStringhe.convertiNullInStringaVuota(codice);
              genere = (Long) SqlManager.getValueFromVectorParam(datiGareGenere, 1).getValue();
            }

            cos_listaDocumentiDaElaborare = sqlManager.getListVector(cos_selDocumentiDaElaborare, new Object[] {idArchiviazione,
                new Long(1) });

            int numDocEsitoNegativo = 0;
            String messaggioApp = null;
            String statoGardocJobs = "2";

            ArrayList<HashMap<String, Object>> documenti = new ArrayList<HashMap<String, Object>>();

            if (cos_listaDocumentiDaElaborare != null && cos_listaDocumentiDaElaborare.size() > 0) {
              for (int p = 0; p < cos_listaDocumentiDaElaborare.size(); p++) {

                idDoc = SqlManager.getValueFromVectorParam(cos_listaDocumentiDaElaborare.get(p), 0).longValue();
                key1Doc = SqlManager.getValueFromVectorParam(cos_listaDocumentiDaElaborare.get(p), 1).getStringValue();
                key1Doc = UtilityStringhe.convertiNullInStringaVuota(key1Doc);
                key2Doc = SqlManager.getValueFromVectorParam(cos_listaDocumentiDaElaborare.get(p), 2).getStringValue();
                key2Doc = UtilityStringhe.convertiNullInStringaVuota(key2Doc);
                provenienza = SqlManager.getValueFromVectorParam(cos_listaDocumentiDaElaborare.get(p), 3).getStringValue();
                provenienza = UtilityStringhe.convertiNullInStringaVuota(provenienza);
                entita = SqlManager.getValueFromVectorParam(cos_listaDocumentiDaElaborare.get(p), 4).getStringValue();
                entita = UtilityStringhe.convertiNullInStringaVuota(entita);

                HashMap<String, Object> d = new HashMap<String, Object>();
                d.put(ArchiviazioneDocumentiManager.KEY1, key1Doc);
                d.put(ArchiviazioneDocumentiManager.KEY2, key2Doc);
                d.put(ArchiviazioneDocumentiManager.IDDOC, idDoc);
                d.put(ArchiviazioneDocumentiManager.IDARCHIVIAZIONE, idArchiviazione);
                d.put(ArchiviazioneDocumentiManager.ENTITA, entita);
                d.put(ArchiviazioneDocumentiManager.PROVENIENZA, provenienza);
                documenti.add(d);

              } // for documenti da elaborare

              try {
                int esito = 1;
                for (int i = 0; i < documenti.size(); i++) {
                  esito = archiviazioneDocumentiManager.trasferisciAreaFTPCOS(documenti.get(i));
                  if(esito == 0){
                    numDocEsitoNegativo++;
                  }
                }
              } catch (Exception e) {
                logger.error("Errore durante la selezione dei documenti della gara da archiviare " + e.getMessage());
              }

            } // if documenti da elaborare

            if (numDocEsitoNegativo > 0) {
              // aggiungere altre precisazioni
              messaggioApp = "La richiesta non risulta elaborata correttamente";
            }

            // aggiorno la riga di archiviazione
            archiviazioneDocumentiManager.updateJobArchiviazioneDocumenti(idArchiviazione, statoGardocJobs, numDocEsitoNegativo,
                messaggioApp, codgara, codice, genere, sysconRichiesta);

          } // for listaRichiesteDaElaborare

        } // if listaRichiesteDaElaborare

      } catch (SQLException e) {
        throw new GestoreException("Errore nella lettura della lista dei documenti da trasferire a COS", null, e);
      }
      if (logger.isDebugEnabled())
        logger.debug("Gestione trasferimento a COS: fine trasferimento in area FTP");
    }
  }

}
