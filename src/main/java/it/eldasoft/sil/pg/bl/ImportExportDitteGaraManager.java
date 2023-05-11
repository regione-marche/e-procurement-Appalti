/*
 * Created on 23/dic/2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.dao.SqlDao;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.excel.CampoImportExcel;
import it.eldasoft.sil.pg.bl.excel.DizionarioStiliExcel;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiRicezioneFunction;
import it.eldasoft.sil.pg.tags.gestori.submit.GestoreDITG;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityExcel;
import it.eldasoft.utils.utility.UtilityFiscali;

/**
 * Classe per la gestione delle funzionalita' di import/export dei lotti su foglio Excel
 *
 * @author Riccardo.Peruzzo
 */


public class ImportExportDitteGaraManager {

  static Logger                     logger = Logger.getLogger(ImportExportDitteGaraManager.class);

  private SqlDao                    sqlDao;

  /** Manager con funzionalita' generali */
  private GeneManager               geneManager;

  /** Manager dei tabellati */
  private TabellatiManager          tabellatiManager;

  /** Manager per l'applicazione PG */
  private PgManager                 pgManager;

  /** Manager della W_GENCHIAVI */
  private GenChiaviManager			genChiaviManager;

  /** Manager SQL per le operazioni su database */
  private SqlManager sqlManager;

  private MEPAManager mepaManager;

  public void setSqlDao(SqlDao sqlDao) {
    this.sqlDao = sqlDao;
  }

  public void setMepaManager(MEPAManager mepaManager) {
	    this.mepaManager = mepaManager;
	  }

  public void setSqlManager(SqlManager sqlManager) {
      this.sqlManager = sqlManager;
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

  private final String       FOGLIO_LISTA_DITTE[]                             = { "Lista ditte" };

  private final int          FOGLIO_LISTA_DITTE__RIGA_INIZIALE                = 3;

  private final int          IMPORT_NUMERO_RIGHE_CONSECUTIVE_VUOTE            = 10;

  private final static int   NUMERO_COLONNE_MODELLO                           = 9;

  private final static int   FOGLIO_LISTA_DITTE__RIGA_INTESTAZIONE            = 1;

  /*
   * Etichette delle colonne del modello
   */
  private final static String LABEL_RAG_SOC                                   = "Ragione sociale ditta";
  private final static String LABEL_COD_FISC                                  = "Codice fiscale";
  private final static String LABEL_P_IVA                                     = "Partita Iva";
  private final static String LABEL_ID_ESTERO                                 = "Identificativo fiscale estero";
  private final static String LABEL_NAZIONE                                   = "Nazione";
  private final static String LABEL_EMAIL                                     = "Email";
  private final static String LABEL_PEC                                       = "PEC";
  private final static String LABEL_DENOMINAZIONE_RT                          = "Denominazione RT";
  private final static String LABEL_RUOLO_RT                                  = "Ruolo in RT";

  private final static String VETTORE_LABEL_COLONNE[]= new String[]{LABEL_RAG_SOC, LABEL_COD_FISC, LABEL_P_IVA, LABEL_ID_ESTERO,
      LABEL_NAZIONE, LABEL_EMAIL, LABEL_PEC, LABEL_DENOMINAZIONE_RT, LABEL_RUOLO_RT};

  private Workbook       workBook                                                     = null;

  private String[]           arrayCampi;
  private int[]              arrayStiliCampi;
  private String[]           arrayTitoloColonne;
  private int[]              arrayLarghezzaColonne;
  private boolean[]          arrayCampiVisibili;
  private int[]              arrayIndiceColonnaCampi;

  private void importLotti(Sheet foglio, String ngara,
      List<CampoImportExcel> listaCampiImportExcel,
      HttpSession session, LoggerImportDitte loggerImport,
      boolean isCodificaAutomaticaAttiva,String genereGara, HttpServletRequest request, TransactionStatus status)
                throws SQLException, SqlComposerException, GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("importLottiGara: inizio metodo");

    this.cancellaDati(ngara, genereGara, request, status);

    int indiceRiga = FOGLIO_LISTA_DITTE__RIGA_INIZIALE - 1;

    int ultimaRigaValorizzata = foglio.getLastRowNum() +1;

    HashMap<String, Object> valoriCampi = new HashMap<String, Object>();

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
          List<String> tmpListaMsgElencoDitte = new ArrayList<String>();
          // Controllo valori di GARE
          String ragioneSociale = (String) valoriCampiRigaExcel.get(0);
          String codiceFiscale = (String) valoriCampiRigaExcel.get(1);
          String partitaIVA = (String) valoriCampiRigaExcel.get(2);
          String IDEstero = (String) valoriCampiRigaExcel.get(3);
          String nazione = (String) valoriCampiRigaExcel.get(4);
          String email = (String) valoriCampiRigaExcel.get(5);
          String PEC = (String) valoriCampiRigaExcel.get(6);
          String denRT = (String) valoriCampiRigaExcel.get(7);
          String ruoloRT = (String) valoriCampiRigaExcel.get(8);
          Long tipimp = new Long(1);
          Long codNazione = null;

          rigaImportabile = this.controlloValoriRiga(listaCampiImportExcel,
              indiceRiga, valoriCampiRigaExcel, valoriCodiga, tmpListaMsg, ngara);

          if (rigaImportabile) {
            if (logger.isDebugEnabled())
                logger.debug("I dati presenti nella riga "
                  + (indiceRiga + 1)
                  + " sono nel formato previsto");

          }

          if (tmpListaMsg.size() > 0) {

              loggerImport.addMessaggioErrore("Riga " + (indiceRiga + 1)
                      + " non importata:");

              loggerImport.addListaMessaggiErrore(tmpListaMsg);

          }
          if (rigaImportabile) {
            try {

            	String indentificativo=null;
            	String insertNazione=null;
            	String insertTipimp=null;
            	String insertNomimp=ragioneSociale;
            	String insertNomimpRT=denRT;
            	Long tipimpRT;
            	String codimpRT="";

        		if(ragioneSociale!= null && ragioneSociale.length()>60) {
        			insertNomimp=ragioneSociale.substring(0,60);
        		}

            	if(codiceFiscale!=null) {
            		indentificativo=codiceFiscale;
        		}else {
        			indentificativo=IDEstero;
        			partitaIVA=IDEstero;
        		}

            	String codimp = isOperatoreRegistrato(indentificativo, partitaIVA);
                if(codimp == null || "".equals(codimp)){

                	//Verifico se la nazione ha un valore valido
                	if(nazione!=null) {
                		Vector<?> vOccNAZ = this.sqlDao.getVectorQuery(
                				"select tab1tip from tab1 where tab1cod='Ag010' and upper(tab1desc) = ?",
                				new Object[] { nazione.toUpperCase() });

                		codNazione = (Long) SqlManager.getValueFromVectorParam(vOccNAZ, 0).getValue();
                		//insertNazione = (String) Long.toString(codNazione);
                	}


            		codimp = insertDittaAnagrafica(tipimp,ragioneSociale,insertNomimp,indentificativo,partitaIVA,email,PEC,codNazione);
            		loggerImport.incrementanumeroAnagraficaDitteInserite();
                }else {
         		    Vector<?> vTipimp = this.sqlDao.getVectorQuery(
                            "select tipimp,nomimp from impr where codimp=?",
                            new Object[] { codimp });

         		   tipimp = (Long) SqlManager.getValueFromVectorParam(vTipimp, 0).getValue();
         		  insertNomimp = (String)SqlManager.getValueFromVectorParam(vTipimp, 1).getValue();
                }

            	if("MANDATARIA".equals(ruoloRT)) {
            		if(tipimp==null || (tipimp!=null && tipimp<=new Long(5))) {
            			tipimpRT = new Long(3);
            		}else {
            			tipimpRT = new Long(10);
            		}

            		if(denRT!= null && denRT.length()>60) {
            			insertNomimpRT=denRT.substring(0,60);
            		}

            		codimpRT = insertDittaAnagraficaRT(tipimpRT,denRT,insertNomimpRT);
            		insertDittaRagimp(codimpRT,codimp,insertNomimp,new Long(1));
            	}

            	if("MANDANTE".equals(ruoloRT)) {

            		Vector<?> vCodimpRT = this.sqlDao.getVectorQuery(
                              "select codimp from ditg d,impr i where d.dittao=i.codimp and d.ngara5=? and "
                              + "i.nomest=? and (i.tipimp=3 or i.tipimp=10)",
                                 new Object[] { ngara,denRT });

            		codimpRT = (String) SqlManager.getValueFromVectorParam(vCodimpRT, 0).getValue();

            		insertDittaRagimp(codimpRT,codimp,insertNomimp,new Long(2));
            	}
            	String select="select codgar1 from gare where ngara = ?";
            	String codgar = (String) sqlManager.getObject(select, new Object[]{ngara});
            	if(!"MANDANTE".equals(ruoloRT)) {
            		String codimpDITG=codimp;
            		String nomimpDITG=insertNomimp;
            		if("MANDATARIA".equals(ruoloRT)) {
            			codimpDITG=codimpRT;
            			nomimpDITG=insertNomimpRT;
            		}
                //Dopo aver aggiunto la ditta in anagrafica (se non era già presente). La aggiungo alla gara corrente
                Vector elencoCampi = new Vector();
                elencoCampi.add(new DataColumn("DITG.NGARA5",
                    new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
                elencoCampi.add(new DataColumn("DITG.CODGAR5",
                    new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar)));
                elencoCampi.add(new DataColumn("DITG.DITTAO",
                    new JdbcParametro(JdbcParametro.TIPO_TESTO, codimpDITG)));

                //campi che si devono inserire perchè adoperati nel gestore
                //GestoreFasiRicezione
                elencoCampi.add(new DataColumn("DITG.NOMIMO",
                    new JdbcParametro(JdbcParametro.TIPO_TESTO, nomimpDITG)));
                elencoCampi.add(new DataColumn("DITG.NPROGG",
                    new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
                elencoCampi.add(new DataColumn("DITG.NUMORDPL",
                    new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
                /*elencoCampi.add(new DataColumn("DITG.ACQUISIZIONE",
                    new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(7))));
                elencoCampi.add(new DataColumn("DITG.NGARAEXP",
                    new JdbcParametro(JdbcParametro.TIPO_TESTO, codgarOrigine)));*/

                DataColumnContainer containerDITG = new DataColumnContainer(elencoCampi);

              	// Gestione dell'inserimento di una ditta in una gara
                GestoreDITG gestoreDITG = new GestoreDITG();
                gestoreDITG.setRequest(request);

                gestoreDITG.inserisci(status, containerDITG);

                loggerImport.addDitteImportate(codimpDITG + " - " + nomimpDITG);

                // Inizializzazione documenti della ditta
                pgManager.inserimentoDocumentazioneDitta(codgar, ngara, codimp);

                Double faseGara = new Double(Math.floor(GestioneFasiRicezioneFunction.FASE_RICEZIONE_PLICHI / 10));
                Long faseGaraLong = new Long(faseGara.longValue());
                if("3".equals(genereGara)){
                  select = "select ngara from gare where codgar1 = ? and ngara != codgar1";
                  List lottiPlicoUnico = sqlManager.getListVector(select, new Object[]{codgar});
                  if(lottiPlicoUnico!=null && lottiPlicoUnico.size()>0){
                    mepaManager.gestioneLottiOffertaUnica(ngara, codimp, containerDITG, gestoreDITG,null,faseGaraLong,status,"INS",false,null,null);
                  }
                }
            	}

              if("MANDANTE".equals(ruoloRT)) {
            	  loggerImport.incrementaRecordImportatiRT();
              }
              else {
            	  loggerImport.incrementaRecordImportati();
              }

              loggerImport.incrementaRecordAggiornati();

              if (logger.isDebugEnabled())
                logger.debug("Inserimento dei valori della riga "
                    + (indiceRiga + 1) + " avvenuta con successo");

              continue;
            } catch (SQLException s) {
              logger.error("Errore nell'inserimento nella tabella", s);
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
          loggerImport.incrementaRecordNonImportati();
        }
      } else {//rigafoglioexcel
        if (logger.isDebugEnabled())
          logger.debug("La riga " + (indiceRiga + 1) + " e' stata saltata "
              + "perche' non inizializzata");

        contatoreRigheVuote++;
        //loggerImport.incrementaRecordNonImportati();
      }
    }//for

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
 public LoggerImportDitte importDitteGara(
     UploadFileForm fileExcel, String ngara, HttpSession session, String genereGara, HttpServletRequest request, TransactionStatus status)
   throws FileNotFoundException, IOException, GestoreException, Exception {

   if (logger.isDebugEnabled())
     logger.debug("importDitteGara: inizio metodo");

   boolean isCodificaAutomaticaAttiva = this.geneManager.isCodificaAutomatica("GARE", "NGARA");

   this.setDefinizioni();

   LoggerImportDitte loggerImport = new LoggerImportDitte();
   this.workBook = WorkbookFactory.create(fileExcel.getSelezioneFile().getInputStream());

   Sheet foglioLottiGara = null;
   int indiceFoglio = 0;
   // Flag per indicare se eseguire il controllo delle informazioni preliminari
   // del foglio Excel che si sta importando: se il foglio Excel e' esportato
   // dalla funzione di export dalla pagina 'Lista dei Lotti di Gara'
   boolean eseguireVerifichePreliminari = true;
   if (workBook.getSheet(FOGLIO_LISTA_DITTE[0]) != null) {
     foglioLottiGara = workBook.getSheet(FOGLIO_LISTA_DITTE[0]);
   }
   if (foglioLottiGara != null) {

     if (logger.isDebugEnabled()) {
         logger.debug("importDitteGara: inizio lettura del foglio '"
             + FOGLIO_LISTA_DITTE[0] + "'");
     }

     // Controllo del numero di campi obbligatori su tutto il file
     Map<String,List<?>> mappa = this.getListaCampiDaImportare(foglioLottiGara, FOGLIO_LISTA_DITTE[indiceFoglio], null);
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
          this.importLotti(foglioLottiGara, ngara, listaCampiImportExcel,
             session, loggerImport, isCodificaAutomaticaAttiva, genereGara, request, status);

        } else {

             logger.error("importDitteGara: Nel foglio '"
                 + FOGLIO_LISTA_DITTE[indiceFoglio] + "' non sono "
                 + "presenti i nomi fisici dei campi che da analisi sono SEMPRE "
                 + "visibili (CODVOC, PREZUN)");
        }
     } else if (mappa.get("listaErrori") != null) {
       List<?> listaErrori = mappa.get("listaErrori");
       String msg="Il formato del file Excel non e' compatibile.\r\n";
       for (int l = 0; l < listaErrori.size(); l++)
         msg += (String) listaErrori.get(l) + "\r\n";

       loggerImport.addMsgVerificaFoglio(msg);
     } else {
       String msgFoglio = FOGLIO_LISTA_DITTE[indiceFoglio];
       logger.error("Il metodo getListaCampiDaImportare ha restituito null. "
           + "Si consiglia di andare in debug importando lo stesso file Excel "
           + "che ha generato questo errore, per capire la causa.");
       loggerImport.addMsgVerificaFoglio("Si e' verificato un errore "
           + "inaspettato nella lettura dell'intestazione del foglio '"
           + msgFoglio + "'");
     }
   } else {
     String msgFoglio = FOGLIO_LISTA_DITTE[indiceFoglio];

     String tmp = "Il formato del file Excel non e' compatibile: non e' "
         + "presente il foglio '" + msgFoglio + "'";
     logger.error(tmp);
     loggerImport.addMsgVerificaFoglio(tmp);
   }
   if (logger.isDebugEnabled()){
     String msgFoglio = FOGLIO_LISTA_DITTE[indiceFoglio];
     logger.debug("importOffertaPrezzi: fine lettura del foglio '"
         + msgFoglio + "'");
   }

   if (logger.isDebugEnabled()) {
     logger.debug("importDitteGara: fine metodo");
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

	       int numeroTotaleCampi = NUMERO_COLONNE_MODELLO;
	       int cnt = 0;

	       arrayCampi = new String[numeroTotaleCampi];
	       arrayStiliCampi = new int[numeroTotaleCampi];
	       arrayTitoloColonne = new String[numeroTotaleCampi];
	       arrayLarghezzaColonne = new int[numeroTotaleCampi];
	       arrayCampiVisibili = new boolean[numeroTotaleCampi];
	       arrayIndiceColonnaCampi = new int[numeroTotaleCampi];

	       // 0
	       arrayCampi[cnt] = "GARE.IMPR.NOMEST";
	       arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
	       arrayTitoloColonne[cnt] = "Ragione sociale ditta";
	       arrayLarghezzaColonne[cnt] = 80;
	       arrayCampiVisibili[cnt] = true;

	       // 1
	       cnt++;
	       arrayCampi[cnt] = "GARE.IMPR.CFIMP";
	       arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
	       arrayTitoloColonne[cnt] = "Codice fiscale";
	       arrayLarghezzaColonne[cnt] = 16;
	       arrayCampiVisibili[cnt] = true;

	       // 2
	       cnt++;
	       arrayCampi[cnt] = "GARE.IMPR.PIVIMP";
	       arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
	       arrayTitoloColonne[cnt] = "Partita Iva";
	       arrayLarghezzaColonne[cnt] = 10;
	       arrayCampiVisibili[cnt] = true;

	       // 3
	       cnt++;
	       arrayCampi[cnt] = "GARE.IMPR.PIVIMP";
	       arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
	       arrayTitoloColonne[cnt] = "Identificativo fiscale estero (solo se estero)";
	       arrayLarghezzaColonne[cnt] = 80;
	       arrayCampiVisibili[cnt] = true;

	       // 4
	       cnt++;
	       arrayCampi[cnt] = "GARE.IMPR.NAZIMP";
	       arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
	       arrayTitoloColonne[cnt] = "Nazione (solo se estero)";
	       arrayLarghezzaColonne[cnt] = 11;
	       arrayCampiVisibili[cnt] = true;

	       // 5
	       cnt++;
	       arrayCampi[cnt] = "GARE.IMPR.EMAIIP";
	       arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
	       arrayTitoloColonne[cnt] = "Email";
	       arrayLarghezzaColonne[cnt] = 11;
	       arrayCampiVisibili[cnt] = true;

	       // 6
	       cnt++;
	       arrayCampi[cnt] = "GARE.IMPR.EMAI2IP";
	       arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
	       arrayTitoloColonne[cnt] = "PEC";
	       arrayLarghezzaColonne[cnt] = 11;
	       arrayCampiVisibili[cnt] = true;

	       // 7
	       cnt++;
	       arrayCampi[cnt] = "GARE.IMPR.NOMEST";
	       arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
	       arrayTitoloColonne[cnt] = "Denominazione RT (solo se componente di raggruppamento)";
	       arrayLarghezzaColonne[cnt] = 11;
	       arrayCampiVisibili[cnt] = true;

	       // 8
	       cnt++;
	       arrayCampi[cnt] = "GARE.RAGIMP.IMPMAN";
	       arrayStiliCampi[cnt] = DizionarioStiliExcel.STRINGA_ALIGN_CENTER;
	       arrayTitoloColonne[cnt] = "Ruolo in RT (solo se componente di raggruppamento)";
	       arrayLarghezzaColonne[cnt] = 11;
	       arrayCampiVisibili[cnt] = true;
	     }

	     for (int i = 0; i < arrayCampi.length; i++) {
	       arrayIndiceColonnaCampi[i] = i + 1;
	     }

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

   Map<String,List<?>> mappa = new HashMap<String,List<?>>();
   List<String> listaNomiFisiciCampiDaImportare = new ArrayList<String>();
   List<Long> listaIndiceColonnaCampiDaImportare = new ArrayList<Long>();
   List<Long> listaIndiceArrayValoreCampiDaImportare = new ArrayList<Long>();
   List<String> listaErrori = new ArrayList<String>();
     if (logger.isDebugEnabled())
       logger.debug("getListaCampiDaImportare: inizio lettura della riga 3 del foglio");

     // Numero massimo di celle inizializzate nella riga in lettura
     int maxColNumber = NUMERO_COLONNE_MODELLO;

     //Controlli sulla struttura del file, deve contenere le intestazioni previste da modello,
     //nello stesso ordine del modello
     Row rigaFoglioExcel = foglio.getRow(FOGLIO_LISTA_DITTE__RIGA_INTESTAZIONE - 1);
     Cell cella = null;
     String etichetta = null;
     String msg="";
     boolean formatoValido=true;
     for(int j=0; j < NUMERO_COLONNE_MODELLO; j++) {
       cella = rigaFoglioExcel.getCell(j);
       if(cella== null) {
         formatoValido = false;
         msg="Il numero di colonne non corrisponde a quello previsto dal modello: " + NUMERO_COLONNE_MODELLO  + ".";
         listaErrori.add(msg);
         break;
       }
     }
     if(formatoValido) {
       for(int j=0; j < NUMERO_COLONNE_MODELLO; j++) {
         cella = rigaFoglioExcel.getCell(j);
         etichetta = cella.getRichStringCellValue().toString();
         //Per le colonne con "Identificativo fiscale estero", "Nazione", "Denominazione RT" e "Ruolo in RT" si ignora la parte delle
         //intestazione tra parentesi
         if(etichetta!=null)
           etichetta=etichetta.toUpperCase();
         else
           etichetta="";

         if(!etichetta.startsWith(VETTORE_LABEL_COLONNE[j].toUpperCase())) {
           msg="Non è presente la colonna '" + VETTORE_LABEL_COLONNE[j] + "' in posizione " + (j+1) +".";
           listaErrori.add(msg);
           formatoValido = false;
         }

       }
     }
     if(formatoValido) {
         // Contatore del numero di celle lette dalla riga (cioe' celle valorizzate
         // con una stringa che rappresenta uno dei nomi fisici dei campi da
         // importare (contenuti nella variabile arrayCampi))
         int numeroCelleLette = 0;

         for (int i = 0; i < arrayCampi.length; i++) {
        	 listaNomiFisiciCampiDaImportare.add(arrayCampi[i]);
         }

         for (int i = 0; i < maxColNumber; i++) {
              numeroCelleLette++;
              listaIndiceColonnaCampiDaImportare.add(new Long(i + 1));
              listaIndiceArrayValoreCampiDaImportare.add(new Long(
                   numeroCelleLette - 1));

       }

       if (listaNomiFisiciCampiDaImportare.size() > 0) {
         mappa.put("listaNomiFisiciCampiDaImportare",
             listaNomiFisiciCampiDaImportare);
         mappa.put("listaIndiceColonnaCampiDaImportare",
             listaIndiceColonnaCampiDaImportare);
         mappa.put("listaIndiceArrayValoreCampiDaImportare",
             listaIndiceArrayValoreCampiDaImportare);
       }
     }else {
       mappa.put("listaErrori", listaErrori);
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
   mappa = this.getListaCampiDaImportare(foglioLottiGara, FOGLIO_LISTA_DITTE[indiceFoglio], entita);

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

       listaCampiImportExcel.add(tmpCampo);
     }
   } else {
     listaCampiImportExcel = null;
   }

   return listaCampiImportExcel;

 }

	public void cancellaDati(String ngara, String genereGara, HttpServletRequest request, TransactionStatus status)
			throws SQLException, GestoreException {

		String numeroGara = ngara;

		// codgar
		String codiceTornata = "";
		Vector<?> ngaraEsistente = this.sqlDao.getVectorQuery("select CODGAR1 from GARE where NGARA = ? ",
				new Object[] { ngara });

		codiceTornata = (String) SqlManager.getValueFromVectorParam(ngaraEsistente, 0).getValue();
		String isGaraLottiConOffertaUnica = "false";
		if ("3".equals(genereGara)) {
			isGaraLottiConOffertaUnica = "true";
		}
		request.setAttribute("isGaraLottiConOffertaUnica", isGaraLottiConOffertaUnica);

		try {
			List<?> listaDitteCopiate = this.sqlDao.getVectorQueryForList(
					"select DITTAO FROM DITG WHERE CODGAR5 = ? AND NGARA5= ? ",
					new Object[] { codiceTornata, numeroGara });
			if (listaDitteCopiate != null && listaDitteCopiate.size() > 0) {
				for (int j = 0; j < listaDitteCopiate.size(); j++) {
					String codiceDitta = (String) SqlManager.getValueFromVectorParam(listaDitteCopiate.get(j), 0)
							.getValue();

					// Dopo aver aggiunto la ditta in anagrafica (se non era già presente). La
					// aggiungo alla gara corrente
					DataColumn[] elencoCampi = new DataColumn[3];
					elencoCampi[0] = new DataColumn("DITG.NGARA5", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara));
					elencoCampi[1] = new DataColumn("DITG.CODGAR5",
							new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceTornata));
					elencoCampi[2] = new DataColumn("DITG.DITTAO",
							new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceDitta));

					elencoCampi[0].setChiave(true);
					elencoCampi[1].setChiave(true);
					elencoCampi[2].setChiave(true);

					DataColumnContainer containerDITG = new DataColumnContainer(elencoCampi);

					// Gestione dell'inserimento di una ditta in una gara
					GestoreDITG gestoreDITG = new GestoreDITG();
					gestoreDITG.setRequest(request);

					gestoreDITG.preDelete(status, containerDITG);

					if ("true".equals(isGaraLottiConOffertaUnica)) {
						this.geneManager.deleteTabelle(new String[] { "DITG" }, "CODGAR5 = ? and DITTAO = ?",
								new Object[] { codiceTornata, codiceDitta });
					} else {
						this.geneManager.deleteTabelle(new String[] { "DITG" },
								"CODGAR5 = ? and DITTAO = ? and NGARA5 = ?",
								new Object[] { codiceTornata, codiceDitta, numeroGara });
					}

				}
			}
		} catch (SQLException s) {
			logger.error("Errore nell'inserimento nella tabella", s);
			throw s;
		}

	}


   private boolean controlloValoriRiga(List<CampoImportExcel> listaCampiImportExcel,
       int indiceRiga, List<Object> valoriCampiRigaExcel, List<String> valoriCodiga, List<String> tmpListaMsg, String ngara) throws SQLException, GestoreException {

	   boolean rigaImportabile = true;

       String ragioneSociale = (String) valoriCampiRigaExcel.get(0);
       String codiceFiscale = (String) valoriCampiRigaExcel.get(1);
       String partitaIVA = (String) valoriCampiRigaExcel.get(2);
       String IDEstero = (String) valoriCampiRigaExcel.get(3);
       String nazione = (String) valoriCampiRigaExcel.get(4);
       String email = (String) valoriCampiRigaExcel.get(5);
       String PEC = (String) valoriCampiRigaExcel.get(6);
       String denRT = (String) valoriCampiRigaExcel.get(7);
       String ruoloRT = (String) valoriCampiRigaExcel.get(8);

       //La ragione sociale dell'operatore deve essere valorizzata
       if (ragioneSociale == null || "".equals(ragioneSociale)) {
    	   tmpListaMsg.add("- La ragione sociale non e' valorizzata");
               rigaImportabile = false;
       }

       //Codice fiscale e id fiscale estero non possono essere entrambi valorizzati.
       if ((codiceFiscale != null && !"".equals(codiceFiscale)) && (IDEstero != null && !"".equals(IDEstero) )) {
    	   tmpListaMsg.add("- Codice fiscale e id fiscale estero non possono essere entrambi valorizzati");
               rigaImportabile = false;
       }

       //Almeno uno tra Codice fiscale e id fiscale estero deve essere valorizzato.
       if ((codiceFiscale == null || "".equals(codiceFiscale)) && (IDEstero == null || "".equals(IDEstero) )) {
    	   tmpListaMsg.add("- Codice fiscale e id fiscale estero non possono essere entrambi non valorizzati");
               rigaImportabile = false;
       }

       //Se codice fiscale valorizzato, deve essere valido
       if (codiceFiscale != null && !"".equals(codiceFiscale)) {
    	   if(!UtilityFiscali.isValidCodiceFiscale(codiceFiscale) && !UtilityFiscali.isValidPartitaIVA(codiceFiscale)) {
    		   tmpListaMsg.add("- Il codice fiscale non ha un formato valido");
                   rigaImportabile = false;
    	   }
       }

       //Se id fiscale estero valorizzato, la partita iva non può essere valorizzata
       if ((IDEstero != null && !"".equals(IDEstero)) && (partitaIVA != null && !"".equals(partitaIVA) )) {
    	   tmpListaMsg.add("- Partita IVA e id fiscale estero non possono essere entrambi valorizzati");
               rigaImportabile = false;
       }

       //Se id fiscale estero valorizzato, la nazione deve essere valorizzata e diversa da 'Italia'
       if ((IDEstero != null && !"".equals(IDEstero)) && (nazione == null || "".equals(nazione) || nazione == "Italia")) {
    	   tmpListaMsg.add("- Con id fiscale estero valorizzato, la nazione deve essere valorizzata e diversa da 'Italia'");
           rigaImportabile = false;
       }
       //Se id fiscale estero valorizzato, non può essere più lungo di 16 caratteri
       if (IDEstero != null && !"".equals(IDEstero)) {
    	   if(IDEstero.length()>16) {
        	   tmpListaMsg.add("- L'id fiscale estero non può essere più lungo di 16 caratteri");
               rigaImportabile = false;
    	   }
       }

       //Se partita Iva valorizzata, deve essere valida
       if (partitaIVA != null && !"".equals(partitaIVA) ) {
    	   if(!UtilityFiscali.isValidPartitaIVA(partitaIVA)) {
    		   tmpListaMsg.add("- La partita IVA non ha un formato valido");
                   rigaImportabile = false;
    	   }
       }

       //Se denominazione RT valorizzata, deve essere valorizzato anche il ruolo
       if ((denRT != null && !"".equals(denRT) ) && (ruoloRT == null && !"".equals(ruoloRT) )) {
    	   tmpListaMsg.add("- Se denominazione RT valorizzata, deve essere valorizzato anche il ruolo");
           rigaImportabile = false;
       }

       //Il ruolo, se valorizzato, deve assumere uno dei seguenti valori: MANDANTE, MANDATARIA
       if (ruoloRT != null && !"".equals(ruoloRT) ) {
    	   if ( !"MANDANTE".equals(ruoloRT) && !"MANDATARIA".equals(ruoloRT)) {
    		   tmpListaMsg.add("- Il ruolo RT puo' assumere esclusivamente i valori: MANDANTE, MANDATARIA");
    		   rigaImportabile = false;
    	   }
       }

       //Verifico se la nazione ha un valore valido
       if(nazione!=null && !"".equals(nazione)) {
		    Vector<?> vOccNAZ = this.sqlDao.getVectorQuery(
                    "select tab1tip from tab1 where tab1cod='Ag010' and upper(tab1desc) = ?",
                    new Object[] { nazione.toUpperCase() });

			if (vOccNAZ == null) {
		    	tmpListaMsg.add("- La nazione non è corretta");
	    		   rigaImportabile = false;
		    }
       }

       //Non possono esserci più occ. in IMPR con CFIMP uguale al codice fiscale o id fiscale estero
       if (codiceFiscale != null && !"".equals(codiceFiscale)) {
		    Vector<?> vOccIMPR = this.sqlDao.getVectorQuery(
                    "select count(*) from IMPR where CFIMP = ? ",
                    new Object[] { codiceFiscale });

		    Long occIMPR = (Long) SqlManager.getValueFromVectorParam(vOccIMPR, 0).getValue();
		    if (occIMPR>1) {
		    	tmpListaMsg.add("- Sono presenti più imprese in anagrafica con uguale codice fiscale o id fiscale estero");
	    		   rigaImportabile = false;
		    }
       }
       if (IDEstero != null && !"".equals(IDEstero)) {
		    Vector<?> vOccIMPR = this.sqlDao.getVectorQuery(
                    "select count(*) from IMPR where CFIMP = ? ",
                    new Object[] { IDEstero });

		    Long occIMPR = (Long) SqlManager.getValueFromVectorParam(vOccIMPR, 0).getValue();
		    if (occIMPR>1) {
		    	tmpListaMsg.add("- Sono presenti più imprese in anagrafica con uguale codice fiscale o id fiscale estero");
	    		   rigaImportabile = false;
		    }
       }

	   String cfimp="";
	   if(codiceFiscale != null && !"".equals(codiceFiscale)) {
		   cfimp=codiceFiscale;
	   }
	   if(IDEstero != null && !"".equals(IDEstero)){
		   cfimp=IDEstero;
	   }

       //	se denominazione RT valorizzata e ruolo = 'MANDATARIA', non deve essere già inserita in gara una RT con la stessa mandataria (verificare occ. in DTG con TIPIMP = 3,10 e mandataria con uguale codice fiscale)
       if ((denRT != null && !"".equals(denRT) ) && "MANDATARIA".equals(ruoloRT)) {
    	   if(!"".equals(cfimp)) {
    		   Vector<?> vOccMAND = this.sqlDao.getVectorQuery(
    				   "select count(*) from ditg d, impr i, ragimp r where d.dittao=i.codimp and d.dittao=r.codime9 and d.ngara5 = ? and "
    				   + "(i.tipimp=3 or i.tipimp=10) and i.nomest= ? and r.impman='1' and exists (select * from impr i2 where i2.codimp=r.coddic and cfimp=?) ",
    				   new Object[] { ngara, denRT, cfimp});

		      Long occIMPR = (Long) SqlManager.getValueFromVectorParam(vOccMAND, 0).getValue();
		      if (occIMPR>0) {
		    	  tmpListaMsg.add("- RT con mandataria uguale a precedente riga nel file");
	    		     rigaImportabile = false;
		      }
    	   }

       }

       //	se denominazione RT valorizzata e ruolo = 'MANDANTE', deve essere già inserita in gara una RT con la stessa denominazione e con componenti con codice fiscale diverso
       if ((denRT != null  && !"".equals(denRT) ) && "MANDANTE".equals(ruoloRT)) {
    	   if(!"".equals(cfimp)) {
		      Vector<?> vOccMAND = this.sqlDao.getVectorQuery(
                    "select count(*) from ditg d, impr i, ragimp r where d.dittao=i.codimp and d.dittao=r.codime9 and d.ngara5 = ? and "
                    + "(i.tipimp=3 or i.tipimp=10) and i.nomest= ? and not exists (select * from impr i2 where i2.codimp=r.coddic and i2.cfimp = ?)",
                    new Object[] { ngara, denRT, codiceFiscale});

		      Long occIMPR = (Long) SqlManager.getValueFromVectorParam(vOccMAND, 0).getValue();
		      if (occIMPR==0) {
		    	  tmpListaMsg.add("- Viene fatto riferimento a una RT per cui manca la riga con la definizione della mandataria oppure per l'RT sono definite più mandanti con uguale codice fiscale o id fiscale estero");
	    		     rigaImportabile = false;
		      }
    	   }

       }

       //	se denominazione RT non valorizzata, non deve essere valorizzato il ruolo e non deve essere già inserita in gara una ditta con uguale codice fiscale
       if(denRT==null  && !"".equals(denRT) ) {
    	 if(ruoloRT!=null && !"".equals(ruoloRT)) {
    	   tmpListaMsg.add("- Se Ruolo RT valorizzato, deve essere valorizzata anche la denominazione");
           rigaImportabile = false;
    	 }
         if(!"".equals(cfimp)) {
		      Vector<?> vOccNoRT = this.sqlDao.getVectorQuery(
                    "select count(*) from ditg d ,impr i where d.dittao=i.codimp and d.ngara5 = ? and i.cfimp = ?",
                    new Object[] { ngara, cfimp});

		      Long occNoRT = (Long) SqlManager.getValueFromVectorParam(vOccNoRT, 0).getValue();
		      if (occNoRT>0) {
		    	  tmpListaMsg.add("- Codice fiscale uguale a precedente riga nel file");
	    		     rigaImportabile = false;
		      }
    	   }
       }

       // se inserisco un MANDATARIO devo verificare non ne sia già presente un'altro con la stessa denominazioneRT collegato alla gara
       if (denRT != null && !"".equals(denRT) && "MANDATARIA".equals(ruoloRT)) {
		     Vector<?> vOccMAND = this.sqlDao.getVectorQuery(
                   "select count(*) from ditg d, impr i where d.dittao=i.codimp and d.ngara5 = ? and "
                   + "(i.tipimp=3 or i.tipimp=10) and i.nomest= ? ",
                   new Object[] { ngara, denRT});

		     Long occIMPR = (Long) SqlManager.getValueFromVectorParam(vOccMAND, 0).getValue();
		     if (occIMPR>0) {
		   	    tmpListaMsg.add("- Denominazione RT uguale a precedente riga nel file");
	    	     rigaImportabile = false;
		     }
       }

     return rigaImportabile;
   }

   private List<Object> letturaRiga(Row rigaFoglioExcel, List<?> listaCampiDaImportare) {
     List<Object> valoriCampi = new ArrayList<Object>();
     Cell cella = null;
     FormulaEvaluator evaluator =  this.workBook.getCreationHelper().createFormulaEvaluator();
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
           else {
             //valoriCampi.add(new Double(cella.getNumericCellValue()));
             valoriCampi.add(new BigDecimal(cella.toString()).toPlainString());
           }
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

   public String insertDittaAnagrafica(Long tipimp, String nomest, String nomimp, String cfimp, String pivimp, String emaiip, String pec, Long nazimp) throws GestoreException, SQLException{

	    String codimp;
	    Long nazimpL;
	    String insertQuery = "INSERT INTO IMPR (CODIMP,TIPIMP,NOMEST,NOMIMP,CFIMP,PIVIMP,EMAIIP,EMAI2IP,NAZIMP) VALUES (?,?,?,?,?,?,?,?,?)";
	    codimp = geneManager.calcolaCodificaAutomatica("IMPR","CODIMP");
	    sqlDao.update(insertQuery, new Object[]{codimp,tipimp,nomest,nomimp,cfimp,pivimp,emaiip,pec,nazimp});

	    return codimp;

	  }

   public String insertDittaAnagraficaRT(Long tipimp, String nomest, String nomimp) throws GestoreException, SQLException{

	    String codimp;
	    String insertQuery = "INSERT INTO IMPR (CODIMP,TIPIMP,NOMEST,NOMIMP) VALUES (?,?,?,?)";
	    codimp = geneManager.calcolaCodificaAutomatica("IMPR","CODIMP");
	    sqlDao.update(insertQuery, new Object[]{codimp,tipimp,nomest,nomimp});

	    return codimp;

	  }

   public void insertDittaRagimp(String codime9, String coddic, String nomdic, Long impman) throws GestoreException, SQLException{

	    String insertQuery = "INSERT INTO RAGIMP (CODIME9,CODDIC,NOMDIC,IMPMAN) values(?,?,?,?)";
	    sqlDao.update(insertQuery, new Object[]{codime9,coddic,nomdic,impman});

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

   public String isOperatoreRegistrato(String codiceFiscale, String piva) throws SQLException{

	    String select="select impr.codimp from impr, w_puser where impr.codimp = w_puser.userkey1 and impr.cfimp = ?";
	    String codiceDitta;
	    codiceDitta = (String)sqlManager.getObject(select, new Object[]{codiceFiscale});
	    if(codiceDitta == null){

	      if((codiceFiscale != null && !"".equals(codiceFiscale)) && (piva != null && !"".equals(piva))){
	        select = "select CODIMP from impr where cfimp = ? and pivimp = ? order by codimp desc";
	        codiceDitta = (String)sqlManager.getObject(select, new Object[]{codiceFiscale,piva});
	      }
	      if(codiceDitta == null){
	        if(piva != null && !"".equals(piva)){
	          select = "select CODIMP from impr where pivimp = ? order by codimp desc";
	          codiceDitta = (String)sqlManager.getObject(select, new Object[]{piva});
	        }
	        if(codiceDitta == null){
	          if(codiceFiscale != null && !"".equals(codiceFiscale)){
	            select = "select CODIMP from impr where cfimp = ? order by codimp desc";
	            codiceDitta = (String)sqlManager.getObject(select, new Object[]{codiceFiscale});
	          }
	          if(codiceDitta == null){
	            if(piva != null && !"".equals(piva)){
	              select = "select CODIMP from impr where cfimp = ? order by codimp desc";
	              codiceDitta = (String)sqlManager.getObject(select, new Object[]{piva});
	            }
	            if(codiceDitta == null){
	              if(codiceFiscale != null && !"".equals(codiceFiscale)){
	                select = "select CODIMP from impr where pivimp = ? order by codimp desc";
	                codiceDitta = (String)sqlManager.getObject(select, new Object[]{codiceFiscale});
	              }
	            }
	          }
	        }
	      }
	    }
	    return codiceDitta;
	  }

}
