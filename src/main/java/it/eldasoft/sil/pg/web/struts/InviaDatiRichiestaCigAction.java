/*
 * 	Created on 02/07/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.InviaDatiRichiestaCigManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class InviaDatiRichiestaCigAction extends ActionBaseNoOpzioni {

  protected static final String    FORWARD_SUCCESS           = "inviadatirichiestacigsuccess";
  protected static final String    FORWARD_ERROR             = "inviadatirichiestacigerror";
  protected static final String    FORWARD_ERRORI_BLOCCANTI         = "inviadatirichiestacigerroribloccanti";
  protected static final String    FORWARD_ERRORI_NON_BLOCCANTI     = "inviadatirichiestacigerrorinonbloccanti";

  static Logger                    logger            = Logger.getLogger(InviaDatiRichiestaCigAction.class);

  private InviaDatiRichiestaCigManager  inviaDatiRichiestaCigManager;

  public void setInviaDatiRichiestaCigManager(InviaDatiRichiestaCigManager inviaDatiRichiestaCigManager) {
    this.inviaDatiRichiestaCigManager = inviaDatiRichiestaCigManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("InviaDatiRichiestaCigAction: inizio metodo");

    String target = FORWARD_SUCCESS;
    String messageKey = null;

    String codgar = request.getParameter("codgar");
    String genere = request.getParameter("genere");
    String numeroLotto = request.getParameter("numeroLotto");
    String operazione = request.getParameter("operazione");
    String tiporichiesta = request.getParameter("tiporichiesta");

    String credenziali = request.getParameter("credenziali");
    String username = null;
    String password = null;

    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    boolean autenticazioneSSO = profiloUtente.isAutenticazioneSSO();
    boolean richiestaInoltrata=false;

    //variabili per tracciatura eventi
    String genericMsgErr = "Errore inaspettato durante la tracciatura su w_logeventi";
    int livEvento = 1;
    String errMsgEvento = "";
    String codiceEvento ="";
    String descrEv = "Invio dei dati di gara per richiesta ";
    if("OP2".equals(operazione)){
      descrEv = "Consultazione dati in seguito a invio richiesta CIG o smartCIG";
      codiceEvento="GA_CONSULTA_CIG";
    }else{
      if("Cig".equals(tiporichiesta)){
        descrEv+= "CIG";
        codiceEvento="GA_RICHIESTA_CIG";
      }else{
        descrEv+= "smartCIG";
        codiceEvento="GA_RICHIESTA_SMARTCIG";
      }
    }

    try{
      if ("CORRENTI".equals(credenziali) || autenticazioneSSO) {

        username = profiloUtente.getLogin();
        String passwordCifrata = profiloUtente.getPwd();
        if (passwordCifrata != null) {
          ICriptazioneByte decriptatore;
          try {
            decriptatore = FactoryCriptazioneByte.getInstance(
                ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
                passwordCifrata.getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
            password = new String(decriptatore.getDatoNonCifrato());
          } catch (CriptazioneException e) {
            livEvento = 3;
            errMsgEvento = this.resBundleGenerale.getString(e.getChiaveResourceBundle()) +":" + e.getMessage();
            logger.error(
                this.resBundleGenerale.getString(e.getChiaveResourceBundle()), e);
          }
        }
      } else if ("ALTRE".equals(credenziali)) {
        username = request.getParameter("username");
        password = request.getParameter("password");
      }

      try {
        //Le operazioni da gestire sono:
        //OP1: prevede dei dei controlli preliminari sui dati di dati di gara per eseguirne poi l'invio per
        //        richiesta CIG
        //        Richiesta Smart CIG
        //OP1.1: si inviano i dati di gara(non si effettuano i controlli preliminari) per
        //        richiesta CIG
        //        Richiesta Smart CIG
        //OP2: Consultazione dei dati di gara
        if("OP1".equals(operazione)){
          HashMap<String,Object> result = new HashMap<String,Object>();
          if("Cig".equals(tiporichiesta))
            result = inviaDatiRichiestaCigManager.controllaDatiRichiestaCIG(codgar, numeroLotto,genere, request.getSession());
          else{
            //Smart CIG
            result = inviaDatiRichiestaCigManager.controllaDatiRichiestaSmartCIG(codgar, numeroLotto);
          }
          ArrayList<String> erroriBloccanti = (ArrayList<String>) result.get("erroriBloccanti");
          ArrayList<String> erroriNonBloccanti = (ArrayList<String>) result.get("erroriNonBloccanti");
          if(erroriBloccanti.size() == 0 && erroriNonBloccanti.size() == 0){
            String esitoWS = null;
            if("Cig".equals(tiporichiesta)){
              richiestaInoltrata=true;
              esitoWS = inviaDatiRichiestaCigManager.inviaDatiRichiestaCig(codgar, numeroLotto, genere, username, password);
            }else{
              //Smart CIG
              richiestaInoltrata=true;
              esitoWS=inviaDatiRichiestaCigManager.inviaDatiRichiestaSmartCig(codgar, numeroLotto, username, password);
            }
            request.setAttribute("esitoWS", esitoWS);
          }else{
            if(erroriBloccanti.size() == 0){
              //CAMBIARE QUESTO FORWARD NELLA PAGINA CHE CHIEDE ALL'UTENTE SE VUOLE PROSEGUIRE
              request.setAttribute("erroriNonBloccanti", erroriNonBloccanti);
              request.setAttribute("genere",genere);
              request.setAttribute("numeroLotto",numeroLotto);
              if (!"CORRENTI".equals(credenziali) && !autenticazioneSSO) {
                request.setAttribute("password",password);
                request.setAttribute("username",username);
                request.setAttribute("credenziali","ALTRE");}
              else{
                request.setAttribute("credenziali","CORRENTI");
              }
              request.setAttribute("tiporichiesta",tiporichiesta);
              target = FORWARD_ERRORI_NON_BLOCCANTI;
            }else{
              //CAMBIARE QUESTO FORWARD NULLA PAGINA CHE ELENCA GLI ERRORI E NON FA PROSEGUIRE
              request.setAttribute("erroriNonBloccanti", erroriNonBloccanti);
              request.setAttribute("erroriBloccanti", erroriBloccanti);
              target = FORWARD_ERRORI_BLOCCANTI;
            }
          }

        }else if("OP1.1".equals(operazione)){
          String esitoWS = null;
          if("Cig".equals(tiporichiesta)){
            richiestaInoltrata=true;
            esitoWS = inviaDatiRichiestaCigManager.inviaDatiRichiestaCig(codgar, numeroLotto, genere, username, password);
          }else{
            //Smart CIG
            richiestaInoltrata=true;
            esitoWS=inviaDatiRichiestaCigManager.inviaDatiRichiestaSmartCig(codgar, numeroLotto, username, password);
          }
          request.setAttribute("esitoWS", esitoWS);

        }else{
          //OP2: Consultazione dei dati di gara
          boolean smartCig=false;
          String uuidTorn = request.getParameter("uuidTorn");
          String uuidGare1 = request.getParameter("uuidGare1");
          if(uuidTorn!=null && !"".equals(uuidTorn) && uuidGare1!=null && !"".equals(uuidGare1) && uuidTorn.equals(uuidGare1))
            smartCig=true;
          String esitoWS = inviaDatiRichiestaCigManager.consultaDati(codgar, numeroLotto, genere, username, password,smartCig);
          request.setAttribute("esitoWS", esitoWS);
          errMsgEvento=esitoWS;
        }

      } catch (GestoreException e){
        livEvento = 3;
        target = FORWARD_ERROR;
        String codice=e.getCodice();
        if("errors.inviadatirichiestacig.validazione".equals(codice)){
          Object parametri[] = e.getParameters(); //Contiene una sola stringa
          String messaggio="La generazione dei dati di gara per la richiesta CIG non rispetta il formato previsto: ";
          messaggio+=parametri[0];
          this.aggiungiMessaggio(request, codice, messaggio);
          errMsgEvento = messaggio;
        }else{
          messageKey = "errors.inviadatirichiestacig.error";
          logger.error(this.resBundleGenerale.getString(messageKey), e);
          this.aggiungiMessaggio(request, messageKey, e.getMessage());
          errMsgEvento= this.resBundleGenerale.getString(messageKey) + ":" + e.getMessage();
        }
      } catch (Exception e) {
        livEvento = 3;
        target = FORWARD_ERROR;
        messageKey = "errors.inviadatirichiestacig.error";
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey, e.getMessage());
        errMsgEvento= this.resBundleGenerale.getString(messageKey) + ":" + e.getMessage();
      } catch (Throwable t) {
        livEvento = 3;
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.applicazione.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), t);
        this.aggiungiMessaggio(request, messageKey);
        errMsgEvento= this.resBundleGenerale.getString(messageKey) + ":" + t.getMessage();
      }
    }finally{

      //Tracciatura eventi solo per le operazioni di richiesta
      try {
        if(richiestaInoltrata && ("OP1.1".equals(operazione) || "OP1".equals(operazione)) || "OP2".equals(operazione)){
          String chiave=numeroLotto;
          if(!"2".equals(genere))
            chiave=codgar;
          LogEvento logEvento = LogEventiUtils.createLogEvento(request);
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(chiave);
          logEvento.setCodEvento(codiceEvento);
          logEvento.setDescr(descrEv);
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        }
      } catch (Exception le) {
        logger.error(genericMsgErr, le);
      }


      if (messageKey != null) response.reset();

      if (logger.isDebugEnabled()) logger.debug("InviaDatiRichiestaCigAction: fine metodo");

      return mapping.findForward(target);
    }
  }

}
