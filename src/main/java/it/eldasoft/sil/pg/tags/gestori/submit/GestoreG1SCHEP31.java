package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

public class GestoreG1SCHEP31 extends AbstractGestoreEntita {

	private GenChiaviManager genChiaviManager;
	private SqlManager sqlManager;

	static Logger logger = Logger.getLogger(GestoreG1SCHEP31.class);
	
	public GestoreG1SCHEP31() {
	    super(false);
	  }
	
	@Override
	  public String getEntita() {
	    return "G1SCHEP31";
	  }

	@Override
	public void setRequest(HttpServletRequest request) {
		super.setRequest(request);
		sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
	        this.getServletContext(), SqlManager.class);
		genChiaviManager=(GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
	        this.getServletContext(), GenChiaviManager.class);
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
	public void preDelete(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
	}

	@Override
	public void preInsert(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
	}

	@Override
	public void preUpdate(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

		if (logger.isDebugEnabled()) {
			logger.debug("GestoreG1SCHEP31.preUpdate: inizio metodo");
		}

		Long idG1SCHEPR31 = (Long) impl.getColumn("G1SCHEP31.ID").getOriginalValue().getValue();
		impl.getColumn("G1SCHEP31.ID").setChiave(true);
		try {
    		if(idG1SCHEPR31==null) {//se non esiste una voce, creo i dati (una tantum)
    		  idG1SCHEPR31 =  Long.valueOf(genChiaviManager.getMaxId("G1SCHEP31", "ID"))+1;
    		  impl.setValue("G1SCHEP31.ID", idG1SCHEPR31);
    		  String codgar= impl.getColumn("TORN.CODGAR").getValue().getStringValue();
    		  impl.setValue("G1SCHEP31.CODGAR", codgar);
    		  if(impl.isColumn("GARE.NGARA")) {//se sono nel dettaglio di un lotto, valorizzo ngara
    		    String ngara= impl.getColumn("GARE.NGARA").getOriginalValue().getStringValue();
      		    impl.setValue("G1SCHEP31.NGARA", ngara);
    		  }
  		 
              impl.insert("G1SCHEP31", sqlManager);
    		}
    		else {
    		  impl.update("G1SCHEP31", sqlManager);
    		}
        } catch (SQLException e) {
          throw new GestoreException("Errore nel salvataggio delle informazioni per la tabella G1SCHEP31",null,e);
        }
		if (logger.isDebugEnabled()) {
			logger.debug("GestoreG1SCHEP31.preUpdate: fine metodo");
		}
	}
}
