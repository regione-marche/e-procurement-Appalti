/*
 * Created on 11/10/10
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
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che controlla se è stata eseguita la pubblicazione su portale Alice
 *
 * @author Marcello Caminiti
 */
public class GetCodcigInvitiPregressiFunction extends AbstractFunzioneTag {

  public GetCodcigInvitiPregressiFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String genere = (String) params[1];
    String ngara = (String) params[2];
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String cig = "";
    try {
      if("3".equals(genere)){
        cig = (String) sqlManager.getObject("select codcig from v_gare_torn where codice = ?", new Object[]{ngara});
      }else{
        cig = (String) sqlManager.getObject("select codcig from gare where ngara = ?", new Object[]{ngara});
      }
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura del codice CIG", e);
    }
    return cig;

  }

}
