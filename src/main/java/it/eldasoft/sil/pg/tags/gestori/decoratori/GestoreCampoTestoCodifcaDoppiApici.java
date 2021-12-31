/*
 * Created on 09/10/18
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.decoratori;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;

import java.util.Vector;

/**
 * Gestore da applicare ai campi di testo per codificare il carattere " in &#34;
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoTestoCodifcaDoppiApici extends AbstractGestoreCampo {

	@Override
  public String gestisciDaTrova(Vector params, DataColumn colWithValue,
			String conf, SqlManager manager) {
		return null;
	}

	@Override
  public String getClasseEdit() {
		return null;
	}

	@Override
  public String getClasseVisua() {
		return null;
	}



	@Override
  public String getValore(String valore) {
	  String ret=null;
	  //Se nella stringa sono presenti dei caratteri ", allora vengono convertiti in notazione html &#34;
	  if(valore.indexOf("\"")>0){
	    valore=valore.replaceAll("\"", "&#34;");
	    ret=valore;
	  }
      return ret;
	}


	@Override
  public String getValorePerVisualizzazione(String valore) {
		return null;
	}

	@Override
  public String getValorePreUpdateDB(String valore) {
		return null;
	}

	@Override
  protected void initGestore() {

	}

	@Override
  public String postHTML(boolean visualizzazione, boolean abilitato) {
		return null;
	}

	@Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {
		return null;
	}


  @Override
  public String getHTML(boolean visualizzazione, boolean abilitato) {

    return null;
  }

}