/*
 * Created on 19/nov/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.utils.utility.UtilityNumeri;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.PageContext;

/**
 * Funzione per l'inizializzazione della pagina Aggiudicazione provvisoria e
 * definitiva per le gare divise in lotti con offerta unica
 *
 * @author Luca.Giacomazzo
 */
public class GestioneAggiudProvDefOffertaUnicaFunction extends GestioneFasiGaraFunction {

  /**
   * Reimplementazione dell'omonimo metodo della classe padre per modificare
   * la gestione della barra del wizard per le gare divise in lotti con offerta
   * unica
   */
  @Override
  protected String gestioneAvanzamentoWizard(String paginaAttivaWizard,
      PageContext pageContext, Long modalitaAggiudicazioneGara, Long iterGara,
      boolean isGaraLottiConOffertaUnica, boolean isSorteggioControlloRequisiti, boolean isValtec, String ricastae, boolean visOffertaEco) {

  	int wizardPaginaAttiva = UtilityNumeri.convertiIntero(paginaAttivaWizard).intValue();

  	List<String> listaTitoliWizard = this.getTitoliWizard();

  	int pos = listaTitoliWizard.indexOf("Proposta di aggiudicazione");
  	if (pos > 0)
  	  listaTitoliWizard.set(pos, "Proposta di aggiudicazione lotto");

  	List<String> listaPagineVisitate = new ArrayList<String>();
    List<String> listaPagineDaVisitare = new ArrayList<String>();

    if(wizardPaginaAttiva < FASE_CALCOLO_AGGIUDICAZIONE){
    	wizardPaginaAttiva = FASE_CALCOLO_AGGIUDICAZIONE;
    	paginaAttivaWizard = "" + wizardPaginaAttiva;
    }
    int indicePartenza = 10;
    // Mapping fra la fase di gara attiva (paginaAttivaWizard) e il titolo
    // della fase stessa
    int indiceLimite = 10;
    int indiceFine = listaTitoliWizard.size();

    switch(wizardPaginaAttiva) {
    case FASE_CALCOLO_AGGIUDICAZIONE:
      indiceLimite = 11;
      break;
    case FASE_AGGIUDICAZIONE_PROVVISORIA:
      indiceLimite = 12;
      break;
    }

    for(int i = indicePartenza; i < indiceLimite; i++)
      listaPagineVisitate.add(listaTitoliWizard.get(i));

    for(int i = indiceLimite; i < indiceFine; i++)
   		listaPagineDaVisitare.add(listaTitoliWizard.get(i));

    pageContext.setAttribute("pagineVisitate", listaPagineVisitate);
    pageContext.setAttribute("pagineDaVisitare", listaPagineDaVisitare);

    return paginaAttivaWizard;
  }

}