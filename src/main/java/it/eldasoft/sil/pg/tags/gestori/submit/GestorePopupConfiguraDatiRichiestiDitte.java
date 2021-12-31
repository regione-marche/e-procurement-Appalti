/*
 * Created on 11/06/15
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;

public class GestorePopupConfiguraDatiRichiestiDitte extends
    AbstractGestoreEntita {

  static final String insert_GARCONFDATI ="insert into garconfdati(id,ngara,entita,campo,formato,numord,obbligatorio) values(?,?,?,?,?,?,?) ";
  static final String delete_GARCONFDATI ="delete from garconfdati where id=? ";
  static final String update_GARCONFDATI ="update garconfdati set obbligatorio=? where id=?";

  @Override
  public String getEntita() {
    return "GARCONFDATI";
  }

  public GestorePopupConfiguraDatiRichiestiDitte() {
    super(false);
  }


  public GestorePopupConfiguraDatiRichiestiDitte(boolean isGestoreStandard) {
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

    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);

    String numeroCampi = this.getRequest().getParameter("numeroCampi");
    String ngara=this.getRequest().getParameter("ngara");
    String salvaDati=this.getRequest().getParameter("salvaDati");
    String entita= "XDPRE";
    String campo = null;
    Long formato=null;
    String obbligatorio=null;
    Long id =null;
    String campoSelezionato = null;
    Long maxNumOrdEsistente = new Long(0);
    String maxNumOrdEsistenteString = this.getRequest().getParameter("maxNumOrdEsistente");
    if(maxNumOrdEsistenteString!=null && !"".equals(maxNumOrdEsistenteString))
      maxNumOrdEsistente= new Long(maxNumOrdEsistenteString);
    try {
      if("1".equals(salvaDati)){
        Long numCampi = new Long(numeroCampi);
        for (int i = 1; i <= numCampi.intValue(); i++) {
          DataColumnContainer dataColumnContainerDiRiga = new DataColumnContainer(
              impl.getColumnsBySuffix("_" + i, false));
          id= dataColumnContainerDiRiga.getLong("GARCONFDATI.ID");
          campoSelezionato = dataColumnContainerDiRiga.getString("CAMPO_SEL");
          //se non è stato selezionato il campo nella popup si procede alla sua eliminazione
          if(!"1".equals(campoSelezionato)){
            this.sqlManager.update(delete_GARCONFDATI, new Object[] {id });
          }else{
            obbligatorio = dataColumnContainerDiRiga.getString("GARCONFDATI.OBBLIGATORIO");
            if(id!=null){
              //il campo va aggiornato
              this.sqlManager.update(update_GARCONFDATI, new Object[] {obbligatorio, id });
            }else{
              //il campo va inserito
              id=new Long(genChiaviManager.getNextId("GARCONFDATI"));
              campo = dataColumnContainerDiRiga.getString("CAMPO");
              formato =  new Long (dataColumnContainerDiRiga.getString("FORMATO_NASCOSTO"));
              maxNumOrdEsistente = new Long(maxNumOrdEsistente.longValue() + 1);
              this.sqlManager.update(insert_GARCONFDATI, new Object[] {id, ngara,entita, campo,formato,maxNumOrdEsistente,obbligatorio});
            }


          }
        }
        this.getRequest().setAttribute("RISULTATO", "OK");
        this.getRequest().setAttribute("modalita", "vis");

      }
    } catch (SQLException e) {
      this.getRequest().setAttribute("RISULTATO", "ERRORI");
      throw new GestoreException(
          "Errore nel salvataggio della configurazione degli attributi aggiuntivi per le lavorazioni e forniture della gara " + ngara
          , null, e);
    }
  }


}