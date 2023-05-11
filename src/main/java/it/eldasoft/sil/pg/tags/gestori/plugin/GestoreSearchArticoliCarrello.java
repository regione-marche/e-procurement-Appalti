package it.eldasoft.sil.pg.tags.gestori.plugin;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;

public class GestoreSearchArticoliCarrello extends AbstractGestorePreload {

  // Costanti
  private static final Logger LOGGER = Logger.getLogger(GestoreSearchArticoliCarrello.class);

  private static final String ENTITA = "MERICART";

  private static final String FILTRO_TIPO_NAME = "tipo";
  private static final String FILTRO_DESCR_NAME = "descr";
  private static final String FILTRO_COD_NAME = "cod";
  private static final String FILTRO_COLORE_NAME = "colore";
  private static final String FILTRO_UNIMISACQ_NAME = "unimisacq";

  private static final String FILTRO_TIPO_WHERE = "meartcat.tipo = ?";
  private static final String FILTRO_DESCR_WHERE = "UPPER(meartcat.descr) LIKE ?";
  private static final String FILTRO_COD_WHERE = "UPPER(meartcat.cod) LIKE ?";
  private static final String FILTRO_COLORE_WHERE = "UPPER(meartcat.colore) LIKE ?";
  private static final String FILTRO_UNIMISACQ_WHERE = "meartcat.unimisacq = ?";

  // Costruttori
  public GestoreSearchArticoliCarrello(BodyTagSupportGene tag) {
    super(tag);
  }

  // Metodi
  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda) throws JspException {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreSearchArticoliCarrello.doBeforeBodyProcessing: inizio metodo");

    final int popUpId = UtilityTags.getNumeroPopUp(page);
    final String filtroTipo = page.getRequest().getParameter(FILTRO_TIPO_NAME);
    final String filtroDescr = page.getRequest().getParameter(FILTRO_DESCR_NAME);
    final String filtroCod = page.getRequest().getParameter(FILTRO_COD_NAME);
    final String filtroColore = page.getRequest().getParameter(FILTRO_COLORE_NAME);
    final String filtroUnimisacq = page.getRequest().getParameter(FILTRO_UNIMISACQ_NAME);

    String where = UtilityTags.getAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA);
    if (StringUtils.isEmpty(where)) where = "";

    String whereParams = UtilityTags.getAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA);
    if (StringUtils.isEmpty(whereParams)) whereParams = "";

    // FILTRO TIPO
    if (StringUtils.isNotEmpty(filtroTipo)) {
      if (StringUtils.isNotEmpty(where)) where += " AND ";
      where += FILTRO_TIPO_WHERE;

      if (StringUtils.isNotEmpty(whereParams)) whereParams += ";";
      whereParams += String.format("N:%s", filtroTipo);
    }

    // FILTRO DESCR
    if (StringUtils.isNotEmpty(filtroDescr)) {
      if (StringUtils.isNotEmpty(where)) where += " AND ";
      where += FILTRO_DESCR_WHERE;

      if (StringUtils.isNotEmpty(whereParams)) whereParams += ";";
      whereParams += String.format("T:%%%s%%", filtroDescr.toUpperCase());
    }

    // FILTRO COD
    if (StringUtils.isNotEmpty(filtroCod)) {
      if (StringUtils.isNotEmpty(where)) where += " AND ";
      where += FILTRO_COD_WHERE;

      if (StringUtils.isNotEmpty(whereParams)) whereParams += ";";
      whereParams += String.format("T:%%%s%%", filtroCod.toUpperCase());
    }

    // FILTRO COLORE
    if (StringUtils.isNotEmpty(filtroColore)) {
      if (StringUtils.isNotEmpty(where)) where += " AND ";
      where += FILTRO_COLORE_WHERE;

      if (StringUtils.isNotEmpty(whereParams)) whereParams += ";";
      whereParams += String.format("T:%%%s%%", filtroColore.toUpperCase());
    }

    // FILTRO UNIMISACQ
    if (StringUtils.isNotEmpty(filtroUnimisacq)) {
      if (StringUtils.isNotEmpty(where)) where += " AND ";
      where += FILTRO_UNIMISACQ_WHERE;

      if (StringUtils.isNotEmpty(whereParams)) whereParams += ";";
      whereParams += String.format("N:%s", filtroUnimisacq);
    }

    UtilityTags.createHashAttributeForSqlBuild(page.getSession(), ENTITA, popUpId);
    UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, whereParams);

    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreSearchArticoliCarrello.doBeforeBodyProcessing: fine metodo");
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda) throws JspException {

  }

}
