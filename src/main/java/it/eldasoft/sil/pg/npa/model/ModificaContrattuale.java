/*
 * Created on 09-gen-2024
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
 * Oggetto che riporta tutti i dati della modifica contrattuale
 *
 * @author Alex.Mancini
 */
public class ModificaContrattuale {
  
  private String dataApprovazione;
  private String urlDocumentazione;
  private EnumNPA motivoRevisionePrezzi;
  private String cigNuovaProcedura;
  private QuadroEconomicoConcessioni quadroEconomicoConcessioniRideterminato;
  private DatiBaseModificaContrattuale datiBaseModificaContrattuale;

  
  public String getDataApprovazione() {
    return dataApprovazione;
  }

  
  public void setDataApprovazione(String dataApprovazione) {
    this.dataApprovazione = dataApprovazione;
  }

  
  public String getUrlDocumentazione() {
    return urlDocumentazione;
  }

  
  public void setUrlDocumentazione(String urlDocumentazione) {
    this.urlDocumentazione = urlDocumentazione;
  }

  
  public EnumNPA getMotivoRevisionePrezzi() {
    return motivoRevisionePrezzi;
  }

  
  public void setMotivoRevisionePrezzi(EnumNPA motivoRevisionePrezzi) {
    this.motivoRevisionePrezzi = motivoRevisionePrezzi;
  }

  
  public String getCigNuovaProcedura() {
    return cigNuovaProcedura;
  }

  
  public void setCigNuovaProcedura(String cigNuovaProcedura) {
    this.cigNuovaProcedura = cigNuovaProcedura;
  }

  
  public QuadroEconomicoConcessioni getQuadroEconomicoConcessioniRideterminato() {
    return quadroEconomicoConcessioniRideterminato;
  }

  
  public void setQuadroEconomicoConcessioniRideterminato(QuadroEconomicoConcessioni quadroEconomicoConcessioniRideterminato) {
    this.quadroEconomicoConcessioniRideterminato = quadroEconomicoConcessioniRideterminato;
  }

  public DatiBaseModificaContrattuale getDatiBaseModificaContrattuale() {
    return datiBaseModificaContrattuale;
  }


  public void setDatiBaseModificaContrattuale(DatiBaseModificaContrattuale datiBaseModificaContrattuale) {
    this.datiBaseModificaContrattuale = datiBaseModificaContrattuale;
  }


  public ModificaContrattuale() {
    // TODO document why this constructor is empty
  }
  
}