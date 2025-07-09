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
//import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
//import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import net.sf.json.JSONObject;

public class AssociaInfoContrattoErpAction extends Action {

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
		String codStipula   = request.getParameter("codStipula");
		String idStipulaStr = request.getParameter("idStipula");
		Long   idStipula  = ("".equals(idStipulaStr))? null: Long.valueOf(idStipulaStr);
		String dataStr    = request.getParameter("data");
		Date   data       = UtilityDate.convertiData(dataStr, UtilityDate.FORMATO_GG_MM_AAAA);
		String idPadreStr = request.getParameter("idPadre");
		Long idPadre      = ("".equals(UtilityStringhe.convertiNullInStringaVuota(idPadreStr)))? null: Long.valueOf(idPadreStr);
		
		boolean isContrattoErpGiaAssociato = isPresenteInGaraOrContatto(numeroContrattoErp, idStipula);
		result.put("isContrattoErpGiaAssociato", isContrattoErpGiaAssociato);
		
		boolean esito = true; 
		String messaggio = "";
		
		try {
			if ( !isContrattoErpGiaAssociato ) {
				if ( idStipula != null ) {
					// caso variante di contratto
					associaInfoContrattoErp(codGara, idStipula, codStipula, numeroContrattoErp, data, idPadre);
				} else {					
					associaGaraAinfoContrattoErp(codGara, numeroContrattoErp, data);
				}
			} else {
				esito = false; 
				messaggio = "Il contratto risulta gia' associato";
			}//if			
			
		} catch (SQLException e) {
			esito = false;
			messaggio = " Problemi con accesso a DB durante associazione contratto Erp";
		}
		
	    result.put("esito", esito?"ok":"ko");
		result.put("risposta", messaggio);		

		out.print(result);
		out.flush();

		return null;

	}//execute

	private void associaGaraAinfoContrattoErp(String codGara, 
			                                  String numeroContrattoErp, 
			                                  Date data) throws SQLException {
		// questo metodo viene chiamato nel caso di gara senza stipula
		
		TransactionStatus status = null;
	    boolean commit = false;		
		
        try {
            status = this.sqlManager.startTransaction();
            
            String sqlUpdateGara = " update gare "
            					 + "   set nrepat = ?"
            					 +"       ,daatto = ?"
            					 + " where ngara = ? ";
            
            this.sqlManager.update(sqlUpdateGara, new Object[]{numeroContrattoErp, data, codGara});             
            
            // aggiorniamo la history ( = tabella G1CONT_ERP ) 
            
            // recupero il cig...
            String sqlCig =       " select g.codcig "
			            		+ " from  gare g "
			            		+ " where 1=1  "
			            		+ "  and g.nrepat = ? "
			            		+ "  and g.ngara = ? ";
            
            String codCig = (String) this.sqlManager.getObject(sqlCig, new Object[]{numeroContrattoErp, codGara});            
            
            // ..e aggiorno la tabella G1CONT_ERP
                        
            String sqlInsert =  " insert into G1CONT_ERP (id,origine, cig, codiceInterno, codiceSAP, operazione, data_operazione) "
                               +"                 values (? ,? ,? ,? ,? , ?, ?) ";
	
            int id_G1CONT_ERP = this.genChiaviManager.getNextId("G1CONT_ERP");
            Timestamp dataOdierna = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
            
            String origine = "GAR";
            this.sqlManager.update(sqlInsert, new Object[]{id_G1CONT_ERP, origine, codCig, codGara, numeroContrattoErp, "A", dataOdierna });		
            
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
		
	} //associaGaraAinfoContrattoErp

	private void associaInfoContrattoErp(String codGara,
			                             Long   idStipula, 
			                             String codStipula, 
			                             String numeroContrattoErp, 
			                             Date   data,
			                             Long idPadre) throws SQLException {
	    // Questo metodo viene chiamato:
		// - quando sono su una stipula => - devo aggiornare sia stipula che gara
		//                                 - in questo caso codGara e' vuoto
		// - quando sono in una gara in cui e' presente una stipula => - devo aggiornare solo gara
		//                                                             - in questo caso ho il codGara
		
		TransactionStatus status = null;
	    boolean commit = false;		
	
        try {
            status = this.sqlManager.startTransaction();
            
            String sqlStato = "select stato from g1stipula where id=? ";
            Long stato = (Long) this.sqlManager.getObject(sqlStato, new Object[]{idStipula});  
                        
            if ( (idPadre!=null)||(new Long(6).equals(stato)) ) {
            	// questo avviene nel caso di variante di contratto
                String sqlUpdateStipula = " update g1stipula "
		                 				 +" set nrepat=? "
		                 				 +"    ,daatto=? "
		                 				 +" where id=? ";

                this.sqlManager.update(sqlUpdateStipula, new Object[]{numeroContrattoErp, data, idStipula});          	            	
            } else {
            
	            String sqlUpdateGara = " update gare "
	            					 + "   set nrepat = ?"
	            					 +"       ,daatto = ?"
	            					 + " where ngara = ( "
	            					 + "				  select codlotto "
	            					 + "				  from  v_gare_stipula vgs "
	            					 + "				  where vgs.id = ? " 
	            					 + "              ) ";
	            
	            this.sqlManager.update(sqlUpdateGara, new Object[]{numeroContrattoErp, data, idStipula});     
            }
            
            // aggiorniamo la history ( = tabella G1CONT_ERP ) 
            
            // recupero il cig...
            String sqlCig =       " select g.codcig "
			            		+ " from  gare g "
			            		+ " where 1=1  "
			            		+ "  and g.nrepat = ? "
			            		+ "  and g.ngara = ( "
			            		+ "				     select codlotto "
			            		+ "				     from  v_gare_stipula vgs "
			            		+ "				     where 1=1 "
			            		+ "				       and vgs.nrepat = g.nrepat "
			            		+ "				       and vgs.id = ? "
			            		+ "                 )  ";
            
            String codCig = (String) this.sqlManager.getObject(sqlCig, new Object[]{numeroContrattoErp, idStipula});            
            
            // ..e a ggiorno la tabella G1CONT_ERP
                        
            String sqlInsert =  " insert into G1CONT_ERP (id,origine, cig, codiceInterno, codiceSAP, operazione, data_operazione) "
                               +"                 values (? ,? ,? ,? ,? , ?, ?) ";
	
            int id_G1CONT_ERP = this.genChiaviManager.getNextId("G1CONT_ERP");
            Timestamp dataOdierna = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
            
            String origine = "".equals(codGara)?"CON":"GAR";
            this.sqlManager.update(sqlInsert, new Object[]{id_G1CONT_ERP, origine, codCig, codStipula, numeroContrattoErp, "A", dataOdierna });
		            
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
		
	}//associaInfoContrattoErp

	private boolean isPresenteInGaraOrContatto(String numeroContrattoErp, Long idStipula) throws SQLException {
		
		String queryStipula = "select count(1) from g1stipula gs where gs.nrepat=? and gs.id<>?";
		Long numOccorrenze = (Long) this.sqlManager.getObject(queryStipula, new Object[]{numeroContrattoErp, idStipula});		

        if( numOccorrenze!=null && numOccorrenze.compareTo(new Long(0))>0){
        	return true;
        }
        
        String queryGare =   " select count(1) from  gare g where 1=1  "+
							 " and g.nrepat = ? ";
					        /* " and g.ngara = ( "
									 + "		 select codlotto "
									 + "		 from  v_gare_stipula vgs "
									 + "		 where g.nrepat = ? "
									 + "           and vgs.id <> ? "
									 + "      ) ";n*/
        numOccorrenze = (Long) this.sqlManager.getObject(queryGare, new Object[]{numeroContrattoErp});
        
        if( numOccorrenze!=null && numOccorrenze.compareTo(new Long(0))>0){
        	return true;
        }
        
		return false;
	}//isPresenteInGaraOContatto

}//class CheckNumeroContrattoErpAction
