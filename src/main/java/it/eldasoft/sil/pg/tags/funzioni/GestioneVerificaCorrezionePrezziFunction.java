/*
 * Created on 30/Set/09
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
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Vengono caricati gli importi per la popup popupVerificaCorrezionePrezzi.
 *
 * @author Marcello Caminiti
 */
public class GestioneVerificaCorrezionePrezziFunction extends
		AbstractFunzioneTag {

	public GestioneVerificaCorrezionePrezziFunction() {
		super(4, new Class[] { PageContext.class, String.class, String.class,
				String.class });
	}

	/**
	 * La funzione restituisce la somma di IMPOFF.DPRE
	 */
	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

		String ngara = (String) params[1];
		String dittao = (String) params[2];

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
				pageContext, SqlManager.class);

	    AggiudicazioneManager aggiudicazioneManager = (AggiudicazioneManager) UtilitySpring.getBean(
	        "aggiudicazioneManager", pageContext, AggiudicazioneManager.class);

	    Double ribasso = null;
	    Double riboepv = null;
		Double impapp = null;
		Double impnrl = null;
		Double impsic = null;
		Double onprge = null;
		double importoRibasso;
		double importoPrezziUnitari;
		String sicinc = null;
		double totImpoff = 0;
		Long modlicg = null;
		String onsogrib=null;

		try {
			Vector datiGARE = sqlManager.getVector(
					"select ribagg, impapp, impnrl, impsic, onprge, sicinc, modlicg, onsogrib, riboepv from gare where ngara = ?",
					new Object[] { ngara });

			if (datiGARE != null && datiGARE.size() > 0) {
				ribasso = ((Double) ((JdbcParametro) datiGARE.get(0)).getValue());
				impapp = ((Double) ((JdbcParametro) datiGARE.get(1)).getValue());
				impnrl = ((Double) ((JdbcParametro) datiGARE.get(2)).getValue());
				impsic = ((Double) ((JdbcParametro) datiGARE.get(3)).getValue());
				onprge = ((Double) ((JdbcParametro) datiGARE.get(4)).getValue());
                sicinc = ((String) ((JdbcParametro) datiGARE.get(5)).getValue());
                modlicg = ((Long) ((JdbcParametro) datiGARE.get(6)).getValue());
                onsogrib = ((String) ((JdbcParametro) datiGARE.get(7)).getValue());
                riboepv = ((Double) ((JdbcParametro) datiGARE.get(8)).getValue());
			}

			if (ribasso == null)
				ribasso = new Double(0);
			if (impapp == null)
				impapp = new Double(0);
			if (impnrl == null)
				impnrl = new Double(0);
			if (impsic == null)
				impsic = new Double(0);
            if (onprge == null)
              onprge = new Double(0);

            //Se OEPV, calcola il ribasso di aggiudicazione a partire dall'importo offerto della ditta
            //Non viene fatto di conseguenza il calcolo dell'importo offerto a partire dal ribasso ma viene
            //considerato direttamente l'importo memorizzato in DITG
            if (modlicg != null && modlicg.longValue() == 6){
               ribasso = new Double(0);
               importoRibasso = 0;
               Vector datiDitta = sqlManager.getVector("select impoff from ditg where ngara5=? and dittao=?",
                   new Object[] {ngara, dittao});
               if (datiDitta != null && datiDitta.size()>0){
                 if (((JdbcParametro) datiDitta.get(0)).getValue() != null)
                	 importoRibasso = ((Double) ((JdbcParametro) datiDitta.get(0)).getValue()).doubleValue();
	                 if(riboepv != null) {
	                	 ribasso = riboepv;
	                 }else {
	                     ribasso = new Double(aggiudicazioneManager.calcolaRIBAUO(impapp.doubleValue(), onprge.doubleValue(),
	                             impsic.doubleValue(), impnrl.doubleValue(), sicinc, importoRibasso,onsogrib));
	                 }
               }
            } else {
    			// Calcolo dell'importo dipendente dal ribasso
    			importoRibasso = impapp.doubleValue() - impnrl.doubleValue()
                         - impsic.doubleValue();

    			if(!"1".equals(onsogrib))
    			  importoRibasso -= onprge.doubleValue();

    			importoRibasso = importoRibasso * (1 + ribasso.doubleValue() / 100);
    			importoRibasso += impnrl.doubleValue();

    			if (sicinc == null || !("2".equals(sicinc)))
    				importoRibasso += impsic.doubleValue();

    			if (!("1".equals(onsogrib)))
                  importoRibasso += onprge.doubleValue();
            }

			// Calcolo dell'importo risultante dal dettaglio dei prezzi unitari
			List ret = sqlManager.getListVector(
					"select impoff  from dpre where ngara= ? and dittao = ?",
					new Object[] { ngara,	dittao });
			if (ret != null && ret.size() > 0) {
				for (int i = 0; i < ret.size(); i++) {
					Double impoff = SqlManager.getValueFromVectorParam(
							ret.get(i), 0).doubleValue();
					if (impoff != null && !impoff.equals(new Double(0)))
						totImpoff += impoff.doubleValue();
				}
			}

			importoPrezziUnitari = totImpoff + impnrl.doubleValue();
			if (sicinc == null || !("2".equals(sicinc)))
				importoPrezziUnitari += impsic.doubleValue();

			// Calcolo coefficiente di discordanza
			double coeffDiscordanza, importoTemp;

			if (importoRibasso != 0){
			  coeffDiscordanza = importoRibasso - impnrl.doubleValue();
              if (sicinc == null || !("2".equals(sicinc)))
                coeffDiscordanza -= impsic.doubleValue();
              }
			else
			  coeffDiscordanza = 0;

			importoTemp = importoPrezziUnitari - impnrl.doubleValue();
			if (sicinc == null || !("2".equals(sicinc))) {
				importoTemp -= impsic.doubleValue();
			}

			// Se l'importo risultante dal dettaglio dei prezzi unitari
			// e' nullo il coefficiente di discordanza e' nullo
			if (importoTemp != 0.0)
				coeffDiscordanza = coeffDiscordanza / importoTemp;
			else
				coeffDiscordanza = 0;

            pageContext.setAttribute("ribasso", ribasso, PageContext.REQUEST_SCOPE);

            pageContext.setAttribute("importoRibasso", new Double(
                importoRibasso), PageContext.REQUEST_SCOPE);

			pageContext.setAttribute("importoPrezziUnitari", new Double(
				importoPrezziUnitari), PageContext.REQUEST_SCOPE);

			double deltaImporti = Math.abs(importoRibasso - importoPrezziUnitari);
            pageContext.setAttribute("deltaImporti", new Double(deltaImporti), PageContext.REQUEST_SCOPE);

	        // Coefficiente di discordanza arrotondato a 3 cifre, per la
			// visualizzazione a video
			if (importoTemp != 0.0)
				pageContext.setAttribute("coeffDiscordanzaArrotondato",
						UtilityNumeri.convertiDouble(new Double(coeffDiscordanza),
								UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 10),
								PageContext.REQUEST_SCOPE);
			else
				pageContext.setAttribute("coeffDiscordanzaArrotondato", null,
						 PageContext.REQUEST_SCOPE);

			// Coefficiente di discordanza non arrotondato, adoperato per
			// l'aggiornamento degli importi
			pageContext.setAttribute("coeffDiscordanza", new Double(coeffDiscordanza),
					PageContext.REQUEST_SCOPE);

		} catch (SQLException e) {
			throw new JspException("Errore durante il caricamento dei dati "
					+ "dalle entità GARE, DPRE e DITG", e);
		} catch (GestoreException e) {
			throw new JspException("Errore in fase di calcolo degli importi", e);
		}

		// valore di ritorno
		Double valoreRitorno = null;

		if (totImpoff != 0)
			valoreRitorno = new Double(totImpoff);

		return UtilityNumeri.convertiImporto(valoreRitorno, 2);
	}

}