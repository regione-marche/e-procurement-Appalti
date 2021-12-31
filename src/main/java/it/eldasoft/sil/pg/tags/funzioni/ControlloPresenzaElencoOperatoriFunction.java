/*
 * Created on 04/03/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class ControlloPresenzaElencoOperatoriFunction extends AbstractFunzioneTag {

  public ControlloPresenzaElencoOperatoriFunction() {
    super(3, new Class[] { PageContext.class,String.class,String.class});
  }

  //La funzione stabilisce se vi sono le condizioni per per associare un elenco alla gara.
  //Per stabilire tali condizioni viene letta l'entita CONFOPECO
  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        pageContext, PgManager.class);

    String codice = (String) GeneralTagsFunction.cast("string", params[1]);
    String entita = (String) GeneralTagsFunction.cast("string", params[2]);

    String ret = "";
    try {
      ret = pgManager.getPresenzaElencoOperatori(codice, entita);
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura dei dati della gara ", e);
    }

    return ret;

  }

}
