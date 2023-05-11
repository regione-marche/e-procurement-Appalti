/*
 * Created on 09-Feb-2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che estrae il numero di gare associate ad uno specifico ids
 *
 *
 * @author Cristian Febas
 */
public class GetLivelloGareAssociateIdsFunction extends AbstractFunzioneTag {

	public GetLivelloGareAssociateIdsFunction() {
		super(3, new Class[] { PageContext.class, String.class });
	}


	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
				"sqlManager", pageContext, SqlManager.class);

		String livelloGareAssociate = "0";
		String ids_prog = (String)params[1];
        String codiceGara = (String)params[2];
        codiceGara= UtilityStringhe.convertiNullInStringaVuota(codiceGara);

		try {

		    String selectNumGare="select count(*) from v_lista_gare_ids where ids_prog = ? and codice_gara is not null";
		    String selectCodiceGara="select count(*) from v_lista_gare_ids where ids_prog = ? and codice_gara = ? ";

			Long numGareAssociate = (Long) sqlManager.getObject(
						selectNumGare, new Object[] { ids_prog });
			if(!new Long(0).equals(numGareAssociate) && !"".equals(codiceGara)){
	            Long numGaraAssociata = (Long) sqlManager.getObject(
                    selectCodiceGara, new Object[] { ids_prog,codiceGara });
	            if(!new Long(0).equals(numGaraAssociata)){
	              livelloGareAssociate = "1";
	            }else{
	              livelloGareAssociate = "2";
	            }
			}else{
			  livelloGareAssociate = numGareAssociate.toString();
			}
		} catch (SQLException e) {
			throw new JspException(
					"Errore durante la lettura del numero di gare associate ad uno specifico ids ", e);
		}

		return livelloGareAssociate;
	}
}
