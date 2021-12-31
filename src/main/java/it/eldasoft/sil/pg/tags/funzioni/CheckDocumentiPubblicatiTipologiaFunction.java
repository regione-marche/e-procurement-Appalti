/*
 * Created on 30/apr/2019
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;


public class CheckDocumentiPubblicatiTipologiaFunction extends AbstractFunzioneTag{

  public CheckDocumentiPubblicatiTipologiaFunction() {
    super(5, new Class[] {PageContext.class, String.class,String.class,String.class,String.class});
  }
  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    
    Long count = new Long(0);
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    String codgar = (String) params[1];
    String tipologia = (String) params[2];
    String busta = (String) params[3];
    String gruppo = (String) params[4];
    
    String select = "select count(*) from documgara where codgar = ? and statodoc = 5";

    if(tipologia != null && tipologia.length() > 0){
      select+=" and tipologia = " + tipologia;
    }
    if(busta != null && busta.length() > 0){
      select+=" and busta = " + busta;
    }
    if(gruppo != null && gruppo.length() > 0){
      select+=" and gruppo = " + gruppo;
    }
    
    try {
      count  = (Long) sqlManager.getObject(select, new Object[] {codgar});
    } catch (SQLException e) {
      throw new JspException("Errore nel conteggio dei documenti",e);
    }
    
    if(count>0){return "true";}
    return "false";
    
  }

}

