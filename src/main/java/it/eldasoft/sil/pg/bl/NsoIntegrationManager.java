package it.eldasoft.sil.pg.bl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;
import org.bouncycastle.util.encoders.Base64;
import org.json.simple.JSONObject;

import it.eldasoft.gene.bl.admin.UffintManager;
import it.eldasoft.gene.db.dao.FileAllegatoDao;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.UfficioIntestatario;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.db.dao.NsoAllegatiDao;
import it.eldasoft.sil.pg.db.dao.NsoOrdiniDao;
import it.eldasoft.sil.pg.db.domain.nso.Beneficiario;
import it.eldasoft.sil.pg.db.domain.nso.Fornitore;
import it.eldasoft.sil.pg.db.domain.nso.LineaOrdine;
import it.eldasoft.sil.pg.db.domain.nso.NsoAllegato;
import it.eldasoft.sil.pg.db.domain.nso.Ordinante;
import it.eldasoft.sil.pg.db.domain.nso.Ordine;
import it.eldasoft.sil.pg.db.domain.nso.PuntoConsegna;
import it.eldasoft.sil.pg.ws.nso.rest.client.ClientNso;
import it.eldasoft.sil.pg.ws.nso.rest.request.OrderNsoRequest;
import it.eldasoft.sil.pg.ws.nso.rest.request.ValidationNsoRequest;
import it.eldasoft.utils.utility.UtilityNumeri;
import oasisNamesSpecificationUblSchemaXsdCommonAggregateComponents2.AddressType;
import oasisNamesSpecificationUblSchemaXsdCommonAggregateComponents2.AttachmentType;
import oasisNamesSpecificationUblSchemaXsdCommonAggregateComponents2.CustomerPartyType;
import oasisNamesSpecificationUblSchemaXsdCommonAggregateComponents2.DeliveryType;
import oasisNamesSpecificationUblSchemaXsdCommonAggregateComponents2.DocumentReferenceType;
import oasisNamesSpecificationUblSchemaXsdCommonAggregateComponents2.ItemType;
import oasisNamesSpecificationUblSchemaXsdCommonAggregateComponents2.LineItemType;
import oasisNamesSpecificationUblSchemaXsdCommonAggregateComponents2.LocationType;
import oasisNamesSpecificationUblSchemaXsdCommonAggregateComponents2.MonetaryTotalType;
import oasisNamesSpecificationUblSchemaXsdCommonAggregateComponents2.OrderLineType;
import oasisNamesSpecificationUblSchemaXsdCommonAggregateComponents2.PartyTaxSchemeType;
import oasisNamesSpecificationUblSchemaXsdCommonAggregateComponents2.PartyType;
import oasisNamesSpecificationUblSchemaXsdCommonAggregateComponents2.PaymentTermsType;
import oasisNamesSpecificationUblSchemaXsdCommonAggregateComponents2.PeriodType;
import oasisNamesSpecificationUblSchemaXsdCommonAggregateComponents2.PriceType;
import oasisNamesSpecificationUblSchemaXsdCommonAggregateComponents2.SupplierPartyType;
import oasisNamesSpecificationUblSchemaXsdCommonAggregateComponents2.TaxCategoryType;
import oasisNamesSpecificationUblSchemaXsdCommonAggregateComponents2.TaxSchemeType;
import oasisNamesSpecificationUblSchemaXsdCommonBasicComponents2.BaseQuantityType;
import oasisNamesSpecificationUblSchemaXsdCommonBasicComponents2.EmbeddedDocumentBinaryObjectType;
import oasisNamesSpecificationUblSchemaXsdCommonBasicComponents2.EndpointIDType;
import oasisNamesSpecificationUblSchemaXsdCommonBasicComponents2.ItemClassificationCodeType;
import oasisNamesSpecificationUblSchemaXsdCommonBasicComponents2.LineExtensionAmountType;
import oasisNamesSpecificationUblSchemaXsdCommonBasicComponents2.PartialDeliveryIndicatorType;
import oasisNamesSpecificationUblSchemaXsdCommonBasicComponents2.PriceAmountType;
import oasisNamesSpecificationUblSchemaXsdCommonBasicComponents2.QuantityType;
import oasisNamesSpecificationUblSchemaXsdOrder2.OrderDocument;
import oasisNamesSpecificationUblSchemaXsdOrder2.OrderType;

/**
 * 
 * @author gabriele.nencini
 *
 */
public class NsoIntegrationManager {
  private static final Logger logger = Logger.getLogger(NsoIntegrationManager.class);
  private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
  private ClientNso clientNso;
  private NsoOrdiniDao nsoOrdiniDao;
  private NsoOrdiniManager nsoOrdiniManager;
  private NsoAllegatiDao nsoAllegatiDao;
  private FileAllegatoDao fileAllegatoDao;
  private UffintManager uffintManager;
  
  /*
   * imported from https://gist.github.com/jdcrensh/4670128
   */
  public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  public static final int BASE = ALPHABET.length();
  public final String charset = "UTF-8";
  
  public void setClientNso(ClientNso clientNso) {
    this.clientNso = clientNso;
  }
  
  public void setNsoOrdiniDao(NsoOrdiniDao nsoOrdiniDao) {
    this.nsoOrdiniDao = nsoOrdiniDao;
  }

  public void setNsoOrdiniManager(NsoOrdiniManager nsoOrdiniManager) {
    this.nsoOrdiniManager = nsoOrdiniManager;
  }

  public void setNsoAllegatiDao(NsoAllegatiDao nsoAllegatiDao) {
    this.nsoAllegatiDao = nsoAllegatiDao;
  }

  public void setFileAllegatoDao(FileAllegatoDao fileAllegatoDao) {
    this.fileAllegatoDao = fileAllegatoDao;
  }

  public void setUffintManager(UffintManager uffintManager) {
    this.uffintManager = uffintManager;
  }

  /**
   * This method will call the validateOrder of ClientNso
   * @param orderId
   * @return
   */
  @SuppressWarnings("unchecked")
  public String getValidatedOrder(String orderId) {
    JSONObject json = new JSONObject();
    
    try {
      OrderDocument order = produceUBLOrderObject(orderId);
    
      ValidationNsoRequest request = new ValidationNsoRequest();
      request.setOrderId(orderId);
      request.setOrderXml(produceUBLOrderAsInputStream(order));
      Response res = clientNso.validateOrder(request);
      json.put("result","OK");
      if(HttpStatus.SC_OK!=res.getStatus()) {
        json.put("result","ERROR");
        if(res.hasEntity()) {
          JSONObject entity = res.readEntity(JSONObject.class);
          Pattern pattern = Pattern.compile("\":(.*?)}");
          Matcher matcher = null;
          
          if(entity.containsKey("details")) {
            ArrayList<String> arrayOfErrors = (ArrayList<String>) entity.get("details");
            final ArrayList<String> arrayToPut = new ArrayList<String>(arrayOfErrors.size());
            for(String error : arrayOfErrors) {
              if(error.startsWith("[PEPPOL") || error.startsWith("[NSO")) {
                //peppol / NSO validation
                logger.info("Error Peppol");
                logger.info(error);
                arrayToPut.add(getErrorTranslatorForPeppolValidation(error));
              } else {
                // XSD validation
                logger.info("Error Validation");
                logger.info(error);
                matcher = pattern.matcher(error);
                String firstReference = null;
                if (matcher.find()) {
                  firstReference = matcher.group(1);
                }
                String secondReference = StringUtils.substringBeforeLast(StringUtils.substringAfterLast(error, ":"), "}");
                
                arrayToPut.add(getErrorTranslatorForXSDValidation(firstReference,secondReference));
              }
            }
            if(!arrayToPut.isEmpty()) {
              entity.put("details", arrayToPut);
            }
            
          }
          json.put("entity", entity);
        } else {
          json.put("entity", "");
        }
      }
    }catch (NsoAccountingCustomerPartyMissingException e) {
      logger.error("Error during validation.",e);
      genericExceptionHandling(json,e);
    }catch (Exception e) {
      logger.error("Error during validation.",e);
      genericExceptionHandling(json,null);
    }
    return json.toJSONString();
  }

  private String getErrorTranslatorForPeppolValidation(String error) {
    if(error.startsWith("[NSO")){
      String er = StringUtils.substringBeforeLast(error, "-");
      String er2 = StringUtils.substringAfter(er, "–");
      if(StringUtils.isEmpty(StringUtils.trimToEmpty(er2))) {
        er2 = StringUtils.substringAfter(er, "-");//different chars in different situations!!!
      }
      return er2.trim();
    }
    if(error.startsWith("[PEPPOL-T01-B27901]")) {
      return "Nella linea d'ordine manca l'identificativo della categoria di tassa.";
    }
    if(error.startsWith("[PEPPOL-T01-R029]")) {
      return "Ciascuna categoria di imposte DEVE avere un'aliquota di categoria FISCALE, tranne se l'ordine non è soggetto a IMPOSTA.";
    }
    if(error.startsWith("[PEPPOL-T01-R030]")) {
      return "Impostare l'IVA nella sezione Linee ordine.";
    }
    if(error.startsWith("[PEPPOL-T01-B03301]")) {
      return "Verificare Sezione parti coinvolte. L'ordinante IPA deve avere impostato l'endpoint NSO.";
    }
    if(error.startsWith("[PEPPOL-T01-R002]")) {
      return "Inserire la data di scadenza dell'ordine nella sezioe Dati generali.";
    }
    if(error.startsWith("[PEPPOL-T01-R008]")) {
      return "Verificare che il totale degli importi immessi per le linee d'ordine sia corretto.";
    }
    if(error.startsWith("[PEPPOL-T01-R030]")) {
      return "Nella sezione AccountingCustomerParty deve essere inserito l'indirizzo.";
    }
    if(error.startsWith("[PEPPOL-T01-B12801]")) {
      return "Nella sezione AccountingCustomerParty-PartyTaxScheme deve essere inserito il companyID.";
    }
    return error;
  }

  private String getErrorTranslatorForXSDValidation(String firstReference, String secondReference) {
    String toMatch = firstReference + secondReference;
    if("IssueDateID".equalsIgnoreCase(toMatch)) {
      return "Manca l'identificativo del documento";
    }
    if("OrderTypeCodeIssueDate".equalsIgnoreCase(toMatch)) {
      return "Manca la data dell'ordine";
    }
//    if("CustomerReferenceDocumentCurrencyCode".equalsIgnoreCase(toMatch)) {
//      return "Manca la tipologi di moneta per l'intero documento";
//    } this is validated by PEPPOL
    if("DocumentTypeID".equalsIgnoreCase(toMatch)) {
      return "Manca identificativo del documento di riferimento";
    }
    if("SellerSupplierPartyBuyerCustomerParty".equalsIgnoreCase(toMatch)) {
      return "Verificare che gli elementi della sezione \"Parti Coinvolte\" abbiano tutte le informazioni";
    }
    if("DeliverySellerSupplierParty".equalsIgnoreCase(toMatch)) {
      return "Verificare che gli elementi della sezione \"Parti Coinvolte\" abbiano tutte le informazioni";
    }
    if("OrderLineSellerSupplierParty".equalsIgnoreCase(toMatch)) {
      return "Verificare la sezione Parti coinvolte. Dati fornitore incompleti e/o mancanti.";
    }
    if("OrderLineBuyerCustomerParty".equalsIgnoreCase(toMatch)) {
      return "Verificare la sezione Parti coinvolte. Dati ordinante incompleti e/o mancanti.";
    }
    if("OrderLine".equalsIgnoreCase(secondReference)) {
      return "Verificare la sezione linee ordine.";
    }
    return "Errore Generico. Verificare la congruenza dei dati inseriti.";
  }

  /**
   * @param json
   * @param e 
   */
  @SuppressWarnings("unchecked")
  private void genericExceptionHandling(final JSONObject json, Exception e) {
    json.put("result","ERROR");
    JSONObject details = new JSONObject();
    ArrayList<String> detailErrorList = new ArrayList<String>(1);
    if(e!=null) {
      detailErrorList.add(e.getMessage());
    } else {
      detailErrorList.add("Errore nella comunicazione con endpoint nso-integration.");
    }
    details.put("details", detailErrorList);
    json.put("entity", details);
  }
  
  /**
   * 
   * @param orderId
   * @return
   */
  @SuppressWarnings("unchecked")
  public String getProcessOrder(String orderId) {
    
    JSONObject json = new JSONObject();
    json.put("result","OK");
    
    try {
      Ordine ord = nsoOrdiniDao.getOrderById(Long.parseLong(orderId));
      
      OrderDocument order = produceUBLOrderObject(ord, false);
      
      OrderNsoRequest request = new OrderNsoRequest();
      request.setOrderId(orderId);
      request.setOrderXml(produceUBLOrderAsInputStream(order));
      request.setOrderDate(dateFormatter.format(ord.getData_ordine()));
      request.setOrderCode(ord.getCodord());
      request.setOrderExpiryDate(dateFormatter.format(ord.getData_scadenza()));
      request.setLinkedOrderId(ord.getId_padre());
      request.setLinkedOrderCode(ord.getCodord_padre());
      request.setRootOrderId(ord.getId_originario());
      request.setRootOrderCode(ord.getCodord_originario());
      request.setCig(ord.getCig());
      request.setNgara(ord.getNgara());
      request.setTotalPriceWithVat(ord.getImporto_totale());
      request.setUffint(ord.getCodein());
      
      Ordinante ordi = nsoOrdiniDao.getBuyerCustomerPartyByNsoOrdiniIdAndTypeOne(ord.getId());
      request.setEndPoint(ordi.getEndpoint());
      
      Fornitore seller = nsoOrdiniDao.getSellerSupplierPartyByNsoOrdiniId(ord.getId());
      if(seller != null) {
        request.setCodimp(seller.getCodimp());
      }
      request.setHasAttachment(Boolean.FALSE);
      if(order.getOrder().getAdditionalDocumentReferenceArray()!=null) {
        request.setHasAttachment(order.getOrder().getAdditionalDocumentReferenceArray().length>0);
      }
      
      StringBuffer sb = new StringBuffer();
      sb.append(StringUtils.upperCase(ord.getCodnaz()));
      sb.append(StringUtils.upperCase(ord.getPiva()));
      sb.append("_OZ_");
      sb.append(StringUtils.leftPad(this.fromBase10(Integer.parseInt(StringUtils.substring(ord.getCodord(), 1))), 5, "0"));
      sb.append(".xml");
      request.setFileName(sb.toString());
      
      logger.info(request);
    
      Response res = clientNso.processOrder(request);
      if(HttpStatus.SC_OK==res.getStatus()) {
        nsoOrdiniManager.variazStatoOrdineToInviato(Long.valueOf(orderId));
      }
      if(HttpStatus.SC_OK!=res.getStatus()) {
        json.put("result","ERROR");
        if(res.hasEntity()) {
          json.put("entity", res.readEntity(JSONObject.class));
        } else {
          json.put("entity", "");
        }
      }
    }catch (NsoAccountingCustomerPartyMissingException e) {
      logger.error("Error during processing.",e);
      genericExceptionHandling(json,e);
    }catch (Exception e) {
      logger.error("Error during processing.",e);
      genericExceptionHandling(json,null);
    }
    return json.toJSONString();
  }
  
  /**
   * 
   * @param orderId
   * @return
   */
  private OrderDocument produceUBLOrderObject(String orderId) throws Exception {
    Ordine ord = null;
    try {
      ord = nsoOrdiniDao.getOrderById(Long.parseLong(orderId));
    }catch(Exception e) {
      logger.error("Impossible find object with id "+orderId,e);
      throw e;
    }
    return produceUBLOrderObject(ord, true);
  }
  
  /**
   * 
   * @param ord
   * @return
   * @throws Exception
   */
  private OrderDocument produceUBLOrderObject(final Ordine ord, boolean isValidation) throws Exception {
    
    final OrderDocument doc = this.generateBasicNsoOrderData();
    final OrderType ordXml = doc.getOrder();
    this.sectionGeneralDataExtraction(ord, ordXml);
    this.sectionRelatedPartiesDataExtraction(ord, ordXml);
    this.sectionDeliveryDataExtraction(ord, ordXml);
    this.sectionOrderLineDataExtraction(ord, ordXml);
    if(!isValidation) {
      this.sectionAttachmentDataExtraction(ord, ordXml);
    }
    //TODO print only if debug is enabled
    this.printLogOrder(doc);
    return doc;
    
  }

  /**
   * This method will insert in the order document all the attachments if present.
   * 
   * @param ord - the {@link it.eldasoft.sil.pg.db.domain.nso.Ordine Ordine}
   * @param ordXml - the {@link oasisNamesSpecificationUblSchemaXsdOrder2.OrderType ordXml} to be filled
   */
  private void sectionAttachmentDataExtraction(final Ordine ord, final OrderType ordXml) {
    List<NsoAllegato> attachmentNamesList = nsoAllegatiDao.getNsoAllegatiByNsoOrdiniId(ord.getId());
    if(logger.isDebugEnabled())
      logger.debug("Found "+attachmentNamesList.size() +" attachment for "+ord);
    if(attachmentNamesList.size()>0) {
      for(NsoAllegato da : attachmentNamesList) {
        BlobFile bf = fileAllegatoDao.getFileAllegato(da.getIdprg(), da.getIddocdig());
        if(bf!=null) {
          DocumentReferenceType drt = ordXml.addNewAdditionalDocumentReference();
          drt.addNewID().setStringValue(da.getDescrizione());
          AttachmentType at = drt.addNewAttachment();
          EmbeddedDocumentBinaryObjectType edbo = at.addNewEmbeddedDocumentBinaryObject();
          String fileName = bf.getNome();
          edbo.setFilename(fileName);
          edbo.setByteArrayValue(Base64.encode(bf.getStream()));
          edbo.setMimeCode(this.extractMimeCodeFromFileExtension(StringUtils.substringAfterLast(fileName, ".")));
        }
      }
    }
  }

  /**
   * This method will extract the MimeCode Accepted for PEPPOL 
   * @see <a href="https://docs.peppol.eu/poacc/upgrade-3/codelist/MimeCode/">Peppol MimeCode</a>
   * @param fileExtension - the Extension to be checked
   * @return the mime code, application/pdf as default
   */
  private String extractMimeCodeFromFileExtension(String fileExtension) {
    if("png".equalsIgnoreCase(fileExtension))
      return "image/png";
    if("jpeg".equalsIgnoreCase(fileExtension) || "jpg".equalsIgnoreCase(fileExtension))
      return "image/jpeg";
    if("tiff".equalsIgnoreCase(fileExtension))
      return "image/tiff";
    if("acad".equalsIgnoreCase(fileExtension))
      return "application/acad";
    if("dwg".equalsIgnoreCase(fileExtension))
      return "application/dwg";
//      return "drawing/dwg";
    if("ods".equalsIgnoreCase(fileExtension))
      return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    if("xlsx".equalsIgnoreCase(fileExtension))
      return "application/vnd.oasis.opendocument.spreadsheet";
    return "application/pdf";
  }

  /**
   * This method is populating the order with the data relatives to the page section "Parti Coinvolte"
   * @param ord - the {@link it.eldasoft.sil.pg.db.domain.nso.Ordine Ordine}
   * @param ordXml - the {@link oasisNamesSpecificationUblSchemaXsdOrder2.OrderType ordXml} to be filled
   * @throws Exception 
   */
  private void sectionRelatedPartiesDataExtraction(final Ordine ord, final OrderType ordXml) throws Exception {
    List<Ordinante> ordiList = nsoOrdiniDao.getBuyerCustomerPartiesByNsoOrdiniId(ord.getId());
    
    CustomerPartyType buyerCustomerParty = null;
    PartyType party = null;
    PartyTaxSchemeType partyTaxScheme = null;

    CustomerPartyType accountingCustomerParty = null; //AccountingCustomerParty
    PartyType accountingParty = null;
    PartyTaxSchemeType accountingPartyTaxScheme = null;
    
    if(ordiList.size()>0) {
      buyerCustomerParty = ordXml.addNewBuyerCustomerParty();
      party = buyerCustomerParty.addNewParty();
      
      partyTaxScheme = party.addNewPartyTaxScheme();
      partyTaxScheme.addNewTaxScheme().addNewID().setStringValue("VAT");
      party.addNewPartyLegalEntity().addNewRegistrationName().setStringValue(ord.getNomein());
      
      
      // aggiunta di accountingCustomerParty
      accountingCustomerParty = ordXml.addNewAccountingCustomerParty();
      accountingParty = accountingCustomerParty.addNewParty();
      
      accountingPartyTaxScheme = accountingParty.addNewPartyTaxScheme();
      accountingPartyTaxScheme.addNewTaxScheme().addNewID().setStringValue("VAT");
      accountingParty.addNewPartyLegalEntity().addNewRegistrationName().setStringValue(ord.getNomein());
      
    }
    
    for(Ordinante ordi : ordiList) {
      if(1==ordi.getTipo().intValue()) {
        //tipo=1 IPA
        party.addNewPartyName().addNewName().setStringValue(ordi.getNomein());
        
        if(StringUtils.isNotEmpty(ordi.getCodipa())) {
          EndpointIDType endId = party.addNewEndpointID();
          endId.setSchemeID("0201");
          endId.setStringValue(ordi.getCodipa());
        }
        
        if(!partyTaxScheme.isSetCompanyID()) {
          partyTaxScheme.addNewCompanyID().setStringValue(StringUtils.upperCase(ordi.getCodnaz())+ordi.getPiva());
        }
        
      }
      if(2==ordi.getTipo().intValue()) {
        if(!partyTaxScheme.isSetCompanyID()) {
          partyTaxScheme.addNewCompanyID().setStringValue(StringUtils.upperCase(ordi.getCodnaz())+ordi.getPiva());
        } else {
          //TODO verify with test
          partyTaxScheme.getCompanyID().setStringValue(StringUtils.upperCase(ordi.getCodnaz())+ordi.getPiva());
        }
        /*
         * @see doc 20200221- CEF_eInvoicing Annex 1 - Order detail -_LP02.docx
         * section 18.6 "dati uffint corrente a prescindere dall'ufficio ordinante"
         */
        party.addNewPartyLegalEntity().addNewRegistrationName().setStringValue(ordi.getNomein());        
      }
    } //end for
    
    // aggiunta di accountingCustomerParty
    if(StringUtils.isBlank(ord.getCodein_fattura())) throw new NsoAccountingCustomerPartyMissingException("Intestatario della fattura mancante.");
    // devo recuperare i dati dalla uffint
    UfficioIntestatario uffFatt = uffintManager.getUfficioIntestatarioByPKWithAddressAndNation(ord.getCodein_fattura());
    if(uffFatt == null ) throw new NsoAccountingCustomerPartyMissingException("Intestatario della fattura mancante o mancante del codice nazione.");
    logger.warn(uffFatt);
    if(StringUtils.isEmpty(uffFatt.getCodnaz())) throw new NsoAccountingCustomerPartyMissingException("Intestatario della fattura mancante del codice nazione.");
    if(StringUtils.isEmpty(uffFatt.getViaein()) 
        || StringUtils.isEmpty(uffFatt.getCitein()) 
        ) throw new NsoAccountingCustomerPartyMissingException("Intestatario dei dati di recapito per la fatturazione.");
    accountingCustomerParty = ordXml.addNewAccountingCustomerParty();
    accountingParty = accountingCustomerParty.addNewParty();
    
    accountingPartyTaxScheme = accountingParty.addNewPartyTaxScheme();
    accountingPartyTaxScheme.addNewTaxScheme().addNewID().setStringValue("VAT");
    accountingPartyTaxScheme.addNewCompanyID().setStringValue(StringUtils.upperCase(uffFatt.getCodnaz())+uffFatt.getPartitaIVA());
    accountingParty.addNewPartyLegalEntity().addNewRegistrationName().setStringValue(uffFatt.getNome());
    AddressType accountingPartyPostalAddress = accountingParty.addNewPostalAddress();
    accountingPartyPostalAddress.addNewCityName().setStringValue(uffFatt.getCitein());
    accountingPartyPostalAddress.addNewCountry().addNewIdentificationCode().setStringValue(uffFatt.getCodnaz().toUpperCase());
    accountingPartyPostalAddress.addNewStreetName().setStringValue(uffFatt.getViaein());
    accountingPartyPostalAddress.addNewPostalZone().setStringValue(uffFatt.getCapein());
    
    Fornitore seller = nsoOrdiniDao.getSellerSupplierPartyByNsoOrdiniId(ord.getId());
    if(seller!=null){
      SupplierPartyType sParty = ordXml.addNewSellerSupplierParty();
      party = sParty.addNewParty();
      EndpointIDType endId = party.addNewEndpointID();
      endId.setSchemeID("9906");
      endId.setStringValue(StringUtils.upperCase(seller.getCodnaz())+seller.getCfimp());
      if("PF".equalsIgnoreCase(seller.getType())) {
        endId.setSchemeID("9907");
      }
      
      if(StringUtils.isNotEmpty(seller.getCfimp())) {
        party.addNewPartyIdentification().addNewID().setStringValue(seller.getCfimp());
      }
      
      AddressType address = party.addNewPostalAddress();
      if(StringUtils.isNotEmpty(seller.getVia()))
        address.addNewStreetName().setStringValue(seller.getVia());
      if(StringUtils.isNotEmpty(seller.getCitta()))
        address.addNewCityName().setStringValue(seller.getCitta());
      if(StringUtils.isNotEmpty(seller.getCap()))
        address.addNewPostalZone().setStringValue(seller.getCap());
      address.addNewCountry().addNewIdentificationCode().setStringValue(seller.getCodnaz());
      
      party.addNewPartyLegalEntity().addNewRegistrationName().setStringValue(seller.getNomimp());
    }
    
    if("1".equalsIgnoreCase(ord.getIs_div_benef())) {
      
        Beneficiario ben = nsoOrdiniDao.getDeliveryPartyByNsoOrdiniId(ord.getId());
        if(ben!=null) {
        DeliveryType delivery = null;
        if(ordXml.getDeliveryArray()!=null && ordXml.getDeliveryArray().length==1) {
          delivery = ordXml.getDeliveryArray()[0];
        } else {
          delivery = ordXml.addNewDelivery();
        }
        
        party = delivery.addNewDeliveryParty();
        
        party.addNewPartyName().addNewName().setStringValue(ben.getDenominazione());
        AddressType address = party.addNewPostalAddress();
        address.addNewStreetName().setStringValue(ben.getIndirizzo());
        address.addNewCityName().setStringValue(ben.getCitta());
        address.addNewPostalZone().setStringValue(ben.getCap());
        address.addNewCountry().addNewIdentificationCode().setStringValue(ben.getCodnaz());
      }
    }
    
  }

  /**
   * This method is populating the order with the data relatives to the page section "Linee Ordine"
   * @param ord - the {@link it.eldasoft.sil.pg.db.domain.nso.Ordine Ordine}
   * @param ordXml - the {@link oasisNamesSpecificationUblSchemaXsdOrder2.OrderType ordXml} to be filled
   * @throws Exception 
   */
  private void sectionOrderLineDataExtraction(final Ordine ord, final OrderType ordXml) throws Exception {
    List<LineaOrdine> lineOrderList = new ArrayList<LineaOrdine>(0);
    try {
      lineOrderList = nsoOrdiniDao.getOrderLinesByNsoOrdiniId(ord.getId());
    }catch(Exception e) {
      logger.error("Impossible find object with id "+ord.getId(),e);
      throw e;
    }
    
    OrderLineType orderLine = null;
    LineItemType lineItem = null;
    QuantityType quantity = null;
    DeliveryType delivery = null;
    PeriodType period = null;
    LineExtensionAmountType lineExtensionAmount = null;
    ItemType item = null;
    TaxCategoryType taxCategory = null;
    TaxSchemeType taxScheme = null;
    PartyType origParty = null;
    
    String unitCodeForQuantity = "C62";
    ord.setImporto_totale(0.0d);
    Double totalAmountWithoutVat = 0.0d;
    for(LineaOrdine line : lineOrderList) {
      Double priceWithVatForLineItem = 0.0d;
      orderLine = ordXml.addNewOrderLine();
      lineItem = orderLine.addNewLineItem();
      
      lineItem.addNewID().setStringValue(line.getId_linea().toString());
      quantity = lineItem.addNewQuantity();
      quantity.setUnitCode(unitCodeForQuantity);
      quantity.setBigDecimalValue(BigDecimal.valueOf(line.getQuantita()));

      PriceType price = lineItem.addNewPrice();
      PriceAmountType priceAmaount = price.addNewPriceAmount();
      priceAmaount.setCurrencyID("EUR");
      priceAmaount.setBigDecimalValue(BigDecimal.valueOf(UtilityNumeri.arrotondaNumero(line.getPrezzo_unitario(),2).doubleValue()));
      
      BaseQuantityType baseQuantity = price.addNewBaseQuantity();
      baseQuantity.setUnitCode(unitCodeForQuantity);
      baseQuantity.setStringValue("1");
      
      
      if(line.getData_inizio_cons()!=null || line.getData_fine_cons()!=null) {
        delivery = lineItem.addNewDelivery();
        period = delivery.addNewRequestedDeliveryPeriod();
        if(line.getData_inizio_cons()!=null) {
          period.addNewStartDate().setStringValue(dateFormatter.format(line.getData_inizio_cons()));
        }
        if(line.getData_fine_cons()!=null) {
          period.addNewEndDate().setStringValue(dateFormatter.format(line.getData_fine_cons()));
        }
      }
      
      lineExtensionAmount = lineItem.addNewLineExtensionAmount();
      lineExtensionAmount.setCurrencyID("EUR");
      lineExtensionAmount.setBigDecimalValue(BigDecimal.valueOf(UtilityNumeri.arrotondaNumero(line.getQuantita() * line.getPrezzo_unitario(),2).doubleValue()));
      
      priceWithVatForLineItem += lineExtensionAmount.getBigDecimalValue().doubleValue();
      totalAmountWithoutVat += priceWithVatForLineItem;
      
      if(StringUtils.isNotEmpty(line.getCentro_costo())) {
        lineItem.addNewAccountingCost().setStringValue(line.getCentro_costo());
      }
      
      
      item = lineItem.addNewItem();
      item.addNewName().setStringValue(line.getCodice());
      if(StringUtils.isNotBlank(line.getDescrizione())) {
        item.addNewDescription().setStringValue(line.getDescrizione());
      }
      taxCategory = item.addNewClassifiedTaxCategory();
      taxCategory.addNewID().setStringValue("S"); //TODO check
      
      taxScheme = taxCategory.addNewTaxScheme();
      taxScheme.addNewID().setStringValue("VAT");
      if(line.getIva()!=null) {
        taxCategory.addNewPercent().setBigDecimalValue(BigDecimal.valueOf(line.getIva()));
        priceWithVatForLineItem *= (1+line.getIva()/100);
        
      }
      priceWithVatForLineItem = Math.round(priceWithVatForLineItem * 100.0) / 100.0;
      //TODO excluded because of the previous "TODO"
//      if(StringUtils.isNotEmpty(line.getCodice_esenzione())) {
//        if(!taxCategory.isSetID()) {
//          taxCategory.addNewID().setStringValue(line.getCodice_esenzione());
//        } else {
//          taxCategory.getID().setStringValue(line.getCodice_esenzione());
//        }
//      }
      
      if(StringUtils.isNotEmpty(line.getCons_parziale())) {
        PartialDeliveryIndicatorType partialDeliveryIndicator = PartialDeliveryIndicatorType.Factory.newInstance();
        partialDeliveryIndicator.setBooleanValue(true);
        if("2".equalsIgnoreCase(line.getCons_parziale())) {
          partialDeliveryIndicator.setBooleanValue(false);
        }
        lineItem.setPartialDeliveryIndicator(partialDeliveryIndicator);
      }
      
      if(StringUtils.isNotEmpty(line.getCodcpv())) {
        ItemClassificationCodeType icct = item.addNewCommodityClassification().addNewItemClassificationCode();
        icct.setListID("STI");
        icct.setStringValue(StringUtils.trimToEmpty(line.getCodcpv()));
      }
      
      if(StringUtils.isNotEmpty(line.getCodein_rich())) {
        origParty = lineItem.addNewOriginatorParty();
        origParty.addNewPartyIdentification().addNewID().setStringValue(line.getCodein_rich_cf());
        origParty.addNewPartyName().addNewName().setStringValue(StringUtils.trimToEmpty(line.getCodein_rich_nome()));
      }
      ord.setImporto_totale(ord.getImporto_totale()+priceWithVatForLineItem);
    }//end for
    
    MonetaryTotalType mtt = ordXml.addNewAnticipatedMonetaryTotal();
    mtt.addNewLineExtensionAmount().setBigDecimalValue(BigDecimal.valueOf(UtilityNumeri.arrotondaNumero(totalAmountWithoutVat,2).doubleValue()));
    mtt.getLineExtensionAmount().setCurrencyID("EUR");
    
    mtt.addNewTaxInclusiveAmount().setBigDecimalValue(BigDecimal.valueOf(UtilityNumeri.arrotondaNumero(ord.getImporto_totale(),2).doubleValue()));
    mtt.getTaxInclusiveAmount().setCurrencyID("EUR");
    
    mtt.addNewPayableAmount().setBigDecimalValue(BigDecimal.valueOf(UtilityNumeri.arrotondaNumero(ord.getImporto_totale(),2).doubleValue()));
    mtt.getPayableAmount().setCurrencyID("EUR");
    
    if(ord.getArrotondamento()!=null) {
      mtt.addNewPayableRoundingAmount().setBigDecimalValue(BigDecimal.valueOf(UtilityNumeri.arrotondaNumero(ord.getArrotondamento(),2).doubleValue()));
      mtt.getPayableRoundingAmount().setCurrencyID("EUR");
      mtt.getPayableAmount().setBigDecimalValue(BigDecimal.valueOf(UtilityNumeri.arrotondaNumero(ord.getImporto_totale() + ord.getArrotondamento(),2).doubleValue()));
    }
  }

  /**
   * This method is populating the order with the data relatives to the page section "Dati Generali"
   * @param ord - the {@link it.eldasoft.sil.pg.db.domain.nso.Ordine Ordine}
   * @param ordXml - the {@link oasisNamesSpecificationUblSchemaXsdOrder2.OrderType ordXml} to be filled
   */
  private void sectionGeneralDataExtraction(final Ordine ord, final OrderType ordXml) {
    ordXml.addNewID().setStringValue(ord.getCodord());
    
    if(StringUtils.isNotEmpty(ord.getCig())) {
      ordXml.addNewOriginatorDocumentReference().addNewID().setStringValue("CIG:"+StringUtils.trimToEmpty(ord.getCig()));
    } else {
      ordXml.addNewOriginatorDocumentReference().addNewID().setStringValue(StringUtils.trimToEmpty(ord.getEsenzione_cig()));
    }
    
    if(ord.getData_ordine()!=null) {
      ordXml.addNewIssueDate().setStringValue(dateFormatter.format(ord.getData_ordine()));
    }
    // avoid insert note, the note field inside the table is used only for internal use
//    if(StringUtils.isNotBlank(StringUtils.trimToEmpty(ord.getNote()))) {
//      ordXml.addNewNote().setStringValue(ord.getNote());
//    }
    
    if(ord.getData_scadenza()!=null) {
      ordXml.addNewValidityPeriod().addNewEndDate().setStringValue(dateFormatter.format(ord.getData_scadenza()));
    }
    
    
    //the code below should be used to set the validityPeriod of the order 
    //the validityPeriod means the date frame in which the customer is willing to receive the goods
//    if(ord.getData_fine_val()!=null) {
//      PeriodType periodType = ordXml.addNewValidityPeriod();
//      periodType.addNewEndDate().setDateValue(ord.getData_fine_val());
//      
//      if(ord.getData_inizio_val()!=null) {
//        periodType.addNewStartDate().setDateValue(ord.getData_inizio_val());
//      }
//    }
    
    if(ord.getData_fine_forn()!=null) {
      DeliveryType delivery = ordXml.addNewDelivery();
      PeriodType periodType = delivery.addNewRequestedDeliveryPeriod();
      periodType.addNewEndDate().setStringValue(dateFormatter.format(ord.getData_fine_forn()));
      if(ord.getData_inizio_forn()!=null) {
        periodType.addNewStartDate().setStringValue(dateFormatter.format(ord.getData_inizio_forn()));
      }
    }
    
    if(ord.getId_padre()!=null) {
      /*
       * "Connected", per il collegamento;
       * "Cancelled" per la revoca;
       * "Revised" per la sostituzione.
       */
      
      Ordine ordRoot = null;
      try {
        ordRoot = nsoOrdiniDao.getOrderById(ord.getId_padre());
      }catch(Exception e) {
        logger.error("Impossible find object with id "+ord.getId_padre(),e);
      }
      if(ordRoot != null) {
        List<Ordinante> ordiList = nsoOrdiniDao.getBuyerCustomerPartiesByNsoOrdiniId(ordRoot.getId());
        StringBuffer linkedId = new StringBuffer();
        linkedId.append(ordRoot.getCodord());
        linkedId.append("#");
        linkedId.append(dateFormatter.format(ordRoot.getData_ordine()));
        linkedId.append("#");
        for(Ordinante ordi : ordiList) {
          if(1==ordi.getTipo().intValue()) {
            linkedId.append(ordi.getCodipa());   
            break;
          }
        }
        linkedId.append("#");
        linkedId.append("Revised");
        ordXml.addNewOrderDocumentReference().addNewID().setStringValue(linkedId.toString());
      }
      
    }
    
    if(StringUtils.isNotBlank(ord.getCup())) {
      DocumentReferenceType docRef = ordXml.addNewAdditionalDocumentReference();
      docRef.addNewID().setStringValue(ord.getCup());
      docRef.addNewDocumentType().setStringValue("CUP");
    }
    
    if(StringUtils.isNotBlank(StringUtils.trimToEmpty((ord.getCentro_costo())))) {
      ordXml.addNewAccountingCost().setStringValue(StringUtils.trim(ord.getCentro_costo()));
    }
    
    if(StringUtils.isNotEmpty(ord.getReferente())) {
      ordXml.addNewCustomerReference().setStringValue(StringUtils.trim(ord.getReferente()));
    }
    
    if(StringUtils.isNotEmpty(ord.getRif_offerta())) {
      ordXml.addNewSalesOrderID().setStringValue(StringUtils.trim(ord.getRif_offerta()));
    }
    
    if(StringUtils.isNotEmpty(ord.getNrepat())) {
      ordXml.addNewContract().addNewID().setStringValue(StringUtils.trim(ord.getNrepat()));
    }
  }
  
  /**
   * This method is populating the order with the data relatives to the page section "Consegne"
   * @param ord - the {@link it.eldasoft.sil.pg.db.domain.nso.Ordine Ordine}
   * @param ordXml - the {@link oasisNamesSpecificationUblSchemaXsdOrder2.OrderType ordXml} to be filled
   */
  private void sectionDeliveryDataExtraction(final Ordine ord, final OrderType ordXml) throws Exception {
    PuntoConsegna pc = null;
    try {
      pc = nsoOrdiniDao.getDeliveryPointByNsoOrdiniId(ord.getId());
    }catch(Exception e) {
      logger.error("Impossible find object with id "+ord.getId(),e);
      throw e;
    }
    
    if(pc!=null) {
      DeliveryType delivery = null;
      if(ordXml.getDeliveryArray()!=null && ordXml.getDeliveryArray().length==1) {
        delivery = ordXml.getDeliveryArray()[0];
      } else {
        delivery = ordXml.addNewDelivery();
      }
      
      LocationType location = delivery.addNewDeliveryLocation();
      AddressType address = location.addNewAddress();
      
      if(StringUtils.isNotEmpty(pc.getCod_punto_cons())) {
        location.addNewID().setStringValue(StringUtils.trim(pc.getCod_punto_cons()));
      } else {
        address.addNewStreetName().setStringValue(pc.getIndirizzo());
        if (StringUtils.isNotEmpty(StringUtils.trimToEmpty(pc.getLocalita()))) {
          address.addNewAdditionalStreetName().setStringValue(StringUtils.trim(pc.getLocalita()));
        }
        if(StringUtils.isNotEmpty(pc.getCitta())) {
          address.addNewCityName().setStringValue(pc.getCitta());
        }
        if(StringUtils.isNotEmpty(pc.getCap())) {
          address.addNewPostalZone().setStringValue(pc.getCap());
        }
        
        if ("1".equalsIgnoreCase(StringUtils.trimToNull(pc.getCons_domicilio()))) {
          location.addNewID().setStringValue(PuntoConsegna.CONSEGNA_DOMICILIARE);
          if(StringUtils.isNotEmpty(pc.getAltre_indic())) {
            address.addNewAddressLine().addNewLine().setStringValue(StringUtils.trim(pc.getAltre_indic()));
          }
        }

      }
      address.addNewCountry().addNewIdentificationCode().setStringValue(pc.getCodnaz());
    }
    
  }
  
  private InputStream produceUBLOrderAsInputStream(final OrderDocument order) throws IOException {
    XmlOptions opts = new XmlOptions();
    opts.setCharacterEncoding(charset);
    return new BufferedInputStream(IOUtils.toInputStream(order.xmlText(opts),charset));
  }
  
  /**
   * This method will build the basic structure of the OrderDocument
   * with the default data
   * @return the OrderDocument
   */
  private OrderDocument generateBasicNsoOrderData() {
    XmlOptions opts = new XmlOptions();
    opts.setCharacterEncoding(charset);
    OrderDocument doc = OrderDocument.Factory.newInstance(opts);
    
    OrderType order = doc.addNewOrder();
    order.addNewCustomizationID().setStringValue("urn:fdc:peppol.eu:poacc:trns:order:3");
    order.addNewProfileID().setStringValue("urn:fdc:peppol.eu:poacc:bis:ordering:3");
    order.addNewOrderTypeCode().setStringValue("220");
    order.addNewDocumentCurrencyCode().setStringValue("EUR");
    
    doc.setOrder(order);
    return doc;
  }
  
  /**
   * Utility for print a OrderDocument
   * @param doc - the OrderDocument to be printed
   * @param formatted - true if you want to format the output in a readable form
   */
  private void printLogOrder(final OrderDocument doc,boolean formatted) {
    XmlOptions opts = new XmlOptions();
    if(formatted) {
      opts.setSavePrettyPrint();
      opts.setSavePrettyPrintIndent(4);
    }
    logger.info("Printing OrderDocument");
    logger.info(doc.xmlText(opts));
  }
  
  /**
   * Utility for print a OrderDocument.
   * It will call {@link #printLogOrder(OrderDocument, boolean)}
   * @param doc - the OrderDocument to be printed
   */
  private void printLogOrder(final OrderDocument doc) {
    printLogOrder(doc,true);
  }

  /**
   * this method will transform a number to base62 String
   * @param i - the integer to be converted
   * @return the generated base62 string
   */
  private String fromBase10(int i) {
    StringBuilder sb = new StringBuilder("");
    if (i == 0) {
      return "a";
    }
    while (i > 0) {
      i = this.fromBase10(i, sb);
    }
    return sb.reverse().toString();
  }

  private int fromBase10(int i, final StringBuilder sb) {
    int rem = i % BASE;
    sb.append(ALPHABET.charAt(rem));
    return i / BASE;
  }
  
  public byte[] getFileXmlFromNsoWsOrdiniByFileName(String fileName) throws GestoreException {
    return this.nsoOrdiniDao.getNsoWsOrdineFileXmlFromFileName(fileName);
//    return this.nsoOrdiniManager.getFileXmlFromNsoWsOrdiniByFileName(fileName);
  }
}
