package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.maggioli.eldasoft.ws.dm.WSDMAmministrazioneAooType;
import it.maggioli.eldasoft.ws.dm.WSDMListaAmministrazioniAooResType;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetPermessiUtenteStipulaAction extends Action {

    
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
    Boolean esito= false;
    
    String idStipula = request.getParameter("idStipula");
    String cm = request.getParameter("cm");
    String codStipula = null;
    String cenint = null;
    String selectV_GARE_STIPULA = "select codstipula,codgar,cenint from v_gare_stipula where id = ? ";
    
    List<?> datiGareStipula = this.sqlManager.getVector(selectV_GARE_STIPULA, new Object[] {Long.valueOf(idStipula)});
    if (datiGareStipula != null && datiGareStipula.size() > 0) {
    	codStipula = (String) SqlManager.getValueFromVectorParam(datiGareStipula, 0).getValue();
    	cenint = (String) SqlManager.getValueFromVectorParam(datiGareStipula, 2).getValue();
    }
    
    String selectPermessiUtente = "select g.autori"
    		+ " from usrsys u"
    		+ " join usr_ein e on e.syscon=u.syscon"
    		+ " left join g_permessi g on g.syscon=u.syscon and g.idstipula = ?"
    		+ " where u.syscon = ? and e.codein = ? ";

    Long autori = (Long) sqlManager.getObject(selectPermessiUtente, new Object[] {Long.valueOf(idStipula),cm,cenint});
    
    if(Long.valueOf(1).equals(autori)) {
    	esito=true;
    }else {
    	esito=false;
    }
     
    result.put("esito",esito);


    out.print(result);
    out.flush();

    return null;

  }

}
