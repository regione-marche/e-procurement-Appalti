package it.eldasoft.sil.pg.tags.funzioni;

import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetDpubattiFromNgaraFunction extends AbstractFunzioneTag {
	public GetDpubattiFromNgaraFunction() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		String ngara = params[1].toString();
		try {
			Date dpubatti = (Date) sqlManager.getObject("select dpubatti from gare1 where ngara=?", new Object[] {ngara});
			if (dpubatti != null) {
				pageContext.setAttribute("dpubatti", dpubatti, PageContext.REQUEST_SCOPE);
			}
		} catch (Throwable e) {
			throw new JspException("Errore nella lettura della data di pubblicazione" ,e);
		}
		return null;
	}
}
