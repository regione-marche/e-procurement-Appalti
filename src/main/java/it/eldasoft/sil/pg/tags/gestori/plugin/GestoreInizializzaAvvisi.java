/*
 * Created on 23-05-2012
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
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore di plugin per l'inizializzazione degli Avvisi
 *
 * @author Marcello Caminiti
 */
public class GestoreInizializzaAvvisi extends AbstractGestorePreload {

  public GestoreInizializzaAvvisi(BodyTagSupportGene tag) {
    super(tag);
  }

  /**
   * Vengono caricate le informazioni chiave per il filtro su g_permessi
   */
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);


    String modo = (String) page.getAttribute(
        UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO, PageContext.REQUEST_SCOPE);

    try {
      String ngara = null;
      if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modo)) {
        // carico la chiave dell'occorrenza
        HashMap key = UtilityTags.stringParamsToHashMap(
            (String) page.getAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA,
                PageContext.REQUEST_SCOPE), null);
        ngara = ((JdbcParametro) key.get("GAREAVVISI.NGARA")).getStringValue();

        String codgar = (String) sqlManager.getObject(
            "select codgar from gareavvisi where gareavvisi.ngara = ?",
            new Object[] { ngara });

        // creo il parametro con la chiave da passare alla pagina di controllo
        // delle autorizzazioni
        String inputFiltro = "CODGAR=T:" + codgar;
        page.setAttribute("inputFiltro", inputFiltro, PageContext.REQUEST_SCOPE);
      }
      String log = (String) page.getAttribute("log");
      if("true".equals(log)){
        try{
          LogEvento logEvento = LogEventiUtils.createLogEvento((HttpServletRequest) page.getRequest());
          logEvento.setLivEvento(1);
          logEvento.setOggEvento(ngara);
          logEvento.setCodEvento("GA_ACCESSO_PROCEDURA");
          logEvento.setDescr("Accesso al dettaglio dell'avviso");
          logEvento.setErrmsg("");
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          String messageKey = "errors.logEventi.inaspettataException";
        }
      }

    } catch (SQLException e) {
      throw new JspException(
          "Errore in fase di esecuzione delle select di inizializzazione", e);

    }
    
    
  }


  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
    // TODO Auto-generated method stub

  }
}
