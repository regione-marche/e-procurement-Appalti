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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.nadc.NewAdcManager;
import it.eldasoft.sil.pg.bl.nadc.model.Contratto;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;
import net.sf.json.JSONObject;

public class CheckNumeroContrattoErpAction extends Action {

	private SqlManager sqlManager;

	private NewAdcManager newAdcManager;

	public void setNewAdcManager(NewAdcManager newAdcManager) {
		this.newAdcManager = newAdcManager;
	}

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
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
		String isVisualizzazioneStr = request.getParameter("isVisualizzazione");
		Boolean isVisualizzazione = "true".equalsIgnoreCase(isVisualizzazioneStr);
		String idStipulaStr = request.getParameter("idStipula");
		Long idStipula= "".equals(idStipulaStr)?null:Long.valueOf(idStipulaStr);
		
		boolean isContrattoErpGiaAssociato = !isVisualizzazione && isPresenteInGaraOrContatto(numeroContrattoErp, idStipula);
		result.put("isContrattoErpGiaAssociato", isContrattoErpGiaAssociato);
		result.put("attivaPulsanteAssocia", false);
		if ( !isContrattoErpGiaAssociato ) {
		  Boolean esito = Boolean.TRUE;
		  Contratto contratto = new Contratto();
		  try {
		    contratto = newAdcManager.getContrattoWs(numeroContrattoErp);
		    if(contratto == null) {
		      throw new Exception();
		    }
		  } catch (Exception e) {
		    result.put("messaggio", "Il contratto cercato non esiste o non è ancora disponibile<br>");
		    result.put("attivaPulsanteAssocia", !isVisualizzazione);
		    esito = Boolean.FALSE;
		  }
		  result.put("esito", Boolean.TRUE.equals(esito)?"ok":"ko");
		  result.put("risposta", contratto);
		} else {
		  result.put("esito", "ko");
		  result.put("messaggio","Il contratto risulta gia' associato");
		}

		out.print(result);
		out.flush();

		return null;

	}//execute

	private boolean isPresenteInGaraOrContatto(String numeroContrattoErp, Long idStipula) throws SQLException {
		
		Long numOccorrenze;
		String queryStipula = "select count(1) from g1stipula gs where gs.nrepat=? ";
		
		Object paramQuery[];		
		if (idStipula!=null) {
			queryStipula += " and gs.id<>? ";
			paramQuery =  new Object[]{numeroContrattoErp, idStipula};
        } else {
        	paramQuery = new Object[]{numeroContrattoErp};	
        }
		
		numOccorrenze = (Long) this.sqlManager.getObject(queryStipula, paramQuery);
		
        if( numOccorrenze!=null && numOccorrenze.compareTo(new Long(0))>0){
        	return true;
        }		
        
        String queryGare =   " select count(1) from  gare g where g.nrepat = ? ";

        numOccorrenze = (Long) this.sqlManager.getObject(queryGare, new Object[]{numeroContrattoErp});		

        if( numOccorrenze!=null && numOccorrenze.compareTo(new Long(0))>0){
        	return true;
        }
        
		return false;
		
	}//isPresenteInGaraOContatto

}//class CheckNumeroContrattoErpAction
