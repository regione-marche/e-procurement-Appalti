/*
 * Created on 25/ott/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe di tipo bean per memorizzare i messaggi di errore, i messaggi di
 * warning, il numero di righe lette, il numero di record importati e il
 * numero di record non importati
 *
 * @author Luca.Giacomazzo
 */
public class LoggerImportOffertaPrezzi {

	private List listaMsgVerificaFoglio = null;
	private List listaErroriImport = null;
	private List listaMessaggiUnitaMisura = null;
	private int  numeroRecordImportati;
	private int  numeroRecordNonImportati;
	private int  numeroRigheLette;
	private int  numeroRecordAggiornati;
	private int  numeroRecordNonAggiornati;
	private int  numeroLottiCreati;
	private int  numeroRecordNonAggiornatiLottiNonAggiudicati;

	public LoggerImportOffertaPrezzi(){
		this.listaMsgVerificaFoglio = new ArrayList();
		this.listaErroriImport = new ArrayList();
		this.listaMessaggiUnitaMisura = new ArrayList();
		this.numeroRecordImportati = 0;
		this.numeroRecordNonImportati = 0;
		this.numeroRigheLette = 0;
		this.numeroRecordAggiornati = 0;
		this.numeroRecordNonAggiornati = 0;
		this.numeroLottiCreati = 0;
		this.numeroRecordNonAggiornatiLottiNonAggiudicati = 0;
	}

	public void addMsgVerificaFoglio(String erroreVerificaFoglio){
		this.listaMsgVerificaFoglio.add(erroreVerificaFoglio);
	}

	public void addMessaggioErrore(String messaggioErrore){
		this.listaErroriImport.add(messaggioErrore);
	}

	public void addListaMessaggiErrore(List listaMessaggi){
		this.listaErroriImport.addAll(listaMessaggi);
	}

	public boolean removeMessaggiErrore(List listaMessaggi){
		return this.listaErroriImport.removeAll(listaMessaggi);
	}

	/*public void addListaMessaggiWarnig(List listaMessaggi){
		this.listaWarningImport.addAll(listaMessaggi);
	}*/

	public void addMessaggioUnitaMisura(String messaggioUnitaMisura){
		this.listaMessaggiUnitaMisura.add(messaggioUnitaMisura);
	}

	public void incrementaRecordImportati(){
		this.numeroRecordImportati++;
	}

	public void incrementaRecordNonImportati(){
		this.numeroRecordNonImportati++;
	}

	public void incrementaRigheLette(){
		this.numeroRigheLette++;
	}

	public void incrementaRecordAggiornati(){
		this.numeroRecordAggiornati++;
	}

	public void incrementaRecordNonAggiornatiLottiNonAggiudicati(){
		this.numeroRecordNonAggiornatiLottiNonAggiudicati++;
	}

	public void incrementaRecordNonAggiornati(){
      this.numeroRecordNonAggiornati++;
  }

	public List getListaMsgVerificaFoglio(){
		return this.listaMsgVerificaFoglio;
	}

	public List getListaMessaggiErrore(){
			return this.listaErroriImport;
	}

	public List getListaMessaggiUnitaMisura(){
		return this.listaMessaggiUnitaMisura;
	}

	public int getNumeroRecordImportati(){
		return this.numeroRecordImportati;
	}

	public int getNumeroRecordNonImportati(){
		return this.numeroRecordNonImportati;
	}

	public int getNumeroRigheLette(){
		return this.numeroRigheLette;
	}

	public int getNumeroRecordAggiornati(){
		return this.numeroRecordAggiornati;
	}

	public int getNumeroRecordNonAggiornati(){
		return this.numeroRecordNonAggiornati;
	}

	public int getNumeroLottiCreati() {
		return numeroLottiCreati;
	}

	public int getNumeroRecordNonAggiornatiLottiNonAggiudicati() {
      return numeroRecordNonAggiornatiLottiNonAggiudicati;
  }

	public void incrementaNumeroLottiCreati(){
		this.numeroLottiCreati++;
	}
}