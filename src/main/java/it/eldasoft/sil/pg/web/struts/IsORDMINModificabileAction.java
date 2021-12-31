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

import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import net.sf.json.JSONObject;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class IsORDMINModificabileAction extends Action {

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

  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);
    
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String ngara = request.getParameter("ngara");
    String caisim = request.getParameter("caisim");

    HashMap<String, Boolean> hMapResult = new HashMap<String, Boolean>();

    // Verifica esistenza categorie "padre" con l'importo minimo ordine gia'
    // settato.
    String selectOPES_PADRE = "select count(*) from opes "
        + " where ngara3 = ? "
        + " and (catoff in (select codliv1 from cais where caisim = ?) "
        + " or catoff in (select codliv2 from cais where caisim = ?) "
        + " or catoff in (select codliv3 from cais where caisim = ?) "
        + " or catoff in (select codliv4 from cais where caisim = ?)) "
        + " and ordmin is not null";

    String selectOPES_FIGLIO = "select count(*) from opes "
        + " where ngara3 = ? "
        + " and catoff in "
        + " (select caisim from cais where codliv1 = ? or codliv2 = ? or codliv3 = ? or codliv4 = ? ) "
        + " and ordmin is not null ";

    Long conteggio_PADRE = (Long) sqlManager.getObject(selectOPES_PADRE, new Object[] { ngara, caisim, caisim, caisim, caisim });
    if (conteggio_PADRE != null && conteggio_PADRE.longValue() > 0) {
      hMapResult.put("isORDMINModificabile", Boolean.FALSE);
    } else {
      Long conteggio_FIGLIO = (Long) sqlManager.getObject(selectOPES_FIGLIO, new Object[] { ngara, caisim, caisim, caisim, caisim });
      if (conteggio_FIGLIO != null && conteggio_FIGLIO.longValue() > 0) {
        hMapResult.put("isORDMINModificabile", Boolean.FALSE);
      } else {
        hMapResult.put("isORDMINModificabile", Boolean.TRUE);
      }
    }

    JSONObject jsonResult = JSONObject.fromObject(hMapResult);
    out.println(jsonResult);

    out.flush();
    return null;
  }

}
