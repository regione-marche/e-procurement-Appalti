/*
 * Created on 02/dic/09
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * La funzione determina la where da applicare alla pagina Lotti aggiudicati
 * a cui si accede dalla pagina Atti aggiudicazione e contrattuale 
 * 
 * @author Marcello Caminiti
 */
public class GestioneLottiAggiudicatiFunction  extends AbstractFunzioneTag {

	public GestioneLottiAggiudicatiFunction() {
		super(2, new Class[] { PageContext.class, String.class });
	}

	public String function(PageContext pageContext, Object[] params)
			throws JspException {

		String codgar = "";
		String ngara = "";
		String ditta = "";
		String where="";
		
		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
				"sqlManager", pageContext, SqlManager.class);

		String chiave = (String) GeneralTagsFunction.cast("string", params[1]);
		ngara = chiave.substring(chiave.indexOf(":") + 1);

		try {
			String select = "select codgar1, ditta from gare where ngara= ?";

			Vector datiGARE = sqlManager.getVector(select,
					new Object[] { ngara });

			if (datiGARE != null && datiGARE.size() > 0) {
				codgar = ((String) ((JdbcParametro) datiGARE.get(0)).getValue());
				ditta = ((String) ((JdbcParametro) datiGARE.get(1)).getValue());
				
				if (codgar == null) codgar="";
				if (ditta == null) ditta="";
				
				where="GARE.CODGAR1 = '" + codgar + "' and GARE.DITTA = '"+ ditta + "' and (GARE.GENERE is null)";
						
			}
		} catch (SQLException e) {
			throw new JspException("Errore durante l'estrazione dei dati dei lotti aggiudicati", e);
		} 
		return where;
	}

	
}