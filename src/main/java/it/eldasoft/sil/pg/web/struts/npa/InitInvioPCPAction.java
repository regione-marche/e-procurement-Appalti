/*
 * Created on 20/11/2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts.npa;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.sil.pg.bl.npa.ScambioDatiPCPManager;
import it.eldasoft.sil.pg.bl.utils.IntegrazioneIAManager;
import it.eldasoft.utils.properties.ConfigManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.Tabellato;
import org.exolab.castor.mapping.xml.Sql;
import org.springframework.transaction.TransactionStatus;

import static it.eldasoft.sil.pg.bl.npa.ScambioDatiPCPManager.checkInternalError;

/**
 * @author manuel.bridda
 *
 */
public class InitInvioPCPAction extends ActionBaseNoOpzioni {

  static Logger               logger = Logger.getLogger(InitInvioPCPAction.class);
  
  private TabellatiManager            tabellatiManager;
  
  private SqlManager                  sqlManager;

  private ScambioDatiPCPManager scambioDatiPCPManager;
  
  
  /**
   * @param sqlManager the sqlManager to set
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  public void setScambioDatiPCPManager(ScambioDatiPCPManager scambioDatiPCPManager) {
    this.scambioDatiPCPManager = scambioDatiPCPManager;
  }
  
  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    
    /*Controlli su AccountSSO, LoA ecc
     * 
     * request.setAttribute("noAuth",true")//TODO
     */
    
    List<Tabellato> lista = tabellatiManager.getTabellato("A1291");
    String codgar = null;
    String codiceGara = null;
    String cfRP = null;
    String cfDRP = null;
    String nomDRP = null;
    String nomRUP = null;
    String isIniz = null;
    String codtecDRP= null;
    Long statonpa=1L;
    Long idschedanparet=0L;
    String isEspd=null;
    String descrizioneG1CFNPA=null;
    Long iddocEspd=0L;
    String inviabile=null;
    String idprgEspd=null;
    try {
    String key = request.getAttribute("key").toString().split(";")[0].split("ID=N:")[1];
    Long idForm = Long.parseLong(key);
    Vector data = sqlManager.getVector("select G1CF_NPA.ISPUBBNAZ,G1CF_NPA.ISCIG,torn.CODGAR, t1.CFTEC,t2.CFTEC,t2.NOMTEC, G1CF_NPA.ISINIZ, G1CF_NPA.STATO_FINALE,"
        + "torn.iterga, torn.dtepar, torn.otepar, torn.dteoff, torn.oteoff, t2.nometei, t2.cogtei, torn.coddrp, g1npascheda.statonpa,g1npascheda.idschedanparet,  "
        + "G1CF_NPA.ISESPD, G1CF_NPA.DESCRIZIONE, g1npascheda.iddoc_espd, g1npascheda.wsresponse, t1.nometei, t1.cogtei, g1npascheda.inviabile, g1npascheda.idprg_espd "
        + "from g1cf_npa, g1npaform, g1npascheda, torn "
        + "left join tecni t1 on (t1.codtec=torn.codrup) left join tecni t2 on (t2.codtec=torn.coddrp) where torn.codgar=g1npaform.codgar and g1cf_npa.ID=g1npaform.IDCFNPA "
        + "and g1npaform.id=g1npascheda.IDFORM and g1npascheda.id=?",
        new Object[] {idForm});
    
    String ispubbnaz = SqlManager.getValueFromVectorParam(data, 0).toString();
    String iscig = SqlManager.getValueFromVectorParam(data, 1).toString();
    codgar = codiceGara = SqlManager.getValueFromVectorParam(data, 2).toString();
    if (codgar.startsWith("$"))
      codiceGara = codgar.substring(1);
    cfRP = SqlManager.getValueFromVectorParam(data, 3).toString();
    cfDRP = SqlManager.getValueFromVectorParam(data, 4).toString();
    nomDRP = StringUtils.trimToEmpty((String)SqlManager.getValueFromVectorParam(data, 13).toString())+ " "+StringUtils.trimToEmpty(SqlManager.getValueFromVectorParam(data, 14).toString());
    codtecDRP = SqlManager.getValueFromVectorParam(data, 15).toString();
    statonpa = SqlManager.getValueFromVectorParam(data, 16).longValue();
    idschedanparet = SqlManager.getValueFromVectorParam(data, 17).longValue();
    isEspd = SqlManager.getValueFromVectorParam(data, 18).stringValue();
    descrizioneG1CFNPA = SqlManager.getValueFromVectorParam(data, 19).stringValue();
    iddocEspd = SqlManager.getValueFromVectorParam(data, 20).longValue();
    idprgEspd = SqlManager.getValueFromVectorParam(data, 25).stringValue();

    String wsresponse=SqlManager.getValueFromVectorParam(data, 21).stringValue();
    if(StringUtils.isNotBlank(wsresponse)){
      JSONObject resp = JSONObject.fromObject(wsresponse);
      if (checkInternalError(resp,"GEN004"))
        request.setAttribute("timeoutRichiestaPrecedente", "true");
    }
      nomRUP = StringUtils.trimToEmpty((String)SqlManager.getValueFromVectorParam(data, 22).toString())+ " "+StringUtils.trimToEmpty(SqlManager.getValueFromVectorParam(data, 23).toString());
      inviabile=SqlManager.getValueFromVectorParam(data, 24).stringValue();
    
    isIniz = SqlManager.getValueFromVectorParam(data, 6).toString();
    Long statoFinale = SqlManager.getValueFromVectorParam(data, 7).longValue();
      for(int i=0;i<lista.size();) {
          int tab1tip = Integer.parseInt(lista.get(i).getTipoTabellato());
            if(!"1".equals(ispubbnaz) && tab1tip >20 && tab1tip<60 && tab1tip!=31 && tab1tip!=30) {//rimuoviamo gli stati specifici della pubblicazione, mai il 30 (confermato) e il 31 (recupero CIG)
              lista.remove(i);
            }
            else if(!"1".equals(iscig) && tab1tip==31) { //rimuoviamo lo stato dedicato al recupero CIG se non ne è previsto il recupero
              lista.remove(i);
            }else if(statoFinale != null && tab1tip > statoFinale) {//rimuoviamo gli stati superiori a quello finale
              lista.remove(i);
            }else {
              i++;
            }
      }
      request.setAttribute("iterga",SqlManager.getValueFromVectorParam(data,8).longValue());
      request.setAttribute("dtepar", SqlManager.getValueFromVectorParam(data,9).dataValue());
      request.setAttribute("otepar", SqlManager.getValueFromVectorParam(data,10).stringValue());
      request.setAttribute("dteoff", SqlManager.getValueFromVectorParam(data,11).dataValue());
      request.setAttribute("oteoff", SqlManager.getValueFromVectorParam(data,12).stringValue());

      if(descrizioneG1CFNPA.indexOf("Facoltativo")>0 && "1".equals(isEspd) && Long.valueOf(1L).equals(statonpa) && iddocEspd==null) {
        String sogliaAttivazioneDGUEObblString = tabellatiManager.getDescrSupplementare("A1z21", "1");
        if(StringUtils.isNotEmpty(sogliaAttivazioneDGUEObblString)) {
          try {
          Double sogliaAttivazioneDGUEObbl = Double.parseDouble(sogliaAttivazioneDGUEObblString);
          String getValmax ="select valmax from v_gare_importi where codgar=?";
          if(codgar.indexOf('$')!=0)
              getValmax+= " and ngara is null";

            Double valMax = null;
            Object valMaxObj = sqlManager.getObject(getValmax, new Object[] {codgar});
            if (valMaxObj instanceof Long) {
              valMax = ((Long) valMaxObj).doubleValue();
            } else {
              valMax = (Double) valMaxObj;
            }

          if(valMax>=sogliaAttivazioneDGUEObbl) {
            request.setAttribute("dgueObbligatorio", "true");
            request.setAttribute("sogliaAttivazioneDGUEObbl", sogliaAttivazioneDGUEObblString);
          }
          }catch(Exception e) {
            logger.error("Errore nella verifica dell'obbligatorietà del DGUE in base all'importo: "+e);
          }
        }
      }
      if(idprgEspd!=null){
        if(!scambioDatiPCPManager.isDGUEValid(idprgEspd,iddocEspd))
          request.setAttribute("alertIsNotDGUE", "Il file DGUE Request allegato non risulta valido.");
      }

      if(StringUtils.isNotBlank(ConfigManager.getValore("ws.pii-detector.url")) && Long.valueOf(1L).equals(statonpa) && "1".equals(isIniz) && idschedanparet==null) {//APPALTI-1910
        String alert = "Controllo dati personali mediante IA:  non sono stati rilevati dati personali nell'oggetto della procedura";
        boolean scriviSuG1CACHE = true;
        try {
          List<Vector> oggetti = sqlManager.getListVector("select destor,cast('tmp' as varchar(20)) from torn where codgar=? and destor is not null " +
                  "union all select oggetto,cast('tmp' as varchar(20)) from gareavvisi where codgar=? and oggetto is not null " +
                  "union all select oggetto,cast('tmp' as varchar(20)) from garealbo where codgar=? and oggetto is not null " +
                  "union all (select not_Gar,CASE WHEN codgar1 like '$%' THEN 'tmp' ELSE ngara END as ngara from gare where codgar1=? and codgar1<>ngara and not_Gar is not null order by ngara)", new Object[]{codgar, codgar, codgar, codgar});
          String oggettiUniti = "";
          HashMap<String, String> oggettiMap = new HashMap();
          if (oggetti != null && !oggetti.isEmpty()) {
            for (Vector v : oggetti) {
              String oggetto = sqlManager.getValueFromVectorParam(v, 0).stringValue();
              oggettiUniti += oggetto + " ";
              oggettiMap.put(sqlManager.getValueFromVectorParam(v, 1).stringValue(), oggetto);
            }
          }
          String sha256 = UtilityTags.genSha256(oggettiUniti);
          String alertCached = (String) sqlManager.getObject("select TESTO from G1CACHE_IA where HASH=?", new Object[]{sha256});

          if(StringUtils.isNotBlank(alertCached)) {
            alert = alertCached;
          } else{
            String uffint = (String) request.getSession().getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
            JSONObject result = IntegrazioneIAManager.getDatiSensibili(oggettiUniti, uffint);
            List<String> datisensibiliProcedura = new ArrayList<>();


            if (result.containsKey("errore")) {
              scriviSuG1CACHE=false;
              if ("KO".equals(result.getString("errore")))
                alert = "Controllo dati personali mediante IA: servizio momentaneamente non disponibile, controllare autonomamente di non aver inserito dati personali nell'oggetto della procedura";
              else
                alert = result.getString("errore");
            } else if (result.containsKey("array") && !result.getJSONArray("array").isEmpty()) {
              JSONArray array = result.getJSONArray("array");
              oggettiMap.forEach((chiave, value) -> {
                List<String> oggettiXLotto = new ArrayList<>();
                List<String> listaValoriGestiti = new ArrayList<>();
                if (StringUtils.isNotEmpty(value)) {
                  for (int i = 0; i < array.size(); i++) {
                    String text = array.getJSONObject(i).getString("text");
                    if (value.contains(text) && !listaValoriGestiti.contains(text)) {
                      oggettiXLotto.add(text + (" (" + array.getJSONObject(i).getString("type") + ")"));
                      listaValoriGestiti.add(text);
                    }
                  }
                }
                if (!oggettiXLotto.isEmpty()) {
                  if ("tmp".equals(chiave))
                    datisensibiliProcedura.add("- l'oggetto della procedura potrebbe contenere dati sensibili: " + String.join(", ", oggettiXLotto));
                  else
                    datisensibiliProcedura.add("- l'oggetto del lotto " + chiave + " potrebbe contenere dati sensibili: " + String.join(", ", oggettiXLotto));
                }
              });
              alert = "Controllo dati personali mediante IA: <br>" + String.join("<br>", datisensibiliProcedura);
            }
            if(scriviSuG1CACHE) {
              Long count = (Long) sqlManager.getObject("select count(1) from G1CACHE_IA where HASH=?", new Object[]{sha256});
              if (count == 0) { //Il controllo potrebbe essere necessario in caso di concorrenza se si sta eseguendo la verifica per la prima volta
                TransactionStatus status = this.sqlManager.startTransaction();
                sqlManager.update("insert into G1CACHE_IA (HASH, TESTO) values (?,?)", new Object[]{sha256, alert});
                this.sqlManager.commitTransaction(status);
              }
            }
          }
        } catch (Exception e) {
          logger.error(e);
          alert="Controllo dati personali mediante IA: servizio momentaneamente non disponibile, controllare autonomamente di non aver inserito dati personali nell'oggetto della procedura";
        }
        request.setAttribute("alertDatiSensibili", alert);
      }
    }catch(Exception e) {
      String messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }
    
    request.setAttribute("isIniz", isIniz);
    request.setAttribute("statiScheda", lista);
    request.setAttribute("codiceGara", codiceGara);
    request.setAttribute("RP", cfRP);
    request.setAttribute("DRP", cfDRP);
    request.setAttribute("NOMRUP",nomRUP);
    request.setAttribute("NOMDRP", nomDRP);
    request.setAttribute("codtecDRP", codtecDRP);
    request.setAttribute("statonpa",statonpa);
    request.setAttribute("idschedanparet",idschedanparet);
    request.setAttribute("isEspd",isEspd);
    request.setAttribute("descrizioneG1CFNPA",descrizioneG1CFNPA);
    request.setAttribute("iddocEspd",iddocEspd);
    request.setAttribute("inviabileMRP", inviabile);
    
    
    return mapping.findForward(CostantiGeneraliStruts.FORWARD_OK);
  }
}
