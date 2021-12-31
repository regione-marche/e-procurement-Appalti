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

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che controlla se vi è il blocco relativo all'esistenza dei lotti
 * per la gara ad offerta unica in esame
 *
 * @author Marcello Caminiti
 */
public class EsisteBloccoLottiFunction extends AbstractFunzioneTag {

  public EsisteBloccoLottiFunction() {
    super(2 , new Class[] { PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String bloccoLotti= "false";
    String codgar = (String) params[1];
    String select="select count(ngara) from gare where codgar1=? and ngara <> codgar1";

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {

      Long numLotti = (Long)sqlManager.getObject(select,new Object[]{codgar});
      if(numLotti!= null && numLotti.longValue()>0)
        bloccoLotti = "true";


    } catch (SQLException e) {
      throw new JspException("Errore nella conteggio del numero di lotti della gara)",e);
    }
    return bloccoLotti;
  }

}
