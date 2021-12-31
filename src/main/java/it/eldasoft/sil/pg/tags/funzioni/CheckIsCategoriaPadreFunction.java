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
 * @author Stefano.Sabbadin
 */
public class CheckIsCategoriaPadreFunction extends AbstractFunzioneTag {

  public CheckIsCategoriaPadreFunction() {
    super(2, new Class[] {PageContext.class, String.class });
  }

  /**
   * Si controlla che non esista alcuna categoria figlia della categoria in input.
   *
   * @return true se la categoria &agrave; padre, false altrimenti
   */
  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    boolean padre = false;

    SqlManager manager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    String key = (String) params[1];

    try {
      Long occorrenze = (Long) manager.getObject("SELECT COUNT(CAISIM) FROM CAIS WHERE CODLIV1=? OR CODLIV2=? OR CODLIV3=? OR CODLIV4=?",
          new String[] {key, key, key, key });
      padre = (occorrenze > 0);
    } catch (SQLException e) {
      throw new JspException("Errore nel conteggio delle occorrenze figlie della categoria " + key  ,e);
    }

    return String.valueOf(padre);
  }

}
