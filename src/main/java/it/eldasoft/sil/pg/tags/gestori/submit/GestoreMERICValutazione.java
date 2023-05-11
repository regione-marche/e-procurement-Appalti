package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;
import java.util.Enumeration;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestoreMERICValutazione extends AbstractGestoreEntita {

  static Logger logger = Logger.getLogger(GestoreMERICValutazione.class);

  // Gestore fittizio.
  // Le operazione DML devono essere eseguite esplicitamente in questo gestore.
  public GestoreMERICValutazione() {
    super(false);
  }

  public String getEntita() {
    return "MERIC";
  }

  public void preInsert(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

  }

  public void postInsert(DataColumnContainer impl) throws GestoreException {

  }

  public void preUpdate(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

    Long meric = impl.getLong("MERIC.ID");

    try {

      Enumeration<?> parametersNames = this.getRequest().getParameterNames();
      while (parametersNames.hasMoreElements()) {
        String parametro = (String) parametersNames.nextElement();
        if (parametro.startsWith("A_") && parametro.endsWith("_id_prodotto_acquistato")) {
          Long mericart_id = new Long(parametro.substring(2, parametro.indexOf("_id_prodotto_acquistato")));
          if (this.getRequest().getParameter(parametro) != null && this.getRequest().getParameter(parametro).trim() != "") {
            Long mericprod_id = new Long(this.getRequest().getParameter(parametro));
            sqlManager.update("update mericprod set acquista = '1' where id = ?", new Object[] { mericprod_id });
            sqlManager.update("update mericprod set acquista = '2' where idricart = ? and id <> ?", new Object[] { mericart_id,
                mericprod_id });
          } else {
            sqlManager.update("update mericprod set acquista = '2' where idricart = ?", new Object[] { mericart_id });
          }
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nell'aggiornamento del carrello dei prodotto acquistati", null, e);
    }

  }

  public void postUpdate(DataColumnContainer impl) throws GestoreException {

  }

  public void preDelete(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

  }

  public void postDelete(DataColumnContainer impl) throws GestoreException {

  }

}
