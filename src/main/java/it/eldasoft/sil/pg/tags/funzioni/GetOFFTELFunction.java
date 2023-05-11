/*
 * Created on 02-02-2013
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
 * Estrae il campo TORN.OFFTEL
 *
 *
 * @author Marcello Caminiti
 */
public class GetOFFTELFunction extends AbstractFunzioneTag {

  public GetOFFTELFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String result = null;
    String codiceGara = (String)params[1];

    if (codiceGara != null && codiceGara.length() > 0) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);

      try {
        Long offtel = (Long) sqlManager.getObject(
            "select offtel from TORN where CODGAR = ?",
            new Object[] { codiceGara });
        if (offtel != null)
          result = String.valueOf(offtel.intValue());

      } catch (SQLException s) {
        throw new JspException("Errore durante la lettura del campo TORN.OFFTEL "
            + codiceGara, s);
      }


    }
    return result;
  }

}