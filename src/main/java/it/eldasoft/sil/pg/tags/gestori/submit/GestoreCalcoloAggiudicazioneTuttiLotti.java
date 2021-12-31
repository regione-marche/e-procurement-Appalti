/*
 * Created on 23/03/16
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
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestoreCalcoloAggiudicazioneTuttiLotti extends
    AbstractGestoreEntita {

  /** Logger */
  static Logger logger = Logger.getLogger(GestoreCalcoloAggiudicazioneTuttiLotti.class);

  private TabellatiManager tabellatiManager;

  @Override
  public String getEntita() {
    return "GARE";
  }

  public GestoreCalcoloAggiudicazioneTuttiLotti() {
    super(false);
}

public GestoreCalcoloAggiudicazioneTuttiLotti(boolean isGestoreStandard) {
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
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    AggiudicazioneManager aggiudicazioneManager = (AggiudicazioneManager) UtilitySpring.getBean(
        "aggiudicazioneManager", this.getServletContext(),
        AggiudicazioneManager.class);

    PgManager pgManager = (PgManager) UtilitySpring.getBean(
        "pgManager", this.getServletContext(),
        PgManager.class);

    tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        this.getServletContext(), TabellatiManager.class);

    String ngara = impl.getString("NGARA");

    this.getRequest().setAttribute("NGARA", ngara);
    String messageKey = null;
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
    int livEvento = 3;
    String codevento = null;
    String descr = null;
    String descrDittap = null;
    String isGaraDLGS2016Auto = impl.getString("ISGARADLGS2016AUTO");
    String isGaraDLGS2016 = impl.getString("ISGARADLGS2016");
    String isGaraDLGS2017 = impl.getString("ISGARADLGS2017");
    String isGaraDL2019 = impl.getString("ISGARADL2019");
    String legregsicVisibile = impl.getString("LEGREGSICVISIBILE");
    String legregsic = "";
    int numeroVoceParametroA1135=1;

    if("true".equals(legregsicVisibile))
      legregsic = impl.getString("LEGREGSIC");

    List listaMessaggi = new ArrayList();
    boolean calcoloSogliaEseguito = true;
    boolean erroreSelect = false;
    Double importoSogliaPerGara = null;
    Long tipgenGara = null;
    Timestamp dpubavg = null;
    Timestamp dinvit = null;
    Long iterga = null;
    Double imptor = null;
    //Si procede con il calcolo della soglia di anomalia e dell'aggiudicazione per ogni lotto
    List listaLotti;
    try {
      listaLotti = this.sqlManager.getListVector("select g.ngara, g.modlicg, g.calcsoang, g.dittap, g1.metsoglia, g1.metcoeff, g1.riptec, g1.ripeco, g.modastg, g.impapp " +
      		"from gare g, gare1 g1 where g.codgar1=? and g.ngara=g1.ngara and g.ngara!=g.codgar1 order by g.ngara", new Object[]{ngara});

      //Individua parametro 'numero minimo ditte per calcolo soglia', differenziato per gare sopra e sotto soglia
      if("true".equals(isGaraDLGS2016) || "true".equals(isGaraDLGS2017) || "true".equals(isGaraDL2019) || "1".equals(legregsic)){
        Vector<?> datiGara = this.sqlManager.getVector("select t.imptor, t.iterga, t.tipgen, t.dpubav, t.dinvit " +
            "from torn t where t.codgar=?", new Object[]{ngara});
        imptor = SqlManager.getValueFromVectorParam(datiGara, 0).doubleValue();
        iterga =  SqlManager.getValueFromVectorParam(datiGara, 1).longValue();
        tipgenGara =  SqlManager.getValueFromVectorParam(datiGara, 2).longValue();
        dpubavg = SqlManager.getValueFromVectorParam(datiGara, 3).dataValue();
        dinvit = SqlManager.getValueFromVectorParam(datiGara, 4).dataValue();
        Object[] soglia = aggiudicazioneManager.getImportoSogliaPerGara(tipgenGara,dpubavg,dinvit,iterga.intValue(),1,ngara);
        importoSogliaPerGara = (Double) soglia[0];
        if(imptor==null){
          imptor = new Double(0);
        }
        if (imptor < importoSogliaPerGara.doubleValue())
          numeroVoceParametroA1135=2;
      }
    } catch (SQLException e) {
        erroreSelect = true;
        this.getRequest().setAttribute("RISULTATO", "ERRORI");
        messageKey = "errors.gestoreException.*.aggiudicazioneTuttiLotti";
        errMsgEvento = this.resBundleGenerale.getString(messageKey);
        throw new GestoreException(
            "Errore durante il calcolo della funzione di aggiudicazione di tutti i lotti",
            "aggiudicazioneTuttiLotti", e);
    } catch (GestoreException e) {
        erroreSelect = true;
        this.getRequest().setAttribute("RISULTATO", "ERRORI");
        messageKey = "errors.gestoreException.*.aggiudicazioneFaseA";
        errMsgEvento = this.resBundleGenerale.getString(messageKey);
        throw e;
    } finally{
      //Tracciatura eventi
      try {
        if(erroreSelect){
          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(ngara);
          logEvento.setCodEvento("GA_CALCOLO_AGG");
          logEvento.setDescr("Calcolo soglia anomalia o della graduatoria");
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        }
      } catch (Exception le) {
        messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
      }
    }

    String lotto = null;

      if(listaLotti!=null && listaLotti.size()>0){
        DataColumnContainer campiLotti = null;
        String[] esito = null;
        Long precut = impl.getLong("PRECUT");

        AbstractGestoreEntita gestoreGARE1 = new DefaultGestoreEntita("GARE1", this.getRequest());
        AbstractGestoreEntita gestoreGARE = new DefaultGestoreEntita("GARE", this.getRequest());
        String intestazioneMessaggio=null;
        boolean aggiudicazioneSuccesso= true;
        Long metsoglia = null;
        Double metcoeff = null;
        if(!"true".equals(isGaraDLGS2016Auto)){
          metsoglia = impl.getLong("METSOGLIA");
          metcoeff = impl.getDouble("METCOEFF");
        }else {
          Object valori[] = aggiudicazioneManager.getMetodoCalcoloSoglia(isGaraDLGS2017);
          metsoglia = (Long)valori[0];
          if(valori[1]!=null)
            metcoeff = (Double)valori[1];
        }

        Long metpunti =null;
        if(impl.isColumn("METPUNTI"))
          metpunti = impl.getLong("METPUNTI");

        String dittap= null;
        String descrizioneMetsoglia=null;
        Boolean DLG2016CalcoloGraduatoria = null;
        Boolean valoreRitornoControllo = null;
        Long riptecLotto=null;
        Long ripecoLotto=null;
        Long metpuntiLotto=null;
        String legregsicLotto=null;
        Long modastg = null;
        Long ditteAmmesse = null;
        Long initEscauto = null;
        for (int row = 0; row < listaLotti.size(); row++) {
          Long metsogliaLotto = null;
          Double metcoeffLotto = null;
          metpuntiLotto=null;
          codevento = "GA_CALCOLO_AGG";
          descr = "Calcolo soglia anomalia o della graduatoria";
          livEvento = 3;
          errMsgEvento = "";
          calcoloSogliaEseguito=true;

          lotto = SqlManager.getValueFromVectorParam(listaLotti.get(row), 0).getStringValue();
          Long modlicg = SqlManager.getValueFromVectorParam(listaLotti.get(row), 1).longValue();
          String calcsoang = SqlManager.getValueFromVectorParam(listaLotti.get(row), 2).getStringValue();
          dittap = SqlManager.getValueFromVectorParam(listaLotti.get(row), 3).getStringValue();
          //Controllo numero minimo ditte rispetto al valore in A1135 per DLG2016
          DLG2016CalcoloGraduatoria =  new Boolean(false);
          if(!new Long(6).equals(modlicg) && !"2".equals(calcsoang)){
            legregsicLotto = legregsic;
          }else{
            legregsicLotto = null;
          }
          modastg=SqlManager.getValueFromVectorParam(listaLotti.get(row), 8).longValue();

          try {
            if("true".equals(isGaraDLGS2016) || "true".equals(isGaraDLGS2017) || "true".equals(isGaraDL2019) || "1".equals(legregsicLotto)){
              valoreRitornoControllo = (Boolean)aggiudicazioneManager.controlloNumDitteAmmesseSopraSoglia(lotto,"A1135", numeroVoceParametroA1135)[0];
              DLG2016CalcoloGraduatoria = new Boolean(!valoreRitornoControllo.booleanValue());
            }
           } catch (SQLException e) {
             calcoloSogliaEseguito = false;
             messageKey = "errors.gestoreException.*.aggiudicazioneFaseA";
             errMsgEvento = this.resBundleGenerale.getString(messageKey);
             livEvento = 3;
             throw new GestoreException(
                      "Errore durante il calcolo dell'aggiudicazione su tutti i lotti della gara",
                      "aggiudicazioneTuttiLotti", e);
            }

          if(DLG2016CalcoloGraduatoria.booleanValue() && "1".equals(legregsicLotto))
            legregsicLotto=null;


          metsogliaLotto = SqlManager.getValueFromVectorParam(listaLotti.get(row), 4).longValue();
          metcoeffLotto =  SqlManager.getValueFromVectorParam(listaLotti.get(row), 5).doubleValue();
          if(metsogliaLotto==null && metsoglia!=null && !DLG2016CalcoloGraduatoria.booleanValue()){
            metsogliaLotto = metsoglia;
            metcoeffLotto = metcoeff;
          }

          if(modlicg!=null && modlicg.longValue()==6){
            riptecLotto = SqlManager.getValueFromVectorParam(listaLotti.get(row), 6).longValue();
            ripecoLotto = SqlManager.getValueFromVectorParam(listaLotti.get(row), 7).longValue();
            if((riptecLotto!= null && (riptecLotto.longValue()==1 || riptecLotto.longValue()==2)) || (ripecoLotto!= null && (ripecoLotto.longValue()==1 || ripecoLotto.longValue()==2)))
              metpuntiLotto = metpunti;
          }




          campiLotti = new DataColumnContainer(this.sqlManager, "GARE", "select * from GARE where ngara=?", new Object[] {lotto});
          campiLotti.getColumn("GARE.NGARA").setChiave(true);
          campiLotti.setValue("GARE.PRECUT", precut);
          campiLotti.addColumn("GARE1.NGARA", JdbcParametro.TIPO_TESTO,
              campiLotti.getString("GARE.NGARA"));
          campiLotti.getColumn("GARE1.NGARA").setChiave(true);
          campiLotti.getColumn("GARE.NGARA").setObjectOriginalValue(
              campiLotti.getString("GARE.NGARA"));
          campiLotti.getColumn("GARE1.NGARA").setObjectOriginalValue(
              campiLotti.getString("GARE.NGARA"));
          campiLotti.addColumn("GARE1.LEGREGSIC", JdbcParametro.TIPO_TESTO,legregsicLotto);
          campiLotti.addColumn("GARE1.METSOGLIA", JdbcParametro.TIPO_NUMERICO,metsogliaLotto);
          campiLotti.addColumn("GARE1.METCOEFF", JdbcParametro.TIPO_DECIMALE,metcoeffLotto);
          campiLotti.getColumn("GARE1.METSOGLIA").setObjectOriginalValue(new Long(-100000000));
          campiLotti.getColumn("GARE1.METCOEFF").setObjectOriginalValue(new Double(-100000000));
          campiLotti.addColumn("GARE1.METPUNTI", JdbcParametro.TIPO_NUMERICO,metpuntiLotto);
          campiLotti.getColumn("GARE1.METPUNTI").setObjectOriginalValue(new Double(-100000000));

          //Se si ripete più volte di seguito il calcolo vi sono problemi nella valorizzazione dei campi:
          //DITTAP, IAGPRO e RIBPRO
          campiLotti.getColumn("GARE.DITTAP").setObjectOriginalValue("");
          campiLotti.getColumn("GARE.IAGPRO").setObjectOriginalValue(new Double(-100000000));
          campiLotti.getColumn("GARE.RIBPRO").setObjectOriginalValue(new Double(-100000000));

          //Gestione ESCAUTO
          initEscauto = null;
          if(modastg!=null && modastg.longValue()==1 && !new Long(6).equals(modlicg) && "1".equals(calcsoang) && !DLG2016CalcoloGraduatoria.booleanValue()){
            ditteAmmesse = aggiudicazioneManager.conteggioDitte(lotto,"AMMGAR <> '2' and (MOTIES < 99 or MOTIES is null)");
            if (imptor.doubleValue() < importoSogliaPerGara.doubleValue()){
              if(ditteAmmesse!=null && ditteAmmesse.longValue() < 10)
                initEscauto = new Long(1);
              else if(ditteAmmesse!=null && ditteAmmesse.longValue() >= 10)
                initEscauto = new Long(2);
            }else if(imptor >= importoSogliaPerGara.doubleValue())
              initEscauto = new Long(4);
            campiLotti.addColumn("GARE1.ESCAUTOFIT", initEscauto);
          }

          Long critlicg = campiLotti.getColumn("GARE.CRITLICG").getValue().longValue();
          String descCritlicg = "";
          if(critlicg != null){
            descCritlicg = tabellatiManager.getDescrTabellato("A2081", critlicg.toString());
          }

          intestazioneMessaggio = "Lotto " +   campiLotti.getString("GARE.NGARA") + " - " + descCritlicg;
          if(metsogliaLotto!=null && ("true".equals(isGaraDLGS2016) || "true".equals(isGaraDLGS2017)) && !"1".equals(legregsicLotto) && ((new Long(13)).equals(modlicg) || (new Long(14)).equals(modlicg))){
            descrizioneMetsoglia = tabellatiManager.getDescrTabellato("A1126", metsogliaLotto.toString());
            intestazioneMessaggio += " - " + descrizioneMetsoglia;
          }
          intestazioneMessaggio += ": ";
          listaMessaggi.add(intestazioneMessaggio);



          try {
            if(dittap == null || "".equals(dittap))
              esito = aggiudicazioneManager.aggiudicazioneFaseA(campiLotti);
            else{
              //La ditta è stata aggiudicata quindi non va eseguito nessun calcolo, ma solo la tracciatura nel log
              listaMessaggi.add(" - Lotto aggiudicato in precedenza. Nessun calcolo eseguito.");
              calcoloSogliaEseguito = false;
              errMsgEvento = "Lotto aggiudicato in precedenza. Nessun calcolo eseguito.";
              livEvento = 2;
            }

          }catch (GestoreException e) {
            if("aggiudicazioneFaseA.ControlloDitteAmmesse".equals(e.getCodice()) ||
                "aggiudicazioneFaseA.ControlloTipoProcedura".equals(e.getCodice()) ||
                "aggiudicazioneFaseA.ControlloModalitaAggiudicazione".equals(e.getCodice())){
              messageKey = e.getCodice();
              errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*." +messageKey);
              errMsgEvento += ". Calcolo aggiudicazione non eseguito.";
              listaMessaggi.add(" - " + errMsgEvento);
              calcoloSogliaEseguito = false;
              livEvento = 3;
            }else {
              calcoloSogliaEseguito = false;
              messageKey = "errors.gestoreException.*.aggiudicazioneFaseA";
              errMsgEvento = this.resBundleGenerale.getString(messageKey);
              livEvento = 3;
              throw e;
            }
          }finally{
            //Tracciatura eventi per calcolo soglia anomalia nel caso di errore
            try {
              if(erroreSelect || !calcoloSogliaEseguito){
                LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
                logEvento.setLivEvento(livEvento);
                logEvento.setOggEvento(lotto);
                logEvento.setCodEvento("GA_CALCOLO_AGG");
                String descrLog = "Calcolo soglia anomalia o della graduatoria";
                descrLog+= "("+ descCritlicg;
                if(metsogliaLotto!=null && ("true".equals(isGaraDLGS2016) || "true".equals(isGaraDLGS2017)) && !"1".equals(legregsicLotto) && ((new Long(13)).equals(modlicg) || (new Long(14)).equals(modlicg)))
                  descrLog += " - " + descrizioneMetsoglia ;
                if("true".equals(isGaraDLGS2017) )
                  descrLog+=" - adeguato DLgs.56/2017";
                if("true".equals(isGaraDL2019) || "1".equals(legregsicLotto)){
                  if("1".equals(legregsicLotto))
                    descrLog+=" - " + AggiudicazioneManager.stringaSogliaNorma_REG_SIC;
                  else{
                    if (DLG2016CalcoloGraduatoria.booleanValue())
                      descrLog+=" - " + AggiudicazioneManager.stringaSogliaNorma_DL_32_2019_G;
                    else
                      descrLog+=" - " + AggiudicazioneManager.stringaSogliaNorma_DL_32_2019_S;
                  }
                }

                descrLog+=")";
                if(initEscauto!=null){
                  descrLog += ". Esclusione automatica off.anomale prevista e " + tabellatiManager.getDescrTabellato("A1165", initEscauto.toString());
                }

                logEvento.setDescr(descrLog);
                logEvento.setErrmsg(errMsgEvento);
                LogEventiUtils.insertLogEventi(logEvento);
              }
            } catch (Exception le) {
              messageKey = "errors.logEventi.inaspettataException";
              logger.error(this.resBundleGenerale.getString(messageKey), le);
            }
          }

          //Gestione GARE1
          if(calcoloSogliaEseguito){
            try{
            if( "true".equals(legregsicVisibile) && !new Long(6).equals(modlicg) && !"2".equals(calcsoang)){
              campiLotti.getColumn("GARE1.LEGREGSIC").setOriginalValue(null);
            }else{
              campiLotti.setValue("GARE1.LEGREGSIC", null);
              campiLotti.getColumn("GARE1.LEGREGSIC").setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO,"1"));
            }
            try{
              gestoreGARE1.update(status, campiLotti);
            }catch (GestoreException e) {
              messageKey = "errors.gestoreException.*.aggiudicazioneFaseA";
              errMsgEvento = this.resBundleGenerale.getString(messageKey);
              livEvento = 3;
              throw e;
            }
            if (esito!=null && esito.length > 0) {
              if ("ControlloNumeroDitteNonSuperato".equals(esito[0])){
                messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloNumeroDitteAmmesse";
                errMsgEvento = this.resBundleGenerale.getString(messageKey);
                errMsgEvento = errMsgEvento.replace("{0}", esito[1]);
                listaMessaggi.add(" - " + errMsgEvento);
                livEvento = 2;
              } else if("ControlloNumeroDitteImportoNonSuperato".equals(esito[0])){
                Long tipgen = new Long(esito[2]);
                Boolean isSogliaEstremoEscluso = new Boolean(esito[4]);
                String sogliaEstremo;
                if (isSogliaEstremoEscluso.booleanValue())
                  sogliaEstremo = "<";
                else
                  sogliaEstremo = "<=";
                if (tipgen.longValue() == 1){
                  messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloNumeroDitteAmmesseLavoriImporto";
                  errMsgEvento = this.resBundleGenerale.getString(messageKey);
                  errMsgEvento = errMsgEvento.replace("{0}", esito[1]);
                  errMsgEvento = errMsgEvento.replace("{1}", UtilityNumeri.convertiImporto(new Double(esito[3]), 2));
                  errMsgEvento = errMsgEvento.replace("{2}", sogliaEstremo);
                  listaMessaggi.add(" - " + errMsgEvento);
                }else if (tipgen.longValue() == 2){
                  messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloNumeroDitteAmmesseFornitureImporto";
                  errMsgEvento = this.resBundleGenerale.getString(messageKey);
                  errMsgEvento = errMsgEvento.replace("{0}", esito[1]);
                  errMsgEvento = errMsgEvento.replace("{1}", UtilityNumeri.convertiImporto(new Double(esito[3]), 2));
                  errMsgEvento = errMsgEvento.replace("{2}", sogliaEstremo);
                  listaMessaggi.add(" - " + errMsgEvento);
                }
                else{
                  messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloNumeroDitteAmmesseServiziImporto";
                  errMsgEvento = this.resBundleGenerale.getString(messageKey);
                  errMsgEvento = errMsgEvento.replace("{0}", esito[1]);
                  errMsgEvento = errMsgEvento.replace("{1}", UtilityNumeri.convertiImporto(new Double(esito[3]), 2));
                  errMsgEvento = errMsgEvento.replace("{2}", sogliaEstremo);
                  listaMessaggi.add(" - " + errMsgEvento);
                }
                livEvento = 2;
              }else if("ControlloNumeroDitteImportoNonSuperatoDL2016".equals(esito[0]) && initEscauto==null){
                Long tipgen = new Long(esito[2]);
                if (tipgen.longValue() == 1){
                  messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloNumeroDitteAmmesseLavoriImportoDL2016";
                }else if (tipgen.longValue() == 2){
                  messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloNumeroDitteAmmesseFornitureImportoDL2016";
                }
                else{
                  messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloNumeroDitteAmmesseServiziImportoDL2016";
                }
                errMsgEvento = this.resBundleGenerale.getString(messageKey);
                errMsgEvento = errMsgEvento.replace("{0}", esito[1]);
                errMsgEvento = errMsgEvento.replace("{1}", UtilityNumeri.convertiImporto(new Double(esito[3]), 2));
                listaMessaggi.add(" - " + errMsgEvento);
                livEvento = 2;
              }else if("ControlloImportoSopraSoglia".equals(esito[0])){
                Long tipgen = new Long(esito[2]);
                if (tipgen.longValue() == 1){
                  messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloImportoSopraSogliaLavori";
                }else if (tipgen.longValue() == 2){
                  messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloImportoSopraSogliaForniture";
                }
                else{
                  messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloImportoSopraSogliaServizi";
                }
                errMsgEvento = this.resBundleGenerale.getString(messageKey);
                errMsgEvento = errMsgEvento.replace("{0}", UtilityNumeri.convertiImporto(new Double(esito[3]), 2));
                listaMessaggi.add(" - " + errMsgEvento);
                livEvento = 2;
              }else{
                //best case
                listaMessaggi.add(" - Calcolo soglia anomalia completato.");
                livEvento = 1;
                errMsgEvento = "";
              }
            }


            try{
              gestoreGARE.update(status, campiLotti);
            }catch (GestoreException e) {
              messageKey = "errors.gestoreException.*.aggiudicazioneFaseA";
              errMsgEvento = this.resBundleGenerale.getString(messageKey);
              livEvento = 3;
              throw e;
            }

            }finally{
              //Tracciatura eventi per calcolo soglia anomalia nel caso di errore
              try {
                  LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
                  logEvento.setLivEvento(livEvento);
                  logEvento.setOggEvento(lotto);
                  logEvento.setCodEvento("GA_CALCOLO_AGG");
                  String descrLog = "Calcolo soglia anomalia o della graduatoria";
                  descrLog+= "("+ descCritlicg;
                  if("1".equals(legregsicLotto)){
                    String soglianorma= campiLotti.getString("GARE1.SOGLIANORMA");
                    if(soglianorma!=null)
                      descrLog+=" - " + soglianorma;
                  }else{
                    if(metsogliaLotto!=null && ("true".equals(isGaraDLGS2016) || "true".equals(isGaraDLGS2017)) && !"1".equals(legregsicLotto) && ((new Long(13)).equals(modlicg) || (new Long(14)).equals(modlicg))){
                      descrLog += " - " + descrizioneMetsoglia ;
                      if("true".equals(isGaraDLGS2017))
                        descrLog+=" - adeguato DLgs.56/2017";
                    }
                    if("true".equals(isGaraDL2019)){
                      String soglianorma= campiLotti.getString("GARE1.SOGLIANORMA");
                      if(soglianorma!=null)
                        descrLog+=" - " + soglianorma;
                    }
                  }
                  descrLog+=")";
                  if(initEscauto!=null){
                    descrLog += ". Esclusione automatica off.anomale prevista e " + tabellatiManager.getDescrTabellato("A1165", initEscauto.toString());
                  }
                  logEvento.setDescr(descrLog);
                  logEvento.setErrmsg(errMsgEvento);
                  LogEventiUtils.insertLogEventi(logEvento);
              } catch (Exception le) {
                messageKey = "errors.logEventi.inaspettataException";
                logger.error(this.resBundleGenerale.getString(messageKey), le);
              }
            }

            HashMap hMapConteggi = new HashMap();
            aggiudicazioneSuccesso = true;
            codevento = "GA_AGGIUDICAZIONE_PROV";
            descr = "Aggiudicazione provvisoria della gara ";
            descrDittap ="";
            try{
              try {
                aggiudicazioneManager.aggiudicazioneFaseB(campiLotti, hMapConteggi,true);
                dittap = campiLotti.getColumn("GARE.DITTAP").getValue().getStringValue();
                dittap = UtilityStringhe.convertiNullInStringaVuota(dittap);
                if(!"".equals(dittap)){
                  descrDittap = "(ditta " + dittap + ")";
                }
                descr ="Aggiudicazione provvisoria della gara ";
              }catch (GestoreException e) {
                if ("aggiudicazioneFaseB.PrimeParimerito".equals(e.getCodice())
                    || "aggiudicazioneFaseB.ControlloDitteAmmesse".equals(e.getCodice())
                    || "aggiudicazioneFaseB.UltimeParimerito".equals(e.getCodice())) {
                  messageKey = "errors.gestoreException.*."+e.getCodice();
                  errMsgEvento = this.resBundleGenerale.getString(messageKey);
                  errMsgEvento +=  ". Calcolo aggiudicazione non completato.";
                  listaMessaggi.add(" - " + errMsgEvento);
                  aggiudicazioneSuccesso = false;
                  livEvento = 3;
                }else {
                  livEvento = 3;
                  messageKey = "errors.gestoreException.*.aggiudicazioneFaseB";
                  errMsgEvento = this.resBundleGenerale.getString(messageKey);
                  throw e;
                }
              }
              if(aggiudicazioneSuccesso){
                String msgEsitoSuccesso= "";
                String numeroDitteAnomale = null;
                Long aqoper = null;
                if(hMapConteggi.get("numeroDitteAnomale")!=null)
                  numeroDitteAnomale =  ((String) hMapConteggi.get("numeroDitteAnomale"));
                if(hMapConteggi.get("aqoper")!=null)
                  aqoper =  ((Long) hMapConteggi.get("aqoper"));
                Long numeroPrimeParimerito = null;
                if(hMapConteggi.get("numeroPrimeParimerito")!=null)
                  numeroPrimeParimerito =  ((Long) hMapConteggi.get("numeroPrimeParimerito"));
                Long numeroDitteAggiudicatarie = null;
                if(hMapConteggi.get("numeroDitteAggiudicatarie")!=null)
                  numeroDitteAggiudicatarie =  ((Long) hMapConteggi.get("numeroDitteAggiudicatarie"));
                else
                  numeroDitteAggiudicatarie =  new Long(0);
                if("TUTTE".equals(numeroDitteAnomale) && !"1".equals(legregsicLotto)){
                  msgEsitoSuccesso= " - Calcolo aggiudicazione completato: non individuata ditta aggiudicataria perchè ditte tutte anomale ";
                }else if((aqoper ==null || (aqoper!=null && aqoper.longValue()!=2)) && (numeroPrimeParimerito==null || (numeroPrimeParimerito!=null && numeroPrimeParimerito.longValue()==0))){
                  msgEsitoSuccesso= " - Calcolo aggiudicazione completato: non individuata la ditta aggiudicataria ";
                }else if(aqoper!=null && aqoper.longValue()==2 && numeroDitteAggiudicatarie.longValue()==0){
                  msgEsitoSuccesso= " - Calcolo aggiudicazione completato: non individuate le ditte aggiudicatarie ";
                }else{
                  long numDittapSenzaAmminversa =0;
                  if(hMapConteggi.get("numDittapSenzaAmminversa")!=null){
                    numDittapSenzaAmminversa = (((Long) hMapConteggi.get("numDittapSenzaAmminversa"))).longValue();
                  }
                  if(aqoper!=null && aqoper.longValue()==2){
                    msgEsitoSuccesso= " - Calcolo aggiudicazione completato: individuate " + numeroDitteAggiudicatarie +" ditte aggiudicatarie ";
                    if(hMapConteggi.get("selAutParimerito")!=null){
                      String tipoSelParimerito = ((String) hMapConteggi.get("selAutParimerito"));
                      if("3".equals(tipoSelParimerito)){
                        msgEsitoSuccesso += " con sorteggio prima classificata tra parimerito";
                        descr += " con sorteggio prima classificata tra parimerito";
                      }
                    }
                    if(numDittapSenzaAmminversa >0)
                      msgEsitoSuccesso+=". Per alcune delle ditte prime classificate non risulta specificato l'esito della verifica della documentazione amministrativa successiva all'apertura offerte (procedura inversa)";
                  }else{
                    msgEsitoSuccesso= " - Calcolo aggiudicazione completato: individuata prima ditta classificata";

                    if(hMapConteggi.get("selAutParimerito")!=null){
                      String tipoSelParimerito = ((String) hMapConteggi.get("selAutParimerito"));
                      if("1".equals(tipoSelParimerito)){
                        msgEsitoSuccesso += " con sorteggio prima classificata tra parimerito";
                        descr += " con sorteggio prima classificata tra parimerito";
                      }
                    }
                    if(numDittapSenzaAmminversa >0)
                      msgEsitoSuccesso+=". Per la ditta prima classificata non risulta specificato l'esito della verifica della documentazione amministrativa successiva all'apertura offerte (procedura inversa)";
                  }
                }
                descr += " " + descrDittap;
                livEvento = 1;
                errMsgEvento = "";

                listaMessaggi.add(msgEsitoSuccesso);
                try{
                  gestoreGARE1.update(status, campiLotti);
                  gestoreGARE.update(status, campiLotti);
                }  catch (GestoreException e) {
                  livEvento = 3;
                  messageKey = "errors.gestoreException.*.aggiudicazioneFaseB";
                  errMsgEvento = this.resBundleGenerale.getString(messageKey);
                  throw e;
                }
              }
            }finally{
              //Tracciatura eventi per aggiudicazione provvisoria
              try {
                LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
                logEvento.setLivEvento(livEvento);
                logEvento.setOggEvento(lotto);
                logEvento.setCodEvento(codevento);
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
        this.getRequest().setAttribute("RISULTATO", "CALCOLOESEGUITO");
        this.getRequest().setAttribute("listaMessaggi", listaMessaggi);
      }

  }
}