package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.QeditorForm;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.tags.gestori.submit.GestoreDocumentazioneGara;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Vector;

public class SaveQFormEditorAction extends ActionBaseNoOpzioni {

  /**
   * Override dell'omonimo metodo di Geneweb per customizzazione
   */
  private SqlManager sqlManager;

  private GenChiaviManager genChiaviManager;

  /**
   * @param sqlManager
   *        the sqlManager to set
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   * @param genChiaviManager
   *        the genChiaviManager to set
   */
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }


  @Override
  @SuppressWarnings("unchecked")
  public final ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) throws IOException {

    DataSourceTransactionManagerBase.setRequest(request);

    String target = "success";
    String messageKey = null;

    QeditorForm qeditordForm = (QeditorForm) form;
    String id = request.getParameter("id");
    String ent = request.getParameter("ent");

    if (id != null && ent !=null) {
      TransactionStatus status = null;
      boolean commitTransaction = false;
      JSONObject result = new JSONObject();
      String update="update " + ent + " set oggetto =? where id=?";
      String msgErr="";
      try {

        String oggetto = qeditordForm.getJsonFile();
        if(oggetto!=null) {
          byte[] test = Base64.decodeBase64(oggetto);
          oggetto = new String(test, Charset.forName("UTF-8"));
        }
        status = this.sqlManager.startTransaction();
        this.sqlManager.update(update, new Object[] {oggetto, new Long(id)});
        //logica specifica di Appalti per cui è stato fatto l'override
        if("QFORM".equals(ent)){
          Vector dati = sqlManager.getVector("select g.ngara,g.iselprem from qform q,garealbo g where g.ngara=q.key1 and q.entita='GARE' and q.id=?", new Object[] {new Long(id)});
          if(dati!=null && !dati.isEmpty()){
            String ngara = SqlManager.getValueFromVectorParam(dati, 0).getStringValue();
            String iselprem = SqlManager.getValueFromVectorParam(dati, 1).getStringValue();
            sqlManager.update("delete from elpremalbo where idqform=?", new Object[]{new Long(id)});
            if("1".equals(iselprem)) {
              PgManager.importQuestions(ngara, new Long(id), sqlManager, genChiaviManager, JSONObject.fromObject(oggetto));
            }
          }
        }
        commitTransaction = true;
      } catch (Exception e) {
        commitTransaction = false;
        msgErr = e.getMessage();
        messageKey = "errors.applicazione.inaspettataException";
        this.aggiungiMessaggio(request, messageKey);
      } finally {
        if (status != null) {
          try {
          if (commitTransaction) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
          }catch(Exception e) {
            throw new IOException(e);
          }
        }
      }
      request.setAttribute("esito",new Boolean(commitTransaction));
      request.setAttribute("messaggio",msgErr);
    }

    if (messageKey != null) response.reset();

    return mapping.findForward(target);

  }

}
