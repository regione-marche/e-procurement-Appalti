package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ListavaloriSettoriAction extends Action {

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
  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String valoriSettori="";
    String codein = request.getParameter("codein");
    JSONArray jsonArray = new JSONArray();

    //si prelevano i valori dalla tabela UFFSET
    List listaValoriSettori = this.sqlManager.getListVector("select distinct(nomset), tab1desc, tab1nord, tab1tip from uffset,tab1 where codein =? and "
        + "datfin is null and nomset is not null and tab1cod=? and nomset=tab1tip order by tab1nord asc, tab1tip asc ", new Object[]{codein, "A1092"});
    if(listaValoriSettori!=null && listaValoriSettori.size()>0){
      String nomset=null;
      String desc=null;
      for(int i=0; i<listaValoriSettori.size();i++){
        nomset=SqlManager.getValueFromVectorParam(listaValoriSettori.get(i), 0).getStringValue();
        desc=SqlManager.getValueFromVectorParam(listaValoriSettori.get(i), 1).getStringValue();
        Object[] row = new Object[2];
        row[0] = nomset;
        row[1] = desc;
        jsonArray.add(row);
      }
    }
    out.println(jsonArray);

    out.flush();
    return null;
  }

}
