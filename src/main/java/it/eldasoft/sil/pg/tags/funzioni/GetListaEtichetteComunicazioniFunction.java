package it.eldasoft.sil.pg.tags.funzioni;

import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetListaEtichetteComunicazioniFunction extends AbstractFunzioneTag {
	public GetListaEtichetteComunicazioniFunction() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		try {
			List ret = new Vector();
			List<Vector<?>> etichette = sqlManager.getListVector("select tab1tip,tab1desc from tab1 where tab1cod = 'W_012' and not (tab1arc is not null and tab1arc = '1') order by tab1tip asc", null);
			if (etichette != null && etichette.size() > 0) {
				String valore = null;
				String descrizione = null;
				for (int i = 0; i < etichette.size(); i++) {
					valore = SqlManager.getValueFromVectorParam(etichette.get(i), 0).getStringValue();
					descrizione = SqlManager.getValueFromVectorParam(etichette.get(i), 1).getStringValue();
					ret.add(new Object[] { valore, descrizione });
				}
				this.getRequest().setAttribute("listaEtichette", ret);
			}
		} catch (Throwable e) {
			throw new JspException("Errore nella lettura delle etichette" ,e);
		}
		return null;
	}
}