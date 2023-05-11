package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import net.sf.json.JSONObject;

public class GetBustaDaStepAction extends Action {


  private TabellatiManager tabellatiManager;
  private PgManagerEst1 pgManagerEst1;

  /**
   * @param fileAllegatoManager
   *        the fileAllegatoManager to set
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  public void setPgManagerEst1(PgManagerEst1 pgManagerEst1) {
    this.pgManagerEst1 = pgManagerEst1;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    String stepWizard = request.getParameter("stepWizard");
    String genereGara = request.getParameter("genereGara");
    Long busta = null;
    String bustaDescr = null;
    //Nel caso di elenchi e cataloghi non si deve valorizzare il campo busta
    //Long step = new Long(stepWizard);
    if (!"10".equals(genereGara) && !"20".equals(genereGara)) {
      busta = pgManagerEst1.getValoreBusta(stepWizard);
    }
    if (busta != null){
      bustaDescr = tabellatiManager.getDescrTabellato("A1013", busta.toString());
    }
    result.put("busta", busta);
    result.put("bustaDescr", bustaDescr);

    out.println(result);
    out.flush();

    return null;

  }
}
