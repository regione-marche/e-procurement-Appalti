package it.eldasoft.sil.pg.web.struts.npa;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.eldasoft.gene.ws.objectstorage.ObjectStorageUtils;
import it.eldasoft.utils.properties.ConfigManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.ActionAjaxLogged;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import net.sf.json.JSONObject;

public class CopiaDocumentiInDOCUMATTI extends ActionAjaxLogged {

	static Logger logger = Logger.getLogger(CopiaDocumentiInDOCUMATTI.class);

	private SqlManager sqlManager;

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	private GenChiaviManager genChiaviManager;

	public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
		this.genChiaviManager = genChiaviManager;
	}

	private GeneManager geneManager;

	public void setGeneManager(GeneManager geneManager) {
		this.geneManager = geneManager;
	}

	private FileAllegatoManager fileAllegatoManager;

	public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
		this.fileAllegatoManager = fileAllegatoManager;
	}

	private static final String CODIFICA_FILE_SU_FILESYSTEM = "filesystem";

	@Override
	public final ActionForward runAction(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response) throws IOException {

		if (logger.isDebugEnabled()) {
			logger.debug("CopiaDocumentiInDOCUMATTI: inizio metodo");
		}
		response.setHeader("cache-control", "no-cache");
		response.setContentType("text/text;charset=utf-8");
		JSONObject jsonReturn = new JSONObject();
		PrintWriter out = response.getWriter();
		String pathDocAss = ConfigManager.getValore("it.eldasoft.documentiAssociati");

		try {
			String[] idDocumenti = (String[]) request.getParameterMap().get("arrayIdDocumentiSelezionati[]");
			String[] descrizioniDocumenti = (String[]) request.getParameterMap().get("arrayDescrizioniDocumentiSelezionati[]");
			String dittao = request.getParameter("dittao");
			String ngara = request.getParameter("ngara");

			if (idDocumenti == null) {
				jsonReturn.put("Esito", "Errore");
				jsonReturn.put("MsgErrore", "Nessun documento selezionato.");
				out.print(jsonReturn);
				out.flush();
				return null;
			}
			String stringSelectW_DOCDIG = "select idprg,iddocdig,digtipdoc,dignomdoc,digdesdoc,digfirma,firmacheck,firmacheckts,cast(0 as numeric(1)) as provenienza" +
					" from w_docdig where (idprg=? and iddocdig=?)";
			String castDATA="DATE";
			if("POS".equals(this.sqlManager.getTipoDB()))
				castDATA="TIMESTAMP";
			else if("MSQ".equals(this.sqlManager.getTipoDB()))
				castDATA="DATETIME";

			String stringSelectC0oggass = "select c0aprg,c0acod,cast(null as varchar(5)) as digtipdoc,c0anomogg,c0atit,cast(null as varchar(1)) as digfirma,cast(null as varchar(1)) as firmacheck,cast(null as " + castDATA + ") as firmacheckts, cast(4 as numeric(1)) as provenienza" +
					" from c0oggass where (c0aprg=? and c0acod=?)";

			HashMap<String,String> idToDescrizioni = new HashMap<String,String>();
			List<Vector<?>> listaDocumentiDaCopiare = new ArrayList<Vector<?>>();

			for (int i=0; i<idDocumenti.length; i++) {
				if(idToDescrizioni.containsKey(idDocumenti[i]))//skippo i documenti già aggiunti (in caso di duplicati)
					continue;
				String iddocumenti = idDocumenti[i];
				if (StringUtils.isNotBlank(iddocumenti)) {
					String idprg = iddocumenti.substring(0, 2);
					Long provenienza = null;
					if(iddocumenti.indexOf(CODIFICA_FILE_SU_FILESYSTEM)>0){
						provenienza = 4L;
						iddocumenti=iddocumenti.split(CODIFICA_FILE_SU_FILESYSTEM)[0];
					}
					Long iddocdig = Long.parseLong(iddocumenti.substring(2));

					if(Long.valueOf(4).equals(provenienza)) {
						listaDocumentiDaCopiare.addAll(sqlManager.getListVector(stringSelectC0oggass, new Object[]{idprg,iddocdig}));
					} else {
						listaDocumentiDaCopiare.addAll(sqlManager.getListVector(stringSelectW_DOCDIG, new Object[]{idprg,iddocdig}));
                    }
					idToDescrizioni.put(idDocumenti[i],descrizioniDocumenti[i]);
				}
			}
			
			TransactionStatus transazioneUpdate = this.sqlManager.startTransaction();
			boolean commitTransaction = false;
			try {

				for (int i=0; i<listaDocumentiDaCopiare.size(); i++) {
					Vector<?> vettoreDatiDocumento = listaDocumentiDaCopiare.get(i);
					String idprg = ((JdbcParametro) vettoreDatiDocumento.get(0)).getStringValue();
					Long iddocdig = ((JdbcParametro) vettoreDatiDocumento.get(1)).longValue();
					JdbcParametro digtipdoc = (JdbcParametro) vettoreDatiDocumento.get(2);
					String dignomdoc = ((JdbcParametro) vettoreDatiDocumento.get(3)).getStringValue();
					JdbcParametro digfirma = (JdbcParametro) vettoreDatiDocumento.get(5);
					JdbcParametro firmacheck = (JdbcParametro) vettoreDatiDocumento.get(6);
					JdbcParametro firmacheckts = (JdbcParametro) vettoreDatiDocumento.get(7);
					Long provenienza = ((JdbcParametro) vettoreDatiDocumento.get(8)).longValue();

					Long newIddocdig = (Long) sqlManager.getObject("select coalesce(max(iddocdig),0) + 1 from W_DOCDIG where IDPRG='PG'", null);

					int newIdDOCUMATTI = genChiaviManager.getNextId("G1DOCUMATTI");

					DataColumnContainer campiDaCopiareW_DOCDIG = new DataColumnContainer(new DataColumn[] {
							new DataColumn("W_DOCDIG.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")),
							new DataColumn("W_DOCDIG.IDDOCDIG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, newIddocdig)),
							new DataColumn("W_DOCDIG.DIGENT", new JdbcParametro(JdbcParametro.TIPO_TESTO, "G1DOCUMATTI")),
							new DataColumn("W_DOCDIG.DIGKEY1", new JdbcParametro(JdbcParametro.TIPO_TESTO, String.valueOf(newIdDOCUMATTI))),
							new DataColumn("W_DOCDIG.DIGTIPDOC", digtipdoc),
							new DataColumn("W_DOCDIG.DIGNOMDOC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, dignomdoc)),
							new DataColumn("W_DOCDIG.DIGDESDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, idToDescrizioni.get(idprg+iddocdig+(Long.valueOf(4).equals(provenienza)?CODIFICA_FILE_SU_FILESYSTEM:"")))),
							new DataColumn("W_DOCDIG.DIGFIRMA", digfirma),
							new DataColumn("W_DOCDIG.FIRMACHECK", firmacheck),
							new DataColumn("W_DOCDIG.FIRMACHECKTS", firmacheckts)
					});
					campiDaCopiareW_DOCDIG.getColumn("IDPRG").setChiave(true);
					campiDaCopiareW_DOCDIG.getColumn("IDDOCDIG").setChiave(true);

					BlobFile fileAllegato = null;
					ByteArrayOutputStream baos = null;
					if(Long.valueOf(4).equals(provenienza)) {
						File fileAssociato = new File(pathDocAss + "/" + dignomdoc);
						byte[] bytes = FileUtils.readFileToByteArray(fileAssociato);
						baos = new ByteArrayOutputStream();
						baos.write(bytes);
					} else {
						fileAllegato = fileAllegatoManager.getFileAllegato(idprg, iddocdig);
						if (fileAllegato != null && fileAllegato.getStream() != null) {
							baos = new ByteArrayOutputStream();
							baos.write(fileAllegato.getStream());
						}
					}
					campiDaCopiareW_DOCDIG.addColumn("W_DOCDIG.DIGOGG", JdbcParametro.TIPO_BINARIO, baos);
					campiDaCopiareW_DOCDIG.insert("W_DOCDIG", this.geneManager.getSql());
					ObjectStorageUtils.addFile("PG", newIddocdig);

					DataColumnContainer containerDOCUMATTI = new DataColumnContainer(new DataColumn[] {
							new DataColumn("G1DOCUMATTI.ID", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(newIdDOCUMATTI))),
							new DataColumn("G1DOCUMATTI.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)),
							new DataColumn("G1DOCUMATTI.DITTAO", new JdbcParametro(JdbcParametro.TIPO_TESTO, dittao)),
							new DataColumn("G1DOCUMATTI.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")),
							new DataColumn("G1DOCUMATTI.IDDOCDIG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, newIddocdig)),
//							new DataColumn("G1DOCUMATTI.STATODOC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, 1L)),
							new DataColumn("G1DOCUMATTI.IDORIGINE", new JdbcParametro(JdbcParametro.TIPO_TESTO, idprg + iddocdig + (Long.valueOf(4).equals(provenienza)?CODIFICA_FILE_SU_FILESYSTEM:"")+"_" + dignomdoc)),
							new DataColumn("G1DOCUMATTI.TIPOLOGIA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, 1L))
					});
					containerDOCUMATTI.insert("G1DOCUMATTI", this.geneManager.getSql());
				}
				commitTransaction = true;
			} catch (IOException | SQLException | GestoreException e) {
				throw e;
			} finally {
				if (transazioneUpdate != null) {
					try {
						if (commitTransaction) {
							this.sqlManager.commitTransaction(transazioneUpdate);
						} else {
							this.sqlManager.rollbackTransaction(transazioneUpdate);
						}
					} catch (SQLException e) {
						throw e;
					}
				}
			}
		} catch (Throwable e) {
			String messaggioPerLog = e.getMessage();
			logger.error(messaggioPerLog, e);
			jsonReturn.put("Esito", "Errore");
			jsonReturn.put("MsgErrore", messaggioPerLog);
		}
		out.print(jsonReturn);
		out.flush();
		if (logger.isDebugEnabled()) {
			logger.debug("CopiaDocumentiInDOCUMATTI: fine metodo");
		}
		return null;
	}
}
