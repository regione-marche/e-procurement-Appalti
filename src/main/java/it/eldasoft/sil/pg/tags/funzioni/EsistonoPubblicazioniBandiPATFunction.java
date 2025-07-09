package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.GestionePATManager;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;

public class EsistonoPubblicazioniBandiPATFunction extends AbstractFunzioneTag {
	public EsistonoPubblicazioniBandiPATFunction() {
		super(2, new Class[] { PageContext.class });
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {
		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		String codgar = (String) params[1];
		String esistonoPubblicazioni = "false";
		String select = "";
		Long conteggio = null;
		try {
			select = "select count(codgar) from garuuid where codgar=? and tipric=?";
			conteggio = (Long) sqlManager.getObject(select, new Object[] { codgar, GestionePATManager.TIPRIC });
		} catch (SQLException e) {
			throw new JspException("Errore durante il controllo sull'esistenza di pubblicazioni sul sito istituzionale", e);
		}
		if (conteggio != null && conteggio > 0)
			esistonoPubblicazioni = "true";

		return esistonoPubblicazioni;
	}
}