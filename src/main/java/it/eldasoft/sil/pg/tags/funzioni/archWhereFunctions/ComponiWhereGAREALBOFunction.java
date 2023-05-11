package it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.functions.archWhereFunctions.AbstractComponiWhereFunction;
import it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereUtils;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

/**
 * Funzione usata per creare la condizione where per l'archivio GAREALBO.
 *
 * @author Alvise Gorinati
 */
public class ComponiWhereGAREALBOFunction extends AbstractComponiWhereFunction {

  // Function IDs
  private static final String NOT_BLANK_CATIGA_ORA_ID = "notBlankCatigaOra";
  private static final String NOT_BLANK_CATIGA_MSQ_ID = "notBlankCatigaMsq";
  private static final String NOT_BLANK_CATIGA_POS_ID = "notBlankCatigaPos";
  private static final String NOT_BLANK_CATIGA_DATIGEN_ID = "notBlankCatigaDatigen";

  private static final String BLANK_CATIGA_ORA_ID = "blankCatigaOra";
  private static final String BLANK_CATIGA_MSQ_ID = "blankCatigaMsq";
  private static final String BLANK_CATIGA_POS_ID = "blankCatigaPos";
  private static final String BLANK_CATIGA_DATIGEN_ID = "blankCatigaDatigen";

  // Query GARE
  private static final String GARE_NOT_BLANK_CATIGA_ORA_WHERE =
      "(garealbo.dtermval IS NULL OR garealbo.dtermval >= TRUNC(sysdate)) AND garealbo.ngara IN (SELECT opes.ngara3 FROM opes WHERE opes.catoff = ?)";
  private static final String GARE_BLANK_CATIGA_ORA_WHERE =
      "(garealbo.dtermval IS NULL OR garealbo.dtermval >= TRUNC(sysdate)) AND garealbo.ngara IN (SELECT ngara FROM garealbo WHERE tipoele IN %s)";
  private static final String GARE_NOT_BLANK_CATIGA_MSQ_WHERE =
      "(garealbo.dtermval IS NULL OR garealbo.dtermval >= CONVERT(DATETIME, CONVERT(VARCHAR(10), getdate(), 111), 120)) AND garealbo.ngara IN (SELECT opes.ngara3 FROM opes WHERE opes.catoff = ?)";
  private static final String GARE_BLANK_CATIGA_MSQ_WHERE =
      "(garealbo.dtermval IS NULL OR garealbo.dtermval >= CONVERT(DATETIME, CONVERT(VARCHAR(10), getdate(), 111), 120)) AND garealbo.ngara IN (SELECT ngara FROM garealbo WHERE tipoele IN %s)";
  private static final String GARE_NOT_BLANK_CATIGA_POS_WHERE =
      "(garealbo.dtermval IS NULL OR garealbo.dtermval >= current_date) AND garealbo.ngara IN (SELECT opes.ngara3 FROM opes WHERE opes.catoff = ?)";
  private static final String GARE_BLANK_CATIGA_POS_WHERE =
      "(garealbo.dtermval IS NULL OR garealbo.dtermval >= current_date) AND garealbo.ngara IN (SELECT ngara FROM garealbo WHERE tipoele IN %s)";

  // Query TORN
  private static final String TORN_NOT_BLANK_CATIGA_ORA_WHERE =
      "(garealbo.dtermval IS NULL OR garealbo.dtermval >= sysdate) AND garealbo.ngara IN (SELECT opes.ngara3 FROM opes WHERE opes.catoff = ?)";
  private static final String TORN_BLANK_CATIGA_ORA_WHERE =
      "(garealbo.dtermval IS NULL OR garealbo.dtermval >= sysdate) AND garealbo.ngara IN (SELECT ngara FROM garealbo WHERE tipoele IN %s)";
  private static final String TORN_NOT_BLANK_CATIGA_MSQ_WHERE =
      "(garealbo.dtermval IS NULL OR garealbo.dtermval >= getdate()) AND garealbo.ngara IN (SELECT opes.ngara3 FROM opes WHERE opes.catoff = ?)";
  private static final String TORN_BLANK_CATIGA_MSQ_WHERE =
      "(garealbo.dtermval IS NULL OR garealbo.dtermval >= getdate()) AND garealbo.ngara IN (SELECT ngara FROM garealbo WHERE tipoele IN %s)";
  private static final String TORN_NOT_BLANK_CATIGA_POS_WHERE =
      "(garealbo.dtermval IS NULL OR garealbo.dtermval >= now()) AND garealbo.ngara IN (SELECT opes.ngara3 FROM opes WHERE opes.catoff = ?)";
  private static final String TORN_BLANK_CATIGA_POS_WHERE =
      "(garealbo.dtermval IS NULL OR garealbo.dtermval >= now()) AND garealbo.ngara IN (SELECT ngara FROM garealbo WHERE tipoele IN %s)";
  private static final String TORN_NOT_BLANK_CATIGA_DATIGEN_WHERE =
      "garealbo.ngara IN (SELECT opes.ngara3 FROM opes WHERE opes.catoff = ?)";

  //Query GAREALBO
  private static final String GAREALBO_WHERE = "GAREALBO.NGARA in (select NGARA from MECATALOGO where MECATALOGO.CODGAR=GAREALBO.CODGAR and MECATALOGO.NGARA=GAREALBO.NGARA) " +
      "and (GAREALBO.DTERMVAL >= #DATE# or GAREALBO.DTERMVAL is null)" +
      " and exists(select codgar9 from pubbli where codgar9=garealbo.codgar and tippub=11 and DATPUB <= #DATE#)";

  // Additional conditions
  private static final String ADDITIONAL_CONDITION =
      "garealbo.ngara IN (SELECT mecatalogo.ngara FROM mecatalogo)";

  // Costruttori
  public ComponiWhereGAREALBOFunction() {
    super(Logger.getLogger(ComponiWhereGAREALBOFunction.class), "GAREALBO");
  }

  // Metodi
  @Override
  protected void integrityChecks(final PageContext pageContext) throws JspException {
    if (functionId.equals(NOT_BLANK_CATIGA_ORA_ID) ||
        functionId.equals(NOT_BLANK_CATIGA_MSQ_ID) ||
        functionId.equals(NOT_BLANK_CATIGA_POS_ID) ||
        functionId.equals(NOT_BLANK_CATIGA_DATIGEN_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 3);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else if (functionId.equals(BLANK_CATIGA_ORA_ID) ||
        functionId.equals(BLANK_CATIGA_MSQ_ID) ||
        functionId.equals(BLANK_CATIGA_POS_ID) ||
        functionId.equals(BLANK_CATIGA_DATIGEN_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 3);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else if (functionId.equals(SKIP_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else {
      throw ComponiWhereUtils.getUnknownFunctionIdException(functionId, ENTITA, LOGGER);
    }
  }

  @Override
  protected String composeWhere(final PageContext pageContext, final int popUpId) throws JspException {
    StringBuilder builder = new StringBuilder();

    if (functionId.equals(SKIP_ID)) {
      return this.composeGarealboWhere(pageContext.getSession(), popUpId, pageContext);
    }

    String request = functionParams[0];
    int tipgen = Integer.parseInt(functionParams[1]);
    boolean addCondition = Boolean.parseBoolean(functionParams[2]);

    if (request.equals("gare")) {
      builder.append(composeGareWhere(pageContext.getSession(), functionId, popUpId, functionParams));
    } else if (request.equals("torn")) {
      builder.append(composeTornWhere(pageContext.getSession(), functionId, popUpId, functionParams));
    }

    if (!functionId.startsWith("not") && tipgen > 0) {
      builder = new StringBuilder(String.format(builder.toString(), fillInClause(tipgen)));
    }

    if (addCondition) {
      builder.append(" AND " + ADDITIONAL_CONDITION);
    }

    return builder.toString();
  }

  /**
   * Compone la condizione where per l'entità GARE
   *
   * @param where
   *        Condizione where da costruire
   * @param functionId
   *        Function ID
   */
  private String composeGareWhere(final HttpSession session, final String functionId, final int popUpId,
		  final String[] functionParams) {
    String where = ComponiWhereUtils.getWhere(session, ENTITA, popUpId);

    if (functionId.equals(SKIP_ID)) {
      return where;
    }

    boolean isNotEmpty = StringUtils.isNotEmpty(where);
    if (isNotEmpty) {
      where += " AND (";
    }

    if (functionId.equals(NOT_BLANK_CATIGA_ORA_ID)) {
      where += GARE_NOT_BLANK_CATIGA_ORA_WHERE;
    } else if (functionId.equals(BLANK_CATIGA_ORA_ID)) {
      where += GARE_BLANK_CATIGA_ORA_WHERE;
    } else if (functionId.equals(NOT_BLANK_CATIGA_MSQ_ID)) {
      where += GARE_NOT_BLANK_CATIGA_MSQ_WHERE;
    } else if (functionId.equals(BLANK_CATIGA_MSQ_ID)) {
      where += GARE_BLANK_CATIGA_MSQ_WHERE;
    } else if (functionId.equals(NOT_BLANK_CATIGA_POS_ID)) {
      where += GARE_NOT_BLANK_CATIGA_POS_WHERE;
    } else if (functionId.equals(BLANK_CATIGA_POS_ID)) {
      where += GARE_BLANK_CATIGA_POS_WHERE;
    }

    if (isNotEmpty) {
      where += ")";
    }

    return where;
  }

  /**
   * Compone la condizione where per l'entità TORN
   *
   * @param where
   *            Condizione where da costruire
   * @param functionId
   *            Function ID
   */
  private String composeTornWhere(final HttpSession session, final String functionId, final int popUpId,
		  final String[] functionParams) {
    String where = ComponiWhereUtils.getWhere(session, ENTITA, popUpId);

    if (functionId.equals(SKIP_ID)) {
      return where;
    }

    boolean isNotEmpty = StringUtils.isNotEmpty(where);
    if (isNotEmpty) {
      where += " AND (";
    }

    if (functionId.equals(NOT_BLANK_CATIGA_ORA_ID)) {
      where +=  TORN_NOT_BLANK_CATIGA_ORA_WHERE ;
    } else if (functionId.equals(BLANK_CATIGA_ORA_ID)) {
      where +=  TORN_BLANK_CATIGA_ORA_WHERE ;
    } else if (functionId.equals(NOT_BLANK_CATIGA_MSQ_ID)) {
      where +=  TORN_NOT_BLANK_CATIGA_MSQ_WHERE ;
    } else if (functionId.equals(BLANK_CATIGA_MSQ_ID)) {
      where +=  TORN_BLANK_CATIGA_MSQ_WHERE ;
    } else if (functionId.equals(NOT_BLANK_CATIGA_POS_ID)) {
      where +=  TORN_NOT_BLANK_CATIGA_POS_WHERE ;
    } else if (functionId.equals(BLANK_CATIGA_POS_ID)) {
      where +=  TORN_BLANK_CATIGA_POS_WHERE ;
    } else if (functionId.equals(NOT_BLANK_CATIGA_DATIGEN_ID)) {
      where +=  TORN_NOT_BLANK_CATIGA_DATIGEN_WHERE ;
    }

    if (isNotEmpty) {
      where += ")";
    }

    return where;
  }

  private String composeGarealboWhere(final HttpSession session, final int popUpId, PageContext pageContext) {

    String where = ComponiWhereUtils.getWhere(session, ENTITA, popUpId);
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    String dataOdierna = UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_GG_MM_AAAA);
    String dbFunctionStringToDate = sqlManager.getDBFunction("stringtodate",
        new String[] { dataOdierna });
    String whereGarealbo=GAREALBO_WHERE.replaceAll("#DATE#", dbFunctionStringToDate);

    boolean isNotEmpty = StringUtils.isNotEmpty(where);
    if (isNotEmpty) {
      where += " AND (";
    }

    where += whereGarealbo;

    if (isNotEmpty) {
      where += ")";
    }

    return where;
  }



  /**
   * Costruisce la "in clause" per le query che la prevedono partendo dal tipgen.
   *
   * @param where
   *            Condizione where da costruire
   * @param tipgen
   *            Tipgen
   */
  private String fillInClause(final int tipgen) {
    switch (tipgen) {
      case 1:
        return "('100', '110', '101', '111')";
      case 2:
        return "('10','110','11','111')";
      case 3:
        return "('1','11','101','111')";
      default:
        return "()";
    }
  }

}
