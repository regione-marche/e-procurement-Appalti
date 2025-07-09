/*
 * Created on 10/10/2024
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.sil.pg.bl.MEPAManager;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class CheckFS12RettificaInCorsoAction extends Action {

  static Logger       logger = Logger.getLogger(CheckFS12RettificaInCorsoAction.class);

  private MEPAManager mepaManager;

  public void setMepaManager(MEPAManager mepaManager) {
    this.mepaManager = mepaManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
                                     final HttpServletResponse response) throws Exception {

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();
    String ngara = request.getParameter("ngara");
    String comtipo = request.getParameter("comtipo");

    result.put("esitoCheckFS12Rettifica", mepaManager.checkFS12RettificaInCorso(ngara,comtipo));

    out.print(result);
    out.flush();

    return null;
  }
}