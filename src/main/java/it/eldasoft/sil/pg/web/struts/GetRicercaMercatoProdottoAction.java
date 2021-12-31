package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetRicercaMercatoProdottoAction extends Action {

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

    JSONArray jsonArray = new JSONArray();

    Long mericprod_id = new Long(request.getParameter("mericprod_id"));

    try {

      // 0 - Codice impresa
      // 1 - Denominazione impresa
      // 2 - Prezzo offerto
      // 3 - Percentuale IVA (valore numerico)
      String selectMERICPROD = "select mericprod.codimp, " // 0
          + " impr.nomest, " // 1
          + " mericprod.preoff, " // 2
          + " mericprod.perciva " // 3
          + " from mericprod, impr "
          + " where mericprod.id = ? "
          + " and mericprod.codimp = impr.codimp";

      List<?> datiMERICPROD = sqlManager.getListVector(selectMERICPROD, new Object[] { mericprod_id });
      if (datiMERICPROD != null && datiMERICPROD.size() > 0) {
        for (int i = 0; i < datiMERICPROD.size(); i++) {
          Vector<Object> vect = (Vector) datiMERICPROD.get(i);

          // 4 - Percentuale IVA (descrizione)
          Long mericprod_perciva = (Long) SqlManager.getValueFromVectorParam(datiMERICPROD.get(i), 3).getValue();
          vect.add(new JdbcParametro(JdbcParametro.TIPO_TESTO, this.getDescrizione("G_055", mericprod_perciva)));

        }
        
        jsonArray = JSONArray.fromObject(datiMERICPROD.toArray());  
        
      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura delle informazioni relative al prodotto.", e);
    }

    out.println(jsonArray);
    out.flush();

    return null;

  }

  /**
   * Ricava la descrizione.
   * 
   * @param tab1cod
   * @param tab1tip
   * @return
   * @throws Exception
   */
  private String getDescrizione(String tab1cod, Long tab1tip) throws Exception {
    String descrizione = null;
    if (tab1tip != null) {
      descrizione = (String) sqlManager.getObject("select tab1desc from tab1 where tab1cod = ? and tab1tip = ?", new Object[] { tab1cod,
          tab1tip });
    }
    return descrizione;
  }

}
