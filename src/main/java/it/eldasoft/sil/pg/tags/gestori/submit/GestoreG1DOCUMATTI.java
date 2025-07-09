package it.eldasoft.sil.pg.tags.gestori.submit;

import java.io.ByteArrayOutputStream;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import it.eldasoft.gene.ws.objectstorage.ObjectStorageUtils;
import it.eldasoft.gene.ws.objectstorage.UploadInfo;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

public class GestoreG1DOCUMATTI extends AbstractGestoreChiaveIDAutoincrementante {

	private GenChiaviManager genChiaviManager;

	static Logger logger = Logger.getLogger(GestoreG1DOCUMATTI.class);

	@Override
	public String getEntita() {
		return "G1DOCUMATTI";
	}

	@Override
	public String getCampoNumericoChiave() {
		return "ID";
	}

	@Override
	public void setRequest(HttpServletRequest request) {
		super.setRequest(request);
	}

	@Override
	public void postDelete(DataColumnContainer impl) throws GestoreException {
	}

	@Override
	public void postInsert(DataColumnContainer impl) throws GestoreException {
	}

	@Override
	public void postUpdate(DataColumnContainer impl) throws GestoreException {
	}

	@Override
	public void preDelete(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

		if (logger.isDebugEnabled()) {
			logger.debug("GestoreG1DOCUMATTI.preDelete: inizio metodo");
		}

		try {
			Long id = impl.getColumn("G1DOCUMATTI.ID").getValue().longValue();
			Vector<?> datiDocumento = sqlManager.getVector("select idprg,iddocdig from g1documatti where id=?", new Object[] {id});
			String idprg = ((JdbcParametro) datiDocumento.get(0)).getStringValue();
			String iddocdig = ((JdbcParametro) datiDocumento.get(1)).getStringValue();
			UploadInfo oldBucketInfo = ObjectStorageUtils.getUploadInfo("select bucket_ref, bucket_uuid from w_docdig where idprg=? and iddocdig=? and digent=?",new Object[] {idprg,iddocdig,this.getEntita()});
			ObjectStorageUtils.deleteFile(oldBucketInfo.bucket_ref(),oldBucketInfo.uuid());
			sqlManager.update("delete from W_DOCDIG where idprg=? and iddocdig=? and digent=?", new Object[] {idprg,iddocdig,this.getEntita()});
		} catch (Exception e) {
			throw new GestoreException(e.getMessage(), null, e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("GestoreG1DOCUMATTI.preDelete: fine metodo");
		}
	}

	@Override
	public void preInsert(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

		if (logger.isDebugEnabled()) {
			logger.debug("GestoreG1DOCUMATTI.preInsert: inizio metodo");
		}

		String nomeDocumento = "";
		String idprg = "PG";
		String descrizioneDocumento = impl.getColumn("W_DOCDIG.DIGDESDOC").getValue().getStringValue();
		Long tipologia = 3L;
		String tipoAllegato = null;
		if (impl.isColumn("W_DOCDIG.DIGTIPDOC")) {
			tipoAllegato = impl.getString("W_DOCDIG.DIGTIPDOC");
		}
		try {
			byte[] file = this.getForm().getSelezioneFile().getFileData();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(file);
			nomeDocumento = this.getForm().getSelezioneFile().getFileName();

			if(!FileAllegatoManager.isEstensioneFileAmmessa(nomeDocumento)) {
				throw new GestoreException("Il file selezionato utilizza una estensione non ammessa", "upload.estensioneNonAmmessa", new String[] {nomeDocumento}, null);
			}else if(!FileAllegatoManager.checkFileContent(file,nomeDocumento)){
	          throw new GestoreException("Il file selezionato da caricare viene scartato in quanto ha un contenuto in formato non valido secondo le verifiche mediante la libreria Apache Tika",
	              "upload.erroreVerificaContenuto", new String[]{nomeDocumento}, null);
			} else if(nomeDocumento.toLowerCase().endsWith(".pdf") && FileAllegatoManager.isMaliciousPdf(file, nomeDocumento)) {
				throw new GestoreException("Il file selezionato da caricare viene scartato in quanto sembra codificare dei contenuti non sicuri (malware)",
								"upload.erroreContenutoMalevolo", new String[]{nomeDocumento}, null);
			}

			this.genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager", this.getServletContext(), GenChiaviManager.class);
			Long id = Long.valueOf(genChiaviManager.getNextId("G1DOCUMATTI"));

			Long newIddocdig = (Long) sqlManager.getObject("select coalesce(max(iddocdig),0) + 1 from W_DOCDIG where IDPRG='PG'", null);

			DataColumnContainer dccW_docdig = new DataColumnContainer(new DataColumn[] {
					new DataColumn("W_DOCDIG.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")),
					new DataColumn("W_DOCDIG.IDDOCDIG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, newIddocdig)),
					new DataColumn("W_DOCDIG.DIGENT", new JdbcParametro(JdbcParametro.TIPO_TESTO, "G1DOCUMATTI")),
					new DataColumn("W_DOCDIG.DIGKEY1", new JdbcParametro(JdbcParametro.TIPO_TESTO, id.toString())),
					new DataColumn("W_DOCDIG.DIGNOMDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeDocumento)),
					new DataColumn("W_DOCDIG.DIGDESDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, descrizioneDocumento)),
					new DataColumn("W_DOCDIG.DIGTIPDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, tipoAllegato)),
					new DataColumn("W_DOCDIG.DIGOGG", new JdbcParametro(JdbcParametro.TIPO_BINARIO, baos)),
				});
			dccW_docdig.insert("W_DOCDIG", this.sqlManager);
			ObjectStorageUtils.addFile("PG", newIddocdig);

			impl.addColumn("G1DOCUMATTI.ID", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, id));
			impl.addColumn("G1DOCUMATTI.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, idprg));
			impl.addColumn("G1DOCUMATTI.IDDOCDIG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, newIddocdig));
			impl.addColumn("G1DOCUMATTI.TIPOLOGIA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, tipologia));

		} catch (GestoreException e) {
			throw e;
		} catch (Exception e) {
			throw new GestoreException("Errore durante l'inserimento in G1DOCUMATTI",null,e);
		}
		this.getRequest().setAttribute("RISULTATO", "DOCUMENTOAGGIUNTO");
		
		if (logger.isDebugEnabled()) {
			logger.debug("GestoreG1DOCUMATTI.preInsert: fine metodo");
		}
	}

	@Override
	public void preUpdate(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

		if (logger.isDebugEnabled()) {
			logger.debug("GestoreG1DOCUMATTI.preUpdate: inizio metodo");
		}

		String nomeDocumento = "";
		String idprg = "PG";
		String descrizioneDocumento = impl.getColumn("W_DOCDIG.DIGDESDOC").getValue().getStringValue();
		Long tipologia = 2L;
		String tipoAllegato = null;
		if (impl.isColumn("W_DOCDIG.DIGTIPDOC")) {
			tipoAllegato = impl.getString("W_DOCDIG.DIGTIPDOC");
		}
		try {
			byte[] file = this.getForm().getSelezioneFile().getFileData();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(file);
			nomeDocumento = this.getForm().getSelezioneFile().getFileName();

			if(!FileAllegatoManager.isEstensioneFileAmmessa(nomeDocumento)) {
				throw new GestoreException("Il file selezionato utilizza una estensione non ammessa", "upload.estensioneNonAmmessa", new String[] {nomeDocumento}, null);
			}else if(!FileAllegatoManager.checkFileContent(file,nomeDocumento)){
	          throw new GestoreException("Il file selezionato da caricare viene scartato in quanto ha un contenuto in formato non valido secondo le verifiche mediante la libreria Apache Tika",
	              "upload.erroreVerificaContenuto", new String[]{nomeDocumento}, null);
			} else if(nomeDocumento.toLowerCase().endsWith(".pdf") && FileAllegatoManager.isMaliciousPdf(file, nomeDocumento)) {
				throw new GestoreException("Il file selezionato da caricare viene scartato in quanto sembra codificare dei contenuti non sicuri (malware)",
								"upload.erroreContenutoMalevolo", new String[]{nomeDocumento}, null);
			}

			Long idDocumatti = impl.getLong("G1DOCUMATTI.ID");
			String oldIdprg = impl.getString("G1DOCUMATTI.IDPRG");
			Long oldIddocdig = impl.getLong("G1DOCUMATTI.IDDOCDIG");

			UploadInfo oldBucketInfo = ObjectStorageUtils.getUploadInfo("select bucket_ref, bucket_uuid from w_docdig where idprg=? and iddocdig=? and digent='G1DOCUMATTI' and digkey1=?",new Object[] {oldIdprg,oldIddocdig,idDocumatti.toString()});
			ObjectStorageUtils.deleteFile(oldBucketInfo.bucket_ref(),oldBucketInfo.uuid());
			sqlManager.update("delete from W_DOCDIG where idprg=? and iddocdig=? and digent='G1DOCUMATTI' and digkey1=?", new Object[] {oldIdprg,oldIddocdig,idDocumatti.toString()});

			Long newIddocdig = (Long) sqlManager.getObject("select coalesce(max(iddocdig),0) + 1 from W_DOCDIG where IDPRG='PG'", null);

			DataColumnContainer dccW_docdig = new DataColumnContainer(new DataColumn[] {
					new DataColumn("W_DOCDIG.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")),
					new DataColumn("W_DOCDIG.IDDOCDIG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, newIddocdig)),
					new DataColumn("W_DOCDIG.DIGENT", new JdbcParametro(JdbcParametro.TIPO_TESTO, "G1DOCUMATTI")),
					new DataColumn("W_DOCDIG.DIGKEY1", new JdbcParametro(JdbcParametro.TIPO_TESTO, idDocumatti.toString())),
					new DataColumn("W_DOCDIG.DIGNOMDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeDocumento)),
					new DataColumn("W_DOCDIG.DIGDESDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, descrizioneDocumento)),
					new DataColumn("W_DOCDIG.DIGTIPDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, tipoAllegato)),
					new DataColumn("W_DOCDIG.DIGOGG", new JdbcParametro(JdbcParametro.TIPO_BINARIO, baos)),
				});
			dccW_docdig.insert("W_DOCDIG", this.sqlManager);
			ObjectStorageUtils.addFile("PG", newIddocdig);

			impl.setValue("G1DOCUMATTI.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, idprg));
			impl.setValue("G1DOCUMATTI.IDDOCDIG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, newIddocdig));
			impl.setValue("G1DOCUMATTI.TIPOLOGIA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, tipologia));

		} catch (Exception e) {
			throw new GestoreException("Errore durante l'inserimento in G1DOCUMATTI",null,e);
		}
		this.getRequest().setAttribute("RISULTATO", "DOCUMENTOAGGIUNTO");

		if (logger.isDebugEnabled()) {
			logger.debug("GestoreG1DOCUMATTI.preUpdate: fine metodo");
		}
	}

}
