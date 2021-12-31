/*
 * Created on 19-04-2016
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
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Vengono controllate le condizioni per determinare se visualizzare la pagina del contratto o
 * stipula accordo quadro o aggiudicazione efficace.
 *
 *
 * @author Marcello Caminiti
 */
public class ControlliVisualizzazionePaginaContrattoFunction extends AbstractFunzioneTag {

	public ControlliVisualizzazionePaginaContrattoFunction() {
		super(3, new Class[] { PageContext.class, String.class, String.class });
	}


	@Override
    public String function(PageContext pageContext, Object[] params)
			throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
				"sqlManager", pageContext, SqlManager.class);

		String result = "";
		String select = "";
		String codgar=(String)params[1];
		String tipoCodice=(String)params[2];

		try {
			select = "select accqua, aqoper, altrisog  from torn where codgar = ?";
			if("numeroGara".equals(tipoCodice))
			  select = "select accqua, g1.aqoper, altrisog  from torn,gare1 g1 where codgar = codgar1 and ngara= ?";

			Vector datiTorn = sqlManager.getVector(select, new Object[] { codgar });

			if (datiTorn!= null && datiTorn.size()>0){
			  String accqua = SqlManager.getValueFromVectorParam(datiTorn, 0).getStringValue();
			  Long aqoper = SqlManager.getValueFromVectorParam(datiTorn, 1).longValue();
			  Long altrisog = SqlManager.getValueFromVectorParam(datiTorn, 2).longValue();
			  if("1".equals(accqua) && (((new Long(2)).equals(aqoper)) || (new Long(3)).equals(altrisog)))
			    result="stipula";
			  else if(!"1".equals(accqua) && (new Long(3)).equals(altrisog))
			    result="aggEff";
			  else
			    result="contratto";
			}

		} catch (SQLException e) {
			throw new JspException(
					"Errore durante la lettura della tabella TORN ", e);
		} catch (GestoreException e) {
		  throw new JspException(
              "Errore durante la lettura della tabella TORN ", e);
      }
		return result;
	}
}
