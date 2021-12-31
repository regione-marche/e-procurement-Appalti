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

import java.sql.SQLException;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore della popup di annullamento dell'aggiudicazione definitiva
 *
 * @author Diego Pavan
 */

public class GestorePopupReinviaComunicazioni extends
		AbstractGestoreEntita {

  /** Logger */
  static Logger logger = Logger.getLogger(GestorePopupAnnullaAggiudicazioneDefinitiva.class);


	@Override
  public String getEntita() {
		return "GARE";
	}

  public GestorePopupReinviaComunicazioni() {
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

		SqlManager sqlManager = (SqlManager)
			UtilitySpring.getBean("sqlManager", this.getServletContext(),
			    SqlManager.class);


	    //variabili per tracciatura eventi

	        try {
	          String ngara = impl.getString("NGARA");

              String update = "update w_invcom set comstato = 14 where comkey1 = ? and comstato = 15";
              sqlManager.update(update, new Object[]{ngara});

	          this.getRequest().setAttribute("RISULTATO", "ESEGUITO");
	        } catch (SQLException e) {
	            this.getRequest().setAttribute("RISULTATO", "ERRORI");
                throw new GestoreException("Errore nell'aggiornamento dello stato nella tabella w_invcom",null, e);
	        }

	}

}