/*
 * Created on 10/Set/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.decoratori;

import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellatoArc;

/**
 * Gestore per il campo TORN.MODREA
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoModrea extends AbstractGestoreCampoTabellatoArc {

  public GestoreCampoModrea() {
    super(false, "T12");
  }

  /**
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato#getSql()
   */
  @Override
  public SqlSelect getSql() {
    return new SqlSelect("select tab2tip,tab2d1, tab2arc from tab2 "
        + "where tab2cod='A1z06' order by tab2nord", null);
  }

}
