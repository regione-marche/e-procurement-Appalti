package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetListaUtentiStipulaAction extends Action {

  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
	    this.sqlManager = sqlManager;
  }


  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();
    int total = 0;
    List<HashMap<String, Object>> hMap = new ArrayList<HashMap<String,Object>>();
    
    ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long usrCorrente = Long.valueOf(profilo.getId());
    
    String idStipula = request.getParameter("idStipula");
    String codStipula = null;
    String cenint = null;
    Boolean esito = false;
    String selectV_GARE_STIPULA = "select codstipula,codgar,cenint from v_gare_stipula where id = ? ";
    
    List<?> datiGareStipula = this.sqlManager.getVector(selectV_GARE_STIPULA, new Object[] {Long.valueOf(idStipula)});
    if (datiGareStipula != null && datiGareStipula.size() > 0) {
    	codStipula = (String) SqlManager.getValueFromVectorParam(datiGareStipula, 0).getValue();
    	cenint = (String) SqlManager.getValueFromVectorParam(datiGareStipula, 2).getValue();
    }
    
    boolean isAssocUffintAbilitata = false;    
    String codein = null;
    if ("1".equals(ConfigManager.getValore("it.eldasoft.associazioneUffintAbilitata"))) {
    	isAssocUffintAbilitata = true;
    	codein = (String) request.getSession().getAttribute(
    			CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
    }
    
    String selectUtenti="";
    List<?> datiUtenti = null;
    
    if (codein != null && !"".equals(codein)) {
    	selectUtenti = "select u.sysute, u.email, u.syscon, g.autori"
    		+ " from usrsys u"
    		+ " join usr_ein e on e.syscon=u.syscon"
    		+ " left join g_permessi g on g.syscon=u.syscon and g.idstipula = ?"
    		+ " where e.codein = ? "
    		+ " order by u.syscon";
    	datiUtenti = sqlManager.getListVector(selectUtenti, new Object[] {Long.valueOf(idStipula),cenint});
    }
    else {
       	selectUtenti = "select u.sysute, u.email, u.syscon, g.autori"
        	+ " from usrsys u"
        	+ " left join g_permessi g on g.syscon=u.syscon and g.idstipula = ?"
        	+ " order by u.syscon";
       	datiUtenti = sqlManager.getListVector(selectUtenti, new Object[] {Long.valueOf(idStipula)});
    }

     if (datiUtenti != null && datiUtenti.size() > 0) {
    	 total = datiUtenti.size();
         for (int a = 0; a < datiUtenti.size(); a++) {
             HashMap<String, Object> hMapCodiceUsr = new HashMap<String, Object>();
             String sysute = (String) SqlManager.getValueFromVectorParam(datiUtenti.get(a), 0).getValue();
             String email = (String) SqlManager.getValueFromVectorParam(datiUtenti.get(a), 1).getValue();
             Long syscon = (Long) SqlManager.getValueFromVectorParam(datiUtenti.get(a), 2).getValue();
             Long autori = (Long) SqlManager.getValueFromVectorParam(datiUtenti.get(a), 3).getValue();
             email = StringUtils.stripToEmpty(email);
             
             String desute = sysute;
             if(!"".equals(email)) {
            	 desute = sysute + " - " + email;
             }
             
             if(!usrCorrente.equals(syscon)) {
                 hMapCodiceUsr.put("codiceusr", syscon);
                 hMapCodiceUsr.put("descrizioneusr", desute);
                 hMapCodiceUsr.put("autori", autori);
                 hMap.add(hMapCodiceUsr);
             }
         }

         String msg = "msg provvisorio";
         result.put("esito",true);
         result.put("messaggio",msg);
         
         result.put("iTotalRecords", total);
         result.put("data", hMap);
    	 
     }


    out.print(result);
    out.flush();

    return null;

  }

}
