package it.eldasoft.sil.pg.tags.gestori.submit.npa;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import it.eldasoft.gene.ws.objectstorage.ObjectStorageUtils;
import it.eldasoft.gene.ws.objectstorage.UploadInfo;
import it.eldasoft.utils.properties.ConfigManager;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.web.struts.UploadMultiploForm;
import it.eldasoft.utils.spring.UtilitySpring;

public class GestoreG1NPAFORM extends AbstractGestoreChiaveIDAutoincrementante {

	/** Logger */
	static Logger logger = Logger.getLogger(GestoreG1NPAFORM.class);

	private GenChiaviManager genChiaviManager;
	
	private PgManagerEst1 pgManagerEst1;
	
	private PgManager pgManager;

	public static final List<Long> statiCancellati = Arrays.asList(20L,34L,50L);
	public static final String elencoStatiCancellati = "(20,34,50)";

	public static final String WHERE_LOTTI_SENZA_SCHEDA = " (select ls.ngara from G1NPALOTTISCHEDA LS where ls.idscheda in ("
			+ "select s.id from G1NPASCHEDA s where s.statonpa not in "+elencoStatiCancellati + " and s.idform in ("
			+ "select f.id from G1NPAFORM f where f.codgar=? and f.idcfnpa in ("
			+ "select cf.id from G1CF_NPA cf where cf.modpres=3)))) ";

	//-------------------- FUNZIONE COUNT SCHEDE --------------------
	public static final String PUBBLICATE_AGGIUDICAZIONI = "inviateAggiudicazioni";
	public static final String PUBBLICATA_AGGIUDICAZIONE_LOTTO = "inviataAggiudicazionePerLotto";
	public static final String PUBBLICATA_P5 = "inviataP5";
	public static final String PUBBLICATA_AD5 = "inviataAD5";
	public static final String CONFERMATE_ADORS2 = "confermateADORS2";
	public static final String INVIATE = "inviate";
	public static final String INVIATE_AD = "inviateAD";
	public static final String INVIATE_NO_PL = "inviateEscludiPL";
	public static final String PRESENTE_CM_IN_COMPILAZIONE = "CMinCompilazione";
	public static final String PRESENTE_CM_1_IN_COMPILAZIONE = "CM1inCompilazione";
	public static final String PRESENTE_ID_IN_COMPILAZIONE = "IDinCompilazione";
	public static final String IN_LAVORAZIONE_CM = "inLavorazioneCM";
	public static final String IN_LAVORAZIONE_ID = "inLavorazioneID";

	public static Long countSchede(String functionID, SqlManager sqlManager, Object[] parametri) throws SQLException {
		Long count = (Long) sqlManager.getObject(GestoreG1NPAFORM.composeQueryCountSchede(functionID), parametri);
		return count;
	}

	private static final String SELECT_BASE = "select count(*) from g1npaform";
	private static final String JOIN_BASE = " inner join g1npascheda on g1npaform.id=g1npascheda.idform inner join g1cf_npa on g1npaform.idcfnpa=g1cf_npa.id";
	private static final String WHERE_BASE = " 1=1";

	private static final String JOIN_GARE = " inner join gare on gare.codgar1=g1npaform.codgar";
	private static final String JOIN_G1NPALOTTISCHEDA = " inner join g1npalottischeda on gare.ngara=g1npalottischeda.ngara and g1npascheda.id=g1npalottischeda.idscheda";
	private static final String JOIN_V_GARE_GENERE = " inner join v_gare_genere on g1npaform.codgar = v_gare_genere.codgar";

	private static final String FILTRO_LOTTO = " and gare.ngara=?";
	private static final String FILTRO_CODGAR = " and g1npaform.codgar=?";
	private static final String FILTRO_NGARA = " and g1npascheda.ngara=?";

	private static final String STATO_PUBBLICATO = " and g1npascheda.statonpa = 60";
	private static final String STATO_NON_ANNULLATO = " and (g1npascheda.isann is null or g1npascheda.isann = '2')";
	private static final String STATO_NON_CANCELLATO = " and g1npascheda.statonpa not in " + elencoStatiCancellati;
	private static final String STATO_CONFERMATO = " and g1npascheda.statonpa >= 30";
	private static final String BLOCCATE_MODIFICHE = " and (statonpa >= 10 or inviabile='1')";
	private static final String STATO_IN_COMPILAZIONE = " and statonpa = 1 and inviabile is null";
	private static final String STATO_IN_LAVORAZIONE_30 = " and statonpa > 1 and statonpa < 30";

	private static final String SCHEDA_AGGIUDICAZIONE = " (g1cf_npa.nome_scheda like 'A%' or g1cf_npa.nome_scheda='P5')";
	private static final String SCHEDA_CM = " and g1cf_npa.nome_scheda like 'CM%'";
	private static final String SCHEDA_CM1 = " and g1cf_npa.nome_scheda='CM1'";
	private static final String SCHEDA_ID = " and g1cf_npa.nome_scheda='ID'";
	private static final String SCHEDA_NON_AGGIUDICAZIONE = " g1cf_npa.nome_scheda = 'NAG'";
	private static final String SCHEDA_AD5 = " and g1cf_npa.nome_scheda='AD5'";
	private static final String SCHEDA_P5 = " and g1cf_npa.nome_scheda='P5'";
	private static final String SCHEDA_AD = " and g1cf_npa.nome_scheda like 'AD%'";
	private static final String SCHEDA_AD_OR_S2 = " and (nome_scheda = 'S2' or nome_scheda like 'AD%' or nome_scheda = 'A3_6')";
	private static final String NON_PL = " and not (nome_scheda like 'PL%' and genere in (1,2,3,100,300))";
	private static final String ESCLUDI_ANN_CONFERMATE = " and not (g1npascheda.statonpa = 30 and g1cf_npa.nome_scheda='ANN')";

	public static String composeWhereSchede(String functionID) {
		String whereComposta = WHERE_BASE;
		// FILTRI CON PARAMETRO
		if (Arrays.asList(PUBBLICATA_AGGIUDICAZIONE_LOTTO).contains(functionID)) {
			whereComposta += FILTRO_LOTTO;
		}
		if (Arrays.asList(IN_LAVORAZIONE_CM,IN_LAVORAZIONE_ID,PRESENTE_ID_IN_COMPILAZIONE,PRESENTE_CM_1_IN_COMPILAZIONE,PRESENTE_CM_IN_COMPILAZIONE,
				PUBBLICATA_AD5, PUBBLICATE_AGGIUDICAZIONI,PUBBLICATA_P5, CONFERMATE_ADORS2, INVIATE,INVIATE_AD, INVIATE_NO_PL).contains(functionID)) {
			whereComposta += FILTRO_CODGAR;
		}
		if (Arrays.asList(PRESENTE_ID_IN_COMPILAZIONE,IN_LAVORAZIONE_ID).contains(functionID)) {
			whereComposta += FILTRO_NGARA;
		}
		// STATO SCHEDE
		if (Arrays.asList(PUBBLICATA_AD5,PUBBLICATA_P5).contains(functionID)) {
			whereComposta += STATO_PUBBLICATO + STATO_NON_ANNULLATO;
		}
		if (Arrays.asList(CONFERMATE_ADORS2).contains(functionID)) {
			whereComposta += STATO_CONFERMATO + STATO_NON_ANNULLATO + STATO_NON_CANCELLATO;
		}
		if (Arrays.asList(INVIATE, INVIATE_NO_PL,INVIATE_AD).contains(functionID)) {
			whereComposta += BLOCCATE_MODIFICHE + STATO_NON_ANNULLATO + STATO_NON_CANCELLATO + ESCLUDI_ANN_CONFERMATE;
		}
		if (Arrays.asList(PRESENTE_ID_IN_COMPILAZIONE,PRESENTE_CM_1_IN_COMPILAZIONE,PRESENTE_CM_IN_COMPILAZIONE).contains(functionID)) {
			whereComposta += STATO_IN_COMPILAZIONE;
		}
		if (Arrays.asList(IN_LAVORAZIONE_CM,IN_LAVORAZIONE_ID).contains(functionID)) {
			whereComposta += STATO_IN_LAVORAZIONE_30 + STATO_NON_ANNULLATO + STATO_NON_CANCELLATO;
		}
		// NOMI SCHEDE
		if (Arrays.asList(PRESENTE_CM_IN_COMPILAZIONE,IN_LAVORAZIONE_CM).contains(functionID)) {
			whereComposta += SCHEDA_CM;
		}
		if (Arrays.asList(PRESENTE_CM_1_IN_COMPILAZIONE).contains(functionID)) {
			whereComposta += SCHEDA_CM1;
		}
		if (Arrays.asList(PRESENTE_ID_IN_COMPILAZIONE,IN_LAVORAZIONE_ID).contains(functionID)) {
			whereComposta += SCHEDA_ID;
		}
		if (Arrays.asList(PUBBLICATA_AD5).contains(functionID)) {
			whereComposta += SCHEDA_AD5;
		}
		if (Arrays.asList(PUBBLICATA_P5).contains(functionID)) {
			whereComposta += SCHEDA_P5;
		}
		if (Arrays.asList(CONFERMATE_ADORS2).contains(functionID)) {
			whereComposta += SCHEDA_AD_OR_S2;
		}
		if (Arrays.asList(PUBBLICATE_AGGIUDICAZIONI, PUBBLICATA_AGGIUDICAZIONE_LOTTO).contains(functionID)) {
			whereComposta += " and ((" + SCHEDA_AGGIUDICAZIONE + STATO_PUBBLICATO + ") or (" + SCHEDA_NON_AGGIUDICAZIONE + STATO_CONFERMATO + "))"  + STATO_NON_ANNULLATO;
		}
		if (Arrays.asList(INVIATE_NO_PL).contains(functionID)) {
			whereComposta += NON_PL;
		}
		if (Arrays.asList(INVIATE_AD).contains(functionID)) {
			whereComposta += SCHEDA_AD;
		}
		return whereComposta;
	}

	public static String composeQueryCountSchede(String functionID) {

		String joinComposta = JOIN_BASE;

		if (Arrays.asList(PUBBLICATA_AGGIUDICAZIONE_LOTTO).contains(functionID)) {
			joinComposta += JOIN_GARE + JOIN_G1NPALOTTISCHEDA;
		}
		if (Arrays.asList(INVIATE_NO_PL).contains(functionID)) {
			joinComposta += JOIN_V_GARE_GENERE;
		}
		String whereComposta = composeWhereSchede(functionID);

		return SELECT_BASE + joinComposta + " where" + whereComposta;
	}
	//-------------------- FINE FUNZIONE COUNT SCHEDE --------------------

	@Override
	public String getCampoNumericoChiave() {
		return "ID";
	}

	@Override
	public String getEntita() {
		return "G1NPAFORM";
	}

	@Override
	public void postDelete(DataColumnContainer datiForm) throws GestoreException {

	}

	@Override
	public void afterInsertEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
		if (logger.isDebugEnabled()) logger.debug("GestoreG1NPAFORM: afterInsertEntita: inizio metodo");

		Long idForm = datiForm.getLong("G1NPAFORM.ID");

		this.genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
		        this.getServletContext(), GenChiaviManager.class);

		String codgar = datiForm.getString("G1NPAFORM.CODGAR");
		Long idcfnpa = datiForm.getLong("G1NPAFORM.IDCFNPA");

		try {
			List<?> datiFormulario = sqlManager.getVector("select islotti, nome_scheda, modpres, flusso from g1cf_npa where id=?", new Object[] {idcfnpa});

			String islotti = (String) SqlManager.getValueFromVectorParam(datiFormulario, 0).getValue();
			Long modpres = (Long) SqlManager.getValueFromVectorParam(datiFormulario, 2).getValue();

			String uuidDaCollegare = null;
			if (StringUtils.isNotEmpty(ConfigManager.getValore("it.eldasoft.bandoavvisosimap.ws.url"))) {
				Vector<?> ultimaSchedaSimileAggiunta = sqlManager.getVector("select g1npascheda.uuid_eform, g1npascheda.statonpa from g1npaform,g1npascheda,g1cf_npa"
						+ " where g1npaform.codgar=? and g1cf_npa.id=? and g1npascheda.idform=g1npaform.id and g1cf_npa.id=g1npaform.idcfnpa order by g1npaform.id desc", new Object[]{codgar, idcfnpa});

				String ultimoUUID_EFORM = null;
				Long statonpaUltimaSchedaSimile = null;
				if (ultimaSchedaSimileAggiunta != null && ultimaSchedaSimileAggiunta.size() > 0) {
					ultimoUUID_EFORM = ((JdbcParametro) ultimaSchedaSimileAggiunta.get(0)).getStringValue();
					statonpaUltimaSchedaSimile = ((JdbcParametro) ultimaSchedaSimileAggiunta.get(1)).longValue();
				}
				if (statiCancellati.contains(statonpaUltimaSchedaSimile)) {
					uuidDaCollegare = ultimoUUID_EFORM;
				}
			}
			pgManager = (PgManager) UtilitySpring.getBean("pgManager",
                this.getServletContext(), PgManager.class);
			String versioneNpa = pgManager.getVersioneNpaCorrente();

			if ("1".equals(islotti)) {
				List<?> datiLotti = sqlManager.getListVector("select ngara from gare where ngara <> codgar1 and codgar1=?", new Object[] {codgar});
				for (int i=0; i<datiLotti.size(); i++) {
					String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
					Long idScheda = new Long(this.genChiaviManager.getNextId("G1NPASCHEDA"));
					DataColumnContainer G1NPASCHEDA = new DataColumnContainer(new DataColumn[] {
							new DataColumn("G1NPASCHEDA.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)),
							new DataColumn("G1NPASCHEDA.ID", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idScheda)),
							new DataColumn("G1NPASCHEDA.IDFORM", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idForm)),
							new DataColumn("G1NPASCHEDA.STATONPA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, 1)),
                            new DataColumn("G1NPASCHEDA.VERNPA", new JdbcParametro(JdbcParametro.TIPO_TESTO, versioneNpa))
                            });

					G1NPASCHEDA.insert("G1NPASCHEDA", this.sqlManager);
				}
			} else {
				Long idScheda = new Long(this.genChiaviManager.getNextId("G1NPASCHEDA"));
				DataColumnContainer G1NPASCHEDA = new DataColumnContainer(new DataColumn[] {
						new DataColumn("G1NPASCHEDA.ID", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idScheda)),
						new DataColumn("G1NPASCHEDA.IDFORM", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idForm)),
						new DataColumn("G1NPASCHEDA.STATONPA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, 1)),
                        new DataColumn("G1NPASCHEDA.VERNPA", new JdbcParametro(JdbcParametro.TIPO_TESTO, versioneNpa))
                        });
				if (StringUtils.isNotEmpty(uuidDaCollegare)) {
					G1NPASCHEDA.addColumn("G1NPASCHEDA.UUID_EFORM", new JdbcParametro(JdbcParametro.TIPO_TESTO, uuidDaCollegare));
				}
				if (((Long) 4L).equals(modpres)) {
					if (datiForm.isColumn("G1NPASCHEDA.NGARA")) {
						String ngara = datiForm.getString("G1NPASCHEDA.NGARA");
						G1NPASCHEDA.addColumn("G1NPASCHEDA.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara));
					} else {
						throw new GestoreException("Non è valorizzato NGARA", null);
					}
				}
				G1NPASCHEDA.insert("G1NPASCHEDA", this.sqlManager);
			}
			//aggancio il DGUE se esistente
			if(datiForm.isColumn("G1CF_NPA.ISESPDfitt") && "1".equals(datiForm.getString("G1CF_NPA.ISESPDfitt"))) {
				Vector<?> data = sqlManager.getVector("select idprg,iddocdg from documgara where idstampa='DGUE' and (isarchi is null or isarchi<>'1') and codgar=? order by iddocdg desc", new Object[]{codgar});
				if(data!=null) {
					String idprg = SqlManager.getValueFromVectorParam(data, 0).getStringValue();
					Long iddocdg = SqlManager.getValueFromVectorParam(data, 1).longValue();
					if (StringUtils.isNotEmpty(idprg) && iddocdg != null) {
						sqlManager.update("update g1npascheda set idprg_espd=?,iddoc_espd=? where idform=?",
								new Object[]{SqlManager.getValueFromVectorParam(data, 0).getStringValue(), SqlManager.getValueFromVectorParam(data, 1).longValue(), idForm});
					}
				}
			}
		} catch (SQLException e) {
			throw new GestoreException("Errore nell'inserimento delle entità figlie", null, e);
		}

		if (logger.isDebugEnabled()) logger.debug("GestoreG1NPAFORM: afterInsertEntita: fine metodo");
	 }

	@Override
	public void postInsert(DataColumnContainer datiForm) throws GestoreException {
		
	}

	@Override
	public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

	}

	@Override
	public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

		if (logger.isDebugEnabled()) logger.debug("GestoreG1NPAFORM: preDelete: inizio metodo");

		GeneManager geneManager = this.getGeneManager();
		Long id = datiForm.getLong("G1NPAFORM.ID");

		geneManager.deleteTabelle(new String[] { "G1NPALOTTISCHEDA" },
				"IDSCHEDA in (select ID from G1NPASCHEDA where idform=?)", new Object[] { id });

		geneManager.deleteTabelle(new String[] { "G1NPASCHEDA" },
				"IDFORM = ?", new Object[] { id });

		if (logger.isDebugEnabled()) logger.debug("GestoreG1NPAFORM: preDelete: fine metodo");
	}

	public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

		if (logger.isDebugEnabled()) logger.debug("GestoreG1NPAFORM: preInsert: inizio metodo");
		super.preInsert(status, datiForm);
		
		if (logger.isDebugEnabled()) logger.debug("GestoreG1NPAFORM: preInsert: fine metodo");
	}

	@Override
	public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

		Boolean isInseritoEFORM = "1".equals(UtilityStruts.getParametroString(this.getRequest(),"isInseritoEFORM"));
		Boolean isCancellatoEFORM = "1".equals(UtilityStruts.getParametroString(this.getRequest(),"isCancellatoEFORM"));

		Boolean isInseritoESPD = "1".equals(UtilityStruts.getParametroString(this.getRequest(),"isInseritoESPD"));
		Boolean isCancellatoESPD = "1".equals(UtilityStruts.getParametroString(this.getRequest(),"isCancellatoESPD"));

		Boolean sbiancaUUIDEFORM = "1".equals(UtilityStruts.getParametroString(this.getRequest(),"sbiancaUUIDEFORM"));

		if (isInseritoEFORM || isCancellatoEFORM) {
			salvataggioDocumento(status, datiForm, "EFORM", (FormFile) ((UploadMultiploForm) this.getForm()).getFormFiles().get(0L));
		}

		if (isInseritoESPD || isCancellatoESPD) {
			salvataggioDocumento(status, datiForm, "ESPD", (FormFile) ((UploadMultiploForm) this.getForm()).getFormFiles().get(1L));
		}

		if (sbiancaUUIDEFORM) {
			Long idScheda = datiForm.getLong("G1NPASCHEDA.ID");
			try {
				sqlManager.update("update G1NPASCHEDA set UUID_EFORM = null where id=?", new Object[] { idScheda });
			} catch (SQLException e) {
				throw new GestoreException("Errore inaspettato nell'aggiornamento di G1NPASCHEDA",null,e);
			}
		}
	}

	private void salvataggioDocumento(TransactionStatus status, DataColumnContainer datiForm, String sezioneDocumento, FormFile ff) throws GestoreException {

		Boolean isInserito = "1".equals(UtilityStruts.getParametroString(this.getRequest(),"isInserito" + sezioneDocumento));
		Boolean isCancellato = "1".equals(UtilityStruts.getParametroString(this.getRequest(),"isCancellato" + sezioneDocumento));

		byte[] file = null;
		String nomeDocumento = "";

		try {
			if (ff != null) {
				file = ff.getFileData();
				nomeDocumento = ff.getFileName();
			} else if (isInserito) {
				throw new GestoreException("File da caricare non trovato", "upload", null);
			}

			if (!(isInserito && StringUtils.isNotBlank(nomeDocumento)) && !isCancellato) {
				throw new GestoreException("Richiesta non valida", null, null);
			}

			Long idForm = datiForm.getLong("G1NPAFORM.ID");
            String codiceGara = datiForm.getString("G1NPAFORM.CODGAR");
            String ngara=null;
            if(codiceGara.charAt(0)=='$')
                ngara=codiceGara.substring(1);
            
			if (isCancellato) {
				Vector<?> documentoDaCancellare = sqlManager.getVector("select idprg_" + sezioneDocumento + " , iddoc_" + sezioneDocumento
						+ " from g1npascheda where idForm=?", new Object[] { idForm });
				String idprg = (String) SqlManager.getValueFromVectorParam(documentoDaCancellare, 0).getValue();
				Long iddoc = (Long) SqlManager.getValueFromVectorParam(documentoDaCancellare, 1).getValue();
				boolean isEspdAndPubb=false;
				if("ESPD".equals(sezioneDocumento)){
					Long count = (Long) sqlManager.getObject("select count(1) from documgara where idprg=? and iddocdg=? and (statodoc is null or statodoc <> 5)",
							new Object[]{idprg,iddoc});
					isEspdAndPubb = count!= null && count==0;
				}
				if(!isEspdAndPubb) {
					UploadInfo oldBucketInfo = ObjectStorageUtils.getUploadInfo("select bucket_ref, bucket_uuid from w_docdig where idprg=? and iddocdig=?",new Object[] {idprg,iddoc});
					ObjectStorageUtils.deleteFile(oldBucketInfo.bucket_ref(),oldBucketInfo.uuid());
					geneManager.deleteTabelle(new String[]{"W_DOCDIG"}, "idprg=? and iddocdig=?", new Object[]{idprg, iddoc});
					geneManager.deleteTabelle(new String[]{"DOCUMGARA"}, "idprg=? and iddocdg=?", new Object[]{idprg, iddoc});
				}
				if ("EFORM".equals(sezioneDocumento)) {
					sqlManager.update("update g1npascheda set dnot_eform=null where idform=?", new Object[] {idForm});
				}
			}
			if (isCancellato && !(isInserito && StringUtils.isNotBlank(nomeDocumento))) {
				sqlManager.update("update G1NPASCHEDA set IDPRG_" + sezioneDocumento + "=null, IDDOC_" + sezioneDocumento + "=null"
						+ " where idform=?", new Object[] {idForm});
			}

			if (isInserito) {
				if(!FileAllegatoManager.isEstensioneFileAmmessa(nomeDocumento)) {
					throw new GestoreException("Il file selezionato utilizza una estensione non ammessa", "upload.estensioneNonAmmessa", new String[] {nomeDocumento}, null);
				}else if(!FileAllegatoManager.checkFileContent(file,nomeDocumento)){
		          throw new GestoreException("Il file selezionato da caricare viene scartato in quanto ha un contenuto in formato non valido secondo le verifiche mediante la libreria Apache Tika",
		              "upload.erroreVerificaContenuto", new String[]{nomeDocumento}, null);
				} else if(nomeDocumento.toLowerCase().endsWith(".pdf") && FileAllegatoManager.isMaliciousPdf(file, nomeDocumento)) {
					throw new GestoreException("Il file selezionato da caricare viene scartato in quanto sembra codificare dei contenuti non sicuri (malware)",
									"upload.erroreContenutoMalevolo", new String[]{nomeDocumento}, null);
				}
				
				ByteArrayOutputStream baos = null;
				baos = new ByteArrayOutputStream();
				baos.write(file);
				Long maxIdDocDig = (Long) sqlManager.getObject("select max(iddocdig) from w_docdig where idprg='PG'", null);
				Long idScheda = (Long) sqlManager.getObject("select id from g1npascheda where idform=?", new Object[] {idForm});
				Long newIdDocDig = null;
				if (maxIdDocDig == null) {
					newIdDocDig = 0L;
				} else {
					newIdDocDig = maxIdDocDig+1;
				}

				String descrizioneDocumento = "";

				if ("EFORM".equals(sezioneDocumento)) {
					String numeroEFORM = (String) sqlManager.getObject("select eform from g1cf_npa where id=(select idcfnpa from g1npaform where id=?)", new Object[] {idForm});
					descrizioneDocumento = "eForm " + numeroEFORM;
				} else {
					descrizioneDocumento = "Documento di gara unico europeo (DGUE)";
				}

				DataColumnContainer containerDOCDIG = new DataColumnContainer( new DataColumn[] {
					new DataColumn("W_DOCDIG.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")),
					new DataColumn("W_DOCDIG.IDDOCDIG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, newIdDocDig)),
					new DataColumn("W_DOCDIG.DIGENT", new JdbcParametro(JdbcParametro.TIPO_TESTO, "G1NPASCHEDA")),
					new DataColumn("W_DOCDIG.DIGKEY1", new JdbcParametro(JdbcParametro.TIPO_TESTO, idScheda.toString())),
					new DataColumn("W_DOCDIG.DIGNOMDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeDocumento)),
					new DataColumn("W_DOCDIG.DIGDESDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, descrizioneDocumento)),
					new DataColumn("W_DOCDIG.DIGOGG", new JdbcParametro(JdbcParametro.TIPO_BINARIO, baos))
				});
				
				if ("ESPD".equals(sezioneDocumento)) {
				  
				  String selectDOCUMGARA_MAX = "select max(NORDDOCG) from DOCUMGARA where CODGAR=?";
		          Long nProgressivoDOCUMGARA = (Long) sqlManager.getObject(selectDOCUMGARA_MAX, new Object[] { codiceGara });
		          if (nProgressivoDOCUMGARA == null) nProgressivoDOCUMGARA = new Long(0);
		          nProgressivoDOCUMGARA = new Long(nProgressivoDOCUMGARA.longValue() + 1);

				  Long gruppo = null;
                  String gartel=null;
                  Long offtel = null;
                  Vector<?> datiTorn = this.sqlManager.getVector("select gartel, iterga, offtel from torn where codgar=?", new Object[] {codiceGara});
                  if(datiTorn != null && datiTorn.size() >0) {
                    gartel = SqlManager.getValueFromVectorParam(datiTorn, 0).getStringValue();
                    Long iterga = SqlManager.getValueFromVectorParam(datiTorn, 1).longValue();
                    offtel = SqlManager.getValueFromVectorParam(datiTorn, 2).longValue();
                    if((new Long(2).equals(iterga) || new Long(4).equals(iterga)))
						gruppo=6L;
                  }
					Long[] tipologiaAndGruppo = getTipologiaAndGruppo(codiceGara,gruppo);
					if(tipologiaAndGruppo[0] == null) {
						tipologiaAndGruppo[0]=new Long(103);
						tipologiaAndGruppo[1]=new Long(1);
					}
                  
					String nomeCampoChiaveNumerica = "NUMORD";
					String entita = "DOCUMGARA";
					String nomeCampoChiave = "CODGAR";
					Long maxNumord = (Long) sqlManager.getObject("select max(" + nomeCampoChiaveNumerica + ") from " + entita + " where " + nomeCampoChiave + "=? and gruppo=?", new Object[] { codiceGara, tipologiaAndGruppo[1] });
					if (maxNumord == null) {
						maxNumord = 0L;
					}
					Long numord = maxNumord + 1;
                  
		          //inserimento documento di gara
				  DataColumnContainer containerDOCUMGARA = new DataColumnContainer( new DataColumn[] {
				        new DataColumn("DOCUMGARA.CODGAR", new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceGara)),
				        new DataColumn("DOCUMGARA.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)),
				        new DataColumn("DOCUMGARA.NORDDOCG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, nProgressivoDOCUMGARA)),
				        new DataColumn("DOCUMGARA.NUMORD", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numord)),
	                    new DataColumn("DOCUMGARA.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")),
	                    new DataColumn("DOCUMGARA.IDDOCDG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, newIdDocDig)),
	                    new DataColumn("DOCUMGARA.TIPOLOGIA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, tipologiaAndGruppo[0])),
	                    new DataColumn("DOCUMGARA.GRUPPO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,tipologiaAndGruppo[1])),
	                    new DataColumn("DOCUMGARA.DESCRIZIONE", new JdbcParametro(JdbcParametro.TIPO_TESTO, "Documento di gara unico europeo (DGUE)")),
	                    new DataColumn("DOCUMGARA.IDSTAMPA", new JdbcParametro(JdbcParametro.TIPO_TESTO, "DGUE"))
	                });
				  
				  if(new Long(6).equals(tipologiaAndGruppo[1])) {
  				    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", this.getServletContext(),
                        TabellatiManager.class);
                    String desc = tabellatiManager.getDescrTabellato("A1173", "1");
                    if(desc!=null && !"".equals(desc))
                      desc = desc.substring(0,1);
                    String allmail="2";
                    if("0".equals(desc) && "1".equals(gartel)) {
                      Long count = (Long) sqlManager.getObject("select count(*) from pubbli where (tippub = 13 or tippub = 23) and codgar9 = ?", new Object[]{codiceGara});
                      if(count == null || count.intValue()==0)//no occorrenze=invito non pubblicato
                        allmail="1"; 
                    }
                    
                    containerDOCUMGARA.addColumn("DOCUMGARA.ALLMAIL", new JdbcParametro(JdbcParametro.TIPO_TESTO, allmail));
  				  }
				  containerDOCUMGARA.insert("DOCUMGARA",sqlManager);
				  
				  containerDOCDIG.setValue("W_DOCDIG.DIGENT", "DOCUMGARA");
				  containerDOCDIG.setValue("W_DOCDIG.DIGKEY1", codiceGara);
				  containerDOCDIG.addColumn("W_DOCDIG.DIGKEY2", new JdbcParametro(JdbcParametro.TIPO_TESTO, nProgressivoDOCUMGARA.toString()));
				  containerDOCDIG.insert("W_DOCDIG", sqlManager);
				  
	              pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
	                     this.getServletContext(), PgManagerEst1.class);
	               
	              if("1".equals(gartel) && !new Long (3).equals(offtel))
	              pgManagerEst1.gestioneDocDGUEConcorrenti(codiceGara, ngara, tipologiaAndGruppo[1], 1L, false);
				}else {
				  containerDOCDIG.insert("W_DOCDIG", sqlManager);
				}
				ObjectStorageUtils.addFile("PG", newIdDocDig);
				
				
				
				sqlManager.update("update G1NPASCHEDA set idprg_" + sezioneDocumento + "=?, iddoc_" + sezioneDocumento + "=?"
						+ " where idform=?", new Object[] {"PG",newIdDocDig,idForm});
			}
			pgManager = (PgManager) UtilitySpring.getBean("pgManager",
                this.getServletContext(), PgManager.class);
			
			pgManager.updateImprdocgDaDocumgara(codiceGara, ngara);
         
		} catch (FileNotFoundException e) {
			throw new GestoreException("File da caricare non trovato", "upload", e);
		} catch (IOException e) {
			throw new GestoreException(
					"Si è verificato un errore durante la scrittura del buffer per il salvataggio del file allegato su DB",
					"upload", e);
		} catch (SQLException e) {
			throw new GestoreException("Si è verificato un errore durante la scrittura del file su DB", "upload", e);
		}
	}
	
	private Long[] getTipologiaAndGruppo(String codgar,Long gruppo) throws SQLException, GestoreException {
	    Long[] tipologiaAndGruppo = new Long[2];
		List<?> w9cfPubb = new ArrayList<>();
		if(gruppo!=null){
			w9cfPubb = sqlManager.getListVector("select ID, GRUPPO, CL_WHERE_VIS, CL_WHERE_ULT from G1CF_PUBB where GRUPPO =? order by gruppo, ID desc", new Object[]{gruppo});
		} else {
			w9cfPubb = sqlManager.getListVector("select ID, GRUPPO, CL_WHERE_VIS, CL_WHERE_ULT from G1CF_PUBB where GRUPPO in (1,6) order by gruppo, ID desc", null);
		}
		if (w9cfPubb != null && w9cfPubb.size() > 0) {

	      for (int i = 0; i < w9cfPubb.size(); i++) {
	        tipologiaAndGruppo[0] = SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 0).longValue();
	        tipologiaAndGruppo[1] = SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 1).longValue();
	        String clausolaWhereVis = (String) SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 2).getValue();
	        String clausolaWhereUlt = (String) SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 3).getValue();
	        if (clausolaWhereVis != null && !clausolaWhereVis.equals("")) {
	          clausolaWhereVis = " and (" + clausolaWhereVis + ")";
	        }
	        if (clausolaWhereUlt != null && !clausolaWhereUlt.equals("")) {
	          clausolaWhereVis = clausolaWhereVis + " and (" + clausolaWhereUlt + ")";
	        }
	        String selectVisibile = "select count(*) from TORN left outer join GARE on TORN.CODGAR=GARE.CODGAR1 where TORN.CODGAR=? ";
	        Long countVisibile = (Long) sqlManager.getObject(selectVisibile + clausolaWhereVis, new Object[] {codgar});
	        if(countVisibile.intValue()>0){
	          break;
	        }
	      }
	    }
	    return tipologiaAndGruppo;
	}
}
