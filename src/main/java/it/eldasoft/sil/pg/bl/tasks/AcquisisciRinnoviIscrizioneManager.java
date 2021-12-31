/*
 * Created on 26/03/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.tasks;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ElencoOperatoriManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.SpringAppContext;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

public class AcquisisciRinnoviIscrizioneManager {

	static Logger logger = Logger.getLogger(AcquisisciRinnoviIscrizioneManager.class);

	private SqlManager sqlManager;
	private PgManager pgManager;
	private ElencoOperatoriManager elencoOperatoriManager;

	/**
	 * @param sqlManager
	 */
	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	/**
	 * Set PgManager
	 *
	 * @param pgManager
	 */
	public void setPgManager(PgManager pgManager) {
		this.pgManager = pgManager;
	}

	/**
	 * @param elencoOperatoriManager
	 */
	public void setElencoOperatoriManager(ElencoOperatoriManager elencoOperatoriManager) {
		this.elencoOperatoriManager = elencoOperatoriManager;
	}

	/**
	 * Gestione dei messaggi FS3 provenienti da portale per il rinnovo delle
	 * iscrizione agli elenchi
	 *
	 * @throws it.eldasoft.gene.web.struts.tags.gestori.GestoreException
	 */
	public void acquisisciRinnoviIscrizione() throws GestoreException {

		// il task deve operare esclusivamente nel caso di applicativo attivo e
		// correttamente avviato ed inoltre deve esistere l'integrazione con il
		// Portale Alice (OP114)
		if (WebUtilities.isAppNotReady()) {
			return;
		}
		ServletContext context = SpringAppContext.getServletContext();
		if (!GeneManager.checkOP(context, "OP114")) {
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("acquisisciRinnoviIscrizione: inizio metodo");
		}

		Long idcom;
		String user;
		String ngara;
		String dataIns;
		String select = "SELECT idcom, userkey1, comkey2, comdatins FROM w_invcom, "
						+ "w_puser WHERE idprg = ? AND comstato = ? AND comtipo = ? AND comkey1 = usernome ORDER BY idcom";
		String idprg = "PA";
		String comstato = "5";
		String comtipo = "FS3";
		boolean operatoreTrovato=true;

		List listaIDCOM = null;
		try {
			listaIDCOM = this.sqlManager.getListVector(select, new Object[]{idprg, comstato, comtipo});
		} catch (SQLException e) {
			throw new GestoreException("Errore nella lettura della tabella W_INVCOM ", null, e);
		}
		if (listaIDCOM != null && listaIDCOM.size() > 0) {

		    //variabili per tracciatura eventi
		    int livEvento = 1;
		    String codEvento = "GA_ACQUISIZIONE_RINNOVO";
		    String oggEvento = "";
		    String descrEventoTmp = "Acquisizione rinnovo iscrizione a elenco operatori/catalogo da portale Appalti";
		    String descrEvento="";
		    String errMsgEvento = "";

		    for (Object listaIDCOM1 : listaIDCOM) {
		       idcom = SqlManager.getValueFromVectorParam(listaIDCOM1, 0).longValue();
		       user = SqlManager.getValueFromVectorParam(listaIDCOM1, 1).getStringValue();
		       ngara = SqlManager.getValueFromVectorParam(listaIDCOM1, 2).getStringValue();
		       dataIns = SqlManager.getValueFromVectorParam(listaIDCOM1, 3).getStringValue();

		       livEvento = 1;
		       oggEvento = ngara;
		       errMsgEvento = "";

		       Long genere = null;
               try {
                  genere = (Long)this.sqlManager.getObject("select genere from gare where ngara=?", new Object[]{ngara});
                } catch (SQLException e) {
                  livEvento = 3;
                  errMsgEvento=e.getMessage();
                }
               if(genere!=null){
                 if(genere.longValue()==10)
                   descrEventoTmp = "Acquisizione rinnovo iscrizione a elenco operatori da portale Appalti";
                 else if(genere.longValue()==20)
                   descrEventoTmp = "Acquisizione rinnovo iscrizione a catalogo da portale Appalti";
               }
               descrEvento = descrEventoTmp + " (cod.ditta " + user + ")";

				//Si deve controllare che l'impresa sia presente in gara sia direttamente che come
		        //mandataria di una RT
				try {
			        String datiControllo[] = pgManager.controlloEsistenzaDittaElencoGara(user, ngara, "$" + ngara, null);
			        if("0".equals(datiControllo[0]) || "2".equals(datiControllo[0])){
			          this.pgManager.aggiornaStatoW_INVOCM(idcom, "7", "");
			          String msg="La ditta " + user + " che ha fatto richiesta di rinnovo d'iscrizione";
			          if("0".equals(datiControllo[0]))
			            msg+=" non è presente fra gli operatori dell'elenco " + ngara;
			          else if("2".equals(datiControllo[0]))
			            msg+=" è presente come mandataria in più raggruppamenti temporanei dell'elenco " + ngara;
			          logger.error(msg);
			          operatoreTrovato=false;
			          livEvento=3;
			          errMsgEvento=msg;
			        }else{
			          user = datiControllo[1];
			          operatoreTrovato=true;
			        }
			    } catch (GestoreException e) {
			       operatoreTrovato=false;
			       this.pgManager.aggiornaStatoW_INVOCM(idcom, "7", "");
			       logger.error("Errore nella ricerca del richiedente della richiesta di rinnovo dell'elenco " + ngara, e);
			       livEvento=3;
                   errMsgEvento=e.getMessage();
			    }


				try {
					if(operatoreTrovato)
					  this.elencoOperatoriManager.rinnovoIscrizione(idcom, user, ngara, dataIns);
				} catch (GestoreException e) {
					this.pgManager.aggiornaStatoW_INVOCM(idcom, "7", "");
					livEvento=3;
	                errMsgEvento=e.getMessage();
				}

				try {
                  LogEvento logEvento = new LogEvento();
                  logEvento.setCodApplicazione("PG");
                  logEvento.setOggEvento(oggEvento);
                  logEvento.setLivEvento(livEvento);
                  logEvento.setCodEvento(codEvento);
                  logEvento.setDescr(descrEvento);
                  logEvento.setErrmsg(errMsgEvento);
                  LogEventiUtils.insertLogEventi(logEvento);
                } catch (Exception le) {
                  logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
                }

			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("acquisisciRinnoviIscrizione: fine metodo");
		}
	}
}
