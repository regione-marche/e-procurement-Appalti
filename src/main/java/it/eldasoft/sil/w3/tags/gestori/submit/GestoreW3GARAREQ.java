/*
 * Created on 20/ott/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.w3.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestoreW3GARAREQ extends AbstractGestoreChiaveNumerica {

  static Logger logger = Logger.getLogger(GestoreW3GARAREQ.class);

  @Override
  public String[] getAltriCampiChiave() {
    return new String[] { "NUMGARA" };
  }

  @Override
  public String getCampoNumericoChiave() {
    return "NUMREQ";
  }

  @Override
  public String getEntita() {
    return "W3GARAREQ";
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
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
  throws GestoreException {

    super.preInsert(status, datiForm);

    this.gestioneReqCig(status, datiForm);

    this.gestioneReqDoc(status, datiForm);

  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    Long numgara = datiForm.getLong("W3GARAREQ.NUMGARA");
    Long numreq = datiForm.getLong("W3GARAREQ.NUMREQ");

    this.getGeneManager().deleteTabelle(new String[] { "W3GARAREQCIG" },
        "NUMGARA = ? AND NUMREQ = ?", new Object[] { numgara, numreq });

    this.getGeneManager().deleteTabelle(new String[] { "W3GARAREQDOC" },
        "NUMGARA = ? AND NUMREQ = ?", new Object[] { numgara, numreq });

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    this.gestioneReqCig(status, datiForm);

    this.gestioneReqDoc(status, datiForm);

  }

  /**
   *
   * @param status
   * @param datiForm
   * @throws GestoreException
   */
  private void gestioneReqCig(TransactionStatus status,
      DataColumnContainer datiForm) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("gestioneReqCig: inizio metodo");

    this.controlloCigDuplicati(status, datiForm);

    AbstractGestoreChiaveNumerica gestoremultiploW3GARAREQCIG = new DefaultGestoreEntitaChiaveNumerica("W3GARAREQCIG", "NUMCIG",
        new String[] {"NUMGARA", "NUMREQ" }, this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm, gestoremultiploW3GARAREQCIG, "W3GARAREQCIG", new DataColumn[] {
        datiForm.getColumn("W3GARAREQ.NUMGARA"), datiForm.getColumn("W3GARAREQ.NUMREQ") }, null);

    if (logger.isDebugEnabled()) logger.debug("gestioneReqCig: fine metodo");

  }

  /**
  *
  * @param status
  * @param datiForm
  * @throws GestoreException
  */
 private void gestioneReqDoc(TransactionStatus status,
     DataColumnContainer datiForm) throws GestoreException {

   if (logger.isDebugEnabled())
     logger.debug("gestioneReqDoc: inizio metodo");



   AbstractGestoreChiaveNumerica gestoremultiploW3GARAREQDOC = new DefaultGestoreEntitaChiaveNumerica("W3GARAREQDOC", "NUMDOC", new String[] { "NUMGARA", "NUMREQ" }, this.getRequest());
   this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
       gestoremultiploW3GARAREQDOC, "W3GARAREQDOC", new DataColumn[] {
           datiForm.getColumn("W3GARAREQ.NUMGARA"),
           datiForm.getColumn("W3GARAREQ.NUMREQ") }, null);

   if (logger.isDebugEnabled())
     logger.debug("gestioneReqDoc: fine metodo");

 }


 /**
 *
 * @param status
 * @param datiForm
 * @throws GestoreException
 */
 private void controlloCigDuplicati(TransactionStatus status,
    DataColumnContainer datiForm) throws GestoreException {
   if (logger.isDebugEnabled())
     logger.debug("controlloCigDuplicati: inizio metodo");

   String nomeCampoNumeroRecord = "NUMERO_W3GARAREQCIG";
   String nomeCampoDelete = "DEL_W3GARAREQCIG";
   String nomeCampoMod = "MOD_W3GARAREQCIG";

   int numeroRecord = datiForm.getLong(nomeCampoNumeroRecord).intValue();
   List<Object> listaCig = new Vector<Object>();
   boolean cigRipetuti = false;
   Long numgara = null;
   Long numreq = null;
   DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(datiForm.getColumns("W3GARAREQCIG", 0));
   for (int i = 1; i <= numeroRecord; i++) {
     DataColumnContainer newDataColumnContainer = new DataColumnContainer(tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));
     boolean deleteOccorrenza = newDataColumnContainer.isColumn(nomeCampoDelete)
     && "1".equals(newDataColumnContainer.getString(nomeCampoDelete));

     if (newDataColumnContainer.isColumn("W3GARAREQCIG.CIG") && false == deleteOccorrenza){
       String tmpCig = newDataColumnContainer.getString("W3GARAREQCIG.CIG");
       numgara = newDataColumnContainer.getLong("W3GARAREQCIG.NUMGARA");
       numreq = newDataColumnContainer.getLong("W3GARAREQCIG.NUMREQ");
       tmpCig = UtilityStringhe.convertiNullInStringaVuota(tmpCig);
       if(!"".equals(tmpCig) ){
         if(!listaCig.isEmpty()){
           for (int j = 0; j < listaCig.size(); j++) {
             if(tmpCig.equals(listaCig.get(j))){
               cigRipetuti =  true;
             }
           }
           if(cigRipetuti){
             //UtilityStruts.addMessage(this.getRequest(), "warning", "errors.requisiti.CIG.duplicati", null);
             //return;
             throw new GestoreException("CIG duplicati",
                 "errors.requisiti.CIG.duplicati", new Object[] { "CIG duplicati" }, new Exception());
           }else{
             listaCig.add(tmpCig);
           }
         }else{
           listaCig.add(tmpCig);
         }
       }
     }
   }

   if (logger.isDebugEnabled())
     logger.debug("controlloCigDuplicati: fine metodo");

  }

}
