/*
 * Created on 03/12/13
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
 * Gestore per il campo meiscrizprod.stato, effettua il caricamento del solo valore
 * "In attesa di verifica conformità"
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoStatoProdottiME extends AbstractGestoreCampoTabellato {

  public GestoreCampoStatoProdottiME() {
    super(false, "T12");
  }

  /**
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato#getSql()
   */
  @Override
  public SqlSelect getSql() {
    return new SqlSelect("select tab1tip,tab1desc from tab1 "
        + "where tab1cod ='ME005' and tab1tip=2 ", null);
  }

}
