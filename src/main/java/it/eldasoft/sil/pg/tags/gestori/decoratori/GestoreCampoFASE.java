/*
 * Created on 30/05/13
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

import javax.servlet.jsp.PageContext;

/**
 * Gestore del campo GARSED.FASE, se TORN.ITERGA!=2 si deve nascondere il valore 1,
 * se GARE.CRITLICG!=2( TORN.CRITLIC!=2 per offerta unica) si nasconde il valore 5
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoFASE extends GestoreTabellatoNoOpzioneVuota {

  @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {

    Long iterga = (Long) this.getPageContext().getAttribute("iterga",
        PageContext.REQUEST_SCOPE);

    Long critlic = (Long) this.getPageContext().getAttribute("critlic",
        PageContext.REQUEST_SCOPE);

    String compreq = (String) this.getPageContext().getAttribute("compreq",
        PageContext.REQUEST_SCOPE);

    String valtec = (String) this.getPageContext().getAttribute("valtec",
        PageContext.REQUEST_SCOPE);

      ValoreTabellato opzione = new ValoreTabellato("1", "Apertura domande di partecipazione");
      int posizioneOpzione = this.getCampo().getValori().indexOf(opzione);

      // si elimina l'opzione "1" se iterga!=2
      if (posizioneOpzione >= 0 && !((new Long(2)).equals(iterga) || (new Long(4)).equals(iterga)))
         this.getCampo().getValori().remove(posizioneOpzione);

      //Si elimina l'opzione '3' se compreq non è 1
      opzione = new ValoreTabellato("3", "Verifica ex art. 48 comma 1 DLgs 163/06");
      posizioneOpzione = this.getCampo().getValori().indexOf(opzione);
      if (posizioneOpzione >= 0 && !"1".equals(compreq))
        this.getCampo().getValori().remove(posizioneOpzione);

      //Si elimina l'opzione '4' se compreq non è 1
      opzione = new ValoreTabellato("4", "Esito verifiche ex art. 48 comma 1 DLgs 163/06");
      posizioneOpzione = this.getCampo().getValori().indexOf(opzione);
      if (posizioneOpzione >= 0 && !"1".equals(compreq))
        this.getCampo().getValori().remove(posizioneOpzione);


      opzione = new ValoreTabellato("5", "Valutazione offerte tecniche");
      posizioneOpzione = this.getCampo().getValori().indexOf(opzione);

      // si elimina l'opzione "5" se critlic!=2
      if (posizioneOpzione >= 0 && !(new Long(2)).equals(critlic) && !"1".equals(valtec))
         this.getCampo().getValori().remove(posizioneOpzione);



    return null;
  }
}
