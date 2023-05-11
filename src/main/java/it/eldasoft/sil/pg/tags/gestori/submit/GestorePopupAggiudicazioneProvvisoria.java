/*
 * Created on 30/lug/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

public class GestorePopupAggiudicazioneProvvisoria extends
    AbstractGestoreEntita {

  /** Logger */
  static Logger logger = Logger.getLogger(GestorePopupAggiudicazioneProvvisoria.class);

  @Override
  public String getEntita() {
    return "GARE";
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

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
        "tabellatiManager", this.getServletContext(),
        TabellatiManager.class);

    String modoRichiamo = impl.getString("MODORICHIAMO");
    String ngara = impl.getString("NGARA");

    this.getRequest().setAttribute("MODORICHIAMO", modoRichiamo);
    this.getRequest().setAttribute("NGARA", ngara);

    String dittap = impl.getString("DITTAP");
    dittap = UtilityStringhe.convertiNullInStringaVuota(dittap);
    boolean gestioneRegSic=false;

    // Calcolo della soglia di anomalia
    if ("SOGLIA".equals(modoRichiamo)) {
      //variabili per tracciatura eventi
      String messageKey = null;
      int livEvento = 3;
      String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
      Long critlicg = impl.getLong("CRITLICG");

      String descrizioneCritlicg=null;
      String descrizioneMetsoglia=null;
      String isGaraDLGS2016=UtilityStruts.getParametroString(this.getRequest(),"isGaraDLGS2016");
      String isGaraDLGS2017=UtilityStruts.getParametroString(this.getRequest(),"isGaraDLGS2017");
      String isGaraDL2019=UtilityStruts.getParametroString(this.getRequest(),"isGaraDL2019");
      String[] esito=null;
      Long escauto = null;
      if(impl.isColumn("ESCAUTOFIT")){
        escauto = impl.getLong("ESCAUTOFIT");

      }

      if(impl.isColumn("LEGREGSICVISIBILE")){
        String applicabilitaLegRegSic = impl.getColumn("LEGREGSICVISIBILE").getValue().stringValue();
        UtilityStringhe.convertiNullInStringaVuota(applicabilitaLegRegSic);
        if(impl.isColumn("GARE1.LEGREGSIC") && "1".equals(applicabilitaLegRegSic)){
          impl.getColumn("GARE1.LEGREGSIC").setOriginalValue(null);
          gestioneRegSic=true;
        }else{
          if(impl.isColumn("GARE1.LEGREGSIC")){
            impl.setValue("GARE1.LEGREGSIC", null);
            impl.getColumn("GARE1.LEGREGSIC").setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO,"1"));
          }
        }
      }

      try{

        descrizioneCritlicg = tabellatiManager.getDescrTabellato("A2081", critlicg.toString());
      //Se la gara è già aggiudicata in via provvisoria si segnala l'errore
        if(!"".equals(dittap)){
          //errMsgEvento="errors.gestoreException.*.aggiudicazioneFaseA.ControlloAggiudicazioneProvvisoria";
          String codiceErrore="errors.gestoreException.*.aggiudicazioneFaseA.ControlloAggiudicazioneProvvisoria";
          errMsgEvento = this.resBundleGenerale.getString(codiceErrore);
          this.getRequest().setAttribute("RISULTATO", "ERRORI");
          throw new GestoreException(errMsgEvento, codiceErrore, null, null);
        }

        try {

          esito = aggiudicazioneManager.aggiudicazioneFaseA(impl);
          //Gestione GARE1
          AbstractGestoreEntita gestoreGARE1 = new DefaultGestoreEntita("GARE1", this.getRequest());


          gestoreGARE1.update(status, impl);

          this.getRequest().setAttribute("RISULTATO", "CALCOLOESEGUITO");

          String descrizioneA1132 = tabellatiManager.getDescrTabellato("A1132", "1");
          if(descrizioneA1132!=null && !"".equals(descrizioneA1132))
            descrizioneA1132 = descrizioneA1132.substring(0, 1);
          this.getRequest().setAttribute("descrizioneA1132", descrizioneA1132);

          if (esito!=null && esito.length > 0) {
            if ("ControlloNumeroDitteNonSuperato".equals(esito[0])){
              livEvento = 2;
              messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloNumeroDitteAmmesse";
              errMsgEvento = this.resBundleGenerale.getString(messageKey);
              errMsgEvento = errMsgEvento.replace("{0}", esito[1]);
              if("false".equals(isGaraDLGS2016) && "false".equals(isGaraDLGS2017) && "false".equals(isGaraDL2019))
                UtilityStruts.addMessage(this.getRequest(), "warning",
                    "warnings.gare.aggiudicazioneFaseA.ControlloNumeroDitteAmmesse",
                    new Object[] { new Long(esito[1]) });
            } else if("ControlloRibassoDitteNonSuperato".equals(esito[0])){
              livEvento = 2;
              messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloRibassoDitteAmmesse";
              errMsgEvento = this.resBundleGenerale.getString(messageKey);
            } else if("ControlloNumeroDitteImportoNonSuperato".equals(esito[0])){
              Long tipgen = new Long(esito[2]);
              Boolean isSogliaEstremoEscluso = new Boolean(esito[4]);
              String sogliaEstremo;
              if (isSogliaEstremoEscluso.booleanValue())
                sogliaEstremo = "<";
              else
                sogliaEstremo = "<=";
              if (tipgen.longValue() == 1){
                livEvento = 2;
                messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloNumeroDitteAmmesseLavoriImporto";
                errMsgEvento = this.resBundleGenerale.getString(messageKey);
                errMsgEvento = errMsgEvento.replace("{0}", esito[1]);
                errMsgEvento = errMsgEvento.replace("{1}", UtilityNumeri.convertiImporto(new Double(esito[3]), 2));
                errMsgEvento = errMsgEvento.replace("{2}", sogliaEstremo);
                UtilityStruts.addMessage(this.getRequest(), "warning",
                    "warnings.gare.aggiudicazioneFaseA.ControlloNumeroDitteAmmesseLavoriImporto",
                    new Object[] { new Long(esito[1]), UtilityNumeri.convertiImporto(new Double(esito[3]), 2), sogliaEstremo });
              }else if (tipgen.longValue() == 2){
                livEvento = 2;
                messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloNumeroDitteAmmesseFornitureImporto";
                errMsgEvento = this.resBundleGenerale.getString(messageKey);
                errMsgEvento = errMsgEvento.replace("{0}", esito[1]);
                errMsgEvento = errMsgEvento.replace("{1}", UtilityNumeri.convertiImporto(new Double(esito[3]), 2));
                errMsgEvento = errMsgEvento.replace("{2}", sogliaEstremo);
                UtilityStruts.addMessage(this.getRequest(), "warning",
                    "warnings.gare.aggiudicazioneFaseA.ControlloNumeroDitteAmmesseFornitureImporto",
                    new Object[] {  new Long(esito[1]), UtilityNumeri.convertiImporto(new Double(esito[3]), 2), sogliaEstremo });
              }
              else{
                livEvento = 2;
                messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloNumeroDitteAmmesseServiziImporto";
                errMsgEvento = this.resBundleGenerale.getString(messageKey);
                errMsgEvento = errMsgEvento.replace("{0}", esito[1]);
                errMsgEvento = errMsgEvento.replace("{1}", UtilityNumeri.convertiImporto(new Double(esito[3]), 2));
                errMsgEvento = errMsgEvento.replace("{2}", sogliaEstremo);
                UtilityStruts.addMessage(this.getRequest(), "warning",
                    "warnings.gare.aggiudicazioneFaseA.ControlloNumeroDitteAmmesseServiziImporto",
                    new Object[] {  new Long(esito[1]), UtilityNumeri.convertiImporto(new Double(esito[3]), 2), sogliaEstremo });
              }
            } else if("ControlloNumeroDitteImportoNonSuperatoDL2016".equals(esito[0]) && escauto == null){
              Long tipgen = new Long(esito[2]);
              if (tipgen.longValue() == 1){
                livEvento = 2;
                messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloNumeroDitteAmmesseLavoriImportoDL2016";
                errMsgEvento = this.resBundleGenerale.getString(messageKey);
                errMsgEvento = errMsgEvento.replace("{0}", esito[1]);
                errMsgEvento = errMsgEvento.replace("{1}", UtilityNumeri.convertiImporto(new Double(esito[3]), 2));
                UtilityStruts.addMessage(this.getRequest(), "warning",messageKey,
                    new Object[] { new Long(esito[1]), UtilityNumeri.convertiImporto(new Double(esito[3]), 2)});
              }else if (tipgen.longValue() == 2){
                livEvento = 2;
                messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloNumeroDitteAmmesseFornitureImportoDL2016";
                errMsgEvento = this.resBundleGenerale.getString(messageKey);
                errMsgEvento = errMsgEvento.replace("{0}", esito[1]);
                errMsgEvento = errMsgEvento.replace("{1}", UtilityNumeri.convertiImporto(new Double(esito[3]), 2));
                UtilityStruts.addMessage(this.getRequest(), "warning",messageKey,
                    new Object[] {  new Long(esito[1]), UtilityNumeri.convertiImporto(new Double(esito[3]), 2)});
              }
              else{
                livEvento = 2;
                messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloNumeroDitteAmmesseServiziImportoDL2016";
                errMsgEvento = this.resBundleGenerale.getString(messageKey);
                errMsgEvento = errMsgEvento.replace("{0}", esito[1]);
                errMsgEvento = errMsgEvento.replace("{1}", UtilityNumeri.convertiImporto(new Double(esito[3]), 2));
                UtilityStruts.addMessage(this.getRequest(), "warning",messageKey,
                    new Object[] {  new Long(esito[1]), UtilityNumeri.convertiImporto(new Double(esito[3]), 2) });
              }
            }else if("ControlloImportoSopraSoglia".equals(esito[0]) && escauto == null){
              Long tipgen = new Long(esito[2]);
              if (tipgen.longValue() == 1){
                livEvento = 2;
                messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloImportoSopraSogliaLavori";
                errMsgEvento = this.resBundleGenerale.getString(messageKey);
                errMsgEvento = errMsgEvento.replace("{0}", UtilityNumeri.convertiImporto(new Double(esito[3]), 2));
                UtilityStruts.addMessage(this.getRequest(), "warning",messageKey,
                    new Object[] { UtilityNumeri.convertiImporto(new Double(esito[3]), 2)});
              }else if (tipgen.longValue() == 2){
                livEvento = 2;
                messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloImportoSopraSogliaForniture";
                errMsgEvento = this.resBundleGenerale.getString(messageKey);
                errMsgEvento = errMsgEvento.replace("{0}", UtilityNumeri.convertiImporto(new Double(esito[3]), 2));
                UtilityStruts.addMessage(this.getRequest(), "warning",messageKey,
                    new Object[] { UtilityNumeri.convertiImporto(new Double(esito[3]), 2)});
              }
              else{
                livEvento = 2;
                messageKey = "warnings.gare.aggiudicazioneFaseA.ControlloImportoSopraSogliaServizi";
                errMsgEvento = this.resBundleGenerale.getString(messageKey);
                errMsgEvento = errMsgEvento.replace("{0}", UtilityNumeri.convertiImporto(new Double(esito[3]), 2));
                UtilityStruts.addMessage(this.getRequest(), "warning",messageKey,
                    new Object[] { UtilityNumeri.convertiImporto(new Double(esito[3]), 2) });
              }
            }else{
              //best case
              livEvento = 1 ;
              errMsgEvento = "";
            }
          }
        } catch (GestoreException e) {
          this.getRequest().setAttribute("RISULTATO", "ERRORI");
          throw e;
        } catch (Throwable e) {
          this.getRequest().setAttribute("RISULTATO", "ERRORI");
          throw new GestoreException(
              "Errore durante il calcolo della prima fase dell'aggiudicazione (calcolo della soglia di anomalia)",
              "aggiudicazioneFaseA", e);
        }

      }finally{
        //Tracciatura eventi
        try {
          Long metsoglia = impl.getLong("METSOGLIA");
          if(metsoglia!=null)
            descrizioneMetsoglia = tabellatiManager.getDescrTabellato("A1126", metsoglia.toString());
          String descr ="Calcolo soglia anomalia o della graduatoria";
          descr+="(" + descrizioneCritlicg;
          if(descrizioneMetsoglia!=null && "false".equals(isGaraDL2019)){
            descr+=" - " + descrizioneMetsoglia;
            if("true".equals(isGaraDLGS2017))
              descr+=" - adeguato DLgs.56/2017";
          }
          if("true".equals(isGaraDL2019) || gestioneRegSic){
            String soglianorma = impl.getString("SOGLIANORMA");
            if(soglianorma!=null)
              descr+=" - " + soglianorma;
          }
          descr+=")";
          if(escauto!=null)
            descr += ". Esclusione automatica off.anomale prevista e " + tabellatiManager.getDescrTabellato("A1165", escauto.toString());


          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(ngara);
          logEvento.setCodEvento("GA_CALCOLO_AGG");
          logEvento.setDescr(descr);
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          messageKey = "errors.logEventi.inaspettataException";
          logger.error(this.resBundleGenerale.getString(messageKey), le);
        }
      }
    }
    // Calcolo delle ditte aggiudicatarie in modalità di aggiudicazione manuale
    // La gestione comprende anche l'eventuale ricalcolo per la presenza
    // di ditte parimerito

    if ("AGGIUDICAZIONE".equals(modoRichiamo)) {
      HashMap hMapConteggi = new HashMap();
      //variabili per tracciatura eventi
      String messageKey = null;
      int livEvento = 3;
      String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
      String descrDittap = "";
      Long selpar = null;

      try{
        //Se la gara è già aggiudicata in via provvisoria si segnala l'errore
        dittap = UtilityStringhe.convertiNullInStringaVuota(dittap);
        if(!"".equals(dittap)){
          descrDittap = "(ditta " + dittap + ")";
          String codiceErrore="errors.gestoreException.*.aggiudicazioneFaseA.ControlloAggiudicazioneProvvisoria";
          errMsgEvento = this.resBundleGenerale.getString(codiceErrore);
          this.getRequest().setAttribute("RISULTATO", "ERRORI");
          throw new GestoreException(errMsgEvento, codiceErrore, null, null);
        }

        //Calcolo dell'Aggiudicazione Provvisoria
        try {
          selpar = impl.getLong("TORN.SELPAR");
          aggiudicazioneManager.aggiudicazioneFaseB(impl, hMapConteggi,false);
          dittap = impl.getString("DITTAP");
          dittap = UtilityStringhe.convertiNullInStringaVuota(dittap);
          descrDittap = "(ditta " + dittap + ")";

          //Gestione GARE1
          AbstractGestoreEntita gestoreGARE1 = new DefaultGestoreEntita("GARE1", this.getRequest());
          gestoreGARE1.update(status, impl);
          livEvento = 1;

          errMsgEvento = "";
          this.getRequest().setAttribute("RISULTATO", "CALCOLOESEGUITO");
          this.getRequest().setAttribute("AQOPER", hMapConteggi.get("aqoper"));
          if(hMapConteggi.get("aqoper")!=null && ((Long)hMapConteggi.get("aqoper")).longValue()==2)
            this.getRequest().setAttribute("numeroDitteAggiudicatarie", hMapConteggi.get("numeroDitteAggiudicatarie"));

          //Nel caso di gara inversa, si deve dare un messaggio informativo se per le ditte con staggi=4 non è valorizzato il campo DITG.AMMINVERSA
          String inversa = impl.getString("TORN.INVERSA");
          if("1".equals(inversa)){
            Long conteggio=(Long)this.sqlManager.getObject("select count(dittao) from ditg where ngara5=? and staggi=? and amminversa is null", new Object[]{ngara, new Long(4)});
            if(conteggio!=null && conteggio.longValue()>0){
              this.getRequest().setAttribute("aggSenzaAmminversa", "si");
            }
          }
        } catch (GestoreException e) {

          if ("aggiudicazioneFaseB.PrimeParimerito".equals(e.getCodice())
              || "aggiudicazioneFaseB.UltimeParimerito".equals(e.getCodice())) {
            livEvento = 2;
            messageKey = "errors.gestoreException.*."+e.getCodice();
            errMsgEvento = this.resBundleGenerale.getString(messageKey);

            // Gestione ditte parimerito
            long numeroPrimeParimerito = 0;
            if (hMapConteggi.get("numeroPrimeParimerito") != null)
              numeroPrimeParimerito = ((Long) hMapConteggi.get("numeroPrimeParimerito")).longValue();

            String esitoControlloAggiudicazioneAccordiQuadroPiuOperatori = "";
            if (hMapConteggi.get("esitoControlloAggiudicazioneAccordiQuadroPiuOperatori") != null)
              esitoControlloAggiudicazioneAccordiQuadroPiuOperatori = ((String)
                  hMapConteggi.get("esitoControlloAggiudicazioneAccordiQuadroPiuOperatori"));

            this.getRequest().setAttribute("selpar", selpar);

            if (numeroPrimeParimerito > 1 ) {
              this.getRequest().setAttribute("NUMEROPRIMEPARIMERITO",
                  hMapConteggi.get("numeroPrimeParimerito"));
              this.getRequest().setAttribute("LISTAPRIMEPARIMERITO",
                  hMapConteggi.get("listaPrimeParimerito"));
              this.getRequest().setAttribute("PRIMAAGGIUDICATARIASELEZIONATA",
                  hMapConteggi.get("primaAggiudicatariaSelezionata"));


              if (numeroPrimeParimerito > 1) {
                this.getRequest().setAttribute("RISULTATO", "PRIMEPARIMERITO");
                this.setStopProcess(true);
              }



              if(impl.isColumn("GARE1.NOTPROV")){
                String notprov = impl.getString("GARE1.NOTPROV");
                if(notprov!= null && !"".equals(notprov))
                  notprov = notprov.replace("\r\n", "%0D");
                this.getRequest().setAttribute("NOTPROV", notprov);
              }

            }else if(esitoControlloAggiudicazioneAccordiQuadroPiuOperatori.equals("NOK")){
              this.getRequest().setAttribute("NUMEROULTIMEPARIMERITODASELEZIONARE",
                  hMapConteggi.get("numeroParimeritoDaSelezionare"));
              this.getRequest().setAttribute("LISTAULTIMEPARIMERITO",
                  hMapConteggi.get("listaUltimeParimerito"));
              this.getRequest().setAttribute("RIBAUOPARIMERITO",
                  hMapConteggi.get("ribauoParimerito"));
              this.getRequest().setAttribute("RISULTATO", "ULTIMEPARIMERITO");
              this.setStopProcess(true);
            }else {
              this.getRequest().setAttribute("RISULTATO", "CALCOLOESEGUITO");
            }
          } else {
            this.getRequest().setAttribute("RISULTATO", "ERRORI");
            throw e;
          }
        } catch (Throwable e) {
          livEvento = 3;
          messageKey = "errors.gestoreException.*.aggiudicazioneFaseB";
          errMsgEvento = this.resBundleGenerale.getString(messageKey);
          this.getRequest().setAttribute("RISULTATO", "ERRORI");
          throw new GestoreException(
              "Errore durante il calcolo della seconda fase dell'aggiudicazione (calcolo della ditta aggiudicataria)",
              "aggiudicazioneFaseB", e);
        }
      }finally{
        //Tracciatura eventi
        try {
          String descrEvento = "Proposta di aggiudicazione della gara " ;
          if(selpar!=null && (new Long(1)).equals(selpar) && hMapConteggi.get("selAutParimerito")!=null){
            String tipoSelParimerito = ((String) hMapConteggi.get("selAutParimerito"));
            if("1".equals(tipoSelParimerito) || "3".equals(tipoSelParimerito))
              descrEvento += " con sorteggio prima classificata tra parimerito";

          }
          descrEvento += " " +  descrDittap;
          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(ngara);
          logEvento.setCodEvento("GA_AGGIUDICAZIONE_PROV");
          logEvento.setDescr(descrEvento);
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