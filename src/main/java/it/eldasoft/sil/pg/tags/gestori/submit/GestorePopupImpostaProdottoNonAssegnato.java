/*
 * Created on 25/03/11
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

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AtacManager;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Gestore non standard per l'aggiornamento del prodotto a non assegnato
 *
 * @author Alex Mancini
 */
public class GestorePopupImpostaProdottoNonAssegnato extends AbstractGestoreEntita {

  /** Logger */
  static Logger            logger           = Logger.getLogger(GestorePopupImpostaProdottoNonAssegnato.class);

  @Override
  public String getEntita() {
    return "TORN";
  }

  public GestorePopupImpostaProdottoNonAssegnato() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupImpostaProdottoNonAssegnato(boolean isGestoreStandard) {
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

    String ngara = null;
    Long contaf = null;

    ngara = UtilityStruts.getParametroString(this.getRequest(), "ngara");
    contaf = Long.parseLong(UtilityStruts.getParametroString(this.getRequest(), "contaf"));
    
    if(ngara != null && contaf != null) {
      String codvoc = "";
      String errMsgEvento = "";
      int livEvento = 1;
      try {
        sqlManager.update("update GCAP set isprodneg = '1' where ngara = ? and contaf = ?",new Object[]{ngara, contaf});
        codvoc = (String) this.sqlManager.getObject("select codvoc from gcap where ngara=? and contaf = ?",new Object[]{ngara, contaf});
      } catch (SQLException e) {
        livEvento = 3;
        errMsgEvento = e.getLocalizedMessage();
        throw new GestoreException("Errore nell'aggiornamento del dato isprodneg in GCAP",null, e);
      } finally{
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(livEvento);
        String oggetto = ngara;
        logEvento.setOggEvento(oggetto);
        logEvento.setCodEvento("GA_RICNEG_NOPROD");
        logEvento.setDescr("Impostazione prodotto non assegnato (cod.lavorazione "+codvoc+")");
        logEvento.setErrmsg(errMsgEvento);
        try{
          LogEventiUtils.insertLogEventi(logEvento);
        }catch(Exception e){
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi");
        }
      }
      
      try {
        AtacManager atacManager = (AtacManager)UtilitySpring.getBean("atacManager", this.getServletContext(), AtacManager.class);
        atacManager.controllaStatoRicercaDiMercato(ngara);
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'aggiornamento dello stato della Gara",null, e);
      }

      this.getRequest().setAttribute("esito", "1");
    }
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm ) throws GestoreException {

  }




  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }




  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {


  }

}