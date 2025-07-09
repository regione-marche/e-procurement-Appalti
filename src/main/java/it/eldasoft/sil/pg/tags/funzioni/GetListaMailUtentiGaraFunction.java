package it.eldasoft.sil.pg.tags.funzioni;

import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetListaMailUtentiGaraFunction extends AbstractFunzioneTag {
	public GetListaMailUtentiGaraFunction() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		String codgar = params[1].toString();
		String isStipula = params[2].toString();
		List ret = new Vector();
		try {
			String select = "select sysute,email from g_permessi left join usrsys on g_permessi.syscon=usrsys.syscon where ";
			List<Vector<?>> utenti = null;
			if ("true".equals(isStipula)) {
				select += "idStipula=?";
				Long idStipula = Long.parseLong(codgar);
				utenti = sqlManager.getListVector(select, new Object[] {idStipula});
			}
			else {
				select += "codgar=?";
				utenti = sqlManager.getListVector(select, new Object[] {codgar});
			}
			if (utenti != null && utenti.size() > 0) {
				String nomeUtente = null;
				String emailUtente = null;
				for (int i = 0; i < utenti.size(); i++) {
					nomeUtente = SqlManager.getValueFromVectorParam(utenti.get(i), 0).getStringValue();
					emailUtente = SqlManager.getValueFromVectorParam(utenti.get(i), 1).getStringValue();
					ret.add(new Object[] { nomeUtente, emailUtente });
				}
				this.getRequest().setAttribute("listaMailUtentiGara", ret);
			}
		} catch (Throwable e) {
			throw new JspException("Errore nella lettura degli indirizzi email degli utenti" ,e);
		}
		return null;
	}
}