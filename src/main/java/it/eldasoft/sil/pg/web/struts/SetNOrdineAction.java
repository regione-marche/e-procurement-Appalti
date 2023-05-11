package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import net.sf.json.JSONObject;
import it.eldasoft.gene.bl.GenChiaviManager;

public class SetNOrdineAction extends Action {

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

		String genere = request.getParameter("genere");
		String ngara = request.getParameter("ngara");
		String modcont = "";
		if ("3".equals(genere)) {
			modcont = request.getParameter("modcont");
		}

		String msgErr = "";

		TransactionStatus status = null;
		boolean commitTransaction = false;
		JSONObject result = new JSONObject();
		String update = "";
		Object par[] = null;
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		String strDate = dateFormat.format(date);
		String anno2C = strDate.substring(2, 4);
		String select = "";

		try {
			status = this.sqlManager.startTransaction();

			select = "select count(*) from gare where nrepat like ?";
			Long cambioAnno = (Long) sqlManager.getObject(select, new Object[] { "%/" + anno2C });
			if (cambioAnno.equals(new Long(0))) {
				update = "update W_GENCHIAVI set CHIAVE =? where TABELLA=?";
				par = new Object[] { Long.valueOf(0), "GARE.NREPAT" };
				this.sqlManager.update(update, par);
				par = new Object[] { Long.valueOf(0), "GARE.NREPAT_AQ" };
				this.sqlManager.update(update, par);
			}

			commitTransaction = true;
		} catch (Exception e) {
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
		}

		try {
			status = this.sqlManager.startTransaction();

			// Recupera valore w_genchiavi
			select = "select g.codgar1,g.ditta,t.accqua from gare g left join torn t on g.codgar1=t.codgar where g.ngara = ?";
			List<?> dati = sqlManager.getListVector(select, new Object[] { ngara });
			String codgar = "";
			String ditta = "";
			String accqua = "";

			if (dati != null && dati.size() > 0) {
				codgar = (String) SqlManager.getValueFromVectorParam(dati.get(0), 0).getValue();
				codgar = UtilityStringhe.convertiNullInStringaVuota(codgar);
				ditta = (String) SqlManager.getValueFromVectorParam(dati.get(0), 1).getValue();
				ditta = UtilityStringhe.convertiNullInStringaVuota(ditta);
				accqua = (String) SqlManager.getValueFromVectorParam(dati.get(0), 2).getValue();
				accqua = UtilityStringhe.convertiNullInStringaVuota(accqua);
			}
			
			Long id;
			if("1".equals(accqua)) {
			  id = new Long(this.genChiaviManager.getNextId("GARE.NREPAT_AQ"));
			}else {
			  id = new Long(this.genChiaviManager.getNextId("GARE.NREPAT"));
			}
			String nOrdine = Long.toString(id) + "/" + anno2C;
			if ("2".equals(modcont)) {
				update = "update GARE set NREPAT =? where CODGAR1=? and DITTA=?";
				par = new Object[] { nOrdine, codgar, ditta };
			} else {
				update = "update GARE set NREPAT =? where NGARA=?";
				par = new Object[] { nOrdine, ngara };
			}

			this.sqlManager.update(update, par);
			commitTransaction = true;
		} catch (Exception e) {
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
		}

		result.put("esito", Boolean.valueOf(commitTransaction));
		result.put("messaggio", msgErr);
		out.println(result);
		out.flush();

		return null;

	}

}
