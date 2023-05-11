package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.utility.UtilityDate;
import net.sf.json.JSONObject;
import it.eldasoft.gene.bl.GenChiaviManager;

public class SetSospensioneGaraAction extends Action {

	/**
	 * Manager per la gestione delle interrogazioni di database.
	 */
	private SqlManager sqlManager;
	
	private GenChiaviManager genChiaviManager;

	/**
	 * @param sqlManager the sqlManager to set
	 */
	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}
	
	/**
	 *
	 * @param genChiaviManager
	 */
	public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
		this.genChiaviManager = genChiaviManager;
	}

	@Override
	public final ActionForward execute(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		DataSourceTransactionManagerBase.setRequest(request);

		response.setHeader("cache-control", "no-cache");
		response.setContentType("text/text;charset=utf-8");
		PrintWriter out = response.getWriter();
	    ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
	    Long syscon = new Long(profilo.getId());

		String codgar = request.getParameter("codgar");
		String opz = request.getParameter("opz");
		String note = request.getParameter("note");
		String oggEvento = codgar;
		Date datpubStipula = null;
		
		if(codgar.indexOf("$")>=0) {
			oggEvento=codgar.substring(codgar.indexOf("$")+1);
		}

		int livEvento = 1;
		String codEvento = "GA_SOSPENDI_GARA";
		String descEvento = "";
		String msgErr = "";

		if ("1".equals(opz)) {			
			descEvento = "Attiva sospensione gara";
		} else if ("2".equals(opz)) {
			descEvento = "Disattiva sospensione gara";
		}

		TransactionStatus status = null;
		boolean commitTransaction = false;
		JSONObject result = new JSONObject();
		String update = "";
		Object par[] = null;

		if ("1".equals(opz)) {
			Long id = new Long(this.genChiaviManager.getNextId("GARSOSPE"));
			update = "insert into GARSOSPE (id, codgar, datini, note, sysconini) values (?, ?, ?, ?, ?);";
			par = new Object[] { id, codgar, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), note, syscon };
		} else {
			update = "update GARSOSPE set DATFINE =?, SYSCONFINE = ? where CODGAR=? and datfine is null";
			par = new Object[] { new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), syscon, codgar };
		}

		try {
			status = this.sqlManager.startTransaction();
			this.sqlManager.update(update, par);
			commitTransaction = true;
		} catch (Exception e) {
			livEvento = 3;
			commitTransaction = false;
			msgErr = e.getMessage();
			throw e;
		} finally {
			if (status != null) {
				if (commitTransaction) {
					this.sqlManager.commitTransaction(status);
				} else {
					this.sqlManager.rollbackTransaction(status);
				}
			}
			LogEvento logEvento = LogEventiUtils.createLogEvento(request);
			logEvento.setLivEvento(livEvento);
			logEvento.setOggEvento(oggEvento);
			logEvento.setCodEvento(codEvento);
			logEvento.setDescr(descEvento);
			logEvento.setErrmsg(msgErr);
			LogEventiUtils.insertLogEventi(logEvento);
		}

		result.put("esito", Boolean.valueOf(commitTransaction));
		result.put("messaggio", msgErr);
		out.println(result);
		out.flush();

		return null;

	}

}
