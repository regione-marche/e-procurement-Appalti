package it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.functions.archWhereFunctions.AbstractComponiWhereFunction;
import it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereUtils;

/**
 * Funzione usata per creare la condizione where per l'archivio V_GARE_TORN.
 * 
 * @author Alvise Gorinati
 */
public class ComponiWhereV_GARE_TORNFunction extends AbstractComponiWhereFunction {
  
  // Function IDs
  private static final String GARE_A_LOTTI_ID = "gareALotti";
  
  // Where conditions
  private static final String GARE_A_LOTTI_WHERE = 
      "v_gare_torn.tipgen = ? AND v_gare_torn.genere = '1'";
  
  // Costruttori
  public ComponiWhereV_GARE_TORNFunction() {
    super(Logger.getLogger(ComponiWhereV_GARE_TORNFunction.class), "V_GARE_TORN");
  }

  // Metodi
  @Override
  protected void integrityChecks(PageContext pageContext) throws JspException {
    ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
    if (functionId.equals(GARE_A_LOTTI_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else if (functionId.equals(SKIP_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else {
      throw ComponiWhereUtils.getUnknownFunctionIdException(functionId, ENTITA, LOGGER);
    }
  }

  @Override
  protected String composeWhere(PageContext pageContext, int popUpId) throws JspException {
    String where = ComponiWhereUtils.getWhere(pageContext.getSession(), ENTITA, popUpId);
    if (StringUtils.isNotEmpty(where)) {
      if (functionId.equals(GARE_A_LOTTI_ID)) {
        where += " AND (" + GARE_A_LOTTI_WHERE + ")";
      }
    } else {
      if (functionId.equals(GARE_A_LOTTI_ID)) {
        where = GARE_A_LOTTI_WHERE;
      } else if (functionId.equals(SKIP_ID)) {
        where = "";
      }
    }
    
    return where;
  }

}
