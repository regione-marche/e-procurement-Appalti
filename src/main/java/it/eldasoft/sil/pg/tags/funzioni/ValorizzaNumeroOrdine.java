/*
  * Created on: 30-mar-2016
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
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
 * Function che valorizza il campo ‘Numero ordine’ (NUMORD.W_CONFCOM) con il valore massimo utilizzato incrementato di 1
 *
 * @author Francesco.DiMattei
 */
public class ValorizzaNumeroOrdine extends AbstractFunzioneTag {

  public ValorizzaNumeroOrdine() {
    super(1, new Class[] {PageContext.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    Double maxNumOrdDouble = null;
    String select="select max(numord) from w_confcom";
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      Object obj = sqlManager.getObject(select, null);
      if(obj!=null){
        if (obj instanceof Long){
          maxNumOrdDouble = ((Long) obj).doubleValue();
        }else{
          if(obj instanceof Double){
            maxNumOrdDouble = (Double) obj;
          }
        }
      }
      if (maxNumOrdDouble == null) {
        maxNumOrdDouble = (double) 0;
      }
      maxNumOrdDouble = new Double(maxNumOrdDouble.doubleValue() + 1) ;

    } catch (SQLException e) {
      throw new JspException("Errore nella lettura del campo numord della w_confcom)",e);
    }
    Long ret = maxNumOrdDouble.longValue();
    return ret.toString();
  }



}
