/*
 * Created on 22/gen/2019
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore che effettua i controlli preliminari e popola la popup per
 * la rettifica dei termini di gara
 *
 * @author Marcello Caminiti
 */
public class GestorePopUpPubblicaSuPortale extends AbstractGestorePreload {

  private SqlManager sqlManager = null;
  private GeneManager geneManager = null;

  public GestorePopUpPubblicaSuPortale(BodyTagSupportGene tag) {
    super(tag);
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }

  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    String queryGare = "select t.iterga, t.gartel, g.genere from torn t left outer join gare g on t.codgar = g.codgar1 where t.codgar = ?";
    String queryPubbli = "select tippub from pubbli where codgar9 = ? and ( tippub = 11 or tippub = 13 or tippub = 15 or tippub = 23)";
    String queryPubg = "select tippubg from pubg, gare where gare.codgar1 = ? and pubg.ngara = gare.ngara and (pubg.tippubg = 12 or pubg.tippubg = 14)";
    String queryWhereDelibere = "select cl_where_vis, cl_where_ult from g1cf_pubb where gruppo = ?";
    String queryMeruolo = "select meruolo from g_permessi g where g.codgar = ? and g.syscon = ?";

    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);

    geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
        page, GeneManager.class);

    // lettura dei parametri di input
    String codgar = page.getRequest().getParameter("codiceGara");
    String ngara = page.getRequest().getParameter("ngara");

    ProfiloUtente profilo = (ProfiloUtente) page.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long syscon = new Long(profilo.getId());

    try {



      List listaDatiGara= this.sqlManager.getListVector(queryGare, new Object[]{codgar});

      Long iterga = new Long(0);
      String gartel = "";
      Long genere = new Long(0);
      Long tippubg = new Long(0);
      Long tippub = new Long(0);
      boolean trovatoTippubg12 = false;
      boolean trovatoTippubg14 = false;
      boolean trovatoPubbli11 = false;
      boolean trovatoPubbli13 = false;
      if (listaDatiGara != null && listaDatiGara.size() > 0) {
        for (int i = 0; i < listaDatiGara.size(); i++) {
          Vector datiGara = (Vector) listaDatiGara.get(i);
          iterga = (Long) ((JdbcParametro) datiGara.get(0)).getValue();
          gartel = (String) ((JdbcParametro) datiGara.get(1)).getValue();
          genere = (Long) ((JdbcParametro) datiGara.get(2)).getValue();
        }
      }
      if("1".equals(gartel)){
        Long meruolo = (Long) this.sqlManager.getObject(queryMeruolo, new Object[]{codgar,syscon});
        if(!new Long(1).equals(meruolo)){
          page.setAttribute("error",
              "true", PageContext.REQUEST_SCOPE);
          page.setAttribute("errorMsg",
              "Non è possibile procedere alla pubblicazione su portale Appalti in quanto non si ha il ruolo di punto ordinante per la gara", PageContext.REQUEST_SCOPE);
          return;
        }
      }

      boolean trovatoGruppoDelibere= false;
      List WhereDelibere = this.sqlManager.getListVector(queryWhereDelibere, new Object[]{new Long(15)});
      if (WhereDelibere != null && WhereDelibere.size() > 0) {
        for (int i = 0; i < WhereDelibere.size() && !trovatoGruppoDelibere; i++) {
          Vector datiPubg = (Vector) WhereDelibere.get(i);
          String where = (String) ((JdbcParametro) datiPubg.get(0)).getValue();
          String whereUlt = (String) ((JdbcParametro) datiPubg.get(1)).getValue();
          Long delibere = new Long(0);
          String query = "select count(*) from TORN left outer join GARE on TORN.CODGAR=GARE.CODGAR1 where TORN.CODGAR=?";
          if (where != null && !where.equals("")) {
            where = " and (" + where + ")";
          }
          if (whereUlt != null && !whereUlt.equals("")) {
            where = where + " and (" + whereUlt + ")";
          }
          Long countDelibere = (Long) this.sqlManager.getObject(query + where, new Object[]{codgar});
          if(countDelibere > 0){
            trovatoGruppoDelibere = true;
          }
        }
      }

      List listaDatiPubg= this.sqlManager.getListVector(queryPubg, new Object[]{codgar});

      List listaDatiPubbli= this.sqlManager.getListVector(queryPubbli, new Object[]{codgar});

      if (listaDatiPubg != null && listaDatiPubg.size() > 0) {
        for (int i = 0; i < listaDatiPubg.size(); i++) {
          Vector datiPubg = (Vector) listaDatiPubg.get(i);
          tippubg = (Long) ((JdbcParametro) datiPubg.get(0)).getValue();
          if(tippubg == 12){trovatoTippubg12 = true;}
          if(tippubg == 14){trovatoTippubg14 = true;}
        }
      }

      if (listaDatiPubbli != null && listaDatiPubbli.size() > 0) {
        for (int i = 0; i < listaDatiPubbli.size(); i++) {
          Vector datiPubbli = (Vector) listaDatiPubbli.get(i);
          tippub = (Long) ((JdbcParametro) datiPubbli.get(0)).getValue();
          if(tippub == 11){trovatoPubbli11 = true;}
          if(tippub == 13 || tippub==23){trovatoPubbli13 = true;}
        }
      }

      if(trovatoGruppoDelibere){
        page.setAttribute("deliberaVisibile",
            "true", PageContext.REQUEST_SCOPE);
      }
      if(trovatoTippubg12 || listaDatiPubbli.size() > 0){
        page.setAttribute("deliberaDisabilitata",
            "true", PageContext.REQUEST_SCOPE);
      }
      if((new Long(10).equals(genere) || new Long(11).equals(genere) || new Long(20).equals(genere)) || new Long(1).equals(iterga) || new Long(2).equals(iterga) || new Long(4).equals(iterga) ){
        page.setAttribute("bandoVisibile",
            "true", PageContext.REQUEST_SCOPE);
      }
      if(trovatoPubbli11){
        page.setAttribute("bandoDisabilitato",
            "true", PageContext.REQUEST_SCOPE);
      }
      if(!new Long(1).equals(iterga) && !"1".equals(gartel) && !new Long(10).equals(genere) && !new Long(11).equals(genere) && !new Long(20).equals(genere)){
        page.setAttribute("invitoVisibile",
            "true", PageContext.REQUEST_SCOPE);
      }
      if(trovatoPubbli13 || (!trovatoPubbli11 && (new Long(2).equals(iterga) || new Long(4).equals(iterga)))){
        page.setAttribute("invitoDisabilitato",
            "true", PageContext.REQUEST_SCOPE);
      }
      if(!new Long(1).equals(iterga) && "1".equals(gartel) && !new Long(10).equals(genere) && !new Long(11).equals(genere) && !new Long(20).equals(genere)){
        page.setAttribute("invitoComunicazioneVisibile",
            "true", PageContext.REQUEST_SCOPE);
      }
      if("1".equals(gartel) && (trovatoPubbli13 || (!trovatoPubbli11 && (new Long(2).equals(iterga) || new Long(4).equals(iterga))))){
        page.setAttribute("invitoComunicazioneDisabilitato",
            "true", PageContext.REQUEST_SCOPE);
      }
      if(!new Long(10).equals(genere) && !new Long(11).equals(genere) && !new Long(20).equals(genere)){
        page.setAttribute("esitoVisibile",
            "true", PageContext.REQUEST_SCOPE);
      }
      if(trovatoTippubg12){
        page.setAttribute("esitoDisabilitato",
            "true", PageContext.REQUEST_SCOPE);
      }
      if(!trovatoTippubg12 && !trovatoTippubg14 && (listaDatiPubbli == null || listaDatiPubbli.size() <= 0)){
        page.setAttribute("integrazioneDisabilitata",
            "true", PageContext.REQUEST_SCOPE);
      }
      if(GeneManager.checkOP(page.getServletContext(), "OP129") && !new Long(10).equals(genere) && !new Long(11).equals(genere) && !new Long(20).equals(genere)){
        page.setAttribute("trasparenzaVisibile",
            "true", PageContext.REQUEST_SCOPE);
      }
      if(trovatoTippubg14){
        page.setAttribute("trasparenzaDisabilitato",
            "true", PageContext.REQUEST_SCOPE);
      }


      Long genereGara = (Long) this.sqlManager.getObject("select genere from gare where codgar1 = ?", new Object[]{codgar});
      if(new Long(11).equals(genereGara)){
        page.setAttribute("genere","dell'avviso", PageContext.REQUEST_SCOPE);}
      if(new Long(20).equals(genereGara)){
        page.setAttribute("genere","del catalogo elettronico", PageContext.REQUEST_SCOPE);}
      if(new Long(10).equals(genereGara)){
        page.setAttribute("genere","dell'elenco operatori", PageContext.REQUEST_SCOPE);}
      if(!new Long(11).equals(genereGara) && !new Long(20).equals(genereGara) && !new Long(10).equals(genereGara)){
        page.setAttribute("genere","della procedura di gara", PageContext.REQUEST_SCOPE);
      }

    }catch (SQLException e) {
      throw new JspException(
          "Errore in fase di controllo dei dati di gara per la pubblicazione", e);
      }

  }
}