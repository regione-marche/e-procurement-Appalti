package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.Date;

public class GetDelpremcontFromNgaraFunction extends AbstractFunzioneTag {
	public GetDelpremcontFromNgaraFunction() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		String ngara = params[1].toString();
		String ret = null;
		try {
			Date delpremcont = (Date) sqlManager.getObject("select delpremcont from gare1 where ngara=?", new Object[] {ngara});
			if (delpremcont != null) {
				ret = delpremcont.toString();
			}

			Long countDitteInserite = (Long) sqlManager.getObject("select count(*) from ditg where ngara5=? and acquisizione=3", new Object[] {ngara});
			if (countDitteInserite > 0) {
				pageContext.setAttribute("ditteInseriteInElenco", "true");
			}

			Long countDitteSenzaPunteggio = (Long) sqlManager.getObject("select count(*) from ditg" +
					" where ngara5=(select elencoe from gare where ngara=?) and ditg.abilitaz = '1' and ditg.dabilitaz is not null and ditg.numordpl is not null" +
					" and dittao not in (select codimp from elprempun where ngarag=? and puntot is not null)", new Object[] {ngara,ngara});

			if (countDitteSenzaPunteggio > 0) {
				pageContext.setAttribute("ditteSenzaPunteggio", "true");
			}

		} catch (Throwable e) {
			throw new JspException("Errore nella lettura della data di pubblicazione" ,e);
		}
		return ret;
	}
}
