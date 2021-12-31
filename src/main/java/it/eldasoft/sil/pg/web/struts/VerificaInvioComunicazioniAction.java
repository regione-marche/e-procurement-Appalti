/*
 * Created on 11/feb/2020
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.maggioli.eldasoft.ws.dm.WSDMMailInfo2Type;
import it.maggioli.eldasoft.ws.dm.WSDMMailInfoType;
import it.maggioli.eldasoft.ws.dm.WSDMVerificaMailResType;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


public class VerificaInvioComunicazioniAction extends Action {
  /**
   * Manager per la gestione delle interrogazioni di database.
   */

  private GestioneWSDMManager gestioneWSDMManager;

  /**
   * @param gestioneWSDMManager
   *        the gestioneWSDMManager to set
   */
  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }


  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    String idprg = request.getParameter("idprg");
    String idcom = request.getParameter("idcom");
    String idconfi = request.getParameter("idconfi");
    Long syscon =  new Long(profilo.getId());
    String wsdmLoginComune = ConfigManager.getValore(GestioneWSDMManager.PROP_WSDM_LOGIN_COMUNE + idconfi);
    if (wsdmLoginComune != null && "1".equals(wsdmLoginComune))
      syscon = new Long(-1);


    JSONArray jsonArray = new JSONArray();

    WSDMVerificaMailResType wsdmVerificaMailRes = gestioneWSDMManager.wsdmVerificaMail(new Long (syscon), idcom, idprg, idconfi);

    if(wsdmVerificaMailRes!=null){
      WSDMMailInfoType mailInfoDest[] = wsdmVerificaMailRes.getDestinatariMailInfo();
      for(int i=0;i<mailInfoDest.length;i++){

        JSONObject row = new JSONObject();
        String destinatarioDescr = mailInfoDest[i].getDestinatarioDescrizione();
        String destinatarioEmail = mailInfoDest[i].getDestinatarioEmail();
        row.put("destinatarioDescr", destinatarioDescr);
        row.put("destinatarioEmail", destinatarioEmail);

        WSDMMailInfo2Type mailInfoArray[] = mailInfoDest[i].getMailInfo2();
        JSONObject[] jsonArrayInterop = new JSONObject[mailInfoArray.length];

        for(int j=0;j<mailInfoArray.length;j++){
          JSONObject datiInterop = new JSONObject();
          WSDMMailInfo2Type singleRecord =  mailInfoArray[j];
          Calendar cal = singleRecord.getMessaggioDataOra();
          Date date = cal.getTime();
          String dataOra = UtilityDate.convertiData(date, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
          datiInterop.put("dataOra", dataOra);
          datiInterop.put("statoSpedizione", singleRecord.getStatoSpedizione());
          datiInterop.put("messaggioDirezione", singleRecord.getMessaggioDirezione());
          datiInterop.put("tipoSpedizione", singleRecord.getTipoSpedizione());
          jsonArrayInterop[j]= datiInterop;
        }
        row.put("interopArray",jsonArrayInterop);
        jsonArray.add(row);
      }

    }else{
      Object[] row = new Object[1];
      row[0] = "Dato non disponibile.";
      jsonArray.add(row);
    }
    out.println(jsonArray);

    out.flush();
    return null;
  }

}