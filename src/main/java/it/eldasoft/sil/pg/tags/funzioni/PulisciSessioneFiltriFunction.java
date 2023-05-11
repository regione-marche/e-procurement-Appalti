/*
 * Created on 04-04-2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
/**
 * Funzione che elimina dalla sessione le variabili adoperate per la
 * gestione delle pagine popup-trova-filtroDitte.jsp e popup-trova-filtroCategorie.jsp
 * e per la gestione nel caso di elenchi dei filtri impostati dalla home e dalla pagina
 * di ricerca avanzata
 *
 * @author Marcello Caminiti
 */
public class PulisciSessioneFiltriFunction extends AbstractFunzioneTag {

  public PulisciSessioneFiltriFunction() {
    super(1, new Class[] { Object.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {


    HttpSession sessione = pageContext.getSession();
    sessione.removeAttribute("trovaDITG");
    sessione.removeAttribute("filtroDitte");
    sessione.removeAttribute("filtroCategorie");
    sessione.removeAttribute("filtroAppalti");
    sessione.removeAttribute("trovaANTICOR");
    sessione.removeAttribute("filtroComunicazioniIn");
    sessione.removeAttribute("filtroComunicazioniOut");
    sessione.removeAttribute("trovaW_INVCOM");
    sessione.removeAttribute("filtroLotti");
    if(params.length>0){
      if(params[0]!=null && ("homeElenchi".equals(params[0])|| "homeCataloghi".equals(params[0]) || "ricercaElenchi".equals(params[0]) || "ricercaCataloghi".equals(params[0]))){
          sessione.removeAttribute("campoFiltroHome");
          sessione.removeAttribute("valoreFiltroHome");
          sessione.removeAttribute("filtroDitteLocale");
          sessione.removeAttribute("valoreFiltroCodimp");
          sessione.removeAttribute("valoreFiltroNomimo");
          sessione.removeAttribute("valoreFiltroCF");
          sessione.removeAttribute("valoreFiltroPIVA");
          sessione.removeAttribute("valoreFiltroTipimp");
          sessione.removeAttribute("valoreFiltroIsmpmi");
          sessione.removeAttribute("valoreFiltroEmail");
          sessione.removeAttribute("valoreFiltroPec");
          sessione.removeAttribute("valoreFiltroCodCat");
          sessione.removeAttribute("valoreFiltroDescCat");
          sessione.removeAttribute("valoreFiltroTipcat");
          sessione.removeAttribute("valoreFiltroNumclass");
          sessione.removeAttribute("valoreFiltroAbilitaz");
          sessione.removeAttribute("valoreFiltroDricind");
          sessione.removeAttribute("valoreFiltroDscad");
          sessione.removeAttribute("valoreFiltroAltnot");
          sessione.removeAttribute("valoreFiltroCoordsic");
          sessione.removeAttribute("valoreFiltroStrin");
      }else if(params[0]!=null && ("listaElenchi".equals(params[0]) || "listaCataloghi".equals(params[0]))){
        sessione.removeAttribute("filtroDitteLocale");
      }
      if(params[0]!=null && ("homeAlboCommissione".equals(params[0]))){
        sessione.removeAttribute("filtroNominativi");
      }

    }

    return null;
  }
}
