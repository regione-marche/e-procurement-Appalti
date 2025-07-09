package it.eldasoft.sil.pg.nadc.model;

public class DatiContratto {
    private String cig;
    private String numero_contratto;
    private String operazione;
    
    public String getCig() {
      return cig;
    }
    
    public void setCig(String cig) {
      this.cig = cig;
    }
    
    public String getNumero_contratto() {
      return numero_contratto;
    }
    
    public void setNumero_contratto(String numero_contratto) {
      this.numero_contratto = numero_contratto;
    }
    
    public String getOperazione() {
      return operazione;
    }
    
    public void setOperazione(String operazione) {
      this.operazione = operazione;
    }
    
    public DatiContratto() {
      
    }

    public DatiContratto(String cig, String numero_contratto, String operazione) {
      this.cig = cig;
      this.numero_contratto = numero_contratto;
      this.operazione = operazione;
    }
    
}
