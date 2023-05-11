package it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.functions.archWhereFunctions.AbstractComponiWhereFunction;
import it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereUtils;

//import it.eldasoft.gene.tags.decorators.archivi.ArchivioTagImpl;
//import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * Funzione usata per creare la condizione where per l'archivio NSO_ORDINI.
 * 
 * @author Alvise Gorinati
 */
public class ComponiWhereNSO_ORDINIFunction extends AbstractComponiWhereFunction {

  // Where conditions
  private static final String DEFAULT_WHERE =
      "nso_ordini.id <> ?";
  
  // Costruttori
  public ComponiWhereNSO_ORDINIFunction() {
    super(Logger.getLogger(ComponiWhereNSO_ORDINIFunction.class), "NSO_ORDINI");
  }

  // Metodi
  @Override
  protected void integrityChecks(final PageContext pageContext) throws JspException {
    if (functionId.equals(DEFAULT_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 1);
      
      if (!functionParams[0].equalsIgnoreCase("true") && !functionParams[0].equalsIgnoreCase("false")) {
        throw ComponiWhereUtils.getWrongParamTypeException(Boolean.class, functionParams[0], ENTITA, LOGGER);
      }
      final boolean modifica = Boolean.parseBoolean(functionParams[0]);
      
      if (modifica) {
        ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
      } else {
        ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
      }
    } else if (functionId.equals(SKIP_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else {
      throw ComponiWhereUtils.getUnknownFunctionIdException(functionId, ENTITA, LOGGER);
    }
  }

  @Override
  protected String composeWhere(final PageContext pageContext, final int popUpId) throws JspException {
    String where = ComponiWhereUtils.getWhere(pageContext.getSession(), ENTITA, popUpId);
    final String filtroUffint = ComponiWhereUtils.getFiltroUffint(pageContext);
    
    if (StringUtils.isNotEmpty(where)) {
      if (functionId.equals(DEFAULT_ID)) {
        final boolean modifica = Boolean.parseBoolean(functionParams[0]);
        
        where += " AND (";
        if (modifica) {
          where += DEFAULT_WHERE;
          if (StringUtils.isNotEmpty(filtroUffint)) {
            where += " AND " + ComponiWhereUtils.getFiltroUffint(pageContext);
          }
        } else {
          where += ComponiWhereUtils.getFiltroUffint(pageContext);
        }
        where += ")";
      }
    } else {
      if (functionId.equals(DEFAULT_ID)) {
        final boolean modifica = Boolean.parseBoolean(functionParams[0]);
        
        if (modifica) {
          where = DEFAULT_WHERE;
          if (StringUtils.isNotEmpty(filtroUffint)) {
            where += " AND " + ComponiWhereUtils.getFiltroUffint(pageContext);
          }
        } else {
          where = ComponiWhereUtils.getFiltroUffint(pageContext);
        }
      } else if (functionId.equals(SKIP_ID)) {
        where = "";
      }
    }
    
    return where;
  }

}
