package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.TempFileUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.excel.DizionarioStiliExcelX;
import it.eldasoft.sil.pg.tags.funzioni.GetFiltroTipoGaraFunction;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityExcelX;
import net.sf.json.JSONObject;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;

public class ReportSchedeANACAction extends Action {

	private SqlManager sqlManager;
	private GeneManager geneManager;
	private TabellatiManager tabellatiManager;

	private final static String EXPORT_EXCEL = "exportExcel";
	private final String NOME_FILE_REPORT_ANAC_EXPORT = "ReportSchedeANAC.xlsx";
	private final String DICITURA_WARNING = "Scheda di esito non inviata";

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}
	public void setGeneManager(GeneManager geneManager) {this.geneManager = geneManager;}
	public void setTabellatiManager(TabellatiManager tabellatiManager) {
		this.tabellatiManager = tabellatiManager;
	}

	@Override
	public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		DataSourceTransactionManagerBase.setRequest(request);
		response.setHeader("cache-control", "no-cache");
		response.setContentType("text/text;charset=utf-8");
		String modalita = request.getParameter("modalita");

		try {
			List<HashMap<String,JdbcParametro>> listaGareProcessate = processaGare(getGareDaProcessare(request, modalita), modalita);

			PrintWriter out = response.getWriter();
			JSONObject result = new JSONObject();
			if (EXPORT_EXCEL.equals(modalita)) {
				HttpSession session = request.getSession();
				String nomeFile = exportExcel(listaGareProcessate, session);
				result.put("data", nomeFile);
			} else {
				result.put("data", listaGareProcessate);
			}
			out.println(result);
			out.flush();
		} catch (Throwable e) {
			throw new JspException("Errore nella lettura delle gare" ,e);
		}
		return null;
	}

	private List<HashMap<String,JdbcParametro>> processaGare(List<HashMap<String,JdbcParametro>> listaGare, String modalita) throws Exception {
		if (listaGare == null || listaGare.isEmpty())
			return null;
		List<HashMap<String,JdbcParametro>> listaGareReturn = new ArrayList<>();
		for (int i = 0; i < listaGare.size(); i++) {
			HashMap<String,JdbcParametro> datiGara = listaGare.get(i);
			Long genere = (Long) datiGara.get("GENERE").getValue();

			String codiceGara = (String) datiGara.get("NGARA").getValue();
			if (genere != null && (genere == 300)) {
				datiGara.put("CODICELOTTO", new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceGara));
				codiceGara = (String) datiGara.get("CODGAR").getValue();
			}
			datiGara.put("CODICEGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceGara));

			String stato = (String) datiGara.get("STATO").getValue();
			String esito = (String) datiGara.get("ESITO").getValue();

			datiGara.put("STATOESITO", new JdbcParametro(JdbcParametro.TIPO_TESTO, (StringUtils.isNotBlank(stato) ? stato : "") + (StringUtils.isNotBlank(esito) ? " - " + esito : "")));

			String statomodpres1 = (String) datiGara.get("STATOMODPRES1").getValue();
			String statomodpres3 = (String) datiGara.get("STATOMODPRES3").getValue();
			String statomodpres4 = (String) datiGara.get("STATOMODPRES4").getValue();

			List<String> schede = new ArrayList<>();
			if (StringUtils.isNotEmpty(statomodpres1)) {
				schede.addAll(Arrays.asList(statomodpres1.split(",")));
			}
			if (StringUtils.isNotEmpty(statomodpres3)) {
				schede.addAll(Arrays.asList(statomodpres3.split(",")));
			}
			if (StringUtils.isNotEmpty(statomodpres4)) {
				schede.addAll(Arrays.asList(statomodpres4.split(",")));
			}
			Object[] schedeEstratte = estraiSchede(schede);

			datiGara.put("SCHEDECOMPILAZIONE", new JdbcParametro(JdbcParametro.TIPO_TESTO,StringUtils.isNotEmpty((String)schedeEstratte[0]) ? schedeEstratte[0] : ""));
			datiGara.put("SCHEDEATTESAERRORI", new JdbcParametro(JdbcParametro.TIPO_TESTO,StringUtils.isNotEmpty((String)schedeEstratte[1]) ? schedeEstratte[1] : ""));
			datiGara.put("SCHEDESUCCESSO", new JdbcParametro(JdbcParametro.TIPO_TESTO,StringUtils.isNotEmpty((String)schedeEstratte[2]) ? schedeEstratte[2] : ""));
			datiGara.put("GARATERMINATA", new JdbcParametro(JdbcParametro.TIPO_BINARIO, schedeEstratte[3]));

			if (EXPORT_EXCEL.equals(modalita)) {
				Long tipgarg = (Long) datiGara.get("TIPGARG").getValue();
				datiGara.put("TIPOPROCEDURA", new JdbcParametro(JdbcParametro.TIPO_TESTO, tabellatiManager.getDescrTabellato("A2044", tipgarg.toString())));

				String ditta = (String) datiGara.get("DITTA").getValue();
				Long esineg = (Long) datiGara.get("ESINEG").getValue();
				if (!((boolean) schedeEstratte[3]) && (StringUtils.isNotEmpty(ditta) || esineg != null)) {
					datiGara.put("DICITURAWARNING", new JdbcParametro(JdbcParametro.TIPO_TESTO, DICITURA_WARNING));
				}

				if (datiGara.get("DACQCIG") != null) {
					String dacqcig = (String) datiGara.get("DACQCIG").getValue();
					if (StringUtils.isNotEmpty(dacqcig) && dacqcig.contains(":")) {
						dacqcig = dacqcig.substring(0, dacqcig.indexOf(":") - 3);
						datiGara.put("DACQCIG", new JdbcParametro(JdbcParametro.TIPO_TESTO, dacqcig));
					}
				}

				JdbcParametro dataAggEsitoParametro = null;
				if (esineg != null) {
					dataAggEsitoParametro = datiGara.get("DATNEG");
				} else {
					dataAggEsitoParametro = datiGara.get("DATTOA");
				}

				if (dataAggEsitoParametro != null) {
					String dataAggEsito = (String) dataAggEsitoParametro.getValue();
					if (StringUtils.isNotEmpty(dataAggEsito) && dataAggEsito.contains(":")) {
						dataAggEsito = dataAggEsito.substring(0, dataAggEsito.indexOf(":") - 3);
						datiGara.put("DATAAGGESITO", new JdbcParametro(JdbcParametro.TIPO_DATA, dataAggEsito));
					}
				}
			}

			listaGareReturn.add(datiGara);
		}
		return listaGareReturn;
	}

	private List<HashMap<String,JdbcParametro>> getGareDaProcessare(HttpServletRequest request, String modalita) throws Exception {

		String whereAddizionale = "";

		final boolean trovaProcedure = geneManager.getProfili().checkProtec(
				(String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO),
				"FUNZ", "VIS", "ALT.GARE.V_GARE_PROFILO-lista.ApriGare", true);

		String entita;
		if (trovaProcedure) {
			entita = "V_GARE_PROFILO";
		} else {
			entita = "V_GARE_TORN";
		}

		ArrayList<Object> params = new ArrayList<>();

		final ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
		final String filtroLivelloUtente = FiltroLivelloUtenteFunction.getFiltroLivelloUtente(entita, profiloUtente);
		final String uffintAbilitata = ConfigManager.getValore("it.eldasoft.associazioneUffintAbilitata");
		final String multiEnteAbilitata = ConfigManager.getValore("loginMultiEnte");

		if (StringUtils.isNotEmpty(filtroLivelloUtente))
			whereAddizionale += " AND " + filtroLivelloUtente;

		if ("V_GARE_PROFILO".equals(entita)) {
			whereAddizionale += " AND V_GARE_PROFILO.CODPROFILO IN (SELECT COD_PROFILO FROM W_ACCPRO WHERE ID_ACCOUNT = ?)";
			params.add(profiloUtente.getId());

			final OpzioniUtente opzioniUtente = new OpzioniUtente(profiloUtente.getFunzioniUtenteAbilitate());
			final CheckOpzioniUtente opzioniAmministratore = new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
			if (uffintAbilitata.equals("1") && !opzioniAmministratore.test(opzioniUtente) && !"1".equals(multiEnteAbilitata)) {
				whereAddizionale += " AND V_GARE_PROFILO.CENINT IN (select codein from usr_ein where syscon = ? )";
				params.add(profiloUtente.getId());
			}
		} else {
			final String filtroProfiloAttivo = (String) request.getSession().getAttribute("filtroProfiloAttivo");
			if (filtroProfiloAttivo != null && !filtroProfiloAttivo.isEmpty()) {
				whereAddizionale += " AND V_GARE_TORN.PROFILOWEB = ?";
				params.add(filtroProfiloAttivo);
			}

			String filtroTipoGara = GetFiltroTipoGaraFunction.getFiltroTipoGara(
					(String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), request.getSession().getServletContext());
			if (!filtroTipoGara.isEmpty()) {
				whereAddizionale += " AND " + filtroTipoGara;
			}

			final boolean visualizzazioneGareALotti = geneManager.getProfili().checkProtec(
					(String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO),
					"FUNZ", "VIS", "ALT.GARE.GARE.GestioneGareALotti", true);
			if (!visualizzazioneGareALotti) {
				whereAddizionale += " AND V_GARE_TORN.GENERE <>1 ";
			}

			final boolean visualizzazioneGareLottiOffUnica = geneManager.getProfili().checkProtec(
					(String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO),
					"FUNZ", "VIS", "ALT.GARE.GARE.GestioneGareLottiOffUnica", true);
			if (!visualizzazioneGareLottiOffUnica) {
				whereAddizionale += " AND V_GARE_TORN.GENERE <>3 ";
			}

			final boolean visualizzazioneGareALottoUnico = geneManager.getProfili().checkProtec(
					(String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO),
					"FUNZ", "VIS", "ALT.GARE.GARE.GestioneGareALottoUnico", true);
			if (!visualizzazioneGareALottoUnico) {
				whereAddizionale += " AND V_GARE_TORN.GENERE <>2 ";
			}
		}

		if ((uffintAbilitata.equals("1") && "V_GARE_TORN".equals(entita)) || "1".equals(multiEnteAbilitata)) {
			whereAddizionale += " AND " + entita + ".CENINT = ?";
			params.add(request.getSession().getAttribute("uffint"));
		}

		String select = "SELECT " + (EXPORT_EXCEL.equals(modalita) ? "DISTINCT " : "") +
				" torn.codgar," +
				" gare.ngara," +
				" gare.codiga," +
				" " + entita + ".codice," +
				" gare.codcig," +
				" gare.not_gar," +
				" v_gare_genere.genere," +
				" COALESCE(v_gare_statoesitolotti.stato, v_gare_statoesito.stato) AS stato," +
				" COALESCE(v_gare_statoesitolotti.esito, v_gare_statoesito.esito) AS esito," +
				" tecni.nomtec as NOMERP," +
				(EXPORT_EXCEL.equals(modalita) ?
					" tecni2.nomtec as NOMEDRP," +
					" gare.tipgarg," +
					" gare.impapp," +
					" " + sqlManager.getDBFunction("DATETIMETOSTRING", new String[] { "gare.dattoa" }) + " as dattoa," +
					" " + sqlManager.getDBFunction("DATETIMETOSTRING", new String[] { "gare.datneg" }) + " as datneg," +
					" " + sqlManager.getDBFunction("DATETIMETOSTRING", new String[] { "gare.dacqcig" }) + " as dacqcig,"
					: ""
				) +
				(!(EXPORT_EXCEL.equals(modalita)) && "V_GARE_PROFILO".equals(entita) ?
					" V_GARE_PROFILO.cenint, V_GARE_PROFILO.codprofilo,"
					: ""
				) +
				" gare.ditta," +
				" gare.esineg," +
				" V_STATI_ANACFORM.stato_ultime_schede as STATOMODPRES1," +
				" V_STATI_ANACFORM2.stato_ultime_schede as STATOMODPRES3," +
				" V_STATI_ANACFORM3.stato_ultime_schede as STATOMODPRES4" +
				" FROM gare" +
				" INNER JOIN torn ON gare.codgar1 = torn.codgar" +
				" INNER JOIN " + entita + " ON TORN.CODGAR=" + entita + ".CODGAR" +
				" INNER JOIN v_gare_genere ON v_gare_genere.codice = gare.ngara and v_gare_genere.genere NOT IN (1,100,4,10,11,20)" +
				" LEFT JOIN tecni ON torn.codrup = tecni.codtec" +
				(EXPORT_EXCEL.equals(modalita) ? " LEFT JOIN tecni tecni2 ON torn.coddrp = tecni2.codtec" : "") +
				" LEFT JOIN v_gare_statoesito ON gare.ngara = v_gare_statoesito.codice" +
				" LEFT JOIN v_gare_statoesitolotti ON gare.ngara = v_gare_statoesitolotti.codice" +
				" LEFT JOIN V_STATI_ANACFORM ON V_STATI_ANACFORM.codgar = torn.codgar AND V_STATI_ANACFORM.modpres = 1" +
				" LEFT JOIN V_STATI_ANACFORM V_STATI_ANACFORM2 ON V_STATI_ANACFORM2.modpres = 3 and V_STATI_ANACFORM2.ngara = gare.ngara" +
				" LEFT JOIN V_STATI_ANACFORM V_STATI_ANACFORM3 ON V_STATI_ANACFORM3.modpres = 4 and V_STATI_ANACFORM3.ngaraScheda = gare.ngara" +
				("V_GARE_PROFILO".equals(entita) ? (
				" INNER JOIN TAB2 ON tab2cod = 'A1z11' and (" +
					"  TAB2.TAB2D2 = cast(gare.tipgarg as varchar(200))" +
						" or TAB2.TAB2D2 like (" + this.sqlManager.getDBFunction("concat", new String[] {"cast(gare.tipgarg as varchar(200))", "',%'"}) + ")" +
						" or TAB2.TAB2D2 like (" + this.sqlManager.getDBFunction("concat", new String[] {"'%,'", "cast(gare.tipgarg as varchar(200))", "',%'"}) + ")" +
						" or TAB2.TAB2D2 like (" + this.sqlManager.getDBFunction("concat", new String[] {"'%,'", "cast(gare.tipgarg as varchar(200))"}) + ")" +
				" ) "
				) : "") +
				" WHERE (torn.isarchi IS NULL or torn.isarchi <> '1')" +
				" AND torn.vernpa IS NOT NULL " +
				" AND gare.codgar1 <> gare.ngara" +
				("V_GARE_PROFILO".equals(entita) ? " and gare.tipgarg is not null and not gare.tipgarg between 50 and 89" : ""
				) +
				whereAddizionale +
				//"  and filtro exists g1cf_npa.schedafinale " +
				" ORDER BY codice DESC, gare.ngara ASC";

		return (List<HashMap<String,JdbcParametro>>) sqlManager.getListHashMap(select, params.isEmpty()? null : params.toArray());
	}

	private Object[] estraiSchede(List<String> schede) throws  Exception {
		TreeMap<Long, Object[]> schedeEstratte = new TreeMap<>();
		boolean garaTerminata = false;
		for (String scheda : schede) {
			String[] datiScheda = scheda.split(";");
			String nome_scheda = datiScheda[0];
			Long id = Long.parseLong(datiScheda[1]);
			Long stato = Long.parseLong(datiScheda[2]);
			Long statoFinale = Long.parseLong(datiScheda[3]);

			Long indiceColonna = null;
			if (stato == 1) {
				indiceColonna = 1L;
			} else if (stato > 1 && stato < statoFinale) {
				indiceColonna = 2L;
			} else {
				if (!stato.equals(statoFinale)) {
					throw new Exception("Stato non valido("+stato+") per la scheda " + nome_scheda + " con idForm " + id);
				}
				if ("NAG".equals(nome_scheda) || nome_scheda.startsWith("A") || "P5".equals(nome_scheda)) {
					garaTerminata = true;
				}
				indiceColonna = 3L;
			}
			schedeEstratte.put(id, new Object[] {nome_scheda, indiceColonna});
		}

		List<String> schedeInCompilazione = new ArrayList<>();
		List<String> schedeAttesaErrori = new ArrayList<>();
		List<String> schedeSuccesso = new ArrayList<>();

		HashMap<String, Long> countRettifiche = new HashMap<>();

		for (Long idForm : schedeEstratte.keySet()) {
			Object[] datiScheda = schedeEstratte.get(idForm);
			Long numeroRettifica = null;
			String nome_scheda = (String) datiScheda[0];
			if (countRettifiche.containsKey(nome_scheda)) {
				numeroRettifica = countRettifiche.get(nome_scheda) + 1;
				countRettifiche.put(nome_scheda, numeroRettifica);
			} else {
				countRettifiche.put(nome_scheda, 0L);
			}
			if (numeroRettifica != null) {
				nome_scheda += "#"+numeroRettifica;
			}
			if ((Long) datiScheda[1] == 1) {
				schedeInCompilazione.add(nome_scheda);
			} else if ((Long) datiScheda[1] == 2) {
				schedeAttesaErrori.add(nome_scheda);
			} else {
				schedeSuccesso.add(nome_scheda);
			}
		}
		return new Object[] {String.join(", ",schedeInCompilazione), String.join(", ",schedeAttesaErrori), String.join(", ",schedeSuccesso), garaTerminata};
	}

	private String exportExcel(List<HashMap<String,JdbcParametro>> gareDaEsportare, HttpSession session) throws  Exception {

		XSSFWorkbook workBook  = new XSSFWorkbook();

		ArrayList<Object[]> definizioni = setDefinizioni();
		DizionarioStiliExcelX dizStiliExcel = new DizionarioStiliExcelX(workBook);

		Sheet foglioReportSchedeANAC = workBook.createSheet("Report schede ANAC");
		setPrintSetup(foglioReportSchedeANAC);
		popolaFoglio(gareDaEsportare, definizioni, dizStiliExcel, foglioReportSchedeANAC);

		workBook.setActiveSheet(0);
		workBook.setSelectedTab(0);

		String nomeFile = FilenameUtils.getBaseName(NOME_FILE_REPORT_ANAC_EXPORT) + "." + FilenameUtils.getExtension(NOME_FILE_REPORT_ANAC_EXPORT);
		File tempFile = null;
		if (session == null) {
			tempFile = new File( new File(System.getProperty("java.io.tmpdir")),nomeFile);
		} else {
			tempFile = TempFileUtilities.getTempFileSenzaNumeoRandom(nomeFile, session);
		}
		FileOutputStream fos = new FileOutputStream(tempFile);
		// Scrittura dell'oggetto workBook nel file temporaneo
		workBook.write(fos);
		fos.close();

		return tempFile.getName();
	}

	private ArrayList<Object[]> setDefinizioni() throws SQLException, GestoreException {
		ArrayList<Object[]> listaDefinizioniColonne = new ArrayList<>();

		listaDefinizioniColonne.add(createDefinizioneColonna("CODICEGARA", DizionarioStiliExcelX.STRINGA_ALIGN_CENTER, "Codice Gara", 14));
		listaDefinizioniColonne.add(createDefinizioneColonna("CODICELOTTO", DizionarioStiliExcelX.STRINGA_ALIGN_CENTER, "Codice lotto", 16));
		listaDefinizioniColonne.add(createDefinizioneColonna("CODIGA", DizionarioStiliExcelX.STRINGA_ALIGN_CENTER, "Lotto", 6));
		listaDefinizioniColonne.add(createDefinizioneColonna("CODCIG", DizionarioStiliExcelX.STRINGA_ALIGN_CENTER, "Codice CIG", 12));
		listaDefinizioniColonne.add(createDefinizioneColonna("DACQCIG", DizionarioStiliExcelX.STRINGA_ALIGN_CENTER, "Data acquisizione CIG", 15));
		listaDefinizioniColonne.add(createDefinizioneColonna("NOT_GAR", DizionarioStiliExcelX.STRINGA_ALIGN_LEFT, "Oggetto", 80));
		listaDefinizioniColonne.add(createDefinizioneColonna("TIPOPROCEDURA", DizionarioStiliExcelX.STRINGA_ALIGN_CENTER, "TipoProcedura", 20));
		listaDefinizioniColonne.add(createDefinizioneColonna("IMPAPP", DizionarioStiliExcelX.DECIMALE2_ALIGN_RIGHT, "Importo a base di gara", 15));
		listaDefinizioniColonne.add(createDefinizioneColonna("NOMERP", DizionarioStiliExcelX.STRINGA_ALIGN_CENTER, "RUP", 20));
		listaDefinizioniColonne.add(createDefinizioneColonna("NOMEDRP", DizionarioStiliExcelX.STRINGA_ALIGN_CENTER, "Incaricato invio schede", 20));
		listaDefinizioniColonne.add(createDefinizioneColonna("STATOESITO", DizionarioStiliExcelX.STRINGA_ALIGN_CENTER, "Stato esito", 30));
		listaDefinizioniColonne.add(createDefinizioneColonna("DATAAGGESITO", DizionarioStiliExcelX.STRINGA_ALIGN_CENTER, "Data aggiudicazione/esito", 15));

		listaDefinizioniColonne.add(createDefinizioneColonna("SCHEDECOMPILAZIONE", DizionarioStiliExcelX.STRINGA_ALIGN_CENTER, "Schede in compilazione", 20));
		listaDefinizioniColonne.add(createDefinizioneColonna("SCHEDEATTESAERRORI", DizionarioStiliExcelX.STRINGA_ALIGN_CENTER, "Schede inviate in attesa o con errori", 20));
		listaDefinizioniColonne.add(createDefinizioneColonna("SCHEDESUCCESSO", DizionarioStiliExcelX.STRINGA_ALIGN_CENTER, "Schede inviate con successo", 20));

		listaDefinizioniColonne.add(createDefinizioneColonna("DICITURAWARNING", DizionarioStiliExcelX.STRINGA_ALIGN_LEFT, "Avvisi", 30));

		return listaDefinizioniColonne;
	}

	private Object[] createDefinizioneColonna(String nomeCampoHashMap, int stile, String titoloColonna, int larghezzaColonna) {
		return new Object[] {nomeCampoHashMap, stile, titoloColonna, larghezzaColonna};
	}

	private void popolaFoglio(List<HashMap<String,JdbcParametro>> gareDaEsportare, ArrayList<Object[]> definizioni, DizionarioStiliExcelX dizStiliExcel, Sheet foglioReportSchedeANAC) {

		Row riga = foglioReportSchedeANAC.createRow(1);
		for (int i=0; i < definizioni.size(); i++) {
			Object[] definizioneColonna = definizioni.get(i);
			UtilityExcelX.scriviCella(foglioReportSchedeANAC, i + 1, 1, definizioneColonna[2], dizStiliExcel.getStileExcel(DizionarioStiliExcelX.CELLA_INTESTAZIONE));
			UtilityExcelX.setLarghezzaColonna(foglioReportSchedeANAC, i + 1, (int) definizioneColonna[3]);
		}

		for (int indiceGara=0; indiceGara<gareDaEsportare.size(); indiceGara++) {
			int indiceRiga = 2 + indiceGara;
			riga = foglioReportSchedeANAC.createRow(indiceRiga);
			HashMap<String,JdbcParametro> datiGara = gareDaEsportare.get(indiceGara);
			for (int indiceCampo=0; indiceCampo<definizioni.size(); indiceCampo++) {
				Object[] definizioneColonna = definizioni.get(indiceCampo);
				JdbcParametro parametroCella = datiGara.get((String)definizioneColonna[0]);
				if (parametroCella != null && parametroCella.getValue() != null)
					UtilityExcelX.scriviCella(foglioReportSchedeANAC, indiceCampo + 1, indiceRiga, parametroCella.getValue(), dizStiliExcel.getStileExcel((int)definizioneColonna[1]));
			}
		}
	}

	private void setPrintSetup(Sheet foglio) {
		PrintSetup printSetup = foglio.getPrintSetup();
		printSetup.setLandscape(true);
		printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
		printSetup.setScale((short) 70);
	}
}
