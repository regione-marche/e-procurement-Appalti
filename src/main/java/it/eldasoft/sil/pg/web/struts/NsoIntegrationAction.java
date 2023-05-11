package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.struts.DispatchActionAjaxLogged;
import it.eldasoft.sil.pg.bl.NsoIntegrationManager;


public class NsoIntegrationAction extends DispatchActionAjaxLogged {

  Logger logger = Logger.getLogger(NsoIntegrationAction.class);
  
  NsoIntegrationManager nsoIntegrationManager;
  
  public void setNsoIntegrationManager(NsoIntegrationManager nsoIntegrationManager) {
    this.nsoIntegrationManager = nsoIntegrationManager;
  }

  /**
   * This method will allow to call the already developed action
   */
  @Override
  protected ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    return this.defaultAction(mapping, form, request, response);
  }

  /**
   * The default action of this Action
   */
  private ActionForward defaultAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if(!"POST".equalsIgnoreCase(request.getMethod())) {
      response.sendError(HttpStatus.SC_FORBIDDEN);
      return null;
    }
    /*
     * 1. define the actions that need to be done
     *  1.1 creation of XML -> specific method which will return an inptutstream it will accept an "NSO Order"
     *  1.1 validation request -> specific method (to be called by itself)
     *  1.2 order request -> specific method
     *  1.3 specific method that will read from DB an Order and then generate an "NSO Order"
     */
    response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
    response.setCharacterEncoding(ContentType.APPLICATION_JSON.getCharset().name());
    PrintWriter out = response.getWriter();
    logger.info("orderId["+request.getParameter("orderId")+"]");
    out.print(this.processOrder(request.getParameter("orderId")));
    out.flush();
    return null;
  }
  
  public ActionForward validateAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
//    if(!"POST".equalsIgnoreCase(request.getMethod())) {
//      response.sendError(HttpStatus.SC_FORBIDDEN);
//      return null;
//    }
    logger.info("orderId["+request.getParameter("orderId")+"]");
    /*
     * 1. define the actions that need to be done
     *  1.1 creation of XML -> specific method which will return an inptutstream it will accept an "NSO Order"
     *  1.1 validation request -> specific method (to be called by itself)
     *  1.2 order request -> specific method
     *  1.3 specific method that will read from DB an Order and then generate an "NSO Order"
     */
    response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
    response.setCharacterEncoding(ContentType.APPLICATION_JSON.getCharset().name());
    PrintWriter out = response.getWriter();
    out.print(this.validateOrder(request.getParameter("orderId")));
    out.flush();
    return null;
  }
  
  public ActionForward download(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

    response.setContentType("application/octet-stream");
    response.setCharacterEncoding("UTF-8");
    String fileName = request.getParameter("fileName");
    byte[] content = null;
    OutputStream out = null;
    Writer writer = null;
    try {
      content = nsoIntegrationManager.getFileXmlFromNsoWsOrdiniByFileName(fileName);
      if (content != null && content.length > 0) {
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setContentLength(content.length);
        out = response.getOutputStream();
        writer = new OutputStreamWriter(out, "UTF-8");
        writer.write(new String(content));
      } else {
        response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Impossibile aprire il file richiesto.");
      }
    } catch (Exception e) {
      logger.error("Impossible stream file xml "+fileName,e);
      try {
        response.setContentType("text/html");
        response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Impossibile aprire il file richiesto.");
      } catch (IOException e1) {
        logger.warn("Impossibile create writer for exception.");
      }
    } finally {
      if(writer != null) {
        try {
          writer.flush();
          writer.close();
        } catch (IOException e) {
          logger.warn("Impossibile close / flush writer.");
        }
      }
    }

    return null;
  }
  
  public ActionForward revokeOrder(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
    String orderId = request.getParameter("orderId");
    logger.info("Revocation of orderId["+orderId+"]");
    
    try {
      Thread.sleep(2000l);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    //TODO update order + send annullamento
    response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
    response.setCharacterEncoding(ContentType.APPLICATION_JSON.getCharset().name());
    logger.info("Revocation of orderId["+orderId+"]");
    PrintWriter out = null;
    try {
    out = response.getWriter();
    out.print(nsoIntegrationManager.revokeOrder(orderId));
    out.flush();
    }catch (Exception e) {
      
    } finally {
      if(out!=null) out.close();
    }
    return null;
  }
  
  /**
   * This method will call the validateOrder of ClientNso
   * @param orderId
   * @return
   */
  public String validateOrder(String orderId) {
    return nsoIntegrationManager.getValidatedOrder(orderId);
  }
  public String processOrder(String orderId) {
    return nsoIntegrationManager.getProcessOrder(orderId);
  }

}
