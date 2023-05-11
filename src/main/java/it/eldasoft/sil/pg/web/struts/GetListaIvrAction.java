package it.eldasoft.sil.pg.web.struts;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;

public class GetListaIvrAction extends DispatchActionBaseNoOpzioni {
   private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  
  static Logger                     logger          = Logger.getLogger(GetListaIvrAction.class);

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    if (logger.isDebugEnabled()) logger.debug("GetListaIvrAction: inizio metodo");

    String codiceDitta = request.getParameter("dittao");
    
    List<Object> listaIVR = new Vector<Object>();

    try {

      listaIVR = sqlManager.getListVector(
            "select IDCALCOLOROW, DATA_INSERIMENTO, IVR, IVR_DATA_INIZIO, IVR_DATA_FINE, SOSPENSIONE_DATA_INIZIO, SOSPENSIONE_DATA_FINE, SOSPENSIONE_REVOCATA"
            + " from IMPRVR"
            + " where CODIMP = ? order by IVR_DATA_INIZIO desc",
            new Object[] { codiceDitta });
      
      listaIVR = listaIVR.size()>7?listaIVR.subList(0, 7):listaIVR;

      if (listaIVR != null && listaIVR.size() > 0) {

        for (int i = 0; i < listaIVR.size(); i++) {
          Date dataIni = (Date) SqlManager.getValueFromVectorParam(listaIVR.get(i), 3).getValue();
          Date dataFin = (Date) SqlManager.getValueFromVectorParam(listaIVR.get(i), 4).getValue();
          Date dataOdierna = new Date();
          if(dataIni.before(dataOdierna) && dataOdierna.before(dataFin)) {
            Long idCalcoloRow = (Long) SqlManager.getValueFromVectorParam(listaIVR.get(i), 0).getValue();
            request.setAttribute("idCalcoloRow", idCalcoloRow.toString());
          }
        }
      }
          


      request.setAttribute("listaIVR", listaIVR);
      request.setAttribute("codiceDitta", codiceDitta);
        
    } catch (SQLException e) {
      String messageKey = "errors.ivr.error";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey, e.getMessage());
    }


    if (logger.isDebugEnabled()) logger.debug("GetListaIvrAction: fine metodo");
    
    return mapping.findForward("success");
  }


}
