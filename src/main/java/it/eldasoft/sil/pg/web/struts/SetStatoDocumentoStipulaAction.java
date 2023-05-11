package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;
import org.apache.struts.config.ForwardConfig;
import org.springframework.transaction.TransactionStatus;

/**
 * Aggiorna lo stato di una stipula
 */


public class SetStatoDocumentoStipulaAction extends ActionBaseNoOpzioni {

  protected static final String FORWARD_SUCCESS = "aggstatosuccess";
  protected static final String FORWARD_ERROR   = "aggstatoerror";

  static Logger                 logger          = Logger.getLogger(SetStatoDocumentoStipulaAction.class);

  private SqlManager            sqlManager;

  private static final String UPDATE_STATO_G1DOCSTIPULA = "update G1DOCSTIPULA set STATODOC = ? where ID = ?";

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }



  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("SetStatoDocumentoStipula: inizio metodo");

    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
    Integer livEvento = 1;
    String messageKey = null;
    String target = FORWARD_SUCCESS;
    String idStipula=new String(request.getParameter("idStipula"));
    String codstipula = "";
    String listaDoc = "";
    String select_codstipula = "select codstipula from g1stipula where id = ?";

    TransactionStatus status = null;
    String[] listaIdsSelezionati;
    boolean commit = true;

    try {
      status = this.sqlManager.startTransaction();
      codstipula=(String)this.sqlManager.getObject(select_codstipula, new Object[] {idStipula});
      listaIdsSelezionati = request.getParameterValues("keys");
      if (listaIdsSelezionati != null) {
          for (int i = 0; i < listaIdsSelezionati.length; i++) {
              String valoriIdsSelezionati = listaIdsSelezionati[i];
                if (valoriIdsSelezionati != null ) {
                  String ids_prog = valoriIdsSelezionati.substring(valoriIdsSelezionati.indexOf(":")+1);
                  Long progIds = Long.valueOf(ids_prog).longValue();
                  this.sqlManager.update(UPDATE_STATO_G1DOCSTIPULA, new Object[]{new Long(1),progIds});
                  if(!listaDoc.equals("")){listaDoc = listaDoc+", ";}
                  listaDoc = listaDoc + ids_prog;
                }
          }
      }
      errMsgEvento = "";
      
    } catch (Throwable e) {
      commit = false;
      errMsgEvento = "Errore nell'aggiornamento dei documenti di stipula";
      livEvento = 3;
      listaDoc = "";
      target = FORWARD_ERROR;
      request.setAttribute("RISULTATO", "KO");
      messageKey = "errors.aggiornaDocStipula.error";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey, e.getMessage());
      return mapping.findForward(target);
    } finally {
      ;
      if (status != null) {
        try {
          if (commit == true) {
            this.sqlManager.commitTransaction(status);
            request.setAttribute("RISULTATO", "OK");
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        } catch (SQLException e) {

        }
      }
      //Tracciatura eventi
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(codstipula);
        logEvento.setCodEvento("GA_STIPULA_RICOMPILADOC");
        logEvento.setDescr("Riporta documenti in compilazione (id. doc. riportati in compilazione: " + listaDoc + ").");
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
      }
    }
    
    if (logger.isDebugEnabled())
      logger.debug("SetStatoDocumentoStipula: fine metodo");
    
    return mapping.findForward(target);

  }

}
