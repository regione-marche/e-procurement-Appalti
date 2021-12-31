/*
 * Created on 25/giu/09
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
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.excel.DizionarioStiliExcel;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityExcel;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

/**
 * Classe di gestione delle funzionalita' inerenti  l'esportazione e
 * l'importazione in formato Excel per OEPV
 *
 * @author Stefano.Cestaro
 */
public class ImportExportOEPVManager {

	/** Logger */
	static Logger logger = Logger.getLogger(ImportExportOEPVManager.class);

	/** Manager SQL per le operazioni su database */
	private SqlManager sqlManager;

	private GenChiaviManager genChiaviManager;

	private PgManagerEst1 pgManagerEst1;

	private TabellatiManager    tabellatiManager ;

	/** Nome del file associato al file Excel contenente l'export OEPV */
	public static final String NOME_FILE_C0OGGASS_EXPORT = "PunteggiOepvGara.xls";

	/** Titolo del documento associato con cui viene inserito il file Excel
	 * contenente l'export OEPV */
	public static final String TITOLO_FILE_C0OGASS_EXPORT_TECNICI =
			"Criteri valutazione e punteggi tecnici ditte (esportazione)";

	public static final String TITOLO_FILE_C0OGASS_EXPORT_ECONOMICI =
	        "Criteri valutazione e punteggi economici ditte (esportazione)";

	/** Nome del file associato al file Excel contenente l'import OEPV */
	public static final String NOME_FILE_C0OGGASS_IMPORT = "PunteggiOepvGara-import.xls";

	/** Titolo del documento associato con cui viene inserito il file Excel
	 * contenente l'import OEPV */
	public static final String TITOLO_FILE_C0OGASS_IMPORT_TECNICI =
		"Criteri valutazione e punteggi tecnici ditte (importazione)";

	/** Titolo del documento associato con cui viene inserito il file Excel
     * contenente l'import OEPV */
    public static final String TITOLO_FILE_C0OGASS_IMPORT_ECONOMICI =
        "Criteri valutazione e punteggi economici ditte (importazione)";


	/** Definizione fogli del modello XLS */
	private static final String FOGLIO_CRITERI_VALUTAZIONE      = "Criteri";
	private static final String FOGLIO_COMMISSIONE              = "Commissione";
	private static final String FOGLIO_DITTE_PUNTEGGI_TECNICI   = "Punteggi tecnici";
	private static final String FOGLIO_DITTE_PUNTEGGI_ECONOMICI = "Punteggi economici";
	private static final String FOGLIO_DITTE_PUNTEGGI_TOTALI    = "Totali";

	/** Definizione delle righe e delle colonne di partenza */
	private static final int FOGLIO_COMMISSIONE_RIGA_INIZIALE_COMMISSARI = 3;
	private static final int FOGLIO_DITTE_RIGA_INIZIALE_DITTE            = 6;
	private static final int FOGLIO_DITTE_COLONNA_INIZIALE_COEFF_PUNT    = 5; // Colonna E
	private static final int FOGLIO_DITTE_COLONNA_PUNTEGGIO_TOTALE			 = 4; // Colonna D
	private static final int FOGLIO_DITTE_COLONNA_INIZIALE_COEFF_PUNT_ECO    = 7;
    private static final int FOGLIO_DITTE_COLONNA_PUNTEGGIO_TOTALE_ECO           = 6;
    private static final int FOGLIO_DITTE_COLONNA_IMPORTO_ECO               = 4;

	/** Variabile per l'intero foglio EXCEL */
	private HSSFWorkbook workXLS;

	/** Definizione degli indici dei fogli dei punteggu */
    private static final int FOGLIO_PUNTEGGI_TECNICI                = 2;
    private static final int FOGLIO_PUNTEGGI_ECONOMICI              = 3;

	/**
	 * Set SqlManager
	 *
	 * @param sqlManager
	 */
	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
      this.genChiaviManager = genChiaviManager;
	}

      public void setPgManagerEst1(PgManagerEst1 pgManagerEst1) {
        this.pgManagerEst1 = pgManagerEst1;
  }

      public void setTabellatiManager(TabellatiManager tabellatiManager) {
        this.tabellatiManager = tabellatiManager;
      }

	/**
	 * Esportazione in formato XLS
	 *
	 * @param ngara
	 * @param associaXLSDocAss
	 * @param codgar
	 * @param step
	 * @param campo
	 * @throws GestoreException
	 */
	public HSSFWorkbook esportazione(String ngara, boolean associaXLSDocAss,String codgar,String step, String campo)
			throws GestoreException {

	  boolean stepValutazTecniche= true;
      if(step!=null){
        int stepAttuale = Integer.parseInt(step);
        if(stepAttuale==GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE)
          stepValutazTecniche=false;
      }

	  // Apertura del file modello XLS
		String modello = null;
   	try {
   		modello = ConfigManager.getValore("it.eldasoft.importExportOEPV.modelloxls");

   		if (modello == null)
   			throw new GestoreException("Modello non definito",
   					"importaesportaexcel.modellonondefinito",null);

   		FileInputStream file = new FileInputStream(modello);
			this.workXLS = new HSSFWorkbook(file);
			file.close();

		} catch (FileNotFoundException e) {
			throw new GestoreException("Non trovato il file modello",
					"importaesportaexcel.modellonontrovato",new Object[] {modello}, e);

		} catch (IOException e) {
			throw new GestoreException("Errore nell'apertura del file modello",
					"importaesportaexcel.modelloerroreio",new Object[] {modello},e);
		}

		// Creazione del dizionario degli stili delle celle dell'intero file Excel
		DizionarioStiliExcel dizStiliExcel = new DizionarioStiliExcel(this.workXLS);

		// Scrittura della pagina dei dati generali e dei criteri di valutazione
		this.setCriteriValutazione(ngara, dizStiliExcel);

		// Scrittura della pagina della commissione
		this.setCommissione(ngara, dizStiliExcel,codgar);

		if(stepValutazTecniche){
		  // Scrittura della pagina dei punteggi tecnici
		  this.setDittePunteggiTecnici(ngara, dizStiliExcel);
		  //Si nasconde il foglio dei punteggi economici
		  this.workXLS.setSheetHidden(this.FOGLIO_PUNTEGGI_ECONOMICI, true);
		  //this.workXLS.removeSheetAt(this.FOGLIO_PUNTEGGI_ECONOMICI);
		  this.workXLS.setActiveSheet(2);
		}else{
		  // Scrittura della pagina dei punteggi economici
		  this.setDittePunteggiEconomici(ngara, dizStiliExcel,campo);
		  //Si nasconde il foglio dei punteggi tecnici
		  this.workXLS.setSheetHidden(this.FOGLIO_PUNTEGGI_TECNICI, true);
		  //this.workXLS.removeSheetAt(this.FOGLIO_PUNTEGGI_TECNICI);
		}

		// Set del flag per forzare il ricalcolo di tutte le formule presenti nei
		// fogli presenti nel foglio di calcolo all'apertura del file stesso
		for(int sheetNum=0; sheetNum < this.workXLS.getNumberOfSheets(); sheetNum++) {
		    if(!this.workXLS.isSheetHidden(sheetNum)){
  		      HSSFSheet sheet = this.workXLS.getSheetAt(sheetNum);
  		      sheet.setForceFormulaRecalculation(true);
		    }
		}
		//this.workXLS.setActiveSheet(0);
		return this.workXLS;
	}

	/**
	 * Importazione da formato XLS
	 *
	 * @param ngara
	 * @throws GestoreException
	 */
	public void importazione(String ngara, String step, HSSFWorkbook XLS) throws GestoreException {
		this.workXLS = XLS;
		boolean stepValutazTecniche= true;
		if(step!=null){
		  int stepAttuale = Integer.parseInt(step);
		  if(stepAttuale==GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE)
		    stepValutazTecniche=false;
		}

		// Cancellazione preliminare delle righe di G1CRIVAL
		try {
    	String deleteG1CRIVAL = "delete from G1CRIVAL where ngara =? and necvan in( select necvan from goev g where ngara=? " +
				"and necvan1 in (select necvan from goev where ngara=? and tippar=? ))";
    	Object par[] = new Object[4];
    	par[0]= ngara;
    	par[1]= ngara;
    	par[2]= ngara;
		if(stepValutazTecniche)
		  par[3]= new Long(1);
        else
          par[3]= new Long(2);
    	this.sqlManager.update(deleteG1CRIVAL,par);
		} catch (SQLException e) {
			throw new GestoreException("Errore durante la cancellazione della " +
         "tabella dei coefficienti","importaesportaexcel.deleteg1crival",e);
		}

		//Se offtel=1 non si deve cancelllare l'importo offerto
		Long offtel = null;
		try {
		  if(!stepValutazTecniche)
		    offtel = (Long)this.sqlManager.getObject("select offtel from torn,gare where ngara=? and codgar1=codgar", new Object[]{ngara});
        } catch (SQLException e) {
          throw new GestoreException("Errore durante la lettura del campo offtel","importaesportaexcel.letturaOfftel",e);
        }
		// Cancellazione dell'importo offerto
		try {
		  if(((new Long(2)).equals(offtel) || offtel==null) && !stepValutazTecniche){
        	String updateDITG = "update ditg set impoff = null, riboepv = null where ngara5 = ?";
        	this.sqlManager.update(updateDITG,new Object[] {ngara});
          }
		} catch (SQLException e) {
			throw new GestoreException("Errore durante la cancellazione dei " +
          "punteggi tecnici ed economici","importaesportaexcel.updateditg",e);
		}

		if(this.verifichePreliminari(ngara,stepValutazTecniche)){
			// Importazione dei punteggi tecnici
			if(stepValutazTecniche)
		      this.getDittePunteggiTecnici(ngara);

			// Importazione dei punteggi economici
			if(!stepValutazTecniche)
			  this.getDittePunteggiEconomici(ngara,offtel);
		} else {
			throw new GestoreException("Errore durante le verifiche preliminari " +
						"dell'operazione di import dati OPEV",
						"importaesportaexcel.importazioneexcel.verifichePreliminari");
		}
	}

	/**
	 * Verifica preliminare delle informazioni generali della gara: prima di
	 * importare i dati dell'OEPV nella gara, si verificano i valori dei campi
	 * TORN.CODGAR (o GARE.CODGAR1), GARE.NGARA, GARE.MODLICG, V_GARE_TORN.ISLOTTI
	 * e V_GARE_TORN.ISGENERE che in fase di esportazione erano stati salvati
	 * nella riga 3 del foglio 'Punteggi economici'
	 * Ritorna true se le informazioni generali della gara in cui si sta importando
	 * coincidono con quelle presenti nel foglio
	 *
	 * @param ngara
	 * @param stepValutazTecniche
	 * @return Ritorna true se da
	 * @throws GestoreException
	 */
	private boolean verifichePreliminari(String ngara, boolean stepValutazTecniche) throws GestoreException {
		boolean result = true;

		if(logger.isDebugEnabled())
			logger.debug("verifichePreliminari: inizio metodo");

		HSSFRow riga3 = null;
		// Valori dei campi dal foglio dei punteggi economici del file Excel
        String codiceGaraXLS, numeroLottoXLS, modlicgXLS, isLottiXLS;

		if(stepValutazTecniche){
		  HSSFSheet foglioDittePunteggiTecnici = workXLS.getSheet(
              ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_TECNICI);

          if (foglioDittePunteggiTecnici == null || this.workXLS.isSheetHidden(this.FOGLIO_PUNTEGGI_TECNICI)) {
              throw new GestoreException("Non trovato il foglio " +
                  ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_TECNICI,
                  "importaesportaexcel.foglionontrovato",
                  new Object[] { ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_ECONOMICI },
                  null);
          }

          riga3 = foglioDittePunteggiTecnici.getRow(2);
          // Lettura valore TORN.CODGAR o GARE.CODGAR1 dal file Excel
          codiceGaraXLS = UtilityExcel.leggiCellaString(foglioDittePunteggiTecnici, 1, 2);
          // Lettura valore GARE.NGARA dal file Excel
          numeroLottoXLS = UtilityExcel.leggiCellaString(foglioDittePunteggiTecnici, 2, 2);
          // Lettura valore GARE.MODLICG dal file Excel
          modlicgXLS = UtilityExcel.leggiCellaString(foglioDittePunteggiTecnici, 3, 2);
          // Lettura valore V_GARE_TORN.ISLOTTI dal file Excel
          isLottiXLS = UtilityExcel.leggiCellaString(foglioDittePunteggiTecnici, 4, 2);
        }else{
		    // Riferimento al foglio dei punteggi economici, nel quale fra le prime righe
	        // sono stati inseriti i valori dei seguenti campi: TORN.CODGAR, GARE.NGARA,
	        // GARE.MODLICG, V_GARE_TORN.ISLOTTI e V_GARE_TORN.ISGENERE
	        HSSFSheet foglioDittePunteggiEconomici = workXLS.getSheet(
	                ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_ECONOMICI);

	        if (foglioDittePunteggiEconomici == null || this.workXLS.isSheetHidden(this.FOGLIO_PUNTEGGI_ECONOMICI)) {
	            throw new GestoreException("Non trovato il foglio " +
	                ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_ECONOMICI,
	                "importaesportaexcel.foglionontrovato",
	                new Object[] { ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_ECONOMICI },
	                null);
	        }


	        riga3 = foglioDittePunteggiEconomici.getRow(2);

	        // Lettura valore TORN.CODGAR o GARE.CODGAR1 dal file Excel
            codiceGaraXLS = UtilityExcel.leggiCellaString(foglioDittePunteggiEconomici, 1, 2);
            // Lettura valore GARE.NGARA dal file Excel
            numeroLottoXLS = UtilityExcel.leggiCellaString(foglioDittePunteggiEconomici, 2, 2);
            // Lettura valore GARE.MODLICG dal file Excel
            modlicgXLS = UtilityExcel.leggiCellaString(foglioDittePunteggiEconomici, 3, 2);
            // Lettura valore V_GARE_TORN.ISLOTTI dal file Excel
            isLottiXLS = UtilityExcel.leggiCellaString(foglioDittePunteggiEconomici, 4, 2);
        }




		if(riga3 != null && riga3.getPhysicalNumberOfCells() > 0){
			// Valori dei campi da databse
			String codiceGara = null;
			String modlicg = null;
			String isLotti = null;

			// Lettura dei campi da database
			try {
				Vector datiGARE = this.sqlManager.getVector(
						"select CODGAR1, MODLICG from GARE where NGARA = ?",
						new Object[]{ngara});

				boolean continuaConfronto = true;
				if(datiGARE == null || (datiGARE != null && datiGARE.size() == 0)){
					result = false;
					continuaConfronto = false;
				} else {
					codiceGara = SqlManager.getValueFromVectorParam(datiGARE, 0).toString();
					modlicg = SqlManager.getValueFromVectorParam(datiGARE, 1).toString();
				}

				String datiV_GARE_TORN = (String) this.sqlManager.getObject(
						"select ISLOTTI from V_GARE_TORN where codgar = ?",
						new Object[]{codiceGara});

				if(continuaConfronto && datiV_GARE_TORN == null){
					result = false;
					continuaConfronto = false;
				} else {
					isLotti = new String(datiV_GARE_TORN);
				}

				if(continuaConfronto){

					if(! codiceGara.equals(codiceGaraXLS)){
						result = false;
						logger.error("Verifiche premilinari OEPV: avviato import da file " +
								"Excel nella gara '" + codiceGara.replaceFirst("$", "") +
								"', che e' diversa da quella da cui era stato esportato " +
								"(Il file Excel era stato esportato dalla gara '" +
								codiceGaraXLS.replaceFirst("$", "") + "')");
					} else if(! ngara.equals(numeroLottoXLS)){
						result = false;
						logger.error("Verifiche premilinari OEPV: avviato import da file " +
								"Excel dal lotto '" + numeroLottoXLS + "', che e' diversa da " +
								"quello da cui era stato esportato (Il file Excel era stato " +
								"esportato dal lotto '" + ngara + "' della gara '" +
								codiceGara.replaceFirst("$", "") + "')");
					} else if(! "6".equals(modlicg)){  // 6 <==> OEPV
						result = false;
						logger.error("Verifiche premilinari OEPV: avviato import da file " +
								"Excel nella gara '" + codiceGara.replaceFirst("$", "") +
								"' con Criterio di aggiudicazione diverso da 'Offerta " +
								"economicamente più vantaggiosa' (GARE.MODLICG != 6)");
					} else if(! "6".equals(modlicgXLS)){ // 6 <==> OEPV
						// Caso assai poco probabile, visto che, per l'OEPV i file Excel
						// sono protetti dalla modifica
						result = false;
						logger.error("Verifiche premilinari OEPV: avviato import da file " +
								"Excel generato dalla gara '" +
								codiceGaraXLS.replaceFirst("$", "") +
								"' con Criterio di aggiudicazione diverso da 'Offerta " +
								"economicamente più vantaggiosa' (GARE.MODLICG != 6)");
					} else if(! isLotti.equals(isLottiXLS)){
						result = false;
						if("1".equals(isLotti)){
							logger.error("Verifiche premilinari OEPV: avviato import da file " +
									"Excel generato dalla gara divisa in lotti '" +
									codiceGaraXLS.replaceFirst("$", "") + "'");
						} else {
							logger.error("Verifiche premilinari OEPV: avviato import da file " +
									"Excel generato dalla gara a lotto unico '" +
									codiceGaraXLS.replaceFirst("$", "") + "'");
						}
					}
				}
			} catch(SQLException s){
				throw new GestoreException("Errore durante la verifica preliminare " +
						"all'operazione di import: controllo che i dati della gara da cui " +
						"e' stato prodotto il file Excel sia la stessa da cui si sta " +
						"importando", "importaesportaexcel.esportazioneexcel", s);
			}
		} else {
			result = false;
			logger.error("Verifiche premilinari OEPV: avviato import da file " +
					"Excel privo delle informazioni che permettono di effettuare dei " +
					"controlli primo di avviare l'operazione di import dei dati " +
					"(Probabilmente il file excel non e' stato generato della funzione " +
					"di export).");
		}

		if(logger.isDebugEnabled())
			logger.debug("verifichePreliminari: fine metodo");

		return result;
	}

	/**
	 * Lettura dei punteggi tecnici ed inserimento in database
	 *
	 * @param ngara
	 * @throws GestoreException
	 * @throws SQLException
	 */
	private void getDittePunteggiTecnici(String ngara) throws GestoreException {
		// Lettura dei punteggi tecnici
		HSSFSheet foglioDittePunteggiTecnici = workXLS.getSheet(
            ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_TECNICI);

		if (foglioDittePunteggiTecnici == null) {
			throw new GestoreException("Non trovato il foglio " +
                ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_TECNICI,
				"importaesportaexcel.foglionontrovato",
				new Object[] { ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_TECNICI },
				null);
		}

		try {
			String insertG1CRIVAL = "insert into g1crival (id,ngara,necvan,dittao,idcridef,coeffi,punteg) values (?,?,?,?,?,?,?) ";
			int righeTotali;
			righeTotali = foglioDittePunteggiTecnici.getPhysicalNumberOfRows();

			// Riga iniziale da cui inizia la lista delle ditte
			int rigaDitte = ImportExportOEPVManager.FOGLIO_DITTE_RIGA_INIZIALE_DITTE;
			// Colonna iniziale da cui iniziano i coefficienti
			int colonnaCoeffPunt = ImportExportOEPVManager.FOGLIO_DITTE_COLONNA_INIZIALE_COEFF_PUNT;
			long id;
			Long idCridef=null;
			if (righeTotali >= rigaDitte ) {
			  List<String> erroriRiga = new ArrayList<String>();
			  Number number=null;
			  HSSFRow rigaIntestazione = foglioDittePunteggiTecnici.getRow(1);
			    for (int i = rigaDitte; i <= righeTotali; i++) {
					// Codice della ditta - colonna A (1) dalla riga iniziale in poi
					String dittao = UtilityExcel.leggiCellaString(foglioDittePunteggiTecnici,1,i);
					if(dittao==null || "".equals(dittao))
					  break;

					// Aggiornamento dei singoli punteggi tecnici in G1CRIVAL
					int colonneTotali;
					colonneTotali = foglioDittePunteggiTecnici.getRow(i-1).getLastCellNum();
					if (colonneTotali >= colonnaCoeffPunt) {
					  number=null;
					  for (int j = colonnaCoeffPunt; j < colonneTotali; j = j + 2) {
					    //Se la cella è nulla si blocca il ciclo
					    Cell cell = rigaIntestazione.getCell(j);
					    if(cell==null)
					      break;

					    // Valore del campo chiave NECVAN
					    Double necvan =null;
					    number = UtilityExcel.leggiNumero(foglioDittePunteggiTecnici,1,j-1,"Necvan",3,erroriRiga);
						if(number != null)
						  necvan = number.doubleValue();

						// Valore del coefficiente COEFFI
						Double coeffi =null;
						number = UtilityExcel.leggiNumero(foglioDittePunteggiTecnici,i-1,j-1,"Coeffi",9,erroriRiga);
						if(number != null)
						  coeffi = number.doubleValue();

						// Valore del punteggio PUNTEG
						Double punteg=null;
						number = UtilityExcel.leggiNumero(foglioDittePunteggiTecnici,i-1,j,"Punteg",3,erroriRiga);
                        if(number != null)
                          punteg = number.doubleValue();

                        idCridef = (Long)this.sqlManager.getObject("select id from g1cridef where ngara=? and necvan=?", new Object[]{ngara,necvan});
						if (dittao != null && necvan != null && punteg != null && idCridef!=null) {
						  id = this.genChiaviManager.getNextId("G1CRIVAL");
						  this.sqlManager.update(insertG1CRIVAL, new Object[] {new Long(id),ngara, necvan, dittao, idCridef, coeffi, punteg});
						}
					  }
					}
				}
			}
		} catch (SQLException e) {
			throw new GestoreException("Errore durante l'aggiornamento del punteggio","importaesportaexcel.importatecnici",e);
		}
	}

	/**
	 * Lettura dei punteggi economici ed inserimento in database
	 * @param ngara
	 * @param offtel
	 * @throws GestoreException
	 * @throws SQLException
	 */
	private void getDittePunteggiEconomici(String ngara,Long offtel) throws GestoreException {

		// Lettura dei punteggi economici
		HSSFSheet foglioDittePunteggiEconomici = workXLS.getSheet(
				ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_ECONOMICI);

		if (foglioDittePunteggiEconomici == null) {
			throw new GestoreException("Non trovato il foglio " +
				ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_ECONOMICI,
				"importaesportaexcel.foglionontrovato",
				new Object[] { ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_ECONOMICI },
				null);
		}

		String cifreRibasso = null;
		try {
          String codiceGara = (String)this.sqlManager.getObject("select codgar1 from gare where ngara=?", new Object[]{ngara});
          cifreRibasso = this.pgManagerEst1.getNumeroDecimaliRibasso(codiceGara);
        } catch (SQLException e) {
          throw new GestoreException("Errore nel calcolo del numero di cifre decimali per il ribasso della gara = " + ngara,null,e);
        }


		try {


		  String updateDITG = "update ditg set impoff = ?, riboepv = ? where ngara5 = ? and dittao = ? and (partgar <> '2' or partgar is null) and (fasgar >= 6 or fasgar is null)";
			String insertG1CRIVAL = "insert into g1crival (id,ngara,necvan,dittao,idcridef,coeffi,punteg) values (?,?,?,?,?,?,?) ";

			int righeTotali = 0;
			righeTotali = foglioDittePunteggiEconomici.getPhysicalNumberOfRows();

			// Riga iniziale da cui inizia la lista delle ditte
			int rigaDitte = ImportExportOEPVManager.FOGLIO_DITTE_RIGA_INIZIALE_DITTE;
			// Colonna iniziale da cui iniziano i coefficienti
			int colonnaCoeffPunt = ImportExportOEPVManager.FOGLIO_DITTE_COLONNA_INIZIALE_COEFF_PUNT+1;
			String campoVisibile="";
	        HSSFRow riga = foglioDittePunteggiEconomici.getRow(4);
	        if(riga!=null){
	          //Nella terza colonna del file (nascosta) in corrispondenza delle colonne dell'importo offerto e del
	          //ribasso c'è l'info di quale colonna è visualizzata nel file excel
	          String visImporto = UtilityExcel.leggiCellaString(foglioDittePunteggiEconomici, 4, 4);
	          String visRibasso = UtilityExcel.leggiCellaString(foglioDittePunteggiEconomici, 5, 4);
	          if("1".equals(visImporto)){
	            campoVisibile="impoff";
	            colonnaCoeffPunt = ImportExportOEPVManager.FOGLIO_DITTE_COLONNA_INIZIALE_COEFF_PUNT_ECO;
	          }else if("1".equals(visRibasso)){
	            campoVisibile="riboepv";
	            colonnaCoeffPunt = ImportExportOEPVManager.FOGLIO_DITTE_COLONNA_INIZIALE_COEFF_PUNT_ECO;
	          }
	        }

			long id;
            Long idCridef=null;

			if (righeTotali >= rigaDitte ) {
			    List<String> erroriRiga = new ArrayList<String>();
			    Number number=null;
			    HSSFRow rigaIntestazione = foglioDittePunteggiEconomici.getRow(1);
			    Double impoff = null;
			    Double riboepv = null;

			    for (int i = rigaDitte; i <= righeTotali; i++) {
					// Codice della ditta - colonna A (1) dalla riga iniziale in poi
					String dittao = UtilityExcel.leggiCellaString(foglioDittePunteggiEconomici,1,i);

					//Nel caso di codice ditta nullo si esce dal ciclo
					if(dittao==null || "".equals(dittao))
					  break;

					impoff = null;
					riboepv = null;

					if(!(new Long(1)).equals(offtel)){
					  if("impoff".equals(campoVisibile) || "".equals(campoVisibile)){
    					number = UtilityExcel.leggiNumero(foglioDittePunteggiEconomici,i-1,3,"Impoff",5,erroriRiga);
    					if(number != null){
    					  impoff = number.doubleValue();
    					  riboepv = this.pgManagerEst1.calcoloRibassoDaImpoff(ngara, impoff,cifreRibasso);
    					}
    				  }else{
					    number = UtilityExcel.leggiNumero(foglioDittePunteggiEconomici,i-1,4,"riboepv",new Integer(cifreRibasso),erroriRiga);
                        if(number != null){
                          riboepv = new Double(number.doubleValue() * -1);
                          impoff = this.pgManagerEst1.calcoloImpoffDaRibasso(ngara, riboepv);
                        }

                      }

					  if (dittao != null  && !(new Long(1)).equals(offtel)) {
                        this.sqlManager.update(updateDITG, new Object[]{impoff, riboepv, ngara, dittao});
					  }
			        }

					// Aggiornamento dei singoli punteggi tecnici in DPUN
					int colonneTotali;
					colonneTotali = foglioDittePunteggiEconomici.getRow(i-1).getLastCellNum();
					if (colonneTotali >= colonnaCoeffPunt) {
						for (int j = colonnaCoeffPunt; j < colonneTotali; j = j + 2) {
						  //Se la cella è nulla si blocca il ciclo
	                      Cell cell = rigaIntestazione.getCell(j);
	                      if(cell==null)
	                        break;

						  // Valore del campo chiave NECVAN
						  Double necvan=null;
						  number = UtilityExcel.leggiNumero(foglioDittePunteggiEconomici,1,j-1,"Necvan",3,erroriRiga);
						  if(number != null)
						    necvan = number.doubleValue();

						  // Valore del coefficiente COEFFI
						  Double coeffi =null;
						  number = UtilityExcel.leggiNumero(foglioDittePunteggiEconomici,i-1,j-1,"Coeffi",9,erroriRiga);
                          if(number != null)
                            coeffi = number.doubleValue();

						  // Valore del punteggio PUNTEG
						  Double punteg = null;
                          number = UtilityExcel.leggiNumero(foglioDittePunteggiEconomici,i-1,j,"Punteg",3,erroriRiga);
                          if(number != null)
                            punteg = number.doubleValue();

                          idCridef = (Long)this.sqlManager.getObject("select id from g1cridef where ngara=? and necvan=?", new Object[]{ngara,necvan});
						  if (dittao != null && necvan != null && punteg != null && idCridef!=null) {
						    id = this.genChiaviManager.getNextId("G1CRIVAL");
						    this.sqlManager.update(insertG1CRIVAL, new Object[] {new Long(id),ngara, necvan, dittao, idCridef, coeffi, punteg});
						  }
						}
					}
				}
			}
		} catch (SQLException e) {
			throw new GestoreException("Errore durante l'aggiornamento del punteggio",
					"importaesportaexcel.importaeconomici",e);
		}
	}

	/**
	 * Scrive i dati generali ed i criteri di valutazione
	 * all'interno del foglio "Criteri valutazione"
	 * @param ngara
	 * @throws GestoreException
	 */
	private void setCriteriValutazione(String ngara, DizionarioStiliExcel dizStiliExcel)
			throws GestoreException {
		HSSFSheet foglioCriteriValutazione = this.workXLS.getSheet(
				ImportExportOEPVManager.FOGLIO_CRITERI_VALUTAZIONE);
		if (foglioCriteriValutazione == null) {
			throw new GestoreException("Non trovato il foglio " +
				ImportExportOEPVManager.FOGLIO_CRITERI_VALUTAZIONE,
				"importaesportaexcel.foglionontrovato",
				new Object[] { ImportExportOEPVManager.FOGLIO_CRITERI_VALUTAZIONE },
				null);
		}

		// Dati generali della gara
		Double mineco = null;
		Double mintec = null;
		String selectGARE = "select codcig,not_gar,impapp from gare where ngara = ?";
		try {
			List datiGare = sqlManager.getListVector(selectGARE,new Object[]{ngara});
			if (datiGare != null && datiGare.size()> 0) {
				//Codice Alice - colonna D(4) riga 3
			    UtilityExcel.scriviCella(foglioCriteriValutazione, 4, 3, ngara,
                  dizStiliExcel.getStileExcel(
                          DizionarioStiliExcel.STRINGA_ALIGN_LEFT));

			    //Codice CIG - colonna D(4) riga 4
			    String codcig = SqlManager.getValueFromVectorParam(datiGare.get(0),0).toString();
                if (codcig != null)
                  UtilityExcel.scriviCella(foglioCriteriValutazione, 4, 4, codcig,
                      dizStiliExcel.getStileExcel(
                          DizionarioStiliExcel.STRINGA_ALIGN_LEFT));

			    // Titolo della gara - colonna D (4) riga 5
				String not_gar = SqlManager.getValueFromVectorParam(datiGare.get(0),1).toString();
				if (not_gar != null)
					UtilityExcel.scriviCella(foglioCriteriValutazione, 4, 5, not_gar,
							dizStiliExcel.getStileExcel(
									DizionarioStiliExcel.STRINGA_ALIGN_LEFT));

				// Importo base d'asta - colonna D (4) riga 6
				Double impapp = SqlManager.getValueFromVectorParam(datiGare.get(0),2).doubleValue();
				if (impapp != null)
					UtilityExcel.scriviCella(foglioCriteriValutazione, 4, 6, impapp,
							dizStiliExcel.getStileExcel(
									DizionarioStiliExcel.DECIMALE2_ALIGN_RIGHT));

			}

			//Lettura dei dati della tabella GARE1
			Vector datiGare1 = sqlManager.getVector("select mintec, mineco from gare1 where ngara = ?", new Object[]{ngara});
		      if(datiGare1!=null && datiGare1.size()>0){
		        mintec = (Double)((JdbcParametro) datiGare1.get(0)).getValue();
		        mineco = (Double)((JdbcParametro) datiGare1.get(1)).getValue();
		      }
		} catch (SQLException e) {
			throw new GestoreException("Errore nell'esportazione dei dati generali " +
					"della gara", "importaesportaexcel.datigenerali",e);
		}




		// Criteri di valutazione
		String selectGOEV = "select norpar, norpar1, tippar, despar, maxpun, livpar, minpun from goev where ngara = ? order by norpar, necvan1, norpar1, necvan";
		try {
			List datiGOEV = sqlManager.getListVector(selectGOEV,new Object[]{ngara});
			int rigaInizialeCriterio = 11;
			Double totalePunteggi = new Double(0);
			Double totalePunteggiTec = new Double(0);
			Double totalePunteggiEco = new Double(0);

			if (datiGOEV != null && datiGOEV.size() > 0) {
				for (int i = 0; i < datiGOEV.size(); i++) {
					// Livello criterio
					Long livpar = SqlManager.getValueFromVectorParam(datiGOEV.get(i),5).longValue();

					// Numero d'ordine criterio - colonna A (1) dalla riga 11 in poi
					Double norpar = SqlManager.getValueFromVectorParam(datiGOEV.get(i),0).doubleValue();
					String norpartoString = UtilityNumeri.convertiDouble(norpar);

					// Numero d'ordine subcriterio - colonna B (2) dalla riga 11 in poi
					Double norpar1 = SqlManager.getValueFromVectorParam(datiGOEV.get(i),1).doubleValue();
					String norpar1toString = UtilityNumeri.convertiDouble(norpar1);

					if (livpar != null) {
						if ((new Long(1)).equals(livpar) || (new Long(3)).equals(livpar)) {
							if (norpar != null)
								UtilityExcel.scriviCella(foglioCriteriValutazione, 1,
									rigaInizialeCriterio + i, norpartoString,
											dizStiliExcel.getStileExcel(
													DizionarioStiliExcel.STRINGA_ALIGN_CENTER));
						}

						if ((new Long(2)).equals(livpar)) {
							if (norpar1 != null)
								UtilityExcel.scriviCella(foglioCriteriValutazione, 2,
										rigaInizialeCriterio + i, norpar1toString,
												dizStiliExcel.getStileExcel(
														DizionarioStiliExcel.STRINGA_ALIGN_CENTER));
						}
					}

					// Tipo criterio - colonna C (3) dalla riga 11 in poi
					Long tippar = SqlManager.getValueFromVectorParam(datiGOEV.get(i),2).longValue();
					if (tippar != null) {
						if ((new Long(1)).equals(tippar))
							UtilityExcel.scriviCella(foglioCriteriValutazione, 3,
									rigaInizialeCriterio + i, "Tecnico",
											dizStiliExcel.getStileExcel(
													DizionarioStiliExcel.STRINGA_ALIGN_CENTER));
						if ((new Long(2)).equals(tippar))
							UtilityExcel.scriviCella(foglioCriteriValutazione, 3,
									rigaInizialeCriterio + i, "Economico",
											dizStiliExcel.getStileExcel(
													DizionarioStiliExcel.STRINGA_ALIGN_CENTER));
					}

					// Descrizione - colonna D (4) dalla riga 11 in poi
					String despar = SqlManager.getValueFromVectorParam(datiGOEV.get(i),3).toString();
					if (despar != null)
						UtilityExcel.scriviCella(foglioCriteriValutazione, 4,
								rigaInizialeCriterio + i, despar,
										dizStiliExcel.getStileExcel(
												DizionarioStiliExcel.STRINGA_ALIGN_LEFT));

					// Punteggio massimo - colonna E (5) dalla riga 11 in poi
					Double maxpun = SqlManager.getValueFromVectorParam(datiGOEV.get(i),4).doubleValue();
					if (maxpun != null) {

						// La somma del totale punteggi deve essere fatta solo per i criteri
						if (livpar != null) {
							if ((new Long(1)).equals(livpar) || (new Long(3)).equals(livpar) ) {
								totalePunteggi = new Double(totalePunteggi.doubleValue() + maxpun.doubleValue());
							}
						}
						if(tippar!=null && tippar.longValue()==1)
						  totalePunteggiTec = new Double(totalePunteggiTec.doubleValue() + maxpun.doubleValue());
						else if(tippar!=null && tippar.longValue()==2)
						  totalePunteggiEco = new Double(totalePunteggiEco.doubleValue() + maxpun.doubleValue());

						UtilityExcel.scriviCella(foglioCriteriValutazione, 5,
								rigaInizialeCriterio + i, maxpun,
										dizStiliExcel.getStileExcel(
												DizionarioStiliExcel.DECIMALE5_ALIGN_CENTER));
					}

					// Soglia minima - colonna F (6) dalla riga 11 in poi
                    Double minpun = SqlManager.getValueFromVectorParam(datiGOEV.get(i),6).doubleValue();
                    if (minpun != null) {

                        UtilityExcel.scriviCella(foglioCriteriValutazione, 6,
                                rigaInizialeCriterio + i, minpun,
                                        dizStiliExcel.getStileExcel(
                                                DizionarioStiliExcel.DECIMALE5_ALIGN_CENTER));
                    }
				}

				// Descrizione totale tecnico- colonna D (4)
                UtilityExcel.scriviCella(foglioCriteriValutazione, 4,
                        rigaInizialeCriterio + datiGOEV.size(), "Totale tecnico",
                                dizStiliExcel.getStileExcel(
                                        DizionarioStiliExcel.STRINGA_ALIGN_LEFT));

                // Valore del totale tecnico - colonna E (5)
                UtilityExcel.scriviCella(foglioCriteriValutazione, 5,
                        rigaInizialeCriterio + datiGOEV.size(), totalePunteggiTec,
                                dizStiliExcel.getStileExcel(
                                        DizionarioStiliExcel.DECIMALE5_ALIGN_CENTER));

                //Valore soglia minima tecnica complessivo - colonna F(6)
                UtilityExcel.scriviCella(foglioCriteriValutazione, 6,
                    rigaInizialeCriterio + datiGOEV.size(), mintec,
                            dizStiliExcel.getStileExcel(
                                    DizionarioStiliExcel.DECIMALE5_ALIGN_CENTER));


                // Descrizione totale economico- colonna D (4)
                UtilityExcel.scriviCella(foglioCriteriValutazione, 4,
                        rigaInizialeCriterio + datiGOEV.size() + 1, "Totale economico",
                                dizStiliExcel.getStileExcel(
                                        DizionarioStiliExcel.STRINGA_ALIGN_LEFT));

                // Valore del totale economico - colonna E (5)
                UtilityExcel.scriviCella(foglioCriteriValutazione, 5,
                        rigaInizialeCriterio + datiGOEV.size() + 1, totalePunteggiEco,
                                dizStiliExcel.getStileExcel(
                                        DizionarioStiliExcel.DECIMALE5_ALIGN_CENTER));

                //Valore soglia minima economico complessivo - colonna F(6)
                UtilityExcel.scriviCella(foglioCriteriValutazione, 6,
                    rigaInizialeCriterio + datiGOEV.size() + 1, mineco,
                            dizStiliExcel.getStileExcel(
                                    DizionarioStiliExcel.DECIMALE5_ALIGN_CENTER));

				// Descrizione totale - colonna D (4)
				UtilityExcel.scriviCella(foglioCriteriValutazione, 4,
						rigaInizialeCriterio + datiGOEV.size()+ 2, "Totale",
								dizStiliExcel.getStileExcel(
										DizionarioStiliExcel.STRINGA_ALIGN_LEFT));

				// Valore del totale - colonna E (5)
				UtilityExcel.scriviCella(foglioCriteriValutazione, 5,
						rigaInizialeCriterio + datiGOEV.size() + 2, totalePunteggi,
								dizStiliExcel.getStileExcel(
										DizionarioStiliExcel.DECIMALE5_ALIGN_CENTER));

			}

		} catch (SQLException e) {
			throw new GestoreException("Errore nell'esportazione dei criteri di " +
					"valutazione", "importaesportaexcel.criterivalutazione",e);
		}
	}

	/**
	 * Scrive la lista dei commissari di gara
	 * all'interno del foglio "Commissari"
	 * @param ngara
	 * @throws GestoreException
	 */
	private void setCommissione(String ngara, DizionarioStiliExcel dizStiliExcel,String codgar)
		throws GestoreException {

		HSSFSheet foglioCommissari = workXLS.getSheet(ImportExportOEPVManager.FOGLIO_COMMISSIONE);

		if (foglioCommissari == null) {
			throw new GestoreException("Non trovato il foglio " +
					ImportExportOEPVManager.FOGLIO_COMMISSIONE,
					"importaesportaexcel.foglionontrovato",
					new Object[] { ImportExportOEPVManager.FOGLIO_COMMISSIONE },
					null);
		}

		// Lista dei commissari
		String selectGFOF = "select codfof, nomfof from gfof where ngara2 = ? order by nomfof";
		//CF occorre distinguere se Offerta Unica o le altre modalità
		String codice;
        if (codgar!=null){
          codice=codgar;
        }else{
          codice=ngara;
        }
		try {
		    List datiGFOF = sqlManager.getListVector(selectGFOF,new Object[]{codice});
			int rigaIniziale = ImportExportOEPVManager.FOGLIO_COMMISSIONE_RIGA_INIZIALE_COMMISSARI;

			if (datiGFOF != null && datiGFOF.size() > 0) {
				for (int i = 0; i < datiGFOF.size(); i++) {
					// Codice tecnico - colonna A (1) dalla riga iniziale in poi
					String codfof = SqlManager.getValueFromVectorParam(datiGFOF.get(i),0).toString();
					if (codfof != null)
						UtilityExcel.scriviCella(foglioCommissari, 1, rigaIniziale + i,
								codfof, dizStiliExcel.getStileExcel(
										DizionarioStiliExcel.STRINGA_ALIGN_CENTER));

					// Nome tecnico - colonna B (2) dalla riga iniziale in poi
					String nomfof = SqlManager.getValueFromVectorParam(datiGFOF.get(i),1).toString();
					if (nomfof != null)
						UtilityExcel.scriviCella(foglioCommissari, 2, rigaIniziale + i,
								nomfof, dizStiliExcel.getStileExcel(
										DizionarioStiliExcel.STRINGA_ALIGN_CENTER));
				}
			}
		} catch (SQLException e) {
			throw new GestoreException("Errore nell'esportazione dei commissari",
					"importaesportaexcel.commissari",e);
		}
	}

	/**
	 * Scrive la lista delle ditte con i relativi punteggi tecnici
	 * all'interno del foglio "Ditte e punteggi tecnici"
	 * @param ngara
	 * @throws GestoreException
	 */
	private void setDittePunteggiTecnici(String ngara, DizionarioStiliExcel dizStiliExcel)
		throws GestoreException {
		HSSFSheet foglioDittePunteggiTecnici = workXLS.getSheet(
				ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_TECNICI);

		if (foglioDittePunteggiTecnici == null) {
			throw new GestoreException("Non trovato il foglio " +
				ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_TECNICI,
				"importaesportaexcel.foglionontrovato",
				new Object[] { ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_TECNICI },
				null);
		}

		this.setDittePunteggi(ngara, foglioDittePunteggiTecnici, new Long(1),
				dizStiliExcel,false,null);

		// Scrittura delle informazioni principali della gara nelle prime righe del
        // foglio dei punteggi tecnici. Tali dati verranno letti e verificati in
        // fase di import. Fra le informazioni principale della gara ci sono:
        // TORN.CODGAR o GARE.CODGAR1, GARE.NGARA, GARE.MODLICG, V_GARE_TORN.ISLOTTI
        // e V_GARE_TORN.ISGENERE
        this.setDatiPrincipaliGara(ngara, foglioDittePunteggiTecnici, dizStiliExcel,null);
	}

	/**
	 * Scrive le intestazioni di colonna per i vari criteri di valutazione
	 * @param ngara
	 * @param foglioDittePunteggiTecnici
	 * @param tippar
	 * @param bloccoImporto
	 * @param campoVisibile
	 * @throws GestoreException
	 */
	private void setDittePunteggi(String ngara,	HSSFSheet foglioDittePunteggi,
			Long tippar, DizionarioStiliExcel dizStiliExcel, boolean bloccoImporto, String campoVisibile) throws GestoreException {

		int numeroRigaIniziale = ImportExportOEPVManager.FOGLIO_DITTE_RIGA_INIZIALE_DITTE;
		int numeroRigaFinale = 0;

		numeroRigaFinale = this.setDitte(ngara, foglioDittePunteggi, dizStiliExcel,tippar,campoVisibile);

		try {
			//Lettura dai Gare1
  		    Double mintec = null;
  		    Double mineco= null;
		    Vector datiGare1 = sqlManager.getVector("select mintec, mineco from gare1 where ngara = ?", new Object[]{ngara});
  		    if(datiGare1!=null && datiGare1.size()>0){
  	          mintec = (Double)((JdbcParametro) datiGare1.get(0)).getValue();
  	          mineco = (Double)((JdbcParametro) datiGare1.get(1)).getValue();
  		    }
  		    String mintecString = UtilityNumeri.convertiDouble(mintec);
  		    String minecotoString = UtilityNumeri.convertiDouble(mineco);
  		    Double maxpunTotale = new Double(0);

		    // Inserimento dei titoli di colonna
			// String selectGOEV = "select norpar, norpar1, necvan, livpar from goev where ngara = ? and tippar = ? order by norpar, norpar1";
			String selectGOEV = "select norpar, norpar1, necvan, livpar, despar, maxpun, minpun from goev a where ngara = ? " +
					" and a.necvan1 in (select b.necvan from goev b where b.tippar = ? and a.ngara = b.ngara) " +
					" order by norpar, necvan1, norpar1, necvan";

			List datiGOEV = sqlManager.getListVector(selectGOEV,new Object[]{ngara,tippar});

			// Colonna iniziale coefficienti e punteggi
			int colonnaImportoTotale = 0;
			int colonnaCoeffPunt;
			int indiceIntestazionePunteggio;
			if(new Long(2).equals(tippar)){
              colonnaCoeffPunt = FOGLIO_DITTE_COLONNA_INIZIALE_COEFF_PUNT_ECO;
              colonnaImportoTotale = FOGLIO_DITTE_COLONNA_IMPORTO_ECO;
              indiceIntestazionePunteggio = FOGLIO_DITTE_COLONNA_PUNTEGGIO_TOTALE_ECO;
			}else{
			  colonnaCoeffPunt = ImportExportOEPVManager.FOGLIO_DITTE_COLONNA_INIZIALE_COEFF_PUNT;

			  indiceIntestazionePunteggio = FOGLIO_DITTE_COLONNA_PUNTEGGIO_TOTALE;
			}

			// Copia lo stile dalla cella A1
			HSSFCellStyle styleA1 = UtilityExcel.getStyleFromCell(foglioDittePunteggi, 1, 1);

			if(new Long(2).equals(tippar)){
			  //Si nasconde o IMPOFF o RIBOEPV
			  if("IMPOFF".equals(campoVisibile)){
			    UtilityExcel.setLarghezzaColonna(foglioDittePunteggi,5,0);
			  }else{
			    UtilityExcel.setLarghezzaColonna(foglioDittePunteggi,4,0);
			    UtilityExcel.setFormatoCelleColonna(foglioDittePunteggi,5,   numeroRigaIniziale, numeroRigaFinale,
                    dizStiliExcel.getStileExcel(DizionarioStiliExcel.DECIMALE9_ALIGN_CENTER_UNLOCKED));
			  }
    		}

			// Crea l'oggetto HSSFPatriarch
			HSSFPatriarch patriarch = foglioDittePunteggi.createDrawingPatriarch();

			if (datiGOEV != null && datiGOEV.size()>0) {
				if (new Long(1).equals(tippar)) {
					UtilityExcel.scriviCella(foglioDittePunteggi, indiceIntestazionePunteggio, 1,
							"Punt.tot. Tecnico", styleA1);
				} else {
					UtilityExcel.scriviCella(foglioDittePunteggi, indiceIntestazionePunteggio, 1,
							"Punt.tot. Economico", styleA1);

					if(colonnaImportoTotale != 0 && "IMPOFF".equals(campoVisibile))
						UtilityExcel.scriviCella(foglioDittePunteggi, colonnaImportoTotale, 1,
							"Importo offerto", styleA1);
				}

				if(colonnaImportoTotale != 0){


				    if(bloccoImporto){
				      //Imposto il valore 1 nella cella nascosta D3 che si adopera per la formattazione
				      //condizionale della colonna degli importi
				      UtilityExcel.scriviCella(foglioDittePunteggi, colonnaImportoTotale, 3, "1",
                          dizStiliExcel.getStileExcel(
                                  DizionarioStiliExcel.STRINGA_ALIGN_CENTER));
				    }

				    if( "IMPOFF".equals(campoVisibile)){
    				    UtilityExcel.setFormatoCelleColonna(foglioDittePunteggi, colonnaImportoTotale,
    							numeroRigaIniziale, numeroRigaFinale,	dizStiliExcel.getStileExcel(
    		                        DizionarioStiliExcel.DECIMALE5_ALIGN_CENTER_UNLOCKED));
    					UtilityExcel.setLarghezzaColonna(foglioDittePunteggi, colonnaImportoTotale,13);
				    }
				}

				UtilityExcel.setFormatoCelleColonna(foglioDittePunteggi, indiceIntestazionePunteggio,
						numeroRigaIniziale, numeroRigaFinale,	dizStiliExcel.getStileExcel(
								DizionarioStiliExcel.DECIMALE3_ALIGN_CENTER_UNLOCKED));
				UtilityExcel.setLarghezzaColonna(foglioDittePunteggi, indiceIntestazionePunteggio,13);

				// Stringa per determinare la formula che calcoli il punteggio totale
				// come somma dei punteggi dei vari criteri
				StringBuffer strFormulaPunteggioTotale = new StringBuffer("");
				// Stringa per determinare la formula che calcoli il punteggio del criterio
				// come somma dei punteggi dei vari sub criteri
				StringBuffer strFormulaPunteggio = new StringBuffer("");
				// Variabile per settare il numero della colonna in cui scrivere la
				// formula per determinare il punteggio di un criterio in funzione del
				// punteggio dei vari subcriteri
				int colonnaPunteggio = 0;

				for (int i = 0; i < datiGOEV.size(); i++) {
					Double norpar = SqlManager.getValueFromVectorParam(datiGOEV.get(i),0).doubleValue();
					String norpartoString = UtilityNumeri.convertiDouble(norpar);

					Double norpar1 = SqlManager.getValueFromVectorParam(datiGOEV.get(i),1).doubleValue();
					String norpar1toString = UtilityNumeri.convertiDouble(norpar1);

					Long necvan = SqlManager.getValueFromVectorParam(datiGOEV.get(i),2).longValue();
					Long livpar = SqlManager.getValueFromVectorParam(datiGOEV.get(i),3).longValue();

					String commento = SqlManager.getValueFromVectorParam(datiGOEV.get(i),4).toString();
					Double maxpun = SqlManager.getValueFromVectorParam(datiGOEV.get(i),5).doubleValue();
					String maxpuntoString = UtilityNumeri.convertiDouble(maxpun);
					Double minpun = SqlManager.getValueFromVectorParam(datiGOEV.get(i),6).doubleValue();
                    String minpuntoString = UtilityNumeri.convertiDouble(minpun);

                    String commentoNote = commento + " (";
					commento += " (" + maxpuntoString + " pt)";
                    /*
					commentoNote += " (" + maxpuntoString;
                    if(minpuntoString!= null && !"".equals(minpuntoString))
                      commentoNote += " - " + minpuntoString;
                    commentoNote += " pt)";
                    */
					if(minpuntoString!= null && !"".equals(minpuntoString))
					  commentoNote += "p.min " + minpuntoString + " - ";
					commentoNote += "p.max " + maxpuntoString + ")";

					// I valori previsti per il campo LIVPAR.GOEV sono:
					// -	1 = criterio senza sub-criteri
					// -	2 = sub-criterio
					// -	3 = criterio con sub-criteri
					if (livpar != null) {
						if ((new Long(1)).equals(livpar) || (new Long(3)).equals(livpar)) {
						  maxpunTotale = new Double(maxpunTotale.doubleValue() + maxpun.doubleValue());
						    if (norpar != null) {

								if(strFormulaPunteggio.length() > 0 && colonnaPunteggio > 0){
									String tmpStrFormulaPunteggio = "ROUND(" + strFormulaPunteggio.toString() + ", 3)";
									for(int l=numeroRigaIniziale; l < numeroRigaFinale; l++){
										UtilityExcel.scriviFormulaCella(foglioDittePunteggi,
												colonnaPunteggio,	l,
												tmpStrFormulaPunteggio.replaceAll("numeroRiga",	"" + l),
												dizStiliExcel.getStileExcel(
														DizionarioStiliExcel.DECIMALE3_ALIGN_CENTER_UNLOCKED));
									}

									// Reset della formula per determinare il punteggio del criterio
									// in funzione dei punteggi dei subcriteri
									strFormulaPunteggio = new StringBuffer("");
									// Reset della colonna in cui scrivere la formula per
									// determinare il punteggio di un criterio in funzione del
									// punteggio dei vari subcriteri
									colonnaPunteggio = 0;
								}

								// Colonna Coefficiente
								if( i > 0)
								  colonnaCoeffPunt++;
								UtilityExcel.scriviCella(foglioDittePunteggi, colonnaCoeffPunt, 1,
										"Coeff. " + norpartoString, styleA1);
								UtilityExcel.scriviCommento(patriarch, colonnaCoeffPunt, 1,
								    commentoNote, workXLS.createFont());
								UtilityExcel.scriviCella(foglioDittePunteggi, colonnaCoeffPunt, 2, necvan,
										dizStiliExcel.getStileExcel(
												DizionarioStiliExcel.INTERO_ALIGN_RIGHT));
								UtilityExcel.scriviCella(foglioDittePunteggi, colonnaCoeffPunt, 3, "C",
										dizStiliExcel.getStileExcel(
												DizionarioStiliExcel.STRINGA_ALIGN_CENTER));
								UtilityExcel.scriviCella(foglioDittePunteggi, colonnaCoeffPunt, 4,
										maxpun, dizStiliExcel.getStileExcel(
												DizionarioStiliExcel.DECIMALE9_ALIGN_CENTER_UNLOCKED));
								UtilityExcel.setFormatoCelleColonna(foglioDittePunteggi,
										colonnaCoeffPunt,	numeroRigaIniziale, numeroRigaFinale,
										dizStiliExcel.getStileExcel(
												DizionarioStiliExcel.DECIMALE9_ALIGN_CENTER_UNLOCKED));
								UtilityExcel.setLarghezzaColonna(foglioDittePunteggi,
										colonnaCoeffPunt, 0);

								// Set della formula che calcola il coefficiente in funzione del
								// punteggio massimo associato al criterio
								// coefficiente = punteggio / punteggio massimo
								String formulaCoefficiente = "IF(" + UtilityExcel.conversioneNumeroColonna(colonnaCoeffPunt) + "4>0,ROUND(" +
									(UtilityExcel.conversioneNumeroColonna(colonnaCoeffPunt+1)) +
									"numeroRiga/" +
									UtilityExcel.conversioneNumeroColonna(colonnaCoeffPunt) + "4, 9),0)";
								for(int j=numeroRigaIniziale; j < numeroRigaFinale; j++){
									UtilityExcel.scriviFormulaCella(foglioDittePunteggi,
											colonnaCoeffPunt,	j,
											formulaCoefficiente.replaceAll("numeroRiga", "" + j),
											dizStiliExcel.getStileExcel(
													DizionarioStiliExcel.DECIMALE9_ALIGN_CENTER_UNLOCKED));
								}

								// Colonna Punteggio
								colonnaCoeffPunt++;
								UtilityExcel.scriviCella(foglioDittePunteggi, colonnaCoeffPunt, 1,
										norpartoString + ". " + commento, styleA1);
								UtilityExcel.scriviCommento(patriarch, colonnaCoeffPunt, 1,
								      commentoNote, workXLS.createFont());
								UtilityExcel.scriviCella(foglioDittePunteggi, colonnaCoeffPunt,
										2, necvan, dizStiliExcel.getStileExcel(
												DizionarioStiliExcel.INTERO_ALIGN_RIGHT));
								UtilityExcel.scriviCella(foglioDittePunteggi, colonnaCoeffPunt, 3, "C",
										dizStiliExcel.getStileExcel(
												DizionarioStiliExcel.STRINGA_ALIGN_CENTER));
								UtilityExcel.setFormatoCelleColonna(foglioDittePunteggi,
										colonnaCoeffPunt,	numeroRigaIniziale,
										numeroRigaFinale, dizStiliExcel.getStileExcel(
												DizionarioStiliExcel.DECIMALE3_ALIGN_CENTER_UNLOCKED));
								UtilityExcel.setLarghezzaColonna(foglioDittePunteggi,
										colonnaCoeffPunt,9);
								if(strFormulaPunteggioTotale.length() > 0)
									strFormulaPunteggioTotale.append("+");

								strFormulaPunteggioTotale.append(
										UtilityExcel.conversioneNumeroColonna(colonnaCoeffPunt) + "numeroRiga");

								if ((new Long(3)).equals(livpar) && colonnaPunteggio == 0){
									// Salvataggio della colonna in cui scrivere la formula per
									// determinare il punteggio di un criterio in funzione del
									// punteggio dei vari subcriteri
									colonnaPunteggio = colonnaCoeffPunt;
								}
							}
						}
						// gestione dei sub criteri
						if ((new Long(2)).equals(livpar)) {
							if (norpar1 != null) {
								// Colonna sub coefficiente
								colonnaCoeffPunt++;
								UtilityExcel.scriviCella(foglioDittePunteggi, colonnaCoeffPunt,
										1, "Coeff.sub. " + norpar1toString, styleA1);
								UtilityExcel.scriviCommento(patriarch, colonnaCoeffPunt, 1,
								    commentoNote, workXLS.createFont());
								UtilityExcel.scriviCella(foglioDittePunteggi, colonnaCoeffPunt,
										2, necvan, dizStiliExcel.getStileExcel(
												DizionarioStiliExcel.INTERO_ALIGN_RIGHT));
								UtilityExcel.scriviCella(foglioDittePunteggi, colonnaCoeffPunt,
										3, "S",	dizStiliExcel.getStileExcel(
												DizionarioStiliExcel.STRINGA_ALIGN_CENTER));
								UtilityExcel.scriviCella(foglioDittePunteggi, colonnaCoeffPunt,
										4, maxpun, dizStiliExcel.getStileExcel(
												DizionarioStiliExcel.DECIMALE9_ALIGN_CENTER_UNLOCKED));
								UtilityExcel.setFormatoCelleColonna(foglioDittePunteggi,
										colonnaCoeffPunt,	numeroRigaIniziale, numeroRigaFinale,
										dizStiliExcel.getStileExcel(
												DizionarioStiliExcel.DECIMALE9_ALIGN_CENTER_UNLOCKED));
								UtilityExcel.setLarghezzaColonna(foglioDittePunteggi,
										colonnaCoeffPunt,0);


								// Set della formula che calcola il sub coefficiente in funzione
								// del punteggio massimo associato al sub criterio
								// coefficiente = punteggio / punteggio massimo se punteggio massimo >0
								// coefficiente = 0 altrimenti
								String formulaCoefficiente = "IF(" + UtilityExcel.conversioneNumeroColonna(colonnaCoeffPunt) + "4>0,ROUND(" +
										(UtilityExcel.conversioneNumeroColonna(colonnaCoeffPunt+1))
										+ "numeroRiga/" +
										UtilityExcel.conversioneNumeroColonna(colonnaCoeffPunt) +
										"4, 9),0)";
								for(int j=numeroRigaIniziale; j < numeroRigaFinale; j++){
									UtilityExcel.scriviFormulaCella(foglioDittePunteggi,
											colonnaCoeffPunt,	j,
											formulaCoefficiente.replaceAll("numeroRiga", "" + j),
											dizStiliExcel.getStileExcel(
													DizionarioStiliExcel.DECIMALE9_ALIGN_CENTER_UNLOCKED));
								}

								// Colonna sub punteggio
								colonnaCoeffPunt++;
								UtilityExcel.scriviCella(foglioDittePunteggi, colonnaCoeffPunt,
										1, norpartoString + "." + norpar1toString + " " + commento, styleA1);
								UtilityExcel.scriviCommento(patriarch, colonnaCoeffPunt, 1,
								    commentoNote, workXLS.createFont());
								UtilityExcel.scriviCella(foglioDittePunteggi, colonnaCoeffPunt,
										2, necvan, dizStiliExcel.getStileExcel(
												DizionarioStiliExcel.INTERO_ALIGN_RIGHT));
								UtilityExcel.scriviCella(foglioDittePunteggi, colonnaCoeffPunt, 3, "S",
										dizStiliExcel.getStileExcel(
												DizionarioStiliExcel.STRINGA_ALIGN_CENTER));
								UtilityExcel.setFormatoCelleColonna(foglioDittePunteggi,
										colonnaCoeffPunt,	numeroRigaIniziale, numeroRigaFinale,
										dizStiliExcel.getStileExcel(
												DizionarioStiliExcel.DECIMALE3_ALIGN_CENTER_UNLOCKED));
								UtilityExcel.setLarghezzaColonna(foglioDittePunteggi,
										colonnaCoeffPunt, 9);

								if(strFormulaPunteggio.length() > 0)
									strFormulaPunteggio.append("+");

								strFormulaPunteggio.append(
										UtilityExcel.conversioneNumeroColonna(colonnaCoeffPunt) + "numeroRiga");
							}
						}
					}
				}

				if(strFormulaPunteggio.length() > 0 && colonnaPunteggio > 0){
					String tmpStrFormulaPunteggio = "ROUND(" + strFormulaPunteggio.toString() + ", 3)";
					for(int l=numeroRigaIniziale; l < numeroRigaFinale; l++){
						UtilityExcel.scriviFormulaCella(foglioDittePunteggi,
								colonnaPunteggio,	l,
								tmpStrFormulaPunteggio.replaceAll("numeroRiga", "" + l),
								dizStiliExcel.getStileExcel(
										DizionarioStiliExcel.DECIMALE3_ALIGN_CENTER_UNLOCKED));
					}

					// Reset della formula per determinare il punteggio del criterio
					// in funzione dei punteggi dei subcriteri
					strFormulaPunteggio = new StringBuffer("");
					// Reset della colonna in cui scrivere la formula per
					// determinare il punteggio di un criterio in funzione del
					// punteggio dei vari subcriteri
					colonnaPunteggio = 0;
				}

				if(strFormulaPunteggioTotale.length() > 0){
					String tmpStrFormulaPunteggioTotale = "ROUND(" +
							strFormulaPunteggioTotale.toString() + ", 3)";
					for(int i=numeroRigaIniziale; i < numeroRigaFinale; i++){
						int colonnaPunteggioTotale = ImportExportOEPVManager.FOGLIO_DITTE_COLONNA_PUNTEGGIO_TOTALE;
						if ((new Long(2)).equals(tippar))
						  colonnaPunteggioTotale = ImportExportOEPVManager.FOGLIO_DITTE_COLONNA_PUNTEGGIO_TOTALE_ECO;
						UtilityExcel.scriviFormulaCella(foglioDittePunteggi, colonnaPunteggioTotale,
								i, tmpStrFormulaPunteggioTotale.replaceAll("numeroRiga", "" + i),
								dizStiliExcel.getStileExcel(
										DizionarioStiliExcel.DECIMALE3_ALIGN_CENTER_UNLOCKED));
					}
				}

				String maxpunTotaleString = UtilityNumeri.convertiDouble(maxpunTotale);
                String commentoPunteggioTot ="";
				if(tippar.longValue()==1 && mintecString != null && !"".equals(mintecString))
				  commentoPunteggioTot = "p.min  " + mintecString + " - ";
				else if(tippar.longValue()==2 && minecotoString != null && !"".equals(minecotoString))
				  commentoPunteggioTot = "p.min " + minecotoString + " - ";

				commentoPunteggioTot +="p.max " + maxpunTotaleString;

                UtilityExcel.scriviCommento(patriarch, indiceIntestazionePunteggio, 1,commentoPunteggioTot, workXLS.createFont());
			}
		} catch (SQLException e) {
			throw new GestoreException("Errore nell'esportazione delle ditte e dei" +
					" punteggi", "importaesportaexcel.dittepunteggi",e);
		}
	}

	/**
	 * Scrive la lista delle ditte
	 * @param ngara
	 * @param foglioDittePunteggi
	 * @param dizStiliExcel
	 * @param tippar
	 * @throws GestoreException
	 */
	private int setDitte(String ngara, HSSFSheet foglio,
			DizionarioStiliExcel dizStiliExcel, Long tippar, String campoVisibile)	throws GestoreException {

		int rigaFinale = 0;

		try {
		  String campoSelect="impoff";
		  if(campoVisibile !=null && !"".equals(campoVisibile)){
    		  if("RIBOEPV".equals(campoVisibile))
    		    campoSelect = "ABS(" + campoVisibile + ")";
    		  else
    		    campoSelect = campoVisibile;
		  }
		  String selectDITG =
				"select dittao, numordpl, nomimo, " + campoSelect + " " +
				  "from ditg " +
				 "where ngara5 = ? " +
				   "and (invoff is null or invoff = '1' or invoff = '2') " +
				   "and (fasgar is null or fasgar > ?) " +
				"order by nprogg";
			Object param[]=new Object[2];
			param[0]= ngara;
			if(tippar.longValue()==1)
			  param[1]= new Long(5);
			else
			  param[1]= new Long(6);

			List datiDITG = sqlManager.getListVector(selectDITG, param);
			int rigaIniziale = ImportExportOEPVManager.FOGLIO_DITTE_RIGA_INIZIALE_DITTE;

			if (datiDITG != null && datiDITG.size()>0) {
			  Double impoff = null;
			  Double riboepv = null;
			  for (int i = 0; i < datiDITG.size(); i++) {
					// Codice ditta - colonna A (1) dalla riga iniziale in poi
					String dittao = SqlManager.getValueFromVectorParam(
							datiDITG.get(i),0).toString();
					if (dittao != null)
						UtilityExcel.scriviCella(foglio, 1, rigaIniziale + i, dittao,
								dizStiliExcel.getStileExcel(
										DizionarioStiliExcel.STRINGA_ALIGN_CENTER));

					// Numero progressivo ditta - collona B (2) dalla riga iniziale in poi
					Long nprogg = SqlManager.getValueFromVectorParam(
							datiDITG.get(i),1).longValue();
					String numeroprogressivo = nprogg.toString();
					if (nprogg != null)
						UtilityExcel.scriviCella(foglio, 2, rigaIniziale + i,
								numeroprogressivo,
								dizStiliExcel.getStileExcel(
										DizionarioStiliExcel.STRINGA_ALIGN_CENTER));

					// Descrizione ditta
					String nomimo = SqlManager.getValueFromVectorParam(
							datiDITG.get(i),2).toString();
					if (nomimo != null)
						UtilityExcel.scriviCella(foglio, 3, rigaIniziale + i, nomimo,
								dizStiliExcel.getStileExcel(
										DizionarioStiliExcel.STRINGA_ALIGN_CENTER));

					if(tippar.longValue()==2){
					  if("IMPOFF".equals(campoVisibile)){
					    //Importo offerto
					    impoff = SqlManager.getValueFromVectorParam(datiDITG.get(i),3).doubleValue();
    					if(impoff!=null)
    					  UtilityExcel.scriviCella(foglio, 4, rigaIniziale + i, impoff,dizStiliExcel.getStileExcel(DizionarioStiliExcel.DECIMALE5_ALIGN_CENTER_UNLOCKED));
                      }else{
                        //Ribasso
                        riboepv = SqlManager.getValueFromVectorParam(datiDITG.get(i),3).doubleValue();
                        if(riboepv!=null)
                          UtilityExcel.scriviCella(foglio, 5, rigaIniziale + i, riboepv,dizStiliExcel.getStileExcel(DizionarioStiliExcel.DECIMALE5_ALIGN_CENTER_UNLOCKED));
                      }
					}


				}
				rigaFinale = rigaIniziale + datiDITG.size();
			}
		} catch (SQLException e) {
			throw new GestoreException("Errore nell'esportazione delle ditte",
					"importaesportaexcel.ditte", e);
		}

		return rigaFinale;
	}

	/**
	 * Scrive la lista delle ditte con i relativi punteggi economici
	 * all'interno del foglio "Ditte e punteggi economici"
	 * @param ngara
	 * @param workXLS
	 * @param campoVisibile
	 * @throws GestoreException
	 */
	private void setDittePunteggiEconomici(String ngara, DizionarioStiliExcel dizStiliExcel,String campoVisibile)
		throws GestoreException {
		HSSFSheet foglioDittePunteggiEconomici = workXLS.getSheet(
				ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_ECONOMICI);

		if (foglioDittePunteggiEconomici == null) {
			throw new GestoreException("Non trovato il foglio " +
					ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_ECONOMICI,
				"importaesportaexcel.foglionontrovato",
				new Object[] { ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_ECONOMICI },
				null);
		}

		if (foglioDittePunteggiEconomici == null) {
			throw new GestoreException("Non trovato il foglio " +
				ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_ECONOMICI,
				"importaesportaexcel.foglionontrovato",
				new Object[] { ImportExportOEPVManager.FOGLIO_DITTE_PUNTEGGI_ECONOMICI },
				null);
		}

		Long offtel = null;
		try {
          offtel = (Long)this.sqlManager.getObject("select offtel from torn,gare where ngara=? and codgar1=codgar", new Object[]{ngara});
        } catch (SQLException e) {
          throw new GestoreException("Errore nell'esportazione delle ditte e dei" +
              " punteggi", "importaesportaexcel.dittepunteggi",e);
        }

        boolean bloccoImporto=false;
        if(offtel!=null && offtel.longValue()==1){
          //foglioDittePunteggiEconomici.protectSheet("BloccoOfftel2014");
          bloccoImporto=true;
        }

		this.setDittePunteggi(ngara, foglioDittePunteggiEconomici, new Long(2),
				dizStiliExcel,bloccoImporto,campoVisibile);

		// Scrittura delle informazioni principali della gara nelle prime righe del
		// foglio dei punteggi tecnici. Tali dati verranno letti e verificati in
		// fase di import. Fra le informazioni principale della gara ci sono:
		// TORN.CODGAR o GARE.CODGAR1, GARE.NGARA, GARE.MODLICG, V_GARE_TORN.ISLOTTI
		// e V_GARE_TORN.ISGENERE
		this.setDatiPrincipaliGara(ngara, foglioDittePunteggiEconomici, dizStiliExcel,campoVisibile);
	}

	/**
	 * Scrittura nel foglio dei punteggi economici delle informazioni principali
	 * della gara per usarle all'inizio dell'operazione di import per verificare
	 * se tali informazioni sono uguali a quelle della gara in cui si
	 * sta importando
	 *
	 * @param ngara
	 * @param foglioDittePunteggiEconomici
	 * @param campoVisibile
	 * @throws GestoreException
	 */
	private void setDatiPrincipaliGara(String ngara, HSSFSheet foglioDittePunteggiEconomici,
			DizionarioStiliExcel dizStiliExcel,String campoVisibile)	throws GestoreException {

		HSSFRow riga3 = foglioDittePunteggiEconomici.getRow(2);
		if(riga3 != null){
			// Valori dei campi da databse
			String codiceGara = null;
			String modlicg = null;
			String isLotti = null;


			// Lettura dei campi da database
			try {
				Vector datiGARE = this.sqlManager.getVector(
						"select CODGAR1, MODLICG from GARE where NGARA = ?",
						new Object[]{ngara});

				if(datiGARE != null && datiGARE.size() > 0){
					codiceGara = SqlManager.getValueFromVectorParam(datiGARE, 0).toString();
					modlicg = SqlManager.getValueFromVectorParam(datiGARE, 1).toString();
				}

				Vector datiV_GARE_TORN = this.sqlManager.getVector(
						"select ISLOTTI from V_GARE_TORN where codgar = ?",
						new Object[]{codiceGara});

				if(datiV_GARE_TORN != null && datiV_GARE_TORN.size() > 0){
					isLotti = SqlManager.getValueFromVectorParam(datiV_GARE_TORN, 0).toString();
				}

				// Scrittura valore TORN.CODGAR o GARE.CODGAR1 sul file Excel
				UtilityExcel.scriviCella(foglioDittePunteggiEconomici, 1, 2, codiceGara,
						dizStiliExcel.getStileExcel(DizionarioStiliExcel.STRINGA_ALIGN_LEFT));
				// Scrittura valore GARE.NGARA sul file Excel
				UtilityExcel.scriviCella(foglioDittePunteggiEconomici, 2, 2, ngara,
						dizStiliExcel.getStileExcel(DizionarioStiliExcel.STRINGA_ALIGN_LEFT));
				// Lettura valore GARE.MODLICG sul file Excel
				UtilityExcel.scriviCella(foglioDittePunteggiEconomici, 3, 2, modlicg,
						dizStiliExcel.getStileExcel(DizionarioStiliExcel.STRINGA_ALIGN_LEFT));
				// Lettura valore V_GARE_TORN.ISLOTTI sul file Excel
				UtilityExcel.scriviCella(foglioDittePunteggiEconomici, 4, 2, isLotti,
						dizStiliExcel.getStileExcel(DizionarioStiliExcel.STRINGA_ALIGN_LEFT));



			} catch(SQLException s){
				throw new GestoreException("Errore durante la scrittura dei dati " +
						"principali della gara da usare in fase di import per la verifica " +
						"preliminare dei dati", "importaesportaexcel.esportazioneexcel", s);
			}
		}

		HSSFRow riga = foglioDittePunteggiEconomici.getRow(4);
        if(riga != null){
          //Si salva sulla quarta riga in corrispondenza alle colonne IMPOFF e RIBOEPV se sono visibili
          if("IMPOFF".equals(campoVisibile))
              UtilityExcel.scriviCella(foglioDittePunteggiEconomici, 4, 4, "1",
                  dizStiliExcel.getStileExcel(DizionarioStiliExcel.STRINGA_ALIGN_LEFT));
          else if("RIBOEPV".equals(campoVisibile))
            UtilityExcel.scriviCella(foglioDittePunteggiEconomici, 5, 4, "1",
                dizStiliExcel.getStileExcel(DizionarioStiliExcel.STRINGA_ALIGN_LEFT));
        }
	}

}