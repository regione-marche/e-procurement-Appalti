/*
 * Created on 09/03/15
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

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
public class GestorePopupSorteggioDitteVerificaRequisiti extends
    AbstractGestoreEntita {

  public GestorePopupSorteggioDitteVerificaRequisiti() {
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
    String nDitteSorteggiate = StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
        "nDitteSorteggiate"));
    String codgar=null;
    String lotto = StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
        "lotto"));

    int livEvento =3;
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
    try{
      //Si deve creare una stringa in cui si ha l'elenco dei codici delle ditte separati da ,
      String elenco="";
      List listaDitte = this.sqlManager.getListVector("select dittao,codgar5 from ditg where ngara5=? and (fasgar is null or fasgar >=2)", new Object[]{ngara});
      if(listaDitte!=null && listaDitte.size()>0){
        String ditta=null;

        for(int i=0;i<listaDitte.size(); i++){
          ditta=SqlManager.getValueFromVectorParam(listaDitte.get(i), 0).stringValue();
          elenco+=ditta;
          if(i < listaDitte.size() - 1)
            elenco+=",";
          if(codgar==null)
            codgar=SqlManager.getValueFromVectorParam(listaDitte.get(i), 1).stringValue();
        }
        HashMap hMapDatiSelezione = new HashMap();
        this.aggiudicazioneManager.selezioneCasualeParimerito("3", elenco, Long.parseLong(nDitteSorteggiate), hMapDatiSelezione);
        if(hMapDatiSelezione.get("listaParimeritoSelezionate")!=null){
          elenco = (String)hMapDatiSelezione.get("listaParimeritoSelezionate");
          //Elenco è una stringa che contiene i codici delle ditte sorteggiate, separati da ,
          String vetCodDitte[] = elenco.split(",");
          String update="update ditg set estimp='1' where ngara5=? and dittao=?";
          if(vetCodDitte!=null && vetCodDitte.length>0){
            for(int j=0; j < vetCodDitte.length; j++){
              ditta= vetCodDitte[j];
              this.sqlManager.update(update, new Object[]{ngara,ditta});
            }
          }
          this.sqlManager.update("update ditg set estimp=null where ngara5=? and estimp='2'", new Object[]{ngara});
          boolean eseguireAggData=true;
          if("true".equals(lotto)){
            Long test = (Long)this.sqlManager.getObject("select count(codgar) from torn where codgar=? and not (dgara is null and ogara is null)", new Object[]{codgar});
            if(test!=null && test.longValue()>0)
              eseguireAggData=false;
          }
          if(eseguireAggData){
            String oggi = UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
            String data = oggi.substring(0, 10);
            String ora = oggi.substring(11, 16);
            this.sqlManager.update("update torn set dgara=?, ogara=? where codgar=? ", new Object[]{new SimpleDateFormat("dd/MM/yyyy").parse(data),ora, codgar});
          }
          errMsgEvento = "Ditte sorteggiate: " + elenco;
        }else{
          errMsgEvento="";
        }
      }
      livEvento = 1 ;
      this.getRequest().setAttribute("sorteggioEseguito", "1");
    } catch (SQLException e) {
      errMsgEvento=e.getMessage();
      throw new GestoreException("Errore nella funzione di sorteggio ditte per verifica requisiti", "sorteggioDitteRettificaTermini", e);
    } catch (ParseException e) {
      errMsgEvento=e.getMessage();
      throw new GestoreException("Errore nella funzione di sorteggio ditte per verifica requisiti", "sorteggioDitteRettificaTermini", e);
    }finally{
      //Tracciatura eventi
      LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
      logEvento.setLivEvento(livEvento);
      logEvento.setOggEvento(ngara);
      logEvento.setCodEvento("GA_SORTEGGIO_DITTE");
      logEvento.setDescr("Sorteggio ditte per verifica requisiti");
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);

    }

  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }


}
