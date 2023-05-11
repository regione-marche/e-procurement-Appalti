package it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.functions.archWhereFunctions.AbstractComponiWhereFunction;
import it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereUtils;

/**
 * Funzione usata per creare la condizione where per l'archivio V_IMPR_SMAT.
 * 
 * @author Alvise Gorinati
 */
public class ComponiWhereV_IMPR_SMATFunction extends AbstractComponiWhereFunction {

  // Where conditions
  private static final String DEFAULT_WHERE =
      "(v_impr_smat.tipimp <> 3 AND v_impr_smat.tipimp <> 10) OR v_impr_smat.tipimp IS NULL";
  
  // Costruttori
  public ComponiWhereV_IMPR_SMATFunction() {
    super(Logger.getLogger(ComponiWhereV_IMPR_SMATFunction.class), "V_IMPR_SMAT");
  }

  // Metodi
  @Override
  protected void integrityChecks(final PageContext pageContext) throws JspException {
    ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
    if (functionId.equals(DEFAULT_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else if (functionId.equals(SKIP_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else {
      throw ComponiWhereUtils.getUnknownFunctionIdException(functionId, ENTITA, LOGGER);
    }
  }

  @Override
  protected String composeWhere(final PageContext pageContext, final int popUpId) throws JspException {
    String where = ComponiWhereUtils.getWhere(pageContext.getSession(), ENTITA, popUpId);
    if (StringUtils.isNotEmpty(where)) {
      if (functionId.equals(DEFAULT_ID)) {
        where += " AND (" + DEFAULT_WHERE + ")";
      }
    } else {
      if (functionId.equals(DEFAULT_ID)) {
        where = DEFAULT_ID;
      } else if (functionId.equals(SKIP_ID)) {
        where = "";
      }
    }
    
    return where;
  }

}
