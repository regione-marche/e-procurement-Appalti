package it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.functions.archWhereFunctions.AbstractComponiWhereFunction;
import it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereUtils;

/**
 * Funzione usata per creare la condizione where per l'archivio DITG.
 * 
 * @author Alvise Gorinati
 */
public class ComponiWhereDITGFunction extends AbstractComponiWhereFunction {

  // Function IDs
  private static final String GARA_LOTTI_CON_OFFERTA_UNICA_ID = "garaLottiConOffertaUnica";
  private static final String GARA_LOTTI_SENZA_OFFERTA_UNICA_ID = "garaLottiSenzaOffertaUnica";
  private static final String DOCUMENTI_TRASPARENZA_ID = "documentiTrasparenza";
  
  // Where conditions
  private static final String CON_OFFERTA_UNICA_WHERE =
      "ditg.codgar5 = ? AND ditg.ngara5 = ditg.codgar5 AND (ditg.invoff <> '2' OR ditg.invoff IS NULL) AND (ditg.ammgar <> '2' OR ditg.ammgar IS NULL)";
  private static final String SENZA_OFFERTA_UNICA_WHERE = 
      "ditg.ngara5 = ? AND ditg.codgar5 = ? AND (ditg.invoff <> '2' OR ditg.invoff IS NULL) AND (ditg.ammgar <> '2' OR ditg.ammgar IS NULL)";
  private static final String DOCUMENTI_TRASPARENZA_WHERE =
      "ditg.ngara5 = ? AND ditg.codgar5 = ? AND ditg.dittao IN (SELECT DISTINCT(ditta) FROM gare WHERE codgar1 = ? AND ditta IS NOT NULL)";

  // Costruttori
  public ComponiWhereDITGFunction() {
    super(Logger.getLogger(ComponiWhereDITGFunction.class), "DITG");
  }

  // Metodi
  @Override
  protected void integrityChecks(final PageContext pageContext) throws JspException {
    ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
    if (functionId.equals(GARA_LOTTI_CON_OFFERTA_UNICA_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else if (functionId.equals(GARA_LOTTI_SENZA_OFFERTA_UNICA_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 2);
    } else if (functionId.equals(DOCUMENTI_TRASPARENZA_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 3);
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
      if (functionId.equals(GARA_LOTTI_CON_OFFERTA_UNICA_ID)) {
        where += " AND (" + CON_OFFERTA_UNICA_WHERE + ")";
      } else if (functionId.equals(GARA_LOTTI_SENZA_OFFERTA_UNICA_ID)) {
        where += " AND (" + SENZA_OFFERTA_UNICA_WHERE + ")";
      } else if (functionId.equals(DOCUMENTI_TRASPARENZA_ID)) {
        where += " AND (" + DOCUMENTI_TRASPARENZA_WHERE + ")";
      }
    } else {
      if (functionId.equals(GARA_LOTTI_CON_OFFERTA_UNICA_ID)) {
        where = CON_OFFERTA_UNICA_WHERE;
      } else if (functionId.equals(GARA_LOTTI_SENZA_OFFERTA_UNICA_ID)) {
        where = SENZA_OFFERTA_UNICA_WHERE;
      } else if (functionId.equals(DOCUMENTI_TRASPARENZA_ID)) {
        where = DOCUMENTI_TRASPARENZA_WHERE;
      } else if (functionId.equals(SKIP_ID)) {
        where = "";
      }
    }
    
    return where;
  }

}
