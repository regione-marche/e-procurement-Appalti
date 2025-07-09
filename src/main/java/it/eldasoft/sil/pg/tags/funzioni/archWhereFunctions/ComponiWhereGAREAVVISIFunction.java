package it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.functions.archWhereFunctions.AbstractComponiWhereFunction;
import it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereUtils;

public class ComponiWhereGAREAVVISIFunction extends AbstractComponiWhereFunction {

	// Where conditions
	private static final String BASE_WHERE = "exists (select gp.numper from g_permessi gp where gp.codgar=v_gare_avvisi.codgar and gp.syscon=#ID_UTENTE#) and exists (select vernpa from torn where torn.codgar=v_gare_avvisi.codgar and vernpa is not null)";
	private static final String BASE_WHERE_A = "exists (select vernpa from torn where torn.codgar=v_gare_avvisi.codgar and vernpa is not null)";

	// Costruttori
	public ComponiWhereGAREAVVISIFunction() {
		super(Logger.getLogger(ComponiWhereGAREAVVISIFunction.class), "V_GARE_AVVISI");
	}

	// Metodi
	@Override
	protected void integrityChecks(final PageContext pageContext) throws JspException {
		if (functionId.equals(SKIP_ID)) {
			ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
			ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
		} else if (functionId.equals(DEFAULT_ID)) {
			ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
//      int expectedParamsCount = 1;
//      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, expectedParamsCount);
		} else {
			throw ComponiWhereUtils.getUnknownFunctionIdException(functionId, ENTITA, LOGGER);
		}
	}

	@Override
	protected String composeWhere(final PageContext pageContext, final int popUpId) throws JspException {

		final ProfiloUtente profiloUtente = (ProfiloUtente) pageContext.getSession()
				.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
		String abilitazioneGare = profiloUtente.getAbilitazioneGare();
		String baseDefault = null;
		if (!"A".equals(abilitazioneGare)) {
			final int id = profiloUtente.getId();
			baseDefault = BASE_WHERE.replace("#ID_UTENTE#", Integer.toString(id));
		} else {
			baseDefault = BASE_WHERE_A;
		}

		String where = ComponiWhereUtils.getWhere(pageContext.getSession(), ENTITA, popUpId);
		if (StringUtils.isNotEmpty(where)) {
			if (functionId.equals(DEFAULT_ID)) {
				where += " AND (" + baseDefault + ")";
			}
		} else {
			if (functionId.equals(DEFAULT_ID)) {
				where = baseDefault;

			} else if (functionId.equals(SKIP_ID)) {
				where = "";
			}
		}

		// Filtro UFFINT
		final String uffint = (String) pageContext.getSession().getAttribute("uffint");
		if (StringUtils.isNotEmpty(uffint)) {
			if (StringUtils.isNotEmpty(where)) {
				where += " AND CENINT = '" + uffint + "'";
			} else {
				where = "CENINT = '" + uffint + "'";
			}
		}

		return where;
	}

}
