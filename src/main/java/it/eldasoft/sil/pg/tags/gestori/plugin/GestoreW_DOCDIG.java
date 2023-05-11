package it.eldasoft.sil.pg.tags.gestori.plugin;

import java.sql.SQLException;


import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

public class GestoreW_DOCDIG extends AbstractGestorePreload {

  public GestoreW_DOCDIG(BodyTagSupportGene tag) {
    super(tag);
  }

  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {

  }

  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);

    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modoAperturaScheda)) {

      String selectW_DOCDIG = "select count(*) from W_DOCDIG where IDPRG = ? and IDDOCDIG = ? and DIGOGG is not null";
      
      try {
        
        String codice = (String) UtilityTags.getParametro(page,
            UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
        DataColumnContainer key = new DataColumnContainer(codice);
        String idprg = (key.getColumnsBySuffix("IDPRG", true))[0].getValue().toString();
        Long iddocdig = (key.getColumnsBySuffix("IDDOCDIG", true))[0].getValue().longValue();

        
        Long conteggio = (Long) sqlManager.getObject(selectW_DOCDIG,
            new Object[] { idprg, iddocdig });
        
        if (conteggio == null
            || (conteggio != null && new Long(0).equals(conteggio))) {
          page.setAttribute("esisteFile", "false", PageContext.REQUEST_SCOPE);
        } else {
          page.setAttribute("esisteFile", "true", PageContext.REQUEST_SCOPE);
        }

      } catch (SQLException e) {
        throw new JspException(
            "Errore nell'esecuzione della query per l'estrazione dei dati del documento",
            e);
      } catch (GestoreException e) {
        throw new JspException(
            "Errore nell'esecuzione della query per l'estrazione dei dati del documento",
            e);
      }
    }
  }
}
