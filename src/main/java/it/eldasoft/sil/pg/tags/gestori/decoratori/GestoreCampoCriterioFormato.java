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
public class GestoreCampoCriterioFormato extends GestoreTabellatoNoOpzioneVuota {

  @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {

    HashMap datiRiga = (HashMap) this.getPageContext().getAttribute("datiRiga",
        PageContext.REQUEST_SCOPE);

    String tippar = (String) datiRiga.get("GOEV_TIPPAR");

    if ("2".equals(tippar)) {

      ValoreTabellato opzioneEliminare = new ValoreTabellato("1", "Data");
      int posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
      if (posizione >= 0)
        this.getCampo().getValori().remove(posizione);
      
      opzioneEliminare = new ValoreTabellato("4", "Testo");
      posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
      if (posizione >= 0)
        this.getCampo().getValori().remove(posizione);
      
    }else{
      if("1".equals(tippar)){
        
        ValoreTabellato opzioneEliminare = new ValoreTabellato("50", "Offerta complessiva espressa mediante importo");
        int posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
        if (posizione >= 0)
          this.getCampo().getValori().remove(posizione);
        
        opzioneEliminare = new ValoreTabellato("51", "Offerta complessiva espressa mediante ribasso");
        posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
        if (posizione >= 0)
          this.getCampo().getValori().remove(posizione);
        
        opzioneEliminare = new ValoreTabellato("52", "Offerta complessiva espressa mediante prezzzi unitari");
        posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
        if (posizione >= 0)
          this.getCampo().getValori().remove(posizione);
      }
    }
    return null;
  }
}
