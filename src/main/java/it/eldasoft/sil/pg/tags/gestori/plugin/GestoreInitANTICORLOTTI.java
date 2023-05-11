/*
 * Created on 30-08-2017
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
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

/**
 * Inizializzazione della scheda ANTICORLOTTI.
 * 
 * @author Luca.Giacomazzo
 */
public class GestoreInitANTICORLOTTI extends AbstractGestorePreload {

	public GestoreInitANTICORLOTTI(BodyTagSupportGene tag) {
		super(tag);
	}

	@Override
	public void doBeforeBodyProcessing(PageContext pageContext,
			String modoAperturaScheda) throws JspException {
		
		if ("NUOVO".equals(modoAperturaScheda)) {
			ProfiloUtente profiloUtente = (ProfiloUtente) pageContext.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
			if ("U".equals(profiloUtente.getAbilitazioneGare())) {
				
				pageContext.setAttribute("codiceFiscaleTecnico", profiloUtente.getCodiceFiscale().toUpperCase(), PageContext.REQUEST_SCOPE);
				
				SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
					pageContext, SqlManager.class);
				try {
					String nomeTec = (String) sqlManager.getObject(
						"select NOMTEC from TECNI where UPPER(CFTEC)=?",
							new Object[] { profiloUtente.getCodiceFiscale().toUpperCase() } );
					
					if (StringUtils.isNotEmpty(nomeTec)) {
						pageContext.setAttribute("nomeTecnico", nomeTec, PageContext.REQUEST_SCOPE);
					} else {
						pageContext.setAttribute("nomeTecnico", profiloUtente.getNome(), PageContext.REQUEST_SCOPE);
					}
				} catch (SQLException e) {
					throw new JspException(
						"Errore durante l'estrazione del nome del tecnico", e);
			    }
			}
		}
	}

	@Override
	public void doAfterFetch(PageContext pageContext, String modoAperturaScheda)
			throws JspException {

	}

}
