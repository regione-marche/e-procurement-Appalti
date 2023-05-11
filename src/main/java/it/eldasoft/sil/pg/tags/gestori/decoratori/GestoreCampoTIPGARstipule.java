/*
 * Created on 19/07/21
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
 * Gestore del campo tipo TIPGAR nel profilo delle stipule.
 *
 * @author Peruzzo Riccardo
 */
public class GestoreCampoTIPGARstipule extends AbstractGestoreCampoTabellatoArc {

  public GestoreCampoTIPGARstipule() {
    super(false, "N7");
  }


  @Override
  public SqlSelect getSql() {
    String select = "select tab1tip,tab1desc,tab1arc from tab1 where tab1cod='A2044' order by tab1nord,tab1tip";

    String descTab = "51,52,53,54,55,56,57,58,64,67,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89";
    select = "select tab1tip,tab1desc,tab1arc from tab1 where tab1cod='A2044' and tab1tip in (" + descTab + ") order by tab1nord,tab1tip";


    return new SqlSelect(select, null);
  }
}
