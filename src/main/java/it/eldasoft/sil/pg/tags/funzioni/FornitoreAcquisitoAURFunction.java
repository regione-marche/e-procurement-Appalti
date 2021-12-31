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
 * Controlla il campo acquisizione per stabilire se il fornitore è acquisito
 * da AUR 
 * 
 * @author Marcello Caminiti
 */
public class FornitoreAcquisitoAURFunction extends AbstractFunzioneTag {

  public FornitoreAcquisitoAURFunction(){
    super(4, new Class[]{PageContext.class,String.class,String.class,String.class});
  }
  
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    
    String fornitoreAcquisito = "NO";
    String codgara = (String) GeneralTagsFunction.cast("string", params[1]);
    String ngara = (String) GeneralTagsFunction.cast("string", params[2]);
    String fornitore = (String) GeneralTagsFunction.cast("string", params[3]);
    String select="select acquisizione from ditg where codgar5=? and ngara5=? and dittao=?";
    try {
      Long acquisizione = (Long)sqlManager.getObject(select, new Object[]{codgara,ngara,fornitore});
      if(acquisizione!= null && acquisizione.longValue()==12)
        fornitoreAcquisito = "SI";
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura del campo acquisizione della ditta ", e);
    }
    
    return fornitoreAcquisito;
  }
  
}