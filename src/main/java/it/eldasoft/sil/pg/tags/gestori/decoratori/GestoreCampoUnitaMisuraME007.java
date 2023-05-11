/*
 * Created on 10/03/14
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
 * Gestore per il tabellato ME007 di MEPA
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoUnitaMisuraME007 extends AbstractGestoreCampoTabellatoArc {

  public GestoreCampoUnitaMisuraME007() {
    super(false, "N");
  }

  /**
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato#getSql()
   */
  @Override
  public SqlSelect getSql() {
    return new SqlSelect("select TAB1TIP,TAB1DESC,TAB1ARC "
        + "FROM TAB1 WHERE TAB1COD = 'ME007'  "
        + "order by TAB1DESC", null);
  }

}
