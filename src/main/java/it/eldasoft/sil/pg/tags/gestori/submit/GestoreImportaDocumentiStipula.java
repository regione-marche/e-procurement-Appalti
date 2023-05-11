/*
 * Created on 07/05/2021
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;
import java.util.List;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.StipuleManager;
import it.eldasoft.utils.spring.UtilitySpring;


public class GestoreImportaDocumentiStipula extends AbstractGestoreEntita{

  @Override
  public String getEntita() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void postDelete(DataColumnContainer arg0) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void postInsert(DataColumnContainer arg0) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void postUpdate(DataColumnContainer arg0) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void preDelete(TransactionStatus arg0, DataColumnContainer arg1) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void preInsert(TransactionStatus arg0, DataColumnContainer arg1) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void preUpdate(TransactionStatus arg0, DataColumnContainer arg1) throws GestoreException {
    
    StipuleManager stipuleManager = (StipuleManager) UtilitySpring.getBean("stipuleManager",
        this.getServletContext(), StipuleManager.class);
    
    String  idStipulaStr = UtilityStruts.getParametroString(this.getRequest(),"idStipula");
    Long idStipula= Long.valueOf(idStipulaStr);
    String modelloSelezionato = this.getRequest().getParameter("modello");
    modelloSelezionato = modelloSelezionato.substring(modelloSelezionato.indexOf(':') + 1);
    
    try {

      //Lista documenti da importare
      List listaDocumenti = sqlManager.getListVector(
          "select  a.NUMORD,a.FASE,a.VISIBILITA,a.DESCRIZIONE,a.ULTDESC,a.OBBLIGATORIO,a.IDPRG,a.IDDOCDG,a.MODFIRMA from G1DOCUMOD g,G1ARCDOCUMOD a "
            + "where a.IDDOCUMOD = g.ID and g.ID = ? ORDER BY a.NUMORD",new Object[] { modelloSelezionato });
  
        stipuleManager.copiaDocumentiPredefiniti(this.getRequest(),listaDocumenti, idStipula);

        this.getRequest().setAttribute("RISULTATO", "OK");
        
    }  catch (SQLException e) {
      this.getRequest().setAttribute("RISULTATO", "ERRORI");
      throw new GestoreException("Errore nell'importo dei criteri di valutazione", null, e);
    }
    
  }

}
