package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;

public class GetSeAttivoModuloANACFormFunction extends AbstractFunzioneTag {
	public GetSeAttivoModuloANACFormFunction() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager", pageContext, PgManager.class);
		String codgar = "";
		if (params[1] != null)
			codgar = params[1].toString();
		try {
			return pgManager.isModuloAnacFormAttivo(pageContext.getSession(), codgar) ? "true" : "false";
		} catch (SQLException e) {
			throw new JspException("Errore durante la verifica del modulo AnacForm" ,e);
		}
	}
}
