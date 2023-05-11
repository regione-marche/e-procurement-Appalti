/*
 * Created on 04/12/08
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
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Estrae l'arrotondamento della cauzione provvisoria
 *
 * @author Stefano.Sabbadin
 */
public class GetArrotondamentoCauzioneProvvisoriaFunction extends
    AbstractFunzioneTag {

  public GetArrotondamentoCauzioneProvvisoriaFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String codice = (String) params[1];
    Long tipoAppalto = null;
    try {
      tipoAppalto = (Long) sqlManager.getObject(
          "select torn.tipgen from torn, gare where torn.codgar = gare.codgar1 and gare.ngara = ?",
          new Object[] { codice });
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura del campo TIPGEN", e);
    }

    int arrotondamento = 0;
    try {
      PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
          pageContext, PgManager.class);

      if(tipoAppalto !=null){
        arrotondamento = pgManager.getArrotondamentoCauzioneProvvisoria(tipoAppalto.intValue());
      }

    } catch (GestoreException e) {
      throw new JspException(
          "Errore durante l'estrazione dell'arrotondamento garanzia provvisoria",
          e);
    }

    return Integer.toString(arrotondamento);
  }

}