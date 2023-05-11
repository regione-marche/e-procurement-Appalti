/*
 * Created on 04/09/13
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.decoratori;

import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato;

/**
 * Gestore per il campo ANTICORLOTTI.SCELTACONTR
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoSceltacontr extends AbstractGestoreCampoTabellato {

  public GestoreCampoSceltacontr() {
    super(false, "N7");
  }

  /**
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato#getSql()
   */
  @Override
  public SqlSelect getSql() {
    return new SqlSelect("select tab1tip,tab1desc from tab1 "
        + "where  tab1cod = ? and tab1tip >= ? and tab1tip <= ? "
        + "order by tab1nord,tab1tip", new Object[]{"A2044", new Long(51), new Long(89)});
  }

}
