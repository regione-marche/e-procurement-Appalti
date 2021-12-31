package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.tasks.ArchiviazioneDocumentiManager;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class SetRichiestaArchiviazioneCOSAction extends Action {

  private ArchiviazioneDocumentiManager archiviazioneDocumentiManager;

  public void setArchiviazioneDocumentiManager(ArchiviazioneDocumentiManager archiviazioneDocumentiManager) {
    this.archiviazioneDocumentiManager = archiviazioneDocumentiManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    Long syscon = null;
    Long tipo_archiviazione = null;
    Long _idRichiesta = null;

    String codgar = request.getParameter("codgar");

    syscon = null;
    tipo_archiviazione = new Long(3);

    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    syscon = new Long(profiloUtente.getId());;

    //INSERIMENTO in GARDOC_JOBS
    _idRichiesta = archiviazioneDocumentiManager.insertJobArchiviazioneDocumenti(syscon, codgar, "", "", "", "", "", "", "", "","","","", tipo_archiviazione,null);
    result.put("idRichiesta", _idRichiesta);

    out.println(result);
    out.flush();

    return null;
  }


}
