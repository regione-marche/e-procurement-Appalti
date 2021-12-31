package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetIPTSchedaInterventoAction extends Action {

  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String endpoint = request.getParameter("endpoint");
    String cui = request.getParameter("cui");
    String url = endpoint + "/DettaglioInterventoAcquisto?codiceCUI=" + cui;

    HttpClient client = new DefaultHttpClient();
    HttpGet get = new HttpGet(url);
    HttpResponse res = client.execute(get);
    BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));

    String message = org.apache.commons.io.IOUtils.toString(rd);
    out.print(message);
    out.flush();

    return null;

  }

}
