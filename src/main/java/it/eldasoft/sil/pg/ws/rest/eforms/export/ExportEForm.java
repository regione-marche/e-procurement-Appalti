package it.eldasoft.sil.pg.ws.rest.eforms.export;

import java.sql.SQLException;

import it.eldasoft.sil.pg.ws.rest.eforms.bl.GestioneJSONEformManager;
import it.eldasoft.sil.pg.ws.rest.eforms.model.EntityManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ExportEForm {
  
  private GestioneJSONEformManager      gestioneJSONEformManager;

  public void setGestioneJSONEformManager(GestioneJSONEformManager gestioneJSONEformManager) {
    this.gestioneJSONEformManager = gestioneJSONEformManager;
  }
  
  public ExportEForm() {

  }

  public JSONObject creaJSONPin(String codiceEform, String uuidEform) throws Exception {

    EntityManager entityManager = new EntityManager();
    JSONObject json = new JSONObject();

    //List<?> results = gestioneJSONEformManager.getDati(uuidEform, PathJsonEform.ROOT, null);

    String opp011 = "";
    String opp012 = "";
    String opp010 = "";
    String bt02 = "";
    String bt05a = "";
    String noticeId = "";
    String opp070 = "";
    String bt05b = "";
    String bt701 = "";
    String bt04 = "";
    String bt03 = "";
    String bt738 = "";
    String ublVersion = "";
    String opt002 = "";
    String opt001 = "";
    String sdkVersion = "";
    String bt702a = "";
    String bt757 = "";


    
    gestioneJSONEformManager.addIfNotEmpty(json, "OPP-011-notice", opp011);
    gestioneJSONEformManager.addIfNotEmpty(json, "OPP-012-notice", opp012);
    json = gestioneJSONEformManager.populateGRBuyer(json, codiceEform, uuidEform, entityManager);

    gestioneJSONEformManager.addIfNotEmpty(json, "OPP-010-notice", opp010);
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-02-notice", bt02);
    JSONArray bt702bArray = new JSONArray();
    //bt702bArray.add("Example item in BT-702(b)-notice");
    gestioneJSONEformManager.putIfNotEmpty(json, "BT-702(b)-notice", bt702bArray);

    json = gestioneJSONEformManager.populateGRProcedure(json, codiceEform, uuidEform);
    json = gestioneJSONEformManager.populateGROrganisationsSection(json, codiceEform, uuidEform, entityManager);
    json = gestioneJSONEformManager.populateGRLotsGroup(json, codiceEform, uuidEform);
    json = gestioneJSONEformManager.populateGRLot(json, codiceEform, uuidEform, entityManager);

    gestioneJSONEformManager.addIfNotEmpty(json, "BT-05(a)-notice", bt05a);
    gestioneJSONEformManager.addIfNotEmpty(json, "noticeId", noticeId);
    gestioneJSONEformManager.addIfNotEmpty(json, "OPP-070-notice", opp070);
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-05(b)-notice", bt05b);

    if("4".equals(codiceEform) || "5".equals(codiceEform)) {
      json = gestioneJSONEformManager.populateGRPart(json, codiceEform, uuidEform, entityManager);
    }
    
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-701-notice", bt701);
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-04-notice", bt04);
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-03-notice", bt03);
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-738-notice", bt738);
    gestioneJSONEformManager.addIfNotEmpty(json, "ublVersion", ublVersion);
    gestioneJSONEformManager.addIfNotEmpty(json, "OPT-002-notice", opt002);
    gestioneJSONEformManager.addIfNotEmpty(json, "OPT-001-notice", opt001);
    gestioneJSONEformManager.addIfNotEmpty(json, "sdkVersion", sdkVersion);
    /*
    JSONObject metadataDb = new JSONObject();
    metadataDb.put("version", "Example for metadata version");
    metadataDb.put("createdOn", "Example for metadata createdOn");
    json.put("metadataDatabase", metadataDb);
    */
    
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-702(a)-notice", bt702a);

    json = gestioneJSONEformManager.populateGRChange(json, codiceEform, uuidEform);

    gestioneJSONEformManager.addIfNotEmpty(json, "BT-757-notice", bt757);

    return json;

  }

  public JSONObject creaJSONCn(String codiceEform, String uuidEform) throws Exception {

    EntityManager entityManager = new EntityManager();
    JSONObject json = new JSONObject();

    //List<?> results = gestioneJSONEformManager.getDati(uuidEform, PathJsonEform.ROOT, null);

    String opp011 = "";
    String opp012 = "";
    String opp010 = "";
    String bt02 = "";
    String bt05a = "";
    String noticeId = "";
    String opp070 = "";
    String bt05b = "";
    String bt701 = "";
    String bt04 = "";
    String bt03 = "";
    String bt738 = "";
    String ublVersion = "";
    String opt002 = "";
    String opt001 = "";
    String sdkVersion = "";
    String bt702a = "";
    String bt757 = "";


    
    gestioneJSONEformManager.addIfNotEmpty(json, "OPP-011-notice", opp011);
    gestioneJSONEformManager.addIfNotEmpty(json, "OPP-012-notice", opp012);
    json = gestioneJSONEformManager.populateGRBuyer(json, codiceEform, uuidEform, entityManager);

    gestioneJSONEformManager.addIfNotEmpty(json, "OPP-010-notice", opp010);
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-02-notice", bt02);
    JSONArray bt702bArray = new JSONArray();
    //bt702bArray.add("Example item in BT-702(b)-notice");
    gestioneJSONEformManager.putIfNotEmpty(json, "BT-702(b)-notice", bt702bArray);

    json = gestioneJSONEformManager.populateGRProcedure(json, codiceEform, uuidEform);
    json = gestioneJSONEformManager.populateGROrganisationsSection(json, codiceEform, uuidEform, entityManager);
    json = gestioneJSONEformManager.populateGRLotsGroup(json, codiceEform, uuidEform);
    json = gestioneJSONEformManager.populateGRLot(json, codiceEform, uuidEform, entityManager);

    gestioneJSONEformManager.addIfNotEmpty(json, "BT-05(a)-notice", bt05a);
    gestioneJSONEformManager.addIfNotEmpty(json, "noticeId", noticeId);
    gestioneJSONEformManager.addIfNotEmpty(json, "OPP-070-notice", opp070);
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-05(b)-notice", bt05b);
    
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-701-notice", bt701);
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-04-notice", bt04);
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-03-notice", bt03);
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-738-notice", bt738);
    gestioneJSONEformManager.addIfNotEmpty(json, "ublVersion", ublVersion);
    gestioneJSONEformManager.addIfNotEmpty(json, "OPT-002-notice", opt002);
    gestioneJSONEformManager.addIfNotEmpty(json, "OPT-001-notice", opt001);
    gestioneJSONEformManager.addIfNotEmpty(json, "sdkVersion", sdkVersion);
    /*
    JSONObject metadataDb = new JSONObject();
    metadataDb.put("version", "Example for metadata version");
    metadataDb.put("createdOn", "Example for metadata createdOn");
    json.put("metadataDatabase", metadataDb);
    */
    
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-702(a)-notice", bt702a);

    json = gestioneJSONEformManager.populateGRChange(json, codiceEform, uuidEform);

    gestioneJSONEformManager.addIfNotEmpty(json, "BT-757-notice", bt757);

    return json;

  }

  public JSONObject creaJSONCan(String codiceEform, String uuidEform) throws Exception {

    EntityManager entityManager = new EntityManager();
    JSONObject json = new JSONObject();

    //List<?> results = gestioneJSONEformManager.getDati(uuidEform, PathJsonEform.ROOT, null);

    String opp011 = "";
    String opp012 = "";
    String opp010 = "";
    String bt02 = "";
    String bt05a = "";
    String noticeId = "";
    String opp070 = "";
    String bt05b = "";
    String bt701 = "";
    String bt04 = "";
    String bt03 = "";
    String bt738 = "";
    String opt999 = "";
    String ublVersion = "";
    String opt002 = "";
    String opt001 = "";
    String sdkVersion = "";
    String bt702a = "";
    String bt757 = "";


    
    gestioneJSONEformManager.addIfNotEmpty(json, "OPP-011-notice", opp011);
    gestioneJSONEformManager.addIfNotEmpty(json, "OPP-012-notice", opp012);
    json = gestioneJSONEformManager.populateGRBuyer(json, codiceEform, uuidEform, entityManager);

    gestioneJSONEformManager.addIfNotEmpty(json, "OPP-010-notice", opp010);
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-02-notice", bt02);
    JSONArray bt702bArray = new JSONArray();
    //bt702bArray.add("Example item in BT-702(b)-notice");
    gestioneJSONEformManager.putIfNotEmpty(json, "BT-702(b)-notice", bt702bArray);

    json = gestioneJSONEformManager.populateGRProcedure(json, codiceEform, uuidEform);
    json = gestioneJSONEformManager.populateGROrganisationsSection(json, codiceEform, uuidEform, entityManager);
    json = gestioneJSONEformManager.populateGRLotsGroup(json, codiceEform, uuidEform);
    json = gestioneJSONEformManager.populateGRLot(json, codiceEform, uuidEform, entityManager);

    gestioneJSONEformManager.addIfNotEmpty(json, "BT-05(a)-notice", bt05a);
    gestioneJSONEformManager.addIfNotEmpty(json, "noticeId", noticeId);
    gestioneJSONEformManager.addIfNotEmpty(json, "OPP-070-notice", opp070);
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-05(b)-notice", bt05b);
    
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-701-notice", bt701);
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-04-notice", bt04);
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-03-notice", bt03);
    
    json = gestioneJSONEformManager.populateGRResult(json, codiceEform, uuidEform, entityManager);
    gestioneJSONEformManager.addIfNotEmpty(json, "OPT-999", opt999);
    
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-738-notice", bt738);
    json = gestioneJSONEformManager.populateGRModification(json, codiceEform, uuidEform);
    gestioneJSONEformManager.addIfNotEmpty(json, "ublVersion", ublVersion);
    gestioneJSONEformManager.addIfNotEmpty(json, "OPT-002-notice", opt002);
    gestioneJSONEformManager.addIfNotEmpty(json, "OPT-001-notice", opt001);
    gestioneJSONEformManager.addIfNotEmpty(json, "sdkVersion", sdkVersion);
    /*
    JSONObject metadataDb = new JSONObject();
    metadataDb.put("version", "Example for metadata version");
    metadataDb.put("createdOn", "Example for metadata createdOn");
    json.put("metadataDatabase", metadataDb);
    */
    
    gestioneJSONEformManager.addIfNotEmpty(json, "BT-702(a)-notice", bt702a);

    json = gestioneJSONEformManager.populateGRChange(json, codiceEform, uuidEform);

    gestioneJSONEformManager.addIfNotEmpty(json, "BT-757-notice", bt757);

    return json;

  }

  public String getBozzaByCodiceEformUuidEform(String codiceEform, String uuidEformOriginario, String uuidEformCorrente) throws Exception {
    return gestioneJSONEformManager.getBozzaByCodiceEformUuidEform(codiceEform, uuidEformOriginario, uuidEformCorrente);
  }

  public void getSalvaBozza(String codiceEform, String uuidEform, String tracciato) throws Exception {
    gestioneJSONEformManager.getSalvaBozza(codiceEform, uuidEform, tracciato);
  }

  public void getSalvaXml(String codiceEform, String uuidEform, String tracciato) throws Exception {
    gestioneJSONEformManager.getSalvaXml(codiceEform, uuidEform, tracciato);
  }

  public Long getIDByUuidEform(String uuidEform) throws SQLException {
    return gestioneJSONEformManager.getIDByUuidEform(uuidEform);
  }

  public Boolean getPrimoSalvataggioBozza(String uuidEform) throws SQLException {
    return gestioneJSONEformManager.getPrimoSalvataggioBozza(uuidEform);
  }

  public Boolean getPrimoSalvataggioXML(String uuidEform) throws SQLException {
    return gestioneJSONEformManager.getPrimoSalvataggioXML(uuidEform);
  }

  public String getCodgarByUuid(String uuidEform) throws SQLException {
    return gestioneJSONEformManager.getCodgarByUuid(uuidEform);
  }
  
}
