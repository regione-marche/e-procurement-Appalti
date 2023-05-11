package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.utility.UtilityDate;
import net.sf.json.JSONObject;

public class SetStatoStipulaAction extends Action {

  /**
   * Manager per la gestione delle interrogazioni di database.
   */
  private SqlManager sqlManager;

  /**
   * @param sqlManager
   *        the sqlManager to set
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String id = request.getParameter("id");
    String opz = request.getParameter("opz");
    Long statoStipula = null;
    String codStipula = null;
    Date datpubStipula = null;
    

    int livEvento = 1;
    String codEvento="";
    String descEvento="";
    String msgErr="";
    
    
    String selectStipula = "select codstipula,datpub from g1stipula where id = ?";
	Vector<?> datiStipula = sqlManager.getVector(selectStipula,new Object[] { id });
	if (datiStipula != null && datiStipula.size() > 0){
	  codStipula = SqlManager.getValueFromVectorParam(datiStipula, 0).stringValue();
	  datpubStipula = (Date) SqlManager.getValueFromVectorParam(datiStipula, 1).getValue();
	}

    if (id != null && opz !=null) {
      if("1".equals(opz)) {
        codEvento="GA_STIPULA_ATTIVA";
        descEvento = "Attivazione contratto";
        statoStipula = Long.valueOf(5);
      }else if("2".equals(opz)) {
        codEvento="GA_STIPULA_DISATTIVA";
        descEvento = "Disattivazione contratto";
        if(datpubStipula!=null) {
          	statoStipula = Long.valueOf(3);
        }else {
          	statoStipula = Long.valueOf(1);
        }
      }

      TransactionStatus status = null;
      boolean commitTransaction = false;
      JSONObject result = new JSONObject();
      String update="update G1STIPULA set STATO =? where ID=?";
      Object par[]=null;
      par = new Object[] {statoStipula, Long.valueOf(id)};

      try {
        status = this.sqlManager.startTransaction();
        this.sqlManager.update(update, par);
        commitTransaction = true;
      } catch (Exception e) {
        livEvento = 3;
        commitTransaction = false;
        msgErr = e.getMessage();
        throw e;
      } finally {
        if (status != null) {
          if (commitTransaction) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        }
        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(codStipula);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(descEvento);
        logEvento.setErrmsg(msgErr);
        LogEventiUtils.insertLogEventi(logEvento);
      }

      result.put("esito", Boolean.valueOf(commitTransaction));
      result.put("messaggio", msgErr);
      out.println(result);
      out.flush();
    }

    return null;

  }

}
