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

public class GetWSERPCarrelloAssociatoAction extends Action {

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

    String codice = request.getParameter("codice");

    String selCarrelloAssociato = "select count(*) from gcap where ngara = ? and codcarr is not null";
    Long counter = (Long) sqlManager.getObject(selCarrelloAssociato, new Object[] { codice });
    JSONObject jsonCarrelloAssociato = new JSONObject();
    if(counter > 0){
      jsonCarrelloAssociato.put("esito",true);
    }else{
      jsonCarrelloAssociato.put("esito",false);
    }

    out.print(jsonCarrelloAssociato);
    out.flush();

    return null;

  }
}

