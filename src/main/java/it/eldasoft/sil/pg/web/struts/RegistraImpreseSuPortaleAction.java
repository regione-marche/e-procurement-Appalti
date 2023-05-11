/*
 * Created on 29/04/13
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
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.system.ConfigurazioneMail;
import it.eldasoft.gene.tags.bl.RegImpresaPortaleManager;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.sicurezza.CriptazioneException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per la registrazione di tutte le imprese iscritte ad un elenco e non
 * registrate su portale
 *
 * @author Marcello Caminiti
 */
public class RegistraImpreseSuPortaleAction extends ActionBaseNoOpzioni {

  protected static final String    FORWARD_SUCCESS           = "successRegistrazione";
  protected static final String    FORWARD_ERROR             = "errorRegistrazione";

  static Logger logger = Logger.getLogger(RegistraImpreseSuPortaleAction.class);

	private RegImpresaPortaleManager registraImpreseSuPortaleManager = null;
	private MailManager mailManager;

	private SqlManager sqlManager = null;

	public void setRegistraImpreseSuPortaleManager(
	    RegImpresaPortaleManager registraImpreseSuPortaleManager) {
		this.registraImpreseSuPortaleManager = registraImpreseSuPortaleManager;
	}

  /**
   * @param mailManager mailManager da settare internamente alla classe.
   */
  public void setMailManager(MailManager mailManager) {
    this.mailManager = mailManager;
  }

  public void setSqlManager(
	    SqlManager sqlManager) {
        this.sqlManager = sqlManager;
    }


	@Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
	if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

	String target = FORWARD_SUCCESS;
	String messageKey = null;

	// Lettura ngara in cui importare l'offerta prezzi
	String elenco = null;
	if(request.getParameter("ngara") != null)
	  elenco = request.getParameter("ngara");
	else
	  elenco = (String) request.getAttribute("ngara");

	String select = "select dittao,nomimo from ditg,impr where ngara5=? and codgar5=? and dittao=codimp and (tipimp is null or (tipimp is not null and tipimp<>3 and tipimp<>10)) " +
			"and dittao not in (select userkey1 from w_puser where userent='IMPR' and userkey1=dittao)";

  	List listaDitte = null;
  	try {
  	  listaDitte = sqlManager.getListVector(select,
  	         new Object[] { elenco, "$"+elenco});

  	} catch (SQLException e) {
  	    target = FORWARD_ERROR;
  	    messageKey = "error.registraImpreseSuPortale";
  	    logger.error("Errore nella lettura delle ditte non registrate dell'elenco: " + elenco, e);
  	    this.aggiungiMessaggio(request, messageKey);
  	}

  	if (listaDitte != null && listaDitte.size() > 0) {
  	  String codiceDitta=null;
  	  String ragSociale = null;
  	  String chiaveMsg =null;
  	  String email=null;
  	  String pec=null;
  	  String codfisc=null;
  	  String piva=null;

      int contatoreRegistrazioniOk =0;
      int contatoreRegistrazioniNok =0;
      HashMap  listaErroriImpreseNonRegistrate = new HashMap();
      ArrayList<String> listaImpreseRegistrate = new ArrayList<String>();

      String codapp = (String) request.getSession().getAttribute(
          CostantiGenerali.MODULO_ATTIVO);
      ConfigurazioneMail cfg;
      try {
        cfg = this.mailManager.getConfigurazione(codapp, CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD);
      } catch (CriptazioneException e) {
        logger.error("Errore durante l'estrazione della configurazione standard di posta per la lettura del delay, non si introduce pertanto alcun delay", e);
        cfg = new ConfigurazioneMail();
      }


      for (int i = 0; i < listaDitte.size(); i++) {
        codiceDitta=SqlManager.getValueFromVectorParam(listaDitte.get(i), 0).getStringValue();
  	    ragSociale = SqlManager.getValueFromVectorParam(listaDitte.get(i), 1).getStringValue();
  	    if(ragSociale==null)
  	      ragSociale="";
  	    chiaveMsg = codiceDitta + "-" + ragSociale;
  	    try {
  	      Vector datiImpresa = sqlManager.getVector("select emai2ip, emaiip, cfimp, pivimp from impr where codimp=?", new Object[]{codiceDitta});
  	      if(datiImpresa!=null && datiImpresa.size()>0){
  	        pec= SqlManager.getValueFromVectorParam(datiImpresa, 0).getStringValue();
  	        email = SqlManager.getValueFromVectorParam(datiImpresa, 1).getStringValue();
  	        codfisc = SqlManager.getValueFromVectorParam(datiImpresa, 2).getStringValue();
  	        piva = SqlManager.getValueFromVectorParam(datiImpresa, 3).getStringValue();

  	        String msg=null;
            try {
              msg = registraImpreseSuPortaleManager.insertImpresaSulPortale(codiceDitta, codiceDitta, false,ragSociale,codfisc,piva,email,pec);
              if(msg!=null && !"".equals(msg)){
                listaErroriImpreseNonRegistrate.put(chiaveMsg,msg);
                contatoreRegistrazioniNok++;
              }else{
                listaImpreseRegistrate.add(chiaveMsg);
                contatoreRegistrazioniOk++;
              }
              // 20171220: si introduce un eventuale delay di attesa per risolvere gli interfacciamenti con provider di posta che
              // bloccano attivita massive mediante antispam (il provider di posta in questo caso viene chiamato dal servizio interrogato
              // mediante la chiamata a insertImpresaSulPortale)
              if (cfg.getDelay() != null) {
                try {
                  TimeUnit.MILLISECONDS.sleep(Integer.parseInt(cfg.getDelay()));
                } catch (InterruptedException e) {
                  logger.error("Errore durante l'attesa tra una registrazione massiva sul portale di una impresa in elenco e la successiva", e);
                }
              }
            } catch (GestoreException e) {
              listaErroriImpreseNonRegistrate.put(chiaveMsg,e.getMessage());
              contatoreRegistrazioniNok++;
            }

  	      }else{
  	        //GESTIRE ERRORE: NON CI SONO DATI DI IMPR!!!
  	        listaErroriImpreseNonRegistrate.put(chiaveMsg,"Non sono stati trovati i dati dell'anagrafica dell'impresa ");
  	        contatoreRegistrazioniNok++;
  	        logger.error("Non sono stati trovati i dati dell'anagrafica dell'impresa " + codiceDitta );
  	      }
  	    } catch (SQLException e) {
  	      listaErroriImpreseNonRegistrate.put(chiaveMsg,e.getMessage());
  	      contatoreRegistrazioniNok++;
  	      logger.error("Errore nella lettura dei dati dell'impresa: " + codiceDitta + elenco, e);
        }


      }
  	  request.setAttribute("numImpreseRegistrate", new Long(contatoreRegistrazioniOk));
  	  request.setAttribute("numImpreseNonRegistrate", new Long(contatoreRegistrazioniNok));
  	  request.setAttribute("listaImpreseRegistrate", listaImpreseRegistrate);
  	  request.setAttribute("listaMessaggiImpreseNonRegistrate", listaErroriImpreseNonRegistrate);
  	}else{
  	  logger.error("Non vi sono ditte da registrare per l'elenco" + elenco);
  	  target = FORWARD_ERROR;
      messageKey = "error.registraImpreseSuPortale.listaVuota";
      this.aggiungiMessaggio(request, messageKey);
    }

	if(logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
	return mapping.findForward(target);
  }
}