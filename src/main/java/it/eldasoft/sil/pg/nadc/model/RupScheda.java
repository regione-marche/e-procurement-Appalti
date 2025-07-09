package it.eldasoft.sil.pg.nadc.model;


public class RupScheda {
  private String codice_fiscale;
  private String nome;
  private String cognome;
  
  public String getCodice_fiscale() {
    return codice_fiscale;
  }
  
  public void setCodice_fiscale(String codice_fiscale) {
    this.codice_fiscale = codice_fiscale;
  }
  
  public String getNome() {
    return nome;
  }
  
  public void setNome(String nome) {
    this.nome = nome;
  }
  
  public String getCognome() {
    return cognome;
  }
  
  public void setCognome(String cognome) {
    this.cognome = cognome;
  }

  public RupScheda() {
    
  }

  public RupScheda(String codice_fiscale, String nome, String cognome) {
    super();
    this.codice_fiscale = codice_fiscale;
    this.nome = nome;
    this.cognome = cognome;
  }

  public RupScheda deepCopy() {
      RupScheda copy = new RupScheda();
      copy.setCodice_fiscale(this.codice_fiscale);
      copy.setNome(this.nome);
      copy.setCognome(this.cognome);
      return copy;
  }
  
  
}
