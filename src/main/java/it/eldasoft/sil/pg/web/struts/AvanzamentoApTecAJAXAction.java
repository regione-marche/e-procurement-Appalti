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
import it.eldasoft.sil.pg.bl.PgManager;

import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AvanzamentoApTecAJAXAction extends Action {

  private SqlManager sqlManager;
  
  private PgManager pgManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  public void setPgManager(PgManager pgManager) {
    this.pgManager = pgManager;
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
      Vector datiTORN=sqlManager.getVector("select iterga,isfineconc from torn, gare where ngara=? and codgar=codgar1", new Object[] {ngara});
      if(datiTORN!=null && datiTORN.size()>0) {
        Long iterga= SqlManager.getValueFromVectorParam(datiTORN, 0).longValue();
        String isFineConc = SqlManager.getValueFromVectorParam(datiTORN, 1).stringValue();
        if((new Long(9).equals(iterga) || new Long(10).equals(iterga)) && !"1".equals(isFineConc)) {
          String idanonimo = (String) this.sqlManager.getObject("select idanonimo from DITG where dittao = ? and ngara5 = ?", new Object[] {dittao,ngara});
          try {
            String iddecodificato=this.pgManager.decodifica(idanonimo);
            if(iddecodificato.indexOf("#")>-1)
              dittao=this.pgManager.decodifica(idanonimo).split("#")[1];
          } catch (Exception e) {}
        }
      }
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
