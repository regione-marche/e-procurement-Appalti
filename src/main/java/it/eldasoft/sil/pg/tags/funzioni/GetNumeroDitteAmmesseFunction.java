package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.Vector;

public class GetNumeroDitteAmmesseFunction extends AbstractFunzioneTag {
	public GetNumeroDitteAmmesseFunction() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {
		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		String ngara = (String) params[1];
		String bustaUnica = (String) params[2];
		String numeroDitteAmmesse = null;
		try {

			Vector<?> datiLotto = sqlManager.getVector("select modlicg, valtec from gare,gare1 where gare.ngara = gare1.ngara and gare.ngara=?", new Object[] {ngara});
			Long modlicg = ((JdbcParametro) datiLotto.get(0)).longValue();
			String valtec = ((JdbcParametro) datiLotto.get(1)).stringValue();
			Object[] parametri;
			if ("true".equals(bustaUnica)) {
				parametri = new Object[] {ngara, 6L};
			} else if ((modlicg != null && modlicg == 6) || "1".equals(valtec)) {
				parametri = new Object[] {ngara, 4L};
			} else {
				parametri = new Object[] {ngara, 5L};
			}
			numeroDitteAmmesse = ((Long) sqlManager.getObject("select count(*) from ditg where ngara5=? and (fasgar>? or fasgar is null)", parametri)).toString();
		} catch (Exception e) {
			throw new JspException("Errore durante il conteggio delle ditte ammesse alla gara: " + e.getMessage());
		}
		return numeroDitteAmmesse;
	}
}
