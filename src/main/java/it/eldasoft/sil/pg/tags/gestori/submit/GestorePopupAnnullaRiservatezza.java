/*
 * Created on 27/giu/2019
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
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloModificaResType;


public class GestorePopupAnnullaRiservatezza extends AbstractGestoreEntita {
  
  static Logger               logger         = Logger.getLogger(GestorePopupAnnullaRiservatezza.class);

  @Override
  public String getEntita() {
    // TODO Auto-generated method stub
    return "GARE";
  }
  
  public GestorePopupAnnullaRiservatezza() {
    super(false);
  }
  
  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;
  
  /** Manager per l'esecuzione di query */
  private GestioneWSDMManager wsdmManager = null;


  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
    wsdmManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        this.getServletContext(), GestioneWSDMManager.class);
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
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub
    
    int livEvento = 3;
    String codEvento = "GA_WSDM_FINE_RISERVATEZZA";
    String oggEvento = "";
    String descrEvento = "Annulla riservatezza dati sul documentale";
    String errMsgEvento = "";
    String chiave = UtilityStruts.getParametroString(this.getRequest(),"chiave");
    String idconfi = UtilityStruts.getParametroString(this.getRequest(),"idconfi");
    if(chiave != null && !"".equals(chiave)){
      oggEvento = chiave;
      try {
        WSDMProtocolloModificaResType res = wsdmManager.wsdmModificaProtocollo(chiave, idconfi);
        if(res.isEsito()){
          livEvento = 1;
          errMsgEvento = "" + res.getMessaggio();
          this.getRequest().setAttribute("annullamentoEseguito", "1");
          this.sqlManager.update("update wsfascicolo set ISRISERVA = ? where key1 = ? and (entita = 'GARE' or entita = 'TORN')", new Object[]{null,chiave});
        }else{
          this.getRequest().setAttribute("annullamentoEseguito", "2");
          errMsgEvento = res.getMessaggio();
          throw new GestoreException(res.getMessaggio(),res.getMessaggio());
        }
        
      } catch (SQLException e) {
        e.printStackTrace();
      }
      finally{
      //Tracciatura eventi
        try {
          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(oggEvento);
          logEvento.setCodEvento(codEvento);
          logEvento.setDescr(descrEvento);
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          String messageKey = "errors.logEventi.inaspettataException";
          logger.error(this.resBundleGenerale.getString(messageKey), le);
          this.getRequest().setAttribute("annullamentoEseguito", "2");
        }
      }
    }
  }

  @Override
  public void preUpdate(TransactionStatus arg0, DataColumnContainer arg1) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

}
