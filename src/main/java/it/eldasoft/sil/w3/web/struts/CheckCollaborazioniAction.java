/*
 * Created on 15/nov/2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.w3.web.struts;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.w3.bl.GestioneServiziIDGARACIGManager;
import it.eldasoft.utils.properties.ConfigManager;

public class CheckCollaborazioniAction extends ActionBaseNoOpzioni {

  protected static final String           FORWARD_SUCCESS = "checksuccess";
  protected static final String           FORWARD_NODATA   = "checknodata";
  protected static final String           FORWARD_ERROR   = "checkfail";

  static Logger                           logger          = Logger.getLogger(CheckCollaborazioniAction.class);

  private GestioneServiziIDGARACIGManager gestioneServiziIDGARACIGManager;
  
  private SqlManager sqlManager;
  
  private GeneManager geneManager;
  
 
  
  public void setGestioneServiziIDGARACIGManager(
      GestioneServiziIDGARACIGManager gestioneServiziIDGARACIGManager) {
    this.gestioneServiziIDGARACIGManager = gestioneServiziIDGARACIGManager;
  }
  
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }
 
  

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("CheckCollaborazioniAction: inizio metodo");

    //String target = FORWARD_SUCCESS;
    String messageKey = null;
    boolean memorizzaCredenziali = false;
    boolean rpntFailed = false;

    try {
      ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = new Long(profilo.getId());
      
      String uffint = (String) request.getSession().getAttribute(
          CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);

      String codrup = request.getParameter("codrup");
      String memorizza = request.getParameter("memorizza");
      String recuperaPassword = request.getParameter("recuperapassword");
      String rpntFailedString = request.getParameter("rpntFailed");
      String codeinFromGare = request.getParameter("codeinFromGare");
      if("1".equals(rpntFailedString)) {
        rpntFailed=true;
      }
      
      //se è nullo, ma arriviamo dal profilo GARE, prendiamo come codein
      if("".equals(StringUtils.stripToEmpty(uffint)) && !"".equals(StringUtils.stripToEmpty(codeinFromGare))){
        uffint = codeinFromGare;
      }
      
      
      String simogwsuser = null;
      String simogwspass = null;

      HashMap<String, String> hMapSIMOGWSUserPass = new HashMap<String, String>();
      // Gestione USER
      if (memorizza == null && rpntFailed) {
     // Leggo le eventuali credenziali memorizzate

        hMapSIMOGWSUserPass = this.gestioneServiziIDGARACIGManager.recuperaSIMOGWSUserPass(codrup);
        simogwspass = ((String) hMapSIMOGWSUserPass.get("simogwspass"));
        simogwsuser = ((String) hMapSIMOGWSUserPass.get("simogwsuser"));
      } else {
        simogwsuser = request.getParameter("simogwsuser");
       if("1".equals(recuperaPassword) && rpntFailed) {
         hMapSIMOGWSUserPass = this.gestioneServiziIDGARACIGManager.recuperaSIMOGWSUserPassFromSyscon(syscon);
         simogwspass = (hMapSIMOGWSUserPass.get("simogwspass"));
       }else {
         simogwspass = request.getParameter("simogwspass");
         memorizzaCredenziali=true;
       }
      }
      
      // Richiesta lista collaborazioni
      List<Vector<Object>> listaCC = this.gestioneServiziIDGARACIGManager.checkCollaborazioni(uffint, simogwsuser, simogwspass, rpntFailed);
      // se arrivo qui, le credenziali sono valide
      if(memorizzaCredenziali && rpntFailed) {
        this.gestioneServiziIDGARACIGManager.memorizzaSIMOGWSUserPass(syscon,simogwsuser, simogwspass);
      }
      if(listaCC.size() > 0) {
        
        //creo un record in tecni per il rup, se non ce l'ha
        String archiviFiltrati = ConfigManager.getValore("it.eldasoft.associazioneUffintAbilitata.archiviFiltrati");
        boolean tecniFiltrato = (archiviFiltrati.indexOf("TECNI") != -1);
        if(tecniFiltrato) {
          Long tecni = (Long) this.sqlManager.getObject("select count(*) from TECNI where CFTEC = ? AND CGENTEI = ?", new Object [] {profilo.getCodiceFiscale(),uffint});
          if(tecni < 1 && simogwsuser != null && simogwsuser.equals(profilo.getCodiceFiscale())){
            TransactionStatus status = this.geneManager.getSql().startTransaction();
            codrup = this.geneManager.calcolaCodificaAutomatica("TECNI","CODTEC");
            this.geneManager.getSql().update("insert into TECNI (codtec, nomtec, cftec, cgentei) values (?,?,?,?)", new Object [] {codrup,profilo.getNome(),profilo.getCodiceFiscale(),uffint});
            this.geneManager.getSql().commitTransaction(status);
          }
        }else {
          Long tecni = (Long) this.sqlManager.getObject("select count(*) from TECNI where CFTEC = ?", new Object [] {profilo.getCodiceFiscale()});
          if(tecni < 1 && simogwsuser != null && simogwsuser.equals(profilo.getCodiceFiscale())){
            TransactionStatus status = this.geneManager.getSql().startTransaction();
            codrup = this.geneManager.calcolaCodificaAutomatica("TECNI","CODTEC");
            this.geneManager.getSql().update("insert into TECNI (codtec, nomtec, cftec) values (?,?,?)", new Object [] {codrup,profilo.getNome(),profilo.getCodiceFiscale()});
            this.geneManager.getSql().commitTransaction(status);
          }
        }
        
        request.setAttribute("isRup",true);
        request.setAttribute("listaCC", listaCC.toArray());
        request.getSession().removeAttribute("erroreInvioRichiestaSimog");
      }else {
        messageKey = "errors.gestioneIDGARACIG.error";
        this.aggiungiMessaggio(request, messageKey, "Operazione non possibile: il RUP non ha accesso su SIMOG per la stazione appaltante corrente");
      }
      
      request.getSession().setAttribute("numeroPopUp", "1");
    } catch (GestoreException e) {
      messageKey = "errors.gestioneIDGARACIG.error";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey, e.getMessage());
      if(rpntFailed) {
        request.setAttribute("erroreInvioRichiestaSimog", "true");
      }else {
        if(e.getMessage().indexOf(GestioneServiziIDGARACIGManager.LOGIN_SIMOG_ERRATA)>-1 && !rpntFailed) {
          request.getSession().setAttribute("erroreCredenzialiRPNT", "true");
        }else {
          if(e.getMessage().indexOf(GestioneServiziIDGARACIGManager.RUP_NON_PRESENTE_O_SENZA_COLL)>-1 && !rpntFailed) {
            request.setAttribute("erroreCredenzialiRPNT", "true");
          }
          else {
            request.setAttribute("erroreInvioRichiestaSimog", "true");
          }
        }
      }
    } catch (Throwable e) {
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }
    if (messageKey != null) response.reset();

    if (logger.isDebugEnabled())
      logger.debug("RichiestaCollaborazioniAction: fine metodo");

    return mapping.findForward("result");

  }

}
