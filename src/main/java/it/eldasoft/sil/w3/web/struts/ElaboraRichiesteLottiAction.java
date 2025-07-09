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
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AnagraficaSimogManager;
import it.eldasoft.sil.w3.bl.GestioneServiziIDGARACIGManager;
import it.eldasoft.sil.w3.bl.ValidazioneIDGARACIGManager;
import it.eldasoft.utils.utility.UtilityDate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ElaboraRichiesteLottiAction extends ActionBaseNoOpzioni {

  protected static final String           FORWARD_SUCCESS = "elaborarichiestelottisuccess";
  protected static final String           FORWARD_WARNING = "elaborarichiestelottiwarning";
  protected static final String           FORWARD_ERROR = "elaborarichiestelottierror";

  static Logger                           logger          = Logger.getLogger(ElaboraRichiesteLottiAction.class);

  private SqlManager                      sqlManager;

  private GestioneServiziIDGARACIGManager gestioneServiziIDGARACIGManager;
 
  private ValidazioneIDGARACIGManager     validazioneIDGARACIGManager;
  
  private AnagraficaSimogManager anagraficaSimogManager;

  public SqlManager getSqlManager() {
    return this.sqlManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneServiziIDGARACIGManager(
      GestioneServiziIDGARACIGManager gestioneServiziIDGARACIGManager) {
    this.gestioneServiziIDGARACIGManager = gestioneServiziIDGARACIGManager;
  }

  public void setValidazioneIDGARACIGManager(
      ValidazioneIDGARACIGManager validazioneIDGARACIGManager) {
    this.validazioneIDGARACIGManager = validazioneIDGARACIGManager;
  }
  
  public void setAnagraficaSimogManager(
		  AnagraficaSimogManager anagraficaSimogManager) {
	    this.anagraficaSimogManager = anagraficaSimogManager;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("ElaboraRichiesteLottiAction: inizio metodo");

    String target = FORWARD_SUCCESS;
    String messageKey = null;
    boolean rpntFailed = false;
    Long numgara = null;
    String errore = null;
    String evento = "";
    String messaggioBaseResponse = "";
    String oggevento = "";
    String riferimentoG = "";
    String riferimento = "";
    Long numeroRichiesteDaElaborare = 0L;
    Long numeroRichiesteElaborate = 0L;
    Long numeroRichiesteNonValide = 0L;
    String msg="Fine richiesta CIG massiva a SIMOG";
    
    try {
      numgara = Long.parseLong(request.getParameter("numgara"));
      String recuperaUser = request.getParameter("recuperauser");
      String recuperaPassword = request.getParameter("recuperapassword");
      String inviaConCigNonPresenti = request.getParameter("inviaConCigNonPresenti");
      String rpntFailedString = request.getParameter("rpntFailed");

      if("1".equals(rpntFailedString)) {
        rpntFailed=true;
      }

      ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = (long) profilo.getId();

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

      // Gestione lista dei lotti
      List<?> datiW3LOTT = sqlManager.getListVector(
          "select numlott, oggetto, stato_simog from w3lott where numgara = ? and stato_simog in (1,3) order by stato_simog, numlott",
          new Object[] { numgara });

      List<Object> listaStatoElaborazioneRichieste = new Vector<Object>();
      
      

      if (datiW3LOTT != null && datiW3LOTT.size() > 0) {
        numeroRichiesteDaElaborare = (long) datiW3LOTT.size();
        messaggioBaseResponse="Risposta SIMOG modifica anagrafica gara";
        oggevento=numgara.toString();
        List<?> datiW3GARA = this.gestioneServiziIDGARACIGManager.getSqlManager().getVector("select id_gara, codgar from w3gara where numgara = ?", new Object[] { numgara }); 
        String idgara = (String) SqlManager.getValueFromVectorParam(datiW3GARA, 0).getValue();
        String codgar = (String) SqlManager.getValueFromVectorParam(datiW3GARA, 1).getValue();
        riferimentoG = " (N.gara "+idgara;
        if(StringUtils.stripToNull(codgar)!=null)
          riferimentoG += ", rif.gara "+codgar.replace("$","");
        riferimentoG += ")";
        riferimento = riferimentoG;
        
        GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, -1, 1, oggevento, "INIT_RICMASSIVA", "Avvio richiesta CIG massiva a SIMOG: "+numeroRichiesteDaElaborare+" lotti da elaborare"+riferimento, null);  
        
        for (int i = 0; i < datiW3LOTT.size(); i++) {
          Long numlott = (Long) SqlManager.getValueFromVectorParam(datiW3LOTT.get(i), 0).getValue();
          String oggetto = (String) SqlManager.getValueFromVectorParam(datiW3LOTT.get(i), 1).getValue();
          Long stato_simog = (Long) SqlManager.getValueFromVectorParam(datiW3LOTT.get(i), 2).getValue();

          HashMap infoValidazione = new HashMap();
          infoValidazione = this.validazioneIDGARACIGManager.validateW3LOTT(syscon, numgara, numlott);

          int numeroErrori = 0;

          if (infoValidazione.get("numeroErrori") != null) {
            numeroErrori = ((Long) infoValidazione.get("numeroErrori")).intValue();
          }

          if (numeroErrori == 0) {
            if ((new Long(1)).equals(stato_simog)) {
              if(!"1".equals(inviaConCigNonPresenti) && i==0) {
                //APPALTI-1061: verifico se il numero di cig in locale è pari al numero di cig su SIMOG: se negativo, sanare la situazione
                evento="CONSULTA_GARA";
                messaggioBaseResponse="Risposta SIMOG consultazione anagrafica gara";
                GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 0, 1, oggevento, evento, "Invio richiesta a SIMOG consultazione anagrafica gara"+riferimento, null);
                              
                HashMap<String, Object> mappaCigNonPresenti = this.gestioneServiziIDGARACIGManager.consultaLottiGara(simogwsuser, simogwspass, numgara,rpntFailed);
                if(mappaCigNonPresenti.size()>0) {
                  String alert = "Attenzione: in SIMOG sono presenti dei CIG ("+mappaCigNonPresenti.size()+") per la gara non associati ad alcuno dei lotti in anagrafica";
                  request.getSession().setAttribute("cigNonPresenti", mappaCigNonPresenti);
                  GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 1, 2, oggevento, evento, messaggioBaseResponse+riferimento, alert);
                  GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, -1, 1, oggevento, "FINE_RICMASSIVA", msg+riferimento, null);  
                  return mapping.findForward(FORWARD_WARNING);
                }
                GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 1, 1, oggevento, evento, messaggioBaseResponse+riferimento, null);
              }
              //APPALTI-1061 fine
              
              evento="INVIO_LOTTO";
              messaggioBaseResponse="Risposta SIMOG assegnazione CIG";
              oggevento = numgara+"/"+numlott;
              String ngara = (String) this.gestioneServiziIDGARACIGManager.getSqlManager().getObject("select ngara from w3lott where numgara = ? and numlott = ?", new Object[] { numgara, numlott });
              if(StringUtils.stripToNull(ngara)!=null)
                riferimento = " (rif.gara "+ngara+")";
              
              GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 0, 1, oggevento, evento, "Invio richiesta a SIMOG assegnazione CIG"+riferimento, null);
              String cig = this.gestioneServiziIDGARACIGManager.richiestaCIG(simogwsuser, simogwspass, numgara, numlott,rpntFailed);
              if(cig!=null) {
              	this.anagraficaSimogManager.setGaraLotto("G", numgara, numlott, null, cig, 
              			new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()));
              }
              GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 1, 1, oggevento, evento, messaggioBaseResponse+". CIG assegnato "+cig+riferimento, null);      
              
            } else {
              evento="MODIFICA_LOTTO";
              messaggioBaseResponse="Risposta SIMOG modifica anagrafica lotto";
              oggevento = numgara+"/"+numlott;
              List<?> datiWLOTT = this.gestioneServiziIDGARACIGManager.getSqlManager().getVector("select ngara, cig from w3lott where numgara = ? and numlott = ?", new Object[] { numgara, numlott });
              String ngara = (String) SqlManager.getValueFromVectorParam(datiWLOTT, 0).getValue();
              String cig = (String) SqlManager.getValueFromVectorParam(datiWLOTT, 1).getValue();
              riferimento = " (CIG "+cig;
              if(StringUtils.stripToNull(ngara)!=null)
                riferimento += ", rif.gara "+ngara;
              riferimento+=")";
              
              GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 0, 1, oggevento, evento, "Invio richiesta a SIMOG modifica anagrafica lotto"+riferimento, null);  
              this.gestioneServiziIDGARACIGManager.modificaLOTTO(simogwsuser, simogwspass, numgara, numlott,rpntFailed);
              GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 1, 1, oggevento, evento, messaggioBaseResponse+riferimento, null);      
            }
            numeroRichiesteElaborate = numeroRichiesteElaborate.longValue() + 1;
          } else {
            listaStatoElaborazioneRichieste.add(((Object) (new Object[] { numlott, oggetto })));
            numeroRichiesteNonValide++;
          }
        }
      }

      target = FORWARD_SUCCESS;
      request.getSession().setAttribute("numeroRichiesteDaElaborare", numeroRichiesteDaElaborare);
      request.getSession().setAttribute("numeroRichiesteElaborate", numeroRichiesteElaborate);
      request.getSession().setAttribute("listaStatoElaborazioneRichieste", listaStatoElaborazioneRichieste);
      request.getSession().setAttribute("numgara", numgara);
      request.getSession().setAttribute("numeroPopUp", "1");
    } catch (GestoreException e) {
      errore = e.getMessage();
      target = FORWARD_ERROR;
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
      errore = e.getMessage();
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }
    if(errore!=null)
      GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, 1, 3, oggevento, evento, messaggioBaseResponse+riferimento, errore);
    
    if(numeroRichiesteDaElaborare>0) {
      int err=0;
      if(errore!=null)
        err=1;
      
      Long diff = numeroRichiesteDaElaborare-numeroRichiesteElaborate-numeroRichiesteNonValide-err;

      msg += ": "+numeroRichiesteElaborate+" richieste elaborate";
      msg += ", "+numeroRichiesteNonValide+" richieste non elaborate per controllo dati non superato"; 
      msg += ", "+err+" richieste non elaborate a causa di errori";
      msg += ", "+(diff)+" richieste non elaborate a causa di errori sulle elaborazioni precedenti ";

      GestioneServiziIDGARACIGManager.loggaEventoSIMOG(request, -1, 1, numgara.toString(), "FINE_RICMASSIVA", 
      msg+riferimentoG, null);  
    }
    if (messageKey != null) response.reset();

    if (logger.isDebugEnabled())
      logger.debug("ElaboraRichiesteLottiAction: fine metodo");

    return mapping.findForward(target);

  }

}
