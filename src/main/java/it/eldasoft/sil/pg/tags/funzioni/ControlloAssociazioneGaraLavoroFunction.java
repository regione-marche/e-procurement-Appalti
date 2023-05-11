/*
 * Created on 20/054/11
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
 * Controlla se alla gara è stato associato un lavoro
 * 
 * @author Marcello Caminiti
 */
public class ControlloAssociazioneGaraLavoroFunction extends AbstractFunzioneTag {

  public ControlloAssociazioneGaraLavoroFunction(){
    super(2, new Class[]{PageContext.class,String.class});
  }
  
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    
    String ngara = (String)params[1];
    String select="select clavor, numera from gare where ngara = ?";
    String ret = "2";
    try {
      Vector datiGare = sqlManager.getVector(select, new Object[]{ngara});
     if (datiGare!= null && datiGare.size()>0){
       String  clavor=null;
       Long numera = null;
       if(((JdbcParametro)datiGare.get(0)).getValue()!=null){
         clavor = (String)((JdbcParametro)datiGare.get(0)).getValue();
       }
       if(((JdbcParametro)datiGare.get(1)).getValue()!=null){
         numera = (Long)((JdbcParametro)datiGare.get(1)).getValue();
       }
       if(clavor!= null && numera!=null) {
    	   Date dataRettifica = (Date) sqlManager.getObject("select ultdat9 from appa where codlav = ? and nappal = ? ", new Object[]{clavor,numera});
    	   if(dataRettifica != null) {
    		   pageContext.setAttribute("datiRettificati", Boolean.valueOf(true));
    	   }
    	   
    	   ret="1";
    	     
       }
         
     }
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura dei dati del lavoro associati alla gara ", e);
    }
    
    return ret;
  }
  
}