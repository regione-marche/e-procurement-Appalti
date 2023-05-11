package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.eldasoft.gene.bl.TabellatiManager;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.spring.UtilitySpring;
import net.sf.json.JSONObject;

public class TracciamentoDownloadDocgaraAction extends Action {


  private SqlManager sqlManager;
  private TabellatiManager tabellatiManager;
 
  /**
   * Set SqlManager
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
      this.sqlManager = sqlManager;
  }
  
  /**
   * @param fileAllegatoManager
   *        the fileAllegatoManager to set
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

@Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

	  
    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");


    try {

      String idprg = request.getParameter("idprg");
      String iddocdg = request.getParameter("iddocdig");
      String key = request.getParameter("key");
      String tipologia = request.getParameter("tipologia");
      String gruppo = request.getParameter("gruppo");
      String busta = request.getParameter("busta");
      
      String descr = "Download file documento di gara (id: " + idprg + "/" + iddocdg; 
      if (!"".equals(tipologia) && tipologia!=null) {
      String SQL_GET_DESC_TIPO_DOC = "SELECT nome FROM g1cf_pubb WHERE id = ?";
      String descr_tipo_doc = (String) this.sqlManager.getObject(SQL_GET_DESC_TIPO_DOC, new Object[]{tipologia});
      descr += " - " + descr_tipo_doc;
      }
      if (!"".equals(gruppo) && gruppo!=null) {
      String descrgruppo = tabellatiManager.getDescrTabellato("A1064", gruppo);
      descr += " - " + descrgruppo;
      }
      if (!"".equals(busta) && gruppo!=null) {
      String descrbusta = tabellatiManager.getDescrTabellato("A1013", busta);
      descr += " - " + descrbusta;
      }
      descr += ")";
      
      LogEvento logevento = LogEventiUtils.createLogEvento(request);
      logevento.setLivEvento(1);
      logevento.setOggEvento(key);
      logevento.setCodEvento("GA_DOWNLOAD_DOCGARA");
      logevento.setDescr(descr);
      logevento.setErrmsg(null);
      LogEventiUtils.insertLogEventi(logevento);



    } catch (Exception e) {
      throw e;
    }


    PrintWriter out = response.getWriter();
    JSONObject result = new JSONObject();
    out.println(result);

    out.flush();
    return null;

  }
}
