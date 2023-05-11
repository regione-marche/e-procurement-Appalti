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
 * Aggiorna il numero di ordine a seguito dello spostamento effettuato nella lista popup-ModificaOrdinamentoDocStipula.jsp
 */


public class AggiornaNumOrdineDocStipulaAction extends ActionBaseNoOpzioni {

  static Logger                 logger          = Logger.getLogger(AggiornaNumOrdineDocStipulaAction.class);

  private SqlManager            sqlManager;

  private static final String UPDATE_NUMORD = "update G1DOCSTIPULA set NUMORD = ? where IDSTIPULA=? and ID=?";

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }



  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("AggiornaNumOrdineDocStipula: inizio metodo");

    String messageKey = null;
    String target = "success";
    String listaId = new String(request.getParameter("listaId"));
    String listaNumord = new String(request.getParameter("listaNumord"));
    String idStipula=new String(request.getParameter("idStipula"));
    String direzione = new String(request.getParameter("direzione"));

    TransactionStatus status = null;
    boolean commit = true;

    try {
      status = this.sqlManager.startTransaction();
      String vetNorddocgTmp[] = listaId.split(",");
      String vetNumord[] = listaNumord.split(",");


      String vetNorddocg[] = new String[vetNorddocgTmp.length];
      if("asc".equals(direzione)){
        //Si deve riordinare la lista degli Norddocg, il primo elemento della lista deve diventare l'ultimo
        for(int j=0;j<vetNorddocgTmp.length - 1;j++){
          vetNorddocg[j] = vetNorddocgTmp[j+1];
        }
        vetNorddocg[vetNorddocgTmp.length - 1] = vetNorddocgTmp[0];
      }else{
        //Si deve riordinare la lista degli Norddocg, l'ultimo elemento della lista deve diventare il primo
        for(int j=0;j<vetNorddocgTmp.length - 1;j++){
          vetNorddocg[j+1] = vetNorddocgTmp[j];
        }
        vetNorddocg[0] = vetNorddocgTmp[vetNorddocgTmp.length - 1];
      }


      for(int i=0; i<vetNorddocg.length;i++){
        int tmpNorddocg = (new Long(vetNorddocg[i])).intValue();
        int tmpNumord = (new Long(vetNumord[i])).intValue();
        this.sqlManager.update(UPDATE_NUMORD, new Object[]{new Long(tmpNumord), idStipula, new Long(tmpNorddocg)});
      }
    } catch (Throwable e) {
      commit = false;
      target = "error";
      messageKey = "errors.confifurazioneDati.aggiornaNumord.error";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey, e.getMessage());
    } finally {
      request.setAttribute("idStipula", idStipula);
      //request.setAttribute("bloccoModifica", bloccoModifica);
      //request.setAttribute("modalita", "vis");
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
      logger.debug("AggiornaNumOrdineDocStipula: fine metodo");

    return mapping.findForward(target);

  }

}
