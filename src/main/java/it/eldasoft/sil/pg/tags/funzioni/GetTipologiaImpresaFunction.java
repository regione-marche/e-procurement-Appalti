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

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che estrae il campo TIPIMP di IMPR.
 * La funzione viene richiamata dalla fasi ricezione e viene 
 *  passato come parametro la chiave della pagina, che è formata
 *  dai campi DITG.CODGAR5,DITG.DITTAO E DITG.NGARA5
 * 
 * @author Marcello Caminiti
 */
public class GetTipologiaImpresaFunction extends AbstractFunzioneTag {

	public GetTipologiaImpresaFunction() {
		super(2, new Class[] { PageContext.class, String.class });
	}
	
	
	public String function(PageContext pageContext, Object[] params)
			throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
				"sqlManager", pageContext, SqlManager.class);

		String result = "";
		
		String chiave = (String) GeneralTagsFunction.cast("string", params[1]);
		
		String dittao="";
		
		try{
    		
            //Il metodo viene richiamato dalle fasi di gara, in cui
            //la chiave è DITG.CODGAR5;DITG.DITTAO;DITG.NGARA5
            String campiChiave[] = chiave.split(";");
            if(campiChiave.length >0){
              for(int i=0;i<campiChiave.length;i++){
                if(campiChiave[i].indexOf("DITTAO")>0){
                  dittao = campiChiave[i].substring(chiave.indexOf(":"));
                  break;
                }
                
              }
            }
            
    		if(dittao!=null){
		        String select = "select tipimp from impr where codimp = ?";
              
                  Long tipimp = (Long) sqlManager.getObject(
	                        select, new Object[] { dittao });
	                
                  if(tipimp != null) {
                    result = String.valueOf(tipimp);
                  }
	        }
    		
		} catch (SQLException e) {
          throw new JspException(
                  "Errore durante la lettura della tipologia dell'impresa", e);
        }
		return result;
	}
}
