/*
 * Created on 10/06/15
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
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che controlla se esistono delle righe visibili per l'entità specificata del generatore attributi
 *
 * @author M.C.
 */
public class EsistonoDatiGeneratoreAttributiFunction extends AbstractFunzioneTag {

  public EsistonoDatiGeneratoreAttributiFunction() {
    super(2, new Class[] {PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    String entita = (String) params[1];
    String select = "select count(DYNENT_NAME) from DYNCAM where DYNENT_NAME = ? and DYNCAM_PK =? and DYNCAM_SCH =?";
    String esistonoDatiVisibili = "FALSE";

    try {
      Long numeroCampiVisibili = (Long) sqlManager.getObject(select, new Object[] {entita,"2","1" });
      if (numeroCampiVisibili != null && numeroCampiVisibili.longValue() > 0) esistonoDatiVisibili = "TRUE";
    } catch (SQLException e) {
      throw new JspException("Errore durante i controlli per determinare se la gara esistono campi visibili nell'entità" + entita, e);
    }
    return esistonoDatiVisibili;
  }

}
