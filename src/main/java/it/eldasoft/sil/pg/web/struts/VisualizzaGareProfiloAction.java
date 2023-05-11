package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.tags.utils.UtilityTags;

public class VisualizzaGareProfiloAction extends ActionBaseNoOpzioni {

  private static final Logger LOGGER = Logger.getLogger(VisualizzaGareProfiloAction.class);
  
  private static final String CODICE_NAME = "codice";
  private static final String GENERE_NAME = "genere";
  
  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("VisualizzaGareProfilo.execute: inizio metodo");
    
    final String codice = request.getParameter(CODICE_NAME);
    final String genere = request.getParameter(GENERE_NAME);
    
    String where = "";
    final String params = "T:" + codice;
    String entita = "";
    
    if (genere.equals("11")) {
      where = "GAREAVVISI.NGARA = ?";
      entita = "GAREAVVISI";
    } else {
      where = "V_GARE_TORN.CODICE = ?";
      entita = "V_GARE_TORN";
    }
    
    
    UtilityTags.createHashAttributeForSqlBuild(request.getSession(), entita, 0);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, params);

    UtilityTags.saveHashAttributeForSqlBuild(request, entita, 0);
    
    if (LOGGER.isDebugEnabled()) LOGGER.debug("VisualizzaGareProfilo.execute: inizio metodo");

    return mapping.findForward("success");
  }
  
}
