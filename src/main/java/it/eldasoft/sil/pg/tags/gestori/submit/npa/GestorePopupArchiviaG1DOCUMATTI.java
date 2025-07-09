/*
 * Created on 13/04/15
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit.npa;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

public class GestorePopupArchiviaG1DOCUMATTI extends AbstractGestoreEntita {

	/** Logger */
	static Logger logger = Logger.getLogger(GestorePopupArchiviaG1DOCUMATTI.class);

	@Override
	public String getEntita() {
		return "G1DOCUMATTI";
	}

	public GestorePopupArchiviaG1DOCUMATTI() {
		super(false);
	}

	public GestorePopupArchiviaG1DOCUMATTI(boolean isGestoreStandard) {
		super(isGestoreStandard);
	}

	@Override
	public void postDelete(DataColumnContainer impl) throws GestoreException {
	}

	@Override
	public void postInsert(DataColumnContainer impl) throws GestoreException {
	}

	@Override
	public void postUpdate(DataColumnContainer impl) throws GestoreException {

	}

	@Override
	public void preDelete(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
	}

	@Override
	public void preInsert(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
	}

	@Override
	public void preUpdate(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

		String[] listaDocumentiSelezionati = this.getRequest().getParameterValues("keys");
		String ngara = this.getRequest().getParameter("ngara");
		String dittao = this.getRequest().getParameter("dittao");

		String isarchi = this.getRequest().getParameter("isarchi");
		if ("1".equals(isarchi)) {
			this.getRequest().setAttribute("isarchi", "2");
			return;
		}

		String elencoIdDoc = "";

		String messageKey = null;
		int livEvento = 3;
		String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
		String oggEvento = ngara;
		try {
			String codgar = (String) sqlManager.getObject("select codgar1 from gare where ngara=?",
					new Object[] { ngara });
			Long genere = (Long) sqlManager.getObject("select genere from V_GARE_TORN where codgar = ?",
					new Object[] { codgar });
			if (genere != null && (genere.equals(3L) || genere.equals(1L))) {
				oggEvento = codgar;
			}

			if (listaDocumentiSelezionati == null || listaDocumentiSelezionati.length == 0) {
				throw new GestoreException("Non e' possibile procedere con l'archiviazione: nessun documento selezionato", "archiviaG1DOCUMATTI.nessunDocumentoSelezionato");
			}

			String updateDocumgara = "update g1documatti set isarchi=1 where id=? and ngara=? and isarchi is null and statodoc = 5";
			Object[] parametriUpdate = null;
			if (StringUtils.isNotEmpty(dittao)) {
				updateDocumgara += " and dittao=?";
			} else {
				updateDocumgara += " and dittao is null";
			}

			for (int i = 0; i < listaDocumentiSelezionati.length; i++) {
				elencoIdDoc += listaDocumentiSelezionati[i] + ",";
				if (StringUtils.isNotEmpty(dittao)) {
					parametriUpdate = new Object[] { listaDocumentiSelezionati[i], ngara, dittao };
				} else {
					parametriUpdate = new Object[] { listaDocumentiSelezionati[i], ngara };
				}
				sqlManager.update(updateDocumgara, parametriUpdate);
			}

			String selectCountDocumentiRimastiDaArchiviare = "select count(*) from g1documatti where ngara=? and isarchi is null and statodoc = 5";
			Object[] parametriSelect = null;
			if (StringUtils.isNotEmpty(dittao)) {
				parametriSelect = new Object[] { ngara, dittao };
				selectCountDocumentiRimastiDaArchiviare += " and dittao=?";
			} else {
				parametriSelect = new Object[] { ngara };
				selectCountDocumentiRimastiDaArchiviare += " and dittao is null";
			}
			Long countDocumentiRimastiDaArchiviare = (Long) sqlManager.getObject(selectCountDocumentiRimastiDaArchiviare, parametriSelect);
			if (countDocumentiRimastiDaArchiviare == null || countDocumentiRimastiDaArchiviare.equals(0L)) {
				throw new GestoreException("Non e' possibile procedere con l'archiviazione: almeno un documento deve rimanere attivo.", "archiviaG1DOCUMATTI.assenzaDocumenti");
			}
			this.getRequest().setAttribute("RISULTATO", "OK");
			livEvento = 1;
			errMsgEvento = "";
		} catch (GestoreException e) {
			this.getRequest().setAttribute("RISULTATO", "ERRORI");
			livEvento = 3;
			errMsgEvento = e.getMessage();
			throw e;
		} catch (Exception e) {
			this.getRequest().setAttribute("RISULTATO", "ERRORI");
			livEvento = 3;
			errMsgEvento = e.getMessage();
			throw new GestoreException("Errore inaspettato nell'archiviazione dei documenti per la gara", null, e);
		} finally {
			try {
				if (StringUtils.isNotEmpty(elencoIdDoc))
					elencoIdDoc = elencoIdDoc.substring(0, elencoIdDoc.length() - 1);

				String descrTipoDoc = "Archiviazione documenti atti e offerte (id.doc.: " + elencoIdDoc + ")";
				LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
				logEvento.setLivEvento(livEvento);
				logEvento.setOggEvento(oggEvento);
				logEvento.setCodEvento("GA_ARCHIVIA_ATTIOFFERTE");
				logEvento.setDescr(descrTipoDoc);
				logEvento.setErrmsg(errMsgEvento);
				LogEventiUtils.insertLogEventi(logEvento);
			} catch (Exception le) {
				messageKey = "errors.logEventi.inaspettataException";
				logger.error(this.resBundleGenerale.getString(messageKey), le);
			}
		}
	}

}