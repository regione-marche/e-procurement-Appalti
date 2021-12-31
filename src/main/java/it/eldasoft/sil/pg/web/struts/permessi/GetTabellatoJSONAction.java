package it.eldasoft.sil.pg.web.struts.permessi;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.Tabellato;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetTabellatoJSONAction extends ActionBaseNoOpzioni {

	private TabellatiManager tabellatiManager; 
	
	public void setTabellatiManager(TabellatiManager tabellatiManager) {
		this.tabellatiManager = tabellatiManager;
	}
	
	@Override
	protected ActionForward runAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		DataSourceTransactionManagerBase.setRequest(request);

	    response.setHeader("cache-control", "no-cache");
	    response.setContentType("text/text;charset=utf-8");
	    PrintWriter out = response.getWriter();

	    JSONArray jsonArray = new JSONArray();
	    
		String codiceTabellato = request.getParameter("tab1cod");
		
		if (StringUtils.isNotEmpty(codiceTabellato)) {
			List<Tabellato> listaTabellato = this.tabellatiManager.getTabellato(codiceTabellato);

			if (listaTabellato != null && listaTabellato.size() > 0) {
				for (int i=0; i < listaTabellato.size(); i++) {
					jsonArray.add(listaTabellato.get(i));
				}
			}
		}

		out.println(jsonArray);
	    out.flush();

	    return null;
	}

}
