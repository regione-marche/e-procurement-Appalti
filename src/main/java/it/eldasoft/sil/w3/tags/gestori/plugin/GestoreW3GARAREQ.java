package it.eldasoft.sil.w3.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

public class GestoreW3GARAREQ extends AbstractGestorePreload {

  /** Logger */
  static Logger logger = Logger.getLogger(GestoreW3GARAREQ.class);

  /**
   *
   * @param tag
   */
  public GestoreW3GARAREQ(BodyTagSupportGene tag) {
    super(tag);
  }

  @Override
  public void doAfterFetch(PageContext arg0, String arg1) throws JspException {

  }

  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modoAperturaScheda)) {

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          page, SqlManager.class);

      String codice = UtilityTags.getParametro(page,
          UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);

      DataColumnContainer key = new DataColumnContainer(codice);
      Long numgara = new Long(
          (key.getColumnsBySuffix("NUMGARA", true))[0].getValue().getStringValue());
      Long numreq = new Long(
          (key.getColumnsBySuffix("NUMREQ", true))[0].getValue().getStringValue());

    }

  }

}
