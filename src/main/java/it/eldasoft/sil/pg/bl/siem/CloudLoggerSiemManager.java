package it.eldasoft.sil.pg.bl.siem;

import java.time.LocalDate;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientProperties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Classe di utilità per interagire con il microservizio Cloud Logger.
 *
 * @author Alvise Gorinati
 */
public class CloudLoggerSiemManager extends AbstractSiemManager {

  // === Costanti ==============================================================
  /**
   * Endpoint per l'estrazione dei log per target di Cloud Logger.
   */
  private static final String EXTRACT_BY_TARGET_ENDPOINT = "/public/extract/byTarget";

  /**
   * Endpoint per l'estrazione dei log per data di Cloud Logger.
   */
  private static final String EXTRACT_BY_DATE_ENDPOINT = "/public/extract/byDate";

  // === Campi =================================================================
  /**
   * Metodo di esporazione log. Se valore 1, allora esporta solamente; se valore
   * 2, allora esporta e verifica.
   */
  private Long exportMethod;

  // === Costruttori ===========================================================
  /**
   * Costruttore di default.
   *
   * @param exportMethod
   *    Metodo di esportazione dei log. 0 = No esportazione, 1 = solo esportazione,
   *    2 = esportazione + verifica
   */
  public CloudLoggerSiemManager(final Long exportMethod) throws SiemException {
    super(Logger.getLogger(CloudLoggerSiemManager.class));

    this.exportMethod = exportMethod;
  }

  // === Metodi ================================================================
  @Override
  public JSONObject extract(final List<String> targets) { 
    if (exportMethod == null || !(exportMethod.equals(1L) || exportMethod.equals(2L))) {
      LOGGER.warn(String.format("Estrazione dei log saltata in quanto esportalog ha valore %d", exportMethod));

      return null;
    }

    LOGGER.info("CloudLoggerManager: inizio metodo extract per target");

    final Client client = ClientBuilder.newClient();
    client.property(ClientProperties.CONNECT_TIMEOUT, 600 * 1000);

    final String endpoint = String.format(
        "%s/%s%s", 
        siemUrl, 
        siemVersion, 
        EXTRACT_BY_TARGET_ENDPOINT
      );

    JSONObject result = new JSONObject();
    JSONArray events = new JSONArray();
  
    { // Chiamata per PG
      final JSONObject body = new JSONObject();
      body.put("codapp", "PG");
      body.put("targets", targets);

      final Response response = client.target(endpoint)
          .request(MediaType.APPLICATION_JSON)
          .post(Entity.json(body.toString()));

      if (!Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
        LOGGER.warn(
          String.format(
            "Richiesta di estrazione per target per PG fallita con codice %d - %s", 
            response.getStatus(),
            response.getStatusInfo().getFamily().toString()
          )
        );

        return null;
      }

      JSONArray source = response.readEntity(JSONObject.class).getJSONArray("events");
      for (int i = 0; i < source.size(); i++) {
        events.add(source.get(i));
      }
    }

    { // Chiamata per PA
      final JSONObject body = new JSONObject();
      body.put("codapp", "PA");
      body.put("targets", targets);

      final Response response = client.target(endpoint)
          .request(MediaType.APPLICATION_JSON)
          .post(Entity.json(body.toString()));

      if (!Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
        LOGGER.warn(
          String.format(
            "Richiesta di estrazione per targer per PA fallita con codice %d - %s", 
            response.getStatus(),
            response.getStatusInfo().getFamily().toString()
          )
        );

        return null;
      }

      JSONArray source = response.readEntity(JSONObject.class).getJSONArray("events");
      for (int i = 0; i < source.size(); i++) {
        events.add(source.get(i));
      }
    }

    result.put("events", events);

    client.close();

    LOGGER.info("CloudLoggerManager: fine metodo extract per target");

    return result;
  }

  @Override
  public JSONObject extract(final LocalDate startDate, final LocalDate endDate) {
    if (exportMethod == null || !(exportMethod.equals(1L))) {
      LOGGER.warn(String.format("Estrazione dei log saltata in quanto esportalog ha valore %d", exportMethod));

      return null;
    }

    LOGGER.info("CloudLoggerManager: inizio metodo extract per data");

    final Client client = ClientBuilder.newClient();
    client.property(ClientProperties.CONNECT_TIMEOUT, 600 * 1000);

    final String endpoint = String.format(
      "%s/%s%s", 
      siemUrl,
      siemVersion,
      EXTRACT_BY_DATE_ENDPOINT
    );

    JSONObject result = new JSONObject();
    JSONArray events = new JSONArray();

    { // Chiamata per PG
      final JSONObject body = new JSONObject();
      body.put("codapp", "PG");
      body.put("startDate", startDate);
      body.put("endDate", endDate);

      final Response response = client.target(endpoint)
        .request(MediaType.APPLICATION_JSON)
        .post(Entity.json(body.toString()));
      
      if (!Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
        LOGGER.warn(
          String.format(
            "Richiesta di estrazione per data per PG fallita con codice %d - %s",
            response.getStatus(),
            response.getStatusInfo().getFamily().toString()
          )
        );
      }

      JSONArray source = response.readEntity(JSONObject.class).getJSONArray("events");
      for (int i = 0; i < source.size(); i++) {
        events.add(source.get(i));
      }
    }

    { // Chiamata per PA
      final JSONObject body = new JSONObject();
      body.put("codapp", "PA");
      body.put("startDate", startDate);
      body.put("endDate", endDate);

      final Response response = client.target(endpoint)
        .request(MediaType.APPLICATION_JSON)
        .post(Entity.json(body.toString()));
      
      if (!Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
        LOGGER.warn(
          String.format(
            "Richiesta di estrazione per data per PA fallita con codice %d - %s",
            response.getStatus(),
            response.getStatusInfo().getFamily().toString()
          )
        );
      }

      JSONArray source = response.readEntity(JSONObject.class).getJSONArray("events");
      for (int i = 0; i < source.size(); i++) {
        events.add(source.get(i));
      }
    }

    result.put("events", events);

    client.close();

    LOGGER.info("CloudLoggerManager: fine metodo extract per data");
    
    return result;
  }

}

