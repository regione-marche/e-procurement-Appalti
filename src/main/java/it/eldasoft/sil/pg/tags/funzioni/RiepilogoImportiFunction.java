/*
 * Created on 18/Set/09
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
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Vengono caricati gli importi per il riepilogo importo offerto complessivo
 * della pagina "Dettaglio offerta prezzi della ditta"
 *
 * @author Marcello Caminiti
 */
public class RiepilogoImportiFunction extends AbstractFunzioneTag {

	public RiepilogoImportiFunction() {
		super(4, new Class[] { PageContext.class, String.class, String.class,
				String.class });
	}

	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

		String codgar = (String) params[1];
		String ngara = (String) params[2];
		String dittao = (String) params[3];
		String ditta = "";
		String nomimo = null;
		
		Long ribcal = null;
		
		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
				pageContext, SqlManager.class);


		PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
            pageContext, PgManagerEst1.class);

		try {
			Double importoNoRibasso = null;
			Double importoSicurezza = null;
			String sicinc = null;
			String onsogrib="";
			Double onprge = null;
			boolean isGaraLottiConOffertaUnica = false;

			Vector datiGARE = sqlManager.getVector(
					"select impnrl, impsic, sicinc, ditta, genere, ribcal, fasgar, onprge, onsogrib " +
					"from gare where ngara=?", new Object[] { ngara });

			if (datiGARE != null && datiGARE.size() > 0) {
				importoNoRibasso = ((Double) ((JdbcParametro) datiGARE.get(0)).getValue());
				if (importoNoRibasso == null)
					importoNoRibasso = new Double(0);
				sicinc = ((String) ((JdbcParametro) datiGARE.get(2)).getValue());
				onsogrib = ((String) ((JdbcParametro) datiGARE.get(8)).getValue());
				onprge = ((Double) ((JdbcParametro) datiGARE.get(7)).getValue());
				if(!"1".equals(onsogrib) && onprge != null )
				  importoNoRibasso = new Double(importoNoRibasso.doubleValue() + onprge.doubleValue());

				if (!("2".equals(sicinc)) || sicinc == null) {
					importoSicurezza = ((Double) ((JdbcParametro) datiGARE.get(1)).getValue());
					if (importoSicurezza == null)
						importoSicurezza = new Double(0);
				}

				ditta = ((String) ((JdbcParametro) datiGARE.get(3)).getValue());
				if (datiGARE.get(4) != null) {
					Long tmp = (Long) ((JdbcParametro) datiGARE.get(4)).getValue();
					if (tmp != null && tmp.longValue() == 3)
						isGaraLottiConOffertaUnica = true;

					pageContext.setAttribute("isGaraLottiConOffertaUnica", ""
							+ isGaraLottiConOffertaUnica,	PageContext.REQUEST_SCOPE);
				}

				if (datiGARE.get(5) != null) {
					ribcal = (Long) ((JdbcParametro) datiGARE.get(5)).getValue();
					if (ribcal == null)
						ribcal = new Long(0);

				   if (isGaraLottiConOffertaUnica){
				     ribcal = new Long(1);
				     // Verifica se c'è almeno un lotto con criterio di aggiudicazione Offerta prezzi e ne recupera il RIBCAL.
				     // Il RIBCAL è impostato uguale per tutti i lotti della gara.
		                Long ribcalLotti = (Long) sqlManager.getObject(
	                        "select ribcal from gare where codgar1=? and " +
	                        "(genere <> 3 or genere is null) and modlicg in (5,14)",
	                        new Object[] { codgar });
    	                if (ribcalLotti != null && ribcalLotti.longValue() > 0) {
    	                  ribcal = new Long(ribcalLotti.longValue());
    	                }
				   }

				   pageContext.setAttribute("ribcal", ribcal, PageContext.REQUEST_SCOPE);
				}

				if (datiGARE.get(6) != null) {
					Long fasgar = (Long) ((JdbcParametro) datiGARE.get(6)).getValue();
					if (fasgar == null)
						fasgar = new Long(0);

					pageContext.setAttribute("faseGara", fasgar, PageContext.REQUEST_SCOPE);
				}

				pageContext.setAttribute("onsogrib", onsogrib, PageContext.REQUEST_SCOPE);

			}

			Vector datiDITG = sqlManager.getVector(
					"select impoff, nomimo, ribauo from ditg where ngara5=? and dittao=? and codgar5=?",
					new Object[]{ngara, dittao, codgar });

			Double importoEffettivo = null;
			Double ribasso = null;
			if (datiDITG != null && datiDITG.size() > 0) {
				importoEffettivo = (Double) ((JdbcParametro) datiDITG.get(0)).getValue();
				if (importoEffettivo == null)
					importoEffettivo = new Double(0);
				nomimo = (String) ((JdbcParametro) datiDITG.get(1)).getValue();
				ribasso = (Double) ((JdbcParametro) datiDITG.get(2)).getValue();
			}
			
			if(ribcal != null && ribcal.intValue() == 3){
			  pageContext.setAttribute("ribasso", ribasso, PageContext.REQUEST_SCOPE);
			}
			
			if (isGaraLottiConOffertaUnica == false) {
				double totImpoff = 0;
				List ret = sqlManager.getListVector(
						"select impoff from dpre where ngara= ? and dittao = ?",
						new Object[]{ ngara, dittao });
				if (ret != null && ret.size() > 0) {
					for (int i = 0; i < ret.size(); i++) {
						Double impoff = SqlManager.getValueFromVectorParam(
								ret.get(i), 0).doubleValue();
						if (impoff != null && !impoff.equals(new Double(0)))
							totImpoff += impoff.doubleValue();
					}
				}

				String importoFormattato = "";
				importoFormattato = pgManagerEst1.convertiMoney5(UtilityNumeri.convertiImporto(
						new Double(totImpoff), 5));
				pageContext.setAttribute("totaleNetto", importoFormattato,
						PageContext.REQUEST_SCOPE);

				importoFormattato = pgManagerEst1.convertiMoney5(UtilityNumeri.convertiImporto(
						importoNoRibasso, 5));
				pageContext.setAttribute("importoNoRibasso", importoFormattato,
						PageContext.REQUEST_SCOPE);

				pageContext.setAttribute("sicinc", sicinc, PageContext.REQUEST_SCOPE);

				if (!("2".equals(sicinc)) || sicinc == null) {
					importoFormattato = pgManagerEst1.convertiMoney5(UtilityNumeri.convertiImporto(
							importoSicurezza, 5));
					pageContext.setAttribute("importoSicurezza", importoFormattato,
							PageContext.REQUEST_SCOPE);
				}

				double totaleOfferto = 0;

				totaleOfferto = totImpoff;

				if (importoNoRibasso != null)
					totaleOfferto = totaleOfferto + importoNoRibasso.doubleValue();

				if (importoSicurezza != null && (!("2".equals(sicinc)) || sicinc == null))
					totaleOfferto = totaleOfferto	+ importoSicurezza.doubleValue();

				importoFormattato = pgManagerEst1.convertiMoney5(UtilityNumeri.convertiImporto(
						new Double(totaleOfferto), 5));

				pageContext.setAttribute("totaleOfferto", importoFormattato,
						PageContext.REQUEST_SCOPE);

				importoFormattato = pgManagerEst1.convertiMoney5(UtilityNumeri.convertiImporto(
						importoEffettivo, 5));
				pageContext.setAttribute("importoEffettivo", importoFormattato,
						PageContext.REQUEST_SCOPE);

				pageContext.setAttribute("dittaAggiudicataria", ditta,
						PageContext.REQUEST_SCOPE);
			} else {
				// nel caso di gare divise a lotti con offerta unica si devono
				// andare a prelevare i dati
				// dai lotti di gara

				double importoNoRibassoTotale = 0;
				double importoSicurezzaTotale = 0;
				double importoOffertaPrezziTotale = 0;
				double totaleOfferto = 0;
				Double importo = null;

				List ret = sqlManager.getListVector(
						"select NGARA,IMPNRL,IMPSIC from gare where codgar1=? and " +
						"(genere <> 3 or genere is null) and modlicg in (5,6,14)",
						new Object[] { codgar });

				if (ret != null && ret.size() > 0) {
					for (int i = 0; i < ret.size(); i++) {
						String ngaraLotto = SqlManager.getValueFromVectorParam(
								ret.get(i), 0).stringValue();

						// Importo non soggetto al ribasso
						importo = SqlManager.getValueFromVectorParam(
								ret.get(i), 1).doubleValue();
						if (importo == null)
							importo = new Double(0);
						importoNoRibassoTotale += importo.doubleValue();

						// Importo sicurezza
						importo = SqlManager.getValueFromVectorParam(
								ret.get(i), 2).doubleValue();
						if (importo == null)
							importo = new Double(0);
						importoSicurezzaTotale += importo.doubleValue();

						// Importo dettaglio prezzi
						List retDPRE = sqlManager.getListVector(
								"select impoff  from dpre where ngara=? and dittao=?" +
								" and exists (select ngara5 from ditg where (fasgar is null or fasgar>5)" +
								" and ngara5=?  and dittao = ?)",
								new Object[] { ngaraLotto, dittao, ngaraLotto, dittao });
						if (retDPRE != null && retDPRE.size() > 0) {
							for (int j = 0; j < retDPRE.size(); j++) {
								Double impoff = SqlManager.getValueFromVectorParam(
												retDPRE.get(j), 0).doubleValue();
								if (impoff == null)
									impoff = new Double(0);
								importoOffertaPrezziTotale += impoff.doubleValue();
							}
						}
					}
					totaleOfferto = importoNoRibassoTotale + importoOffertaPrezziTotale;
					if (sicinc == null || "1".equals(sicinc)) totaleOfferto += importoSicurezzaTotale;

					String importoFormattato = pgManagerEst1.convertiMoney5(
							UtilityNumeri.convertiImporto(new Double(totaleOfferto), 5));

					pageContext.setAttribute("totaleOfferto",
							importoFormattato, PageContext.REQUEST_SCOPE);

					pageContext.setAttribute("sicinc", sicinc, PageContext.REQUEST_SCOPE);
				}
			}
		} catch (SQLException e) {
			throw new JspException("Errore durante il calcolo degli importi "
					+ "del riepilogo importo offerto complessivo", e);
		} catch (GestoreException e) {
			throw new JspException(
					"Errore in fase di esecuzione della query per determinare l'importo totale netto",
					e);
		}

		//Si determina se è attiva l'integrazione AUR
	    String integrazioneAUR="0";
	    String urlWSAUR = ConfigManager.getValore("it.eldasoft.sil.pg.integrazioneAUR.ws.url");
	    if(urlWSAUR != null && !"".equals(urlWSAUR)){
	      integrazioneAUR ="1";
	    }
	    pageContext.setAttribute("integrazioneAUR", integrazioneAUR, PageContext.REQUEST_SCOPE);


		return nomimo;
	}



}