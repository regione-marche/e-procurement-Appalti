package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.NsoOrdiniManager;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetNsoPresenzaLavFornAction extends Action {


  private NsoOrdiniManager nsoOrdiniManager;

  public void setNsoOrdiniManager(NsoOrdiniManager nsoOrdiniManager) {
    this.nsoOrdiniManager = nsoOrdiniManager;
  }


  @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String codiceGara = request.getParameter("codiceGara");
    String numeroGara = request.getParameter("numeroGara");
    String codiceDitta = request.getParameter("codiceDitta");

    boolean verify = nsoOrdiniManager.getPresenzaLavForn(codiceGara, numeroGara, codiceDitta);

    out.print(verify);
    out.flush();

    return null;

  }
}

