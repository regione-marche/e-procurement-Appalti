/*
 * Created on 22/nov/2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.TempFileUtilities;
import it.eldasoft.gene.db.dao.SqlDao;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.excel.DizionarioStiliExcelX;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.utility.UtilityExcelX;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderFormatting;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Classe per la gestione delle funzionalita' di export degli operatori iscritti su foglio Excel
 *
 * @author Manuel.Bridda
 */


public class ExportOperatoriEconomiciDGUEManager {

  static Logger                     logger = Logger.getLogger(ExportOperatoriEconomiciDGUEManager.class);

  private SqlDao                    sqlDao;

  public void setSqlDao(SqlDao sqlDao) {
    this.sqlDao = sqlDao;
  }

  private final String       SCHEMA_CAMPI                                                 = "GARE";
  
  /** Manager dei tabellati */
  private TabellatiManager          tabellatiManager;
  
  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * Nome del file associato al file Excel contenente l'export degli operatori iscritti
   */

  private final String       FOGLIO_LISTA_OPERATORI[]                             = { "Lista operatori economici"};

  //private final int          FOGLIO_LISTA_OPERATORI_RIGA_NOME_FISICO_CAMPI        = 3;

  private final int          FOGLIO_LISTA_OPERATORI__RIGA_INIZIALE                = 2;

  /**
   * Nome del file associato al file Excel contenente l'export degli operatori iscritti
   */

  private final String       NOME_FILE_C0OGGASS_EXPORT                                    = "DgueOperatoriEconomici.xlsx";

  private XSSFWorkbook       workBook                                                     = null;

  private String[]           arrayCampi;
  private int[]              arrayStiliCampi;
  private String[]           arrayTitoloColonne;
  private int[]              arrayLarghezzaColonne;
  private boolean[]          arrayCampiVisibili;
  private int[]              arrayIndiceColonnaCampi;


  /**
   * Export Xlsx degli operatori economici che hanno presentato della documentazione XML creata con DGUE. 
   *
   * @param ngara
   *        numero della gara
   *
   * @param categoria
   *        contiene la categoria per la viene fatto l'export
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
  public String exportOperatoriEconomiciDGUE(String codgar, String codimp, Long fase, boolean prettyPrint, HttpSession session)
  throws FileNotFoundException, SQLException, GestoreException, IOException,
      Exception {

    this.setDefinizioni(codimp.length()==0,prettyPrint);

    this.workBook = new XSSFWorkbook();
    // Creazione del dizionario degli stili delle celle dell'intero file Excel
    DizionarioStiliExcelX dizStiliExcel = new DizionarioStiliExcelX(this.workBook);

    // Scrittura sul foglio Excel degli operatori iscritti
    this.setOperatoriEconomici(codgar, codimp, fase, prettyPrint, dizStiliExcel);

    // Set del foglio 'Lista operatori iscritti' come foglio attivo

      this.workBook.setActiveSheet(this.workBook.getSheetIndex(FOGLIO_LISTA_OPERATORI[0]));
      this.workBook.setSelectedTab(this.workBook.getSheetIndex(FOGLIO_LISTA_OPERATORI[0]));

    // Creazione di un file temporaneo nella cartella temporanea e salvataggio
    // del suo nome in sessione nell'oggetto TempfileDeleter
      if(codimp.length()>0)
      codimp = "_"+codimp;
    String nomeFile = codgar.toUpperCase().replace("/", "_").replace("$", "");
    nomeFile += "_" + FilenameUtils.getBaseName(NOME_FILE_C0OGGASS_EXPORT) + codimp + "." +
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

   /**
    * Scrittura su foglio Excel dei dati deglio Operatori Iscritti
    *
    * @param ngara
    * @throws GestoreException
    * @throws SQLException
    */
   private void setOperatoriEconomici(String codgar, String codimp, Long fase, boolean prettyPrint, DizionarioStiliExcelX dizStiliExcel)
         throws GestoreException, SQLException {

     Sheet foglioListaOperatori = workBook.createSheet(FOGLIO_LISTA_OPERATORI[0]);
                                           //0                     1                     2                         3
     String selectCampiOperatoriIscritti = "select DITG.NUMORDPL,  V_DGUEELABSUB.CODIMP,   V_DGUEELABSUB.DIGNOMDOC,    V_DGUEELABSUB.ESCLUSIONE, "
         //4                        5                     6                   7                       8                          9
       + "V_DGUEELABSUB.NOMEOE,     V_DGUEELABSUB.RUOLO,  V_DGUEELABSUB.CF,   V_DGUEELABSUB.PIVA,     V_DGUEELABSUB.ISGRUPPO,    V_DGUEELABSUB.NOMEGRUPPO, "
         //10                         11                             12                        13                    14
       + "V_DGUEELABSUB.COMPONENTI,  V_DGUEELABSUB.ISCONSORZIO,    V_DGUEELABSUB.CONSORZIATE, V_DGUEELABSUB.SUB,  V_DGUEELABSUB.AUX"
       + " from DITG left join V_DGUEELABSUB on V_DGUEELABSUB.CODIMP=DITG.DITTAO and DITG.NGARA5=V_DGUEELABSUB.NGARA"
       + " where CODGAR=? AND BUSTA=? ";
   
     //se voglio il cartesiano di tutte le ditte terze:
     if(!prettyPrint) {              //0                    1                       2                           3
     selectCampiOperatoriIscritti = "select DITG.NUMORDPL,  V_DGUEELABSUB.CODIMP,   V_DGUEELABSUB.DIGNOMDOC,    V_DGUEELABSUB.ESCLUSIONE, "
           //4                      5                      6                   7                       8                          9
         + "V_DGUEELABSUB.NOMEOE,   V_DGUEELABSUB.RUOLO,   V_DGUEELABSUB.CF,   V_DGUEELABSUB.PIVA,     V_DGUEELABSUB.ISGRUPPO,    V_DGUEELABSUB.NOMEGRUPPO, "
           //10                         11                            12                        
         + "V_DGUEELABSUB.COMPONENTI,  V_DGUEELABSUB.ISCONSORZIO,    V_DGUEELABSUB.CONSORZIATE, "
           //13 (+2)            14 (+2)     15 (+2)         16 (+2)         17 (+2)     18 (+2)             19 (+2)     20 (+2)
         + "d1.DENOMINAZIONE,   d1.CF,      d1.ATTIVITA,    d1.PRESTAZIONE, d1.QUOTA,   d2.DENOMINAZIONE,   d2.CF,      d2.ATTIVITA"
         + " from DITG left join V_DGUEELABSUB on V_DGUEELABSUB.CODIMP=DITG.DITTAO and DITG.NGARA5=V_DGUEELABSUB.NGARA LEFT JOIN DGUE_ELABSUB d1 on (d1.idelaborazione=V_DGUEELABSUB.idelaborazione and d1.RUOLO=1) LEFT JOIN DGUE_ELABSUB d2 on (d2.idelaborazione=V_DGUEELABSUB.idelaborazione and d2.RUOLO=2) "
         + " where CODGAR=? AND BUSTA=? ";
     }
     List<Object> parameters = new ArrayList<Object>();
     parameters.add(codgar);
     parameters.add(fase);
     
     if(codimp.length()>0) {
       selectCampiOperatoriIscritti += " AND DITG.DITTAO = ?";
       parameters.add(codimp);
     }
     selectCampiOperatoriIscritti += " ORDER BY DITG.NUMORDPL,V_DGUEELABSUB.TAB5NORD,V_DGUEELABSUB.NOMEOE ";

    try {
      
       int indiceColonna = 1;
       //DizionarioCampi dizCampi = DizionarioCampi.getInstance();
       //Campo campo = null;
       //Row riga = null;
       
       List<CellStyle> stili = new ArrayList<CellStyle>();
       stili.add(dizStiliExcel.getStileExcel(1));//fittizio

       for (int ii = 0; ii < arrayCampi.length; ii++) {
         if (arrayCampiVisibili[ii]) {
             foglioListaOperatori.setDefaultColumnStyle(
               (short) (arrayIndiceColonnaCampi[ii] - 1),
               dizStiliExcel.getStileExcel(arrayStiliCampi[ii]));
         stili.add(dizStiliExcel.getStileExcel(arrayStiliCampi[ii]));
         }
       }


       foglioListaOperatori.setPrintGridlines(true);
       // (0.77 inc <=> 2 cm)
       foglioListaOperatori.setMargin(Sheet.TopMargin, 0.77);
       foglioListaOperatori.setMargin(Sheet.BottomMargin, 0.77);
       foglioListaOperatori.setMargin(Sheet.LeftMargin, 0.77);
       foglioListaOperatori.setMargin(Sheet.RightMargin, 0.77);

       // Set delle informazioni per l'area di stampa dei dati del foglio

       PrintSetup printSetup = foglioListaOperatori.getPrintSetup();
       printSetup.setLandscape(true);
       printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);

       printSetup.setScale((short) 70); // Presumo significhi zoom al 70%
       // Scrittura dell'intestazione della foglio
       for (int indiceRigaIntestazione = 0; indiceRigaIntestazione < FOGLIO_LISTA_OPERATORI__RIGA_INIZIALE; indiceRigaIntestazione++) {

         //riga = foglioListaOperatori.createRow(indiceRigaIntestazione);
         CellStyle stile = null;
         for (int ii = 0; ii < arrayCampi.length; ii++) {
           if (arrayCampiVisibili[ii]) {
             switch (indiceRigaIntestazione) {
             case 0:
               if (stile == null){
                 stile = dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_INTESTAZIONE);
               }
               UtilityExcelX.scriviCella(foglioListaOperatori,
                   arrayIndiceColonnaCampi[ii], indiceRigaIntestazione + 1,
                   arrayTitoloColonne[ii], stile);
               UtilityExcelX.setLarghezzaColonna(foglioListaOperatori,
                   arrayIndiceColonnaCampi[ii], arrayLarghezzaColonne[ii]);
               break;

//             case FOGLIO_LISTA_OPERATORI_RIGA_NOME_FISICO_CAMPI - 1:
//               // Seconda riga: nomi fisici dei campi
//               if (stile == null)
//                 stile = dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_NASCOSTA);
//               campo = dizCampi.getCampoByNomeFisico(arrayCampi[ii].substring(arrayCampi[ii].indexOf(".") + 1));
//               UtilityExcelX.scriviCella(foglioListaOperatori,
//                   arrayIndiceColonnaCampi[ii], indiceRigaIntestazione + 1,
//                   SCHEMA_CAMPI.concat(".").concat(campo.getNomeFisicoCampo()),
//                   stile);
//               break;
//             case FOGLIO_LISTA_OPERATORI__RIGA_INIZIALE - 2:
//               // Riga separatrice intestazione dai dati
//               if (stile == null){
//                 stile = dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_SEPARATRICE);
//               }
//               UtilityExcelX.scriviCella(foglioListaOperatori, arrayIndiceColonnaCampi[ii], indiceRigaIntestazione + 1, " ",
//                   stile);
//               break;
             }//switch
           }//if
         }  //for interno

//           if (indiceRigaIntestazione == 0) {
//             // Set titolo della colonna importo
//             //UtilityExcel.scriviCella(foglioOperatoriIscritti, indiceColonnaImporto, indiceRigaIntestazione + 1, "Importo",stile);
//           } else if (indiceRigaIntestazione > 0
//               && indiceRigaIntestazione < FOGLIO_LISTA_OPERATORI__RIGA_INIZIALE - 2) {
//             // Nascondo la riga del foglio
//             riga.setZeroHeight(true);
//           } else if (indiceRigaIntestazione == FOGLIO_LISTA_OPERATORI__RIGA_INIZIALE - 2) {
//             //UtilityExcel.scriviCella(foglioOperatoriIscritti,    indiceColonnaImporto, indiceRigaIntestazione + 1, " ", stile);
//             // Set altezza della riga che divide l'intestazione dai dati
//             riga.setHeightInPoints(3);
//           }

       }//for esterno


         int indiceRiga = FOGLIO_LISTA_OPERATORI__RIGA_INIZIALE;

           List<?> listaCampiOperatoriIscritti = this.sqlDao.getVectorQueryForList(
                   selectCampiOperatoriIscritti, parameters.toArray());

           if (listaCampiOperatoriIscritti != null && listaCampiOperatoriIscritti.size() > 0) {
             // Reset dell'indice di colonna
             indiceColonna = 1;
             for (int i = 0; i < listaCampiOperatoriIscritti.size(); i++) {
               Vector<?> record = (Vector<?>) listaCampiOperatoriIscritti.get(i);
               if (record != null && record.size() > 0) {
                   
                 //Campo 0: NUMORDPL
                 if (arrayCampiVisibili[0]) {
                   Long numeroPlico = SqlManager.getValueFromVectorParam( record, 0).longValue();
                   if (numeroPlico != null)
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, numeroPlico,  stili.get(indiceColonna));
                   indiceColonna++;
                  }
                 
                 //Campo 1: CODIMP 
                 if (arrayCampiVisibili[1]) {
                   codimp = SqlManager.getValueFromVectorParam(record, 1).getStringValue();
                   if (codimp != null)
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, codimp,  stili.get(indiceColonna));
                  indiceColonna++;
                  }
                 
                 //Campo 2: DIGNOMDOC 
                 if (arrayCampiVisibili[2]) {
                   String dignomdoc = SqlManager.getValueFromVectorParam(record, 2).getStringValue();
                   if (dignomdoc != null)
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, dignomdoc,  stili.get(indiceColonna));
                  indiceColonna++;
                  }
                 
                 //Campo 3: ESCLUSIONE 
                 if (arrayCampiVisibili[3]) {
                   String eslusione = SqlManager.getValueFromVectorParam(record, 3).getStringValue();
                   if (StringUtils.stripToNull(eslusione) != null) {
                     if("1".equals(eslusione)) 
                       eslusione="Si";
                     else
                       eslusione="No";
                     UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, eslusione,  stili.get(indiceColonna));
                   }
                  indiceColonna++;
                  }
                 
                 //Campo 4: NOMEOE 
                 if (arrayCampiVisibili[4]) {
                   String nomeoe = SqlManager.getValueFromVectorParam(record, 4).getStringValue();
                   if (nomeoe != null)
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, nomeoe,  stili.get(indiceColonna));
                  indiceColonna++;
                  }
                 
                 //Campo 6: CF 
                 if (arrayCampiVisibili[6]) {
                   String cf = SqlManager.getValueFromVectorParam(record, 6).getStringValue();
                   if (cf != null)
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, cf,  stili.get(indiceColonna));
                  indiceColonna++;
                  }
                 
                 //Campo 5: PIVA 
                 if (arrayCampiVisibili[5]) {
                   String piva = SqlManager.getValueFromVectorParam(record, 7).getStringValue();
                   if (piva != null)
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, piva,  stili.get(indiceColonna));
                  indiceColonna++;
                  }
                 
                 //Campo 7: RUOLO 
                 if (arrayCampiVisibili[7]) {
                   String ruolo = SqlManager.getValueFromVectorParam(record, 5).getStringValue();
                   if (ruolo != null)
                     ruolo = this.tabellatiManager.getDescrTabellato("G1j01", ruolo);
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, ruolo,  stili.get(indiceColonna));
                  indiceColonna++;
                  }
                 
                 //Campo 8: ISGRUPPO 
                 if (arrayCampiVisibili[8]) {
                   String isgruppo = SqlManager.getValueFromVectorParam(record, 8).getStringValue();
                   if (StringUtils.stripToNull(isgruppo) != null) {
                     if("1".equals(isgruppo)) 
                       isgruppo="Si";
                     else
                       isgruppo="No";
                     UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, isgruppo,  stili.get(indiceColonna));
                   }
                   indiceColonna++;
                  }
                 
                 //Campo 9: NOMEGRUPPO 
                 if (arrayCampiVisibili[9]) {
                   String nomegruppo = SqlManager.getValueFromVectorParam(record, 9).getStringValue();
                   if (nomegruppo != null)
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, nomegruppo,  stili.get(indiceColonna));
                  indiceColonna++;
                  }
                 
                 //Campo 10: COMPONENTI
                 if (arrayCampiVisibili[10]) {
                   String componenti = SqlManager.getValueFromVectorParam(record, 10).getStringValue();
                   if (componenti != null)
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, componenti,  stili.get(indiceColonna));
                  indiceColonna++;
                  }
                 
                 //Campo 11: ISCONSORZIO
                 if (arrayCampiVisibili[11]) {
                   String isconsorzio = SqlManager.getValueFromVectorParam(record, 11).getStringValue();
                   if (StringUtils.stripToNull(isconsorzio) != null) {
                     if("1".equals(isconsorzio)) 
                       isconsorzio="Si";
                     else
                       isconsorzio="No";
                     UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, isconsorzio,  stili.get(indiceColonna));
                   }
                   indiceColonna++;
                  }
                 
                 //Campo 12: CONSORZIATE
                 if (arrayCampiVisibili[12]) {
                   String consorziate = SqlManager.getValueFromVectorParam(record, 12).getStringValue();
                   if (consorziate != null)
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, consorziate,  stili.get(indiceColonna));
                  indiceColonna++;
                  }
                 
                 //Campo 13: DITTE IN SUBAPPALTO (raggruppate)
                 if (arrayCampiVisibili[13]) {
                   String sub = SqlManager.getValueFromVectorParam(record, 13).getStringValue();
                   if (sub != null)
                     sub=sub.replace("#!#","\n");
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, sub,  stili.get(indiceColonna));
                  indiceColonna++;
                  }
                 
                 //Campo 14: DITTE IN SUBAPPALTO (raggruppate)
                 if (arrayCampiVisibili[14]) {
                   String aux = SqlManager.getValueFromVectorParam(record, 14).getStringValue();
                   if (aux != null)
                     aux=aux.replace("#!#","\n");
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, aux,  stili.get(indiceColonna));
                  indiceColonna++;
                  }
                 
                 //torno a un indice più basso perché questi dati provengono da una query differente!
                 //Campo 13+2: DENOMINAZIONE SUB
                 if (arrayCampiVisibili[15]) {
                   String denominazione = SqlManager.getValueFromVectorParam(record, 13).getStringValue();
                   if (denominazione != null)
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, denominazione,  stili.get(indiceColonna));
                  indiceColonna++;
                  }
                 
                 //Campo 14+2: CF SUB
                 if (arrayCampiVisibili[16]) {
                   String cf = SqlManager.getValueFromVectorParam(record, 14).getStringValue();
                   if (cf != null)
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, cf,  stili.get(indiceColonna));
                  indiceColonna++;
                  }
                 
                 //Campo 15+2: ATTIVITA SUB
                 if (arrayCampiVisibili[17]) {
                   String attivita = SqlManager.getValueFromVectorParam(record, 15).getStringValue();
                   if (attivita != null)
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, attivita,  stili.get(indiceColonna));
                  indiceColonna++;
                  }
                 
                 //Campo 16+2: PRESTAZIONE SUB
                 if (arrayCampiVisibili[18]) {
                   String prestazione = SqlManager.getValueFromVectorParam(record, 16).getStringValue();
                   if (prestazione != null)
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, prestazione,  stili.get(indiceColonna));
                  indiceColonna++;
                  }
                 
                 //Campo 17+2: QUOTA SUB
                 if (arrayCampiVisibili[19]) {
                   Double quota = (Double) SqlManager.getValueFromVectorParam(record, 17).getValue();
                   if (quota != null)
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, quota,  stili.get(indiceColonna));
                  indiceColonna++;
                  }
                 
                 //Campo 18+2: DENOMINAZIONE SUB
                 if (arrayCampiVisibili[20]) {
                   String denominazione = SqlManager.getValueFromVectorParam(record, 18).getStringValue();
                   if (denominazione != null)
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, denominazione,  stili.get(indiceColonna));
                  indiceColonna++;
                  }
                 
                 //Campo 19+2: CF SUB
                 if (arrayCampiVisibili[21]) {
                   String cf = SqlManager.getValueFromVectorParam(record, 19).getStringValue();
                   if (cf != null)
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, cf,  stili.get(indiceColonna));
                  indiceColonna++;
                  }
                 
                 //Campo 20+2: ATTIVITA SUB
                 if (arrayCampiVisibili[22]) {
                   String attivita = SqlManager.getValueFromVectorParam(record, 20).getStringValue();
                   if (attivita != null)
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna, indiceRiga + i, attivita,  stili.get(indiceColonna));
                  indiceColonna++;
                  }
                 
                 // Reset dell'indice di colonna
                 indiceColonna = 1;
               }//if record
             }//for i
             indiceRiga += listaCampiOperatoriIscritti.size();
           }//if
           
           Sheet sheet = this.workBook.getSheet(FOGLIO_LISTA_OPERATORI[0]);
           int totalColumns = sheet.getRow(0).getPhysicalNumberOfCells();

           //regole di formattazione condizionale
           SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();
           int totRows=sheet.getLastRowNum();
           String lastColumn = CellReference.convertNumToColString(totalColumns-1);

           
           ConditionalFormattingRule[] cfRules = null;
           CellRangeAddress[] regions = null;
           //se la visualizzazione è per più plichi (più aziende che hanno presentato l'offerta)
           if(codimp.length()==0) {
             ConditionalFormattingRule rule = sheetCF.createConditionalFormattingRule(
                 "$A"+FOGLIO_LISTA_OPERATORI__RIGA_INIZIALE+"<>$A"+(FOGLIO_LISTA_OPERATORI__RIGA_INIZIALE+1));
             BorderFormatting border = rule.createBorderFormatting();
             border.setBorderBottom(CellStyle.BORDER_THIN);
             border.setBottomBorderColor(IndexedColors.DARK_BLUE.index);
             
             cfRules = new ConditionalFormattingRule[]{rule};
             regions = new CellRangeAddress[]{CellRangeAddress.valueOf(
                 "A"+FOGLIO_LISTA_OPERATORI__RIGA_INIZIALE+":"+lastColumn+totRows)};
             sheetCF.addConditionalFormatting(regions, cfRules);
             }
           
          String indexEsclusione = "";
          for(int i=0; i<sheet.getRow(0).getLastCellNum();i++) {
            String toCompare = sheet.getRow(0).getCell(i).getStringCellValue();
            if("Criteri di esclusione?".equals(toCompare)) {
              indexEsclusione=CellReference.convertNumToColString(i);
            }
          }
           //Colonna CRITERI DI ESCLUSIONE 
           ConditionalFormattingRule rule = sheetCF.createConditionalFormattingRule(
               "$"+indexEsclusione+FOGLIO_LISTA_OPERATORI__RIGA_INIZIALE+"=\"Si\"");
           PatternFormatting fill = rule.createPatternFormatting();
           fill.setFillBackgroundColor(IndexedColors.YELLOW.index);
           fill.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
           
           cfRules = new ConditionalFormattingRule[]{rule};
           regions = new CellRangeAddress[]{CellRangeAddress.valueOf(
               indexEsclusione+FOGLIO_LISTA_OPERATORI__RIGA_INIZIALE+":"+indexEsclusione+totRows+1)};
           sheetCF.addConditionalFormatting(regions, cfRules);


    } catch (SQLException s) {
      logger.error("Export operatori economici'" + codgar
          + "': errore durante l'export su Excel dello sheet 'Lista operatori economici'");
      throw s;
    }


   }//end setOperatoriIscritti

/**
 * Set degli array di definizione 
 *
 * 
 * 
 */
private void setDefinizioni(boolean isGlobale, boolean prettyPrint) throws SQLException, GestoreException {

   int numeroTotaleCampi = 23;
   int cnt = 0;
    
   arrayCampi = new String[numeroTotaleCampi];
   arrayStiliCampi = new int[numeroTotaleCampi];
   arrayTitoloColonne = new String[numeroTotaleCampi];
   arrayLarghezzaColonne = new int[numeroTotaleCampi];
   arrayCampiVisibili = new boolean[numeroTotaleCampi];
   arrayIndiceColonnaCampi = new int[numeroTotaleCampi];
    
   //0
   arrayCampi[cnt] = "GARE.DITG.NUMORDPL";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.INTERO_ALIGN_RIGHT;
   arrayTitoloColonne[cnt] = "Numero plico";
   arrayLarghezzaColonne[cnt] = 10;
   arrayCampiVisibili[cnt] = true;
    
   // 1
   cnt++;
   arrayCampi[cnt] = "GARE.V_DGUEELABSUB.CODIMP";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
   arrayTitoloColonne[cnt] = "Codice impresa";
   arrayLarghezzaColonne[cnt] = 20;
   arrayCampiVisibili[cnt] = false;
    
   //2
   cnt++;
   arrayCampi[cnt] = "GARE.V_DGUEELABSUB.DIGNOMDOC";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
   arrayTitoloColonne[cnt] = "File XML";
   arrayLarghezzaColonne[cnt] = 40;
   arrayCampiVisibili[cnt] = true;
    
   //3
   cnt++;
   arrayCampi[cnt] = "GARE.V_DGUEELABSUB.ESCLUSIONE";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_CENTER;
   arrayTitoloColonne[cnt] = "Criteri di esclusione?";
   arrayLarghezzaColonne[cnt] = 10;
   arrayCampiVisibili[cnt] = true;
    
   //4
   cnt++;
   arrayCampi[cnt] = "GARE.V_DGUEELABSUB.NOMEOE";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
   arrayTitoloColonne[cnt] = "Denominazione";
   arrayLarghezzaColonne[cnt] = 40;
   arrayCampiVisibili[cnt] = true;
   
   //5
   cnt++;
   arrayCampi[cnt] = "GARE.V_DGUEELABSUB.CF";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
   arrayTitoloColonne[cnt] = "Codice fiscale";
   arrayLarghezzaColonne[cnt] = 20;
   arrayCampiVisibili[cnt] = true;
   
   //6
   cnt++;
   arrayCampi[cnt] = "GARE.V_DGUEELABSUB.PIVA";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
   arrayTitoloColonne[cnt] = "Partita IVA / Id fiscale estero";
   arrayLarghezzaColonne[cnt] = 20;
   arrayCampiVisibili[cnt] = true;
   
   //7
   cnt++;
   arrayCampi[cnt] = "GARE.V_DGUEELABSUB.RUOLO";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
   arrayTitoloColonne[cnt] = "Ruolo";
   arrayLarghezzaColonne[cnt] = 20;
   arrayCampiVisibili[cnt] = true;
      
   //8
   cnt++;
   arrayCampi[cnt] = "GARE.V_DGUEELABSUB.ISGRUPPO";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_CENTER;
   arrayTitoloColonne[cnt] = "Gruppo?";
   arrayLarghezzaColonne[cnt] = 10;
   arrayCampiVisibili[cnt] = true;
   
   //9
   cnt++;
   arrayCampi[cnt] = "GARE.V_DGUEELABSUB.NOMEGRUPPO";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
   arrayTitoloColonne[cnt] = "Nome gruppo";
   arrayLarghezzaColonne[cnt] = 40;
   arrayCampiVisibili[cnt] = true;
  
   //10
   cnt++;
   arrayCampi[cnt] = "GARE.V_DGUEELABSUB.COMPONENTI";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
   arrayTitoloColonne[cnt] = "Componenti gruppo";
   arrayLarghezzaColonne[cnt] = 40;
   arrayCampiVisibili[cnt] = true;
   
   //11
   cnt++;
   arrayCampi[cnt] = "GARE.V_DGUEELABSUB.ISCONSORZIO";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_CENTER;
   arrayTitoloColonne[cnt] = "Consorzio?";
   arrayLarghezzaColonne[cnt] = 10;
   arrayCampiVisibili[cnt] = true;
   
   //12
   cnt++;
   arrayCampi[cnt] = "GARE.V_DGUEELABSUB.CONSORZIATE";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
   arrayTitoloColonne[cnt] = "Consorziate esecutrici";
   arrayLarghezzaColonne[cnt] = 40;
   arrayCampiVisibili[cnt] = true;
  
   //13 gestione con raggruppamenti
   cnt++;
   arrayCampi[cnt] = "GARE.V_DGUEELABSUB.SUB";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
   arrayTitoloColonne[cnt] = "Subappaltatore, identificativo fiscale, Attivita, Prestazione, Quota (0 - N)";
   arrayLarghezzaColonne[cnt] = 50;
   arrayCampiVisibili[cnt] = prettyPrint;
   
   //14
   cnt++;
   arrayCampi[cnt] = "GARE.V_DGUEELABSUB.AUX";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
   arrayTitoloColonne[cnt] = "Ausiliaria, Identificativo fiscale, Attività (0 - N)";
   arrayLarghezzaColonne[cnt] = 50;
   arrayCampiVisibili[cnt] = prettyPrint;  
   //fine gestione con raggruppamenti
   
   //15 gestione disaccoppiata ditte con ruolo = 1 
   cnt++;
   arrayCampi[cnt] = "GARE.DGUE_ELABSUB.DENOMINAZIONE";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
   arrayTitoloColonne[cnt] = "Denominazione ditta subappaltatrice";
   arrayLarghezzaColonne[cnt] = 40;
   arrayCampiVisibili[cnt] = !prettyPrint;
   
   //16
   cnt++;
   arrayCampi[cnt] = "GARE.DGUE_ELABSUB.CF";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
   arrayTitoloColonne[cnt] = "CF o P.IVA / Id fiscale estero";
   arrayLarghezzaColonne[cnt] = 20;
   arrayCampiVisibili[cnt] = !prettyPrint;
   
   //17
   cnt++;
   arrayCampi[cnt] = "GARE.DGUE_ELABSUB.ATTIVITA";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
   arrayTitoloColonne[cnt] = "Attività";
   arrayLarghezzaColonne[cnt] = 40;
   arrayCampiVisibili[cnt] = !prettyPrint;
   
   //18
   cnt++;
   arrayCampi[cnt] = "GARE.DGUE_ELABSUB.PRESTAZIONE";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
   arrayTitoloColonne[cnt] = "Prestazione";
   arrayLarghezzaColonne[cnt] = 40;
   arrayCampiVisibili[cnt] = !prettyPrint;
   
   //19
   cnt++;
   arrayCampi[cnt] = "GARE.DGUE_ELABSUB.QUOTA";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.DECIMALE2_ALIGN_RIGHT;
   arrayTitoloColonne[cnt] = "Quota";
   arrayLarghezzaColonne[cnt] = 10;
   arrayCampiVisibili[cnt] = !prettyPrint;
   //gestione disaccoppiata ditte con ruolo = 1 
   
   //20 gestione disaccoppiata ditte con ruolo = 2 
   cnt++;
   arrayCampi[cnt] = "GARE.DGUE_ELABSUB.DENOMINAZIONE";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
   arrayTitoloColonne[cnt] = "Denominazione ditta ausiliaria";
   arrayLarghezzaColonne[cnt] = 40;
   arrayCampiVisibili[cnt] = !prettyPrint;
   
   //21
   cnt++;
   arrayCampi[cnt] = "GARE.DGUE_ELABSUB.CF";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
   arrayTitoloColonne[cnt] = "CF o P.IVA / Id fiscale estero";
   arrayLarghezzaColonne[cnt] = 20;
   arrayCampiVisibili[cnt] = !prettyPrint;
   
   //22
   cnt++;
   arrayCampi[cnt] = "GARE.DGUE_ELABSUB.ATTIVITA";
   arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
   arrayTitoloColonne[cnt] = "Attività";
   arrayLarghezzaColonne[cnt] = 40;
   arrayCampiVisibili[cnt] = !prettyPrint;
   //gestione disaccoppiata ditte con ruolo = 2 

  for (int i = 0; i < arrayCampi.length; i++) {
    if(i==0) {
      if(arrayCampiVisibili[i]==true) {
        arrayIndiceColonnaCampi[i] = i + 1;
      }
      else {
          arrayIndiceColonnaCampi[i] = i;
      }
    }
    else {
        if(arrayCampiVisibili[i]==true) {
            arrayIndiceColonnaCampi[i] = arrayIndiceColonnaCampi[i-1]+1;
        }
        else {
            arrayIndiceColonnaCampi[i] = arrayIndiceColonnaCampi[i-1] ;
        }
    }
  }
}
}
