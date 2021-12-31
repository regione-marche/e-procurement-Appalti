package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetDatiDitteRilanciOffEcoAction extends Action {

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

    //JSONArray jsonArray = new JSONArray();
    JSONObject result = new JSONObject();
    List<HashMap<String, Object>> hMap = new ArrayList<HashMap<String, Object>>();

    String ngara = request.getParameter("ngara");
    String modlicg = request.getParameter("modlicg");
    Long formato = null;

    Long ribcal = (Long)sqlManager.getObject("select ribcal from gare where ngara=?", new Object[]{ngara});

    if("6".equals(modlicg)){
      Long idG1cridef = null;
      idG1cridef = (Long)this.sqlManager.getObject("select id from g1cridef where ngara=? and formato=51", new Object[]{ngara});
      if(idG1cridef==null){
        idG1cridef = (Long)this.sqlManager.getObject("select id from g1cridef where ngara=? and formato=50", new Object[]{ngara});
        if(idG1cridef==null){
          idG1cridef = (Long)this.sqlManager.getObject("select id from g1cridef where ngara=? and formato=52", new Object[]{ngara});
          formato= new Long(52);
        }else
          formato=new Long(50);
      }else
        formato=new Long(51);
    }

    try {

      // 0 - Codice ditta
      // 1 - Numero ordine
      // 2 - nomimo
      // 3 - numero rilanci
      String select = "select d.dittao, "  //0
          + "d.numordpl, "                 //1
          + "d.nomimo, "                   //2
          + "(select count(g.id) from garilanci g where d.ngara5=g.ngara and d.dittao=g.dittao and g.numril <> -1) "  //3
          + "from ditg d "
          + "where d.NGARA5 = ? "
          + "and (d.INVOFF in ('0', '1') or d.INVOFF is null) "
          + "and (d.FASGAR > 5 or d.FASGAR = 0 or d.FASGAR is null) "
          + "order by d.numordpl" ;

      List<?> datiDitte = sqlManager.getListVector(select, new Object[] { ngara });
      if (datiDitte != null && datiDitte.size() > 0) {

        for (int i = 0; i < datiDitte.size(); i++) {
          HashMap<String, Object> hMapDitta = new HashMap<String, Object>();
          hMapDitta.put("ditta", SqlManager.getValueFromVectorParam(datiDitte.get(i), 0).getValue());
          hMapDitta.put("numordpl", SqlManager.getValueFromVectorParam(datiDitte.get(i), 1).getValue());
          hMapDitta.put("nomimo", SqlManager.getValueFromVectorParam(datiDitte.get(i), 2).getValue());
          hMapDitta.put("numril", SqlManager.getValueFromVectorParam(datiDitte.get(i), 3).getValue());
          hMap.add(hMapDitta);
        }
        result.put("iTotalRecords", datiDitte.size());
        result.put("data", hMap);
        result.put("ribcal", ribcal);
        result.put("formato", formato);
     }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura delle informazioni delle ditte che hanno presentato offerta economica.", e);
    }

    out.println(result);
    out.flush();

    return null;

  }

}
