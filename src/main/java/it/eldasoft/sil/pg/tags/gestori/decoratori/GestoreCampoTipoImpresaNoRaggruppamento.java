/*
 * Created on 18/06/2018
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
 * Gestore del campo TIPIMP in cui vengono esclusi i valori 3 e 10
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoTipoImpresaNoRaggruppamento extends AbstractGestoreCampoTabellato {

    public GestoreCampoTipoImpresaNoRaggruppamento() {
	super(false, "T100");
    }

    @Override
    public SqlSelect getSql() {
      return new SqlSelect("Select tab1tip,tab1desc from tab1 where tab1cod = ? and tab1tip<>3 and tab1tip<>10  order by tab1nord", new Object[] { "Ag008" });
    }
}
