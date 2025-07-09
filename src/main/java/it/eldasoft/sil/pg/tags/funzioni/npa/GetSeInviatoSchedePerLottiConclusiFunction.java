package it.eldasoft.sil.pg.tags.funzioni.npa;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.tags.gestori.submit.npa.GestoreG1NPAFORM;
import it.eldasoft.utils.spring.UtilitySpring;
import org.apache.commons.lang.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.List;
import java.util.Vector;

public class GetSeInviatoSchedePerLottiConclusiFunction extends AbstractFunzioneTag {
	public GetSeInviatoSchedePerLottiConclusiFunction() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1", pageContext, PgManagerEst1.class);
		String ngara = params[1].toString();
		String codgar = params[2].toString();
		try {
			if (StringUtils.isEmpty(codgar)) {
				codgar = (String) sqlManager.getObject("select codgar1 from gare where ngara=?", new Object[] {ngara});
			}
			if (pgManagerEst1.isAffidamentoDiretto(codgar)) {
				Long countSchedeAggiudicazioneInviate = GestoreG1NPAFORM.countSchede(GestoreG1NPAFORM.PUBBLICATE_AGGIUDICAZIONI, sqlManager, new Object[]{codgar});
				if (countSchedeAggiudicazioneInviate != null && countSchedeAggiudicazioneInviate > 0) {
					return "true";
				} else {
					return "false";
				}
			}

			String inviateP5 = GetSeInviateSchedeAggiudicazioneFunction.isPubblicataP5(codgar, sqlManager);
			if ("true".equals(inviateP5)) {
				return inviateP5;
			}

			List<Vector<JdbcParametro>> listaLottiConclusi = (List<Vector<JdbcParametro>>)sqlManager.getListVector("select ngara from gare where (gare.ditta is not null or gare.esineg is not null) and codgar1=?", new Object [] {codgar});
			return GetSeInviateSchedeAggiudicazioneFunction.isLottiPubblicatiSuANAC(listaLottiConclusi, sqlManager);
		} catch (Throwable e) {
			throw new JspException("Errore nella lettura delle pubblicazioni su ANAC" ,e);
		}
	}
}
