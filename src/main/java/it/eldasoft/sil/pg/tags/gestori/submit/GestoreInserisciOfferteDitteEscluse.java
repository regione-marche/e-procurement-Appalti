/*
 * Created on 05/05/11
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
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per il salvataggio delle occorrenze di DITG
 * presenti nelle pagine inserimentoOfferteDitteEscluse-lista.jsp
 * e dettaglioOfferteDittaEsclusa-OffertaUnicaLotti.jsp
 * 
 * @author Marcello Caminiti
 */
public class GestoreInserisciOfferteDitteEscluse extends AbstractGestoreEntita {
  
  public String getEntita() {
    return "DITG";
  }

  public GestoreInserisciOfferteDitteEscluse() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestoreInserisciOfferteDitteEscluse(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }
  
    
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  	     
    int numeroDitte = 0;
    String numDitte = this.getRequest().getParameter("numeroDitte");
    if(numDitte != null && numDitte.length() > 0)
      numeroDitte =  UtilityNumeri.convertiIntero(numDitte).intValue();
        

    for (int i = 1; i <= numeroDitte; i++) {
      DataColumnContainer dataColumnContainerDiRiga = new DataColumnContainer(
      		impl.getColumnsBySuffix("_" + i, false));
      
      boolean isDITGModificata = dataColumnContainerDiRiga.isModifiedTable("DITG");
      
      if(isDITGModificata ) {
        
        //Il gestore viene richiamato dalle pagine inserimentoOfferteDitteEscluse-lista.jsp 
        //e dettaglioOfferteDittaEsclusa-OffertaUnicaLotti.jsp
        //Nella seconda il campo NGARA5 non è modificabile ma visibile, quindi 
        //al salvataggio non è presente nel request. Per inserire il valore di tale campo nel
        // request e' stato aggiunto un campo fittizio con nome NGARA5fit_+ suffisso
        if(dataColumnContainerDiRiga.isColumn("DITG.NGARA5fit")){
          //Si deve inserire il campo NGARA5 e si deve eliminare NGARA5fit
          String codiceLotto = dataColumnContainerDiRiga.getString("DITG.NGARA5fit");
          dataColumnContainerDiRiga.addColumn("DITG.NGARA5", codiceLotto);
          dataColumnContainerDiRiga.getColumn("DITG.NGARA5").setChiave(true);
          dataColumnContainerDiRiga.getColumn("DITG.NGARA5").setObjectOriginalValue(codiceLotto);
          dataColumnContainerDiRiga.removeColumns(new String[]{"DITG.NGARA5fit"});
        }
        DataColumnContainer containerDITG = new DataColumnContainer(dataColumnContainerDiRiga);
        try {
          containerDITG.update("DITG", sqlManager);
        } catch (SQLException e) {
          throw new GestoreException("Errore nel salvataggio dei dati in DITG",
              null, e);
        }  	
      }
    }
      
  }

  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }
 
}