package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.tasks.ArchiviazioneDocumentiManager;

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetTrasferisciDocumentaleWSDMAction extends Action {

  private SqlManager sqlManager;
  private GeneManager geneManager;
  private GestioneWSDMManager gestioneWSDMManager;
  private ArchiviazioneDocumentiManager archiviazioneDocumentiManager;
  
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
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

    String codiceGara = request.getParameter("codgar");
    String idconfi = request.getParameter("idconfi");
    

    String integrazioneWSDM="0";
    String trasferisciDocumentaleWSDM="0";    
    String integrazioneConservazione="0";
    String trasferisciCOS="0";
    String trasferisciDigiP="0";


    try {
      boolean isIntegrazioneWSDMAttivaValida = gestioneWSDMManager.isIntegrazioneWSDMAttivaValida(GestioneWSDMManager.SERVIZIO_DOCUMENTALE, idconfi);
      if(isIntegrazioneWSDMAttivaValida)
        integrazioneWSDM="1";
    }catch (SQLException e) {
      throw new GestoreException("Errore nella verifica dell'integrazione WSDM Protocollo per la gara " + codiceGara, null, e);
    }

    integrazioneConservazione = archiviazioneDocumentiManager.getSistemaConservazione(codiceGara);
    
    if("1".equals(integrazioneWSDM)){
      //Verifico se la funzione risulta abilitata da profilo
      if (this.geneManager.getProfili().checkProtec((String) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS", "ALT.GARE.TrasferisciAlDocumentale")){
        trasferisciDocumentaleWSDM = "1";
      }else{
        trasferisciDocumentaleWSDM = "0";
      }

    }else{
      trasferisciDocumentaleWSDM = "0";
    }

    if("1".equals(integrazioneConservazione) && this.geneManager.getProfili().checkProtec((String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS", "ALT.GARE.TrasferisciCos")){
      trasferisciCOS = "1";
    }
    if("2".equals(integrazioneConservazione) && this.geneManager.getProfili().checkProtec((String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS", "ALT.GARE.TrasferisciDigiP")){
      trasferisciDigiP = "1";
    }
    
    JSONArray jsonArray = new JSONArray();

    jsonArray.add(new Object[] { trasferisciDocumentaleWSDM, trasferisciCOS, trasferisciDigiP});

    out.println(jsonArray);
    out.flush();

    return null;

  }

}
