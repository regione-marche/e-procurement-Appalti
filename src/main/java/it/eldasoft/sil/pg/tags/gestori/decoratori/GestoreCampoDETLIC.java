/*
 * Created on 05/nov/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.decoratori;

import java.util.HashMap;

import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.tags.decorators.campi.ValoreTabellato;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreTabellatoNoOpzioneVuota;

/**
 * Gestore del campo tipo appalto della tabella torn. Questo gestore elimina la
 * voce vuota in modifica, ed inoltre toglie la voce "Lavori" se l'appalto non è
 * di tipo "Lavori", mentre toglie tutte le altre voci se è proprio "Lavori"
 *
 * @author Stefano.Sabbadin
 */
public class GestoreCampoDETLIC extends GestoreTabellatoNoOpzioneVuota {

  @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {

    HashMap datiRiga = (HashMap) this.getPageContext().getAttribute("datiRiga",
        PageContext.REQUEST_SCOPE);

    String tipgen = (String) datiRiga.get("TORN_TIPGEN");

    if ("1".equals(tipgen)) {
      //Si elimina l'opzione "Ribasso percentuale"
      ValoreTabellato opzioneEliminare = new ValoreTabellato("5", "Ribasso percentuale");
      int posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
      /*
      if ("1".equals(valore)) {
        // si eliminano anche tutte le opzioni diverse da "Lavori"
        for (int i = this.getCampo().getValori().size() - 1; i >= 0; i--) {
          if (i != posizioneLavori) this.getCampo().getValori().remove(i);
        }
      } else {
        // si elimina anche l'opzione di appalto per "Lavori"
        if (posizioneLavori >= 0)
          this.getCampo().getValori().remove(posizioneLavori);
      }
      */
      if (posizione >= 0)
        this.getCampo().getValori().remove(posizione);
    }else{
      String valoreCampo = this.campo.getValue();
      //Si elimina l'opzione "Ribasso sull'elenco prezzi posto a base di gara"
      ValoreTabellato opzioneEliminare = new ValoreTabellato("1", "Ribasso sull'elenco prezzi posto a base di gara");
      int posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
      if (posizione >= 0){
	      if (valoreCampo== null || "".equals(valoreCampo) || (!"".equals(valoreCampo) && !"1".equals(valoreCampo))) {
		        this.getCampo().getValori().remove(posizione);
	      }
      }
      //Si elimina l'opzione "Ribasso sull'elenco prezzi posto a base di gara"
      opzioneEliminare = new ValoreTabellato("2", "Ribasso sull'importo posto a base di gara");
      posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
      if (posizione >= 0){
	      if (valoreCampo== null || "".equals(valoreCampo) || (!"".equals(valoreCampo) && !"2".equals(valoreCampo))) {
		        this.getCampo().getValori().remove(posizione);
	      }
      }
    }
    return null;
  }
}
