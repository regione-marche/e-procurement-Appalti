package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.tasks.ArchiviazioneDocumentiManager;
import it.maggioli.eldasoft.ws.dm.WSDMDocumentoCollegaResType;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

public class AssociaDocumentiDittaProtocolloAction extends Action {

  private SqlManager          sqlManager;
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

    String username = request.getParameter("username");
    String ruolo = request.getParameter("ruolo");
    String tipoCollegamento = request.getParameter("tipocollegamento");
    String codgar = request.getParameter("codgar");
    String idconfi = request.getParameter("idconfi");
    int esitoAzione=1;
    long numeroElaborazioni=0;

    //Si devono elaborare i documenti delle ditte in stato 3 e 21
    String selectDocumenti = "select d.ngara, d.codimp, d.IDPRG, d.iddocdg, d.busta, g.id, d.codgar from v_gare_docditta d join w_docdig w on d.iddocdg=w.iddocdig and d.idprg=w.idprg "
          + " join  gardoc_wsdm g  on d.iddocdg=g.key2 and d.idprg=g.key1 and g.entita='W_DOCDIG' and g.stato_archiviazione in (3,21) and g.provenienza='3' "
          + " where d.codgar = ? and d.idprg is not null and d.iddocdg is not null and d.doctel='1' order by d.codgar, d.ngara, d.bustaord, d.codimp, d.norddoci";

    List listaDocumenti = this.sqlManager.getListVector(selectDocumenti, new Object[]{codgar});


      if(listaDocumenti!= null && listaDocumenti.size()>0){
        String ngara=null;
        String ditta=null;
        String idprg=null;
        Long iddocdg=null;
        Long busta=null;
        Long stato_archiviazione = null;
        String esito = null;
        Long idGardoc = null;
        WSDMDocumentoCollegaResType wdmDocumentoCollegaResType = null;
        TransactionStatus status = null;
        boolean commitTransaction = false;
        numeroElaborazioni = listaDocumenti.size();
        for(int i=0;i<listaDocumenti.size();i++){
          try{
            commitTransaction = false;
            status = this.sqlManager.startTransaction();
            ngara = SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 0).stringValue();
            ditta = SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 1).stringValue();
            idprg = SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 2).stringValue();
            iddocdg = SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 3).longValue();
            busta = SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 4).longValue();
            idGardoc = SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 5).longValue();
            wdmDocumentoCollegaResType = this.archiviazioneDocumentiManager.collegaDocumenti(ngara, codgar, ditta, tipoCollegamento, busta, null, idprg, iddocdg.toString(), username, ruolo,idconfi);
            if(wdmDocumentoCollegaResType!=null){
              if(wdmDocumentoCollegaResType.isEsito()){
                stato_archiviazione = new Long(20);
                esito = null;
              }else{
                stato_archiviazione = new Long(21);
                esito = wdmDocumentoCollegaResType.getMessaggio();
                esitoAzione = -1;
              }
              //Aggiorno lo stato della elaborazione positivo
              this.sqlManager.update("update gardoc_wsdm set stato_archiviazione = ?, esito = ? where id= ?",
                  new Object[]{ stato_archiviazione, esito, idGardoc });
              commitTransaction = true;
            }
          } catch(Exception e){
            //Se si presenta un errore nel ciclo non si deve bloccare
            commitTransaction = false;
            esitoAzione = -1;
          } finally {
            if (status != null) {
              if (commitTransaction) {
                this.sqlManager.commitTransaction(status);
              } else {
                this.sqlManager.rollbackTransaction(status);
              }
            }
          }
        }
        result.put("esito", new Long(esitoAzione));
        result.put("numeroElaborazioni", new Long(numeroElaborazioni));
      }else{
        esitoAzione = 0;
        result.put("esito", new Long(esitoAzione));
      }

    out.print(result);
    out.flush();

    return null;

  }

}
