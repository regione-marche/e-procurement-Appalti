package it.eldasoft.sil.pg.ws.rest.eforms.bl;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.ws.objectstorage.ObjectStorageUtils;
import it.eldasoft.gene.ws.objectstorage.UploadInfo;
import it.eldasoft.sil.pg.bl.InviaVigilanzaManager;
import it.eldasoft.sil.pg.ws.rest.eforms.export.gestoreQuery.PathJsonEform;
import it.eldasoft.sil.pg.ws.rest.eforms.export.gestoreQuery.QueryRegistry;
import it.eldasoft.sil.pg.ws.rest.eforms.model.EntityManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityMath;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import java.io.StringReader;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * Considerazioni prima di modificare questa classe:
 * SE nel metodo dove devi mettere le mani e' commentata la "gestione" dell'arrayObject e trovi piu' record nel json che produce
 * con lo stesso codice es. GR-Procedure-Purpose significa che la query non e' corretta e che si deve adeguare 
 * per gestire il nuovo caso trovato. (Aiuto) se il genere che viene passato e' Long.valueOf(0), puoi splittare le query 
 * suddividendole per genere, bastera' passare null come genere e splittare la query corrispondente al 
 * PathJsonEform nel file QueryRegistry.
 * 
 * @author User Alex Mancini
 *
 */
public class GestioneJSONEformManager {

  private static final Logger logger = Logger.getLogger(GestioneJSONEformManager.class);
  private static final String PREFISSO_ORG = "ORG";
  private static final String PREFISSO_CON = "CON";
  private static final String PREFISSO_TEN = "TEN";
  private static final String PREFISSO_TPA = "TPA";

  private SqlManager      sqlManager;

  private InviaVigilanzaManager inviaVigilanzaManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  public SqlManager getSqlManager() {
    return this.sqlManager;
  }
  public void setInviaVigilanzaManager(InviaVigilanzaManager inviaVigilanzaManager) {
    this.inviaVigilanzaManager = inviaVigilanzaManager;
  }



  // =========================================================================
  //                         GR-Procedure
  // =========================================================================
  public JSONObject populateGRProcedure(JSONObject parent, String codiceEform, String uuidEform) throws Exception {
    // Creiamo l'array "GR-Procedure"
    JSONArray grProcedureArray = new JSONArray();

    // Creiamo l'oggetto corrispondente (il singolo elemento dell'array)
    JSONObject grProcedureObject = new JSONObject();

    // --- 1) GR-Procedure-Purpose
    populateGRProcedurePurpose(grProcedureObject, codiceEform, uuidEform);

    // --- 2) GR-Procedure-TenderingTerms
    populateGRProcedureTenderingTerms(grProcedureObject, codiceEform, uuidEform);

    // --- 3) GR-Procedure-LotDistribution
    populateGRProcedureLotDistribution(grProcedureObject, codiceEform, uuidEform);

    // --- 4) GR-Procedure-Procedure-subsection
    populateGRProcedureProcedureSubsection(grProcedureObject, codiceEform, uuidEform);

    // Aggiungiamo l'oggetto all'array
    grProcedureArray.add(grProcedureObject);

    // Infine, mettiamo l'array "GR-Procedure" nel parent
    putIfNotEmpty(parent,"GR-Procedure", grProcedureArray);

    return parent;
  }

  // =========================================================================
  //                         GR-Procedure-Purpose
  // =========================================================================
  private void populateGRProcedurePurpose(JSONObject grProcedureObject, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-Procedure-Purpose" e' un array
    JSONArray purposeArray = new JSONArray();
    JSONObject purposeObj = new JSONObject();

    // 1. Sotto-sezione "GR-Procedure-Description"
    populateGRProcedureDescription(purposeObj, codiceEform, uuidEform);

    // 2. Sotto-sezione "GR-Procedure-LocalLegalBasisNoID"
    populateGRProcedureLocalLegalBasisNoID(purposeObj, codiceEform, uuidEform);

    // 3. Sotto-sezione "GR-Procedure-MainLegalBasis"
    populateGRProcedureMainLegalBasis(purposeObj, codiceEform, uuidEform);

    // 4. Sotto-sezione "GR-Procedure-Scope"
    populateGRProcedureScope(purposeObj, codiceEform, uuidEform);

    // 5. Sotto-sezione "GR-Procedure-PlaceOfPerformance"
    populateGRProcedurePlaceOfPerformance(purposeObj, codiceEform, uuidEform);

    // 6. Sotto-sezione "GR-Procedure-LocalLegalBasisWithID"
    populateGRProcedureLocalLegalBasisWithID(purposeObj, codiceEform, uuidEform);

    // 7. Sotto-sezione "GR-Previous-Notice"
    populateGRPreviousNotice(purposeObj, codiceEform, uuidEform);

    // Aggiungiamo l'oggetto a purposeArray
    purposeArray.add(purposeObj);

    // Inseriamo l'array in grProcedureObject
    putIfNotEmpty(grProcedureObject,"GR-Procedure-Purpose", purposeArray);
  }

  // -----------------------------------------------------
  // GR-Procedure-Description
  // -----------------------------------------------------
  private void populateGRProcedureDescription(JSONObject procedurePurposeObject, String codiceEform, String uuidEform) throws Exception {

    List<?> results = getDati(uuidEform, PathJsonEform.GR_PROCEDURE_DESCRIPTION, null);

    //JSONArray descrArray = new JSONArray();

    for(Object result : results) {

      JSONObject descrObj = new JSONObject();

      
      String bt21 = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      Object bt23 = SqlManager.getValueFromVectorParam(result, 1).getValue();
      String bt24 = SqlManager.getValueFromVectorParam(result, 2).stringValue();
      String bt22 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
      String bt531 = "";//SqlManager.getValueFromVectorParam(result, 2).stringValue();
      String bt127 = "";//SqlManager.getValueFromVectorParam(result, 5).stringValue();

      addIfNotEmpty(descrObj, "BT-22-Procedure", bt22);

      // "GR-Procedure-Additional-Nature" (array)
      if(bt531 != null && !bt531.isEmpty()) {
        JSONArray additionalNatureArray = new JSONArray();
        JSONObject additionalNatureObj = new JSONObject();
        addIfNotEmpty(additionalNatureObj, "BT-531-Procedure", bt531);
        additionalNatureArray.add(additionalNatureObj);
        putIfNotEmpty(descrObj,"GR-Procedure-Additional-Nature", additionalNatureArray);
      }
      Long bt23Long = null;
      if (bt23 instanceof String) {
        bt23Long = Long.valueOf((String) bt23);
      } else if (bt23 instanceof Long) {
        bt23Long = Long.valueOf((Long) bt23);
      }

      addIfNotEmpty(descrObj, "BT-23-Procedure",  getValoreByTip(bt23Long,getGenereByCodgar(getCodgarByUuid(uuidEform))));
      addIfNotEmpty(descrObj, "BT-24-Procedure",  bt24);
      addIfNotEmpty(descrObj, "BT-21-Procedure",  bt21);
      addIfNotEmpty(descrObj, "BT-127-notice",    bt127);

      // Aggiungiamo descrObj all'array
      //descrArray.add(descrObj);
      putIfNotEmpty(procedurePurposeObject,"GR-Procedure-Description", descrObj);
    }

  }

  // -----------------------------------------------------
  // GR-Procedure-LocalLegalBasisNoID
  // -----------------------------------------------------
  private void populateGRProcedureLocalLegalBasisNoID(JSONObject procedurePurposeObject, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray localLegalBasisNoIDArray = new JSONArray();
    JSONObject localLegalBasisNoIDObj = new JSONObject();

    // "BT-01(f)-Procedure"
    String bt01f = "";
    addIfNotEmpty(localLegalBasisNoIDObj, "BT-01(f)-Procedure", bt01f);
    // "BT-01(e)-Procedure"
    String bt01e = "";
    addIfNotEmpty(localLegalBasisNoIDObj, "BT-01(e)-Procedure", bt01e);

    localLegalBasisNoIDArray.add(localLegalBasisNoIDObj);
    putIfNotEmpty(procedurePurposeObject,"GR-Procedure-LocalLegalBasisNoID", localLegalBasisNoIDArray);
  }

  // -----------------------------------------------------
  // GR-Procedure-MainLegalBasis
  // -----------------------------------------------------
  private void populateGRProcedureMainLegalBasis(JSONObject procedurePurposeObject, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray mainLegalBasisArray = new JSONArray();
    JSONObject mainLegalBasisObj = new JSONObject();

    // "BT-01-notice"
    String bt01notice = "32014L0025";
    addIfNotEmpty(mainLegalBasisObj, "BT-01-notice", bt01notice);

    mainLegalBasisArray.add(mainLegalBasisObj);
    putIfNotEmpty(procedurePurposeObject,"GR-Procedure-MainLegalBasis", mainLegalBasisArray);
  }

  // -----------------------------------------------------
  // GR-Procedure-Scope
  // -----------------------------------------------------
  private void populateGRProcedureScope(JSONObject procedurePurposeObject, String codiceEform, String uuidEform) throws Exception {

    List<?> results = getDati(uuidEform, PathJsonEform.GR_PROCEDURE_SCOPE, null);

    //JSONArray scopeArray = new JSONArray();
    for(Object result : results){

      
      Double bt27 = SqlManager.getValueFromVectorParam(result, 0).doubleValue();
      String bt271 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();

      JSONObject scopeObj = new JSONObject();
      
      // Sotto-sezione GR-Procedure-Scope-AdditionalClassification
      populateGRProcedureScopeAdditionalClassification(scopeObj, codiceEform, uuidEform);
      
      
      addIfNotEmpty(scopeObj, "BT-27-Procedure", bt27);
      if(bt27 != null) {
        addIfNotEmpty(scopeObj, "BT-27-Procedure-currency", "EUR");
      }
      addIfNotEmpty(scopeObj, "BT-271-Procedure", bt271);
      
      // Sotto-sezione GR-Procedure-Scope-MainClassification
      populateGRProcedureScopeMainClassification(scopeObj, codiceEform, uuidEform);
      
      
      
      //scopeArray.add(scopeObj);
      putIfNotEmpty(procedurePurposeObject,"GR-Procedure-Scope", scopeObj);
    }
  }

  // -----------------------------------------------------
  // GR-Procedure-Scope-AdditionalClassification
  // -----------------------------------------------------
  private void populateGRProcedureScopeAdditionalClassification(JSONObject scopeObj, String codiceEform, String uuidEform) throws Exception {

    List<?> results = getDati(uuidEform, PathJsonEform.GR_PROCEDURE_SCOPE_ADDITIONALCLASSIFICATION, Long.valueOf(0));

    JSONArray addClassArray = new JSONArray();
    for(Object result : results){

      String bt263 = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt26a = "cpv";

      if(bt263 != null && bt263.contains("-")) {
        bt263 = bt263.split("-")[0];
      }

      JSONObject addClassObj = new JSONObject();
  
      addIfNotEmpty(addClassObj, "BT-26(a)-Procedure", bt26a);
      addIfNotEmpty(addClassObj, "BT-263-Procedure", bt263);
  
      addClassArray.add(addClassObj);
    }
    putIfNotEmpty(scopeObj,"GR-Procedure-Scope-AdditionalClassification", addClassArray);
  }

  // -----------------------------------------------------
  // GR-Procedure-Scope-MainClassification
  // -----------------------------------------------------
  private void populateGRProcedureScopeMainClassification(JSONObject scopeObj, String codiceEform, String uuidEform) throws Exception {

    List<?> results = getDati(uuidEform, PathJsonEform.GR_PROCEDURE_SCOPE_MAINCLASSIFICATION, Long.valueOf(0));

    for(Object result : results){

      String bt262 = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt26m = "";

      if(bt262 != null && bt262.contains("-")) {
        bt262 = bt262.split("-")[0];
        bt26m = "cpv";
      }
      
      JSONObject mainClassObj = new JSONObject();

      addIfNotEmpty(mainClassObj, "BT-26(m)-Procedure", bt26m);
      addIfNotEmpty(mainClassObj, "BT-262-Procedure", bt262);

      putIfNotEmpty(scopeObj,"GR-Procedure-Scope-MainClassification", mainClassObj);
    }
  }

  // -----------------------------------------------------
  // GR-Procedure-PlaceOfPerformance
  // -----------------------------------------------------
  private void populateGRProcedurePlaceOfPerformance(JSONObject procedurePurposeObject, String codiceEform, String uuidEform) throws Exception {

    List<?> results = getDati(uuidEform, PathJsonEform.GR_PROCEDURE_PLACEOFPERFORMANCE, null);

    //JSONArray placeOfPerfArray = new JSONArray();
    //Dato che devo recuperare dei dati su gare ma mi serve solo il primo, ciclo una volta sola
    for(int i = 0; i < 1; i++) {
      String bt5071 = SqlManager.getValueFromVectorParam(results.get(i), 0).stringValue();
      String prosla = SqlManager.getValueFromVectorParam(results.get(i), 1).stringValue();
      String loclav = SqlManager.getValueFromVectorParam(results.get(i), 2).stringValue();
      String nomssl = SqlManager.getValueFromVectorParam(results.get(i), 3).stringValue();
      Long locnaz = SqlManager.getValueFromVectorParam(results.get(i), 4).longValue();
      
      String bt5141 = "ITA";
      if(locnaz != null) {
        bt5141 = getSiglaByCodice(locnaz);
      }
      
      String bt5131 = "";
      if(nomssl != null){bt5131 = bt5131 + nomssl;}
      if(nomssl != null && prosla != null){bt5131 = bt5131 + "  ";}
      if(loclav != null){bt5131 = bt5131 + loclav;}
      if(prosla != null){bt5131 = bt5131 + " (" + prosla + ")";}

      String bt728 = "";
      String bt727 = "";
      String bt5101c = "";
      String bt5121 = "";
      String bt5101a = "";
      String bt5101b = "";


      JSONObject placeOfPerfObj = new JSONObject();
      
      addIfNotEmpty(placeOfPerfObj, "BT-728-Procedure", bt728);
      addIfNotEmpty(placeOfPerfObj, "BT-5141-Procedure", bt5141);
      addIfNotEmpty(placeOfPerfObj, "BT-727-Procedure", bt727);
      addIfNotEmpty(placeOfPerfObj, "BT-5101(c)-Procedure", bt5101c);
      addIfNotEmpty(placeOfPerfObj, "BT-5121-Procedure", bt5121);
      addIfNotEmpty(placeOfPerfObj, "BT-5131-Procedure", bt5131);
      addIfNotEmpty(placeOfPerfObj, "BT-5101(a)-Procedure", bt5101a);
      addIfNotEmpty(placeOfPerfObj, "BT-5101(b)-Procedure", bt5101b);
      if (bt5071 != null && bt5071.length() == 5) {
        addIfNotEmpty(placeOfPerfObj, "BT-5071-Procedure", bt5071);        
      }
      
      //placeOfPerfArray.add(placeOfPerfObj);
      putIfNotEmpty(procedurePurposeObject,"GR-Procedure-PlaceOfPerformance", placeOfPerfObj);
    }
  }

  // -----------------------------------------------------
  // GR-Procedure-LocalLegalBasisWithID
  // -----------------------------------------------------
  private void populateGRProcedureLocalLegalBasisWithID(JSONObject procedurePurposeObject, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray localLegalWithIDArray = new JSONArray();
    JSONObject localLegalWithIDObj = new JSONObject();

    // "BT-01(d)-Procedure"
    String bt01d = "";
    addIfNotEmpty(localLegalWithIDObj, "BT-01(d)-Procedure", bt01d);
    // "BT-01(c)-Procedure"
    String bt01c = "";
    addIfNotEmpty(localLegalWithIDObj, "BT-01(c)-Procedure", bt01c);

    localLegalWithIDArray.add(localLegalWithIDObj);
    putIfNotEmpty(procedurePurposeObject,"GR-Procedure-LocalLegalBasisWithID", localLegalWithIDArray);
  }

  // -----------------------------------------------------
  // GR-Previous-Notice
  // -----------------------------------------------------
  private void populateGRPreviousNotice(JSONObject procedurePurposeObject, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray prevNoticeArray = new JSONArray();
    JSONObject prevNoticeObj = new JSONObject();

    // "OPP-090-Procedure"
    String opp090 = "";
    addIfNotEmpty(prevNoticeObj, "OPP-090-Procedure", opp090);

    prevNoticeArray.add(prevNoticeObj);
    putIfNotEmpty(procedurePurposeObject,"GR-Previous-Notice", prevNoticeArray);
  }

  // =========================================================================
  //                 GR-Procedure-TenderingTerms
  // =========================================================================
  private void populateGRProcedureTenderingTerms(JSONObject grProcedureObject, String codiceEform, String uuidEform) throws Exception {

    JSONObject tenderingTermsObj = new JSONObject();

    // 1. GR-Procedure-ExclusionGrounds-Source
    populateGRProcedureExclusionGroundsSource(tenderingTermsObj, codiceEform, uuidEform);

    // 2. GR-Procedure-ExclusionGrounds
    populateGRProcedureExclusionGrounds(tenderingTermsObj, codiceEform, uuidEform);

    // 3. GR-Procedure-CrossBorderLaw
    populateGRProcedureCrossBorderLaw(tenderingTermsObj, codiceEform, uuidEform);

    putIfNotEmpty(grProcedureObject,"GR-Procedure-TenderingTerms", tenderingTermsObj);
  }

  // -----------------------------------------------------
  // GR-Procedure-ExclusionGrounds-Source
  // -----------------------------------------------------
  private void populateGRProcedureExclusionGroundsSource(JSONObject tenderingTermsObj, String codiceEform, String uuidEform) throws Exception {

    JSONObject exclGroundSourceObj = new JSONObject();

    addIfNotEmpty(exclGroundSourceObj, "BT-806-Procedure", getMotivoEsclusione(uuidEform, "Si", null));

    putIfNotEmpty(tenderingTermsObj,"GR-Procedure-ExclusionGrounds-Source", exclGroundSourceObj);
  }

  
  // -----------------------------------------------------
  // GR-Procedure-ExclusionGrounds
  // -----------------------------------------------------
  private void populateGRProcedureExclusionGrounds(JSONObject tenderingTermsObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray exclGroundsArray = new JSONArray();
    JSONObject exclGroundsObj = new JSONObject();

    // "BT-67(a)-Procedure"
    String bt67a = "";
    addIfNotEmpty(exclGroundsObj, "BT-67(a)-Procedure", bt67a);
    // "BT-67(b)-Procedure"
    String bt67b = "";
    addIfNotEmpty(exclGroundsObj, "BT-67(b)-Procedure", bt67b);

    exclGroundsArray.add(exclGroundsObj);
    putIfNotEmpty(tenderingTermsObj,"GR-Procedure-ExclusionGrounds", exclGroundsArray);
  }

  // -----------------------------------------------------
  // GR-Procedure-CrossBorderLaw
  // -----------------------------------------------------
  private void populateGRProcedureCrossBorderLaw(JSONObject tenderingTermsObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray crossBorderLawArray = new JSONArray();
    JSONObject crossBorderLawObj = new JSONObject();

    // "BT-09(b)-Procedure"
    String bt09b = "";
    addIfNotEmpty(crossBorderLawObj, "BT-09(b)-Procedure", bt09b);
    // "BT-09(a)-Procedure"
    String bt09a = "";
    addIfNotEmpty(crossBorderLawObj, "BT-09(a)-Procedure", bt09a);

    crossBorderLawArray.add(crossBorderLawObj);
    putIfNotEmpty(tenderingTermsObj,"GR-Procedure-CrossBorderLaw", crossBorderLawArray);
  }

  // =========================================================================
  //                GR-Procedure-LotDistribution
  // =========================================================================
  private void populateGRProcedureLotDistribution(JSONObject grProcedureObject, String codiceEform, String uuidEform) throws Exception {

    JSONObject lotDistributionObj = new JSONObject();

    // 1. GR-Procedure-GroupComposition
    populateGRProcedureGroupComposition(lotDistributionObj, codiceEform, uuidEform);

    // 2. GR-Procedure-MaxLots
    populateGRProcedureMaxLots(lotDistributionObj, codiceEform, uuidEform);

    putIfNotEmpty(grProcedureObject,"GR-Procedure-LotDistribution", lotDistributionObj);
  }

  // -----------------------------------------------------
  // GR-Procedure-GroupComposition
  // -----------------------------------------------------
  private void populateGRProcedureGroupComposition(JSONObject lotDistributionObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray groupCompositionArray = new JSONArray();
    JSONObject groupCompositionObj = new JSONObject();

    // "BT-330-Procedure"
    String bt330 = "";
    addIfNotEmpty(groupCompositionObj, "BT-330-Procedure", bt330);

    // "GR-Group-Composition-Reference"
    JSONArray groupCompositionRefArray = new JSONArray();
    JSONObject groupCompositionRefObj = new JSONObject();
    // "BT-1375-Procedure"
    String bt1375 = "";
    addIfNotEmpty(groupCompositionRefObj, "BT-1375-Procedure", bt1375);
    groupCompositionRefArray.add(groupCompositionRefObj);

    putIfNotEmpty(groupCompositionObj,"GR-Group-Composition-Reference", groupCompositionRefArray);

    groupCompositionArray.add(groupCompositionObj);
    putIfNotEmpty(lotDistributionObj,"GR-Procedure-GroupComposition", groupCompositionArray);
  }

  // -----------------------------------------------------
  // GR-Procedure-MaxLots
  // -----------------------------------------------------
  private void populateGRProcedureMaxLots(JSONObject lotDistributionObj, String codiceEform, String uuidEform) throws Exception {

    List<?> results = getDati(uuidEform, PathJsonEform.GR_PROCEDURE_MAXLOTS, Long.valueOf(0));

    //JSONArray maxLotsArray = new JSONArray();
    for(Object result : results){

      JSONObject maxLotsObj = new JSONObject();

      Long nofdit = SqlManager.getValueFromVectorParam(result, 0).longValue();
      Long ngadit = SqlManager.getValueFromVectorParam(result, 1).longValue();
      Long offlot = SqlManager.getValueFromVectorParam(result, 2).longValue();

      Long bt31 = null;
      Long bt33 = null;
      if(offlot != null) {
        if(Long.valueOf(1).equals(offlot)) {
          bt31 = offlot;
          bt33 = offlot;
        } else if(Long.valueOf(2).equals(offlot)) {
          bt31 = nofdit;
          bt33 = ngadit;
        } else if(Long.valueOf(3).equals(offlot)) {
          bt31 = (Long) sqlManager.getObject("select count(*) from gare where codgar1 != ngara and codgar1 = ? ",new Object[] { getCodgarByUuid(uuidEform) });
          bt33 = ngadit;
        }
      }
      
      addIfNotEmpty(maxLotsObj, "BT-31-Procedure", bt31);
      addIfNotEmpty(maxLotsObj, "BT-33-Procedure", bt33);

      //maxLotsArray.add(maxLotsObj);
      putIfNotEmpty(lotDistributionObj,"GR-Procedure-MaxLots", maxLotsObj);
    }
  }

  // =========================================================================
  //          GR-Procedure-Procedure-subsection
  // =========================================================================
  private void populateGRProcedureProcedureSubsection(JSONObject grProcedureObject, String codiceEform, String uuidEform) throws Exception {

    JSONObject subsectionObj = new JSONObject();

    // 1. GR-Procedure-AdditionalInformation
    populateGRProcedureAdditionalInformation(subsectionObj, codiceEform, uuidEform);

    // 2. GR-Procedure-Procedure
    populateGRProcedureProcedure(subsectionObj, codiceEform, uuidEform);

    // 3. GR-Procedure-Accelerated
    populateGRProcedureAccelerated(subsectionObj, codiceEform, uuidEform);

    // 4. GR-Procedure-DirectAward
    populateGRProcedureDirectAward(subsectionObj, codiceEform, uuidEform);

    // 5. GR-Procedure-PinTerminated
    populateGRProcedurePinTerminated(subsectionObj, codiceEform, uuidEform);

    putIfNotEmpty(grProcedureObject,"GR-Procedure-Procedure-subsection", subsectionObj);
  }

  // -----------------------------------------------------
  // GR-Procedure-AdditionalInformation
  // -----------------------------------------------------
  private void populateGRProcedureAdditionalInformation(JSONObject subsectionObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray addInfoArray = new JSONArray();
    JSONObject addInfoObj = new JSONObject();

    // "BT-300-Procedure"
    String bt300 = "";
    addIfNotEmpty(addInfoObj, "BT-300-Procedure", bt300);

    addInfoArray.add(addInfoObj);
    putIfNotEmpty(subsectionObj,"GR-Procedure-AdditionalInformation", addInfoArray);
  }

  // -----------------------------------------------------
  // GR-Procedure-Procedure
  // -----------------------------------------------------
  private void populateGRProcedureProcedure(JSONObject subsectionObj, String codiceEform, String uuidEform) throws Exception {

    List<?> results = getDati(uuidEform, PathJsonEform.GR_PROCEDURE_PROCEDURE, null);

    JSONArray procedureArray = new JSONArray();
    for(Object result : results){

      JSONObject procedureItem = new JSONObject();

      Long tipgarg = SqlManager.getValueFromVectorParam(result, 0).longValue();
      Long iterga = SqlManager.getValueFromVectorParam(result, 1).longValue();

      String bt21 = "";
      String bt24 = "";
      String bt634 = "";
      String bt88 = "";
      String bt763 = "";
      String bt105 = getTipoProceduraByIterga(tipgarg, iterga);


      addIfNotEmpty(procedureItem, "BT-634-Procedure", bt634);
      addIfNotEmpty(procedureItem, "BT-88-Procedure", bt88);
      addIfNotEmpty(procedureItem, "BT-105-Procedure", bt105);
      addIfNotEmpty(procedureItem, "BT-763-Procedure", bt763);

      // Aggiungiamo procedureItem all'array
      procedureArray.add(procedureItem);
    }
    putIfNotEmpty(subsectionObj,"GR-Procedure-Procedure", procedureArray);
  }


  // -----------------------------------------------------
  // GR-Procedure-Accelerated
  // -----------------------------------------------------
  private void populateGRProcedureAccelerated(JSONObject subsectionObj, String codiceEform, String uuidEform) throws Exception {

    List<?> results = getDati(uuidEform, PathJsonEform.GR_PROCEDURE_ACCELERATED, Long.valueOf(0));

    for(Object result : results){

      JSONObject acceleratedObj = new JSONObject();

      String prourg = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt1351 = SqlManager.getValueFromVectorParam(result, 1).stringValue();

      Boolean bt106 = Boolean.FALSE;

      if(prourg != null && "1".equals(prourg)) {
        bt106 = Boolean.TRUE;
      }

      addIfNotEmpty(acceleratedObj, "BT-106-Procedure", bt106);
      addIfNotEmpty(acceleratedObj, "BT-1351-Procedure", bt1351);

      putIfNotEmpty(subsectionObj,"GR-Procedure-Accelerated", acceleratedObj);
    }
  }

  // -----------------------------------------------------
  // GR-Procedure-DirectAward
  // -----------------------------------------------------
  private void populateGRProcedureDirectAward(JSONObject subsectionObj, String codiceEform, String uuidEform) throws Exception {

    List<?> results = getDati(uuidEform, PathJsonEform.GR_PROCEDURE_DIRECTAWARD, Long.valueOf(0));

    for(Object result : results){

      String bt136 = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt135 = "";
      String bt1252 = "";
      String bt195_135 = "";
      String bt197_135 = "";
      String bt198_135 = "";
      String bt196_135 = "";
      String bt198_136 = "";
      String bt195_136 = "";
      String bt197_136 = "";
      String bt196_136 = "";
      String bt197_1252 = "";
      String bt196_1252 = "";
      String bt195_1252 = "";
      String bt198_1252 = "";


      JSONObject directAwardObj = new JSONObject();

      addIfNotEmpty(directAwardObj, "BT-136-Procedure", getTab2d1ByTab2tip(bt136, "A1z22"));
      addIfNotEmpty(directAwardObj, "BT-135-Procedure", bt135);
      addIfNotEmpty(directAwardObj, "BT-1252-Procedure", bt1252);

      putIfNotEmpty(subsectionObj,"GR-Procedure-DirectAward", directAwardObj);
    }
  }

  // -----------------------------------------------------
  // GR-Procedure-PinTerminated
  // -----------------------------------------------------
  private void populateGRProcedurePinTerminated(JSONObject subsectionObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray pinTerminatedArray = new JSONArray();
    JSONObject pinTerminatedObj = new JSONObject();

    // "BT-756-Procedure"
    String bt756 = "";
    addIfNotEmpty(pinTerminatedObj, "BT-756-Procedure", bt756);

    pinTerminatedArray.add(pinTerminatedObj);
    putIfNotEmpty(subsectionObj,"GR-Procedure-PinTerminated", pinTerminatedArray);
  }





  // =========================================================================
  //                           GR-Organisations-Section
  // =========================================================================
  public JSONObject populateGROrganisationsSection(JSONObject parent, String codiceEform, String uuidEform, EntityManager entityManager) throws Exception {
    // "GR-Organisations-Section" e' un array:
    JSONArray orgSectionArray = new JSONArray();

    // Creiamo l'oggetto (singolo elemento dell'array)
    JSONObject orgSectionObj = new JSONObject();

    String famiglia = getFamilyByCodEform(codiceEform);
    if(!"CAN".equals(famiglia)) {
      // 1) GR-Organisations
      populateGROrganisations(orgSectionObj, codiceEform, uuidEform, entityManager);
    }
    // 2) GR-UBO-Subsection
    populateGRUBOSubsection(orgSectionObj, codiceEform, uuidEform);

    // 3) GR-Organisations-Subsection
    populateGROrganisationsSubsection(orgSectionObj, codiceEform, uuidEform, entityManager);

    // Aggiungiamo orgSectionObj all'array "GR-Organisations-Section"
    orgSectionArray.add(orgSectionObj);

    // Mettiamo l'array nel parent
    putIfNotEmpty(parent,"GR-Organisations-Section", orgSectionArray);

    return parent;
  }

  // =========================================================================
  //                           GR-Organisations
  // =========================================================================
  private void populateGROrganisations(JSONObject orgSectionObj, String codiceEform, String uuidEform, EntityManager entityManager) throws Exception {

    JSONArray grOrgsArray = new JSONArray();
    
    populateGROrganisationsUffici(grOrgsArray, codiceEform, uuidEform, entityManager);
    
    String famiglia = getFamilyByCodEform(codiceEform);
    if("CAN".equals(famiglia)) {
      populateGROrganisationsOperatori(grOrgsArray, codiceEform, uuidEform, entityManager);
    }
    
    putIfNotEmpty(orgSectionObj,"GR-Organisations", grOrgsArray);
  }
  
  private void populateGROrganisationsUffici(JSONArray grOrgsArray, String codiceEform, String uuidEform, EntityManager entityManager) throws Exception {

    String famiglia = getFamilyByCodEform(codiceEform);
    
    List<?> results = new ArrayList<>();
    Boolean isAD1 = codiceEform.equals("25") || codiceEform.equals("26") || codiceEform.equals("28");

    if("CAN".equals(famiglia) && Boolean.FALSE.equals(isAD1)) {
      results = getDati(uuidEform, PathJsonEform.GR_ORGANISATIONS_UFFICI_CAN, Long.valueOf(0));
    } else {
      results = getDati(uuidEform, PathJsonEform.GR_ORGANISATIONS_UFFICI_CN_PIN, Long.valueOf(0));
    }

    for(Object result : results){

      // "GR-Organisations" e' un array
      JSONObject grOrgObj = new JSONObject();

      Long numpun = SqlManager.getValueFromVectorParam(result, 0).longValue();
      String codein = SqlManager.getValueFromVectorParam(result, 1).stringValue();
      String orgricorso = SqlManager.getValueFromVectorParam(result, 2).stringValue();

      // "OPT-200-Organization-Company"
      String entita = "uffint";
      String chiave = codein;
      if(numpun != null && !Long.valueOf(0).equals(numpun)) {
        entita = "punticon";
        chiave = String.valueOf(numpun)+";"+codein;
      }
      
      Boolean condizionePIN = ("punticon".equals(entita) && ("PIN".equals(famiglia) && (orgricorso == null || (orgricorso != null && "1".equals(orgricorso) && !"1".equals(codiceEform) && !"2".equals(codiceEform)))));
      Boolean condizioneCN  = ("punticon".equals(entita) && ("CN".equals(famiglia)));
      Boolean condizioneCAN = ("punticon".equals(entita) && ("CAN".equals(famiglia)));
      
      if("uffint".equals(entita) || Boolean.TRUE.equals(condizionePIN) || Boolean.TRUE.equals(condizioneCN) || Boolean.TRUE.equals(condizioneCAN)) {
        String opt200 = entityManager.getOrCreateValue(PREFISSO_ORG, entita, chiave, Boolean.TRUE);
        addIfNotEmpty(grOrgObj, "OPT-200-Organization-Company", opt200);

        // 1) GR-Touch-Point
        populateGRTouchPoint(grOrgObj, codiceEform, numpun, codein, null);

        // 2) GR-Company-Contact
        populateGRCompanyContact(grOrgObj, codiceEform, numpun, codein, null);

        // 3) GR-Company-Address
        populateGRCompanyAddress(grOrgObj, codiceEform, numpun, codein, null);

        // 4) GR-Company-BuyerExclusive
        populateGRCompanyBuyerExclusive(grOrgObj, codiceEform, numpun, codein, null);

        // 5) GR-Company
        populateGRCompany(grOrgObj, codiceEform, numpun, codein, null);
        
        // 6) GR-Company-EconomicOperatorExclusive
        //populateGRCompanyEconomicOperatorExclusive(grOrgObj, codiceEform, uuidEform);

        // Aggiungiamo l'oggetto all'array in modo ordinato
        grOrgsArray.add(extractPositionByNumberOrgCode(grOrgsArray, opt200), grOrgObj);
      }
    }
  }
  
  private void populateGROrganisationsOperatori(JSONArray grOrgsArray, String codiceEform, String uuidEform, EntityManager entityManager) throws Exception {

    List<?> listaLotti = new ArrayList<>();
    
    Boolean isAD1 = codiceEform.equals("25") || codiceEform.equals("26") || codiceEform.equals("28");
    
    if(Boolean.FALSE.equals(isAD1)) {
      listaLotti = getDatiCAN(uuidEform, PathJsonEform.GET_LOTTI_CAN, Long.valueOf(0));
    } else {
      listaLotti = getDati(uuidEform, PathJsonEform.GET_LOTTI, null);
    }
    

    List<String> listaditte = new ArrayList<>();
    for (int i = 0; i < listaLotti.size(); i++) {
      String ngara = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).stringValue();

      Long aqoper = getAqoperByNgara(ngara);
      List<?> results = new ArrayList<>();
      if(aqoper != null && Long.valueOf(2).equals(aqoper)) {
        results = getDatiLotti(ngara, PathJsonEform.GR_ORGANISATIONS_OPERATORI_ACCORDO_QUADRO, Long.valueOf(0));
      } else {
        results = getDatiLotti(ngara, PathJsonEform.GR_ORGANISATIONS_OPERATORI, Long.valueOf(0));
      }

      for(Object result : results){
        String dittagare = SqlManager.getValueFromVectorParam(result, 0).stringValue();
        Long tipimp = SqlManager.getValueFromVectorParam(result, 1).longValue();

        //if(aqoper != null && Long.valueOf(2).equals(aqoper)) { //caso AccordoQuadro
        if(tipimp != null && (tipimp == 10 || tipimp == 3)){
          List<?> listRagimp = sqlManager.getListVector("select coddic from ragimp where codime9 = ?", new Object[] { dittagare });
          for(int y = 0; y < listRagimp.size(); y++) {
            String dittaRT  = (String) SqlManager.getValueFromVectorParam(listRagimp.get(y), 0).getValue();
            listaditte.add(dittaRT);
          }
        } else {
          listaditte.add(dittagare);
        }

        //List<?> results = getDati(uuidEform, PathJsonEform.GR_ORGANISATIONS_OPERATORI, Long.valueOf(0));
      }
    }
    listaditte = listaditte.stream()
        .distinct()
        .collect(Collectors.toList());
    for(String ditta : listaditte){

      // "GR-Organisations" e' un array
      JSONObject grOrgObj = new JSONObject();
      /*
      String dittagare = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      Long aqoper = SqlManager.getValueFromVectorParam(result, 1).longValue();

      List<List<String>> listaRag = getListaDitteByDitta(dittagare, aqoper, getCodgarByUuid(uuidEform));

      for(List<String> listaDitte : listaRag){

        for(String ditta : listaDitte){*/

      // "OPT-200-Organization-Company"
      String opt200 = entityManager.getOrCreateValue(PREFISSO_ORG, "impresa", ditta, Boolean.TRUE);
      addIfNotEmpty(grOrgObj, "OPT-200-Organization-Company", opt200);

      // 1) GR-Touch-Point
      populateGRTouchPoint(grOrgObj, codiceEform, null, null, ditta);

      // 2) GR-Company-Contact
      populateGRCompanyContact(grOrgObj, codiceEform, null, null, ditta);

      // 3) GR-Company-Address
      populateGRCompanyAddress(grOrgObj, codiceEform, null, null, ditta);

      // 4) GR-Company-BuyerExclusive
      populateGRCompanyBuyerExclusive(grOrgObj, codiceEform, null, null, ditta);

      // 5) GR-Company
      populateGRCompany(grOrgObj, codiceEform, null, null, ditta);
      
      // 6) GR-Company-EconomicOperatorExclusive
      populateGRCompanyEconomicOperatorExclusive(grOrgObj, codiceEform, null, null, ditta);

      // Aggiungiamo l'oggetto all'array in modo ordinato
      grOrgsArray.add(extractPositionByNumberOrgCode(grOrgsArray, opt200), grOrgObj);
    }
  }
  
  

  // -----------------------------------------------------
  // GR-Touch-Point
  // -----------------------------------------------------
  private void populateGRTouchPoint(JSONObject grOrgObj, String codiceEform, Long numpun, String codein, String ditta) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDatiOrganization(PathJsonEform.GR_TOUCH_POINT, Long.valueOf(0), numpun, codein);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-Touch-Point" e' un array
    JSONArray touchPointArray = new JSONArray();
    JSONObject touchPointObj = new JSONObject();

    // "BT-500-Organization-TouchPoint"
    String bt500 = "";
    addIfNotEmpty(touchPointObj, "BT-500-Organization-TouchPoint", bt500);

    // GR-TouchPoint-Contact
    populateGRTouchPointContact(touchPointObj, codiceEform, numpun, codein);

    // "BT-505-Organization-TouchPoint"
    String bt505 = "";
    addIfNotEmpty(touchPointObj, "BT-505-Organization-TouchPoint", bt505);

    // "OPT-201-Organization-TouchPoint"
    String opt201 = "";
    addIfNotEmpty(touchPointObj, "OPT-201-Organization-TouchPoint", opt201);

    // "BT-16-Organization-TouchPoint"
    String bt16 = "";
    addIfNotEmpty(touchPointObj, "BT-16-Organization-TouchPoint", bt16);

    // "BT-509-Organization-TouchPoint"
    String bt509 = "";
    addIfNotEmpty(touchPointObj, "BT-509-Organization-TouchPoint", bt509);

    // GR-TouchPoint-Address
    populateGRTouchPointAddress(touchPointObj, codiceEform, numpun, codein);

    touchPointArray.add(touchPointObj);
    putIfNotEmpty(grOrgObj,"GR-Touch-Point", touchPointArray);
  }

  // -----------------------------------------------------
  // GR-TouchPoint-Contact
  // -----------------------------------------------------
  private void populateGRTouchPointContact(JSONObject touchPointObj, String codiceEform, Long numpun, String codein) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray contactArray = new JSONArray();
    JSONObject contactObj = new JSONObject();

    // "BT-502-Organization-TouchPoint"
    String bt502 = "";
    addIfNotEmpty(contactObj, "BT-502-Organization-TouchPoint", bt502);
    // "BT-503-Organization-TouchPoint"
    String bt503 = "";
    addIfNotEmpty(contactObj, "BT-503-Organization-TouchPoint", bt503);
    // "BT-506-Organization-TouchPoint"
    String bt506 = "";
    addIfNotEmpty(contactObj, "BT-506-Organization-TouchPoint", bt506);
    // "BT-739-Organization-TouchPoint"
    String bt739 = "";
    addIfNotEmpty(contactObj, "BT-739-Organization-TouchPoint", bt739);

    contactArray.add(contactObj);
    putIfNotEmpty(touchPointObj,"GR-TouchPoint-Contact", contactArray);
  }

  // -----------------------------------------------------
  // GR-TouchPoint-Address
  // -----------------------------------------------------
  private void populateGRTouchPointAddress(JSONObject touchPointObj, String codiceEform, Long numpun, String codein) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray addressArray = new JSONArray();
    JSONObject addressObj = new JSONObject();

    // "BT-510(a)-Organization-TouchPoint"
    String bt510a = "";
    addIfNotEmpty(addressObj, "BT-510(a)-Organization-TouchPoint", bt510a);
    // "BT-507-Organization-TouchPoint"
    String bt507 = "";
    addIfNotEmpty(addressObj, "BT-507-Organization-TouchPoint", bt507);
    // "BT-510(c)-Organization-TouchPoint"
    String bt510c = "";
    addIfNotEmpty(addressObj, "BT-510(c)-Organization-TouchPoint", bt510c);
    // "BT-510(b)-Organization-TouchPoint"
    String bt510b = "";
    addIfNotEmpty(addressObj, "BT-510(b)-Organization-TouchPoint", bt510b);
    // "BT-512-Organization-TouchPoint"
    String bt512 = "";
    addIfNotEmpty(addressObj, "BT-512-Organization-TouchPoint", bt512);
    // "BT-513-Organization-TouchPoint"
    String bt513 = "";
    addIfNotEmpty(addressObj, "BT-513-Organization-TouchPoint", bt513);
    // "BT-514-Organization-TouchPoint"
    String bt514 = "";
    addIfNotEmpty(addressObj, "BT-514-Organization-TouchPoint", bt514);

    addressArray.add(addressObj);
    putIfNotEmpty(touchPointObj,"GR-TouchPoint-Address", addressArray);
  }

  // -----------------------------------------------------
  // GR-Company-Contact
  // -----------------------------------------------------
  private void populateGRCompanyContact(JSONObject grOrgObj, String codiceEform, Long numpun, String codein, String ditta) throws Exception {

    List<?> results = new ArrayList<>();
    if(ditta == null) {
      Long isPuntoContatto = Long.valueOf(0);
      if(numpun != null && !Long.valueOf(0).equals(numpun)) {
        isPuntoContatto = Long.valueOf(1);
      }
      results = getDatiOrganization(PathJsonEform.GR_COMPANY_CONTACT_UFFICI, isPuntoContatto, numpun, codein);
    } else {
      
      results = getDatiOrganization(PathJsonEform.GR_COMPANY_CONTACT_OPERATORI, Long.valueOf(0), null, ditta);
    }
    //JSONArray companyContactArray = new JSONArray();
    for(Object result : results){

      String emai2in = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String emaiin = SqlManager.getValueFromVectorParam(result, 1).stringValue();
      String bt503 = SqlManager.getValueFromVectorParam(result, 2).stringValue();
      String bt739 = SqlManager.getValueFromVectorParam(result, 3).stringValue();
      String bt506 = emai2in != null && !emai2in.isEmpty()?emai2in:emaiin;
      String bt502 = "";

      JSONObject contactObj = new JSONObject();

      addIfNotEmpty(contactObj, "BT-739-Organization-Company", bt739);
      addIfNotEmpty(contactObj, "BT-502-Organization-Company", bt502);
      addIfNotEmpty(contactObj, "BT-506-Organization-Company", bt506);
      addIfNotEmpty(contactObj, "BT-503-Organization-Company", bt503);

      //companyContactArray.add(contactObj);
      putIfNotEmpty(grOrgObj,"GR-Company-Contact", contactObj);
    }
  }

  // -----------------------------------------------------
  // GR-Company-Address
  // -----------------------------------------------------
  private void populateGRCompanyAddress(JSONObject grOrgObj, String codiceEform, Long numpun, String codein, String ditta) throws Exception {

    List<?> results = new ArrayList<>();
    if(ditta == null) {
      Long isPuntoContatto = Long.valueOf(0);
      if(numpun != null && !Long.valueOf(0).equals(numpun)) {
        isPuntoContatto = Long.valueOf(1);
      }
      results = getDatiOrganization(PathJsonEform.GR_COMPANY_ADDRESS_UFFICI, isPuntoContatto, numpun, codein);
    } else {
      
      results = getDatiOrganization(PathJsonEform.GR_COMPANY_ADDRESS_OPERATORI, Long.valueOf(0), null, ditta);
    }
    //JSONArray companyAddressArray = new JSONArray();
    for(Object result : results){

      JSONObject addressObj = new JSONObject();

      String citein = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String proein = SqlManager.getValueFromVectorParam(result, 1).stringValue();
      Long codnaz = SqlManager.getValueFromVectorParam(result, 2).longValue();
      String bt507 = SqlManager.getValueFromVectorParam(result, 3).stringValue();
      String indirizzo = SqlManager.getValueFromVectorParam(result, 4).stringValue();
      String civico = SqlManager.getValueFromVectorParam(result, 5).stringValue();
      String bt512 = SqlManager.getValueFromVectorParam(result, 6).stringValue();
      String bt514 = "";
      String bt513 = "";
      String bt510b = "";
      String bt510c = "";

      String bt510a = "";
      if(indirizzo != null && !indirizzo.isEmpty()) {
        bt510a = indirizzo;
        if(civico != null && !civico.isEmpty())
          bt510a += " n. "+civico;
      }
      
      if(citein != null && !citein.isEmpty()) {
        bt513 = citein;
        if(proein != null && !proein.isEmpty()) {
          bt513 += " ("+proein+")";
        }
      }

      if(codnaz != null) {
        bt514 = getSiglaByCodice(codnaz);
      }

      addIfNotEmpty(addressObj, "BT-514-Organization-Company", bt514);
      addIfNotEmpty(addressObj, "BT-513-Organization-Company", bt513);
      addIfNotEmpty(addressObj, "BT-507-Organization-Company", bt507);
      addIfNotEmpty(addressObj, "BT-510(b)-Organization-Company", bt510b);
      addIfNotEmpty(addressObj, "BT-510(c)-Organization-Company", bt510c);
      addIfNotEmpty(addressObj, "BT-512-Organization-Company", bt512);
      addIfNotEmpty(addressObj, "BT-510(a)-Organization-Company", bt510a);

      //companyAddressArray.add(addressObj);
      putIfNotEmpty(grOrgObj,"GR-Company-Address", addressObj);
    }
  }


  // -----------------------------------------------------
  // GR-Company-BuyerExclusive
  // -----------------------------------------------------
  private void populateGRCompanyBuyerExclusive(JSONObject grOrgObj, String codiceEform, Long numpun, String codein, String ditta) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray buyerExclusiveArray = new JSONArray();
    JSONObject buyerExclusiveObj = new JSONObject();

    // "OPP-052-Organization"
    String opp052 = "";
    addIfNotEmpty(buyerExclusiveObj, "OPP-052-Organization", opp052);
    // "OPP-050-Organization"
    String opp050 = "";
    addIfNotEmpty(buyerExclusiveObj, "OPP-050-Organization", opp050);
    // "OPP-051-Organization"
    String opp051 = "";
    addIfNotEmpty(buyerExclusiveObj, "OPP-051-Organization", opp051);

    buyerExclusiveArray.add(buyerExclusiveObj);
    putIfNotEmpty(grOrgObj,"GR-Company-BuyerExclusive", buyerExclusiveArray);
  }

  // -----------------------------------------------------
  // GR-Company
  // -----------------------------------------------------
  private void populateGRCompany(JSONObject grOrgObj, String codiceEform, Long numpun, String codein, String ditta) throws Exception {

    List<?> results = new ArrayList<>();
    if(ditta == null) {
      Long isPuntoContatto = Long.valueOf(0);
      if(numpun != null && !Long.valueOf(0).equals(numpun)) {
        isPuntoContatto = Long.valueOf(1);
      }
      results = getDatiOrganization(PathJsonEform.GR_COMPANY_UFFICI, isPuntoContatto, numpun, codein);
    } else {
      results = getDatiOrganization(PathJsonEform.GR_COMPANY_OPERATORI, Long.valueOf(0), null, ditta);
    }
    for(Object result : results){

      JSONObject companyObj = new JSONObject();

      String bt500 = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String cfein = SqlManager.getValueFromVectorParam(result, 1).stringValue();
      String ivaein = SqlManager.getValueFromVectorParam(result, 2).stringValue();
      String bt505 = SqlManager.getValueFromVectorParam(result, 3).stringValue();
      String bt16 = "";
      String bt509 = "";


      addIfNotEmpty(companyObj, "BT-16-Organization-Company", bt16);
      addIfNotEmpty(companyObj, "BT-505-Organization-Company", bt505);

      // "GR-Organisation-Identifier" (array)
      //JSONArray orgIdArray = new JSONArray();
      JSONObject orgIdObj = new JSONObject();
      if(cfein != null && !cfein.isEmpty()) {
        addIfNotEmpty(orgIdObj, "BT-501-Organization-Company", cfein);
      } else if(ivaein != null && !ivaein.isEmpty()) {
        addIfNotEmpty(orgIdObj, "BT-501-Organization-Company", ivaein);
      }
      putIfNotEmpty(companyObj,"GR-Organisation-Identifier", orgIdObj);

      addIfNotEmpty(companyObj, "BT-500-Organization-Company", bt500);
      addIfNotEmpty(companyObj, "BT-509-Organization-Company", bt509);

      //companyArray.add(companyObj);
      putIfNotEmpty(grOrgObj,"GR-Company", companyObj);
    }
  }

  // =========================================================================
  //                        GR-UBO-Subsection
  // =========================================================================
  private void populateGRUBOSubsection(JSONObject orgSectionObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-UBO-Subsection" e' un array
    JSONArray uboSubsectionArray = new JSONArray();
    JSONObject uboSubObj = new JSONObject();

    // GR-UBO
    populateGRUBO(uboSubObj, codiceEform, uuidEform);

    uboSubsectionArray.add(uboSubObj);
    putIfNotEmpty(orgSectionObj,"GR-UBO-Subsection", uboSubsectionArray);
  }

  // =========================================================================
  //                        GR-UBO
  // =========================================================================
  private void populateGRUBO(JSONObject uboSubObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-UBO" e' un array
    JSONArray grUboArray = new JSONArray();
    JSONObject grUboObj = new JSONObject();

    // "BT-514-UBO"
    String bt514 = "";
    addIfNotEmpty(grUboObj, "BT-514-UBO", bt514);
    // "BT-510(c)-UBO"
    String bt510c = "";
    addIfNotEmpty(grUboObj, "BT-510(c)-UBO", bt510c);
    // "BT-512-UBO"
    String bt512 = "";
    addIfNotEmpty(grUboObj, "BT-512-UBO", bt512);
    // "OPT-160-UBO"
    String opt160 = "";
    addIfNotEmpty(grUboObj, "OPT-160-UBO", opt160);
    // "OPT-202-UBO"
    String opt202 = "";
    addIfNotEmpty(grUboObj, "OPT-202-UBO", opt202);
    // "BT-500-UBO"
    String bt500 = "";
    addIfNotEmpty(grUboObj, "BT-500-UBO", bt500);
    // "BT-510(a)-UBO"
    String bt510a = "";
    addIfNotEmpty(grUboObj, "BT-510(a)-UBO", bt510a);
    // "BT-506-UBO"
    String bt506 = "";
    addIfNotEmpty(grUboObj, "BT-506-UBO", bt506);

    // "GR-UBO-Winner" (array)
    JSONArray uboWinnerArray = new JSONArray();
    JSONObject uboWinnerObj = new JSONObject();
    // "BT-706-UBO"
    String bt706 = "";
    addIfNotEmpty(uboWinnerObj, "BT-706-UBO", bt706);
    uboWinnerArray.add(uboWinnerObj);
    putIfNotEmpty(grUboObj,"GR-UBO-Winner", uboWinnerArray);

    // "BT-513-UBO"
    String bt513 = "";
    addIfNotEmpty(grUboObj, "BT-513-UBO", bt513);
    // "BT-739-UBO"
    String bt739 = "";
    addIfNotEmpty(grUboObj, "BT-739-UBO", bt739);
    // "BT-510(b)-UBO"
    String bt510b = "";
    addIfNotEmpty(grUboObj, "BT-510(b)-UBO", bt510b);
    // "BT-503-UBO"
    String bt503 = "";
    addIfNotEmpty(grUboObj, "BT-503-UBO", bt503);
    // "BT-507-UBO"
    String bt507 = "";
    addIfNotEmpty(grUboObj, "BT-507-UBO", bt507);

    grUboArray.add(grUboObj);
    putIfNotEmpty(uboSubObj,"GR-UBO", grUboArray);
  }

  // =========================================================================
  //                  GR-Organisations-Subsection
  // =========================================================================
  private void populateGROrganisationsSubsection(JSONObject orgSectionObj, String codiceEform, String uuidEform, EntityManager entityManager) throws Exception {

    JSONObject orgSubObj = new JSONObject();

    String famiglia = getFamilyByCodEform(codiceEform);
    if("CAN".equals(famiglia)) {
      populateGROrganisations(orgSubObj, codiceEform, uuidEform, entityManager);
    }

    putIfNotEmpty(orgSectionObj,"GR-Organisations-Subsection", orgSubObj);
  }  

  // -----------------------------------------------------
  // GR-Company-EconomicOperatorExclusive
  // -----------------------------------------------------
  private void populateGRCompanyEconomicOperatorExclusive(JSONObject grOrgObj, String codiceEform, Long numpun, String codein, String ditta) throws Exception {

    List<?> results = getDatiOrganization(PathJsonEform.GR_COMPANY_ECONOMICOPERATOREXCLUSIVE, Long.valueOf(0), null, ditta);

    //JSONArray econOpArray = new JSONArray();
    for(Object result : results){

      Long cladim = SqlManager.getValueFromVectorParam(result, 0).longValue();
      String bt746 = "";
      String bt633 = "";
      String bt165 = "";
      
      if(cladim != null && (Long.valueOf(1).equals(cladim) || Long.valueOf(2).equals(cladim) || Long.valueOf(3).equals(cladim))) {
        bt165 = "sme";
      }

      JSONObject econOpObj = new JSONObject();

      addIfNotEmpty(econOpObj, "BT-746-Organization", bt746);
      addIfNotEmpty(econOpObj, "BT-165-Organization-Company", bt165);
      addIfNotEmpty(econOpObj, "BT-633-Organization", bt633);

      // "GR-Organisation-UBO-Reference"
      JSONArray uboRefArray = new JSONArray();
      JSONObject uboRefObj = new JSONObject();
      // "OPT-302-Organization"
      String opt302 = "";
      addIfNotEmpty(uboRefObj, "OPT-302-Organization", opt302);
      uboRefArray.add(uboRefObj);

      putIfNotEmpty(econOpObj,"GR-Organisation-UBO-Reference", uboRefArray);

      //econOpArray.add(econOpObj);
      putIfNotEmpty(grOrgObj,"GR-Company-EconomicOperatorExclusive", econOpObj);
    }
  }




  // -----------------------------------------------------
  // GR-LotsGroup
  // -----------------------------------------------------
  public JSONObject populateGRLotsGroup(JSONObject parent, String codiceEform, String uuidEform) throws Exception {
    // Creiamo l'array "GR-LotsGroup"
    JSONArray lotsGroupArray = new JSONArray();

    // Oggetto singolo all'interno dell'array
    JSONObject lotsGroupObj = new JSONObject();

    // (1) GR-LotsGroup-Purpose
    populateGRLotsGroupPurpose(lotsGroupObj, codiceEform, uuidEform);

    // (2) GR-LotsGroup-TenderingTerms
    populateGRLotsGroupTenderingTerms(lotsGroupObj, codiceEform, uuidEform);

    // Aggiungiamo l'oggetto all'array
    lotsGroupArray.add(lotsGroupObj);

    // Mettiamo l'array in parent
    putIfNotEmpty(parent,"GR-LotsGroup", lotsGroupArray);

    return parent;
  }

  // =========================================================================
  //                       GR-LotsGroup-Purpose
  // =========================================================================
  private void populateGRLotsGroupPurpose(JSONObject lotsGroupObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-LotsGroup-Purpose" e' un array
    JSONArray purposeArray = new JSONArray();
    JSONObject purposeObj = new JSONObject();

    // 1. "BT-137-LotsGroup"
    String bt137 = "";
    addIfNotEmpty(purposeObj, "BT-137-LotsGroup", bt137);

    // 2. Sotto-sezione "GR-LotsGroup-Description"
    populateGRLotsGroupDescription(purposeObj, codiceEform, uuidEform);

    // 3. Sotto-sezione "GR-LotsGroup-Scope"
    populateGRLotsGroupScope(purposeObj, codiceEform, uuidEform);

    // 4. Sotto-sezione "GR-LotsGroup-FrameworkAgreement"
    populateGRLotsGroupFrameworkAgreement(purposeObj, codiceEform, uuidEform);

    // 5. Sotto-sezione "GR-LotsGroup-AdditionalInformation"
    populateGRLotsGroupAdditionalInformation(purposeObj, codiceEform, uuidEform);

    // Aggiungiamo purposeObj all'array
    purposeArray.add(purposeObj);

    // Mettiamo l'array in lotsGroupObj
    putIfNotEmpty(lotsGroupObj,"GR-LotsGroup-Purpose", purposeArray);
  }

  // -----------------------------------------------------
  // GR-LotsGroup-Description
  // -----------------------------------------------------
  private void populateGRLotsGroupDescription(JSONObject purposeObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-LotsGroup-Description" e' un array
    JSONArray descriptionArray = new JSONArray();
    JSONObject descObj = new JSONObject();

    // "BT-21-LotsGroup"
    String bt21 = "";
    addIfNotEmpty(descObj, "BT-21-LotsGroup", bt21);
    // "BT-22-LotsGroup"
    String bt22 = "";
    addIfNotEmpty(descObj, "BT-22-LotsGroup", bt22);
    // "BT-24-LotsGroup"
    String bt24 = "";
    addIfNotEmpty(descObj, "BT-24-LotsGroup", bt24);

    descriptionArray.add(descObj);
    putIfNotEmpty(purposeObj,"GR-LotsGroup-Description", descriptionArray);
  }

  // -----------------------------------------------------
  // GR-LotsGroup-Scope
  // -----------------------------------------------------
  private void populateGRLotsGroupScope(JSONObject purposeObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray scopeArray = new JSONArray();
    JSONObject scopeObj = new JSONObject();

    // "BT-726-LotsGroup"
    String bt726 = "";
    addIfNotEmpty(scopeObj, "BT-726-LotsGroup", bt726);
    // "BT-27-LotsGroup"
    String bt27 = "";
    addIfNotEmpty(scopeObj, "BT-27-LotsGroup", bt27);

    scopeArray.add(scopeObj);
    putIfNotEmpty(purposeObj,"GR-LotsGroup-Scope", scopeArray);
  }

  // -----------------------------------------------------
  // GR-LotsGroup-FrameworkAgreement
  // -----------------------------------------------------
  private void populateGRLotsGroupFrameworkAgreement(JSONObject purposeObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray frameworkArray = new JSONArray();
    JSONObject frameworkObj = new JSONObject();

    // "BT-157-LotsGroup"
    String bt157 = "";
    addIfNotEmpty(frameworkObj, "BT-157-LotsGroup", bt157);
    // "BT-271-LotsGroup"
    String bt271 = "";
    addIfNotEmpty(frameworkObj, "BT-271-LotsGroup", bt271);

    frameworkArray.add(frameworkObj);
    putIfNotEmpty(purposeObj,"GR-LotsGroup-FrameworkAgreement", frameworkArray);
  }

  // -----------------------------------------------------
  // GR-LotsGroup-AdditionalInformation
  // -----------------------------------------------------
  private void populateGRLotsGroupAdditionalInformation(JSONObject purposeObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray addInfoArray = new JSONArray();
    JSONObject addInfoObj = new JSONObject();

    // "BT-300-LotsGroup"
    String bt300 = "";
    addIfNotEmpty(addInfoObj, "BT-300-LotsGroup", bt300);

    addInfoArray.add(addInfoObj);
    putIfNotEmpty(purposeObj,"GR-LotsGroup-AdditionalInformation", addInfoArray);
  }

  // =========================================================================
  //                    GR-LotsGroup-TenderingTerms
  // =========================================================================
  private void populateGRLotsGroupTenderingTerms(JSONObject lotsGroupObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-LotsGroup-TenderingTerms" e' un array
    JSONArray tenderingTermsArray = new JSONArray();
    JSONObject tenderingTermsObj = new JSONObject();

    // 1. GR-LotsGroup-AwardCriteria
    populateGRLotsGroupAwardCriteria(tenderingTermsObj, codiceEform, uuidEform);

    // Aggiungiamo l'oggetto all'array
    tenderingTermsArray.add(tenderingTermsObj);
    putIfNotEmpty(lotsGroupObj,"GR-LotsGroup-TenderingTerms", tenderingTermsArray);
  }

  // -----------------------------------------------------
  // GR-LotsGroup-AwardCriteria
  // -----------------------------------------------------
  private void populateGRLotsGroupAwardCriteria(JSONObject tenderingTermsObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray awardCriteriaArray = new JSONArray();
    JSONObject awardCriteriaObj = new JSONObject();

    // "BT-733-LotsGroup"
    String bt733 = "";
    addIfNotEmpty(awardCriteriaObj, "BT-733-LotsGroup", bt733);

    // Sotto-sezione "GR-LotsGroup-AwardCriteria-Criterion"
    populateGRLotsGroupAwardCriteriaCriterion(awardCriteriaObj, codiceEform, uuidEform);

    // "BT-543-LotsGroup"
    String bt543 = "";
    addIfNotEmpty(awardCriteriaObj, "BT-543-LotsGroup", bt543);

    awardCriteriaArray.add(awardCriteriaObj);
    putIfNotEmpty(tenderingTermsObj,"GR-LotsGroup-AwardCriteria", awardCriteriaArray);
  }

  // -----------------------------------------------------
  // GR-LotsGroup-AwardCriteria-Criterion
  // -----------------------------------------------------
  private void populateGRLotsGroupAwardCriteriaCriterion(JSONObject awardCriteriaObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray criterionArray = new JSONArray();
    JSONObject criterionObj = new JSONObject();

    // "BT-539-LotsGroup"
    String bt539 = "";
    addIfNotEmpty(criterionObj, "BT-539-LotsGroup", bt539);
    // "BT-734-LotsGroup"
    String bt734 = "";
    addIfNotEmpty(criterionObj, "BT-734-LotsGroup", bt734);

    // Sotto-sezione: GR-LotsGroup-AwardCriteria-Criterion-Parameters
    populateGRLotsGroupAwardCriteriaCriterionParameters(criterionObj, codiceEform, uuidEform);

    // "BT-540-LotsGroup"
    String bt540 = "";
    addIfNotEmpty(criterionObj, "BT-540-LotsGroup", bt540);

    criterionArray.add(criterionObj);
    putIfNotEmpty(awardCriteriaObj,"GR-LotsGroup-AwardCriteria-Criterion", criterionArray);
  }

  // -----------------------------------------------------
  // GR-LotsGroup-AwardCriteria-Criterion-Parameters
  // -----------------------------------------------------
  private void populateGRLotsGroupAwardCriteriaCriterionParameters(JSONObject criterionObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-LotsGroup-AwardCriteria-Criterion-Parameters" e' un array
    JSONArray critParamsArray = new JSONArray();
    JSONObject critParamsObj = new JSONObject();

    // 1. GR-LotsGroup-AwardCriteria-ThresholdCriterion-Parameter
    populateGRLotsGroupAwardCriteriaThresholdParam(critParamsObj, codiceEform, uuidEform);

    // 2. GR-LotsGroup-AwardCriteria-FixedCriterion-Parameter
    populateGRLotsGroupAwardCriteriaFixedParam(critParamsObj, codiceEform, uuidEform);

    // 3. GR-LotsGroup-AwardCriteria-WeightCriterion-Parameter
    populateGRLotsGroupAwardCriteriaWeightParam(critParamsObj, codiceEform, uuidEform);

    critParamsArray.add(critParamsObj);
    putIfNotEmpty(criterionObj,"GR-LotsGroup-AwardCriteria-Criterion-Parameters", critParamsArray);
  }

  // -----------------------------------------------------
  //  GR-LotsGroup-AwardCriteria-ThresholdCriterion-Parameter
  // -----------------------------------------------------
  private void populateGRLotsGroupAwardCriteriaThresholdParam(JSONObject critParamsObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray thresholdParamArray = new JSONArray();
    JSONObject thresholdParamObj = new JSONObject();

    // "BT-5423-LotsGroup"
    String bt5423 = "";
    addIfNotEmpty(thresholdParamObj, "BT-5423-LotsGroup", bt5423);
    // "BT-541-LotsGroup-ThresholdNumber"
    String bt541Threshold = "";
    addIfNotEmpty(thresholdParamObj, "BT-541-LotsGroup-ThresholdNumber", bt541Threshold);

    thresholdParamArray.add(thresholdParamObj);
    putIfNotEmpty(critParamsObj,"GR-LotsGroup-AwardCriteria-ThresholdCriterion-Parameter", thresholdParamArray);
  }

  // -----------------------------------------------------
  //  GR-LotsGroup-AwardCriteria-FixedCriterion-Parameter
  // -----------------------------------------------------
  private void populateGRLotsGroupAwardCriteriaFixedParam(JSONObject critParamsObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray fixedParamArray = new JSONArray();
    JSONObject fixedParamObj = new JSONObject();

    // "BT-5422-LotsGroup"
    String bt5422 = "";
    addIfNotEmpty(fixedParamObj, "BT-5422-LotsGroup", bt5422);
    // "BT-541-LotsGroup-FixedNumber"
    String bt541Fixed = "";
    addIfNotEmpty(fixedParamObj, "BT-541-LotsGroup-FixedNumber", bt541Fixed);

    fixedParamArray.add(fixedParamObj);
    putIfNotEmpty(critParamsObj,"GR-LotsGroup-AwardCriteria-FixedCriterion-Parameter", fixedParamArray);
  }

  // -----------------------------------------------------
  //  GR-LotsGroup-AwardCriteria-WeightCriterion-Parameter
  // -----------------------------------------------------
  private void populateGRLotsGroupAwardCriteriaWeightParam(JSONObject critParamsObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray weightParamArray = new JSONArray();
    JSONObject weightParamObj = new JSONObject();

    // "BT-541-LotsGroup-WeightNumber"
    String bt541Weight = "";
    addIfNotEmpty(weightParamObj, "BT-541-LotsGroup-WeightNumber", bt541Weight);
    // "BT-5421-LotsGroup"
    String bt5421 = "";
    addIfNotEmpty(weightParamObj, "BT-5421-LotsGroup", bt5421);

    weightParamArray.add(weightParamObj);
    putIfNotEmpty(critParamsObj,"GR-LotsGroup-AwardCriteria-WeightCriterion-Parameter", weightParamArray);
  }

  // =========================================================================
  //          GR-Lot  
  // =========================================================================
  public JSONObject populateGRLot(JSONObject parent, String codiceEform, String uuidEform, EntityManager entityManager) throws Exception {
    // "GR-Lot" e' un array
    JSONArray grLotArray = new JSONArray();
    List<?> listaLotti = new ArrayList<>();
    String famiglia = getFamilyByCodEform(codiceEform);
    
    Boolean isAD1 = codiceEform.equals("25") || codiceEform.equals("26") || codiceEform.equals("28");
    
    if("CAN".equals(famiglia) && Boolean.FALSE.equals(isAD1)) {
      listaLotti = getDatiCAN(uuidEform, PathJsonEform.GET_LOTTI_CAN, Long.valueOf(0));
    } else {
      listaLotti = getDati(uuidEform, PathJsonEform.GET_LOTTI, null);
    }
    
    for (int i = 0; i < listaLotti.size(); i++) {
      String ngara = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).stringValue();

      List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT, Long.valueOf(0));

      for(Object result : results){

        String bt137Lot = SqlManager.getValueFromVectorParam(result, 0).stringValue();
        
        // Creiamo l'oggetto singolo all'interno dell'array
        JSONObject grLotObj = new JSONObject();
        
        addIfNotEmpty(grLotObj, "BT-137-Lot", getLotIdentByCodiga(bt137Lot));
        
        // 2. GR-Lot-TenderingTerms (parziale)
        populateGRLotTenderingTerms(grLotObj, codiceEform, ngara, uuidEform, entityManager);
        
        // 2) GR-Lot-SubmissionInfo
        populateGRLotSubmissionInfo(grLotObj, uuidEform, codiceEform, ngara, entityManager);
        
        // 3) GR-Lot-Review
        populateGRLotReview(grLotObj, codiceEform, ngara, entityManager);
        
        // 4) GR-Lot-Purpose
        populateGRLotPurpose(grLotObj, codiceEform, ngara);
        
        // Aggiungiamo grLotObj all'array
        grLotArray.add(grLotObj);
      }
    }
    // Mettiamo l'array in parent
    putIfNotEmpty(parent,"GR-Lot", grLotArray);

    return parent;
  }

  
  // =========================================================================
  //          GR-Lot-TenderingTerms
  // =========================================================================
  private void populateGRLotTenderingTerms(JSONObject grLotObj, String codiceEform, String ngara, String uuidEform, EntityManager entityManager) throws Exception {

    JSONObject tenderingTermsObj = new JSONObject();

    // GR-Lot-ExecutionRequirements
    populateGRLotExecutionRequirements(tenderingTermsObj, codiceEform, ngara);

    // GR-FSR
    populateGRFSR(tenderingTermsObj, codiceEform, ngara);

    // GR-Lot-ElectronicCatalog
    populateGRLotElectronicCatalog(tenderingTermsObj, codiceEform, ngara);

    // GR-Lot-TenderEval
    populateGRLotTenderEval(tenderingTermsObj, codiceEform, ngara);

    // GR-Nda
    populateGRNda(tenderingTermsObj, codiceEform, ngara);

    // GR-Lot-AwardCriteria
    populateGRLotAwardCriteria(tenderingTermsObj, codiceEform, ngara);

    // GR-Lot-ElectronicSignatureRequired
    populateGRLotElectronicSignatureRequired(tenderingTermsObj, codiceEform, ngara);

    // GR-Lot-Variants
    populateGRLotVariants(tenderingTermsObj, codiceEform, ngara);

    // GR-ReservedParticipation
    populateGRReservedParticipation(tenderingTermsObj, codiceEform, ngara);

    // GR-Lot-TenderingTerms-CVD
    populateGRLotTenderingTermsCVD(tenderingTermsObj, codiceEform, ngara);

    // GR-Recurrence
    populateGRRecurrence(tenderingTermsObj, codiceEform, ngara);

    // GR-Lot-EInvoicing
    populateGRLotEInvoicing(tenderingTermsObj, codiceEform, ngara);

    // GR-Lot-FinancialTerms
    populateGRLotFinancialTerms(tenderingTermsObj, codiceEform, ngara);

    // GR-Lot-SelectionCriteria
    populateGRLotSelectionCriteria(tenderingTermsObj, codiceEform, ngara);

    // GR-Lot-SelectionCriteria-Source
    populateGRLotSelectionCriteriaSource(tenderingTermsObj, codiceEform, ngara, uuidEform);

    // GR-Lot-SecurityClearance2
    populateGRLotSecurityClearance2(tenderingTermsObj, codiceEform, ngara);

    // GR-Lot-PostAwardProcess
    populateGRLotPostAwardProcess(tenderingTermsObj, codiceEform, ngara);

    // GR-Lot-Candidates
    populateGRLotCandidates(tenderingTermsObj, codiceEform, ngara);

    // GR-Lot-TendererQualification
    populateGRLotTendererQualification(tenderingTermsObj, codiceEform, ngara);

    // GR-Lot-TenderReceipt (OPZIONALE, se presente)
    populateGRLotTenderReceipt(tenderingTermsObj, codiceEform, ngara, entityManager);

    // GR-Lot-Requirements2SecurityClearance1
    populateGRLotRequirements2SecurityClearance1(tenderingTermsObj, codiceEform, ngara);

    // GR-Lot-AwardingConsequences
    populateGRLotAwardingConsequences(tenderingTermsObj, codiceEform, ngara);

    // GR-Lot-SubcontractRequest
    populateGRLotSubcontractRequest(tenderingTermsObj, codiceEform, ngara);

    // GR-Lot-SubcontractTerms
    populateGRLotSubcontractTerms(tenderingTermsObj, codiceEform, ngara);

    // GR-LateTendererInformation
    populateGRLateTendererInformation(tenderingTermsObj, codiceEform, ngara);

    // GR-Lot-EconomicOperatorShortlist
    populateGRLotEconomicOperatorShortlist(tenderingTermsObj, codiceEform, ngara);

    // GR-Lot-PrizeInformation
    populateGRLotPrizeInformation(tenderingTermsObj, codiceEform, ngara);

    //tenderingTermsArray.add(tenderingTermsObj);
    putIfNotEmpty(grLotObj,"GR-Lot-TenderingTerms", tenderingTermsObj);


  }

  // -----------------------------------------------------
  // GR-Lot-ExecutionRequirements
  // -----------------------------------------------------
  private void populateGRLotExecutionRequirements(JSONObject tenderingTermsObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray execReqArray = new JSONArray();
    JSONObject execReqObj = new JSONObject();

    // Sotto-sezione: GR-Lot-PerformanceTerms
    populateGRLotPerformanceTerms(execReqObj, codiceEform, ngara);

    // Sotto-sezione: GR-Lot-ReservedExecution
    populateGRLotReservedExecution(execReqObj, codiceEform, ngara);

    execReqArray.add(execReqObj);
    putIfNotEmpty(tenderingTermsObj,"GR-Lot-ExecutionRequirements", execReqArray);
  }
  // -----------------------------------------------------
  // GR-Lot-PerformanceTerms
  // -----------------------------------------------------
  private void populateGRLotPerformanceTerms(JSONObject execReqObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray performanceArray = new JSONArray();
    JSONObject perfObj = new JSONObject();

    // "BT-70-Lot"
    String bt70 = "";
    addIfNotEmpty(perfObj, "BT-70-Lot", bt70);
    // "OPT-060-Lot"
    String opt60 = "";
    addIfNotEmpty(perfObj, "OPT-060-Lot", opt60);

    performanceArray.add(perfObj);
    putIfNotEmpty(execReqObj,"GR-Lot-PerformanceTerms", performanceArray);
  }
  // -----------------------------------------------------
  // GR-Lot-ReservedExecution
  // -----------------------------------------------------
  private void populateGRLotReservedExecution(JSONObject execReqObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_RESERVEDEXECUTION, Long.valueOf(0));

    //JSONArray reservedExecArray = new JSONArray();
    for(Object result : results){

      
      String bt736 = SqlManager.getValueFromVectorParam(result, 0).stringValue();

      JSONObject reservedExecObj = new JSONObject();

      addIfNotEmpty(reservedExecObj, "BT-736-Lot", getValoreByCodice(bt736));
  
      //reservedExecArray.add(reservedExecObj);
      putIfNotEmpty(execReqObj,"GR-Lot-ReservedExecution", reservedExecObj);
    }
  }

  // -----------------------------------------------------
  // GR-FSR
  // -----------------------------------------------------
  private void populateGRFSR(JSONObject tenderingTermsObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray fsrArray = new JSONArray();
    JSONObject fsrObj = new JSONObject();

    // "BT-681-Lot"
    String bt681 = "";
    addIfNotEmpty(fsrObj, "BT-681-Lot", bt681);

    fsrArray.add(fsrObj);
    putIfNotEmpty(tenderingTermsObj,"GR-FSR", fsrArray);
  }

  // -----------------------------------------------------
  // GR-Lot-ElectronicCatalog
  // -----------------------------------------------------
  private void populateGRLotElectronicCatalog(JSONObject tenderingTermsObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray eCatalogArray = new JSONArray();
    JSONObject eCatalogObj = new JSONObject();

    // "BT-764-Lot"
    String bt764 = "allowed";
    addIfNotEmpty(eCatalogObj, "BT-764-Lot", bt764);

    eCatalogArray.add(eCatalogObj);
    putIfNotEmpty(tenderingTermsObj,"GR-Lot-ElectronicCatalog", eCatalogArray);
  }

  // -----------------------------------------------------
  // GR-Lot-TenderEval
  // -----------------------------------------------------
  private void populateGRLotTenderEval(JSONObject tenderingTermsObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray tenderEvalArray = new JSONArray();
    JSONObject tenderEvalObj = new JSONObject();

    // "OPT-301-Lot-TenderEval"
    String opt301TenderEval = "";
    addIfNotEmpty(tenderEvalObj, "OPT-301-Lot-TenderEval", opt301TenderEval);

    tenderEvalArray.add(tenderEvalObj);
    putIfNotEmpty(tenderingTermsObj,"GR-Lot-TenderEval", tenderEvalArray);
  }

  // -----------------------------------------------------
  // GR-Nda
  // -----------------------------------------------------
  private void populateGRNda(JSONObject tenderingTermsObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray ndaArray = new JSONArray();
    JSONObject ndaObj = new JSONObject();

    // "BT-801-Lot"
    String bt801 = "";
    addIfNotEmpty(ndaObj, "BT-801-Lot", bt801);
    // "BT-802-Lot"
    String bt802 = "";
    addIfNotEmpty(ndaObj, "BT-802-Lot", bt802);

    ndaArray.add(ndaObj);
    putIfNotEmpty(tenderingTermsObj,"GR-Nda", ndaArray);
  }

  // -----------------------------------------------------
  // GR-Lot-AwardCriteria
  // -----------------------------------------------------
  private void populateGRLotAwardCriteria(JSONObject tenderingTermsObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray awardCriteriaArray = new JSONArray();
    JSONObject awardCriteriaObj = new JSONObject();

    // Sotto-sezione: GR-Lot-AwardCriteria-Criterion
    populateGRLotAwardCriteriaCriterion(awardCriteriaObj, codiceEform, ngara);

    // "BT-733-Lot"
    String bt733 = "";
    addIfNotEmpty(awardCriteriaObj, "BT-733-Lot", bt733);
    // "BT-543-Lot"
    String bt543 = "";
    addIfNotEmpty(awardCriteriaObj, "BT-543-Lot", bt543);

    awardCriteriaArray.add(awardCriteriaObj);
    putIfNotEmpty(tenderingTermsObj,"GR-Lot-AwardCriteria", awardCriteriaArray);
  }
  // -----------------------------------------------------
  // GR-Lot-AwardCriteria-Criterion
  // -----------------------------------------------------
  private void populateGRLotAwardCriteriaCriterion(JSONObject awardCriteriaObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_AWARDCRITERIA_CRITERION, Long.valueOf(0));

    JSONArray criterionArray = new JSONArray();
    for(Object result : results){

      Long modlicg = SqlManager.getValueFromVectorParam(result, 0).longValue();
      Long tippar = SqlManager.getValueFromVectorParam(result, 1).longValue();
      String despar = SqlManager.getValueFromVectorParam(result, 2).stringValue();
      Double maxpun = SqlManager.getValueFromVectorParam(result, 3).doubleValue();
      Double minpun = SqlManager.getValueFromVectorParam(result, 4).doubleValue();
      
      
      HashMap<String, Object> datiCriterio = getCriterioByModlicgTippar(modlicg, tippar, despar, maxpun, minpun);
      
      String bt734 = "";

      JSONObject criterionObj = new JSONObject();
      
      addIfNotEmpty(criterionObj, "BT-539-Lot", datiCriterio.get("BT-539-Lot"));
      addIfNotEmpty(criterionObj, "BT-540-Lot", datiCriterio.get("BT-540-Lot"));
      addIfNotEmpty(criterionObj, "BT-734-Lot", bt734);
      
      // Sotto-sezione: GR-Lot-AwardCriteria-Criterion-Parameters
      populateGRLotAwardCriteriaCriterionParameters(criterionObj, codiceEform, ngara, datiCriterio);
      
      criterionArray.add(criterionObj);
      putIfNotEmpty(awardCriteriaObj,"GR-Lot-AwardCriteria-Criterion", criterionArray);
      
      //Caso particolare in cui ci sono diversi criteri in una gara che non e' OEPV 
      // a causa di una copia di una gara OEPV
      if(modlicg == null || !Long.valueOf(6).equals(modlicg)){
        break;
      }
    }
  }

  // -----------------------------------------------------
  // GR-Lot-AwardCriteria-Criterion-Parameters
  // -----------------------------------------------------
  private void populateGRLotAwardCriteriaCriterionParameters(JSONObject criterionObj, String codiceEform, String ngara, HashMap<String, Object> datiCriterio) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-Lot-AwardCriteria-Criterion-Parameters" e' un array
    JSONArray critParamsArray = new JSONArray();
    JSONObject critParamsObj = new JSONObject();

    // 1) GR-Lot-AwardCriteria-ThresholdCriterion-Parameter
    populateGRLotAwardCriteriaThresholdParam(critParamsObj, codiceEform, ngara, datiCriterio);

    // 2) GR-Lot-AwardCriteria-FixedCriterion-Parameter
    populateGRLotAwardCriteriaFixedParam(critParamsObj, codiceEform, ngara);

    // 3) GR-Lot-AwardCriteria-WeightCriterion-Parameter
    populateGRLotAwardCriteriaWeightParam(critParamsObj, codiceEform, ngara, datiCriterio);

    critParamsArray.add(critParamsObj);
    putIfNotEmpty(criterionObj,"GR-Lot-AwardCriteria-Criterion-Parameters", critParamsArray);
  }
  // -----------------------------------------------------
  // GR-Lot-AwardCriteria-ThresholdCriterion-Parameter
  // -----------------------------------------------------

  private void populateGRLotAwardCriteriaThresholdParam(JSONObject critParamsObj, String codiceEform, String ngara, HashMap<String, Object> datiCriterio) throws Exception {
    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray thresholdArray = new JSONArray();
    JSONObject thresholdObj = new JSONObject();

    addIfNotEmpty(thresholdObj, "BT-5423-Lot", datiCriterio.get("BT-5423-Lot"));
    addIfNotEmpty(thresholdObj, "BT-541-Lot-ThresholdNumber", datiCriterio.get("BT-541-Lot-ThresholdNumber"));

    thresholdArray.add(thresholdObj);
    putIfNotEmpty(critParamsObj,"GR-Lot-AwardCriteria-ThresholdCriterion-Parameter", thresholdArray);
  }
  // -----------------------------------------------------
  // GR-Lot-AwardCriteria-FixedCriterion-Parameter
  // -----------------------------------------------------
  private void populateGRLotAwardCriteriaFixedParam(JSONObject critParamsObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray fixedParamArray = new JSONArray();
    JSONObject fixedParamObj = new JSONObject();

    // "BT-541-Lot-FixedNumber"
    String bt541Fix = "";
    addIfNotEmpty(fixedParamObj, "BT-541-Lot-FixedNumber", bt541Fix);
    // "BT-5422-Lot"
    String bt5422 = "";
    addIfNotEmpty(fixedParamObj, "BT-5422-Lot", bt5422);

    fixedParamArray.add(fixedParamObj);
    putIfNotEmpty(critParamsObj,"GR-Lot-AwardCriteria-FixedCriterion-Parameter", fixedParamArray);
  }
  // -----------------------------------------------------
  // GR-Lot-AwardCriteria-WeightCriterion-Parameter
  // -----------------------------------------------------
  private void populateGRLotAwardCriteriaWeightParam(JSONObject critParamsObj, String codiceEform, String ngara, HashMap<String, Object> datiCriterio) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray weightParamArray = new JSONArray();
    JSONObject weightParamObj = new JSONObject();

    addIfNotEmpty(weightParamObj, "BT-541-Lot-WeightNumber", datiCriterio.get("BT-541-Lot-WeightNumber"));
    addIfNotEmpty(weightParamObj, "BT-5421-Lot", datiCriterio.get("BT-5421-Lot"));

    weightParamArray.add(weightParamObj);
    putIfNotEmpty(critParamsObj,"GR-Lot-AwardCriteria-WeightCriterion-Parameter", weightParamArray);
  }

  // -----------------------------------------------------
  // GR-Lot-ElectronicSignatureRequired
  // -----------------------------------------------------
  private void populateGRLotElectronicSignatureRequired(JSONObject tenderingObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray esigArray = new JSONArray();
    JSONObject esigObj = new JSONObject();

    // "BT-744-Lot"
    String bt744 = "";
    addIfNotEmpty(esigObj, "BT-744-Lot", bt744);

    esigArray.add(esigObj);
    putIfNotEmpty(tenderingObj,"GR-Lot-ElectronicSignatureRequired", esigArray);
  }

  // -----------------------------------------------------
  // GR-Lot-Variants
  // -----------------------------------------------------
  private void populateGRLotVariants(JSONObject tenderingObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_VARIANTS, Long.valueOf(0));

    //JSONArray variantsArray = new JSONArray();
    for(Object result : results){

      String ammvar = SqlManager.getValueFromVectorParam(result, 0).stringValue();

      JSONObject variantsObj = new JSONObject();
      String bt63 = "";
      if(ammvar != null) {
        if("1".equals(ammvar)) {
          bt63 = "allowed";
        } else if("2".equals(ammvar)) {
          bt63 = "not-allowed";
        }
      }

      addIfNotEmpty(variantsObj, "BT-63-Lot", bt63);

      //variantsArray.add(variantsObj);
      putIfNotEmpty(tenderingObj,"GR-Lot-Variants", variantsObj);
    }
  }

  // -----------------------------------------------------
  // GR-ReservedParticipation
  // -----------------------------------------------------
  private void populateGRReservedParticipation(JSONObject tenderingObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_RESERVEDPARTICIPATION, Long.valueOf(0));

    //JSONArray reservedArray = new JSONArray();
    for(Object result : results){

      JSONObject reservedObj = new JSONObject();

      
      String affris = SqlManager.getValueFromVectorParam(result, 0).stringValue();

      String bt71 = getTab2d1ByTab2tip(affris, "A1z26");

      addIfNotEmpty(reservedObj, "BT-71-Lot", bt71);

      //reservedArray.add(reservedObj);
      putIfNotEmpty(tenderingObj,"GR-ReservedParticipation", reservedObj);
    }
  }

  // -----------------------------------------------------
  // GR-Lot-TenderingTerms-CVD
  // -----------------------------------------------------
  private void populateGRLotTenderingTermsCVD(JSONObject tenderingObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray cvdArray = new JSONArray();
    JSONObject cvdObj = new JSONObject();

    // Sotto-sezione: GR-Lot-TenderingTerms-CVD-type
    populateGRLotTenderingTermsCVDType(cvdObj, codiceEform, ngara);

    // "BT-717-Lot"
    String bt717 = "";
    addIfNotEmpty(cvdObj, "BT-717-Lot", bt717);

    cvdArray.add(cvdObj);
    putIfNotEmpty(tenderingObj,"GR-Lot-TenderingTerms-CVD", cvdArray);
  }

  private void populateGRLotTenderingTermsCVDType(JSONObject cvdObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray cvdTypeArray = new JSONArray();
    JSONObject cvdTypeObj = new JSONObject();

    // "BT-735-Lot"
    String bt735 = "";
    addIfNotEmpty(cvdTypeObj, "BT-735-Lot", bt735);

    cvdTypeArray.add(cvdTypeObj);
    putIfNotEmpty(cvdObj,"GR-Lot-TenderingTerms-CVD-type", cvdTypeArray);
  }

  // -----------------------------------------------------
  // GR-Recurrence
  // -----------------------------------------------------
  private void populateGRRecurrence(JSONObject tenderingObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray recArray = new JSONArray();
    JSONObject recObj = new JSONObject();

    // "BT-769-Lot"
    String bt769 = "";
    addIfNotEmpty(recObj, "BT-769-Lot", bt769);
    // "BT-94-Lot"
    String bt94 = "";
    addIfNotEmpty(recObj, "BT-94-Lot", bt94);
    // "BT-95-Lot"
    String bt95 = "";
    addIfNotEmpty(recObj, "BT-95-Lot", bt95);

    recArray.add(recObj);
    putIfNotEmpty(tenderingObj,"GR-Recurrence", recArray);
  }

  // -----------------------------------------------------
  // GR-Lot-EInvoicing
  // -----------------------------------------------------
  private void populateGRLotEInvoicing(JSONObject tenderingObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray eInvArray = new JSONArray();
    JSONObject eInvObj = new JSONObject();

    // "BT-743-Lot"
    String bt743 = "required";
    addIfNotEmpty(eInvObj, "BT-743-Lot", bt743);

    eInvArray.add(eInvObj);
    putIfNotEmpty(tenderingObj,"GR-Lot-EInvoicing", eInvArray);
  }

  // -----------------------------------------------------
  // GR-Lot-FinancialTerms
  // -----------------------------------------------------
  private void populateGRLotFinancialTerms(JSONObject tenderingObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray finTermsArray = new JSONArray();
    JSONObject finTermsObj = new JSONObject();

    // "BT-77-Lot"
    String bt77 = "Vedi documentazione di gara";
    addIfNotEmpty(finTermsObj, "BT-77-Lot", bt77);

    finTermsArray.add(finTermsObj);
    putIfNotEmpty(tenderingObj,"GR-Lot-FinancialTerms", finTermsArray);
  }

  // -----------------------------------------------------
  // GR-Lot-SelectionCriteria
  // -----------------------------------------------------
  private void populateGRLotSelectionCriteria(JSONObject tenderingObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDatiLotti(ngara, PathJsonEform.GR_LOT_SELECTIONCRITERIA, Long.valueOf(0));

    JSONArray selCriteriaArray = new JSONArray();
    for(Object result : results){

      
      String bt750 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt809 = "";
      String bt40 = "";

      JSONObject selCriteriaObj = new JSONObject();
      
      addIfNotEmpty(selCriteriaObj, "BT-750-Lot", bt750);
      addIfNotEmpty(selCriteriaObj, "BT-809-Lot", bt809);
      addIfNotEmpty(selCriteriaObj, "BT-40-Lot", bt40);
      
      // Sotto-sezione: GR-Lot-SelectionCriteria-SecondStageInvite
      populateGRLotSelectionCriteriaSecondStageInvite(selCriteriaObj, codiceEform, ngara);
      
      selCriteriaArray.add(selCriteriaObj);
      putIfNotEmpty(tenderingObj,"GR-Lot-SelectionCriteria", selCriteriaArray);
    }
  }

  private void populateGRLotSelectionCriteriaSecondStageInvite(JSONObject selCriteriaObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray secondStageArray = new JSONArray();
    JSONObject secondStageObj = new JSONObject();

    // "GR-Lot-SelectionCriteria-SecondStageInvite-Weight"
    populateGRLotSecondStageInviteWeight(secondStageObj, codiceEform, ngara);

    // "GR-Lot-SelectionCriteria-SecondStageInvite-Threshold"
    populateGRLotSecondStageInviteThreshold(secondStageObj, codiceEform, ngara);

    secondStageArray.add(secondStageObj);
    putIfNotEmpty(selCriteriaObj,"GR-Lot-SelectionCriteria-SecondStageInvite", secondStageArray);
  }

  private void populateGRLotSecondStageInviteWeight(JSONObject secondStageObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray weightArray = new JSONArray();
    JSONObject weightObj = new JSONObject();

    // "BT-752-Lot-WeightNumber"
    String bt752Weight = "";
    addIfNotEmpty(weightObj, "BT-752-Lot-WeightNumber", bt752Weight);
    // "BT-7531-Lot"
    String bt7531 = "";
    addIfNotEmpty(weightObj, "BT-7531-Lot", bt7531);

    weightArray.add(weightObj);
    putIfNotEmpty(secondStageObj,"GR-Lot-SelectionCriteria-SecondStageInvite-Weight", weightArray);
  }

  private void populateGRLotSecondStageInviteThreshold(JSONObject secondStageObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray thresholdArray = new JSONArray();
    JSONObject thrObj = new JSONObject();

    // "BT-752-Lot-ThresholdNumber"
    String bt752Threshold = "";
    addIfNotEmpty(thrObj, "BT-752-Lot-ThresholdNumber", bt752Threshold);
    // "BT-7532-Lot"
    String bt7532 = "";
    addIfNotEmpty(thrObj, "BT-7532-Lot", bt7532);

    thresholdArray.add(thrObj);
    putIfNotEmpty(secondStageObj,"GR-Lot-SelectionCriteria-SecondStageInvite-Threshold", thresholdArray);
  }

  // -----------------------------------------------------
  // GR-Lot-SelectionCriteria-Source
  // -----------------------------------------------------
  private void populateGRLotSelectionCriteriaSource(JSONObject tenderingObj, String codiceEform, String ngara, String uuidEform) throws Exception {

    JSONObject selCriteriaSourceObj = new JSONObject();

    addIfNotEmpty(selCriteriaSourceObj, "BT-821-Lot", getMotivoEsclusione(uuidEform, null, ngara));

    putIfNotEmpty(tenderingObj,"GR-Lot-SelectionCriteria-Source", selCriteriaSourceObj);
  }

  // -----------------------------------------------------
  // GR-Lot-SecurityClearance2
  // -----------------------------------------------------
  private void populateGRLotSecurityClearance2(JSONObject tenderingObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray clearanceArray = new JSONArray();
    JSONObject clearanceObj = new JSONObject();

    // "BT-732-Lot"
    String bt732 = "";
    addIfNotEmpty(clearanceObj, "BT-732-Lot", bt732);
    // "BT-578-Lot"
    String bt578 = "";
    addIfNotEmpty(clearanceObj, "BT-578-Lot", bt578);

    clearanceArray.add(clearanceObj);
    putIfNotEmpty(tenderingObj,"GR-Lot-SecurityClearance2", clearanceArray);
  }

  // -----------------------------------------------------
  // GR-Lot-PostAwardProcess
  // -----------------------------------------------------
  private void populateGRLotPostAwardProcess(JSONObject tenderingObj, String codiceEform, String ngara) throws Exception {

    
    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_POSTAWARDPROCESS, Long.valueOf(0));

    //JSONArray postAwardArray = new JSONArray();
    for(Object result : results){

      String ordiele = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      Boolean bt93 = Boolean.TRUE;

      Boolean bt92 = Boolean.FALSE;
      if("1".equals(ordiele)) {
        bt92 = Boolean.TRUE;
      }

      JSONObject postAwardObj = new JSONObject();
  
      addIfNotEmpty(postAwardObj, "BT-93-Lot", bt93);
      addIfNotEmpty(postAwardObj, "BT-92-Lot", bt92);
  
      //postAwardArray.add(postAwardObj);
      putIfNotEmpty(tenderingObj,"GR-Lot-PostAwardProcess", postAwardObj);
    }
  }

  // -----------------------------------------------------
  // GR-Lot-Candidates
  // -----------------------------------------------------
  private void populateGRLotCandidates(JSONObject tenderingObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_CANDIDATES, Long.valueOf(0));

    //JSONArray candidatesArray = new JSONArray();
    for(Object result : results){

      JSONObject candObj = new JSONObject();

      Long bt50 = SqlManager.getValueFromVectorParam(result, 0).longValue(); //minope
      Long bt51 = SqlManager.getValueFromVectorParam(result, 1).longValue(); //maxope
      Boolean bt52 = null;
      Boolean bt661 = null;
      
      if(bt51 != null) {
        bt661 = Boolean.TRUE;
        bt52 = Boolean.TRUE;
      }
      if(bt50 != null) {
        bt52 = Boolean.TRUE;
      }

      addIfNotEmpty(candObj, "BT-50-Lot", bt50);
      addIfNotEmpty(candObj, "BT-51-Lot", bt51);
      addIfNotEmpty(candObj, "BT-52-Lot", bt52);
      addIfNotEmpty(candObj, "BT-661-Lot", bt661);
  
      //candidatesArray.add(candObj);
      putIfNotEmpty(tenderingObj,"GR-Lot-Candidates", candObj);
    }
  }

  // -----------------------------------------------------
  // GR-Lot-TendererQualification
  // -----------------------------------------------------
  private void populateGRLotTendererQualification(JSONObject tenderingObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray tenderQualArray = new JSONArray();
    JSONObject qualObj = new JSONObject();

    // "GR-LateTendererInformation" (ESISTE SIA QUI SIA SOTTO?)
    //    - in JSON c'e' una sezione "GR-LateTendererInformation" DOPPIA
    //    - la popoleremo con un metodo a parte (facciamo anche la sezione "BT-771-Lot", "BT-772-Lot")

    // "BT-761-Lot"
    Boolean bt761 = Boolean.FALSE;
    addIfNotEmpty(qualObj, "BT-761-Lot", bt761);
    // "BT-76-Lot"
    String bt76 = "";
    addIfNotEmpty(qualObj, "BT-76-Lot", bt76);
    // "BT-772-Lot"
    String bt772 = "";
    addIfNotEmpty(qualObj, "BT-772-Lot", bt772);
    // "BT-771-Lot"
    String bt771 = "";
    addIfNotEmpty(qualObj, "BT-771-Lot", bt771);

    tenderQualArray.add(qualObj);
    putIfNotEmpty(tenderingObj,"GR-Lot-TendererQualification", tenderQualArray);
  }

  // -----------------------------------------------------
  // GR-Lot-TenderReceipt
  // -----------------------------------------------------
  private void populateGRLotTenderReceipt(JSONObject tenderingObj, String codiceEform, String ngara, EntityManager entityManager) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_TENDERRECEIPT, Long.valueOf(0));

    //JSONArray tenderReceiptArray = new JSONArray();
    for(Object result : results){

      Long pcooff = SqlManager.getValueFromVectorParam(result, 0).longValue();
      Long pcopre = SqlManager.getValueFromVectorParam(result, 1).longValue();
      String cenint = SqlManager.getValueFromVectorParam(result, 2).stringValue();
      Long iterga = SqlManager.getValueFromVectorParam(result, 3).longValue();
      
      
      String entita = "uffint";
      String chiave = cenint;
      
      if(Long.valueOf(2).equals(iterga) || Long.valueOf(4).equals(iterga)) {
        if(pcopre != null) {
          entita = "punticon";
          chiave = String.valueOf(pcopre)+";"+cenint;
        }
      } else {
        if(pcooff != null) {
          entita = "punticon";
          chiave = String.valueOf(pcooff)+";"+cenint;
        }
      }
      
      String opt301TR = entityManager.getOrCreateValue(PREFISSO_ORG, entita, chiave, Boolean.FALSE);
      
      JSONObject receiptObj = new JSONObject();
      
      addIfNotEmpty(receiptObj, "OPT-301-Lot-TenderReceipt", opt301TR);
      
      //tenderReceiptArray.add(receiptObj);
      putIfNotEmpty(tenderingObj,"GR-Lot-TenderReceipt", receiptObj);
    }
  }

  // -----------------------------------------------------
  // GR-Lot-Requirements2SecurityClearance1
  // -----------------------------------------------------
  private void populateGRLotRequirements2SecurityClearance1(JSONObject tenderingObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray reqSecClearArray = new JSONArray();
    JSONObject reqSecClearObj = new JSONObject();

    // "BT-79-Lot"
    String bt79 = "";
    addIfNotEmpty(reqSecClearObj, "BT-79-Lot", bt79);
    // "BT-78-Lot"
    String bt78 = "";
    addIfNotEmpty(reqSecClearObj, "BT-78-Lot", bt78);

    reqSecClearArray.add(reqSecClearObj);
    putIfNotEmpty(tenderingObj,"GR-Lot-Requirements2SecurityClearance1", reqSecClearArray);
  }

  // -----------------------------------------------------
  // GR-Lot-AwardingConsequences
  // -----------------------------------------------------
  private void populateGRLotAwardingConsequences(JSONObject tenderingObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray awardingConseqArray = new JSONArray();
    JSONObject awardingConseqObj = new JSONObject();

    // "BT-120-Lot"
    String bt120 = "";
    addIfNotEmpty(awardingConseqObj, "BT-120-Lot", bt120);
    // Sotto-sezione: GR-Jury-Member
    populateGRJuryMember(awardingConseqObj, codiceEform, ngara);

    // "BT-42-Lot"
    String bt42 = "";
    addIfNotEmpty(awardingConseqObj, "BT-42-Lot", bt42);
    // "BT-41-Lot"
    String bt41 = "";
    addIfNotEmpty(awardingConseqObj, "BT-41-Lot", bt41);

    awardingConseqArray.add(awardingConseqObj);
    putIfNotEmpty(tenderingObj,"GR-Lot-AwardingConsequences", awardingConseqArray);
  }

  private void populateGRJuryMember(JSONObject awardingConseqObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray juryMemberArray = new JSONArray();
    JSONObject juryMemberObj = new JSONObject();

    // "BT-46-Lot"
    String bt46 = "";
    addIfNotEmpty(juryMemberObj, "BT-46-Lot", bt46);

    juryMemberArray.add(juryMemberObj);
    putIfNotEmpty(awardingConseqObj,"GR-Jury-Member", juryMemberArray);
  }

  // -----------------------------------------------------
  // GR-Lot-SubcontractRequest
  // -----------------------------------------------------
  private void populateGRLotSubcontractRequest(JSONObject tenderingObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray subReqArray = new JSONArray();
    JSONObject subReqObj = new JSONObject();

    // "BT-651-Lot"
    String bt651 = "";
    addIfNotEmpty(subReqObj, "BT-651-Lot", bt651);

    subReqArray.add(subReqObj);
    putIfNotEmpty(tenderingObj,"GR-Lot-SubcontractRequest", subReqArray);
  }

  // -----------------------------------------------------
  // GR-Lot-SubcontractTerms
  // -----------------------------------------------------
  private void populateGRLotSubcontractTerms(JSONObject tenderingObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray subTermsArray = new JSONArray();
    JSONObject subTermsObj = new JSONObject();

    // "BT-64-Lot"
    String bt64 = "";
    addIfNotEmpty(subTermsObj, "BT-64-Lot", bt64);
    // "BT-65-Lot"
    String bt65 = "";
    addIfNotEmpty(subTermsObj, "BT-65-Lot", bt65);
    // "BT-729-Lot"
    String bt729 = "";
    addIfNotEmpty(subTermsObj, "BT-729-Lot", bt729);

    subTermsArray.add(subTermsObj);
    putIfNotEmpty(tenderingObj,"GR-Lot-SubcontractTerms", subTermsArray);
  }

  // -----------------------------------------------------
  // GR-LateTendererInformation
  // -----------------------------------------------------
  private void populateGRLateTendererInformation(JSONObject tenderingObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // Doppio array "GR-LateTendererInformation"? Nel JSON appare
    // anche sotto GR-Lot-TendererQualification. Gestiamo anche qui:

    JSONArray lateTenderArray = new JSONArray();
    JSONObject lateTenderObj = new JSONObject();

    // "BT-772-Lot"
    String bt772 = "";
    addIfNotEmpty(lateTenderObj, "BT-772-Lot", bt772);
    // "BT-771-Lot"
    String bt771 = "";
    addIfNotEmpty(lateTenderObj, "BT-771-Lot", bt771);

    lateTenderArray.add(lateTenderObj);
    putIfNotEmpty(tenderingObj,"GR-LateTendererInformation", lateTenderArray);
  }

  // -----------------------------------------------------
  // GR-Lot-EconomicOperatorShortlist
  // -----------------------------------------------------
  private void populateGRLotEconomicOperatorShortlist(JSONObject tenderingObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray econOpShortArray = new JSONArray();
    JSONObject econOpShortObj = new JSONObject();

    // "BT-47-Lot"
    String bt47 = "";
    addIfNotEmpty(econOpShortObj, "BT-47-Lot", bt47);

    econOpShortArray.add(econOpShortObj);
    putIfNotEmpty(tenderingObj,"GR-Lot-EconomicOperatorShortlist", econOpShortArray);
  }

  // -----------------------------------------------------
  // GR-Lot-PrizeInformation
  // -----------------------------------------------------
  private void populateGRLotPrizeInformation(JSONObject tenderingObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray prizeInfoArray = new JSONArray();
    JSONObject prizeInfoObj = new JSONObject();

    // "BT-44-Lot"
    String bt44 = "";
    addIfNotEmpty(prizeInfoObj, "BT-44-Lot", bt44);
    // "BT-644-Lot"
    String bt644 = "";
    addIfNotEmpty(prizeInfoObj, "BT-644-Lot", bt644);
    // "BT-45-Lot"
    String bt45 = "";
    addIfNotEmpty(prizeInfoObj, "BT-45-Lot", bt45);

    prizeInfoArray.add(prizeInfoObj);
    putIfNotEmpty(tenderingObj,"GR-Lot-PrizeInformation", prizeInfoArray);
  }

  // =========================================================================
  //                     GR-Lot-SubmissionInfo
  // =========================================================================
  private void populateGRLotSubmissionInfo(JSONObject grLotObj, String uuidEform, String codiceEform, String ngara, EntityManager entityManager) throws Exception {

    JSONObject subInfoObj = new JSONObject();

    // GR-Lot-FinancialGuarantee
    populateGRLotFinancialGuarantee(subInfoObj, codiceEform, ngara);

    // GR-Lot-SubmissionTool
    populateGRLotSubmissionTool(subInfoObj, codiceEform, ngara);

    // GR-Lot-AddInfo
    populateGRLotAddInfo(subInfoObj, codiceEform, ngara, entityManager);

    // GR-Lot-SubmissionMethod
    populateGRLotSubmissionMethod(subInfoObj, codiceEform, ngara);

    // GR-Lot-SubmissionLanguage
    populateGRLotSubmissionLanguage(subInfoObj, codiceEform, ngara);

    // GR-Lot-TenderValidity
    populateGRLotTenderValidity(subInfoObj, codiceEform, ngara);

    // GR-Lot-DocProvider
    populateGRLotDocProvider(subInfoObj, codiceEform, ngara);

    // GR-Lot-Deadlines1
    populateGRLotDeadlines1(subInfoObj, uuidEform, codiceEform, ngara);

    // GR-Lot-Procedure
    populateGRLotProcedure(subInfoObj, codiceEform, ngara);

    // GR-Lot-Deadlines2
    populateGRLotDeadlines2(subInfoObj, codiceEform, ngara);

    // GR-Lot-ProcurementDocuments
    populateGRLotProcurementDocuments(subInfoObj, codiceEform, ngara);

    // GR-Lot-PublicOpeningEvent
    populateGRLotPublicOpeningEvent(subInfoObj, uuidEform, codiceEform, ngara);

    putIfNotEmpty(grLotObj,"GR-Lot-SubmissionInfo", subInfoObj);
  }

  private void populateGRLotFinancialGuarantee(JSONObject subInfoObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray finGuaranteeArray = new JSONArray();
    JSONObject finGuaranteeObj = new JSONObject();

    // "BT-751-Lot"
    Boolean bt751 = Boolean.FALSE;
    addIfNotEmpty(finGuaranteeObj, "BT-751-Lot", bt751);
    // "BT-75-Lot"
    String bt75 = "";
    addIfNotEmpty(finGuaranteeObj, "BT-75-Lot", bt75);

    finGuaranteeArray.add(finGuaranteeObj);
    putIfNotEmpty(subInfoObj,"GR-Lot-FinancialGuarantee", finGuaranteeArray);
  }

  private void populateGRLotSubmissionTool(JSONObject subInfoObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray submissionToolArray = new JSONArray();
    JSONObject toolObj = new JSONObject();

    // "BT-124-Lot"
    String bt124 = "";
    addIfNotEmpty(toolObj, "BT-124-Lot", bt124);
    // "BT-632-Lot"
    String bt632 = "";
    addIfNotEmpty(toolObj, "BT-632-Lot", bt632);

    submissionToolArray.add(toolObj);
    putIfNotEmpty(subInfoObj,"GR-Lot-SubmissionTool", submissionToolArray);
  }

  private void populateGRLotAddInfo(JSONObject subInfoObj, String codiceEform, String ngara, EntityManager entityManager) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_ADDINFO, Long.valueOf(0));

    //JSONArray addInfoArray = new JSONArray();
    for(Object result : results){

      Long pcodoc = SqlManager.getValueFromVectorParam(result, 0).longValue();
      String cenint = SqlManager.getValueFromVectorParam(result, 1).stringValue();
      
      String entita = "uffint";
      String chiave = cenint;
      if(pcodoc != null) {
        entita = "punticon";
        chiave = String.valueOf(pcodoc)+";"+cenint;
      }
      
      String opt301AddInfo = entityManager.getOrCreateValue(PREFISSO_ORG, entita, chiave, Boolean.FALSE);

      
      JSONObject addInfoObj = new JSONObject();
  
      // "OPT-301-Lot-AddInfo"
      addIfNotEmpty(addInfoObj, "OPT-301-Lot-AddInfo", opt301AddInfo);

      //addInfoArray.add(addInfoObj);
      putIfNotEmpty(subInfoObj,"GR-Lot-AddInfo", addInfoObj);
    }
  }

  private void populateGRLotSubmissionMethod(JSONObject subInfoObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray subMethodArray = new JSONArray();
    JSONObject subMethodObj = new JSONObject();

    // "BT-18-Lot"
    addIfNotEmpty(subMethodObj, "BT-18-Lot", getUrlProcedurePortaleAppaltiByNgara(ngara));
    // "BT-19-Lot"
    String bt19 = "";
    addIfNotEmpty(subMethodObj, "BT-19-Lot", bt19);
    // "BT-745-Lot"
    String bt745 = "";
    addIfNotEmpty(subMethodObj, "BT-745-Lot", bt745);
    // "BT-17-Lot"
    String bt17 = "allowed";
    addIfNotEmpty(subMethodObj, "BT-17-Lot", bt17);

    subMethodArray.add(subMethodObj);
    putIfNotEmpty(subInfoObj,"GR-Lot-SubmissionMethod", subMethodArray);
  }

  private void populateGRLotSubmissionLanguage(JSONObject subInfoObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    //JSONArray subLangArray = new JSONArray();
    JSONObject subLangObj = new JSONObject();

    // "BT-97-Lot"
    String bt97 = "ITA";
    addIfNotEmpty(subLangObj, "BT-97-Lot", bt97);

    //subLangArray.add(subLangObj);
    putIfNotEmpty(subInfoObj,"GR-Lot-SubmissionLanguage", subLangObj);
  }

  private void populateGRLotTenderValidity(JSONObject subInfoObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_TENDERVALIDITY, Long.valueOf(0));

    //JSONArray validityArray = new JSONArray();
    for(Object result : results){

      String bt98 = SqlManager.getValueFromVectorParam(result, 0).stringValue();

      JSONObject validityObj = new JSONObject();
  
      addIfNotEmpty(validityObj, "BT-98-Lot", bt98);
      
      if(bt98 != null && !bt98.isEmpty()) {
        addIfNotEmpty(validityObj, "BT-98-Lot-Unit", "DAY");
      }
      
      //validityArray.add(validityObj);
      putIfNotEmpty(subInfoObj,"GR-Lot-TenderValidity", validityObj);
    }
  }

  private void populateGRLotDocProvider(JSONObject subInfoObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray docProviderArray = new JSONArray();
    JSONObject docProviderObj = new JSONObject();

    // "OPT-301-Lot-DocProvider"
    String opt301DocProv = "";
    addIfNotEmpty(docProviderObj, "OPT-301-Lot-DocProvider", opt301DocProv);

    docProviderArray.add(docProviderObj);
    putIfNotEmpty(subInfoObj,"GR-Lot-DocProvider", docProviderArray);
  }

  // GR-Lot-Deadlines1
  private void populateGRLotDeadlines1(JSONObject subInfoObj, String uuidEform, String codiceEform, String ngara) throws Exception {
    Boolean isRettifica = isSchedaRettificaByUuidEformCorrente(uuidEform);
    List<?> results = new ArrayList<>();
    if(Boolean.TRUE.equals(isRettifica)) {
      results = getDatiLotti(uuidEform, PathJsonEform.GR_LOT_DEADLINES1_RETTIFICA, Long.valueOf(0));
    } else {
      results = getDatiLotti(ngara, PathJsonEform.GR_LOT_DEADLINES1, Long.valueOf(0));
    }

    //JSONArray deadlines1Array = new JSONArray();
    for(Object result : results){

      Date bt131d = (Date) SqlManager.getValueFromVectorParam(result, 0).getValue();
      String bt131t = SqlManager.getValueFromVectorParam(result, 1).stringValue();
      Date bt1311d = (Date) SqlManager.getValueFromVectorParam(result, 2).getValue();
      String bt1311t = SqlManager.getValueFromVectorParam(result, 3).stringValue();
      String bt631 = "";
      String bt130 = "";
      String bt13t = "";
      String bt13d = "";

      if((bt131d == null || bt1311d == null) && Boolean.TRUE.equals(isRettifica)) {
        List<?> results2 = new ArrayList<>();
        results2 = getDatiLotti(ngara, PathJsonEform.GR_LOT_DEADLINES1, Long.valueOf(0));
        for(Object result2 : results2){
          if(bt131d == null) {
            bt131d = (Date) SqlManager.getValueFromVectorParam(result2, 0).getValue();
            bt131t = SqlManager.getValueFromVectorParam(result2, 1).stringValue();
          }
          if(bt1311d == null) {
            bt1311d = (Date) SqlManager.getValueFromVectorParam(result2, 2).getValue();
            bt1311t = SqlManager.getValueFromVectorParam(result2, 3).stringValue();
          }
        }
      }
      
      JSONObject dlObj = new JSONObject();

      Long iterga = getItergaByCodgar(getCodgarByUuid(uuidEform));
      
      if (Long.valueOf(2).equals(iterga) || Long.valueOf(4).equals(iterga)) {
        addIfNotEmpty(dlObj, "BT-1311(d)-Lot", bt1311d);
        addIfNotEmpty(dlObj, "BT-1311(t)-Lot", bt1311t);        
      } else {
        addIfNotEmpty(dlObj, "BT-131(d)-Lot", bt131d);
        addIfNotEmpty(dlObj, "BT-131(t)-Lot", bt131t);
      }
      
      
      addIfNotEmpty(dlObj, "BT-631-Lot", bt631);
      addIfNotEmpty(dlObj, "BT-130-Lot", bt130);
      addIfNotEmpty(dlObj, "BT-13(t)-Lot", bt13t);
      addIfNotEmpty(dlObj, "BT-13(d)-Lot", bt13d);

      //deadlines1Array.add(dlObj);
      putIfNotEmpty(subInfoObj,"GR-Lot-Deadlines1", dlObj);
    }
  }

  private void populateGRLotProcedure(JSONObject subInfoObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray procArray = new JSONArray();
    JSONObject procObj = new JSONObject();

    // "BT-634-Lot"
    String bt634 = "";
    addIfNotEmpty(procObj, "BT-634-Lot", bt634);

    procArray.add(procObj);
    putIfNotEmpty(subInfoObj,"GR-Lot-Procedure", procArray);
  }

  private void populateGRLotDeadlines2(JSONObject subInfoObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_DEADLINES2, Long.valueOf(0));

    for(Object result : results){
      
      Date bt630d = (Date) SqlManager.getValueFromVectorParam(result, 0).getValue();
      String bt630t = SqlManager.getValueFromVectorParam(result, 1).stringValue();
  
      JSONObject dl2Obj = new JSONObject();
  
      addIfNotEmpty(dl2Obj, "BT-630(d)-Lot", bt630d);
      addIfNotEmpty(dl2Obj, "BT-630(t)-Lot", bt630t);

      putIfNotEmpty(subInfoObj,"GR-Lot-Deadlines2", dl2Obj);
    }
  }

  private void populateGRLotProcurementDocuments(JSONObject subInfoObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray procDocArray = new JSONArray();
    JSONObject procDocObj = new JSONObject();

    // "OPT-140-Lot"
    String opt140 = ngara;
    addIfNotEmpty(procDocObj, "OPT-140-Lot", opt140);

    // GR-Lot-DocsAllNonOfficialLanguages
    populateGRLotDocsAllNonOfficialLanguages(procDocObj, codiceEform, ngara);

    // "BT-707-Lot"
    String bt707 = "";
    addIfNotEmpty(procDocObj, "BT-707-Lot", bt707);

    // GR-Lot-DocsAllOfficialLanguages
    populateGRLotDocsAllOfficialLanguages(procDocObj, codiceEform, ngara);

    // "BT-615-Lot"
    String url = getUrlSQL(null, ngara);
    String bt615 = "";
    addIfNotEmpty(procDocObj, "BT-615-Lot", bt615);
    addIfNotEmpty(procDocObj, "BT-14-Lot", "non-restricted-document");
    addIfNotEmpty(procDocObj, "BT-15-Lot", url);

    procDocArray.add(procDocObj);
    putIfNotEmpty(subInfoObj,"GR-Lot-ProcurementDocuments", procDocArray);
  }

  private void populateGRLotDocsAllNonOfficialLanguages(JSONObject procDocObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray allNonOffArray = new JSONArray();
    JSONObject nonOffObj = new JSONObject();

    // "GR-Lot-DocsNonOfficialLanguage"
    JSONArray docsNonOffLangArray = new JSONArray();
    JSONObject docsNonOffLangObj = new JSONObject();

    // "BT-737-Lot"
    String bt737 = "";
    addIfNotEmpty(docsNonOffLangObj, "BT-737-Lot", bt737);
    docsNonOffLangArray.add(docsNonOffLangObj);

    putIfNotEmpty(nonOffObj,"GR-Lot-DocsNonOfficialLanguage", docsNonOffLangArray);
    allNonOffArray.add(nonOffObj);

    putIfNotEmpty(procDocObj,"GR-Lot-DocsAllNonOfficialLanguages", allNonOffArray);
  }

  private void populateGRLotDocsAllOfficialLanguages(JSONObject procDocObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray allOffArray = new JSONArray();
    JSONObject offObj = new JSONObject();

    // "GR-Lot-DocsOfficialLanguage"
    JSONArray docsOffLangArray = new JSONArray();
    JSONObject docsOffLangObj = new JSONObject();

    // "BT-708-Lot"
    String bt708 = "";
    addIfNotEmpty(docsOffLangObj, "BT-708-Lot", bt708);
    docsOffLangArray.add(docsOffLangObj);

    putIfNotEmpty(offObj,"GR-Lot-DocsOfficialLanguage", docsOffLangArray);
    allOffArray.add(offObj);

    putIfNotEmpty(procDocObj,"GR-Lot-DocsAllOfficialLanguages", allOffArray);
  }

  private void populateGRLotPublicOpeningEvent(JSONObject subInfoObj, String uuidEform, String codiceEform, String ngara) throws Exception {

    Boolean isRettifica = isSchedaRettificaByUuidEformCorrente(uuidEform);
    List<?> results = new ArrayList<>();
    if(Boolean.TRUE.equals(isRettifica)) {
      results = getDatiLotti(uuidEform, PathJsonEform.GR_LOT_PUBLICOPENINGEVENT_RETTIFICA, Long.valueOf(0));
    } else {
      results = getDatiLotti(ngara, PathJsonEform.GR_LOT_PUBLICOPENINGEVENT, Long.valueOf(0));
    }

    //JSONArray publicOpenArray = new JSONArray();
    for(Object result : results){
      
      Date bt132d = (Date) SqlManager.getValueFromVectorParam(result, 0).getValue();
      String bt132t = SqlManager.getValueFromVectorParam(result, 1).stringValue();
      String bt134 = "";
      String bt133 = "";
      
      if(bt132d == null && Boolean.TRUE.equals(isRettifica)) {
        List<?> results2 = new ArrayList<>();
        results2 = getDatiLotti(ngara, PathJsonEform.GR_LOT_PUBLICOPENINGEVENT, Long.valueOf(0));
        for(Object result2 : results2){
          bt132d = (Date) SqlManager.getValueFromVectorParam(result2, 0).getValue();
          bt132t = SqlManager.getValueFromVectorParam(result2, 1).stringValue();
        }
      }

      JSONObject publicOpenObj = new JSONObject();

      addIfNotEmpty(publicOpenObj, "BT-134-Lot", bt134);
      addIfNotEmpty(publicOpenObj, "BT-132(d)-Lot", bt132d);
      addIfNotEmpty(publicOpenObj, "BT-132(t)-Lot", bt132t);
      addIfNotEmpty(publicOpenObj, "BT-133-Lot", bt133);

      //publicOpenArray.add(publicOpenObj);
      putIfNotEmpty(subInfoObj,"GR-Lot-PublicOpeningEvent", publicOpenObj);
    }
  }

  // =========================================================================
  //                        GR-Lot-Review
  // =========================================================================
  private void populateGRLotReview(JSONObject grLotObj, String codiceEform, String ngara, EntityManager entityManager) throws Exception {

    JSONObject reviewObj = new JSONObject();

    // Sotto-sezione: GR-Lot-Deadline
    populateGRLotReviewDeadline(reviewObj, codiceEform, ngara);

    // GR-Lot-ReviewOrg
    populateGRLotReviewOrg(reviewObj, codiceEform, ngara, entityManager);

    // GR-Lot-ReviewInfo
    populateGRLotReviewInfo(reviewObj, codiceEform, ngara);

    // GR-Lot-Mediator
    populateGRLotMediator(reviewObj, codiceEform, ngara);

    putIfNotEmpty(grLotObj,"GR-Lot-Review", reviewObj);
  }

  private void populateGRLotReviewDeadline(JSONObject reviewObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_DEADLINE, Long.valueOf(0));

    //JSONArray deadlineArray = new JSONArray();
    for(Object result : results){
      
      String bt99 = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      
      JSONObject dlObj = new JSONObject();
  
      addIfNotEmpty(dlObj, "BT-99-Lot", bt99);
  
      //deadlineArray.add(dlObj);
      putIfNotEmpty(reviewObj,"GR-Lot-Deadline", dlObj);
    }
  }

  private void populateGRLotReviewOrg(JSONObject reviewObj, String codiceEform, String ngara, EntityManager entityManager) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_REVIEWORG, Long.valueOf(0));

    for(Object result : results){

      Long numpun = SqlManager.getValueFromVectorParam(result, 0).longValue();
      String codein = SqlManager.getValueFromVectorParam(result, 1).stringValue();

      String entita = "punticon";
      String chiave = String.valueOf(numpun)+";"+codein;
      String opt301ReviewOrg = entityManager.getOrCreateValue(PREFISSO_ORG, entita, chiave, Boolean.FALSE);
      
      JSONObject revOrgObj = new JSONObject();

      addIfNotEmpty(revOrgObj, "OPT-301-Lot-ReviewOrg", opt301ReviewOrg);

      putIfNotEmpty(reviewObj,"GR-Lot-ReviewOrg", revOrgObj);
    }
  }

  private void populateGRLotReviewInfo(JSONObject reviewObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray reviewInfoArray = new JSONArray();
    JSONObject revInfoObj = new JSONObject();

    // "OPT-301-Lot-ReviewInfo"
    String opt301ReviewInfo = "";
    addIfNotEmpty(revInfoObj, "OPT-301-Lot-ReviewInfo", opt301ReviewInfo);

    reviewInfoArray.add(revInfoObj);
    putIfNotEmpty(reviewObj,"GR-Lot-ReviewInfo", reviewInfoArray);
  }

  private void populateGRLotMediator(JSONObject reviewObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray mediatorArray = new JSONArray();
    JSONObject medObj = new JSONObject();

    // "OPT-301-Lot-Mediator"
    String opt301Mediator = "";
    addIfNotEmpty(medObj, "OPT-301-Lot-Mediator", opt301Mediator);

    mediatorArray.add(medObj);
    putIfNotEmpty(reviewObj,"GR-Lot-Mediator", mediatorArray);
  }

  // =========================================================================
  //                       GR-Lot-Purpose
  // =========================================================================
  private void populateGRLotPurpose(JSONObject grLotObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDatiLotti(ngara, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray purposeArray = new JSONArray();
    JSONObject purposeObj = new JSONObject();

    // 1) GR-Lot-GpaAgreement
    populateGRLotGpaAgreement(purposeObj, codiceEform, ngara);

    // 2) GR-Lot-ContractExtension
    populateGRLotContractExtension(purposeObj, codiceEform, ngara);

    // 3) GR-Lot-EmployLegis
    populateGRLotEmployLegis(purposeObj, codiceEform, ngara);

    // 4) GR-Lot-Scope
    populateGRLotScope(purposeObj, codiceEform, ngara);

    // 5) GR-Lot-PlannedDuration
    String famiglia = getFamilyByCodEform(codiceEform);
    Boolean isAD1 = codiceEform.equals("25") || codiceEform.equals("26") || codiceEform.equals("28"); 
    if(!"CAN".equals(famiglia) || Boolean.TRUE.equals(isAD1)) {
      populateGRLotPlannedDuration(purposeObj, codiceEform, ngara);
    }
    // 6) GR-Lot-ProcurementType
    populateGRLotProcurementType(purposeObj, codiceEform, ngara);

    // 7) GR-Lot-EnvironLegis
    populateGRLotEnvironLegis(purposeObj, codiceEform, ngara);

    // 8) GR-Lot-AdditionalInformation
    populateGRLotPurposeAdditionalInformation(purposeObj, codiceEform, ngara);

    // 9) GR-Lot-ContractingSystem
    populateGRLotContractingSystem(purposeObj, codiceEform, ngara);

    // 10) GR-Lot-FrameworkAgreement
    populateGRLotFrameworkAgreement(purposeObj, codiceEform, ngara);

    // 11) GR-Lot-AuctionTerms
    populateGRLotAuctionTerms(purposeObj, codiceEform, ngara);

    // 11) GR-Lot-EUFunds-Indicator
    populateGRLotEUFundsIndicator(purposeObj, codiceEform, ngara);

    // 11) GR-Lot-EUFunds
    populateGRLotEUFunds(purposeObj, codiceEform, ngara);

    // 12) GR-Lot-Description
    populateGRLotDescription(purposeObj, codiceEform, ngara);

    // 13) GR-Lot-FiscalLegis
    populateGRLotFiscalLegis(purposeObj, codiceEform, ngara);

    // 14) GR-Lot-PreviousPlanning
    populateGRLotPreviousPlanning(purposeObj, codiceEform, ngara);

    // 15) GR-Lot-PlaceOfPerformance
    populateGRLotPlaceOfPerformance(purposeObj, codiceEform, ngara);

    purposeArray.add(purposeObj);
    putIfNotEmpty(grLotObj,"GR-Lot-Purpose", purposeArray);
  }

  //GR-Lot-GpaAgreement
  private void populateGRLotGpaAgreement(JSONObject purposeObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_GPAAGREEMENT, Long.valueOf(0));

    for(Object result : results){

      //JSONArray gpaArray = new JSONArray();
      JSONObject gpaObj = new JSONObject();

      
      String accappub = SqlManager.getValueFromVectorParam(result, 0).stringValue();

      // "BT-115-Lot"
      Boolean bt115 = Boolean.FALSE;
      if(accappub != null && "1".equals(accappub)) {
        bt115 = Boolean.TRUE;
      }

      addIfNotEmpty(gpaObj, "BT-115-Lot", bt115);

      //gpaArray.add(gpaObj);
      putIfNotEmpty(purposeObj,"GR-Lot-GpaAgreement", gpaObj);
    }
  }

  private void populateGRLotContractExtension(JSONObject purposeObj, String codiceEform, String ngara) throws Exception {

    Long genere = (Long) sqlManager.getObject("select genere from v_gare_genere where codice = ? ", new Object[] { ngara } );

    if(Long.valueOf(10).equals(genere)) {
      JSONObject extensionObj = new JSONObject();
      addIfNotEmpty(extensionObj, "BT-58-Lot", Long.valueOf(0));
      putIfNotEmpty(purposeObj,"GR-Lot-ContractExtension", extensionObj);
    } else {

      List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_CONTRACTEXTENSION, Long.valueOf(0));

      //JSONArray extensionArray = new JSONArray();
      for(Object result : results){

        Long bt58 = SqlManager.getValueFromVectorParam(result, 0).longValue();
        String ammopz = SqlManager.getValueFromVectorParam(result, 1).stringValue();
        String desopz = SqlManager.getValueFromVectorParam(result, 2).stringValue();
        String ammrin = SqlManager.getValueFromVectorParam(result, 3).stringValue();
        String desrin = SqlManager.getValueFromVectorParam(result, 4).stringValue();

        String bt57 = "";
        if(ammrin != null && "1".equals(ammrin)) {
          bt57 = desrin;
        }
        String bt54 = "";
        if(ammopz != null && "1".equals(ammopz)) {
          bt54 = desopz;
        }

        JSONObject extensionObj = new JSONObject();


        if(Long.valueOf(10).equals(genere)) {
          bt58 = Long.valueOf(0);
        }

        addIfNotEmpty(extensionObj, "BT-58-Lot", bt58);
        addIfNotEmpty(extensionObj, "BT-57-Lot", bt57);
        addIfNotEmpty(extensionObj, "BT-54-Lot", bt54);

        //extensionArray.add(extensionObj);
        putIfNotEmpty(purposeObj,"GR-Lot-ContractExtension", extensionObj);
      }
    }
  }

  private void populateGRLotEmployLegis(JSONObject purposeObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray employArray = new JSONArray();
    JSONObject employObj = new JSONObject();

    // "OPT-301-Lot-EmployLegis"
    String opt301El = "";
    addIfNotEmpty(employObj, "OPT-301-Lot-EmployLegis", opt301El);
    // "OPT-130-Lot-EmployLegis"
    String opt130El = "";
    addIfNotEmpty(employObj, "OPT-130-Lot-EmployLegis", opt130El);
    // "OPT-113-Lot-EmployLegis"
    String opt113El = "";
    addIfNotEmpty(employObj, "OPT-113-Lot-EmployLegis", opt113El);

    employArray.add(employObj);
    putIfNotEmpty(purposeObj,"GR-Lot-EmployLegis", employArray);
  }

  private void populateGRLotScope(JSONObject purposeObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_SCOPE, null);

    //JSONArray scopeArray = new JSONArray();
    for(Object result : results){


      Double bt27 = SqlManager.getValueFromVectorParam(result, 0).doubleValue();
      String bt726 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
      String bt625 = "";
      String bt25 = "";


      JSONObject scopeObj = new JSONObject();

      addIfNotEmpty(scopeObj, "BT-25-Lot", bt25);

      // GR-Lot-Scope-AdditionalClassification
      populateGRLotScopeAdditionalClassification(scopeObj, codiceEform, ngara);

      addIfNotEmpty(scopeObj, "BT-726-Lot", bt726);

      // GR-Lot-Scope-MainClassification
      populateGRLotScopeMainClassification(scopeObj, codiceEform, ngara);

      addIfNotEmpty(scopeObj, "BT-625-Lot", bt625);
      addIfNotEmpty(scopeObj, "BT-27-Lot", bt27);

      //scopeArray.add(scopeObj);
      putIfNotEmpty(purposeObj,"GR-Lot-Scope", scopeObj);
    }
  }

  private void populateGRLotScopeAdditionalClassification(JSONObject scopeObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_SCOPE_ADDITIONALCLASSIFICATION, Long.valueOf(0));

    JSONArray addClassArray = new JSONArray();
    for(Object result : results){

      String bt263 = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt26a = "cpv";
      
      if(bt263 != null && bt263.contains("-")) {
        bt263 = bt263.split("-")[0];
      }

      JSONObject addClassObj = new JSONObject();

      addIfNotEmpty(addClassObj, "BT-26(a)-Lot", bt26a);
      addIfNotEmpty(addClassObj, "BT-263-Lot", bt263);

      addClassArray.add(addClassObj);
    }
    putIfNotEmpty(scopeObj,"GR-Lot-Scope-AdditionalClassification", addClassArray);
  }

  //GR-Lot-Scope-MainClassification
  private void populateGRLotScopeMainClassification(JSONObject scopeObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_SCOPE_MAINCLASSIFICATION, Long.valueOf(0));

    for(Object result : results){

      String bt262 = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt26m = "";

      if(bt262 != null && bt262.contains("-")) {
        bt262 = bt262.split("-")[0];
        bt26m = "cpv";
      }

      JSONObject mainClassObj = new JSONObject();
      
      addIfNotEmpty(mainClassObj, "BT-26(m)-Lot", bt26m);
      addIfNotEmpty(mainClassObj, "BT-262-Lot", bt262);

      putIfNotEmpty(scopeObj,"GR-Lot-Scope-MainClassification", mainClassObj);
    }
  }

  private void populateGRLotPlannedDuration(JSONObject purposeObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_PLANNEDDURATION, Long.valueOf(0));

    //JSONArray plannedDurArray = new JSONArray();
    for(Object result : results){

      Long teutil = SqlManager.getValueFromVectorParam(result, 0).longValue();
      Long temesi = SqlManager.getValueFromVectorParam(result, 1).longValue();
      String accqua = SqlManager.getValueFromVectorParam(result, 2).stringValue();
      Long aqdurata = SqlManager.getValueFromVectorParam(result, 3).longValue();
      Long aqtempo = SqlManager.getValueFromVectorParam(result, 4).longValue();
      Date dinlavgDate = (Date) SqlManager.getValueFromVectorParam(result, 5).getValue();

      Date bt536 = null;
      Long bt36 = null;
      String bt36Unit = "";
      Date bt537 = null;
      String bt538 = "";

      String codgar = getCodgarByNgara(ngara);
      if(Long.valueOf(3).equals(getGenereByCodgar(codgar))) {
        String selectFittizia = "select teutil, temesi, dinlavg from gare where codgar1 = ? and genere = 3";
        List dati = sqlManager.getVector(selectFittizia,
            new Object[] { codgar });
        if (dati != null && dati.size() > 0) {
          teutil = (Long) SqlManager.getValueFromVectorParam(dati, 0).getValue();
          temesi = (Long) SqlManager.getValueFromVectorParam(dati, 1).getValue();
          dinlavgDate = (Date) SqlManager.getValueFromVectorParam(dati, 2).getValue();
        }
      }

      if ("1".equals(accqua)) {
        if (aqdurata != null && aqtempo != null) {
          switch (aqtempo.intValue()) {
          case 1:
            bt36Unit = "MONTH";
            bt36 = aqdurata;
            break;
          case 2:
            bt36Unit = "MONTH";
            bt36 = aqdurata*12;
            break;
          case 3:
            bt36Unit = "DAY";
            bt36 = aqdurata;
            break;
          default:
            break;
          }
        }
      } else if (teutil != null && temesi != null) {
        if (((Long) 1L).equals(temesi)) {
          if (dinlavgDate == null) {
            bt36Unit = "DAY";
            bt36 = teutil;

          } else {
            Calendar dinlavg = Calendar.getInstance();
            dinlavg.setTime(dinlavgDate);
            bt536 = new java.sql.Date(dinlavg.getTimeInMillis());

            dinlavg.add(Calendar.DATE,teutil.intValue());
            bt537 = new java.sql.Date(dinlavg.getTimeInMillis());
          }
        } else if (((Long) 2L).equals(temesi)) {
          if (dinlavgDate == null) {
            bt36Unit = "MONTH";
            bt36 = teutil;
          } else {
            Calendar dinlavg = Calendar.getInstance();
            dinlavg.setTime(dinlavgDate);
            bt536 = new java.sql.Date(dinlavg.getTimeInMillis());

            dinlavg.add(Calendar.MONTH,teutil.intValue());
            bt537 = new java.sql.Date(dinlavg.getTimeInMillis());
          }
        }
      }      

      JSONObject durObj = new JSONObject();

      addIfNotEmpty(durObj, "BT-537-Lot", bt537);
      addIfNotEmpty(durObj, "BT-538-Lot", bt538);
      addIfNotEmpty(durObj, "BT-36-Lot", bt36);
      addIfNotEmpty(durObj, "BT-36-Lot-Unit", bt36Unit);
      addIfNotEmpty(durObj, "BT-536-Lot", bt536);

      //plannedDurArray.add(durObj);
      putIfNotEmpty(purposeObj,"GR-Lot-PlannedDuration", durObj);
    }
  }

  private void populateGRLotProcurementType(JSONObject purposeObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray procTypeArray = new JSONArray();
    JSONObject procTypeObj = new JSONObject();

    // GR-Lot-Green-Procurement
    populateGRLotGreenProcurement(procTypeObj, codiceEform, ngara);

    // GR-Lot-ProcurementType-Strategic
    populateGRLotProcurementTypeStrategic(procTypeObj, codiceEform, ngara);

    // GR-Lot-Environmental-Impact
    populateGRLotEnvironmentalImpact(procTypeObj, codiceEform, ngara);

    // GR-Lot-ProcurementType-Accessibility
    populateGRLotProcurementTypeAccessibility(procTypeObj, codiceEform, ngara);

    // GR-Lot-Social-Objective
    populateGRLotSocialObjective(procTypeObj, codiceEform, ngara);

    // GR-Lot-Innovation-Procurement
    populateGRLotInnovationProcurement(procTypeObj, codiceEform, ngara);

    procTypeArray.add(procTypeObj);
    putIfNotEmpty(purposeObj,"GR-Lot-ProcurementType", procTypeArray);
  }

  private void populateGRLotGreenProcurement(JSONObject procTypeObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray greenArray = new JSONArray();
    JSONObject greenObj = new JSONObject();

    // "BT-805-Lot"
    String bt805 = "";
    addIfNotEmpty(greenObj, "BT-805-Lot", bt805);

    greenArray.add(greenObj);
    putIfNotEmpty(procTypeObj,"GR-Lot-Green-Procurement", greenArray);
  }

  private void populateGRLotProcurementTypeStrategic(JSONObject procTypeObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_PROCUREMENTTYPE_STRATEGIC, Long.valueOf(0));

    //JSONArray strategicArray = new JSONArray();
    for(Object result : results){

      String isgreen = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String isrecycle = SqlManager.getValueFromVectorParam(result, 1).stringValue();
      String desgreen = SqlManager.getValueFromVectorParam(result, 2).stringValue();
      
      String bt06 = "";
      String bt777 = "";
      if((isgreen != null && "1".equals(isgreen)) || (isrecycle != null && "1".equals(isrecycle))) {
        bt06 = "env-imp";
        bt777 = desgreen;
      }
      
      JSONObject stratObj = new JSONObject();
      
      addIfNotEmpty(stratObj, "BT-06-Lot", bt06);
      addIfNotEmpty(stratObj, "BT-777-Lot", bt777);
      
      //strategicArray.add(stratObj);
      putIfNotEmpty(procTypeObj,"GR-Lot-ProcurementType-Strategic", stratObj);
    }
  }

  private void populateGRLotEnvironmentalImpact(JSONObject procTypeObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray envImpactArray = new JSONArray();
    JSONObject envObj = new JSONObject();

    // "BT-774-Lot"
    String bt774 = "";
    addIfNotEmpty(envObj, "BT-774-Lot", bt774);

    envImpactArray.add(envObj);
    putIfNotEmpty(procTypeObj,"GR-Lot-Environmental-Impact", envImpactArray);
  }

  private void populateGRLotProcurementTypeAccessibility(JSONObject procTypeObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray accessibilityArray = new JSONArray();
    JSONObject accessObj = new JSONObject();

    // "BT-755-Lot"
    String bt755 = "";
    addIfNotEmpty(accessObj, "BT-755-Lot", bt755);
    // "BT-754-Lot"
    String bt754 = "";
    addIfNotEmpty(accessObj, "BT-754-Lot", bt754);

    accessibilityArray.add(accessObj);
    putIfNotEmpty(procTypeObj,"GR-Lot-ProcurementType-Accessibility", accessibilityArray);
  }

  private void populateGRLotSocialObjective(JSONObject procTypeObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray socialObjArray = new JSONArray();
    JSONObject socialObj = new JSONObject();

    // "BT-775-Lot"
    String bt775 = "";
    addIfNotEmpty(socialObj, "BT-775-Lot", bt775);

    socialObjArray.add(socialObj);
    putIfNotEmpty(procTypeObj,"GR-Lot-Social-Objective", socialObjArray);
  }

  private void populateGRLotInnovationProcurement(JSONObject procTypeObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray innovationArray = new JSONArray();
    JSONObject innovationObj = new JSONObject();

    // "BT-776-Lot"
    String bt776 = "";
    addIfNotEmpty(innovationObj, "BT-776-Lot", bt776);

    innovationArray.add(innovationObj);
    putIfNotEmpty(procTypeObj,"GR-Lot-Innovation-Procurement", innovationArray);
  }

  private void populateGRLotEnvironLegis(JSONObject purposeObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray environArray = new JSONArray();
    JSONObject envObj = new JSONObject();

    // "OPT-112-Lot-EnvironLegis"
    String opt112 = "";
    addIfNotEmpty(envObj, "OPT-112-Lot-EnvironLegis", opt112);
    // "OPT-120-Lot-EnvironLegis"
    String opt120 = "";
    addIfNotEmpty(envObj, "OPT-120-Lot-EnvironLegis", opt120);
    // "OPT-301-Lot-EnvironLegis"
    String opt301 = "";
    addIfNotEmpty(envObj, "OPT-301-Lot-EnvironLegis", opt301);

    environArray.add(envObj);
    putIfNotEmpty(purposeObj,"GR-Lot-EnvironLegis", environArray);
  }

  private void populateGRLotPurposeAdditionalInformation(JSONObject purposeObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray addInfoArray = new JSONArray();
    JSONObject addInfoObj = new JSONObject();

    // "BT-300-Lot"
    String bt300 = "";
    addIfNotEmpty(addInfoObj, "BT-300-Lot", bt300);

    addInfoArray.add(addInfoObj);
    putIfNotEmpty(purposeObj,"GR-Lot-AdditionalInformation", addInfoArray);
  }

  private void populateGRLotContractingSystem(JSONObject purposeObj, String codiceEform, String ngara) throws Exception {

    //JSONArray contractingSystemArray = new JSONArray();
    JSONObject contractSysObj = new JSONObject();

    // GR-Lot-FrameworkAgreement-Use
    populateGRLotFrameworkAgreementUse(contractSysObj, codiceEform, ngara);

    // GR-Lot-DPS-Use
    populateGRLotDPSUse(contractSysObj, codiceEform, ngara);

    //contractingSystemArray.add(contractSysObj);
    putIfNotEmpty(purposeObj,"GR-Lot-ContractingSystem", contractSysObj);
  }

  //GR-Lot-FrameworkAgreement-Use
  private void populateGRLotFrameworkAgreementUse(JSONObject contractSysObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_FRAMEWORKAGREEMENT_USE, Long.valueOf(0));

    for(Object result : results){

      //JSONArray faUseArray = new JSONArray();
      JSONObject faUseObj = new JSONObject();

      
      String accqua = SqlManager.getValueFromVectorParam(result, 0).stringValue();

      // "BT-765-Lot"
      String bt765 = "none";
      if(accqua != null && "1".equals(accqua)) {
        bt765 = "fa-mix";
      }

      addIfNotEmpty(faUseObj, "BT-765-Lot", bt765);

      //faUseArray.add(faUseObj);
      putIfNotEmpty(contractSysObj,"GR-Lot-FrameworkAgreement-Use", faUseObj);
    }
  }

  //GR-Lot-DPS-Use
  private void populateGRLotDPSUse(JSONObject contractSysObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_DPS_USE, Long.valueOf(0));

    for(Object result : results){

      String issda = SqlManager.getValueFromVectorParam(result, 0).stringValue();

      JSONObject dpsUseObj = new JSONObject();

      // "BT-766-Lot"
      String bt766 = "none";
      if(issda != null && !issda.isEmpty()) {
        bt766 = getTab2d1ByTab2tip("A1z32", issda);
      }
      addIfNotEmpty(dpsUseObj, "BT-766-Lot", bt766);

      putIfNotEmpty(contractSysObj,"GR-Lot-DPS-Use", dpsUseObj);
    }
  }

  //GR-Lot-FrameworkAgreement
  private void populateGRLotFrameworkAgreement(JSONObject purposeObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_FRAMEWORKAGREEMENT, Long.valueOf(0));

    //JSONArray frameworkArray = new JSONArray();
    for(Object result : results){

      Long aqoper = SqlManager.getValueFromVectorParam(result, 0).longValue();
      Long aqnumope = SqlManager.getValueFromVectorParam(result, 1).longValue();
      
      Long bt113 = null;
      if(Long.valueOf(1).equals(aqoper)) {
        bt113 = aqoper;
      } else if(Long.valueOf(2).equals(aqoper)) {
        bt113 = aqnumope;
      }
      
      String bt111 = "";
      String bt271 = "";
      String bt109 = "";
      String opt90 = "";

      JSONObject fwObj = new JSONObject();
      
      addIfNotEmpty(fwObj, "BT-113-Lot", bt113);
      addIfNotEmpty(fwObj, "BT-111-Lot", bt111);
      addIfNotEmpty(fwObj, "BT-271-Lot", bt271);
      addIfNotEmpty(fwObj, "BT-109-Lot", bt109);
      addIfNotEmpty(fwObj, "OPT-090-Lot", opt90);
      
      //frameworkArray.add(fwObj);
      putIfNotEmpty(purposeObj,"GR-Lot-FrameworkAgreement", fwObj);
    }
  }

  private void populateGRLotEUFunds(JSONObject purposeObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_EUFUNDS, Long.valueOf(0));

    for(Object result : results){

      
      String bt6140 = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt7220 = "";
      String bt5010 = "";

    JSONArray euFundsArray = new JSONArray();
    JSONObject euFundsObj = new JSONObject();

    addIfNotEmpty(euFundsObj, "BT-7220-Lot", bt7220);
    addIfNotEmpty(euFundsObj, "BT-6140-Lot", bt6140);
    addIfNotEmpty(euFundsObj, "BT-5010-Lot", bt5010);

    euFundsArray.add(euFundsObj);
    putIfNotEmpty(purposeObj,"GR-Lot-EUFunds", euFundsArray);
    }
  }

  /**
   * Popola la sezione "GR-Lot-EUFunds-Indicator" dentro l'oggetto purposeObj,
   * aggiungendo il campo "BT-60-Lot".
   */
  private void populateGRLotEUFundsIndicator(JSONObject purposeObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_EUFUNDS_INDICATOR, Long.valueOf(0));

    //JSONArray euFundsIndicatorArray = new JSONArray();
    for(Object result : results){

      // "GR-Lot-EUFunds-Indicator" e' un array
      JSONObject euFundsIndicatorObj = new JSONObject();

      
      String bt60 = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt60Str = "no-eu-funds";
      if(bt60 != null && "1".equals(bt60)) {
        bt60Str = "eu-funds";
      }

      addIfNotEmpty(euFundsIndicatorObj, "BT-60-Lot", bt60Str);

      // Aggiungiamo euFundsIndicatorObj all'array
      //euFundsIndicatorArray.add(euFundsIndicatorObj);
      putIfNotEmpty(purposeObj,"GR-Lot-EUFunds-Indicator", euFundsIndicatorObj);
    }


  }


  /**
   * Popola la sezione "GR-Lot-AuctionTerms"
   */
  private void populateGRLotAuctionTerms(JSONObject purposeObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_AUCTIONTERMS, Long.valueOf(0));

    for(Object result : results){

      
      String ricastae = SqlManager.getValueFromVectorParam(result, 0).stringValue();


      //JSONArray auctionTermsArray = new JSONArray();
      JSONObject auctionTermsObj = new JSONObject();
      
      Boolean bt767 = Boolean.FALSE;
      String bt122 = "";
      String bt123 = "";
      
      if(ricastae != null && "1".equals(ricastae)) {
        bt767 = Boolean.TRUE;
      }
      
      addIfNotEmpty(auctionTermsObj, "BT-767-Lot", bt767);
      addIfNotEmpty(auctionTermsObj, "BT-122-Lot", bt122);
      addIfNotEmpty(auctionTermsObj, "BT-123-Lot", bt123);

      // Aggiungiamo l'oggetto all'array
      //auctionTermsArray.add(auctionTermsObj);

      // Inseriamo l'array "GR-Lot-AuctionTerms" nel purposeObj
      putIfNotEmpty(purposeObj,"GR-Lot-AuctionTerms", auctionTermsObj);
    }
  }

  private void populateGRLotDescription(JSONObject purposeObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_DESCRIPTION, null);

    //Non e' un array
    //JSONArray descrArray = new JSONArray();

    for(Object result : results){

      
      String bt21 = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = SqlManager.getValueFromVectorParam(result, 1).stringValue();
      Object bt23 = SqlManager.getValueFromVectorParam(result, 2).getValue();
      String bt22 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();

      JSONObject descrObj = new JSONObject();

      addIfNotEmpty(descrObj, "BT-21-Lot", bt21);

      // GR-Lot-Additional-Nature
      populateGRLotAdditionalNature(descrObj, codiceEform, ngara);

      Long bt23Long = null;
      if (bt23 instanceof String) {
        bt23Long = Long.valueOf((String) bt23);
      } else if (bt23 instanceof Long) {
        bt23Long = Long.valueOf((Long) bt23);
      }
      
      addIfNotEmpty(descrObj, "BT-24-Lot", bt24);
      addIfNotEmpty(descrObj, "BT-23-Lot", getValoreByTip(bt23Long,getGenereByNgara(ngara)));
      addIfNotEmpty(descrObj, "BT-22-Lot", bt22);

      //      descrArray.add(descrObj);
      putIfNotEmpty(purposeObj,"GR-Lot-Description", descrObj);
    }
  }

  private void populateGRLotAdditionalNature(JSONObject descrObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray addNatureArray = new JSONArray();
    JSONObject addNatureObj = new JSONObject();

    // "BT-531-Lot"
    String bt531 = "";
    addIfNotEmpty(addNatureObj, "BT-531-Lot", bt531);

    addNatureArray.add(addNatureObj);
    putIfNotEmpty(descrObj,"GR-Lot-Additional-Nature", addNatureArray);
  }

  private void populateGRLotFiscalLegis(JSONObject purposeObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray fiscalArray = new JSONArray();
    JSONObject fiscalObj = new JSONObject();

    // "OPT-111-Lot-FiscalLegis"
    String opt111 = "";
    addIfNotEmpty(fiscalObj, "OPT-111-Lot-FiscalLegis", opt111);
    // "OPT-110-Lot-FiscalLegis"
    String opt110 = "";
    addIfNotEmpty(fiscalObj, "OPT-110-Lot-FiscalLegis", opt110);
    // "OPT-301-Lot-FiscalLegis"
    String opt301 = "";
    addIfNotEmpty(fiscalObj, "OPT-301-Lot-FiscalLegis", opt301);

    fiscalArray.add(fiscalObj);
    putIfNotEmpty(purposeObj,"GR-Lot-FiscalLegis", fiscalArray);
  }

  private void populateGRLotPreviousPlanning(JSONObject purposeObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray prevPlanningArray = new JSONArray();
    JSONObject prevPlanObj = new JSONObject();

    // "BT-1251-Lot"
    String bt1251 = "";
    addIfNotEmpty(prevPlanObj, "BT-1251-Lot", bt1251);
    // "BT-125(i)-Lot"
    String bt125i = "";
    addIfNotEmpty(prevPlanObj, "BT-125(i)-Lot", bt125i);

    prevPlanningArray.add(prevPlanObj);
    putIfNotEmpty(purposeObj,"GR-Lot-PreviousPlanning", prevPlanningArray);
  }

  //GR-Lot-PlaceOfPerformance
  private void populateGRLotPlaceOfPerformance(JSONObject purposeObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOT_PLACEOFPERFORMANCE, null);

    //JSONArray placePerfArray = new JSONArray();
    for(Object result : results){

      String bt5071 = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String prosla = SqlManager.getValueFromVectorParam(result, 1).stringValue();
      String loclav = SqlManager.getValueFromVectorParam(result, 2).stringValue();
      String nomssl = SqlManager.getValueFromVectorParam(result, 3).stringValue();
      Long locnaz = SqlManager.getValueFromVectorParam(result, 4).longValue();
      
      String bt5141 = "ITA";
      if(locnaz != null) {
        bt5141 = getSiglaByCodice(locnaz);
      }

      String bt5131 = "";
      if(nomssl != null){bt5131 = bt5131 + nomssl;}
      if(nomssl != null && prosla != null){bt5131 = bt5131 + "  ";}
      if(loclav != null){bt5131 = bt5131 + loclav;}
      if(prosla != null){bt5131 = bt5131 + " (" + prosla + ")";}
      
      String bt5101c = "";
      String bt5121 = "";
      String bt728 = "";
      String bt727 = "";
      String bt5101b = "";
      String bt5101a = "";

      JSONObject placePerfObj = new JSONObject();
      
      addIfNotEmpty(placePerfObj, "BT-5141-Lot", bt5141);
      addIfNotEmpty(placePerfObj, "BT-5101(c)-Lot", bt5101c);
      if (bt5071 != null && bt5071.length() == 5) {
        addIfNotEmpty(placePerfObj, "BT-5071-Lot", bt5071);
      }
      addIfNotEmpty(placePerfObj, "BT-5121-Lot", bt5121);
      addIfNotEmpty(placePerfObj, "BT-728-Lot", bt728);
      addIfNotEmpty(placePerfObj, "BT-727-Lot", bt727);
      addIfNotEmpty(placePerfObj, "BT-5101(b)-Lot", bt5101b);
      addIfNotEmpty(placePerfObj, "BT-5131-Lot", bt5131);
      addIfNotEmpty(placePerfObj, "BT-5101(a)-Lot", bt5101a);
      
      //placePerfArray.add(placePerfObj);
      putIfNotEmpty(purposeObj,"GR-Lot-PlaceOfPerformance", placePerfObj);
    }
  }


  /**
   * Popola la macro-sezione "GR-Buyer" all'interno di un JSONObject "parent".
   * Secondo lo schema, "GR-Buyer" e' un array con un oggetto che contiene:
   * - "GR-ContractingAuthority": array
   *    - Ogni oggetto di "GR-ContractingAuthority" contiene:
   *      - "GR-ContractingAuthority-Buyer": array con i campi buyer
   *      - "GR-Procedure-SProvider": array con i campi SProvider
   *
   * @param parent      L'oggetto JSON che conterra' la sezione "GR-Buyer"
   * @param codiceEform Parametro obbligatorio in firma, ma NON usato
   * @return            Lo stesso JSONObject, arricchito di "GR-Buyer"
   */
  public JSONObject populateGRBuyer(JSONObject parent, String codiceEform, String uuidEform, EntityManager entityManager) throws Exception {
    // 1) "GR-Buyer" e' un array
    //JSONArray grBuyerArray = new JSONArray();

    // 2) Creiamo l'oggetto che andra' in "GR-Buyer"
    JSONObject grBuyerObj = new JSONObject();

    // 3) Popoliamo "GR-ContractingAuthority"
    populateGRContractingAuthority(grBuyerObj, codiceEform, uuidEform, entityManager);

    // 4) Aggiungiamo grBuyerObj all'array
    //grBuyerArray.add(grBuyerObj);

    // 5) Inseriamo l'array nel parent
    putIfNotEmpty(parent,"GR-Buyer", grBuyerObj);

    return parent;
  }

  // =========================================================================
  //         GR-ContractingAuthority
  // =========================================================================
  private void populateGRContractingAuthority(JSONObject grBuyerObj, String codiceEform, String uuidEform, EntityManager entityManager) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-ContractingAuthority" e' un array
    JSONArray contractingAuthorityArray = new JSONArray();
    JSONObject contractingAuthorityObj = new JSONObject();

    // 1) GR-ContractingAuthority-Buyer
    populateGRContractingAuthorityBuyer(contractingAuthorityObj, codiceEform, uuidEform, entityManager);

    // 2) GR-Procedure-SProvider
    populateGRProcedureSProvider(contractingAuthorityObj, codiceEform, uuidEform);

    contractingAuthorityArray.add(contractingAuthorityObj);
    putIfNotEmpty(grBuyerObj,"GR-ContractingAuthority", contractingAuthorityArray);
  }

  // -----------------------------------------------------
  // GR-ContractingAuthority-Buyer
  // -----------------------------------------------------
  private void populateGRContractingAuthorityBuyer(JSONObject contractingAuthorityObj, String codiceEform, String uuidEform, EntityManager entityManager) throws Exception {

    List<?> results = getDati(uuidEform, PathJsonEform.GR_CONTRACTINGAUTHORITY_BUYER, Long.valueOf(0));

    // "GR-ContractingAuthority-Buyer" e' un array
    //JSONArray caBuyerArray = new JSONArray();

    for (Object result : results) {

      JSONObject caBuyerObj = new JSONObject();

      
      String bt11        = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt10        = SqlManager.getValueFromVectorParam(result, 1).stringValue();
      String bt508       = SqlManager.getValueFromVectorParam(result, 2).stringValue();
      String bt610       = SqlManager.getValueFromVectorParam(result, 3).stringValue();
      String codein      = SqlManager.getValueFromVectorParam(result, 4).stringValue();
      String bt740       = "cont-ent";

      // Aggiungi campi (solo se non vuoti)
      addIfNotEmpty(caBuyerObj, "BT-11-Procedure-Buyer",  getTab2d1ByTab2tip(bt11,"G_z28"));
      addIfNotEmpty(caBuyerObj, "BT-10-Procedure-Buyer",  getTab2d1ByTab2tip(bt10,"G_z29"));
      addIfNotEmpty(caBuyerObj, "OPT-300-Procedure-Buyer", entityManager.getOrCreateValue(PREFISSO_ORG, "uffint", codein, Boolean.TRUE));
      addIfNotEmpty(caBuyerObj, "BT-508-Procedure-Buyer", bt508);
      addIfNotEmpty(caBuyerObj, "BT-610-Procedure-Buyer", getTab2d1ByTab2tip(bt610,"G_z30"));
      addIfNotEmpty(caBuyerObj, "BT-740-Procedure-Buyer", bt740);

      // Aggiungo l'oggetto all'array
      //caBuyerArray.add(caBuyerObj);
      putIfNotEmpty(contractingAuthorityObj,"GR-ContractingAuthority-Buyer", caBuyerObj);
    }

  }

  // -----------------------------------------------------
  // GR-Procedure-SProvider
  // -----------------------------------------------------
  private void populateGRProcedureSProvider(JSONObject contractingAuthorityObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-Procedure-SProvider" e' un array
    JSONArray sProviderArray = new JSONArray();
    JSONObject sProviderObj = new JSONObject();

    // "OPT-030-Procedure-SProvider"
    String opt030 = "";
    addIfNotEmpty(sProviderObj, "OPT-030-Procedure-SProvider", opt030);
    // "OPT-300-Procedure-SProvider"
    String opt300SProv = "";
    addIfNotEmpty(sProviderObj, "OPT-300-Procedure-SProvider", opt300SProv);

    sProviderArray.add(sProviderObj);
    putIfNotEmpty(contractingAuthorityObj,"GR-Procedure-SProvider", sProviderArray);
  }


  /**
   * Popola la sezione "GR-Result"
   */
  public JSONObject populateGRResult(JSONObject parent, String codiceEform, String uuidEform, EntityManager entityManager) throws Exception {

    // Creiamo l'oggetto interno
    JSONObject grResultObj = new JSONObject();

    // 1) GR-TenderingParty-Section
    populateGRTenderingPartySection(grResultObj, codiceEform, uuidEform, entityManager);

    // 2) GR-LotResult-Section
    populateGRLotResultSection(grResultObj, codiceEform, uuidEform, entityManager);

    // 3) Popoliamo "GR-NoticeResult-Section"
    populateGRNoticeResultSection(grResultObj, codiceEform, uuidEform);

    // 4) Popoliamo "GR-LotTender-Section"
    populateGRLotTenderSection(grResultObj, codiceEform, uuidEform, entityManager);

    // 5) Popoliamo "GR-SettledContract-Section"
    populateGRSettledContractSection(grResultObj, codiceEform, uuidEform, entityManager);

    // Mettiamo l'array in parent
    putIfNotEmpty(parent,"GR-Result", grResultObj);

    return parent;
  }

  // =========================================================================
  //          GR-TenderingParty-Section
  // =========================================================================
  private void populateGRTenderingPartySection(JSONObject grResultObj, String codiceEform, String uuidEform, EntityManager entityManager) throws Exception {

    JSONObject tenderingPartySectionObj = new JSONObject();

    // GR-TenderingParty
    populateGRTenderingParty(tenderingPartySectionObj, codiceEform, uuidEform, entityManager);

    putIfNotEmpty(grResultObj,"GR-TenderingParty-Section", tenderingPartySectionObj);
  }

  private void populateGRTenderingParty(JSONObject tenderingPartySectionObj, String codiceEform, String uuidEform, EntityManager entityManager) throws Exception {

    List<?> listaLotti = new ArrayList<>();
    
    Boolean isAD1 = codiceEform.equals("25") || codiceEform.equals("26") || codiceEform.equals("28");
    
    if(Boolean.FALSE.equals(isAD1)) {
      listaLotti = getDatiCAN(uuidEform, PathJsonEform.GET_LOTTI_CAN, Long.valueOf(0));
    } else {
      listaLotti = getDati(uuidEform, PathJsonEform.GET_LOTTI, null);
    }
    
    JSONArray tenderingPartyArray = new JSONArray();
    List<Object> results = new ArrayList<>();
    for (int i = 0; i < listaLotti.size(); i++) {
      String ngara = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).stringValue();

      Long aqoper = getAqoperByNgara(ngara);
      if(aqoper != null && Long.valueOf(2).equals(aqoper)) {
        results.addAll((List<?>) getDatiLotti(ngara, PathJsonEform.GR_TENDERINGPARTY_ACCORDO_QUADRO, Long.valueOf(0)));
      } else {
        results.addAll((List<?>) getDatiLotti(ngara, PathJsonEform.GR_TENDERINGPARTY, Long.valueOf(0)));
      }
    }
    List<String> ditteGiaInserite = new ArrayList<>();
    for(Object result : results){
      String dittagare = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String opt211 = SqlManager.getValueFromVectorParam(result, 1).stringValue();
      Long tipimp = SqlManager.getValueFromVectorParam(result, 2).longValue();

      if(!ditteGiaInserite.contains(dittagare)) {
        ditteGiaInserite.add(dittagare);
        JSONObject tenderingPartyObj = new JSONObject();

        //if(aqoper != null && Long.valueOf(2).equals(aqoper)) { //caso AccordoQuadro
        if(tipimp != null && (tipimp == 10 || tipimp == 3)){
          List<?> listRagimp = sqlManager.getListVector("select coddic from ragimp where codime9 = ?", new Object[] { dittagare });
          String tpaChiave = "";
          List<String> listaditteRTI = new ArrayList<>();
          for(int y = 0; y < listRagimp.size(); y++) {
            String dittaRT  = (String) SqlManager.getValueFromVectorParam(listRagimp.get(y), 0).getValue();
            tpaChiave += entityManager.getOrCreateValue(PREFISSO_ORG, "impresa", dittaRT, Boolean.TRUE)+";";
            listaditteRTI.add(dittaRT);
          }
          tpaChiave = tpaChiave.substring(0, tpaChiave.length()-1);
          String opt210 = entityManager.getOrCreateValue(PREFISSO_TPA, "raggruppamentoImprese", tpaChiave, Boolean.TRUE);
          addIfNotEmpty(tenderingPartyObj, "OPT-210-Tenderer", opt210);

          // GR-Subcontractor
          populateGRSubcontractor(tenderingPartyObj, codiceEform, uuidEform);

          addIfNotEmpty(tenderingPartyObj, "OPT-211-Tenderer", opt211);

          // GR-Tenderer
          populateGRTenderer(tenderingPartyObj, codiceEform, dittagare, listaditteRTI, entityManager);

          tenderingPartyArray.add(tenderingPartyObj);
        } else {
          String tpaChiave = entityManager.getOrCreateValue(PREFISSO_ORG, "impresa", dittagare, Boolean.TRUE);
          String opt210 = entityManager.getOrCreateValue(PREFISSO_TPA, "raggruppamentoImprese", tpaChiave, Boolean.TRUE);
          addIfNotEmpty(tenderingPartyObj, "OPT-210-Tenderer", opt210);

          // GR-Subcontractor
          populateGRSubcontractor(tenderingPartyObj, codiceEform, uuidEform);

          addIfNotEmpty(tenderingPartyObj, "OPT-211-Tenderer", opt211);

          List<String> listaDitta = new ArrayList<>();
          listaDitta.add(dittagare);
          // GR-Tenderer
          populateGRTenderer(tenderingPartyObj, codiceEform, dittagare, listaDitta, entityManager);

          tenderingPartyArray.add(tenderingPartyObj);
        }
        //}
      }
    }
    ditteGiaInserite.clear();

    putIfNotEmpty(tenderingPartySectionObj,"GR-TenderingParty", tenderingPartyArray);

  }
    

  private void populateGRSubcontractor(JSONObject tenderingPartyObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-Subcontractor" e' un array
    JSONArray subcontrArray = new JSONArray();
    JSONObject subcontrObj = new JSONObject();

    // GR-Tenderer-Main-Contractor-Id (array)
    populateGRTendererMainContractorId(subcontrObj, codiceEform, uuidEform);

    // "OPT-301-Tenderer-SubCont"
    String opt301SubCont = "";
    addIfNotEmpty(subcontrObj, "OPT-301-Tenderer-SubCont", opt301SubCont);

    subcontrArray.add(subcontrObj);
    putIfNotEmpty(tenderingPartyObj,"GR-Subcontractor", subcontrArray);
  }

  private void populateGRTendererMainContractorId(JSONObject subcontrObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-Tenderer-Main-Contractor-Id" e' un array
    JSONArray mainContrIdArray = new JSONArray();
    JSONObject mainContrIdObj = new JSONObject();

    // "OPT-301-Tenderer-MainCont"
    String opt301MainCont = "";
    addIfNotEmpty(mainContrIdObj, "OPT-301-Tenderer-MainCont", opt301MainCont);

    mainContrIdArray.add(mainContrIdObj);
    putIfNotEmpty(subcontrObj,"GR-Tenderer-Main-Contractor-Id", mainContrIdArray);
  }

  private void populateGRTenderer(JSONObject tenderingPartyObj, String codiceEform, String dittaPadre, List<String> listaDitte, EntityManager entityManager) throws Exception {

        // "GR-Tenderer" e' un array
    JSONArray tendererArray = new JSONArray();
    
    Boolean isRaggruppamento = Boolean.FALSE;
    //e' un consorzio o una rti
    if(listaDitte.size() > 1) {
      isRaggruppamento = Boolean.TRUE;
    }
    
    for(String ditta : listaDitte){
      JSONObject tendererObj = new JSONObject();
      
      String opt300 = entityManager.getOrCreateValue(PREFISSO_ORG, "impresa", ditta, Boolean.TRUE);
      Boolean opt170 = null;
      if(Boolean.TRUE.equals(isRaggruppamento)) {
        opt170 = Boolean.FALSE;
        if(Boolean.TRUE.equals(isMandatariaByDitta(dittaPadre, ditta))) {
          opt170 = Boolean.TRUE;          
        }
      }
      addIfNotEmpty(tendererObj, "OPT-300-Tenderer", opt300);
      addIfNotEmpty(tendererObj, "OPT-170-Tenderer", opt170);
      
      tendererArray.add(tendererObj);
    }
    putIfNotEmpty(tenderingPartyObj,"GR-Tenderer", tendererArray);
  }

  // =========================================================================
  //         GR-LotResult-Section
  // =========================================================================
  private void populateGRLotResultSection(JSONObject grResultObj, String codiceEform, String uuidEform, EntityManager entityManager) throws Exception {

    JSONObject lotResultSectionObj = new JSONObject();
    
    // GR-LotResult
    populateGRLotResult(lotResultSectionObj, codiceEform, uuidEform, entityManager);

    putIfNotEmpty(grResultObj,"GR-LotResult-Section", lotResultSectionObj);

  }

  /**
   * Popoliamo (in questa prima meta') alcune parti di "GR-LotResult" come:
   *  - "GR-LotResult-Parties"
   *  - "GR-LotResult-1" e "GR-LotResult-2"
   * 
   * NON gestiamo ancora:
   *  - "GR-LotResult-CVD"
   *  - "GR-LotResult-TenderValue"
   *  - "GR-Winner"
   *  - "GR-LotResult-ReceivedSubmissions"
   *  - "GR-LotResult-ReviewComplainants"
   *  - "GR-LotResult-ReviewRequests"
   * ... (lo faremo nella seconda meta')
   */
  private void populateGRLotResult(JSONObject lotResultSectionObj, String codiceEform, String uuidEform, EntityManager entityManager) throws Exception {

    List<?> listaLottiAggiudicati = new ArrayList<>();

    Boolean isAD1 = codiceEform.equals("25") || codiceEform.equals("26") || codiceEform.equals("28");
    
    if(Boolean.FALSE.equals(isAD1)) {
      listaLottiAggiudicati = getDatiCAN(uuidEform, PathJsonEform.GET_LOTTI_CAN, Long.valueOf(0));
    } else {
      listaLottiAggiudicati = getDati(uuidEform, PathJsonEform.GET_LOTTI, null);
    }

    JSONArray lotResultArray = new JSONArray();
    for (int i = 0; i < listaLottiAggiudicati.size(); i++) {
      String ngara = SqlManager.getValueFromVectorParam(listaLottiAggiudicati.get(i), 0).stringValue();

      JSONObject lotResultObj = new JSONObject();

      populateGRLotResultParties(lotResultObj, codiceEform, ngara);
      populateGRLotResult2(lotResultObj, codiceEform, ngara);
      populateGRLotResult1(lotResultObj, codiceEform, ngara, entityManager);

      String opt322LotResult = getResIdentByCodiga(getCodigaByNgara(ngara));
      addIfNotEmpty(lotResultObj, "OPT-322-LotResult", opt322LotResult);

      populateGRLotResultCVD(lotResultObj, codiceEform, ngara);
      populateGRLotResultTenderValue(lotResultObj, codiceEform, ngara);
      populateGRWinner(lotResultObj, codiceEform, ngara);
      populateGRLotResultReceivedSubmissions(lotResultObj, codiceEform, ngara);
      populateGRLotResultReviewComplainants(lotResultObj, codiceEform, ngara);
      populateGRLotResultReviewRequests(lotResultObj, codiceEform, ngara);

      lotResultArray.add(lotResultObj);
    }
    putIfNotEmpty(lotResultSectionObj,"GR-LotResult", lotResultArray);
  }

  // -----------------------------------------------------
  // GR-LotResult-Parties
  // -----------------------------------------------------
  private void populateGRLotResultParties(JSONObject lotResultObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-LotResult-Parties" e' un array
    JSONArray partiesArray = new JSONArray();
    JSONObject partiesObj = new JSONObject();

    // Sotto-sezioni: GR-LotResult-Financing-Party, GR-LotResult-Payer-Party
    populateGRLotResultFinancingParty(partiesObj, codiceEform, uuidEform);
    populateGRLotResultPayerParty(partiesObj, codiceEform, uuidEform);

    partiesArray.add(partiesObj);
    putIfNotEmpty(lotResultObj,"GR-LotResult-Parties", partiesArray);
  }

  private void populateGRLotResultFinancingParty(JSONObject partiesObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-LotResult-Financing-Party" e' un array
    JSONArray financingPartyArray = new JSONArray();
    JSONObject financingPartyObj = new JSONObject();

    // "OPT-301-LotResult-Financing"
    String opt301Fin = "";
    addIfNotEmpty(financingPartyObj, "OPT-301-LotResult-Financing", opt301Fin);

    financingPartyArray.add(financingPartyObj);
    putIfNotEmpty(partiesObj,"GR-LotResult-Financing-Party", financingPartyArray);
  }

  private void populateGRLotResultPayerParty(JSONObject partiesObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-LotResult-Payer-Party" e' un array
    JSONArray payerPartyArray = new JSONArray();
    JSONObject payerPartyObj = new JSONObject();

    // "OPT-301-LotResult-Paying"
    String opt301Pay = "";
    addIfNotEmpty(payerPartyObj, "OPT-301-LotResult-Paying", opt301Pay);

    payerPartyArray.add(payerPartyObj);
    putIfNotEmpty(partiesObj,"GR-LotResult-Payer-Party", payerPartyArray);
  }

  // -----------------------------------------------------
  // GR-LotResult-2
  // -----------------------------------------------------
  private void populateGRLotResult2(JSONObject lotResultObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-LotResult-2" e' un array
    JSONArray lotResult2Array = new JSONArray();
    JSONObject lotResult2Obj = new JSONObject();

    // "BT-660-LotResult"
    String bt660 = "";
    addIfNotEmpty(lotResult2Obj, "BT-660-LotResult", bt660);
    
    // "BT-709-LotResult"
    String bt709 = "";
    addIfNotEmpty(lotResult2Obj, "BT-709-LotResult", bt709);

    // "BT-119-LotResult"
    String bt119 = "";
    addIfNotEmpty(lotResult2Obj, "BT-119-LotResult", bt119);

    lotResult2Array.add(lotResult2Obj);
    putIfNotEmpty(lotResultObj,"GR-LotResult-2", lotResult2Array);
  }

  // -----------------------------------------------------
  // GR-LotResult-1
  // -----------------------------------------------------
  private void populateGRLotResult1(JSONObject lotResultObj, String codiceEform, String ngara, EntityManager entityManager) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOTRESULT_1, Long.valueOf(0));

    //JSONArray lotResult1Array = new JSONArray();
    for(Object result : results){
      
      String codiga = SqlManager.getValueFromVectorParam(result, 0).stringValue();

      String bt13713 = getLotIdentByCodiga(codiga);


      JSONObject lotResult1Obj = new JSONObject();

      // GR-LotResult-Contract-Id-Ref
      populateGRLotResultContractIdRef(lotResult1Obj, codiceEform, ngara, entityManager);

      addIfNotEmpty(lotResult1Obj, "BT-13713-LotResult", bt13713);

      // GR-LotResult-Tender-Id-Ref
      populateGRLotResultTenderIdRef(lotResult1Obj, codiceEform, ngara, entityManager);

      //lotResult1Array.add(lotResult1Obj);
      putIfNotEmpty(lotResultObj,"GR-LotResult-1", lotResult1Obj);
    }
  }

  private void populateGRLotResultContractIdRef(JSONObject lotResult1Obj, String codiceEform, String ngara, EntityManager entityManager) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOTRESULT_CONTRACT_ID_REF, Long.valueOf(0));

    //JSONArray contractIdRefArray = new JSONArray();
    for(Object result : results){

      String codgar = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      Long ncont = SqlManager.getValueFromVectorParam(result, 1).longValue();
      
      String chiave = codgar+""+ncont.toString();
      String opt315 = entityManager.getOrCreateValue(PREFISSO_CON, "contratto", chiave, Boolean.TRUE);
      
      JSONObject contractIdRefObj = new JSONObject();
      
      addIfNotEmpty(contractIdRefObj, "OPT-315-LotResult", opt315);
      
      //contractIdRefArray.add(contractIdRefObj);
      putIfNotEmpty(lotResult1Obj,"GR-LotResult-Contract-Id-Ref", contractIdRefObj);
    }
  }

  private void populateGRLotResultTenderIdRef(JSONObject lotResult1Obj, String codiceEform, String ngara, EntityManager entityManager) throws Exception {

    
    Long aqoper = getAqoperByNgara(ngara);
    List<?> results = new ArrayList<>();
    if(aqoper != null && Long.valueOf(2).equals(aqoper)) {
      results = getDatiLotti(ngara, PathJsonEform.GR_LOTRESULT_TENDER_ID_REF_ACCORDO_QUADRO, Long.valueOf(0));
    } else {
      results = getDatiLotti(ngara, PathJsonEform.GR_LOTRESULT_TENDER_ID_REF, Long.valueOf(0));
    }

    JSONArray tenderIdRefArray = new JSONArray();
    for(Object result : results){

      String dittao = SqlManager.getValueFromVectorParam(result, 0).stringValue();

      String chiave = getCodgarByNgara(ngara)+""+dittao+""+ngara;
      String opt320 = entityManager.getOrCreateValue(PREFISSO_TEN, "offerta", chiave, Boolean.TRUE);

      JSONObject tenderIdRefObj = new JSONObject();

      addIfNotEmpty(tenderIdRefObj, "OPT-320-LotResult", opt320);

      tenderIdRefArray.add(tenderIdRefObj);
      putIfNotEmpty(lotResult1Obj,"GR-LotResult-Tender-Id-Ref", tenderIdRefArray);
    }
  }

  // -----------------------------------------------------
  // GR-LotResult-CVD
  // -----------------------------------------------------
  private void populateGRLotResultCVD(JSONObject lotResultObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray cvdArray = new JSONArray();
    JSONObject cvdObj = new JSONObject();

    // "BT-735-LotResult"
    String bt735 = "";
    addIfNotEmpty(cvdObj, "BT-735-LotResult", bt735);

    // GR-LotResult-CVD-Vehicles
    populateGRLotResultCVDVehicles(cvdObj, codiceEform, uuidEform);

    cvdArray.add(cvdObj);
    putIfNotEmpty(lotResultObj,"GR-LotResult-CVD", cvdArray);
  }

  private void populateGRLotResultCVDVehicles(JSONObject cvdObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray vehiclesArray = new JSONArray();
    JSONObject vehiclesObj = new JSONObject();

    // GR-LotResult-CVD-Vehicles-Statistics
    populateGRLotResultCVDVehiclesStatistics(vehiclesObj, codiceEform, uuidEform);

    // "BT-723-LotResult"
    String bt723 = "";
    addIfNotEmpty(vehiclesObj, "BT-723-LotResult", bt723);

    vehiclesArray.add(vehiclesObj);
    putIfNotEmpty(cvdObj,"GR-LotResult-CVD-Vehicles", vehiclesArray);
  }

  private void populateGRLotResultCVDVehiclesStatistics(JSONObject vehiclesObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray statsArray = new JSONArray();
    JSONObject statsObj = new JSONObject();

    // "OPT-155-LotResult"
    String opt155 = "";
    addIfNotEmpty(statsObj, "OPT-155-LotResult", opt155);
    // "OPT-156-LotResult"
    String opt156 = "";
    addIfNotEmpty(statsObj, "OPT-156-LotResult", opt156);

    statsArray.add(statsObj);
    putIfNotEmpty(vehiclesObj,"GR-LotResult-CVD-Vehicles-Statistics", statsArray);
  }

  // -----------------------------------------------------
  // GR-LotResult-TenderValue
  // -----------------------------------------------------
  private void populateGRLotResultTenderValue(JSONObject lotResultObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray tenderValueArray = new JSONArray();
    JSONObject tvObj = new JSONObject();

    // "BT-711-LotResult"
    String bt711 = "";
    addIfNotEmpty(tvObj, "BT-711-LotResult", bt711);
    // "BT-710-LotResult"
    String bt710 = "";
    addIfNotEmpty(tvObj, "BT-710-LotResult", bt710);

    tenderValueArray.add(tvObj);
    putIfNotEmpty(lotResultObj,"GR-LotResult-TenderValue", tenderValueArray);
  }

  // -----------------------------------------------------
  // GR-Winner
  // -----------------------------------------------------
  private void populateGRWinner(JSONObject lotResultObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_WINNER, Long.valueOf(0));

    for(Object result : results){

      Long esineg = SqlManager.getValueFromVectorParam(result, 0).longValue();
      String bt144Giustneg = SqlManager.getValueFromVectorParam(result, 1).stringValue();
      String bt142 = esineg!=null?"clos-nw":"selec-w";
      
      JSONObject winnerObj = new JSONObject();
      
      addIfNotEmpty(winnerObj, "BT-144-LotResult", getTab2d1ByTab2tip(bt144Giustneg, "A1z27"));
      
      addIfNotEmpty(winnerObj, "BT-142-LotResult", bt142);
      
      putIfNotEmpty(lotResultObj,"GR-Winner", winnerObj);
    }
  }

  // -----------------------------------------------------
  // GR-LotResult-ReceivedSubmissions
  // -----------------------------------------------------
  private void populateGRLotResultReceivedSubmissions(JSONObject lotResultObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_LOTRESULT_RECEIVEDSUBMISSIONS, Long.valueOf(0));
    
    for(Object result : results){

      Long bt759 = SqlManager.getValueFromVectorParam(result, 0).longValue();
      
      JSONObject recvSubsObj = new JSONObject();

      addIfNotEmpty(recvSubsObj, "BT-759-LotResult", bt759);
      addIfNotEmpty(recvSubsObj, "BT-760-LotResult", "tenders");
      
      putIfNotEmpty(lotResultObj,"GR-LotResult-ReceivedSubmissions", recvSubsObj);
    }
  }

  // -----------------------------------------------------
  // GR-LotResult-ReviewComplainants
  // -----------------------------------------------------
  private void populateGRLotResultReviewComplainants(JSONObject lotResultObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray reviewComplArray = new JSONArray();
    JSONObject reviewComplObj = new JSONObject();

    // "BT-712(b)-LotResult"
    String bt712b = "";
    addIfNotEmpty(reviewComplObj, "BT-712(b)-LotResult", bt712b);
    // "BT-712(a)-LotResult"
    String bt712a = "";
    addIfNotEmpty(reviewComplObj, "BT-712(a)-LotResult", bt712a);

    reviewComplArray.add(reviewComplObj);
    putIfNotEmpty(lotResultObj,"GR-LotResult-ReviewComplainants", reviewComplArray);
  }

  // -----------------------------------------------------
  // GR-LotResult-ReviewRequests
  // -----------------------------------------------------
  private void populateGRLotResultReviewRequests(JSONObject lotResultObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray reviewReqArray = new JSONArray();
    JSONObject reviewReqObj = new JSONObject();

    // "BT-636-LotResult"
    String bt636 = "";
    addIfNotEmpty(reviewReqObj, "BT-636-LotResult", bt636);
    // "BT-635-LotResult"
    String bt635 = "";
    addIfNotEmpty(reviewReqObj, "BT-635-LotResult", bt635);

    reviewReqArray.add(reviewReqObj);
    putIfNotEmpty(lotResultObj,"GR-LotResult-ReviewRequests", reviewReqArray);
  }

  // =========================================================================
  //        2) GR-NoticeResult-Section
  // =========================================================================
  private void populateGRNoticeResultSection(JSONObject grResultObj, String codiceEform, String uuidEform) throws Exception {

    List<?> results = getDati(uuidEform, PathJsonEform.GR_NOTICERESULT_SECTION, Long.valueOf(0));

    for(Object result : results){

      Double bt161nr = SqlManager.getValueFromVectorParam(result, 0).doubleValue();
      String bt118nr = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
      String bt1118nr = "";//SqlManager.getValueFromVectorParam(result, 2).stringValue();

      JSONObject noticeResultSectionObj = new JSONObject();

      // 1) GR-GroupFramework
      populateGRGroupFramework(noticeResultSectionObj, codiceEform, uuidEform);

      addIfNotEmpty(noticeResultSectionObj, "BT-161-NoticeResult", bt161nr);
      addIfNotEmpty(noticeResultSectionObj, "BT-118-NoticeResult", bt118nr);
      addIfNotEmpty(noticeResultSectionObj, "BT-1118-NoticeResult", bt1118nr);

      putIfNotEmpty(grResultObj,"GR-NoticeResult-Section", noticeResultSectionObj);
    }
  }

  // -----------------------------------------------------
  // GR-GroupFramework
  // -----------------------------------------------------
  private void populateGRGroupFramework(JSONObject noticeResultSectionObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray groupFrameworkArray = new JSONArray();
    JSONObject groupFwObj = new JSONObject();

    // "BT-556-NoticeResult"
    String bt556 = "";
    addIfNotEmpty(groupFwObj, "BT-556-NoticeResult", bt556);

    // "BT-156-NoticeResult"
    String bt156 = "";
    addIfNotEmpty(groupFwObj, "BT-156-NoticeResult", bt156);
    // "BT-1561-NoticeResult"
    String bt1561 = "";
    addIfNotEmpty(groupFwObj, "BT-1561-NoticeResult", bt1561);

    groupFrameworkArray.add(groupFwObj);
    putIfNotEmpty(noticeResultSectionObj,"GR-GroupFramework", groupFrameworkArray);
  }

  // =========================================================================
  //        3) GR-LotTender-Section
  // =========================================================================
  private void populateGRLotTenderSection(JSONObject grResultObj, String codiceEform, String uuidEform, EntityManager entityManager) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray lotTenderSectionArray = new JSONArray();
    JSONObject lotTenderSectionObj = new JSONObject();

    // GR-LotTender
    populateGRLotTender(lotTenderSectionObj, codiceEform, uuidEform, entityManager);

    lotTenderSectionArray.add(lotTenderSectionObj);
    putIfNotEmpty(grResultObj,"GR-LotTender-Section", lotTenderSectionArray);
  }

  private void populateGRLotTender(JSONObject lotTenderSectionObj, String codiceEform, String uuidEform, EntityManager entityManager) throws Exception {

    List<?> listaLotti = new ArrayList<>();
    
    Boolean isAD1 = codiceEform.equals("25") || codiceEform.equals("26") || codiceEform.equals("28");
    
    if(Boolean.FALSE.equals(isAD1)) {
      listaLotti = getDatiCAN(uuidEform, PathJsonEform.GET_LOTTI_CAN, Long.valueOf(0));
    } else {
      listaLotti = getDati(uuidEform, PathJsonEform.GET_LOTTI, null);
    }

    JSONArray lotTenderArray = new JSONArray();
    for (int i = 0; i < listaLotti.size(); i++) {
      String ngara = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).stringValue();

      Long aqoper = getAqoperByNgara(ngara);
      List<?> results = new ArrayList<>();
      if(aqoper != null && Long.valueOf(2).equals(aqoper)) {
        results = getDatiLotti(ngara, PathJsonEform.GR_LOTTENDER_ACCORDO_QUADRO, Long.valueOf(0));
      } else {
        results = getDatiLotti(ngara, PathJsonEform.GR_LOTTENDER, Long.valueOf(0));
      }

      for(Object result : results){
        String dittao = SqlManager.getValueFromVectorParam(result, 0).stringValue();
        Long tipimp = SqlManager.getValueFromVectorParam(result, 1).longValue();

        JSONObject lotTenderObj = new JSONObject();

        // 1) GR-LotTender-Subcontracting
        populateGRLotTenderSubcontracting(lotTenderObj, codiceEform, ngara, dittao, tipimp);

        // 2) GR-LotTender-1
        populateGRLotTender1(lotTenderObj, codiceEform, ngara, dittao, tipimp, entityManager);

        // 3) GR-Tender
        populateGRTender(lotTenderObj, codiceEform, ngara, dittao, tipimp, entityManager);

        // 4) GR-LotTender-2
        populateGRLotTender2(lotTenderObj, codiceEform, ngara, dittao, tipimp);

        // 5) GR-FSR-Measures
        populateGRFSRMeasures(lotTenderObj, codiceEform, ngara, dittao, tipimp);

        lotTenderArray.add(lotTenderObj);
      }
    }
    putIfNotEmpty(lotTenderSectionObj,"GR-LotTender", lotTenderArray);
  }

  private void populateGRLotTenderSubcontracting(JSONObject lotTenderObj, String codiceEform, String ngara, String ditta, Long tipimp) throws Exception {

    List<?> results = getDatiTender(PathJsonEform.GR_LOTTENDER_SUBCONTRACTING, ngara, ditta);

    for(Object result : results){

      String ricsub = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      Boolean bt730 = null;
      Boolean bt731 = null;
      String bt555 = "";
      String bt553 = "";
      String bt554 = "";
      
      String bt773 = getValoreByCodice(ricsub);
      if("1".equals(ricsub)) {
        bt730 = Boolean.FALSE;
        bt731 = Boolean.FALSE;
      }

      JSONObject subcontrObj = new JSONObject();

      addIfNotEmpty(subcontrObj, "BT-730-Tender", bt730);
      addIfNotEmpty(subcontrObj, "BT-555-Tender", bt555);
      addIfNotEmpty(subcontrObj, "BT-773-Tender", bt773);

      addIfNotEmpty(subcontrObj, "BT-553-Tender", bt553);
      addIfNotEmpty(subcontrObj, "BT-554-Tender", bt554);
      addIfNotEmpty(subcontrObj, "BT-731-Tender", bt731);

      putIfNotEmpty(lotTenderObj,"GR-LotTender-Subcontracting", subcontrObj);
    }
  }

  private void populateGRLotTender1(JSONObject lotTenderObj, String codiceEform, String ngara, String ditta, Long tipimp, EntityManager entityManager) throws Exception {

    JSONObject tender1Obj = new JSONObject();

    String bt13714 = getLotIdentByCodiga(getCodigaByNgara(ngara));
    addIfNotEmpty(tender1Obj, "BT-13714-Tender", bt13714);

    if(tipimp != null && (tipimp == 10 || tipimp == 3)){
      List<?> listRagimp = sqlManager.getListVector("select coddic from ragimp where codime9 = ?", new Object[] { ditta });
      String tpaChiave = "";
      for(int y = 0; y < listRagimp.size(); y++) {
        String dittaRT  = (String) SqlManager.getValueFromVectorParam(listRagimp.get(y), 0).getValue();
        tpaChiave += entityManager.getOrCreateValue(PREFISSO_ORG, "impresa", dittaRT, Boolean.TRUE)+";";                
      }
      tpaChiave = tpaChiave.substring(0, tpaChiave.length()-1);
      String opt310 = entityManager.getOrCreateValue(PREFISSO_TPA, "raggruppamentoImprese", tpaChiave, Boolean.TRUE);
      addIfNotEmpty(tender1Obj, "OPT-310-Tender", opt310);
    } else {
      String tpaChiave = entityManager.getOrCreateValue(PREFISSO_ORG, "impresa", ditta, Boolean.TRUE);
      String opt310 = entityManager.getOrCreateValue(PREFISSO_TPA, "raggruppamentoImprese", tpaChiave, Boolean.TRUE);
      addIfNotEmpty(tender1Obj, "OPT-310-Tender", opt310);
    }

    putIfNotEmpty(lotTenderObj,"GR-LotTender-1", tender1Obj);
  }

  private void populateGRTender(JSONObject lotTenderObj, String codiceEform, String ngara, String ditta, Long tipimp, EntityManager entityManager) throws Exception {

    Long aqoper = getAqoperByNgara(ngara);
    List<?> results = new ArrayList<>();
    if(aqoper != null && Long.valueOf(2).equals(aqoper)) {
      results = getDatiTender(PathJsonEform.GR_TENDER_ACCORDO_QUADRO, ngara, ditta);
    } else {
      results = getDatiTender(PathJsonEform.GR_TENDER, ngara, ditta);
    }
    
    for(Object result : results){
      Double bt720 = SqlManager.getValueFromVectorParam(result, 0).doubleValue();
      Long bt3201 = SqlManager.getValueFromVectorParam(result, 1).longValue();
      
      String chiave = getCodgarByNgara(ngara)+""+ditta+""+ngara;
      String opt321 = entityManager.getOrCreateValue(PREFISSO_TEN, "offerta", chiave, Boolean.TRUE);
      
      String bt171 = "";
      String bt1711 = "";
      String bt193 = "";

      JSONObject grTenderObj = new JSONObject();
      
      addIfNotEmpty(grTenderObj, "BT-171-Tender", bt171);
      addIfNotEmpty(grTenderObj, "BT-720-Tender", bt720);
      addIfNotEmpty(grTenderObj, "OPT-321-Tender", opt321);
      addIfNotEmpty(grTenderObj, "BT-3201-Tender", bt3201);
      addIfNotEmpty(grTenderObj, "BT-1711-Tender", bt1711);
      addIfNotEmpty(grTenderObj, "BT-193-Tender", bt193);
      
      putIfNotEmpty(lotTenderObj,"GR-Tender", grTenderObj);
    }
  }

  private void populateGRLotTender2(JSONObject lotTenderObj, String codiceEform, String uuidEform, String ditta, Long tipimp) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray tender2Array = new JSONArray();
    JSONObject tender2Obj = new JSONObject();

    // "BT-160-Tender"
    String bt160 = "";
    addIfNotEmpty(tender2Obj, "BT-160-Tender", bt160);
    // "BT-162-Tender"
    String bt162 = "";
    addIfNotEmpty(tender2Obj, "BT-162-Tender", bt162);
    // "BT-163-Tender"
    String bt163 = "";
    addIfNotEmpty(tender2Obj, "BT-163-Tender", bt163);
    // "BT-191-Tender"
    String bt191 = "";
    addIfNotEmpty(tender2Obj, "BT-191-Tender", bt191);

    tender2Array.add(tender2Obj);
    putIfNotEmpty(lotTenderObj,"GR-LotTender-2", tender2Array);
  }

  private void populateGRFSRMeasures(JSONObject lotTenderObj, String codiceEform, String uuidEform, String ditta, Long tipimp) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray fsrMeasuresArray = new JSONArray();
    JSONObject fsrMeasuresObj = new JSONObject();

    // "BT-682-Tender"
    String bt682 = "";
    addIfNotEmpty(fsrMeasuresObj, "BT-682-Tender", bt682);

    fsrMeasuresArray.add(fsrMeasuresObj);
    putIfNotEmpty(lotTenderObj,"GR-FSR-Measures", fsrMeasuresArray);
  }

  // =========================================================================
  //        4) GR-SettledContract-Section
  // =========================================================================
  private void populateGRSettledContractSection(JSONObject grResultObj, String codiceEform, String uuidEform, EntityManager entityManager) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray settledContractSectionArray = new JSONArray();
    JSONObject settledContractSectionObj = new JSONObject();

    // GR-SettledContract
    populateGRSettledContract(settledContractSectionObj, codiceEform, uuidEform, entityManager);

    settledContractSectionArray.add(settledContractSectionObj);
    putIfNotEmpty(grResultObj,"GR-SettledContract-Section", settledContractSectionArray);
  }

  private void populateGRSettledContract(JSONObject settledContractSectionObj, String codiceEform, String uuidEform, EntityManager entityManager) throws Exception {

    
    List<?> listaLotti = new ArrayList<>();
    
    Boolean isAD1 = codiceEform.equals("25") || codiceEform.equals("26") || codiceEform.equals("28");

    if(Boolean.FALSE.equals(isAD1)) {
      listaLotti = getDatiCAN(uuidEform, PathJsonEform.GR_SETTLEDCONTRACT, Long.valueOf(0));
    } else {
      listaLotti = getDati(uuidEform, PathJsonEform.GR_SETTLEDCONTRACT_AD1, Long.valueOf(0));
    }

    JSONArray settledContractArray = new JSONArray();
    for (int i = 0; i < listaLotti.size(); i++) {
      String codgar = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).stringValue();
      Long ncont = SqlManager.getValueFromVectorParam(listaLotti.get(i), 1).longValue();

      JSONObject settledContractObj = new JSONObject();

      // 1) GR-SettledContract-2
      populateGRSettledContract2(settledContractObj, codiceEform, codgar, ncont);

      // 2) GR-SettledContract-1
      populateGRSettledContract1(settledContractObj, codiceEform, codgar, ncont, entityManager);

      settledContractArray.add(settledContractObj);

    }
    putIfNotEmpty(settledContractSectionObj,"GR-SettledContract", settledContractArray);
  }

  private void populateGRSettledContract2(JSONObject settledContractObj, String codiceEform, String codgar, Long ncont) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray sc2Array = new JSONArray();
    JSONObject sc2Obj = new JSONObject();

    // "BT-6110-Contract"
    String bt6110 = "";
    addIfNotEmpty(sc2Obj, "BT-6110-Contract", bt6110);
    // "BT-722-Contract"
    String bt722 = "";
    addIfNotEmpty(sc2Obj, "BT-722-Contract", bt722);
    // "BT-5011-Contract"
    String bt5011 = "";
    addIfNotEmpty(sc2Obj, "BT-5011-Contract", bt5011);

    sc2Array.add(sc2Obj);
    putIfNotEmpty(settledContractObj,"GR-SettledContract-2", sc2Array);
  }

  private void populateGRSettledContract1(JSONObject settledContractObj, String codiceEform, String codgar, Long ncont, EntityManager entityManager) throws Exception {

    List<?> results = getDatiWithParams(PathJsonEform.GR_SETTLEDCONTRACT_1, codgar, ncont);

    //JSONArray sc1Array = new JSONArray();
    for(Object result : results){

      Date bt1451 = (Date) SqlManager.getValueFromVectorParam(result, 0).getValue();
      Date bt145 = (Date) SqlManager.getValueFromVectorParam(result, 1).getValue();
      String bt150 = SqlManager.getValueFromVectorParam(result, 2).stringValue();
      String accqua = SqlManager.getValueFromVectorParam(result, 3).stringValue();
      
      String chiave = codgar+""+ncont.toString();
      String opt316 = entityManager.getOrCreateValue(PREFISSO_CON, "contratto", chiave, Boolean.TRUE);
      Boolean bt768 = null;
      if(accqua != null) {
        //bt768 = Boolean.FALSE;
        if("1".equals(accqua)) {
          bt768 = Boolean.TRUE;  
        }
      }
      
      String bt721 = "";
      String bt151 = "";
      String opt100 = "";

      JSONObject sc1Obj = new JSONObject();

      addIfNotEmpty(sc1Obj, "OPT-316-Contract", opt316);

      // GR-Contract-Signatory
      populateGRContractSignatory(sc1Obj, codiceEform, codgar, ncont);

      // GR-SettledContractTenderReference
      populateGRSettledContractTenderReference(sc1Obj, codiceEform, codgar, ncont, entityManager);

      addIfNotEmpty(sc1Obj, "BT-150-Contract", bt150);      
      addIfNotEmpty(sc1Obj, "BT-1451-Contract", bt1451);
      addIfNotEmpty(sc1Obj, "BT-721-Contract", bt721);
      addIfNotEmpty(sc1Obj, "BT-151-Contract", bt151);
      addIfNotEmpty(sc1Obj, "BT-145-Contract", bt145);
      addIfNotEmpty(sc1Obj, "OPT-100-Contract", opt100);
      addIfNotEmpty(sc1Obj, "BT-768-Contract", bt768);

      //sc1Array.add(sc1Obj);
      putIfNotEmpty(settledContractObj,"GR-SettledContract-1", sc1Obj);

    }
  }

  private void populateGRContractSignatory(JSONObject sc1Obj, String codiceEform, String codgar, Long ncont) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray signatoryArray = new JSONArray();
    JSONObject signatoryObj = new JSONObject();

    // "OPT-300-Contract-Signatory"
    String opt300Sign = "";
    addIfNotEmpty(signatoryObj, "OPT-300-Contract-Signatory", opt300Sign);

    signatoryArray.add(signatoryObj);
    putIfNotEmpty(sc1Obj,"GR-Contract-Signatory", signatoryArray);
  }

  private void populateGRSettledContractTenderReference(JSONObject sc1Obj, String codiceEform, String codgar, Long ncont, EntityManager entityManager) throws Exception {

    List<?> listaLotti = getDatiWithParams(PathJsonEform.GET_LOTTI_CONTRATTO, codgar, ncont);

    JSONArray tenderRefArray = new JSONArray();
    for (int i = 0; i < listaLotti.size(); i++) {
      String ngara = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).stringValue();
      
      Long aqoper = getAqoperByNgara(ngara);
      List<?> results = new ArrayList<>();
      if(aqoper != null && Long.valueOf(2).equals(aqoper)) {
        results = getDatiLotti(ngara, PathJsonEform.GR_SETTLEDCONTRACTTENDERREFERENCE_ACCORDO_QUADRO, Long.valueOf(0));
      } else {
        results = getDatiLotti(ngara, PathJsonEform.GR_SETTLEDCONTRACTTENDERREFERENCE, Long.valueOf(0));
      }

      for(Object result : results){

        String dittao = SqlManager.getValueFromVectorParam(result, 0).stringValue();

        String chiave = getCodgarByNgara(ngara)+""+dittao+""+ngara;
        String bt3202 = entityManager.getOrCreateValue(PREFISSO_TEN, "offerta", chiave, Boolean.TRUE);

        JSONObject tenderRefObj = new JSONObject();

        addIfNotEmpty(tenderRefObj, "BT-3202-Contract", bt3202);

        tenderRefArray.add(tenderRefObj);
      }
    }
    putIfNotEmpty(sc1Obj,"GR-SettledContractTenderReference", tenderRefArray);
  }



  /**
   * Popola l'intera sezione "GR-Part" all'interno del JSON parent.
   * "GR-Part" e' un array di cui popoleremo la singola entry,
   * che contiene:
   *   - GR-Part-SubmissionInfo
   *   - GR-Part-TenderingTerms
   *   - GR-Part-Purpose
   *   - GR-Part-Review
   *
   * @param parent      JSONObject in cui inserire la sezione GR-Part
   * @param codiceEform Parametro in firma, non usato
   * @return            Lo stesso JSONObject, arricchito di "GR-Part"
   */
  public JSONObject populateGRPart(JSONObject parent, String codiceEform, String uuidEform, EntityManager entityManager) throws Exception {
    // "GR-Part" e' un array
    JSONArray grPartArray = new JSONArray();
    List<?> listaLotti = getDati(uuidEform, PathJsonEform.GET_LOTTI, null);
    
    for (int i = 0; i < listaLotti.size(); i++) {
      String ngara = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).stringValue();
      
      JSONObject grPartObj = new JSONObject();

      // 1) GR-Part-SubmissionInfo
      populateGRPartSubmissionInfo(grPartObj, codiceEform, ngara);

      // 2) GR-Part-TenderingTerms
      populateGRPartTenderingTerms(grPartObj, codiceEform, ngara);

      // 3) GR-Part-Purpose
      populateGRPartPurpose(grPartObj, codiceEform, ngara);

      // 4) GR-Part-Review
      populateGRPartReview(grPartObj, codiceEform, ngara, entityManager);

      // Aggiungiamo grPartObj all'array
      grPartArray.add(grPartObj);

      // Inseriamo l'array nel parent
      putIfNotEmpty(parent,"GR-Part", grPartArray);
    }
    return parent;
  }

  // =========================================================================
  //                       GR-Part-SubmissionInfo
  // =========================================================================
  private void populateGRPartSubmissionInfo(JSONObject grPartObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-Part-SubmissionInfo" e' un array
    JSONArray subInfoArray = new JSONArray();
    JSONObject subInfoObj = new JSONObject();

    // 1) GR-Part-ProcurementDocuments
    populateGRPartProcurementDocuments(subInfoObj, codiceEform, uuidEform);

    // 2) GR-Part-SubmissionTool
    populateGRPartSubmissionTool(subInfoObj, codiceEform, uuidEform);

    // 3) GR-Part-AddInfo
    populateGRPartAddInfo(subInfoObj, codiceEform, uuidEform);

    // 4) GR-Part-DocProvider
    populateGRPartDocProvider(subInfoObj, codiceEform, uuidEform);

    // 5) GR-Part-Deadlines
    populateGRPartDeadlines(subInfoObj, codiceEform, uuidEform);

    subInfoArray.add(subInfoObj);
    putIfNotEmpty(grPartObj,"GR-Part-SubmissionInfo", subInfoArray);
  }

  // -----------------------------------------------------
  // GR-Part-ProcurementDocuments
  // -----------------------------------------------------
  private void populateGRPartProcurementDocuments(JSONObject subInfoObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray procDocsArray = new JSONArray();
    JSONObject procDocsObj = new JSONObject();

    // "BT-707-Part"
    String bt707 = "";
    addIfNotEmpty(procDocsObj, "BT-707-Part", bt707);

    // GR-Part-DocsAllOfficialLanguages
    populateGRPartDocsAllOfficialLanguages(procDocsObj, codiceEform, uuidEform);

    // "BT-14-Part"
    String bt14 = "";
    addIfNotEmpty(procDocsObj, "BT-14-Part", bt14);
    // "BT-615-Part"
    String bt615 = "";
    addIfNotEmpty(procDocsObj, "BT-615-Part", bt615);
    // "BT-15-Part"
    String bt15 = "";
    addIfNotEmpty(procDocsObj, "BT-15-Part", bt15);

    // GR-Part-DocsAllNonOfficialLanguages
    populateGRPartDocsAllNonOfficialLanguages(procDocsObj, codiceEform, uuidEform);

    // "OPT-140-Part"
    String opt140 = "";
    addIfNotEmpty(procDocsObj, "OPT-140-Part", opt140);

    procDocsArray.add(procDocsObj);
    putIfNotEmpty(subInfoObj,"GR-Part-ProcurementDocuments", procDocsArray);
  }

  private void populateGRPartDocsAllOfficialLanguages(JSONObject procDocsObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray allOffLangArray = new JSONArray();
    JSONObject allOffLangObj = new JSONObject();

    // "GR-Part-DocsOfficialLanguage"
    JSONArray docsOffLangArray = new JSONArray();
    JSONObject docsOffLangObj = new JSONObject();

    // "BT-708-Part"
    String bt708 = "";
    addIfNotEmpty(docsOffLangObj, "BT-708-Part", bt708);

    docsOffLangArray.add(docsOffLangObj);
    putIfNotEmpty(allOffLangObj,"GR-Part-DocsOfficialLanguage", docsOffLangArray);

    allOffLangArray.add(allOffLangObj);
    putIfNotEmpty(procDocsObj,"GR-Part-DocsAllOfficialLanguages", allOffLangArray);
  }

  private void populateGRPartDocsAllNonOfficialLanguages(JSONObject procDocsObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray allNonOffLangArray = new JSONArray();
    JSONObject allNonOffLangObj = new JSONObject();

    // "GR-Part-DocsNonOfficialLanguage"
    JSONArray docsNonOffLangArray = new JSONArray();
    JSONObject docsNonOffLangObj = new JSONObject();

    // "BT-737-Part"
    String bt737 = "";
    addIfNotEmpty(docsNonOffLangObj, "BT-737-Part", bt737);

    docsNonOffLangArray.add(docsNonOffLangObj);
    putIfNotEmpty(allNonOffLangObj,"GR-Part-DocsNonOfficialLanguage", docsNonOffLangArray);

    allNonOffLangArray.add(allNonOffLangObj);
    putIfNotEmpty(procDocsObj,"GR-Part-DocsAllNonOfficialLanguages", allNonOffLangArray);
  }

  private void populateGRPartSubmissionTool(JSONObject subInfoObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray subToolArray = new JSONArray();
    JSONObject subToolObj = new JSONObject();

    // "BT-632-Part"
    String bt632 = "";
    addIfNotEmpty(subToolObj, "BT-632-Part", bt632);
    // "BT-124-Part"
    String bt124 = "";
    addIfNotEmpty(subToolObj, "BT-124-Part", bt124);

    subToolArray.add(subToolObj);
    putIfNotEmpty(subInfoObj,"GR-Part-SubmissionTool", subToolArray);
  }

  private void populateGRPartAddInfo(JSONObject subInfoObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray addInfoArray = new JSONArray();
    JSONObject addInfoObj = new JSONObject();

    // "OPT-301-Part-AddInfo"
    String opt301AddInfo = "";
    addIfNotEmpty(addInfoObj, "OPT-301-Part-AddInfo", opt301AddInfo);

    addInfoArray.add(addInfoObj);
    putIfNotEmpty(subInfoObj,"GR-Part-AddInfo", addInfoArray);
  }

  private void populateGRPartDocProvider(JSONObject subInfoObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray docProviderArray = new JSONArray();
    JSONObject docProviderObj = new JSONObject();

    // "OPT-301-Part-DocProvider"
    String opt301DocProv = "";
    addIfNotEmpty(docProviderObj, "OPT-301-Part-DocProvider", opt301DocProv);

    docProviderArray.add(docProviderObj);
    putIfNotEmpty(subInfoObj,"GR-Part-DocProvider", docProviderArray);
  }

  private void populateGRPartDeadlines(JSONObject subInfoObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray deadlinesArray = new JSONArray();
    JSONObject dlObj = new JSONObject();

    // "BT-13(d)-Part"
    String bt13d = "";
    addIfNotEmpty(dlObj, "BT-13(d)-Part", bt13d);
    // "BT-13(t)-Part"
    String bt13t = "";
    addIfNotEmpty(dlObj, "BT-13(t)-Part", bt13t);

    deadlinesArray.add(dlObj);
    putIfNotEmpty(subInfoObj,"GR-Part-Deadlines", deadlinesArray);
  }

  // =========================================================================
  //                       GR-Part-TenderingTerms
  // =========================================================================
  private void populateGRPartTenderingTerms(JSONObject grPartObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray tenderingArray = new JSONArray();
    JSONObject tenderingObj = new JSONObject();

    // GR-Part-ContractExecutionRequirements
    populateGRPartContractExecutionRequirements(tenderingObj, codiceEform, uuidEform);

    // GR-Part-TendererQualification
    populateGRPartTendererQualification(tenderingObj, codiceEform, uuidEform);

    tenderingArray.add(tenderingObj);
    putIfNotEmpty(grPartObj,"GR-Part-TenderingTerms", tenderingArray);
  }

  private void populateGRPartContractExecutionRequirements(JSONObject tenderingObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray contractExecReqArr = new JSONArray();
    JSONObject contractExecReqObj = new JSONObject();

    // "BT-736-Part"
    String bt736 = "";
    addIfNotEmpty(contractExecReqObj, "BT-736-Part", bt736);

    contractExecReqArr.add(contractExecReqObj);
    putIfNotEmpty(tenderingObj,"GR-Part-ContractExecutionRequirements", contractExecReqArr);
  }

  private void populateGRPartTendererQualification(JSONObject tenderingObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray tenderQualArr = new JSONArray();
    JSONObject tenderQualObj = new JSONObject();

    // "BT-71-Part"
    String bt71 = "";
    addIfNotEmpty(tenderQualObj, "BT-71-Part", bt71);

    tenderQualArr.add(tenderQualObj);
    putIfNotEmpty(tenderingObj,"GR-Part-TendererQualification", tenderQualArr);
  }

  // =========================================================================
  //                         GR-Part-Purpose
  // =========================================================================
  private void populateGRPartPurpose(JSONObject grPartObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-Part-Purpose" e' un array
    JSONArray purposeArray = new JSONArray();
    JSONObject purposeObj = new JSONObject();

    // GR-Part-EmployLegis
    populateGRPartEmployLegis(purposeObj, codiceEform, ngara);

    // GR-Part-PlannedDuration
    populateGRPartPlannedDuration(purposeObj, codiceEform, ngara);

    // GR-Part-Description
    populateGRPartDescription(purposeObj, codiceEform, ngara);

    // GR-Part-Scope
    populateGRPartScope(purposeObj, codiceEform, ngara);

    // GR-Part-EnvironLegis
    populateGRPartEnvironLegis(purposeObj, codiceEform, ngara);

    // GR-Part-AdditionalInformation
    populateGRPartAdditionalInformation(purposeObj, codiceEform, ngara);

    // "BT-137-Part"
    String bt137 = "";
    addIfNotEmpty(purposeObj, "BT-137-Part", bt137);

    // GR-Part-FiscalLegis
    populateGRPartFiscalLegis(purposeObj, codiceEform, ngara);

    // GR-Part-PlaceOfPerformance
    populateGRPartPlaceOfPerformance(purposeObj, codiceEform, ngara);

    // GR-Part-PreviousPlanning
    populateGRPartPreviousPlanning(purposeObj, codiceEform, ngara);

    // GR-Part-TenderEval
    populateGRPartTenderEval(purposeObj, codiceEform, ngara);

    // GR-Part-TenderReceipt
    populateGRPartTenderReceipt(purposeObj, codiceEform, ngara);

    // GR-Part-GpaAgreement
    populateGRPartGpaAgreement(purposeObj, codiceEform, ngara);

    // GR-Part-ContractingSystem
    populateGRPartContractingSystem(purposeObj, codiceEform, ngara);

    purposeArray.add(purposeObj);
    putIfNotEmpty(grPartObj,"GR-Part-Purpose", purposeArray);
  }

  // -----------------------------------------------------
  // GR-Part-EmployLegis
  // -----------------------------------------------------
  private void populateGRPartEmployLegis(JSONObject purposeObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray employLegisArr = new JSONArray();
    JSONObject employLegisObj = new JSONObject();

    // "OPT-301-Part-EmployLegis"
    String opt301Employ = "";
    addIfNotEmpty(employLegisObj, "OPT-301-Part-EmployLegis", opt301Employ);
    // "OPT-130-Part-EmployLegis"
    String opt130Employ = "";
    addIfNotEmpty(employLegisObj, "OPT-130-Part-EmployLegis", opt130Employ);
    // "OPT-113-Part-EmployLegis"
    String opt113Employ = "";
    addIfNotEmpty(employLegisObj, "OPT-113-Part-EmployLegis", opt113Employ);

    employLegisArr.add(employLegisObj);
    putIfNotEmpty(purposeObj,"GR-Part-EmployLegis", employLegisArr);
  }

  // -----------------------------------------------------
  // GR-Part-PlannedDuration
  // -----------------------------------------------------
  private void populateGRPartPlannedDuration(JSONObject purposeObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(resulxt, 1).stringValue();
    }

    JSONArray plannedDurArr = new JSONArray();
    JSONObject durObj = new JSONObject();

    // "BT-538-Part"
    String bt538 = "";
    addIfNotEmpty(durObj, "BT-538-Part", bt538);
    // "BT-36-Part"
    String bt36 = "";
    addIfNotEmpty(durObj, "BT-36-Part", bt36);
    // "BT-536-Part"
    String bt536 = "";
    addIfNotEmpty(durObj, "BT-536-Part", bt536);
    // "BT-537-Part"
    String bt537 = "";
    addIfNotEmpty(durObj, "BT-537-Part", bt537);

    plannedDurArr.add(durObj);
    putIfNotEmpty(purposeObj,"GR-Part-PlannedDuration", plannedDurArr);
  }

  // -----------------------------------------------------
  // GR-Part-Description
  // -----------------------------------------------------
  private void populateGRPartDescription(JSONObject purposeObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_PART_DESCRIPTION, Long.valueOf(11));

    //JSONArray descrArr = new JSONArray();
    for(Object result : results){

      String bt21 = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      Long bt23 = SqlManager.getValueFromVectorParam(result, 1).longValue();
      String bt24 = bt21; //uguale al 21
      String bt22 = "";
      
      JSONObject descrObj = new JSONObject();

      addIfNotEmpty(descrObj, "BT-23-Part", getValoreByTip(bt23, Long.valueOf(11)));
      // GR-Part-Additional-Nature
      populateGRPartAdditionalNature(descrObj, codiceEform, ngara);

      addIfNotEmpty(descrObj, "BT-22-Part", bt22);
      addIfNotEmpty(descrObj, "BT-21-Part", bt21);
      addIfNotEmpty(descrObj, "BT-24-Part", bt24);

      //descrArr.add(descrObj);
      putIfNotEmpty(purposeObj,"GR-Part-Description", descrObj);
    }
  }

  private void populateGRPartAdditionalNature(JSONObject descrObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray addNatureArr = new JSONArray();
    JSONObject addNatureObj = new JSONObject();

    // "BT-531-Part"
    String bt531 = "";
    addIfNotEmpty(addNatureObj, "BT-531-Part", bt531);

    addNatureArr.add(addNatureObj);
    putIfNotEmpty(descrObj,"GR-Part-Additional-Nature", addNatureArr);
  }

  // -----------------------------------------------------
  // GR-Part-Scope
  // -----------------------------------------------------
  private void populateGRPartScope(JSONObject purposeObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_PART_SCOPE, Long.valueOf(11));

    //JSONArray scopeArr = new JSONArray();
    for(Object result : results){


      Double bt27 = SqlManager.getValueFromVectorParam(result, 0).doubleValue();
      String bt726 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();

      JSONObject scopeObj = new JSONObject();

      addIfNotEmpty(scopeObj, "BT-726-Part", bt726);
      // GR-Part-Scope-MainClassification
      populateGRPartScopeMainClassification(scopeObj, codiceEform, ngara);

      // GR-Part-Scope-AdditionalClassification
      populateGRPartScopeAdditionalClassification(scopeObj, codiceEform, ngara);

      addIfNotEmpty(scopeObj, "BT-27-Part", bt27);
      if(bt27 != null) {
        addIfNotEmpty(scopeObj, "BT-27-Part-Currency", "EUR");
      }

      //scopeArr.add(scopeObj);
      putIfNotEmpty(purposeObj,"GR-Part-Scope", scopeObj);
    }
  }

  private void populateGRPartScopeMainClassification(JSONObject scopeObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_PART_SCOPE_MAINCLASSIFICATION, Long.valueOf(11));

    for(Object result : results){

      String bt262 = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt26m = "";

      if(bt262 != null && bt262.contains("-")) {
        bt262 = bt262.split("-")[0];
        bt26m = "cpv";
      }

      JSONObject mainClassObj = new JSONObject();

      addIfNotEmpty(mainClassObj, "BT-26(m)-Part", bt26m);
      addIfNotEmpty(mainClassObj, "BT-262-Part", bt262);

      putIfNotEmpty(scopeObj,"GR-Part-Scope-MainClassification", mainClassObj);
    }
  }

  private void populateGRPartScopeAdditionalClassification(JSONObject scopeObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_PART_SCOPE_ADDITIONALCLASSIFICATION, Long.valueOf(11));

    JSONArray addClassArray = new JSONArray();
    for(Object result : results){

      String bt263 = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt26a = "cpv";
      
      if(bt263 != null && bt263.contains("-")) {
        bt263 = bt263.split("-")[0];
      }

      JSONObject addClassObj = new JSONObject();

      addIfNotEmpty(addClassObj, "BT-26(a)-Part", bt26a);
      addIfNotEmpty(addClassObj, "BT-263-Part", bt263);

      addClassArray.add(addClassObj);
    }
    putIfNotEmpty(scopeObj,"GR-Part-Scope-AdditionalClassification", addClassArray);
  }

  // -----------------------------------------------------
  // GR-Part-EnvironLegis
  // -----------------------------------------------------
  private void populateGRPartEnvironLegis(JSONObject purposeObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray environArr = new JSONArray();
    JSONObject environObj = new JSONObject();

    // "OPT-112-Part-EnvironLegis"
    String opt112 = "";
    addIfNotEmpty(environObj, "OPT-112-Part-EnvironLegis", opt112);
    // "OPT-120-Part-EnvironLegis"
    String opt120 = "";
    addIfNotEmpty(environObj, "OPT-120-Part-EnvironLegis", opt120);
    // "OPT-301-Part-EnvironLegis"
    String opt301 = "";
    addIfNotEmpty(environObj, "OPT-301-Part-EnvironLegis", opt301);

    environArr.add(environObj);
    putIfNotEmpty(purposeObj,"GR-Part-EnvironLegis", environArr);
  }

  // -----------------------------------------------------
  // GR-Part-AdditionalInformation
  // -----------------------------------------------------
  private void populateGRPartAdditionalInformation(JSONObject purposeObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray addInfoArr = new JSONArray();
    JSONObject addInfoObj = new JSONObject();

    // "BT-300-Part"
    String bt300 = "";
    addIfNotEmpty(addInfoObj, "BT-300-Part", bt300);

    addInfoArr.add(addInfoObj);
    putIfNotEmpty(purposeObj,"GR-Part-AdditionalInformation", addInfoArr);
  }

  // -----------------------------------------------------
  // GR-Part-FiscalLegis
  // -----------------------------------------------------
  private void populateGRPartFiscalLegis(JSONObject purposeObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray fiscalArr = new JSONArray();
    JSONObject fiscalObj = new JSONObject();

    // "OPT-111-Part-FiscalLegis"
    String opt111 = "";
    addIfNotEmpty(fiscalObj, "OPT-111-Part-FiscalLegis", opt111);
    // "OPT-110-Part-FiscalLegis"
    String opt110 = "";
    addIfNotEmpty(fiscalObj, "OPT-110-Part-FiscalLegis", opt110);
    // "OPT-301-Part-FiscalLegis"
    String opt301 = "";
    addIfNotEmpty(fiscalObj, "OPT-301-Part-FiscalLegis", opt301);

    fiscalArr.add(fiscalObj);
    putIfNotEmpty(purposeObj,"GR-Part-FiscalLegis", fiscalArr);
  }

  // -----------------------------------------------------
  // GR-Part-PlaceOfPerformance
  // -----------------------------------------------------
  private void populateGRPartPlaceOfPerformance(JSONObject purposeObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_PART_PLACEOFPERFORMANCE, Long.valueOf(11));

    //JSONArray placePerfArr = new JSONArray();
    for(Object result : results){

      String bt5071 = SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt5101c = "";
      String bt728 = "";
      String bt5121 = "";
      String bt5131 = "";
      String bt5101a = "";
      String bt727 = "";
      String bt5101b = "";
      String bt5141 = "ITA";

      JSONObject placePerfObj = new JSONObject();

      addIfNotEmpty(placePerfObj, "BT-5101(c)-Part", bt5101c);
      addIfNotEmpty(placePerfObj, "BT-728-Part", bt728);
      addIfNotEmpty(placePerfObj, "BT-5121-Part", bt5121);
      addIfNotEmpty(placePerfObj, "BT-5131-Part", bt5131);
      addIfNotEmpty(placePerfObj, "BT-5101(a)-Part", bt5101a);
      addIfNotEmpty(placePerfObj, "BT-727-Part", bt727);
      if (bt5071 != null && bt5071.length() == 5) {
        addIfNotEmpty(placePerfObj, "BT-5071-Part", bt5071);
      }
      addIfNotEmpty(placePerfObj, "BT-5101(b)-Part", bt5101b);
      addIfNotEmpty(placePerfObj, "BT-5141-Part", bt5141);

      //placePerfArr.add(placePerfObj);
      putIfNotEmpty(purposeObj,"GR-Part-PlaceOfPerformance", placePerfObj);
    }
  }

  // -----------------------------------------------------
  // GR-Part-PreviousPlanning
  // -----------------------------------------------------
  private void populateGRPartPreviousPlanning(JSONObject purposeObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray prevPlanArr = new JSONArray();
    JSONObject prevPlanObj = new JSONObject();

    // "BT-125(i)-Part"
    String bt125i = "";
    addIfNotEmpty(prevPlanObj, "BT-125(i)-Part", bt125i);
    // "BT-1251-Part"
    String bt1251 = "";
    addIfNotEmpty(prevPlanObj, "BT-1251-Part", bt1251);

    prevPlanArr.add(prevPlanObj);
    putIfNotEmpty(purposeObj,"GR-Part-PreviousPlanning", prevPlanArr);
  }

  // -----------------------------------------------------
  // GR-Part-TenderEval
  // -----------------------------------------------------
  private void populateGRPartTenderEval(JSONObject purposeObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray tenderEvalArr = new JSONArray();
    JSONObject tenderEvalObj = new JSONObject();

    // "OPT-301-Part-TenderEval"
    String opt301Eval = "";
    addIfNotEmpty(tenderEvalObj, "OPT-301-Part-TenderEval", opt301Eval);

    tenderEvalArr.add(tenderEvalObj);
    putIfNotEmpty(purposeObj,"GR-Part-TenderEval", tenderEvalArr);
  }

  // -----------------------------------------------------
  // GR-Part-TenderReceipt
  // -----------------------------------------------------
  private void populateGRPartTenderReceipt(JSONObject purposeObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray tenderReceiptArr = new JSONArray();
    JSONObject tenderReceiptObj = new JSONObject();

    // "OPT-301-Part-TenderReceipt"
    String opt301Receipt = "";
    addIfNotEmpty(tenderReceiptObj, "OPT-301-Part-TenderReceipt", opt301Receipt);

    tenderReceiptArr.add(tenderReceiptObj);
    putIfNotEmpty(purposeObj,"GR-Part-TenderReceipt", tenderReceiptArr);
  }

  // -----------------------------------------------------
  // GR-Part-GpaAgreement
  // -----------------------------------------------------
  private void populateGRPartGpaAgreement(JSONObject purposeObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray gpaArr = new JSONArray();
    JSONObject gpaObj = new JSONObject();

    // "BT-115-Part"
    String bt115 = "";
    addIfNotEmpty(gpaObj, "BT-115-Part", bt115);

    gpaArr.add(gpaObj);
    putIfNotEmpty(purposeObj,"GR-Part-GpaAgreement", gpaArr);
  }

  // -----------------------------------------------------
  // GR-Part-ContractingSystem
  // -----------------------------------------------------
  private void populateGRPartContractingSystem(JSONObject purposeObj, String codiceEform, String ngara) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray contractingSystemArr = new JSONArray();
    JSONObject csObj = new JSONObject();

    // GR-Part-FrameworkAgreement
    populateGRPartFrameworkAgreement(csObj, codiceEform, ngara);
    // GR-Part-DPS
    populateGRPartDPS(csObj, codiceEform, ngara);

    contractingSystemArr.add(csObj);
    putIfNotEmpty(purposeObj,"GR-Part-ContractingSystem", contractingSystemArr);
  }

  private void populateGRPartFrameworkAgreement(JSONObject csObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_PART_FRAMEWORKAGREEMENT, Long.valueOf(11));

    //JSONArray fwArr = new JSONArray();
    for(Object result : results){

      String accqua = SqlManager.getValueFromVectorParam(result, 0).stringValue();

      // "BT-765-Lot"
      String bt765 = "none";
      if(accqua != null && "1".equals(accqua)) {
        bt765 = "fa-mix";
      }

      JSONObject fwObj = new JSONObject();

      addIfNotEmpty(fwObj, "BT-765-Part", bt765);

      //fwArr.add(fwObj);
      putIfNotEmpty(csObj,"GR-Part-FrameworkAgreement", fwObj);
    }
  }

  private void populateGRPartDPS(JSONObject csObj, String codiceEform, String ngara) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_PART_DPS, Long.valueOf(11));

    for(Object result : results){

      String issda = SqlManager.getValueFromVectorParam(result, 0).stringValue();

      JSONObject dpsObj = new JSONObject();

      // "BT-766-Part"
      String bt766 = "none";
      if(issda != null && !issda.isEmpty()) {
        bt766 = getTab2d1ByTab2tip(issda, "A1z32");
      }
      addIfNotEmpty(dpsObj, "BT-766-Part", bt766);

      putIfNotEmpty(csObj,"GR-Part-DPS", dpsObj);
    }
  }

  // =========================================================================
  //                         GR-Part-Review
  // =========================================================================
  private void populateGRPartReview(JSONObject grPartObj, String codiceEform, String ngara, EntityManager entityManager) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    // "GR-Part-Review" e' un array
    JSONArray reviewArr = new JSONArray();
    JSONObject reviewObj = new JSONObject();

    // GR-Part-Mediator
    populateGRPartMediator(reviewObj, codiceEform, ngara);

    // GR-Part-ReviewOrg
    populateGRPartReviewOrg(reviewObj, codiceEform, ngara, entityManager);

    // GR-Part-ReviewInfo
    populateGRPartReviewInfo(reviewObj, codiceEform, ngara);

    reviewArr.add(reviewObj);
    putIfNotEmpty(grPartObj,"GR-Part-Review", reviewArr);
  }

  private void populateGRPartMediator(JSONObject reviewObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray mediatorArr = new JSONArray();
    JSONObject mediatorObj = new JSONObject();

    // "OPT-301-Part-Mediator"
    String opt301Mediator = "";
    addIfNotEmpty(mediatorObj, "OPT-301-Part-Mediator", opt301Mediator);

    mediatorArr.add(mediatorObj);
    putIfNotEmpty(reviewObj,"GR-Part-Mediator", mediatorArr);
  }

  private void populateGRPartReviewOrg(JSONObject reviewObj, String codiceEform, String ngara, EntityManager entityManager) throws Exception {

    List<?> results = getDatiLotti(ngara, PathJsonEform.GR_PART_REVIEWORG, Long.valueOf(11));

    for(Object result : results){

      Long numpun = SqlManager.getValueFromVectorParam(result, 0).longValue();
      String codein = SqlManager.getValueFromVectorParam(result, 1).stringValue();

      String entita = "punticon";
      String chiave = String.valueOf(numpun)+";"+codein;
      String opt301ReviewOrg = entityManager.getOrCreateValue(PREFISSO_ORG, entita, chiave, Boolean.FALSE);
      
      JSONObject reviewOrgObj = new JSONObject();

      addIfNotEmpty(reviewOrgObj, "OPT-301-Part-ReviewOrg", opt301ReviewOrg);

      putIfNotEmpty(reviewObj,"GR-Part-ReviewOrg", reviewOrgObj);
    }
  }

  private void populateGRPartReviewInfo(JSONObject reviewObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray reviewInfoArr = new JSONArray();
    JSONObject reviewInfoObj = new JSONObject();

    // "OPT-301-Part-ReviewInfo"
    String opt301ReviewInfo = "";
    addIfNotEmpty(reviewInfoObj, "OPT-301-Part-ReviewInfo", opt301ReviewInfo);

    reviewInfoArr.add(reviewInfoObj);
    putIfNotEmpty(reviewObj,"GR-Part-ReviewInfo", reviewInfoArr);
  }



  /**
   * Popola la sezione "GR-Change" all'interno del JSON parent.
   * La struttura (dal tuo JSON) e':
   *
   * "GR-Change" : [ {
   *   "BT-140-notice" : "",
   *   "GR-ChangeSections" : [ {
   *     "GR-ChangedSectionIdentifiers" : [ {
   *       "BT-13716-notice" : ""
   *     } ],
   *     "BT-718-notice" : "",
   *     "BT-141(a)-notice" : "",
   *     "BT-719-notice" : ""
   *   } ],
   *   "BT-762-notice" : "",
   *   "BT-758-notice" : ""
   * } ]
   *
   * @param parent      JSONObject in cui inserire GR-Change
   * @param codiceEform Parametro richiesto, ma NON usato
   * @return            Lo stesso JSONObject arricchito di "GR-Change"
   */
  public JSONObject populateGRChange(JSONObject parent, String codiceEform, String uuidEform) throws Exception {
    // "GR-Change" e' un array
    JSONArray grChangeArray = new JSONArray();
    JSONObject grChangeObj = new JSONObject();

    // "BT-140-notice"
    String bt140 = "";
    addIfNotEmpty(grChangeObj, "BT-140-notice", bt140);

    // GR-ChangeSections
    populateGRChangeSections(grChangeObj, codiceEform, uuidEform);

    // "BT-762-notice"
    String bt762 = "";
    addIfNotEmpty(grChangeObj, "BT-762-notice", bt762);

    // "BT-758-notice"
    String bt758 = "";
    addIfNotEmpty(grChangeObj, "BT-758-notice", bt758);

    // Aggiungiamo l'oggetto all'array
    grChangeArray.add(grChangeObj);

    // Mettiamo l'array nel parent
    putIfNotEmpty(parent,"GR-Change", grChangeArray);

    return parent;
  }

  /**
   * Popola la sezione "GR-ChangeSections" dentro l'oggetto "GR-Change".
   */
  private void populateGRChangeSections(JSONObject grChangeObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray changeSectionsArray = new JSONArray();
    JSONObject changeSectionsObj = new JSONObject();

    // GR-ChangedSectionIdentifiers
    populateGRChangedSectionIdentifiers(changeSectionsObj, codiceEform, uuidEform);

    // "BT-718-notice"
    String bt718 = "";
    addIfNotEmpty(changeSectionsObj, "BT-718-notice", bt718);

    // "BT-141(a)-notice"
    String bt141a = "";
    addIfNotEmpty(changeSectionsObj, "BT-141(a)-notice", bt141a);

    // "BT-719-notice"
    String bt719 = "";
    addIfNotEmpty(changeSectionsObj, "BT-719-notice", bt719);

    changeSectionsArray.add(changeSectionsObj);
    putIfNotEmpty(grChangeObj,"GR-ChangeSections", changeSectionsArray);
  }

  /**
   * Popola la sezione "GR-ChangedSectionIdentifiers".
   */
  private void populateGRChangedSectionIdentifiers(JSONObject changeSectionsObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray changedSecIdArray = new JSONArray();
    JSONObject changedSecIdObj = new JSONObject();

    // "BT-13716-notice"
    String bt13716 = "";
    addIfNotEmpty(changedSecIdObj, "BT-13716-notice", bt13716);

    changedSecIdArray.add(changedSecIdObj);
    putIfNotEmpty(changeSectionsObj,"GR-ChangedSectionIdentifiers", changedSecIdArray);
  }


  /**
   * Popola la macro-sezione "GR-Modification" all'interno di parent.
   *
   * Dal tuo JSON, la struttura e':
   * "GR-Modification" : [ {
   *   "GR-ContractModification" : [ {
   *     "GR-ContractModification-Modification" : [ {
   *       "BT-202-Contract" : "",
   *       "GR-ModificationSectionsIdentifers" : [ {
   *         "BT-1501(p)-Contract" : ""
   *       } ]
   *     } ],
   *     "BT-1501(n)-Contract" : "",
   *     "BT-1501(c)-Contract" : "",
   *     "GR-ContractModification-ModificationReason" : [ {
   *       "BT-200-Contract" : "",
   *       "BT-201-Contract" : ""
   *     } ]
   *   } ]
   * } ]
   *
   * @param parent      JSONObject in cui inserire GR-Modification
   * @param codiceEform Parametro obbligatorio, NON usato
   * @return            Lo stesso JSONObject con "GR-Modification"
   */
  public JSONObject populateGRModification(JSONObject parent, String codiceEform, String uuidEform) throws Exception {
    // "GR-Modification" e' un array
    JSONArray grModificationArray = new JSONArray();
    JSONObject grModificationObj = new JSONObject();

    // GR-ContractModification
    populateGRContractModification(grModificationObj, codiceEform, uuidEform);

    // Aggiungiamo l'oggetto all'array
    grModificationArray.add(grModificationObj);

    // Mettiamo l'array "GR-Modification" nel parent
    putIfNotEmpty(parent,"GR-Modification", grModificationArray);

    return parent;
  }

  // -----------------------------------------------------
  // GR-ContractModification
  // -----------------------------------------------------
  private void populateGRContractModification(JSONObject grModificationObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray contractModArray = new JSONArray();
    JSONObject contractModObj = new JSONObject();

    // 1) GR-ContractModification-Modification
    populateGRContractModificationModification(contractModObj, codiceEform, uuidEform);

    // "BT-1501(n)-Contract"
    String bt1501n = "";
    addIfNotEmpty(contractModObj, "BT-1501(n)-Contract", bt1501n);
    // "BT-1501(c)-Contract"
    String bt1501c = "";
    addIfNotEmpty(contractModObj, "BT-1501(c)-Contract", bt1501c);

    // 2) GR-ContractModification-ModificationReason
    populateGRContractModificationReason(contractModObj, codiceEform, uuidEform);

    contractModArray.add(contractModObj);
    putIfNotEmpty(grModificationObj,"GR-ContractModification", contractModArray);
  }

  /**
   * Sezione "GR-ContractModification-Modification", un array con oggetti
   * contenenti:
   *  - "BT-202-Contract"
   *  - "GR-ModificationSectionsIdentifers" (sottosezione)
   */
  private void populateGRContractModificationModification(JSONObject contractModObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray modArray = new JSONArray();
    JSONObject modObj = new JSONObject();

    // "BT-202-Contract"
    String bt202 = "";
    addIfNotEmpty(modObj, "BT-202-Contract", bt202);

    // GR-ModificationSectionsIdentifers
    populateGRModificationSectionsIdentifers(modObj, codiceEform, uuidEform);

    modArray.add(modObj);
    putIfNotEmpty(contractModObj,"GR-ContractModification-Modification", modArray);
  }

  private void populateGRModificationSectionsIdentifers(JSONObject modObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray sectIdArray = new JSONArray();
    JSONObject sectIdObj = new JSONObject();

    // "BT-1501(p)-Contract"
    String bt1501p = "";
    addIfNotEmpty(sectIdObj, "BT-1501(p)-Contract", bt1501p);

    sectIdArray.add(sectIdObj);
    putIfNotEmpty(modObj,"GR-ModificationSectionsIdentifers", sectIdArray);
  }

  private void populateGRContractModificationReason(JSONObject contractModObj, String codiceEform, String uuidEform) throws Exception {

    //Impostato ROOT momentaneamente, questo metodo non e' stato ancora completato
    List<?> results = new ArrayList<>();//getDati(uuidEform, PathJsonEform.ROOT, null);

    for(Object result : results){

      
      String bt21 = "";//SqlManager.getValueFromVectorParam(result, 0).stringValue();
      String bt24 = "";//SqlManager.getValueFromVectorParam(result, 1).stringValue();
    }

    JSONArray reasonArray = new JSONArray();
    JSONObject reasonObj = new JSONObject();

    // "BT-200-Contract"
    String bt200 = "";
    addIfNotEmpty(reasonObj, "BT-200-Contract", bt200);
    // "BT-201-Contract"
    String bt201 = "";
    addIfNotEmpty(reasonObj, "BT-201-Contract", bt201);

    reasonArray.add(reasonObj);
    putIfNotEmpty(contractModObj,"GR-ContractModification-ModificationReason", reasonArray);
  }




  //-----------------------------------------------------
  // Others
  // -----------------------------------------------------

  /**
   * Metodo di utilita': controlla se la stringa non e' null e non e' vuota.
   */
  public boolean isNotEmpty(String value) {
    return value != null && !value.trim().isEmpty();
  }


  public static void putIfNotEmpty(JSONObject parentObj, String childKey, Object child) {
    if (child == null) {
      return; // Non inserire nulla se e' null
    }

    if (child instanceof JSONObject) {
      putIfNonEmptyJSONObject(parentObj, childKey, (JSONObject) child);
    } else if (child instanceof JSONArray) {
      putIfNonEmptyJSONArray(parentObj, childKey, (JSONArray) child);
    } else {
      // Per altri tipi (String, Number, Boolean, ecc.)
      parentObj.put(childKey, child);
    }
  }

  private static void putIfNonEmptyJSONObject(JSONObject parentObj, String childKey, JSONObject jsonObj) {
    if (!jsonObj.isEmpty()) {
      parentObj.put(childKey, jsonObj);
    }
  }

  private static void putIfNonEmptyJSONArray(JSONObject parentObj, String childKey, JSONArray jsonArr) {
    if (jsonArr.isEmpty()) {
      return;
    }
    if (arrayContainsNonEmptyElement(jsonArr)) {
      parentObj.put(childKey, jsonArr);
    }
  }

  private static boolean arrayContainsNonEmptyElement(JSONArray jsonArr) {
    // Restituisce true solo se trova almeno un elemento che è un JSONObject non vuoto.
    for (Object element : jsonArr) {
      if (element instanceof JSONObject) {
        if (!((JSONObject) element).isEmpty()) {
          return true;
        }
      }
    }
    return false;
  }





  /**
   * metodo per ottenere codgar da uuidEform
   */
  public String getCodgarByUuid(String uuidEform) throws SQLException {
    return (String) this.sqlManager.getObject(
        "SELECT f.codgar " +
            "FROM G1NPASCHEDA s " +
            "INNER JOIN g1npaform f ON f.id = s.idform " +
            "WHERE s.uuid_eform = ?",
            new Object[] { uuidEform }
        );
  }
  
  /**
   * metodo per ottenere iterga da codgar
   */
  public Long getItergaByCodgar(String codgar) throws SQLException {
    return (Long) this.sqlManager.getObject(
        "SELECT iterga FROM torn WHERE codgar = ? ",
            new Object[] { codgar }
        );
  }
  
  /**
   * metodo per ottenere uuidEform da codgar
   */
  private String getUuidByCodgar(String codgar) throws SQLException {
    return (String) this.sqlManager.getObject(
        "SELECT s.uuid_eform " +
            "FROM G1NPASCHEDA s " +
            "INNER JOIN g1npaform f ON f.id = s.idform " +
            "WHERE f.codgar = ?",
            new Object[] { codgar }
        );
  }

  /**
   * Esempio di metodo per ottenere il genere da codgar
   */
  private Long getGenereByCodgar(String codgar) throws SQLException {
    return (Long) this.sqlManager.getObject(
        "select genere "
            + "from v_gare_genere "
            + "where codgar = ? "
            + "and genere in (1,2,3,10,11) ",
            new Object[] { codgar }
        );
  }

  public Long getGenereByNgara(String ngara) throws Exception {
    String genereSql = "select genere from v_gare_genere where codice = ? ";
    return (Long) this.sqlManager.getObject(genereSql, new Object[]{ngara});
  }
  
  public String getCodgarByNgara(String ngara) throws Exception {
    String codgarSql = "select codgar1 from gare where ngara = ? ";
    return (String) this.sqlManager.getObject(codgarSql, new Object[]{ngara});
  }
  /**
   * Esegue la query e ritorna i dati come lista di oggetti
   */
  private List<?> getDatiByQuery(String query, String codgar) throws Exception {
    try {
      List<?> listaDati = this.sqlManager.getListVector(query, new Object[] { codgar });
      return (listaDati != null) ? listaDati : new ArrayList<>();
    } catch (Exception e) {
      String errorMessage = String.format("Errore in fase di recupero dati. Query: %s, Parametri: %s", 
          query, codgar);
      throw new Exception(errorMessage, e);
    }
  }

  private List<?> getDatiByQueryParams(String query, Object... params) throws Exception {
    try {
      List<?> listaDati = this.sqlManager.getListVector(query, params);
      return (listaDati != null) ? listaDati : new ArrayList<>();
    } catch (Exception e) {
      String errorMessage = String.format("Errore in fase di recupero dati. Query: %s, Parametri: %s", 
          query, Arrays.toString(params));
      throw new Exception(errorMessage, e);
    }
  }


  /**
   * Metodo "unificato" che: 
   * 1) ricava codgar dall'uuidEform, 
   * 2) ricava il genere dal codgar, PS se il genere viene passato in input sara' uguale a zero, lo zero lo utilizziamo
   *                                 quando dobbiamo accedere a torn, evitando coss' di scrivere la stessa query n volte
   *                                 per ogni tipo di genere
   * 3) ricava la query dalla registry in base a (genere, code),
   * 4) esegue la query e restituisce i risultati.
   */
  public List<?> getDati(String uuidEform, PathJsonEform code, Long genere) throws Exception {
    // Passo 1: ottengo il codgar dall'uuidEform
    String codgar = getCodgarByUuid(uuidEform);
    if (codgar == null) {
      throw new IllegalArgumentException("Impossibile recuperare codgar dall'uuidEform: " + uuidEform);
    }

    // Passo 2: ottengo il genere dal codgar
    if(genere == null) {
      genere = getGenereByCodgar(codgar);
      if (genere == null) {
        throw new IllegalArgumentException("Impossibile recuperare genere dal codgar: " + codgar);
      }
      // classifico il genere
      genere = getClassificazioneByGenere(genere);
    }
    // Passo 3: recupero la query associata (genere, code)
    String finalQuery = QueryRegistry.getQuery(genere.intValue(), code);

    // Passo 4: eseguo la query e ritorno i dati
    return getDatiByQuery(finalQuery, codgar);
  }
  
  public List<?> getDatiCAN(String uuidEform, PathJsonEform code, Long genere) throws Exception {
    // Passo 1: ottengo il codgar dall'uuidEform
    String codgar = getCodgarByUuid(uuidEform);
    if (codgar == null) {
      throw new IllegalArgumentException("Impossibile recuperare codgar dall'uuidEform: " + uuidEform);
    }

    // Passo 2: ottengo il genere dal codgar
    if(genere == null) {
      genere = getGenereByCodgar(codgar);
      if (genere == null) {
        throw new IllegalArgumentException("Impossibile recuperare genere dal codgar: " + codgar);
      }
      // classifico il genere
      genere = getClassificazioneByGenere(genere);
    }
    // Passo 3: recupero la query associata (genere, code)
    String finalQuery = QueryRegistry.getQuery(genere.intValue(), code);

    // Passo 4: eseguo la query e ritorno i dati
    return getDatiByQueryParams(finalQuery, uuidEform, codgar);
  }



  public List<?> getDatiLotti(String ngara, PathJsonEform code, Long genere) throws Exception {

    // Passo 2: ottengo il genere da ngara
    if(genere == null) {
      genere = getGenereByNgara(ngara);
      if (genere == null) {
        throw new IllegalArgumentException("Impossibile recuperare genere da ngara: " + ngara);
      }
      // classifico il genere
      genere = getClassificazioneByGenere(genere);
    }
    // Passo 3: recupero la query associata (genere, code)
    String finalQuery = QueryRegistry.getQuery(genere.intValue(), code);

    // Passo 4: eseguo la query e ritorno i dati
    return getDatiByQuery(finalQuery, ngara);
  }

  /**
   * 
   * @param code
   * @param puntoContatto se 0 significa che devo andare sulla tabella uffint altrimenti su punticon
   * @param numpun
   * @param codein
   * @return
   * @throws Exception
   */
  private List<?> getDatiOrganization(PathJsonEform code, Long puntoContatto, Long numpun, String codein) throws Exception {

    List<?> dati = null;

    String finalQuery = QueryRegistry.getQuery(puntoContatto.intValue(), code);
    if((numpun == null || Long.valueOf(0).equals(numpun))&& Long.valueOf(0).equals(puntoContatto)) {
      dati = getDatiByQueryParams(finalQuery, codein);
    } else {
      dati = getDatiByQueryParams(finalQuery, numpun, codein);
    }
    return dati;
  }
  
  private List<?> getDatiTender(PathJsonEform code, String ngara, String ditta) throws Exception {
    List<?> dati = null;
    String finalQuery = QueryRegistry.getQuery(0, code);
    dati = getDatiByQueryParams(finalQuery, ngara, ditta);
    return dati;
  }
  
  private List<?> getDatiWithParams(PathJsonEform code, Object... params) throws Exception {
    List<?> dati = null;
    String finalQuery = QueryRegistry.getQuery(0, code);
    dati = getDatiByQueryParams(finalQuery, params);
    return dati;
  }

  /**
   * Aggiunge la chiave/valore a un JSONObject solo se il valore non e' vuoto o nullo.
   */
  public void addIfNotEmpty(JSONObject jsonObj, String jsonKey, Object value) {
    if (value == null) {
      // Se il valore e' null, non facciamo nulla
      return;
    }

    if (value instanceof String) {
      // Gestiamo il caso stringa: aggiungiamo alla JSON solo se non e' vuota o spazi bianchi
      String strValue = (String) value;
      if (!strValue.trim().isEmpty()) {
        putIfNotEmpty(jsonObj,jsonKey, strValue);
      }
    } else if(value instanceof java.sql.Date) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      putIfNotEmpty(jsonObj,jsonKey, sdf.format(value));
    } else {
      // Per gli altri tipi (es. Long), aggiungiamo direttamente
      putIfNotEmpty(jsonObj,jsonKey, value);
    }
  }


  public String getTab2d1ByTab2tip(String tab2tip, String tab2cod) throws Exception {
    String dato = null;
    if(tab2tip != null) {
      String query = "select tab2d1 from tab2 where tab2tip = ? and tab2cod = ? ";
      dato = (String) this.sqlManager.getObject(query, new Object[]{tab2tip, tab2cod});
    }
    return dato;
  }

  /**
   * Questa funzione e' stata creata per evitare di scrivere n query uguali per ogni genere nella classe QueryRegistry
   * ---per ora solo genere 1 e 3 verranno uniti nel valore 1
   * per ora solo genere 100 e 300 verranno uniti nel valore 100
   * @param genere
   */
  private Long getClassificazioneByGenere(Long genere) {
    switch (String.valueOf(genere)) {
    //case "1":
    //case "3":
      //genere = Long.valueOf(1);
      //break;
    case "100":
    case "300":
      genere = Long.valueOf(100);
      break;

    default:
      break;
    }
    return genere;
  }



  private String getTipoProceduraOth(Long tipgarg) throws Exception {
    String tipoProcedura = null;
    if(tipgarg != null) {
      String procedura= inviaVigilanzaManager.getFromTab2("A1z11", tipgarg, false);
      if(procedura != null) {
        if("50".equals(procedura)) {
          tipoProcedura = "oth-single";
        }
        if("51".equals(procedura)) {
          tipoProcedura = "oth-mult";
        }
      }
    }
    return tipoProcedura;
  }

  public String getTipoProceduraByIterga(Long tipgarg, Long iterga) throws Exception {

    String tipoProcedura = getTipoProceduraOth(tipgarg);
    if(tipoProcedura == null) {
      switch (String.valueOf(iterga)) {
      case "1":
      case "9":
        tipoProcedura = "open";
        break;
      case "2":
        tipoProcedura = "restricted";
        break;
      case "4":
        tipoProcedura = "neg-w-call";
        break;
      case "3":
      case "5":
      case "6":
        tipoProcedura = "neg-wo-call";
        break;
      default:
        tipoProcedura = "comp-tend";
        break;
      }
    }
    return tipoProcedura;
  }


  private String getSiglaByCodice(Long locnaz) throws SQLException {
    return (String) this.sqlManager.getObject("select codiso3 from g_nazioni where codice = ? ", new Object[]{locnaz});
  }
  
  
  private HashMap<String, Object> getCriterioByModlicgTippar(Long modlicg, Long tippar, String despar, Double maxpun, Double minpun) {
    HashMap<String, Object> res = new HashMap<>();
    Boolean criterio = Boolean.FALSE;
    
    if(minpun != null){
      minpun = UtilityMath.round(minpun, 0);
      res.put("BT-541-Lot-ThresholdNumber", minpun);
      res.put("BT-5423-Lot", "min-score");
      criterio = Boolean.TRUE;
    }

    if(modlicg == null || !Long.valueOf(6).equals(modlicg)){
      res.put("BT-539-Lot", "price");
      res.put("BT-540-Lot", "Prezzo");
      res.put("BT-541-Lot-WeightNumber", Long.valueOf(100));
      criterio = Boolean.TRUE;
    } else {
      if(tippar != null){
        if(tippar == 1)res.put("BT-539-Lot", "quality");
        if(tippar == 2)res.put("BT-539-Lot", "price");
        criterio = Boolean.TRUE;
      }
      if(maxpun != null){
        maxpun = UtilityMath.round(maxpun, 0);
        res.put("BT-541-Lot-WeightNumber", maxpun);
        criterio = Boolean.TRUE;
      }
      if(despar != null) {
        res.put("BT-540-Lot", despar);
        criterio = Boolean.TRUE;
      }
    }
    
    if (Boolean.TRUE.equals(criterio)) {
      res.put("BT-5421-Lot", "per-exa");
    }

    return res;
  }
  
  
  
  

  public String getUrlSQL(String codGar, String ngara) throws Exception {
    Long genere = null;
    if(ngara != null) {
      genere = getGenereByNgara(ngara);
      codGar = getCodgarByNgara(ngara);
    } else {
      String genereSql = "select genere, codice "
          + "from v_gare_genere "
          + "where codgar = ? "
          + "and genere in (1,2,3,10,11) ";
      Vector<?> datiVGenere = this.sqlManager.getVector(genereSql, new Object[]{codGar});
      
      if(datiVGenere!=null && !datiVGenere.isEmpty()){
        genere = (Long) SqlManager.getValueFromVectorParam(datiVGenere, 0).getValue();
        ngara = (String) SqlManager.getValueFromVectorParam(datiVGenere, 1).getValue();
      }
    }
    String url = ConfigManager.getValore("portaleAppalti.urlPubblica");
    String path = ConfigManager.getValore("invioScp.urlProcedurePortaleAppalti");

    if (url.endsWith("/")) {
      url = url.substring(0, url.length() - 1);
    }
    if (!path.startsWith("/")) {
      path = "/" + path;
    }
    if (!path.endsWith("/")) {
      path = path +"/";
    }
    
    switch (String.valueOf(genere)) {
    case "2": //gara a lotto unico
    case "10":
    case "11":
    case "20":
      url += path + ngara;
      break;
    case "1":
    case "100":
    case "3":
    case "300":
      url += path + codGar;
      break;
    default:
      break;
    }

    String res = "";
    if(!url.equals(ConfigManager.getValore("portaleAppalti.urlPubblica"))) {
      res = url;
    }
    return res;
  }
  
  
  /**
   * metodo per ottenere isespd da uuidEform
   */
  private String getIsespdByUuid(String uuidEform) throws SQLException {
    return (String) this.sqlManager.getObject(
        "select a.isespd "
        + "from g1cf_npa a "
        + "inner join g1npaform b on a.id = b.idcfnpa "
        + "inner join g1npascheda c on b.id = c.idform "
        + "where c.uuid_eform = ? ",
            new Object[] { uuidEform }
        );
  }
  
  private String getMotivoEsclusione(String uuidEform, String isProcedure, String ngara) throws Exception {
    String isEspd = getIsespdByUuid(uuidEform);
    String res = "";
    if(isNotEmpty(isEspd)) {
      res = "epo-sub-espd";
    } else {
      Long genere = null;
      if(isProcedure != null) {
        String codgar = getCodgarByUuid(uuidEform);
        genere = getGenereByCodgar(codgar);
      } else {
        genere = getGenereByNgara(ngara);
      }
      if(genere != null) {
        if(Long.valueOf(11).equals(genere)) {
          res = "epo-notice";
        } else {
          res = "epo-procurement-document";
        }
      }
    }
    return res;
  }
  
  public String getBozzaByCodiceEformUuidEform(String codiceEform, String uuidEformOriginario, String uuidEformCorrente) throws Exception {

    String bozzaRettifica = (String) this.sqlManager.getObject(
        "select s.exchange_eform "
            + "from g1cf_npa n "
            + "inner join g1npaform f on n.id = f.idcfnpa "
            + "inner join g1npascheda s on s.idform = f.id "
            + "where n.eform = ? "
            + "and s.uuid_eform = ? ",
            new Object[] { codiceEform, uuidEformOriginario }
        );

    if(bozzaRettifica != null && Boolean.TRUE.equals(isSchedaRettificaByUuidFormOriginario(uuidEformOriginario))) {
      bozzaRettifica = changeDateForBozzaRettifica(bozzaRettifica, uuidEformCorrente);
    }
    return bozzaRettifica;
  }

  private String changeDateForBozzaRettifica(String bozzaRettifica, String uuidEformCorrente) throws Exception {
    // Converte la stringa in un JSONObject
    JSONObject jsonObj = JSONObject.fromObject(bozzaRettifica);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String codgar = getCodgarByUuid(uuidEformCorrente);
    Long iterga = getItergaByCodgar(codgar);
    // Ottiene i dati da aggiornare
    List<?> results = getDatiLotti(uuidEformCorrente, PathJsonEform.GR_LOT_DEADLINES1_RETTIFICA, Long.valueOf(0));

    for (Object result : results) {
      Date bt131d = (Date) SqlManager.getValueFromVectorParam(result, 0).getValue();
      String bt131t = SqlManager.getValueFromVectorParam(result, 1).stringValue();
      Date BT1311d = (Date) SqlManager.getValueFromVectorParam(result, 2).getValue();
      String bt1311t = SqlManager.getValueFromVectorParam(result, 3).stringValue();
      Date bt132d = (Date) SqlManager.getValueFromVectorParam(result, 4).getValue();
      String bt132t = SqlManager.getValueFromVectorParam(result, 5).stringValue();

      updateChildren(jsonObj, sdf, BT1311d, bt1311t, bt131d, bt131t, bt132d, bt132t, iterga);
    }

    // Ritorna il JSON aggiornato in formato stringa
    return jsonObj.toString();
  }

  private void updateChildren(JSONObject obj, SimpleDateFormat sdf, Date bt1311d, String bt1311t, Date bt131d, String bt131t, Date bt132d, String bt132t, Long iterga) {
    if (obj.has("children")) {
      // Definisce i contentId target
      String targetContentIdBt1311d = "BT-1311(d)-Lot";
      String targetContentIdBt1311t = "BT-1311(t)-Lot";
      String targetContentIdBt131d = "BT-131(d)-Lot";
      String targetContentIdBt131t = "BT-131(t)-Lot";
      String targetContentIdBt132d = "BT-132(d)-Lot";
      String targetContentIdBt132t = "BT-132(t)-Lot";

      JSONArray children = obj.getJSONArray("children");
      for (int i = 0; i < children.size(); i++) {
        JSONObject child = children.getJSONObject(i);
        String contentId = child.optString("contentId", "");

        if (contentId.equals(targetContentIdBt1311d) && (Long.valueOf(2).equals(iterga) || Long.valueOf(4).equals(iterga))) {
          if(bt1311d != null)
            child.put("value", sdf.format(bt1311d));
        } else if (contentId.equals(targetContentIdBt1311t) && (Long.valueOf(2).equals(iterga) || Long.valueOf(4).equals(iterga))) {
          child.put("value", bt1311t);
        } else if (contentId.equals(targetContentIdBt131d) && (!Long.valueOf(2).equals(iterga) && !Long.valueOf(4).equals(iterga))) {
          if(bt131d != null)
            child.put("value", sdf.format(bt131d));
        } else if (contentId.equals(targetContentIdBt131t) && (!Long.valueOf(2).equals(iterga) && !Long.valueOf(4).equals(iterga))) {
          child.put("value", bt131t);
        } else if (contentId.equals(targetContentIdBt132d)) {
          if(bt132d != null)
            child.put("value", sdf.format(bt132d));
        } else if (contentId.equals(targetContentIdBt132t)) {
          child.put("value", bt132t);
        }
        // Richiamo ricorsivo per eventuali children nidificati
        updateChildren(child, sdf, bt1311d, bt1311t, bt131d, bt131t, bt132d, bt132t, iterga);
      }
    }
  }

  
  public String getCodigaByNgara(String ngara) throws SQLException {
    return (String) this.sqlManager.getObject(
        "select codiga from gare where ngara = ?",
            new Object[] { ngara }
        );
  }
  
  public void getSalvaBozza(String codiceEform, String uuidEform, String in) throws Exception {

    TransactionStatus status = null;
    boolean commit = true;

    String updateExchangeEform = "";
    try {

      status = this.sqlManager.startTransaction();

      updateExchangeEform = "UPDATE g1npascheda "
          + "SET exchange_eform = ? "
          + "WHERE uuid_eform = ? "
          + "  AND idform IN ( "
          + "    SELECT f.id "
          + "    FROM g1npaform f "
          + "    INNER JOIN g1cf_npa n ON n.id = f.idcfnpa "
          + "    WHERE n.eform = ? "
          + "  )";
      this.sqlManager.update(updateExchangeEform, new Object[] { in, uuidEform, codiceEform });

    } catch (Exception e) {
      commit = false;
      throw new Exception("Errore durante il salvataggio della bozza per uuidEform: "+uuidEform+" e codice eform: "+codiceEform, e);
    } finally {
      if (status != null) {
        if (commit) {
          this.sqlManager.commitTransaction(status);
        } else {
          this.sqlManager.rollbackTransaction(status);
        }
      }
    }
    
  }
  
  public String getLotIdentByCodiga(String codiga) {
    String lotIdentifier = null;
    if(codiga != null && !"".equals(codiga)) {
      lotIdentifier = "LOT-"+String.format("%0" + 4 + "d", Integer.valueOf(codiga));
    } else {
      lotIdentifier = "LOT-"+String.format("%0" + 4 + "d", Integer.valueOf(1));
    }
    return lotIdentifier;
  }
  
  public String getResIdentByCodiga(String codiga) {
    String lotIdentifier = null;
    if(codiga != null && !"".equals(codiga)) {
      lotIdentifier = "RES-"+String.format("%0" + 4 + "d", Integer.valueOf(codiga));
    } else {
      lotIdentifier = "RES-"+String.format("%0" + 4 + "d", Integer.valueOf(1));
    }
    return lotIdentifier;
  }
  
  
  private List<List<String>> getListaDitteByDitta(String ditta, Long aqoper, String codgar) throws Exception {
    List<List<String>> listaDitte = new ArrayList<>();
    List<String> lista = new ArrayList<>();

    if(aqoper != null && aqoper.intValue()==2){
      List datiDitgaq = sqlManager.getListVector(
          "select d.dittao "
              + "from ditgaq d "
              + "inner join gare g on d.ngara = g.ngara "
              + "where g.codgar1 = ? ",
              new Object[] { codgar });
      if(datiDitgaq != null && datiDitgaq.size()>0){
        for(int i=0;i<datiDitgaq.size();i++){
          String dittao = (String) SqlManager.getValueFromVectorParam(datiDitgaq.get(i),0).getValue();
          Long tipimpAq = (Long) sqlManager.getObject(
              "select tipimp from impr where codimp = ?",
              new Object[] { dittao });
          if(tipimpAq != null && (tipimpAq == 10 || tipimpAq == 3)){
            List listRagimp = sqlManager.getListVector("select coddic from ragimp where codime9 = ?",
                new Object[] { dittao });
            for(int j=0; j<listRagimp.size(); j++) {
              String dittaRT  = (String) SqlManager.getValueFromVectorParam(
                  listRagimp.get(j), 0).getValue();
              lista.add(dittaRT);
            }
          }else{
            lista.add(dittao);
          }
        }
        listaDitte.add(lista);
      }
    } else {
      Long tipimp = (Long) sqlManager.getObject(
          "select tipimp from impr where codimp = ?",
          new Object[] { ditta });
      if(tipimp != null && (tipimp == 10 || tipimp == 3)){
        List listRagimp = sqlManager.getListVector("select coddic from ragimp where codime9 = ?",
            new Object[] { ditta });
        for(int i=0; i<listRagimp.size(); i++) {
          String dittaRT  = (String) SqlManager.getValueFromVectorParam(
              listRagimp.get(i), 0).getValue();
          lista.add(dittaRT);
        }
        listaDitte.add(lista);
      } else {
        lista.add(ditta);
        listaDitte.add(lista);
      }
    }
    return listaDitte;
  }
  
  

  private String getFamilyByCodEform(String codiceEform) throws Exception {
    
    String famiglia = "";
      switch (codiceEform) {
      case "1":
      case "2":
      case "4":
      case "5":
      case "7":
      case "8":
      case "10":
      case "11":
      case "12":
      case "13":
      case "14":
        famiglia = "PIN";
        break;

      case "15":
      case "16":
      case "17":
      case "18":
      case "19":
      case "20":
      case "21":
      case "22":
      case "23":
      case "24":
        famiglia = "CN";
        break;

      case "25":
      case "26":
      case "27":
      case "28":
      case "29":
      case "30":
      case "31":
      case "32":
      case "33":
      case "34":
      case "35":
      case "36":
      case "37":
      case "38":
      case "39":
      case "40":
        famiglia = "CAN";
        break;

      default:
        throw new Exception("Il codice Eform "+codiceEform+" non appartiene ad alcuna famiglia");
      }
      return famiglia;
  }
  
  /**
   * Se Genere= 10
      works       
      se TIPOELE >= 100
      supplies
      se 10 <= TIPOELE < 100
      services
      se TIPOELE < 10
      
      Se Genere= 11
      works
      se TIPOAPP >= 100
      supplies    
      se 10 <= TIPOAPP < 100
      services
      se TIPOAPP < 10
      
      Se Genere!= 10,11
      works       se TIPGEN.TORN = 1
      supplies    se TIPGEN.TORN = 2
      services        se TIPGEN.TORN = 3
   * @param bt23
   * @param genere
   * @return
   */
  private String getValoreByTip(Long bt23, Long genere) {
    String valore = null;
    if(genere != null) {
      if(Long.valueOf(10).equals(genere) || Long.valueOf(11).equals(genere)) {
        if(bt23 >= Long.valueOf(100)) {
          valore = "works";
        } else if(bt23 < Long.valueOf(10)) {
          valore = "services";
        } else {
          valore = "supplies";
        }
      } else {
        if(Long.valueOf(1).equals(bt23)) {
          valore = "works";
        } else if(Long.valueOf(2).equals(bt23)) {
          valore = "supplies";
        } else if(Long.valueOf(3).equals(bt23)) {
          valore = "services";
        }
      }
    }
    return valore;
  }
  
  private Boolean isMandatariaByDitta(String dittaPadre, String ditta) throws SQLException {
    Boolean isMandataria = Boolean.FALSE;
    String getImpman = "select impman from ragimp where codime9 = ? and coddic = ?";
    String impman = (String) this.sqlManager.getObject(getImpman, new Object[]{dittaPadre,ditta});
    if(impman != null && "1".equals(impman)) {
      isMandataria = Boolean.TRUE;
    }
    return isMandataria;
  }
  
  private String getValoreByCodice(String codice) {
    String value = "not-known";
    if(codice != null) {
      if("1".equals(codice)) {
        value = "yes";
      } else {
        value = "no";
      }
    }
    return value;
  }
  
  private Long getAqoperByDittaNgara(String ditta, String ngara) throws SQLException {
    return (Long) this.sqlManager.getObject(
        "select g1.aqoper "
        + "from gare g "
        + "inner join gare1 g1 on g1.ngara = g.ngara "
        + "where g.esineg is null "
        + "and g.ditta = ? "
        + "and g.ngara = ? ",
            new Object[] { ditta, ngara }
        );
  }
  
  private Long getAqoperByNgara(String ngara) throws SQLException {
    return (Long) this.sqlManager.getObject(
        "select aqoper from gare1 where ngara = ? ",
            new Object[] { ngara }
        );
  }
  
  public void getSalvaXml(String codiceEform, String uuidEform, String tracciato) throws Exception {
    // Parsing sicuro anti-XXE PRIMA di qualsiasi operazione sul database
    Document safeXmlDoc;
    try {
      safeXmlDoc = parseXMLSecurely(tracciato);
    } catch (Exception e) {
      logger.error("Tentativo di importazione XML vulnerabile rilevato: ", e);
      throw new WebApplicationException("Importazione XML non riuscita: XML malformato o non consentito.", Response.Status.BAD_REQUEST);
    }
    //Abbiamo deciso che se passa il controllo precendente possiamo tranquillamente passare il tracciato
    //senza utilizzare safeXmlDoc
    //String safeTracciato = convertDocumentToString(safeXmlDoc);

    // Da qui in avanti usa safeTracciato invece di tracciato
    TransactionStatus transazioneUpdate = this.sqlManager.startTransaction();
    boolean commitTransaction = false;
    try {
      Long statonpa = (Long) this.sqlManager.getObject(
          "select statonpa from g1npascheda where uuid_eform = ? ",
                  new Object[] { uuidEform });
      if(statonpa != null && statonpa >= Long.valueOf(10)) {
        throw new WebApplicationException("XML_ALREADY_SENT");
      }
      Vector<?> datiScheda = sqlManager.getVector(
          "SELECT gs.id, gs.idprg_eform, gs.iddoc_eform, gc.eform, gp.codgar "
          + "FROM g1npaform gp "
          + "INNER JOIN g1npascheda gs ON gp.id = gs.idform "
          + "INNER JOIN g1cf_npa gc ON gp.idcfnpa = gc.id "
          + "WHERE gs.uuid_Eform = ? ",
          new Object[] { uuidEform });

      Long idScheda = SqlManager.getValueFromVectorParam(datiScheda, 0).longValue();
      String idprgEform = SqlManager.getValueFromVectorParam(datiScheda, 1).stringValue();
      Long iddocEform = SqlManager.getValueFromVectorParam(datiScheda, 2).longValue();
      String numeroEFORM = SqlManager.getValueFromVectorParam(datiScheda, 3).stringValue();
      String codgar = SqlManager.getValueFromVectorParam(datiScheda, 4).stringValue();

      if (idprgEform != null && iddocEform != null) {
        UploadInfo oldBucketInfo = ObjectStorageUtils.getUploadInfo("select bucket_ref, bucket_uuid from w_docdig where idprg=? and iddocdig=?",new Object[] {idprgEform,iddocEform});
        ObjectStorageUtils.deleteFile(oldBucketInfo.bucket_ref(),oldBucketInfo.uuid());
        sqlManager.update("delete from W_DOCDIG where idprg=? and iddocdig=?", new Object[] { idprgEform, iddocEform });
      }

      Long maxIdDocDig = (Long) sqlManager.getObject("select max(iddocdig) from w_docdig where idprg='PG'", null);
      Long newIdDocDig = (maxIdDocDig == null) ? Long.valueOf(1) : (maxIdDocDig + 1);

      byte[] xmlByte = tracciato.getBytes(StandardCharsets.UTF_8);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      baos.write(xmlByte);

      if (codgar != null && codgar.startsWith("$")) {
        codgar = codgar.substring(1);
      }
      String descrizioneDocumento = "eForm " + numeroEFORM;
      String nomeDocumento = codgar + "_MeForm_" + numeroEFORM + ".xml";

      DataColumnContainer containerDOCDIG = new DataColumnContainer(new DataColumn[] {
          new DataColumn("W_DOCDIG.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")),
          new DataColumn("W_DOCDIG.IDDOCDIG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, newIdDocDig)),
          new DataColumn("W_DOCDIG.DIGENT", new JdbcParametro(JdbcParametro.TIPO_TESTO, "G1NPASCHEDA")),
          new DataColumn("W_DOCDIG.DIGKEY1", new JdbcParametro(JdbcParametro.TIPO_TESTO, idScheda.toString())),
          new DataColumn("W_DOCDIG.DIGNOMDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeDocumento)),
          new DataColumn("W_DOCDIG.DIGDESDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, descrizioneDocumento)),
          new DataColumn("W_DOCDIG.DIGOGG", new JdbcParametro(JdbcParametro.TIPO_BINARIO, baos)) });
      containerDOCDIG.insert("W_DOCDIG", sqlManager);
      ObjectStorageUtils.addFile("PG", newIdDocDig);

      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      sqlManager.update("update g1npascheda set idprg_eform=?, iddoc_eform=?, dnot_eform=? where g1npascheda.id=?", new Object[] { "PG", newIdDocDig, timestamp, idScheda });
      commitTransaction = true;
    } catch (SQLException e) {
      logger.error("Errore durante il salvataggio dell'MeForm", e);
    } finally {
      try {
        if (commitTransaction) {
          this.sqlManager.commitTransaction(transazioneUpdate);
        } else {
          this.sqlManager.rollbackTransaction(transazioneUpdate);
        }
      } catch (SQLException e) {
        throw new Exception("Errore durante la chiusura della transazione", e);
      }
    }
  }
  
  
  private String getUrlProcedurePortaleAppaltiByNgara(String ngara) throws Exception {
    Long genere = getGenereByNgara(ngara);
    String codGar = getCodgarByNgara(ngara);

    String url = ConfigManager.getValore("portaleAppalti.urlPubblica");
    String path = ConfigManager.getValore("invioScp.urlProcedurePortaleAppalti");

    if (url.endsWith("/")) {
      url = url.substring(0, url.length() - 1);
    }
    if (!path.startsWith("/")) {
      path = "/" + path;
    }
    if (!path.endsWith("/")) {
      path = path +"/";
    }

    switch (String.valueOf(genere)) {
    case "2": //gara a lotto unico
    case "10":
    case "11":
    case "20":
      url += path + ngara;
      break;
    case "1":
    case "100":
    case "3":
    case "300":
      url += path + codGar;
      break;
    default:
      break;
    }
    return url;
  }
  
  public Long getIDByUuidEform(String uuidEform) throws SQLException {
    return (Long) this.sqlManager.getObject(
        "select id from g1npascheda where uuid_eform = ? ",
            new Object[] { uuidEform }
        );
  }

  public Boolean getPrimoSalvataggioBozza(String uuidEform) throws SQLException {
    Object result = this.sqlManager.getObject(
        "SELECT CASE WHEN exchange_eform IS NULL THEN 1 ELSE 0 END " +
            "FROM g1npascheda " +
            "WHERE uuid_eform = ?",
            new Object[]{uuidEform}
        );

    Boolean primoSalvatagglio = Boolean.FALSE;
    if (result instanceof Boolean) {
      primoSalvatagglio = (Boolean) result;
    } else if (result instanceof Number) {
      primoSalvatagglio = ((Number) result).intValue() != 0;
    }
    return primoSalvatagglio;
  }
  
  public Boolean getPrimoSalvataggioXML(String uuidEform) throws SQLException {
    Object result = this.sqlManager.getObject(
        "SELECT CASE WHEN iddoc_eform IS NULL THEN 1 ELSE 0 END " +
            "FROM g1npascheda " +
            "WHERE uuid_eform = ?",
            new Object[]{uuidEform}
        );

    Boolean primoSalvataggio = Boolean.FALSE;
    if (result instanceof Boolean) {
      primoSalvataggio = (Boolean) result;
    } else if (result instanceof Number) {
      primoSalvataggio = ((Number) result).intValue() != 0;
    }
    return primoSalvataggio;
  }

  private int extractPositionByNumberOrgCode(JSONArray array, String orgCode) {
    int newOrgNumber = Integer.parseInt(orgCode.replace("ORG-", ""));
    
    // Trova la posizione corretta per mantenere l'ordine
    int insertIndex = 0;
    for (int i = 0; i < array.size(); i++) {
        JSONObject existingObj = (JSONObject) array.get(i);
        String existingOpt200 = (String) existingObj.get("OPT-200-Organization-Company");
        int existingOrgNumber = Integer.parseInt(existingOpt200.replace("ORG-", ""));
        
        if (newOrgNumber < existingOrgNumber) {
            break; // Trova il punto di inserimento
        }
        insertIndex++;
    }
    return insertIndex;
  }

  
  private String getNomeDittaByCodimp(String ditta) throws SQLException {
    return (String) this.sqlManager.getObject(
        "select nomimp from impr where codimp = ? ",
        new Object[] { ditta }
    );
  }
  
  private Boolean isSchedaRettificaByUuidEformCorrente(String uuidEform) throws SQLException {
    Long trovato = (Long) this.sqlManager.getObject(
        "SELECT COUNT(*) as found "
        + "FROM g1nparet p "
        + "INNER JOIN g1npascheda s ON s.id = p.idnpascheda "
        + "WHERE s.uuid_eform = ? ",
            new Object[]{ uuidEform }
        );

    Boolean isRettifica = Boolean.FALSE;
    if (trovato != null && trovato > Long.valueOf(0)) {
      isRettifica = Boolean.TRUE;
    }
    return isRettifica;
  }
  
  private Boolean isSchedaRettificaByUuidFormOriginario(String uuidEform) throws SQLException {
    Long trovato = (Long) this.sqlManager.getObject(
        "SELECT COUNT(*) as found "
        + "FROM g1nparet p "
        + "INNER JOIN g1npascheda s ON s.id = p.idnpascheda "
        + "WHERE s.uuid_eform in "
        + "( "
        + "    select uuid_eform "
        + "    from g1npascheda "
        + "    where idschedanparet = "
        + "    ( "
        + "        select id "
        + "        from g1npascheda s "
        + "        where s.uuid_eform = ? "
        + "    ) "
        + ") ",
            new Object[]{ uuidEform }
        );

    Boolean isRettifica = Boolean.FALSE;
    if (trovato != null && trovato > Long.valueOf(0)) {
      isRettifica = Boolean.TRUE;
    }
    return isRettifica;
  }
  
  
  private Document parseXMLSecurely(String xmlContent) throws Exception {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

    // Blocca gli attacchi XXE
    docFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    InputSource is = new InputSource(new StringReader(xmlContent));
    return docBuilder.parse(is);
  }
  

  private String convertDocumentToString(Document doc) throws Exception {
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    StringWriter writer = new StringWriter();
    transformer.transform(new DOMSource(doc), new StreamResult(writer));
    return writer.getBuffer().toString();
  }

  
}




