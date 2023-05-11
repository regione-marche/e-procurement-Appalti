/*
 * Created on 18-10-2016
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
 * Funzione che controlla l'esistenza di occorrenze di GARECONT
 * con STATO=3
 *
 * @author Marcello Caminiti
 */
public class EsisteODADefinitoFunction extends AbstractFunzioneTag {

  public EsisteODADefinitoFunction() {
    super(2, new Class[] { PageContext.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String result="NO";

    String ngara = (String) params[1];
    if (ngara !=null && !"".equals(ngara)){
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);

      try {
        Long conteggio = (Long) sqlManager.getObject(
            "select count(NGARA) from GARECONT where NGARA = ? and STATO>?", new Object[]{ngara, new Long(2)});
        if(conteggio != null && conteggio.longValue()>0)
            result = "SI";
      } catch (SQLException e) {
        throw new JspException(
            "Errore durante la lettura dello stato di GARECONT ", e);
      }

    }
    return result;
  }
}
