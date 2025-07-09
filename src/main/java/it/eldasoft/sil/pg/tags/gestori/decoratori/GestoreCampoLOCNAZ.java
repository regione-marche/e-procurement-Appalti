/*
 * Created on 21/06/24
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
import it.eldasoft.gene.tags.decorators.campi.ValoreTabellato;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreTabellatoNoOpzioneVuota;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.PageContext;
import java.util.HashMap;

/**
 * Gestore del campo GARE1_LOCNAZ associato al tabellato Ag010.
 * Vengono rimossi dai possibili valori quelli a cui non è associato un codice ISTAT nella tabella G_NAZIONI
 * @author Manuel Bridda
 */
public class GestoreCampoLOCNAZ extends AbstractGestoreCampoTabellatoArc {

  public GestoreCampoLOCNAZ() {
    super(false, "N7");
  }

  /**
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato#getSql()
   */
  @Override
  public SqlSelect getSql() {

    String select = "select tab1tip,tab1desc,tab1arc from tab1, g_nazioni where tab1cod='Ag010'" +
            " and tab1tip=codice and codistat is not null"+
            " order by tab1nord,tab1tip";

    return new SqlSelect(select, null);
  }
}
