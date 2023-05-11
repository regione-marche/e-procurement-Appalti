package it.eldasoft.sil.w3.tags.gestori.submit;


import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityNumeri;

public class GestoreW3DELEGHE extends AbstractGestoreEntita {

  public String getEntita() {
    return "W9DELEGHE";
  }
  
  public GestoreW3DELEGHE() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestoreW3DELEGHE(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    
    try {
      datiForm.delete(getEntita(), sqlManager);
    } catch (SQLException e) {
      throw new GestoreException("Errore nella cancellazione dei dati in W9DELEGHE",null, e);
    }
  }

  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    
    try {
      Long id = (Long) this.sqlManager.getObject("select max(id) from W9DELEGHE ", new Object[] {});
      if(id==null)
        id=0L;
      datiForm.addColumn(getEntita()+".ID", new Long(id+1));
      
      datiForm.insert(getEntita(), sqlManager);
      
    } catch (SQLException e) {
      throw new GestoreException("Errore nell'inserimento dei dati in W9DELEGHE",null, e);
    }
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    
    int numeroCollaboratori = 0;
    String numCollaboratori = this.getRequest().getParameter("numeroCollaboratori");
    if (numCollaboratori != null && numCollaboratori.length() > 0) {
      numeroCollaboratori =  UtilityNumeri.convertiIntero(numCollaboratori).intValue();
    }
    else {
      try {//caso modifica dalla scheda
        datiForm.update("W9DELEGHE", sqlManager);
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'aggiornamento dei dati in W9DELEGHE",null, e);
      }
    }

    Long ruolo = null;
    //caso modifica dalla lista
    for (int i = 1; i <= numeroCollaboratori; i++) {
      DataColumnContainer dataColumnContainerDiRiga = new DataColumnContainer(
          datiForm.getColumnsBySuffix("_" + i, false));

      try {
        if (dataColumnContainerDiRiga.isModifiedTable("W9DELEGHE")) {
            ruolo = dataColumnContainerDiRiga.getLong("W9DELEGHE.RUOLO");
            
            dataColumnContainerDiRiga.setValue("W9DELEGHE.RUOLO", ruolo);
            
            dataColumnContainerDiRiga.update("W9DELEGHE", sqlManager);
        }
      } catch (SQLException e) {
           throw new GestoreException("Errore nell'aggiornamento dei dati in W9DELEGHE",null, e);
      }
    }

  }

}
