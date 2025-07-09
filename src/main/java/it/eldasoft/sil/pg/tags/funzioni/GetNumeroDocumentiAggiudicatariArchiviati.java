/*
 * Created on 28/03/23
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.tasks.ConservazioneDocumentiAggiudicatariManager;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione per verificare la cardinalità dei documenti relativi agli aggiudicatari
 * 
 * @author Manuel Bridda
 */
public class GetNumeroDocumentiAggiudicatariArchiviati extends AbstractFunzioneTag {

  public GetNumeroDocumentiAggiudicatariArchiviati() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    
    String codgar = (String) params[1];
    
    //il valore booleano nella funzione permette di prelevare tutti i documenti di interesse (false)
    //o solo quelli relativi agli aggiudicatari (true)
    return ConservazioneDocumentiAggiudicatariManager.getDocumentiDaGestire(sqlManager, codgar, false).size()+"";
    
  }

}