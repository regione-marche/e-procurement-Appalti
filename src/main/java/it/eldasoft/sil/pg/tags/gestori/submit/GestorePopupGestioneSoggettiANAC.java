/*
 * Created on 29/12/23
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.AccountSSO;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.npa.ScambioDatiPCPManager;
import it.eldasoft.sil.pg.web.struts.npa.ScambioDatiPCPAction;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import net.sf.json.JSONObject;

/**
 * Gestore non standard per valorizzare i campi TORN.CODRUP o TORN.CODDRP
 *
 * @author Manuel Bridda
 */
public class GestorePopupGestioneSoggettiANAC extends AbstractGestoreEntita {

  private ScambioDatiPCPManager        scambioDatiPCPManager        = null;


  public GestorePopupGestioneSoggettiANAC() {
    super(false);
  }

  @Override
  public String getEntita() {
    return "TORN";
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);

    // Estraggo i managers
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
    
    scambioDatiPCPManager = (ScambioDatiPCPManager) UtilitySpring.getBean("scambioDatiPCPManager",
        this.getServletContext(), ScambioDatiPCPManager.class);
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    
    // lettura dei parametri di input
    String codgar = datiForm.getString("CODGAR");
    String uffint = (String) this.getRequest().getParameter("SAPresaCarico");
    HttpSession session = this.getRequest().getSession();

    AccountSSO accountSSO = (AccountSSO) session.getAttribute(CostantiGenerali.ID_ATTRIBUTO_SESSIONE_ACCOUNT_SSO);
    if(accountSSO==null || !accountSSO.hasLoa(3))
      throw new GestoreException(
          "Non e' stato effettuato l'accesso tramite SSO con un livello di autenticazione maggiore o uguale a 2", "scambiodatiPCP.ws.loaU3");

    ProfiloUtente profiloUtente = (ProfiloUtente) session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    try {
      
      JSONObject result = new JSONObject();
      Client client = ClientBuilder.newClient();
      client.property(ClientProperties.CONNECT_TIMEOUT, 60000);
      Map<String,Object> map = new HashMap<String,Object>();
      map.put(ScambioDatiPCPManager.CLIENT, client);
      map.put(ScambioDatiPCPManager.SESSION, this.getRequest().getSession());

      Vector values = sqlManager.getVector("select idappaltonpa, vernpa, altrisog, cenint from torn where codgar=?", new Object[] {codgar});
      map.put(ScambioDatiPCPManager.ID_APPALTO_TORN, SqlManager.getValueFromVectorParam(values, 0).stringValue());
      map.put(ScambioDatiPCPManager.VER_NPA, SqlManager.getValueFromVectorParam(values, 1).stringValue());
      Long altrisog = SqlManager.getValueFromVectorParam(values,2).longValue();
      String cenint = SqlManager.getValueFromVectorParam(values, 3).stringValue();

      Vector valuesUffint = sqlManager.getVector("select codausa, cfein, codausaanac, cfanac from uffint where codein=?", new Object[] {uffint});
      String codausa = SqlManager.getValueFromVectorParam(valuesUffint, 0).stringValue();
      String cfSA = SqlManager.getValueFromVectorParam(valuesUffint, 1).stringValue();

      //nel caso fosse stata selezionata la CUC che ha fatto la gara prelevo i dati anac fittizi in caso di altrisog in (2,3,4) e
      if (cenint.equals(uffint) && altrisog != null && ((SqlManager.getValueFromVectorParam(valuesUffint, 3).stringValue()!=null && (Long.valueOf(2).equals(altrisog) || Long.valueOf(3).equals(altrisog))) || Long.valueOf(4).equals(altrisog))) {
        codausa = SqlManager.getValueFromVectorParam(valuesUffint, 2).stringValue();
        cfSA = SqlManager.getValueFromVectorParam(valuesUffint, 3).stringValue();
        map.put(ScambioDatiPCPManager.IS_CODAUSA_FITT,true);
      }
      map.put("codiceFiscaleSA",cfSA);
      map.put(ScambioDatiPCPManager.CODICE_AUSA, codausa);
      
      String error = null;
//vecchia gestione
//      if(datiForm.isColumn("CODRUP")) {
//        if(datiForm.isModifiedColumn("CODRUP")) {
//          if(datiForm.getColumn("CODRUP").getValue()==null) {
//            error = "scambiodatiPCP.ws.rupValorizzato";
//          }else {
//            String cftec = (String) sqlManager.getObject("select cftec from tecni where codtec=?", new Object[] {datiForm.getString("CODRUP")});
//            if(StringUtils.isBlank(cftec))
//              error = "scambiodatiPCP.ws.rupCfValorizzato";
//            else {
//              if(cftec.equalsIgnoreCase(profiloUtente.getCodiceFiscale())) {
//                map.put(ScambioDatiPCPManager.CFRP, cftec);
//                insertEvento(this.getRequest(),ScambioDatiPCPAction.PRESACARICO,codgar,map,null);
//                result=scambioDatiPCPManager.servicePresaCarico(map);
//                insertEvento(this.getRequest(),ScambioDatiPCPAction.PRESACARICO,codgar,map,result);
//              }
//              else
//                error = "scambiodatiPCP.ws.rupCfDifforme";
//            }
//          }
//        }else {
//          error = "scambiodatiPCP.ws.rupNonModificato";
//        }
//      }else {
//        String cfrup = (String) sqlManager.getObject("select tecni.cftec from tecni, torn where tecni.codtec=torn.codrup and torn.codgar=?", new Object[] {codgar});   
//        if(cfrup.equalsIgnoreCase(profiloUtente.getCodiceFiscale())){
//          map.put(ScambioDatiPCPManager.CFRP, cfrup);
//          if(datiForm.isModifiedColumn("CODDRP")) {
//            String cftec = null;
//            if(datiForm.getColumn("CODDRP").getOriginalValue().getStringValue()!=null) { //se c'era già un DRP lo rimuovo
//              cftec = (String) sqlManager.getObject("select cftec from tecni where codtec=?", new Object[] {datiForm.getColumn("CODDRP").getOriginalValue().getStringValue()});
//              map.put(ScambioDatiPCPManager.CFDRP, cftec);
//              //map.put(ScambioDatiPCPManager.BODY_RUOLO, "DRP1");
//              insertEvento(this.getRequest(),ScambioDatiPCPAction.ELIMINASOGGETTO,codgar,map,null);
//              result=scambioDatiPCPManager.serviceEliminaSoggetto(map);
//              insertEvento(this.getRequest(),ScambioDatiPCPAction.ELIMINASOGGETTO,codgar,map,result);
//              if(result.has("done") && "Y".equals(result.getString("done")))
//                sqlManager.update("update torn set coddrp=null where codgar=?", new Object[] {codgar});
//            } 
//            if(datiForm.getString("CODDRP")!=null) {//se abbiamo un nuovo DRP, lo aggiungiamo
//              cftec = (String) sqlManager.getObject("select cftec from tecni where codtec=?", new Object[] {datiForm.getString("CODDRP")});
//              if(StringUtils.isBlank(cftec))
//                error = "scambiodatiPCP.ws.drpCfValorizzato";
//              else if(cftec.equalsIgnoreCase(cfrup))
//                error = "scambiodatiPCP.ws.drpIsRup";
//              else { 
//                map.put(ScambioDatiPCPManager.CFDRP, cftec);
//                insertEvento(this.getRequest(),ScambioDatiPCPAction.AGGIUNGISOGGETTO,codgar,map,null);
//                result=scambioDatiPCPManager.serviceAggiungiSoggetto(map);
//                insertEvento(this.getRequest(),ScambioDatiPCPAction.AGGIUNGISOGGETTO,codgar,map,result);
//              }
//            }
//          }else {
//            error = "scambiodatiPCP.ws.drpNonModificato";
//          }
//        }else {
//          error = "scambiodatiPCP.ws.utenteNonAutorizzato";
//        }
//      } 
      //gestione transitoria?
      if(datiForm.isColumn("CODDRP")) {
          if(datiForm.getColumn("CODDRP").getValue()==null) {
            error = "scambiodatiPCP.ws.incaricatoValorizzato";
          }else {
            String cftec = (String) sqlManager.getObject("select cftec from tecni where codtec=?", new Object[] {datiForm.getString("CODDRP")});
            if(StringUtils.isBlank(cftec))
              error = "scambiodatiPCP.ws.incaricatoCfValorizzato";
            else {
              if(cftec.equalsIgnoreCase(profiloUtente.getCodiceFiscale())) {
                map.put(ScambioDatiPCPManager.CFRP, cftec);
                insertEvento(this.getRequest(),ScambioDatiPCPAction.PRESACARICO,codgar,map,null);
                result=scambioDatiPCPManager.servicePresaCarico(map);
                insertEvento(this.getRequest(),ScambioDatiPCPAction.PRESACARICO,codgar,map,result);
              }
              else
                error = "scambiodatiPCP.ws.incaricatoCfDifforme";
            }
          }
      }
      if(StringUtils.isNotBlank(error)) {
        throw new GestoreException(
            error, error);
      }
      if(result.has("done") && "Y".equals(result.getString("done"))) {
        this.getRequest().setAttribute("esito", "OK");
//        if(datiForm.isColumn("CODRUP")) {
//          sqlManager.update("update torn set codrup=? where codgar=?", new Object[] {datiForm.getString("CODRUP"),codgar});
//          sqlManager.update("update torn set codrup = ? where codgar in (select codgar1 from gare where ngarapa in "
//            + "(select ngara from gare where codgar1=?) or preced in (select ngara from gare where codgar1=?))",
//            new Object[] {datiForm.getString("CODRUP"),codgar,codgar});
//        }
//        else
          sqlManager.update("update torn set coddrp=? where codgar=?", new Object[] {datiForm.getString("CODDRP"),codgar});
      }else
        this.getRequest().setAttribute("result", result);
    } catch (MessageBodyProviderNotFoundException e) {
      String keycloakBaseUrl = ConfigManager.getValore("keycloak.realms.baseurl");
      String keycloakRealm = ConfigManager.getValore("keycloak.progettoappalti.subpath");
      if(StringUtils.isBlank(keycloakBaseUrl) || StringUtils.isBlank(keycloakRealm))
        throw new GestoreException("Il servizio gateway ha risposto con un formato dati non conforme. L'errore potrebbe essere dovuto alla mancata configurazione dell'accesso tramite servizi keycloak.","scambiodatiPCP.ws.redirectLoginGateway", e);
      else
        throw new GestoreException("Il servizio gateway ha risposto con un formato dati non conforme","scambiodatiPCP.ws.badResponseGateway",e);
    }catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'aggiornamento del campo TORN."+(datiForm.isColumn("CODRUP")?"CODRUP":"CODDRP"), null,  e);
    }
  }

  private void insertEvento(HttpServletRequest request, String servizio, String codgar, Map<String, Object> parameters, JSONObject result) {
    
    String oggEvento = codgar;
    if(codgar.charAt(0)=='$')
      oggEvento=codgar.substring(1);
    
    LogEvento logEvento = LogEventiUtils.createLogEvento(request);
    logEvento.setLivEvento(1);
    logEvento.setOggEvento(oggEvento);
    if(result==null) {
      logEvento.setCodEvento("NPA_REQ_"+servizio);
      logEvento.setDescr("Invio richiesta ad ANAC - "+servizio);
      parameters.put(ScambioDatiPCPManager.LOG_EVENTO_REQ, logEvento);
    }else {
      logEvento.setCodEvento("NPA_RES_"+servizio);
      logEvento.setDescr("Risposta ANAC - "+servizio);
      logEvento.setErrmsg(result.toString());
      LogEventiUtils.insertLogEventi(logEvento);
    }
  }

}
