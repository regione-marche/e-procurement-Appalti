/*
 * Created on 04/07/16
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
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per la pagina gare-popup-attivaAperturaDomandePartecipazione.jsp
 *
 * @author Marcello Caminiti
 */
public class GestorePopupAperturaDomandePart extends AbstractGestoreEntita {

  /** Logger */
  static Logger logger = Logger.getLogger(GestorePopupAperturaDomandePart.class);

	@Override
  public String getEntita() {
		return "GARE";
	}

  public GestorePopupAperturaDomandePart() {
	    super(false);
	  }

	/**
	 * @param isGestoreStandard
	*/
  public GestorePopupAperturaDomandePart(boolean isGestoreStandard) {
	    super(isGestoreStandard);
	}

	@Override
  public void postDelete(DataColumnContainer datiForm)
			throws GestoreException {

	}

	@Override
  public void postInsert(DataColumnContainer datiForm)
			throws GestoreException {

	}

	@Override
  public void postUpdate(DataColumnContainer datiForm)
			throws GestoreException {

	}

	@Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	}

	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	  String  ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
	  MEPAManager mepaManager = (MEPAManager) UtilitySpring.getBean("mepaManager",
	        this.getServletContext(), MEPAManager.class);

      //variabili per tracciatura eventi
      String messageKey = null;
      int livEvento = 3;
      String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
      try{
        try{
          this.sqlManager.update("update gare set fasgar=?, stepgar=? where ngara=?", new Object[]{new Long(-4), new Long(-40),ngara});
          mepaManager.impostaComunicazioniAScartate(ngara,"DOMANDE");
          livEvento = 1;
          errMsgEvento = "";

        }catch(SQLException e) {
          livEvento = 3;
          errMsgEvento = "Errore durante l'operazione di attivazione dell'apertura domande di partecipazione";
          this.getRequest().setAttribute("operazioneEseguita", "ERRORI");
          throw new GestoreException(
                  "Errore durante l'operazione di attivazione dell'apertura domande di partecipazione)",
                 null, e);
        }

      }finally{
        //Tracciatura eventi
        try {
          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(ngara);
          logEvento.setCodEvento("GA_ATTIVA_FASE_DOMANDE");
          logEvento.setDescr("Attivazione fase apertura domande di partecipazione ");
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          messageKey = "errors.logEventi.inaspettataException";
          logger.error(this.resBundleGenerale.getString(messageKey), le);
        }
      }


      this.getRequest().setAttribute("operazioneEseguita", "OK");

	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	}
}
