/*
 * Created on 30/04/18
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


public class EsistonoDitteInGaraFunction extends AbstractFunzioneTag {

  public EsistonoDitteInGaraFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String campo = (String) params[1];
    String valoreCampo = (String) params[2];
    String condizione = (String) params[3];
    String esistonoDitteInGara = "false";

    if (valoreCampo != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      try {
        String selectDITG = "select count(*) from ditg where " + campo + " = ? ";
        if(condizione !=null && !"".equals(condizione))
          selectDITG+=condizione;
        Long conteggio = (Long) sqlManager.getObject(selectDITG, new Object[] {valoreCampo});

        if (conteggio != null && conteggio.longValue() > 0) {
          esistonoDitteInGara = "true";
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante il conteggio delle ditte in gara", e);
      }
    }

    return esistonoDitteInGara;
  }

}