/*
 * Created on 10/04/20
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;


public class GestoreInsertDocumentiGruppo15 extends AbstractGestorePreload {

  public GestoreInsertDocumentiGruppo15(BodyTagSupportGene tag) {
    super(tag);
  }


  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);

    // lettura dei parametri di input
    String codgar = page.getRequest().getParameter("codgar");

    String select= "";
    Long numDoc= null;

    try {
      select="select count(codgar) from documgara where codgar=? and gruppo = ?";

      numDoc = (Long) sqlManager.getObject(select, new Object[] {codgar, new Long(15)});

      if (numDoc!= null && numDoc.longValue()>0)
        page.setAttribute("documentiPresenti","SI", PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante il calcolo del numero dei documenti delle delibere a procedere per la gara" + codgar,e);
    }
  }
}