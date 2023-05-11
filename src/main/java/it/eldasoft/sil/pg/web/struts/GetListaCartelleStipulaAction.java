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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetListaCartelleStipulaAction extends Action {

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

    String folder = request.getParameter("folder");

    String selezioneCartelle = "select distinct(cartella) from g1stipula where cartella like ? ";
    
    List<?> datiCartelle = null;
    datiCartelle = sqlManager.getListVector(selezioneCartelle, new Object[] {folder + "%"});
    if (datiCartelle.size() > 20) {
    	datiCartelle = datiCartelle.subList(0, 20);
    }
    JSONArray jsonArrayCartelle = null;
    if (datiCartelle != null && datiCartelle.size() > 0) {
    	jsonArrayCartelle= JSONArray.fromObject(datiCartelle.toArray());
    } else {
    	jsonArrayCartelle = new JSONArray();
    }

    out.println(jsonArrayCartelle);
    out.flush();

    return null;

  }

}
