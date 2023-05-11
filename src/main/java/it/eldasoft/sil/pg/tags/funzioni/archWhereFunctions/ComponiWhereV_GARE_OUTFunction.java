package it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.functions.archWhereFunctions.AbstractComponiWhereFunction;
import it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereUtils;

/**
 * Funzione usata per creare la condizione where per l'archivio V_GARE_OUT.
 * 
 * @author Alvise Gorinati
 */
public class ComponiWhereV_GARE_OUTFunction extends AbstractComponiWhereFunction {

  // Where conditions
  private static final String DEFAULT_WHERE =
      "v_gare_out.lotti = ? AND NOT EXISTS (SELECT ngara FROM gare WHERE v_gare_out.id_lista = gare.cliv1)";
  
  // Costruttori
  public ComponiWhereV_GARE_OUTFunction() {
    super(Logger.getLogger(ComponiWhereV_GARE_OUTFunction.class), "V_GARE_OUT");
  }

  // Metodi
  @Override
  protected void integrityChecks(PageContext pageContext) throws JspException {
    if (functionId.equals(DEFAULT_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else {
      throw ComponiWhereUtils.getUnknownFunctionIdException(functionId, ENTITA, LOGGER);
    }
  }

  @Override
  protected String composeWhere(PageContext pageContext, int popUpId) throws JspException {
    String where = ComponiWhereUtils.getWhere(pageContext.getSession(), ENTITA, popUpId);
    if (StringUtils.isNotEmpty(where)) {
      if (functionId.equals(DEFAULT_ID)) {
        where += " AND (" + DEFAULT_WHERE + ")";
      }
    } else {
      if (functionId.equals(DEFAULT_ID)) {
        where = DEFAULT_WHERE;
      } else if (functionId.equals(SKIP_ID)) {
        where = "";
      }
    }
    
    return where;
  }

}
