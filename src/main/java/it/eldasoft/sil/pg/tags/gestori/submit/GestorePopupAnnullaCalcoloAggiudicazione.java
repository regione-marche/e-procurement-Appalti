/*
 * Created on 3/nov/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.utils.spring.UtilitySpring;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore della popup di annullamento del calcolo di aggiudicazione
 *
 * @author Marcello Caminiti
 */

public class GestorePopupAnnullaCalcoloAggiudicazione extends
		AbstractGestoreEntita {

  /** Logger */
  static Logger logger = Logger.getLogger(GestorePopupAnnullaCalcoloAggiudicazione.class);


	@Override
  public String getEntita() {
		return "GARE";
	}

  public GestorePopupAnnullaCalcoloAggiudicazione() {
    super(false);
  }

	@Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {
	}

	@Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {
	}

	@Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {

	}

	@Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
			throws GestoreException {
	}

	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
			throws GestoreException {
	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
			throws GestoreException {

		AggiudicazioneManager aggiudicazioneManager = (AggiudicazioneManager)
			UtilitySpring.getBean("aggiudicazioneManager", this.getServletContext(),
						AggiudicazioneManager.class);

		String oggEvento = "";

		boolean isGaraLottiConOffertaUnica = false;
		String tmp = this.getRequest().getParameter("isGaraLottiConOffertaUnica");
		if(tmp == null)
			tmp = (String) this.getRequest().getAttribute("isGaraLottiConOffertaUnica");
		if(tmp != null)
			isGaraLottiConOffertaUnica = true;

		tmp = this.getRequest().getParameter("bustalotti");
        if(tmp == null)
            tmp = (String) this.getRequest().getAttribute("bustalotti");
		Long bustalotti=null;
		if(tmp!=null && !"".equals(tmp))
		  bustalotti = new Long(tmp);

		String esisteGestioneOffEco = this.getRequest().getParameter("esisteGestioneOffEco");

	    //variabili per tracciatura eventi
	    String messageKey = null;
	    int livEvento = 3;
	    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");


	    try{
	        try {
	            if(isGaraLottiConOffertaUnica){
	                String codiceGara = impl.getString("CODGAR");
	                oggEvento = codiceGara;
	                aggiudicazioneManager.annullaCalcoloAggiudicazione(codiceGara, null, true,bustalotti,"true".equals(esisteGestioneOffEco));
	                this.getRequest().setAttribute("CODGAR", codiceGara);
	                //String bustalotti = this.getRequest().getParameter("bustalotti");
	                this.getRequest().setAttribute("bustalotti", tmp);
	            } else {
	                String ngara = impl.getString("NGARA");
	                oggEvento = ngara;
	                String codiceGara = impl.getString("CODGAR1");
	                aggiudicazioneManager.annullaCalcoloAggiudicazione(codiceGara, ngara,
	                        false,bustalotti, "true".equals(esisteGestioneOffEco));
	                this.getRequest().setAttribute("NGARA", ngara);
	            }
	            //best case
	            livEvento = 1;
	            errMsgEvento = "";
	            this.getRequest().setAttribute("RISULTATO", "CALCOLOESEGUITO");
	        } catch (GestoreException e) {
	            this.getRequest().setAttribute("RISULTATO", "ERRORI");
                livEvento = 3;
                errMsgEvento = e.getMessage();
	            throw e;
	        } catch (Throwable e) {
	            this.getRequest().setAttribute("RISULTATO", "ERRORI");
	            livEvento = 3;
	            messageKey = "errors.gestoreException.*.annullaCalcoloAggiudicazione";
	            errMsgEvento = this.resBundleGenerale.getString(messageKey);
	            throw new GestoreException(
	                    "Errore durante l'annullamento del calcolo aggiudicazione",
	                    "annullaCalcoloAggiudicazione", e);
	        }

	    }finally{
	        //Tracciatura eventi
	        try {
	          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
	          logEvento.setLivEvento(livEvento);
	          logEvento.setOggEvento(oggEvento);
	          logEvento.setCodEvento("GA_ANNULLA_AGG_PROV");
	          logEvento.setDescr("Annullamento calcolo proposta di aggiudicazione");
	          logEvento.setErrmsg(errMsgEvento);
	          LogEventiUtils.insertLogEventi(logEvento);
	        } catch (Exception le) {
	          messageKey = "errors.logEventi.inaspettataException";
	          logger.error(this.resBundleGenerale.getString(messageKey), le);
	        }
	    }


	}

}