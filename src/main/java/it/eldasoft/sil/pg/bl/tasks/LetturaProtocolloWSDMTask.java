/*
 * Created on 26/03/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.tasks;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;

public class LetturaProtocolloWSDMTask {

  static Logger logger = Logger.getLogger(LetturaProtocolloWSDMTask.class);

  private SqlManager sqlManager;
  private GestioneWSDMManager gestioneWSDMManager;


  /**
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  private class Conteggi{
    private long opOk=0;
    private long opNok=0;

    public long getOpOk() {
      return opOk;
    }

    public void setOpOk() {
      this.opOk += 1;
    }

    public long getOpNok() {
      return opNok;
    }

    public void setOpNok() {
      this.opNok += 1;
    }


 }


  public void letturaProtocolloLapisopera() throws GestoreException {

    if (WebUtilities.isAppNotReady()) {
      return;
    }

    if (logger.isDebugEnabled()) {
      logger.debug("letturaProtocolloLapisopera: inizio metodo");
    }

    Long id=null;
    String numeroprot="";
    String utente="";
    String pwd="";
    String cognome="";
    Long idconfi=null;

    String select = "select id, numeroprot from wsdocumento where numeroprot like '" + GestioneWSDMManager.PREFISSO_COD_FASCICOLO_LAPISOPERA + "%' and annoprot is null";

    List<?> listaWsDoc = null;
    try {
      listaWsDoc = this.sqlManager.getListVector(select, null);
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della tabella WSDOCUMENTO ", null, e);
    }
    if (listaWsDoc != null && listaWsDoc.size() > 0) {

      //variabili per tracciatura eventi
      int livEvento = 1;
      String codEvento = "GA_WSDM_LETTURA_PROTOCOLLO";
      String oggEvento = "";
      String descrEvento = "Esecuzione task di lettura protocollo ";
      String errMsgEvento = "";

      Vector<JdbcParametro> datiLogin = null;
      try {
        datiLogin = this.sqlManager.getVector("select username, password, cognome, idconfiwsdm from wslogin where syscon=-1 and servizio='FASCICOLOPROTOCOLLO'", null);
      } catch (SQLException e) {
        throw new GestoreException("Errore nella lettura della tabella WSLOGIN ", null, e);
      }

      utente = SqlManager.getValueFromVectorParam(datiLogin,0).getStringValue();
      pwd = SqlManager.getValueFromVectorParam(datiLogin,1).getStringValue();
      cognome = SqlManager.getValueFromVectorParam(datiLogin,2).getStringValue();
      idconfi = SqlManager.getValueFromVectorParam(datiLogin,3).longValue();

      //si deve verificare se è attiva la configurazione del WSDM
      try {
        if(!this.gestioneWSDMManager.isIntegrazioneWSDMAttivaValida(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO,idconfi.toString()))
          return;
      } catch (Exception e) {
        throw new GestoreException("Errore nella lettura della configurazione del WSDM ", null, e);
      }

      String passwordDecoded = null;
      if (pwd != null && pwd.trim().length() > 0) {
        ICriptazioneByte passwordICriptazioneByte = null;
        try {
          passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
              ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), pwd.getBytes(),
              ICriptazioneByte.FORMATO_DATO_CIFRATO);
        } catch (CriptazioneException e) {
          throw new GestoreException("Errore nella decifrazione della password della WSLOGIN ", null, e);
        }
        passwordDecoded = new String(passwordICriptazioneByte.getDatoNonCifrato());
      }else{
        passwordDecoded = "";
      }

      Conteggi conteggi= new Conteggi();

      for (Object WsDoc : listaWsDoc) {
        id = SqlManager.getValueFromVectorParam(WsDoc, 0).longValue();
        numeroprot = SqlManager.getValueFromVectorParam(WsDoc, 1).getStringValue();

        livEvento = 1;
        errMsgEvento = "";
        this.gestioneSingoloDocumento(utente, passwordDecoded, cognome, idconfi, numeroprot, id, conteggi);

      }

      descrEvento +="(" + conteggi.getOpOk() + " richieste completate con protocollo, " + conteggi.getOpNok() + " richieste non completate)";

      LogEvento logEvento = new LogEvento();
      logEvento.setCodApplicazione("PG");
      logEvento.setOggEvento(oggEvento);
      logEvento.setLivEvento(livEvento);
      logEvento.setCodEvento(codEvento);
      logEvento.setDescr(descrEvento);
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("letturaProtocolloLapisopera: fine metodo");
    }
  }

  private void gestioneSingoloDocumento(String utente, String passwordDecoded, String cognome, Long idconfi, String numeroprot, Long idDoc, Conteggi conteggi) {

    String codiceProcesso = numeroprot.substring(GestioneWSDMManager.PREFISSO_COD_FASCICOLO_LAPISOPERA.length());
    boolean doCommit=false;
    TransactionStatus status = null;

    try {

      WSDMProtocolloDocumentoResType res = gestioneWSDMManager.wsdmProtocolloAsincronoEsito(utente, passwordDecoded, cognome, codiceProcesso,
          GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO, idconfi.toString());
      if(res.isEsito()) {
        status = this.sqlManager.startTransaction();
        String numProt = res.getProtocolloDocumento().getNumeroProtocollo();
        Long annoProt = res.getProtocolloDocumento().getAnnoProtocollo();
        this.sqlManager.update("update wsdocumento set numeroprot=?, annoprot =? where id = ?", new Object[] {numProt, annoProt, idDoc});
        Timestamp dataProtocollo= this.gestioneWSDMManager.getDataProtocollo(res);
        this.sqlManager.update("update w_invcom set comnumprot=?, comdatprot =? where comnumprot = ?", new Object[] {numProt, dataProtocollo, numeroprot});
        this.sqlManager.update("update torn set nproti=? where nproti = ?", new Object[] {numProt, numeroprot});
        this.sqlManager.update("update garecont set nproat=? where nproat = ?", new Object[] {numProt, numeroprot});
        this.sqlManager.update("update gare1 set aenproti=? where aenproti = ?", new Object[] {numProt, numeroprot});
        this.sqlManager.update("update ditg set nprdom=?, dprdom =?  where nprdom = ?", new Object[] {numProt, dataProtocollo, numeroprot});
        this.sqlManager.update("update ditg set nproff=?, dproff =?  where nproff = ?", new Object[] {numProt, dataProtocollo, numeroprot});
        doCommit=true;
        conteggi.setOpOk();
      }else {
        conteggi.setOpNok();
        logger.error("Errore nella gestione del protocollo associato a WSDOCUMENTO.ID=" + idDoc + " :  " + res.getMessaggio());
      }
    } catch (Exception e) {
      conteggi.setOpNok();
      logger.error("Errore nella gestione del protocollo associato a WSDOCUMENTO.ID=" + idDoc + " :  " + e.getMessage());
    } finally{
      if (status != null) {
        try {
          if (doCommit) {
            this.sqlManager.commitTransaction(status);
          } else if(!doCommit)  {
            this.sqlManager.rollbackTransaction(status);
          }
        } catch (SQLException e) {
          logger.error("Errore nella gestione del protocollo associato a WSDOCUMENTO.ID=" + idDoc + " :  " + e.getMessage());
        }
      }
    }

  }

}
