/*
 * Created on 19/04/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

public class ControlliInvioComunicazioniFunction extends AbstractFunzioneTag {

  public ControlliInvioComunicazioniFunction() {
    super(4, new Class[] { PageContext.class,String.class,String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ret="OK";

    String idprg = (String) params[1];
    Long idcom = new Long((String) params[2]);
    String commodello = (String) params[3];

    try {
      Vector<?> datiComunicazione = sqlManager.getVector("select COMDATSCA, COMORASCA, COMMSGTES from w_invcom where idprg = ? and idcom = ?", new Object[] { idprg, idcom.toString() });
      if (datiComunicazione != null && datiComunicazione.size() > 0) {
        String commsgtes = SqlManager.getValueFromVectorParam(datiComunicazione, 2).getStringValue();
        if(commsgtes == null || "".equals(commsgtes)) {
          ret="NO-TESTOCOM";
        } else if ("1".equals(commodello)) {
          Timestamp comdatsca = SqlManager.getValueFromVectorParam(datiComunicazione, 0).dataValue();
          String comorasca = SqlManager.getValueFromVectorParam(datiComunicazione, 1).getStringValue();
          if (comdatsca==null || comorasca==null || "".equals(comorasca)) {
            ret="NO-DATA";
          } else {
            Date oggi = UtilityDate.getDataOdiernaAsDate();
            Date comdatscaDate = new Date(comdatsca.getTime());
            if(comorasca.indexOf(":")>0) {
              String ore = comorasca.split(":")[0];
              String minuti = comorasca.split(":")[1];
              Calendar calendar = Calendar.getInstance();
              calendar.setTime(comdatscaDate);
              calendar.add(Calendar.HOUR_OF_DAY, new Long(ore).intValue());
              calendar.add(Calendar.MINUTE, new Long(minuti).intValue());
              comdatscaDate = calendar.getTime();
            }
            if(oggi.compareTo(comdatscaDate) > 0) {
              ret="NO-TERMINISCA";
            }
          }
        }
      }
    } catch (Exception e) {
      throw new JspException("Errore nel conteggio dei soggetti destinatari", e);
    }

    return ret;

  }

}
