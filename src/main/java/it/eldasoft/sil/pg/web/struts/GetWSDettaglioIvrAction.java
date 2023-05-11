package it.eldasoft.sil.pg.web.struts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.sil.pg.bl.utils.JwtTokenUtilities;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetWSDettaglioIvrAction extends DispatchActionBaseNoOpzioni {
  
  private static final String APPVR_WS_URL     = "appVr.ws.url";
  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  
  static Logger                     logger          = Logger.getLogger(GetWSDettaglioIvrAction.class);

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {
    long start = System.currentTimeMillis();
    if (logger.isDebugEnabled()) logger.debug("GetWSDettaglioIvrAction: inizio metodo");
    try {
      String idCodiceRow = request.getParameter("idCodiceRow");
      String uffint = (String) request.getSession().getAttribute("uffint");
      uffint = StringUtils.stripToEmpty(uffint);
      
      String cfein = (String) this.sqlManager.getObject("select cfein from uffint where codein = ? ", new Object[] { uffint });
      cfein = UtilityStringhe.convertiNullInStringaVuota(cfein);
      
      String urlEndpoint = ConfigManager.getValore(APPVR_WS_URL);
      if (urlEndpoint == null || (urlEndpoint != null && "".equals(urlEndpoint.trim()))) {
        throw new Exception("Non e' definito l'indirizzo del servizio");
      }
      
      HashMap<String,String> data = new HashMap<String,String>();
      JSONObject obj = new JSONObject();
      obj.put("USER_CF", cfein);
  
      long expiration = System.currentTimeMillis() + 5 * 60000l;
      String chiaveSegretaJwtChiaro = JwtTokenUtilities.getChiaveSegretaJwtChiaro();
      String authorization = JwtTokenUtilities.generateToken("Appalti", expiration, chiaveSegretaJwtChiaro, data);
      
      Client client = ClientBuilder.newClient();
      WebTarget webTarget = client.target(urlEndpoint+"getCalcsRowsDetailsGrid?id="+idCodiceRow);
      JSONObject resp = webTarget.request(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, authorization).header("Content-Type", MediaType.APPLICATION_JSON).get(JSONObject.class);
      
      JSONArray lista = new ObjectMapper().readValue(resp.containsKey("indicators")?resp.getString("indicators"):"", JSONArray.class);
      
      List<Object> listaDettagliIVR = new ArrayList<Object>();
      if (lista != null) { 
         for (int i=0;i<lista.size();i++){ 
           String[] sObj = new String[4];
           JSONObject jObject = lista.getJSONObject(i);
           sObj[0] = !"null".equals(jObject.getString("id"))?jObject.getString("id"):"";
           sObj[1] = !"null".equals(jObject.getString("indicatorDesc"))?jObject.getString("indicatorDesc"):"";
           sObj[2] = !"null".equals(jObject.getString("indicatorValue"))?jObject.getString("indicatorValue"):"";
           sObj[3] = !"null".equals(jObject.getString("indicatorWeight"))?jObject.getString("indicatorWeight"):"";
           listaDettagliIVR.add(sObj);
         } 
      }
      
      request.setAttribute("objDettaglioIVR", resp); 
      request.setAttribute("listObjDettaglioIVR", listaDettagliIVR);
    } catch(ProcessingException e) {
      logger.error("Impossibile raggiungere il servizio getCalcsRowsDetailsGrid",e);
      String messageKey = "errors.wsesterno.dettaglioIVR";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch(Exception e) {
      logger.error("Impossibile effettuare redirect a GetWSDettaglioIvrAction.",e);
      String messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } finally {
      logger.info("GetWSDettaglioIvrAction executed: "+(System.currentTimeMillis()-start));
    }
    
    if (logger.isDebugEnabled()) logger.debug("GetWSDettaglioIvrAction: fine metodo");
    
    return mapping.findForward("success");
  }
  

}
