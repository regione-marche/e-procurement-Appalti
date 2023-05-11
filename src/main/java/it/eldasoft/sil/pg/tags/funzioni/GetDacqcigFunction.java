/*
 * Created on 09-08-2015
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
 * Gestore adoperato per estrarre la DACQCIG di gare a partire dal valore di ngara 
 *
 *
 * @author Francesco.DiMattei
 */
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetDacqcigFunction extends AbstractFunzioneTag {

  public GetDacqcigFunction() {
    super(2, new Class[] {PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String ngara = (String) params[1];


    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    Date data = null;
    String dacqcig = null;
    try {
        data = (Date) sqlManager.getObject("select dacqcig from gare where NGARA = ?", new Object[] {ngara});
    } catch (SQLException s) {
      throw new JspException("Errore durante la lettura della Data acquisizione codice CIG ", s);
    }

    if (data != null) {
      dacqcig = UtilityDate.convertiData(data, UtilityDate.FORMATO_GG_MM_AAAA);
      pageContext.setAttribute("DACQCIG", dacqcig, PageContext.REQUEST_SCOPE);
      return dacqcig;
    } else {
      return "";
    }
  }

}