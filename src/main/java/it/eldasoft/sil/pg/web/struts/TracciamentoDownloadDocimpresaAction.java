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

public class TracciamentoDownloadDocimpresaAction extends Action {


  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");


    try {
      String idprg = request.getParameter("idprg");
      String iddocdg = request.getParameter("iddocdg");
      String ngara = request.getParameter("ngara");
      String dittao = request.getParameter("dittao");
      String doctel = request.getParameter("doctel");
      String descr = "Download file presentato dall'operatore (id: " + idprg + " - " + iddocdg + ", cod.ditta: " + dittao;
      if("1".equals(doctel))
        descr += ", acquisito da portale";
      descr += ")";
      LogEvento logevento = LogEventiUtils.createLogEvento(request);
      logevento.setLivEvento(1);
      logevento.setOggEvento(ngara);
      logevento.setCodEvento("GA_DOWNLOAD_DOCIMPRESA");
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
