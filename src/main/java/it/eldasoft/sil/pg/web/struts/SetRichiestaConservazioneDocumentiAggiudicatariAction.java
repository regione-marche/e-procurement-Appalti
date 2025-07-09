/* 	Created on 21/03/2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.tasks.ArchiviazioneDocumentiManager;
import it.eldasoft.sil.pg.bl.tasks.ConservazioneDocumentiAggiudicatariManager;
import it.eldasoft.sil.pg.bl.tasks.CreazioneArchivioDocumentiGaraManager;
import it.eldasoft.utils.properties.ConfigManager;

/**
 * Funzione per l'archiviazione dei documenti degli aggiudicatari. Solo per wsdm TITULUS
 * @author Manuel Bridda
 */
public class SetRichiestaConservazioneDocumentiAggiudicatariAction extends ActionBaseNoOpzioni {

  protected static final String    FORWARD_SUCCESS           = "setrichiestaconservazionedocumentiaggiudicatarisuccess";
  protected static final String    FORWARD_ERROR             = "setrichiestaconservazionedocumentiaggiudicatarierror";

  static Logger                    logger            = Logger.getLogger(InviaRichiestaExportDocumentiAction.class);

  private ConservazioneDocumentiAggiudicatariManager  conservazioneDocumentiAggiudicatariManager;

  public void setConservazioneDocumentiAggiudicatariManager(ConservazioneDocumentiAggiudicatariManager conservazioneDocumentiAggiudicatariManager) {
    this.conservazioneDocumentiAggiudicatariManager = conservazioneDocumentiAggiudicatariManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("AvviaConservazioneDocumentiAggiudicatariAction: inizio metodo");

    String target = FORWARD_ERROR;

    String codgar = request.getParameter("codgar");
    String codice = request.getParameter("codice");
    String genere = request.getParameter("genere");
    String idconfi =  request.getParameter("idconfi");

    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long syscon = new Long(profiloUtente.getId());
    Long sysdet = syscon;
    
    Long id = null;
    int livEvento = 1;
    String errMsg ="";
    try{
      String wsdmLoginComune = ConfigManager.getValore(GestioneWSDMManager.PROP_WSDM_LOGIN_COMUNE+idconfi);
      if (wsdmLoginComune != null && "1".equals(wsdmLoginComune)) {
        syscon = new Long(-1);
      }
      id =  conservazioneDocumentiAggiudicatariManager.insertJob(syscon, codgar, "GARE", sysdet);
      request.setAttribute("codgar", codgar);
      target = FORWARD_SUCCESS;
    }catch(Exception e){
      livEvento = 3;
      errMsg = e.getMessage();
      this.aggiungiMessaggio(request, "errors.generico", "Errore durante l'inserimento del job di archiviazione documenti aggiudicatario per la gara " + codgar);
    }finally{
      String descr ="Richiesta archiviazione documenti aggiudicatario per la gara ";
      if(id!=null)
        descr += "(id.richiesta: " + id.toString() +")";
      String setOggEvento = codice;
      if("1".equals(genere) || "3".equals(genere))
        setOggEvento = codgar;
      LogEvento logevento = LogEventiUtils.createLogEvento(request);
      logevento.setLivEvento(livEvento);
      logevento.setOggEvento(setOggEvento);
      logevento.setCodEvento("GA_WSDM_CONSERVA_AGGI");
      logevento.setDescr(descr);
      logevento.setErrmsg(errMsg);
      LogEventiUtils.insertLogEventi(logevento);
    }



    if (logger.isDebugEnabled()) logger.debug("AvviaConservazioneDocumentiAggiudicatariAction: fine metodo");

    return mapping.findForward(target);

  }

}
