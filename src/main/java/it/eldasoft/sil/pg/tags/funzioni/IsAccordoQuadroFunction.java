/*
 * Created on 24-02-2016
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
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Estrae il campo TORN.ACCQUA per stabilire se si tratta di Accordo Quadro
 *
 *
 * @author Febas Cristian
 */
public class IsAccordoQuadroFunction extends AbstractFunzioneTag {

  public IsAccordoQuadroFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String accordoQuadro = "";
    String select = "select accqua from torn where codgar=?";

    String codiceGara = (String) params[1];
    codiceGara = UtilityStringhe.convertiNullInStringaVuota(codiceGara);

    if (!"".equals(codiceGara)) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      try {
        accordoQuadro = (String) sqlManager.getObject(select, new Object[] { codiceGara });
      } catch (SQLException s) {
        throw new JspException("Errore durante la lettura del campo ACCQUA della gara " + codiceGara, s);
      }
    }
    return accordoQuadro;
  }

}