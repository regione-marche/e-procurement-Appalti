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
 * Funzione usata per creare la condizione where per l'archivio V_AGGIUDICATARI_STIPULA.
 *
 * @author Marcello Caminiti
 */
public class ComponiWhereV_AGGIUDICATARI_STIPULAFunction extends AbstractComponiWhereFunction {

  // Where conditions
  private static final String DEFAULT_WHERE = "exists (select gp.numper from g_permessi gp where gp.codgar=v_aggiudicatari_stipula.codgar and gp.syscon=#ID_UTENTE#)"
      + " and not exists (select ngara,ncont from v_gare_stipula vgp where v_aggiudicatari_stipula.ngara=vgp.ngara and v_aggiudicatari_stipula.ncont=vgp.ncont)";

  private static final String WHERE_A ="codice is not null and not exists "
      + "(select ngara,ncont from v_gare_stipula vgp where v_aggiudicatari_stipula.ngara=vgp.ngara and v_aggiudicatari_stipula.ncont=vgp.ncont)";

  // Costruttori
  public ComponiWhereV_AGGIUDICATARI_STIPULAFunction() {
    super(Logger.getLogger(ComponiWhereV_AGGIUDICATARI_STIPULAFunction.class), "V_AGGIUDICATARI_STIPULA");
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
    String whereDefault=null;
    if (!"A".equals(abilitazioneGare)) {
      final int id = profiloUtente.getId();
      whereDefault = DEFAULT_WHERE.replace("#ID_UTENTE#", Integer.toString(id));
    }

    String where = ComponiWhereUtils.getWhere(pageContext.getSession(), ENTITA, popUpId);
    if (StringUtils.isNotEmpty(where)) {
      if ("A".equals(abilitazioneGare))
        where += " AND " + WHERE_A ;
      else
        where += " AND " + whereDefault ;
    } else {
      if ("A".equals(abilitazioneGare))
        where = WHERE_A ;
      else
        where = whereDefault ;
    }

    // Filtro UFFINT
    final String uffint = (String) pageContext.getSession().getAttribute("uffint");
    if (StringUtils.isNotEmpty(uffint)) {
      where += " AND CENINT = '" + uffint + "'";
    }

    return where;
  }

}
