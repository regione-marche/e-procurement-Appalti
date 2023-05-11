/*
 * Created on 01/03/18
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
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;
/**
 * Gestore non standard che si occupa di gestire la funzionalità
 * di archiviazione di una comunicazione
 *
 * @author Marcello Caminiti
 */
public class GestoreArchiviaComunicazione extends AbstractGestoreEntita {


  @Override
  public String getEntita() {
    return "W_INVCOM";
  }

  public GestoreArchiviaComunicazione() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestoreArchiviaComunicazione(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer
      dataColumnContainer) throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {

    String  idprg = UtilityStruts.getParametroString(this.getRequest(),"idprg");
    String  idcomString = UtilityStruts.getParametroString(this.getRequest(),"idcom");
    Long idcom = new Long(idcomString);
    String comkey1 = UtilityStruts.getParametroString(this.getRequest(),"comkey1");
    try {
      String update = "update w_invcom set comstato=? where idprg=? and idcom=?";
      sqlManager.update(update, new Object[]{"12",idprg,idcom});

    } catch (SQLException e) {
      this.getRequest().setAttribute("archiviazioneEseguita", "2");
      throw new GestoreException("Errore nell'archiviazione della comunicazione:" + idcomString, null, e);
    }

    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        this.getServletContext(), PgManagerEst1.class);
    pgManagerEst1.aggiornamentoDataAggiornamentoPortaleComunicazione(comkey1, idcom);


    //Se tutto è andato bene setto nel request il parametro archiviazioneEseguita = 1
    this.getRequest().setAttribute("archiviazioneEseguita", "1");
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer dataColumnContainer)
      throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }




  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {


  }

}