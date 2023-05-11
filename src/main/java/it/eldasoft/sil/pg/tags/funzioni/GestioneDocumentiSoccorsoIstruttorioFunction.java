/*
 * Created on 08/02/21
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
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;


public class GestioneDocumentiSoccorsoIstruttorioFunction extends AbstractFunzioneTag {

  public GestioneDocumentiSoccorsoIstruttorioFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String idprg = (String) params[1];
    String idcomString = (String) params[2];
    Long idcom = null;
    if(idcomString != null && !"".equals(idcomString))
      idcom = new Long(idcomString);

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      List listaDocumenti = sqlManager.getListVector(
          "select ID, IDPRG, IDCOM, NUMORD, DESCRIZIONE, OBBLIGATORIO, FORMATO "
              + "from G1DOCSOC "
              + "where IDPRG = ? and IDCOM = ? "
              + "order by NUMORD", new Object[] { idprg, idcom});

      if (listaDocumenti != null && listaDocumenti.size() > 0)
        pageContext.setAttribute("documentiSoccorsoIstruttorio", listaDocumenti,
            PageContext.REQUEST_SCOPE);
      Long maxNumord = (Long)sqlManager.getObject("select max(numord) from G1DOCSOC where IDPRG = ? and IDCOM = ?", new Object[] { idprg, idcom});
      if(maxNumord == null)
        maxNumord = new Long(0);
      pageContext.setAttribute("maxNumord", maxNumord,PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre i documenti soccorso istruittorio per la comunicazione "
          + idprg + " " + idcomString, e);
    }

    return null;
  }

}
