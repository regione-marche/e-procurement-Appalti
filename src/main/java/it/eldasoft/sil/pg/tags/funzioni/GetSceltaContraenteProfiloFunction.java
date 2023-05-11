/*
 * Created on 18/09/19
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
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Lettura del set relativo alla scelta contraente legata al profilo (tab A1z03)
 *
 * @author C.F.
 */
public class GetSceltaContraenteProfiloFunction extends AbstractFunzioneTag {

  public GetSceltaContraenteProfiloFunction(){
    super(1, new Class[]{PageContext.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String sceltaContraenteProfilo="";
    String profiloAttivo = (String) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);

    try {
      String descTabellato = (String) sqlManager.getObject(
          "select tab2d2 from tab2 where tab2cod=? and tab2d1=?",
          new Object[] { "A1z03", profiloAttivo });
      sceltaContraenteProfilo = descTabellato;
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura del set relativo alla scelta contraente del profilo",e);
    }

    return sceltaContraenteProfilo;

  }

}