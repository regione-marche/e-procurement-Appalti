/*
 * Created on 25/02/20210
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
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
/**
 * Gestore non standard che si occupa di gestire la funzionalità
 * per assegnare il numero d'ordine invito (DITG.NUMORDINV)
 *
 * @author Cristian Febas
 */
public class GestoreSorteggioOrdineInvito extends AbstractGestoreEntita {

  /** Logger */
  static Logger            logger           = Logger.getLogger(GestoreSorteggioOrdineInvito.class);

  @Override
  public String getEntita() {
    return "DITG";
  }

  public GestoreSorteggioOrdineInvito() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestoreSorteggioOrdineInvito(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
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
    String codEvento = "GA_SORTEGGIO_ORDINE_INVITO";
    String oggEvento = "";
    String descrEvento = "";
    String errMsgEvento = "";
    String ditteSorteggiate ="";
    try{
    	
      String  codiceGara = UtilityStruts.getParametroString(this.getRequest(),"codiceGara");    	
      String  ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");

      oggEvento = ngara;
      descrEvento="Sorteggio numero ordine di invito operatori in fase di prequalifica";
    
      ditteSorteggiate = this.sorteggioModalitaCasuale(codiceGara,ngara, status);

      //Se tutto è andato bene setto nel request il parametro ordineAssegnato = 1
      this.getRequest().setAttribute("ordineAssegnato", "1");
    }catch(GestoreException e){
      livEvento =3;
      errMsgEvento = e.getMessage();
      throw e;
    } finally{
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(oggEvento);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(descrEvento);
        logEvento.setErrmsg("Dettaglio numero ordine invito : " + ditteSorteggiate + " " + errMsgEvento);
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


  /**
   * Metodo per il settaggio del numero ordine invito (DITG.NUMORDINV) secondo la modalità casuale
   *
   * @param codgar
   * @param ngara
   * @param status
   *
   * @throws GestoreException
   */
  public String sorteggioModalitaCasuale(String codgar,String ngara, TransactionStatus status) throws GestoreException {

    String select="select dittao,nprogg from ditg where codgar5 = ? and ngara5=? order by nprogg";

    int numordplPartenza=0;
    String elenco="";

    try {

      List listaOperatori = this.sqlManager.getListVector(select, new Object[]{codgar,ngara});

      if (listaOperatori!=null && listaOperatori.size()>0){
        int numeroOperatori = listaOperatori.size();
        ArrayList  valori = new ArrayList(numeroOperatori);
        String update="update ditg set numordinv = ? where codgar5 = ? and ngara5=? and dittao = ?";

        for (int i=0;i<numeroOperatori;i++)
          valori.add(i, new Long(i + 1 + numordplPartenza));

        Random r =  new Random();

        for(int i = 0;i<numeroOperatori;i++) {
          // valore compreso tra 0 e la dimensione del vettore valori
          int rand=     r.nextInt(valori.size());

          Vector operatore = (Vector) listaOperatori.get(i);
          String dittao = (String)((JdbcParametro) operatore.get(0)).getValue();
          Long nprogg = (Long)((JdbcParametro) operatore.get(1)).getValue();

          //Aggiornamento del campo numordinv
           this.sqlManager.update(update, new Object[] { valori.get(rand),codgar, ngara,dittao});
           if(!"".equals(elenco)) {
        	   elenco+=", ";   
           }
           elenco+=nprogg +" " +dittao + " N.inv."+valori.get(rand);
          //si deve aggiornare il vettore dei valori, eliminado il valore appena assegnato
          valori.remove(rand);
        }

      }

    } catch (SQLException e) {
      throw new GestoreException("Errore nell'assegnamento del numero d'ordine invito in modalità casuale", null, e);
    }
    
    return elenco;
  }
  
  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {


  }





}