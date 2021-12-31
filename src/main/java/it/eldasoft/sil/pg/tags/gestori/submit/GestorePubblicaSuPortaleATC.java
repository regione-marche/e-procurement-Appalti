/*
 * Created on 31/08/10
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
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneATCManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire la pubblicazione
 * su portale alice gare
 *
 * @author Marcello Caminiti
 */
public class GestorePubblicaSuPortaleATC extends
    AbstractGestoreEntita {

  static Logger               logger         = Logger.getLogger(GestorePubblicaSuPortaleATC.class);

  private GestioneATCManager gestioneATCManager;
  private GenChiaviManager genChiaviManager;

  /*
  public GestorePubblicaSuPortaleATC() {
    super(false);
  }
  */

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);

    gestioneATCManager = (GestioneATCManager) UtilitySpring.getBean("gestioneATCManager",
        this.getServletContext(), GestioneATCManager.class);

    genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);


  }

  @Override
  public String getEntita() {
    return "CATG";
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {


    //variabili per tracciatura eventi
    int livEvento = 3;
    String codEvento = "";
    String oggEvento = "";
    String descrEvento = "";
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
    String messageKey = "";


    TransactionStatus status=null;
    boolean commitTransaction = false;
    try{
      status = sqlManager.startTransaction();
      sqlManager.update("delete from catg where ngarA='$#########' and ncatg=1000", null);
      commitTransaction=true;
    } catch (SQLException e) {

    } finally {
      if (status != null) {
        try {
            if (commitTransaction) {
                sqlManager.commitTransaction(status);
            } else {
                sqlManager.rollbackTransaction(status);
            }
        } catch (SQLException e) {

        }
      }
    }



    //Selezione pubblicazioni bando da processare
    List listaPubblicazioni = null;
    try{
      listaPubblicazioni = this.sqlManager.getListVector("select codgar9, datpub from pubbli where tippub=11 and not exists (select id from garuuid where codgar=codgar9 and tipric='ATCPAT_bando')", null);
    }catch(SQLException e){
      throw new GestoreException("Errore nella lettura dei bandi pubblicati non presenti in GARUUID", null, e);
    }

    String strutturaATC = UtilityStruts.getParametroString(this.getRequest(),"strutturaATC");
    String msgErrori="";

    if(listaPubblicazioni!=null && listaPubblicazioni.size()>0){
      String codgar = null;
      String ngara = null;
      Timestamp datpub = null;
      String uuid=null;
      Vector<?> datiGara = null;
      String descrGenere =null;
      Long genPubblicazione = null;
      codEvento = "GA_PUBBLICA_BANDO_ATCPAT";
      for(int i=0;i<listaPubblicazioni.size();i++){
        try{
            commitTransaction = false;
            livEvento = 1;
            errMsgEvento = "";
            codgar = SqlManager.getValueFromVectorParam(listaPubblicazioni.get(i), 0).getStringValue();
            datpub = SqlManager.getValueFromVectorParam(listaPubblicazioni.get(i), 1).dataValue();

            try {
              datiGara = this.sqlManager.getVector("select codice,genere from V_GARE_GENERE where codgar = ? and genere <100 ", new Object[]{codgar});
              if(datiGara!=null && datiGara.size()>0){
                ngara = SqlManager.getValueFromVectorParam(datiGara, 0).getStringValue();
                genPubblicazione = SqlManager.getValueFromVectorParam(datiGara, 1).longValue();
              }
              oggEvento = ngara;
              int index_genere = genPubblicazione.intValue();
              switch (index_genere) {
              case 1:
                descrGenere = "gara";
                oggEvento = codgar;
                break;
              case 2:
                descrGenere = "gara";
                break;
              case 3:
                oggEvento = codgar;
                descrGenere = "gara";
                break;
              case 10:
                descrGenere = "elenco";
                break;
              case 11:
                descrGenere = "avviso";
                break;
              case 20:
                descrGenere = "catalogo";
                break;
              }
            } catch (SQLException gp) {
              livEvento = 3;
              errMsgEvento = "Errore nella lettura della view V_GARE_GENERE per la gara " + codgar + ": " + gp + ".\n";
              ngara=null;
              genPubblicazione=null;
              msgErrori+=errMsgEvento;
            }

            if(genPubblicazione!=null){
              descrEvento = "Pubblicazione " + descrGenere + " su sito istituzionale ATC";
              //Integrazione ATC pubblicazione bando
              HashMap<String,String> risposta = null;
              try{
                risposta=this.gestioneATCManager.insertBando(ngara, codgar, genPubblicazione, datpub, strutturaATC, "BANDO");
              }catch(Exception e){
                livEvento = 3;
                errMsgEvento = "Errore nell'inserimento del bando per la gara " + codgar + " : " + e + ".\n";
                msgErrori+="Errore nell'inserimento del bando per la gara " + codgar + " : " + e + ".\n";
              }
              if(risposta!=null){
                if(risposta.get("msg")!=null && !"".equals(risposta.get("msg"))){
                  String msg= risposta.get("msg");
                  livEvento = 3;
                  messageKey = "errors.gestoreException.*.ws.ATC.InternetSoluzioni.addOggetto.esitoNOK";
                  errMsgEvento = this.resBundleGenerale.getString(messageKey);
                  errMsgEvento= errMsgEvento.replace("{0}", msg);
                  msgErrori+="Errore nell'inserimento del bando per la gara " + ngara + " : " + errMsgEvento + ".\n";
                }else{
                  Integer newId= this.genChiaviManager.getNextId("GARUUID");
                  String insertGaruuid = "insert into garuuid(id,codgar,tipric,uuid) values(?,?,?,?)";
                  //Si deve prelevare il valore da mettere in uuid dalla risposta del servizio
                  if(risposta.get("idGara")!=null && !"".equals(risposta.get("idGara"))){
                    uuid= risposta.get("idGara");
                  }
                  try {
                    status = sqlManager.startTransaction();
                    this.sqlManager.update(insertGaruuid, new Object[] { newId,codgar,"ATCPAT_bando",uuid});
                    commitTransaction=true;
                  } catch (SQLException e) {
                    livEvento = 3;
                    errMsgEvento = "Errore nell'inserimento id GARUUID";
                    msgErrori+="Errore nell'inserimento id GARUUID per la gara " + codgar + " : " + e + ".\n";
                  }
                }

              }
            }
       } finally{

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
              messageKey = "errors.logEventi.inaspettataException";
              logger.error(this.resBundleGenerale.getString(messageKey), le);
            }
            if (status != null) {
              try {
                  if (commitTransaction) {
                      sqlManager.commitTransaction(status);
                  } else {
                      sqlManager.rollbackTransaction(status);
                  }
              } catch (SQLException e) {

              }
            }
          }
        }
      }

    //Selezione esiti pubblicati da processare
    try{
      listaPubblicazioni = this.sqlManager.getListVector("select p.ngara, p.dinpubg, g.codgar1 from pubg p, gare g where p.tippubg=12 and p.ngara=g.ngara and not exists (select d.id from garuuid d where d.codgar=g.codgar1 and d.tipric='ATCPAT_esito')", null);
    }catch(SQLException e){
      msgErrori+="Errore nella lettura degli esiti pubblicati non presenti in GARUUID";
    }

    if(listaPubblicazioni!=null && listaPubblicazioni.size()>0){
      String codgar = null;
      String ngara = null;
      Timestamp datpub = null;
      String uuid=null;
      Long genPubblicazione = null;
      descrEvento = "Pubblicazione esito di gara su sito istituzionale ATC";
      codEvento = "GA_PUBBLICA_ESITO_ATCPAT";
      for(int i=0;i<listaPubblicazioni.size();i++){
        try{
            commitTransaction = false;
            livEvento = 1;
            errMsgEvento = "";
            ngara = SqlManager.getValueFromVectorParam(listaPubblicazioni.get(i), 0).getStringValue();
            datpub = SqlManager.getValueFromVectorParam(listaPubblicazioni.get(i), 1).dataValue();
            codgar = SqlManager.getValueFromVectorParam(listaPubblicazioni.get(i), 2).getStringValue();
            try {
              genPubblicazione = (Long) sqlManager.getObject("select genere from V_GARE_GENERE where codgar = ? and genere <100 ", new Object[]{codgar});
              oggEvento = ngara;
              if(genPubblicazione.longValue()==1 || genPubblicazione.longValue()==3)
                oggEvento = codgar;
            } catch (SQLException gp) {
              livEvento = 3;
              errMsgEvento = "Errore nella lettura della view V_GARE_GENERE per la gara " + codgar + ": " + gp + ".\n";
              genPubblicazione=null;
              msgErrori+=errMsgEvento;
            }

            if(genPubblicazione!=null){
              HashMap<String,String> risposta = null;
              try{
                risposta=this.gestioneATCManager.insertBando(ngara, codgar, genPubblicazione, datpub, strutturaATC, "ESITO");
              }catch(Exception e){
                livEvento = 3;
                errMsgEvento = "Errore nell'inserimento del bando per la gara " + codgar + " : " + e + ".\n";
                msgErrori+="Errore nell'inserimento del bando per la gara " + codgar + " : " + e + ".\n";
              }
              if(risposta!=null){
                if(risposta.get("msg")!=null && !"".equals(risposta.get("msg"))){
                  String msg= risposta.get("msg");
                  livEvento = 3;
                  messageKey = "errors.gestoreException.*.ws.ATC.InternetSoluzioni.addOggetto.esitoNOK";
                  errMsgEvento = this.resBundleGenerale.getString(messageKey);
                  errMsgEvento= errMsgEvento.replace("{0}", msg);
                  msgErrori+="Errore nell'inserimento del bando per la gara " + ngara + " : " + errMsgEvento + ".\n";
                }else{
                  Integer newId= this.genChiaviManager.getNextId("GARUUID");
                  String insertGaruuid = "insert into garuuid(id,codgar,tipric,uuid) values(?,?,?,?)";
                  if(risposta.get("idGara")!=null && !"".equals(risposta.get("idGara"))){
                    uuid=risposta.get("idGara");
                  }
                  try {
                    status = sqlManager.startTransaction();
                    this.sqlManager.update(insertGaruuid, new Object[] { newId,codgar,"ATCPAT_esito",uuid});
                    commitTransaction=true;
                  } catch (SQLException e) {
                    livEvento = 3;
                    errMsgEvento = "Errore nell'inserimento id GARUUID";
                    msgErrori+="Errore nell'inserimento id GARUUID per la gara " + codgar + " : " + e + ".\n";
                  }
                }

              }
            }
       } finally{

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
              messageKey = "errors.logEventi.inaspettataException";
              logger.error(this.resBundleGenerale.getString(messageKey), le);
            }
            if (status != null) {
              try {
                  if (commitTransaction) {
                      sqlManager.commitTransaction(status);
                  } else {
                      sqlManager.rollbackTransaction(status);
                  }
              } catch (SQLException e) {

              }
            }
          }
        }
      }

    this.getRequest().setAttribute("pubblicazioneEseguita", "1");
    if("".equals(msgErrori))
      msgErrori=null;
    this.getRequest().setAttribute("messaggi", msgErrori);


  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {



  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }


}
