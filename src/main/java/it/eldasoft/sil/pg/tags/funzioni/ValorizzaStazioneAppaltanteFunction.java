/*
 * Created on 06/10/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Si controlla se nella Uffint esiste una sola occorrenza, in tale caso
 * di questa vengono estratti il codein ed il nomein
 *
 * @author Marcello Caminiti
 */
public class ValorizzaStazioneAppaltanteFunction extends AbstractFunzioneTag {


  public ValorizzaStazioneAppaltanteFunction(){
    super(1, new Class[]{Object.class});
  }


  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
        pageContext, GeneManager.class);

    String uffintAbilitato = (String)pageContext.getSession().getAttribute("uffint");
    String select ="select codein,nomein,iscuc from uffint";
    Vector datiUffint;
    String codein = null;
    String nomein = null;
    String iscuc = null;
    if(uffintAbilitato==null || "".equals(uffintAbilitato)){
      long occorrenzeUffint = geneManager.countOccorrenze("UFFINT", null, null);
      if(occorrenzeUffint==1){
        try {
          datiUffint = sqlManager.getVector(select, null);
          if(datiUffint.get(0) != null){
            codein = ((JdbcParametro) datiUffint.get(0)).getStringValue();
          }
          if(datiUffint.get(1) != null){
            nomein = ((JdbcParametro) datiUffint.get(1)).getStringValue();
          }
          if(datiUffint.get(2) != null){
            iscuc = ((JdbcParametro) datiUffint.get(2)).getStringValue();
          }
          pageContext.setAttribute("initCENINT", codein, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("initNOMEIN", nomein, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("initISCUC", iscuc, PageContext.REQUEST_SCOPE);
        } catch (SQLException e) {
          throw new JspException(
              "Errore durante la lettura della tabella UFFINT ", e);
        }
      }
    }else{
      try {
        select += " where codein=?";
        datiUffint = sqlManager.getVector(select, new Object[]{uffintAbilitato});
        if(datiUffint.get(1) != null){
          nomein = ((JdbcParametro) datiUffint.get(1)).getStringValue();
        }
        if(datiUffint.get(2) != null){
          iscuc = ((JdbcParametro) datiUffint.get(2)).getStringValue();
        }
        pageContext.setAttribute("initCENINT", uffintAbilitato, PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("initNOMEIN", nomein, PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("initISCUC", iscuc, PageContext.REQUEST_SCOPE);
      } catch (SQLException e) {
        throw new JspException(
            "Errore durante la lettura della tabella UFFINT ", e);
      }
    }
    return null;
  }

}