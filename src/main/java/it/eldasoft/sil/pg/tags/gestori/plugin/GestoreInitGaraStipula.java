/*
 * Created on 06/07/2021
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
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore che inizializza una gara creata rapidamente con i dati di aggiudicazione per poter fare una stipula
 *
 * @author Peruzzo Riccardo
 */
public class GestoreInitGaraStipula extends AbstractGestorePreload {

	SqlManager sqlManager = null;

	public GestoreInitGaraStipula(BodyTagSupportGene tag) {
		super(tag);
	}

	@Override
	public void doAfterFetch(PageContext page, String modoAperturaScheda) throws JspException {
	}

	public void inizializzaManager(PageContext page) {
		sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", page, SqlManager.class);

	}

	@Override
	public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda) throws JspException {

		this.inizializzaManager(page);

		HttpSession session = page.getSession();
		String uffint = (String) session.getAttribute("uffint");

		String codein = uffint;
		String cenint =null;
		String nomein =null;
		String iscuc =null;
		
		try {
			Vector<?> datiSA = this.sqlManager.getVector(
					"select codein,nomein,iscuc from uffint" + " where uffint.codein = ? ", new Object[] { codein });
			if (datiSA != null && datiSA.size() > 0) {
				cenint = SqlManager.getValueFromVectorParam(datiSA, 0).getStringValue();
				page.setAttribute("initCENINT", cenint, PageContext.REQUEST_SCOPE);
				nomein = SqlManager.getValueFromVectorParam(datiSA, 1).getStringValue();
				page.setAttribute("initNOMEIN", nomein, PageContext.REQUEST_SCOPE);
				iscuc = SqlManager.getValueFromVectorParam(datiSA, 2).getStringValue();
                page.setAttribute("initISCUC", iscuc, PageContext.REQUEST_SCOPE);
			}
		} catch (SQLException e) {
			throw new JspException("Errore durante la lettura dei dati della uffint", e);
		}

	}
}