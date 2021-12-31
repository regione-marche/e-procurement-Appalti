/*
 * Created on 16-lug-2008
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
 * Gestore di plugin per l'inizializzazione di Elenchi Operatori Economici
 *
 * @author Cristian.Febas
 */
public class GestoreInizializzaElenco extends AbstractGestorePreload {

  public GestoreInizializzaElenco(BodyTagSupportGene tag) {
    super(tag);
  }

  /**
   * Vengono caricate le informazioni chiave per il filtro su g_permessi
   */
  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);


    String modo = (String) page.getAttribute(
        UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO, PageContext.REQUEST_SCOPE);

    try {
      
      HashMap key = UtilityTags.stringParamsToHashMap(
          (String) page.getAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA,
              PageContext.REQUEST_SCOPE), null);
      String ngara = null;
      if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modo)) {
        ngara = ((JdbcParametro) key.get("GARE.NGARA")).getStringValue();
        // carico la chiave dell'occorrenza
        String codgar = (String) sqlManager.getObject(
            "select codgar1 from gare where gare.ngara = ?",
            new Object[] { ngara });

        // creo il parametro con la chiave da passare alla pagina di controllo
        // delle autorizzazioni
        String inputFiltro = "CODGAR=T:" + codgar;
        page.setAttribute("inputFiltro", inputFiltro, PageContext.REQUEST_SCOPE);

        Long conteggioDitte = (Long)sqlManager.getObject(
            "select count(*) from ditg where codgar5 = ? and ngara5 = ?",
            new Object[] { codgar,ngara });
        if(conteggioDitte!=null && conteggioDitte.longValue()>0)
          page.setAttribute("bloccoTipologia", new Boolean(true), PageContext.REQUEST_SCOPE);

      }
      
      String log = (String) page.getAttribute("log");
      String genere = (String) page.getAttribute("genere");
      String descrizione = "Accesso al dettaglio della procedura";
      if("10".equals(genere)){
        descrizione = "Accesso al dettaglio dell'elenco";
      }else{
        if("20".equals(genere)){
          descrizione = "Accesso al dettaglio del catalogo";
        }
      }
      if("true".equals(log) && ngara != null){
        try{
          LogEvento logEvento = LogEventiUtils.createLogEvento((HttpServletRequest) page.getRequest());
          logEvento.setLivEvento(1);
          logEvento.setOggEvento(ngara);
          logEvento.setCodEvento("GA_ACCESSO_PROCEDURA");
          logEvento.setDescr(descrizione);
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


  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
    // TODO Auto-generated method stub

  }
}
