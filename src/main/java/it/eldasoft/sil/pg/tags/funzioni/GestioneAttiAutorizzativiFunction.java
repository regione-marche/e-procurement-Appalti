/*
 * Created on 03/nov/08
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
 * Funzione per inizializzare le sezioni degli atti autorizzativi di una gara a
 * lotto unico o di un lotto di gara in fase di modifica
 *
 * @author Stefano.Sabbadin
 */
public class GestioneAttiAutorizzativiFunction extends AbstractFunzioneTag {

  public GestioneAttiAutorizzativiFunction() {
    super(3, new Class[] { PageContext.class,String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String entita = (String) params[1];
    String chiave = (String) params[2];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String select="";

    Object[] param = new Object[1];
    if("GARATT".equals(entita)){
      select= "select CODGAR, NUMATT, TATTOT, DATTOT, NATTOT, DATRICT, NPROAT, DPROAA, NOTEAT "
        + "from GARATT "
        + "where GARATT.CODGAR = ? "
        + "order by GARATT.NUMATT asc";
      param[0] = chiave;
    }else if("MERICATT".equals(entita)){
      select="select IDRIC, ID, TATTO, DATTO, NATTO "
        + "from MERICATT "
        + "where MERICATT.IDRIC = ? "
        + "order by ID asc";
      param[0] = new Long(chiave);
    }

    try {
      List listaAttiAutorizzativi = sqlManager.getListVector(
          select, param);

      if (listaAttiAutorizzativi != null && listaAttiAutorizzativi.size() > 0)
        pageContext.setAttribute("attiAutorizzativi", listaAttiAutorizzativi,
            PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre gli atti autorizzativi "
          + "della tabella " + entita + " con chiave "
          + chiave, e);
    }

    return null;
  }

}
