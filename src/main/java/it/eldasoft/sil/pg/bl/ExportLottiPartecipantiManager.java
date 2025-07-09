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
import it.eldasoft.utils.utility.UtilityExcelX;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Classe per la gestione delle funzionalita' di export degli operatori iscritti su foglio Excel
 *
 * @author Manuel.Bridda
 */


public class ExportLottiPartecipantiManager {

    static Logger                     logger = Logger.getLogger(ExportLottiPartecipantiManager.class);

    private SqlDao                    sqlDao;

    public void setSqlDao(SqlDao sqlDao) {
        this.sqlDao = sqlDao;
    }

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

    private final String[]      FOGLIO_LISTA                                                = { "Lista lotti","Lista operatori economici"};

    private final int           FOGLIO_LISTA_RIGA_INIZIALE                                  = 2;
    private final int           numeroTotaleCampi                                           = 10;

    /**
     * Nome del file associato al file Excel contenente l'export degli operatori iscritti
     */
    private final String       NOME_FILE_C0OGGASS_EXPORT                                    = "RiepilogoLottiPartecipanti.xlsx";

    private XSSFWorkbook       workBook                                                     = null;
    private String[]           arrayCampi;
    private int[]              arrayStiliCampi;
    private String[]           arrayTitoloColonne;
    private int[]              arrayLarghezzaColonne;
    private boolean[]          arrayCampiVisibili;
    private int[]              arrayIndiceColonnaCampi;


    /**
     * Export Xlsx dei lotti della gara coi relativi partecipanti.
     *

     * @param session
     *        sessione dell'utente
     * @return Ritorna il nome del file salvato nella cartella temporanea
     */
    public String exportLottiPartecipanti(String codgar, boolean orderByPartecipante, HttpSession session)
            throws FileNotFoundException, SQLException, GestoreException, IOException,
            Exception {

        this.setDefinizioni(orderByPartecipante);

        this.workBook = new XSSFWorkbook();
        // Creazione del dizionario degli stili delle celle dell'intero file Excel
        DizionarioStiliExcelX dizStiliExcel = new DizionarioStiliExcelX(this.workBook);

        // Scrittura sul foglio Excel
        this.setDati(codgar, orderByPartecipante, dizStiliExcel);

        // Set del foglio attivo
        int foglioAttivo=orderByPartecipante?1:0;
        this.workBook.setActiveSheet(this.workBook.getSheetIndex(FOGLIO_LISTA[foglioAttivo]));
        this.workBook.setSelectedTab(this.workBook.getSheetIndex(FOGLIO_LISTA[foglioAttivo]));

        // Creazione di un file temporaneo nella cartella temporanea e salvataggio
        // del suo nome in sessione nell'oggetto TempfileDeleter
        String nomeFile = codgar.toUpperCase().replace("/", "_").replace("$", "");
        nomeFile += "_" + FilenameUtils.getBaseName(NOME_FILE_C0OGGASS_EXPORT)+ "." +
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
    }

    /**
     * Scrittura su foglio Excel dei dati
     */
    private void setDati(String codgar, boolean orderByPartecipante, DizionarioStiliExcelX dizStiliExcel)
            throws GestoreException, SQLException {

        String nomefoglio = FOGLIO_LISTA[0];
        String orderby = "";

        if(!orderByPartecipante){
            orderby=" g.codiga,d.numordpl";
        }else{
            orderby=" d.numordpl,g.codiga";
            nomefoglio=FOGLIO_LISTA[1];
        }
                                //0     //1      //2       //3      //4        //5      //6     //7      //8      //9        //10     //11     //12
        String select = "select g.ngara,g.codcig,g.not_gar,g.impapp,d.numordpl,d.nomimo,i.cfimp,i.pivimp,d.ammgar,t.tab1desc,i.codimp,i.tipimp,g.codiga " +
                " from gare g left join ditg d on g.ngara=d.ngara5 and d.partgar='1' and d.invoff='1' left join impr i on i.codimp=d.dittao left join tab1 t on t.tab1tip=d.fasgar and t.tab1cod='A1011' " +
                " where g.codgar1=? and g.codgar1<>g.ngara order by"+orderby;

        Sheet foglioDati = workBook.createSheet(nomefoglio);

        try {
            int indiceColonna;
            List<CellStyle> stili = new ArrayList<>();
            stili.add(dizStiliExcel.getStileExcel(1));//fittizio

            for (int ii = 0; ii < arrayCampi.length; ii++) {
                if (arrayCampiVisibili[ii]) {
                    foglioDati.setDefaultColumnStyle(
                            (short) (arrayIndiceColonnaCampi[ii] - 1),
                            dizStiliExcel.getStileExcel(arrayStiliCampi[ii]));
                    stili.add(dizStiliExcel.getStileExcel(arrayStiliCampi[ii]));
                }
            }
            foglioDati.setPrintGridlines(true);
            // (0.77 inc <=> 2 cm)
            foglioDati.setMargin(Sheet.TopMargin, 0.77);
            foglioDati.setMargin(Sheet.BottomMargin, 0.77);
            foglioDati.setMargin(Sheet.LeftMargin, 0.77);
            foglioDati.setMargin(Sheet.RightMargin, 0.77);

            // Set delle informazioni per l'area di stampa dei dati del foglio
            PrintSetup printSetup = foglioDati.getPrintSetup();
            printSetup.setLandscape(true);
            printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);

            printSetup.setScale((short) 70); // Presumo significhi zoom al 70%
            // Scrittura dell'intestazione della foglio
            for (int indiceRigaIntestazione = 0; indiceRigaIntestazione < FOGLIO_LISTA_RIGA_INIZIALE; indiceRigaIntestazione++) {
                CellStyle stile = null;
                for (int ii = 0; ii < arrayCampi.length; ii++) {
                    if (arrayCampiVisibili[ii]) {
                        if (indiceRigaIntestazione == 0) {
                            if (stile == null) {
                                stile = dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_INTESTAZIONE);
                            }
                            UtilityExcelX.scriviCella(foglioDati,
                                    arrayIndiceColonnaCampi[ii], indiceRigaIntestazione + 1,
                                    arrayTitoloColonne[ii], stile);
                            UtilityExcelX.setLarghezzaColonna(foglioDati,
                                    arrayIndiceColonnaCampi[ii], arrayLarghezzaColonne[ii]);
                        }
                    }
                }

                int indiceRiga = FOGLIO_LISTA_RIGA_INIZIALE;

                List<?> listaCampi = this.sqlDao.getVectorQueryForList(
                        select, new Object[]{codgar});

                if (listaCampi != null && !listaCampi.isEmpty()) {
                    // Reset dell'indice di colonna
                    indiceColonna = 1;
                    int indexArrayCampi = 0;
                    if (orderByPartecipante)
                        indexArrayCampi = 5; //questo per gestire il doppio ordinamento delle colonne: pari al numero di colonne sotto if(!orderByPartecipante)
                    for (int i = 0; i < listaCampi.size(); i++) {
                        Vector<?> record = (Vector<?>) listaCampi.get(i);
                        if (record != null && !record.isEmpty()) {
                            //i dati qui sotto mi servono per gestire il caso di RTI
                            String codimp = SqlManager.getValueFromVectorParam(record, 10).stringValue();
                            Long tipimp=SqlManager.getValueFromVectorParam(record, 11).longValue();

                            Vector<?> datiMandataria = null;
                            if(new Long(3).equals(tipimp) || new Long(10).equals(tipimp)){
                                datiMandataria = this.sqlDao.getVectorQuery("select nomimp, cfimp, pivimp from impr join ragimp on coddic=codimp where codime9=? and impman='1'",new Object[]{codimp});
                            }

                            String numerogara = SqlManager.getValueFromVectorParam(record, 0).stringValue();
                            String codcig = SqlManager.getValueFromVectorParam(record, 1).getStringValue();
                            String not_gar = SqlManager.getValueFromVectorParam(record, 2).getStringValue();
                            Double impapp = (Double) SqlManager.getValueFromVectorParam(record, 3).getValue();
                            Long numordpl = SqlManager.getValueFromVectorParam(record, 4).longValue();
                            String nomimo = SqlManager.getValueFromVectorParam(record, 5).getStringValue();
                            String cf = SqlManager.getValueFromVectorParam(record, 6).getStringValue();
                            //String piva = SqlManager.getValueFromVectorParam(record, 7).getStringValue();
                            if (datiMandataria != null) {
                                cf = SqlManager.getValueFromVectorParam(datiMandataria, 1).getStringValue();
                                //piva = SqlManager.getValueFromVectorParam(datiMandataria, 2).getStringValue();
                            }
                            String ammgar = (String) SqlManager.getValueFromVectorParam(record, 8).getValue();

                            String fasgarDecoded = (String) SqlManager.getValueFromVectorParam(record, 9).getValue();
                            String codiga = (String) SqlManager.getValueFromVectorParam(record, 12).getValue();

                            boolean esisteDitta=numordpl!=null || StringUtils.isNotBlank(nomimo) || StringUtils.isNotBlank(cf);
                            //se sto facendo l'export per partecipante, non riporto i lotti deserti
                            if(orderByPartecipante && !esisteDitta)
                                continue;

                            for (int j = 0; j < 2; j++) {
                                if (!orderByPartecipante) {
                                    //Campo NGARA
                                    if (arrayCampiVisibili[indexArrayCampi % numeroTotaleCampi]) {
                                        if (StringUtils.isNotBlank(numerogara))
                                            UtilityExcelX.scriviCella(foglioDati, indiceColonna, indiceRiga + i, numerogara, stili.get(indiceColonna));
                                        indiceColonna++;
                                    }

                                    //Campo CODIGA
                                    if (arrayCampiVisibili[(indexArrayCampi + 1) % numeroTotaleCampi]) {

                                        if (StringUtils.isNotBlank(codiga))
                                            UtilityExcelX.scriviCella(foglioDati, indiceColonna, indiceRiga + i, codiga, stili.get(indiceColonna));
                                        indiceColonna++;
                                    }

                                    //Campo CODCIG
                                    if (arrayCampiVisibili[(indexArrayCampi + 2) % numeroTotaleCampi]) {

                                        if (StringUtils.isNotBlank(codcig))
                                            UtilityExcelX.scriviCella(foglioDati, indiceColonna, indiceRiga + i, codcig, stili.get(indiceColonna));
                                        indiceColonna++;
                                    }

                                    //Campo NOT_GAR
                                    if (arrayCampiVisibili[(indexArrayCampi + 3) % numeroTotaleCampi]) {
                                        if (StringUtils.isNotBlank(not_gar))
                                            UtilityExcelX.scriviCella(foglioDati, indiceColonna, indiceRiga + i, not_gar, stili.get(indiceColonna));
                                        indiceColonna++;
                                    }

                                    //Campo IMPAPP
                                    if (arrayCampiVisibili[(indexArrayCampi + 4) % numeroTotaleCampi]) {
                                        if (impapp != null)
                                            UtilityExcelX.scriviCella(foglioDati, indiceColonna, indiceRiga + i, impapp, stili.get(indiceColonna));
                                        indiceColonna++;
                                    }
                                } else {
                                    //Campo NUMORDPL
                                    if (arrayCampiVisibili[(indexArrayCampi + 5) % numeroTotaleCampi]) {
                                        if (numordpl != null)
                                            UtilityExcelX.scriviCella(foglioDati, indiceColonna, indiceRiga + i, numordpl, stili.get(indiceColonna));
                                        indiceColonna++;
                                    }

                                    //Campo NOMIMO
                                    if (arrayCampiVisibili[(indexArrayCampi + 6) % numeroTotaleCampi]) {
                                        if (StringUtils.isNotBlank(nomimo)){
                                            if (datiMandataria != null)
                                                nomimo += " (mandataria " + SqlManager.getValueFromVectorParam(datiMandataria, 0).stringValue() + ")";
                                            UtilityExcelX.scriviCella(foglioDati, indiceColonna, indiceRiga + i, nomimo, stili.get(indiceColonna));
                                        }
                                        indiceColonna++;
                                    }
                                    //Campo CF/PIVA
                                    if (arrayCampiVisibili[(indexArrayCampi + 7) % numeroTotaleCampi]) {
                                        if (StringUtils.isNotBlank(cf))
                                            UtilityExcelX.scriviCella(foglioDati, indiceColonna, indiceRiga + i, cf, stili.get(indiceColonna));
                                        indiceColonna++;
                                    }
                                }
                                orderByPartecipante = !orderByPartecipante;
                            }
                            //Campo AMMGAR
                            if (arrayCampiVisibili[8]) {
                                if(esisteDitta) {
                                    String ammisDecoded = "Si";
                                    if ("2".equals(ammgar))
                                        ammisDecoded = "No";
                                    UtilityExcelX.scriviCella(foglioDati, indiceColonna, indiceRiga + i, ammisDecoded, stili.get(indiceColonna));
                                }
                                indiceColonna++;
                            }

                            //Campo FASGAR
                            if (arrayCampiVisibili[9]) {
                                if (StringUtils.isNotBlank(fasgarDecoded))
                                    UtilityExcelX.scriviCella(foglioDati, indiceColonna, indiceRiga + i, fasgarDecoded, stili.get(indiceColonna));

                                indiceColonna++;
                            }
                            // Reset dell'indice di colonna
                            indiceColonna = 1;
                        }
                    }
                    indiceRiga += listaCampi.size();
                }
            }

        } catch (SQLException s) {
            logger.error("Export riepilogo lotti e partecipanti'" + codgar
                    + "': errore durante la creazione del foglio Excel");
            throw s;
        }


    }

    /**
     * Set degli array di definizione
     *
     *
     *
     */
    private void setDefinizioni(boolean orderByPartecipante) {
        int cnt = 0;

        arrayCampi = new String[numeroTotaleCampi];
        arrayStiliCampi = new int[numeroTotaleCampi];
        arrayTitoloColonne = new String[numeroTotaleCampi];
        arrayLarghezzaColonne = new int[numeroTotaleCampi];
        arrayCampiVisibili = new boolean[numeroTotaleCampi];
        arrayIndiceColonnaCampi = new int[numeroTotaleCampi];
        for(int i=0;i<2;i++) {
            if(!orderByPartecipante){
                arrayCampi[cnt] = "GARE.GARE.NGARA";
                arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
                arrayTitoloColonne[cnt] = "Codice lotto";
                arrayLarghezzaColonne[cnt] = 14;
                arrayCampiVisibili[cnt] = true;
                cnt++;

                arrayCampi[cnt] = "GARE.GARE.CODIGA";
                arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_RIGHT;
                arrayTitoloColonne[cnt] = "Lotto";
                arrayLarghezzaColonne[cnt] = 5;
                arrayCampiVisibili[cnt] = true;
                cnt++;

                arrayCampi[cnt] = "GARE.GARE.CODCIG";
                arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
                arrayTitoloColonne[cnt] = "Codice CIG";
                arrayLarghezzaColonne[cnt] = 10;
                arrayCampiVisibili[cnt] = true;
                cnt++;

                arrayCampi[cnt] = "GARE.GARE.NOT_GAR";
                arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
                arrayTitoloColonne[cnt] = "Oggetto";
                arrayLarghezzaColonne[cnt] = 80;
                arrayCampiVisibili[cnt] = true;
                cnt++;

                arrayCampi[cnt] = "GARE.GARE.IMPAPP";
                arrayStiliCampi[cnt] = DizionarioStiliExcelX.DECIMALE2_ALIGN_RIGHT;
                arrayTitoloColonne[cnt] = "Importo a base di gara";
                arrayLarghezzaColonne[cnt] = 11;
                arrayCampiVisibili[cnt] = true;
                cnt++;
            }else{
                arrayCampi[cnt] = "GARE.DITG.NUMORDPL";
                arrayStiliCampi[cnt] = DizionarioStiliExcelX.INTERO_ALIGN_RIGHT;
                arrayTitoloColonne[cnt] = "N.plico";
                arrayLarghezzaColonne[cnt] = 7;
                arrayCampiVisibili[cnt] = true;
                cnt++;

                arrayCampi[cnt] = "GARE.DITG.NOMIMO";
                arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
                arrayTitoloColonne[cnt] = "Rag.sociale ditta";
                arrayLarghezzaColonne[cnt] = 61;
                arrayCampiVisibili[cnt] = true;
                cnt++;

                arrayCampi[cnt] = "GARE.IMPR.PIVIMP";
                arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
                arrayTitoloColonne[cnt] = "CF/P.IVA.";
                arrayLarghezzaColonne[cnt] = 16;
                arrayCampiVisibili[cnt] = true;
                cnt++;
            }
            orderByPartecipante=!orderByPartecipante;
        }
        arrayCampi[cnt] = "GARE.DITG.AMMGAR";
        arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_CENTER;
        arrayTitoloColonne[cnt] = "Ammissione";
        arrayLarghezzaColonne[cnt] = 10;
        arrayCampiVisibili[cnt] = true;
        cnt++;

        arrayCampi[cnt] = "GARE.DITG.FASGAR";
        arrayStiliCampi[cnt] = DizionarioStiliExcelX.STRINGA_ALIGN_LEFT;
        arrayTitoloColonne[cnt] = "Fase di esclusione";
        arrayLarghezzaColonne[cnt] = 30;
        arrayCampiVisibili[cnt] = true;

        for (int i = 0; i < arrayCampi.length; i++) {
            if(i==0) {
                if(arrayCampiVisibili[i]) {
                    arrayIndiceColonnaCampi[i] = i + 1;
                }
                else {
                    arrayIndiceColonnaCampi[i] = i;
                }
            }
            else {
                if(arrayCampiVisibili[i]) {
                    arrayIndiceColonnaCampi[i] = arrayIndiceColonnaCampi[i-1]+1;
                }
                else {
                    arrayIndiceColonnaCampi[i] = arrayIndiceColonnaCampi[i-1] ;
                }
            }
        }
    }
}
