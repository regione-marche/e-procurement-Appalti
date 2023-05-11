package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class EsisteTabellaFunction extends
		AbstractFunzioneTag {

	public EsisteTabellaFunction() {
		super(2, new Class[]{PageContext.class, String.class});
	}

	@Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
	    String esisteTabella = "0";

	    GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
	    		pageContext, GeneManager.class);

	    String nomeTabella = (String)params[1];
	    if (geneManager.esisteTabella(nomeTabella)) {
	      esisteTabella ="1";
	    }
	    return esisteTabella;
	}

}
