package it.eldasoft.sil.pg.ws.nso.rest.client;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.ws.nso.rest.request.OrderNsoRequest;
import it.eldasoft.sil.pg.ws.nso.rest.request.ValidationNsoRequest;
import it.eldasoft.utils.properties.ConfigManager;

/**
 * This class will be used to send data in REST mode to the REST application "nso-integration"
 * 
 * @author gabriele.nencini
 *
 */
public class ClientNso {

  private static Logger      logger                           = Logger.getLogger(ClientNso.class);

  public static final String NSO_SERVICE                      = "NSO_WS";
  public static final String NSO_ENDPOINT                     = "nso.ws.url";
  public static final String NSO_ENDPOINT_VALIDATE_ORDER_PATH = "nso.ws.url.validateOrder.path";
  public static final String NSO_ENDPOINT_PROCESS_ORDER_PATH  = "nso.ws.url.processOrder.path";

  private GestioneWSDMManager wsdmManager;
  
  public void setWsdmManager(GestioneWSDMManager wsdmManager) {
    this.wsdmManager = wsdmManager;
  }
  
  private HttpAuthenticationFeature feature;
  private Client client;
  
  public ClientNso() {
  }
  
  /**
   * This method will initialize the credentials that needs to be used for auth to the service
   * @throws Exception
   */
  private void initializeCreds() throws Exception {
    String[] creds = this.wsdmManager.getWSDMLoginComune(NSO_SERVICE,null);
    this.feature = HttpAuthenticationFeature.basic(creds[0], creds[1]);
    logger.info("Credentials setted for "+NSO_SERVICE+" with username "+creds[0]);
    this.client = ClientBuilder.newClient().register(MultiPartFeature.class);
    this.client.register(this.feature);
    if(logger.isDebugEnabled()) logger.debug("Client with MultiPartFeature created for "+NSO_SERVICE);
  }

  /**
   * This method will invoke the validation endpoint in order to let the user check if the order can be sent to NSO
   * @param validationRequest - the ValidationNsoRequest that contains the information to be sent
   * @return the Http Response
   */
  public Response validateOrder(ValidationNsoRequest validationRequest) throws Exception {
    logger.info("Invocation for orderId [" + validationRequest.getOrderId() + "]");
    try {
      if(this.feature==null || this.client==null) {
        this.initializeCreds();
      }
      
      String urlNso = ConfigManager.getValore(NSO_ENDPOINT);
      String urlNsoPath = ConfigManager.getValore(NSO_ENDPOINT_VALIDATE_ORDER_PATH);
      String urlNsoPathWithOrder = urlNsoPath + "/" + validationRequest.getOrderId();

      WebTarget webTarget = this.client.target(urlNso).path(urlNsoPathWithOrder);

      MultiPart multiPart = new MultiPart();
      multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

      StreamDataBodyPart streamBodyPart = new StreamDataBodyPart("file", validationRequest.getOrderXml());
      multiPart.bodyPart(streamBodyPart);

      Response response = webTarget.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(multiPart, multiPart.getMediaType()));
      logger.info("Response Status received: " + response.getStatus());
      return response;
    } catch (Exception e) {
      logger.error("Error during communication with nso-integration", e);
      throw e;
    } finally {
      if (validationRequest.getOrderXml() != null) {
        try {
          validationRequest.getOrderXml().close();
        } catch (IOException e) {
          logger.warn("Impossibile close InputStream");
        }
      }
    }
  }
  
  /**
   * This method will invoke the process order endpoint to let the user send the data to NSO
   * @param order - the OrderNsoRequest that contains the informations to be sent
   * @return the Response or an Server Error Response in case of internal errors
   */
  public Response processOrder(OrderNsoRequest order) {
    logger.info("Invocation of processOrder for "+order);
    InputStream file = null;
    try {
      if(this.feature==null || this.client==null) {
        this.initializeCreds();
      }
      
      String urlNso = ConfigManager.getValore(NSO_ENDPOINT);
      String urlNsoPath = ConfigManager.getValore(NSO_ENDPOINT_PROCESS_ORDER_PATH);
      String urlNsoPathWithOrder = urlNsoPath + "/" + order.getOrderId();

      WebTarget webTarget = this.client.target(urlNso).path(urlNsoPathWithOrder);
      
      MultiPart multiPart = new MultiPart();
      multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
      file = order.getOrderXml();
      StreamDataBodyPart streamBodyPart = new StreamDataBodyPart("file", file);
      
      multiPart.bodyPart(streamBodyPart);
      multiPart.getBodyParts().add(new FormDataBodyPart("orderCode", order.getOrderCode()));
      multiPart.getBodyParts().add(new FormDataBodyPart("orderDate", order.getOrderDate()));
      multiPart.getBodyParts().add(new FormDataBodyPart("fileName", order.getFileName()));
      multiPart.getBodyParts().add(new FormDataBodyPart("endpoint", order.getEndPoint()));
      multiPart.getBodyParts().add(new FormDataBodyPart("codimp", order.getCodimp()));
      multiPart.getBodyParts().add(new FormDataBodyPart("orderExpiryDate", order.getOrderExpiryDate()));
      if(order.getLinkedOrderId()!=null) {
        multiPart.getBodyParts().add(new FormDataBodyPart("linkedOrderCode", order.getLinkedOrderCode()));
        multiPart.getBodyParts().add(new FormDataBodyPart("linkedOrderId", order.getLinkedOrderId().toString()));
      }
      if(order.getRootOrderId()!=null) {
        multiPart.getBodyParts().add(new FormDataBodyPart("rootOrderCode", order.getRootOrderCode()));
        multiPart.getBodyParts().add(new FormDataBodyPart("rootOrderId", order.getRootOrderId().toString()));
      }
      multiPart.getBodyParts().add(new FormDataBodyPart("hasAttachment", order.getHasAttachment().toString()));
      multiPart.getBodyParts().add(new FormDataBodyPart("uffint", order.getUffint()));
      multiPart.getBodyParts().add(new FormDataBodyPart("cig", order.getCig()));
      multiPart.getBodyParts().add(new FormDataBodyPart("ngara", order.getNgara()));
      multiPart.getBodyParts().add(new FormDataBodyPart("totalPriceWithVat", order.getTotalPriceWithVat().toString()));
      
      Response response = webTarget.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(multiPart, multiPart.getMediaType()));
      logger.info("Response Status received: " + response.getStatus());
      return response;
    } catch (Exception e) {
      logger.error("Error during communication with nso-integration", e);
    } finally {
      if (file != null) {
        try {
          file.close();
        } catch (IOException e) {
          logger.warn("Impossibile close InputStream");
        }
      }
    }
    //prevent null pointer on caller
    return Response.serverError().build();
  }

}
