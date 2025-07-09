package it.eldasoft.sil.pg.web.struts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.ActionAjaxLogged;

public class GetInternalWSUrlAction extends ActionAjaxLogged {
  
  static Logger  logger = Logger.getLogger(GetInternalWSUrlAction.class);

  @Override
  public final ActionForward runAction(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws IOException {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();
    
    String error2Log=null;
    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    String url = request.getParameter("url");
    String tns = request.getParameter("tns");
    boolean verify = false;
    
    if(StringUtils.isNotBlank(url)){
      if(url.indexOf("?")>-1)
        error2Log="la url del servizio non deve contenere '?'";
      if(url.indexOf("/services/")==-1)
        error2Log="la url del servizio deve contenere '/services/'";
      
      url+="?wsdl";
    }
    
    if(error2Log==null) {
      try {

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
      }catch(Exception e) {
        error2Log=e.getMessage();
      }
    }
    
    if(error2Log!=null)
      logger.error("Tentativo di contattare "+url+" con tns="+tns+" da parte dell'utente "+profiloUtente.getId()+"\n"+error2Log);

    out.print(verify);
    out.flush();

    return null;

  }
}
