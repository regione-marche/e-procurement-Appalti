package it.eldasoft.sil.pg.tags.gestori.plugin;

import java.util.ArrayList;
import java.util.List;

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
  private static final String FILTRO_OGGETTO_NAME = "filtroOggetto";
  private static final String FILTRO_IMPORTO_MIN_NAME = "filtroImportoMin";
  private static final String FILTRO_IMPORTO_MAX_NAME = "filtroImportoMax";

  private static final String WHERE_STATO = "w3lott.stato_simog in (1,3,5)";
  private static final String WHERE_OGGETTO = "upper(w3lott.oggetto) like ?";
  private static final String WHERE_IMPORTO_MIN = "w3lott.importo_lotto >= ?";
  private static final String WHERE_IMPORTO_MAX = "w3lott.importo_lotto <= ?";

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
    final String filtroOggetto = page.getRequest().getParameter(FILTRO_OGGETTO_NAME);
    final String filtroImportoMin = page.getRequest().getParameter(FILTRO_IMPORTO_MIN_NAME);
    final String filtroImportoMax = page.getRequest().getParameter(FILTRO_IMPORTO_MAX_NAME);
    List<String> parameters= new ArrayList<String>();
    String where="";
    
    if("SIMOG".equals(filtroStatoSimog))
      where = concatCondition(where, WHERE_STATO);
    //ogggetto
    if(StringUtils.isNotEmpty(filtroOggetto)) {
      where = concatCondition(where, WHERE_OGGETTO);
      parameters.add("T:%"+filtroOggetto.toUpperCase()+"%");
    }
    if(StringUtils.isNotEmpty(filtroImportoMin)) {
      where = concatCondition(where, WHERE_IMPORTO_MIN);
      parameters.add("F:"+filtroImportoMin);
    }
    if(StringUtils.isNotEmpty(filtroImportoMax)) {
      where = concatCondition(where, WHERE_IMPORTO_MAX);
      parameters.add("F:"+filtroImportoMax);
    }

    UtilityTags.createHashAttributeForSqlBuild(page.getSession(), ENTITA, popUpId);
    UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, StringUtils.join(parameters,";"));

    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreW3GARELotti.DoBeforeBodyProcessing: fine metodo");
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda) throws JspException {

  }

  private String concatCondition(String where, String newContion) {
    if(StringUtils.isNotEmpty(where))
      where+=" AND ";
    return where+newContion;
  }

}
