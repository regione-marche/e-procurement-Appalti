/*
 * Created on 04-05-201
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per l'inizializzazione della pagina inserimentoOfferteDitteEscluse-lista.jsp
 *
 * @author Marcello Caminiti
 */
public class GestioneInserisciOfferteDitteEscluseFunction extends AbstractFunzioneTag {

  public GestioneInserisciOfferteDitteEscluseFunction() {
    super(3, new Class[] { PageContext.class,String.class ,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        pageContext, PgManager.class);

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
        "tabellatiManager", pageContext, TabellatiManager.class);

    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        pageContext, PgManagerEst1.class);

    String chiave = (String) params[1];
    HashMap key = UtilityTags.stringParamsToHashMap(chiave, null);
    //String codiceGara = ((JdbcParametro) key.get("DITG.CODGAR5")).getStringValue();
    String ngara = ((JdbcParametro) key.get("DITG.NGARA5")).getStringValue();


    String isGaraLottiConOffertaUnica = (String) params[2];

    String updateLista = UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA);
    if (updateLista == null || updateLista.length() == 0) updateLista = "0";
    pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA, updateLista,
        PageContext.REQUEST_SCOPE);

    String numeroCifreDecimaliRibasso;
    try {
      String codiceTornata = (String)sqlManager.getObject("select codgar1 from gare where ngara=?", new Object[]{ngara});
      numeroCifreDecimaliRibasso = pgManagerEst1.getNumeroDecimaliRibasso(codiceTornata);
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura del numero di decimali da usare per il ribasso della gara(NGARA = '" + ngara + ")", e);
    }
    pageContext.setAttribute("numeroCifreDecimaliRibasso",numeroCifreDecimaliRibasso);

    pageContext.setAttribute("numeroCifreDecimaliPunteggioTecnico",
        tabellatiManager.getDescrTabellato("A1049", "1"));

    if ( !"true".equals(isGaraLottiConOffertaUnica)) {
        pgManager.setDatiCalcoloRibasso(pageContext, sqlManager, ngara);
        pgManager.getOFFAUM(pageContext, sqlManager, ngara);
        pgManager.setDatiCalcoloImportoOfferto(pageContext,ngara);

        //Lettura dei punteggi economici e tecnici della gara
        try{
        Double maxPunTecnico = pgManager.getSommaPunteggioTecnico(ngara);

        if (maxPunTecnico == null)
          maxPunTecnico = new Double(-1000);

        if (maxPunTecnico != null)
          pageContext.setAttribute("punteggioTecnico",
              UtilityNumeri.convertiDouble(maxPunTecnico,
                  UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
              PageContext.REQUEST_SCOPE);


        Double maxPunEconomico = pgManager.getSommaPunteggioEconomico(ngara);

        if (maxPunEconomico == null)
          maxPunEconomico = new Double(-1000);

        if (maxPunEconomico != null)
          pageContext.setAttribute("punteggioEconomico",
              UtilityNumeri.convertiDouble(maxPunEconomico,
                  UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
                      PageContext.REQUEST_SCOPE);

        //Lettura delle soglie minime
        Double sogliaTecnicaMinima = (Double)sqlManager.getObject("select mintec from gare1 where ngara=?", new Object[]{ngara});
        if (sogliaTecnicaMinima != null)
          pageContext.setAttribute("sogliaTecnicaMinima",
              UtilityNumeri.convertiDouble(sogliaTecnicaMinima,
                  UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
              PageContext.REQUEST_SCOPE);


        Double sogliaEconomicaMinima = (Double)sqlManager.getObject("select mineco from gare1 where ngara=?", new Object[]{ngara});
        if (sogliaEconomicaMinima != null)
          pageContext.setAttribute("sogliaEconomicaMinima",
              UtilityNumeri.convertiDouble(sogliaEconomicaMinima,
                  UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
              PageContext.REQUEST_SCOPE);


       }catch (SQLException e) {
         throw new JspException("Errore durante il calcolo della somma dei " +
             "punteggi tecnici ed economici della gara (NGARA = '" + ngara + ")",
             e);
     }
    }

    return null;
  }


}