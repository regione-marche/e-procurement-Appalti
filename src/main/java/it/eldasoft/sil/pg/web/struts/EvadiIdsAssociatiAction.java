package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

/**
 * Evade gli IDS associati alla gara
 */


public class EvadiIdsAssociatiAction extends ActionBaseNoOpzioni {

  static Logger                 logger          = Logger.getLogger(AggiornaNumOrdineConfigurazioneAction.class);

  private SqlManager            sqlManager;

  private static final String UPDATE_UTENTI_IDS = "update  utenti_ids ui1 set flag_evadi = ?" +
  		" where exists(select ui2.ids_prog from utenti_ids ui2,v_lista_gare_ids gi" +
  		" where ui2.ids_prog = gi.ids_prog and ui1.ids_prog=ui2.ids_prog and ui2.flag_evadi = 2 and gi.codice_gara = ? )";
  //eventualmente raffinare ancora : solo su quelli che valgono 2


  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }



  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("EvadiIdsAssociati: inizio metodo");

    String messageKey = null;
    String target = "success";
    String codiceGara=new String(request.getParameter("codiceGara"));
    String messaggioControllo = "ESECUZIONE_OK";

    TransactionStatus status = null;
    boolean commit = true;

    try {
        status = this.sqlManager.startTransaction();
        this.sqlManager.update(UPDATE_UTENTI_IDS, new Object[]{new Long(1),codiceGara});
    } catch (Throwable e) {
      commit = false;
      target = "error";
      messageKey = "errors.evadiIdsAssociati.error";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey, e.getMessage());
      messaggioControllo = "ESECUZIONE_KO";
    } finally {
      request.setAttribute("codiceGara", codiceGara);
      request.setAttribute("messaggioControllo", messaggioControllo);
      if (status != null) {
        try {
          if (commit == true) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        } catch (SQLException e) {

        }
      }
    }


    if (logger.isDebugEnabled())
      logger.debug("EvadiIdsAssociati: fine metodo");

    return mapping.findForward(target);

  }

}
