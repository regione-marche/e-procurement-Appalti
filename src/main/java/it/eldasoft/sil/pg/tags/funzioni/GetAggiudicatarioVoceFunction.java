package it.eldasoft.sil.pg.tags.funzioni;
/*
 * Created on 20/01/2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */


import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringEscapeUtils;
/**
 * Funzione per il calcolo degli aggiudicatari per voce
 *
 * @author Cristian Febas
 */
public class GetAggiudicatarioVoceFunction extends AbstractFunzioneTag{
	public GetAggiudicatarioVoceFunction() {
	    super(3, new Class[]{PageContext.class, String.class, String.class});
	  }

	@Override
  public String function(PageContext pageContext, Object[] params)
    throws JspException {

	  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

      String ngara = (String) params[1];
      String contafStr = (String) params[2];
      contafStr = UtilityStringhe.convertiNullInStringaVuota(contafStr);
      String msgAggiudicatarioVoce = null;
      
 
      String selAggiudicatarioVoce = "select dittao,preoff,datacons from dpre where ngara=? and contaf=? and qtaordinata is not null";

      try {
	     if(!"".equals(contafStr)) {
	    	  Long contaf = Long.valueOf(contafStr);
	    	  Vector<?> datiAggiudicatarioVoce = sqlManager.getVector(selAggiudicatarioVoce, new Object[]{ngara, contaf});
	    	  if(datiAggiudicatarioVoce!=null && datiAggiudicatarioVoce.size()>0){
	    		  String codAggiudicatarioVoce = SqlManager.getValueFromVectorParam(datiAggiudicatarioVoce, 0).getStringValue();
	    		  String ragSocAggiudicatarioVoce = (String) sqlManager.getObject("select nomest from impr where codimp=?", new Object[]{codAggiudicatarioVoce});
	    		  Double preOffAggiudicatarioVoce = (Double) SqlManager.getValueFromVectorParam(datiAggiudicatarioVoce, 1).getValue();
	    		  String preOffAggiudicatarioVoceStr ="";
	    		  if(preOffAggiudicatarioVoce!=null) {
	    			  preOffAggiudicatarioVoceStr = UtilityNumeri.convertiImporto(preOffAggiudicatarioVoce, 2)+ " euro";
	    		  }
	    		  Date dConsAggiudicatarioVoce = (Date) SqlManager.getValueFromVectorParam(datiAggiudicatarioVoce, 2).getValue();
	    		  String dataConsAggiudicatarioVoce = UtilityDate.convertiData(dConsAggiudicatarioVoce, UtilityDate.FORMATO_GG_MM_AAAA);
	    		  
	    		  if(preOffAggiudicatarioVoce !=null &&  dConsAggiudicatarioVoce!=null) {
		    		  msgAggiudicatarioVoce =
		    				   "Assegnatario: " + StringEscapeUtils.escapeHtml(ragSocAggiudicatarioVoce)+"\r\n"
		    	    		  +"Prezzo offerto: " + preOffAggiudicatarioVoceStr+"\r\n"
		    	    		  +"Data consegna garantita: " + "\r\n"+dataConsAggiudicatarioVoce+"\r\n";
	    		  }
	    		  pageContext.setAttribute("preOffAggiudicatarioVoce",preOffAggiudicatarioVoce,PageContext.REQUEST_SCOPE);
	    	  }
	    	  else {
	    		  pageContext.setAttribute("preOffAggiudicatarioVoce","",PageContext.REQUEST_SCOPE);
	    	  }
	    	  //verifico lo spostamento dell'assegnazione rispetto al default  	  
	    	  //calcolo il default (dittao) ordinando in base a prezzo offerto e data consegna asc sulla stessa voce
  			List<?> datiListaDPRE= sqlManager.getListVector("select dittao,qtaordinabile,preoff,qtaordinata,datacons from dpre where ngara = ? and contaf = ?"
  					+ " order by preoff,datacons asc", new Object[]{ngara,contaf});
  			
			Double prezzoOfferto_prec = null;
			Double prezzoOfferto = null;
			Date dataConsegna_prec = null;
			String dittao = null;
			String dittaAggiudicatariaDefault = null;
			String dittaAggiudicataria = null;
			String aggDefaultFlag=null;
			
			if(datiListaDPRE.size()==0) {
				pageContext.setAttribute("aggDefaultFlag","NOOFF",PageContext.REQUEST_SCOPE);  
			}else {
				for (int j = 0; j < datiListaDPRE.size(); j++) {
					dittao = (String) SqlManager.getValueFromVectorParam(datiListaDPRE.get(j), 0).getValue();
					prezzoOfferto = (Double) SqlManager.getValueFromVectorParam(datiListaDPRE.get(j), 2).getValue();
					Double qtaOrdinata = (Double) SqlManager.getValueFromVectorParam(datiListaDPRE.get(j), 3).getValue();
					Date dataConsegnaGarantita = (Date) SqlManager.getValueFromVectorParam(datiListaDPRE.get(j), 4).getValue();
					if(qtaOrdinata!=null) {
						dittaAggiudicataria = dittao;
					}
					if(j==0) {
						dittaAggiudicatariaDefault = dittao;	
					}
					
					if(j==1) {
	    		  	    if(prezzoOfferto_prec!=null && prezzoOfferto_prec!=null && prezzoOfferto_prec.equals(prezzoOfferto)) {
	    		  	    	if(dataConsegnaGarantita!=null && dataConsegna_prec!=null && dataConsegnaGarantita.equals(dataConsegna_prec)) {
	    		  	    		dittaAggiudicatariaDefault = null;
	    		  	    	}
	    		  	    }
					}
			  	    prezzoOfferto_prec = prezzoOfferto;
			  	    dataConsegna_prec = dataConsegnaGarantita;
				}//end for
				
				if(dittaAggiudicataria!=null) {
					if(dittaAggiudicatariaDefault!=null && dittaAggiudicataria.equals(dittaAggiudicatariaDefault)) {
						aggDefaultFlag="DEFAGG";
					}else {
						aggDefaultFlag="NODEFAGG";
						msgAggiudicatarioVoce = msgAggiudicatarioVoce + "* Attenzione: Non è il prezzo più basso"+"\r\n";
						
					}
				}else {
					aggDefaultFlag="NOAGG";
				}


				pageContext.setAttribute("aggDefaultFlag",aggDefaultFlag,PageContext.REQUEST_SCOPE);  
				
			}
  			
	    	  
	     }    	  
        
  
	  } catch (SQLException e) {
	          throw new JspException(
	              "Errore durante la lettura dell'assegnatario della voce !", e);
	  }

	    return msgAggiudicatarioVoce;
	}

}
