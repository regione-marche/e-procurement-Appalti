package it.eldasoft.sil.pg.ws.rest.durc.verificaRegolarita.entity;

public class RispostaVerificaRegolarita {
  private String idRichiestaApplicazione;
  private RispostaCfVerificaRegolarita rispostaCfVerificaRegolarita;
  
  public String getIdRichiestaApplicazione() {
    return idRichiestaApplicazione;
  }
  
  public void setIdRichiestaApplicazione(String idRichiestaApplicazione) {
    this.idRichiestaApplicazione = idRichiestaApplicazione;
  }
  
  public RispostaCfVerificaRegolarita getRispostaCfVerificaRegolarita() {
    return rispostaCfVerificaRegolarita;
  }
  
  public void setRispostaCfVerificaRegolarita(RispostaCfVerificaRegolarita rispostaCfVerificaRegolarita) {
    this.rispostaCfVerificaRegolarita = rispostaCfVerificaRegolarita;
  }
  
  public RispostaVerificaRegolarita() {
    
  }
}
