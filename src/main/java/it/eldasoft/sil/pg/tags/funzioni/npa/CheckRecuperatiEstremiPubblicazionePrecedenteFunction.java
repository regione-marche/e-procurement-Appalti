package it.eldasoft.sil.pg.tags.funzioni.npa;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.Arrays;

/**
 * @author manuel.bridda
 *
 */
public class CheckRecuperatiEstremiPubblicazionePrecedenteFunction extends AbstractFunzioneTag {

	public CheckRecuperatiEstremiPubblicazionePrecedenteFunction() {
		super(2, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		String idform = (String) params[1];

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

		try {
		    Long count = (Long) sqlManager.getObject("select count(*) from g1npascheda g,g1npaform f ,g1cf_npa cf where g.idform=f.id and f.idcfnpa=cf.id and g.idform in (select gg.idschedanparet from g1npascheda gg where gg.idform=?)"
			+" and (((g.ispubted is null or g.ispubted<>'1') and cf.eform is not null) or ((g.ispubpvl is null or g.ispubpvl<>'1') and cf.ispubbnaz is not null))", new Object[]{idform});

			return count!=null && count>0? "false" : "true";

		} catch (Exception ex) {
			throw new JspException("Errore nel recupero estremi pubblicazione della scheda precedente. Scheda idform=" + idform+"\n"+ex);
		}
    }
}