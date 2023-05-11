/*
 * Created on 07-02-2013
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
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Viene estratto il valore del tabellato ME005 di MEISCRIZPROD.STATO
 *
 * @author Marcello Caminiti
 */
public class GetTabellatiMEISCRIZPRODFunction extends AbstractFunzioneTag {

  public GetTabellatiMEISCRIZPRODFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    try {

      List<?> listaStato = sqlManager.getListVector("select tab1tip, tab1desc from tab1 where tab1cod = ? order by tab1tip",
          new Object[] { "ME005" });


      pageContext.setAttribute("listaStato", listaStato, PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
      throw new JspException("Errore nell'estrazione dei dati tabellati relativi ai prodotti", e);
    }
    return null;
  }

}
