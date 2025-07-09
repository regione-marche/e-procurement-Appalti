package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestionePATManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetListaStrutturePATFunction extends AbstractFunzioneTag {
	public GetListaStrutturePATFunction() {
		super(1, new Class[] { PageContext.class });
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

//		GestionePATManager gestionePATManager = (GestionePATManager) UtilitySpring.getBean("gestionePATManager",
//				pageContext, GestionePATManager.class);
//		try {
//			List ret = new Vector();
//			List<Map<String, String>> strutture = gestionePATManager.getStrutture();
//			if (strutture != null && strutture.size() > 0) {
//				String id = null;
//				String nome = null;
//				for (int i = 0; i < strutture.size(); i++) {
//					id = strutture.get(i).get("id");
//					nome = strutture.get(i).get("nome_ufficio");
//					ret.add(new Object[] { id, nome });
//				}
//				this.getRequest().setAttribute("listaStrutturePAT", ret);
//			}
//		} catch (GestoreException e) {
//			throw new JspException("Errore nella lettura delle strutture" ,
//					e);
//		}

		return null;
	}
}
