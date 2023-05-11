/*
 * Created on 30/04/12
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
 * Gestore del campo fittizzio RTI per indicare se si sta inserendo in gare una RTI (di imrese
 * o di professionist) oppure no
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoSiNoRTI extends AbstractGestoreCampo {

  public String gestisciDaTrova(Vector params, DataColumn col, String conf,
      SqlManager manager) {

    return null;
  }

  public String getClasseEdit() {

    return null;
  }

  public String getClasseVisua() {

    return null;
  }

  public String getHTML(boolean visualizzazione, boolean abilitato) {

    return null;
  }

  public String getValore(String valore) {

    return null;
  }

  public String getValorePerVisualizzazione(String valore) {

    return null;
  }

  public String getValorePreUpdateDB(String valore) {

    return null;
  }

  protected void initGestore() {
    this.getCampo().setTipo("ET2");
    this.getCampo().getValori().clear();
    this.getCampo().addValore("0", "No");
    this.getCampo().addValore("3", "Si, di imprese");
    this.getCampo().addValore("10", "Si, di professionisti");
  }

  public String postHTML(boolean visualizzazione, boolean abilitato) {

    return null;
  }

  public String preHTML(boolean visualizzazione, boolean abilitato) {

    return null;
  }

}
