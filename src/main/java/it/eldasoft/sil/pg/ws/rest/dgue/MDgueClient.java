package it.eldasoft.sil.pg.ws.rest.dgue;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;

/**
 * Classe per invocare il ws M-DGUE per l'analisi dei file DGUE
 * @author gabriele.nencini
 *
 */
public class MDgueClient {
  private final Logger logger = Logger.getLogger(MDgueClient.class);
  private Client client;
  private final String infoEndpoint;
  private final String statusEndpoint;

  /**
   * Costruttore
   * @param infoEndpoint - il servizio di parsing del file DGUE
   * @param statusEndpoint - il servizio di controllo sullo stato di m-dgue
   */
  public MDgueClient(String infoEndpoint, String statusEndpoint) {
    this.infoEndpoint = infoEndpoint;
    this.statusEndpoint = statusEndpoint;
    client = ClientBuilder.newClient();
  }
  
  /**
   * Questo metodo invoca il servizio di status per verificare che il servizio m-dgue sia effettivamente up and running.
   * @return true se e solo se il servizio di status risponde con {@link javax.ws.rs.core.Response.Status.Family.SUCCESSFUL}
   */
  public boolean isActiveService() {
    try {
      WebTarget webTarget = this.client.target(this.statusEndpoint);
      Response resp = webTarget.request().get();
      if(Family.SUCCESSFUL.equals(resp.getStatusInfo().getFamily()))
          return true;
    } catch(Exception e) {
      logger.warn("Errore invocazione servizio m-dgue, forse al momento non disponibile. ["+e.getMessage()+"]");
    }
    logger.warn("Servizio m-dgue al momento non disponibile.");
    return false;
  }
  
  /**
   * Invocazione del servizio per la elaborazione del file DGUE
   * @param fileName - il nome del file da elaborare
   * @param base64FileContent - il contenuto del file in Base64
   * @return un oggetto JSON con i dati
   */
  public JSONObject processDgueInfoRequest(String fileName,String base64FileContent) {
    long start = System.currentTimeMillis();
    try {
      WebTarget webTarget = this.client.target(this.infoEndpoint);
      JSONObject req = new JSONObject();
      req.accumulate("nameFile", fileName);
      req.accumulate("base64Xml", base64FileContent);
      logger.debug("Invoke m-dgue-ms");
      JSONObject resp = webTarget.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(req), JSONObject.class);
      if(logger.isTraceEnabled())
        logger.trace("Resp: "+resp);
      return  resp;
    } catch(WebApplicationException e) {
      logger.warn("WSResponse is not SUCCESSFUL, return a JSONObject null. "+e.getMessage());
      JSONObject json = new JSONObject();
      String msg = "";
      if(e.getMessage().contains("400"))
        msg = "File xml non valido: il file analizzato non è un documento DGUE";
      if(e.getMessage().contains("500"))
        msg = "File xml non valido: il file analizzato non è un documento DGUE response ma un DGUE request";
      json.put("error", msg);
      return json;
    } catch(Exception e) {
      logger.error("Error during processing request, return a JSONObject null.",e);
      JSONObject json = new JSONObject();
      String msg = e.getMessage();
      json.put("error", msg);
      return json;
    } finally {
      if(logger.isDebugEnabled()) {
        logger.debug("Made m-dgue-ms request in "+(System.currentTimeMillis()-start)+"ms");
      }
    }
    
  }
  
}
