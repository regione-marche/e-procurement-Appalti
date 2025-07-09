package it.eldasoft.sil.pg.web.struts.npa;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.tags.gestori.submit.npa.GestoreG1NPAFORM;
import net.sf.json.JSONObject;

public class AggiungiLottiSchedaAJAX extends Action {

	private GeneManager geneManager;
	private SqlManager sqlManager;
	private GenChiaviManager genChiaviManager;
	private PgManager pgManager;

	static Logger logger = Logger.getLogger(AggiungiLottiSchedaAJAX.class);

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
		this.genChiaviManager = genChiaviManager;
	}

	public void setGeneManager(GeneManager geneManager) {
		this.geneManager = geneManager;
	}
	
	public void setPgManager(PgManager pgManager) {
      this.pgManager = pgManager;
    }
  
	@Override
	public final ActionForward execute(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response) throws JspException {

		int livEvento = 1;

		Long idForm = null;
		try {
			DataSourceTransactionManagerBase.setRequest(request);
			response.setHeader("cache-control", "no-cache");
			response.setContentType("text/text;charset=utf-8");

			idForm = Long.parseLong(request.getParameter("idForm"));
			Long countSchede = (Long) sqlManager.getObject("select count(id) from g1npascheda where statonpa <> 1 and idform=?", new Object[] {idForm});

			if (countSchede == null || countSchede > 0) {
				return null;
			}

			TransactionStatus transazioneUpdate = this.sqlManager.startTransaction();
			boolean commitTransaction = false;
			try {

				List<?> datiForm = sqlManager.getVector("select nome_scheda, codgar, g1npascheda.id, modpres from g1npaform,g1cf_npa,g1npascheda"
						+ " where g1cf_npa.id=g1npaform.idcfnpa and g1npascheda.idform=g1npaform.id and g1npaform.id=?", new Object[] { idForm });
				int contatore = 0;
				String nomeScheda = (String) SqlManager.getValueFromVectorParam(datiForm, contatore++).getValue();
				String codgar = (String) SqlManager.getValueFromVectorParam(datiForm, contatore++).getValue();
				Long idScheda = (Long) SqlManager.getValueFromVectorParam(datiForm, contatore++).getValue();
				Long modpres = (Long) SqlManager.getValueFromVectorParam(datiForm, contatore++).getValue();

				String codiceFlusso = (String) sqlManager.getObject("select flusso from g1cf_npa,g1npaform"
						+ " where idcfnpa = g1cf_npa.id and codgar=? order by g1npaform.id asc", new Object[] { codgar });

				setIdPartecipanteNpa(codgar);
				aggiornaVersioneNpaScheda(idScheda);

				if (modpres != null && modpres == 3) {
					geneManager.deleteTabelle(new String[] { "G1NPALOTTISCHEDA" }, "IDSCHEDA=?",
							new Object[] { idScheda });

					Long isFlussoNAG = (Long) sqlManager.getObject(
							"select count(id) from g1cf_npa where nome_scheda=? and (flusso like ? or flusso like ? or flusso like ? or flusso=?)",
							new Object[] { "NAG", "%," + codiceFlusso + ",%", codiceFlusso + ",%", "%," + codiceFlusso,
									codiceFlusso });

					String select = "select g.ngara from gare g where g.ngara <> g.codgar1 and g.codgar1=?";
					if (StringUtils.isNotBlank(nomeScheda) && "A".equals(nomeScheda.substring(0, 1))) {
						select += " and (g.ditta is not null";
						if (isFlussoNAG == null || isFlussoNAG == 0) {
							select += " or g.esineg is not null";
						}
						select += ")";
					} else if ("NAG".equals(nomeScheda)) {
						select += " and g.esineg is not null";
					} else {
						throw new GestoreException("Errore nell'inserimento della scheda: nome_scheda non valido",
								null);
					}
					select += " and g.ngara not in "+GestoreG1NPAFORM.WHERE_LOTTI_SENZA_SCHEDA;


					List<?> datiLotti = sqlManager.getListVector(select, new Object[] { codgar, codgar });
					for (int i = 0; i < datiLotti.size(); i++) {
						String ngara = (String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue();
						Long idLottoScheda = new Long(this.genChiaviManager.getNextId("G1NPALOTTISCHEDA"));
						DataColumnContainer G1NPALOTTISCHEDA = new DataColumnContainer(new DataColumn[] {
								new DataColumn("G1NPALOTTISCHEDA.NGARA",
										new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)),
								new DataColumn("G1NPALOTTISCHEDA.ID",
										new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idLottoScheda)),
								new DataColumn("G1NPALOTTISCHEDA.IDSCHEDA",
										new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idScheda)) });
						try {
							G1NPALOTTISCHEDA.insert("G1NPALOTTISCHEDA", this.sqlManager);
						} catch (SQLException e) {
							throw new GestoreException("Errore nell'inserimento del lotto " + ngara, null, e);
						}
					}
				}
				commitTransaction = true;
			} catch (Throwable e) {
				livEvento = 3;
				logger.error("Errore durante la lettura dei dati dal database", e);
			} finally {
				if (transazioneUpdate != null) {
					try {
						if (commitTransaction) {
							this.sqlManager.commitTransaction(transazioneUpdate);
						} else {
							this.sqlManager.rollbackTransaction(transazioneUpdate);
						}
					} catch (Throwable e) {
						livEvento = 3;
						logger.error("Errore durante la chiusura della transazione", e);
					} finally {
						if (livEvento != 3) {
							PrintWriter out = response.getWriter();
							JSONObject result = new JSONObject();
							out.print(result);
							out.flush();
						}
					}
				}
			}
		} catch (SQLException | IOException e) {
			logger.error("Errore durante la chiusura della transazione", e);
		}
		return null;
	}

	private void aggiornaVersioneNpaScheda(Long idScheda) throws Exception {
	  String versioneNpa = pgManager.getVersioneNpaCorrente();
	  String updateVersioneNpa = "UPDATE G1NPASCHEDA SET vernpa=? WHERE id = ? ";
      this.sqlManager.update(updateVersioneNpa, new Object[] { versioneNpa, idScheda });
	}

  private void setIdPartecipanteNpa(String codgar) throws Exception {
		String getDatiVgareGenere = "select codice " + "from v_gare_genere " + "where genere in (2,100,300) "
				+ "AND codgar = ? ";
		List<String> datiVgareGenere = this.sqlManager.getListVector(getDatiVgareGenere, new Object[] { codgar });
		if (datiVgareGenere != null && !datiVgareGenere.isEmpty()) {
			for (int i = 0; i < datiVgareGenere.size(); i++) {
				String codiceLotto = (String) SqlManager.getValueFromVectorParam(datiVgareGenere.get(i), 0).getValue();
				String uuid = UUID.randomUUID().toString();
				String getDatiDitG = "select dittao " + "from DITG " + "where codgar5 = ? " + "and ngara5 = ? "
						+ "order by numordpl";
				List<String> datiDitG = this.sqlManager.getListVector(getDatiDitG,
						new Object[] { codgar, codiceLotto });
				if (datiDitG != null && !datiDitG.isEmpty()) {
					for (int y = 0; y < datiDitG.size(); y++) {
						String codiceDitta = (String) SqlManager.getValueFromVectorParam(datiDitG.get(y), 0).getValue();
						Vector<?> datiImpr = getDatiImprSQL(codiceDitta);
						if (datiImpr != null && !datiImpr.isEmpty()) {
							Long tipimp = (Long) SqlManager.getValueFromVectorParam(datiImpr, 0).getValue();

							if (tipimp != null && (Long.valueOf(3).equals(tipimp) || Long.valueOf(10).equals(tipimp))) {
								List<String> datiRagimp = getDatiRagimpSQL(codiceDitta);
								if (datiRagimp != null && !datiRagimp.isEmpty()) {
									for (int x = 0; x < datiRagimp.size(); x++) {
										String codiceDittaInt = (String) SqlManager
												.getValueFromVectorParam(datiRagimp.get(x), 0).getValue();
										inserisciIdPartecipNpa(codgar, codiceLotto, codiceDittaInt, uuid);
									}
									inserisciIdPartecipNpa(codgar, codiceLotto, codiceDitta, uuid);
								}
							} else {
								inserisciIdPartecipNpa(codgar, codiceLotto, codiceDitta, uuid);
							}
							uuid = UUID.randomUUID().toString();
						}
					}
				}
			}
		}

	}

	private List<String> getDatiRagimpSQL(String codiceDitta) throws Exception {
		String getDatiRagimp = "select coddic from ragimp where codime9 = ?";
		return this.sqlManager.getListVector(getDatiRagimp, new Object[] { codiceDitta });
	}

	private Vector<?> getDatiImprSQL(String codiceDitta) throws Exception {
		String getDatiImpr = "select tipimp from impr where codimp = ? ";
		return this.sqlManager.getVector(getDatiImpr, new Object[] { codiceDitta });
	}

	private void inserisciIdPartecipNpa(String codgar, String ngara, String codiceDitta, String idPartecipNpa)
			throws Exception {
		String selectG1NPALOTTISCHEDA = "SELECT idpartecipnpa FROM ditg WHERE codgar5 = ? and ngara5 = ? and dittao = ? ";
		String idPartecipnpa = (String) this.sqlManager.getObject(selectG1NPALOTTISCHEDA,
				new Object[] { codgar, ngara, codiceDitta });
		if (idPartecipnpa == null) {
			String updateIdPartecipnpa = "UPDATE ditg SET idpartecipnpa=? WHERE codgar5 = ? and ngara5 = ? and dittao = ? ";
			this.sqlManager.update(updateIdPartecipnpa, new Object[] { idPartecipNpa, codgar, ngara, codiceDitta });
		}
	}

}