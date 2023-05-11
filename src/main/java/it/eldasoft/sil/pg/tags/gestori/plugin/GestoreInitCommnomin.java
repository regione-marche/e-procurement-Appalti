/*
 * Created on 30-06-2015
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore preload per la pagina scheda COMMNOMIN. Si occupa di caricare tutti i dati
 * relativi alla scheda nominativo
 *
 * @author C.F.
 */
public class GestoreInitCommnomin extends AbstractGestorePreload {

    SqlManager sqlManager = null;

    public GestoreInitCommnomin(BodyTagSupportGene tag) {
	super(tag);
    }

    @Override
    public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda) throws JspException {

	sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", page, SqlManager.class);

	String modo = (String) page.getAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO, PageContext.REQUEST_SCOPE);

	try {
	    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modo)) {

		// carico la chiave dell'occorrenza
		HashMap key = UtilityTags.stringParamsToHashMap(
			(String) page.getAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, PageContext.REQUEST_SCOPE), null);
		Long id = (Long) ((JdbcParametro) key.get("COMMNOMIN.ID")).getValue();

		// carico NOMTEC.TECNI tramite la chiave di collegamento CODTEC.COMMNOMIN
		String nomtec = (String) sqlManager.getObject(
			"select nomtec from tecni,commnomin where tecni.codtec = commnomin.codtec and commnomin.id = ?", new Object[] { id });
		page.setAttribute("nomeTecnico", nomtec, PageContext.REQUEST_SCOPE);

		// carico NOMEIN.UFFINT tramite la chiave di collegamento CODTEC.COMMNOMIN
		String nomein = (String) sqlManager.getObject(
			"select nomein from uffint,commnomin where uffint.codein = commnomin.codein and commnomin.id = ?", new Object[] { id });
		nomein = UtilityStringhe.convertiNullInStringaVuota(nomein);
		if (!"".equals(nomein)) {
		    page.setAttribute("nomeStruttura", nomein, PageContext.REQUEST_SCOPE);
		}

	    }

	} catch (SQLException e) {
	    throw new JspException("Errore durante il calcolo del numero d'ordine del criterio", e);
	}

    }

    @Override
    public void doAfterFetch(PageContext page, String modoAperturaScheda) throws JspException {
    }

}