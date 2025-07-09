package it.eldasoft.sil.pg.ws.rest.eforms.exceptions;

/**
 * Piccola classe interna per restituire più valori
 * da handleException.
 */
public class ExceptionHandlerResult {
  private int status;
  private int livEvento;
  private String errMsgEvento;

  public ExceptionHandlerResult(int status, int livEvento, String errMsgEvento) {
    this.status = status;
    this.livEvento = livEvento;
    this.errMsgEvento = errMsgEvento;
  }

  public ExceptionHandlerResult() {
    
  }

  
  public int getStatus() {
    return status;
  }

  
  public void setStatus(int status) {
    this.status = status;
  }

  
  public int getLivEvento() {
    return livEvento;
  }

  
  public void setLivEvento(int livEvento) {
    this.livEvento = livEvento;
  }

  
  public String getErrMsgEvento() {
    return errMsgEvento;
  }

  
  public void setErrMsgEvento(String errMsgEvento) {
    this.errMsgEvento = errMsgEvento;
  }
  
  
}
