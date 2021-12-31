/*
 * Created on 05/09/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class IsQuantitaArticoloPerUMAction extends Action {

  /**
   * Manager per la gestione delle interrogazioni di database.
   */
  private SqlManager sqlManager;

  /**
   * @param sqlManager
   *        the sqlManager to set
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    Long meartcat_id = null;
    if (request.getParameter("meartcat_id") != "") {
      meartcat_id = new Long(request.getParameter("meartcat_id"));
    }

    HashMap<String, Boolean> hMapResult = new HashMap<String, Boolean>();

    String select = "select przunitper"
        + " from meartcat where id=?";

    if (meartcat_id != null) {

      Long pzrunitper = (Long) this.sqlManager.getObject(select, new Object[] { meartcat_id});
      if (pzrunitper != null && pzrunitper.longValue() == 4) {
        hMapResult.put("isQuantitaArticoloPerUM", Boolean.TRUE);
      } else {
        hMapResult.put("isQuantitaArticoloPerUM", Boolean.FALSE);
      }
    }

    JSONObject jsonResult = JSONObject.fromObject(hMapResult);
    out.println(jsonResult);

    out.flush();
    return null;
  }

}
