package it.eldasoft.sil.pg.tags.funzioni.npa;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.Arrays;

public class CheckSchedaCM1InCorsoFunction extends AbstractFunzioneTag {

	public CheckSchedaCM1InCorsoFunction() {
		super(2, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		String codgar = (String) params[1];

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

		try {
		    Long stato = (Long) sqlManager.getObject("select g1npascheda.statonpa from g1npascheda, g1npaform, g1cf_npa where " +
				"g1cf_npa.id=g1npaform.idcfnpa and g1npaform.id=g1npascheda.idform and g1cf_npa.nome_scheda='CM1' and g1npaform.codgar=?" +
				" order by g1npascheda.id desc", new Object[]{codgar});

			return (!(stato==null || Arrays.asList(1L).contains(stato))) +"";

		} catch (Exception ex) {
			throw new JspException("Errore nella verifica dello stato dell'eventuale scheda CM1" + ex.getMessage());
		}
    }
}