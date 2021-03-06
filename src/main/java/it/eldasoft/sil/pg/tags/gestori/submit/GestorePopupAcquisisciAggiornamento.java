/*
 * Created on 03/03/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoAnagraficaImpresaDocument;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per l'acquisizione di un singolo aggiornamento FS5 proveniente
 * da Portale ALice
 *
 * @author Marcello Caminiti
 */
public class GestorePopupAcquisisciAggiornamento extends AbstractGestoreEntita {

  /** Logger */
  static Logger            logger           = Logger.getLogger(GestorePopupAcquisisciAggiornamento.class);

  static String     nomeFileXML_Aggiornamento = "dati_agganag.xml";

  @Override
  public String getEntita() {
    return "IMPR";
  }

  public GestorePopupAcquisisciAggiornamento() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupAcquisisciAggiornamento(boolean isGestoreStandard) {
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

    //variabili per tracciatura eventi
    int livEvento = 1;
    String codEvento = "GA_ACQUISIZIONE_AGGANAG";
    String oggEvento = "";
    String descrEvento = "Acquisizione aggiornamento anagrafico da portale Appalti";
    String errMsgEvento = "";
    String user = null;
    Long idcom = null;
    FileAllegatoManager fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager",
        this.getServletContext(), FileAllegatoManager.class);

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);

    try{
      String idocmString = UtilityStruts.getParametroString(this.getRequest(),"idcom");
      idcom =  new Long(idocmString);
      user = UtilityStruts.getParametroString(this.getRequest(),"comkey1");
      String messaggioVariazioni = UtilityStruts.getParametroString(this.getRequest(),"messaggioVariazioni");

      String select=null;
      boolean errori=false;
      Date comDataStato = null;

      select="select USERKEY1 from w_puser where USERNOME = ? ";
      String codiceDitta;
      try {
        codiceDitta = (String)sqlManager.getObject(select, new Object[]{user});
      } catch (SQLException e) {
        this.getRequest().setAttribute("erroreAcquisizione", "1");
        throw new GestoreException("Errore nella lettura della tabella W_PUSER ",null, e);
      }
      oggEvento = codiceDitta;

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
        this.getRequest().setAttribute("erroreAcquisizione", "1");
        throw new GestoreException("Errore nella lettura della tabella W_DOCDIG ",null, e);
      }
      String idprgW_INVCOM = null;
      Long iddocdig = null;
      GestorePopupAcquisisciDaPortale gacqport = new GestorePopupAcquisisciDaPortale();
      gacqport.setRequest(this.getRequest());
      if(datiW_DOCDIG != null ){
        if(((JdbcParametro)datiW_DOCDIG.get(0)).getValue() != null)
          idprgW_INVCOM = ((JdbcParametro) datiW_DOCDIG.get(0)).getStringValue();

        if(((JdbcParametro)datiW_DOCDIG.get(1)).getValue() != null)
        try {
          iddocdig = ((JdbcParametro) datiW_DOCDIG.get(1)).longValue();
        } catch (GestoreException e2) {
          this.getRequest().setAttribute("erroreAcquisizione", "1");
          throw new GestoreException("Errore nella lettura della tabella W_DOCDIG ",null, e2);
        }


        //Lettura del file xml immagazzinato nella tabella W_DOCDIG
        BlobFile fileAllegato = null;
        try {
          fileAllegato = fileAllegatoManager.getFileAllegato(idprgW_INVCOM,iddocdig);
        } catch (Exception e) {
          this.getRequest().setAttribute("erroreAcquisizione", "1");
          throw new GestoreException("Errore nella lettura del file allegato presente nella tabella W_DOCDIG",null, e);
        }
        String xml=null;
        if(fileAllegato!=null && fileAllegato.getStream()!=null){
          xml = new String(fileAllegato.getStream());

          AggiornamentoAnagraficaImpresaDocument document;
          try {
            document = AggiornamentoAnagraficaImpresaDocument.Factory.parse(xml);
            pgManager.aggiornaDitta(document,codiceDitta,"UPDATE");

          //Viene popolata la G_NOTEAVVISI
            if(messaggioVariazioni!=null && !"".equals(messaggioVariazioni)){
              // Operatore (utente di USRSYS che ha avuto accesso all'applicativo)
              ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
                  CostantiGenerali.PROFILO_UTENTE_SESSIONE);

              //La variabile contiene l'informazione se ? stata modificata la pec o i referenti.
              String impostareStatoNota = UtilityStruts.getParametroString(this.getRequest(),"impostareStatoNota");
              pgManager.InserisciVariazioni(messaggioVariazioni, codiceDitta,"INS",profilo,comDataStato,"true".equals(impostareStatoNota));
            }

            //Aggiornamento dello stato a processata
            gacqport.aggiornaStatoW_INVOCM(idcom,"6");

          } catch (XmlException e) {
            this.getRequest().setAttribute("erroreAcquisizione", "1");
            throw new GestoreException("Errore nella lettura del file XML ",null, e);
          }

        }else{
          //Aggiornamento dello stato a errore
          gacqport.aggiornaStatoW_INVOCM(idcom,"7");
          errori=true;

        }


      }else{
      //Aggiornamento dello stato a errore
        gacqport.aggiornaStatoW_INVOCM(idcom,"7");
        errori=true;

      }

      if(errori)
        this.getRequest().setAttribute("erroreAcquisizione", "1");
      else
        this.getRequest().setAttribute("acquisizioneEseguita", "1");
    }catch(GestoreException e){
      livEvento = 3;
      errMsgEvento = e.getMessage();
      throw e;
    }finally {
      //Tracciatura eventi
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(oggEvento);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(descrEvento + " (cod.operatore: " + user + ", id.comunicazione: " + idcom + ")");
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        String messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
      }
    }
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