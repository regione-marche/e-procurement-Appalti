package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.SetProfiloAction;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;

public class SetProfiloVisualizzaGareAction extends SetProfiloAction {

	private static final String GENERE_PARAM = "genere";
	
	@Override
	protected ActionForward runAction(final ActionMapping mapping, final ActionForm form, 
			final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
		final ActionForward forward = super.runAction(mapping, form, request, response);
		
		final String genere = UtilityStruts.getParametroString(request, GENERE_PARAM);
		
		String entita;
		if (genere.equals("11")) {
	      entita = "GAREAVVISI";
	    } else {
	      entita = "V_GARE_TORN";
	    }
		
		UtilityTags.restoreHashAttributeForSqlBuild(request, entita, 0);
		UtilityTags.getUtilityHistory(request.getSession()).addQueriedEntity(entita);
		
		return forward;
	}
	
}
