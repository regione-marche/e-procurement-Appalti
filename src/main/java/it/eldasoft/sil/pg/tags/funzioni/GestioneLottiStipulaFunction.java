/*
 * Created on 19/mag/21
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
 * Funzione per inizializzare le sezioni delle gare di stipula in fase di
 * visualizzazione
 *
 * @author Riccardo.Peruzzo
 */
public class GestioneLottiStipulaFunction extends AbstractFunzioneTag {

	public GestioneLottiStipulaFunction() {
		super(1, new Class[] { String.class });
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {
		String whereLottiAggiudicati = (String) params[0];

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		try {
			List listaLotti = sqlManager
					.getListVector("select GARE.NGARA, GARE.CODIGA, TORN.NUMAVCP, GARE.CODCIG, GARE.DATTOA, GARE.IAGGIU, GARE1.IMPRIN, GARE1.IMPALTRO , GARE.NOT_GAR"
							+ " from GARE"
							+ "	join GARECONT on ((GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1) OR (GARECONT.NGARA=GARE.CODGAR1 AND (GARECONT.NGARAL IS NULL OR GARECONT.NGARAL=GARE.NGARA)))"
							+ " join TORN on TORN.CODGAR=GARE.CODGAR1"
							+ " join GARE1 on GARE1.NGARA=GARE.NGARA WHERE " + whereLottiAggiudicati, new Object[] {});

			if (listaLotti != null && listaLotti.size() > 0)
			  pageContext.setAttribute("gare", listaLotti, PageContext.REQUEST_SCOPE);

		} catch (SQLException e) {
			throw new JspException("Errore nell'estrarre le gare", e);
		}
		return null;
	}

}