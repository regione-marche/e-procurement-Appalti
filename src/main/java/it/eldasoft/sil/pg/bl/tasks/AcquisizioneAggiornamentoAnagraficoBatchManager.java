/*
 * Created on 28/feb/2019
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.bl.tasks;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ControlloDati190Manager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAcquisisciDaPortale;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoAnagraficaImpresaDocument;
import it.eldasoft.sil.portgare.datatypes.RecapitiType;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;


public class AcquisizioneAggiornamentoAnagraficoBatchManager {

  static Logger      logger = Logger.getLogger(ControlloDati190Manager.class);
  static String     nomeFileXML_Aggiornamento = "dati_agganag.xml";
  
  private SqlManager sqlManager;
  private PgManager pgManager;  
  private FileAllegatoManager fileAllegatoManager;
  
  /**
  *
  * @param fileAllegatoDao
  */
 public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
   this.fileAllegatoManager = fileAllegatoManager;
 }

 /**
  *
  * @param sqlManager
  */
 public void setSqlManager(SqlManager sqlManager) {
   this.sqlManager = sqlManager;
 }

 /**
  *
  * @param pgManager
  */
 public void setPgManager(PgManager pgManager) {
   this.pgManager = pgManager;
 }
  
 public void acquisizioneAggiornamentoAnagrafico() throws SQLException, GestoreException{
    
   //variabili per tracciatura eventi
   int livEvento = 1;
   String codEvento = "GA_ACQUISIZIONE_AGGANAG";
   String oggEvento = "";
   String descrEvento = "Acquisizione aggiornamento anagrafico da portale Appalti";
   String errMsgEvento = "";
   
   String idprg="PA";
   String comstato= "5";
   String comtipo="FS5";
   boolean errori = false;
   Date comDataStato = null;
   Long idcom = null;
   String nomeDitta = null;
   if (logger.isDebugEnabled())
        logger.debug("AcquisizioneAggiornamentoAnagrafico: inizio metodo");
   
   
   String select = "select IDCOM, COMKEY1 from W_INVCOM where IDPRG = ? and COMTIPO = ? AND COMSTATO = ?";
   List AggiornamentiDaEseguire = sqlManager.getListVector(select,
       new Object[]{idprg,comtipo,comstato});
   if(AggiornamentiDaEseguire != null && AggiornamentiDaEseguire.size()>0){
     for(int i = 0; i < AggiornamentiDaEseguire.size(); i++ ){
       try{
       idcom = (Long) SqlManager.getValueFromVectorParam(AggiornamentiDaEseguire.get(i), 0).longValue();
       nomeDitta = (String) SqlManager.getValueFromVectorParam(AggiornamentiDaEseguire.get(i), 1).stringValue();
       
       select="select USERKEY1 from w_puser where USERNOME = ? ";
       String codiceDitta;
       try {
         codiceDitta = (String)sqlManager.getObject(select, new Object[]{nomeDitta});
         oggEvento = codiceDitta;
       } catch (SQLException e) {
         errMsgEvento = e.getMessage();
         livEvento = 3;
         throw new GestoreException("Errore nella lettura della tabella W_PUSER ",null, e);
       }
       
       //Vengono letti i documenti associati ad ogni occorrenza di di W_INVCOM
       select="select idprg,iddocdig from w_docdig where DIGENT = ? and digkey1 = ? and idprg = ? and dignomdoc = ? ";
       String digent="W_INVCOM";
       String idprgW_DOCDIG="PA";

       Vector datiW_DOCDIG = null;
       try {
         comDataStato = (Date)sqlManager.getObject("select comdatastato from w_invcom where idprg=? and idcom=?", new Object[]{"PA",idcom});
           datiW_DOCDIG = sqlManager.getVector(select,
               new Object[]{digent, idcom.toString(), idprgW_DOCDIG, nomeFileXML_Aggiornamento});

       } catch (SQLException e) {
         livEvento = 3;
         errMsgEvento = e.getMessage();
         throw new GestoreException("Errore nella lettura della tabella W_DOCDIG ",null, e);
       }
       
       String idprgW_INVCOM = null;
       Long iddocdig = null;
       
       if(datiW_DOCDIG != null ){
         if(((JdbcParametro)datiW_DOCDIG.get(0)).getValue() != null)
           idprgW_INVCOM = ((JdbcParametro) datiW_DOCDIG.get(0)).getStringValue();

         if(((JdbcParametro)datiW_DOCDIG.get(1)).getValue() != null)
         try {
           iddocdig = ((JdbcParametro) datiW_DOCDIG.get(1)).longValue();
         } catch (GestoreException e2) {
           livEvento = 3;
           errMsgEvento = e2.getMessage();
           throw new GestoreException("Errore nella lettura della tabella W_DOCDIG ",null, e2);
         }


         //Lettura del file xml immagazzinato nella tabella W_DOCDIG
         BlobFile fileAllegato = null;
         try {
           fileAllegato = fileAllegatoManager.getFileAllegato(idprgW_INVCOM,iddocdig);
         } catch (Exception e) {
           livEvento = 3;
           errMsgEvento = e.getMessage();
           throw new GestoreException("Errore nella lettura del file allegato presente nella tabella W_DOCDIG",null, e);
         }
         String xml=null;
         if(fileAllegato!=null && fileAllegato.getStream()!=null){
           xml = new String(fileAllegato.getStream());

           AggiornamentoAnagraficaImpresaDocument document;
           try {
             document = AggiornamentoAnagraficaImpresaDocument.Factory.parse(xml);
             
             String msgImpresa=pgManager.controlloDatiImpresa(document, codiceDitta);

             //Controllo Legale Rappresentante
             String msgLegale = pgManager.controlloDatiReferenti( document, codiceDitta, "LEGALE");

             //Controllo Direttore Tecnico
             String msgDirettore = pgManager.controlloDatiReferenti( document, codiceDitta, "DIRETTORE");

             //Controllo Azionista
             //String msgAzionista = pgManager.controlloDatiReferenti( document, codiceDitta, "AZIONISTA");

             //Controllo Soggetti con altre cariche o qualifiche
             String msgAltreCariche = pgManager.controlloDatiReferenti( document, codiceDitta, "ALTRECARICHE");

             //Controllo Collaboratore
             String msgCollaboratore = pgManager.controlloDatiReferenti( document, codiceDitta, "COLLABORATORE");

             String messaggioVariazioni = msgImpresa + msgLegale + msgDirettore + msgAltreCariche + msgCollaboratore;

             //Si controlla se è variata la pec o ci sono modifiche ai referenti
             //Informazione che serve per impostare lo stato della nota ad 'aperto'
             RecapitiType recapiti = document.getAggiornamentoAnagraficaImpresa().getDatiImpresa().getImpresa().getRecapiti();
             String pecMsg = "";
             String pecDb = "";
             if(recapiti!=null){
               pecMsg = recapiti.getPec();
               try {
                 pecDb = (String)sqlManager.getObject("select emai2ip from impr where codimp = ?", new Object[]{codiceDitta});
               } catch (SQLException e) {
                 livEvento = 3;
                 errMsgEvento = e.getMessage();
                 throw new GestoreException("Errore nella lettura del campo IMPR.EMAI2IP",null, e);
               }
               if(pecDb==null)
                 pecDb="";
               if(pecMsg==null)
                 pecMsg="";
             }
             boolean impostareStatoNota = false;
             if(!pecMsg.equals(pecDb) || !"".equals(msgLegale) || !"".equals(msgDirettore) || !"".equals(msgAltreCariche) || !"".equals(msgCollaboratore))
               impostareStatoNota = true;
             
             pgManager.aggiornaDitta(document,codiceDitta,"UPDATE");

             //Viene popolata la G_NOTEAVVISI
             if(messaggioVariazioni!=null && !"".equals(messaggioVariazioni)){
               
               //La variabile contiene l'informazione se è stata modificata la pec o i referenti.
               pgManager.InserisciVariazioni(messaggioVariazioni, codiceDitta,"INS",null,comDataStato,impostareStatoNota);
             }

             //Aggiornamento dello stato a processata
             this.aggiornaStatoW_INVOCM(idcom,"6");

           } catch (XmlException e) {
             errMsgEvento = e.getMessage();
             livEvento = 3;
             throw new GestoreException("Errore nella lettura del file XML ",null, e);
           }

         }else{
           //Aggiornamento dello stato a errore
           this.aggiornaStatoW_INVOCM(idcom,"7");
           livEvento = 3;
           errori=true;

         }
       }else{
       //Aggiornamento dello stato a errore
         this.aggiornaStatoW_INVOCM(idcom,"7");
         livEvento = 3;
         errori=true;
       }
     }finally{
         try {
           LogEvento logEvento = new LogEvento();
           logEvento.setCodApplicazione("PG");
           logEvento.setLivEvento(livEvento);
           logEvento.setOggEvento(oggEvento);
           logEvento.setCodEvento(codEvento);
           logEvento.setDescr(descrEvento + " (cod.operatore: " + nomeDitta + ", id.comunicazione: " + idcom + ")");
           logEvento.setErrmsg(errMsgEvento);
           LogEventiUtils.insertLogEventi(logEvento);
         } catch (Exception le) {
           logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
         }
       }
       
     }
   }
   if (logger.isDebugEnabled())
      logger.debug("AcquisizioneAggiornamentoAnagrafico: fine metodo");
  }
  
 public void aggiornaStatoW_INVOCM(Long idcom, String stato) throws GestoreException{
   //Aggiornamento dello stato delle occorrenze per le quali in w_invcom è richiesta l' Iscrizione in elenco
   try {
     sqlManager.update(
         "update W_INVCOM set COMSTATO = ?, COMDATASTATO = ? where IDPRG=? and IDCOM=?",
         new Object[] { stato,new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),"PA",idcom.toString()});
   } catch (SQLException e) {
     throw new GestoreException("Errore nell'aggiornamento dell'entità W_INVCOM", null, e);
   }
 }
  
}
