package it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.functions.archWhereFunctions.AbstractComponiWhereFunction;
import it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereUtils;

/**
 * Funzione usata per creare la condizione where per l'archivio PERI.
 * 
 * @author Alvise Gorinati
 */
public class ComponiWherePERIFunction extends AbstractComponiWhereFunction {

  // Function IDs
  private static final String PROGETTI_ID = "progetti";
  private static final String CODLAV_NOT_EQUAL_ID = "codlavNotEqual";
  private static final String INTTRI_ID = "inttri";
  
  // Where conditions
  private static final String DEFAULT_WHERE =
      "peri.cenint = ?";
  private static final String CODLAV_NOT_EQUAL_WHERE = 
      "peri.codlav <> ?";
  private static final String PROGETTI_WHERE = 
      "peri.codlav <> ? AND peri.livpro = ?";
  private static final String INTTRI_WHERE =
      "NOT EXISTS (SELECT inttri.codint FROM inttri WHERE inttri.contri = ? AND inttri.codint = peri.codlav) AND (peri.fornserv IS NULL OR peri.fornserv = '2')";
  
  // Costruttori
  public ComponiWherePERIFunction() {
    super(Logger.getLogger(ComponiWherePERIFunction.class), "PERI");
  }

  // Metodi
  @Override
  protected void integrityChecks(final PageContext pageContext) throws JspException {
    if (functionId.equals(DEFAULT_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else if (functionId.equals(PROGETTI_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 2);
    } else if (functionId.equals(CODLAV_NOT_EQUAL_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else if (functionId.equals(INTTRI_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      
      if (!functionParams[0].equalsIgnoreCase("true") && !functionParams[0].equalsIgnoreCase("false")) {
        throw ComponiWhereUtils.getWrongParamTypeException(Boolean.class, functionParams[0], ENTITA, LOGGER);
      }
      
      int expectedParamCount = 1;
      if (Boolean.parseBoolean(functionParams[0])) {
        expectedParamCount = 2;
      }
      
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, expectedParamCount);
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
    if (StringUtils.isNotEmpty(where)) {
      if (functionId.equals(DEFAULT_ID)) {
        where += " AND (" + DEFAULT_WHERE + ")";
      } else if (functionId.equals(PROGETTI_ID)) {
        where += "AND (" + PROGETTI_WHERE;
        
        final String filtroLivelloUtente = ComponiWhereUtils.getFiltroLivelloUtente(pageContext, ENTITA);
        if (StringUtils.isNotEmpty(filtroLivelloUtente)) {
          where += " AND " + filtroLivelloUtente;
        }
        
        where += ")";
      } else if (functionId.equals(CODLAV_NOT_EQUAL_ID)) {
        where += " AND (" + CODLAV_NOT_EQUAL_WHERE + ")";
      } else if (functionId.equals(INTTRI_ID)) {
        where += " AND (" + INTTRI_WHERE;
        
        if (Boolean.parseBoolean(functionParams[0])) {
          where += " AND " + DEFAULT_WHERE;
        }
        
        where += ")";
      }
    } else {
      if (functionId.equals(DEFAULT_ID)) {
        where = DEFAULT_WHERE;
      } else if (functionId.equals(PROGETTI_ID)) {
        where = PROGETTI_WHERE;
        
        final String filtroLivelloUtente = ComponiWhereUtils.getFiltroLivelloUtente(pageContext, ENTITA);
        if (StringUtils.isNotEmpty(filtroLivelloUtente)) {
          where += " AND " + filtroLivelloUtente;
        }
      } else if (functionId.equals(CODLAV_NOT_EQUAL_ID)) {
        where = CODLAV_NOT_EQUAL_WHERE;
      } else if (functionId.equals(INTTRI_ID)) {
        where = INTTRI_ID;
        
        if (Boolean.parseBoolean(functionParams[0])) {
          where += " AND " + DEFAULT_WHERE;
        }
      } else if (functionId.equals(SKIP_ID)) {
        where = "";
      }
    }
    
    return where;
  }

}
