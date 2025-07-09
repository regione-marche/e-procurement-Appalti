package it.eldasoft.sil.pg.web.struts.npa;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.ActionAjaxLogged;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import net.sf.json.JSONObject;

public class EliminaAnacFormAJAX extends ActionAjaxLogged {

	private GeneManager geneManager;
	private SqlManager sqlManager;
	private PgManager pgManager;

	static Logger logger = Logger.getLogger(EliminaAnacFormAJAX.class);

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}
	
	public void setGeneManager(GeneManager geneManager) {
		this.geneManager = geneManager;
	}
	
	public void setPgManager(PgManager pgManager) {
      this.pgManager = pgManager;
  }

	public final ActionForward runAction(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response) {

		int livEvento = 1;
		String codEvento = "GA_ELIMINA_ANACFORM";
		String oggEvento = "";
		String descrEvento = "Eliminazione della scheda ANAC";
		String errMsgEvento = "";
		String codiceGara=null;
		String ngara=null;
		Long idScheda=null;
		Long iddoc_espd=null;
		String idprg_espd=null;
		String nome_scheda = null;
		Long idschedanparet=null;

		Long idForm = null;
		try {
			DataSourceTransactionManagerBase.setRequest(request);
			response.setHeader("cache-control", "no-cache");
			response.setContentType("text/text;charset=utf-8");

			idForm = Long.parseLong(request.getParameter("idForm"));
			Long countSchede = (Long) sqlManager.getObject("select count(id) from g1npascheda where statonpa > 1 and idform=?", new Object[] {idForm});

			if (countSchede != null && countSchede > 0) {
				return null;
			}

			Vector data = sqlManager.getVector("select g1npascheda.id,codgar,g1npascheda.idprg_espd, g1npascheda.iddoc_espd, g1cf_npa.nome_scheda,g1npascheda.idschedanparet from g1npascheda, g1npaform, g1cf_npa"
                + " where g1npaform.id=g1npascheda.idform and g1npaform.idcfnpa=g1cf_npa.id and idForm=?", new Object[] {idForm});
			
			if(data!=null && data.size()>0){
    	        try {
                idScheda= SqlManager.getValueFromVectorParam(data, 0).longValue();
                codiceGara = SqlManager.getValueFromVectorParam(data, 1).stringValue();
                idprg_espd = SqlManager.getValueFromVectorParam(data, 2).stringValue();
					iddoc_espd = SqlManager.getValueFromVectorParam(data, 3).longValue();
                nome_scheda = SqlManager.getValueFromVectorParam(data, 4).stringValue();
                if(codiceGara.charAt(0)=='$') {
                  ngara = oggEvento = codiceGara.substring(1);
                } else {
                	oggEvento = codiceGara;
                }

                descrEvento += " (" + nome_scheda + ", id. " + idScheda + ")";

                idschedanparet = SqlManager.getValueFromVectorParam(data, 5).longValue();
                if(codiceGara.charAt(0)=='$')
                  ngara=codiceGara.substring(1);

              } catch (GestoreException e) {
                logger.error("Errore nella lettura dei dati associati alla scheda");
              }
			}

			TransactionStatus transazioneUpdate = this.sqlManager.startTransaction();
			boolean commitTransaction = false;
			try {
				geneManager.deleteTabelle(new String[] { "G1NPALOTTISCHEDA" }, "IDSCHEDA in (select ID from G1NPASCHEDA where idform=?)", new Object[] { idForm });

				Object[] parametriDeleteW_DOCDIG;
				String whereDOCDIG;
				if (iddoc_espd != null && idschedanparet==null) {//cancelliamo solo se non si tratta di una scheda di rettifica (if) e se il documento non è pubblicato (exists sotto)
					whereDOCDIG = "(digent='G1NPASCHEDA' and digkey1=?) or (digent='DOCUMGARA' and idprg=? and iddocdig=? and exists (select 1 from documgara where idprg=? and iddocdg=? and (statodoc is null or statodoc <> 5)))";
					parametriDeleteW_DOCDIG = new Object[] { idScheda.toString(), idprg_espd, iddoc_espd.toString(),idprg_espd, iddoc_espd.toString()};
				} else {
					whereDOCDIG = "digent='G1NPASCHEDA' and digkey1=?";
					parametriDeleteW_DOCDIG = new Object[] { idScheda.toString()};
				}
				geneManager.deleteTabelle(new String[] { "W_DOCDIG" }, whereDOCDIG, parametriDeleteW_DOCDIG);

				if(idschedanparet==null && StringUtils.isNotEmpty(idprg_espd) && iddoc_espd != null) {//cancelliamo solo se non si tratta di una scheda di rettifica (if) e se il documento non è pubblicato
    				geneManager.deleteTabelle(new String[] { "DOCUMGARA" }, "codgar = ? and idstampa='DGUE' and idprg=? and iddocdg=? and (statodoc is null or statodoc <> 5)", new Object[] { codiceGara, idprg_espd, iddoc_espd });
					pgManager.updateImprdocgDaDocumgara(codiceGara, ngara);
				}
				geneManager.deleteTabelle(new String[] { "G1NPASCHEDA" }, "IDFORM = ?", new Object[] { idForm });
				geneManager.deleteTabelle(new String[] { "G1NPAFORM" }, "ID = ?", new Object[] { idForm });
				commitTransaction = true;
			} catch (Throwable e) {
				livEvento = 3;
				errMsgEvento = e.getMessage();
				logger.error("Errore durante l'eliminazione della scheda " + errMsgEvento);
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
						errMsgEvento = e.getMessage();
						logger.error("Errore durante la chiusura della transazione");
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
			livEvento = 3;
			errMsgEvento = e.getMessage();
		}
		try {
			LogEvento logEvento = LogEventiUtils.createLogEvento(request);
			logEvento.setCodApplicazione("PG");
			logEvento.setOggEvento(oggEvento);
			logEvento.setLivEvento(livEvento);
			logEvento.setCodEvento(codEvento);
			logEvento.setDescr(descrEvento);
			logEvento.setErrmsg(errMsgEvento);
			LogEventiUtils.insertLogEventi(logEvento);
		} catch (Exception loggerError) {
			logger.error("Errore inaspettato durante la tracciatura su w_logeventi", loggerError);
		} catch (Throwable e) {
			logger.error("Errore inaspettato durante l'inserimento dei messaggi di fine procedura", e);
		}
		return null;
	}
}