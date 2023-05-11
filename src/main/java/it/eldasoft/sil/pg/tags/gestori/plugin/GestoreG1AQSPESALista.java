package it.eldasoft.sil.pg.tags.gestori.plugin;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;

public class GestoreG1AQSPESALista extends AbstractGestorePreload {

  // Costanti
  private static final Logger LOGGER = Logger.getLogger(GestoreG1AQSPESALista.class);

  private static final String ENTITA = "G1AQSPESA";

  private static final String CENINT_NAME = "cenint";
  private static final String NGARA_NAME = "ngara";
  private static final String NCONT_NAME = "ncont";

  private static final String WHERE = "G1AQSPESA.NGARA = ? and G1AQSPESA.NCONT = ? and G1AQSPESA.CENINT = ?";

  // Costruttori
  public GestoreG1AQSPESALista(BodyTagSupportGene tag) {
    super(tag);
  }

  // Metodi
  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda) throws JspException {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreG1AQSPESALista.doBeforeBodyProcessing: inizio metodo");

    final int popUpId = UtilityTags.getNumeroPopUp(page);
    final String cenint = page.getRequest().getParameter(CENINT_NAME);
    final String ngara = page.getRequest().getParameter(NGARA_NAME);
    final String ncont = page.getRequest().getParameter(NCONT_NAME);


    UtilityTags.createHashAttributeForSqlBuild(page.getSession(), ENTITA, popUpId);

    String where = UtilityTags.getAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA);
    if (StringUtils.isNotEmpty(cenint) && StringUtils.isNotEmpty(ngara) && StringUtils.isNotEmpty(ncont)) {

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
      whereParams = String.format("T:%s;N:%s;T:%s", ngara, ncont, cenint);

      UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, whereParams);
    }

    UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);

    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreG1AQSPESALista.doBeforeBodyProcessing: fine metodo");
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda) throws JspException {

  }

}
