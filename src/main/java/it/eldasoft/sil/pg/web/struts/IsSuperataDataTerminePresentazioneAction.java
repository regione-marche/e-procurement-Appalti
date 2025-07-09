/*
 * Created on 12/06/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.MEPAManager;
import net.sf.json.JSONObject;

public class IsSuperataDataTerminePresentazioneAction extends Action {

  private MEPAManager mepaManager;

  public void setMepaManager(MEPAManager mepaManager) {
    this.mepaManager = mepaManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    String ngara = request.getParameter("ngara");
    String tipo = request.getParameter("tipo");

    String campoData = "dteoff";
    String campoOra = "oteoff";
    if("2".equals(tipo)){
      campoData = "dtepar";
      campoOra = "otepar";
    }
    String isSuperataDataTerminePresentazione = "false";

    String esitoControllo [] = mepaManager.controlloDataConDataAttuale(ngara, campoData, campoOra);
    isSuperataDataTerminePresentazione =  esitoControllo[0];
    result.put("isSuperataDataTerminePresentazione", isSuperataDataTerminePresentazione);
    if("false".equals(isSuperataDataTerminePresentazione)){
      result.put("dataScadenza", esitoControllo [1]);
      result.put("oraScadenza", esitoControllo [2]);
    }

    out.print(result);
    out.flush();

    return null;

  }
}
