/*
 * Created on 10/01/13
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per l'inizializzazione delle pagine 'Aggiudicazione provvisoria'
 * e 'Aggiudicazione definitiva' delle gare ad Offerta unica
 *
 * @author Marcello Caminiti
 */
public class GestionePagineAggProvvDefOffertaUnicaFunction extends AbstractFunzioneTag {

  public static final String    PARAM_WIZARD_PAGINA_ATTIVA          = "WIZARD_PAGINA_ATTIVA";

  // Fase 1 Aggiudicazione Provvisoria
  public static final int       FASE_CALCOLO_AGGIUDICAZIONE_LOTTI       = 1;
  // Fase 2 Aggiudicazione Provvisoria
  public static final int       FASE_DATI_GARA_PROVVISORI  = 2;

//Fase 1 Aggiudicazione Definitiva
  public static final int       FASE_AGGIUDICAZIONE_DEFINTIVA_LOTTI       = 1;
  // Fase 2 Aggiudicazione Definitiva
  public static final int       FASE_DATI_GARA_DEFINITIVA  = 2;


  private static final String[] TITOLO_FASI_PROVVISORIA                    = new String[] {
    "Proposta di aggiudicazione", "Calcolo aggiudicazione lotti", "Dati di gara"};

  private static final String[] TITOLO_FASI_DEFINITIVA                    = new String[] {
    "Aggiudicazione definitiva", "Chiusura proposta di aggiudicazione", "Aggiudicazione definitiva lotti", "Chiusura aggiudicazione definitiva"};

  private String tipoAggiudicazione= null;

  public GestionePagineAggProvvDefOffertaUnicaFunction() {
    super(3, new Class[] { PageContext.class,String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

	//int faseAggiudicazione = 1;
    String stepApertura = UtilityTags.getParametro(pageContext,"stepIniziale");

	tipoAggiudicazione = (String)params[2];

	SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
	        pageContext, SqlManager.class);

	String codiceGara = UtilityTags.getParametro(pageContext,
	        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
	codiceGara = codiceGara.substring(codiceGara.indexOf(":") + 1);

	String paginaAttivaWizard = UtilityTags.getParametro(pageContext,
	        GestioneFasiGaraFunction.PARAM_WIZARD_PAGINA_ATTIVA);

	if (paginaAttivaWizard == null
	        || (paginaAttivaWizard != null && paginaAttivaWizard.length() == 0)){

	  if(stepApertura!=null)
	    paginaAttivaWizard = "" + stepApertura;
	  else
	    paginaAttivaWizard = "1";
	}

	int wizardPaginaAttiva = UtilityNumeri.convertiIntero(paginaAttivaWizard).intValue();

    // Gestione dell'avanzamento del wizard
    this.gestioneAvanzamentoWizard(wizardPaginaAttiva, pageContext);

    String campoDitta="ditta";
    if("1".equals(paginaAttivaWizard)){
      campoDitta="dittap";
    }


    //lotti aggiudicati
    	try{
	    	String select="select count(ngara) from gare where codgar1 = ? and (genere is null or genere <> 3) and " + campoDitta + " is not null";
	    	Long numLottiAggiudicati = (Long) sqlManager.getObject(
	    			select, new Object[]{codiceGara});
	    	if (numLottiAggiudicati != null)
	    		pageContext.setAttribute("numLottiAggiudicati",
	    				numLottiAggiudicati, PageContext.REQUEST_SCOPE);

	    	select="select count(ngara) from gare where codgar1 = ? and (genere is null or genere <> 3) and " + campoDitta + " is null";
	    	Long numLottiNoAggiudicati = (Long) sqlManager.getObject(
	    			select, new Object[]{codiceGara});
	    	if (numLottiNoAggiudicati != null)
	    		pageContext.setAttribute("numLottiNoAggiudicati",
	    				numLottiNoAggiudicati, PageContext.REQUEST_SCOPE);

    	}catch (SQLException s) {
	      throw new JspException(
	              "Errore durante la lettura numero di lotti aggiudicati e non ", s);
	        }


    String updateLista = UtilityTags.getParametro(pageContext,
            UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA);
    if (updateLista == null || updateLista.length() == 0) updateLista = "0";
    pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA,
        updateLista, PageContext.REQUEST_SCOPE);

    pageContext.setAttribute("paginaAttivaWizard", paginaAttivaWizard,
            PageContext.REQUEST_SCOPE);

     // Creazione del parametro con la chiave da passare alla pagina di controllo
     // delle autorizzazioni
     String inputFiltro = "CODGAR=T:".concat(codiceGara);
     pageContext.setAttribute("inputFiltro", inputFiltro,
            PageContext.REQUEST_SCOPE);

	return null;
  }

  private void gestioneAvanzamentoWizard(int paginaAttivaWizard,
	      PageContext pageContext) {
	    List listaPagineVisitate = new ArrayList();
	    List listaPagineDaVisitare = new ArrayList();
	    int len;

	    if("AggProv".equals(tipoAggiudicazione)){
    	    for (int i = 1; i <= paginaAttivaWizard; i++)
    	      listaPagineVisitate.add(TITOLO_FASI_PROVVISORIA[i]);

    	    len =TITOLO_FASI_PROVVISORIA.length;
    	    for (int i = paginaAttivaWizard + 1; i < len; i++)
    	      listaPagineDaVisitare.add(TITOLO_FASI_PROVVISORIA[i]);
	    }else {
            for (int i = 1; i <= paginaAttivaWizard; i++)
              listaPagineVisitate.add(TITOLO_FASI_DEFINITIVA[i]);

            len =TITOLO_FASI_DEFINITIVA.length;
            for (int i = paginaAttivaWizard + 1; i < len; i++)
              listaPagineDaVisitare.add(TITOLO_FASI_DEFINITIVA[i]);

	    }

	    pageContext.setAttribute("pagineVisitate", listaPagineVisitate);
	    pageContext.setAttribute("pagineDaVisitare", listaPagineDaVisitare);
	  }
}
