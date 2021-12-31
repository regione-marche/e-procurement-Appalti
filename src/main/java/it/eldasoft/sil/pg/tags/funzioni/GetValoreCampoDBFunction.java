/*
 * Created on 09-08-2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

/**
 * La funzione legge un campo di una tabella eseguendo la where passata come parametro.
 * Il valore ritornato dal db viene convertito in stringa
 *
 *
 */
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetValoreCampoDBFunction extends AbstractFunzioneTag {

  public GetValoreCampoDBFunction() {
    super(4, new Class[] {PageContext.class, String.class, String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String campo = (String) params[1];
    String entita = (String) params[2];
    String where = (String) params[3];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String select = "select " + campo + " from " + entita + " where " + where;
    String ret=null;
    Object valore = null;
    try {
      valore = sqlManager.getObject(select, null);
      if(valore!=null){
        if(valore instanceof Date){
          Date data = (Date) valore;
          ret = UtilityDate.convertiData(data, UtilityDate.FORMATO_GG_MM_AAAA);
        }else if(valore instanceof Long){
          Long valoreLong = (Long) valore;
          ret = Long.toString(valoreLong.longValue());
        }else if(valore instanceof Double){
          Double valoreDouble = (Double) valore;
          ret = Double.toString(valoreDouble.doubleValue());
        }else{
          ret = (String)valore;
        }

      }
    } catch (SQLException s) {
      throw new JspException("Errore durante la lettura del campo " + campo + " della tabella " + entita , s);
    }


    return ret;
  }

}