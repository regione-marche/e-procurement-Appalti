package it.eldasoft.sil.pg.tags.funzioni.npa;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.List;
import java.util.Vector;

public class CheckSchedaANNUltimaPubblicataFunction extends AbstractFunzioneTag {
	public CheckSchedaANNUltimaPubblicataFunction() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		String codgar = params[1].toString();

		String sql = "select c.nome_scheda, s.statonpa from g1npascheda s,g1npaform f ,g1cf_npa c where c.id=f.idcfnpa and f.id=s.idform and f.codgar=? and s.statonpa > 1 order by s.id desc";
		try {
			List<Vector> results = sqlManager.getListVector(sql, new Object[]{codgar});
			if (results != null && !results.isEmpty()) {
				if ("ANN".equals(SqlManager.getValueFromVectorParam(results.get(0), 0).getValue())
						&& new Long(30).equals(SqlManager.getValueFromVectorParam(results.get(0), 1).getValue())) {
					return "true";
				}
			}
		} catch (Throwable e) {
			throw new JspException("Errore nella lettura delle pubblicazioni su ANAC", e);
		}
		return "false";
	}
}