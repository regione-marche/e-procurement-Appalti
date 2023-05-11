/*
 * Created on 16/09/15
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
 * Gestore del campo W_INVCOM.COMSTATO, vengono eliminati i valori la cui descrizione comincia con EVENTO
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoCOMSTATO extends GestoreTabellatoNoOpzioneVuota {

  @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {

    ValoreTabellato opzione = new ValoreTabellato("5", "Evento da processare");
    int posizione = this.getCampo().getValori().indexOf(opzione);

    if (posizione >= 0)
       this.getCampo().getValori().remove(posizione);

    opzione = new ValoreTabellato("6", "Evento processato correttamente");
    posizione = this.getCampo().getValori().indexOf(opzione);

    if (posizione >= 0)
       this.getCampo().getValori().remove(posizione);

    opzione = new ValoreTabellato("7", "Evento processato con messaggi");
    posizione = this.getCampo().getValori().indexOf(opzione);

    if (posizione >= 0)
       this.getCampo().getValori().remove(posizione);

    opzione = new ValoreTabellato("8", "Evento processato e scartato");
    posizione = this.getCampo().getValori().indexOf(opzione);

    if (posizione >= 0)
       this.getCampo().getValori().remove(posizione);

    opzione = new ValoreTabellato("9", "Evento da protocollare");
    posizione = this.getCampo().getValori().indexOf(opzione);

    if (posizione >= 0)
       this.getCampo().getValori().remove(posizione);

    opzione = new ValoreTabellato("13", "Evento da processare dopo correzione dati");
    posizione = this.getCampo().getValori().indexOf(opzione);

    if (posizione >= 0)
       this.getCampo().getValori().remove(posizione);

    return null;
  }
}
