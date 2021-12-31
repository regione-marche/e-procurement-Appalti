package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetArchivioCategorieDipendentiAction extends Action {

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

  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String caisim = request.getParameter("caisim");
    out.println(this.getDipendenze(caisim));

    out.flush();
    return null;
  }

  /**
   * Verifica l'esistenza di dipendenze
   * 
   * @param caisim
   * @param jsonArray
   * @throws SQLException
   */
  private JSONArray getDipendenze(String caisim) throws SQLException {

    JSONArray jsonArray = new JSONArray();
    // Conteggio dipendenze per categorie figlie
    String selectCAIS = "select count(*) from cais where codliv1 = ? or codliv2 = ? or codliv3 = ? or codliv4 = ?";
    Long numeroCAIS = (Long) this.sqlManager.getObject(selectCAIS, new String[] { caisim, caisim, caisim, caisim });
    if (numeroCAIS != null && numeroCAIS.longValue() > 0) {
      Object[] row = new Object[3];
      row[0] = "CAIS";
      row[1] = numeroCAIS;
      row[2] = DizionarioTabelle.getInstance().getDaNomeTabella("CAIS").getDescrizione();
      jsonArray.add(row);
    }

    // Conteggio delle righe dipendenti su tutte le altre tabelle
    String tabella[] = { "CATE", "ARCHDOCG", "OPES", "CATG", "ISCRIZCAT", "ISCRIZCLASSI", "CATAPP", "ULTAPP", "SUBA", "CPRIGHE", "CNRIGHE" };
    String fktabella[] = { "CATISC", "CATEGORIA", "CATOFF", "CATIGA", "CODCAT", "CODCAT", "CATIGA", "CATOFF", "CATLAV", "CATIGA", "CATEG" };

    for (int t = 0; t < tabella.length; t++) {
      if (this.sqlManager.isTable(tabella[t])) {
        String sql = "select count(*) from " + tabella[t] + " where " + fktabella[t] + " = ?";
        Long numero = (Long) this.sqlManager.getObject(sql, new Object[] { caisim });
        if (numero != null && numero.longValue() > 0) {
          Object[] row = new Object[3];
          row[0] = tabella[t];
          row[1] = numero;
          row[2] = DizionarioTabelle.getInstance().getDaNomeTabella(tabella[t]).getDescrizione();
          jsonArray.add(row);
        }
      }
    }

    return jsonArray;

  }

}
