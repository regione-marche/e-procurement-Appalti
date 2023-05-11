/*
 * Created on 07-04-2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

/**
 * Gestore adoperato 
 * per estrarre il calcsome a partire dal valore di codgar
 *
 * @author Peruzzo
 */
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetCalcsomeFunction extends AbstractFunzioneTag {

  public GetCalcsomeFunction() {
    super(2, new Class[]{PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String calcsome = null;
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

     String codgar = (String) params[1];

      try {
    	  calcsome = (String) sqlManager.getObject(
            "select calcsome from torn where codgar= ? ", new Object[]{codgar});
      } catch (SQLException s){
        throw new JspException("Errore durante la lettura del codice di gara " +
              "calcsome", s);
      }

    if(calcsome != null && calcsome.length() > 0)
      return calcsome;
    else
      return "";
  }
}