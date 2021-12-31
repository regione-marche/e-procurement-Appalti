/*
 * Created on 23/gen/2014
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
import it.eldasoft.utils.utility.UtilityDate;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class EsistonoProdottiAssociatiAction extends Action {

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

    HashMap<String, Long> hMapResult = new HashMap<String, Long>();

    String selectV_SEL_MEPRODOTTI = "select count(*) "
        + " from v_sel_meprodotti, ditg "
        + " where v_sel_meprodotti.idartcat = ? "
        + " and v_sel_meprodotti.stato = 4 "
        + " and v_sel_meprodotti.datscadoff > ? "
        + " and ditg.dittao = v_sel_meprodotti.codimp "
        + " and ditg.ngara5 = v_sel_meprodotti.ngara "
        + " and ditg.abilitaz = 1";

    if (meartcat_id != null) {

      Long conteggio = (Long) this.sqlManager.getObject(selectV_SEL_MEPRODOTTI, new Object[] { meartcat_id,
          new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()) });
      if (conteggio == null )
        conteggio = new Long(0);

      hMapResult.put("numeroProdottiAssociati", conteggio);
    }

    JSONObject jsonResult = JSONObject.fromObject(hMapResult);
    out.println(jsonResult);

    out.flush();
    return null;
  }

}
