package it.eldasoft.sil.pg.tags.funzioni.npa;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.tags.gestori.submit.npa.GestoreG1NPAFORM;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class GetSeInviateSchedeAggiudicazioneFunction extends AbstractFunzioneTag {
	public GetSeInviateSchedeAggiudicazioneFunction() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		String ngara = params[1].toString();

		try {
			Vector datiGara = sqlManager.getVector("select V_GARE_GENERE.genere, V_GARE_GENERE.codgar,torn.modcont from V_GARE_GENERE,torn where V_GARE_GENERE.codgar=(select codgar1 from gare where ngara=?) and genere <100 and torn.codgar=v_gare_genere.codgar", new Object[]{ngara});
			Long genereGara = (Long) ((JdbcParametro) datiGara.get(0)).getValue();
			String codgar = (String) ((JdbcParametro) datiGara.get(1)).getValue();
			Long modcont = (Long) ((JdbcParametro) datiGara.get(2)).getValue();

			Long countSchedeAggiudicazioneInviate = null;
			if (((Long) 2L).equals(modcont)) {
				String dittao = params[2].toString();
				List<Vector<JdbcParametro>> listaLottiAggiudicati = (List<Vector<JdbcParametro>>)sqlManager.getListVector("select ngara from gare where ditta=? and codgar1=(select codgar1 from gare where ngara=?)", new Object [] {dittao, ngara});
				return isLottiPubblicatiSuANAC(listaLottiAggiudicati, sqlManager);
			}
			else if (Arrays.asList(1,3).contains(genereGara))
				countSchedeAggiudicazioneInviate = GestoreG1NPAFORM.countSchede(GestoreG1NPAFORM.PUBBLICATA_AGGIUDICAZIONE_LOTTO, sqlManager ,new Object[] {ngara});
			else
				countSchedeAggiudicazioneInviate = GestoreG1NPAFORM.countSchede(GestoreG1NPAFORM.PUBBLICATE_AGGIUDICAZIONI, sqlManager, new Object[] {codgar});

			if (countSchedeAggiudicazioneInviate != null && countSchedeAggiudicazioneInviate > 0) {
				return "true";
			} else {
				return "false";
			}
		} catch (Throwable e) {
			throw new JspException("Errore nella lettura delle pubblicazioni su ANAC" ,e);
		}
	}

	public static String isLottiPubblicatiSuANAC(List<Vector<JdbcParametro>> listaLotti, SqlManager sqlManager) throws Throwable{
		for (Vector datiLotto : listaLotti) {
			String ngaraLotto = (String) ((JdbcParametro) datiLotto.get(0)).getValue();
			Long countSchedeAggiudicazioneInviate = GestoreG1NPAFORM.countSchede(GestoreG1NPAFORM.PUBBLICATA_AGGIUDICAZIONE_LOTTO, sqlManager, new Object[]{ngaraLotto});
			if (countSchedeAggiudicazioneInviate == null || countSchedeAggiudicazioneInviate == 0)
				return "false";
		}
		return "true";
	}

	public static String isPubblicataP5(String codgar, SqlManager sqlManager) throws Throwable {
		Long countSchedeP5Pubblicate = GestoreG1NPAFORM.countSchede(GestoreG1NPAFORM.PUBBLICATA_P5, sqlManager, new Object[]{codgar});
		if (countSchedeP5Pubblicate == null || countSchedeP5Pubblicate == 0)
			return "false";
		else
			return "true";
	}

}
