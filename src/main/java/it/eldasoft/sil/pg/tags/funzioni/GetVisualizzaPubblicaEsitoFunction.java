package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.Vector;

public class GetVisualizzaPubblicaEsitoFunction extends AbstractFunzioneTag {
	public GetVisualizzaPubblicaEsitoFunction() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {
		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		Long idscheda = Long.parseLong((String)params[1]);
		String visualizzaPubblicaEsitoSuPortale = "false";
		try {
			Vector<?> datiScheda = sqlManager.getVector("select g1npaform.codgar, g1cf_npa.nome_scheda from g1npascheda,g1npaform,g1cf_npa where g1npascheda.id=? and g1npascheda.idform=g1npaform.id and g1npaform.idcfnpa=g1cf_npa.id", new Object[] {idscheda});
			String codgar = ((JdbcParametro) datiScheda.get(0)).stringValue();
			String nome_scheda = ((JdbcParametro) datiScheda.get(1)).stringValue();

			Long countPubblicazioniEsiti = (Long) sqlManager.getObject("select count(*) from pubg where tippubg=12 and ngara in (select ngara from gare where codgar1=?)", new Object[] {codgar});
			Long countPubblicazioniBandi = (Long) sqlManager.getObject("select count(*) from pubbli where tippub in (11,13) and codgar9=?", new Object[] {codgar});

			String eseguitoPubblicazioniInerenti = "false";
			boolean isPubblicatiEsiti = countPubblicazioniEsiti != null && countPubblicazioniEsiti > 0;
			boolean isSchedaDiAggiudicazione = StringUtils.isNotEmpty(nome_scheda) && nome_scheda.startsWith("A") && !("ANN".equals(nome_scheda));
			if (isSchedaDiAggiudicazione && isPubblicatiEsiti) {
				eseguitoPubblicazioniInerenti = "true";
			} else if (StringUtils.isNotEmpty(nome_scheda) && !(nome_scheda.startsWith("A")) && countPubblicazioniBandi != null && countPubblicazioniBandi > 0) {
				eseguitoPubblicazioniInerenti = "true";
			}
			pageContext.setAttribute("eseguitoPubblicazioniInerenti", eseguitoPubblicazioniInerenti);

			if (!isPubblicatiEsiti && isSchedaDiAggiudicazione) {
				visualizzaPubblicaEsitoSuPortale = "true";
			}
		} catch (Exception e) {
			throw new JspException("Errore durante il controllo di visualizzazione funzione pubblica esito su portale " + e.getMessage());
		}
		return visualizzaPubblicaEsitoSuPortale;
	}
}
