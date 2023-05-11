package it.eldasoft.sil.pg.tags.gestori.plugin;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;

public class GestoreGFOFLista extends AbstractGestorePreload {

  // Costanti
  private static final Logger LOGGER = Logger.getLogger(GestoreGFOFLista.class);

  private static final String ENTITA = "GFOF";

  private static final String WHERE = "GFOF.NGARA2=? and GFOF.ESPGIU='1'";

  // Costruttori
  public GestoreGFOFLista(BodyTagSupportGene tag) {
    super(tag);
  }

  // Metodi
  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda) throws JspException {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreGFOFLista.doBeforeBodyProcessing: inizio metodo");

    final int popUpId = UtilityTags.getNumeroPopUp(page);
    final String lotto  = page.getRequest().getParameter("lotto");
    final String codgar  = page.getRequest().getParameter("codgar");

    UtilityTags.createHashAttributeForSqlBuild(page.getSession(), ENTITA, popUpId);

    String where = UtilityTags.getAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA);
    if (StringUtils.isNotEmpty(lotto)) {

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
      whereParams = String.format("T:%s", lotto);

      UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, whereParams);

    }

    UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);

    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreGFOFLista.doBeforeBodyProcessing: fine metodo");
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda) throws JspException {

  }

}
