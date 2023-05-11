/*
 * Created on 03/09/2015
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

/**
 * Funzione per il calcolo del numero di comunicazioni ancora in bozza e quindi non inviate.
 * 
 * @author Stefano.Sabbadin
 */
public class GetNumComunicazioniBozzaFunction extends AbstractFunzioneTag {

  public GetNumComunicazioniBozzaFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    String codice = (String) params[2];
    String entita = (String) params[1];
    
    int i = 0;
    String[] filtri = new String[4];
    StringBuilder select = new StringBuilder("select count(idcom) from w_invcom where idprg=? and coment=? and comkey1=? and comstato=?");
    filtri[i++] = "PG";
    filtri[i++] = entita;
    filtri[i++] = codice;
    filtri[i++] = "1";
   
    Long conteggio = null;

    try {
      conteggio = (Long) sqlManager.getObject(select.toString(), filtri);
    } catch (SQLException e) {
      throw new JspException("Errore nel conteggio di comunicazioni in stato bozza il record con chiave " + codice, e);
    }

    return conteggio.toString();
  }

}
