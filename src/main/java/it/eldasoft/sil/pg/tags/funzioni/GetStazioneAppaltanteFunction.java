/*
 * Created on 21/04/11
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
 * Restituisce la stazione appaltante della gara
 * 
 * @author Marcello Caminiti
 */
public class GetStazioneAppaltanteFunction extends AbstractFunzioneTag {

  public GetStazioneAppaltanteFunction(){
    super(3, new Class[]{PageContext.class,String.class,String.class});
  }
  
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    
    String entita = (String) GeneralTagsFunction.cast("string", params[2]);
    String codStazioneAppaltante= null;
    String chiave = (String) GeneralTagsFunction.cast("string", params[1]);
    String select=null;
    try {
      if("TORN".equals(entita))
        select="select cenint from torn where codgar=?";
      else
        select="select cenint from gare,torn where ngara = ? and codgar1 = codgar";
      codStazioneAppaltante= (String)sqlManager.getObject(select, new Object[]{chiave});
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura della stazione appaltante della gara ", e);
    }
    
    return codStazioneAppaltante;
  }
  
}