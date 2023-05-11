package it.eldasoft.sil.pg.tags.funzioni;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class IsPersonalizzazioneGenovaAttivaFunction extends
		AbstractFunzioneTag {

	public IsPersonalizzazioneGenovaAttivaFunction() {
		super(1, new Class[]{PageContext.class});
	}
	  
	public String function(PageContext pageContext, Object[] params) throws JspException {
	    String isPersonalizzazioneGenovaAttiva = "0";
	    
	    GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
	    		pageContext, GeneManager.class);
	    
	    if (geneManager.esisteTabella("v_usrsys_matricolario")) {
			isPersonalizzazioneGenovaAttiva ="1";
	    }
	    return isPersonalizzazioneGenovaAttiva;
	}

}
