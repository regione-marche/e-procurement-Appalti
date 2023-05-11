/*
 * Created on 10/ott/2018
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
import it.eldasoft.sil.pg.bl.CopiaCriteriManager;
import it.eldasoft.utils.spring.UtilitySpring;


public class GestoreImportaCriteriModello extends AbstractGestoreEntita{

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

    CopiaCriteriManager copiaCriteriManager = (CopiaCriteriManager) UtilitySpring.getBean("copiaCriteriManager",
        this.getServletContext(), CopiaCriteriManager.class);

    // TODO Auto-generated method stub
    String  ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    String modelloSelezionato = this.getRequest().getParameter("modello");
    String  codiceGara = UtilityStruts.getParametroString(this.getRequest(),"codiceGara");
    modelloSelezionato = modelloSelezionato.substring(modelloSelezionato.indexOf(':') + 1);

    try {

      //Lista criteri da importare
      List listaCriteri = sqlManager.getListVector(
          "select G.NECVAN,G.NORPAR,G.TIPPAR,G.MAXPUN,G.DESPAR,G.LIVPAR,G.NECVAN1," +
          "G.NORPAR1,G.MINPUN, G1.ID, G1.DESCRI,G1.MAXPUN,G1.FORMATO,G1.MODPUNTI,G1.MODMANU,G.ISNOPRZ,G1.NUMDECI,G1.FORMULA, G1.ESPONENTE from GOEVMOD G LEFT JOIN G1CRIDEF G1 "
            + "ON G.ID = G1.IDGOEVMOD where G.IDCRIMOD = ? ORDER BY G.NECVAN, G1.ID",new Object[] { modelloSelezionato });

        //Si devono cancellare gli eventuali criteri presenti nella gara di destinazione

        //Cancellazione delle figlie di goev
        this.getSqlManager().update(
            "delete from DPUN where NGARA = ? and NECVAN in (select necvan from goev where NGARA = ?)",
            new Object[] { ngara,ngara });

        this.getSqlManager().update(
            "delete from GOEV where NGARA = ?",
            new Object[] { ngara });

        this.getSqlManager().update(
            "update GARE1 set mintec = NULL, mineco = NULL where NGARA = ?",
            new Object[] { ngara });

        copiaCriteriManager.copiaCriteri(codiceGara, listaCriteri, ngara, "modello");

        this.getRequest().setAttribute("RISULTATO", "OK");

    }  catch (SQLException e) {
      this.getRequest().setAttribute("RISULTATO", "ERRORI");
      throw new GestoreException("Errore nell'importo dei criteri di valutazione", null, e);
    }

  }

}
