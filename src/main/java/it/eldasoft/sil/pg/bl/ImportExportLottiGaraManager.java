/*
 * Created on 05/apr/2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.TempFileUtilities;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.dao.SqlDao;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.excel.CampoImportExcel;
import it.eldasoft.sil.pg.bl.excel.DizionarioStiliExcelX;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityExcelX;
import it.eldasoft.utils.utility.UtilityMath;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Classe per la gestione delle funzionalita' di import/export dei lotti su foglio Excel
 *
 * @author Cristian.Febas
 */


public class ImportExportLottiGaraManager {

  static Logger                     logger = Logger.getLogger(ImportExportLottiGaraManager.class);

  private SqlDao                    sqlDao;

  /** Manager con funzionalita' generali */
  private GeneManager               geneManager;

  /** Manager dei tabellati */
  private TabellatiManager          tabellatiManager;

  /** Manager per l'applicazione PG */
  private PgManager                 pgManager;

  /** Manager della W_GENCHIAVI */
  private GenChiaviManager          genChiaviManager;

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

  private final String       SCHEMA_CAMPI                                                 = "GARE";


  /**
   * Nome del file associato al file Excel contenente l'export dei lotti di gara
   */

  private final String       FOGLIO_DATI_GARA                                             = "Dati gara";

  private final String       FOGLIO_LISTA_LOTTI[]                             = { "Lista lotti", "Sommario" };

  private final int          FOGLIO_LISTA_LOTTI_RIGA_NOME_FISICO_CAMPI        = 3;

  private final int          FOGLIO_DATI_GARA_RIGA_INIZIALE_DATI_GARA         = 4;

  private final int          FOGLIO_LISTA_LOTTI_ULTERIORI_RIGHE_DA_FORMATTARE = 20;

  private final int          FOGLIO_LISTA_LOTTI__RIGA_INIZIALE                = 10;

  private final int          IMPORT_NUMERO_RIGHE_CONSECUTIVE_VUOTE                        = 10;

  /**
   * Nome del file associato al file Excel contenente l'export dei lotti della gara
   */

  private final String       NOME_FILE_C0OGGASS_EXPORT                                    = "LottiGara.xlsx";

  private Workbook       workBook                                                     = null;

  private String[]           arrayCampi;
  private int[]              arrayStiliCampi;
  private String[]           arrayTitoloColonne;
  private int[]              arrayLarghezzaColonne;
  private boolean[]          arrayCampiVisibili;
  private int[]              arrayIndiceColonnaCampi;
  private int                indicePosizioneColonnaCodcpv;
  private int                indicePosizioneColonnaInizioCampiGARE1;
  private int                numCampiGARE1Consecutivi;


  /**
   * Export dei lotti  file Excel con salvataggio dello stesso nella
   * directory temporanea dell'application server.
   * Ritorna il nome del file salvato nella directory temporanea
   *
   * @param codgar
   *        codice della gara
   *
   * @param isGaraLottiOffDist
   *        flag per indicare se la gara è a lotti con offerte distinte (bustalotti = 1)
   * @param session
   *        sessione dell'utente
   * @return Ritorna il nome del file salvato nella cartella temporanea
   * @throws NullPointerException
   * @throws FileNotFoundException
   * @throws IOException
   * @throws SQLException
   * @throws GestoreException
   * @throws Exception
   */
  public String exportLottiGara(String codgar, boolean isGaraLottiOffDist,HttpSession session)
  throws FileNotFoundException, SQLException, GestoreException, IOException,
      Exception {

    String profiloAttivo = "PG_DEFAULT";
    if (session != null) {
      profiloAttivo = (String) session.getAttribute("profiloAttivo");
    }

    this.setDefinizioni();

    this.workBook = new XSSFWorkbook();
    // Creazione del dizionario degli stili delle celle dell'intero file Excel
    DizionarioStiliExcelX dizStiliExcel = new DizionarioStiliExcelX((XSSFWorkbook)this.workBook);

    // Scrittura sul foglio Excel dei dati generali della gara
    this.setDatiGeneraliGara(codgar, profiloAttivo, isGaraLottiOffDist, dizStiliExcel);

    // Scrittura sul foglio Excel dei lotti della gara
    this.setLottiGara(codgar, profiloAttivo, isGaraLottiOffDist, dizStiliExcel);

    // Set del foglio 'Lotti Gara' come foglio attivo

      this.workBook.setActiveSheet(this.workBook.getSheetIndex(FOGLIO_LISTA_LOTTI[0]));
      this.workBook.setSelectedTab(this.workBook.getSheetIndex(FOGLIO_LISTA_LOTTI[0]));

    // Creazione di un file temporaneo nella cartella temporanea e salvataggio
    // del suo nome in sessione nell'oggetto TempfileDeleter
    String nomeFile = codgar.toUpperCase().replaceAll("/", "_");
    nomeFile += "_" + FilenameUtils.getBaseName(NOME_FILE_C0OGGASS_EXPORT) + "." +
          FilenameUtils.getExtension(NOME_FILE_C0OGGASS_EXPORT);
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


  private void importLotti(Sheet foglio, String codgar,
      List<CampoImportExcel> listaCampiImportExcel, boolean isGaraLottiOffDist,
      HttpSession session, LoggerImportOffertaPrezzi loggerImport,
      boolean isCodificaAutomaticaAttiva)
                throws SQLException, SqlComposerException, GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("importLottiGara: inizio metodo");

    this.cancellaDati(codgar, isGaraLottiOffDist, isCodificaAutomaticaAttiva);

    int indiceRiga = FOGLIO_LISTA_LOTTI__RIGA_INIZIALE - 1;

    int ultimaRigaValorizzata = foglio.getLastRowNum() +1;

    HashMap<String, Object> valoriCampi = new HashMap<String, Object>();
    Vector<?> datiLottoComplementare = this.sqlDao.getVectorQuery(
        "select MODASTG, RIBCAL, SICINC from GARE where CODGAR1 = ? and NGARA = ? and GENERE = ?",
        new Object[] { codgar, codgar, new Long(3) });
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
             new Object[] { codgar });

    // Il campo TORN.TIPGEN dice se la gara e' per forniture (TIPGEN = 2)
    // o servizi (TIPGEN = 3)
    Long campoTORN_TIPGEN = (Long) SqlManager.getValueFromVectorParam(datiTornata, 0).getValue();
    String fdfs = PgManager.getTabellatoPercCauzioneProvvisoria(campoTORN_TIPGEN.intValue());

    String tmpPerCauzProvv = this.tabellatiManager.getDescrTabellato(fdfs, "1");
    Double percentualeCauzProvv = UtilityNumeri.convertiDouble(
        tmpPerCauzProvv, UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE);
    valoriCampi.put("PGAROF", percentualeCauzProvv);

    int numeroDecimali = this.pgManager.getArrotondamentoCauzioneProvvisoria(campoTORN_TIPGEN.intValue());

    Double percentualeCauzioneProvv = null;
    if (tmpPerCauzProvv != null && !"".equals(tmpPerCauzProvv))
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

    // Attributi per GARE1
    String valtec = null;
    Long contoeco = null;
    Long ultdetlic = null;
    Long aqoper = null;
    Long aqnumope = null;
    if (SqlManager.getValueFromVectorParam(datiTornata, 15) != null) {
      valtec = (String) SqlManager.getValueFromVectorParam(datiTornata, 15).getValue();
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

    List<String> valoriCodiga = new ArrayList<String>();

    // Contatore del numero di righe consecutive vuote, per terminare la lettura
    // del foglio Excel prima di giungere all'ultima riga inizializzata dopo che
    // sono state trovate un numero di righe vuote consecutive pari alla
    // costante IMPORT_NUMERO_RIGHE_CONSECUTIVE_VUOTE
    int contatoreRigheVuote = 0;

    for (; indiceRiga < ultimaRigaValorizzata
    && contatoreRigheVuote <= IMPORT_NUMERO_RIGHE_CONSECUTIVE_VUOTE; indiceRiga++) {
      Row rigaFoglioExcel = foglio.getRow(indiceRiga);
      if (rigaFoglioExcel != null) {
        // Lista dei valori di GARE
        List<Object> valoriCampiRigaExcel = this.letturaRiga(rigaFoglioExcel, listaCampiImportExcel);
        int numeroCelleNull = 0;
        for (int h = 0; h < valoriCampiRigaExcel.size(); h++){
          if (valoriCampiRigaExcel.get(h) == null){
            numeroCelleNull++;
          }
        }
        if (numeroCelleNull < valoriCampiRigaExcel.size()) {
          // Reset del contatore del numero di righe vuote se diverso da zero
          if (contatoreRigheVuote > 0){
            contatoreRigheVuote = 0;
          }

          loggerImport.incrementaRigheLette();

          boolean rigaImportabile = true;
          List<String> tmpListaMsg = new ArrayList<String>();
          // Controllo valori di GARE
          String codiceLotto = "";
          String codiga = null;
          rigaImportabile = this.controlloValoriRiga(listaCampiImportExcel,
              indiceRiga, valoriCampiRigaExcel, valoriCodiga, tmpListaMsg);

          if (rigaImportabile) {
            if (logger.isDebugEnabled())
                logger.debug("I dati presenti nella riga "
                  + (indiceRiga + 1)
                  + " sono nel formato previsto");

                double impapp = 0;
                if (isCodificaAutomaticaAttiva) {
                  codiga = (String) valoriCampiRigaExcel.get(0);
                  //codiceCIG = (String) valoriCampiRigaExcel.get(1);
                  HashMap<String, String> mappa = this.pgManager.calcolaCodificaAutomatica("GARE",
                      Boolean.FALSE, codgar, new Long(codiga));
                  codiceLotto = mappa.get("numeroGara");
                  if(valoriCampiRigaExcel.get(3) != null){
                    impapp = (Double) valoriCampiRigaExcel.get(3);
                  }
                } else {
                  codiceLotto = (String) valoriCampiRigaExcel.get(0);
                  codiga = (String) valoriCampiRigaExcel.get(1);
                  if(valoriCampiRigaExcel.get(4)!=null){
                    impapp = (Double) valoriCampiRigaExcel.get(4);
                  }
              }
              // Determinazione del campo GARE.GAROFF (Importo cauzione provvisoria)
              // come prodotto tra GARE.IMPAPP e GARE.PGAROF, con arrotondamento al
              // numero di decimali indicato dal
              if (percentualeCauzioneProvv != null)
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
              if (idiaut != null){
                valoriCampi.put("IDIAUT", idiaut);
              } else{
                valoriCampi.put("IDIAUT", null);
              }
          }

          if (tmpListaMsg.size() == 0) {
            ;
          } else if (tmpListaMsg.size() > 0) {
            if (!isCodificaAutomaticaAttiva && (codiceLotto != null && codiceLotto.length() > 0)) {

            } else {
                if (rigaImportabile)
                  loggerImport.addMessaggioErrore("La riga " + (indiceRiga + 1)
                      + " importata parzialmente:");
                else
                  loggerImport.addMessaggioErrore("La riga " + (indiceRiga + 1)
                      + " non importata:");
            }

            if (tmpListaMsg.size() > 0){
              loggerImport.addListaMessaggiErrore(tmpListaMsg);
            }
          }
          if (rigaImportabile) {

            //Nel file EXCEL da importare sono stati introdotti campi non dell'entità gare (es. GARE1 e GARCPV)
            //tali campi vanno scorporati da valoriCampiRigaExcel e valoricampi

            //Addiziono nella lista i codiga già utilizzati
            valoriCodiga.add(codiga);

            String sqlInsertGARE = new String(
            "insert into GARE (CAMPI) values (VALORI)");
            StringBuffer strCampi = null;
            StringBuffer strValori = null;
            strCampi = new StringBuffer("CODGAR1, NGARA, CODIGA, CODCIG, NOT_GAR, IMPAPP, IMPNRL, IMPSIC, CUPPRG, ");
            strValori = new StringBuffer("?, ?, ?, ?, ?, ?, ?, ?, ?, ");

            List<Object> listaValoriRiga = new ArrayList<Object>();
            listaValoriRiga.add(codgar);
            //Aggiungo ngara solo se cod.Automatica, altrimenti c'e' già
            if (isCodificaAutomaticaAttiva) {
              listaValoriRiga.add(codiceLotto);
            }

            //Dalla lista dei valori della riga va tolto il valore relativo a CODCPV, che è dell'entità GARCPV,
            //e quindi va gestito separatamente.
            //Analoga gestione per i campi relativi a GARE1
            String codcpv = (String)valoriCampiRigaExcel.get(this.indicePosizioneColonnaCodcpv);
            List<Object> listaValoriRigaGare1 = new ArrayList<Object>();
            Object valore = null;
            boolean ammrinDaSettare=false;
            boolean ammopzDaSettare=false;
            for(int indice=this.indicePosizioneColonnaInizioCampiGARE1; indice<this.indicePosizioneColonnaInizioCampiGARE1 + this.numCampiGARE1Consecutivi; indice++){
              valore = valoriCampiRigaExcel.get(indice);
              /*

              if(valore == null)
                listaValoriRigaGare1.add(null);
              else if(valore instanceof Double)
                listaValoriRigaGare1.add(valore);
              else if(valore instanceof Long)
                listaValoriRigaGare1.add(valore);
              else if(valore instanceof String)
                listaValoriRigaGare1.add(valore);
              */
              //Per stabilire se inizializzare i campi AMMRIN e AMMOPZ si assume che i campi nel file excel siano esattamente nell'ordine:
              //CODCUI,ANNINT,IMPRIN,DESRIN,IMPSERV,IMPPROR,IMPALTRO,DESOPZ
              if(valore!=null){
                if(indice > (this.indicePosizioneColonnaInizioCampiGARE1 + 1) && indice < (this.indicePosizioneColonnaInizioCampiGARE1 + 4))
                  ammrinDaSettare=true;
                else if(indice >= this.indicePosizioneColonnaInizioCampiGARE1 + 4)
                  ammopzDaSettare=true;
              }
              listaValoriRigaGare1.add(valore);

            }

            //Rimozione campi GARE1
            for(int indice=this.indicePosizioneColonnaInizioCampiGARE1; indice<this.indicePosizioneColonnaInizioCampiGARE1 + this.numCampiGARE1Consecutivi ; indice++){
              valoriCampiRigaExcel.remove(this.indicePosizioneColonnaInizioCampiGARE1);
            }

            //Rimozione valore CODCPV
            valoriCampiRigaExcel.remove(indicePosizioneColonnaCodcpv);


            listaValoriRiga.addAll(valoriCampiRigaExcel);

            //Valori di inizializzazione i campi AMMRIN e AMMOPZ
            if(ammrinDaSettare)
              listaValoriRigaGare1.add("1");
            else
              listaValoriRigaGare1.add(null);
            if(ammopzDaSettare)
              listaValoriRigaGare1.add("1");
            else
              listaValoriRigaGare1.add(null);

            Iterator<?> iterator1 = valoriCampi.keySet().iterator();
            Object[] valori = new Object[valoriCampi.size() + listaValoriRiga.size()];
            int indice = listaValoriRiga.size();
            while (iterator1.hasNext()) {
              String nomeCampo = (String) iterator1.next();
              strCampi.append(nomeCampo.concat(", "));
              strValori.append("?, ");
              valori[indice] = valoriCampi.get(nomeCampo);
              indice++;
            }

            for (int y = 0; y < listaValoriRiga.size(); y++) {
                if ((isCodificaAutomaticaAttiva && y == 3) || (!isCodificaAutomaticaAttiva && y == 2)) {
                    if ("0000000000".equals(listaValoriRiga.get(y))) {
                        int nextId = this.genChiaviManager.getNextId("GARE.CODCIG");
                        valori[y] = "#".concat(StringUtils.leftPad("" + nextId, 9, "0"));
                    } else {
                        if (listaValoriRiga.get(y) != null) {
                            valori[y] = ((String) listaValoriRiga.get(y)).toUpperCase();
                        }
                    }
                } else {
                  valori[y] = listaValoriRiga.get(y);
                }
            }

            sqlInsertGARE = sqlInsertGARE.replaceFirst("CAMPI", strCampi.substring( 0, strCampi.length() - 2));
            sqlInsertGARE = sqlInsertGARE.replaceFirst("VALORI", strValori.substring(0, strValori.length() - 2));

            try {
              //Inserimento dell'occorrenza in GARE
              this.sqlDao.update(sqlInsertGARE, valori);

              //Inserimento dell'occorrenza in GARE1
              Object[] valoriGare1 = new Object[7 + listaValoriRigaGare1.size()];
              valoriGare1[0]=codiceLotto;
              valoriGare1[1]=codgar;
              valoriGare1[2]=valtec;
              valoriGare1[3]=contoeco;
              valoriGare1[4]=ultdetlic;
              valoriGare1[5]=aqoper;
              valoriGare1[6]=aqnumope;
              for(int z=0; z < listaValoriRigaGare1.size(); z++)
                valoriGare1[z+7]=listaValoriRigaGare1.get(z);

              this.sqlDao.update("insert into GARE1(NGARA,CODGAR1,VALTEC,CONTOECO,ULTDETLIC,AQOPER,AQNUMOPE,CODCUI,ANNINT,IMPRIN,DESRIN,IMPSERV,IMPPROR,IMPALTRO,DESOPZ,AMMRIN,AMMOPZ) "
                  + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",valoriGare1 );

              if(codcpv!=null && !"".equals(codcpv))
                this.sqlDao.update("insert into garcpv(ngara,numcpv,codcpv,tipcpv) values (?,?,?,?)", new Object[]{codiceLotto, new Long(1),codcpv, "1"});

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
        } else {//numcell
          if (logger.isDebugEnabled())
            logger.debug("La riga " + (indiceRiga + 1) + " e' stata saltata "
                + "perche' presenta tutte e " + valoriCampiRigaExcel.size()
                + " le celle non valorizzate");

          contatoreRigheVuote++;
        }
      } else {//rigafoglioexcel
        if (logger.isDebugEnabled())
          logger.debug("La riga " + (indiceRiga + 1) + " e' stata saltata "
              + "perche' non inizializzata");

        contatoreRigheVuote++;
      }
    }//for

    //AGGIORNAMENTI VARI

    Double importoTotaleImpapp = new Double(0);
    List<?> listaImpapp = this.sqlDao.getVectorQueryForList(
        "select NGARA, CODIGA, IMPAPP from GARE where codgar1 = ? and "
                + "(GARE.GENERE is null or GARE.GENERE <> 3)", new Object[] { codgar });
    if (listaImpapp != null && listaImpapp.size() > 0) {
      for (int re = 0; re < listaImpapp.size(); re++) {
        Vector<?> tmpVec = (Vector<?>) listaImpapp.get(re);
        String ngara = SqlManager.getValueFromVectorParam(tmpVec, 0).getStringValue();
        Double impapp = (Double) SqlManager.getValueFromVectorParam(tmpVec, 2).getValue();
        if(impapp == null){
          impapp = new Double(0);
        }
        importoTotaleImpapp = importoTotaleImpapp + impapp;
        this.pgManager.inizializzaDitteLottiOffertaUnica(codgar, ngara, true);
        //IMPRDOCG
        this.inizializzaDocumentazioneDitte(codgar, ngara,pgManager);

      }
    }

    Double importoContributo = this.pgManager.getContributoAutoritaStAppaltante(importoTotaleImpapp, "A1z02");

    try {
      this.sqlDao.update("update torn set imptor=?,istaut=? where codgar=?",new Object[]{importoTotaleImpapp,importoContributo,codgar});
    } catch (SQLException e) {
        throw new GestoreException("Errore nell'aggiornamento dell'importo complessivo"
          + "' della gara '"
          + codgar
          + "'", "importOffertaPrezzi.erroreAggiornamentoImportoGara", e);
    }

    if (logger.isDebugEnabled())
      logger.debug("importLottiGara: fine metodo");

  }
  /**
  *
  * @param fileExcel
  * @param importLotti
  * @param archiviaXLSDocAss
  * @param session
  * @throws FileNotFoundException
  * @throws IOException
  */
 public LoggerImportOffertaPrezzi importLottiGara(
     UploadFileForm fileExcel, String codgar, boolean isGaraLotti, HttpSession session)
   throws FileNotFoundException, IOException, GestoreException, Exception {

   if (logger.isDebugEnabled())
     logger.debug("importLottiGara: inizio metodo");

   boolean isCodificaAutomaticaAttiva = this.geneManager.isCodificaAutomatica("GARE", "NGARA");

   this.setDefinizioni();

   LoggerImportOffertaPrezzi loggerImport = new LoggerImportOffertaPrezzi();
   this.workBook = WorkbookFactory.create(fileExcel.getSelezioneFile().getInputStream());

   Sheet foglioLottiGara = null;
   int indiceFoglio = -1;
   // Flag per indicare se eseguire il controllo delle informazioni preliminari
   // del foglio Excel che si sta importando: se il foglio Excel e' esportato
   // dalla funzione di export dalla pagina 'Lista dei Lotti di Gara'
   boolean eseguireVerifichePreliminari = true;
   if (workBook.getSheet(FOGLIO_LISTA_LOTTI[0]) != null) {
     foglioLottiGara = workBook.getSheet(FOGLIO_LISTA_LOTTI[0]);
     indiceFoglio = 0;
   } else if (workBook.getSheet(FOGLIO_LISTA_LOTTI[1]) != null) {
     foglioLottiGara = workBook.getSheet(FOGLIO_LISTA_LOTTI[1]);
     indiceFoglio = 1;
     eseguireVerifichePreliminari = false;
   }
   if (foglioLottiGara != null) {
     if (eseguireVerifichePreliminari) {
       if (!this.verifichePreliminari(codgar, foglioLottiGara,
           isCodificaAutomaticaAttiva)) {
         throw new GestoreException("Errore durante le verifiche preliminari "
             + "dell'operazione di import dei lotti di gara",
             "importExportOffertaPrezzi.verifichePreliminari");
       }
     }

     if (logger.isDebugEnabled()) {
         logger.debug("importLotti: inizio lettura del foglio '"
             + FOGLIO_LISTA_LOTTI[0] + "'");
     }

     // Controllo del numero di campi obbligatori su tutto il file
     Map<String,List<?>> mappa = this.getListaCampiDaImportare(foglioLottiGara, FOGLIO_LISTA_LOTTI[indiceFoglio], null);
     if (mappa != null
         && mappa.containsKey("listaNomiFisiciCampiDaImportare")
         && !mappa.containsKey("listaErrori")) {
       List<?> listaNomiFisiciCampiDaImportare = mappa.get("listaNomiFisiciCampiDaImportare");
       int numeroCampiObbligatori = -1;
       int counter = 0;
       if (isCodificaAutomaticaAttiva) {
         numeroCampiObbligatori = 1;
       } else {
         numeroCampiObbligatori = 2;
       }

        for (int i = 0; i < listaNomiFisiciCampiDaImportare.size()
             && counter < numeroCampiObbligatori; i++) {
          //Conteggio presenza dati obbligatori
          if (!isCodificaAutomaticaAttiva) {
            if (arrayCampi[0].equals(listaNomiFisiciCampiDaImportare.get(i))) {
              counter++;
            }
          }
          if (arrayCampi[1].equals(listaNomiFisiciCampiDaImportare.get(i))) {
            counter++;
          }
        }

        if (counter == numeroCampiObbligatori) {
          // Dati della tabella GARE
          List<CampoImportExcel> listaCampiImportExcel = this.getListaCampiDaImportareExcel(foglioLottiGara, indiceFoglio, "GARE", isCodificaAutomaticaAttiva);

          /*  IMPORT EFFETTIVO*/
          this.importLotti(foglioLottiGara, codgar, listaCampiImportExcel, isGaraLotti,
             session, loggerImport, isCodificaAutomaticaAttiva);

        } else {

             logger.error("importLottiGara: Nel foglio '"
                 + FOGLIO_LISTA_LOTTI[indiceFoglio] + "' non sono "
                 + "presenti i nomi fisici dei campi che da analisi sono SEMPRE "
                 + "visibili (CODVOC, PREZUN)");
        }
     } else if (mappa.get("listaErrori") != null) {
       List<?> listaErrori = mappa.get("listaErrori");
       for (int l = 0; l < listaErrori.size(); l++)
         logger.error("importLotti: " + (String) listaErrori.get(l));

       loggerImport.addMsgVerificaFoglio("Il formato del file Excel non e' "
           + "compatibile. Si consiglia di eseguire nuovamente l'esportazione "
           + "per produrre un file in formato valido");
     } else {
       String msgFoglio = FOGLIO_LISTA_LOTTI[indiceFoglio];
       logger.error("Il metodo getListaCampiDaImportare ha restituito null. "
           + "Si consiglia di andare in debug importando lo stesso file Excel "
           + "che ha generato questo errore, per capire la causa.");
       loggerImport.addMsgVerificaFoglio("Si e' verificato un errore "
           + "inaspettato nella lettura dell'intestazione del foglio '"
           + msgFoglio + "'");
     }
   } else {
     String msgFoglio = FOGLIO_LISTA_LOTTI[indiceFoglio];

     String tmp = "Il formato del file Excel non e' compatibile: non e' "
         + "presente il foglio '" + msgFoglio;
     logger.error(tmp);
     loggerImport.addMsgVerificaFoglio(tmp);
   }
   if (logger.isDebugEnabled()){
     String msgFoglio = FOGLIO_LISTA_LOTTI[indiceFoglio];
     logger.debug("importOffertaPrezzi: fine lettura del foglio '"
         + msgFoglio + "'");
   }

   if (logger.isDebugEnabled()) {
     logger.debug("importOffertaPrezzi: fine metodo");
   }
   return loggerImport;
 }

 /**
  * Set degli array di definizione in funzione del tipo di fornitura
  * Se non specificato il tipoFornitura, si considera la gestione
  * dell'offerta prezzi
  *
  * @param tipoFornitura
  * @param gara
  * @throws SQLException
  * @throws GestoreException
  */
 private void setDefinizioni() throws SQLException, GestoreException {

   if (true) {

       int numeroTotaleCampi = 17;
       int cnt = 0;

       arrayCampi = new String[numeroTotaleCampi];
       arrayStiliCampi = new int[numeroTotaleCampi];
       arrayTitoloColonne = new String[numeroTotaleCampi];
       arrayLarghezzaColonne = new int[numeroTotaleCampi];
       arrayCampiVisibili = new boolean[numeroTotaleCampi];
       arrayIndiceColonnaCampi = new int[numeroTotaleCampi];

       // 0
       arrayCampi[cnt] = "GARE.GARE.NGARA";
       arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_CENTER;
       arrayTitoloColonne[cnt] = "Codice lotto";
       arrayLarghezzaColonne[cnt] = 14;
       arrayCampiVisibili[cnt] = true;

       // 1
       cnt++;
       arrayCampi[cnt] = "GARE.GARE.CODIGA";
       arrayStiliCampi[cnt] = DizionarioStiliExcelX.INTERO_ALIGN_CENTER;
       arrayTitoloColonne[cnt] = "Lotto";
       arrayLarghezzaColonne[cnt] = 6;
       arrayCampiVisibili[cnt] = true;

       // 2
       cnt++;
       arrayCampi[cnt] = "GARE.GARE.CODCIG";
       arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_CENTER;
       arrayTitoloColonne[cnt] = "Codice CIG";
       arrayLarghezzaColonne[cnt] = 10;
       arrayCampiVisibili[cnt] = true;

       // 3
       cnt++;
       arrayCampi[cnt] = "GARE.GARE.NOT_GAR";
       arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
       arrayTitoloColonne[cnt] = "Oggetto";
       arrayLarghezzaColonne[cnt] = 80;
       arrayCampiVisibili[cnt] = true;

       // 4
       cnt++;
       arrayCampi[cnt] = "GARE.GARE.IMPAPP";
       arrayStiliCampi[cnt] = DizionarioStiliExcelX.DECIMALE2_ALIGN_RIGHT;
       arrayTitoloColonne[cnt] = "Importo a base di gara";
       arrayLarghezzaColonne[cnt] = 11;
       arrayCampiVisibili[cnt] = true;

       // 5
       cnt++;
       arrayCampi[cnt] = "GARE.GARE.IMPNRL";
       arrayStiliCampi[cnt] = DizionarioStiliExcelX.DECIMALE2_ALIGN_RIGHT;
       arrayTitoloColonne[cnt] = "di cui non soggetto a ribasso";
       arrayLarghezzaColonne[cnt] = 11;
       arrayCampiVisibili[cnt] = true;

       // 6
       cnt++;
       arrayCampi[cnt] = "GARE.GARE.IMPSIC";
       arrayStiliCampi[cnt] = DizionarioStiliExcelX.DECIMALE2_ALIGN_RIGHT;
       arrayTitoloColonne[cnt] = "di cui sicurezza";
       arrayLarghezzaColonne[cnt] = 11;
       arrayCampiVisibili[cnt] = true;

       // 7
       cnt++;
       arrayCampi[cnt] = "GARE.GARE.CUPPRG";
       arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
       arrayTitoloColonne[cnt] = "Codice CUP";
       arrayLarghezzaColonne[cnt] = 11;
       arrayCampiVisibili[cnt] = true;

       // 8
       cnt++;
       arrayCampi[cnt] = "GARE.GARCPV.CODCPV";
       arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
       arrayTitoloColonne[cnt] = "Codice CPV principale";
       arrayLarghezzaColonne[cnt] = 11;
       arrayCampiVisibili[cnt] = true;

       // 9
       cnt++;
       arrayCampi[cnt] = "GARE.GARE1.CODCUI";
       arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
       arrayTitoloColonne[cnt] = "Codice CUI";
       arrayLarghezzaColonne[cnt] = 11;
       arrayCampiVisibili[cnt] = true;

       // 10
       cnt++;
       arrayCampi[cnt] = "GARE.GARE1.ANNINT";
       arrayStiliCampi[cnt] = DizionarioStiliExcelX.INTERO_ALIGN_CENTER;
       arrayTitoloColonne[cnt] = "Anno programmazione";
       arrayLarghezzaColonne[cnt] = 6;
       arrayCampiVisibili[cnt] = true;

       // 11
       cnt++;
       arrayCampi[cnt] = "GARE.GARE1.IMPRIN";
       arrayStiliCampi[cnt] = DizionarioStiliExcelX.DECIMALE2_ALIGN_RIGHT;
       arrayTitoloColonne[cnt] = "Importo rinnovi";
       arrayLarghezzaColonne[cnt] = 11;
       arrayCampiVisibili[cnt] = true;

       // 12
       cnt++;
       arrayCampi[cnt] = "GARE.GARE1.DESRIN";
       arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
       arrayTitoloColonne[cnt] = "Descrizione rinnovi";
       arrayLarghezzaColonne[cnt] = 80;
       arrayCampiVisibili[cnt] = true;

       // 13
       cnt++;
       arrayCampi[cnt] = "GARE.GARE1.IMPSERV";
       arrayStiliCampi[cnt] = DizionarioStiliExcelX.DECIMALE2_ALIGN_RIGHT;
       arrayTitoloColonne[cnt] = "Importo opzione servizi analoghi";
       arrayLarghezzaColonne[cnt] = 11;
       arrayCampiVisibili[cnt] = true;

       // 14
       cnt++;
       arrayCampi[cnt] = "GARE.GARE1.IMPPROR";
       arrayStiliCampi[cnt] = DizionarioStiliExcelX.DECIMALE2_ALIGN_RIGHT;
       arrayTitoloColonne[cnt] = "Importo opzione proroga";
       arrayLarghezzaColonne[cnt] = 11;
       arrayCampiVisibili[cnt] = true;

       // 15
       cnt++;
       arrayCampi[cnt] = "GARE.GARE1.IMPALTRO";
       arrayStiliCampi[cnt] = DizionarioStiliExcelX.DECIMALE2_ALIGN_RIGHT;
       arrayTitoloColonne[cnt] = "Importo altre opzioni";
       arrayLarghezzaColonne[cnt] = 11;
       arrayCampiVisibili[cnt] = true;

       // 16
       cnt++;
       arrayCampi[cnt] = "GARE.GARE1.DESOPZ";
       arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
       arrayTitoloColonne[cnt] = "descrizione opzioni";
       arrayLarghezzaColonne[cnt] = 80;
       arrayCampiVisibili[cnt] = true;
     }

     for (int i = 0; i < arrayCampi.length; i++) {
       arrayIndiceColonnaCampi[i] = i + 1;
     }

     numCampiGARE1Consecutivi=8;
   }


 /**
  * Lettura della riga del foglio 'Lista lotti' che in fase
  * di export e' stata valorizzata con i nomi fisici dei campi esportati
  *
  * @param foglio
  * @param nomeFoglio
  * @return Ritorna la lista dei campi da leggere nel foglio 'Lista lotti di Gara
  *         e forniture', sottoforma di lista di oggetti Campo. La lista e'
  *         vuota se nella riga non si trova nessun nome fisico
  */
 private Map<String,List<?>> getListaCampiDaImportare(Sheet foglio, String nomeFoglio, String entita) {
   if (logger.isDebugEnabled())
     logger.debug("getListaCampiDaImportare: inizio metodo");

   DizionarioCampi dizCampi = DizionarioCampi.getInstance();
   Row riga = foglio.getRow(FOGLIO_LISTA_LOTTI_RIGA_NOME_FISICO_CAMPI - 1);
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
       if ("GARE".equals(entita)) {
         for (int i = 0; i < arrayCampi.length; i++) {
           if (arrayCampi[i].indexOf(".GARE.") > 0 || arrayCampi[i].indexOf(".GARCPV.") > 0 || arrayCampi[i].indexOf(".GARE1.") > 0) {
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
       Cell cella = riga.getCell(i);
       String carattereColonna = UtilityExcelX.conversioneNumeroColonna(i + 1);
       if (cella != null) {
         if (logger.isDebugEnabled())
           logger.debug("getListaCampiDaImportare: inizio lettura della "
               + "cella "
               + carattereColonna
               + (riga.getRowNum() + 1));

         if (cella.getCellType() == Cell.CELL_TYPE_STRING) {
           RichTextString richTextString = cella.getRichStringCellValue();
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
         + FOGLIO_LISTA_LOTTI_RIGA_NOME_FISICO_CAMPI
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


 /**
  * Gestione delle lista dei campi da importare da Excel
  *
  * @param foglioListaLotti
  * @param indiceFoglio
  * @param entita
  * @return
  */
 private List<CampoImportExcel> getListaCampiDaImportareExcel(
     Sheet foglioLottiGara, int indiceFoglio, String entita, boolean isCodificaAutomaticaAttiva ) {

   Map<String,List<?>> mappa = null;
   mappa = this.getListaCampiDaImportare(foglioLottiGara, FOGLIO_LISTA_LOTTI[indiceFoglio], entita);

   List<CampoImportExcel> listaCampiImportExcel = new ArrayList<CampoImportExcel>();

   if (mappa != null
       && mappa.containsKey("listaNomiFisiciCampiDaImportare")
       && !mappa.containsKey("listaErrori")) {

     List<String> listaNomiFisiciCampiDaImportare = (List<String>) mappa.get("listaNomiFisiciCampiDaImportare");
     List<Long> listaIndiceColonnaCampiDaImportare = (List<Long>) mappa.get("listaIndiceColonnaCampiDaImportare");
     List<Long> listaIndiceArrayValoreCampiDaImportare = (List<Long>) mappa.get("listaIndiceArrayValoreCampiDaImportare");

     CampoImportExcel tmpCampo = null;
     for (int i = 0; i < listaNomiFisiciCampiDaImportare.size(); i++) {
       String nomeFisicoCampo = listaNomiFisiciCampiDaImportare.get(i);
       tmpCampo = new CampoImportExcel(nomeFisicoCampo,new Long(3),tabellatiManager);
       tmpCampo.setColonnaCampo(listaIndiceColonnaCampiDaImportare.get(i).intValue());
       tmpCampo.setColonnaArrayValori(listaIndiceArrayValoreCampiDaImportare.get(i).intValue());

       /* CAMPI OBBLIGATORI*/
       if (isCodificaAutomaticaAttiva){
         if (nomeFisicoCampo.equalsIgnoreCase(arrayCampi[1])) {
           tmpCampo.setObbligatorio(true);
         } else {
           tmpCampo.setObbligatorio(false);
         }
       } else {
         if (nomeFisicoCampo.equalsIgnoreCase(arrayCampi[0]) || nomeFisicoCampo.equalsIgnoreCase(arrayCampi[1])) {
           tmpCampo.setObbligatorio(true);
         } else {
           tmpCampo.setObbligatorio(false);
         }
       }
       listaCampiImportExcel.add(tmpCampo);
     }
   } else {
     listaCampiImportExcel = null;
   }

   return listaCampiImportExcel;

 }

 /**
  * Scrittura sul foglio Excel dei dati generali della gara
  *
  * @param codgar
  * @throws SQLException
  * @throws GestoreException
  */
 private void setDatiGeneraliGara(String codgar, String profiloAttivo,
     boolean isGaraLottiOffDist, DizionarioStiliExcelX dizStiliExcel)
     throws SQLException, GestoreException {

   Sheet foglioDatiGenerali = workBook.createSheet(FOGLIO_DATI_GARA);
   UtilityExcelX.setLarghezzaColonna(foglioDatiGenerali, 1, 30);
   UtilityExcelX.setLarghezzaColonna(foglioDatiGenerali, 2, 45);

   Row riga = null;
   Cell cella = null;
   CellStyle stile = dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_INTESTAZIONE);

   // Scrittura dell'intestazione delle due colonne del foglio
   // Prima riga del foglio
   riga = foglioDatiGenerali.createRow(0);
   // Prima cella: vuota
   cella = riga.createCell(0);
   cella.setCellStyle(stile);
   // Seconda cella: titolo "Dati generali della gara"
   cella = riga.createCell(1);
   cella.setCellStyle(stile);
   cella.setCellValue(new XSSFRichTextString("Dati generali della gara"));
   stile = dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_NASCOSTA);
   // Seconda riga del foglio (riga nascosta)
   riga = foglioDatiGenerali.createRow(1);
   // Prima cella: vuota
   cella = riga.createCell(0);
   cella.setCellStyle(stile);
   // Seconda cella: vuota
   cella = riga.createCell(1);
   cella.setCellStyle(stile);
   riga.setZeroHeight(true); // nascondo la riga

   stile = dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_SEPARATRICE);
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

     try {

         //Nel caso di lotto di gara per una gara con bustalotti=1 si deve caricare l'oggetto della gara
         Long bustalotti = null;
         String destor = null;
         Vector<?> datiGaraComplementare = this.sqlDao.getVectorQuery("select bustalotti,destor from gare,torn where codgar1=codgar and ngara=?", new Object[]{codgar});
         if (datiGaraComplementare != null && datiGaraComplementare.size() > 0) {
           bustalotti = (Long) SqlManager.getValueFromVectorParam(datiGaraComplementare, 0).getValue();
           destor = (String) SqlManager.getValueFromVectorParam(datiGaraComplementare, 1).getValue();
           if((new Long(1)).equals(bustalotti)){
             UtilityExcelX.scriviCella(foglioDatiGenerali, 1, indiceRiga, "Codice gara",
                 dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_RIGHT_BOLD_ITALIC));
             if (codgar != null && codgar.length() > 0)
               UtilityExcelX.scriviCella(foglioDatiGenerali, 2, indiceRiga++,
                   codgar, dizStiliExcel.getStileExcel(
                         DizionarioStiliExcelX.STRINGA_ALIGN_LEFT));
             else
               indiceRiga++;

             // Titolo della gara
             UtilityExcelX.scriviCella(foglioDatiGenerali, 1, indiceRiga,
                 "Titolo", dizStiliExcel.getStileExcel(
                         DizionarioStiliExcelX.STRINGA_ALIGN_RIGHT_BOLD_ITALIC));
             if (destor != null && destor.length() > 0)
               UtilityExcelX.scriviCella(foglioDatiGenerali, 2, indiceRiga++,
                   destor, dizStiliExcel.getStileExcel(
                         DizionarioStiliExcelX.STRINGA_ALIGN_LEFT));
             else
               indiceRiga++;
           }
         }
     } catch (SQLException s) {
       logger.error("Export lotti della gara '" + codgar
           + "': errore durante l'export su Excel dello sheet 'Dati generali' della gara");
       throw s;
     }
    }

   /**
    * Scrittura nel foglio dei Lotti delle informazioni principali
    * della gara per usarle all'inizio dell'operazione di import per verificare
    * se tali informazioni sono uguali a quelle della gara in cui si sta
    * importando
    *
    * @param ngara
    * @param foglioLottiGara
    * @throws GestoreException
    */
   private void setDatiPrincipaliGara(String codgar,
       Sheet foglioLottiGara, boolean isCodificaAutomaticaAttiva,
       DizionarioStiliExcelX dizStiliExcel) throws GestoreException {

     Row riga5 = foglioLottiGara.createRow(4);
     if (riga5 != null) {
       // Valori dei campi da databse
       String codiceGara = null;
       String modlic = null;
       String isLotti = null;
       String isGenere = null;

       // Lettura dei campi da database
       try {
         Vector<?> datiTORN = this.sqlDao.getVectorQuery(
             "select CODGAR, MODLIC from TORN where TORN.CODGAR = ? ",
             new Object[] { codgar });

         if (datiTORN != null && datiTORN.size() > 0) {
           codiceGara = SqlManager.getValueFromVectorParam(datiTORN, 0).toString();
           modlic = SqlManager.getValueFromVectorParam(datiTORN, 1).toString();
         }

         Vector<?> datiV_GARE_TORN = this.sqlDao.getVectorQuery(
             "select ISLOTTI, GENERE from V_GARE_TORN where codgar = ? ",
             new Object[] { codiceGara });

         if (datiV_GARE_TORN != null && datiV_GARE_TORN.size() > 0) {
           isLotti = SqlManager.getValueFromVectorParam(datiV_GARE_TORN, 0).toString();
           isGenere = SqlManager.getValueFromVectorParam(datiV_GARE_TORN, 1).toString();
         }

         // Scrittura valore TORN.CODGAR o GARE.CODGAR1 sul file Excel
         UtilityExcelX.scriviCella(foglioLottiGara, 1, 5, codiceGara,
             dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_NASCOSTA));
         // Scrittura valore CODGAR sul file Excel
         UtilityExcelX.scriviCella(foglioLottiGara, 2, 5, codgar,
             dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_NASCOSTA));
         // Lettura valore GARE.MODLICG sul file Excel
         UtilityExcelX.scriviCella(foglioLottiGara, 3, 5, modlic,
             dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_NASCOSTA));
         // Lettura valore V_GARE_TORN.ISLOTTI sul file Excel
         UtilityExcelX.scriviCella(foglioLottiGara, 4, 5, isLotti,
             dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_NASCOSTA));
         // Lettura valore V_GARE_TORN.ISGENERE sul file Excel
         UtilityExcelX.scriviCella(foglioLottiGara, 5, 5, isGenere,
             dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_NASCOSTA));
         if (isCodificaAutomaticaAttiva)
           UtilityExcelX.scriviCella(foglioLottiGara, 6, 5, "1",
               dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_NASCOSTA));
         else
           UtilityExcelX.scriviCella(foglioLottiGara, 6, 5, "2",
               dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_NASCOSTA));
        //Nuova gestione del campo Soggetto a ribasso
         UtilityExcelX.scriviCella(foglioLottiGara, 7, 5, "1",
             dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_NASCOSTA));
       } catch (SQLException s) {
         throw new GestoreException("Errore durante la scrittura dei dati "
                 + "principali della gara da usare in fase di import per la "
                 + "verifica preliminare dei dati",
             "importExportLottiGara.datiPrincipaliGara", s);
       }
       riga5.setZeroHeight(true);
     }
   }


   /**
    * Verifica preliminare delle informazioni generali della gara: prima di
    * importare i lotti nella gara, si verificano i valori dei
    * campi TORN.CODGAR (o GARE.CODGAR1), GARE.NGARA, GARE.MODLICG,
    * V_GARE_TORN.ISLOTTI e V_GARE_TORN.ISGENERE che in fase di esportazione
    * erano stati salvati nella riga 5 del foglio 'Lotti Gara'
    * Ritorna true se le informazioni generali della gara in cui si sta
    * importando coincidono con quelle presenti nel foglio
    *
    * @param codgar
    * @return Ritorna true se da
    * @throws GestoreException
    */
   private boolean verifichePreliminari(String codgar,
       Sheet foglioLottiGara, boolean isCodificaAutomaticaAttiva)
       throws GestoreException {
     boolean result = true;

     if (logger.isDebugEnabled())
       logger.debug("verifichePreliminari: inizio metodo");

     Row riga5 = foglioLottiGara.getRow(4);
     if (riga5 != null && riga5.getPhysicalNumberOfCells() > 0) {
       // Valori dei campi da databse
       String codiceGara = null;
       String modlic = null;
       String isLotti = null;
       String isGenere = null;
       // Valori dei campi dal foglio dei punteggi economici del file Excel
       String codiceGaraXLS, numeroLottoXLS, modlicgXLS, isLottiXLS, isGenereXLS = null;
       boolean isCodificaAutomaticaXLS = false;

       // Lettura dei campi da database
       try {
         Vector<?> datiTORN = this.sqlDao.getVectorQuery(
             "select CODGAR, MODLIC from TORN where TORN.CODGAR = ? ",
             new Object[] { codgar });

         boolean continuaConfronto = true;
         if (datiTORN == null || (datiTORN != null && datiTORN.size() == 0)) {
           result = false;
           continuaConfronto = false;
         } else {
           codiceGara = SqlManager.getValueFromVectorParam(datiTORN, 0).toString();
           modlic = SqlManager.getValueFromVectorParam(datiTORN, 1).toString();
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
           codiceGaraXLS = UtilityExcelX.leggiCellaString(
               foglioLottiGara, 1, 5);
           // Lettura valore GARE.NGARA dal file Excel
           numeroLottoXLS = UtilityExcelX.leggiCellaString(
               foglioLottiGara, 2, 5);
           // Lettura valore GARE.MODLICG dal file Excel
           modlicgXLS = UtilityExcelX.leggiCellaString(
               foglioLottiGara, 3, 5);
           // Lettura valore V_GARE_TORN.ISLOTTI dal file Excel
           isLottiXLS = UtilityExcelX.leggiCellaString(
               foglioLottiGara, 4, 5);
           // Lettura valore V_GARE_TORN.ISGENERE dal file Excel
           isGenereXLS = UtilityExcelX.leggiCellaString(
               foglioLottiGara, 5, 5);
           String tmp = UtilityExcelX.leggiCellaString(
               foglioLottiGara, 6, 5);
           if ("1".equals(tmp)) isCodificaAutomaticaXLS = true;

           if(modlic== null)
             modlic = "";

           if(modlicgXLS==null)
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
           } else if (!codgar.equals(numeroLottoXLS)) {
             result = false;
             logger.error("Verifiche premilinari import offerta prezzi: avviato "
                 + "import da file Excel dal lotto '"
                 + numeroLottoXLS
                 + "', che e' diversa da quello da cui era stato esportato (Il "
                 + "file Excel era stato esportato dal lotto '"
                 + codgar
                 + "' della gara '"
                 + codiceGara.replaceFirst("$", "")
                 + "')");
           } else if (!modlic.equals(modlicgXLS)) {
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
    * Scrittura su foglio Excel dei dati di lotti di Gara
    *
    * @param codgar
    * @throws GestoreException
    * @throws SQLException
    */
   private void setLottiGara(String codgar, String profiloAttivo, boolean isGaraLotti, DizionarioStiliExcelX dizStiliExcel)
         throws GestoreException, SQLException {

     Sheet foglioListaLotti = workBook.createSheet(FOGLIO_LISTA_LOTTI[0]);
     boolean isCodificaAutomaticaAttiva = this.geneManager.isCodificaAutomatica("GARE", "NGARA");

     if (isCodificaAutomaticaAttiva) {
       arrayCampiVisibili[0] = false; // NGARA non visibile
       arrayIndiceColonnaCampi[0] = -1;
       for (int hi = 1; hi < arrayCampiVisibili.length; hi++){
         arrayIndiceColonnaCampi[hi] = arrayIndiceColonnaCampi[hi] - 1;
       }
     }

     String selectGARA = null;
     String selectCampiLottoGara = null;

     selectGARA = " select GARE.NGARA from GARE where CODGAR1 = ? order by NGARA asc";
     selectCampiLottoGara = " select GARE.NGARA, GARE.CODIGA, GARE.CODCIG, GARE.NOT_GAR, GARE.IMPAPP, GARE.IMPNRL, GARE.IMPSIC, GARE.CUPPRG" +
            " from GARE where NGARA = ? and (GARE.GENERE is null or GARE.GENERE <> 3) order by NGARA asc";

     String selectGarcpvLotto = "select codcpv from garcpv where ngara =? and tipcpv='1'";
     String selectGare1Lotto = "select CODCUI, ANNINT, IMPRIN, DESRIN, IMPSERV, IMPPROR, IMPALTRO, DESOPZ  from GARE1 where NGARA = ?";

     List<String> listaLottiDiGara = new ArrayList<String>();

    try {
       // Ultima riga del foglio da formattare o a cui applicare la formula per
       // il calcolo dell'importo come prodotto quantita' * prezzo unitario
       int numeroRigheDaFormattare = FOGLIO_LISTA_LOTTI_ULTERIORI_RIGHE_DA_FORMATTARE;

       List<?> tmpLottiGara = this.sqlDao.getVectorQueryForList(selectGARA, new Object[] { codgar });
       if (tmpLottiGara != null && tmpLottiGara.size() > 0) {
         for (int ef = 0; ef < tmpLottiGara.size(); ef++) {
           Vector<?> vet = (Vector<?>) tmpLottiGara.get(ef);
           listaLottiDiGara.add(((JdbcParametro) vet.get(0)).getStringValue());
         }
       }

       // Convalida dati per CODIGA
       if (arrayCampiVisibili[1]) {
       CellRangeAddressList addressList = new CellRangeAddressList(
             FOGLIO_LISTA_LOTTI__RIGA_INIZIALE - 1,
             FOGLIO_LISTA_LOTTI__RIGA_INIZIALE
                 + numeroRigheDaFormattare - 1, arrayIndiceColonnaCampi[1] - 1,
             arrayIndiceColonnaCampi[1] - 1);

         DataValidationHelper dvHelper = foglioListaLotti.getDataValidationHelper();
         DataValidationConstraint validazioneCODIGA = dvHelper.createNumericConstraint(
             DataValidationConstraint.ValidationType.INTEGER, DataValidationConstraint.OperatorType.BETWEEN, "0", "999");
         DataValidation dataValidation = dvHelper.createValidation(
             validazioneCODIGA, addressList);
           dataValidation.setSuppressDropDownArrow(true);
           dataValidation.setShowErrorBox(true);

         foglioListaLotti.addValidationData(dataValidation);
      }
       // Convalida dati per CODCIG
       if (arrayCampiVisibili[2]) {
         CellRangeAddressList addressList = new CellRangeAddressList(
               FOGLIO_LISTA_LOTTI__RIGA_INIZIALE - 1,
               FOGLIO_LISTA_LOTTI__RIGA_INIZIALE
                   + numeroRigheDaFormattare - 1, arrayIndiceColonnaCampi[2] - 1,
               arrayIndiceColonnaCampi[2] - 1);


         DataValidationHelper dvHelper = foglioListaLotti.getDataValidationHelper();
         DataValidationConstraint validazioneCODCIG = dvHelper.createNumericConstraint(
             DataValidationConstraint.ValidationType.TEXT_LENGTH, DataValidationConstraint.OperatorType.EQUAL, "10",null);
         DataValidation dataValidation = dvHelper.createValidation(
             validazioneCODCIG, addressList);
           dataValidation.setSuppressDropDownArrow(true);
           dataValidation.setShowErrorBox(true);

           foglioListaLotti.addValidationData(dataValidation);
        }

       int indiceColonna = 1;
       DizionarioCampi dizCampi = DizionarioCampi.getInstance();
       Campo campo = null;
       Row riga = null;

       List<CellStyle> stili = new ArrayList<CellStyle>();

       for (int ii = 0; ii < arrayCampi.length; ii++) {
         if (arrayCampiVisibili[ii])
           foglioListaLotti.setDefaultColumnStyle(
               (short) (arrayIndiceColonnaCampi[ii] - 1),
               dizStiliExcel.getStileExcel(arrayStiliCampi[ii]));
         stili.add(dizStiliExcel.getStileExcel(arrayStiliCampi[ii]));
       }


       foglioListaLotti.setPrintGridlines(true);
       // (0.77 inc <=> 2 cm)
       foglioListaLotti.setMargin(Sheet.TopMargin, 0.77);
       foglioListaLotti.setMargin(Sheet.BottomMargin, 0.77);
       foglioListaLotti.setMargin(Sheet.LeftMargin, 0.77);
       foglioListaLotti.setMargin(Sheet.RightMargin, 0.77);

       // Set delle informazioni per l'area di stampa dei dati del foglio
       PrintSetup printSetup = foglioListaLotti.getPrintSetup();
       printSetup.setLandscape(true);
       printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);

       printSetup.setScale((short) 70); // Presumo significhi zoom al 70%
       // Scrittura dell'intestazione della foglio
       for (int indiceRigaIntestazione = 0; indiceRigaIntestazione < FOGLIO_LISTA_LOTTI__RIGA_INIZIALE; indiceRigaIntestazione++) {

         riga = foglioListaLotti.createRow(indiceRigaIntestazione);
         CellStyle stile = null;
         for (int ii = 0; ii < arrayCampi.length; ii++) {
           if (arrayCampiVisibili[ii]) {
             switch (indiceRigaIntestazione) {
             case 0:
               if (stile == null){
                 stile = dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_INTESTAZIONE);
               }
               UtilityExcelX.scriviCella(foglioListaLotti,
                   arrayIndiceColonnaCampi[ii], indiceRigaIntestazione + 1,
                   arrayTitoloColonne[ii], stile);
               UtilityExcelX.setLarghezzaColonna(foglioListaLotti,
                   arrayIndiceColonnaCampi[ii], arrayLarghezzaColonne[ii]);
               break;

             case FOGLIO_LISTA_LOTTI_RIGA_NOME_FISICO_CAMPI - 1:
               // Seconda riga: nomi fisici dei campi
               if (stile == null)
                 stile = dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_NASCOSTA);
               campo = dizCampi.getCampoByNomeFisico(arrayCampi[ii].substring(arrayCampi[ii].indexOf(".") + 1));
               UtilityExcelX.scriviCella(foglioListaLotti,
                   arrayIndiceColonnaCampi[ii], indiceRigaIntestazione + 1,
                   SCHEMA_CAMPI.concat(".").concat(campo.getNomeFisicoCampo()),
                   stile);
               break;
             case FOGLIO_LISTA_LOTTI__RIGA_INIZIALE - 2:
               // Riga separatrice intestazione dai dati
               if (stile == null){
                 stile = dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_SEPARATRICE);
               }
               UtilityExcelX.scriviCella(foglioListaLotti, arrayIndiceColonnaCampi[ii], indiceRigaIntestazione + 1, " ",
                   stile);
               break;
             }//switch
           }//if
         }  //for interno

           if (indiceRigaIntestazione == 0) {
             // Set titolo della colonna importo
             //UtilityExcel.scriviCella(foglioListaLotti, indiceColonnaImporto, indiceRigaIntestazione + 1, "Importo",stile);
           } else if (indiceRigaIntestazione > 0
               && indiceRigaIntestazione < FOGLIO_LISTA_LOTTI__RIGA_INIZIALE - 2) {
             // Nascondo la riga del foglio
             riga.setZeroHeight(true);
           } else if (indiceRigaIntestazione == FOGLIO_LISTA_LOTTI__RIGA_INIZIALE - 2) {
             //UtilityExcel.scriviCella(foglioListaLotti,    indiceColonnaImporto, indiceRigaIntestazione + 1, " ", stile);
             // Set altezza della riga che divide l'intestazione dai dati
             riga.setHeightInPoints(3);
           }

       }//for esterno

         this.setDatiPrincipaliGara(codgar, foglioListaLotti,isCodificaAutomaticaAttiva, dizStiliExcel);

         int indiceRiga = FOGLIO_LISTA_LOTTI__RIGA_INIZIALE;
         for (int ef = 0; ef < listaLottiDiGara.size(); ef++) {
           String tmpNgara = listaLottiDiGara.get(ef);

           List<?> listaCampiLottoGara = this.sqlDao.getVectorQueryForList(
               selectCampiLottoGara, new Object[] { tmpNgara });

           if (listaCampiLottoGara != null && listaCampiLottoGara.size() > 0) {
             // Reset dell'indice di colonna
             indiceColonna = 1;
             for (int i = 0; i < listaCampiLottoGara.size(); i++) {
               Vector<?> record = (Vector<?>) listaCampiLottoGara.get(i);
               if (record != null && record.size() > 0) {
                 // Campo NGARA (campo visibile se gara a lotti con offerta unica)
                 if (arrayCampiVisibili[0]) {
                   UtilityExcelX.scriviCella(foglioListaLotti, indiceColonna++, indiceRiga + i, tmpNgara, stili.get(indiceColonna - 1));
                 }

                 // Campo CODIGA (campo visibile se gara a lotti con offerta unica
                 // e con codifica automatica)
                 if (arrayCampiVisibili[1]) {
                   String codiga = SqlManager.getValueFromVectorParam( record, 1).getStringValue();
                   if (codiga != null)
                      UtilityExcelX.scriviCella(foglioListaLotti,indiceColonna++, indiceRiga + i, codiga, stili.get(indiceColonna -1));
                   else
                      indiceColonna++;
                 }

                 // Campo CODCIG
                 if (arrayCampiVisibili[2]) {
                   String codcig = SqlManager.getValueFromVectorParam( record, 2).getStringValue();
                   codcig = UtilityStringhe.convertiNullInStringaVuota(codcig);
                   if (!"".equals(codcig))
                      UtilityExcelX.scriviCella(foglioListaLotti,indiceColonna++, indiceRiga + i, codcig, stili.get(indiceColonna -1));
                   else
                      indiceColonna++;
                 }

                 // Campo NOT_GAR
                 if (arrayCampiVisibili[3]) {
                   String not_gar = SqlManager.getValueFromVectorParam( record, 3).getStringValue();
                   not_gar = UtilityStringhe.convertiNullInStringaVuota(not_gar);
                   if (!"".equals(not_gar))
                      UtilityExcelX.scriviCella(foglioListaLotti,indiceColonna++, indiceRiga + i, not_gar, stili.get(indiceColonna - 1));
                   else
                      indiceColonna++;
                 }

                 // Campo IMPAPP
                 if (arrayCampiVisibili[4]) {
                   Double impapp = SqlManager.getValueFromVectorParam( record, 4).doubleValue();
                   if (impapp != null)
                      UtilityExcelX.scriviCella(foglioListaLotti,indiceColonna++, indiceRiga + i, impapp, stili.get(indiceColonna - 1));
                   else
                      indiceColonna++;
                 }

                 // Campo IMPNRL
                 if (arrayCampiVisibili[5]) {
                   Double impnrl = SqlManager.getValueFromVectorParam( record, 5).doubleValue();
                   if (impnrl != null)
                      UtilityExcelX.scriviCella(foglioListaLotti,indiceColonna++, indiceRiga + i, impnrl, stili.get(indiceColonna - 1));
                   else
                      indiceColonna++;
                 }

                 // Campo IMPSIC
                 if (arrayCampiVisibili[6]) {
                   Double impsic = SqlManager.getValueFromVectorParam( record, 6).doubleValue();
                   if (impsic != null)
                      UtilityExcelX.scriviCella(foglioListaLotti,indiceColonna++, indiceRiga + i, impsic, stili.get(indiceColonna - 1));
                   else
                      indiceColonna++;
                 }

                 // Campo CUPPRG
                 if (arrayCampiVisibili[7]) {
                   String cupprg = SqlManager.getValueFromVectorParam( record, 7).stringValue();
                   if (cupprg != null)
                      UtilityExcelX.scriviCella(foglioListaLotti,indiceColonna++, indiceRiga + i, cupprg, stili.get(indiceColonna - 1));
                   else
                      indiceColonna++;
                 }

                 // Campo CODCPV
                 if (arrayCampiVisibili[8]) {
                   Vector codcpvVet = this.sqlDao.getVectorQuery(selectGarcpvLotto, new Object[]{tmpNgara});
                   if(codcpvVet!=null){
                     String codcpv = SqlManager.getValueFromVectorParam( codcpvVet, 0).stringValue();
                     if (codcpv != null)
                        UtilityExcelX.scriviCella(foglioListaLotti,indiceColonna++, indiceRiga + i, codcpv,stili.get(indiceColonna - 1));
                     else
                       indiceColonna++;
                   }else
                      indiceColonna++;
                 }

                 //Lettura campi di GARE1
                 Vector datiGare1 = this.sqlDao.getVectorQuery(selectGare1Lotto, new Object[]{tmpNgara});
                 if(datiGare1!=null && datiGare1.size()>0){
                   // Campo CODCUI
                   if (arrayCampiVisibili[9]) {
                     String codcui = SqlManager.getValueFromVectorParam( datiGare1, 0).stringValue();
                     if (codcui != null)
                        UtilityExcelX.scriviCella(foglioListaLotti,indiceColonna++, indiceRiga + i, codcui, stili.get(indiceColonna - 1));
                     else
                       indiceColonna++;
                   }else
                      indiceColonna++;

                   // Campo ANNINT
                   if (arrayCampiVisibili[10]) {
                     Long annint = SqlManager.getValueFromVectorParam( datiGare1, 1).longValue();
                     if (annint != null)
                        UtilityExcelX.scriviCella(foglioListaLotti,indiceColonna++, indiceRiga + i, annint, stili.get(indiceColonna - 1));
                     else
                       indiceColonna++;
                   }else
                      indiceColonna++;

                   // Campo IMPRIN
                   if (arrayCampiVisibili[11]) {
                     Double imprin = SqlManager.getValueFromVectorParam( datiGare1, 2).doubleValue();
                     if (imprin != null)
                        UtilityExcelX.scriviCella(foglioListaLotti,indiceColonna++, indiceRiga + i, imprin, stili.get(indiceColonna - 1));
                     else
                       indiceColonna++;
                   }else
                      indiceColonna++;

                   // Campo DESRIN
                   if (arrayCampiVisibili[12]) {
                     String desrin = SqlManager.getValueFromVectorParam( datiGare1, 3).stringValue();
                     if (desrin != null)
                        UtilityExcelX.scriviCella(foglioListaLotti,indiceColonna++, indiceRiga + i, desrin, stili.get(indiceColonna - 1));
                     else
                       indiceColonna++;
                   }else
                      indiceColonna++;

                   // Campo IMPSERV
                   if (arrayCampiVisibili[13]) {
                     Double impserv = SqlManager.getValueFromVectorParam( datiGare1, 4).doubleValue();
                     if (impserv != null)
                        UtilityExcelX.scriviCella(foglioListaLotti,indiceColonna++, indiceRiga + i, impserv, stili.get(indiceColonna - 1));
                     else
                       indiceColonna++;
                   }else
                      indiceColonna++;

                   // Campo IMPPROR
                   if (arrayCampiVisibili[14]) {
                     Double imppror = SqlManager.getValueFromVectorParam( datiGare1, 5).doubleValue();
                     if (imppror != null)
                        UtilityExcelX.scriviCella(foglioListaLotti,indiceColonna++, indiceRiga + i, imppror, stili.get(indiceColonna - 1));
                     else
                       indiceColonna++;
                   }else
                      indiceColonna++;

                   // Campo IMPALTRO
                   if (arrayCampiVisibili[15]) {
                     Double impaltro = SqlManager.getValueFromVectorParam( datiGare1, 6).doubleValue();
                     if (impaltro != null)
                        UtilityExcelX.scriviCella(foglioListaLotti,indiceColonna++, indiceRiga + i, impaltro, stili.get(indiceColonna - 1));
                     else
                       indiceColonna++;
                   }else
                      indiceColonna++;

                   // Campo DESOPZ
                   if (arrayCampiVisibili[16]) {
                     String desopz = SqlManager.getValueFromVectorParam( datiGare1, 7).stringValue();
                     if (desopz != null) {
                       UtilityExcelX.scriviCella(foglioListaLotti,indiceColonna++, indiceRiga + i, desopz, stili.get(indiceColonna -1));
                     }else
                       indiceColonna++;
                   }else
                      indiceColonna++;

                 }

                 // Reset dell'indice di colonna
                 indiceColonna = 1;
               }//if record
             }//for i
             indiceRiga += listaCampiLottoGara.size();
           }//if
         }//for ef



    } catch (SQLException s) {
      logger.error("Export lotti della gara '" + codgar
          + "': errore durante l'export su Excel dello sheet 'Lotti della Gara'");
      throw s;
    }


   }//end setLottiGara


   public void cancellaDati(String codgar, boolean isGaraLottiOffDist, boolean isCodificaAutomaticaAttiva)
   throws SQLException, GestoreException {

    if (isGaraLottiOffDist) {
      try {
        List<?> listaLotti = this.sqlDao.getVectorQueryForList(
            "select NGARA from GARE where GARE.CODGAR1 = ? and "
                + "(GARE.GENERE is null or GARE.GENERE <> 3)",
            new Object[] { codgar });

          // Cancellazione dei lotti della gara ad offerte distinte
          //  e delle occorrenze che, se pur
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
                    + "' della gara a lotti '" + codgar + "'");
              }
            }
          }
      } catch (GestoreException g) {
        throw g;
      } catch (SQLException g) {
        logger.error("Errore nella cancellazione delle occorrenze presenti " +
                "nelle tabelle relative alla gara a lotti '" + codgar + "'", g);
        throw g;
      }
    }
  }


   private boolean controlloValoriRiga(List<CampoImportExcel> listaCampiImportExcel,
       int indiceRiga, List<Object> valoriCampiRigaExcel, List<String> valoriCodiga, List<String> tmpListaMsg) throws SQLException, GestoreException {

     boolean rigaImportabile = true;
     Double impappXls = new Double(0);
     Double impnrlXls = new Double(0);
     Double impsicXls = new Double(0);
     for (int colonna = 0; colonna < listaCampiImportExcel.size(); colonna++) {
       CampoImportExcel tmpCampo = listaCampiImportExcel.get(colonna);
       String strCellaExcel = UtilityExcelX.conversioneNumeroColonna(tmpCampo.getColonnaCampo())
           + (indiceRiga + 1);
       Object valore = valoriCampiRigaExcel.get(colonna);

       //Si individua la posizione della colonna CODCPV
       if(arrayCampi[8].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo())))
         indicePosizioneColonnaCodcpv = colonna;

       //Si individua la posizione della colonna da cui cominciano i campi di GARE1
       if(arrayCampi[9].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo())))
         indicePosizioneColonnaInizioCampiGARE1 = colonna;

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
             + " contiene un errore: "
             + ((Error) valore).getMessage());
       } else {
           if (arrayCampiVisibili[0] && arrayCampi[0].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
             String tmpValore = (String) valore;
             if (tmpValore != null ) {
               //Verifica sui dati preesistenti e di sessione se esiste già il codice NGARA:
               Vector<?> ngaraEsistente = this.sqlDao.getVectorQuery(
                   "select count(*) from GARE where NGARA = ? ",
                   new Object[] { tmpValore });
               if (ngaraEsistente != null && ngaraEsistente.size() > 0) {
                 Long res = (Long) SqlManager.getValueFromVectorParam(ngaraEsistente, 0).getValue();
                 if (!new Long(0).equals(res)) {
                   tmpListaMsg.add(" - la cella "
                       + strCellaExcel
                       + " presenta un valore duplicato per il campo Codice Lotto.");
                   rigaImportabile = false;
                 }
               }
             }
           } else if (arrayCampi[1].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
             String tmpValore = null;
             if (valore instanceof Double) {
              Double tmpDoubleValore =  (Double) valore;
              Long tmpLongValore =  tmpDoubleValore.longValue();
              tmpValore = tmpLongValore.toString();
              valoriCampiRigaExcel.set(colonna, "" + ((Double) valore).longValue());
             }
             if (valore instanceof Long){
               Long tmpLongValore =  (Long) valore;
               tmpValore = tmpLongValore.toString();
               valoriCampiRigaExcel.set(colonna, "" + ((Long) valore).longValue());
              }
             if (valore instanceof String){
              tmpValore = (String) valore;
              if (!ImportExportLottiGaraManager.isNumeric(tmpValore)) {
                tmpListaMsg.add(" - la cella "
                    + strCellaExcel
                    + " presenta un valore non numerico per il campo Lotto.");
                rigaImportabile = false;
              }
             }
             if (tmpValore != null) {
               for (int ind = 0; ind < valoriCodiga.size(); ind++) {
                 String codigaPresente = valoriCodiga.get(ind);
                 if (tmpValore.equals(codigaPresente)) {
                   tmpListaMsg.add(" - la cella "
                       + strCellaExcel
                       + " presenta un valore duplicato per il campo Lotto.");
                   rigaImportabile = false;
                 }
               }
             }
           } else if(arrayCampi[2].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
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

             if (tmpValore != null && tmpValore.length() != 10) {
               tmpListaMsg.add(" - la cella "
                   + strCellaExcel
                   + " presenta un valore non ammesso: il codice CIG deve avere una lunghezza di 10 caratteri.");
               rigaImportabile = false;
             } else {
                 if (!this.pgManager.controlloCodiceCIG(tmpValore)) {
                     tmpListaMsg.add(" - la cella "
                             + strCellaExcel
                             + " presenta "
                             + "un valore non ammesso: "
                             + "il codice CIG non e' valido.");
                     rigaImportabile = false;
                 }
             }
           } else if(arrayCampi[7].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
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

             if (tmpValore != null && tmpValore.length() != 15) {
               tmpListaMsg.add(" - la cella "
                   + strCellaExcel
                   + " presenta un valore non ammesso: il codice CUP deve avere una lunghezza di 15 caratteri.");
               rigaImportabile = false;
             }
           } else if(arrayCampi[8].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
             String tmpValore = ((String) valore).toUpperCase();
             valoriCampiRigaExcel.set(colonna, tmpValore.toUpperCase());

             if(tmpValore!=null && !"".equals(tmpValore)){
               Vector<?> datiTabcpv = this.sqlDao.getVectorQuery("select count(cpvcod) from tabcpv where CPVCOD4 = ?", new Object[]{tmpValore});
               if(datiTabcpv!=null){
                 Long  conteggio = SqlManager.getValueFromVectorParam(datiTabcpv, 0).longValue();
                 if (conteggio != null && conteggio.longValue() == 0) {
                   tmpListaMsg.add(" - la cella "
                       + strCellaExcel
                       + " presenta "
                       + "un valore non ammesso: "
                       + "il codice CPV non e' presente in archivio.");
                   rigaImportabile = false;
                 }
               }
             }
           } else if(arrayCampi[9].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
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

             if (tmpValore != null && tmpValore.length() < 20 || tmpValore != null && tmpValore.length() > 22) {
               tmpListaMsg.add(" - la cella "
                   + strCellaExcel
                   + " presenta un valore non ammesso: il codice CUI deve avere una lunghezza compresa fra i 20 ed i 22 caratteri.");
               rigaImportabile = false;
             }
           } else {
             switch (tmpCampo.getTipoCampo()) {
             case Campo.TIPO_STRINGA:
             case Campo.TIPO_NOTA:
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
                 } else {
                   String tmp = (String) valore;
                   if (tmp.length() > tmpCampo.getLunghezzaCampo()) {
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
                 // In Excel, la formattazione di un campo di tipo numerico comporta
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
                 double tmpValore = UtilityMath.round(valoreCampo, tmpCampo.getCifreDecimali());
                 if (valoreCampo != tmpValore){
                   valoriCampiRigaExcel.set(colonna, new Double(tmpValore));
                 }
                 //controllo che IMPSIC + IMPNLR <= IMPAPP
                 Double valCampo = null ;
                 if (arrayCampi[4].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
                   valCampo = ((Double) valoriCampiRigaExcel.get(colonna)).doubleValue();
                   impappXls = valCampo;
                   if(impappXls == null){
                     impappXls = new Double(0);
                   }
                 }
                 if (arrayCampi[5].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
                   valCampo = ((Double) valoriCampiRigaExcel.get(colonna)).doubleValue();
                   impnrlXls = valCampo;
                   if(impnrlXls == null){
                     impnrlXls = new Double(0);
                   }
                 }
                 if (arrayCampi[6].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
                   valCampo = ((Double) valoriCampiRigaExcel.get(colonna)).doubleValue();
                   impsicXls = valCampo;
                   if(impsicXls == null){
                     impsicXls = new Double(0);
                   }
                 }
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
     }//for

     //controllo che IMPSIC + IMPNLR <= IMPAPP
     if (impappXls < UtilityMath.round((impnrlXls + impsicXls),2)){
       tmpListaMsg.add(" - La somma dell'importo sicurezza e dell'importo non soggetto a ribasso supera l'importo a base di gara");
       rigaImportabile = false;
     }

     return rigaImportabile;
   }

   private boolean controlloValoriRiga(List<CampoImportExcel> listaCampiImportExcel,
       int indiceRiga, List<Object> valoriCampiRigaExcelGARE, List<String> valoriCodiga, List<String> tmpListaMsg,HashMap<String,List<Object>> hmValoriCampiRigaExcel) throws SQLException, GestoreException {

     boolean rigaImportabile = true;
     Double impappXls = new Double(0);
     Double impnrlXls = new Double(0);
     Double impsicXls = new Double(0);

     List<Object> valoriCampiRigaExcelGARE1 = new ArrayList<Object>();
     List<Object> valoriCampiRigaExcelGARCPV = new ArrayList<Object>();

     for (int colonna = 0; colonna < listaCampiImportExcel.size(); colonna++) {
       CampoImportExcel tmpCampo = listaCampiImportExcel.get(colonna);
       String strCellaExcel = UtilityExcelX.conversioneNumeroColonna(tmpCampo.getColonnaCampo())
           + (indiceRiga + 1);
       Object valore = valoriCampiRigaExcelGARE.get(colonna);
       if (valore == null) {
         if (tmpCampo.isObbligatorio()) {
           if (tmpCampo.getValoreDiDefault() != null) {
             valoriCampiRigaExcelGARE.set(colonna, tmpCampo.getValoreDiDefault());
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
             + " contiene un errore: "
             + ((Error) valore).getMessage());
       } else {
           if (arrayCampiVisibili[0] && arrayCampi[0].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
             String tmpValore = (String) valore;
             if (tmpValore != null ) {
               //Verifica sui dati preesistenti e di sessione se esiste già il codice NGARA:
               Vector<?> ngaraEsistente = this.sqlDao.getVectorQuery(
                   "select count(*) from GARE where NGARA = ? ",
                   new Object[] { tmpValore });
               if (ngaraEsistente != null && ngaraEsistente.size() > 0) {
                 Long res = (Long) SqlManager.getValueFromVectorParam(ngaraEsistente, 0).getValue();
                 if (!new Long(0).equals(res)) {
                   tmpListaMsg.add(" - la cella "
                       + strCellaExcel
                       + " presenta un valore duplicato per il campo Codice Lotto.");
                   rigaImportabile = false;
                 }
               }
             }
           } else if (arrayCampi[1].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
             String tmpValore = null;
             if (valore instanceof Double) {
              Double tmpDoubleValore =  (Double) valore;
              Long tmpLongValore =  tmpDoubleValore.longValue();
              tmpValore = tmpLongValore.toString();
              valoriCampiRigaExcelGARE.set(colonna, "" + ((Double) valore).longValue());
             }
             if (valore instanceof Long){
               Long tmpLongValore =  (Long) valore;
               tmpValore = tmpLongValore.toString();
               valoriCampiRigaExcelGARE.set(colonna, "" + ((Long) valore).longValue());
              }
             if (valore instanceof String){
              tmpValore = (String) valore;
              if (!ImportExportLottiGaraManager.isNumeric(tmpValore)) {
                tmpListaMsg.add(" - la cella "
                    + strCellaExcel
                    + " presenta un valore non numerico per il campo Lotto.");
                rigaImportabile = false;
              }
             }
             if (tmpValore != null) {
               for (int ind = 0; ind < valoriCodiga.size(); ind++) {
                 String codigaPresente = valoriCodiga.get(ind);
                 if (tmpValore.equals(codigaPresente)) {
                   tmpListaMsg.add(" - la cella "
                       + strCellaExcel
                       + " presenta un valore duplicato per il campo Lotto.");
                   rigaImportabile = false;
                 }
               }
             }
           } else if(arrayCampi[2].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
             String tmpValore = null;
             if (valore instanceof Double) {
                 Double tmpDoubleValore =  (Double) valore;
                 Long tmpLongValore =  tmpDoubleValore.longValue();
                 tmpValore = tmpLongValore.toString();
                 valoriCampiRigaExcelGARE.set(colonna, "" + ((Double) valore).longValue());
             }
             if (valore instanceof Long) {
                 Long tmpLongValore =  (Long) valore;
                 tmpValore = tmpLongValore.toString();
                 valoriCampiRigaExcelGARE.set(colonna, "" + ((Long) valore).longValue());
             }
             if (valore instanceof String) {
                 tmpValore = ((String) valore).toUpperCase();
                 valoriCampiRigaExcelGARE.set(colonna, tmpValore.toUpperCase());
             }

             if (tmpValore != null && tmpValore.length() != 10) {
               tmpListaMsg.add(" - la cella "
                   + strCellaExcel
                   + " presenta un valore non ammesso: il codice CIG deve avere una lunghezza di 10 caratteri.");
               rigaImportabile = false;
             } else {
                 if (!this.pgManager.controlloCodiceCIG(tmpValore)) {
                     tmpListaMsg.add(" - la cella "
                             + strCellaExcel
                             + " presenta "
                             + "un valore non ammesso: "
                             + "il codice CIG non e' valido.");
                     rigaImportabile = false;
                 }
             }
           } else if(arrayCampi[7].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
             String tmpValore = null;
             if (valore instanceof Double) {
                 Double tmpDoubleValore =  (Double) valore;
                 Long tmpLongValore =  tmpDoubleValore.longValue();
                 tmpValore = tmpLongValore.toString();
                 valoriCampiRigaExcelGARE.set(colonna, "" + ((Double) valore).longValue());
             }
             if (valore instanceof Long) {
                 Long tmpLongValore =  (Long) valore;
                 tmpValore = tmpLongValore.toString();
                 valoriCampiRigaExcelGARE.set(colonna, "" + ((Long) valore).longValue());
             }
             if (valore instanceof String) {
                 tmpValore = ((String) valore).toUpperCase();
                 valoriCampiRigaExcelGARE.set(colonna, tmpValore.toUpperCase());
             }

             if (tmpValore != null && tmpValore.length() != 15) {
               tmpListaMsg.add(" - la cella "
                   + strCellaExcel
                   + " presenta un valore non ammesso: il codice CUP deve avere una lunghezza di 15 caratteri.");
               rigaImportabile = false;
             }
           } else if(arrayCampi[8].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
             String tmpValore = ((String) valore).toUpperCase();
             valoriCampiRigaExcelGARE.set(colonna, tmpValore.toUpperCase());
             if(tmpValore!=null && !"".equals(tmpValore)){
               Vector<?> datiTabcpv = this.sqlDao.getVectorQuery("select count(cpvcod) from tabcpv where CPVCOD4 = ?", new Object[]{tmpValore});
               if(datiTabcpv!=null){
                 Long  conteggio = SqlManager.getValueFromVectorParam(datiTabcpv, 0).longValue();
                 if (conteggio != null && conteggio.longValue() == 0) {
                   tmpListaMsg.add(" - la cella "
                       + strCellaExcel
                       + " presenta "
                       + "un valore non ammesso: "
                       + "il codice CPV non e' presente in archivio.");
                   rigaImportabile = false;
                 }
               }
             }
           } else if(arrayCampi[9].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
             String tmpValore = null;
             if (valore instanceof Double) {
                 Double tmpDoubleValore =  (Double) valore;
                 Long tmpLongValore =  tmpDoubleValore.longValue();
                 tmpValore = tmpLongValore.toString();
                 valoriCampiRigaExcelGARE.set(colonna, "" + ((Double) valore).longValue());
             }
             if (valore instanceof Long) {
                 Long tmpLongValore =  (Long) valore;
                 tmpValore = tmpLongValore.toString();
                 valoriCampiRigaExcelGARE.set(colonna, "" + ((Long) valore).longValue());
             }
             if (valore instanceof String) {
                 tmpValore = ((String) valore).toUpperCase();
                 valoriCampiRigaExcelGARE.set(colonna, tmpValore.toUpperCase());
             }

             if (tmpValore != null && tmpValore.length() < 20 || tmpValore != null && tmpValore.length() > 22) {
               tmpListaMsg.add(" - la cella "
                   + strCellaExcel
                   + " presenta un valore non ammesso: il codice CUI deve avere una lunghezza compresa fra i 20 ed i 22 caratteri.");
               rigaImportabile = false;
             }
           } else {
             switch (tmpCampo.getTipoCampo()) {
             case Campo.TIPO_STRINGA:
             case Campo.TIPO_NOTA:
                 if (arrayCampi[1].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(
                     tmpCampo.getNomeFisicoCampo()))) {
                   // Il campo CODIGA nel foglio Excel e' di tipo numerico, mentre su
                   // DB e' di tipo stringa: a questo punto si converte in stringa il
                   // valore numerico letto dal foglio Excel
                   if (valore instanceof Long)
                     valoriCampiRigaExcelGARE.set(colonna, ""
                         + ((Long) valore).longValue());
                   if (valore instanceof Double)
                     valoriCampiRigaExcelGARE.set(colonna, ""
                         + ((Double) valore).longValue());
                 } else {
                   String tmp = (String) valore;
                   if (tmp.length() > tmpCampo.getLunghezzaCampo()) {
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
                 valoriCampiRigaExcelGARE.set(colonna, null);
               } else {
                 // In Excel, la formattazione di un campo di tipo numerico comporta
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
                 double valoreCampo = ((Double) valoriCampiRigaExcelGARE.get(colonna)).doubleValue();
                 double tmpValore = UtilityMath.round(valoreCampo, tmpCampo.getCifreDecimali());
                 if (valoreCampo != tmpValore){
                   valoriCampiRigaExcelGARE.set(colonna, new Double(tmpValore));
                 }
                 //controllo che IMPSIC + IMPNLR <= IMPAPP
                 Double valCampo = null ;
                 if (arrayCampi[4].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
                   valCampo = ((Double) valoriCampiRigaExcelGARE.get(colonna)).doubleValue();
                   impappXls = valCampo;
                   if(impappXls == null){
                     impappXls = new Double(0);
                   }
                 }
                 if (arrayCampi[5].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
                   valCampo = ((Double) valoriCampiRigaExcelGARE.get(colonna)).doubleValue();
                   impnrlXls = valCampo;
                   if(impnrlXls == null){
                     impnrlXls = new Double(0);
                   }
                 }
                 if (arrayCampi[6].equalsIgnoreCase(SCHEMA_CAMPI.concat(".").concat(tmpCampo.getNomeFisicoCampo()))) {
                   valCampo = ((Double) valoriCampiRigaExcelGARE.get(colonna)).doubleValue();
                   impsicXls = valCampo;
                   if(impsicXls == null){
                     impsicXls = new Double(0);
                   }
                 }
               }
               break;
             case Campo.TIPO_DATA:
               if (!(valore instanceof Date)) {
                 tmpListaMsg.add("- la cella "
                     + strCellaExcel
                     + " non e' in formato data.");
                 valoriCampiRigaExcelGARE.set(colonna, null);
               }
               break;
             }
           }
       }
     }//for

     //controllo che IMPSIC + IMPNLR <= IMPAPP
     if (impappXls < UtilityMath.round((impnrlXls + impsicXls),2)){
       tmpListaMsg.add(" - La somma dell'importo sicurezza e dell'importo non soggetto a ribasso supera l'importo a base di gara");
       rigaImportabile = false;
     }

     return rigaImportabile;
   }

   private List<Object> letturaRiga(Row rigaFoglioExcel, List<?> listaCampiDaImportare) {
     List<Object> valoriCampi = new ArrayList<Object>();
     Cell cella = null;
     FormulaEvaluator evaluator = workBook.getCreationHelper().createFormulaEvaluator();
     for (int i = 0; i < listaCampiDaImportare.size(); i++) {
       CampoImportExcel campoImportExcel = (CampoImportExcel) listaCampiDaImportare.get(i);
       cella = rigaFoglioExcel.getCell(campoImportExcel.getColonnaCampo() - 1);
       if (cella != null) {
         switch (cella.getCellType()) {
         case Cell.CELL_TYPE_STRING:
           valoriCampi.add(cella.getRichStringCellValue().toString());
           break;
         case Cell.CELL_TYPE_NUMERIC:
           if (HSSFDateUtil.isCellDateFormatted(cella))
             valoriCampi.add(HSSFDateUtil.getJavaDate(cella.getNumericCellValue()));
           else
             valoriCampi.add(new Double(cella.getNumericCellValue()));
           break;
         case Cell.CELL_TYPE_BOOLEAN:
           valoriCampi.add(new Boolean(cella.getBooleanCellValue()));
           break;
         case Cell.CELL_TYPE_BLANK:
           valoriCampi.add(null);
           break;
         case Cell.CELL_TYPE_ERROR:
           valoriCampi.add(new Error("Cella con errore"));
           break;
         case Cell.CELL_TYPE_FORMULA:
           switch (evaluator.evaluateFormulaCell(cella)) {
           case Cell.CELL_TYPE_BOOLEAN:
             valoriCampi.add(new Boolean(cella.getBooleanCellValue()));
             break;
           case Cell.CELL_TYPE_NUMERIC:
             if (HSSFDateUtil.isCellDateFormatted(cella))
               valoriCampi.add(HSSFDateUtil.getJavaDate(cella.getNumericCellValue()));
             else
               valoriCampi.add(new Double(cella.getNumericCellValue()));
             break;
           case Cell.CELL_TYPE_STRING:
             valoriCampi.add(cella.getRichStringCellValue().toString());
             break;
           case Cell.CELL_TYPE_BLANK:
             valoriCampi.add(null);
             break;
           case Cell.CELL_TYPE_ERROR:
             valoriCampi.add(new Error(
                 "Cella con formula, il cui risultato e' un errore"));
             break;
           }
           break;
         }
       } else {
         String letteraColonna = UtilityExcelX.conversioneNumeroColonna(i + 1);
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


   private void inizializzaDocumentazioneDitte(String codiceGaraDestinazione, String nGaraDestinazione, PgManager pgManager) throws SQLException, GestoreException{
     List<?> listaDitteCopiate = this.sqlDao.getVectorQueryForList(
         "select DITTAO FROM DITG WHERE CODGAR5 = ? AND NGARA5= ? ",
         new Object[] { codiceGaraDestinazione,nGaraDestinazione });
       if (listaDitteCopiate != null && listaDitteCopiate.size() > 0) {
         for (int j=0; j < listaDitteCopiate.size(); j++) {
           String codiceDitta = (String) SqlManager.getValueFromVectorParam(
               listaDitteCopiate.get(j), 0).getValue();

           //Viene popolata la IMPRDOCG a partire dalle occorrenze di DOCUMGARA,
           //della gara destinazione, impostando SITUAZDOCI a 2 e PROVENI a 1
           pgManager.inserimentoDocumentazioneDitta(codiceGaraDestinazione, nGaraDestinazione, codiceDitta);
         }
      }
   }

   private static boolean isNumeric(String str) {

     boolean numerico = true;
     char[] seq = str.toCharArray();

     for (int i=0; i < seq.length; i++) {
       try {
         Integer.parseInt(Character.toString(seq[i]));
       } catch (Exception e) {
         numerico = false;
       }
     }

     return numerico;
   }

}
