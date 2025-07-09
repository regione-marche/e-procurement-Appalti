package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetStoricoInoltriAction extends Action {

	/**
	 * Manager per la gestione delle interrogazioni di database.
	 */
	private SqlManager sqlManager;

	private TabellatiManager tabellatiManager;

	/**
	 * @param sqlManager the sqlManager to set
	 */
	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	/**
	 * @param sqlManager the sqlManager to set
	 */
	public void setTabellatiManager(TabellatiManager tabellatiManager) {
		this.tabellatiManager = tabellatiManager;
	}

	@Override
	public final ActionForward execute(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		DataSourceTransactionManagerBase.setRequest(request);

		response.setHeader("cache-control", "no-cache");
		response.setContentType("text/text;charset=utf-8");
		PrintWriter out = response.getWriter();

		JSONArray jsonArray = new JSONArray();

		String idcom = new String(request.getParameter("idcom"));
		String idprg = new String(request.getParameter("idprg"));

		try {

			String select = "select COMMSGOGG, "
					+ this.sqlManager.getDBFunction("datetimetostring", new String[] { "COMDATINS" })
					+ ", COMSTATO, desmail from W_INVCOM i left join v_w_invcom_dest d on d.idcom = i.idcom and d.idprg = i.idprg where IDCOMINO=? and IDPRGINO=?";
			List<?> datiComIno = sqlManager.getListVector(select, new Object[] { idcom, idprg });

			if (datiComIno == null || datiComIno.size() == 0) {
				out.println(jsonArray);
				out.flush();
				return null;
			}
			for (int i = 0; i < datiComIno.size(); i++) {

				String commsgogg = SqlManager.getValueFromVectorParam(datiComIno.get(i), 0).getStringValue();
				String comdatins = SqlManager.getValueFromVectorParam(datiComIno.get(i), 1).getStringValue();
				String comstato = SqlManager.getValueFromVectorParam(datiComIno.get(i), 2).getStringValue();
				String comstatoDesc = tabellatiManager.getDescrTabellato("G_z20", comstato);
				String destinatari = SqlManager.getValueFromVectorParam(datiComIno.get(i), 3).getStringValue();
				destinatari = destinatari.replace(",", ", ");

				Object[] row = new Object[4];
				row[0] = commsgogg;
				row[1] = comdatins;
				row[2] = comstatoDesc;
				row[3] = destinatari;

				jsonArray.add(row);
			}
		} catch (SQLException e) {
			throw new JspException(
					"Errore durante la lettura dello storico degli inoltri della comunicazione con id:" + idcom, e);
		}
		out.println(jsonArray);
		out.flush();
		return null;
	}
}