package it.eldasoft.sil.pg.bl.tasks;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.bl.GestionePATManager;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScadenzaTerminiPatManager {

	static Logger logger = Logger.getLogger(ScadenzaTerminiPatManager.class);

	private SqlManager sqlManager;
	private GestionePATManager gestionePATManager;

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	public void setGestionePATManager(GestionePATManager gestionePATManager) {
		this.gestionePATManager = gestionePATManager;
	}

	private void verificaTerminiGara(List<?> datiPubblicazione) throws Throwable {

		int i = 0;
		String idGaruuid = SqlManager.getValueFromVectorParam(datiPubblicazione, i).getStringValue();
		String codgar = SqlManager.getValueFromVectorParam(datiPubblicazione, ++i).getStringValue();
		String uuid = SqlManager.getValueFromVectorParam(datiPubblicazione, ++i).getStringValue();
		Date dteoff = (Date) SqlManager.getValueFromVectorParam(datiPubblicazione, ++i).getValue();
		String oteoff = SqlManager.getValueFromVectorParam(datiPubblicazione, ++i).getStringValue();
		String ngara = SqlManager.getValueFromVectorParam(datiPubblicazione, ++i).getStringValue();
		try {
			if (dteoff == null)
				throw new Throwable("La data di scadenza termini della gara non è valorizzata");

			if (StringUtils.isBlank(oteoff)) {
				oteoff = "23:59";
			}
			Calendar cal = Calendar.getInstance();
			Date dataScadenza = new Date(dteoff.getTime());
			String oraScadenza = oteoff.substring(0, 2);
			String minutiScadenza = oteoff.substring(3);
			cal.setTime(dataScadenza);
			cal.add(Calendar.HOUR, Integer.parseInt(oraScadenza));
			cal.add(Calendar.MINUTE, Integer.parseInt(minutiScadenza));

			Calendar currentTime = Calendar.getInstance();
			Boolean isScadutiTermini = currentTime.compareTo(cal) > 0;

			TransactionStatus transazioneUpdate = this.sqlManager.startTransaction();
			boolean commitTransaction = false;
			try {
				if (isScadutiTermini) {
					gestionePATManager.rendiLottoVisibile(null, uuid, ngara);
					this.sqlManager.update("update garuuid set riservata='2' where id=?", new Object[] { idGaruuid });
				}
				commitTransaction = true;
			} catch (SQLException SQLErr) {
				throw new Throwable("Errore inaspettato durante il salvataggio della gara", SQLErr);
			} finally {
				if (transazioneUpdate != null) {
					try {
						if (commitTransaction) {
							this.sqlManager.commitTransaction(transazioneUpdate);
						} else {
							this.sqlManager.rollbackTransaction(transazioneUpdate);
						}
					} catch (SQLException e) {
						throw new Throwable("Errore durante la chiusura della transazione", e);
					}
				}
			}
		} catch (Throwable e) {
			throw new Throwable("Errore durante la verifica dei termini della gara " + codgar + " " + e.getMessage());
		}
	}

	public void verificaScadenzaTerminiPAT() {

		if (WebUtilities.isAppNotReady())
			return;
		try {
			if (!gestionePATManager.isConfiguratoPerPAT()) {
				return;
			}
		} catch (GestoreException e) {
			logger.error("Errore inaspettato durante la lettura della configurazione per ISWEB", e);
			return;
		}

		if (logger.isDebugEnabled())
			logger.debug("verificaScadenzaTerminiPAT: inizio metodo");
		int livEvento = 1;
		String codEvento = "GA_WSISWEB_VERIFICA_SCAD_TERM";
		String oggEvento = "";
		String descrEvento = "Procedura di verifica scadenza termini PAT";
		String errMsgEvento = null;

		String selectDatiPubblicazioni = "select garuuid.id,garuuid.codgar,garuuid.uuid, dteoff, oteoff, garuuid.ngara from garuuid" +
				" left join torn on garuuid.codgar=torn.codgar where riservata='1' and tipric=? and vernpa is not null";

		String tracciaturaErrori = "";
		try {
			Boolean isErrori = false;
			List<?> datiPubblicazioni = this.sqlManager.getListVector(selectDatiPubblicazioni, new Object[] {GestionePATManager.TIPRIC});
			for (int contatore = 0; contatore < datiPubblicazioni.size(); contatore++) {
				try {
					List<?> datiPubblicazione = (List<?>) datiPubblicazioni.get(contatore);
					verificaTerminiGara(datiPubblicazione);
				} catch (Throwable e) {
					isErrori = true;
					tracciaturaErrori += e.getMessage() + "\n";
				}
			}
			if (isErrori)
				throw new Throwable(tracciaturaErrori);
		} catch (SQLException sqlErr) {
			livEvento = 3;
			errMsgEvento = sqlErr.getMessage();
			descrEvento = "Procedura di verifica scadenza termini PAT: terminazione con errori";
		} catch (Throwable exception) {
			livEvento = 3;
			errMsgEvento = exception.getMessage();
			descrEvento = "Procedura di verifica scadenza termini PAT: terminazione con errori";
		} finally {
			try {
				LogEvento logEvento = new LogEvento();
				logEvento.setCodApplicazione("PG");
				logEvento.setOggEvento(oggEvento);
				logEvento.setLivEvento(livEvento);
				logEvento.setCodEvento(codEvento);
				logEvento.setDescr(descrEvento);
				logEvento.setErrmsg(errMsgEvento);
				LogEventiUtils.insertLogEventi(logEvento);

			} catch (Exception loggerError) {
				logger.error("Errore inaspettato durante la tracciatura su w_logeventi", loggerError);
			} catch (Throwable e) {
				logger.error("Errore inaspettato durante l'inserimento dei messaggi di fine procedura", e);
			}
		}
		if (logger.isDebugEnabled())
			logger.debug("verificaScadenzaTerminiPAT: fine metodo");
	}
}