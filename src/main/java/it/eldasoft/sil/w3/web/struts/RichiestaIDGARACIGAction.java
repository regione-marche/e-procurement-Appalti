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
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AnagraficaSimogManager;
import it.eldasoft.sil.w3.bl.GestioneServiziIDGARACIGManager;
import it.eldasoft.utils.utility.UtilityDate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class RichiestaIDGARACIGAction extends ActionBaseNoOpzioni {

  protected static final String           FORWARD_SUCCESS = "richiestaidgaracigsuccess";
  protected static final String           FORWARD_COLLEGAMENTO = "collegaidgaracigsuccess";
  protected static final String           FORWARD_ERROR   = "richiestaidgaracigerror";

  static Logger                           logger          = Logger.getLogger(RichiestaIDGARACIGAction.class);

  private GestioneServiziIDGARACIGManager gestioneServiziIDGARACIGManager;
  
  private AnagraficaSimogManager anagraficaSimogManager;

  public void setGestioneServiziIDGARACIGManager(
      GestioneServiziIDGARACIGManager gestioneServiziIDGARACIGManager) {
    this.gestioneServiziIDGARACIGManager = gestioneServiziIDGARACIGManager;
  }

  public void setAnagraficaSimogManager(
		  AnagraficaSimogManager anagraficaSimogManager) {
	    this.anagraficaSimogManager = anagraficaSimogManager;
  }
  
  
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("RichiestaIDGARACIGAction: inizio metodo");

    String target = FORWARD_SUCCESS;
    String messageKey = null;
    boolean rpntFailed = false;
    String entita = null;
    Long numgara = null;
    Long numlott = null;
    String errore = null;
    String evento = "";
    String messaggioBaseResponse = "";
    String oggevento = "";
    String riferimento = "";
    try {
      entita = request.getParameter("entita");
      numgara = new Long(request.getParameter("numgara"));
      numlott = new Long(request.getParameter("numlott"));
      String recuperaUser = request.getParameter("recuperauser");
      String recuperaPassword = request.getParameter("recuperapassword");
      //String memorizza = request.getParameter("memorizza");
      String rpntFailedString = request.getParameter("rpntFailed");
      if("1".equals(rpntFailedString)) {
        rpntFailed=true;
      }
      
      String inviaConCigNonPresenti = request.getParameter("inviaConCigNonPresenti");
      String codiceCig =request.getParameter("codiceCig");
      String dataCreazioneLotto = request.getParameter("dataCreazioneLotto"); 

      String idgara = null;
      String cig = null;

      request.getSession().removeAttribute("collegaCig");
      if("W3LOTT".equals(entita) && codiceCig!=null && !"".equals(codiceCig)) {
        cig=codiceCig;
        Date dataCreazione = UtilityDate.convertiData(dataCreazioneLotto, UtilityDate.FORMATO_GG_MM_AAAA);
        this.gestioneServiziIDGARACIGManager.aggiornaW3LOTTCIGManuale(numgara, numlott, cig, dataCreazione);
        this.anagraficaSimogManager.setGaraLotto("G", numgara, numlott, null, cig, dataCreazione);
        request.getSession().setAttribute("collegaCig", true);
        
        String ngara = (String) this.gestioneServiziIDGARACIGManager.getSqlManager().getObject("select ngara from w3lott where numgara = ? and numlott = ?", new Object[] { numgara, numlott });
        GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, -1, 1, numgara+"/"+numlott, "ASSOCIA_CIG", "Associazione CIG staccato in SIMOG al lotto (CIG "+cig+", rif. lotto "+ngara, null);
      }else { 
  
        String codrup = request.getParameter("codrup");
        String simogwsuser = null;
        String simogwspass = null;

        // Leggo le eventuali credenziali memorizzate
        HashMap<String, String> hMapSIMOGWSUserPass = new HashMap<String, String>();
        hMapSIMOGWSUserPass = this.gestioneServiziIDGARACIGManager.recuperaSIMOGWSUserPass(codrup);

        // Gestione USER
        if (recuperaUser != null && "1".equals(recuperaUser) && rpntFailed) {
          simogwsuser = ((String) hMapSIMOGWSUserPass.get("simogwsuser"));
        } else {
          simogwsuser = request.getParameter("simogwsuser");
        }

        // Gestione PASSWORD
        if (recuperaPassword != null && "1".equals(recuperaPassword) && rpntFailed) {
          simogwspass = ((String) hMapSIMOGWSUserPass.get("simogwspass"));
        } else {
          simogwspass = request.getParameter("simogwspass");
        }
        // Invio al web service
       
        if ("W3GARA".equals(entita)) {
          evento = "INVIO_GARA";
          messaggioBaseResponse="Risposta SIMOG assegnazione numero ANAC";
          oggevento=numgara.toString();
          String codgar = (String) this.gestioneServiziIDGARACIGManager.getSqlManager().getObject("select codgar from w3gara where numgara = ?", new Object[] {numgara});
          if(StringUtils.stripToNull(codgar)!=null)
            riferimento = " (rif.gara "+codgar.replace("$","")+")";
          GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 0, 1, oggevento, evento, "Invio richiesta a SIMOG assegnazione numero gara"+riferimento, null);  
          
          idgara = this.gestioneServiziIDGARACIGManager.richiestaIDGARA(simogwsuser,
              simogwspass, numgara, rpntFailed);
          
          GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 1, 1, oggevento, evento, messaggioBaseResponse+". N. assegnato "+idgara+riferimento, null);      
          if(idgara!=null) {
          	this.anagraficaSimogManager.setGaraLotto("T", numgara, null, idgara, null, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()));
          }
        } else if ("W3LOTT".equals(entita)){
       
          if(!"1".equals(inviaConCigNonPresenti)) {   
            evento="CONSULTA_GARA";
            messaggioBaseResponse="Risposta SIMOG consultazione anagrafica gara";
            oggevento = numgara.toString();
            List<?> datiW3GARA = this.gestioneServiziIDGARACIGManager.getSqlManager().getVector("select id_gara, codgar from w3gara where numgara = ?", new Object[] { numgara });
            idgara = (String) SqlManager.getValueFromVectorParam(datiW3GARA, 0).getValue();
            String codgar = (String) SqlManager.getValueFromVectorParam(datiW3GARA, 1).getValue();
            riferimento = " (N.gara "+idgara;
            if(StringUtils.stripToNull(codgar)!=null)
              riferimento += ", rif.gara "+codgar.replace("$","");
            riferimento += ")";
            
            GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 0, 1, oggevento, evento, "Invio richiesta a SIMOG consultazione anagrafica gara"+riferimento, null);
            //APPALTI-1061: verifico se il numero di cig in locale è pari al numero di cig su SIMOG: se negativo, sanare la situazione
            HashMap<String, Object> mappaCigNonPresenti = this.gestioneServiziIDGARACIGManager.consultaLottiGara(simogwsuser, simogwspass, numgara,rpntFailed);
               
            if(mappaCigNonPresenti.size()>0) {
              String alert = "Attenzione: in SIMOG sono presenti dei CIG ("+mappaCigNonPresenti.size()+") per la gara non associati ad alcuno dei lotti in anagrafica";
              request.getSession().setAttribute("cigNonPresenti", mappaCigNonPresenti);  
              GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 2, 1, oggevento, evento, messaggioBaseResponse+riferimento, alert);
              return mapping.findForward(FORWARD_ERROR);
            }
            GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 1, 1, oggevento, evento, messaggioBaseResponse+riferimento, null);
          }
           
          evento="INVIO_LOTTO";
          messaggioBaseResponse="Risposta SIMOG assegnazione CIG";
          oggevento = numgara+"/"+numlott;
          String ngara = (String) this.gestioneServiziIDGARACIGManager.getSqlManager().getObject("select ngara from w3lott where numgara = ? and numlott = ?", new Object[] { numgara, numlott });
          if(StringUtils.stripToNull(ngara)!=null)
            riferimento = " (rif.gara "+ngara+")";
          
          GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 0, 1, oggevento, evento, "Invio richiesta a SIMOG assegnazione CIG"+riferimento, null);      
          cig = this.gestioneServiziIDGARACIGManager.richiestaCIG(simogwsuser,
              simogwspass, numgara, numlott,rpntFailed);

           if(cig!=null) {
            	this.anagraficaSimogManager.setGaraLotto("G", numgara, numlott, null, cig, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()));
           }
           GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 1, 1, oggevento, evento, messaggioBaseResponse+". CIG assegnato "+cig+riferimento, null);      
           
        } else if ("W3SMARTCIG".equals(entita)){
          List<?> datiW3SMARTCIG = this.gestioneServiziIDGARACIGManager.getSqlManager().getVector("select cig, codgar from w3smartcig where codrich = ?", new Object[] { numgara });
          String cigValorizzato = (String) SqlManager.getValueFromVectorParam(datiW3SMARTCIG, 0).getValue();
          String codgar = (String) SqlManager.getValueFromVectorParam(datiW3SMARTCIG, 1).getValue();
          String messaggioBaseRequest = "Invio richiesta a SIMOG assegnazione smartCIG";
          messaggioBaseResponse="Risposta SIMOG assegnazione smartCIG";
          oggevento = numgara.toString();
          evento="INVIO_SMARTCIG";
          if(StringUtils.stripToNull(cigValorizzato)!=null || StringUtils.stripToNull(codgar)!=null) {
            riferimento=" (";
            if(StringUtils.stripToNull(cigValorizzato)!=null) {
              evento="MODIFICA_SMARTCIG";
              messaggioBaseRequest =  "Invio richiesta a SIMOG modifica smartCIG";
              messaggioBaseResponse="Risposta SIMOG modifica smartCIG";
              riferimento+="CIG "+cigValorizzato;
            }
            
            if(StringUtils.stripToNull(codgar)!=null) {
              if(StringUtils.stripToNull(cigValorizzato)!=null)
                riferimento+=", ";
              riferimento += "rif.gara "+codgar.replace("$","");
            }
            riferimento +=")";
          }
          
          GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 0, 1, oggevento, evento, messaggioBaseRequest+riferimento, null);
          cig = this.gestioneServiziIDGARACIGManager.richiestaSMARTCIG(simogwsuser,
              simogwspass, numgara,rpntFailed);
          
          if(cig!=null) {
          	this.anagraficaSimogManager.setGaraLotto("S", numgara, numlott, null, cig, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()));
          }
          
          if("MODIFICA_SMARTCIG".equals(evento))
            GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 1, 1, oggevento, evento, messaggioBaseResponse+riferimento, null);  
          else
            GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 1, 1, oggevento, evento, messaggioBaseResponse+". CIG assegnato "+cig+riferimento, null);
        }

        target = FORWARD_SUCCESS;
      }
      
      request.getSession().setAttribute("entita", entita);
      request.getSession().setAttribute("numgara", numgara);
      request.getSession().setAttribute("numlott", numlott);
      request.getSession().setAttribute("idgara", idgara);
      request.getSession().setAttribute("cig", cig);
      request.getSession().setAttribute("numeroPopUp", "1");
      request.getSession().removeAttribute("erroreInvioRichiestaSimog");
      request.getSession().removeAttribute("cigNonPresenti");
     
    } catch (GestoreException e) {
      errore=e.getMessage();
      target = FORWARD_ERROR;
      messageKey = "errors.gestioneIDGARACIG.error";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey, e.getMessage());
      if(rpntFailed) {
        request.setAttribute("erroreInvioRichiestaSimog", "true");
      }else {
        if(e.getMessage().indexOf(GestioneServiziIDGARACIGManager.LOGIN_SIMOG_ERRATA)>-1) {
          request.getSession().setAttribute("erroreCredenzialiRPNT", "true");
        }
        else {
          request.setAttribute("erroreCredenzialiRPNT", "true");
        }
      }
    } catch (Throwable e) {
      errore=e.getMessage();
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if(errore!=null)
        GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 1, 3, oggevento, evento, messaggioBaseResponse+riferimento, errore);        

    if (messageKey != null) response.reset();

    if (logger.isDebugEnabled())
      logger.debug("RichiestaIDGARACIGAction: fine metodo");

    return mapping.findForward(target);

  }

}
