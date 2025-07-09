package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.List;
import java.util.Vector;

public class GetListaElementiClasseMassimaFunction extends AbstractFunzioneTag {
	public GetListaElementiClasseMassimaFunction() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		String ngara = params[1].toString();

		try {
			List<Vector<JdbcParametro>> listaQuesiti = (List<Vector<JdbcParametro>>)sqlManager.getListVector(
					"select e1.id from elpremgare e1 where e1.ngara=? and e1.idopzione=(select max(e2.idopzione) from elpremgare e2 where e2.ngara=? and e2.idquesito=e1.idquesito)",
					new Object [] {ngara, ngara});
			List<Long> listaQuesitiReturn = new Vector<>();
			if (listaQuesiti != null && listaQuesiti.size() > 0) {
                for (Vector<JdbcParametro> quesito : listaQuesiti) {
                    Long id = (Long) SqlManager.getValueFromVectorParam(quesito, 0).getValue();
					listaQuesitiReturn.add(id);
                }
				this.getRequest().setAttribute("listaElementiClasseMassima", listaQuesitiReturn);
			}
			return null;
		} catch (Throwable e) {
			throw new JspException("Errore nella degli elementi premiali dell'elenco" ,e);
		}
	}
}
