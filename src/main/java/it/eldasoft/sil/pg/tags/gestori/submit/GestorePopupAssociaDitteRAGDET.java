/*
 * Created on 12/07/10
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

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import org.springframework.transaction.TransactionStatus;

public class GestorePopupAssociaDitteRAGDET extends
    AbstractGestoreEntita {

  public String getEntita() {
    return "RAGIMP";
  }
  
  public GestorePopupAssociaDitteRAGDET() {
    super(false);
  }

  
  public GestorePopupAssociaDitteRAGDET(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }
  
  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  public void postUpdate(DataColumnContainer impl) throws GestoreException {

  }

  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
    
    String[] listaDitteSelezionate = this.getRequest().getParameterValues("keys");
    
    Long maxId = null;
    String codimp=null; 
    String coddic=null;
    String ngara=null;
    
    String insertRAGDET = "insert into ragdet (codimp, coddic, numdic, ngara)"
      + " values (?,?,?,?)";
    
    for (int i = 0; i < listaDitteSelezionate.length; i++) {
      String[] valoriDittaSelezionata = listaDitteSelezionate[i].split(";");
      if (valoriDittaSelezionata.length == 3) {
        codimp= valoriDittaSelezionata[0];
        coddic= valoriDittaSelezionata[1];
        ngara=valoriDittaSelezionata[2];
        
        if(codimp!= null && coddic!=null && ngara!=null){
          try {
            maxId = (Long) this.sqlManager.getObject(
                "select max(numdic) from ragdet where codimp = ? and coddic = ?",
                new Object[] { codimp, coddic });
            
            if (maxId == null) maxId = new Long(0);
            maxId = new Long(maxId.longValue() + 1);

            this.sqlManager.update(insertRAGDET, new Object[] { codimp,
                coddic, maxId, ngara });

            this.getRequest().setAttribute("RISULTATO", "OK");
          } catch (SQLException e) {
            this.getRequest().setAttribute("RISULTATO", "ERRORI");
            throw new GestoreException(
                "Errore nell'aggiornamento della RAGDET con chiavi "
                + "CODIMP = " + codimp + ", CODDIC = " + coddic, null, e);
          }
        }
        
      }
    }
    
  }

}