/*
 * Created on 04/03/11
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
 * Funzione che estrae il valore di GARUUID.UUID
 *
 * @author Marcello Caminiti
 */
public class GetUuidFunction extends AbstractFunzioneTag {

  public GetUuidFunction() {
    super(3, new Class[] { PageContext.class,String.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String codice = (String) GeneralTagsFunction.cast("string", params[1]);
    String tipo = (String) GeneralTagsFunction.cast("string", params[2]);
    String select="select uuid from garuuid where codgar=? and tipric=?";
    String ret="";

    if("BANDO".equals(tipo))
      tipo="ATCPAT_bando";
    else
      tipo="ATCPAT_esito";
    try {
      String uuid = (String)sqlManager.getObject(select, new Object[]{codice,tipo});
      ret=uuid;
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura di GARUUID.UUID per la gara " + codice, e);
    }


    return ret;

  }

}
