/*
 * Created on 26-07-2012
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
 * Funzione che controlla se alla gara è stata associata una gara OLIAMM
 * (il campo GARE.CLIV1 non nullo)
 *
 * @author Marcello Caminiti
 */
public class EsisteGaraOLIAMMFunction extends AbstractFunzioneTag {

  public EsisteGaraOLIAMMFunction() {
    super(2 , new Class[] { PageContext.class, String.class});
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String esisteGaraOliamm= "false";
    String ngara = (String) params[1];

    String select="select cliv1 from gare where ngara=?";

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {

      Long idGaraOliamm = (Long)sqlManager.getObject(select,new Object[]{ngara});
      if(idGaraOliamm!= null)
        esisteGaraOliamm = "true";
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura del campo GARE.CLIV1)",e);
    }
    return esisteGaraOliamm;
  }

}
