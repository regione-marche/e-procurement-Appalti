package it.eldasoft.sil.pg.tags.funzioni.npa;

import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetDatiFormulario extends AbstractFunzioneTag {
	public GetDatiFormulario() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		String idForm = params[1].toString();
		try {
			String selectScheda = "select cf.nome_scheda, cf.modpres"
					+ " , ns.idschedanparet"
					+ " from g1npaform nf, g1npascheda ns, g1cf_npa cf"
					+ " where nf.id=? and nf.id = ns.idform and cf.id = nf.idcfnpa";
			Vector<?> datiScheda = sqlManager.getVector(selectScheda, new Object[] { idForm });
			String nomeScheda = ((JdbcParametro) datiScheda.get(0)).getStringValue();
			Long modpres = ((JdbcParametro) datiScheda.get(1)).longValue();
			Long idschedanparet = ((JdbcParametro) datiScheda.get(2)).longValue();

			this.getRequest().setAttribute("nome_scheda", nomeScheda);
			this.getRequest().setAttribute("modpres", modpres);
			if (idschedanparet != null) {
				this.getRequest().setAttribute("isRettifica", "true");
				int numeroRettifiche = 0;
				String uuid_eform_originale = null;
				do {
					numeroRettifiche++;
					Vector<?> datiFormularioPrecedente = sqlManager.getVector("select uuid_eform, idschedanparet from g1npascheda where id=?", new Object[] {idschedanparet});
					uuid_eform_originale = ((JdbcParametro) datiFormularioPrecedente.get(0)).getStringValue();
					idschedanparet = ((JdbcParametro) datiFormularioPrecedente.get(1)).longValue();
				} while (idschedanparet != null);
				this.getRequest().setAttribute("numeroRettifiche", numeroRettifiche);
				this.getRequest().setAttribute("uuid_eform_originale", uuid_eform_originale);
			} else {
				this.getRequest().setAttribute("isRettifica", "false");
			}
		} catch (Throwable e) {
			throw new JspException("Errore nella lettura degli indirizzi email degli utenti" ,e);
		}
		return null;
	}
}