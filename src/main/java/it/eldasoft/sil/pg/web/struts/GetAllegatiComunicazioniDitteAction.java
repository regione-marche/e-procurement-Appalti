package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import net.sf.json.JSONObject;

public class GetAllegatiComunicazioniDitteAction extends Action {

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

    JSONObject result = new JSONObject();
    List<HashMap<String, Object>> hMap = new ArrayList<HashMap<String, Object>>();

    String idprg = request.getParameter("idprg");
    String idcom = request.getParameter("idcom");
    String comtipo = request.getParameter("comtipo");

    try {

      //Nella lista prodotta le colonne saranno le seguenti
      // 0 - Descrizione
      // 1 - Nome allegato
      // 2 - Progressivo allegato

      /*
       * dato che non siamo in grado di leggere i campi
       * di tipo TIMESTAMP come effettivamente TIMESTAMP (DATE + TIME)
       * dobbiamo forzare l'uso delle funzioni per trasformare il campo in stringa
       */
    	//Comunicazioni inviate
        String desdatinvString = sqlManager.getDBFunction("DATETIMETOSTRING",
            new String[] { "FIRMACHECKTS" });
    	
    	
      String select = "select DIGDESDOC, "
          + "DIGNOMDOC,"
          + "IDDOCDIG,"
          + "IDPRG,"
          + "FIRMACHECK,"
          + desdatinvString
          + " from W_DOCDIG where DIGENT = ? ";

      Object par[] = null;
      if("FS12".equals(comtipo)){
        select += "AND DIGKEY1 = ? ";
        par = new Object[]{"W_INVCOM", idcom};
      }else{
        select += "AND DIGKEY1 = ? AND DIGKEY2 = ? ";
        par = new Object[]{"W_INVCOM", idprg, idcom};
      }

      select += "order by IDDOCDIG";

      List allegati = this.sqlManager.getListVector(select, par);
      if (allegati != null && allegati.size() > 0) {
        for (int i = 0; i < allegati.size(); i++) {
          HashMap<String, Object> hMapAllegati = new HashMap<String, Object>();
          hMapAllegati.put("descrizione", SqlManager.getValueFromVectorParam(allegati.get(i), 0).getValue());
          hMapAllegati.put("nome", SqlManager.getValueFromVectorParam(allegati.get(i), 1).getValue());
          hMapAllegati.put("iddocdig", SqlManager.getValueFromVectorParam(allegati.get(i), 2).getValue());
          hMapAllegati.put("idprg", SqlManager.getValueFromVectorParam(allegati.get(i), 3).getValue());
          hMapAllegati.put("firmacheck", SqlManager.getValueFromVectorParam(allegati.get(i), 4).getValue());
          hMapAllegati.put("firmacheckts", SqlManager.getValueFromVectorParam(allegati.get(i), 5).getStringValue());
          hMap.add(hMapAllegati);
        }
      }

      result.put("data", hMap);

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura degli allegati della comunicazione.", e);
    }

    out.println(result);
    out.flush();

    return null;

  }
}
