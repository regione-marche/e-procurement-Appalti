/*
 * Created on 25-02-2022
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
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che controlla se vi sono dei lotti in una gara
 * @author Peruzzo Riccardo
 */
public class EsistonoLottiFunction extends AbstractFunzioneTag {

  public EsistonoLottiFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String codgar = (String) GeneralTagsFunction.cast("string", params[1]);
    String ret="false";

    if (codgar != null && codgar.length()>0){
      String select="select count(ngara) from gare where codgar1=? and codgar1<>ngara";
      try {
        Long conteggio = (Long)sqlManager.getObject(select, new Object[] { codgar });
        if(conteggio!=null && conteggio.longValue()>0)
          ret = "true";
      } catch (SQLException e) {
          throw new JspException(
              "Errore durante la lettura dei lotti della gara ", e);
      }
    }
    return ret;
  }
}
