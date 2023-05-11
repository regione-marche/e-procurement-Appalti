/*
 * Created on 26/feb/09
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
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Gestore non standard per l'aggiornamento della fase gara
 *
 * @author Stefano.Sabbadin
 */
public class GestoreSetFase extends AbstractGestoreEntita {

  /** Logger */
  static Logger logger = Logger.getLogger(GestoreSetFase.class);

  @Override
  public String getEntita() {
    return "GARE";
  }

  public GestoreSetFase() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestoreSetFase(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer dataColumnContainer)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer dataColumnContainer)
      throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {


    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        this.getServletContext(), TabellatiManager.class);


    //variabili per tracciatura eventi
    String messageKey = null;
    int livEvento = 3;
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");

    String chiusuraAperturaFasiRicezione = dataColumnContainer.getString("CHIUSURA_FASI_RICEZIONE");
    String codiceGara = dataColumnContainer.getString("GARE.NGARA");
    String gestioneSoglia = dataColumnContainer.getString("GESTIONE_SOGLIA");
    String isGaraDLGS2017 = dataColumnContainer.getString("ISGARADLGS2017");



    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);

    MEPAManager mepaManager = (MEPAManager) UtilitySpring.getBean("mepaManager",
        this.getServletContext(), MEPAManager.class);

    String codEvento="GA_ATTIVA_FASE_DOCAMM";
    String descrEvento = "Attivazione fase apertura doc. amministrativa";
    try{

      try {
        String nobustamm= (String)this.sqlManager.getObject("select nobustamm from torn where codgar=?", new Object[] {"$"+codiceGara});
        if("1".equals(nobustamm)) {
          String select="select g.modlicg,g1.valtec from gare g,gare1 g1  where g.ngara=? and g1.ngara=g.ngara";
          Vector<JdbcParametro> datiGara = sqlManager.getVector(select, new Object[] { codiceGara });
          Long modlicg = null;
          String valtec = null;
          if (datiGara != null && datiGara.size() > 0){
            modlicg = (Long) (datiGara.get(0)).getValue();
            valtec = (String) (datiGara.get(1)).getValue();
          }
          pgManager.updateChiusuraAperturaFasiRicezione(codiceGara, chiusuraAperturaFasiRicezione,false,modlicg,valtec);
          if("ATTIVA".equals(chiusuraAperturaFasiRicezione)) {
            codEvento="GA_ATTIVA_FASE_OFFERTE";
            descrEvento = "Attivazione fase apertura offerte";
          }
        }else {
          pgManager.updateChiusuraAperturaFasiRicezione(codiceGara, chiusuraAperturaFasiRicezione,true,null,null);
          if("ATTIVA".equals(chiusuraAperturaFasiRicezione)){
            mepaManager.impostaComunicazioniAScartate(codiceGara, "DOC_AMM");
          }
        }

        livEvento = 1;
        errMsgEvento = "";
      } catch (SQLException s) {
        livEvento = 3;
        messageKey = "errors.gestoreException.update.updateFaseGare";
        errMsgEvento = this.resBundleGenerale.getString(messageKey);
        this.getRequest().setAttribute("CHIUSURA_FASI_RICEZIONE", "");
        throw new GestoreException(
            "Errore nell'aggiornamento del campo GASE.FASGAR, GARE.STEPGAR durante " +
            "l'operazione attivazione/disattivazione delle fasi di ricezione " +
            "delle domande e offerte", "updateFaseGare", s);
      }

    }finally{
      if("ATTIVA".equals(chiusuraAperturaFasiRicezione)){
        //Tracciatura eventi
        try {
          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(codiceGara);
          logEvento.setCodEvento(codEvento);
          logEvento.setDescr(descrEvento);
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          messageKey = "errors.logEventi.inaspettataException";
          logger.error(this.resBundleGenerale.getString(messageKey), le);
        }
      }
    }

    //Gestione del metodo di calcolo soglia, la tracciatura va fatta per ogni lotto
    if("MAN".equals(gestioneSoglia) || "AUTO".equals(gestioneSoglia)){
      Long metsoglia = null;
      Double metcoeff = null;
      String applicaTuttiLotti ="1";

      boolean isGaraLottiConOffertaUnica = false;
      String tmp = this.getRequest().getParameter("isGaraLottiConOffertaUnica");
      if(tmp == null)
          tmp = (String) this.getRequest().getAttribute("isGaraLottiConOffertaUnica");

      if("true".equals(tmp))
          isGaraLottiConOffertaUnica = true;

      AggiudicazioneManager aggiudicazioneManager = (AggiudicazioneManager) UtilitySpring.getBean("aggiudicazioneManager",
          this.getServletContext(), AggiudicazioneManager.class);

      //Estrazione dei lotti
      String selectLotti="";

      if("MAN".equals(gestioneSoglia)){
        //Gestione modalita automatica
        metsoglia = dataColumnContainer.getLong("METSOGLIA");
        metcoeff = dataColumnContainer.getDouble("METCOEFF");
        selectLotti ="select ngara from gare where ";
        if(isGaraLottiConOffertaUnica)
          selectLotti+=" codgar1=? and codgar1!=ngara and (modlicg= 13 or modlicg= 14) ";
        else
          selectLotti+=" ngara=?";
      }else if("AUTO".equals(gestioneSoglia)){
        //gestione modalità automatica
        selectLotti+="select g.ngara from gare g, gare1 g1 where ";
        if(isGaraLottiConOffertaUnica)
          selectLotti+=" g.codgar1=? and g.ngara=g1.ngara and g.codgar1!=g.ngara and (modlicg= 13 or modlicg= 14) and metsoglia is null";
        else
          selectLotti+="g.ngara=? and g.ngara=g1.ngara and metsoglia is null";
        if(isGaraLottiConOffertaUnica)
          applicaTuttiLotti = dataColumnContainer.getString("APPLICA_TUTTI_LOTTI");
      }


      List lotti=null;;
      try {
        lotti = this.sqlManager.getListVector(selectLotti, new Object[]{codiceGara});
      } catch (SQLException e) {
        this.getRequest().setAttribute("CHIUSURA_FASI_RICEZIONE", "");
        throw new GestoreException(
            "Errore nell'impostazione del metodo di calcolo della soglia di anomalia", "updateFaseGare", e);
      }
      if(lotti!=null && lotti.size()>0){
        Object valori[] = null;
        String lotto = null;
        String update="update gare1 set metsoglia=?, metcoeff=? where ngara=?";
        for(int i=0;i<lotti.size();i++){
          try{
            lotto=SqlManager.getValueFromVectorParam(lotti.get(i), 0).stringValue();
            if("AUTO".equals(gestioneSoglia) && (i==0 || "2".equals(applicaTuttiLotti))){
              valori = aggiudicazioneManager.getMetodoCalcoloSoglia(isGaraDLGS2017,"",new Long("0"));
              metsoglia = (Long)valori[0];
              if(valori[1]!=null)
                metcoeff = (Double)valori[1];
              else
                metcoeff = null;
            }
            this.sqlManager.update(update, new Object[]{metsoglia, metcoeff, lotto});
            livEvento = 1;
            errMsgEvento = "";
          } catch (SQLException e) {
            livEvento = 3;
            messageKey = "errors.gestoreException.update.setMetodoCalcoloSoglia";
            errMsgEvento = this.resBundleGenerale.getString(messageKey);
            //this.getRequest().setAttribute("CHIUSURA_FASI_RICEZIONE", "");
          } catch (GestoreException e) {
            livEvento = 3;
            messageKey = "errors.gestoreException.update.setMetodoCalcoloSoglia";
            errMsgEvento = this.resBundleGenerale.getString(messageKey);

          } finally{

            //Tracciatura eventi
            try {
              String descr="Assegnazione metodo calcolo anomalia ";
              String descrizioneMetsoglia = "";
              if(metsoglia!=null){
                descrizioneMetsoglia = tabellatiManager.getDescrTabellato("A1126", metsoglia.toString());
                descr+="(" + descrizioneMetsoglia;
                if(metcoeff!=null){
                  String metcoeffString = Double.toString(metcoeff.doubleValue());
                  if(metcoeffString.indexOf(".")>0)
                    metcoeffString=metcoeffString.replace(".", ",");
                  descr += " - " + metcoeffString;
                }
                if("true".equals(isGaraDLGS2017))
                  descr+=" - adeguato DLgs.56/2017";
                descr+=")";
              }

              LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
              logEvento.setLivEvento(livEvento);
              logEvento.setOggEvento(lotto);
              logEvento.setCodEvento("GA_METODO_ANOMALIA");
              logEvento.setDescr(descr);
              logEvento.setErrmsg(errMsgEvento);
              LogEventiUtils.insertLogEventi(logEvento);
            } catch (Exception le) {
              messageKey = "errors.logEventi.inaspettataException";
              logger.error(this.resBundleGenerale.getString(messageKey), le);
            }
          }
        }
      }
    }

 }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer)
      throws GestoreException {
  }

}