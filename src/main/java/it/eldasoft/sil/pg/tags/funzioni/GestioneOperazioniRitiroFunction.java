/*
 * Created on 17/lug/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per l'inizializzazione della pagina 'Operazioni di ritiro'
 * 
 * @author Marcello Caminiti
 */
public class GestioneOperazioniRitiroFunction extends AbstractFunzioneTag {

  public static final String    PARAM_WIZARD_PAGINA_ATTIVA          = "WIZARD_PAGINA_ATTIVA";

  // Fase 1 delle operazioni di ritiro
  public static final int       FASE_DA_RITIRARE       = 1;
  //Fase 2 delle operazioni di ritiro
  public static final int       FASE_RITIRATE_NON_STAMPATE  = 2;
  
  private static final String[] TITOLO_OPERAZIONI_RITIRO                    = new String[] {
      "Operazioni ritiro plichi","Da ritirare", "Ritirati e non stampati"};

  public GestioneOperazioniRitiroFunction() {
    super(1, new Class[] { String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

	  

    int faseGara = 1;
    
    String paginaAttivaWizard = (String) UtilityTags.getParametro(pageContext,
        GestioneFasiGaraFunction.PARAM_WIZARD_PAGINA_ATTIVA);

    if (paginaAttivaWizard == null
        || (paginaAttivaWizard != null && paginaAttivaWizard.length() == 0))
      paginaAttivaWizard = "" + faseGara;

    int wizardPaginaAttiva = UtilityNumeri.convertiIntero(paginaAttivaWizard).intValue();

    // Gestione dell'avanzamento del wizard
    this.gestioneAvanzamentoWizard(wizardPaginaAttiva, pageContext);

    // Gestione del filtro sulle ditte in base all'avanzamento del wizard
    this.gestioneFiltroFaseOpRitiro(wizardPaginaAttiva, pageContext);
    pageContext.setAttribute("paginaAttivaWizard", paginaAttivaWizard,
        PageContext.REQUEST_SCOPE);

    String updateLista = UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA);
    if (updateLista == null || updateLista.length() == 0) updateLista = "0";
    pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA,
        updateLista, PageContext.REQUEST_SCOPE);

    return null;
  }

  
  private void gestioneAvanzamentoWizard(int paginaAttivaWizard,
      PageContext pageContext) {
    List listaPagineVisitate = new ArrayList();
    List listaPagineDaVisitare = new ArrayList();
    int len;
    
    for (int i = 1; i <= paginaAttivaWizard; i++)
      listaPagineVisitate.add(TITOLO_OPERAZIONI_RITIRO[i]);

    len =TITOLO_OPERAZIONI_RITIRO.length;
    for (int i = paginaAttivaWizard + 1; i < len; i++)
      listaPagineDaVisitare.add(TITOLO_OPERAZIONI_RITIRO[i]);

    pageContext.setAttribute("pagineVisitate", listaPagineVisitate);
    pageContext.setAttribute("pagineDaVisitare", listaPagineDaVisitare);
  }

  private void gestioneFiltroFaseOpRitiro(int faseOpRitiroAttiva,
      PageContext pageContext) {
    StringBuffer result = new StringBuffer("");

    switch (faseOpRitiroAttiva) {
    case GestioneOperazioniRitiroFunction.FASE_DA_RITIRARE:
      result.append("V_DITTE_PRIT.RITIRO = 0");
      break;
    case GestioneOperazioniRitiroFunction.FASE_RITIRATE_NON_STAMPATE:
    	ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);

	    Integer idUtente = new Integer(profilo.getId());
    	String filtro="V_DITTE_PRIT.RITIRO = 1 ";
    	filtro+="and V_DITTE_PRIT.UTENTE=" + idUtente.toString();
	    result.append(filtro);

      break;
    
    }

    pageContext.setAttribute("filtroFaseOpRitiro", result.toString());
  }

}
