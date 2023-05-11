package it.eldasoft.sil.pg.tags.gestori.decoratori.trova.gestori;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.decorators.trova.gestori.AbstractGestoreTrova;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;

public class RicercaDitteW_INVCOMDESGestoreTrova extends AbstractGestoreTrova{

	// Costanti
	private static final String CAMPO_FITT_NAME = "CampoFitt";
	private static final String IDPRG_NAME = "idprg";
	private static final String IDCOM_NAME = "idcom";
	private static final String COMKEY1_NAME = "comkey1";

	private static final String GARA_LOTTO_1_FILTER =
			"DITG.NGARA5 = '%s' AND DITG.DITTAO NOT IN (select DESCODSOG from w_invcomdes where IDPRG = '%s' and IDCOM = %s AND DESCODSOG is not null) and RTOFFERTA is null";
	private static final String GARA_LOTTO_2_FILTER =
			"DITG.NGARA5 = '%s'  and RTOFFERTA is null";

	private final String IDPRG;
	private final String IDCOM;
	private final String COMKEY1;

	// Costruttori
	public RicercaDitteW_INVCOMDESGestoreTrova(HttpServletRequest request, String entity) {
		super(Logger.getLogger(RicercaDitteW_INVCOMDESGestoreTrova.class), request, entity);

		IDPRG = UtilityStruts.getParametroString(request, IDPRG_NAME);
		IDCOM = UtilityStruts.getParametroString(request, IDCOM_NAME);
		COMKEY1 = UtilityStruts.getParametroString(request, COMKEY1_NAME);

		popUpLevel=1;
	}

	// Metodi
	@Override
	public String composeFilter() {
		String filter = "";

		if (LOGGER.isDebugEnabled()) LOGGER.debug("composeFilter: inizio metodo");

		final String campoFittValStr = UtilityStruts.getParametroString(request, CAMPO_FITT_NAME);
		try {
			final int campoFittVal = Integer.parseInt(campoFittValStr);

			if (campoFittVal == 1) {
				filter = String.format(GARA_LOTTO_1_FILTER, COMKEY1, IDPRG, IDCOM);
			} else {
				filter = String.format(GARA_LOTTO_2_FILTER, COMKEY1);
			}
		} catch (Exception e) {
			LOGGER.warn(String.format("Valore del campo non numerico: %s", campoFittValStr));
			return filter;
		}

		if (LOGGER.isDebugEnabled()) LOGGER.debug("composeFilter: fine metodo");

		return filter;
	}

}
