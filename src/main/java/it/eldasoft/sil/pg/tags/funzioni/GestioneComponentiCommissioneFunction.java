/*
 * Created on 19/nov/08
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
 * Funzione per inizializzare le sezioni dei componenti della commissione di
 * una gara a lotto unico o di un lotto di gara in fase di modifica
 *
 * @author Luca.Giacomazzo
 */
public class GestioneComponentiCommissioneFunction extends AbstractFunzioneTag {

  public GestioneComponentiCommissioneFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String nGara = (String) params[0];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    try {
      List listaComponentiCommissione = sqlManager.getListVector(
          "select NGARA2, CODFOF, NOMFOF, INCFOF, INTFOF, IMPFOF, IMPLIQ, IMPSPE, DLIQSPE, INDISPONIBILITA, MOTIVINDISP, DATARICHIESTA, DATAACCETTAZIONE, ESPGIU, ID " +
            "from GFOF where NGARA2 = ? order by ID", new Object[]{nGara});

      if (listaComponentiCommissione != null && listaComponentiCommissione.size() > 0)
        pageContext.setAttribute("commissione", listaComponentiCommissione,
            PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre i componenti della " +
            "commissione della gara " + nGara, e);
    }
    return null;
  }

}