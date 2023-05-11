/*
 * Created on 29/08/18
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ImportExportOffertaPrezziManager;
import it.eldasoft.sil.pg.bl.LoggerImportOffertaPrezzi;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per l'import della lista delle variazioni prezzi
 *
 * @author Marcello Caminiti
 */
public class EseguiImportVariazionePrezziAction extends ActionBaseNoOpzioni {

	static Logger logger = Logger.getLogger(EseguiImportVariazionePrezziAction.class);

	private ImportExportOffertaPrezziManager importExportOffertaPrezziManager = null;

	private SqlManager sqlManager;

	/**
	 * @param importExportOffertaPrezziManager importExportOffertaPrezziManager
	 * da settare internamente alla classe.
	 */
	public void setImportExportOffertaPrezziManager(
			ImportExportOffertaPrezziManager importExportOffertaPrezziManager) {
		this.importExportOffertaPrezziManager = importExportOffertaPrezziManager;
	}

	public void setSqlManager(SqlManager sqlManager){
		this.sqlManager = sqlManager;
	}

	@Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

	if(logger.isDebugEnabled()) logger.debug("runAcion: inizio metodo");

	String target = CostantiGeneraliStruts.FORWARD_OK;
	String messageKey = null;
	String risultato = "OK";

	//variabili per tracciatura eventi
	String genericMsgErr = "Errore inaspettato durante la tracciatura su w_logeventi";
	int livEvento = 1;
	String errMsgEvento = "";
	String descrEv = "Import da excel per variazione prezzi";

    // Lettura ngara in cui importare l'offerta prezzi
	String ngara = null;
	if(request.getParameter("ngara") != null)
		ngara = request.getParameter("ngara");
	else
		ngara = (String) request.getAttribute("ngara");


	String codiceDitta = null;
    if(request.getParameter("codiceDitta") != null && request.getParameter("codiceDitta").length() > 0)
        codiceDitta = request.getParameter("codiceDitta");
    else if(request.getAttribute("codiceDitta") != null && ((String) request.getAttribute("codiceDitta")).length() > 0)
        codiceDitta = (String) request.getAttribute("codiceDitta");

    if(codiceDitta!=null && !"".equals(codiceDitta))
      descrEv+=" offerti dalla ditta " + codiceDitta;
    else
      descrEv+=" a base di gara";

    String tmp = null;
    // Lettura del flag per indicare che la gara e' a lotti con offerta unica
    boolean isGaraLottiConOffertaUnica = false;
    if(request.getParameter("garaLottiConOffertaUnica") != null)
        tmp = request.getParameter("garaLottiConOffertaUnica");
    else if(request.getAttribute("garaLottiConOffertaUnica") != null)
        tmp = (String) request.getAttribute("garaLottiConOffertaUnica");
    if("1".equals(tmp))
        isGaraLottiConOffertaUnica = true;

	try{

  		UploadFileForm fileExcel = (UploadFileForm) form;

  		if(fileExcel != null && fileExcel.getSelezioneFile().getFileSize() > 0){

  				LoggerImportOffertaPrezzi loggerImport =
  					this.importExportOffertaPrezziManager.importVariazionePrezzi(
  						(UploadFileForm) form, ngara, codiceDitta,
  						isGaraLottiConOffertaUnica,request.getSession());

  	    	if(loggerImport.getListaMsgVerificaFoglio().size() > 0)
  	    		risultato = "KO";
  	    	else
  	    		risultato = "OK";

  	    	if("OK".equals(risultato) && codiceDitta != null){
  	    		String nomeImpresa = (String) this.sqlManager.getObject(
  	  					"select NOMIMP from IMPR where CODIMP = ? ", new Object[]{codiceDitta});

  	  			if(nomeImpresa != null && nomeImpresa.length() > 0)
  	  				request.setAttribute("nomeImpresa", nomeImpresa);
  	    	}

  	    	if("OK".equals(risultato)){
  	    	  descrEv+="\nNumero righe lette dal file Excel:"+ loggerImport.getNumeroRigheLette()+"\n";
  	    	  descrEv+="\nNumero righe aggiornate:"+ loggerImport.getNumeroRecordAggiornati()+"\n";
  	    	  descrEv+="\nNumero righe non aggiornate:"+ loggerImport.getNumeroRecordNonAggiornati()+"\n";
  	    	  if(codiceDitta != null && loggerImport.getNumeroRecordNonAggiornatiLottiNonAggiudicati()>0)
  	    	    descrEv+="\n>Numero righe scartate perchè lotti non aggiudicati dalla ditta:"+ loggerImport.getNumeroRecordNonAggiornatiLottiNonAggiudicati()+"\n";

  	    	}

  	    	request.setAttribute("loggerImport", loggerImport);
  	    	request.setAttribute("RISULTATO", risultato);


  		}



	} catch (IOException io) {
	  livEvento = 3;
	  messageKey = "errors.importOffertaPrezzi.erroreIO";
      logger.error(this.resBundleGenerale.getString(messageKey), io);
      this.aggiungiMessaggio(request, messageKey);
      errMsgEvento = this.resBundleGenerale.getString(messageKey) +":" + io.getMessage();
	} catch (GestoreException g) {
	  livEvento = 3;
	  if(g.getCause() == null)
          messageKey = "errors.importOffertaPrezzi.verifichePreliminari";
      else
          messageKey = "errors.importOffertaPrezzi.erroreLetturaDatiEstratti.variazionePrezzi";
      this.aggiungiMessaggio(request, messageKey);
      logger.error(this.resBundleGenerale.getString(messageKey), g);
      errMsgEvento = this.resBundleGenerale.getString(messageKey) +":" + g.getMessage();
    } catch (Throwable t) {
      livEvento = 3;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
      errMsgEvento = this.resBundleGenerale.getString(messageKey) +":" + t.getMessage();
    }finally{
        // A prescindere dall'esito dell'importazione, si ripristina
        // i valori letti dal request all'inizio della Action
        request.setAttribute("ngara", ngara);
        if(codiceDitta != null)
            request.setAttribute("codiceDitta", codiceDitta);

        request.setAttribute("garaLottiConOffertaUnica",
                isGaraLottiConOffertaUnica ? "1" : "2");

        if(messageKey != null){
          // Si e' verificato un errore
          request.setAttribute("RISULTATO", "KO");
          target = "errorImport";
        }

        //Tracciatura eventi
        try {
          LogEvento logEvento = LogEventiUtils.createLogEvento(request);
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(ngara);
          logEvento.setCodEvento("GA_VARIAZIONE_PREZZI");
          logEvento.setDescr(descrEv);
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          logger.error(genericMsgErr, le);
        }
        if(logger.isDebugEnabled()) logger.debug("runAcion: fine metodo");
        return mapping.findForward(target);
  	}
  }
}