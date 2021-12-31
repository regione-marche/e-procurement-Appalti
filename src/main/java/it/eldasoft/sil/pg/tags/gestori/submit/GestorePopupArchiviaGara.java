package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestorePopupArchiviaGara extends AbstractGestoreEntita {

	static Logger      logger     = Logger.getLogger(GestorePopupArchiviaGara.class);

	@Override
  public String getEntita() {
	    return "TORN";
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

		this.getRequest().setAttribute("RISULTATO", "OPERAZIONEESEGUITA");

	}

	@Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {


	}

	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {


	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

		this.getRequest().setAttribute("RISULTATO", "ERRORE");
		if(datiForm.isColumn("GAREAVVISI.ISARCHI")){
		  try {

	        datiForm.update("GAREAVVISI", sqlManager);
    	  } catch (SQLException e) {
    	        throw new GestoreException("Errore nell'update dell'occorrenza in GAREAVVISI",
    	                null, e);
    	  }
		}else if(datiForm.isColumn("GAREALBO.ISARCHI")){
		  try {

            datiForm.update("GAREALBO", sqlManager);
          } catch (SQLException e) {
                throw new GestoreException("Errore nell'update dell'occorrenza in GAREALBO",
                        null, e);
          }
		}
		else if(datiForm.isColumn("MERIC.ISARCHI")){
          try {

            datiForm.update("MERIC", sqlManager);
          } catch (SQLException e) {
                throw new GestoreException("Errore nell'update dell'occorrenza in MERIC",
                        null, e);
          }
        }

	}

}
