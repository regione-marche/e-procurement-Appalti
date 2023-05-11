/*
 * Created on 04/ago/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.integrazioni.CinecaAnagraficaComuneManager;
import it.eldasoft.gene.bl.integrazioni.CinecaWSManager;
import it.eldasoft.gene.bl.integrazioni.CinecaWSPersoneFisicheManager;
import it.eldasoft.gene.bl.scadenz.ScadenzariManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.bl.AnagraficaManager;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.utils.ListaDocumentiPortaleUtilities;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiRicezioneFunction;
import it.eldasoft.sil.portgare.datatypes.AbilitazionePreventivaType;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoAnagraficaImpresaDocument;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoIscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.sil.portgare.datatypes.AlboProfessionaleType;
import it.eldasoft.sil.portgare.datatypes.AltriDatiAnagraficiType;
import it.eldasoft.sil.portgare.datatypes.CameraCommercioType;
import it.eldasoft.sil.portgare.datatypes.CassaEdileType;
import it.eldasoft.sil.portgare.datatypes.CassaPrevidenzaType;
import it.eldasoft.sil.portgare.datatypes.ContoCorrenteDedicatoType;
import it.eldasoft.sil.portgare.datatypes.DatoAnnuoImpresaType;
import it.eldasoft.sil.portgare.datatypes.DocumentoType;
import it.eldasoft.sil.portgare.datatypes.INAILType;
import it.eldasoft.sil.portgare.datatypes.INPSType;
import it.eldasoft.sil.portgare.datatypes.ISO9001Type;
import it.eldasoft.sil.portgare.datatypes.ImpresaType;
import it.eldasoft.sil.portgare.datatypes.IndirizzoEstesoType;
import it.eldasoft.sil.portgare.datatypes.IndirizzoType;
import it.eldasoft.sil.portgare.datatypes.IscrizioneElenchiRicostruzioneType;
import it.eldasoft.sil.portgare.datatypes.IscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.sil.portgare.datatypes.IscrizioneWhitelistAntimafiaType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.sil.portgare.datatypes.ListaPartecipantiRaggruppamentoType;
import it.eldasoft.sil.portgare.datatypes.PartecipanteRaggruppamentoType;
import it.eldasoft.sil.portgare.datatypes.RatingLegalitaType;
import it.eldasoft.sil.portgare.datatypes.RecapitiType;
import it.eldasoft.sil.portgare.datatypes.ReferenteImpresaType;
import it.eldasoft.sil.portgare.datatypes.RegistrazioneImpresaDocument;
import it.eldasoft.sil.portgare.datatypes.RichiestaGenericaType;
import it.eldasoft.sil.portgare.datatypes.RichiestaVariazioneDocument;
import it.eldasoft.sil.portgare.datatypes.RinnovoIscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.sil.portgare.datatypes.SOAType;
import it.eldasoft.utils.profiles.FiltroLivelloUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.profiles.cache.DizionarioLivelli;
import it.eldasoft.utils.profiles.domain.Livello;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityFiscali;
import it.eldasoft.utils.utility.UtilityMath;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.eldasoft.www.PortaleAlice.EsitoOutType;
import it.eldasoft.www.PortaleAlice.PortaleAliceProxy;
import it.maggioli.eldasoft.ws.erp.WSERPUgovAnagraficaType;
import it.maggioli.eldasoft.ws.erp.WSERPUgovResType;
import it.maggioli.eldasoft.ws.erp.WSERP_PortType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * Classe di gestione delle funzionalita' comuni di PG
 *
 * @author Luca.Giacomazzo
 */
public class PgManager {

  /** Logger */
  static Logger               logger                        = Logger.getLogger(PgManager.class);

  public static final String  messaggioCodAutNonPresente    = "Per poter procedere nell'acquisizione della registrazione"
                                                                + " occorre impostare la codifica automatica attiva anche per gli archivi generali";


  private SqlManager          sqlManager;

  /** Manager per le transazioni e selezioni nel database */
  private GeneManager         geneManager;

  /** Manager per l'interrogazione dei tabellati */
  private TabellatiManager            tabellatiManager;

  /** Manager per l'interrogazione del campo blob della w_DOCDIG */
  private FileAllegatoManager fileAllegatoManager;

  private AnagraficaManager   anagraficaManager;

  /** Manager per la gestione dello Scadenzario */
  private ScadenzariManager   scadenzariManager;

  /** Manager per la gestione delle chiavi di una entita */
  private GenChiaviManager   genChiaviManager;

  /** Manager per gestire l'integrazione con Cineca */
  private CinecaWSManager cinecaWSManager;

  private CinecaWSPersoneFisicheManager cinecaWSPersoneFisicheManager;

  private CinecaAnagraficaComuneManager cinecaAnagraficaComuneManager;
  
  /** Manager per gestire l'integrazione programmazione (RdA/RdI)*/
  private GestioneProgrammazioneManager gestioneProgrammazioneManager;


  /**
   * Nome del file per la registrazione dei messaggi provenienti da Portale
   * Alice
   */


	private static final String REPLACEMENT_NGARA = "#NGARA#";
	private static final String REPLACEMENT_CODGAR = "#CODGAR#";
	private static final String REPLACEMENT_CODIGA = "#CODIGA#";
	private static final String REPLACEMENT_G1CODCIG = "#G1CODCIG#";
	private static final String REPLACEMENT_OGGETA = "#OGGETA#";
	private static final String REPLACEMENT_G1DESTOR = "#G1DESTOR#";
	private static final String REPLACEMENT_OGGETTOGA  = "#OGGETTOGA#";
	private static final String REPLACEMENT_DTEPAR  = "#DTEPAR#";
	private static final String REPLACEMENT_OTEPAR  = "#OTEPAR#";
	private static final String REPLACEMENT_DTEOFF  = "#DTEOFF#";
	private static final String REPLACEMENT_OTEOFF  = "#OTEOFF#";
	private static final String REPLACEMENT_DESOFF  = "#DESOFF#";
	private static final String REPLACEMENT_OESOFF  = "#OESOFF#";
	private static final String REPLACEMENT_DATAOGGI  = "#DATAOGGI#";

	private static final String REPLACEMENT_G1NPROGG = "#G1NPROGG#";
	private static final String REPLACEMENT_G1NUMORDPL = "#G1NUMORDPL#";
	private static final String REPLACEMENT_DITTAO = "#DITTAO#";
	private static final String REPLACEMENT_NOMIMP = "#NOMIMP#";
	private static final String REPLACEMENT_CFIMP = "#CFIMP#";
	private static final String REPLACEMENT_PIVIMP = "#PIVIMP#";
	private static final String REPLACEMENT_G_DTRISOA  = "#G_DTRISOA#";
	private static final String REPLACEMENT_G_DSCANC  = "#G_DSCANC#";

	private static final String REPLACEMENT_NOMTECRUP  = "#NOMTECRUP#";
	private static final String REPLACEMENT_CFTECRUP  = "#CFTECRUP#";
    private static final String REPLACEMENT_INCTEC  = "#INCTECRUP#";

    private static final String REPLACEMENT_URL_MEVAL  = "#URL_MEVAL#";

    private static final String costanteIdContratto = "  #NREPAT#";

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  private final String codapp = "PG";

  /**
   * @param geneManager
   *        geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  public void setAnagraficaManager(AnagraficaManager anagraficaManager) {
    this.anagraficaManager = anagraficaManager;
  }

  public void setScadenzariManager(ScadenzariManager scadenzariManager) {
    this.scadenzariManager = scadenzariManager;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  public void setCinecaWSManager(CinecaWSManager cinecaWSManager) {
    this.cinecaWSManager = cinecaWSManager;
  }

  public void setCinecaWSPersoneFisicheManager(CinecaWSPersoneFisicheManager cinecaWSPersoneFisicheManager) {
    this.cinecaWSPersoneFisicheManager = cinecaWSPersoneFisicheManager;
  }

  public void setCinecaAnagraficaComuneManager(CinecaAnagraficaComuneManager cinecaAnagraficaComuneManager) {
    this.cinecaAnagraficaComuneManager = cinecaAnagraficaComuneManager;
  }
  
  public void setGestioneProgrammazioneManager(GestioneProgrammazioneManager gestioneProgrammazioneManager) {
    this.gestioneProgrammazioneManager = gestioneProgrammazioneManager;
  }

  /**
   * Aggiornamento delle ditte escluse o vincitrici in altri lotti della gara
   *
   * @param codiceTornata
   * @param codiceLotto
   * @param eseguiUpdateDittaEsclusa
   * @return Ritorna null se l'operazione termina con successo, altrimenti il
   *         metodo
   * @throws GestoreException
   */
  public void updateDitteEscluseVincitriciAltriLotti(String codiceTornata,
      String codiceLotto, boolean eseguiUpdateDittaEsclusa)
      throws GestoreException {

    String codiceDitta = null;
    try {
      // Estraggo la lista delle ditte che partecipano al lotto che si sta
      // considerando
      List<?> listaDitte = this.sqlManager.getListVector(
          "select DITTAO, AMMGAR, STAGGI from DITG " + "where NGARA5 = ? ",
          new Object[] { codiceLotto });

      if (listaDitte != null && listaDitte.size() > 0) {
        for (int i = 0; i < listaDitte.size(); i++) {

          Vector<?> ditta = (Vector<?>) listaDitte.get(i);
          String dittaAmmessaGara = ((JdbcParametro) ditta.get(1)).getStringValue();
          Object dittaAggiudicata = ((JdbcParametro) ditta.get(2)).getValue();

          if ((dittaAmmessaGara == null || !"2".equals(dittaAmmessaGara))
              && dittaAggiudicata == null) {
            codiceDitta = ((JdbcParametro) ditta.get(0)).getStringValue();

            boolean updateDitteVincitriciEseguito = this.aggiornaDITGDittaVincitriceAltriLotti(
                codiceTornata, codiceLotto, codiceDitta);

            if (eseguiUpdateDittaEsclusa
                && !updateDitteVincitriciEseguito
                && !"1".equals(dittaAmmessaGara)) {
              this.aggiornaDITGDittaEsclusaAltriLotti(codiceTornata,
                  codiceLotto, codiceDitta, ditta);
            }
          }
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore durante il controllo ditte escluse "
          + "e vincitrice in altri lotti della gara", null, e);
    }
  }

  /**
   * Metodo per determinare se, in una gara a lotti, almeno una ditta del lotto
   * attuale e' stata esclusa dagli altri lotti
   *
   * La verifica per escludere dal lotto corrente ditte escluse in altri lotti,
   * avviene prima dell'aggiornamento del lotto corrente per escludere ditte
   * vincitrici di altri lotti. Questo ha portato ad una query più complessa per
   * determinare appunto il numero di ditte escluse in altri lotti per motivi
   * diversi da 'Vincitrice di altri lotti'
   *
   * @param codiceTornata
   * @param codiceLotto
   * @return Ritorna true se almeno una ditta del lotto attuale è stata esclusa
   *         dagli lotti della gara per motivi diversi da 'Ditta vincitrice di
   *         altri lotti' (DITG.MOTIES = 98), false altrimenti
   *
   * @throws GestoreException
   */
  public boolean verificheEsclusioneDitteAltriLotti(String codiceTornata,
      String codiceLotto) throws GestoreException {
    boolean result = false;

    try {
      // Numero massimo delle ditte aggiudicatrici della tornata
      Long numeroMaxDitteAggiudicatrici = (Long) this.sqlManager.getObject(
          "select NGADIT from TORN where CODGAR = ? ",
          new Object[] { codiceTornata });
      if (numeroMaxDitteAggiudicatrici == null)
        numeroMaxDitteAggiudicatrici = new Long(1);

      Long numeroDitteEscluseDaLotti = (Long) this.sqlManager.getObject(
          "select count(*) from DITG A " + "where A.CODGAR5 = ? " // codice
                                                                  // tornata
              + "and A.NGARA5 <> ? " // codice lotto attuale
              + "and A.AMMGAR = '2' "
              + "and A.DITTAO in "
              + "(select DITTAO from DITG "
              + "where CODGAR5 = ? " // codice tornata
              + "and NGARA5  = ? " // codice lotto attuale
              + "and (AMMGAR = '0' or AMMGAR is null) "
              + "and STAGGI is null) "
              + "and not exists "
              + "(select DITTAO, count(*) from DITG B "
              + "where B.MOTIES = ? " // (MOTIES = 98) Motivazione 'Ditta
                                      // vincitrice di altro lotto'
              + "and A.CODGAR5 = B.CODGAR5 "
              + "and A.DITTAO = B.DITTAO "
              + "group by B.DITTAO "
              + "having count(*) = ? )" // numero massimo ditte aggiudicatrici
          , new Object[] { codiceTornata, codiceLotto, codiceTornata,
              codiceLotto, new Long(98), numeroMaxDitteAggiudicatrici });

      if (numeroDitteEscluseDaLotti != null
          && numeroDitteEscluseDaLotti.longValue() > 0) result = true;
    } catch (SQLException s) {
      throw new GestoreException(
          "Errore durante la verifica se almeno una ditta del lotto '".concat(
              codiceLotto).concat("' della gara a lotti '").concat(
              codiceTornata).concat(
              "' e' stata esclusa dagli altri lotti della gara."), null, s);
    }
    return result;
  }

  /**
   * Aggiornamento della DITG in caso la ditta sia stata esclusa da altri lotti
   *
   * @param codiceTornata
   * @param codiceLotto
   * @param codiceDitta
   * @param ditta
   *        Ditta del lotto corrente
   * @param updateVincitriciEseguito
   * @throws SQLException
   */
  private void aggiornaDITGDittaEsclusaAltriLotti(String codiceTornata,
      String codiceLotto, String codiceDitta, Vector<?> ditta)
      throws SQLException, GestoreException {

    try {
      Long numeroLottiDittaEsclusa = (Long) this.sqlManager.getObject(
          "select count(1) from DITG "
              + "where CODGAR5 = ? "
              + "and NGARA5 <> ? "
              + "and AMMGAR = '2' "
              + "and DITTAO = ? ", new Object[] { codiceTornata, codiceLotto,
              codiceDitta });

      if (numeroLottiDittaEsclusa != null
          && numeroLottiDittaEsclusa.longValue() > 0) {

        Object[] sqlParam = new Object[11];

        sqlParam[0] = new Long(1); // FASGAR
        sqlParam[1] = new Long(2); // AMMGAR
        sqlParam[2] = null; // RIBAUO
        sqlParam[3] = null; // MOTIES
        sqlParam[4] = null; // IMPOFF

        sqlParam[5] = null; // IMPOFF1
        sqlParam[6] = null; // PUNTEC
        sqlParam[7] = null; // PUNECO
        sqlParam[8] = codiceTornata; // CODGAR5
        sqlParam[9] = codiceDitta; // DITTAO
        sqlParam[10] = codiceLotto; // NGARA5
        this.sqlManager.update(
            "update DITG set FASGAR = ?, AMMGAR = ?, RIBAUO = ?, MOTIES = ?, "
                + "IMPOFF = ?, IMPOFF1 = ?, PUNTEC = ?, PUNECO = ? "
                + "where CODGAR5 = ? "
                + "and DITTAO = ? "
                + "and NGARA5 = ? ", sqlParam);
        this.aggiornaDITGAMMIS(codiceTornata, codiceLotto, codiceDitta,
            new Long(1), new Long(2), null, null, true, false, false);
      }
    } catch (SQLException s) {
      throw new GestoreException(
          "Errore durante l'esclusione della ditta (con codice = '".concat(
              codiceDitta).concat("') dal lotto '").concat(codiceLotto).concat(
              "' perche' esclusa da almeno un lotto, diverso da quello corrente,"
                  + " della tornata '").concat(codiceTornata).concat("'."),
          null, s);
    }
  }

  /**
   * Aggiornamento della DITG in caso la ditta sia vincitrice di altri lotti
   *
   * @param codiceTornata
   * @param codiceLotto
   * @param codiceDitta
   * @param numeroMaxDitteAggiudicatrici
   * @throws SQLException
   */
  private boolean aggiornaDITGDittaVincitriceAltriLotti(String codiceTornata,
      String codiceLotto, String codiceDitta) throws SQLException {

    boolean result = false;

    // Numero massimo delle ditte aggiudicatrici
    Long numeroMaxDitteAggiudicatrici = (Long) this.sqlManager.getObject(
        "select NGADIT from TORN where CODGAR = ? ",
        new Object[] { codiceTornata });
    if (numeroMaxDitteAggiudicatrici == null)
      numeroMaxDitteAggiudicatrici = new Long(1);

    // Controllo se la ditta ha vinto altri lotti della gara e
    // in caso positivo, aggiorno alcuni dati del lotto attuale
    Long numeroLottiAggiudicati = (Long) this.sqlManager.getObject(
        "select count(1) from DITG "
            + "where CODGAR5 = ? "
            + "and DITTAO = ? "
            + "and MOTIES = 98", new Object[] { codiceTornata, codiceDitta });

    if (numeroLottiAggiudicati != null
        && numeroLottiAggiudicati.longValue() >= numeroMaxDitteAggiudicatrici.longValue()) {
      this.sqlManager.update(
          "update DITG set AMMGAR = ?, FASGAR = ?, MOTIES = ?, RIBAUO = ?, "
              + "IMPOFF = ?, IMPOFF1 = ?, PUNTEC = ?, PUNECO = ? "
              + "where CODGAR5 = ? "
              + "and DITTAO = ? "
              + "and NGARA5 = ? ", new Object[] { "2", "1", new Long(99), null,
              null, null, null, null, codiceTornata, codiceDitta, codiceLotto });
      this.aggiornaDITGAMMIS(codiceTornata, codiceLotto, codiceDitta, new Long(
          1), new Long(2), new Long(99), null, true, true, false);
      result = true;
    }
    return result;
  }

  /**
   * Cancellazione di tutte le entita figlie di una tornata. L'occorrenza nella
   * tabella TORN viene cancellata del gestore dell'entita' TORN
   *
   * @param codiceTornata
   * @throws GestoreException
   */
  public void deleteTORN(String codiceTornata) throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("deleteTORN(" + codiceTornata + "): inizio metodo");

    try {
      // Determino i codici dei lotti della tornata in cancellazione
      //IMPORTANTE: poichè nella cancellazione di una gara viene effettuato
      //il decremento del numero di aggiudicazioni, e nel caso di gare ad offerta
      //unica questo decremento per i lotti ha necessità di usare i dati presenti
      //nella gara generica, è importante che nel ciclo di cancellazione dei lotti,
      //la gara fittizzia venga cancellata per ultima, quindi è stato inserito un
      //ordinamento decrescente nella selezione dei lotti
      List<?> listaLottiTornata = this.sqlManager.getListVector(
          "select ngara from gare where codgar1 = ? order by ngara desc",
          new Object[] { codiceTornata });

      if (listaLottiTornata != null && listaLottiTornata.size() > 0) {
        for (int i = 0; i < listaLottiTornata.size(); i++) {
          Vector<?> codiceLotto = (Vector<?>) listaLottiTornata.get(i);
          // Cancellazione dell'i-esimo lotto della tornata
          this.deleteGARE(((JdbcParametro) codiceLotto.get(0)).getStringValue());
        }
      }



      // Cancellazione delle occorrenze nella tabella GARE
      this.geneManager.deleteTabelle(new String[] { "GARE" }, "codgar1 = ? ",
          new Object[] { codiceTornata });

      //solo nel caso di Gare ad offerte distinte si deve effettuare la cancellazione di
      //WSFASCICOLO e WSDOCUMENTO con entità=TORN, altrimenti con entità=GARE
      Long genere = (Long) this.sqlManager.getObject("select genere from v_gare_torn where codgar=?", new Object[]{codiceTornata});

      if (genere != null && genere.longValue() == 1) {
        this.geneManager.deleteTabelle(new String[] { "WSFASCICOLO" }, "entita=? and key1 = ? ",
            new Object[] { "TORN",codiceTornata });
        this.geneManager.deleteTabelle(new String[] { "WSDOCUMENTO" }, "entita=? and key1 = ? ",
            new Object[] { "TORN", codiceTornata });
      }


      // Cancellazione delle tabelle figlie dell'entita' TORN
      this.geneManager.deleteTabelle(new String[] { "PUBBLI" }, "codgar9 = ? ",
          new Object[] { codiceTornata });
      this.geneManager.deleteTabelle(new String[] { "EDIT" }, "codgar4 = ? ",
          new Object[] { codiceTornata });

      // Eliminazione dei file allegati alla documgara relativi alla tornata
      this.deleteFileAllegatiDocumentazione("TORN", codiceTornata);

      // Cancellazione delle occorrenze nella tabella G_PERMESSI
      this.geneManager.deleteTabelle(new String[] { "G_PERMESSI" },
          "codgar = ? ", new Object[] { codiceTornata });

      // Cancellazione delle occorrenze nella tabella GARATT
      this.geneManager.deleteTabelle(new String[] { "GARATT" },
          "codgar = ? ", new Object[] { codiceTornata });

      // Cancellazione delle occorrenze nella tabella GAREIDS
      this.geneManager.deleteTabelle(new String[] { "GAREIDS" },
          "codgar = ? ", new Object[] { codiceTornata });

      // Cancellazione delle occorrenze nella tabella GARTECNI
      this.geneManager.deleteTabelle(new String[] { "GARTECNI" },
          "codgar = ? ", new Object[] { codiceTornata });

      // Cancellazione delle occorrenze nella tabella DOCUMGARA non associate
      //a lotti
      this.geneManager.deleteTabelle(new String[] { "DOCUMGARA" },
          "codgar = ? ", new Object[] { codiceTornata });

      // Cancellazione delle comunicazioni associate alla tornata
      this.deleteComunicazioni("TORN", codiceTornata);

      // Cancellazione delle conversazioni associate alla tornata
      this.deleteConversazioni("TORN", codiceTornata);

      // Cancellazione delle comunicazioni associate alla tornata
      this.deleteWsVigilanza(codiceTornata);

      //elimino le occorrenze di gardoc_wsdm e anche di gardoc_jobs
      Long id_archiviazione = (Long) this.sqlManager.getObject( "select id_archiviazione from gardoc_jobs where codgara = ?", new Object[] {codiceTornata});
      if (id_archiviazione!= null) {
        this.geneManager.deleteTabelle(new String[] { "GARDOC_WSDM" },
            "id_archiviazione = ?", new Object[] { id_archiviazione });
        this.geneManager.deleteTabelle(new String[] { "GARDOC_JOBS" },
            "id_archiviazione = ?", new Object[] { id_archiviazione });

      }
      
    //Rimuovo le occorrenze da GARERDA contattando il WS 
    this.gestioneProgrammazioneManager.scollegaRdaGara(codiceTornata,null);
     


    } catch (SQLException e) {
      throw new GestoreException("Errore durante la cancellazione della gara "
          + (codiceTornata.startsWith("$") ? "a lotto unico" : "a lotti ")
          + "'".concat(codiceTornata).concat("' e delle sue entita' figlie"),
          null, e);
    }

    if (logger.isDebugEnabled())
      logger.debug("deleteTORN(" + codiceTornata + "): fine metodo");
  }

  /**
   * Cancellazione di tutte le entita' figlie di un lotto di gara. Se si
   * cancella un lotto di gara, l'occorrenza in GARE viene cancellata dal
   * gestore dell'entita', mentre se si cancella una gara a lotto unico,
   * l'occorrenza in GARE viene cancellata dal metodo PgManager.deleteTORN
   * IMPORTANTE: nel caso il metodo venga richiamato dalla cancellazione di tutti i lotti
   *             di una gara a plico unico(genere=3), è importante che l'occorrenza fittizia
   *             (gare.codgar1=gare.ngara) venga cancellata per ultima, poichè per le operazioni
   *             di calcolo per i lotti servono dei dati che si trovano solo sull'occorrenza fittizia
   *
   * @param codiceGara
   * @throws GestoreException
   */
  public void deleteGARE(String codiceGara) throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("deleteGARE(" + codiceGara + "): inizio metodo");

    // Eliminazione dei file allegati alla documgara
    this.deleteFileAllegatiDocumentazione("GARE", codiceGara);

    // Decrementazione del numero inviti (ISCRIZCAT.INVREA e COMMUROLI.INVITI)
    this.gestioneNumContatoriIscrizcatCancellazioneGara(codiceGara);

    // eliminazione dei documenti associati ad imprdocg
    String delete = "delete from w_docdig where IDPRG in(select IMPRDOCG.IDPRG from IMPRDOCG where NGARA = ?) and IDDOCDIG in (select IMPRDOCG.IDDOCDG from IMPRDOCG where NGARA = ?)";
    try {
      this.sqlManager.update(delete, new Object[] { codiceGara, codiceGara, });
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'eliminazione dele righe delle tabella IMPRDOCG ",
          null, e);
    }

    this.geneManager.deleteTabelle(new String[] { "PERP", "GCAP", "GOEV",
        "PUBG", "CATG", "GARCPV", "GARSED", "RAGDET", "GCAP_EST", "GCAP_SAN",
        "COMMVERB", "GARSEDSOSP", "GAREALBO", "DOCUMGARA", "IMPRDOCG",
        "ISCRIZCAT", "GARESTATI", "DITGAMMIS", "DITGSTATI", "GARASS", "GARE1",
        "GARECONT", "GARSEDPRES", "MECATALOGO", "GAREATTI", "GAREIVA",
        "PUBBTERM", "GARCOMPREQ", "CHIAVIBUSTE", "DITGAVVAL", "GARATTIAGG",
        "GARALTSOG","DITGAQ", "ISCRIZUFF", "GARUUID"}, "ngara = ? ",
        new Object[] { codiceGara });
    this.geneManager.deleteTabelle(new String[] { "GFOF" }, "ngara2 = ? ",
        new Object[] { codiceGara });
    this.geneManager.deleteTabelle(new String[] { "OPES" }, "ngara3 = ? ",
        new Object[] { codiceGara });
    this.geneManager.deleteTabelle(new String[] { "OPSU" }, "ngara4 = ? ",
        new Object[] { codiceGara });
    this.geneManager.deleteTabelle(new String[] { "DPUN", "DPUN_CG", "DPRE", "GARSTR",
        "DPRE_SAN" }, "ngara = ? ", new Object[] { codiceGara });
    this.geneManager.deleteTabelle(new String[] { "OPSD" }, "ngara6 = ? ",
        new Object[] { codiceGara });
    this.geneManager.deleteTabelle(new String[] { "DITG" }, "ngara5 = ? ",
        new Object[] { codiceGara });

    this.geneManager.deleteTabelle(new String[] { "WSFASCICOLO" }, "entita=? and key1 = ? ",
        new Object[] { "GARE",codiceGara });
    this.geneManager.deleteTabelle(new String[] { "WSDOCUMENTO" }, "entita=? and key1 = ? ",
        new Object[] { "GARE", codiceGara });


    // Eliminazione dell'entità ISCRIZCLASSI
    // Non viene eseguita con il deleteTabelle perchè altrimenti verrebbe
    // richiamata
    // la cancellazione degli oggetti associati che da errore perchè l'entità ha
    // più
    // chiavi di quelle gestite in C0OGGASS.
    delete = "delete from iscrizclassi where ngara = ?";
    try {
      this.sqlManager.update(delete, new Object[] { codiceGara, });
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'eliminazione dele righe delle tabella ISCRIZCLASSI ",
          null, e);
    }

    // Eliminazione dell'entità QFORM
    delete = "delete from QFORM where entita = 'GARE' and key1=?";
    try {
      this.sqlManager.update(delete, new Object[] { codiceGara, });
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'eliminazione dele righe delle tabella ISCRIZCLASSI ",
          null, e);
    }

    // Cancellazione delle comunicazioni associate alla gara
    this.deleteComunicazioni("GARE", codiceGara);

    // Cancellazione delle conversazioni associate alla gara
    this.deleteConversazioni("GARE", codiceGara);

    // Ripristina a 'Progettazione' la fase nell'appalto associato alla gara/lotto
    String select="select clavor, numera from gare where ngara=?";
    String clavor=null;
    Long numera=null;
    try {
      Vector<?> datiAppalto = this.sqlManager.getVector(select,new Object[]{ codiceGara});
      clavor = (String)((JdbcParametro) datiAppalto.get(0)).getValue();
      numera = (Long)((JdbcParametro) datiAppalto.get(1)).getValue();
      if (numera != null) {
        //Aggiorna il campo FASEAPPALTO solo se è non ci sono altre gare che fanno riferimento allo stesso appalto (caso di lotto di gara con plico unico)
        Long numeroLottiAppalto= (Long)this.sqlManager.getObject("select count(ngara) from gare where clavor=? and numera=? and esineg is null", new Object[]{clavor,numera});
        if (numeroLottiAppalto == null || (numeroLottiAppalto != null && numeroLottiAppalto.longValue() == 1)) {
          this.sqlManager.update("update appa set faseappalto=? where codlav=? and nappal=?", new Object[]{"P",clavor,numera});
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'aggiornamento della fase dell'appalto associato alla gara",
          null, e);
    }

    //sbiancamento dello stato delle ricerche di mercato
    try {
      String seguen=(String)this.sqlManager.getObject("select seguen from gare where ngara=? ", new Object[] {codiceGara});
      if(seguen!=null && !"".equals(seguen))
        this.sqlManager.update("update gare set isriconclusa=null where ngara=?", new Object[]{seguen});
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'aggiornamento dello stato delle ricerche di mercato",
          null, e);
    }
    
    //Rimuovo le occorrenze da GARERDA contattando il WS
    try {
      String  codgar = (String) this.sqlManager.getObject("select CODGAR1 from GARE where NGARA = ?",new Object[] {codiceGara});
      this.gestioneProgrammazioneManager.scollegaRdaLotto(codgar, codiceGara,null);
    } catch (SQLException e) {
      throw new GestoreException("Errore nel recupero del codice della gara",null, e);
    }

    if (logger.isDebugEnabled())
      logger.debug("deleteGARE(" + codiceGara + "): fine metodo");
  }

  /**
   * Metodo per la cancellazione delle comunicazione alle ditte concorrenti
   *
   * @param coment
   *        - Entità associata (TORN o GARE)
   * @param comkey1
   *        - Valore del campo chiave (CODGAR.TORN è NGARA.GARE)
   * @throws GestoreException
   */
  public void deleteComunicazioni(String coment, String comkey1)
      throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("deleteComunicazioni("
          + coment
          + ","
          + comkey1
          + "): inizio metodo");

    try {
      List<?> datiW_INVCOM = this.sqlManager.getListVector(
          "select idprg, idcom from w_invcom where coment = ? and comkey1 = ?",
          new Object[] { coment, comkey1 });

      if (datiW_INVCOM != null && datiW_INVCOM.size() > 0) {
        for (int i = 0; i < datiW_INVCOM.size(); i++) {
          String idprg = (String) SqlManager.getValueFromVectorParam(
              datiW_INVCOM.get(i), 0).getValue();
          Long idcom = (Long) SqlManager.getValueFromVectorParam(
              datiW_INVCOM.get(i), 1).getValue();

          this.geneManager.deleteTabelle(new String[] { "W_INVCOMDES" },
              "idprg = ? and idcom = ?", new Object[] { idprg, idcom });

          //Eliminazione di WSALLEGATI associati a W_DOCDIG
          List<?> datiW_DOCDIG = this.sqlManager.getListVector(
              "select idprg, iddocdig from w_docdig where digent = ? and digkey1 = ? and digkey2 = ?",
              new Object[] { "W_INVCOM", idprg , idcom.toString()});
          if (datiW_DOCDIG != null && datiW_DOCDIG.size() > 0) {
            for (int j = 0; j < datiW_DOCDIG.size(); j++) {
              String idprgDoc = (String) SqlManager.getValueFromVectorParam(
                  datiW_DOCDIG.get(j), 0).getValue();
              Long idcomDoc = (Long) SqlManager.getValueFromVectorParam(
                  datiW_DOCDIG.get(j), 1).getValue();
              this.geneManager.deleteTabelle(new String[] { "WSALLEGATI" },
                  "entita = ? and key1 = ? and key2 = ?", new Object[] { "W_DOCDIG", idprgDoc, idcomDoc.toString()});
            }
          }

          this.geneManager.deleteTabelle(new String[] { "W_DOCDIG" },
              "digkey1 = ? and digkey2 = ? and digent = 'W_INVCOM'",
              new Object[] { idprg, idcom.toString() });

          this.geneManager.deleteTabelle(new String[] { "WSALLEGATI" },
              "entita = ? and key1 = ? and key2 = ?", new Object[] { "W_INVCOM", "PG", idcom.toString()});
        }

        this.geneManager.deleteTabelle(new String[] { "W_INVCOM" },
            "coment = ? and comkey1 = ?", new Object[] { coment, comkey1 });


      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante la cancellazione della comunicazione", null, e);
    }

    if (logger.isDebugEnabled())
      logger.debug("deleteComunicazioni("
          + coment
          + ","
          + comkey1
          + "): fine metodo");
  }


  /**
   * Metodo per la cancellazione delle comunicazione alle ditte concorrenti
   *
   * @param discent
   *        - Entità associata (TORN o GARE)
   * @param disckey1
   *        - Valore del campo chiave (CODGAR.TORN è NGARA.GARE)
   * @throws GestoreException
   */
  public void deleteConversazioni(String discent, String disckey1)
      throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("deleteConversazioni("
          + discent
          + ","
          + disckey1
          + "): inizio metodo");

    try {
      List<?> datiW_DISCUSS_P = this.sqlManager.getListVector(
          "select discid_p from w_discuss_p where discent = ? and disckey1 = ?",
          new Object[] { discent, disckey1 });

      if (datiW_DISCUSS_P != null && datiW_DISCUSS_P.size() > 0) {
        for (int i = 0; i < datiW_DISCUSS_P.size(); i++) {
          Long discid_p = (Long) SqlManager.getValueFromVectorParam(
              datiW_DISCUSS_P.get(i), 0).getValue();

          this.geneManager.deleteTabelle(new String[] { "W_DISCUSS", "W_DISCREAD", "W_DISCDEST", "W_DISCALL" },
              "discid_p = ?", new Object[] {discid_p});
        }

        this.geneManager.deleteTabelle(new String[] { "W_DISCUSS_P" },
            "discent = ? and disckey1 = ?", new Object[] { discent, disckey1 });


      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante la cancellazione della comunicazione", null, e);
    }

    if (logger.isDebugEnabled())
      logger.debug("deleteConversazioni("
          + discent
          + ","
          + discent
          + "): fine metodo");
  }

  /**
   * Metodo per la cancellazione dei file allegati alla documentzione di gara
   *
   * @param entita
   *        - Entità (TORN o GARE)
   * @param chiave
   *        - Valore del campo chiave (CODGAR.TORN è NGARA.GARE)
   * @throws GestoreException
   */
  public void deleteFileAllegatiDocumentazione(String entita, String chiave)
      throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("deleteFileAllegatiDocumentazione("
          + entita
          + ","
          + chiave
          + "): inizio metodo");

    // In W_DOCDIG le righe sono associate alla documgara tramite il codgar,
    // quindi nel caso di gare ho a disposizione ngara e devo quindi
    // recuperare il campo codgar1
    List<?> datiDOCUMGARA = null;

    try {
      if ("TORN".equals(entita))
        datiDOCUMGARA = this.sqlManager.getListVector(
            "select idprg, iddocdg from documgara where codgar = ? ",
            new Object[] { chiave });
      else {
        String codgar1 = (String) this.sqlManager.getObject(
            "select codgar1 from gare where ngara = ?", new Object[] { chiave });
        datiDOCUMGARA = this.sqlManager.getListVector(
            "select idprg, iddocdg from documgara where codgar = ? and ngara=?",
            new Object[] { codgar1, chiave });

      }

      if (datiDOCUMGARA != null && datiDOCUMGARA.size() > 0) {
        for (int i = 0; i < datiDOCUMGARA.size(); i++) {
          String idprg = (String) SqlManager.getValueFromVectorParam(
              datiDOCUMGARA.get(i), 0).getValue();
          Long iddocdg = (Long) SqlManager.getValueFromVectorParam(
              datiDOCUMGARA.get(i), 1).getValue();

          this.geneManager.deleteTabelle(new String[] { "W_DOCDIG" },
              "idprg = ? and iddocdig = ? ", new Object[] { idprg, iddocdg });

        }

      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante la cancellazione della comunicazione", null, e);
    }

    if (logger.isDebugEnabled())
      logger.debug("deleteFileAllegatiDocumentazione("
          + entita
          + ","
          + chiave
          + "): fine metodo");
  }

  /**
   * Metodo per la copia di una gara a lotti o una gara a lotto unico
   *
   * @param status
   * @param codiceGaraSorgente
   *        Codice gara sorgente
   * @param codiceGaraDestinazione
   *        Codice gara destinazione
   * @param copiaLotti
   *        Flag indicante se copiare i lotti o meno
   * @param prefissoCodiceLotti
   *        Prefisso del codice dei nuovi lotti
   * @param copiaDitte
   *        Flag indicante se copiare o meno le ditte
   * @param copiaOfferte
   *        Flag indicante se copiare le offerte delle ditte
   * @param copiaScadenzario
   *        Flag indicante se copiare lo scadenzario
   * @param copiaTermini
   *        Flag indicante se copiare i termini dell'offerta
   * @param request
   *        Oggetto request
   * @throws GestoreException
   */
  public void copiaTORN(TransactionStatus status, String codiceGaraSorgente,
      String codiceGaraDestinazione, boolean copiaLotti,
      String prefissoCodiceLotti, boolean copiaDitte, boolean copiaOfferte,
      boolean copiaScadenzario, boolean isCodificaAutomatica,
      boolean copiaDocumentazione, HttpServletRequest request,boolean copiaTermini) throws GestoreException {

    DataColumnContainer campiDaCopiare = null;
    List<?> listaOccorrenzeDaCopiare = null;

    if (prefissoCodiceLotti == null) prefissoCodiceLotti = "";

    try {
      if (copiaDitte) {
        // ////////////////////////////////////////
        // Copia EDIT
        // ////////////////////////////////////////
        if (logger.isDebugEnabled())
          logger.debug("copiaTORN: inizio copia occorrenze di EDIT");

        listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
            "select * from EDIT where CODGAR4 = ? ",
            new Object[] { codiceGaraSorgente });

        if (listaOccorrenzeDaCopiare != null
            && listaOccorrenzeDaCopiare.size() > 0) {
          campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
              "EDIT", "select * from EDIT", new Object[] {});
          for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {
            campiDaCopiare.setValoriFromMap(
                (HashMap<?,?>) listaOccorrenzeDaCopiare.get(row), true);
            campiDaCopiare.getColumn("EDIT.CODGAR4").setChiave(true);
            campiDaCopiare.getColumn("EDIT.CODIME").setChiave(true);
            campiDaCopiare.setValue("EDIT.CODGAR4", codiceGaraDestinazione);

            campiDaCopiare.insert("EDIT", this.geneManager.getSql());
          }
        }
        if (logger.isDebugEnabled())
          logger.debug("copiaTORN: fine copia occorrenze di EDIT");

      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nella copia di EDIT e TORN!",
          "copiaTORN", e);
    }

    // Copia delle entita' figlie di TORN
    this.copiaTORNcomune(codiceGaraSorgente, codiceGaraDestinazione, copiaTermini, request, copiaDitte);

    // Array di nomi delle entita' copiate in questo metodo
    String[] entitaCopiate = new String[] { "EDIT" };
    // Array di nomi dei campi chiave delle entita' copiate in questo metodo
    String[] campiChiaveEntitaCopiate = new String[] { "CODGAR4" };
    // Eseguo la copia delle occorrenze del generatore attributi delle due
    // entita' copiate
    for (int i = 0; i < entitaCopiate.length; i++) {
      this.geneManager.copiaOccorrenzeGeneratoreAttributi(entitaCopiate[i],
          campiChiaveEntitaCopiate[i] + " = ?",
          new Object[] { codiceGaraSorgente },
          new String[] { campiChiaveEntitaCopiate[i] },
          new Object[] { codiceGaraDestinazione }, false);
      // Eseguo la copia delle occorrenze di note avvisi delle entita' copiate
      this.geneManager.copiaOccorrenzeNoteAvvisi(entitaCopiate[i],
          campiChiaveEntitaCopiate[i] + " = ?",
          new Object[] { codiceGaraSorgente },
          new String[] { campiChiaveEntitaCopiate[i] },
          new Object[] { codiceGaraDestinazione }, false);
    }

    if (copiaLotti) {
      // Copia dei lotti di gara
      try {
        List<?> listaLottiDiGara = this.geneManager.getSql().getListHashMap(
            "select NGARA,GENERE from GARE "
                + "where CODGAR1 = ? "
                + "order by NGARA asc", new Object[] { codiceGaraSorgente });
        if (listaLottiDiGara != null && listaLottiDiGara.size() > 0) {
          int progressivoLottiCopiati = 0;
          String nGaraSorgente = null;
          String nGaraDestinazione = null;

          for (int i = 0; i < listaLottiDiGara.size(); i++) {
            nGaraSorgente = ((JdbcParametro) ((HashMap<?,?>) listaLottiDiGara.get(i)).get("NGARA")).getStringValue();
            String genere = ((JdbcParametro) ((HashMap<?,?>) listaLottiDiGara.get(i)).get("GENERE")).getStringValue();
            if (nGaraSorgente != null && nGaraSorgente.length() > 0) {
              boolean esisteLotto = true;

              if (genere != null && "3".equals(genere)) {
                // per le gare divise a lotti con offerta unica, la gara
                // complementare(genere=3)
                // ngara = codgar1
                nGaraDestinazione = codiceGaraDestinazione;
                Long count = (Long) this.geneManager.getSql().getObject(
                    "select count(NGARA) from GARE where NGARA = ?",
                    new Object[] { nGaraDestinazione });
                if (count.longValue() == 0)
                  esisteLotto = false;
                else {
                  throw new GestoreException("Errore nella copia dei lotti di "
                      + "gara, esiste già la gara "
                      + nGaraDestinazione, "copiaTORN");
                }
              } else if (isCodificaAutomatica) {
                nGaraDestinazione = this.getNumeroGaraCodificaAutomatica(
                    codiceGaraDestinazione, null,"GARE","NGARA");
                esisteLotto = false;
              } else {
                do {
                  progressivoLottiCopiati++;
                  nGaraDestinazione = prefissoCodiceLotti.concat(UtilityStringhe.fillLeft(
                      "" + progressivoLottiCopiati, '0', 3));
                  // Cerco se il valore calcolato di nGaraDestinazione e' usato
                  // in GARE come campo NGARA e in TORN come campo CODGAR.
                  // Questo per mantenere univoci i codici delle gare divise in
                  // lotti dai codici delle gare a lotto unico
                  Long count = (Long) this.geneManager.getSql().getObject(
                      "select count(NGARA) from GARE where NGARA = ?",
                      new Object[] { nGaraDestinazione });
                  Long count1 = (Long) this.geneManager.getSql().getObject(
                      "select count(CODGAR) from TORN where CODGAR = ?",
                      new Object[] { nGaraDestinazione });
                  if (count.longValue() == 0 && count1.longValue() == 0)
                    esisteLotto = false;
                } while (progressivoLottiCopiati < 1000 && esisteLotto);
              }
              if (!esisteLotto) {
                // Flag per indicare che si sta copiando una gara dal metodo
                // PgManager.copiaTORN
                boolean copiaGareDaTorn = true;

                this.copiaGARE(status, nGaraSorgente, codiceGaraSorgente,
                    nGaraDestinazione, codiceGaraDestinazione, copiaDitte,
                    copiaOfferte, copiaGareDaTorn, request, true,
                    copiaScadenzario,copiaTermini);

                // Copia della documentazione di gara
                if (copiaDocumentazione) {
                  this.copiaDocumentazione(status, nGaraSorgente,
                      codiceGaraSorgente, nGaraDestinazione,
                      codiceGaraDestinazione, true, "TORN");
                  if (copiaOfferte)
                    this.copiaIMPRDOCG(status, nGaraSorgente, codiceGaraSorgente,
                        nGaraDestinazione, codiceGaraDestinazione);
                }

              } else {
                throw new GestoreException("Errore nella copia dei lotti di "
                    + "gara per raggiunto valore massimo del progressivo "
                    + "con cui si completa il prefisso dei lotti"
                    + codiceGaraSorgente, "copiaTORN");
              }
            }
          }
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nella copia dei lotti di gara "
            + codiceGaraSorgente, "copiaTORN", e);
      }
      // Copia della documentazione di gara
      if (copiaDocumentazione)
        this.copiaDocumentazione(status, "", codiceGaraSorgente, "",
          codiceGaraDestinazione, false, "TORN");
    }

    // Copia dello scadenzario
    if (copiaScadenzario) {
      Object chiveFrom[] = new Object[] { codiceGaraSorgente };
      Object chiveTo[] = new Object[] { codiceGaraDestinazione };
      try {
        this.scadenzariManager.insertClonazioneAttivitaScadenzario(codapp,
            "TORN", chiveFrom, chiveTo, false);
        this.scadenzariManager.updateDateScadenzarioEntita("TORN", chiveTo,
            codapp, false, null);
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella copia dello Scadenzario della gara "
                + codiceGaraSorgente, "copiaTORN", e);
      }
    }
  }

  /**
   * Funzione che esegue la copia di un lotto di gara
   *
   * @param status
   *        Transaction status
   * @param nGaraSorgente
   *        Numero della gara sorgente
   * @param codiceGaraSorgente
   *        Codice della gara sorgente
   * @param nGaraDestinazione
   *        Numero gara destinazione
   * @param copiaDitte
   *        Flag indicante se copiare o meno le ditte
   * @param copiaOfferte
   *        Flag indicante se copiare le offerte delle ditte
   * @param copiaGareDaTorn
   *        Flag indicante se la copia dei lotti e' stata richiamata dal metodo
   *        copiaTORN o meno
   * @param request
   *        Oggetto request
   * @param copiaComeLotto
   *        Flag per indicare che la gara è copiata come lotto di gara
   * @param copiaScadenzario
   *        Flag per indicare che si deve copiare lo scadenzario
   * @param copiaTermini
   *        Flag inidicante che si devono copiare i termini
   * @throws GestoreException
   */
  public void copiaGARE(TransactionStatus status, String nGaraSorgente,
      String codiceGaraSorgente, String nGaraDestinazione,
      String codiceGaraDestinazione, boolean copiaDitte, boolean copiaOfferte,
      boolean copiaGareDaTorn, HttpServletRequest request,
      boolean copiaComeLotto, boolean copiaScadenzario, boolean copiaTermini) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("copiaGARE: inizio metodo");

    DataColumnContainer campiDaCopiare = null;
    List<?> listaOccorenzeDaCopiare = null;

    // Inizializzazione codice gara sorgente
    if (codiceGaraSorgente == null) {
      try {
        List<?> listaGare = this.geneManager.getSql().getListVector(
            "select CODGAR1 from GARE where NGARA = ?",
            new Object[] { nGaraSorgente });
        codiceGaraSorgente = ((JdbcParametro) ((Vector<?>) listaGare.get(0)).get(0)).stringValue();
      } catch (SQLException e1) {
        throw new GestoreException(
            "Errore nella selezione del campo CODGAR1 della gara sorgente !",
            "copiaGARE", e1);
      }
    }

    if (codiceGaraDestinazione == null
        || (codiceGaraDestinazione != null && codiceGaraDestinazione.length() == 0))
      codiceGaraDestinazione = "$".concat(nGaraDestinazione);

    if (codiceGaraDestinazione.startsWith("$")) {
      // Se entra dentro il presente IF il metodo copiaGARE non e' stato
      // richiamato dal metodo copiaTORN
      this.copiaTORNcomune(codiceGaraSorgente, codiceGaraDestinazione, copiaTermini, request,copiaDitte);

      /*
      if (!codiceGaraSorgente.startsWith("$")) {
        // L'occorrenza TORN fittizia destinazione appena creata dovrà avere gli
        // stessi valori di DTEPAR e OTEPAR della occorrenza in GARE sorgente
        try {
          Long tipoGara = (Long) this.geneManager.getSql().getObject(
              "select TIPGEN from TORN where CODGAR = ? ",
              new String[] { codiceGaraSorgente });
          if (tipoGara == null) tipoGara = new Long(1);

          if (tipoGara.intValue() == 1) {
            campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
                "TORN",
                "select CODGAR, DTEOFF, OTEOFF, DESOFF, OESOFF, DINDOC, "
                    + "DESDOC, DTEPAR, OTEPAR, ISTAUT "
                    + "from TORN where CODGAR = ?",
                new String[] { codiceGaraDestinazione });
            listaOccorenzeDaCopiare = this.geneManager.getSql().getListHashMap(
                "select CODGAR1, DTEOFF, OTEOFF, DESOFF, OESOFF, DINDOC, "
                    + "DESDOC, DTEPARG, OTEPARG, ISTAUT "
                    + "from GARE "
                    + "where NGARA = ? ", new String[] { nGaraSorgente });
          } else {
            campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
                "TORN",
                "select CODGAR, DTEOFF, OTEOFF, DESOFF, OESOFF, DINDOC, "
                    + "DESDOC, DTEPAR, OTEPAR, CORGAR, ISTAUT "
                    + "from TORN where CODGAR = ?",
                new String[] { codiceGaraDestinazione });
            listaOccorenzeDaCopiare = this.geneManager.getSql().getListHashMap(
                "select CODGAR1, DTEOFF, OTEOFF, DESOFF, OESOFF, DINDOC, "
                    + "DESDOC, DTEPARG, OTEPARG, NUMERA, CORGAR1 "
                    + "from GARE "
                    + "where NGARA = ? ", new String[] { nGaraSorgente });
          }

          if (listaOccorenzeDaCopiare != null
              && listaOccorenzeDaCopiare.size() > 0) {
            for (int row = 0; row < listaOccorenzeDaCopiare.size(); row++) {
              HashMap hm = (HashMap) listaOccorenzeDaCopiare.get(row);
              // Nella hash cambio il nome al campo DTEPARG in DTEPAR
              hm.put("TORN.DTEPAR", hm.get("DTEPARG"));
              hm.remove("DTEPAGR");
              // Nella hash cambio il nome al campo OTEPARG in OTEPAR
              hm.put("TORN.OTEPAR", hm.get("OTEPARG"));
              hm.remove("OTEPAGR");
              // Nella hash cambio il nome al campo CODGAR1 in CODGAR
              hm.put("TORN.CODGAR", hm.get("GARE.CODGAR1"));
              hm.remove("CODGAR1");

              if (tipoGara.intValue() != 1) {
                // Nella hash cambio il nome al campo CORGAR1 in CORGAR
                hm.put("TORN.CORGAR", hm.get("GARE.CORGAR1"));
                hm.remove("GARE.CORGAR1");
              }

              campiDaCopiare.setValoriFromMap(hm, false);
              campiDaCopiare.getColumn("TORN.CODGAR").setChiave(true);
              campiDaCopiare.setValue("TORN.CODGAR", codiceGaraDestinazione);

              // Nella copia di un lotto di gara come gara a lotto singolo
              // bisogna sbiancare il campo TORN.ISTAUT
              campiDaCopiare.setValue("TORN.ISTAUT", null);

              campiDaCopiare.update("TORN", this.geneManager.getSql());
            }
          }
        } catch (SQLException e1) {
          throw new GestoreException(
              "Errore nella selezione del campo TORN.TIPGEN della gara sorgente !",
              "copiaGARE", e1);
        }
      }
      */
    }

    Long genere;
    try {
      genere = (Long) this.geneManager.getSql().getObject("select genere from v_gare_torn where codice=?",
          new Object[] { nGaraSorgente });
    } catch (SQLException e1) {
      throw new GestoreException(
          "Errore nella lettura del genere della gara sorgente!",
          "copiaGARE", e1);
    }


    Boolean isPadreRicercaMercatoNegoziata = false;
    try {
    	Long iterRicMeNeg = (Long) this.geneManager.getSql().getObject("select t.iterga"
				+ " from torn t join gare g1 on t.codgar=g1.codgar1"
				+ " join gare g2 on g1.ngara=g2.seguen and g2.ngara=?", new Object[] { nGaraSorgente });
    	if(iterRicMeNeg!=null && Long.valueOf(8).equals(iterRicMeNeg)) {
    		isPadreRicercaMercatoNegoziata = true;
    	}
	} catch (SQLException sqle) {
	      throw new GestoreException(
	              "Errore nella lettura dell'iter di gara per le ricerche di mercato!",
	              "copiaGARE", sqle);
	}


    // Inizio copia delle entita' figlie di GARE
    String querySql = "select * from ENTITA where CAMPOCHIAVE = ? ";

    String entitaCopia[] = { "OPES", "PUBG", "CATG", "GARSTR",
        "GCAP", "GOEV", "GARE", "GARCPV", "GCAP_EST", "GCAP_SAN",
        "GARASS", "GARE1", "GARSEDPRES", "GARECONT", "G1CRIDEF" };

    List<String[]> listaCampiChiaveEntitaCopia = new ArrayList<String[]>();
    listaCampiChiaveEntitaCopia.add(0, new String[] { "NGARA3", "NOPEGA" });
    listaCampiChiaveEntitaCopia.add(1, new String[] { "NGARA", "NPUBG" });
    listaCampiChiaveEntitaCopia.add(2, new String[] { "NGARA", "NCATG" });
    listaCampiChiaveEntitaCopia.add(3, new String[] { "NGARA", "NUMSTR" });
    listaCampiChiaveEntitaCopia.add(4, new String[] { "NGARA", "CONTAF" });
    listaCampiChiaveEntitaCopia.add(5, new String[] { "NGARA", "NECVAN" });
    listaCampiChiaveEntitaCopia.add(6, new String[] { "NGARA" });
    listaCampiChiaveEntitaCopia.add(7, new String[] { "NGARA", "NUMCPV" });
    listaCampiChiaveEntitaCopia.add(8, new String[] { "NGARA", "CONTAF" });
    listaCampiChiaveEntitaCopia.add(9, new String[] { "NGARA", "CONTAF" });
    listaCampiChiaveEntitaCopia.add(10, new String[] { "NGARA", "NUMASS" });
    listaCampiChiaveEntitaCopia.add(11, new String[] { "NGARA", "CODGAR1" });
    listaCampiChiaveEntitaCopia.add(12, new String[] { "NGARA", "NUMSED" });
    listaCampiChiaveEntitaCopia.add(13, new String[] { "NGARA", "NCONT" });
    listaCampiChiaveEntitaCopia.add(14, new String[] { "NGARA", "NECVAN" });

    for (int i = 0; i < entitaCopia.length; i++) {
      Long idOriginale=null;
      long idCridef =0;
      // Per GCAP, se si effettua una copia senza ditte oppure con ditte,
      // ma senza offerte si devono escludere le occorrenze
      // con DITTAO.GCAP non nullo
      // Per GCAP_EST si devono seguire le stesse regole di GCAP
      // per GCAP_SAN si devono seguire le stesse regole di GCAP
      if (i == 4 && (!copiaDitte || !copiaOfferte))
        querySql = "select * from ENTITA where CAMPOCHIAVE = ? and DITTAO IS NULL";
      else if ((i == 8 || i == 9) && (!copiaDitte || !copiaOfferte))
        querySql = "select * from ENTITA where CAMPOCHIAVE = ? and CONTAF in (select contaf from gcap where ngara=? and DITTAO IS NULL)";
      else if (i == 1) {
        if (copiaTermini)
          querySql = "select * from ENTITA where CAMPOCHIAVE = ? and ((tippubg != 12 and tippubg != 14) or tippubg is null)";
        else
          continue; //Se non c'è l'opzione copia Termini attivata, allora si salta la copia di PUBG
      }else if (i == 13 && genere != null
          && genere.longValue() == 3 ) {
        //Nel caso di Offerta unica non si deve effettuare la copia di GARECONT
        continue;
      }
      else
        querySql = "select * from ENTITA where CAMPOCHIAVE = ? ";

      //Per l'entità G1CRIVAL inserisco l'ordinamento rispetto al campo chiave
      if (i == 14)
        querySql+=" order by id";

      if (this.geneManager.getSql().isTable(entitaCopia[i])
          && this.geneManager.countOccorrenze(
              entitaCopia[i],
              listaCampiChiaveEntitaCopia.get(i)[0].concat(" = ?"),
              new Object[] { nGaraSorgente }) > 0) {

        String sql = querySql.replaceFirst("ENTITA", entitaCopia[i]).replaceFirst(
            "CAMPOCHIAVE", listaCampiChiaveEntitaCopia.get(i)[0]);

        try {
          if ((i == 8 || i == 9) && (!copiaDitte || !copiaOfferte)) {
            // per GCAP_SAN si devono seguire le stesse regole di GCAP_EST
            // per GCAP_EST si devono specificare 2 parametri per la
            // select se si effettua una copia
            // senza ditte oppure con ditte,ma senza offerte
            listaOccorenzeDaCopiare = this.geneManager.getSql().getListHashMap(
                sql, new Object[] { nGaraSorgente, nGaraSorgente });
          } else {
            listaOccorenzeDaCopiare = this.geneManager.getSql().getListHashMap(
                sql, new Object[] { nGaraSorgente });
          }

          // Se ci sono occorenze allora eseguo la copia
          if (listaOccorenzeDaCopiare != null
              && listaOccorenzeDaCopiare.size() > 0) {
            for (int row = 0; row < listaOccorenzeDaCopiare.size(); row++) {
              if ((i == 8 || i == 9) && (!copiaDitte || !copiaOfferte)) {
                // per GCAP_SAN si devono seguire le stesse regole di GCAP_EST
                // per GCAP_EST si devono specificare 2
                // parametri per la select se si effettua una
                // copia
                // senza ditte oppure con ditte,ma senza offerte
                campiDaCopiare = new DataColumnContainer(
                    this.geneManager.getSql(), entitaCopia[i], sql,
                    new Object[] { nGaraSorgente, nGaraSorgente });
              } else {
                campiDaCopiare = new DataColumnContainer(
                    this.geneManager.getSql(), entitaCopia[i], sql,
                    new Object[] { nGaraSorgente });
              }

              if ("GCAP".equalsIgnoreCase(entitaCopia[i])) {
                // Rimozione dei campi da non copiare
                // che riferiscono del collegamento alle rda (integrazione WSERP)
                String campiDaNonCopiare[] = new String[]{ "GCAP.CODCARR","GCAP.CODRDA", "GCAP.POSRDA", "GCAP.ISPRODNEG"};
                campiDaCopiare.removeColumns(campiDaNonCopiare);
              }

              campiDaCopiare.setValoriFromMap(
                  (HashMap<?,?>) listaOccorenzeDaCopiare.get(row), true);

              for (int er = 0; er < listaCampiChiaveEntitaCopia.get(i).length; er++)
                campiDaCopiare.getColumn(
                    listaCampiChiaveEntitaCopia.get(i)[er]).setChiave(
                    true);

              campiDaCopiare.setValue(
                  listaCampiChiaveEntitaCopia.get(i)[0],
                  nGaraDestinazione);

              // Gestioni particolari della copia
              if ("GARE".equalsIgnoreCase(entitaCopia[i])) {
                campiDaCopiare.setValue("GARE.CODGAR1", codiceGaraDestinazione);

                // Rimozione dei campi da non copiare
                // MOD SS151209 - Aggiunto campi relativi all'aggiudicazione
                // provvisoria
                String campiDaNonCopiare[] = null;

                if (copiaDitte) {
                  campiDaNonCopiare = new String[]{ "GARE.FASGAR",
                      "GARE.NOFVAL", "GARE.NOFMED", "GARE.MEDIA", "GARE.DITTAP",
                      "GARE.RIBPRO", "GARE.IAGPRO", "GARE.DVPROV", "GARE.NVPROV",
                      "GARE.DITTA", "GARE.NOMIMA", "GARE.RIBAGG", "GARE.IAGGIU",
                      "GARE.DVERAG", "GARE.NPROVA", "GARE.TATTOA", "GARE.DATTOA",
                      "GARE.NATTOA", "GARE.NPROAA", "GARE.TIATTO", "GARE.NREPAT",
                      "GARE.DAATTO", "GARE.DCOMAG", "GARE.NCOMAG", "GARE.DCOMNG",
                      "GARE.NCOMNG", "GARE.CONTEN", "GARE.MOTCON", "GARE.IMPGAR",
                      "GARE.RIDISO", "GARE.NQUIET", "GARE.DQUIET", "GARE.ISTCRE",
                      "GARE.INDIST", "GARE.RICDOC", "GARE.RESPRE", "GARE.RICPRE",
                      "GARE.DACERT", "GARE.NPROCE", "GARE.LIMMAX", "GARE.NUMELE",
                      "GARE.NUMERA", "GARE.CLAVOR", "GARE.VERCAN", "GARE.VERNUM",
                      "GARE.STEPGAR", "GARE.ELENCOE","GARE.DRICZDOCCR",
                      "GARE.TAGGEFF", "GARE.NAGGEFF", "GARE.DAGGEFF",
                      "GARE.DCOMDITTAGG", "GARE.NCOMDITTAGG", "GARE.DCOMDITTNAG",
                      "GARE.NCOMDITTNAG", "GARE.NMAXIMO", "GARE.CODCIG", "GARE.DACQCIG",
                      "GARE.ESINEG", "GARE.DATNEG", "GARE.DLETTAGGPROV","GARE.DSEDPUBEVA",
                      "GARE.DAVVPRVREQ", "GARE.DRICHDOCCR", "GARE.NPROREQ", "GARE.DTERMDOCCR",
                      "GARE.DRICESP", "GARE.IMPCOM", "GARE.IMPLIQ", "GARE.DATLIQ",
                      "GARE.DINVDOCTEC", "GARE.DCONVDITTE", "GARE.RIBOEPV", "GARE.ISRICONCLUSA"};
                } else {
                  campiDaNonCopiare = new String[]{ "GARE.FASGAR",
                      "GARE.NOFVAL", "GARE.NOFMED", "GARE.MEDIA", "GARE.DITTAP",
                      "GARE.RIBPRO", "GARE.IAGPRO", "GARE.DVPROV", "GARE.NVPROV",
                      "GARE.DITTA", "GARE.NOMIMA", "GARE.RIBAGG", "GARE.IAGGIU",
                      "GARE.DVERAG", "GARE.NPROVA", "GARE.TATTOA", "GARE.DATTOA",
                      "GARE.NATTOA", "GARE.NPROAA", "GARE.TIATTO", "GARE.NREPAT",
                      "GARE.DAATTO", "GARE.DCOMAG", "GARE.NCOMAG", "GARE.DCOMNG",
                      "GARE.NCOMNG", "GARE.CONTEN", "GARE.MOTCON", "GARE.IMPGAR",
                      "GARE.RIDISO", "GARE.NQUIET", "GARE.DQUIET", "GARE.ISTCRE",
                      "GARE.INDIST", "GARE.RICDOC", "GARE.RESPRE", "GARE.RICPRE",
                      "GARE.DACERT", "GARE.NPROCE", "GARE.LIMMAX", "GARE.NUMELE",
                      "GARE.NUMERA", "GARE.CLAVOR", "GARE.VERCAN", "GARE.VERNUM",
                      "GARE.STEPGAR", "GARE.DRICZDOCCR",
                      "GARE.TAGGEFF", "GARE.NAGGEFF", "GARE.DAGGEFF",
                      "GARE.DCOMDITTAGG", "GARE.NCOMDITTAGG", "GARE.DCOMDITTNAG",
                      "GARE.NCOMDITTNAG", "GARE.NMAXIMO", "GARE.CODCIG", "GARE.DACQCIG",
                      "GARE.ESINEG", "GARE.DATNEG", "GARE.DLETTAGGPROV","GARE.DSEDPUBEVA",
                      "GARE.DAVVPRVREQ", "GARE.DRICHDOCCR", "GARE.NPROREQ", "GARE.DTERMDOCCR",
                      "GARE.DRICESP", "GARE.IMPCOM", "GARE.IMPLIQ", "GARE.DATLIQ",
                      "GARE.DINVDOCTEC", "GARE.DCONVDITTE", "GARE.RIBOEPV", "GARE.ISRICONCLUSA"};
                }

                campiDaCopiare.removeColumns(campiDaNonCopiare);
                //Se non è stata selezionata la scelta di copiare i Termini dell'offerta e non si copia come lotto
                //si devono escludere i seguenti campi
                if (!copiaTermini && !copiaComeLotto) {
                  campiDaNonCopiare = new String[]{"GARE.DINVIT","GARE.NPROTI","GARE.DTEOFF",
                      "GARE.OTEOFF","GARE.DTERMRICHCPOG","GARE.DTERMRISPCPOG","GARE.DESOFF",
                      "GARE.OESOFF","GARE.DPUBAVG","GARE.DFPUBAG","GARE.NAVVIGG","GARE.DAVVIGG",
                      "GARE.DIBANDG"};
                  campiDaCopiare.removeColumns(campiDaNonCopiare);
                }else if (!copiaTermini && copiaComeLotto) {
                  //Caso di copia come lotto e non è stata selezionata la scelta di copiare i Termini dell'offerta
                  campiDaNonCopiare = new String[]{"GARE.DINVIT","GARE.NPROTI","GARE.DTEOFF",
                      "GARE.OTEOFF","GARE.DTERMRICHCPOG","GARE.DTERMRISPCPOG","GARE.DESOFF",
                      "GARE.OESOFF"};
                  campiDaCopiare.removeColumns(campiDaNonCopiare);
                }
                // Se la gara è copiata come lotto, allora i valori di DIBANDG,
                // DAVVIGG, DPUBAVG, DFPUBAG
                // si devono prendere da quelli della gara di destinazione
                if (copiaComeLotto) {
                  Vector<?> datiTorn = this.sqlManager.getVector(
                      "select DIBAND, DAVVIG, DPUBAV, DFPUBA, NAVVIG from"
                          + " torn where codgar = ?",
                      new Object[] { codiceGaraDestinazione });
                  if (datiTorn != null && datiTorn.size() > 0) {
                    campiDaCopiare.setValue("GARE.DIBANDG",
                        ((JdbcParametro) datiTorn.get(0)).getValue());
                    campiDaCopiare.setValue("GARE.DAVVIGG",
                        ((JdbcParametro) datiTorn.get(1)).getValue());
                    campiDaCopiare.setValue("GARE.DPUBAVG",
                        ((JdbcParametro) datiTorn.get(2)).getValue());
                    campiDaCopiare.setValue("GARE.DFPUBAG",
                        ((JdbcParametro) datiTorn.get(3)).getValue());
                    campiDaCopiare.setValue("GARE.NAVVIGG",
                        ((JdbcParametro) datiTorn.get(4)).getValue());
                  }
                }

                //ricerca di mercato negoziata:non copio il codice gara padre per non
                //avere più affidamenti generati pe rlo stesso operatore
                if(isPadreRicercaMercatoNegoziata) {
                	campiDaCopiare.removeColumns(new String[]{"GARE.SEGUEN"});
                }

              }

              if ("GARESTATI".equalsIgnoreCase(entitaCopia[i]))
                campiDaCopiare.setValue("GARESTATI.CODGAR",
                    codiceGaraDestinazione);

              if ("GARE1".equalsIgnoreCase(entitaCopia[i])) {
                campiDaCopiare.setValue("GARE1.CODGAR1", codiceGaraDestinazione);
                // Rimozione dei campi da non copiare
                campiDaCopiare.removeColumns(new String[] { "GARE1.NOTPROV",
                    "GARE1.NOTDEFI", "GARE1.NPANNREVAGG", "GARE1.NOTNEG", "GARE1.NPLETTAGGPROVV", "GARE1.DCOMSVIP",
                    "GARE1.NCOMSVIP", "GARE1.NOTCOMM", "GARE1.DLETCOM", "GARE1.NPLETCOM", "GARE1.NRICHNOMINAMIT", "GARE1.DRICHNOMINAMIT",
                    "GARE1.METSOGLIA", "GARE1.METCOEFF", "GARE1.MEDIASCA", "GARE1.SOMMARIB", "GARE1.MEDIAIMP",
                    "GARE1.SOGLIAIMP", "GARE1.AEDINVIT","GARE1.AENPROTI","GARE1.NOFALAINF",
                    "GARE1.NOFALASUP","GARE1.METPUNTI","GARE1.UUID","GARE1.IAGGIUINI","GARE1.RIBAGGINI",
                    "GARE1.SOGLIA1", "GARE1.SOGLIAVAR", "GARE1.MEDIARAP", "GARE1.SOGLIANORMA", "GARE1.MOTESENTECIG", "GARE1.NUMRDO",
                    "GARE1.STATOCG"});
              }

              if ("GARECONT".equalsIgnoreCase(entitaCopia[i])) {
                String campiDaNonCopiare[] = new String[]{ "GARECONT.FASGAR",
                    "GARECONT.CODIMP", "GARECONT.INCORSO", "GARECONT.DVERBC", "GARECONT.CONSANT",
                    "GARECONT.MOTANT", "GARECONT.DAVVES", "GARECONT.NGIORIT", "GARECONT.LAVSUB",
                    "GARECONT.PERCAV", "GARECONT.DTERES", "GARECONT.DCERTU", "GARECONT.ISNCONF",
                    "GARECONT.DESNCONF", "GARECONT.NOTE", "GARECONT.NMESPRO", "GARECONT.IMPLIQ",
                    "GARECONT.COORBA", "GARECONT.STATO", "GARECONT.IMPIVA", "GARECONT.IMPTOT",
                    "GARECONT.DATDEF", "GARECONT.DATTRA", "GARECONT.DATLET", "GARECONT.DATREV",
                    "GARECONT.MOTREV", "GARECONT.DATRIF", "GARECONT.NPROAT", "GARECONT.IDPRG",
                    "GARECONT.IDDOCDG", "GARECONT.IMPQUA", "GARECONT.LREGCO", "GARECONT.NREGCO",
                    "GARECONT.DREGCO", "GARECONT.NUMCONT", "GARECONT.CODBIC", "GARECONT.DSCAPO",
                    "GARECONT.NPROPO", "GARECONT.NUMCONT", "GARECONT.DCONSD" , "GARECONT.DPROAT",
                    "GARECONT.NPROTCOORBA", "GARECONT.DPROTCOORBA"};
                campiDaCopiare.removeColumns(campiDaNonCopiare);
              }

              if ("G1CRIDEF".equals(entitaCopia[i])) {
                idOriginale=campiDaCopiare.getLong("G1CRIDEF.ID");
                //Si deve valorizzare il campo chiave ID
                idCridef= genChiaviManager.getNextId("G1CRIDEF");
                campiDaCopiare.setValue("G1CRIDEF.ID", new Long(idCridef));
              }

              // Inserimento del nuovo record
              campiDaCopiare.insert(entitaCopia[i], this.geneManager.getSql());

              if ("G1CRIDEF".equals(entitaCopia[i])) {
                /////////////////////////////////////////////////////
                // Inserimento occorenze di G1CRIREG figlia di G1CRIDEF
                /////////////////////////////////////////////////////

                List<?> listaOccorrenzeDaCopiareCrireg = null;
                campiDaCopiare = null;

                listaOccorrenzeDaCopiareCrireg = this.geneManager.getSql().getListHashMap(
                    "select * from G1CRIREG where idcridef = ?" ,
                    new Object[] { idOriginale});

                if (listaOccorrenzeDaCopiareCrireg != null
                    && listaOccorrenzeDaCopiareCrireg.size() > 0) {
                  campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
                      "G1CRIREG", "select * from G1CRIREG", new Object[] {});

                  int idCrireg=0;
                  for (int rowCrireg = 0; rowCrireg < listaOccorrenzeDaCopiareCrireg.size(); rowCrireg++) {

                    campiDaCopiare.setValoriFromMap(
                        ((HashMap<?,?>) listaOccorrenzeDaCopiareCrireg.get(rowCrireg)), true);
                    campiDaCopiare.getColumn("G1CRIREG.ID").setChiave(true);
                    idCrireg=this.genChiaviManager.getNextId("G1CRIREG");
                    campiDaCopiare.setValue("G1CRIREG.ID", new Long(idCrireg));
                    campiDaCopiare.setValue("G1CRIREG.IDCRIDEF", idCridef);

                    campiDaCopiare.insert("G1CRIREG", this.geneManager.getSql());
                  }
                }
                if (copiaOfferte) {
                  // //////////////////////////////////////////////
                  // Inserimento occorrenze G1CRIVAL figlia di G1CRIDEF
                  // //////////////////////////////////////////////

                  List<?> listaOccorrenzeDaCopiareCrival = null;
                  campiDaCopiare = null;

                  listaOccorrenzeDaCopiareCrival = this.geneManager.getSql().getListHashMap(
                      "select * from G1CRIVAL where idcridef = ? and ngara=?" ,
                      new Object[] { idOriginale,nGaraSorgente });

                  if (listaOccorrenzeDaCopiareCrival != null
                      && listaOccorrenzeDaCopiareCrival.size() > 0) {
                    campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
                        "G1CRIVAL", "select * from G1CRIVAL", new Object[] {});

                    int idCrival=0;
                    for (int rowCrival = 0; rowCrival < listaOccorrenzeDaCopiareCrival.size(); rowCrival++) {

                      campiDaCopiare.setValoriFromMap(
                          ((HashMap<?,?>) listaOccorrenzeDaCopiareCrival.get(rowCrival)), true);
                      campiDaCopiare.getColumn("G1CRIVAL.ID").setChiave(true);
                      idCrival=this.genChiaviManager.getNextId("G1CRIVAL");
                      campiDaCopiare.setValue("G1CRIVAL.ID", new Long(idCrival));
                      campiDaCopiare.setValue("G1CRIVAL.NGARA", nGaraDestinazione);
                      campiDaCopiare.setValue("G1CRIVAL.IDCRIDEF", idCridef);

                      campiDaCopiare.insert("G1CRIVAL", this.geneManager.getSql());
                    }
                  }
                }

              }


              // Gestione dei permessi della gara destinazione
              /*
              if (!copiaGareDaTorn
                  && "GARE".equalsIgnoreCase(entitaCopia[i])
                  && codiceGaraDestinazione.startsWith("$")) {
              */
              if ("GARE".equalsIgnoreCase(entitaCopia[i]) && (!copiaGareDaTorn
                  && codiceGaraDestinazione.startsWith("$")|| (copiaGareDaTorn && genere != null && genere.longValue() == 3))) {
                // Estraggo il gestore di V_GARE_TORN
                AbstractGestoreEntita gest = new DefaultGestoreEntita(
                    "V_GARE_TORN", request);
                DataColumnContainer dcc = new DataColumnContainer(
                    this.geneManager.getSql(), "V_GARE_TORN",
                    "select * from V_GARE_TORN", new String[] {});
                dcc.setValue("V_GARE_TORN.CODGAR",
                    campiDaCopiare.getString("GARE.CODGAR1"));
                gest.inserisciPermessi(dcc, "CODGAR", new Integer(2));

                //Aggiornamento del campo meruolo della G_permessi
                ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
                    CostantiGenerali.PROFILO_UTENTE_SESSIONE);
                Long idUtente = new Long(profilo.getId());
                String ruoloME = profilo.getRuoloUtenteMercatoElettronico();
                Long meruolo=new Long(2);
                if (ruoloME != null && !"".equals(ruoloME))
                  meruolo= new Long(ruoloME);
                this.updateMeruoloG_Permessi(codiceGaraDestinazione, idUtente, meruolo);
              }


            }

            // Eseguo la copia delle occorrenze del generatore attributi
            this.geneManager.copiaOccorrenzeGeneratoreAttributi(
                entitaCopia[i],
                listaCampiChiaveEntitaCopia.get(i)[0].concat(" = ?"),
                new Object[] { nGaraSorgente },
                new String[] { listaCampiChiaveEntitaCopia.get(i)[0] },
                new Object[] { nGaraDestinazione }, false);
            // Eseguo la copia delle occorrenze di note avvisi delle entita'
            // copiate
            this.geneManager.copiaOccorrenzeNoteAvvisi(
                entitaCopia[i],
                listaCampiChiaveEntitaCopia.get(i)[0].concat(" = ?"),
                new Object[] { nGaraSorgente },
                new String[] { listaCampiChiaveEntitaCopia.get(i)[0] },
                new Object[] { nGaraDestinazione }, false);
          }
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore nella copia del lotto di gara ! per entita' "
                  + entitaCopia[i], "copiaGARE", e);
        }
      }
    }

    // Gestione GARECONT
    // Quando si copia una gara si deve creare l'occorrenza di GARECONT senza
    // popolarla solo nel caso di offerta unica e non per la copia di un lotto
    try {

      if (genere != null
          && genere.longValue() == 3 &&
          nGaraDestinazione.equals(codiceGaraDestinazione)) {
        Long maxNcont = (Long) this.geneManager.getSql().getObject(
            "select max(ncont) from garecont where ngara=?",
            new Object[] { nGaraDestinazione });
        if (maxNcont == null)
          maxNcont = new Long(1);
        else
          maxNcont = new Long(maxNcont.longValue() + 1);

        this.geneManager.getSql().update(
            "insert into garecont(ngara,ncont) values(?,?)",
            new Object[] { nGaraDestinazione, maxNcont });

      }


    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella copia del lotto di gara ! per entita' GARECONT",
          "copiaGARE", e);
    }

    if (copiaDitte) {
      this.copiaDitte(nGaraSorgente, codiceGaraSorgente, nGaraDestinazione,
          codiceGaraDestinazione, copiaOfferte, copiaGareDaTorn,
          !codiceGaraDestinazione.startsWith("$"));
    }

    // Copia dello scadenzario
    if (copiaScadenzario) {
      String entita = "TORN";
      Object[] chiaveFrom = new Object[] { codiceGaraSorgente };
      Object[] chiaveTo = new Object[1];
      // Se la gara da copiare è un lotto allora entitaFrom="GARE" e chiaveFrom
      // è ngara
      if (!codiceGaraSorgente.startsWith("$")) {
        entita = "GARE";
        chiaveFrom[0] = nGaraSorgente;
      }

      if (copiaComeLotto) {
        chiaveTo[0] = nGaraDestinazione;
      } else {
        chiaveTo[0] = codiceGaraDestinazione;
      }

      try {
        this.scadenzariManager.insertClonazioneAttivitaScadenzario(codapp,
            entita, chiaveFrom, chiaveTo, false);
        this.scadenzariManager.updateDateScadenzarioEntita(entita, chiaveTo,
            codapp, false, null);
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella copia dello Scadenzario della gara " + nGaraSorgente,
            "copiaGARE", e);
      }
    }

    // //////////////////////////////////////////////
    // Inserimento occorrenze GARCONFDATI
    // //////////////////////////////////////////////
    campiDaCopiare = null;
    List<?> listaOccorrenzeDaCopiare = null;

    if (logger.isDebugEnabled()) {
      logger.debug("copiaDitte: inizio inserimento occorrenze GARCONFDATI");
    }

    try {
      listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
          "select * from GARCONFDATI "
              + "where NGARA = ? and ENTITA=?", new Object[] { nGaraSorgente , "XDPRE"});
      if (listaOccorrenzeDaCopiare != null
          && listaOccorrenzeDaCopiare.size() > 0) {
        campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
            "GARCONFDATI", "select * from GARCONFDATI ", new Object[] {});

        for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {
          campiDaCopiare.setValoriFromMap(
              ((HashMap<?,?>) listaOccorrenzeDaCopiare.get(row)), true);
          campiDaCopiare.getColumn("GARCONFDATI.ID").setChiave(true);
          Long newId = new Long(genChiaviManager.getNextId("GARCONFDATI"));
          campiDaCopiare.setValue("GARCONFDATI.ID", newId);
          campiDaCopiare.setValue("GARCONFDATI.NGARA", nGaraDestinazione);
          campiDaCopiare.insert("GARCONFDATI", this.geneManager.getSql());

        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella copia della GARCONFDATI per la gara " + nGaraSorgente,
          "copiaGARE", e);
    }


    // //////////////////////////////////////////////
    // Inserimento occorrenze GARALTSOG
    // //////////////////////////////////////////////
    campiDaCopiare = null;
    if (logger.isDebugEnabled()) {
      logger.debug("copiaDitte: fine inserimento occorrenze GARALTSOG");
    }


    try {
      listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
          "select * from GARALTSOG "
              + "where NGARA = ?", new Object[] { nGaraSorgente });
      if (listaOccorrenzeDaCopiare != null
          && listaOccorrenzeDaCopiare.size() > 0) {
        campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
            "GARALTSOG", "select * from GARALTSOG ", new Object[] {});

        for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {
          campiDaCopiare.setValoriFromMap(
              ((HashMap<?,?>) listaOccorrenzeDaCopiare.get(row)), true);
          campiDaCopiare.getColumn("GARALTSOG.ID").setChiave(true);
          Long newId = new Long(genChiaviManager.getNextId("GARALTSOG"));
          campiDaCopiare.setValue("GARALTSOG.ID", newId);
          campiDaCopiare.setValue("GARALTSOG.CODGAR", null);
          campiDaCopiare.setValue("GARALTSOG.NGARA", nGaraDestinazione);
          campiDaCopiare.insert("GARALTSOG", this.geneManager.getSql());

        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella copia della GARALTSOG per la gara " + codiceGaraSorgente,
          "copiaGARE", e);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("copiaDitte: fine inserimento occorrenze GARALTSOG");
    }

    if (logger.isDebugEnabled()) logger.debug("copiaGARE: fine metodo");
  }

  /**
   * Metodo per la copia delle ditte e delle relative offerte, se l'argomento
   * copiaOfferte e' a true
   *
   * @param nGaraSorgente
   *        Codice lotto sorgente
   * @param codiceGaraSorgente
   *        Codice gara/tornata sorgente
   * @param nGaraDestinazione
   *        Codice lotto destinazione
   * @param codiceGaraDestinazione
   *        Codice gara/tornata destinazione
   * @param copiaOfferte
   *        Flag indicante se copiare o meno le offerte delle ditte
   * @param copiaGareDaTorn
   *        Flag indicante se la copia dei lotti e' stata richiamata dal metodo
   *        copiaTORN o meno
   * @param isDestinazioneTornata
   *        Flag indicante se la gara di destinazione e' una tornata o un lotto
   * @throws GestoreException
   */
  public void copiaDitte(String nGaraSorgente, String codiceGaraSorgente,
      String nGaraDestinazione, String codiceGaraDestinazione,
      boolean copiaOfferte, boolean copiaGareDaTorn,
      boolean isDestinazioneTornata) throws GestoreException {

    // OSSERVAZIONE: questo metodo copia SOLO le occorrenze del generatore
    // attributi della entita' DITG

    if (logger.isDebugEnabled()) logger.debug("copiaDitte: inizio metodo");

    // Flag per indicare che:
    // se valorizzato a true: l'inserimento della ditta avviene determinando il
    // valore del DITG.NPROGG come il max del campo stesso (anche se si tratta
    // di una gara a lotti)
    // se valorizzato a false: l'inserimento della ditta avviene usando il campo
    // EDIT.NPROGT se la ditta e' gia' presente nella tabella EDIT, oppure
    // determinando il max fra il campo EDIT.NPROGT e DITG.NPROGG relativamente
    // all'intera tornata (e non nel lotto di gara in analisi (per una gara
    // divisa a lotti))
    boolean modalitaNPROGGsuLotti = false;

    // Determino la modalita' di inserimento della ditta in funzione del valore
    // del tabellato A1041 presente nella tab1
    List<Tabellato> listaTabellato = tabellatiManager.getTabellato("A1041");
    if (listaTabellato != null && listaTabellato.size() > 0) {
      String descrTab = listaTabellato.get(0).getDescTabellato();
      if (descrTab != null && descrTab.startsWith("1"))
        modalitaNPROGGsuLotti = true;
    }

    try {
      // Si determina se la gara è una gara divisa in lotti con offera unica
      // perchè in questo caso il flag modalitaNPROGGsuLotti deve essere false
      String select = "select genere from gare where ngara = ?";
      Long genere = (Long) this.geneManager.getSql().getObject(select,
          new Object[] { codiceGaraSorgente });
      if (genere != null && 3 == genere.longValue()) {
        modalitaNPROGGsuLotti = false;
      }

      DataColumnContainer campiDaCopiare = null;
      List<?> listaOccorrenzeDaCopiare = null;

      // Se la copia delle ditte viene richieste dalla copia di un lotto
      if (!copiaGareDaTorn) {

        // //////////////////////////////////////////////////////
        // Inserimento occorrenze EDIT
        // //////////////////////////////////////////////////////
        campiDaCopiare = null;
        listaOccorrenzeDaCopiare = null;

        if (logger.isDebugEnabled())
          logger.debug("copiaDitte: inizio " + "inserimento occorrenze EDIT");

        // Determino il valore massimo del campo EDIT.NPROGT
        // per il codice di destinazione
        Long maxNPROGT = (Long) this.geneManager.getSql().getObject(
            "select MAX(NPROGT) from EDIT where EDIT.CODGAR4 = ?",
            new Object[] { codiceGaraDestinazione });
        if (maxNPROGT == null) maxNPROGT = new Long(0);

        int contatore = 1;
        listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
            "select * from EDIT "
                + "where exists( "
                + "select CODGAR5 from DITG "
                + "where DITG.CODGAR5 = EDIT.CODGAR4 "
                + "and DITG.DITTAO = EDIT.CODIME "
                + "and DITG.NGARA5 = ? ) "
                + // nGaraSorgente
                "and not exists( "
                + "select CODGAR4 from EDIT EDIT1 "
                + "where EDIT1.CODGAR4 = ? "
                + // codiceGaraDestinazione
                "and EDIT.CODIME = EDIT1.CODIME) order by EDIT.NPROGT asc",
            new Object[] { nGaraSorgente, codiceGaraDestinazione });

        if (listaOccorrenzeDaCopiare != null
            && listaOccorrenzeDaCopiare.size() > 0) {
          for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {
            campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
                "EDIT", "select * from EDIT", new Object[] {});

            campiDaCopiare.setValoriFromMap(
                (HashMap<?,?>) listaOccorrenzeDaCopiare.get(row), true);

            campiDaCopiare.getColumn("EDIT.CODGAR4").setChiave(true);
            campiDaCopiare.getColumn("EDIT.CODIME").setChiave(true);

            campiDaCopiare.setValue("EDIT.CODGAR4", codiceGaraDestinazione);
            campiDaCopiare.setValue("EDIT.NPROGT",
                new Long(maxNPROGT.intValue() + contatore));
            contatore++;

            if (!copiaOfferte) {
              // Campi da inizializzare
              campiDaCopiare.setValue("EDIT.DOCOK", "1");
              campiDaCopiare.setValue("EDIT.DATOK", "1");
              campiDaCopiare.setValue("EDIT.DITINV", "1");

              // Campi da non copiare
              campiDaCopiare.removeColumns(new String[] { "EDIT.DRICIN",
                  "EDIT.MOTESC", "EDIT.ESTIMP" });
            }

            // Inserimento del nuovo record
            campiDaCopiare.insert("EDIT", this.geneManager.getSql());
          }
        }
        // Rispetto a PWB non viene fatto il controllo se delle ditte non
        // vengono copiate perchè escluse dalla tornata
        if (logger.isDebugEnabled())
          logger.debug("copiaDitte: fine " + "inserimento occorrenze EDIT");
      }

      // //////////////////////////////////////////////
      // Inserimento occorrenze DITG
      // //////////////////////////////////////////////
      // Nel caso di copia della DITG senza la copia delle offerte, poichè i
      // campi copiare sono pochi, l'oggetto campiDaCopiare contiene SOLO i
      // campi da copiare. Infatti di solito tale oggetto contiene tutti i campi
      // e da esso vengono rimossi i campi da non copiare.
      if (logger.isDebugEnabled())
        logger.debug("copiaDitte: inizio " + "inserimento occorrenze DITG");
      campiDaCopiare = null;
      listaOccorrenzeDaCopiare = null;

      String selectDitg = "select * from DITG where NGARA5 in ( "
          + "select DITG1.NGARA5 from DITG DITG1, EDIT "
          + "where EDIT.CODGAR4 = ? "
          + // codiceGaraDestinazione
          "and DITG1.DITTAO = EDIT.CODIME "
          + "and DITG1.NGARA5 = ? )  "
          + "order by NPROGG asc, NOMIMO asc "; // nGaraSorgente

      if (!copiaOfferte) {
        //Si devono escludere le ditte con acquisizione=5, e le relative ditte inserite nei lotti
        selectDitg = "select * from DITG D where NGARA5 in ( "
            + "select DITG1.NGARA5 from DITG DITG1, EDIT where EDIT.CODGAR4 = ? "
            + "and DITG1.DITTAO = EDIT.CODIME and DITG1.NGARA5 = ? ) "
            + "and (D.ACQUISIZIONE is null or D.ACQUISIZIONE <> 5) and D.DITTAO not in "
            + "(select D2.RTOFFERTA from DITG D2 where D2.CODGAR5=D.CODGAR5 and "
            + "D2.CODGAR5=D2.NGARA5 and D2.RTOFFERTA is not null) "
            + "order by D.NPROGG asc, D.NOMIMO asc ";
      }

      listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
          selectDitg, new Object[] { codiceGaraDestinazione, nGaraSorgente });

      if (listaOccorrenzeDaCopiare != null
          && listaOccorrenzeDaCopiare.size() > 0) {

        String selectTipgar = "select iterga from torn where codgar=?";
        Long iterga = (Long) this.sqlManager.getObject(selectTipgar,
            new Object[] { codiceGaraDestinazione });
        String dbFunctionDateToStringDprdom = sqlManager.getDBFunction("DATETIMETOSTRING",
            new String[] { "dprdom" });
        String dbFunctionDateToStringDproff = sqlManager.getDBFunction("DATETIMETOSTRING",
            new String[] { "dproff" });
        String selectDateProtocollo="select " + dbFunctionDateToStringDprdom + ", " + dbFunctionDateToStringDproff + " from ditg where ngara5=? and codgar5=? and dittao=?";
        Vector<?> dateProt=null;
        String dprdomString=null;
        String dproffString=null;
        java.util.Date dprdom=null;
        java.util.Date dproff=null;
        Timestamp dprdomTime = null;
        Timestamp dproffTime = null;

        for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {

          if (copiaOfferte) {
            campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
                "DITG", "select * from DITG", new Object[] {});

            // I campi STAGGI, CONGRUO, CONGAL, CONGMOT non devono essere mai
            // copiati nel caso di copia delle offerte delle ditte
            campiDaCopiare.removeColumns(new String[] { "DITG.STAGGI",
                "DITG.CONGRUO", "DITG.CONGALT", "DITG.CONGMOT", "DITG.STAGGIALI"});
          } else
            campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
                "DITG", "select CODGAR5, DITTAO, NGARA5, NOMIMO, IMPAPPD, "
                    + "CATIMOK, INVGAR, NPROGG, NUMORDPL from DITG",
                new Object[] {});

          campiDaCopiare.setValoriFromMap(
              (HashMap<?,?>) listaOccorrenzeDaCopiare.get(row), true);

          campiDaCopiare.getColumn("DITG.CODGAR5").setChiave(true);
          campiDaCopiare.getColumn("DITG.NGARA5").setChiave(true);
          campiDaCopiare.getColumn("DITG.DITTAO").setChiave(true);

          campiDaCopiare.setValue("DITG.CODGAR5", codiceGaraDestinazione);
          campiDaCopiare.setValue("DITG.NGARA5", nGaraDestinazione);

          if (modalitaNPROGGsuLotti) {
            campiDaCopiare.setValue("DITG.NPROGG", new Long(row + 1));
          } else {
            Long tmpNPROGT = (Long) this.geneManager.getSql().getObject(
                "select NPROGT from EDIT " + "where CODGAR4 = ? " + // codiceGaraDestinazione
                    "and CODIME  = ? ", // codice ditta nella DITG che si sta
                                        // copiando
                new Object[] { codiceGaraDestinazione,
                    campiDaCopiare.getString("DITTAO") });

            campiDaCopiare.setValue("DITG.NPROGG", tmpNPROGT);
          }

          if (copiaOfferte) {
            if (campiDaCopiare.getLong("DITG.MOTIES") != null) {
              long motivazEsclus = campiDaCopiare.getLong("DITG.MOTIES").longValue();
              if (motivazEsclus >= 98 && motivazEsclus <= 101) {
                campiDaCopiare.setValue("DITG.AMMGAR", "1");
                campiDaCopiare.removeColumns(new String[] { "DITG.MOTIES" });
                campiDaCopiare.removeColumns(new String[] { "DITG.FASGAR" });
              }
            }
          } else {
            // Inizializzazione campi
            campiDaCopiare.setValue("DITG.NUMORDPL",
                campiDaCopiare.getLong("DITG.NPROGG"));
            campiDaCopiare.setValue("DITG.CATIMOK", "1");
            campiDaCopiare.setValue("DITG.INVGAR", "1");

            if (new Long(1).equals(iterga)) {
              campiDaCopiare.addColumn("DITG.INVOFF", JdbcParametro.TIPO_TESTO,
                  "1");
            }

          }
          if (copiaOfferte) {
            Long acquisizione = campiDaCopiare.getLong("DITG.ACQUISIZIONE");
            if (acquisizione != null && acquisizione.longValue()!=5)
              campiDaCopiare.setValue("DITG.ACQUISIZIONE", null);
            //Si deve fare una select per estrarre i valori di DPRDOM e DPROFF come String
            //quindi convertirli in Timestamp e assegnarli ai campi, altrimenti non viene salvato
            //l'orario
            dprdomTime=null;
            dproffTime=null;
            dateProt = this.sqlManager.getVector(selectDateProtocollo, new Object[]{nGaraSorgente,codiceGaraSorgente,campiDaCopiare.getString("DITTAO")});
            if(dateProt!=null && dateProt.size()>0){
              dprdomString = ((JdbcParametro)dateProt.get(0)).getStringValue();
              if(dprdomString!=null && !"".equals(dprdomString)){
                dprdom = UtilityDate.convertiData(dprdomString, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
                dprdomTime =  new Timestamp(dprdom.getTime());
          }
              dproffString = ((JdbcParametro)dateProt.get(1)).getStringValue();
              if(dproffString!=null && !"".equals(dproffString)){
                dproff = UtilityDate.convertiData(dproffString, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
                dproffTime =  new Timestamp(dproff.getTime());
              }
            }
            campiDaCopiare.setValue("DITG.DPRDOM", dprdomTime);
            campiDaCopiare.setValue("DITG.DPROFF", dproffTime);
          }


          campiDaCopiare.insert("DITG", this.geneManager.getSql());

        }
      }

      if (logger.isDebugEnabled()) {
        logger.debug("copiaDitte: fine inserimento occorrenze DITG");
        logger.debug("copiaDitte: inizio inserimento occorrenze DPRE");
      }

      // //////////////////////////////////////////////
      // Inserimento occorrenze DPRE
      // //////////////////////////////////////////////
      campiDaCopiare = null;
      listaOccorrenzeDaCopiare = null;

      // Si dovrebbe implementare anche la copia di DPRE_SAN, ma poichè
      // la copia offerte è disabilitata, non scatterebbe mai
      // Se si riabilita la copia offerte, si deve implementare la copia di
      // DPRE_SAN
      if (copiaOfferte) {
        listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
            "select * from DPRE "
                + "where NGARA in ( "
                + "select NGARA from EDIT, DPRE DPRE1 "
                + "where EDIT.CODIME = DPRE1.DITTAO "
                + "and EDIT.CODGAR4 = ? "
                + // codiceGaraDestinazione
                "and DPRE1.NGARA  = ? "
                + // nGaraSorgente
                ")", new Object[] { codiceGaraDestinazione, nGaraSorgente });

        if (listaOccorrenzeDaCopiare != null
            && listaOccorrenzeDaCopiare.size() > 0) {
          campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
              "DPRE", "select * from DPRE ", new Object[] {});

          for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {
            campiDaCopiare.setValoriFromMap(
                ((HashMap<?,?>) listaOccorrenzeDaCopiare.get(row)), true);
            campiDaCopiare.getColumn("DPRE.NGARA").setChiave(true);
            campiDaCopiare.getColumn("DPRE.DITTAO").setChiave(true);
            campiDaCopiare.getColumn("DPRE.CONTAF").setChiave(true);

            campiDaCopiare.setValue("DPRE.NGARA", nGaraDestinazione);
            campiDaCopiare.removeColumns(new String[] { "DPRE.PERRIB" });

            campiDaCopiare.insert("DPRE", this.geneManager.getSql());

          }
        }

        // //////////////////////////////////////////////
        // Inserimento occorrenze DPRE_SAN
        // //////////////////////////////////////////////
        campiDaCopiare = null;
        listaOccorrenzeDaCopiare = null;

        listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
            "select * from DPRE_SAN "
                + "where NGARA in ( "
                + "select NGARA from EDIT, DPRE_SAN DPRE_SAN1 "
                + "where EDIT.CODIME = DPRE_SAN1.DITTAO "
                + "and EDIT.CODGAR4 = ? "
                + // codiceGaraDestinazione
                "and DPRE_SAN1.NGARA  = ? "
                + // nGaraSorgente
                ")", new Object[] { codiceGaraDestinazione, nGaraSorgente });

        if (listaOccorrenzeDaCopiare != null
            && listaOccorrenzeDaCopiare.size() > 0) {
          campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
              "DPRE_SAN", "select * from DPRE_SAN ", new Object[] {});

          for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {
            campiDaCopiare.setValoriFromMap(
                ((HashMap<?,?>) listaOccorrenzeDaCopiare.get(row)), true);
            campiDaCopiare.getColumn("DPRE_SAN.NGARA").setChiave(true);
            campiDaCopiare.getColumn("DPRE_SAN.DITTAO").setChiave(true);
            campiDaCopiare.getColumn("DPRE_SAN.CONTAF").setChiave(true);

            campiDaCopiare.setValue("DPRE_SAN.NGARA", nGaraDestinazione);

            campiDaCopiare.insert("DPRE_SAN", this.geneManager.getSql());

          }
        }

      }

      if (logger.isDebugEnabled()) {
        logger.debug("copiaDitte: fine inserimento occorrenze DPRE");
        logger.debug("copiaDitte: inizio inserimento occorrenze DPUN");
      }

      if (copiaOfferte) {
        // //////////////////////////////////////////////
        // Inserimento occorrenze DPUN
        // //////////////////////////////////////////////

        listaOccorrenzeDaCopiare = null;
        campiDaCopiare = null;

        listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
            "select * from DPUN " + "where NGARA = ? " + // nGaraSorgente
                "and DITTAO in ( "
                + "select CODIME from EDIT "
                + "where CODGAR4 = ? )", // codiceGaraDestinazione"
            new Object[] { nGaraSorgente, codiceGaraDestinazione });

        if (listaOccorrenzeDaCopiare != null
            && listaOccorrenzeDaCopiare.size() > 0) {
          campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
              "DPUN", "select * from DPUN", new Object[] {});

          for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {

            campiDaCopiare.setValoriFromMap(
                ((HashMap<?,?>) listaOccorrenzeDaCopiare.get(row)), true);
            campiDaCopiare.getColumn("DPUN.NGARA").setChiave(true);
            campiDaCopiare.getColumn("DPUN.DITTAO").setChiave(true);
            campiDaCopiare.getColumn("DPUN.NECVAN").setChiave(true);

            campiDaCopiare.setValue("DPUN.NGARA", nGaraDestinazione);

            campiDaCopiare.insert("DPUN", this.geneManager.getSql());
          }
        }
      }
      if (logger.isDebugEnabled()) {
        logger.debug("copiaDitte: fine inserimento occorrenze DPUN");
        logger.debug("copiaDitte: inizio inserimento occorrenze RAGDET");
      }

      if (copiaOfferte) {
        // //////////////////////////////////////////////
        // Inserimento occorrenze RAGDET
        // la gestione è stata presa da powerbuilder
        // //////////////////////////////////////////////
        listaOccorrenzeDaCopiare = null;
        campiDaCopiare = null;

        String selectRagdet = "select * from RAGDET where NGARA = ? " + // nGaraSorgente
            "and exists ( select ditg.dittao from ditg, edit where edit.codgar4 = ? "
            + // codiceGaraDestinazione"
            "and ditg.dittao=edit.codime and ditg.ngara5 = ? "
            + // nGaraSorgente
            "and ditg.ngara5=ragdet.ngara and ditg.dittao=ragdet.codimp ";
        if (!copiaOfferte)
          selectRagdet += "and (DITG.ACQUISIZIONE is null or DITG.ACQUISIZIONE <> 5 ))";
        else
          selectRagdet += ")";

        listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
            selectRagdet, new Object[] { nGaraSorgente, codiceGaraDestinazione, nGaraSorgente });

        if (listaOccorrenzeDaCopiare != null
            && listaOccorrenzeDaCopiare.size() > 0) {
          campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
              "RAGDET", "select * from RAGDET", new Object[] {});

          for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {

            campiDaCopiare.setValoriFromMap(
                ((HashMap<?,?>) listaOccorrenzeDaCopiare.get(row)), true);
            campiDaCopiare.getColumn("RAGDET.CODIMP").setChiave(true);
            campiDaCopiare.getColumn("RAGDET.CODDIC").setChiave(true);
            campiDaCopiare.getColumn("RAGDET.NUMDIC").setChiave(true);

            // Si deve calcolare il valore di NUMDIC
            Long maxNumdic = (Long) this.geneManager.getSql().getObject(
                "select max(NUMDIC) from RAGDET where CODIMP= ? and CODDIC=?",
                new Object[] {
                    ((HashMap<?,?>) listaOccorrenzeDaCopiare.get(row)).get("CODIMP").toString(),
                    ((HashMap<?,?>) listaOccorrenzeDaCopiare.get(row)).get("CODDIC").toString() });

            long newNumdic = 1;
            if (maxNumdic != null && maxNumdic.longValue() > 0)
              newNumdic = maxNumdic.longValue() + 1;

            campiDaCopiare.setValue("RAGDET.NUMDIC", new Long(newNumdic));
            campiDaCopiare.setValue("RAGDET.NGARA", nGaraDestinazione);

            campiDaCopiare.insert("RAGDET", this.geneManager.getSql());
          }
        }
        // //////////////////////////////////////////////
        // Inserimento occorrenze DITGAMMIS
        // //////////////////////////////////////////////
        if (logger.isDebugEnabled()) {
          logger.debug("copiaDitte: fine inserimento occorrenze RAGDET");
          logger.debug("copiaDitte: inizio inserimento occorrenze DITGAMMIS");
        }
        listaOccorrenzeDaCopiare = null;
        campiDaCopiare = null;

        listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
            "select * from DITGAMMIS where NGARA = ? " + // nGaraSorgente
                "and DITTAO in ( "
                + "select CODIME from EDIT "
                + "where CODGAR4 = ? )" // codiceGaraDestinazione"
                + " and FASGAR < 7",
            new Object[] { nGaraSorgente, codiceGaraDestinazione });

        if (listaOccorrenzeDaCopiare != null
            && listaOccorrenzeDaCopiare.size() > 0) {
          campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
              "DITGAMMIS", "select * from DITGAMMIS", new Object[] {});

          for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {

            campiDaCopiare.setValoriFromMap(
                ((HashMap<?,?>) listaOccorrenzeDaCopiare.get(row)), true);
            campiDaCopiare.getColumn("DITGAMMIS.NGARA").setChiave(true);
            campiDaCopiare.getColumn("DITGAMMIS.DITTAO").setChiave(true);
            campiDaCopiare.getColumn("DITGAMMIS.CODGAR").setChiave(true);
            campiDaCopiare.getColumn("DITGAMMIS.FASGAR").setChiave(true);

            campiDaCopiare.setValue("DITGAMMIS.NGARA", nGaraDestinazione);
            campiDaCopiare.setValue("DITGAMMIS.CODGAR", codiceGaraDestinazione);

            campiDaCopiare.insert("DITGAMMIS", this.geneManager.getSql());
          }
        }
        // //////////////////////////////////////////////
        // Inserimento occorrenze DITGAVVAL
        // //////////////////////////////////////////////
        if (logger.isDebugEnabled()) {
          logger.debug("copiaDitte: fine inserimento occorrenze DITGAMMIS");
          logger.debug("copiaDitte: inizio inserimento occorrenze DITGAVVAL");
        }

        listaOccorrenzeDaCopiare = null;
        campiDaCopiare = null;

        listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
            "select * from DITGAVVAL " + "where NGARA = ? " + // nGaraSorgente
                "and DITTAO in ( "
                + "select CODIME from EDIT "
                + "where CODGAR4 = ? )", // codiceGaraDestinazione"
            new Object[] { nGaraSorgente, codiceGaraDestinazione });

        if (listaOccorrenzeDaCopiare != null
            && listaOccorrenzeDaCopiare.size() > 0) {
          campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
              "DITGAVVAL", "select * from DITGAVVAL", new Object[] {});

          for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {
            campiDaCopiare.setValoriFromMap(
                ((HashMap<?,?>) listaOccorrenzeDaCopiare.get(row)), true);
            campiDaCopiare.getColumn("DITGAVVAL.ID").setChiave(true);
            Long newId = new Long(genChiaviManager.getNextId("DITGAVVAL"));
            campiDaCopiare.setValue("DITGAVVAL.ID", newId);
            campiDaCopiare.setValue("DITGAVVAL.NGARA", nGaraDestinazione);
            campiDaCopiare.insert("DITGAVVAL", this.geneManager.getSql());
          }

        }

        // //////////////////////////////////////////////
        // Inserimento occorrenze DITGSTATI
        // //////////////////////////////////////////////
        if (logger.isDebugEnabled()) {
          logger.debug("copiaDitte: fine inserimento occorrenze DITGAVVAL");
          logger.debug("copiaDitte: inizio inserimento occorrenze DITGSTATI");
        }
        listaOccorrenzeDaCopiare = null;
        campiDaCopiare = null;

        listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
            "select * from DITGSTATI where NGARA = ? " + // nGaraSorgente
                "and DITTAO in ( "
                + "select CODIME from EDIT "
                + "where CODGAR4 = ? )" // codiceGaraDestinazione"
                + " and FASGAR < 7",
            new Object[] { nGaraSorgente, codiceGaraDestinazione });

        if (listaOccorrenzeDaCopiare != null
            && listaOccorrenzeDaCopiare.size() > 0) {
          campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
              "DITGSTATI", "select * from DITGSTATI", new Object[] {});

          for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {

            campiDaCopiare.setValoriFromMap(
                ((HashMap<?,?>) listaOccorrenzeDaCopiare.get(row)), true);
            campiDaCopiare.getColumn("DITGSTATI.NGARA").setChiave(true);
            campiDaCopiare.getColumn("DITGSTATI.DITTAO").setChiave(true);
            campiDaCopiare.getColumn("DITGSTATI.CODGAR").setChiave(true);
            campiDaCopiare.getColumn("DITGSTATI.FASGAR").setChiave(true);
            campiDaCopiare.getColumn("DITGSTATI.STEPGAR").setChiave(true);

            campiDaCopiare.setValue("DITGSTATI.NGARA", nGaraDestinazione);
            campiDaCopiare.setValue("DITGSTATI.CODGAR", codiceGaraDestinazione);

            campiDaCopiare.insert("DITGSTATI", this.geneManager.getSql());
          }
        }
      }//fine copia offerte cf

      if (logger.isDebugEnabled()) {
          logger.debug("copiaDitte: fine inserimento occorrenze DITGSTATI");
        logger.debug("copiaDitte: inizio gestione dei campi del generatore "
            + "attributi per la sola entita' DITG");
      }

      selectDitg = "select DITTAO from DITG "
          + "where NGARA5 in ( "
          + "select DITG1.NGARA5 "
          + "from DITG DITG1, EDIT "
          + "where EDIT.CODGAR4 = ? "
          + // codiceGaraDestinazione
          "and DITG.DITTAO = EDIT.CODIME "
          + "and DITG1.NGARA5 = ? ) ";
      if (!copiaOfferte)
        selectDitg += "and (DITG.ACQUISIZIONE is null or DITG.ACQUISIZIONE <> 5 ) ";

      selectDitg  += "group by NGARA5, DITTAO";
      listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
          selectDitg, new Object[] { codiceGaraDestinazione, nGaraDestinazione });

      // Eseguo la copia delle occorrenze del generatore attributi
      if (listaOccorrenzeDaCopiare != null
          && listaOccorrenzeDaCopiare.size() > 0) {
        for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {
          String dittao = ((JdbcParametro) ((HashMap<?,?>) listaOccorrenzeDaCopiare.get(row)).get("DITTAO")).getStringValue();
          this.geneManager.copiaOccorrenzeGeneratoreAttributi("DITG",
              " CODGAR5 = ? and DITTAO = ? and NGARA5 = ? ", new Object[] {
                  codiceGaraSorgente, dittao, nGaraSorgente }, new String[] {
                  "CODGAR5", "DITTAO", "NGARA5" }, new Object[] {
                  codiceGaraDestinazione, dittao, nGaraDestinazione }, false);
          // Eseguo la copia delle occorrenze di note avvisi delle entita'
          // copiate
          this.geneManager.copiaOccorrenzeNoteAvvisi("DITG",
              " CODGAR5 = ? and DITTAO = ? and NGARA5 = ? ", new Object[] {
                  codiceGaraSorgente, dittao, nGaraSorgente }, new String[] {
                  "CODGAR5", "DITTAO", "NGARA5" }, new Object[] {
                  codiceGaraDestinazione, dittao, nGaraDestinazione }, false);
        }
      }

      if (logger.isDebugEnabled())
        logger.debug("copiaDitte: fine gestione "
            + "dei campi del generatore attributi per la sola entita' DITG");

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella copia delle ditte del lotto di una gara: !",
          "copiaDitte", e);
    }
    if (logger.isDebugEnabled()) logger.debug("copiaDitte: fine metodo");
  }

  /**
   * Metodo comune ai metodi copiaTORN e copiaGARE per la copia delle entita':
   * TORN e entità figlie e gestione sia dei permessi della nuova gara che
   * delle occorrenze del generatore attributi
   *
   * @param status
   * @param codiceGaraSorgente
   * @param codiceGaraDestinazione
   * @param copiaTermini
   * @param request
   * @throws GestoreException
   */
  private void copiaTORNcomune(String codiceGaraSorgente,
      String codiceGaraDestinazione, boolean copiaTermini, HttpServletRequest request, boolean copiaDitte)
      throws GestoreException {

    DataColumnContainer campiDaCopiare = null;
    List<?> listaOccorrenzeDaCopiare = null;

    try {

      if (copiaTermini) {
        // /////////////////////////////////////
        // Copia di PUBBLI
        // /////////////////////////////////////
        if (logger.isDebugEnabled())
          logger.debug("copiaTORNcomune: inizio copia occorrenze di PUBBLI");

        listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
            "select * from PUBBLI where CODGAR9 = ? and ((tippub != 11 and tippub != 12 and tippub != 13 and tippub != 15 and tippub != 16 and tippub != 23) or tippub is null)",
            new Object[] { codiceGaraSorgente });

        if (listaOccorrenzeDaCopiare != null
            && listaOccorrenzeDaCopiare.size() > 0) {
          campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
              "PUBBLI", "select * from PUBBLI", new Object[] {});
          for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {
            campiDaCopiare.setValoriFromMap(
                (HashMap<?,?>) listaOccorrenzeDaCopiare.get(row), true);
            campiDaCopiare.getColumn("PUBBLI.CODGAR9").setChiave(true);
            campiDaCopiare.getColumn("PUBBLI.NUMPUB").setChiave(true);
            campiDaCopiare.setValue("PUBBLI.CODGAR9", codiceGaraDestinazione);

            campiDaCopiare.insert("PUBBLI", this.geneManager.getSql());
          }
        }
        if (logger.isDebugEnabled())
          logger.debug("copiaTORNcomune: fine copia occorrenze di PUBBLI");
      }

      // /////////////////////////////////////
      // Copia di GARATT
      // /////////////////////////////////////
      if (logger.isDebugEnabled())
        logger.debug("copiaTORNcomune: inizio copia occorrenze di GARATT");

      listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
          "select * from GARATT where CODGAR = ? ",
          new Object[] { codiceGaraSorgente });

      if (listaOccorrenzeDaCopiare != null
          && listaOccorrenzeDaCopiare.size() > 0) {
        campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
            "GARATT", "select * from GARATT", new Object[] {});
        for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {
          campiDaCopiare.setValoriFromMap(
              (HashMap<?,?>) listaOccorrenzeDaCopiare.get(row), true);
          campiDaCopiare.getColumn("GARATT.CODGAR").setChiave(true);
          campiDaCopiare.getColumn("GARATT.NUMATT").setChiave(true);
          campiDaCopiare.setValue("GARATT.CODGAR", codiceGaraDestinazione);

          campiDaCopiare.insert("GARATT", this.geneManager.getSql());
        }
      }
      if (logger.isDebugEnabled())
        logger.debug("copiaTORNcomune: fine copia occorrenze di GARATT");




      // /////////////////////////////////////
      // Copia di GARTECNI
      // /////////////////////////////////////
      if (logger.isDebugEnabled())
        logger.debug("copiaTORNcomune: inizio copia occorrenze di GARTECNI");

      listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
          "select * from GARTECNI where CODGAR = ? ",
          new Object[] { codiceGaraSorgente });

      if (listaOccorrenzeDaCopiare != null
          && listaOccorrenzeDaCopiare.size() > 0) {
        campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
            "GARTECNI", "select * from GARTECNI", new Object[] {});
        for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {
          campiDaCopiare.setValoriFromMap(
              (HashMap<?,?>) listaOccorrenzeDaCopiare.get(row), true);
          campiDaCopiare.getColumn("GARTECNI.CODGAR").setChiave(true);
          campiDaCopiare.getColumn("GARTECNI.NUMTEC").setChiave(true);
          campiDaCopiare.setValue("GARTECNI.CODGAR", codiceGaraDestinazione);

          campiDaCopiare.insert("GARTECNI", this.geneManager.getSql());
        }
      }

      if (logger.isDebugEnabled())
        logger.debug("copiaTORNcomune: fine copia occorrenze di GARTECNI");

      // /////////////////////////////////////
      // Copia di TORN
      // /////////////////////////////////////
      if (logger.isDebugEnabled())
        logger.debug("copiaTORNcomune: inizio copia occorrenza di TORN");

      listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
          "select * from TORN where CODGAR = ? ",
          new Object[] { codiceGaraSorgente });

      if (listaOccorrenzeDaCopiare != null
          && listaOccorrenzeDaCopiare.size() > 0) {
        campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
            "TORN", "select * from TORN", new Object[] {});
        for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {
          campiDaCopiare.setValoriFromMap(
              (HashMap<?,?>) listaOccorrenzeDaCopiare.get(row), true);
          campiDaCopiare.getColumn("TORN.CODGAR").setChiave(true);
          campiDaCopiare.setValue("TORN.CODGAR", codiceGaraDestinazione);
          campiDaCopiare.removeColumns(new String[] { "TORN.ESINEG","TORN.MODAST", "TORN.MODGAR", "TORN.TATTOC",
              "TORN.NPNOMINACOMM", "TORN.UUID", "TORN.NUMAVCP", "TORN.CLIV2"});
          //Se non è stata selezionata la copia dei termini di gara si devono escludere i seguenti campi:
          if (!copiaTermini) {
            campiDaCopiare.removeColumns(new String[] { "TORN.DTEPAR","TORN.OTEPAR",
                "TORN.DTERMRICHCDP","TORN.DTERMRISPCDP","TORN.DINVIT","TORN.NPROTI","TORN.DTEOFF",
                "TORN.OTEOFF","TORN.DTERMRICHCPO","TORN.DTERMRISPCPO","TORN.DESOFF","TORN.OESOFF","TORN.DGARA"
                ,"TORN.OGARA","TORN.DRIDOC","TORN.NRIDOC","TORN.DINDOC","TORN.DESDOC","TORN.NAVVIG",
                "TORN.DAVVIG","TORN.DPUBAV","TORN.DFPUBA","TORN.DIBAND"});
          }
          if (!copiaDitte) {
            campiDaCopiare.removeColumns(new String[] { "TORN.NGARAAQ","TORN.CODCIGAQ"});
          }

          campiDaCopiare.insert("TORN", this.geneManager.getSql());
        }
      }

      //Gestione campo CLIV2
      ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
              CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = new Long(profilo.getId());

      this.sqlManager.update("update torn set cliv2=? where codgar=? ",new Object[]{syscon, codiceGaraDestinazione});

      if (logger.isDebugEnabled())
        logger.debug("copiaTORNcomune: fine copia " + "occorrenza di TORN");


      // /////////////////////////////////////
      // Copia di GARERDA
      // /////////////////////////////////////
      if(gestioneProgrammazioneManager.isAttivaIntegrazioneProgrammazione()){
        logger.debug("copiaTORNcomune: skip copia occorrenze di GARERDA");
      }else {
      if (logger.isDebugEnabled())
        logger.debug("copiaTORNcomune: inizio copia occorrenze di GARERDA");
      listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
          "select * from GARERDA where CODGAR = ? ",
          new Object[] { codiceGaraSorgente });

      if (listaOccorrenzeDaCopiare != null
          && listaOccorrenzeDaCopiare.size() > 0) {
        DataColumnContainer campiDaCopiareGARERDA = new DataColumnContainer(this.geneManager.getSql(),
            "GARERDA", "select * from GARERDA", new Object[] {});

        int idGarerda=0;
        for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {
          campiDaCopiareGARERDA.setValoriFromMap(
              ((HashMap<?,?>) listaOccorrenzeDaCopiare.get(row)), true);
          campiDaCopiareGARERDA.getColumn("GARERDA.ID").setChiave(true);
          idGarerda=this.genChiaviManager.getNextId("GARERDA");
          campiDaCopiareGARERDA.setValue("GARERDA.ID", new Long(idGarerda));
          campiDaCopiareGARERDA.setValue("GARERDA.CODGAR", codiceGaraDestinazione);
          String campiDaNonCopiareGARERDA[] = new String[]{ "GARERDA.NUMRDA", "GARERDA.POSRDA"};
          campiDaCopiareGARERDA.removeColumns(campiDaNonCopiareGARERDA);
          campiDaCopiareGARERDA.insert("GARERDA", this.geneManager.getSql());
        }
      }
      }

      if (logger.isDebugEnabled())
        logger.debug("copiaTORNcomune: fine copia occorrenze di GARERDA");


      // Array di nomi delle entita' copiate in questo metodo
      //String[] entitaCopiate = new String[] { "PUBBLI", "TORN" };
      String[] entitaCopiate = null;
      // Array di nomi dei campi chiave delle entita' copiate in questo metodo
      //String[] campiChiaveEntitaCopiate = new String[] { "CODGAR9", "CODGAR" };
      String[] campiChiaveEntitaCopiate = null;
      if (copiaTermini) {
        entitaCopiate = new String[] { "PUBBLI", "TORN" };
        campiChiaveEntitaCopiate = new String[] { "CODGAR9", "CODGAR" };
      } else {
        entitaCopiate = new String[] { "TORN" };
        campiChiaveEntitaCopiate = new String[] { "CODGAR" };
      }

      // Eseguo la copia delle occorrenze del generatore attributi delle diverse
      // entita' copiate
      for (int i = 0; i < entitaCopiate.length; i++)
        if (!("TORN".equals(entitaCopiate[i]) && (!codiceGaraSorgente.startsWith("$") && codiceGaraDestinazione.startsWith("$")))) {
          this.geneManager.copiaOccorrenzeGeneratoreAttributi(entitaCopiate[i],
              campiChiaveEntitaCopiate[i] + " = ?",
              new Object[] { codiceGaraSorgente },
              new String[] { campiChiaveEntitaCopiate[i] },
              new Object[] { codiceGaraDestinazione }, false);
          // Eseguo la copia delle occorrenze di note avvisi delle entita'
          // copiate
          this.geneManager.copiaOccorrenzeNoteAvvisi(entitaCopiate[i],
              campiChiaveEntitaCopiate[i] + " = ?",
              new Object[] { codiceGaraSorgente },
              new String[] { campiChiaveEntitaCopiate[i] },
              new Object[] { codiceGaraDestinazione }, false);
        }
      // Gestione dei permessi della gara destinazione
      // Estraggo il gestore
      AbstractGestoreEntita gest = new DefaultGestoreEntita("TORN", request);
      gest.inserisciPermessi(campiDaCopiare, "CODGAR", new Integer(2));

    } catch (SQLException e) {
      throw new GestoreException("Errore nella copia di PUBBLI, TORN!",
          "copiaTORNcomune", e);
    }
  }

  /**
   * Estrae l'elenco dei correttivi di default
   *
   * @return array di stringhe con le 2 percentuali (la prima per lavori, la
   *         seconda per forniture e servizi) di correttivo
   */
  public String[] getCorrettiviDefault() {
    String[] correttiviDefault = new String[2];

    String descrizione = tabellatiManager.getDescrTabellato("A1029", "1");
    correttiviDefault[0] = PgManager.getNumeroFromInizioDescrizione(descrizione);
    descrizione = tabellatiManager.getDescrTabellato("A1029", "2");
    correttiviDefault[1] = PgManager.getNumeroFromInizioDescrizione(descrizione);

    return correttiviDefault;
  }

  /**
   * Estrae l'elenco degli importi delle soglie per gara
   *
   * @return array di stringhe con i due importi massimi (la prima per lavori,
   *         la seconda per forniture e servizi)
   */
  public String[] getImportiSoglieGara() {
    String[] soglie = new String[2];

    String descrizione = tabellatiManager.getDescrTabellato("A1019", "1");
    soglie[0] = PgManager.getNumeroFromInizioDescrizione(descrizione);
    descrizione = tabellatiManager.getDescrTabellato("A1019", "2");
    soglie[1] = PgManager.getNumeroFromInizioDescrizione(descrizione);

    return soglie;
  }

  /**
   * In base alla descrizione estratta dalla TAB1DESC estrae il numero definito
   * all'inizio della stringa. Nel caso di decimali, il separatore ritornato è
   * sempre il punto
   *
   * @param descrizione
   *        descrizione del tabellato
   */
  public static String getNumeroFromInizioDescrizione(String descrizione) {
    StringBuffer correttivo = new StringBuffer("");
    char carattere = ' ';
    // dalla descrizione si deve estrarre il valore numerico, considerando la
    // prima parte della stringa finchè si trovano caratteri numerici e il
    // simbolo decimale
    for (int i = 0; i < descrizione.length(); i++) {
      carattere = descrizione.charAt(i);
      if ((carattere >= '0' && carattere <= '9')) {
        correttivo.append(carattere);
      } else if ((carattere == '.' || carattere == ',')) {
        // si usa come decimale il punto sempre, anche se è indicata la virgola
        correttivo.append('.');
      } else {
        break;
      }
    }
    return correttivo.toString();
  }

  /**
   * Esegue il calcolo dell'importo del contributo da parte della stazione
   * appaltante a partire dall'importo della Gara
   *
   * @param importoGara
   *        importo gara
   * @param tabellato
   *        A1z01 per gara a lotto unico, A1z02 per gara a lotti o lotto di gara
   * @return importo del contributo, null se l'importo della gara è 0 o non
   *         valorizzato
   * @throws GestoreException
   */
  public Double getContributoAutoritaStAppaltante(Double importoGara,
      String tabellato) throws GestoreException {
    Double risultato = null;

    if (importoGara != null && importoGara.doubleValue() > 0) {
      try {
        List<?> lista = this.sqlManager.getListVector(
            "select tab2d1,tab2d2 from tab2 where tab2cod=? order by tab2tip",
            new Object[] { tabellato });
        if (lista != null && lista.size() > 0) {
          String importoContributo = null;
          String descImportoMassimoGara = null;
          int posizioneSpazio = -1;
          Double importoEstratto = null;
          double importoMassimoGara = 0;
          boolean trovato = false;
          // si cicla sui dati estratti fintantochè si rimane sotto lo scaglione
          for (int i = 0; i < lista.size() && !trovato; i++) {
            Vector<?> contributo = (Vector<?>) lista.get(i);
            importoContributo = ((JdbcParametro) contributo.get(0)).getStringValue();
            descImportoMassimoGara = ((JdbcParametro) contributo.get(1)).getStringValue();
            posizioneSpazio = descImportoMassimoGara.indexOf(' ');
            importoEstratto = null;
            if (posizioneSpazio > 0) {
              importoEstratto = UtilityNumeri.convertiDouble(StringUtils.replace(
            		  descImportoMassimoGara.substring(0, posizioneSpazio), ",", "."),
                  UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE);
            }
            // l'ultima riga non indica il limite massimo, perchè non esiste,
            // per cui si fissa un limite massimo più alto possibile in modo
            // da essere sempre sotto
            if (importoEstratto != null)
              importoMassimoGara = importoEstratto.doubleValue();
            else
              importoMassimoGara = Double.MAX_VALUE;

            if (importoGara.doubleValue() < importoMassimoGara) {
              // memorizzo il risultato, anche se non è detto che sia lo
              // scaglione giusto
              risultato = UtilityNumeri.convertiDouble(importoContributo,
                  UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE);
              trovato = true;
            }
          }
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'estrazione dei contributi",
            "getContributi", e);
      }
    }

    return risultato;
  }

  public void esclusioneDittaAltriLottiNonEsaminati(String codiceDitta,
      String codiceTornata, String codiceLotto, Long motivoEsclusioneDitta,
      String dettaglioMotivoEsclusione) throws GestoreException, SQLException {

    // 11/05/2010 L.G.: questo metodo e' stato modificato gestendo la
    // storicizzazione
    // di AMMGAR nella tabella figlia DITGAMMIS solo perche' il codice esistente
    // gestisce l'aggiornamento di esclusione di una ditta in un lotto anche
    // negli
    // altri lotti, per gare a lotti "OMOGENEE"
    // A livello normativo sembra che le gare omogenee non siano piu' previste,
    // quindi questo metodo dovra' essere commentato (o meglio cancellato)
    // quanto prima
    List<?> listaDittaDaEscludereDaAltriLotti = this.sqlManager.getListVector(
        "select DITG.NGARA5, GARE.FASGAR from DITG, GARE "
            + "where DITG.NGARA5 = GARE.NGARA "
            + "and DITG.NGARA5 <> ? "
            + "and DITG.CODGAR5 = ? "
            + "and DITG.DITTAO = ? "
            + "and (DITG.AMMGAR is null or DITG.AMMGAR = 0) ", new Object[] {
            codiceLotto, codiceTornata, codiceDitta });

    if (listaDittaDaEscludereDaAltriLotti != null
        && listaDittaDaEscludereDaAltriLotti.size() > 0) {

      for (int i = 0; i < listaDittaDaEscludereDaAltriLotti.size(); i++) {
        Vector<?> tmpVector = (Vector<?>) listaDittaDaEscludereDaAltriLotti.get(i);
        String strUpdate = "update DITG "
            + "set AMMGAR = 2, RIBAUO = null, IMPOFF = null, "
            + "PUNTEC = null, PUNECO = null, "
            + "FASGAR = ?, MOTIES = ?, ANNOFF = ? "
            + "where DITG.NGARA5  = ? "
            + "and DITG.CODGAR5 = ? "
            + "and DITG.DITTAO  = ? ";

        // Il campo DITG.FASGAR (fase di esclusione della ditta) viene impostato
        // in funzione della fase del lotto a cui la ditta appartiene. In
        // particolare:
        // - se GARE.FASGAR di destinazione < 2 o nullo: DITG.FASGAR = 1;
        // - se GARE.FASGAR di destinazione >= 2: DITG.FASGAR = 2;
        Long fasGarDestinazione = ((JdbcParametro) tmpVector.get(1)).longValue();
        Long valoreFasGar = PgManager.checkFaseGaraPerEsclusione(fasGarDestinazione);
        String codiceAltroLotto = ((JdbcParametro) tmpVector.get(0)).getStringValue();

        this.sqlManager.update(strUpdate, new Object[] { valoreFasGar,
            motivoEsclusioneDitta, dettaglioMotivoEsclusione, codiceAltroLotto,
            codiceTornata, codiceDitta });

        this.aggiornaDITGAMMIS(codiceTornata, codiceAltroLotto, codiceDitta,
            valoreFasGar, new Long(2), motivoEsclusioneDitta,
            dettaglioMotivoEsclusione, true, true, true);
      }
    }
  }

  /**
   * Metodo per aggiornamento della tabella DITGAMMIS per storicizzare le
   * operazioni di esclusione di una ditta da un lotto.
   *
   * @param codiceTornata
   * @param codiceLotto
   * @param codiceDitta
   * @param valoreFasGar
   * @param ammgar
   * @param motivoEsclusioneDitta
   * @param dettaglioMotivoEsclusione
   * @param aggiornaAmmgar
   *        se true aggiorna il campo AMMGAR
   * @param aggiornaMotivEscl
   *        se true aggiorna il campo MOTIVESCL
   * @param aggiornaDetMotEscl
   *        se true aggiorna il campo DETMOTESCL
   * @throws SQLException
   */
  public void aggiornaDITGAMMIS(String codiceTornata, String codiceLotto,
      String codiceDitta, Long valoreFasGar, Long ammgar,
      Long motivoEsclusioneDitta, String dettaglioMotivoEsclusione,
      boolean aggiornaAmmgar, boolean aggiornaMotivEscl,
      boolean aggiornaDetMotEscl) throws SQLException {

    // Aggiornamento nella DITGAMMIS: per la storicizzazione dello stato
    // di ammissione della ditta alla gara si deve effettuare insert o
    // update nella tabella DITGAMMIS
    // (S.Santi 27.12.11) Se l'aggiornamento è relativo a MOTIES=99 o 100
    // (esclusione o riammissione in fase di aggiudicazione negli altri lotti
    // della gara se gara omogenea),
    // vengono prima cancellate le eventuali occ. in DITGAMMIS in modo da
    // inserire sempre quella allineata con DITG
    // Questa esigenza non si pone nel caso di esclusione in seguito alla
    // modifica di AMMGAR perchè in quel caso
    // l'aggiornamento riguarda solo le ditte con AMMGAR ancora nullo.
    if (motivoEsclusioneDitta != null
        && (motivoEsclusioneDitta.longValue() == 99 || motivoEsclusioneDitta.longValue() == 100)) {
      this.sqlManager.update(
          "delete from ditgammis where codgar=? and ngara=? and dittao=?"
              + " and (ammgar in (2,6) or fasgar>=?)", new Object[] {
              codiceTornata, codiceLotto, codiceDitta, valoreFasGar });
    }

    if (this.geneManager.countOccorrenze("DITGAMMIS",
        "CODGAR=? and NGARA=? and DITTAO=? and FASGAR=?", new Object[] {
            codiceTornata, codiceLotto, codiceDitta, valoreFasGar }) == 0) {

      // Esecuzione operazione di insert su DITGAMMIS
      this.sqlManager.update(
          "insert into DITGAMMIS (CODGAR, NGARA, DITTAO, "
              + "FASGAR, AMMGAR, MOTIVESCL, DETMOTESCL) values (?, ?, ?, ?, ?, ?, ?)",
          new Object[] { codiceTornata, codiceLotto, codiceDitta, valoreFasGar,
              ammgar, motivoEsclusioneDitta, dettaglioMotivoEsclusione });
    } else {
      if (aggiornaMotivEscl || aggiornaDetMotEscl || aggiornaAmmgar) {
        // Sql di update di DITGAMMIS
        String strUpdateDITGAMMIS = "update DITGAMMIS set CAMPI where CODGAR=? "
            + "and NGARA=? and DITTAO=? and FASGAR = ?";
        StringBuffer strBuffer = new StringBuffer("");

        // Oggetto per contenere i parametri per l'update
        List<Object> listaParametriUpdate = new ArrayList<Object>();

        if (aggiornaAmmgar) {
          strBuffer.append("AMMGAR=? ,");
          listaParametriUpdate.add(ammgar);
        }
        if (aggiornaMotivEscl) {
          strBuffer.append("MOTIVESCL=? ,");
          listaParametriUpdate.add(motivoEsclusioneDitta);
        }
        if (aggiornaDetMotEscl) {
          strBuffer.append("DETMOTESCL=? ,");
          listaParametriUpdate.add(dettaglioMotivoEsclusione);
        }

        listaParametriUpdate.add(codiceTornata);
        listaParametriUpdate.add(codiceLotto);
        listaParametriUpdate.add(codiceDitta);
        listaParametriUpdate.add(valoreFasGar);

        strUpdateDITGAMMIS = strUpdateDITGAMMIS.replaceAll("CAMPI",
            strBuffer.substring(0, strBuffer.length() - 1));

        // Esecuzione operazione di update su DITGAMMIS
        this.sqlManager.update(strUpdateDITGAMMIS,
            listaParametriUpdate.toArray());
      }
    }
  }

  /**
   * Nell'operazione di esclusione ditta in altri perche' vincitrice del lotto
   * di gara attuale, il campo DITG.FASGAR (fase di esclusione della ditta)
   * viene impostato in funzione della fase del lotto a cui la ditta appartiene.
   * In particolare: - se GARE.FASGAR di destinazione < 2 o nullo: DITG.FASGAR =
   * 1; - se GARE.FASGAR di destinazione >= 2: DITG.FASGAR = 2;
   *
   * @param fasGarDestinazione
   * @return Ritorna la fase di gara corretta per l'operazione di esclusione di
   *         una ditta in lotti diversi dal lotto in analisi
   */
  public static Long checkFaseGaraPerEsclusione(Long fasGarDestinazione) {
    if (fasGarDestinazione == null) fasGarDestinazione = new Long(1);

    // Variabile di appoggio che conterra' il valore del campo DITG.FASGAR
    Long valoreFasGar = null;
    if (fasGarDestinazione.intValue() < 2)
      valoreFasGar = new Long(1);
    else
      valoreFasGar = new Long(2);
    return valoreFasGar;
  }

  /**
   *
   * @param numeroGara
   * @param operazione
   * @param gestioneBustaAmm
   * @param modlicg
   * @param valtec
   * @throws SQLException
   */
  public void updateChiusuraAperturaFasiRicezione(String numeroGara,
      String operazione,boolean gestioneBustaAmm, Long modlicg, String valtec) throws SQLException {

    Long fasgar=null;
    Long stepgar=null;
    if ("ATTIVA".equals(operazione)) {
      if(gestioneBustaAmm) {
        fasgar = new Long(GestioneFasiGaraFunction.FASE_APERTURA_DOCUM_AMMINISTR / 10);
        stepgar = new Long(GestioneFasiGaraFunction.FASE_APERTURA_DOCUM_AMMINISTR);
      }else {
        if(new Long(6).equals(modlicg) || "1".equals(valtec)) {
          fasgar = new Long(GestioneFasiGaraFunction.FASE_VALUTAZIONE_TECNICA / 10);
          stepgar = new Long(GestioneFasiGaraFunction.FASE_VALUTAZIONE_TECNICA);
        }else {
          fasgar = new Long(GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE / 10);
          stepgar = new Long(GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE);
        }

      }
      this.sqlManager.update(
          "update GARE set FASGAR = ?, STEPGAR = ? where NGARA = ? ",
          new Object[] { fasgar, stepgar, numeroGara }, 1);
    } else if ("DISATTIVA".equals(operazione)) {

      this.sqlManager.update(
          "update GARE set FASGAR = ?, STEPGAR = ? where NGARA = ? ",
          new Object[] {
              new Long(GestioneFasiRicezioneFunction.FASE_RICEZIONE_PLICHI / 10),
              new Long(
                  GestioneFasiRicezioneFunction.FASE_RICEZIONE_PLICHI),
              numeroGara }, 1);

      //si devono sbiancare fasgar e stepgar dei lotti
      this.sqlManager.update(
          "update GARE set FASGAR = null, STEPGAR = null where CODGAR1=? and NGARA != CODGAR1 ",
          new Object[] {numeroGara});

    }
  }

  /**
   * Ritorna il codice del tabellato per la gestione della percentuale cauzione
   * provvisoria
   *
   * @param tipgen
   *        tipo di appalto
   * @return codice del tabellato
   */
  public static String getTabellatoPercCauzioneProvvisoria(int tipgen) {
    String tabellato = null;
    switch (tipgen) {
    case 1:
      tabellato = "A1017";
      break;
    case 2:
      tabellato = "A1020";
      break;
    case 3:
      tabellato = "A1021";
      break;
    }
    return tabellato;
  }

  /**
   * Determina l'arrotondamento da applicare nel calcolo della cauzione
   * provvisoria a seconda del tipo di appalto
   *
   * @param tipgen
   *        tipo di appalto
   * @return numero di decimali da utilizzare nell'arrotondamento (0 = 0
   *         decimali, 2 = 2 decimali)
   * @throws GestoreException
   */
  public int getArrotondamentoCauzioneProvvisoria(int tipgen)
      throws GestoreException {
    int numeroDecimali = 2;

    String tabellato = PgManager.getTabellatoPercCauzioneProvvisoria(tipgen);
    String sql = "select tab1desc from tab1 where tab1cod = ? and tab1tip = 2";
    try {
      String descrArrotondamento = (String) this.sqlManager.getObject(sql,
          new Object[] { tabellato });
      // a seconda che inizi per 1 o 2 si determina se va fatto
      // l'arrotondamento
      // all'interno oppure no
      if (descrArrotondamento.charAt(0) == '1') {
        numeroDecimali = 0;
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante il calcolo della garanzia provvisoria",
          "calcolaCauzProvv", e);
    }

    return numeroDecimali;
  }

  public void verificaPreliminareDatiCopiaGara(String codiceGaraSorgente,
      String codiceGaraDestinazione, String nGaraSorgente,
      String nGaraDestinazione, boolean copiaComeLotto) throws GestoreException {

    String tmpCodGarDestinazione = new String(codiceGaraDestinazione);

    if (codiceGaraSorgente != null && codiceGaraSorgente.length() > 0) {
      // Verifico l'esistenza del codice gara sorgente
      try {
        List<?> ret = this.sqlManager.getVector(
            "select 1 from torn where codgar = ?",
            new Object[] { codiceGaraSorgente });
        if (ret == null || (ret != null && ret.size() == 0))
          throw new GestoreException(
              "La gara sorgente non esiste nella basi dati",
              "verificaPreCopiaGara.codiceGaraSorgenteNonEsistente");
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella selezione della gara di sorgente",
            "verificaPreCopiaGara", e);
      }
    }

    if (copiaComeLotto) {
      // Verifico l'esistenza del codice gara destinazione per la copia di un
      // lotto di gara
      try {
        List<?> ret = this.sqlManager.getVector(
            "select 1 from torn where codgar = ?",
            new Object[] { codiceGaraDestinazione });
        if (ret == null || (ret != null && ret.size() == 0))
          throw new GestoreException(
              "La gara di destinazione a cui bisogna copiare il lotto "
                  + nGaraSorgente
                  + " non esiste nella basi dati",
              "verificaPreCopiaGara.codiceGaraDestinazioneNonEsistente");
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella selezione della gara di destinazione nella copia "
                + "di un lotto di gara", "verificaPreCopiaGara", e);
      }
    } else {
      if (tmpCodGarDestinazione.startsWith("$"))
        tmpCodGarDestinazione = tmpCodGarDestinazione.substring(1);

      // Verifico la non esistenza del codice gara destinazione
      try {
        List<?> ret = this.sqlManager.getVector(
            "select 1 from TORN where CODGAR = ? or CODGAR = ?", new Object[] {
                tmpCodGarDestinazione, "$".concat(tmpCodGarDestinazione) });

        List<?> ret1 = this.sqlManager.getVector(
            "select 1 from GARE where NGARA = ?",
            new Object[] { tmpCodGarDestinazione });

        if ((ret != null && ret.size() > 0)
            || (ret1 != null && ret1.size() > 0))
          throw new GestoreException("La gara di destinazione è già esistente",
              "verificaPreCopiaGara.codiceGaraDestinazioneEsistente");
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella selezione della gara di destinazione",
            "verificaPreCopiaGara", e);
      }
    }

    if (nGaraSorgente != null && nGaraSorgente.length() > 0) {
      // Verifico l'esistenza del numero di gara sorgente
      try {
        List<?> ret = this.sqlManager.getVector(
            "select 1 from GARE where NGARA = ?",
            new Object[] { nGaraSorgente });
        if (ret == null || (ret != null && ret.size() == 0))
          throw new GestoreException(
              "Il numero della gara sorgente non esiste nella basi dati",
              "verificaPreCopiaGara.nGaraSorgenteNonEsistente");
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella selezione del numero della gara di sorgente",
            "verificaPreCopiaGara", e);
      }
    }

    if (nGaraDestinazione != null && nGaraDestinazione.length() > 0) {
      // Verifico la non esistenza del numero di gara destinazione
      try {
        List<?> ret = this.sqlManager.getVector(
            "select 1 from TORN where CODGAR = ? or CODGAR = ?", new Object[] {
                nGaraDestinazione, "$".concat(nGaraDestinazione) });

        List<?> ret1 = this.sqlManager.getVector(
            "select 1 from GARE where NGARA = ?",
            new Object[] { nGaraDestinazione });

        if ((ret != null && ret.size() > 0)
            || (ret1 != null && ret1.size() > 0))
          throw new GestoreException(
              "Il numero della gara di destinazione è già esistente",
              "verificaPreCopiaGara.nGaraDestinazioneEsistente");
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella selezione del numero della gara di destinazione",
            "verificaPreCopiaGara", e);
      }
    }
  }

  /**
   * @return la modalita' di inserimento della ditta in funzione del valore del
   *         tabellato A1041 presente nella tab1
   */
  public boolean isModalitaNPROGGsuLotto() {
    boolean result = false;
    String descTab = this.tabellatiManager.getDescrTabellato("A1041", "1");
    if (descTab != null) result = descTab.startsWith("1");
    return result;
  }

  /**
   * Aggiornamento del campo RITDOM oppure RITOFF oppure RITREQ in funzione del
   * tipo di protocollo dopo la stampa della lista dei plichi ritirati
   * dall'utente
   *
   * @param tipoProtocollo
   * @param dataProtocollo
   * @param operatoreDataProtocollo
   * @param idUtente
   * @throws SQLException
   * @throws GestoreException
   */
  public void updateRitiroDefinitivo(String tipoProtocollo,
      String dataProtocollo, String operatoreDataProtocollo, int idUtente)
      throws SQLException, GestoreException {

    List<Object> listaParametri = new ArrayList<Object>();

    StringBuffer sqlSelect = new StringBuffer(
        "select CODGAR, NGARA, TIPPROT, DITTAO "
            + "from V_DITTE_PRIT "
            + "where RITIRO = ? "
            + "and UTENTE = ? ");

    listaParametri.add(new Long(1));
    listaParametri.add(new Long(idUtente));
    if (tipoProtocollo != null && tipoProtocollo.length() > 0) {
      sqlSelect.append("and TIPPROT = ? ");
      listaParametri.add(new Long(tipoProtocollo));
    }

    if (dataProtocollo != null && dataProtocollo.length() > 0) {
      sqlSelect.append(" and DATAP " + operatoreDataProtocollo + " ? ");
      listaParametri.add(new Timestamp(UtilityDate.convertiData(dataProtocollo,
          UtilityDate.FORMATO_GG_MM_AAAA).getTime()));
    }

    List<?> listaDitte = this.sqlManager.getListVector(sqlSelect.toString(),
        listaParametri.toArray());

    if (listaDitte != null && listaDitte.size() > 0) {
      for (Iterator<?> iterator = listaDitte.iterator(); iterator.hasNext();) {
        Vector<?> ditta = (Vector<?>) iterator.next();
        String codgar = ((JdbcParametro) ditta.get(0)).getStringValue();
        String ngara = ((JdbcParametro) ditta.get(1)).getStringValue();
        Double tipprot = ((JdbcParametro) ditta.get(2)).doubleValue();
        String dittao = ((JdbcParametro) ditta.get(3)).getStringValue();

        String campoRitiro = "";
        switch (tipprot.intValue()) {
        case 1:
          campoRitiro = "RITDOM";
          break;
        case 2:
          campoRitiro = "RITOFF";
          break;
        case 3:
          campoRitiro = "RITREQ";
          break;
        }

        String sqlUpdate = "update DITG set "
            + campoRitiro
            + " = ? "
            + "where CODGAR5 = ? and NGARA5 = ? AND DITTAO = ? ";

        this.sqlManager.update(sqlUpdate, new Object[] { new Integer(2),
            codgar, ngara, dittao }, 1);
      }
    }
  }

  /**
   * Ritorna il valore della property
   * it.eldasoft.stampe.<idApplicazione>.<codiceApplicazione>.server, la quale
   * rappresenta per il server la cartella destinata a contenere le stampe
   * prodotte dall'applicativo. Se valorizzata, altrimenti ritorna null
   *
   * @return Ritorna il valore della property it.eldasoft.stampe.server
   */
  public String getPathStampeLatoServer() {
    // Path del file appena composto lato server
    String pathFile = ConfigManager.getValore("it.eldasoft.stampe.server");

    if (pathFile != null && pathFile.length() > 0)
      return pathFile;
    else
      return null;
  }

  /**
   * Ritorna il valore della property
   * it.eldasoft.stampe.<idApplicazione>.<codiceApplicazione>.client, la quale
   * rappresenta per il client la cartella destinata a contenere le stampe
   * prodotte dall'applicativo. Se valorizzata, altrimenti ritorna null
   *
   * @return Ritorna il valore della property it.eldasoft.stampe.client
   */
  public String getPathStampeLatoClient() {
    // Path del file appena composto lato client
    String pathFile = ConfigManager.getValore("it.eldasoft.stampe.client");

    if (pathFile != null && pathFile.length() > 0)
      return pathFile;
    else
      return null;
  }

  public String[] getValoriEtichettaProtocollo(String codiceGara,
      String codiceDitta, String numeroGara, int tipoProtocollo)
      throws SQLException {
    String[] result = new String[6];

    HashMap<?,?> valoriDB = this.sqlManager.getHashMap(
        "select NPROGG, DRICIND, ORADOM, NPRDOM, DATOFF, ORAOFF, NPROFF, DATREQ, NPRREQ ,ORAREQ,"
            + " DITTAO,NOMIMO from ditg "
            + "where CODGAR5 = ? and DITTAO = ? and NGARA5 = ?", new Object[] {
            codiceGara, codiceDitta, numeroGara });

    if (valoriDB != null) {
      result[0] = ((JdbcParametro) valoriDB.get("NPROGG")).getStringValue();

      switch (tipoProtocollo) {
      case 1:
        result[1] = ((JdbcParametro) valoriDB.get("DRICIND")).getStringValue();
        result[2] = ((JdbcParametro) valoriDB.get("NPRDOM")).getStringValue();
        result[3] = ((JdbcParametro) valoriDB.get("ORADOM")).getStringValue();
        break;
      case 2:
        result[1] = ((JdbcParametro) valoriDB.get("DATOFF")).getStringValue();
        result[2] = ((JdbcParametro) valoriDB.get("NPROFF")).getStringValue();
        result[3] = ((JdbcParametro) valoriDB.get("ORAOFF")).getStringValue();
        break;
      case 3:
        result[1] = ((JdbcParametro) valoriDB.get("DATREQ")).getStringValue();
        result[2] = ((JdbcParametro) valoriDB.get("NPRREQ")).getStringValue();
        result[3] = ((JdbcParametro) valoriDB.get("ORAREQ")).getStringValue();
        break;
      }
      result[4] = ((JdbcParametro) valoriDB.get("DITTAO")).getStringValue();
      result[5] = ((JdbcParametro) valoriDB.get("NOMIMO")).getStringValue();

      return result;
    } else
      return null;
  }

  /**
   * Calcola della somma dei punteggi tecnici della gara
   *
   * @param ngara
   * @return Ritorna la somma dei punteggi tecnici della gara
   * @throws SQLException
   */
  public Double getSommaPunteggioTecnico(String ngara) throws SQLException {
    Double result = this.getSommaPunteggi(ngara, new Long(1), "");
    return result;
  }

  /**
   * Calcola della somma dei punteggi economici della gara
   *
   * @param ngara
   * @return Ritorna la somma dei punteggi economici della gara
   * @throws SQLException
   */
  public Double getSommaPunteggioEconomico(String ngara) throws SQLException {
    Double result = this.getSommaPunteggi(ngara, new Long(2), "");
    return result;
  }

  /**
   * Calcola della somma dei punteggi specificando tippar ed un eventuale filtro
   *
   * @param ngara
   * @param tippar
   * @param filtro
   * @return Ritorna la somma dei punteggi della gara
   * @throws SQLException
   */
  public Double getSommaPunteggi(String ngara, Long tippar, String filtro) throws SQLException{
    Double result = null;
    BigDecimal sommaPunteggi = new BigDecimal(0);
    boolean punteggioImpostato = false;
    String select= "select MAXPUN from GOEV where NGARA = ? "
        + "and (LIVPAR = 1 or LIVPAR = 3) and TIPPAR = ? ";
    if(!"".equals(filtro) && filtro !=null)
      select += " and " + filtro;
    List<?> listaMaxpunTecnico = this.sqlManager.getListHashMap(
        select, new Object[] {
            ngara, tippar });
    if (listaMaxpunTecnico != null && listaMaxpunTecnico.size() > 0) {
      Double tmp = null;
      for (int i = 0; i < listaMaxpunTecnico.size(); i++) {
        tmp = (Double) ((JdbcParametro) ((HashMap<?,?>) listaMaxpunTecnico.get(i)).get("MAXPUN")).getValue();
        if (tmp != null) {
          BigDecimal bigMaxpun= BigDecimal.valueOf(tmp);
          sommaPunteggi = sommaPunteggi.add(bigMaxpun);
          punteggioImpostato = true;
        }
      }
      if (punteggioImpostato) result = new Double(sommaPunteggi.doubleValue());
    }
    return result;
  }

  /**
   * Calcola della somma dei punteggi tecnici della gara raggruppando per SEZTEC
   *
   * @param ngara
   * @return Ritorna la somma dei punteggi tecnici della gara
   * @throws SQLException
   */
  public Double[] getSommaPunteggiTecniciSez(String ngara) throws SQLException {
    Double result[] = new Double[2];
    result[0] = this.getSommaPunteggi(ngara, new Long(1), "SEZTEC = '1'");
    result[1] = this.getSommaPunteggi(ngara, new Long(1), "SEZTEC = '2'");
    return result;
  }

  /**
   * Funzione che esegue il calcolo delle codifica automatica specifica per le
   * gare d'appalto. Questo metodo e' stato sviluppato a partire dal metodo
   * GeneManager.calcolaCodificaAutomatica del progetto Gene e rappresenta una
   * customizzazione. Infatti generare il codice gara non è univoco, visto che
   * le entita' in gioco sono due (TORN e GARE) rispettivamente per gare a lotti
   * e gare a lotto unico. Inoltre il numero della gara, per un lotto di gara,
   * e' anch'esso da generare in automatico.
   *
   * @param entita
   *        Entita = GARE o TORN
   * @param isGaraLottoUnico
   * @param codiceGaraLotti
   *        Codice della gara a lotti alla quale si sta creando un nuovo lotto
   *
   * @return HashMap contenente due oggetti associati alle chiavi 'codiceGara' e
   *         'numeroGara'
   * @throws GestoreException
   */
  public HashMap<String,String> calcolaCodificaAutomatica(String entita,
      Boolean isGaraLottoUnico, String codiceGaraLotti, Long progressivoLotto)
      throws GestoreException {
    // HashMap per contenere due stringhe che rappresentano CODGAR e NGARA
    // con chiave 'codiceGara' e 'numeroGara' rispettivamente
    HashMap<String,String> result = new HashMap<String,String>();

    String codiceGara = null;
    String numeroGara = null;

    if (entita != null && entita.length() > 0) {
      if ("GARE".equals(entita.toUpperCase())
          || "GAREALBO".equalsIgnoreCase(entita)
          || "MECATALOGO".equalsIgnoreCase(entita)) {
        if (isGaraLottoUnico != null) {
          if (isGaraLottoUnico.booleanValue()) {
            // Generare CODGAR e NGARA per gara a lotto unico, dove CODGAR = "$"
            // + NGARA.
            // NGARA lo si genera usando il record di G_CONFCOD relativo alla
            // entita TORN
            if ("GARE".equals(entita.toUpperCase())) {
              codiceGara = this.getCodiceGaraCodificaAutomatica(true, "TORN");
            } else {
              codiceGara = this.getCodiceGaraCodificaAutomatica(true, entita);
            }
            numeroGara = codiceGara.substring(1);
          } else {
            if (codiceGaraLotti != null && codiceGaraLotti.length() > 0) {
              // Generare NGARA per il lotto di una gara divisa in lotti, dove
              // NGARA = CODGAR + suffisso generato usando il record di
              // G_CONFCOD
              // relativo all'entita' GARE
              numeroGara = this.getNumeroGaraCodificaAutomatica(
                  codiceGaraLotti, progressivoLotto,"GARE","NGARA");
              codiceGara = new String(codiceGaraLotti);
            } else
              throw new GestoreException(
                  "Errore nel calcolo del numero della "
                      + "gara (GARE.NGARA) per un nuovo lotto della gara a partire "
                      + "dal codice della gara a lotti di appartenenza: '"
                      + codiceGaraLotti
                      + "'", null);
          }
        } else
          throw new NullPointerException(
              "L'argomento isGaraLottoUnico e' nullo");
      } else if ("TORN".equals(entita.toUpperCase())) {
        // Generare CODGAR per gara divisa in lotti dove CODGAR lo si genera
        // usando il record di G_CONFCOD relativo all'entita' TORN
        codiceGara = this.getCodiceGaraCodificaAutomatica(false, "TORN");
      } else
        throw new GestoreException("Errore nel calcolo codifica automatica: "
            + "l'entita "
            + entita
            + " non e' valida per il calcolo del "
            + "codice gara (CODGAR e/o NGARA).", null);
    } else {
      throw new GestoreException("Errore nel calcolo codifica automatica: "
          + "l'entita non specificata per il calcolo del codice gara "
          + "(CODGAR e/o NGARA).", null);
    }

    result.put("codiceGara", codiceGara);
    result.put("numeroGara", numeroGara);
    return result;
  }

  /**
   * Genera il codice della gara secondo il criterio nella tabella G_CONFCOD
   *
   * @param isGaraLottoUnico
   * @return Ritorna il codice della gara (GARE.CODGAR o TORN.CODGAR)
   *
   * @throws GestoreException
   */
  private String getCodiceGaraCodificaAutomatica(boolean isGaraLottoUnico,
      String entita) throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("getCodiceGaraCodificaAutomatica: inizio metodo");

    String campo = "CODGAR";

    String result = null;
    try {
      JdbcParametro parametroCriterioCODGAR = null;
      Long contatore = null;
      HashMap<?,?> parametri = this.geneManager.getParametriCodificaAutomatica(
          entita, campo);

      if (parametri != null) {
        contatore = (Long) parametri.get("contatore");
        parametroCriterioCODGAR = (JdbcParametro) parametri.get("parametro");
      }

      if (parametroCriterioCODGAR != null) {
        String criterioCODGAR = parametroCriterioCODGAR.stringValue();
        long tmpContatore = 0;
        if (contatore != null) tmpContatore = contatore.longValue();

        boolean codiceUnivoco = false;
        int numeroTentativi = 0;
        StringBuffer strBuffer = null;

        // tento di inserire il record finchè non genero un codice univoco a
        // causa della concorrenza, o raggiungo il massimo numero di tentativi
        while (!codiceUnivoco
            && numeroTentativi < CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {

          strBuffer = new StringBuffer("");
          tmpContatore++;
          this.sqlManager.update("update G_CONFCOD set CONTAT = ? "
              + "where NOMENT = ? "
              + "and NOMCAM = ? ", new Object[] { new Long(tmpContatore),
              entita, campo });

          strBuffer = this.geneManager.calcoloCodiceAutomatico(tmpContatore,
              criterioCODGAR);

          try {
            if (isGaraLottoUnico)
              result = new String("$" + strBuffer);
            else
              result = new String(strBuffer);

            this.verificaPreliminareDatiCopiaGara(null, result, null, null,
                false);
            codiceUnivoco = true;
          } catch (GestoreException g) {
            if ("verificaPreCopiaGara.codiceGaraDestinazioneEsistente".equals(g.getCodice())) {
              numeroTentativi++;
              // Ripristino il valore del criterio con cui calcolare codice
              // della gara
              criterioCODGAR = parametroCriterioCODGAR.stringValue();
            }
          }
        }

        if (!codiceUnivoco
            && numeroTentativi >= CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {
          throw new GestoreException("Codifica automatica non riuscita dopo "
              + CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT
              + " tentativi a causa del disallineamento del contatore è "
              + "rispetto ai codici inseriti", "erroreCodiceCodificaAutomatica");
        }

        if (logger.isDebugEnabled())
          logger.debug("getCodiceGaraCodificaAutomatica: fine metodo");
        return result;
      } else {
        throw new GestoreException("Codice di codifica automatica sbagliato",
            "erroreCodiceCodificaAutomatica");
      }
    } catch (GestoreException e) {
      throw e;
    } catch (Throwable t) {
      throw new GestoreException(
          "Errore nel calcolo della codifica automatica per "
              + entita
              + "."
              + campo, "getCodiceGaraCodificaAutomatica", t);
    }
  }


  /**
   * Viene calcolato il codice della gara, prendendo in considerazione la regola definita
   * in G_CONFCOD per l'entita ed il campo specificati.
   * Viene adoperato per aggiungere il suffisso dei lotti o dei rilanci
   *
   * @param codiceGara
   * @param progressivoSuffisso
   * @param entita
   * @param campo
   * @return
   * @throws GestoreException
   */
  public String getNumeroGaraCodificaAutomatica(String codiceGara,
      Long progressivoSuffisso, String entita, String campo) throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("getNumeroGaraCodificaAutomatica: inizio metodo");

    // Numero massimo di tentativi per generare il numero della gara
    int NUMERO_TENTATIVI_NGARA = 25;

    String result = null;
    //String entita = "GARE";
    //String campo = "NGARA";
    String tmpCodiceGara = new String(codiceGara);

    int maxLungCodiceGara = 14; // Costante, a meno della valorizzazione del
                                // tabellato A1050
    String strMaxLunghezzaCodiceGara = tabellatiManager.getDescrTabellato(
        "A1050", "1");
    if (strMaxLunghezzaCodiceGara != null
        && strMaxLunghezzaCodiceGara.length() > 0) {
      Integer tmp1 = UtilityNumeri.convertiIntero(strMaxLunghezzaCodiceGara.substring(
          0, strMaxLunghezzaCodiceGara.indexOf(" ")));
      if (tmp1 != null) {
        if (tmp1.intValue() > 0 && tmp1.intValue() < 20)
          maxLungCodiceGara = tmp1.intValue();
      }
    }

    if (codiceGara.length() > maxLungCodiceGara)
      tmpCodiceGara = tmpCodiceGara.substring(0, maxLungCodiceGara);

    JdbcParametro parametroCriterioNGARA = null;
    Long contatore = null;

    try {
      HashMap<?,?> parametri = this.geneManager.getParametriCodificaAutomatica(
          entita, campo);

      if (parametri != null) {
        contatore = (Long) parametri.get("contatore");
        parametroCriterioNGARA = (JdbcParametro) parametri.get("parametro");
      }

      if (progressivoSuffisso == null) {
        // Per capire da che numero far partire il contatore, si determina il
        // numero di lotti esistenti e il contatore lo si incrementa di uno

        // oppure lo si fa partire da zero
        // MOD SS101109 - Non considera nel conteggio dei lotti l'eventuale
        // occ. in GARE complem.a TORN per le gare a offerta unica
        String selectConteggio= "select count(*) from GARE where CODGAR1 = ? and "
            + "(GENERE is null or GENERE<>3)";

        if("PRECED".equals(campo))
          selectConteggio= "select count(ngara) from GARE where preced = ?";
        Long numeroLottiEsistenti = (Long) this.sqlManager.getObject(
            selectConteggio, new Object[] { codiceGara });

        if (numeroLottiEsistenti != null
            && numeroLottiEsistenti.longValue() > 0)
          contatore = new Long(numeroLottiEsistenti.longValue());
        else
          contatore = new Long(0);
      } else
        // Decremento il progressivo del lotto, perche' nel calcolo vero e
        // proprio del codice lotto viene subito incrementato
        contatore = new Long(progressivoSuffisso.longValue() - 1);

      if (parametroCriterioNGARA != null) {
        String criterioNGARA = parametroCriterioNGARA.stringValue();
        long tmpContatore = contatore.longValue();
        boolean codiceUnivoco = false;
        int numeroTentativi = 0;
        StringBuffer strBuffer = null;

        while (!codiceUnivoco && numeroTentativi < NUMERO_TENTATIVI_NGARA) {

          strBuffer = new StringBuffer("");
          tmpContatore++;

          strBuffer = this.geneManager.calcoloCodiceAutomatico(tmpContatore,
              criterioNGARA);
          result = tmpCodiceGara + new String(strBuffer);


          try {
            if("PRECED".equals(campo)){
              //Si deve effettuare la verifica considerando che la gara di rilancio è un lotto unico
              this.verificaPreliminareDatiCopiaGara(null, "$" + result, null,
                  null, false);
            }else{
              //Si deve effettuare la verifica considerando che la gara è un lotto
              this.verificaPreliminareDatiCopiaGara(null, codiceGara, null,
                  result, true);
            }
              codiceUnivoco = true;
          } catch (GestoreException g) {
            if ("verificaPreCopiaGara.codiceGaraDestinazioneEsistente".equals(g.getCodice())) {
              numeroTentativi++;
              // Ripristino il valore del criterio con cui calcolare codice
              // della gara
              criterioNGARA = parametroCriterioNGARA.stringValue();
            }
          }
        }

        if (!codiceUnivoco && numeroTentativi >= NUMERO_TENTATIVI_NGARA) {
          throw new GestoreException("Codifica automatica non riuscita dopo "
              + NUMERO_TENTATIVI_NGARA
              + " tentativi a causa del disallineamento del contatore è "
              + "rispetto ai codici inseriti", "erroreCodiceCodificaAutomatica");
        }

        if (logger.isDebugEnabled())
          logger.debug("getNumeroGaraCodificaAutomatica: fine metodo");

        return result;
      } else {
        throw new GestoreException("Codice di codifica automatica sbagliato",
            "erroreCodiceCodificaAutomatica");
      }
    } catch (GestoreException e) {
      throw e;
    } catch (Throwable t) {
      throw new GestoreException(
          "Errore nel calcolo della codifica automatica per "
              + entita
              + "."
              + campo, "getNumeroGaraCodificaAutomatica", t);
    }
  }

  /**
   * Aggiorna il campo DESTEST dell'entita GCAP_EST
   *
   * @param entita
   *        , assume i valori GCAP e V_GCAP_DPRE
   * @param datiForm
   * @throws SQLException
   */
  public void aggiornaGCAP_EST(String entita, DataColumnContainer datiForm)
      throws GestoreException {
    String campoNGARA = entita + ".NGARA";
    String campoCONTAF = entita + ".CONTAF";

    String ngara;
    try {
      ngara = datiForm.getString(campoNGARA);
      Long contaf = datiForm.getLong(campoCONTAF);
      String ls_sql = "";

      Long numeroOccorrenze = (Long) this.sqlManager.getObject(
          "select count(*) from GCAP_EST where NGARA = ? and CONTAF= ?",
          new Object[] { ngara, contaf });

      if (numeroOccorrenze.longValue() > 0) {
        ls_sql = "update GCAP_EST set DESEST=? where NGARA = ? and CONTAF= ?";
      } else {
        ls_sql = "insert into GCAP_EST (DESEST,NGARA,CONTAF) values(?,?,?)";
      }
      this.sqlManager.update(ls_sql,
          new Object[] { datiForm.getObject("GCAP_EST.DESEST"), ngara, contaf });
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'aggiornamento della tabella GCAP_EST ", null, e);
    }
  }

  /**
   * Metodo usato dalle classi GestoreFasiRicezione.java, GestoreFasiGara.java,
   * GestoreFasiRicezione_Inviti.java (gestore di submit) per aggiornare i campi
   * GARE.FASGAR e GARE.STEPGAR. Per quanto riguarda le pagine delle fasi di
   * ricezione l'update avviene solo se non si è già passati alle fasi di gara,
   * ovvero solo se FASGAR.GARE < 2
   *
   * Il campo FASGAR assume il valore pari a Math.floor(numeroFaseAttiva/10),
   * menter STEPGAR assume il valore numeroFaseAttiva. Questa distinzione e'
   * stata necessaria in seguito all'introduzione di pagine a scheda a cui non
   * corrispondono vere e proprie fasi di gara e per mantenere la compatibilita'
   * con PWB
   *
   * @param numeroFaseAttiva
   * @param codiceGara
   * @param isFromRicezOfferte
   *        true se l'update e' richiesto dalle pagine di ricezione offerte,
   *        false se richiesto dalle pagine di fasi di gara
   * @throws GestoreException
   */
  public void aggiornaFaseGara(Long numeroFaseAttiva, String codiceGara,
      boolean isFromRicezOfferte) throws GestoreException {
    if(numeroFaseAttiva!=null) {
      String sqlUpdate = null;
      if (isFromRicezOfferte)
        sqlUpdate = "update GARE set FASGAR = ?, STEPGAR = ? "
            + "where NGARA = ? and (FASGAR < 2 or FASGAR is null) ";
      else
        sqlUpdate = "update GARE set FASGAR = ?, STEPGAR = ? where NGARA = ? ";
      Double d = new Double(Math.floor(numeroFaseAttiva.doubleValue() / 10));

      try {
        this.sqlManager.update(sqlUpdate, new Object[] { new Long(d.longValue()),
            numeroFaseAttiva, codiceGara });
      } catch (SQLException s) {
        throw new GestoreException(
            "Errore nell'aggiornamento della fase di gare della tabella GARE",
            "updateGARE_FASGAR", s);
      }
    }
  }

  /**
   * Metodo che preleva il valore del campo OFFAUM.TORN
   *
   * Tali valori vengono inseriti nel Request
   *
   * @param pageContext
   * @param sqlManager
   * @param numeroGara
   * @return
   */
  public void getOFFAUM(PageContext pageContext, SqlManager sqlManager,
      String numeroGara) throws JspException {

    try {
      String codiceGara = (String) this.sqlManager.getObject(
          "select CODGAR1 from GARE where NGARA = ?",
          new Object[] { numeroGara });
      if (codiceGara != null && !"".equals(codiceGara)) {
        String offaum = (String) this.sqlManager.getObject(
            "select offaum from TORN where codgar = ?",
            new Object[] { codiceGara });
        if (offaum != null && !"".equals(offaum)) {
          pageContext.setAttribute("offaum", offaum, PageContext.REQUEST_SCOPE);
        }
      }

    } catch (SQLException s) {
      throw new JspException(
          "Errore durante la lettura del campo OFFAUM.TORN ", s);
    }
  }

  /**
   * Aggiorna l'entità GCAP_SAN
   *
   * @param entita
   *        , assume i valori GCAP e V_GCAP_DPRE
   * @param datiForm
   * @throws SQLException
   */
  public void aggiornaGCAP_SAN(String entita, DataColumnContainer datiForm)
      throws GestoreException {

    String campoNGARA = entita + ".NGARA";
    String campoCONTAF = entita + ".CONTAF";

    String ngara;
    try {
      ngara = datiForm.getString(campoNGARA);
      Long contaf = datiForm.getLong(campoCONTAF);

      if (datiForm.isModifiedTable("GCAP_SAN")) {
        Long numeroOccorrenze = (Long) this.sqlManager.getObject(
            "select count(*) from GCAP_SAN where NGARA = ? and CONTAF= ?",
            new Object[] { ngara, contaf });

        if (numeroOccorrenze.longValue() > 0) {
          datiForm.update("GCAP_SAN", this.geneManager.getSql());
        } else {
          // Si impostano le chiavi di GCAP_SAN
          datiForm.getColumn("GCAP_SAN.NGARA").setChiave(true);
          datiForm.getColumn("GCAP_SAN.CONTAF").setChiave(true);
          datiForm.setValue("GCAP_SAN.NGARA", ngara);
          datiForm.setValue("GCAP_SAN.CONTAF", contaf);
          datiForm.insert("GCAP_SAN", this.geneManager.getSql());
        }
      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'aggiornamento della tabella GCAP_SAN ", null, e);
    }
  }

  /**
   * Preleva il valore del campo TIPFORN di TORN
   *
   * @param session
   *
   * @param nagra
   * @throws GestoreException
   */
  public Long prelevaTIPFORN(String ngara) throws GestoreException {

    Long ret = null;
    String codgar1 = null;
    String select = "select codgar1 from gare where ngara=?";
    try {
      codgar1 = (String) this.sqlManager.getObject(select, new Object[] { ngara });
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante la valutazione del codice della gara ", null, e);
    }

    select = "select tipforn from torn where codgar = ?";
    try {
      Long tipforn = (Long) this.sqlManager.getObject(select,
          new Object[] { codgar1 });
      ret = tipforn;

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante la valutazione del campo Tipo forniture(TORN.TIPFORN) della gara ",
          null, e);
    }

    return ret;
  }

  /**
   * Aggiorna l'entità DPRE_SAN
   *
   * @param entita
   *        , assume i valori GCAP e V_GCAP_DPRE
   * @param datiForm
   * @throws SQLException
   */
  public void aggiornaDPRE_SAN(String entita, DataColumnContainer datiForm)
      throws GestoreException {

    String campoNGARA = entita + ".NGARA";
    String campoCONTAF = entita + ".CONTAF";
    String campoDITTAO = entita + ".COD_DITTA";

    String ngara;
    try {
      ngara = datiForm.getString(campoNGARA);
      Long contaf = datiForm.getLong(campoCONTAF);
      String dittao = datiForm.getString(campoDITTAO);

      if (datiForm.isModifiedTable("DPRE_SAN")) {
        Long numeroOccorrenze = (Long) this.sqlManager.getObject(
            "select count(*) from DPRE_SAN where NGARA = ? and CONTAF= ? and DITTAO = ?",
            new Object[] { ngara, contaf, dittao });

        if (numeroOccorrenze.longValue() > 0) {
          datiForm.update("DPRE_SAN", this.geneManager.getSql());
        } else {
          // Si impostano le chiavi di DPRE_SAN
          datiForm.getColumn("DPRE_SAN.NGARA").setChiave(true);
          datiForm.getColumn("DPRE_SAN.CONTAF").setChiave(true);
          datiForm.getColumn("DPRE_SAN.DITTAO").setChiave(true);
          datiForm.setValue("DPRE_SAN.NGARA", ngara);
          datiForm.setValue("DPRE_SAN.CONTAF", contaf);
          datiForm.setValue("DPRE_SAN.DITTAO", dittao);
          datiForm.insert("DPRE_SAN", this.geneManager.getSql());
        }
      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'aggiornamento della tabella DPRE_SAN ", null, e);
    }
  }

  /**
   * Copia delle occorrenze dell'entità DOCUMGARA
   *
   * @param status
   * @param nGaraSorgente
   *        ,
   * @param codiceGaraSorgente
   * @param nGaraDestinazione
   * @param codiceGaraDestinazione
   * @param lottoDiGara
   * @param entita
   *        assume i valori "TORN", "GARE"
   * @throws SQLException
   */
  public void copiaDocumentazione(TransactionStatus status,
      String nGaraSorgente, String codiceGaraSorgente,
      String nGaraDestinazione, String codiceGaraDestinazione,
      boolean lottoDiGara, String entita) throws GestoreException {

    /*
     * FileAllegatoManager fileAllegatoManager = (FileAllegatoManager)
     * UtilitySpring.getBean("fileAllegatoManager", this.getServletContext(),
     * FileAllegatoManager.class);
     */

    // Per la copia di documgara creo una gestione apposita rispetto a quella
    // precedente
    // poichè il campo chiave è CODGAR, mentre nella gestione precedente tutte
    // le entita
    // hanno campo chiave ngara
    List<?> listaOccorenzeDaCopiare = null;
    DataColumnContainer campiDaCopiare = null;
    long numOccorrenze = 0;
    String sql = "select * from DOCUMGARA where CODGAR = ?";

    boolean copiaLottoInLottoUnico = false;
    if (!codiceGaraSorgente.startsWith("$")
        && codiceGaraDestinazione.startsWith("$")) {
      copiaLottoInLottoUnico = true;
    }

    Long bustalotti = null;
    String esclusioneDGUE=" and (IDSTAMPA is null or IDSTAMPA != 'DGUE')";

    try {
      if (copiaLottoInLottoUnico) {
        numOccorrenze = this.geneManager.countOccorrenze("DOCUMGARA",
            "CODGAR = ? and (NGARA = ? or NGARA is null) and GRUPPO != 11 and GRUPPO != 12 and (ISARCHI!='1' or ISARCHI is null)" + esclusioneDGUE, new Object[] {
                codiceGaraSorgente, nGaraSorgente });
        sql += " and (NGARA = ? or NGARA is null)";
      } else if (lottoDiGara) {
        bustalotti = (Long)this.sqlManager.getObject("select bustalotti from gare where ngara=?", new Object[]{codiceGaraSorgente});
        if ((bustalotti == null || (bustalotti != null && bustalotti.longValue()!=1)) || ((new Long(1)).equals(bustalotti) && !codiceGaraSorgente.equals(codiceGaraDestinazione)) ) {
          numOccorrenze = this.geneManager.countOccorrenze("DOCUMGARA",
              "CODGAR = ? and NGARA = ? and GRUPPO != 11 and GRUPPO != 12 and (ISARCHI!='1' or ISARCHI is null)" + esclusioneDGUE, new Object[] { codiceGaraSorgente,
                  nGaraSorgente });
          sql += " and NGARA = ?";
        }
      } else {
        if ("TORN".equals(entita)) {
          sql += " and NGARA is null";
        }
        numOccorrenze = this.geneManager.countOccorrenze("DOCUMGARA",
            "CODGAR = ? and GRUPPO != 11 and GRUPPO != 12 and (ISARCHI!='1' or ISARCHI is null)" + esclusioneDGUE,  new Object[] { codiceGaraSorgente });

      }
      sql += " and GRUPPO != 11 and GRUPPO != 12 and (ISARCHI!='1' or ISARCHI is null) " + esclusioneDGUE + " order by NORDDOCG";

      if (this.geneManager.getSql().isTable("DOCUMGARA") && numOccorrenze > 0) {
        if (lottoDiGara || copiaLottoInLottoUnico)
          listaOccorenzeDaCopiare = this.geneManager.getSql().getListHashMap(
              sql, new Object[] { codiceGaraSorgente, nGaraSorgente });
        else
          listaOccorenzeDaCopiare = this.geneManager.getSql().getListHashMap(
              sql, new Object[] { codiceGaraSorgente });

        // Se ci sono occorenze allora eseguo la copia
        if (listaOccorenzeDaCopiare != null
            && listaOccorenzeDaCopiare.size() > 0) {
          for (int row = 0; row < listaOccorenzeDaCopiare.size(); row++) {
            if (lottoDiGara || copiaLottoInLottoUnico)
              campiDaCopiare = new DataColumnContainer(
                  this.geneManager.getSql(), "DOCUMGARA", sql, new Object[] {
                      codiceGaraSorgente, nGaraSorgente });
            else
              campiDaCopiare = new DataColumnContainer(
                  this.geneManager.getSql(), "DOCUMGARA", sql,
                  new Object[] { codiceGaraSorgente });

            campiDaCopiare.setValoriFromMap(
                (HashMap<?,?>) listaOccorenzeDaCopiare.get(row), true);

            String campiDaNonCopiare[] = new String[]{ "DOCUMGARA.DATARILASCIO"};
            campiDaCopiare.removeColumns(campiDaNonCopiare);

            campiDaCopiare.getColumn("CODGAR").setChiave(true);
            campiDaCopiare.setValue("CODGAR", codiceGaraDestinazione);
            // campiDaCopiare.setValue("NGARA",nGaraDestinazione);
            // se il campo NGARA è valorizzato, va sostituito con il valore di
            // nGaraDestinazione
            if (campiDaCopiare.getString("NGARA") != null
                || copiaLottoInLottoUnico)
              campiDaCopiare.setValue("NGARA", nGaraDestinazione);

            campiDaCopiare.getColumn("NORDDOCG").setChiave(true);

            long newNorddocg = 1;
            if (!codiceGaraSorgente.equals(codiceGaraDestinazione)) {
              Long norddcog = campiDaCopiare.getLong("NORDDOCG");
              newNorddocg = norddcog.longValue();
            } else {
              // Si deve calcolare il valore di NORDDOCG
              Long maxNorddocg = (Long) this.geneManager.getSql().getObject(
                  "select max(NORDDOCG) from DOCUMGARA where CODGAR= ?",
                  new Object[] { codiceGaraDestinazione });

              if (maxNorddocg != null && maxNorddocg.longValue() > 0)
                newNorddocg = maxNorddocg.longValue() + 1;
            }
            campiDaCopiare.setValue("NORDDOCG", new Long(newNorddocg));

            // Per adesso non gestisco la copia dei file, ossia W_DOCDIG
            campiDaCopiare.setValue("IDPRG", "PG");
            campiDaCopiare.setValue("IDDOCDG", null);

            //Si sbianca STATODOC
            campiDaCopiare.setValue("STATODOC", null);

            // Inserimento del nuovo record
            campiDaCopiare.insert("DOCUMGARA", this.geneManager.getSql());

            // Copia delle occorrenze di W_DOCDIG figlie di DOCUMGARA
            HashMap<?,?> hm = (HashMap<?,?>) listaOccorenzeDaCopiare.get(row);
            String idprg = hm.get("IDPRG").toString();
            Long iddocdg = ((JdbcParametro) hm.get("IDDOCDG")).longValue();

            long numOccorrenzeW_DOCDIG = this.geneManager.countOccorrenze(
                "W_DOCDIG", "IDPRG = ? and IDDOCDIG = ?", new Object[] { idprg,
                    iddocdg });
            if (numOccorrenzeW_DOCDIG > 0) {
              // Il campo W_DOCDIG.DIGOGG è di tipo BLOB e va trattato
              // separatamente
              String select = "select IDPRG,IDDOCDIG,DIGENT,DIGKEY1,DIGKEY2,DIGTIPDOC,DIGNOMDOC,DIGDESDOC from W_DOCDIG where IDPRG = ? and IDDOCDIG = ?";
              List<?> occorrenzeW_DOCDIGDaCopiare = this.geneManager.getSql().getListHashMap(
                  select, new Object[] { idprg, iddocdg });
              if (occorrenzeW_DOCDIGDaCopiare != null
                  && occorrenzeW_DOCDIGDaCopiare.size() > 0) {
                for (int i = 0; i < occorrenzeW_DOCDIGDaCopiare.size(); i++) {
                  DataColumnContainer campiDaCopiareW_DOCDIG = new DataColumnContainer(
                      this.geneManager.getSql(), "W_DOCDIG", select,
                      new Object[] { idprg, iddocdg });
                  campiDaCopiareW_DOCDIG.setValoriFromMap(
                      (HashMap<?,?>) occorrenzeW_DOCDIGDaCopiare.get(i), true);
                  campiDaCopiareW_DOCDIG.getColumn("IDPRG").setChiave(true);

                  campiDaCopiareW_DOCDIG.getColumn("IDDOCDIG").setChiave(true);

                  // Si deve calcolare il valore di IDDOCDIG
                  Long maxIDDOCDIG = (Long) this.geneManager.getSql().getObject(
                      "select max(IDDOCDIG) from W_DOCDIG where IDPRG= ?",
                      new Object[] { idprg });

                  long newIDDOCDIG = 1;
                  if (maxIDDOCDIG != null && maxIDDOCDIG.longValue() > 0)
                    newIDDOCDIG = maxIDDOCDIG.longValue() + 1;

                  campiDaCopiareW_DOCDIG.setValue("IDDOCDIG", new Long(
                      newIDDOCDIG));
                  campiDaCopiareW_DOCDIG.setValue("DIGKEY1",
                      codiceGaraDestinazione);
                  campiDaCopiareW_DOCDIG.setValue("DIGKEY2", new Long(
                      newNorddocg));
                  BlobFile fileAllegato = null;
                  fileAllegato = fileAllegatoManager.getFileAllegato(idprg,
                      iddocdg);
                  ByteArrayOutputStream baos = null;
                  if (fileAllegato != null && fileAllegato.getStream() != null) {
                    baos = new ByteArrayOutputStream();
                    baos.write(fileAllegato.getStream());
                  }
                  campiDaCopiareW_DOCDIG.addColumn("W_DOCDIG.DIGOGG",
                      JdbcParametro.TIPO_BINARIO, baos);

                  // Inserimento del nuovo record su w_docdig
                  campiDaCopiareW_DOCDIG.insert("W_DOCDIG",
                      this.geneManager.getSql());

                  // Aggiornamento dei campi IDPRG e IDDOCDG di documgara
                  this.sqlManager.update(
                      "update DOCUMGARA set IDPRG=?,IDDOCDG = ? where CODGAR=? and NORDDOCG=?",
                      new Object[] { idprg, new Long(newIDDOCDIG),
                          codiceGaraDestinazione, new Long(newNorddocg) });
                }
              }
            }

          }
        }

      }

      //Gestione integrazione MDUGE
      String urlMDGUE = ConfigManager.getValore(CostantiAppalti.PROP_INTEGRAZIONE_MDGUE_URL);
      if(urlMDGUE!=null && !"".equals(urlMDGUE) ) {
        this.copiaDocumentazioneDGUE(status, nGaraSorgente, codiceGaraSorgente, nGaraDestinazione, codiceGaraDestinazione, lottoDiGara, entita);
      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella copia del lotto di gara  per entita' DOCUMGARA",
          "copiaGARE", e);
    } catch (IOException e) {
      throw new GestoreException(
          "Errore nella lettura del campo BLOB DIGOGG della W_DOCDIG",
          "copiaGARE", e);
    }

    //Copia QFORM
    try {
      String dbdultaggmodString = sqlManager.getDBFunction("DATETIMETOSTRING",
          new String[] { "dultaggmod" });
      String select ="select titolo, descrizione, tipologia, busta, idmodello," + dbdultaggmodString + ", oggetto from qform where entita='GARE' and key1=? and stato in (1,5)";
      List<?> listaQform = this.sqlManager.getListVector(select, new Object[]{nGaraSorgente});
      if(listaQform!=null && listaQform.size()>0) {
        int id=0;
        String titolo=null;
        String descrizione=null;
        Long tipologia = null;
        Long busta = null;
        Long idmodello = null;
        String dultaggmodStringValue = null;
        java.util.Date dultaggmod = null;
        String oggetto = null;
        Timestamp dultaggmodTime =null;
        for(int i=0;i<listaQform.size();i++) {
          id = genChiaviManager.getNextId("QFORM");
          titolo = SqlManager.getValueFromVectorParam(listaQform.get(i), 0).getStringValue();
          descrizione = SqlManager.getValueFromVectorParam(listaQform.get(i), 1).getStringValue();
          tipologia = SqlManager.getValueFromVectorParam(listaQform.get(i), 2).longValue();
          busta = SqlManager.getValueFromVectorParam(listaQform.get(i), 3).longValue();
          idmodello = SqlManager.getValueFromVectorParam(listaQform.get(i), 4).longValue();
          dultaggmodStringValue = SqlManager.getValueFromVectorParam(listaQform.get(i), 5).getStringValue();
          dultaggmodTime = null;
          if(dultaggmodStringValue!=null && !"".equals(dultaggmodStringValue)) {
            dultaggmod = UtilityDate.convertiData(dultaggmodStringValue, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
            dultaggmodTime = new Timestamp(dultaggmod.getTime());
          }
          oggetto = SqlManager.getValueFromVectorParam(listaQform.get(i), 6).getStringValue();
          this.sqlManager.update("insert into qform(id,entita,key1,titolo, descrizione, tipologia, busta, stato, idmodello, dultaggmod, oggetto) values(?,?,?,?,?,?,?,?,?,?,?)",
              new Object[] {new Long(id),"GARE", nGaraDestinazione,titolo,descrizione,tipologia,busta,new Long(1),idmodello,dultaggmodTime,oggetto});
        }
      }
    } catch (Exception e) {
      throw new GestoreException("Errore nella copia di QFORM", "copiaGARE", e);
    }

  }

  /**
   * Metodo che aggiorna il numero di inviti di ISCRIZCAT L'aggiornamento viene
   * fatto sempre indipendentemente dal criterio di rotazione
   *
   * @param codiceGara
   * @param numeroGara
   * @param codiceDitta
   * @param codiceCategoria
   * @param modo
   *        : INS DEL
   * @param status
   * @param tipgen
   * @param classifica
   *        è valorizzato solo nel caso di calcolo degli inviti per ISCRIZCLASSI
   * @param codiceStazApp
   *
   * @throws GestoreException
   */
  public void aggiornaNumInviti(String codiceGara, String numeroGara,
      String codiceDitta, String codiceCategoria, String modo,
      Long tipgen, Long classifica, String codiceStazApp)
      throws GestoreException {

    try {
     this.aggiornaIscrizcat(codiceGara, numeroGara, codiceDitta, codiceCategoria, modo, tipgen, classifica, codiceStazApp, 1);
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'aggiornamento del numero di inviti della ditta ", null,
          e);
    }
  }

  /**
   * Viene effettuato il controllo fra data1 e data2 Se data1 > data2 viene
   * restituito true
   *
   * @param data1
   * @param data2
   * @return boolean
   *
   */
  public boolean confrontoDate(Date data1, Date data2) throws GestoreException {
    boolean confronto = false;

    GregorianCalendar dataUno = new GregorianCalendar();
    dataUno.setTimeInMillis(data1.getTime());

    GregorianCalendar dataDue = new GregorianCalendar();
    dataDue.setTimeInMillis(data2.getTime());

    if (dataUno.after(dataDue)) confronto = true;

    return confronto;
  }

  /**
   * Metodo che inserisce per una ditta le occorrenze in IMPRDOCG, prelevando i
   * dati da DOCUMGARA
   *
   * @param codgar
   * @param ngara
   * @param codiceDitta
   * @throws GestoreException
   */
  public void inserimentoDocumentazioneDitta(String codgar, String ngara,
      String codiceDitta) throws GestoreException {
    try {
      String gartel = (String) this.sqlManager.getObject(
          "select gartel from torn where codgar = ?",
          new Object[] { codgar });

      Long acquisizione = (Long) this.sqlManager.getObject(
          "select acquisizione from ditg where codgar5 = ? and ngara5 = ? and dittao=?",
          new Object[] { codgar, ngara, codiceDitta });

      List<?> listaDocumgara = null;
      // nel caso delle gare ad offerta unica, il campo NGARA.DOCUMGARA è vuoto,
      // quindi si deve differenziare la select
      Long bustalotti = (Long)this.sqlManager.getObject("select bustalotti from gare where ngara=?", new Object[]{codgar});
      if (ngara.equals(codgar) && !codgar.startsWith("$")) {
        String select="select NORDDOCG from DOCUMGARA where CODGAR = ? and NGARA is null and GRUPPO = 3";
        if (bustalotti != null && bustalotti.longValue() == 1)
          select="select NORDDOCG,NGARA, BUSTA from DOCUMGARA where CODGAR = ? and GRUPPO = 3";
        if (acquisizione != null && acquisizione.longValue() == 5)
          select += " and BUSTA <> 4";
        listaDocumgara = this.sqlManager.getListVector(select,
            new Object[] { codgar });
      }else if (!ngara.equals(codgar) && !codgar.startsWith("$")) {
        String select="select NORDDOCG,NGARA, BUSTA from DOCUMGARA where CODGAR = ? and (NGARA = ? or NGARA is null) and GRUPPO = 3";
        if (acquisizione != null && acquisizione.longValue() == 5)
          select += " and BUSTA <> 4";
        listaDocumgara = this.sqlManager.getListVector(select,
            new Object[] { codgar, ngara });
      } else {
        String select="select NORDDOCG from DOCUMGARA where CODGAR = ? and NGARA = ? and GRUPPO = 3";
        if (acquisizione != null && acquisizione.longValue() == 5)
          select += " and BUSTA <> 4";
        listaDocumgara = this.sqlManager.getListVector(select,
            new Object[] { codgar, ngara });
      }

      if (listaDocumgara != null && listaDocumgara.size() > 0) {
        String sql = "insert into IMPRDOCG(CODGAR,CODIMP,NORDDOCI,NGARA,PROVENI,DOCTEL) values(?,?,?,?,?,?)";
        String selectLotti = "select g.ngara from gare g, gare1 g1 where g.codgar1=? and g.ngara<> g.codgar1 and g1.ngara=g.ngara";
        String condizioneValTec = " and (g.modlicg = 6  or g1.valtec='1')";
        String numeroGara = null;
        Long busta=null;
        boolean eseguireInsert=true;
        for (int i = 0; i < listaDocumgara.size(); i++) {
          numeroGara = ngara;
          eseguireInsert = true;
          Vector<?> documento = (Vector<?>) listaDocumgara.get(i);
          Long tmpProgressivoDoc = (Long) ((JdbcParametro) documento.get(0)).getValue();
          String doctel = "2";
          if ("1".equals(gartel))
            doctel="1";
          if (bustalotti != null && bustalotti.longValue() == 1) {
            //Nel caso di gara ad offerta unica con bustalotti=1 si deve fare la seguente gestione:
            //Se busta=1,4 allora imprdocg viene associato alla gara
            //Se busta=2,3 allora se è specificato il lotto, imprdocg viene associato al lotto, altrimenti
            //si deve associare imprdocg ad ogni lotto della gara, se però busta=2 si associa solo ai lotti
            //con modlig=6 o valtec=1
            busta = (Long) ((JdbcParametro) documento.get(2)).getValue();
            if (busta.longValue() == 2 || busta.longValue() == 3) {
              numeroGara = (String) ((JdbcParametro) documento.get(1)).getValue();
              if (numeroGara == null) {
                eseguireInsert = false;
                String select = selectLotti;
                //Nel caso di copia lotto le occorrenze vanno inserite solo per il nuovo lotto
                if (!ngara.equals(codgar))
                  select += " and g.ngara= '" + ngara + "'";
                if (busta.longValue() == 2)
                  select += condizioneValTec;
                List<?> listaLotti = this.sqlManager.getListVector(select, new Object[]{codgar});
                if (listaLotti != null && listaLotti.size() > 0) {
                  for (int j=0; j < listaLotti.size(); j++) {
                    this.sqlManager.update(sql, new Object[] { codgar, codiceDitta,
                        tmpProgressivoDoc, SqlManager.getValueFromVectorParam(listaLotti.get(j), 0).getStringValue(), new Long(1), doctel });
                  }

                }
              }
            } else {
              if (!ngara.equals(codgar) && !codgar.startsWith("$")) {
                //Caso della copia di un lotto di gara
                //Nel caso di bustalotti=1 (quindi gara ad offerta unica) e busta == 1,2 il documento in imprdocg non va inserito, perchè già associato alla gara
                eseguireInsert=false;
              }
            }
          }
          if (eseguireInsert)
            this.sqlManager.update(sql, new Object[] { codgar, codiceDitta,
                tmpProgressivoDoc, numeroGara, new Long(1), doctel });
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'inserimento delle occorrenze in IMPRDOCG ", null, e);
    }
  }

  /**
   * Metodo che inserisce per una ditta le occorrenze di categoria con codice
   * '0' in ISCRIZCAT
   *
   *
   * @param codgar
   * @param ngara
   * @param codiceDitta
   * @throws GestoreException
   */
  public void inserimentoCategoriaGenerica(String codgar, String ngara,
      String tipoele, String codiceDitta) throws GestoreException {
    try {

      // Occorre inserire una riga fittizia per ogni tipologia di elenco
      // contemplata (L,F,S)
      /*
       * String tipofilled = UtilityStringhe.fillLeft(tipoele,'0', 3);
       * if (tipofilled.charAt(0)=='1') { String sql=
       * "insert into ISCRIZCAT(CODGAR,NGARA,CODIMP,CODCAT,TIPCAT) values(?,?,?,?,?)"
       * ; this.sqlManager.update(sql, new Object[] { codgar,ngara,
       * codiceDitta,"0",new Long(1)}); } if (tipofilled.length()>1 &&
       * tipofilled.charAt(1)=='1') { String sql=
       * "insert into ISCRIZCAT(CODGAR,NGARA,CODIMP,CODCAT,TIPCAT) values(?,?,?,?,?)"
       * ; this.sqlManager.update(sql, new Object[] { codgar,ngara,
       * codiceDitta,"0",new Long(2)}); } if (tipofilled.length()>2 &&
       * tipofilled.charAt(2)=='1') { String sql=
       * "insert into ISCRIZCAT(CODGAR,NGARA,CODIMP,CODCAT,TIPCAT) values(?,?,?,?,?)"
       * ; this.sqlManager.update(sql, new Object[] { codgar,ngara,
       * codiceDitta,"0",new Long(3)}); }
       */
      String sql = "insert into ISCRIZCAT(CODGAR,NGARA,CODIMP,CODCAT,TIPCAT) values(?,?,?,?,?)";
      this.sqlManager.update(sql, new Object[] { codgar, ngara, codiceDitta,
          "0", new Long(1) });
      this.sqlManager.update(sql, new Object[] { codgar, ngara, codiceDitta,
          "0", new Long(2) });
      this.sqlManager.update(sql, new Object[] { codgar, ngara, codiceDitta,
          "0", new Long(3) });

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'inserimento delle occorrenze in ISCRIZCAT ", null, e);
    }
  }

  /**
   * Metodo che detemina se vi sono le condizioni per il calcolo del numero di
   * penalità (INVPEN.ISCRIZCAT). Il calcolo viene fatto sempre, indipendemente
   * dal criterio di rotazione.
   *
   * @param codiceGara
   * @param numeroGara
   * @param status
   * @throws GestoreException
   *
   * @return boolean: true se vi sono le condizioni per il calcolo del numero
   *         penalità
   */

  public boolean controlliPreliminariCalcoloNumPenalita(String codiceGara,
      String numeroGara, TransactionStatus status) throws GestoreException {

    boolean calcoloPenalita = true;

    return calcoloPenalita;
  }

  /**
   * Metodo che decrementa il numero degli inviti per categoria
   * (INVREA.ISCRIZCAT) e degli inviti della commissione
   * (COMMRUOLI.INVITI) e del numero di aggiudicazioni (ISCRIZCAT.AGG)
   * IMPORTANTE: nel caso il metodo venga richiamato dalla cancellazione di tutti i lotti
   *             di una gara a plico unico(genere=3), è importante che l'occorrenza fittizia
   *             (gare.codgar1=gare.ngara) venga cancellata per ultima, poichè per le operazioni
   *             di calcolo per i lotti servono dei dati che si trovano solo sull'occorrenza fittizia
   *
   * @param nGara
   * @throws GestoreException
   *
   */
  private void gestioneNumContatoriIscrizcatCancellazioneGara(String nGara)
      throws GestoreException {
    // String select="select elencoe,codgar1 from gare where ngara=?";
    String select = "select elencoe,codgar1,tipgen,cenint, ditta, dattoa, genere from gare,torn where ngara=? and codgar1=codgar";
    try {
      Vector<?> datiGare = this.sqlManager.getVector(select,
          new Object[] { nGara });
      String elencoe = null;
      String codgar1 = null;
      Long tipgen = null;
      String cenint=null;
      String dittaAgg=null;
      Timestamp dattoa  = null;
      Long genere = null;
      if (datiGare != null && datiGare.size() > 0) {
        elencoe = (String) ((JdbcParametro) datiGare.get(0)).getValue();
        codgar1 = (String) ((JdbcParametro) datiGare.get(1)).getValue();
        tipgen = (Long) ((JdbcParametro) datiGare.get(2)).getValue();
        cenint = (String) ((JdbcParametro) datiGare.get(3)).getValue();
        dittaAgg = (String) ((JdbcParametro) datiGare.get(4)).getValue();
        dattoa = ((JdbcParametro) datiGare.get(5)).dataValue();
        genere = (Long) ((JdbcParametro) datiGare.get(6)).getValue();
      }
      Long aqoper = (Long)this.sqlManager.getObject("select aqoper from gare1 where ngara=?", new Object[]{nGara});

      //Nel caso di gara a plico unico, il metodo viene richiamato sia per la gara fittizia che per i lotti
      //ed il codice elenco è valorizzato solo per la gara fittizia
      //Per il calcolo del numero inviti, si deve considerare solo la gara generica
      //Per il calcolo del numero di aggiudicazioni si devono considerare solo i lotti

      boolean calcoloNumInviti=true;
      boolean calcoloNumAgg=true;
      String chiaveGara=nGara;

      if((new Long(3)).equals(genere)){
        //Il campo genere è valorizzato a 3 nella gara fittizzia per le gare a busta unica, quindi
        //si è in presenza della gara fittizia, non si deve fare il calcolo del num aggiudicazioni
        calcoloNumAgg=false;
      }else if(genere==null || "".equals(genere)){
        //si deve valutare se si tratta di un lotto di una gara a busta unica
        Vector<?> datiGara=this.sqlManager.getVector("select elencoe,genere from gare where ngara=codgar1 and codgar1=?", new Object[]{codgar1});
        if(datiGara!=null && datiGara.size()>0){
          genere=SqlManager.getValueFromVectorParam(datiGara, 1).longValue();
          if((new Long(3)).equals(genere)){
            //si tratta di un lotto di una gara a busta unica, non si deve fare il calcolo del num inviti
            calcoloNumInviti=false;
            elencoe=SqlManager.getValueFromVectorParam(datiGara, 0).stringValue();
            chiaveGara=codgar1;
          }
        }
      }


      select = "select catiga,numcla from catg where ngara=? and ncatg=1";
      // String catiga = (String)this.sqlManager.getObject(select, new
      // Object[]{nGara});
      String catiga = null;
      Long numcla = null;
      Vector<?> datiCatg = this.sqlManager.getVector(select,
          new Object[] { chiaveGara });
      if (datiCatg != null && datiCatg.size() > 0) {
        catiga = (String) ((JdbcParametro) datiCatg.get(0)).getValue();
        numcla = (Long) ((JdbcParametro) datiCatg.get(1)).getValue();
      }

      if (catiga == null || "".equals(catiga)) catiga = "0";




      if (elencoe != null && !"".equals(elencoe)) {
        // Si determina il tipo algo dell'elenco
        Long tipoalgo = (Long) this.sqlManager.getObject(
            "select tipoalgo from garealbo where ngara=? and codgar= ?",
            new Object[] { elencoe, "$" + elencoe });

        select = "select dittao,acquisizione,rtofferta from ditg where codgar5=? and ngara5=?";
        List<?> listaDITG = this.sqlManager.getListVector(select, new Object[] {
            codgar1, chiaveGara });
        if (listaDITG != null && listaDITG.size() > 0) {
          String modo="DEL";
          if(dittaAgg!=null && calcoloNumAgg && !(new Long(2)).equals(aqoper))
            dittaAgg = this.getDittaSelezionataDaElenco(dittaAgg,chiaveGara);
          String codiceDitta = null;
          Long acquisizione = null;
          String rtofferta = null;
          for (int i = 0; i < listaDITG.size(); i++) {
            Vector<?> ditta = (Vector<?>) listaDITG.get(i);
            codiceDitta = ((JdbcParametro) ditta.get(0)).getStringValue();
            acquisizione = ((JdbcParametro) ditta.get(1)).longValue();
            rtofferta = ((JdbcParametro) ditta.get(2)).getStringValue();
            if (acquisizione != null && acquisizione.longValue() == 3) {

              if (tipoalgo.longValue() == 1
                  || tipoalgo.longValue() == 3
                  || tipoalgo.longValue() == 4
                  || tipoalgo.longValue() == 5
                  || tipoalgo.longValue() == 11
                  || tipoalgo.longValue() == 12
                  || tipoalgo.longValue()==14
                  || tipoalgo.longValue()==15) {

                if(calcoloNumInviti){
                this.aggiornaNumInviti("$" + elencoe, elencoe, codiceDitta,
                          catiga, modo, tipgen, null,null);

                // Aggiornamento di INVREA di ISCRIZCLASSI
                if (numcla!=null)
                  this.aggiornaNumInviti("$" + elencoe, elencoe, codiceDitta,
                        catiga, modo, tipgen, numcla,null);
                }

                if(calcoloNumAgg && !(new Long(2)).equals(aqoper) && dittaAgg!=null && dittaAgg.equals(codiceDitta) && dattoa!=null){
                  this.aggiornaNumAggiudicazioniDitta("$" + elencoe, elencoe, codiceDitta, catiga, modo, tipgen, null, null);
                  if (!"0".equals(catiga))
                    if(!(new Long(2)).equals(aqoper) && dittaAgg!=null && dittaAgg.equals(codiceDitta) && dattoa!=null)
                      this.aggiornaNumAggiudicazioniDitta("$" + elencoe, elencoe, codiceDitta, catiga, modo, tipgen, numcla, null);
                }

              } else if (tipoalgo.longValue() == 2
                  || tipoalgo.longValue() == 6
                  || tipoalgo.longValue() == 7
                  || tipoalgo.longValue() == 10
                  || tipoalgo.longValue()==13) {
                if(calcoloNumInviti)
                this.aggiornaNumInviti("$" + elencoe, elencoe, codiceDitta,
                      "0", modo, tipgen, null,null);
                if(calcoloNumAgg && !(new Long(2)).equals(aqoper) && dittaAgg!=null && dittaAgg.equals(codiceDitta) && dattoa!=null)
                  this.aggiornaNumAggiudicazioniDitta("$" + elencoe, elencoe, codiceDitta, "0", modo, tipgen, null, null);

              } else if (tipoalgo.longValue() == 8
                  || tipoalgo.longValue() == 9) {
                if(calcoloNumInviti){
                this.aggiornaNumInviti("$" + elencoe, elencoe, codiceDitta,
                      "0", modo,  tipgen, null,null);
                //Si deve fare il conteggio anche su ISCRIZUFF.INVREA sempre per la categoria '0'
                this.aggiornaNumInviti("$" + elencoe, elencoe, codiceDitta,
                      "0", modo, tipgen,null,cenint);
                }

                if(calcoloNumAgg && !(new Long(2)).equals(aqoper) && dittaAgg!=null && dittaAgg.equals(codiceDitta) && dattoa!=null){
                  this.aggiornaNumAggiudicazioniDitta("$" + elencoe, elencoe, codiceDitta, "0", modo, tipgen, null, null);
                  this.aggiornaNumAggiudicazioniDitta("$" + elencoe, elencoe, codiceDitta, "0", modo, tipgen, null, cenint);
              }
            }

              //Nel caso di accordo quadro con più operatori, si deve decrementare il numero di aggiudicazioni considerando le occorrenze in DITGAQ
              if(calcoloNumAgg && (new Long(2)).equals(aqoper) && dattoa!=null){
                if(rtofferta!=null && !"".equals(rtofferta))
                  codiceDitta = rtofferta;
                this.aggiornaNumAggiudicazioniDITGAQ("$" + elencoe, elencoe, chiaveGara, nGara, codiceDitta, catiga, tipgen, numcla, cenint, tipoalgo, modo,2);
          }
        }
      }
        }
      }
      // Gestione COMMRUOLI.NUMINVITI
      select = "select codfof,incfof from gfof where ngara2=? ";
      List<?> listaDatiGfof = this.sqlManager.getListVector(select, new Object[]{nGara});
      if (listaDatiGfof != null && listaDatiGfof.size() > 0) {
        String codtec = null;
        Long ruolo = null;
        Vector<?> datiNominativo = null;
        String selectNominativo = "select id,idalbo from commnomin where codtec = ? and dataab is not null ";
        for (int i=0; i < listaDatiGfof.size(); i++) {
          codtec = SqlManager.getValueFromVectorParam(listaDatiGfof.get(i), 0).getStringValue();
          ruolo = SqlManager.getValueFromVectorParam(listaDatiGfof.get(i), 1).longValue();
          datiNominativo = this.sqlManager.getVector(selectNominativo, new Object[] { codtec });
          if (datiNominativo != null && datiNominativo.size() > 0) {
            Long idnomin = (Long)((JdbcParametro) datiNominativo.get(0)).getValue();
            Long idalbo = (Long)((JdbcParametro) datiNominativo.get(1)).getValue();
            //decremento il numero presenze
            String updateNumeroPresenzeCommissione = "update commruoli set inviti = (inviti - 1)" +
                    " where idalbo = ? and idnomin = ? and ruolo= ? and inviti > 0";
            this.sqlManager.update(updateNumeroPresenzeCommissione, new Object[] { idalbo, idnomin, ruolo });
          }
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella decrementazione del numero inviti ", null, e);
    }
  }

  /**
   * Viene calcolato il numero delle penalita.
   * Se il numero di penalità calcolato è < 0 viene ritornato 0
   *
   * @param codgar
   * @param ngara
   * @param codiceDitta
   * @param codiceCategoria
   * @param tipoCategoria
   * @param classe
   *        valorizzata solo nel caso di ISCRIZCLASSI
   * @param entita
   *        valori "ISCRIZCAT" e "ISCRIZCLASSI"
   *@param cenint
   *@param convetiZero
   *    true comporta che se il numero calcolato delle penalità è = 0, viene convertito in null
   *    false il valore calcolato non viene convertito
   *
   * @throws GestoreException
   *         ,SQLException
   *
   */
  public Long getNumeroPenalita(String codgar, String ngara,
      String codiceDitta, String codiceCategoria, Long tipoCategoria,
      Long classe, String entita, String cenint, boolean convetiZero) throws SQLException, GestoreException {
    Long numPenalita = null;
    TransactionStatus status = null;
    Long invitiRealiElenco = null;
    String select = null;

    if (this.controlliPreliminariCalcoloNumPenalita(codgar, ngara, status)) {
      if ("ISCRIZCAT".equals(entita)) {
        select = "select max(INVREA) from iscrizcat where codgar=? and ngara=? and codcat=? and codimp <> ? and tipcat=?";
        numPenalita = (Long) this.sqlManager.getObject(select, new Object[] {
            codgar, ngara, codiceCategoria, codiceDitta, tipoCategoria });
        select = "select INVREA from iscrizcat where codgar=? and ngara=? and codcat=? and codimp = ? and tipcat=?";
        invitiRealiElenco = (Long) this.sqlManager.getObject(select,
            new Object[] { codgar, ngara, codiceCategoria, codiceDitta,
                tipoCategoria });
      } else if ("ISCRIZCLASSI".equals(entita)) {
        select = "select max(INVREA) from iscrizclassi where codgar=? and ngara=? and codcat=? and codimp <> ? and tipcat=? and numclass = ?";
        numPenalita = (Long) this.sqlManager.getObject(select,
            new Object[] { codgar, ngara, codiceCategoria, codiceDitta,
                tipoCategoria, classe });
        select = "select INVREA from iscrizclassi where codgar=? and ngara=? and codcat=? and codimp = ? and tipcat=? and numclass = ?";
        invitiRealiElenco = (Long) this.sqlManager.getObject(select,
            new Object[] { codgar, ngara, codiceCategoria, codiceDitta,
                tipoCategoria, classe });
      }else if ("ISCRIZUFF".equals(entita)) {
        select = "select max(INVREA) from iscrizuff where codgar=? and ngara=? and codcat=? and codimp <> ? and tipcat=? and cenint=?";
        numPenalita = (Long) this.sqlManager.getObject(select, new Object[] {
            codgar, ngara, codiceCategoria, codiceDitta, tipoCategoria, cenint });
        select = "select INVREA from iscrizuff where codgar=? and ngara=? and codcat=? and codimp = ? and tipcat=? and cenint=?";
        invitiRealiElenco = (Long) this.sqlManager.getObject(select,
            new Object[] { codgar, ngara, codiceCategoria, codiceDitta,
                tipoCategoria, cenint });
      }

      if (numPenalita != null && invitiRealiElenco != null)
        numPenalita = new Long(numPenalita.longValue()
            - invitiRealiElenco.longValue());
      if (numPenalita != null && numPenalita.longValue() < 0)
        numPenalita = new Long(0);
    }

    if (numPenalita != null && numPenalita.longValue() == 0 && convetiZero)
      numPenalita = null;

    return numPenalita;
  }

  /**
   * Metodo che calcola i seguenti valori: numeratore= IMPOFF.DITG + ONPRGE.GARE
   * + IMPSIC.GARE – IMPAPP.GARE -> se SICINC.GARE <> 1, altrimenti non si
   * considera IMPSIC denominatore=IMPAPP.GARE – IMPSIC.GARE – IMPNRL.GARE –
   * ONPRGE.GARE
   *
   * Tali valori vengono inseriti nel Request
   *
   * @param pageContext
   * @param sqlManager
   * @param codiceGara
   * @return
   */
  public void setDatiCalcoloRibasso(PageContext pageContext,
      SqlManager sqlManager, String codiceGara) throws JspException {

    try {
      Long ribcal = null;
      double denominatore;
      Double onprge = null;
      Double impapp = null;
      Double impsic = null;
      Double impnrl = null;
      String sicinc = "";
      double numeratore;
      String onsogrib = "";

      HashMap<String, Object> datiGara = this.getDatiGaraRibassoImporto(codiceGara);

      if (datiGara != null && datiGara.size() > 0) {
        ribcal = (Long) datiGara.get("ribcal");
        pageContext.setAttribute("ribcal", ribcal, PageContext.REQUEST_SCOPE);

        onprge = (Double) datiGara.get("onprge");
        impapp = (Double) datiGara.get("impapp");
        impsic = (Double) datiGara.get("impsic");
        impnrl = (Double) datiGara.get("impnrl");
        sicinc = (String) datiGara.get("sicinc");
        onsogrib = (String) datiGara.get("onsogrib");

        denominatore = impapp.doubleValue()
            - impsic.doubleValue()
            - impnrl.doubleValue();
        if (!"1".equals(onsogrib)) denominatore -= onprge.doubleValue();

        pageContext.setAttribute("denominatore", new Double(denominatore),
            PageContext.REQUEST_SCOPE);

        numeratore = -impapp.doubleValue();
        if (sicinc == null || "".equals(sicinc) || "2".equals(sicinc))
          numeratore += impsic.doubleValue();

        pageContext.setAttribute("numeratore", new Double(numeratore),
            PageContext.REQUEST_SCOPE);
      }
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura dei dati della gara "
          + "nella fase 'Apertura offerte economiche", e);
    }
  }

  /**
   * Viene calcolato il codice ISTAT a partire da comune e provincia
   *
   * @param comune
   * @param provincia
   *
   * @throws GestoreException
   *
   * @return codice istat
   */
  public String getCodiceISTAT(String comune, String provincia)
      throws SQLException {
    String codiceIstat = null;

    String dbFunctionSubStringTabcod3 = this.sqlManager.getDBFunction("substr",
        new String[] { "tabsche.tabcod3", "4", "3" });

    String dbFunctionSubStringTabcod2 = this.sqlManager.getDBFunction("substr",
        new String[] { "tb1.tabcod2", "2", "3" });

    String select = "select tabsche.tabcod3 from tabsche,tabsche tb1 where tabsche.tabcod ='S2003' ";
    select += "and tabsche.tabcod1 ='09' and tb1.tabcod ='S2003' and tb1.tabcod1 ='07' and ";
    select += dbFunctionSubStringTabcod3
        + "="
        + dbFunctionSubStringTabcod2
        + " and ";
    select += "tb1.tabcod3 = ? and tabsche.tabdesc = ? ";

    codiceIstat = (String) this.sqlManager.getObject(select, new Object[] {
        provincia, comune });

    return codiceIstat;
  }

  /**
   * Viene calcolato il codice ISTAT a partire dalla provincia
   *
   * @param provincia
   *
   * @throws GestoreException
   *
   * @return codice istat
   */
  public String getCodiceISTAT(String provincia) throws SQLException {
    String codiceIstat = null;

    String select = "select tabcod2 from tabsche where tabsche.tabcod='S2003' and tabsche.tabcod1='07' ";
    select += "and upper(tabsche.tabcod3)=?";
    codiceIstat = (String) this.sqlManager.getObject(select,
        new Object[] { provincia });

    return codiceIstat;
  }

  /**
   * Vengono aggiornati i dati dell'impresa prelevandoli dal file xml
   *
   * @param document
   * @param codiceImpresa
   * @param modo
   *        : INS, UPDATE
   *
   * @throws GestoreException
   *
   *
   */
  public void aggiornaDitta(Object document, String codiceImpresa, String modo)
      throws GestoreException {

    // IscrizioneImpresaElencoOperatoriDocument
    ImpresaType impresa = null;
    if (document instanceof RegistrazioneImpresaDocument)
      impresa = ((RegistrazioneImpresaDocument) document).getRegistrazioneImpresa().getDatiImpresa().getImpresa();
    else if (document instanceof IscrizioneImpresaElencoOperatoriDocument)
      impresa = ((IscrizioneImpresaElencoOperatoriDocument) document).getIscrizioneImpresaElencoOperatori().getDatiImpresa().getImpresa();
    else if (document instanceof AggiornamentoIscrizioneImpresaElencoOperatoriDocument)
      impresa = ((AggiornamentoIscrizioneImpresaElencoOperatoriDocument) document).getAggiornamentoIscrizioneImpresaElencoOperatori().getDatiImpresa().getImpresa();
    else
      impresa = ((AggiornamentoAnagraficaImpresaDocument) document).getAggiornamentoAnagraficaImpresa().getDatiImpresa().getImpresa();
    try {
      String ragioneSociale = impresa.getRagioneSociale();
      String naturaGiuridica = impresa.getNaturaGiuridica();
      Long tipoSocCooperativa = null;
      if(impresa.isSetTipoSocietaCooperativa() && !"".equals(impresa.getTipoSocietaCooperativa()))
        tipoSocCooperativa= new Long(impresa.getTipoSocietaCooperativa());

      String tipoImpresa = impresa.getTipoImpresa();
      String codiceFiscale = impresa.getCodiceFiscale();
      String partitaIVA = impresa.getPartitaIVA();
      String oggettoSociale = impresa.getOggettoSociale();
      String microPiccolaMediaImpresa = null;
      if (impresa.isSetMicroPiccolaMediaImpresa()) {
        microPiccolaMediaImpresa = impresa.getMicroPiccolaMediaImpresa();
        if ("0".equals(microPiccolaMediaImpresa))
          microPiccolaMediaImpresa="2";
      }

      IndirizzoType indirizzoDitta = impresa.getSedeLegale();
      String indirizzo = indirizzoDitta.getIndirizzo();
      String numCivico = indirizzoDitta.getNumCivico();
      String cap = indirizzoDitta.getCap();
      String comune = indirizzoDitta.getComune();
      String provincia = indirizzoDitta.getProvincia();
      String nazione = indirizzoDitta.getNazione();

      String sitoWeb = impresa.getSitoWeb();

      RecapitiType recapiti = impresa.getRecapiti();
      // String modalitaComunicazione = recapiti.getModalitaComunicazione();
      String email = recapiti.getEmail();
      String telefono = recapiti.getTelefono();
      String cellulare = recapiti.getCellulare();
      String fax = recapiti.getFax();
      String pec = recapiti.getPec();
      String gruppoiva = null;
      if(impresa.isSetGruppoIva())
        gruppoiva = impresa.getGruppoIva();

      CameraCommercioType cciaa = impresa.getCciaa();
      String numRegistroDitte = null;
      if (cciaa.isSetNumRegistroDitte())
        numRegistroDitte = cciaa.getNumRegistroDitte();

      Calendar dataDomandaIscrizione = null;
      if (cciaa.isSetDataDomandaIscrizione())
        dataDomandaIscrizione = cciaa.getDataDomandaIscrizione();

      String iscritto = null;
      if (cciaa.isSetIscritto()) {
        iscritto = cciaa.getIscritto();
        if ("0".equals(iscritto))
          iscritto = "2";
      }

      String numIscrizione = null;
      if (cciaa.isSetNumIscrizione())
    	  numIscrizione = cciaa.getNumIscrizione();

      Calendar dataIscrizione = null;
      if (cciaa.isSetDataIscrizione())
        dataIscrizione = cciaa.getDataIscrizione();

      String provinciaIscrizione = null;
      if (cciaa.isSetProvinciaIscrizione())
        provinciaIscrizione = cciaa.getProvinciaIscrizione();

      Calendar dataNullaOsta = null;
      if (cciaa.isSetDataNullaOstaAntimafia())
        dataNullaOsta = cciaa.getDataNullaOstaAntimafia();

      String soggDurc = null;
      if (impresa.isSetSoggettoDURC()) {
        soggDurc = impresa.getSoggettoDURC();
        if ("0".equals(soggDurc))
         soggDurc ="2";
      }

      Long settoreProduttivo = null;
      if (impresa.isSetSettoreProduttivo())
        settoreProduttivo = new Long(impresa.getSettoreProduttivo());

      String numIscrizioneINPS = null;
      Calendar dataIscrizioneINPS = null;
      String localitaIscrizioneINPS = null;
      String posContributivaIndividuale = null;
      if (impresa.isSetInps()) {
        INPSType inps = impresa.getInps();
        numIscrizioneINPS = inps.getNumIscrizione();
        dataIscrizioneINPS = inps.getDataIscrizione();
        localitaIscrizioneINPS = inps.getLocalitaIscrizione();
        posContributivaIndividuale = inps.getPosizContributivaIndividuale();
      }

      String numIscrizioneINAIL = null;
      Calendar dataIscrizioneINAIL = null;
      String localitaIscrizioneINAIL = null;
      String posAssicurativa = null;
      if (impresa.isSetInail()) {
        INAILType inail = impresa.getInail();
        numIscrizioneINAIL = inail.getNumIscrizione();
        dataIscrizioneINAIL = inail.getDataIscrizione();
        localitaIscrizioneINAIL = inail.getLocalitaIscrizione();
        posAssicurativa = inail.getPosizAssicurativa();
      }

      String numIscrizioneCassaEdile = null;
      Calendar dataIscrizioneCassaEdile = null;
      String localitaIscrizioneCassaEdile = null;
      String codiceCassaEdile = null;
      if (impresa.isSetCassaEdile()) {
        CassaEdileType CassaEdile = impresa.getCassaEdile();
        numIscrizioneCassaEdile = CassaEdile.getNumIscrizione();
        dataIscrizioneCassaEdile = CassaEdile.getDataIscrizione();
        localitaIscrizioneCassaEdile = CassaEdile.getLocalitaIscrizione();
        codiceCassaEdile = CassaEdile.getCodice();
      }

      SOAType soa = impresa.getSoa();
      String numIscrizioneSOA = soa.getNumIscrizione();
      Calendar dataIscrizioneSOA = soa.getDataIscrizione();
      Calendar dataScadenzaSOA = soa.getDataScadenza();
      //Calendar dataUltimaRichiestaIscrizione = soa.getDataUltimaRichiestaIscrizione();
      String organismoCertSOA = soa.getOrganismoCertificatore();
      Calendar dataScadenzaTriennale = soa.getDataScadenzaTriennale();
      Calendar dataVerificaTriennale = soa.getDataVerificaTriennale();
      Calendar dataScadenzaIntermedia = soa.getDataScadenzaIntermedia();

      String estremiContoCorrente = null;
      String soggettiContoCorrente = null;
      String codiceBic= null;

      if (impresa.isSetContoCorrente()) {
        ContoCorrenteDedicatoType ContoCorrenteDedicato = impresa.getContoCorrente();
        estremiContoCorrente = ContoCorrenteDedicato.getEstremi();
        soggettiContoCorrente = ContoCorrenteDedicato.getSoggettiAbilitati();
        codiceBic = ContoCorrenteDedicato.getBic();
      }

      String socioUnico = null;
      if (impresa.isSetSocioUnico()) {
    	  socioUnico = impresa.getSocioUnico();
        if ("0".equals(socioUnico))
        	socioUnico ="2";
      }

      String regimeFiscale = impresa.getRegimeFiscale();
      Long regFisc = null;
      if (regimeFiscale != null && !"".equals(regimeFiscale))
        regFisc = Long.valueOf(regimeFiscale);

      ISO9001Type iso = impresa.getIso9001();
      String numIscrizioneIso = iso.getNumIscrizione();
      Calendar dataScadenzaIso = iso.getDataScadenza();
      String organismoCertISO = iso.getOrganismoCertificatore();

      //Sezione 'White list'
      IscrizioneWhitelistAntimafiaType whla = impresa.getIscrizioneWhitelistAntimafia();
      String iscrittoWhla = null;
      String sedePrefetturaCompetenteWhla = null;
      String sezioniIscrizioneWhla = null;
      Calendar dataIscrizioneWhla = null;
      Calendar dataScadenzaIscrizioneWhla = null;
      String aggiornamentoWhla = null;
      if (whla != null){
        if (whla.isSetIscritto()) {
          iscrittoWhla = whla.getIscritto();
          if ("0".equals(iscrittoWhla))
            iscrittoWhla =  "2";
        }
        sedePrefetturaCompetenteWhla = whla.getSedePrefetturaCompetente();
        sezioniIscrizioneWhla = whla.getSezioniIscrizione();
        dataIscrizioneWhla = whla.getDataIscrizione();
        dataScadenzaIscrizioneWhla = whla.getDataScadenzaIscrizione();
        aggiornamentoWhla = null;
        if (whla.isSetAggiornamento()) {
          aggiornamentoWhla = whla.getAggiornamento();
          if ("0".equals(aggiornamentoWhla))
            aggiornamentoWhla =  "2";
        }
      }

      //Iscrizione elenchi ricostruzione
      IscrizioneElenchiRicostruzioneType  iscrizioneELRicostruzione = impresa.getIscrizioneElenchiRicostruzione();
      String iscrittoAntimafia= null;
      Calendar dataScadenzaIscrizioneAntimafia = null;
      String rinnovoIscrizioneAntimafia= null;
      String iscrittoElencoSpecialeProff= null;
      if(iscrizioneELRicostruzione!=null){
        if(iscrizioneELRicostruzione.isSetIscrittoAnagrafeAntimafiaEsecutori()){
          iscrittoAntimafia = iscrizioneELRicostruzione.getIscrittoAnagrafeAntimafiaEsecutori();
          if ("0".equals(iscrittoAntimafia))
            iscrittoAntimafia =  "2";
        }

        dataScadenzaIscrizioneAntimafia = iscrizioneELRicostruzione.getDataScadenza();

        if(iscrizioneELRicostruzione.isSetRinnovoIscrizioneInCorso()){
          rinnovoIscrizioneAntimafia = iscrizioneELRicostruzione.getRinnovoIscrizioneInCorso();
          if ("0".equals(rinnovoIscrizioneAntimafia))
            rinnovoIscrizioneAntimafia =  "2";
        }

        if(iscrizioneELRicostruzione.isSetIscrittoElencoSpecialeProfessionisti()){
          iscrittoElencoSpecialeProff = iscrizioneELRicostruzione.getIscrittoElencoSpecialeProfessionisti();
          if ("0".equals(iscrittoElencoSpecialeProff))
            iscrittoElencoSpecialeProff =  "2";
        }
      }

      //Rating di legalità
      RatingLegalitaType  ratingLegalita = impresa.getRatingLegalita();
      String possideRating= null;
      Calendar dataScadenzaRating = null;
      String rating= null;
      String aggiornamentoRating= null;
      if(ratingLegalita!=null){
        if(ratingLegalita.isSetPossiedeRating()){
          possideRating = ratingLegalita.getPossiedeRating();
          if ("0".equals(possideRating))
            possideRating =  "2";
        }

        dataScadenzaRating = ratingLegalita.getDataScadenza();

        rating = ratingLegalita.getRating();

        if(ratingLegalita.isSetAggiornamentoRatingInCorso()){
          aggiornamentoRating = ratingLegalita.getAggiornamentoRatingInCorso();
          if ("0".equals(aggiornamentoRating))
            aggiornamentoRating =  "2";
        }
      }

      //Classe di dimensione
      String classeDimensione = impresa.getClasseDimensione();

      //Settore attività economica
      String settoreAttivitaEconomica = impresa.getSettoreAttivitaEconomica();

      AbilitazionePreventivaType abilitazione = impresa.getAbilitazionePreventiva();
      Calendar dataScadenzaRinnovo = abilitazione.getDataScadenzaRinnovo();
      String faseRinnovo = null;
      if (abilitazione.isSetFaseRinnovo()) {
        faseRinnovo = abilitazione.getFaseRinnovo();
      }
      Calendar dataRichiestaRinnovo = abilitazione.getDataRichiestaRinnovo();

      String zoneAttivita = impresa.getZoneAttivita();
      String altreCertif = impresa.getAltreCertificazioniAttestazioni();
      String ultdic = impresa.getUlterioriDichiarazioni();
      String iscrizioneAltriIstit = impresa.getAltriIstitutiPrevidenziali();
      String assunzioniObbligate = null;
      if (impresa.isSetAssunzioniObbligate()) {
        assunzioniObbligate = impresa.getAssunzioniObbligate();
        if ("0".equals(assunzioniObbligate))
          assunzioniObbligate="2";
      }
      // Dati Libero Professionista
      String titoloLiberoProfessionista = null;
      Calendar dataNascitaLiberoProfessionista = null;
      String comuneNascitaLiberoProfessionista = null;
      String provinciaNascitaLiberoProfessionista = null;
      String sessoLiberoProfessionista = null;
      String tipologiaAlboProfessionale = null;
      String numIscrizioneAlboProfessionale = null;
      Calendar dataIscrizioneAlboProfessionale = null;
      String provinciaIScrizioneAlboProfessionale = null;
      String tipologiaCassaPrevidenziale = null;
      String numMatricolaCassaPrevidenziale = null;
      String cognome = null;
      String nome = null;

      if (impresa.isSetAltriDatiAnagrafici()) {
        AltriDatiAnagraficiType altriDatiAnagrafici = impresa.getAltriDatiAnagrafici();
        titoloLiberoProfessionista = altriDatiAnagrafici.getTitolo();
        dataNascitaLiberoProfessionista = altriDatiAnagrafici.getDataNascita();
        comuneNascitaLiberoProfessionista = altriDatiAnagrafici.getComuneNascita();
        provinciaNascitaLiberoProfessionista = altriDatiAnagrafici.getProvinciaNascita();
        sessoLiberoProfessionista = altriDatiAnagrafici.getSesso();
        cognome = altriDatiAnagrafici.getCognome();
        nome = altriDatiAnagrafici.getNome();

        AlboProfessionaleType alboProfessionale = altriDatiAnagrafici.getAlboProfessionale();
        tipologiaAlboProfessionale = alboProfessionale.getTipologia();
        numIscrizioneAlboProfessionale = alboProfessionale.getNumIscrizione();
        dataIscrizioneAlboProfessionale = alboProfessionale.getDataIscrizione();
        provinciaIScrizioneAlboProfessionale = alboProfessionale.getProvinciaIscrizione();

        CassaPrevidenzaType cassaPrevidenza = altriDatiAnagrafici.getCassaPrevidenza();
        tipologiaCassaPrevidenziale = cassaPrevidenza.getTipologia();
        numMatricolaCassaPrevidenziale = cassaPrevidenza.getNumMatricola();
      }

      java.util.Date campoData = null;

      // Aggiornamento dati Impresa
      Vector<DataColumn> elencoCampiIMPR = new Vector<DataColumn>();

      elencoCampiIMPR.add(new DataColumn("IMPR.CODIMP", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, codiceImpresa)));
      elencoCampiIMPR.add(new DataColumn("IMPR.NOMEST", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, ragioneSociale)));

      if (ragioneSociale.length() > 61)
        ragioneSociale = ragioneSociale.substring(0, 60);
      elencoCampiIMPR.add(new DataColumn("IMPR.NOMIMP", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, ragioneSociale)));

      // Se è modificata l'intestazione chiamo la funzione d'aggiornamento
      // dell'intestazione in database
      if(ragioneSociale != null){
      geneManager.aggiornaIntestazioniInDB("IMPR", ragioneSociale,
          new Object[] { codiceImpresa });
      }

      Long natgiui = null;
      if (naturaGiuridica != null && !"".equals(naturaGiuridica))
        natgiui = new Long(naturaGiuridica);

      elencoCampiIMPR.add(new DataColumn("IMPR.NATGIUI", new JdbcParametro(
          JdbcParametro.TIPO_NUMERICO, natgiui)));
      elencoCampiIMPR.add(new DataColumn("IMPR.TIPOCOOP", new JdbcParametro(
          JdbcParametro.TIPO_NUMERICO, tipoSocCooperativa)));
      elencoCampiIMPR.add(new DataColumn("IMPR.TIPIMP", new JdbcParametro(
          JdbcParametro.TIPO_NUMERICO, new Long(tipoImpresa))));
      elencoCampiIMPR.add(new DataColumn("IMPR.CFIMP", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, codiceFiscale)));
      elencoCampiIMPR.add(new DataColumn("IMPR.PIVIMP", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, partitaIVA)));
      elencoCampiIMPR.add(new DataColumn("IMPR.ISGRUPPOIVA", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, gruppoiva)));
      elencoCampiIMPR.add(new DataColumn("IMPR.OGGSOC", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, oggettoSociale)));
      elencoCampiIMPR.add(new DataColumn("IMPR.ISMPMI", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, microPiccolaMediaImpresa)));
      elencoCampiIMPR.add(new DataColumn("IMPR.INDIMP", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, indirizzo)));
      elencoCampiIMPR.add(new DataColumn("IMPR.NCIIMP", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, numCivico)));
      elencoCampiIMPR.add(new DataColumn("IMPR.CAPIMP", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, cap)));
      elencoCampiIMPR.add(new DataColumn("IMPR.LOCIMP", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, comune)));
      elencoCampiIMPR.add(new DataColumn("IMPR.PROIMP", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, provincia)));

      String codiceIstat = null;
      // String codiceIstat = this.getCodiceISTAT(comune.toUpperCase(),
      // provincia.toUpperCase());
      if (provincia != null)
        codiceIstat = this.getCodiceISTAT(comune.toUpperCase(),
            provincia.toUpperCase());
      elencoCampiIMPR.add(new DataColumn("IMPR.CODCIT", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, codiceIstat)));

      String select = "select tab1tip from tab1 where tab1cod='Ag010' and upper(tab1desc) = ?";

      Long nazimp = (Long) this.sqlManager.getObject(select,
          new Object[] { nazione.toUpperCase() });
      elencoCampiIMPR.add(new DataColumn("IMPR.NAZIMP", new JdbcParametro(
          JdbcParametro.TIPO_NUMERICO, nazimp)));
      // elencoCampiIMPR.add(new DataColumn("IMPR.MGSFLG",
      // new JdbcParametro(JdbcParametro.TIPO_TESTO, modalitaComunicazione)));
      elencoCampiIMPR.add(new DataColumn("IMPR.EMAI2IP", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, pec)));
      elencoCampiIMPR.add(new DataColumn("IMPR.EMAIIP", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, email)));
      elencoCampiIMPR.add(new DataColumn("IMPR.TELIMP", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, telefono)));
      elencoCampiIMPR.add(new DataColumn("IMPR.FAXIMP", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, fax)));
      elencoCampiIMPR.add(new DataColumn("IMPR.TELCEL", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, cellulare)));
      elencoCampiIMPR.add(new DataColumn("IMPR.INDWEB", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, sitoWeb)));

      // CCIAA
      elencoCampiIMPR.add(new DataColumn("IMPR.REGDIT", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, numRegistroDitte)));
      campoData = null;
      if (dataDomandaIscrizione != null)
        campoData = dataDomandaIscrizione.getTime();
      elencoCampiIMPR.add(new DataColumn("IMPR.DISCIF", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));
      elencoCampiIMPR.add(new DataColumn("IMPR.NCCIAA", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, numIscrizione)));
      elencoCampiIMPR.add(new DataColumn("IMPR.ISCRCCIAA", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, iscritto)));

      campoData = null;
      if (dataIscrizione != null) campoData = dataIscrizione.getTime();
      elencoCampiIMPR.add(new DataColumn("IMPR.DCCIAA", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));

      String pcciaa = null;
      if (provinciaIscrizione != null && !"".equals(provinciaIscrizione)) {
        // select="SELECT TABCOD2 FROM TABSCHE WHERE TABCOD = 'S2003' AND TABCOD1 = '07' and upper(TABCOD3)= ?";
        // pcciaa = (String)sqlManager.getObject(select, new
        // Object[]{provinciaIscrizione.toUpperCase()});
        pcciaa = this.getCodiceISTAT(provinciaIscrizione.toUpperCase());
      }
      elencoCampiIMPR.add(new DataColumn("IMPR.PCCIAA", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, pcciaa)));

      campoData = null;
      if (dataNullaOsta != null) campoData = dataNullaOsta.getTime();
      elencoCampiIMPR.add(new DataColumn("IMPR.DANTIM", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));

      //DURC
      elencoCampiIMPR.add(new DataColumn("IMPR.SOGGDURC", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, soggDurc)));
      elencoCampiIMPR.add(new DataColumn("IMPR.SETTPROD", new JdbcParametro(
          JdbcParametro.TIPO_NUMERICO, settoreProduttivo)));

      // Iscrizione INPS
      elencoCampiIMPR.add(new DataColumn("IMPR.NINPS", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, numIscrizioneINPS)));
      campoData = null;
      if (dataIscrizioneINPS != null) campoData = dataIscrizioneINPS.getTime();
      elencoCampiIMPR.add(new DataColumn("IMPR.DINPS", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));
      elencoCampiIMPR.add(new DataColumn("IMPR.LINPS", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, localitaIscrizioneINPS)));
      elencoCampiIMPR.add(new DataColumn("IMPR.POSINPS", new JdbcParametro(
          JdbcParametro.TIPO_TESTO,posContributivaIndividuale)));

      // Iscrizione INAIL
      elencoCampiIMPR.add(new DataColumn("IMPR.NINAIL", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, numIscrizioneINAIL)));
      campoData = null;
      if (dataIscrizioneINAIL != null)
        campoData = dataIscrizioneINAIL.getTime();
      elencoCampiIMPR.add(new DataColumn("IMPR.DINAIL", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));
      elencoCampiIMPR.add(new DataColumn("IMPR.LINAIL", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, localitaIscrizioneINAIL)));
      elencoCampiIMPR.add(new DataColumn("IMPR.POSINAIL", new JdbcParametro(
          JdbcParametro.TIPO_TESTO,posAssicurativa)));

      // Iscrizione CASSA EDILE
      elencoCampiIMPR.add(new DataColumn("IMPR.NCEDIL", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, numIscrizioneCassaEdile)));
      campoData = null;
      if (dataIscrizioneCassaEdile != null)
        campoData = dataIscrizioneCassaEdile.getTime();
      elencoCampiIMPR.add(new DataColumn("IMPR.DCEDIL", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));
      elencoCampiIMPR.add(new DataColumn("IMPR.LCEDIL", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, localitaIscrizioneCassaEdile)));
      elencoCampiIMPR.add(new DataColumn("IMPR.CODCEDIL", new JdbcParametro(
          JdbcParametro.TIPO_TESTO,codiceCassaEdile)));

      elencoCampiIMPR.add(new DataColumn("IMPR.NISANC", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, numIscrizioneSOA)));

      campoData = null;
      if (dataIscrizioneSOA != null) campoData = dataIscrizioneSOA.getTime();
      elencoCampiIMPR.add(new DataColumn("IMPR.DISANC", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));

      campoData = null;
      if (dataScadenzaSOA != null) campoData = dataScadenzaSOA.getTime();
      elencoCampiIMPR.add(new DataColumn("IMPR.DSCANC", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));

      /*
      campoData = null;
      if (dataUltimaRichiestaIscrizione != null)
        campoData = dataUltimaRichiestaIscrizione.getTime();
      elencoCampiIMPR.add(new DataColumn("IMPR.DURANC", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));
      */

      Long organismoCert = null;
      if (organismoCertSOA != null) organismoCert = new Long(organismoCertSOA);
      elencoCampiIMPR.add(new DataColumn("IMPR.OCTSOA", new JdbcParametro(
          JdbcParametro.TIPO_NUMERICO, organismoCert)));

      campoData = null;
      if (dataScadenzaTriennale != null) campoData = dataScadenzaTriennale.getTime();
      elencoCampiIMPR.add(new DataColumn("IMPR.DTRISOA", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));

      campoData = null;
      if (dataVerificaTriennale != null) campoData = dataVerificaTriennale.getTime();
      elencoCampiIMPR.add(new DataColumn("IMPR.DVERSOA", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));

      if (dataScadenzaIntermedia != null) campoData = dataScadenzaIntermedia.getTime();
      elencoCampiIMPR.add(new DataColumn("IMPR.DINTSOA", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));

      elencoCampiIMPR.add(new DataColumn("IMPR.NUMISO", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, numIscrizioneIso)));

      campoData = null;
      if (dataScadenzaIso != null) campoData = dataScadenzaIso.getTime();
      elencoCampiIMPR.add(new DataColumn("IMPR.DATISO", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));

      organismoCert = null;
      if (organismoCertISO != null) organismoCert = new Long(organismoCertISO);
      elencoCampiIMPR.add(new DataColumn("IMPR.OCTISO", new JdbcParametro(
          JdbcParametro.TIPO_NUMERICO, organismoCert)));

      // Conto corrente dedicato
      elencoCampiIMPR.add(new DataColumn("IMPR.COORBA", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, estremiContoCorrente)));
      elencoCampiIMPR.add(new DataColumn("IMPR.SOGMOV", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, soggettiContoCorrente)));
      elencoCampiIMPR.add(new DataColumn("IMPR.CODBIC", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, codiceBic)));

      elencoCampiIMPR.add(new DataColumn("IMPR.SOCIOUNICO", new JdbcParametro(
              JdbcParametro.TIPO_TESTO, socioUnico)));
      elencoCampiIMPR.add(new DataColumn("IMPR.REGFISC", new JdbcParametro(
              JdbcParametro.TIPO_NUMERICO, regFisc)));

      //////
      //Sezione 'White list'
      elencoCampiIMPR.add(new DataColumn("IMPR.ISCRIWL", new JdbcParametro(
          JdbcParametro.TIPO_TESTO,iscrittoWhla)));
      elencoCampiIMPR.add(new DataColumn("IMPR.WLPREFE", new JdbcParametro(
          JdbcParametro.TIPO_TESTO,sedePrefetturaCompetenteWhla)));
      elencoCampiIMPR.add(new DataColumn("IMPR.WLSEZIO", new JdbcParametro(
          JdbcParametro.TIPO_TESTO,sezioniIscrizioneWhla)));

      campoData = null;
      if (dataIscrizioneWhla != null)
        campoData = dataIscrizioneWhla.getTime();
      elencoCampiIMPR.add(new DataColumn("IMPR.WLDISCRI", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));

      campoData = null;
      if (dataScadenzaIscrizioneWhla != null)
        campoData = dataScadenzaIscrizioneWhla.getTime();
      elencoCampiIMPR.add(new DataColumn("IMPR.WLDSCAD", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));

      elencoCampiIMPR.add(new DataColumn("IMPR.WLINCORSO", new JdbcParametro(
          JdbcParametro.TIPO_TESTO,aggiornamentoWhla)));
      //////


      //Iscrizione elenchi ricostruzione
      elencoCampiIMPR.add(new DataColumn("IMPR.ISCRIAE", new JdbcParametro(
          JdbcParametro.TIPO_TESTO,iscrittoAntimafia)));
      elencoCampiIMPR.add(new DataColumn("IMPR.AEINCORSO", new JdbcParametro(
          JdbcParametro.TIPO_TESTO,rinnovoIscrizioneAntimafia)));
      elencoCampiIMPR.add(new DataColumn("IMPR.ISCRIESP", new JdbcParametro(
          JdbcParametro.TIPO_TESTO,iscrittoElencoSpecialeProff)));

      campoData = null;
      if (dataScadenzaIscrizioneAntimafia != null)
        campoData = dataScadenzaIscrizioneAntimafia.getTime();
      elencoCampiIMPR.add(new DataColumn("IMPR.AEDSCAD", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));


      //Rating di legalità
      elencoCampiIMPR.add(new DataColumn("IMPR.ISCRIRAT", new JdbcParametro(
          JdbcParametro.TIPO_TESTO,possideRating)));

      Long ratingLong=null;
      if(rating!=null && !"".equals(rating))
        ratingLong = new Long(rating);
      elencoCampiIMPR.add(new DataColumn("IMPR.RATING", new JdbcParametro(
          JdbcParametro.TIPO_NUMERICO,ratingLong)));
      elencoCampiIMPR.add(new DataColumn("IMPR.RATINCORSO", new JdbcParametro(
          JdbcParametro.TIPO_TESTO,aggiornamentoRating)));

      campoData = null;
      if (dataScadenzaRating != null)
        campoData = dataScadenzaRating.getTime();
      elencoCampiIMPR.add(new DataColumn("IMPR.RATDSCAD", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));



      //Classe di dimensione
      Long tempLong = null;
      if (classeDimensione != null && !"".equals(classeDimensione)) tempLong = new Long(classeDimensione);
      elencoCampiIMPR.add(new DataColumn("IMPR.CLADIM", new JdbcParametro(
          JdbcParametro.TIPO_NUMERICO,tempLong)));

      //Settore attività economica
      elencoCampiIMPR.add(new DataColumn("IMPR.CODATT", new JdbcParametro(
          JdbcParametro.TIPO_TESTO,settoreAttivitaEconomica)));

      campoData = null;
      if (dataScadenzaRinnovo != null)
        campoData = dataScadenzaRinnovo.getTime();
      elencoCampiIMPR.add(new DataColumn("IMPR.DSCNOS", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));

      elencoCampiIMPR.add(new DataColumn("IMPR.RINNOS", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, faseRinnovo)));

      campoData = null;
      if (dataRichiestaRinnovo != null)
        campoData = dataRichiestaRinnovo.getTime();
      elencoCampiIMPR.add(new DataColumn("IMPR.DRINOS", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));

      elencoCampiIMPR.add(new DataColumn("IMPR.ZONEAT", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, zoneAttivita)));

      elencoCampiIMPR.add(new DataColumn("IMPR.ACERTATT", new JdbcParametro(
          JdbcParametro.TIPO_TESTO,altreCertif)));

      elencoCampiIMPR.add(new DataColumn("IMPR.ULTDIC", new JdbcParametro(
          JdbcParametro.TIPO_TESTO,ultdic)));

      elencoCampiIMPR.add(new DataColumn("IMPR.AISTPREV", new JdbcParametro(
          JdbcParametro.TIPO_TESTO,iscrizioneAltriIstit)));

      elencoCampiIMPR.add(new DataColumn("IMPR.ASSOBBL", new JdbcParametro(
          JdbcParametro.TIPO_TESTO,assunzioniObbligate)));

      elencoCampiIMPR.add(new DataColumn("IMPR.DAESTERN", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, "1")));

      // Libero professionista
      if (impresa.isSetAltriDatiAnagrafici()) {
        elencoCampiIMPR.add(new DataColumn("IMPR.COGNOME", new JdbcParametro(
            JdbcParametro.TIPO_TESTO, cognome)));

        elencoCampiIMPR.add(new DataColumn("IMPR.NOME", new JdbcParametro(
            JdbcParametro.TIPO_TESTO, nome)));

        Long tmpLong = null;
        if (titoloLiberoProfessionista != null
            && !"".equals(titoloLiberoProfessionista))
          tmpLong = new Long(titoloLiberoProfessionista);
        elencoCampiIMPR.add(new DataColumn("IMPR.INCTEC", new JdbcParametro(
            JdbcParametro.TIPO_TESTO, tmpLong)));

        campoData = null;
        if (dataNascitaLiberoProfessionista != null)
          campoData = dataNascitaLiberoProfessionista.getTime();
        elencoCampiIMPR.add(new DataColumn("IMPR.DNATEC", new JdbcParametro(
            JdbcParametro.TIPO_DATA, campoData)));
        elencoCampiIMPR.add(new DataColumn("IMPR.CNATEC", new JdbcParametro(
            JdbcParametro.TIPO_TESTO, comuneNascitaLiberoProfessionista)));
        elencoCampiIMPR.add(new DataColumn("IMPR.PRONAS", new JdbcParametro(
            JdbcParametro.TIPO_TESTO, provinciaNascitaLiberoProfessionista)));
        elencoCampiIMPR.add(new DataColumn("IMPR.SEXTEC", new JdbcParametro(
            JdbcParametro.TIPO_TESTO, sessoLiberoProfessionista)));

        tmpLong = null;
        if (tipologiaAlboProfessionale != null
            && !"".equals(tipologiaAlboProfessionale))
          tmpLong = new Long(tipologiaAlboProfessionale);
        elencoCampiIMPR.add(new DataColumn("IMPR.TIPALB", new JdbcParametro(
            JdbcParametro.TIPO_TESTO, tmpLong)));

        elencoCampiIMPR.add(new DataColumn("IMPR.ALBTEC", new JdbcParametro(
            JdbcParametro.TIPO_TESTO, numIscrizioneAlboProfessionale)));

        campoData = null;
        if (dataIscrizioneAlboProfessionale != null)
          campoData = dataIscrizioneAlboProfessionale.getTime();
        elencoCampiIMPR.add(new DataColumn("IMPR.DATALB", new JdbcParametro(
            JdbcParametro.TIPO_DATA, campoData)));

        codiceIstat = null;
        if (provinciaIScrizioneAlboProfessionale != null)
          codiceIstat = this.getCodiceISTAT(provinciaIScrizioneAlboProfessionale.toUpperCase());
        elencoCampiIMPR.add(new DataColumn("IMPR.PROALB", new JdbcParametro(
            JdbcParametro.TIPO_TESTO, codiceIstat)));

        tmpLong = null;
        if (tipologiaCassaPrevidenziale != null
            && !"".equals(tipologiaCassaPrevidenziale))
          tmpLong = new Long(tipologiaCassaPrevidenziale);
        elencoCampiIMPR.add(new DataColumn("IMPR.TCAPRE", new JdbcParametro(
            JdbcParametro.TIPO_TESTO, tmpLong)));

        elencoCampiIMPR.add(new DataColumn("IMPR.NCAPRE", new JdbcParametro(
            JdbcParametro.TIPO_TESTO, numMatricolaCassaPrevidenziale)));
      }

      DataColumnContainer containerIMPR = new DataColumnContainer(
          elencoCampiIMPR);

      containerIMPR.getColumn("IMPR.CODIMP").setChiave(true);
      containerIMPR.getColumn("IMPR.CODIMP").setObjectOriginalValue(
          codiceImpresa);

      // Devo forzare il valore originale dei campi opzionali ad
      // un valore non nullo altrimenti non vedo il cambiamento
      Date datamtp = new Date(1);
      datamtp.setTime(1);
      containerIMPR.getColumn("IMPR.OGGSOC").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.ISMPMI").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.NISANC").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.DANTIM").setObjectOriginalValue(datamtp);
      containerIMPR.getColumn("IMPR.DISANC").setObjectOriginalValue(datamtp);
      containerIMPR.getColumn("IMPR.DSCANC").setObjectOriginalValue(datamtp);
      containerIMPR.getColumn("IMPR.DTRISOA").setObjectOriginalValue(datamtp);
      containerIMPR.getColumn("IMPR.DVERSOA").setObjectOriginalValue(datamtp);
      containerIMPR.getColumn("IMPR.DINTSOA").setObjectOriginalValue(datamtp);
      //containerIMPR.getColumn("IMPR.DURANC").setObjectOriginalValue(datamtp);
      containerIMPR.getColumn("IMPR.PROIMP").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.TELIMP").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.FAXIMP").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.TELCEL").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.EMAIIP").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.EMAI2IP").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.NUMISO").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.DATISO").setObjectOriginalValue(datamtp);
      containerIMPR.getColumn("IMPR.OCTSOA").setObjectOriginalValue(
          new Long(-1));
      containerIMPR.getColumn("IMPR.OCTISO").setObjectOriginalValue(
          new Long(-1));
      containerIMPR.getColumn("IMPR.DSCNOS").setObjectOriginalValue(datamtp);
      containerIMPR.getColumn("IMPR.RINNOS").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.DRINOS").setObjectOriginalValue(datamtp);
      containerIMPR.getColumn("IMPR.ZONEAT").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.ACERTATT").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.AISTPREV").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.ISGRUPPOIVA").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.TIPOCOOP").setObjectOriginalValue(
          new Long(-1));

      containerIMPR.getColumn("IMPR.REGDIT").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.DISCIF").setObjectOriginalValue(datamtp);
      containerIMPR.getColumn("IMPR.NCCIAA").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.ISCRCCIAA").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.DCCIAA").setObjectOriginalValue(datamtp);
      containerIMPR.getColumn("IMPR.PCCIAA").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.DANTIM").setObjectOriginalValue(datamtp);
      containerIMPR.getColumn("IMPR.NINPS").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.DINPS").setObjectOriginalValue(datamtp);
      containerIMPR.getColumn("IMPR.LINPS").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.POSINPS").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.NINAIL").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.DINAIL").setObjectOriginalValue(datamtp);
      containerIMPR.getColumn("IMPR.LINAIL").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.POSINAIL").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.NCEDIL").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.DCEDIL").setObjectOriginalValue(datamtp);
      containerIMPR.getColumn("IMPR.LCEDIL").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.CODCEDIL").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.COORBA").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.CODBIC").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.SOGMOV").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.SOCIOUNICO").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.REGFISC").setObjectOriginalValue(Long.valueOf(-1));
      containerIMPR.getColumn("IMPR.SETTPROD").setObjectOriginalValue(
          new Long(-1));
      containerIMPR.getColumn("IMPR.ASSOBBL").setObjectOriginalValue(" ");
      if (impresa.isSetAltriDatiAnagrafici()) {
        containerIMPR.getColumn("IMPR.INCTEC").setObjectOriginalValue(
            new Long(-1));
        containerIMPR.getColumn("IMPR.DNATEC").setObjectOriginalValue(datamtp);
        containerIMPR.getColumn("IMPR.CNATEC").setObjectOriginalValue(" ");
        containerIMPR.getColumn("IMPR.PRONAS").setObjectOriginalValue(" ");
        containerIMPR.getColumn("IMPR.SEXTEC").setObjectOriginalValue(" ");
        containerIMPR.getColumn("IMPR.TIPALB").setObjectOriginalValue(
            new Long(-1));
        containerIMPR.getColumn("IMPR.ALBTEC").setObjectOriginalValue(" ");
        containerIMPR.getColumn("IMPR.DATALB").setObjectOriginalValue(datamtp);
        containerIMPR.getColumn("IMPR.PROALB").setObjectOriginalValue(" ");
        containerIMPR.getColumn("IMPR.TCAPRE").setObjectOriginalValue(
            new Long(-1));
        containerIMPR.getColumn("IMPR.NCAPRE").setObjectOriginalValue(" ");
        containerIMPR.getColumn("IMPR.COGNOME").setObjectOriginalValue(" ");
        containerIMPR.getColumn("IMPR.NOME").setObjectOriginalValue(" ");
      }
      containerIMPR.getColumn("IMPR.SOGGDURC").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.ASSOBBL").setObjectOriginalValue(" ");
      ///////////////////////////////////////////////
      //Sezione 'White list'
      containerIMPR.getColumn("IMPR.ISCRIWL").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.WLPREFE").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.WLSEZIO").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.WLDISCRI").setObjectOriginalValue(datamtp);
      containerIMPR.getColumn("IMPR.WLDSCAD").setObjectOriginalValue(datamtp);
      containerIMPR.getColumn("IMPR.WLINCORSO").setObjectOriginalValue(" ");

      //Iscrizione elenchi ricostruzione
      containerIMPR.getColumn("IMPR.ISCRIAE").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.AEINCORSO").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.ISCRIESP").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.AEDSCAD").setObjectOriginalValue(datamtp);

      //Rating di legalità
      containerIMPR.getColumn("IMPR.ISCRIRAT").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.RATINCORSO").setObjectOriginalValue(" ");
      containerIMPR.getColumn("IMPR.RATING").setObjectOriginalValue(new Long(-1));
      containerIMPR.getColumn("IMPR.RATDSCAD").setObjectOriginalValue(datamtp);

      //Classe di dimensione
      containerIMPR.getColumn("IMPR.CLADIM").setObjectOriginalValue(
          new Long(-1));
      //Settore attività economica
      containerIMPR.getColumn("IMPR.CODATT").setObjectOriginalValue(" ");
      ///////////////////////////////////////////////



      if ("UPDATE".equals(modo)) {
        containerIMPR.update("IMPR", sqlManager);
      } else {
        containerIMPR.getColumn("IMPR.CODIMP").setObjectOriginalValue(" ");
        containerIMPR.getColumn("IMPR.CODIMP").setChiave(true);
        containerIMPR.insert("IMPR", sqlManager);
      }

      if("1".equals(gruppoiva))
        this.sqlManager.update(
            "update impr set isgruppoiva=? where  pivimp=? and (nazimp is null or nazimp='1')", new Object[] {"1", partitaIVA });

      // Gestione degli altri indirizzi
      this.gestioneAltriIndirizzi(impresa, codiceImpresa);

      // Gestione dati annuali
      this.gestioneDatiAnnualiImpresa(impresa, codiceImpresa);

      HashMap<String, Object> parametri = null;
      // Inizio aggiornamento dati LEGALE RAPPRESENTANTE
      // Si cancellano le occorrenze con data termine incarico nulla
      this.sqlManager.update(
          "delete from IMPLEG where CODIMP2 = ? and LEGFIN is null ",
          new Object[] { codiceImpresa });
      String integrazioneCineca = ConfigManager.getValore("integrazioneAnagraficheUGOV");
      integrazioneCineca = UtilityStringhe.convertiNullInStringaVuota(integrazioneCineca);
      String rapprLegale = null;
      boolean solaLettura = false;

      // TEIM
      ReferenteImpresaType legaleRappresentante[] = null;
      if (document instanceof RegistrazioneImpresaDocument)
        legaleRappresentante = ((RegistrazioneImpresaDocument) document).getRegistrazioneImpresa().getDatiImpresa().getLegaleRappresentanteArray();
      else if (document instanceof IscrizioneImpresaElencoOperatoriDocument)
        legaleRappresentante = ((IscrizioneImpresaElencoOperatoriDocument) document).getIscrizioneImpresaElencoOperatori().getDatiImpresa().getLegaleRappresentanteArray();
      else if (document instanceof AggiornamentoIscrizioneImpresaElencoOperatoriDocument)
        legaleRappresentante = ((AggiornamentoIscrizioneImpresaElencoOperatoriDocument) document).getAggiornamentoIscrizioneImpresaElencoOperatori().getDatiImpresa().getLegaleRappresentanteArray();
      else
        legaleRappresentante = ((AggiornamentoAnagraficaImpresaDocument) document).getAggiornamentoAnagraficaImpresa().getDatiImpresa().getLegaleRappresentanteArray();

      for (int i = 0; i < legaleRappresentante.length; i++) {
        solaLettura = legaleRappresentante[i].getSolaLettura();
        if (!solaLettura) {
          String cognomeLegale = legaleRappresentante[i].getCognome();
          String nomeLegale = legaleRappresentante[i].getNome();
          String titolo = legaleRappresentante[i].getTitolo();
          String codfiscLegale = legaleRappresentante[i].getCodiceFiscale();
          String sessoLegale = legaleRappresentante[i].getSesso();
          Calendar dataNascitaLegale = legaleRappresentante[i].getDataNascita();
          String comuneNascitaLegale = legaleRappresentante[i].getComuneNascita();
          String provinciaNascitaLegale = legaleRappresentante[i].getProvinciaNascita();
          IndirizzoType indirizzoTypeLegale = legaleRappresentante[i].getResidenza();
          String indirizzoLegale = indirizzoTypeLegale.getIndirizzo();
          String numCivicoLegale = indirizzoTypeLegale.getNumCivico();
          String capLegale = indirizzoTypeLegale.getCap();
          String comuneLegale = indirizzoTypeLegale.getComune();
          String nazioneLegale = indirizzoTypeLegale.getNazione();
          String provinciaLegale = indirizzoTypeLegale.getProvincia();
          String note = legaleRappresentante[i].getNote();
          //boolean responsabileDic = legaleRappresentante[i].getResponsabileDichiarazioni();
          //String respDic=(responsabileDic ? "1" : "2");
          String respDic = legaleRappresentante[i].getResponsabileDichiarazioni();
          if ("0".equals(respDic))
            respDic ="2";
          AlboProfessionaleType alboProfessionaleLegale = legaleRappresentante[i].getAlboProfessionale();
          CassaPrevidenzaType cassaPrevidenzaLegale = legaleRappresentante[i].getCassaPrevidenza();

          parametri = new HashMap<String, Object>();
          parametri.put("cognome", cognomeLegale);
          parametri.put("nome", nomeLegale);
          parametri.put("titolo", titolo);
          parametri.put("codfisc", codfiscLegale);
          parametri.put("sesso", sessoLegale);
          campoData = null;
          if (dataNascitaLegale != null) campoData = dataNascitaLegale.getTime();
          parametri.put("dataNascita", campoData);
          parametri.put("comuneNascita", comuneNascitaLegale);
          parametri.put("provinciaNascita", provinciaNascitaLegale);
          parametri.put("indirizzo", indirizzoLegale);
          parametri.put("numCivico", numCivicoLegale);
          parametri.put("cap", capLegale);
          parametri.put("comune", comuneLegale);
          parametri.put("nazione", nazioneLegale);
          parametri.put("provincia", provinciaLegale);
          parametri.put("alboProfessionale", alboProfessionaleLegale);
          parametri.put("cassaPrevidenza", cassaPrevidenzaLegale);

          String codiceLegale = this.aggiornaReferenti(parametri);

          // IMPLEG
          String nomleg = "" + cognomeLegale + " " + nomeLegale;
          Calendar dataInizio = legaleRappresentante[i].getDataInizioIncarico();
          java.util.Date DataInizioIncarico = dataInizio.getTime();

          Calendar dataFine = legaleRappresentante[i].getDataFineIncarico();
          java.util.Date DataFineIncarico = null;
          if (dataFine != null) DataFineIncarico = dataFine.getTime();


          //Modifiche per cambiamento chiave entità IMPLEG e IMPDTE
            Long newId = new Long(genChiaviManager.getNextId("IMPLEG"));
            this.sqlManager.update(
                "insert into IMPLEG(ID,CODIMP2,CODLEG,NOMLEG,DAESTERN,LEGINI,LEGFIN,NOTLEG,RESPDICH) values(?,?,?,?,?,?,?,?,?)",
                new Object[] {newId, codiceImpresa, codiceLegale, nomleg, "1",
                    DataInizioIncarico, DataFineIncarico, note, respDic});

              }
            }
      // Fine aggiornamento dati LEGALE RAPPRESENTANTE

      //inizio integrazione con cineca
      //C.F. Viene richiesto da Cineca a giugno 2021 di abolire questa casistica: l'integrazione avviene solo in aggiudicazione
      //if ("1".equals(integrazioneCineca)) {
      if (false) {
        try {
          String[] res = null;
          if ("6".equals(tipoImpresa)) {
            if ("10".equals(naturaGiuridica)) {
              //verifico la presenza della ditta individuale
              res = cinecaWSPersoneFisicheManager.getCinecaPersonaFisica(null,codiceImpresa);
            }else{
              //verifico la presenza della ditta individuale

              res = cinecaWSManager.getCinecaDittaIndividuale(codiceImpresa);
            }
          }else{
            //verifico la presenza del soggetto collettivo
            res = cinecaWSManager.getCinecaSoggettoCollettivo(codiceImpresa);
          }
          if (res[0] != null && new Integer(res[0]) > 0) {
            String idInternoStr = res[2];
            Long idInterno = null;
            if (idInternoStr != null) {//dovrebbe esserlo sempre in questo caso
              idInterno = new Long(idInternoStr);
            }

            HashMap<String, Object> soggettoCollettivo = cinecaWSManager.getDatiSoggettoCollettivo(codiceImpresa);
            if (!"6".equals(tipoImpresa) && "1".equals(res[0])) {
              //masterizzo
                  String codEsterno = (String) soggettoCollettivo.get("codEsterno");
                  String[] ctrlDOres = cinecaAnagraficaComuneManager.getDatiObbligatoriAnagrafica("TRACC", null, soggettoCollettivo);
                  if("true".equals(ctrlDOres[0])){
                      WSERP_PortType wserp = cinecaAnagraficaComuneManager.getWSERP("WSERP");
                      String[] credenziali = cinecaAnagraficaComuneManager.getWSLogin(new Long(50), "CINECA");
                      String username = credenziali[0];
                      String password = credenziali[1];
                      WSERPUgovAnagraficaType anagrafica = new WSERPUgovAnagraficaType();
                      anagrafica.setIdInterno(idInterno);
                      anagrafica.setCodEsterno(codEsterno);
                      WSERPUgovResType resSC = wserp.WSERPSoggettoCollettivo(username, password, "MASTERIZZA", anagrafica);
                  }
            }

            String[] ctrlDOres = cinecaWSManager.setCinecaSoggettoCollettivo(null, idInterno, rapprLegale, containerIMPR);
            if("false".equals(ctrlDOres[0])){
              String msg = " - Non risultano valorizzati i seguenti dati obbligatori: \n" + ctrlDOres[1];
              cinecaWSManager.setNoteAvvisi(msg, codiceImpresa, "INS", new Long(50), new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), false);
            }
          }

        } catch (Exception e) {
          //CF24/07/2018 Tale procedura e' coinvolta in operazioni schedulate: la mancata integrazione con anagrafica Cineca non deve bloccare le richieste da portale
          // ma segnalare l'eventuale disallineamento causa eccezione del ws (attualmente il ws genera una eccezione in caso di esito negativo)
          cinecaWSManager.setNoteAvvisi(e.getMessage(), codiceImpresa, "INS", new Long(50), new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), false);
        }



      }//fine integrazione con cineca


      // Inizio aggiornamento dati DIRETTORE TECNICO

      // TEIM
      ReferenteImpresaType direttoreTecnico[] = null;
      if (document instanceof RegistrazioneImpresaDocument)
        direttoreTecnico = ((RegistrazioneImpresaDocument) document).getRegistrazioneImpresa().getDatiImpresa().getDirettoreTecnicoArray();
      else if (document instanceof IscrizioneImpresaElencoOperatoriDocument)
        direttoreTecnico = ((IscrizioneImpresaElencoOperatoriDocument) document).getIscrizioneImpresaElencoOperatori().getDatiImpresa().getDirettoreTecnicoArray();
      else if (document instanceof AggiornamentoIscrizioneImpresaElencoOperatoriDocument)
        direttoreTecnico = ((AggiornamentoIscrizioneImpresaElencoOperatoriDocument) document).getAggiornamentoIscrizioneImpresaElencoOperatori().getDatiImpresa().getDirettoreTecnicoArray();
      else
        direttoreTecnico = ((AggiornamentoAnagraficaImpresaDocument) document).getAggiornamentoAnagraficaImpresa().getDatiImpresa().getDirettoreTecnicoArray();

      if (direttoreTecnico != null && direttoreTecnico.length > 0) {

        // Si cancellano le occorrenze con data termine incarico nulla
        this.sqlManager.update(
            "delete from IMPDTE where CODIMP3 = ? and DIRFIN is null ",
            new Object[] { codiceImpresa });
        for (int i = 0; i < direttoreTecnico.length; i++) {
          solaLettura = direttoreTecnico[i].getSolaLettura();
          if (!solaLettura) {
            String cognomeDirettore = direttoreTecnico[i].getCognome();
            String nomeDirettore = direttoreTecnico[i].getNome();
            String titolo = direttoreTecnico[i].getTitolo();
            String codfiscDirettore = direttoreTecnico[i].getCodiceFiscale();
            // String partitaIVADirettore = direttoreTecnico[i].getPartitaIVA();
            String sessoDirettore = direttoreTecnico[i].getSesso();
            Calendar dataNascitaDirettore = direttoreTecnico[i].getDataNascita();
            String comuneNascitaDirettore = direttoreTecnico[i].getComuneNascita();
            String provinciaNascitaDirettore = direttoreTecnico[i].getProvinciaNascita();
            IndirizzoType indirizzoTypeDirettore = direttoreTecnico[i].getResidenza();
            String indirizzoDirettore = indirizzoTypeDirettore.getIndirizzo();
            String numCivicoDirettore = indirizzoTypeDirettore.getNumCivico();
            String capDirettore = indirizzoTypeDirettore.getCap();
            String comuneDirettore = indirizzoTypeDirettore.getComune();
            String nazioneDirettore = indirizzoTypeDirettore.getNazione();
            String provinciaDirettore = indirizzoTypeDirettore.getProvincia();
            String note = direttoreTecnico[i].getNote();
            //boolean responsabileDic = direttoreTecnico[i].getResponsabileDichiarazioni();
            //String respDic=(responsabileDic ? "1" : "2");
            String respDic = direttoreTecnico[i].getResponsabileDichiarazioni();
            if ("0".equals(respDic))
              respDic ="2";
            AlboProfessionaleType alboProfessionaleDirettore = direttoreTecnico[i].getAlboProfessionale();
            CassaPrevidenzaType cassaPrevidenzaDirettore = direttoreTecnico[i].getCassaPrevidenza();

            parametri = new HashMap<String, Object>();
            parametri.put("cognome", cognomeDirettore);
            parametri.put("nome", nomeDirettore);
            parametri.put("titolo", titolo);
            parametri.put("codfisc", codfiscDirettore);
            // parametri.put("piva", partitaIVADirettore);
            parametri.put("sesso", sessoDirettore);
            campoData = null;
            if (dataNascitaDirettore != null)
              campoData = dataNascitaDirettore.getTime();
            parametri.put("dataNascita", campoData);
            parametri.put("comuneNascita", comuneNascitaDirettore);
            parametri.put("provinciaNascita", provinciaNascitaDirettore);
            parametri.put("indirizzo", indirizzoDirettore);
            parametri.put("numCivico", numCivicoDirettore);
            parametri.put("cap", capDirettore);
            parametri.put("comune", comuneDirettore);
            parametri.put("nazione", nazioneDirettore);
            parametri.put("provincia", provinciaDirettore);
            parametri.put("alboProfessionale", alboProfessionaleDirettore);
            parametri.put("cassaPrevidenza", cassaPrevidenzaDirettore);

            String codiceDirettore = this.aggiornaReferenti(parametri);

            // IMPDTE
            String nomdte = "" + cognomeDirettore + " " + nomeDirettore;
            Calendar dataInizio = direttoreTecnico[i].getDataInizioIncarico();
            java.util.Date DataInizioIncarico = dataInizio.getTime();

            Calendar dataFine = direttoreTecnico[i].getDataFineIncarico();
            java.util.Date DataFineIncarico = null;
            if (dataFine != null) DataFineIncarico = dataFine.getTime();


            //Modifiche per cambiamento chiave entità IMPLEG e IMPDTE
            Long newId = new Long(genChiaviManager.getNextId("IMPDTE"));
              this.sqlManager.update(
                  "insert into IMPDTE(ID,CODIMP3,CODDTE,NOMDTE,DAESTERN,DIRINI,DIRFIN,NOTDTE,RESPDICH) values(?,?,?,?,?,?,?,?,?)",
                  new Object[] {newId, codiceImpresa, codiceDirettore, nomdte, "1",
                      DataInizioIncarico, DataFineIncarico, note, respDic });

          }
        }

      }
      // Fine aggiornamento dati DIRETTORE TECNICO

      // Inizio aggiornamento dati ALTRA CARICA O QUALIFICA
      ReferenteImpresaType altraCarica[] = null;
      if (document instanceof RegistrazioneImpresaDocument)
        altraCarica = ((RegistrazioneImpresaDocument) document).getRegistrazioneImpresa().getDatiImpresa().getAltraCaricaArray();
      else if (document instanceof IscrizioneImpresaElencoOperatoriDocument)
        altraCarica = ((IscrizioneImpresaElencoOperatoriDocument) document).getIscrizioneImpresaElencoOperatori().getDatiImpresa().getAltraCaricaArray();
      else if (document instanceof AggiornamentoIscrizioneImpresaElencoOperatoriDocument)
        altraCarica = ((AggiornamentoIscrizioneImpresaElencoOperatoriDocument) document).getAggiornamentoIscrizioneImpresaElencoOperatori().getDatiImpresa().getAltraCaricaArray();
      else
        altraCarica = ((AggiornamentoAnagraficaImpresaDocument) document).getAggiornamentoAnagraficaImpresa().getDatiImpresa().getAltraCaricaArray();

      if (altraCarica != null && altraCarica.length > 0) {

        // Si cancellano le occorrenze con data termine incarico nulla
        this.sqlManager.update(
            "delete from IMPAZI where CODIMP4 = ? and FINAZI is null ",
            new Object[] { codiceImpresa });

        Long numazi = null;
        for (int i = 0; i < altraCarica.length; i++) {
          solaLettura = altraCarica[i].getSolaLettura();
          if (!solaLettura) {
            String cognomeAltraCarica = altraCarica[i].getCognome();
            String nomeAltraCarica = altraCarica[i].getNome();
            String titolo = altraCarica[i].getTitolo();
            String codfiscAltraCarica = altraCarica[i].getCodiceFiscale();
            String sessoAltraCarica = altraCarica[i].getSesso();
            Calendar dataNascitaAltraCarica = altraCarica[i].getDataNascita();
            String comuneNascitaAltraCarica = altraCarica[i].getComuneNascita();
            String provinciaNascitaAltraCarica = altraCarica[i].getProvinciaNascita();
            IndirizzoType indirizzoTypeAltraCarica = altraCarica[i].getResidenza();
            String indirizzoAltraCarica = indirizzoTypeAltraCarica.getIndirizzo();
            String numCivicoAltraCarica = indirizzoTypeAltraCarica.getNumCivico();
            String capAltraCarica = indirizzoTypeAltraCarica.getCap();
            String comuneAltraCarica = indirizzoTypeAltraCarica.getComune();
            String nazioneAltraCarica = indirizzoTypeAltraCarica.getNazione();
            String provinciaAltraCarica = indirizzoTypeAltraCarica.getProvincia();
            String note = altraCarica[i].getNote();
            String respDic = altraCarica[i].getResponsabileDichiarazioni();
            if ("0".equals(respDic))
              respDic ="2";
            AlboProfessionaleType alboProfessionaleAltraCarica = altraCarica[i].getAlboProfessionale();
            CassaPrevidenzaType cassaPrevidenzaAltraCarica = altraCarica[i].getCassaPrevidenza();
            String qualifica = altraCarica[i].getQualifica();

            parametri = new HashMap<String, Object>();
            parametri.put("cognome", cognomeAltraCarica);
            parametri.put("nome", nomeAltraCarica);
            parametri.put("titolo", titolo);
            parametri.put("codfisc", codfiscAltraCarica);
            parametri.put("sesso", sessoAltraCarica);
            campoData = null;
            if (dataNascitaAltraCarica != null)
              campoData = dataNascitaAltraCarica.getTime();
            parametri.put("dataNascita", campoData);
            parametri.put("comuneNascita", comuneNascitaAltraCarica);
            parametri.put("provinciaNascita", provinciaNascitaAltraCarica);
            parametri.put("indirizzo", indirizzoAltraCarica);
            parametri.put("numCivico", numCivicoAltraCarica);
            parametri.put("cap", capAltraCarica);
            parametri.put("comune", comuneAltraCarica);
            parametri.put("nazione", nazioneAltraCarica);
            parametri.put("provincia", provinciaAltraCarica);
            parametri.put("alboProfessionale", alboProfessionaleAltraCarica);
            parametri.put("cassaPrevidenza", cassaPrevidenzaAltraCarica);

            String codiceAltraCarica = this.aggiornaReferenti(parametri);

            // IMPAZI
            String nomtec = "" + cognomeAltraCarica + " " + nomeAltraCarica;
            Calendar dataInizio = altraCarica[i].getDataInizioIncarico();
            java.util.Date DataInizioIncarico = dataInizio.getTime();

            Calendar dataFine = altraCarica[i].getDataFineIncarico();
            java.util.Date DataFineIncarico = null;
            if (dataFine != null) DataFineIncarico = dataFine.getTime();

            Long incazi = null;
            if (qualifica != null && !"".equals(qualifica))
              incazi = new Long(qualifica);

            numazi = (Long) this.sqlManager.getObject(
                "select max(numazi) from impazi where codimp4=?",
                new Object[] { codiceImpresa });

            if (numazi == null)
              numazi = new Long(1);
            else
              numazi = new Long(numazi.longValue() + 1);

            this.sqlManager.update(
                "insert into IMPAZI(CODIMP4,NUMAZI,CODTEC,NOMTEC,INIAZI,FINAZI,INCAZI,NOTAZI,RESPDICH) values(?,?,?,?,?,?,?,?,?)",
                new Object[] { codiceImpresa, numazi, codiceAltraCarica, nomtec,
                    DataInizioIncarico, DataFineIncarico, incazi, note,respDic });
          }
        }

      }
      // Fine aggiornamento dati ALTRA CARICA O QUALIFICA

      // Inizio aggiornamento dati COLLABORATORE
      ReferenteImpresaType collaboratore[] = null;
      if (document instanceof RegistrazioneImpresaDocument)
        collaboratore = ((RegistrazioneImpresaDocument) document).getRegistrazioneImpresa().getDatiImpresa().getCollaboratoreArray();
      else if (document instanceof IscrizioneImpresaElencoOperatoriDocument)
        collaboratore = ((IscrizioneImpresaElencoOperatoriDocument) document).getIscrizioneImpresaElencoOperatori().getDatiImpresa().getCollaboratoreArray();
      else if (document instanceof AggiornamentoIscrizioneImpresaElencoOperatoriDocument)
        collaboratore = ((AggiornamentoIscrizioneImpresaElencoOperatoriDocument) document).getAggiornamentoIscrizioneImpresaElencoOperatori().getDatiImpresa().getCollaboratoreArray();
      else
        collaboratore = ((AggiornamentoAnagraficaImpresaDocument) document).getAggiornamentoAnagraficaImpresa().getDatiImpresa().getCollaboratoreArray();

      if (collaboratore != null && collaboratore.length > 0) {

        // Si cancellano le occorrenze con data termine incarico nulla
        this.sqlManager.update(
            "delete from G_IMPCOL where CODIMP = ? and INCFIN is null ",
            new Object[] { codiceImpresa });

        Long numcol = null;
        for (int i = 0; i < collaboratore.length; i++) {
          solaLettura = collaboratore[i].getSolaLettura();
          if (!solaLettura) {
            String cognomeCollaboratore = collaboratore[i].getCognome();
            String nomeCollaboratore = collaboratore[i].getNome();
            String titolo = collaboratore[i].getTitolo();
            String codfiscCollaboratore = collaboratore[i].getCodiceFiscale();
            String sessoCollaboratore = collaboratore[i].getSesso();
            Calendar dataNascitaCollaboratore = collaboratore[i].getDataNascita();
            String comuneNascitaCollaboratore = collaboratore[i].getComuneNascita();
            String provinciaNascitaCollaboratore = collaboratore[i].getProvinciaNascita();
            IndirizzoType indirizzoTypeCollaboratore = collaboratore[i].getResidenza();
            String indirizzoCollaboratore = indirizzoTypeCollaboratore.getIndirizzo();
            String numCivicoCollaboratore = indirizzoTypeCollaboratore.getNumCivico();
            String capCollaboratore = indirizzoTypeCollaboratore.getCap();
            String comuneCollaboratore = indirizzoTypeCollaboratore.getComune();
            String nazioneCollaboratore = indirizzoTypeCollaboratore.getNazione();
            String provinciaCollaboratore = indirizzoTypeCollaboratore.getProvincia();
            String note = collaboratore[i].getNote();
            String qualifica = collaboratore[i].getQualifica();
            AlboProfessionaleType alboProfessionaleCollaboratore = collaboratore[i].getAlboProfessionale();
            CassaPrevidenzaType cassaPrevidenzaCollaboratore = collaboratore[i].getCassaPrevidenza();

            parametri = new HashMap<String, Object>();
            parametri.put("cognome", cognomeCollaboratore);
            parametri.put("nome", nomeCollaboratore);
            parametri.put("titolo", titolo);
            parametri.put("codfisc", codfiscCollaboratore);
            parametri.put("sesso", sessoCollaboratore);
            campoData = null;
            if (dataNascitaCollaboratore != null)
              campoData = dataNascitaCollaboratore.getTime();
            parametri.put("dataNascita", campoData);
            parametri.put("comuneNascita", comuneNascitaCollaboratore);
            parametri.put("provinciaNascita", provinciaNascitaCollaboratore);
            parametri.put("indirizzo", indirizzoCollaboratore);
            parametri.put("numCivico", numCivicoCollaboratore);
            parametri.put("cap", capCollaboratore);
            parametri.put("comune", comuneCollaboratore);
            parametri.put("nazione", nazioneCollaboratore);
            parametri.put("provincia", provinciaCollaboratore);

            parametri.put("alboProfessionale", alboProfessionaleCollaboratore);
            parametri.put("cassaPrevidenza", cassaPrevidenzaCollaboratore);

            String codiceCollaboratore = this.aggiornaReferenti(parametri);

            // G_IMPCOL
            String nomtec = "" + cognomeCollaboratore + " " + nomeCollaboratore;
            Calendar dataInizio = collaboratore[i].getDataInizioIncarico();
            java.util.Date DataInizioIncarico = dataInizio.getTime();

            Calendar dataFine = collaboratore[i].getDataFineIncarico();
            java.util.Date DataFineIncarico = null;
            if (dataFine != null) DataFineIncarico = dataFine.getTime();

            Long inctip = null;
            if (qualifica != null && !"".equals(qualifica))
              inctip = new Long(qualifica);

            numcol = (Long) this.sqlManager.getObject(
                "select max(numcol) from g_impcol where codimp=?",
                new Object[] { codiceImpresa });

            if (numcol == null)
              numcol = new Long(1);
            else
              numcol = new Long(numcol.longValue() + 1);
            this.sqlManager.update(
                "insert into G_IMPCOL(CODIMP,CODTEC,NOMTEC,INCINI,INCFIN,INCTIP,NOTCOL,NUMCOL) values(?,?,?,?,?,?,?,?)",
                new Object[] { codiceImpresa, codiceCollaboratore, nomtec,
                    DataInizioIncarico, DataFineIncarico, inctip, note, numcol });
          }
        }

      }
      // Fine aggiornamento dati COLLABORATORE

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'aggiornamento dei dati dell'impresa", null, e);
    } catch (Exception e) {
      throw new GestoreException(
          "Errore nell'aggiornamento dei dati dell'impresa", null, e);
    }
  }

  /**
   * Vengono aggiornati i dati del tecnico (TEIM), con i dati passati nella
   * HashMap parametri
   *
   * @param parametri
   *
   * @throws GestoreException
   *
   * @return codiceReferente
   */
  private String aggiornaReferenti(HashMap<String, Object> parametri) throws GestoreException {
    String parametro = null;
    String select = null;
    String codiceReferente = null;

    try {
      String codfisc = (String) parametri.get("codfisc");
      // String piva = (String)parametri.get("piva");
      String cognome = (String) parametri.get("cognome");
      String nome = (String) parametri.get("nome");
      String sesso = (String) parametri.get("sesso");
      java.util.Date dataNascita = (java.util.Date) parametri.get("dataNascita");
      String comuneNascita = (String) parametri.get("comuneNascita");
      String provinviaNascita = (String) parametri.get("provinciaNascita");
      String indirizzo = (String) parametri.get("indirizzo");
      String numCivico = (String) parametri.get("numCivico");
      String cap = (String) parametri.get("cap");
      String comune = (String) parametri.get("comune");
      String titolo = (String) parametri.get("titolo");
      String nazione = (String) parametri.get("nazione");
      String provincia = (String) parametri.get("provincia");

      AlboProfessionaleType alboProfessionale = (AlboProfessionaleType) parametri.get("alboProfessionale");
      String tipologiaAlbo = alboProfessionale.getTipologia();
      String numIscrizione = alboProfessionale.getNumIscrizione();
      String provinciaIscrizione = alboProfessionale.getProvinciaIscrizione();
      Calendar dataIscrizione = alboProfessionale.getDataIscrizione();

      CassaPrevidenzaType cassaPrevidenza = (CassaPrevidenzaType) parametri.get("cassaPrevidenza");
      String tipologiaCassa = cassaPrevidenza.getTipologia();
      String numMatricola = cassaPrevidenza.getNumMatricola();

      // Tremite il codice fiscale o la partita iva
      // determino se esiste il codice del tecnico
      /*
       * select="select codtim from teim where "; if (codfisc != null &&
       * !"".equals(codfisc)) { select += "upper(cftim) = ?"; parametro =
       * codfisc.toUpperCase(); }else if (piva != null && !"".equals(piva)) { select
       * += "(pivatei) = ?"; parametro = piva.toUpperCase(); }
       */
      select = "select codtim from teim where upper(cftim) = ? order by codtim";
      parametro = codfisc.toUpperCase();

      codiceReferente = (String) this.sqlManager.getObject(select,
          new Object[] { parametro });

      Vector<DataColumn> elencoCampiTEIM = new Vector<DataColumn>();

      String intestazione = null;
      if (cognome != null) intestazione = cognome;
      if (nome != null) {
        if (intestazione != null)
          intestazione += " " + nome;
        else
          intestazione = nome;
      }

      elencoCampiTEIM.add(new DataColumn("TEIM.COGTIM", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, cognome)));
      elencoCampiTEIM.add(new DataColumn("TEIM.NOMETIM", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, nome)));

      elencoCampiTEIM.add(new DataColumn("TEIM.NOMTIM", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, intestazione)));

      elencoCampiTEIM.add(new DataColumn("TEIM.CFTIM", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, codfisc)));
      /*
       * elencoCampiTEIM.add(new DataColumn("TEIM.PIVATEI", new
       * JdbcParametro(JdbcParametro.TIPO_TESTO,piva)));
       */
      elencoCampiTEIM.add(new DataColumn("TEIM.SEXTIM", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, sesso)));
      elencoCampiTEIM.add(new DataColumn("TEIM.DNATIM", new JdbcParametro(
          JdbcParametro.TIPO_DATA, dataNascita)));
      elencoCampiTEIM.add(new DataColumn("TEIM.CNATIM", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, comuneNascita)));
      elencoCampiTEIM.add(new DataColumn("TEIM.PRONAS", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, provinviaNascita)));
      elencoCampiTEIM.add(new DataColumn("TEIM.INDTIM", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, indirizzo)));
      elencoCampiTEIM.add(new DataColumn("TEIM.NCITIM", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, numCivico)));
      elencoCampiTEIM.add(new DataColumn("TEIM.CAPTIM", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, cap)));
      elencoCampiTEIM.add(new DataColumn("TEIM.LOCTIM", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, comune)));
      elencoCampiTEIM.add(new DataColumn("TEIM.PROTIM", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, provincia)));
      String codiceIstat = null;
      if (provincia != null)
        codiceIstat = this.getCodiceISTAT(comune.toUpperCase(),
            provincia.toUpperCase());
      elencoCampiTEIM.add(new DataColumn("TEIM.CITTEC", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, codiceIstat)));

      select = "select tab1tip from tab1 where tab1cod='Ag010' and upper(tab1desc) = ?";
      Long nazimp = (Long) this.sqlManager.getObject(select,
          new Object[] { nazione.toUpperCase() });

      elencoCampiTEIM.add(new DataColumn("TEIM.NAZTIM", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, nazimp)));

      elencoCampiTEIM.add(new DataColumn("TEIM.DAESTERN", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, "1")));

      Long tmpLong = null;
      if (titolo != null && !"".equals(titolo)) tmpLong = new Long(titolo);
      elencoCampiTEIM.add(new DataColumn("TEIM.INCTEC", new JdbcParametro(
          JdbcParametro.TIPO_NUMERICO, tmpLong)));

      tmpLong = null;
      if (tipologiaAlbo != null && !"".equals(tipologiaAlbo))
        tmpLong = new Long(tipologiaAlbo);
      elencoCampiTEIM.add(new DataColumn("TEIM.TIPALB", new JdbcParametro(
          JdbcParametro.TIPO_NUMERICO, tmpLong)));
      elencoCampiTEIM.add(new DataColumn("TEIM.ALBTIM", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, numIscrizione)));

      java.util.Date campoData = null;
      if (dataIscrizione != null) campoData = dataIscrizione.getTime();
      elencoCampiTEIM.add(new DataColumn("TEIM.DATALB", new JdbcParametro(
          JdbcParametro.TIPO_DATA, campoData)));

      codiceIstat = this.getCodiceISTAT(provinciaIscrizione);
      elencoCampiTEIM.add(new DataColumn("TEIM.PROALB", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, codiceIstat)));

      tmpLong = null;
      if (tipologiaCassa != null && !"".equals(tipologiaCassa))
        tmpLong = new Long(tipologiaCassa);
      elencoCampiTEIM.add(new DataColumn("TEIM.TCAPRE", new JdbcParametro(
          JdbcParametro.TIPO_NUMERICO, tmpLong)));
      elencoCampiTEIM.add(new DataColumn("TEIM.NCAPRE", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, numMatricola)));

      if (codiceReferente != null && !"".equals(codiceReferente)) {
        // L'occorrenza di TEIM è presente in db e si deve aggiornare.
        elencoCampiTEIM.add(new DataColumn("TEIM.CODTIM", new JdbcParametro(
            JdbcParametro.TIPO_TESTO, codiceReferente)));

        DataColumnContainer containerITEIM = new DataColumnContainer(
            elencoCampiTEIM);
        containerITEIM.getColumn("TEIM.CODTIM").setChiave(true);
        containerITEIM.getColumn("TEIM.CODTIM").setObjectOriginalValue(
            codiceReferente);

        /*
         * //Devo forzare il valore originale dei partita iva ad //un valore non
         * nullo altrimenti non vedo il cambiamento Date datamtp = new Date();
         * datamtp.setTime(1);
         * containerITEIM.getColumn("TEIM.PIVATEI").setObjectOriginalValue(" ");
         */

        containerITEIM.update("TEIM", sqlManager);
      } else {

        if (geneManager.isCodificaAutomatica("TEIM", "CODTIM")) {
          // Setto il codice del tecnico delle imprese come chiave altrimenti
          // non ritorna sulla riga giusta
          codiceReferente = geneManager.calcolaCodificaAutomatica("TEIM",
              "CODTIM");
        } else {
          // IMPORTANTE: SOLUZIONE TEMPORANEA, CRISTIAN DEVE FARE SAPERE LA
          // VERSIONE FINALE
          // Devo determinare il nuovo codice di TEIM secondo la forma "PAT001"
          select = "select max("
              + this.sqlManager.getDBFunction("SUBSTR", new String[] { "CODTIM",
                  "4", "6" })
              + ")";
          select += " from teim where codtim like 'PAT%'";

          codiceReferente = "PAT001";
          String codice = (String) this.sqlManager.getObject(select, null);
          if (codice != null && !"".equals(codice)) {
            Long progressivo = Long.valueOf(codice);
            progressivo = new Long(progressivo.longValue() + 1);
            codiceReferente = "PAT"
                + UtilityStringhe.fillLeft(progressivo.toString(), '0', 3);
          }
        }

        elencoCampiTEIM.add(new DataColumn("TEIM.CODTIM", new JdbcParametro(
            JdbcParametro.TIPO_TESTO, codiceReferente)));

        DataColumnContainer containerITEIM = new DataColumnContainer(
            elencoCampiTEIM);
        containerITEIM.insert("TEIM", sqlManager);
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'aggiornamento dei dati del tecnico", null, e);
    } catch (Exception e) {
      throw new GestoreException(
          "Errore nell'aggiornamento dei dati del tecnico", null, e);
    }
    return codiceReferente;
  }

  /**
   * Vengono inserite le righe per gli altri indirizzi
   *
   * @param impresa
   * @param codiceImpresa
   *
   * @throws GestoreException
   *
   *
   */
  private void gestioneAltriIndirizzi(ImpresaType impresa, String codiceImpresa)
      throws GestoreException {
    String sql = "";

    sql = "delete from IMPIND where CODIMP5 = ?";
    try {
      // cancello tutte le eventuali occorre presenti in db
      this.sqlManager.update(sql, new Object[] { codiceImpresa });

      // Inserimento degli indirizzi passati da portale
      IndirizzoEstesoType datiIndirizzi[] = impresa.getIndirizzoArray();
      if (datiIndirizzi.length > 0) {
        String select = "select tab1tip from tab1 where tab1cod='Ag010' and upper(tab1desc) = ?";
        for (int i = 0; i < datiIndirizzi.length; i++) {
          sql = "select max(INDCON) from IMPIND where CODIMP5 = ? ";
          Long indcon = (Long) this.sqlManager.getObject(sql,
              new Object[] { codiceImpresa });
          Long newIndocon = new Long(1);
          if (indcon != null && indcon.longValue() > 0)
            newIndocon = new Long(indcon.longValue() + 1);
          String tipoIndirizzo = datiIndirizzi[i].getTipoIndirizzo();
          String indirizzo = datiIndirizzi[i].getIndirizzo();
          String numCivico = datiIndirizzi[i].getNumCivico();
          String cap = datiIndirizzi[i].getCap();
          String comune = datiIndirizzi[i].getComune();
          String provincia = datiIndirizzi[i].getProvincia();
          String nazione = datiIndirizzi[i].getNazione();
          String telefono = datiIndirizzi[i].getTelefono();
          String fax = datiIndirizzi[i].getFax();

          String codiceIstat = null;
          if (provincia != null)
            codiceIstat = this.getCodiceISTAT(comune.toUpperCase(),
                provincia.toUpperCase());

          Long nazimp = (Long) this.sqlManager.getObject(select,
              new Object[] { nazione.toUpperCase() });

          sql = "insert into IMPIND(CODIMP5,INDCON,INDTIP,INDIND,INDNC,INDCAP,INDLOC,INDPRO,INDTEL,INDFAX,"
              + "CODCIT,NAZIMP) values(?,?,?,?,?,?,?,?,?,?,?,?)";
          this.sqlManager.update(sql, new Object[] { codiceImpresa, newIndocon,
              tipoIndirizzo, indirizzo, numCivico, cap, comune, provincia,
              telefono, fax, codiceIstat, nazimp });
        }
      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'aggiornamento degli altri indirizzi", null, e);
    } catch (Exception e) {
      throw new GestoreException(
          "Errore nell'aggiornamento degli altri indirizzi", null, e);
    }
  }

  /**
   * Vengono inserite le righe i dati annuali di un'impresa
   *
   * @param impresa
   * @param codiceImpresa
   *
   * @throws GestoreException
   *
   *
   */
  private void gestioneDatiAnnualiImpresa(ImpresaType impresa, String codiceImpresa)
      throws GestoreException {
    String sql = "";

    try {

      // Inserimento degli indirizzi passati da portale
      DatoAnnuoImpresaType datiAnnui[] = impresa.getDatoAnnuoArray();
      if (datiAnnui.length > 0) {
        for (int i = 0; i < datiAnnui.length; i++) {
          int anno = datiAnnui[i].getAnno();
          Long dipendenti = null;
          if (datiAnnui[i].isSetDipendenti()) {
            int numDipendenti = datiAnnui[i].getDipendenti();
            dipendenti = new Long(numDipendenti);
          }

          sql="select count(codimp) from impanno where codimp=? and anno=?";
          Long conteggio = (Long)sqlManager.getObject(sql, new Object[]{codiceImpresa, new Long(anno)});
          if (conteggio!= null && conteggio.longValue()>0) {
            sql = "update IMPANNO set NUMDIP =? where CODIMP =? and ANNO = ?";
            this.sqlManager.update(sql, new Object[] {dipendenti,
                codiceImpresa,new Long(anno) });
          }else if ((conteggio == null || conteggio.longValue() == 0) && dipendenti != null) {
            sql = "insert into IMPANNO(CODIMP,ANNO,NUMDIP) values(?,?,?)";
            this.sqlManager.update(sql, new Object[] { codiceImpresa, new Long(anno),
                dipendenti });
          }



        }
      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella gestione dei Dati annuali dell'impresa", null, e);
    } catch (Exception e) {
      throw new GestoreException(
          "Errore nella gestione dei Dati annuali dell'impresa", null, e);
    }
  }

  /**
   * Viene effettuata la registrazione al portale(messaggi FS1)
   *
   * @param fileAllegatoManager
   * @param idcom
   * @param messaggioMap
   * @return true: vi sono stati errori false: non vi sono stati errori
   * @throws GestoreException
   *
   *
   */

  public boolean insertRegistrazionePortale(
      FileAllegatoManager fileAllegatoManager, Long idcom, Map<String, Object> messaggioMap,String comstato)
      throws GestoreException {

    String select = null;
    String sel_u = null;

    boolean errori = false;

    //variabili per tracciatura eventi
    int livEvento = 1;
    String codEvento = "GA_ACQUISIZIONE_REGANAG";
    String oggEvento = "";
    String descrEvento = "Acquisizione registrazione anagrafica da portale Appalti";
    String errMsgEvento = "";

    // Occorrenze per le quali in w_invcom è richiesta la registrazione
    String idprg = "PA";
    String comtipo = "FS1";
    String user = null;
    try{
      sel_u = "select IDCOM,COMKEY1,COMDATASTATO from w_invcom where idprg = ? and idcom = ? and comstato = ? and comtipo = ? order by IDCOM";

      // List listaIDCOM = null;
      Vector<?> vectorIDCOM = null;
      try {
        vectorIDCOM = this.sqlManager.getVector(sel_u, new Object[] { idprg, idcom,
            comstato, comtipo });

      } catch (SQLException e) {
        livEvento = 3;
        errMsgEvento = e.getMessage();
        throw new GestoreException(
            "Errore nella lettura della tabella W_INVCOM ", null, e);
      }
      if (vectorIDCOM != null && vectorIDCOM.size() > 0) {



        idcom = SqlManager.getValueFromVectorParam(vectorIDCOM, 0).longValue();
        user = SqlManager.getValueFromVectorParam(vectorIDCOM, 1).getStringValue();
        java.sql.Date comDataStato = (java.sql.Date) SqlManager.getValueFromVectorParam(vectorIDCOM, 2).getValue();

        // Vengono letti i documenti associati ad ogni occorrenza di di W_INVCOM
        select = "select idprg,iddocdig from w_docdig where DIGENT = ? and digkey1 = ? and idprg = ? and dignomdoc = ? ";
        String digent = "W_INVCOM";
        String idprgW_DOCDIG = "PA";

        Vector<?> datiW_DOCDIG = null;
        try {
          datiW_DOCDIG = this.sqlManager.getVector(select, new Object[] { digent,
              idcom.toString(), idprgW_DOCDIG, CostantiAppalti.nomeFileXML_IscrizioneImpresa });

        } catch (SQLException e) {
          livEvento = 3;
          errMsgEvento = e.getMessage();
          throw new GestoreException(
              "Errore nella lettura della tabella W_DOCDIG ", null, e);
        }
        String idprgW_INVCOM = null;
        Long iddocdig = null;
        if (datiW_DOCDIG != null) {
          if (((JdbcParametro) datiW_DOCDIG.get(0)).getValue() != null)
            idprgW_INVCOM = ((JdbcParametro) datiW_DOCDIG.get(0)).getStringValue();

          if (((JdbcParametro) datiW_DOCDIG.get(1)).getValue() != null)
            iddocdig = ((JdbcParametro) datiW_DOCDIG.get(1)).longValue();

          // Lettura del file xml immagazzinato nella tabella W_DOCDIG
          BlobFile fileAllegato = null;
          try {
            fileAllegato = fileAllegatoManager.getFileAllegato(idprgW_INVCOM,
                iddocdig);
          } catch (Exception e) {
            livEvento = 3;
            errMsgEvento = e.getMessage();
            throw new GestoreException(
                "Errore nella lettura del file allegato presente nella tabella W_DOCDIG",
                null, e);
          }
          String xml = null;
          if (fileAllegato != null && fileAllegato.getStream() != null) {
            xml = new String(fileAllegato.getStream());
            RegistrazioneImpresaDocument document;
            try {
              document = RegistrazioneImpresaDocument.Factory.parse(xml);

              String codiceDitta = null;
              String codfisc = document.getRegistrazioneImpresa().getDatiImpresa().getImpresa().getCodiceFiscale();
              codfisc = codfisc.toUpperCase();
              String partitaiva = document.getRegistrazioneImpresa().getDatiImpresa().getImpresa().getPartitaIVA();
              String userNome = document.getRegistrazioneImpresa().getAccount().getUsername();
              String ragSociale = document.getRegistrazioneImpresa().getDatiImpresa().getImpresa().getRagioneSociale();
              String pec = document.getRegistrazioneImpresa().getDatiImpresa().getImpresa().getRecapiti().getPec();
              String mail = document.getRegistrazioneImpresa().getDatiImpresa().getImpresa().getRecapiti().getEmail();
              String gruppoiva = document.getRegistrazioneImpresa().getDatiImpresa().getImpresa().getGruppoIva();

              String tipoImpresa = document.getRegistrazioneImpresa().getDatiImpresa().getImpresa().getTipoImpresa();
              Long tipimp = null;
              if (tipoImpresa != null && !"".equals(tipoImpresa))
                tipimp = new Long(tipoImpresa);
              boolean saltareControlloPivaNulla = anagraficaManager.saltareControlloObbligPiva(tipimp);
              if("1".equals(gruppoiva))
                saltareControlloPivaNulla=false;
              if (!saltareControlloPivaNulla
                  && partitaiva != null
                  && !"".equals(partitaiva))
                partitaiva = partitaiva.toUpperCase();

              String modo = "UPDATE";
              String msg = null;
              // Controllo di unicità dell'user e della mail

              select = "select count(IDUSER) from W_PUSER where USERNOME = ?";
              Long numOccorrenzeUser = (Long) this.sqlManager.getObject(select,
                  new Object[] { userNome });
              if (numOccorrenzeUser != null && numOccorrenzeUser.longValue() > 0) {
                logger.error("IDCOM="
                    + idcom.toString()
                    + ":Esiste già un'impresa registrata sul portale con nome utente "
                    + userNome);
                msg = "<br><br>&nbsp;Esiste già un'impresa registrata sul portale con nome utente <b>"
                    + userNome
                    + "</b>.";
                messaggioMap.put("tipo", "DUPL-BACKOFFICE");
                messaggioMap.put("valore", msg);
                errori = true;
                livEvento = 3;
                errMsgEvento = "IDCOM="
                    + idcom.toString()
                    + ":Esiste già un'impresa registrata sul portale con nome utente "
                    + userNome;
                return errori;
              }
              // Controllo di unicità del codice fiscale e partiva Iva
              // Nel caso di libero professionista se è impostato il tabellato
              // G_045 la partita iva può essere nulla
              if (saltareControlloPivaNulla
                  && (partitaiva == null || "".equals(partitaiva)))
                select = "select count(IDUSER) from W_PUSER, IMPR where upper(cfimp)=? and pivimp = ? "
                    + "and userent=? and userkey1=codimp";
              else
                select = "select count(IDUSER) from W_PUSER, IMPR where upper(cfimp)=? and upper(pivimp)=? "
                    + "and userent=? and userkey1=codimp";
              Long numOccorrenzecfpi = (Long) this.sqlManager.getObject(select,
                  new Object[] { codfisc, partitaiva, "IMPR" });
              if (numOccorrenzecfpi != null && numOccorrenzecfpi.longValue() > 0) {
                String partitaivaTmp = partitaiva;
                if (partitaivaTmp == null || "".equals(partitaivaTmp))
                  partitaivaTmp = "nulla";
                logger.error("IDCOM="
                    + idcom.toString()
                    + ":Esiste già un'impresa registrata sul portale con codice fiscale "
                    + codfisc
                    + " e partita Iva"
                    + partitaivaTmp);
                msg = "<br><br>&nbsp;Esiste già un'impresa registrata sul portale con codice fiscale <b>"
                    + codfisc
                    + "</b> e partita Iva <b>"
                    + partitaivaTmp
                    + "</b>.";
                messaggioMap.put("tipo", "DUPL-BACKOFFICE");
                messaggioMap.put("valore", msg);
                errori = true;
                livEvento = 3;
                errMsgEvento = "IDCOM="
                    + idcom.toString()
                    + ":Esiste già un'impresa registrata sul portale con codice fiscale "
                    + codfisc
                    + " e partita Iva"
                    + partitaivaTmp;
                return errori;
              }

              /*
              String selectCodimp = "";
              Object parametri[] = null;
              // Impresa da inserire o da aggiornare?
              Long numImpr = null;
              if (saltareControlloPivaNulla
                  && (partitaiva == null || "".equals(partitaiva))) {
                select = "select count(codimp) from impr where upper(cfimp)=? and pivimp is null";
                numImpr = (Long) sqlManager.getObject(select,
                    new Object[] { codfisc });
              } else {
                select = "select count(codimp) from impr where upper(cfimp)=? and upper(pivimp)=?";
                numImpr = (Long) sqlManager.getObject(select, new Object[] {
                    codfisc, partitaiva });
                if (numImpr == null || numImpr.longValue() == 0) {
                  select = "select count(codimp) from impr where upper(pivimp)=? and (cfimp is null or upper(cfimp) = ?)";
                  numImpr = (Long) sqlManager.getObject(select, new Object[] {
                      partitaiva, partitaiva });
                  if (numImpr == null || numImpr.longValue() == 0) {
                    select = "select count(codimp) from impr where upper(cfimp)=? and pivimp is null";
                    numImpr = (Long) sqlManager.getObject(select,
                        new Object[] { codfisc });
                    if (numImpr != null && numImpr.longValue() == 1) {
                      selectCodimp = "select codimp from impr where upper(cfimp)=? and pivimp is null";
                      parametri = new Object[] { codfisc };
                    }
                  } else if (numImpr != null && numImpr.longValue() == 1) {
                    selectCodimp = "select codimp from impr where upper(pivimp)=? and (cfimp is null or upper(cfimp) = ?)";
                    parametri = new Object[] { partitaiva, partitaiva };
                  }
                } else if (numImpr != null && numImpr.longValue() == 1) {
                  selectCodimp = "select codimp from impr where upper(cfimp)=? and upper(pivimp)=?";
                  parametri = new Object[] { codfisc, partitaiva };
                }
              }

              if (numImpr == null
                  || numImpr.longValue() == 0
                  || numImpr.longValue() > 1) {
                // Se non esiste nessuna impr con codfisc e piva dati, oppure ne
                // esistono più di 1
                // si inserisce la nuova impresa, determinando il codice con la
                // codifica automatica
                codiceDitta = geneManager.calcolaCodificaAutomatica("IMPR",
                    "CODIMP");
                modo = "INS";
              } else {

                if (saltareControlloPivaNulla
                    && (partitaiva == null || "".equals(partitaiva)))
                  codiceDitta = (String) sqlManager.getObject(
                      "select codimp from impr where upper(cfimp)=? and pivimp is null",
                      new Object[] { codfisc });
                else
                  codiceDitta = (String) sqlManager.getObject(selectCodimp,
                      parametri);

              }
              this.aggiornaDitta(document, codiceDitta, modo);
            */
              String datiDitta[] = this.controlloEsistenzaDitta(partitaiva, codfisc, gruppoiva);
              codiceDitta = datiDitta[0];
              modo = datiDitta[1];
              if("UPDATE".equals(modo)){

                String msgImpresa=this.controlloDatiImpresa(document, codiceDitta);

                //Controllo Legale Rappresentante
                String msgLegale = this.controlloDatiReferenti( document, codiceDitta, "LEGALE");

                //Controllo Direttore Tecnico
                String msgDirettore = this.controlloDatiReferenti( document, codiceDitta, "DIRETTORE");

                //Controllo Azionista
                //String msgAzionista = pgManager.controlloDatiReferenti( document, codiceDitta, "AZIONISTA");

                //Controllo Soggetti con altre cariche o qualifiche
                String msgAltreCariche = this.controlloDatiReferenti( document, codiceDitta, "ALTRECARICHE");

                //Controllo Collaboratore
                String msgCollaboratore = this.controlloDatiReferenti( document, codiceDitta, "COLLABORATORE");

                String messaggioVariazioni = msgImpresa + msgLegale + msgDirettore + msgAltreCariche + msgCollaboratore;

                //Si controlla se è variata la pec o ci sono modifiche ai referenti
                //Informazione che serve per impostare lo stato della nota ad 'aperto'
                RecapitiType recapiti = document.getRegistrazioneImpresa().getDatiImpresa().getImpresa().getRecapiti();
                String pecMsg = "";
                String pecDb = "";
                if(recapiti!=null){
                  pecMsg = recapiti.getPec();
                  try {
                    pecDb = (String)sqlManager.getObject("select emai2ip from impr where codimp = ?", new Object[]{codiceDitta});
                  } catch (SQLException e) {
                    livEvento = 3;
                    errMsgEvento = e.getMessage();
                    throw new GestoreException("Errore nella lettura del campo IMPR.EMAI2IP",null, e);
                  }
                  if(pecDb==null)
                    pecDb="";
                  if(pecMsg==null)
                    pecMsg="";
                }
                boolean impostareStatoNota = false;
                if(!pecMsg.equals(pecDb) || !"".equals(msgLegale) || !"".equals(msgDirettore) || !"".equals(msgAltreCariche) || !"".equals(msgCollaboratore))
                  impostareStatoNota = true;


                //Viene popolata la G_NOTEAVVISI
                if(messaggioVariazioni!=null && !"".equals(messaggioVariazioni)){
                  this.InserisciVariazioni(messaggioVariazioni, codiceDitta,"INS",null,comDataStato,impostareStatoNota,null);
                }
              }
              this.aggiornaDitta(document, codiceDitta, modo);
              oggEvento = codiceDitta;

              // Popolamento della w_puser
              long newIdUser = 0;
              select = "select max(IDUSER) from W_PUSER";
              Long maxIdUseer = (Long) this.sqlManager.getObject(select, null);
              if (maxIdUseer == null || maxIdUseer.longValue() == 0)
                newIdUser++;
              else
                newIdUser = maxIdUseer.longValue() + 1;

              messaggioMap.put("idUser", new Long(newIdUser));
              messaggioMap.put("codImpr", codiceDitta);

              if (ragSociale.length() > 120)
                ragSociale = ragSociale.substring(0, 120);
              String update = "insert into W_PUSER (IDUSER,USERNOME,USERDESC,USERENT,USERKEY1) values (?,?,?,?,?)";
              this.sqlManager.update(update, new Object[] { new Long(newIdUser),
                  userNome, ragSociale, "IMPR", codiceDitta});

              // Interrogazione del servizio del portale
              PortaleAliceProxy proxy = new PortaleAliceProxy();
              // indirizzo del servizio letto da properties
              String endPoint = ConfigManager.getValore(CostantiGenerali.PROP_URL_WEB_SERVICE_PORTALE_ALICE);
              proxy.setEndpoint(endPoint);
              EsitoOutType risultato = proxy.attivaImpresa(user, ragSociale);

              if (!risultato.isEsitoOk()) {
                String codiceErrore = risultato.getCodiceErrore();
                if (codiceErrore != null && !"".equals(codiceErrore)) {
                  if (codiceErrore.indexOf("UNEXP-ERR") >= 0) {
                    msg = codiceErrore.substring(codiceErrore.indexOf(" "));
                    logger.error("Errore durante la chiamata del servizio attivaImpresa: "
                        + msg);
                    errMsgEvento = "Errore durante la chiamata del servizio attivaImpresa: "+ msg;
                    msg = "<br>&nbsp;" + msg;

                  }
                  if (codiceErrore.indexOf("UNKNOWN-USER") >= 0) {
                    logger.error("Errore durante la chiamata del servizio attivaImpresa: L'user "
                        + userNome
                        + " non risulta definito");
                    errMsgEvento = "Errore durante la chiamata del servizio attivaImpresa: L'user "
                        + userNome
                        + " non risulta definito";
                    msg = "<br>&nbsp;L'user <b>"
                        + userNome
                        + "</b> non è definito.<br> ";

                  }
                  if (codiceErrore.indexOf("INVALID-EMAIL") >= 0) {
                    logger.error("Errore durante la chiamata del servizio attivaImpresa: L'indirizzo mail "
                        + mail
                        + " oppure pec "
                        + pec
                        + " non è valido");
                    errMsgEvento ="Errore durante la chiamata del servizio attivaImpresa: L'indirizzo mail "
                        + mail
                        + " oppure pec "
                        + pec
                        + " non è valido";
                    msg = "<br>&nbsp;L'indirizzo mail <b>"
                        + mail
                        + " oppure pec "
                        + pec
                        + "</b> non è valido.<br> ";

                  }

                }

                messaggioMap.put("tipo", "DUPL-Portale");
                messaggioMap.put("valore", msg);
                livEvento = 3;
                oggEvento = "";
                throw new GestoreException(
                    "Il servizio di registrazione dell'impresa ha ritornato un errore",
                    "attivaImpresa");
              }

              // Aggiornamento dello stato delle occorrenze per le quali in
              // w_invcom è richiesta l' Iscrizione in elenco
              this.aggiornaStatoW_INVOCM(idcom, "6", codiceDitta);

            } catch (XmlException e) {
              livEvento = 3;
              errMsgEvento = e.getMessage();
              throw new GestoreException("Errore nella lettura del file XML",
                  null, e);
            } catch (SQLException e) {
              livEvento = 3;
              errMsgEvento = e.getMessage();
              throw new GestoreException("Errore nella lettura di IMPR", null, e);
            } catch (RemoteException e) {
              String codice = "registrazionePortale";
              String messaggio = e.getMessage();
              if (messaggio.indexOf("Connection refused") > 0)
                codice += ".noServizio";
              if (messaggio.indexOf("Connection timed out") > 0)
                codice += ".noServer";
              livEvento = 3;
              errMsgEvento = e.getMessage();
              throw new GestoreException(
                  "Errore durante la registrazione sul portale ", codice, e);

            } catch (Exception e) {
              livEvento = 3;
              throw new GestoreException("Errore nella connessione al portale",
                  null, e);
            }

          } else {

            errori = true;
            if(!"7".equals(comstato)){
              this.aggiornaStatoW_INVOCM(idcom, "7", "");
              }
          }

        } else {

          errori = true;
          if(!"7".equals(comstato)){
            this.aggiornaStatoW_INVOCM(idcom, "7", "");
            }
        }

      }
    }finally {
      //Tracciatura eventi
      try {
        LogEvento logEvento = new LogEvento();
        logEvento.setCodApplicazione("PG");
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(oggEvento);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(descrEvento + " (cod.operatore: " + user + ", id.comunicazione: " + idcom + ")");
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
      }
    }

    return errori;

  }

  /**
   * Viene aggiornato lo stato di una occorrenza di W_INVCOM. Riguarda solo il
   * caso di registrazione impresa
   *
   * @param idcom
   * @param stato
   *
   * @throws GestoreException
   *
   *
   */
  public void aggiornaStatoW_INVOCM(Long idcom, String stato, String codiceDitta)
      throws GestoreException {
    try {
      // Solo se registrazione impresa completata correttamente, aggiorna la
      // data richiesta registrazione
      // e la data registrazione in IMPR
      if ("6".equals(stato)) {
        this.sqlManager.update(
            "update IMPR set DINVREG = (select COMDATASTATO from W_INVCOM where IDPRG=? and IDCOM=?), DELAREG = ? where CODIMP = ?",
            new Object[] { "PA", idcom.toString(),
                new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),
                codiceDitta });
      }
      this.sqlManager.update(
          "update W_INVCOM set COMSTATO = ?, COMDATASTATO = ? where IDPRG=? and IDCOM=?",
          new Object[] { stato,
              new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),
              "PA", idcom.toString() });
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'aggiornamento dell'entità W_INVCOM", null, e);
    }
  }

  /**
   * Viene allineato il tipgarg del lotto con il tipgar della tornata.
   *
   * @param nGaraDestinazione
   * @param codiceGaraDestinazione
   *
   * @throws GestoreException
   *
   *
   */
  public void updateTipgargLotto(String nGaraDestinazione,
      String codiceGaraDestinazione) throws GestoreException {
    String select = "";
    select = "select tipgar from torn where codgar=?";

    try {
      Long tipgar = (Long) this.sqlManager.getObject(select,
          new Object[] { codiceGaraDestinazione });
      this.sqlManager.update("update gare set tipgarg = ? where ngara = ? ",
          new Object[] { tipgar, nGaraDestinazione });
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'allineamento dei tipo procedura del lotto"
              + nGaraDestinazione, null, e);
    }

  }

  /**
   * Viene prelevato il valore del campo TAB2D2 per il tabellato 'A1z03' con
   * TAB2D1 filtrato per il profilo attivo
   *
   * @param profiloAttivo
   * @return valore del campo TAB2D2
   *
   * @throws GestoreException
   *
   *
   */
  public String getFiltroTipoGara(String profiloAttivo) throws GestoreException {

    String filtro = "";

    long occorrenzeTAB2 = geneManager.countOccorrenze("TAB2", "TAB2COD=?",
        new Object[] { "A1z03" });
    try {
      if (occorrenzeTAB2 > 0) {

        String descTabellato = (String) this.sqlManager.getObject(
            "select tab2d2 from tab2 where tab2cod=? and tab2d1=?",
            new Object[] { "A1z03", profiloAttivo });

        if (descTabellato != null) {
          filtro = descTabellato;
        } else {
          descTabellato = (String) this.sqlManager.getObject(
              "select tab2d2 from tab2 where tab2cod=? and tab2d1=?",
              new Object[] { "A1z03", "default" });
          if (descTabellato != null) filtro = descTabellato;
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante la lettura della tabellato 'A1z03' ", null, e);
    }

    return filtro;
  }

  /**
   * Viene letta dalla tabella CONFOPECO l'informazione per l'inizializzazione
   * del numero di ditte iscritte e del numero minime ditte da iscrivere nella
   * gara
   *
   * @param gareTipgarg
   * @param tipgen
   * @param importoTotaleBaseDAsta
   * @param codiceGara
   * @param whereFasiRicezione
   *
   * @return vettore contenente il numero minimo di operatori e numero di
   *         operatori selezionati
   *
   * @throws GestoreException
   *
   *
   */
  public Long[] getNumeroMinimoDitte(int gareTipgarg, String tipgen,
      Double importoTotaleBaseDAsta, String codiceGara,
      String whereFasiRicezione) throws GestoreException {
    Long ret[] = null;

    if (importoTotaleBaseDAsta == null)
      importoTotaleBaseDAsta = new Double(0);

    String select = "select ISABILITATO, NUMMINOP, NMAXSEL from CONFOPECO where TIPOPROCEDURA = ?  and TIPOLOGIA = ? and "
        + "((DAIMPORTO is not null and AIMPORTO is not null and DAIMPORTO <> 0 and AIMPORTO <> 0 and DAIMPORTO <= ? and AIMPORTO > ? ) or "
        + "((DAIMPORTO is null or DAIMPORTO = 0) and AIMPORTO > ? ) or ((AIMPORTO is null or AIMPORTO = 0) and DAIMPORTO <= ?) "
        + "or((DAIMPORTO is null or DAIMPORTO =0 ) and ((AIMPORTO is null or AIMPORTO=0)) ))";

    Vector<?> vect;
    try {
      vect = this.sqlManager.getVector(select, new Object[] { new Long(gareTipgarg),
          new Long(tipgen), importoTotaleBaseDAsta, importoTotaleBaseDAsta,
          importoTotaleBaseDAsta, importoTotaleBaseDAsta });
      if (vect != null && vect.size() > 0) {
        String abilitato = (String) ((JdbcParametro) vect.get(0)).getValue();
        boolean isAbilitato = "1".equals(abilitato);
        Long numeroMinimoOperatori = (Long) ((JdbcParametro) vect.get(1)).getValue();
        if (numeroMinimoOperatori == null) numeroMinimoOperatori = new Long(0);

        Long numSupDitteSel = (Long) ((JdbcParametro) vect.get(2)).getValue();
        if (numSupDitteSel == null) numSupDitteSel = new Long(0);

        if (isAbilitato) {

          String condizioneWhere = "NGARA5 = ? ";

          if (whereFasiRicezione != null && whereFasiRicezione.length() > 0)
            condizioneWhere = condizioneWhere + whereFasiRicezione;

          long numeroOperatoriSelezionati = geneManager.countOccorrenze("DITG",
              condizioneWhere, new Object[] { codiceGara });

          ret = new Long[] { numeroMinimoOperatori,
              new Long(numeroOperatoriSelezionati), new Long(numSupDitteSel)};
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante la lettura del numero minimo degli operatori da selezionare ",
          null, e);
    }
    return ret;

  }

  public HashMap<String, Object> getDatiGaraRibassoImporto(String codiceGara)
      throws SQLException {
    HashMap<String, Object> ret = new HashMap<String, Object>();

    Vector<?> datiGara = this.sqlManager.getVector(
        "select ribcal, onprge, impapp, impsic, impnrl, sicinc, onsogrib from gare "
            + "where ngara = ?", new Object[] { codiceGara });
    Long ribcal = null;
    Double onprge = null;
    Double impapp = null;
    Double impsic = null;
    Double impnrl = null;
    String sicinc = "";
    String onsogrib = "";

    if (datiGara != null && datiGara.size() > 0) {
      if (((JdbcParametro) datiGara.get(0)).getValue() != null)
        ribcal = (Long) ((JdbcParametro) datiGara.get(0)).getValue();

      if (((JdbcParametro) datiGara.get(1)).getValue() != null)
        onprge = (Double) ((JdbcParametro) datiGara.get(1)).getValue();
      if (onprge == null) onprge = new Double(0);

      if (((JdbcParametro) datiGara.get(2)).getValue() != null)
        impapp = (Double) ((JdbcParametro) datiGara.get(2)).getValue();
      if (impapp == null) impapp = new Double(0);

      if (((JdbcParametro) datiGara.get(3)).getValue() != null)
        impsic = (Double) ((JdbcParametro) datiGara.get(3)).getValue();
      if (impsic == null) impsic = new Double(0);

      if (((JdbcParametro) datiGara.get(4)).getValue() != null)
        impnrl = (Double) ((JdbcParametro) datiGara.get(4)).getValue();
      if (impnrl == null) impnrl = new Double(0);

      if (((JdbcParametro) datiGara.get(5)).getValue() != null)
        sicinc = (String) ((JdbcParametro) datiGara.get(5)).getValue();

      if (((JdbcParametro) datiGara.get(6)).getValue() != null)
        onsogrib = (String) ((JdbcParametro) datiGara.get(6)).getValue();

      ret.put("ribcal", ribcal);
      ret.put("onprge", onprge);
      ret.put("impapp", impapp);
      ret.put("impsic", impsic);
      ret.put("impnrl", impnrl);
      ret.put("sicinc", sicinc);
      ret.put("onsogrib", onsogrib);
    }

    return ret;
  }

  /**
   * Metodo che calcola i seguenti valori: importo1 = IMPAPP.GARE – IMPSIC.GARE
   * – IMPNRL.GARE – ONPRGE.GARE - importo2 = IMPNRL.GARE + IMPSIC.GARE se
   * SICINC.GARE =1, = IMPNRL.GARE altrimenti
   *
   * Tali valori vengono inseriti nel Request
   *
   * @param pageContext
   * @param codiceGara
   * @return
   */
  public void setDatiCalcoloImportoOfferto(PageContext pageContext,
      String codiceGara) throws JspException {

    try {
      Long ribcal = null;
      double importo1;
      Double onprge = null;
      Double impapp = null;
      Double impsic = null;
      Double impnrl = null;
      String sicinc = "";
      String onsogrib = "";
      double importo2;

      HashMap<String, Object> datiGara = this.getDatiGaraRibassoImporto(codiceGara);

      if (datiGara != null && datiGara.size() > 0) {
        ribcal = (Long) datiGara.get("ribcal");
        pageContext.setAttribute("ribcal", ribcal, PageContext.REQUEST_SCOPE);

        onprge = (Double) datiGara.get("onprge");
        impapp = (Double) datiGara.get("impapp");
        impsic = (Double) datiGara.get("impsic");
        impnrl = (Double) datiGara.get("impnrl");
        sicinc = (String) datiGara.get("sicinc");
        onsogrib = (String) datiGara.get("onsogrib");

        // importo1 = impapp.doubleValue() - impsic.doubleValue() -
        // impnrl.doubleValue() - onprge.doubleValue();
        importo1 = impapp.doubleValue()
            - impsic.doubleValue()
            - impnrl.doubleValue();
        if (!"1".equals(onsogrib)) importo1 -= onprge.doubleValue();

        pageContext.setAttribute("importo1", new Double(importo1),
            PageContext.REQUEST_SCOPE);

        importo2 = impnrl.doubleValue();
        if ("1".equals(sicinc)) importo2 += impsic.doubleValue();

        if (!"1".equals(onsogrib)) importo2 += onprge.doubleValue();

        pageContext.setAttribute("importo2", new Double(importo2),
            PageContext.REQUEST_SCOPE);
      }
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura dei dati della gara "
          + "nella fase 'Apertura offerte economiche", e);
    }
  }

  /**
   * Viene controllato se è attiva la codifica automatica su TEIM ed IMPR.
   *
   * @return true, se la codifica automatica è attiva false, altrimenti
   */
  public boolean getCodificaAutomaticaPerPortaleAttiva() {
    boolean ret = true;
    if (!(geneManager.isCodificaAutomatica("TEIM", "CODTIM") && geneManager.isCodificaAutomatica(
        "IMPR", "CODIMP"))) ret = false;
    return ret;
  }

  public String getDescCampoClassifica(String classifica, String tiplavg) {
    String tabellato = "A1015";
    if ("2".equals(tiplavg))
      tabellato = "G_035";
    else if ("3".equals(tiplavg))
      tabellato = "G_036";
    else if ("4".equals(tiplavg))
      tabellato = "G_037";
    else if ("5".equals(tiplavg)) tabellato = "G_049";

    String descClassifica = this.tabellatiManager.getDescrTabellato(tabellato,
        classifica);
    return descClassifica;
  }

  /**
   * Copia delle occorrenze dell'entità IMPRDOCG
   *
   * @param status
   * @param nGaraSorgente
   *        ,
   * @param codiceGaraSorgente
   * @param nGaraDestinazione
   * @param codiceGaraDestinazione
   *
   * @throws SQLException
   */
  public void copiaIMPRDOCG(TransactionStatus status, String nGaraSorgente,
      String codiceGaraSorgente, String nGaraDestinazione,
      String codiceGaraDestinazione) throws GestoreException {

    List<?> listaOccorenzeDaCopiare = null;
    DataColumnContainer campiDaCopiare = null;
    long numOccorrenze = 0;
    boolean condizioneBustalottiPerCopiaLottoInGara = false;

    try {

      Long bustalotti = (Long)this.sqlManager.getObject("select bustalotti from gare where ngara=?", new Object[]{codiceGaraSorgente});
      String sql = null;

      if (bustalotti != null && bustalotti.longValue() == 1 && codiceGaraSorgente.equals(codiceGaraDestinazione))
        condizioneBustalottiPerCopiaLottoInGara = true;

      if (condizioneBustalottiPerCopiaLottoInGara) {
        //Si devono scartare le occorrenze associate a DOCUMGARA con gruppo=3 associate al lotto di partenza
        String fromWhere ="from IMPRDOCG I, DOCUMGARA D where I.CODGAR = ? and  I.CODGAR = D.CODGAR "
            + "and I.NORDDOCI = D.NORDDOCG and ((I.NGARA = ? and D.GRUPPO <> 3) or (I.NGARA = ? and D.NGARA is null and D.GRUPPO = 3))";

        sql = "select I.CODGAR, I.NGARA,CODIMP, I.NORDDOCI, I.PROVENI, I.DESCRIZIONE, I.DATARILASCIO, I.ORARILASCIO, I.DURATA, I.DATASCADENZA, I.IDPRG,"
            + "I.IDDOCDG, I.SITUAZDOCI, I.NOTEDOCI, I.DOCTEL, I.BUSTA " + fromWhere;

        numOccorrenze = (Long)this.sqlManager.getObject("select count(I.CODGAR) " + fromWhere, new Object[]{codiceGaraSorgente,nGaraSorgente,nGaraSorgente});

      } else {
        sql = "select * from IMPRDOCG where CODGAR = ? and NGARA = ?";
        numOccorrenze = this.geneManager.countOccorrenze("IMPRDOCG",
            "CODGAR = ? and NGARA = ?", new Object[] { codiceGaraSorgente,
                nGaraSorgente });
      }

      if (this.geneManager.getSql().isTable("IMPRDOCG") && numOccorrenze > 0) {
        if (condizioneBustalottiPerCopiaLottoInGara)
          listaOccorenzeDaCopiare = this.geneManager.getSql().getListHashMap(sql,
            new Object[] { codiceGaraSorgente, nGaraSorgente, nGaraSorgente });
        else
          listaOccorenzeDaCopiare = this.geneManager.getSql().getListHashMap(sql,
              new Object[] { codiceGaraSorgente, nGaraSorgente });

        // Se ci sono occorenze allora eseguo la copia
        if (listaOccorenzeDaCopiare != null
            && listaOccorenzeDaCopiare.size() > 0) {
          for (int row = 0; row < listaOccorenzeDaCopiare.size(); row++) {
            if (condizioneBustalottiPerCopiaLottoInGara)
              campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
                "IMPRDOCG", sql, new Object[] { codiceGaraSorgente,
                    nGaraSorgente,nGaraSorgente });
            else
              campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
                  "IMPRDOCG", sql, new Object[] { codiceGaraSorgente,
                      nGaraSorgente });

            campiDaCopiare.setValoriFromMap(
                (HashMap<?,?>) listaOccorenzeDaCopiare.get(row), true);

            campiDaCopiare.getColumn("CODGAR").setChiave(true);
            campiDaCopiare.setValue("CODGAR", codiceGaraDestinazione);

            campiDaCopiare.getColumn("NGARA").setChiave(true);
            campiDaCopiare.setValue("NGARA", nGaraDestinazione);

            campiDaCopiare.getColumn("CODIMP").setChiave(true);
            campiDaCopiare.getColumn("NORDDOCI").setChiave(true);
            campiDaCopiare.getColumn("PROVENI").setChiave(true);

            campiDaCopiare.setValue("IDPRG", null);
            campiDaCopiare.setValue("IDDOCDG", null);

            // Inserimento del nuovo record
            campiDaCopiare.insert("IMPRDOCG", this.geneManager.getSql());

            String codimp = campiDaCopiare.getString("CODIMP");
            Long norddoci = campiDaCopiare.getLong("NORDDOCI");
            Long proveni = campiDaCopiare.getLong("PROVENI");
            // Copia delle occorrenze di W_DOCDIG figlie di DOCUMGARA

            HashMap<?,?> hm = (HashMap<?,?>) listaOccorenzeDaCopiare.get(row);
            String idprg = hm.get("IDPRG").toString();
            Long iddocdg = ((JdbcParametro) hm.get("IDDOCDG")).longValue();

            long numOccorrenzeW_DOCDIG = this.geneManager.countOccorrenze(
                "W_DOCDIG", "IDPRG = ? and IDDOCDIG = ?", new Object[] { idprg,
                    iddocdg });
            if (numOccorrenzeW_DOCDIG > 0) {
              // Il campo W_DOCDIG.DIGOGG è di tipo BLOB e va trattato
              // separatamente
              String select = "select IDPRG,IDDOCDIG,DIGENT,DIGKEY1,DIGKEY2,DIGTIPDOC,DIGNOMDOC,DIGDESDOC from W_DOCDIG where IDPRG = ? and IDDOCDIG = ?";
              List<?> occorrenzeW_DOCDIGDaCopiare = this.geneManager.getSql().getListHashMap(
                  select, new Object[] { idprg, iddocdg });
              if (occorrenzeW_DOCDIGDaCopiare != null
                  && occorrenzeW_DOCDIGDaCopiare.size() > 0) {
                for (int i = 0; i < occorrenzeW_DOCDIGDaCopiare.size(); i++) {
                  DataColumnContainer campiDaCopiareW_DOCDIG = new DataColumnContainer(
                      this.geneManager.getSql(), "W_DOCDIG", select,
                      new Object[] { idprg, iddocdg });
                  campiDaCopiareW_DOCDIG.setValoriFromMap(
                      (HashMap<?,?>) occorrenzeW_DOCDIGDaCopiare.get(i), true);
                  campiDaCopiareW_DOCDIG.getColumn("IDPRG").setChiave(true);

                  campiDaCopiareW_DOCDIG.getColumn("IDDOCDIG").setChiave(true);

                  // Si deve calcolare il valore di IDDOCDIG
                  Long maxIDDOCDIG = (Long) this.geneManager.getSql().getObject(
                      "select max(IDDOCDIG) from W_DOCDIG where IDPRG= ?",
                      new Object[] { idprg });

                  long newIDDOCDIG = 1;
                  if (maxIDDOCDIG != null && maxIDDOCDIG.longValue() > 0)
                    newIDDOCDIG = maxIDDOCDIG.longValue() + 1;

                  campiDaCopiareW_DOCDIG.setValue("IDDOCDIG", new Long(
                      newIDDOCDIG));
                  campiDaCopiareW_DOCDIG.setValue("DIGKEY1",
                      codiceGaraDestinazione);
                  campiDaCopiareW_DOCDIG.setValue("DIGKEY2", norddoci);
                  BlobFile fileAllegato = null;
                  fileAllegato = fileAllegatoManager.getFileAllegato(idprg,
                      iddocdg);
                  ByteArrayOutputStream baos = null;
                  if (fileAllegato != null && fileAllegato.getStream() != null) {
                    baos = new ByteArrayOutputStream();
                    baos.write(fileAllegato.getStream());
                  }
                  campiDaCopiareW_DOCDIG.addColumn("W_DOCDIG.DIGOGG",
                      JdbcParametro.TIPO_BINARIO, baos);

                  // Inserimento del nuovo record su w_docdig
                  campiDaCopiareW_DOCDIG.insert("W_DOCDIG",
                      this.geneManager.getSql());

                  // Aggiornamento dei campi IDPRG e IDDOCDG di documgara
                  this.sqlManager.update(
                      "update IMPRDOCG set IDPRG=?,IDDOCDG = ? where CODGAR=? and NGARA = ? and CODIMP = ? and NORDDOCI=? and PROVENI=?",
                      new Object[] { idprg, new Long(newIDDOCDIG),
                          codiceGaraDestinazione, nGaraDestinazione, codimp,
                          norddoci,proveni });
                }
              }
            }

          }
        }

      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella copia del lotto di gara  per entita' IMPRDOCG",
          "copiaGARE", e);
    } catch (IOException e) {
      throw new GestoreException(
          "Errore nella lettura del campo BLOB DIGOGG della W_DOCDIG",
          "copiaGARE", e);
    }

  }

  public void inizializzaDitteLottiOffertaUnica(String codgar, String ngara,
      boolean aggiornaImporto) throws GestoreException {

    String sql = "select NOMIMO, NPROGG, DITTAO, IMPAPPD, NUMORDPL, AMMGAR, FASGAR from DITG where CODGAR5 = ? and NGARA5 = ?";
    String sqlInsert = "insert into DITG (CODGAR5,NOMIMO,NPROGG,DITTAO,NGARA5,IMPAPPD,NUMORDPL, AMMGAR, FASGAR, INVGAR, INVOFF, NCOMOPE) values(?,?,?,?,?,?,?,?,?,?,?,?)";

    try {
      // Recupera l'importo a base d'asta del lotto
      Double impapp = (Double) this.sqlManager.getObject(
          "select impapp from gare where ngara=?", new Object[] { ngara });
      List<?> listaDatiDitg = this.sqlManager.getListVector(sql, new Object[] { codgar,
          codgar });
      if (listaDatiDitg != null && listaDatiDitg.size() > 0) {
        String ammgar = null;
        Long fasgar =  null;
        String invoff = null;
        Long iterga = (Long) this.sqlManager.getObject("select iterga from torn where codgar=?", new Object[]{codgar});
        if (iterga!= null && iterga.longValue() == 1)
          invoff = "1";

        for (int i = 0; i < listaDatiDitg.size(); i++) {
          Object[] parametri = new Object[12];
          parametri[0] = codgar;
          parametri[1] = SqlManager.getValueFromVectorParam(
              listaDatiDitg.get(i), 0).stringValue();
          parametri[2] = SqlManager.getValueFromVectorParam(
              listaDatiDitg.get(i), 1).longValue();
          parametri[3] = SqlManager.getValueFromVectorParam(
              listaDatiDitg.get(i), 2).stringValue();
          parametri[4] = ngara;
          if (aggiornaImporto)
            parametri[5] = impapp;
          else
            parametri[5] = SqlManager.getValueFromVectorParam(
                listaDatiDitg.get(i), 3).doubleValue();
          parametri[6] = SqlManager.getValueFromVectorParam(
              listaDatiDitg.get(i), 4).longValue();

          //se la ditta è stata esclusa a livello di gara si devono copiare anche ammgar e fasgar
          ammgar = SqlManager.getValueFromVectorParam(
              listaDatiDitg.get(i), 5).stringValue();

          if ("2".equals(ammgar)) {
            fasgar = SqlManager.getValueFromVectorParam(
                listaDatiDitg.get(i), 6).longValue();
          } else {
            ammgar = null;
            fasgar = null;
          }

          parametri[7] = ammgar;
          parametri[8] = fasgar;
          parametri[9] = "1";
          parametri[10] = invoff;
          parametri[11] = "1";

          this.sqlManager.update(sqlInsert, parametri);
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'allineamento delle ditte del lotto con quelle della gara '"
              + codgar
              + "'", "allineaDitteGara", null, e);

    }

  }

  /**
   * Gestione degli aggiornamenti per l'entità ISCRIZCLASSI
   *
   * @param operazione
   *        valori: "DEL", "INS" e "UPD"
   * @param codgar
   * @param ngara
   * @param codimp
   * @param codcat
   * @param tipcat
   * @param infnumclass
   * @param supnumclass
   * @param calcoloInviti
   *
   * @throws SQLException
   */
  public void updateIscrizclassi(String operazione, String codgar,
      String ngara, String codimp, String codcat, Long tipcat,
      Long infnumclass, Long supnumclass, boolean calcoloInviti)
      throws GestoreException {
    if ("DEL".equals(operazione)) {
      try {
        this.sqlManager.update(
            "delete from iscrizclassi where codgar=? and ngara=? and codimp=? and codcat=? and tipcat=?",
            new Object[] { codgar, ngara, codimp, codcat, tipcat });
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella cancellazione dei dati dell'entità ISCRIZCLASSI",
            null, e);
      }
    } else if ("INS".equals(operazione)) {
      String tabellato = "A1015";
      if (tipcat.longValue() == 2)
        tabellato = "G_035";
      else if (tipcat.longValue() == 3)
        tabellato = "G_036";
      else if (tipcat.longValue() == 4)
        tabellato = "G_037";
      else if (tipcat.longValue() == 5) tabellato = "G_049";

      String insertSql = "insert into iscrizclassi(codgar,ngara,codimp,codcat,tipcat,numclass,invrea,invpen) values(?,?,?,?,?,?,?,?)";

      List<Tabellato> listaValoriTabellato = tabellatiManager.getTabellato(tabellato);
      if (listaValoriTabellato != null && listaValoriTabellato.size() > 0) {
        Long limiteTabellatogetLimiteTabA1084 = null;
        limiteTabellatogetLimiteTabA1084 = this.getLimiteTabA1084();
        if (infnumclass == null) {
          //Come classe minima considera il primo valore del tabellato non archiviato
          String isArchiviato = "0";
          for (int i = 0; i < listaValoriTabellato.size(); i++) {
            isArchiviato = (listaValoriTabellato.get(i)).getArcTabellato();
            if (infnumclass == null && (isArchiviato == null || !"1".equals(isArchiviato))) {
              infnumclass = new Long((listaValoriTabellato.get(i)).getTipoTabellato());
            }
          }
        }

        if (supnumclass == null) {
          if (tipcat.longValue() != 1
              || (tipcat.longValue() == 1 && limiteTabellatogetLimiteTabA1084 == null))
            supnumclass = new Long(
                (listaValoriTabellato.get(listaValoriTabellato.size() - 1)).getTipoTabellato());
          else
            supnumclass = limiteTabellatogetLimiteTabA1084;
        }
        for (int i = 0; i < listaValoriTabellato.size(); i++) {
          String valoreClassifica = listaValoriTabellato.get(i).getTipoTabellato();
          Long valoreClassificaLong = new Long(valoreClassifica);
          if (infnumclass.longValue() <= valoreClassificaLong.longValue()
              && valoreClassificaLong.longValue() <= supnumclass.longValue()) {
            Long numPenalita = null;

            try {
              if (calcoloInviti)
                numPenalita = this.getNumeroPenalita(codgar, ngara, codimp,
                    codcat, tipcat, valoreClassificaLong, "ISCRIZCLASSI",null,true);
              this.sqlManager.update(insertSql, new Object[] { codgar, ngara,
                  codimp, codcat, tipcat, valoreClassificaLong, null,
                  numPenalita });
            } catch (SQLException e) {
              throw new GestoreException(
                  "Errore nell'inserimento dei dati dell'entità ISCRIZCLASSI",
                  null, e);
            }
          }
        }
      }
    } else {
      // Aggiornamento di ISCRIZCLASSI, cancellando le classi non più presenti
      // ed inserendo le nuove
      try {
        String tabellato = "A1015";
        if (tipcat.longValue() == 2)
          tabellato = "G_035";
        else if (tipcat.longValue() == 3)
          tabellato = "G_036";
        else if (tipcat.longValue() == 4)
          tabellato = "G_037";
        else if (tipcat.longValue() == 5) tabellato = "G_049";

        String select = "";
        String insertSql = "insert into iscrizclassi(codgar,ngara,codimp,codcat,tipcat,numclass,invrea,invpen) values(?,?,?,?,?,?,?,?)";

        List<Tabellato> listaValoriTabellato = tabellatiManager.getTabellato(tabellato);
        if (listaValoriTabellato != null && listaValoriTabellato.size() > 0) {
          Long limiteTabellatogetLimiteTabA1084 = null;
          limiteTabellatogetLimiteTabA1084 = this.getLimiteTabA1084();

          if (infnumclass == null) {
            //Come classe minima considera il primo valore del tabellato non archiviato
            String isArchiviato = "0";
            for (int i = 0; i < listaValoriTabellato.size(); i++) {
              isArchiviato = (listaValoriTabellato.get(i)).getArcTabellato();
              if (infnumclass == null && (isArchiviato == null || !"1".equals(isArchiviato))) {
                infnumclass = new Long((listaValoriTabellato.get(i)).getTipoTabellato());
              }
            }
          }

          if (supnumclass == null) {
            if (tipcat.longValue() != 1
                || (tipcat.longValue() == 1 && limiteTabellatogetLimiteTabA1084 == null))
              supnumclass = new Long(
                  listaValoriTabellato.get(listaValoriTabellato.size() - 1).getTipoTabellato());
            else
              supnumclass = limiteTabellatogetLimiteTabA1084;
          }
          // Eliminazione delle classi fuori dal range selezionato
          this.sqlManager.update(
              "delete from iscrizclassi where codgar=? and ngara=? and codimp=? and codcat=? and tipcat=? and (numclass < ? or numclass > ?)",
              new Object[] { codgar, ngara, codimp, codcat, tipcat,
                  infnumclass, supnumclass });

          // Inserimento delle evenutali nuove classi
          for (int i = 0; i < listaValoriTabellato.size(); i++) {
            String valoreClassifica = listaValoriTabellato.get(i).getTipoTabellato();
            Long valoreClassificaLong = new Long(valoreClassifica);
            if (infnumclass.longValue() <= valoreClassificaLong.longValue()
                && valoreClassificaLong.longValue() <= supnumclass.longValue()) {
              select = "select count(codgar) from iscrizclassi where codgar=? and ngara=? and codimp=? and codcat=? and tipcat=? and numclass = ?";
              Long countClassi = (Long) this.sqlManager.getObject(select,
                  new Object[] { codgar, ngara, codimp, codcat, tipcat,
                      valoreClassificaLong });
              if (countClassi == null || countClassi.equals(new Long(0))) {
                Long numPenalita = null;

                if (calcoloInviti)
                  numPenalita = this.getNumeroPenalita(codgar, ngara, codimp,
                      codcat, tipcat, valoreClassificaLong, "ISCRIZCLASSI",null, true);
                this.sqlManager.update(insertSql, new Object[] { codgar, ngara,
                    codimp, codcat, tipcat, valoreClassificaLong, null,
                    numPenalita });
              }

            }
          }
        }

      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella cancellazione dei dati dell'entità ISCRIZCLASSI",
            null, e);
      }
    }

  }

  /**
   * Aggiornamento delle penalità per ISCRIZCAT e ISCRIZCLASSI
   *
   * @param codgar
   * @param ngara
   * @param ditta
   * @param status
   *
   * @throws GestoreException
   */
  public void updatePenalita(String codagr, String ngara, String ditta,
      TransactionStatus status) throws GestoreException {

    if (this.controlliPreliminariCalcoloNumPenalita("$" + ngara, ngara, status)) {
      String select = "select codcat,tipcat from iscrizcat where codgar = ? and ngara = ? and codimp = ?";

      try {
        List<?> listaIscrizcat = this.sqlManager.getListVector(select,
            new Object[] { codagr, ngara, ditta });
        if (listaIscrizcat != null && listaIscrizcat.size() > 0) {
          for (int j = 0; j < listaIscrizcat.size(); j++) {
            Vector<?> lotto = (Vector<?>) listaIscrizcat.get(j);
            String codiceCategoria = (String) ((JdbcParametro) lotto.get(0)).getValue();
            Long tipoCategoria = (Long) ((JdbcParametro) lotto.get(1)).getValue();
            Long numPenalita = this.getNumeroPenalita(codagr, ngara, ditta,
                codiceCategoria, tipoCategoria, null, "ISCRIZCAT",null, true);
            this.sqlManager.update(
                "update ISCRIZCAT set INVPEN=? where codgar=? and ngara=? and codcat=? and codimp=? and tipcat=?",
                new Object[] { numPenalita, codagr, ngara, codiceCategoria,
                    ditta, tipoCategoria });

            //Aggiornamento ISCRIZUFF in base al calcolo di numero inviti virtuali
            this.aggInvitiVirtualiIscrizuff(codagr, ngara, codiceCategoria, ditta, tipoCategoria);

            if (!"0".equals(codiceCategoria)) {
              // Calcolo del numero delle penalità per ISCRIZCLASSI
              select = "select tipcat,numclass from iscrizclassi where codgar = ? and ngara = ? and codimp = ? and codcat = ?";
              List<?> listaIscriclassi = this.sqlManager.getListVector(select,
                  new Object[] { codagr, ngara, ditta, codiceCategoria });
              if (listaIscriclassi != null && listaIscriclassi.size() > 0) {
                for (int z = 0; z < listaIscriclassi.size(); z++) {
                  Vector<?> datiIscrizclassi = (Vector<?>) listaIscriclassi.get(z);
                  tipoCategoria = (Long) ((JdbcParametro) datiIscrizclassi.get(0)).getValue();
                  Long numclass = (Long) ((JdbcParametro) datiIscrizclassi.get(1)).getValue();

                  numPenalita = this.getNumeroPenalita(codagr, ngara, ditta,
                      codiceCategoria, tipoCategoria, numclass, "ISCRIZCLASSI",null,true);
                  this.sqlManager.update(
                      "update ISCRIZCLASSI set INVPEN=? where codgar=? and ngara=? and codcat=? and codimp=? and tipcat=? and numclass = ?",
                      new Object[] { numPenalita, codagr, ngara,
                          codiceCategoria, ditta, tipoCategoria, numclass });
                }
              }
            }

          }

        }

      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nel calcolo del numero di penalità della ditta " + ditta,
            null, e);
      }
    }
  }

  /**
   * Viene calcolato il valore del campo passato come argomento (INVREA, AGGREA) di Iscrizclassi e la ralativa classe di
   * appartenenza
   *
   * @param codiceGara
   * @param numeroGara
   * @param codiceDitta
   * @param codiceCategoria
   * @param classifica
   * @param nomeCampo
   *
   * @throws GestoreException
   */
  private Long[] getCampoIscrizclassi(String codiceGara, String numeroGara,
      String codiceDitta, String codiceCategoria, Long classifica, String nomeCampo)
      throws GestoreException {

    Long campo = null;
    Long ret[] = new Long[2];

    String select = "select count(codgar) from iscrizclassi where codgar=? and ngara = ? and codimp = ? and codcat =? and numclass=?";
    try {
      Long conteggio = (Long) this.sqlManager.getObject(select, new Object[] {
          codiceGara, numeroGara, codiceDitta, codiceCategoria, classifica });
      if (conteggio != null && conteggio.longValue() > 0) {
        select = "select " + nomeCampo +" from iscrizclassi where codgar=? and ngara = ? and codimp = ? and codcat =? and numclass=?";
        campo = (Long) this.sqlManager.getObject(select, new Object[] {
            codiceGara, numeroGara, codiceDitta, codiceCategoria, classifica });
        ret[0] = campo;
        ret[1] = classifica;
      } else {
        select = "select numclass from iscrizclassi where codgar=? and ngara = ? and codimp = ? and codcat =? and numclass is not null";
        List<?> listaDatiNumclass = this.sqlManager.getListVector(select,
            new Object[] { codiceGara, numeroGara, codiceDitta, codiceCategoria });
        if (listaDatiNumclass != null && listaDatiNumclass.size() > 0) {
          Long classificaIscrizclassi = null;
          Long classificaTmp = null;
          long scarto = 10000;
          long scartoTmp = 10000;
          for (int i = 0; i < listaDatiNumclass.size(); i++) {
            Vector<?> datiNumclass = (Vector<?>) listaDatiNumclass.get(i);
            classificaTmp = (Long) ((JdbcParametro) datiNumclass.get(0)).getValue();
            scartoTmp = Math.abs(classifica.longValue()
                - classificaTmp.longValue());
            if (scartoTmp < scarto) {
              scarto = scartoTmp;
              classificaIscrizclassi = classificaTmp;
            }
          }
          if (classificaIscrizclassi != null
              && classificaIscrizclassi.longValue() != 0) {
            select = "select "+ nomeCampo + " from iscrizclassi where codgar=? and ngara = ? and codimp = ? and codcat =? and numclass=?";
            campo = (Long) this.sqlManager.getObject(select, new Object[] {
                codiceGara, numeroGara, codiceDitta, codiceCategoria,
                classificaIscrizclassi });
            ret[0] = campo;
            ret[1] = classificaIscrizclassi;
          }
        }
      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura del campo " + nomeCampo + " dell'entità ISCRIZCLASSI",
          null, e);
    }
    return ret;
  }

  /**
   * Viene controllato se la stringa contiene un valore intero
   *
   * @param input
   *
   * @return true se la stringa contiene un intero false negli altri casi
   */
  public boolean isInteger(String input) {
    try {
      Integer.parseInt(input);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Viene restituito il valore impostato nel tabellato A1084
   *
   * @return valore del limite impostato nel tabellato 1084
   */
  public Long getLimiteTabA1084() {
    Long ret = null;
    String limite = tabellatiManager.getDescrTabellato("A1084", "1");
    // Per separare il numero dalla descrizione controllo se vi è lo spazio, se
    // non lo
    // trovo si continua senza applicare la limitazione
    if (limite != null) {
      int pos = limite.indexOf(" ");
      if (pos > 0) limite = limite.substring(0, pos);

      if (!"".equals(limite) && !"0".equals(limite) && this.isInteger(limite))
        ret = Long.valueOf(limite);
    }
    return ret;
  }

  /**
   * Vengono controllati i valori della lista, quando un valore non è numerico,
   * allora viene sbiancato tale valore
   *
   */
  public void aggiornaListaImporti(List<?> lista) {
    if (lista != null && lista.size() > 0) {
      for (int i = 0; i < lista.size(); i++) {
        String importo = ((Tabellato) lista.get(i)).getDatoSupplementare();
        if (importo != null && !NumberUtils.isNumber(importo))
          lista.set(i, null);
      }
    }

  }

  /**
   * Viene inserita un'occorrenza in G_NOTEAVVISI
   *
   * @param document
   *        messaggio da inserire nella nota, può essere un'istanza di
   *        RichiestaVariazioneDocument(fornito dal Portale) oppure una stringa
   * @param codiceImpresa
   * @param modo
   * @param profilo
   * @param dataNota
   * @param impostaStatoAperta
   * @param titolo
   *
   * @throws GestoreException
   */
  public void InserisciVariazioni(Object document, String codiceImpresa,
      String modo, ProfiloUtente profilo, Date dataNota, boolean impostaStatoAperta, String titolo)
      throws GestoreException {

    RichiestaGenericaType richiestaVarDatiId = null;
    String testoRichiesta = null;
    Long statoNota = null;
    String titoloNota = null;

    if (document instanceof RichiestaVariazioneDocument) {
      richiestaVarDatiId = ((RichiestaVariazioneDocument) document).getRichiestaVariazione();
      testoRichiesta = richiestaVarDatiId.getDescrizione();
      statoNota = new Long(1);
      titoloNota = "Richiesta variazione dati identificativi da portale";
    } else if (document instanceof String) {
      testoRichiesta = (String) document;
      statoNota = new Long(90);
      if(titolo!=null)
        titoloNota=titolo;
      else
        titoloNota = "Notifica variazione dati da portale";

      // Si eliminano i caratteri &nbsp;
      testoRichiesta = testoRichiesta.replaceAll("&nbsp;", " ");
    }

    if (impostaStatoAperta)
      statoNota = new Long(1);

    try {

      Long notecod = null;
      // Operatore (utente di USRSYS che ha avuto accesso all'applicativo)
      Long syscon = null;
      if(profilo!=null){
        syscon = new Long(profilo.getId());
      }else{
        syscon = new Long(48);
      }
      Date datains = new Date(UtilityDate.getDataOdiernaAsDate().getTime());

      // Aggiornamento dati Impresa
      Vector<DataColumn> elencoCampiNOTEAVVISI = new Vector<DataColumn>();

      if (notecod == null) {
        notecod = (Long) this.sqlManager.getObject(
            "select max(notecod) from g_noteavvisi", null);
        if (notecod == null) {
          notecod = new Long(0);
        }
        notecod = new Long(notecod.longValue() + 1);
      }
      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.NOTECOD",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, notecod)));
      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.NOTEPRG",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")));
      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.NOTEENT",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, "IMPR")));
      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.NOTEKEY1",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceImpresa)));
      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.AUTORENOTA",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, syscon)));
      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.STATONOTA",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, statoNota)));
      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.TIPONOTA",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(3))));
      if (document instanceof RichiestaVariazioneDocument) {
        elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.DATANOTA",
            new JdbcParametro(JdbcParametro.TIPO_DATA, datains)));
      } else {
        elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.DATANOTA",
            new JdbcParametro(JdbcParametro.TIPO_DATA, dataNota)));
        elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.DATACHIU",
            new JdbcParametro(JdbcParametro.TIPO_DATA, datains)));
      }
      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.TITOLONOTA",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, titoloNota)));
      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.TESTONOTA",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, testoRichiesta)));

      DataColumnContainer containerNOTEAVVISI = new DataColumnContainer(
          elencoCampiNOTEAVVISI);

      containerNOTEAVVISI.getColumn("G_NOTEAVVISI.NOTECOD").setChiave(true);
      containerNOTEAVVISI.getColumn("G_NOTEAVVISI.NOTECOD").setObjectOriginalValue(
          notecod);

      if ("UPDATE".equals(modo)) {
        containerNOTEAVVISI.update("...Non previsto per il momento.....",
            sqlManager);
      } else {
        containerNOTEAVVISI.getColumn("G_NOTEAVVISI.NOTECOD").setObjectOriginalValue(
            "0");
        containerNOTEAVVISI.getColumn("G_NOTEAVVISI.NOTECOD").setChiave(true);
        containerNOTEAVVISI.insert("G_NOTEAVVISI", sqlManager);
      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'acquisizione della variazione dati identificativi dell'impresa",
          null, e);
    }

  }

  /**
   * Viene effettuato un confronto fra i dati in db e quelli provenienti dal
   * Portale per IMPR, se vi sono differenze viene costruito un messaggio
   * informativo
   *
   * @param sqlManager
   * @param tabellatiManager
   * @param document
   * @param codiceImpresa
   * @return String
   *
   * @throws GestoreException
   */
  /**
   * @param document
   * @param codiceImpresa
   * @return
   * @throws GestoreException
   */
  public String controlloDatiImpresa(Object document, String codiceImpresa)
      throws GestoreException {
    String msg = "";


    ImpresaType impresa = null;
    if (document instanceof RegistrazioneImpresaDocument)
      impresa = ((RegistrazioneImpresaDocument) document).getRegistrazioneImpresa().getDatiImpresa().getImpresa();
    else if (document instanceof IscrizioneImpresaElencoOperatoriDocument)
      impresa = ((IscrizioneImpresaElencoOperatoriDocument) document).getIscrizioneImpresaElencoOperatori().getDatiImpresa().getImpresa();
    else if (document instanceof AggiornamentoIscrizioneImpresaElencoOperatoriDocument)
      impresa = ((AggiornamentoIscrizioneImpresaElencoOperatoriDocument) document).getAggiornamentoIscrizioneImpresaElencoOperatori().getDatiImpresa().getImpresa();
    else
      impresa = ((AggiornamentoAnagraficaImpresaDocument) document).getAggiornamentoAnagraficaImpresa().getDatiImpresa().getImpresa();

    String select = "select NOMEST,NATGIUI,TIPIMP,CFIMP,PIVIMP,INDIMP,NCIIMP,CAPIMP,LOCIMP,PROIMP,NAZIMP,"
        + "EMAI2IP,EMAIIP,TELIMP,FAXIMP,TELCEL,REGDIT,DISCIF,NCCIAA,DCCIAA,PCCIAA,NINPS,DINPS,LINPS,NINAIL,DINAIL,LINAIL,"
        + "NCEDIL,DCEDIL,LCEDIL,NISANC,DISANC,DSCANC,DURANC,NUMISO,"
        + "DATISO,DANTIM,DSCNOS,DRINOS,OCTSOA,OCTISO,COORBA,SOGMOV,RINNOS,ZONEAT,SEXTEC,PRONAS,CNATEC,DNATEC,INCTEC,TIPALB,ALBTEC,"
        + "DATALB,PROALB,TCAPRE,NCAPRE,OGGSOC,SOGGDURC,SETTPROD,POSINPS,POSINAIL,CODCEDIL,ACERTATT,ULTDIC,ASSOBBL,AISTPREV,"
        + "DTRISOA,DVERSOA,ISMPMI,INDWEB,ISCRCCIAA,"
        + "ISCRIWL,WLPREFE,WLSEZIO,WLDISCRI,WLDSCAD,WLINCORSO,CLADIM,CODATT,CODBIC,COGNOME,NOME,"
        + "ISCRIAE,AEDSCAD,AEINCORSO,ISCRIESP,ISCRIRAT,RATING,RATDSCAD,RATINCORSO,SOCIOUNICO,REGFISC,DINTSOA,TIPOCOOP from impr where CODIMP=?";

    // Sezione dati generali
    DataColumnContainer datiImpr = new DataColumnContainer(sqlManager, "IMPR",
        select, new Object[] { codiceImpresa });
    datiImpr.setValue("IMPR.NOMEST", impresa.getRagioneSociale());
    if (datiImpr.isModifiedColumn("IMPR.NOMEST"))
      msg += "La ragione sociale é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + datiImpr.getColumn("IMPR.NOMEST").getOriginalValue()
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + impresa.getRagioneSociale()
          + "\n";

    String naturaGiuridica = impresa.getNaturaGiuridica();
    Long natgiui = null;
    if (naturaGiuridica != null && !"".equals(naturaGiuridica))
      natgiui = new Long(naturaGiuridica);
    else
      naturaGiuridica = "";
    datiImpr.setValue("IMPR.NATGIUI", natgiui);
    if (datiImpr.isModifiedColumn("IMPR.NATGIUI")) {
      String descOriginale = "";
      Long tmp = datiImpr.getColumn("IMPR.NATGIUI").getOriginalValue().longValue();
      if (tmp != null)
        descOriginale = tabellatiManager.getDescrTabellato("G_043",
            tmp.toString());

      if (!"".equals(naturaGiuridica))
        naturaGiuridica = tabellatiManager.getDescrTabellato("G_043",
            naturaGiuridica);

      if (!"".equals(msg)) msg += "\n";

      msg += "La forma giuridica é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + descOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + naturaGiuridica
          + "\n";

    }

    String tipologiaSocCooperativa = null;  //ADEGUARE GESTIONE TIPOCOOP, sosistuire isSetAltriDatiAnagrafici e getNaturaGiuridica con i metodi giusti
    if(impresa.isSetTipoSocietaCooperativa())
      tipologiaSocCooperativa=impresa.getTipoSocietaCooperativa();
    Long tipSocCooperativa = null;
    if (tipologiaSocCooperativa != null && !"".equals(tipologiaSocCooperativa))
      tipSocCooperativa = new Long(tipologiaSocCooperativa);
    else
      tipologiaSocCooperativa = "";
    datiImpr.setValue("IMPR.TIPOCOOP", tipSocCooperativa);
    if (datiImpr.isModifiedColumn("IMPR.TIPOCOOP")) {
      String descOriginale = "";
      Long tmp = datiImpr.getColumn("IMPR.TIPOCOOP").getOriginalValue().longValue();
      if (tmp != null)
        descOriginale = tabellatiManager.getDescrTabellato("G_074",
            tmp.toString());

      if (!"".equals(tipologiaSocCooperativa))
        tipologiaSocCooperativa = tabellatiManager.getDescrTabellato("G_074",
            tipologiaSocCooperativa);

      if (!"".equals(msg)) msg += "\n";

      msg += "La tipologia società cooperativa é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + descOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + tipologiaSocCooperativa
          + "\n";

    }

    String tipoImpresaString = impresa.getTipoImpresa();
    Long tipoImpresa = null;
    if (tipoImpresaString != null && !"".equals(tipoImpresaString))
      tipoImpresa = new Long(tipoImpresaString);
    else
      tipoImpresaString = "";
    datiImpr.setValue("IMPR.TIPIMP", tipoImpresa);
    if (datiImpr.isModifiedColumn("IMPR.TIPIMP")) {
      String descOriginale = "";
      Long tmp = datiImpr.getColumn("IMPR.TIPIMP").getOriginalValue().longValue();
      if (tmp != null)
        descOriginale = tabellatiManager.getDescrTabellato("Ag008",
            tmp.toString());
      if (!"".equals(tipoImpresaString))
        tipoImpresaString = tabellatiManager.getDescrTabellato("Ag008",
            tipoImpresaString);

      if (!"".equals(msg)) msg += "\n";

      msg += "La tipologia é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + descOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + tipoImpresaString
          + "\n";
    }

    datiImpr.setValue("IMPR.CFIMP", impresa.getCodiceFiscale());
    if (datiImpr.isModifiedColumn("IMPR.CFIMP")) {
      String cfOriginale = datiImpr.getColumn("IMPR.CFIMP").getOriginalValue().getStringValue();
      if (!"".equals(msg)) msg += "\n";

      msg += "Il codice fiscale é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + cfOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + impresa.getCodiceFiscale()
          + "\n";
    }

    datiImpr.setValue("IMPR.PIVIMP", impresa.getPartitaIVA());
    if (datiImpr.isModifiedColumn("IMPR.PIVIMP")) {
      String pivaOriginale = datiImpr.getColumn("IMPR.PIVIMP").getOriginalValue().getStringValue();
      if (!"".equals(msg)) msg += "\n";

      msg += "La partita I.V.A. é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + pivaOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + impresa.getPartitaIVA()
          + "\n";
    }

    datiImpr.setValue("IMPR.OGGSOC", impresa.getOggettoSociale());
    if (datiImpr.isModifiedColumn("IMPR.OGGSOC")) {
      String oggettoSocialeOriginale = datiImpr.getColumn("IMPR.OGGSOC").getOriginalValue().getStringValue();
      if (oggettoSocialeOriginale == null)
        oggettoSocialeOriginale="";
      String oggettoSociale = "";
      if (impresa.getOggettoSociale() != null) oggettoSociale = impresa.getOggettoSociale();

      msg += "L'oggetto sociale é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + oggettoSocialeOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + oggettoSociale
          + "\n";
    }

    String microImpresa = "";
    if (impresa.isSetMicroPiccolaMediaImpresa()) {
      microImpresa = impresa.getMicroPiccolaMediaImpresa();
      if ("0".equals(microImpresa))microImpresa = "2";
    }
    datiImpr.setValue("IMPR.ISMPMI", microImpresa);
    /*
    if (datiImpr.isModifiedColumn("IMPR.ISMPMI")) {
      String microImpresaOriginale = "";
      if (datiImpr.getColumn("IMPR.ISMPMI").getOriginalValue() != null)
        microImpresaOriginale = datiImpr.getColumn("IMPR.ISMPMI").getOriginalValue().getStringValue();

      if (!"".equals(msg)) msg += "\n";

      if ("1".equals(microImpresaOriginale))
        microImpresaOriginale = "Si";
      else if ("2".equals(microImpresaOriginale))
        microImpresaOriginale = "No";
      else
        microImpresaOriginale = "";


      if ("1".equals(microImpresa))
        microImpresa = "Si";
      else if ("2".equals(microImpresa))
        microImpresa = "No";
      else
        microImpresa = "";

      msg += "Il flag Micro, piccola o media impresa? e' cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + microImpresaOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + microImpresa
          + "\n";
    }
    */

    java.util.Date campoData = null;

    // Libero Professionista
    if ("6".equals(tipoImpresaString)) {
      AltriDatiAnagraficiType datiLiberoProfessionista = impresa.getAltriDatiAnagrafici();

      datiImpr.setValue("IMPR.SEXTEC", datiLiberoProfessionista.getSesso());
      if (datiImpr.isModifiedColumn("IMPR.SEXTEC")) {
        String sessoOriginale = "";
        if (datiImpr.getColumn("IMPR.SEXTEC").getOriginalValue() != null)
          sessoOriginale = datiImpr.getColumn("IMPR.SEXTEC").getOriginalValue().getStringValue();
        String sesso = "";
        if (datiLiberoProfessionista.getSesso() != null)
          sesso = datiLiberoProfessionista.getSesso();
        if (!"".equals(msg)) msg += "\n";

        if ("M".equals(sessoOriginale))
          sessoOriginale = "Maschio";
        else if ("F".equals(sessoOriginale)) sessoOriginale = "Femmina";

        if ("M".equals(sesso))
          sesso = "Maschio";
        else if ("F".equals(sesso)) sesso = "Femmina";

        msg += "Il sesso e' cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + sessoOriginale
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + sesso
            + "\n";
      }

      datiImpr.setValue("IMPR.PRONAS",
          datiLiberoProfessionista.getProvinciaNascita());
      if (datiImpr.isModifiedColumn("IMPR.PRONAS")) {
        String provinciaOriginale = "";
        if (datiImpr.getColumn("IMPR.PRONAS").getOriginalValue() != null
            && !"".equals(datiImpr.getColumn("IMPR.PRONAS").getOriginalValue().getStringValue())) {
          provinciaOriginale = datiImpr.getColumn("IMPR.PRONAS").getOriginalValue().getStringValue();
          provinciaOriginale = this.getProvinciaDaSigla(provinciaOriginale.toUpperCase());
        }
        String provincia = "";
        if (datiLiberoProfessionista.getProvinciaNascita() != null
            && !"".equals(datiLiberoProfessionista.getProvinciaNascita())) {
          provincia = datiLiberoProfessionista.getProvinciaNascita();
          provincia = this.getProvinciaDaSigla(provincia.toUpperCase());
        }
        if (!"".equals(msg)) msg += "\n";

        msg += "La provincia di nascita e' cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + provinciaOriginale
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + provincia
            + "\n";
      }

      datiImpr.setValue("IMPR.CNATEC",
          datiLiberoProfessionista.getComuneNascita());
      if (datiImpr.isModifiedColumn("IMPR.CNATEC")) {
        String comuneOriginale = "";
        if (datiImpr.getColumn("IMPR.CNATEC").getOriginalValue() != null)
          comuneOriginale = datiImpr.getColumn("IMPR.CNATEC").getOriginalValue().getStringValue();
        String comune = "";
        if (datiLiberoProfessionista.getComuneNascita() != null)
          comune = datiLiberoProfessionista.getComuneNascita();
        if (!"".equals(msg)) msg += "\n";

        msg += "Il comune di nascita e' cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + comuneOriginale
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + comune
            + "\n";
      }

      Calendar dataNascita = datiLiberoProfessionista.getDataNascita();
      campoData = null;
      if (dataNascita != null) campoData = dataNascita.getTime();
      datiImpr.setValue("IMPR.DNATEC", campoData);
      if (datiImpr.isModifiedColumn("IMPR.DNATEC")) {
        String dnatecOriginale = "";
        if (datiImpr.getColumn("IMPR.DNATEC").getOriginalValue().dataValue() != null) {
          dnatecOriginale = UtilityDate.convertiData(
              new Date(
                  datiImpr.getColumn("IMPR.DNATEC").getOriginalValue().dataValue().getTime()),
              UtilityDate.FORMATO_GG_MM_AAAA);
        }

        String dnatec = "";
        if (dataNascita != null)
          dnatec = UtilityDate.convertiData(dataNascita.getTime(),
              UtilityDate.FORMATO_GG_MM_AAAA);
        if (!"".equals(msg)) msg += "\n";

        msg += "La data di nascita é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + dnatecOriginale
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + dnatec
            + "\n";
      }

      datiImpr.setValue("IMPR.INCTEC", datiLiberoProfessionista.getTitolo());
      if (datiImpr.isModifiedColumn("IMPR.INCTEC")) {
        Long titoloOriginale = datiImpr.getColumn("IMPR.INCTEC").getOriginalValue().longValue();
        String titoloOriginaleDesc;
        if (titoloOriginale != null) {
          titoloOriginaleDesc = tabellatiManager.getDescrTabellato("Ag004",
              titoloOriginale.toString());
        } else {
          titoloOriginaleDesc = "";
        }

        String titoloDesc = "";
        if (datiLiberoProfessionista.getTitolo() != null)
          titoloDesc = tabellatiManager.getDescrTabellato("Ag004",
              datiLiberoProfessionista.getTitolo());

        if (!"".equals(msg)) msg += "\n";

        msg += "Il titolo é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + titoloOriginaleDesc
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + titoloDesc
            + "\n";
      }

    }

    // Sezione Indirizzo
    IndirizzoType indirizzoDitta = impresa.getSedeLegale();

    datiImpr.setValue("IMPR.INDIMP", indirizzoDitta.getIndirizzo());
    if (datiImpr.isModifiedColumn("IMPR.INDIMP")) {
      String indOriginale = datiImpr.getColumn("IMPR.INDIMP").getOriginalValue().getStringValue();
      if (!"".equals(msg)) msg += "\n";

      msg += "L'indirizzo é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + indOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + indirizzoDitta.getIndirizzo()
          + "\n";
    }

    datiImpr.setValue("IMPR.NCIIMP", indirizzoDitta.getNumCivico());
    if (datiImpr.isModifiedColumn("IMPR.NCIIMP")) {
      String numcivicoOriginale = datiImpr.getColumn("IMPR.NCIIMP").getOriginalValue().getStringValue();
      if (!"".equals(msg)) msg += "\n";

      msg += "Il n.civico é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + numcivicoOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + indirizzoDitta.getNumCivico()
          + "\n";
    }

    datiImpr.setValue("IMPR.CAPIMP", indirizzoDitta.getCap());
    if (datiImpr.isModifiedColumn("IMPR.CAPIMP")) {
      String capOriginale = datiImpr.getColumn("IMPR.CAPIMP").getOriginalValue().getStringValue();
      if (!"".equals(msg)) msg += "\n";

      msg += "Il C.A.P. é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + capOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + indirizzoDitta.getCap()
          + "\n";
    }

    datiImpr.setValue("IMPR.LOCIMP", indirizzoDitta.getComune());
    if (datiImpr.isModifiedColumn("IMPR.LOCIMP")) {
      String comuneOriginale = datiImpr.getColumn("IMPR.LOCIMP").getOriginalValue().getStringValue();
      if (!"".equals(msg)) msg += "\n";

      msg += "Il comune é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + comuneOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + indirizzoDitta.getComune()
          + "\n";
    }

    datiImpr.setValue("IMPR.PROIMP", indirizzoDitta.getProvincia());
    if (datiImpr.isModifiedColumn("IMPR.PROIMP")) {
      String provincia = indirizzoDitta.getProvincia();
      String provinciaOriginale = "";
      if (provincia == null) provincia = "";
      if (datiImpr.getColumn("IMPR.PROIMP").getOriginalValue() != null)
        provinciaOriginale = datiImpr.getColumn("IMPR.PROIMP").getOriginalValue().getStringValue();

      if (!"".equals(msg)) msg += "\n";

      msg += "La provincia é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + provinciaOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + provincia
          + "\n";
    }

    String nazione = indirizzoDitta.getNazione();
    select = "select tab1tip from tab1 where tab1cod='Ag010' and upper(tab1desc) = ?";

    Long nazimp = null;
    try {
      nazimp = (Long) this.sqlManager.getObject(select,
          new Object[] { nazione.toUpperCase() });
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della nazione", null, e);
    }
    datiImpr.setValue("IMPR.NAZIMP", nazimp);
    if (datiImpr.isModifiedColumn("IMPR.NAZIMP")) {
      Long nazioneOriginale = datiImpr.getColumn("IMPR.NAZIMP").getOriginalValue().longValue();
      String nazioneOriginaleDesc;
      if (nazioneOriginale != null) {
        nazioneOriginaleDesc = tabellatiManager.getDescrTabellato("Ag010",
            nazioneOriginale.toString());
      } else {
        nazioneOriginaleDesc = "";
      }
      if (!"".equals(msg)) msg += "\n";

      msg += "La nazione é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + nazioneOriginaleDesc
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + nazione
          + "\n";
    }

    RecapitiType recapiti = impresa.getRecapiti();
    /*
     *
     * datiImpr.setValue("IMPR.MGSFLG", recapiti.getModalitaComunicazione());
     * if (datiImpr.isModifiedColumn("IMPR.MGSFLG")) { String tipoMsgOriginale =
     * datiImpr.getColumn("IMPR.MGSFLG").getOriginalValue().getStringValue();
     * String tipoMsgOriginaleDesc = tabellatiManager.getDescrTabellato("Agx01",
     * tipoMsgOriginale); String tipoMsgDesc =
     * tabellatiManager.getDescrTabellato("Agx01",
     * recapiti.getModalitaComunicazione()); if (!"".equals(msg)) msg +="\n";
     *
     * msg+="Il Tipo messaggio é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
     * + tipoMsgOriginaleDesc + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
     * tipoMsgDesc + "\n"; }
     */

    datiImpr.setValue("IMPR.EMAIIP", recapiti.getEmail());
    if (datiImpr.isModifiedColumn("IMPR.EMAIIP")) {
      String mailOriginale = "";
      if (datiImpr.getColumn("IMPR.EMAIIP").getOriginalValue() != null)
        mailOriginale = datiImpr.getColumn("IMPR.EMAIIP").getOriginalValue().getStringValue();
      String mail = "";
      if (recapiti.getEmail() != null) mail = recapiti.getEmail();

      if (!"".equals(msg)) msg += "\n";

      msg += "L'indirizzo E-mail é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + mailOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + mail
          + "\n";

    }

    datiImpr.setValue("IMPR.TELIMP", recapiti.getTelefono());
    if (datiImpr.isModifiedColumn("IMPR.TELIMP")) {
      String telOriginale = "";
      if (datiImpr.getColumn("IMPR.TELIMP").getOriginalValue() != null)
        telOriginale = datiImpr.getColumn("IMPR.TELIMP").getOriginalValue().getStringValue();
      String tel = "";
      if (recapiti.getTelefono() != null) tel = recapiti.getTelefono();

      if (!"".equals(msg)) msg += "\n";

      msg += "Il telefono é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + telOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + tel
          + "\n";
    }

    datiImpr.setValue("IMPR.FAXIMP", recapiti.getFax());
    if (datiImpr.isModifiedColumn("IMPR.FAXIMP")) {
      String faxOriginale = "";
      if (datiImpr.getColumn("IMPR.FAXIMP").getOriginalValue() != null)
        faxOriginale = datiImpr.getColumn("IMPR.FAXIMP").getOriginalValue().getStringValue();
      String fax = "";
      if (recapiti.getFax() != null) fax = recapiti.getFax();

      if (!"".equals(msg)) msg += "\n";

      msg += "Il fax é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + faxOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + fax
          + "\n";
    }

    datiImpr.setValue("IMPR.TELCEL", recapiti.getCellulare());
    if (datiImpr.isModifiedColumn("IMPR.TELCEL")) {
      String telOriginale = "";
      if (datiImpr.getColumn("IMPR.TELCEL").getOriginalValue() != null)
        telOriginale = datiImpr.getColumn("IMPR.TELCEL").getOriginalValue().getStringValue();
      String cell = "";
      if (recapiti.getCellulare() != null) cell = recapiti.getCellulare();

      if (!"".equals(msg)) msg += "\n";

      msg += "Il cellulare é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + telOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + cell
          + "\n";
    }

    datiImpr.setValue("IMPR.EMAI2IP", recapiti.getPec());
    if (datiImpr.isModifiedColumn("IMPR.EMAI2IP")) {
      String pecOriginale = "";
      if (datiImpr.getColumn("IMPR.EMAI2IP").getOriginalValue() != null)
        pecOriginale = datiImpr.getColumn("IMPR.EMAI2IP").getOriginalValue().getStringValue();
      String pec = "";
      if (recapiti.getPec() != null) pec = recapiti.getPec();

      if (!"".equals(msg)) msg += "\n";

      msg += "L'indirizzo PEC é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + pecOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + pec
          + "\n";
    }


    datiImpr.setValue("IMPR.INDWEB", impresa.getSitoWeb());
    if (datiImpr.isModifiedColumn("IMPR.INDWEB")) {
      String sitoWebOriginale = datiImpr.getColumn("IMPR.INDWEB").getOriginalValue().getStringValue();
      if (sitoWebOriginale == null)
        sitoWebOriginale="";
      String sitoWeb = "";
      if (impresa.getSitoWeb() != null) sitoWeb = impresa.getSitoWeb();

      msg += "L'indirizzo internet é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + sitoWebOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + sitoWeb
          + "\n";
    }

    // Sezione DURC
    String soggettoDurc = "";
    if (impresa.isSetSoggettoDURC()) {
      soggettoDurc = impresa.getSoggettoDURC();
      if ("0".equals(soggettoDurc))soggettoDurc = "2";
    }
    datiImpr.setValue("IMPR.SOGGDURC", soggettoDurc);
    if (datiImpr.isModifiedColumn("IMPR.SOGGDURC")) {
      String soggDurcOriginale = "";
      if (datiImpr.getColumn("IMPR.SOGGDURC").getOriginalValue() != null)
        soggDurcOriginale = datiImpr.getColumn("IMPR.SOGGDURC").getOriginalValue().getStringValue();

      if ("1".equals(soggettoDurc) )
        soggettoDurc = "Si";
      else if ("2".equals(soggettoDurc) )
        soggettoDurc = "No";
      else
        soggettoDurc = "";


      if ("1".equals(soggDurcOriginale) )
        soggDurcOriginale = "Si";
      else if ("2".equals(soggDurcOriginale) )
        soggDurcOriginale = "No";
      else
        soggDurcOriginale = "";

      if (!"".equals(msg)) msg += "\n";

      msg += "Il campo soggetto alle normative del DURC? é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + soggDurcOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + soggettoDurc
          + "\n";
    }

    String settoreProduttivoString = impresa.getSettoreProduttivo();
    Long settoreProduttivo = null;
    if (settoreProduttivoString != null && !"".equals(settoreProduttivoString))
      settoreProduttivo = new Long(settoreProduttivoString);
    else
      settoreProduttivoString = "";
    datiImpr.setValue("IMPR.SETTPROD", settoreProduttivo);
    if (datiImpr.isModifiedColumn("IMPR.SETTPROD")) {
      String descOriginale = "";
      Long tmp = datiImpr.getColumn("IMPR.SETTPROD").getOriginalValue().longValue();
      if (tmp != null)
        descOriginale = tabellatiManager.getDescrTabellato("G_056",
            tmp.toString());
      if (!"".equals(settoreProduttivoString))
        settoreProduttivoString = tabellatiManager.getDescrTabellato("G_056",
            settoreProduttivoString);

      if (!"".equals(msg)) msg += "\n";

      msg += "Il settore produttivo é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + descOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + settoreProduttivoString
          + "\n";
    }

    // Sezione CCIIA
    CameraCommercioType cciaa = impresa.getCciaa();

    String iscritto = "";
    if (cciaa.isSetIscritto()) {
      iscritto = cciaa.getIscritto();
      if ("0".equals(iscritto))
    	  iscritto = "2";
    }
    datiImpr.setValue("IMPR.ISCRCCIAA", iscritto);
    if (datiImpr.isModifiedColumn("IMPR.ISCRCCIAA")) {
      String iscrittoOriginale = "";
      if (datiImpr.getColumn("IMPR.ISCRCCIAA").getOriginalValue() != null)
        iscrittoOriginale = datiImpr.getColumn("IMPR.ISCRCCIAA").getOriginalValue().getStringValue();

      if ("1".equals(iscritto) )
        iscritto = "Si";
      else if ("2".equals(iscritto) )
        iscritto = "No";
      else
        iscritto = "";


      if ("1".equals(iscrittoOriginale) )
        iscrittoOriginale = "Si";
      else if ("2".equals(iscrittoOriginale) )
        iscrittoOriginale = "No";
      else
        iscrittoOriginale = "";

      if (!"".equals(msg)) msg += "\n";

      msg += "Il campo Iscritto alla Camera di Commercio? é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + iscrittoOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + iscritto
          + "\n";
    }

    String numIscrizione = cciaa.getNumIscrizione();
    datiImpr.setValue("IMPR.NCCIAA", numIscrizione);
    if (datiImpr.isModifiedColumn("IMPR.NCCIAA")) {
      String ncciaOriginale = "";
      if (datiImpr.getColumn("IMPR.NCCIAA").getOriginalValue() != null)
        ncciaOriginale = datiImpr.getColumn("IMPR.NCCIAA").getOriginalValue().getStringValue();

      if (numIscrizione == null) numIscrizione = "";

      if (!"".equals(msg)) msg += "\n";

      msg += "Il numero iscrizione Registro Imprese é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + ncciaOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + numIscrizione
          + "\n";
    }

    Calendar dataIscrizione = cciaa.getDataIscrizione();
    campoData = null;
    if (dataIscrizione != null) campoData = dataIscrizione.getTime();
    datiImpr.setValue("IMPR.DCCIAA", campoData);
    if (datiImpr.isModifiedColumn("IMPR.DCCIAA")) {
      String dcciaaOrginale = "";
      if (datiImpr.getColumn("IMPR.DCCIAA").getOriginalValue().dataValue() != null) {
        dcciaaOrginale = UtilityDate.convertiData(
            new Date(
                datiImpr.getColumn("IMPR.DCCIAA").getOriginalValue().dataValue().getTime()),
            UtilityDate.FORMATO_GG_MM_AAAA);
      }

      String dcciaa = "";
      if (dataIscrizione != null)
        dcciaa = UtilityDate.convertiData(dataIscrizione.getTime(),
            UtilityDate.FORMATO_GG_MM_AAAA);
      if (!"".equals(msg)) msg += "\n";

      msg += "La data iscrizione Registro Imprese é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dcciaaOrginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dcciaa
          + "\n";
    }

    String numRegistroDitte = cciaa.getNumRegistroDitte();
    datiImpr.setValue("IMPR.REGDIT", numRegistroDitte);
    if (datiImpr.isModifiedColumn("IMPR.REGDIT")) {
      String registroOriginale = "";
      if (datiImpr.getColumn("IMPR.REGDIT").getOriginalValue() != null)
        registroOriginale = datiImpr.getColumn("IMPR.REGDIT").getOriginalValue().getStringValue();

      if (numRegistroDitte == null) numRegistroDitte = "";

      if (!"".equals(msg)) msg += "\n";

      msg += "Il numero R.E.A. é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + registroOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + numRegistroDitte
          + "\n";

    }

    Calendar dataDomandaIscrizione = cciaa.getDataDomandaIscrizione();
    campoData = null;
    if (dataDomandaIscrizione != null)
      campoData = dataDomandaIscrizione.getTime();
    datiImpr.setValue("IMPR.DISCIF", campoData);
    if (datiImpr.isModifiedColumn("IMPR.DISCIF")) {
      String discifOrginale = "";
      if (datiImpr.getColumn("IMPR.DISCIF").getOriginalValue().dataValue() != null) {
        discifOrginale = UtilityDate.convertiData(
            new Date(
                datiImpr.getColumn("IMPR.DISCIF").getOriginalValue().dataValue().getTime()),
            UtilityDate.FORMATO_GG_MM_AAAA);

      }

      String discif = "";
      if (dataDomandaIscrizione != null)
        discif = UtilityDate.convertiData(dataDomandaIscrizione.getTime(),
            UtilityDate.FORMATO_GG_MM_AAAA);

      if (!"".equals(msg)) msg += "\n";

      msg += "La data iscrizione R.E.A. é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + discifOrginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + discif
          + "\n";
    }

    String provinciaIscrizione = cciaa.getProvinciaIscrizione();
    String pcciaa = "";
    if (provinciaIscrizione != null) {

      // select="SELECT TABCOD2 FROM TABSCHE WHERE TABCOD = 'S2003' AND TABCOD1 = '07' and upper(TABCOD3)= ?";

      try {
        // pcciaa = (String)sqlManager.getObject(select, new
        // Object[]{provinciaIscrizione.toUpperCase()});
        pcciaa = this.getCodiceISTAT(provinciaIscrizione.toUpperCase());
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella lettura Codice ISTAT provincia di iscriz. C.C.I.A.A.",
            null, e);
      }

    }
    datiImpr.setValue("IMPR.PCCIAA", pcciaa);
    if (datiImpr.isModifiedColumn("IMPR.PCCIAA")) {
      if (pcciaa == null) pcciaa = "";
      String pcciaaOriginale = "";
      if (datiImpr.getColumn("IMPR.PCCIAA").getOriginalValue() != null)
        pcciaaOriginale = datiImpr.getColumn("IMPR.PCCIAA").getOriginalValue().getStringValue();

      if (!"".equals(msg)) msg += "\n";

      msg += "Il codice ISTAT provincia di iscriz. C.C.I.A.A. é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + pcciaaOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + pcciaa
          + "\n";
    }

    Calendar dataNullaOsta = cciaa.getDataNullaOstaAntimafia();
    campoData = null;
    if (dataNullaOsta != null) campoData = dataNullaOsta.getTime();
    datiImpr.setValue("IMPR.DANTIM", campoData);
    if (datiImpr.isModifiedColumn("IMPR.DANTIM")) {
      String dantimOrginale = "";
      if (datiImpr.getColumn("IMPR.DANTIM").getOriginalValue().dataValue() != null) {
        dantimOrginale = UtilityDate.convertiData(
            new Date(
                datiImpr.getColumn("IMPR.DANTIM").getOriginalValue().dataValue().getTime()),
            UtilityDate.FORMATO_GG_MM_AAAA);
      }

      if (!"".equals(msg)) msg += "\n";

      String newValue = "";
      if (dataNullaOsta != null)
        newValue = UtilityDate.convertiData(dataNullaOsta.getTime(),
            UtilityDate.FORMATO_GG_MM_AAAA);

      msg += "La data nulla osta antimafia é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dantimOrginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + newValue
          + "\n";
    }

    // Sezione Iscrizione INPS,INAIL,CASSA EDILE
    INPSType inps = impresa.getInps();
    datiImpr.setValue("IMPR.NINPS", inps.getNumIscrizione());
    if (datiImpr.isModifiedColumn("IMPR.NINPS")) {
      String ninpsOriginale = "";
      if (datiImpr.getColumn("IMPR.NINPS").getOriginalValue() != null)
        ninpsOriginale = datiImpr.getColumn("IMPR.NINPS").getOriginalValue().getStringValue();
      String ninps = "";
      if (inps.getNumIscrizione() != null) ninps = inps.getNumIscrizione();
      if (!"".equals(msg)) msg += "\n";

      msg += "Il numero iscrizione INPS é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + ninpsOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + ninps
          + "\n";
    }

    datiImpr.setValue("IMPR.POSINPS", inps.getPosizContributivaIndividuale());
    if (datiImpr.isModifiedColumn("IMPR.POSINPS")) {
      String posinpsOriginale = "";
      if (datiImpr.getColumn("IMPR.POSINPS").getOriginalValue() != null)
        posinpsOriginale = datiImpr.getColumn("IMPR.POSINPS").getOriginalValue().getStringValue();
      String posinps = "";
      if (inps.getPosizContributivaIndividuale() != null) posinps = inps.getPosizContributivaIndividuale();
      if (!"".equals(msg)) msg += "\n";

      msg += "La posizione contributiva individuale INPS é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + posinpsOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + posinps
          + "\n";
    }

    Calendar dataIscrizioneINPS = inps.getDataIscrizione();
    campoData = null;
    if (dataIscrizioneINPS != null) campoData = dataIscrizioneINPS.getTime();
    datiImpr.setValue("IMPR.DINPS", campoData);
    if (datiImpr.isModifiedColumn("IMPR.DINPS")) {
      String dinpsOriginale = "";
      if (datiImpr.getColumn("IMPR.DINPS").getOriginalValue().dataValue() != null) {
        dinpsOriginale = UtilityDate.convertiData(
            new Date(
                datiImpr.getColumn("IMPR.DINPS").getOriginalValue().dataValue().getTime()),
            UtilityDate.FORMATO_GG_MM_AAAA);
      }

      String dataINPS = "";
      if (dataIscrizioneINPS != null)
        dataINPS = UtilityDate.convertiData(dataIscrizioneINPS.getTime(),
            UtilityDate.FORMATO_GG_MM_AAAA);
      if (!"".equals(msg)) msg += "\n";

      msg += "La data iscrizione INPS é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dinpsOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dataINPS
          + "\n";
    }

    datiImpr.setValue("IMPR.LINPS", inps.getLocalitaIscrizione());
    if (datiImpr.isModifiedColumn("IMPR.LINPS")) {
      String linpsOriginale = "";
      if (datiImpr.getColumn("IMPR.LINPS").getOriginalValue() != null)
        linpsOriginale = datiImpr.getColumn("IMPR.LINPS").getOriginalValue().getStringValue();
      String linps = "";
      if (inps.getLocalitaIscrizione() != null)
        linps = inps.getLocalitaIscrizione();
      if (!"".equals(msg)) msg += "\n";

      msg += "La località di iscrizione INPS é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + linpsOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + linps
          + "\n";
    }

    INAILType inail = impresa.getInail();
    datiImpr.setValue("IMPR.NINAIL", inail.getNumIscrizione());
    if (datiImpr.isModifiedColumn("IMPR.NINAIL")) {
      String ninailOriginale = "";
      if (datiImpr.getColumn("IMPR.NINAIL").getOriginalValue() != null)
        ninailOriginale = datiImpr.getColumn("IMPR.NINAIL").getOriginalValue().getStringValue();
      String ninail = "";
      if (inail.getNumIscrizione() != null) ninail = inail.getNumIscrizione();
      if (!"".equals(msg)) msg += "\n";

      msg += "Il numero iscrizione INAIL é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + ninailOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + ninail
          + "\n";
    }

    datiImpr.setValue("IMPR.POSINAIL", inail.getPosizAssicurativa());
    if (datiImpr.isModifiedColumn("IMPR.POSINAIL")) {
      String posinailOriginale = "";
      if (datiImpr.getColumn("IMPR.POSINAIL").getOriginalValue() != null)
        posinailOriginale = datiImpr.getColumn("IMPR.POSINAIL").getOriginalValue().getStringValue();
      String posinail = "";
      if (inail.getPosizAssicurativa() != null) posinail = inail.getPosizAssicurativa();
      if (!"".equals(msg)) msg += "\n";

      msg += "La posizione assicurativa INAIL é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + posinailOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + posinail
          + "\n";
    }

    Calendar dataIscrizioneINAIL = inail.getDataIscrizione();
    campoData = null;
    if (dataIscrizioneINAIL != null) campoData = dataIscrizioneINAIL.getTime();
    datiImpr.setValue("IMPR.DINAIL", campoData);
    if (datiImpr.isModifiedColumn("IMPR.DINAIL")) {
      String dinailOriginale = "";
      if (datiImpr.getColumn("IMPR.DINAIL").getOriginalValue().dataValue() != null) {
        dinailOriginale = UtilityDate.convertiData(
            new Date(
                datiImpr.getColumn("IMPR.DINAIL").getOriginalValue().dataValue().getTime()),
            UtilityDate.FORMATO_GG_MM_AAAA);
      }

      String dataINAIL = "";
      if (dataIscrizioneINAIL != null)
        dataINAIL = UtilityDate.convertiData(dataIscrizioneINAIL.getTime(),
            UtilityDate.FORMATO_GG_MM_AAAA);
      if (!"".equals(msg)) msg += "\n";

      msg += "La data iscrizione INAIL é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dinailOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dataINAIL
          + "\n";
    }

    datiImpr.setValue("IMPR.LINAIL", inail.getLocalitaIscrizione());
    if (datiImpr.isModifiedColumn("IMPR.LINAIL")) {
      String linailOriginale = "";
      if (datiImpr.getColumn("IMPR.LINAIL").getOriginalValue() != null)
        linailOriginale = datiImpr.getColumn("IMPR.LINAIL").getOriginalValue().getStringValue();
      String linail = "";
      if (inail.getLocalitaIscrizione() != null)
        linail = inail.getLocalitaIscrizione();
      if (!"".equals(msg)) msg += "\n";

      msg += "La località di iscrizione INAIL é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + linailOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + linail
          + "\n";
    }

    CassaEdileType CassaEdile = impresa.getCassaEdile();
    datiImpr.setValue("IMPR.NCEDIL", CassaEdile.getNumIscrizione());
    if (datiImpr.isModifiedColumn("IMPR.NCEDIL")) {
      String ncedilOriginale = "";
      if (datiImpr.getColumn("IMPR.NCEDIL").getOriginalValue() != null)
        ncedilOriginale = datiImpr.getColumn("IMPR.NCEDIL").getOriginalValue().getStringValue();
      String ncedil = "";
      if (CassaEdile.getNumIscrizione() != null)
        ncedil = CassaEdile.getNumIscrizione();
      if (!"".equals(msg)) msg += "\n";

      msg += "Il numero iscrizione Cassa Edile é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + ncedilOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + ncedil
          + "\n";
    }

    datiImpr.setValue("IMPR.CODCEDIL", CassaEdile.getCodice());
    if (datiImpr.isModifiedColumn("IMPR.CODCEDIL")) {
      String codiceOriginale = "";
      if (datiImpr.getColumn("IMPR.CODCEDIL").getOriginalValue() != null)
        codiceOriginale = datiImpr.getColumn("IMPR.CODCEDIL").getOriginalValue().getStringValue();
      String codice = "";
      if (CassaEdile.getCodice() != null) codice = CassaEdile.getCodice();
      if (!"".equals(msg)) msg += "\n";

      msg += "Il codice Cassa Edile é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + codiceOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + codice
          + "\n";
    }

    Calendar dataIscrizioneCassaEdile = CassaEdile.getDataIscrizione();
    campoData = null;
    if (dataIscrizioneCassaEdile != null)
      campoData = dataIscrizioneCassaEdile.getTime();
    datiImpr.setValue("IMPR.DCEDIL", campoData);
    if (datiImpr.isModifiedColumn("IMPR.DCEDIL")) {
      String dcedilOriginale = "";
      if (datiImpr.getColumn("IMPR.DCEDIL").getOriginalValue().dataValue() != null) {
        dcedilOriginale = UtilityDate.convertiData(
            new Date(
                datiImpr.getColumn("IMPR.DCEDIL").getOriginalValue().dataValue().getTime()),
            UtilityDate.FORMATO_GG_MM_AAAA);
      }

      String dataCassaEdile = "";
      if (dataIscrizioneCassaEdile != null)
        dataCassaEdile = UtilityDate.convertiData(
            dataIscrizioneCassaEdile.getTime(), UtilityDate.FORMATO_GG_MM_AAAA);
      if (!"".equals(msg)) msg += "\n";

      msg += "La data iscrizione Cassa Edile é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dcedilOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dataCassaEdile
          + "\n";
    }

    datiImpr.setValue("IMPR.LCEDIL", CassaEdile.getLocalitaIscrizione());
    if (datiImpr.isModifiedColumn("IMPR.LCEDIL")) {
      String lcedilOriginale = "";
      if (datiImpr.getColumn("IMPR.LCEDIL").getOriginalValue() != null)
        lcedilOriginale = datiImpr.getColumn("IMPR.LCEDIL").getOriginalValue().getStringValue();
      String lcedil = "";
      if (CassaEdile.getLocalitaIscrizione() != null)
        lcedil = CassaEdile.getLocalitaIscrizione();
      if (!"".equals(msg)) msg += "\n";

      msg += "La località di iscrizione Cassa Edile é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + lcedilOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + lcedil
          + "\n";
    }

    // Libero Professionista
    if ("6".equals(tipoImpresaString)) {
      AltriDatiAnagraficiType datiLiberoProfessionista = impresa.getAltriDatiAnagrafici();
      datiImpr.setValue("IMPR.COGNOME", datiLiberoProfessionista.getCognome());
      if (datiImpr.isModifiedColumn("IMPR.COGNOME")) {
        String cognomeOriginale = "";
        if (datiImpr.getColumn("IMPR.COGNOME").getOriginalValue() != null)
          cognomeOriginale = datiImpr.getColumn("IMPR.COGNOME").getOriginalValue().getStringValue();
        String cognome = "";
        if (datiLiberoProfessionista.getCognome() != null)
          cognome = datiLiberoProfessionista.getCognome();
        if (!"".equals(msg)) msg += "\n";

        msg += "Il cognome del professionista e' cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + cognomeOriginale
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + cognome
            + "\n";
      }

      datiImpr.setValue("IMPR.NOME", datiLiberoProfessionista.getNome());
      if (datiImpr.isModifiedColumn("IMPR.NOME")) {
        String nomeOriginale = "";
        if (datiImpr.getColumn("IMPR.NOME").getOriginalValue() != null)
          nomeOriginale = datiImpr.getColumn("IMPR.NOME").getOriginalValue().getStringValue();
        String nome = "";
        if (datiLiberoProfessionista.getNome() != null)
          nome = datiLiberoProfessionista.getNome();
        if (!"".equals(msg)) msg += "\n";

        msg += "Il nome del professionista e' cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + nomeOriginale
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + nome
            + "\n";
      }

      AlboProfessionaleType alboProfessionale = datiLiberoProfessionista.getAlboProfessionale();

      datiImpr.setValue("IMPR.TIPALB", alboProfessionale.getTipologia());
      if (datiImpr.isModifiedColumn("IMPR.TIPALB")) {
        Long tipologiaOriginale = datiImpr.getColumn("IMPR.TIPALB").getOriginalValue().longValue();
        String tipologiaOriginaleDesc;
        if (tipologiaOriginale != null) {
          tipologiaOriginaleDesc = tabellatiManager.getDescrTabellato("G_040",
              tipologiaOriginale.toString());
        } else {
          tipologiaOriginaleDesc = "";
        }

        String tipologiaDesc = "";
        if (alboProfessionale.getTipologia() != null)
          tipologiaDesc = tabellatiManager.getDescrTabellato("G_040",
              alboProfessionale.getTipologia());

        if (!"".equals(msg)) msg += "\n";

        msg += "La tipologia dell'albo professionale é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + tipologiaOriginaleDesc
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + tipologiaDesc
            + "\n";
      }

      datiImpr.setValue("IMPR.ALBTEC", alboProfessionale.getNumIscrizione());
      if (datiImpr.isModifiedColumn("IMPR.ALBTEC")) {
        String numIscrizioneOriginale = "";
        if (datiImpr.getColumn("IMPR.ALBTEC").getOriginalValue() != null)
          numIscrizioneOriginale = datiImpr.getColumn("IMPR.ALBTEC").getOriginalValue().getStringValue();
        String numIscr = "";
        if (alboProfessionale.getNumIscrizione() != null)
          numIscr = alboProfessionale.getNumIscrizione();
        if (!"".equals(msg)) msg += "\n";

        msg += "Il numero iscrizione all'albo professionale e' cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + numIscrizioneOriginale
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + numIscr
            + "\n";
      }

      Calendar dataIscrizioneAlbo = alboProfessionale.getDataIscrizione();
      campoData = null;
      if (dataIscrizioneAlbo != null) campoData = dataIscrizioneAlbo.getTime();
      datiImpr.setValue("IMPR.DATALB", campoData);
      if (datiImpr.isModifiedColumn("IMPR.DATALB")) {
        String datalbOriginale = "";
        if (datiImpr.getColumn("IMPR.DATALB").getOriginalValue().dataValue() != null) {
          datalbOriginale = UtilityDate.convertiData(
              new Date(
                  datiImpr.getColumn("IMPR.DATALB").getOriginalValue().dataValue().getTime()),
              UtilityDate.FORMATO_GG_MM_AAAA);
        }

        String datalb = "";
        if (dataIscrizioneAlbo != null)
          datalb = UtilityDate.convertiData(dataIscrizioneAlbo.getTime(),
              UtilityDate.FORMATO_GG_MM_AAAA);
        if (!"".equals(msg)) msg += "\n";

        msg += "La data iscrizione all'albo professionale é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + datalbOriginale
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + datalb
            + "\n";
      }

      provinciaIscrizione = alboProfessionale.getProvinciaIscrizione();
      String codiceIstat = null;
      try {
        if (provinciaIscrizione != null)
          codiceIstat = this.getCodiceISTAT(provinciaIscrizione.toUpperCase());

      } catch (SQLException e) {
        throw new GestoreException("Errore nella lettura del codice ISTAT ",
            null, e);
      }

      datiImpr.setValue("IMPR.PROALB", codiceIstat);
      if (datiImpr.isModifiedColumn("IMPR.PROALB")) {
        String codiceISTATOriginale = "";
        if (datiImpr.getColumn("IMPR.PROALB").getOriginalValue() != null)
          codiceISTATOriginale = datiImpr.getColumn("IMPR.PROALB").getOriginalValue().getStringValue();

        if (!"".equals(msg)) msg += "\n";

        msg += "Il codice ISTAT della provincia di iscrizione all'albo è cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + codiceISTATOriginale
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + codiceIstat
            + "\n";
      }

      CassaPrevidenzaType cassaPrevidenza = datiLiberoProfessionista.getCassaPrevidenza();
      datiImpr.setValue("IMPR.TCAPRE", cassaPrevidenza.getTipologia());
      if (datiImpr.isModifiedColumn("IMPR.TCAPRE")) {
        Long tipologiaOriginale = datiImpr.getColumn("IMPR.TCAPRE").getOriginalValue().longValue();
        String tipologiaOriginaleDesc;
        if (tipologiaOriginale != null) {
          tipologiaOriginaleDesc = tabellatiManager.getDescrTabellato("G_041",
              tipologiaOriginale.toString());
        } else {
          tipologiaOriginaleDesc = "";
        }

        String tipologiaDesc = "";
        if (cassaPrevidenza.getTipologia() != null)
          tipologiaDesc = tabellatiManager.getDescrTabellato("G_041",
              cassaPrevidenza.getTipologia());

        if (!"".equals(msg)) msg += "\n";

        msg += "La tipologia della cassa di previdenza é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + tipologiaOriginaleDesc
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + tipologiaDesc
            + "\n";
      }

      datiImpr.setValue("IMPR.NCAPRE", cassaPrevidenza.getNumMatricola());
      if (datiImpr.isModifiedColumn("IMPR.NCAPRE")) {
        String numIscrizioneOriginale = "";
        if (datiImpr.getColumn("IMPR.NCAPRE").getOriginalValue() != null)
          numIscrizioneOriginale = datiImpr.getColumn("IMPR.NCAPRE").getOriginalValue().getStringValue();
        String numIscr = "";
        if (cassaPrevidenza.getNumMatricola() != null)
          numIscr = cassaPrevidenza.getNumMatricola();
        if (!"".equals(msg)) msg += "\n";

        msg += "Il numero matricola di iscrizione alla cassa di previdenza  e' cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + numIscrizioneOriginale
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + numIscr
            + "\n";
      }

    }

    // Sezione Iscrizione S.O.A
    SOAType soa = impresa.getSoa();
    datiImpr.setValue("IMPR.NISANC", soa.getNumIscrizione());
    if (datiImpr.isModifiedColumn("IMPR.NISANC")) {
      String nisancOriginale = "";
      if (datiImpr.getColumn("IMPR.NISANC").getOriginalValue() != null)
        nisancOriginale = datiImpr.getColumn("IMPR.NISANC").getOriginalValue().getStringValue();
      String nisanc = "";
      if (soa.getNumIscrizione() != null) nisanc = soa.getNumIscrizione();
      if (!"".equals(msg)) msg += "\n";

      msg += "Il numero attestazione SOA é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + nisancOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + nisanc
          + "\n";
    }

    Calendar dataIscrizioneSOA = soa.getDataIscrizione();
    campoData = null;
    if (dataIscrizioneSOA != null) campoData = dataIscrizioneSOA.getTime();
    datiImpr.setValue("IMPR.DISANC", campoData);
    if (datiImpr.isModifiedColumn("IMPR.DISANC")) {
      String disancOriginale = "";
      if (datiImpr.getColumn("IMPR.DISANC").getOriginalValue().dataValue() != null) {
        disancOriginale = UtilityDate.convertiData(
            new Date(
                datiImpr.getColumn("IMPR.DISANC").getOriginalValue().dataValue().getTime()),
            UtilityDate.FORMATO_GG_MM_AAAA);
      }

      String dataSOA = "";
      if (dataIscrizioneSOA != null)
        dataSOA = UtilityDate.convertiData(dataIscrizioneSOA.getTime(),
            UtilityDate.FORMATO_GG_MM_AAAA);
      if (!"".equals(msg)) msg += "\n";

      msg += "La data rilascio dell'attestazione SOA é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + disancOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dataSOA
          + "\n";
    }

    Calendar dataScadenzaTriennaleSOA = soa.getDataScadenzaTriennale();
    campoData = null;
    if (dataScadenzaTriennaleSOA != null) campoData = dataScadenzaTriennaleSOA.getTime();
    datiImpr.setValue("IMPR.DTRISOA", campoData);
    if (datiImpr.isModifiedColumn("IMPR.DTRISOA")) {
      String dtrisoaOriginale = "";
      if (datiImpr.getColumn("IMPR.DTRISOA").getOriginalValue().dataValue() != null) {
        dtrisoaOriginale = UtilityDate.convertiData(
            new Date(
                datiImpr.getColumn("IMPR.DTRISOA").getOriginalValue().dataValue().getTime()),
            UtilityDate.FORMATO_GG_MM_AAAA);
      }

      String dtrisoa = "";
      if (dataScadenzaTriennaleSOA != null)
        dtrisoa = UtilityDate.convertiData(dataScadenzaTriennaleSOA.getTime(),
            UtilityDate.FORMATO_GG_MM_AAAA);
      if (!"".equals(msg)) msg += "\n";

      msg += "La data scadenza validità triennale dell'attestazione SOA é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dtrisoaOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dtrisoa
          + "\n";
    }

    Calendar dataVerificaTriennaleSOA = soa.getDataVerificaTriennale();
    campoData = null;
    if (dataVerificaTriennaleSOA != null) campoData = dataVerificaTriennaleSOA.getTime();
    datiImpr.setValue("IMPR.DVERSOA", campoData);
    if (datiImpr.isModifiedColumn("IMPR.DVERSOA")) {
      String dversoaOriginale = "";
      if (datiImpr.getColumn("IMPR.DVERSOA").getOriginalValue().dataValue() != null) {
        dversoaOriginale = UtilityDate.convertiData(
            new Date(
                datiImpr.getColumn("IMPR.DVERSOA").getOriginalValue().dataValue().getTime()),
            UtilityDate.FORMATO_GG_MM_AAAA);
      }

      String dversoa = "";
      if (dataVerificaTriennaleSOA != null)
        dversoa = UtilityDate.convertiData(dataVerificaTriennaleSOA.getTime(),
            UtilityDate.FORMATO_GG_MM_AAAA);
      if (!"".equals(msg)) msg += "\n";

      msg += "La data verifica triennale dell'attestazione SOA é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dversoaOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dversoa
          + "\n";
    }

    Calendar dataScadenzaIntermediaSOA = soa.getDataScadenzaIntermedia();
    campoData = null;
    if (dataScadenzaIntermediaSOA != null) campoData = dataScadenzaIntermediaSOA.getTime();
    datiImpr.setValue("IMPR.DINTSOA", campoData);
    if (datiImpr.isModifiedColumn("IMPR.DINTSOA")) {
      String dintsoaOriginale = "";
      if (datiImpr.getColumn("IMPR.DINTSOA").getOriginalValue().dataValue() != null) {
        dintsoaOriginale = UtilityDate.convertiData(
            new Date(
                datiImpr.getColumn("IMPR.DINTSOA").getOriginalValue().dataValue().getTime()),
            UtilityDate.FORMATO_GG_MM_AAAA);
      }

      String dintsoa = "";
      if (dataScadenzaIntermediaSOA != null)
        dintsoa = UtilityDate.convertiData(dataScadenzaIntermediaSOA.getTime(),
            UtilityDate.FORMATO_GG_MM_AAAA);
      if (!"".equals(msg)) msg += "\n";

      msg += "La data scadenza intermedia dell'attestazione SOA é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dintsoaOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dintsoa
          + "\n";
    }

    Calendar dataScadenzaSOA = soa.getDataScadenza();
    campoData = null;
    if (dataScadenzaSOA != null) campoData = dataScadenzaSOA.getTime();
    datiImpr.setValue("IMPR.DSCANC", campoData);
    if (datiImpr.isModifiedColumn("IMPR.DSCANC")) {
      String dcsancOriginale = "";
      if (datiImpr.getColumn("IMPR.DSCANC").getOriginalValue().dataValue() != null) {
        dcsancOriginale = UtilityDate.convertiData(
            new Date(
                datiImpr.getColumn("IMPR.DSCANC").getOriginalValue().dataValue().getTime()),
            UtilityDate.FORMATO_GG_MM_AAAA);
      }

      String dscanc = "";
      if (dataScadenzaSOA != null)
        dscanc = UtilityDate.convertiData(dataScadenzaSOA.getTime(),
            UtilityDate.FORMATO_GG_MM_AAAA);
      if (!"".equals(msg)) msg += "\n";

      msg += "La data scadenza validità quinquennale dell'attestazione SOA é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dcsancOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dscanc
          + "\n";
    }

    /*
    Calendar dataUltimaRichiestaIscrizione = soa.getDataUltimaRichiestaIscrizione();
    campoData = null;
    if (dataUltimaRichiestaIscrizione != null)
      campoData = dataUltimaRichiestaIscrizione.getTime();
    datiImpr.setValue("IMPR.DURANC", campoData);
    if (datiImpr.isModifiedColumn("IMPR.DURANC")) {
      String durancOriginale = "";
      if (datiImpr.getColumn("IMPR.DURANC").getOriginalValue().dataValue() != null) {
        durancOriginale = UtilityDate.convertiData(
            new Date(
                datiImpr.getColumn("IMPR.DURANC").getOriginalValue().dataValue().getTime()),
            UtilityDate.FORMATO_GG_MM_AAAA);
      }

      String duranc = "";
      if (dataUltimaRichiestaIscrizione != null)
        duranc = UtilityDate.convertiData(
            dataUltimaRichiestaIscrizione.getTime(),
            UtilityDate.FORMATO_GG_MM_AAAA);
      if (!"".equals(msg)) msg += "\n";

      msg += "La data ultima richiesta iscrizione S.O.A. é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + durancOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + duranc
          + "\n";
    }
    */

    String organismoCertSOAString = soa.getOrganismoCertificatore();
    Long organismoCertSOA = null;
    if (organismoCertSOAString != null && !"".equals(organismoCertSOAString))
      organismoCertSOA = new Long(organismoCertSOAString);
    else
      organismoCertSOAString = "";
    datiImpr.setValue("IMPR.OCTSOA", organismoCertSOA);
    if (datiImpr.isModifiedColumn("IMPR.OCTSOA")) {
      String descOriginale = "";
      if (datiImpr.getColumn("IMPR.OCTSOA").getOriginalValue().longValue() != null) {
        Long tmp = datiImpr.getColumn("IMPR.OCTSOA").getOriginalValue().longValue();
        descOriginale = tabellatiManager.getDescrTabellato("Ag020",
            tmp.toString());
      }
      if (!"".equals(organismoCertSOAString))
        organismoCertSOAString = tabellatiManager.getDescrTabellato("Ag020",
            organismoCertSOAString);

      if (!"".equals(msg)) msg += "\n";

      msg += "L'organismo di attestazione SOA é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + descOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + organismoCertSOAString
          + "\n";
    }

    // Sezione certificazione ISO9000
    ISO9001Type iso = impresa.getIso9001();
    datiImpr.setValue("IMPR.NUMISO", iso.getNumIscrizione());
    if (datiImpr.isModifiedColumn("IMPR.NUMISO")) {
      String numisoOriginale = "";
      if (datiImpr.getColumn("IMPR.NUMISO").getOriginalValue() != null)
        numisoOriginale = datiImpr.getColumn("IMPR.NUMISO").getOriginalValue().getStringValue();
      String numiso = "";
      if (iso.getNumIscrizione() != null) numiso = iso.getNumIscrizione();
      if (!"".equals(msg)) msg += "\n";

      msg += "Il numero iscrizione ISO 9001 é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + numisoOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + numiso
          + "\n";

    }

    Calendar dataScadenzaIso = iso.getDataScadenza();
    campoData = null;
    if (dataScadenzaIso != null) campoData = dataScadenzaIso.getTime();
    datiImpr.setValue("IMPR.DATISO", campoData);
    if (datiImpr.isModifiedColumn("IMPR.DATISO")) {
      String datisoOriginale = "";
      if (datiImpr.getColumn("IMPR.DATISO").getOriginalValue().dataValue() != null) {
        datisoOriginale = UtilityDate.convertiData(
            new Date(
                datiImpr.getColumn("IMPR.DATISO").getOriginalValue().dataValue().getTime()),
            UtilityDate.FORMATO_GG_MM_AAAA);
      }

      String datiso = "";
      if (dataScadenzaIso != null)
        datiso = UtilityDate.convertiData(dataScadenzaIso.getTime(),
            UtilityDate.FORMATO_GG_MM_AAAA);
      if (!"".equals(msg)) msg += "\n";

      msg += "La data scadenza certificaz. ISO 9001 é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + datisoOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + datiso
          + "\n";
    }

    String organismoCertISOString = iso.getOrganismoCertificatore();
    Long organismoCertISO = null;
    if (organismoCertISOString != null && !"".equals(organismoCertISOString))
      organismoCertISO = new Long(organismoCertISOString);
    else
      organismoCertISOString = "";
    datiImpr.setValue("IMPR.OCTISO", organismoCertISO);
    if (datiImpr.isModifiedColumn("IMPR.OCTISO")) {
      String descOriginale = "";
      if (datiImpr.getColumn("IMPR.OCTISO").getOriginalValue().longValue() != null) {
        Long tmp = datiImpr.getColumn("IMPR.OCTISO").getOriginalValue().longValue();
        descOriginale = tabellatiManager.getDescrTabellato("Ag021",
            tmp.toString());
      }
      if (!"".equals(organismoCertISOString))
        organismoCertISOString = tabellatiManager.getDescrTabellato("Ag021",
            organismoCertISOString);

      if (!"".equals(msg)) msg += "\n";

      msg += "L'organismo certificatore ISO 9001 é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + descOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + organismoCertISOString
          + "\n";
    }

    /////////////////////////////////////////////////
    // white list
    IscrizioneWhitelistAntimafiaType whla = impresa.getIscrizioneWhitelistAntimafia();
    if (whla != null){
      //Iscritto nella white list antimafia (dpcm 18 aprile 2013)?
      String iscrittoWhla = null;
      if (whla.isSetIscritto()) {
        iscrittoWhla = whla.getIscritto();
        if ("0".equals(iscrittoWhla))
          iscrittoWhla =  "2";
      }
      datiImpr.setValue("IMPR.ISCRIWL", iscrittoWhla);
      if (datiImpr.isModifiedColumn("IMPR.ISCRIWL")) {
        String iscrittoWhlaOriginale = "";
        if (datiImpr.getColumn("IMPR.ISCRIWL").getOriginalValue() != null)
          iscrittoWhlaOriginale = datiImpr.getColumn("IMPR.ISCRIWL").getOriginalValue().getStringValue();

        if ("1".equals(iscrittoWhla) )
          iscrittoWhla = "Si";
        else if ("2".equals(iscrittoWhla) )
          iscrittoWhla = "No";
        else
          iscrittoWhla = "";

        if ("1".equals(iscrittoWhlaOriginale) )
          iscrittoWhlaOriginale = "Si";
        else if ("2".equals(iscrittoWhlaOriginale) )
          iscrittoWhlaOriginale = "No";
        else
          iscrittoWhlaOriginale = "";

        if (!"".equals(msg)) msg += "\n";

        msg += "Il campo iscritto nella white list antimafia (dpcm 18 aprile 2013)? é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + iscrittoWhlaOriginale
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + iscrittoWhla
            + "\n";
      }
      // Sede prefettura presso cui è istituita la white list antim.
      //String sedePrefetturaCompetenteWhla = whla.getSedePrefetturaCompetente();
      datiImpr.setValue("IMPR.WLPREFE", whla.getSedePrefetturaCompetente());
      if (datiImpr.isModifiedColumn("IMPR.WLPREFE")) {
        String sedePrefetturaCompetenteWhlaOriginale = "";
        if (datiImpr.getColumn("IMPR.WLPREFE").getOriginalValue() != null)
          sedePrefetturaCompetenteWhlaOriginale = datiImpr.getColumn("IMPR.WLPREFE").getOriginalValue().getStringValue();
        String sedePrefetturaCompetenteWhla = "";
        if (whla.getSedePrefetturaCompetente() != null) sedePrefetturaCompetenteWhla = whla.getSedePrefetturaCompetente();
        if (!"".equals(msg)) msg += "\n";

        msg += "La sede prefettura presso cui è istituita la white list antim. é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + sedePrefetturaCompetenteWhlaOriginale
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + sedePrefetturaCompetenteWhla
            + "\n";

      }
      // Sezioni di iscrizione white list antimafia
      datiImpr.setValue("IMPR.WLSEZIO", whla.getSezioniIscrizione());
      if (datiImpr.isModifiedColumn("IMPR.WLSEZIO")) {
        String sezioniIscrizioneWhlaOriginale = "";
        if (datiImpr.getColumn("IMPR.WLSEZIO").getOriginalValue() != null){
          sezioniIscrizioneWhlaOriginale = datiImpr.getColumn("IMPR.WLSEZIO").getOriginalValue().getStringValue();
        }
        String sezioniIscrizioneWhla = "";
        if (whla.getSezioniIscrizione() != null){
          sezioniIscrizioneWhla = whla.getSezioniIscrizione();
        }
        if (!"".equals(msg)) msg += "\n";

        msg += "Le sezioni di iscrizione white list antimafia sono cambiate \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + sezioniIscrizioneWhlaOriginale
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + sezioniIscrizioneWhla
            + "\n";

      }
      // Data iscrizione white list antimafia
      Calendar dataIscrizioneWhla = whla.getDataIscrizione();
      campoData = null;
      if (dataIscrizioneWhla != null) campoData = dataIscrizioneWhla.getTime();
      datiImpr.setValue("IMPR.WLDISCRI", campoData);
      if (datiImpr.isModifiedColumn("IMPR.WLDISCRI")) {
        String discrWhlaOriginale = "";
        if (datiImpr.getColumn("IMPR.WLDISCRI").getOriginalValue().dataValue() != null) {
          discrWhlaOriginale = UtilityDate.convertiData(
              new Date(
                  datiImpr.getColumn("IMPR.WLDISCRI").getOriginalValue().dataValue().getTime()),
              UtilityDate.FORMATO_GG_MM_AAAA);
        }

        String discrWhla = "";
        if (dataIscrizioneWhla != null)
          discrWhla = UtilityDate.convertiData(dataIscrizioneWhla.getTime(),
              UtilityDate.FORMATO_GG_MM_AAAA);
        if (!"".equals(msg)) msg += "\n";

        msg += "La data iscrizione white list antimafia é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + discrWhlaOriginale
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + discrWhla
            + "\n";
      }
      // Data scadenza iscrizione white list antimafia
      Calendar dataScadenzaIscrizioneWhla = whla.getDataScadenzaIscrizione();
      campoData = null;
      if (dataScadenzaIscrizioneWhla != null) campoData = dataScadenzaIscrizioneWhla.getTime();
      datiImpr.setValue("IMPR.WLDSCAD", campoData);
      if (datiImpr.isModifiedColumn("IMPR.WLDSCAD")) {
        String dscadIscrWhlaOriginale = "";
        if (datiImpr.getColumn("IMPR.WLDSCAD").getOriginalValue().dataValue() != null) {
          dscadIscrWhlaOriginale = UtilityDate.convertiData(
              new Date(
                  datiImpr.getColumn("IMPR.WLDSCAD").getOriginalValue().dataValue().getTime()),
              UtilityDate.FORMATO_GG_MM_AAAA);
        }

        String dscadIscrWhla = "";
        if (dataScadenzaIscrizioneWhla != null)
          dscadIscrWhla = UtilityDate.convertiData(dataScadenzaIscrizioneWhla.getTime(),
              UtilityDate.FORMATO_GG_MM_AAAA);
        if (!"".equals(msg)) msg += "\n";

        msg += "La data scadenza iscrizione white list antimafia é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + dscadIscrWhlaOriginale
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + dscadIscrWhla
            + "\n";
      }
      //Aggiornamento in corso white list antimafia?
      String aggiornamentoWhla = null;
      if (whla.isSetAggiornamento()) {
        aggiornamentoWhla = whla.getAggiornamento();
        if ("0".equals(aggiornamentoWhla))
          aggiornamentoWhla =  "2";
      }
      datiImpr.setValue("IMPR.WLINCORSO", aggiornamentoWhla);
      if (datiImpr.isModifiedColumn("IMPR.WLINCORSO")) {
        String aggiornamentoWhlaOriginale = "";
        if (datiImpr.getColumn("IMPR.WLINCORSO").getOriginalValue() != null)
          aggiornamentoWhlaOriginale = datiImpr.getColumn("IMPR.WLINCORSO").getOriginalValue().getStringValue();

        if ("1".equals(aggiornamentoWhla) )
          aggiornamentoWhla = "Si";
        else if ("2".equals(aggiornamentoWhla) )
          aggiornamentoWhla = "No";
        else
          aggiornamentoWhla = "";

        if ("1".equals(aggiornamentoWhlaOriginale) )
          aggiornamentoWhlaOriginale = "Si";
        else if ("2".equals(aggiornamentoWhlaOriginale) )
          aggiornamentoWhlaOriginale = "No";
        else
          aggiornamentoWhlaOriginale = "";

        if (!"".equals(msg)) msg += "\n";

        msg += "Il campo aggiornamento in corso white list antimafia? é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + aggiornamentoWhlaOriginale
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + aggiornamentoWhla
            + "\n";
      }


    }
    /////////////////////////////////////////////////

    String msgTmp=null;
    ////////////////////////////////
    //Iscrizione elenchi ricostruzione
    IscrizioneElenchiRicostruzioneType iscrizioneElenchiRic = impresa.getIscrizioneElenchiRicostruzione();
    if (iscrizioneElenchiRic != null){

      String iscrittoElenchiRic = null;
      if (iscrizioneElenchiRic.isSetIscrittoAnagrafeAntimafiaEsecutori()) {
        iscrittoElenchiRic = iscrizioneElenchiRic.getIscrittoAnagrafeAntimafiaEsecutori();
      }
      msgTmp = this.controlloVariazioneCampiSN(datiImpr, iscrittoElenchiRic, "IMPR.ISCRIAE", "Il campo iscritto a anagrafe antimaf.esecut.(art.30 c.6 DL 189/2016)? è cambiato");
      if(!"".equals(msgTmp)){
        if (!"".equals(msg)) msg += "\n";
        msg += msgTmp;
      }

      // Data scadenza iscrizione anagrafe antimafia esecutori
      Calendar dataScadenzaIscrizioneAntimafia = iscrizioneElenchiRic.getDataScadenza();
      msgTmp= this.controlloVariazioneCampiData(datiImpr, dataScadenzaIscrizioneAntimafia, "IMPR.AEDSCAD", "La data scadenza iscrizione anagrafe antimafia esecutori è cambiata");
      if(!"".equals(msgTmp)){
        if (!"".equals(msg)) msg += "\n";
        msg += msgTmp;
      }

      //Rinnovo iscrizione in corso anagrafe antimafia esecutori?
      String rinnovoIscriz = null;
      if (iscrizioneElenchiRic.isSetRinnovoIscrizioneInCorso()) {
        rinnovoIscriz = iscrizioneElenchiRic.getRinnovoIscrizioneInCorso();
      }
      msgTmp = this.controlloVariazioneCampiSN(datiImpr, rinnovoIscriz, "IMPR.AEINCORSO", "Il campo rinnovo iscrizione in corso anagrafe antimafia esecutori? è cambiato");
      if(!"".equals(msgTmp)){
        if (!"".equals(msg)) msg += "\n";
        msg += msgTmp;
      }

      //Iscritto a elenco speciale profession.(art.34 DL 189/2016)?
      String iscrittoElencoSpeciale =  null;
      if (iscrizioneElenchiRic.isSetIscrittoElencoSpecialeProfessionisti()) {
        iscrittoElencoSpeciale = iscrizioneElenchiRic.getIscrittoElencoSpecialeProfessionisti();
      }
      msgTmp = this.controlloVariazioneCampiSN(datiImpr, iscrittoElencoSpeciale, "IMPR.ISCRIESP", "Il campo iscritto a elenco speciale profession.(art.34 DL 189/2016)? è cambiato");
      if(!"".equals(msgTmp)){
        if (!"".equals(msg)) msg += "\n";
        msg += msgTmp;
      }

    }
    /////////////////////////////////////////////////

    ////////////////////////////////
    //Rating di legalità (DL 1/2012)
    RatingLegalitaType  ratingLegalita = impresa.getRatingLegalita();
    if (iscrizioneElenchiRic != null){
      //Possiede rating di legalità?
      String possiedeRating = null;
      if (ratingLegalita.isSetPossiedeRating()) {
        possiedeRating = ratingLegalita.getPossiedeRating();
      }
      msgTmp = this.controlloVariazioneCampiSN(datiImpr, possiedeRating, "IMPR.ISCRIRAT", "Il campo possiede rating di legalità? è cambiato");
      if(!"".equals(msgTmp)){
        if (!"".equals(msg)) msg += "\n";
        msg += msgTmp;
      }

      //Rating di legalità
      String rating = ratingLegalita.getRating();
      msgTmp= this.controlloVariazioneCampiTabellati(datiImpr, rating, "IMPR.RATING", "Il campo rating di legalità è cambiato","G_069");
      if(!"".equals(msgTmp)){
        if (!"".equals(msg)) msg += "\n";
        msg += msgTmp;
      }


      //Data scadenza possesso rating di legalità
      Calendar dataScadenzaRating = ratingLegalita.getDataScadenza();
      msgTmp= this.controlloVariazioneCampiData(datiImpr, dataScadenzaRating, "IMPR.RATDSCAD", "La data scadenza possesso rating di legalità è cambiata");
      if(!"".equals(msgTmp)){
        if (!"".equals(msg)) msg += "\n";
        msg += msgTmp;
      }

      //Aggiornamento rating di legalità in corso?
      String aggiornamentoInCorso = null;
      if (ratingLegalita.isSetAggiornamentoRatingInCorso()) {
        aggiornamentoInCorso = ratingLegalita.getAggiornamentoRatingInCorso();
      }
      msgTmp = this.controlloVariazioneCampiSN(datiImpr, aggiornamentoInCorso, "IMPR.RATINCORSO", "Il campo aggiornamento rating di legalità in corso? è cambiato");
      if(!"".equals(msgTmp)){
        if (!"".equals(msg)) msg += "\n";
        msg += msgTmp;
      }

    }
    /////////////////////////////////////////////////


    ////////////////////////////////
    //Classe di dimensione
    String classeDimensioneString = impresa.getClasseDimensione();
    Long classeDimensione = null;
    if (classeDimensioneString != null && !"".equals(classeDimensioneString))
      classeDimensione = new Long(classeDimensioneString);
    else
      classeDimensioneString = "";
    datiImpr.setValue("IMPR.CLADIM", classeDimensione);
    if (datiImpr.isModifiedColumn("IMPR.CLADIM")) {
      String descOriginale = "";
      if (datiImpr.getColumn("IMPR.CLADIM").getOriginalValue().longValue() != null) {
        Long tmp = datiImpr.getColumn("IMPR.CLADIM").getOriginalValue().longValue();
        descOriginale = tabellatiManager.getDescrTabellato("G_062",
            tmp.toString());
      }
      if (!"".equals(classeDimensioneString))
        classeDimensioneString = tabellatiManager.getDescrTabellato("G_062",
            classeDimensioneString);

      if (!"".equals(msg)) msg += "\n";

      msg += "La classe di dimensione é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + descOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + classeDimensioneString
          + "\n";
    }
    ////////////////////////////////

    ////////////////////////////////
    //Settore attività economica
    datiImpr.setValue("IMPR.CODATT", impresa.getSettoreAttivitaEconomica());
    if (datiImpr.isModifiedColumn("IMPR.CODATT")) {
      String settoreAttivitaEconomicaOriginale = "";
      String descOriginale = "";
      String originalValue = datiImpr.getColumn("IMPR.CODATT").getOriginalValue().getStringValue();
      //JdbcParametro originalValue = datiImpr.getColumn("IMPR.CODATT").getOriginalValue();
      if (originalValue != null && originalValue != ""){
        settoreAttivitaEconomicaOriginale = datiImpr.getColumn("IMPR.CODATT").getOriginalValue().getStringValue();
        descOriginale = tabellatiManager.getDescrTabellato("G_j07",settoreAttivitaEconomicaOriginale);
      }
      String settoreAttivitaEconomica = "";
      String currentValue = impresa.getSettoreAttivitaEconomica();
      //if (impresa.getSettoreAttivitaEconomica() != null){
      if (currentValue != null && currentValue != ""){
        settoreAttivitaEconomica = impresa.getSettoreAttivitaEconomica();
        settoreAttivitaEconomica = tabellatiManager.getDescrTabellato("G_j07",settoreAttivitaEconomica);
      }
      if (!"".equals(msg)) msg += "\n";

      msg += "Il settore attività economica é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + descOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + settoreAttivitaEconomica
          + "\n";
    }
    ////////////////////////////////

    // Conto Corrente dedicato
    ContoCorrenteDedicatoType ContoCorrenteDedicato = impresa.getContoCorrente();
    datiImpr.setValue("IMPR.COORBA", ContoCorrenteDedicato.getEstremi());
    if (datiImpr.isModifiedColumn("IMPR.COORBA")) {
      String estremiOriginale = "";
      if (datiImpr.getColumn("IMPR.COORBA").getOriginalValue() != null)
        estremiOriginale = datiImpr.getColumn("IMPR.COORBA").getOriginalValue().getStringValue();
      String estremi = "";
      if (ContoCorrenteDedicato.getEstremi() != null)
        estremi = ContoCorrenteDedicato.getEstremi();
      if (!"".equals(msg)) msg += "\n";

      msg += "Il codice IBAN del conto corrente dedicato e' cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + estremiOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + estremi
          + "\n";
    }

    //Codice BIC
    datiImpr.setValue("IMPR.CODBIC", ContoCorrenteDedicato.getBic());
    if (datiImpr.isModifiedColumn("IMPR.CODBIC")) {
      String bicOriginale = "";
      if (datiImpr.getColumn("IMPR.CODBIC").getOriginalValue() != null)
        bicOriginale = datiImpr.getColumn("IMPR.CODBIC").getOriginalValue().getStringValue();
      String bic = "";
      if (ContoCorrenteDedicato.getBic() != null)
        bic = ContoCorrenteDedicato.getBic();
      if (!"".equals(msg)) msg += "\n";

      msg += "Il codice BIC del conto corrente dedicato e' cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + bicOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + bic
          + "\n";
    }

    datiImpr.setValue("IMPR.SOGMOV",
        ContoCorrenteDedicato.getSoggettiAbilitati());
    if (datiImpr.isModifiedColumn("IMPR.SOGMOV")) {
      String soggettiOriginale = "";
      if (datiImpr.getColumn("IMPR.SOGMOV").getOriginalValue() != null)
        soggettiOriginale = datiImpr.getColumn("IMPR.SOGMOV").getOriginalValue().getStringValue();
      String soggetti = "";
      if (ContoCorrenteDedicato.getSoggettiAbilitati() != null)
        soggetti = ContoCorrenteDedicato.getSoggettiAbilitati();
      if (!"".equals(msg)) msg += "\n";

      msg += "I soggetti abilitati a movimentazioni del conto corrente dedicato sono cambiati \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + soggettiOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + soggetti
          + "\n";
    }

    // Socio Unico
    String socioUnico = "";
    if (impresa.isSetSocioUnico()) {
      socioUnico = impresa.getSocioUnico();
      if ("0".equals(socioUnico))socioUnico = "2";
    }
    datiImpr.setValue("IMPR.SOCIOUNICO", socioUnico);
    if (datiImpr.isModifiedColumn("IMPR.SOCIOUNICO")) {
      String socioUnicoOriginale = "";
      if (datiImpr.getColumn("IMPR.SOCIOUNICO").getOriginalValue() != null)
        socioUnicoOriginale = datiImpr.getColumn("IMPR.SOCIOUNICO").getOriginalValue().getStringValue();

      if ("1".equals(socioUnico) )
        socioUnico = "Si";
      else if ("2".equals(socioUnico) )
        socioUnico = "No";
      else
        socioUnico = "";


      if ("1".equals(socioUnicoOriginale) )
        socioUnicoOriginale = "Si";
      else if ("2".equals(socioUnicoOriginale) )
        socioUnicoOriginale = "No";
      else
        socioUnicoOriginale = "";

      if (!"".equals(msg)) msg += "\n";

      msg += "Il campo socio unico? é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + socioUnicoOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + socioUnico
          + "\n";
    }

    String regimeFiscale = impresa.getRegimeFiscale();
    Long regFisc = null;
    if (regimeFiscale != null && !"".equals(regimeFiscale))
      regFisc = new Long(regimeFiscale);
    else
    	regimeFiscale = "";
    datiImpr.setValue("IMPR.REGFISC", regFisc);
    if (datiImpr.isModifiedColumn("IMPR.REGFISC")) {
      String descOriginale = "";
      Long tmp = datiImpr.getColumn("IMPR.REGFISC").getOriginalValue().longValue();
      if (tmp != null)
        descOriginale = tabellatiManager.getDescrTabellato("G_070",
            tmp.toString());

      if (!"".equals(regimeFiscale))
    	  regimeFiscale = tabellatiManager.getDescrTabellato("G_070",
    			  regimeFiscale);

      if (!"".equals(msg)) msg += "\n";

      msg += "Il regime fiscale é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + descOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + regimeFiscale
          + "\n";

    }

    // Abilitazione preventiva
    AbilitazionePreventivaType abilitazione = impresa.getAbilitazionePreventiva();
    Calendar dataScadenzaRinnovo = abilitazione.getDataScadenzaRinnovo();
    campoData = null;
    if (dataScadenzaRinnovo != null) campoData = dataScadenzaRinnovo.getTime();
    datiImpr.setValue("IMPR.DSCNOS", campoData);
    if (datiImpr.isModifiedColumn("IMPR.DSCNOS")) {
      String dscnosOriginale = "";
      if (datiImpr.getColumn("IMPR.DSCNOS").getOriginalValue().dataValue() != null) {
        dscnosOriginale = UtilityDate.convertiData(
            new Date(
                datiImpr.getColumn("IMPR.DSCNOS").getOriginalValue().dataValue().getTime()),
            UtilityDate.FORMATO_GG_MM_AAAA);
      }

      String dscnos = "";
      if (dataScadenzaRinnovo != null)
        dscnos = UtilityDate.convertiData(dataScadenzaRinnovo.getTime(),
            UtilityDate.FORMATO_GG_MM_AAAA);
      if (!"".equals(msg)) msg += "\n";

      msg += "La data scadenza abilitazione preventiva é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dscnosOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + dscnos
          + "\n";
    }

    String faseRinnovo = null;
    if (abilitazione.isSetFaseRinnovo()) {
      faseRinnovo = abilitazione.getFaseRinnovo();
      datiImpr.setValue("IMPR.RINNOS", faseRinnovo);
      if (datiImpr.isModifiedColumn("IMPR.RINNOS")) {
        String faseRinnoviOriginale = datiImpr.getColumn("IMPR.RINNOS").getOriginalValue().getStringValue();

        if ("1".equals(faseRinnoviOriginale))
          faseRinnoviOriginale = "Si";
        else if ("2".equals(faseRinnoviOriginale))
          faseRinnoviOriginale = "No";
        else
          faseRinnoviOriginale = "";

        if ("1".equals(faseRinnovo))
          faseRinnovo = "Si";
        else if ("2".equals(faseRinnovo))
          faseRinnovo = "No";
        else
          faseRinnovo = "";

        if (!faseRinnoviOriginale.equals(faseRinnovo)) {
          if (!"".equals(msg)) msg += "\n";

          msg += "In fase di rinnovo é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + faseRinnoviOriginale
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + faseRinnovo
              + "\n";
        }
      }
    }

    Calendar dataRichiestaRinnovo = abilitazione.getDataRichiestaRinnovo();
    campoData = null;
    if (dataRichiestaRinnovo != null)
      campoData = dataRichiestaRinnovo.getTime();
    datiImpr.setValue("IMPR.DRINOS", campoData);
    if (datiImpr.isModifiedColumn("IMPR.DRINOS")) {
      String drinosOriginale = "";
      if (datiImpr.getColumn("IMPR.DRINOS").getOriginalValue().dataValue() != null) {
        drinosOriginale = UtilityDate.convertiData(
            new Date(
                datiImpr.getColumn("IMPR.DRINOS").getOriginalValue().dataValue().getTime()),
            UtilityDate.FORMATO_GG_MM_AAAA);
      }

      String drinos = "";
      if (dataRichiestaRinnovo != null)
        drinos = UtilityDate.convertiData(dataRichiestaRinnovo.getTime(),
            UtilityDate.FORMATO_GG_MM_AAAA);
      if (!"".equals(msg)) msg += "\n";

      msg += "La data richiesta rinnovo abilitazione preventiva é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + drinosOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + drinos
          + "\n";
    }

    String zoneAttivita = impresa.getZoneAttivita();
    datiImpr.setValue("IMPR.ZONEAT", zoneAttivita);
    if (datiImpr.isModifiedColumn("IMPR.ZONEAT")) {
      String listaRegioniOriginale = "";
      String zoneatOriginale = datiImpr.getColumn("IMPR.ZONEAT").getOriginalValue().getStringValue();
      if (zoneatOriginale != null && !"".equals(zoneatOriginale))
        listaRegioniOriginale = this.listaRegioni(zoneatOriginale);

      String listaRegioni = "";
      if (zoneAttivita != null && !"".equals(zoneAttivita))
        listaRegioni = this.listaRegioni(zoneAttivita);

      if (!"".equals(msg)) msg += "\n";

      msg += "Le zone di attivita' sono cambiate \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + listaRegioniOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + listaRegioni
          + "\n";
    }

    datiImpr.setValue("IMPR.ACERTATT", impresa.getAltreCertificazioniAttestazioni());
    if (datiImpr.isModifiedColumn("IMPR.ACERTATT")) {
      String altreCertifOriginale = "";
      if (datiImpr.getColumn("IMPR.ACERTATT").getOriginalValue() != null)
        altreCertifOriginale = datiImpr.getColumn("IMPR.ACERTATT").getOriginalValue().getStringValue();
      String altreCertif = "";
      if (impresa.getAltreCertificazioniAttestazioni() != null)
        altreCertif = impresa.getAltreCertificazioniAttestazioni();
      if (!"".equals(msg)) msg += "\n";

      msg += "Le altre certificazioni o attestazioni possedute sono cambiate\nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + altreCertifOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + altreCertif
          + "\n";
    }

    datiImpr.setValue("IMPR.ULTDIC", impresa.getUlterioriDichiarazioni());
    if (datiImpr.isModifiedColumn("IMPR.ULTDIC")) {
      String ultDicOriginale = "";
      if (datiImpr.getColumn("IMPR.ULTDIC").getOriginalValue() != null)
        ultDicOriginale = datiImpr.getColumn("IMPR.ULTDIC").getOriginalValue().getStringValue();
      String ultDic = "";
      if (impresa.getUlterioriDichiarazioni() != null)
        ultDic = impresa.getUlterioriDichiarazioni();
      if (!"".equals(msg)) msg += "\n";

      msg += "Le ulteriori dichiarazioni dell'operatore sono cambiate\nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + ultDicOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + ultDic
          + "\n";
    }

    datiImpr.setValue("IMPR.AISTPREV", impresa.getAltriIstitutiPrevidenziali());
    if (datiImpr.isModifiedColumn("IMPR.AISTPREV")) {
      String altriIstitutiOriginale = "";
      if (datiImpr.getColumn("IMPR.AISTPREV").getOriginalValue() != null)
        altriIstitutiOriginale = datiImpr.getColumn("IMPR.AISTPREV").getOriginalValue().getStringValue();
      String altriIstituti = "";
      if (impresa.getAltriIstitutiPrevidenziali() != null)
        altriIstituti = impresa.getAltriIstitutiPrevidenziali();
      if (!"".equals(msg)) msg += "\n";

      msg += "Le iscrizioni ad altri istituti previdenziali sono cambiate\nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + altriIstitutiOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + altriIstituti
          + "\n";
    }

    String assObbligate = null;
    if (impresa.isSetAssunzioniObbligate()) {
      assObbligate = impresa.getAssunzioniObbligate();
      if ("0".equals(assObbligate))
        assObbligate =  "2";
    }
    datiImpr.setValue("IMPR.ASSOBBL", assObbligate);
    if (datiImpr.isModifiedColumn("IMPR.ASSOBBL")) {
      String assObbligateOriginale = "";
      if (datiImpr.getColumn("IMPR.ASSOBBL").getOriginalValue() != null)
        assObbligateOriginale = datiImpr.getColumn("IMPR.ASSOBBL").getOriginalValue().getStringValue();

      if ("1".equals(assObbligate) )
        assObbligate = "Si";
      else if ("2".equals(assObbligate) )
        assObbligate = "No";
      else
        assObbligate = "";

      if ("1".equals(assObbligateOriginale) )
        assObbligateOriginale = "Si";
      else if ("2".equals(assObbligateOriginale) )
        assObbligateOriginale = "No";
      else
        assObbligateOriginale = "";

      if (!"".equals(msg)) msg += "\n";

      msg += "Il campo soggetto alla Legge 68/1999 (Assunzioni obbligate)? é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + assObbligateOriginale
          + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
          + assObbligate
          + "\n";
    }

    // Inserimento dei dipendenti per anno passati da portale
    DatoAnnuoImpresaType datiAnnui[] = impresa.getDatoAnnuoArray();
    if (datiAnnui.length > 0) {
      try{
      for (int i = 0; i < datiAnnui.length; i++) {
        int anno = datiAnnui[i].getAnno();
        Long dipendenti = null;
        if (datiAnnui[i].isSetDipendenti())
          dipendenti = new Long(datiAnnui[i].getDipendenti());
        Long conteggio = (Long)sqlManager.getObject("select count(codimp) from impanno where codimp=? and anno=?",
            new Object[]{codiceImpresa, new Long(anno)});
        if (conteggio!= null && conteggio.longValue()>0) {
          Long numdipOriginale = (Long)sqlManager.getObject("select numdip from IMPANNO where CODIMP =? and ANNO = ?",
              new Object[]{codiceImpresa, new Long(anno)});

          if ((numdipOriginale == null && dipendenti != null)  || (numdipOriginale != null && dipendenti == null) || (numdipOriginale != null && dipendenti != null && numdipOriginale.intValue()!= dipendenti.intValue())) {
            if (!"".equals(msg)) msg += "\n";
            String numdipOriginaleString="";
            if (numdipOriginale != null)
              numdipOriginaleString = numdipOriginale.toString();

            String numdipString="";
            if (dipendenti != null)
              numdipString = dipendenti.toString();

            msg += "Il numero di dipendenti per l'anno " + anno + "  é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                + numdipOriginaleString
                + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                + numdipString
                + "\n";
          }

        } else {
          if (!"".equals(msg)) msg += "\n";
          if (dipendenti != null)
            msg += "Sono stati definiti " + dipendenti + " dipendenti per l'anno " + anno +"\n";
          else
            msg += "Non sono stati definiti dipendenti per l'anno " + anno +"\n";
        }
      }
      }catch (SQLException e) {
        throw new GestoreException(
            "Errore nella lettura dei dati di IMPANNO per l'impresa " + codiceImpresa, null, e);
      }
    }
    return msg;
  }

  /**
   * Viene effettuato un confronto fra i dati in db e quelli provenienti dal
   * Portale per il referente. Per adesso vengono prese in considerazione solo
   * le variazioni rispetto alle righe di IMPLEG e IMPDTE, non si analizzano le
   * variazioni a livello di anagrafica del tecnico in TEIM Se vi sono
   * differenze nei dati viene costruito un messaggio informativo
   *
   * @param document
   * @param codiceImpresa
   * @param tipoReferente
   *        <ul>
   *        <li>LEGALE - Legale rappresentante</li>
   *        <li>DIRETTORE - Direttore tecnico</li>
   *        <li>AZIONISTA - Azionista</li>
   *        </ul>
   * @return String
   *
   * @throws GestoreException
   */
  public String controlloDatiReferenti(Object document, String codiceImpresa,
      String tipoReferente) throws GestoreException {
    String msg = "";
    String cognome = "";
    String nome = "";
    String codfisc = "";
    String select = "";
    String parametro = "";
    String codiceReferente = "";
    String referente = "";
    boolean effettuareControllo = false;
    Calendar dataFineIncarico = null;
    String note = "";
    Long qualifica = null;
    String qualificaString = "";
    String respDichiarazioni ="";

    ReferenteImpresaType referenti[] = null;
    if ("LEGALE".equals(tipoReferente)) {
      if (document instanceof RegistrazioneImpresaDocument)
        referenti = ((RegistrazioneImpresaDocument) document).getRegistrazioneImpresa().getDatiImpresa().getLegaleRappresentanteArray();
      else if (document instanceof IscrizioneImpresaElencoOperatoriDocument)
        referenti = ((IscrizioneImpresaElencoOperatoriDocument) document).getIscrizioneImpresaElencoOperatori().getDatiImpresa().getLegaleRappresentanteArray();
      else if (document instanceof AggiornamentoIscrizioneImpresaElencoOperatoriDocument)
        referenti = ((AggiornamentoIscrizioneImpresaElencoOperatoriDocument) document).getAggiornamentoIscrizioneImpresaElencoOperatori().getDatiImpresa().getLegaleRappresentanteArray();
      else
        referenti = ((AggiornamentoAnagraficaImpresaDocument) document).getAggiornamentoAnagraficaImpresa().getDatiImpresa().getLegaleRappresentanteArray();

      effettuareControllo = true;
      referente = "Legale rappresentante";
    } else if ("DIRETTORE".equals(tipoReferente)) {
      if (document instanceof RegistrazioneImpresaDocument)
        referenti = ((RegistrazioneImpresaDocument) document).getRegistrazioneImpresa().getDatiImpresa().getDirettoreTecnicoArray();
      else if (document instanceof IscrizioneImpresaElencoOperatoriDocument)
        referenti = ((IscrizioneImpresaElencoOperatoriDocument) document).getIscrizioneImpresaElencoOperatori().getDatiImpresa().getDirettoreTecnicoArray();
      else if (document instanceof AggiornamentoIscrizioneImpresaElencoOperatoriDocument)
        referenti = ((AggiornamentoIscrizioneImpresaElencoOperatoriDocument) document).getAggiornamentoIscrizioneImpresaElencoOperatori().getDatiImpresa().getDirettoreTecnicoArray();
      else
        referenti = ((AggiornamentoAnagraficaImpresaDocument) document).getAggiornamentoAnagraficaImpresa().getDatiImpresa().getDirettoreTecnicoArray();
      if (referenti != null && referenti.length > 0) {
        effettuareControllo = true;
        referente = "Direttore tecnico";
      }
    } else if ("COLLABORATORE".equals(tipoReferente)) {
      if (document instanceof RegistrazioneImpresaDocument)
        referenti = ((RegistrazioneImpresaDocument) document).getRegistrazioneImpresa().getDatiImpresa().getCollaboratoreArray();
      else if (document instanceof IscrizioneImpresaElencoOperatoriDocument)
        referenti = ((IscrizioneImpresaElencoOperatoriDocument) document).getIscrizioneImpresaElencoOperatori().getDatiImpresa().getCollaboratoreArray();
      else if (document instanceof AggiornamentoIscrizioneImpresaElencoOperatoriDocument)
        referenti = ((AggiornamentoIscrizioneImpresaElencoOperatoriDocument) document).getAggiornamentoIscrizioneImpresaElencoOperatori().getDatiImpresa().getCollaboratoreArray();
      else
        referenti = ((AggiornamentoAnagraficaImpresaDocument) document).getAggiornamentoAnagraficaImpresa().getDatiImpresa().getCollaboratoreArray();
      if (referenti != null && referenti.length > 0) {
        effettuareControllo = true;
        referente = "Collaboratore";
      }
    } else if ("ALTRECARICHE".equals(tipoReferente)) {
      if (document instanceof RegistrazioneImpresaDocument)
        referenti = ((RegistrazioneImpresaDocument) document).getRegistrazioneImpresa().getDatiImpresa().getAltraCaricaArray();
      else if (document instanceof IscrizioneImpresaElencoOperatoriDocument)
        referenti = ((IscrizioneImpresaElencoOperatoriDocument) document).getIscrizioneImpresaElencoOperatori().getDatiImpresa().getAltraCaricaArray();
      else if (document instanceof AggiornamentoIscrizioneImpresaElencoOperatoriDocument)
        referenti = ((AggiornamentoIscrizioneImpresaElencoOperatoriDocument) document).getAggiornamentoIscrizioneImpresaElencoOperatori().getDatiImpresa().getAltraCaricaArray();
      else
        referenti = ((AggiornamentoAnagraficaImpresaDocument) document).getAggiornamentoAnagraficaImpresa().getDatiImpresa().getAltraCaricaArray();
      if (referenti != null && referenti.length > 0) {
        effettuareControllo = true;
        referente = "Soggetto con altra carica o qualifica";
      }
    } /*else {
      referenti = ((AggiornamentoAnagraficaImpresaDocument) document).getAggiornamentoAnagraficaImpresa().getDatiImpresa().getAzionistaArray();
      if (referenti != null && referenti.length > 0) {
        effettuareControllo = true;
        referente = "Azionista";
      }
    }
    */

    if (effettuareControllo) {
      boolean solaLettura = false;
      for (int i = 0; i < referenti.length; i++) {
        solaLettura = referenti[i].getSolaLettura();
        if (!solaLettura) {
          cognome = referenti[i].getCognome();
          nome = referenti[i].getNome();
          codfisc = referenti[i].getCodiceFiscale();
          dataFineIncarico = referenti[i].getDataFineIncarico();
          note = referenti[i].getNote();
          qualificaString = referenti[i].getQualifica();
          if (!"COLLABORATORE".equals(tipoReferente)) {
            //respDic = referenti[i].getResponsabileDichiarazioni();
            //respDichiarazioni = (respDic ? "1" : "2");
            respDichiarazioni = referenti[i].getResponsabileDichiarazioni();
            if ("0".equals(respDichiarazioni))
              respDichiarazioni="2";
          }

          if (note == null) note = "";
          if (respDichiarazioni == null) respDichiarazioni = "";

          // Tramite il codice fiscale determino se esiste il codice del tecnico
          select = "select codtim from teim where upper(cftim) = ? order by codtim";
          parametro = codfisc.toUpperCase();

          try {

            codiceReferente = (String) this.sqlManager.getObject(select,
                new Object[] { parametro });
            String nomCogn = "" + cognome + " " + nome;

            if (codiceReferente == null || "".equals(codiceReferente)) {
              msg += "\nVerra' inserito il nuovo "
                  + referente
                  + " "
                  + nomCogn
                  + "\n";
              // return msg;
              continue;
            }

            //Modifiche per cambiamento chiave entità IMPLEG e IMPDTE
            if ("LEGALE".equals(tipoReferente)) {
              select = "select CODLEG, NOMLEG,LEGFIN,NOTLEG,RESPDICH from IMPLEG where CODIMP2 = ? and LEGFIN is null";

            //Modifiche per cambiamento chiave entità IMPLEG e IMPDTE
            } else if ("DIRETTORE".equals(tipoReferente)) {
              select = "select CODDTE, NOMDTE,DIRFIN,NOTDTE,RESPDICH from IMPDTE where CODIMP3 = ? and DIRFIN is null";

            } else if ("COLLABORATORE".equals(tipoReferente)) {
              select = "select CODTEC, NOMTEC,INCFIN,NOTCOL from G_IMPCOL where CODIMP = ? and INCFIN is null and INCTIP = ?";
              if (qualificaString != null && !"".equals(qualificaString))
                qualifica = new Long(qualificaString);
            } else {
              select = "select CODTEC, NOMTEC,FINAZI,NOTAZI,RESPDICH from IMPAZI where CODIMP4 = ? and FINAZI is null and INCAZI = ?";
              if (qualificaString != null && !"".equals(qualificaString))
                qualifica = new Long(qualificaString);
            }

            List<?> listaDatiReferenti = null;
            if ("LEGALE".equals(tipoReferente)
                || "DIRETTORE".equals(tipoReferente))
              listaDatiReferenti = this.sqlManager.getListVector(select,
                  new Object[] { codiceImpresa });
            else
              listaDatiReferenti = this.sqlManager.getListVector(select, new Object[] {
                  codiceImpresa, qualifica });
            if (listaDatiReferenti != null && listaDatiReferenti.size() > 0) {
              boolean referentePresente = false;
              for (int j = 0; j < listaDatiReferenti.size(); j++) {
                // Assumo che per i tecnici non si cambi il tecnico
                // dell'impresa(ossi non cambia il codice tecnico)
                Vector<?> tmp = (Vector<?>) listaDatiReferenti.get(j);
                String codiceReferenteOriginale = ((JdbcParametro) tmp.get(0)).getStringValue();
                String nomCognOriginale = ((JdbcParametro) tmp.get(1)).getStringValue();
                Timestamp DataFine = ((JdbcParametro) tmp.get(2)).dataValue();
                String noteOriginale = ((JdbcParametro) tmp.get(3)).getStringValue();
                String responsabileDicOriginale = "";

                if (!"COLLABORATORE".equals(tipoReferente))
                  responsabileDicOriginale = ((JdbcParametro) tmp.get(4)).getStringValue();

                if (codiceReferenteOriginale == null)
                  codiceReferenteOriginale = "";

                if (nomCognOriginale == null) nomCognOriginale = "";

                if (noteOriginale == null) noteOriginale = "";

                if (responsabileDicOriginale == null) responsabileDicOriginale = "";

                if (codiceReferenteOriginale.equals(codiceReferente)) {
                  referentePresente = true;

                  /*
                  // Nel caso di direttore e legale rappresentante si verifica se
                  // il referente è in carica
                  // oppure no. In quest'ultimo caso si scrive nel log un
                  // opportuno messaggio.
                  if (DataFine != null
                      && ("LEGALE".equals(tipoReferente) || "DIRETTORE".equals(tipoReferente))) {
                    msg += "\nIl "
                        + referente
                        + " "
                        + nomCognOriginale
                        + " con codice "
                        + codiceReferente
                        + " è già inserito ma non è in carica, quindi verrà ripristinato sbiancando la data di fine incarico\n";
                  }
                  */

                  if (!nomCognOriginale.equals(nomCogn) && effettuareControllo) {

                    // if (!"".equals(msg))
                    // msg +="\n";
                    String msg1 = "\nIl nome del ";
                    if ("Azionista".equals(referente)) msg1 = "\nIl nome dell'";
                    msg += msg1
                        + referente
                        + " é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + nomCognOriginale
                        + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + nomCogn
                        + "\n";
                  }

                  // Controllo sulla data fine incarico
                  String dataFin = "";
                  if (dataFineIncarico != null)
                    dataFin = UtilityDate.convertiData(
                        dataFineIncarico.getTime(),
                        UtilityDate.FORMATO_GG_MM_AAAA);

                  String dataFinOriginale = "";
                  if (DataFine != null)
                    dataFinOriginale = UtilityDate.convertiData(
                        new Date(DataFine.getTime()),
                        UtilityDate.FORMATO_GG_MM_AAAA);

                  if (!dataFinOriginale.equals(dataFin)) {
                    msg += "\nLa data fine incarico del";
                    if ("Azionista".equals(referente)) msg += "l'";
                    msg += " "
                        + referente
                        + " "
                        + nomCogn
                        + " é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + dataFinOriginale
                        + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + dataFin
                        + "\n";
                  }

                  // Controllo sulle note
                  if (!noteOriginale.equals(note) && effettuareControllo) {

                    // if (!"".equals(msg))
                    // msg +="\n";
                    String msg1 = "\nLe note del ";
                    if ("Azionista".equals(referente)) msg1 = "\nLe note dell'";
                    msg += msg1
                        + referente
                        + " "
                        + nomCogn
                        + " sono cambiate \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + noteOriginale
                        + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + note
                        + "\n";
                  }

                  // Controllo sul campo responsabile delle dichiarazioni
                  if (!"COLLABORATORE".equals(tipoReferente) && !responsabileDicOriginale.equals(respDichiarazioni) && effettuareControllo) {
                    if ("1".equals(respDichiarazioni))
                      respDichiarazioni = "Si";
                    else if ("2".equals(respDichiarazioni))
                      respDichiarazioni = "No";

                    if ("1".equals(responsabileDicOriginale))
                      responsabileDicOriginale = "Si";
                    else if ("2".equals(responsabileDicOriginale))
                      responsabileDicOriginale = "No";
                    else
                      responsabileDicOriginale = "";

                    String msg1 = "\nIl campo responsabile delle dichiarazioni ";
                    msg += msg1
                        + referente
                        + " "
                        + nomCogn
                        + " è cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + responsabileDicOriginale
                        + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + respDichiarazioni
                        + "\n";
                  }

                  break;

                }

              }
              if (!referentePresente) {
                msg += "\nVerra' inserito il nuovo "
                    + referente
                    + " "
                    + nomCogn
                    + " con codice "
                    + codiceReferente
                    + "\n";
              }

            } else {
              msg += "\nVerra' inserito il nuovo "
                  + referente
                  + " "
                  + nomCogn
                  + " con codice "
                  + codiceReferente
                  + "\n";
            }

            msg += this.controlloDatiTeim(referenti[i]);

          } catch (SQLException e) {
            throw new GestoreException(
                "Errore nella lettura dei dati del tecnico", null, e);
          }
        }
      }
    }

    return msg;
  }

  /**
   * A partire dalla stringa ZONEAT viene ricostruita la lista delle regioni
   *
   * @param zoneat
   */
  public String listaRegioni(String zoneatt) {
    String listaRegioni = "";

    String regioni[] = { "Piemonte", "Valle d'Aosta", "Liguria", "Lombardia",
        "Friuli Venezia Giulia", "Trentino Alto Adige", "Veneto",
        "Emilia Romagna", "Toscana", "Umbria", "Marche", "Abruzzo", "Molise",
        "Lazio", "Campania", "Basilicata", "Puglia", "Calabria", "Sardegna",
        "Sicilia" };

    if ("11111111111111111111".equals(zoneatt)) {
      listaRegioni = "Tutte le regioni";
    } else {
      for (int i = 0; i < 20; i++) {
        if (zoneatt.charAt(i) == '1') {
          if (listaRegioni != "") listaRegioni += ", ";
          listaRegioni += regioni[i];
        }
      }
    }

    return listaRegioni;
  }

  /**
   * Determina l'importo di iscrizione associato al numero di categoria.
   *
   * @param tipoCategoria
   *        Tipo di categoria: 1=Lavori, 2=Forniture, 3=Servizi
   * @param numeroClassifica
   * @return Ritorna l'importo d'iscrizione associato al numero di categoria
   * @throws GestoreException
   */
  public Double getImportoIscrizioneCategoria(int tipoCategoria,
      Long numeroClassifica) throws GestoreException {
    String codiceTabellato = null;
    switch (tipoCategoria) {
    case 1:
      codiceTabellato = "G_z09";
      break;
    case 2:
      codiceTabellato = "G_z07";
      break;
    case 3:
      codiceTabellato = "G_z08";
      break;
    case 4:
      codiceTabellato = "G_z11";
      break;
    case 5:
      codiceTabellato = "G_z12";
      break;
    }

    String importoIscrizione = null;
    try {
      importoIscrizione = (String) this.geneManager.getSql().getObject(
          "select tab2d1 from tab2 where tab2cod = ? and tab2tip = ?",
          new Object[] { codiceTabellato, numeroClassifica.toString() });
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nel determinare l'importo d'iscrizione della categoria dell'appalto",
          null, e);
    }
    return UtilityNumeri.convertiDouble(importoIscrizione);
  }

  /**
   * Viene prelevata la provincia a partire dal codice ISTAT
   *
   * @param siglaProvincia
   * @throws GestoreException
   * @return String
   */
  public String getProvinciaDaSigla(String siglaProvincia)
      throws GestoreException {
    String provincia = null;

    String select = "select tabdesc from tabsche where tabsche.tabcod='S2003' and tabsche.tabcod1='07' ";
    select += "and upper(tabsche.tabcod3)=?";
    try {
      provincia = (String) this.sqlManager.getObject(select,
          new Object[] { siglaProvincia });
    } catch (SQLException e) {
      throw new GestoreException("Errore nella determinazione della provincia",
          null, e);
    }

    return provincia;
  }

  /**
   * Viene calcolata la somma dei punteggi di tipo "tipologia" per la gara
   * "ngara" escludendo l'occorrenza con necvan specificato
   *
   * @param ngara
   * @param tipologia
   * @param necvan
   * @throws SQLException
   */
  public Double getSommaPunteggio(String ngara, Long tipologia, Long necvan)
      throws SQLException {
    Double result = null;
    double sommaPunteggi = 0;
    boolean punteggioImpostato = false;

    List<?> listaMaxpunEconomico = this.sqlManager.getListHashMap(
        "select MAXPUN from GOEV where NGARA = ? "
            + "and (LIVPAR = 1 or LIVPAR = 3) and TIPPAR = ? and NECVAN <> ?",
        new Object[] { ngara, tipologia, necvan });
    if (listaMaxpunEconomico != null && listaMaxpunEconomico.size() > 0) {
      for (int i = 0; i < listaMaxpunEconomico.size(); i++) {
        Double tmp = (Double) ((JdbcParametro) ((HashMap<?,?>) listaMaxpunEconomico.get(i)).get("MAXPUN")).getValue();
        if (tmp != null) {
          sommaPunteggi += tmp.doubleValue();
          punteggioImpostato = true;
        }
      }
      if (punteggioImpostato) result = new Double(sommaPunteggi);
    }
    return result;
  }

  /**
   * Viene cancellata una categoria da OPES o da ISCRIZCAT portandosi via tutti
   * i padri se questi non hanno altre figlie
   *
   * @param ngara
   * @param codiceCategoria
   * @param ditta
   * @param entita
   *
   * @throws SQLException
   */
  public void cancellaCategoriaConGerarchia(String ngara,
      String codiceCategoria, String ditta, String entita) throws SQLException {
    try {
      // Quando si cancella una categoria, si deve procedere alla cancellazione
      // delle categorie padre, se queste non
      // hanno altr figlie
      Vector<?> datiCategoriaFoglia = this.sqlManager.getVector(
          "select codliv1,codliv2,codliv3,codliv4 from cais where caisim = ?",
          new Object[] { codiceCategoria });
      if (datiCategoriaFoglia != null && datiCategoriaFoglia.size() > 0) {
        String caisim = codiceCategoria;
        String codliv[] = new String[4];

        codliv[0] = (String) ((JdbcParametro) datiCategoriaFoglia.get(0)).getValue();
        codliv[1] = (String) ((JdbcParametro) datiCategoriaFoglia.get(1)).getValue();
        codliv[2] = (String) ((JdbcParametro) datiCategoriaFoglia.get(2)).getValue();
        codliv[3] = (String) ((JdbcParametro) datiCategoriaFoglia.get(3)).getValue();

        String select = "";
        int numLivelli = 4;
        if ((codliv[3] == null || "".equals(codliv[3]))
            && (codliv[2] == null || "".equals(codliv[2]))
            && (codliv[1] == null || "".equals(codliv[1]))
            && (codliv[0] == null || "".equals(codliv[0])))
          numLivelli = 0;
        else if ((codliv[3] == null || "".equals(codliv[3]))
            && (codliv[2] != null && !"".equals(codliv[2])))
          numLivelli = 3;
        else if ((codliv[2] == null || "".equals(codliv[2]))
            && (codliv[1] != null && !"".equals(codliv[1])))
          numLivelli = 2;
        else if ((codliv[1] == null || "".equals(codliv[1]))
            && (codliv[0] != null && !"".equals(codliv[0]))) numLivelli = 1;

        if (numLivelli > 0) {
          caisim = codiceCategoria;
          for (int i = numLivelli; i >= 0; i--) {
            if ("OPES".equals(entita))
              this.sqlManager.update(
                  "delete from opes where ngara3=? and catoff = ?",
                  new Object[] { ngara, caisim });
            else
              this.sqlManager.update(
                  "delete from ISCRIZCAT where CODGAR = ? and NGARA = ? and CODIMP = ? and CODCAT = ?",
                  new Object[] { "$" + ngara, ngara, ditta, caisim });

            if (i == 0) break;

            select = this.getSelect(i, numLivelli, codliv, codiceCategoria,
                entita, ngara, ditta);
            String codice = (String) this.sqlManager.getObject(select, null);
            if (codice == null || "".equals(codice)) {
              caisim = codliv[i - 1];
            } else
              break;
          }
        } else {
          if ("OPES".equals(entita))
            this.sqlManager.update(
                "delete from opes where ngara3=? and catoff = ?", new Object[] {
                    ngara, codiceCategoria });
          else
            this.sqlManager.update(
                "delete from ISCRIZCAT where CODGAR = ? and NGARA = ? and CODIMP = ? and CODCAT = ?",
                new Object[] { "$" + ngara, ngara, ditta, codiceCategoria });
        }
      } else {
        if ("OPES".equals(entita))
          this.sqlManager.update(
              "delete from opes where ngara3=? and catoff = ?", new Object[] {
                  ngara, codiceCategoria });
        else
          this.sqlManager.update(
              "delete from ISCRIZCAT where CODGAR = ? and NGARA = ? and CODIMP = ? and CODCAT = ?",
              new Object[] { "$" + ngara, ngara, ditta, codiceCategoria });
      }

    } catch (SQLException e) {
      throw e;
    }

  }

  /**
   * Viene creata la select per estrarre tutte le occorre di OPES o di ISCRIZCAT
   * che hanno come figlia la categoria corrente, tenendo conto della struttura
   * a livelli di CAIS
   *
   * @param livelloAttuale
   * @param numLivelli
   * @param codliv
   * @param caisim
   * @param entita
   * @param ngara
   * @param ditta
   *
   * @return string
   */
  public String getSelect(int livelloAttuale, int numLivelli, String codliv[],
      String caisim, String entita, String ngara, String ditta) {
    String select = "";

    if (livelloAttuale > 0) {
      if ("OPES".equals(entita))
        select = "select caisim from cais,opes where caisim=catoff and ngara3='"
            + ngara
            + "' and ";
      else
        select = "select caisim from cais,iscrizcat where caisim=codcat and ngara='"
            + ngara
            + "' and codimp='"
            + ditta
            + "' and ";

      for (int i = 0; i < livelloAttuale; i++) {
        select += "codliv" + (i + 1) + " = '" + codliv[i] + "' and ";
      }

      /*
       * for (int i=livelloAttuale; i<numLivelli;i++) { select+= "(codliv" +
       * (i+1) + " <> '" + codliv[i] + "' or codliv" + (i+1)+ " is null) and ";
       * }
       *
       * if ("OPES".equals(entita)) select+= "catoff <> '" + caisim + "'"; else
       * select+= "codcat <> '" + caisim + "'";
       */
      select = select.substring(0, select.length() - 4);

    }

    return select;

  }

  /**
   * Viene determinato se una categoria è una foglia
   *
   * @param codCategoria
   * @return String "1" foglia, "2" altrimenti
   *
   * @throws SQLException
   *
   *
   */
  public String isfoglia(String codCategoria) throws SQLException {
    String isfoglia = (String) this.sqlManager.getObject(
        "select isfoglia from V_CAIS_TIT where CAISIM = ?",
        new Object[] { codCategoria });

    return isfoglia;

  }

  /**
   * Aggiornamento delle ditte escluse o vincitrici in altri lotti della gara
   *
   * @param dittao
   * @return Ritorna un vettore contenente email, pec e fax dell'impresa e
   *         l'informazione se si tratta di RTI
   * @throws SQLException
   */
  public String[] getMailFax(String dittao) throws SQLException {
    String email = null;
    String Pec = null;
    String fax = null;
    String tipimp = null;
    String isRTI = "no";
    String ret[] = new String[4];

    String selectImpr = "select emaiip, emai2ip, faximp,tipimp from impr where codimp = ? ";
    Vector<?> datiImpr = this.sqlManager.getVector(selectImpr, new Object[] { dittao });
    if (datiImpr != null) {
      if (datiImpr.get(0) != null)
        email = ((JdbcParametro) datiImpr.get(0)).getStringValue();

      if (datiImpr.get(1) != null)
        Pec = ((JdbcParametro) datiImpr.get(1)).getStringValue();

      if (datiImpr.get(2) != null)
        fax = ((JdbcParametro) datiImpr.get(2)).getStringValue();

      if (datiImpr.get(3) != null)
        tipimp = ((JdbcParametro) datiImpr.get(3)).getStringValue();

      if ("3".equals(tipimp) || "10".equals(tipimp)) {
        List<?> datiImprMan = this.sqlManager.getListVector(
            "select emai2ip, emaiip, faximp from impr where exists "
                + "(select coddic from ragimp where coddic=impr.codimp and codime9=? and impman=?)",
            new Object[] { dittao, "1" });
        if (datiImprMan != null && datiImprMan.size() == 1) {
          Pec = (String) ((JdbcParametro) ((Vector<?>) datiImprMan.get(0)).get(0)).getValue();
          email = (String) ((JdbcParametro) ((Vector<?>) datiImprMan.get(0)).get(1)).getValue();
          fax = (String) ((JdbcParametro) ((Vector<?>) datiImprMan.get(0)).get(2)).getValue();
        } else {
          Pec = null;
          email = null;
          fax = null;
        }
        isRTI = "si";
      }
    }

    ret[0] = email;
    ret[1] = Pec;
    ret[2] = fax;
    ret[3] = isRTI;

    return ret;
  }

  /**
   * Viene effettuato un confronto fra i dati in db e quelli provenienti dal
   * Portale per il referente.
   *
   * @param referente
   * @return String
   *
   * @throws GestoreException
   */
  public String controlloDatiTeim(ReferenteImpresaType referente)
      throws GestoreException {
    String msg = "";
    String cognome = referente.getCognome();
    String nome = referente.getNome();
    String titolo = referente.getTitolo();
    String codfisc = referente.getCodiceFiscale();
    String sesso = referente.getSesso();
    Calendar dataNascita = referente.getDataNascita();
    String comuneNascita = referente.getComuneNascita();
    String provinciaNascita = referente.getProvinciaNascita();
    IndirizzoType indirizzoType = referente.getResidenza();
    String indirizzo = indirizzoType.getIndirizzo();
    String numCivico = indirizzoType.getNumCivico();
    String cap = indirizzoType.getCap();
    String comune = indirizzoType.getComune();
    String nazione = indirizzoType.getNazione();
    String provincia = indirizzoType.getProvincia();
    //String note = referente.getNote();
    AlboProfessionaleType alboProfessionale = referente.getAlboProfessionale();
    String tipologiaAlbo = alboProfessionale.getTipologia();
    String numIscrizione = alboProfessionale.getNumIscrizione();
    String provinciaIscrizione = alboProfessionale.getProvinciaIscrizione();
    Calendar dataIscrizione = alboProfessionale.getDataIscrizione();

    CassaPrevidenzaType cassaPrevidenza = referente.getCassaPrevidenza();
    String tipologiaCassa = cassaPrevidenza.getTipologia();
    String numMatricola = cassaPrevidenza.getNumMatricola();

    String intestazione = null;
    if (cognome != null) intestazione = cognome;
    if (nome != null) {
      if (intestazione != null)
        intestazione += " " + nome;
      else
        intestazione = nome;
    }

    // Tramite il codice fiscale o la partita iva
    // determino se esiste il codice del tecnico
    String select = "select codtim from teim where upper(cftim) = ? order by codtim";
    String parametro = codfisc.toUpperCase();
    try {
      String codiceReferente = (String) this.sqlManager.getObject(select,
          new Object[] { parametro });
      if (codiceReferente != null && !"".equals(codiceReferente)) {
        // L'occorrenza di TEIM è presente in db si devono valutare le
        // variazioni
        select = "select NOMTIM,COGTIM,NOMETIM,INCTEC,INDTIM,NCITIM,PROTIM,CAPTIM,LOCTIM,CITTEC,NAZTIM,SEXTIM,"
            + "PRONAS,CNATIM,DNATIM,TIPALB,ALBTIM,DATALB,PROALB,TCAPRE,NCAPRE from TEIM where CODTIM=?";

        DataColumnContainer datiTeim = new DataColumnContainer(sqlManager,
            "TEIM", select, new Object[] { codiceReferente });
        String tecnicoOriginale = datiTeim.getColumn("TEIM.NOMTIM").getOriginalValue().getStringValue();

        // Nome
        datiTeim.setValue("TEIM.NOMETIM", nome);
        if (datiTeim.isModifiedColumn("TEIM.NOMETIM")) {
          String nomeOriginale = datiTeim.getColumn("TEIM.NOMETIM").getOriginalValue().getStringValue();
          msg += "\nIl nome del tecnico "
              + tecnicoOriginale
              + " é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + nomeOriginale
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + nome
              + "\n";
        }

        // Cognome
        datiTeim.setValue("TEIM.COGTIM", cognome);
        if (datiTeim.isModifiedColumn("TEIM.COGTIM")) {
          String cognomeOriginale = datiTeim.getColumn("TEIM.COGTIM").getOriginalValue().getStringValue();

          msg += "\nIl cognome del tecnico "
              + tecnicoOriginale
              + " é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + cognomeOriginale
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + cognome
              + "\n";
        }

        // Incarico
        Long incarico = null;
        if (titolo != null && !"".equals(titolo))
          incarico = new Long(titolo);
        else
          titolo = "";
        datiTeim.setValue("TEIM.INCTEC", incarico);
        if (datiTeim.isModifiedColumn("TEIM.INCTEC")) {
          String descOriginale = "";
          Long tmp = datiTeim.getColumn("TEIM.INCTEC").getOriginalValue().longValue();
          if (tmp != null)
            descOriginale = tabellatiManager.getDescrTabellato("Ag004",
                tmp.toString());

          if (!"".equals(titolo))
            titolo = tabellatiManager.getDescrTabellato("Ag004", titolo);

          msg += "\nIl titolo del tecnico "
              + tecnicoOriginale
              + " é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + descOriginale
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + titolo
              + "\n";

        }

        // Indirizzo
        datiTeim.setValue("TEIM.INDTIM", indirizzo);
        if (datiTeim.isModifiedColumn("TEIM.INDTIM")) {
          String indOriginale = datiTeim.getColumn("TEIM.INDTIM").getOriginalValue().getStringValue();

          msg += "\nL'indirizzo del tecnico "
              + tecnicoOriginale
              + " é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + indOriginale
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + indirizzo
              + "\n";
        }

        // Numero
        datiTeim.setValue("TEIM.NCITIM", numCivico);
        if (datiTeim.isModifiedColumn("TEIM.NCITIM")) {
          String numcivicoOriginale = datiTeim.getColumn("TEIM.NCITIM").getOriginalValue().getStringValue();

          msg += "\nIl n.civico del tecnico "
              + tecnicoOriginale
              + " é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + numcivicoOriginale
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + numCivico
              + "\n";
        }

        // Cap
        datiTeim.setValue("TEIM.CAPTIM", cap);
        if (datiTeim.isModifiedColumn("TEIM.CAPTIM")) {
          String capOriginale = datiTeim.getColumn("TEIM.CAPTIM").getOriginalValue().getStringValue();

          msg += "\nIl C.A.P. del tecnico "
              + tecnicoOriginale
              + " é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + capOriginale
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + cap
              + "\n";
        }

        // Comune
        datiTeim.setValue("TEIM.LOCTIM", comune);
        if (datiTeim.isModifiedColumn("TEIM.LOCTIM")) {
          String comuneOriginale = datiTeim.getColumn("TEIM.LOCTIM").getOriginalValue().getStringValue();

          msg += "\nIl comune del tecnico "
              + tecnicoOriginale
              + " é cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + comuneOriginale
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + comune
              + "\n";
        }

        // Provincia
        datiTeim.setValue("TEIM.PROTIM", provincia);
        if (datiTeim.isModifiedColumn("TEIM.PROTIM")) {
          String provinciaOriginale = "";
          if (provincia == null) provincia = "";
          if (datiTeim.getColumn("TEIM.PROTIM").getOriginalValue() != null)
            provinciaOriginale = datiTeim.getColumn("TEIM.PROTIM").getOriginalValue().getStringValue();

          msg += "\nLa provincia del tecnico "
              + tecnicoOriginale
              + " é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + provinciaOriginale
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + provincia
              + "\n";
        }

        // Nazione
        select = "select tab1tip from tab1 where tab1cod='Ag010' and upper(tab1desc) = ?";
        Long nazimp = null;
        try {
          nazimp = (Long) this.sqlManager.getObject(select,
              new Object[] { nazione.toUpperCase() });
        } catch (SQLException e) {
          throw new GestoreException("Errore nella lettura della nazione",
              null, e);
        }
        datiTeim.setValue("TEIM.NAZTIM", nazimp);
        if (datiTeim.isModifiedColumn("TEIM.NAZTIM")) {
          Long nazioneOriginale = datiTeim.getColumn("TEIM.NAZTIM").getOriginalValue().longValue();
          String nazioneOriginaleDesc;
          if (nazioneOriginale != null) {
            nazioneOriginaleDesc = tabellatiManager.getDescrTabellato("Ag010",
                nazioneOriginale.toString());
          } else {
            nazioneOriginaleDesc = "";
          }

          msg += "\nLa nazione del tecnico "
              + tecnicoOriginale
              + " é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + nazioneOriginaleDesc
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + nazione
              + "\n";
        }

        // Sesso
        datiTeim.setValue("TEIM.SEXTIM", sesso);
        if (datiTeim.isModifiedColumn("TEIM.SEXTIM")) {
          String sessoOriginale = "";
          if (datiTeim.getColumn("TEIM.SEXTIM").getOriginalValue() != null)
            sessoOriginale = datiTeim.getColumn("TEIM.SEXTIM").getOriginalValue().getStringValue();

          if ("M".equals(sessoOriginale))
            sessoOriginale = "Maschio";
          else if ("F".equals(sessoOriginale)) sessoOriginale = "Femmina";

          if ("M".equals(sesso))
            sesso = "Maschio";
          else if ("F".equals(sesso)) sesso = "Femmina";

          msg += "\nIl sesso del tecnico "
              + tecnicoOriginale
              + " e' cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + sessoOriginale
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + sesso
              + "\n";
        }

        // Provincia di nascita
        datiTeim.setValue("TEIM.PRONAS", provinciaNascita);
        if (datiTeim.isModifiedColumn("TEIM.PRONAS")) {
          String provinciaOriginale = "";
          if (datiTeim.getColumn("TEIM.PRONAS").getOriginalValue() != null
              && !"".equals(datiTeim.getColumn("TEIM.PRONAS").getOriginalValue().getStringValue())) {
            provinciaOriginale = datiTeim.getColumn("TEIM.PRONAS").getOriginalValue().getStringValue();
            provinciaOriginale = this.getProvinciaDaSigla(provinciaOriginale.toUpperCase());
          }

          if (provincia != null && !"".equals(provincia)) {
            provincia = this.getProvinciaDaSigla(provincia.toUpperCase());
          }

          msg += "\nLa provincia di nascita del tecnico "
              + tecnicoOriginale
              + " e' cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + provinciaOriginale
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + provincia
              + "\n";
        }

        // Comune di nascita
        datiTeim.setValue("TEIM.CNATIM", comuneNascita);
        if (datiTeim.isModifiedColumn("TEIM.CNATIM")) {
          String comuneOriginale = "";
          if (datiTeim.getColumn("TEIM.CNATIM").getOriginalValue() != null)
            comuneOriginale = datiTeim.getColumn("TEIM.CNATIM").getOriginalValue().getStringValue();

          msg += "\nIl comune di nascita del tecnico "
              + tecnicoOriginale
              + " e' cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + comuneOriginale
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + comuneNascita
              + "\n";
        }

        // Data nascita
        java.util.Date campoData = null;
        if (dataNascita != null) campoData = dataNascita.getTime();
        datiTeim.setValue("TEIM.DNATIM", campoData);
        if (datiTeim.isModifiedColumn("TEIM.DNATIM")) {
          String dnatecOriginale = "";
          if (datiTeim.getColumn("TEIM.DNATIM").getOriginalValue().dataValue() != null) {
            dnatecOriginale = UtilityDate.convertiData(
                new Date(
                    datiTeim.getColumn("TEIM.DNATIM").getOriginalValue().dataValue().getTime()),
                UtilityDate.FORMATO_GG_MM_AAAA);
          }

          String dnatec = "";
          if (dataNascita != null)
            dnatec = UtilityDate.convertiData(dataNascita.getTime(),
                UtilityDate.FORMATO_GG_MM_AAAA);

          msg += "\nLa data di nascita del tecnico "
              + tecnicoOriginale
              + " é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + dnatecOriginale
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + dnatec
              + "\n";
        }

        // Albo professionale
        datiTeim.setValue("TEIM.TIPALB", tipologiaAlbo);
        if (datiTeim.isModifiedColumn("TEIM.TIPALB")) {
          Long tipologiaOriginale = datiTeim.getColumn("TEIM.TIPALB").getOriginalValue().longValue();
          String tipologiaOriginaleDesc;
          if (tipologiaOriginale != null) {
            tipologiaOriginaleDesc = tabellatiManager.getDescrTabellato(
                "G_040", tipologiaOriginale.toString());
          } else {
            tipologiaOriginaleDesc = "";
          }

          String tipologiaDesc = "";
          if (tipologiaAlbo != null)
            tipologiaDesc = tabellatiManager.getDescrTabellato("G_040",
                tipologiaAlbo);

          msg += "\nLa tipologia dell'albo professionale del tecnico "
              + tecnicoOriginale
              + " é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + tipologiaOriginaleDesc
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + tipologiaDesc
              + "\n";
        }

        // iscrizione all'albo
        datiTeim.setValue("TEIM.ALBTIM", numIscrizione);
        if (datiTeim.isModifiedColumn("TEIM.ALBTIM")) {
          String numIscrizioneOriginale = "";
          if (datiTeim.getColumn("TEIM.ALBTIM").getOriginalValue() != null)
            numIscrizioneOriginale = datiTeim.getColumn("TEIM.ALBTIM").getOriginalValue().getStringValue();

          if (numIscrizione == null) numIscrizione = "";
          msg += "\nIl numero iscrizione all'albo professionale del tecnico "
              + tecnicoOriginale
              + " e' cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + numIscrizioneOriginale
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + numIscrizione
              + "\n";
        }

        campoData = null;
        if (dataIscrizione != null) campoData = dataIscrizione.getTime();
        datiTeim.setValue("TEIM.DATALB", campoData);
        if (datiTeim.isModifiedColumn("TEIM.DATALB")) {
          String datalbOriginale = "";
          if (datiTeim.getColumn("TEIM.DATALB").getOriginalValue().dataValue() != null) {
            datalbOriginale = UtilityDate.convertiData(
                new Date(
                    datiTeim.getColumn("TEIM.DATALB").getOriginalValue().dataValue().getTime()),
                UtilityDate.FORMATO_GG_MM_AAAA);
          }

          String datalb = "";
          if (dataIscrizione != null)
            datalb = UtilityDate.convertiData(dataIscrizione.getTime(),
                UtilityDate.FORMATO_GG_MM_AAAA);

          msg += "\nLa data iscrizione all'albo professionale del tecnico "
              + tecnicoOriginale
              + " é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + datalbOriginale
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + datalb
              + "\n";
        }

        String codiceIstat = null;
        try {
          if (provinciaIscrizione != null)
            codiceIstat = this.getCodiceISTAT(provinciaIscrizione.toUpperCase());

        } catch (SQLException e) {
          throw new GestoreException("Errore nella lettura del codice ISTAT ",
              null, e);
        }
        datiTeim.setValue("TEIM.PROALB", codiceIstat);
        if (datiTeim.isModifiedColumn("TEIM.PROALB")) {
          String codiceISTATOriginale = "";
          if (datiTeim.getColumn("TEIM.PROALB").getOriginalValue() != null)
            codiceISTATOriginale = datiTeim.getColumn("TEIM.PROALB").getOriginalValue().getStringValue();

          msg += "\nIl codice ISTAT della provincia di iscrizione all'albo del tecnico "
              + tecnicoOriginale
              + " è cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + codiceISTATOriginale
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + codiceIstat
              + "\n";
        }

        datiTeim.setValue("TEIM.TCAPRE", tipologiaCassa);
        if (datiTeim.isModifiedColumn("TEIM.TCAPRE")) {
          Long tipologiaOriginale = datiTeim.getColumn("TEIM.TCAPRE").getOriginalValue().longValue();
          String tipologiaOriginaleDesc;
          if (tipologiaOriginale != null) {
            tipologiaOriginaleDesc = tabellatiManager.getDescrTabellato(
                "G_041", tipologiaOriginale.toString());
          } else {
            tipologiaOriginaleDesc = "";
          }

          String tipologiaDesc = "";
          if (tipologiaCassa != null)
            tipologiaDesc = tabellatiManager.getDescrTabellato("G_041",
                tipologiaCassa);

          msg += "\nLa tipologia della cassa di previdenza del tecnico "
              + tecnicoOriginale
              + " é cambiata \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + tipologiaOriginaleDesc
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + tipologiaDesc
              + "\n";
        }

        datiTeim.setValue("TEIM.NCAPRE", numMatricola);
        if (datiTeim.isModifiedColumn("TEIM.NCAPRE")) {
          String numIscrizioneOriginale = "";
          if (datiTeim.getColumn("TEIM.NCAPRE").getOriginalValue() != null)
            numIscrizioneOriginale = datiTeim.getColumn("TEIM.NCAPRE").getOriginalValue().getStringValue();
          String numIscr = "";
          if (numMatricola != null) numIscr = numMatricola;

          msg += "\nIl numero matricola di iscrizione alla cassa di previdenza  del tecnico "
              + tecnicoOriginale
              + " e' cambiato \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + numIscrizioneOriginale
              + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
              + numIscr
              + "\n";
        }

      } else {
        // Inserimento nuovo tecnico
        msg += "\nVerra' inserito il nuovo tecnico "
            + intestazione
            + " con codice "
            + codiceReferente
            + "\n";
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella verifica delle variazioni dei dati del tecnico", null,
          e);
    }
    return msg;
  }

  /**
   * Viene effettuato il controllo dei dati in db nelle entita ANTICORLOTTI,
   * ANTICORPATECIP e ANTICORDITTE, per vedere se rispettano le condizioni
   * imposte da AVCP
   *
   * @param id
   *        anticor.id se verificaSingola=false anticorlotti.is se
   *        verificaSingola=true
   * @param verificaSingola
   *        true se si deve eseguire il controllo su una singola riga di
   *        anticorlotti false se si deve eseguire il controllo su tutte le
   *        righe di anticorlotti
   * @param saltaDatiContratto
   *        true non vengono controllati i dati del contratto: datainizio,
   *        dataultimazione, impsommeliq false vengono controllati anche i dati
   *        del contratto
   * @param stato
   *        se vale 1 e 3 si saltano i controlli su partecipanti,
   *        aggiudicataria, liquidazioni
   * @return HashMap esito true controlli superati false altrimenti msg messaggi
   *         sui controlli non superati
   *
   *
   * @throws SQLException
   * @throws GestoreException
   */
  public HashMap<String, Object> controlloDatiAVCP(Long id, boolean verificaSingola,
      boolean saltaDatiContratto, String codiceStazioneAppaltante) throws SQLException, GestoreException {
    Boolean controlliSuperati = new Boolean(true);
    String msg = "";
    boolean msgLotto = false;
    HashMap<String, Object> valoriRitorno = new HashMap<String, Object>();

    String select = "select cig, codfiscprop, denomprop, oggetto, sceltacontr, lotti.id, impaggiudic, impsommeliq, datainizio, dataultimazione, stato from anticorlotti lotti, anticor a where a.id=? and a.id=lotti.idanticor and pubblica='1'  order by cig";
    if (verificaSingola)
      select = "select cig, codfiscprop, denomprop, oggetto, sceltacontr, id, impaggiudic, impsommeliq, datainizio, dataultimazione, stato from anticorlotti where id=? order by cig";

    List<?> listaAnticorlotti = null;

    listaAnticorlotti = this.sqlManager.getListVector(select, new Object[] { id });

    if (listaAnticorlotti != null && listaAnticorlotti.size() > 0) {
      String cig = null;
      String codfiscprop = null;
      String denomprop = null;
      String oggetto = null;
      Long sceltacontr = null;
      Long idanticorlotti = null;
      Double impaggiudic = null;
      Double impsommeliq = null;
      Timestamp datainizio = null;
      Timestamp dataultimazione = null;
      boolean codfiscFormato1Valido = true;
      boolean codfiscFormato2Valido = true;
      //boolean codfiscFormato3Valido = true;
      Long stato = null;
      String ragSocRaggruppamento=null;

      select = "select id, tipo, aggiudicataria, ragsoc from anticorpartecip where idanticorlotti=?";
      List<?> listaPartecipanti = null;
      for (int i = 0; i < listaAnticorlotti.size(); i++) {
        cig = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
            listaAnticorlotti.get(i), 0).getStringValue());
        codfiscprop = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
            listaAnticorlotti.get(i), 1).getStringValue());
        denomprop = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
            listaAnticorlotti.get(i), 2).getStringValue());
        oggetto = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
            listaAnticorlotti.get(i), 3).getStringValue());
        sceltacontr = SqlManager.getValueFromVectorParam(
            listaAnticorlotti.get(i), 4).longValue();
        idanticorlotti = SqlManager.getValueFromVectorParam(
            listaAnticorlotti.get(i), 5).longValue();
        impaggiudic = SqlManager.getValueFromVectorParam(
            listaAnticorlotti.get(i), 6).doubleValue();
        impsommeliq = SqlManager.getValueFromVectorParam(
            listaAnticorlotti.get(i), 7).doubleValue();
        datainizio = SqlManager.getValueFromVectorParam(
            listaAnticorlotti.get(i), 8).dataValue();
        dataultimazione = SqlManager.getValueFromVectorParam(
            listaAnticorlotti.get(i), 9).dataValue();
        stato = SqlManager.getValueFromVectorParam(listaAnticorlotti.get(i), 10).longValue();

        // Codice CIG
        if (cig == null) {
          controlliSuperati = new Boolean(false);
          msgLotto = true;
          String msgOggetto = "";
          if (oggetto != null) {
            msgOggetto = "'" + oggetto + "'";
          }
          msg += "Il codice CIG del lotto " + msgOggetto + " (id="
              + idanticorlotti.toString()
              + ") non è valorizzato.\n";
        } else {
          // Controllo unicità codice CIG, da fare considerando solo i lotti
          // dell'anno corrente
          Long annorif = null;
          if (verificaSingola)
            annorif = (Long) this.sqlManager.getObject(
                "select annorif from anticorlotti, anticor where anticorlotti.id = ? and anticorlotti.idanticor=anticor.id",
                new Object[] { id });
          else
            annorif = (Long) this.sqlManager.getObject(
                "select annorif from anticor where id = ?", new Object[] { id });

          if (codiceStazioneAppaltante == null || "".equals(codiceStazioneAppaltante))
            codiceStazioneAppaltante="*";
          Long count = (Long) this.sqlManager.getObject(
              "select count(lotti.id) from anticorlotti lotti, anticor a where pubblica = ? and upper(cig)=? and a.id=lotti.idanticor and a.annorif=?" +
              " and a.codein=?",
              new Object[] { "1", cig.toUpperCase(), annorif,codiceStazioneAppaltante });
          if (count != null && count.longValue() > 1) {
            controlliSuperati = new Boolean(false);
            msgLotto = true;
            msg += "Ci sono più lotti con codice CIG '" + cig.toUpperCase() + "'.\n";
          }

          // Controllo validita' codice CIG (no codice CIG fittizio)
        	if (!this.controlloCodiceCIG(cig)) {
        	  controlliSuperati = new Boolean(false);
              msgLotto = true;
              msg += "Il CIG '" + cig.toUpperCase() + "' non e' valido.\n";
        	}
          }

        // Codice fiscale proponente
        if (codfiscprop == null) {
          controlliSuperati = new Boolean(false);
          msgLotto = true;
          msg += "Non è valorizzato il codice fiscale del proponente del lotto con codice CIG '"
              + cig
              + "'.\n";
        } else {
          // Verifica di correttezza del formato del codice fiscale
          codfiscFormato1Valido = UtilityFiscali.isValidCodiceFiscale(codfiscprop);
          codfiscFormato2Valido = UtilityFiscali.isValidPartitaIVA(codfiscprop);
          if (!codfiscFormato1Valido && !codfiscFormato2Valido) {
            controlliSuperati = new Boolean(false);
            msgLotto = true;
            msg += "Il codice fiscale del proponente del lotto con codice CIG '"
                + cig
                + "' non ha un formato valido.\n";
          }
        }

        // Denominazione proponente
        if (denomprop == null) {
          controlliSuperati = new Boolean(false);
          msgLotto = true;
          msg += "Non è valorizzata la denominazione del proponente del lotto con codice CIG '"
              + cig
              + "'.\n";
        }

        // Oggetto
        if (oggetto == null) {
          controlliSuperati = new Boolean(false);
          msgLotto = true;
          msg += "Non è valorizzato l'oggetto del lotto con codice CIG '"
              + cig
              + "'.\n";
        }

        // Scelta contraente
        if (sceltacontr == null) {
          controlliSuperati = new Boolean(false);
          msgLotto = true;
          msg += "Non è valorizzata la scelta contraente del lotto con codice CIG '"
              + cig
              + "'.\n";
        }

        // Importo aggiudicazione
        if (impaggiudic == null && (new Long(2)).equals(stato)) {
          controlliSuperati = new Boolean(false);
          msgLotto = true;
          msg += "Non è valorizzato l'importo di aggiudicazione del lotto con codice CIG '"
              + cig
              + "'.\n";
        }

        if (impaggiudic != null && impaggiudic.doubleValue()<0) {
          controlliSuperati = new Boolean(false);
          msgLotto = true;
          msg += "L'importo di aggiudicazione del lotto con codice CIG '"
              + cig
              + "' ha un valore negativo.\n";
        }

        // Somme liquidate
        if (impsommeliq != null && impsommeliq.doubleValue() <0 ) {
          controlliSuperati = new Boolean(false);
          msgLotto = true;
          msg += "L'importo liquidato del lotto con codice CIG '"
              + cig
              + "' ha un valore negativo.\n";
        }

        /*
        // Data inizio
        if (datainizio == null
            && !saltaDatiContratto
            && (new Long(2)).equals(stato)) {
          controlliSuperati = new Boolean(false);
          msgLotto = true;
          msg += "Non è valorizzata la data inizio del lotto con codice cig = "
              + cig
              + ".\n";
        }

        // Data ultimazione
        if (dataultimazione == null
            && !saltaDatiContratto
            && (new Long(2)).equals(stato)) {
          controlliSuperati = new Boolean(false);
          msgLotto = true;
          msg += "Non è valorizzata la data ultimazione del lotto con codice cig = "
              + cig
              + ".\n";
        }

        // Somme liquidate
        if (impsommeliq == null
            && !saltaDatiContratto
            && (new Long(2)).equals(stato)) {
          controlliSuperati = new Boolean(false);
          msgLotto = true;
          msg += "Non è valorizzato l'importo somme liquidate del lotto con codice cig = "
              + cig
              + ".\n";
        }
        */
        //Se data ultimazione è valorizzata, lo deve essere anche data inizio e data inizio<=data ultimazione
        if (dataultimazione != null && !saltaDatiContratto && datainizio == null) {
          controlliSuperati = new Boolean(false);
          msgLotto = true;
          msg += "Non è valorizzata la data inizio del lotto con codice CIG '"
              + cig
              + "'.\n";
        }
        if (dataultimazione != null && datainizio != null && datainizio.after(dataultimazione) && !saltaDatiContratto) {
          controlliSuperati = new Boolean(false);
          msgLotto = true;
          msg += "La data inizio del lotto con codice CIG '"
              + cig
              + "' non può essere successiva alla data ultimazione.\n";
        }


        // Controllo sui partecipanti di ogni lotto
        listaPartecipanti = this.sqlManager.getListVector(select,
            new Object[] { new Long(idanticorlotti) });

        //if ((new Long(2)).equals(stato)) {
          if (listaPartecipanti != null && listaPartecipanti.size() > 0) {
            String ragsoc = null;
            String codfiscPartecip = null;
            String idfiscest = null;
            Long ruolo = null;
            Long tipo = null;
            String aggiudicataria = null;
            boolean aggiudicatariaPresente = false;
            Long idPartecipante=null;
            for (int j = 0; j < listaPartecipanti.size(); j++) {
              idPartecipante = SqlManager.getValueFromVectorParam(
                  listaPartecipanti.get(j), 0).longValue();
              tipo = SqlManager.getValueFromVectorParam(
                  listaPartecipanti.get(j), 1).longValue();
              aggiudicataria = SqlManager.getValueFromVectorParam(
                  listaPartecipanti.get(j), 2).stringValue();
              ragSocRaggruppamento = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
                  listaPartecipanti.get(j), 3).getStringValue());

              List<?> listaDitte = null;
               listaDitte = this.sqlManager.getListVector("select ragsoc, codfisc, idfiscest, ruolo from anticorditte" +
              		" where idanticorpartecip=?", new Object[] { new Long(idPartecipante) });

              /*
              int numMandatarie=0;
              int numMandante=0;
              int numCapogruppo=0;
              int numAssociate=0;
              int numConsorziate=0;
              */
              if (listaDitte != null && listaDitte.size() > 0) {
                for (int z=0;z < listaDitte.size(); z++) {
                  ragsoc = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
                      listaDitte.get(z), 0).getStringValue());
                  codfiscPartecip = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
                      listaDitte.get(z), 1).getStringValue());
                  idfiscest = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
                      listaDitte.get(z), 2).getStringValue());
                  ruolo = SqlManager.getValueFromVectorParam(
                      listaDitte.get(z), 3).longValue();

                  if (ragsoc == null) {
                    controlliSuperati = new Boolean(false);
                    msgLotto = true;
                    msg += "Non è valorizzata la ragione sociale di un partecipante";
                    if (tipo != null && tipo.longValue() == 2) {
                      msg += " del raggruppamento '"
                      + ragSocRaggruppamento
                      + "'";
                    }
                    msg	+= " del lotto con codice CIG '"
                        + cig
                        + "'.\n";
                  }
                  if (codfiscPartecip == null && idfiscest == null) {
                    controlliSuperati = new Boolean(false);
                    msgLotto = true;
                    msg += "Non è valorizzato nè il codice fiscale nè l'id fiscale estero del partecipante '"
                        + ragsoc;
                    if (tipo != null && tipo.longValue() == 2) {
                      msg += "' del raggruppamento '"
                      + ragSocRaggruppamento;
                    }
                    msg += "' del lotto con codice CIG '"
                        + cig
                        + "'.\n";
                  } else {
                    if (codfiscPartecip != null) {
                      codfiscFormato1Valido = UtilityFiscali.isValidCodiceFiscale(codfiscPartecip);
                      codfiscFormato2Valido = UtilityFiscali.isValidPartitaIVA(codfiscPartecip);
                      if (!codfiscFormato1Valido && !codfiscFormato2Valido) {
                        controlliSuperati = new Boolean(false);
                        msgLotto = true;
                        msg += "Il codice fiscale del partecipante '"
                            + ragsoc;
                        if (tipo != null && tipo.longValue() == 2) {
                          msg += "' del raggruppamento '"
                          + ragSocRaggruppamento;
                        }
                        msg += "' del lotto con codice CIG '"
                            + cig
                            + "' non ha un formato valido.\n";
                      }
                    }
                  }

                  if (tipo != null && tipo.longValue() == 2) {
                    if (listaDitte.size() < 2) {
                      controlliSuperati = new Boolean(false);
                      msgLotto = true;
                      msg += "Non sono presenti almeno due ditte"
                      + " componenti il raggruppamento '"
                      + ragSocRaggruppamento
                      + "' del lotto con codice CIG '"
                      + cig
                      + "'.\n";
                    }
                    if (ruolo == null) {
                      controlliSuperati = new Boolean(false);
                      msgLotto = true;
                      msg += "Non è valorizzato il ruolo del partecipante '"
                          + ragsoc
                          + "' del raggruppamento '"
                          + ragSocRaggruppamento
                          + "' del lotto con codice CIG '"
                          + cig
                          + "'.\n";
                    }
                    /*
                    else{
                      switch (ruolo.intValue()) {
                      case 1:
                        numMandante++;
                        break;
                      case 2:
                        numMandatarie++;
                        break;
                      case 3:
                        numAssociate++;
                        break;
                      case 4:
                        numCapogruppo++;
                        break;
                      case 5:
                        numConsorziate++;
                        break;
                      }
                    }
                    */
                  }
                }
              } else {
                controlliSuperati = new Boolean(false);
                msgLotto = true;
                msg += "Non sono presenti almeno due ditte"
                  + " componenti il raggruppamento '"
                  + ragSocRaggruppamento
                  + "' del lotto con codice CIG '"
                  + cig
                  + "'.\n";
              }
              if ("1".equals(aggiudicataria)) aggiudicatariaPresente = true;

              /*
              if (listaDitte.size()>0 && tipo != null && tipo.longValue() == 2 && (((numMandatarie>0 || numMandante>0)&& !(numMandatarie==1 && numMandante>=1)) ||
                  ((numCapogruppo>0 || numAssociate>0 )&& !(numCapogruppo==1 && numAssociate>=1)) || (numMandatarie==0 && numCapogruppo==0 && numConsorziate==0))) {
                controlliSuperati = new Boolean(false);
                msgLotto = true;
                msg += "Nel lotto con codice cig = "
                  + cig
                  +	" il raggruppamento partecipante "
                  + ragSocRaggruppamento
                  + " contiene dei ruoli incompatibili.\n";
              }
              */
            }

            if (!aggiudicatariaPresente && (new Long(2)).equals(stato)) {
              controlliSuperati = new Boolean(false);
              msgLotto = true;
              msg += "Non è presente l'aggiudicataria del lotto con codice CIG '"
                  + cig
                  + "'.\n";
            }

          } else {
            if ((new Long(2)).equals(stato)) {
              controlliSuperati = new Boolean(false);
              msgLotto = true;
              msg += "Non sono presenti i partecipanti del lotto con codice CIG '"
                  + cig
                  + "'.\n";
            }
          }
        //}

        if (msgLotto == true) msg += "\n";
        msgLotto = false;
      }
    }

    valoriRitorno.put("esito", controlliSuperati);
    valoriRitorno.put("msg", msg);

    return valoriRitorno;
  }


  /**
   * Metodo che inserisce in DB i lotti di un adempimento.
   *
   * @param annorif
   * @param cig
   * @param idAnticor
   * @param idAnticorlottiDaricaricare
   * @param idLotto
   * @param ufficioIntestatario
   * @param lottoInBo
   * @param controlloCigDuplicati
   * @return  1: inserimento eseguito con successo
   *          2: Lotti non importati
   *
   * @throws GestoreException
   */
  public HashMap<String,Object> insertLottiAdempimento(Long annorif, String cig, Long idAnticor, Long idAnticorlottiDaricaricare,
      String idLotto, String ufficioIntestatario, String lottoInBo, String daannoprecRicaricato, boolean controlloCigDuplicati,
      String cfResponsabile) throws GestoreException {

    String risultato = "1";
    String cigDuplicati = "";
    String pubblica = "";
    String daAnnoPrec = "";
    String insertAnticorlotti = "insert into ANTICORLOTTI (ID, IDANTICOR, DAANNOPREC, CIG, CODFISCPROP, DENOMPROP, OGGETTO, SCELTACONTR, IMPAGGIUDIC, DATAINIZIO, "
        + "DATAULTIMAZIONE, IMPSOMMELIQ, PUBBLICA, INVIABILE, STATO, LOTTOINBO, IDLOTTO, CODFISRESP, NOMERESP, IDCONTRATTO) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

    // Gestione inserimento in ANTICORLOTTI, ANTICORPARTECIP e ANTICORDITTE da lotto
    Object parametri[] = null;
    StringBuffer select = new StringBuffer("select lotto, cig, denomprop, codfiscprop, oggetto, tipgarg, iterga, impaggiudic, datainizio, dataultimazione, impsommeliq, aggiudicataria, esineg, dattoa, dteoff, codfisresp, nomeresp from v_dati_lotti ");
    StringBuffer where = new StringBuffer(" where ");
    if (annorif != null) {
      where.append(" ((datpub >= ? and  datpub <= ? ");
      where.append(" or (datpub is null and (dinvit >= ? and  dinvit <= ?))) ");
      where.append(" or (esineg is not null and datneg >= ? and  datneg <= ?) ");
      where.append(" or (dattoa >= ? and  dattoa <= ?) ");
      where.append(" or (dattoa is not null and  ((datainizio >=? and  datainizio <= ?) or (dataultimazione >=? and dataultimazione<=?)))) ");

      java.util.Date dataInizio = null;
      if (annorif.intValue() == 2013)
        dataInizio = UtilityDate.convertiData("01/12/2012",
            UtilityDate.FORMATO_GG_MM_AAAA);
      else
        dataInizio = UtilityDate.convertiData("01/01/" + annorif.toString(),
            UtilityDate.FORMATO_GG_MM_AAAA);

      java.util.Date dataFine = UtilityDate.convertiData("31/12/" + annorif.toString(),
          UtilityDate.FORMATO_GG_MM_AAAA);

      parametri = new Object[] {
          dataInizio, dataFine, dataInizio, dataFine, dataInizio, dataFine,
          dataInizio, dataFine, dataInizio, dataFine, dataInizio, dataFine };
    }

    if (StringUtils.isNotEmpty(cig)) {
      if (where.length() > 7)
        where.append(" and UPPER(cig)='" + cig.toUpperCase() + "'");
      else
        where.append(" cig='" + cig + "'");
    } else if (StringUtils.isNotEmpty(idLotto)) {
      if (where.length() > 7)
        where.append(" and lotto='" + idLotto + "'");
      else
        where.append(" lotto='" + idLotto + "'");
    }

    if (StringUtils.isNotEmpty(ufficioIntestatario) && !"*".equals(ufficioIntestatario)) {
      if (where.length() > 7)
        where.append(" and codiceprop='" + ufficioIntestatario + "'");
      else
        where.append(" codiceprop='" + ufficioIntestatario + "'");
    }

    if (StringUtils.isNotEmpty(cfResponsabile)) {
    	if (where.length() > 7) {
    		where.append(" and UPPER(CODFISRESP)='" + cfResponsabile.toUpperCase() + "'");
    	} else {
    		where.append(" UPPER(CODFISRESP)='" + cfResponsabile.toUpperCase() + "'");
    }
    }

    if (where.length() > 7) {
    	select.append(where.toString());
    }

    List<?> listaLotti = null;
    try {
      listaLotti = this.sqlManager.getListVector(select.toString(), parametri);
    } catch (SQLException e) {
      //this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException(
          "Errore nella lettura dei dati dei lotti per popolare ANTICORLOTTI ", null, e);
    }

    if (listaLotti != null && listaLotti.size() > 0) {
      String codiceCig = null;
      String oggetto = null;
      Long tipoGara = null;
      //Long iterga = null;
      Double iaggiu = null;
      String codfiscStazApp = null;
      String denomStazApp = null;
      //String dittaAgg = null;
      java.util.Date dataI = null;
      java.util.Date dataF = null;
      Double ImportoLiq = null;
      String lottoOld = "";
      Long idAnticorlotti = null;
      String inviabile = "";
      Long esineg = null;
      java.util.Date dattoa = null;
      Long stato = null;
      java.util.Date dteoff = null;
      Timestamp dteoffTimestamp = null;
      String codfisresp = null;
      String nomeresp = null;
      double importoArrotondato;
      String idContratto = null;
      // Inserimento in ANTICORLOTTI dal lotto
      for (int i = 0; i < listaLotti.size(); i++) {
        String ngara = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).getStringValue();
        codiceCig = (String)SqlManager.getValueFromVectorParam(listaLotti.get(i), 1).getValue();
        // Poichè nella view V_DATI_LOTTI vengono presentate più righe in base
        // alla DINVIT e DATPUB, ordino per DINVIT e DATPUB crescente
        // e si prende per ogni lotto solo la prima occorrenza

        //Si deve controllare l'unicità del codice CIG, se non è unico non importo il lotto e memorizzo

        Long conteggioCigDuplicati = new Long(0);
        if (codiceCig != null) {
          try {
            if (controlloCigDuplicati)
              conteggioCigDuplicati = (Long)this.sqlManager.getObject("select count(cig) from v_dati_lotti where upper(cig)=? and lotto<>?",
            		  new Object[]{codiceCig.toUpperCase(),ngara});
          } catch (SQLException e) {
              throw new GestoreException("Errore nel controllo dei CIG duplicati", null, e);
          }
        }
        if (conteggioCigDuplicati != null && conteggioCigDuplicati.longValue()>0) {
          if (cigDuplicati.indexOf(codiceCig) < 0) {
            if (cigDuplicati != "")
              cigDuplicati += ", ";

            cigDuplicati += codiceCig;
            //Caso di inserimento singolo lotto da dati correnti
            if (cig != null && !"".equals(cig)) {
              risultato = "2";
            }
          }
        } else if (!lottoOld.equals(ngara)) {
          denomStazApp = (String)SqlManager.getValueFromVectorParam(listaLotti.get(i), 2).getValue();
          codfiscStazApp = (String)SqlManager.getValueFromVectorParam(listaLotti.get(i), 3).getValue();
          oggetto = (String)SqlManager.getValueFromVectorParam(listaLotti.get(i), 4).getValue();
          tipoGara = SqlManager.getValueFromVectorParam(listaLotti.get(i), 5).longValue();
          //iterga = SqlManager.getValueFromVectorParam(listaLotti.get(i), 6).longValue();
          iaggiu = SqlManager.getValueFromVectorParam(listaLotti.get(i), 7).doubleValue();
          if (iaggiu != null) {
            importoArrotondato = UtilityMath.round(iaggiu.doubleValue(),2);
            iaggiu = new Double(importoArrotondato);
          }

          dataI = SqlManager.getValueFromVectorParam(listaLotti.get(i), 8).dataValue();
          dataF = SqlManager.getValueFromVectorParam(listaLotti.get(i), 9).dataValue();
          ImportoLiq = SqlManager.getValueFromVectorParam(listaLotti.get(i), 10).doubleValue();
          if (ImportoLiq != null) {
            importoArrotondato = UtilityMath.round(ImportoLiq.doubleValue(),2);
            ImportoLiq = new Double(importoArrotondato);
          }
          //dittaAgg = (String)SqlManager.getValueFromVectorParam(listaLotti.get(i), 11).getValue();
          esineg = SqlManager.getValueFromVectorParam(listaLotti.get(i), 12).longValue();
          dattoa = SqlManager.getValueFromVectorParam(listaLotti.get(i), 13).dataValue();
          if (esineg != null)
            stato = new Long(3);
          else if (dattoa != null)
            stato = new Long(2);
          else
            stato = new Long(1);
          dteoffTimestamp = SqlManager.getValueFromVectorParam(listaLotti.get(i), 14).dataValue();
          if (dteoffTimestamp != null)
            dteoff = new java.util.Date(dteoffTimestamp.getTime());
          else
            dteoff = null;

          codfisresp = SqlManager.getValueFromVectorParam(listaLotti.get(i), 15).getStringValue();
          nomeresp = SqlManager.getValueFromVectorParam(listaLotti.get(i), 16).getStringValue();

          //java.util.Date dataOdierna =  UtilityDate.getDataOdiernaAsDate();
          SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
          java.util.Date dataOdierna;
          try {
            dataOdierna = dateFormat.parse(dateFormat.format(new java.util.Date()));
          } catch (ParseException e) {
            throw new GestoreException("Errore nell'estrarre la data odierna", null, e);
          }

            denomStazApp = StringUtils.left(denomStazApp, 250);
            Long sceltaContr = this.getSceltaContraente(tipoGara);

            if (idAnticorlottiDaricaricare == null)
              idAnticorlotti = new Long(genChiaviManager.getNextId("ANTICORLOTTI"));
            else
              idAnticorlotti = new Long(idAnticorlottiDaricaricare);

            pubblica = "2";
            daAnnoPrec = "3";
            inviabile = "2";

            if (annorif != null) {
              try {
                Long conteggioCig = (Long)this.sqlManager.getObject("select count(anticor.id) from anticor, anticorlotti  " +
                      "where anticor.id=anticorlotti.idanticor and annorif=? and esportato='1' and pubblica='1' and cig =? " +
                      " and codein=? ", new Object[]{new Long(annorif.longValue()-1), codiceCig, ufficioIntestatario});
                if (conteggioCig == null || (new Long(0)).equals(conteggioCig))
                  daAnnoPrec = "2";
              } catch (SQLException e) {
                throw new GestoreException(
                    "Errore nella lettura dei dati degli adempimenti dell'anno " + annorif.toString(), null, e);
              }
            }

            if (daannoprecRicaricato != null && !"".equals(daannoprecRicaricato))
              daAnnoPrec = daannoprecRicaricato;

            if(oggetto!=null && !"".equals(oggetto)){
              if(oggetto.indexOf(costanteIdContratto)>0){
                int indice = oggetto.indexOf(costanteIdContratto);
                idContratto = oggetto.substring( indice + costanteIdContratto.length());
                oggetto = oggetto.substring(0, indice);

              }else
                idContratto=null;
            }else
              idContratto=null;

            try {
              this.sqlManager.update(insertAnticorlotti, new Object[] {
                  idAnticorlotti, idAnticor, daAnnoPrec, codiceCig, codfiscStazApp, denomStazApp, oggetto, sceltaContr,
                  iaggiu, dataI, dataF, ImportoLiq, pubblica, inviabile, stato,lottoInBo, ngara, codfisresp, nomeresp, idContratto });
            } catch (SQLException e) {
              //this.getRequest().setAttribute("erroreOperazione", "1");
              throw new GestoreException("Errore nell'inserimento in ANTICORLOTTI ", null, e);
            }

            try {
              if ((new Long(2)).equals(stato) || ((new Long(1)).equals(stato) && dteoff != null && dteoff.before(dataOdierna))) {
                this.insertFiglieAnticorLottiDaLotto(ngara, idAnticorlotti);
              }
            } catch (GestoreException e) {
              //this.getRequest().setAttribute("erroreOperazione", "1");
              throw e;
            }

            lottoOld = ngara;
            // Controllo dei dati inseriti per valurare i valori di pubblica e inviabile
            HashMap<?,?> esitoControlli = null;
            String msg = null;
            try {
              esitoControlli = this.controlloDatiAVCP(idAnticorlotti,
                  true, false,ufficioIntestatario);
              if (esitoControlli != null) {
                Boolean controlloOk = (Boolean) esitoControlli.get("esito");
                if (controlloOk.booleanValue()) {
                  inviabile = "1";
                  pubblica = "1";
                } else
                  msg = (String)esitoControlli.get("msg");

              }
            } catch (SQLException e) {
              //this.getRequest().setAttribute("erroreOperazione", "1");
              throw new GestoreException(
                  "Errore nella verifica dei dati del lotto " + idAnticorlotti.toString(), null, e);
            }
            try {
              this.sqlManager.update(
                  "update anticorlotti set inviabile=?, pubblica=?, testolog=? where id=?",
                  new Object[] { inviabile, pubblica, msg, idAnticorlotti });
            } catch (SQLException e) {
              //this.getRequest().setAttribute("erroreOperazione", "1");
              throw new GestoreException(
                  "Errore nell'aggiornamento dei valori di PUBBLICA e INVIABILE di ANTICORLOTTI ",null, e);
            }
          }
        }
    } else {
      if (cig != null && !"".equals(cig)) {
        risultato = "2";
      }
    }
    HashMap<String,Object> ret = new HashMap<String,Object>();
    ret.put("risultato", risultato);
    ret.put("cigDuplicati", cigDuplicati);
    return ret;
  }

  /**
   * Metodo che popola le tabelle ANTICORPARTECIP e ANTICORDITTE prelevando i
   * dati dalle view v_dati_ditte_partecipanti, v_dati_imprese_singole e
   * v_dati_imprese_raggruppamenti
   *
   * @param lotto
   * @param gara
   * @param idAnticorlotti
   *
   * @throws GestoreException
   */
  @SuppressWarnings("unchecked")
  private void insertFiglieAnticorLottiDaLotto(String lotto, Long idAnticorlotti) throws GestoreException {

    Long idAnticorpartecip = null;
    Long idAnticorditte = null;

    String selectPartecipanti = "select ditta,tipo, ragsoc, isaggiudicataria from v_dati_ditte_partecipanti where lotto=?";
    // Gestione inserimento in ANTICORPARTECIP
    List<Vector<JdbcParametro>> listaDittePartecipanti = null;
    try {
      listaDittePartecipanti = this.sqlManager.getListVector(selectPartecipanti, new Object[] { lotto });
    } catch (SQLException e) {
      //this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException("Errore nella lettura di v_dati_ditte_partecipanti ", null, e);
    }
    if (listaDittePartecipanti != null && listaDittePartecipanti.size() > 0) {
      String codiceDittaPartecip = null;
      Long tipo = null;
      String ragSoc = null;
      String insertAnticorpartecip = "insert into anticorpartecip (id, idanticorlotti, tipo, aggiudicataria, ragsoc) values (?,?,?,?,?)";
      Long aggiudicataria = null;
      String selectDettaglioDitte = "";
      for (int j = 0; j < listaDittePartecipanti.size(); j++) {
        codiceDittaPartecip = SqlManager.getValueFromVectorParam(
            listaDittePartecipanti.get(j), 0).getStringValue();
        tipo = SqlManager.getValueFromVectorParam(
            listaDittePartecipanti.get(j), 1).longValue();
        ragSoc = SqlManager.getValueFromVectorParam(
            listaDittePartecipanti.get(j), 2).getStringValue();
        aggiudicataria = SqlManager.getValueFromVectorParam(
            listaDittePartecipanti.get(j), 3).longValue();

        idAnticorpartecip = new Long(this.genChiaviManager.getNextId("ANTICORPARTECIP"));

        ragSoc = StringUtils.left(ragSoc, 250);

        try {
          this.sqlManager.update(insertAnticorpartecip, new Object[] {
              idAnticorpartecip, idAnticorlotti, tipo, aggiudicataria.toString(), ragSoc });
        } catch (SQLException e) {
          //this.getRequest().setAttribute("erroreOperazione", "1");
          throw new GestoreException("Errore nell'inserimento in ANTICORPARTECIP ", null, e);
        }

        if (new Long(1).equals(tipo))
          selectDettaglioDitte = "select ragsoc, codfisc, piva, nazione, tipoassociazione,tipo_ruolo from v_dati_imprese_singole where cod_ditta=?  ";
        else
          selectDettaglioDitte = "select ragsoc, codfisc, piva, nazione, tipoassociazione,tipo_ruolo from v_dati_imprese_raggruppamenti where cod_associazione=? ";

        // Gestione inserimento in ANTICORDITTE
        List<Vector<JdbcParametro>> listaDettaglioDitte = null;
        try {
          listaDettaglioDitte = this.sqlManager.getListVector(
              selectDettaglioDitte, new Object[] { codiceDittaPartecip });
        } catch (SQLException e) {
          //this.getRequest().setAttribute("erroreOperazione", "1");
          throw new GestoreException("Errore nella lettura di v_dati_imprese ",
              null, e);
        }

        if (listaDettaglioDitte != null && listaDettaglioDitte.size() > 0) {
          String ragioneSociale = null;
          String codiceFiscale = null;
          String piva = null;
          String nazione = null;
          Long tipoAssociazione = null;
          String insertAnticorditte = "insert into anticorditte (id, idanticorpartecip, ragsoc, codfisc, idfiscest, ruolo) values (?,?,?,?,?,?)";
          String codfiscAnticorDitte = null;
          String idFiscale = null;
          Long ruolo = null;
          String tipoRuolo = null;
          for (int k = 0; k < listaDettaglioDitte.size(); k++) {
            ragioneSociale = SqlManager.getValueFromVectorParam(
                listaDettaglioDitte.get(k), 0).getStringValue();
            codiceFiscale = SqlManager.getValueFromVectorParam(
                listaDettaglioDitte.get(k), 1).getStringValue();
            piva = SqlManager.getValueFromVectorParam(
                listaDettaglioDitte.get(k), 2).getStringValue();
            nazione = SqlManager.getValueFromVectorParam(
                listaDettaglioDitte.get(k), 3).getStringValue();
            tipoAssociazione = SqlManager.getValueFromVectorParam(
                listaDettaglioDitte.get(k), 4).longValue();
            tipoRuolo = SqlManager.getValueFromVectorParam(
                listaDettaglioDitte.get(k), 5).getStringValue();
            //Considera il codice Fiscale e se nullo la partita Iva, qualsiasi sia la nazione
            if (codiceFiscale == null || "".equals(codiceFiscale))
              codiceFiscale = piva;
            if ("ITALIA".equals(nazione)) {
              codfiscAnticorDitte = codiceFiscale;
              idFiscale = null;
            } else {
              codfiscAnticorDitte = null;
              idFiscale = codiceFiscale;
            }

            if (new Long(3).equals(tipoAssociazione) || new Long(10).equals(tipoAssociazione)){
              if ("1".equals(tipoRuolo))
                ruolo = new Long(2); // 02-MANDATARIA
              else
                ruolo = new Long(1); // 01-MANDANTE
            }

            idAnticorditte = new Long(genChiaviManager.getNextId("ANTICORDITTE"));

            ragioneSociale = StringUtils.left(ragioneSociale, 250);

            try {
              this.sqlManager.update(insertAnticorditte, new Object[] {
                  idAnticorditte, idAnticorpartecip, ragioneSociale,
                  codfiscAnticorDitte, idFiscale, ruolo });
            } catch (SQLException e) {
              //this.getRequest().setAttribute("erroreOperazione", "1");
              throw new GestoreException("Errore nell'inserimento in ANTICORDITTE ", null, e);
            }
          }
        }
      }
    }
  }

  /**
   * Metodo che effettua l'import nell'adempimento dell'anno di riferimento
   * dei lotti dell'adempimento dell'anno precedente
   *
   * @param annorif
   * @param ufficioIntestatario
   * @param idAnticor
   *
   * @throws GestoreException
   */
  public boolean insertLottiAdempimentoAnnoPrecedente(Long annorif, String ufficioIntestatario, Long idAnticor) throws GestoreException{

    String insertAnticorlotti = "insert into anticorlotti(id, idanticor, daannoprec, cig, codfiscprop, denomprop, oggetto, sceltacontr, impaggiudic, datainizio, "
      + "dataultimazione, impsommeliq,pubblica,inviabile,stato,lottoinbo,idlotto,codfisresp, nomeresp,idcontratto) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

    String completato = null;
    String esportato = null;
    String pubblica = "";
    String daAnnoPrec = "";
    boolean msgWarningAnnoPrec = false;

      // IMPORT di ANTICORLOTTI, ANTICORPARTECIP e ANTICORDITTE
      // Si effettua l'import solo se l'adempimento precedente è completato ed
      // esportato
      Long annorifPrec = new Long(annorif.longValue() - 1);
      Vector<?> datiAnticor = null;
      try {
        datiAnticor = this.sqlManager.getVector(
            "select completato, esportato from anticor where annorif=? and codein=?",
            new Object[] { annorifPrec, ufficioIntestatario});
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella lettura dei campi COMPLETATO e ESPORTATO di ANTICOR ",
            null, e);
      }

      if (datiAnticor != null && datiAnticor.size() > 0) {
        completato = SqlManager.getValueFromVectorParam(datiAnticor, 0).getStringValue();
        esportato = SqlManager.getValueFromVectorParam(datiAnticor, 1).getStringValue();
        if (!("1".equals(completato) && "1".equals(esportato))) {
          // Se esistono lotti si deve dare un messaggio di warning
          try {
            Long count = (Long) this.sqlManager.getObject(
                "select count(cig) from anticorlotti,anticor where idanticor= anticor.id and annorif=? and pubblica='1' and codein=? ",
                new Object[] { annorifPrec, ufficioIntestatario });
            if (count != null && count.longValue() > 0)
              msgWarningAnnoPrec = true;
          } catch (SQLException e) {
            throw new GestoreException(
                "Errore nel conteggio dei lotti da importare ", null, e);
          }
        }
      }

      if ("1".equals(completato) && "1".equals(esportato)) {
        /*
         * select =
         * "select cig, codfiscprop, denomprop, oggetto, sceltacontr, impaggiudic, datainizio, dataultimazione, impsommeliq, inviabile, anticorlotti.id "
         * +
         * "from anticorlotti,anticor where idanticor= anticor.id and annorif=? and pubblica=1 and stato<>3 and "
         * ;
         */

        String select = "select cig, codfiscprop, denomprop, oggetto, sceltacontr, impaggiudic, datainizio, dataultimazione, impsommeliq, inviabile, lotti1.id, "
          + " stato, lottoinbo, idlotto, codfisresp, nomeresp,idcontratto from anticorlotti lotti1,anticor where idanticor= anticor.id and annorif=? and pubblica='1' and "
          + " anticor.codein=? and cig not in (select cig from anticorlotti lotti2,anticor where lotti2.idanticor= anticor.id and annorif=? and anticor.codein=? and lotti2.cig is not null) ";

        List<?> listaAnticor = null;
        try {
          listaAnticor = this.sqlManager.getListVector(select, new Object[] {
              annorifPrec, ufficioIntestatario, annorif,ufficioIntestatario });
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore nella lettura dei dati di ANTICORLOTTI riferiti all'anno "
                  + annorifPrec.toString(), null, e);
        }

        if (listaAnticor != null && listaAnticor.size() > 0) {
          String cig = null;
          String codfiscprop = null;
          String denomprop = null;
          String oggetto = null;
          Long sceltacontr = null;
          Double impaggiudic = null;
          java.util.Date dataIn = null;
          java.util.Date dataUltimazione = null;
          Double impsommeliq = null;
          String inviabile = null;
          Long idAnticorlotti = null;
          Long oldIdAnticorLotti = null;
          String selectAnticorPartecip = null;
          Long stato = null;
          String idLotto=null;
          String lottoInBo=null;
          String aggiudicataria=null;
          String codfisresp = null;
          String nomeresp = null;
          String idcontratto = null;
          boolean eseguireControlloDati;
          double importoArrotondato;
          String nrepat = null;
          for (int ii = 0; ii < listaAnticor.size(); ii++) {
            eseguireControlloDati=false;
            cig = (String)SqlManager.getValueFromVectorParam(listaAnticor.get(ii), 0).getValue();
            codfiscprop = (String)SqlManager.getValueFromVectorParam(listaAnticor.get(ii), 1).getValue();
            denomprop = (String)SqlManager.getValueFromVectorParam(listaAnticor.get(ii), 2).getValue();
            oggetto = (String)SqlManager.getValueFromVectorParam(listaAnticor.get(ii),3).getValue();
            sceltacontr = SqlManager.getValueFromVectorParam(listaAnticor.get(ii), 4).longValue();
            impaggiudic = SqlManager.getValueFromVectorParam(listaAnticor.get(ii), 5).doubleValue();
            if (impaggiudic != null) {
              importoArrotondato = UtilityMath.round(impaggiudic, 2);
              impaggiudic = new Double(importoArrotondato);
            }
            dataIn = SqlManager.getValueFromVectorParam(listaAnticor.get(ii), 6).dataValue();
            dataUltimazione = SqlManager.getValueFromVectorParam(listaAnticor.get(ii), 7).dataValue();
            impsommeliq = SqlManager.getValueFromVectorParam(listaAnticor.get(ii), 8).doubleValue();
            if (impsommeliq != null) {
              importoArrotondato = UtilityMath.round(impsommeliq, 2);
              impsommeliq = new Double(importoArrotondato);
            }
            inviabile = (String)SqlManager.getValueFromVectorParam(listaAnticor.get(ii), 9).getValue();
            oldIdAnticorLotti = SqlManager.getValueFromVectorParam(listaAnticor.get(ii), 10).longValue();
            stato = SqlManager.getValueFromVectorParam(listaAnticor.get(ii), 11).longValue();
            lottoInBo = (String)SqlManager.getValueFromVectorParam(listaAnticor.get(ii), 12).getValue();
            idLotto = (String)SqlManager.getValueFromVectorParam(listaAnticor.get(ii), 13).getValue();
            codfisresp = (String)SqlManager.getValueFromVectorParam(listaAnticor.get(ii), 14).getValue();
            nomeresp = (String)SqlManager.getValueFromVectorParam(listaAnticor.get(ii), 15).getValue();
            idcontratto = (String)SqlManager.getValueFromVectorParam(listaAnticor.get(ii), 16).getValue();

            idAnticorlotti = new Long(genChiaviManager.getNextId("ANTICORLOTTI"));
            if ("".equals(idLotto))
              idLotto = null;

            pubblica = "2";
            daAnnoPrec = "1";

            //Confronto fra impsommeliq del lotto dell'adempimento e quello di V_DATI_LOTTI
            try {
              double impsommeGare =0;
              double impsomme = 0;
              //Il confronto sull'importo liquidato viene fatto solo per i lotti collegati a V_DATI_LOTTI
              if (idLotto != null) {
                  Object impsommeliqGare = this.sqlManager.getObject("select impsommeliq from v_dati_lotti where lotto = ?",
                      new Object[]{idLotto});
                  if (impsommeliqGare != null) {
                    if (impsommeliqGare instanceof Double)
                      impsommeGare = ((Double) impsommeliqGare).doubleValue();
                    else if (impsommeliqGare instanceof Long)
                      impsommeGare = (new Double((Long) impsommeliqGare)).doubleValue();
                  }
                  impsommeGare = UtilityMath.round(impsommeGare, 2);
                  if (impsommeliq != null)
                    impsomme = impsommeliq.doubleValue();
                  if (impsommeGare > impsomme)
                    impsommeliq = new Double(impsommeGare);
              }
              int annoUltimazione=0;
              if (dataUltimazione != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dataUltimazione);
                annoUltimazione = cal.get(Calendar.YEAR);
              }
              boolean saltareImport = false;
              try {
                if (idLotto != null) {
                  Vector<?> datiTorn = this.sqlManager.getVector("select altrisog, accqua, aqoper from v_dati_lotti where lotto=?", new Object[]{idLotto});
                if (datiTorn != null && datiTorn.size() > 0) {
                  Long altrisog = SqlManager.getValueFromVectorParam(datiTorn, 0).longValue();
                  String accqua = SqlManager.getValueFromVectorParam(datiTorn, 1).stringValue();
                  Long aqoper = SqlManager.getValueFromVectorParam(datiTorn, 2).longValue();

                  if (altrisog != null && (altrisog.longValue() == 2 || altrisog.longValue() == 3))
                    saltareImport = true;
                  else if ("1".equals(accqua) && new Long(2).equals(aqoper))
                    saltareImport = true;
                }
                }
              } catch (SQLException e) {
                throw new GestoreException(
                    "Errore nella lettura dei dati di TORN del lotto " + idLotto, null, e);
              }
              if ((impsommeGare>impsomme || (stato.longValue()!=3 && (dataUltimazione == null || annoUltimazione >= annorif))) && !saltareImport) {
                daAnnoPrec = "3";
                if (idLotto != null) {
	                Vector<?> datiLotto = this.sqlManager.getVector("select cig,  denomprop, codfiscprop, oggetto, tipgarg, impaggiudic, datainizio, " +
	                        "dataultimazione, esineg, dattoa, aggiudicataria from v_dati_lotti where lotto = ? ", new Object[]{idLotto});
	                if (datiLotto != null && datiLotto.size() > 0) {
	                  cig = (String)SqlManager.getValueFromVectorParam(datiLotto, 0).getValue();
	                  codfiscprop=(String)SqlManager.getValueFromVectorParam(datiLotto, 2).getValue();
	                  denomprop = StringUtils.left((String)SqlManager.getValueFromVectorParam(datiLotto, 1).getValue(), 250);
	                  oggetto = (String)SqlManager.getValueFromVectorParam(datiLotto, 3).getValue();
	                  sceltacontr = this.getSceltaContraente(SqlManager.getValueFromVectorParam(datiLotto, 4).longValue());
	                  impaggiudic =SqlManager.getValueFromVectorParam(datiLotto, 5).doubleValue();
	                  if (impaggiudic != null) {
	                    importoArrotondato = UtilityMath.round(impaggiudic, 2);
	                    impaggiudic = new Double(importoArrotondato);
	                  }
	                  dataIn = SqlManager.getValueFromVectorParam(datiLotto, 6).dataValue();
	                  dataUltimazione = SqlManager.getValueFromVectorParam(datiLotto, 7).dataValue();
	                  stato = null;
	                  Long esineg = SqlManager.getValueFromVectorParam(datiLotto, 8).longValue();
	                  java.util.Date dattoa = SqlManager.getValueFromVectorParam(datiLotto, 9).dataValue();
	                  aggiudicataria = (String)SqlManager.getValueFromVectorParam(datiLotto, 10).getValue();
	                  if (esineg != null)
	                    stato = new Long(3);
	                  else if (dattoa != null)
	                    stato = new Long(2);
	                  else
	                    stato = new Long(1);
	                  pubblica = "2";
	                  inviabile = "2";
	                  lottoInBo = "1";
	                  eseguireControlloDati=true;

	                  if(oggetto!=null && !"".equals(oggetto)){
	                    if(oggetto.indexOf(costanteIdContratto)>0){
	                      int indice = oggetto.indexOf(costanteIdContratto);
	                      idcontratto = oggetto.substring( indice + costanteIdContratto.length());
	                      oggetto = oggetto.substring(0, indice);

	                    }
	                  }
	                }
                }
              }

            } catch (SQLException e) {
              throw new GestoreException(
                  "Errore nella lettura dei dati correnti del lotto " + idLotto, null, e);
            }

            //Se stato<>3 e dataultimazione è nulla daannoprec=3
            //if (stato.longValue()!=3 && dataUltimazione == null)
            //  daAnnoPrec = "3";

            if ("1".equals(inviabile) && "3".equals(daAnnoPrec))
              pubblica = "1";

            try {
              this.sqlManager.update(insertAnticorlotti, new Object[] {
                  idAnticorlotti, idAnticor, daAnnoPrec, cig, codfiscprop,
                  denomprop, oggetto, sceltacontr, impaggiudic, dataIn,
                  dataUltimazione, impsommeliq, pubblica, inviabile, stato,
                  lottoInBo, idLotto, codfisresp, nomeresp,idcontratto });
            } catch (SQLException e) {
              throw new GestoreException(
                  "Errore nell'inserimento in ANTICORLOTTI ", null, e);
            }

            // ANTICORPARTECIP
            if (!eseguireControlloDati) {
              selectAnticorPartecip = "select id, tipo, aggiudicataria, ragsoc from anticorpartecip where idanticorlotti=?";
              List<?> listaDittePartecipanti = null;
              try {
                listaDittePartecipanti = this.sqlManager.getListVector(
                    selectAnticorPartecip, new Object[] { oldIdAnticorLotti });
              } catch (SQLException e) {
                throw new GestoreException(
                    "Errore nella lettura di anticorpartecip ", null, e);
              }
              if (listaDittePartecipanti != null && listaDittePartecipanti.size() > 0) {
                Long oldIdAnticorPartecip = null;
                Long tipo = null;
                aggiudicataria = null;
                String ragsoc = null;
                Long idAnticorpartecip = null;
                String insertAnticorpartecip = "insert into anticorpartecip (id,idanticorlotti,tipo,aggiudicataria,ragsoc) values(?,?,?,?,?)";
                String selectDettaglioDitte = "select ragsoc, codfisc, idfiscest, ruolo from anticorditte where idanticorpartecip=?";
                for (int jj = 0; jj < listaDittePartecipanti.size(); jj++) {
                  oldIdAnticorPartecip = SqlManager.getValueFromVectorParam(
                      listaDittePartecipanti.get(jj), 0).longValue();
                  tipo = SqlManager.getValueFromVectorParam(
                      listaDittePartecipanti.get(jj), 1).longValue();
                  aggiudicataria = SqlManager.getValueFromVectorParam(
                      listaDittePartecipanti.get(jj), 2).getStringValue();
                  ragsoc = SqlManager.getValueFromVectorParam(
                      listaDittePartecipanti.get(jj), 3).getStringValue();

                  idAnticorpartecip = new Long(genChiaviManager.getNextId("ANTICORPARTECIP"));

                  ragsoc = StringUtils.left(ragsoc, 250);

                  try {
                    this.sqlManager.update(insertAnticorpartecip, new Object[] {
                        idAnticorpartecip, idAnticorlotti, tipo, aggiudicataria,
                        ragsoc });
                  } catch (SQLException e) {
                    throw new GestoreException(
                        "Errore nell'inserimento in ANTICORPARTECIP ", null, e);
                  }

                  // ANTICORDITTE
                  List<?> listaDettaglioDitte = null;
                  try {
                    listaDettaglioDitte = this.sqlManager.getListVector(
                        selectDettaglioDitte,
                        new Object[] { oldIdAnticorPartecip });
                  } catch (SQLException e) {
                    throw new GestoreException(
                        "Errore nella lettura di anticorditte ", null, e);
                  }

                  if (listaDettaglioDitte != null
                      && listaDettaglioDitte.size() > 0) {
                    String insertAnticorditte = "insert into anticorditte(id,idanticorpartecip,ragsoc,codfisc,idfiscest,ruolo) values(?,?,?,?,?,?)";
                    String ragioneSociale = null;
                    String codfisc = null;
                    String idfiscest = null;
                    Long ruolo = null;
                    Long idAnticorditte = null;
                    for (int kk = 0; kk < listaDettaglioDitte.size(); kk++) {
                      ragioneSociale = SqlManager.getValueFromVectorParam(
                          listaDettaglioDitte.get(kk), 0).getStringValue();
                      codfisc = SqlManager.getValueFromVectorParam(
                          listaDettaglioDitte.get(kk), 1).getStringValue();
                      idfiscest = SqlManager.getValueFromVectorParam(
                          listaDettaglioDitte.get(kk), 2).getStringValue();
                      ruolo = SqlManager.getValueFromVectorParam(
                          listaDettaglioDitte.get(kk), 3).longValue();
                      idAnticorditte = new Long(genChiaviManager.getNextId("ANTICORDITTE"));
                      ragioneSociale = StringUtils.left(ragioneSociale, 250);

                      try {
                        this.sqlManager.update(insertAnticorditte, new Object[] {
                            idAnticorditte, idAnticorpartecip, ragioneSociale,
                            codfisc, idfiscest, ruolo });
                      } catch (SQLException e) {
                        throw new GestoreException(
                            "Errore nell'inserimento in ANTICORDITTE ", null, e);
                      }

                    }
                  }
                }
              }
            } else {
              if ((new Long(2)).equals(stato))
                this.insertFiglieAnticorLottiDaLotto(idLotto, idAnticorlotti);
            }

            // Controllo dei dati inseriti per valurare i valori di pubblica e
            // inviabile
            if (eseguireControlloDati) {
              HashMap<String,Object> esitoControlli = null;
              String msg = null;
              try {
                esitoControlli = this.controlloDatiAVCP(idAnticorlotti,
                    true, false,ufficioIntestatario);
                if (esitoControlli != null) {
                  Boolean controlloOk = (Boolean) esitoControlli.get("esito");
                  if (controlloOk.booleanValue()) {
                    inviabile = "1";
                    pubblica = "1";
                    this.sqlManager.update(
                        "update anticorlotti set inviabile=?, pubblica=?, testolog=? where id=?",
                        new Object[] { inviabile, pubblica, msg, idAnticorlotti });
                  } else {
                    msg = (String)esitoControlli.get("msg");
                    this.sqlManager.update(
                        "update anticorlotti set testolog=? where id=?",
                        new Object[] { msg, idAnticorlotti });
                  }

                }
              } catch (SQLException e) {
                throw new GestoreException(
                    "Errore nella verifica dei dati del lotto "
                        + idAnticorlotti.toString(), null, e);
              }

            }
          }
        }
      }

      return msgWarningAnnoPrec;

  }

  /**
   * Metodo che effettua il calcolo di modlicg, sfruttando l'algoritmo
   * delle analoghe funzioni javascript presenti nelle pagina gare-pg-datigen.jsp,
   * torn-pg-datigen.jsp e gare-pg-datigen-offertaUnica.jsp
   *
   * @param critlic
   * @param detlic
   * @param calcsoan
   * @param applegregg
   *
   * @retun Long
   */
  public Long getMODLICG(Long critlic, Long detlic, String calcsoan, Long applegregg) {
    Long ret=null;
    if (critlic != null) {
      if (critlic.longValue() == 1 && detlic  != null && calcsoan  != null && !"".equals(calcsoan)) {
          if (detlic.longValue() == 1 && "1".equals(calcsoan)) ret = new Long(13);
          if (detlic.longValue() == 1 && "2".equals(calcsoan)) ret = new Long(1);
          if (detlic.longValue() == 2 && "1".equals(calcsoan)) ret = new Long(13);
          if (detlic.longValue() == 2 && "2".equals(calcsoan)) ret = new Long(1);
          if (detlic.longValue() == 3 && "1".equals(calcsoan)) ret = new Long(14);
          if (detlic.longValue() == 3 && "2".equals(calcsoan)) ret = new Long(5);
          if (detlic.longValue() == 4 && "1".equals(calcsoan)) ret = new Long(13);
          if (detlic.longValue() == 4 && "2".equals(calcsoan)) ret = new Long(1);
          if (detlic.longValue() == 5 && "1".equals(calcsoan)) ret = new Long(13);
          if (detlic.longValue() == 5 && "2".equals(calcsoan)) ret = new Long(1);
          //Regione Sicilia
          if ("1".equals(calcsoan)) {
              if (detlic.longValue() == 1 && applegregg != null &&  applegregg.longValue()== 1) ret = new Long(15);
              if (detlic.longValue() == 2 && applegregg != null &&  applegregg.longValue() == 1) ret = new Long(15);
              if (detlic.longValue() == 3 && applegregg != null &&  applegregg.longValue() == 2) ret = new Long(16);
          }
      }
      if (critlic.longValue() == 2) {
          ret = new Long(6);
      }
  }

    return ret;
  }

  /**
   * Metodo che effettua il calcolo di ITERGA, sfruttando l'algoritmo
   * delle analoghe funzioni javascript presenti nelle pagina gare-pg-datigen.jsp,
   * torn-pg-datigen.jsp
   *
   * @param tipogara
   * @throws SQLException
   * @retun Long
   */
  public Long getITERGA(Long tipogara) throws SQLException{
    Long ret = null;
    List<?> listaValoriA1z04 = this.sqlManager.getListVector("select tab2tip,tab2d2 from tab2" +
        " where tab2cod='A1z04' order by tab2tip", null);
    boolean valoreTrovato= false;
    if (listaValoriA1z04 != null && listaValoriA1z04.size()>0) {

      for (int i=0;i<listaValoriA1z04.size() && !valoreTrovato;i++) {
        String tab2tip = SqlManager.getValueFromVectorParam(listaValoriA1z04.get(i), 0).getStringValue();
        String desc = SqlManager.getValueFromVectorParam(listaValoriA1z04.get(i), 1).getStringValue();
        if (desc.indexOf(",")>0) {
          String vettValori[] = desc.split(",");
          for (int j=0;j<vettValori.length;j++) {
            String desc1 = vettValori[j];
            if (desc1.equals(tipogara.toString())) {
              ret = new Long(tab2tip);
              valoreTrovato= true;
            }
          }
        } else {
          if (desc.equals(tipogara.toString())) {
            ret = new Long(tab2tip);
            valoreTrovato= true;
          }
        }
      }
    }

    if (valoreTrovato == false)
      ret= new Long(2);

    return ret;
  }

  /**
   * Metodo che imposta il campo G_PERMESSI.MERUOLO per le gare
   *
   * @param chiaveGara
   * @param idUtente
   * @param meRuolo
   * @throws SQLException,GestoreException
   *
   */
  public void updateMeruoloG_Permessi(String chiaveGara, Long idUtente, Long meRuolo)throws SQLException, GestoreException {
    //Aggiornamento g_pemessi del proprietario
    String update="update g_permessi set meruolo=? where syscon=? and codgar=? and predef is null and sysrif is null";
    this.sqlManager.update(update, new Object[]{meRuolo,idUtente,chiaveGara});

    //Aggiornamento utenti configurati
    String sql = "select syscon "
      + "from g_permessi "
      + "where sysrif = ? "
      + "and predef = ? "
      + "and codgar is null";

    List<?> listaDefinizioni = this.sqlManager.getListVector(sql, new Object[]{idUtente,new Integer(2)});
    if (listaDefinizioni != null && listaDefinizioni.size() > 0) {
      Long idUtentePermssi = null;
      for (int i = 0; i < listaDefinizioni.size(); i++) {
        idUtentePermssi = SqlManager.getValueFromVectorParam(
            listaDefinizioni.get(i), 0).longValue();
        //Determinazione dell campo USRSYS.MERUOLO
        Long meruolo = new Long(2);
        Object ruoloME = this.sqlManager.getObject("select meruolo from usrsys where syscon=?", new Object[]{idUtentePermssi});
        if (ruoloME != null) {
          meruolo = (Long)ruoloME;
        }
        this.sqlManager.update(update, new Object[]{meruolo,idUtentePermssi,chiaveGara});
      }
      //Si controlla se sono stati inseriti più utenti punti ordinante
      this.controlloEsistenzaPiuPuntiOrdinante(chiaveGara, idUtente);
   }
  }

  /**
   * Metodo che controlla le occorrenze di DOCUMGARA e verifica se devono essere inserite le
   * corrispondenti righe in IMPRDOCG
   *
   * @param codgar
   * @param ngara
   * @param nProgressivoDOCUMGARA
   * @throws GestoreException
   */
  private void updateIMPRDOCGInserimento(String codgar, String ngara, Long nProgressivoDOCUMGARA, Long busta) throws GestoreException{

      try {
    	List<?> ditteGara = this.sqlManager.getListVector("select dittao,acquisizione from ditg where codgar5=? and ngara5=?", new Object[]{codgar,ngara});
        if (ditteGara != null && ditteGara.size() > 0) {
          String select=null;
          Object parametri[] = null;
          Long acquisizione = null;
          for (int j=0; j < ditteGara.size(); j++) {
            String ditta = SqlManager.getValueFromVectorParam(ditteGara.get(j), 0).getStringValue();
            acquisizione = SqlManager.getValueFromVectorParam(ditteGara.get(j), 1).longValue();
            if (!(acquisizione != null && acquisizione.longValue() == 5 && busta != null && busta.longValue() == 4)) {
              //Si considerano le ditte in gara indipendentemente dal tipo impresa
              select="select norddoci from imprdocg where codimp=? and proveni=? and norddoci=? and codgar=? and ngara=?";
              parametri = new Object[5];
              parametri[0]=ditta;
              parametri[1]=new Long(1);
              parametri[2]=nProgressivoDOCUMGARA;
              parametri[3]=codgar;
              parametri[4]=ngara;
              Long norddoci = (Long)this.sqlManager.getObject(select, parametri);
              if (norddoci == null) {
                String gartel = (String) this.sqlManager.getObject(
                    "select gartel from torn where codgar = ?",
                    new Object[] { codgar });
                String doctel = "2";
                if ("1".equals(gartel))
                  doctel="1";
                this.sqlManager.update("insert into IMPRDOCG(CODGAR,CODIMP,NORDDOCI,NGARA,PROVENI,DOCTEL) " +
                        "values(?,?,?,?,?,?)", new Object[] { codgar, ditta,nProgressivoDOCUMGARA, ngara, new Long(1), doctel });
              }
            }
          }
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'inserimento delle occorrenze in IMPRDOCG", null, e);
      }

  }

  /**
   * Metodo che controlla le occorrenze di IMPRDOCG e verifica se non vi è corrispondenza
   * con DOCUMGARA ed in tal caso le cancella
   *
   * @param codgar
   * @param ngara
   * @throws GestoreException
   */
  private void updateIMPRDOCGICancellazione(String codgar, String ngara) throws GestoreException{
    try {
    	List<?> ditteGara = this.sqlManager.getListVector("select dittao,tipimp from ditg,impr where codgar5=? and ngara5=? and dittao=codimp", new Object[]{codgar, ngara});
      if (ditteGara != null && ditteGara.size()>0) {
        for (int i=0;i<ditteGara.size();i++) {
          String ditta = SqlManager.getValueFromVectorParam(ditteGara.get(i), 0).getStringValue();
          List<?> listaNorddoci =  this.sqlManager.getListVector("select norddoci from imprdocg where codgar=? and ngara=? and codimp=? and proveni=?",
              new Object[]{codgar,ngara, ditta, new Long(1)});
          if (listaNorddoci != null && listaNorddoci.size() > 0) {
            for (int j=0; j < listaNorddoci.size(); j++) {
              Long norddoci = SqlManager.getValueFromVectorParam(listaNorddoci.get(j), 0).longValue();
              //String ngara = SqlManager.getValueFromVectorParam(listaNorddoci.get(j), 1).stringValue();
              Long norddocg = (Long)this.sqlManager.getObject("select norddocg from documgara where gruppo=? and codgar=? and norddocg=? ",
                  new Object[]{new Long(3),codgar,norddoci});
              if (norddocg == null) {
                this.sqlManager.update("delete from imprdocg where codgar=? and codimp=? and ngara=? and norddoci=? and proveni=?",
                    new Object[]{codgar, ditta, ngara, norddoci, new Long(1)});
              }
            }
          }
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nella cancellazione delle occorrenze di IMPRDOCG", null, e);
    }
  }

  /**
   * Metodo che effettua l'allineamento delle occorrenze di IMPRDOCG con quelle di DOCUMGARA considerando
   * solo le occorrenze con gruppo=3
   *
   * @param codgar
   * @param ngara
   * @throws GestoreException
   */
  public void updateImprdocgDaDocumgara(String codgar, String ngara) throws GestoreException{
    List<?> listaLotti = null;
    Long bustalotti = null;
    Long genere=null;
    if (ngara == null || "".equals(ngara)) {
      //Nel caso di gara ad offerta unica si deve considerare solo la gara principale
      String selectLotti = "select g.ngara, g.modlicg, g1.valtec from gare g, gare1 g1 where g.codgar1=?";
      try {
        genere = (Long)this.sqlManager.getObject("select genere from v_gare_torn where codgar=? ",new Object[]{codgar});
        bustalotti = (Long)this.sqlManager.getObject("select bustalotti from gare where codgar1=? and ngara=codgar1",new Object[]{codgar});
        if (genere != null && genere.longValue() == 3) {
          if (bustalotti == null || (bustalotti  != null && bustalotti.longValue() != 1))
            selectLotti+=" and g.ngara = g.codgar1";
          else
            selectLotti+=" and g.ngara <> g.codgar1";
        }
        selectLotti += " and g.ngara = g1.ngara";

        listaLotti = this.sqlManager.getListVector(selectLotti,new Object[]{codgar});
      } catch (SQLException e) {
        throw new GestoreException("Errore nella lettura dei lotti della gara", null, e);
      }
    }

    if (ngara != null && !"".equals(ngara)) {
      //Gara a lotto unico o lotto di gara
      this.updateIMPRDOCGICancellazione(codgar, ngara);
    } else {
        if (listaLotti != null && listaLotti.size()>0) {
          for (int j=0;j<listaLotti.size();j++) {
            ngara = SqlManager.getValueFromVectorParam(listaLotti.get(j), 0).getStringValue();
            this.updateIMPRDOCGICancellazione(codgar, ngara);
          }
          //nel caso di bustalotti 1, le occorrenze di IMPRDOCG sono associate sia ai lotti(busta tecnica ed economica)
          //che alla gara con ngara=codgar1, quindi vanno eliminate anche quest'ultime
          if(new Long(1).equals(bustalotti))
            this.updateIMPRDOCGICancellazione(codgar, codgar);
        }
    }

    //Verifica inserimento in IMPRDOCG
    String select="select ngara, norddocg, busta from documgara where codgar=? and gruppo=?";
    try {
      List<?> listaDocumenti = this.sqlManager.getListVector(select,new Object[]{codgar, new Long(3)});
      if (listaDocumenti != null && listaDocumenti.size() > 0) {
        ngara=null;
        Long norddocg = null;
        Long busta =  null;
        Long modlicg = null;
        String valtec = null;
        boolean eseguireInserimento = true;
        for (int i=0; i < listaDocumenti.size(); i++) {
          eseguireInserimento = true;
          ngara = SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 0).getStringValue();
          norddocg = SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 1).longValue();
          busta = SqlManager.getValueFromVectorParam(listaDocumenti.get(i),2).longValue();
          if (ngara != null && !"".equals(ngara)) {
            this.updateIMPRDOCGInserimento(codgar, ngara, norddocg, busta);
          } else {
            if ((bustalotti != null && bustalotti.longValue() == 1 && (busta.longValue() == 2 || busta.longValue() == 3)) || (new Long(1)).equals(genere)) {
              if (listaLotti != null && listaLotti.size() > 0) {
                for (int j=0; j < listaLotti.size(); j++) {
                  eseguireInserimento = true;
                  ngara = SqlManager.getValueFromVectorParam(listaLotti.get(j), 0).getStringValue();
                  if (busta.longValue() == 2) {
                    modlicg = SqlManager.getValueFromVectorParam(listaLotti.get(j), 1).longValue();
                    valtec = SqlManager.getValueFromVectorParam(listaLotti.get(j), 2).getStringValue();
                    if (!((modlicg != null && modlicg.longValue() == 6) || "1".equals(valtec)))
                      eseguireInserimento = false;
                  }
                  if (eseguireInserimento)
                    this.updateIMPRDOCGInserimento(codgar, ngara, norddocg, busta);
                }
              }
            } else
              this.updateIMPRDOCGInserimento(codgar, codgar, norddocg, busta);
          }
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della DOCUMGARA", null, e);
    }
  }

  /**
   * Per determinare la modalità di selezione ditte viene fatto un primo controllo
   * sugli ou235,ou236 e ou237. Se non è valorizzato nessuno di questi tre
   * viene letto il tabellato A1101.
   * I valori possibili sono "MAN"(manuale), "AUTO" (automatica) e  "MISTA"
   * @param profiloUtente
   * @return String
   */
  public String getModalitaSelezioneDitteElenco(ProfiloUtente profiloUtente) {
    String modalita = "MAN";
    OpzioniUtente opzioniUtente = new OpzioniUtente( profiloUtente.getFunzioniUtenteAbilitate());
    if(opzioniUtente.isOpzionePresente(CostantiGeneraliAccount.SELEZIONE_AUTOMATICA_OPERATORE_ELENCO)){
      modalita = "AUTO";
    }else if(opzioniUtente.isOpzionePresente(CostantiGeneraliAccount.SELEZIONE_MANUALE_OPERATORE_ELENCO)){
      modalita = "MAN";
    }else if(opzioniUtente.isOpzionePresente(CostantiGeneraliAccount.SELEZIONE_AUTOMATICA_MANUALE_OPERATORE_ELENCO)){
      modalita = "MISTA";
    }else {
      List<Tabellato> listaTabellato = tabellatiManager.getTabellato("A1101");
      if (listaTabellato != null && listaTabellato.size() > 0) {
        String descrTab = (listaTabellato.get(0)).getDescTabellato();
        if (descrTab != null && descrTab.startsWith("1"))
          modalita = "AUTO";
        else if(descrTab != null && descrTab.startsWith("2"))
          modalita = "MISTA";
      }
    }
    return modalita;
  }

  /**
   * il metodo legge l'entita CONFOPECO per stabilire se vi sono le condizioni per associare
   * gli elenchi alla gara
   *
   * @param codice
   * @param entita
   * @return 1 si possono associare gli elenchi,
   *         2 no
   *
   * @throws SQLException
   */

  public String getPresenzaElencoOperatori(String codice, String entita) throws SQLException{
    String select="";
    String ret="1";

    if ("GARE".equals(entita)) {
      select="select TIPGARG,IMPAPP,TIPGEN from gare,torn where gare.ngara=? and gare.codgar1 = torn.codgar";
    } else {
      select="select TIPGAR,IMPTOR,TIPGEN from torn where torn.codgar=?";
    }


      Long numElenchi = (Long) this.sqlManager.getObject("select count(codgar) from garealbo",null);
      if (numElenchi != null && numElenchi.longValue() > 0) {
        Vector<?> dati = this.sqlManager.getVector(select, new Object[]{codice});
        if (dati != null && dati.size() > 0) {
          Long gareTipgarg=null;
          Double importoTotaleBaseDAsta = null;
          Long gareTipgen=null;
          if (((JdbcParametro)dati.get(0)).getValue() != null) {
            gareTipgarg = (Long)((JdbcParametro)dati.get(0)).getValue();
          }
          if (((JdbcParametro)dati.get(1)).getValue() != null) {
            importoTotaleBaseDAsta = (Double)((JdbcParametro)dati.get(1)).getValue();
          }
          if (importoTotaleBaseDAsta == null)
            importoTotaleBaseDAsta = new Double(0);
          if (((JdbcParametro)dati.get(2)).getValue() != null) {
            gareTipgen = (Long)((JdbcParametro)dati.get(2)).getValue();
          }
          //Considera l'occ. in configurazione con importo inferiore nullo, 0 o minore uguale a quello di gara
          // e importo superiore nullo, 0 o maggiore a quello di gara (esclude l'estremo).
          select="select ISABILITATO from CONFOPECO where TIPOPROCEDURA = ?  and TIPOLOGIA = ? and " +
            "((DAIMPORTO is not null and AIMPORTO is not null and DAIMPORTO <> 0 and AIMPORTO <> 0 and DAIMPORTO <= ? and AIMPORTO > ? ) or " +
            "((DAIMPORTO is null or DAIMPORTO = 0) and AIMPORTO > ? ) or ((AIMPORTO is null or AIMPORTO = 0) and DAIMPORTO <= ?) " +
            "or((DAIMPORTO is null or DAIMPORTO =0 ) and ((AIMPORTO is null or AIMPORTO=0)) ))";

          String abilitato = (String) this.sqlManager.getObject(select,new Object[]{gareTipgarg,gareTipgen, importoTotaleBaseDAsta,
              importoTotaleBaseDAsta,importoTotaleBaseDAsta,importoTotaleBaseDAsta});

          if ((abilitato!= null && !"1".equals(abilitato)) | abilitato == null) {
            ret = "2";
          }

        }
      } else {
        ret = "2";
      }
    return ret;
  }

  public String getCodiceElencoDaAssociare(String catiga, Long tipgen, boolean catalogo) throws SQLException{
    String ret = null;
    String tipoelecases = null;
    String select = null;
    Object params[] = new Object[2];


    if (tipgen.longValue() == 1) {
      tipoelecases="'100','110','101','111'";
     }
    else if (tipgen.longValue() == 2) {
      tipoelecases="'10','110','11','111'";
     }
    else if (tipgen.longValue() == 3) {
      tipoelecases="'1','11','101','111'";
     }

    if (catiga != null && !"".equals(catiga)) {
      if(catalogo)
        select = "select GAREALBO.ngara from GAREALBO,MECATALOGO where GAREALBO.ngara = MECATALOGO.ngara and (GAREALBO.DTERMVAL is null or GAREALBO.DTERMVAL >= ?) and GAREALBO.NGARA in (select OPES.NGARA3 from OPES where OPES.CATOFF = ?) ";
      else
        select = "select GAREALBO.ngara from GAREALBO where (GAREALBO.DTERMVAL is null or GAREALBO.DTERMVAL >= ?) and GAREALBO.NGARA in (select OPES.NGARA3 from OPES where OPES.CATOFF = ?) ";
      params = new Object[2];
      params[0] = UtilityDate.getDataOdiernaAsDate();
      params[1] = catiga;
    } else {
      if(catalogo)
        select = "select GAREALBO.ngara from GAREALBO,MECATALOGO where GAREALBO.ngara = MECATALOGO.ngara and  (GAREALBO.DTERMVAL is null or GAREALBO.DTERMVAL >= ? ) and GAREALBO.NGARA in (select ngara from garealbo where tipoele in (" + tipoelecases + "))";
      else
        select = "select GAREALBO.ngara from GAREALBO where (GAREALBO.DTERMVAL is null or GAREALBO.DTERMVAL >= ? ) and GAREALBO.NGARA in (select ngara from garealbo where tipoele in (" + tipoelecases + "))";
      params = new Object[1];
      params[0] = UtilityDate.getDataOdiernaAsDate();
    }

    List<?> datiGarealbo = this.sqlManager.getListVector(select, params);
    if (datiGarealbo != null && datiGarealbo.size() == 1) {
      ret = SqlManager.getValueFromVectorParam(datiGarealbo.get(0), 0).getStringValue();
    }
    return ret;
  }

  /**
   * il metodo calcola il valore del campo ANTICORLOTTI.SCELTACONR
   *
   * @param tipoGara
   * @return Long
   *
   * @throws GestoreException
   */
  private Long getSceltaContraente(Long tipoGara) throws GestoreException{
    Long sceltaContr= null;
    if (tipoGara != null) {
      if (tipoGara.longValue() >= 51
          && tipoGara.longValue() <= 89)
        sceltaContr = tipoGara;
      else {
        List<?> listaValoriTabellatoA1z05 = null;
        try {
          listaValoriTabellatoA1z05 = this.sqlManager.getListVector("select tab2tip, tab2d2 from " +
                "tab2 where tab2cod=? and tab2d2 is not null and tab2d2 like ?", new Object[] {
              "A1z05", "%" + tipoGara.toString() + "%" });
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore nella lettura del tabellato A1z05",
              null, e);
        }
        if (listaValoriTabellatoA1z05 != null && listaValoriTabellatoA1z05.size()>0) {
          for (int j = 0; j < listaValoriTabellatoA1z05.size(); j++) {
            String tab2d2 = SqlManager.getValueFromVectorParam(listaValoriTabellatoA1z05.get(j), 1).getStringValue();
            if (tab2d2.contains(",")) {
              String vetValori[] = tab2d2.split(",");
              for (int z=0; z < vetValori.length; z++ ) {
                if (tipoGara.longValue() == (new Long(vetValori[z]).longValue())) {
                  sceltaContr = new Long(SqlManager.getValueFromVectorParam(listaValoriTabellatoA1z05.get(j), 0).getStringValue());
                  break;
                }
              }
            } else {
              if (tipoGara.longValue() == (new Long(tab2d2).longValue())) {
                sceltaContr = new Long(SqlManager.getValueFromVectorParam(listaValoriTabellatoA1z05.get(j), 0).getStringValue());
                break;
              }
            }
          }
          if (sceltaContr == null)
            sceltaContr = new Long(52);
        }
      }
    }
    return sceltaContr;
  }

	/**
	 * Gestisce l'inserimento della documentazione presente nel documento
	 * Viene restituito il numero di protocollo associato alla comunicazione, numero
	 * di protocollo che viene adoperato per individuare l'occorrenza in WSDOCUMENTI
	 * da collegare alla riga di WSALLEGATI che viene insertia per ogni occorrenza di W_DOCDIG
	 * e la stringa contenente l'elenco dei nomi dei documenti
	 *
	 * @param document container in cui sono presenti i dati dei documenti
	 * @param codiceGara codice della gara
	 * @param codiceImpresa codice dell'imrpesa
	 * @param numGara numero della gara
	 * @param data data di presentazione richiesta
	 * @param ora ora di presentazione richiesta
	 * @param posticipareAcqQfrom
	 * @return Object[]
	 * @throws GestoreException
	 */
	public Object[] insertDocumenti(Object document, String codiceGara,
					String codiceImpresa, String numGara, java.util.Date data, String ora, Long idComunicazione, boolean posticipareAcqQform)
					throws GestoreException {
	    String comnumprot = null;
	    Timestamp comdatprotTimestamp=null;
		ListaDocumentiType listaDocumenti = null;
		boolean aggiornamento=false;
		boolean rinnovo=false;
		if (document instanceof IscrizioneImpresaElencoOperatoriDocument) {
			listaDocumenti = ((IscrizioneImpresaElencoOperatoriDocument) document).getIscrizioneImpresaElencoOperatori().getDocumenti();
		} else if (document instanceof AggiornamentoIscrizioneImpresaElencoOperatoriDocument) {
			listaDocumenti = ((AggiornamentoIscrizioneImpresaElencoOperatoriDocument) document).getAggiornamentoIscrizioneImpresaElencoOperatori().getDocumenti();
			aggiornamento = true;
		} else if (document instanceof RinnovoIscrizioneImpresaElencoOperatoriDocument) {
			listaDocumenti = ((RinnovoIscrizioneImpresaElencoOperatoriDocument) document).getRinnovoIscrizioneImpresaElencoOperatori().getDocumenti();
			rinnovo=true;
		}

		String msgElencoDoc = "Documenti presentati:\r\n";
		if (listaDocumenti != null && listaDocumenti.sizeOfDocumentoArray()>0) {
		  Long idDocumentoProtocollo = null;

		  //Caricamento dati per l'inserimento in WSALLEGATI
          try {
            String dbFunctionDateToString = sqlManager.getDBFunction("DATETIMETOSTRING",
                new String[] { "comdatprot" });
            Vector<?> datiInvcom = this.sqlManager.getVector("select comnumprot,comdatprot, " + dbFunctionDateToString + " from w_invcom where idprg='PA' and idcom=?", new Object[]{idComunicazione});
            if (datiInvcom != null && datiInvcom.size()>0) {
              comnumprot = SqlManager.getValueFromVectorParam(datiInvcom, 0).stringValue();
              Timestamp comdatprot = SqlManager.getValueFromVectorParam(datiInvcom, 1).dataValue();
              if (comnumprot != null && comdatprot!= null) {
                Calendar dataProt = Calendar.getInstance();
                dataProt.setTime(new Date(comdatprot.getTime()));
                int annoProtocollo=dataProt.get(Calendar.YEAR);
                idDocumentoProtocollo = (Long) this.sqlManager.getObject("select id from wsdocumento where numeroprot=? and annoprot=?", new Object[]{comnumprot,new Long(annoProtocollo)});
              }
              String comdatprotString = ((JdbcParametro)datiInvcom.get(2)).getStringValue();
              if(comdatprotString!=null && !"".equals(comdatprotString)){
                comdatprotTimestamp = new Timestamp(UtilityDate.convertiData(comdatprotString, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS).getTime());
            }
            }
          } catch (SQLException e) {
            logger.error("Errore nell'acquisizione della documentazione", e);
            throw new GestoreException("Errore nell'acquisizione della documentazione", null, e);
          }

          String uuid=null;
          String nomeFile = null;
          int idDitgqform=0;
          Long statoDitgqform=null;
          Timestamp dataatt = null;
          Iterator<DocumentoType> iterator = null;
          if(!rinnovo) {
            iterator = ListaDocumentiPortaleUtilities.getIteratore(listaDocumenti.getDocumentoArray());
          }else {
            iterator = Arrays.stream(listaDocumenti.getDocumentoArray()).iterator();
          }

          while(iterator.hasNext()) {
            DocumentoType datoCodificato = iterator.next();
            nomeFile = datoCodificato.getNomeFile();
            String descrizione = datoCodificato.getDescrizione();
            if(!CostantiAppalti.nomeFileQestionario.equals(nomeFile))
              msgElencoDoc+= nomeFile + "\r\n";
            byte[] file = this.getFileFromDocumento(datoCodificato,idComunicazione.toString());
            long id = -1;
            if (datoCodificato.isSetId()) {
              id = datoCodificato.getId();
            }
            uuid = datoCodificato.getUuid();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
              baos.write(file);
            } catch (IOException e1) {
              logger.error("Errore nell'acquisizione della documentazione", e1);
              throw new GestoreException("Errore nell'acquisizione della documentazione", null, e1);
            }

            String select;
            String update;
            Long progressivo = null;
            try {
              if(CostantiAppalti.nomeFileQestionario.equals(nomeFile)) {
                //Gestione iscrizione tramite qform,
                idDitgqform = this.genChiaviManager.getNextId("DITGQFORM");
                if(posticipareAcqQform) {
                  statoDitgqform = new Long(CostantiAppalti.statoDITGQFORMDaAttivare);
                  dataatt = null;
                }else {
                  statoDitgqform = new Long(CostantiAppalti.statoDITGQFORMAttivo);
                  dataatt = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
                  if(aggiornamento) {
                    this.sqlManager.update("update ditgqform set stato=?, datadis=? where ngara= ? and codgar =? and dittao = ? and stato = ?",
                        new Object[] {new Long(CostantiAppalti.statoDITGQFORMSuperato), dataatt,
                            numGara, codiceGara, codiceImpresa, new Long(CostantiAppalti.statoDITGQFORMAttivo)});
                  }
                }
                this.sqlManager.update("insert into ditgqform(id,ngara,codgar,dittao,oggetto,uuid,datapres,stato,dataatt) "
                    + "values(?,?,?,?,?,?,?,?,?)", new Object[] {new Long(idDitgqform), numGara, codiceGara, codiceImpresa, baos.toString(), uuid,
                        new Timestamp(data.getTime()), statoDitgqform, dataatt});

                if(aggiornamento) {
                  //Va gestita la cancellazione dei file elencati nel json
                  this.deleteFileQform(baos.toString(), numGara,  codiceImpresa, new Timestamp(data.getTime()));
                }
              }else {
                //Inserimento in W_DOCDIG
                select = "SELECT MAX(iddocdig) FROM w_docdig WHERE idprg = 'PA'";
                Long nProgressivoW_DOCDIG = (Long) this.sqlManager.getObject(select, null);

                if (nProgressivoW_DOCDIG == null) {
                  nProgressivoW_DOCDIG = new Long(0);
                }

                Long newProgressivoW_DOCDIG = nProgressivoW_DOCDIG + 1;

                Vector<DataColumn> elencoCampiW_DOCDIG = new Vector<DataColumn>();
                String idPrg = "PA";
                elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.IDPRG",
                    new JdbcParametro(JdbcParametro.TIPO_TESTO, idPrg)));
                elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.IDDOCDIG",
                    new JdbcParametro(JdbcParametro.TIPO_NUMERICO, newProgressivoW_DOCDIG)));
                elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.DIGENT",
                    new JdbcParametro(JdbcParametro.TIPO_TESTO, "IMPRDOCG")));
                elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.DIGNOMDOC",
                    new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeFile)));
                elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.DIGOGG",
                    new JdbcParametro(JdbcParametro.TIPO_BINARIO, baos)));

                DataColumnContainer containerW_DOCDIG = new DataColumnContainer(elencoCampiW_DOCDIG);

                containerW_DOCDIG.insert("W_DOCDIG", sqlManager);
                //brutalmente mi faccio la copia dopo la insert
                String sqlUpdate= "update W_DOCDIG set FIRMACHECKTS = (select w2.FIRMACHECKTS from W_DOCDIG w2 where w2.idprg='PA' and w2.digkey1=? and w2.digkey3=?)," +
	                		"FIRMACHECK = (select w2.FIRMACHECK from W_DOCDIG w2 where w2.idprg='PA' and w2.digkey1=? and w2.digkey3=?) " +
	                		"where IDDOCDIG = ? and IDPRG = ?";
                logger.debug("Aggiorno il documento "+newProgressivoW_DOCDIG);
                this.sqlManager.update(sqlUpdate, new Object[]{idComunicazione.toString(),uuid,idComunicazione.toString(),uuid,newProgressivoW_DOCDIG, idPrg});
                logger.debug("Aggiornato il documento "+newProgressivoW_DOCDIG+" con i dati della firma se presenti.");
                //Inserimento in IMPRDOCG
                Long proveni = null;
                if (id > 0) {

                  progressivo = id;

                  //verifico che ci sia prima l'occorrenza in IMPRDOCG
                  select = "SELECT idprg, iddocdg  FROM imprdocg WHERE codgar = ? AND codimp = ? AND norddoci = ? AND proveni = ?";
                  Object occorrezzaImprdocg = this.sqlManager.getVector(select,
                      new Object[]{codiceGara, codiceImpresa, progressivo, (long) 1});

                  if (occorrezzaImprdocg != null) {
                    //si tratta di un documento richiesto
                    String idprg = SqlManager.getValueFromVectorParam(occorrezzaImprdocg, 0).stringValue();
                    Long iddocdg = SqlManager.getValueFromVectorParam(occorrezzaImprdocg, 1).longValue();

                    if (StringUtils.isEmpty(idprg) && iddocdg == null) {
                      //non esiste ancora un riferimento nella imprdocg al documento
                      //richiesto di documgara, quindi creo il legame
                      update = "UPDATE imprdocg SET idprg = ?, iddocdg = ?, datarilascio = ?, orarilascio = ?, doctel = ?, situazdoci=?, uuid = ? "
                          + "WHERE codgar = ? AND codimp = ? AND norddoci = ? AND proveni = ?";
                      this.sqlManager.update(update, new Object[]{"PA", newProgressivoW_DOCDIG,
                          data, ora, "1", new Long(2), uuid, codiceGara, codiceImpresa, progressivo, (long) 1});
                    } else {
                      //anche se si rientra nel caso di documento richiesto dall’ente
                      //(cioè contiene il riferimento a DOCUMGARA), se la corrispondente
                      //occorrenza in IMPRDOCG contiene già un riferimento a un documento
                      //(IDPRG, IDDOCDG.IMPRDOCG), viene inserita una nuova occorrenza
                      //in IMPRDOCG inizializzando la sua descrizione (DESCRIZIONE.IMPRDOCG)
                      //con quella della corrispondente occorrenza in DOCUMGARA.
                      this.insertDocumentoGara(codiceGara, codiceImpresa, progressivo,
                          numGara, newProgressivoW_DOCDIG, proveni, descrizione, data, ora, uuid);
                    }
                  } else {
                    //altrimenti inserisci un'occorrenza nuova in imprdocg per casi di disallineamento
                    this.insertDocumentoGara(codiceGara, codiceImpresa, progressivo,
                        numGara, newProgressivoW_DOCDIG, proveni, descrizione, data, ora, uuid);
                  }
                } else {
                  //inserisci occorrenza in imprdocg
                  progressivo = this.insertDocumentoGara(codiceGara, codiceImpresa, progressivo,
                      numGara, newProgressivoW_DOCDIG, proveni, descrizione, data, ora, uuid);
                }

                //Aggiornamento di W_DOCDIG con il riferimento a IMPRDOCG
                update = "UPDATE w_docdig SET digkey1 = ?, digkey2 = ?, digkey3 = ? WHERE idprg = ? AND iddocdig = ?";
                this.sqlManager.update(update, new Object[]{codiceGara, progressivo.toString(), codiceImpresa, "PA", newProgressivoW_DOCDIG});

                //inserimento in wsallegati
                if (idDocumentoProtocollo != null) {
                  int idWsallegati = this.genChiaviManager.getNextId("WSALLEGATI");
                  this.sqlManager.update("insert into wsallegati(id, entita, key1, key2, idwsdoc) values(?,?,?,?,?)",
                      new Object[]{idWsallegati, "W_DOCDIG", "PA", newProgressivoW_DOCDIG.toString(),idDocumentoProtocollo});
                }
              }
			} catch (Exception e) {
			  logger.error("Errore nell'acquisizione della documentazione", e);
			  throw new GestoreException("Errore nell'acquisizione della documentazione", null, e);
			}
          }

		}else {
		  msgElencoDoc="Nessun documento presentato";
		}
		return new Object[]{comnumprot,comdatprotTimestamp,msgElencoDoc};
	}

	public long insertDocumentoGara(String codiceGara, String codiceImpresa,
					Long progressivo, String numGara, Long newProgressivoW_DOCDIG,
					Long proveni, String descrizione, java.util.Date data, String ora, String uuid) throws SQLException {

		String select;

		if (StringUtils.isEmpty(descrizione)) {
			select = "SELECT descrizione FROM documgara "
							+ "WHERE codgar = ? AND norddocg = ?";
			descrizione = (String) this.sqlManager.getObject(select,
							new Object[]{codiceGara, progressivo});
		}

		select = "SELECT MAX(norddoci) FROM imprdocg WHERE codgar = ? AND codimp = ?";
		Long nProgressivoIMPRDOCG = (Long) this.sqlManager.getObject(select,
						new Object[]{codiceGara, codiceImpresa});

		select = "SELECT MAX(norddocg) FROM documgara WHERE codgar = ?";
		Long nProgressivoDOCUMGARA = (Long) this.sqlManager.getObject(select,
						new Object[]{codiceGara});

		if (nProgressivoIMPRDOCG == null) {
			nProgressivoIMPRDOCG = new Long(0);
		}
		if (nProgressivoDOCUMGARA == null) {
			nProgressivoDOCUMGARA = new Long(0);
		}
		progressivo = nProgressivoDOCUMGARA;
		if (nProgressivoIMPRDOCG > nProgressivoDOCUMGARA) {
			progressivo = nProgressivoIMPRDOCG;
		}

		progressivo++;
		proveni = new Long(2);

		Vector<DataColumn> elencoCampiIMPRDOCG = new Vector<DataColumn>();

		elencoCampiIMPRDOCG.add(new DataColumn("IMPRDOCG.CODGAR",
						new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceGara)));
		elencoCampiIMPRDOCG.add(new DataColumn("IMPRDOCG.NGARA",
						new JdbcParametro(JdbcParametro.TIPO_TESTO, numGara)));
		elencoCampiIMPRDOCG.add(new DataColumn("IMPRDOCG.CODIMP",
						new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceImpresa)));
		elencoCampiIMPRDOCG.add(new DataColumn("IMPRDOCG.NORDDOCI",
						new JdbcParametro(JdbcParametro.TIPO_NUMERICO, progressivo)));
		elencoCampiIMPRDOCG.add(new DataColumn("IMPRDOCG.IDPRG",
						new JdbcParametro(JdbcParametro.TIPO_TESTO, "PA")));
		elencoCampiIMPRDOCG.add(new DataColumn("IMPRDOCG.IDDOCDG",
						new JdbcParametro(JdbcParametro.TIPO_NUMERICO, newProgressivoW_DOCDIG)));
		elencoCampiIMPRDOCG.add(new DataColumn("IMPRDOCG.SITUAZDOCI",
						new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(2))));
		elencoCampiIMPRDOCG.add(new DataColumn("IMPRDOCG.PROVENI",
						new JdbcParametro(JdbcParametro.TIPO_NUMERICO, proveni)));
		elencoCampiIMPRDOCG.add(new DataColumn("IMPRDOCG.DESCRIZIONE",
						new JdbcParametro(JdbcParametro.TIPO_NUMERICO, descrizione)));
		elencoCampiIMPRDOCG.add(new DataColumn("IMPRDOCG.DATARILASCIO",
						new JdbcParametro(JdbcParametro.TIPO_DATA, data)));
		elencoCampiIMPRDOCG.add(new DataColumn("IMPRDOCG.ORARILASCIO",
						new JdbcParametro(JdbcParametro.TIPO_TESTO, ora)));
		elencoCampiIMPRDOCG.add(new DataColumn("IMPRDOCG.DOCTEL",
						new JdbcParametro(JdbcParametro.TIPO_TESTO, "1")));
		elencoCampiIMPRDOCG.add(new DataColumn("IMPRDOCG.UUID",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, uuid)));

		DataColumnContainer containerIMPRDOCG = new DataColumnContainer(elencoCampiIMPRDOCG);
		containerIMPRDOCG.insert("IMPRDOCG", sqlManager);
		return progressivo;
	}

	/**
     * Controlla la duplicazione del codice CIG di una gara, producendo un messaggio
     * contenente l'elenco delle gare in cui è presente il CIG
     *
     * @param cig codice CIG
     * @param gara numero della gara
     * @return Elenco delle gare in cui è presente il CIG
     * @throws GestoreException
     */
	public String controlloUnicitaCIG(String cig, String gara) throws GestoreException{
	    String ret=null;
	    List<?> listaDatiGara = null;
        try {
    	    if (gara == null || "".equals(gara)){
              listaDatiGara = this.sqlManager.getListVector("select ngara, not_gar, tipgarg, codgar1, codiga , genere from gare where codcig=? order by codgar1",
   	              new Object[]{cig});
    	    }else {
              listaDatiGara = this.sqlManager.getListVector("select ngara, not_gar, tipgarg, codgar1, codiga , genere from gare where ngara<>? and codcig=? order by codgar1",
                  new Object[]{gara, cig});
    	    }
	      if (listaDatiGara != null && listaDatiGara.size() > 0) {
	        StringBuffer buf = new StringBuffer("<br><ul>");
	        String oggetto = null;
	        Long tipgarg = null;
	        String tipoGara=null;
	        Long genere = null;
	        String codgar = null;
	        for (int i = 0; i < listaDatiGara.size(); i++) {
	          buf.append("<li style=\"list-style-type: disc;margin-left: 30px;\" >");
	          codgar = SqlManager.getValueFromVectorParam(listaDatiGara.get(i), 3).stringValue();
	          genere = SqlManager.getValueFromVectorParam(listaDatiGara.get(i), 5).longValue();
	          if (genere == null)
	            genere = (Long)sqlManager.getObject("select genere from v_gare_torn where codgar=?", new Object[]{codgar});
	          if (!(new Long(2).equals(genere))&& !(new Long(4).equals(genere))) {
	            buf.append(codgar);
	            buf.append(" - ");
	          }
	          buf.append(SqlManager.getValueFromVectorParam(listaDatiGara.get(i), 0).stringValue());
	          buf.append(" - ");
	          oggetto=SqlManager.getValueFromVectorParam(listaDatiGara.get(i), 1).stringValue();
	          if (oggetto != null && !"".equals(oggetto) &&  oggetto.length()>500)
	            oggetto=oggetto.substring(0,500);
	          buf.append(oggetto);
	          buf.append(" - ");
	          if (!(new Long(4).equals(genere))) {
    	          tipgarg = SqlManager.getValueFromVectorParam(listaDatiGara.get(i), 2).longValue();
    	          tipoGara = tabellatiManager.getDescrTabellato("A2044", tipgarg.toString());
    	          buf.append(tipoGara);
	          } else {
	            buf.append("Ordine di acquisto");
	          }
	          buf.append("</li>");
	        }
	        buf.append("</ul>");
	        ret= buf.toString();
	      }
	    } catch (SQLException e) {
	      throw new GestoreException(
	          "Errore durante l'estrazione dei dati per effettuare la verifica del codice cig",
	          "checkCIG", e);
	    }
	    return ret;
	  }

	/**
	 * Metodo per testare la validita' del codice CIG.
	 * (metodo copiato dalla classe it.eldasoft.sil.dl229.bl.ControlliValidazione229Manager dell'applicazione DL229)
	 *
	 * @param codiceCig
	 * @return Ritorna true se il <i>codiceCig</i> e' formalmente valido, false altrimenti
	 */
	public boolean controlloCodiceCIG(final String codiceCig) {
		boolean result = true;

		String strC1_7 = "";// primi 7 caratteri
	    String strC4_10 = "";// dal 4 al 10 carattere
	    String strK = ""; //Firma
	    long nDecStrK_chk = 0;//Firma decimale
	    long nDecStrK = 0;//controllo della firma decimale

		if (StringUtils.isEmpty(codiceCig) ||
				(StringUtils.isNotEmpty(codiceCig) && codiceCig.length() != 10) ||
						"0000000000".equals(codiceCig)) {
			result = false;
		} else {
		    // Verifico se si tratta di un CIG
			char strC1 = codiceCig.charAt(0); //Estraggo il primo carattere
		    if (StringUtils.isNumeric("" + strC1)) {
		    	// CIG
		    	try {
		    		strK = codiceCig.substring(7,10); //Estraggo la firma
		    		nDecStrK = Integer.parseInt(strK, 16); //trasformo in decimale
		    		strC1_7 = codiceCig.substring(0, 7); //Estraggo la parte significativa
		    		long nStrC1 = Integer.parseInt(strC1_7);
		    		//	Calcola Firma
		    		nDecStrK_chk = ((nStrC1 * 1/1) * 211 % 4091);

		    		if (nDecStrK_chk != nDecStrK) {
				    	// La firma non coincide
				    	result = false;
				    }
		    	} catch(NumberFormatException e) {
		    		// Impossibile calcolare la firma
		    		result = false;
		    	}
		    } else if (Character.toUpperCase(strC1) == 'X' || Character.toUpperCase(strC1) == 'Z' || Character.toUpperCase(strC1) == 'Y') {
		    	// Verifico se si tratta di uno SMART CIG
		    	try {
		    		strK = codiceCig.substring(1,3);//Estraggo la firma
		    		nDecStrK = Integer.parseInt (strK, 16); //trasformo in decimale
		    		strC4_10 = codiceCig.substring(3,10);
		    		long nDecStrC4_10 = Integer.parseInt (strC4_10, 16); //trasformo in decimale
		    		// Calcola Firma
		    		nDecStrK_chk = ((nDecStrC4_10 * 1/1) * 211 % 251);

		    		if (nDecStrK_chk != nDecStrK) {
				    	// La firma non coincide
				    	result = false;
				    }
		    	} catch(NumberFormatException e) {
		    		// Impossibile calcolare la firma
		    		result = false;
		    	}
	    	} else if (!this.controlloCodiceCIGFittizio(codiceCig)) {
	    		// Verifico se si tratta di un CIG fittizio
	    		result = false;
	    	}
		}
	    return result;
	}

	/**
	 * Metodo per testare la validita' del codice CIG fittizio.
	 *
	 * @param codiceCig
	 * @return Ritorna true se il <i>codiceCig</i> e' un codice CIG fittizio, false altrimenti
	 */
	public boolean controlloCodiceCIGFittizio(final String codiceCig) {
		boolean result = true;
		if (StringUtils.isEmpty(codiceCig) || (StringUtils.isNotEmpty(codiceCig) && codiceCig.length() != 10 && "0000000000".equals(codiceCig))) {
			result = false;
		} else if (!(StringUtils.indexOf(codiceCig, "#") == 0 || StringUtils.indexOf(codiceCig, "$") == 0 || StringUtils.indexOf(codiceCig, "NOCIG")==0)) {
			result = false;
		}
	    return result;
	}

	/**
     * Controlla la presenza nella banca dati di una impresa a partire dal codice fiscale e dalla
     * partita iva.
     * Inoltre se l'impresa non è presente nella banca dati ne viene calcolato il codice tramite
     * la codifica automatica.
     *
     * @param partitaiva partitaiva
     * @param codfisc codice fiscale
     * @param gruppoiva
     * @return  String[] String[0] codice dell'impresa
     *                   String[1] modo, ossia 'INS' (se l'impresa non è presente), 'UPDATE' (se presente).
     * @throws GestoreException
     */
	public String[] controlloEsistenzaDitta(String partitaiva,
	    String codfisc, String gruppoiva) throws GestoreException{

  	  String selectCodimp = "";
      String select="";
  	  Object parametri[] = null;
      String codiceDitta = null;
  	  String modo=null;

      Long numImpr = null;
      try{
      if (partitaiva == null || "".equals(partitaiva)) {
        //Gestione nel caso la partita iva sia nulla
        select = "select count(codimp) from impr where upper(cfimp)=? and pivimp is null";
        numImpr = (Long) this.sqlManager.getObject(select,
            new Object[] { codfisc });
        if (numImpr == null || numImpr.longValue() == 0) {
          select = "select count(codimp) from impr where upper(cfimp)=?";
          numImpr = (Long) this.sqlManager.getObject(select,
              new Object[] { codfisc });
          if (numImpr == null || numImpr.longValue() == 0){
            select = "select count(codimp) from impr where upper(pivimp)=?";
            numImpr = (Long) this.sqlManager.getObject(select,
                new Object[] { codfisc });
            if (numImpr != null && numImpr.longValue() == 1){
              selectCodimp = "select codimp from impr where upper(pivimp)=?";
              parametri = new Object[] { codfisc };
            }
          }else if (numImpr != null && numImpr.longValue() == 1){
            selectCodimp = "select codimp from impr where upper(cfimp)=?";
            parametri = new Object[] { codfisc };
          }
        }else if (numImpr != null && numImpr.longValue() == 1) {
          selectCodimp = "select codimp from impr where upper(cfimp)=? and pivimp is null";
          parametri = new Object[] { codfisc };
        }
      } else {
        select = "select count(codimp) from impr where upper(cfimp)=? and upper(pivimp)=?";
        numImpr = (Long) this.sqlManager.getObject(select, new Object[] {
            codfisc, partitaiva });
        if ((numImpr == null || numImpr.longValue() == 0) && !"1".equals(gruppoiva)) {
          select = "select count(codimp) from impr where upper(pivimp)=? and (cfimp is null or upper(cfimp) = ?)";
          numImpr = (Long) this.sqlManager.getObject(select, new Object[] {
              partitaiva, partitaiva });
          if (numImpr == null || numImpr.longValue() == 0) {
            select = "select count(codimp) from impr where upper(cfimp)=? and pivimp is null";
            numImpr = (Long) this.sqlManager.getObject(select,
                new Object[] { codfisc });
            //
            if (numImpr == null || numImpr.longValue() == 0) {
              select = "select count(codimp) from impr where upper(pivimp)=?";
              numImpr = (Long) this.sqlManager.getObject(select,
                  new Object[] { partitaiva });
              if (numImpr == null || numImpr.longValue() == 0) {
                select = "select count(codimp) from impr where upper(cfimp)=?";
                numImpr = (Long) this.sqlManager.getObject(select,
                    new Object[] { codfisc });
                if (numImpr == null || numImpr.longValue() == 0) {
                  select = "select count(codimp) from impr where upper(cfimp)=?";
                  numImpr = (Long) this.sqlManager.getObject(select,
                      new Object[] { partitaiva });
                  if (numImpr == null || numImpr.longValue() == 0) {
                    select = "select count(codimp) from impr where upper(pivimp)=?";
                    numImpr = (Long) this.sqlManager.getObject(select,
                        new Object[] { codfisc });
                    if (numImpr != null && numImpr.longValue() == 1){
                      selectCodimp = "select codimp from impr where upper(pivimp)=?";
                      parametri = new Object[] { codfisc };
                    }
                  } else if (numImpr != null && numImpr.longValue() == 1){
                    selectCodimp = "select codimp from impr where upper(cfimp)=?";
                    parametri = new Object[] { partitaiva };
                  }
                } else if (numImpr != null && numImpr.longValue() == 1){
                  selectCodimp = "select codimp from impr where upper(cfimp)=?";
                  parametri = new Object[] { codfisc };
                }

              }else if (numImpr != null && numImpr.longValue() == 1){
                selectCodimp = "select codimp from impr where upper(pivimp)=?";
                parametri = new Object[] { partitaiva };
              }
            }else if (numImpr != null && numImpr.longValue() == 1) {
              selectCodimp = "select codimp from impr where upper(cfimp)=? and pivimp is null";
              parametri = new Object[] { codfisc };
            }
            //

          } else if (numImpr != null && numImpr.longValue() == 1) {
            selectCodimp = "select codimp from impr where upper(pivimp)=? and (cfimp is null or upper(cfimp) = ?)";
            parametri = new Object[] { partitaiva, partitaiva };
          }
        } else if (numImpr != null && numImpr.longValue() == 1) {
          selectCodimp = "select codimp from impr where upper(cfimp)=? and upper(pivimp)=?";
          parametri = new Object[] { codfisc, partitaiva };
        }
      }

      if (numImpr == null
          || numImpr.longValue() == 0
          || numImpr.longValue() > 1) {
        // Se non esiste nessuna impr con codfisc e piva dati, oppure ne
        // esistono più di 1
        // si inserisce la nuova impresa, determinando il codice con la
        // codifica automatica
        codiceDitta = geneManager.calcolaCodificaAutomatica("IMPR",
            "CODIMP");
        modo = "INS";
      } else {
        //E' stata trova una sola impresesa, si effettua l'aggiornamento
        codiceDitta = (String) this.sqlManager.getObject(selectCodimp,
              parametri);
        modo = "UPDATE";
      }
      }catch (SQLException e) {
        if (codfisc == null)
          codfisc = "null";
        if (partitaiva == null)
          partitaiva = "null";

        throw new GestoreException(
            "Errore durante il controllo dell'esista nella banca dati dell'impresa con codfisc=" + codfisc + " e" +
            		" partita iva = " + partitaiva,null, e);
      }
      return new String[]{codiceDitta,modo};
	}

	/**
     * Viene gestito l'inserimento delle partecipanti di una RT o di un consorzio in IMPR, RAGIMP
     * e RAGDET a partire da un xml.
     *
     * @param lista lista dei partecipanti
     * @param codiceImpresaPadre codice della RT o del consorzio
     * @param tipimp tipo impresa
     * @param idProfilo id dell'utente connesso
     * @param gara codice della gara
     * @throws GestoreException
     */
    public void gestionePartecipanti(ListaPartecipantiRaggruppamentoType lista, String codiceImpresaPadre, Long tipimp,
	    int idProfilo, String gara, String codiceImpresa) throws GestoreException{
	  //Si deve controllare se effettivamente vi sono le partecipanti
	  /*
      TipoPartecipazioneType tipoPartecipazione= document.getTipoPartecipazione();
	  if (!tipoPartecipazione.isSetPartecipantiRaggruppamento())
	    return;
	  */
	  PartecipanteRaggruppamentoType listaPartecipanti[] = lista.getPartecipanteArray();
	  String ragSoc=null;
	  String codFisc=null;
	  String piva = null;
	  Long nazimp = null;
	  String ambitoTerritorale = null;
	  String idFiscaleEstero = null;
	  Double quota;
	  Long tipoImpresa = null;
	  String impresa = null;
	  String modo = null;
	  StringBuffer messaggioNota= new StringBuffer();
	  //Si determina se si tratta di gara o elenco
	  Long genere = null;
	  try {
	    genere=(Long)this.sqlManager.getObject("select genere from gare where ngara=?", new Object[]{gara});
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella lettura del campo GARE.GENERE della gara" + gara, null, e);
      }
	  if (tipimp != null && (tipimp.longValue() == 2 || tipimp.longValue() == 11)) {
		  messaggioNota.append("Per alcune delle ditte consorziate designate come esecutrici per");
		  if (genere != null && genere.longValue() == 10)
		    messaggioNota.append(" l'elenco ");
		  else
		    messaggioNota.append(" la gara ");
		  messaggioNota.append(gara);
	  } else {
		  messaggioNota.append("Per alcune delle ditte componenenti il raggruppamento");
	  }
	  messaggioNota.append(" la ragione sociale fornita da portale" +
	  		" differisce da quella in anagrafica. Di seguito l'elenco delle incongruenze riscontrate" +
	  		" con la ragione sociale fornita da portale e la corrispondente in anagrafica: \n");
	  boolean incongruenzeRagSoc=false;
	  for (int i = 0; i < listaPartecipanti.length; i++) {
	    ragSoc = listaPartecipanti[i].getRagioneSociale();
	    codFisc = listaPartecipanti[i].getCodiceFiscale();
	    piva = listaPartecipanti[i].getPartitaIVA();
	    ambitoTerritorale = listaPartecipanti[i].getAmbitoTerritoriale();
	    if (listaPartecipanti[i].isSetQuota()) {
	      quota = new Double(listaPartecipanti[i].getQuota());
	    } else {
	      quota=null;
	    }
	    tipoImpresa = new Long(listaPartecipanti[i].getTipoImpresa());
	    if("2".equals(ambitoTerritorale)) {
	      idFiscaleEstero = listaPartecipanti[i].getIdFiscaleEstero();
	      codFisc = idFiscaleEstero;
	      piva = idFiscaleEstero;
	    }
	    //Si controlla l'esistenza dell'Impresa solo fra quelle registrate
	    String selectImpreseRegistrate="select codimp from impr,w_puser where codimp=userkey1 and codimp<>? and upper(cfimp)=?";
	    Object par[] = null;
	    if (piva != null && !"".equals(piva)) {
	      selectImpreseRegistrate+=" and pivimp=?";
	      par = new Object[]{codiceImpresa,codFisc.toUpperCase(),piva};
	    } else {
	      selectImpreseRegistrate+=" and pivimp is null";
	      par = new Object[]{codiceImpresa,codFisc.toUpperCase()};
	    }
	    try {
          String impresaRegistrata = (String)this.sqlManager.getObject(selectImpreseRegistrate, par);
          if (impresaRegistrata == null || "".equals(impresaRegistrata)) {
            //L'impresa non è presente fra le imprese registrate
            //Si controllano le imprese nella banca dati
            String dati[] = this.controlloEsistenzaDitta( piva, codFisc, "");
            impresa = dati[0];
            modo = dati[1];
          } else {
            modo = "UPDATE";
            impresa = impresaRegistrata;
          }
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore nel controllo della presenza in database della componente con cfimp=" + codFisc, null, e);
        }

	    String nomest = null;
        String nomimp = null;
	    if ("INS".equals(modo)) {
	      nomimp = ragSoc;
	      if (nomimp.length() > 61)
	        nomimp = nomimp.substring(0, 60);
	      if (listaPartecipanti[i].isSetNazione()) {
	        String nazione = listaPartecipanti[i].getNazione();
	        try {
	            nazimp = (Long) this.sqlManager.getObject("select tab1tip from tab1 where tab1cod='Ag010' and upper(tab1desc) = ?",
	                new Object[] { nazione.toUpperCase() });
	        } catch (SQLException e) {
	            throw new GestoreException(
	                "Errore nel determinare il valore del tabellato per la nazione " + nazione, null, e);
	        }
	      }else
	        nazimp = null;

	      String update = "insert into IMPR (CODIMP,NOMIMP,NOMEST,CFIMP,PIVIMP,TIPIMP,NAZIMP) values (?,?,?,?,?,?,?) ";
          try {
            this.sqlManager.update(update, new Object[] { impresa,
                nomimp, ragSoc, codFisc, piva, tipoImpresa, nazimp});
          } catch (SQLException e) {
            throw new GestoreException(
                "Errore nell'inserimento dell'impresa " + impresa + " in IMPR", null, e);
          }
	    } else {
	      try {
            Vector<?> datiRagsoc = this.sqlManager.getVector("select nomest,nomimp from impr where codimp=?", new Object[]{impresa});
            if (datiRagsoc != null && datiRagsoc.size()>0) {
              nomest = SqlManager.getValueFromVectorParam(datiRagsoc, 0).getStringValue();
              nomimp = SqlManager.getValueFromVectorParam(datiRagsoc, 1).getStringValue();
            }
            if (nomest == null)
              nomest="";
            String confronto1=nomest.toUpperCase();
            String confronto2=ragSoc.toUpperCase();
            confronto1 = confronto1.replaceAll ("[ \\p{Punct}]", "");  //elimino gli spazi e i segni di punteggiatura
            confronto2 = confronto2.replaceAll ("[ \\p{Punct}]", "");  //elimino gli spazi e i segni di punteggiatura
            if (!confronto1.equals(confronto2)) {
              incongruenzeRagSoc=true;
              messaggioNota.append("- ");
              messaggioNota.append(ragSoc).append(" = ");
              messaggioNota.append(nomest).append(" (cod.anagrafica ");
              messaggioNota.append(impresa).append(")\n");
            }
          } catch (SQLException e) {
            throw new GestoreException(
                "Errore nella lettura dei dati dell'impresa " + impresa + " in IMPR", null, e);
          }
	    }
	    //Se l'impresa non fa parte della RT o del consorzio, si deve inserire in RAGIMP
	    Long conteggio = geneManager.countOccorrenze("RAGIMP", "CODIME9=? AND CODDIC=?", new Object[]{codiceImpresaPadre,impresa});
	    if (conteggio == null || conteggio.longValue() == 0) {
	      try {
            this.sqlManager.update("insert into ragimp(codime9,coddic,nomdic,quodic,impman) values(?,?,?,?,?)", new Object[]{codiceImpresaPadre,impresa,
                nomimp,quota,"2"});
          } catch (SQLException e) {
            throw new GestoreException(
                "Errore nell'inserimento dell'impresa " + impresa + " in RAGIMP", null, e);
          }
	    }

	    //Se l'impresa è un consorzio (tipimp=2,11) si devono caricare in ragdet le componenti del consorzio
        //della ditta
        try {
          if (tipimp != null && (tipimp.longValue() == 2 || tipimp.longValue() == 11)) {
            Long numdic= (Long)this.sqlManager.getObject("select max(numdic) from ragdet where codimp=? and coddic=?",  new Object[]{codiceImpresaPadre,impresa});
            if (numdic == null)
              numdic = new Long(0);
            else
              numdic = new Long(numdic.longValue() + 1);

            this.sqlManager.update("insert into ragdet(codimp,coddic,numdic,ngara) values(?,?,?,?)", new Object[]{codiceImpresaPadre,impresa,numdic,gara});
          }
        } catch (SQLException e) {
          throw new GestoreException("Errore nell'inserimento delle componenti del raggruppamento della gara " + gara, null, e);
        }

	  }
	  if (incongruenzeRagSoc) {
	    //Inserimento della nota in G_NOTEAVVISI
	    try{
	      Long notecod = null;
	      // Operatore (utente di USRSYS che ha avuto accesso all'applicativo)
	      Long syscon = null;
	      if(idProfilo!=-1)
	        syscon = new Long(idProfilo);
	      else{
	        syscon = new Long(48);
	      }
	      Date datains = new Date(UtilityDate.getDataOdiernaAsDate().getTime());

	      // Aggiornamento dati Impresa
	      Vector<DataColumn> elencoCampiNOTEAVVISI = new Vector<DataColumn>();


	        notecod = (Long) this.sqlManager.getObject(
	            "select max(notecod) from g_noteavvisi", null);
	        if (notecod == null) {
	          notecod = new Long(0);
	        }
	        notecod = new Long(notecod.longValue() + 1);
	      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.NOTECOD",
	          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, notecod)));
	      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.NOTEPRG",
	          new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")));
	      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.NOTEENT",
	          new JdbcParametro(JdbcParametro.TIPO_TESTO, "IMPR")));
	      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.NOTEKEY1",
	          new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceImpresaPadre)));
	      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.AUTORENOTA",
	          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, syscon)));
	      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.STATONOTA",
	          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1))));
	      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.TIPONOTA",
	          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(3))));
	      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.DATANOTA",
	            new JdbcParametro(JdbcParametro.TIPO_DATA, datains)));
	      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.TITOLONOTA",
	          new JdbcParametro(JdbcParametro.TIPO_TESTO, "Notifica incongruenza dati in acquisizione da portale")));
	      elencoCampiNOTEAVVISI.add(new DataColumn("G_NOTEAVVISI.TESTONOTA",
	          new JdbcParametro(JdbcParametro.TIPO_TESTO, messaggioNota.toString())));

	      DataColumnContainer containerNOTEAVVISI = new DataColumnContainer(
	          elencoCampiNOTEAVVISI);

	      containerNOTEAVVISI.getColumn("G_NOTEAVVISI.NOTECOD").setObjectOriginalValue(
          "0");
          containerNOTEAVVISI.getColumn("G_NOTEAVVISI.NOTECOD").setChiave(true);
          containerNOTEAVVISI.insert("G_NOTEAVVISI", sqlManager);

	    }catch(SQLException e) {
          throw new GestoreException(
              "Errore nell'inserimento della nota ", null, e);
        }
	  }
	}

    /**
     * Viene controllato se una ditta è presente fra le ditte di un elenco, sia direttamente sia come mandataria fra le RT.
     *
     * @param codiceDitta
     * @param numeroElenco
     * @param codiceElenco
     * @param codiceRaggruppamento
     * @return  String[2], il primo elemento contiente
     *            0 ditta non presente in elenco
     *            1 ditta presente
     *            2 ditta presente in più RT dell'elenco
     *          il secondo elemento il codice della ditta trovato
     * @throws GestoreException
     */
    public String[] controlloEsistenzaDittaElencoGara(String codiceDitta, String numeroElenco, String codiceElenco, String codiceRaggruppamento) throws GestoreException{
      String datiRet[] = new String[2];
      //Si controlla se la ditta è presente direttamene nell'elenco
      Long conteggio = null;
      Long genere = null;
      try {
        genere = (Long)this.sqlManager.getObject("select genere from v_gare_torn where codgar=?", new Object[]{codiceElenco});
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella lettura del campo V_GARE_TORN.GENERE per gara" + codiceElenco, null, e);
      }

      //Nel caso ad offerte distinte si deve dare la possibilità di inserire la stessa ditta in lotti differenti e
      //si deve evitare che se in un lotto ho una ditta, in un altro non si possa inserire un RTI con mandataria quella ditta
      if (new Long(1).equals(genere) && !codiceDitta.equals(codiceRaggruppamento) )
        conteggio = geneManager.countOccorrenze("DITG", "DITTAO=? AND CODGAR5=?", new Object[]{codiceDitta, codiceElenco});
      else
        conteggio = geneManager.countOccorrenze("DITG", "DITTAO=? AND CODGAR5=? and NGARA5=?", new Object[]{codiceDitta, codiceElenco,numeroElenco});


      if (conteggio != null && conteggio.longValue() == 1) {
        datiRet[1] = codiceDitta;
        datiRet[0] = "1";
      } else {
        //Si controlla se la ditta è presente come mandataria di una RT dell'elenco
        String select="select count(codimp) from impr,ragimp,edit where impr.codimp=edit.codime and impr.codimp=ragimp.codime9 and " +
          " edit.codgar4=? and ragimp.coddic=? and ragimp.impman='1'";

        //Nel caso di gara a lotti con offerte distinte si deve considerare il fatto che le ditte si possono trovare in lotti differenti
        //quindi si aggiunge la condizione che il codice del raggruppamento deve essere diverso da quello in esame
        if (new Long(1).equals(genere) && codiceRaggruppamento != null)
          select+= " and ragimp.codime9!='" + codiceRaggruppamento + "'";
        try {
          conteggio = (Long)this.sqlManager.getObject(select, new Object[]{codiceElenco,codiceDitta});
          if (conteggio == null || conteggio.longValue() == 0) {
            datiRet[0] = "0";
            datiRet[1] = codiceDitta;
          } else if (conteggio != null) {
            select="select codime9 from impr,ragimp,edit where impr.codimp=edit.codime and impr.codimp=ragimp.codime9 and " +
              " edit.codgar4=? and ragimp.coddic=? and ragimp.impman='1'";
            if (new Long(1).equals(genere) && codiceRaggruppamento != null)
              select+= " and ragimp.codime9!='" + codiceRaggruppamento + "'";

            datiRet[1] = (String)this.sqlManager.getObject(select, new Object[]{codiceElenco,codiceDitta});
            if (conteggio.longValue() == 1) {
              datiRet[0] = "1";
            } else {
              datiRet[0] = "2";
            }
          }
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore nel controllo dell'esistenza della ditta " + codiceDitta + " fra le mandatarie delle RT dell'elenco" + numeroElenco, null, e);
        }
      }
      return datiRet;
    }

    /**
     * Determina la percentuale della cauzione provvisoria
     *
     * @param tipoAppalto
     * @return Double
     * @throws SQLException
     */
    public Double initPGAROF(Long tipgen) throws SQLException {
      Double percentuale = null;
      String tabellato = PgManager.getTabellatoPercCauzioneProvvisoria(tipgen.intValue());
      String sql = "select tab1desc from tab1 where tab1cod = ? and tab1tip = 1";

      String descrPercentuale = (String) this.sqlManager.getObject(sql,
          new Object[] { tabellato });
      percentuale = UtilityNumeri.convertiDouble(descrPercentuale,
          UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE);

      return percentuale;
    }

    /**
     * Esegue il calcolo della cauzione provvisoria in base alla percentuale,
     * all'importo base di gara, ed al tipo di arrotondamento previsto
     *
     * @param impapp
     * @param pgaroff
     * @param tipgen
     * @return Double
     * @throws GestoreException
     */
    public Double calcolaGAROFF(Double impapp, Double pgaroff, Long tipgen) throws GestoreException{
      Double ret= null;
      if (pgaroff != null) {
        double importoAppalto = 0;
        if (impapp != null)
          importoAppalto = impapp.doubleValue();

        double percentuale = 0;
        if (pgaroff != null)
          percentuale = pgaroff.doubleValue();

        int tipgenInt = tipgen.intValue();
        int numeroDecimali = this.getArrotondamentoCauzioneProvvisoria(tipgenInt);

        double importoCauzioneProvvisoria = UtilityMath.round(importoAppalto
            * percentuale
            / 100., numeroDecimali);
        ret = new Double(importoCauzioneProvvisoria);
      }
      return ret;
    }


    /**
     * Esegue il calcolo dell'importo offerto da una ditta a partire dal ribasso
     *
     * @param impapp
     * @param pgaroff
     * @param tipgen
     * @return Double
     * @throws GestoreException
     */
    public double calcolaImportoOfferto(double impapp, double impsic,double impnrl, double onprge,
        double ribauo, String sicinc, String onsogrib) {

      double ret = impapp - impsic - impnrl;
      if (!"1".equals(onsogrib))
        ret-=onprge;

      ret*=(1+ribauo/100);
      ret+=impnrl;
      if ("1".equals(sicinc))
        ret+=impsic;
      if (!"1".equals(onsogrib))
        ret+=onprge;

      return ret;
    }

    public void controlloEsistenzaPiuPuntiOrdinante(String chiaveGara, Long idUtente) throws SQLException{
      Long numeroPuntiOrdinante = (Long)this.sqlManager.getObject("select count(numper) from g_permessi where codgar=? and meruolo=?", new Object[]{chiaveGara,new Long(1)});
      if (numeroPuntiOrdinante != null && numeroPuntiOrdinante.longValue()>1) {
        String valore = tabellatiManager.getDescrTabellato("A1137", "1");
        if (valore != null && !"".equals(valore))
          valore = valore.substring(0, 1);
        if ("1".equals(valore)){
          Long numper = (Long)this.sqlManager.getObject("select numper from g_permessi where codgar=? and syscon=?", new Object[]{chiaveGara,idUtente});
          if(numper!=null){
            this.sqlManager.update("update g_permessi set meruolo=? where codgar=? and meruolo=? and numper!=?", new Object[]{new Long(2),chiaveGara, new Long(1),numper});
          }else
          this.sqlManager.update("update g_permessi set meruolo=? where codgar=? and meruolo=?", new Object[]{new Long(2),chiaveGara, new Long(1)});
      }
    }
    }



    /**
     * Viene costruito il filtro per le comunicazioni da leggere
     *
     * @param profilo da gestire, valori :
     *          1 - Gare
     *          2 - Elenchi
     *          3 - Cataloghi
     *          4 - Ricerche di mercato
     *          5 - Avvisi
     *          6 - Protocollo
     *          7 - Affidamenti
     * @param filtroUtente
     * @param filtroTipoGra (solo per profilo 1)
     * @param visGareLotti (solo per profilo 1)
     * @param visGareOffertaUnica (solo per profilo 1)
     * @param visGareLottoUnico (solo per profilo 1)
     * @param profiloWEb (solo per profilo 1)
     * @param filtroUffint
     */
    public String getFiltroComunicazioneDaLeggere(String profilo, String filtroUtente, String filtroTipoGara, boolean visGareLotti,
        boolean visGareOffertaUnica, boolean visGareLottoUnico, String profiloWEb, String tipoOperazione, String filtroUffint, String filtroProfilo) {
      String entita = "";
      String chiave = "";
      String filtroComent = "";
      if ("10".equals(profilo)) {
    	  filtroComent += " and coment = 'G1STIPULA' ";
      }else {
    	  filtroComent += " and coment is null ";
      }

      String filtro="from w_invcom , ENTITA  where comkey2=CHIAVE and comtipo='FS12' and comdatlet is null" + filtroComent + "and comstato='3' ";
      if ("sel".equals(tipoOperazione))
        filtro="from w_invcom , ENTITA, w_puser  where comkey2=CHIAVE and comtipo='FS12' and comdatlet is null" + filtroComent + "and comstato='3' and w_puser.usernome = w_invcom.comkey1" ;
      if ("1".equals(profilo)) {
        entita = "v_gare_torn";
        chiave = "codice";
        //Inserimento filtro sull'utente
        if (filtroUtente != null && !"".equals(filtroUtente))
          filtro += " and " + filtroUtente;
        //Inserimento filtro su profilo attivo
        if (profiloWEb != null && !"".equals(profiloWEb))
          filtro+=" and profiloweb=" +  profiloWEb;
        //Inserimento filtro tipo gara
        if (filtroTipoGara != null && !"".equals(filtroTipoGara))
          filtro+=" and " +  filtroTipoGara;
        //Filtri sul genere della gara
        if (!visGareLotti)
          filtro += " and genere <>1";
        if (!visGareOffertaUnica)
          filtro += " and genere <>3";
        if (!visGareLottoUnico)
          filtro += " and genere <>2";
        if (filtroUffint != null && !"".equals(filtroUffint))
          filtro += " and  CENINT = '" + filtroUffint + "'";
      }else if ("8".equals(profilo)) {
        entita = "v_gare_profilo";
        chiave = "codice";
        if (filtroUffint != null && !"".equals(filtroUffint))
          filtro += filtroUffint;
        if (filtroProfilo != null && !"".equals(filtroProfilo))
          filtro += filtroProfilo;
        //Inserimento filtro sull'utente
        if (filtroUtente != null && !"".equals(filtroUtente))
          filtro += " and " + filtroUtente;
      }else if ("2".equals(profilo)) {
        entita = "v_gare_eleditte";
        chiave = "codice";
        //Inserimento filtro sull'utente
        if (filtroUtente != null && !"".equals(filtroUtente))
          filtro += " and " + filtroUtente;
        if (filtroUffint != null && !"".equals(filtroUffint))
          filtro += " and exists (select codgar from torn where codgar = " + entita + ".CODGAR and CENINT = '" + filtroUffint + "')";
      }else if ("3".equals(profilo)) {
        entita = "v_gare_catalditte";
        chiave = "codice";
        //Inserimento filtro sull'utente
        if (filtroUtente != null && !"".equals(filtroUtente))
          filtro += " and " + filtroUtente;
        if (filtroUffint != null && !"".equals(filtroUffint))
          filtro += " and exists (select codgar from torn where codgar = " + entita + ".CODGAR and CENINT = '" + filtroUffint + "')";
      }else if ("4".equals(profilo)) { // va riscritto
        if ("count".equals(tipoOperazione))
          filtro="from w_invcom , MERIC, GARE, ENTITA  where MERIC.ID = GARE.IDRIC and GARE.NGARA = ENTITA.CHIAVE and GARECONT.NCONT=1 and comkey2=ENTITA.CHIAVE and comtipo='FS12' and comdatlet is null and comstato='3'";
        else
          filtro="from w_invcom , MERIC, GARE, ENTITA, w_puser  where MERIC.ID = GARE.IDRIC and GARE.NGARA = ENTITA.CHIAVE and GARECONT.NCONT=1 and comkey2=ENTITA.CHIAVE and comtipo='FS12' and comdatlet is null and comstato='3' and w_puser.usernome = w_invcom.comkey1 ";
        entita = "GARECONT";
        chiave = "ngara";
        //Inserimento filtro sull'utente
        if (filtroUtente != null && !"".equals(filtroUtente))
          filtro += " and " + filtroUtente;
        if (filtroUffint != null && !"".equals(filtroUffint))
          filtro += " and MERIC.CENINT = '" + filtroUffint + "'";
      }else if ("5".equals(profilo)) {
        entita = "gareavvisi";
        chiave = "ngara";
        //Inserimento filtro sull'utente
        if (filtroUtente != null && !"".equals(filtroUtente))
          filtro += " and " + filtroUtente;
        if (filtroUffint != null && !"".equals(filtroUffint))
          filtro += " and exists (select codgar from torn where codgar = " + entita + ".CODGAR and CENINT = '" + filtroUffint + "')";
      }else if ("6".equals(profilo)) {
        entita = "v_gare_nscad";
        chiave = "ngara";
        //Inserimento filtro sull'utente
        if (filtroUtente != null && !"".equals(filtroUtente))
          filtro += " and " + filtroUtente;
      }else if ("7".equals(profilo)) {
        entita = "v_ditte_prit";
        chiave = "ngara";
        //Inserimento filtro sull'utente
        if (filtroUtente != null && !"".equals(filtroUtente))
          filtro += " and " + filtroUtente;
      }else if ("9".equals(profilo)) {
        entita = "nso_ordini nso";
        chiave = "nso.id";
        if (StringUtils.isNotBlank(filtroUffint)) {
          filtro += " and nso.CODEIN = '" + filtroUffint + "'";
        }
      }else if ("10".equals(profilo)) {
        //Per le stipule la view v_gare_stipula ha problemi di lentezza nel reperire le comunicazioni, quindi si lavora direttamente con la tabella G1STIPULA
        filtro="from w_invcom, ENTITA, gare, torn  where comkey2=CHIAVE and comtipo='FS12' and comdatlet is null" + filtroComent + "and comstato='3' and gare.ngara=g1stipula.ngara and torn.codgar=gare.codgar1";
        if ("sel".equals(tipoOperazione))
          filtro="from w_invcom , ENTITA, gare, torn, w_puser  where comkey2=CHIAVE and comtipo='FS12' and comdatlet is null" + filtroComent + "and comstato='3' and gare.ngara=g1stipula.ngara and torn.codgar=gare.codgar1 and w_puser.usernome = w_invcom.comkey1" ;
        entita = "g1stipula";
        chiave = "codstipula";
        if (filtroUtente != null && !"".equals(filtroUtente))
            filtro += " and " + filtroUtente;
        if (StringUtils.isNotBlank(filtroUffint)) {
          filtro += " and CENINT = '" + filtroUffint + "'";
        }
      }

      filtro = StringUtils.replace(filtro, "ENTITA", entita);
      filtro = StringUtils.replace(filtro, "CHIAVE", chiave);

      return filtro;
    }

    /**
     * Viene effettuata la sostituzione dei mnemonici definiti nei testi delle comunicazioni.
     *
     * @param parametri
     * @return String
     */
    public String sostituzioneMnemonici(HashMap<String, Object> parametri) {
      String stringa= (String)parametri.get("oggetto");

      String ngara= (String)parametri.get("ngara");
      if (ngara == null)
        ngara="";
      stringa = StringUtils.replace(stringa, REPLACEMENT_NGARA, ngara);

      String codgar= (String)parametri.get("codgar");
      if (codgar == null)
        codgar="";
      stringa = StringUtils.replace(stringa, REPLACEMENT_CODGAR, codgar);

      String codiga= (String)parametri.get("codiga");
      if (codiga == null)
        codiga="";
      stringa = StringUtils.replace(stringa, REPLACEMENT_CODIGA, codiga);

      String g1codcig= (String)parametri.get("g1codcig");
      if (g1codcig == null)
        g1codcig="";
      stringa = StringUtils.replace(stringa, REPLACEMENT_G1CODCIG, g1codcig);

      String oggetta= (String)parametri.get("oggetta");
      if (oggetta == null)
        oggetta="";
      stringa = StringUtils.replace(stringa, REPLACEMENT_OGGETA, oggetta);

      String g1destor= (String)parametri.get("g1destor");
      if (g1destor == null)
        g1destor="";
      stringa = StringUtils.replace(stringa, REPLACEMENT_G1DESTOR, g1destor);

      String oggettoga= (String)parametri.get("oggettoga");
      if (oggettoga == null)
        oggettoga="";
      stringa = StringUtils.replace(stringa, REPLACEMENT_OGGETTOGA, oggettoga);

      String dtepar="";
      Date data = null;
      if (parametri.get("dtepar") != null) {
        data = new Date(((Timestamp)parametri.get("dtepar")).getTime());
        dtepar = UtilityDate.convertiData(data, UtilityDate.FORMATO_GG_MM_AAAA);
      }
      stringa = StringUtils.replace(stringa, REPLACEMENT_DTEPAR, dtepar);

      String otepar= (String)parametri.get("otepar");
      if (otepar == null)
        otepar="";
      stringa = StringUtils.replace(stringa, REPLACEMENT_OTEPAR, otepar);

      String dteoff="";
      data = null;
      if (parametri.get("dteoff") != null) {
        data = new Date(((Timestamp)parametri.get("dteoff")).getTime());
        dteoff = UtilityDate.convertiData(data, UtilityDate.FORMATO_GG_MM_AAAA);
      }
      stringa = StringUtils.replace(stringa, REPLACEMENT_DTEOFF, dteoff);

      String oteoff= (String)parametri.get("oteoff");
      if (oteoff == null)
        oteoff="";
      stringa = StringUtils.replace(stringa, REPLACEMENT_OTEOFF, oteoff);

      String desoff="";
      data = null;
      if (parametri.get("desoff") != null) {
        data = new Date(((Timestamp)parametri.get("desoff")).getTime());
        desoff = UtilityDate.convertiData(data, UtilityDate.FORMATO_GG_MM_AAAA);
      }
      stringa = StringUtils.replace(stringa, REPLACEMENT_DESOFF, desoff);

      String oesoff= (String)parametri.get("oesoff");
      if (oesoff == null)
        oesoff="";
      stringa = StringUtils.replace(stringa, REPLACEMENT_OESOFF, oesoff);

      java.util.Date date = new java.util.Date();
      String dataString =UtilityDate.convertiData(date, UtilityDate.FORMATO_GG_MM_AAAA);
      stringa = StringUtils.replace(stringa, REPLACEMENT_DATAOGGI, dataString);

      String inctec= (String)parametri.get("inctec");
      if (inctec == null){
        inctec="";
       }else{
        inctec = tabellatiManager.getDescrTabellato("Ag004", inctec.toString());
       }
      stringa = StringUtils.replace(stringa, REPLACEMENT_INCTEC, inctec);

      String nomtecrup= (String)parametri.get("nomtecrup");
      if (nomtecrup == null)
        nomtecrup="";
      stringa = StringUtils.replace(stringa, REPLACEMENT_NOMTECRUP, nomtecrup);

      String cftecrup= (String)parametri.get("cftecrup");
      if (cftecrup == null)
        cftecrup="";
      stringa = StringUtils.replace(stringa, REPLACEMENT_CFTECRUP, cftecrup);

      String urlMEval= (String)parametri.get("urlMEval");
      if (urlMEval == null)
        urlMEval="";
      stringa = StringUtils.replace(stringa, REPLACEMENT_URL_MEVAL, urlMEval);

      return stringa;
    }

    /**
     * Viene effettuata la sostituzione dei mnemonici definiti nei testi delle comunicazioni.
     *
     * @param parametri
     * @return String
     */
    public String sostituzioneMnemoniciDittaSingola(HashMap<String, Object> parametri) {
      String stringa= (String)parametri.get("oggetto");

      String g1nprogg= (String)parametri.get("g1nprogg");
      if (g1nprogg == null)
        g1nprogg="";
      stringa = StringUtils.replace(stringa, REPLACEMENT_G1NPROGG, g1nprogg);

      String g1numordpl= (String)parametri.get("g1numordpl");
      if (g1numordpl == null)
        g1numordpl="";
      stringa = StringUtils.replace(stringa, REPLACEMENT_G1NUMORDPL, g1numordpl);

      String dittao= (String)parametri.get("dittao");
      if (dittao == null)
        dittao="";
      stringa = StringUtils.replace(stringa, REPLACEMENT_DITTAO, dittao);

      String nomimp= (String)parametri.get("nomimp");
      if (nomimp == null)
        nomimp="";
      stringa = StringUtils.replace(stringa, REPLACEMENT_NOMIMP, nomimp);

      String cfimp= (String)parametri.get("cfimp");
      if (cfimp == null)
        cfimp="";
      stringa = StringUtils.replace(stringa, REPLACEMENT_CFIMP, cfimp);

      String pivimp= (String)parametri.get("pivimp");
      if (pivimp == null)
        pivimp="";
      stringa = StringUtils.replace(stringa, REPLACEMENT_PIVIMP, pivimp);


      String g_dtrisoa="";
      Date data = null;
      if (parametri.get("g_dtrisoa") != null) {
        data = new Date(((Timestamp)parametri.get("g_dtrisoa")).getTime());
        g_dtrisoa = UtilityDate.convertiData(data, UtilityDate.FORMATO_GG_MM_AAAA);
      }
      stringa = StringUtils.replace(stringa, REPLACEMENT_G_DTRISOA, g_dtrisoa);

      String g_dscanc="";
      data = null;
      if (parametri.get("g_dscanc") != null) {
        data = new Date(((Timestamp)parametri.get("g_dscanc")).getTime());
        g_dscanc = UtilityDate.convertiData(data, UtilityDate.FORMATO_GG_MM_AAAA);
      }
      stringa = StringUtils.replace(stringa, REPLACEMENT_G_DSCANC, g_dscanc);

      return stringa;
    }

    /**
     * Viene confrontato il valore massimo di fasgar(moltiplicato per 10) dei lotti(escludendo la gara complementare) con quello quello passato come argomento.
     * Viene restituito il massimo fra i due.
     *
     * @param numeroStepAttivo
     * @param codiceTornata
     * @return Long
     * @throws GestoreException
     */
    public Long getStepAttivo(Long numeroStepAttivo, String codiceTornata) throws GestoreException{
      Long valoreStepAggiornamento =  numeroStepAttivo;
      try {
        Long maxFasgar = (Long)this.sqlManager.getObject("select max(fasgar) from gare where codgar1=? and codgar1!=ngara", new Object[]{codiceTornata});
        if (maxFasgar != null) {
          Long tmpMaxFasgar = new Long (maxFasgar.longValue() * 10);
          if (numeroStepAttivo != null && tmpMaxFasgar.longValue() > numeroStepAttivo.longValue())
            valoreStepAggiornamento = tmpMaxFasgar;
        }

      } catch (SQLException e) {
        throw new GestoreException("Errore nella lettura della fase di gara",null,e);
      }
      return valoreStepAggiornamento;
    }

    /**
     * Si controlla se per ogni lotto è presente il documento relativo alla busta
     *
     * @param codgar codice della gara
     * @param valtec condizione che indica la presenza della valutazione tecnica
     * @param busta
     * @param filtroIsarchi
     * @param filtroCriteriEco
     * @return boolean
     * @throws SQLException
     * @throws GestoreException
     *
     */
    public boolean controlloDocumentazioneLotti(String codgar, boolean valtec, Long busta, boolean filtroIsarchi, boolean filtroCriteriEco, boolean filtroContestoValidita) throws SQLException, GestoreException{
      boolean ret = this.controlloDocumentazioneLotti(codgar, valtec, busta, filtroIsarchi, filtroCriteriEco, filtroContestoValidita, "");
      return ret;
    }


  /**
   * Si controlla se per ogni lotto è presente il documento relativo alla busta
   * @param codgar codice della gara
   * @param valtec condizione che indica la presenza della valutazione tecnica
   * @param busta
   * @param filtroIsarchi
   * @param filtroCriteriEco
   * @param filtroContestoValidita
   * @param sezioniTec
   * @return
   * @throws SQLException
   * @throws GestoreException
   */
  public boolean controlloDocumentazioneLotti(String codgar, boolean valtec, Long busta, boolean filtroIsarchi, boolean filtroCriteriEco, boolean filtroContestoValidita, String sezioniTec) throws SQLException, GestoreException{
    boolean ret=true;
    String selectLotti = "select g.ngara from gare g, gare1 g1 where g.codgar1=? and g.codgar1!=g.ngara and g1.ngara = g.ngara";
    if (valtec) {
      //Si devono considerare i lotti OPEV o con valutazione tecnica
      selectLotti += " and (g.modlicg=6 or g1.valtec='1')";
      if ("1".equals(sezioniTec)) {
        selectLotti += " and g1.sezionitec='1'";
      }
    } else if (filtroCriteriEco) {
      //Si considerano solo i lotti per i quali sono valorizzati i criteri economici
      selectLotti = "select g.ngara from gare g where g.codgar1=? and g.codgar1!=g.ngara and (g.modlicg=6 and exists "
          + "(select e.ngara from goev e where e.ngara = g.ngara and e.tippar=2) or g.modlicg <> 6)";
    }

    List<?> listaLotti = this.sqlManager.getListVector(selectLotti, new Object[]{codgar});
    if (listaLotti != null && listaLotti.size() > 0) {
      String lotto=null;
      String selectDocumgara="select count(codgar) from DOCUMGARA where CODGAR=? and gruppo=? and busta=? and ngara =?";
      if (filtroIsarchi)
        selectDocumgara += " and (ISARCHI is null or ISARCHI<>'1')";
      if (filtroContestoValidita)
        selectDocumgara += " and CONTESTOVAL is null ";
      if ("1".equals(sezioniTec)) {
        selectDocumgara += " and SEZTEC = ? ";
      }
      Long conteggio = null;
      Long conteggio1 = null;
      Long conteggio2 = null;
      for (int i = 0; i < listaLotti.size(); i++) {
        lotto = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).stringValue();
        if ("1".equals(sezioniTec)) {
          conteggio1 = (Long) this.sqlManager.getObject(selectDocumgara, new Object[]{codgar, new Long(3), busta, lotto, new Long(1)});
          conteggio2 = (Long) this.sqlManager.getObject(selectDocumgara, new Object[]{codgar, new Long(3), busta, lotto, new Long(2)});
          if (!(new Long(1).compareTo(conteggio1) <= 0 &&  new Long(1).compareTo(conteggio2) <= 0)) {
            ret = false;
            break;
          }
        } else {
          conteggio = (Long)this.sqlManager.getObject(selectDocumgara, new Object[]{codgar, new Long(3), busta, lotto});
          if (conteggio == null || conteggio != null && conteggio.longValue() == 0) {
            ret = false;
            break;
          }
        }

      }
    } else {
      //Nel caso in cui si controllino solo i lotti con criteri economici, se non esistono non devo bloccare
      if (!filtroCriteriEco)
        ret = false;
    }
    return ret;
  }

    /**
     * Metodo che controlla i diritti in modifica di una gara, considerando il campo "PROPRI" o "AUTORI"
     * @param entita
     * @param nomeCampoWhere
     * @param valoreCampo
     * @param isCodgar (se true in valoreCampo vi è CODGAR, altrimenti NGARA)
     * @param tipoControllo, "1" per "PROPRI", "2" per "AUTORI"
     * @param request
     * @retun boolean
     * @throws GestoreException
     */
    public boolean getAutorizzatoModificaGara(String entita, String nomeCampoWhere,
        String valoreCampo, boolean isCodgar, String tipoControllo, HttpServletRequest request) throws GestoreException{
      boolean ret = true;

      // si verifica se è definito un filtro sul livello utente per l'entità
      if (DizionarioLivelli.getInstance().isFiltroLivelloPresente(entita)) {
        // in caso affermativo, si costruisce il filtro sulla base dell'utente
        ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        FiltroLivelloUtente filtroUtente = profilo.getFiltroLivelloUtente();
        // si indica inoltre l'entità per cui eseguire il filtro
        Livello livello = DizionarioLivelli.getInstance().get(entita);
        filtroUtente.setLivello(livello, entita);
        // si genera la stringa da aggiungere alla clausola where opportunamente
        // valorizzata con il valore del filtro da applicare
        if (filtroUtente.getCondizione() != null) {
          ret = false;
          String nomeCampo="autori";
          if ("1".equals(tipoControllo))
            nomeCampo="propri";

          String seletc="select " + nomeCampo + " from g_permessi where " + nomeCampoWhere + " = ? and syscon = ?";
          try {
            Object param[]= new Object[2];

            if (nomeCampoWhere.contains("IDMERIC"))
              param[0]= new Long(valoreCampo);
            else{
              if (isCodgar) {
                param[0]= valoreCampo;
              } else {
                param[0]= this.sqlManager.getObject("select codgar1 from gare where ngara=?", new Object[]{valoreCampo});
              }

            }

            param[1] = new Long(profilo.getId());

            Object obj = this.sqlManager.getObject(seletc, param);

            if ("1".equals(tipoControllo)) {
              String valore = (String)obj;
              String abilitazione = new String (profilo.getAbilitazioneStd());
              if (nomeCampoWhere.contains("IDMERIC")||nomeCampoWhere.contains("CODGAR"))
                abilitazione = new String (profilo.getAbilitazioneGare());

              if ("1".equals(valore) || (new String("A")).equals(abilitazione)) {
                ret = true;
              }
            } else {
              Long valore = (Long)obj;
              if ((new Long(1)).equals(valore)) {
                ret = true;
              }
            }

          } catch (SQLException s) {
            throw new GestoreException("Errore nella lettura dei privilegi di modifica della gara",null,s);
          }
        }
      }

      return ret;
    }

    /**
     * Si controlla che tutte le ditte abbiano punteggio tecnio ed economico
     * non superiori a quelli massimi della gara.
     * Nel caso di offerta unica se il controllo non viene supertato, viene restituito false
     * Per gli altri tipi di gara, se il controllo non viene superato viene generato un errore e si
     * produce il relativo messaggio.
     * Con il parametro modo si distingue se i messaggi da prendere dal file resource devono essere
     * relativi all'attivazione del calcolo di aggiudicazione o al calcolo soglia anomalia
     *
     * @param ngara
     * @param maxPunTecnico
     * @param maxPunEconomico
     * @param isGaraOffUnica
     * @param modo
     *@return boolean
     *
     * @throws GestoreException
     */
    public boolean controlloPunteggiDitte (String ngara, Double maxPunTecnico, Double maxPunEconomico, boolean isGaraOffUnica, String modo)
        throws GestoreException{

      String suffisso="aggiudicazioneFaseA";
      if ("ATTIVAZIONE".equals(modo))
        suffisso="attivazioneCalcoloAggiudicazione";

      //Tutte le ditte devono avere punteggio tecnio ed economico non superiori a quelli della gara
      //Si devono caricare i dati delle ditte visualizzate allo step dell'apertura
      String select = "select puntec,puneco from ditg where ngara5=? and (INVOFF <> '2' or INVOFF is null) and " +
                      "(AMMGAR <> '2' or AMMGAR is null) and (MOTIES < 99 or MOTIES is null)";

      boolean ret=true;
      try {
        List<?> listaDitte = this.sqlManager.getListVector(select, new Object[]{ngara});
        if (listaDitte != null && listaDitte.size() > 0) {
          boolean controlloPunteggiTecniciSuperato = true;
          boolean controlloPunteggiEconomiciSuperato = true;
          Double punteggioTecnico = null;
          Double punteggioEconomico = null;

          for (int i = 0; i < listaDitte.size(); i++) {
            Vector<?> ditta = (Vector<?>) listaDitte.get(i);
            punteggioTecnico = ((JdbcParametro) ditta.get(0)).doubleValue();
            punteggioEconomico = ((JdbcParametro) ditta.get(1)).doubleValue();
            if (maxPunTecnico != null && punteggioTecnico!= null && punteggioTecnico.doubleValue()> maxPunTecnico.doubleValue()) {
              controlloPunteggiTecniciSuperato = false;
            }
            if (maxPunEconomico!= null && punteggioEconomico!= null && punteggioEconomico.doubleValue()> maxPunEconomico.doubleValue()) {
              controlloPunteggiEconomiciSuperato = false;
            }
          }
          if (!isGaraOffUnica) {
            if (!controlloPunteggiTecniciSuperato && controlloPunteggiEconomiciSuperato) {
              Throwable e = new Throwable();
               //this.getRequest().setAttribute("RISULTATO", "ERRORI");
               throw new GestoreException(
                   "Errore durante il calcolo della prima fase dell'aggiudicazione (calcolo della soglia di anomalia)",
                   suffisso + ".ControlloPunteggiTecniciDitte", e);
             } else if (controlloPunteggiTecniciSuperato && !controlloPunteggiEconomiciSuperato) {
               Throwable e = new Throwable();
               //this.getRequest().setAttribute("RISULTATO", "ERRORI");
               throw new GestoreException(
                   "Errore durante il calcolo della prima fase dell'aggiudicazione (calcolo della soglia di anomalia)",
                   suffisso + ".ControlloPunteggiEconomiciDitte", e);
             } else if (!controlloPunteggiTecniciSuperato && !controlloPunteggiEconomiciSuperato) {
               Throwable e = new Throwable();
               //this.getRequest().setAttribute("RISULTATO", "ERRORI");
               throw new GestoreException(
                   "Errore durante il calcolo della prima fase dell'aggiudicazione (calcolo della soglia di anomalia)",
                   suffisso + ".ControlloPunteggiDitte", e);
             }
          } else {
            if (!controlloPunteggiTecniciSuperato || !controlloPunteggiEconomiciSuperato) {
              ret = false;
            }
          }
        }

      } catch (SQLException e) {
        //this.getRequest().setAttribute("RISULTATO", "ERRORI");
        throw new GestoreException(
            "Errore durante il calcolo della prima fase dell'aggiudicazione (calcolo della soglia di anomalia)",
            "aggiudicazioneFaseA", e);
      }
      return ret;
    }

    /**
     * Il metodo effettua l'aggiornamento degli importi della gara a partire dai dati delle lavorazioni.
     * Nel caso di affidamenti(modlicg=null) si aggiornano pure i dati di DITG.
     *
     * @param ngara
     * @param gestioneDPRE, se è true viene effettuato l'inserimento ind DPRE
     *
     * @throws GestoreException
     */
    public void aggiornamentoImportiDaLavorazioni(String ngara, boolean gestioneDPRE) throws GestoreException{
      String sql = "select sum(quanti * prezun) from gcap where ngara=? and dittao is null";
      String FiltroSogrib = " and sogrib = ? and solsic='2'";
      String sqlFiltroSogrib = sql + FiltroSogrib;
      String FiltroSolsic = " and solsic = ?";
      String sqlFiltroSolsic = sql + FiltroSolsic;
      String updateGare = "update gare set impapp=?, impnrl=?, impsic=?, garoff=?, idiaut=? where ngara=?";
      String updateDITG = "update ditg set impoff=?,ribauo=? where ngara5=?";
      String sqlFiltroMisuraCorpo = sql + " and clasi1=?";
      String sqlMisuraCorpoFiltroSogrib = sqlFiltroMisuraCorpo + FiltroSogrib;
      String sqlMisuraCorpoFiltroSolsic = sqlFiltroMisuraCorpo + FiltroSolsic;
      String sqlGare1="select imprin,impserv,imppror,impaltro from gare1 where ngara=?";

      try {
        Object importo = this.sqlManager.getObject(sql, new Object[]{ngara});
        Double impapp = this.getValoreImportoToDouble(importo);
        importo = this.sqlManager.getObject(sqlFiltroSogrib, new Object[]{ngara,"1"});
        Double impnrl =  this.getValoreImportoToDouble(importo);
        importo = this.sqlManager.getObject(sqlFiltroSolsic, new Object[]{ngara,"1"});
        Double impsic =  this.getValoreImportoToDouble(importo);
        Long tipgen = (Long)this.sqlManager.getObject("select tipgen from torn,gare where ngara=? and codgar=codgar1", new Object[]{ngara});
        Double percentuale = this.initPGAROF(tipgen);
        Double garoff = this.calcolaGAROFF(impapp, percentuale, tipgen);
        Vector<?> datiGare1=this.sqlManager.getVector(sqlGare1, new Object[]{ngara});
        Double imprin=new Double(0);
        Double impserv=new Double(0);
        Double imppror = new Double(0);
        Double impaltro = new Double(0);
        if(datiGare1!= null && datiGare1.size()>0){
          imprin=SqlManager.getValueFromVectorParam(datiGare1, 0).doubleValue();
          if(imprin==null)
            imprin=new Double(0);
          impserv=SqlManager.getValueFromVectorParam(datiGare1, 1).doubleValue();
          if(impserv==null)
            impserv=new Double(0);
          imppror=SqlManager.getValueFromVectorParam(datiGare1, 2).doubleValue();
          if(imppror==null)
            imppror=new Double(0);
          impaltro=SqlManager.getValueFromVectorParam(datiGare1, 3).doubleValue();
          if(impaltro==null)
            impaltro=new Double(0);
        }
        double importoGara=0;
        if(impapp!=null)
          importoGara = impapp.doubleValue();
        Double valmax= new Double(importoGara + imprin.doubleValue() + impserv.doubleValue() + imppror.doubleValue() + impaltro.doubleValue());
        Double idiaut = this.getContributoAutoritaStAppaltante(valmax, "A1z01");

        //Aggiornamento dati della gara
        this.sqlManager.update(updateGare, new Object[]{impapp,impnrl,impsic,garoff,idiaut,ngara});
        if (new Long(1).equals(tipgen)) {
          //Misura
          importo = this.sqlManager.getObject(sqlFiltroMisuraCorpo, new Object[]{ngara, new Long(3)});
          Double impmis = this.getValoreImportoToDouble(importo);
          importo = this.sqlManager.getObject(sqlMisuraCorpoFiltroSogrib, new Object[]{ngara, new Long(3),"1"});
          Double impnrm = this.getValoreImportoToDouble(importo);
          importo = this.sqlManager.getObject(sqlMisuraCorpoFiltroSolsic, new Object[]{ngara, new Long(3),"1"});
          Double impsmi = this.getValoreImportoToDouble(importo);
          //Corpo
          importo = this.sqlManager.getObject(sqlFiltroMisuraCorpo, new Object[]{ngara, new Long(1)});
          Double impcor = this.getValoreImportoToDouble(importo);
          importo = this.sqlManager.getObject(sqlMisuraCorpoFiltroSogrib, new Object[]{ngara, new Long(1),"1"});
          Double impnrc = this.getValoreImportoToDouble(importo);
          importo = this.sqlManager.getObject(sqlMisuraCorpoFiltroSolsic, new Object[]{ngara, new Long(1),"1"});
          Double impsco = this.getValoreImportoToDouble(importo);
          updateGare = "update gare set impmis=?, impnrm=?, impsmi=?, impcor=?, impnrc=?,impsco=?  where ngara=?";
          this.sqlManager.update(updateGare, new Object[]{impmis,impnrm,impsmi,impcor,impnrc,impsco, ngara});
        }

        //Aggiornamento di DITG nel caso di affidamento
        Long modlicg = (Long)this.sqlManager.getObject("select modlicg from gare where ngara=?", new Object[]{ngara});
        if (modlicg == null) {
          this.sqlManager.update(updateDITG, new Object[]{impapp, new Double(0),ngara});
        }

        //Gestione DPRE
        if (gestioneDPRE) {
          String ngaraaq = (String)this.sqlManager.getObject("select ngaraaq from torn,gare where ngara=? and codgar=codgar1", new Object[]{ngara});
          String dittaAccordoQuadro = (String)this.sqlManager.getObject("select ditta from gare where ngara=?", new Object[]{ngaraaq});
          this.sqlManager.update("delete from dpre where ngara=? and dittao=?", new Object[]{ngara,dittaAccordoQuadro});

          List<?> listaDatiDPREAccordoQuadro = this.sqlManager.getListHashMap(
              "select d.contaf, d.ngara, d.dittao, d.preoff, d.impoff from dpre d,gcap g where d.ngara=? and d.dittao=? and " +
              " d.ngara = g.ngara and d.contaf=g.contaf and g.sogrib=?" +
              " and d.contaf in (select contaf from gcap where ngara=? and dittao is null)",
              new Object[] { ngaraaq,dittaAccordoQuadro,"2", ngara });

          if (listaDatiDPREAccordoQuadro != null && listaDatiDPREAccordoQuadro.size() > 0) {
            DataColumnContainer campiDaInserire = new DataColumnContainer(this.sqlManager,
                "DPRE", "select contaf, ngara, dittao, preoff, impoff from dpre", new Object[] {});
            for (int row = 0; row < listaDatiDPREAccordoQuadro.size(); row++) {
              campiDaInserire.setValoriFromMap((HashMap<?,?>) listaDatiDPREAccordoQuadro.get(row), true);
              campiDaInserire.getColumn("DPRE.CONTAF").setChiave(true);
              campiDaInserire.getColumn("DPRE.NGARA").setChiave(true);
              campiDaInserire.getColumn("DPRE.DITTAO").setChiave(true);
              campiDaInserire.setValue("DPRE.NGARA", ngara);

              campiDaInserire.insert("DPRE", this.sqlManager);
            }
          }
        }

      } catch (SQLException e) {
        throw new GestoreException(
            "Errore durante l'aggiornamento degli importi della gara " + ngara + " a partire dai dati della lavorazione",null, e);
      }

    }

    /**
     * Il metodo determina se l'oggetto passato come argomento è di tipo Long o Double,
     * e ritorna l'oggetto convertito in Double
     *
     * @param oggetto
     * @return Double
     */
    public Double getValoreImportoToDouble(Object oggetto) {
      Double importo = null;
      if (oggetto != null) {
        if (oggetto instanceof Long)
          importo = new Double(((Long) oggetto));
        else if (oggetto instanceof Double)
          importo = new Double((Double) oggetto);
      }
      return importo;
    }

    /**
     * Viene effettuato l'aggiornamento della tabella ISCRIZUFF in base al calcolo del numero di inviti virtuali
     * @param codgar
     * @param ngara
     * @param codiceCategoria
     * @param codiceDitta
     * @param tipoCategoria
     * @throws SQLException
     * @throws GestoreException
     */
    public void aggInvitiVirtualiIscrizuff(String codgar, String ngara, String codiceCategoria, String codiceDitta, Long tipoCategoria  ) throws SQLException, GestoreException{
      String selectIscrizuff="select distinct(cenint) from iscrizuff where codgar=? and ngara=? and "
          + "codcat=? and tipcat=?";
      List<?> listaIscrizuff = this.sqlManager.getListVector(selectIscrizuff,
          new Object[] { codgar, ngara, codiceCategoria, tipoCategoria });
      if (listaIscrizuff != null && listaIscrizuff.size() > 0) {
        Long id;
        Long numPenalitaIscrizuff=null;
        int idNuovo=0;
        String cenint;
        for (int z = 0; z < listaIscrizuff.size(); z++) {
          id = null;
          cenint = SqlManager.getValueFromVectorParam(listaIscrizuff.get(z), 0).getStringValue();
          numPenalitaIscrizuff = this.getNumeroPenalita(codgar, ngara, codiceDitta,
              codiceCategoria, tipoCategoria, null, "ISCRIZUFF",cenint, false);
          id = (Long)this.sqlManager.getObject("select id from iscrizuff where codgar=? and ngara=? and codimp=? and "
              + "codcat=? and cenint=? and tipcat=?", new Object[]{codgar, ngara, codiceDitta, codiceCategoria, cenint, tipoCategoria});
          if (numPenalitaIscrizuff != null && numPenalitaIscrizuff.longValue() <= 0) {
            if (id != null)
              this.sqlManager.update("update iscrizuff set invpen = ? where id=?", new Object[]{new Long(0),id});
          } else if (numPenalitaIscrizuff != null && numPenalitaIscrizuff.longValue() > 0) {
            if (id != null) {
              this.sqlManager.update("update iscrizuff set invpen = ? where id=?", new Object[]{numPenalitaIscrizuff,id});
            } else {
              idNuovo = this.genChiaviManager.getNextId("ISCRIZUFF");
              this.sqlManager.update(
                "insert into ISCRIZUFF(ID,CODGAR,NGARA,CODIMP,CODCAT,CENINT,TIPCAT,INVPEN) values(?,?,?,?,?,?,?,?)",
                new Object[] { new Long(idNuovo), codgar, ngara, codiceDitta, codiceCategoria, cenint, tipoCategoria, numPenalitaIscrizuff });
            }
          }
        }
      }
    }

  /**
   * Metodo che aggiorna il numero di inviti oppure il numero di aggiudicazioni di ISCRIZCAT.
   * L'aggiornamento viene fatto sempre indipendentemente dal criterio di rotazione
   *
   * @param codiceGara
   * @param numeroGara
   * @param codiceDitta
   * @param codiceCategoria
   * @param modo
   *        : INS, DEL
   * @param status
   * @param tipgen
   * @param classifica
   *        è valorizzato solo nel caso di calcolo degli inviti per ISCRIZCLASSI
   * @param codiceStazApp
   * @param modalita
   *          1 per aggiornamento invrea
   *          2 per aggrea
   * @throws SQLException
   *
   * @throws GestoreException
   */
  public void aggiornaIscrizcat(String codiceGara, String numeroGara,
    String codiceDitta, String codiceCategoria, String modo,
    Long tipgen, Long classifica, String codiceStazApp, long modalita) throws SQLException, GestoreException {

    Long valoreCampo = null;
    long numCalcolato;

    String campo="";
    if(modalita==1)
      campo = "invrea";
    else if(modalita==2)
      campo = "aggrea";

    String select = "select " + campo + " from iscrizcat where codgar=? and ngara = ? and codimp = ? and codcat =?";
    Object parametri[] = null;


      if (codiceStazApp == null) {
        if ("0".equals(codiceCategoria)) {
          select += " and tipcat=?";
          parametri = new Object[5];
          parametri[4] = tipgen;
        } else {
          parametri = new Object[4];
        }
        parametri[0] = codiceGara;
        parametri[1] = numeroGara;
        parametri[2] = codiceDitta;
        parametri[3] = codiceCategoria;

        if (classifica == null)
          valoreCampo = (Long) this.sqlManager.getObject(select, parametri);
        else {
          // Calcolo INVREA per iscrizclassi
          Long datiIscrizclassi[] = this.getCampoIscrizclassi(codiceGara,
              numeroGara, codiceDitta, codiceCategoria, classifica, campo);
          if (datiIscrizclassi != null && datiIscrizclassi.length > 1) {
            valoreCampo = datiIscrizclassi[0];
            classifica = datiIscrizclassi[1];
          }
        }

        if ("INS".equals(modo)) {
          numCalcolato = 1;
          if (valoreCampo != null && valoreCampo.longValue() > 0) {
            numCalcolato = valoreCampo.longValue() + 1;
          }

        } else {
          numCalcolato = 0;
          if (valoreCampo != null && valoreCampo.longValue() > 0) {
            numCalcolato = valoreCampo.longValue() - 1;
          }
        }
        valoreCampo = new Long(numCalcolato);

        select = "update iscrizcat set " + campo + " = ? where codgar=? and ngara = ? and codimp = ? and codcat =? ";

        if ("0".equals(codiceCategoria)) {
          select += " and tipcat=?";
          parametri = new Object[6];
          parametri[5] = tipgen;
        } else {
          parametri = new Object[5];
        }

        if (classifica != null) {
          // Aggiornamento inviti per iscrizclassi
          select = "update iscrizclassi set " + campo + "=? where codgar=? and ngara=? and codimp=? and codcat=? and numclass=?";
          parametri = new Object[6];
          parametri[5] = classifica;
        }

        parametri[0] = valoreCampo;
        parametri[1] = codiceGara;
        parametri[2] = numeroGara;
        parametri[3] = codiceDitta;
        parametri[4] = codiceCategoria;

      } else {
        select = "select " + campo + ",id from iscrizuff where codgar=? and ngara = ? and codimp = ? and codcat =? and cenint=? and tipcat=?";
        parametri = new Object[6];
        parametri[0] = codiceGara;
        parametri[1] = numeroGara;
        parametri[2] = codiceDitta;
        parametri[3] = codiceCategoria;
        parametri[4] = codiceStazApp;
        parametri[5] = tipgen;

        //invrea = (Long) this.sqlManager.getObject(select, parametri);
        Long id=null;
        Vector<?> datiIscrizuff = this.sqlManager.getVector(select, parametri);
        if (datiIscrizuff != null && datiIscrizuff.size()>0) {
          valoreCampo = SqlManager.getValueFromVectorParam(datiIscrizuff, 0).longValue();
          id= SqlManager.getValueFromVectorParam(datiIscrizuff, 1).longValue();
        }
        numCalcolato = 1;
        if ("INS".equals(modo)) {
          numCalcolato = 1;
          if (valoreCampo != null && valoreCampo.longValue() > 0) {
            numCalcolato = valoreCampo.longValue() + 1;
          }

        } else {
          numCalcolato = 0;
          if (valoreCampo != null && valoreCampo.longValue() > 0) {
            numCalcolato = valoreCampo.longValue() - 1;
          }
        }

        if (valoreCampo != null || (valoreCampo == null && "INS".equals(modo) && id != null)) {
          valoreCampo = new Long(numCalcolato);
          select = "update iscrizuff set " + campo + " = ? where id=?";
          parametri = new Object[2];
          parametri[0] = valoreCampo;
          parametri[1] = id;
        }else if (valoreCampo == null && "INS".equals(modo)) {
          //Non è presente l'occorrenza, la si deve inserire
          id = new Long(genChiaviManager.getNextId("ISCRIZUFF"));
          select="insert into ISCRIZUFF(ID,CODGAR,NGARA,CODIMP,CODCAT,CENINT,TIPCAT," + campo + ") values(?,?,?,?,?,?,?,?)";
          parametri = new Object[8];
          parametri[0] = id;
          parametri[1] = codiceGara;
          parametri[2] = numeroGara;
          parametri[3] = codiceDitta;
          parametri[4] = codiceCategoria;
          parametri[5] = codiceStazApp;
          parametri[6] = tipgen;
          parametri[7] = new Long(1);

        }
      }
      if (!select.startsWith("select"))
        this.sqlManager.update(select, parametri);
  }

  /**
   * Metodo che aggiorna il numero di aggiudicazioni di ISCRIZCAT. L'aggiornamento viene
   * fatto sempre indipendentemente dal criterio di rotazione
   *
   * @param codiceGara
   * @param numeroGara
   * @param codiceDitta
   * @param codiceCategoria
   * @param modo
   *        : INS, DEL
   * @param status
   * @param tipgen
   * @param classifica
   *        è valorizzato solo nel caso di calcolo degli inviti per ISCRIZCLASSI
   * @param codiceStazApp
   *
   * @throws GestoreException
   */
  public void aggiornaNumAggiudicazioniDitta(String codiceGara, String numeroGara,
      String codiceDitta, String codiceCategoria, String modo,
      Long tipgen, Long classifica, String codiceStazApp)
      throws GestoreException {

   try {
     this.aggiornaIscrizcat(codiceGara, numeroGara, codiceDitta, codiceCategoria, modo, tipgen, classifica, codiceStazApp, 2);
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'aggiornamento del numero di aggiudicazioni della ditta " + codiceDitta + " per la gara " + numeroGara, null,
          e);
    }
  }


  /**
   * Metodo che effettua l'aggiornamento del numero di aggiudicazioni per una ditta predisponendo i parametri
   * per la chiamata al metodo aggiornaNumAggiudicazioniDitta in base al tipoalgo dell'elenco
   *
   * @param codiceElenco
   * @param numeroElenco
   * @param codiceDitta
   * @param codiceCategoria
   * @param status
   * @param tipoGara
   * @param classifica
   *        è valorizzato solo nel caso di calcolo degli inviti per ISCRIZCLASSI
   * @param codiceStazApp
   * @param tipoalgo
   *
   * @throws GestoreException
   */
  public void aggiornaNumAggiudicazioni(String codiceElenco, String numeroElenco,
      String codiceDitta, String codiceCategoria, Long tipoGara,
      Long classifica, String codiceStazApp, Long tipoalgo, String modo) throws GestoreException {
    if(tipoalgo.longValue()==1 || tipoalgo.longValue()==3 || tipoalgo.longValue()==4 || tipoalgo.longValue()==5 || tipoalgo.longValue()==11 || tipoalgo.longValue()==12 || tipoalgo.longValue()==14 || tipoalgo.longValue()==15 ){
      if(codiceCategoria== null || "".equals(codiceCategoria))
        codiceCategoria="0";

        this.aggiornaNumAggiudicazioniDitta(codiceElenco, numeroElenco, codiceDitta, codiceCategoria, modo, tipoGara,null,null);
        //Se per la categoria prevalente è stata specificata la classe, si devono calcolare le aggiudicazioni
        //sul dettaglio della classe della categoria d'iscrizione dell'operatore
        if(!"0".equals(codiceCategoria) && classifica!=null){
          this.aggiornaNumAggiudicazioniDitta(codiceElenco, numeroElenco, codiceDitta, codiceCategoria, modo, tipoGara,classifica,null);
        }
    }else if(tipoalgo.longValue()==2 || tipoalgo.longValue()==6 || tipoalgo.longValue()==7 || tipoalgo.longValue()==10 || tipoalgo.longValue()==13){
      this.aggiornaNumAggiudicazioniDitta(codiceElenco, numeroElenco, codiceDitta, "0", modo, tipoGara,null,null);

    }else if(tipoalgo.longValue()==8 || tipoalgo.longValue()==9){
      //Si deve fare il conteggio sulla categoria '0'
      this.aggiornaNumAggiudicazioniDitta(codiceElenco, numeroElenco, codiceDitta, "0", modo, tipoGara,null,null);

      //Si deve fare il conteggio anche su ISCRIZUFF.AGGREA sempre per la categoria '0'
      this.aggiornaNumAggiudicazioniDitta(codiceElenco, numeroElenco, codiceDitta, "0", modo, tipoGara,null,codiceStazApp);
    }

  }

  /**
   * Il metodo controlla se la ditta è stata selezionata da elenco per la gara, altrimenti verifica se si tratta di un RT e
   * va a controllare se la mandataria è stata selezionata da elenco. Se si trova la ditta selezionata da elenco ne viene restituito
   * il codice
   *
   * @param ditta
   * @param ngara
   * @return String
   * @throws GestoreException
   */
  public String getDittaSelezionataDaElenco(String ditta, String ngara) throws GestoreException{
    String ret=null;
    try {
      Long acquisizione=null;
      String dittainv = null;
      Vector<?> datiDitta = this.sqlManager.getVector("select acquisizione,dittainv from ditg where ngara5=? and dittao=?", new Object[]{ngara,ditta});
      if(datiDitta!=null && datiDitta.size()>0){
        acquisizione = SqlManager.getValueFromVectorParam(datiDitta, 0).longValue();
        dittainv = SqlManager.getValueFromVectorParam(datiDitta, 1).getStringValue();
      }

      if(new Long(3).equals(acquisizione)){
        ret=ditta;
      }else{
        Long tipimp = (Long)this.sqlManager.getObject("select tipimp from impr where codimp=?", new Object[]{ditta});
        if(tipimp != null && (tipimp.longValue()==3 || tipimp.longValue()==10) && dittainv!=null && !"".equals(dittainv)){
          acquisizione=(Long)this.sqlManager.getObject("select acquisizione from ditg where ngara5=? and dittao=?", new Object[]{ngara,dittainv});
          if(new Long(3).equals(acquisizione))
            ret=dittainv;
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nell'elaborazione dell'impresa " + ditta + " per determinare nel caso sia un raggruppamento, la mandataria", null, e);
    }
    return ret;
  }


  /**
   * Calcolo del numero di aggiudicazioni per le occorrenze in DITGAQ.
   * In base al valore di tipoFiltro si può decidere su quali occorrenze fare il calcolo, in particolare<br>
   * tipoFiltro=0 si considerano tutte le occorrenze di DITGAQ<br>
   * tipoFiltro=1 si considerano tutte le occorrenze tranne quella corrispondente a dittaAgg<br>
   * tipoFiltro=2 si considera solo l'occorrenza uguale a dittaAgg<br>
   * @param codiceElenco
   * @param numeroElenco
   * @param chiaveGara
   * @param codiceLotto
   * @param dittaAgg
   * @param codiceCategoria
   * @param tipoGara
   * @param classifica
   * @param codiceStazApp
   * @param tipoalgo
   * @param modoCalcolo
   * @param tipoFiltro
   * @throws GestoreException
   */
  public void aggiornaNumAggiudicazioniDITGAQ(String codiceElenco, String numeroElenco,
      String chiaveGara, String codiceLotto, String dittaAgg, String codiceCategoria, Long tipoGara,
      Long classifica, String codiceStazApp, Long tipoalgo, String modoCalcolo, int tipoFiltro) throws GestoreException{

      //Gestione aggiornamento conteggio numero aggiudicazioni per le occorrenze in DITGAQ
      try {
        List<?> listaDitgaq = this.sqlManager.getListVector("select dittao from ditgaq where ngara=?", new Object[]{codiceLotto});
        if(listaDitgaq!=null && listaDitgaq.size()>0){
          String codiceDittaDitgaq=null;
          for(int j=0;j< listaDitgaq.size();j++){
            codiceDittaDitgaq = SqlManager.getValueFromVectorParam(listaDitgaq.get(j), 0).stringValue();
            if(tipoFiltro==0 || (tipoFiltro==1 && !dittaAgg.equals(codiceDittaDitgaq)) || (tipoFiltro==2 && dittaAgg.equals(codiceDittaDitgaq))){
              codiceDittaDitgaq = this.getDittaSelezionataDaElenco(codiceDittaDitgaq,chiaveGara);
              if(codiceDittaDitgaq!=null){
                this.aggiornaNumAggiudicazioni(codiceElenco, numeroElenco, codiceDittaDitgaq, codiceCategoria, tipoGara, classifica, codiceStazApp, tipoalgo, modoCalcolo);
              }
            }
          }
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nel conteggio del numero aggiudicazioni per Le ditte in DITGAQ per la gara " + codiceLotto , null, e);
      }
  }

  /**
   * Viene effettuato il conteggio del numero di aggiudicazioni per tutti i lotti della gara per cui è valorizzata la ditta aggiudicataria.
   * Se codiceLottoEscluso è valorizzato, questo viene escluso dalla lista dei lotti
   * @param codiceElenco
   * @param numeroElenco
   * @param codiceGara
   * @param codiceLottoEscluso
   * @param codiceCategoria
   * @param tipoGara
   * @param classifica
   * @param codiceStazApp
   * @param tipoalgo
   * @param modo
   * @throws GestoreException
   */
  public void aggiornaNumAggiudicazioniLotti(String codiceElenco, String numeroElenco,
      String codiceGara, String codiceLottoEscluso, String codiceCategoria, Long tipoGara,
      Long classifica, String codiceStazApp, Long tipoalgo, String modo) throws GestoreException{

    String select = "select g.ngara, ditta, aqoper from gare g, gare1 g1 where g.ngara=g1.ngara and g.codgar1=? and g.ngara!=g.codgar1 and (ditta !=null or ditta is not null)";
    if(codiceLottoEscluso!=null)
      select+= " and g.ngara!='" + codiceLottoEscluso + "'";
    try {
      List<?> listaLottiAggiudicati = this.sqlManager.getListVector(select, new Object[]{codiceGara});
      if(listaLottiAggiudicati!=null && listaLottiAggiudicati.size()>0){
        String codiceLotto;
        String dittaAgg;
        String codiceDittaElenco;
        Long aqoper =null;
        for(int i=0;i< listaLottiAggiudicati.size();i++){
          codiceLotto = SqlManager.getValueFromVectorParam(listaLottiAggiudicati.get(i), 0).stringValue();
          dittaAgg = SqlManager.getValueFromVectorParam(listaLottiAggiudicati.get(i), 1).stringValue();
          aqoper = SqlManager.getValueFromVectorParam(listaLottiAggiudicati.get(i), 2).longValue();
          codiceDittaElenco = this.getDittaSelezionataDaElenco(dittaAgg,codiceGara);
          if(codiceDittaElenco!=null){
            this.aggiornaNumAggiudicazioni(codiceElenco, numeroElenco, codiceDittaElenco, codiceCategoria, tipoGara, classifica, codiceStazApp, tipoalgo, modo);

            if(aqoper!=null && aqoper.longValue()==2){
              //Gestione decremento per le occorrenze in DITGAQ
              this.aggiornaNumAggiudicazioniDITGAQ(codiceElenco, numeroElenco, codiceGara, codiceLotto, dittaAgg, codiceCategoria, tipoGara, classifica, codiceStazApp, tipoalgo, modo,1);
            }
          }
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nel conteggio del numero aggiudicazioni per tutti i lotti della gara " + codiceGara , null, e);
    }
  }

    /**
     * Estra lo stream associato ad un file presente nell'entità W_DOCDIG
     * @param documento
     * @throws SQLException
     */
    public byte[] getFileFromDocumento(DocumentoType documento,String idcom) throws GestoreException {
    	byte[] file = null;
    	try {
	    	if(documento != null) {
	    		if( !StringUtils.isEmpty(documento.getUuid()) ) {
	    			//(da v1.15.0)
	    			//leggi il contenuto da W_DOCDIG
	    			String idPrg = "PA";
					Long idDoc = (Long) this.geneManager.getSql().getObject(
					    "select iddocdig from w_docdig where idprg='" + idPrg + "' and digkey1=? and digkey3=? ",
					    new Object[]{idcom,documento.getUuid()});
					BlobFile blob = this.fileAllegatoManager.getFileAllegato(idPrg, idDoc);
					if(blob != null) {
						file = blob.getStream();
					}
	    		} else {
	    			//(fino a v1.14.x)
	    			//se esiste viene estratto dall'xml e memorizzato in documento.file
	    			file = documento.getFile();
	    		}
	    	}
    	} catch (SQLException e) {
    		throw new GestoreException("Errore nell'estrazione dello stream del file da DocumentoType!",
    	            null, e);
		} catch (IOException e) {
			throw new GestoreException("Errore nell'estrazione dello stream del file da DocumentoType!",
    	            null, e);
		}
    	return file;
    }

    /**
     * Validazione URL.
     *
     * @param url
     * @param entita
     * @param campo
     * @param pagina
     * @param listaControlli
     * @throws GestoreException
     */
    public static boolean validazioneURL(String url)
        throws GestoreException {

      String regex = "(((http|HTTP|https|HTTPS|ftp|FPT|ftps|FTPS|sftp|SFTP)://)|((w|W){3}(\\d)?\\.))[\\w\\?!\\./:;\\-_=#+*%@&quot;\\(\\)&amp;]+";
      boolean res = false;
      if (url != null && !"".equals(url)) {
        if (!url.matches(regex)) {
          res = false;
        }else{
          res = true;
        }
      }
      return res;
    }

    /**
     * Controllo variazione del valore di un campo Si\No nell'aggiornamento dell'anagrafica di IMPR da portale
     * @param datiImpr
     * @param valoreDato
     * @param nomeCampo
     * @param etichettaCampo
     * @return String
     * @throws GestoreException
     */
    private String controlloVariazioneCampiSN(DataColumnContainer datiImpr, String valoreDato, String nomeCampo, String etichettaCampo) throws GestoreException{
      String msg="";
      if ("0".equals(valoreDato))
        valoreDato =  "2";

      datiImpr.setValue( nomeCampo, valoreDato);
      if (datiImpr.isModifiedColumn( nomeCampo)) {
        String valoreDatoOriginale = "";
        if (datiImpr.getColumn( nomeCampo).getOriginalValue() != null)
          valoreDatoOriginale = datiImpr.getColumn( nomeCampo).getOriginalValue().getStringValue();

        if ("1".equals(valoreDato) )
          valoreDato = "Si";
        else if ("2".equals(valoreDato) )
          valoreDato = "No";
        else
          valoreDato = "";

        if ("1".equals(valoreDatoOriginale) )
          valoreDatoOriginale = "Si";
        else if ("2".equals(valoreDatoOriginale) )
          valoreDatoOriginale = "No";
        else
          valoreDatoOriginale = "";

        msg += etichettaCampo + " \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + valoreDatoOriginale
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + valoreDato
            + "\n";

      }
      return msg;
    }

    /**
     * Controllo variazione del valore di un campo data nell'aggiornamento dell'anagrafica di IMPR da portale
     * @param datiImpr
     * @param valoreDato
     * @param nomeCampo
     * @param etichettaCampo
     * @return String
     * @throws GestoreException
     */
    private String controlloVariazioneCampiData(DataColumnContainer datiImpr, Calendar valoreDato, String nomeCampo, String etichettaCampo) throws GestoreException{
      String msg="";
      java.util.Date campoData = null;

      if (valoreDato != null) campoData = valoreDato.getTime();
      datiImpr.setValue(nomeCampo, campoData);
      if (datiImpr.isModifiedColumn(nomeCampo)) {
        String dataOriginale = "";
        if (datiImpr.getColumn(nomeCampo).getOriginalValue().dataValue() != null) {
          dataOriginale = UtilityDate.convertiData(
              new Date(
                  datiImpr.getColumn(nomeCampo).getOriginalValue().dataValue().getTime()),
              UtilityDate.FORMATO_GG_MM_AAAA);
        }

        String dataString = "";
        if (valoreDato != null)
          dataString = UtilityDate.convertiData(valoreDato.getTime(),
              UtilityDate.FORMATO_GG_MM_AAAA);

        msg += etichettaCampo +" \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + dataOriginale
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + dataString
            + "\n";
      }

      return msg;
    }

    /**
     * Controllo variazione del valore di un campo tabellato nell'aggiornamento dell'anagrafica di IMPR da portale
     * @param datiImpr
     * @param valoreDato
     * @param nomeCampo
     * @param etichettaCampo
     * @param tabellato
     * @return String
     * @throws GestoreException
     */
    private String controlloVariazioneCampiTabellati(DataColumnContainer datiImpr, String valoreDato, String nomeCampo, String etichettaCampo, String tabellato) throws GestoreException{
      String msg="";
      Long campoLong = null;
      if (valoreDato != null && !"".equals(valoreDato))
        campoLong = new Long(valoreDato);
      else
        valoreDato = "";
      datiImpr.setValue(nomeCampo, campoLong);
      if (datiImpr.isModifiedColumn(nomeCampo)) {
        String descOriginale = "";
        if (datiImpr.getColumn(nomeCampo).getOriginalValue().longValue() != null) {
          Long tmp = datiImpr.getColumn(nomeCampo).getOriginalValue().longValue();
          descOriginale = tabellatiManager.getDescrTabellato(tabellato,
              tmp.toString());
        }
        if (!"".equals(valoreDato))
          valoreDato = tabellatiManager.getDescrTabellato(tabellato,
              valoreDato);

        msg += etichettaCampo + " \nda:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + descOriginale
            + "\na:\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
            + valoreDato
            + "\n";
      }

      return msg;

    }

    /**
     * Metodo per la cancellazione delle occorrenze in wsvigilanza
     * @param codiceGara
     *        - Valore del campo chiave (CODGAR.TORN)
     * @throws GestoreException
     */
    public void deleteWsVigilanza(String codiceGara)
        throws GestoreException {

      String delete = "delete from wsvigilanza_pg where key1 = ?";
      try {
        this.sqlManager.update(delete, new Object[] { codiceGara });
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore durante l'eliminazione dele righe delle tabella WSVIGILANZA ",
            null, e);
      }
    }

    /**
     * Viene gestita la copia dei documento DGUE, che hanno la seguente caratteristica, che i documenti dei
     * conocorrenti puntano alla stessa occorrenza di w_docdig dei documenti di gara. Nella copia questa caratteristica
     * deve essere mantenuta
     *
     * @param status
     * @param nGaraSorgente
     * @param codiceGaraSorgente
     * @param nGaraDestinazione
     * @param codiceGaraDestinazione
     * @param lottoDiGara
     * @param entita
     * @throws GestoreException
     * @throws SQLException
     * @throws IOException
     */
    public void copiaDocumentazioneDGUE(TransactionStatus status,
        String nGaraSorgente, String codiceGaraSorgente,
        String nGaraDestinazione, String codiceGaraDestinazione,
        boolean lottoDiGara, String entita) throws GestoreException, SQLException, IOException {

      List<?> listaOccorenzeDaCopiare = null;
      List<?> listaDocumentiConcorrenti = null;
      DataColumnContainer campiDaCopiare = null;
      DataColumnContainer campiDaCopiareConcorrenti = null;
      long numOccorrenze = 0;
      String sql = "select * from DOCUMGARA where CODGAR = ?";
      String sqlConcorrenti=null;

      boolean copiaLottoInLottoUnico = false;
      if (!codiceGaraSorgente.startsWith("$")
          && codiceGaraDestinazione.startsWith("$")) {
        copiaLottoInLottoUnico = true;
      }

      Long bustalotti = null;


        if (copiaLottoInLottoUnico) {
          numOccorrenze = this.geneManager.countOccorrenze("DOCUMGARA",
              "CODGAR = ? and (NGARA = ? or NGARA is null) and (GRUPPO = 1 or GRUPPO = 6) and (ISARCHI!='1' or ISARCHI is null) and IDSTAMPA = 'DGUE'" , new Object[] {
                  codiceGaraSorgente, nGaraSorgente });
          sql += " and (NGARA = ? or NGARA is null)";
        } else if (lottoDiGara) {
          bustalotti = (Long)this.sqlManager.getObject("select bustalotti from gare where ngara=?", new Object[]{codiceGaraSorgente});
          if ((bustalotti == null || (bustalotti != null && bustalotti.longValue()!=1)) || ((new Long(1)).equals(bustalotti) && !codiceGaraSorgente.equals(codiceGaraDestinazione)) ) {
            numOccorrenze = this.geneManager.countOccorrenze("DOCUMGARA",
                "CODGAR = ? and NGARA = ? and (GRUPPO = 1 or GRUPPO = 6) and (ISARCHI!='1' or ISARCHI is null) and IDSTAMPA = 'DGUE'", new Object[] { codiceGaraSorgente,
                    nGaraSorgente });
            sql += " and NGARA = ?";
          }
        } else {
          if ("TORN".equals(entita)) {
            sql += " and NGARA is null";
          }
          numOccorrenze = this.geneManager.countOccorrenze("DOCUMGARA",
              "CODGAR = ? and (GRUPPO = 1 or GRUPPO = 6) and (ISARCHI!='1' or ISARCHI is null) and IDSTAMPA = 'DGUE'",  new Object[] { codiceGaraSorgente });

        }

        //Ricerca dei documenti di gara DGUE
        sql += " and (GRUPPO = 1 or GRUPPO = 6) and (ISARCHI!='1' or ISARCHI is null) and IDSTAMPA = 'DGUE' order by NORDDOCG";

        if (this.geneManager.getSql().isTable("DOCUMGARA") && numOccorrenze > 0) {
          if (lottoDiGara || copiaLottoInLottoUnico)
            listaOccorenzeDaCopiare = this.geneManager.getSql().getListHashMap(
                sql, new Object[] { codiceGaraSorgente, nGaraSorgente });
          else
            listaOccorenzeDaCopiare = this.geneManager.getSql().getListHashMap(
                sql, new Object[] { codiceGaraSorgente });

          if (listaOccorenzeDaCopiare != null
              && listaOccorenzeDaCopiare.size() > 0) {

            for (int row = 0; row < listaOccorenzeDaCopiare.size(); row++) {
              if (lottoDiGara || copiaLottoInLottoUnico)
                campiDaCopiare = new DataColumnContainer(
                    this.geneManager.getSql(), "DOCUMGARA", sql, new Object[] {
                        codiceGaraSorgente, nGaraSorgente });
              else
                campiDaCopiare = new DataColumnContainer(
                    this.geneManager.getSql(), "DOCUMGARA", sql,
                    new Object[] { codiceGaraSorgente });

              campiDaCopiare.setValoriFromMap(
                  (HashMap<?,?>) listaOccorenzeDaCopiare.get(row), true);

              String campiDaNonCopiare[] = new String[]{ "DOCUMGARA.DATARILASCIO"};
              campiDaCopiare.removeColumns(campiDaNonCopiare);

              campiDaCopiare.getColumn("CODGAR").setChiave(true);
              campiDaCopiare.setValue("CODGAR", codiceGaraDestinazione);
              // campiDaCopiare.setValue("NGARA",nGaraDestinazione);
              // se il campo NGARA è valorizzato, va sostituito con il valore di
              // nGaraDestinazione
              if (campiDaCopiare.getString("NGARA") != null
                  || copiaLottoInLottoUnico)
                campiDaCopiare.setValue("NGARA", nGaraDestinazione);

              campiDaCopiare.getColumn("NORDDOCG").setChiave(true);

              long newNorddocg = 1;
              if (!codiceGaraSorgente.equals(codiceGaraDestinazione)) {
                Long norddcog = campiDaCopiare.getLong("NORDDOCG");
                newNorddocg = norddcog.longValue();
              } else {
                // Si deve calcolare il valore di NORDDOCG
                Long maxNorddocg = (Long) this.geneManager.getSql().getObject(
                    "select max(NORDDOCG) from DOCUMGARA where CODGAR= ?",
                    new Object[] { codiceGaraDestinazione });

                if (maxNorddocg != null && maxNorddocg.longValue() > 0)
                  newNorddocg = maxNorddocg.longValue() + 1;
              }
              campiDaCopiare.setValue("NORDDOCG", new Long(newNorddocg));

              // Per adesso non gestisco la copia dei file, ossia W_DOCDIG
              campiDaCopiare.setValue("IDPRG", "PG");
              campiDaCopiare.setValue("IDDOCDG", null);

              //Si sbianca STATODOC
              campiDaCopiare.setValue("STATODOC", null);

              // Inserimento del nuovo record
              campiDaCopiare.insert("DOCUMGARA", this.geneManager.getSql());

              // Copia delle occorrenze di W_DOCDIG figlie di DOCUMGARA
              HashMap<?,?> hm = (HashMap<?,?>) listaOccorenzeDaCopiare.get(row);
              String idprg = hm.get("IDPRG").toString();
              Long iddocdg = ((JdbcParametro) hm.get("IDDOCDG")).longValue();

              long numOccorrenzeW_DOCDIG = this.geneManager.countOccorrenze(
                  "W_DOCDIG", "IDPRG = ? and IDDOCDIG = ?", new Object[] { idprg,
                      iddocdg });
              if (numOccorrenzeW_DOCDIG > 0) {
                // Il campo W_DOCDIG.DIGOGG è di tipo BLOB e va trattato
                // separatamente
                String select = "select IDPRG,IDDOCDIG,DIGENT,DIGKEY1,DIGKEY2,DIGTIPDOC,DIGNOMDOC,DIGDESDOC,DIGFIRMA from W_DOCDIG where IDPRG = ? and IDDOCDIG = ?";
                List<?> occorrenzeW_DOCDIGDaCopiare = this.geneManager.getSql().getListHashMap(
                    select, new Object[] { idprg, iddocdg });
                if (occorrenzeW_DOCDIGDaCopiare != null
                    && occorrenzeW_DOCDIGDaCopiare.size() > 0) {
                  for (int i = 0; i < occorrenzeW_DOCDIGDaCopiare.size(); i++) {
                    DataColumnContainer campiDaCopiareW_DOCDIG = new DataColumnContainer(
                        this.geneManager.getSql(), "W_DOCDIG", select,
                        new Object[] { idprg, iddocdg });
                    campiDaCopiareW_DOCDIG.setValoriFromMap(
                        (HashMap<?,?>) occorrenzeW_DOCDIGDaCopiare.get(i), true);
                    campiDaCopiareW_DOCDIG.getColumn("IDPRG").setChiave(true);

                    campiDaCopiareW_DOCDIG.getColumn("IDDOCDIG").setChiave(true);

                    // Si deve calcolare il valore di IDDOCDIG
                    Long maxIDDOCDIG = (Long) this.geneManager.getSql().getObject(
                        "select max(IDDOCDIG) from W_DOCDIG where IDPRG= ?",
                        new Object[] { idprg });

                    long newIDDOCDIG = 1;
                    if (maxIDDOCDIG != null && maxIDDOCDIG.longValue() > 0)
                      newIDDOCDIG = maxIDDOCDIG.longValue() + 1;

                    campiDaCopiareW_DOCDIG.setValue("IDDOCDIG", new Long(
                        newIDDOCDIG));
                    campiDaCopiareW_DOCDIG.setValue("DIGKEY1",
                        codiceGaraDestinazione);
                    campiDaCopiareW_DOCDIG.setValue("DIGKEY2", new Long(
                        newNorddocg));
                    BlobFile fileAllegato = null;
                    fileAllegato = fileAllegatoManager.getFileAllegato(idprg,
                        iddocdg);
                    ByteArrayOutputStream baos = null;
                    if (fileAllegato != null && fileAllegato.getStream() != null) {
                      baos = new ByteArrayOutputStream();
                      baos.write(fileAllegato.getStream());
                    }
                    campiDaCopiareW_DOCDIG.addColumn("W_DOCDIG.DIGOGG",
                        JdbcParametro.TIPO_BINARIO, baos);

                    // Inserimento del nuovo record su w_docdig
                    campiDaCopiareW_DOCDIG.insert("W_DOCDIG",
                        this.geneManager.getSql());

                    // Aggiornamento dei campi IDPRG e IDDOCDG di documgara
                    this.sqlManager.update(
                        "update DOCUMGARA set IDPRG=?,IDDOCDG = ? where CODGAR=? and NORDDOCG=?",
                        new Object[] { idprg, new Long(newIDDOCDIG),
                            codiceGaraDestinazione, new Long(newNorddocg) });

                    //Ricerca dei documenti concorrenti collegati allo stesso allegato dei documenti di gara
                    sqlConcorrenti = "select * from DOCUMGARA where CODGAR = ? and GRUPPO = 3 and (ISARCHI!='1' or ISARCHI is null) and IDSTAMPA = 'DGUE' and IDPRG=? and IDDOCDG=? ";
                    listaDocumentiConcorrenti = this.geneManager.getSql().getListHashMap(
                        sqlConcorrenti, new Object[] { codiceGaraSorgente, idprg, iddocdg });

                    if (listaDocumentiConcorrenti != null
                        && listaDocumentiConcorrenti.size() > 0) {
                      for (int riga = 0; riga < listaDocumentiConcorrenti.size(); riga++) {
                        campiDaCopiareConcorrenti = new DataColumnContainer(
                            this.geneManager.getSql(), "DOCUMGARA", sqlConcorrenti,
                            new Object[] { codiceGaraSorgente, idprg, iddocdg });

                        campiDaCopiareConcorrenti.setValoriFromMap(
                            (HashMap<?,?>) listaDocumentiConcorrenti.get(riga), true);

                        campiDaNonCopiare = new String[]{ "DOCUMGARA.DATARILASCIO"};
                        campiDaCopiareConcorrenti.removeColumns(campiDaNonCopiare);

                        campiDaCopiareConcorrenti.getColumn("CODGAR").setChiave(true);
                        campiDaCopiareConcorrenti.setValue("CODGAR", codiceGaraDestinazione);
                        // campiDaCopiare.setValue("NGARA",nGaraDestinazione);
                        // se il campo NGARA è valorizzato, va sostituito con il valore di
                        // nGaraDestinazione
                        if (campiDaCopiareConcorrenti.getString("NGARA") != null
                            || copiaLottoInLottoUnico)
                          campiDaCopiareConcorrenti.setValue("NGARA", nGaraDestinazione);

                        campiDaCopiareConcorrenti.getColumn("NORDDOCG").setChiave(true);

                        newNorddocg = 1;
                        if (!codiceGaraSorgente.equals(codiceGaraDestinazione)) {
                          Long norddcog = campiDaCopiareConcorrenti.getLong("NORDDOCG");
                          newNorddocg = norddcog.longValue();
                        } else {
                          // Si deve calcolare il valore di NORDDOCG
                          Long maxNorddocg = (Long) this.geneManager.getSql().getObject(
                              "select max(NORDDOCG) from DOCUMGARA where CODGAR= ?",
                              new Object[] { codiceGaraDestinazione });

                          if (maxNorddocg != null && maxNorddocg.longValue() > 0)
                            newNorddocg = maxNorddocg.longValue() + 1;
                        }
                        campiDaCopiareConcorrenti.setValue("NORDDOCG", new Long(newNorddocg));

                        campiDaCopiareConcorrenti.setValue("IDPRG", "PG");
                        campiDaCopiareConcorrenti.setValue("IDDOCDG", new Long(newIDDOCDIG));

                        //Si sbianca STATODOC
                        campiDaCopiareConcorrenti.setValue("STATODOC", null);

                        // Inserimento del nuovo record
                        campiDaCopiareConcorrenti.insert("DOCUMGARA", this.geneManager.getSql());

                      }

                    }
                  }
                }
              }
            }
          }
        }

  }

    /**
     *
     * @param json
     * @param ngara
     * @param ditta
     * @param data
     * @throws SQLException
     */
    public void deleteFileQform(String json, String ngara, String ditta, Timestamp data) throws Exception {
      if(json!=null) {
        JSONObject jsonOggetto = (JSONObject)JSONSerializer.toJSON(json);
        JSONObject surveyType = ((JSONObject)jsonOggetto.get(CostantiAppalti.sezioneDatiQuestionario));

        JSONObject result = ((JSONObject)surveyType.get("result"));
        JSONArray test = result.getJSONArray(CostantiAppalti.sezioneFileCancellatiQuestionario);
        String uuid=null;
        for(Object o: test){
          if ( o instanceof JSONObject ) {
            uuid = ((JSONObject)o).getString("uuid");
            this.sqlManager.update("update imprdocg set isarchi = ?, datadis =? where ngara=? and codimp=? and uuid=?",
                new Object[]{"1", data, ngara,ditta, uuid});

          }
        }
      }
    }
}