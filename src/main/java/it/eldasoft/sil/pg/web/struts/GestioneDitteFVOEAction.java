package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.struts.DispatchActionAjaxLogged;
import it.eldasoft.sil.pg.bl.GestioneFVOEManager;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

public class GestioneDitteFVOEAction extends DispatchActionAjaxLogged {

  static Logger  logger = Logger.getLogger(GestioneDitteFVOEAction.class);
  
  private GestioneFVOEManager gestioneFVOEManager;

  public void setGestioneFVOEManager(GestioneFVOEManager gestioneFVOEManager) {
    this.gestioneFVOEManager = gestioneFVOEManager;
  }

  public final ActionForward getDitte(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws IOException {

    JSONObject result = new JSONObject();

    if (logger.isDebugEnabled()) logger.debug("GetDitte_GestioneFVOEAction: inizio metodo");

    PrintWriter out = response.getWriter();

    String codgar = request.getParameter("codgar");
    String soloNonInserite = request.getParameter("soloNonInserite");
    try {
      result = gestioneFVOEManager.recuperaDitte(codgar, "true".equals(soloNonInserite));
    }catch(SQLException e) {
      result.put("messaggio", "Si è verificato un errore nel recupero delle ditte da DITG.");
    }

    if (logger.isDebugEnabled()) logger.debug("GetDitte_GestioneFVOEAction: fine metodo");
  
    out.print(result);
    out.flush();

    return null;

  }

  public final ActionForward setDitte(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
                                      final HttpServletResponse response) throws IOException {

    JSONObject result = new JSONObject();

    if (logger.isDebugEnabled()) logger.debug("SetDitte_GestioneFVOEAction: inizio metodo");

    PrintWriter out = response.getWriter();

    String codgar = request.getParameter("codgar");
    String arrmultikey = request.getParameter("arrmultikey");
    try {
      result = gestioneFVOEManager.aggiungiDitte(codgar,arrmultikey);
    }catch(SQLException e) {
      result.put("messaggio", "Si è verificato un errore nel recupero delle ditte da DITG.");
    }

    if (logger.isDebugEnabled()) logger.debug("SetDitte_GestioneFVOEAction: fine metodo");

    out.print(result);
    out.flush();

    return null;

  }
}

