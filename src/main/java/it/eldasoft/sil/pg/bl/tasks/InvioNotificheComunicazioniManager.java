package it.eldasoft.sil.pg.bl.tasks;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.utils.MailUtils;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.mail.IMailSender;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;

public class InvioNotificheComunicazioniManager {

	static Logger logger = Logger.getLogger(InvioNotificheComunicazioniManager.class);

	private SqlManager sqlManager;

	private MailManager mailManager;

	/**
	 * Set SqlManager
	 *
	 * @param sqlManager
	 */
	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	public void setMailManager(MailManager mailManager) {
		this.mailManager = mailManager;
	}

	public void inviaNotificheComunicazioni() {

		if (WebUtilities.isAppNotReady() || StringUtils.isBlank(ConfigManager.getValore("comunicazioni.notifiche.idcfg")))
			return;

		if (logger.isDebugEnabled())
			logger.debug("inviaNotificheComunicazioni: inizio metodo");
		int livEvento = 1;
		String codEvento = "GA_INVIO_NOTIFICHE_FS12";
		String oggEvento = "";
		String descrEvento = "Invio mail di notifica per comunicazioni ricevute.";
		String errMsgEvento = "";

		long millisecondiInUnGiorno = 24*60*60*1000;
		String dbFunctionDateToString = sqlManager.getDBFunction("DATETIMETOSTRING",
		        new String[] { "comdatins" });

	    String afterDate = UtilityDate.convertiData(new Date(System.currentTimeMillis() - millisecondiInUnGiorno), UtilityDate.FORMATO_GG_MM_AAAA);
		String select = "select idprg,idcom,comesnot," + dbFunctionDateToString + ",comkey2,torng.indnotifiche,torna.indnotifiche,torne.indnotifiche,g1stipula.indnotifiche"
				+ " from w_invcom"
				+ " left join torn as torng on ((select codgar1 from gare where ngara=w_invcom.comkey2) = torng.codgar)"
				+ " left join torn as torna on ((select codgar from gareavvisi where ngara=w_invcom.comkey2) = torna.codgar)"
				+ " left join torn as torne on ((select codgar from garealbo where ngara=w_invcom.comkey2) = torne.codgar)"
				+ " left join g1stipula on (comkey2 = g1stipula.ngara)"
				+ " where comtipo='FS12' and comstato = '3' and (comesnot is null or comesnot < 3)"
				+ " and (comdatins > "+sqlManager.getDBFunction("stringtodate", new String[] { afterDate })+ ")"
				+ " and (coment is null or coment <> 'HOMEPG')"
				+ " and (torng.indnotifiche is not null or torna.indnotifiche is not null or torne.indnotifiche is not null or g1stipula.indnotifiche is not null)";
		List<Vector<Object>> comunicazioniDaNotificare = new ArrayList<Vector<Object>>();
		try {
			comunicazioniDaNotificare = sqlManager.getListVector(select, null);
			if (comunicazioniDaNotificare == null) {
				throw new Exception("Errore inaspettato durante la lettura delle comunicazioni");
			}
			String tracciaturaErrori = "";
			String oggettoMail = ConfigManager.getValore("it.eldasoft.titolo") + ": notifica comunicazione ricevuta";
			IMailSender iMailSender = MailUtils.getInstance(mailManager,
					ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE),
					ConfigManager.getValore("comunicazioni.notifiche.idcfg"));
			for (int i = 0; i < comunicazioniDaNotificare.size(); i++) {
				int contatore = 0;
				livEvento = 1;
				String idprg = SqlManager.getValueFromVectorParam(comunicazioniDaNotificare.get(i), contatore++).stringValue();
				Long idcom = SqlManager.getValueFromVectorParam(comunicazioniDaNotificare.get(i), contatore++).longValue();
				Long comesnot = SqlManager.getValueFromVectorParam(comunicazioniDaNotificare.get(i), contatore++).longValue();
				String dataInserimento = SqlManager.getValueFromVectorParam(comunicazioniDaNotificare.get(i), contatore++).stringValue();
				String ngara = SqlManager.getValueFromVectorParam(comunicazioniDaNotificare.get(i), contatore++).stringValue();

				String[] dicituraNgara = {"la gara ", "l'avviso ", "l'elenco ", "la stipula "};
				String[] indnotifiche = {
						SqlManager.getValueFromVectorParam(comunicazioniDaNotificare.get(i), contatore++).stringValue(),
						SqlManager.getValueFromVectorParam(comunicazioniDaNotificare.get(i), contatore++).stringValue(),
						SqlManager.getValueFromVectorParam(comunicazioniDaNotificare.get(i), contatore++).stringValue(),
						SqlManager.getValueFromVectorParam(comunicazioniDaNotificare.get(i), contatore++).stringValue()};

				if (comesnot == null)
					comesnot = 0L;

				int tipoGara= -1;
				for (int j = 0; j < indnotifiche.length; j++) {
				    if (StringUtils.isNotEmpty(indnotifiche[j])) {
				    	tipoGara = j;
				        break;
				    }
				}
				String testoMail = "Gentile utente,\nin data " + dataInserimento + " hai ricevuto una nuova comunicazione per " + dicituraNgara[tipoGara] + ngara + ".";
				
					String arrayDestinatari[] = indnotifiche[tipoGara].split(",");
					for (int contatoreDestinatari = 0; contatoreDestinatari < arrayDestinatari.length; contatoreDestinatari++) {
						String arraySingoloDestinatario[] = new String[] { arrayDestinatari[contatoreDestinatari] };
						if (arraySingoloDestinatario[0] != null) {
							try {
								oggEvento = ngara;
								iMailSender.send(arraySingoloDestinatario, null, null, oggettoMail, testoMail, null);
								if (iMailSender.getDelay() != null) {
									TimeUnit.MILLISECONDS.sleep(iMailSender.getDelay());
								}
								comesnot = 100L;
							} catch (Exception e) {
								livEvento = 3;
								tracciaturaErrori += "Non è stato possibile inviare una notifica per " + dicituraNgara[tipoGara] + ngara + ". " + e.getMessage() + "\n";
							}
						}
					}
					if (comesnot != 100)
						comesnot++;
					TransactionStatus transazioneUpdate = this.sqlManager.startTransaction();
					boolean commitTransaction = false;
					try {
						sqlManager.update("update w_invcom set comesnot=? where idprg=? and idcom=?",new Object[] { comesnot, idprg, idcom});
						commitTransaction = true;
					} catch (Throwable e) {
						throw new Throwable("Errore durante la scrittura sulla base dati", e);
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
					LogEvento logEvento = new LogEvento();
					logEvento.setCodApplicazione("PG");
					logEvento.setOggEvento(oggEvento);
					logEvento.setLivEvento(livEvento);
					logEvento.setCodEvento(codEvento);
					logEvento.setDescr(descrEvento);
					logEvento.setErrmsg(tracciaturaErrori);
					LogEventiUtils.insertLogEventi(logEvento);
				}
			livEvento=1;
		} catch (Throwable exception) {
			livEvento = 3;
			errMsgEvento = "Errore inaspettato durante l'invio delle notifiche " + exception.getMessage();
		} finally {
			try {
				if (livEvento == 3) {
					LogEvento logEvento = new LogEvento();
					logEvento.setCodApplicazione("PG");
					logEvento.setLivEvento(livEvento);
					logEvento.setCodEvento(codEvento);
					logEvento.setDescr(descrEvento);
					logEvento.setErrmsg(errMsgEvento);
					LogEventiUtils.insertLogEventi(logEvento);
				}
			} catch (Exception loggerError) {
				logger.error("Errore inaspettato durante la tracciatura su w_logeventi", loggerError);
			} catch (Throwable e) {
				logger.error("Errore inaspettato durante l'inserimento dei messaggi di fine procedura", e);
			}
		}
		if (logger.isDebugEnabled())
			logger.debug("inviaNotificheComunicazioni: fine metodo");
	}
}