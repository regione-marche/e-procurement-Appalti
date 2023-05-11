package it.eldasoft.sil.pg.tags.gestori.decoratori.trova.gestori;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.decorators.trova.gestori.AbstractGestoreTrova;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;

public class MericGestoreTrova extends AbstractGestoreTrova {

	// Costanti
	private static final String RICERCHE_NON_COMPLETATE_NAME = "ricercheNonCompletate";
	
	private static final String FILTER =
			"id in (select idric from mericart a where not exists(select idric from v_odaprod p where p.idric= a.idric and p.idricart=a.id))";
	
	// Costruttori
	public MericGestoreTrova(HttpServletRequest request, String entity) {
		super(Logger.getLogger(MericGestoreTrova.class), request, entity);
	}

	// Metodi
	@Override
	public String composeFilter() {
		String filter = "";
		
		if (LOGGER.isDebugEnabled()) LOGGER.debug("composeFilter: inizio metodo");
		
		final String ricercheNonCompletateStr = UtilityStruts.getParametroString(request, RICERCHE_NON_COMPLETATE_NAME);
		try {
			final int ricercheNonCompletate = Integer.parseInt(ricercheNonCompletateStr);
			if (ricercheNonCompletate == 1) {
				filter += FILTER;
			}
		} catch (Exception e) {
			LOGGER.warn(String.format("Valore del campo non numerico: %s", ricercheNonCompletateStr));
			return filter;
		}

		if (LOGGER.isDebugEnabled()) LOGGER.debug("composeFilter: fine metodo");
		
		return filter;
	}

}
