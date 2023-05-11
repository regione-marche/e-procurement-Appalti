/*
 * Created on 09-03-2022
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

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Verifica se esistono prodotti valutati per quella ditta
 *
 *
 * @author Cristian Febas
 */
public class isProdottiValutatiDittaFunction extends AbstractFunzioneTag {

  public isProdottiValutatiDittaFunction() {
    super(3, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String result = null;
    String codiceGara = (String)params[1];
    String codiceDitta = (String)params[2];

    if (codiceGara != null && codiceGara.length() > 0) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);

      try {
    	  
		  Long cntProdottiValutati = (Long) sqlManager.getObject("select count(*) from dpre where ngara =? and dittao = ? and qtaordinata is not null",
				  new Object[] { codiceGara,codiceDitta });
		  if(Long.valueOf(0)<cntProdottiValutati) {
			  Long cntProdottiAggiudicati = (Long) sqlManager.getObject("select count(*) from v_gare_prodotti_valutati where ngara =? and dittao = ? and affidamento is not null",
					  new Object[] { codiceGara,codiceDitta });
			  if(Long.valueOf(0)<cntProdottiAggiudicati) {
				  pageContext.setAttribute("isProdottiAggiudicatiDitta",true,PageContext.REQUEST_SCOPE);  
			  }
			  
			  result="true";
		  }


      } catch (SQLException s) {
        throw new JspException("Errore durante la verifica dei prodotti valutati per operatore economico"
            + codiceGara, s);
      }


    }
    return result;
  }

}