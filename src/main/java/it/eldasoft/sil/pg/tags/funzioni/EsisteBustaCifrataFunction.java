/*
 * Created on 18-02-2016
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
 * Funzione che controlla l'esistenza in CHIAVIBUSTE di una occorrenza per la gara
 * per il tipo di busta specificato, con valorizzata la chiave privata.
 *
 * @author Marcello Caminiti
 */
public class EsisteBustaCifrataFunction extends AbstractFunzioneTag {

  public EsisteBustaCifrataFunction() {
    super(3 , new Class[] { PageContext.class, String.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String ngara = (String) params[1];
    String tipoBusta = (String) params[2];
    String ret="false";

    String select="select count(id) from chiavibuste where ngara=? and busta=? and chiavepriv is not null";
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      Long conteggio = (Long)sqlManager.getObject(select,new Object[]{ngara,tipoBusta});
      if(conteggio!= null && conteggio.longValue() >0)
        ret = "true";


    } catch (SQLException e) {
      throw new JspException("Errore nella lettura di CHIAVIBUSTE)",e);
    }
    return ret;
  }

}
