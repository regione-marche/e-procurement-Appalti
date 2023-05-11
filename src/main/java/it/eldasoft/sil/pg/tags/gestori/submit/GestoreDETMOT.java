        /*
 * Created on: 30-mar-2016
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;


import org.springframework.transaction.TransactionStatus;


import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

/**
 * Gestore di submit dell'entita' DETMOT
 * 
 * @author Francesco.DiMattei
 */
public class GestoreDETMOT extends AbstractGestoreEntita {

  public String getEntita() {
    return "TAB1";
  }

  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  public void postDelete(DataColumnContainer datiForm) 
      throws GestoreException {
  }

  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  public void postInsert(DataColumnContainer datiForm) 
      throws GestoreException {
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    //Al salvataggio gestire l'inserimento, la modifica o la cancellazione (nel caso il campo venga sbiancato) dell'occ. in DETMOT corrispondente al valore tab. corrente.
    if (datiForm.getColumn("DETMOT.ANNOFF").isModified()) {
      Long tab1tip = datiForm.getLong("TAB1.TAB1TIP");
      String annoff = datiForm.getColumn("DETMOT.ANNOFF").getValue().getStringValue();
      Object parametri[] = null;
      String sql = "";
      String msgsqlerr = "";
      boolean occPresente = false;
      /////////////////////
      try {
        String selCountDetmot = "SELECT COUNT(MOTIES) FROM DETMOT WHERE MOTIES=?";
        Long occorrenza = (Long) this.sqlManager.getObject(selCountDetmot, new Object[] {tab1tip});
        occPresente = (occorrenza > 0);
      } catch (SQLException e) {
        throw new GestoreException("Errore nel conteggio delle occorrenze su entità DETMOT", null,e);
      }
      /////////////////
      //cancellazione
      if (annoff == "" ) {
        sql = "delete from DETMOT where MOTIES=?";
        parametri = new Object[]{tab1tip};
        msgsqlerr = "nella cancellazione";
      }else{
        //////////////
        //inserimento
        if (!occPresente) {
          sql="insert into DETMOT( MOTIES, ANNOFF) values (?, ?)";
          parametri = new Object[]{tab1tip,annoff};
          msgsqlerr = "nell'inserimento";
        }else{
          ////////////
          //modifica
          sql="update DETMOT set ANNOFF = ? where MOTIES=?";
          parametri = new Object[]{annoff,tab1tip};
          msgsqlerr = "nell'aggiornamento";
        }
      }
      //eseguo SQL di inserimento, modifica o cancellazione su DETMOT
      try {
        this.sqlManager.update(sql, parametri);
      } catch (SQLException e) {
        throw new GestoreException("Errore "+ msgsqlerr +" del motivo esclusione gara", null,e);
      }
    }
    //cancellazione delle eventuali righe in DETMOT che fanno riferimento a valori del tab. non più presenti in TAB1
    try {
      String deleteDetmotNonInTab1 = "delete from detmot where moties not in(SELECT tab1tip FROM tab1 WHERE tab1cod='A2054' and (tab1tip<>98 and tab1tip<>99 and tab1tip<>100 and tab1tip<>101))";
      this.sqlManager.update(deleteDetmotNonInTab1, null);
    } catch (SQLException e) {
      throw new GestoreException("Errore nella cancellazione delle righe in DETMOT non più presenti in TAB1", null,e);
    }
  }

  public void postUpdate(DataColumnContainer datiForm) 
      throws GestoreException {
  }

}