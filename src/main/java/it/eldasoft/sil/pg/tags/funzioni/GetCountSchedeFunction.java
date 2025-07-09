package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.tags.gestori.submit.npa.GestoreG1NPAFORM;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;
import java.util.ArrayList;

public class GetCountSchedeFunction extends AbstractFunzioneTag {
	public GetCountSchedeFunction() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		String idFunzione = null;
		if (params[1] != null)
			idFunzione = params[1].toString();

		ArrayList<Object> listaParametri = new ArrayList<>();

		for (int i = 2; i < params.length; i++) {
			if (params[i] != null) {
				listaParametri.add(params[i]);
			}
		}
		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		try {
			Object[] parametri = listaParametri.toArray();
			Long countSchede = GestoreG1NPAFORM.countSchede(idFunzione, sqlManager, parametri);
			if(countSchede==null) countSchede=0L;
			return countSchede.toString();
		} catch (SQLException e) {
			throw new JspException("Errore durante il count delle schede" ,e);
		}
	}
}
