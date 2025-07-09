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

public class GetDatiTecnicoAction extends ActionAjaxLogged {

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
	    
		String codtec = request.getParameter("codtec");
		
		if(StringUtils.isNotBlank(codtec)){
            try {
                Vector dati = sqlManager.getVector("select nometim, cogtim, sextim, cftim, dnatim, naztim from teim where codtim=?", new Object[]{codtec});
				json.put("nome",SqlManager.getValueFromVectorParam(dati,0).getValue());
				json.put("cognome",SqlManager.getValueFromVectorParam(dati,1).getValue());
				json.put("sesso",SqlManager.getValueFromVectorParam(dati,2).getValue());
				String cf = (String) SqlManager.getValueFromVectorParam(dati,3).getValue();
				json.put("cf",cf);
				Date dnatec = 	(Date) SqlManager.getValueFromVectorParam(dati,4).getValue();
				if(dnatec!=null){
					Calendar cal = Calendar.getInstance();
					cal.setTime(dnatec);
					json.put("giorno",cal.get(Calendar.DAY_OF_MONTH));
					json.put("mese",cal.get(Calendar.MONTH)+1);
					json.put("anno",cal.get(Calendar.YEAR));
				}
				Long naztei = 	(Long) SqlManager.getValueFromVectorParam(dati,5).getValue();
				if(naztei!=null && naztei!=1L){
					String codice = (String) sqlManager.getObject("select codcatastale from g_nazioni where codice=?", new Object[]{naztei});
					if(StringUtils.isNotBlank(codice)){
						json.put("codiceStato",codice);
					}
				}else{
					json.put("codiceStato","Z000");
					if(StringUtils.isNotBlank(cf) && cf.length()==16)
						json.put("codiceComune",cf.substring(11,15));
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
