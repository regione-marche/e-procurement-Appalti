/*
 * Created on 19/dic/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore che estrae alcune informazioni da visualizzare come riepilogo nella
 * popup di conferma per l'inserimento delle pubblicazioni
 * 
 * @author Stefano.Sabbadin
 */
public class GestoreInsDatiPubblicazioni extends AbstractGestorePreload {

  public GestoreInsDatiPubblicazioni(BodyTagSupportGene tag) {
    super(tag);
  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload#doAfterFetch(javax.servlet.jsp.PageContext,
   *      java.lang.String)
   */
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload#doBeforeBodyProcessing(javax.servlet.jsp.PageContext,
   *      java.lang.String)
   */
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {
    GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
        page, GeneManager.class);
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
        "tabellatiManager", page, TabellatiManager.class);

    // lettura dei parametri di input
    String codgar = page.getRequest().getParameter("codgar");
    String ngara = page.getRequest().getParameter("ngara");
    String genere = page.getRequest().getParameter("genere");
    Integer bando = Integer.valueOf(page.getRequest().getParameter("bando"));

    if("1".equals(genere)){
      try {
        String primoLotto = (String) sqlManager.getObject("select ngara from gare,torn where torn.codgar = ? and torn.codgar = gare.codgar1 order by ngara", new Object[]{codgar});
        page.setAttribute("ngara", primoLotto,PageContext.REQUEST_SCOPE);
        
        if(primoLotto == null || "".equals(primoLotto)){
          page.setAttribute("errors", "true",PageContext.REQUEST_SCOPE);
          return;
        }
        
      } catch (SQLException e) {
        throw new JspException(
            "Errore durante la lettura dei lotti di gara", e);
      }
    }else{
      if("3".equals(genere)){
        page.setAttribute("ngara", ngara,PageContext.REQUEST_SCOPE);
        ngara = null;
      }
    }
    
    // NB: IL CODICE SOTTOSTANTE E' RICONDUCIBILE, A MENO DEI CAMPI ESTRATTI
    // NELLA SELECT, DI QUANTO IMPLEMENTATO IN
    // GestoreInsertPubblicazioniPredefinite; DI CONSEGUENZA UNA EVENTUALE SUA
    // MODIFICA IMPLICA UNA RIPETIZIONE DELLA STESSA ANCHE NELL'ALTRA CLASSE

    // predisposizione dati di input per la query da eseguire
    Integer filtroClassificazione = null;
    if (bando.intValue() == 1)
      filtroClassificazione = new Integer(1);
    else
      filtroClassificazione = new Integer(2);

    // costruzione dinamica della query
    String appendFrom = "";
    String campoTipgar = "torn.tipgar";
    String campoImporto = "torn.imptor";
    if (ngara != null && !"".equals(ngara)) {
      // siamo nel caso di gara a lotto unico o lotto di gara
      appendFrom = ", gare";
      campoTipgar = "gare.tipgarg";
      campoImporto = "gare.impapp";
    }

    // query per estrarre i dati da utilizzare nel filtro sulle definizioni
    // delle pubblicazioni
    StringBuffer sqlDatiGara = new StringBuffer("");
    sqlDatiGara.append("select torn.tipgen, ").append(campoTipgar).append(", ").append(
        campoImporto).append(" ");
    sqlDatiGara.append("from torn");
    sqlDatiGara.append(appendFrom);
    sqlDatiGara.append(" ");
    sqlDatiGara.append("where torn.codgar = ? ");
    if (ngara != null && !"".equals(ngara)) {
      sqlDatiGara.append("and torn.codgar = gare.codgar1 ");
      sqlDatiGara.append("and gare.ngara = ? ");
    }

    // definizione dei parametri (se ngara è valorizzato va passato un parametro
    // in più)
    Object[] params = null;
    if (ngara != null && !"".equals(ngara)) {
      params = new Object[] { codgar, ngara };
    } else {
      params = new Object[] { codgar };
    }

    String tipgen = null;
    Long tipgar = null;
    Double importoGara = null;

    // estrazione dei dati della gara
    try {
      Vector datiGara = sqlManager.getVector(sqlDatiGara.toString(), params);
      tipgen = SqlManager.getValueFromVectorParam(datiGara, 0).getStringValue();
      tipgar = SqlManager.getValueFromVectorParam(datiGara, 1).longValue();
      importoGara = SqlManager.getValueFromVectorParam(datiGara, 2).doubleValue();
      if (importoGara == null) importoGara = new Double(0);
    } catch (SQLException e) {
      throw new JspException("Errore durante l'estrazione dei dati della gara",
          e);
    } catch (GestoreException e) {
      throw new JspException(
          "Errore durante la lettura dell'importo della gara", e);
    }

    StringBuffer sqlEstrazioneCATPUB = new StringBuffer("");
    sqlEstrazioneCATPUB.append("select catpub.liminf, catpub.limsup, catpub.codtab ");
    sqlEstrazioneCATPUB.append("from catpub ");
    sqlEstrazioneCATPUB.append("where catpub.tiplav = ? ");
    // tipgar deve essere uguale al tipgar della tabella di partenza, oppure 0
    // oppure null
    sqlEstrazioneCATPUB.append("and (catpub.tipgar in (?, 0) or catpub.tipgar is null) ");
    // se l'importo è nullo allora si prende la fascia con limite inferiore 0,
    // altrimenti si prende quella che contiene l'importo
    // In particolare se il limite superiore non è previsto, il campo viene lasciato nullo anziché impostato a un valore molto grande. 
    // Analogamente per il limite inferiore, viene lasciato nullo invece che impostato a 0. 
    // Va adeguato di conseguenza il criterio di selezione in CATPUB.
    sqlEstrazioneCATPUB.append("and (liminf is null or liminf <= ?) and (? <= limsup or limsup is null) ");
    sqlEstrazioneCATPUB.append("and catpub.tipcla = ? ");
    
    // FINE PARTE SIMILE

    page.setAttribute("tipgen", tabellatiManager.getDescrTabellato("A1007",
        tipgen), PageContext.REQUEST_SCOPE);
    page.setAttribute("tipgar", tipgar != null
        ? tabellatiManager.getDescrTabellato("A2044", tipgar.toString())
        : "non definito", PageContext.REQUEST_SCOPE);
    page.setAttribute("impgara", UtilityNumeri.convertiImporto(importoGara, 2),
        PageContext.REQUEST_SCOPE);

    // estrazione dei dati da visualizzare nella pagina
    try {
      Vector rigaCATPUB = sqlManager.getVector(sqlEstrazioneCATPUB.toString(),
          new Object[] { tipgen, tipgar, importoGara, importoGara, 
              filtroClassificazione });
      Boolean occTrovate = new Boolean(false);
      if (rigaCATPUB != null && rigaCATPUB.size() > 0) {
        
        long countOccorrenze = geneManager.countOccorrenze(
            "TABPUB", "codtab = ?", new Object[]{
                SqlManager.getValueFromVectorParam(rigaCATPUB, 2).longValue()});
        
        if(countOccorrenze > 0){
          occTrovate = true;
          // solo se si estrae una occorrenza allora si popolano gli attributi
          // nel request
          Double limiteinf = SqlManager.getValueFromVectorParam(rigaCATPUB, 0).doubleValue();
          Double limitesup = SqlManager.getValueFromVectorParam(rigaCATPUB, 1).doubleValue();

          page.setAttribute("limiteinf",UtilityNumeri.convertiImporto(limiteinf,
              2), PageContext.REQUEST_SCOPE);
          page.setAttribute("limitesup", UtilityNumeri.convertiImporto(limitesup,
              2), PageContext.REQUEST_SCOPE);
        }
      }
      page.setAttribute("occTrovate", occTrovate, PageContext.REQUEST_SCOPE);
      if (ngara != null && !"".equals(ngara)) {
        page.setAttribute("ngara", ngara, PageContext.REQUEST_SCOPE);
      }
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante l'estrazione dei dati di filtro per le pubblicazioni bando ed esito",
          e);
    } catch (GestoreException e) {
      throw new JspException(
          "Errore durante la lettura del range di importi determinato per le pubblicazioni bando ed esito",
          e);
    }
  }

}