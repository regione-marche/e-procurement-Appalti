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
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import net.sf.json.JSONObject;

public class GetDettaglioGareQformlibAction extends Action {

  private SqlManager sqlManager;

  private TabellatiManager tabellatiManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
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

    String id = request.getParameter("id");

    try {
      String dbdultaggmodString = sqlManager.getDBFunction("DATETIMETOSTRING",
          new String[] { "q.dultaggmod" });
      String select = "select v.genere, q.key1, v.oggetto, q.busta, " + dbdultaggmodString + ", q.stato,v.codgar from qform q, v_gare_genere v where q.idmodello = ? "
          + "and q.stato <>8 and q.entita='GARE' and v.codice=q.key1 order by v.genere, q.key1";

      List<?> datiGare = sqlManager.getListVector(select, new Object[] { new Long(id) });
      if(datiGare!=null && datiGare.size()>0){
        Long genere = null;
        Long busta = null;
        Long stato = null;
        String descTabellato=null;
        String codgar = null;
        String nomtec= null;
        for (int i = 0; i < datiGare.size(); i++) {
          HashMap<String, Object> hMapGara = new HashMap<String, Object>();
          genere = (Long) SqlManager.getValueFromVectorParam(datiGare.get(i), 0).getValue();
          if(new Long(10).equals(genere) || new Long(20).equals(genere))
            descTabellato = "Elenco";
          else
            descTabellato = "Gara";
          hMapGara.put("genere", descTabellato);
          hMapGara.put("codice", SqlManager.getValueFromVectorParam(datiGare.get(i), 1).getValue());
          hMapGara.put("oggetto", SqlManager.getValueFromVectorParam(datiGare.get(i), 2).getValue());
          if(new Long(10).equals(genere) || new Long(20).equals(genere)) {
            descTabellato=null;
          }else {
            busta = (Long) SqlManager.getValueFromVectorParam(datiGare.get(i), 3).getValue();
            descTabellato = this.tabellatiManager.getDescrTabellato("A1013", busta.toString()) ;
          }
          hMapGara.put("oggetto", SqlManager.getValueFromVectorParam(datiGare.get(i), 2).getValue());
          hMapGara.put("busta", descTabellato);
          hMapGara.put("data", SqlManager.getValueFromVectorParam(datiGare.get(i), 4).getValue());
          stato = (Long) SqlManager.getValueFromVectorParam(datiGare.get(i), 5).getValue();
          descTabellato = this.tabellatiManager.getDescrTabellato("A1061", stato.toString()) ;
          hMapGara.put("stato", descTabellato);
          codgar = SqlManager.getValueFromVectorParam(datiGare.get(i), 6).getStringValue();
          nomtec = (String)this.sqlManager.getObject("select nomtec from tecni , torn where codgar=? and codrup=codtec", new Object[] {codgar});
          hMapGara.put("nomtec", nomtec);
          hMap.add(hMapGara);
        }
      }
      result.put("data", hMap);

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura delle gare ed elenchi associati al qformlib " + id, e);
    }

    out.println(result);
    out.flush();

    return null;

  }

}
