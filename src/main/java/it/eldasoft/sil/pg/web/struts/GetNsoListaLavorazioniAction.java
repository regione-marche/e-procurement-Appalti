package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.NsoOrdiniManager;

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

public class GetNsoListaLavorazioniAction extends Action {

  private NsoOrdiniManager nsoOrdiniManager;

  public void setNsoOrdiniManager(NsoOrdiniManager nsoOrdiniManager) {
    this.nsoOrdiniManager = nsoOrdiniManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();


    String codiceGara = request.getParameter("codiceGara");
    String numeroGara = request.getParameter("numeroGara");
    String codiceDitta = request.getParameter("codiceDitta");
    String incluseConsumate = request.getParameter("incl_cons");


    JSONObject result = new JSONObject();
    int totalLavNso = 0;
    int totalAfterFilterLavNso = 0;


    List<HashMap<String, Object>> hMapLavNso = new ArrayList<HashMap<String, Object>>();
    hMapLavNso = this.nsoOrdiniManager.getListaLavorazioniNso(codiceGara, numeroGara, codiceDitta,incluseConsumate);


    if (hMapLavNso != null && hMapLavNso.size() > 0) {
      totalLavNso = hMapLavNso.size();
      totalAfterFilterLavNso = hMapLavNso.size();
    }


    result.put("iTotalRecords", totalLavNso);
    result.put("iTotalDisplayRecords", totalAfterFilterLavNso);
    result.put("data", hMapLavNso);

    out.print(result);
    out.flush();

    return null;

  }
}

