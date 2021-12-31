/*
 * Created on 04/07/12
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
 * In modifica vengono esclusi i valori del tabellato A2054 compresi fra 98 e 101
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoMOTIVESCL extends GestoreTabellatoNoOpzioneVuota {

  public String preHTML(boolean visualizzazione, boolean abilitato) {


      String valore = this.campo.getValue();
      if (valore== null || "".equals(valore) || (!"".equals(valore) && !"98".equals(valore) && !"99".equals(valore) )) {
        ValoreTabellato opzioneEsclusione = new ValoreTabellato("98", "Impresa vincitrice della gara");
        int posizione = this.getCampo().getValori().indexOf(opzioneEsclusione);

        // si elimina l'opzione "98"
        if (posizione >= 0)
           this.getCampo().getValori().remove(posizione);

        opzioneEsclusione = new ValoreTabellato("99", "Impresa vincitrice di un'altra gara");
        posizione = this.getCampo().getValori().indexOf(opzioneEsclusione);

        // si elimina anche l'opzione "99"
        if (posizione >= 0)
           this.getCampo().getValori().remove(posizione);

        if(!"100".equals(valore)){
          opzioneEsclusione = new ValoreTabellato("100", "Impresa riammessa alla gara");
          posizione = this.getCampo().getValori().indexOf(opzioneEsclusione);

          // si elimina anche l'opzione "100"
          if (posizione >= 0)
             this.getCampo().getValori().remove(posizione);
        }

        if( !"101".equals(valore)){
          opzioneEsclusione = new ValoreTabellato("101", "Impresa riammessa alla gara e aveva vinto");
          posizione = this.getCampo().getValori().indexOf(opzioneEsclusione);

          // si elimina anche l'opzione "101"
          if (posizione >= 0)
             this.getCampo().getValori().remove(posizione);
        }




      }



    return null;
  }
}
