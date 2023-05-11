/*
 * Created on 15/oct/2021
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
import it.eldasoft.utils.utility.UtilityStringhe;

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
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Classe per la gestione delle funzionalita' di export degli operatori iscritti su foglio Excel
 *
 * @author Riccardo.Peruzzo
 */


public class ExportOperatoriIscrittiManager {

  static Logger                     logger = Logger.getLogger(ExportOperatoriIscrittiManager.class);

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

  private final String       FOGLIO_LISTA_OPERATORI[]                             = { "Lista operatori iscritti", "Sommario" };

  private final int          FOGLIO_LISTA_OPERATORI_RIGA_NOME_FISICO_CAMPI        = 3;

  private final int          FOGLIO_LISTA_OPERATORI__RIGA_INIZIALE                = 10;

  /**
   * Nome del file associato al file Excel contenente l'export degli operatori iscritti
   */

  private final String       NOME_FILE_C0OGGASS_EXPORT                                    = "iscrittiCategoria.xlsx";

  private XSSFWorkbook       workBook                                                     = null;

  private String[]           arrayCampi;
  private int[]              arrayStiliCampi;
  private String[]           arrayTitoloColonne;
  private int[]              arrayLarghezzaColonne;
  private boolean[]          arrayCampiVisibili;
  private int[]              arrayIndiceColonnaCampi;


  /**
   * Export degli operatori iscritti file Excel con salvataggio dello stesso nella
   * directory temporanea dell'application server.
   * Ritorna il nome del file salvato nella directory temporanea
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
  public String exportOperatoriIscritti(String ngara, String categoria,HttpSession session)
  throws FileNotFoundException, SQLException, GestoreException, IOException,
      Exception {
	  
    this.setDefinizioni(ngara);

    this.workBook = new XSSFWorkbook();
    // Creazione del dizionario degli stili delle celle dell'intero file Excel
    DizionarioStiliExcelX dizStiliExcel = new DizionarioStiliExcelX(this.workBook);

    // Scrittura sul foglio Excel degli operatori iscritti
    this.setOperatoriIscritti(ngara, categoria, dizStiliExcel);

    // Set del foglio 'Lista operatori iscritti' come foglio attivo

      this.workBook.setActiveSheet(this.workBook.getSheetIndex(FOGLIO_LISTA_OPERATORI[0]));
      this.workBook.setSelectedTab(this.workBook.getSheetIndex(FOGLIO_LISTA_OPERATORI[0]));

    // Creazione di un file temporaneo nella cartella temporanea e salvataggio
    // del suo nome in sessione nell'oggetto TempfileDeleter
    String nomeFile = ngara.toUpperCase().replaceAll("/", "_");
    nomeFile += "_" + FilenameUtils.getBaseName(NOME_FILE_C0OGGASS_EXPORT) +  "_" + categoria + "." +
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
   private void setOperatoriIscritti(String ngara, String categoria, DizionarioStiliExcelX dizStiliExcel)
         throws GestoreException, SQLException {

     Sheet foglioListaOperatori = workBook.createSheet(FOGLIO_LISTA_OPERATORI[0]);

     String selectCampiOperatoriIscritti = null;

     selectCampiOperatoriIscritti = "select DITG.NPROGG,DITG.DITTAO,DITG.NOMIMO,IMPR.CFIMP,IMPR.PIVIMP,DITG.ABILITAZ,DITG.COORDSIC,DITG.REQTORRE "
     		+ " from DITG,IMPR where DITG.DITTAO=IMPR.CODIMP and DITG.NGARA5=? and exists(select * from ISCRIZCAT where DITG.DITTAO=ISCRIZCAT.CODIMP and "
     		+ "DITG.CODGAR5=ISCRIZCAT.CODGAR and DITG.NGARA5=ISCRIZCAT.NGARA and ISCRIZCAT.CODCAT=?) order by DITG.NPROGG";

    try {

       int indiceColonna = 1;
       DizionarioCampi dizCampi = DizionarioCampi.getInstance();
       Campo campo = null;
       Row riga = null;
       
       List<CellStyle> stili = new ArrayList<CellStyle>();

       for (int ii = 0; ii < arrayCampi.length; ii++) {
         if (arrayCampiVisibili[ii])
        	 foglioListaOperatori.setDefaultColumnStyle(
               (short) (arrayIndiceColonnaCampi[ii] - 1),
               dizStiliExcel.getStileExcel(arrayStiliCampi[ii]));
         stili.add(dizStiliExcel.getStileExcel(arrayStiliCampi[ii]));
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

         riga = foglioListaOperatori.createRow(indiceRigaIntestazione);
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

             case FOGLIO_LISTA_OPERATORI_RIGA_NOME_FISICO_CAMPI - 1:
               // Seconda riga: nomi fisici dei campi
               if (stile == null)
                 stile = dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_NASCOSTA);
               campo = dizCampi.getCampoByNomeFisico(arrayCampi[ii].substring(arrayCampi[ii].indexOf(".") + 1));
               UtilityExcelX.scriviCella(foglioListaOperatori,
                   arrayIndiceColonnaCampi[ii], indiceRigaIntestazione + 1,
                   SCHEMA_CAMPI.concat(".").concat(campo.getNomeFisicoCampo()),
                   stile);
               break;
             case FOGLIO_LISTA_OPERATORI__RIGA_INIZIALE - 2:
               // Riga separatrice intestazione dai dati
               if (stile == null){
                 stile = dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_SEPARATRICE);
               }
               UtilityExcelX.scriviCella(foglioListaOperatori, arrayIndiceColonnaCampi[ii], indiceRigaIntestazione + 1, " ",
                   stile);
               break;
             }//switch
           }//if
         }  //for interno

           if (indiceRigaIntestazione == 0) {
             // Set titolo della colonna importo
             //UtilityExcel.scriviCella(foglioOperatoriIscritti, indiceColonnaImporto, indiceRigaIntestazione + 1, "Importo",stile);
           } else if (indiceRigaIntestazione > 0
               && indiceRigaIntestazione < FOGLIO_LISTA_OPERATORI__RIGA_INIZIALE - 2) {
             // Nascondo la riga del foglio
             riga.setZeroHeight(true);
           } else if (indiceRigaIntestazione == FOGLIO_LISTA_OPERATORI__RIGA_INIZIALE - 2) {
             //UtilityExcel.scriviCella(foglioOperatoriIscritti,    indiceColonnaImporto, indiceRigaIntestazione + 1, " ", stile);
             // Set altezza della riga che divide l'intestazione dai dati
             riga.setHeightInPoints(3);
           }

       }//for esterno


         int indiceRiga = FOGLIO_LISTA_OPERATORI__RIGA_INIZIALE;

           List<?> listaCampiOperatoriIscritti = this.sqlDao.getVectorQueryForList(
        		   selectCampiOperatoriIscritti, new Object[] { ngara,categoria });

           if (listaCampiOperatoriIscritti != null && listaCampiOperatoriIscritti.size() > 0) {
             // Reset dell'indice di colonna
             indiceColonna = 1;
             for (int i = 0; i < listaCampiOperatoriIscritti.size(); i++) {
               Vector<?> record = (Vector<?>) listaCampiOperatoriIscritti.get(i);
               if (record != null && record.size() > 0) {
            	   
            	 // Campo NPROGG
                 if (arrayCampiVisibili[0]) {
                   Double nprogg = SqlManager.getValueFromVectorParam( record, 0).doubleValue();
                   if (nprogg != null)
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna++, indiceRiga + i, nprogg,  stili.get(indiceColonna));
                   else
                       indiceColonna++;
                  }
            	   
                 // Campo DITTAO 
                 if (arrayCampiVisibili[1]) {
                   String dittao = SqlManager.getValueFromVectorParam( record, 1).getStringValue();
                   if (dittao != null)
                       UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna++, indiceRiga + i, dittao,  stili.get(indiceColonna));
                   else
                       indiceColonna++;
                  }

                 // Campo NOMIMO 
                 if (arrayCampiVisibili[2]) {
                   String nomimo = SqlManager.getValueFromVectorParam( record, 2).getStringValue();
                   if (nomimo != null)
                      UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna++, indiceRiga + i, nomimo,  stili.get(indiceColonna));
                   else
                      indiceColonna++;
                 }

                 // Campo CFIMP
                 if (arrayCampiVisibili[3]) {
                   String codcig = SqlManager.getValueFromVectorParam( record, 3).getStringValue();
                   codcig = UtilityStringhe.convertiNullInStringaVuota(codcig);
                   if (!"".equals(codcig))
                      UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna++, indiceRiga + i, codcig,  stili.get(indiceColonna));
                   else
                      indiceColonna++;
                 }

                 // Campo PIVIMP
                 if (arrayCampiVisibili[4]) {
                   String not_gar = SqlManager.getValueFromVectorParam( record, 4).getStringValue();
                   not_gar = UtilityStringhe.convertiNullInStringaVuota(not_gar);
                   if (!"".equals(not_gar))
                      UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna++, indiceRiga + i, not_gar,  stili.get(indiceColonna));
                   else
                      indiceColonna++;
                 }

                 // Campo ABILITAZ 
                 if (arrayCampiVisibili[5]) {
                   Long abilitaz = SqlManager.getValueFromVectorParam( record, 5).longValue();
                   if (abilitaz != null) {
                	  String abilitazStr = abilitaz + "";
                	  abilitazStr = this.tabellatiManager.getDescrTabellato("A1075", abilitazStr);
                      UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna++, indiceRiga + i, abilitazStr,  stili.get(indiceColonna));
                   }
                   else
                      indiceColonna++;
                 }

                 // Campo COORDSIC
                 if (arrayCampiVisibili[6]) {
                   String coordsic = SqlManager.getValueFromVectorParam( record, 6).getStringValue();
                   if (coordsic != null) {
                	  String coordsicStr;
                	  if("1".equals(coordsic)) 
                		  coordsicStr="Si";
                	  else
                		  coordsicStr="No";
                      UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna++, indiceRiga + i, coordsicStr,  stili.get(indiceColonna));
                   }
                   else
                      indiceColonna++;
                 }

                 // Campo REQTORRE
                 if (arrayCampiVisibili[7]) {
                   String reqtorre = SqlManager.getValueFromVectorParam( record, 7).getStringValue();
                   if (reqtorre != null) {
                	   String reqtorreStr;
                	   if("1".equals(reqtorre)) 
                		   reqtorreStr="Si";
                	   else
                		   reqtorreStr="No";
                	   UtilityExcelX.scriviCella(foglioListaOperatori,indiceColonna++, indiceRiga + i, reqtorreStr,  stili.get(indiceColonna));
                   }
                   else
                      indiceColonna++;
                 }
                 // Reset dell'indice di colonna
                 indiceColonna = 1;
               }//if record
             }//for i
             indiceRiga += listaCampiOperatoriIscritti.size();
           }//if



    } catch (SQLException s) {
      logger.error("Export operatori iscritti'" + ngara
          + "': errore durante l'export su Excel dello sheet 'Lista operatori iscritti'");
      throw s;
    }


   }//end setOperatoriIscritti

/**
 * Set degli array di definizione 
 *
 * 
 * 
 */
private void setDefinizioni(String ngara) throws SQLException, GestoreException {

  if (true) {
	  
	  String selectGarealbo = "select coordsic,reqtorre from garealbo where ngara=?";
	  
	  Vector<?> recordGarealbo = this.sqlDao.getVectorQuery(
			  selectGarealbo, new Object[] { ngara });

	  String coordsic = SqlManager.getValueFromVectorParam( recordGarealbo, 0).getStringValue();
	  
	  String reqtorre = SqlManager.getValueFromVectorParam( recordGarealbo, 1).getStringValue();
	  
      int numeroTotaleCampi = 8;
      int cnt = 0;

      arrayCampi = new String[numeroTotaleCampi];
      arrayStiliCampi = new int[numeroTotaleCampi];
      arrayTitoloColonne = new String[numeroTotaleCampi];
      arrayLarghezzaColonne = new int[numeroTotaleCampi];
      arrayCampiVisibili = new boolean[numeroTotaleCampi];
      arrayIndiceColonnaCampi = new int[numeroTotaleCampi];

      // 0
      arrayCampi[cnt] = "GARE.DITG.NPROGG";
      arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_CENTER;
      arrayTitoloColonne[cnt] = "Numero ord. ditta";
      arrayLarghezzaColonne[cnt] = 18;
      arrayCampiVisibili[cnt] = true;
      
      // 1
      cnt++;
      arrayCampi[cnt] = "GARE.DITG.DITTAO";
      arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_CENTER;
      arrayTitoloColonne[cnt] = "Codice ditta";
      arrayLarghezzaColonne[cnt] = 13;
      arrayCampiVisibili[cnt] = true;

      // 2
      cnt++;
      arrayCampi[cnt] = "GARE.DITG.NOMIMO";
      arrayStiliCampi[cnt] = DizionarioStiliExcelX.INTERO_ALIGN_LEFT;
      arrayTitoloColonne[cnt] = "Rag.sociale ditta";
      arrayLarghezzaColonne[cnt] = 61;
      arrayCampiVisibili[cnt] = true;

      // 3
      cnt++;
      arrayCampi[cnt] = "GARE.IMPR.CFIMP";
      arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_CENTER;
      arrayTitoloColonne[cnt] = "Codice fiscale";
      arrayLarghezzaColonne[cnt] = 16;
      arrayCampiVisibili[cnt] = true;

      // 4
      cnt++;
      arrayCampi[cnt] = "GARE.IMPR.PIVIMP";
      arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_CENTER;
      arrayTitoloColonne[cnt] = "Partita I.V.A.";
      arrayLarghezzaColonne[cnt] = 16;
      arrayCampiVisibili[cnt] = true;

      // 5
      cnt++;
      arrayCampi[cnt] = "GARE.DITG.ABILITAZ";
      arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_CENTER;
      arrayTitoloColonne[cnt] = "Stato abilitazione";
      arrayLarghezzaColonne[cnt] = 19;
      arrayCampiVisibili[cnt] = true;

      // 6
      cnt++;
      arrayCampi[cnt] = "GARE.DITG.COORDSIC";
      arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_CENTER;
      arrayTitoloColonne[cnt] = "Coord.sicurezza?";
      arrayLarghezzaColonne[cnt] = 17;
      if ("1".equals(coordsic)) {
    	  arrayCampiVisibili[cnt] = true;
      }
      else
    	  arrayCampiVisibili[cnt] = false;
      
      // 7
      cnt++;
      arrayCampi[cnt] = "GARE.DITG.REQTORRE";
      arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_CENTER;
      arrayTitoloColonne[cnt] = "Requisito asc.torre?";
      arrayLarghezzaColonne[cnt] = 22;
      if ("1".equals(reqtorre)) {
    	  arrayCampiVisibili[cnt] = true;
  	  }
      else
    	  arrayCampiVisibili[cnt] = false;


    for (int i = 0; i < arrayCampi.length; i++) {
    	if(i==0) {
    		arrayIndiceColonnaCampi[i] = i + 1;
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
}
