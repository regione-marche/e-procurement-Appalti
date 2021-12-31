/*
 * Created on 19/10/16
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
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore della popup di annullamento dell'aggiudicazione definitiva
 *
 * @author Marcello Caminiti
 */

public class GestorePopupAnnullaAggiudicazioneDefinitiva extends
		AbstractGestoreEntita {

  /** Logger */
  static Logger logger = Logger.getLogger(GestorePopupAnnullaAggiudicazioneDefinitiva.class);


	@Override
  public String getEntita() {
		return "GARE";
	}

  public GestorePopupAnnullaAggiudicazioneDefinitiva() {
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
		 PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
		     this.getServletContext(), PgManagerEst1.class);

		String oggEvento = "";

		boolean isGaraLottiConOffertaUnica = false;
        String tmp = this.getRequest().getParameter("isGaraLottiConOffertaUnica");
        if(tmp == null || "".equals(tmp))
            tmp = (String) this.getRequest().getAttribute("isGaraLottiConOffertaUnica");
        if(tmp != null && !"".equals(tmp))
            isGaraLottiConOffertaUnica = true;

	    //variabili per tracciatura eventi
	    String messageKey = null;
	    int livEvento = 3;
	    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");

	    try{
	        try {
	          String ngara = impl.getString("NGARA");
              oggEvento = ngara;
              String codiceGara = impl.getString("CODGAR1");

              aggiudicazioneManager.annullaAggiudicazioneDefinitiva(codiceGara, ngara,isGaraLottiConOffertaUnica);

              //Aggiornamento della data ultimo aggiornamento

              String pubblicazioneBandoPortale = pgManagerEst1.esisteBloccoPubblicazionePortale(codiceGara, "BANDO", false);
              String pubblicazioneEsitoPortale = pgManagerEst1.esisteBloccoPubblicazionePortale(codiceGara, "ESITO", true);
              if("TRUE".equals(pubblicazioneBandoPortale) || "TRUE".equals(pubblicazioneEsitoPortale)){
                java.util.Date oggi = UtilityDate.getDataOdiernaAsDate();
                this.sqlManager.update("update TORN set DULTAGG = ? where CODGAR=?",new Object[]{oggi,codiceGara});
              }

              //this.getRequest().setAttribute("NGARA", ngara);
	          //best case
	          livEvento = 1;
	          errMsgEvento = "";
	          this.getRequest().setAttribute("RISULTATO", "CALCOLOESEGUITO");
              this.getRequest().setAttribute("NGARA", ngara);
	        } catch (GestoreException e) {
	            this.getRequest().setAttribute("RISULTATO", "ERRORI");
                livEvento = 3;
                errMsgEvento = e.getMessage();
	            throw e;
	        } catch (Throwable e) {
	            this.getRequest().setAttribute("RISULTATO", "ERRORI");
	            livEvento = 3;
	            messageKey = "errors.gestoreException.*.annullaAggiudicazioneDefinitiva";
	            errMsgEvento = this.resBundleGenerale.getString(messageKey);
	            throw new GestoreException(
	                    "Errore durante l'annullamento dell'aggiudicazione definitiva",
	                    "annullaAggiudicazioneDefinitiva", e);
	        }

	    }finally{
	        //Tracciatura eventi
	        try {
	          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
	          logEvento.setLivEvento(livEvento);
	          logEvento.setOggEvento(oggEvento);
	          logEvento.setCodEvento("GA_ANNULLA_AGG_DEF");
	          logEvento.setDescr("Annullamento aggiudicazione definitiva");
	          logEvento.setErrmsg(errMsgEvento);
	          LogEventiUtils.insertLogEventi(logEvento);
	        } catch (Exception le) {
	          messageKey = "errors.logEventi.inaspettataException";
	          logger.error(this.resBundleGenerale.getString(messageKey), le);
	        }
	    }


	}

}