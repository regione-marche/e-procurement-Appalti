/*
 * Created on 23/03/17
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
 * Funzione per inizializzare le sezioni delle pubblicazioni
 *
 * @author Francesco.DiMattei
 */

public class GestionePubblicazioniFunction extends AbstractFunzioneTag {

  public GestionePubblicazioniFunction() {
    super(1, new Class[] { String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String codtab = (String) params[0];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      List listaPubblicazioni = sqlManager.getListVector(
          "select CODTAB, CODPUB, TIPPUB "
              + "from TABPUB "
              + "where TABPUB.CODTAB = ? "
              + "order by TABPUB.CODPUB asc", new Object[] { codtab });

      if (listaPubblicazioni != null && listaPubblicazioni.size() > 0)
        pageContext.setAttribute("listaPubblicazioniBandoEsito", listaPubblicazioni,
            PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre le pubblicazioni con chiave "
          + codtab, e);
    }

    return null;
  }

}
