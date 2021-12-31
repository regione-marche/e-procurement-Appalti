/*
 * Created on 01/09/10
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
 * Gestore del campo tipo pubblicazione della tabella pubbli e pubg. Questo gestore elimina la
 * voce "Portale Alice Gare" per le nuove occorrenze e per quelle in modifica
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoTIPPUB extends GestoreTabellatoNoOpzioneVuota {

  @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {

    String valore = this.campo.getValue();
    if (valore== null || "".equals(valore) || (!"".equals(valore) && !"11".equals(valore) && !"12".equals(valore) && !"13".equals(valore) && !"14".equals(valore) && !"15".equals(valore) && !"16".equals(valore)  && !"23".equals(valore))) {
      ValoreTabellato opzionePortale = new ValoreTabellato("11", "Portale Appalti");
      int posizionePortale = this.getCampo().getValori().indexOf(opzionePortale);

      // si elimina anche l'opzione "11"
      if (posizionePortale >= 0)
         this.getCampo().getValori().remove(posizionePortale);

      opzionePortale = new ValoreTabellato("12", "Portale Appalti");
      posizionePortale = this.getCampo().getValori().indexOf(opzionePortale);

      // si elimina anche l'opzione "12"
      if (posizionePortale >= 0)
         this.getCampo().getValori().remove(posizionePortale);

      opzionePortale = new ValoreTabellato("13", "Portale Appalti");
      posizionePortale = this.getCampo().getValori().indexOf(opzionePortale);

      // si elimina anche l'opzione "13"
      if (posizionePortale >= 0)
         this.getCampo().getValori().remove(posizionePortale);

      opzionePortale = new ValoreTabellato("14", "Amministrazione trasparente");
      posizionePortale = this.getCampo().getValori().indexOf(opzionePortale);

      // si elimina anche l'opzione "14"
      if (posizionePortale >= 0)
         this.getCampo().getValori().remove(posizionePortale);

      opzionePortale = new ValoreTabellato("15", "Delibera a contrarre nel portale Appalti");
      posizionePortale = this.getCampo().getValori().indexOf(opzionePortale);

      // si elimina anche l'opzione "15"
      if (posizionePortale >= 0)
         this.getCampo().getValori().remove(posizionePortale);

      opzionePortale = new ValoreTabellato("16", "Prima pubblicazione atti SCP");
      posizionePortale = this.getCampo().getValori().indexOf(opzionePortale);

      // si elimina anche l'opzione "16"
      if (posizionePortale >= 0)
         this.getCampo().getValori().remove(posizionePortale);

      opzionePortale = new ValoreTabellato("23", "Invio RDO con protocollazione in corso");
      posizionePortale = this.getCampo().getValori().indexOf(opzionePortale);

      // si elimina anche l'opzione "23"
      if (posizionePortale >= 0)
         this.getCampo().getValori().remove(posizionePortale);

    }

    return null;
  }
}
