/*
 * 	Created on 02/07/2013
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
import it.eldasoft.sil.pg.bl.tasks.CreazioneArchivioDocumentiGaraManager;

public class InviaRichiestaExportDocumentiAction extends ActionBaseNoOpzioni {

  protected static final String    FORWARD_SUCCESS           = "inviarichiestaexportdocumentisuccess";
  protected static final String    FORWARD_ERROR             = "inviarichiestaexportdocumentierror";

  static Logger                    logger            = Logger.getLogger(InviaRichiestaExportDocumentiAction.class);

  private CreazioneArchivioDocumentiGaraManager  creazioneArchivioDocumentiGaraManager;

  public void setCreazioneArchivioDocumentiGaraManager(CreazioneArchivioDocumentiGaraManager creazioneArchivioDocumentiGaraManager) {
    this.creazioneArchivioDocumentiGaraManager = creazioneArchivioDocumentiGaraManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("InviaRichiestaExportDocumentiAction: inizio metodo");

    String target = FORWARD_ERROR;

    String codgar = request.getParameter("codgar");
    String codice = request.getParameter("codice");
    String genere = request.getParameter("genere");
    String oggetto = request.getParameter("oggetto");
    String idstipula = request.getParameter("idstipula");
    String entita = request.getParameter("entita");

    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long syscon = new Long(profiloUtente.getId());

    Long id = null;
    int livEvento = 1;
    String errMsg ="";
    try{
      String chiave= codgar;
      if(idstipula!=null && !"".equals(idstipula)) {
        chiave=idstipula;
      }
      id =  creazioneArchivioDocumentiGaraManager.insertJob(syscon, chiave, entita);
      target = FORWARD_SUCCESS;
      request.setAttribute("soggetto", oggetto + " " + codgar);
    }catch(Exception e){
      livEvento = 3;
      errMsg = e.getMessage();
      this.aggiungiMessaggio(request, "errors.generico", "Errore durante l'inserimento del job di esportazione documenti per la gara " + codgar);
    }finally{
      String descr ="Richiesta archiviazione su file zip dei documenti di gara ";
      if(id!=null)
        descr += "(id.richiesta: " + id.toString() +")";
      String setOggEvento = codice;
      if("1".equals(genere) || "3".equals(genere))
        setOggEvento = codgar;
      LogEvento logevento = LogEventiUtils.createLogEvento(request);
      logevento.setLivEvento(livEvento);
      logevento.setOggEvento(setOggEvento);
      logevento.setCodEvento("GA_ARCHIVIA_DOCUMENTI_ZIP");
      logevento.setDescr(descr);
      logevento.setErrmsg(errMsg);
      LogEventiUtils.insertLogEventi(logevento);
    }



    if (logger.isDebugEnabled()) logger.debug("InviaRichiestaExportDocumentiAction: fine metodo");

    return mapping.findForward(target);

  }

}
