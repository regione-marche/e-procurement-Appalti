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

public class AggiungiDestinatariNotificheAction extends ActionBaseNoOpzioni {

  protected static final String FORWARD_ERRORE  = "aggiungidestinatarinotificheerror";
  protected static final String FORWARD_SUCCESS = "aggiungidestinatarinotifichesuccess";

  static Logger                 logger          = Logger.getLogger(AggiungiDestinatariNotificheAction.class);

  private SqlManager            sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("AggiungiDestinatariNotificheAction: inizio metodo");

    Long discid_p = new Long(request.getParameter("discid_p"));

    String target = FORWARD_SUCCESS;
    String messageKey = null;

    String insertW_DISCDEST = "insert into w_discdest (discid_p, discid, destnum, destid, destname, destmail) values (?,?,?,?,?,?)";

    TransactionStatus status = null;
    boolean commit = true;

    try {

      status = this.sqlManager.startTransaction();

      Long maxdestnum = (Long) this.sqlManager.getObject("select max(destnum) from w_discdest where discid_p = ? and discid = ?",
          new Object[] { discid_p, new Long(-1) });
      if (maxdestnum == null) maxdestnum = new Long(0);

      String[] listaDestinatariSelezionati = request.getParameterValues("keysEmail");
      if (listaDestinatariSelezionati != null) {
        for (int dest = 0; dest < listaDestinatariSelezionati.length; dest++) {
          String[] valoriDestinatariSelezionati = listaDestinatariSelezionati[dest].split(";");
          if (valoriDestinatariSelezionati.length >= 3) {
            String destid = valoriDestinatariSelezionati[0];
            String destname = valoriDestinatariSelezionati[1];
            String destmail = valoriDestinatariSelezionati[2];

            this.sqlManager.update(insertW_DISCDEST,
                new Object[] { discid_p, new Long(-1), new Long(maxdestnum + dest + 1), new Long(destid), destname, destmail });

          }
        }
      }

    } catch (Throwable t) {
      commit = false;
      target = FORWARD_ERRORE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);

    } finally {
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

    if (messageKey != null) response.reset();

    if (logger.isDebugEnabled()) logger.debug("AggiungiDestinatariNotificheAction: fine metodo");

    return mapping.findForward(target);

  }

}
