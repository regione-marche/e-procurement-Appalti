/*
 * Created on 31-gen-2025
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.npa.model;

/**
 * Bean description:    
 * Rettifica elenco di soggetti che hanno presentato manifestazione di interesse o domanda di partecipazione.
 *
 * @author Manuel.Bridda
 */
public class AnacFormS2R extends AnacFormS2 {

    private EnumNPA motivoRichiestaRettifica;

    public EnumNPA getMotivoRichiestaRettifica() {
        return motivoRichiestaRettifica;
    }

    public void setMotivoRichiestaRettifica(EnumNPA motivoRichiestaRettifica) {
        this.motivoRichiestaRettifica = motivoRichiestaRettifica;
    }
}