/*
 * Created on 20/mag/2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiRicezioneFunction;
import it.eldasoft.sil.pg.tags.gestori.plugin.GestorePopupFasiGara;

import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per il salvataggio in sessione dei campi di DITG
 * modificati dalle due popup denominate 'Ulteriori dettagli' delle fasi di
 * ricezione offerte e fasi di gara
 *
 * @author Luca.Giacomazzo
 */
public class GestorePopupFasiRicezione_Gara extends AbstractGestoreEntita {

	public GestorePopupFasiRicezione_Gara() {
		super(false);
	}

	@Override
  public String getEntita() {
		return "DITG";
	}

	@Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
	}

	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

		/* Copia in sessione dei campi non chiave delle entita' DITGSTATI e DITG,
		 * , con la chiave costitutita come segue:
		 *
		 * DITGSTATI = <codiceTornata>;<codiceDitta>;<codiceLotto>;<fasgar>
		 * DITG      = <codiceTornata>;<codiceDitta>;<codiceLotto>
		 */

		String wizardPaginaAttiva = null;
		if(this.getRequest().getAttribute(
				GestioneFasiRicezioneFunction.PARAM_WIZARD_PAGINA_ATTIVA) != null)
			wizardPaginaAttiva = (String) this.getRequest().getAttribute(
					GestioneFasiRicezioneFunction.PARAM_WIZARD_PAGINA_ATTIVA);
		else
			wizardPaginaAttiva = this.getRequest().getParameter(
					GestioneFasiRicezioneFunction.PARAM_WIZARD_PAGINA_ATTIVA);

		//	 Campi chiave di DITG
		DataColumnContainer campiChiaveDITG = new DataColumnContainer(datiForm.getColumns("DITG", 0));
		String codiceTornata = campiChiaveDITG.getColumn("DITG.CODGAR5").getValue().getStringValue();
		String codiceLotto = campiChiaveDITG.getColumn("DITG.NGARA5").getValue().getStringValue();
		String codiceDitta = campiChiaveDITG.getColumn("DITG.DITTAO").getValue().getStringValue();

		// Chiave con cui inserire in sessione il DataColumncontainer dati della
		// tabella DITG relativi alla ditta di cui si e' modificato il
		// dettaglio. La chiave e' nella forma:
		// <codiceTornata>;<codiceDitta>;<codiceLotto>
		String keyDITG = codiceTornata + ";" + codiceDitta + ";" + codiceLotto;

		// Chiave con cui inserire in sessione il DataColumncontainer dati della
		// tabella DITGSTATI relativi alla ditta di cui si e' modificato il
		// dettaglio. La chiave e' nella forma:
		// <codiceTornata>;<codiceDitta>;<codiceLotto>;<stepgar>
		String keyDITGSTATI = codiceTornata + ";" + codiceDitta + ";" +
				codiceLotto + ";" +	wizardPaginaAttiva;

		HttpSession sessione = this.getRequest().getSession();

		if(datiForm.isModifiedTable("DITGSTATI") || datiForm.isModifiedTable("DITG")){

			if(datiForm.isModifiedTable("DITGSTATI")){
				// Campi non chiave dell'entita' DITGSTATI
				DataColumnContainer dataColumnContainerDITGSTATI =
					new DataColumnContainer(datiForm.getColumns("DITGSTATI", 2));

				if(sessione.getAttribute(GestorePopupFasiGara.CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA) != null){
					HashMap mappa = (HashMap) sessione.getAttribute(
							GestorePopupFasiGara.CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA);
					if(mappa.containsKey(keyDITGSTATI))
						mappa.remove(keyDITGSTATI);

					mappa.put(keyDITGSTATI, dataColumnContainerDITGSTATI);
				} else {
					// Creazione dell'oggetto HashMap per contenere tutti i DataColumnContainer
					// di DITGSTATI relativi ai record modificati
					HashMap mappa = new HashMap();
					mappa.put(keyDITGSTATI, dataColumnContainerDITGSTATI);
					sessione.setAttribute(GestorePopupFasiGara.CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA,
							mappa);

				}
			} else {
				// Se i campi dell'entita' DITGSTATI non sono stati modificati, significa
				// che l'eventuale oggetto gia' presente in sessione non e' piu' valido
				// perche' all'ultima modifca della popup i valori dei campi sono identici
				// a quelli presenti nella base dati
				if(sessione.getAttribute(GestorePopupFasiGara.CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA) != null){
					HashMap mappa = (HashMap) sessione.getAttribute(
							GestorePopupFasiGara.CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA);
					if(mappa.containsKey(keyDITGSTATI))
						mappa.remove(keyDITGSTATI);
				}
			}

			if(datiForm.isModifiedTable("DITG")){
				// Campi non chiave dell'entita' DITG
				DataColumnContainer dataColumnContainerDITG =
					new DataColumnContainer(datiForm.getColumns("DITG", 2));

				// Nomi dei campi dell'entita' DITG gestiti salvandoli in sessione.
				// Gli altri campi presenti nella popup non vengono salvati in sessione,
				// perche':
				// - ad essi sono collegate delle funzioni JS che ne aggiornano il valore
				// direttamente dalle pagine 'Ricezione offerte' e 'Fasi di gara';
				// - e' possibile eseguire degli update non voluti di campi di DITG;
				String campiDITG_gestitiConSessione = "DITG.ORADOM;DITG.MEZDOM;" +
						"DITG.ALTNOT;DITG.NPRREQ;DITG.DATREQ;DITG.ORAREQ;DITG.MEZREQ;" +
						"DITG.RICSUB;DITG.CONGALT;DITG.NPLETTRICHGIU;" +
						"DITG.DLETTRICHGIU;DITG.DTERMPRESGIU;DITG.NPRICEZGIU;" +
						"DITG.DRICEZGIU;DITG.OGGRICEZGIU;DITG.RICAVVAL;DITG.NOTAVVAL;" +
						"DITG.REQMIN;DITG.ESTIMP;DITG.INOSSERV;DITG.COORDSIC;" +
						"DITG.NVCOMMTECN;DITG.DVCOMMTECN;DITG.NVCONTRADD;DITG.DVCONTRADD;"+
						"DITG.NPROFF;DITG.PERCMANO;DITG.DSORTEV";
				HashMap colonneDITG = dataColumnContainerDITG.getColonne();
				Iterator iter = colonneDITG.keySet().iterator();

				String nomiCampiDaNonSalvare = "";
				while(iter.hasNext()){
					String nomeCampo = (String) iter.next();
					if(campiDITG_gestitiConSessione.indexOf(nomeCampo) < 0)
						nomiCampiDaNonSalvare = nomiCampiDaNonSalvare.concat(nomeCampo).concat(";");
				}

				if(nomiCampiDaNonSalvare.length() > 1){
					nomiCampiDaNonSalvare = nomiCampiDaNonSalvare.substring(0,
							nomiCampiDaNonSalvare.length() -1);
					dataColumnContainerDITG.removeColumns(nomiCampiDaNonSalvare.split(";"));
				}

				if(sessione.getAttribute(GestorePopupFasiGara.CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA) != null){
					HashMap mappa = (HashMap) sessione.getAttribute(
							GestorePopupFasiGara.CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA);
					if(mappa.containsKey(keyDITG))
						mappa.remove(keyDITG);

					mappa.put(keyDITG, dataColumnContainerDITG);
				} else {
					// Creazione dell'oggetto HashMap per contenere tutti i DataColumnContainer
					// di DITGSTATI relativi ai record modificati
					HashMap mappa = new HashMap();
					mappa.put(keyDITG, dataColumnContainerDITG);
					sessione.setAttribute(GestorePopupFasiGara.CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA,
							mappa);
				}
			} else {
				// Se i campi dell'entita' DITG non sono stati modificati, significa che
				// l'eventuale oggetto gia' presente in sessione non e' piu' valido perche'
				// all'ultima modifca della popup i valori dei campi sono identici a quelli
				// presenti nella base dati
				if(sessione.getAttribute(GestorePopupFasiGara.CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA) != null){
					HashMap mappa = (HashMap) sessione.getAttribute(
							GestorePopupFasiGara.CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA);
					if(mappa.containsKey(keyDITG))
						mappa.remove(keyDITG);
				}
			}
		} else {
			// Se i campi delle entita' DITG e DITGSTATI non sono stati modificati,
			// significa che l'eventuale oggetto gia' presente in sessione non e' piu'
			// valido perche' all'ultima modifca della popup i valori dei campi sono
			// identici a quellis presenti nella base dati
			if(sessione.getAttribute(GestorePopupFasiGara.CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA) != null){
				HashMap mappa = (HashMap) sessione.getAttribute(
						GestorePopupFasiGara.CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA);
				if(mappa.containsKey(keyDITGSTATI))
					mappa.remove(keyDITGSTATI);
				if(mappa.containsKey(keyDITG))
					mappa.remove(keyDITG);
			}
		}
        this.geneManager.preUpdateGestore("DITG", datiForm);
		// Set nel request di un attributo per riaprire la stessa popup e invocare
		// subito l'istruzione JS wndow.close();
		this.getRequest().setAttribute("RISULTATO_SALVATAGGIO", "OK");

		// Ripristino nel request della pagina attiva del wizard
		this.getRequest().setAttribute(
				GestioneFasiRicezioneFunction.PARAM_WIZARD_PAGINA_ATTIVA,
				wizardPaginaAttiva);
		this.getRequest().setAttribute("stepWizard", new Long(wizardPaginaAttiva));
	}

	@Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
	}

}