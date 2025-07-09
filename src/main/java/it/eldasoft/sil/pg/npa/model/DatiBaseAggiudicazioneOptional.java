/*
 * Created on 13-nov-2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.npa.model;


import it.eldasoft.sil.pg.bl.npa.NpaFunzioniManager;
import org.apache.commons.lang.StringUtils;

/**
 * Bean description:    
 * Dato in comune con il Business Group Award Criterion delle eforms
 *
 * @author Alex.Mancini
 * 
 * @property
 * private EnumNPA criteriAggiudicazione;
 */
public class DatiBaseAggiudicazioneOptional {

  private EnumNPA criteriAggiudicazione;
  
  
  public EnumNPA getCriteriAggiudicazione() {
    return criteriAggiudicazione;
  }

  
  public void setCriteriAggiudicazione(EnumNPA criteriAggiudicazione) {
    this.criteriAggiudicazione = criteriAggiudicazione;
  }

  public DatiBaseAggiudicazioneOptional() {
    // TODO document why this constructor is empty
  }

  public DatiBaseAggiudicazioneOptional(Long critlicg, Boolean costofisso, String gartel, Long genere) {
    String criterioAggiudicazione = NpaFunzioniManager.getCriterioAggiudicazione(critlicg, costofisso, gartel, genere);
    if (StringUtils.isNotEmpty(criterioAggiudicazione))
      this.criteriAggiudicazione = new EnumNPA("criteriAggiudicazione", criterioAggiudicazione);
  }

}
