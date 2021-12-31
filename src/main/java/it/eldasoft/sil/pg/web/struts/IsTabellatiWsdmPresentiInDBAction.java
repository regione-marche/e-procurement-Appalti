/*
 * Created on 03/mar/2020
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


public class IsTabellatiWsdmPresentiInDBAction extends Action{

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
    
    String sistema = request.getParameter("sistema");
    String idconfi = request.getParameter("idconfi");
    
    JSONArray jsonArray = new JSONArray();
    String countTabellati = "select count(*) from wsdmtab";

    Long cnt = (Long) sqlManager.getObject(countTabellati, new Object[] {});
    if(cnt != null && cnt.intValue()>0){
      jsonArray.add(new Object[] { "TRUE" });
    }
    
    out.println(jsonArray);

    out.flush();
    return null;
  }
}
