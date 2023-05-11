package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetURLAction extends Action {

  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String url = request.getParameter("url");
    String tns = request.getParameter("tns");
    boolean verify = false;

    URL myurl = new URL(url);
    
    if (url.startsWith("https")) {
      HttpsURLConnection conn = (HttpsURLConnection) myurl.openConnection();
      conn.setRequestMethod("GET");
      if (conn.getResponseCode() == 200) {
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String message = org.apache.commons.io.IOUtils.toString(br);
        verify = message.contains(tns);
      }
    } else {
      HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
      conn.setRequestMethod("GET");
      if (conn.getResponseCode() == 200) {
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String message = org.apache.commons.io.IOUtils.toString(br);
        verify = message.contains(tns);
      }
    }

    out.print(verify);
    out.flush();

    return null;

  }
}
