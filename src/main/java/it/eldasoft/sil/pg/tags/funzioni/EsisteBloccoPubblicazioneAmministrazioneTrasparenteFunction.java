/*
 * Created on 04/03/14
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
 * Funzione che controlla se è stata eseguita la pubblicazione su Amministrazione trasparente
 *
 * @author Cristian Febas
 */
public class EsisteBloccoPubblicazioneAmministrazioneTrasparenteFunction extends AbstractFunzioneTag {

  public EsisteBloccoPubblicazioneAmministrazioneTrasparenteFunction() {
    super(3, new Class[] {PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    String chiaveGara = (String) params[1];
    String select = "select count(NGARA) from PUBG where NGARA = ? and (TIPPUBG=14)";
    String pubblicazioneSuAmministrazioneTrasparente = "FALSE";

    try {
      Long numeroPubblicazioni = (Long) sqlManager.getObject(select, new Object[] {chiaveGara });
      if (numeroPubblicazioni != null && numeroPubblicazioni.longValue() > 0) pubblicazioneSuAmministrazioneTrasparente = "TRUE";
    } catch (SQLException e) {
      throw new JspException("Errore durante i controlli per determinare se la gara è pubblicata sulla Amministrazione trasparente", e);
    }
    return pubblicazioneSuAmministrazioneTrasparente;
  }

}
