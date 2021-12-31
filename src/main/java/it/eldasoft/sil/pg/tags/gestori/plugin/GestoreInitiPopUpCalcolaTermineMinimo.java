/*
 * Created on 23/03/120
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.sil.pg.bl.ScadenzeManager;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore richiamato dalla pagina popup-calcolaTermineMinimo.jsp per effettuare il calcolo del
 * numero giorni prelevati da CATSCA
 *
 * @author Marcello Caminiti
 */
public class GestoreInitiPopUpCalcolaTermineMinimo extends AbstractGestorePreload {

  public GestoreInitiPopUpCalcolaTermineMinimo(BodyTagSupportGene tag) {
    super(tag);
  }


  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {


    ScadenzeManager scadenzeManager = (ScadenzeManager) UtilitySpring.getBean(
        "scadenzeManager", page,ScadenzeManager.class);


    // lettura dei parametri di input
    String tipgen = page.getRequest().getParameter("tipgen");
    String tipgar = page.getRequest().getParameter("tipgar");
    String importo = page.getRequest().getParameter("importo");
    String prourg = page.getRequest().getParameter("prourg");
    String docweb = page.getRequest().getParameter("docweb");
    String terrid = page.getRequest().getParameter("terrid");
    String campo = page.getRequest().getParameter("campo");
    String entita = page.getRequest().getParameter("entita");
    String dinvit = page.getRequest().getParameter("dinvit");
    String faseInviti = page.getRequest().getParameter("faseInviti");
    String iterGara = page.getRequest().getParameter("iterGara");
    String messageKey=null;

    if ((tipgen== null || "".equals(tipgen) || importo == null || "".equals(importo) || tipgar == null || "".equals(tipgar)) && !"Si".equals(faseInviti)) {
      page.setAttribute("blocco","true", PageContext.REQUEST_SCOPE);
      messageKey = "error.calcolaTermineMinimo.bloccoFaseInvitiTorn";
      UtilityStruts.addMessage(page.getRequest(), "error",  messageKey, null);
      return;
    }else if ((tipgen== null || "".equals(tipgen) || importo == null || "".equals(importo) || tipgar == null || "".equals(tipgar)) && "Si".equals(faseInviti)) {
      if("TORN".equals(entita)){
        messageKey = "error.calcolaTermineMinimo.bloccoFaseInvitiTorn";
        UtilityStruts.addMessage(page.getRequest(), "error",  messageKey, null);

        //page.setAttribute("bloccoFaseInvitiTorn","true", PageContext.REQUEST_SCOPE);
        page.setAttribute("blocco","true", PageContext.REQUEST_SCOPE);
        return;
      }
      else if( "GARE".equals(entita)){
        messageKey = "error.calcolaTermineMinimo.bloccoFaseInvitiGare";
        UtilityStruts.addMessage(page.getRequest(), "error",  messageKey, null);
        //page.setAttribute("bloccoFaseInvitiGare","true", PageContext.REQUEST_SCOPE);
        page.setAttribute("blocco","true", PageContext.REQUEST_SCOPE);
        return;
      }
    }



      Integer tipoCalcolo = null;
      Integer giorniScadenzaChiarimenti=null;;
      if("DTEOFF".equals(campo) && !"Si".equals(faseInviti))
        tipoCalcolo = new Integer(1);
      else if("DTEPAR".equals(campo) && !"Si".equals(faseInviti))
        tipoCalcolo = new Integer(2);
      else
        tipoCalcolo = new Integer(3);

      Integer tipoApp = new Integer(tipgen);
      Double  ImportoDouble = new Double(importo);
      Integer tipoProc = new Integer(iterGara);

      if("".equals(prourg))
        prourg = null;

      if( "".equals(terrid))
        terrid = null;

      if( "".equals(docweb))
        docweb =null;


      Integer giorniScadenza = scadenzeManager.getGiorniScadenza(tipoCalcolo, tipoApp, ImportoDouble, tipoProc, prourg, terrid, null, docweb, null);
      if(giorniScadenza == null){
        //page.setAttribute("noGiorni","true", PageContext.REQUEST_SCOPE);
        page.setAttribute("blocco","true", PageContext.REQUEST_SCOPE);
        messageKey = "error.calcolaTermineMinimo.noGiorni";
        UtilityStruts.addMessage(page.getRequest(), "error",  messageKey, null);
        return;
      }else{

        if((dinvit==null || "".equals(dinvit)) && "Si".equals(faseInviti)){
          if("TORN".equals(entita)){
            messageKey = "error.calcolaTermineMinimo.dataInvitoTorn";
            UtilityStruts.addMessage(page.getRequest(), "error",  messageKey, null);
            page.setAttribute("blocco","true", PageContext.REQUEST_SCOPE);
            return;
          }
          else if( "GARE".equals(entita)){
            messageKey = "error.calcolaTermineMinimo.dataInvitoGare";
            UtilityStruts.addMessage(page.getRequest(), "error",  messageKey, null);
            page.setAttribute("blocco","true", PageContext.REQUEST_SCOPE);
            return;
          }

        }


        page.setAttribute("giorniScadenza",giorniScadenza, PageContext.REQUEST_SCOPE);

        if("DTEOFF".equals(campo)){
          tipoCalcolo = new Integer(4);

        }else{
          tipoCalcolo = new Integer(5);
        }
        giorniScadenzaChiarimenti = scadenzeManager.getGiorniScadenza(tipoCalcolo, tipoApp, ImportoDouble, tipoProc, prourg, terrid, null, docweb, null);
        if(giorniScadenzaChiarimenti == null)
          giorniScadenzaChiarimenti = new Integer(0);
        page.setAttribute("giorniScadenzaChiarimenti",giorniScadenzaChiarimenti, PageContext.REQUEST_SCOPE);
      }



  }

}