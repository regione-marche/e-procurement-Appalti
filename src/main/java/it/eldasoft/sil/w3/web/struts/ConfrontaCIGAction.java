package it.eldasoft.sil.w3.web.struts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.avlp.simog.massload.xmlbeans.SchedaType;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.w3.bl.GestioneServiziIDGARACIGManager;
import it.eldasoft.sil.w3.bl.GestioneXMLIDGARACIGManager;
import it.eldasoft.sil.w3.bl.ValidazioneIDGARACIGManager;

/**
 * Action per lo scarico del CIG da SIMOG, il caricamento dei dati in locale
 * per l'apertura della popup di confronto dei dati. 
 * 
 * @author luca.giacomazzo
 *
 */
public class ConfrontaCIGAction extends DispatchActionBaseNoOpzioni {

	static Logger                           logger          = Logger.getLogger(ConsultaGaraLottoAction.class);

	  protected static final String           FORWARD_SUCCESS = "confrontacigsuccess";
	  protected static final String           FORWARD_ERROR   = "confrontacigerror";
	
	  protected static final String           FORWARD_SUCCESS_RIALLIENA = "riallineacigsuccess";
	  
	private GestioneServiziIDGARACIGManager gestioneServiziIDGARACIGManager;

	private GestioneXMLIDGARACIGManager gestioneXMLIDGARACIGManager;
	
	private ValidazioneIDGARACIGManager validazioneIDGARACIGManager;
	
	public void setGestioneServiziIDGARACIGManager(GestioneServiziIDGARACIGManager gestioneServiziIDGARACIGManager) {
		this.gestioneServiziIDGARACIGManager = gestioneServiziIDGARACIGManager;
	}

	public void setGestioneXMLIDGARACIGManager(GestioneXMLIDGARACIGManager gestioneXMLIDGARACIGManager) {
		this.gestioneXMLIDGARACIGManager = gestioneXMLIDGARACIGManager;
	}
	
	public void setValidazioneIDGARACIGManager(ValidazioneIDGARACIGManager validazioneIDGARACIGManager) {
		this.validazioneIDGARACIGManager = validazioneIDGARACIGManager;
	}
	
	
	/** 
	 * Metodo per il controllo dell'utente e la lista delle collaborazioni
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	public ActionForward inizializza(ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		if (logger.isDebugEnabled())
	      logger.debug("inizializza: inizio metodo");

		String target = FORWARD_ERROR;
		String messageKey = null;

		try {
			Long numgara = Long.parseLong(request.getParameter("numgara"));
			Long numlott = Long.parseLong(request.getParameter("numlott"));

			ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
			Long syscon = (long) profilo.getId();
			
			HashMap<String, Object> infoValidazione = new HashMap<String, Object>();
			infoValidazione = this.validazioneIDGARACIGManager.validateRUP(numgara, syscon, numlott);

			int numeroErrori = 0;

			if (infoValidazione.get("numeroErrori") != null) {
				numeroErrori = ((Long) infoValidazione.get("numeroErrori")).intValue();
			}

			if (numeroErrori == 0) {

			} else {
				
				request.setAttribute("listaControlli", infoValidazione.get("listaControlli"));
				request.setAttribute("numeroWarning", infoValidazione.get("numeroWarning"));
				request.setAttribute("numeroErrori", infoValidazione.get("numeroErrori"));
			}

	    } catch (Throwable e) {
	    	target = FORWARD_ERROR;
	    	messageKey = "errors.applicazione.inaspettataException";
	    	logger.error(this.resBundleGenerale.getString(messageKey), e);
	    	this.aggiungiMessaggio(request, messageKey);
	    }

		if (messageKey != null) response.reset();

	    if (logger.isDebugEnabled())
	    	logger.debug("inizializza: fine metodo");

	    return mapping.findForward(target);
	}
	
	/** 
	 * Metodo per la visualizzazione dei dati a confronto
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	public ActionForward avvioConfronto(ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		if (logger.isDebugEnabled())
	      logger.debug("avvioConfronto: inizio metodo");

		String target = FORWARD_ERROR;
		String messageKey = null;

	    boolean rpntFailed = false;

		try {
			String cig = request.getParameter("cig");
			Long numgara = Long.parseLong(request.getParameter("numgara"));
			Long numlott = Long.parseLong(request.getParameter("numlott"));
			
			
			String recuperaUser = request.getParameter("recuperauser");
			String recuperaPassword = request.getParameter("recuperapassword");
			String rpntFailedString = request.getParameter("rpntFailed");
			if("1".equals(rpntFailedString)) {
		        rpntFailed=true;
		      }
			String codUffInt = (String) request.getSession().getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
	    
			String codrup = request.getParameter("codrup");
			String simogwsuser = null;
			String simogwspass = null;

			// Leggo le eventuali credenziali memorizzate
			HashMap<String, String> hMapSIMOGWSUserPass = new HashMap<String, String>();
			hMapSIMOGWSUserPass = this.gestioneServiziIDGARACIGManager.recuperaSIMOGWSUserPass(codrup);

			// Gestione USER
			if (recuperaUser != null && "1".equals(recuperaUser)) {
				simogwsuser = (hMapSIMOGWSUserPass.get("simogwsuser"));
			} else {
				simogwsuser = request.getParameter("simogwsuser");
			}

			// Gestione PASSWORD
			if (recuperaPassword != null && "1".equals(recuperaPassword)) {
				simogwspass = (hMapSIMOGWSUserPass.get("simogwspass"));
			} else {
				simogwspass = request.getParameter("simogwspass");
			}

			// Invio al web service
			HashMap<String, Object> hMapConfrontaGaraLotto = new HashMap<String, Object>();
			hMapConfrontaGaraLotto = this.gestioneServiziIDGARACIGManager.confrontaGaraLotto(simogwsuser, simogwspass, cig, codUffInt,rpntFailed);

			if (!hMapConfrontaGaraLotto.isEmpty()) {
				if (hMapConfrontaGaraLotto.containsKey("schedaDatiSIMOG")) {
					request.getSession().setAttribute("schedaDatiSIMOG", hMapConfrontaGaraLotto.get("schedaDatiSIMOG"));
				}
				if (hMapConfrontaGaraLotto.containsKey("schedaDatiVigilanza")) {
					request.getSession().setAttribute("schedaDatiVigilanza", hMapConfrontaGaraLotto.get("schedaDatiVigilanza"));
				}
				
				Set<String> insiemeChiaviHashMap = hMapConfrontaGaraLotto.keySet();
				Iterator<String> iter = insiemeChiaviHashMap.iterator();
				while (iter.hasNext()) {
					String temp = iter.next();
					if (temp.startsWith("lista")) {
						request.setAttribute(temp, hMapConfrontaGaraLotto.get(temp));
					}
				}
			}
			
			target = FORWARD_SUCCESS;

		} catch (GestoreException e) {
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
	    	target = FORWARD_ERROR;
	    	messageKey = "errors.applicazione.inaspettataException";
	    	logger.error(this.resBundleGenerale.getString(messageKey), e);
	    	this.aggiungiMessaggio(request, messageKey);
	    }

		if (messageKey != null) response.reset();

	    if (logger.isDebugEnabled())
	    	logger.debug("avvioConfronto: fine metodo");

	    return mapping.findForward(target);
	}

	/**
	 * Metodo per riallineare i dati di SIMOG in locale
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	public ActionForward riallineaDati(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) throws IOException, ServletException {

		if (logger.isDebugEnabled())
			logger.debug("riallineaDati: inizio metodo");

		String target = FORWARD_SUCCESS_RIALLIENA;
		String messageKey = null;	
		boolean esito = true;
				
		try {
			String cig = request.getParameter("cig");
			String codrup = request.getParameter("codrup");
			
			ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
			Long syscon = (long) profilo.getId();
			String codUffInt = (String) request.getSession().getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
			
			SchedaType schedaDatiSIMOG = (SchedaType) request.getSession().getAttribute("schedaDatiSIMOG");

			if (schedaDatiSIMOG != null) {
				esito = this.gestioneXMLIDGARACIGManager.riallineaGaraLottodaSIMOG(schedaDatiSIMOG, cig, syscon, codUffInt, codrup);
			} else {
				esito = false;
			}
			
			request.getSession().removeAttribute("schedaDatiSIMOG");
			request.getSession().removeAttribute("schedaDatiVigilanza");
			
		} catch(GestoreException ge) {
			logger.error("Errore nell'operazione di riallineamento dei dati", ge);
			target = FORWARD_ERROR;
			
		} catch (Throwable t) {
			logger.error("Errore inaspettato nell'operazione di riallineamento dei dati", t);
			target = FORWARD_ERROR;
		}
		
		request.setAttribute("esito", esito);
			
		if (logger.isDebugEnabled())
			logger.debug("riallineaDati: fine metodo");

		return mapping.findForward(target);
	}
}
