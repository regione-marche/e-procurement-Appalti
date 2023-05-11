/*
 * Created on 28-giu-2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPAnagraficaType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore di plugin dei dati generali di GARE
 *
 * @author Riccardo.Peruzzo
 */
public class GestoreDatiGeneraliTorn extends AbstractGestorePreload {

	SqlManager sqlManager = null;
	
	TabellatiManager tabellatiManager = null;
	
	PgManager pgManager = null;

	private GestioneWSERPManager gestioneWSERPManager = null;

	public GestoreDatiGeneraliTorn(BodyTagSupportGene tag) {
		super(tag);
	}
	
	
	/**
	 * Viene tracciato il log dell'accesso al dettaglio scheda per gare a lotti
	 */
	@Override
	public void doBeforeBodyProcessing(PageContext page, String AperturaScheda) throws JspException {
		
		tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", page, TabellatiManager.class);
		
	    pgManager = (PgManager) UtilitySpring.getBean("pgManager", page, PgManager.class);

		sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
			page, SqlManager.class);

		gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
			page, GestioneWSERPManager.class);

		HashMap key = null;
		String oggettoEvento = null;
		
		String integrazioneWSERP="0";
		String tipoWSERP = "";
		String urlWSERP = ConfigManager.getValore("wserp.erp.url");
		urlWSERP = UtilityStringhe.convertiNullInStringaVuota(urlWSERP);
		if(!"".equals(urlWSERP)){
			integrazioneWSERP ="1";

			try {
				WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
				if (configurazione.isEsito()) {
					tipoWSERP = configurazione.getRemotewserp();
				}
			} catch (GestoreException e) {
				UtilityStruts.addMessage(page.getRequest(), "error",
					"wserpconfigurazione.erp.configurazioneleggi.remote.error",new Object[]{":\r\n Configurazione integrazione con sistema ERP non corretta"});
			}
		}

		String modo = (String) page.getAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO, PageContext.REQUEST_SCOPE);

		if (UtilityTags.SCHEDA_MODO_VISUALIZZA.equals(modo)) {
			key = UtilityTags.stringParamsToHashMap(
					(String) page.getAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, PageContext.REQUEST_SCOPE),
					null);
			String codgar = ((JdbcParametro) key.get("TORN.CODGAR")).getStringValue();
			oggettoEvento = codgar;
			String log = (String) page.getAttribute("log");
			if ("true".equals(log)) {

				try {
					LogEvento logEvento = LogEventiUtils.createLogEvento((HttpServletRequest) page.getRequest());
					logEvento.setLivEvento(1);
					logEvento.setOggEvento(oggettoEvento);
					logEvento.setCodEvento("GA_ACCESSO_PROCEDURA");
					logEvento.setDescr("Accesso al dettaglio della gara");
					logEvento.setErrmsg("");
					LogEventiUtils.insertLogEventi(logEvento);
				} catch (Exception le) {
					String messageKey = "errors.logEventi.inaspettataException";
				}
			}
		}else if(UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modo)) {
	        //Visualizzazione sul Portale dell'espletamento di gara telematica
	        String defA1115_8 = tabellatiManager.getDescrTabellato("A1115", "8");
	        boolean resDefA1115_8 = false;
	        if(defA1115_8 != null)
	        	resDefA1115_8 = defA1115_8.startsWith("2");
	        if (resDefA1115_8){
	        	page.setAttribute("initESPLPORT", "2", PageContext.REQUEST_SCOPE);
	        }else {
	        	page.setAttribute("initESPLPORT", "1", PageContext.REQUEST_SCOPE);
	        }
	        
			if("RAIWAY".equals(tipoWSERP)){
				String numeroRda = page.getRequest().getParameter("numeroRda");
				if(numeroRda!=null) {
					String divisione = page.getRequest().getParameter("divisione");
					divisione = UtilityStringhe.convertiNullInStringaVuota(divisione);
					try {
						this.setDatiInitDaWSERP(page, sqlManager, tipoWSERP, numeroRda, divisione);
					} catch (GestoreException e) {
						throw new JspException(
							"Errore in fase di esecuzione delle select di inizializzazione", e);
					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
				}
				
			}

		}

	}

	@Override
	public void doAfterFetch(PageContext page, String modoAperturaScheda) throws JspException {
		// TODO Auto-generated method stub

	}

	private void setDatiInitDaWSERP(PageContext page, SqlManager sqlManager, String tipoWSERP, String numeroRda, String esercizio)
		throws GestoreException, SQLException {
		ProfiloUtente profilo = (ProfiloUtente) page.getSession().getAttribute(
			CostantiGenerali.PROFILO_UTENTE_SESSIONE);
		Long syscon = new Long(profilo.getId());
		String profiloAttivo = (String) page.getSession().getAttribute("profiloAttivo");
		String servizio = page.getRequest().getParameter("servizio");
		if(servizio==null || "".equals(servizio))
			servizio ="WSERP";
		String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);
		String username = credenziali[0];
		String password = credenziali[1];
		WSERPRdaType erpSearch = new WSERPRdaType();
		//imposto il num Rda come filtro ed esercizio obbligatorio,per ora metto fisso
		erpSearch.setCodiceRda(numeroRda);
		erpSearch.setEsercizio(esercizio);
		WSERPRdaResType wserpRdaRes = null;
		if("RAIWAY".equals(tipoWSERP) && numeroRda!=null){
			wserpRdaRes = this.gestioneWSERPManager.wserpListaRda(username, password, servizio, erpSearch );
			WSERPRdaType[] rdaArray = wserpRdaRes.getRdaArray();
			if(rdaArray!= null){
				WSERPRdaType rda = rdaArray[0];
				if ("RAIWAY".equals(tipoWSERP)){
					String codiceRda = rda.getCodiceRda();
					codiceRda = UtilityStringhe.convertiNullInStringaVuota(codiceRda);
					String oggetto = rda.getOggetto();
					// chiedere
					page.setAttribute("initNUMRDA",codiceRda, PageContext.REQUEST_SCOPE);
					boolean inserimentoGarerda= true;
					page.setAttribute("initGarerda", new Boolean(inserimentoGarerda),PageContext.REQUEST_SCOPE);
					page.setAttribute("initNUMRDA",codiceRda, PageContext.REQUEST_SCOPE);
					Date dataConsegna = null;
					if(dataConsegna != null){
						rda.getDataConsegna().getTime();
						page.setAttribute("initDATRIL",  UtilityDate.convertiData(
								dataConsegna, UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);
					}

					Date dataCreazione = null;
					if(dataCreazione != null){
						rda.getDataCreazioneRda().getTime();
						page.setAttribute("initDATCRE", UtilityDate.convertiData(
								dataCreazione, UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);
					}
		            String divisione = rda.getDivisione();
		            page.setAttribute("initSTRUTTURA", divisione, PageContext.REQUEST_SCOPE);
					page.setAttribute("initDESTOR", oggetto, PageContext.REQUEST_SCOPE);
					String sceltaContraente = rda.getSceltaContraente();
					if(!"".equals(sceltaContraente)){
						page.setAttribute("initTIPGAR", sceltaContraente, PageContext.REQUEST_SCOPE);
                        Long iterga = this.pgManager.getITERGA(Long.valueOf(sceltaContraente));
                        page.setAttribute("initITERGA", iterga, PageContext.REQUEST_SCOPE);
					}
					Double valoreStimato = rda.getValoreStimato();
					page.setAttribute("initIMPTOR", valoreStimato, PageContext.REQUEST_SCOPE);
					Boolean green = rda.getGreen();
					if(green != null){
						if(green){
							page.setAttribute("initISGREEN", "1", PageContext.REQUEST_SCOPE);
							String motGreen = rda.getMotGreen();
							page.setAttribute("initMOTGREEN", motGreen, PageContext.REQUEST_SCOPE);
						} else {
							page.setAttribute("initISGREEN", "2", PageContext.REQUEST_SCOPE);
						}
					}
					WSERPAnagraficaType[] wserpAnagraficaArray = rda.getTecniciArray();
					if(wserpAnagraficaArray !=null){
						for(int i=0; i < wserpAnagraficaArray.length; i++){
							WSERPAnagraficaType wserpAnagrafica = wserpAnagraficaArray[i];
							if(wserpAnagrafica != null){
								Vector<?> datiTecnico = sqlManager.getVector("select codtec from tecni where nomtec = ?", new Object[] { wserpAnagrafica.getDenominazione() });
								if (datiTecnico != null && !datiTecnico.isEmpty()) {
									String codtec = (String) ((JdbcParametro) datiTecnico.get(0)).getValue();
									page.setAttribute("initCODRUP",codtec, PageContext.REQUEST_SCOPE);
									page.setAttribute("initNOMTEC1",wserpAnagrafica.getDenominazione(), PageContext.REQUEST_SCOPE);
								}
							}
						}
					}
				}
			}
			
		}

	}
}