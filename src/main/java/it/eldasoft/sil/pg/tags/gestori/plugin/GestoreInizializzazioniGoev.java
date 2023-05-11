/*
 * Created on 04-giu-2009
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore preload per la pagina scheda goev
 * 
 * @author Marcello.Caminiti
 */
public class GestoreInizializzazioniGoev extends AbstractGestorePreload {

  public GestoreInizializzazioniGoev(BodyTagSupportGene tag) {
    super(tag);
  }

  // Viene calcolato il massimo di norpar per la gara da presentare
  // all'apertura della pagina
  public void doBeforeBodyProcessing(PageContext pageContext,
      String modoAperturaScheda) throws JspException {

    if ("NUOVO".equals(modoAperturaScheda)) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);
      String ngara = "";
      HashMap key = UtilityTags.stringParamsToHashMap(
          (String) pageContext.getAttribute(
              UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, PageContext.REQUEST_SCOPE),
          null);
      if (key.get("GOEV.NGARA") != null)
        ngara = ((JdbcParametro) key.get("GOEV.NGARA")).getStringValue();
      else {
        HashMap keyParent = UtilityTags.stringParamsToHashMap(
            (String) pageContext.getAttribute(
                UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT,
                PageContext.REQUEST_SCOPE), null);
        ngara = ((JdbcParametro) keyParent.get("GARE.NGARA")).getStringValue();
      }
      
      //Per problemi con Oracle per determinare il max di norpar, non si
      //adoperare max(norpar), poichè restituisce un long anche se norpar
      //è double
      String select = "select distinct norpar from goev where ngara = ? order by norpar desc";

      try {
        Double norpar = (Double) sqlManager.getObject(select,
            new Object[] { ngara });
        double newNorpar = 1;
        if (norpar != null){
          newNorpar = Math.ceil(norpar.doubleValue());
          if(norpar.doubleValue() == newNorpar)
            newNorpar += 1;
        }
        
        pageContext.setAttribute("newNorpar",
            UtilityNumeri.convertiDouble(new Double(newNorpar),
                UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
                    PageContext.REQUEST_SCOPE);
      } catch (SQLException e) {
        throw new JspException(
            "Errore durante il calcolo del numero d'ordine del criterio", e);
      }
    }

  }

  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }

}