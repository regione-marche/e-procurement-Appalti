/*
 * Created on 18-08-2015
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
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che determina la applicabilità o meno del campo discriminante la
 * applicazione della legge regionale per la Sicilia
 *
 * @author Cristian Febas
 */
public class IsLeggeRegioneSiciliaFunction extends AbstractFunzioneTag {

  public IsLeggeRegioneSiciliaFunction() {
    super(1, new Class[] { PageContext.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String result = "1";

      try {

        Vector vectTabA1116 = sqlManager.getVector("select TAB1TIP, TAB1DESC from TAB1 WHERE TAB1COD = 'A1116'" +
        		" order by TAB1TIP", new Object[] {});

        if (vectTabA1116 != null && vectTabA1116.size() > 0) {
          Long tipTab =  SqlManager.getValueFromVectorParam(vectTabA1116, 0).longValue();
          String descTab = SqlManager.getValueFromVectorParam(vectTabA1116, 1).toString();
          descTab = descTab.substring(0, descTab.indexOf(' '));

          if (descTab.length() > 0) {
              if (new Long(1).equals(tipTab)){
                if ("0".equals(descTab)){
                  result = "-1";
                }else{
                  result = "1";
                }
              }
          }else{
              //gestisci eccezione
              result = "-1";
          }
        }
      } catch (SQLException e) {
        throw new JspException(
            "Errore durante la lettura dei dati della gara per la Legge Regionale Sicilia", e);
      } catch (GestoreException e) {
        throw new JspException(
            "Errore in fase di determinazione dei parametri della gara per la Legge Regionale Sicilia", e);
      }

    return result;
  }

}
