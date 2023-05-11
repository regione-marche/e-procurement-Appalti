package it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.functions.archWhereFunctions.AbstractComponiWhereFunction;
import it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereUtils;

/**
 * Funzione usata per creare la condizione where per l'archivio GARE.
 *
 * @author Alvise Gorinati
 */
public class ComponiWhereGAREFunction extends AbstractComponiWhereFunction {

  // Function IDs
  private static final String DITTA_NOT_NULL_ID = "dittaNotNull";
  private static final String G1CRIMOD_ID = "g1crimod";
  private static final String ASSOCIA_GARA_ID = "associaGara";
  private static final String PERI_ID = "peri";

  // Where conditions
  private static final String BASE_WHERE =
      "gare.codgar1 = ? AND gare.codgar1 != gare.ngara";
  private static final String ADDITIONAL_WHERE =
      "EXISTS (SELECT gare1.ngara FROM gare1 WHERE gare.ngara = gare1.ngara AND (gare1.valtec = '1' OR gare.modlicg = 6))";
  private static final String DITTA_NOT_NULL_WHERE =
      "gare.ditta IS NOT NULL";
  private static final String G1CRIMOD_WHERE =
      "gare.modlicg = 6 AND EXISTS (SELECT * FROM goev WHERE goev.ngara = gare.ngara)";
  private static final String ASSOCIA_GARA_WHERE =
      "gare.codgar1 IN (SELECT codgar FROM torn WHERE torn.tipgen = ? #FILTRO_LIVELLO_UTENTE#) "
    + "AND gare.ngara NOT IN (SELECT gare.ngara FROM gare, appa WHERE appa.codlav = gare.clavor "
    + "AND appa.nappal = gare.numera AND (appa.codlav <> ? OR appa.nappal <> ?)) "
    + "AND (gare.genere IS NULL OR gare.genere NOT IN (3, 10, 11)) AND gare.codgar1 NOT IN (SELECT ngara FROM gare WHERE genere = 3)";
  private static final String PERI_NUOVO_WHERE =
      "gare.ngara NOT IN (SELECT gare.ngara FROM gare, appa WHERE appa.codlav = gare.clavor AND appa.nappal = gare.numera) "
    + "AND (gare.genere IS NULL OR gare.genere NOT IN (3, 10, 11)) AND gare.codgar1 NOT IN (SELECT ngara FROM gare WHERE genere = 3)";
  private static final String PERI_WHERE =
      "gare.ngara NOT IN (SELECT gare.ngara FROM gare, appa WHERE appa.codlav = gare.clavor AND appa.nappal = gare.numera "
    + "AND (appa.codlav <> ? OR appa.nappal <> ?)) AND (gare.genere IS NULL OR gare.genere NOT IN (3, 10, 11)) "
    + "AND gare.codgar1 NOT IN (SELECT ngara FROM gare WHERE genere = 3)";

  // Costruttori
  public ComponiWhereGAREFunction() {
    super(Logger.getLogger(ComponiWhereGAREFunction.class), "GARE");
  }

  // Metodi
  @Override
  protected void integrityChecks(final PageContext pageContext) throws JspException {
    if (functionId.equals(DEFAULT_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 1);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else if (functionId.equals(DITTA_NOT_NULL_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else if (functionId.equals(G1CRIMOD_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else if (functionId.equals(ASSOCIA_GARA_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 3);
    } else if (functionId.equals(PERI_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 1);
      final String modo = functionParams[0];
      int paramCount = 0;
      if (!modo.equals("NUOVO")) {
        paramCount = 2;
      }

      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, paramCount);
    } else if (functionId.equals(SKIP_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else {
      throw ComponiWhereUtils.getUnknownFunctionIdException(functionId, ENTITA, LOGGER);
    }
  }

  @Override
  protected String composeWhere(final PageContext pageContext, final int popUpId) throws JspException {
    int busta = 0;
    if (functionId.equals(DEFAULT_ID)) {
      busta = Integer.parseInt(functionParams[0]);
    }

    String where = ComponiWhereUtils.getWhere(pageContext.getSession(), ENTITA, popUpId);
    if (StringUtils.isNotEmpty(where)) {
      if (functionId.equals(DEFAULT_ID) ||
          functionId.equals(DITTA_NOT_NULL_ID)) {
        where += " AND (" + BASE_WHERE;

        if (functionId.equals(DEFAULT_ID) && busta == 2) {
          where += " AND " + ADDITIONAL_WHERE;
        } else if (functionId.equals(DITTA_NOT_NULL_ID)) {
          where += " AND " + DITTA_NOT_NULL_WHERE;
        }

        where += ")";
      } else if (functionId.equals(G1CRIMOD_ID)) {
        where += " AND (" + G1CRIMOD_WHERE + ")";
      } else if (functionId.equals(ASSOCIA_GARA_ID)) {
        where += " AND (" + composeAssociaGaraWhere(pageContext) + ")";
      } else if (functionId.equals(PERI_ID)) {
        where += " AND (";

        if (functionParams[0].equals("NUOVO")) {
          where += PERI_NUOVO_WHERE;
        } else {
          where += PERI_WHERE;
        }

        where += ")";
      }
    } else {
      if (functionId.equals(DEFAULT_ID) ||
          functionId.equals(DITTA_NOT_NULL_ID)) {
        where = BASE_WHERE;

        if (functionId.equals(DEFAULT_ID) && busta == 2) {
          where += " AND " + ADDITIONAL_WHERE;
        } else if (functionId.equals(DITTA_NOT_NULL_ID)) {
          where += " AND " + DITTA_NOT_NULL_WHERE;
        }
      } else if (functionId.equals(G1CRIMOD_ID)) {
        where = G1CRIMOD_WHERE;
      } else if (functionId.equals(ASSOCIA_GARA_ID)) {
        where = composeAssociaGaraWhere(pageContext);
      } else if (functionId.equals(PERI_ID)) {
        if (functionParams[0].equals("NUOVO")) {
          where = PERI_NUOVO_WHERE;
        } else {
          where = PERI_WHERE;
        }
      } else if (functionId.equals(SKIP_ID)) {
        where = "";
      }
    }

    return where;
  }

  private String composeAssociaGaraWhere(final PageContext pageContext) {
    String where = new String(ASSOCIA_GARA_WHERE);

    final String filtroUtente = ComponiWhereUtils.getFiltroLivelloUtente(pageContext, "TORN");
    if (!StringUtils.isEmpty(filtroUtente)) {
      where = where.replace("#FILTRO_LIVELLO_UTENTE#", "AND " + filtroUtente);
    } else {
      where = where.replace("#FILTRO_LIVELLO_UTENTE#", "");
    }

    return where;
  }

}
