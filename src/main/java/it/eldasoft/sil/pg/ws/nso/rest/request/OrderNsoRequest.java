package it.eldasoft.sil.pg.ws.nso.rest.request;

/**
 * This class represent the request of an Order to be transmitted to NSO
 * @author gabriele.nencini
 *
 */
public class OrderNsoRequest extends ValidationNsoRequest{
  private String orderCode;
  private String orderDate;
  private String fileName;
  private String endPoint;
  private String orderExpiryDate;
  private String linkedOrderCode;
  private Long linkedOrderId;
  private String rootOrderCode;
  private Long rootOrderId;
  private Boolean hasAttachment;
  private String codimp;
  private String uffint;
  private Double totalPriceWithVat;
  private String cig;
  private String ngara;
  //specific fields for celeris api
  private String sender;
  private String receiver;
  
  public String getOrderCode() {
    return orderCode;
  }
  
  public void setOrderCode(String orderCode) {
    this.orderCode = orderCode;
  }
  
  public String getOrderDate() {
    return orderDate;
  }
  
  public void setOrderDate(String orderDate) {
    this.orderDate = orderDate;
  }
  
  public String getFileName() {
    return fileName;
  }
  
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
  
  public String getEndPoint() {
    return endPoint;
  }
  
  public void setEndPoint(String endPoint) {
    this.endPoint = endPoint;
  }
  
  public String getOrderExpiryDate() {
    return orderExpiryDate;
  }
  
  public void setOrderExpiryDate(String orderExpiryDate) {
    this.orderExpiryDate = orderExpiryDate;
  }
  
  public String getLinkedOrderCode() {
    return linkedOrderCode;
  }
  
  public void setLinkedOrderCode(String linkedOrderCode) {
    this.linkedOrderCode = linkedOrderCode;
  }
  
  public Long getLinkedOrderId() {
    return linkedOrderId;
  }
  
  public void setLinkedOrderId(Long linkedOrderId) {
    this.linkedOrderId = linkedOrderId;
  }
  
  public String getRootOrderCode() {
    return rootOrderCode;
  }
  
  public void setRootOrderCode(String rootOrderCode) {
    this.rootOrderCode = rootOrderCode;
  }
  
  public Long getRootOrderId() {
    return rootOrderId;
  }
  
  public void setRootOrderId(Long rootOrderId) {
    this.rootOrderId = rootOrderId;
  }
  
  public Boolean getHasAttachment() {
    return hasAttachment;
  }
  
  public void setHasAttachment(Boolean hasAttachment) {
    this.hasAttachment = hasAttachment;
  }
  
  public String getCodimp() {
    return codimp;
  }
  
  public void setCodimp(String codimp) {
    this.codimp = codimp;
  }
  
  public String getUffint() {
    return uffint;
  }
  
  public void setUffint(String uffint) {
    this.uffint = uffint;
  }
  
  public Double getTotalPriceWithVat() {
    return totalPriceWithVat;
  }
  
  public void setTotalPriceWithVat(Double totalPriceWithVat) {
    this.totalPriceWithVat = totalPriceWithVat;
  }
  
  public String getCig() {
    return cig;
  }
  
  public void setCig(String cig) {
    this.cig = cig;
  }
  
  public String getNgara() {
    return ngara;
  }
  
  public void setNgara(String ngara) {
    this.ngara = ngara;
  }

  
  /**
   * @return the sender
   */
  public String getSender() {
    return sender;
  }

  
  /**
   * @param sender the sender to set
   */
  public void setSender(String sender) {
    this.sender = sender;
  }

  
  /**
   * @return the receiver
   */
  public String getReceiver() {
    return receiver;
  }

  
  /**
   * @param receiver the receiver to set
   */
  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  @Override
  public String toString() {
    return "OrderNsoRequest ["
        + (orderCode != null ? "orderCode=" + orderCode + ", " : "")
        + (orderDate != null ? "orderDate=" + orderDate + ", " : "")
        + (fileName != null ? "fileName=" + fileName + ", " : "")
        + (endPoint != null ? "endPoint=" + endPoint + ", " : "")
        + (orderExpiryDate != null ? "orderExpiryDate=" + orderExpiryDate + ", " : "")
        + (linkedOrderCode != null ? "linkedOrderCode=" + linkedOrderCode + ", " : "")
        + (linkedOrderId != null ? "linkedOrderId=" + linkedOrderId + ", " : "")
        + (rootOrderCode != null ? "rootOrderCode=" + rootOrderCode + ", " : "")
        + (rootOrderId != null ? "rootOrderId=" + rootOrderId + ", " : "")
        + (hasAttachment != null ? "hasAttachment=" + hasAttachment + ", " : "")
        + (codimp != null ? "codimp=" + codimp + ", " : "")
        + (uffint != null ? "uffint=" + uffint + ", " : "")
        + (totalPriceWithVat != null ? "totalPriceWithVat=" + totalPriceWithVat + ", " : "")
        + (cig != null ? "cig=" + cig + ", " : "")
        + (ngara != null ? "ngara=" + ngara + ", " : "")
        + (sender != null ? "sender=" + sender + ", " : "")
        + (receiver != null ? "receiver=" + receiver : "")
        + "]";
  }

  
  
}