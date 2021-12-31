/*
 * Created on 02/ott/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.GestoreProfili;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.docass.DocumentiAssociatiManager;
import it.eldasoft.gene.commons.web.TempFileUtilities;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.dao.SqlDao;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.docass.DocumentoAssociato;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.docass.CostantiDocumentiAssociati;
import it.eldasoft.gene.web.struts.docass.GestioneFileDocumentiAssociatiException;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.excel.DizionarioStiliExcel;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityExcel;
import it.eldasoft.utils.utility.UtilityMath;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddressList;

/**
 * Classe per la gestione delle funzionalita' di import/export dell'offerta
 * prezzi su foglio Excel
 *
 * @author Luca.Giacomazzo
 */
public class ImportExportOffertaPrezziManager {


  static Logger                     logger = Logger.getLogger(ImportExportOffertaPrezziManager.class);

  private SqlDao                    sqlDao;

  /** Manager con funzionalita' generali */
  private GeneManager               geneManager;

  /** Manager dei documenti associati */
  private DocumentiAssociatiManager documentiAssociatiManager;

  /** Manager dei tabellati */
  private TabellatiManager          tabellatiManager;

  /** Manager per l'applicazione PG */
  private PgManager                 pgManager;

  /** Manager della W_GENCHIAVI */
  private GenChiaviManager			genChiaviManager;

  private GestioneWSERPManager gestioneWSERPManager;

  public void setSqlDao(SqlDao sqlDao) {
    this.sqlDao = sqlDao;
  }

  /**
   * @param geneManager
   *        geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  /**
   * @param documentiAssociatiManager
   *        documentiAssociatiManager da settare internamente alla classe.
   */
  public void setDocumentiAssociatiManager(
      DocumentiAssociatiManager documentiAssociatiManager) {
    this.documentiAssociatiManager = documentiAssociatiManager;
  }

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * @param pgManager
   *        pgManager da settare internamente alla classe.
   */
  public void setPgManager(PgManager pgManager) {
    this.pgManager = pgManager;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
	this.genChiaviManager = genChiaviManager;
  }

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }

  private final String       FOGLIO_DATI_GARA                                             = "Dati gara";

  private final String       FOGLIO_LAVORAZIONE_E_FORNITURE[]                             = {
      "Lavorazioni e forniture", "Sommario"                                              };

  private final String       FOGLIO_LISTA_PRODOTTI[]                             = {
      "Lista prodotti", "Sommario"                                              };

  private final String       FOGLIO_OFFERTA_PREZZI[]                             = {
      "Dettaglio offerta prezzi", "Sommario"                                              };

  private final int          FOGLIO_LAVORAZIONE_E_FORNITURE_RIGA_NOME_FISICO_CAMPI        = 3;

  private final int          FOGLIO_OFFERTA_DITTE_RIGA_NOME_FISICO_CAMPI                  = 3;

  private final int          FOGLIO_DATI_GARA_RIGA_INIZIALE_DATI_GARA                     = 4;

  private final int          FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE                = 10;

  private final int          FOGLIO_OFFERTA_PREZZI_RIGA_INIZIALE                          = 10;

  private final int          IMPORT_NUMERO_RIGHE_CONSECUTIVE_VUOTE                        = 10;

  /**
   * Per il foglio 'Lavorazioni e forniture', rappresenta il numero di righe da
   * formattare dopo l'ultima occorrenza della GCAP estratta
   */
  private final int          FOGLIO_LAVORAZIONE_E_FORNITURE_ULTERIORI_RIGHE_DA_FORMATTARE = 20;

  private final int          FOGLIO_OFFERTA_PREZZI_ULTERIORI_RIGHE_DA_FORMATTARE          = 20;

  private final DVConstraint DATA_VALIDATION_CONSTRAINT_SI_NO                             = DVConstraint.createExplicitListConstraint(new String[] {
      "si", "no"                                                                         });

  private final DVConstraint DATA_VALIDATION_CONSTRAINT_CORPO_MISURA                      = DVConstraint.createExplicitListConstraint(new String[] {
      "a corpo", "a misura"                                                              });

  /**
   * Nome del file associato al file Excel contenente l'export delle lavorazioni
   * e forniture
   */
  private final String       NOME_FILE_C0OGGASS_EXPORT                                    = "LavorazioniFornitureGara.xls";

  /**
   * Titolo del documento associato con cui viene inserito il file Excel
   * contenente l'export lavorazioni e forniture
   */
  private final String       TITOLO_FILE_C0OGASS_EXPORT                                   = "Lista lavorazioni e forniture formato excel (esportazione)";

  /**
   * Nome del file con cui viene inserito in C0OGGASS il file Excel contenente i
   * ddati per l'offerta prezzi in fase di import
   */
  private final String       NOME_FILE_C0OGGASS_IMPORT                                    = "LavorazioniFornitureGara-import.xls";

  /**
   * Titolo del documento associato con cui viene inserito il file Excel
   * contenente l'import lavorazioni e forniture lato gara
   */
  private final String       TITOLO_FILE_C0OGASS_IMPORT                                   = "Lista lavorazioni e forniture formato excel (importazione)";

  /**
   * Titolo del documento associato con cui viene inserito il file Excel
   * contenente l'import lavorazioni e forniture lato ditta
   */
  private final String       TITOLO_FILE_C0OGASS_IMPORT_DITTA                             = "Lista lavoraz. e forniture in excel della ditta cod.";

  private final String       SCHEMA_CAMPI                                                 = "GARE";


  /**
   * Nome del file associato al file Excel contenente l'export dell'offerta
   * prezzi
   */
  private final String       NOME_FILE_EXPORT_OFFERTA_PREZZI                               = "OffertaPrezziDitteInGara.xls";

  // private final String TABELLA_CAMPI = "GCAP";

  private HSSFWorkbook       workBook                                                     = null;

  private String[]           arrayCampi;
  private int[]              arrayStiliCampi;
  private String[]           arrayTitoloColonne;
  private int[]              arrayLarghezzaColonne;
  private boolean[]          arrayCampiVisibili;
  private int[]              arrayIndiceColonnaCampi;
  private boolean            nuovaGestioneRibsubAttiva                                    = false;
  private List<?>               listaDatiXdpre                                               = null;
  private String[]           arrayFormatoCampiXdpre;
  /**
   * Posizione della colonna nascosta che conterrà l'informazione relativa al CONTAF.
   */
  private final int          posCampoContaf                                               = 60;
  private boolean            controlloUnivocitaCodvoc                                     = false;
  private String             valoreCodvoc;
  private String             cellaCodvoc;

  /**
   * Export dell'offerta prezzi su file Excel con salvataggio dello stesso nella
   * directory temporanea dell'application server e possibilita' di salvare lo
   * stesso come documento associato. Ritorna il nome del file salvato nella
   * directory temporanea
   *
   * @param ngara
   *        codice della gara a lotto unico o del lotto di gara
   * @param exportPrezziUnitari
   *        flag indicante se esportare sul file Excel i prezzi unitari o meno
   * @param salvaXLSInDocAss
   *        flag indicante se salvare il file Excel prodotto come documento
   *        associato o meno
   * @param isGaraLottiConOffertaUnica
   *        flaf per indicare se la gara è ad Offerta Unica
   * @param session
   *        sessione dell'utente
   * @param ribcal
   *        ribcal della gara
   * @return Ritorna il nome del file salvato nella cartella temporanea
   * @throws GestioneFileDocumentiAssociatiException
   * @throws NullPointerException
   * @throws GestioneFileDocumentiAssociatiException
   * @throws FileNotFoundException
   * @throws IOException
   * @throws SQLException
   * @throws GestoreException
   * @throws Exception
   */
  public String exportOffertaPrezzi(String ngara, boolean exportPrezziUnitari,
      boolean salvaXLSInDocAss, boolean isGaraLottiConOffertaUnica,
      HttpSession session, String ribcal) throws GestioneFileDocumentiAssociatiException,
      FileNotFoundException, SQLException, GestoreException, IOException,
      Exception {

    String profiloAttivo = "PG_DEFAULT";
    String moduloAttivo = "PG";
    if (session != null) {
      profiloAttivo = (String) session.getAttribute("profiloAttivo");
      moduloAttivo = (String) session.getAttribute("moduloAttivo");
    }

    // Gestione delle configurazioni.
    // In funzione del tipo di fornitura si devono creare gli array opportuni
    Long tipoFornitura = null;

    Vector<?> datiTORN = this.sqlDao.getVectorQuery(
        "select torn.tipforn from torn, gare where torn.codgar = gare.codgar1 and gare.ngara = ?",
        new Object[] { ngara });
    if (datiTORN != null && datiTORN.size() > 0) {
      if (SqlManager.getValueFromVectorParam(datiTORN, 0) != null) {
        tipoFornitura = SqlManager.getValueFromVectorParam(datiTORN, 0).longValue();
      }
    }


    if (tipoFornitura == null) tipoFornitura = new Long(3);

    boolean pesoVisibile=false;
    if("3".equals(ribcal))
      pesoVisibile=true;
    this.setDefinizioni(tipoFornitura, null, pesoVisibile);

    this.workBook = new HSSFWorkbook();
    // Creazione del dizionario degli stili delle celle dell'intero file Excel
    DizionarioStiliExcel dizStiliExcel = new DizionarioStiliExcel(this.workBook);

    // Scrittura sul foglio Excel dei dati generali della gara
    this.setDatiGeneraliGara(ngara, profiloAttivo, isGaraLottiConOffertaUnica,
        dizStiliExcel);

    // Scrittura sul foglio Excel delle lavorazioni e forniture della gara

    this.setLavorazioniForniture(ngara, profiloAttivo, exportPrezziUnitari,
        isGaraLottiConOffertaUnica, dizStiliExcel, tipoFornitura, ribcal);


    // Set del foglio 'Lavorazioni e forniture' come foglio attivo
    if (new Long(98).equals(tipoFornitura)){
      this.workBook.setActiveSheet(this.workBook.getSheetIndex(FOGLIO_LISTA_PRODOTTI[0]));
      this.workBook.setSelectedTab(this.workBook.getSheetIndex(FOGLIO_LISTA_PRODOTTI[0]));
    }else{
      this.workBook.setActiveSheet(this.workBook.getSheetIndex(FOGLIO_LAVORAZIONE_E_FORNITURE[0]));
      this.workBook.setSelectedTab(this.workBook.getSheetIndex(FOGLIO_LAVORAZIONE_E_FORNITURE[0]));

    }

    // Creazione di un file temporaneo nella cartella temporanea e salvataggio
    // del suo nome in sessione nell'oggetto TempfileDeleter
    String nomeFile = ngara.toUpperCase().replaceAll("/", "_");
    nomeFile += "_" + FilenameUtils.getBaseName(NOME_FILE_C0OGGASS_EXPORT) +
        "." + FilenameUtils.getExtension(NOME_FILE_C0OGGASS_EXPORT);
    File tempFile = null;
    if (session == null) {
      tempFile = new File(new File(System.getProperty("java.io.tmpdir")),nomeFile);
    } else {
      tempFile = TempFileUtilities.getTempFileSenzaNumeoRandom(nomeFile,
          session);
    }
    FileOutputStream fos = new FileOutputStream(tempFile);
    // Scrittura dell'oggetto workBook nel file temporaneo
    this.workBook.write(fos);
    fos.close();

    // Inserimento come documento associato alla gara del file Excel generato,
    // copiando il file dalla cartella temp a quella dei documenti associati
    if (salvaXLSInDocAss) {
      DocumentoAssociato docAss = new DocumentoAssociato();
      docAss.setCodApp(moduloAttivo);
      if (isGaraLottiConOffertaUnica)
        docAss.setEntita("TORN");
      else
        docAss.setEntita("GARE");
      docAss.setCampoChiave1(ngara);
      docAss.setCampoChiave2("#");
      docAss.setCampoChiave3("#");
      docAss.setCampoChiave4("#");
      docAss.setCampoChiave5("#");
      docAss.setDataInserimento(new Date());
      docAss.setTitolo(TITOLO_FILE_C0OGASS_EXPORT);
      docAss.setNomeDocAss(NOME_FILE_C0OGGASS_EXPORT);
      docAss.setPathDocAss(CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT);

      this.documentiAssociatiManager.associaFile(docAss,
          FileUtils.readFileToByteArray(tempFile));
    }
    return tempFile.getName();
    // } else
    // throw new NullPointerException("modelloNonDefinito");
  }

  /**
   * Set degli array di definizione in funzione del tipo di fornitura
   * Se non specificato il tipoFornitura, si considera la gestione
   * dell'offerta prezzi
   *
   * @param tipoFornitura
   * @param gara
   * @param gestioneRibassoPesato
   * @throws SQLException
   * @throws GestoreException
   */
  private void setDefinizioni(Long tipoFornitura, String gara, boolean gestioneRibassoPesato) throws SQLException, GestoreException {

    if (tipoFornitura != null) {
      if (new Long(1).equals(tipoFornitura)) {

        int numeroTotaleCampi = 39;
        int cnt = 0;

        arrayCampi = new String[numeroTotaleCampi];
        arrayStiliCampi = new int[numeroTotaleCampi];
        arrayTitoloColonne = new String[numeroTotaleCampi];
        arrayLarghezzaColonne = new int[numeroTotaleCampi];
        arrayCampiVisibili = new boolean[numeroTotaleCampi];
        arrayIndiceColonnaCampi = new int[numeroTotaleCampi];

        // 0
        arrayCampi[cnt] = "GARE.GCAP.NGARA";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Codice lotto";
        arrayLarghezzaColonne[cnt] = 14;
        arrayCampiVisibili[cnt] = true;

        // 1
        cnt++;
        arrayCampi[cnt] = "GARE.GARE.CODIGA";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.INTERO_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Lotto";
        arrayLarghezzaColonne[cnt] = 6;
        arrayCampiVisibili[cnt] = true;

        // 2
        cnt++;
        arrayCampi[cnt] = "GARE.GARE.CODCIG";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Codice CIG";
        arrayLarghezzaColonne[cnt] = 10;
        arrayCampiVisibili[cnt] = true;

        // 3
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.CODVOC";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Voce lotto";
        arrayLarghezzaColonne[cnt] = 14;
        arrayCampiVisibili[cnt] = true;

        // 4
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.VOCE";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Descrizione";
        arrayLarghezzaColonne[cnt] = 25;
        arrayCampiVisibili[cnt] = false;

        // 5
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP_EST.DESEST";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Descrizione estesa";
        arrayLarghezzaColonne[cnt] = 65;
        arrayCampiVisibili[cnt] = false;

        // 6
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.CODCAT";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Categoria";
        arrayLarghezzaColonne[cnt] = 25;
        arrayCampiVisibili[cnt] = false;

        // 7
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.CLASI1";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "A corpo o a misura?";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

        // 8
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.SOLSIC";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Solo sicurezza?";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

        // 9
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.SOGRIB";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Sogg. a ribasso?";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

        // 10
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.UNIMIS";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Um";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

        // 11
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.QUANTI";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE1_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Quantità complessive";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

        // 12
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.PREZUN";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Prezzo unitario";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

        // 13
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP_SAN.CODATC";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Codice ATC";
        arrayLarghezzaColonne[cnt] = 15;
        arrayCampiVisibili[cnt] = true;

        // 14
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP_SAN.CODAUR";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Codice AUR prodotto richiesto";
        arrayLarghezzaColonne[cnt] = 15;
        arrayCampiVisibili[cnt] = true;

        // 15
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP_SAN.PRINCATT";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Principio attivo";
        arrayLarghezzaColonne[cnt] = 15;
        arrayCampiVisibili[cnt] = true;

        // 16
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP_SAN.FORMAFARM";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Forma farmaceutica";
        arrayLarghezzaColonne[cnt] = 15;
        arrayCampiVisibili[cnt] = true;

        // 17
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP_SAN.DOSAGGIO";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Dosaggio";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 18
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP_SAN.VIASOMM";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Via di somministrazione";
        arrayLarghezzaColonne[cnt] = 20;
        arrayCampiVisibili[cnt] = true;

        // 19
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.UNIMIS";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Unità di misura";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 20
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.QUANTI";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE1_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Fabbisogno relativo a 12 mesi in unità di misura";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 21
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.PREZUN";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Base di gara unitaria (euro, IVA esclusa)";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 22
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.PERCIVA";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.INTERO_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "IVA";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

        // 23
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP_SAN.NOTE";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Note";
        arrayLarghezzaColonne[cnt] = 25;
        arrayCampiVisibili[cnt] = true;

        // 24
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.DENOMPROD";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Denominazione del prodotto";
        arrayLarghezzaColonne[cnt] = 25;
        arrayCampiVisibili[cnt] = true;

        // 25
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.CODAIC";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Codice AIC";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 26
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.CODAUR";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Codice AUR prodotto richiesto";
        arrayLarghezzaColonne[cnt] = 15;
        arrayCampiVisibili[cnt] = false;

        // 27
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.NUNICONF";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Numero unità per confezione";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 28
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.CLASRESP";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Classe di rimborsabilità (A,C,H)";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 29
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.PREZVPUBBL";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Prezzo di vendita al pubblico IVA inclusa (compliare obbligatoriamente anche per gli ex factory e per emoderivati classe A)";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 30
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.IVAPVPUBBL";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.INTERO_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "IVA";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 31
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.PSCONTOBL";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Per i farmaci ex factory per i quali è obbligatorio indicare la % di sconto obbligatorio aggiuntivo al 33,35% sulle forniture cedute a strutture pubbliche del SSN offerto";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 32
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.ESTRGUSO";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Indicare gli estremi della G.U. in cui è stata pubblicata la % di sconto della colonna precedente";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 33
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE.PREOFF";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Prezzo unitario di offerta IVA esclusa";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 34
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.PREZUNRIF";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Prezzo unitario di riferimento (ex factory o 50% o emoderivato)";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 35
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.SCONTOFF";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Sconto offerto per prodotto";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 36
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.SCONTOBBL";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Sconto obbligatorio per legge";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 37
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.SCONTAGG";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Sconto aggiuntivo rispetto a quello obbligatorio";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 38
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.CONTAF";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Numero progressivo";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

      } else if (new Long(2).equals(tipoFornitura)) {

        int numeroTotaleCampi = 30;
        int cnt = 0;

        arrayCampi = new String[numeroTotaleCampi];
        arrayStiliCampi = new int[numeroTotaleCampi];
        arrayTitoloColonne = new String[numeroTotaleCampi];
        arrayLarghezzaColonne = new int[numeroTotaleCampi];
        arrayCampiVisibili = new boolean[numeroTotaleCampi];
        arrayIndiceColonnaCampi = new int[numeroTotaleCampi];

        // 0
        arrayCampi[cnt] = "GARE.GCAP.NGARA";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Codice lotto";
        arrayLarghezzaColonne[cnt] = 14;
        arrayCampiVisibili[cnt] = true;

        // 1
        cnt++;
        arrayCampi[cnt] = "GARE.GARE.CODIGA";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.INTERO_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Lotto";
        arrayLarghezzaColonne[cnt] = 6;
        arrayCampiVisibili[cnt] = true;

        // 2
        cnt++;
        arrayCampi[cnt] = "GARE.GARE.CODCIG";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Codice CIG";
        arrayLarghezzaColonne[cnt] = 10;
        arrayCampiVisibili[cnt] = true;

        // 3
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.CODVOC";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Voce lotto";
        arrayLarghezzaColonne[cnt] = 14;
        arrayCampiVisibili[cnt] = true;

        // 4
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.VOCE";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Descrizione";
        arrayLarghezzaColonne[cnt] = 25;
        arrayCampiVisibili[cnt] = false;

        // 5
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP_EST.DESEST";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Descrizione estesa";
        arrayLarghezzaColonne[cnt] = 65;
        arrayCampiVisibili[cnt] = false;

        // 6
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.CODCAT";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Categoria";
        arrayLarghezzaColonne[cnt] = 25;
        arrayCampiVisibili[cnt] = false;

        // 7
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.CLASI1";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "A corpo o a misura?";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

        // 8
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.SOLSIC";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Solo sicurezza?";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

        // 9
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.SOGRIB";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Sogg. a ribasso?";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

        // 10
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.UNIMIS";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Um";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

        // 11
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.QUANTI";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE1_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Quantità complessive";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

        // 12
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.PREZUN";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Prezzo unitario";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

        // 13
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.PERCIVA";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.INTERO_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "IVA";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

        // 14
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP_SAN.CODCLASS";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Codice CND";
//        arrayTitoloColonne[cnt] = "Codice classificazione (derivata dalla classificazione nazionale)";
        arrayLarghezzaColonne[cnt] = 15;
        arrayCampiVisibili[cnt] = true;

        // 15
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP_SAN.DEPRODCN";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Descrizione CND";
//        arrayTitoloColonne[cnt] = "Descrizione del prodotto derivato dalla codifica nazionale";
        arrayLarghezzaColonne[cnt] = 25;
        arrayCampiVisibili[cnt] = true;

        // 16
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP_SAN.CODAUR";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Codice AUR prodotto richiesto";
        arrayLarghezzaColonne[cnt] = 15;
        arrayCampiVisibili[cnt] = true;

        // 17
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP_SAN.DPRODCAP";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Descrizione del prodotto da capitolato";
        arrayLarghezzaColonne[cnt] = 25;
        arrayCampiVisibili[cnt] = true;

        // 18
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.QUANTI";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE1_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Quantità complessive";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 19
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.PREZUN";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Base di gara unitaria (euro, IVA esclusa)";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 20
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.DENOMPROD";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Descrizione prodotto del fornitore";
        arrayLarghezzaColonne[cnt] = 25;
        arrayCampiVisibili[cnt] = true;

        // 21
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.CODPROD";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Codice prodotto del fornitore";
        arrayLarghezzaColonne[cnt] = 15;
        arrayCampiVisibili[cnt] = true;

        // 22
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.CODAUR";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Codice AUR prodotto offerto";
        arrayLarghezzaColonne[cnt] = 15;
        arrayCampiVisibili[cnt] = false;

        // 23
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.NREPDISP";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Numero di repertorio dispositivo";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 24
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.REF";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "REF";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 25
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.QUANTICONF";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Quantità di prodotto in ciascuna confezione";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

        // 26
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE.PREOFF";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Prezzo unitario offerto";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 27
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.IVAPVPUBBL";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE0_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Aliquota IVA";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 28
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.NOTE";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Note";
        arrayLarghezzaColonne[cnt] = 25;
        arrayCampiVisibili[cnt] = true;

        // 29
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.CONTAF";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Numero progressivo";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

      } else if (new Long(98).equals(tipoFornitura)) {
        // Istituto Zooprofilattico
        int numeroTotaleCampi = 18;           //;
        int cnt = 0;

        arrayCampi = new String[numeroTotaleCampi];
        arrayStiliCampi = new int[numeroTotaleCampi];
        arrayTitoloColonne = new String[numeroTotaleCampi];
        arrayLarghezzaColonne = new int[numeroTotaleCampi];
        arrayCampiVisibili = new boolean[numeroTotaleCampi];
        arrayIndiceColonnaCampi = new int[numeroTotaleCampi];

        // 0
        arrayCampi[cnt] = "GARE.GCAP.NGARA";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Codice lotto";
        arrayLarghezzaColonne[cnt] = 14;
        arrayCampiVisibili[cnt] = true;

        // 1
        cnt++;
        arrayCampi[cnt] = "GARE.GARE.CODIGA";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.INTERO_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Lotto";
        arrayLarghezzaColonne[cnt] = 6;
        arrayCampiVisibili[cnt] = true;

        // 2
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.NORVOC";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.INTERO_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Pos.";
        arrayLarghezzaColonne[cnt] = 6;
        arrayCampiVisibili[cnt] = true;

        // 3
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.CODVOC";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Codice IZS";
        arrayLarghezzaColonne[cnt] = 20;
        arrayCampiVisibili[cnt] = true;

        // 4
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.VOCE";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Descrizione";
        arrayLarghezzaColonne[cnt] = 65;
        arrayCampiVisibili[cnt] = true;

        // 5
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP_EST.DESEST";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Note";
        arrayLarghezzaColonne[cnt] = 65;
        arrayCampiVisibili[cnt] = true;

        // 6
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.UNIMIS";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Um";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 7
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.QUANTI";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE1_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Quantità richiesta in Um";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 8
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.IVAPROD";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.INTERO_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "IVA";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 9
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.NUNICONF";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE1_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Quantità max. per confezione in Um";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

//--------------------------OFFERTA----------------------------------------//
        // 10
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.CODPROD";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Codice prodotto offerto";
        arrayLarghezzaColonne[cnt] = 15;
        arrayCampiVisibili[cnt] = true;

        // 11
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.DENOMPROD";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Descrizione prodotto offerto";
        arrayLarghezzaColonne[cnt] = 65;
        arrayCampiVisibili[cnt] = true;

        // 12
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE.UNIMIS";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Um Confezione";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

        // 13
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.NUNICONF";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Quantità per confezione in Um";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 14
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE.QUANTI";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE1_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Numero confezioni";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 15
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE.PREOFF";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Prezzo per confezione";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 16
        cnt++;
        arrayCampi[cnt] = "GARE.DPRE_SAN.IVAPVPUBBL";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.INTERO_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "IVA confezione";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 17
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.CONTAF";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Numero progressivo";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

      } else {

        int numeroTotaleCampi = 16;
        int cnt = 0;

        arrayCampi = new String[numeroTotaleCampi];
        arrayStiliCampi = new int[numeroTotaleCampi];
        arrayTitoloColonne = new String[numeroTotaleCampi];
        arrayLarghezzaColonne = new int[numeroTotaleCampi];
        arrayCampiVisibili = new boolean[numeroTotaleCampi];
        arrayIndiceColonnaCampi = new int[numeroTotaleCampi];

        // 0
        arrayCampi[cnt] = "GARE.GCAP.NGARA";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Codice lotto";
        arrayLarghezzaColonne[cnt] = 14;
        arrayCampiVisibili[cnt] = true;

        // 1
        cnt++;
        arrayCampi[cnt] = "GARE.GARE.CODIGA";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.INTERO_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Lotto";
        arrayLarghezzaColonne[cnt] = 6;
        arrayCampiVisibili[cnt] = true;

        // 2
        cnt++;
        arrayCampi[cnt] = "GARE.GARE.CODCIG";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Codice CIG";
        arrayLarghezzaColonne[cnt] = 10;
        arrayCampiVisibili[cnt] = true;

        // 3
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.CODVOC";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Voce lotto";
        arrayLarghezzaColonne[cnt] = 14;
        arrayCampiVisibili[cnt] = true;

        // 4
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.VOCE";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Descrizione";
        arrayLarghezzaColonne[cnt] = 25;
        arrayCampiVisibili[cnt] = true;

        // 5
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP_EST.DESEST";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Descrizione estesa";
        arrayLarghezzaColonne[cnt] = 65;
        arrayCampiVisibili[cnt] = true;

        // 6
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.CODCAT";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Categoria";
        arrayLarghezzaColonne[cnt] = 25;
        arrayCampiVisibili[cnt] = true;

        // 7
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.CLASI1";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "A corpo o a misura?";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 8
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.SOLSIC";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Solo sicurezza?";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 9
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.SOGRIB";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Sogg. a ribasso?";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 10
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.UNIMIS";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Um";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 11
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.QUANTI";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE1_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Quantità complessive";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 12
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.PREZUN";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Prezzo unitario";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 13
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.PERCIVA";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.INTERO_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "IVA";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = true;

        // 14
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.PESO";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE4_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Peso";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = gestioneRibassoPesato;

        // 15
        cnt++;
        arrayCampi[cnt] = "GARE.GCAP.CONTAF";
        arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
        arrayTitoloColonne[cnt] = "Numero progressivo";
        arrayLarghezzaColonne[cnt] = 11;
        arrayCampiVisibili[cnt] = false;

      }

      if (new Long(98).equals(tipoFornitura)){
        for (int i = 0; i < 16; i++) {
          arrayIndiceColonnaCampi[i] = i + 1;
        }
        for (int i = 16; i < arrayCampi.length; i++) {
          arrayIndiceColonnaCampi[i] = i + 2;
        }
      }else{
        for (int i = 0; i < arrayCampi.length; i++) {
          arrayIndiceColonnaCampi[i] = i + 1;
        }
      }

    }else{
      //Gestione Offerta Prezzi
      int numeroTotaleCampi = 19;
      int cnt = 0;

      //Si deve vedere se vi sono campi definiti col generatore attributi
      //e associati alla gara tramite GARCONFADATI
      listaDatiXdpre = this.sqlDao.getVectorQueryForList("select dyncam_name, dyncam_desc, dyncam_form, dyncam_dom,dyncam_tab from dyncam_gen,garconfdati "
          + "where dynent_name=entita and dyncam_name = campo and dynent_name=? and ngara=? order by numord", new Object[]{"XDPRE",gara});
      if(listaDatiXdpre!=null && listaDatiXdpre.size()>0)
        numeroTotaleCampi+= listaDatiXdpre.size();

      arrayCampi = new String[numeroTotaleCampi];
      arrayStiliCampi = new int[numeroTotaleCampi];
      arrayTitoloColonne = new String[numeroTotaleCampi];
      arrayLarghezzaColonne = new int[numeroTotaleCampi];
      arrayCampiVisibili = new boolean[numeroTotaleCampi];
      arrayIndiceColonnaCampi = new int[numeroTotaleCampi];
      arrayFormatoCampiXdpre = new String[listaDatiXdpre.size()];

      // 0
      arrayCampi[cnt] = "GARE.V_GCAP_DPRE.NGARA";
      arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
      arrayTitoloColonne[cnt] = "Codice lotto";
      arrayLarghezzaColonne[cnt] = 14;
      arrayCampiVisibili[cnt] = true;

      // 1
      cnt++;
      arrayCampi[cnt] = "GARE.V_GCAP_DPRE.CODIGA";
      arrayStiliCampi[cnt] = DizionarioStiliExcel.INTERO_ALIGN_CENTER;
      arrayTitoloColonne[cnt] = "Lotto";
      arrayLarghezzaColonne[cnt] = 6;
      arrayCampiVisibili[cnt] = true;

      // 2
      cnt++;
      arrayCampi[cnt] = "GENE.IMPR.NOMEST";
      arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
      arrayTitoloColonne[cnt] = "Ragione sociale ditta";
      arrayLarghezzaColonne[cnt] = 65;
      arrayCampiVisibili[cnt] = true;

      // 3
      cnt++;
      arrayCampi[cnt] = "GENE.IMPR.CFIMP";
      arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
      arrayTitoloColonne[cnt] = "C.F./P.Iva";
      arrayLarghezzaColonne[cnt] = 16;
      arrayCampiVisibili[cnt] = true;

      // 4
      cnt++;
      arrayCampi[cnt] = "GARE.GCAP.CODVOC";
      arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
      arrayTitoloColonne[cnt] = "Voce lotto";
      arrayLarghezzaColonne[cnt] = 14;
      arrayCampiVisibili[cnt] = true;


      // 5
      cnt++;
      arrayCampi[cnt] = "GARE.V_GCAP_DPRE.VOCE";
      arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
      arrayTitoloColonne[cnt] = "Descrizione";
      arrayLarghezzaColonne[cnt] = 25;
      arrayCampiVisibili[cnt] = true;

      // 6
      cnt++;
      arrayCampi[cnt] = "GARE.GCAP_EST.DESEST";
      arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_LEFT;
      arrayTitoloColonne[cnt] = "Descrizione estesa";
      arrayLarghezzaColonne[cnt] = 65;
      arrayCampiVisibili[cnt] = true;

      // 7
      cnt++;
      arrayCampi[cnt] = "GARE.GCAP.CLASI1";
      arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
      arrayTitoloColonne[cnt] = "A corpo o a misura?";
      arrayLarghezzaColonne[cnt] = 11;
      arrayCampiVisibili[cnt] = true;

      // 8
      cnt++;
      arrayCampi[cnt] = "GARE.V_GCAP_DPRE.SOLSIC";
      arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
      arrayTitoloColonne[cnt] = "Solo sicurezza?";
      arrayLarghezzaColonne[cnt] = 11;
      arrayCampiVisibili[cnt] = true;

      // 9
      cnt++;
      arrayCampi[cnt] = "GARE.V_GCAP_DPRE.SOGRIB";
      arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
      arrayTitoloColonne[cnt] = "Sogg. a ribasso?";
      arrayLarghezzaColonne[cnt] = 11;
      arrayCampiVisibili[cnt] = true;

      // 10
      cnt++;
      arrayCampi[cnt] = "GARE.UNIMIS.DESUNI";
      arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
      arrayTitoloColonne[cnt] = "Um";
      arrayLarghezzaColonne[cnt] = 11;
      arrayCampiVisibili[cnt] = true;

      // 11
      cnt++;
      arrayCampi[cnt] = "GARE.V_GCAP_DPRE.QUANTIEFF";
      arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE1_ALIGN_RIGHT;
      arrayTitoloColonne[cnt] = "Quantità";
      arrayLarghezzaColonne[cnt] = 11;
      arrayCampiVisibili[cnt] = true;

      //12
      cnt++;
      arrayCampi[cnt] = "GARE.V_GCAP_DPRE.PERRIB";
      arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE1_ALIGN_RIGHT;
      arrayTitoloColonne[cnt] = "Ribasso";
      arrayLarghezzaColonne[cnt] = 11;
      arrayCampiVisibili[cnt] = gestioneRibassoPesato;

      // 13
      cnt++;
      arrayCampi[cnt] = "GARE.V_GCAP_DPRE.PREOFF";
      arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
      arrayTitoloColonne[cnt] = "Prezzo unitario";
      arrayLarghezzaColonne[cnt] = 11;
      arrayCampiVisibili[cnt] = true;

      // 14
      cnt++;
      arrayCampi[cnt] = "GARE.V_GCAP_DPRE.PERCIVAEFF";
      arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE1_ALIGN_RIGHT;
      arrayTitoloColonne[cnt] = "Iva";
      arrayLarghezzaColonne[cnt] = 11;
      arrayCampiVisibili[cnt] = true;

      // 15
      cnt++;
      arrayCampi[cnt] = "GARE.V_GCAP_DPRE.IMPOFF";
      arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
      arrayTitoloColonne[cnt] = "Importo";
      arrayLarghezzaColonne[cnt] = 11;
      arrayCampiVisibili[cnt] = true;

      // 16
      cnt++;
      arrayCampi[cnt] = "GARE.V_GCAP_DPRE.PESO";
      arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
      arrayTitoloColonne[cnt] = "Peso";
      arrayLarghezzaColonne[cnt] = 11;
      arrayCampiVisibili[cnt] = gestioneRibassoPesato;

      // 17
      cnt++;
      arrayCampi[cnt] = "GARE.V_GCAP_DPRE.RIBPESO";
      arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
      arrayTitoloColonne[cnt] = "Ribasso pesato";
      arrayLarghezzaColonne[cnt] = 11;
      arrayCampiVisibili[cnt] = gestioneRibassoPesato;

      // 18
      cnt++;
      arrayCampi[cnt] = "GARE.GCAP.CONTAF";
      arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
      arrayTitoloColonne[cnt] = "Numero progressivo";
      arrayLarghezzaColonne[cnt] = 11;
      arrayCampiVisibili[cnt] = false;

      //Gestione campi XDPRE
      if (listaDatiXdpre != null && listaDatiXdpre.size() > 0) {
        for (int i=0; i<listaDatiXdpre.size(); i++) {
          Vector<?> record = (Vector<?>) listaDatiXdpre.get(i);
          if (record != null && record.size() > 0) {
            String nomeCampo =  SqlManager.getValueFromVectorParam(record, 0).stringValue();
            String descrizione = SqlManager.getValueFromVectorParam(record, 1).stringValue();
            String definizione = SqlManager.getValueFromVectorParam(record, 2).stringValue();
            String dominio = SqlManager.getValueFromVectorParam(record, 3).stringValue();
            String tabellato = SqlManager.getValueFromVectorParam(record, 4).stringValue();

            cnt++;
            arrayCampi[cnt] = "GARE.XDPRE." + nomeCampo;
            //arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
            arrayTitoloColonne[cnt] = descrizione;
            // arrayLarghezzaColonne[cnt] = 11;
            arrayCampiVisibili[cnt] = true;
            //Gestione del formato
            if(definizione!=null){
              if(definizione.startsWith("VC")){
                //Campo Stringa
                arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
                String tmp = definizione.substring(2);
                if(tmp!=null) {
                  int dimCampo = (new Long(tmp)).intValue();
                  if(dimCampo<65 && dimCampo >11)
                    arrayLarghezzaColonne[cnt] = dimCampo;
                  else if(dimCampo <=11)
                    arrayLarghezzaColonne[cnt] = 11;
                  else
                    arrayLarghezzaColonne[cnt] = 65;
                }
                arrayFormatoCampiXdpre[i]="STRING";
                if(dominio!=null && dominio.startsWith("SN")){
                  arrayFormatoCampiXdpre[i]="SN";
                }
              }else  if(definizione.startsWith("DT")){
                //Campo data
                arrayStiliCampi[cnt] = DizionarioStiliExcel.DATA_ALIGN_CENTER;
                arrayLarghezzaColonne[cnt] = 20;
                arrayFormatoCampiXdpre[i]="DT";
              }else if(definizione.startsWith("NU") && tabellato==null){
                //Campo numerico
                arrayLarghezzaColonne[cnt] = 11;
                if(definizione.indexOf(".")<0 ){
                  arrayStiliCampi[cnt] = DizionarioStiliExcel.INTERO_ALIGN_CENTER;
                  arrayFormatoCampiXdpre[i]="LONG";
                }else{
                  String tmp[] = definizione.split("\\.");
                  if("0".equals(tmp[1])){
                    arrayStiliCampi[cnt] = DizionarioStiliExcel.INTERO_ALIGN_CENTER;
                    arrayFormatoCampiXdpre[i]="LONG";
                  }
                  else{
                    arrayFormatoCampiXdpre[i]="DOUBLE";
                    int cifreDecimali = (new Long(tmp[1])).intValue();
                    if(cifreDecimali<=5)
                      arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT;
                    else
                      arrayStiliCampi[cnt] = DizionarioStiliExcel.DECIMALE9_ALIGN_RIGHT;
                  }
                }
              }else if(definizione.startsWith("NU") && tabellato!=null){
                //Campo tabellato
                arrayStiliCampi[cnt] = DizionarioStiliExcel.DATA_ALIGN_CENTER;
                arrayLarghezzaColonne[cnt] = 20;
                arrayFormatoCampiXdpre[i]="TABELLATO";
              }
            }
          }
        }
      }
      for (int i = 0; i < arrayCampi.length; i++) {
        arrayIndiceColonnaCampi[i] = i + 1;
      }
    }
  }

  /**
   *
   * @param fileExcel
   * @param importPrezziUnitari
   * @param archiviaXLSDocAss
   * @param session
   * @throws FileNotFoundException
   * @throws IOException
   */
  public LoggerImportOffertaPrezzi importOffertaPrezzi(
      UploadFileForm fileExcel, String ngara, String codiceDitta,
      boolean archiviaXLSDocAss, boolean isGaraLottiConOffertaUnica,
      String isPrequalifica, HttpSession session)
    throws GestioneFileDocumentiAssociatiException,
      FileNotFoundException, IOException, GestoreException, Exception {

    if (logger.isDebugEnabled())
      logger.debug("importOffertaPrezzi: inizio metodo");

    boolean isCodificaAutomaticaAttiva = this.geneManager.isCodificaAutomatica(
        "GARE", "NGARA");

    String moduloAttivo = "PG";
    if (session != null) {
      moduloAttivo = (String) session.getAttribute("moduloAttivo");
    }

    // Gestione delle configurazioni.
    // In funzione del tipo di fornitura si devono creare gli array opportuni
    // GestoreProfili gestoreProfili = geneManager.getProfili();
    Long tipoFornitura = null;
    // if (gestoreProfili.checkProtec(profiloAttivo, "COLS",
    // "VIS","GARE.TORN.TIPFORN")) {
    // }

    Vector<?> datiTORN = this.sqlDao.getVectorQuery(
        "select torn.tipforn from torn, gare where torn.codgar = gare.codgar1 and gare.ngara = ?",
        new Object[] { ngara });
    if (datiTORN != null && datiTORN.size() > 0) {
      if (SqlManager.getValueFromVectorParam(datiTORN, 0) != null) {
        tipoFornitura = SqlManager.getValueFromVectorParam(datiTORN, 0).longValue();
      }
    }

    if (tipoFornitura == null) tipoFornitura = new Long(3);
    this.setDefinizioni(tipoFornitura,null,false);

    LoggerImportOffertaPrezzi loggerImport = new LoggerImportOffertaPrezzi();
    this.workBook = new HSSFWorkbook(
        fileExcel.getSelezioneFile().getInputStream());

    HSSFSheet foglioLavorazioniForniture = null;
    int indiceFoglio = -1;
    // Flag per indicare se eseguire il controllo delle informazioni preliminari
    // del foglio Excel che si sta importando: se il foglio Excel e' esportato
    // dalla funzione di export dalla pagina 'Lista delle lavorazioni'
    boolean eseguireVerifichePreliminari = true;

    String descA1162 = this.tabellatiManager.getDescrTabellato("A1162", "1");
    if(descA1162!=null && descA1162.length()>0 && "1".equals(descA1162.substring(0, 1)))
      this.controlloUnivocitaCodvoc=true;
    else
      this.controlloUnivocitaCodvoc=false;

    if(new Long(98).equals(tipoFornitura)){
      if (workBook.getSheet(FOGLIO_LISTA_PRODOTTI[0]) != null) {
        foglioLavorazioniForniture = workBook.getSheet(FOGLIO_LISTA_PRODOTTI[0]);
        indiceFoglio = 0;
      } else if (workBook.getSheet(FOGLIO_LISTA_PRODOTTI[1]) != null) {
        foglioLavorazioniForniture = workBook.getSheet(FOGLIO_LISTA_PRODOTTI[1]);
        indiceFoglio = 1;
        eseguireVerifichePreliminari = false;
      }

    }else{
      if (workBook.getSheet(FOGLIO_LAVORAZIONE_E_FORNITURE[0]) != null) {
        foglioLavorazioniForniture = workBook.getSheet(FOGLIO_LAVORAZIONE_E_FORNITURE[0]);
        indiceFoglio = 0;
      } else if (workBook.getSheet(FOGLIO_LAVORAZIONE_E_FORNITURE[1]) != null) {
        foglioLavorazioniForniture = workBook.getSheet(FOGLIO_LAVORAZIONE_E_FORNITURE[1]);
        indiceFoglio = 1;
        eseguireVerifichePreliminari = false;
      }

    }

    if (foglioLavorazioniForniture != null) {

      HSSFRow rigaGestioneRibsub = foglioLavorazioniForniture.getRow(4);
      if (rigaGestioneRibsub != null && rigaGestioneRibsub.getPhysicalNumberOfCells() > 0) {
        // Se essite la riga ed è valorizzata allora è presente la nuova gestione ribsub
        String nuovaGestioneRibsub = UtilityExcel.leggiCellaString(
            foglioLavorazioniForniture, 7, 5);
        if (nuovaGestioneRibsub != null && "1".equals(nuovaGestioneRibsub))
          nuovaGestioneRibsubAttiva = true;
        }

      if (eseguireVerifichePreliminari) {
        if (!this.verifichePreliminari(ngara, foglioLavorazioniForniture,
            isCodificaAutomaticaAttiva)) {
          throw new GestoreException("Errore durante le verifiche preliminari "
              + "dell'operazione di import dati offerta prezzi",
              "importExportOffertaPrezzi.verifichePreliminari");
        }
      }

      if (logger.isDebugEnabled()) {
        if (!new Long(98).equals(tipoFornitura)) {
          logger.debug("importOffertaPrezzi: inizio lettura del foglio '"
              + FOGLIO_LAVORAZIONE_E_FORNITURE[0] + "'");
        } else {
          logger.debug("importOffertaPrezzi: inizio lettura del foglio '"
              + FOGLIO_LISTA_PRODOTTI[0] + "'");
        }
      }

      // Controllo del numero di campi obbligatori su tutto il file
      Map<String,List<?>> mappa = this.getListaCampiDaImportare(foglioLavorazioniForniture,
          FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio], null);
      if (mappa != null
          && mappa.containsKey("listaNomiFisiciCampiDaImportare")
          && !mappa.containsKey("listaErrori")) {
        List<?> listaNomiFisiciCampiDaImportare = mappa.get("listaNomiFisiciCampiDaImportare");
        int numeroCampiObbligatori = -1;
        int counter = 0;
        if (codiceDitta == null) {
          // Per le gare a lotto unico e per le gare a lotti con offerte
          // distinte vi e' un unico campo obbligatorio: CODVOC, mentre per le
          // gare a lotti con offerta unica i campi obbligatori sono NGARA,
          // CODVOC
          if (isGaraLottiConOffertaUnica)
            numeroCampiObbligatori = 2;
          else
            numeroCampiObbligatori = 1;

          for (int i = 0; i < listaNomiFisiciCampiDaImportare.size()
              && counter < numeroCampiObbligatori; i++) {
            if (isGaraLottiConOffertaUnica
                && (arrayCampi[0].equals(listaNomiFisiciCampiDaImportare.get(i))
                		|| arrayCampi[1].equals(listaNomiFisiciCampiDaImportare.get(i))))
              counter++;
            if (arrayCampi[3].equals(listaNomiFisiciCampiDaImportare.get(i)))
              counter++;

            if(new Long(98).equals(tipoFornitura) && !isGaraLottiConOffertaUnica){
              if (arrayCampi[1].equals(listaNomiFisiciCampiDaImportare.get(i)))
                counter++;
            }
          }
        } else {
          // Per le gare a lotto unico e per le gare a lotti con offerte
          // distinte i campi obbligatori sono CODVOC e PREZUN, mentre per le
          // gare a lotti con offerta unica i campi obbligatori sono NGARA o
          // CODIGA (a seconda se e' attiva la codifica automatica o meno),
          // CODVOC e PREZUN
          // Nel caso di TORN.TIPFORN=98 i campi obbligatori sono CODVOV,PREOFF e QUANTI,
          // per tutti i tipi di gara, e per le gare ad offerta unica in più ci sono
          // NGARA o CODIGA (a seconda se e' attiva la codifica automatica o meno),
          if (!(new Long(98).equals(tipoFornitura))) {
            if (isGaraLottiConOffertaUnica)
              numeroCampiObbligatori = 3;
            else
              numeroCampiObbligatori = 2;
          } else {
            if (isGaraLottiConOffertaUnica)
              numeroCampiObbligatori = 4;
            else
              numeroCampiObbligatori = 3;
          }

          for (int i = 0; i < listaNomiFisiciCampiDaImportare.size()
              && counter < numeroCampiObbligatori; i++) {

            if (isGaraLottiConOffertaUnica && isCodificaAutomaticaAttiva
                && arrayCampi[1].equals(listaNomiFisiciCampiDaImportare.get(i)))
              counter++;
            else if (isGaraLottiConOffertaUnica && (!isCodificaAutomaticaAttiva)
                && arrayCampi[0].equals(listaNomiFisiciCampiDaImportare.get(i)))
              counter++;
            else if (!(new Long(98).equals(tipoFornitura))
                && arrayCampi[3].equals(listaNomiFisiciCampiDaImportare.get(i)))
              counter++;
            else if ((new Long(1).equals(tipoFornitura))
                && arrayCampi[33].equals(listaNomiFisiciCampiDaImportare.get(i)))
              counter++;
            else if ((new Long(2).equals(tipoFornitura))
                && arrayCampi[26].equals(listaNomiFisiciCampiDaImportare.get(i)))
              counter++;
            else if ((new Long(3).equals(tipoFornitura))
                && arrayCampi[10].equals(listaNomiFisiciCampiDaImportare.get(i)))
              counter++;
            else if ((new Long(98).equals(tipoFornitura))
                && arrayCampi[3].equals(listaNomiFisiciCampiDaImportare.get(i)))
              counter++;
            else if ((new Long(98).equals(tipoFornitura))
                && arrayCampi[14].equals(listaNomiFisiciCampiDaImportare.get(i)))
              counter++;
            else if ((new Long(98).equals(tipoFornitura))
                && arrayCampi[15].equals(listaNomiFisiciCampiDaImportare.get(i)))
              counter++;
          }
        }

        if (counter == numeroCampiObbligatori) {

          // Dati della tabella GCAP
          List<CampoImportExcel> listaCampiImportExcel = this.getListaCampiDaImportareExcel(
              foglioLavorazioniForniture, indiceFoglio, "GCAP", tipoFornitura,
              codiceDitta, isPrequalifica);

          // Dati della tabella GCAP_SAN
          List<CampoImportExcel> listaCampiImportExcelGCAP_SAN = this.getListaCampiDaImportareExcel(
              foglioLavorazioniForniture, indiceFoglio, "GCAP_SAN",
              tipoFornitura, codiceDitta, isPrequalifica);

          // Dati della tabella DPRE
          List<CampoImportExcel> listaCampiImportExcelDPRE = this.getListaCampiDaImportareExcel(
              foglioLavorazioniForniture, indiceFoglio, "DPRE", tipoFornitura,
              codiceDitta, isPrequalifica);

          // Dati della tabella DPRE_SAN
          List<CampoImportExcel> listaCampiImportExcelDPRE_SAN = this.getListaCampiDaImportareExcel(
              foglioLavorazioniForniture, indiceFoglio, "DPRE_SAN",
              tipoFornitura, codiceDitta, isPrequalifica);

          if (codiceDitta != null && codiceDitta.length() > 0) {
            // Gestione inserimento delle offerte della ditte
            this.importOffertaPrezziDitta(foglioLavorazioniForniture, ngara,
                codiceDitta, listaCampiImportExcel, isGaraLottiConOffertaUnica,
                isPrequalifica,session, loggerImport, isCodificaAutomaticaAttiva,
                listaCampiImportExcelGCAP_SAN, listaCampiImportExcelDPRE,
                listaCampiImportExcelDPRE_SAN, tipoFornitura);
          } else {
            // Gestione inserimento dei dati relativi alla gara.
            // E' possibile per l'ente inserire nuove voci in GCAP
            // prima di procedere all'inserimento delle offerte
            // delle singole ditte
            this.importOffertaPrezziGara(foglioLavorazioniForniture, ngara,
                listaCampiImportExcel, isGaraLottiConOffertaUnica, session,
                loggerImport, isCodificaAutomaticaAttiva, tipoFornitura,
                listaCampiImportExcelGCAP_SAN);
          }

          if (archiviaXLSDocAss) {
            moduloAttivo = (String) session.getAttribute("moduloAttivo");
            DocumentoAssociato docAss = new DocumentoAssociato();
            docAss.setCodApp(moduloAttivo);
            if (isGaraLottiConOffertaUnica)
              docAss.setEntita("TORN");
            else
              docAss.setEntita("GARE");
            docAss.setCampoChiave1(ngara);
            docAss.setCampoChiave2("#");
            docAss.setCampoChiave3("#");
            docAss.setCampoChiave4("#");
            docAss.setCampoChiave5("#");
            docAss.setDataInserimento(new Date());

            if (codiceDitta != null) {
              docAss.setTitolo(TITOLO_FILE_C0OGASS_IMPORT_DITTA.concat(codiceDitta));
              docAss.setNomeDocAss(NOME_FILE_C0OGGASS_IMPORT.replaceAll(".xls",
                  "-".concat(codiceDitta).concat(".xls")));
            } else {
              docAss.setTitolo(TITOLO_FILE_C0OGASS_IMPORT);
              docAss.setNomeDocAss(NOME_FILE_C0OGGASS_IMPORT);
            }
            docAss.setPathDocAss("[default]");

            this.documentiAssociatiManager.associaFile(docAss, fileExcel.getSelezioneFile().getFileData());
          }
        } else {
          if (!(new Long(98).equals(tipoFornitura))) {
            if (isGaraLottiConOffertaUnica)
              logger.error("importOffertaPrezzi: Nel foglio '"
                  + FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio] + "' non sono "
                  + "presenti i nomi fisici dei campi che da analisi sono SEMPRE "
                  + "visibili (NGARA, CODVOC, PREZUN)");
            else
              logger.error("importOffertaPrezzi: Nel foglio '"
                  + FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio] + "' non sono "
                  + "presenti i nomi fisici dei campi che da analisi sono SEMPRE "
                  + "visibili (CODVOC, PREZUN)");
          } else {
            if (isGaraLottiConOffertaUnica)
              logger.error("importOffertaPrezzi: Nel foglio '"
                  + FOGLIO_LISTA_PRODOTTI[indiceFoglio] + "' non sono "
                  + "presenti i nomi fisici dei campi che da analisi sono SEMPRE "
                  + "visibili (NGARA, CODVOC, QUANTI, PREZOFF)");
            else
              logger.error("importOffertaPrezzi: Nel foglio '"
                  + FOGLIO_LISTA_PRODOTTI[indiceFoglio] + "' non sono "
                  + "presenti i nomi fisici dei campi che da analisi sono SEMPRE "
                  + "visibili (CODVOC, QUANTI, PREZOFF)");
          }

          if (codiceDitta == null) {
            if (isGaraLottiConOffertaUnica)
              loggerImport.addMsgVerificaFoglio("Il formato del file Excel "
                  + "non e' compatibile: nel foglio '"
                  + FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio]
                  + "' non sono presenti le colonne 'Codice lotto' o 'Codice'");
            else
              loggerImport.addMsgVerificaFoglio("Il formato del file Excel "
                  + "non e' compatibile: nel foglio '"
                  + FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio]
                  + "' non e' presente la colonna 'Codice'");
          } else {
            if (!(new Long(98).equals(tipoFornitura))) {
              if (isGaraLottiConOffertaUnica)
                loggerImport.addMsgVerificaFoglio("Il formato del file Excel "
                    + "non e' compatibile: nel foglio '"
                    + FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio]
                    + "' non sono presenti le colonne 'Codice lotto' o 'Codice' "
                    + "o 'Prezzo unitario'");
              else
                loggerImport.addMsgVerificaFoglio("Il formato del file Excel non "
                    + "e' compatibile: nel foglio '"
                    + FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio]
                    + "' non sono presenti la colonna 'Codice' o 'Prezzo unitario'");
            } else {
              if (isGaraLottiConOffertaUnica)
                loggerImport.addMsgVerificaFoglio("Il formato del file Excel "
                    + "non e' compatibile: nel foglio '"
                    + FOGLIO_LISTA_PRODOTTI[indiceFoglio]
                    + "' non sono presenti le colonne 'Codice lotto' o 'Codice IZS' "
                    + "o 'Numero confezioni' o 'Prezzo per confezione'");
              else
                loggerImport.addMsgVerificaFoglio("Il formato del file Excel non "
                    + "e' compatibile: nel foglio '"
                    + FOGLIO_LISTA_PRODOTTI[indiceFoglio]
                    + "' non sono presenti le colonne 'Codice IZS' o 'Numero confezioni' "
                    + "o 'Prezzo per confezione'");
            }
          }
        }
      } else if (mappa.get("listaErrori") != null) {
        List<?> listaErrori = mappa.get("listaErrori");
        for (int l = 0; l < listaErrori.size(); l++)
          logger.error("importOffertaPrezzi: " + (String) listaErrori.get(l));

        loggerImport.addMsgVerificaFoglio("Il formato del file Excel non e' "
            + "compatibile. Si consiglia di eseguire nuovamente l'esportazione "
            + "per produrre un file in formato valido");
      } else {
        String msgFoglio = FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio];
        if ((new Long(98).equals(tipoFornitura))) {
          msgFoglio = FOGLIO_LISTA_PRODOTTI[indiceFoglio];
        }
        logger.error("Il metodo getListaCampiDaImportare ha restituito null. "
            + "Si consiglia di andare in debug importando lo stesso file Excel "
            + "che ha generato questo errore, per capire la causa.");
        loggerImport.addMsgVerificaFoglio("Si e' verificato un errore "
            + "inaspettato nella lettura dell'intestazione del foglio '"
            + msgFoglio + "'");
      }
    } else {
      /*
      String msgFoglio = FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio];
      if ((new Long(98).equals(tipoFornitura))) {
        msgFoglio = FOGLIO_LISTA_PRODOTTI[indiceFoglio];
      }
       */

      String tmp = "Il formato del file Excel selezionato non e' compatibile col formato atteso";
      logger.error(tmp);
      loggerImport.addMsgVerificaFoglio(tmp);
    }

    if (logger.isDebugEnabled() && indiceFoglio>=0) {
      String msgFoglio = FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio];
      if ((new Long(98).equals(tipoFornitura))) {
        msgFoglio = FOGLIO_LISTA_PRODOTTI[indiceFoglio];
      }
      logger.debug("importOffertaPrezzi: fine lettura del foglio '"
          + msgFoglio + "'");
    }

    if (logger.isDebugEnabled()) {
      logger.debug("importOffertaPrezzi: fine metodo");
    }
    return loggerImport;
  }

  /**
   * Gestione delle lista dei campi da importare da Excel
   *
   * @param foglioLavorazioniForniture
   * @param indiceFoglio
   * @param entita
   * @param tipoFornitura
   * @param codiceDitta
   * @param isPrequalifica
   * @return
   */
  private List<CampoImportExcel> getListaCampiDaImportareExcel(
      HSSFSheet foglioLavorazioniForniture, int indiceFoglio, String entita,
      Long tipoFornitura, String codiceDitta,String isPrequalifica) {

    Map<String,List<?>> mappa = null;
    if (new Long(98).equals(tipoFornitura)) {
      mappa = getListaCampiDaImportare(foglioLavorazioniForniture,
          FOGLIO_LISTA_PRODOTTI[indiceFoglio], entita);
    } else {
      mappa = getListaCampiDaImportare(foglioLavorazioniForniture,
          FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio], entita);
    }

    List<CampoImportExcel> listaCampiImportExcel = new ArrayList<CampoImportExcel>();

    if (mappa != null
        && mappa.containsKey("listaNomiFisiciCampiDaImportare")
        && !mappa.containsKey("listaErrori")) {

      List<?> listaNomiFisiciCampiDaImportare = mappa.get("listaNomiFisiciCampiDaImportare");
      List<?> listaIndiceColonnaCampiDaImportare = mappa.get("listaIndiceColonnaCampiDaImportare");
      List<?> listaIndiceArrayValoreCampiDaImportare = mappa.get("listaIndiceArrayValoreCampiDaImportare");

      CampoImportExcel tmpCampo = null;
      for (int i = 0; i < listaNomiFisiciCampiDaImportare.size(); i++) {
        String nomeFisicoCampo = (String) listaNomiFisiciCampiDaImportare.get(i);
        tmpCampo = new CampoImportExcel(nomeFisicoCampo,tipoFornitura);
        tmpCampo.setColonnaCampo(((Long) listaIndiceColonnaCampiDaImportare.get(i)).intValue());
        tmpCampo.setColonnaArrayValori(((Long) listaIndiceArrayValoreCampiDaImportare.get(i)).intValue());

        switch (tipoFornitura.intValue()) {
        case 1:
          if (nomeFisicoCampo.equalsIgnoreCase(arrayCampi[0])
              || nomeFisicoCampo.equalsIgnoreCase(arrayCampi[1])
              || nomeFisicoCampo.equalsIgnoreCase(arrayCampi[6])
              || nomeFisicoCampo.equalsIgnoreCase(arrayCampi[7])
              || ("false".equals(isPrequalifica) && codiceDitta != null && codiceDitta.length() > 0 && nomeFisicoCampo.equalsIgnoreCase(arrayCampi[33]))) {
            tmpCampo.setObbligatorio(true);
          } else {
            tmpCampo.setObbligatorio(false);
          }
          break;

        case 2:
          if (nomeFisicoCampo.equalsIgnoreCase(arrayCampi[0])
              || nomeFisicoCampo.equalsIgnoreCase(arrayCampi[1])
              || nomeFisicoCampo.equalsIgnoreCase(arrayCampi[6])
              || nomeFisicoCampo.equalsIgnoreCase(arrayCampi[7])
              || ("false".equals(isPrequalifica) && (codiceDitta != null && codiceDitta.length() > 0 && nomeFisicoCampo.equalsIgnoreCase(arrayCampi[26])))) {
            tmpCampo.setObbligatorio(true);
          } else {
            tmpCampo.setObbligatorio(false);
          }
          break;

        case 98:
          if (nomeFisicoCampo.equalsIgnoreCase(arrayCampi[0])
              || nomeFisicoCampo.equalsIgnoreCase(arrayCampi[1])
              || nomeFisicoCampo.equalsIgnoreCase(arrayCampi[3])
              || nomeFisicoCampo.equalsIgnoreCase(arrayCampi[14])
              || nomeFisicoCampo.equalsIgnoreCase(arrayCampi[15])) {
            tmpCampo.setObbligatorio(true);
          } else {
            tmpCampo.setObbligatorio(false);
          }
          break;

        default:
          if (nomeFisicoCampo.equalsIgnoreCase(arrayCampi[0])
              || nomeFisicoCampo.equalsIgnoreCase(arrayCampi[1])
              || nomeFisicoCampo.equalsIgnoreCase(arrayCampi[3])
              || nomeFisicoCampo.equalsIgnoreCase(arrayCampi[7])
              || nomeFisicoCampo.equalsIgnoreCase(arrayCampi[8])
              || nomeFisicoCampo.equalsIgnoreCase(arrayCampi[9])
              || ("false".equals(isPrequalifica) && (codiceDitta != null && codiceDitta.length() > 0 && nomeFisicoCampo.equalsIgnoreCase(arrayCampi[13])))) {
            tmpCampo.setObbligatorio(true);
          } else {
            tmpCampo.setObbligatorio(false);
          }
          break;
        }
        listaCampiImportExcel.add(tmpCampo);
      }
    } else {
      listaCampiImportExcel = null;
    }

    return listaCampiImportExcel;

  }

  private void importOffertaPrezziGara(HSSFSheet foglio, String ngara,
      List<CampoImportExcel> listaCampiImportExcel, boolean isGaraLottiConOffertaUnica,
      HttpSession session, LoggerImportOffertaPrezzi loggerImport,
      boolean isCodificaAutomaticaAttiva, Long tipoFornitura,
      List<CampoImportExcel> listaCampiImportExcelGCAP_SAN)
                throws SQLException, SqlComposerException, GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("importOffertaPrezziGara: inizio metodo");

    this.cancellaDati(ngara, isGaraLottiConOffertaUnica, isCodificaAutomaticaAttiva);

    int indiceRiga = -1;
    if (new Long(98).equals(tipoFornitura)){
      indiceRiga = FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE - 1;
    }else{
      indiceRiga = FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE - 1;
    }

    int ultimaRigaValorizzata = foglio.getPhysicalNumberOfRows();
    int colonnaCodiceLavorazioneFornitura = -1;
    int colonnaCodiceLotto = -1;
    int colonnaDescrizioneLotto = -1;
    int colonnaCODIGA = -1;
    int colonnaCODCIG = -1;
    int colonnaNOT_GAR = -1;
    // Per gare a lotti con offerta unica e con codifica automatica attiva e'
    // possibile inserire le lavorazioni/forniture con la creazione automatica
    // dei lotti a cui le lavorazioni/forniture sono associate. Questo metodo
    // inserisce nella GCAP tutte le occorrenze e memorizza nella HashMap
    // mappaCodiciLotti i codici dei lotti da creare (cioe' il valore del campo
    // GARE.NGARA)
    HashMap<String,String> mappaCodiciLotti = new HashMap<String,String>();
    HashMap<String,String> mappaCodiciCIG = new HashMap<String,String>();
    HashMap<String,String> mappaDescrizioniLotti = new HashMap<String,String>();
    if(new Long(98).equals(tipoFornitura)){
      for (int colonna = 0; colonna < listaCampiImportExcel.size(); colonna++) {
        CampoImportExcel tmpCampo = listaCampiImportExcel.get(colonna);
        if (isGaraLottiConOffertaUnica
            && (!isCodificaAutomaticaAttiva)
            && arrayCampi[0].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                tmpCampo.getNomeFisicoCampo())))
          colonnaCodiceLotto = tmpCampo.getColonnaArrayValori();
        else if (arrayCampi[1].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
            tmpCampo.getNomeFisicoCampo())))
          colonnaCodiceLavorazioneFornitura = tmpCampo.getColonnaArrayValori();
        else if (arrayCampi[2].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
            tmpCampo.getNomeFisicoCampo())))
          colonnaCODIGA = tmpCampo.getColonnaArrayValori();
        else if (arrayCampi[3].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
            tmpCampo.getNomeFisicoCampo())))
          colonnaDescrizioneLotto = tmpCampo.getColonnaArrayValori();
      }
    } else {
      for (int colonna = 0; colonna < listaCampiImportExcel.size(); colonna++) {
        CampoImportExcel tmpCampo = listaCampiImportExcel.get(colonna);
        if (isGaraLottiConOffertaUnica
            && (!isCodificaAutomaticaAttiva)
            && arrayCampi[0].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                tmpCampo.getNomeFisicoCampo())))
          colonnaCodiceLotto = tmpCampo.getColonnaArrayValori();
        else if (arrayCampi[1].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
            tmpCampo.getNomeFisicoCampo())))
          colonnaCODIGA = tmpCampo.getColonnaArrayValori();
        else if (arrayCampi[2].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
            tmpCampo.getNomeFisicoCampo())))
          colonnaCODCIG = tmpCampo.getColonnaArrayValori();
        else if (arrayCampi[3].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
            tmpCampo.getNomeFisicoCampo())))
          colonnaCodiceLavorazioneFornitura = tmpCampo.getColonnaArrayValori();
      }
    }

    // Contatore del numero di righe consecutive vuote, per terminare la lettura
    // del foglio Excel prima di giungere all'ultima riga inizializzata dopo che
    // sono state trovate un numero di righe vuote consecutive pari alla
    // costante IMPORT_NUMERO_RIGHE_CONSECUTIVE_VUOTE
    int contatoreRigheVuote = 0;

    for (; indiceRiga < ultimaRigaValorizzata
        && contatoreRigheVuote <= IMPORT_NUMERO_RIGHE_CONSECUTIVE_VUOTE; indiceRiga++) {
      HSSFRow rigaFoglioExcel = foglio.getRow(indiceRiga);
      if (rigaFoglioExcel != null) {
        // Lista dei valori di GCAP
        List<Object> valoriCampiRigaExcel = this.letturaRiga(rigaFoglioExcel,
            listaCampiImportExcel);

        List<Object> valoriCampiRigaExcelGCAP_SAN = null;
        if (listaCampiImportExcelGCAP_SAN != null) {
          valoriCampiRigaExcelGCAP_SAN = this.letturaRiga(rigaFoglioExcel,
              listaCampiImportExcelGCAP_SAN);
        }

        int numeroCelleNull = 0;
        for (int h = 0; h < valoriCampiRigaExcel.size(); h++)
          if (valoriCampiRigaExcel.get(h) == null) numeroCelleNull++;

        if (numeroCelleNull < valoriCampiRigaExcel.size()) {
          // Reset del contatore del numero di righe vuote se diverso da zero
          if (contatoreRigheVuote > 0) contatoreRigheVuote = 0;

          loggerImport.incrementaRigheLette();

          boolean rigaImportabile = true;
          List<String> tmpListaMsg = new ArrayList<String>();

          // Controllo valori di GCAP
          this.valoreCodvoc=null;
          this.cellaCodvoc=null;
          rigaImportabile = this.controlloValoriRiga(listaCampiImportExcel,
              indiceRiga, valoriCampiRigaExcel, tmpListaMsg, tipoFornitura,false);

          // Controllo valori di GCAP_SAN
          if (rigaImportabile && valoriCampiRigaExcelGCAP_SAN != null) {
            rigaImportabile = this.controlloValoriRiga(
                listaCampiImportExcelGCAP_SAN, indiceRiga,
                valoriCampiRigaExcelGCAP_SAN, tmpListaMsg, tipoFornitura,false);
          }

          if (rigaImportabile) {
            if (logger.isDebugEnabled())
              logger.debug("I dati presenti nella riga "
                  + (indiceRiga + 1)
                  + " sono nel formato previsto");
          }


          String codiceLavorazFornitura = this.getCodvocString( valoriCampiRigaExcel.get(colonnaCodiceLavorazioneFornitura));
          String codiceLotto = "";
          String descrizioneLotto = "";
          String codiga = null;
          String codiceCIG = null;
          if (isGaraLottiConOffertaUnica) {
            if (isCodificaAutomaticaAttiva) {
              codiga = (String) valoriCampiRigaExcel.get(colonnaCODIGA);
              codiceCIG = (String) valoriCampiRigaExcel.get(colonnaCODCIG);
              HashMap<String,String> mappa = this.pgManager.calcolaCodificaAutomatica("GARE",
                  Boolean.FALSE, ngara, new Long(codiga));
              codiceLotto = mappa.get("numeroGara");
            } else {
              codiceLotto = (String) valoriCampiRigaExcel.get(colonnaCodiceLotto);
            }
            if (new Long(98).equals(tipoFornitura))
              descrizioneLotto = (String) valoriCampiRigaExcel.get(colonnaDescrizioneLotto);
          }

          if (rigaImportabile) {
            String chiaveGara=ngara;
            if (isGaraLottiConOffertaUnica)
              chiaveGara=codiceLotto;
            rigaImportabile=this.controlloCodvoc(tmpListaMsg, chiaveGara,isGaraLottiConOffertaUnica);
          }

          if (tmpListaMsg.size() == 0) {
            if (codiceLotto != null && codiceLotto.length() > 0) {
              if (isGaraLottiConOffertaUnica && isCodificaAutomaticaAttiva) {
                if (!mappaCodiciLotti.containsKey(codiga))
                  mappaCodiciLotti.put(codiga, codiceLotto);
                  mappaCodiciCIG.put(codiga, codiceCIG);
                  mappaDescrizioniLotti.put(codiga, descrizioneLotto);
              } else {
                // Verifica che il codice lotto esista e sia relativo ad un
                // lotto con criterio di aggiudicazione di tipo offerta prezzi
                // unitaria o OEPV (MODLICG = 5, 6, 14, 16)
                boolean esisteLotto = (0 != this.geneManager.countOccorrenze(
                    "GARE", "NGARA = ? and CODGAR1 = ? and (GENERE is null "
                    + "or GENERE <> 3) and MODLICG in (5,6,14,16)",
                    new Object[] { codiceLotto, ngara }));

                // Il codice lotto non esiste o non e' di tipo offerta prezzi
                // (MODLICG = 5,6,14,16)
                if (!esisteLotto) {
                  loggerImport.addMessaggioErrore("La riga " + (indiceRiga + 1)
                      + " non importata:");
                  loggerImport.addMessaggioErrore("- la cella "
                      + UtilityExcel.conversioneNumeroColonna(colonnaCodiceLotto + 1)
                      + (indiceRiga + 1)
                      + " presenta un codice lotto inesistente o "
                      + "con criterio di aggiudicazione diverso da offerta "
                      + "prezzi unitari");
                  loggerImport.incrementaRecordNonImportati();
                  continue;
                }
              }
            }
          } else if (tmpListaMsg.size() > 0) {
            if (!isCodificaAutomaticaAttiva && (codiceLotto != null && codiceLotto.length() > 0)) {
              // Verifica che il codice lotto esista e sia relativo ad un lotto
              // con criterio di aggiudicazione di tipo offerta prezzi unitaria
              // (MODLICG = 5, 6, 14, 16)
              boolean esisteLotto = (0 != this.geneManager.countOccorrenze("GARE",
                  "NGARA = ? and CODGAR1 = ? and (GENERE is null or GENERE <> 3) "
                  + "and MODLICG in (5,6,14,16)", new Object[] {codiceLotto,
                      ngara }));

              if (esisteLotto) {
                if (codiceLavorazFornitura != null
                    && codiceLavorazFornitura.length() > 0) {
                  if (rigaImportabile)
                    loggerImport.addMessaggioErrore((isGaraLottiConOffertaUnica
                        ? "Lotto " + codiceLotto + " - " : "")
                        + "Lavorazione o fornitura " + codiceLavorazFornitura
                        + " (riga " + (indiceRiga + 1)
                        + ") importata parzialmente:");
                  else
                    loggerImport.addMessaggioErrore((isGaraLottiConOffertaUnica
                        ? "Lotto " + codiceLotto + " - " : "")
                        + "Lavorazione o fornitura " + codiceLavorazFornitura
                        + " (riga " + (indiceRiga + 1) + ") non importata:");
                } else {
                  if (rigaImportabile)
                    loggerImport.addMessaggioErrore("La riga "
                            + (indiceRiga + 1) + " importata parzialmente:");
                  else
                    loggerImport.addMessaggioErrore("La riga "
                        + (indiceRiga + 1) + " non importata:");
                }
              } else {
                // Il codice lotto non esiste o non e' di tipo offerta prezzi
                // (MODLICG = 5,6,14,16)
                loggerImport.addMessaggioErrore("La riga " + (indiceRiga + 1)
                    + " non importata:");
                loggerImport.addMessaggioErrore("- la cella "
                    + UtilityExcel.conversioneNumeroColonna(colonnaCodiceLotto + 1)
                    + (indiceRiga + 1) + " presenta un codice lotto inesistente"
                    + " o con criterio di aggiudicazione diverso da offerta "
                    + "prezzi unitari");
                loggerImport.incrementaRecordNonImportati();
                continue;
              }
            } else {
              if (isGaraLottiConOffertaUnica) {
                if (rigaImportabile)
                  loggerImport.addMessaggioErrore("La riga " + (indiceRiga + 1)
                      + " importata parzialmente:");
                else
                  loggerImport.addMessaggioErrore("La riga " + (indiceRiga + 1)
                      + " non importata:");
              } else {
                if (codiceLavorazFornitura != null
                    && codiceLavorazFornitura.length() > 0) {
                  if (rigaImportabile)
                    loggerImport.addMessaggioErrore((isGaraLottiConOffertaUnica
                        ? "Lotto " + codiceLotto + " - " : "")
                        + "Lavorazione o fornitura "
                        + codiceLavorazFornitura + " (riga " + (indiceRiga + 1)
                        + ") importata parzialmente:");
                  else
                    loggerImport.addMessaggioErrore((isGaraLottiConOffertaUnica
                        ? "Lotto " + codiceLotto + " - " : "")
                        + "Lavorazione o fornitura "
                        + codiceLavorazFornitura + " (riga " + (indiceRiga + 1)
                        + ") non importata:");
                } else {
                  if (rigaImportabile)
                    loggerImport.addMessaggioErrore("La riga " + (indiceRiga + 1)
                        + " importata parzialmente:");
                  else
                    loggerImport.addMessaggioErrore("La riga " + (indiceRiga + 1)
                        + " non importata:");
                }
              }
            }

            if (tmpListaMsg.size() > 0)
              loggerImport.addListaMessaggiErrore(tmpListaMsg);
          }

          if (rigaImportabile) {
            CampoImportExcel campoImportUNIMIS = null;
            boolean campoTrovato = false;

            for (int l = 0; l < listaCampiImportExcel.size() && !campoTrovato; l++) {
              campoImportUNIMIS = listaCampiImportExcel.get(l);
              if(new Long(98).equals(tipoFornitura)){
                if (arrayCampi[10].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                    campoImportUNIMIS.getNomeFisicoCampo().toUpperCase())))
                  campoTrovato = true;
              }else{
                if (arrayCampi[10].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                    campoImportUNIMIS.getNomeFisicoCampo().toUpperCase())))
                  campoTrovato = true;
              }
            }

            if (campoTrovato)
              gestioneUnitaDiMisura(campoImportUNIMIS, valoriCampiRigaExcel,
                  loggerImport);

            String strSqlInsert = new String(
                "insert into GCAP (CAMPI) values (VALORI)");
            String strSqlInsertGCAP_EST = new String(
                "insert into GCAP_EST (NGARA, CONTAF, DESEST) values (?, ?, ?) ");
            Object[] paramInsertGCAP_EST = new Object[3];
            StringBuffer strCampi = null;
            StringBuffer strValori = null;

            if (isGaraLottiConOffertaUnica) {
              if (isCodificaAutomaticaAttiva) {
                strCampi = new StringBuffer("NGARA, CONTAF, NORVOC, ");
                strValori = new StringBuffer("?, ?, ?, ");
              } else {
                strCampi = new StringBuffer("CONTAF, NORVOC, ");
                strValori = new StringBuffer("?, ?, ");
              }

              // I campi SOLSIC e SOGRIB non sono presenti fra i campi da
                // importare per le gare con tipo di fornitura != 3
              if(! new Long(3).equals(tipoFornitura)){
                strCampi.append("SOLSIC, SOGRIB, ");
                strValori.append("?, ?, ");
              }
            } else {
              strCampi = new StringBuffer("CONTAF, NORVOC, NGARA, ");
              strValori = new StringBuffer("?, ?, ?, ");
            }

            CampoImportExcel campoSOLSIC = null;
            CampoImportExcel campoSOGRIB = null;
            CampoImportExcel campoPESO = null;
            for (int y = 0; y < listaCampiImportExcel.size(); y++) {
              CampoImportExcel tmpCampo = listaCampiImportExcel.get(y);
              if(!new Long(98).equals(tipoFornitura)){
                if (arrayCampi[8].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                    tmpCampo.getNomeFisicoCampo())))
                  campoSOLSIC = listaCampiImportExcel.get(y);
                if (arrayCampi[9].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                    tmpCampo.getNomeFisicoCampo())))
                  campoSOGRIB = listaCampiImportExcel.get(y);
                if (arrayCampi[14].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                    tmpCampo.getNomeFisicoCampo())))
                  campoPESO = listaCampiImportExcel.get(y);
              }
            }

            if(nuovaGestioneRibsubAttiva && campoSOGRIB!=null){
              String valoreExcelSogrib = valoriCampiRigaExcel.get(campoSOGRIB.getColonnaArrayValori()).toString();
              String nuovoValoreSogrib="2";
              if(valoreExcelSogrib!=null && "2".equals(valoreExcelSogrib))
                nuovoValoreSogrib="1";
              valoriCampiRigaExcel.set(
                  campoSOGRIB.getColonnaArrayValori(), nuovoValoreSogrib);
            }

            String soloSicurezza = null;
            String soggettoRibasso = null;
            if (campoSOGRIB != null){
              soggettoRibasso = (String) valoriCampiRigaExcel.get(campoSOGRIB.getColonnaArrayValori());
            }
            if (campoSOLSIC != null) {
              soloSicurezza = (String) valoriCampiRigaExcel.get(campoSOLSIC.getColonnaArrayValori());
              if ("1".equals(soloSicurezza)) {
                if (campoSOGRIB != null) {
                  if (!"1".equals(soggettoRibasso)){
                     valoriCampiRigaExcel.set(
                         campoSOGRIB.getColonnaArrayValori(), "1");
                     soggettoRibasso = "1";
                  }

                } else {
                  strCampi.append("SOLSIC, ");
                  strValori.append("?, ");
                }
              }
            }

            Double peso = null;
            if(campoPESO != null){
              peso = (Double) valoriCampiRigaExcel.get(campoPESO.getColonnaArrayValori());
            }

            // Se il campo GCAP.CLASI1 non e' fra i campi da importare, bisogna
            // assegnargli il valore di default ('a misura').
            campoTrovato = false;
            if (!isGaraLottiConOffertaUnica) {
              for (int y = 0; y < listaCampiImportExcel.size() && !campoTrovato; y++) {
                CampoImportExcel tmpCampo = listaCampiImportExcel.get(y);
                if (arrayCampi[7].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                    tmpCampo.getNomeFisicoCampo()))) {
                  campoTrovato = true;
                }
              }
            }

            if (!campoTrovato) {
              strCampi.append("CLASI1, ");
              strValori.append("?, ");
              if(new Long(1).equals(tipoFornitura) | new Long(2).equals(tipoFornitura)){
                strCampi.append("VOCE, ");
                strValori.append("?, ");
              }
            }

            for (int l = 0; l < listaCampiImportExcel.size(); l++) {
              CampoImportExcel tmpCampo = listaCampiImportExcel.get(l);
              String tmp = tmpCampo.getNomeFisicoCampo();

              boolean condizioneImportPeso=true;
              //Nel caso di fornitura=3 non si deve importare PESO se SOGRIB=1
              if(new Long(3).equals(tipoFornitura) && tmp.indexOf("PESO")>=0  && "1".equals(soggettoRibasso) && peso!=null && peso.doubleValue()!=0){
                condizioneImportPeso=false;
              }

              if (tmp.indexOf("CODIGA") < 0 && tmp.indexOf("CODCIG")<0 && tmp.indexOf("NOT_GAR")<0 ) { // Il campo GARE.CODIGA non deve
                // essere fra i campi
                if (tmp.indexOf("GCAP_EST") < 0 && condizioneImportPeso) {

                  strCampi.append(tmp.substring(tmp.lastIndexOf(".") + 1).concat(
                      ", "));
                  strValori.append("?, ");
                }else if(!condizioneImportPeso){
                  //Messaggio che non si è importato il campo peso
                  valoriCampiRigaExcel.remove(tmpCampo.getColonnaArrayValori() - 1);
                  loggerImport.addMessaggioErrore("Riga " + (indiceRiga + 1) + ": non è stato importato il peso in quanto la lavorazione non è soggetta a ribasso");
                } else {
                  paramInsertGCAP_EST[2] = valoriCampiRigaExcel.remove(tmpCampo.getColonnaArrayValori());
                }
              } else {
                // Se il campo CODIGA e' fra i campi da importare, allora si
                // e' in presenza di una gara a lotti con offerta unica e con
                // codifica automatica attiva: il campo CODIGA deve essere
                // rimosso dagli oggetti valoriCampiRigaExcel
                // per non incorrere in errori in fase di salvataggio e/o
                if (tmp.indexOf("CODIGA") >= 0)
                colonnaCODIGA = tmpCampo.getColonnaArrayValori();
                if (tmp.indexOf("CODCIG") >= 0)
                  colonnaCODCIG = tmpCampo.getColonnaArrayValori();
                if (tmp.indexOf("NOT_GAR") >= 0)
                  colonnaNOT_GAR = tmpCampo.getColonnaArrayValori();
              }


            }

            // Rimozione del valore del campo GARE.CODIGA dall'oggetto che
            // contiene i valori letti dal foglio Excel dei campi da importare
            if (colonnaCODIGA >= 0) valoriCampiRigaExcel.remove(colonnaCODIGA);
            if (colonnaCODCIG >= 0) valoriCampiRigaExcel.remove(colonnaCODCIG-1);
            if (colonnaNOT_GAR >= 0) valoriCampiRigaExcel.remove(colonnaNOT_GAR-2);

            if (!campoTrovato) {
              if(new Long(1).equals(tipoFornitura)){
                CampoImportExcel campoPRINCATT = null;
                for (int y = 0; y < listaCampiImportExcelGCAP_SAN.size(); y++) {
                  CampoImportExcel tmpCampo = listaCampiImportExcelGCAP_SAN.get(y);
                  if (arrayCampi[14].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                      tmpCampo.getNomeFisicoCampo())))
                    campoPRINCATT = listaCampiImportExcelGCAP_SAN.get(y);
                }

                String principio_attivo = null;
                if (campoPRINCATT != null) {
                  principio_attivo = (String) valoriCampiRigaExcelGCAP_SAN.get(campoPRINCATT.getColonnaArrayValori());
                }
                valoriCampiRigaExcel.add(0, principio_attivo );
              }else{
                if(new Long(2).equals(tipoFornitura)){
                  CampoImportExcel campoDPRODCAP = null;
                  for (int y = 0; y < listaCampiImportExcelGCAP_SAN.size(); y++) {
                    CampoImportExcel tmpCampo = listaCampiImportExcelGCAP_SAN.get(y);
                    if (arrayCampi[15].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                        tmpCampo.getNomeFisicoCampo())))
                      campoDPRODCAP = listaCampiImportExcelGCAP_SAN.get(y);
                  }

                  String desc_prod_capitolato = null;
                  if (campoDPRODCAP != null) {
                    desc_prod_capitolato = (String) valoriCampiRigaExcelGCAP_SAN.get(campoDPRODCAP.getColonnaArrayValori());
                  }
                  valoriCampiRigaExcel.add(0, desc_prod_capitolato);
                }
              }
              // Aggiunta del valore di default del campo GCAP.CLASI1 all'array
              // contenente i valori della i-esima riga del foglio Excel
              valoriCampiRigaExcel.add(0, new Long(3));

            }

            strSqlInsert = strSqlInsert.replaceFirst("CAMPI",
                strCampi.substring(0, strCampi.length() - 2));
            strSqlInsert = strSqlInsert.replaceFirst("VALORI",
                strValori.substring(0, strValori.length() - 2));

            try {
              String tmpNgara = null;
              List<Object> listaValoriRiga = new ArrayList<Object>();
              if (isGaraLottiConOffertaUnica) {
                if (isCodificaAutomaticaAttiva) {
                  tmpNgara = codiceLotto;
                  listaValoriRiga.add(tmpNgara);
                } else {
                  if(!new Long(1).equals(tipoFornitura) && !new Long(2).equals(tipoFornitura))
                    tmpNgara = (String) valoriCampiRigaExcel.get(1);
                  else
                    tmpNgara = (String) valoriCampiRigaExcel.get(2);
                }

                paramInsertGCAP_EST[0] = tmpNgara;

                // Determino il max contaf per lotto di gara
                Long tmpMaxContaf = null;
                Vector<?> tmpVec = this.sqlDao.getVectorQuery(
                    "select max(CONTAF) from GCAP where NGARA = ? ",
                    new Object[] { tmpNgara });
                if (tmpVec != null && tmpVec.size() > 0)
                  tmpMaxContaf = (Long) ((JdbcParametro) tmpVec.get(0)).getValue();
                if (tmpMaxContaf == null) tmpMaxContaf = new Long(0);

                listaValoriRiga.add(new Long(tmpMaxContaf.longValue() + 1));
                listaValoriRiga.add(new Long(tmpMaxContaf.longValue() + 1));
                paramInsertGCAP_EST[1] = new Long(tmpMaxContaf.longValue() + 1);

                if(! new Long(3).equals(tipoFornitura)){
                    listaValoriRiga.add("2");
                    listaValoriRiga.add("2");
                }
              } else {
                listaValoriRiga.add(new Long(
                    loggerImport.getNumeroRecordImportati() + 1));
                listaValoriRiga.add(new Double(
                    loggerImport.getNumeroRecordImportati() + 1));
                listaValoriRiga.add(ngara);
                paramInsertGCAP_EST[0] = ngara;
                paramInsertGCAP_EST[1] = new Long(
                    loggerImport.getNumeroRecordImportati() + 1);
              }

              if(new Long(3).equals(tipoFornitura)){
                if (strValori.toString().split(", ").length - 3 > valoriCampiRigaExcel.size())
                  listaValoriRiga.add("1");
              }

              listaValoriRiga.addAll(valoriCampiRigaExcel);

              sqlDao.update(strSqlInsert, listaValoriRiga.toArray());
              // Salvataggio del campo DESEST nell'entita' GCAP_EST (estensione
              // della GCAP)
              sqlDao.update(strSqlInsertGCAP_EST, paramInsertGCAP_EST);

              // Inserimento della tabella di estensione GCAP_SAN
              this.insertGCAP_SAN(listaCampiImportExcelGCAP_SAN,
                  valoriCampiRigaExcelGCAP_SAN, rigaFoglioExcel,
                  (String) paramInsertGCAP_EST[0],
                  (Long) paramInsertGCAP_EST[1]);

              loggerImport.incrementaRecordImportati();

              if (logger.isDebugEnabled())
                logger.debug("Inserimento dei valori della riga "
                    + (indiceRiga + 1) + " avvenuta con successo");

              continue;
            } catch (SQLException s) {
              logger.error("Errore nell'inserimento nella tabella GCAP ", s);
              throw s;
            }
          } else {
            loggerImport.incrementaRecordNonImportati();
          }
        } else {
          if (logger.isDebugEnabled())
            logger.debug("La riga " + (indiceRiga + 1) + " e' stata saltata "
                + "perche' presenta tutte e " + valoriCampiRigaExcel.size()
                + " le celle non valorizzate");

          contatoreRigheVuote++;
        }
      } else {
        if (logger.isDebugEnabled())
          logger.debug("La riga " + (indiceRiga + 1) + " e' stata saltata "
              + "perche' non inizializzata");

        contatoreRigheVuote++;
      }
    }
    //Impostazione a '2' di SOLSIC nullo
    String updateGap="update gcap set solsic='2' where ngara=? and solsic is null";
    if(isGaraLottiConOffertaUnica){
      updateGap = "update gcap set solsic='2' where ngara in (select ngara from gare where codgar1=?) and solsic is null";
    }
    sqlDao.update(updateGap, new Object[]{ngara});

    //Impostazione a '2' di SOGRIB nullo
    updateGap="update gcap set sogrib='2' where ngara=? and sogrib is null";
    if(isGaraLottiConOffertaUnica){
      updateGap = "update gcap set sogrib='2' where ngara in (select ngara from gare where codgar1=?) and sogrib is null";
    }
    sqlDao.update(updateGap, new Object[]{ngara});

    if (!mappaCodiciLotti.isEmpty()) {
      this.creazioneLotti(mappaCodiciLotti, ngara, loggerImport, tipoFornitura,mappaDescrizioniLotti,null,mappaCodiciCIG);
      this.aggiornamentoDitteLotti(mappaCodiciLotti, ngara);
    }

    if (logger.isDebugEnabled())
      logger.debug("importOffertaPrezziGara: fine metodo");
  }

  /**
   * Creazione dei nuovi lotti equivalenti alla occorrenza complementare per
   * gare a lotti con offerta unica e con codifica automatica attiva. Per ogni
   * lotto vengono determinati anche l'importo a sicurezza, l'importo non
   * soggetto a ribasso, l'importo soggetto a ribasso e l'importo dell'appalto.
   * Tutti questi importo possono essere comunque sbagliati,
   *
   * @param mappaCodiciLotti
   * @param ngara
   * @throws GestoreException
   */
  public void creazioneLotti(HashMap<String,String> mappaCodiciLotti, String ngara,
      LoggerImportOffertaPrezzi loggerImport, Long tipoFornitura,HashMap<String,String> mappaDescrizioniLotti,
      HashMap<String,Long> mappaCodiciLottiOLIAMM, HashMap<String,String> mappaCodiciCIG) throws GestoreException {

    Iterator<String> iterator = mappaCodiciLotti.keySet().iterator();
    double sommaImpapp=0;
    while (iterator.hasNext()) {
      String codiga = iterator.next();
      String codiceLotto = mappaCodiciLotti.get(codiga);
      String codcig = mappaCodiciCIG.get(codiga);

      HashMap<String,Object> valoriCampi = new HashMap<String,Object>();
      valoriCampi.put("CODGAR1", ngara);
      valoriCampi.put("CODIGA", codiga);
      valoriCampi.put("NGARA", codiceLotto);

      // Controllo del valore del codice CIG: se uguale 0000000000, allora si vuole inserire un
      // cig fittizio, generando un valore univoco attraverso l'occorrenza nella tabella W_GENCHIAVI.
      if ("0000000000".equals(codcig)) {
    	int nextId = this.genChiaviManager.getNextId("GARE.CODCIG");
    	valoriCampi.put("CODCIG", "#".concat(StringUtils.leftPad("" + nextId, 9, "0")));
      } else {
    	valoriCampi.put("CODCIG", codcig);
      }

      if (new Long(98).equals(tipoFornitura)) {
        Long codcom = mappaCodiciLottiOLIAMM.get(codiga);;
        valoriCampi.put("CODCOM", codcom);
      }

      try {
        // Nella creazione di un lotto di gara per gare a lotti con offerta
        // unica vengono valorizzati i seguenti campi: CODGAR1, NGARA, CODIGA,
        // TIPGARG, MODASTG, ESTIMP, RIBCAL, MODLICG, SICINC, NOT_GAR,
        // PRECUT, PGAROF, IDIAUT, IMPAPP, IMPNRL, IMPSIC, GAROFF.
        // Alcuni di essi assumono valori dalla occorrenza complementare (quali
        // TIPGARG, MODASTG, ESTIMP, RIBCAL)
        Vector<?> datiLottoComplementare = this.sqlDao.getVectorQuery(
            "select MODASTG, RIBCAL, SICINC from GARE where CODGAR1 = ? and NGARA = ? and GENERE = ?",
            new Object[] { ngara, ngara, new Long(3) });
        if (datiLottoComplementare != null && datiLottoComplementare.size() > 0) {
          valoriCampi.put("MODASTG", SqlManager.getValueFromVectorParam(
              datiLottoComplementare, 0).getValue()); // MODASTG
          valoriCampi.put("RIBCAL", SqlManager.getValueFromVectorParam(
              datiLottoComplementare, 1).getValue()); // RIBCAL
          valoriCampi.put("SICINC", SqlManager.getValueFromVectorParam(
              datiLottoComplementare, 2).getValue()); //SICINC
        }

        // Altri campi da tabellati o valore costante
        // PRECUT.GARE = valore parametrizzato del tabellato A1018
        valoriCampi.put("PRECUT", new Long(
            this.tabellatiManager.getDescrTabellato("A1018", "1").trim()));
        // Valorizzazione del campo GARE.PGAROF (Percentuale di cauzione
        // provvisoria)
        Vector<?> datiTornata = this.sqlDao.getVectorQuery(
            "select TIPGEN, DPUBAV, DFPUBA, NAVVIG, DAVVIG, DIBAND, TATTOT, "
                 + "DATTOT, NATTOT, NPROAT, TIPGAR, MODLIC, CRITLIC, DETLIC, "
                 + "CALCSOAN, VALTEC, CONTOECO, ULTDETLIC, AQOPER, AQNUMOPE from TORN where CODGAR = ? ",
                 new Object[] { ngara });

        // Il campo TORN.TIPGEN dice se la gara e' per forniture (TIPGEN = 2)
        // o servizi (TIPGEN = 3)
        Long campoTORN_TIPGEN = (Long) SqlManager.getValueFromVectorParam(
            datiTornata, 0).getValue();
        String fdfs = PgManager.getTabellatoPercCauzioneProvvisoria(campoTORN_TIPGEN.intValue());

        String tmpPerCauzProvv = this.tabellatiManager.getDescrTabellato(fdfs, "1");
        Double percentualeCauzProvv = UtilityNumeri.convertiDouble(
            tmpPerCauzProvv, UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE);
        valoriCampi.put("PGAROF", percentualeCauzProvv);

        int numeroDecimali = this.pgManager.getArrotondamentoCauzioneProvvisoria(campoTORN_TIPGEN.intValue());

        Double percentualeCauzioneProvv = null;
        if(tmpPerCauzProvv!=null && !"".equals(tmpPerCauzProvv))
          percentualeCauzioneProvv = new Double(tmpPerCauzProvv);

        valoriCampi.put("DPUBAVG", SqlManager.getValueFromVectorParam(
            datiTornata, 1).getValue());
        valoriCampi.put("DFPUBAG", SqlManager.getValueFromVectorParam(
            datiTornata, 2).getValue());
        valoriCampi.put("NAVVIGG", SqlManager.getValueFromVectorParam(
            datiTornata, 3).getValue());
        valoriCampi.put("DAVVIGG", SqlManager.getValueFromVectorParam(
            datiTornata, 4).getValue());
        valoriCampi.put("DIBANDG", SqlManager.getValueFromVectorParam(
            datiTornata, 5).getValue());
        valoriCampi.put("TATTOG", SqlManager.getValueFromVectorParam(
            datiTornata, 6).getValue());
        valoriCampi.put("DATTOG", SqlManager.getValueFromVectorParam(
            datiTornata, 7).getValue());
        valoriCampi.put("NATTOG", SqlManager.getValueFromVectorParam(
            datiTornata, 8).getValue());
        valoriCampi.put("NPROAG", SqlManager.getValueFromVectorParam(
            datiTornata, 9).getValue());
        valoriCampi.put("TIPGARG", SqlManager.getValueFromVectorParam(
            datiTornata, 10).getValue());
        valoriCampi.put("MODLICG", SqlManager.getValueFromVectorParam(
            datiTornata, 11).getValue());
        valoriCampi.put("CRITLICG", SqlManager.getValueFromVectorParam(
            datiTornata, 12).getValue());
        valoriCampi.put("DETLICG", SqlManager.getValueFromVectorParam(
            datiTornata, 13).getValue());
        valoriCampi.put("CALCSOANG", SqlManager.getValueFromVectorParam(
            datiTornata, 14).getValue());
        // CF15/12/2015 Attributi per GARE1
        String valtec = null;
        Long contoeco = null;
        Long ultdetlic = null;
        Long aqoper = null;
        Long aqnumope = null;
        if (SqlManager.getValueFromVectorParam(datiTornata, 15) != null) {
          valtec= (String) SqlManager.getValueFromVectorParam(datiTornata, 15).getValue();
        }
        if (SqlManager.getValueFromVectorParam(datiTornata, 16) != null) {
          contoeco = (Long) SqlManager.getValueFromVectorParam(datiTornata, 16).getValue();
        }
        if (SqlManager.getValueFromVectorParam(datiTornata, 17) != null) {
          ultdetlic = (Long) SqlManager.getValueFromVectorParam(datiTornata, 17).getValue();
        }
        if (SqlManager.getValueFromVectorParam(datiTornata, 18) != null) {
          aqoper = (Long) SqlManager.getValueFromVectorParam(datiTornata, 18).getValue();
        }
        if (SqlManager.getValueFromVectorParam(datiTornata, 19) != null) {
          aqnumope = (Long) SqlManager.getValueFromVectorParam(datiTornata, 19).getValue();
        }
        // Determinazione dei campi GARE.IMPAPP, GARE.IMPNRL, GARE.IMPSIC,
        // GARE.IDIAUT, GARE.GAROFF
        double importoSoloSicurezza = 0;
        double importoNonSoggettoARibasso = 0;
        double importoSoggettoARibasso = 0;

        if(new Long(3).equals(tipoFornitura)){
            // Importo solo sicurezza (GARE.IMPSIC)
            List<?> listaImportiSoloSicurezza = this.sqlDao.getVectorQueryForList(
                "select QUANTI, PREZUN from GCAP where NGARA = ? and SOLSIC = ? "
                    + "and QUANTI is not null and PREZUN is not null",
                new Object[] { codiceLotto, new Long(1) });
            if (listaImportiSoloSicurezza != null
                && listaImportiSoloSicurezza.size() > 0) {
              for (int i = 0; i < listaImportiSoloSicurezza.size(); i++) {
                Vector<?> vettore = (Vector<?>) listaImportiSoloSicurezza.get(i);
                if (vettore != null && vettore.size() > 0) {
                  Double quantita = (Double) ((JdbcParametro) vettore.get(0)).getValue();
                  Double prezzoUnitario = (Double) ((JdbcParametro) vettore.get(1)).getValue();
                  importoSoloSicurezza += (quantita.doubleValue() * prezzoUnitario.doubleValue());
                }
              }

              if (importoSoloSicurezza > 0)
                valoriCampi.put("IMPSIC", new Double(UtilityMath.round(
                    importoSoloSicurezza, 2)));
            }
        }

        // Importo non soggetto a ribasso (GARE.IMPNRL)
        List<?> listaImportiNonSoggettiARibasso = null;
        if (new Long(3).equals(tipoFornitura)) {
            listaImportiNonSoggettiARibasso = this.sqlDao.getVectorQueryForList(
                "select QUANTI, PREZUN from GCAP where NGARA = ? and SOGRIB = ? "
                    + "and SOLSIC <> ? and QUANTI is not null and PREZUN is not null",
                new Object[] { codiceLotto, new Long(1), new Long(1) });

            if (listaImportiNonSoggettiARibasso != null
                && listaImportiNonSoggettiARibasso.size() > 0) {
              for (int i = 0; i < listaImportiNonSoggettiARibasso.size(); i++) {
                Vector<?> vettore = (Vector<?>) listaImportiNonSoggettiARibasso.get(i);
                if (vettore != null && vettore.size() > 0) {
                  Double quantita = (Double) ((JdbcParametro) vettore.get(0)).getValue();
                  Double prezzoUnitario = (Double) ((JdbcParametro) vettore.get(1)).getValue();
                  importoNonSoggettoARibasso += (quantita.doubleValue() * prezzoUnitario.doubleValue());
                }
              }

              if (importoNonSoggettoARibasso > 0)
                valoriCampi.put("IMPNRL", new Double(UtilityMath.round(
                    importoNonSoggettoARibasso, 2)));
            }
        }

        List<?> listaImportiSoggettiARibasso = null;
        if (new Long(3).equals(tipoFornitura)) {
            // Importo soggetto a ribasso: serve per calcolare GARE.IMPAPP (importo
            // della gara) come somma di importo totale articoli a solo sicurezza,
            // importo totale articoli non soggetti a ribasso e importo totale
            // articoli soggetti a ribasso
            listaImportiSoggettiARibasso = this.sqlDao.getVectorQueryForList(
                "select QUANTI, PREZUN from GCAP where NGARA = ? and SOLSIC <> ? "
                    + "and SOGRIB <> ? and QUANTI is not null and PREZUN is not null",
                new Object[] { codiceLotto, new Long(1), new Long(1) });
        } else {
                listaImportiSoggettiARibasso = this.sqlDao.getVectorQueryForList(
                    "select QUANTI, PREZUN from GCAP where NGARA = ? "
                        + "and SOLSIC = '2' and SOGRIB = '2' and QUANTI is not null "
                        +   "and PREZUN is not null", new Object[] { codiceLotto });
        }

        if (listaImportiSoggettiARibasso != null
            && listaImportiSoggettiARibasso.size() > 0) {
          for (int i = 0; i < listaImportiSoggettiARibasso.size(); i++) {
            Vector<?> vettore = (Vector<?>) listaImportiSoggettiARibasso.get(i);
            if (vettore != null && vettore.size() > 0) {
              Double quantita = (Double) ((JdbcParametro) vettore.get(0)).getValue();
              Double prezzoUnitario = (Double) ((JdbcParametro) vettore.get(1)).getValue();
              importoSoggettoARibasso += (quantita.doubleValue() * prezzoUnitario.doubleValue());
            }
          }
        }
        double impapp = UtilityMath.round(importoSoggettoARibasso, 2)
            + UtilityMath.round(importoSoloSicurezza, 2)
            + UtilityMath.round(importoNonSoggettoARibasso, 2);
        valoriCampi.put("IMPAPP", new Double(impapp));
        sommaImpapp += impapp;

        // Determinazione del campo GARE.GAROFF (Importo cauzione provvisoria)
        // come prodotto tra GARE.IMPAPP e GARE.PGAROF, con arrotondamento al
        // numero di decimali indicato dal
        if(percentualeCauzioneProvv!=null)
          valoriCampi.put("GAROFF", new Double(UtilityMath.round(impapp
              * percentualeCauzioneProvv.doubleValue()
              / 100, numeroDecimali)));
        else
          valoriCampi.put("GAROFF", null);

        // Determinazione del campo GARE.IDIAUT (Contributo della ditta alla
        // Autorita' Vigilanza Contratti Pub.), a partire da GARE.IMPAPP:
        // bisogna confrontare tale campo con il tabellato A1z01 che contiene
        // i range di importo del lotto il valore del campo
        Double idiaut = null;
        List<Tabellato> listaImporti = this.tabellatiManager.getTabellato("A1z01");

        if (listaImporti != null && listaImporti.size() > 0) {
          String descImportoMassimoGara = null;
          int posizioneSpazio = -1;
          Double importoMassimo = null;
          Double importoMinimo = new Double(0);
          for (int i = 0; i < listaImporti.size() && idiaut == null; i++) {
            Tabellato singoloScaglione = listaImporti.get(i);
            descImportoMassimoGara = singoloScaglione.getDescTabellato();
            posizioneSpazio = descImportoMassimoGara.indexOf(' ');
            importoMassimo = null;
            if (posizioneSpazio > 0) {
              importoMassimo = UtilityNumeri.convertiDouble(
                  StringUtils.replace(descImportoMassimoGara.substring(0,
                      posizioneSpazio), ",", "."),
                  UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE);
            }
            // l'ultima riga non indica il limite massimo, perchè non esiste,
            // per cui si fissa un limite massimo più alto possibile in modo
            // da essere sempre sotto
            if (importoMassimo == null)
              importoMassimo = new Double(Double.MAX_VALUE);

            if (impapp >= importoMinimo.doubleValue()
                && impapp < importoMassimo.doubleValue())
              if (singoloScaglione.getDatoSupplementare() != null)
                idiaut = new Double(singoloScaglione.getDatoSupplementare());
              else {
                importoMinimo = importoMassimo;
                importoMassimo = null;
              }
          }
        }
        if (idiaut != null) valoriCampi.put("IDIAUT", idiaut);

        // Determinazione del campo GARE.NOT_GAR come concatenazione della
        // descrizione
        // breve degli articoli non sicurezza e soggetti a ribasso
        // (SOLSIC <> 1 e SOGRIB <> 1) or (SOLSIC is null or SOGRIB is null)
        List<?> listaDescrBrevi;
        if (new Long(1).equals(tipoFornitura)) {
          listaDescrBrevi = this.sqlDao.getVectorQueryForList(
            "select PRINCATT from GCAP,GCAP_SAN" +
            " where GCAP.NGARA=GCAP_SAN.NGARA and GCAP.CONTAF=GCAP_SAN.CONTAF" +
            " and GCAP.NGARA = ? and ((SOLSIC <> ? and SOGRIB <> ?) "
                + "or (SOLSIC is null or SOGRIB  is null))", new Object[] {
                codiceLotto, "1", "1" });
        } else {
          if (new Long(2).equals(tipoFornitura)) {
            listaDescrBrevi = this.sqlDao.getVectorQueryForList(
            "select DPRODCAP from GCAP,GCAP_SAN" +
            " where GCAP.NGARA=GCAP_SAN.NGARA and GCAP.CONTAF=GCAP_SAN.CONTAF" +
            " and GCAP.NGARA = ? and ((SOLSIC <> ? and SOGRIB <> ?) "
                + "or (SOLSIC is null or SOGRIB  is null))", new Object[] {
                codiceLotto, "1", "1" });
          } else {
        	listaDescrBrevi = this.sqlDao.getVectorQueryForList(
        		"select VOCE from GCAP" +
        			" where NGARA = ? and ((SOLSIC <> ? and SOGRIB <> ?) "
                	+ "or (SOLSIC is null or SOGRIB  is null))", new Object[] {
                codiceLotto, "1", "1" });
          }
        }

        if (listaDescrBrevi != null && listaDescrBrevi.size() > 0 && !new Long(98).equals(tipoFornitura)) {
          StringBuffer oggettoLotto = new StringBuffer("");
          for (int re = 0; re < listaDescrBrevi.size(); re++) {
            Vector<?> tmpVec = (Vector<?>) listaDescrBrevi.get(re);
            if (oggettoLotto.length() > 0)
            	oggettoLotto.append(" - ");
            oggettoLotto.append(SqlManager.getValueFromVectorParam(tmpVec, 0).getStringValue());
          }

          Campo campoNOT_GAR = DizionarioCampi.getInstance().getCampoByNomeFisico(
              "GARE.NOT_GAR");

          if (oggettoLotto.length() > campoNOT_GAR.getLunghezza())
            valoriCampi.put("NOT_GAR", oggettoLotto.substring(0,
                campoNOT_GAR.getLunghezza() - 1));
          else
            valoriCampi.put("NOT_GAR", oggettoLotto.toString());
        } else if(new Long(98).equals(tipoFornitura)) {
          String oggettoLotto = mappaDescrizioniLotti.get(codiga);
          Campo campoNOT_GAR = DizionarioCampi.getInstance().getCampoByNomeFisico(
          		"GARE.NOT_GAR");

          if (oggettoLotto.length() > campoNOT_GAR.getLunghezza())
            valoriCampi.put("NOT_GAR", oggettoLotto.substring(0,
                campoNOT_GAR.getLunghezza() - 1));
          else
            valoriCampi.put("NOT_GAR", oggettoLotto);
        }

        String sqlInsertGARE = "insert into GARE (CAMPI) values (VALORI)";
        Iterator<String> iterator1 = valoriCampi.keySet().iterator();
        StringBuffer strCampi = new StringBuffer("");
        StringBuffer strValori = new StringBuffer("");
        Object[] valori = new Object[valoriCampi.size()];
        int indice = 0;
        while (iterator1.hasNext()) {
          String nomeCampo = iterator1.next();
          strCampi.append(nomeCampo.concat(", "));
          strValori.append("?, ");
          valori[indice] = valoriCampi.get(nomeCampo);
          indice++;
        }

        sqlInsertGARE = sqlInsertGARE.replaceFirst("CAMPI", strCampi.substring(
            0, strCampi.length() - 2));
        sqlInsertGARE = sqlInsertGARE.replaceFirst("VALORI",
            strValori.substring(0, strValori.length() - 2));

        this.sqlDao.update(sqlInsertGARE, valori);
        //Inserimento dell'occorrenza in GARE1
        this.sqlDao.update("insert into GARE1(NGARA,CODGAR1,VALTEC,CONTOECO,ULTDETLIC,AQOPER,AQNUMOPE) values(?,?,?,?,?,?,?)",new Object[]{codiceLotto,ngara,valtec,contoeco,ultdetlic,aqoper,aqnumope} );
      } catch (SQLException e) {
        throw new GestoreException("Errore nella creazione del lotto '"
            + codiceLotto
            + "' della gara '"
            + ngara
            + "'", "importOffertaPrezzi.erroreCreazioneLotto", e);
      }
      loggerImport.incrementaNumeroLottiCreati();
    }

    //Aggiornamento del valore di TORN.IMPTOR e TORN.ISTAUT
    Double importoTotaleImpapp = new Double(sommaImpapp);
    Double importoContributo = this.pgManager.getContributoAutoritaStAppaltante(importoTotaleImpapp, "A1z02");
    try {
      this.sqlDao.update("update torn set imptor=?,istaut=? where codgar=?",new Object[]{importoTotaleImpapp,importoContributo,ngara});
    } catch (SQLException e) {
        throw new GestoreException("Errore nell'aggiornamento dell'importo complessivo"
          + "' della gara '"
          + ngara
          + "'", "importOffertaPrezzi.erroreAggiornamentoImportoGara", e);
    }
   }

  /**
   * Scrittura sul foglio Excel dei dati generali della gara
   *
   * @param nagara
   * @throws SQLException
   * @throws GestoreException
   */
  private void setDatiGeneraliGara(String ngara, String profiloAttivo,
      boolean isGaraLottiConOffertaUnica, DizionarioStiliExcel dizStiliExcel)
      throws SQLException, GestoreException {

    HSSFSheet foglioDatiGenerali = workBook.createSheet(FOGLIO_DATI_GARA);
    UtilityExcel.setLarghezzaColonna(foglioDatiGenerali, 1, 30);
    UtilityExcel.setLarghezzaColonna(foglioDatiGenerali, 2, 45);

    HSSFRow riga = null;
    HSSFCell cella = null;
    HSSFCellStyle stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_INTESTAZIONE);

    // Scrittura dell'intestazione delle due colonne del foglio
    // Prima riga del foglio
    riga = foglioDatiGenerali.createRow(0);
    // Prima cella: vuota
    cella = riga.createCell(0);
    cella.setCellStyle(stile);
    // Seconda cella: titolo "Dati generali della gara"
    cella = riga.createCell(1);
    cella.setCellStyle(stile);
    cella.setCellValue(new HSSFRichTextString("Dati generali della gara"));
    stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_NASCOSTA);
    // Seconda riga del foglio (riga nascosta)
    riga = foglioDatiGenerali.createRow(1);
    // Prima cella: vuota
    cella = riga.createCell(0);
    cella.setCellStyle(stile);
    // Seconda cella: vuota
    cella = riga.createCell(1);
    cella.setCellStyle(stile);
    riga.setZeroHeight(true); // nascondo la riga

    stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_SEPARATRICE);
    // Terza riga del foglio (riga di divisione tra intestazione e dati)
    riga = foglioDatiGenerali.createRow(2);
    riga.setHeightInPoints(3);
    // Prima cella: vuota
    cella = riga.createCell(0);
    cella.setCellStyle(stile);
    // Seconda cella: vuota
    cella = riga.createCell(1);
    cella.setCellStyle(stile);
    // Fine intestazione foglio

    int indiceRiga = FOGLIO_DATI_GARA_RIGA_INIZIALE_DATI_GARA;
    GestoreProfili gestoreProfili = geneManager.getProfili();

    if (isGaraLottiConOffertaUnica) {
      try {
        // Dati generali della gara
        String selectTORN = "select DESTOR, IMPTOR from TORN where CODGAR = ? ";
        Vector<?> datiTorn = sqlDao.getVectorQuery(selectTORN, new Object[] { ngara });
        if (datiTorn != null && datiTorn.size() > 0) {
          String destor = SqlManager.getValueFromVectorParam(datiTorn,
              0).toString();
          Double imptor = SqlManager.getValueFromVectorParam(datiTorn,
              1).doubleValue();
          if (imptor == null) imptor = new Double(0);

          // codice ALICE
          if (gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
              "GARE.TORN.CODGAR")) {
            UtilityExcel.scriviCella(foglioDatiGenerali, 1, indiceRiga,
                "Codice gara", dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.STRINGA_ALIGN_RIGHT_BOLD_ITALIC));
            if (ngara != null && ngara.length() > 0)
              UtilityExcel.scriviCella(foglioDatiGenerali, 2, indiceRiga++,
                  ngara, dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.STRINGA_ALIGN_LEFT));
            else
              indiceRiga++;
          }

          // Titolo della tornata
          if (gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
              "GARE.GARE.NOT_GAR")) {
            UtilityExcel.scriviCella(foglioDatiGenerali, 1, indiceRiga, "Oggetto",
                dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.STRINGA_ALIGN_RIGHT_BOLD_ITALIC));
            if (destor != null && destor.length() > 0)
              UtilityExcel.scriviCella(foglioDatiGenerali, 2, indiceRiga++,
                  destor, dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.STRINGA_ALIGN_LEFT));
            else
              indiceRiga++;
          }

          // Importo complessivo
          if (gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
              "GARE.TORN.IMPTOR")) {
            UtilityExcel.scriviCella(foglioDatiGenerali, 1, indiceRiga,
                "Importo complessivo", dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.STRINGA_ALIGN_RIGHT_ITALIC));
            if (imptor != null && imptor.doubleValue() != 0)
              UtilityExcel.scriviCella(foglioDatiGenerali, 2,
                  indiceRiga++, imptor,
                  dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.DECIMALE2_ALIGN_RIGHT));
            else
              indiceRiga++;
          }
        }
      } catch (SQLException s) {
        logger.error("Export lavorazioni e forniture della gara a lotti con "
            + "offerta unica '" + ngara + "': errore durante l'export su Excel "
            + "dello sheet 'Dati generali'");
        throw s;
      } catch (GestoreException g) {
        logger.error("Export lavorazioni e forniture della gara a lotti con "
            + "offerta unica '" + ngara + "': errore durante l'export su Excel "
            + "dello sheet 'Dati generali' della gare per lavorazioni e forniture");
        throw g;
      }
    } else {
      try {

        // Dati generali della gara
        String selectGARE = "select CODCIG, CODIGA, NOT_GAR, IMPMIS, IMPCOR, "
            + "IMPAPP, IMPNRL, IMPSIC, ONPRGE, TIPGEN, CODGAR from GARE,TORN where ngara = ?"
            + " and GARE.CODGAR1=TORN.CODGAR";

        Vector<?> datiGare = sqlDao.getVectorQuery(selectGARE, new Object[] { ngara });
        if (datiGare != null && datiGare.size() > 0) {

          Long tipoGara = this.getTipoGara(ngara);

          String codiceCIG = SqlManager.getValueFromVectorParam(
              datiGare, 0).toString();
          String codiga = SqlManager.getValueFromVectorParam(datiGare,
              1).toString();
          String not_gar = SqlManager.getValueFromVectorParam(
              datiGare, 2).toString();

          Double impmis = SqlManager.getValueFromVectorParam(datiGare,
              3).doubleValue();
          if (impmis == null) impmis = new Double(0);

          Double impcor = SqlManager.getValueFromVectorParam(datiGare,
              4).doubleValue();

          Double impapp = SqlManager.getValueFromVectorParam(datiGare,
              5).doubleValue();
          if (impapp == null) impapp = new Double(0);

          Double impnrl = SqlManager.getValueFromVectorParam(datiGare,
              6).doubleValue();
          if (impnrl == null) impnrl = new Double(0);

          Double impsic = SqlManager.getValueFromVectorParam(datiGare,
              7).doubleValue();
          if (impsic == null) impsic = new Double(0);

          Double onprge = (Double) SqlManager.getValueFromVectorParam(datiGare,
              8).getValue();

          Long tipgen = (Long) SqlManager.getValueFromVectorParam(datiGare,
              9).getValue();

          String codgar =  SqlManager.getValueFromVectorParam(
              datiGare, 10).toString();

          //Nel caso di lotto di gara per una gara con bustalotti=1 si deve caricare l'oggetto della gara
          Long bustalotti = null;
          String destor = null;
          Vector<?> datiGaraComplementare = this.sqlDao.getVectorQuery(
        		  "select bustalotti,destor from gare,torn where codgar1=codgar and ngara=(select codgar1 from gare g where g.ngara=?)",
        		  new Object[]{ngara});
          if (datiGaraComplementare != null && datiGaraComplementare.size() > 0) {
            bustalotti = (Long) SqlManager.getValueFromVectorParam(datiGaraComplementare,
                0).getValue();
            destor = (String) SqlManager.getValueFromVectorParam(datiGaraComplementare,
                1).getValue();
          }

          if ((new Long(1)).equals(bustalotti)) {
            // codice ALICE
            if (gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS", "GARE.GARE.CODGAR1")) {
              UtilityExcel.scriviCella(foglioDatiGenerali, 1, indiceRiga,
                  "Codice gara", dizStiliExcel.getStileExcel(
                          DizionarioStiliExcel.STRINGA_ALIGN_RIGHT_BOLD_ITALIC));
              if (codgar != null && codgar.length() > 0)
                UtilityExcel.scriviCella(foglioDatiGenerali, 2, indiceRiga++,
                    codgar, dizStiliExcel.getStileExcel(
                          DizionarioStiliExcel.STRINGA_ALIGN_LEFT));
              else
                indiceRiga++;
            }

            //Oggetto gara
            if (gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
                "GARE.GARE.NOT_GAR")) {
              UtilityExcel.scriviCella(foglioDatiGenerali, 1, indiceRiga,
                  "Oggetto gara", dizStiliExcel.getStileExcel(
                          DizionarioStiliExcel.STRINGA_ALIGN_RIGHT_BOLD_ITALIC));
              if (destor != null && destor.length() > 0)
                UtilityExcel.scriviCella(foglioDatiGenerali, 2, indiceRiga++,
                    destor, dizStiliExcel.getStileExcel(
                          DizionarioStiliExcel.STRINGA_ALIGN_LEFT));
              else
                indiceRiga++;
            }
            // lotto
            if (gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
                "GARE.GARE.CODIGA")) {
              UtilityExcel.scriviCella(foglioDatiGenerali, 1, indiceRiga,
                  "Lotto", dizStiliExcel.getStileExcel(
                          DizionarioStiliExcel.STRINGA_ALIGN_RIGHT_BOLD_ITALIC));
              if (codiga != null && codiga.length() > 0)
                UtilityExcel.scriviCella(foglioDatiGenerali, 2, indiceRiga++,
                    codiga, dizStiliExcel.getStileExcel(
                          DizionarioStiliExcel.STRINGA_ALIGN_LEFT));
              else
                indiceRiga++;
            }

          } else {
            // codice ALICE
            if (gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
                "GARE.GARE.NGARA")) {
              UtilityExcel.scriviCella(foglioDatiGenerali, 1, indiceRiga,
                  "Codice gara", dizStiliExcel.getStileExcel(
                          DizionarioStiliExcel.STRINGA_ALIGN_RIGHT_BOLD_ITALIC));
              if (ngara != null && ngara.length() > 0)
                UtilityExcel.scriviCella(foglioDatiGenerali, 2, indiceRiga++,
                    ngara, dizStiliExcel.getStileExcel(
                          DizionarioStiliExcel.STRINGA_ALIGN_LEFT));
              else
                indiceRiga++;
            }
          }

          // Codice CIG
          if (gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
              "GARE.GARE.CODCIG")) {
            UtilityExcel.scriviCella(foglioDatiGenerali, 1, indiceRiga,
                "Codice CIG", dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.STRINGA_ALIGN_RIGHT_BOLD_ITALIC));
            if (codiceCIG != null && codiceCIG.length() > 0)
              UtilityExcel.scriviCella(foglioDatiGenerali, 2, indiceRiga++,
                  codiceCIG, dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.STRINGA_ALIGN_LEFT));
            else
              indiceRiga++;
          }

          // Titolo della gara
          if (gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
              "GARE.GARE.NOT_GAR")) {
            UtilityExcel.scriviCella(foglioDatiGenerali, 1, indiceRiga,
                "Oggetto", dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.STRINGA_ALIGN_RIGHT_BOLD_ITALIC));
            if (not_gar != null && not_gar.length() > 0)
              UtilityExcel.scriviCella(foglioDatiGenerali, 2, indiceRiga++,
                  not_gar, dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.STRINGA_ALIGN_LEFT));
            else
              indiceRiga++;
          }

          // Importo opere a misura
          if (gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
              "GARE.GARE.IMPMIS") && "1".equals("" + tipoGara)) {
            UtilityExcel.scriviCella(foglioDatiGenerali, 1, indiceRiga,
                "Importo opere a misura", dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.STRINGA_ALIGN_RIGHT_ITALIC));
            if (impmis != null && impmis.doubleValue() != 0)
              UtilityExcel.scriviCella(foglioDatiGenerali, 2,
                  indiceRiga++, impmis, dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.DECIMALE2_ALIGN_RIGHT));
            else
              indiceRiga++;
          }

          // Importo opere a corpo
          if (gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
              "GARE.GARE.IMPCOR") && "1".equals("" + tipoGara)) {
            UtilityExcel.scriviCella(foglioDatiGenerali, 1, indiceRiga,
                "Importo opere a corpo", dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.STRINGA_ALIGN_RIGHT_ITALIC));
            if (impcor != null)
              UtilityExcel.scriviCella(foglioDatiGenerali, 2, indiceRiga++,
                  impcor, dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.DECIMALE2_ALIGN_RIGHT));
            else
              indiceRiga++;
          }

          // Importo oneri di progettazione: non previsto da analisi
          if(gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
              "GARE.ONPRGE") && tipgen!=null && tipgen.longValue()==1){
            UtilityExcel.scriviCella(foglioDatiGenerali, 1, indiceRiga,
                "Oneri di progettazione", dizStiliExcel.getStileExcel(
                    DizionarioStiliExcel.STRINGA_ALIGN_RIGHT_ITALIC));
            if (onprge != null) {
              UtilityExcel.scriviCella(foglioDatiGenerali, 2, indiceRiga++,onprge,
                  dizStiliExcel.getStileExcel(
                      DizionarioStiliExcel.DECIMALE2_ALIGN_RIGHT));

            } else
              indiceRiga++;

          }


          // Importo a base d'asta
          if (gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
              "GARE.GARE.IMPAPP")) {
            UtilityExcel.scriviCella(foglioDatiGenerali, 1, indiceRiga,
                "Importo a base di gara", dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.STRINGA_ALIGN_RIGHT_BOLD_ITALIC));
            if (impapp != null) {
              UtilityExcel.scriviCella(foglioDatiGenerali, 2, indiceRiga++,impapp,
                  dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.DECIMALE2_ALIGN_RIGHT_LEFT_BOLD));
            } else
              indiceRiga++;
          }

          if (onprge == null) onprge = new Double(0);
          // Importo soggetto a ribasso = IMPAPP – IMPNRL – IMPSIC – ONPRGE
          if (gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
              "GARE.GARE.IMPNRL")
              || gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
                  "GARE.GARE.IMPSIC")) {

            double importoSoggettoARibasso = impapp.doubleValue()
                - impnrl.doubleValue() - impsic.doubleValue()
                - onprge.doubleValue();
            UtilityExcel.scriviCella(foglioDatiGenerali, 1, indiceRiga,
                "di cui soggetto a ribasso", dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.STRINGA_ALIGN_RIGHT_ITALIC));
            UtilityExcel.scriviCella(foglioDatiGenerali, 2, indiceRiga++,
                new Double(importoSoggettoARibasso),
                dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.DECIMALE2_ALIGN_RIGHT));
          }

          // Importo non soggetto a ribasso
          if (gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
              "GARE.GARE.IMPNRL")) {
            UtilityExcel.scriviCella(foglioDatiGenerali, 1, indiceRiga,
                "di cui non soggetto a ribasso", dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.STRINGA_ALIGN_RIGHT_ITALIC));
            if (impnrl != null && impnrl.doubleValue() != 0)
              UtilityExcel.scriviCella(foglioDatiGenerali, 2, indiceRiga++,
                  impnrl, dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.DECIMALE2_ALIGN_RIGHT));
            else
              indiceRiga++;
          }

          // Importo di cui sicurezza
          if (gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
              "GARE.GARE.IMPSIC")) {
            UtilityExcel.scriviCella(foglioDatiGenerali, 1, indiceRiga,
                "di cui sicurezza", dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.STRINGA_ALIGN_RIGHT_ITALIC));
            if (impsic != null && impsic.doubleValue() != 0)
              UtilityExcel.scriviCella(foglioDatiGenerali, 2, indiceRiga++,
                  impsic, dizStiliExcel.getStileExcel(
                        DizionarioStiliExcel.DECIMALE2_ALIGN_RIGHT));
          }

          // Importo oneri di progettazione: non previsto da analisi
          /*
           * if(gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
           * "GARE.ONPRGE")){ campo =
           * dizCampi.getCampoByNomeFisico("GARE.ONPRGE");
           * UtilityExcel.scriviCella(foglioDatiGenerali, 1, indiceRiga,
           * campo.getDescrizioneBreve(), stileTestoRight); if (onprge != null
           * && onprge.doubleValue() != 0)
           * UtilityExcel.scriviCella(foglioDatiGenerali, 2, indiceRiga++,
           * onprge, stileDecimal2Right); }
           */
        }
      } catch (SQLException s) {
        logger.error("Export lavorazioni e forniture della gara '" + ngara
            + "': errore durante l'export su Excel dello sheet 'Dati generali'");
        throw s;
      } catch (GestoreException g) {
        logger.error("Export lavorazioni e forniture della gara '" + ngara
            + "': errore durante l'export su Excel dello sheet 'Dati generali' "
            + "della gare per lavorazioni e forniture");
        throw g;
      }
    }



    /*
     * Si e' deciso di tracciare su log il punto esatto in cui si verifica
     * l'errore, catturando l'eccezione, loggando una stringa di errore e
     * riemettendo l'eccezione stessa, visto che nella procedura di export su
     * foglio excel, lo stesso tipo di eccezione può essere emessa in punti
     * diversi.
     */
  }

  /**
   * Scrittura su foglio Excel dei dati di lavorazioni e forniture
   *
   * @param ngara
   * @throws GestoreException
   * @throws SQLException
   */
  private void setLavorazioniForniture(String ngara, String profiloAttivo,
      boolean exportPrezziUnitari, boolean isGaraLottiConOffertaUnica,
      DizionarioStiliExcel dizStiliExcel, Long tipoFornitura, String ribcal)
      throws GestoreException, SQLException {

    GestoreProfili gestoreProfili = geneManager.getProfili();

    HSSFSheet foglioLavorazioniForniture=null;

    if (new Long(98).equals(tipoFornitura)){
      foglioLavorazioniForniture = workBook.createSheet(FOGLIO_LISTA_PRODOTTI[0]);
    } else {
      foglioLavorazioniForniture = workBook.createSheet(FOGLIO_LAVORAZIONE_E_FORNITURE[0]);
    }

    boolean isCodificaAutomaticaAttiva = this.geneManager.isCodificaAutomatica("GARE", "NGARA");

    //Si determina se è attiva l'integrazione con WSERP
    String urlWSERP = ConfigManager.getValore("wserp.erp.url");
    String tipoWSERP = "";
    if(urlWSERP != null && !"".equals(urlWSERP)){
      WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
      if(configurazione.isEsito()){
        tipoWSERP = configurazione.getRemotewserp();
      }
    }

    Long tipoGara = null;
    if (isGaraLottiConOffertaUnica) {
      /*
       * I campi standard da visualizzare sono nell'ordine: NGARA, NORVOC,
       * CODVOC, VOCE, DESEST, SOLSIC, SOGRIB, UNIMIS.DESUNI, QUANTI, PREZUN.
       * L'esportazione di tali campi dipende dal profilo con cui si effettua
       * l'export, tuttavia si assume che alcuni campi fondamentali, come il
       * codice articolo (GCAP.CODVOC), la quantità (GCAP.QUANTI) e il prezzo
       * GCAP.PREZUN), siano SEMPRE visibili. Il campo Importo, essendo un campo
       * calcolato, viene sempre esportato come formula
       */

      if(!new Long(98).equals(tipoFornitura))
        arrayCampiVisibili[7] = false; // CLASI1 non visibile
      tipoGara = new Long(-1); // deve assumere un valore diverso da 1, in
      // quanto le gare a lotti con offerta unica non sono MAI gare per lavori,
      // ma sempre per forniture o servizi

      if (isCodificaAutomaticaAttiva) {
        arrayCampiVisibili[0] = false; // NGARA non visibile
        arrayIndiceColonnaCampi[0] = -1;
      } else {
        arrayCampiVisibili[1] = false; // CODIGA non visibile
        arrayIndiceColonnaCampi[1] = -1;
        arrayCampiVisibili[2] = false; // CODCIG non visibile
        arrayIndiceColonnaCampi[2] = -1;
      }
    } else {
      /*
       * I campi standard da visualizzare sono nell'ordine: CODVOC, VOCE,
       * DESEST, CLASI1, SOLSIC, SOGRIB, UNIMIS.DESUNI, QUANTI, PREZUN.
       * L'esportazione di tali campi dipende dal profilo con cui si effettua
       * l'export, tuttavia si assume che alcuni campi fondamentali, come il
       * codice articolo (GCAP.CODVOC), la quantità (GCAP.QUANTI) e il prezzo
       * GCAP.PREZUN), siano SEMPRE visibili. Il campo Importo, essendo un campo
       * calcolato, viene sempre esportato come formula
       */
      arrayCampiVisibili[0] = false; // GCAP.NGARA non visibile
      arrayCampiVisibili[1] = false; // GARE.CODIGA non visibile
      arrayCampiVisibili[2] = false; // GARE.CODCIG non visibile
      // arrayCampiVisibili[?] = false; // GCAP.NORVOC non visibile
      tipoGara = getTipoGara(ngara);
    }

    // Determino i campi non visibili, il numero di campi visibili e aggiorno
    // il numero di colonna di ciascun campo successivo a quello che si sta
    // considerando, quando questo non e' visibile da profilo, trascurando
    // i campi SEMPRE visibili.
    // Il campo CLASI1 e' esportabile solo per gare di tipo 'Lavori'
    // (TORN.TIPGEN = 1)
    if (isGaraLottiConOffertaUnica) {
      // Per le gare con offerta unica e codifica automatica il campo CODIGA e'
      // visibile, mentre per le gare con offerta unica senza codifica
      // automatica il campo NGARA e' visibile
      /*
       * if(arrayCampiVisibili[2] == false ||
       * !gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
       * arrayCampi[2])){ arrayCampiVisibili[2] = false;
       * arrayIndiceColonnaCampi[2] = -1; }
       */

      // Aggiornamento del numero di colonna di ciascun campo successivo
      // a quello che si sta considerando
      int hi = 1;
      if (isCodificaAutomaticaAttiva) hi = 0;
      for (; hi < arrayCampiVisibili.length; hi++)
        arrayIndiceColonnaCampi[hi]--;
    } else {
      arrayIndiceColonnaCampi[0] = -1; // NGARA non visibile
      arrayIndiceColonnaCampi[1] = -1; // CODIGA non visibile
      arrayIndiceColonnaCampi[2] = -1; // CODCIG non visibile
      // arrayIndiceColonnaCampi[2] = -1; // NORVOC non visibile
      // Aggiornamento del numero di colonna di ciascun campo successivo
      // a quello che si sta considerando
      for (int hi = 2; hi < arrayCampiVisibili.length; hi++)
         arrayIndiceColonnaCampi[hi] = arrayIndiceColonnaCampi[hi] - 2;
    }

     for (int h = 2; h < arrayCampiVisibili.length ; h++) {
      if (arrayCampiVisibili[h] == false
          || !gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
              arrayCampi[h])
          || (arrayCampi[h].indexOf("CLASI1") >= 0 && !"1".equals("" + tipoGara))){

        //if(!(arrayCampi[h].indexOf("QUANTI") >= 0 ) || (arrayCampi[h].indexOf("PREZUN") >= 0 )){
        boolean eseguireAggiornamento = false;
        if(tipoFornitura.longValue()==1 || tipoFornitura.longValue()==2 || tipoFornitura.longValue()==98)
          eseguireAggiornamento = true;
        else if(!(arrayCampi[h].indexOf("QUANTI") >= 0 ) || (arrayCampi[h].indexOf("PREZUN") >= 0 ))
          eseguireAggiornamento = true;
        if(  eseguireAggiornamento){
          arrayCampiVisibili[h] = false;
          arrayIndiceColonnaCampi[h] = -1;
          // Aggiornamento del numero di colonna di ciascun campo successivo
          // a quello che si sta considerando
            for (int hi = h + 1; hi < arrayCampiVisibili.length; hi++)
              arrayIndiceColonnaCampi[hi]--;
        }

      }
    }

    // Set dell'indice della colonna Importo
     int k = 1;
     while(arrayIndiceColonnaCampi[arrayIndiceColonnaCampi.length - k] < 0){
       k++;
     }
    int indiceColonnaImporto = arrayIndiceColonnaCampi[arrayIndiceColonnaCampi.length - k] + 1;
    if(new Long(98).equals(tipoFornitura)){
      indiceColonnaImporto = arrayIndiceColonnaCampi[arrayIndiceColonnaCampi.length - 2] + 1;
    }

    String selectGCAP = null;

    switch (tipoFornitura.intValue()) {
    case 1:
      if (it.eldasoft.utils.sql.comp.SqlManager.DATABASE_ORACLE.equals(SqlManager.getTipoDB())) {
        selectGCAP = " select GCAP.CODVOC, " // 0
            + " GCAP.VOCE, " // 1
            + " GCAP.CLASI1, " // 2
            + " GCAP.SOLSIC, " // 3
            + " GCAP.SOGRIB, " // 4
            + " UNIMIS.DESUNI, " // 5
            + " GCAP.QUANTI, " // 6
            + " GCAP.PREZUN, " // 7
            + " GCAP.NGARA, " // 8
            + " GCAP.CONTAF, " // 9
            + " GCAP.NORVOC, " // 10
            + " GCAP_SAN.CODATC, " // 11
            + " GCAP_SAN.CODAUR, " // 12
            + " GCAP_SAN.PRINCATT, " // 13
            + " GCAP_SAN.FORMAFARM, " // 14
            + " GCAP_SAN.DOSAGGIO, " // 15
            + " GCAP_SAN.VIASOMM, " // 16
            + " GCAP_SAN.NOTE  " // 17
            + " from GCAP, unimis, GCAP_SAN "
            + " where GCAP.UNIMIS = UNIMIS.TIPO(+) "
            + " and GCAP.NGARA = ? "
            + " and GCAP.DITTAO is null "
            + " and UNIMIS.CONTA(+) = -1 "
            + " and GCAP.NGARA = GCAP_SAN.NGARA(+) "
            + " and GCAP.CONTAF = GCAP_SAN.CONTAF(+) "
            + " order by GCAP.NORVOC asc, GCAP.CONTAF asc ";
      } else if (it.eldasoft.utils.sql.comp.SqlManager.DATABASE_SQL_SERVER.equals(SqlManager.getTipoDB()) ||
          it.eldasoft.utils.sql.comp.SqlManager.DATABASE_POSTGRES.equals(SqlManager.getTipoDB())) {
        selectGCAP = "select GCAP.CODVOC, " // 0
            + " GCAP.VOCE, " // 1
            + " GCAP.CLASI1, " // 2
            + " GCAP.SOLSIC, " // 3
            + " GCAP.SOGRIB, " // 4
            + " UNIMIS_1.DESUNI, " // 5
            + " GCAP.QUANTI, " // 6
            + " GCAP.PREZUN, " // 7
            + " GCAP.NGARA, " // 8
            + " GCAP.CONTAF, " // 9
            + " GCAP.NORVOC, " // 10
            + " GCAP_SAN.CODATC, " // 11
            + " GCAP_SAN.CODAUR, " // 12
            + " GCAP_SAN.PRINCATT, " // 13
            + " GCAP_SAN.FORMAFARM, " // 14
            + " GCAP_SAN.DOSAGGIO, " // 15
            + " GCAP_SAN.VIASOMM, " // 16
            + " GCAP_SAN.NOTE  " // 17
            + " from (GCAP left outer join (select * from unimis where conta=-1) unimis_1 on gcap.unimis = unimis_1.tipo) "
            + " left outer join gcap_san on gcap.ngara = gcap_san.ngara and gcap.contaf = gcap_san.contaf "
            + " where GCAP.NGARA = ? "
            + " and GCAP.DITTAO is null "
            + " order by GCAP.NORVOC asc, GCAP.CONTAF asc";
      }
      break;

    case 2:
      if (it.eldasoft.utils.sql.comp.SqlManager.DATABASE_ORACLE.equals(SqlManager.getTipoDB())) {
        selectGCAP = " select GCAP.CODVOC, " // 0
            + " GCAP.VOCE, " // 1
            + " GCAP.CLASI1, " // 2
            + " GCAP.SOLSIC, " // 3
            + " GCAP.SOGRIB, " // 4
            + " UNIMIS.DESUNI, " // 5
            + " GCAP.QUANTI, " // 6
            + " GCAP.PREZUN, " // 7
            + " GCAP.NGARA, " // 8
            + " GCAP.CONTAF, " // 9
            + " GCAP.NORVOC, " // 10
            + " GCAP_SAN.CODCLASS, " // 11
            + " GCAP_SAN.DEPRODCN, " // 12
            + " GCAP_SAN.CODAUR, " // 13
            + " GCAP_SAN.DPRODCAP " // 14
            + " from GCAP, unimis, GCAP_SAN "
            + " where GCAP.UNIMIS = UNIMIS.TIPO(+) "
            + " and GCAP.NGARA = ? "
            + " and GCAP.DITTAO is null "
            + " and UNIMIS.CONTA(+) = -1 "
            + " and GCAP.NGARA = GCAP_SAN.NGARA(+) "
            + " and GCAP.CONTAF = GCAP_SAN.CONTAF(+) "
            + " order by GCAP.NORVOC asc, GCAP.CONTAF asc ";
      } else if (it.eldasoft.utils.sql.comp.SqlManager.DATABASE_SQL_SERVER.equals(SqlManager.getTipoDB())
          || it.eldasoft.utils.sql.comp.SqlManager.DATABASE_POSTGRES.equals(SqlManager.getTipoDB())) {
        selectGCAP = " select GCAP.CODVOC, " // 0
            + " GCAP.VOCE, " // 1
            + " GCAP.CLASI1, " // 2
            + " GCAP.SOLSIC, " // 3
            + " GCAP.SOGRIB, " // 4
            + " UNIMIS_1.DESUNI, " // 5
            + " GCAP.QUANTI, " // 6
            + " GCAP.PREZUN, " // 7
            + " GCAP.NGARA, " // 8
            + " GCAP.CONTAF, " // 9
            + " GCAP.NORVOC, " // 10
            + " GCAP_SAN.CODCLASS, " // 11
            + " GCAP_SAN.DEPRODCN, " // 12
            + " GCAP_SAN.CODAUR, " // 13
            + " GCAP_SAN.DPRODCAP " // 14
            + " from (GCAP left outer join (select * from unimis where conta=-1) unimis_1 on gcap.unimis = unimis_1.tipo) "
            + " left outer join gcap_san on gcap.ngara = gcap_san.ngara and gcap.contaf = gcap_san.contaf "
            + " where GCAP.NGARA = ? "
            + " and GCAP.DITTAO is null "
            + " order by GCAP.NORVOC asc, GCAP.CONTAF asc";
      }
      break;

    case 3:
      if (it.eldasoft.utils.sql.comp.SqlManager.DATABASE_ORACLE.equals(SqlManager.getTipoDB())) {
        selectGCAP = "select GCAP.CODVOC, GCAP.VOCE, GCAP.CLASI1, GCAP.SOLSIC, GCAP.SOGRIB, UNIMIS.DESUNI, GCAP.QUANTI, GCAP.PREZUN, GCAP.NGARA, GCAP.CONTAF, GCAP.NORVOC, GCAP.CODCAT, GCAP.PERCIVA, GCAP.PESO "
            + "from GCAP, unimis "
            + "where GCAP.UNIMIS = UNIMIS.TIPO(+) "
            + "and GCAP.NGARA = ? "
            + "and GCAP.DITTAO is null "
            + "and UNIMIS.CONTA(+) = -1 "
            + "order by GCAP.NORVOC asc, GCAP.CONTAF asc";
        // Osservazione: questa query e' stata testata su Oracle 10
      } else if (it.eldasoft.utils.sql.comp.SqlManager.DATABASE_SQL_SERVER.equals(SqlManager.getTipoDB())
          || it.eldasoft.utils.sql.comp.SqlManager.DATABASE_POSTGRES.equals(SqlManager.getTipoDB())) {
        selectGCAP = "select GCAP.CODVOC, GCAP.VOCE, GCAP.CLASI1, GCAP.SOLSIC, GCAP.SOGRIB, UNIMIS_1.DESUNI, GCAP.QUANTI, GCAP.PREZUN, GCAP.NGARA, GCAP.CONTAF, GCAP.NORVOC, GCAP.CODCAT, GCAP.PERCIVA, GCAP.PESO "
            + "from GCAP left outer join (select * from unimis where conta=-1) unimis_1 on GCAP.UNIMIS = UNIMIS_1.TIPO "
            + "where GCAP.NGARA = ? "
            + "and GCAP.DITTAO is null "
            + "order by GCAP.NORVOC asc, GCAP.CONTAF asc";
        // Osservazione: questa query e' stata testata su MS SqlServer 2005
      }  else if (it.eldasoft.utils.sql.comp.SqlManager.DATABASE_DB2.equals(SqlManager.getTipoDB())) {
        // TODO query da implementare
      }
      break;

    case 98:
      if (it.eldasoft.utils.sql.comp.SqlManager.DATABASE_ORACLE.equals(SqlManager.getTipoDB())) {
        selectGCAP = " select GCAP.CODVOC, " // 0
            + " GCAP.NUNICONF, " // 1
            + " GCAP.VOCE, " // 2
            + " UNIMIS.DESUNI, " // 3
            + " GCAP.QUANTI, " // 4
            + " GCAP.NGARA, " // 5
            + " GCAP.CONTAF, " // 6
            + " GCAP.NORVOC, " // 7
            + " GCAP.IVAPROD " // 8
            + " from GCAP, unimis "
            + " where GCAP.UNIMIS = UNIMIS.TIPO(+) "
            + " and GCAP.NGARA = ? "
            + " and GCAP.DITTAO is null "
            + " and UNIMIS.CONTA(+) = -1 "
            + " order by GCAP.NORVOC asc, GCAP.CONTAF asc ";


      } else if (it.eldasoft.utils.sql.comp.SqlManager.DATABASE_SQL_SERVER.equals(SqlManager.getTipoDB())
          || it.eldasoft.utils.sql.comp.SqlManager.DATABASE_POSTGRES.equals(SqlManager.getTipoDB())) {
        selectGCAP = "select GCAP.CODVOC, " // 0
            + " GCAP.NUNICONF, " // 1
            + " GCAP.VOCE, " // 2
            + " UNIMIS_1.DESUNI, " // 3
            + " GCAP.QUANTI, " // 4
            + " GCAP.NGARA, " // 5
            + " GCAP.CONTAF, " // 6
            + " GCAP.NORVOC, " // 7
            + " GCAP.IVAPROD " // 8
            + " from (GCAP left outer join (select * from unimis where conta=-1) unimis_1 on gcap.unimis = unimis_1.tipo) "
            + " where GCAP.NGARA = ? "
            + " and GCAP.DITTAO is null "
            + " order by GCAP.NORVOC asc, GCAP.CONTAF asc";
      }
      break;


    default:
      break;
    }

    // Per le gare a lotti con offerta unica, si estraggono la lista dei lotti
    // con per cui sono stati definiti delle forniture e/o servizi e su tali
    // lotti si ripete lo stesso codice. Per gli altri tipi di gara non e'
    // necessario estrarre i lotti, perchè si estraggono le forniture e/o
    // servizi
    // per ciascun lotto
    List<String> listaLottiDiGara = new ArrayList<String>();

    try {
      // Ultima riga del foglio da formattare o a cui applicare la formula per
      // il calcolo dell'importo come prodotto quantita' * prezzo unitario
      int numeroRigheDaFormattare = FOGLIO_LAVORAZIONE_E_FORNITURE_ULTERIORI_RIGHE_DA_FORMATTARE;

      if (isGaraLottiConOffertaUnica) {
        List<?> tmpLottiGara = this.sqlDao.getVectorQueryForList(
            "select NGARA from GARE where CODGAR1 = ? and (GENERE is null or GENERE <> 3) "
                + "and MODLICG in (5,6,14,16) order by NGARA asc",
            new Object[] { ngara });
        if (tmpLottiGara != null && tmpLottiGara.size() > 0) {
          for (int ef = 0; ef < tmpLottiGara.size(); ef++) {
            Vector<?> vet = (Vector<?>) tmpLottiGara.get(ef);
            listaLottiDiGara.add(((JdbcParametro) vet.get(0)).getStringValue());
          }
        }

        if (listaLottiDiGara.size() > 0) {
          Vector<?> tmpVector = this.sqlDao.getVectorQuery(
              "select count(GCAP.NGARA) from GCAP, GARE "
                  + "where GCAP.NGARA = GARE.NGARA "
                  + "and GARE.CODGAR1 = ? "
                  + "and (GARE.GENERE is null or GARE.GENERE <> 3) "
                  + "and GARE.MODLICG in (5,6,14,16) ", new Object[] { ngara });
          if (tmpVector != null && tmpVector.size() > 0) {
            Long numeroFornitureServizi = (Long) ((JdbcParametro) tmpVector.get(0)).getValue();
            if (numeroFornitureServizi != null
                && numeroFornitureServizi.longValue() > 0)
              numeroRigheDaFormattare += numeroFornitureServizi.longValue();
          }
        }
      } else {
        listaLottiDiGara.add(ngara);

        List<?> tmpListaLavorazioniForniture = this.sqlDao.getVectorQueryForList(
            selectGCAP, new Object[] { ngara });

        if (tmpListaLavorazioniForniture != null
            && tmpListaLavorazioniForniture.size() > 0)
          numeroRigheDaFormattare += tmpListaLavorazioniForniture.size();
      }

      // Se i campi CLASI1, SOLSIC e SOGRIB sono visibili, allora per ciascuna
      // colonna si crea la convalida dei dati, in modo che l'utente possa
      // scegliere tra i valori 'a corpo'/'a misura' per CLASI1 e 'si'/'no'
      // per i campi SOLSIC e SOGRIB

      if (isGaraLottiConOffertaUnica && isCodificaAutomaticaAttiva)
        numeroRigheDaFormattare = 10000;
      // Convalida dati per CODIGA
      if (arrayCampiVisibili[1]) {
      CellRangeAddressList addressList = new CellRangeAddressList(
            FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE - 1,
            FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE
                + numeroRigheDaFormattare - 1, arrayIndiceColonnaCampi[1] - 1,
            arrayIndiceColonnaCampi[1] - 1);
        DVConstraint validazioneCODIGA = DVConstraint.createNumericConstraint(
            DVConstraint.ValidationType.INTEGER, DVConstraint.OperatorType.BETWEEN, "0",
            "999");
        HSSFDataValidation dataValidation = new HSSFDataValidation(addressList,
            validazioneCODIGA);
        foglioLavorazioniForniture.addValidationData(dataValidation);
     }

      if(! new Long(98).equals(tipoFornitura)){
        // Convalida dati per CODCIG
        if (arrayCampiVisibili[2]) {
          CellRangeAddressList addressList = new CellRangeAddressList(
                FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE - 1,
                FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE
                    + numeroRigheDaFormattare - 1, arrayIndiceColonnaCampi[2] - 1,
                arrayIndiceColonnaCampi[2] - 1);
            DVConstraint validazioneCODCIG = DVConstraint.createNumericConstraint(
            DVConstraint.ValidationType.TEXT_LENGTH, DVConstraint.OperatorType.EQUAL, "10",null);
            HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, validazioneCODCIG);
            foglioLavorazioniForniture.addValidationData(dataValidation);
         }

        // Convalida dati per CLASI1
        if (arrayCampiVisibili[7]) {
          CellRangeAddressList addressList = new CellRangeAddressList(
              FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE - 1,
              FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE
                  + numeroRigheDaFormattare - 1, arrayIndiceColonnaCampi[7] - 1,
              arrayIndiceColonnaCampi[7] - 1);
          HSSFDataValidation dataValidation = new HSSFDataValidation(addressList,
              DATA_VALIDATION_CONSTRAINT_CORPO_MISURA);
          dataValidation.setSuppressDropDownArrow(false);
          foglioLavorazioniForniture.addValidationData(dataValidation);
        }

        // Convalida dati per SOLSIC
        if (arrayCampiVisibili[8]) {
          CellRangeAddressList addressList = new CellRangeAddressList(
              FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE - 1,
              FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE
                  + numeroRigheDaFormattare - 1, arrayIndiceColonnaCampi[8] - 1,
              arrayIndiceColonnaCampi[8] - 1);
          HSSFDataValidation dataValidation = new HSSFDataValidation(addressList,
              DATA_VALIDATION_CONSTRAINT_SI_NO);
          dataValidation.setSuppressDropDownArrow(false);
          foglioLavorazioniForniture.addValidationData(dataValidation);
        }

        // Convalida dati per SOGRIB
        if (arrayCampiVisibili[9]) {
          CellRangeAddressList addressList = new CellRangeAddressList(
              FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE - 1,
              FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE
                  + numeroRigheDaFormattare - 1, arrayIndiceColonnaCampi[9] - 1,
              arrayIndiceColonnaCampi[9] - 1);
          HSSFDataValidation dataValidation = new HSSFDataValidation(addressList,
              DATA_VALIDATION_CONSTRAINT_SI_NO);
          dataValidation.setSuppressDropDownArrow(false);
          foglioLavorazioniForniture.addValidationData(dataValidation);
        }
      }

      int indiceColonna = 1;
      DizionarioCampi dizCampi = DizionarioCampi.getInstance();
      Campo campo = null;
      HSSFRow riga = null;

      //Si nasconde la colonna relativa a CONTAF
      foglioLavorazioniForniture.setColumnHidden(posCampoContaf, true);

      for (int ii = 0; ii < arrayCampi.length; ii++) {
        if (arrayCampiVisibili[ii])
          foglioLavorazioniForniture.setDefaultColumnStyle(
              (short) (arrayIndiceColonnaCampi[ii] - 1),
              dizStiliExcel.getStileExcel(arrayStiliCampi[ii]));
      }
      // Il formato della colonna Importo: decimale a 5 cifre con allineamento
      // a destra
      foglioLavorazioniForniture.setDefaultColumnStyle(
          (short) (indiceColonnaImporto - 1),
          dizStiliExcel.getStileExcel(DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT));

      foglioLavorazioniForniture.setPrintGridlines(true);
      // (0.77 inc <=> 2 cm)
      foglioLavorazioniForniture.setMargin(HSSFSheet.TopMargin, 0.77);
      foglioLavorazioniForniture.setMargin(HSSFSheet.BottomMargin, 0.77);
      foglioLavorazioniForniture.setMargin(HSSFSheet.LeftMargin, 0.77);
      foglioLavorazioniForniture.setMargin(HSSFSheet.RightMargin, 0.77);

      // Set delle informazioni per l'area di stampa dei dati del foglio
      HSSFPrintSetup printSetup = foglioLavorazioniForniture.getPrintSetup();
      printSetup.setLandscape(true);
      printSetup.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);

      if (isGaraLottiConOffertaUnica && isCodificaAutomaticaAttiva)
        printSetup.setScale((short) 62); // Presumo significhi zoom al 62%
      else if (isGaraLottiConOffertaUnica && !isCodificaAutomaticaAttiva)
        printSetup.setScale((short) 65); // Presumo significhi zoom al 65%
      else
        printSetup.setScale((short) 70); // Presumo significhi zoom al 70%

      // Scrittura dell'intestazione della foglio
      for (int indiceRigaIntestazione = 0; indiceRigaIntestazione < FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE; indiceRigaIntestazione++) {
        riga = foglioLavorazioniForniture.createRow(indiceRigaIntestazione);
        HSSFCellStyle stile = null;
        for (int ii = 0; ii < arrayCampi.length; ii++) {
          if (arrayCampiVisibili[ii]) {
            switch (indiceRigaIntestazione) {
            case 0:
              // Prima riga: Nomi delle colonne
              // Gestione dei colori delle intestazioni
              switch (tipoFornitura.intValue()) {
              case 1:
                if (ii < 24) {
                  stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_INTESTAZIONE);
                } else {
                  stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_INTESTAZIONE_ARANCIONE);
                }
                break;

              case 2:
                if (ii < 20) {
                  stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_INTESTAZIONE);
                } else {
                  stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_INTESTAZIONE_ARANCIONE);
                }
                break;

              case 98:
                if (ii < 10) {
                  stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_INTESTAZIONE);
                } else {
                  stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_INTESTAZIONE_ARANCIONE);
                }
                break;

              default:
                if (stile == null)
                  stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_INTESTAZIONE);

                break;
              }

              UtilityExcel.scriviCella(foglioLavorazioniForniture,
                  arrayIndiceColonnaCampi[ii], indiceRigaIntestazione + 1,
                  arrayTitoloColonne[ii], stile);
              UtilityExcel.setLarghezzaColonna(foglioLavorazioniForniture,
                  arrayIndiceColonnaCampi[ii], arrayLarghezzaColonne[ii]);
              break;
            case FOGLIO_LAVORAZIONE_E_FORNITURE_RIGA_NOME_FISICO_CAMPI - 1:
              // Seconda riga: nomi fisici dei campi
              if (stile == null)
                stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_NASCOSTA);
              campo = dizCampi.getCampoByNomeFisico(arrayCampi[ii].substring(arrayCampi[ii].indexOf(".") + 1));
              UtilityExcel.scriviCella(foglioLavorazioniForniture,
                  arrayIndiceColonnaCampi[ii], indiceRigaIntestazione + 1,
                  SCHEMA_CAMPI.concat(".").concat(campo.getNomeFisicoCampo()),
                  stile);
              break;
            case FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE - 2:
              // Riga separatrice intestazione dai dati

              // Gestione dei colori
              switch (tipoFornitura.intValue()) {
              case 1:
                if (ii < 24) {
                  stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_SEPARATRICE);
                } else {
                  stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_SEPARATRICE_ARANCIONE);
                }
                break;

              case 2:
                if (ii < 20) {
                  stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_SEPARATRICE);
                } else {
                  stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_SEPARATRICE_ARANCIONE);
                }
                break;

              default:
                if (stile == null)
                  stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_SEPARATRICE);
                break;
              }

              UtilityExcel.scriviCella(foglioLavorazioniForniture,
                  arrayIndiceColonnaCampi[ii], indiceRigaIntestazione + 1, " ",
                  stile);
              break;
            }
          }
        }

        if (new Long(98).equals(tipoFornitura)) {
          if (indiceRigaIntestazione == 0) {
            // Set titolo della colonna importo
            UtilityExcel.scriviCella(foglioLavorazioniForniture,
                indiceColonnaImporto, indiceRigaIntestazione + 1, "Prezzo complessivo",
                stile);
            // La colonna 'Importo' ha la stessa larghezza della colonna 'Prezzo
            // unitario'
            UtilityExcel.setLarghezzaColonna(foglioLavorazioniForniture,
                indiceColonnaImporto,
                arrayLarghezzaColonne[arrayLarghezzaColonne.length - 1]);
          } else if (indiceRigaIntestazione > 0
              && indiceRigaIntestazione < FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE - 2) {
            // Nascondo la riga del foglio
            riga.setZeroHeight(true);
          } else if (indiceRigaIntestazione == FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE - 2) {
            UtilityExcel.scriviCella(foglioLavorazioniForniture,
                indiceColonnaImporto, indiceRigaIntestazione + 1, " ", stile);
            // Set altezza della riga che divide l'intestazione dai dati
            riga.setHeightInPoints(3);
          }
        }else{
          if (indiceRigaIntestazione == 0) {
            // Set titolo della colonna importo
            UtilityExcel.scriviCella(foglioLavorazioniForniture,
                indiceColonnaImporto, indiceRigaIntestazione + 1, "Importo",
                stile);
            // La colonna 'Importo' ha la stessa larghezza della colonna 'Prezzo
            // unitario'
            UtilityExcel.setLarghezzaColonna(foglioLavorazioniForniture,
                indiceColonnaImporto,
                arrayLarghezzaColonne[arrayLarghezzaColonne.length - 1]);
          } else if (indiceRigaIntestazione > 0
              && indiceRigaIntestazione < FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE - 2) {
            // Nascondo la riga del foglio
            riga.setZeroHeight(true);
          } else if (indiceRigaIntestazione == FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE - 2) {
            UtilityExcel.scriviCella(foglioLavorazioniForniture,
                indiceColonnaImporto, indiceRigaIntestazione + 1, " ", stile);
            // Set altezza della riga che divide l'intestazione dai dati
            riga.setHeightInPoints(3);
          }
        }
      }

      // I campi GCAP.QUANTI, GCAP.PREZUN e Importo sono sempre visibili,
      // quindi si va a settare il titolo della colonna e il nome fisico
      // del campo (a meno del campo Importo, perche' campo calcolato come
      // prodotto dei due campi precedenti)
      // Set titolo e nome fisico della colonna Quantita' (GCAP.QUANTI)

      // Set della formula per il calcolo dell'importo come
      // ROUND(GCAP.QUANTI, 5) * ROUND(GCAP.PREZUN, 5)

      //Nel caso di tipforn=98 i campi da considerare sono: DPRE.QUANTI, DPRE.PREOFF
      //quindi la formula per l'importo diventa:
      // ROUND(DPRE.QUANTI, 5) * ROUND(DPRE.PREOFF, 5)

      String letteraColonnaQUANTI = null;
      String letteraColonnaPREZUN = null;

      switch (tipoFornitura.intValue()) {
      case 1:
        letteraColonnaQUANTI = UtilityExcel.conversioneNumeroColonna(arrayIndiceColonnaCampi[20]);
        letteraColonnaPREZUN = UtilityExcel.conversioneNumeroColonna(arrayIndiceColonnaCampi[33]);
        break;

      case 2:
        letteraColonnaQUANTI = UtilityExcel.conversioneNumeroColonna(arrayIndiceColonnaCampi[18]);
        letteraColonnaPREZUN = UtilityExcel.conversioneNumeroColonna(arrayIndiceColonnaCampi[26]);
        break;

      case 3:
        letteraColonnaQUANTI = UtilityExcel.conversioneNumeroColonna(arrayIndiceColonnaCampi[11]);
        letteraColonnaPREZUN = UtilityExcel.conversioneNumeroColonna(arrayIndiceColonnaCampi[12]);
        break;

      case 98:
        letteraColonnaQUANTI = UtilityExcel.conversioneNumeroColonna(arrayIndiceColonnaCampi[14]);
        letteraColonnaPREZUN = UtilityExcel.conversioneNumeroColonna(arrayIndiceColonnaCampi[15]);
        break;

      default:
        break;
      }

      if (letteraColonnaQUANTI != null && letteraColonnaPREZUN != null) {
        for (int ig = FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE; ig <= (FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE + numeroRigheDaFormattare); ig++) {
          String formula = "ROUND(" + letteraColonnaQUANTI + ig + ", 3) * ROUND("
              + letteraColonnaPREZUN + ig + ", 5)";
          // String formula = letteraColonnaQUANTI + ig + " * " +
          // letteraColonnaPREZUN + ig;
          UtilityExcel.scriviFormulaCella(foglioLavorazioniForniture,
              indiceColonnaImporto, ig, formula, null);
        }
      }

      // Scrittura delle informazioni principali della gara nelle prime righe
      // del foglio delle lavorazioni e forniture. Tali dati verranno letti e
      // verificati in fase di import. Fra le informazioni principale della gara
      // ci sono: TORN.CODGAR o GARE.CODGAR1, GARE.NGARA, GARE.MODLICG,
      // V_GARE_TORN.ISLOTTI, V_GARE_TORN.ISGENERE e export prodotto applicativo
      // configurato con la codifica automatica
      this.setDatiPrincipaliGara(ngara, foglioLavorazioniForniture,
          isCodificaAutomaticaAttiva, dizStiliExcel);

      int indiceRiga = FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE;
      for (int ef = 0; ef < listaLottiDiGara.size(); ef++) {
        String tmpNgara = listaLottiDiGara.get(ef);

        List<?> listaLavorazioniForniture = this.sqlDao.getVectorQueryForList(
            selectGCAP, new Object[] { tmpNgara });

        if (listaLavorazioniForniture != null
            && listaLavorazioniForniture.size() > 0) {
          Map<String,String> mappaTabelleatoA1051 = this.getMappaTabellatoA1051();

          // Reset dell'indice di colonna
          indiceColonna = 1;
          for (int i = 0; i < listaLavorazioniForniture.size(); i++) {
            Vector<?> record = (Vector<?>) listaLavorazioniForniture.get(i);
            if (record != null && record.size() > 0) {

              int posCampiRecord=0;
              int posizioneCampiExcel=0;

              // Campo NGARA (campo visibile se gara a lotti con offerta unica)
              if (isGaraLottiConOffertaUnica && arrayCampiVisibili[0]) {
                UtilityExcel.scriviCella(foglioLavorazioniForniture,
                    indiceColonna++, indiceRiga + i, tmpNgara, null);
              }

              // Campo CODIGA (campo visibile se gara a lotti con offerta unica
              // e con codifica automatica)
              if (isGaraLottiConOffertaUnica && isCodificaAutomaticaAttiva) {
                Vector<?> tmpDatiGara = this.sqlDao.getVectorQuery(
                    "select CODIGA,CODCIG from GARE where NGARA = ? ",
                    new Object[] { tmpNgara });
                if (tmpDatiGara != null && tmpDatiGara.size() > 0) {
                  String codiga = ((JdbcParametro) tmpDatiGara.get(0)).getStringValue();
                  UtilityExcel.scriviCella(foglioLavorazioniForniture,
                      indiceColonna++, indiceRiga + i, codiga, null);
                  String codcig = ((JdbcParametro) tmpDatiGara.get(1)).getStringValue();
                  UtilityExcel.scriviCella(foglioLavorazioniForniture,
                      indiceColonna++, indiceRiga + i, codcig, null);
                } else
                  indiceColonna = indiceColonna + 2;
              }

              // Campo NORVOC (campo visibile se gara a lotti con offerta unica
              // e se visibile da profilo)
              /*
               * if(isGaraLottiConOffertaUnica && arrayCampiVisibili[2]){ Double
               * norvoc = SqlManager.getValueFromVectorParam( record,
               * 10).doubleValue(); if (norvoc != null)
               * UtilityExcel.scriviCella(foglioLavorazioniForniture,
               * indiceColonna++, indiceRiga + i, norvoc, null); else
               * indiceColonna++; }
               */

              if(new Long(98).equals(tipoFornitura) && arrayCampiVisibili[2]){
                // Campo NORVOC
                Double norvoc = SqlManager.getValueFromVectorParam( record, 7).doubleValue();
                if (norvoc != null)
                   UtilityExcel.scriviCella(foglioLavorazioniForniture,indiceColonna++, indiceRiga + i, norvoc, null);
                else
                   indiceColonna++;
              }

              // Campo CODVOC
              posizioneCampiExcel = 3;
              if(new Long(98).equals(tipoFornitura))
                posizioneCampiExcel = 4;

              if (arrayCampiVisibili[posizioneCampiExcel]) {
                String codvoc = SqlManager.getValueFromVectorParam(
                    record, 0).toString();
                if (codvoc != null && codvoc.length() > 0)
                  UtilityExcel.scriviCella(foglioLavorazioniForniture,
                      indiceColonna++, indiceRiga + i, codvoc, null);
                else
                  indiceColonna++;
              }

              // Campo VOCE
              posCampiRecord=1;
              posizioneCampiExcel = 4;
              if(new Long(98).equals(tipoFornitura)){
                posizioneCampiExcel = 5;
                posCampiRecord=2;
              }

              if (arrayCampiVisibili[posizioneCampiExcel]) {
                String voce = SqlManager.getValueFromVectorParam(
                    record, posCampiRecord).toString();
                if (voce != null && voce.length() > 0)
                  UtilityExcel.scriviCella(foglioLavorazioniForniture,
                      indiceColonna++, indiceRiga + i, voce, null);
                else
                  indiceColonna++;
              }

              // Campo DESEST
              posCampiRecord=9;
              posizioneCampiExcel = 5;
              if(new Long(98).equals(tipoFornitura)){
                posizioneCampiExcel = 6;
                posCampiRecord=6;
              }

              Long contaf = SqlManager.getValueFromVectorParam(record, posCampiRecord).longValue();
              if (arrayCampiVisibili[posizioneCampiExcel]) {

                String desest = null;

                if("AVM".equals(tipoWSERP)){
                    String queryGCAP = "select gc.ngara,gc.contaf,ge.desest,gc.datacons" +
                    " from gcap gc,gcap_est ge" +
                    " where gc.ngara=ge.ngara and gc.contaf=ge.contaf" +
                    " and gc.codrda is not null and gc.posrda is not null and gc.datacons is not null" +
                    " and gc.ngara = ? and gc.contaf = ?";
                    Vector<?> res = this.sqlDao.getVectorQuery(queryGCAP, new Object[] {tmpNgara, contaf});
                    if (res != null && res.size() > 0) {
                      JdbcParametro result2 = (JdbcParametro) res.get(2);
                      desest = (String) result2.getValue();
                      JdbcParametro result3 = (JdbcParametro) res.get(3);
                      Date dataCons = (Date) result3.getValue();
                      if(dataCons != null){
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        String formattedDataCons = formatter.format(dataCons);
                        desest = UtilityStringhe.convertiNullInStringaVuota(desest);
                        desest = desest + "\r\n Data consegna " + formattedDataCons;
                      }
                    }
                }else{
                  Vector<?> res = this.sqlDao.getVectorQuery(
                      "select DESEST from GCAP_EST where NGARA = ? and CONTAF = ? ",
                      new Object[] { tmpNgara, contaf });
                  if (res != null && res.size() > 0) {
                    JdbcParametro result = (JdbcParametro) res.get(0);
                    if (result != null && result.getValue() != null)
                      desest = (String) result.getValue();
                  }
                }//if AVM

                if (desest != null && desest.length() > 0) {
                  if (desest.length() > 32766)
                    desest = desest.substring(0, 32759).concat(" [...]");
                  UtilityExcel.scriviCella(foglioLavorazioniForniture,
                      indiceColonna++, indiceRiga + i, desest, null);
                } else
                  indiceColonna++;
              }

              if(!new Long(98).equals(tipoFornitura)){
                // Campo CODCAT
                if (arrayCampiVisibili[6]) {
                  String codcat = SqlManager.getValueFromVectorParam(
                      record, 11).toString();
                  if (codcat != null && codcat.length() > 0)
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i, codcat, null);
                  else
                    indiceColonna++;
                }

                // Campo CLASI1
                if (arrayCampiVisibili[7]) {
                  Long clasi1 = SqlManager.getValueFromVectorParam(record, 2).longValue();
                  if (clasi1 != null)
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++,  indiceRiga + i,
                        (mappaTabelleatoA1051.get(clasi1.toString())).toLowerCase(),
                        null);
                  else
                    indiceColonna++;
                }

                // Campo SOLSIC
                String solsic = null;
                if (arrayCampiVisibili[8]) {
                  solsic = SqlManager.getValueFromVectorParam(record, 3).toString();
                  if (solsic != null && solsic.length() > 0)
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i,
                        solsic.equals("1") ? "si" : "no", null);
                  else
                    indiceColonna++;
                }

                // Campo SOGRIB
                String sogrib = null;
                if (arrayCampiVisibili[9]) {
                  sogrib = SqlManager.getValueFromVectorParam(record, 4).toString();
                  if (sogrib != null && sogrib.length() > 0) {
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i,
                        sogrib.equals("1") ? "no" : "si", null);
                  } else
                    indiceColonna++;
                }

                // Campo DESUNI
                if (arrayCampiVisibili[10]) {
                  String desuni = SqlManager.getValueFromVectorParam(
                      record, 5).toString();
                  if (desuni != null && desuni.length() > 0)
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i, desuni, null);
                  else
                    indiceColonna++;
                }

                // Campo QUANTI
                if (arrayCampiVisibili[11]) {
                  Double quanti = SqlManager.getValueFromVectorParam(
                      record, 6).doubleValue();
                  if (quanti != null)
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i, quanti, null);
                  else
                    indiceColonna++;
                }

                if (arrayCampiVisibili[12]) {
                  if (exportPrezziUnitari || "1".equals(solsic) || "1".equals(sogrib)) {
                    // Campo PREZUN
                    Double prezun = SqlManager.getValueFromVectorParam(
                        record, 7).doubleValue();
                    if (prezun != null)
                      UtilityExcel.scriviCella(foglioLavorazioniForniture,
                          indiceColonna++, indiceRiga + i, prezun, null);
                    else
                      indiceColonna++;
                  } else
                    indiceColonna ++;
                }

                if (new Long(2).equals(tipoFornitura) || new Long(3).equals(tipoFornitura)) {
                  // Campo PERCIVA
                  if (arrayCampiVisibili[13]) {
                    Long perciva = SqlManager.getValueFromVectorParam(record, 12).longValue();
                    if (perciva != null)
                      UtilityExcel.scriviCella(foglioLavorazioniForniture,
                          indiceColonna++, indiceRiga + i, perciva, null);
                    else
                      indiceColonna++;
                  }
                }
                if(new Long(3).equals(tipoFornitura)){
                  //Campo PESO
                  if (arrayCampiVisibili[14]) {
                    Double peso = SqlManager.getValueFromVectorParam(record, 13).doubleValue();
                    if (peso != null)
                      UtilityExcel.scriviCella(foglioLavorazioniForniture,
                          indiceColonna++, indiceRiga + i, peso, null);
                    else
                      indiceColonna++;
                  }
                }
              }
              // Ulteriori campi per modalità specifiche per ARSS.
              // I campi sono fissi
              if (new Long(1).equals(tipoFornitura)) {
                // GCAP_SAN.CODATC
                if (arrayCampiVisibili[13]) {
                  String codatc = SqlManager.getValueFromVectorParam(
                      record, 11).toString();
                  if (codatc != null && codatc.length() > 0)
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i, codatc, null);
                  else
                    indiceColonna++;
                }

                // GCAP_SAN.CODAUR
                if (arrayCampiVisibili[14]) {
                  String codaur = SqlManager.getValueFromVectorParam(
                      record, 12).toString();
                  if (codaur != null && codaur.length() > 0)
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i, codaur, null);
                  else
                    indiceColonna++;
                }

                // GCAP_SAN.PRINCATT
                if (arrayCampiVisibili[15]) {
                  String princatt = SqlManager.getValueFromVectorParam(
                      record, 13).toString();
                  if (princatt != null && princatt.length() > 0)
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i, princatt, null);
                  else
                    indiceColonna++;
                }

                // GCAP_SAN.FORMAFARM
                if (arrayCampiVisibili[16]) {
                  String formafarm = SqlManager.getValueFromVectorParam(
                      record, 14).toString();
                  if (formafarm != null && formafarm.length() > 0)
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i, formafarm, null);
                  else
                    indiceColonna++;
                }

                // GCAP_SAN.DOSAGGIO
                if (arrayCampiVisibili[17]) {
                  String dosaggio = SqlManager.getValueFromVectorParam(
                      record, 15).toString();
                  if (dosaggio != null && dosaggio.length() > 0)
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i, dosaggio, null);
                  else
                    indiceColonna++;
                }

                // GCAP_SAN.VIASOMM
                if (arrayCampiVisibili[18]) {
                  String viasomm = SqlManager.getValueFromVectorParam(
                      record, 16).toString();
                  if (viasomm != null && viasomm.length() > 0)
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i, viasomm, null);
                  else
                    indiceColonna++;
                }
              }

              if (new Long(1).equals(tipoFornitura) || new Long(98).equals(tipoFornitura)) {
                // GCAP.UNIMIS
                posizioneCampiExcel = 19;
                posCampiRecord=5;
                if(new Long(98).equals(tipoFornitura)){
                  posizioneCampiExcel = 6;
                  posCampiRecord=3;
                }

                if (arrayCampiVisibili[posizioneCampiExcel]) {
                  String desuni = SqlManager.getValueFromVectorParam(
                      record, posCampiRecord).toString();
                  if (desuni != null && desuni.length() > 0){
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i, desuni, null);
                  }else
                    indiceColonna++;
                }


                // GCAP.QUANTI
                posizioneCampiExcel = 20;
                posCampiRecord=6;
                if(new Long(98).equals(tipoFornitura)){
                  posizioneCampiExcel = 7;
                  posCampiRecord=4;
                }

                if (arrayCampiVisibili[posizioneCampiExcel]) {
                  Double quanti = SqlManager.getValueFromVectorParam(
                      record, posCampiRecord).doubleValue();
                  if (quanti != null)
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i, quanti, null);
                  else
                    indiceColonna++;
                }
              }

              if (new Long(1).equals(tipoFornitura)) {
                // GCAP.PREZUN
                if (arrayCampiVisibili[21]) {
                  if (exportPrezziUnitari) {
                    // Campo PREZUN (e' sempre visibile nell'export)
                    Double prezun = SqlManager.getValueFromVectorParam(
                        record, 7).doubleValue();
                    if (prezun != null)
                      UtilityExcel.scriviCella(foglioLavorazioniForniture,
                          indiceColonna++, indiceRiga + i, prezun, null);
                    else
                      indiceColonna++;
                  } else
                    indiceColonna ++;
                }

                // GCAP.PERCIVA
                if (arrayCampiVisibili[22]) {
                  Long perciva = SqlManager.getValueFromVectorParam(record, 12).longValue();
                  if (perciva != null)
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i, perciva, null);
                  else
                    indiceColonna++;
                }

                // GCAP_SAN.NOTE
                if (arrayCampiVisibili[23]) {
                  String note = SqlManager.getValueFromVectorParam(
                      record, 17).toString();
                  if (note != null && note.length() > 0)
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i, note, null);
                  else
                    indiceColonna++;
                }
              }

              if (new Long(2).equals(tipoFornitura)) {
                // GCAP_SAN.CODCLASS
                if (arrayCampiVisibili[14]) {
                  String codclass = SqlManager.getValueFromVectorParam(
                      record, 11).toString();
                  if (codclass != null && codclass.length() > 0)
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i, codclass, null);
                  else
                    indiceColonna++;
                }

                // GCAP_SAN.DEPRODCN
                if (arrayCampiVisibili[15]) {
                  String deprodcn = SqlManager.getValueFromVectorParam(
                      record, 12).toString();
                  if (deprodcn != null && deprodcn.length() > 0)
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i, deprodcn, null);
                  else
                    indiceColonna++;
                }

                // GCAP_SAN.CODAUR
                if (arrayCampiVisibili[16]) {
                  String codaur = SqlManager.getValueFromVectorParam(
                      record, 13).toString();
                  if (codaur != null && codaur.length() > 0)
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i, codaur, null);
                  else
                    indiceColonna++;
                }

                // GCAP_SAN.DPRODCAP
                if (arrayCampiVisibili[17]) {
                  String dprodcap = SqlManager.getValueFromVectorParam(
                      record, 14).toString();
                  if (dprodcap != null && dprodcap.length() > 0)
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i, dprodcap, null);
                  else
                    indiceColonna++;
                }

                // GCAP.QUANTI
                if (arrayCampiVisibili[18]) {
                  Double quanti = SqlManager.getValueFromVectorParam(
                      record, 6).doubleValue();
                  if (quanti != null)
                    UtilityExcel.scriviCella(foglioLavorazioniForniture,
                        indiceColonna++, indiceRiga + i, quanti, null);
                  else
                    indiceColonna++;
                }

                // GCAP.PREZUN
                if (arrayCampiVisibili[19]) {
                  if (exportPrezziUnitari) {
                    Double prezun = SqlManager.getValueFromVectorParam(
                        record, 7).doubleValue();
                    if (prezun != null)
                      UtilityExcel.scriviCella(foglioLavorazioniForniture,
                          indiceColonna++, indiceRiga + i, prezun, null);
                    else
                      indiceColonna++;
                  } else
                    indiceColonna += 2;
                }
              }

              //GCAP.CONTAF
              UtilityExcel.scriviCella(foglioLavorazioniForniture,
                  posCampoContaf +1, indiceRiga + i, contaf, null);
            }

            if (new Long(98).equals(tipoFornitura)) {
              // GCAP.IVAPROD
              if (arrayCampiVisibili[8]) {
                Long ivaprod = SqlManager.getValueFromVectorParam(
                    record, 8).longValue();
                if (ivaprod != null){
                  UtilityExcel.scriviCella(foglioLavorazioniForniture,
                      indiceColonna++, indiceRiga + i, ivaprod, null);
                } else
                  indiceColonna++;
              }

              //GCAP.NUNICONF
              if (arrayCampiVisibili[9]) {
                Double nuniconf = SqlManager.getValueFromVectorParam(
                    record, 1).doubleValue();
                if (nuniconf != null)
                  UtilityExcel.scriviCella(foglioLavorazioniForniture,
                      indiceColonna++, indiceRiga + i, nuniconf, null);
                else
                  indiceColonna++;
              }
            }



            // Reset dell'indice di colonna
            indiceColonna = 1;
          }
          indiceRiga += listaLavorazioniForniture.size();
        }
      }
    } catch (SQLException s) {
      logger.error("Export lavorazioni e forniture della gara '" + ngara
          + "': errore durante l'export su Excel dello sheet 'Lavorazioni "
          + "e forniture'");
      throw s;
    } catch (GestoreException g) {
      logger.error("Export lavorazioni e forniture della gara '" + ngara
          + "': errore durante l'export su Excel dello sheet 'Lavorazioni "
          + "e forniture'");
      throw g;
    }
    /*
     * Si e' deciso di tracciare su log il punto esatto in cui si e' verificato
     * l'errore, catturando l'eccezione, loggando una stringa di errore e
     * riemettendo l'eccezione stessa, visto che nella procedura di export su
     * foglio excel, lo stesso tipo di eccezione può essere emessa in punti
     * diversi.
     */
  }

  /**
   * Scrittura nel foglio dei punteggi economici delle informazioni principali
   * della gara per usarle all'inizio dell'operazione di import per verificare
   * se tali informazioni sono uguali a quelle della gara in cui si sta
   * importando
   *
   * @param ngara
   * @param foglioDittePunteggiEconomici
   * @throws GestoreException
   */
  private void setDatiPrincipaliGara(String ngara,
      HSSFSheet foglioLavorazioniForniture, boolean isCodificaAutomaticaAttiva,
      DizionarioStiliExcel dizStiliExcel) throws GestoreException {

    HSSFRow riga5 = foglioLavorazioniForniture.createRow(4);
    if (riga5 != null) {
      // Valori dei campi da databse
      String codiceGara = null;
      String modlicg = null;
      String isLotti = null;
      String isGenere = null;

      // Lettura dei campi da database
      try {
        Vector<?> datiGARE = this.sqlDao.getVectorQuery(
            "select CODGAR, MODLIC from TORN, GARE "
                + "where TORN.CODGAR = GARE.CODGAR1 and GARE.NGARA = ? ",
            new Object[] { ngara });

        if (datiGARE != null && datiGARE.size() > 0) {
          codiceGara = SqlManager.getValueFromVectorParam(datiGARE, 0).toString();
          modlicg = SqlManager.getValueFromVectorParam(datiGARE, 1).toString();
        }

        Vector<?> datiV_GARE_TORN = this.sqlDao.getVectorQuery(
            "select ISLOTTI, GENERE from V_GARE_TORN where codgar = ? ",
            new Object[] { codiceGara });

        if (datiV_GARE_TORN != null && datiV_GARE_TORN.size() > 0) {
          isLotti = SqlManager.getValueFromVectorParam(datiV_GARE_TORN, 0).toString();
          isGenere = SqlManager.getValueFromVectorParam(datiV_GARE_TORN, 1).toString();
        }

        // Scrittura valore TORN.CODGAR o GARE.CODGAR1 sul file Excel
        UtilityExcel.scriviCella(foglioLavorazioniForniture, 1, 5, codiceGara,
            dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_NASCOSTA));
        // Scrittura valore GARE.NGARA sul file Excel
        UtilityExcel.scriviCella(foglioLavorazioniForniture, 2, 5, ngara,
            dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_NASCOSTA));
        // Lettura valore GARE.MODLICG sul file Excel
        UtilityExcel.scriviCella(foglioLavorazioniForniture, 3, 5, modlicg,
            dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_NASCOSTA));
        // Lettura valore V_GARE_TORN.ISLOTTI sul file Excel
        UtilityExcel.scriviCella(foglioLavorazioniForniture, 4, 5, isLotti,
            dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_NASCOSTA));
        // Lettura valore V_GARE_TORN.ISGENERE sul file Excel
        UtilityExcel.scriviCella(foglioLavorazioniForniture, 5, 5, isGenere,
            dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_NASCOSTA));
        if (isCodificaAutomaticaAttiva)
          UtilityExcel.scriviCella(foglioLavorazioniForniture, 6, 5, "1",
              dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_NASCOSTA));
        else
          UtilityExcel.scriviCella(foglioLavorazioniForniture, 6, 5, "2",
              dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_NASCOSTA));
       //Nuova gestione del campo Soggetto a ribasso
        UtilityExcel.scriviCella(foglioLavorazioniForniture, 7, 5, "1",
            dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_NASCOSTA));
      } catch (SQLException s) {
        throw new GestoreException("Errore durante la scrittura dei dati "
                + "principali della gara da usare in fase di import per la "
                + "verifica preliminare dei dati",
            "importExportOffertaPrezzi.lavorazioniForniture", s);
      }
      riga5.setZeroHeight(true);
    }
  }

  /**
   * Verifica preliminare delle informazioni generali della gara: prima di
   * importare i dati dell'offerta prezzi nella gara, si verificano i valori dei
   * campi TORN.CODGAR (o GARE.CODGAR1), GARE.NGARA, GARE.MODLICG,
   * V_GARE_TORN.ISLOTTI e V_GARE_TORN.ISGENERE che in fase di esportazione
   * erano stati salvati nella riga 5 del foglio 'Lavorazioni e forniture'
   * Ritorna true se le informazioni generali della gara in cui si sta
   * importando coincidono con quelle presenti nel foglio
   *
   * @param ngara
   * @param foglioLavorazioniForniture
   * @param isCodificaAutomaticaAttiva
   * @param eseguireControlloCodificaAutomatica
   * @return Ritorna true se da
   * @throws GestoreException
   */
  private boolean verifichePreliminari(String ngara,
      HSSFSheet foglioLavorazioniForniture, boolean isCodificaAutomaticaAttiva)
      throws GestoreException {
    boolean result = true;

    if (logger.isDebugEnabled())
      logger.debug("verifichePreliminari: inizio metodo");

    HSSFRow riga5 = foglioLavorazioniForniture.getRow(4);
    if (riga5 != null && riga5.getPhysicalNumberOfCells() > 0) {
      // Valori dei campi da databse
      String codiceGara = null;
      String modlicg = null;
      String isLotti = null;
      String isGenere = null;
      // Valori dei campi dal foglio dei punteggi economici del file Excel
      String codiceGaraXLS, numeroLottoXLS, modlicgXLS, isLottiXLS, isGenereXLS = null;
      boolean isCodificaAutomaticaXLS = false;

      // Lettura dei campi da database
      try {
        Vector<?> datiGARE = this.sqlDao.getVectorQuery(
            "select CODGAR, MODLIC from TORN, GARE "
                + "where TORN.CODGAR = GARE.CODGAR1 and GARE.NGARA = ? ",
            new Object[] { ngara });

        boolean continuaConfronto = true;
        if (datiGARE == null || (datiGARE != null && datiGARE.size() == 0)) {
          result = false;
          continuaConfronto = false;
        } else {
          codiceGara = SqlManager.getValueFromVectorParam(datiGARE, 0).toString();
          modlicg = SqlManager.getValueFromVectorParam(datiGARE, 1).toString();
        }

        Vector<?> datiV_GARE_TORN = this.sqlDao.getVectorQuery(
            "select ISLOTTI, GENERE from V_GARE_TORN where codgar = ? ",
            new Object[] { codiceGara });

        if (continuaConfronto
            && (datiV_GARE_TORN == null || (datiV_GARE_TORN != null && datiV_GARE_TORN.size() == 0))) {
          result = false;
          continuaConfronto = false;
        } else {
          isLotti = SqlManager.getValueFromVectorParam(datiV_GARE_TORN, 0).toString();
          isGenere = SqlManager.getValueFromVectorParam(datiV_GARE_TORN, 1).toString();
        }

        if (continuaConfronto) {
          // Lettura valore TORN.CODGAR o GARE.CODGAR1 dal file Excel
          codiceGaraXLS = UtilityExcel.leggiCellaString(
              foglioLavorazioniForniture, 1, 5);
          // Lettura valore GARE.NGARA dal file Excel
          numeroLottoXLS = UtilityExcel.leggiCellaString(
              foglioLavorazioniForniture, 2, 5);
          // Lettura valore GARE.MODLICG dal file Excel
          modlicgXLS = UtilityExcel.leggiCellaString(
              foglioLavorazioniForniture, 3, 5);
          // Lettura valore V_GARE_TORN.ISLOTTI dal file Excel
          isLottiXLS = UtilityExcel.leggiCellaString(
              foglioLavorazioniForniture, 4, 5);
          // Lettura valore V_GARE_TORN.ISGENERE dal file Excel
          isGenereXLS = UtilityExcel.leggiCellaString(
              foglioLavorazioniForniture, 5, 5);
          String tmp = UtilityExcel.leggiCellaString(
              foglioLavorazioniForniture, 6, 5);
          if ("1".equals(tmp)) isCodificaAutomaticaXLS = true;

          if (modlicg == null)
            modlicg = "";

          if (modlicgXLS == null)
            modlicgXLS = "";

          if (!codiceGara.equals(codiceGaraXLS)) {
            result = false;
            logger.error("Verifiche premilinari import offerta prezzi: avviato "
                + "import da file Excel nella gara '"
                + codiceGara.replaceFirst("$", "")
                + "', che e' diversa da quella da cui era stato esportato "
                + "(Il file Excel era stato esportato dalla gara '"
                + codiceGaraXLS.replaceFirst("$", "")
                + "')");
          } else if (!ngara.equals(numeroLottoXLS)) {
            result = false;
            logger.error("Verifiche premilinari import offerta prezzi: avviato "
                + "import da file Excel dal lotto '"
                + numeroLottoXLS
                + "', che e' diversa da quello da cui era stato esportato (Il "
                + "file Excel era stato esportato dal lotto '"
                + ngara
                + "' della gara '"
                + codiceGara.replaceFirst("$", "")
                + "')");
          } else if (!modlicg.equals(modlicgXLS)) {
            result = false;
            logger.error("Verifiche premilinari import offerta prezzi: avviato "
                + "import da file Excel nella gara '"
                + codiceGara.replaceFirst("$", "")
                + "' con Criterio di aggiudicazione diverso da 'Offerta "
                + "economicamente più vantaggiosa' (GARE.MODLICG != 6)");
          } else if (!isLotti.equals(isLottiXLS)) {
            result = false;
            if ("1".equals(isLotti)) {
              logger.error("Verifiche premilinari import offerta prezzi: "
                  + "avviato import da file Excel generato dalla gara divisa in "
                  + "lotti '"
                  + codiceGaraXLS.replaceFirst("$", "")
                  + "'");
            } else {
              logger.error("Verifiche premilinari import offerta prezzi: "
                  + "avviato import da file Excel generato dalla gara a lotto "
                  + "unico '"
                  + codiceGaraXLS.replaceFirst("$", "")
                  + "'");
            }
          } else if (!isGenere.equals(isGenereXLS)) {
            result = false;
            if ("2".equals("isGenere")) {
              logger.error("Verifiche premilinari import offerta prezzi: "
                  + "avviato import da file Excel generato dalla gara a lotti ad "
                  + "offerte distinte '"
                  + codiceGaraXLS.replaceFirst("$", "")
                  + "'");
            } else {
              logger.error("Verifiche premilinari import offerta prezzi: "
                  + "avviato import da file Excel generato dalla gara a lotti "
                  + "con offerta unica '"
                  + codiceGaraXLS.replaceFirst("$", "")
                  + "'");
            }
          } else if (isCodificaAutomaticaAttiva != isCodificaAutomaticaXLS) {
            result = false;
            if (isCodificaAutomaticaXLS) {
              logger.error("Verifiche premilinari import offerta prezzi: "
                  + "avviato import da file Excel generato da applicativo con "
                  + "codifica automatica attiva");
            } else {
              logger.error("Verifiche premilinari import offerta prezzi: "
                  + "avviato import da file Excel generato da applicativo con "
                  + "codifica automatica non attiva");
            }
          }
        }
      } catch (SQLException s) {
        throw new GestoreException(
            "Errore durante la verifica preliminare "
                + "all'operazione di import offerta prezzi: controllo che i dati "
                + "della gara da cui e' stato prodotto il file Excel sia la stessa "
                + "da cui si sta importando",
            "errors.exportOffertaPrezzi.sqlErrori", s);
      }
    } else {
      result = false;
      logger.error("Verifiche premilinari import offerta prezzi: avviato import "
          + "da file Excel privo delle informazioni che permettono di effettuare "
          + "dei controlli primo di avviare l'operazione di import dei dati "
          + "(Probabilmente il file excel non e' stato generato della funzione "
          + "di export).");
    }

    if (logger.isDebugEnabled())
      logger.debug("verifichePreliminari: fine metodo");

    return result;
  }

  /**
   * Ritorna il valore del gare TORN.TIPGEN
   *
   * @param ngara
   * @param tipoGara
   * @return
   * @throws SQLException
   */
  private Long getTipoGara(String ngara) throws SQLException {
    Long tipoGara = null;
    Vector<?> selectTORN_GARE = this.sqlDao.getVectorQuery(
        "select TIPGEN from TORN, GARE "
            + "where TORN.CODGAR = GARE.CODGAR1 "
            + "and GARE.NGARA = ?", new Object[] { ngara });
    if (selectTORN_GARE != null && selectTORN_GARE.size() > 0) {
      JdbcParametro result = (JdbcParametro) selectTORN_GARE.get(0);
      if (result != null && result.getValue() != null)
        tipoGara = (Long) result.getValue();
    }
    return tipoGara;
  }

  private Map<String,String> getMappaTabellatoA1051() {
    HashMap<String,String> hm = new HashMap<String,String>();
    List<Tabellato> listaTabellato = this.tabellatiManager.getTabellato("A1051");
    for (int i = 0; i < listaTabellato.size(); i++) {
      Tabellato val = listaTabellato.get(i);
      hm.put(val.getTipoTabellato(), val.getDescTabellato());
    }
    return hm;
  }

  /**
   * Lettura della riga del foglio 'Lista lavorazioni e forniture' che in fase
   * di export e' stata valorizzata con i nomi fisici dei campi esportati
   *
   * @param foglio
   * @param nomeFoglio
   * @return Ritorna la lista dei campi da leggere nel foglio 'Lista lavorazioni
   *         e forniture', sottoforma di lista di oggetti Campo. La lista e'
   *         vuota se nella riga non si trova nessun nome fisico
   */
  private Map<String,List<?>> getListaCampiDaImportare(HSSFSheet foglio, String nomeFoglio,
      String entita) {
    if (logger.isDebugEnabled())
      logger.debug("getListaCampiDaImportare: inizio metodo");

    DizionarioCampi dizCampi = DizionarioCampi.getInstance();
    HSSFRow riga = foglio.getRow(FOGLIO_LAVORAZIONE_E_FORNITURE_RIGA_NOME_FISICO_CAMPI - 1);
    Map<String,List<?>> mappa = new HashMap<String,List<?>>();
    List<String> listaNomiFisiciCampiDaImportare = new ArrayList<String>();
    List<Long> listaIndiceColonnaCampiDaImportare = new ArrayList<Long>();
    List<Long> listaIndiceArrayValoreCampiDaImportare = new ArrayList<Long>();
    List<String> listaErrori = new ArrayList<String>();
    if (riga != null && riga.getPhysicalNumberOfCells() > 0) {
      if (logger.isDebugEnabled())
        logger.debug("getListaCampiDaImportare: inizio lettura della riga 3 del foglio");

      // Numero massimo di celle inizializzate nella riga in lettura
      int maxColNumber = riga.getLastCellNum();

      // Contatore del numero di celle lette dalla riga (cioe' celle valorizzate
      // con una stringa che rappresenta uno dei nomi fisici dei campi da
      // importare (contenuti nella variabile arrayCampi))
      int numeroCelleLette = 0;

      Collection<String> collezioneNomeFisiciCampi = new ArrayList<String>();

      if (entita != null) {
        if ("GCAP".equals(entita)) {
          for (int i = 0; i < arrayCampi.length; i++) {
            if (arrayCampi[i].indexOf(".GCAP.") > 0
                || arrayCampi[i].indexOf(".GCAP_EST.") > 0
                || arrayCampi[i].indexOf(".GARE.") > 0) {
              collezioneNomeFisiciCampi.add(arrayCampi[i]);
            }
          }
        }

        if ("GCAP_SAN".equals(entita)) {
          for (int i = 0; i < arrayCampi.length; i++) {
            if (arrayCampi[i].indexOf(".GCAP_SAN.") > 0) {
              collezioneNomeFisiciCampi.add(arrayCampi[i]);
            }
          }
        }

        if ("DPRE".equals(entita)) {
          for (int i = 0; i < arrayCampi.length; i++) {
            if (arrayCampi[i].indexOf(".DPRE.") > 0) {
              collezioneNomeFisiciCampi.add(arrayCampi[i]);
            }
          }
        }

        if ("DPRE_SAN".equals(entita)) {
          for (int i = 0; i < arrayCampi.length; i++) {
            if (arrayCampi[i].indexOf(".DPRE_SAN.") > 0) {
              collezioneNomeFisiciCampi.add(arrayCampi[i]);
            }
          }
        }

      } else {
        for (int i = 0; i < arrayCampi.length; i++) {
          collezioneNomeFisiciCampi.add(arrayCampi[i]);
        }
      }

      for (int i = 0; i < maxColNumber; i++) {
        HSSFCell cella = riga.getCell(i);
        String carattereColonna = UtilityExcel.conversioneNumeroColonna(i + 1);
        if (cella != null) {
          if (logger.isDebugEnabled())
            logger.debug("getListaCampiDaImportare: inizio lettura della "
                + "cella "
                + carattereColonna
                + (riga.getRowNum() + 1));

          if (cella.getCellType() == HSSFCell.CELL_TYPE_STRING) {
            HSSFRichTextString richTextString = cella.getRichStringCellValue();
            if (richTextString != null) {
              String valoreCella = richTextString.getString();
              if (valoreCella != null && valoreCella.trim().length() > 0) {
                valoreCella = valoreCella.trim();
                if (collezioneNomeFisiciCampi.contains(valoreCella)) {
                  numeroCelleLette++;
                  Campo campo = dizCampi.getCampoByNomeFisico(valoreCella.substring(valoreCella.indexOf(".") + 1));
                  if (campo != null) {
                    listaNomiFisiciCampiDaImportare.add(valoreCella.toUpperCase());
                    listaIndiceColonnaCampiDaImportare.add(new Long(i + 1));
                    listaIndiceArrayValoreCampiDaImportare.add(new Long(
                        numeroCelleLette - 1));
                  } else
                    listaErrori.add("Lettura campi da importare: il valore "
                        + "della cella "
                        + carattereColonna
                        + (riga.getRowNum() + 1)
                        + " non corrisponde ad alcun campo presente nei metadati."
                        + " Valore della "
                        + "cella: "
                        + valoreCella);
                } else {
                  if (logger.isDebugEnabled())
                    logger.debug("Lettura campi da importare: il valore "
                        + "della cella "
                        + carattereColonna
                        + (riga.getRowNum() + 1)
                        + " non corrisponde ad campo importabile. Valore della "
                        + "cella: "
                        + valoreCella);
                }
              } else {
                if (logger.isDebugEnabled())
                  logger.debug("Lettura campi da importare: il valore della "
                      + "cella "
                      + carattereColonna
                      + (riga.getRowNum() + 1)
                      + " e' null oppure e' una stringa di lunghezza zero (Caso 1)");
              }
            } else {
              if (logger.isDebugEnabled())
                logger.debug("Lettura campi da importare: il valore della cella "
                    + carattereColonna
                    + (riga.getRowNum() + 1)
                    + " e' null oppure e' "
                    + "una stringa di lunghezza zero (Caso 2)");
            }
          } else {
            if (logger.isDebugEnabled())
              logger.debug("Lettura campi da importare: il valore della cella "
                  + carattereColonna
                  + (riga.getRowNum() + 1)
                  + " e' di tipo diverso dal tipo stringa");
          }
          if (logger.isDebugEnabled())
            logger.debug("getListaCampiDaImportare: fine lettura della cella "
                + carattereColonna
                + (riga.getRowNum() + 1));
        } else {
          if (logger.isDebugEnabled())
            logger.debug("Lettura campi da importare: la cella "
                + carattereColonna
                + (riga.getRowNum() + 1)
                + " non e' stata inizializzata");
        }
      }
    } else {
      listaErrori.add("Lettura campi da importare: la riga "
          + FOGLIO_LAVORAZIONE_E_FORNITURE_RIGA_NOME_FISICO_CAMPI
          + " del foglio '"
          + nomeFoglio
          + "' non e' stata inizializzata");
    }

    if (listaErrori.size() > 0) mappa.put("listaErrori", listaErrori);

    if (listaNomiFisiciCampiDaImportare.size() > 0) {
      mappa.put("listaNomiFisiciCampiDaImportare",
          listaNomiFisiciCampiDaImportare);
      mappa.put("listaIndiceColonnaCampiDaImportare",
          listaIndiceColonnaCampiDaImportare);
      mappa.put("listaIndiceArrayValoreCampiDaImportare",
          listaIndiceArrayValoreCampiDaImportare);
    }
    if (logger.isDebugEnabled())
      logger.debug("getListaCampiDaImportare: fine metodo");

    if (mappa.isEmpty())
      return null;
    else
      return mappa;
  }

  private void gestioneUnitaDiMisura(CampoImportExcel campoGCAP_UNIMIS,
      List<Object> valoriRiga, LoggerImportOffertaPrezzi loggerImport)
      throws SqlComposerException, SQLException {

    if (logger.isDebugEnabled())
      logger.debug("gestioneUnitaDiMisura: inizio metodo");

    String valoreGCAP_UNIMIS = (String) valoriRiga.get(campoGCAP_UNIMIS.getColonnaArrayValori());
    if (valoreGCAP_UNIMIS != null && valoreGCAP_UNIMIS.length() > 0) {
      try {
        String valoreCampoUNIMIS_TIPO = null;
        String querySQL = "select TIPO from UNIMIS "
          + "where CONTA = ? "
          + "and DESUNI = ? "
          + "order by NUMDEC desc";

        Vector<?> res = this.sqlDao.getVectorQuery(querySQL, new Object[] {
            new Long(-1), valoreGCAP_UNIMIS });

        if (res != null && res.size() > 0) {
          JdbcParametro result = (JdbcParametro) res.get(0);
          if (result != null && result.getValue() != null)
            valoreCampoUNIMIS_TIPO = (String) result.getValue();
        } else {
          String operatoreUpper = it.eldasoft.utils.sql.comp.SqlManager.getComposer(
              SqlManager.getTipoDB()).getFunzioneUpperCase();
          querySQL = "select TIPO from UNIMIS "
              + "where CONTA = ? "
              + "and OPERATORE_UPPER(DESUNI) = OPERATORE_UPPER(?) "
              + "order by NUMDEC desc";
          querySQL = querySQL.replaceAll("OPERATORE_UPPER", operatoreUpper);
          res = sqlDao.getVectorQuery(querySQL, new Object[] {
              new Long(-1), valoreGCAP_UNIMIS });

          if (res != null && res.size() > 0) {
            JdbcParametro result = (JdbcParametro) res.get(0);
            if (result != null && result.getValue() != null)
              valoreCampoUNIMIS_TIPO = (String) result.getValue();
          }
        }

        if (valoreCampoUNIMIS_TIPO != null
            && valoreCampoUNIMIS_TIPO.length() > 0) {
          if (logger.isDebugEnabled())
            logger.debug("L'unita ' di misura con descrizione '"
                + valoriRiga.get(campoGCAP_UNIMIS.getColonnaArrayValori())
                + " e' gia' presente nella tabella UNIMIS (UNIMIS.CONTA = -1 "
                + "and UNIMIS.TIPO = '"
                + valoreCampoUNIMIS_TIPO
                + "'");

          valoriRiga.set(campoGCAP_UNIMIS.getColonnaArrayValori(),
              valoreCampoUNIMIS_TIPO);
        } else {
          if (valoreGCAP_UNIMIS.length() > 3)
            valoreCampoUNIMIS_TIPO = valoreGCAP_UNIMIS.substring(0, 3);
          else
            valoreCampoUNIMIS_TIPO = valoreGCAP_UNIMIS;

          int progressivo = 0;
          long numeroOccorrenze = 0L;
          String tmpTIPO = new String(valoreCampoUNIMIS_TIPO);
          do {
            if (progressivo > 0) {
              if (valoreCampoUNIMIS_TIPO.length() == 1)
                tmpTIPO = valoreCampoUNIMIS_TIPO + progressivo;
              else if (valoreCampoUNIMIS_TIPO.length() == 2)
                tmpTIPO = valoreCampoUNIMIS_TIPO + progressivo;
              else
                tmpTIPO = valoreCampoUNIMIS_TIPO.substring(0, 2) + progressivo;
            }
            progressivo++;
            Vector<?> ret = this.sqlDao.getVectorQuery(
                "select count(*) from UNIMIS where CONTA = ? and TIPO = ? ",
                new Object[] { new Long(-1), tmpTIPO });
            if (ret != null && ret.size() > 0) {
              Long valore = (Long) ((JdbcParametro) ret.get(0)).getValue();
              if (valore != null) numeroOccorrenze = valore.longValue();
            }
          } while (numeroOccorrenze > 0);

          if (valoreGCAP_UNIMIS.length() > 100)
            valoreGCAP_UNIMIS = valoreGCAP_UNIMIS.substring(0, 100);

          this.sqlDao.update(
              "insert into UNIMIS (CONTA, TIPO, DESUNI, NUMDEC) values (?, ?, ?, ?)",
              new Object[] { new Long(-1), tmpTIPO, valoreGCAP_UNIMIS,
                  new Long(0) });
          if (logger.isDebugEnabled())
            logger.debug("Inserimento nella tabella UNIMIS di una nuova "
                + "occorenza (CONTA = -1 and TIPO = '"
                + tmpTIPO
                + "')");

          valoriRiga.set(campoGCAP_UNIMIS.getColonnaArrayValori(), tmpTIPO);
          loggerImport.addMessaggioUnitaMisura("unita' di misura: '"
              + valoreGCAP_UNIMIS
              + "'");
        }
      } catch (SQLException s) {
        logger.error(
            "Errore nella gestione del campo GCAP.UNIMIS in relazione "
                + "con il proprio archivio (tabella UNIMIS)", s);
        throw s;
      }
    }
    if (logger.isDebugEnabled())
      logger.debug("gestioneUnitaDiMisura: fine metodo");
  }

  private boolean controlloValoriRiga(List<CampoImportExcel> listaCampiImportExcel,
      int indiceRiga, List<Object> valoriCampiRigaExcel, List<String> tmpListaMsg, Long tipoFornitura,
      boolean importDatiDitta) {

    boolean rigaImportabile = true;
    for (int colonna = 0; colonna < listaCampiImportExcel.size(); colonna++) {
      CampoImportExcel tmpCampo = listaCampiImportExcel.get(colonna);
      String strCellaExcel = UtilityExcel.conversioneNumeroColonna(tmpCampo.getColonnaCampo())
          + (indiceRiga + 1);
      Object valore = valoriCampiRigaExcel.get(colonna);
      if (valore == null) {
        if (tmpCampo.isObbligatorio()) {
          if (tmpCampo.getValoreDiDefault() != null) {
            valoriCampiRigaExcel.set(colonna, tmpCampo.getValoreDiDefault());
          } else {
            tmpListaMsg.add("- la cella "
                + strCellaExcel
                + " non e' valorizzata");
            rigaImportabile = false;
          }
        }
      } else if (valore instanceof Error) {
        tmpListaMsg.add(" - la cella "
            + strCellaExcel
            + " contiene un "
            + "errore: "
            + ((Error) valore).getMessage());
      } else {
        if(arrayCampi[3].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))){
          String valoreString = this.getCodvocString(valore);
          this.valoreCodvoc = valoreString;
          this.cellaCodvoc = strCellaExcel;
        }else
        if (!new Long(98).equals(tipoFornitura) && arrayCampi[2].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo())) && !importDatiDitta){
        	String tmpValore = null;
            if (valore instanceof Double) {
                Double tmpDoubleValore =  (Double) valore;
                Long tmpLongValore =  tmpDoubleValore.longValue();
                tmpValore = tmpLongValore.toString();
                valoriCampiRigaExcel.set(colonna, "" + ((Double) valore).longValue());
            }
            if (valore instanceof Long) {
            	Long tmpLongValore =  (Long) valore;
             	tmpValore = tmpLongValore.toString();
             	valoriCampiRigaExcel.set(colonna, "" + ((Long) valore).longValue());
            }
            if (valore instanceof String) {
            	tmpValore = ((String) valore).toUpperCase();
            	valoriCampiRigaExcel.set(colonna, tmpValore.toUpperCase());
            }

            tmpValore = UtilityStringhe.convertiNullInStringaVuota(tmpValore);
            if (!"".equals(tmpValore) && tmpValore.length() != 10) {
              tmpListaMsg.add(" - la cella "
                  + strCellaExcel
                  + " presenta un valore non ammesso: "
                  +	"il codice CIG deve avere una lunghezza di 10 caratteri.");
              rigaImportabile = false;
            } else {
	            if (!this.pgManager.controlloCodiceCIG(tmpValore)) {
	                tmpListaMsg.add(" - la cella "
	                    + strCellaExcel
	                    + " presenta un valore non ammesso: il codice CIG non e' valido.");
	                rigaImportabile = false;
	            }
            }
          } else if (!new Long(98).equals(tipoFornitura) && arrayCampi[7].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
            tmpCampo.getNomeFisicoCampo()))) {
            String tmpValore = (String) valore;
            if (tmpValore != null || tmpValore.length() > 0) {
              List<Tabellato> valoriTabellato = tmpCampo.getTabellatoDelCampo();
              boolean valoreTrovato = false;
              for (int u = 0; u < valoriTabellato.size() && !valoreTrovato; u++) {
                Tabellato tmpTabellato = valoriTabellato.get(u);
                if (tmpTabellato.getDescTabellato().toUpperCase().indexOf(
                    tmpValore.toUpperCase()) >= 0) {
                  //Il campo CLASI1 e' numerico e gli viene assegnato un valore stringa
                  //In Postgres questo da problemi
                  String nomeCampo= tmpCampo.getNomeFisicoCampo();
                  if("GCAP.CLASI1".equals(nomeCampo))
                    valoriCampiRigaExcel.set(tmpCampo.getColonnaArrayValori(),
                        new Long(tmpTabellato.getTipoTabellato()));
                  else
                    valoriCampiRigaExcel.set(tmpCampo.getColonnaArrayValori(),
                      tmpTabellato.getTipoTabellato());
                  valoreTrovato = true;
                }
              }
              if (!valoreTrovato) {
                // Set al valore di default al campo GCAP.CLASI1 ('a misura')
                valoriCampiRigaExcel.set(tmpCampo.getColonnaArrayValori(), "3");
                valoreTrovato = true;

                if (valoreTrovato) {
                  tmpListaMsg.add(" - la cella "
                      + strCellaExcel
                      + " presenta "
                      + "un valore non ammesso. Tale valore e' stato ignorato ed e' "
                      + "stato importato il valore di default (a misura)");
                } else {
                  tmpListaMsg.add(" - la cella "
                      + strCellaExcel
                      + " presenta "
                      + "un valore non ammesso. (Valori consentiti: 'a misura', "
                      + "'a corpo')");
                }
              }
            }
          } else if(new Long(98).equals(tipoFornitura) && arrayCampi[6].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
              tmpCampo.getNomeFisicoCampo()))) {
            if (valore instanceof String) {
              String tmp = (String) valore;
              Campo campoUNIMIS_DESCR = DizionarioCampi.getInstance().getCampoByNomeFisico(
                  "UNIMIS.DESUNI");
              if (tmp.length() > campoUNIMIS_DESCR.getLunghezza()) {
                tmpListaMsg.add(" - la cella "
                    + strCellaExcel
                    + " ha piu' di "
                    + tmpCampo.getLunghezzaCampo()
                    + " caratteri");
                rigaImportabile = false;
              }
            } else {
              tmpListaMsg.add(" - la cella "
                  + strCellaExcel
                  + " non e' in "
                  + "formato stringa.");
              rigaImportabile = false;
            }
          } else if (!new Long(98).equals(tipoFornitura)
        		  && arrayCampi[6].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
              String tmpValore = (String) valore;
              tmpValore = UtilityStringhe.convertiNullInStringaVuota(tmpValore);
              if (!"".equals(tmpValore)) {
                String integrazioneCineca = ConfigManager.getValore("integrazioneAnagraficheUGOV");
                integrazioneCineca = UtilityStringhe.convertiNullInStringaVuota(integrazioneCineca);
                String selVerificaCategoria = "select caisim from cais where caisim = ?";
                if("1".equals(integrazioneCineca)){
                  selVerificaCategoria = "select codcat from v_ubuy_beniservizi where codcat = ?";
                }
                try {
                  Vector<?> datiCat = this.sqlDao.getVectorQuery(selVerificaCategoria, new Object[] { tmpValore });
                  if (!(datiCat != null && datiCat.size() > 0)) {
                    tmpListaMsg.add(" - la cella "
                        + strCellaExcel
                        + " presenta "
                        + "un valore non ammesso. ");
                    rigaImportabile = false;
                  }
                } catch (SQLException e) {
                  logger.error("Errore nella verifica dell'esistenza della categoria ", e);
                }
              }
          }
          else if (!new Long(98).equals(tipoFornitura) && arrayCampi[10].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
            tmpCampo.getNomeFisicoCampo()))) {
            if (valore instanceof String) {
              String tmp = (String) valore;

              Campo campoUNIMIS_DESCR = DizionarioCampi.getInstance().getCampoByNomeFisico(
                  "UNIMIS.DESUNI");
              if (tmp.length() > campoUNIMIS_DESCR.getLunghezza()) {
                tmpListaMsg.add(" - la cella "
                    + strCellaExcel
                    + " ha piu' di "
                    + tmpCampo.getLunghezzaCampo()
                    + " caratteri");
                rigaImportabile = false;
              }
            } else {
              tmpListaMsg.add(" - la cella "
                  + strCellaExcel
                  + " non e' in "
                  + "formato stringa.");
              rigaImportabile = false;
            }

          }else if (new Long(3).equals(tipoFornitura) && arrayCampi[13].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
            tmpCampo.getNomeFisicoCampo()))) {
            if (valore instanceof Double) {
              long liva = new Double((Double)valore).longValue();
              if (liva > 99) {
                tmpListaMsg.add(" - la cella "
                    + strCellaExcel
                    + " presenta "
                    + "un valore non ammesso: "
                    + "l'IVA deve risultare di 2 cifre.");
                rigaImportabile = false;
              }
            }

          } else {
            switch (tmpCampo.getTipoCampo()) {
            case Campo.TIPO_STRINGA:
            case Campo.TIPO_NOTA:
  //            String codice_cliente = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_CLIENTE);

                if (arrayCampi[1].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                    tmpCampo.getNomeFisicoCampo()))) {
                  // Il campo CODIGA nel foglio Excel e' di tipo numerico, mentre su
                  // DB e' di tipo stringa: a questo punto si converte in stringa il
                  // valore numerico letto dal foglio Excel
                  if (valore instanceof Long)
                    valoriCampiRigaExcel.set(colonna, ""
                        + ((Long) valore).longValue());
                  if (valore instanceof Double)
                    valoriCampiRigaExcel.set(colonna, ""
                        + ((Double) valore).longValue());
                } else if (!(valore instanceof String))
                  tmpListaMsg.add(" - la cella "
                      + strCellaExcel
                      + " non e' in formato stringa.");
                else if (!new Long(98).equals(tipoFornitura) && arrayCampi[8].equalsIgnoreCase( // Controllo dei campi con
                // valore si/no e tabellato
                SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
                  if ("si".equalsIgnoreCase((String) valore)) {
                    valoriCampiRigaExcel.set(tmpCampo.getColonnaArrayValori(), "1");
                  } else if ("no".equalsIgnoreCase((String) valore)) {
                    valoriCampiRigaExcel.set(colonna, "2");
                  } else {
                    valoriCampiRigaExcel.set(tmpCampo.getColonnaArrayValori(), null);
                    tmpListaMsg.add(" - la cella "
                        + strCellaExcel
                        + " presenta un "
                        + "valore non ammesso. (Valori consentiti: 'si', 'no')");
                  }
                } else if (!new Long(98).equals(tipoFornitura) && arrayCampi[9].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                    tmpCampo.getNomeFisicoCampo()))) {
                  if ("si".equalsIgnoreCase((String) valore))
                    valoriCampiRigaExcel.set(colonna, "1");
                  else if ("no".equalsIgnoreCase((String) valore))
                    valoriCampiRigaExcel.set(colonna, "2");
                  else {
                    valoriCampiRigaExcel.set(colonna, null);
                    tmpListaMsg.add(" - la cella "
                        + strCellaExcel
                        + " presenta un valore non ammesso. (Valori consentiti: "
                        + "'si', 'no')");
                  }
                } else {
                  String tmp = (String) valore;
                  String dominio = tmpCampo.getDominioCampo();
                  if (tmp.length() > tmpCampo.getLunghezzaCampo() && !"CLOB".equals(dominio)) {
                    tmpListaMsg.add(" - la cella "
                        + strCellaExcel
                        + " ha piu' di "
                        + tmpCampo.getLunghezzaCampo()
                        + " caratteri");
                    rigaImportabile = false;
                  }
                }


              break;
            case Campo.TIPO_INTERO:
            case Campo.TIPO_DECIMALE:
              if (!(valore instanceof Double)) {
                tmpListaMsg.add("- la cella "
                    + strCellaExcel
                    + " non e' in formato numerico.");
                valoriCampiRigaExcel.set(colonna, null);
              } else {
                // In Excel, la formattazione di un campo di tipo numerico
                // comporta
                // la modifica della visualizzazione della cella e non del valore
                // che effettivamente questa contiene. Ad esempio: una cella che
                // e' formattata per visualizzare al piu' 3 cifre decimali:
                // - se il valore e': 12,321 o 32,26 o 102,2 allora valore
                // visualizzato e valore contenuto coincidono;
                // - se il valore e': 12,3212 o 32,2628 allora il valore
                // visualizzato e' diverso dal valore contenuto. Per la precisione
                // i valori visualizzati sono rispettivamente: 12,321 e 32,263;
                // Questo comporta il controllo dei valori delle celle di campo in
                // formato decimale e l'eventuale arrotondamento del valore
                double valoreCampo = ((Double) valoriCampiRigaExcel.get(colonna)).doubleValue();
                double tmpValore = UtilityMath.round(valoreCampo,
                    tmpCampo.getCifreDecimali());
                if (valoreCampo != tmpValore)
                  valoriCampiRigaExcel.set(colonna, new Double(tmpValore));
              }
              break;
            case Campo.TIPO_DATA:
              if (!(valore instanceof Date)) {
                tmpListaMsg.add("- la cella "
                    + strCellaExcel
                    + " non e' in formato data.");
                valoriCampiRigaExcel.set(colonna, null);
              }
              break;
            }
          }
      }
    }


    return rigaImportabile;
  }

  private List<Object> letturaRiga(HSSFRow rigaFoglioExcel, List<CampoImportExcel> listaCampiDaImportare) {
    List<Object> valoriCampi = new ArrayList<Object>();
    HSSFCell cella = null;
    HSSFFormulaEvaluator evaluator = new HSSFFormulaEvaluator(workBook);
    for (int i = 0; i < listaCampiDaImportare.size(); i++) {
      CampoImportExcel campoImportExcel = listaCampiDaImportare.get(i);
      cella = rigaFoglioExcel.getCell(campoImportExcel.getColonnaCampo() - 1);
      if (cella != null) {
        switch (cella.getCellType()) {
        case HSSFCell.CELL_TYPE_STRING:
          valoriCampi.add(cella.getRichStringCellValue().toString());
          break;
        case HSSFCell.CELL_TYPE_NUMERIC:
          if (HSSFDateUtil.isCellDateFormatted(cella))
            valoriCampi.add(HSSFDateUtil.getJavaDate(cella.getNumericCellValue()));
          else
            valoriCampi.add(new Double(cella.getNumericCellValue()));
          break;
        case HSSFCell.CELL_TYPE_BOOLEAN:
          valoriCampi.add(new Boolean(cella.getBooleanCellValue()));
          break;
        case HSSFCell.CELL_TYPE_BLANK:
          valoriCampi.add(null);
          break;
        case HSSFCell.CELL_TYPE_ERROR:
          valoriCampi.add(new Error("Cella con errore"));
          break;
        case HSSFCell.CELL_TYPE_FORMULA:
          switch (evaluator.evaluateFormulaCell(cella)) {
          case HSSFCell.CELL_TYPE_BOOLEAN:
            valoriCampi.add(new Boolean(cella.getBooleanCellValue()));
            break;
          case HSSFCell.CELL_TYPE_NUMERIC:
            if (HSSFDateUtil.isCellDateFormatted(cella))
              valoriCampi.add(HSSFDateUtil.getJavaDate(cella.getNumericCellValue()));
            else
              valoriCampi.add(new Double(cella.getNumericCellValue()));
            break;
          case HSSFCell.CELL_TYPE_STRING:
            valoriCampi.add(cella.getRichStringCellValue().toString());
            break;
          case HSSFCell.CELL_TYPE_BLANK:
            valoriCampi.add(null);
            break;
          case HSSFCell.CELL_TYPE_ERROR:
            valoriCampi.add(new Error(
                "Cella con formula, il cui risultato e' un errore"));
            break;
          }
          break;
        }
      } else {
        String letteraColonna = UtilityExcel.conversioneNumeroColonna(i + 1);
        if (logger.isDebugEnabled())
          logger.debug("La cella "
              + letteraColonna
              + (rigaFoglioExcel.getRowNum() + 1)
              + " non e' stata inizializzata. Si controllera' successivamente "
              + "se il valore della colonna "
              + letteraColonna
              + " e' un campo "
              + "obbligatorio o se le puo' essere associato un valore di default");
        valoriCampi.add(null);
      }
    }
    return valoriCampi;
  }

  private void importOffertaPrezziDitta(HSSFSheet foglio, String ngara,
      String codiceDitta, List<CampoImportExcel> listaCampiImportExcel,
      boolean isGaraLottiConOffertaUnica, String isPrequalifica, HttpSession session,
      LoggerImportOffertaPrezzi loggerImport,
      boolean isCodificaAutomaticaAttiva, List<CampoImportExcel> listaCampiImportExcelGCAP_SAN,
      List<CampoImportExcel> listaCampiImportExcelDPRE, List<CampoImportExcel> listaCampiImportExcelDPRE_SAN,
      Long tipoFornitura) throws SQLException, SqlComposerException {

    if (logger.isDebugEnabled())
      logger.debug("importOffertaPrezziDitta: inizio metodo");

    String label = "Lavorazione o fornitura ";
    String label1 = "importata";
    boolean isIntegrazioneOliamm = false;
    if (new Long(98).equals(tipoFornitura)) {
      label ="Prodotto ";
      label1 ="importato";
      String select="select cliv1 from gare where ngara=?";
      if(isGaraLottiConOffertaUnica)
        select ="select cliv1 from gare where ngara=(select codgar1 from gare where ngara=?)";
      try {
        Vector<?> tmp = this.sqlDao.getVectorQuery(select, new Object[]{ngara});
        Long cliv1 = ((Long) ((JdbcParametro) tmp.get(0)).getValue());
        if (cliv1!= null)
          isIntegrazioneOliamm= true;
      } catch (SQLException g) {
        logger.error(
            "Errore nella lettura del campo GARE.CLIV1 della gara  "+ ngara, g);
        throw g;
      }
    }

    if (isGaraLottiConOffertaUnica) {
      try {
        // Lista dei lotti non importabili in quanto è presente almeno un voce
        // (DPRE) che non rispetta i requisiti minimi (REQMIN = '2')
        List<?> listaLottiNonImportabili = this.sqlDao.getVectorQueryForList(
            "select NGARA from DPRE where DPRE.NGARA in ("
                + "select NGARA from GARE where GARE.CODGAR1 = ? "
                + "and (GARE.GENERE is null or GARE.GENERE <> 3)) "
                + "and DPRE.REQMIN = '2' and DPRE.DITTAO = ?", new Object[] {
                ngara, codiceDitta });

        String whereLottiNonImportabili = null;

        if (listaLottiNonImportabili != null
            && listaLottiNonImportabili.size() > 0) {
          whereLottiNonImportabili = "";
          for (int i = 0; i < listaLottiNonImportabili.size(); i++) {
            Vector<?> occorrenza = (Vector<?>) listaLottiNonImportabili.get(i);
            String ngaraNonEsportabile = ((JdbcParametro) occorrenza.get(0)).getStringValue();
            if (i > 0) whereLottiNonImportabili += ",";
            whereLottiNonImportabili += "'" + ngaraNonEsportabile + "'";
          }
        }

        // Cancellazione delle estensioni di GCAP
        String sqlSelectGCAP = "select NGARA, CONTAF from GCAP where GCAP.NGARA in ("
            + "select NGARA from GARE where GARE.CODGAR1 = ? "
            + "and (GARE.GENERE is null or GARE.GENERE <> 3)) "
            + "and DITTAO = ?";
        if (whereLottiNonImportabili != null
            && whereLottiNonImportabili.length() > 0) {
          sqlSelectGCAP += " and GCAP.NGARA not in ("
              + whereLottiNonImportabili
              + ")";
        }

        List<?> listaOccorrenze = this.sqlDao.getVectorQueryForList(sqlSelectGCAP,
            new Object[] { ngara, codiceDitta });
        if (listaOccorrenze != null && listaOccorrenze.size() > 0) {
          for (int wq = 0; wq < listaOccorrenze.size(); wq++) {
            Vector<?> occorrenza = (Vector<?>) listaOccorrenze.get(wq);
            String codiceGara = ((JdbcParametro) occorrenza.get(0)).getStringValue();
            Long contaf = (Long) ((JdbcParametro) occorrenza.get(1)).getValue();
            // Cancellazione dell'estensione GCAP_EST
            this.sqlDao.update(
                "delete from GCAP_EST where NGARA = ? and CONTAF = ?",
                new Object[] { codiceGara, contaf });

            // Cancellazione dell'estensione GCAP_SAN
            this.sqlDao.update(
                "delete from GCAP_SAN where NGARA = ? and CONTAF = ?",
                new Object[] { codiceGara, contaf });
          }
        }

        // Cancellazione delle estensioni di DPRE
        String sqlSelectDPRE = "select NGARA,CONTAF from DPRE where DPRE.NGARA in ("
            + "select NGARA from GARE where GARE.CODGAR1 = ? "
            + "and (GARE.GENERE is null or GARE.GENERE <> 3)) and DITTAO = ?";
        if (whereLottiNonImportabili != null
            && whereLottiNonImportabili.length() > 0) {
          sqlSelectDPRE += " and DPRE.NGARA not in ("
              + whereLottiNonImportabili
              + ")";
        }

        List<?> listaOccorrenzeDPRE = this.sqlDao.getVectorQueryForList(sqlSelectDPRE,
            new Object[] { ngara, codiceDitta });
        if (listaOccorrenzeDPRE != null && listaOccorrenzeDPRE.size() > 0) {
          for (int od = 0; od < listaOccorrenzeDPRE.size(); od++) {
            Vector<?> occorrenzaDPRE = (Vector<?>) listaOccorrenzeDPRE.get(od);
            String codiceGara = ((JdbcParametro) occorrenzaDPRE.get(0)).getStringValue();
            Long contaf = (Long) ((JdbcParametro) occorrenzaDPRE.get(1)).getValue();

            // Cancellazione estensione DPRE_SAN
            this.sqlDao.update(
                "delete from DPRE_SAN where NGARA = ? and CONTAF = ? and DITTAO = ?",
                new Object[] { codiceGara, contaf , codiceDitta});
          }
        }

        String sqlDeleteGCAP = "delete from GCAP where GCAP.NGARA in ("
            + "select NGARA from GARE where GARE.CODGAR1 = ? and "
            + "(GARE.GENERE is null or GARE.GENERE <> 3)) and DITTAO = ?";
        if (whereLottiNonImportabili != null
            && whereLottiNonImportabili.length() > 0) {
          sqlDeleteGCAP += " and GCAP.NGARA not in ("
              + whereLottiNonImportabili
              + ")";
        }
        this.sqlDao.update(sqlDeleteGCAP, new Object[] { ngara, codiceDitta });

        String sqlDeleteDPRE = "delete from DPRE where DPRE.NGARA in ("
            + "select NGARA from GARE where GARE.CODGAR1 = ? and "
            + "(GARE.GENERE is null or GARE.GENERE <> 3)) and DITTAO = ?";
        if (whereLottiNonImportabili != null
            && whereLottiNonImportabili.length() > 0) {
          sqlDeleteDPRE += " and DPRE.NGARA not in ("
              + whereLottiNonImportabili
              + ")";
        }
        this.sqlDao.update(sqlDeleteDPRE, new Object[] { ngara, codiceDitta });

      } catch (SQLException g) {
        logger.error(
            "Errore nella cancellazione delle occorrenze presenti "
                + "nelle tabelle GCAP, GCAP_EST, GCAP_SAN, DPRE e DPRE_SAN relative alla gara a lotti "
                + "con offerta unica '"
                + ngara
                + "' e con codice ditta pari a '"
                + codiceDitta
                + "'", g);
        throw g;
      }
    } else {
      try {
        boolean esistonoVociNonRequisitiMinimi = (0 != this.geneManager.countOccorrenze(
            "DPRE", "NGARA = ? and REQMIN = '2' and DITTAO = ?", new Object[] {
                ngara, codiceDitta }));
        if (!esistonoVociNonRequisitiMinimi) {
          // Cancellazione estensioni di GCAP
          List<?> listaOccorrenze = this.sqlDao.getVectorQueryForList(
              "select NGARA, CONTAF from GCAP where NGARA = ? and DITTAO = ?",
              new Object[] { ngara, codiceDitta });
          if (listaOccorrenze != null && listaOccorrenze.size() > 0) {
            for (int wq = 0; wq < listaOccorrenze.size(); wq++) {
              Vector<?> occorrenza = (Vector<?>) listaOccorrenze.get(wq);
              String codiceGara = ((JdbcParametro) occorrenza.get(0)).getStringValue();
              Long contaf = (Long) ((JdbcParametro) occorrenza.get(1)).getValue();
              // Cancellazione di GCAP_EST
              this.sqlDao.update(
                  "delete from GCAP_EST where NGARA = ? and CONTAF = ?",
                  new Object[] { codiceGara, contaf });
              // Cancellazione di GCAP_SAN
              this.sqlDao.update(
                  "delete from GCAP_SAN where NGARA = ? and CONTAF = ?",
                  new Object[] { codiceGara, contaf });
            }
          }

          // Cancellazione delle estensioni di DPRE
          List<?> listaOccorrenzeDPRE = this.sqlDao.getVectorQueryForList(
              "select NGARA,CONTAF from DPRE where NGARA = ? and DITTAO = ?",
              new Object[] { ngara, codiceDitta });
          if (listaOccorrenzeDPRE != null && listaOccorrenzeDPRE.size() > 0) {
            for (int od = 0; od < listaOccorrenzeDPRE.size(); od++) {
              Vector<?> occorrenzaDPRE = (Vector<?>) listaOccorrenzeDPRE.get(od);
              String codiceGara = ((JdbcParametro) occorrenzaDPRE.get(0)).getStringValue();
              Long contaf = (Long) ((JdbcParametro) occorrenzaDPRE.get(1)).getValue();

              // Cancellazione estensione DPRE_SAN
              this.sqlDao.update(
                  "delete from DPRE_SAN where NGARA = ? and CONTAF = ? and DITTAO = ?",
                  new Object[] { codiceGara, contaf, codiceDitta });
            }
          }

          this.sqlDao.update("delete from GCAP where NGARA = ? and DITTAO = ?",
              new Object[] { ngara, codiceDitta });
          this.sqlDao.update("delete from DPRE where NGARA = ? and DITTAO = ?",
              new Object[] { ngara, codiceDitta });
        }
      } catch (SQLException g) {
        logger.error(
            "Errore nella cancellazione delle occorrenze presenti "
                + "nelle tabelle GCAP, GCAP_EST, GCAP_SAN, DPRE e DPRE_SAN relative alla gara '"
                + ngara
                + "' e con codice ditta pari a '"
                + codiceDitta
                + "'", g);
        throw g;
      }
    }

    int colonnaCodiceLavorazioneFornitura = -1;
    int colonnaCodiceLotto = -1;
    int colonnaCODIGA = -1;
    int cCLF = 0;
    if(new Long(98).equals(tipoFornitura)){
      cCLF = 1;
    }
    for (int colonna = 0; colonna < listaCampiImportExcel.size()
        && colonnaCodiceLavorazioneFornitura < cCLF; colonna++) {
      CampoImportExcel tmpCampo = listaCampiImportExcel.get(colonna);
      if(new Long(98).equals(tipoFornitura)){
        if (isGaraLottiConOffertaUnica
            && (!isCodificaAutomaticaAttiva)
            && arrayCampi[0].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                tmpCampo.getNomeFisicoCampo())))
          colonnaCodiceLotto = tmpCampo.getColonnaArrayValori();
        else if (arrayCampi[3].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
            tmpCampo.getNomeFisicoCampo())))
          colonnaCodiceLavorazioneFornitura = tmpCampo.getColonnaArrayValori();
        else if (arrayCampi[1].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
            tmpCampo.getNomeFisicoCampo())))
          colonnaCODIGA = tmpCampo.getColonnaArrayValori();
      }else{
        if (isGaraLottiConOffertaUnica
            && (!isCodificaAutomaticaAttiva)
            && arrayCampi[0].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                tmpCampo.getNomeFisicoCampo())))
          colonnaCodiceLotto = tmpCampo.getColonnaArrayValori();
        else if (arrayCampi[1].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
            tmpCampo.getNomeFisicoCampo())))
          colonnaCODIGA = tmpCampo.getColonnaArrayValori();
        //else if (arrayCampi[2].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
        //    tmpCampo.getNomeFisicoCampo())))
        //  colonnaCODCIG = tmpCampo.getColonnaArrayValori();
        else if (arrayCampi[3].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
            tmpCampo.getNomeFisicoCampo())))
          colonnaCodiceLavorazioneFornitura = tmpCampo.getColonnaArrayValori();
      }
    }

    int indiceRiga = FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE - 1;
    int ultimaRigaValorizzata = foglio.getPhysicalNumberOfRows();

    // Per le gare NON ad offerta unica:
    // - la variabile locNgara rappresenta il codice dell'i-esimo lotto per cui
    // si sta importando l'offerta prezzi;
    // - all'inizio della procedura di import vengono determinati il valore
    // massimo dei campi GCAP.CONTAF e GCAP.NORVOC da usare per l'inserimento
    // di nuovi articoli da parte della ditta (nuove occorrenze nella GCAP);
    // mentre per le gare ad offerta unica:
    // - la variabile locNgara rappresenta il codice della gara complementare;
    // - il valore massimo dei campi GCAP.CONTAF e GCAP.NORVOC da usare per
    // l'inserimento di nuovi articoli da parte della ditta (nuove occorrenze
    // nella GCAP) viene posticipato a quando, nella lettura della i-esima
    // riga del foglio Excel, la variabile locNgara assume il codice del lotto
    // specificato nella riga che si sta per importare;
    long maxGCAP_CONTAF = -1;
    double maxGCAP_NORVOC = -1;
    if (!isGaraLottiConOffertaUnica) {
      Vector<?> contatoriGCAP = this.sqlDao.getVectorQuery(
          "select max(CONTAF), coalesce(max(NORVOC),0) from GCAP where NGARA = ?",
          new Object[] { ngara });
      maxGCAP_CONTAF = ((Long) ((JdbcParametro) contatoriGCAP.get(0)).getValue()).longValue();
      Object obj = ((JdbcParametro) contatoriGCAP.get(1)).getValue();
      if (obj instanceof Double)
        maxGCAP_NORVOC = ((Double) obj).doubleValue();
      else
        maxGCAP_NORVOC = new Double("" + ((Long) obj).longValue()).doubleValue();
    }
    // Contatore del numero di righe consecutive vuote, per terminare la lettura
    // del foglio Excel prima di giungere all'ultima riga inizializzata dopo che
    // sono state trovate un numero di righe vuote consecutive pari alla
    // costante IMPORT_NUMERO_RIGHE_CONSECUTIVE_VUOTE
    int contatoreRigheVuote = 0;

    for (; indiceRiga < ultimaRigaValorizzata
        && contatoreRigheVuote <= IMPORT_NUMERO_RIGHE_CONSECUTIVE_VUOTE; indiceRiga++) {
      if (logger.isDebugEnabled())
        logger.debug("Inizio lettura della riga " + (indiceRiga + 1));

      HSSFRow rigaFoglioExcel = foglio.getRow(indiceRiga);
      if (rigaFoglioExcel != null) {
        // Lista valori di GCAP
        List<Object> valoriCampiRigaExcel = this.letturaRiga(rigaFoglioExcel,
            listaCampiImportExcel);

        // Lista valori di GCAP_SAN
        List<Object> valoriCampiRigaExcelGCAP_SAN = null;
        if (listaCampiImportExcelGCAP_SAN != null) {
          valoriCampiRigaExcelGCAP_SAN = this.letturaRiga(rigaFoglioExcel,
              listaCampiImportExcelGCAP_SAN);
        }

        // Lista valori di DPRE
        List<Object> valoriCampiRigaExcelDPRE = null;
        if (listaCampiImportExcelDPRE != null) {
          valoriCampiRigaExcelDPRE = this.letturaRiga(rigaFoglioExcel,
              listaCampiImportExcelDPRE);
        }

        // Lista valori di DPRE_SAN
        List<Object> valoriCampiRigaExcelDPRE_SAN = null;
        if (listaCampiImportExcelDPRE_SAN != null) {
          valoriCampiRigaExcelDPRE_SAN = this.letturaRiga(rigaFoglioExcel,
              listaCampiImportExcelDPRE_SAN);
        }

        // Contatore del numero di celle non inizializzate
        int numeroCelleNull = 0;
        for (int h = 0; h < valoriCampiRigaExcel.size(); h++)
          if (valoriCampiRigaExcel.get(h) == null) numeroCelleNull++;

        // Se il numero di celle non inizializzate e' minore del numero di campi
        // da leggere in ciascuna riga del foglio
        if (numeroCelleNull < valoriCampiRigaExcel.size()) {
          // Reset del contatore del numero di righe vuote se diverso da zero
          if (contatoreRigheVuote > 0) contatoreRigheVuote = 0;

          // Incremento contatore numero righe lette dal foglio
          loggerImport.incrementaRigheLette();

          boolean intestazioneLogGiaInserita = false;
          boolean rigaImportabile = true;
          List<String> tmpListaMsg = new ArrayList<String>();
          List<String> backupTmpListaMsg = null;

          String codiceLavorazioneFornitura = this.getCodvocString(valoriCampiRigaExcel.get(colonnaCodiceLavorazioneFornitura));
          Long valoreContafCella=null;
          //Lettura del valore di contaf memorizzato nel foglio
          HSSFCell cella = rigaFoglioExcel.getCell(this.posCampoContaf);
          if (cella != null) {
            Double valTmp = new Double(cella.getNumericCellValue());
            if(valTmp!=null)
              valoreContafCella=new Long(valTmp.longValue());
          }

          //Nel caso di integrazione OLIAMM non è possibile importare nuovi prodotti, quindi si salta al ciclo successivo
          if (isIntegrazioneOliamm) {
            String numeroGara = null;
            if (isGaraLottiConOffertaUnica) {
              if (isCodificaAutomaticaAttiva) {
                Object tmp = valoriCampiRigaExcel.get(colonnaCODIGA);
                String codiga = null;
                if (tmp instanceof Double) {
                  Long codigaLong = new Long(((Double) valoriCampiRigaExcel.get(colonnaCODIGA)).longValue());
                  codiga = codigaLong.toString();
                } else
                  codiga = (String) valoriCampiRigaExcel.get(colonnaCODIGA);

                Vector<?> tmpVec = this.sqlDao.getVectorQuery(
                    "select NGARA from GARE where GARE.CODGAR1 = ? and NGARA <> ? "
                        + "and CODIGA = ? and (GARE.GENERE is null or GARE.GENERE <> 3) "
                        + "and GARE.MODLICG in (5,6,14,16) order by GARE.NGARA asc",
                    new Object[] { ngara, ngara, codiga });
                if (tmpVec != null) {
                  numeroGara = ((JdbcParametro) tmpVec.get(0)).getStringValue();
                }
              } else {
                numeroGara = (String) valoriCampiRigaExcel.get(colonnaCodiceLotto);
              }
            } else {
              numeroGara = ngara;// (String)
            }

            long numOccorrenzeGCAP = this.geneManager.countOccorrenze("GCAP",
                "GCAP.NGARA = ?  and GCAP.CODVOC = ? and GCAP.DITTAO is null",
                new Object[] { numeroGara, codiceLavorazioneFornitura });
            if (numOccorrenzeGCAP <= 0) {
              loggerImport.addMessaggioErrore((isGaraLottiConOffertaUnica
                  ? "Lotto " + numeroGara + " - "
                  : "")
                  + "Prodotto "
                  + codiceLavorazioneFornitura
                  + " (riga "
                  + (indiceRiga + 1)
                  + ") non importato:\n- il prodotto è definito "
                  + "dalla ditta e non è possibile inserirlo "
                  + "perché la gara è collegata a OLIAMM");
              loggerImport.incrementaRecordNonAggiornati();
              continue;
            }
          }


          // Controllo valori per GCAP
          this.valoreCodvoc=null;
          this.cellaCodvoc=null;
          rigaImportabile = this.controlloValoriRiga(listaCampiImportExcel,
              indiceRiga, valoriCampiRigaExcel, tmpListaMsg, tipoFornitura,true);

          // Controllo valori per GCAP_SAN
          if (rigaImportabile && valoriCampiRigaExcelGCAP_SAN != null) {
            rigaImportabile = this.controlloValoriRiga(
                listaCampiImportExcelGCAP_SAN, indiceRiga,
                valoriCampiRigaExcelGCAP_SAN, tmpListaMsg, tipoFornitura,true);
          }

          // Controllo valori per DPRE
          if (rigaImportabile && valoriCampiRigaExcelDPRE != null) {
            rigaImportabile = this.controlloValoriRiga(listaCampiImportExcelDPRE,
                indiceRiga, valoriCampiRigaExcelDPRE, tmpListaMsg, tipoFornitura,true);
          }

          // Controllo valori per DPRE_SAN
          if (rigaImportabile && valoriCampiRigaExcelDPRE_SAN != null) {
            rigaImportabile = this.controlloValoriRiga(
                listaCampiImportExcelDPRE_SAN, indiceRiga,
                valoriCampiRigaExcelDPRE_SAN, tmpListaMsg, tipoFornitura,true);
          }


          if (rigaImportabile)
            if (logger.isDebugEnabled())
              logger.debug("I dati presenti nella riga "
                  + (indiceRiga + 1)
                  + " sono nel formato previsto");

          String codiceLotto = "";
          String codiceLavorazFornitura = this.getCodvocString(valoriCampiRigaExcel.get(colonnaCodiceLavorazioneFornitura));

          if (isGaraLottiConOffertaUnica) {
            if (isCodificaAutomaticaAttiva) {
              String codiga = (String) valoriCampiRigaExcel.get(colonnaCODIGA);

              Vector<?> tmpVec = this.sqlDao.getVectorQuery(
                  "select NGARA from GARE where GARE.CODGAR1 = ? and NGARA <> ? "
                      + "and CODIGA = ? and (GARE.GENERE is null or GARE.GENERE <> 3) "
                      + "and GARE.MODLICG in (5,6,14,16) order by GARE.NGARA asc",
                  new Object[] { ngara, ngara, codiga });
              if (tmpVec != null) {
                codiceLotto = ((JdbcParametro) tmpVec.get(0)).getStringValue();
              }
            } else {
              codiceLotto = (String) valoriCampiRigaExcel.get(colonnaCodiceLotto);
            }
          } else {
            codiceLotto = ngara;// (String)
            // valoriCampiRigaExcel.get(colonnaCodiceLotto);
          }

          /*
          if (rigaImportabile && valoreContafCella!=null) {
            rigaImportabile=this.controlloCodvoc(tmpListaMsg, codiceLotto,valoreContafCella, true);
          }
          */


          if (tmpListaMsg.size() == 0) {
            if (codiceLotto != null && codiceLotto.length() > 0) {
              // Verifica che il codice lotto esista e sia relativo ad un lotto
              // con criterio di aggiudicazione di tipo offerta prezzi unitaria
              // (MODLICG = 5, 14, 16)
              boolean esisteLotto = (0 != this.geneManager.countOccorrenze(
                  "GARE", "NGARA = ? and (GENERE is null or GENERE <> 3) "
                      + "and MODLICG in (5,6,14,16)",
                  new Object[] { codiceLotto }));

              if (!esisteLotto) {
                // Il codice lotto non esiste o non e' di tipo offerta prezzi
                // (MODLICG = 5,14,16)
                loggerImport.addMessaggioErrore("La riga "
                    + (indiceRiga + 1)
                    + " non è stata importata:");
                loggerImport.addMessaggioErrore("- la cella "
                    + UtilityExcel.conversioneNumeroColonna(colonnaCodiceLotto + 1)
                    + (indiceRiga + 1)
                    + " presenta un codice lotto inesistente o "
                    + "con criterio di aggiudicazione diverso da offerta "
                    + "prezzi unitari");
                loggerImport.incrementaRecordNonImportati();
                continue;
              }

              boolean esistonoVociNonRequisitiMinimi = (0 != this.geneManager.countOccorrenze(
                  "DPRE", "NGARA = ? and REQMIN = '2' and DITTAO = ?",
                  new Object[] { codiceLotto, codiceDitta }));
              if (esistonoVociNonRequisitiMinimi) {
                // In questo caso per il lotto in esame esistono delle voci di
                // DPRE che non rispettano
                // i requisiti minimi
                loggerImport.addMessaggioErrore("La riga "
                    + (indiceRiga + 1)
                    + " non è stata importata:");
                loggerImport.addMessaggioErrore("- le informazioni in essa contenute "
                    + "si riferiscono ad un lotto contenente una o "
                    + "più voci che per la ditta in esame non rispettano "
                    + "i requisiti minimi richiesti");
                loggerImport.incrementaRecordNonAggiornati();
                continue;
              }

            }
          } else if (tmpListaMsg.size() > 0) {
            String intestazioneMsgLog = null;

            if (codiceLotto != null && codiceLotto.length() > 0) {
              // Verifica che il codice lotto esista e sia relativo ad un lotto
              // con criterio di aggiudicazione di tipo offerta prezzi unitaria
              // (MODLICG = 5, 14, 16)
              boolean esisteLotto = (0 != this.geneManager.countOccorrenze(
                  "GARE", "NGARA = ? and (GENERE is null or GENERE <> 3) "
                      + "and MODLICG in (5,6,14,16)",
                  new Object[] { codiceLotto }));

              if (esisteLotto) {
                if (codiceLavorazFornitura != null
                    && codiceLavorazFornitura.length() > 0) {
                  if (rigaImportabile) {
                    // loggerImport.addMessaggioErrore(
                    // (isGaraLottiConOffertaUnica ? "Lotto " + codiceLotto +
                    // " - " : "")
                    // + "Lavorazione o fornitura " + codiceLavorazFornitura +
                    // " (riga " + (indiceRiga + 1) +
                    // ") importata parzialmente:");
                    intestazioneMsgLog = (isGaraLottiConOffertaUnica ? "Lotto "
                        + codiceLotto
                        + " - " : "")
                        + label
                        + codiceLavorazFornitura
                        + " (riga "
                        + (indiceRiga + 1)
                        + ") " +label1 +" parzialmente:";
                    intestazioneLogGiaInserita = true;
                  } else {
                    // loggerImport.addMessaggioErrore(
                    // (isGaraLottiConOffertaUnica ? "Lotto " + codiceLotto +
                    // " - " : "")
                    // + "Lavorazione o fornitura " + codiceLavorazFornitura +
                    // " (riga " + (indiceRiga + 1) + ") non importata:");
                    intestazioneMsgLog = (isGaraLottiConOffertaUnica ? "Lotto "
                        + codiceLotto
                        + " - " : "")
                        + label
                        + codiceLavorazFornitura
                        + " (riga "
                        + (indiceRiga + 1)
                        + ") non " +label1 +":";
                    intestazioneLogGiaInserita = true;
                  }
                } else {
                  if (rigaImportabile) {
                    // loggerImport.addMessaggioErrore("La riga " + (indiceRiga
                    // + 1) +
                    // " importata parzialmente:");
                    intestazioneMsgLog = "La riga "
                        + (indiceRiga + 1)
                        + " importata parzialmente:";
                    intestazioneLogGiaInserita = true;
                  } else {
                    // loggerImport.addMessaggioErrore("La riga " + (indiceRiga
                    // + 1) +
                    // " non importata:");
                    intestazioneMsgLog = "La riga "
                        + (indiceRiga + 1)
                        + " non importata:";
                    intestazioneLogGiaInserita = true;
                  }
                }
              } else {
                // Il codice lotto non esiste o non e' di tipo offerta prezzi
                // (MODLICG = 5,14,16)
                loggerImport.addMessaggioErrore("La riga "
                    + (indiceRiga + 1)
                    + " non importata:");
                loggerImport.addMessaggioErrore("- la cella "
                    + UtilityExcel.conversioneNumeroColonna(colonnaCodiceLotto + 1)
                    + (indiceRiga + 1)
                    + " presenta un codice lotto inesistente o "
                    + "con criterio di aggiudicazione diverso da offerta "
                    + "prezzi unitari");
                intestazioneLogGiaInserita = true;
                loggerImport.incrementaRecordNonImportati();
                continue;
              }
            } else {
              if (isGaraLottiConOffertaUnica) {
                if (rigaImportabile)
                  intestazioneMsgLog = "La riga "
                      + (indiceRiga + 1)
                      + " importata parzialmente:";
                // loggerImport.addMessaggioErrore("La riga " + (indiceRiga + 1)
                // +
                // " importata parzialmente:");
                else
                  intestazioneMsgLog = "La riga "
                      + (indiceRiga + 1)
                      + " non importata:";
                // loggerImport.addMessaggioErrore("La riga " + (indiceRiga + 1)
                // +
                // " non importata:");
              } else {
                if (codiceLavorazFornitura != null
                    && codiceLavorazFornitura.length() > 0) {
                  if (rigaImportabile) {
                    // loggerImport.addMessaggioErrore(
                    // (isGaraLottiConOffertaUnica ? "Lotto " + codiceLotto +
                    // " - " : "")
                    // + "Lavorazione o fornitura " + codiceLavorazFornitura +
                    // " (riga " + (indiceRiga + 1) +
                    // ") importata parzialmente:");
                    intestazioneMsgLog = (isGaraLottiConOffertaUnica ? "Lotto "
                        + codiceLotto
                        + " - " : "")
                        + label
                        + codiceLavorazFornitura
                        + " (riga "
                        + (indiceRiga + 1)
                        + ") " + label1 + " parzialmente:";
                    intestazioneLogGiaInserita = true;
                  } else {
                    intestazioneMsgLog = (isGaraLottiConOffertaUnica ? "Lotto "
                        + codiceLotto
                        + " - " : "")
                        + label
                        + codiceLavorazFornitura
                        + " (riga "
                        + (indiceRiga + 1)
                        + ") non " + label1 + ":";
                    // loggerImport.addMessaggioErrore(
                    // (isGaraLottiConOffertaUnica ? "Lotto " + codiceLotto +
                    // " - " : "")
                    // + "Lavorazione o fornitura " + codiceLavorazFornitura +
                    // " (riga " + (indiceRiga + 1) + ") non importata:");
                    intestazioneLogGiaInserita = true;
                  }
                } else {
                  /*
                   * intestazioneMsgLog = "La riga " + (indiceRiga + 1) +
                   * " importata parzialmente:";
                   * //loggerImport.addMessaggioErrore("La riga " + (indiceRiga
                   * + 1) + // " importata parzialmente:"); else
                   * intestazioneMsgLog = "La riga " + (indiceRiga + 1) +
                   * " non importata:";
                   * //loggerImport.addMessaggioErrore("La riga " + (indiceRiga
                   * + 1) + // " non importata:");
                   */
                  if (rigaImportabile) {
                    intestazioneMsgLog = "La riga "
                        + (indiceRiga + 1)
                        + " importata parzialmente:";
                    // loggerImport.addMessaggioErrore("La riga " + (indiceRiga
                    // + 1) +
                    // " importata parzialmente:");
                    intestazioneLogGiaInserita = true;
                  } else {
                    // loggerImport.addMessaggioErrore("La riga " + (indiceRiga
                    // + 1) +
                    // " non importata:");
                    intestazioneMsgLog = "La riga "
                        + (indiceRiga + 1)
                        + " non importata:";
                    intestazioneLogGiaInserita = true;
                  }
                }
              }
            }

            loggerImport.addMessaggioErrore(intestazioneMsgLog);
            loggerImport.addListaMessaggiErrore(tmpListaMsg);

            backupTmpListaMsg = new ArrayList<String>();
            backupTmpListaMsg.add(intestazioneMsgLog);
            backupTmpListaMsg.addAll(tmpListaMsg);
            tmpListaMsg.clear();
          }

          codiceLotto = "";
          String locNgara = new String(ngara);

          if (isGaraLottiConOffertaUnica) {
            if (isCodificaAutomaticaAttiva) {
              String codiga = (String) valoriCampiRigaExcel.get(colonnaCODIGA);

              Vector<?> tmpVec = this.sqlDao.getVectorQuery(
                  "select NGARA from GARE where GARE.CODGAR1 = ? and NGARA <> ? "
                      + "and CODIGA = ? and (GARE.GENERE is null or GARE.GENERE <> 3) "
                      + "and GARE.MODLICG in (5,6,14,16) order by GARE.NGARA asc",
                  new Object[] { ngara, ngara, codiga });
              if (tmpVec != null) {
                codiceLotto = ((JdbcParametro) tmpVec.get(0)).getStringValue();
              }
            } else
              codiceLotto = (String) valoriCampiRigaExcel.get(colonnaCodiceLotto);

            // Per le gare a lotti con offerta unica la variabile locNgara
            // diventa pari al codice del lotto della riga che si sta importando
            locNgara = new String(codiceLotto);

            // Controllo dell'esistenza del codice lotto per la gara a lotti con
            // offerta unica che si sta considerando
            if (codiceLotto == null
                || (codiceLotto != null && codiceLotto.length() == 0)
                || 0 == this.geneManager.countOccorrenze("GARE",
                    "NGARA = ? and CODGAR1 = ? and (GENERE is null or GENERE <> 3) "
                        + "and MODLICG in (5,6,14,16)", new Object[] {
                        codiceLotto, ngara })) {
              // Il codice lotto non esiste o non e' di tipo offerta prezzi
              // (MODLICG = 5,14,16)
              loggerImport.addMessaggioErrore("La riga "
                  + (indiceRiga + 1)
                  + " non importata:");
              loggerImport.addMessaggioErrore("- la cella "
                  + UtilityExcel.conversioneNumeroColonna(colonnaCodiceLotto + 1)
                  + (indiceRiga + 1)
                  + " presenta un codice lotto inesistente o "
                  + "con criterio di aggiudicazione diverso da offerta "
                  + "prezzi unitari");
              loggerImport.incrementaRecordNonImportati();
              continue;
            }
          }

          boolean esisteArticoloInDPRE = false;
          if(valoreContafCella == null) {
            Vector<?> ret = sqlDao.getVectorQuery("select count(*) from DPRE, GCAP "
                + "where DPRE.NGARA = ? "
                + "and DPRE.DITTAO = ? "
                + "and DPRE.CONTAF = GCAP.CONTAF "
                + "and DPRE.NGARA = GCAP.NGARA "
                + "and GCAP.CODVOC = ? ", new Object[] { locNgara, codiceDitta,
                codiceLavorazioneFornitura });
            if (ret != null && ret.size() > 0) {
              Long a = (Long) ((JdbcParametro) ret.get(0)).getValue();
              if (a != null) esisteArticoloInDPRE = a.longValue() > 0;
            }
          }

          if (esisteArticoloInDPRE) {
            if (!intestazioneLogGiaInserita)
              loggerImport.addMessaggioErrore(label
                  + codiceLavorazioneFornitura
                  + " (riga "
                  + (indiceRiga + 1)
                  + ") non "+label1+": ");

            loggerImport.addMessaggioErrore(" - codice duplicato");
            loggerImport.incrementaRecordNonAggiornati();
            continue;
            // ATTENZIONE: il continue fa terminare l'iterazione in esecuzione
            // del ciclo for e fa iniziare quella successiva
          }

          if (rigaImportabile) {
            String sqlOccorrenzeGCAP="GCAP.NGARA = ?  and GCAP.CODVOC = ? and GCAP.DITTAO is null";
            Object par[]=null;
            if(valoreContafCella!=null){
              sqlOccorrenzeGCAP="GCAP.NGARA = ?  and GCAP.CODVOC = ? and GCAP.CONTAF=? and GCAP.DITTAO is null";
              par= new Object[3];
            }else
              par= new Object[2];

            par[0]=locNgara;
            par[1]=codiceLavorazioneFornitura;
            if(valoreContafCella!=null)
              par[2]=valoreContafCella;

            long occorrenzeGCAP = this.geneManager.countOccorrenze("GCAP",
                sqlOccorrenzeGCAP,par);

            if(valoreContafCella!=null && occorrenzeGCAP==0){
              long conteggioGCAP = this.geneManager.countOccorrenze("GCAP",
                  "GCAP.NGARA = ?  and GCAP.CODVOC = ? and GCAP.CONTAF!=? and GCAP.DITTAO is null",par);
              if(conteggioGCAP>0){
                if (!intestazioneLogGiaInserita)
                  loggerImport.addMessaggioErrore(label
                      + codiceLavorazioneFornitura
                      + " (riga "
                      + (indiceRiga + 1)
                      + ") non "+label1+": ");

                loggerImport.addMessaggioErrore(" - codice duplicato");
                loggerImport.incrementaRecordNonAggiornati();
                continue;
              }
            }

            if (occorrenzeGCAP > 0) {
              Vector<?> contatoriGCAP = this.sqlDao.getVectorQuery(
                  "select max(CONTAF), coalesce(max(NORVOC),0) from GCAP where NGARA = ?",
                  new Object[] { locNgara });
              maxGCAP_CONTAF = ((Long) ((JdbcParametro) contatoriGCAP.get(0)).getValue()).longValue();
              Object obj = ((JdbcParametro) contatoriGCAP.get(1)).getValue();
              if (obj instanceof Double)
                maxGCAP_NORVOC = ((Double) obj).doubleValue();
              else
                maxGCAP_NORVOC = new Double("" + ((Long) obj).longValue()).doubleValue();

              // Se maxGCAP_NORVOC non e' un numero intero, allora lo si
              // arrotonda
              // all'intero precedente. Prima dell'inserimento in GCAP viene
              // incrementato di 1
              if (maxGCAP_NORVOC != Math.floor(maxGCAP_NORVOC))
                maxGCAP_NORVOC = Math.floor(maxGCAP_NORVOC);

              Vector<?> campiGCAP = null;
              if(valoreContafCella==null){
                campiGCAP = this.sqlDao.getVectorQuery(
                    "select SOLSIC, SOGRIB, CLASI1, QUANTI, CONTAF, UNIMIS, VOCE, "
                        + "NORVOC, CODVOC, NUNICONF,IVAPROD, CODCAT, PERCIVA from GCAP "
                        + "where GCAP.NGARA = ? "
                        + "and GCAP.CODVOC = ? "
                        + "and GCAP.DITTAO is null "
                        + "order by CONTAF asc", new Object[] { locNgara,
                        codiceLavorazioneFornitura });
              }else{
                campiGCAP = this.sqlDao.getVectorQuery(
                    "select SOLSIC, SOGRIB, CLASI1, QUANTI, CONTAF, UNIMIS, VOCE, "
                        + "NORVOC, CODVOC, NUNICONF,IVAPROD, CODCAT, PERCIVA from GCAP "
                        + "where GCAP.NGARA = ? "
                        + "and GCAP.CODVOC = ? "
                        + "and GCAP.CONTAF = ? "
                        + "and GCAP.DITTAO is null "
                        + "order by CONTAF asc", new Object[] { locNgara,
                        codiceLavorazioneFornitura,  valoreContafCella});
              }


              String soloSicurezza = (String) ((JdbcParametro) campiGCAP.get(0)).getValue();
              String soggettoRibasso = (String) ((JdbcParametro) campiGCAP.get(1)).getValue();
              Long aCorpoMisura = (Long) ((JdbcParametro) campiGCAP.get(2)).getValue();
              Double quanti = (Double) ((JdbcParametro) campiGCAP.get(3)).getValue();
              Long contaf = (Long) ((JdbcParametro) campiGCAP.get(4)).getValue();
              String unitaMisura = (String) ((JdbcParametro) campiGCAP.get(5)).getValue();
              String descrBreve = (String) ((JdbcParametro) campiGCAP.get(6)).getValue();
              Double norvoc =  (Double)((JdbcParametro)campiGCAP.get(7)).getValue();
              String codiceIZS = (String) ((JdbcParametro) campiGCAP.get(8)).getValue();
              Double nuniconf = (Double)((JdbcParametro)campiGCAP.get(9)).getValue();
              Long ivaprod = (Long) ((JdbcParametro) campiGCAP.get(10)).getValue();
              String categoria = (String) ((JdbcParametro) campiGCAP.get(11)).getValue();
              Long perciva = (Long) ((JdbcParametro) campiGCAP.get(12)).getValue();

              if (quanti == null) quanti = new Double(0);

              // Estrazione del campo DESEST dall'entita' GCAP_EST
              if(valoreContafCella==null){
              campiGCAP = sqlDao.getVectorQuery(
                  "select GCAP_EST.DESEST from GCAP_EST, GCAP "
                      + "where GCAP_EST.NGARA = GCAP.NGARA "
                      + "and GCAP_EST.CONTAF = GCAP.CONTAF "
                      + "and GCAP.NGARA = ? "
                      + "and GCAP.CODVOC = ? "
                      + "and GCAP.DITTAO is null "
                      + "order by GCAP.CONTAF asc", new Object[] { locNgara,
                      codiceLavorazioneFornitura });
              }else{
                campiGCAP = sqlDao.getVectorQuery(
                    "select GCAP_EST.DESEST from GCAP_EST, GCAP "
                        + "where GCAP_EST.NGARA = GCAP.NGARA "
                        + "and GCAP_EST.CONTAF = GCAP.CONTAF "
                        + "and GCAP.NGARA = ? "
                        + "and GCAP.CODVOC = ? "
                        + "and GCAP.CONTAF = ? "
                        + "and GCAP.DITTAO is null "
                        + "order by GCAP.CONTAF asc", new Object[] { locNgara,
                        codiceLavorazioneFornitura, valoreContafCella});
              }

              String descrEstesa = null;
              if (campiGCAP != null)
                descrEstesa = (String) ((JdbcParametro) campiGCAP.get(0)).getValue();

              if (!("1".equals(soloSicurezza) || "1".equals(soggettoRibasso))) {
                Object parametriInsertDPRE[] = { locNgara, codiceDitta, contaf,
                    null, null, null, null };

                // Ricerca del campo "Prezzo Unitario"
                CampoImportExcel campoPrezzoUnitario = null;
                boolean campoPrezUnTrovato = false;
                // Ricerca del campo "Numero confezioni"
                CampoImportExcel campoQuantiOff = null;
                boolean campoQuantiOffTrovato = false;
                //Ricerca del campo "Numero unità per confezione"
                CampoImportExcel campoNumeroUnitaConfezione = null;
                boolean campoNumUnitaConfTrovato = false;
                // Ricerca del campo "Percentuale IVA"
                CampoImportExcel campoPercentualeIva = null;
                boolean campoPercIvaTrovato = false;

                switch (tipoFornitura.intValue()) {
                case 1:
                  for (int i = listaCampiImportExcelDPRE.size() - 1; i >= 0
                      && !campoPrezUnTrovato; i--) {
                    campoPrezzoUnitario = listaCampiImportExcelDPRE.get(i);
                    if (arrayCampi[33].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                        campoPrezzoUnitario.getNomeFisicoCampo())))
                      campoPrezUnTrovato = true;
                  }
                  break;

                case 2:
                  for (int i = listaCampiImportExcelDPRE.size() - 1; i >= 0
                      && !campoPrezUnTrovato; i--) {
                    campoPrezzoUnitario = listaCampiImportExcelDPRE.get(i);
                    if (arrayCampi[26].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                        campoPrezzoUnitario.getNomeFisicoCampo())))
                      campoPrezUnTrovato = true;
                  }
                  break;

                case 98:
                  for (int i = listaCampiImportExcelDPRE.size() - 1; i >= 0
                      && !campoPrezUnTrovato; i--) {
                    campoPrezzoUnitario = listaCampiImportExcelDPRE.get(i);
                    if (arrayCampi[15].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                        campoPrezzoUnitario.getNomeFisicoCampo())))
                      campoPrezUnTrovato = true;
                  }

                  for (int i = listaCampiImportExcelDPRE.size() - 1; i >= 0
                    && !campoQuantiOffTrovato; i--) {
                    campoQuantiOff = listaCampiImportExcelDPRE.get(i);
                    if (arrayCampi[14].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                        campoQuantiOff.getNomeFisicoCampo())))
                     campoQuantiOffTrovato = true;
                  }

                  for (int i = listaCampiImportExcelDPRE_SAN.size() - 1; i >= 0
                    && !campoNumUnitaConfTrovato; i--) {
                    campoNumeroUnitaConfezione = listaCampiImportExcelDPRE_SAN.get(i);
                    if (arrayCampi[13].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                        campoNumeroUnitaConfezione.getNomeFisicoCampo())))
                      campoNumUnitaConfTrovato = true;
                  }

                  CampoImportExcel campoImportUNIMIS = null;
                  boolean campoTrovato = false;

                  for (int l = 0; l < listaCampiImportExcelDPRE.size() && !campoTrovato; l++) {
                    campoImportUNIMIS = listaCampiImportExcelDPRE.get(l);
                    if (arrayCampi[12].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                        campoImportUNIMIS.getNomeFisicoCampo().toUpperCase())))
                          campoTrovato = true;

                  }

                  if (campoTrovato)
                    this.gestioneUnitaDiMisura(campoImportUNIMIS, valoriCampiRigaExcelDPRE,
                        loggerImport);


                  break;

                default:
                  for (int i = listaCampiImportExcel.size() - 1; i >= 0
                      && !campoPrezUnTrovato; i--) {
                    campoPrezzoUnitario = listaCampiImportExcel.get(i);
                    if (arrayCampi[12].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                        campoPrezzoUnitario.getNomeFisicoCampo())))
                      campoPrezUnTrovato = true;
                  }
                  for (int i = listaCampiImportExcel.size() - 1; i >= 0
                        && !campoPercIvaTrovato; i--) {
                    campoPercentualeIva = listaCampiImportExcel.get(i);
                    if (arrayCampi[13].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                        campoPercentualeIva.getNomeFisicoCampo())))
                       campoPercIvaTrovato = true;
                  }
                  break;
                }

                // Lettura del valore del campo "Prezzo Unitario"
                Double valorePrezzoUnitario = null;
                Double valoreQuantiOff = null;
                Double valoreNumUnitaPerConf = null;
                //String valoreUMConfezione = null;
                Double valorePercentualeIva = null;

                switch (tipoFornitura.intValue()) {
                case 1:
                case 2:
                  valorePrezzoUnitario = (Double) valoriCampiRigaExcelDPRE.get(campoPrezzoUnitario.getColonnaArrayValori());
                  break;
                case 98:
                  valoreQuantiOff = (Double) valoriCampiRigaExcelDPRE.get(campoQuantiOff.getColonnaArrayValori());
                  valorePrezzoUnitario = (Double) valoriCampiRigaExcelDPRE.get(campoPrezzoUnitario.getColonnaArrayValori());
                  valoreNumUnitaPerConf = (Double) valoriCampiRigaExcelDPRE_SAN.get(campoNumeroUnitaConfezione.getColonnaArrayValori());
                  if(valoreQuantiOff==null)valoreQuantiOff=new Double(0);
                  if(valorePrezzoUnitario==null) valorePrezzoUnitario=new Double(0);
                  if(valoreNumUnitaPerConf==null) valoreNumUnitaPerConf=new Double(0);

                  break;

                default:
                  valorePrezzoUnitario = (Double) valoriCampiRigaExcel.get(campoPrezzoUnitario.getColonnaArrayValori());
                  if(campoPercIvaTrovato){
                    valorePercentualeIva = (Double) valoriCampiRigaExcel.get(campoPercentualeIva.getColonnaArrayValori());
                  }

                  break;
                }

                if (valorePrezzoUnitario != null || "true".equals(isPrequalifica)) {
                  parametriInsertDPRE[3] = valorePrezzoUnitario;
                  Double importoOfferto = null;
                  if ("true".equals(isPrequalifica) && valorePrezzoUnitario == null) {
                    importoOfferto = null;
                    //new Double(UtilityMath.round(0, 5));
                  } else {
                    if (new Long(98).equals(tipoFornitura)) {
                      importoOfferto = new Double(
                          UtilityMath.round(valoreQuantiOff.doubleValue()
                              * valorePrezzoUnitario.doubleValue(), 5));
                    } else {
                      importoOfferto = new Double(
                          UtilityMath.round(quanti.doubleValue()
                              * valorePrezzoUnitario.doubleValue(), 5));
                    }
                    parametriInsertDPRE[4] = importoOfferto;
                  }
                  if (aCorpoMisura != null && aCorpoMisura.longValue() == 1) {
                    Double quantita = null;
                    boolean trovatoCampo = false;
                    for (int i = listaCampiImportExcel.size() - 1; i >= 0
                        && !trovatoCampo; i--) {
                      CampoImportExcel campoImport = listaCampiImportExcel.get(i);
                      if(new Long(98).equals(tipoFornitura)){
                        if (arrayCampi[10].equalsIgnoreCase(SCHEMA_CAMPI.concat(
                        ".").concat(campoImport.getNomeFisicoCampo()))) {
                      quantita = (Double) valoriCampiRigaExcel.get(campoImport.getColonnaArrayValori());
                      trovatoCampo = true;
                        }
                      } else {
                        if (arrayCampi[11].equalsIgnoreCase(SCHEMA_CAMPI.concat(
                        ".").concat(campoImport.getNomeFisicoCampo()))) {
                        	quantita = (Double) valoriCampiRigaExcel.get(campoImport.getColonnaArrayValori());
                        	trovatoCampo = true;
                        }
                      }
                    }

                    if (UtilityNumeri.confrontaDouble(quantita.doubleValue(),
                        quanti.doubleValue(), 5) != 0)
                      parametriInsertDPRE[5] = quantita;
                  }

                  parametriInsertDPRE[6] = valorePercentualeIva;

                  this.sqlDao.update(
                      "insert into DPRE (NGARA, DITTAO, CONTAF, "
                          + "PREOFF, IMPOFF, QUANTI, PERCIVA) values (?, ?, ?, ?, ?, ?, ?)",
                      parametriInsertDPRE);

                  // Aggiornamento dei valori della tabella principale DPRE
                  this.updateDPRE(listaCampiImportExcelDPRE,
                      valoriCampiRigaExcelDPRE, rigaFoglioExcel, locNgara,
                      codiceDitta, contaf);

                  // Inserimento nella tabella di estensione DPRE_SAN
                  this.insertDPRE_SAN(listaCampiImportExcelDPRE_SAN,
                      valoriCampiRigaExcelDPRE_SAN, rigaFoglioExcel, locNgara,
                      codiceDitta, contaf);

                  //Nel caso di tipoFornitura = 98 si devono aggiornare i campi QUANTIUNI e PREOFFUNI di DPRE_SAN
                  //inoltre se il valore specificato in DPRE.UNIMIS non è presente in UNIMIS, allora lo devo inserire
                  if(new Long(98).equals(tipoFornitura)){
                    Double preoffuni = null;
                    Double quantiuni = null;
                    if(valoreNumUnitaPerConf.doubleValue()!=0){
                      quantiuni = new Double(
                          UtilityMath.round(valoreNumUnitaPerConf.doubleValue()
                              * valoreQuantiOff.doubleValue(), 1));
                      preoffuni = new Double(
                          UtilityMath.round(valorePrezzoUnitario.doubleValue()
                              / valoreNumUnitaPerConf.doubleValue(), 5));
                    }

                    this.sqlDao.update("update DPRE_SAN set QUANTIUNI = ?, PREOFFUNI =? where NGARA=? and CONTAF=? and DITTAO=?",
                        new Object[]{quantiuni,preoffuni,locNgara,contaf,codiceDitta});

                  }


                  loggerImport.incrementaRecordAggiornati();

                  // Controllo che qualche campo sia stato modificato rispetto
                  // a quanto salvato nel database
                  for (int i = 0; i < listaCampiImportExcel.size(); i++) {
                    CampoImportExcel campoImportExcel = listaCampiImportExcel.get(i);
                    String letteraColonna = UtilityExcel.conversioneNumeroColonna(campoImportExcel.getColonnaCampo());
                    String tmp = null;

                    if(new Long(98).equals(tipoFornitura)){

                      //NORVOC
                      if(arrayCampi[2].equalsIgnoreCase(SCHEMA_CAMPI.concat("."
                        ).concat(campoImportExcel.getNomeFisicoCampo()))) {
                        Double doubleTmp = (Double)valoriCampiRigaExcel.get(campoImportExcel.getColonnaArrayValori());
                          if(norvoc != null){
                            if(doubleTmp != null){
                              if(UtilityNumeri.confrontaDouble(doubleTmp.doubleValue(),norvoc.doubleValue(), 3) != 0)
                                tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                              }
                            else {
                              tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                            }
                          } else {
                            if(doubleTmp != null)
                              tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                          }
                      }else if (arrayCampi[3].equalsIgnoreCase(SCHEMA_CAMPI.concat(
                         ".").concat(campoImportExcel.getNomeFisicoCampo()))) {
                        //CODVOC
                        tmp = (String) valoriCampiRigaExcel.get(campoImportExcel.getColonnaArrayValori());
                        if (codiceIZS != null && codiceIZS.length() > 0) {
                          if (tmp != null) {
                            if (!tmp.trim().equalsIgnoreCase(codiceIZS.trim()))
                              tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                          } else {
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                          }
                        } else {
                          if (tmp != null && tmp.length() > 0)
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                        }
                      }else if (arrayCampi[4].equalsIgnoreCase(SCHEMA_CAMPI.concat(
                         ".").concat(campoImportExcel.getNomeFisicoCampo()))) {
                        //Descrizione
                        tmp = (String) valoriCampiRigaExcel.get(campoImportExcel.getColonnaArrayValori());
                        if (descrBreve != null && descrBreve.length() > 0) {
                          if (tmp != null) {
                            if (!tmp.trim().equalsIgnoreCase(descrBreve.trim()))
                              tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                          } else {
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                          }
                        } else {
                          if (tmp != null && tmp.length() > 0)
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                        }
                      } else if (arrayCampi[5].equalsIgnoreCase(SCHEMA_CAMPI.concat(
                        ".").concat(campoImportExcel.getNomeFisicoCampo()))) {
                        //Note
                        tmp = (String) valoriCampiRigaExcel.get(campoImportExcel.getColonnaArrayValori());
                        if (descrEstesa != null && descrEstesa.length() > 0) {
                          if (tmp != null) {
                            if (!tmp.trim().equalsIgnoreCase(descrEstesa.trim()))
                              tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                          } else {
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                          }
                        } else {
                        }
                      } else if (arrayCampi[6].equalsIgnoreCase(
                    		  SCHEMA_CAMPI.concat(".").concat(campoImportExcel.getNomeFisicoCampo()))) {
                        tmp = (String) valoriCampiRigaExcel.get(campoImportExcel.getColonnaArrayValori());
                        //Um
                        if (unitaMisura != null && unitaMisura.length() > 0) {
                          if (tmp != null && tmp.length() > 0) {
                            Vector<?> descrUnitaMisura = this.sqlDao.getVectorQuery(
                                "select DESUNI from UNIMIS where TIPO = ? and CONTA = ?",
                                new Object[] { unitaMisura, new Long(-1) });
                            if (descrUnitaMisura != null
                                && descrUnitaMisura.size() > 0) {
                              String descrUM = (String) ((JdbcParametro) descrUnitaMisura.get(0)).getValue();
                              if (descrUM == null
                                  || (descrUM != null && descrUM.length() == 0)
                                  || (descrUM != null && descrUM.length() > 0 && !descrUM.trim().equalsIgnoreCase(
                                      tmp.trim())))
                                tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                            }
                          } else {
                            if (unitaMisura != null && unitaMisura.length() > 0)
                              tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                          }
                        } else {
                          if (tmp != null && tmp.length() > 0)
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                        }
                      } else if (arrayCampi[7].equalsIgnoreCase(
                    		  SCHEMA_CAMPI.concat(".").concat(campoImportExcel.getNomeFisicoCampo()))) {
                        //Quantità richiesta in UM
                        Double doubleTmp = (Double) valoriCampiRigaExcel.get(campoImportExcel.getColonnaArrayValori());

                        if (quanti != null) {
                          if (doubleTmp != null) {
                            if (UtilityNumeri.confrontaDouble(
                                doubleTmp.doubleValue(), quanti.doubleValue(), 5) != 0)
                              tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                          } else {
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                          }
                        } else {
                          if (doubleTmp != null)
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                        }
                      } else if (arrayCampi[8].equalsIgnoreCase(
                    		  SCHEMA_CAMPI.concat(".").concat(campoImportExcel.getNomeFisicoCampo()))) {
                        //IVA
                        Double doubleTmp = (Double) valoriCampiRigaExcel.get(campoImportExcel.getColonnaArrayValori());

                        if (ivaprod != null) {
                          if (doubleTmp != null) {
                            if (UtilityNumeri.confrontaDouble(
                                doubleTmp.doubleValue(), (new Double(ivaprod)).doubleValue(), 5) != 0)
                              tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                          } else {
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                          }
                        } else {
                          if (doubleTmp != null)
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                        }
                      } else if (arrayCampi[9].equalsIgnoreCase(
                    		  SCHEMA_CAMPI.concat(".").concat(campoImportExcel.getNomeFisicoCampo()))) {
                        //N.max unità per confezione
                        Double doubleTmp = (Double) valoriCampiRigaExcel.get(campoImportExcel.getColonnaArrayValori());

                        if (nuniconf != null) {
                          if (doubleTmp != null) {
                            if (UtilityNumeri.confrontaDouble(
                                doubleTmp.doubleValue(), nuniconf.doubleValue(), 5) != 0)
                              tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                          } else {
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                          }
                        } else {
                          if (doubleTmp != null)
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                        }
                      }

                    //Altri tipi fornitura
                    } else {
                    if (arrayCampi[4].equalsIgnoreCase(
                    		SCHEMA_CAMPI.concat(".").concat(campoImportExcel.getNomeFisicoCampo()))) {
                     tmp = (String) valoriCampiRigaExcel.get(campoImportExcel.getColonnaArrayValori());
                      if (descrBreve != null && descrBreve.length() > 0) {
                        if (tmp != null) {
                          if (!tmp.trim().equalsIgnoreCase(descrBreve.trim()))
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                        } else {
                          tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                        }
                      } else {
                        if (tmp != null && tmp.length() > 0)
                          tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                      }
                    } else if (arrayCampi[5].equalsIgnoreCase(
                    		SCHEMA_CAMPI.concat(".").concat(campoImportExcel.getNomeFisicoCampo()))) {
                      tmp = (String) valoriCampiRigaExcel.get(campoImportExcel.getColonnaArrayValori());

                      if (descrEstesa != null && descrEstesa.length() > 0) {
                        if (tmp != null) {
                          if (!tmp.trim().equalsIgnoreCase(descrEstesa.trim()))
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                        } else {
                          tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                        }
                      } else {
                        if (tmp != null && tmp.length() > 0)
                          tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                      }
                    } else if (arrayCampi[6].equalsIgnoreCase(
                    		SCHEMA_CAMPI.concat(".").concat(campoImportExcel.getNomeFisicoCampo()))) {
                      tmp = (String) valoriCampiRigaExcel.get(campoImportExcel.getColonnaArrayValori());

                      if (categoria != null && categoria.length() > 0) {
                        if (tmp != null) {
                          if (!tmp.trim().equalsIgnoreCase(categoria.trim()))
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                        } else {
                          tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                        }
                      } else {
                        if (tmp != null && tmp.length() > 0)
                          tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                      }
                    } else if (arrayCampi[7].equalsIgnoreCase(
                    		SCHEMA_CAMPI.concat(".").concat(campoImportExcel.getNomeFisicoCampo()))) {
                      //Il campo CLASI1 è numerico e quindi va trattato in quanto tale
                      //tmp = (String) valoriCampiRigaExcel.get(campoImportExcel.getColonnaArrayValori());
                      Long tmpLong = (Long) valoriCampiRigaExcel.get(campoImportExcel.getColonnaArrayValori());
                      if (aCorpoMisura != null) {
                        if (tmpLong != null) {
                          if (tmpLong.longValue() != aCorpoMisura.longValue())
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                        } else {
                          tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                        }
                      } else {
                        if (tmp != null )
                          tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                      }
                    } else if (arrayCampi[8].equalsIgnoreCase(SCHEMA_CAMPI.concat(
                        ".").concat(campoImportExcel.getNomeFisicoCampo()))) {
                      tmp = (String) valoriCampiRigaExcel.get(campoImportExcel.getColonnaArrayValori());

                      if (soloSicurezza != null && soloSicurezza.length() > 0) {
                        if (tmp != null && tmp.length() > 0) {
                          if (!tmp.trim().equalsIgnoreCase(soloSicurezza.trim()))
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                        } else
                          tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                      } else {
                        if (tmp != null && tmp.length() > 0)
                          tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                      }
                    } else if (arrayCampi[9].equalsIgnoreCase(SCHEMA_CAMPI.concat(
                        ".").concat(campoImportExcel.getNomeFisicoCampo()))) {
                      //Nuove gestione RIBCAL
                      if(nuovaGestioneRibsubAttiva){
                        String valoreExcelSogrib = valoriCampiRigaExcel.get(campoImportExcel.getColonnaArrayValori()).toString();
                        String nuovoValoreSogrib="2";
                        if(valoreExcelSogrib!=null && "2".equals(valoreExcelSogrib))
                          nuovoValoreSogrib="1";
                        valoriCampiRigaExcel.set(
                            campoImportExcel.getColonnaArrayValori(), nuovoValoreSogrib);
                      }
                      tmp = (String) valoriCampiRigaExcel.get(campoImportExcel.getColonnaArrayValori());

                      if (soggettoRibasso != null
                          && soggettoRibasso.length() > 0) {
                        if (tmp != null && tmp.length() > 0) {
                          if (!tmp.trim().equalsIgnoreCase(
                              soggettoRibasso.trim()))
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                        } else
                          tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                      } else {
                        if (tmp != null && tmp.length() > 0)
                          tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                      }
                    } else if (arrayCampi[10].equalsIgnoreCase(SCHEMA_CAMPI.concat(
                        ".").concat(campoImportExcel.getNomeFisicoCampo()))) {
                      tmp = (String) valoriCampiRigaExcel.get(campoImportExcel.getColonnaArrayValori());

                      if (unitaMisura != null && unitaMisura.length() > 0) {
                        if (tmp != null && tmp.length() > 0) {
                          Vector<?> descrUnitaMisura = this.sqlDao.getVectorQuery(
                              "select DESUNI from UNIMIS where TIPO = ? and CONTA = ?",
                              new Object[] { unitaMisura, new Long(-1) });
                          if (descrUnitaMisura != null
                              && descrUnitaMisura.size() > 0) {
                            String descrUM = (String) ((JdbcParametro) descrUnitaMisura.get(0)).getValue();
                            if (descrUM == null
                                || (descrUM != null && descrUM.length() == 0)
                                || (descrUM != null && descrUM.length() > 0 && !descrUM.trim().equalsIgnoreCase(
                                    tmp.trim())))
                              tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                          }
                        } else {
                          if (unitaMisura != null && unitaMisura.length() > 0)
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                        }
                      } else {
                        if (tmp != null && tmp.length() > 0)
                          tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                      }
                    } else if (arrayCampi[11].equalsIgnoreCase(
                    		SCHEMA_CAMPI.concat(".").concat(campoImportExcel.getNomeFisicoCampo()))
                    		&& aCorpoMisura != null && aCorpoMisura.longValue() == 3) {
                      Double doubleTmp = (Double) valoriCampiRigaExcel.get(campoImportExcel.getColonnaArrayValori());

                      if (quanti != null) {
                        if (doubleTmp != null) {
                          if (UtilityNumeri.confrontaDouble(
                              doubleTmp.doubleValue(), quanti.doubleValue(), 5) != 0)
                            tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                        } else {
                          tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                        }
                      } else {
                        if (doubleTmp != null)
                          tmpListaMsg.add(letteraColonna + (indiceRiga + 1));
                      }
                    }
                    }
                  }//ciclo for

                  if (tmpListaMsg.size() > 0) {
                    if (!intestazioneLogGiaInserita)
                      loggerImport.addMessaggioErrore((isGaraLottiConOffertaUnica
                          ? "Lotto " + codiceLotto + " - "
                          : "")
                          + label
                          + codiceLavorazioneFornitura
                          + " (riga "
                          + (indiceRiga + 1)
                          + ") "+label1+" e rilevata incongruenza dati:");

                    String strTmp = tmpListaMsg.get(0);

                    if (tmpListaMsg.size() == 1)
                      loggerImport.addMessaggioErrore(" - la cella "
                          + strTmp
                          + " risulta modificata rispetto ai dati della gara");
                    else if (tmpListaMsg.size() > 1) {
                      for (int i = 1; i < tmpListaMsg.size(); i++)
                        strTmp = strTmp.concat(", ").concat(
                            tmpListaMsg.get(i));

                      loggerImport.addMessaggioErrore(" - le celle "
                          + strTmp
                          + " risultano modificate rispetto ai dati della gara");
                    }
                    tmpListaMsg.clear();
                  }
                } else {
                  if ("false".equals(isPrequalifica)) {
                  if (!intestazioneLogGiaInserita)
                    loggerImport.addMessaggioErrore((isGaraLottiConOffertaUnica
                        ? "Lotto " + codiceLotto + " - "
                        : "")
                        + label
                        + codiceLavorazioneFornitura
                        + " (riga "
                        + (indiceRiga + 1)
                        + ") non "+label1+": ");

                  loggerImport.addMessaggioErrore(" - la cella "
                      + UtilityExcel.conversioneNumeroColonna(campoPrezzoUnitario.getColonnaCampo())
                      + (indiceRiga + 1)
                      + " non e' valorizzata");
                  loggerImport.incrementaRecordNonAggiornati();
                }
                }
              } else {
                // Cancellazione di eventuali messaggi di log di import parziale
                // o non avvenuto che andrebbero in antitesi con i messaggi di
                // non import perche' articolo/fornitura solo sicurezza o
                // soggetta
                // a ribasso
                if (backupTmpListaMsg != null && backupTmpListaMsg.size() > 0)
                  loggerImport.removeMessaggiErrore(backupTmpListaMsg);

                if ("1".equals(soloSicurezza))
                  loggerImport.addMessaggioErrore((isGaraLottiConOffertaUnica
                      ? "Lotto " + codiceLotto + " - "
                      : "")
                      + label
                      + codiceLavorazioneFornitura
                      + " (riga "
                      + (indiceRiga + 1)
                      + ") non "+label1+" perche' di tipo "
                      + "'Solo sicurezza'");
                else
                  loggerImport.addMessaggioErrore((isGaraLottiConOffertaUnica
                      ? "Lotto " + codiceLotto + " - "
                      : "")
                      + label
                      + codiceLavorazioneFornitura
                      + " (riga "
                      + (indiceRiga + 1)
                      + ") non "+label1+" perche' di tipo "
                      + "'Non soggetta a ribasso'");
                loggerImport.incrementaRecordNonAggiornati();
              }
            } else {
              CampoImportExcel campoImportUNIMIS = null;

              CampoImportExcel campoPrezUnitario = null;
              CampoImportExcel campoQuantita = null;
              CampoImportExcel campoPercIva = null;

              // Ricerca del campo "Prezzo Unitario"
              switch (tipoFornitura.intValue()) {
              case 1:
                for (int i = listaCampiImportExcelDPRE.size() - 1; i >= 0; i--) {
                  CampoImportExcel tmpCampo = listaCampiImportExcelDPRE.get(i);
                  if (arrayCampi[33].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                      tmpCampo.getNomeFisicoCampo())))
                    campoPrezUnitario = listaCampiImportExcelDPRE.get(i);
                }
                break;

              case 2:
                for (int i = listaCampiImportExcelDPRE.size() - 1; i >= 0; i--) {
                  CampoImportExcel tmpCampo = listaCampiImportExcelDPRE.get(i);
                  if (arrayCampi[26].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                      tmpCampo.getNomeFisicoCampo())))
                    campoPrezUnitario = listaCampiImportExcelDPRE.get(i);
                }
                break;



              default:
                for (int i = listaCampiImportExcel.size() - 1; i >= 0; i--) {
                  CampoImportExcel tmpCampo = listaCampiImportExcel.get(i);
                  if (arrayCampi[12].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                      tmpCampo.getNomeFisicoCampo())))
                    campoPrezUnitario = listaCampiImportExcel.get(i);
                }
                for (int i = listaCampiImportExcel.size() - 1; i >= 0; i--) {
                  CampoImportExcel tmpCampo = listaCampiImportExcel.get(i);
                  if (arrayCampi[13].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                      tmpCampo.getNomeFisicoCampo())))
                    campoPercIva = listaCampiImportExcel.get(i);
                }
                break;
              }
              // Ricerca campo "Quantita"
              for (int i = listaCampiImportExcel.size() - 1; i >= 0; i--) {
                CampoImportExcel tmpCampo = listaCampiImportExcel.get(i);
                if (arrayCampi[11].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                    tmpCampo.getNomeFisicoCampo())))
                  campoQuantita = listaCampiImportExcel.get(i);
              }


              // Lettura del valore del campo "Prezzo Unitario"
              Double valorePrezUnitario = null;
              switch (tipoFornitura.intValue()) {
              case 1:
              case 2:
                valorePrezUnitario = (Double) valoriCampiRigaExcelDPRE.get(campoPrezUnitario.getColonnaArrayValori());
                break;

              default:
                valorePrezUnitario = (Double) valoriCampiRigaExcel.get(campoPrezUnitario.getColonnaArrayValori());
                break;
              }

              // Lettura del valore del campo "Percentuale Iva"
              Double valorePercIva = null;
              if(tipoFornitura.intValue() == 3 && campoPercIva != null) {
                valorePercIva = (Double) valoriCampiRigaExcel.get(campoPercIva.getColonnaArrayValori());
              }

              List<Object> valoriCampiNuovoArticoloGCAP = new ArrayList<Object>();
              for (int i = 0; i < listaCampiImportExcel.size(); i++) {
                CampoImportExcel campoImport = listaCampiImportExcel.get(i);
                if (arrayCampi[0].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                    campoImport.getNomeFisicoCampo())))
                  valoriCampiNuovoArticoloGCAP.add(valoriCampiRigaExcel.get(campoImport.getColonnaArrayValori()));
                else if (arrayCampi[1].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                    campoImport.getNomeFisicoCampo())))
                  valoriCampiNuovoArticoloGCAP.add(valoriCampiRigaExcel.get(campoImport.getColonnaArrayValori()));
                /*
                 * else if(arrayCampi[2].equalsIgnoreCase(
                 * SCHEMA_CAMPI.concat("."
                 * ).concat(campoImport.getNomeFisicoCampo())))
                 * valoriCampiNuovoArticoloGCAP
                 * .add(valoriCampiRigaExcel.get(campoImport
                 * .getColonnaArrayValori()));
                 */
                else if (arrayCampi[2].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                    campoImport.getNomeFisicoCampo())))
                  valoriCampiNuovoArticoloGCAP.add(valoriCampiRigaExcel.get(campoImport.getColonnaArrayValori()));
                else if (arrayCampi[3].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                    campoImport.getNomeFisicoCampo())))
                  valoriCampiNuovoArticoloGCAP.add(valoriCampiRigaExcel.get(campoImport.getColonnaArrayValori()));
                else if (arrayCampi[4].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                    campoImport.getNomeFisicoCampo())))
                  valoriCampiNuovoArticoloGCAP.add(valoriCampiRigaExcel.get(campoImport.getColonnaArrayValori()));
                else if (arrayCampi[5].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                    campoImport.getNomeFisicoCampo())))
                  valoriCampiNuovoArticoloGCAP.add(valoriCampiRigaExcel.get(campoImport.getColonnaArrayValori()));// new
                // Long(3));
                else if (arrayCampi[6].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                    campoImport.getNomeFisicoCampo()))){
                    valoriCampiNuovoArticoloGCAP.add(null);
                }else if (arrayCampi[7].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                    campoImport.getNomeFisicoCampo())))
                  valoriCampiNuovoArticoloGCAP.add(new Long(3));
                else if (arrayCampi[8].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                    campoImport.getNomeFisicoCampo())))
                  valoriCampiNuovoArticoloGCAP.add(new Long(2));

                else if (arrayCampi[9].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                    campoImport.getNomeFisicoCampo()))) {
                  valoriCampiNuovoArticoloGCAP.add("2");

                } else if (arrayCampi[10].equalsIgnoreCase(SCHEMA_CAMPI.concat(
                    ".").concat(campoImport.getNomeFisicoCampo()))) {
                  valoriCampiNuovoArticoloGCAP.add(valoriCampiRigaExcel.get(campoImport.getColonnaArrayValori()));
                  campoImportUNIMIS = listaCampiImportExcel.get(i);

                } else if (arrayCampi[11].equalsIgnoreCase(SCHEMA_CAMPI.concat(
                    ".").concat(campoImport.getNomeFisicoCampo()))) {
                  valoriCampiNuovoArticoloGCAP.add(valoriCampiRigaExcel.get(campoImport.getColonnaArrayValori()));

                } else if (arrayCampi[12].equalsIgnoreCase(SCHEMA_CAMPI.concat(
                    ".").concat(campoImport.getNomeFisicoCampo()))) {
                  switch (tipoFornitura.intValue()) {
                  case 1:
                  case 2:
                    valoriCampiNuovoArticoloGCAP.add(valorePrezUnitario);
                    break;

                  default:
                    valoriCampiNuovoArticoloGCAP.add(valoriCampiRigaExcel.get(campoImport.getColonnaArrayValori()));
                    break;
                  }

                } else if(arrayCampi[13].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(campoImport.getNomeFisicoCampo()))){
                  valoriCampiNuovoArticoloGCAP.add(valorePercIva);

                }
              }

              if (campoImportUNIMIS != null)
                this.gestioneUnitaDiMisura(campoImportUNIMIS,
                    valoriCampiNuovoArticoloGCAP, loggerImport);

              String strSqlInsertGCAP = new String(
                  "insert into GCAP (CAMPI) values (VALORI)");
              String strSqlInsertGCAP_EST = new String(
                  "insert into GCAP_EST (NGARA, CONTAF, DESEST) values (?, ?, ?) ");
              String strDESEST = null;
              StringBuffer strCampi = null;
              StringBuffer strValori = null;

              if (isGaraLottiConOffertaUnica) {
                if (isCodificaAutomaticaAttiva) {
                  strCampi = new StringBuffer("CONTAF, NORVOC, NGARA, DITTAO, ");
                  strValori = new StringBuffer("?, ?, ?, ?, ");
                } else {
                  strCampi = new StringBuffer("CONTAF, NORVOC, DITTAO, ");
                  strValori = new StringBuffer("?, ?, ?, ");
                }
              } else {
                strCampi = new StringBuffer("CONTAF, NORVOC, NGARA, DITTAO, ");
                strValori = new StringBuffer("?, ?, ?, ?, ");
              }

              if (isGaraLottiConOffertaUnica) {
                Vector<?> contatoriGCAP = this.sqlDao.getVectorQuery(
                    "select max(CONTAF), coalesce(max(NORVOC),0) from GCAP where NGARA = ?",
                    new Object[] { locNgara });
                maxGCAP_CONTAF = ((Long) ((JdbcParametro) contatoriGCAP.get(0)).getValue()).longValue();
                Object obj = ((JdbcParametro) contatoriGCAP.get(1)).getValue();
                if (obj instanceof Double)
                  maxGCAP_NORVOC = ((Double) obj).doubleValue();
                else
                  maxGCAP_NORVOC = new Double("" + ((Long) obj).longValue()).doubleValue();
              }

              Object valoriCampiDPRE[] = { locNgara, codiceDitta,
                  new Long(++maxGCAP_CONTAF), null, null };

              Double quantita = (Double) valoriCampiRigaExcel.get(campoQuantita.getColonnaArrayValori());
              if (quantita == null) quantita = new Double(0);

              // Se il campo GCAP.CLASI1 non e' fra i campi da importare,
              // bisogna
              // assegnargli il valore di default ('a misura').
              boolean campoTrovato = false;
              if (!isGaraLottiConOffertaUnica) {
                for (int y = 0; y < listaCampiImportExcel.size()
                    && !campoTrovato; y++) {
                  CampoImportExcel tmpCampo = listaCampiImportExcel.get(y);
                  if (arrayCampi[7].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                      tmpCampo.getNomeFisicoCampo()))) {
                    campoTrovato = true;

                  }
                }
              }
              if (!campoTrovato) {
                strCampi.append("CLASI1, ");
                strValori.append("?, ");

                // Aggiunta del valore di default del campo GCAP.CLASI1
                // all'array
                // contenente i valori della i-esima riga del foglio Excel
                  valoriCampiNuovoArticoloGCAP.add(0, new Long(3));

              }

              int numeroColonneRimosse = 0;
              for (int l = 0; l < listaCampiImportExcel.size(); l++) {
                CampoImportExcel tmpCampo = listaCampiImportExcel.get(l);
                String tmp = tmpCampo.getNomeFisicoCampo();
                if (tmp.indexOf("CODIGA") < 0 && tmp.indexOf("CODCIG") < 0) {
                  if (tmp.indexOf("GCAP_EST") < 0) {
                    strCampi.append(tmp.substring(tmp.lastIndexOf(".") + 1).concat(", "));
                    strValori.append("?, ");
                  } else {
                    int indice = tmpCampo.getColonnaArrayValori();
                    if(!campoTrovato){
                      indice++;
                    }
                    strDESEST = (String) valoriCampiNuovoArticoloGCAP.remove(indice - numeroColonneRimosse);
                  }
                } else {
                  int indice = tmpCampo.getColonnaArrayValori();
                  if(!campoTrovato)
                    indice++;
                  valoriCampiNuovoArticoloGCAP.remove(indice - numeroColonneRimosse);
                  numeroColonneRimosse++;
                }

              }


              String stringCampi = strCampi.substring(0, strCampi.length() - 2);
              String stringValori = strValori.substring(0,
                  strValori.length() - 2);
              strSqlInsertGCAP = strSqlInsertGCAP.replaceFirst("CAMPI",
                  stringCampi);
              strSqlInsertGCAP = strSqlInsertGCAP.replaceFirst("VALORI",
                  stringValori);

              List<Object> listaValoriRiga = new ArrayList<Object>();
              listaValoriRiga.add(new Long(maxGCAP_CONTAF));
              listaValoriRiga.add(new Double(++maxGCAP_NORVOC));

              if (isGaraLottiConOffertaUnica) {
                if (isCodificaAutomaticaAttiva) {
                  listaValoriRiga.add(locNgara);
                }
              } else {
                listaValoriRiga.add(locNgara);
              }

              // if (!isGaraLottiConOffertaUnica) listaValoriRiga.add(locNgara);

              listaValoriRiga.add(codiceDitta);
              listaValoriRiga.addAll(valoriCampiNuovoArticoloGCAP);

              if (valorePrezUnitario != null || "true".equals(isPrequalifica)) {
                if (quantita != null && quantita.doubleValue() > 0) {
                  valoriCampiDPRE[3] = valorePrezUnitario;
                  if("true".equals(isPrequalifica) && valorePrezUnitario == null){
                   valoriCampiDPRE[4] = null;
                  }else{
                  valoriCampiDPRE[4] = new Double(
                      UtilityMath.round(valorePrezUnitario.doubleValue()
                          * quantita.doubleValue(), 5));
                  }
                }
                try {
                  // Inserimento nella tabella principale GCAP
                  sqlDao.update(strSqlInsertGCAP, listaValoriRiga.subList(0,
                      stringCampi.split(",").length).toArray());

                  // Salvataggio del campo DESEST nella tabella GCAP_EST
                  // (estensione della tabella GCAP)
                  if (strDESEST != null && strDESEST.length() > 0)
                    sqlDao.update(strSqlInsertGCAP_EST, new Object[] {
                        locNgara, new Long(maxGCAP_CONTAF), strDESEST });

                  // Inserimento nella tabella di estensione GCAP_SAN
                  this.insertGCAP_SAN(listaCampiImportExcelGCAP_SAN,
                      valoriCampiRigaExcelGCAP_SAN, rigaFoglioExcel, locNgara,
                      new Long(maxGCAP_CONTAF));

                  // Inserimento nella tabella principale di DPRE
                  sqlDao.update(
                      "insert into DPRE (NGARA, DITTAO, CONTAF, PREOFF, IMPOFF) values (?, ?, ?, ?, ?)",
                      valoriCampiDPRE);

                  // Aggiornamento dei valori della tabella principale DPRE
                  this.updateDPRE(listaCampiImportExcelDPRE,
                      valoriCampiRigaExcelDPRE, rigaFoglioExcel,
                      (String) valoriCampiDPRE[0], (String) valoriCampiDPRE[1],
                      (Long) valoriCampiDPRE[2]);

                  // Inserimento nella tabella di estensione DPRE_SAN
                  this.insertDPRE_SAN(listaCampiImportExcelDPRE_SAN,
                      valoriCampiRigaExcelDPRE_SAN, rigaFoglioExcel,
                      (String) valoriCampiDPRE[0], (String) valoriCampiDPRE[1],
                      (Long) valoriCampiDPRE[2]);

                  loggerImport.incrementaRecordImportati();
                  if (logger.isDebugEnabled())
                    logger.debug("Inserimento dei valori della riga "
                        + (indiceRiga + 1)
                        + " avvenuta con successo");
                } catch (SQLException s) {
                  logger.error(
                      "Errore nell'inserimento nella tabella GCAP o DPRE "
                          + "durante l'inserimento di un nuovo articolo", s);
                  throw s;
                }
                if (!intestazioneLogGiaInserita)
                  loggerImport.addMessaggioErrore((isGaraLottiConOffertaUnica
                      ? "Lotto " + codiceLotto + " - "
                      : "")
                      + "Importata nuova lavorazione o "
                      + "fornitura con codice "
                      + codiceLavorazioneFornitura
                      + " (riga "
                      + (indiceRiga + 1)
                      + ")");
              } else {
                if (!intestazioneLogGiaInserita)
                  loggerImport.addMessaggioErrore((isGaraLottiConOffertaUnica
                      ? "Lotto " + codiceLotto + " - "
                      : "")
                      + label
                      + codiceLavorazioneFornitura
                      + " (riga "
                      + (indiceRiga + 1)
                      + ") non "+label1+": ");

                loggerImport.addMessaggioErrore(" - la cella "
                    + UtilityExcel.conversioneNumeroColonna(campoPrezUnitario.getColonnaCampo())
                    + (indiceRiga + 1)
                    + " non e' valorizzata");
                loggerImport.incrementaRecordNonImportati();
              }
            }
          } else {
            loggerImport.incrementaRecordNonAggiornati();
          }
        } else {
          if (logger.isDebugEnabled())
            logger.debug("La riga "
                + (indiceRiga + 1)
                + " e' stata saltata "
                + "perche' presenta tutte e "
                + valoriCampiRigaExcel.size()
                + " le celle non valorizzate");

          contatoreRigheVuote++;
        }
      } else {
        if (logger.isDebugEnabled())
          logger.debug("La riga "
              + (indiceRiga + 1)
              + " e' stata saltata "
              + "perche' non inizializzata");

        contatoreRigheVuote++;
      }
      if (logger.isDebugEnabled())
        logger.debug("Fine lettura della riga " + (indiceRiga + 1));
    }
    long recordNonAggiornati = 0;
    if (isGaraLottiConOffertaUnica) {
      if("false".equals(isPrequalifica))
        recordNonAggiornati = this.geneManager.countOccorrenze("V_GCAP_DPRE",
          "CODGAR = ? and COD_DITTA = ? and PREOFF is null", new Object[] {
              ngara, codiceDitta });
    } else {
      recordNonAggiornati = this.geneManager.countOccorrenze("V_GCAP_DPRE",
          "NGARA = ? and COD_DITTA = ? and PREOFF is null", new Object[] {
              ngara, codiceDitta });
    }
    if (recordNonAggiornati > 0
        && loggerImport.getNumeroRecordNonAggiornati() < recordNonAggiornati) {
      do {
        loggerImport.incrementaRecordNonAggiornati();
      } while (loggerImport.getNumeroRecordNonAggiornati() < recordNonAggiornati);
    }

  //Impostazione a '2' di SOLSIC nullo
    String updateGap="update gcap set solsic='2' where ngara=? and solsic is null";
    if(isGaraLottiConOffertaUnica){
      updateGap = "update gcap set solsic='2' where ngara in (select ngara from gare where codgar1=?) and solsic is null";
    }
    sqlDao.update(updateGap, new Object[]{ngara});

    //Impostazione a '2' di SOGRIB nullo
    updateGap="update gcap set sogrib='2' where ngara=? and sogrib is null";
    if(isGaraLottiConOffertaUnica){
      updateGap = "update gcap set sogrib='2' where ngara in (select ngara from gare where codgar1=?) and sogrib is null";
    }
    sqlDao.update(updateGap, new Object[]{ngara});

    if (logger.isDebugEnabled())
      logger.debug("importOffertaPrezziDitta: fine metodo");
  }

  private void insertDPRE_SAN(List<CampoImportExcel> listaCampiImportExcelDPRE_SAN,
      List<Object> valoriCampiRigaExcelDPRE_SAN, HSSFRow rigaFoglioExcel, String ngara,
      String dittao, Long contaf) throws SQLException {

    if (listaCampiImportExcelDPRE_SAN != null
        && valoriCampiRigaExcelDPRE_SAN != null) {

      String strSqlInsertDPRE_SAN = new String(
          "insert into DPRE_SAN (CAMPI) values (VALORI)");
      StringBuffer strCampiDPRE_SAN = new StringBuffer(
          "NGARA, DITTAO, CONTAF, ");
      StringBuffer strValoriDPRE_SAN = new StringBuffer("?, ?, ?, ");

      for (int iDPRE_SAN = 0; iDPRE_SAN < valoriCampiRigaExcelDPRE_SAN.size(); iDPRE_SAN++) {
        CampoImportExcel tmpCampoDPRE_SAN = listaCampiImportExcelDPRE_SAN.get(iDPRE_SAN);
        String tmp = tmpCampoDPRE_SAN.getNomeFisicoCampo();
        strCampiDPRE_SAN.append(tmp.substring(tmp.lastIndexOf(".") + 1).concat(
            ", "));
        strValoriDPRE_SAN.append("?, ");
      }

      strSqlInsertDPRE_SAN = strSqlInsertDPRE_SAN.replaceFirst("CAMPI",
          strCampiDPRE_SAN.substring(0, strCampiDPRE_SAN.length() - 2));
      strSqlInsertDPRE_SAN = strSqlInsertDPRE_SAN.replaceFirst("VALORI",
          strValoriDPRE_SAN.substring(0, strValoriDPRE_SAN.length() - 2));
      List<Object> listaValoriRigaDPRE_SAN = new ArrayList<Object>();
      listaValoriRigaDPRE_SAN.add(ngara);
      listaValoriRigaDPRE_SAN.add(dittao);
      listaValoriRigaDPRE_SAN.add(contaf);
      listaValoriRigaDPRE_SAN.addAll(valoriCampiRigaExcelDPRE_SAN);
      this.sqlDao.update(strSqlInsertDPRE_SAN, listaValoriRigaDPRE_SAN.toArray());
    }

  }

  private void updateDPRE(List<CampoImportExcel> listaCampiImportExcelDPRE,
      List<Object> valoriCampiRigaExcelDPRE, HSSFRow rigaFoglioExcel, String ngara,
      String dittao, Long contaf) throws SQLException {

    if (listaCampiImportExcelDPRE != null && valoriCampiRigaExcelDPRE != null) {

      String strSqlUpdateDPRE = new String("update dpre set ");
      StringBuffer strCampiValoriDPRE = new StringBuffer("");

      for (int iDPRE = 0; iDPRE < valoriCampiRigaExcelDPRE.size(); iDPRE++) {
        CampoImportExcel tmpCampoDPRE = listaCampiImportExcelDPRE.get(iDPRE);
        String tmp = tmpCampoDPRE.getNomeFisicoCampo();
        strCampiValoriDPRE.append(tmp.substring(tmp.lastIndexOf(".") + 1).concat(
            "= ?, "));
      }
      strSqlUpdateDPRE += strCampiValoriDPRE.substring(0,
          strCampiValoriDPRE.length() - 2);
      strSqlUpdateDPRE += " where ngara = ? and dittao = ? and contaf = ?";
      List<Object> listaValoriRigaDPRE = new ArrayList<Object>();
      listaValoriRigaDPRE.addAll(valoriCampiRigaExcelDPRE);
      listaValoriRigaDPRE.add(ngara);
      listaValoriRigaDPRE.add(dittao);
      listaValoriRigaDPRE.add(contaf);
      this.sqlDao.update(strSqlUpdateDPRE, listaValoriRigaDPRE.toArray());
    }
  }

  private void insertGCAP_SAN(List<CampoImportExcel> listaCampiImportExcelGCAP_SAN,
      List<Object> valoriCampiRigaExcelGCAP_SAN, HSSFRow rigaFoglioExcel, String ngara,
      Long contaf) throws SQLException {

    if (listaCampiImportExcelGCAP_SAN != null
        && valoriCampiRigaExcelGCAP_SAN != null) {

      String strSqlInsertGCAP_SAN = new String(
          "insert into GCAP_SAN (CAMPI) values (VALORI)");
      StringBuffer strCampiGCAP_SAN = new StringBuffer("NGARA, CONTAF, ");
      StringBuffer strValoriGCAP_SAN = new StringBuffer("?, ?, ");

      for (int iGCAP_SAN = 0; iGCAP_SAN < valoriCampiRigaExcelGCAP_SAN.size(); iGCAP_SAN++) {
        CampoImportExcel tmpCampoGCAP_SAN = listaCampiImportExcelGCAP_SAN.get(iGCAP_SAN);
        String tmp = tmpCampoGCAP_SAN.getNomeFisicoCampo();
        strCampiGCAP_SAN.append(tmp.substring(tmp.lastIndexOf(".") + 1).concat(
            ", "));
        strValoriGCAP_SAN.append("?, ");
      }

      strSqlInsertGCAP_SAN = strSqlInsertGCAP_SAN.replaceFirst("CAMPI",
          strCampiGCAP_SAN.substring(0, strCampiGCAP_SAN.length() - 2));
      strSqlInsertGCAP_SAN = strSqlInsertGCAP_SAN.replaceFirst("VALORI",
          strValoriGCAP_SAN.substring(0, strValoriGCAP_SAN.length() - 2));
      List<Object> listaValoriRigaGCAP_SAN = new ArrayList<Object>();
      listaValoriRigaGCAP_SAN.add(ngara);
      listaValoriRigaGCAP_SAN.add(contaf);
      listaValoriRigaGCAP_SAN.addAll(valoriCampiRigaExcelGCAP_SAN);
      this.sqlDao.update(strSqlInsertGCAP_SAN, listaValoriRigaGCAP_SAN.toArray());
    }
  }

  class CampoImportExcel {

    private Campo   campo;

    private boolean obbligatorio;

    // Indice della colonna nel foglio Excel in cui e' presente il campo:
    // colonnaCampo = 3 --> colonna C
    private int     colonnaCampo;

    // Indice della colonna nell'array che conterra' il valore del campo
    private int     colonnaArrayValori;

    private Object  valoreDiDefault;

    private List<Tabellato>    tabellatoDelCampo;

    public int getTipoCampo() {
      return campo.getTipoColonna();
    }

    public int getColonnaCampo() {
      return colonnaCampo;
    }

    public void setColonnaCampo(int colonnaCampo) {
      this.colonnaCampo = colonnaCampo;
    }

    public int getLunghezzaCampo() {
      return campo.getLunghezza();
    }

    public int getCifreDecimali() {
      return campo.getDecimali();
    }

    /**
     * @return Ritorna colonnaArrayValori.
     */
    public int getColonnaArrayValori() {
      return colonnaArrayValori;
    }

    /**
     * @param colonnaArrayValori
     *        colonnaArrayValori da settare internamente alla classe.
     */
    public void setColonnaArrayValori(int colonnaArrayValori) {
      this.colonnaArrayValori = colonnaArrayValori;
    }

    public void setObbligatorio(boolean isObbligatorio) {
      this.obbligatorio = isObbligatorio;
    }

    public boolean isObbligatorio() {
      return obbligatorio;
    }

    public Object getValoreDiDefault() {
      return valoreDiDefault;
    }

    public String getNomeFisicoCampo() {
      return campo.getNomeFisicoCampo();
    }

    public List<Tabellato> getTabellatoDelCampo() {
      return tabellatoDelCampo;
    }

    public String getDominioCampo(){
      return campo.getDominio();
    }

    public CampoImportExcel(String nomeFisicoCampo,Long tipoFornitura) {
      super();
      if (nomeFisicoCampo != null) {
        if (nomeFisicoCampo.length() > 0) {
          campo = DizionarioCampi.getInstance().getCampoByNomeFisico(
              nomeFisicoCampo.substring(nomeFisicoCampo.indexOf(".") + 1));
          colonnaCampo = -1;
          colonnaArrayValori = -1;

          if(new Long(98).equals(tipoFornitura)){
            if (nomeFisicoCampo.equalsIgnoreCase(arrayCampi[6]))
              valoreDiDefault = "3";
            if(nomeFisicoCampo.equalsIgnoreCase(arrayCampi[7]) || nomeFisicoCampo.equalsIgnoreCase(arrayCampi[8]))
              this.valoreDiDefault = "2";
          }{
            if (nomeFisicoCampo.equalsIgnoreCase(arrayCampi[5]))
              valoreDiDefault = "3";
            if(nomeFisicoCampo.equalsIgnoreCase(arrayCampi[8]) || nomeFisicoCampo.equalsIgnoreCase(arrayCampi[9]))
              this.valoreDiDefault = "2";
          }


          if(nuovaGestioneRibsubAttiva && nomeFisicoCampo.equals("GARE.GCAP.SOGRIB"))
            this.valoreDiDefault = "1";


          tabellatoDelCampo = null;
          if (campo.getCodiceTabellato() != null
              && campo.getCodiceTabellato().length() > 0) {
            List<Tabellato> listaTmp = tabellatiManager.getTabellato(campo.getCodiceTabellato());
            if (listaTmp != null && listaTmp.size() > 0) {
              tabellatoDelCampo = listaTmp;
            } else {
              tabellatoDelCampo = null;
            }
          }
        } else
          throw new IllegalArgumentException(
              "Argomento di ingresso al metodo e' stringa vuota");
      } else
        throw new NullPointerException(
            "Argomento di ingresso al metodo e' null");
    }
  }

  /**
   * Viene effettuato l'inserimento delle ditte nei lotti di una gara ad offerta unica, escludendo la gara
   * complementare
   *
   * @param mappaCodiciLotti
   *        HashMap che contiene le info sui lotti.
   * @param codgar1
   *
   *@throws GestoreException
   */
  public void aggiornamentoDitteLotti(HashMap<String,String> mappaCodiciLotti, String codgar1) throws GestoreException{
    Iterator<String> iterator = mappaCodiciLotti.keySet().iterator();

    while (iterator.hasNext()) {
      String codiga = iterator.next();
      String ngara = mappaCodiciLotti.get(codiga);
      pgManager.inizializzaDitteLottiOffertaUnica(codgar1, ngara, true);
    }

  }

  public void cancellaDati(String ngara, boolean isGaraLottiConOffertaUnica, boolean isCodificaAutomaticaAttiva)
   throws SQLException, GestoreException {

    if (isGaraLottiConOffertaUnica) {
      try {
        List<?> listaLotti = this.sqlDao.getVectorQueryForList(
            "select NGARA from GARE where GARE.CODGAR1 = ? and "
                + "(GARE.GENERE is null or GARE.GENERE <> 3)",
            new Object[] { ngara });

        if (isCodificaAutomaticaAttiva) {
          // Tenuto conto che, nel caso di gara ad offerta unica, con codifica
          // automatica attiva, la funzione di import dell'offerta prezzi lato
          // gara e' attiva solo se la gara non ha nessuna ditta associata
          // (entita' DITG), si puo' avviare direttamente la cancellazione di
          // lotti della gara
          // Cancellazione dei lotti della gara ad offerta unica, a meno della
          // occorrenza complementare in GARE e delle occorrenze che, se pur
          // presenti in entita' figlie di GARE, sono gestite come entita'
          // figlie di TORN solamente per le gare a lotti con offerta unica
          if (listaLotti != null && listaLotti.size() > 0) {
            for (int yi = 0; yi < listaLotti.size(); yi++) {
              Vector<?> tmpVect = (Vector<?>) listaLotti.get(yi);
              String numeroLotto = ((JdbcParametro) tmpVect.get(0)).getStringValue();
              // Cancellazione entita' figlie del lotto
              this.pgManager.deleteGARE(numeroLotto);
              // Cancellazione del lotto vero e proprio
              this.geneManager.deleteTabelle(new String[] { "GARE" },
                  "NGARA = ? and (GARE.GENERE is null or GARE.GENERE <> 3)",
                  new Object[] { numeroLotto });
              if (logger.isDebugEnabled()) {
                logger.debug("Cancellazione del lotto '" + numeroLotto
                    + "' della gara a lotti ad offerta unica '" + ngara + "'");
              }
            }
          }
        } else {
          if (listaLotti == null || listaLotti.size() == 0) {
            if (logger.isDebugEnabled())
              logger.debug("Errore per avvio dell'operazione di import su una "
                  + "gara ad offerta unica (con codifica automatica disattivata) "
                  + "priva di lotti definiti");
            throw new GestoreException(
                "Errore per avvio dell'operazione di "
                    + "import dalla a gara ad offerta unica (CODGAR = '"
                    + ngara
                    + "'(con codifica automatica disattivata) priva di lotti definiti",
                "importOffertaPrezzi.nessunLottoEsistente");
          } else {
            sqlDao.update("delete from GCAP where GCAP.NGARA in ("
                + "select NGARA from GARE where GARE.CODGAR1 = ? and "
                + "(GARE.GENERE is null or GARE.GENERE <> 3))",
                new Object[] { ngara });
            sqlDao.update("delete from GCAP_EST where NGARA in ("
                + "select NGARA from GARE where GARE.CODGAR1 = ? and "
                + "(GARE.GENERE is null or GARE.GENERE <> 3))",
                new Object[] { ngara });
            sqlDao.update("delete from GCAP_SAN where NGARA in ("
                + "select NGARA from GARE where GARE.CODGAR1 = ? and "
                + "(GARE.GENERE is null or GARE.GENERE <> 3))",
                new Object[] { ngara });
            sqlDao.update("delete from DPRE where DPRE.NGARA in ("
                + "select NGARA from GARE where GARE.CODGAR1 = ? and "
                + "(GARE.GENERE is null or GARE.GENERE <> 3))",
                new Object[] { ngara });
            sqlDao.update("delete from DPRE_SAN where DPRE_SAN.NGARA in ("
                + "select NGARA from GARE where GARE.CODGAR1 = ? and "
                + "(GARE.GENERE is null or GARE.GENERE <> 3))",
                new Object[] { ngara });
          }
        }
      } catch (GestoreException g) {
        throw g;
      } catch (SQLException g) {
        logger.error("Errore nella cancellazione delle occorrenze presenti " +
                "nelle tabelle GCAP, GCAP_EST, GCAP_SAN, DPRE e DPRE_SAN " +
                "relative alla gara a lotti con offerta unica '" + ngara + "'",
                g);
        throw g;
      }
    } else {
      try {
        sqlDao.update("delete from GCAP where NGARA = ?",
            new Object[] { ngara });
        sqlDao.update("delete from GCAP_SAN where NGARA = ?",
            new Object[] { ngara });
        sqlDao.update("delete from GCAP_EST where NGARA = ?",
            new Object[] { ngara });
        sqlDao.update("delete from DPRE where NGARA = ?",
            new Object[] { ngara });
        sqlDao.update("delete from DPRE_SAN where NGARA = ?",
            new Object[] { ngara });
      } catch (SQLException g) {
        logger.error("Errore nella cancellazione delle occorrenze presenti "
            + "nelle tabelle GCAP, GCAP_EST, GCAP_SAN, DPRE, DPRE_SAN "
            + "relative alla gara '" + ngara + "'", g);
        throw g;
      }
    }

  }

  /**
   * Export dell'offerta prezzi per gare OEPV su file Excel con salvataggio dello stesso nella
   * directory temporanea dell'application server. Ritorna il nome del file salvato nella
   * directory temporanea
   *
   * @param ngara
   *        codice della gara a lotto unico o del lotto di gara
   * @param isGaraLottiConOffertaUnica
   *        flaf per indicare se la gara è ad Offerta Unica
   * @param session
   *        sessione dell'utente
   * @param ribcal
   *        ribcal della gara/lotto
   * @return Ritorna il nome del file salvato nella cartella temporanea
   * @throws GestioneFileDocumentiAssociatiException
   * @throws NullPointerException
   * @throws FileNotFoundException
   * @throws IOException
   * @throws SQLException
   * @throws GestoreException
   * @throws Exception
   */
  public String exportOffertaPrezziOEPV(String ngara, boolean isGaraLottiConOffertaUnica,
      HttpSession session, String ribcal) throws GestioneFileDocumentiAssociatiException,
      FileNotFoundException, SQLException, GestoreException, IOException,
      Exception {

    String profiloAttivo = "PG_DEFAULT";
    if (session != null) {
      profiloAttivo = (String) session.getAttribute("profiloAttivo");
    }

    boolean gestioneRibassoPesato = "3".equals(ribcal);
    this.setDefinizioni(null,ngara,gestioneRibassoPesato);

    this.workBook = new HSSFWorkbook();
    // Creazione del dizionario degli stili delle celle dell'intero file Excel
    DizionarioStiliExcel dizStiliExcel = new DizionarioStiliExcel(this.workBook);

    // Scrittura sul foglio Excel dei dati generali della gara
    this.setDatiGeneraliGara(ngara, profiloAttivo, isGaraLottiConOffertaUnica,
        dizStiliExcel);

    // Scrittura sul foglio Excel dei dati dell'offerta prezzi
    this.setOffertaPrezzi(ngara, profiloAttivo, isGaraLottiConOffertaUnica,
        dizStiliExcel, gestioneRibassoPesato);

    this.workBook.setActiveSheet(this.workBook.getSheetIndex(FOGLIO_OFFERTA_PREZZI[0]));
    this.workBook.setSelectedTab(this.workBook.getSheetIndex(FOGLIO_OFFERTA_PREZZI[0]));

    // Creazione di un file temporaneo nella cartella temporanea e salvataggio
    // del suo nome in sessione nell'oggetto TempfileDeleter
    String nomeFile = ngara.toUpperCase().replaceAll("/", "_");
    nomeFile += "_" + FilenameUtils.getBaseName(NOME_FILE_EXPORT_OFFERTA_PREZZI) + "." +
        FilenameUtils.getExtension(NOME_FILE_EXPORT_OFFERTA_PREZZI);
    File tempFile = null;
    if (session == null) {
      tempFile = new File( new File(System.getProperty("java.io.tmpdir")),nomeFile);
    } else {
      tempFile = TempFileUtilities.getTempFileSenzaNumeoRandom(nomeFile,
          session);
    }
    FileOutputStream fos = new FileOutputStream(tempFile);
    // Scrittura dell'oggetto workBook nel file temporaneo
    this.workBook.write(fos);
    fos.close();

    return tempFile.getName();
    // } else
    // throw new NullPointerException("modelloNonDefinito");
  }


  /**
   * Scrittura su foglio Excel dei dati dell'offerta prezzi
   *
   * @param ngara
   * @param profiloAttivo
   * @param isGaraLottiConOffertaUnica
   * @param dizStiliExcel
   * @throws GestoreException
   * @throws SQLException
   */
  private void setOffertaPrezzi(String ngara, String profiloAttivo,
      boolean isGaraLottiConOffertaUnica, DizionarioStiliExcel dizStiliExcel, boolean gestioneRibassoPesato)
      throws GestoreException, SQLException {

    HSSFSheet foglioOffertaPrezzi=null;
    foglioOffertaPrezzi = workBook.createSheet(FOGLIO_OFFERTA_PREZZI[0]);

    boolean isCodificaAutomaticaAttiva = this.geneManager.isCodificaAutomatica(
        "GARE", "NGARA");

    GestoreProfili gestoreProfili = geneManager.getProfili();

    Long tipoGara = null;
    if (isGaraLottiConOffertaUnica) {
      /*
       * I campi standard da visualizzare sono nell'ordine: NGARA.V_GCAP_DPRE
       * o CODIGA.V_GCAP_DPRE,IMPR.NOMEST, IMPR.CFIMP, V_GAP_DPRE.VOCE, DESEST.GCAP_EST,
       * CLASI1.GCAP, SOLSIC.V_GCAP_DPRE , SOGRIB.V_GCAP_DPRE, UNIMIS.DESUNI,
       * QUANTIEFF.V_GCAP_DPRE , PREOFF.V_GCAP_DPRE .
       * L'esportazione di tali campi dipende dal profilo con cui si effettua
       * l'export
       */

      if (isCodificaAutomaticaAttiva) {
        arrayCampiVisibili[0] = false; // NGARA non visibile
        arrayIndiceColonnaCampi[0] = -1;
      } else {
        arrayCampiVisibili[1] = false; // CODIGA non visibile
        arrayIndiceColonnaCampi[1] = -1;
      }
      tipoGara = new Long(-1); // deve assumere un valore diverso da 1, in
      // quanto le gare a lotti con offerta unica non sono MAI gare per lavori,
      // ma sempre per forniture o servizi
    } else {
      /*
       * I campi standard da visualizzare sono nell'ordine: IMPR.NOMEST, IMPR.CFIMP,
       * V_GAP_DPRE.VOCE, DESEST.GCAP_EST, CLASI1.GCAP, SOLSIC.V_GCAP_DPRE ,
       * SOGRIB.V_GCAP_DPRE, UNIMIS.DESUNI, QUANTIEFF.V_GCAP_DPRE , PREOFF.V_GCAP_DPRE .
       * L'esportazione di tali campi dipende dal profilo con cui si effettua
       * l'export, tuttavia si assume che alcuni campi fondamentali, come il
       * codice articolo (GCAP.CODVOC), la quantità (GCAP.QUANTI) e il prezzo
       * GCAP.PREZUN), siano SEMPRE visibili. Il campo Importo, essendo un campo
       * calcolato, viene sempre esportato come formula
       */
      arrayCampiVisibili[0] = false; // V_GCAP_DPRE.NGARA non visibile
      arrayCampiVisibili[1] = false; // V_GCAP_DPRE.CODIGA non visibile
      tipoGara = getTipoGara(ngara);
    }

    // Determino i campi non visibili, il numero di campi visibili e aggiorno
    // il numero di colonna di ciascun campo successivo a quello che si sta
    // considerando, quando questo non e' visibile da profilo, trascurando
    // i campi SEMPRE visibili.
    // Il campo CLASI1 e' esportabile solo per gare di tipo 'Lavori'
    // (TORN.TIPGEN = 1)
    if (isGaraLottiConOffertaUnica) {
      // Per le gare con offerta unica e codifica automatica il campo CODIGA e'
      // visibile, mentre per le gare con offerta unica senza codifica
      // automatica il campo NGARA e' visibile
      /*
       * if(arrayCampiVisibili[2] == false ||
       * !gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",
       * arrayCampi[2])){ arrayCampiVisibili[2] = false;
       * arrayIndiceColonnaCampi[2] = -1; }
       */

      // Aggiornamento del numero di colonna di ciascun campo successivo
      // a quello che si sta considerando
      int hi = 1;
      if (isCodificaAutomaticaAttiva) hi = 0;
      for (; hi < arrayCampiVisibili.length; hi++)
        arrayIndiceColonnaCampi[hi]--;
    } else {
      arrayIndiceColonnaCampi[0] = -1; // NGARA non visibile
      arrayIndiceColonnaCampi[1] = -1; // CODIGA non visibile

      // Aggiornamento del numero di colonna di ciascun campo successivo
      // a quello che si sta considerando
      for (int hi = 2; hi < arrayCampiVisibili.length; hi++)
         arrayIndiceColonnaCampi[hi] = arrayIndiceColonnaCampi[hi] - 2;
    }

    for (int h = 2; h < arrayCampiVisibili.length - 1; h++) {
       if (arrayCampiVisibili[h] == false
           || (arrayCampi[h].indexOf("CLASI1") >= 0 && !"1".equals("" + tipoGara))
           || (arrayCampi[h].indexOf("PERCIVAEFF") >= 0 && !gestoreProfili.checkProtec(profiloAttivo, "COLS", "VIS",arrayCampi[h])) ) {
         arrayCampiVisibili[h] = false;
         arrayIndiceColonnaCampi[h] = -1;
         // Aggiornamento del numero di colonna di ciascun campo successivo
         // a quello che si sta considerando
           for (int hi = h + 1; hi < arrayCampiVisibili.length; hi++)
             arrayIndiceColonnaCampi[hi]--;
       }
     }

    // Set dell'indice della colonna Importo
    int indiceColonnaImporto = arrayIndiceColonnaCampi[arrayIndiceColonnaCampi.length - 1] + 1;
    indiceColonnaImporto = arrayIndiceColonnaCampi[15] + 1;
    String selectDati = null;

      if (it.eldasoft.utils.sql.comp.SqlManager.DATABASE_ORACLE.equals(SqlManager.getTipoDB())) {
        selectDati = "select V_GCAP_DPRE.NGARA, " //0
            + " V_GCAP_DPRE.CODIGA, " //1
            + " IMPR.NOMEST, " //2
            + " IMPR.CFIMP, " //3
            + " V_GCAP_DPRE.CODVOC," //4
            + " V_GCAP_DPRE.VOCE, " //5
            + " GCAP.CLASI1, " //6
            + " V_GCAP_DPRE.SOLSIC, " //7
            + " V_GCAP_DPRE.SOGRIB, " //8
            + " UNIMIS.DESUNI, " //9
            + " V_GCAP_DPRE.QUANTIEFF, " //10
            + " V_GCAP_DPRE.PREOFF, " //11
            + " V_GCAP_DPRE.IMPOFF, " //12
            + " V_GCAP_DPRE.CONTAF, " //13
            + " V_GCAP_DPRE.COD_DITTA, " //14
            + " V_GCAP_DPRE.PERCIVAEFF, " //15
            + " V_GCAP_DPRE.PERRIB, "  //16
            + " V_GCAP_DPRE.PESO, "  //17
            + " V_GCAP_DPRE.RIBPESO "  //18
            + " from  V_GCAP_DPRE, UNIMIS, DITG, IMPR, GCAP "
            + " where  V_GCAP_DPRE.UNIMIS = UNIMIS.TIPO(+) "
            + " and  V_GCAP_DPRE.NGARA = ? "
            + " and  V_GCAP_DPRE.COD_DITTA=IMPR.CODIMP "
            + " and UNIMIS.CONTA(+) = -1 "
            + " and V_GCAP_DPRE.NGARA = DITG.NGARA5 "
            + " and V_GCAP_DPRE.CODGAR = DITG.CODGAR5 "
            + " and V_GCAP_DPRE.COD_DITTA = DITG.DITTAO "
            + " and GCAP.NGARA=V_GCAP_DPRE.NGARA  "
            + " and GCAP.CONTAF=V_GCAP_DPRE.CONTAF "
            + " and (DITG.FASGAR>5 or DITG.FASGAR is null)"
            + " order by V_GCAP_DPRE.COD_DITTA, V_GCAP_DPRE.NORVOC asc, V_GCAP_DPRE.CONTAF asc  ";
      } else if (it.eldasoft.utils.sql.comp.SqlManager.DATABASE_SQL_SERVER.equals(SqlManager.getTipoDB()) ||
          it.eldasoft.utils.sql.comp.SqlManager.DATABASE_POSTGRES.equals(SqlManager.getTipoDB())) {
        selectDati = "select V_GCAP_DPRE.NGARA, " //0
            + " V_GCAP_DPRE.CODIGA, " //1
            + " IMPR.NOMEST, " //2
            + " IMPR.CFIMP, " //3
            + " V_GCAP_DPRE.CODVOC," //4
            + " V_GCAP_DPRE.VOCE, " //5
            + " GCAP.CLASI1, " //6
            + " V_GCAP_DPRE.SOLSIC, " //7
            + " V_GCAP_DPRE.SOGRIB, " //8
            + " UNIMIS_1.DESUNI, " //9
            + " V_GCAP_DPRE.QUANTIEFF, " //10
            + " V_GCAP_DPRE.PREOFF, " //11
            + " V_GCAP_DPRE.IMPOFF, " //12
            + " V_GCAP_DPRE.CONTAF, " //13
            + " V_GCAP_DPRE.COD_DITTA, " //14
            + " V_GCAP_DPRE.PERCIVAEFF, " //15
            + " V_GCAP_DPRE.PERRIB, "  //16
            + " V_GCAP_DPRE.PESO, "  //17
            + " V_GCAP_DPRE.RIBPESO "  //18
            + " from (V_GCAP_DPRE left outer join (select * from unimis where conta=-1) unimis_1 on v_gcap_dpre.unimis = unimis_1.tipo) "
            + " INNER JOIN IMPR ON (V_GCAP_DPRE.COD_DITTA=IMPR.CODIMP) "
            + " INNER JOIN DITG ON (V_GCAP_DPRE.NGARA = DITG.NGARA5 and V_GCAP_DPRE.CODGAR = DITG.CODGAR5 and V_GCAP_DPRE.COD_DITTA = DITG.DITTAO "
            + " and (DITG.FASGAR>5 or DITG.FASGAR is null)) "
            + " INNER JOIN GCAP ON ( GCAP.NGARA=V_GCAP_DPRE.NGARA and GCAP.CONTAF=V_GCAP_DPRE.CONTAF) "
            + " where V_GCAP_DPRE.NGARA = ? "
            + " order by V_GCAP_DPRE.COD_DITTA, V_GCAP_DPRE.NORVOC asc, V_GCAP_DPRE.CONTAF asc  ";
      }


    // Per le gare a lotti con offerta unica, si estraggono la lista dei lotti
    // con per cui sono stati definiti offerta prezzi e su tali
    // lotti si ripete lo stesso codice. Per gli altri tipi di gara non e'
    // necessario estrarre i lotti, perchè si estrae l'offerta prezzi
    // per ciascun lotto
    List<String> listaLottiDiGara = new ArrayList<String>();

    try {
      // Ultima riga del foglio da formattare o a cui applicare la formula per
      // il calcolo dell'importo come prodotto quantita' * prezzo unitario
      int numeroRigheDaFormattare = FOGLIO_OFFERTA_PREZZI_ULTERIORI_RIGHE_DA_FORMATTARE;

      if (isGaraLottiConOffertaUnica) {
        List<?> tmpLottiGara = this.sqlDao.getVectorQueryForList(
            "select NGARA from GARE where CODGAR1 = ? and (GENERE is null or GENERE <> 3) "
                + "and MODLICG in (5,6,14,16) order by NGARA asc",
            new Object[] { ngara });
        if (tmpLottiGara != null && tmpLottiGara.size() > 0) {
          for (int ef = 0; ef < tmpLottiGara.size(); ef++) {
            Vector<?> vet = (Vector<?>) tmpLottiGara.get(ef);
            listaLottiDiGara.add(((JdbcParametro) vet.get(0)).getStringValue());
          }
        }

        if (listaLottiDiGara.size() > 0) {
          Vector<?> tmpVector = this.sqlDao.getVectorQuery(
              "select count(V_GCAP_DPRE.NGARA) from V_GCAP_DPRE, GARE, DITG " +
                  "where V_GCAP_DPRE.NGARA = GARE.NGARA " +
                  "and GARE.CODGAR1 =? " +
                  "and (GARE.GENERE is null or GARE.GENERE <> 3) "+
                  "and GARE.MODLICG in (5,6,14,16) " +
                  "and DITG.CODGAR5=GARE.CODGAR1 " +
                  "and DITG.NGARA5= V_GCAP_DPRE.NGARA " +
                  "and DITG.DITTAO = V_GCAP_DPRE.COD_DITTA " +
                  "and (DITG.FASGAR >5 or DITG.FASGAR is null)", new Object[] { ngara });
          if (tmpVector != null && tmpVector.size() > 0) {
            Long numeroOffertaPRezzi = (Long) ((JdbcParametro) tmpVector.get(0)).getValue();
            if (numeroOffertaPRezzi != null
                && numeroOffertaPRezzi.longValue() > 0)
              numeroRigheDaFormattare += numeroOffertaPRezzi.longValue();
          }
        }
      } else {
        listaLottiDiGara.add(ngara);

        List<?> tmpListaOffertaPRezzi = this.sqlDao.getVectorQueryForList(
            selectDati, new Object[] { ngara });

        if (tmpListaOffertaPRezzi != null
            && tmpListaOffertaPRezzi.size() > 0)
          numeroRigheDaFormattare += tmpListaOffertaPRezzi.size();
      }

      // Se i campi CLASI1, SOLSIC e SOGRIB sono visibili, allora per ciascuna
      // colonna si crea la convalida dei dati, in modo che l'utente possa
      // scegliere tra i valori 'a corpo'/'a misura' per CLASI1 e 'si'/'no'
      // per i campi SOLSIC e SOGRIB

      if (isGaraLottiConOffertaUnica && isCodificaAutomaticaAttiva)
        numeroRigheDaFormattare = 10000;
      // Convalida dati per CODIGA
      if (arrayCampiVisibili[1]) {
      CellRangeAddressList addressList = new CellRangeAddressList(
            FOGLIO_OFFERTA_PREZZI_RIGA_INIZIALE - 1,
            FOGLIO_OFFERTA_PREZZI_RIGA_INIZIALE
                + numeroRigheDaFormattare - 1, arrayIndiceColonnaCampi[1] - 1,
            arrayIndiceColonnaCampi[1] - 1);
        DVConstraint validazioneCODIGA = DVConstraint.createNumericConstraint(
            DVConstraint.ValidationType.INTEGER, DVConstraint.OperatorType.BETWEEN, "0",
            "999");
        HSSFDataValidation dataValidation = new HSSFDataValidation(addressList,
            validazioneCODIGA);
        foglioOffertaPrezzi.addValidationData(dataValidation);
     }


      // Convalida dati per CLASI1
      if (arrayCampiVisibili[7]) {
        CellRangeAddressList addressList = new CellRangeAddressList(
            FOGLIO_OFFERTA_PREZZI_RIGA_INIZIALE - 1,
            FOGLIO_OFFERTA_PREZZI_RIGA_INIZIALE
                + numeroRigheDaFormattare - 1, arrayIndiceColonnaCampi[7] - 1,
            arrayIndiceColonnaCampi[7] - 1);
        HSSFDataValidation dataValidation = new HSSFDataValidation(addressList,
            DATA_VALIDATION_CONSTRAINT_CORPO_MISURA);
        dataValidation.setSuppressDropDownArrow(false);
        foglioOffertaPrezzi.addValidationData(dataValidation);
      }

      // Convalida dati per SOLSIC
      if (arrayCampiVisibili[8]) {
        CellRangeAddressList addressList = new CellRangeAddressList(
            FOGLIO_OFFERTA_PREZZI_RIGA_INIZIALE - 1,
            FOGLIO_OFFERTA_PREZZI_RIGA_INIZIALE
                + numeroRigheDaFormattare - 1, arrayIndiceColonnaCampi[8] - 1,
            arrayIndiceColonnaCampi[8] - 1);
        HSSFDataValidation dataValidation = new HSSFDataValidation(addressList,
            DATA_VALIDATION_CONSTRAINT_SI_NO);
        dataValidation.setSuppressDropDownArrow(false);
        foglioOffertaPrezzi.addValidationData(dataValidation);
      }

      // Convalida dati per SOGRIB
      if (arrayCampiVisibili[9]) {
        CellRangeAddressList addressList = new CellRangeAddressList(
            FOGLIO_OFFERTA_PREZZI_RIGA_INIZIALE - 1,
            FOGLIO_OFFERTA_PREZZI_RIGA_INIZIALE
                + numeroRigheDaFormattare - 1, arrayIndiceColonnaCampi[9] - 1,
            arrayIndiceColonnaCampi[9] - 1);
        HSSFDataValidation dataValidation = new HSSFDataValidation(addressList,
            DATA_VALIDATION_CONSTRAINT_SI_NO);
        dataValidation.setSuppressDropDownArrow(false);
        foglioOffertaPrezzi.addValidationData(dataValidation);
      }

      int indiceColonna = 1;
      DizionarioCampi dizCampi = DizionarioCampi.getInstance();
      Campo campo = null;
      HSSFRow riga = null;

      for (int ii = 0; ii < arrayCampi.length; ii++) {
        if (arrayCampiVisibili[ii])
          foglioOffertaPrezzi.setDefaultColumnStyle(
              (short) (arrayIndiceColonnaCampi[ii] - 1),
              dizStiliExcel.getStileExcel(arrayStiliCampi[ii]));
      }
      // Il formato della colonna Importo: decimale a 5 cifre con allineamento
      // a destra
      foglioOffertaPrezzi.setDefaultColumnStyle(
          (short) (indiceColonnaImporto - 1),
          dizStiliExcel.getStileExcel(DizionarioStiliExcel.DECIMALE5_ALIGN_RIGHT));

      foglioOffertaPrezzi.setPrintGridlines(true);
      // (0.77 inc <=> 2 cm)
      foglioOffertaPrezzi.setMargin(HSSFSheet.TopMargin, 0.77);
      foglioOffertaPrezzi.setMargin(HSSFSheet.BottomMargin, 0.77);
      foglioOffertaPrezzi.setMargin(HSSFSheet.LeftMargin, 0.77);
      foglioOffertaPrezzi.setMargin(HSSFSheet.RightMargin, 0.77);

      // Set delle informazioni per l'area di stampa dei dati del foglio
      HSSFPrintSetup printSetup = foglioOffertaPrezzi.getPrintSetup();
      printSetup.setLandscape(true);
      printSetup.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);

      if (isGaraLottiConOffertaUnica && isCodificaAutomaticaAttiva)
        printSetup.setScale((short) 62); // Presumo significhi zoom al 62%
      else if (isGaraLottiConOffertaUnica && !isCodificaAutomaticaAttiva)
        printSetup.setScale((short) 65); // Presumo significhi zoom al 65%
      else
        printSetup.setScale((short) 70); // Presumo significhi zoom al 70%

      // Scrittura dell'intestazione della foglio
      for (int indiceRigaIntestazione = 0; indiceRigaIntestazione < FOGLIO_OFFERTA_PREZZI_RIGA_INIZIALE; indiceRigaIntestazione++) {
        riga = foglioOffertaPrezzi.createRow(indiceRigaIntestazione);
        HSSFCellStyle stile = null;
        for (int ii = 0; ii < arrayCampi.length; ii++) {
          if (arrayCampiVisibili[ii]) {
            switch (indiceRigaIntestazione) {
            case 0:
              // Prima riga: Nomi delle colonne
              // Gestione dei colori delle intestazioni
              if (ii <= 11) {
                stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_INTESTAZIONE);
              } else {
                stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_INTESTAZIONE_ARANCIONE);
              }

              UtilityExcel.scriviCella(foglioOffertaPrezzi,
                  arrayIndiceColonnaCampi[ii], indiceRigaIntestazione + 1,
                  arrayTitoloColonne[ii], stile);
              UtilityExcel.setLarghezzaColonna(foglioOffertaPrezzi,
                  arrayIndiceColonnaCampi[ii], arrayLarghezzaColonne[ii]);
              break;
            case FOGLIO_OFFERTA_DITTE_RIGA_NOME_FISICO_CAMPI - 1:
              // Seconda riga: nomi fisici dei campi
              if (stile == null)
                stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_NASCOSTA);
              campo = dizCampi.getCampoByNomeFisico(arrayCampi[ii].substring(arrayCampi[ii].indexOf(".") + 1));
              UtilityExcel.scriviCella(foglioOffertaPrezzi,
                  arrayIndiceColonnaCampi[ii], indiceRigaIntestazione + 1,
                  SCHEMA_CAMPI.concat(".").concat(campo.getNomeFisicoCampo()),
                  stile);
              break;
            case FOGLIO_OFFERTA_PREZZI_RIGA_INIZIALE - 2:
              // Riga separatrice intestazione dai dati

              // Gestione dei colori
              stile = dizStiliExcel.getStileExcel(DizionarioStiliExcel.CELLA_SEPARATRICE);

              UtilityExcel.scriviCella(foglioOffertaPrezzi,
                  arrayIndiceColonnaCampi[ii], indiceRigaIntestazione + 1, " ",
                  stile);
              break;
            }
          }
        }


        if (indiceRigaIntestazione > 0
            && indiceRigaIntestazione < FOGLIO_OFFERTA_PREZZI_RIGA_INIZIALE - 2) {
          // Nascondo la riga del foglio
          riga.setZeroHeight(true);
        }
        else if (indiceRigaIntestazione == FOGLIO_OFFERTA_PREZZI_RIGA_INIZIALE - 2) {
          // Set altezza della riga che divide l'intestazione dai dati
          riga.setHeightInPoints(3);
        }
      }

      int indiceRiga = FOGLIO_OFFERTA_PREZZI_RIGA_INIZIALE;
      for (int ef = 0; ef < listaLottiDiGara.size(); ef++) {
        String tmpNgara = listaLottiDiGara.get(ef);

        List<?> listaOffertaPrezzi = this.sqlDao.getVectorQueryForList(
            selectDati, new Object[] { tmpNgara });

        if (listaOffertaPrezzi != null && listaOffertaPrezzi.size() > 0) {
          Map<String,String> mappaTabelleatoA1051 = this.getMappaTabellato("A1051");

          // Reset dell'indice di colonna
          indiceColonna = 1;
          for (int i = 0; i < listaOffertaPrezzi.size(); i++) {
            Vector<?> record = (Vector<?>) listaOffertaPrezzi.get(i);
            if (record != null && record.size() > 0) {
              // Campo NGARA (campo visibile se gara a lotti con offerta unica)
              if (isGaraLottiConOffertaUnica && arrayCampiVisibili[0]) {
                UtilityExcel.scriviCella(foglioOffertaPrezzi,
                    indiceColonna++, indiceRiga + i, tmpNgara, null);
              }

              // Campo CODIGA (campo visibile se gara a lotti con offerta unica
              // e con codifica automatica)
              if (isGaraLottiConOffertaUnica && isCodificaAutomaticaAttiva) {
                String codiga = SqlManager.getValueFromVectorParam( record, 1).stringValue();
                if (codiga != null && codiga.length() > 0) {
                  UtilityExcel.scriviCella(foglioOffertaPrezzi,
                      indiceColonna++, indiceRiga + i, codiga, null);
                } else
                  indiceColonna++;
              }

              // Campo NOMEST
              if (arrayCampiVisibili[2]) {
                String nomest = SqlManager.getValueFromVectorParam(
                    record, 2).toString();
                if (nomest != null && nomest.length() > 0)
                  UtilityExcel.scriviCella(foglioOffertaPrezzi,
                      indiceColonna++, indiceRiga + i, nomest, null);
                else
                  indiceColonna++;
              }

              // Campo CFIMP
              if (arrayCampiVisibili[3]) {
                String cfimp = SqlManager.getValueFromVectorParam(
                    record, 3).toString();
                if (cfimp != null && cfimp.length() > 0)
                  UtilityExcel.scriviCella(foglioOffertaPrezzi,
                      indiceColonna++, indiceRiga + i, cfimp, null);
                else
                  indiceColonna++;
              }

              // Campo CODVOC
              if (arrayCampiVisibili[4]) {
                String voce = SqlManager.getValueFromVectorParam(
                    record, 4).toString();
                if (voce != null && voce.length() > 0)
                  UtilityExcel.scriviCella(foglioOffertaPrezzi,
                      indiceColonna++, indiceRiga + i, voce, null);
                else
                  indiceColonna++;
              }

              // Campo VOCE
              if (arrayCampiVisibili[5]) {
                String voce = SqlManager.getValueFromVectorParam(
                    record, 5).toString();
                if (voce != null && voce.length() > 0)
                  UtilityExcel.scriviCella(foglioOffertaPrezzi,
                      indiceColonna++, indiceRiga + i, voce, null);
                else
                  indiceColonna++;
              }

              Long contaf = SqlManager.getValueFromVectorParam(record, 13).longValue();
              // Campo DESEST
              if (arrayCampiVisibili[6]) {
                String desest = null;
                Vector<?> res = this.sqlDao.getVectorQuery(
                    "select DESEST from GCAP_EST where NGARA = ? and CONTAF = ? ",
                    new Object[] { tmpNgara, contaf });
                if (res != null && res.size() > 0) {
                  JdbcParametro result = (JdbcParametro) res.get(0);
                  if (result != null && result.getValue() != null)
                    desest = (String) result.getValue();
                }

                if (desest != null && desest.length() > 0) {
                  if (desest.length() > 32766)
                    desest = desest.substring(0, 32759).concat(" [...]");
                  UtilityExcel.scriviCella(foglioOffertaPrezzi,
                      indiceColonna++, indiceRiga + i, desest, null);
                } else
                  indiceColonna++;
              }

              // Campo CLASI1
              if (arrayCampiVisibili[7]) {
                Long clasi1 = SqlManager.getValueFromVectorParam(record, 6).longValue();
                if (clasi1 != null)
                  UtilityExcel.scriviCella(foglioOffertaPrezzi,
                      indiceColonna++,  indiceRiga + i,
                      (mappaTabelleatoA1051.get(clasi1.toString())).toLowerCase(),
                      null);
                else
                  indiceColonna++;
              }

              // Campo SOLSIC
              String solsic = null;
              if (arrayCampiVisibili[8]) {
                solsic = SqlManager.getValueFromVectorParam(record, 7).toString();
                if (solsic != null && solsic.length() > 0)
                  UtilityExcel.scriviCella(foglioOffertaPrezzi,
                      indiceColonna++, indiceRiga + i,
                      solsic.equals("1") ? "si" : "no", null);
                else
                  indiceColonna++;
              }

              // Campo SOGRIB
              String sogrib = null;
              if (arrayCampiVisibili[9]) {
                sogrib = SqlManager.getValueFromVectorParam(record, 8).toString();
                if (sogrib != null && sogrib.length() > 0) {
                  UtilityExcel.scriviCella(foglioOffertaPrezzi,
                      indiceColonna++, indiceRiga + i,
                      sogrib.equals("1") ? "no" : "si", null);
                } else
                  indiceColonna++;
              }

              // Campo DESUNI
              if (arrayCampiVisibili[10]) {
                String desuni = SqlManager.getValueFromVectorParam(
                    record, 9).toString();
                if (desuni != null && desuni.length() > 0)
                  UtilityExcel.scriviCella(foglioOffertaPrezzi,
                      indiceColonna++, indiceRiga + i, desuni, null);
                else
                  indiceColonna++;
              }

              // Campo QUANTIEFF
              if (arrayCampiVisibili[11]) {
                Double quanti = SqlManager.getValueFromVectorParam(
                    record, 10).doubleValue();
                if (quanti != null)
                  UtilityExcel.scriviCella(foglioOffertaPrezzi,
                      indiceColonna++, indiceRiga + i, quanti, null);
                else
                  indiceColonna++;
              }

              // Campo PERRIB
              if (arrayCampiVisibili[12]) {
                Double perrib = SqlManager.getValueFromVectorParam(
                    record, 16).doubleValue();
                if (perrib != null)
                  UtilityExcel.scriviCella(foglioOffertaPrezzi,
                      indiceColonna++, indiceRiga + i, perrib, null);
                else
                  indiceColonna++;
              }

              //Campo PREOFF
              if (arrayCampiVisibili[13]) {
                Double preoff = SqlManager.getValueFromVectorParam(
                    record, 11).doubleValue();
                if (preoff != null)
                  UtilityExcel.scriviCella(foglioOffertaPrezzi,
                      indiceColonna++, indiceRiga + i, preoff, null);
                else
                  indiceColonna++;
              } else
                indiceColonna += 2;

              // Campo PERCIVAEFF
              if (arrayCampiVisibili[14]) {
                Double perciva = SqlManager.getValueFromVectorParam(
                    record, 15).doubleValue();
                if (perciva != null)
                  UtilityExcel.scriviCella(foglioOffertaPrezzi,
                      indiceColonna++, indiceRiga + i, perciva, null);
                else
                  indiceColonna++;
              }

              //Campo IMPOFF
              if (arrayCampiVisibili[15]) {
                Double impoff = SqlManager.getValueFromVectorParam(
                    record, 12).doubleValue();
                if (impoff != null)
                  UtilityExcel.scriviCella(foglioOffertaPrezzi,
                      indiceColonna++, indiceRiga + i, impoff, null);
                else
                  indiceColonna++;
              }

              // Campo PESO
              if (arrayCampiVisibili[16]) {
                Double peso = SqlManager.getValueFromVectorParam(
                    record, 17).doubleValue();
                if (peso != null)
                  UtilityExcel.scriviCella(foglioOffertaPrezzi,
                      indiceColonna++, indiceRiga + i, peso, null);
                else
                  indiceColonna++;
              }

              // Campo RIBPESO
              if (arrayCampiVisibili[17]) {
                Double ribpeso = SqlManager.getValueFromVectorParam(
                    record, 18).doubleValue();
                if (ribpeso != null)
                  UtilityExcel.scriviCella(foglioOffertaPrezzi,
                      indiceColonna++, indiceRiga + i, ribpeso, null);
                else
                  indiceColonna++;
              }

              //Gestione eventuali campi di XDPRE associati alla gara
              if (listaDatiXdpre != null && listaDatiXdpre.size() > 0) {
                for (int j=0; j<listaDatiXdpre.size(); j++) {
                  Vector<?> recordXDPRE = (Vector<?>) listaDatiXdpre.get(j);
                  if (recordXDPRE != null && recordXDPRE.size() > 0) {
                    String nomeCampo =  SqlManager.getValueFromVectorParam(recordXDPRE, 0).stringValue();
                    String tabellato = SqlManager.getValueFromVectorParam(recordXDPRE, 4).stringValue();
                    String codDitta = SqlManager.getValueFromVectorParam(record, 14).stringValue();
                    Vector<?> datoXdpre = sqlDao.getVectorQuery(
                        "select " + nomeCampo + "  from xdpre where xngara=? and xcontaf=? and xdittao = ?",
                        new Object[] { tmpNgara, contaf, codDitta});
                    if (datoXdpre != null && datoXdpre.size() > 0) {
                      JdbcParametro result = (JdbcParametro) datoXdpre.get(0);
                      if (result != null && result.getValue() != null){
                        if("DT".equals(arrayFormatoCampiXdpre[j])){
                          Date dato = (Date) result.getValue();
                          UtilityExcel.scriviCella(foglioOffertaPrezzi,
                              indiceColonna++, indiceRiga + i, UtilityDate.convertiData(dato, UtilityDate.FORMATO_GG_MM_AAAA), null);
                        } else if("TABELLATO".equals(arrayFormatoCampiXdpre[j])) {
                          Long dato = (Long) result.getValue();
                          Map<String,String> mappaTabelleatoXDPRE = this.getMappaTabellato(tabellato);
                          UtilityExcel.scriviCella(foglioOffertaPrezzi,
                              indiceColonna++,  indiceRiga + i,
                              (mappaTabelleatoXDPRE.get(dato.toString())).toLowerCase(),
                              null);
                        } else if("LONG".equals(arrayFormatoCampiXdpre[j])) {
                          Long dato = (Long) result.getValue();
                          UtilityExcel.scriviCella(foglioOffertaPrezzi,
                              indiceColonna++, indiceRiga + i, dato, null);
                        } else if("DOUBLE".equals(arrayFormatoCampiXdpre[j])) {
                          Double dato = (Double) result.getValue();
                          UtilityExcel.scriviCella(foglioOffertaPrezzi,
                              indiceColonna++, indiceRiga + i, dato, null);
                        } else if("STRING".equals(arrayFormatoCampiXdpre[j])) {
                          String dato = (String) result.getValue();
                          if (dato.length() > 32766)
                            dato = dato.substring(0, 32759).concat(" [...]");
                          UtilityExcel.scriviCella(foglioOffertaPrezzi,
                              indiceColonna++, indiceRiga + i, dato, null);
                        } else if("SN".equals(arrayFormatoCampiXdpre[j])) {
                          String dato = (String) result.getValue();
                          UtilityExcel.scriviCella(foglioOffertaPrezzi,
                            indiceColonna++, indiceRiga + i,
                            dato.equals("1") ? "si" : "no", null);
                        }
                      } else
                        indiceColonna++;
                    } else
                      indiceColonna++;
                  }
                }
              }
            }
            // Reset dell'indice di colonna
            indiceColonna = 1;
          }
          indiceRiga += listaOffertaPrezzi.size();
        }
      }
    } catch (SQLException s) {
      logger.error("Export dettaglio offerta prezzi della gara '" + ngara
          + "': errore durante l'export su Excel dello sheet 'Dettaglio "
          + "offerta prezzi'");
      throw s;
    } catch (GestoreException g) {
      logger.error("Export dettaglio offerta prezzi della gara  '" + ngara
          + "': errore durante l'export su Excel dello sheet 'Dettaglio "
          + "offerta prezzi'");
      throw g;
    }
    /*
     * Si e' deciso di tracciare su log il punto esatto in cui si e' verificato
     * l'errore, catturando l'eccezione, loggando una stringa di errore e
     * riemettendo l'eccezione stessa, visto che nella procedura di export su
     * foglio excel, lo stesso tipo di eccezione può essere emessa in punti
     * diversi.
     */
  }

  private Map<String,String> getMappaTabellato(String tabellato) {
    HashMap<String,String> hm = new HashMap<String,String>();
    List<Tabellato> listaTabellato = this.tabellatiManager.getTabellato(tabellato);
    for (int i = 0; i < listaTabellato.size(); i++) {
      Tabellato val = listaTabellato.get(i);
      hm.put(val.getTipoTabellato(), val.getDescTabellato());
    }
    return hm;
  }

  /**
  * Import del file Excel per le vaziazioni dei prezzi
  * @param fileExcel
  * @param ngara
  * @param codiceDitta
  * @param isGaraLottiConOffertaUnica
  * @param session
  * @throws FileNotFoundException
  * @throws IOException
  * @throws GestoreException
  * @throws Exception
  */
 public LoggerImportOffertaPrezzi importVariazionePrezzi(
     UploadFileForm fileExcel, String ngara, String codiceDitta,
     boolean isGaraLottiConOffertaUnica, HttpSession session)
   throws FileNotFoundException, IOException, GestoreException, Exception {

   if (logger.isDebugEnabled())
     logger.debug("importVariazionePrezzi: inizio metodo");

   boolean isCodificaAutomaticaAttiva = this.geneManager.isCodificaAutomatica(
       "GARE", "NGARA");

   Long tipoFornitura = null;
   Vector<?> datiTORN = this.sqlDao.getVectorQuery(
       "select torn.tipforn from torn, gare where torn.codgar = gare.codgar1 and gare.ngara = ?",
       new Object[] { ngara });
   if (datiTORN != null && datiTORN.size() > 0) {
     if (SqlManager.getValueFromVectorParam(datiTORN, 0) != null) {
       tipoFornitura = SqlManager.getValueFromVectorParam(datiTORN, 0).longValue();
     }
   }

   if (tipoFornitura == null) tipoFornitura = new Long(3);
   this.setDefinizioni(tipoFornitura,null,false);

   LoggerImportOffertaPrezzi loggerImport = new LoggerImportOffertaPrezzi();
   this.workBook = new HSSFWorkbook(
       fileExcel.getSelezioneFile().getInputStream());

   HSSFSheet foglioLavorazioniForniture = null;
   int indiceFoglio = -1;
   // Flag per indicare se eseguire il controllo delle informazioni preliminari
   // del foglio Excel che si sta importando: se il foglio Excel e' esportato
   // dalla funzione di export dalla pagina 'Lista delle lavorazioni'
   boolean eseguireVerifichePreliminari = true;

   //boolean nuovaGestioneRibsubAttiva = false;


   if (workBook.getSheet(FOGLIO_LAVORAZIONE_E_FORNITURE[0]) != null) {
     foglioLavorazioniForniture = workBook.getSheet(FOGLIO_LAVORAZIONE_E_FORNITURE[0]);
     indiceFoglio = 0;
   } else if (workBook.getSheet(FOGLIO_LAVORAZIONE_E_FORNITURE[1]) != null) {
     foglioLavorazioniForniture = workBook.getSheet(FOGLIO_LAVORAZIONE_E_FORNITURE[1]);
     indiceFoglio = 1;
   }

   if (foglioLavorazioniForniture != null) {

       if (!this.verifichePreliminari(ngara, foglioLavorazioniForniture,isCodificaAutomaticaAttiva)) {
         throw new GestoreException("Errore durante le verifiche preliminari "
             + "dell'operazione di import dati offerta prezzi",
             "importExportOffertaPrezzi.verifichePreliminari");
       }


     if (logger.isDebugEnabled()) {
       logger.debug("importVariazionePrezzi: inizio lettura del foglio '"
           + FOGLIO_LAVORAZIONE_E_FORNITURE[0] + "'");
     }

     // Controllo del numero di campi obbligatori su tutto il file
     Map<String,List<?>> mappa = this.getListaCampiDaImportare(foglioLavorazioniForniture,
         FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio], null);
     if (mappa != null
         && mappa.containsKey("listaNomiFisiciCampiDaImportare")
         && !mappa.containsKey("listaErrori")) {
       List<?> listaNomiFisiciCampiDaImportare = mappa.get("listaNomiFisiciCampiDaImportare");
       int numeroCampiObbligatori = -1;
       int counter = 0;

         // Per le gare a lotto unico e per le gare a lotti con offerte
         // distinte vi e' un unico campo obbligatorio: CODVOC, mentre per le
         // gare a lotti con offerta unica i campi obbligatori sono NGARA,
         // CODVOC
         if (isGaraLottiConOffertaUnica)
           numeroCampiObbligatori = 2;
         else
           numeroCampiObbligatori = 1;

         for (int i = 0; i < listaNomiFisiciCampiDaImportare.size()
             && counter < numeroCampiObbligatori; i++) {
           if (isGaraLottiConOffertaUnica
               && (arrayCampi[0].equals(listaNomiFisiciCampiDaImportare.get(i))
                       || arrayCampi[1].equals(listaNomiFisiciCampiDaImportare.get(i))))
             counter++;
           if (arrayCampi[3].equals(listaNomiFisiciCampiDaImportare.get(i)))
             counter++;

         }


       if (counter == numeroCampiObbligatori) {

         // Dati della tabella GCAP
         List<CampoImportExcel> listaCampiImportExcel = this.getListaCampiDaImportareExcel(
             foglioLavorazioniForniture, indiceFoglio, "GCAP", tipoFornitura,
             codiceDitta, "false");

         this.updateVariazionePrezzi(foglioLavorazioniForniture, ngara, codiceDitta,
             listaCampiImportExcel, isGaraLottiConOffertaUnica, session, loggerImport,
             tipoFornitura,isCodificaAutomaticaAttiva);


       } else {
         if (isGaraLottiConOffertaUnica)
           logger.error("importVariazionePrezzi: Nel foglio '"
               + FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio] + "' non sono "
               + "presenti i nomi fisici dei campi che da analisi sono SEMPRE "
               + "visibili (NGARA, CODVOC, PREZUN)");
         else
           logger.error("importVariazionePrezzi: Nel foglio '"
               + FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio] + "' non sono "
               + "presenti i nomi fisici dei campi che da analisi sono SEMPRE "
               + "visibili (CODVOC, PREZUN)");

         if (codiceDitta == null) {
           if (isGaraLottiConOffertaUnica)
             loggerImport.addMsgVerificaFoglio("Il formato del file Excel "
                 + "non e' compatibile: nel foglio '"
                 + FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio]
                 + "' non sono presenti le colonne 'Codice lotto' o 'Codice'");
           else
             loggerImport.addMsgVerificaFoglio("Il formato del file Excel "
                 + "non e' compatibile: nel foglio '"
                 + FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio]
                 + "' non e' presente la colonna 'Codice'");
         } else {
           if (isGaraLottiConOffertaUnica)
             loggerImport.addMsgVerificaFoglio("Il formato del file Excel "
                 + "non e' compatibile: nel foglio '"
                 + FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio]
                 + "' non sono presenti le colonne 'Codice lotto' o 'Codice' "
                 + "o 'Prezzo unitario'");
           else
             loggerImport.addMsgVerificaFoglio("Il formato del file Excel non "
                 + "e' compatibile: nel foglio '"
                 + FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio]
                 + "' non sono presenti la colonna 'Codice' o 'Prezzo unitario'");

         }
       }
     } else if (mappa.get("listaErrori") != null) {
       List<?> listaErrori = mappa.get("listaErrori");
       for (int l = 0; l < listaErrori.size(); l++)
         logger.error("importVariazionePrezzi: " + (String) listaErrori.get(l));

       loggerImport.addMsgVerificaFoglio("Il formato del file Excel non e' "
           + "compatibile. Si consiglia di eseguire nuovamente l'esportazione "
           + "per produrre un file in formato valido");
     } else {
       String msgFoglio = FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio];
       logger.error("Il metodo getListaCampiDaImportare ha restituito null. "
           + "Si consiglia di andare in debug importando lo stesso file Excel "
           + "che ha generato questo errore, per capire la causa.");
       loggerImport.addMsgVerificaFoglio("Si e' verificato un errore "
           + "inaspettato nella lettura dell'intestazione del foglio '"
           + msgFoglio + "'");
     }
   } else {
     String tmp = "Il formato del file Excel selezionato non e' compatibile col formato attesto";
     logger.error(tmp);
     loggerImport.addMsgVerificaFoglio(tmp);
   }

   if (logger.isDebugEnabled() && indiceFoglio>=0) {
     String msgFoglio = FOGLIO_LAVORAZIONE_E_FORNITURE[indiceFoglio];
     logger.debug("importVariazionePrezzi: fine lettura del foglio '"
         + msgFoglio + "'");
   }

   if (logger.isDebugEnabled()) {
     logger.debug("importVariazionePrezzi: fine metodo");
   }
   return loggerImport;
 }

 /**
  *
  * @param foglio
  * @param ngara
  * @param codiceDitta
  * @param listaCampiImportExcel
  * @param isGaraLottiConOffertaUnica
  * @param session
  * @param loggerImport
  * @param tipoFornitura
  * @param isCodificaAutomaticaAttiva
  * @throws SQLException
  * @throws SqlComposerException
  */
 private void updateVariazionePrezzi(HSSFSheet foglio, String ngara,
     String codiceDitta, List<CampoImportExcel> listaCampiImportExcel,
     boolean isGaraLottiConOffertaUnica, HttpSession session,
     LoggerImportOffertaPrezzi loggerImport, Long tipoFornitura,
     boolean isCodificaAutomaticaAttiva) throws SQLException, SqlComposerException {

   if (logger.isDebugEnabled())
     logger.debug("updateVariazionePrezzi: inizio metodo");

   String label = "Lavorazione o fornitura ";
   String label1 = "aggiornata";


   int colonnaCodiceLavorazioneFornitura = -1;
   int colonnaCodiceLotto = -1;
   int colonnaCODIGA = -1;
   int cCLF = 0;

   for (int colonna = 0; colonna < listaCampiImportExcel.size()
       && colonnaCodiceLavorazioneFornitura < cCLF; colonna++) {
     CampoImportExcel tmpCampo = listaCampiImportExcel.get(colonna);
     if (isGaraLottiConOffertaUnica
         && (!isCodificaAutomaticaAttiva)
         && arrayCampi[0].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
             tmpCampo.getNomeFisicoCampo())))
       colonnaCodiceLotto = tmpCampo.getColonnaArrayValori();
     else if (arrayCampi[1].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
         tmpCampo.getNomeFisicoCampo())))
       colonnaCODIGA = tmpCampo.getColonnaArrayValori();
     else if (arrayCampi[3].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
         tmpCampo.getNomeFisicoCampo())))
       colonnaCodiceLavorazioneFornitura = tmpCampo.getColonnaArrayValori();
   }

   CampoImportExcel campoPrezzoUnitario = null;
   CampoImportExcel campoQuantita = null;
   boolean campoPrezUnTrovato= false;
   boolean campoQuantitaTrovato= false;

   switch (tipoFornitura.intValue()) {
   case 1:
     for (int i = listaCampiImportExcel.size() - 1; i >= 0
         && !campoPrezUnTrovato; i--) {
       campoPrezzoUnitario = listaCampiImportExcel.get(i);
       if (arrayCampi[33].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
           campoPrezzoUnitario.getNomeFisicoCampo()))){
         campoPrezUnTrovato = true;
       }
     }
     break;

   case 2:
     for (int i = listaCampiImportExcel.size() - 1; i >= 0
         && !campoPrezUnTrovato; i--) {
       campoPrezzoUnitario = listaCampiImportExcel.get(i);
       if (arrayCampi[26].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
           campoPrezzoUnitario.getNomeFisicoCampo()))){
         campoPrezUnTrovato = true;
       }
     }
     break;

   default:
     for (int i = listaCampiImportExcel.size() - 1; i >= 0
         && !campoPrezUnTrovato; i--) {
       campoPrezzoUnitario = listaCampiImportExcel.get(i);
       if (arrayCampi[12].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
           campoPrezzoUnitario.getNomeFisicoCampo()))){
         campoPrezUnTrovato = true;
       }
     }
     for (int i = listaCampiImportExcel.size() - 1; i >= 0
         && !campoQuantitaTrovato; i--) {
       campoQuantita = listaCampiImportExcel.get(i);
       if (arrayCampi[11].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
           campoQuantita.getNomeFisicoCampo()))){
         campoQuantitaTrovato = true;
       }
     }
     break;
   }

   int indiceRiga = FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE - 1;
   int ultimaRigaValorizzata = foglio.getPhysicalNumberOfRows();

   // Per le gare NON ad offerta unica:
   // - la variabile locNgara rappresenta il codice dell'i-esimo lotto per cui
   // si sta importando l'offerta prezzi;
   // - all'inizio della procedura di import vengono determinati il valore
   // massimo dei campi GCAP.CONTAF e GCAP.NORVOC da usare per l'inserimento
   // di nuovi articoli da parte della ditta (nuove occorrenze nella GCAP);
   // mentre per le gare ad offerta unica:
   // - la variabile locNgara rappresenta il codice della gara complementare;
   // - il valore massimo dei campi GCAP.CONTAF e GCAP.NORVOC da usare per
   // l'inserimento di nuovi articoli da parte della ditta (nuove occorrenze
   // nella GCAP) viene posticipato a quando, nella lettura della i-esima
   // riga del foglio Excel, la variabile locNgara assume il codice del lotto
   // specificato nella riga che si sta per importare;

   // Contatore del numero di righe consecutive vuote, per terminare la lettura
   // del foglio Excel prima di giungere all'ultima riga inizializzata dopo che
   // sono state trovate un numero di righe vuote consecutive pari alla
   // costante IMPORT_NUMERO_RIGHE_CONSECUTIVE_VUOTE
   int contatoreRigheVuote = 0;



   for (; indiceRiga < ultimaRigaValorizzata
       && contatoreRigheVuote <= IMPORT_NUMERO_RIGHE_CONSECUTIVE_VUOTE; indiceRiga++) {
     if (logger.isDebugEnabled())
       logger.debug("Inizio lettura della riga " + (indiceRiga + 1));

     HSSFRow rigaFoglioExcel = foglio.getRow(indiceRiga);
     if (rigaFoglioExcel != null) {
       // Lista valori di GCAP
       List<Object> valoriCampiRigaExcel = this.letturaRiga(rigaFoglioExcel,
           listaCampiImportExcel);


       // Contatore del numero di celle non inizializzate
       int numeroCelleNull = 0;
       for (int h = 0; h < valoriCampiRigaExcel.size(); h++)
         if (valoriCampiRigaExcel.get(h) == null) numeroCelleNull++;

       // Se il numero di celle non inizializzate e' minore del numero di campi
       // da leggere in ciascuna riga del foglio
       if (numeroCelleNull < valoriCampiRigaExcel.size()) {
         // Reset del contatore del numero di righe vuote se diverso da zero
         if (contatoreRigheVuote > 0) contatoreRigheVuote = 0;

         // Incremento contatore numero righe lette dal foglio
         loggerImport.incrementaRigheLette();

         boolean intestazioneLogGiaInserita = false;
         boolean rigaImportabilePrezun = true;
         boolean rigaImportabileQuantita = true;
         List<String> tmpListaMsg = new ArrayList<String>();
         List<String> backupTmpListaMsg = null;

         String codiceLavorazioneFornitura = this.getCodvocString(valoriCampiRigaExcel.get(colonnaCodiceLavorazioneFornitura));

         String strCellaExcelPrezun = UtilityExcel.conversioneNumeroColonna(campoPrezzoUnitario.getColonnaCampo())
             + (indiceRiga + 1);

         String strCellaExcelQuantita = UtilityExcel.conversioneNumeroColonna(campoQuantita.getColonnaCampo())
             + (indiceRiga + 1);

         // Controllo del prezzo unitario
         /*
         String strCellaExcelPrezun = UtilityExcel.conversioneNumeroColonna(campoPrezzoUnitario.getColonnaCampo())
             + (indiceRiga + 1);
         int colonnaPrezzun = campoPrezzoUnitario.getColonnaArrayValori();
         Object valore = valoriCampiRigaExcel.get(colonnaPrezzun);
         if (valore == null) {
           if (campoPrezzoUnitario.isObbligatorio()) {
             if (campoPrezzoUnitario.getValoreDiDefault() != null) {
               valoriCampiRigaExcel.set(colonnaPrezzun, campoPrezzoUnitario.getValoreDiDefault());
             } else {
               tmpListaMsg.add("- la cella "
                   + strCellaExcelPrezun
                   + " non e' valorizzata");
               rigaImportabile = false;
             }
           }
         } else if (valore instanceof Error) {
           tmpListaMsg.add(" - la cella "
               + strCellaExcelPrezun
               + " contiene un "
               + "errore: "
               + ((Error) valore).getMessage());
         } else {
           if (!(valore instanceof Double)) {
             tmpListaMsg.add("- la cella "
                 + strCellaExcelPrezun
                 + " non e' in formato numerico.");
             valoriCampiRigaExcel.set(colonnaPrezzun, null);
             rigaImportabile = false;
           } else {
             // In Excel, la formattazione di un campo di tipo numerico
             // comporta
             // la modifica della visualizzazione della cella e non del valore
             // che effettivamente questa contiene. Ad esempio: una cella che
             // e' formattata per visualizzare al piu' 3 cifre decimali:
             // - se il valore e': 12,321 o 32,26 o 102,2 allora valore
             // visualizzato e valore contenuto coincidono;
             // - se il valore e': 12,3212 o 32,2628 allora il valore
             // visualizzato e' diverso dal valore contenuto. Per la precisione
             // i valori visualizzati sono rispettivamente: 12,321 e 32,263;
             // Questo comporta il controllo dei valori delle celle di campo in
             // formato decimale e l'eventuale arrotondamento del valore
             double valoreCampo = ((Double) valoriCampiRigaExcel.get(colonnaPrezzun)).doubleValue();
             double tmpValore = UtilityMath.round(valoreCampo,
                 campoPrezzoUnitario.getCifreDecimali());
             if (valoreCampo != tmpValore)
               valoriCampiRigaExcel.set(colonnaPrezzun, new Double(tmpValore));
           }
         }
          */
         //Controllo formato dei campi QUANTI e PREZUN
         rigaImportabilePrezun = this.controlloRigaVariazioniPrezzo(campoPrezzoUnitario, strCellaExcelPrezun, valoriCampiRigaExcel, tmpListaMsg);
         rigaImportabileQuantita = this.controlloRigaVariazioniPrezzo(campoQuantita, strCellaExcelQuantita, valoriCampiRigaExcel, tmpListaMsg);

         if (rigaImportabilePrezun && rigaImportabileQuantita)
           if (logger.isDebugEnabled())
             logger.debug("I dati presenti nella riga "
                 + (indiceRiga + 1)
                 + " sono nel formato previsto");

         String codiceLotto = "";
         String codiceLavorazFornitura = this.getCodvocString(valoriCampiRigaExcel.get(colonnaCodiceLavorazioneFornitura));

         Long valoreContafCella=null;
         //Lettura del valore di contaf memorizzato nel foglio
         HSSFCell cella = rigaFoglioExcel.getCell(this.posCampoContaf);
         if (cella != null) {
           Double valTmp = new Double(cella.getNumericCellValue());
           if(valTmp!=null)
             valoreContafCella=new Long(valTmp.longValue());
         }

         if (isGaraLottiConOffertaUnica) {
           // Il campo CODIGA nel foglio Excel e' di tipo numerico, mentre su
           // DB e' di tipo stringa: a questo punto si converte in stringa il
           // valore numerico letto dal foglio Excel

           if (isCodificaAutomaticaAttiva) {

             Object objectCodiga = valoriCampiRigaExcel.get(colonnaCODIGA);
             if(objectCodiga instanceof Long)
               valoriCampiRigaExcel.set(colonnaCODIGA, ""
                   + ((Long) objectCodiga).longValue());
             else if(objectCodiga instanceof Double)
               valoriCampiRigaExcel.set(colonnaCODIGA, ""
                   + ((Double) objectCodiga).longValue());

             String codiga = (String)valoriCampiRigaExcel.get(colonnaCODIGA);

             String selectLotti="";
             if(codiceDitta==null || "".equals(codiceDitta)){
               selectLotti= "select NGARA from GARE where GARE.CODGAR1 = ? and NGARA <> ? "
                   + "and CODIGA = ? and (GARE.GENERE is null or GARE.GENERE <> 3) "
                   + "and GARE.MODLICG in (5,6,14,16) and DITTA is not null";
             }
             else{
               selectLotti= "select NGARA from GARE where GARE.CODGAR1 = ? and NGARA <> ? "
                   + "and CODIGA = ? and (GARE.GENERE is null or GARE.GENERE <> 3) "
                   + "and GARE.MODLICG in (5,6,14,16) and (DITTA = '" + codiceDitta + "' "
                   + "or exists (select * from ditgaq,gare1 where gare1.ngara = gare.ngara "
                   + "and ditgaq.ngara = gare1.ngara and ditgaq.dittao = '"+codiceDitta+"' and gare1.aqoper = 2))";
               }
             selectLotti = selectLotti + " order by GARE.NGARA asc";

             Vector<?> tmpVec = this.sqlDao.getVectorQuery(selectLotti,
                 new Object[] { ngara, ngara, codiga });
             if (tmpVec != null) {
               codiceLotto = ((JdbcParametro) tmpVec.get(0)).getStringValue();
             }
           } else {
             codiceLotto = (String) valoriCampiRigaExcel.get(colonnaCodiceLotto);
           }
         } else {
           codiceLotto = ngara;// (String)
           // valoriCampiRigaExcel.get(colonnaCodiceLotto);
         }

         if (tmpListaMsg.size() == 0) {
           if (codiceLotto != null && codiceLotto.length() > 0) {
             // Verifica che il codice lotto esista e sia relativo ad un lotto
             // con criterio di aggiudicazione di tipo offerta prezzi unitaria
             // (MODLICG = 5, 14, 16) e aggiudicato
             String selectLotti="";
             if(codiceDitta==null || "".equals(codiceDitta)){
               selectLotti= "NGARA = ? and (GENERE is null or GENERE <> 3) "
                   + "and MODLICG in (5,6,14,16) and DITTA is not null";
               }
             else{
               selectLotti= "NGARA = ? and (GENERE is null or GENERE <> 3) "
                   + "and MODLICG in (5,6,14,16) and (DITTA ='" + codiceDitta + "' "
                   + "or exists (select * from ditgaq,gare1 where ditgaq.ngara = gare1.ngara "
                   + "and gare1.ngara = gare.ngara and ditgaq.dittao = '"+codiceDitta+"' and gare1.aqoper = 2))";
               }
             boolean esisteLotto = (0 != this.geneManager.countOccorrenze(
                 "GARE", selectLotti, new Object[] { codiceLotto }));

             if (!esisteLotto) {
               // Il codice lotto non esiste o non e' di tipo offerta prezzi
               // (MODLICG = 5,14,16)
               loggerImport.addMessaggioErrore("La riga "
                   + (indiceRiga + 1)
                   + " non è stata aggiornata:");
               loggerImport.addMessaggioErrore("- la riga presenta un codice lotto inesistente o "
                   + "con criterio di aggiudicazione diverso da offerta "
                   + "prezzi unitari o non è aggiudicato");
               loggerImport.incrementaRecordNonAggiornatiLottiNonAggiudicati();
               continue;
             }
           }
         } else if (tmpListaMsg.size() > 0) {
           String intestazioneMsgLog = null;

           if (codiceLotto != null && codiceLotto.length() > 0) {
             // Verifica che il codice lotto esista e sia relativo ad un lotto
             // con criterio di aggiudicazione di tipo offerta prezzi unitaria
             // (MODLICG = 5, 14, 16)
             String selectLotti="";
             if(codiceDitta==null || "".equals(codiceDitta)){
               selectLotti= "NGARA = ? and (GENERE is null or GENERE <> 3) "
                   + "and MODLICG in (5,6,14,16) and DITTA is not null";
               }
             else{
               selectLotti= "NGARA = ? and (GENERE is null or GENERE <> 3) "
                   + "and MODLICG in (5,6,14,16) and (DITTA ='" + codiceDitta + "' "
                   + "or exists (select * from ditgaq,gare1 where ditgaq.ngara = gare1.ngara "
                   + "and gare1.ngara = gare.ngara and ditgaq.dittao = '"+codiceDitta+"' and gare1.aqoper = 2))";
             }
             boolean esisteLotto = (0 != this.geneManager.countOccorrenze(
                 "GARE", selectLotti, new Object[] { codiceLotto }));

             if (esisteLotto) {
               if (codiceLavorazFornitura != null
                   && codiceLavorazFornitura.length() > 0) {
                 if (rigaImportabilePrezun && rigaImportabileQuantita) {
                   intestazioneMsgLog = (isGaraLottiConOffertaUnica ? "Lotto "
                       + codiceLotto
                       + " - " : "")
                       + label
                       + codiceLavorazFornitura
                       + " (riga "
                       + (indiceRiga + 1)
                       + ") " +label1 +" parzialmente:";
                   intestazioneLogGiaInserita = true;
                 } else {

                   intestazioneMsgLog = (isGaraLottiConOffertaUnica ? "Lotto "
                       + codiceLotto
                       + " - " : "")
                       + label
                       + codiceLavorazFornitura
                       + " (riga "
                       + (indiceRiga + 1)
                       + ") non " +label1 +":";
                   intestazioneLogGiaInserita = true;
                 }
               } else {
                 if (rigaImportabilePrezun && rigaImportabileQuantita) {

                   intestazioneMsgLog = "La riga "
                       + (indiceRiga + 1)
                       + " importata parzialmente:";
                   intestazioneLogGiaInserita = true;
                 } else {

                   intestazioneMsgLog = "La riga "
                       + (indiceRiga + 1)
                       + " non aggiornata:";
                   intestazioneLogGiaInserita = true;
                 }
               }
             } else {
               // Il codice lotto non esiste o non e' di tipo offerta prezzi
               // (MODLICG = 5,14,16) o non e' aggiudicato
               loggerImport.addMessaggioErrore("La riga "
                   + (indiceRiga + 1)
                   + " non aggiornata:");
               loggerImport.addMessaggioErrore("- la riga presenta un codice lotto inesistente o "
                   + "con criterio di aggiudicazione diverso da offerta "
                   + "prezzi unitari o non aggiudicato");
               intestazioneLogGiaInserita = true;
               loggerImport.incrementaRecordNonAggiornatiLottiNonAggiudicati();
               continue;
             }
           } else {
             if (isGaraLottiConOffertaUnica) {
               if (rigaImportabilePrezun && rigaImportabileQuantita)
                 intestazioneMsgLog = "La riga "
                     + (indiceRiga + 1)
                     + " importata parzialmente:";

               else
                 intestazioneMsgLog = "La riga "
                     + (indiceRiga + 1)
                     + " non aggiornata:";

             } else {
               if (codiceLavorazFornitura != null
                   && codiceLavorazFornitura.length() > 0) {
                 if (rigaImportabilePrezun && rigaImportabileQuantita) {

                   intestazioneMsgLog = (isGaraLottiConOffertaUnica ? "Lotto "
                       + codiceLotto
                       + " - " : "")
                       + label
                       + codiceLavorazFornitura
                       + " (riga "
                       + (indiceRiga + 1)
                       + ") " + label1 + " parzialmente:";
                   intestazioneLogGiaInserita = true;
                 } else {
                   intestazioneMsgLog = (isGaraLottiConOffertaUnica ? "Lotto "
                       + codiceLotto
                       + " - " : "")
                       + label
                       + codiceLavorazFornitura
                       + " (riga "
                       + (indiceRiga + 1)
                       + ") non " + label1 + ":";

                   intestazioneLogGiaInserita = true;
                 }
               } else {

                 if (rigaImportabilePrezun && rigaImportabileQuantita) {
                   intestazioneMsgLog = "La riga "
                       + (indiceRiga + 1)
                       + " importata parzialmente:";

                   intestazioneLogGiaInserita = true;
                 } else {

                   intestazioneMsgLog = "La riga "
                       + (indiceRiga + 1)
                       + " non aggiornata:";
                   intestazioneLogGiaInserita = true;
                 }
               }
             }
           }

           loggerImport.addMessaggioErrore(intestazioneMsgLog);
           loggerImport.addListaMessaggiErrore(tmpListaMsg);

           backupTmpListaMsg = new ArrayList<String>();
           backupTmpListaMsg.add(intestazioneMsgLog);
           backupTmpListaMsg.addAll(tmpListaMsg);
           tmpListaMsg.clear();
         }

         codiceLotto = "";
         String locNgara = new String(ngara);

         if (isGaraLottiConOffertaUnica) {
           if (isCodificaAutomaticaAttiva) {
             String codiga = (String) valoriCampiRigaExcel.get(colonnaCODIGA);

             String selectLotti="";
             if(codiceDitta==null || "".equals(codiceDitta)){
               selectLotti= "select NGARA from GARE where GARE.CODGAR1 = ? and NGARA <> ? "
                     + "and CODIGA = ? and (GARE.GENERE is null or GARE.GENERE <> 3) "
                     + "and GARE.MODLICG in (5,6,14,16) and DITTA is not null";
             }
             else{
               selectLotti= "select NGARA from GARE where GARE.CODGAR1 = ? and NGARA <> ? "
                     + "and CODIGA = ? and (GARE.GENERE is null or GARE.GENERE <> 3) "
                     + "and GARE.MODLICG in (5,6,14,16) and (DITTA ='" + codiceDitta + "' "
                     + "or exists (select * from ditgaq,gare1 where gare1.ngara = gare.ngara "
                     + "and ditgaq.ngara = gare.ngara and ditgaq.dittao = '"+codiceDitta+"' and gare1.aqoper = 2))";
             }
             selectLotti = selectLotti + " order by GARE.NGARA asc";
             Vector<?> tmpVec = this.sqlDao.getVectorQuery(selectLotti,
                 new Object[] { ngara, ngara, codiga });
             if (tmpVec != null) {
               codiceLotto = ((JdbcParametro) tmpVec.get(0)).getStringValue();
             }
           } else
             codiceLotto = (String) valoriCampiRigaExcel.get(colonnaCodiceLotto);

           // Per le gare a lotti con offerta unica la variabile locNgara
           // diventa pari al codice del lotto della riga che si sta importando
           locNgara = new String(codiceLotto);

           // Controllo dell'esistenza del codice lotto per la gara a lotti con
           // offerta unica che si sta considerando

           String selectLotti="";
           if(codiceDitta==null || "".equals(codiceDitta)){
             selectLotti= "NGARA = ? and CODGAR1 = ? and (GENERE is null or GENERE <> 3) "
                 + "and MODLICG in (5,6,14,16) and DITTA is not null";
           }
           else{
             selectLotti= "NGARA = ? and CODGAR1 = ? and (GENERE is null or GENERE <> 3) "
                 + "and MODLICG in (5,6,14,16) and (DITTA ='" + codiceDitta + "' "
                 + "or exists (select * from ditgaq,gare1 where ditgaq.ngara = gare1.ngara "
                 + "and gare1.ngara = gare.ngara and ditgaq.dittao = '"+codiceDitta+"' and gare1.aqoper = 2))";
           }

           if (codiceLotto == null
               || (codiceLotto != null && codiceLotto.length() == 0)
               || 0 == this.geneManager.countOccorrenze("GARE",
                   selectLotti, new Object[] {
                       codiceLotto, ngara })) {
             // Il codice lotto non esiste o non e' di tipo offerta prezzi
             // (MODLICG = 5,14,16)
             loggerImport.addMessaggioErrore("La riga "
                 + (indiceRiga + 1)
                 + " non aggiornata:");
             loggerImport.addMessaggioErrore("- la riga presenta un codice lotto inesistente o non aggiudicato o "
                 + "con criterio di aggiudicazione diverso da offerta "
                 + "prezzi unitari");
             loggerImport.incrementaRecordNonAggiornatiLottiNonAggiudicati();
             continue;
           }
         }



         if (rigaImportabilePrezun && rigaImportabileQuantita) {
           Object parametriFiltro[];
           Object parametriSelect[];
           String filtroGcap = "GCAP.NGARA = ?  and GCAP.CODVOC = ? and GCAP.DITTAO is null";
           String selectGcap = "select SOLSIC, SOGRIB, PREZUN, QUANTI, CONTAF from GCAP "
                     + "where GCAP.NGARA = ? "
                     + "and GCAP.CODVOC = ? "
                     + "and GCAP.DITTAO is null "
                     + "order by CONTAF asc";
           if(codiceDitta !=null && !"".equals(codiceDitta)){
             if(valoreContafCella!=null){
               filtroGcap = "GCAP.NGARA = ?  and GCAP.CODVOC = ? and GCAP.CONTAF=?";
               selectGcap ="select G.SOLSIC, G.SOGRIB, D.PREOFF, D.IMPOFF, G.CONTAF from GCAP G left join DPRE D "
                       + "on G.NGARA=D.NGARA and G.CONTAF=D.CONTAF and D.DITTAO = ? "
                       + "where G.NGARA = ? "
                       + "and G.CODVOC = ? "
                       + "and G.CONTAF = ? "
                       + "order by CONTAF asc";
               parametriSelect=new Object[]{codiceDitta,locNgara, codiceLavorazioneFornitura,valoreContafCella};
               parametriFiltro=new Object[]{locNgara, codiceLavorazioneFornitura,valoreContafCella};
             }else{
               filtroGcap = "GCAP.NGARA = ?  and GCAP.CODVOC = ?";
               selectGcap ="select G.SOLSIC, G.SOGRIB, D.PREOFF, D.IMPOFF, G.CONTAF from GCAP G left join DPRE D "
                       + "on G.NGARA=D.NGARA and G.CONTAF=D.CONTAF and D.DITTAO = ? "
                       + "where G.NGARA = ? "
                       + "and G.CODVOC = ? "
                       + "order by CONTAF asc";
               parametriSelect=new Object[]{codiceDitta,locNgara, codiceLavorazioneFornitura};
               parametriFiltro=new Object[]{locNgara, codiceLavorazioneFornitura};
             }
           }else{
             if(valoreContafCella==null){
               parametriSelect=new Object[]{locNgara, codiceLavorazioneFornitura};
               parametriFiltro=new Object[]{locNgara, codiceLavorazioneFornitura};
             }else{
               filtroGcap = "GCAP.NGARA = ?  and GCAP.CODVOC = ? and GCAP.DITTAO is null and GCAP.CONTAF=?";
               selectGcap = "select SOLSIC, SOGRIB, PREZUN, QUANTI, CONTAF from GCAP "
                   + "where GCAP.NGARA = ? "
                   + "and GCAP.CODVOC = ? "
                   + "and GCAP.CONTAF = ? "
                   + "and GCAP.DITTAO is null "
                   + "order by CONTAF asc";
               parametriSelect=new Object[]{locNgara, codiceLavorazioneFornitura,valoreContafCella};
               parametriFiltro=new Object[]{locNgara, codiceLavorazioneFornitura,valoreContafCella};
             }
           }

           long occorrenzeGCAP = this.geneManager.countOccorrenze("GCAP",filtroGcap, parametriFiltro);

           if (occorrenzeGCAP > 0) {
             Vector<?> campiGCAP = this.sqlDao.getVectorQuery(selectGcap, parametriSelect);

             String soloSicurezza = SqlManager.getValueFromVectorParam(campiGCAP, 0).getStringValue();
             String soggettoRibasso = SqlManager.getValueFromVectorParam(campiGCAP, 1).getStringValue();
             Double prezun = (Double) ((JdbcParametro) campiGCAP.get(2)).getValue();
             Double quanti = null;
             Double importoOffertoStoricizzato=null;
             Double importoOfferto=null;
             if(codiceDitta==null || "".equals(codiceDitta))
               quanti = (Double) ((JdbcParametro) campiGCAP.get(3)).getValue();
             else
               importoOffertoStoricizzato = (Double) ((JdbcParametro) campiGCAP.get(3)).getValue();
             Long contaf = (Long) ((JdbcParametro) campiGCAP.get(4)).getValue();

             if (!("1".equals(soloSicurezza) || "1".equals(soggettoRibasso))) {

               // Lettura del valore del campo "Prezzo Unitario"
               Double valorePrezzoUnitario = (Double) valoriCampiRigaExcel.get(campoPrezzoUnitario.getColonnaArrayValori());


               if (valorePrezzoUnitario != null) {

                 if(prezun == null || valorePrezzoUnitario.doubleValue()!=prezun.doubleValue()){
                   //Storicizazzione del prezzo corrente in GARVARPRE
                   String insertVariazione = "insert into GARVARPRE(id,ngara,contaf,prezzo,importo,numvar,dataoravar,dittao) values(?,?,?,?,?,?,?,?)";

                   if(codiceDitta==null || "".equals(codiceDitta)){
                     if(prezun == null){
                       importoOffertoStoricizzato = null;
                     }else{
                     importoOffertoStoricizzato = new Double(
                         UtilityMath.round(quanti.doubleValue()
                             * prezun.doubleValue(), 5));
                     }
                   }

                   int nextId = this.genChiaviManager.getNextId("GARVARPRE");

                   Long variazione = new Long(1);
                   Vector<?> ret = sqlDao.getVectorQuery("select count(*) from GARVARPRE where ngara=? and contaf=?", new Object[] { locNgara,contaf });
                   if (ret != null && ret.size() > 0) {
                     variazione = (Long) ((JdbcParametro) ret.get(0)).getValue();
                   }


                   this.sqlDao.update(insertVariazione, new Object[]{new Long(nextId), locNgara, contaf, prezun,
                       importoOffertoStoricizzato, new Long(variazione.longValue() + 1), new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),codiceDitta});

                   if(codiceDitta==null || "".equals(codiceDitta)){
                     //Aggiornamento GCAP.PREZUN col valore presente nella cella
                     this.sqlDao.update("update gcap set prezun=? where ngara=? and contaf=?", new Object[]{valorePrezzoUnitario,locNgara, contaf});
                   }else{
                     //Aggiornamento DPRE.PREOFF e DPRE. IMPOFF col valore presente nella cella
                     Double valoreQuantita = (Double) valoriCampiRigaExcel.get(campoQuantita.getColonnaArrayValori());
                     importoOfferto = new Double(
                         UtilityMath.round(valoreQuantita.doubleValue()
                             * valorePrezzoUnitario.doubleValue(), 5));
                     this.sqlDao.update("update dpre set preoff=?, impoff=? where ngara=? and contaf=? and dittao=?", new Object[]{valorePrezzoUnitario,importoOfferto,locNgara, contaf,codiceDitta});
                   }

                   loggerImport.incrementaRecordAggiornati();
                 }else{
                   //Non vi è stata variazione di prezzo
                   if (!intestazioneLogGiaInserita)
                     loggerImport.addMessaggioErrore((isGaraLottiConOffertaUnica
                         ? "Lotto " + codiceLotto + " - "
                         : "")
                         + label
                         + codiceLavorazioneFornitura
                         + " (riga "
                         + (indiceRiga + 1)
                         + ") non "+label1+": ");
                   loggerImport.addMessaggioErrore(" - la cella "
                       + strCellaExcelPrezun
                       + " non risulta modificata rispetto ai dati della gara");
                   loggerImport.incrementaRecordNonAggiornati();
                 }

                 if (tmpListaMsg.size() > 0) {
                   if (!intestazioneLogGiaInserita)
                     loggerImport.addMessaggioErrore((isGaraLottiConOffertaUnica
                         ? "Lotto " + codiceLotto + " - "
                         : "")
                         + label
                         + codiceLavorazioneFornitura
                         + " (riga "
                         + (indiceRiga + 1)
                         + ") "+label1+" e rilevata incongruenza dati:");

                   String strTmp = tmpListaMsg.get(0);

                   if (tmpListaMsg.size() == 1)
                     loggerImport.addMessaggioErrore(" - la cella "
                         + strCellaExcelPrezun
                         + " risulta modificata rispetto ai dati della gara");
                   else if (tmpListaMsg.size() > 1) {
                     for (int i = 1; i < tmpListaMsg.size(); i++)
                       strTmp = strTmp.concat(", ").concat(
                           tmpListaMsg.get(i));

                     loggerImport.addMessaggioErrore(" - le celle "
                         + strTmp
                         + " risultano modificate rispetto ai dati della gara");
                   }
                   tmpListaMsg.clear();
                 }
               } else {
                 //Prezzo unitario nell'Excel nullo
                 if (!intestazioneLogGiaInserita)
                   loggerImport.addMessaggioErrore((isGaraLottiConOffertaUnica
                       ? "Lotto " + codiceLotto + " - "
                       : "")
                       + label
                       + codiceLavorazioneFornitura
                       + " (riga "
                       + (indiceRiga + 1)
                       + ") non "+label1+": ");

                 loggerImport.addMessaggioErrore(" - la cella "
                     + UtilityExcel.conversioneNumeroColonna(campoPrezzoUnitario.getColonnaCampo())
                     + (indiceRiga + 1)
                     + " non e' valorizzata");
                 loggerImport.incrementaRecordNonAggiornati();

               }
             } else{
               //Riga relative a lavorazioni di tipo 'Solo sicurezza' o 'Non soggetto a ribasso'
               if (!intestazioneLogGiaInserita)
                 loggerImport.addMessaggioErrore((isGaraLottiConOffertaUnica
                     ? "Lotto " + codiceLotto + " - "
                     : "")
                     + label
                     + codiceLavorazioneFornitura
                     + " (riga "
                     + (indiceRiga + 1)
                     + ") non "+label1+": ");
               if ("1".equals(soloSicurezza))
                 loggerImport.addMessaggioErrore("- e' relativa a lavorazioni di tipo 'Solo sicurezza'");
               else
                 loggerImport.addMessaggioErrore("- e' relativa a lavorazioni di tipo 'Non soggetto a ribasso'");

               loggerImport.incrementaRecordNonAggiornati();
             }
           } else {
             //Non è stata trovata l'occorrenza in GCAP, riportare messaggio nel log!!!!
             if (!intestazioneLogGiaInserita)
               loggerImport.addMessaggioErrore((isGaraLottiConOffertaUnica
                   ? "Lotto " + codiceLotto + " - "
                   : "")
                   + label
                   + codiceLavorazioneFornitura
                   + " (riga "
                   + (indiceRiga + 1)
                   + ") non "+label1+": ");
             loggerImport.addMessaggioErrore("- non è stata riscontrata la corrispondenza nella banca dati");
             loggerImport.incrementaRecordNonAggiornati();
           }
         } else {
           loggerImport.incrementaRecordNonAggiornati();
         }
       } else {
         if (logger.isDebugEnabled())
           logger.debug("La riga "
               + (indiceRiga + 1)
               + " e' stata saltata "
               + "perche' presenta tutte e "
               + valoriCampiRigaExcel.size()
               + " le celle non valorizzate");

         contatoreRigheVuote++;
       }
     } else {
       if (logger.isDebugEnabled())
         logger.debug("La riga "
             + (indiceRiga + 1)
             + " e' stata saltata "
             + "perche' non inizializzata");

       contatoreRigheVuote++;
     }
     if (logger.isDebugEnabled())
       logger.debug("Fine lettura della riga " + (indiceRiga + 1));
   }


   if (logger.isDebugEnabled())
     logger.debug("updateVariazionePrezzi: fine metodo");
 }

 /**
  *
  * @param campoPrezzo
  * @param strCellaExce
  * @param valoriCampiRigaExcel
  * @param tmpListaMsg
  * @return boolean
  */
 private boolean controlloRigaVariazioniPrezzo(CampoImportExcel campoPrezzo, String strCellaExce, List<Object> valoriCampiRigaExcel, List<String> tmpListaMsg){
   boolean rigaImportabile=true;
   int colonna = campoPrezzo.getColonnaArrayValori();
   Object valore = valoriCampiRigaExcel.get(colonna);
   if (valore == null) {
     if (campoPrezzo.isObbligatorio()) {
       if (campoPrezzo.getValoreDiDefault() != null) {
         valoriCampiRigaExcel.set(colonna, campoPrezzo.getValoreDiDefault());
       } else {
         tmpListaMsg.add("- la cella "
             + strCellaExce
             + " non e' valorizzata");
         rigaImportabile = false;
       }
     }
   } else if (valore instanceof Error) {
     tmpListaMsg.add(" - la cella "
         + strCellaExce
         + " contiene un "
         + "errore: "
         + ((Error) valore).getMessage());
   } else {
     if (!(valore instanceof Double)) {
       tmpListaMsg.add("- la cella "
           + strCellaExce
           + " non e' in formato numerico.");
       valoriCampiRigaExcel.set(colonna, null);
       rigaImportabile = false;
     } else {
       // In Excel, la formattazione di un campo di tipo numerico
       // comporta
       // la modifica della visualizzazione della cella e non del valore
       // che effettivamente questa contiene. Ad esempio: una cella che
       // e' formattata per visualizzare al piu' 3 cifre decimali:
       // - se il valore e': 12,321 o 32,26 o 102,2 allora valore
       // visualizzato e valore contenuto coincidono;
       // - se il valore e': 12,3212 o 32,2628 allora il valore
       // visualizzato e' diverso dal valore contenuto. Per la precisione
       // i valori visualizzati sono rispettivamente: 12,321 e 32,263;
       // Questo comporta il controllo dei valori delle celle di campo in
       // formato decimale e l'eventuale arrotondamento del valore
       double valoreCampo = ((Double) valoriCampiRigaExcel.get(colonna)).doubleValue();
       double tmpValore = UtilityMath.round(valoreCampo,
           campoPrezzo.getCifreDecimali());
       if (valoreCampo != tmpValore)
         valoriCampiRigaExcel.set(colonna, new Double(tmpValore));
     }
   }
   return rigaImportabile;
 }

/*
 private boolean controlloCodvoc(List<String> tmpListaMsg, String ngara, Long contaf, boolean saltareControlloTabellato) {

   boolean rigaImportabile = true;
   if(saltareControlloTabellato || (!saltareControlloTabellato && this.controlloUnivocitaCodvoc)){
     String select = "select count(ngara) from gcap where ngara=? and codvoc=?";
     Object par[] = null;
     if(contaf!=null){
       select = "select count(ngara) from gcap where ngara=? and codvoc=? and contaf!=? and dittao is null";
       par = new Object[3];
     }else
       par = new Object[2];
     par[0]= ngara;
     par[1]=this.valoreCodvoc;
     if(contaf!=null)
       par[2]=contaf;

     try {
       Vector<?> datiCodvoc = this.sqlDao.getVectorQuery(select, par);
       if(datiCodvoc!=null && datiCodvoc.size()>0){
         Long conteggio = SqlManager.getValueFromVectorParam(datiCodvoc, 0).longValue();
         if(conteggio!=null && conteggio.longValue()>0){
           tmpListaMsg.add(" - la cella "
               + this.cellaCodvoc
               + " presenta un valore non ammesso: il codice specificato è già presente in una lavorazione.");
           rigaImportabile = false;
         }
       }
     } catch (Exception e) {
       logger.error("Errore nella verifica dell'unicità del valore del campo CODVOC per la gara " + ngara, e);
     }
   }
   return rigaImportabile;
 }
*/


 private boolean controlloCodvoc(List<String> tmpListaMsg, String ngara,boolean lotto) {

   boolean rigaImportabile = true;
   if(this.controlloUnivocitaCodvoc){
     String select = "select count(ngara) from gcap where ngara=? and codvoc=?";
     try {
       Vector<?> datiCodvoc = this.sqlDao.getVectorQuery(select, new Object[]{ngara,this.valoreCodvoc});
       if(datiCodvoc!=null && datiCodvoc.size()>0){
         Long conteggio = SqlManager.getValueFromVectorParam(datiCodvoc, 0).longValue();
         if(conteggio!=null && conteggio.longValue()>0){
           String msg=" - la cella " + this.cellaCodvoc + " presenta un valore non ammesso: è presente un'altra lavorazione ";
           if(lotto)
             msg += "nel lotto " + ngara;
           else
             msg += "in gara";
           msg +=" con la stessa voce.";
           tmpListaMsg.add(msg);
           rigaImportabile = false;
         }
       }
     } catch (Exception e) {
       logger.error("Errore nella verifica dell'unicità del valore del campo CODVOC per la gara " + ngara, e);
     }
   }
   return rigaImportabile;
 }

 /**
  * Si controlla il formato del valore di codvoc, se è un Double o un Long lo si converte in String
  * @param stringaObj
  * @return String
  */
 private String getCodvocString(Object stringaObj){
   String stringa = null;
   if (stringaObj != null) {
     if (stringaObj instanceof Long) {
       stringa = ((Long) stringaObj).toString();
     } else if (stringaObj instanceof Double) {
       Double tmp = new Double((Double) stringaObj);
       stringa = (new Long(tmp.longValue())).toString();
     }else
       stringa=(String)stringaObj;
   }
   return stringa;
 }
}