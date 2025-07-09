package it.eldasoft.sil.w3.tags.gestori.plugin;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;

public class GestoreW3LOTTMASSIVO extends AbstractGestorePreload {

	/** Logger */
	static Logger logger = Logger.getLogger(GestoreW3LOTTMASSIVO.class);
  /**
   * 
   * @param tag
   */
  public GestoreW3LOTTMASSIVO(BodyTagSupportGene tag) {
    super(tag);
  }

  @Override
  public void doAfterFetch(PageContext arg0, String arg1) throws JspException {

  }

  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", page, SqlManager.class);

    String codice = UtilityTags.getParametro(page, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT);
    Long numgara = null;
    DataColumnContainer key = new DataColumnContainer(codice);
    try {
    	numgara = new Long((key.getColumnsBySuffix("NUMGARA", true))[0].getValue().getStringValue());
    } catch (Exception e) {
    	codice = UtilityTags.getParametro(page, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
    	key = new DataColumnContainer(codice);
    	numgara = new Long((key.getColumnsBySuffix("NUMGARA", true))[0].getValue().getStringValue());
    }

    try {
		  //Ricavo Versione Simog dalla gara
		  Long ver_simog = (Long)sqlManager.getObject("select ver_simog from w3gara where NUMGARA = ?", new Object[] {numgara});
		  page.setAttribute("ver_simog", ver_simog, PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
    	throw new JspException(
    			"Errore nel recupero della versione simog dalla gara", e);
    }
  }
}
