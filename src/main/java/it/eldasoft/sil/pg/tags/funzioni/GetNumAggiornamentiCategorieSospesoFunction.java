/*
 * Created on 27/01/2017
 *
 * Copyright (c) Maggioli S.p.A. - Divisione EldaSoft
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
 * Funzione per il calcolo del numero di aggiornamenti delle categorie per una ditta in sospeso.
 *
 * @author M. Caminiti
 */
public class GetNumAggiornamentiCategorieSospesoFunction extends AbstractFunzioneTag {

  public GetNumAggiornamentiCategorieSospesoFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    String codice = (String) params[2];
    String ditta = (String) params[1];

    String select = "select count(id) from garacquisiz where ngara = ? and codimp = ? and stato = ? ";
    Long conteggio = null;

    try {
      conteggio = (Long) sqlManager.getObject(select, new Object[]{codice, ditta, new Long(1)});
    } catch (SQLException e) {
      throw new JspException("Errore nel conteggio delle richieste di aggiornamento delle categorie per l'elenco " + codice, e);
    }

    return conteggio.toString();
  }

}
