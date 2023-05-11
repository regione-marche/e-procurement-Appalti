package it.eldasoft.sil.pg.tags.gestori.plugin;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;

public class GestoreRaggruppamentoImprese extends AbstractGestorePreload {

  // Costanti
  private static final Logger LOGGER = Logger.getLogger(GestoreRaggruppamentoImprese.class);
  
  private static final String ENTITA = "RAGIMP";
  
  private static final String CODIMP_NAME = "codimp";
  private static final String NGARA_NAME = "ngara";
  
  private static final String WHERE = "ragimp.codime9 = ? AND ragimp.coddic NOT IN (SELECT coddic FROM ragdet WHERE ragdet.ngara = ? AND ragdet.codimp = ?)";
  
  // Costruttori
  public GestoreRaggruppamentoImprese(BodyTagSupportGene tag) {
    super(tag);
  }

  // Metodi
  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda) throws JspException {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreRaggruppamentoImprese.doBeforeBodyProcessing: inizio metodo");

    final int popUpId = UtilityTags.getNumeroPopUp(page);
    final String codimp = page.getRequest().getParameter(CODIMP_NAME);
    final String ngara = page.getRequest().getParameter(NGARA_NAME);
    
    UtilityTags.createHashAttributeForSqlBuild(page.getSession(), ENTITA, popUpId);

    String where = UtilityTags.getAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA);
    if (StringUtils.isNotEmpty(codimp) && StringUtils.isNotEmpty(ngara)) {
      if (StringUtils.isNotEmpty(where)) {
        where += " AND " + WHERE;
      } else {
        where = WHERE;
      }
    
      String whereParams = UtilityTags.getAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA);
      if (StringUtils.isNotEmpty(whereParams)) {
        whereParams += ";";
      } else {
        whereParams = "";
      }
      whereParams = String.format("T:%s;T:%s;T:%s", codimp, ngara, codimp);
      
      UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, whereParams);
    }
    
    UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    
    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreRaggruppamentoImprese.doBeforeBodyProcessing: inizio metodo");
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda) throws JspException {
    
  }

}
