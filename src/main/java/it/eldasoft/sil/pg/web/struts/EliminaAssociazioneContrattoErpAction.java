/*
 * Created on 12/06/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;

import net.sf.json.JSONObject;

public class EliminaAssociazioneContrattoErpAction extends Action {

	private SqlManager sqlManager;	
	private GenChiaviManager genChiaviManager;	

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}
	
	public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
		this.genChiaviManager = genChiaviManager;
	}	

	@Override
	public final ActionForward execute(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		DataSourceTransactionManagerBase.setRequest(request);

		response.setHeader("cache-control", "no-cache");
		response.setContentType("text/text;charset=utf-8");

		PrintWriter out = response.getWriter();
		JSONObject result = new JSONObject();
		WSERPRdaType[] datiRisposta;

		String numeroContrattoErp = request.getParameter("numeroContrattoErp");
		String codGara      = request.getParameter("codGara");
		String idStipulaStr = request.getParameter("idStipula");
		String codStipula   = request.getParameter("codStipula");
		Long   idStipula    = ("".equals(idStipulaStr))? null: Long.valueOf(idStipulaStr);
		String idPadreStr   = request.getParameter("idPadre");
		Long idPadre        = ("".equals(UtilityStringhe.convertiNullInStringaVuota(idPadreStr)))? null: Long.valueOf(idPadreStr);
				
		boolean esito = true; 
		String messaggio = "";
		
        try {
        	if ( idStipula != null ) {
        		// caso variante di contratto
        		eliminaAssociazioneContrattoErp(codGara, idStipula, codStipula, numeroContrattoErp, idPadre);
        	} else {
        		eliminaGaraAssociazioneContrattoErp(codGara, numeroContrattoErp);
        	}
        } catch (SQLException e) {
        	esito = false;
        	messaggio = " Problemi con accesso a DB durant eliminazione contratto erp ";
        }
		    
	    result.put("esito", esito?"ok":"ko");
		result.put("risposta", messaggio);		

		out.print(result);
		out.flush();

		return null;

	}//execute

	private void eliminaGaraAssociazioneContrattoErp(String codGara, String numeroContrattoErp) throws SQLException {
	    TransactionStatus status = null;
	    boolean commit = false;		
	
        try {
            status = this.sqlManager.startTransaction();
            
            // recupero il cig...
            String sqlCig =       " select g.codcig "
			            		+ " from  gare g "
			            		+ " where g.ngara  = ? "
			            		+ "   and g.nrepat = ? ";
            
            String codCig = (String) this.sqlManager.getObject(sqlCig, new Object[]{codGara, numeroContrattoErp });             
            
            // elimino i dati inseriti in gara e stipula ( ordina importante perche uso la vista.. )
            String sqlUpdateGara = " update gare "
								 + "   set nrepat = null "
								 +"       ,daatto = null "
								 + " where ngara  = ? "
								 + "   and nrepat = ? ";

            this.sqlManager.update(sqlUpdateGara, new Object[]{codGara, numeroContrattoErp}); 
         
            // aggiorniamo la history ( = tabella G1CONT_ERP ) 
            
            // ..e aggiorno la tabella G1CONT_ERP
            Timestamp dataOdierna = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
            int id_G1CONT_ERP = this.genChiaviManager.getNextId("G1CONT_ERP");
            
            String sqlInsert =  " insert into G1CONT_ERP (id,origine, cig, codiceInterno, codiceSAP, operazione, data_operazione) "
                               +"                 values (? ,? ,? ,? ,? , ?, ?) ";
            
            String origine = "GAR";
            this.sqlManager.update(sqlInsert, new Object[]{id_G1CONT_ERP, origine, codCig, codGara, numeroContrattoErp, "D", dataOdierna });           
            
            commit = true;
            
        } catch (SQLException e) {    
      	  throw e;
        } finally {
            if (status != null) {
              try {
                if (commit) {
                  this.sqlManager.commitTransaction(status);
                } else {
                  this.sqlManager.rollbackTransaction(status);
                }
              } catch (SQLException e) {
            	  throw e;
              }
            }
          }
		
	}//eliminaGaraAssociazioneContrattoErp

	private void eliminaAssociazioneContrattoErp(String codGara, 
												 Long idStipula, 
												 String codStipula, 
												 String numeroContrattoErp,
												 Long idPadre) throws SQLException {		
		
	    TransactionStatus status = null;
	    boolean commit = false;		
	
        try {
            status = this.sqlManager.startTransaction();
            
            // recupero il cig...
            String sqlCig =       " select g.codcig "
			            		+ " from  gare g "
			            		+ " where 1=1  "
			            		+ "  and g.ngara = ( "
			            		+ "				     select codlotto "
			            		+ "				     from  v_gare_stipula vgs "
			            		+ "				     where 1=1 "
			            		+ "				       and vgs.id = ? "
			            		+ "                 )  ";
            
            String codCig = (String) this.sqlManager.getObject(sqlCig, new Object[]{idStipula}); 
            
            // recupero lo stato della polizza
            String sqlStato = "select stato from g1stipula where id=? ";
            Long stato = (Long) this.sqlManager.getObject(sqlStato, new Object[]{idStipula});             
            
            boolean condCancellazioneGare = !( (idPadre!=null)||(new Long(6).equals(stato)) );
            
            if (condCancellazioneGare) {
	            // elimino i dati inseriti in gara 
	            String sqlUpdateGara = " update gare "
									 + "   set nrepat = null "
									 + "      ,daatto = null "
									 + " where ngara = ( "
									 + "				  select codlotto "
									 + "				  from  v_gare_stipula vgs "
									 + "				  where vgs.id = ? "
									 + "               ) ";
							
	            this.sqlManager.update(sqlUpdateGara, new Object[]{idStipula}); 
            } else {

	            String sqlUpdateStipula = " update g1stipula "
	            		                 +" set nrepat= null "
	            		                 +"    ,daatto= null "
	            		                 +" where id = ? ";
	            						
			
	            this.sqlManager.update(sqlUpdateStipula, new Object[]{idStipula});
            
            }
            
            // aggiorniamo la history ( = tabella G1CONT_ERP ) 
            
            // ..e aggiorno la tabella G1CONT_ERP
            Timestamp dataOdierna = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
            int id_G1CONT_ERP = this.genChiaviManager.getNextId("G1CONT_ERP");
            
            String sqlInsert =  " insert into G1CONT_ERP (id,origine, cig, codiceInterno, codiceSAP, operazione, data_operazione) "
                               +"                 values (? ,? ,? ,? ,? , ?, ?) ";
            
            String origine = "".equals(codGara)?"CON":"GAR";
            this.sqlManager.update(sqlInsert, new Object[]{id_G1CONT_ERP, origine, codCig, codStipula, numeroContrattoErp, "D", dataOdierna });           
            
            commit = true;
            
        } catch (SQLException e) {
      	  e.printStackTrace();    
      	  throw e;
        } finally {
            if (status != null) {
              try {
                if (commit) {
                  this.sqlManager.commitTransaction(status);
                } else {
                  this.sqlManager.rollbackTransaction(status);
                }
              } catch (SQLException e) {
            	  throw e;
              }
            }
          }
		
	}//eliminaAssociazioneContrattoErp

}//class EliminaAssociazioneContrattoErpAction
