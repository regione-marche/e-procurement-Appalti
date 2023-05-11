package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionAjaxLogged;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneProgrammazioneManager;
import net.sf.json.JSONObject;

public class GetRdaRdiAction extends ActionAjaxLogged {

  static Logger  logger = Logger.getLogger(GetRdaRdiAction.class);
  
  private GestioneProgrammazioneManager gestioneProgrammazioneManager;

  public void setGestioneProgrammazioneManager(GestioneProgrammazioneManager gestioneProgrammazioneManager) {
    this.gestioneProgrammazioneManager = gestioneProgrammazioneManager;
  }

  @Override
  public final ActionForward runAction(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws IOException {

    JSONObject result = new JSONObject();

    if (logger.isDebugEnabled()) logger.debug("GetRdaRdiAction: inizio metodo");

    PrintWriter out = response.getWriter();
    HttpSession session = request.getSession();
    ProfiloUtente profilo = (ProfiloUtente) session.getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long syscon = new Long(profilo.getId());
    String uffintGara = (String)  session.getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
    
    String type = request.getParameter("type");
    if("false".equals(type))
      type=GestioneProgrammazioneManager.PARAMS_CODAPP_NC;
    String codrda = request.getParameter("codrda");
    String codgar = request.getParameter("codgar");
    String ngara = request.getParameter("ngara");
    String rup = request.getParameter("rup");
    String amministrazioneDesc = request.getParameter("amministrazioneDesc");
    String articoloDesc = request.getParameter("articoloDesc");
    
    try {
      if(StringUtils.isNotEmpty(codgar)) {
        result = gestioneProgrammazioneManager.recuperaRdaCollegate(codgar,ngara,true,"true".equals(type),false);
      }else {
        result = gestioneProgrammazioneManager.consultaRda(syscon, uffintGara, type, codrda, rup, amministrazioneDesc, articoloDesc);
      }
    }catch(GestoreException e) {
      result.put("messaggio", "Si è verificato un errore.");
    }

    if (logger.isDebugEnabled()) logger.debug("GetRdaRdiAction: fine metodo");
  
    out.print(result);
    out.flush();

    return null;

  }
}

