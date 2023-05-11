/*
 * Created on 29/mar/2019
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;


public class CheckCategoriaPresenteLottiFunction extends AbstractFunzioneTag{
  
  public CheckCategoriaPresenteLottiFunction() {
    super(3, new Class[] {PageContext.class, Object.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    
    String categoria = null;
    if(params[1] == null){
      return "false";
    }
    if( params[1] instanceof String){
      categoria = (String) params[1];
    }else{
      JdbcParametro jdbcParametro = (JdbcParametro) params[1];
      categoria = jdbcParametro.getStringValue();
    }
    String codiceGara = (String) params[2];
    
    List listaLotti = null;
    try {
      listaLotti = sqlManager.getListVector("select ngara from gare where codgar1 = ? and codgar1 <> ngara",
              new Object[] { codiceGara });
      boolean flag = false;
      if (listaLotti != null && listaLotti.size() > 0) {
        String ngaraLotto = null;
        for (int i = 0; i < listaLotti.size(); i++) {
          ngaraLotto = (String) SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).getValue();
          Long occorrenze = (Long) sqlManager.getObject("SELECT COUNT(CATIGA) FROM CATG WHERE NGARA=? and CATIGA = ?",
              new String[] {ngaraLotto, categoria });
          if(occorrenze > 0){
            flag = true;
          }else{
          occorrenze = (Long) sqlManager.getObject("SELECT COUNT(CATOFF) FROM OPES WHERE NGARA3=? and CATOFF = ?",
              new String[] {ngaraLotto, categoria });
          if(occorrenze > 0){
            flag = true;
            }
          }
        } 
      }
      return ""+flag;
    }catch (SQLException e) {
      throw new JspException("Errore nel conteggio delle occorrenze figlie della categoria " + e);
    }
  }

}
