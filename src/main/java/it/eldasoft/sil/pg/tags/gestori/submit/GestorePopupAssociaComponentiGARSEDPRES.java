/*
 * Created on 04/06/13
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

/**
 * Gestore associato alla pagina gfof-garsedpres-lista-componenti.jsp per gestire
 * l'associazione di un componente della commissione ad una seduta di gara
 *
 */
public class GestorePopupAssociaComponentiGARSEDPRES extends
    AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "GFOF";
  }

  public GestorePopupAssociaComponentiGARSEDPRES() {
    super(false);
  }


  public GestorePopupAssociaComponentiGARSEDPRES(boolean isGestoreStandard) {
    super(isGestoreStandard);
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

    String[] listaComponentiSelezionati = this.getRequest().getParameterValues("keys");

    String ngara=null;
    String codfof=null;
    String numsed=null;


    String insertGarsedpres = "insert into garsedpres (ngara, numsed, codfof)"
      + " values (?,?,?)";

    for (int i = 0; i < listaComponentiSelezionati.length; i++) {
      String[] valoriComponenteSelezionato = listaComponentiSelezionati[i].split(";");
      if (valoriComponenteSelezionato.length == 3) {
        ngara= valoriComponenteSelezionato[0];
        codfof= valoriComponenteSelezionato[1];
        numsed=valoriComponenteSelezionato[2];

        if(ngara!= null && codfof!=null && numsed!=null){
          try {
            this.sqlManager.update(insertGarsedpres, new Object[] { ngara,
                new Long(numsed), codfof });

            this.getRequest().setAttribute("RISULTATO", "OK");
          } catch (SQLException e) {
            this.getRequest().setAttribute("RISULTATO", "ERRORI");
            throw new GestoreException(
                "Errore nell'aggiornamento della GARSEDPRES con chiavi "
                + "NGARA = " + ngara + ", CODFOF = " + codfof + " e NUMSED = " + numsed , null, e);
          }
        }

      }
    }

  }

}