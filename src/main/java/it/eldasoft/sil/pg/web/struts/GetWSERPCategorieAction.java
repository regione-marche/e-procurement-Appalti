/*
 * Created on 22/02/2024
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;



import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.erp.RaiwayWSERPManager;
import it.eldasoft.utils.properties.ConfigManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author Alex.Mancini
 *
 */
public class GetWSERPCategorieAction extends ActionBaseNoOpzioni {

  /** Logger Log4J di classe. */
  private static Logger                          logger            = Logger.getLogger(GetWSERPCategorieAction.class);

  public static final String APPALTI_MS_RAI_WS_BASEURL = "appalti-ms.rai.ws.baseUrl";

  private RaiwayWSERPManager raiwayWSERPManager;
  
  public void setRaiwayWSERPManager(RaiwayWSERPManager raiwayWSERPManager){
    this.raiwayWSERPManager = raiwayWSERPManager;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("GetWSERPAlboFornitoriAction: inizio metodo");
    }

    PrintWriter out = response.getWriter();
    String messageKey = null;

    try {
      String url = ConfigManager.getValore(APPALTI_MS_RAI_WS_BASEURL);
      if (url == null || "".equals(url)) {
        throw new GestoreException("L'indirizzo per la connessione al web service non e' definito", "");
      }
      if (url.endsWith("/"))
        url = url.substring(0, url.length() - 1);

      url = url + "/getCategorieMerc";
      HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Authorization", this.raiwayWSERPManager.getAuthorization());
      conn.setRequestProperty("Content-Type", "application/json");

      BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      StringBuilder res = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        res.append(line);
      }
      reader.close();
      conn.disconnect();

      JSONObject listaCategorieJson = new JSONObject();
      JSONArray jsonArray = JSONArray.fromObject(res.toString());
      List<Vector<?>> risultatoListaCategorie = new ArrayList<>();
      for (int i = 0; i < jsonArray.size(); i++) {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        Vector<String> dati = new Vector<>();

        
        dati.add(jsonObject.getString("caisim"));
        dati.add(jsonObject.getString("descat"));
        
        String tiplavgKey = jsonObject.getString("tiplavg");
        String tiplavg = null;
        switch (tiplavgKey) {
        case "1":
          tiplavg = "Lavori";
          break;
        case "2":
          tiplavg = "Forniture";
          break;
        case "3":
          tiplavg = "Servizi";
          break;
        default:
          break;
        }
        dati.add(tiplavg);
        jsonObject.put("tipgen", tiplavg);
        dati.add(jsonObject.getString("quaobb"));
        dati.add(jsonObject.getString("acontec"));
        dati.add(jsonObject.getString("caisord"));
        dati.add(jsonObject.getString("titcat"));
        risultatoListaCategorie.add(dati);
        
        
      }

      listaCategorieJson.put("categorie", jsonArray);
      //request.setAttribute("listaCategorie", listaCategorieJson);

      out.print(listaCategorieJson);
      out.flush();

    } catch (Exception e) {
      messageKey = "errors.alboFornitoriLista.error";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey, e.getMessage());
    } catch (Throwable e) {
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (messageKey != null) {
      response.reset();
    }

    if (logger.isDebugEnabled()) {
      logger.debug("GetWSERPAlboFornitoriAction: fine metodo");
    }

    
    return null;

  }

  

}
