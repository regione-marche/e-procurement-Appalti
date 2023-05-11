/*
 * Created on 18/043/11
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
import it.eldasoft.sil.pg.bl.AurManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.rmi.RemoteException;
import java.sql.SQLException;

import javax.xml.rpc.holders.StringHolder;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.example.getDettaglioOffertaFornitore.RetXmlGetDettaglioOffertaFornitoreDocument;
import org.example.getDettaglioOffertaFornitore.RetXmlGetDettaglioOffertaFornitoreDocument.RetXmlGetDettaglioOffertaFornitore.RigheOffertaFornitore;
import org.springframework.transaction.TransactionStatus;

import pc_nicola.aur.WebServices.WsAURSoapProxy;

/**
 * Gestore non standard per l'acquisizione delle offerte provenienti
 * da Portale AUR
 * 
 * @author Marcello Caminiti
 */
public class GestorePopupInserOffertaFornitoreAUR extends AbstractGestoreEntita {
  static Logger               logger                              = Logger.getLogger(GestorePopupAcquisisciDaPortale.class);
    
  public String getEntita() {
    return "GCAP";
  }

  public GestorePopupInserOffertaFornitoreAUR() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupInserOffertaFornitoreAUR(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  public void preDelete(TransactionStatus status, DataColumnContainer
      dataColumnContainer) throws GestoreException {
  }

  public void postDelete(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  public void preInsert(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {
    
    if (logger.isDebugEnabled())
      logger.debug("GestorePopupInserOffertaFornitoreAUR: preInsert: inizio metodo");
    
       
    String messageKey = null;
    String urlWsAUR = ConfigManager.getValore("it.eldasoft.sil.pg.integrazioneAUR.ws.url");
    String userAUR = ConfigManager.getValore("it.eldasoft.sil.pg.userAUR");
    String passwordAUR = ConfigManager.getValore("it.eldasoft.sil.pg.passwordAUR");
            
    if(userAUR== null || "".equals(userAUR)){
      messageKey = "errors.aur.noUser";
      this.getRequest().setAttribute("erroreAcquisizione", "1");
      throw new GestoreException("Non è valorizzato l'user AUR", messageKey);
    }
    
    if(passwordAUR== null || "".equals(passwordAUR)){
      messageKey = "errors.aur.noPassword";
      this.getRequest().setAttribute("erroreAcquisizione", "1");
      throw new GestoreException("Non è valorizzata la password AUR", messageKey);
    }
    
        
    WsAURSoapProxy proxy=new WsAURSoapProxy(urlWsAUR);
    StringHolder holder = new StringHolder();
        
    String numeroGara = UtilityStruts.getParametroString(this.getRequest(),"numeroGara");
    String codiceDitta = UtilityStruts.getParametroString(this.getRequest(),"codiceDitta");
    String carrello = UtilityStruts.getParametroString(this.getRequest(),"carrello");
    String garaLottiConOffertaUnica = UtilityStruts.getParametroString(this.getRequest(),"garaLottiConOffertaUnica");
    String codiceFornitore = "";
    String tipoForniture = UtilityStruts.getParametroString(this.getRequest(),"tipoForniture");
    
    AurManager aurManager = (AurManager) UtilitySpring.getBean("aurManager",
        this.getServletContext(), AurManager.class);
    
    if(codiceDitta.startsWith("A"))
      codiceFornitore = codiceDitta.substring(1);
    
    try {
      if(logger.isDebugEnabled()) {
        String log="Chiamata al servizio AUR_GetDettaglioOffertaFornitore con i seguenti parametri:";
        log+=" utente=" + userAUR + "; password=" + passwordAUR + "; carrello=" + carrello + "; codiceFornitore=" + codiceFornitore;
        logger.debug(log);
      }
      long esito=proxy.AUR_GetDettaglioOffertaFornitore( userAUR, passwordAUR, carrello, codiceFornitore, holder );
      
      
      if(esito<0){
        if(esito==-1000600){
          messageKey = "errors.aur.noOffertaFornitore";
          this.getRequest().setAttribute("erroreAcquisizione", "1");
          throw new GestoreException("Offerta fornitore non presente", messageKey);
        }
        if(esito==-1000601){
          messageKey = "errors.aur.offertaFornitoreIncompleta";
          this.getRequest().setAttribute("erroreAcquisizione", "1");
          throw new GestoreException("Offerta fornitore incompleta", messageKey);
        }
        messageKey = "errors.aur.esitoWebService";
        this.getRequest().setAttribute("erroreAcquisizione", "1");
        throw new GestoreException("Il webservice AUR_GetDettaglioOffertaFornitore ha restituito il codice di errore:" + String.valueOf(esito), messageKey);
      }else if(esito>0){
        messageKey = "warning.aur.esitoWebService";
        this.getRequest().setAttribute("erroreAcquisizione", "1");
        throw new GestoreException("Il webservice AUR_GetDettaglioOffertaFornitore ha restituito il codice di warning:" + String.valueOf(esito), messageKey);
      }else{
        RetXmlGetDettaglioOffertaFornitoreDocument document = RetXmlGetDettaglioOffertaFornitoreDocument.Factory.parse(holder.value);
        //RigaOffertaFornitore[] righeOffertaFornitore = document.getRetXmlGetDettaglioOffertaFornitore().getRigheOffertaFornitore();
        RigheOffertaFornitore righeOffertaFornitore = document.getRetXmlGetDettaglioOffertaFornitore().getRigheOffertaFornitore();
                
        aurManager.insertOffertaProdotto(righeOffertaFornitore, numeroGara, codiceDitta, garaLottiConOffertaUnica, tipoForniture);
        this.getRequest().setAttribute("acquisizioneEseguita", "1");
      }
      
    } catch (RemoteException e) {
      messageKey = "errors.aur.erroreWebService";
      this.getRequest().setAttribute("erroreAcquisizione", "1");
      throw new GestoreException("Errore nella chiamata al web service AUR AUR_GetDettaglioOffertaFornitore", messageKey);
    } catch (XmlException e) {
      messageKey = "errors.aur.erroreXmlWebService";
      this.getRequest().setAttribute("erroreAcquisizione", "1");
      throw new GestoreException("Errore nella lettura del xml ritornato dal webservice AUR_GetElencoProdottiFornitore", messageKey);
    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreAcquisizione", "1");
      throw new GestoreException("Errore nell'inserimento dell'offerta proveniente da Portale AUR", null,e);
    }
    
    if (logger.isDebugEnabled())
      logger.debug("GestorePopupInserOffertaFornitoreAUR: preInsert: fine metodo");
  }
  
  
  public void preUpdate(TransactionStatus status, DataColumnContainer dataColumnContainer)
      throws GestoreException {
        
  }
  
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }
  
  

  
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

    
  }
  
}