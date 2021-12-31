/*
 * Created on 01/giu/2017
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.bl.scp;

import java.io.Serializable;

/**
 * Risultato pubblicazione.
 * 
 * @author Mirco.Franzoni
 */
public class PubblicazioneAttoResult extends InserimentoResult implements
		Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	private Long idExArt29;

	private Long idGara;

	public void setIdExArt29(Long idExArt29) {
		this.idExArt29 = idExArt29;
	}

	public Long getIdExArt29() {
		return idExArt29;
	}

	public void setIdGara(Long idGara) {
		this.idGara = idGara;
	}

	public Long getIdGara() {
		return idGara;
	}

}
