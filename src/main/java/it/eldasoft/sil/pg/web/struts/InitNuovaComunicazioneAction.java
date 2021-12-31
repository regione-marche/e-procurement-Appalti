/*
 * Created on 16/12/2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.InitNuovaComunicazioneManager;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Esegue la lettura dei modelli disponibili per la creazione di una nuova comunicazione
 */
public class InitNuovaComunicazioneAction extends ActionBaseNoOpzioni {

  static Logger     logger = Logger.getLogger(InitNuovaComunicazioneAction.class);

  private InitNuovaComunicazioneManager initNuovaComunicazioneManager;

  public void setInitNuovaComunicazioneManager(InitNuovaComunicazioneManager initNuovaComunicazioneManager){
    this.initNuovaComunicazioneManager = initNuovaComunicazioneManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if(logger.isDebugEnabled()) logger.debug("InitNuovaComunicazioneAction: inizio metodo");

    String target = "success";
    String genere = new String(request.getParameter("genere"));
    Long genereLong = new Long(genere);
    String idconfi = request.getParameter("idconfi");
    try {
      List listaModelliComunicazioni = initNuovaComunicazioneManager.getListaModelliComunicazioni(genereLong);

      request.setAttribute("listaModelliComunicazioni", listaModelliComunicazioni);
      request.setAttribute("numeroTotaleModelliEstratti", new Long(listaModelliComunicazioni.size()));
      request.setAttribute("genere", genere);
      request.setAttribute("idconfi", idconfi);
      // set nel request del parameter per disabilitare la navigazione
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);
      target = "success";
    } catch (GestoreException e) {
      logger.error("Errore nella chiamata al metodo getListaModelliComunicazioni", e);
      target = "errore";
      this.aggiungiMessaggio(request, "errors.getListaModelliComunicazioni");
    }

    if(logger.isDebugEnabled()) logger.debug("InitNuovaComunicazioneAction: fine metodo");

    if(target != null)
      return mapping.findForward(target);
    else
      return null;
  }

}
