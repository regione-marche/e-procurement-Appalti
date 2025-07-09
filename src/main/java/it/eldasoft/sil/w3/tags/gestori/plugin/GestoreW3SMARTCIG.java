package it.eldasoft.sil.w3.tags.gestori.plugin;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

public class GestoreW3SMARTCIG extends AbstractGestorePreload {

  /** Logger */
  static Logger logger = Logger.getLogger(GestoreW3SMARTCIG.class);

  /**
   * 
   * @param tag
   */
  public GestoreW3SMARTCIG(BodyTagSupportGene tag) {
    super(tag);
  }

  public void doAfterFetch(PageContext arg0, String arg1) throws JspException {

  }

  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

	  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
	          page, SqlManager.class);
  	
	  String cfein = null;
	  String cfanac = null;
	  String iscuc = null;
	  String azienda_denom = null;
      HttpSession session = page.getSession();
      String codein = (String) session.getAttribute("uffint");
      codein = StringUtils.stripToEmpty(codein);
      try {
            if ("".equals(codein) && !UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modoAperturaScheda)) {
              codein= (String) sqlManager.getObject("select codein from w3smartcig where codrich=?", new Object[] {UtilityTags.getParametro(page,"codrich")});
             }
            if(!"".equals(codein)){
			  Vector<?> datiSA = sqlManager.getVector(
		          "select cfein, cfanac, iscuc, nomein from uffint where codein = ?", new Object[] { codein });
			  if(datiSA!= null){
				  cfein = datiSA.get(0).toString();
				  cfanac = datiSA.get(1).toString();
				  iscuc = datiSA.get(2).toString();
				  azienda_denom = datiSA.get(3).toString();
		      }
			  if (iscuc != null && iscuc.equals("1") && cfanac != null && !cfanac.trim().equals("")) {
				  cfein = cfanac;
			  }
			  page.setAttribute("cfein", cfein, PageContext.REQUEST_SCOPE);
            }
	      }catch (SQLException e) {
	          throw new JspException(
	                  "Errore nel recupero del codice fiscale della Stazione appaltante", e);
	      }
	  
      
    if (UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modoAperturaScheda)) {
       page.setAttribute("azienda_denom", azienda_denom, PageContext.REQUEST_SCOPE);
    }
  }

}
