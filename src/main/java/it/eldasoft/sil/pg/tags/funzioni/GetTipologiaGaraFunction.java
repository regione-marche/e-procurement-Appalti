/*
 * Created on 8-ott-2009
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
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che estrae il campo GENERE di V_GARE_TORN.
 * I valori possibili sono:
 * 1 - gara divisa in lotti con offerte distinte
 * 2 - gara a lotto unico
 * 3 - gara divisa in lotti con offerta unica
 * 10 - elenco
 * 11 - avvisi
 * 20 - catalogo elettronico
 *
 * @author Marcello Caminiti
 */
public class GetTipologiaGaraFunction extends AbstractFunzioneTag {

	public GetTipologiaGaraFunction() {
		super(2, new Class[] { PageContext.class, String.class });
	}


	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
				"sqlManager", pageContext, SqlManager.class);

		String result = "";
		String codice ="";
		String chiave = (String) GeneralTagsFunction.cast("string", params[1]);
		codice = chiave.substring(chiave.indexOf(":") + 1);
		String codgar="";

		try {
			if (chiave.indexOf("NGARA") > 0) {
				codgar = (String) sqlManager.getObject(
						"select codgar1 from gare where ngara = ?", new Object[] { codice });
			} else codgar = codice;

			Long genere = (Long) sqlManager.getObject(
					"select genere from v_gare_torn where codgar=?", new Object[]{codgar});

			if(genere != null) {
	    	  result = String.valueOf(genere);
    	    } else {
    	    	// Il campo GENERE puo' non essere stato estratto perche' la gara in
    	    	// questione e' per elenco operatori economici
    	    	genere = (Long) sqlManager.getObject(
    						"select genere from v_gare_eleditte where codgar=?", new Object[]{codgar});
    	    	if(genere != null){
    	    		result = String.valueOf(genere);
    	    	}else{
    	    	  // Il campo GENERE puo' non essere stato estratto perche' la gara in
                  // questione e' un catalogo elettronico
                  genere = (Long) sqlManager.getObject(
                              "select genere from v_gare_catalditte where codgar=?", new Object[]{codgar});
                  if(genere != null){
                    result = String.valueOf(genere);
                  }else{
                    //Si controlla se si tratta di un avviso
                    genere=(Long) sqlManager.getObject(
                        "select genere from gare where codgar1=?", new Object[]{codgar});
                    if(genere != null)
                      result = String.valueOf(genere);
                  }
    	    	}
    	    }
		} catch (SQLException e) {
			throw new JspException(
					"Errore durante la lettura della tipologia della gara ", e);
		}

		return result;
	}

}