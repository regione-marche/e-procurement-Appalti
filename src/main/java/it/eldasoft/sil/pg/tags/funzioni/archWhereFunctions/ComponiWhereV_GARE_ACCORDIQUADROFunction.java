package it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.functions.archWhereFunctions.AbstractComponiWhereFunction;
import it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereUtils;

/**
 * Funzione usata per creare la condizione where per l'archivio V_GARE_ACCORDIQUADRO.
 *
 * @author Alvise Gorinati
 */
public class ComponiWhereV_GARE_ACCORDIQUADROFunction extends AbstractComponiWhereFunction {

  // Where conditions
  private static final String BASE_WHERE =
      "v_gare_accordiquadro.tipgen = ? AND (v_gare_accordiquadro.isarchi <> '1' OR v_gare_accordiquadro.isarchi IS NULL)";
  private static final String ADDITIONAL_CENINT_WHERE =
        "(NOT EXISTS (SELECT id FROM garaltsog g WHERE g.ngara = v_gare_accordiquadro.ngara) OR "
      + "EXISTS (SELECT id FROM garaltsog g WHERE g.ngara = v_gare_accordiquadro.ngara AND g.cenint = ?))";
  private static final String ADDITIONAL_AQOPER_WHERE =
      "V_GARE_ACCORDIQUADRO.AQOPER = ?";

  // Costruttori
  public ComponiWhereV_GARE_ACCORDIQUADROFunction() {
    super(Logger.getLogger(ComponiWhereV_GARE_ACCORDIQUADROFunction.class), "V_GARE_ACCORDIQUADRO");
  }

  // Metodi
  @Override
  protected void integrityChecks(final PageContext pageContext) throws JspException {
    if (functionId.equals(SKIP_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else if (functionId.equals(DEFAULT_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 3);

      if (!functionParams[0].equalsIgnoreCase("true") && !functionParams[0].equals("false")) {
        throw ComponiWhereUtils.getWrongParamTypeException(Boolean.class, functionParams[0], ENTITA, LOGGER);
      }

      if (!functionParams[1].equalsIgnoreCase("true") && !functionParams[1].equals("false")) {
        throw ComponiWhereUtils.getWrongParamTypeException(Boolean.class, functionParams[1], ENTITA, LOGGER);
      }

      if (!StringUtils.isNumeric("" + functionParams[2])) {
        throw ComponiWhereUtils.getWrongParamTypeException(Long.class, functionParams[2], ENTITA, LOGGER);
      }

      final boolean useCenint = Boolean.parseBoolean(functionParams[0]);
      final boolean checkAqoper = Boolean.parseBoolean(functionParams[1]);

      int expectedParamsCount = 1;
      if (useCenint) {
        expectedParamsCount++;
      }

      if (checkAqoper) {
        expectedParamsCount++;
      }

      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, expectedParamsCount);
    } else {
      throw ComponiWhereUtils.getUnknownFunctionIdException(functionId, ENTITA, LOGGER);
    }
  }

  @Override
  protected String composeWhere(final PageContext pageContext, final int popUpId) throws JspException {
    boolean useCenint = false;
    boolean checkAqoper = false;
    String tipgen = "";
    if (!functionId.equals(SKIP_ID)) {
      useCenint = Boolean.parseBoolean(functionParams[0]);
      checkAqoper = Boolean.parseBoolean(functionParams[1]);
      //Ho aggiunto ai parametri della functionId il tipgen, al solo fine di passarlo all'archivio per costruire il messaggio nell'archivio
      //Il valore di tipgen è presente nel trovaParameter in sessione, ma dalla jsp è troppo complesso risalire al suo valore
      tipgen = functionParams[2];
      pageContext.setAttribute("tipgen", tipgen);
    }

    String where = ComponiWhereUtils.getWhere(pageContext.getSession(), ENTITA, popUpId);
    if (StringUtils.isNotEmpty(where)) {
      if (functionId.equals(DEFAULT_ID)) {
        where += " AND (" + BASE_WHERE + ")";
      }
    } else {
      if (functionId.equals(DEFAULT_ID)) {
        where = BASE_WHERE;
      } else if (functionId.equals(SKIP_ID)) {
        where = "";
      }
    }

    if (useCenint) {
      where += " AND " + ADDITIONAL_CENINT_WHERE;
    }

    if (checkAqoper) {
      where += " AND " + ADDITIONAL_AQOPER_WHERE;
    }


    return where;
  }

}
