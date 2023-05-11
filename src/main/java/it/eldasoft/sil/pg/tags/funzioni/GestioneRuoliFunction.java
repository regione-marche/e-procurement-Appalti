/*
 * Created on 09/lug/15
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
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

/**
 * Funzione per l'estrazione dei Ruoli Nominativi componenti commissione
 * 
 * @author roberto.marcon
 *
 */
public class GestioneRuoliFunction extends AbstractFunzioneTag {

    public GestioneRuoliFunction() {
	super(1, new Class[] { String.class });
    }

    @Override
    public String function(PageContext pageContext, Object[] params) throws JspException {
	String idNomin = StringUtils.stripToNull((String) params[0]);

	SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

	String select = "Select id, idalbo, idnomin, ruolo, inviti from COMMRUOLI where IDNOMIN = ? order by id";
	try {
	    @SuppressWarnings("rawtypes")
	    List listaDati = sqlManager.getListVector(select, new Object[] { idNomin });

	    if (listaDati != null && listaDati.size() > 0) {
		pageContext.setAttribute("datiCOMMRUOLI", listaDati, PageContext.REQUEST_SCOPE);
	    }

	} catch (SQLException e) {
	    throw new JspException("Errore nell'estrarre le informazioni per il ruolo " + idNomin, e);
	}

	return null;
    }
}
