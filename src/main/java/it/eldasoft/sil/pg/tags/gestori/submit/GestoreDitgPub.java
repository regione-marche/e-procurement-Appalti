package it.eldasoft.sil.pg.tags.gestori.submit;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import it.eldasoft.gene.ws.objectstorage.ObjectStorageUtils;
import it.eldasoft.gene.ws.objectstorage.UploadInfo;
import it.eldasoft.utils.utility.UtilityNumeri;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

public class GestoreDitgPub extends AbstractGestoreEntita {

	static Logger logger = Logger.getLogger(GestoreDitgPub.class);

	@Override
	public String getEntita() {
		return "DITGPUB";
	}

	@Override
	public void setRequest(HttpServletRequest request) {
		super.setRequest(request);
	}

	public GestoreDitgPub() {
		super(false);
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
	}

	@Override
	public void preInsert(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
	}

	@Override
	public void preUpdate(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

		if (logger.isDebugEnabled()) {
			logger.debug("GestoreDitgPub.preUpdate: inizio metodo");
		}

		String ngara = this.getRequest().getParameter("ngara");
		String codgar = this.getRequest().getParameter("codgar");
		int numeroRighe = 0;
		String numRighe = this.getRequest().getParameter("numeroRighe");
		if (numRighe != null && numRighe.length() > 0) {
			numeroRighe = UtilityNumeri.convertiIntero(this.getRequest().getParameter("numeroRighe")).intValue();
		}

		try {
			Long iterga = (Long) this.sqlManager.getObject("select iterga from torn where codgar=(select codgar1 from gare where ngara=?)", new Object[] { ngara });
			for (int i = 1; i <= numeroRighe; i++) {
				DataColumnContainer dataColumnContainerDiRiga = new DataColumnContainer(impl.getColumnsBySuffix("_" + i, false));
				if (dataColumnContainerDiRiga.isModifiedColumn("V_DITGPUB.LIVPUBATTI")) {
					String dittao = dataColumnContainerDiRiga.getColumn("V_DITGPUB.DITTAO").getValue().getStringValue();
					String dittaoDITG = dittao;

					String ngaraSecondoGrado = null;
					String codgarSecondoGrado = null;
					//Se concorso progettazione in 2 gradi con ditta singola primo grado e RT nel secondo si recupera dittao del primo grado
					if (((Long)9L).equals(iterga)) {
						Vector<JdbcParametro> datiSecondoGrado = sqlManager.getVector("select ngara,codgar1 from gare where ngarapa=?", new Object[] {ngara});
						if (datiSecondoGrado != null) {
							ngaraSecondoGrado = (String) datiSecondoGrado.get(0).getValue();
							codgarSecondoGrado = (String) datiSecondoGrado.get(1).getValue();
						}
						if (StringUtils.isNotEmpty(ngaraSecondoGrado)) {
							String dittaoPrimoGrado = (String) sqlManager.getObject("select dittainv from ditg where dittao=? and ngara5=? and codgar5=?", new Object[] {dittao, ngaraSecondoGrado, codgarSecondoGrado});
								if (StringUtils.isNotEmpty(dittaoPrimoGrado))
									dittaoDITG = dittaoPrimoGrado;
						}
					}

					Long livpubatti = dataColumnContainerDiRiga.getColumn("V_DITGPUB.LIVPUBATTI").getValue().longValue();
					Long livpubattiOriginale = dataColumnContainerDiRiga.getColumn("V_DITGPUB.LIVPUBATTI").getOriginalValue().longValue();

					if ((livpubattiOriginale == null || Arrays.asList(2L,3L).contains(livpubattiOriginale)) && ((Long) 1L).equals(livpubatti)) {
						Vector<DataColumn> elencoCampi = new Vector<>();
						elencoCampi.add(new DataColumn("DITGPUB.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
						elencoCampi.add(new DataColumn("DITGPUB.CODGAR", new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar)));
						elencoCampi.add(new DataColumn("DITGPUB.DITTAO", new JdbcParametro(JdbcParametro.TIPO_TESTO, dittao)));

						AbstractGestoreChiaveIDAutoincrementante gestoreDITGPUB = new DefaultGestoreEntitaChiaveIDAutoincrementante("DITGPUB", "ID", this.getRequest());
						DataColumnContainer containerDITGPUB = new DataColumnContainer(elencoCampi);
						gestoreDITGPUB.inserisci(status, containerDITGPUB);
					} else if (((Long) 1L).equals(livpubattiOriginale) && !((Long) 1L).equals(livpubatti)) {
						List<Vector<JdbcParametro>> elencoG1DOCUMATTI = sqlManager.getListVector("select idprg,iddocdig from G1DOCUMATTI where ngara=? and dittao=?", new Object[] {ngara,dittao});
                        for (Vector<JdbcParametro> datiDocumento : elencoG1DOCUMATTI) {
                            String idprg = (datiDocumento.get(0)).getStringValue();
                            String iddocdig = (datiDocumento.get(1)).getStringValue();

                            UploadInfo oldBucketInfo = ObjectStorageUtils.getUploadInfo("select bucket_ref, bucket_uuid from w_docdig where idprg=? and iddocdig=?", new Object[]{idprg, iddocdig});
                            ObjectStorageUtils.deleteFile(oldBucketInfo.bucket_ref(), oldBucketInfo.uuid());
                            sqlManager.update("delete from w_docdig where idprg=? and iddocdig=? and digent=?", new Object[]{idprg, iddocdig, "G1DOCUMATTI"});
                        }
						geneManager.deleteTabelle(new String[] {"G1DOCUMATTI"},"dittao=? and ngara=?", new Object[] { dittao, ngara });
						geneManager.deleteTabelle(new String[] {"DITGPUB"},"ngara=? and codgar=? and dittao=?", new Object[] {ngara,codgar,dittao});
					}

					DataColumnContainer containerDITG = createContainerDITG(ngara, codgar, dittaoDITG, livpubatti, livpubattiOriginale);
					containerDITG.update("DITG",sqlManager);

					if (StringUtils.isNotEmpty(ngaraSecondoGrado)) {
						DataColumnContainer containerDITGSecondoGrado = createContainerDITG(ngaraSecondoGrado, codgarSecondoGrado, dittao, livpubatti, livpubattiOriginale);
						containerDITGSecondoGrado.update("DITG",sqlManager);
					}
				}
			}
		} catch (Exception e) {
			throw new GestoreException(e.getMessage(), null, e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("GestoreDitgPub.preUpdate: fine metodo");
		}
	}

	private DataColumnContainer createContainerDITG(String ngara, String codgar, String dittao, Long livpubatti, Long livpubattiOriginale) throws GestoreException {
		Vector<DataColumn> elencoCampiDITG = new Vector<>();
		elencoCampiDITG.add(new DataColumn("DITG.NGARA5", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
		elencoCampiDITG.add(new DataColumn("DITG.CODGAR5", new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar)));
		elencoCampiDITG.add(new DataColumn("DITG.DITTAO", new JdbcParametro(JdbcParametro.TIPO_TESTO, dittao)));
		elencoCampiDITG.add(new DataColumn("DITG.LIVPUBATTI", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, livpubatti)));

		elencoCampiDITG.get(0).setChiave(true);
		elencoCampiDITG.get(0).setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara));
		elencoCampiDITG.get(1).setChiave(true);
		elencoCampiDITG.get(1).setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar));
		elencoCampiDITG.get(2).setChiave(true);
		elencoCampiDITG.get(2).setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, dittao));
		elencoCampiDITG.get(3).setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_NUMERICO,livpubattiOriginale));
		DataColumnContainer containerDITG = new DataColumnContainer(elencoCampiDITG);
		return containerDITG;
	}

}
