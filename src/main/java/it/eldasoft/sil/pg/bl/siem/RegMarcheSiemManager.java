package it.eldasoft.sil.pg.bl.siem;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import org.apache.log4j.Logger;
import org.apache.tika.utils.StringUtils;
import org.glassfish.jersey.client.ClientProperties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Alvise Gorinati
 */
public class RegMarcheSiemManager extends AbstractSiemManager {

  // === Costruttori ===========================================================
  public RegMarcheSiemManager() throws SiemException {
    super(Logger.getLogger(RegMarcheSiemManager.class));
  }

  // === Metodi ================================================================
  @Override
  public JSONObject extract(final List<String> targets) {
    LOGGER.info("RegMarcheSiemManager: inizio metodo extract per target");

    final Client client = ClientBuilder.newClient();
    client.property(ClientProperties.CONNECT_TIMEOUT, 60000);

    JSONObject result = new JSONObject();
    JSONArray events = new JSONArray();
    String[] dataSource = {"appalti_suam","portaleappalti_suam"};

    for(int d=0; d<dataSource.length; d++) {
      String url = siemUrl.replace("appalti_suam", dataSource[d]);
      for (final String target : targets) {
        JSONObject body = buildBody(target,dataSource[d]);

        final Response response = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", getBasicAuth())
                .header("Content-Type", "application/json")
                .post(Entity.json(body.toString()));

        if (!Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
          LOGGER.warn(
                  String.format(
                          "Richiesta di estrazione per target %s fallita con codice %d - %s",
                          target,
                          response.getStatus(),
                          response.getStatusInfo()
                  )
          );
          return null;
        }

        final JSONArray source = response
                .readEntity(JSONObject.class)
                .getJSONObject("hits")
                .getJSONArray("hits");
        for (int i = 0; i < source.size(); i++) {
          events.add(source.get(i));
        }
      }
    }

    result.put("events", events);

    LOGGER.info("RegMarcheSiemManager: fine metodo extract per target");

    return result;
  }

  @Override
  public JSONObject extract(LocalDate startDate, LocalDate endDate) {
    return null;
  }

  private String getBasicAuth() {
    String auth = null;
    try {
      if (!StringUtils.isBlank(siemPassword)) {
        ICriptazioneByte passwordICriptazioneByte = null;
        passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
                ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), siemPassword.getBytes(),
                ICriptazioneByte.FORMATO_DATO_CIFRATO);
        siemPassword = new String(passwordICriptazioneByte.getDatoNonCifrato());
      }
      String authString = siemUsername + ":" + siemPassword;
      byte[] authByte = authString.getBytes("UTF-8");
      auth = "Basic " + DatatypeConverter.printBase64Binary(authByte);
    }catch (Exception e) {
      LOGGER.error(e.getMessage());
    }
    return auth;
  }

  private JSONObject buildBody(final String target, final String dataset) {
    ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.of("Europe/Rome"));
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    String formattedDate = currentDateTime.format(formatter);
    JSONObject result = new JSONObject();

    Map<String, Object> body = new HashMap<>();
    body.put("query", new HashMap<String, Object>() {{
      put("bool", new HashMap<String, Object>() {{
        put("must", new ArrayList<String>());
        put("filter", Arrays.asList(
          new HashMap<String, Object>() {{
            put("bool", new HashMap<String, Object>() {{
              put("should", Arrays.asList(
                new HashMap<String, Object>() {{
                  put("term", new HashMap<String, Object>() {{
                    put("data_stream.dataset", new HashMap<String, Object>() {{
                      put("value", dataset);
                    }});
                  }});
                }}
              ));
              put("minimum_should_match", 1);
            }});
          }},
          new HashMap<String, Object>() {{
            put("range", new HashMap<String, Object>() {{
              put("@timestamp", new HashMap<String, Object>() {{
                put("format", "strict_date_optional_time");
                put("gte", "2024-01-01T08:00:00.000Z");
                put("lte", formattedDate);
              }});
            }});
          }},
          new HashMap<String, Object>() {{
            put("match_phrase", new HashMap<String, Object>() {{
              put("target", target);
            }});
          }},
          new HashMap<String, Object>() {{
            put("match_phrase", new HashMap<String, Object>() {{
              put("agent.name", siemAgentName);
            }});
          }}
        ));
        put("should", new ArrayList<Object>());
        put("must_not", new ArrayList<Object>());
      }});
    }});
    body.put("sort", Arrays.asList(
            new HashMap<String, Object>() {{
              put("@timestamp", "asc");
            }}
    ));
    body.put("size",10000);

    result.putAll(body);

    return result;
  }
}

