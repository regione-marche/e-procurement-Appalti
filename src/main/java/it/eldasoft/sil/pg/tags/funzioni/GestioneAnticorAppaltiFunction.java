/*
 * Created on 30-08-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

/**
 * Funzione per l'inizializzazione delle pagine 'anticor-pg-appalti.jsp'
 * 
 * @author Marcello Caminiti
 */
public class GestioneAnticorAppaltiFunction extends AbstractFunzioneTag {

  public GestioneAnticorAppaltiFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String updateLista = UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA);
    if (updateLista == null || updateLista.length() == 0) updateLista = "0";
    pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA,
        updateLista, PageContext.REQUEST_SCOPE);

    String chiave = (String) params[1];
    String id = chiave.substring(chiave.indexOf(':') + 1);

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String completato = null;
    String esportato = null;
    Long annorif = null;
    String pubblicato = null;
    Long countIdANTICORLOTTI = null;

    ProfiloUtente profiloUtente = (ProfiloUtente) pageContext.getSession().getAttribute(
    		CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    
    try {
	    if ("U".equals(profiloUtente.getAbilitazioneGare())) {
	    	String nomeTecnico = (String) sqlManager.getObject("select NOMTEC from TECNI where UPPER(CFTEC)=?", 
	    			new Object[] { profiloUtente.getCodiceFiscale().toUpperCase()} );
	    	
	    	if (StringUtils.isNotEmpty(nomeTecnico)) {
	    		pageContext.setAttribute("nomeResponsabile", nomeTecnico);
	    		pageContext.setAttribute("cfResponsabile", profiloUtente.getCodiceFiscale().toUpperCase());
	    	} else {
	    		pageContext.setAttribute("nomeResponsabile", profiloUtente.getNome());
	    		pageContext.setAttribute("cfResponsabile", profiloUtente.getCodiceFiscale().toUpperCase());
	    	}
	    }
    } catch (SQLException e) {
        throw new JspException("Errore nella lettura dei dati di IMPR per la denominazione del tecnico con CF="
        		+ profiloUtente.getCodiceFiscale(), e);
    }
    
    try {
      Vector<?> datiAnticor = sqlManager.getVector(
          "select completato,esportato, annorif, pubblicato from anticor where id=?",
          new Object[] { new Long(id) });
      countIdANTICORLOTTI = (Long) sqlManager.getObject(
          "select count(ID) from ANTICORLOTTI where IDANTICOR=? and PUBBLICA='1'",
          new Object[] { new Long(id) });
      if (datiAnticor != null && datiAnticor.size() > 0) {
        completato = SqlManager.getValueFromVectorParam(datiAnticor, 0).getStringValue();
        esportato = SqlManager.getValueFromVectorParam(datiAnticor, 1).getStringValue();
        annorif = SqlManager.getValueFromVectorParam(datiAnticor, 2).longValue();
        pubblicato = SqlManager.getValueFromVectorParam(datiAnticor, 3).getStringValue();
      }
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura dei dati di ANTICOR con ID=" + id, e);
    } catch (GestoreException e) {
      throw new JspException("Errore nella lettura dei dati di ANTICOR con ID=" + id, e);
    }
    pageContext.setAttribute("countIdANTICORLOTTI", countIdANTICORLOTTI, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("esportato", esportato, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("annorif", annorif, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("pubblicato", pubblicato, PageContext.REQUEST_SCOPE);

    return completato;
  }

}