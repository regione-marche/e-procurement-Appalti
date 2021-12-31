package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.TransactionStatus;

public class GestoreLineeOrdiniNso extends AbstractGestoreChiaveIDAutoincrementante {

  GenChiaviManager genChiaviManager = null;

  @Override
  public String getCampoNumericoChiave() {
    return "ID";
  }

  @Override
  public String getEntita() {
    return "NSO_LINEE_ORDINI";
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    Long idOrdine =datiForm.getLong("NSO_LINEE_ORDINI.NSO_ORDINI_ID");
    String selectMaxIdLineaOrdine ="select coalesce(max(id_linea),0) from nso_linee_ordini where nso_ordini_id = ?";
    Long maxIdLineaOrdine;
    try {
      maxIdLineaOrdine = (Long) this.sqlManager.getObject(selectMaxIdLineaOrdine, new Object[] {idOrdine });
      maxIdLineaOrdine = new Long(maxIdLineaOrdine.intValue() + 1);
      datiForm.setOriginalValue("NSO_LINEE_ORDINI.ID_LINEA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null));
      datiForm.setValue("NSO_LINEE_ORDINI.ID_LINEA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, maxIdLineaOrdine));

    } catch (SQLException e) {
      throw new GestoreException("Errore nella determinazione del progressivo Id Linea", null, e);
    }
    super.preInsert(status, datiForm);
  }

  @Override
  public void afterInsertEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    // Gestione delle sezioni 'Atti autorizzativi'



    Long idLineaOrdine =datiForm.getLong("NSO_LINEE_ORDINI.ID");


    HttpSession session = this.getRequest().getSession();
    String uffint = (String) session.getAttribute("uffint");




    //Calcolo gli importi totali?


  }


  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    Long idLinea =datiForm.getLong("NSO_LINEE_ORDINI.ID");


  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    Long idLinea =datiForm.getLong("NSO_LINEE_ORDINI.ID");
    Double quantitaDisponibile =datiForm.getDouble("QUANTITA_DISPONIBILE");
    Double quantita =datiForm.getDouble("NSO_LINEE_ORDINI.QUANTITA");
    //controllo sulla quanti ta disponibile
    //if(quantita!= null && quantitaDisponibile !=null && quantita > quantitaDisponibile){
    if(quantita!= null && quantitaDisponibile !=null && quantitaDisponibile < new Double(0)){
      UtilityStruts.addMessage(this.getRequest(), "warning", "warnings.nso.qtaSuperata",null);
    }
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }




}
