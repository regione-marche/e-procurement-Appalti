/*
 * Created on 04/02/13
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

/**
* Gestore campo si/no con valori invertiti(si=2, no=1) e senza valore nullo
*
* @author Marcello Caminiti
*/
public class GestoreCampoSiNoInvertitoSenzaNull extends AbstractGestoreCampo {

  @Override
  public String gestisciDaTrova(Vector params, DataColumn col, String conf,
      SqlManager manager) {

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
  public String getHTML(boolean visualizzazione, boolean abilitato) {

    return null;
  }

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
  protected void initGestore() {
    this.getCampo().setTipo("ET2");
    this.getCampo().getValori().clear();
    this.getCampo().addValore("1", "No");
    this.getCampo().addValore("2", "Si");
  }

  @Override
  public String postHTML(boolean visualizzazione, boolean abilitato) {

    return null;
  }

  @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {

    return null;
  }

}
