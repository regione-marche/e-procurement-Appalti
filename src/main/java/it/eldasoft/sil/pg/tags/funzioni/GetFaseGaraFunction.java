/*
 * Created on 05-Nov-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Estrae il campo GARE.FASGAR necessario per la pagina gare-pagine-scheda.jsp
 * per rendere attivo o meno il tab relativo alla pagina 'Fasi di gara' a
 * partire dal campo chiave GARE.NGARA
 * 
 * @author Luca.Giacomazzo
 */
public class GetFaseGaraFunction extends AbstractFunzioneTag {

  public GetFaseGaraFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    
    String numeroGara = (String) UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
    numeroGara = numeroGara.substring(numeroGara.indexOf(":") + 1);
    
    Long faseDiGara = null;
    try {
      faseDiGara = (Long) sqlManager.getObject(
          "select FASGAR from GARE where NGARA = ?", new Object[]{numeroGara});
    } catch (SQLException s){
      throw new JspException("Errore durante la lettura della fase di gara " +
            "(GARE.FASGAR)", s);
    }
    if(faseDiGara != null)
      return faseDiGara.toString();
    else
      return "0";
  }

}