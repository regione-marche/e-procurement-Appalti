/*
 * Created on 13/09/11
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

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Ritorna eventualmente il fascicolo di gara
 * 
 * @author Cristian Febas
 */
public class GetFascicoloFunction extends AbstractFunzioneTag {

  public GetFascicoloFunction(){
    super(3, new Class[]{PageContext.class,String.class,String.class});
  }
  
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    
    String idprg = (String) params[1];
    Long idcom = new Long((String) params[2]);
    String codice = null;
    String numfasc = null;

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      codice = (String) sqlManager.getObject(
          "select comkey1 from w_invcom where idprg = ? and idcom = ?",
          new Object[] { idprg, idcom });
    } catch (SQLException s) {
      throw new JspException("Errore durante la lettura della gara della comunicazione",
          s);
    }

    if(codice!=null){
      try {
        numfasc = (String) sqlManager.getObject(
            "select numerofascicolo from fascicoli where ngarafascicolo= ?", new Object[] { codice });
        
      } catch (SQLException e) {
        throw new JspException(
            "Errore durante la lettura del fascicolo della gara ", e);
      }
      
    }

    return numfasc;
  }
  
}