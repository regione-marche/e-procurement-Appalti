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

import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato;

/**
 * Gestore per il campo unimis.gcap come tabellato dalla UNIMIS 
 * 
 * @author Marcello Caminiti
 */
public class GestoreCampoUnitaMisura extends AbstractGestoreCampoTabellato {

  public GestoreCampoUnitaMisura() {
    super(false, "T12");
  }

  /**
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato#getSql()
   */
  public SqlSelect getSql() {
    return new SqlSelect("select tipo,desuni from unimis "
        + "where conta = -1  "
        + "order by desuni", null);
  }

}
