/*
 * Created on 31-07-2012
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
 * Funzione che preleva il campo V_GARE_OUT.RIFERIMENTO
 *
 * @author Marcello Caminiti
 */
public class GetRiferimentoOliammFunction extends AbstractFunzioneTag {

  public GetRiferimentoOliammFunction() {
    super(2 , new Class[] { PageContext.class, String.class});
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String ret= "";
    String ngara = (String) params[1];

    String select="select distinct riferimento from v_gare_out,gare where ngara=? and cliv1 = id_lista";

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {

      String riferimento = (String)sqlManager.getObject(select,new Object[]{ngara});
      if(riferimento!= null)
        ret = riferimento;
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura del campo V_GARE_OUT.RIFERIMENTO)",e);
    }
    return ret;
  }

}
