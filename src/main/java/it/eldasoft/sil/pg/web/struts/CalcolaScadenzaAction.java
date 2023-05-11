/*
 * Created on 07/nov/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.sil.pg.bl.ScadenzeManager;
import it.eldasoft.utils.utility.UtilityDate;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per il calcolo della data scadenza e predisposizione del settaggio
 * successivo nella pagina
 * 
 * @author Stefano.Sabbadin
 */
public class CalcolaScadenzaAction extends ActionBaseNoOpzioni {

  /** Logger */
  static Logger           logger = Logger.getLogger(CalcolaScadenzaAction.class);

  /** Manager della BL per la gestione delle scadenze */
  private ScadenzeManager scadenzeManager;

  /**
   * @param scadenzeManager
   *        scadenzeManager da settare internamente alla classe.
   */
  public void setScadenzeManager(ScadenzeManager scadenzeManager) {
    this.scadenzeManager = scadenzeManager;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String target = CostantiGeneraliStruts.FORWARD_OK;

    String valoreDataDestinazione = "";
    CalcolaScadenzaForm calcolaScadenzaForm = (CalcolaScadenzaForm) form;

    String descrCampoDestinazione = getNomeCampoDestinazione(calcolaScadenzaForm.getCalcolo().intValue());
    
    if (descrCampoDestinazione == null) {
      request.setAttribute("errore", "1");
    } else {
      try {
        Integer giorniScadenza = this.scadenzeManager.getGiorniScadenza(
            calcolaScadenzaForm.getCalcolo(),
            calcolaScadenzaForm.getTipgen(), calcolaScadenzaForm.getImportoAppalto(),
            calcolaScadenzaForm.getTipgar(), calcolaScadenzaForm.getProurg(),
            calcolaScadenzaForm.getTerrid(), calcolaScadenzaForm.getBanweb(),
            calcolaScadenzaForm.getDocweb(), calcolaScadenzaForm.getOggcont());

        // se viene calcolato un numero di giorni, allora si calcola la data
        // scadenza sommando i giorni alla data di origine
        if (giorniScadenza != null) {
          GregorianCalendar dataDestinazione = new GregorianCalendar();
          dataDestinazione.setTimeInMillis(calcolaScadenzaForm.getDataOrigineAsDate().getTime());
          dataDestinazione.add(Calendar.DAY_OF_MONTH, giorniScadenza.intValue());
          valoreDataDestinazione = UtilityDate.convertiData(new Date(
              dataDestinazione.getTimeInMillis()), UtilityDate.FORMATO_GG_MM_AAAA);
        }
      } catch (Throwable e) {
        logger.error(
            "Si sono verificati degli errori durante il calcolo del numero di giorni di scadenza",
            e);
        // non si modifica il forward in quanto dalla popup, in base al test
        // su alcuni attributi, o si setta il dato o si apre un'alert
        // nell'opener
        request.setAttribute("errore", "1");
      }
    }

    // si settano le informazioni (eventualmente se la data scadenza non è
    // valorizzata allora vuol dire che c'è stato qualche problema o i dati
    // in input non erano sufficienti)
    request.setAttribute("campoDataScadenza",
        calcolaScadenzaForm.getCampoDataDestinazione());
    request.setAttribute("descrCampoDataScadenza", descrCampoDestinazione);
    request.setAttribute("dataScadenza", valoreDataDestinazione);

    return mapping.findForward(target);
  }

  /**
   * Ritorna il nome del campo destinazione del calcolo della scadenza
   * 
   * @param calcolo
   *        tipo di calcolo
   * @return nome del campo destinazione, null se non previsto
   */
  private String getNomeCampoDestinazione(int tipoCalcolo) {
    String descrCampoDestinazione;
    switch (tipoCalcolo) {
    case 1:
    case 3:
      descrCampoDestinazione = "Data ricezione offerte";
      break;
    case 2:
      descrCampoDestinazione = "Data ricezione domande";
      break;
    case 4:
      descrCampoDestinazione = "Data fine pubblicazione bando";
      break;
    case 5:
      descrCampoDestinazione = "Data termine invio";
      break;
    default:
      descrCampoDestinazione = null;
    }
    return descrCampoDestinazione;
  }
}
