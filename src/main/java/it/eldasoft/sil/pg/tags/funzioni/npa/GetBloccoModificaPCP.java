package it.eldasoft.sil.pg.tags.funzioni.npa;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.tags.gestori.submit.npa.GestoreG1NPAFORM;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetBloccoModificaPCP extends AbstractFunzioneTag {
	public GetBloccoModificaPCP() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		String codgar = params[1].toString();
		String escludiPL = params[2].toString();

		try {
			String functionID = GestoreG1NPAFORM.INVIATE;
			if ("true".equals(escludiPL)) {
				functionID = GestoreG1NPAFORM.INVIATE_NO_PL;
			}
			Long countPubblicazioniANAC = GestoreG1NPAFORM.countSchede(functionID, sqlManager, new Object[] {codgar});
			if (countPubblicazioniANAC != null && countPubblicazioniANAC > 0) {
				Long countCM1 = GestoreG1NPAFORM.countSchede(GestoreG1NPAFORM.PRESENTE_CM_1_IN_COMPILAZIONE, sqlManager, new Object[] {codgar});
				if (countCM1 > 0) {
					Long countAD = GestoreG1NPAFORM.countSchede(GestoreG1NPAFORM.INVIATE_AD, sqlManager, new Object[] {codgar});
					if (countAD == 0)
						pageContext.getRequest().setAttribute("impoOpzRinnModificabili","true");
				}
				return "true";
			} else {
				return "false";
			}
		} catch (Throwable e) {
			throw new JspException("Errore nella lettura delle pubblicazioni su ANAC" ,e);
		}
	}
}