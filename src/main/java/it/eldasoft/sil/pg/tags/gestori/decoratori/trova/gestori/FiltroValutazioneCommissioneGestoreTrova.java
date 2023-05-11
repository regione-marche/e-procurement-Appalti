package it.eldasoft.sil.pg.tags.gestori.decoratori.trova.gestori;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.decorators.trova.gestori.AbstractGestoreTrova;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;

public class FiltroValutazioneCommissioneGestoreTrova extends AbstractGestoreTrova {

	// Costanti
    private static final String CAMPO_0_NAME = "Campo0";
    private static final String CAMPO_1_NAME = "Campo1";
    private static final String IS_VALUTAZIONE_COMMISSIONE_NAME = "isValutazioneCommissione";
	private static final String TIPPAR_NAME = "tippar";
	private static final String CODICE_GARA_DITG_NAME = "codiceGaraDitg";

	private static final String IS_VALUTAZIONE_COMMISSIONE_FILTER =
			"EXISTS (SELECT 1 FROM GFOF GF, GOEV G,G1CRIDEF C LEFT JOIN G1CRIVAL V ON V.IDCRIDEF= C.ID AND V.NGARA=DITG.NGARA5 AND V.DITTAO=DITG.DITTAO WHERE G.NGARA = DITG.NGARA5 AND C.NGARA = DITG.NGARA5 "
		  + "AND G.TIPPAR = %s AND G.NECVAN = C.NECVAN AND GF.NGARA2 = %s AND C.MAXPUN >0 AND (C.MODPUNTI = '1' or C.MODPUNTI = '3') AND V.COEFFI IS NULL AND GF.ESPGIU = '1' AND "
		  + "NOT EXISTS (SELECT 1 FROM G1CRIVALCOM G1 WHERE G1.IDGFOF  = GF.ID AND G1.IDCRIVAL = V.ID AND COEFFI IS NOT NULL))";

	private static final String IS_NOT_VALUTAZIONE_COMMISSIONE_FILTER =
			" EXISTS (SELECT 1 FROM G1CRIDEF C,GOEV G WHERE G.NGARA = DITG.NGARA5 AND C.NGARA = DITG.NGARA5 AND G.TIPPAR = %s AND G.NECVAN = C.NECVAN "
		  + " AND C.MAXPUN >0 AND (C.MODPUNTI = '1' or C.MODPUNTI = '3') AND NOT EXISTS (SELECT V.ID FROM G1CRIVAL V WHERE V.IDCRIDEF=C.ID AND V.NGARA=DITG.NGARA5 AND V.DITTAO=DITG.DITTAO AND COEFFI IS NOT NULL))";

	// Costruttori
	public FiltroValutazioneCommissioneGestoreTrova(HttpServletRequest request, String entity) {
		super(Logger.getLogger(FiltroValutazioneCommissioneGestoreTrova.class), request, entity);
		popUpLevel=1;
	}

	// Metodi
	@Override
	public String composeFilter() {
		String filter = "";

		final String campo0Value = UtilityStruts.getParametroString(request, CAMPO_0_NAME);
        final String campo1Value = UtilityStruts.getParametroString(request, CAMPO_1_NAME);


		if (LOGGER.isDebugEnabled()) LOGGER.debug("composeFilter: inizio metodo");

		if ((campo0Value == null || "".equals(campo0Value)) && (campo1Value == null || "".equals(campo1Value)) ) {
    		final String tippar = UtilityStruts.getParametroString(request, TIPPAR_NAME);

    		final String isValutazioneCommissioneStr = UtilityStruts.getParametroString(request, IS_VALUTAZIONE_COMMISSIONE_NAME);
    		try {
    			if (Boolean.parseBoolean(isValutazioneCommissioneStr)) {
    				final String codiceGaraDitg = UtilityStruts.getParametroString(request, CODICE_GARA_DITG_NAME);

    				filter = String.format(IS_VALUTAZIONE_COMMISSIONE_FILTER, tippar, codiceGaraDitg);
    			} else {
    				filter = String.format(IS_NOT_VALUTAZIONE_COMMISSIONE_FILTER, tippar);
    			}
    		} catch (Exception e) {
    			LOGGER.warn(String.format("Valore del campo non booleano: %s", isValutazioneCommissioneStr));
    			return filter;
    		}

		}

		if (LOGGER.isDebugEnabled()) LOGGER.debug("composeFilter: fine metodo");

		return filter;
	}

}
