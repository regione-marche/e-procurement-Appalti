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

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AvanzamentoApTecAJAXAction extends Action {

  private SqlManager sqlManager;

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

    JSONObject result = new JSONObject();

    String ngara = request.getParameter("ngara");
    String dittao = request.getParameter("dittao");
    String comtipo = "FS11B";

    String selectW_INVCOM = "select count(*) from w_invcom where comkey2 = ? and (comstato = '5' or comstato = '13') and comtipo = ?";
    Object par[]=null;
    if(dittao!=null && !"".equals(dittao)){
      selectW_INVCOM = "select count(*) from w_invcom, w_puser where usernome = comkey1 and comkey2 = ? "
          + "and  userkey1=? and comstato = '5' and comtipo = ?";
      par = new Object[] { ngara, dittao, comtipo };
    }else
      par = new Object[] { ngara, comtipo };

    Long cnt = (Long) sqlManager.getObject(selectW_INVCOM, par);

    result.put("cnt", cnt);

    out.print(result);
    out.flush();

    return null;

  }
}
