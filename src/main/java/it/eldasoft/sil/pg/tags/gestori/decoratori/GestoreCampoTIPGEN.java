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

import it.eldasoft.gene.tags.decorators.campi.ValoreTabellato;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreTabellatoNoOpzioneVuota;

/**
 * Gestore del campo tipo appalto della tabella torn. Questo gestore elimina la
 * voce vuota in modifica, ed inoltre toglie la voce "Lavori" se l'appalto non è
 * di tipo "Lavori", mentre toglie tutte le altre voci se è proprio "Lavori"
 * 
 * @author Stefano.Sabbadin
 */
public class GestoreCampoTIPGEN extends GestoreTabellatoNoOpzioneVuota {

  public String preHTML(boolean visualizzazione, boolean abilitato) {
    // si elimina l'opzione vuota
    super.preHTML(visualizzazione, abilitato);

    String valore = this.campo.getValue();
    if (!"".equals(valore)) {
      ValoreTabellato opzioneLavori = new ValoreTabellato("1", "Lavori");
      int posizioneLavori = this.getCampo().getValori().indexOf(opzioneLavori);
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
    }
    return null;
  }
}
