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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

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
public class GestoreCampoCriterioFormula extends GestoreTabellatoNoOpzioneVuota {

  @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {
/*
    HashMap datiRiga = (HashMap) this.getPageContext().getAttribute("datiRiga",
        PageContext.REQUEST_SCOPE);

    String formato = (String) datiRiga.get("G1CRIDEF_FORMATO");

    if ("3".equals(formato)) {
      ValoreTabellato opzioneEliminare = new ValoreTabellato("1", "Elenco valori");
      int posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
      if (posizione >= 0){
        Vector<?> vector = new Vector();
        vector = this.getCampo().getValori();
        for(int i = 0; i < vector.size(); i++){
          if(i != posizione){
            vector.remove(i);
          }
        }
      }
    }else{
      if("51".equals(formato)){
        Vector<?> vector = new Vector();
        vector = this.getCampo().getValori();
        for(int i = 0; i < vector.size(); i++){
          if(i<1 || i>6)
            vector.remove(i);
          }
        }
      else{
        if ("50".equals(formato) || "52".equals(formato)) {
          ArrayList<Integer> posizioni = new ArrayList<Integer>();
          ValoreTabellato opzioneEliminare = new ValoreTabellato("2", "Elenco Range");
          int posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
          posizioni.add(posizione);
          opzioneEliminare = new ValoreTabellato("7", "Proporzionalità inversa (punteggio max. al valore offerto più basso)");
          posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
          posizioni.add(posizione);
          opzioneEliminare = new ValoreTabellato("8", "Bilineare con soglia coefficiente 0.8 (off. migliorativa decrescente)");
          posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
          posizioni.add(posizione);
          opzioneEliminare = new ValoreTabellato("9", "Bilineare con soglia coefficiente 0.85(off. migliorativa decrescente)");
          posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
          posizioni.add(posizione);
          opzioneEliminare = new ValoreTabellato("10", "Bilineare con soglia coefficiente 0.9 (off. migliorativa decrescente)");
          posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
          posizioni.add(posizione);
          if (posizione >= 0){
            Vector<?> vector = new Vector();
            vector = this.getCampo().getValori();
            for(int i = 0; i < vector.size(); i++){
              if(!posizioni.contains(i)){
                vector.remove(i);
              }
            }
          }
        }else{
          ArrayList<Integer> posizioni = new ArrayList<Integer>();
          ValoreTabellato opzioneEliminare = new ValoreTabellato("2", "Elenco Range");
          int posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
          posizioni.add(posizione);
          opzioneEliminare = new ValoreTabellato("3", "Proporzionalità diretta (punteggio max. al valore offerto più alto)");
          posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
          posizioni.add(posizione);
          opzioneEliminare = new ValoreTabellato("4", "Bilineare con soglia coefficiente 0.8 (off. migliorativa crescente)");
          posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
          posizioni.add(posizione);
          opzioneEliminare = new ValoreTabellato("5", "Bilineare con soglia coefficiente 0.85(off. migliorativa crescente)");
          posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
          posizioni.add(posizione);
          opzioneEliminare = new ValoreTabellato("6", "Bilineare con soglia coefficiente 0.9 (off. migliorativa crescente)");
          posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
          posizioni.add(posizione);
          opzioneEliminare = new ValoreTabellato("7", "Proporzionalità inversa (punteggio max. al valore offerto più basso)");
          posizione = this.getCampo().getValori().indexOf(opzioneEliminare);
          posizioni.add(posizione);
          if (posizione >= 0){
            Vector<?> vector = new Vector();
            vector = this.getCampo().getValori();
            int dim = vector.size();
            for(int i = dim-1; i > 0; i--){
              if(!posizioni.contains(i)){
                vector.remove(i);
              }
            }
          }
        }
      }
    }
    */
    return null;
    
  }
}
