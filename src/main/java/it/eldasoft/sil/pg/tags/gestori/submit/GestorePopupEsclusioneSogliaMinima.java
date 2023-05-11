/*
 * Created on 18/07/17
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
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityMath;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire la
 * funzione di Esclusione soglia minima e riparametrazione lanciato dalla pagina
 * popupEsclusioneSogliaMinima.jsp
 *
 * @author Marcello Caminiti
 */
public class GestorePopupEsclusioneSogliaMinima extends
    AbstractGestoreEntita {

  static Logger               logger         = Logger.getLogger(GestorePopupEsclusioneSogliaMinima.class);

  public GestorePopupEsclusioneSogliaMinima() {
    super(false);
  }

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;
  private PgManager  pgManager;
  private TabellatiManager  tabellatiManager;
  private MEPAManager mepaManager;

  private String decimaliArrotondamento;
  private String msgRiparam = null;
  private String msgRiparamCrit = null;
  private String selectGare1Tec = "select riptec, ripcritec from gare1 where ngara=?";
  private String selectGare1Eco = "select ripeco, ripcrieco from gare1 where ngara=?";




  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);
    tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        this.getServletContext(), TabellatiManager.class);
    mepaManager = (MEPAManager) UtilitySpring.getBean("mepaManager",
        this.getServletContext(), MEPAManager.class);
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

    //variabili per tracciatura eventi
    int livEvento = 3;
    String codEvento = "GA_OEPV_RIPARAM_TEC";
    String oggEvento = "";
    String descrEvento = "Esclusione soglia minima e riparametrazione criteri di valutazione busta tecnica";
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");


    String ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    String tipo= UtilityStruts.getParametroString(this.getRequest(),"tipo");

    Long riparam=null;
    Long riparamCrit=null;
    Vector<?> datiGare1=null;


    String selectGare1= selectGare1Tec;
    oggEvento = ngara;

    if("2".equals(tipo)){
      codEvento = "GA_OEPV_RIPARAM_ECO";
      descrEvento = "Esclusione soglia minima e riparametrazione criteri di valutazione busta economica";
      selectGare1= selectGare1Eco;
    }

    try{

      try {
        datiGare1 = this.sqlManager.getVector(selectGare1, new Object[]{ngara});
      } catch (SQLException e) {
        livEvento = 3;
        this.getRequest().setAttribute("calcoloEseguito", "2");
        errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.esclusioneSoglia.letturaDatiGare1");
        throw new GestoreException(errMsgEvento, "esclusioneSoglia.letturaDatiGare1",e);
      }

      if(datiGare1!=null && datiGare1.size()>0){
        riparam = SqlManager.getValueFromVectorParam(datiGare1, 0).longValue();
        riparamCrit = SqlManager.getValueFromVectorParam(datiGare1, 1).longValue();
      }

      decimaliArrotondamento = tabellatiManager.getDescrTabellato("A1049", "1");

      //Costruzione della descrizione per la tracciatura degli eventi
      if(riparam==null)
        msgRiparam="no";
      else
        msgRiparam = tabellatiManager.getDescrTabellato("A1144", riparam.toString());
      if(riparamCrit!=null)
        msgRiparamCrit = tabellatiManager.getDescrTabellato("A1145", riparamCrit.toString());

      descrEvento+= "(Riparametrazione: " + msgRiparam;
      if(msgRiparamCrit!=null && !"".equals(msgRiparamCrit))
        descrEvento+= " - " + msgRiparamCrit;
      descrEvento+= ")";

      int riparamInt=0;
      if(riparam!=null)
        riparamInt = riparam.intValue();

      switch(riparamInt){
        case 0: case 3: {
          //Esclusione
          try {
            this.esclusione(ngara, tipo, status);
          } catch (SQLException e) {
            livEvento = 3;
            this.getRequest().setAttribute("calcoloEseguito", "2");
            errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.esclusioneSoglia.Esclusione");
            throw new GestoreException(errMsgEvento, "esclusioneSoglia.Esclusione",e);
          }
          break;
        }
        case 1: {
          //Esclusione
          try {
            this.esclusione(ngara, tipo, status);
          } catch (SQLException e) {
            livEvento = 3;
            this.getRequest().setAttribute("calcoloEseguito", "2");
            errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.esclusioneSoglia.Esclusione");
            throw new GestoreException(errMsgEvento, "esclusioneSoglia.Esclusione",e);
          }
          //Riparametrazione
          try {
            this.riparametrazione(ngara, riparamCrit, tipo);
          } catch (SQLException e) {
            livEvento = 3;
            this.getRequest().setAttribute("calcoloEseguito", "2");
            errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.esclusioneSoglia.Riparametrizzazione");
            throw new GestoreException(errMsgEvento, "esclusioneSoglia.Riparametrizzazione",e);
          }
          break;
        }
        case 2: {
          //Riparametrazione
          try {
            this.riparametrazione(ngara, riparamCrit, tipo);
          } catch (SQLException e) {
            livEvento = 3;
            this.getRequest().setAttribute("calcoloEseguito", "2");
            errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.esclusioneSoglia.Riparametrizzazione");
            throw new GestoreException(errMsgEvento, "esclusioneSoglia.Riparametrizzazione",e);
          }
          //Esclusione
          try {
            this.esclusione(ngara, tipo, status);
          } catch (SQLException e) {
            livEvento = 3;
            this.getRequest().setAttribute("calcoloEseguito", "2");
            errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.esclusioneSoglia.Esclusione");
            throw new GestoreException(errMsgEvento, "esclusioneSoglia.Esclusione",e);
          }
        }
      }

      livEvento = 1;
      errMsgEvento = "";
      this.getRequest().setAttribute("calcoloEseguito", "1");
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
        String messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
        this.getRequest().setAttribute("calcoloEseguito", "2");
      }
    }
  }


  /**
   *
   * @param ngara
   * @param riparamCrit
   * @param tipo
   * @throws SQLException
   * @throws GestoreException
   */
  private void riparametrazione(String ngara, Long riparamCrit, String tipo) throws SQLException, GestoreException{
    if(riparamCrit!=null){

      Long fase = new Long(5);
      if("2".equals(tipo))
        fase=new Long(6);

      int riparamCritInt = riparamCrit.intValue();
      switch(riparamCritInt){
        case 1:{
          //Riparam punteggio totale
          this.riparametrazionePunteggioTotale(ngara, fase,false);
          break;
        }
        case 2:{
          this.riparametrazionePunteggioCriteri(ngara, fase,false);
          break;
        }
        case 3:{
          // Riparam punteggio criteri e punteggio totale
          this.riparametrazionePunteggioCriteri(ngara, fase,true);
          this.riparametrazionePunteggioTotale(ngara, fase,true);
          break;
        }
      }
    }
  }

  /**
   * Viene calcolata la riparametrazione del punteggio totale
   * @param ngara
   * @param fase
   * @param punteggiIntermedi
   * @throws SQLException
   * @throws GestoreException
   */
  private void riparametrazionePunteggioTotale(String ngara, Long fase, boolean punteggiIntermedi) throws SQLException, GestoreException{
    String selectPunteggioTotMax = "select max(###) from DITG where ngara5=? and (fasgar is null or fasgar > ?)";
    String selectPunteggiGara = "select ###,dittao from ditg where ngara5=? and (fasgar is null or fasgar > ?) order by NUMORDPL asc,NOMIMO asc";
    String updateDitg = "update ditg set ###=? where ngara5=? and dittao=?";

    if(fase.longValue()==5){
      if(!punteggiIntermedi){
        selectPunteggioTotMax=selectPunteggioTotMax.replace("###", "puntec");
        selectPunteggiGara = selectPunteggiGara.replace("###", "puntec");
      }else{
        selectPunteggioTotMax=selectPunteggioTotMax.replace("###", "puntecrip1");
        selectPunteggiGara = selectPunteggiGara.replace("###", "puntecrip1");
      }
      updateDitg = updateDitg.replace("###", "puntecrip");
    }else{
      if(!punteggiIntermedi){
        selectPunteggioTotMax=selectPunteggioTotMax.replace("###", "puneco");
        selectPunteggiGara = selectPunteggiGara.replace("###", "puneco");
      }else{
        selectPunteggioTotMax=selectPunteggioTotMax.replace("###", "punecorip1");
        selectPunteggiGara = selectPunteggiGara.replace("###", "punecorip1");
      }
      updateDitg = updateDitg.replace("###", "punecorip");
    }
    Double punteggioTotMaxDitte = this.pgManager.getValoreImportoToDouble(this.sqlManager.getObject(selectPunteggioTotMax, new Object[]{ngara,fase}));
    if(punteggioTotMaxDitte==null){
      return ;
    }
    Double punteggioTotaleGara=null;
    if(fase.longValue()==5)
      punteggioTotaleGara = this.pgManager.getSommaPunteggioTecnico(ngara);
    else
      punteggioTotaleGara = this.pgManager.getSommaPunteggioEconomico(ngara);

    boolean riparametrazioneSenzaCalcolo=false;
    if(punteggioTotMaxDitte.doubleValue()!=0 && punteggioTotaleGara.equals(punteggioTotMaxDitte)){
      riparametrazioneSenzaCalcolo=true;
    }

    List<?> listaPunteggiDitteGara=sqlManager.getListVector(selectPunteggiGara, new Object[]{ngara,fase});
    if(listaPunteggiDitteGara!=null && listaPunteggiDitteGara.size()>0){
      Double punteggioDitta = null;
      double coeffCalcolatoDitta ;
      double punteggioCalcolato;
      String ditta=null;
      for(int i=0;i<listaPunteggiDitteGara.size();i++){
        punteggioDitta = SqlManager.getValueFromVectorParam(listaPunteggiDitteGara.get(i), 0).doubleValue();
        ditta = SqlManager.getValueFromVectorParam(listaPunteggiDitteGara.get(i), 1).stringValue();
        if(riparametrazioneSenzaCalcolo){
          punteggioCalcolato = punteggioDitta.doubleValue();
        }else if(punteggioTotMaxDitte.doubleValue()==0){
          punteggioCalcolato = new Long(0);
        }else{
          coeffCalcolatoDitta = punteggioDitta.doubleValue()/punteggioTotMaxDitte.doubleValue();
          coeffCalcolatoDitta =UtilityMath.round(coeffCalcolatoDitta, 9);
          punteggioCalcolato = coeffCalcolatoDitta * punteggioTotaleGara.doubleValue();
          if(this.decimaliArrotondamento!=null && !"".equals(decimaliArrotondamento))
            punteggioCalcolato = UtilityMath.round(punteggioCalcolato, new Long(this.decimaliArrotondamento).intValue());
        }
        this.sqlManager.update(updateDitg, new Object[]{new Double(punteggioCalcolato),ngara, ditta});

      }
    }
  }

  /**
   *
   * @param ngara
   * @param fase
   * @param punteggiIntermedi
   * @throws SQLException
   * @throws GestoreException
   */
  private void riparametrazionePunteggioCriteri(String ngara, Long fase, boolean punteggiIntermedi) throws SQLException, GestoreException{

    String selectDitteGara = "select dittao from ditg where ngara5=? and (fasgar is null or fasgar > ?) order by NUMORDPL asc,NOMIMO asc";
    String selectMaxCoeffi="select max(d.coeffi),d.necvan from dpun d, goev g, ditg t where d.ngara=? and d.ngara=g.ngara and"
        + " d.ngara=t.ngara5 and d.necvan=g.necvan and d.dittao=t.dittao and (g.livpar=1 or g.livpar=2) and (t.fasgar is null or t.fasgar > ?)"
        + " and g.tippar=? group by d.necvan";
    String selectMaxCoeffiConSottocriteri="select max(d.coeffirip),d.necvan from dpun d, goev g, ditg t where d.ngara=? and d.ngara=g.ngara and"
      + " d.ngara=t.ngara5 and d.necvan=g.necvan and d.dittao=t.dittao and (g.livpar=3) and (t.fasgar is null or t.fasgar > ?)"
      + " and g.tippar=? group by d.necvan";
    String selectDatiCriterio ="select d.coeffi,g.maxpun,d.dittao,d.punteg from dpun d,goev g , ditg t where d.ngara=? and d.ngara=g.ngara and"
        + " d.ngara=t.ngara5 and d.necvan=g.necvan and d.necvan=? and d.dittao=t.dittao and (t.fasgar is null or t.fasgar > ?)";
    String selectDatiCriterioConSottocriteri ="select d.coeffirip,g.maxpun,d.dittao,d.puntegrip from dpun d,goev g , ditg t where d.ngara=? and d.ngara=g.ngara and"
      + " d.ngara=t.ngara5 and d.necvan=g.necvan and d.necvan=? and d.dittao=t.dittao and (t.fasgar is null or t.fasgar > ?)";
    String selectGoevCriteriConSottoCriteri="select necvan,maxpun from goev where ngara=? and tippar=? and livpar=3";
    String selectSommaPuntegripCriteri="select sum(d.puntegrip) from dpun d, goev g where d.ngara=?  and d.dittao=? and "
        + " d.ngara=g.ngara and d.necvan=g.necvan and tippar=? and (g.livpar = 1 or g.livpar = 3)";
    String selectSommaPuntegripSottocriteri="select sum(d.puntegrip), d.dittao from dpun d, goev g where d.ngara=? and d.ngara=g.ngara and d.necvan=g.necvan and g.necvan1=? and g.livpar=2 group by dittao";
    String updateDitg = "update ditg set ###=? where ngara5=? and dittao=?";
    String updateDitgConIntermedi = "update ditg set ###=?, ***=? where ngara5=? and dittao=?";
    String updateDpun="update dpun set coeffirip=?, puntegrip=? where ngara=? and necvan=? and dittao=?";
    String updateDpunConIntermedi="update dpun set coeffirip=?, puntegrip=?, coeffirip1=?, puntegrip1=? where ngara=? and necvan=? and dittao=?";

    Long tippar=new Long(1);
    if(fase.longValue()==6){
      tippar=new Long(2);
      if(punteggiIntermedi){
        updateDitgConIntermedi = updateDitgConIntermedi.replace("###", "punecorip");
        updateDitgConIntermedi = updateDitgConIntermedi.replace("***", "punecorip1");
      }else{
        updateDitg = updateDitg.replace("###", "punecorip");
      }
    }else{
      if(punteggiIntermedi){
        updateDitgConIntermedi = updateDitgConIntermedi.replace("###", "puntecrip");
        updateDitgConIntermedi = updateDitgConIntermedi.replace("***", "puntecrip1");
      }else{
        updateDitg = updateDitg.replace("###", "puntecrip");
      }
    }

    List<?> listaCoeffiCriteri=sqlManager.getListVector(selectMaxCoeffi, new Object[]{ngara,fase,tippar});
    if(listaCoeffiCriteri!=null && listaCoeffiCriteri.size()>0){
      Double maxCoeffi=null;
      Long necvan=null;
      List<?> listaDatiCriterio = null;
      boolean riparametrazioneSenzaCalcolo=false;

      //Riparametrazione per i criteri con tippar=1 e 2
      for(int i=0;i<listaCoeffiCriteri.size();i++){
        maxCoeffi=SqlManager.getValueFromVectorParam(listaCoeffiCriteri.get(i), 0).doubleValue();
        necvan=SqlManager.getValueFromVectorParam(listaCoeffiCriteri.get(i), 1).longValue();
        if(maxCoeffi!=null){
          riparametrazioneSenzaCalcolo=false;
          if(maxCoeffi.doubleValue()==1)
            riparametrazioneSenzaCalcolo=true;
          listaDatiCriterio = this.sqlManager.getListVector(selectDatiCriterio, new Object[]{ngara,necvan,fase});
          if(listaDatiCriterio!=null && listaDatiCriterio.size()>0){
            Double coeff = null;
            Double maxpun = null;
            String ditta = null;
            double coeffirip=0;
            double puntegrip=0;
            Double punteg=null;
            for(int j=0;j<listaDatiCriterio.size();j++){
              coeff=SqlManager.getValueFromVectorParam(listaDatiCriterio.get(j), 0).doubleValue();
              maxpun=SqlManager.getValueFromVectorParam(listaDatiCriterio.get(j), 1).doubleValue();
              ditta=SqlManager.getValueFromVectorParam(listaDatiCriterio.get(j), 2).getStringValue();
              punteg = SqlManager.getValueFromVectorParam(listaDatiCriterio.get(j), 3).doubleValue();
              if(riparametrazioneSenzaCalcolo){
                coeffirip=coeff.doubleValue();
                puntegrip=punteg.doubleValue();
              }else{
                if(maxCoeffi.doubleValue()!=0){
                  coeffirip =coeff.doubleValue() /maxCoeffi.doubleValue();
                  coeffirip =UtilityMath.round(coeffirip, 9);
                }
                else
                  coeffirip =0;

                puntegrip = coeffirip * maxpun.doubleValue();
                if(this.decimaliArrotondamento!=null && !"".equals(decimaliArrotondamento))
                  puntegrip =UtilityMath.round(puntegrip, new Long(this.decimaliArrotondamento).intValue());
              }

              this.sqlManager.update(updateDpun,
                  new Object[]{new Double(coeffirip),new Double(puntegrip),ngara,necvan,ditta});

            }
          }
        }
      }
      //Se livpar = 3 sommo i valori riparametrati dei suoi sottocriteri
      List<?> listaCriteriConSottoCrit=null;
      listaCriteriConSottoCrit = this.sqlManager.getListVector(selectGoevCriteriConSottoCriteri, new Object[]{ngara,tippar});
      Double maxpun = null;
      if(listaCriteriConSottoCrit!=null && listaCriteriConSottoCrit.size()>0){
        necvan=null;
        maxpun = null;
        Double sommaPuntegrip=null;
        double coeffirip=0;
        String ditta=null;
        List<?> listaSommaSottocriteri = null;
        for(int j=0;j<listaCriteriConSottoCrit.size();j++){
          necvan = SqlManager.getValueFromVectorParam(listaCriteriConSottoCrit.get(j), 0).longValue();
          maxpun = SqlManager.getValueFromVectorParam(listaCriteriConSottoCrit.get(j), 1).doubleValue();
          listaSommaSottocriteri = this.sqlManager.getListVector(selectSommaPuntegripSottocriteri, new Object[]{ngara,necvan});
          for(int i=0;i<listaSommaSottocriteri.size();i++){
            sommaPuntegrip = this.pgManager.getValoreImportoToDouble(SqlManager.getValueFromVectorParam(listaSommaSottocriteri.get(i), 0).getValue());
            ditta = SqlManager.getValueFromVectorParam(listaSommaSottocriteri.get(i), 1).stringValue();
            if(sommaPuntegrip==null)
              sommaPuntegrip = new Double(0);
            coeffirip = new Double(sommaPuntegrip) / maxpun;
            coeffirip =UtilityMath.round(coeffirip, 9);
            this.sqlManager.update(updateDpunConIntermedi,new Object[]{new Double(coeffirip),sommaPuntegrip,new Double(coeffirip),sommaPuntegrip,ngara,necvan,ditta});
          }
        }
      }
      
      //Riparametro i totali appena calcolati nei criteri con livpar = 3 (criteri con sottocriteri)
      listaCriteriConSottoCrit=sqlManager.getListVector(selectMaxCoeffiConSottocriteri, new Object[]{ngara,fase,tippar});
      if(listaCriteriConSottoCrit!=null && listaCriteriConSottoCrit.size()>0){
        maxCoeffi=null;
        necvan=null;
        listaDatiCriterio = null;
        riparametrazioneSenzaCalcolo=false;
        
        for(int i=0;i<listaCriteriConSottoCrit.size();i++){
          maxCoeffi=SqlManager.getValueFromVectorParam(listaCriteriConSottoCrit.get(i), 0).doubleValue();
          necvan=SqlManager.getValueFromVectorParam(listaCriteriConSottoCrit.get(i), 1).longValue();
          if(maxCoeffi!=null){
            riparametrazioneSenzaCalcolo=false;
            if(maxCoeffi.doubleValue()==1)
              riparametrazioneSenzaCalcolo=true;
            listaDatiCriterio = this.sqlManager.getListVector(selectDatiCriterioConSottocriteri, new Object[]{ngara,necvan,fase});
            if(listaDatiCriterio!=null && listaDatiCriterio.size()>0){
              Double coeff = null;
              maxpun = null;
              String ditta = null;
              double coeffirip=0;
              double puntegrip=0;
              Double punteg=null;
              for(int j=0;j<listaDatiCriterio.size();j++){
                coeff=SqlManager.getValueFromVectorParam(listaDatiCriterio.get(j), 0).doubleValue();
                maxpun=SqlManager.getValueFromVectorParam(listaDatiCriterio.get(j), 1).doubleValue();
                ditta=SqlManager.getValueFromVectorParam(listaDatiCriterio.get(j), 2).getStringValue();
                punteg = SqlManager.getValueFromVectorParam(listaDatiCriterio.get(j), 3).doubleValue();
                if(riparametrazioneSenzaCalcolo){
                  coeffirip=coeff.doubleValue();
                  puntegrip=punteg.doubleValue();
                }else{
                  if(maxCoeffi.doubleValue()!=0){
                    coeffirip =coeff.doubleValue() /maxCoeffi.doubleValue();
                    coeffirip =UtilityMath.round(coeffirip, 9);
                  }
                  else
                    coeffirip =0;

                  puntegrip = coeffirip * maxpun.doubleValue();
                  if(this.decimaliArrotondamento!=null && !"".equals(decimaliArrotondamento))
                    puntegrip =UtilityMath.round(puntegrip, new Long(this.decimaliArrotondamento).intValue());
                }

                this.sqlManager.update(updateDpun,
                    new Object[]{new Double(coeffirip),new Double(puntegrip),ngara,necvan,ditta});
              }
            }
          }
        }
      }
      //Aggiorno valori in ditg, sommando i punteggi precedentemente riparamentrati per ogni criterio
      List<?> listaDitteGara=this.sqlManager.getListVector(selectDitteGara, new Object[]{ngara,fase});
      if(listaDitteGara!=null && listaDitteGara.size()>0){
        String ditta=null;
        double punteggioTotaleDitta=0;
        Double punteggioCriterio = null;
        
        for(int i=0;i<listaDitteGara.size();i++){
          punteggioTotaleDitta=0;
          ditta = SqlManager.getValueFromVectorParam(listaDitteGara.get(i), 0).getStringValue();
          punteggioCriterio = this.pgManager.getValoreImportoToDouble(this.sqlManager.getObject(selectSommaPuntegripCriteri, new Object[]{ngara,ditta,tippar}));
          if(punteggioCriterio!=null)
            punteggioTotaleDitta+=punteggioCriterio.doubleValue();

          if(this.decimaliArrotondamento!=null && !"".equals(decimaliArrotondamento))
            punteggioTotaleDitta = UtilityMath.round(punteggioTotaleDitta, new Long(this.decimaliArrotondamento).intValue());
          //this.sqlManager.update(updateDitg, new Object[]{new Double(punteggioTotaleDitta),ngara,ditta});
          if(punteggiIntermedi)
            this.sqlManager.update(updateDitgConIntermedi, new Object[]{new Double(punteggioTotaleDitta),new Double(punteggioTotaleDitta),ngara,ditta});
          else
            this.sqlManager.update(updateDitg, new Object[]{new Double(punteggioTotaleDitta),ngara,ditta});
        }
      }
    }
  }
  private void esclusione(String ngara, String tipo,TransactionStatus status) throws SQLException, GestoreException{
    //Controllo punteggi totali
    Double sogliaMinima = null;
    String codgar = null;
    String selectGare1 ="select ###,codgar1 from gare1 where ngara=?";
    if("1".equals(tipo))
      selectGare1 = selectGare1.replace("###", "mintec");
    else
      selectGare1 = selectGare1.replace("###", "mineco");

    Vector<?> datiGare1 = this.sqlManager.getVector(selectGare1, new Object[]{ngara});
    if(datiGare1!=null && datiGare1.size()>0){
      sogliaMinima = SqlManager.getValueFromVectorParam(datiGare1, 0).doubleValue();
      codgar = SqlManager.getValueFromVectorParam(datiGare1, 1).getStringValue();
    }
    ArrayList<String> elencoDitte= new ArrayList<String>();
    boolean esitoControlloPunteggiTotali = this.mepaManager.esitoControlloPunteggiTotaliDitteSogliaMinima(ngara, sogliaMinima, new Long(tipo),elencoDitte);
    boolean esitoControlloPunteggiCriteri[]=this.mepaManager.esitoControlloPunteggiCriteriDitteSogliaMinima(ngara,new Long(tipo),elencoDitte);
    Long fase= new Long(5);
    if("2".equals(tipo))
      fase= new Long(6);

    if(!esitoControlloPunteggiTotali || !esitoControlloPunteggiCriteri[0]){
      //esclusione delle ditte che non hanno superato il controllo
      GestoreDITG gestoreDITG= new GestoreDITG();
      gestoreDITG.setRequest(this.getRequest());
      DataColumn[] columnsV_DITGAMMIS= new DataColumn[3];
      columnsV_DITGAMMIS[1] = new DataColumn("V_DITGAMMIS.AMMGAR",new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(2)));
      columnsV_DITGAMMIS[2] = new DataColumn("V_DITGAMMIS.MOTIVESCL",new JdbcParametro(JdbcParametro.TIPO_NUMERICO,new Long(104)));
      String annof = (String)this.sqlManager.getObject("select annoff from detmot where moties=?", new Object[]{new Long(104)});
      columnsV_DITGAMMIS[0] = new DataColumn("V_DITGAMMIS.DETMOTESCL",new JdbcParametro(JdbcParametro.TIPO_TESTO,annof));
      for (String ditta : elencoDitte) {
        gestoreDITG.gestioneDITGAMMIS(codgar, ngara, ditta, fase, columnsV_DITGAMMIS, status);
        gestoreDITG.aggiornaStatoAmmissioneDITG(codgar, ngara, ditta, fase, columnsV_DITGAMMIS, status);
        if("1".equals(tipo))
          this.sqlManager.update("update ditg set ribauo=null,puneco=null,impoff=null,impsicazi=null,impperm=null,impcano=null,"
              + "fasgar=?, ammgar=? where ngara5=? and dittao=?", new Object[]{fase, new Long(2), ngara, ditta});
        else
          this.sqlManager.update("update ditg set fasgar=?, ammgar=? where ngara5=? and dittao=?", new Object[]{fase, new Long(2), ngara, ditta});
      }
    }
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
