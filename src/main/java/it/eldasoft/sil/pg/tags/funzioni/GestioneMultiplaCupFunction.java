/*
 * Created on 07/03/19
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
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per inizializzare le sezioni degli N cup
 *
 */
public class GestioneMultiplaCupFunction extends AbstractFunzioneTag {

  public GestioneMultiplaCupFunction() {
    super(2, new Class[] { PageContext.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String chiave = (String) params[1];
        SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String select="select ID, NGARA, CUP from GARECUP where NGARA = ? order by id";

    try {
      List listaNCup = sqlManager.getListVector(
          select, new Object[]{chiave});

      if (listaNCup != null && listaNCup.size() > 0)
        pageContext.setAttribute("listaNCup", listaNCup,PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre i cup della tabella GARECUP con chiave" + chiave, e);
    }

    return null;
  }

}
