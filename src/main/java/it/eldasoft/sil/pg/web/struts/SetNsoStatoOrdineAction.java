package it.eldasoft.sil.pg.web.struts;

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

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;

/**
 * Aggiorna lo stato di un ordine NSO
 */


public class SetNsoStatoOrdineAction extends ActionBaseNoOpzioni {

  protected static final String FORWARD_SUCCESS = "aggstordsuccess";
  protected static final String FORWARD_ERROR   = "aggstorderror";

  static Logger                 logger          = Logger.getLogger(SetNsoStatoOrdineAction.class);

  private SqlManager            sqlManager;

  private static final String UPDATE_STATO_ORDINE = "update NSO_ORDINI set STATO_ORDINE = ? where ID = ?";

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }



  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("SetNsoStatoOrdine: inizio metodo");

    String messageKey = null;
    String target = FORWARD_SUCCESS;
    String idOrdine=new String(request.getParameter("idOrdine"));
    String statoOrdine=new String(request.getParameter("statoOrdine"));

    TransactionStatus status = null;
    boolean commit = true;

    try {
      status = this.sqlManager.startTransaction();
      this.sqlManager.update(UPDATE_STATO_ORDINE, new Object[]{new Long(statoOrdine),new Long(idOrdine)});
    } catch (Throwable e) {
      commit = false;
      target = FORWARD_ERROR;
      request.setAttribute("RISULTATO", "KO");
      messageKey = "errors.confifurazioneDati.aggiornaNumord.error";
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
    }


    ForwardConfig config = mapping.findForwardConfig(target);
    ActionRedirect redirect = new ActionRedirect(config);
    redirect.addParameter("metodo", "apri");
    redirect.addParameter("activePage", "0");
    redirect.addParameter("key", "NSO_ORDINI.ID=N:"+idOrdine);
    redirect.addParameter("jspPath","/WEB-INF/pages/gare/nso_ordini/nso_ordini-lista.jsp");
//    redirect.addParameter("jspPathTo","");
    redirect.addParameter("isPopUp","0");
    redirect.addParameter("entita","NSO_ORDINI");
    redirect.setRedirect(true);
    if (logger.isDebugEnabled())
      logger.debug("SetNsoStatoOrdine: fine metodo");
    return redirect;

  }

}
