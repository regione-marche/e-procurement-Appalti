/*
 * Created on 21/10/20
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

public class EsistonoDitteInviatoOffertaNullaFunction extends AbstractFunzioneTag {

  public EsistonoDitteInviatoOffertaNullaFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String codgar = (String) params[1];
    String saltoGara = (String) params[2];
    String esistonoDitteInvioOffertaNulla = "false";

    if (codgar != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      try {
        String selectDITG = "select count(*) from ditg where codgar5 = ? and invoff is null and (fasgar is null or fasgar>1)";
        if("1".equals(saltoGara))
          selectDITG += " and ngara5 != codgar5";
        Long conteggio = (Long) sqlManager.getObject(selectDITG, new Object[] {codgar});

        if (conteggio != null && conteggio.longValue() > 0) {
          esistonoDitteInvioOffertaNulla = "true";
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo del campo invoff delle ditte della gara", e);
      }
    }

    return esistonoDitteInvioOffertaNulla;
  }

}