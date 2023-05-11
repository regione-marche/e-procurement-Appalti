/*
 * Created on 28/02/2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire la 
 * assegnazione al miglior offerente
 *
 * @author Cristian Febas
 */
public class GestorePopupAssegnaMigliorOfferente extends
    AbstractGestoreEntita {

  public GestorePopupAssegnaMigliorOfferente() {
    super(false);
  }

  /** Logger Log4J di classe */
  static Logger          logger = Logger.getLogger(GestorePopupAssegnaMigliorOfferente.class);

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;


  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
  }

  @Override
  public String getEntita() {
    return "GCAP";
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    // lettura dei parametri di input
    String ngara = StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),"ngara"));
    
    try {
    	
		
		//HashMap mappaMigliorOfferente = new HashMap();

    	
    	List<?> datiGcapContaf = this.sqlManager.getListVector("select distinct(contaf) from gcap where ngara = ? and isprodneg is null order by contaf", new Object[]{ngara});
    	if (datiGcapContaf!= null && datiGcapContaf.size()>0){
    		for(int i=0;i<datiGcapContaf.size();i++){
    			Long contaf = (Long) SqlManager.getValueFromVectorParam(datiGcapContaf.get(i), 0).getValue();
    			//ordino in base a prezzo offerto e data consegna asc
    			List<?> datiListaDPRE=this.sqlManager.getListVector("select dittao,qtaordinabile,preoff,qtaordinata,datacons from dpre where ngara = ? and contaf = ?"
    					+ " order by preoff,datacons asc", new Object[]{ngara,contaf});
    			Double prezzoOfferto_prec = null;
    			Double prezzoOfferto = null;
    			Date dataConsegna_prec = null;
    			int offertaPariMerito = 0;
    			String dittao = null;
    			String dittaAggiudicataria = null;
    			String updateDpre = "update DPRE set qtaordinata=qtaordinabile where ngara=? and contaf=? and dittao=?";
    			
    			for (int j = 0; j < datiListaDPRE.size(); j++) {
    				dittao = (String) SqlManager.getValueFromVectorParam(datiListaDPRE.get(j), 0).getValue();
    				prezzoOfferto = (Double) SqlManager.getValueFromVectorParam(datiListaDPRE.get(j), 2).getValue();
    				Date dataConsegnaGarantita = (Date) SqlManager.getValueFromVectorParam(datiListaDPRE.get(j), 4).getValue();
    				if(j==0) {
    					dittaAggiudicataria = dittao;
    					if(datiListaDPRE.size()==1) {
    		  	    		this.sqlManager.update(updateDpre,new Object[] {ngara,contaf,dittaAggiudicataria});    
    		  	    		break;
    					}
    				}
    				if(j==1) {
        		  	    if(prezzoOfferto_prec!=null && prezzoOfferto_prec!=null && prezzoOfferto_prec.equals(prezzoOfferto)) {
        		  	    	if(dataConsegnaGarantita!=null && dataConsegna_prec!=null && dataConsegnaGarantita.equals(dataConsegna_prec)) {
        		  	    		break;	
        		  	    	}else {
        		  	    		this.sqlManager.update(updateDpre,new Object[] {ngara,contaf,dittaAggiudicataria});    
        		  	    		break;
        		  	    	}
        		  	    }else {
    		  	    		this.sqlManager.update(updateDpre,new Object[] {ngara,contaf,dittaAggiudicataria});    
    		  	    		break;
        		  	    }
    				}
    		  	    prezzoOfferto_prec = prezzoOfferto;
    		  	    dataConsegna_prec = dataConsegnaGarantita;
    			}
    		}
    	}
    	
    	this.getRequest().setAttribute("assegnamentoEseguito", "1");
  	      
		
		
	} catch (SQLException e) {
		throw new GestoreException("Errore nell'aggiornamento del prodotto aggiudicato", null, e);
	}
   
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  


 
}
