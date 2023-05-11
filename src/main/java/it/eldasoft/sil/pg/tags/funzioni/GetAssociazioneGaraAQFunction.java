/*
 * Created on 20/10/22
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
import java.util.Date;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Ottiene il clavor e il numera della gara a partire dal numero gara
 * 
 * @author Manuel Bridda
 */
public class GetAssociazioneGaraAQFunction extends AbstractFunzioneTag {

  public GetAssociazioneGaraAQFunction(){
    super(2, new Class[]{PageContext.class,String.class});
  }
  
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    
    String ngara = (String)params[1];
    String select="select cogg.c0akey1, cogg.c0akey2 from gare, garecont gc, c0oggass cogg where gare.ngara = ? and "
        + "(gc.codimp=gare.ditta or gc.codimp is null) and "
        + "((gc.ngara=gare.ngara and gc.ncont=1) or (gc.ngara=gare.codgar1 and (gc.ngaral is null or gc.ngaral=gare.ngara))) "
        + "and gc.ngara=cogg.c0akey1 and gc.ncont=cogg.c0akey2  and cogg.c0aent = 'GARECONT'"; 
    try {
      Vector datiGare = sqlManager.getVector(select, new Object[]{ngara});
      if (datiGare!= null && datiGare.size()>0){
        String c0akey1 = null;
        String c0akey2 = null;
        if(((JdbcParametro)datiGare.get(0)).getValue()!=null){
          c0akey1 = (String)((JdbcParametro)datiGare.get(0)).getValue();
        }
        if(((JdbcParametro)datiGare.get(1)).getValue()!=null){
          c0akey2 = (String)((JdbcParametro)datiGare.get(1)).getValue();
        }
        if(c0akey1!= null && c0akey2!=null) {
          pageContext.setAttribute("c0akey1", c0akey1);
          pageContext.setAttribute("c0akey2", c0akey2);
        }    
       }  
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura dei dati del lavoro associati alla gara ", e);
    }
    
    return "";
  }
  
}