package it.eldasoft.sil.pg.tags.gestori.plugin;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;

public class GestoreSearchProdottiPopUp extends AbstractGestorePreload {

  // Costanti
  private static final Logger LOGGER = Logger.getLogger(GestoreSearchProdottiPopUp.class);

  private static final String ENTITA = "V_CATAPROD";

  private static final String FILTRO_DESCR_NAME = "descr";
  private static final String FILTRO_COD_NAME = "cod";
  private static final String FILTRO_NOME_NAME = "nome";
  private static final String FILTRO_DESCAGG_NAME = "descagg";
  private static final String FILTRO_STATO_NAME = "stato";
  private static final String FILTRO_CATEGORIA_NAME = "categoria";

  private static final String FILTRO_DESCR_WERE = "UPPER(v_cataprod.descr) LIKE ?";
  private static final String FILTRO_COD_WHERE = "UPPER(v_cataprod.cod) LIKE ?";
  private static final String FILTRO_NOME_WHERE = "UPPER(v_cataprod.nome) LIKE ?";
  private static final String FILTRO_DESCAGG_WHERE = "UPPER(v_cataprod.descagg) LIKE ?";
  private static final String FILTRO_STATO_WHERE = "v_cataprod.stato = ?";
  private static final String FILTRO_CATEGORIA_WHERE = "(UPPER(v_cataprod.caisim) LIKE ? OR UPPER(v_cataprod.descat) LIKE ?)";

  // Costruttori
  public GestoreSearchProdottiPopUp(BodyTagSupportGene tag) {
    super(tag);
  }

  // Metodi
  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda) throws JspException {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreSearchProdottiPopUp.doBeforeBodyProcessing: inizio metodo");

    final int popUpId = UtilityTags.getNumeroPopUp(page);
    final String filtroDescr = page.getRequest().getParameter(FILTRO_DESCR_NAME);
    final String filtroCod = page.getRequest().getParameter(FILTRO_COD_NAME);
    final String filtroNome = page.getRequest().getParameter(FILTRO_NOME_NAME);
    final String filtroDescagg = page.getRequest().getParameter(FILTRO_DESCAGG_NAME);
    final String filtroStato = page.getRequest().getParameter(FILTRO_STATO_NAME);
    final String filtroCategoria = page.getRequest().getParameter(FILTRO_CATEGORIA_NAME);

    String where ="";
    String whereParams ="";

    // FILTRO DESCR
    if (StringUtils.isNotEmpty(filtroDescr)) {
      if (StringUtils.isNotEmpty(where)) where += " AND ";
      where += FILTRO_DESCR_WERE;

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

    // FILTRO NOME
    if (StringUtils.isNotEmpty(filtroNome)) {
      if (StringUtils.isNotEmpty(where)) where += " AND ";
      where += FILTRO_NOME_WHERE;

      if (StringUtils.isNotEmpty(whereParams)) whereParams += ";";
      whereParams += String.format("T:%%%s%%", filtroNome.toUpperCase());
    }

    // FILTRO DESCAGG
    if (StringUtils.isNotEmpty(filtroDescagg)) {
      if (StringUtils.isNotEmpty(where)) where += " AND ";
      where += FILTRO_DESCAGG_WHERE;

      if (StringUtils.isNotEmpty(whereParams)) whereParams += ";";
      whereParams += String.format("T:%%%s%%", filtroDescagg.toUpperCase());
    }

    // FILTRO STATO
    if (StringUtils.isNotEmpty(filtroStato)) {
      if (StringUtils.isNotEmpty(where)) where += " AND ";
      where += FILTRO_STATO_WHERE;

      if (StringUtils.isNotEmpty(whereParams)) whereParams += ";";
      whereParams += String.format("N:%s", filtroStato);
    }

    // FILTRO CATEGORIA
    if (StringUtils.isNotEmpty(filtroCategoria)) {
      if (StringUtils.isNotEmpty(where)) where += " AND ";
      where += FILTRO_CATEGORIA_WHERE;

      if (StringUtils.isNotEmpty(whereParams)) whereParams += ";";
      whereParams += String.format("T:%%%s%%;T:%%%s%%", filtroCategoria.toUpperCase(), filtroCategoria.toUpperCase());
    }

    UtilityTags.createHashAttributeForSqlBuild(page.getSession(), ENTITA, popUpId);
    UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, whereParams);

    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreSearchProdottiPopUp.doBeforeBodyProcessing: fine metodo");
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda) throws JspException {

  }

}
