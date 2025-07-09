package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetListaParametriPredefinitiComposizioneFunction extends AbstractFunzioneTag {
	public GetListaParametriPredefinitiComposizioneFunction() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

		String valoriCache = "";
		try {
			ProfiloUtente profilo = (ProfiloUtente) pageContext.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
			Long syscon = new Long(profilo.getId());

			String nomeFile = ConfigManager.getValore("verbaleGara.nomeModello");
			Long idModello = (Long) sqlManager.getObject("select id_modello from W_MODELLI where nomefile=?",
					new Object[] { nomeFile });

			String selectCache = "select w_modparam.codice, valore from w_modparam"
					+ " left join w_cachemodpar on w_modparam.codice=w_cachemodpar.codice and w_modparam.id_modello=w_cachemodpar.id_modello and id_account=?"
					+ " where w_modparam.id_modello=? order by progr asc";
			List<Vector<?>> datiCacheModello = sqlManager.getListVector(selectCache, new Object[] { syscon, idModello });

			for (int i = 1; i < datiCacheModello.size(); i++) {
				String valore = SqlManager.getValueFromVectorParam(datiCacheModello.get(i), 1).stringValue();
				if (StringUtils.isBlank(valore)) {
					valore = "2";
				}
				valoriCache += valore + ",";
			}
			if (StringUtils.isBlank(valoriCache)) {
				return "2,2,2,2,2,2,2,2,2,2,2,2,2,2";
			}
			valoriCache = valoriCache.substring(0, valoriCache.length() - 1);

//			TODO implementazione scelte predefinite da tabellato
//			String parametriTabellato = "0,0,0,0,0,0,0,0,0,0,0,0,0";
//
//			String selectTabellato = "select w_modparam.codice, valore from w_modparam"
//					+ " left join w_cachemodpar on w_modparam.codice=w_cachemodpar.codice and w_modparam.id_modello=w_cachemodpar.id_modello and id_account=?"
//					+ " where w_modparam.id_modello=? order by progr asc";
//			List<Vector<?>> datiTabellatoModello = sqlManager.getListVector(selectTabellato, new Object[] { syscon, idModello });
//
//			this.getRequest().setAttribute("parametriTabellato", parametriTabellato);

		} catch (GestoreException | SQLException e) {
			throw new JspException("Errore nella lettura dei parametri predefiniti da w_cachemodpar " + e.getMessage(), e);
		}
		return valoriCache;
	}
}