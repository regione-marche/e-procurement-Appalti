/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eldasoft.sil.pg.bl;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.excel.ExcelResultBean;
import it.eldasoft.sil.pg.bl.excel.ExcelUtils;
import it.eldasoft.sil.pg.bl.excel.bean.ImportArticoliBean;
import it.eldasoft.sil.pg.bl.excel.bean.ImportArticoliConfigBean;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.www.PortaleAlice.EsitoOutType;
import it.eldasoft.www.PortaleAlice.PortaleAliceProxy;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.springframework.transaction.TransactionStatus;

/**
 *
 * @author marco.perazzetta
 */
public class ArticoliManager {

	static Logger logger = Logger.getLogger(ArticoliManager.class);
	private SqlManager sqlManager;
	private PgManager pgManager;
	private GenChiaviManager genChiaviManager;
	public static final String FOGLIO_MODELLO_ARTICOLI = "Articoli";
	private static final int START_ROW = 2;
	private static final int COL_CODICE_CATEGORIA = 1;
	private static final String COL_CODICE_CATEGORIA_NAME = "CODICE CATEGORIA";
	private static final int COL_CODICE_ARTICOLO = 2;
	private static final String COL_CODICE_ARTICOLO_NAME = "CODICE ARTICOLO";
	private static final int COL_TIPO_ARTICOLO = 3;
	private static final String COL_TIPO_ARTICOLO_NAME = "TIPO ARTICOLO";
	private static final int COL_DESCRIZIONE = 4;
	private static final String COL_DESCRIZIONE_NAME = "DESCRIZIONE";
	private static final int COL_DESCRIZIONE_TECNICA = 5;
	private static final String COL_DESCRIZIONE_TECNICA_NAME = "DESCRIZIONE TECNICA";
	private static final int COL_OBB_INS_IMMAGINE = 6;
	private static final String COL_OBB_INS_IMMAGINE_NAME = "OBBLIGO INSERIMENTO IMMAGINE";
	private static final int COL_OBB_INS_DESCRIZIONE_TECNICA = 7;
	private static final String COL_OBB_INS_DESCRIZIONE_TECNICA_NAME = "OBBLIGO INSERIMENTO DESCRIZIONE TECNICA";
	private static final int COL_OBB_INS_DIMENSIONI = 8;
	private static final String COL_OBB_INS_DIMENSIONI_NAME = "OBBLIGO INSERIMENTO DIMENSIONI";
	private static final int COL_OBB_INS_CERTIFICAZIONI = 9;
	private static final String COL_OBB_INS_CERTIFICAZIONI_NAME = "OBBLIGO INSERIMENTO CERTIFICAZIONI";
	private static final int COL_CERTIFICAZIONI_RICHIESTE = 10;
	private static final String COL_CERTIFICAZIONI_RICHIESTE_NAME = "CERTIFICAZIONI RICHIESTE";
	private static final int COL_OBB_INS_SCHEDA_TECNICA = 11;
	private static final String COL_OBB_INS_SCHEDA_TECNICA_NAME = "OBBLIGO INSERIMENTO SCHEDA TECNICA";
	private static final int COL_OBB_INS_GARANZIA = 12;
	private static final String COL_OBB_INS_GARANZIA_NAME = "INSERIMENTO GARANZIA";
	//private static final int COL_GARANZIA = 13;
	//private static final String COL_GARANZIA_NAME = "GARANZIA";
	private static final int COL_COLORE = 13;
	private static final String COL_COLORE_NAME = "COLORE";
	private static final int COL_PRZ_UNITARIO_RIFERITO_A = 14;
	private static final String COL_PRZ_UNITARIO_RIFERITO_A_NAME = "MODALITA' DI ACQUISTO";
	private static final int COL_UNITA_MISURA_PRZ = 15;
	private static final String COL_UNITA_MISURA_PRZ_NAME = "UNITA' DI MISURA SU CUI E' ESPRESSO IL PREZZO";
	private static final int COL_DECIMALI_UNITA_MISURA_PRZ = 16;
	private static final String COL_DECIMALI_UNITA_MISURA_PRZ_NAME = "N.MAX DECIMALI PREZZO UNITARIO";
	private static final int COL_UNITA_MISURA_ACQ = 17;
	private static final String COL_UNITA_MISURA_ACQ_NAME = "UNITA' DI  MISURA A CUI E' RIFERITO L'ACQUISTO";
	private static final int COL_DECIMALI_UNITA_MISURA_ACQ = 18;
	private static final String COL_DECIMALI_UNITA_MISURA_ACQ_NAME = "N.MAX DECIMALI PREZZO UNITARIO PER L'ACQUISTO";
	private static final int COL_QTA_MISURA_ACQ = 19;
	private static final String COL_QTA_MISURA_ACQ_NAME = "LOTTO MINIMO PER UNITA' DI MISURA";
	private static final int COL_QTA_MIN_PRZ = 20;
	private static final String COL_QTA_MIN_PRZ_NAME = "LOTTO MINIMO PER UNITA' DI MISURA:VALORE MINIMO";
	private static final int COL_QTA_MAX_PRZ = 21;
	private static final String COL_QTA_MAX_PRZ_NAME = "LOTTO MINIMO PER UNITA' DI MISURA:VALORE MASSIMO";
	private static final int COL_TEMPO_MAX_CONSEGNA = 22;
	private static final String COL_TEMPO_MAX_CONSEGNA_NAME = "TEMPO MASSIMO DI CONSEGNA";
	private static final int COL_UNITA_MISURA_TEMPO_CONSEGNA = 23;
	private static final String COL_UNITA_MISURA_TEMPO_CONSEGNA_NAME = "UNITA' DI MISURA TEMPO MASSIMO DI CONSEGNA";
	private static final int COL_ARTICOLO_ACQ_VERDE = 24;
	private static final String COL_ARTICOLO_ACQ_VERDE_NAME = "ARTICOLO PER ACQUISTO VERDE";
	private static final int COL_PRODOTTO_DA_VERIFICARE = 25;
	private static final String COL_PRODOTTO_DA_VERIFICARE_NAME = "PRODOTTO DA VERIFICARE";
	private static final int COL_NOTE = 26;
	private static final int COL_STATO = 27;
	private static final String TIPO_ART_BENE = "Bene";
	private static final String TIPO_ART_SERVIZIO = "Servizio";
	private static final String UNITA_DI_MISURA = "Unità di misura";
	private static final String CONFEZIONE = "Confezione";
	private static final String PRODOTTO_SERVIZIO = "Prodotto/Servizio";
	private static final String PRODOTTO_SERVIZIO_UM = "Prodotto/servizio per UM";
	private static String SQL_GET_CATEGORIA = "SELECT caisim as codice, descat as descrizione FROM cais WHERE caisim = ?";
	private static String SQL_CHECK_CATEGORIA_FOGLIA = "SELECT 1 FROM cais WHERE codliv1 = ? OR codliv2 = ? OR codliv3 = ? OR codliv4 = ?";
	private static String SQL_GET_NUMERO_CATEGORIA_CATALOGO = "SELECT nopega as numeroCategoria FROM opes WHERE ngara3 = ? AND catoff = ?";
	private static String SQL_GET_ARTICOLO_CATEGORIA_PER_CATALOGO_E_COD = "SELECT id as idArticolo FROM meartcat WHERE ngara = ? AND upper(cod) = ?";
	private static String COD_TIPO_ARTICOLO = "ME001";
	private static String COD_STATO_ARTICOLO = "ME002";
	private static String COD_PRZ_UNITARIO_RIFERITO_A = "ME003";
	private static String COD_UNITA_DI_MISURA = "ME007";
	private static String COD_TEMPO_CONSEGNA = "ME004";
	private static String SQL_GET_TABELLATO_FROM_TAB1 = "SELECT tab1tip FROM tab1 where upper(tab1desc) = ? AND tab1cod = ?";
	//private static String SQL_GET_TABELLATO_FROM_TAB2 = "SELECT tab2tip FROM tab2 where upper(tab2d1) = ? AND tab2cod = ?";
	private static String SQL_INSERT_UNITA_DI_MISURA = "INSERT INTO tab1(tab1cod, tab1tip, tab1desc, tab1mod, tab1nord) VALUES (?,?,?,?,?) ";
	//private static String SQL_GET_MAX_UNITA_DI_MISURA_ID_MSQ = "SELECT MAX(CAST(tab1tip AS numeric)) FROM tab1 where tab1cod = ?";
	//private static String SQL_GET_MAX_UNITA_DI_MISURA_ID_ORA = "SELECT MAX(CAST(tab1tip AS number)) FROM tab1 where tab1cod = ?";
	private static String SQL_GET_MAX_UNITA_DI_MISURA = "SELECT MAX(tab1tip) FROM tab1 where tab1cod = ?";
	private static final String SI = "SI";
	private static final String SI_ID = "1";
	private static final String NO_ID = "2";
	private static final String BOZZA = "bozza";
	private static final String ATTIVO = "attivo";
	private static final String ARCHIVIATO = "archiviato";

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
	 * Importazione dati dal modello excel con formato XLS, XLSX, ODS, OTS
	 *
	 * @param importConfigBean bean che contiene tutti i parametri necessari alla
	 * configurazione dell'importazione
	 * @param resBundleGenerale il resource bundle del contesto
	 * @return resutlt un bean che contiente il numero di righe parsate, le righe
	 * corrette e le righe con errori
	 * @throws GestoreException eccezione sollevata dal manager
	 */
	public ExcelResultBean importData(ImportArticoliConfigBean importConfigBean, ResourceBundle resBundleGenerale) throws GestoreException {

		Object sheet = null;
		String fileExtension = ExcelUtils.getFileExtension(importConfigBean.getFile().getFileName());
		InputStream fileStream = null;
		try {
			fileStream = importConfigBean.getFile().getInputStream();
			if (fileExtension.equals(ExcelUtils.TYPE_XLSX)) {
				Workbook xssfWb;
				xssfWb = new XSSFWorkbook(fileStream);
				sheet = xssfWb.getSheet(FOGLIO_MODELLO_ARTICOLI);
			} else if (fileExtension.equals(ExcelUtils.TYPE_XLS)) {
				Workbook hssfWb;
				POIFSFileSystem fs = new POIFSFileSystem(fileStream);
				hssfWb = new HSSFWorkbook(fs);
				sheet = hssfWb.getSheet(FOGLIO_MODELLO_ARTICOLI);
			} else if (fileExtension.equals(ExcelUtils.TYPE_OTS) || fileExtension.equals(ExcelUtils.TYPE_ODS)) {
				OdfSpreadsheetDocument ods = (OdfSpreadsheetDocument) OdfDocument.loadDocument(fileStream);
				sheet = ods.getTableByName(FOGLIO_MODELLO_ARTICOLI);
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
			throw new GestoreException("Non trovato il foglio " + FOGLIO_MODELLO_ARTICOLI,
							"importaesportaexcel.foglionontrovato", new Object[]{FOGLIO_MODELLO_ARTICOLI},
							null);
		}
		ExcelResultBean result = processRows(sheet, importConfigBean, resBundleGenerale);
		return result;
	}

	private ExcelResultBean processRows(Object sheet, ImportArticoliConfigBean importConfigBean, ResourceBundle resBundleGenerale) throws GestoreException {

		List<ImportArticoliBean> articoli = new ArrayList<ImportArticoliBean>();
		ExcelResultBean result = new ExcelResultBean();
		int numeroRigheProcessate = 0;
		int numeroRigaCorrente = START_ROW;
		boolean emptyRow = false;

		for (int rowIndex = START_ROW - 1; rowIndex <= ExcelUtils.getNumberOfRows(sheet); rowIndex++) {

			Object row;
			String codCat = null;
			String codArt = null;
			String tipoArt = null;
			String desc = null;
			String descTec = null;
			String oblInsImg = null;
			String oblInsDesAgg = null;
			String oblInsDim = null;
			String oblInsCert = null;
			String cert = null;
			String oblInsScedTec = null;
			String oblInsGar = null;
			Integer gar = null;
			String colore = null;
			String prezzoRifA = null;
			//String impConIva = null;
			String unitaMisuraPerPrz = null;
			Integer decUnitaMisuraPerPrz = null;
			String unitaMisuraAcq = null;
			Integer decUnitaMisuraAcq = null;
			Double qtaUnitaMisuraAcq = null;
			Double qtaMinPerUnitaMisuraPrz = null;
			Double qtaMaxPerUnitaMisuraPrz = null;
			Integer tempoMaxConsegna = null;
			String unitaMisuraTempoConsegna = null;
			String articoloAcqVerdeStr = null;
			String articoloAcqVerde = null;
			String prodottoDaVerificareStr = null;
			String prodottoDaVerificare = null;
			String note = null;
			String stato = null;
			List<String> erroriRiga = new ArrayList<String>();

			numeroRigheProcessate++;
			if (rowIndex != START_ROW - 1) {
				numeroRigaCorrente++;
			}

			row = ExcelUtils.getRow(sheet, rowIndex);

			if (row != null) {

				codCat = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_CODICE_CATEGORIA - 1));
				ExcelUtils.checkFieldMandatory(codCat, COL_CODICE_CATEGORIA_NAME, erroriRiga, resBundleGenerale);
				ExcelUtils.checkMaxFieldSize(codCat, COL_CODICE_CATEGORIA_NAME, 30, erroriRiga, resBundleGenerale);

				codArt = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_CODICE_ARTICOLO - 1));
				ExcelUtils.checkFieldMandatory(codArt, COL_CODICE_ARTICOLO_NAME, erroriRiga, resBundleGenerale);
				ExcelUtils.checkMaxFieldSize(codArt, COL_CODICE_ARTICOLO_NAME, 30, erroriRiga, resBundleGenerale);

				tipoArt = ExcelUtils.readStringFromCell(sheet, rowIndex, COL_TIPO_ARTICOLO - 1);
				ExcelUtils.checkFieldMandatory(tipoArt, COL_TIPO_ARTICOLO_NAME, erroriRiga, resBundleGenerale);

				desc = StringUtils.substring(ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_DESCRIZIONE - 1)), 0, 250);
				ExcelUtils.checkFieldMandatory(desc, COL_DESCRIZIONE_NAME, erroriRiga, resBundleGenerale);

				descTec = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_DESCRIZIONE_TECNICA - 1));
				ExcelUtils.checkFieldMandatory(descTec, COL_DESCRIZIONE_TECNICA_NAME, erroriRiga, resBundleGenerale);

				oblInsImg = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_OBB_INS_IMMAGINE - 1));
				oblInsImg = StringUtils.isNotBlank(oblInsImg) && oblInsImg.toUpperCase().equals(SI)
								? SI_ID : NO_ID;
				ExcelUtils.checkFieldMandatory(oblInsImg, COL_OBB_INS_IMMAGINE_NAME, erroriRiga, resBundleGenerale);

				oblInsDesAgg = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_OBB_INS_DESCRIZIONE_TECNICA - 1));
				oblInsDesAgg = StringUtils.isNotBlank(oblInsDesAgg) && oblInsDesAgg.toUpperCase().equals(SI)
								? SI_ID : NO_ID;
				ExcelUtils.checkFieldMandatory(oblInsDesAgg, COL_OBB_INS_DESCRIZIONE_TECNICA_NAME, erroriRiga, resBundleGenerale);

				oblInsDim = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_OBB_INS_DIMENSIONI - 1));
				if ((StringUtils.isBlank(tipoArt)
                                || (StringUtils.isNotBlank(tipoArt) && !tipoArt.toUpperCase().equals(TIPO_ART_BENE.toUpperCase()))) && StringUtils.isNotBlank(oblInsDim)) {
                    erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.articoli.leavefieldempty").replace("{0}", COL_OBB_INS_DIMENSIONI_NAME)
                                    .replace("{1}", COL_TIPO_ARTICOLO_NAME).replace("{2}", TIPO_ART_BENE));
                }
				oblInsDim = StringUtils.isNotBlank(oblInsDim) && oblInsDim.toUpperCase().equals(SI)
								? SI_ID : NO_ID;
				ExcelUtils.checkFieldMandatory(oblInsDim, COL_OBB_INS_DIMENSIONI_NAME, erroriRiga, resBundleGenerale);

				oblInsCert = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_OBB_INS_CERTIFICAZIONI - 1));
				oblInsCert = StringUtils.isNotBlank(oblInsCert) && oblInsCert.toUpperCase().equals(SI)
								? SI_ID : NO_ID;
				ExcelUtils.checkFieldMandatory(oblInsCert, COL_OBB_INS_CERTIFICAZIONI_NAME, erroriRiga, resBundleGenerale);

				cert = StringUtils.substring(ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_CERTIFICAZIONI_RICHIESTE - 1)), 0, 2000);
				if (StringUtils.isNotBlank(oblInsCert) && oblInsCert.equals(SI_ID)) {
					ExcelUtils.checkFieldMandatory(cert, COL_CERTIFICAZIONI_RICHIESTE_NAME, erroriRiga, resBundleGenerale);
				}
				if (StringUtils.isBlank(oblInsCert) || oblInsCert.equals("NO") && cert != null) {
					erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.articoli.leavefieldempty").replace("{0}", COL_CERTIFICAZIONI_RICHIESTE_NAME)
									.replace("{1}", COL_OBB_INS_CERTIFICAZIONI_NAME).replace("{2}", SI));
				}

				oblInsScedTec = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_OBB_INS_SCHEDA_TECNICA - 1));
				oblInsScedTec = StringUtils.isNotBlank(oblInsScedTec) && oblInsScedTec.toUpperCase().equals(SI)
								? SI_ID : NO_ID;
				ExcelUtils.checkFieldMandatory(oblInsScedTec, COL_OBB_INS_SCHEDA_TECNICA_NAME, erroriRiga, resBundleGenerale);

				oblInsGar = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_OBB_INS_GARANZIA - 1));
				oblInsGar = StringUtils.isNotBlank(oblInsGar) && oblInsGar.toUpperCase().equals(SI)
								? SI_ID : NO_ID;
				ExcelUtils.checkFieldMandatory(oblInsGar, COL_OBB_INS_GARANZIA_NAME, erroriRiga, resBundleGenerale);

				colore = StringUtils.substring(ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_COLORE - 1)), 0, 60);
				if ((StringUtils.isBlank(tipoArt)
								|| (StringUtils.isNotBlank(tipoArt) && !tipoArt.toUpperCase().equals(TIPO_ART_BENE.toUpperCase()))) && StringUtils.isNotBlank(colore)) {
					erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.articoli.leavefieldempty").replace("{0}", COL_COLORE_NAME)
									.replace("{1}", COL_TIPO_ARTICOLO_NAME).replace("{2}", TIPO_ART_BENE));
				}

				prezzoRifA = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_PRZ_UNITARIO_RIFERITO_A - 1));
				ExcelUtils.checkFieldMandatory(prezzoRifA, COL_PRZ_UNITARIO_RIFERITO_A_NAME, erroriRiga, resBundleGenerale);

				unitaMisuraPerPrz = StringUtils.substring(ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_UNITA_MISURA_PRZ - 1)), 0, 100);
				ExcelUtils.checkFieldMandatory(unitaMisuraPerPrz, COL_UNITA_MISURA_PRZ_NAME, erroriRiga, resBundleGenerale);

				Number decUnitaMisuraPerPrzNumber = ExcelUtils.readNumberFromCell(sheet, rowIndex, COL_DECIMALI_UNITA_MISURA_PRZ - 1, 0);
				if (decUnitaMisuraPerPrzNumber != null) {
					decUnitaMisuraPerPrz = decUnitaMisuraPerPrzNumber.intValue();
				}
				ExcelUtils.checkFieldMandatory(decUnitaMisuraPerPrzNumber, COL_DECIMALI_UNITA_MISURA_PRZ_NAME, erroriRiga, resBundleGenerale);
				if (decUnitaMisuraPerPrz != null) {
					ExcelUtils.checkMaxFieldSize(decUnitaMisuraPerPrz, COL_DECIMALI_UNITA_MISURA_PRZ_NAME, 2, erroriRiga, resBundleGenerale);
					if(decUnitaMisuraPerPrz<0 || decUnitaMisuraPerPrz>5)
					  erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.articoli.decimaliUnitMisura").replace("{0}", COL_DECIMALI_UNITA_MISURA_PRZ_NAME));
				}

				unitaMisuraAcq = StringUtils.substring(ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_UNITA_MISURA_ACQ - 1)), 0, 100);
				if (StringUtils.isNotBlank(prezzoRifA) && prezzoRifA.toUpperCase().equals(UNITA_DI_MISURA.toUpperCase())) {
					ExcelUtils.checkFieldMandatory(unitaMisuraAcq, COL_UNITA_MISURA_ACQ_NAME, erroriRiga, resBundleGenerale);
				}
				if ((StringUtils.isBlank(prezzoRifA)
								|| (StringUtils.isNotBlank(prezzoRifA) && !prezzoRifA.toUpperCase().equals(UNITA_DI_MISURA.toUpperCase())))
								&& StringUtils.isNotBlank(unitaMisuraAcq)) {
					erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.articoli.leavefieldempty").replace("{0}", COL_UNITA_MISURA_ACQ_NAME)
									.replace("{1}", COL_PRZ_UNITARIO_RIFERITO_A_NAME).replace("{2}", UNITA_DI_MISURA));
				}

				Number decUnitaMisuraAcqNumber = ExcelUtils.readNumberFromCell(sheet, rowIndex, COL_DECIMALI_UNITA_MISURA_ACQ - 1, 0);
				if (decUnitaMisuraAcqNumber != null) {
					decUnitaMisuraAcq = decUnitaMisuraAcqNumber.intValue();
				}
				if (StringUtils.isNotBlank(prezzoRifA) && prezzoRifA.toUpperCase().equals(UNITA_DI_MISURA.toUpperCase())) {
					ExcelUtils.checkFieldMandatory(decUnitaMisuraAcq, COL_DECIMALI_UNITA_MISURA_ACQ_NAME, erroriRiga, resBundleGenerale);
				}
				if(decUnitaMisuraAcqNumber != null && (decUnitaMisuraAcq<0 || decUnitaMisuraAcq>5))
                  erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.articoli.decimaliUnitMisura").replace("{0}", COL_DECIMALI_UNITA_MISURA_ACQ_NAME));

				if ((StringUtils.isBlank(prezzoRifA)
								|| (StringUtils.isNotBlank(prezzoRifA) && !prezzoRifA.toUpperCase().equals(UNITA_DI_MISURA.toUpperCase())))
								&& decUnitaMisuraAcqNumber != null) {
					erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.articoli.leavefieldempty").replace("{0}", COL_DECIMALI_UNITA_MISURA_ACQ_NAME)
									.replace("{1}", COL_PRZ_UNITARIO_RIFERITO_A_NAME).replace("{2}", UNITA_DI_MISURA));
					if (decUnitaMisuraAcq != null) {
						ExcelUtils.checkMaxFieldSize(decUnitaMisuraAcq, COL_DECIMALI_UNITA_MISURA_ACQ_NAME, 2, erroriRiga, resBundleGenerale);
						ExcelUtils.checkMinFieldValue(decUnitaMisuraAcq, COL_DECIMALI_UNITA_MISURA_ACQ_NAME, 0, erroriRiga, resBundleGenerale);

					}
				}

				Number qtaUnitaMisuraAcqNumber = ExcelUtils.readNumberFromCell(sheet, rowIndex, COL_QTA_MISURA_ACQ - 1, 5);
				if (qtaUnitaMisuraAcqNumber != null) {
					qtaUnitaMisuraAcq = qtaUnitaMisuraAcqNumber.doubleValue();
				}
				if (StringUtils.isNotBlank(prezzoRifA) && prezzoRifA.toUpperCase().equals(CONFEZIONE.toUpperCase())) {
					ExcelUtils.checkFieldMandatory(qtaUnitaMisuraAcq, COL_QTA_MISURA_ACQ_NAME, erroriRiga, resBundleGenerale);
					if (qtaUnitaMisuraAcq != null) {
						ExcelUtils.checkMinFieldValue(qtaUnitaMisuraAcq, COL_QTA_MISURA_ACQ_NAME, 0, erroriRiga, resBundleGenerale);
					}
				}
				if ((StringUtils.isBlank(prezzoRifA)
								|| (StringUtils.isNotBlank(prezzoRifA) && !prezzoRifA.toUpperCase().equals(CONFEZIONE.toUpperCase())))
								&& qtaUnitaMisuraAcqNumber != null) {
					erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.articoli.leavefieldempty").replace("{0}", COL_QTA_MISURA_ACQ_NAME)
									.replace("{1}", COL_PRZ_UNITARIO_RIFERITO_A_NAME).replace("{2}", CONFEZIONE));
				}

				Number qtaMinPerUnitaMisuraPrzNumber = ExcelUtils.readNumberFromCell(sheet, rowIndex, COL_QTA_MIN_PRZ - 1, 5);
				if (qtaMinPerUnitaMisuraPrzNumber != null) {
					qtaMinPerUnitaMisuraPrz = qtaMinPerUnitaMisuraPrzNumber.doubleValue();
				}
				if (StringUtils.isNotBlank(prezzoRifA) && (prezzoRifA.toUpperCase().equals(UNITA_DI_MISURA.toUpperCase())
				    || prezzoRifA.toUpperCase().equals(PRODOTTO_SERVIZIO.toUpperCase())) && qtaMinPerUnitaMisuraPrz != null) {
					ExcelUtils.checkMinFieldValue(qtaMinPerUnitaMisuraPrz, COL_QTA_MISURA_ACQ_NAME, 0, erroriRiga, resBundleGenerale);
				}
				if ((StringUtils.isBlank(prezzoRifA)
								|| (StringUtils.isNotBlank(prezzoRifA) && (prezzoRifA.toUpperCase().equals(CONFEZIONE.toUpperCase()) ||
								    prezzoRifA.toUpperCase().equals(PRODOTTO_SERVIZIO_UM.toUpperCase()))))
								&& qtaMinPerUnitaMisuraPrzNumber != null) {
					erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.articoli.estremiLottoMinimo").replace("{0}", COL_QTA_MIN_PRZ_NAME)
									.replace("{1}", COL_PRZ_UNITARIO_RIFERITO_A_NAME).replace("{2}", UNITA_DI_MISURA).replace("{3}", PRODOTTO_SERVIZIO));
				}

				Number qtaMaxPerUnitaMisuraPrzNumber = ExcelUtils.readNumberFromCell(sheet, rowIndex, COL_QTA_MAX_PRZ - 1, 5);
				if (qtaMaxPerUnitaMisuraPrzNumber != null) {
					qtaMaxPerUnitaMisuraPrz = qtaMaxPerUnitaMisuraPrzNumber.doubleValue();
				}
				if (StringUtils.isNotBlank(prezzoRifA) && (prezzoRifA.toUpperCase().equals(UNITA_DI_MISURA.toUpperCase())
				    || prezzoRifA.toUpperCase().equals(PRODOTTO_SERVIZIO.toUpperCase())) && qtaMaxPerUnitaMisuraPrz != null) {
					ExcelUtils.checkMinFieldValue(qtaMaxPerUnitaMisuraPrz, COL_QTA_MISURA_ACQ_NAME, 0, erroriRiga, resBundleGenerale);
				}
				if ((StringUtils.isBlank(prezzoRifA)
								|| (StringUtils.isNotBlank(prezzoRifA) && prezzoRifA.toUpperCase().equals(CONFEZIONE.toUpperCase()) ||
								    prezzoRifA.toUpperCase().equals(PRODOTTO_SERVIZIO_UM.toUpperCase())))
								&& qtaMaxPerUnitaMisuraPrzNumber != null) {
					erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.articoli.estremiLottoMinimo").replace("{0}", COL_QTA_MAX_PRZ_NAME)
									.replace("{1}", COL_PRZ_UNITARIO_RIFERITO_A_NAME).replace("{2}", UNITA_DI_MISURA).replace("{3}", PRODOTTO_SERVIZIO));
				}
				if(qtaMinPerUnitaMisuraPrzNumber!=null && qtaMaxPerUnitaMisuraPrzNumber!=null && qtaMaxPerUnitaMisuraPrz<qtaMinPerUnitaMisuraPrz){
				  erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.articoli.confrontoValori").replace("{0}", COL_QTA_MAX_PRZ_NAME)
				      .replace("{1}", COL_QTA_MIN_PRZ_NAME));
				}


				Number tempoMaxConsegnaNumber = ExcelUtils.readNumberFromCell(sheet, rowIndex, COL_TEMPO_MAX_CONSEGNA - 1, 0);
				if (tempoMaxConsegnaNumber != null) {
					tempoMaxConsegna = tempoMaxConsegnaNumber.intValue();
				}
				ExcelUtils.checkFieldMandatory(tempoMaxConsegna, COL_TEMPO_MAX_CONSEGNA_NAME, erroriRiga, resBundleGenerale);

				unitaMisuraTempoConsegna = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_UNITA_MISURA_TEMPO_CONSEGNA - 1));
				ExcelUtils.checkFieldMandatory(unitaMisuraTempoConsegna, COL_UNITA_MISURA_TEMPO_CONSEGNA_NAME, erroriRiga, resBundleGenerale);

				articoloAcqVerdeStr = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_ARTICOLO_ACQ_VERDE - 1));
				if (StringUtils.isNotBlank(tipoArt) && tipoArt.toUpperCase().equals(TIPO_ART_BENE.toUpperCase())) {
					ExcelUtils.checkFieldMandatory(articoloAcqVerdeStr, COL_ARTICOLO_ACQ_VERDE_NAME, erroriRiga, resBundleGenerale);
				} else if ((StringUtils.isBlank(tipoArt)
								|| (StringUtils.isNotBlank(tipoArt) && !tipoArt.toUpperCase().equals(TIPO_ART_BENE.toUpperCase()))) && StringUtils.isNotBlank(articoloAcqVerdeStr)) {
					erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.articoli.leavefieldempty").replace("{0}", COL_ARTICOLO_ACQ_VERDE_NAME)
									.replace("{1}", COL_TIPO_ARTICOLO_NAME).replace("{2}", TIPO_ART_BENE));
				}
				articoloAcqVerde = StringUtils.isNotBlank(articoloAcqVerdeStr) && articoloAcqVerdeStr.toUpperCase().equals(SI)
								? SI_ID : NO_ID;

				prodottoDaVerificareStr = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_PRODOTTO_DA_VERIFICARE - 1));
				if (StringUtils.isNotBlank(tipoArt) && tipoArt.toUpperCase().equals(TIPO_ART_BENE.toUpperCase())) {
					ExcelUtils.checkFieldMandatory(prodottoDaVerificareStr, COL_PRODOTTO_DA_VERIFICARE_NAME, erroriRiga, resBundleGenerale);
				}
				if ((StringUtils.isBlank(tipoArt)
								|| (StringUtils.isNotBlank(tipoArt) && !tipoArt.toUpperCase().equals(TIPO_ART_BENE.toUpperCase()))) && StringUtils.isNotBlank(prodottoDaVerificareStr)) {
					erroriRiga.add(resBundleGenerale.getString("errors.gestoreException.*.articoli.leavefieldempty").replace("{0}", COL_PRODOTTO_DA_VERIFICARE_NAME)
									.replace("{1}", COL_TIPO_ARTICOLO_NAME).replace("{2}", TIPO_ART_BENE));
				}
				prodottoDaVerificare = StringUtils.isNotBlank(prodottoDaVerificareStr) && prodottoDaVerificareStr.toUpperCase().equals(SI)
								? SI_ID : NO_ID;

				note = ExcelUtils.myTrim(ExcelUtils.readStringFromCell(sheet, rowIndex, COL_NOTE - 1));

				stato = ExcelUtils.readStringFromCell(sheet, rowIndex, COL_STATO - 1);
				if (StringUtils.isBlank(stato)) {
					stato = BOZZA;
				}

				if (StringUtils.isBlank(codCat)
								&& StringUtils.isBlank(codArt)
								&& StringUtils.isBlank(tipoArt)
								&& StringUtils.isBlank(desc)
								&& StringUtils.isBlank(descTec)
								&& StringUtils.isBlank(cert)
								&& StringUtils.isBlank(colore)
								&& StringUtils.isBlank(prezzoRifA)
								&& StringUtils.isBlank(unitaMisuraPerPrz)
								&& StringUtils.isBlank(unitaMisuraAcq)
								&& StringUtils.isBlank(unitaMisuraTempoConsegna)
								&& StringUtils.isBlank(note)
								&& gar == null
								&& decUnitaMisuraPerPrz == null
								&& decUnitaMisuraAcq == null
								&& qtaUnitaMisuraAcq == null
								&& qtaMinPerUnitaMisuraPrz == null
								&& qtaMaxPerUnitaMisuraPrz == null
								&& tempoMaxConsegna == null) {
					emptyRow = true;
				}
			} else {
				emptyRow = true;
			}

			if (emptyRow) {
				break;
			} else {

				result.setNumRigheTotaliAnalizzate(numeroRigheProcessate);

				if (!erroriRiga.isEmpty()) {
					result.getRigheConErrore().put(numeroRigaCorrente, erroriRiga);
				} else {

				    if (StringUtils.isNotBlank(prezzoRifA) && prezzoRifA.toUpperCase().equals(PRODOTTO_SERVIZIO_UM.toUpperCase()))
				      qtaUnitaMisuraAcq = new Double(0);

					ImportArticoliBean importBean = new ImportArticoliBean();
					importBean.setIndiceRiga(numeroRigaCorrente);
					importBean.setArticoloAcqVerde(articoloAcqVerde);
					importBean.setCert(cert);
					importBean.setCodArt(codArt);
					importBean.setCodCat(codCat);
					importBean.setColore(colore);
					importBean.setDecUnitaMisuraAcq(decUnitaMisuraAcq == null ? decUnitaMisuraPerPrz : decUnitaMisuraAcq);
					importBean.setDecUnitaMisuraPerPrz(decUnitaMisuraPerPrz);
					importBean.setDesc(desc);
					importBean.setDescTec(descTec);
					importBean.setGar(gar);
					importBean.setNote(note);
					importBean.setOblInsCert(oblInsCert);
					importBean.setOblInsDesAgg(oblInsDesAgg);
					importBean.setOblInsDim(oblInsDim);
					importBean.setOblInsGar(oblInsGar);
					importBean.setOblInsImg(oblInsImg);
					importBean.setOblInsScedTec(oblInsScedTec);
					importBean.setPrezzoRifA(prezzoRifA);
					importBean.setProdottoDaVerificare(prodottoDaVerificare);
					importBean.setQtaMaxPerUnitaMisuraPrz(qtaMaxPerUnitaMisuraPrz);
					importBean.setQtaMinPerUnitaMisuraPrz(qtaMinPerUnitaMisuraPrz);
					importBean.setQtaUnitaMisuraAcq(qtaUnitaMisuraAcq);
					importBean.setTempoMaxConsegna(tempoMaxConsegna);
					importBean.setTipoArt(tipoArt);
					importBean.setUnitaMisuraAcq(unitaMisuraAcq);
					importBean.setUnitaMisuraPerPrz(unitaMisuraPerPrz);
					importBean.setUnitaMisuraTempoConsegna(unitaMisuraTempoConsegna);
					importBean.setStatoArticolo(stato);

					articoli.add(importBean);
				}
			}
		}
		processArticolo(articoli, importConfigBean, result, resBundleGenerale);
		result.setNumRigheErrore(result.getRigheConErrore() != null ? result.getRigheConErrore().values().size() : 0);
		return result;
	}

	private void processArticolo(List<ImportArticoliBean> articoli, ImportArticoliConfigBean importConfigBean,
					ExcelResultBean result, ResourceBundle resBundleGenerale) throws GestoreException {

		String errorCategoriaNotInCais = resBundleGenerale.getString("errors.gestoreException.*.articoli.categorianonincais");
		//String errorCategoriaNotALeaf = resBundleGenerale.getString("errors.gestoreException.*.articoli.categorianonfoglia");
		String errorCategoriaNotFound = resBundleGenerale.getString("errors.gestoreException.*.articoli.categorianontrovatapercodengara");
		//String errorArticoloTrovatoPerCatalogoECodice = resBundleGenerale.getString("errors.gestoreException.*.articoli.articolotrovatopercatalogoecodice");
		String errorInserimentoArticolo = resBundleGenerale.getString("errors.gestoreException.*.articoli.errorinserimentoarticolo");
		boolean unitaMisuraInserita= false;

		for (ImportArticoliBean articolo : articoli) {

			TransactionStatus status;
			boolean doCommit = false;
			Vector categoriaCais;
			Vector categoriaCatalogo;
			Vector articoloCategoria;
			List<String> erroriRiga = new ArrayList<String>();
			Long numeroCategoria;
			Long unitaMisuraPrz;
			Long unitaMisuraAcq;

			try {
				status = this.sqlManager.startTransaction();
			} catch (SQLException ex) {
				throw new GestoreException("Errore inaspettato accorso durante la procedura di import/export excel", "importaesportaexcel.erroreinaspettato", ex);
			}

			try {
				categoriaCais = sqlManager.getVector(SQL_GET_CATEGORIA, new Object[]{articolo.getCodCat()});
				if (categoriaCais == null) {
					erroriRiga.add(errorCategoriaNotInCais.replace("{0}", articolo.getCodCat()));
					result.getRigheConErrore().put(articolo.getIndiceRiga(), erroriRiga);
					continue;
				}

				/*Long isCategoriaNotALeaf = (Long) sqlManager.getObject(SQL_CHECK_CATEGORIA_FOGLIA, new Object[]{articolo.getCodCat(),
				 articolo.getCodCat(), articolo.getCodCat(), articolo.getCodCat()});
				 if (isCategoriaNotALeaf != null) {
				 erroriRiga.add(errorCategoriaNotALeaf.replace("{0}", articolo.getCodCat()));
				 result.getRigheConErrore().put(articolo.getIndiceRiga(), erroriRiga);
				 continue;
				 }*/
				categoriaCatalogo = sqlManager.getVector(SQL_GET_NUMERO_CATEGORIA_CATALOGO, new Object[]{importConfigBean.getCodiceCatalogo(), articolo.getCodCat()});
				if (categoriaCatalogo == null) {
					erroriRiga.add(errorCategoriaNotFound.replace("{0}", articolo.getCodCat()).replace("{1}", importConfigBean.getCodiceCatalogo().toString()));
					result.getRigheConErrore().put(articolo.getIndiceRiga(), erroriRiga);
					continue;
				}

				numeroCategoria = ((JdbcParametro) categoriaCatalogo.get(0)).longValue();
				articolo.setNumeroCategoria(numeroCategoria);

				Long tipoArticolo = (Long) sqlManager.getObject(SQL_GET_TABELLATO_FROM_TAB1,
								new Object[]{articolo.getTipoArt().toUpperCase(), COD_TIPO_ARTICOLO});
				articolo.setTipoArticoloId(tipoArticolo);

				articoloCategoria = sqlManager.getVector(SQL_GET_ARTICOLO_CATEGORIA_PER_CATALOGO_E_COD,
								new Object[]{importConfigBean.getCodiceCatalogo(), articolo.getCodArt().toUpperCase()});
				if (articoloCategoria != null) {
					//erroriRiga.add(errorArticoloTrovatoPerCatalogoECodice.replace("{0}", importConfigBean.getCodiceCatalogo()).replace("{1}", articolo.getCodArt()));
					//result.getRigheConErrore().put(articolo.getIndiceRiga(), erroriRiga);
					result.setNumeroRigheArticoliGiaInCatalogo(result.getNumeroRigheArticoliGiaInCatalogo() + 1);
					if (result.getCodiciArticoliScartati().length() > 0) {
						result.getCodiciArticoliScartati().append(", ");
					}
					result.getCodiciArticoliScartati().append(articolo.getCodArt().toUpperCase());
					continue;
				}

				Long prezzoUnitarioRiferitoA = (Long) sqlManager.getObject(SQL_GET_TABELLATO_FROM_TAB1,
								new Object[]{articolo.getPrezzoRifA().toUpperCase(), COD_PRZ_UNITARIO_RIFERITO_A});
				articolo.setPrezzoUnitarioRiferitoAId(prezzoUnitarioRiferitoA);

				Long tempoConsegna = (Long) sqlManager.getObject(SQL_GET_TABELLATO_FROM_TAB1,
								new Object[]{articolo.getUnitaMisuraTempoConsegna().toUpperCase(), COD_TEMPO_CONSEGNA});
				articolo.setTempoConsegnaId(tempoConsegna);

				Object unitaMisuraPrzObj = sqlManager.getObject(SQL_GET_TABELLATO_FROM_TAB1,
								new Object[]{articolo.getUnitaMisuraPerPrz().toUpperCase(), COD_UNITA_DI_MISURA});
				if (unitaMisuraPrzObj != null) {
					unitaMisuraPrz = (Long) unitaMisuraPrzObj;
				} else {
					unitaMisuraPrz = insertUnitaMisura(articolo.getUnitaMisuraPerPrz());
					unitaMisuraInserita = true;
				}
				articolo.setUnitaMisuraPrzId(unitaMisuraPrz);

				if (StringUtils.isNotBlank(articolo.getUnitaMisuraAcq())) {
					Object unitaMisuraAcqObj = sqlManager.getObject(SQL_GET_TABELLATO_FROM_TAB1,
									new Object[]{articolo.getUnitaMisuraAcq().toUpperCase(), COD_UNITA_DI_MISURA});
					if (unitaMisuraAcqObj != null) {
						unitaMisuraAcq = (Long) unitaMisuraAcqObj;
					} else {
						unitaMisuraAcq = insertUnitaMisura(articolo.getUnitaMisuraAcq());
						unitaMisuraInserita = true;
					}
					articolo.setUnitaMisuraAcqId(unitaMisuraAcq);
				} else {
					articolo.setUnitaMisuraAcqId(unitaMisuraPrz);
				}

				Long statoArticolo = (Long) sqlManager.getObject(SQL_GET_TABELLATO_FROM_TAB1,
								new Object[]{articolo.getStatoArticolo().toUpperCase(), COD_STATO_ARTICOLO});
				articolo.setStatoArticoloId(statoArticolo);

				try {
					int recordInserito = insertArticolo(articolo, importConfigBean);
					if (recordInserito > 0) {
						doCommit = true;
					}
				} catch (Exception ex) {
					erroriRiga.add(errorInserimentoArticolo);
					result.getRigheConErrore().put(articolo.getIndiceRiga(), erroriRiga);
				}

			} catch (SQLException ex) {
				throw new GestoreException("Errore inaspettato accorso durante la procedura di import/export excel", "importaesportaexcel.erroreinaspettato", ex);
			} finally {
				if (status != null) {
					try {
						if (doCommit) {
							this.sqlManager.commitTransaction(status);
							result.setNumRigheSuccesso(result.getNumRigheSuccesso() + 1);
						} else {
							this.sqlManager.rollbackTransaction(status);
						}
					} catch (SQLException ex) {
						throw new GestoreException("Errore inaspettato accorso durante la procedura di import/export excel", "importaesportaexcel.erroreinaspettato", ex);
					}
				}
			}
		}
		if(unitaMisuraInserita){
		    //Chiamata al servizio di sincronizzazione delle unità di misura
	        PortaleAliceProxy proxy = new PortaleAliceProxy();
	        //indirizzo del servizio letto da properties
	        String endPoint = ConfigManager.getValore(CostantiGenerali.PROP_URL_WEB_SERVICE_PORTALE_ALICE);
	        proxy.setEndpoint(endPoint);
	        EsitoOutType risultato;
            try {
              risultato = proxy.sincronizzaUnitaMisura();
              if(!risultato.isEsitoOk()){
                String codiceErrore = risultato.getCodiceErrore();
                //ErroreException e= mew S;
                //throw new GestoreException("Errore durante la sincronizzazione col Portale Appalti",codiceMessaggio,new Object[]{datiMessaggio},e);
                Exception e= new Exception(codiceErrore);
                throw new GestoreException("Errore durante la sincronizzazione col Portale Appalti dell'unità di misura","importaesportaexcel.sincunitamisura",e);
              }
            } catch (RemoteException e1) {
              throw new GestoreException("Errore durante la sincronizzazione col Portale Appalti dell'unità di misura","importaesportaexcel.sincunitamisura",e1);
            }

		}
	}

	private Long insertUnitaMisura(String codiceUnitaMisura) throws GestoreException, SQLException {

		Long nexUnitaMisuraId;
		/*
		if (it.eldasoft.utils.sql.comp.SqlManager.DATABASE_ORACLE.equals(SqlManager.getTipoDB())) {
			nexUnitaMisuraId = (Long) sqlManager.getObject(SQL_GET_MAX_UNITA_DI_MISURA_ID_ORA, new Object[]{COD_UNITA_DI_MISURA});
		} else {
			nexUnitaMisuraId = (Long) sqlManager.getObject(SQL_GET_MAX_UNITA_DI_MISURA_ID_MSQ, new Object[]{COD_UNITA_DI_MISURA});
		}
		*/
		nexUnitaMisuraId = (Long) sqlManager.getObject(SQL_GET_MAX_UNITA_DI_MISURA, new Object[]{COD_UNITA_DI_MISURA});
		nexUnitaMisuraId = nexUnitaMisuraId == null ? 1 : nexUnitaMisuraId + 1;
		//Object[] params = new Object[]{COD_UNITA_DI_MISURA, nexUnitaMisuraId + "", codiceUnitaMisura, null, nexUnitaMisuraId};
		Object[] params = new Object[]{COD_UNITA_DI_MISURA, nexUnitaMisuraId, codiceUnitaMisura, null, nexUnitaMisuraId};
		sqlManager.update(SQL_INSERT_UNITA_DI_MISURA, params);
		return nexUnitaMisuraId;
	}

	private int insertArticolo(ImportArticoliBean articolo, ImportArticoliConfigBean importConfigBean) throws GestoreException, SQLException {

		Long articoloId = new Long(genChiaviManager.getNextId("MEARTCAT"));

		String SQL_INSERT_ARTICOLO = "INSERT INTO meartcat (descrtecn, certifrich, unimistempocons, tipo, obblcertif, datmod, stato "
						+ ",nopega, obbldescagg, przunitper, unimisacq, colore, datins, obbldim, cod, id, descr, chkprod, tempomaxcons "
						+ ",decunimisprz, note, decunimisacq, obblimg, obblgar, qmaxunimis, ngara, qminunimis, qunimisacq, gpp "
						+ ",unimisprz, obblschtecn) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		Object[] params = new Object[]{articolo.getDescTec(),
			articolo.getCert(),
			articolo.getTempoConsegnaId(),
			articolo.getTipoArticoloId(),
			articolo.getOblInsCert(),
			new Date(),
			articolo.getStatoArticoloId(),
			articolo.getNumeroCategoria(),
			articolo.getOblInsDesAgg(),
			articolo.getPrezzoUnitarioRiferitoAId(),
			articolo.getUnitaMisuraAcqId(),
			articolo.getColore(),
			new Date(),
			articolo.getOblInsDim(),
			articolo.getCodArt(),
			articoloId,
			articolo.getDesc(),
			articolo.getProdottoDaVerificare(),
			articolo.getTempoMaxConsegna(),
			articolo.getDecUnitaMisuraPerPrz(),
			articolo.getNote(),
			articolo.getDecUnitaMisuraAcq(),
			articolo.getOblInsImg(),
			articolo.getOblInsGar(),
			articolo.getQtaMaxPerUnitaMisuraPrz(),
			importConfigBean.getCodiceCatalogo(),
			articolo.getQtaMinPerUnitaMisuraPrz(),
			articolo.getQtaUnitaMisuraAcq(),
			articolo.getArticoloAcqVerde(),
			articolo.getUnitaMisuraPrzId(),
			articolo.getOblInsScedTec()};

		return sqlManager.update(SQL_INSERT_ARTICOLO, params);
	}
}
