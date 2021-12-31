/*
 * Created on 30/08/13
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.www.PortaleAlice.EsitoOutType;
import it.eldasoft.www.PortaleAlice.PortaleAliceProxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.transaction.TransactionStatus;
/**
 * Gestore non standard che si occupa di gestire la funzionalità
 * di pubblicazione su Portale Alice dei file XML prodotti per
 * adempimenti legge 190/2012
 *
 * @author Marcello Caminiti
 */
public class GestorePopupPubblicaXmlPortale extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "ANTICOR";
  }

  public GestorePopupPubblicaXmlPortale() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupPubblicaXmlPortale(boolean isGestoreStandard) {
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

    String percorsoAVCP = ConfigManager.getValore("it.eldasoft.sil.pg.avcp");
    if(percorsoAVCP==null || "".equals(percorsoAVCP)){
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException("Non è valorizzata la property del percorso della cartella AVCP ","cartellaAVCP", new Exception());
    }

    String ulrPortaleAlice = ConfigManager.getValore(CostantiGenerali.PROP_URL_WEB_SERVICE_PORTALE_ALICE);
    if(ulrPortaleAlice==null || "".equals(ulrPortaleAlice)){
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException("Non è valorizzata la property della URL del Web Service portale Appalti","urlPortale", new Exception());
    }

    String idString = UtilityStruts.getParametroString(this.getRequest(),"id");
    String annorifString = UtilityStruts.getParametroString(this.getRequest(),"anno");
    Long id = new Long(idString);
    Long annorif = new Long(annorifString);
    String nomeFile=null;
    String ufficioIntestatario=null;
    HttpSession session = this.getRequest().getSession();
    String codFiscUffIntestatario=null;
    if ( session != null) {
      ufficioIntestatario = (String) session.getAttribute("uffint");
    }
    if(ufficioIntestatario!=null && !"".equals(ufficioIntestatario)){
      try {
        codFiscUffIntestatario = (String)this.sqlManager.getObject("select codfiscprop from anticorlotti where idanticor=?", new Object[]{id});
        if(codFiscUffIntestatario ==null || "".equals(codFiscUffIntestatario))
          codFiscUffIntestatario = (String)this.sqlManager.getObject("select cfein from uffint where codein=?", new Object[]{ufficioIntestatario});
      } catch (SQLException e) {
        this.getRequest().setAttribute("erroreOperazione", "1");
        throw new GestoreException(
            "Errore durante la lettura del codice fiscale dell'ufficio intestatario ", "cfAnticorlotti", e);
      }
    }

    try {
      nomeFile = (String)sqlManager.getObject("select nomefile from anticor where id=?", new Object[]{id});
    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException("Errore durante la lettura dei dati di anticor ","datiAnticor", e);
    }

    //Percorso completo file zip
    String percorsoFile = percorsoAVCP;
    if(codFiscUffIntestatario!=null)
      percorsoFile += codFiscUffIntestatario + "/";
    percorsoFile += nomeFile;
    File file = new File(percorsoFile);

    if(!file.isFile()){
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException("Non è possibile trovare il file " + nomeFile,"pubblicazioneXMLAVCP.fileNonPresente", new Exception());
    }

    FileInputStream fis;
    try {
      fis = new FileInputStream(file);
      //fis = new FileInputStream(percorsoAVCP + nomeFile);
    } catch (FileNotFoundException e) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException("Errore durante la lettura del file " + nomeFile, "pubblicazioneXMLAVCP.letturaFile",new Object[]{nomeFile}, e);
    }

    //Chiamata al servizio
    PortaleAliceProxy proxy = new PortaleAliceProxy();
    proxy.setEndpoint(ulrPortaleAlice);
    EsitoOutType esito=null;
    try {
      esito = proxy.uploadDatasetAppalti(annorif.intValue(),codFiscUffIntestatario, IOUtils.toByteArray(fis));
      if(esito.isEsitoOk()){
        sqlManager.update("update anticor set pubblicato=? where id=?", new Object[]{"1", id});
      }
    } catch (RemoteException e) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      String codice="pubblicazioneXMLAVCP.connessione";
      String messaggio = e.getMessage();
      if(messaggio.indexOf("Connection refused")>0)
        codice+=".noServizio";
      if(messaggio.indexOf("Connection timed out")>0)
        codice+=".noServer";
      throw new GestoreException("Errore nella connessione al portale", codice,new Object[]{nomeFile}, e);
    } catch (IOException e) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException("Errore durante la pubblicazione sul portale Appalti del file " + nomeFile, "pubblicazioneXMLAVCP.pubblicazioneFile",new Object[]{nomeFile}, e);
    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException("Errore durante l'aggiornamento di ANTICOR ","aggiornamentoAnticor", e);
    }catch (Exception e){
      throw new GestoreException("Errore nella connessione al portale", "pubblicazioneXMLAVCP.connessione", e);
    } finally{
      try {
        fis.close();
      } catch (IOException e) {

      }
    }

    if(!esito.isEsitoOk()){
      String codiceErrore = esito.getCodiceErrore();
      String codiceMessaggio = "pubblicazioneXMLAVCP.rispostaPortale";
      String datiMessaggio = "";
      if (codiceErrore!=null && !"".equals(codiceErrore) && codiceErrore.indexOf("WARN-REMOVE-BACKUP")<0){
        if(codiceErrore.indexOf("YEAR-NOT-VALID")>=0){
         datiMessaggio = "L'anno di riferimento è antecendente al 2010 o successivo all'anno in corso";
        }else if(codiceErrore.indexOf("ZIP-NOT-VALID")>=0){
          datiMessaggio = "Il file ZIP contenente gli XML è vuoto";
        }else if(codiceErrore.indexOf("NO-BACKUP")>=0){
          datiMessaggio = "Non è stato possibile effettuare il backup dei dati per l'anno di riferimento che si sta aggiornando";
        }else if(codiceErrore.indexOf("NO-MKDIR")>=0){
          datiMessaggio = "Non è stato possibile creare la cartella per l'anno di riferimento";
        }else if(codiceErrore.indexOf("ZIP-READ-ERROR")>=0){
          datiMessaggio = "Non è stato possibile il file ZIP per l'anno di riferimento";
        }else if(codiceErrore.indexOf("CFSA-NOT-VALID")>=0){
          datiMessaggio = "Il codice fiscale della stazione appaltante non ha un formato valido";
        }
        try {
          fis.close();
        } catch (IOException e) {

        }

        SQLException e=null;
        throw new GestoreException("Errore durante la pubblicazione del file zip sul portale Appalti",codiceMessaggio,new Object[]{datiMessaggio},e);
      }
    }

    //Se tutto è andato bene setto nel request il parametro operazioneEseguita = 1
    this.getRequest().setAttribute("operazioneEseguita", "1");
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