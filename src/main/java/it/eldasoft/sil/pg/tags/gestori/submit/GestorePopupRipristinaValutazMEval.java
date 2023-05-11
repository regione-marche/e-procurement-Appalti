/*
 * Created on 05/10/22
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
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.appaltiecontratti.appalticgmsclient.api.V10Api;
import it.appaltiecontratti.appalticgmsclient.model.GarecgUpdateResponse;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.MEvalManager;

public class GestorePopupRipristinaValutazMEval extends
    AbstractGestoreEntita {

  /** Logger */
  static Logger logger = Logger.getLogger(GestorePopupRipristinaValutazMEval.class);


  @Override
  public String getEntita() {
    return "GFOF";
  }

  public GestorePopupRipristinaValutazMEval() {
    super(false);
  }

  public GestorePopupRipristinaValutazMEval(boolean isGestoreStandard) {
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
    String lotto = UtilityStruts.getParametroString(this.getRequest(),"lotto");
    String codgar = UtilityStruts.getParametroString(this.getRequest(),"codgar");
    String esito ="OK";

    String cfSa=null;
    try {
      cfSa = (String)this.sqlManager.getObject("select cfein from torn,uffint where codgar=? and cenint=codein", new String[] {codgar});
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della stazione appaltante",null,e);
    }

    int livEvento = 3;
    String errMsgEvento="";
    try {


      if(lotto!=null && !"".equals(lotto)) {
        V10Api apiAppaltiCgMs = MEvalManager.getClient(cfSa, 10); //Si imposta 10' come durata di validità del token
        GarecgUpdateResponse resp=null;
        try {
          //resp = apiAppaltiCgMs.resetConfermaByGaraUsingPATCH(cfSa, lotto);
          resp = apiAppaltiCgMs.resetConfermaByGaraUsingPOST(cfSa, lotto);
        } catch (Exception e) {
          esito="NOK";
          logger.error("Errore nella chiamata del servizio resetConfermaByGaraUsingPATCH per la gara" +  lotto   ,e);
          errMsgEvento = e.getMessage();
        }
        if("OK".equals(esito)) {
          boolean commitTransaction=true;
          if(resp.isEsito()) {
            try {
              status = this.sqlManager.startTransaction();
              this.sqlManager.update("update gare1 set statocg=? where ngara=?", new Object[] {new Long(1), lotto});
              commitTransaction=true;
              livEvento = 1 ;
            } catch (SQLException e) {
              esito="NOK";
              commitTransaction = false;
              logger.error("Errore nell'aggiornamento di commicg della gara " +  lotto   ,e);
              errMsgEvento = e.getMessage();
            } finally {
              if (status != null) {
                try {
                if (commitTransaction) {
                  this.sqlManager.commitTransaction(status);
                } else {
                  this.sqlManager.rollbackTransaction(status);
                }
                }catch(Exception e) {

                }
              }
            }

          }else {
            esito="NOK";
            List<String> listaMessaggi = resp.getInfoMessaggi();
            if(listaMessaggi!=null) {
              Iterator<String> msg = listaMessaggi.iterator();
              errMsgEvento="La chiamata al servizio resetConfermaByGaraUsingPATCH ha restituisto i seguenti messaggi:";
              while(msg.hasNext()) {
                String messaggio=msg.next();
                logger.error("La chiamata al servizio resetConfermaByGaraUsingPATCH per la gara " +  lotto  + " ha restituito il seguente messaggio: " + messaggio);
                errMsgEvento+=messaggio + "\r\n";
              }
            }
          }
        }
      }



    }finally {
      this.getRequest().setAttribute("RISULTATO", esito);
      // Tracciatura eventi

      LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
      logEvento.setLivEvento(livEvento);
      logEvento.setOggEvento(lotto);
      logEvento.setCodEvento("GA_RIPRISTINAVAL_MEVAL");
      logEvento.setDescr("Ripristino valutazione in corso su M-Eval nella fase 'Valutazione tecnica'.");
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);
    }
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }
}