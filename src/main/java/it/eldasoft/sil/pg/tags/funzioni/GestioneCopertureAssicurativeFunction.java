/*
 * Created on 20/02/12
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
 * Funzione per inizializzare le sezioni delle coperture assicurative di una gara a
 * lotto unico o di un lotto di gara in fase di modifica
 * 
 * @author Marcello Caminiti
 */
public class GestioneCopertureAssicurativeFunction extends AbstractFunzioneTag {

  public GestioneCopertureAssicurativeFunction() {
    super(1, new Class[] { String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String nGara = (String) params[0];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      List listaCopertureAssicurative = sqlManager.getListVector(
          "select NGARA, NUMASS, TIPASS, IMPASS, NOTASS "
              + "from GARASS "
              + "where NGARA = ? "
              + "order by NUMASS asc", new Object[] { nGara });
      
      if (listaCopertureAssicurative != null && listaCopertureAssicurative.size() > 0)
        pageContext.setAttribute("copertureAssicurative", listaCopertureAssicurative,
            PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre le coperture assicurative "
          + "della gara "
          + nGara, e);
    }

    return null;
  }

}
