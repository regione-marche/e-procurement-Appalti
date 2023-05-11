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
 * Aggiorna il numero di ordine a seguito dello spostamento effettuato nella lista popup-ConfiguraDatiRichiestiDitte.jsp
 */


public class AggiornaNumOrdineConfigurazioneAction extends ActionBaseNoOpzioni {

  static Logger                 logger          = Logger.getLogger(AggiornaNumOrdineConfigurazioneAction.class);

  private SqlManager            sqlManager;

  private static final String UPDATE_NUMORD = "update garconfdati set numord=? where id=?";

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }



  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("AggiornaNumOrdineConfigurazione: inizio metodo");

    String messageKey = null;
    String target = "success";
    String listaId = new String(request.getParameter("listaId"));
    String listaNumord = new String(request.getParameter("listaNumord"));
    String ngara=new String(request.getParameter("ngara"));
    String bloccoModifica = new String(request.getParameter("bloccoModifica"));
    String direzione = new String(request.getParameter("direzione"));

    TransactionStatus status = null;
    boolean commit = true;

    try {
      status = this.sqlManager.startTransaction();
      String vetIdTmp[] = listaId.split(",");
      String vetNumord[] = listaNumord.split(",");


      String vetId[] = new String[vetIdTmp.length];
      if("asc".equals(direzione)){
        //Si deve riordinare la lista degli id, il primo elemento della lista deve diventare l'ultimo
        for(int j=0;j<vetIdTmp.length - 1;j++){
          vetId[j] = vetIdTmp[j+1];
        }
        vetId[vetIdTmp.length - 1] = vetIdTmp[0];
      }else{
        //Si deve riordinare la lista degli id, l'ultimo elemento della lista deve diventare il primo
        for(int j=0;j<vetIdTmp.length - 1;j++){
          vetId[j+1] = vetIdTmp[j];
        }
        vetId[0] = vetIdTmp[vetIdTmp.length - 1];
      }


      for(int i=0; i<vetId.length;i++){
        int tmpId = (new Long(vetId[i])).intValue();
        int tmpNumord = (new Long(vetNumord[i])).intValue();
        this.sqlManager.update(UPDATE_NUMORD, new Object[]{new Long(tmpNumord),new Long(tmpId)});
      }
    } catch (Throwable e) {
      commit = false;
      target = "error";
      messageKey = "errors.confifurazioneDati.aggiornaNumord.error";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey, e.getMessage());
    } finally {
      request.setAttribute("ngara", ngara);
      request.setAttribute("bloccoModifica", bloccoModifica);
      request.setAttribute("modalita", "vis");
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
      logger.debug("AggiornaNumOrdineConfigurazione: fine metodo");

    return mapping.findForward(target);

  }

}
