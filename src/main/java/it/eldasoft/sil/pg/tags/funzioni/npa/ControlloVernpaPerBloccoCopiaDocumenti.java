package it.eldasoft.sil.pg.tags.funzioni.npa;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

public class ControlloVernpaPerBloccoCopiaDocumenti extends AbstractFunzioneTag {

	public ControlloVernpaPerBloccoCopiaDocumenti() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		String codiceGaraDaCopiare = (String) params[1];
		String ret = "false";

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager", pageContext, PgManager.class);

		try {
			String verNPAPartenza = (String) sqlManager.getObject("select vernpa from torn where codgar=?", new Object[] { codiceGaraDaCopiare });
			String verNPACorrente = pgManager.getVersioneNpaCorrente();
			if (StringUtils.isNotEmpty(verNPACorrente) && StringUtils.isEmpty(verNPAPartenza)) {
				ret = "true";
			} else if (StringUtils.isEmpty(verNPACorrente) && StringUtils.isNotEmpty(verNPAPartenza)) {
				ret = "true";
			}
		} catch (Exception ex) {
			throw new JspException("Errore nella lettura dal database: " + ex.getMessage());
		}
		return ret;
	}
}