package it.eldasoft.sil.pg.tags.gestori.plugin;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;

public class GestoreW3GARELotti extends AbstractGestorePreload {

  // Costanti
  private static final Logger LOGGER = Logger.getLogger(GestoreW3GARELotti.class);
  
  private static final String ENTITA = "W3LOTT";
  
  private static final String FILTRO_STAT_SIMOG_NAME = "filtroStatoSimog";
  
  private static final String WHERE = "w3lott.stato_simog in (1,3,5)";
  
  // Costruttori
  public GestoreW3GARELotti(BodyTagSupportGene tag) {
    super(tag);
  }

  // Metodi
  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda) throws JspException {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreW3GARELotti.DoBeforeBodyProcessing: inizio metodo");

    final int popUpId = UtilityTags.getNumeroPopUp(page);
    final String filtroStatoSimog = page.getRequest().getParameter(FILTRO_STAT_SIMOG_NAME);
    
    String where = UtilityTags.getAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA);
    if (StringUtils.isNotEmpty(where)) {
      where += " AND ";
    } else {
      where = "";
    }
    
    if (StringUtils.isNotEmpty(filtroStatoSimog) && filtroStatoSimog.equals("SIMOG")) {
      where += WHERE;
    }
    if (StringUtils.isNotEmpty(filtroStatoSimog) && filtroStatoSimog.equals("TUTTI")) {
      where = null;
    }
    
    UtilityTags.createHashAttributeForSqlBuild(page.getSession(), ENTITA, popUpId);
    UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    
    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreW3GARELotti.DoBeforeBodyProcessing: fine metodo");
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda) throws JspException {
    
  }

}
