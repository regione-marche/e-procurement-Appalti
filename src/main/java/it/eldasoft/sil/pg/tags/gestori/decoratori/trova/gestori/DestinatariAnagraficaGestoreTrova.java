package it.eldasoft.sil.pg.tags.gestori.decoratori.trova.gestori;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.decorators.trova.gestori.AbstractGestoreTrova;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;

public class DestinatariAnagraficaGestoreTrova extends AbstractGestoreTrova {

	// Costanti
	private static final String CAMPO_FITT_NAME = "CampoFitt";
	private static final String FILTRO_RADIO_NAME = "filtroRadio";
	private static final String IDPRG_NAME = "idprg";
	private static final String IDCOM_NAME = "idcom";

	private static final String GARA_LOTTO_1_FR1_FILTER =
			"IMPR.CODIMP NOT IN (select DESCODSOG from w_invcomdes where IDPRG = '%s' and IDCOM = %s AND DESCODENT='IMPR' AND DESCODSOG is not null) and (IMPR.TIPIMP is null or (IMPR.TIPIMP <> 3 and IMPR.TIPIMP <> 10))";
	private static final String GARA_LOTTO_2_FR1_FILTER =
			"IMPR.TIPIMP is null or (IMPR.TIPIMP <> 3 and IMPR.TIPIMP <> 10)";
	private static final String GARA_LOTTO_1_FR2_FILTER =
			"TECNI.CODTEC NOT IN (select DESCODSOG from w_invcomdes where IDPRG = '%s' and IDCOM = %s AND DESCODENT='TECNI' AND DESCODSOG is not null)";
	private static final String USRSYS_FILTER =  "exists (select *  from USRSYS where USRSYS.SYSCF = TECNI.CFTEC)"; 

	private final String IDPRG;
	private final String IDCOM;

	// Costruttori
	public DestinatariAnagraficaGestoreTrova(HttpServletRequest request, String entity) {
		super(Logger.getLogger(DestinatariAnagraficaGestoreTrova.class), request, entity);

		IDPRG = UtilityStruts.getParametroString(request, IDPRG_NAME);
		IDCOM = UtilityStruts.getParametroString(request, IDCOM_NAME);

		popUpLevel=1;
	}

	// Metodi
	@Override
	public String composeFilter() {
		if (LOGGER.isDebugEnabled()) LOGGER.debug("composeFilter: inizio metodo");

		final int campoFittVal = Integer.parseInt(UtilityStruts.getParametroString(request, CAMPO_FITT_NAME));

		if (LOGGER.isDebugEnabled()) LOGGER.debug("composeFilter: fine metodo");

		return getFilters()[campoFittVal - 1];
	}

	private String[] getFilters() {
		final int radioFilterValue = Integer.parseInt(UtilityStruts.getParametroString(request, FILTRO_RADIO_NAME));

		switch (radioFilterValue) {
		case 1:
			return new String[] { String.format(GARA_LOTTO_1_FR1_FILTER, IDPRG, IDCOM), GARA_LOTTO_2_FR1_FILTER };
		case 2:
			return new String[] { String.format(GARA_LOTTO_1_FR2_FILTER, IDPRG, IDCOM), "" };
		case 3:
			return new String[] { String.format(USRSYS_FILTER+" AND " + GARA_LOTTO_1_FR2_FILTER, IDPRG, IDCOM), USRSYS_FILTER };
		default:
			return new String[] { "", "" };
		}
	}

}
