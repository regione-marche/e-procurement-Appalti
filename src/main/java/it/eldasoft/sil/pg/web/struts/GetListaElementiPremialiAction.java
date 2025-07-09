package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetListaElementiPremialiAction extends Action {

	private SqlManager sqlManager;

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	@Override
	public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		DataSourceTransactionManagerBase.setRequest(request);
		response.setHeader("cache-control", "no-cache");
		response.setContentType("text/text;charset=utf-8");
		PrintWriter out = response.getWriter();
		JSONObject result = new JSONObject();

		String elencoe = request.getParameter("elencoe");
		String ngara = request.getParameter("ngara");

		try {
			List<HashMap<String,Object>> listaQuesiti = (List<HashMap<String,Object>>)sqlManager.getListHashMap(
					"select idquesito, titolo, tipoquesito, descquesito, idopzione, descopzione, pesoponmin, pesoponmax" +
							" from elpremalbo where ngara=? and idquesito not in (select idquesito from elpremgare where elpremgare.ngara=?) and (isarchi is null or isarchi <> '1') order by idquesito asc, id asc",
					new Object [] {elencoe,ngara});
			List<HashMap<String,Object>> listaQuesitiReturn = new ArrayList<>();
			if (listaQuesiti != null && listaQuesiti.size() > 0) {
				for (int i = 0; i < listaQuesiti.size(); i++) {
					HashMap<String,Object> quesito = listaQuesiti.get(i);
					Long idquesito = (Long) ((JdbcParametro)quesito.get("IDQUESITO")).getValue();
					Long idopzione = (Long) ((JdbcParametro)quesito.get("IDOPZIONE")).getValue();
					String descopzione = ((JdbcParametro)quesito.get("DESCOPZIONE")).getStringValue();
					String tipoquesito = ((JdbcParametro)quesito.get("TIPOQUESITO")).getStringValue();

					if (StringUtils.isNotEmpty(tipoquesito)) {
						listaQuesitiReturn.add(quesito);
					} else if (!listaQuesitiReturn.isEmpty() && idopzione != 0) {
						HashMap<String,Object> ultimoQuesito = listaQuesitiReturn.get(listaQuesitiReturn.size()-1);
						List<Object[]> listaOpzioni = (List<Object[]>) ultimoQuesito.get("LISTAOPZIONI");
						if (listaOpzioni == null) {
							listaOpzioni = new ArrayList<>();
						}
						if (!listaOpzioni.isEmpty() && idquesito != null && !idquesito.equals(((Object[]) listaOpzioni.get(0))[0])) {
							//idquesito delle opzioni precedenti non è uguale a quella attuale
							throw new Exception("Gli elementi premiali dell'elenco hanno struttura errata");
						}
						listaOpzioni.add(new Object[] { idquesito, idopzione, descopzione });
						ultimoQuesito.put("LISTAOPZIONI", listaOpzioni);
						listaQuesitiReturn.set(listaQuesitiReturn.size()-1,ultimoQuesito);
					}
				}
				result.put("data", listaQuesitiReturn);
			}
			out.println(result);
			out.flush();
			return null;
		} catch (Throwable e) {
			throw new JspException("Errore nella degli elementi premiali dell'elenco" ,e);
		}
	}
}
