/*
 * Created on 24/nov/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.w3.tags.funzioni.decoratori.campi;

import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato;

import java.util.HashMap;

import javax.servlet.jsp.PageContext;


public class GestoreCampoW3TABREQ extends AbstractGestoreCampoTabellato {

    public GestoreCampoW3TABREQ() {
        super(false, "T2000");
    }

    @Override
    public SqlSelect getSql() {
      HashMap datiRiga = (HashMap) this.getPageContext().getAttribute("datiRiga",
          PageContext.REQUEST_SCOPE);
      if (datiRiga != null) {
        return new SqlSelect("select codice_dettaglio,descrizione" +
        		" from w3tabreq " +
        		" where codice_dettaglio = ? " +
        		" order by codice_dettaglio", new Object[] { (datiRiga.get("W3GARAREQ_CODICE_DETTAGLIO")!=null)?datiRiga.get("W3GARAREQ_CODICE_DETTAGLIO"):"" });
          }
      return null;
    }
}

