package it.eldasoft.sil.pg.web.struts.permessi;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

public class SetPermessiRicercaMercatoAction extends Action {

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
	    Long id = new Long(request.getParameter("id"));
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
	      codric = (String)sqlManager.getObject("select codric from meric where id = ? ", new Object[] { id });
	      codEvento = "GA_ASSEGNA_PERMESSI";

	      if ("DELETE".equals(operation)) {
	        String deleteG_PERMESSI = "delete from g_permessi where idmeric = ? and syscon = ?";
	        this.sqlManager.update(deleteG_PERMESSI, new Object[] { id, syscon });
	        descEvento+= "Rimossi i privilegi per l'utente '"+ utente +"'("+syscon+")";

	      } else if ("INSERTUPDATE".equals(operation)) {

	        Long autori = new Long(request.getParameter("autori"));
	        String propri = request.getParameter("propri");
	        Long ruolo = new Long(request.getParameter("ruolo"));
	        
	        String selectG_PERMESSI = "select count(*) from g_permessi where syscon = ? and idmeric = ?";
	        Long cnt = (Long) this.sqlManager.getObject(selectG_PERMESSI, new Object[] { syscon, id });

	        if (cnt != null && cnt.longValue() > 0) {
	          String updateG_PERMESSI = "update g_permessi set autori = ?, propri = ?, meruolo = ? where syscon = ? and idmeric = ?";
	          this.sqlManager.update(updateG_PERMESSI, new Object[] { autori, propri, ruolo, syscon, id });
	          descEvento+= "Modificati i privilegi dell'utente '"+ utente +"'("+syscon+"): ";
	        } else {
	          String insertG_PERMESSI = "insert into g_permessi (numper, syscon, autori, propri, idmeric, meruolo) values (?,?,?,?,?,?)";
	          Object[] obj = new Object[6];
	          obj[0] = _getNextNumper();
	          obj[1] = syscon;
	          obj[2] = autori;
	          obj[3] = propri;
	          obj[4] = id;
			  obj[5] = ruolo;
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
	        
	        if(ruolo!= null && ruolo.intValue() == 2){
	          descEvento+= ", ruolo = Punto istruttore";
	        }else if(ruolo!= null && ruolo.intValue() == 1){
	          descEvento+= ", ruolo = Punto ordinante";
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
	        logEvento.setOggEvento(codric);
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
