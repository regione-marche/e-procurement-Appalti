/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eldasoft.sil.pg.bl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.upload.FormFile;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.excel.DizionarioStiliExcelX;
import it.eldasoft.sil.pg.bl.excel.ExcelUtils;
import it.eldasoft.sil.pg.bl.excel.ExcelUtilsX;
import it.eldasoft.sil.pg.bl.excel.bean.ImportAdempimenti190Bean;
import it.eldasoft.sil.pg.bl.excel.bean.ImportAdempimenti190ConfigBean;
import it.eldasoft.sil.pg.bl.excel.bean.ImportAdempimenti190ResultBean;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityExcel;
import it.eldasoft.utils.utility.UtilityExcelX;
import it.eldasoft.utils.utility.UtilityFiscali;
import it.eldasoft.utils.utility.UtilityNumeri;

/**
 *
 * @author marco.perazzetta
 */
public class Adempimenti190Manager {

	static Logger logger = Logger.getLogger(Adempimenti190Manager.class);
	private SqlManager sqlManager;
	private PgManager pgManager;
	private GenChiaviManager genChiaviManager;
	public static final String MODELLO_ADEMPIMENTI_LEGGE_190 = "datilegge190.xlsx";
	public static final String FOGLIO_MODELLO_ADEMPIMENTI_LEGGE_190 = "Gare";
	private static final int START_ROW = 2;
	private static final int COL_CODICE_FISCALE_PROP = 1;
	private static final int COL_RAGIONE_SOCIALE_PROP = 2;
	private static final int COL_ANNO_RIF = 3;
	private static final int COL_CIG = 4;
	private static final int COL_OGGETTO_LOTTO = 5;
	private static final int COL_PROCEDURA_SCELTA_CONTR = 6;
	private static final int COL_CODICE_FISCALE_OPE_ITA = 7;
	private static final int COL_CODICE_FISCALE_OPE_ESTERO = 8;
	private static final int COL_RAGIONE_SOCIALE_OPE = 9;
	private static final int COL_DEN_RAGGRUPPAMENTO = 10;
	private static final int COL_RUOLO_OPE = 11;
	private static final int COL_AGGIUDICATARIO = 12;
	private static final int COL_IMPORTO_AGGIUDICAZIONE = 13;
	private static final int COL_DATA_INIZIO = 14;
	private static final int COL_DATA_ULTIMAZIONE = 15;
	private static final int COL_IMPORTO_SOMME_LIQUIDATE = 16;
	private static final int COL_CODICE_FISCALE_RSPONSABILE = 17;
	private static final int COL_NOME_COGNOME_RESPONSABILE = 18;
	private static final int INDEX_LAST_EXAMPLE_ROW = 12;
	private static final String DV_SCELTA_CONTRAENTE = "'Scelta Contraente'!$A$1:$A$29";
	private static final String DV_RUOLO = "'Ruolo'!$A$1:$A$5";
	private static final int CIG_MIN_LENGHT = 10;
	private static final String CODICE_SCELTA = "A2044";
	private static final String CODICE_RUOLO = "A1094";
	private static final int IN_CORSO = 1;
	private static final int AGGIUDICATO = 2;
	private static String SQL_GET_SCELTA_O_RUOLO = "SELECT tab1tip FROM tab1 where upper(tab1desc) = ? AND tab1cod = ?";
	private static String SQL_GET_LOTTO_BY_CIG = "SELECT id as IDLOTTO, LOTTOINBO, DAANNOPREC, CODFISRESP from ANTICORLOTTI where CIG=? and IDANTICOR=? ";
	private static String SQL_GET_ANTICOR_CODEIN = "SELECT codein FROM anticor WHERE id = ?";
	private static String SQL_GET_CF_STAZAPP = "SELECT cfein FROM uffint WHERE codein = ?";
	private static String SQL_INSERT_LOTTI = "INSERT INTO anticorlotti(id, idanticor, daannoprec, cig, codfiscprop, denomprop, oggetto, sceltacontr,  "
					+ "impaggiudic, datainizio, dataultimazione, impsommeliq, stato, lottoinbo, pubblica, inviabile, codfisresp, nomeresp) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
	private static String SQL_UPDATE_CONTROLLI_FIELDS = "UPDATE anticorlotti SET pubblica = ?, inviabile = ?, testolog=? WHERE id = ?";
	private static String SQL_DELETE_PARTECIP = "DELETE FROM anticorpartecip WHERE idanticorlotti = ? ";
	private static String SQL_INSERT_PARTECIP = "INSERT INTO anticorpartecip(id, idanticorlotti, tipo, aggiudicataria, ragsoc) "
					+ "VALUES(?,?,?,?,?)";
	private static String SQL_INSERT_DITTE = "INSERT INTO anticorditte(id, idanticorpartecip, ragsoc, codfisc, idfiscest, ruolo) "
					+ "VALUES(?,?,?,?,?,?)";
	private static String SQL_SET_ANTICORLOTTO_AGGIUDICATO = "UPDATE anticorlotti SET stato = ? WHERE id = ? ";
	private static String SQL_GET_CIG_LOTTO_BY_IDCONTRATTO = "select cig,id from anticorlotti where idanticor=? and idcontratto=?";
	//private static String SQL_CONTROLLO_LOTTI_CIG = "select count(*) from anticorlotti where idanticor=? and ((idcontratto=? and (cig is null or cig <> ?)) or (cig = ? and (idcontratto is null or idcontratto <> ?)))";
	private static String SQL_CONTROLLO_LOTTI_CIG = "select count(*) from anticorlotti where idanticor=? and cig = ? and (idcontratto is null or idcontratto <> ?)";
	private static String SQL_CONTROLLO_LOTTI_IDCONTRATTO = "select count(*) from anticorlotti where idanticor=? and idcontratto=? and (cig is null or cig <> ?)";
	private static String SQL_CONTROLLO_LOTTI_CIG_ANNOPREC = "select count(*) from anticorlotti where idanticor=? and idcontratto=? and cig = ? and daannoprec=?";
	private static String SQL_UPDATE_SAP = "UPDATE ANTICORLOTTI SET DATAINIZIO=?, DATAULTIMAZIONE=?, IMPSOMMELIQ=?, IDCONTRATTO=? WHERE ID=?";
	private static String SQL_CONTROLLO_AGGIUDICATARIE = "select tipo,  p.id from anticorlotti l, anticorpartecip p where l.idanticor=? and l.idcontratto=? and l.id= p.idanticorlotti and aggiudicataria='1'";

	private static final int NUMERO_COLONNE_FILE_SAP =15;
	private static final int COL_IDCONTRATTO_SAP = 1;
	private static final int COL_CIG_SAP = 2;
	private static final int COL_CODFISC_PROP_SAP = 3;
	private static final int COL_DENOMINAZIONE_SAP = 4;
	private static final int COL_OGGETTO_SAP = 5;
	private static final int COL_SCELTACONTRAENTE_SAP = 6;
	private static final int COL_COD_FISC_AGGIUD_SAP = 7;
	private static final int COL_ID_FISCALE_ESTERO_AGGIUD_SAP = 8;
	private static final int COL_RAG_SOG_AGGIUD_SAP = 9;
	private static final int COL_IMPORTO_AGGIUD_SAP = 10;
	private static final int COL_DATA_INIZIO_SAP = 11;
	private static final int COL_DATA_ULTIMAZIONE_SAP = 12;
	private static final int COL_IMPORTO_SOMME_LIQ_SAP = 13;
	private static final int COL_CODFISC_RESPONSABILE_SAP = 14;
	private static final int COL_NOM_COGN_REPSONSABILE_SAP = 15;
	private static String SQL_INSERT_LOTTI_SAP = "INSERT INTO anticorlotti(id, idanticor, daannoprec, cig, codfiscprop, denomprop, oggetto, sceltacontr,  "
        + "impaggiudic, datainizio, dataultimazione, impsommeliq, stato, lottoinbo, pubblica, inviabile, codfisresp, nomeresp,idcontratto) "
        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";


	/**
	 * Set SqlManager
	 *
	 * @param sqlManager
	 */
	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	/**
	 * Set PgManager
	 *
	 * @param pglManager
	 */
	public void setPgManager(PgManager pgManager) {
		this.pgManager = pgManager;
	}

	/**
	 * @param genChiaviManager the genChiaviManager to set
	 */
	public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
		this.genChiaviManager = genChiaviManager;
	}

	/**
	 * Esportazione in formato XLS
	 *
	 * @param idAnticor la idAnticor dell'adempimento da esportare
	 * @param percorsoExcel il percorso del template excel da compilare
	 * @param profiloUtente
	 * @return il byte array dell'excel da passare all'output stream
	 * @throws GestoreException eccezione sollevata dal manager
	 * @throws InvalidFormatException 
	 */
	public byte[] createExcel(String percorsoExcel, long idAnticor, ProfiloUtente profiloUtente) throws GestoreException, InvalidFormatException {

		if (percorsoExcel == null || percorsoExcel.equals("")) {
			throw new GestoreException("Modello non definito", "importaesportaexcel.modellonondefinito");
		}

		XSSFWorkbook wb = null;

		try {
			FileInputStream file = new FileInputStream(percorsoExcel);
			//POIFSFileSystem fs = new POIFSFileSystem(file);
			wb = new XSSFWorkbook(OPCPackage.open(file));
			file.close();
		} catch (FileNotFoundException e) {
			throw new GestoreException("File del modello non trovato", "importaesportaexcel.modellonontrovato", new Object[]{percorsoExcel}, e);
		} catch (IOException e) {
			throw new GestoreException("Errore nell'apertura del file modello", "importaesportaexcel.modelloerroreio", new Object[]{percorsoExcel}, e);
		}

		// Creazione del dizionario degli stili delle celle dell'intero file Excel
		DizionarioStiliExcelX dizStiliExcel = new DizionarioStiliExcelX(wb);

		if ("U".equals(profiloUtente.getAbilitazioneGare()) && StringUtils.isNotEmpty(profiloUtente.getCodiceFiscale())) {
			// Scrittura della pagina dei dati generali e dei criteri di valutazione
			this.populateExcel(wb, idAnticor, dizStiliExcel, profiloUtente.getCodiceFiscale());
		} else {
			// Scrittura della pagina dei dati generali e dei criteri di valutazione
			this.populateExcel(wb, idAnticor, dizStiliExcel, null);
		}



		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			wb.write(os);
		} catch (IOException e) {
			throw new GestoreException("Errore inaspettato durante la procedura di import/export excel", "importaesportaexcel.erroreinaspettato", e);
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				throw new GestoreException("Errore inaspettato durante la procedura di import/export excel", "importaesportaexcel.erroreinaspettato", e);
			}
		}
		return os.toByteArray();
	}

	/**
	 * Scrive i dati generali ed i criteri di valutazione all'interno del foglio
	 * "Criteri valutazione"
	 *
	 * @param ngara
	 * @throws GestoreException
	 */
	private void populateExcel(XSSFWorkbook wb, long idAnticor, DizionarioStiliExcelX dizStiliExcel, String codiceFiscaleResponsabile) throws GestoreException {

		//Recupero i dati dei lotti
		StringBuilder sbQuery;
		Vector<?> datiAnticor;
		List<?> datiAntiCorLotti;
		List<?> datiAntiCorPartecip;

		Sheet sheet = wb.getSheet(FOGLIO_MODELLO_ADEMPIMENTI_LEGGE_190);
		if (sheet == null) {
			throw new GestoreException("Non trovato il foglio " + FOGLIO_MODELLO_ADEMPIMENTI_LEGGE_190,
					"importaesportaexcel.foglionontrovato", new Object[]{FOGLIO_MODELLO_ADEMPIMENTI_LEGGE_190},
						null);
		}
		try {
			sbQuery = new StringBuilder();
			sbQuery.append("select ANNORIF from ANTICOR where ID=?");

			String selectAntiCor = sbQuery.toString();
			datiAnticor = this.sqlManager.getVector(selectAntiCor, new Object[] { idAnticor } );
			if (datiAnticor == null) {
				datiAnticor = new Vector();
			}

			sbQuery = new StringBuilder();
			sbQuery.append("SELECT acl.id, acl.cig, acl.codfiscprop, acl.denomprop, acl.oggetto, t1.tab1desc as sceltacontr, ")
                            .append("acl.impaggiudic, acl.datainizio, acl.dataultimazione, acl.impsommeliq, acl.daannoprec, ")
                            .append("acl.codfisresp, acl.nomeresp ")
                            .append("FROM anticorlotti acl ")
                            .append("LEFT JOIN TAB1 t1 ON t1.tab1tip = acl.sceltacontr AND t1.tab1cod = 'A2044' ")
                            .append("WHERE acl.idanticor = ? AND  ")
                            .append("((acl.daannoprec = '2' or acl.daannoprec = '3')  AND (acl.cig IS NOT NULL OR acl.cig <> '')) ");
			Object[] arrayObj = null;
			if (StringUtils.isEmpty(codiceFiscaleResponsabile)) {
				arrayObj = new Object[] { idAnticor };
			} else {
				sbQuery.append(" and acl.CODFISRESP=? ");
				arrayObj = new Object[] { idAnticor, codiceFiscaleResponsabile};
			}

			String selectAntiCorLotti = sbQuery.toString();
			datiAntiCorLotti = this.sqlManager.getListVector(selectAntiCorLotti, arrayObj );
			if (datiAntiCorLotti == null) {
				datiAntiCorLotti = new ArrayList<Vector>();
			}
			//rimuovo i commenti, danno problemi in lettura del file
			for(int i=0; i<sheet.getRow(0).getLastCellNum();i++) {
			  sheet.getRow(0).getCell(i).removeCellComment();
			}

			int indiceRigaCorrente = START_ROW;
			boolean rigaCreataDaLotto;
			Map<Integer, CellStyle> styleMap = new HashMap<Integer, CellStyle>();

			for (int i=0; i < datiAntiCorLotti.size(); i++) {
				rigaCreataDaLotto = false;

				//Se sto inserendo il nono record, copio la riga precedente e così via con i successivi
				if (indiceRigaCorrente > INDEX_LAST_EXAMPLE_ROW) {
					ExcelUtilsX.copyRow(sheet, indiceRigaCorrente - 1, indiceRigaCorrente, styleMap);
					rigaCreataDaLotto = true;
				}

				Long idLotto = SqlManager.getValueFromVectorParam(datiAntiCorLotti.get(i), 0).longValue();
				sbQuery = new StringBuilder();
				sbQuery.append("SELECT acp.aggiudicataria, acp.ragsoc as dengruppo, ")
								.append("acd.ragsoc as ragsocope, t1.tab1desc as ruolo, acd.codfisc as codfiscita, acd.idfiscest as codfiscest, acp.tipo ")
								.append("FROM anticorpartecip acp ")
								.append("LEFT JOIN anticorditte acd ON acp.id = acd.idanticorpartecip ")
								.append("LEFT JOIN TAB1 t1 ON t1.tab1tip = acd.ruolo AND t1.tab1cod = 'A1094' ")
								.append("WHERE acp.idanticorlotti = ?");
				String selectAntiCorPartecip = sbQuery.toString();
				datiAntiCorPartecip = sqlManager.getListVector(selectAntiCorPartecip, new Object[]{idLotto});
				if (datiAntiCorPartecip == null || datiAntiCorPartecip.isEmpty()) {
					datiAntiCorPartecip = createEmptyPartecipRecord();
				}

				for (int j = 0; j < datiAntiCorPartecip.size(); j++) {

					//Se sto inserendo il nono record, copio la riga precedente e così via con i successivi
					if (indiceRigaCorrente > INDEX_LAST_EXAMPLE_ROW && !rigaCreataDaLotto) {
						ExcelUtilsX.copyRow(sheet, indiceRigaCorrente - 1, indiceRigaCorrente, styleMap);
						rigaCreataDaLotto = false;
					}
					fillLottiData(sheet, indiceRigaCorrente, datiAnticor, datiAntiCorLotti, i, dizStiliExcel);
					fillDitteData(sheet, indiceRigaCorrente, datiAntiCorPartecip, j, dizStiliExcel);
					indiceRigaCorrente++;
					if (rigaCreataDaLotto) {
						rigaCreataDaLotto = false;
					}
				}
			}
			if (indiceRigaCorrente < INDEX_LAST_EXAMPLE_ROW) {
				datiAntiCorLotti = createEmptyLottoRecord();
				datiAntiCorPartecip = createEmptyPartecipRecord();
				for (int i = indiceRigaCorrente; i <= indiceRigaCorrente + (INDEX_LAST_EXAMPLE_ROW - indiceRigaCorrente) + 1; i++) {
					fillLottiData(sheet, i, datiAnticor, datiAntiCorLotti, 0, dizStiliExcel);
					fillDitteData(sheet, i, datiAntiCorPartecip, 0, dizStiliExcel);
				}
			}
		} catch (SQLException e) {
			throw new GestoreException("Errore inaspettato durante la procedura di import/export excel", "importaesportaexcel.erroreinaspettato", e);
		}
	}

	private List<Vector<JdbcParametro>> createEmptyLottoRecord() {

		List<Vector<JdbcParametro>> datiAntiCorLotti = new ArrayList<Vector<JdbcParametro>>();
		Vector<JdbcParametro> emptyRecord = new Vector<JdbcParametro>();
		int i = 0;
		emptyRecord.add(i++, new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null));
		emptyRecord.add(i++, new JdbcParametro(JdbcParametro.TIPO_TESTO, null));
		emptyRecord.add(i++, new JdbcParametro(JdbcParametro.TIPO_TESTO, null));
		emptyRecord.add(i++, new JdbcParametro(JdbcParametro.TIPO_TESTO, null));
		emptyRecord.add(i++, new JdbcParametro(JdbcParametro.TIPO_TESTO, null));
		emptyRecord.add(i++, new JdbcParametro(JdbcParametro.TIPO_TESTO, null));
		emptyRecord.add(i++, new JdbcParametro(JdbcParametro.TIPO_DECIMALE, null));
		emptyRecord.add(i++, new JdbcParametro(JdbcParametro.TIPO_DATA, null));
		emptyRecord.add(i++, new JdbcParametro(JdbcParametro.TIPO_DATA, null));
		emptyRecord.add(i++, new JdbcParametro(JdbcParametro.TIPO_DECIMALE, null));
		emptyRecord.add(i++, new JdbcParametro(JdbcParametro.TIPO_TESTO, null));
		datiAntiCorLotti.add(0, emptyRecord);
		return datiAntiCorLotti;
	}

	private List<Vector<JdbcParametro>> createEmptyPartecipRecord() {

		List<Vector<JdbcParametro>> datiAntiCorPartecip = new ArrayList<Vector<JdbcParametro>>();
		Vector<JdbcParametro> emptyRecord = new Vector<JdbcParametro>();
		int i = 0;
		emptyRecord.add(i++, new JdbcParametro(JdbcParametro.TIPO_TESTO, null));
		emptyRecord.add(i++, new JdbcParametro(JdbcParametro.TIPO_TESTO, null));
		emptyRecord.add(i++, new JdbcParametro(JdbcParametro.TIPO_TESTO, null));
		emptyRecord.add(i++, new JdbcParametro(JdbcParametro.TIPO_TESTO, null));
		emptyRecord.add(i++, new JdbcParametro(JdbcParametro.TIPO_TESTO, null));
		emptyRecord.add(i++, new JdbcParametro(JdbcParametro.TIPO_TESTO, null));
		emptyRecord.add(i++, new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null));
		datiAntiCorPartecip.add(0, emptyRecord);
		return datiAntiCorPartecip;
	}

	private void fillLottiData(Sheet sheet, int indiceRigaCorrente, Vector<?> datiAnticor, List<?> datiAntiCorLotti,
					int indiceRecordLotti, DizionarioStiliExcelX dizStiliExcel) throws GestoreException {

		String daAnnoPrecedente = SqlManager.getValueFromVectorParam(datiAntiCorLotti.get(indiceRecordLotti), 10).stringValue();
		Long anno = ((JdbcParametro) datiAnticor.get(0)).longValue();
		if (anno != null && daAnnoPrecedente != null) {
			UtilityExcelX.scriviCella(sheet, COL_ANNO_RIF, indiceRigaCorrente, anno, dizStiliExcel.getStileExcel(DizionarioStiliExcelX.INTERO_ALIGN_CENTER_FRAMED));
		} else {
			UtilityExcelX.scriviCella(sheet, COL_ANNO_RIF, indiceRigaCorrente, "", dizStiliExcel.getStileExcel(DizionarioStiliExcelX.INTERO_ALIGN_CENTER_FRAMED));
		}

		String cig = SqlManager.getValueFromVectorParam(datiAntiCorLotti.get(indiceRecordLotti), 1).getStringValue();
		if (cig != null) {
			UtilityExcelX.scriviCella(sheet, COL_CIG, indiceRigaCorrente, cig, dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		} else {
			UtilityExcelX.scriviCella(sheet, COL_CIG, indiceRigaCorrente, "", dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		}

		String oggettoLotto = SqlManager.getValueFromVectorParam(datiAntiCorLotti.get(indiceRecordLotti), 4).getStringValue();
		if (oggettoLotto != null ) {
			//UtilityExcel.scriviCella(sheet, COL_OGGETTO_LOTTO, indiceRigaCorrente, oggettoLotto, dizStiliExcel.getStileExcel(DizionarioStiliExcel.STRINGA_ALIGN_CENTER_FRAMED));
		  UtilityExcelX.scriviCella(sheet, COL_OGGETTO_LOTTO, indiceRigaCorrente, oggettoLotto, dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_GENERAL_FORMAT));
		} else {
			UtilityExcelX.scriviCella(sheet, COL_OGGETTO_LOTTO, indiceRigaCorrente, "", dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		}

		String sceltaContraente = SqlManager.getValueFromVectorParam(datiAntiCorLotti.get(indiceRecordLotti), 5).getStringValue();
		if (sceltaContraente != null) {
			UtilityExcelX.scriviCella(sheet, COL_PROCEDURA_SCELTA_CONTR, indiceRigaCorrente, sceltaContraente, dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		} else {
			UtilityExcelX.scriviCella(sheet, COL_PROCEDURA_SCELTA_CONTR, indiceRigaCorrente, "", dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		}
		CellRangeAddressList cralSceltaContraente = new CellRangeAddressList(indiceRigaCorrente, indiceRigaCorrente, COL_PROCEDURA_SCELTA_CONTR - 1, COL_PROCEDURA_SCELTA_CONTR - 1);
		
		DataValidationHelper dvHelper = sheet.getDataValidationHelper();
	    DataValidationConstraint dvcSceltaContraente = dvHelper.createFormulaListConstraint(DV_SCELTA_CONTRAENTE);
	    DataValidation dvSceltaContraente = dvHelper.createValidation(dvcSceltaContraente, cralSceltaContraente);
	    dvSceltaContraente.setSuppressDropDownArrow(true);
	    dvSceltaContraente.setShowErrorBox(true);
		dvSceltaContraente.setEmptyCellAllowed(true);
		dvSceltaContraente.setErrorStyle(DataValidation.ErrorStyle.STOP);
		dvSceltaContraente.createErrorBox("Errore", "Valore di Scelta contraente non valido");
		sheet.addValidationData(dvSceltaContraente);

		String codFiscProp = SqlManager.getValueFromVectorParam(datiAntiCorLotti.get(indiceRecordLotti), 2).getStringValue();
		if (codFiscProp != null) {
			UtilityExcelX.scriviCella(sheet, COL_CODICE_FISCALE_PROP, indiceRigaCorrente, codFiscProp, dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		} else {
			UtilityExcelX.scriviCella(sheet, COL_CODICE_FISCALE_PROP, indiceRigaCorrente, "", dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		}

		String ragSocialeProp = SqlManager.getValueFromVectorParam(datiAntiCorLotti.get(indiceRecordLotti), 3).getStringValue();
		if (ragSocialeProp != null) {
			UtilityExcelX.scriviCella(sheet, COL_RAGIONE_SOCIALE_PROP, indiceRigaCorrente, ragSocialeProp, dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		} else {
			UtilityExcelX.scriviCella(sheet, COL_RAGIONE_SOCIALE_PROP, indiceRigaCorrente, "", dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		}

		Double importoAgg = SqlManager.getValueFromVectorParam(datiAntiCorLotti.get(indiceRecordLotti), 6).doubleValue();
		if (importoAgg != null) {
			//String importoAggString = UtilityNumeri.convertiDouble(importoAgg);
			UtilityExcelX.scriviCella(sheet, COL_IMPORTO_AGGIUDICAZIONE, indiceRigaCorrente, importoAgg, dizStiliExcel.getStileExcel(DizionarioStiliExcelX.DECIMALE2_ALIGN_CENTER_FRAMED));
		} else {
			UtilityExcelX.scriviCella(sheet, COL_IMPORTO_AGGIUDICAZIONE, indiceRigaCorrente, "", dizStiliExcel.getStileExcel(DizionarioStiliExcelX.DECIMALE2_ALIGN_CENTER_FRAMED));
		}

		Timestamp dataInizioTime = SqlManager.getValueFromVectorParam(datiAntiCorLotti.get(indiceRecordLotti), 7).dataValue();
		if (dataInizioTime != null) {
			GregorianCalendar dataInizio = new GregorianCalendar();
			dataInizio.setTimeInMillis(dataInizioTime.getTime());
			UtilityExcelX.scriviCella(sheet, COL_DATA_INIZIO, indiceRigaCorrente, UtilityDate.convertiData(dataInizio.getTime(), UtilityDate.FORMATO_GG_MM_AAAA), dizStiliExcel.getStileExcel(DizionarioStiliExcelX.DATA_ALIGN_CENTER_FRAMED));
		} else {
			UtilityExcelX.scriviCella(sheet, COL_DATA_INIZIO, indiceRigaCorrente, "", dizStiliExcel.getStileExcel(DizionarioStiliExcelX.DATA_ALIGN_CENTER_FRAMED));
		}

		Timestamp dataUltimazioneTime = SqlManager.getValueFromVectorParam(datiAntiCorLotti.get(indiceRecordLotti), 8).dataValue();
		if (dataUltimazioneTime != null) {
			GregorianCalendar dataUltimazione = new GregorianCalendar();
			dataUltimazione.setTimeInMillis(dataUltimazioneTime.getTime());
			UtilityExcelX.scriviCella(sheet, COL_DATA_ULTIMAZIONE, indiceRigaCorrente, UtilityDate.convertiData(dataUltimazione.getTime(), UtilityDate.FORMATO_GG_MM_AAAA), dizStiliExcel.getStileExcel(DizionarioStiliExcelX.DATA_ALIGN_CENTER_FRAMED));
		} else {
			UtilityExcelX.scriviCella(sheet, COL_DATA_ULTIMAZIONE, indiceRigaCorrente, "", dizStiliExcel.getStileExcel(DizionarioStiliExcelX.DATA_ALIGN_CENTER_FRAMED));
		}

		Double importoSomme = SqlManager.getValueFromVectorParam(datiAntiCorLotti.get(indiceRecordLotti), 9).doubleValue();
		if (importoSomme != null) {
			//String importoSommeString = UtilityNumeri.convertiDouble(importoSomme);
			UtilityExcelX.scriviCella(sheet, COL_IMPORTO_SOMME_LIQUIDATE, indiceRigaCorrente, importoSomme, dizStiliExcel.getStileExcel(DizionarioStiliExcelX.DECIMALE2_ALIGN_CENTER_FRAMED));
		} else {
			UtilityExcelX.scriviCella(sheet, COL_IMPORTO_SOMME_LIQUIDATE, indiceRigaCorrente, "", dizStiliExcel.getStileExcel(DizionarioStiliExcelX.DECIMALE2_ALIGN_CENTER_FRAMED));
		}

		String codiceFiscaleResponsabile = null;
		JdbcParametro tmpJdbcParametro = SqlManager.getValueFromVectorParam(datiAntiCorLotti.get(indiceRecordLotti), 11);
		if (tmpJdbcParametro != null && tmpJdbcParametro.getValue() != null) {
			codiceFiscaleResponsabile = tmpJdbcParametro.getStringValue();
		}

		if (StringUtils.isNotEmpty(codiceFiscaleResponsabile)) {
			UtilityExcelX.scriviCella(sheet, COL_CODICE_FISCALE_RSPONSABILE, indiceRigaCorrente, codiceFiscaleResponsabile, dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		} else {
			UtilityExcelX.scriviCella(sheet, COL_CODICE_FISCALE_RSPONSABILE, indiceRigaCorrente, "", dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		}

		tmpJdbcParametro = null;
		String nomeRespesponsabile = null;
		tmpJdbcParametro = SqlManager.getValueFromVectorParam(datiAntiCorLotti.get(indiceRecordLotti), 12);
		if (tmpJdbcParametro != null && tmpJdbcParametro.getValue() != null) {
			nomeRespesponsabile = tmpJdbcParametro.getStringValue();
		}

		if (StringUtils.isNotEmpty(nomeRespesponsabile)) {
			UtilityExcelX.scriviCella(sheet, COL_NOME_COGNOME_RESPONSABILE, indiceRigaCorrente, nomeRespesponsabile, dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		} else {
			UtilityExcelX.scriviCella(sheet, COL_NOME_COGNOME_RESPONSABILE, indiceRigaCorrente, "", dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		}
	}

	private void fillDitteData(Sheet sheet, int indiceRigaCorrente, List<?> datiAntiCorPartecip, int indiceRecordPartecip,
					DizionarioStiliExcelX dizStiliExcel) throws GestoreException {

		String codFiscOpITA = SqlManager.getValueFromVectorParam(datiAntiCorPartecip.get(indiceRecordPartecip), 4).getStringValue();
		if (codFiscOpITA != null) {
			UtilityExcelX.scriviCella(sheet, COL_CODICE_FISCALE_OPE_ITA, indiceRigaCorrente, codFiscOpITA, dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		} else {
			UtilityExcelX.scriviCella(sheet, COL_CODICE_FISCALE_OPE_ITA, indiceRigaCorrente, "", dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		}

		String codFiscOpEST = SqlManager.getValueFromVectorParam(datiAntiCorPartecip.get(indiceRecordPartecip), 5).getStringValue();
		if (codFiscOpEST != null) {
			UtilityExcelX.scriviCella(sheet, COL_CODICE_FISCALE_OPE_ESTERO, indiceRigaCorrente, codFiscOpEST, dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		} else {
			UtilityExcelX.scriviCella(sheet, COL_CODICE_FISCALE_OPE_ESTERO, indiceRigaCorrente, "", dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		}

		String ragSocOperatore = SqlManager.getValueFromVectorParam(datiAntiCorPartecip.get(indiceRecordPartecip), 2).getStringValue();
		if (codFiscOpEST != null) {
			UtilityExcelX.scriviCella(sheet, COL_RAGIONE_SOCIALE_OPE, indiceRigaCorrente, ragSocOperatore, dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		} else {
			UtilityExcelX.scriviCella(sheet, COL_RAGIONE_SOCIALE_OPE, indiceRigaCorrente, "", dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		}

		String ruolo = SqlManager.getValueFromVectorParam(datiAntiCorPartecip.get(indiceRecordPartecip), 3).getStringValue();
		if (codFiscOpEST != null) {
			UtilityExcelX.scriviCella(sheet, COL_RUOLO_OPE, indiceRigaCorrente, ruolo, dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		} else {
			UtilityExcelX.scriviCella(sheet, COL_RUOLO_OPE, indiceRigaCorrente, "", dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		}
		CellRangeAddressList cralRuolo = new CellRangeAddressList(indiceRigaCorrente, indiceRigaCorrente, COL_RUOLO_OPE - 1, COL_RUOLO_OPE - 1);
		
		DataValidationHelper dvHelper = sheet.getDataValidationHelper();
	    DataValidationConstraint dvcRuolo = dvHelper.createFormulaListConstraint(DV_RUOLO);
	    DataValidation dvRuolo = dvHelper.createValidation(dvcRuolo, cralRuolo);
	    dvRuolo.setSuppressDropDownArrow(true);
	    dvRuolo.setShowErrorBox(true);
		dvRuolo.setEmptyCellAllowed(true);
		dvRuolo.setErrorStyle(DataValidation.ErrorStyle.STOP);
		dvRuolo.createErrorBox("Errore", "Valore di Ruolo non valido");
		sheet.addValidationData(dvRuolo);

		Long tipoImpresa = SqlManager.getValueFromVectorParam(datiAntiCorPartecip.get(indiceRecordPartecip), 6).longValue();
		String denGruppo = SqlManager.getValueFromVectorParam(datiAntiCorPartecip.get(indiceRecordPartecip), 1).getStringValue();
		if (denGruppo != null && tipoImpresa != null && tipoImpresa == ImportAdempimenti190Bean.RAGGRUPPAMENTO_IMPRESE) {
			UtilityExcelX.scriviCella(sheet, COL_DEN_RAGGRUPPAMENTO, indiceRigaCorrente, denGruppo, dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		} else {
			UtilityExcelX.scriviCella(sheet, COL_DEN_RAGGRUPPAMENTO, indiceRigaCorrente, "", dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		}

		String aggiudicatario = (SqlManager.getValueFromVectorParam(datiAntiCorPartecip.get(indiceRecordPartecip), 0)).stringValue();
		if (aggiudicatario != null) {
			UtilityExcelX.scriviCella(sheet, COL_AGGIUDICATARIO, indiceRigaCorrente, aggiudicatario.equals("1") ? "SI" : "NO", dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		} else {
			UtilityExcelX.scriviCella(sheet, COL_AGGIUDICATARIO, indiceRigaCorrente, "", dizStiliExcel.getStileExcel(DizionarioStiliExcelX.STRINGA_ALIGN_CENTER_FRAMED));
		}
		CellRangeAddressList cralAggiudicataria = new CellRangeAddressList(indiceRigaCorrente, indiceRigaCorrente, COL_AGGIUDICATARIO - 1, COL_AGGIUDICATARIO - 1);
		
        DataValidationConstraint dvcAggiudicataria = dvHelper.createExplicitListConstraint(new String[]{"SI", "NO"});
        DataValidation dvAggiudicataria = dvHelper.createValidation(dvcAggiudicataria, cralAggiudicataria);
        dvAggiudicataria.setSuppressDropDownArrow(true);
        dvAggiudicataria.setShowErrorBox(true);
		dvAggiudicataria.setEmptyCellAllowed(true);
		dvAggiudicataria.setErrorStyle(DataValidation.ErrorStyle.STOP);
		dvAggiudicataria.setShowErrorBox(true);
		dvAggiudicataria.createErrorBox("Errore", "Valore di Aggiudicataria non valido");
		sheet.addValidationData(dvAggiudicataria);
	}

	/**
	 * Importazione dati dal modello excel con formato XLS, XLSX, ODS, OTS
	 *
	 * @param importConfigBean bean che contiene tutti i parametri necessari alla
	 * configurazione dell'importazione
	 * @param resBundleGenerale il resource bundle del contesto
	 * @return resutlt un bean che contiente il numero di righe parsate, le righe
	 * corrette e le righe con errori
	 * @throws GestoreException eccezione sollevata dal manager
	 */
	public ImportAdempimenti190ResultBean importData(ImportAdempimenti190ConfigBean importConfigBean, ResourceBundle resBundleGenerale)
			throws GestoreException {

		this.getStazioneAppaltanteInfo(importConfigBean);

		Object sheet = null;
		String fileExtension = ExcelUtils.getFileExtension(importConfigBean.getFile().getFileName());
		InputStream fileStream = null;
		try {
			fileStream = importConfigBean.getFile().getInputStream();
			if (fileExtension.equals(ExcelUtils.TYPE_XLSX)) {
				Workbook xssfWb;
				xssfWb = new XSSFWorkbook(fileStream);
				sheet = xssfWb.getSheet(FOGLIO_MODELLO_ADEMPIMENTI_LEGGE_190);
			} else if (fileExtension.equals(ExcelUtils.TYPE_XLS)) {
				Workbook hssfWb;
				POIFSFileSystem fs = new POIFSFileSystem(fileStream);
				hssfWb = new HSSFWorkbook(fs);
				sheet = hssfWb.getSheet(FOGLIO_MODELLO_ADEMPIMENTI_LEGGE_190);
			} else if (fileExtension.equals(ExcelUtils.TYPE_OTS) || fileExtension.equals(ExcelUtils.TYPE_ODS)) {
				OdfSpreadsheetDocument ods = (OdfSpreadsheetDocument) OdfDocument.loadDocument(fileStream);
				sheet = ods.getTableByName(FOGLIO_MODELLO_ADEMPIMENTI_LEGGE_190);
			}
		} catch (Exception ex) {
			throw new GestoreException("Si è verificato un errore nell'apertura del documento Excel/OpenDocument", "importaesportaexcel.excelerroreio",
							new Object[]{importConfigBean.getFile().getFileName()}, ex);
		} finally {
			if (fileStream != null) {
				try {
					fileStream.close();
				} catch (IOException ex) {
					throw new GestoreException("Si è verificato un errore nella chiusura dello stream del file excel", "importaesportaexcel.errorchiusurastreamexcel",
									new Object[]{importConfigBean.getFile().getFileName()}, ex);
				}
			}
		}
		if (sheet == null) {
			throw new GestoreException("Non trovato il foglio " + FOGLIO_MODELLO_ADEMPIMENTI_LEGGE_190,
							"importaesportaexcel.foglionontrovato", new Object[]{FOGLIO_MODELLO_ADEMPIMENTI_LEGGE_190},
							null);
		}
		ImportAdempimenti190ResultBean result = this.processRows(sheet, importConfigBean, resBundleGenerale);
		return result;
	}

	private ImportAdempimenti190ResultBean processRows(Object sheet, ImportAdempimenti190ConfigBean importConfigBean,
			ResourceBundle resBundleGenerale) throws GestoreException {

		ImportAdempimenti190ResultBean result = new ImportAdempimenti190ResultBean();
		int numeroRigheProcessate = 0;
		int numeroRigaCorrente = START_ROW;
		HashMap<String, List<ImportAdempimenti190Bean>> lotti = new HashMap<String, List<ImportAdempimenti190Bean>>();
		Map<String, Integer> gruppiPartecipanti = new HashMap<String, Integer>();
		Set<String> partecipanti = new HashSet<String>();
		Set<String> cigSet = new HashSet<String>();
		Set<String> cigSetOutOfOrder = new HashSet<String>();
		String cigGruppoPrecedente = null;
		String gruppoPrecedente = null;
		String cigPrecedente = null;
		String cigGruppo = null;
		Vector<?> anticorlotto = null;
		ImportAdempimenti190Bean importBean = null;
		boolean emptyRow = false;

		for (int rowIndex = START_ROW - 1; rowIndex <= ExcelUtils.getNumberOfRows(sheet); rowIndex++) {

			Object row;
			String codFiscProp = null;
			String ragSocialeProp = null;
			Long anno = null;
			String cig = null;
			String cigTemporaneo = null;
			String oggettoLotto = null;
			String sceltaContraente = null;
			String codFiscOpITA = null;
			String codFiscOpEST = null;
			String ragSocOperatore = null;
			String gruppo = null;
			String ruolo = null;
			String aggiudicatario = null;
			Double importoAgg = null;
			Date dataInizio = null;
			Date dataUltimazione = null;
			Double importoSommeLiquidate = null;
			String codFiscResponsabileExcel = null;
			String nomeResponsabileExcel = null;
			List<String> erroriRiga = new ArrayList<String>();

			numeroRigheProcessate++;
			if (rowIndex != START_ROW - 1) {
				numeroRigaCorrente++;
			}

			row = ExcelUtils.getRow(sheet, rowIndex);
			anticorlotto = null;
			importBean = new ImportAdempimenti190Bean();

			if (row != null) {
				cig = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_CIG - 1)).toUpperCase();
				cigTemporaneo = new String(cig);
				if (StringUtils.isBlank(cig) || cig.length() != CIG_MIN_LENGHT) {
					erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.cignottenchars"));
				} else if (!"0000000000".equals(cig) && !this.pgManager.controlloCodiceCIG(cig)) {
					erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.cigNonValido"));
				} else {
					// Per importare piu' lotti con cig fittizzi si usa una copia del CIG
					if ("0000000000".equals(cigTemporaneo)) {
						String oggetto = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_OGGETTO_LOTTO - 1)).toUpperCase();
						//Ogni cig fittizio va visto come un nuovo lotto, quindi nella composizione del cigTemporaneo si è aggiunto il numero di riga per
						//renderlo unico
						cigTemporaneo = cigTemporaneo + oggetto + rowIndex;

					}
					if (!cigTemporaneo.equals(cigPrecedente) && cigSet.contains(cigTemporaneo)) {
						erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.recordcigfuoridalraggruppamento"));
						cigSetOutOfOrder.add(cigTemporaneo);
					} else if (cigSetOutOfOrder.contains(cigTemporaneo)) {
						erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.recordcigfuoridalraggruppamento"));
					} else if (!cigSet.contains(cigTemporaneo)) {
						cigSet.add(cigTemporaneo);
					}
				}

				if (StringUtils.isNotEmpty(cig)) {
					try {
						anticorlotto = this.sqlManager.getVector(SQL_GET_LOTTO_BY_CIG, new Object[] { cig, importConfigBean.getIdAnticor() } );
					} catch (SQLException ex) {
						throw new GestoreException("Errore inaspettato accorso durante il recupero informazioni sul lotto", "importaesportaexcel.erroreinaspettato", ex);
					}
				}

				String codFiscaleResponsabileDB = null;
				if (anticorlotto != null) {
					importBean.setIdLotto(((JdbcParametro) anticorlotto.get(0)).longValue());
					importBean.setLottoInbo(((JdbcParametro) anticorlotto.get(1)).stringValue());
					importBean.setDaAnnoPrec(((JdbcParametro) anticorlotto.get(2)).stringValue());
					JdbcParametro jdbcPar = ((JdbcParametro) anticorlotto.get(3));
					if (jdbcPar != null && jdbcPar.getValue() != null) {
						codFiscaleResponsabileDB = jdbcPar.getStringValue();
					}
				}

				codFiscResponsabileExcel = StringUtils.substring(ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex,
						COL_CODICE_FISCALE_RSPONSABILE - 1)), 0, 16).toUpperCase();
				nomeResponsabileExcel = StringUtils.substring(ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex,
						COL_NOME_COGNOME_RESPONSABILE - 1)), 0, 161);

				// Se utente non amministratore, allora il CF del responsabile indicato nel foglio Excel
				// deve coincidere con quelli dell'utente che sta eseguendo l'operazione di importazione
				if (!importConfigBean.isUtenteAmministratore()) {
					if (StringUtils.isNotEmpty(codFiscResponsabileExcel)) {
						if (!codFiscResponsabileExcel.equalsIgnoreCase(importConfigBean.getCodiceFiscaleResponsabile())) {
							erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.codFiscResponsabileNotSame1"));
						} else if (anticorlotto != null && !codFiscResponsabileExcel.equalsIgnoreCase(codFiscaleResponsabileDB)) {
							erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.codFiscResponsabileNotSame2"));
							importBean.setCodFiscResponsabile(null);
						} else {
							importBean.setCodFiscResponsabile(importConfigBean.getCodiceFiscaleResponsabile());
						}
					} else {
						erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.codFiscResponsabileNull"));
					}
				}

				//Se il lotto è dell'anno precedente, tale informazione deve essere scritta nel log
				if (importBean.getDaAnnoPrec() != null && importBean.getDaAnnoPrec().equals(ImportAdempimenti190Bean.ANNO_PRECEDENTE)) {
					erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.lottoannoprec"));
				}

				codFiscProp = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_CODICE_FISCALE_PROP - 1));

				if (StringUtils.isBlank(codFiscProp)) {
					erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.codfiscstazappnull"));
				}

				if (!importConfigBean.getIdStazioneAppaltante().equals(ImportAdempimenti190ConfigBean.NO_GESTIONE_UFFICI_INTESTATARI)
                                && codFiscProp!=null && !codFiscProp.equals(importConfigBean.getCodiceFiscaleStazioneAppaltante())) {
                    erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.codfiscstazappnotsame"));
                }

				ragSocialeProp = StringUtils.substring(ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_RAGIONE_SOCIALE_PROP - 1)), 0, 250);

				Number annoNumber = ExcelUtils.readNumberFromCell(sheet, rowIndex, COL_ANNO_RIF - 1, 0);
				if (annoNumber != null) {
					anno = annoNumber.longValue();
				}
				if (anno == null || !anno.toString().equals(importConfigBean.getAnnoAdempimento())) {
					erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.annonotequalannorif"));
				}

				oggettoLotto = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_OGGETTO_LOTTO - 1));
				sceltaContraente = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_PROCEDURA_SCELTA_CONTR - 1));
				codFiscOpITA = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_CODICE_FISCALE_OPE_ITA - 1));
				codFiscOpEST = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_CODICE_FISCALE_OPE_ESTERO - 1));
				ragSocOperatore = StringUtils.substring(ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_RAGIONE_SOCIALE_OPE - 1)), 0, 250);
				ruolo = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_RUOLO_OPE - 1));
				aggiudicatario = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_AGGIUDICATARIO - 1));
				gruppo = StringUtils.substring(ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_DEN_RAGGRUPPAMENTO - 1)), 0, 250);

				if (StringUtils.isNotBlank(ragSocOperatore) || StringUtils.isNotBlank(ruolo) || StringUtils.isNotBlank(aggiudicatario)
				    || StringUtils.isNotBlank(gruppo)) {
    				if (((StringUtils.isBlank(codFiscOpITA) && StringUtils.isBlank(codFiscOpEST))
    								|| (StringUtils.isNotBlank(codFiscOpITA) && StringUtils.isNotBlank(codFiscOpEST)))
    								&& (StringUtils.isBlank(importBean.getLottoInbo())
    								|| StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE)
    								|| (StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_IN_BACKOFFICE)
    								&& importConfigBean.isAggiornaPartecipante()))) {
    					erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.notaxcode"));
    				}
				}

				ragSocOperatore = StringUtils.substring(ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_RAGIONE_SOCIALE_OPE - 1)), 0, 250);
				String cigConfronto = new String(cig + ragSocOperatore);
				if("0000000000".equals(cig))
				  cigConfronto += rowIndex;
				if (StringUtils.isNotBlank(cig) & StringUtils.isNotBlank(ragSocOperatore) && !partecipanti.contains(cigConfronto)) {
					partecipanti.add(cigConfronto);
				} else if (StringUtils.isNotBlank(cig) & StringUtils.isNotBlank(ragSocOperatore) && partecipanti.contains(cigConfronto)
								&& (StringUtils.isBlank(importBean.getLottoInbo())
								|| StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE)
								|| (StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_IN_BACKOFFICE)
								&& importConfigBean.isAggiornaPartecipante()))) {
					erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.partecipantegiaassegnatoalmedesimolotto")
									.replace("{0}", ragSocOperatore).replace("{1}", cig));
				}

				if (StringUtils.isNotBlank(gruppo) && StringUtils.isNotBlank(cig) && !gruppiPartecipanti.containsKey(cigTemporaneo + gruppo)) {
					gruppiPartecipanti.put(cigTemporaneo + gruppo, 1);
				} else if (StringUtils.isNotBlank(gruppo) && StringUtils.isNotBlank(cig)
								&& gruppiPartecipanti.containsKey(cigTemporaneo + gruppo)
								&& (!(cigTemporaneo + gruppo).equals(cigGruppoPrecedente) && !cigSetOutOfOrder.contains(cigTemporaneo))
								&& (StringUtils.isBlank(importBean.getLottoInbo())
								|| StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE)
								|| (StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_IN_BACKOFFICE)
								&& importConfigBean.isAggiornaPartecipante()))) {
					erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.recordgruppofuoridalraggruppamento"));
				} else if (StringUtils.isNotBlank(gruppo) && StringUtils.isNotBlank(cigTemporaneo)
								&& gruppiPartecipanti.containsKey(cigTemporaneo + gruppo) && (cigTemporaneo + gruppo).equals(cigGruppoPrecedente)) {
					gruppiPartecipanti.put(cigTemporaneo + gruppo, gruppiPartecipanti.get(cigTemporaneo + gruppo) + 1);
				}


				if ((StringUtils.isNotBlank(gruppo) && StringUtils.isBlank(ruolo))
								|| (StringUtils.isBlank(gruppo) && StringUtils.isNotBlank(ruolo))
								&& (StringUtils.isBlank(importBean.getLottoInbo())
								|| StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE)
								|| (StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_IN_BACKOFFICE)
								&& importConfigBean.isAggiornaPartecipante()))) {
					erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.dengrupporuoloerror"));
				}

				aggiudicatario = StringUtils.isNotBlank(aggiudicatario) && aggiudicatario.toUpperCase().equals("SI")
								? ImportAdempimenti190Bean.AGGIUDICATARIA : ImportAdempimenti190Bean.NON_AGGIUDICATARIA;

				try {
					Number importoAggiudic = ExcelUtils.readNumberFromCell(sheet, rowIndex, COL_IMPORTO_AGGIUDICAZIONE - 1, 2);
					if (importoAggiudic != null) {
					  importoAggiudic = UtilityNumeri.arrotondaNumero(importoAggiudic,2);
					  importoAgg = importoAggiudic.doubleValue();
					}
				} catch (GestoreException ex) {
					if (StringUtils.isBlank(importBean.getLottoInbo())
									|| StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE)
									|| (StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_IN_BACKOFFICE)
									&& importConfigBean.isAggiornaImpAggiudicazione())) {
						erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*." + ex.getCodice()).replace("{0}", "IMPORTO AGGIUDICAZIONE"));
					}
				}

				try {
				  dataInizio = ExcelUtils.readDateFromCell(sheet, rowIndex, COL_DATA_INIZIO - 1);
				} catch (GestoreException ex) {
					if (StringUtils.isBlank(importBean.getLottoInbo())
									|| StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE)
									|| (StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_IN_BACKOFFICE)
									&& importConfigBean.isAggiornaDataInizio())) {
						erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*." + ex.getCodice()).replace("{0}", "DATA INIZIO"));
					}
				}

				try {
					dataUltimazione = ExcelUtils.readDateFromCell(sheet, rowIndex, COL_DATA_ULTIMAZIONE - 1);
				} catch (GestoreException ex) {
					if (StringUtils.isBlank(importBean.getLottoInbo())
									|| StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE)
									|| (StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_IN_BACKOFFICE)
									&& importConfigBean.isAggiornaDataUltimazione())) {
						erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*." + ex.getCodice()).replace("{0}", "DATA ULTIMAZIONE"));
					}
				}

				try {
					Number importoSommeLiq = ExcelUtils.readNumberFromCell(sheet, rowIndex, COL_IMPORTO_SOMME_LIQUIDATE - 1, 2);
					if (importoSommeLiq != null) {
					  importoSommeLiq = UtilityNumeri.arrotondaNumero(importoSommeLiq,2);
					  importoSommeLiquidate = importoSommeLiq.doubleValue();
					}
				} catch (GestoreException ex) {
					if (StringUtils.isBlank(importBean.getLottoInbo())
									|| StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE)
									|| (StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_IN_BACKOFFICE)
									&& importConfigBean.isAggiornaImpSommeLiquidate())) {
						erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*." + ex.getCodice()).replace("{0}", "IMPORTO SOMME_LIQUIDATE"));
					}
				}

				if (StringUtils.isBlank(codFiscProp)
						&& StringUtils.isBlank(ragSocialeProp)
						&& anno == null
						&& StringUtils.isBlank(cig)
						&& StringUtils.isBlank(oggettoLotto)
						&& StringUtils.isBlank(sceltaContraente)
						&& StringUtils.isBlank(codFiscOpITA)
						&& StringUtils.isBlank(codFiscOpEST)
						&& StringUtils.isBlank(ragSocOperatore)
						&& StringUtils.isBlank(gruppo)
						&& StringUtils.isBlank(ruolo)
						&& importoAgg == null
						&& dataInizio == null
						&& dataUltimazione == null
						&& importoSommeLiquidate == null
						&& (importConfigBean.isUtenteAmministratore() || (!importConfigBean.isUtenteAmministratore()
								&& StringUtils.isBlank(codFiscResponsabileExcel)))
					) {
						emptyRow = true;
				}
			} else {
				emptyRow = true;
			}

			if (StringUtils.equals(cig, cigTemporaneo)) {
				cigGruppo = (cig != null ? cig : "") + (gruppo != null ? gruppo : "");
			} else {
				cigGruppo = (cigTemporaneo != null ? cigTemporaneo : "") + (gruppo != null ? gruppo : "");
			}

			if (StringUtils.isBlank(importBean.getLottoInbo())
					|| StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE)
					|| (StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_IN_BACKOFFICE)
					&& importConfigBean.isAggiornaPartecipante())) {
				this.checkNumPartecipantiPerLotto(numeroRigaCorrente, cigGruppo, cigPrecedente, gruppoPrecedente, cigGruppoPrecedente,
						resBundleGenerale, gruppiPartecipanti, lotti, result);
			}

			if (emptyRow) {
				break;
			}

			result.setNumRigheTotaliAnalizzate(numeroRigheProcessate);

			if (!erroriRiga.isEmpty()) {
				result.getRigheConErrore().put(numeroRigaCorrente, erroriRiga);
			} else {

				importBean.setIndiceRiga(numeroRigaCorrente);
				importBean.setAggiudicatario(aggiudicatario);
				importBean.setAnno(anno);
				importBean.setCig(cigTemporaneo);
				importBean.setCodFiscOpEST(codFiscOpEST);
				importBean.setCodFiscOpITA(codFiscOpITA);
				importBean.setCodFiscProp(codFiscProp);
				importBean.setDataInizio(dataInizio);
				importBean.setDataUltimazione(dataUltimazione);
				importBean.setDenGruppo(gruppo);
				importBean.setImportoAgg(importoAgg);
				importBean.setImportoSommeLiquidate(importoSommeLiquidate);
				importBean.setOggettoLotto(oggettoLotto);
				importBean.setRagSocOperatore(ragSocOperatore);
				importBean.setRagSocialeProp(ragSocialeProp);
				importBean.setRuolo(ruolo);
				importBean.setSceltaContraente(sceltaContraente);
				importBean.setStato(IN_CORSO);
				importBean.setCodFiscResponsabile(codFiscResponsabileExcel);
				importBean.setNomeResponsabile(nomeResponsabileExcel);

				if (StringUtils.equals(cigTemporaneo, cigPrecedente) && lotti.containsKey(cigTemporaneo)) {
					lotti.get(cigTemporaneo).add(importBean);
				} else if (lotti.containsKey(cigTemporaneo)) {
					erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.rowoutoforder"));
					result.getRigheConErrore().put(numeroRigaCorrente, erroriRiga);
				} else {
					List<ImportAdempimenti190Bean> righeLotto = new ArrayList<ImportAdempimenti190Bean>();
					righeLotto.add(importBean);
					lotti.put(cigTemporaneo, righeLotto);
				}
			}
			cigGruppoPrecedente = cigGruppo;
			gruppoPrecedente = gruppo;
			if (StringUtils.equals(cig, cigTemporaneo)) {
				cigPrecedente = cig;
			} else {
				cigPrecedente = cigTemporaneo;
			}
		}
		if (result.getNumRigheTotaliAnalizzate() >= numeroRigheProcessate
						&& (importBean != null && (StringUtils.isBlank(importBean.getLottoInbo())
						|| StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE))
						|| (importBean != null && StringUtils.equals(importBean.getLottoInbo(), ImportAdempimenti190Bean.LOTTO_IN_BACKOFFICE)
						&& importConfigBean.isAggiornaPartecipante()))) {
			//Aggiungo un ultima volta il controllo per evitare di perdermi un possibile errore all'ultima riga del file e questa non è nulla, ma ha campi vuoti
			this.checkNumPartecipantiPerLotto(numeroRigaCorrente + 1, null, cigPrecedente, gruppoPrecedente, cigGruppoPrecedente,
							resBundleGenerale, gruppiPartecipanti, lotti, result);
		}
		this.processLotti(lotti, importConfigBean, result, resBundleGenerale);
		result.setNumRigheErrore(result.getRigheConErrore() != null ? result.getRigheConErrore().values().size() : 0);
		return result;
	}

	private void getStazioneAppaltanteInfo(ImportAdempimenti190ConfigBean importConfigBean) throws GestoreException {

		try {
			String codein = ((JdbcParametro) sqlManager.getVector(SQL_GET_ANTICOR_CODEIN,
					new Object[]{ importConfigBean.getIdAnticor() }).get(0)).getStringValue();
			importConfigBean.setIdStazioneAppaltante(codein);

			if (!StringUtils.equals(codein, ImportAdempimenti190ConfigBean.NO_GESTIONE_UFFICI_INTESTATARI)) {
				String codiceFiscaleStazioneAppaltante = ((JdbcParametro) sqlManager.getVector(SQL_GET_CF_STAZAPP,
						new Object[]{codein}).get(0)).getStringValue();
				importConfigBean.setCodiceFiscaleStazioneAppaltante(codiceFiscaleStazioneAppaltante);

				if (!StringUtils.equals(codein, ImportAdempimenti190ConfigBean.NO_GESTIONE_UFFICI_INTESTATARI)
								&& StringUtils.isBlank(importConfigBean.getCodiceFiscaleStazioneAppaltante())) {
					throw new GestoreException("Importazione bloccata, impostare il codice fiscale proponente nella corrispondente occorrenza nell’archivio",
									"label.tags.uffint.multiplo");
				}
			}
		} catch (SQLException ex) {
			throw new GestoreException("Impossibile ricavare la stazione appaltante collegata all'adempimento", "legge190.cannotretrievecodein",
							new Object[]{importConfigBean.getIdAnticor()}, ex);
		}
	}

	private void processLotti(Map<String, List<ImportAdempimenti190Bean>> lotti,
					ImportAdempimenti190ConfigBean configBean, ImportAdempimenti190ResultBean result, ResourceBundle resBundleGenerale) throws GestoreException {

		String errorAnticorLottoUpdate = resBundleGenerale.getString("errors.gestoreException.*.legge190.cannotupdateanticorlotti");
		String errorAnticorLottoInsert = resBundleGenerale.getString("errors.gestoreException.*.legge190.cannotinsertanticorlotti");
		String errorAnticorPartecipUpdate = resBundleGenerale.getString("errors.gestoreException.*.legge190.cannotupdatepartecip");
		String errorAnticorDitteUpdate = resBundleGenerale.getString("errors.gestoreException.*.legge190.cannotupdateditte");
		String errorAnticorPartecipDelete = resBundleGenerale.getString("errors.gestoreException.*.legge190.cannotdeleteanticorpartecip");
		String erroreInaspettato = resBundleGenerale.getString("errors.gestoreException.*.importaesportaexcel.erroreinaspettato");
		String erroreControlliLotto = resBundleGenerale.getString("errors.gestoreException.*.legge190.errorecontrolli");

		for (String cigLotto : lotti.keySet()) {

			TransactionStatus status;
			boolean doCommit = false;
			String gruppoPrecedente = null;
			boolean aggiornaLotto = true;
			boolean rigaOk = false;
			Long idPartecipante = null;
			Long anticorLottoId = null;
			String lottoInbo = null;
			String daAnnoPrec = null;
			boolean isAggiudicato = false;

			try {
				status = this.sqlManager.startTransaction();
			} catch (SQLException ex) {
				throw new GestoreException("Errore inaspettato accorso durante la procedura di import/export excel", "importaesportaexcel.erroreinaspettato", ex);
			}

			ListIterator<ImportAdempimenti190Bean> it = lotti.get(cigLotto).listIterator();

			try {

				while (it.hasNext()) {
					rigaOk = false;
					ImportAdempimenti190Bean recordAdempimento = it.next();

					if (aggiornaLotto) { //Aggiorno o inserisco il lotto solo quando processo la prima riga del gruppo con stesso cig
						aggiornaLotto = false;
						anticorLottoId = recordAdempimento.getIdLotto();
						lottoInbo = recordAdempimento.getLottoInbo();
						daAnnoPrec = recordAdempimento.getDaAnnoPrec();

						if (anticorLottoId != null) {
							try {
								this.updateAnticorLotto(recordAdempimento, configBean);
							} catch (Exception ex) {
								this.gestisciErroreRiga(errorAnticorLottoUpdate, recordAdempimento, result, lotti, resBundleGenerale, ex);
								break;
							}

							if ((lottoInbo.equals(ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE)
		                        && (daAnnoPrec.equals(ImportAdempimenti190Bean.ANNO_CORRENTE)
		                            ||  daAnnoPrec.equals(ImportAdempimenti190Bean.ANNO_PRECEDENTE_RIPORTATO_ANNO_CORRENTE)))
		                            || (lottoInbo.equals(ImportAdempimenti190Bean.LOTTO_IN_BACKOFFICE)
		                            && (daAnnoPrec.equals(ImportAdempimenti190Bean.ANNO_CORRENTE)
		                            ||  daAnnoPrec.equals(ImportAdempimenti190Bean.ANNO_PRECEDENTE_RIPORTATO_ANNO_CORRENTE))
		                            && configBean.isAggiornaPartecipante())) {
								try {
									this.deleteAnticorPartecipAndDitte(anticorLottoId, lottoInbo, daAnnoPrec, configBean);
								} catch (Exception ex) {
									this.gestisciErroreRiga(errorAnticorPartecipDelete, recordAdempimento, result, lotti, resBundleGenerale, ex);
									break;
								}
							}
						} else {
							try {
								anticorLottoId = this.insertAnticorLotto(recordAdempimento, configBean);
								lottoInbo = ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE;
								daAnnoPrec = ImportAdempimenti190Bean.ANNO_CORRENTE;
							} catch (Exception ex) {
								this.gestisciErroreRiga(errorAnticorLottoInsert, recordAdempimento, result, lotti, resBundleGenerale, ex);
								break;
							}
						}
					}

					if ((lottoInbo.equals(ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE)
                        && (daAnnoPrec.equals(ImportAdempimenti190Bean.ANNO_CORRENTE)
                            ||  daAnnoPrec.equals(ImportAdempimenti190Bean.ANNO_PRECEDENTE_RIPORTATO_ANNO_CORRENTE)))
                            || (lottoInbo.equals(ImportAdempimenti190Bean.LOTTO_IN_BACKOFFICE)
                            && (daAnnoPrec.equals(ImportAdempimenti190Bean.ANNO_CORRENTE)
                            ||  daAnnoPrec.equals(ImportAdempimenti190Bean.ANNO_PRECEDENTE_RIPORTATO_ANNO_CORRENTE))
                            && configBean.isAggiornaPartecipante())) {

						if(!(StringUtils.isBlank(recordAdempimento.getRagSocOperatore())
						    && StringUtils.isBlank(recordAdempimento.getCodFiscOpITA())
                            && StringUtils.isBlank(recordAdempimento.getCodFiscOpEST()))){
    					    try {
    							if (StringUtils.isNotBlank(recordAdempimento.getDenGruppo()) && !recordAdempimento.getDenGruppo().equals(gruppoPrecedente)) {
    								idPartecipante = this.insertAnticorPartecipante(anticorLottoId, recordAdempimento, true);
    							} else if (StringUtils.isBlank(recordAdempimento.getDenGruppo())) {
    								idPartecipante = this.insertAnticorPartecipante(anticorLottoId, recordAdempimento, false);
    							}
    							if (StringUtils.equals(recordAdempimento.getAggiudicatario(), ImportAdempimenti190Bean.AGGIUDICATARIA) && !isAggiudicato) {
    								isAggiudicato = true;
    							}
    						} catch (Exception ex) {
    							this.gestisciErroreRiga(errorAnticorPartecipUpdate, recordAdempimento, result, lotti, resBundleGenerale, ex);
    							break;
    						}
    						gruppoPrecedente = recordAdempimento.getDenGruppo();

    						try {
    							this.insertAnticorDitta(idPartecipante, recordAdempimento);
    						} catch (Exception ex) {
    							gestisciErroreRiga(errorAnticorDitteUpdate, recordAdempimento, result, lotti, resBundleGenerale, ex);
    							break;
    						}
						}
						if (isAggiudicato) {
	                        try {
	                            this.setAnticorLottoAggiudicato(anticorLottoId);
	                            isAggiudicato = false;
	                        } catch (Exception ex) {
	                            this.gestisciErroreRiga(errorAnticorLottoUpdate, recordAdempimento, result, lotti, resBundleGenerale, ex);
	                            break;
	                        }
	                    }

						try {
							this.controlliAVCP(anticorLottoId, configBean.getUfficioIntestatario());
						} catch (Exception ex) {
							this.gestisciErroreRiga(erroreControlliLotto, recordAdempimento, result, lotti, resBundleGenerale, ex);
							break;
						}
					}

					rigaOk = this.gestisciSuccessoRiga(recordAdempimento, result);
					if (!rigaOk) {
						this.gestisciErroreRiga(erroreInaspettato, recordAdempimento, result, lotti, resBundleGenerale, null);
					}

				}
				doCommit = rigaOk;
			} catch (Exception ex) {
				throw new GestoreException("Errore inaspettato accorso durante la procedura di import/export excel", "importaesportaexcel.erroreinaspettato", ex);
			} finally {
				if (status != null) {
					try {
						if (doCommit) {
							this.sqlManager.commitTransaction(status);
							this.calcolaRigheConSuccesso(cigLotto, result);
						} else {
							this.sqlManager.rollbackTransaction(status);
						}
					} catch (SQLException ex) {
						throw new GestoreException("Errore inaspettato accorso durante la procedura di import/export excel", "importaesportaexcel.erroreinaspettato", ex);
					}
				}
			}
		}
	}

	private void updateAnticorLotto(ImportAdempimenti190Bean recordAdempimento,
					ImportAdempimenti190ConfigBean configBean) throws GestoreException, SQLException {

		Long sceltaContraenteId = null;
		StringBuilder sb = new StringBuilder();
		List<Object> paramsList = new ArrayList<Object>();
		paramsList.add(recordAdempimento.getIdLotto());


		if ((recordAdempimento.getLottoInbo().equals(ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE)
		    && (recordAdempimento.getDaAnnoPrec().equals(ImportAdempimenti190Bean.ANNO_CORRENTE)
            || recordAdempimento.getDaAnnoPrec().equals(ImportAdempimenti190Bean.ANNO_PRECEDENTE_RIPORTATO_ANNO_CORRENTE)))
			|| (recordAdempimento.getLottoInbo().equals(ImportAdempimenti190Bean.LOTTO_IN_BACKOFFICE)
			&& (recordAdempimento.getDaAnnoPrec().equals(ImportAdempimenti190Bean.ANNO_CORRENTE)
			|| recordAdempimento.getDaAnnoPrec().equals(ImportAdempimenti190Bean.ANNO_PRECEDENTE_RIPORTATO_ANNO_CORRENTE))
			&& configBean.isAggiorna())) {

			if (recordAdempimento.getLottoInbo().equals(ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE)) {

				if (StringUtils.isNotBlank(recordAdempimento.getSceltaContraente())) {
					sceltaContraenteId = (Long) sqlManager.getObject(SQL_GET_SCELTA_O_RUOLO, new Object[]{recordAdempimento.getSceltaContraente().toUpperCase(), CODICE_SCELTA});
				}
				paramsList.add(recordAdempimento.getCodFiscProp());
				paramsList.add(recordAdempimento.getRagSocialeProp());
				paramsList.add(recordAdempimento.getOggettoLotto());
				paramsList.add(sceltaContraenteId);
				paramsList.add(recordAdempimento.getStato());

				sb.append("UPDATE anticorlotti SET id = ?, codfiscprop = ?, denomprop = ?, oggetto = ?, sceltacontr = ?, stato = ? ");
			} else if (recordAdempimento.getLottoInbo().equals(ImportAdempimenti190Bean.LOTTO_IN_BACKOFFICE) && configBean.isAggiorna()) {
				sb.append("UPDATE anticorlotti SET id = ? ");
			}

			if (recordAdempimento.getLottoInbo().equals(ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE) || configBean.isAggiornaImpAggiudicazione()){
            	sb.append(", impaggiudic = ? ");
				paramsList.add(recordAdempimento.getImportoAgg());
			}
			if (recordAdempimento.getLottoInbo().equals(ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE) || configBean.isAggiornaDataInizio()) {
				sb.append(", datainizio = ? ");
				paramsList.add(recordAdempimento.getDataInizio());
			}
			if (recordAdempimento.getLottoInbo().equals(ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE) || configBean.isAggiornaDataUltimazione()) {
				sb.append(", dataultimazione = ? ");
				paramsList.add(recordAdempimento.getDataUltimazione());
			}
			if (recordAdempimento.getLottoInbo().equals(ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE) || configBean.isAggiornaImpSommeLiquidate()) {
				sb.append(", impsommeliq = ? ");
				paramsList.add(recordAdempimento.getImportoSommeLiquidate());
			}

			paramsList.add(recordAdempimento.getCig());
			paramsList.add(recordAdempimento.getIdLotto());
			sb.append("WHERE cig = ? AND id = ? ");

			this.sqlManager.update(sb.toString(), paramsList.toArray());

			//Aggiornamento della gara
			if(recordAdempimento.getLottoInbo().equals(ImportAdempimenti190Bean.LOTTO_IN_BACKOFFICE) && (configBean.isAggiornaDataInizio() ||
			    configBean.isAggiornaDataUltimazione() ||configBean.isAggiornaImpSommeLiquidate() )){
			  String ngara=(String)this.sqlManager.getObject("select idlotto from anticorlotti where id=?", new Object[]{recordAdempimento.getIdLotto()});
			  Vector<?> datiTorn = this.sqlManager.getVector("select codgar, clavor, numera, accqua, altrisog, modcont from torn,gare where ngara=? and codgar=codgar1", new Object[]{ngara});
			  if(datiTorn!=null && datiTorn.size()>0){
			    String codgar=SqlManager.getValueFromVectorParam(datiTorn, 0).stringValue();
			    String clavor=SqlManager.getValueFromVectorParam(datiTorn, 1).stringValue();
			    Long numera=SqlManager.getValueFromVectorParam(datiTorn, 2).longValue();
			    String accqua=SqlManager.getValueFromVectorParam(datiTorn, 3).stringValue();
			    Long altrisog=SqlManager.getValueFromVectorParam(datiTorn, 4).longValue();
			    Long modcont=SqlManager.getValueFromVectorParam(datiTorn, 5).longValue();
			    Long genere=(Long)this.sqlManager.getObject("select genere from gare where ngara=?", new Object[]{codgar});
			    boolean aggiornare= true;
			    if(clavor!=null && !"".equals(clavor) && numera!=null && !("1".equals(accqua) || new Long(2).equals(altrisog) || new Long(3).equals(altrisog)))
			      aggiornare=false;
			    if(new Long(3).equals(genere) && new Long(2).equals(modcont))
			      aggiornare=false;
			    if(aggiornare){
			      sb = new StringBuilder();
			      paramsList = new ArrayList<Object>();
			      sb.append("update garecont set");
			      if (configBean.isAggiornaDataInizio()) {
	                sb.append(" dverbc = ? ");
	                if(configBean.isAggiornaDataUltimazione() || configBean.isAggiornaImpSommeLiquidate())
	                  sb.append(",");
	                paramsList.add(recordAdempimento.getDataInizio());
	              }
			      if (configBean.isAggiornaDataUltimazione()) {
                    sb.append(" dcertu = ? ");
                    if( configBean.isAggiornaImpSommeLiquidate())
                      sb.append(",");
                    paramsList.add(recordAdempimento.getDataUltimazione());
                  }
			      if (configBean.isAggiornaImpSommeLiquidate()) {
                    sb.append(" impliq = ? ");
                    paramsList.add(recordAdempimento.getImportoSommeLiquidate());
                  }
			      sb.append("WHERE ngara = ? or ngaral=? ");
			      paramsList.add(ngara);
		          paramsList.add(ngara);
		          this.sqlManager.update(sb.toString(), paramsList.toArray());
			    }
			  }

			}
		}
	}

	private Long insertAnticorLotto(ImportAdempimenti190Bean recordAdempimento, ImportAdempimenti190ConfigBean configBean)
					throws GestoreException, SQLException {

		Long sceltaContraenteId = null;
		Long anticorlottoId;

		if (StringUtils.isNotBlank(recordAdempimento.getSceltaContraente())) {
			sceltaContraenteId = (Long) this.sqlManager.getObject(SQL_GET_SCELTA_O_RUOLO, new Object[]{recordAdempimento.getSceltaContraente().toUpperCase(), CODICE_SCELTA});
		}

		anticorlottoId = new Long(this.genChiaviManager.getNextId("ANTICORLOTTI"));

		Object[] params = new Object[]{anticorlottoId, configBean.getIdAnticor(), ImportAdempimenti190Bean.ANNO_CORRENTE, recordAdempimento.getCig(),
			recordAdempimento.getCodFiscProp(), recordAdempimento.getRagSocialeProp(), recordAdempimento.getOggettoLotto(), sceltaContraenteId,
			recordAdempimento.getImportoAgg(), recordAdempimento.getDataInizio(), recordAdempimento.getDataUltimazione(),
			recordAdempimento.getImportoSommeLiquidate(), recordAdempimento.getStato(), ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE, 2, 2,
			recordAdempimento.getCodFiscResponsabile(), recordAdempimento.getNomeResponsabile()};

		if (StringUtils.indexOf(recordAdempimento.getCig(), "0000000000") == 0) {
			int id = this.genChiaviManager.getNextId("GARE.CODCIG");
			params[3] = "#".concat(StringUtils.leftPad("" + id, 9, "0"));
		}

		this.sqlManager.update(SQL_INSERT_LOTTI, params);

		return anticorlottoId;
	}

	private void deleteAnticorPartecipAndDitte(Long anticorLottoId, String lottoInbo, String daAnnoPrec, ImportAdempimenti190ConfigBean configBean)
					throws GestoreException, SQLException {

		this.sqlManager.update(SQL_DELETE_PARTECIP, new Object[]{anticorLottoId});

	}

	private Long insertAnticorPartecipante(Long anticorLottoId, ImportAdempimenti190Bean recordAdempimento, boolean isRappruppamento)
					throws GestoreException, SQLException {

		long tipologiaImpresa;
		tipologiaImpresa = isRappruppamento ? ImportAdempimenti190Bean.RAGGRUPPAMENTO_IMPRESE : ImportAdempimenti190Bean.IMPRESA_SINGOLA;

		Long partecipanteId = new Long(this.genChiaviManager.getNextId("ANTICORPARTECIP"));

		Object[] params = new Object[]{partecipanteId, anticorLottoId, tipologiaImpresa, recordAdempimento.getAggiudicatario(),
			tipologiaImpresa == ImportAdempimenti190Bean.RAGGRUPPAMENTO_IMPRESE ? recordAdempimento.getDenGruppo() : recordAdempimento.getRagSocOperatore()};

		this.sqlManager.update(SQL_INSERT_PARTECIP, params);

		return partecipanteId;
	}

	private void insertAnticorDitta(Long partecipanteId, ImportAdempimenti190Bean recordAdempimento) throws GestoreException, SQLException {

		Long ruoloId = null;
		if (StringUtils.isNotBlank(recordAdempimento.getRuolo())) {
			ruoloId = (Long) this.sqlManager.getObject(SQL_GET_SCELTA_O_RUOLO, new Object[]{recordAdempimento.getRuolo().toUpperCase(), CODICE_RUOLO});
		}

		Long dittaId = new Long(this.genChiaviManager.getNextId("ANTICORDITTE"));

		Object[] params = new Object[]{dittaId, partecipanteId, recordAdempimento.getRagSocOperatore(), recordAdempimento.getCodFiscOpITA(),
			recordAdempimento.getCodFiscOpEST(), ruoloId};

		this.sqlManager.update(SQL_INSERT_DITTE, params);
	}

	private void controlliAVCP(Long anticorLottoId, String ufficioIntestatario) throws GestoreException, SQLException {

		Map<String,Object> esitoControlli = this.pgManager.controlloDatiAVCP(anticorLottoId, true, false, ufficioIntestatario);
		String inviabile = ImportAdempimenti190Bean.NON_INVIABILE;
		String pubblica = ImportAdempimenti190Bean.NON_PUBBLICABILE;
		 String msg = null;
		if (esitoControlli != null) {
			Boolean controlloOk = (Boolean) esitoControlli.get("esito");
			if (controlloOk.booleanValue()) {
				inviabile = ImportAdempimenti190Bean.INVIABILE;
				pubblica = ImportAdempimenti190Bean.PUBBLICABILE;
			} else {
			  msg = (String)esitoControlli.get("msg");
			}
		}
		this.sqlManager.update(SQL_UPDATE_CONTROLLI_FIELDS, new Object[]{pubblica, inviabile, msg, anticorLottoId});
	}

	private void setAnticorLottoAggiudicato(Long anticorLottoId) throws GestoreException, SQLException {
		this.sqlManager.update(SQL_SET_ANTICORLOTTO_AGGIUDICATO, new Object[]{AGGIUDICATO, anticorLottoId});
	}

	private void gestisciErroreRiga(String errore, ImportAdempimenti190Bean recordAdempimentoConErrore, ImportAdempimenti190ResultBean result,
			Map<String, List<ImportAdempimenti190Bean>> lotti, ResourceBundle resBundleGenerale, Exception ex) {

		// Traccio l'errore su log per eventuale debuggin
		if (ex != null) {
			logger.error(ex.getMessage(), ex);
			if (ex.getCause() != null) {
				logger.error(ex.getCause().getMessage(), ex);
			}
		}

		String erroreRigaesclusapererrorilotto = resBundleGenerale.getString("errors.gestoreException.*.legge190.rigaesclusapererrorilotto");

		if (result.getLottiConSuccesso().containsKey(recordAdempimentoConErrore.getCig())) {
			result.getLottiConSuccesso().remove(recordAdempimentoConErrore.getCig());
		}
		//devo inserire per ogni record che afferisce al lotto, un errore
		for (ImportAdempimenti190Bean adempimentoDaScartare : lotti.get(recordAdempimentoConErrore.getCig())) {
			if (adempimentoDaScartare.getIndiceRiga() != recordAdempimentoConErrore.getIndiceRiga()) {
				if (result.getRigheConErrore().containsKey(adempimentoDaScartare.getIndiceRiga())) {
					result.getRigheConErrore().get(adempimentoDaScartare.getIndiceRiga()).add(erroreRigaesclusapererrorilotto);
				} else {
					List<String> erroreRiga = new ArrayList<String>();
					erroreRiga.add(erroreRigaesclusapererrorilotto);
					result.getRigheConErrore().put(adempimentoDaScartare.getIndiceRiga(), erroreRiga);
				}
			}
		}
		List<String> erroreRiga = new ArrayList<String>();
		erroreRiga.add(errore);
		result.getRigheConErrore().put(recordAdempimentoConErrore.getIndiceRiga(), erroreRiga);
	}

	private boolean gestisciSuccessoRiga(ImportAdempimenti190Bean recordAdempimento, ImportAdempimenti190ResultBean result) {

		if (result.getLottiConSuccesso().containsKey(recordAdempimento.getCig())) {
			result.getLottiConSuccesso().get(recordAdempimento.getCig()).add(recordAdempimento);
		} else {
			List<ImportAdempimenti190Bean> adempimenti = new ArrayList<ImportAdempimenti190Bean>();
			adempimenti.add(recordAdempimento);
			result.getLottiConSuccesso().put(recordAdempimento.getCig(), adempimenti);
		}
		return true;
	}

	private void calcolaRigheConSuccesso(String cig, ImportAdempimenti190ResultBean result) {

		int numeroRigheConSuccesso = result.getNumRigheSuccesso();
		ListIterator<ImportAdempimenti190Bean> it = result.getLottiConSuccesso().get(cig).listIterator();
		while (it.hasNext()) {
			numeroRigheConSuccesso++;
			it.next();
		}
		result.setNumRigheSuccesso(numeroRigheConSuccesso);
	}

	private void checkNumPartecipantiPerLotto(int numeroRigaCorrente, String cigGruppo, String cigPrecedente, String gruppoPrecedente,
					String cigGruppoPrecedente, ResourceBundle resBundleGenerale, Map<String, Integer> gruppiPartecipanti,
					HashMap<String, List<ImportAdempimenti190Bean>> lotti, ImportAdempimenti190ResultBean result) {

		if ((numeroRigaCorrente > START_ROW && !cigGruppoPrecedente.equals(cigGruppo) && gruppiPartecipanti.containsKey(cigGruppoPrecedente)
						&& (gruppiPartecipanti.get(cigGruppoPrecedente)) < 2)) {
			lotti.remove(cigPrecedente);
			if (result.getRigheConErrore().containsKey(numeroRigaCorrente - 1)) {
				result.getRigheConErrore().get(numeroRigaCorrente - 1)
								.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.lessthanthwopartecipantipergruppo")
												.replace("{0}", gruppoPrecedente).replace("{1}", cigPrecedente));
			} else {
				List<String> erroriRigaPrecedente = new ArrayList<String>();
				erroriRigaPrecedente.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.lessthanthwopartecipantipergruppo")
								.replace("{0}", gruppoPrecedente).replace("{1}", cigPrecedente));
				result.getRigheConErrore().put(numeroRigaCorrente - 1, erroriRigaPrecedente);
			}
		}
	}

	/**
     * Importazione dati dal modello excel con formato XLS, XLSX, ODS, OTS per SAP
     *
     * @param idAnticor
     * @param file
     * @param uffint
     * @param resBundleGenerale
     * @return resutlt un bean che contiente il numero di righe parsate, le righe
     * corrette e le righe con errori
     * @throws GestoreException eccezione sollevata dal manager
     */
    public ImportAdempimenti190ResultBean importDataSAP(String idAnticor, FormFile file, String uffint, ResourceBundle resBundleGenerale)
            throws GestoreException {

        Object sheet = null;
        String fileExtension = ExcelUtils.getFileExtension(file.getFileName());
        InputStream fileStream = null;
        try {
            fileStream = file.getInputStream();
            if (fileExtension.equals(ExcelUtils.TYPE_XLSX)) {
                Workbook xssfWb;
                xssfWb = new XSSFWorkbook(fileStream);
                sheet = xssfWb.getSheetAt(0);
            } else if (fileExtension.equals(ExcelUtils.TYPE_XLS)) {
                Workbook hssfWb;
                POIFSFileSystem fs = new POIFSFileSystem(fileStream);
                hssfWb = new HSSFWorkbook(fs);
                sheet = hssfWb.getSheetAt(0);
            } else if (fileExtension.equals(ExcelUtils.TYPE_OTS) || fileExtension.equals(ExcelUtils.TYPE_ODS)) {
                OdfSpreadsheetDocument ods = (OdfSpreadsheetDocument) OdfDocument.loadDocument(fileStream);
                sheet = ods.getTableList().get(0);
            }
        } catch (Exception ex) {
            throw new GestoreException("Si è verificato un errore nell'apertura del documento Excel/OpenDocument", "importaesportaexcel.excelerroreio",
                            new Object[]{file.getFileName()}, ex);
        } finally {
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException ex) {
                    throw new GestoreException("Si è verificato un errore nella chiusura dello stream del file excel", "importaesportaexcel.errorchiusurastreamexcel",
                                    new Object[]{file.getFileName()}, ex);
                }
            }
        }
        if (sheet == null) {
            throw new GestoreException("Non trovato il foglio " + FOGLIO_MODELLO_ADEMPIMENTI_LEGGE_190,
                            "importaesportaexcel.foglionontrovato", new Object[]{FOGLIO_MODELLO_ADEMPIMENTI_LEGGE_190},
                            null);
        }
        ImportAdempimenti190ResultBean result = this.processRowsSAP(sheet, idAnticor, uffint, resBundleGenerale);
        return result;
    }

    /**
     *
     * @param sheet
     * @param idAnticor
     * @param uffint
     * @param resBundleGenerale
     * @return ImportAdempimenti190ResultBean
     * @throws GestoreException
     */
    private ImportAdempimenti190ResultBean processRowsSAP(Object sheet, String idAnticor, String uffint,
        ResourceBundle resBundleGenerale) throws GestoreException {

    ImportAdempimenti190ResultBean result = new ImportAdempimenti190ResultBean();
    int numeroRigheProcessate = 0;
    int numeroRigaCorrente = START_ROW;

    Vector<?> anticorlotto = null;
    boolean emptyRow = false;

    //si controlla che nel file sia presente un numero di colonne apri a 15!
    int numRighe = ExcelUtils.getNumberOfRows(sheet);
    if(numRighe>0){
      int indiceControllo = 0;
      int numColonne=0;
      //if(numRighe==1)
      //  indiceControllo = 0;
      if (sheet instanceof Sheet) {
        numColonne = ((Sheet) sheet).getRow(indiceControllo).getLastCellNum();
      } else if (sheet instanceof OdfTable) {
        numColonne = ((OdfTable) sheet).getColumnByIndex(indiceControllo).getCellCount();
      }
      if(numColonne != NUMERO_COLONNE_FILE_SAP){
        throw new GestoreException("Importazione bloccata, numero di colonne diverso da quello atteso",
            "legge190.sap.numerocolonne");
      }
    }

    List<?> listaSceltaContraente;
    try {
      String subStringPrimi2Caratteri = sqlManager.getDBFunction("substr",
          new String[] { "tab1desc", "1", "2" });
      String SQL_SCELTA_CONTRAENTE = "select " + subStringPrimi2Caratteri + ",tab1tip from tab1 where tab1cod='A2044' and tab1tip<90 and tab1tip>50 order by tab1tip desc";
      listaSceltaContraente = this.sqlManager.getListVector(SQL_SCELTA_CONTRAENTE, null);
    } catch (SQLException e) {
      throw new GestoreException("Errore inaspettato accorso durante il recupero informazioni sul lotto", "importaesportaexcel.erroreinaspettato", e);
    }

    Object row;
    String codFiscProp = null;
    String ragSocialeProp = null;
    String cig = null;
    String oggettoLotto = null;
    String sceltaContraenteString = null;
    String codFiscOpITA = null;
    String codFiscOpEST = null;
    String ragSocAgg = null;
    Double importoAgg = null;
    Date dataInizio = null;
    Date dataUltimazione = null;
    Double importoSommeLiquidate = null;
    String codFiscResponsabileExcel = null;
    String nomeResponsabileExcel = null;
    List<String> erroriRiga = new ArrayList<String>();
    String idcontratto = null;
    String daAnnoPrec = null;
    int sceltaContraente=0;
    String cigLotto = null;
    boolean inserimentoLotto = true;
    Long idAnticorlotti= null;
    Vector<?> cigIdAnticorlotti = null;
    Number importoSommeLiq = null;
    Long partecipanteId = null;
    Long dittaId = null;
    int numeroRigheConSuccesso =0;
    TransactionStatus status = null;
    boolean eseguireCommit= true;
    Long conteggioLotti = null;
    boolean codfiscAggValido = true;
    boolean idFiscAggValido = true;
    Vector<?> datiAgg = null;
    String tipo = null;
    Long anticorpartecip = null;
    boolean esitoControlloAgg = false;

    String subStringPrimoCarattereCIG = sqlManager.getDBFunction("substr",
        new String[] { "cig", "1", "1" });
    String subStringPrimi5CaratteriCIG = sqlManager.getDBFunction("substr",
        new String[] { "cig", "1", "5" });

    String errorAnticorLottoInsert = resBundleGenerale.getString("errors.gestoreException.*.legge190.rigaesclusapererrorilotto");
    String errorAnticorLottoUpdate = resBundleGenerale.getString("errors.gestoreException.*.legge190.rigaesclusaaggiornamentopererrorilotto");
    String erroreControlliLotto = resBundleGenerale.getString("errors.gestoreException.*.legge190.errorecontrolli");
    String errorMessg=null;
    boolean lottoAnnoPrec= false;
    boolean controlliPrecAgg = true;
    for (int rowIndex = START_ROW - 1; rowIndex <= numRighe; rowIndex++) {
      emptyRow = false;
      lottoAnnoPrec= false;
      erroriRiga = new ArrayList<String>();
      controlliPrecAgg = true;
        numeroRigheProcessate++;
        if (rowIndex != START_ROW - 1) {
            numeroRigaCorrente++;
        }

        row = ExcelUtils.getRow(sheet, rowIndex);
        anticorlotto = null;

        if (row != null) {

          //Controlli di validità
          idcontratto =   ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_IDCONTRATTO_SAP - 1));

            if(StringUtils.isBlank(idcontratto)){
              erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.idcontrattonullo"));
            }else{

              cig = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_CIG_SAP - 1));
              if (StringUtils.isBlank(cig) || cig.length() != CIG_MIN_LENGHT) {
                  erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.cignottenchars"));
              }else{
                try {
                    anticorlotto = this.sqlManager.getVector(SQL_GET_LOTTO_BY_CIG, new Object[] { cig, new Long(idAnticor) } );
                    if (anticorlotto != null) {
                      daAnnoPrec = (((JdbcParametro) anticorlotto.get(2)).stringValue());
                      if (daAnnoPrec != null && daAnnoPrec.equals(ImportAdempimenti190Bean.ANNO_PRECEDENTE)) {
                        erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.lottoannoprec"));
                        lottoAnnoPrec = true;
                      }
                   }
                } catch (SQLException ex) {
                    throw new GestoreException("Errore inaspettato accorso durante il recupero informazioni sul lotto", "importaesportaexcel.erroreinaspettato", ex);
                }
              }

              if(!lottoAnnoPrec){
                if (!"0000000000".equals(cig)){
                  try {
                    if(!this.pgManager.controlloCodiceCIG(cig)){
                      erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.cigNonValido"));
                      controlliPrecAgg= false;
                    } else{
                      cigLotto = null;
                      conteggioLotti = (Long)this.sqlManager.getObject(SQL_CONTROLLO_LOTTI_CIG_ANNOPREC,new Object[]{new Long(idAnticor), idcontratto, cig ,ImportAdempimenti190Bean.ANNO_PRECEDENTE});
                      if(conteggioLotti!=null && conteggioLotti.longValue() >0){
                        erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.lottoannoprec"));
                        controlliPrecAgg= false;
                      }else{
                        conteggioLotti = (Long)this.sqlManager.getObject(SQL_CONTROLLO_LOTTI_CIG,new Object[]{new Long(idAnticor), cig, idcontratto });
                        if(conteggioLotti!=null && conteggioLotti.longValue() >0){
                          erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.idcontrattoduplicato.SAP"));
                          controlliPrecAgg= false;
                        }
                        conteggioLotti = (Long)this.sqlManager.getObject(SQL_CONTROLLO_LOTTI_IDCONTRATTO,new Object[]{new Long(idAnticor), idcontratto, cig});
                        if(conteggioLotti!=null && conteggioLotti.longValue() >0){
                          erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.idcontrattoduplicato.CIG"));
                          controlliPrecAgg= false;
                        }
                      }
                    }
                  } catch (Exception e) {
                    throw new GestoreException("Errore inaspettato accorso durante il recupero informazioni sul lotto", "importaesportaexcel.erroreinaspettato", e);
                  }
                }else{
                  try {
                    cigIdAnticorlotti = this.sqlManager.getVector(SQL_GET_CIG_LOTTO_BY_IDCONTRATTO + " and cig is not null and ( " + subStringPrimi5CaratteriCIG + " = 'NOCIG' or " + subStringPrimoCarattereCIG + " = '$' "
                        + "or " + subStringPrimoCarattereCIG + " = '#') and daannoprec=? ",new Object[]{new Long(idAnticor), idcontratto, ImportAdempimenti190Bean.ANNO_PRECEDENTE});
                    if(cigIdAnticorlotti!=null && cigIdAnticorlotti.size()>0){
                      cigLotto = SqlManager.getValueFromVectorParam(cigIdAnticorlotti, 0).getStringValue();
                      if(cigLotto!=null && !"".equals(cigLotto)){
                        erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.lottoannoprec"));
                        controlliPrecAgg= false;
                      }
                    }else{
                      cigIdAnticorlotti = this.sqlManager.getVector(SQL_GET_CIG_LOTTO_BY_IDCONTRATTO + " and cig is not null and ( " + subStringPrimi5CaratteriCIG + " <> 'NOCIG' and " + subStringPrimoCarattereCIG + " <> '$' "
                          + "and " + subStringPrimoCarattereCIG + " <> '#')",new Object[]{new Long(idAnticor), idcontratto});
                      if(cigIdAnticorlotti!=null && cigIdAnticorlotti.size()>0){
                        cigLotto = SqlManager.getValueFromVectorParam(cigIdAnticorlotti, 0).getStringValue();
                        if(cigLotto!=null && !"".equals(cigLotto)){
                          erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.idcontrattoduplicato.CIG"));
                          controlliPrecAgg= false;
                        }
                      }
                    }
                  } catch (Exception e) {
                    throw new GestoreException("Errore inaspettato accorso durante il recupero informazioni sul lotto", "importaesportaexcel.erroreinaspettato", e);
                  }
                }

                if(!StringUtils.isBlank(cig))
                  cig =cig.toUpperCase();

                codFiscProp = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_CODFISC_PROP_SAP - 1));
                if (StringUtils.isNotEmpty(codFiscProp)) {
                  if(!UtilityFiscali.isValidCodiceFiscale(codFiscProp) && !UtilityFiscali.isValidPartitaIVA(codFiscProp)){
                    erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.codfiscstazappnonvalido"));
                    controlliPrecAgg= false;
                  }
                }else{
                  erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.codfiscstazappnull"));
                  controlliPrecAgg= false;
                }

                ragSocialeProp = StringUtils.substring(ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_DENOMINAZIONE_SAP - 1)), 0, 250);
                if (StringUtils.isEmpty(ragSocialeProp)) {
                  erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.ragsocstazappnull"));
                  controlliPrecAgg= false;
                }

                oggettoLotto = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_OGGETTO_SAP - 1));
                if (StringUtils.isEmpty(oggettoLotto)) {
                  erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.oggettoLottonull"));
                  controlliPrecAgg= false;
                }

                sceltaContraenteString = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_SCELTACONTRAENTE_SAP - 1));
                if (StringUtils.isEmpty(sceltaContraenteString)) {
                  erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.sceltacontraentenull"));
                  controlliPrecAgg= false;
                }else{
                  // Si considerano i primi 2 caratteri inseriti e si confrontano con i primi 2 caratteri della descrizione del tabellato A2044
                  if(listaSceltaContraente!=null){
                    String prefisso=sceltaContraenteString.substring(0, 2);
                    for(int j=0;j<listaSceltaContraente.size();j++){
                      if(prefisso.equals(SqlManager.getValueFromVectorParam(listaSceltaContraente.get(j), 0).stringValue())){
                        sceltaContraente = SqlManager.getValueFromVectorParam(listaSceltaContraente.get(j), 1).longValue().intValue();
                        break;
                      }
                    }
                    if(sceltaContraente==0){
                      erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.sceltacontraentenonvalido"));
                      controlliPrecAgg= false;
                    }
                  }
                }


                codFiscOpITA = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_COD_FISC_AGGIUD_SAP - 1));
                codFiscOpEST = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_ID_FISCALE_ESTERO_AGGIUD_SAP - 1));

                if(StringUtils.isEmpty(codFiscOpITA) && StringUtils.isEmpty(codFiscOpEST)){
                  erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.notaxcode"));
                  controlliPrecAgg= false;
                }else{
                  codfiscAggValido = true;
                  idFiscAggValido = true;

                  if(StringUtils.isNotEmpty(codFiscOpITA) && !UtilityFiscali.isValidCodiceFiscale(codFiscOpITA, true) && !UtilityFiscali.isValidPartitaIVA(codFiscOpITA)){
                    erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.codfiscaggiudnonvalido"));
                    codfiscAggValido = false;
                  }else if(StringUtils.isEmpty(codFiscOpITA))
                    codfiscAggValido = false;

                  if(StringUtils.isNotEmpty(codFiscOpEST) && !UtilityFiscali.isValidCodiceFiscale(codFiscOpEST, false)){
                    erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.idfiscesteroaggiudnonvalido"));
                    idFiscAggValido = false;

                  }else if(StringUtils.isEmpty(codFiscOpEST))
                    idFiscAggValido = false;

                  if((codfiscAggValido || idFiscAggValido) && controlliPrecAgg){
                    try {
                      datiAgg = this.sqlManager.getVector(SQL_CONTROLLO_AGGIUDICATARIE, new Object[]{idAnticor,idcontratto});
                      if(datiAgg!=null){
                        tipo = SqlManager.getValueFromVectorParam(datiAgg, 0).getStringValue();
                        anticorpartecip = SqlManager.getValueFromVectorParam(datiAgg, 1).longValue();
                        if(anticorpartecip!=null){
                          esitoControlloAgg=true;
                          String codfiscAgg = null;
                          String idfiscAgg = null;
                          String select="select codfisc, idfiscest from anticorditte where idanticorpartecip=?";
                          if("2".equals(tipo))
                            select += " and ruolo='2'";
                          Vector<?> datiAggDb = this.sqlManager.getVector(select, new Object[]{anticorpartecip});
                          if(datiAggDb!=null){
                            codfiscAgg = SqlManager.getValueFromVectorParam(datiAggDb, 0).getStringValue();
                            idfiscAgg = SqlManager.getValueFromVectorParam(datiAggDb, 1).getStringValue();
                          }
                          if((codFiscOpITA!=null && !codFiscOpITA.equals(codfiscAgg)) || (codFiscOpEST!=null && !codFiscOpEST.equals(idfiscAgg))){
                            esitoControlloAgg = false;

                          }

                          if(!esitoControlloAgg){
                            String msg = resBundleGenerale.getString("errors.gestoreException.*.legge190.aggiudicatariaVariata");
                            String valoreMsg = null;
                            if(codFiscOpITA!=null && !codFiscOpITA.equals(codfiscAgg))
                              valoreMsg = codfiscAgg;
                            else
                              valoreMsg = idfiscAgg;
                            if(valoreMsg==null)
                              valoreMsg = "";
                            msg = msg.replace("{0}", valoreMsg);
                            if(codfiscAggValido)
                              valoreMsg = codFiscOpITA;
                            else if(idFiscAggValido)
                              valoreMsg = codFiscOpEST;
                            msg = msg.replace("{1}", valoreMsg);
                            erroriRiga.add(msg);
                          }
                        }
                      }
                    } catch (SQLException e) {
                      throw new GestoreException("Errore inaspettato accorso durante il recupero informazioni sul lotto", "importaesportaexcel.erroreinaspettato", e);
                    }
                  }
                }

                ragSocAgg = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_RAG_SOG_AGGIUD_SAP - 1));
                if (StringUtils.isEmpty(ragSocAgg)) {
                  erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.ragsocaggiudnull"));
                }


                try {
                  Number importoAggiudic = ExcelUtils.readNumberFromCell(sheet, rowIndex, COL_IMPORTO_AGGIUD_SAP - 1, 2);
                  if (importoAggiudic != null) {
                    importoAggiudic = UtilityNumeri.arrotondaNumero(importoAggiudic,2);
                    importoAgg = importoAggiudic.doubleValue();
                  }else{
                    erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.importoaggiudnull"));
                  }
                } catch (GestoreException ex) {
                  erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*." + ex.getCodice()).replace("{0}", "IMPORTO AGGIUDICAZIONE"));
                }


                try {
                  dataInizio = ExcelUtils.readDateFromCell(sheet, rowIndex, COL_DATA_INIZIO_SAP - 1);
                } catch (GestoreException ex) {
                    erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*." + ex.getCodice()).replace("{0}", "DATA INIZIO"));
                }

                try {
                    dataUltimazione = ExcelUtils.readDateFromCell(sheet, rowIndex, COL_DATA_ULTIMAZIONE_SAP - 1);
                } catch (GestoreException ex) {
                    erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*." + ex.getCodice()).replace("{0}", "DATA ULTIMAZIONE"));
                }

                try {
                    importoSommeLiq = ExcelUtils.readNumberFromCell(sheet, rowIndex, COL_IMPORTO_SOMME_LIQ_SAP - 1, 2);
                    if (importoSommeLiq != null) {
                      importoSommeLiq = UtilityNumeri.arrotondaNumero(importoSommeLiq,2);
                      importoSommeLiquidate = importoSommeLiq.doubleValue();
                    }
                } catch (GestoreException ex) {
                    erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*." + ex.getCodice()).replace("{0}", "IMPORTO SOMME_LIQUIDATE"));
                }

                codFiscResponsabileExcel = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex,
                    COL_CODFISC_RESPONSABILE_SAP - 1));
                if (StringUtils.isNotEmpty(codFiscResponsabileExcel) && !UtilityFiscali.isValidCodiceFiscale(codFiscResponsabileExcel)) {
                  erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.legge190.codfiscresponsabilenonvalido"));
                }
                if (StringUtils.isNotEmpty(codFiscResponsabileExcel))
                  codFiscResponsabileExcel = codFiscResponsabileExcel.toUpperCase();

                nomeResponsabileExcel = StringUtils.substring(ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex,
                    COL_NOM_COGN_REPSONSABILE_SAP - 1)), 0, 161);
                if(nomeResponsabileExcel!=null && nomeResponsabileExcel.length()>161)
                  nomeResponsabileExcel.substring(0, 161);



                if (StringUtils.isBlank(idcontratto)
                    && StringUtils.isBlank(cig)
                    && StringUtils.isBlank(codFiscProp)
                    && StringUtils.isBlank(ragSocialeProp)
                    && StringUtils.isBlank(oggettoLotto)
                    && StringUtils.isBlank(sceltaContraenteString)
                    && StringUtils.isBlank(codFiscOpITA)
                    && StringUtils.isBlank(codFiscOpEST)
                    && StringUtils.isBlank(ragSocAgg)
                    && importoAgg == null
                    && dataInizio == null
                    && dataUltimazione == null
                    && importoSommeLiquidate == null
                    && StringUtils.isBlank(codFiscResponsabileExcel)
                    && StringUtils.isBlank(nomeResponsabileExcel)) {
                    emptyRow = true;
                }
              }
            }
        } else {
            emptyRow = true;
        }

        if (emptyRow) {
            break;
        }

        result.setNumRigheTotaliAnalizzate(numeroRigheProcessate);

        if (!erroriRiga.isEmpty()) {
            result.getRigheConErrore().put(numeroRigaCorrente, erroriRiga);
        } else {
          //Si deve stabilire se inserire il lotto o aggiungere un nuovo lotto
          inserimentoLotto=true;
          idAnticorlotti = null;
          try {
            if("0000000000".equals(cig)){
              cigLotto = null;
              cigIdAnticorlotti = this.sqlManager.getVector(SQL_GET_CIG_LOTTO_BY_IDCONTRATTO + " and cig is not null and ( " + subStringPrimi5CaratteriCIG + " = 'NOCIG' or " + subStringPrimoCarattereCIG + " = '$' "
                  + "or " + subStringPrimoCarattereCIG + " = '#')",new Object[]{new Long(idAnticor), idcontratto});
              if(cigIdAnticorlotti!=null){
                idAnticorlotti = SqlManager.getValueFromVectorParam(cigIdAnticorlotti, 1).longValue();
                if(idAnticorlotti!=null )
                  inserimentoLotto=false;
              }
            }else {
              idAnticorlotti = (Long)this.sqlManager.getObject("SELECT id from ANTICORLOTTI where CIG=? and IDANTICOR=? and IDCONTRATTO = ?", new Object[] { cig, new Long(idAnticor), idcontratto} );
              if(idAnticorlotti!=null )
                inserimentoLotto=false;
            }

            status = this.sqlManager.startTransaction();
            eseguireCommit = false;

            if(!inserimentoLotto){
              //Aggiornamento lotto
              errorMessg = errorAnticorLottoUpdate;
              this.sqlManager.update(SQL_UPDATE_SAP, new Object[]{dataInizio, dataUltimazione,importoSommeLiq, idcontratto, idAnticorlotti});

              errorMessg = erroreControlliLotto;
              this.controlliAVCP(idAnticorlotti, uffint);

              numeroRigheConSuccesso++;
              eseguireCommit= true;
            }else{
              //Inserimento in ANTICORLOTTI
              errorMessg = errorAnticorLottoInsert;
              idAnticorlotti = new Long(this.genChiaviManager.getNextId("ANTICORLOTTI"));

              Object[] params = new Object[]{idAnticorlotti, new Long(idAnticor), ImportAdempimenti190Bean.ANNO_CORRENTE, cig, codFiscProp,
                ragSocialeProp, oggettoLotto, new Long(sceltaContraente), importoAgg, dataInizio, dataUltimazione, importoSommeLiquidate, AGGIUDICATO,
                ImportAdempimenti190Bean.LOTTO_NOT_IN_BACKOFFICE, 2, 2, codFiscResponsabileExcel, nomeResponsabileExcel,idcontratto};

              if (StringUtils.indexOf(cig, "0000000000") == 0) {
                int id = this.genChiaviManager.getNextId("GARE.CODCIG");
                params[3] = "#".concat(StringUtils.leftPad("" + id, 9, "0"));
              }

              this.sqlManager.update(SQL_INSERT_LOTTI_SAP, params);

              //Inserimento ANTICORPARTECIP
              partecipanteId = new Long(this.genChiaviManager.getNextId("ANTICORPARTECIP"));
              params = new Object[]{partecipanteId, idAnticorlotti, new Long(1), "1", ragSocAgg};
              this.sqlManager.update(SQL_INSERT_PARTECIP, params);

              //Inserimento ANTICORDITTE
              dittaId = new Long(this.genChiaviManager.getNextId("ANTICORDITTE"));
              params = new Object[]{dittaId, partecipanteId, ragSocAgg, codFiscOpITA, codFiscOpEST, null};

              this.sqlManager.update(SQL_INSERT_DITTE, params);

              errorMessg = erroreControlliLotto;
              this.controlliAVCP(idAnticorlotti, uffint);

              numeroRigheConSuccesso++;
              eseguireCommit= true;
            }
          } catch (SQLException ex) {
            List<String> erroriRigaSalvataggio = new ArrayList<String>();
            erroriRigaSalvataggio.add(errorMessg);
            result.getRigheConErrore().put(numeroRigaCorrente, erroriRigaSalvataggio);
          }finally {
            if (status != null) {
              try {
                  if (eseguireCommit) {
                      this.sqlManager.commitTransaction(status);
                  } else {
                      this.sqlManager.rollbackTransaction(status);

                  }
              } catch (SQLException ex) {
                  throw new GestoreException("Errore inaspettato accorso durante la procedura di import/export excel", "importaesportaexcel.erroreinaspettato", ex);
              }
          }
      }

        }

    }

    result.setNumRigheSuccesso(numeroRigheConSuccesso);
    result.setNumRigheErrore(result.getRigheConErrore() != null ? result.getRigheConErrore().values().size() : 0);
    return result;
}
}