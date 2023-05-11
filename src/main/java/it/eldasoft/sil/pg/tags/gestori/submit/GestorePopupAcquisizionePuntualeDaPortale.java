/*
 * Created on 25/03/11
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

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ElencoOperatoriManager;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Gestore non standard per l'acquisizione di un singolo aggiornamento FS2 o FS4 proveniente
 * da Portale ALice
 *
 * @author Marcello Caminiti
 */
public class GestorePopupAcquisizionePuntualeDaPortale extends AbstractGestoreEntita {

  /** Logger */
  static Logger            logger           = Logger.getLogger(GestorePopupAcquisizionePuntualeDaPortale.class);

  @Override
  public String getEntita() {
    return "IMPR";
  }

  public GestorePopupAcquisizionePuntualeDaPortale() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupAcquisizionePuntualeDaPortale(boolean isGestoreStandard) {
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
    String codEvento = "";
    String oggEvento = "";
    String descrEvento = "";
    String errMsgEvento = "";


    ElencoOperatoriManager elencoOperatoriManager = (ElencoOperatoriManager) UtilitySpring.getBean("elencoOperatoriManager",
        this.getServletContext(), ElencoOperatoriManager.class);

    String idocmString = UtilityStruts.getParametroString(this.getRequest(),"idcom");
    Long idcom =  new Long(idocmString);
    String user = UtilityStruts.getParametroString(this.getRequest(),"comkey1");
    String ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    String tipo = UtilityStruts.getParametroString(this.getRequest(),"tipo");
    String messaggioCategorie =  UtilityStruts.getParametroString(this.getRequest(),"messagggioCategorie");
    String saltareAggCateg = UtilityStruts.getParametroString(this.getRequest(),"saltareAggCateg");
    boolean aggiornamentoCategorie = (!"true".equals(saltareAggCateg));
    String msgAnteprima = UtilityStruts.getParametroString(this.getRequest(),"msgAnteprima");

    String profiloAttivo = (String) this.getRequest().getSession().getAttribute("profiloAttivo");

    if("FS2".equals(tipo)){
      codEvento = "GA_ACQUISIZIONE_ISCRIZIONE";
      descrEvento = "Acquisizione iscrizione a elenco operatori o catalogo da portale Appalti";
    }else{
      codEvento = "GA_ACQUISIZIONE_INTEGRAZIONE";
      descrEvento = "Acquisizione integrazione dati/documenti a iscrizione in elenco operatori o catalogo da portale Appalti";
    }
    oggEvento = ngara;

    String xml=null;

    try{

      GestorePopupAcquisisciDaPortale gacqport = new GestorePopupAcquisisciDaPortale();
      gacqport.setRequest(getRequest());
      HashMap<String, Object> ret = elencoOperatoriManager.acquisizioneDaPortaleSingola(idcom, user, ngara, tipo, aggiornamentoCategorie, messaggioCategorie, msgAnteprima, gacqport, status);
      int esito = ((Integer)ret.get("esito")).intValue();
      if(ElencoOperatoriManager.OK==esito)
        this.getRequest().setAttribute("acquisizioneEseguita", "1");
      else
        this.getRequest().setAttribute("erroreAcquisizione", "1");
      xml = (String)ret.get("xml");
      descrEvento += (String)ret.get("descrEvento");
    }catch(GestoreException e){
      this.getRequest().setAttribute("erroreAcquisizione", "1");
      livEvento = 3;
      errMsgEvento = e.getMessage();
      throw e;
    }finally {
      //Tracciatura eventi
      if(xml != null && !"".equals(xml)) {
        if("FS2".equals(tipo))
          xml = CostantiAppalti.nomeFileXML_Iscrizione + "\r\n" + xml;
        else
          xml= CostantiAppalti.nomeFileXML_Aggiornamento + "\r\n" + xml;
        if(errMsgEvento!="")
          errMsgEvento+="\r\n\r\n";
        errMsgEvento+= xml;
      }
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