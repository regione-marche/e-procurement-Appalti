/*
 * Created on 19/10/21
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
 * Funzione che estrae il valore di GAREALBO.COORDSIC a partire
 * dal ngara
 *
 * @author Riccardo Peruzzo
 */
public class GetCoordsicGarealboFunction extends AbstractFunzioneTag {

  public GetCoordsicGarealboFunction() {
    super(2, new Class[] { PageContext.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ngara = (String) GeneralTagsFunction.cast("string", params[1]);
    String select="";
    String ret="";

    select="select coordsic from garealbo where ngara=?";

    try {
      String coordsic = (String)sqlManager.getObject(select, new Object[]{ngara});
      ret=coordsic;
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura del campo GAREALBO.COORDSIC ", e);
    }


    return ret;

  }

}
