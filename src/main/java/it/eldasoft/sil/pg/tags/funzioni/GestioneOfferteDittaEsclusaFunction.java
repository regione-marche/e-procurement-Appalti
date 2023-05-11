/*
 * Created on 05/05/11
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Inizializzazione della pagina dettaglioOfferteDittaEsclusa-OffertaUnicaLotti.jsp
 *
 * Questa classe si ispira alla classe GestioneOfferteTecnicheEconomicheDittaFunction
 *
 * @author Marcello Caminiti
 */
public class GestioneOfferteDittaEsclusaFunction extends AbstractFunzioneTag {

	public GestioneOfferteDittaEsclusaFunction(){
	  super(2, new Class[] { PageContext.class,String.class});
	}

	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
        "tabellatiManager", pageContext, TabellatiManager.class);
    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        pageContext, PgManager.class);
    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        pageContext, PgManagerEst1.class);

    String chiave = (String) params[1];
    HashMap key = UtilityTags.stringParamsToHashMap(chiave, null);
    String codiceGara = ((JdbcParametro) key.get("DITG.CODGAR5")).getStringValue();

    Long modalitaAggiudicazioneGara = null;
    // Conteggio del numero di lotti, di una gara divisa in lotti con
    // offerta unica, di tipo 'OEPV' (GARE.MODLICG = 6).
    Long numeroLottiOEPV = new Long(0);

    try {
      Vector datiLotto = sqlManager.getVector(
          "select CODGAR1, FASGAR, MODLICG, MODASTG, DITTAP from GARE " +
           "where GARE.NGARA = ?", new Object[]{codiceGara});
      if (datiLotto != null && datiLotto.size() > 0) {

	      if (((JdbcParametro) datiLotto.get(2)).getValue() != null)
	        modalitaAggiudicazioneGara = (Long) ((JdbcParametro)
	        		datiLotto.get(2)).getValue();


      	// Conteggio del numero di lotti di una gara divisa in lotti con
      	// offerta unica di tipo Miglior offerta prezzi e Offerta
      	// economicamente più vantaggiosa (GARE.MODLIGC in (5, 14, 16, 6))
      	Long numeroLottiMigliorOffertaPrezzi = new Long(0);

      	Long numeroLotti =  (Long) sqlManager.getObject(
      			"select count(*) from gare " +
     			 "where codgar1 = ? " +
     			   "and genere is null ", new Object[]{codiceGara});


	      	if(numeroLotti != null && numeroLotti.longValue() > 0){
	        	numeroLottiOEPV = (Long) sqlManager.getObject(
	        			"select count(*) from gare " +
	        			 "where codgar1 = ? " +
	        			   "and genere is null " +
	        			   "and modlicg = 6", new Object[]{codiceGara});
	        	// Se almeno un lotto di tale gara e' di tipo OEPV, allora la fase
	        	// FASE_APERTURA_OFFERTE_TECNICHE deve essere visibile per tutti i
	        	// lotti e quindi l'oggetto modalitaAggiudicazioneGara viene posto
	        	// comunque pari a new Long(6)
	        	if(numeroLottiOEPV != null && numeroLottiOEPV.longValue() > 0)
	        		modalitaAggiudicazioneGara = new Long(6);
	        	else
	        		numeroLottiOEPV = new Long(0);

	        	numeroLottiMigliorOffertaPrezzi = (Long) sqlManager.getObject(
	        			"select count(*) from gare " +
	        			 "where codgar1 = ? " +
	        			   "and genere is null " +
	        			   "and modlicg in (5, 14, 16)", new Object[]{codiceGara});
	        	if(numeroLottiMigliorOffertaPrezzi == null)
	        		numeroLottiMigliorOffertaPrezzi = new Long(0);
	      	}


	      pageContext.setAttribute("numeroLottiMigliorOffertaPrezzi",
	      		numeroLottiMigliorOffertaPrezzi, PageContext.REQUEST_SCOPE);
	      pageContext.setAttribute("numeroLottiOEPV",	numeroLottiOEPV,
	      		PageContext.REQUEST_SCOPE);
	      pageContext.setAttribute("numeroLotti",	numeroLotti,
	      		PageContext.REQUEST_SCOPE);

	      if(modalitaAggiudicazioneGara != null)
	      	pageContext.setAttribute("modalitaAggiudicazioneGara",
            modalitaAggiudicazioneGara, PageContext.REQUEST_SCOPE);




      }
    } catch (SQLException s) {
      throw new JspException(
          "Errore durante la lettura di dati supplementari del lotto", s);
    }

    String numeroGara  = ((JdbcParametro) key.get("DITG.NGARA5")).getStringValue();
    String numeroCifreDecimaliRibasso;
    try {
      numeroCifreDecimaliRibasso = pgManagerEst1.getNumeroDecimaliRibasso(codiceGara);
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura del numero di decimali da usare per il ribasso della gara(NGARA = '" + numeroGara + ")", e);
    }
    pageContext.setAttribute("numeroCifreDecimaliRibasso",numeroCifreDecimaliRibasso);

      pageContext.setAttribute("numeroCifreDecimaliPunteggioTecnico",
          tabellatiManager.getDescrTabellato("A1049", "1"));


      if(numeroLottiOEPV!=null && numeroLottiOEPV.longValue()>0){
        //Gestione punteggi tecnici ed economici dei singoli lotti
        String codiceDitta = ((JdbcParametro) key.get("DITG.DITTAO")).getStringValue();

        String select="select ngara5 from ditg where codgar5 = ? and ngara5 <> ? and dittao = ? and ammgar='2' and (partgar = '1' or partgar is null)";
        select+=" order by ngara5";
        try {
          List listaDitte = sqlManager.getListVector(select, new Object[]{codiceGara,numeroGara,codiceDitta});
          if(listaDitte!= null && listaDitte.size()>0){
            Double punteggioTecnicoMassimo = null;
            Double punteggioEconomicoMassimo = null;
            List listaPunteggiTecnici = new ArrayList();
            List listaPunteggiEconomici = new ArrayList();
            List listaSoglieTecniche = new ArrayList();
            List listaSoglieEconomiche = new ArrayList();
            for (int i = 0; i < listaDitte.size(); i++) {
              Vector ditta = (Vector) listaDitte.get(i);
              String numeroGaraDitta = ((JdbcParametro) ditta.get(0)).getStringValue();
              punteggioTecnicoMassimo = pgManager.getSommaPunteggioTecnico(numeroGaraDitta);
              punteggioEconomicoMassimo = pgManager.getSommaPunteggioEconomico(numeroGaraDitta);
              Double sogliaTecnica = null;
              Double sogliaEconomica = null;

              sogliaTecnica = (Double)sqlManager.getObject("select mintec from gare1 where ngara=?", new Object[]{numeroGaraDitta});
              sogliaEconomica = (Double)sqlManager.getObject("select mineco from gare1 where ngara=?", new Object[]{numeroGaraDitta});

              if(punteggioTecnicoMassimo==null)
                listaPunteggiTecnici.add(new Double(-1000));
              else
                listaPunteggiTecnici.add(UtilityNumeri.convertiDouble(punteggioTecnicoMassimo,
                  UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3));
              if(punteggioEconomicoMassimo == null)
                listaPunteggiEconomici.add(new Double(-1000));
              else
                listaPunteggiEconomici.add(UtilityNumeri.convertiDouble(punteggioEconomicoMassimo,
                  UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3));

              if(sogliaTecnica!= null)
                listaSoglieTecniche.add(UtilityNumeri.convertiDouble(sogliaTecnica,
                    UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3));
              else
                listaSoglieTecniche.add(sogliaTecnica);


              if(sogliaEconomica!= null)
                listaSoglieEconomiche.add(UtilityNumeri.convertiDouble(sogliaEconomica,
                    UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3));
              else
                listaSoglieEconomiche.add(sogliaEconomica);

            }
            pageContext.setAttribute("listaPunteggiTecnici", listaPunteggiTecnici,
                PageContext.REQUEST_SCOPE);
            pageContext.setAttribute("listaPunteggiEconomici", listaPunteggiEconomici,
                PageContext.REQUEST_SCOPE);

            pageContext.setAttribute("listaSoglieTecniche", listaSoglieTecniche,
                PageContext.REQUEST_SCOPE);

            pageContext.setAttribute("listaSoglieEconomiche", listaSoglieEconomiche,
                PageContext.REQUEST_SCOPE);
          }


        } catch (SQLException e) {
          throw new JspException("Errore durante il calcolo dei punteggi tecnici di tutti i lotti)",
              e);
        }
      }

    String updateLista = UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA);
    if (updateLista == null || updateLista.length() == 0) updateLista = "0";
    pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA,
        updateLista, PageContext.REQUEST_SCOPE);

    // Creazione del parametro con la chiave da passare alla pagina di
    // controllo delle autorizzazioni
    String inputFiltro = "CODGAR=T:".concat(codiceGara);
    pageContext.setAttribute("inputFiltro", inputFiltro,
        PageContext.REQUEST_SCOPE);

    //Lettura del campo OFFAUM.TORN
    pgManager.getOFFAUM(pageContext, sqlManager, codiceGara);

    return null;
	}

}