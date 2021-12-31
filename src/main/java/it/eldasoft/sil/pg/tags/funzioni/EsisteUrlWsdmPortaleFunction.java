/*
 * Created on 30/giu/2020
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


public class EsisteUrlWsdmPortaleFunction extends AbstractFunzioneTag{


  public EsisteUrlWsdmPortaleFunction() {
      super(2, new Class[]{PageContext.class, String.class});
  }

  @Override
public String function(PageContext pageContext, Object[] params) throws JspException {

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
              pageContext, SqlManager.class);
      
      String res = "false";
      
      String idconfi = (String)params[1];
      Long idconfiLong = new Long(idconfi);
      
      Long conteggio;
      try {
        conteggio = (Long) sqlManager.getObject("select count(*) from wsdmconfipro where chiave = 'protocollazione.wsdm.url' and idconfi = ?", new Object[]{idconfiLong});
      }  catch (SQLException e) {
        throw new JspException("Errore durante la lettura in wsdmconfipro", e);
      }
      if (conteggio.intValue()>0) {
        res = "true";
      }
      return res;
  }

}
