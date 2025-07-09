package it.eldasoft.sil.pg.nadc.model;


public class Rup {
  private String codiceFiscale;
  private String nome;
  private String cognome;
  private String email;
  private String matricola;
  private String direzione;
  private String sottostruttura;
  private String password;
  
  public String getCodiceFiscale() {
    return codiceFiscale;
  }
  
  public void setCodiceFiscale(String codiceFiscale) {
    this.codiceFiscale = codiceFiscale;
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
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }
  
  public String getMatricola() {
    return matricola;
  }
  
  public void setMatricola(String matricola) {
    this.matricola = matricola;
  }
  
  public String getDirezione() {
    return direzione;
  }
  
  public void setDirezione(String direzione) {
    this.direzione = direzione;
  }
  
  public String getSottostruttura() {
    return sottostruttura;
  }
  
  public void setSottostruttura(String sottostruttura) {
    this.sottostruttura = sottostruttura;
  }
  
  public String getPassword() {
    return password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public Rup() {
    
  }

  public Rup(String codiceFiscale, String nome, String cognome, String email, String matricola, String direzione, String sottostruttura,
      String password) {
    this.codiceFiscale = codiceFiscale;
    this.nome = nome;
    this.cognome = cognome;
    this.email = email;
    this.matricola = matricola;
    this.direzione = direzione;
    this.sottostruttura = sottostruttura;
    this.password = password;
  }
  
  
}
