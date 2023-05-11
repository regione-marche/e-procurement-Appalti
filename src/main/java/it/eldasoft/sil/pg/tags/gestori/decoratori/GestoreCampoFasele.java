/*
 * Created on 22/08/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.decoratori;

import java.util.Vector;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.gene.tags.decorators.campi.ValoreTabellato;
import it.eldasoft.sil.pg.bl.PgManagerEst1;

/**
 * Gestore per il campo DOCUMGARA.FASELE
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoFasele extends AbstractGestoreCampo {

  @Override
  public String getValore(String valore) {

    return null;
  }

  @Override
  public String getValorePerVisualizzazione(String valore) {

    return null;
  }

  @Override
  public String getValorePreUpdateDB(String valore) {

    return null;
  }

  @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {

    if(abilitato) {
      String fasEle = (String) this.getPageContext().getAttribute("fasEle");

      ValoreTabellato opzione = new ValoreTabellato("3", "Rinnovo");
      int posizioneOpzione = this.getCampo().getValori().indexOf(opzione);

      // si elimina l'opzione "3" nel caso di iscrizione
      if (posizioneOpzione >= 0 && PgManagerEst1.ELENCO_DOC_ISCRIZIONE.equalsIgnoreCase(fasEle))
         this.getCampo().getValori().remove(posizioneOpzione);

      // si eliminano le opzione "1" e "2" nel caso di Rinnovo
      if (PgManagerEst1.ELENCO_DOC_RINNOVO.equalsIgnoreCase(fasEle)) {
        opzione = new ValoreTabellato("1", "Iscrizione");
        posizioneOpzione = this.getCampo().getValori().indexOf(opzione);
        if (posizioneOpzione >= 0 )
          this.getCampo().getValori().remove(posizioneOpzione);

        opzione = new ValoreTabellato("2", "Iscrizione e rinnovo");
        posizioneOpzione = this.getCampo().getValori().indexOf(opzione);
        if (posizioneOpzione >= 0 )
          this.getCampo().getValori().remove(posizioneOpzione);
      }
    }
      return null;
  }

  @Override
  public String getHTML(boolean visualizzazione, boolean abilitato) {

    return null;
  }

  @Override
  public String postHTML(boolean visualizzazione, boolean abilitato) {

    return null;
  }

  @Override
  public String getClasseEdit() {

    return null;
  }

  @Override
  public String getClasseVisua() {

    return null;
  }

  @Override
  protected void initGestore() {


  }

  @Override
  public String gestisciDaTrova(Vector params, DataColumn col, String conf, SqlManager manager) {

    return null;
  }


}
