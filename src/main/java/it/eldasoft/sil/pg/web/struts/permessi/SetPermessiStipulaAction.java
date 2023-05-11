package it.eldasoft.sil.pg.web.struts.permessi;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

public class SetPermessiStipulaAction extends Action {

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
	  public final ActionForward execute(final ActionMapping mapping, final ActionForm form,
			  final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		DataSourceTransactionManagerBase.setRequest(request);

	    String operation = request.getParameter("operation");
	    String id = request.getParameter("id");
	    Long idStipula = Long.valueOf(id);
	    String codstipula = request.getParameter("codstipula");
	    Long syscon = new Long(request.getParameter("syscon"));

	    TransactionStatus status = null;
	    boolean commitTransaction = false;

	    String descEvento = "";
	    String codEvento = "";
	    String errMsg = "";
	    String codric = "";
	    int livello = 1;
	    try {
	      status = this.sqlManager.startTransaction();
	      String utente = (String)sqlManager.getObject("select sysute from usrsys where syscon = ? ", new Object[] { syscon });
	      codEvento = "GA_ASSEGNA_PERMESSI";

	      if ("DELETE".equals(operation)) {
	        String deleteG_PERMESSI = "delete from g_permessi where idstipula = ? and syscon = ?";
	        this.sqlManager.update(deleteG_PERMESSI, new Object[] { idStipula, syscon });
	        descEvento+= "Rimossi i privilegi per l'utente '"+ utente +"'("+syscon+")";

	      } else if ("INSERTUPDATE".equals(operation)) {

	        Long autori = new Long(request.getParameter("autori"));
	        String propri = request.getParameter("propri");

	        String selectG_PERMESSI = "select count(*) from g_permessi where syscon = ? and idstipula = ?";
	        Long cnt = (Long) this.sqlManager.getObject(selectG_PERMESSI, new Object[] { syscon, idStipula });

	        if (cnt != null && cnt.longValue() > 0) {
	          String updateG_PERMESSI = "update g_permessi set autori = ?, propri = ? where syscon = ? and idstipula = ?";
	          this.sqlManager.update(updateG_PERMESSI, new Object[] { autori, propri, syscon, idStipula });
	          descEvento+= "Modificati i privilegi dell'utente '"+ utente +"'("+syscon+"): ";
	        } else {
	          String insertG_PERMESSI = "insert into g_permessi (numper, syscon, autori, propri, idstipula) values (?,?,?,?,?)";
	          Object[] obj = new Object[5];
	          obj[0] = _getNextNumper();
	          obj[1] = syscon;
	          obj[2] = autori;
	          obj[3] = propri;
	          obj[4] = idStipula;
	          this.sqlManager.update(insertG_PERMESSI, obj);
              descEvento+= "Assegnati i privilegi per l'utente '"+ utente +"'("+syscon+"): ";
	        }
	        descEvento+= "lettura = SI";
	        if(autori!= null && autori.intValue() == 1){
	          descEvento+= ", scrittura = SI";
	        }else{
	          descEvento+= ", scrittura = NO";
	        }

	        if("1".equals(propri)){
	          descEvento+= ", controllo completo = SI";
	        }else{
	          descEvento+= ", controllo completo = NO";
	        }

	      }

          livello = 1;
	      commitTransaction = true;
	    } catch (Exception e) {
	      commitTransaction = false;
	      errMsg = e.getMessage();
	      livello = 3;
	    } finally {
	      if (status != null) {
	        if (commitTransaction) {
	          this.sqlManager.commitTransaction(status);
	        } else {
	          this.sqlManager.rollbackTransaction(status);
	        }
	        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
	        logEvento.setLivEvento(livello);
	        logEvento.setOggEvento(codstipula);
	        logEvento.setCodEvento(codEvento);
	        logEvento.setDescr(descEvento );
	        logEvento.setErrmsg(errMsg);
	        LogEventiUtils.insertLogEventi(logEvento);
	      }
	    }
	    return null;
	  }

	  /**
	   *
	   * @return
	   * @throws SQLException
	   */
	  private Long _getNextNumper() throws SQLException {
	    Long nextNumper = (Long) this.sqlManager.getObject("select max(numper) from g_permessi", new Object[] {});
	    if (nextNumper == null) nextNumper = new Long(0);
	    nextNumper = new Long(nextNumper.longValue() + 1);
	    return nextNumper;
	  }

}
