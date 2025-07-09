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

  public final static String JOIN_CONTRATTO = "join GARECONT on ((GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1) OR (GARECONT.NGARA=GARE.CODGAR1 AND (GARECONT.NGARAL IS NULL OR GARECONT.NGARAL=GARE.NGARA) and CODIMP = GARE.DITTA))"
      + " join TORN on TORN.CODGAR=GARE.CODGAR1"
      + " join GARE1 on GARE1.NGARA=GARE.NGARA WHERE ";

	public GestioneLottiStipulaFunction() {
		super(4, new Class[] { PageContext.class, String.class, String.class, Long.class });
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {
		String whereLottiAggiudicati = (String) params[1];
		String id = (String) params[2];
		Long stato = (Long) params[3];

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		String select="select GARE.NGARA, GARE.CODIGA, TORN.NUMAVCP, GARE.CODCIG, GARE.DATTOA, GARE.IAGGIU, GARE1.IMPRIN, GARE1.IMPALTRO , GARE.NOT_GAR, TORN.IDAPPALTONPA from GARE ";
		Object par[] = null;
		if(new Long(6).equals(stato)) {
		  select ="select GARE.NGARA, GARE.CODIGA, TORN.NUMAVCP, GARE.CODCIG, GARE.DATTOA, G1AGGISTO.IAGGIU, GARE1.IMPRIN, GARE1.IMPALTRO , GARE.NOT_GAR, TORN.IDAPPALTONPA from GARE, G1STIPULA, G1AGGISTO,GARE1, TORN WHERE G1STIPULA.ID=? AND G1AGGISTO.IDSTIPULA = G1STIPULA.ID AND  G1AGGISTO.NGARA = GARE.NGARA AND GARE1.NGARA=GARE.NGARA AND TORN.CODGAR=GARE.CODGAR1";
		  par=new Object[] {new Long(id)};
		}else {
		  select="select GARE.NGARA, GARE.CODIGA, TORN.NUMAVCP, GARE.CODCIG, GARE.DATTOA, GARE.IAGGIU, GARE1.IMPRIN, GARE1.IMPALTRO , GARE.NOT_GAR, TORN.IDAPPALTONPA from GARE "+  JOIN_CONTRATTO + whereLottiAggiudicati;
		}
		try {
			List listaLotti = sqlManager.getListVector(select, par);

			if (listaLotti != null && listaLotti.size() > 0)
			  pageContext.setAttribute("gare", listaLotti, PageContext.REQUEST_SCOPE);

		} catch (SQLException e) {
			throw new JspException("Errore nell'estrarre le gare", e);
		}
		return null;
	}

}