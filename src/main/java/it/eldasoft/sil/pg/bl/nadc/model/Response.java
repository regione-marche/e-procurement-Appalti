package it.eldasoft.sil.pg.bl.nadc.model;


public class Response {
  private Integer status;
  private String message;
  private ResponseData data;

  public Integer getStatus() {
      return status;
  }

  public void setStatus(Integer status) {
      this.status = status;
  }

  public String getMessage() {
      return message;
  }

  public void setMessage(String message) {
      this.message = message;
  }

  public ResponseData getData() {
      return data;
  }

  public void setData(ResponseData data) {
      this.data = data;
  }
}
