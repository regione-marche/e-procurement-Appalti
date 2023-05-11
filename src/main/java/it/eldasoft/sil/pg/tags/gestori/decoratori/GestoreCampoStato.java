/*
 * Created on 06/12/12
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
 * Gestore del campo V_GARE_STATOESITO.STATO
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoStato extends AbstractGestoreCampo {

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
    this.getCampo().setTipo("ET17");
    this.getCampo().getValori().clear();
    this.getCampo().addValore("", "");
    this.getCampo().addValore("In corso", "In corso");
    this.getCampo().addValore("In aggiudicazione", "In aggiudicazione");
    this.getCampo().addValore("Sospesa", "Sospesa");
    this.getCampo().addValore("Conclusa", "Conclusa");
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
