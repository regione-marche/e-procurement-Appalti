package it.eldasoft.sil.pg.tags.funzioni.npa;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * @author manuel.bridda
 *
 */
public class EsistonoRTNonDettagliateFunction extends AbstractFunzioneTag {

	public EsistonoRTNonDettagliateFunction() {
		super(2, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		String codgar = (String) params[1];

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

		try {
		    List<Vector> listaDitte = sqlManager.getListVector("select nomimo from DITG, impr where dittao=codimp and codgar5 = ?" +
							" and codgar5<>ngara5 and tipimp in (3,10) and (fasgar is null or fasgar >= 1)" +
							" and not exists (select coddic, impman from ragimp where codime9 = dittao) ",
			new Object[] { codgar });
			String ditte = "";
			for(int i=0; i<listaDitte.size(); i++){
                if(i>0)
					ditte += ", ";
				ditte += "'"+SqlManager.getValueFromVectorParam(listaDitte.get(i),0) + "'";
			}

			return ditte;

		} catch (Exception ex) {
			throw new JspException("Errore nella verifica dello stato dell'eventuale scheda P3_1" + ex.getMessage());
		}
    }
}