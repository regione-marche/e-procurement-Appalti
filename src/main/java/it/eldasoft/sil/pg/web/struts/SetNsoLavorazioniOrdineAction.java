package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.NsoOrdiniManager;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class SetNsoLavorazioniOrdineAction extends Action {

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

    JSONObject result = new JSONObject();

    String idOrdineStr = request.getParameter("idOrdine");
    Long idOrdine = new Long(idOrdineStr);
    String numeroGara = request.getParameter("numeroGara");
    String codiceDitta = request.getParameter("codiceDitta");
    String uffint = request.getParameter("uffint");
    String arrmultikey = request.getParameter("arrmultikey");

    if(!"".equals(arrmultikey)){
      this.nsoOrdiniManager.variazioneLineeOrdine(request, arrmultikey, idOrdine, numeroGara, codiceDitta, uffint);
    }

    out.print(result);
    out.flush();

    return null;

  }
}

