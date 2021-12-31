/*
 * Created on 29/lug/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione per inizializzare le sezioni delle pubblicazioni e termini di
 * iscrizione per le gare per elenco opertaori economici
 * 
 * @author Luca.Giacomazzo
 */
public class GestionePubblicazioniTerminiIscrizioneFunction extends
		AbstractFunzioneTag {

	public GestionePubblicazioniTerminiIscrizioneFunction() {
		super(1, new Class[] { String.class });
	}

	public String function(PageContext pageContext, Object[] params)
			throws JspException {
    String nGara = (String) params[0];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      List listaPubblicazioniTerminiIscrizione = sqlManager.getListVector(
          "select CODGAR, NGARA, NUMPT, NPUBAVVBAN, DPUBAVVBAN,OPUBAVVBAN, DTERMPRES, OTERMPRES, DSORTEGGIO, OSORTEGGIO, NOTEPT "
              + "from PUBBTERM "
              + "where CODGAR = ? and NGARA = ? "
              + "order by NUMPT asc", new Object[] {"$"+nGara, nGara });

      if (listaPubblicazioniTerminiIscrizione != null && listaPubblicazioniTerminiIscrizione.size() > 0)
        pageContext.setAttribute("pubblicazioniTerminiIscr", listaPubblicazioniTerminiIscrizione,
            PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre le pubblicazioni e termini di iscrizione "
          + "della gara " + nGara, e);
    }
    return null;
	}

}