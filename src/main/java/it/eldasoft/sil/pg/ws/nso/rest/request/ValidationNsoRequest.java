package it.eldasoft.sil.pg.ws.nso.rest.request;

import java.io.InputStream;

public class ValidationNsoRequest {
  protected String orderId;
  private InputStream orderXml;
  
  public String getOrderId() {
    return orderId;
  }
  
  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }
  
  public InputStream getOrderXml() {
    return orderXml;
  }
  
  public void setOrderXml(InputStream orderXml) {
    this.orderXml = orderXml;
  }

  @Override
  public String toString() {
    return "ValidationNsoRequest [orderId=" + orderId + "]";
  }
  
}
