/*
 * Created on 22/02/2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

/**
 * Funzione che recupera i dati dell' articolo
 *
 * @author Cristian.Febas
 */
public class GetDatiArticoloFunction extends AbstractFunzioneTag {

  public GetDatiArticoloFunction() {
    super(3, new Class[] { PageContext.class,String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ngara = (String) params[1];
    ngara = UtilityStringhe.convertiNullInStringaVuota(ngara);
    String contafStr = (String) params[2];
    contafStr = UtilityStringhe.convertiNullInStringaVuota(contafStr);
    String isAffidato = "false";
    
    
    String selectDatiGcap = "select gc.codvoc,gc.voce,ge.desest,gc.quanti,gc.prezun,gc.datacons,gc.isprodneg"
      		+ " from gcap gc"
      		+ " join GCAP_EST ge"
      		+ " on gc.ngara=ge.ngara and gc.contaf=ge.contaf"
      		+ " where gc.ngara = ? and gc.contaf = ? ";
    

    String selectAggiudicatario = "select d.dittao"
    		+ " from dpre d"
    		+ " join gare g"
    		+ " on d.ngara=g.seguen and d.dittao=g.ditta"
    		+ " where d.ngara=? and d.contaf=?"
    		+ " and d.qtaordinata is not null and g.seguen =? ";

    if(!"".equals(ngara) && !"".equals(contafStr)){
      Long contaf = Long.valueOf(contafStr);



      try {
        Vector<?> datiGcap = sqlManager.getVector(selectDatiGcap,new Object[] { ngara,contaf });
        
        if (datiGcap != null && datiGcap.size() > 0){
          String codvoc = SqlManager.getValueFromVectorParam(datiGcap, 0).stringValue();
          String voce = SqlManager.getValueFromVectorParam(datiGcap, 1).stringValue();
          String desest = SqlManager.getValueFromVectorParam(datiGcap, 2).stringValue();
          Double quanti = SqlManager.getValueFromVectorParam(datiGcap, 3).doubleValue();
          Double prezun= SqlManager.getValueFromVectorParam(datiGcap, 4).doubleValue();
          Date datacons = (Date) SqlManager.getValueFromVectorParam(datiGcap, 5).getValue();
          String isProdNeg = SqlManager.getValueFromVectorParam(datiGcap, 6).stringValue();
          String dtcons = UtilityDate.convertiData(datacons, UtilityDate.FORMATO_GG_MM_AAAA);
          pageContext.setAttribute("codvoc",codvoc,PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("voce",voce,PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("desest",desest,PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("quanti",quanti,PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("prezun",prezun,PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("datacons",dtcons,PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("isprodneg",isProdNeg,PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("ngara",ngara,PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("contaf",contaf,PageContext.REQUEST_SCOPE);
          
          
          //Recupero dell'assegnatario di default con la stessa logica presente nella lista da cui si accede
          List<?> datiListaDPRE= sqlManager.getListVector("select dittao,qtaordinabile,preoff,qtaordinata,datacons from dpre where ngara = ? and contaf = ?"
  					+ " order by preoff,datacons asc", new Object[]{ngara,contaf});
			Double prezzoOfferto_prec = null;
			Double prezzoOfferto = null;
			Date dataConsegna_prec = null;
			String dittao = null;
			String dittaAggiudicatariaDefault = null;
			String dittaAggiudicataria = null;
          
			if(datiListaDPRE.size()==0) {
				;  
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
						;
					}else {
						pageContext.setAttribute("aggiudicatarioDefault",dittaAggiudicatariaDefault,PageContext.REQUEST_SCOPE);		
					}
				}
			}
          
          //verifico anche se l'eventuale aggiudicatario ha un affidamento
          String aggiudicatario = (String) sqlManager.getObject(selectAggiudicatario,new Object[] { ngara,contaf,ngara });
          aggiudicatario = StringUtils.stripToEmpty(aggiudicatario);
          if(!"".equals(aggiudicatario)) {
        	  isAffidato = "true";
        	  pageContext.setAttribute("isAffidato",isAffidato,PageContext.REQUEST_SCOPE);
        	  
          }
          
        }

      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre i dati dell'articolo", e);
      } catch (GestoreException ge) {
        throw new JspException("Errore nell'estrarre i dati dell'articolo", ge);
      }

    }

    return null;
  }

}
