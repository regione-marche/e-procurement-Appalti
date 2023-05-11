package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import net.sf.json.JSONObject;

public class TracciamentoDownloadFS12Action extends Action {


  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");


    try {
      String idprg = request.getParameter("idprg");
      String iddocdg = request.getParameter("iddocdg");
      String gara = request.getParameter("gara");
      String comkey1 = request.getParameter("comkey1");

      String descr = "Download file allegati a comunicazione in ingresso (id: " + idprg + " - " + iddocdg + ", cod.operatore: " + comkey1 + ")";

      LogEvento logevento = LogEventiUtils.createLogEvento(request);
      logevento.setLivEvento(1);
      logevento.setOggEvento(gara);
      logevento.setCodEvento("GA_DOWNLOAD_DOCFS12");
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
