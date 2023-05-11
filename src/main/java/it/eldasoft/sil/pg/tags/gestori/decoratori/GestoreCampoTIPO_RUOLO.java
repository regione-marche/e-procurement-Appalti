/*
 * Created on 29/lug/10
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
 * Gestore del campo tipo ruolo. Questo gestore estrae la lista dei ruoli ammissibili 
 * per quanto riguarda i "Ruoli Nominativi componenti commissione"
 * 
 * @author roberto.marcon
 */
public class GestoreCampoTIPO_RUOLO extends AbstractGestoreCampoTabellatoArc {

    public GestoreCampoTIPO_RUOLO() {
	super(false, "N2");
    }

    @Override
    public SqlSelect getSql() {
	return new SqlSelect("Select tab1tip, tab1desc, tab1arc from tab1 where tab1cod = ? order by tab1tip", new Object[] { "A1001" });
    }
}
