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
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.bl.tasks.CreazioneArchivioDocumentiGaraManager;

public class InviaRichiestaExportDocumentiAction extends ActionBaseNoOpzioni {

  private static final String UPDATE_ESPORTA_QUERY =
    "UPDATE gardoc_jobs SET esportalog = ?, esportaeml=? WHERE id_archiviazione=?";

  protected static final String    FORWARD_SUCCESS           = "inviarichiestaexportdocumentisuccess";
  protected static final String    FORWARD_ERROR             = "inviarichiestaexportdocumentierror";

  static Logger                    logger            = Logger.getLogger(InviaRichiestaExportDocumentiAction.class);

  private CreazioneArchivioDocumentiGaraManager  creazioneArchivioDocumentiGaraManager;
  private SqlManager sqlManager;

  public void setCreazioneArchivioDocumentiGaraManager(CreazioneArchivioDocumentiGaraManager creazioneArchivioDocumentiGaraManager) {
    this.creazioneArchivioDocumentiGaraManager = creazioneArchivioDocumentiGaraManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
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
    String includiXmlMetadati = request.getParameter("includiXmlMetadati");
    String esportalog = request.getParameter("esportaLog");
    String esportaEml = request.getParameter("esportaEml");

    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long syscon = new Long(profiloUtente.getId());

    Long id = null;
    int livEvento = 1;
    String errMsg ="";
    TransactionStatus transactionStatus = null;
    boolean updated = false;
    boolean block = false;
    try{
      String chiave= codgar;
      if(idstipula!=null && !"".equals(idstipula)) {
        chiave=idstipula;
      }
      Long esportaLog = NumberUtils.createLong(esportalog);
      if(new Long(1).equals(esportaLog)) {
        //verifico che l'export non sia stato forzato
        Long conteggio = (Long) sqlManager.getObject("select count(1) FROM v_gare_statoesito WHERE codgar = ? AND codstato = 3", new Object[]{codgar});
        if (("1".equals(genere) || "2".equals(genere) || "3".equals(genere)) && conteggio == 0) {
          livEvento = 3;
          errMsg = "Errore nell'inserimento della richiesta di export documenti. Non è possibile procedere all'export comprensivo dei log perchè la gara non è aggiudicata né annullata.";
          logger.error(errMsg);
          this.aggiungiMessaggio(request, "errors.generico", errMsg);
          block = true;
        }
      }

      transactionStatus = sqlManager.startTransaction();
      if(!block) {
        id = creazioneArchivioDocumentiGaraManager.insertJob(syscon, chiave, entita, includiXmlMetadati);
        target = FORWARD_SUCCESS;
        request.setAttribute("soggetto", oggetto + " " + codgar);
        sqlManager.update(UPDATE_ESPORTA_QUERY, new Object[]{esportaLog, StringUtils.stripToNull(esportaEml), id});
        updated = true;
      }
    }catch(Exception e){
      livEvento = 3;
      errMsg = e.getMessage();
      logger.error(e);
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

      try {
        if (updated) {
          sqlManager.commitTransaction(transactionStatus);
        } else {
          sqlManager.rollbackTransaction(transactionStatus);
        }
      } catch (SQLException e) {
        livEvento = 3;
        errMsg = e.getMessage();
        logger.error(e);
        this.aggiungiMessaggio(request, "errors.generico", "Errore durante l'inserimento del job di esportazione documenti per la gara " + codgar);
      }
    }



    if (logger.isDebugEnabled()) logger.debug("InviaRichiestaExportDocumentiAction: fine metodo");

    return mapping.findForward(target);

  }

}
