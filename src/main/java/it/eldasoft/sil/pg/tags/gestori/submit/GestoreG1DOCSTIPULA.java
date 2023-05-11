package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.StipuleManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestoreG1DOCSTIPULA extends AbstractGestoreChiaveIDAutoincrementante {

  private static final Logger logger = Logger.getLogger(GestoreG1DOCSTIPULA.class);

  GenChiaviManager genChiaviManager = null;

  @Override
  public String getCampoNumericoChiave() {
    return "ID";
  }

  @Override
  public String getEntita() {
    return "G1DOCSTIPULA";
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    Long idStipula = (Long) datiForm.getColumn("G1DOCSTIPULA.IDSTIPULA").getValue().getValue();
    //calcolo il numero ordine
    Long maxNumOrd = null;
    Long newNumOrd  = null;
    try {
      maxNumOrd = (Long) this.sqlManager.getObject(
                "select max(coalesce(numord,0)) from G1DOCSTIPULA where idstipula= ?",
                new Object[] {idStipula} );
      if (maxNumOrd != null && maxNumOrd.longValue()>0){
    	  newNumOrd  = maxNumOrd.longValue() + 1;
      }else {
    	  newNumOrd  = Long.valueOf(1);
      }
      datiForm.setValue("G1DOCSTIPULA.NUMORD", newNumOrd);
    } catch (SQLException e) {
    	throw new GestoreException("Errore nell'assegnamento del numero d'ordine al documento di stipula", null, e);    	
    }

    super.preInsert(status, datiForm);
    Long idDocStipula = (Long) datiForm.getColumn("G1DOCSTIPULA.ID").getValue().getValue();

    // gestione dell'allegato nella scheda
    StipuleManager stipuleManager = (StipuleManager) UtilitySpring.getBean(
        "stipuleManager", this.getServletContext(), StipuleManager.class);
    String operazione="INSERT";
    stipuleManager.setDocumentoAllegato(this.getRequest(), operazione, idDocStipula,
        null, datiForm, this.getForm());


  }

  @Override
  public void afterInsertEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {




  }


  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
    //gestita al salvataggio del dettaglio dell'allegato (sia in inserimento che in modifica),
    //la visualizzazione della lista degli allegati invece della scheda di dettaglio stesso
    this.getRequest().setAttribute("salvataggioOK", "true");
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    StipuleManager stipuleManager = (StipuleManager) UtilitySpring.getBean(
        "stipuleManager", this.getServletContext(), StipuleManager.class);

      Long idDocStipula = (Long) datiForm.getColumn("G1DOCSTIPULA.ID").getValue().getValue();
      Long iddocdg = (Long) datiForm.getColumn("W_DOCDIG.IDDOCDIG").getValue().getValue();

      datiForm.removeColumns(new String[] { "W_DOCDIG.DIGDESDOC", "W_DOCDIG.IDPRG","W_DOCDIG.IDDOCDIG","W_DOCDIG.DIGNOMDOC" });

      String operazione=null;
      if(iddocdg!=null){
        operazione = "UPDATE";
      }else{
        operazione = "INSERT";
      }

   // gestione dell'allegato nella scheda
      stipuleManager.setDocumentoAllegato(this.getRequest(), operazione, idDocStipula,
          iddocdg, datiForm, this.getForm());


  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
    //gestita al salvataggio del dettaglio dell'allegato (sia in inserimento che in modifica),
    //la visualizzazione della lista degli allegati invece della scheda di dettaglio stesso
    this.getRequest().setAttribute("salvataggioOK", "true");
  }


}
