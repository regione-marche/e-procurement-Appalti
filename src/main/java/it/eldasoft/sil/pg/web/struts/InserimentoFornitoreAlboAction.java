/*
 * Created on 24/09/2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.bl.erp.RaiWSERPManager;
import it.eldasoft.sil.pg.bl.erp.RaiwayWSERPManager;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Esegue l'inserimento dei fornitori con i dati prelevati dall'albo RAI 
 */
public class InserimentoFornitoreAlboAction extends ActionBaseNoOpzioni {

  static Logger     logger = Logger.getLogger(InserimentoFornitoreAlboAction.class);
  
  private RaiwayWSERPManager raiwayWSERPManager;
  
  private SqlManager sqlManager;
  
  private RaiWSERPManager raiWSERPManager;
  
  public void setSqlManager(SqlManager sqlManager){
    this.sqlManager = sqlManager;
  }
  
  public void setRaiWSERPManager(RaiWSERPManager raiWSERPManager){
	    this.raiWSERPManager = raiWSERPManager;
  }
  
  public void setRaiwayWSERPManager(RaiwayWSERPManager raiwayWSERPManager){
    this.raiwayWSERPManager = raiwayWSERPManager;
  }
    
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if(logger.isDebugEnabled()) logger.debug("InserimentoFornitoreAlboAction: inizio metodo");

    String target = null;
    String messageKey = null;
    String[] listaFornitoriSelezionati;
    String codFornitore = request.getParameter("codiceFornitore");
    codFornitore =StringUtils.stripToEmpty(codFornitore);
    String codiceFornitore = "";
    if(!"".equals(codFornitore)) {
    	codiceFornitore = request.getParameter("codiceFornitore");
    }
    
    String categoria = request.getParameter("categoria");
    String qsl = request.getParameter("qsl");
    String qslMsg="solo fornitori qualificati";
    if("2".equals(qsl)) {
    	qslMsg="fornitori con documenti scaduti";
    }
    String ngara = request.getParameter("ngara");
    String garaLottiConOffertaUnica = request.getParameter("garaLottiConOffertaUnica");
    String numeroFaseAttiva = request.getParameter("numeroFaseAttiva");
    String tipoWSERP = request.getParameter("tipoWSERP");
    listaFornitoriSelezionati = request.getParameterValues("keys");
    Long numFaseAttiva=null;
    if (numeroFaseAttiva!=null && !"".equals(numeroFaseAttiva))
      numFaseAttiva = Long.valueOf(numeroFaseAttiva);
    
    //variabili per tracciatura eventi
    int livEvento = 1;
    String codEvento = "GA_INSERISCI_DA_ALBOSAP";
    String oggEvento = "";
    String descrEvento = "";
    String errMsgEvento = "";
    String ditteInsDaAlbo ="";
    
    	try {
    		
    	  //macro in per tipoWSERP raiway insert su impr e prima chiamata la WS
    	  if("RAIWAY".equals(tipoWSERP)) {
    	    String[] listaFornitori = null;
    	    if (listaFornitoriSelezionati != null) {
    	      listaFornitori = new String[listaFornitoriSelezionati.length];
    	      for (int i = 0; i < listaFornitoriSelezionati.length; i++) {
    	        String[] valoriFornitoriSelezionati = listaFornitoriSelezionati[i].split("_");
    	        if (valoriFornitoriSelezionati.length > 0) {
    	          if (valoriFornitoriSelezionati.length > 1) { // ci sono due parametri cf e piva
    	            listaFornitori[i] = raiwayWSERPManager.insertAnagraficaFornitore(valoriFornitoriSelezionati[0], valoriFornitoriSelezionati[1]);
    	          } else { // c'è solo il cf
    	            listaFornitori[i] = raiwayWSERPManager.insertAnagraficaFornitore(valoriFornitoriSelezionati[0], null);
    	          }
  	            }
    	      }//for
    	    }
    	    if(listaFornitori != null) {
    	      listaFornitoriSelezionati = listaFornitori;
    	    }
    	  }
    	  
    	      oggEvento = ngara;
    	      //determino la categoria prevalente della gara
    	      String catiga = (String)this.sqlManager.getObject("select catiga from catg where ngara=?", new Object[]{ngara});
    	      descrEvento="Inserimento in gara ditte da albo SAP con categoria "+categoria+" (gara con categoria prevalente "+catiga+") \n"
    	      		+ "e con opzione: " + qslMsg;
    
		    if (listaFornitoriSelezionati != null) {
		    	for (int i = 0; i < listaFornitoriSelezionati.length; i++) {
		    		String[] valoriFornitoriSelezionati = listaFornitoriSelezionati[i].split(";");
		    		if (valoriFornitoriSelezionati.length >= 1) {
		    			if (valoriFornitoriSelezionati[0] != null ) {
		    				codiceFornitore = valoriFornitoriSelezionati[0];
	    					raiWSERPManager.insertFornitoreAlbo(codiceFornitore, ngara, garaLottiConOffertaUnica, numFaseAttiva);
       			            if(!"".equals(ditteInsDaAlbo)) {
	    			           ditteInsDaAlbo+=", ";   
	    			        }
	    			        ditteInsDaAlbo+=codiceFornitore;
		    			}
		    		}
		    		
		    	}//for
		    	
		    }else {
		    	if(!"".equals(codiceFornitore)) {
					raiWSERPManager.insertFornitoreAlbo(codiceFornitore, ngara, garaLottiConOffertaUnica, numFaseAttiva);
			        ditteInsDaAlbo+=codiceFornitore;
		    	}
		    }
		    target = "success";
		    request.setAttribute("RISULTATO", "OK");
		} catch (Exception e) {
		     livEvento =3;
		     errMsgEvento = e.getMessage();
	        target = "errore";
	        messageKey = "errors.alboFornitoriLista.InserimentoFornitoreAlbo";
	        this.aggiungiMessaggio(request, messageKey);
	    }finally{
	        //Tracciatura eventi solo per le operazioni di richiesta
	        try {
	            LogEvento logEvento = LogEventiUtils.createLogEvento(request);
	            logEvento.setLivEvento(livEvento);
	            logEvento.setOggEvento(oggEvento);
	            logEvento.setCodEvento(codEvento);
	            logEvento.setDescr(descrEvento);
	            logEvento.setErrmsg("Dettaglio ditte inserite da albo SAP : " + ditteInsDaAlbo + " " + errMsgEvento);
	            LogEventiUtils.insertLogEventi(logEvento);
	        } catch (Exception le) {
	            logger.error(this.resBundleGenerale.getString(messageKey), le);
	        }

	    }
    
    if(logger.isDebugEnabled()) logger.debug("InserimentoFornitoreAlboAction: fine metodo");

    if(target != null)
      return mapping.findForward(target);
    else
      return null;
  }
  
  
  
}
