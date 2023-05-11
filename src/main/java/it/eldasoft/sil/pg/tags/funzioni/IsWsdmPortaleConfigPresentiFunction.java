/*
 * Created on 03/feb/2020
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;


public class IsWsdmPortaleConfigPresentiFunction extends AbstractFunzioneTag{
  
  public IsWsdmPortaleConfigPresentiFunction(){
    super(2, new Class[] { PageContext.class, String.class });
  }
  
  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    boolean res = false;
    try {
      String idconfi = (String) params[1];
      Long count = (Long) sqlManager.getObject("select count(*) from wsdmconfipro where idconfi = ? and valore is not null and chiave like 'protocollazione.wsdm%'", new Object[]{new Long(idconfi)});
      if(count.intValue()>0){
        res = true;
      }else{
        res = false;
      }
      
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura della tabella wsdmconfiuff", e);
    }
    if(!res){
        UtilityStruts.addMessage(this.getRequest(), "warning",
        "warnings.generico",new Object[]{"La configurazione per il portale Appalti non è definita."});
    }
    return null;
    
  }

}
