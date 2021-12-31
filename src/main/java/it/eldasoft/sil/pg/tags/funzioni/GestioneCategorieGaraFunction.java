/*
 * Created on 30/10/08
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
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per inizializzare le sezioni delle categorie d'iscrizione di una gara
 * a lotto unico o di un lotto di gara in fase di modifica
 * In particolare:
 * - vengono caricate le ulteriori categorie dell'appalto (occorrenze in OPES)
 * - vengono caricati dei tabellati necessari alle funzioni javascript delle
 *   ulteriori categorie dell'appalto
 *
 * @author Luca.Giacomazzo
 */
public class GestioneCategorieGaraFunction extends AbstractFunzioneTag {

  public GestioneCategorieGaraFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String nGara = (String) params[0];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        pageContext, TabellatiManager.class);

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        pageContext, PgManager.class);

    try{
      List listaCategorieUlteriori = sqlManager.getListVector(
          "select OPES.CATOFF, CAIS.DESCAT, CAIS.ACONTEC, CAIS.QUAOBB, CAIS.TIPLAVG, " +
                 "OPES.NGARA3, OPES.NOPEGA, OPES.IMPAPO,  OPES.NUMCLU, OPES.ISCOFF, " +
                 "OPES.ACONTEC, OPES.QUAOBB, OPES.DESCOP, V_CAIS_TIT.ISFOGLIA " +
            "from OPES, CAIS, V_CAIS_TIT " +
           "where OPES.NGARA3 = ? " +
             "and OPES.CATOFF = CAIS.CAISIM " +
             "and CAIS.CAISIM = V_CAIS_TIT.CAISIM  " +
           "order by OPES.NOPEGA asc", new Object[]{nGara});

      if(listaCategorieUlteriori != null && listaCategorieUlteriori.size() > 0)
        pageContext.setAttribute("ulterioriCategorie", listaCategorieUlteriori,
            PageContext.REQUEST_SCOPE);
    }  catch(SQLException s){
      throw new JspException("Errore nell'estrarre le ulteriori categorie " +
            "della gara " + nGara, s);
    }

    pageContext.setAttribute("importiIscrizioneLavori",
        tabellatiManager.getTabellato("G_z09"), PageContext.REQUEST_SCOPE);

    List listaImporti =tabellatiManager.getTabellato("G_z07");
    pgManager.aggiornaListaImporti(listaImporti);
    pageContext.setAttribute("importiIscrizioneForniture",
        listaImporti, PageContext.REQUEST_SCOPE);

    listaImporti = tabellatiManager.getTabellato("G_z08");
    pgManager.aggiornaListaImporti(listaImporti);
    pageContext.setAttribute("importiIscrizioneServizi",
        listaImporti, PageContext.REQUEST_SCOPE);

    listaImporti = tabellatiManager.getTabellato("G_z11");
    pgManager.aggiornaListaImporti(listaImporti);
    pageContext.setAttribute("importiIscrizioneLavori150",
        listaImporti, PageContext.REQUEST_SCOPE);

    listaImporti = tabellatiManager.getTabellato("G_z12");
    pgManager.aggiornaListaImporti(listaImporti);
    pageContext.setAttribute("importiIscrizioneServiziProfessionali",
        listaImporti, PageContext.REQUEST_SCOPE);

    return null;
  }

}