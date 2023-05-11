package it.eldasoft.sil.pg.ws.rest.dgue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Classe dto per la tabella tecni per dgue
 * @author gabriele.nencini
 *
 */
@JsonInclude(value = Include.NON_EMPTY)
public class TecniDgueDto {
  private String nome;
  private String telefono;
  private String fax;
  private String email;
  
  /**
   * @return the nome
   */
  public String getNome() {
    return nome;
  }
  
  /**
   * @param nome the nome to set
   */
  public void setNome(String nome) {
    this.nome = nome;
  }
  
  /**
   * @return the telefono
   */
  public String getTelefono() {
    return telefono;
  }
  
  /**
   * @param telefono the telefono to set
   */
  public void setTelefono(String telefono) {
    this.telefono = telefono;
  }
  
  /**
   * @return the fax
   */
  public String getFax() {
    return fax;
  }
  
  /**
   * @param fax the fax to set
   */
  public void setFax(String fax) {
    this.fax = fax;
  }
  
  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }
  
  /**
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }
  
  
}
