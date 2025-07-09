package it.eldasoft.sil.pg.tags.funzioni.npa;

import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.sil.pg.tags.gestori.submit.npa.GestoreG1NPAFORM;
import org.apache.commons.lang.StringUtils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * @author manuel.bridda
 *
 */
public class IsSchedaP3_1CompatibileFunction extends AbstractFunzioneTag {

	public IsSchedaP3_1CompatibileFunction() {
		super(2, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		String codiceGara = (String) params[1];

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

		try {
			Long countSchedeNonP31Attive = (Long) sqlManager.getObject("select count(*) from g1npascheda, g1npaform, g1cf_npa where " +
					"g1cf_npa.id=g1npaform.idcfnpa and g1npaform.id=g1npascheda.idform and g1cf_npa.nome_scheda not in ('P3_1','CM1','ANN') and g1npascheda.statonpa not in " + GestoreG1NPAFORM.elencoStatiCancellati + " and (g1npascheda.isann is null or g1npascheda.isann = '2')" + " and g1npaform.codgar=?"
					,new Object[] { codiceGara });
			if(countSchedeNonP31Attive!=null && countSchedeNonP31Attive > 0)
                return null;

		    Vector condizioniP3_1 = sqlManager.getVector("select CL_WHERE_VIS, CL_WHERE_ULT from g1cf_npa where nome_scheda='P3_1'", null);

			if (condizioniP3_1 == null || condizioniP3_1.size() == 0)
				return null;
			else {
    			String clausulaWhereVis = SqlManager.getValueFromVectorParam(condizioniP3_1, 0).stringValue();
    			String clausulaWhereUlt = SqlManager.getValueFromVectorParam(condizioniP3_1, 1).stringValue();
    			String clausuleExtra = "";
                if (StringUtils.isNotBlank(clausulaWhereVis))
                    clausuleExtra = " and " + "(" + clausulaWhereVis + ")";
                if (StringUtils.isNotBlank(clausulaWhereUlt))
                    clausuleExtra += " and " + "(" + clausulaWhereUlt + ")";

                List<?> countVisibili = sqlManager.getListVector(GetAnacFormAggiungibiliFunction.selectFiltro + clausuleExtra, new Object[] { codiceGara });
                if (countVisibili != null && countVisibili.size() > 0) {
                  return "true";
                }
			}
			
		} catch (Exception ex) {
			throw new JspException("Errore nel caricamento della lista: " + ex.getMessage());
		}
		return null;
	}
}