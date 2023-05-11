package it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.functions.archWhereFunctions.AbstractComponiWhereFunction;
import it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereUtils;

/**
 * Funzione usata per creare la condizione where per l'archivio G1STIPULA.
 *
 * @author Marcello Caminiti
 */
public class ComponiWhereG1STIPULAFunction extends AbstractComponiWhereFunction {

  // Where conditions
  private static final String WHERE_BASE = "stato=5 and livello=0";

  private static final String WHERE_AGGIUNTIVA ="and exists (select gp.numper from g_permessi gp where gp.idstipula=g1stipula.id and gp.syscon=#ID_UTENTE#)";

  // Costruttori
  public ComponiWhereG1STIPULAFunction() {
    super(Logger.getLogger(ComponiWhereG1STIPULAFunction.class), "G1STIPULA");
  }

  // Metodi
  @Override
  protected void integrityChecks(PageContext pageContext) throws JspException {
    if (functionId.equals(DEFAULT_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    }else {
      throw ComponiWhereUtils.getUnknownFunctionIdException(functionId, ENTITA, LOGGER);
    }
  }

  @Override
  protected String composeWhere(PageContext pageContext, int popUpId) throws JspException {

    final ProfiloUtente profiloUtente = (ProfiloUtente) pageContext.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    String abilitazioneGare = profiloUtente.getAbilitazioneGare();
    String whereAggiuntiva=null;
    if (!"A".equals(abilitazioneGare)) {
      final int id = profiloUtente.getId();
      whereAggiuntiva = WHERE_AGGIUNTIVA.replace("#ID_UTENTE#", Integer.toString(id));
    }

    String where = ComponiWhereUtils.getWhere(pageContext.getSession(), ENTITA, popUpId);
    if (StringUtils.isNotEmpty(where)) {
      if ("A".equals(abilitazioneGare))
        where += " AND " + WHERE_BASE ;
      else
        where += " AND " + WHERE_BASE + " " +  whereAggiuntiva;
    } else {
      if ("A".equals(abilitazioneGare))
        where = WHERE_BASE ;
      else
        where = WHERE_BASE + " " +  whereAggiuntiva ;
    }

    // Filtro UFFINT
    final String uffint = (String) pageContext.getSession().getAttribute("uffint");
    if (StringUtils.isNotEmpty(uffint)) {
      where += " and exists (select id from v_gare_stipula vgs where vgs.id=g1stipula.id and vgs.cenint='" + uffint + "')";
    }

    return where;
  }

}
