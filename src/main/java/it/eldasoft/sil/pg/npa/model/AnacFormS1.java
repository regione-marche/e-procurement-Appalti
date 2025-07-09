/*
 * Created on 05-ott-2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.npa.model;

import java.util.List;

/**
 * Bean description:    
 * Elenco di soggetti che hanno presentato manifestazione di interesse o domanda di partecipazione. 
 *
 * @author Alex.Mancini
 */
public class AnacFormS1 {

  private List<ElencoSoggettiRichiedenti> elencoSoggettiRichiedenti;
  
  public List<ElencoSoggettiRichiedenti> getElencoSoggettiRichiedenti() {
    return elencoSoggettiRichiedenti;
  }

  public void setElencoSoggettiRichiedenti(List<ElencoSoggettiRichiedenti> elencoSoggettiRichiedenti) {
    this.elencoSoggettiRichiedenti = elencoSoggettiRichiedenti;
  }

  public AnacFormS1() {
    // TODO document why this constructor is empty
  }
  
}