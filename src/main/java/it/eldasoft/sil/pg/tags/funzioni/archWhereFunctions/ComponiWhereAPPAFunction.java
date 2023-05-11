package it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.functions.archWhereFunctions.AbstractComponiWhereFunction;
import it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereUtils;

/**
 * Funzione usata per creare la condizione where per l'archivio APPA.
 *
 * @author Alvise Gorinati
 */
public class ComponiWhereAPPAFunction extends AbstractComponiWhereFunction {

  // Function IDs
  private static final String ASSOCIA_APPALTO_ID = "associaAppalto";
  private static final String MODIFICA_GARE_DATIGEN_ID = "modificaGareDatigen";
  private static final String GARE_DATIGEN_ID = "gareDatigen";
  private static final String CODLAV_FILTER_ID = "codlavFilter";
  private static final String IMPORT_ID = "importPrimus";

  // Where conditions
  private static final String ASSOCIA_APPALTO_WHERE =
      "appa.dagg IS NULL AND appa.dvoagg IS NULL AND appa.tiplavg = ? AND NOT EXISTS "
    + "(SELECT ngara FROM gare WHERE appa.codlav = gare.clavor AND appa.nappal = gare.numera AND gare.esineg IS NULL)";
  private static final String MODIFICA_GARE_DATIGEN_WHERE =
      "appa.dagg IS NULL AND appa.dvoagg IS NULL AND appa.tiplavg = ? AND NOT EXISTS "
    + "(SELECT ngara FROM gare WHERE appa.codlav = gare.clavor AND appa.nappal = gare.numera AND gare.ngara != ?)";
  private static final String GARE_DATIGEN_WHERE =
      "appa.dagg IS NULL AND appa.dvoagg IS NULL AND appa.tiplavg = ? AND NOT EXISTS "
    + "(SELECT ngara FROM gare WHERE appa.codlav = gare.clavor AND appa.nappal = gare.numera)";
  private static final String CODLAV_FILTER_WHERE =
      "appa.codlav = ?";
  private static final String IMPORT_WHERE =
      "peri.cenint = ?";

  // Filters
  private static final String CUP_FILTER =
      "appa.codlav IN (SELECT codlav FROM peri WHERE peri.cupprg LIKE ?)";

  // Costruttori
  public ComponiWhereAPPAFunction() {
    super(Logger.getLogger(ComponiWhereAPPAFunction.class), "APPA");
  }

  // Metodi
  @Override
  protected void integrityChecks(final PageContext pageContext) throws JspException {
    if (functionId.equals(ASSOCIA_APPALTO_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 1);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else if (functionId.equals(GARE_DATIGEN_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else if (functionId.equals(MODIFICA_GARE_DATIGEN_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 2);
    } else if (functionId.equals(CODLAV_FILTER_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else if (functionId.equals(IMPORT_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
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
      if (functionId.equals(ASSOCIA_APPALTO_ID)) {
        where += " AND (" + ASSOCIA_APPALTO_WHERE + ")";
      } else if (functionId.equals(MODIFICA_GARE_DATIGEN_ID)) {
        where += " AND (" + MODIFICA_GARE_DATIGEN_WHERE + ")";
      } else if (functionId.equals(GARE_DATIGEN_ID)) {
        where += " AND (" + GARE_DATIGEN_WHERE + ")";
      } else if (functionId.equals(CODLAV_FILTER_ID)) {
        where += " AND (" + CODLAV_FILTER_WHERE + ")";
      } else if (functionId.equals(IMPORT_ID)) {
        where += " AND (" + IMPORT_WHERE + ")";
      }
    } else {
      if (functionId.equals(ASSOCIA_APPALTO_ID)) {
        where = ASSOCIA_APPALTO_WHERE;
      } else if (functionId.equals(MODIFICA_GARE_DATIGEN_ID)) {
        where = MODIFICA_GARE_DATIGEN_WHERE;
      } else if (functionId.equals(GARE_DATIGEN_ID)) {
        where = GARE_DATIGEN_WHERE;
      } else if (functionId.equals(CODLAV_FILTER_ID)) {
        where = CODLAV_FILTER_WHERE;
      } else if (functionId.equals(IMPORT_ID)) {
        where = IMPORT_WHERE;
      } else if (functionId.equals(SKIP_ID)) {
        where = "";
      }
    }

    if (functionId.equals(ASSOCIA_APPALTO_ID)) {
      // Filtro livello utente
      final String filtroLivelloUtente = ComponiWhereUtils.getFiltroLivelloUtente(pageContext, "PERI");
      if (StringUtils.isNotEmpty(filtroLivelloUtente)) {
        where += " AND " + filtroLivelloUtente.replaceAll("PERI.CODLAV", "APPA.CODLAV");
      }

      // Filtro UFFINT
      final String filtroUffint = ComponiWhereUtils.getFiltroUffint(pageContext);
      if (StringUtils.isNotEmpty(filtroUffint)) {
        where +=  filtroUffint;
      }

      // Add filtro CUP
      final boolean addCupFilter = Boolean.parseBoolean(functionParams[0]);
      if (addCupFilter) {
        where += " AND " + CUP_FILTER;
      }
    } else if (functionId.equals(MODIFICA_GARE_DATIGEN_ID) ||
        functionId.equals(GARE_DATIGEN_ID)) {
      // Filtro livello utente
      final String filtroLivelloUtente = ComponiWhereUtils.getFiltroLivelloUtente(pageContext, "PERI");
      if (StringUtils.isNotEmpty(filtroLivelloUtente)) {
        where += " " + filtroLivelloUtente.replaceAll("PERI.CODLAV", "APPA.CODLAV");
      }
    }

    return where;
  }

}
