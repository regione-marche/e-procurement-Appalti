/*
 * Created on 15/09/20
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire il
 * sorteggio ditte per verifica requisiti
 *
 * @author Marcello Caminiti
 */
public class GestorePopupSorteggioVerificaDoc extends
    AbstractGestoreEntita {

  public GestorePopupSorteggioVerificaDoc() {
    super(false);
  }

   /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;

  private AggiudicazioneManager aggiudicazioneManager = null;

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);

    aggiudicazioneManager = (AggiudicazioneManager) UtilitySpring.getBean("aggiudicazioneManager",
        this.getServletContext(), AggiudicazioneManager.class);
  }

  @Override
  public String getEntita() {
    return "GARE";
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
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

    // lettura dei parametri di input
    String ngara = StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
      "ngara"));
    Long percentuale = datiForm.getLong("PERCENTUALE");
    Timestamp dataLimite = datiForm.getData("DATA");
    int numDitte =0;
    int numDitteSorteggiate = 0;
    String esito ="1";

    int livEvento =3;
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
    try{
      //Si deve creare una stringa in cui si ha l'elenco dei codici delle ditte separati da ,
      String elenco="";
      String select="select dittao from ditg where ngara5=? and abilitaz = ? and (DSORTEV is null or DSORTEV < ?)";
      Object par [] = new Object[]{ngara, new Long(1),dataLimite};

      List listaDitte = this.sqlManager.getListVector(select, par);
      if(listaDitte!=null && listaDitte.size()>0){
        numDitte = listaDitte.size();

        //Calcolo delle percentuale delle ditte da sorteggiare, considerando l'arrotondamento all'intero superiore
        BigDecimal percentualeBG = new BigDecimal(percentuale);
        BigDecimal numDitteBG = new BigDecimal(numDitte);
        BigDecimal numDitteSorteggiateBD = percentualeBG.divide(new BigDecimal(100)).multiply(numDitteBG);
        double d = Math.ceil(numDitteSorteggiateBD.doubleValue());
        numDitteSorteggiate = (int)d;

        String ditta=null;

        for(int i=0;i<listaDitte.size(); i++){
          ditta=SqlManager.getValueFromVectorParam(listaDitte.get(i), 0).stringValue();
          elenco+=ditta;
          if(i < listaDitte.size() - 1)
            elenco+=",";
        }
        HashMap<Object, Object> hMapDatiSelezione = new HashMap<Object, Object>();
        this.aggiudicazioneManager.selezioneCasualeParimerito("3", elenco, numDitteSorteggiate, hMapDatiSelezione);
        if(hMapDatiSelezione.get("listaParimeritoSelezionate")!=null){
          elenco = (String)hMapDatiSelezione.get("listaParimeritoSelezionate");
          //Elenco è una stringa che contiene i codici delle ditte sorteggiate, separati da ,
          String vetCodDitte[] = elenco.split(",");
          String update="update ditg set estimp='1', dsortev =? where ngara5=? and dittao=?";
          if(vetCodDitte!=null && vetCodDitte.length>0){
            Date oggi = UtilityDate.getDataOdiernaAsDate();
            numDitteSorteggiate = vetCodDitte.length;
            for(int j=0; j < vetCodDitte.length; j++){
              ditta= vetCodDitte[j];
              this.sqlManager.update(update, new Object[]{oggi, ngara,ditta});
            }
          }

          errMsgEvento = "Ditte sorteggiate: " + elenco;
        }else{
          errMsgEvento="";
          esito = "-1";
        }
      }
      livEvento = 1 ;
      this.getRequest().setAttribute("sorteggioEseguito", esito);
      this.getRequest().setAttribute("numDitte", new Long(numDitte));
      this.getRequest().setAttribute("numDitteSorteggiate", new Long(numDitteSorteggiate));
    } catch (SQLException e) {
      errMsgEvento=e.getMessage();
      throw new GestoreException("Errore nella funzione di sorteggio ditte per verifica documenti", "sorteggioDitteVerificaDoc", e);
    } finally{
      //Tracciatura eventi
      String descr="Sorteggio operatori in elenco per verifica documenti (percentuale sorteggio: " + percentuale.toString() + "%,";
      descr += "data limite " + UtilityDate.convertiData(dataLimite, UtilityDate.FORMATO_GG_MM_AAAA) + ", n.operatori coinvolti " + numDitte +", n. operatori sorteggiati " + numDitteSorteggiate + ").";
      LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
      logEvento.setLivEvento(livEvento);
      logEvento.setOggEvento(ngara);
      logEvento.setCodEvento("GA_SORTEGGIO_DITTE_ELE");
      logEvento.setDescr(descr);
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);

    }

  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }


}
