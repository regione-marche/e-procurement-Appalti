/*
 * Created on 05/09/13
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

/**
 * Funzione per inizializzare le sezioni dei partecipanti al raggruppamento
 * per la pagina anticorditte-scheda.jp
 *
 * @author Marcello Caminiti
 */
public class GestioneAnticorditteFunction extends AbstractFunzioneTag {

  public GestioneAnticorditteFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String id = StringUtils.stripToNull((String)params[1]);
    if(id!=null){
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);
      try {
        @SuppressWarnings("unchecked")
        List<JdbcParametro> listaPartecipanti = sqlManager.getListVector(
            "select ID, IDANTICORPARTECIP, RAGSOC, CODFISC, IDFISCEST, RUOLO " +
              "from ANTICORDITTE where IDANTICORPARTECIP = ?",
              new Object[]{new Long(id)});

        if (listaPartecipanti != null && listaPartecipanti.size() > 0)
          pageContext.setAttribute("partecipanti", listaPartecipanti,
              PageContext.REQUEST_SCOPE);
      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre i partecipanti " +
              "del raggruppamento " + id, e);
      }
    }
    return null;
  }

}