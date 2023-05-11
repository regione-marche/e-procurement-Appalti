/*
 * Created on 20-Nov-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

/**
 * Estrae il campo GARE.CODGAR1 necessario alle pagine prive di tale campo o
 * equivalente. Tale campo e' necessario per determinare i permessi dell'utente
 * sui dati della lista/scheda in analisi
 * 
 * @author Luca.Giacomazzo
 */
import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetCodiceGaraFunction extends AbstractFunzioneTag {

  public GetCodiceGaraFunction() {
    super(1, new Class[]{PageContext.class});
  }
  
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    
    pageContext = (PageContext) params[0];
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    
    String numeroGara = GeneralTagsFunction.getValCampo(UtilityTags.getParametro(
        pageContext, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA), "NGARA");
    if(numeroGara.length() == 0)
      numeroGara = GeneralTagsFunction.getValCampo(UtilityTags.getParametro(
          pageContext, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT), "NGARA");

    String codiceGara = null;
    try {
      codiceGara = (String) sqlManager.getObject(
          "select CODGAR1 from GARE where NGARA = ?", new Object[]{numeroGara});
    } catch (SQLException s){
      throw new JspException("Errore durante la lettura del codice di gara " +
            "(GARE.FASGAR)", s);
    }
    if(codiceGara != null && codiceGara.length() > 0)
      return codiceGara;
    else
      return "";
  }

}