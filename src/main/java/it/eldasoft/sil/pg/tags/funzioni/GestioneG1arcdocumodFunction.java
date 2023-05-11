/*
 * Created on 17/mag/21
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

/**
 * Funzione per inizializzare le sezioni dei documenti di contratto in fase di
 * modifica
 *
 * @author Riccardo.Peruzzo
 */
public class GestioneG1arcdocumodFunction extends AbstractFunzioneTag {

	public GestioneG1arcdocumodFunction() {
		super(1, new Class[] { String.class });
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {
		String ID = (String) params[0];

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		try {
			List listaDoc = sqlManager
					.getListVector("select G.NUMORD, G.FASE, G.DESCRIZIONE, G.ULTDESC, G.VISIBILITA, G.OBBLIGATORIO, G.IDDOCUMOD, G.ID, G.REQCAP, G.TIPODOC, "
							+ "G.CONTESTOVAL, G.MODFIRMA, G.IDSTAMPA, G.ALLMAIL, G.IDPRG,G.IDDOCDG, W.DIGNOMDOC"
      						+ " from G1ARCDOCUMOD G left join W_DOCDIG W"
      						+ " on G.IDPRG = W.IDPRG  and G.IDDOCDG = W.IDDOCDIG"
      						+ " where G.IDDOCUMOD = ? order by G.NUMORD", new Object[] { ID });
			if (listaDoc != null && listaDoc.size() > 0)
				pageContext.setAttribute("documenti", listaDoc, PageContext.REQUEST_SCOPE);
		} catch (SQLException e) {
			throw new JspException("Errore nell'estrarre i documenti con " + "id " + ID, e);
		}
		return null;
	}

}