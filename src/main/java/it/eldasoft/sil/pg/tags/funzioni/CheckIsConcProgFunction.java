/*
 * Created on 12-apr-2013
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
 * Function che esegue i controlli sulla categoria in input e verifica se la categoria &egrave; padre di ulteriori sottocategorie.
 *
 * @author Manuel.Bridda
 */
public class CheckIsConcProgFunction extends AbstractFunzioneTag {

  public CheckIsConcProgFunction() {
    super(2, new Class[] {PageContext.class, String.class });
  }

  /**
   * Si controlla che non esista alcuna categoria figlia della categoria in input.
   *
   * @return true se la categoria &agrave; padre, false altrimenti
   */
  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    boolean isconcprog = false;

    SqlManager manager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    String codgar = (String) params[1];

    try {
      Long occorrenze = (Long) manager.getObject("SELECT COUNT(1) FROM TORN WHERE ISCONCPROG='1' AND CODGAR=?",
          new String[] {codgar});
      isconcprog = (occorrenze > 0);
    } catch (SQLException e) {
      throw new JspException("Errore nella verifica del valore di 'concorso di progettazione'" + codgar  ,e);
    }

    return String.valueOf(isconcprog);
  }

}
