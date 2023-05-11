/*
 * Created on 21-10-2016
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
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
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che controlla se vi sono delle fasi dell'asta elettronica ancora in corso
 *
 * @author Marcello Caminiti
 */
public class EsistonoFasiAstaInCorsoFunction extends AbstractFunzioneTag {

  public EsistonoFasiAstaInCorsoFunction() {
    super(2 , new Class[] { PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String esistonoFasi= "false";
    String ngara = (String) params[1];
    String select="select count(id) from aefasi where ngara=? and dataorafine > ?";

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      Date oggi = UtilityDate.getDataOdiernaAsDate();
      Long numFasi = (Long)sqlManager.getObject(select,new Object[]{ngara, oggi});
      if(numFasi!= null && numFasi.longValue()>0)
        esistonoFasi = "true";


    } catch (SQLException e) {
      throw new JspException("Errore nella conteggio del numero di lotti della gara)",e);
    }
    return esistonoFasi;
  }

}
