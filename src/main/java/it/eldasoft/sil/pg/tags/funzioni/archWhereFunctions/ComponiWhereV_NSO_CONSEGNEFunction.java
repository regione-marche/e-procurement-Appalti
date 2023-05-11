package it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.functions.archWhereFunctions.AbstractComponiWhereFunction;
import it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereUtils;

/**
 * Funzione usata per creare la condizione where per l'archivio V_NSO_CONSEGNE.
 * 
 * @author Alvise Gorinati
 */
public class ComponiWhereV_NSO_CONSEGNEFunction extends AbstractComponiWhereFunction {

  // Costruttori
  public ComponiWhereV_NSO_CONSEGNEFunction() {
    super(Logger.getLogger(ComponiWhereV_NSO_CONSEGNEFunction.class), "V_NSO_CONSEGNE");
  }

  // Metodi
  @Override
  protected void integrityChecks(PageContext pageContext) throws JspException {
    if (functionId.equals(SKIP_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else {
      throw ComponiWhereUtils.getUnknownFunctionIdException(functionId, ENTITA, LOGGER);
    }
  }

  @Override
  protected String composeWhere(final PageContext pageContext, final int popUpId) throws JspException {
    String where = ComponiWhereUtils.getWhere(pageContext.getSession(), ENTITA, popUpId);
    if (StringUtils.isNotEmpty(where)) {
      // TODO inserire qui le condizioni a seconda del function ID
    } else {
      if (functionId.equals(SKIP_ID)) {
        where = "";
      }
    }
    
    return where;
  }

}
