package it.eldasoft.sil.pg.tags.gestori.plugin;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

public class GestoreV_GARE_ADESIONILista extends AbstractGestorePreload {

  // Costanti
  private static final Logger LOGGER = Logger.getLogger(GestoreV_GARE_ADESIONILista.class);

  private static final String ENTITA = "V_GARE_ADESIONI";

  private static final String DITTA_NAME = "ditta";
  private static final String CENINT_NAME = "cenint";
  private static final String NGARAAQ_NAME = "ngaraaq";


  private static final String WHERE_BASE = "V_GARE_ADESIONI.NGARAAQ = ? and V_GARE_ADESIONI.CENINT = ? ";
  private static final String WHERE_BASE_CODCIG = "(V_GARE_ADESIONI.NGARAAQ = ? or V_GARE_ADESIONI.CODCIGAQ = ?) and V_GARE_ADESIONI.CENINT = ?";
  private static final String WHERE_BASE_LOTTI = "V_GARE_ADESIONI.NGARAAQ in (#) and V_GARE_ADESIONI.CENINT = ? ";
  private static final String WHERE_BASE_CODCIG_LOTTI = "(V_GARE_ADESIONI.NGARAAQ in (#) or V_GARE_ADESIONI.CODCIGAQ in (%)) and V_GARE_ADESIONI.CENINT = ? ";


  // Costruttori
  public GestoreV_GARE_ADESIONILista(BodyTagSupportGene tag) {
    super(tag);
  }

  // Metodi
  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda) throws JspException {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreV_GARE_ADESIONILista.doBeforeBodyProcessing: inizio metodo");

    final int popUpId = UtilityTags.getNumeroPopUp(page);
    final String cenint = page.getRequest().getParameter(CENINT_NAME);
    final String ngaraaq = page.getRequest().getParameter(NGARAAQ_NAME);
    final String ditta = page.getRequest().getParameter(DITTA_NAME);


    UtilityTags.createHashAttributeForSqlBuild(page.getSession(), ENTITA, popUpId);

    String where = UtilityTags.getAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA);
    if (StringUtils.isNotEmpty(cenint) && StringUtils.isNotEmpty(ngaraaq) ) {



      String whereParams = UtilityTags.getAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA);
      if (StringUtils.isNotEmpty(whereParams)) {
        whereParams += ";";
      } else {
        whereParams = "";
      }

      String whereComposta="";
      if(ditta == null || "".equals(ditta)) {
        SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
            page, SqlManager.class);

        String codcig;
        try {
          codcig = (String)sqlManager.getObject("select codcig from gare where ngara=?", new Object[] {ngaraaq});
        } catch (SQLException e) {
          throw new JspException(
              "Errore nella lettura del codice cig della gara " + ngaraaq, e);
        }
        if(codcig !=null && !"".equals(codcig) && !codcig.startsWith("#")) {
          whereComposta = WHERE_BASE_CODCIG;
          whereParams = String.format("T:%s;T:%s;T:%s", ngaraaq, codcig, cenint);
        }else {
          whereComposta = WHERE_BASE;
          whereParams = String.format("T:%s;T:%s", ngaraaq, cenint);
        }

      }else {
        PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
            page, PgManagerEst1.class);
        try {
          String  elencoLotti =pgManagerEst1 .getElencoLottiAggiudicati(ngaraaq, ditta);
          String elencoCodcigLotti=pgManagerEst1.getElencoCigLotti(ngaraaq, ditta);
          if(elencoCodcigLotti!=null) {
            whereComposta = WHERE_BASE_CODCIG_LOTTI;
            whereComposta= whereComposta.replace("#", elencoLotti);
            whereComposta= whereComposta.replace("%", elencoCodcigLotti);
          }else {
            whereComposta = WHERE_BASE_LOTTI;
            whereComposta= whereComposta.replace("#", elencoLotti);
          }
          whereParams = String.format("T:%s",  cenint);
        } catch (SQLException e) {
          throw new JspException(
              "Errore nella lettura dei lotti aggiudicati della gara " + ngaraaq, e);
        }

      }

      if (StringUtils.isNotEmpty(where)) {
        where += " AND " + whereComposta;
      } else {
        where = whereComposta;
      }


      UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, whereParams);
    }

    UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);

    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreV_GARE_ADESIONILista.doBeforeBodyProcessing: fine metodo");
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda) throws JspException {

  }

}
