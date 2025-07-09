package it.eldasoft.sil.pg.tags.gestori.plugin.npa;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;

public class GestoreInizializzazioniG1NPAFORM extends AbstractGestorePreload {

	private SqlManager sqlManager;

	public GestoreInizializzazioniG1NPAFORM(BodyTagSupportGene tag) {
		super(tag);
	}

	@Override
	public void doAfterFetch(PageContext page, String modoAperturaScheda) throws JspException {

	}

	@Override
	public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda) throws JspException {

		this.sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", page, SqlManager.class);

		HashMap key = UtilityTags.stringParamsToHashMap(
				(String) page.getAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, PageContext.REQUEST_SCOPE), null);

		try {
			String formularioScelto = page.getRequest().getParameter("formularioScelto");

			if (StringUtils.isNotBlank(formularioScelto)) {
				Long idFormulario = Long.parseLong(formularioScelto);
				Vector<?> datiG1CFNPA = sqlManager.getVector(
						"select nome_scheda, oggetto, descrizione, islotti, eform, isespd from g1cf_npa where id=?",
						new Object[] { idFormulario });

				int contatoreCampi = 0;

				String nomeScheda = ((JdbcParametro) datiG1CFNPA.get(contatoreCampi++)).getStringValue();
				String oggetto = ((JdbcParametro) datiG1CFNPA.get(contatoreCampi++)).getStringValue();
				String descrizione = ((JdbcParametro) datiG1CFNPA.get(contatoreCampi++)).getStringValue();
				String isLotti = ((JdbcParametro) datiG1CFNPA.get(contatoreCampi++)).getStringValue();
				String eform = ((JdbcParametro) datiG1CFNPA.get(contatoreCampi++)).getStringValue();
				String isEspd = ((JdbcParametro) datiG1CFNPA.get(contatoreCampi++)).getStringValue();

				page.setAttribute("initNOME_SCHEDA", nomeScheda, PageContext.REQUEST_SCOPE);
				page.setAttribute("initOGGETTO", oggetto, PageContext.REQUEST_SCOPE);
				page.setAttribute("initDESCRIZIONE", descrizione, PageContext.REQUEST_SCOPE);
				page.setAttribute("initISLOTTI", isLotti, PageContext.REQUEST_SCOPE);
				page.setAttribute("numeroEFORM", eform, PageContext.REQUEST_SCOPE);
				page.setAttribute("isESPD", isEspd, PageContext.REQUEST_SCOPE);
			}

			if (key.get("G1NPAFORM.ID") != null) {
				Long idForm = (Long) ((JdbcParametro) key.get("G1NPAFORM.ID")).getValue();
				
				List<?> datiScheda = sqlManager.getVector("select idprg_eform,iddoc_eform,idprg_espd,iddoc_espd,statonpa, modpres,islotti,eform,isespd, idschedanparet, inviabile"
						+ " from g1npascheda,g1npaform,g1cf_npa"
						+ " where idForm=? and g1npascheda.idform = g1npaform.id and g1npaform.idcfnpa = g1cf_npa.id", new Object[] {idForm});

				int contatoreCampi = 0;
				String idprg_eform = ((JdbcParametro) datiScheda.get(contatoreCampi++)).getStringValue();
				Long iddoc_eform = ((JdbcParametro) datiScheda.get(contatoreCampi++)).longValue();
				String idprg_espd = ((JdbcParametro) datiScheda.get(contatoreCampi++)).getStringValue();
				Long iddoc_espd = ((JdbcParametro) datiScheda.get(contatoreCampi++)).longValue();
				Long statonpa = ((JdbcParametro) datiScheda.get(contatoreCampi++)).longValue();

				Long modpres = ((JdbcParametro) datiScheda.get(contatoreCampi++)).longValue();
				String isLotti = ((JdbcParametro) datiScheda.get(contatoreCampi++)).getStringValue();
				String eform = ((JdbcParametro) datiScheda.get(contatoreCampi++)).getStringValue();
				String isEspd = ((JdbcParametro) datiScheda.get(contatoreCampi++)).getStringValue();
				Long idschedarettificata = ((JdbcParametro) datiScheda.get(contatoreCampi++)).longValue();
				String inviabile = ((JdbcParametro) datiScheda.get(contatoreCampi++)).stringValue();

				String nomeDocumento = "";
				if(StringUtils.isNotBlank(eform) && iddoc_eform != null && StringUtils.isNotBlank(idprg_eform)) {
					getDatiDocumento(page, idprg_eform, iddoc_eform, "EFORM");
				}

				if ("1".equals(isEspd) && iddoc_espd != null && StringUtils.isNotBlank(idprg_espd)) {
					getDatiDocumento(page, idprg_espd, iddoc_espd, "ESPD");
				}
                
                if(idschedarettificata != null) //non si può/non ha senso rettificare l'espd: lo lasciamo in visualizzazione
                  isEspd="2";

				page.setAttribute("numeroEFORM", eform, PageContext.REQUEST_SCOPE);
				page.setAttribute("isESPD", isEspd, PageContext.REQUEST_SCOPE);
				page.setAttribute("statonpa", statonpa, PageContext.REQUEST_SCOPE);
				page.setAttribute("inviabileMRP",inviabile);
			}
		} catch (Exception e) {
			throw new JspException("Errore in fase di esecuzione delle select di inizializzazione", e);
		}
	}

	private void getDatiDocumento(PageContext page, String idprg, Long iddoc, String sezioneDocumenti) throws SQLException {
		Vector<?> datiEstrattiDocumento = sqlManager.getVector("select w_docdig.dignomdoc,w_docdig.digdesdoc,documgara.statodoc,documgara.isarchi " +
						"from w_docdig left join documgara on w_docdig.idprg=documgara.idprg and iddocdig=iddocdg where w_docdig.idprg=? and w_docdig.iddocdig=?"
				, new Object[] {idprg,iddoc});
		String dignomdoc = ((JdbcParametro) datiEstrattiDocumento.get(0)).getStringValue();
		String digdesdoc = ((JdbcParametro) datiEstrattiDocumento.get(1)).getStringValue();
		Long statodoc = (Long) ((JdbcParametro) datiEstrattiDocumento.get(2)).getValue();
		String isarchi = (String)((JdbcParametro) datiEstrattiDocumento.get(3)).getValue();
		String eliminabile = !new Long(5).equals(statodoc) || "1".equals(isarchi)?"true":"false";
		if (StringUtils.isNotBlank(dignomdoc)) {
			List datiDocumento = new Vector();
			datiDocumento.add(idprg);
			datiDocumento.add(iddoc);
			datiDocumento.add(dignomdoc);
			datiDocumento.add(digdesdoc);
			datiDocumento.add(eliminabile);
			page.setAttribute("documento" + sezioneDocumenti, datiDocumento, PageContext.REQUEST_SCOPE);
		}
	}
}
