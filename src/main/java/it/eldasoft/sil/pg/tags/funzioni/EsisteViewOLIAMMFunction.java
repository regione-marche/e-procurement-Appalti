/*
 * Created on 17-09-2012
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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che controlla se nel db è presente la view V_GARE_OUT,
 * specifica per OLIAMM
 *
 * @author Marcello Caminiti
 */
public class EsisteViewOLIAMMFunction extends AbstractFunzioneTag {

  public EsisteViewOLIAMMFunction() {
    super(1 , new Class[] { PageContext.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String esisteViewOLIAMM= "false";

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    if(sqlManager.isTable("V_GARE_OUT"))
        esisteViewOLIAMM = "true";



    return esisteViewOLIAMM;
  }

}
