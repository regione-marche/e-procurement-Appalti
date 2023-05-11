/*
 * Created on 12/05/14
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
import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * Gestore dei campi fittizzi associati al tabellato A1098. Se tramite profilo è nascosto il campo
 * relativo al punto di contatto, viene eliminata la voce "Punto di contatto". Se è nascosto il campo
 * relativo al luogo, viene elminata la voce "Altro (specificare)"
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoPresso extends GestoreTabellatoNoOpzioneVuota {

  @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {
    // si elimina l'opzione vuota
    super.preHTML(visualizzazione, abilitato);

    if(!visualizzazione){
      String nomeCampo = this.campo.getNome();
      String campoPuntoContatto=null;
      String campoLuogo=null;
      String entita=null;
      if("PCOPRE_FIT".equals(nomeCampo)){
        campoPuntoContatto = "PCOPRE";
        campoLuogo = "LOCPRE";
        entita ="TORN";
      }else if("PCODOC_FIT".equals(nomeCampo)){
        campoPuntoContatto = "PCODOC";
        campoLuogo = "DOCGAR";
        entita ="TORN";
      }else if("PCOOFF_FIT".equals(nomeCampo)){
        campoPuntoContatto = "PCOOFF";
        campoLuogo = "LOCOFF";
        entita ="TORN";
      }else if("PCOGAR_FIT".equals(nomeCampo)){
        campoPuntoContatto = "PCOGAR";
        campoLuogo = "LOCGAR";
        entita ="TORN";
      }else if("PCOESE_FIT".equals(nomeCampo)){
        campoPuntoContatto = "PCOESE";
        campoLuogo = "LOCESE";
        entita ="GARECONT";
      }else if("PCOFAT_FIT".equals(nomeCampo)){
        campoPuntoContatto = "PCOFAT";
        campoLuogo = "LOCFAT";
        entita ="GARECONT";
      }


      boolean campoPuntoContattoVisibile = UtilityTags.checkProtection(this.getPageContext(),
          "COLS.VIS.GARE."+ entita + "." + campoPuntoContatto, true);
      boolean campoLuogoVisibile = UtilityTags.checkProtection(this.getPageContext(),
          "COLS.VIS.GARE." + entita + "." + campoLuogo, true);

      ValoreTabellato opzioneDaEliminare = null;
      int posizioneElemento =0;
      if(!campoPuntoContattoVisibile){
        opzioneDaEliminare = new ValoreTabellato("2", "Punto di contatto");
        posizioneElemento = this.getCampo().getValori().indexOf(opzioneDaEliminare);
        if (posizioneElemento >= 0)
          this.getCampo().getValori().remove(posizioneElemento);
      }

      if(!campoLuogoVisibile){
        opzioneDaEliminare = new ValoreTabellato("3", "Altro (specificare)");
        posizioneElemento = this.getCampo().getValori().indexOf(opzioneDaEliminare);
        if (posizioneElemento >= 0)
          this.getCampo().getValori().remove(posizioneElemento);
      }
    }



    return null;
  }
}
