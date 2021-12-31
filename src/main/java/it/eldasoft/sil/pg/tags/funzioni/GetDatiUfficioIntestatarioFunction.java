/*
 * Created on 12/12/13
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Vengono estratti codice fiscale e denominazione della stazione appaltante
 * abilitata per il profilo corrente
 *
 * @author Marcello Caminiti
 */
public class GetDatiUfficioIntestatarioFunction extends AbstractFunzioneTag {

  public GetDatiUfficioIntestatarioFunction(){
    super(2, new Class[]{PageContext.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String codiceUffint = (String) params[1];
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    String select="select cfein,nomein from uffint where codein = ?";

    try {
      Vector datiUffint = sqlManager.getVector(select, new Object[]{codiceUffint});
      if(datiUffint!=null && datiUffint.size()>0){
        String cfein=null;
        if (((JdbcParametro) datiUffint.get(0)).getValue() != null)
          cfein = (String) ((JdbcParametro)
              datiUffint.get(0)).getValue();
        pageContext.setAttribute("codfiscUffint",
            cfein, PageContext.REQUEST_SCOPE);

        String nomein = null;
        if (((JdbcParametro) datiUffint.get(1)).getValue() != null)
          nomein = (String) ((JdbcParametro)
              datiUffint.get(1)).getValue();
        if(nomein!= null && nomein.length()>250)
          nomein=nomein.substring(0, 250);
        pageContext.setAttribute("denomUffint",
            nomein, PageContext.REQUEST_SCOPE);
      }
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre i dati dell'ufficio intestatario " + codiceUffint, e);
    }

    return null;
  }

}