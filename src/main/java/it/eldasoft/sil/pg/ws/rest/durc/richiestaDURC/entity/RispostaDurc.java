package it.eldasoft.sil.pg.ws.rest.durc.richiestaDURC.entity;

public class RispostaDurc {
  private String idRichiestaApplicazione;
  private RispostaCfRichiestaDurc rispostaCfRichiestaDurc;
  
  public String getIdRichiestaApplicazione() {
    return idRichiestaApplicazione;
  }
  
  public void setIdRichiestaApplicazione(String idRichiestaApplicazione) {
    this.idRichiestaApplicazione = idRichiestaApplicazione;
  }
  
  public RispostaCfRichiestaDurc getRispostaCfRichiestaDurc() {
    return rispostaCfRichiestaDurc;
  }

  public void setRispostaCfRichiestaDurc(RispostaCfRichiestaDurc rispostaCfRichiestaDurc) {
    this.rispostaCfRichiestaDurc = rispostaCfRichiestaDurc;
  }

  public RispostaDurc() {
    
  }
}
