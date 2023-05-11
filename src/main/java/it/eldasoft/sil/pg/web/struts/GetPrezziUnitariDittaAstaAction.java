package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetPrezziUnitariDittaAstaAction extends Action {

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
    List<HashMap<String, Object>> hMap = new ArrayList<HashMap<String, Object>>();

    String ngara = request.getParameter("ngara");
    String ditta = request.getParameter("ditta");
    String idString = request.getParameter("id");
    String unimis = null;
    try {

      // 0 - Codice articolo
      // 1 - Voce
      // 2 - Unità di misura
      // 3 - Quantita
      // 4 - Prezzo offerto
      // 5 - Importo offerto
      // 6 - Num. Progressivo
      String select = "select CODVOC, " //0
          + "VOCE, " //1
          + "UNIMIS, " //2
          + "QUANTIEFF, " //3
          + "PREOFF, " //4
          + "IMPOFF, "  //5
          + "CONTAF "  //6
          + "FROM V_GCAP_DPRE "
          + "WHERE NGARA = ? and COD_DITTA=? "
          + " order by NORVOC ASC, CONTAF ASC";

      List<?> datiPrezziUnitari = sqlManager.getListVector(select, new Object[] { ngara, ditta });
      if (datiPrezziUnitari != null && datiPrezziUnitari.size() > 0) {
       Vector datiAerilpre = null;
        Long contaf = null;
        select = "select preoff, impoff from aerilpre where idril=? and ngara=? and dittao=? and contaf=?";
        for (int i = 0; i < datiPrezziUnitari.size(); i++) {
          HashMap<String, Object> hMapprezzo = new HashMap<String, Object>();
          hMapprezzo.put("codvoc", SqlManager.getValueFromVectorParam(datiPrezziUnitari.get(i), 0).getValue());
          hMapprezzo.put("voce", SqlManager.getValueFromVectorParam(datiPrezziUnitari.get(i), 1).getValue());
          unimis = SqlManager.getValueFromVectorParam(datiPrezziUnitari.get(i), 2).stringValue();
          unimis = (String)sqlManager.getObject("select desuni from unimis where conta = -1 and tipo=?", new Object[]{unimis});
          hMapprezzo.put("unimis", unimis);
          hMapprezzo.put("quantieff", SqlManager.getValueFromVectorParam(datiPrezziUnitari.get(i), 3).getValue());
          contaf=sqlManager.getValueFromVectorParam(datiPrezziUnitari.get(i), 6).longValue();
          datiAerilpre = this.sqlManager.getVector(select, new Object[]{new Long(idString),ngara, ditta, contaf});
          if(datiAerilpre!=null && datiAerilpre.size()>0){
            hMapprezzo.put("preoff", SqlManager.getValueFromVectorParam(datiAerilpre, 0).getValue());
            hMapprezzo.put("impoff", SqlManager.getValueFromVectorParam(datiAerilpre, 1).getValue());
          }else{
            hMapprezzo.put("preoff", SqlManager.getValueFromVectorParam(datiPrezziUnitari.get(i), 4).getValue());
            hMapprezzo.put("impoff", SqlManager.getValueFromVectorParam(datiPrezziUnitari.get(i), 5).getValue());
          }
          hMap.add(hMapprezzo);
        }
        String nomimo = (String)sqlManager.getObject("select nomimo from ditg where ngara5=? and dittao=?", new Object[]{ngara, ditta});
        result.put("Ragsoc", nomimo);
        result.put("data", hMap);
      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura delle informazioni del dettaglio prezzi della ditta invitata all'asta.", e);
    }

    out.println(result);
    out.flush();

    return null;

  }

}
