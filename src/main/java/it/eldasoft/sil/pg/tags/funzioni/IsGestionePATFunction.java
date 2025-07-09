package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestionePATManager;
import it.eldasoft.utils.spring.UtilitySpring;
import org.apache.commons.lang.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;

public class IsGestionePATFunction extends AbstractFunzioneTag {
	public IsGestionePATFunction() { super(-1, null);}

@Override
public String function(PageContext pageContext, Object[] params) throws JspException {
		String codgar = null;
		if (params.length > 1) {
            codgar = (String) params[1];
        }
		try {
			boolean configuratoPerPAT;
			if (StringUtils.isNotEmpty(codgar)) {
				SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
				String cenint = (String) sqlManager.getObject("select cenint from torn where codgar=?", new Object[]{codgar});
				configuratoPerPAT = GestionePATManager.isConfiguratoPerPAT(cenint);
			} else {
				GestionePATManager gestionePATManager = (GestionePATManager) UtilitySpring.getBean("gestionePATManager", pageContext, GestionePATManager.class);
				configuratoPerPAT = gestionePATManager.isConfiguratoPerPAT();
			}
			if (configuratoPerPAT) this.getRequest().setAttribute("isGestionePATISWEB", "true");
		} catch (SQLException | GestoreException e) {
			throw new JspException("Errore durante il controllo della configurazione del portale PAT");
		}
		return null;
	}
}