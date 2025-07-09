package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionAjaxLogged;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GestisciListaConfidurAction extends DispatchActionAjaxLogged {

  static Logger  logger = Logger.getLogger(GestisciListaConfidurAction.class);

  private SqlManager sqlManager;
  
  private GenChiaviManager genChiaviManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  public final ActionForward getLista(final ActionMapping mapping,
      final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    JSONObject result = new JSONObject();

    if (logger.isDebugEnabled()) logger.debug("GestisciListaConfidurAction: inizio metodo");

    PrintWriter out = response.getWriter();
    JSONObject resp =  new JSONObject();
    JSONArray array =  new JSONArray();

    String select = "select t1.tab1tip, t1.tab1desc, t2.tab1tip, t2.tab1desc, g1.durata from tab1 t1 cross join tab1 t2 left join g1confidur g1 on g1.tipgar=t1.tab1tip and g1.critlic=t2.tab1tip "
        + "where t1.tab1cod='A2044' and t2.tab1cod='A2081' and (t1.tab1arc is null or t1.tab1arc='2') and (t2.tab1arc is null or t2.tab1arc='2') order by t1.tab1nord, t2.tab1nord";
    List<?> datiTabellati;
    try {
      datiTabellati = sqlManager.getListVector(select,null);
      for (int l = 0; l < datiTabellati.size(); l++) {
        JSONObject item = new JSONObject();

        Long tab1tipA2044 = (Long) SqlManager.getValueFromVectorParam(datiTabellati.get(l), 0).getValue();
        String tab1descA2044 = (String) SqlManager.getValueFromVectorParam(datiTabellati.get(l), 1).getValue();
        Long tab1tipA2081 = (Long) SqlManager.getValueFromVectorParam(datiTabellati.get(l), 2).getValue();
        String tab1descA2081 = (String) SqlManager.getValueFromVectorParam(datiTabellati.get(l), 3).getValue();
        Long durata = (Long) SqlManager.getValueFromVectorParam(datiTabellati.get(l), 4).getValue();

        item.put("tab1tipA2044", tab1tipA2044);
        item.put("tab1descA2044", tab1descA2044);
        item.put("tab1tipA2081", tab1tipA2081);
        item.put("tab1descA2081", tab1descA2081);
        item.put("durata",durata);

        array.add(item);   
      }
      resp.put("listaConfidur",array);
    }catch(SQLException e) {
      result.put("messaggio", "Si è verificato un errore.");
    }
    if (logger.isDebugEnabled()) logger.debug("GestisciListaConfidurAction: fine metodo");

    out.print(resp);
    out.flush();

    return null;

  }
  
  
  public ActionForward setLista(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String target = null;
    String messageKey = null;

    DataSourceTransactionManagerBase.setRequest(request);

    Long tipgar = Long.parseLong(request.getParameter("tipgar"));
    Long critlic = Long.parseLong(request.getParameter("critlic"));
    Long durata = Long.parseLong(request.getParameter("durata"));
    String operation  = request.getParameter("operation");

    try {

      TransactionStatus status = null;
      boolean commitTransaction = false;
      try {
        status = this.sqlManager.startTransaction();

        if ("DELETE".equals(operation)) {
            String delete = "delete from G1CONFIDUR where tipgar = ? and critlic = ?";
            this.sqlManager.update(delete, new Object[] { tipgar, critlic});
        } else if ("INSERTUPDATE".equals(operation)) {
            String select = "select count(*) from G1CONFIDUR where tipgar = ? and critlic = ?";
            Long cnt = (Long) this.sqlManager.getObject(select, new Object[] { tipgar, critlic});

            if (cnt != null && cnt.longValue() > 0) {
              String updateW_TAGSLIST = "update G1CONFIDUR set durata = ? where tipgar = ? and critlic = ?";
              this.sqlManager.update(updateW_TAGSLIST, new Object[] { durata, tipgar, critlic});
            } else {
              int id = genChiaviManager.getNextId("G1CONFIDUR");
              String insertW_TAGSLIST = "insert into G1CONFIDUR (id, tipgar, critlic, durata) values (?,?,?,?)";
              this.sqlManager.update(insertW_TAGSLIST, new Object[] {id, tipgar, critlic,durata});
            }
          }
        commitTransaction = true;
      } catch (Exception e) {
        commitTransaction = false;
      } finally {
        if (status != null) {
          if (commitTransaction) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        }
      }
    } catch (Throwable e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (target != null) {
      return mapping.findForward(target);
    } else {
      return null;
    }
  }
}

