package it.eldasoft.sil.w3.bl;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.w3.utils.UtilityPermessi;
import it.eldasoft.simog.ws.xmlbeans.GaraDocument;
import it.eldasoft.simog.ws.xmlbeans.GaraType;
import it.eldasoft.simog.ws.xmlbeans.LottoType;
import it.eldasoft.simog.ws.xmlbeans.TecnicoType;
import it.eldasoft.utils.properties.ConfigManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

public class EldasoftSIMOGWSManager {

  static Logger               logger                  = Logger.getLogger(EldasoftSIMOGWSManager.class);

  private static final String PROP_PROTEZIONE_ARCHIVI = "it.eldasoft.protezionearchivi";

  private GeneManager         geneManager;

  private SqlManager          sqlManager;

  protected GenChiaviManager  genChiaviManager;

  private W3Manager           w3Manager;

  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  public void setW3Manager(W3Manager w3Manager) {
    this.w3Manager = w3Manager;
  }

  /**
   * Inserimento dei dati della gara e dei lotti
   * 
   * @param login
   * @param password
   * @param datiGaraLottoXML
   * @throws XmlException
   * @throws GestoreException
   * @throws SQLException
   * @throws Exception
   */
  public List<String[]> inserisciGaraLotto(String login, String password, String datiGaraLottoXML) throws Exception {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.inserisciGaraLotto: inizio metodo");

    List<String[]> messaggiDML = new Vector<String[]>();

    try {

      // Ricavo l'identificativo account dell'utente che ha inviato richiesta via WS
      Long idAccountRemoto = (long) this.w3Manager.getIdAccount(login, password);

      // Documento principale ricavato da parse XML
      GaraDocument garaDocument = GaraDocument.Factory.parse(datiGaraLottoXML);

      // Controllo congruenza struttura via XMLBEANS
      ArrayList<Object> validationErrors = new ArrayList<Object>();
      XmlOptions validationOptions = new XmlOptions();
      validationOptions.setErrorListener(validationErrors);
      boolean isValid = garaDocument.validate(validationOptions);

      if (isValid) {
        // Inserimento della gara
        this.gestioneW3GARA(idAccountRemoto, garaDocument, messaggiDML);
      } else {
        String listaErroriValidazione = "";
        Iterator<?> iter = validationErrors.iterator();
        while (iter.hasNext()) {
          listaErroriValidazione += iter.next() + " ";
        }
        logger.error("I dati inviati per la gare ed i lotti non rispettano il formato previsto: "
            + datiGaraLottoXML
            + "\n"
            + listaErroriValidazione);
        throw new Exception(UtilityTags.getResource("errors.inserisciGaraLotto.validate", null, false));
      }

    } catch (XmlException e) {
      logger.error("Errore nel lettura dei dati inviati: " + datiGaraLottoXML);
      throw new Exception(UtilityTags.getResource("errors.inserisciGaraLotto.xmlexception", null, false));
    } catch (SQLException e) {
      logger.error("Errore di database nella gestione dell'inserimento dei dati della gara e dei lotti: " + datiGaraLottoXML);
      throw new Exception(UtilityTags.getResource("errors.database.dataAccessException", null, false));
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.inserisciGaraLotto: fine metodo");

    return messaggiDML;

  }

  /**
   * Consultazione IDGARA sulla base dell'identificativo UUID
   * 
   * @param uuid
   * @return
   * @throws Exception
   */
  public String consultaIDGARA(String uuid) throws Exception {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.consultaIDGARA: inizio metodo");

    String idgara = null;

    try {

      // Verifico se esiste una gara con codice UUID
      Long conteggio = (Long) sqlManager.getObject("select count(*) from w3gara where gara_uuid = ?", new Object[] { uuid });

      if (conteggio != null && conteggio.longValue() > 0) {
        // Se esiste una gara procedo alla lettura dell'identificativo IDGARA
        idgara = (String) sqlManager.getObject("select id_gara from w3gara where gara_uuid = ?", new Object[] { uuid });
        if (idgara == null) {
          // Messaggio IDGARA non presente
          logger.error("Per la gara indicata (UUID: " + uuid + ") non e' ancora stato assegnato l'identificativo IDGARA");
          throw new Exception(UtilityTags.getResource("errors.consultaIDGARA.IDGARAnonAssegnato", null, false));
        }
      } else {
        // Messaggio (UUID) gara non presente
        logger.error("La gara indicata (UUID: " + uuid + ") non e' presente in base dati");
        throw new Exception(UtilityTags.getResource("errors.consultaIDGARA.UUIDnonPresente", null, false));
      }
    } catch (SQLException e) {
      logger.error("Errore di database nella gestione della richiesta di consultazione IDGARA", e);
      throw new Exception(UtilityTags.getResource(" errors.database.dataAccessException", null, false));
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.consultaIDGARA: fine metodo");

    return idgara;

  }

  /**
   * Consultazione CIG sulla base dell'identificativo UUID
   * 
   * @param uuid
   * @return
   * @throws Exception
   */
  public String consultaCIG(String uuid) throws Exception {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.consultaCIG: inizio metodo");

    String cig = null;

    try {

      // Verifico se esiste una gara con codice UUID
      Long conteggio = (Long) sqlManager.getObject("select count(*) from w3lott where lotto_uuid = ?", new Object[] { uuid });

      if (conteggio != null && conteggio.longValue() > 0) {
        // Se esiste un lotto procedo alla lettura dell'identificativo CIG
        cig = (String) sqlManager.getObject("select cig from w3lott where lotto_uuid = ?", new Object[] { uuid });
        if (cig == null) {
          // Messaggio CIG non presente
          logger.error("Per il lotto indicato (UUID: " + uuid + ") non e' ancora stato assegnato l'identificativo CIG");
          throw new Exception(UtilityTags.getResource("errors.consultaCIG.CIGnonAssegnato", null, false));
        }
      } else {
        // Messaggio (UUID) lotto non presente
        logger.error("Il lotto indicato (UUID: " + uuid + ") non e' presente in base dati");
        throw new Exception(UtilityTags.getResource("errors.consultaCIG.UUIDnonPresente", null, false));
      }
    } catch (SQLException e) {
      logger.error("Errore di database nella gestione della richiesta di consultazione CIG", e);
      throw new Exception(UtilityTags.getResource(" errors.database.dataAccessException", null, false));
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.consultaCIG: fine metodo");

    return cig;

  }

  /**
   * Inserimento/aggiornamento della gara in W3GARA.
   * 
   * @param idAccountRemoto
   * @param garaDocument
   * @throws GestoreException
   * @throws SQLException
   * @throws Exception
   */
  private void gestioneW3GARA(Long idAccountRemoto, GaraDocument garaDocument, List<String[]> messaggiDML) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.gestioneW3GARA: inizio metodo");

    try {

      GaraType datiGara = garaDocument.getGara();

      // Controllo di esistenza della gara sulla base del
      // codice univoco universale UUID
      boolean esisteW3GARA = this.w3Manager.esisteW3GARA_UUID(datiGara.getUUID());

      Long numgara = null;

      if (!esisteW3GARA) {
        // LA GARA NON ESISTE: SI PROCEDE A CREARNE UNA NUOVA

        // Calcolo della chiave
        numgara = this.getNextValNUMGARA();

        DataColumnContainer dccW3GARA = new DataColumnContainer(new DataColumn[] { new DataColumn("W3GARA.NUMGARA", new JdbcParametro(
            JdbcParametro.TIPO_NUMERICO, numgara)) });

        // Utente proprietario
        dccW3GARA.addColumn("W3GARA.SYSCON", JdbcParametro.TIPO_NUMERICO, idAccountRemoto);

        // Identificativo Univoco Universale
        dccW3GARA.addColumn("W3GARA.GARA_UUID", datiGara.getUUID());

        // Stato della gara: nuova gara in attesa di richiesta, a SIMOG,
        // del numero gara IDGARA
        dccW3GARA.addColumn("W3GARA.STATO_SIMOG", JdbcParametro.TIPO_NUMERICO, 1L);

        // Aggiungo la lista delle colonne, per poterle utilizzare
        // nel metodo successivo
        dccW3GARA.addColumn("W3GARA.OGGETTO", JdbcParametro.TIPO_TESTO, null);
        dccW3GARA.addColumn("W3GARA.TIPO_SCHEDA", JdbcParametro.TIPO_TESTO, null);
        dccW3GARA.addColumn("W3GARA.MODO_INDIZIONE", JdbcParametro.TIPO_NUMERICO, null);
        dccW3GARA.addColumn("W3GARA.MODO_REALIZZAZIONE", JdbcParametro.TIPO_NUMERICO, null);
        dccW3GARA.addColumn("W3GARA.IMPORTO_GARA", JdbcParametro.TIPO_DECIMALE, null);
        dccW3GARA.addColumn("W3GARA.RUP_CODTEC", JdbcParametro.TIPO_TESTO, null);
        dccW3GARA.addColumn("W3GARA.COLLABORAZIONE", JdbcParametro.TIPO_DECIMALE, null);
        dccW3GARA.addColumn("W3GARA.CIG_ACC_QUADRO", JdbcParametro.TIPO_TESTO, null);

        // Setto nel DataColumnContainer anche gli altri dati
        this.dccW3GARA_AltriDatiGara(dccW3GARA, datiGara, idAccountRemoto);

        dccW3GARA.insert("W3GARA", this.sqlManager);

        messaggiDML.add(new String[] { "GARA", "INS", datiGara.getUUID() });

      } else {
        // LA GARA ESISTE GIA': SI PROCEDE AD AGGIORNARLA PREVIO
        // SUPERAMENTO DI ALCUNI CONTROLLI

        // Ricavo la chiave
        numgara = this.w3Manager.getNUMGARA_UUID(datiGara.getUUID());

        // Controllo n. 1: i nuovi dati devono appartenere all'utente
        // che originariamente li ha inviati
        Long syscon_originale = (Long) sqlManager.getObject("select syscon from w3gara where numgara = ?", new Object[] { numgara });
        if (syscon_originale.longValue() != idAccountRemoto) {
          logger.error("La gara (NUMGARA: "
              + numgara.toString()
              + ") che si sta tentando di aggiornare e' di proprieta' di un altro utente: non e' possibile procedere nell'aggiornamento");
          throw new GestoreException(
              "La gara che si sta tentando di aggiornare e' di proprieta' di un altro utente: non e' possibile procedere nell'aggiornamento",
              "errors.inserisciGaraLotto.syscon.differente", null);
        }

        // Controllo n. 2: i nuovi dati devono essere in carico
        // allo stesso RUP che li aveva in carico originariamente
        String rup_originale = (String) sqlManager.getObject(
            "select tecni.cftec from tecni, w3gara where w3gara.rup_codtec = tecni.codtec and w3gara.numgara = ?", new Object[] { numgara });
        if (rup_originale != null) {
          if (!rup_originale.equals(datiGara.getRUP().getCFTEC())) {
            logger.error("La gara (NUMGARA: "
                + numgara.toString()
                + ")che si sta tentando di aggiornare e' in carico ad un RUP differente: non e' possibile procedere nell'aggiornamento");
            throw new GestoreException(
                "La gara che si sta tentando di aggiornare e' in carico ad un RUP differente: non e' possibile procedere nell'aggiornamento",
                "errors.inserisciGaraLotto.rup.differente", null);
          }
        }

        // Controllo n. 3: la gara deve essere in uno stato modificabile
        if (!this.w3Manager.isW3GARA_UUIDModificabile(datiGara.getUUID())) {
          logger.error("La gara (NUMGARA: "
              + numgara.toString()
              + ") che si sta tentando di aggiornare e' in uno stato non modificabile: non e' possibile procedere nell'aggiornamento");
          throw new GestoreException(
              "La gara che si sta tentando di aggiornare e' in uno stato non modificabile: non e' possibile procedere nell'aggiornamento",
              "errors.inserisciGaraLotto.nonmodificabile", null);
        }

        DataColumnContainer dccW3GARA = new DataColumnContainer(this.sqlManager, "W3GARA", "select * from w3gara where numgara = ?",
            new Object[] { numgara });

        // Aggiungo al DataColumnContainer anche gli altri dati comuni
        this.dccW3GARA_AltriDatiGara(dccW3GARA, datiGara, idAccountRemoto);

        if (dccW3GARA.isModifiedTable("W3GARA")) {

          Long stato_simog = dccW3GARA.getLong("W3GARA.STATO_SIMOG");
          if (stato_simog != null && !stato_simog.equals(1L)) {
            dccW3GARA.setValue("W3GARA.STATO_SIMOG", 3L);
          }

          dccW3GARA.getColumn("W3GARA.NUMGARA").setChiave(true);
          dccW3GARA.setValue("W3GARA.NUMGARA", numgara);
          dccW3GARA.setOriginalValue("W3GARA.NUMGARA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numgara));
          dccW3GARA.update("W3GARA", this.sqlManager);

          messaggiDML.add(new String[] { "GARA", "UPD", datiGara.getUUID() });

        }

      }

      // Gestione delle richieste di cancellazione dei lotti gia' inseriti
      // ma non piu' presenti nell'invio remoto
      this.gestioneRichiesteCancellazioneW3LOTT(datiGara, numgara, messaggiDML);

      // Gestione dell'inserimento/aggiornamento dei lotti associata alla gara
      this.gestioneW3LOTT(datiGara, numgara, messaggiDML);

    } catch (SQLException e) {
      throw new GestoreException("Errore di database nella gestione dell'inserimento della gare e dei lotti",
          "errors.inserisciGaraLotto.sqlerror", e);
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.gestioneW3GARA: fine metodo");

  }

  /**
   * Gestione delle richieste di cancellazione dei lotti presenti in base dati
   * ma non presenti nell'invio remoto
   * 
   * @param datiGara
   * @param numgara
   * @throws SQLException
   */
  private void gestioneRichiesteCancellazioneW3LOTT(GaraType datiGara, Long numgara, List<String[]> messaggiDML) throws SQLException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.gestioneRichiesteCancellazioneW3LOTT: inizio metodo");

    String selectW3LOTT = "select numlott, lotto_uuid, stato_simog from w3lott where numgara = ? and stato_simog in (1,2,3,4)";
    List<?> datiW3LOTT = this.sqlManager.getListVector(selectW3LOTT, new Object[] { numgara });

    LottoType[] datiListaLotti = datiGara.getLOTTIArray();

    if (datiW3LOTT != null && datiW3LOTT.size() > 0 && datiListaLotti != null && datiListaLotti.length > 0) {

      for (int i = 0; i < datiW3LOTT.size(); i++) {

        boolean richiestaDiCancellazione = true;

        Long numlott = (Long) SqlManager.getValueFromVectorParam(datiW3LOTT.get(i), 0).getValue();
        String lotto_uuid = (String) SqlManager.getValueFromVectorParam(datiW3LOTT.get(i), 1).getValue();
        Long stato_simog = (Long) SqlManager.getValueFromVectorParam(datiW3LOTT.get(i), 2).getValue();

        for (int j = 0; j < datiListaLotti.length; j++) {

          LottoType datiLotto = datiListaLotti[j];
          if (lotto_uuid != null && lotto_uuid.equals(datiLotto.getUUID())) {
            richiestaDiCancellazione = false;
          }

        }

        if (richiestaDiCancellazione == true) {
          if (stato_simog != null && !stato_simog.equals(1L)) {
            this.sqlManager.update("update w3lott set stato_simog = ? where numgara = ? and numlott = ?", new Object[] { 5L, numgara, numlott });
            messaggiDML.add(new String[] { "LOTTO", "DEL", lotto_uuid });
          } else {
            this.sqlManager.update("delete from w3lottcate where numgara = ? and numlott = ?", new Object[] { numgara, numlott });
            this.sqlManager.update("delete from w3lott where numgara = ? and numlott = ?", new Object[] { numgara, numlott });
            messaggiDML.add(new String[] { "LOTTO", "DEL", lotto_uuid });
          }
        }
      }
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.gestioneRichiesteCancellazioneW3LOTT: fine metodo");

  }

  /**
   * Inserimento dei lotti associati alla gara
   * 
   * @param datiGara
   * @param numgara
   * @throws GestoreException
   * @throws SQLException
   */
  private void gestioneW3LOTT(GaraType datiGara, Long numgara, List<String[]> messaggiDML) throws GestoreException, SQLException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.gestioneW3LOTT: inizio metodo");

    LottoType[] datiListaLotti = datiGara.getLOTTIArray();

    if (datiListaLotti != null && datiListaLotti.length > 0) {
      for (int i = 0; i < datiListaLotti.length; i++) {

        LottoType datiLotto = datiListaLotti[i];

        // Controllo di univocita' del lotto sulla base dell'identificativo
        // universale univoco
        boolean esisteW3LOTT = this.w3Manager.esisteW3LOTT_UUID(datiLotto.getUUID());

        Long numlott = null;

        // Il lotto non esiste di procede all'inserimento di un nuovo lotto
        if (!esisteW3LOTT) {
          numlott = this.getNextValNUMLOTT(numgara);

          DataColumnContainer dccW3LOTT = new DataColumnContainer(new DataColumn[] { new DataColumn("W3LOTT.NUMGARA", new JdbcParametro(
              JdbcParametro.TIPO_NUMERICO, numgara)) });
          dccW3LOTT.addColumn("W3LOTT.NUMLOTT", JdbcParametro.TIPO_NUMERICO, numlott);

          // Identificativo Univoco Universale
          dccW3LOTT.addColumn("W3LOTT.LOTTO_UUID", datiLotto.getUUID());

          // Stato del lotto: nuovo lotto in attesa di richiesta, a SIMOG,
          // del codice identificativo CIG
          dccW3LOTT.addColumn("W3LOTT.STATO_SIMOG", JdbcParametro.TIPO_NUMERICO, 1L);

          // Aggiunto gli altri campi in modo da poterli utilizzare
          // nel metodo successivo
          dccW3LOTT.addColumn("W3LOTT.OGGETTO", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.SOMMA_URGENZA", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.TIPO_CONTRATTO", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.FLAG_ESCLUSO", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.CPV", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.ID_SCELTA_CONTRAENTE", JdbcParametro.TIPO_NUMERICO, null);
          dccW3LOTT.addColumn("W3LOTT.IMPORTO_LOTTO", JdbcParametro.TIPO_DECIMALE, null);
          dccW3LOTT.addColumn("W3LOTT.IMPORTO_ATTUAZIONE_SICUREZZA", JdbcParametro.TIPO_DECIMALE, null);
          dccW3LOTT.addColumn("W3LOTT.ID_CATEGORIA_PREVALENTE", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.LUOGO_ISTAT", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.LUOGO_NUTS", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.TRIENNIO_ANNO_INIZIO", JdbcParametro.TIPO_NUMERICO, null);
          dccW3LOTT.addColumn("W3LOTT.TRIENNIO_ANNO_FINE", JdbcParametro.TIPO_NUMERICO, null);
          dccW3LOTT.addColumn("W3LOTT.TRIENNIO_PROGRESSIVO", JdbcParametro.TIPO_NUMERICO, null);

          // Setto nel DataColumnContainer anche gli altri dati
          this.dccW3LOTT_AltriDatiLotto(dccW3LOTT, datiLotto, numgara, numlott);

          dccW3LOTT.insert("W3LOTT", this.sqlManager);

          messaggiDML.add(new String[] { "LOTTO", "INS", datiLotto.getUUID() });

        }

        // Il lotto esiste gia' si procede all'aggiornamento
        if (esisteW3LOTT) {

          HashMap<String, Long> hMap = new HashMap<String, Long>();
          hMap = this.w3Manager.getNUMGARA_NUMLOTT_UUID(datiLotto.getUUID());
          numlott = ((Long) hMap.get("numlott"));

          // Il lotto deve essere in uno stato modificabile
          if (this.w3Manager.isW3LOTT_UUIDModificabile(datiLotto.getUUID())) {

            DataColumnContainer dccW3LOTT = new DataColumnContainer(this.sqlManager, "W3LOTT",
                "select * from w3lott where numgara = ? and numlott = ?", new Object[] { numgara, numlott });

            // Aggiungo al DataColumnContainer anche gli altri dati comuni
            boolean listaUlterioriCategorieModificata = this.dccW3LOTT_AltriDatiLotto(dccW3LOTT, datiLotto, numgara, numlott);

            if (dccW3LOTT.isModifiedTable("W3LOTT") || listaUlterioriCategorieModificata == true) {
              // Stato del lotto: lotto modifica in attesa di allineamento a SIMOG
              Long stato_simog = dccW3LOTT.getLong("W3LOTT.STATO_SIMOG");
              if (stato_simog != null && !stato_simog.equals(1L)) {
                dccW3LOTT.setValue("W3LOTT.STATO_SIMOG", 3L);
              }

              dccW3LOTT.getColumn("W3LOTT.NUMGARA").setChiave(true);
              dccW3LOTT.getColumn("W3LOTT.NUMLOTT").setChiave(true);
              dccW3LOTT.setValue("W3LOTT.NUMGARA", numgara);
              dccW3LOTT.setValue("W3LOTT.NUMLOTT", numlott);

              dccW3LOTT.update("W3LOTT", this.sqlManager);

              messaggiDML.add(new String[] { "LOTTO", "UPD", datiLotto.getUUID() });

            }
          }
        }
      }
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.gestioneW3LOTT: fine metodo");

  }

  /**
   * Gestione dei dati comuni per l'inserimento e l'aggiornamento in W3LOTT
   * 
   * @param dccW3LOTT
   * @param datiLotto
   * @param numgara
   * @param numlott
   * @throws GestoreException
   * @throws SQLException
   */
  private boolean dccW3LOTT_AltriDatiLotto(DataColumnContainer dccW3LOTT, LottoType datiLotto, Long numgara, Long numlott)
      throws GestoreException, SQLException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.dccW3LOTT_AltriDatiLotto: inizio metodo");

    boolean isListaUlterioriCategorieModificata = false;

    // Oggetto
    dccW3LOTT.setValue("W3LOTT.OGGETTO", datiLotto.getOGGETTO());

    // Somma urgenza
    if (datiLotto.isSetSOMMAURGENZA()) {
      dccW3LOTT.setValue("W3LOTT.SOMMA_URGENZA", datiLotto.getSOMMAURGENZA().toString());
    }

    // Tipo contratto
    dccW3LOTT.setValue("W3LOTT.TIPO_CONTRATTO", datiLotto.getTIPOCONTRATTO().toString());

    // Flag escluso
    if (datiLotto.isSetFLAGESCLUSO()) {
      dccW3LOTT.setValue("W3LOTT.FLAG_ESCLUSO", datiLotto.getFLAGESCLUSO().toString());
    }

    // CPV
    if (datiLotto.isSetCPV()) {
      dccW3LOTT.setValue("W3LOTT.CPV", datiLotto.getCPV());
    }

    // Modalita' di scelta del contraente
    if (datiLotto.isSetIDSCELTACONTRAENTE()) {
      dccW3LOTT.setValue("W3LOTT.ID_SCELTA_CONTRAENTE", (long) datiLotto.getIDSCELTACONTRAENTE().intValue());
    }

    // Importo totale del lotto
    dccW3LOTT.setValue("W3LOTT.IMPORTO_LOTTO", datiLotto.getIMPORTOLOTTO());

    // Importo per l'attuazione della sicurezza
    if (datiLotto.isSetIMPORTOATTUAZIONESICUREZZA()) {
      dccW3LOTT.setValue("W3LOTT.IMPORTO_ATTUAZIONE_SICUREZZA", datiLotto.getIMPORTOATTUAZIONESICUREZZA());
    }

    // Categoria prevalente
    if (datiLotto.isSetIDCATEGORIAPREVALENTE()) {
      dccW3LOTT.setValue("W3LOTT.ID_CATEGORIA_PREVALENTE", datiLotto.getIDCATEGORIAPREVALENTE());
    }

    // Gestione delle categorie
    if (datiLotto.isSetCATEGORIE()) {

      boolean inserisciCategorie = true;

      String[] datiCategorie = datiLotto.getCATEGORIE().getCATEGORIAArray();

      // Confronto la lista delle categorie gia' memorizzate con quelle inviate
      // via WS
      // Se ci sono differenze provvedo a cancellare le categorie presenti
      // per poi inserirle nuovamente in blocco
      String sqlSelectW3LOTTCATE = "select categoria from w3lottcate where numgara = ? and numlott = ?";
      List<?> datiW3LOTTCATE = this.sqlManager.getListVector(sqlSelectW3LOTTCATE, new Object[] { numgara, numlott });
      if (datiW3LOTTCATE != null && datiW3LOTTCATE.size() > 0) {

        String[] datiCategorieW3LOTTCATE = new String[datiW3LOTTCATE.size()];
        for (int i = 0; i < datiW3LOTTCATE.size(); i++) {
          datiCategorieW3LOTTCATE[i] = (String) SqlManager.getValueFromVectorParam(datiW3LOTTCATE.get(i), 0).getValue();
        }
        java.util.Arrays.sort(datiCategorieW3LOTTCATE);
        java.util.Arrays.sort(datiCategorie);

        if (java.util.Arrays.equals(datiCategorieW3LOTTCATE, datiCategorie)) {
          inserisciCategorie = false;
        } else {
          isListaUlterioriCategorieModificata = true;
          inserisciCategorie = true;
          String sqlDeleteW3LOTTCATE = "delete from w3lottcate where numgara = ? and numlott = ?";
          this.sqlManager.update(sqlDeleteW3LOTTCATE, new Object[] { numgara, numlott });
        }

      }

      // Inserimento di tutte le categorie
      if (inserisciCategorie) {
        if (datiCategorie != null && datiCategorie.length > 0) {
          for (int j = 0; j < datiCategorie.length; j++) {
            DataColumnContainer dccW3LOTTCATE = new DataColumnContainer(new DataColumn[] { new DataColumn("W3LOTTCATE.NUMGARA",
                new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numgara)) });
            dccW3LOTTCATE.addColumn("W3LOTTCATE.NUMLOTT", JdbcParametro.TIPO_NUMERICO, numlott);
            dccW3LOTTCATE.addColumn("W3LOTTCATE.NUMCATE", JdbcParametro.TIPO_NUMERICO, (long) (j + 1));
            dccW3LOTTCATE.addColumn("W3LOTTCATE.CATEGORIA", JdbcParametro.TIPO_TESTO, datiCategorie[j]);
            dccW3LOTTCATE.insert("W3LOTTCATE", this.sqlManager);
          }
        }
      }
    }

    // Luogo ISTAT
    if (datiLotto.isSetLUOGOISTAT()) {
		  String luogoIstat = datiLotto.getLUOGOISTAT();
		  if (luogoIstat.length() == 6) {
		    String codiceIstatRegione = (String) this.sqlManager.getObject(
		      "select tabcod3 from tabsche where tabcod = 'S2003' and tabcod1='09' and tabcod3 like ?",
		      new Object[] { "%".concat(luogoIstat) });
		    if (StringUtils.isNotEmpty(codiceIstatRegione)) {
		      luogoIstat = codiceIstatRegione;
		    } else {
		      luogoIstat = null;
		    }
		  }
      dccW3LOTT.setValue("W3LOTT.LUOGO_ISTAT", luogoIstat);
    }

    // Luogo NUTS
    if (datiLotto.isSetLUOGONUTS()) {
      dccW3LOTT.setValue("W3LOTT.LUOGO_NUTS", datiLotto.getLUOGONUTS().toString());
    }

    // Triennio anno inizio
    if (datiLotto.isSetTRIENNIOANNOINIZIO()) {
      dccW3LOTT.setValue("W3LOTT.TRIENNIO_ANNO_INIZIO", (long) datiLotto.getTRIENNIOANNOINIZIO());
    }

    // Triennio anno fine
    if (datiLotto.isSetTRIENNIOANNOFINE()) {
      dccW3LOTT.setValue("W3LOTT.TRIENNIO_ANNO_FINE", (long) datiLotto.getTRIENNIOANNOFINE());
    }

    // Triennio progressivo
    if (datiLotto.isSetTRIENNIOPROGRESSIVO()) {
      dccW3LOTT.setValue("W3LOTT.TRIENNIO_PROGRESSIVO", (long) datiLotto.getTRIENNIOPROGRESSIVO());
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.dccW3LOTT_AltriDatiLotto: fine metodo");

    return isListaUlterioriCategorieModificata;

  }

  /**
   * Gestione dei dati della gara comuni all'inserimento e all'aggiornamento in
   * W3GARA
   * 
   * @param dccW3GARA
   * @param datiGara
   * @param syscon_remoto
   * @throws GestoreException
   * @throws SQLException
   */
  private void dccW3GARA_AltriDatiGara(DataColumnContainer dccW3GARA, GaraType datiGara, Long syscon_remoto) throws GestoreException,
      SQLException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.dccW3GARA_AltriDatiGara: inizio metodo");

    // Oggetto
    dccW3GARA.setValue("W3GARA.OGGETTO", datiGara.getOGGETTO());

    // Tipo scheda
    if (datiGara.isSetTIPOSCHEDA()) {
      dccW3GARA.setValue("W3GARA.TIPO_SCHEDA", datiGara.getTIPOSCHEDA().toString());
    }

    // Modo indizione
    if (datiGara.isSetMODOINDIZIONE()) {
      dccW3GARA.setValue("W3GARA.MODO_INDIZIONE", Long.parseLong(datiGara.getMODOINDIZIONE().toString()));
    }

    // Modo realizzazione
    if (datiGara.isSetMODOREALIZZAZIONE()) {
      dccW3GARA.setValue("W3GARA.MODO_REALIZZAZIONE", Long.parseLong(datiGara.getMODOREALIZZAZIONE().toString()));
    }

    // Importo gara
    if (datiGara.isSetIMPORTOGARA()) {
      dccW3GARA.setValue("W3GARA.IMPORTO_GARA", datiGara.getIMPORTOGARA());
    }

    // RUP
    String rup_codtec = this.gestisciRUP(datiGara.getRUP(), syscon_remoto);
    dccW3GARA.setValue("W3GARA.RUP_CODTEC", rup_codtec);

    /*APPALTI-1063
    // Gestione della collaborazione.
    // Inserisco la collaborazione di default solo se la collaborazione
    // associata al RUP indicato è una sola
    if (rup_codtec != null) {
      List<?> datiW3AZIENDAUFFICIO = this.sqlManager.getListVector(
          "select w3usrsyscoll.w3aziendaufficio_id from w3usrsyscoll, w3usrsys where w3usrsyscoll.syscon = w3usrsys.syscon and w3usrsys.rup_codtec = ?",
          new Object[] { rup_codtec });

      if (datiW3AZIENDAUFFICIO != null && datiW3AZIENDAUFFICIO.size() == 1) {
        Long collaborazione = (Long) SqlManager.getValueFromVectorParam(datiW3AZIENDAUFFICIO.get(0), 0).getValue();
        dccW3GARA.setValue("W3GARA.COLLABORAZIONE", collaborazione);
      }
    }*/

    // CIG accordo quadro
    if (datiGara.isSetCIGACCQUADRO()) {
      dccW3GARA.setValue("W3GARA.CIG_ACC_QUADRO", datiGara.getCIGACCQUADRO());
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.dccW3GARA_AltriDatiGara: fine metodo");

  }

  /**
   * Gestione del RUP incaricato
   * 
   * @param tecnico
   * @param syscon_remoto
   * @return
   * @throws GestoreException
   * @throws SQLException
   */
  private String gestisciRUP(TecnicoType tecnico, Long syscon_remoto) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.gestisciRUP: inizio metodo");

    String codtec = null;

    try {

      String protezioneArchivi = ConfigManager.getValore(PROP_PROTEZIONE_ARCHIVI);

      boolean esisteTecnico = false;
      String cftec = null;
      String pivatec = null;

      cftec = tecnico.getCFTEC();
      if (tecnico.isSetPIVATEC()) pivatec = tecnico.getPIVATEC();

      if (cftec != null) {
        if (protezioneArchivi != null && "1".equals(protezioneArchivi)) {
          String selectTECNI = "select tecni.codtec from tecni, w3permessi "
              + " where tecni.cftec = ? "
              + " and w3permessi.codtec = tecni.codtec "
              + " and w3permessi.syscon = ?";
          codtec = (String) sqlManager.getObject(selectTECNI, new Object[] { cftec, syscon_remoto });
        } else {
          String selectTECNI = "select codtec from tecni where cftec = ?";
          codtec = (String) sqlManager.getObject(selectTECNI, new Object[] { cftec });
        }
      } else if (pivatec != null) {
        if (protezioneArchivi != null && "1".equals(protezioneArchivi)) {
          String selectTECNI = "select tecni.codtec from tecni, w3permessi "
              + " where tecni.pivatec = ? "
              + " and w3permessi.codtec = tecni.codtec "
              + " and w3permessi.syscon = ?";
          codtec = (String) sqlManager.getObject(selectTECNI, new Object[] { pivatec, syscon_remoto });
        } else {
          String selectTECNI = "select codtec from tecni where pivatec = ?";
          codtec = (String) sqlManager.getObject(selectTECNI, new Object[] { pivatec });
        }
      }
      if (codtec != null) esisteTecnico = true;

      if (!esisteTecnico) {
        codtec = this.geneManager.calcolaCodificaAutomatica("TECNI", "CODTEC");

        DataColumn[] dcTECNI = new DataColumn[] { new DataColumn("TECNI.CODTEC", new JdbcParametro(JdbcParametro.TIPO_TESTO, codtec)) };
        DataColumnContainer dccTECNI = new DataColumnContainer(dcTECNI);

        if (tecnico.isSetCOGTEI()) dccTECNI.addColumn("TECNI.COGTEI", JdbcParametro.TIPO_TESTO, tecnico.getCOGTEI());

        if (tecnico.isSetNOMETEI()) dccTECNI.addColumn("TECNI.NOMETEI", JdbcParametro.TIPO_TESTO, tecnico.getNOMETEI());

        dccTECNI.addColumn("TECNI.NOMTEC", JdbcParametro.TIPO_TESTO, tecnico.getNOMTEC());

        dccTECNI.addColumn("TECNI.CFTEC", JdbcParametro.TIPO_TESTO, cftec);

        if (pivatec != null) dccTECNI.addColumn("TECNI.PIVATEC", JdbcParametro.TIPO_TESTO, pivatec);

        dccTECNI.insert("TECNI", this.sqlManager);

        // Gestione delle protezioni
        if (protezioneArchivi != null && "1".equals(protezioneArchivi)) {
          this.w3Manager.gestioneProtezioneArchivi(syscon_remoto, "TECNI", codtec);
        }
        UtilityPermessi.inserisciPermessi(syscon_remoto, "TECNI", "CODTEC", codtec, sqlManager);

      }
    } catch (SQLException e) {
      throw new GestoreException("Errore di database nella gestione del RUP incaricato", "errors.inserisciGaraLotto.gestisciRUP.sqlerror",
          e);
    }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.gestisciRUP: fine metodo");

    return codtec;

  }

  /**
   * Calcola in NUMGARA di W3GARA
   * 
   * @return
   */
  private Long getNextValNUMGARA() throws GestoreException {

    if (logger.isDebugEnabled()) 
    	logger.debug("getNextValNUMGARA: inizio metodo");

    Long numgara = (long) this.genChiaviManager.getMaxId("W3GARA", "NUMGARA");
    numgara = numgara + 1;

    if (logger.isDebugEnabled()) 
    	logger.debug("getNextValNUMGARA: inizio metodo");

    return numgara;
  }

  /**
   * Calcola NUMLOTT di W3LOTT
   * 
   * @param numgara
   * @return
   * @throws GestoreException
   */
  private Long getNextValNUMLOTT(Long numgara) throws GestoreException {

    if (logger.isDebugEnabled()) 
    	logger.debug("getNextValNUMLOTT: inizio metodo");

    Long numlott = null;

    try {
      numlott = (Long) this.sqlManager.getObject("select max(numlott) from w3lott where numgara = ?", new Object[] { numgara });
      if (numlott == null) 
    	  numlott = 0L;
      numlott = numlott.longValue() + 1;
    } catch (SQLException e) {
      throw new GestoreException("Errore durante il controllo del lotto", "controlloW3LOTT", e);
    }

    if (logger.isDebugEnabled()) 
    	logger.debug("getNextValNUMLOTT: inizio metodo");

    return numlott;
  }

}
