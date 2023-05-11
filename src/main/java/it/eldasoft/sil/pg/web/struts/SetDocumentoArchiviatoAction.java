package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.tasks.ArchiviazioneDocumentiManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import net.sf.json.JSONObject;

public class SetDocumentoArchiviatoAction extends Action {

  private SqlManager sqlManager;
  private ArchiviazioneDocumentiManager archiviazioneDocumentiManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

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

    String operation = request.getParameter("operation");

    String documentiAssociatiDB = ConfigManager.getValore("it.eldasoft.documentiAssociatiDB");

    String selectWSALLEGATI = "select count(*) from wsallegati where entita = ? and key1 = ? and key2 = ?";
    if ("INSERT".equals(operation)) {
      ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
              CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = new Long(profiloUtente.getId());


      String cos = request.getParameter("cos");
      String key1 = request.getParameter("k1");
      String key2 = request.getParameter("k2");
      String provenienza  = request.getParameter("provenienza");
      String entita = "W_DOCDIG";
      if("4".equals(provenienza) && !documentiAssociatiDB.equals("1")){
        entita = "C0OGGASS";
      }
      String idRichiesta = request.getParameter("idRichiesta");
      idRichiesta = UtilityStringhe.convertiNullInStringaVuota(idRichiesta);
      String stato = request.getParameter("stato");
      stato = UtilityStringhe.convertiNullInStringaVuota(stato);

      if(!"".equals(idRichiesta)){

        Long cnt = null;
        if(!"true".equals(cos) && !"21".equals(stato) && !"23".equals(stato)){
          cnt = (Long) this.sqlManager.getObject(selectWSALLEGATI, new Object[] { entita, key1, new Long(key2) });
        }
        if(cnt == null || cnt.longValue() == 0){

          DataSourceTransactionManagerBase.setRequest(request);



          //INSERIMENTO in GARDOC_WSDM
          archiviazioneDocumentiManager.insertDettJobArchiviazioneDocumenti(syscon,idRichiesta, key1, key2, provenienza, documentiAssociatiDB);
        }
      }

    }
    out.println(result);
    out.flush();
    return null;
  }

}
