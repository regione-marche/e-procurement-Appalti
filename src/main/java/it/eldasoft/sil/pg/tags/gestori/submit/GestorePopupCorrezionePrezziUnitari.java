/*
 * Created on 02/ott/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityMath;

import java.sql.SQLException;
import java.util.List;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per la correzione
 * dei prezzi unitari
 *
 * @author Marcello.Caminiti
 */
public class GestorePopupCorrezionePrezziUnitari extends AbstractGestoreEntita {

	public GestorePopupCorrezionePrezziUnitari() {
		super(false);
	}

	@Override
  public String getEntita() {
		return "GARE";
	}

	@Override
  public void postDelete(DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void postInsert(DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void postUpdate(DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

		String codgar = datiForm.getString("CODGAR");
		String ngara = datiForm.getString("NGARA");
		String ditta = datiForm.getString("DITTAO");
		//Double impoff = datiForm.getDouble("IMPOFF");
		Double importoDettaglio = datiForm.getDouble("IMPORTO_DETTAGLIO");
		Double coeffDiscordanza = datiForm.getDouble("COEFFICIENTE");
		String sicinc = datiForm.getString("SICINC");
		Double onprge = datiForm.getDouble("ONPRGE");
		Double impsic = datiForm.getDouble("IMPSIC");
		Double impnrl = datiForm.getDouble("IMPNRL");

		/*if (impoff == null)
			impoff = new Double(0); */
		if (importoDettaglio == null)
			importoDettaglio = new Double(0);
		if (coeffDiscordanza == null)
			coeffDiscordanza = new Double(0);
		if (onprge == null)
			onprge = new Double(0);
		if (impsic == null)
			impsic = new Double(0);
		if (impnrl == null)
			impnrl = new Double(0);

		try {
			this.getSqlManager().update(
					"update DITG set IMPOFF1 = ? where CODGAR5 = ? and DITTAO = ? and NGARA5 = ?",
					new Object[] { importoDettaglio,codgar, ditta, ngara });

			// Copia di backup di PREOFF.DPRE
			this.getSqlManager().update(
					"update DPRE set IMPOFF1 = PREOFF where NGARA = ? and DITTAO = ?",
					new Object[] { ngara, ditta });

			// Aggiornamento di PREOFF.DPRE e IMPOFF.DPRE
			this.getSqlManager().update(
					"update DPRE set PREOFF = (PREOFF * ?) where NGARA = ? and DITTAO = ?",
					new Object[] { coeffDiscordanza, ngara, ditta });

			List ret = sqlManager.getListVector(
					"select quanti,preoff,contaf  from dpre where ngara= ? and dittao = ?",
					new Object[] { ngara,	ditta });

			importoDettaglio = new Double(0);
			if (ret != null && ret.size() > 0) {
				for (int i = 0; i < ret.size(); i++) {
					double newImpoff;
					Double quanti = SqlManager.getValueFromVectorParam(
							ret.get(i), 0).doubleValue();
					Double preoff = SqlManager.getValueFromVectorParam(
							ret.get(i), 1).doubleValue();
					Long contaf = SqlManager.getValueFromVectorParam(
							ret.get(i), 2).longValue();

					if (preoff == null)
						preoff = new Double(0);

					// Se QUANTI.DPRE è nullo si deve prendere QUANTI.GCAP
					if (quanti == null)
						quanti = (Double) sqlManager.getObject(
								"select quanti from gcap where ngara= ? and contaf = ?",
								new Object[] { ngara, contaf });

					newImpoff = quanti.doubleValue() * preoff.doubleValue();
					newImpoff = UtilityMath.round(newImpoff, 5);
					this.getSqlManager().update(
							"update DPRE set IMPOFF = ? where NGARA = ? and DITTAO = ? and CONTAF = ?",
							new Object[]{new Double(newImpoff),	ngara, ditta,	contaf });
					// MOD SS061109 - Calcola l'importo totale risultante dal dettaglo
					// prezzi e aggiorna l'importo offerto della ditta con il risultante
					if (newImpoff != 0)
						importoDettaglio = new Double(importoDettaglio.doubleValue() + newImpoff);
				}
			}

			Double importoOffertaPrezzi = new Double(importoDettaglio.doubleValue() +
                impnrl.doubleValue());

			if (! "2".equals(sicinc))
              importoOffertaPrezzi =  new Double(importoOffertaPrezzi.doubleValue() +
                      impsic.doubleValue());

			// Aggiornamento di IAGGIU.GARE
            double iaggiu = importoOffertaPrezzi.doubleValue() + onprge.doubleValue();
            if (sicinc != null && "2".equals(sicinc))
                iaggiu += impsic.doubleValue();

            iaggiu = UtilityMath.round(iaggiu, 2);
            this.getSqlManager().update("update GARE set IAGGIU = ? where NGARA = ?",
                    new Object[] { new Double(iaggiu), ngara });


			this.getRequest().setAttribute("RISULTATO", "CALCOLOESEGUITO");

		} catch (SQLException e) {
			this.getRequest().setAttribute("RISULTATO", "ERRORI");
			throw new GestoreException(
					"Errore durante la correzione dei prezzi unitari", "null",
					e);
		}
	}
}