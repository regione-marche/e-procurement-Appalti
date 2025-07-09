package it.eldasoft.sil.w3.tags.gestori.plugin;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;

public class GestoreW3GARA extends AbstractGestorePreload {

  /** Logger */
  static Logger logger = Logger.getLogger(GestoreW3GARA.class);

  /**
   * 
   * @param tag
   */
  public GestoreW3GARA(BodyTagSupportGene tag) {
    super(tag);
  }

  public void doAfterFetch(PageContext arg0, String arg1) throws JspException {

  }

  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

	  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", page, SqlManager.class);
	  String cfein = null;
	  String cfanac = null;
	  String iscuc = null;
	  String azienda_denom = null;
	  HttpSession session = page.getSession();
	  String codein = (String) session.getAttribute("uffint");
	  codein = StringUtils.stripToEmpty(codein);
	  try {
		    if ("".equals(codein) && !UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modoAperturaScheda)) {
		      codein= (String) sqlManager.getObject("select codein from w3gara where numgara=?", new Object[] {UtilityTags.getParametro(page,"numgara")});
		     }
		    if(!"".equals(codein)){
			  //ricavo il CF della SA
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
	    } catch (SQLException e) {
	      throw new JspException(
	          "Errore nel recupero del codice fiscale della Stazione appaltante", e);
	    }
      
    if (UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modoAperturaScheda)) {
      page.setAttribute("azienda_denom", azienda_denom, PageContext.REQUEST_SCOPE);

    } else {
    	//calcolo il totale importo gara
      try {
      	String codice = UtilityTags.getParametro(page, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);

        DataColumnContainer key = new DataColumnContainer(codice);
        Long numgara = new Long((key.getColumnsBySuffix("NUMGARA", true))[0].getValue().getStringValue());

        Object importoObj = sqlManager.getObject("select SUM(" + sqlManager.getDBFunction("isnull", new String[] {"IMPORTO_LOTTO", "0.00" }) + ") from W3LOTT where NUMGARA=? and STATO_SIMOG in (1,2,3,4,7,99)", 
            new Object[] { numgara });

        Double importo = new Double(0);
        if (importoObj != null) {
          if (!(importoObj instanceof Double)) {
            importo = new Double(importoObj.toString());
          } else {
            importo = (Double) importoObj;
          }
        }
      	page.setAttribute("importo", importo, PageContext.REQUEST_SCOPE);
      	//calcolo numero lotti
      	Long numero_lotti = (Long) sqlManager.getObject("select count(*) from w3lott where numgara = ? and stato_simog in (1,2,3,4,7,99)", new Object[] { numgara });
      	page.setAttribute("numero_lotti", numero_lotti, PageContext.REQUEST_SCOPE);
        
      } catch (SQLException e) {
          throw new JspException("Errore nel calcolo dell'importo gara", e);
      }
    }
  }
}
