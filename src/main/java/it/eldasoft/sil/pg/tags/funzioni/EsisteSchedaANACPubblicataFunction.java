/*
 * Created on 27/02/24
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
 * Funzione che controlla se per la gara esistono schede ANAC in stato Pubblicato
 *
 * @author Manuel Bridda
 */
public class EsisteSchedaANACPubblicataFunction extends AbstractFunzioneTag {

  public EsisteSchedaANACPubblicataFunction() {
    super(3, new Class[] {PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    String codgar = (String) params[1];
    
    String result="false";
    
    try {
      result=esisteSchedaANACPubblicata(sqlManager,codgar);
    }catch (SQLException e) {
      throw new JspException("Errore durante i controlli per determinare la presenza di schede ANAC pubblicate", e);
    }
    
    return result;
  }
  
  public String esisteSchedaANACPubblicata(SqlManager sqlManager, String codgar) throws SQLException {

    String select = "select count(*) from g1npascheda, g1npaform, g1cf_npa where g1npascheda.idform=g1npaform.id and g1cf_npa.id=g1npaform.idcfnpa and g1npascheda.statonpa=60 "
        + "and g1cf_npa.retterm is not null and g1npaform.codgar=?";
    
    String schedePubblicateANAC = "false";

    Long numeroSchede = (Long) sqlManager.getObject(select, new Object[] {codgar});
    if (numeroSchede != null && numeroSchede.longValue() > 0) schedePubblicateANAC = "true";
      
    return schedePubblicateANAC;
  
  }

}
