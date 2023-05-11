/*
 * Created on 10/06/13
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
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;


/**
 * Gestore associato alla popup per la rinomina del codice della categoria
 *
 * @author Marcello Caminiti
 */
public class GestoreRinominaCategoria extends
    AbstractGestoreEntita {

  private final String entita[] = {"CAIS", "CATE","ARCHDOCG","OPES","CATG","ISCRIZCAT","ISCRIZCLASSI","CATAPP","ULTAPP","SUBA","CPRIGHE"};

  private final String campiEntita[] = {"CAISIM","CATISC","CATEGORIA","CATOFF","CATIGA","CODCAT","CODCAT","CATIGA","CATOFF","CATLAV","CATIGA"};

  public GestoreRinominaCategoria() {
    super(false);
  }


  @Override
  public String getEntita() {
    return "CAIS";
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    String codice=UtilityStruts.getParametroString(this.getRequest(),"codice");
    String nuovoCodice = datiForm.getString("NEWCODICE");
    //Si deve controllare che il nuovo codice non sia già adoperato
    try {
      Long count = (Long)this.sqlManager.getObject("select count(caisim) from cais where caisim=?", new Object[]{nuovoCodice});
      if(count!=null && count.longValue()>0){
        this.getRequest().setAttribute("RISULTATO", "NOK");
        Exception e = new Exception();
        throw new GestoreException(
            "Non e' possibile rinominare la categoria, esistono già categorie con codice " + nuovoCodice, "categorie.rinominaCodiceDuplicato",new Object[]{nuovoCodice},  e);
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella valutazione sull'esistenza di una categoria con codice " + nuovoCodice, null,  e);
    }

    //Rinomina dei campi CODLIV1, CODLIV2,CODLIV3,CODLIV4 della CAIS
    String indice="";
    try {
      for(int i=0;i<4;i++){
        indice = String.valueOf(i + 1);
        this.sqlManager.update("update CAIS set CODLIV" + indice + " = ? where CODLIV" + indice + " = ?", new Object[]{nuovoCodice,codice});
      }
    } catch (SQLException e1) {
      this.getRequest().setAttribute("RISULTATO", "NOK");
      throw new GestoreException(
          "Errore durante la rinominazione del campo CODLIV" + indice + " della tabella CAIS", "categorie.rinominaCodliv",new Object[]{indice},  e1);
    }

    for(int i=0;i<entita.length;i++){
      if(this.sqlManager.isTable(entita[i])){
        try {
          this.sqlManager.update("update " + entita[i] + " set " + campiEntita[i] + " = ? where " + campiEntita[i] + " = ?", new Object[]{nuovoCodice,codice});
        } catch (SQLException e) {
          this.getRequest().setAttribute("RISULTATO", "NOK");
          throw new GestoreException(
              "Errore durante la rinominazione del campo " + campiEntita[i] + " della tabella " +  entita[i], "categorie.rinomina",new Object[]{campiEntita[i],entita[i]},  e);
        }
      }
    }


    // setta l'operazione a completata, in modo da scatenare il reload della
    // pagina principale
    this.getRequest().setAttribute("RISULTATO", "OK");
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
