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
 * Aggiorna il numero di ordine a seguito dello spostamento effettuato nella lista popup-ModificaOrdinamentoDocumentazione.jsp
 */


public class AggiornaNumOrdineDocumentazioneAction extends ActionBaseNoOpzioni {

  static Logger                 logger          = Logger.getLogger(AggiornaNumOrdineDocumentazioneAction.class);

  private SqlManager            sqlManager;

  //private static final String UPDATE_NUMORD = "update garconfdati set numord=? where id=?";
  private static final String UPDATE_NUMORD = "update DOCUMGARA set NUMORD = ? where CODGAR=? and NORDDOCG=?";

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }



  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("AggiornaNumOrdineDocumentazione: inizio metodo");

    String messageKey = null;
    String target = "success";
    String listaNorddocg = new String(request.getParameter("listaNorddocg"));
    String listaNumord = new String(request.getParameter("listaNumord"));
    String ngara=new String(request.getParameter("ngara"));
    //String bloccoModifica = new String(request.getParameter("bloccoModifica"));
    String direzione = new String(request.getParameter("direzione"));
    String codgar1=new String(request.getParameter("codgar1"));
    String tipoDoc=new String(request.getParameter("tipoDoc"));
    String genereGara=new String(request.getParameter("genereGara"));
    String isFaseInvito=new String(request.getParameter("isFaseInvito"));    
    String tipologia=new String(request.getParameter("tipologia"));  
    String esitoRicalcNumordDocGara=new String(request.getParameter("esitoRicalcNumordDocGara"));

    TransactionStatus status = null;
    boolean commit = true;

    try {
      status = this.sqlManager.startTransaction();
      String vetNorddocgTmp[] = listaNorddocg.split(",");
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
        this.sqlManager.update(UPDATE_NUMORD, new Object[]{new Long(tmpNumord), codgar1, new Long(tmpNorddocg)});
      }
    } catch (Throwable e) {
      commit = false;
      target = "error";
      messageKey = "errors.confifurazioneDati.aggiornaNumord.error";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey, e.getMessage());
    } finally {
      request.setAttribute("ngara", ngara);
      //request.setAttribute("bloccoModifica", bloccoModifica);
      //request.setAttribute("modalita", "vis");
      request.setAttribute("codgar1", codgar1);
      request.setAttribute("tipoDoc", tipoDoc);
      request.setAttribute("genereGara", genereGara);
      request.setAttribute("isFaseInvito", isFaseInvito);  
      request.setAttribute("tipologia", tipologia);  
      request.setAttribute("esitoRicalcNumordDocGara", esitoRicalcNumordDocGara);
      if (status != null) {
        try {
          if (commit == true) {
            this.sqlManager.commitTransaction(status);
            request.setAttribute("esitoAggiornaNumOrdineDocAction", "1");
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        } catch (SQLException e) {

        }
      }
    }


    if (logger.isDebugEnabled())
      logger.debug("AggiornaNumOrdineDocumentazione: fine metodo");

    return mapping.findForward(target);

  }

}
