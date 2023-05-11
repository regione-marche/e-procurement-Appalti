package it.eldasoft.sil.pg.tags.gestori.decoratori;

import it.eldasoft.gene.tags.decorators.campi.ValoreTabellato;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreTabellatoNoOpzioneVuota;


public class GestoreCampoBustaQform extends GestoreTabellatoNoOpzioneVuota {

  @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {


      ValoreTabellato opzione = new ValoreTabellato("2", "Offerta tecnica");
      int posizione = this.getCampo().getValori().indexOf(opzione);

      if (posizione >= 0)
         this.getCampo().getValori().remove(posizione);

      opzione = new ValoreTabellato("3", "Offerta economica");
      posizione = this.getCampo().getValori().indexOf(opzione);

      if (posizione >= 0)
         this.getCampo().getValori().remove(posizione);



    return null;
  }
}
