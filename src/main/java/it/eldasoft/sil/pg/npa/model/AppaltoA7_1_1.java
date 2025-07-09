/*
 * Created on 06-feb-2024
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
 * Oggetto che riporta i dati di aggiudicazione per procedura negoziata senza bando pari o sopra soglia.
 *
 * @author Alex.Mancini
 */
public class AppaltoA7_1_1 {

  private EnumNPA motivoUrgenza;
  private String linkDocumenti;
  private Boolean relazioneUnicaSulleProcedure;
  private Boolean opereUrbanizzazioneScomputo;

  
  public EnumNPA getMotivoUrgenza() {
    return motivoUrgenza;
  }

  
  public void setMotivoUrgenza(EnumNPA motivoUrgenza) {
    this.motivoUrgenza = motivoUrgenza;
  }

  
  public String getLinkDocumenti() {
    return linkDocumenti;
  }

  
  public void setLinkDocumenti(String linkDocumenti) {
    this.linkDocumenti = linkDocumenti;
  }

  
  public Boolean getRelazioneUnicaSulleProcedure() {
    return relazioneUnicaSulleProcedure;
  }

  
  public void setRelazioneUnicaSulleProcedure(Boolean relazioneUnicaSulleProcedure) {
    this.relazioneUnicaSulleProcedure = relazioneUnicaSulleProcedure;
  }

  
  public Boolean getOpereUrbanizzazioneScomputo() {
    return opereUrbanizzazioneScomputo;
  }

  
  public void setOpereUrbanizzazioneScomputo(Boolean opereUrbanizzazioneScomputo) {
    this.opereUrbanizzazioneScomputo = opereUrbanizzazioneScomputo;
  }

  public AppaltoA7_1_1() {
    // TODO document why this constructor is empty
  }
  
}