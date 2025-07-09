package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.GestionePATManager;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;

public class GetUuidFunction extends AbstractFunzioneTag {

	public GetUuidFunction() {
		super(2, new Class[] { PageContext.class, String.class });
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {
		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		String codgar = (String) GeneralTagsFunction.cast("string", params[1]);
		String select = "select uuid from garuuid where codgar=? and tipric=?";
		try {
			String tipric = GestionePATManager.TIPRIC;
			return (String) sqlManager.getObject(select, new Object[] { codgar, tipric });
		} catch (SQLException e) {
			throw new JspException("Errore durante la lettura di GARUUID.UUID per la gara " + codgar, e);
		}
	}
}
