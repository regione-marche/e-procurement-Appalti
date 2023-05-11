package it.eldasoft.sil.pg.tags.gestori.plugin;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;

public class GestoreSearchArticoli extends AbstractGestorePreload {

  // Costanti
  private static final Logger LOGGER             = Logger.getLogger(GestoreSearchArticoli.class);

  private static final String ENTITA             = "MEARTCAT";

  private static final String FILTRO_TIPO_NAME   = "tipo";
  private static final String FILTRO_COD_NAME    = "cod";
  private static final String FILTRO_DESCR_NAME  = "descr";
  private static final String FILTRO_STATO_NAME  = "stato";
  private static final String FILTRO_COLORE_NAME = "colore";

  private static final String FILTRO_TIPO_WHERE = "meartcat.tipo = ?";
  private static final String FILTRO_COD_WHERE = "UPPER(meartcat.cod) LIKE ?";
  private static final String FILTRO_DESCR_WHERE = "UPPER(meartcat.descr) LIKE ?";
  private static final String FILTRO_STATO_WHERE = "meartcat.stato = ?";
  private static final String FILTRO_COLORE_WHERE = "UPPER(meartcat.colore) like ?";

  // Costruttori
  public GestoreSearchArticoli(BodyTagSupportGene tag) {
    super(tag);
  }

  // Metodi
  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda) throws JspException {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreSearchArticoli.doBeforeBodyProcessing: inizio metodo");

    final int popUpId = UtilityTags.getNumeroPopUp(page);
    final String filtroTipo = page.getRequest().getParameter(FILTRO_TIPO_NAME);
    final String filtroCod = page.getRequest().getParameter(FILTRO_COD_NAME);
    final String filtroDescr = page.getRequest().getParameter(FILTRO_DESCR_NAME);
    final String filtroStato = page.getRequest().getParameter(FILTRO_STATO_NAME);
    final String filtroColore = page.getRequest().getParameter(FILTRO_COLORE_NAME);

    String where="";
    String whereParams = "";

    // FILTRO TIPO
    if (StringUtils.isNotEmpty(filtroTipo)) {
      if (StringUtils.isNotEmpty(where)) where += " AND ";
      where += FILTRO_TIPO_WHERE;

      if (StringUtils.isNotEmpty(whereParams)) whereParams += ";";
      whereParams += String.format("N:%s", filtroTipo);
    }

    // FILTRO COD
    if (StringUtils.isNotEmpty(filtroCod)) {
      if (StringUtils.isNotEmpty(where)) where += " AND ";
      where += FILTRO_COD_WHERE;

      if (StringUtils.isNotEmpty(whereParams)) whereParams += ";";
      whereParams += String.format("T:%%%s%%", filtroCod.toUpperCase());
    }

    // FILTRO DESCRIZIONE
    if (StringUtils.isNotEmpty(filtroDescr)) {
      if (StringUtils.isNotEmpty(where)) where += " AND ";
      where += FILTRO_DESCR_WHERE;

      if (StringUtils.isNotEmpty(whereParams)) whereParams += ";";
      whereParams += String.format("T:%%%s%%", filtroDescr.toUpperCase());
    }

    // FILTRO STATO
    if (StringUtils.isNotEmpty(filtroStato)) {
      if (StringUtils.isNotEmpty(where)) where += " AND ";
      where += FILTRO_STATO_WHERE;

      if (StringUtils.isNotEmpty(whereParams)) whereParams += ";";
      whereParams += String.format("N:%s", filtroStato);
    }

    // FILTRO COLORE
    if (StringUtils.isNotEmpty(filtroColore)) {
      if (StringUtils.isNotEmpty(where)) where += " AND ";
      where += FILTRO_COLORE_WHERE;

      if (StringUtils.isNotEmpty(whereParams)) whereParams += ";";
      whereParams += String.format("T:%%%s%%", filtroColore.toUpperCase());
    }

    UtilityTags.createHashAttributeForSqlBuild(page.getSession(), ENTITA, popUpId);
    UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, whereParams);

    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreSearchArticoli.doBeforeBodyProcessing: fine metodo");
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda) throws JspException {

  }

}
