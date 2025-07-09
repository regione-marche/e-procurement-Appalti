package it.eldasoft.sil.pg.web.struts.npa;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.ActionAjaxLogged;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class GetDatiImpresaAction extends ActionAjaxLogged {

	private SqlManager sqlManager;
	
	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}
	
	@Override
	protected ActionForward runAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		DataSourceTransactionManagerBase.setRequest(request);

	    response.setHeader("cache-control", "no-cache");
	    response.setContentType("text/text;charset=utf-8");
	    PrintWriter out = response.getWriter();

	    JSONObject json = new JSONObject();
	    
		String codimp = request.getParameter("codimp");
		
		if(StringUtils.isNotBlank(codimp)){
            try {
                Vector dati = sqlManager.getVector("select impr.ninps, impr.nomimp, impr.cfimp, impr.indimp, impr.nciimp, impr.nazimp, g_comuni.codcat from impr left join g_comuni on (impr.codcit=g_comuni.codistat) where  impr.codimp=?", new Object[]{codimp});
				json.put("matricolaINPS",SqlManager.getValueFromVectorParam(dati,0).getValue());
				json.put("cognome",SqlManager.getValueFromVectorParam(dati,1).getValue());
				json.put("cf",SqlManager.getValueFromVectorParam(dati,2).getValue());
				json.put("indirizzo",StringUtils.trimToEmpty((String) SqlManager.getValueFromVectorParam(dati,3).getValue())
						+" "+ StringUtils.trimToEmpty((String) SqlManager.getValueFromVectorParam(dati,4).getValue()));

				Long naztei = 	(Long) SqlManager.getValueFromVectorParam(dati,5).getValue();
				if(naztei!=null && naztei!=1L){
					String codice = (String) sqlManager.getObject("select codcatastale from g_nazioni where codice=?", new Object[]{naztei});
					if(StringUtils.isNotBlank(codice)){
						json.put("codiceStato",codice);
					}
				}else{
					json.put("codiceStato","Z000");
					json.put("codiceComune",SqlManager.getValueFromVectorParam(dati,6).getValue());
				}
			} catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

		out.println(json);
	    out.flush();

	    return null;
	}

}
