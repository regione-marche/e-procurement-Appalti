package it.eldasoft.sil.pg.tags.funzioni.npa;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.ValidazioneManipolazioneEFormManager;
import it.eldasoft.sil.pg.bl.npa.ScambioDatiPCPManager;
import it.eldasoft.utils.spring.UtilitySpring;
import org.apache.axis.utils.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class GetEsitoValidazioneEFormFunction extends AbstractFunzioneTag {
	public GetEsitoValidazioneEFormFunction() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		ValidazioneManipolazioneEFormManager validazioneManipolazioneEFormManager = (ValidazioneManipolazioneEFormManager) UtilitySpring.getBean("validazioneManipolazioneEFormManager", pageContext, ValidazioneManipolazioneEFormManager.class);
		ScambioDatiPCPManager scambioDatiPCPManager = (ScambioDatiPCPManager) UtilitySpring.getBean("scambioDatiPCPManager", pageContext, ScambioDatiPCPManager.class);
		Long idscheda = Long.parseLong((String)params[1]);
		String erroriValidazione = "";
		try {
			Vector<?> datiScheda = sqlManager.getVector("select idschedanparet,idprg_eform,iddoc_eform,idform from g1npascheda where id=?", new Object[] {idscheda});
			Long idSchedaNpaRet = ((JdbcParametro) datiScheda.get(0)).longValue();
			String idprg_eform = ((JdbcParametro) datiScheda.get(1)).stringValue();
			Long iddoc_eform = ((JdbcParametro) datiScheda.get(2)).longValue();
			Long idform = ((JdbcParametro) datiScheda.get(3)).longValue();

			if (StringUtils.isEmpty(idprg_eform) || iddoc_eform == null) {
				return null;
			}
			Map<String,Object> parameters = new HashMap<>();
			parameters.put(ScambioDatiPCPManager.ID_SCHEDA_NPA_RET,idSchedaNpaRet);
			parameters.put(ScambioDatiPCPManager.ID_FORM,idform);

			Object[] avvisiErroriValidazioneEForm = validazioneManipolazioneEFormManager.controllaAggiornaEform(idprg_eform, iddoc_eform, parameters);
			pageContext.setAttribute("avvisiValidazioneEForm", (String) avvisiErroriValidazioneEForm[3]);
			scambioDatiPCPManager.lanciaErroriValidazioneEForm(avvisiErroriValidazioneEForm);
		} catch (Exception e) {
			erroriValidazione = e.getMessage();
		}
		return erroriValidazione;
	}
}
