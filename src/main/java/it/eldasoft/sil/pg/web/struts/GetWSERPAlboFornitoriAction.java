/*
 * Created on 27/sept/2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;



import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreResType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author Cristian.Febas - Eldasoft S.p.A. Treviso
 *
 */
public class GetWSERPAlboFornitoriAction extends ActionBaseNoOpzioni {

	/** FORWARD_SUCCESS forward richiesta lista cup avvenuta con successo. */
  protected static final String          FORWARD_SUCCESS   = "listafalbosuccess";
  /** FORWARD_ERROR forward per richiesta lista cup ha generato un errore. */
  protected static final String          FORWARD_ERROR     = "listafalboerror";
  /** Logger Log4J di classe. */
  private static Logger                          logger            = Logger.getLogger(GetWSERPAlboFornitoriAction.class);
  
  private static final String PROP_ARIBA_WS_OATH            = "ariba.ws.oath";
  private static final String PROP_ARIBA_WS_APIKEY            = "ariba.ws.apikey";
  
  private GestioneWSERPManager gestioneWSERPManager;

  private SqlManager sqlManager;

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("GetWSERPAlboFornitoriAction: inizio metodo");
    }

    String target = FORWARD_SUCCESS;
    String messageKey = null;
    
    try {    

      
      
      String username = ConfigManager.getValore(PROP_ARIBA_WS_APIKEY);  
      String password = ConfigManager.getValore(PROP_ARIBA_WS_OATH);
  

      // Parametri di ricerca
      HashMap<String, String> hMapParametri = new HashMap<String, String>();
      hMapParametri.put("categoria", request.getParameter("categoria"));

      
      WSERPFornitoreType fornitore = new WSERPFornitoreType();
      List<Vector< ? >> risultatoListaFornitori = new ArrayList<Vector< ? >>();
      
      WSERPRdaType rdaSearch = new WSERPRdaType();
      
      String codgar = request.getParameter("codgar");
      codgar = StringUtils.stripToEmpty(codgar);
      String ngara = request.getParameter("ngara");
      ngara = StringUtils.stripToEmpty(ngara);
      String numeroFaseAttiva = request.getParameter("numeroFaseAttiva");
      numeroFaseAttiva = StringUtils.stripToEmpty(numeroFaseAttiva);
      String garaLottiConOffertaUnica = request.getParameter("garaLottiConOffertaUnica");
      garaLottiConOffertaUnica = StringUtils.stripToEmpty(garaLottiConOffertaUnica);
      String category = request.getParameter("categoria");
      category = StringUtils.stripToEmpty(category);
      if(!"".equals(category)) {
    	  rdaSearch.setCodiceMateriale(request.getParameter("categoria"));  
      }

      String descat = request.getParameter("descat");
      descat = StringUtils.stripToEmpty(descat);
      if(!"".equals(descat)) {
    	  rdaSearch.setCodiceArticolo(request.getParameter("descat"));  
      }
      
      String qsl = request.getParameter("qsl");
      qsl = StringUtils.stripToEmpty(qsl);
      if(!"".equals(qsl)) {
    	  rdaSearch.setNatura(qsl);  
      }
      
      String qslMsg="solo fornitori qualificati";
      if("2".equals(qsl)) {
      	qslMsg="fornitori con documenti scaduti";
      }

      
      request.setAttribute("ngara", ngara);
      request.setAttribute("numeroFaseAttiva", numeroFaseAttiva);
	  request.setAttribute("garaLottiConOffertaUnica", garaLottiConOffertaUnica);
      request.setAttribute("categoria", category);
      request.setAttribute("qsl", qsl);
      
      WSERPRdaResType wserpRdaRes = this.gestioneWSERPManager.wserpListaRda(username, password, "WSERP", rdaSearch );
      String errMsgEvento = wserpRdaRes.getMessaggio();
      errMsgEvento = StringUtils.stripToEmpty(errMsgEvento);
      if(!"".equals(errMsgEvento)) {
          //traccio eventuali incongruenze
    	  String msgIncongruenzeAlbo="L'estrazione ha segnalato le seguenti incongruenze:\n";
    	  errMsgEvento=msgIncongruenzeAlbo+errMsgEvento;
    	  LogEvento logEvento = LogEventiUtils.createLogEvento(request);
          logEvento.setCodApplicazione("PG");
          logEvento.setLivEvento(3);
          logEvento.setOggEvento(category);
          logEvento.setCodEvento("GA_CATALBO_ERP");
          logEvento.setDescr("Estrazione ditte (albo fornitori) con categoria "+category+" e con opzione: "+qslMsg);
          logEvento.setErrmsg(errMsgEvento);
          this.gestioneWSERPManager.updLogEventi(logEvento);
      }

      WSERPRdaType[] rdaArray = wserpRdaRes.getRdaArray();
      if(rdaArray!= null) {
          int totalRDA = rdaArray.length;
          
          for (int a = 0; a < totalRDA; a++) {
        	  WSERPRdaType rdaSingle = rdaArray[a];

              Vector<?> datiFornitore = this.sqlManager.getVector("select codimp,cfimp,pivimp,locimp,proimp"
              		+ " from impr where cgenimp = ? and codimp not in (select dittao from ditg where codgar5=?and ngara5=?)",
            		  new Object[] { rdaSingle.getCodiceCarrello(),codgar,ngara });
              if(datiFornitore!=null && datiFornitore.size()>0){
                  Vector<String> dati = new Vector<String>();
                  String codimp=SqlManager.getValueFromVectorParam(datiFornitore, 0).stringValue();
                  String cfimp=SqlManager.getValueFromVectorParam(datiFornitore, 1).stringValue();
                  String pivimp=SqlManager.getValueFromVectorParam(datiFornitore, 2).stringValue();
                  String locimp=SqlManager.getValueFromVectorParam(datiFornitore, 3).stringValue();
                  String proimp=SqlManager.getValueFromVectorParam(datiFornitore, 4).stringValue();
                  dati.add(codimp);
                  dati.add(rdaSingle.getCodiceCarrello());
                  dati.add(rdaSingle.getCodiceRda());
                  dati.add(cfimp);
                  dati.add(pivimp);
                  String ecc = rdaSingle.getCodiceCigAQ();
                  ecc = StringUtils.stripToEmpty(ecc);
                  if(!"".equals(ecc) && !"-1".equals(ecc)) {
                	  dati.add(rdaSingle.getCodiceCigAQ());
                  }else {
                	  dati.add("Non disponibile");
                  }
                  locimp = StringUtils.stripToEmpty(locimp);
                  if(!"".equals(locimp)) {
                	  dati.add(locimp);
                  }else {
                	  dati.add("Non disponibile");
                  }
                  proimp = StringUtils.stripToEmpty(proimp);
                  if(!"".equals(proimp)) {
                	  dati.add(proimp);
                  }else {
                	  dati.add("Non disponibile");
                  }
                  risultatoListaFornitori.add(dati);
              }else {
                  Vector<String> dati = new Vector<String>();
                  String codimp="Non disponibile";
                  String cfimp="Non disponibile";
                  String pivimp="Non disponibile";
                  String locimp="Non disponibile";
                  String proimp="Non disponibile";
                  dati.add(codimp);
                  dati.add(rdaSingle.getCodiceCarrello());
                  dati.add(rdaSingle.getCodiceRda());
                  dati.add(cfimp);
                  dati.add(pivimp);
                  String ecc = rdaSingle.getCodiceCigAQ();
                  ecc = StringUtils.stripToEmpty(ecc);
                  if(!"".equals(ecc) && !"-1".equals(ecc)) {
                	  dati.add(rdaSingle.getCodiceCigAQ());
                  }else {
                	  dati.add("Non disponibile");
                      locimp = StringUtils.stripToEmpty(locimp);
                      if(!"".equals(locimp)) {
                    	  dati.add(locimp);
                      }else {
                    	  dati.add("Non disponibile");
                      }
                  }
                  dati.add(locimp);
                  dati.add(proimp);
                  risultatoListaFornitori.add(dati);
              }
              
          }
    	  
      }
      
      if(wserpRdaRes.isEsito()) {
          target = FORWARD_SUCCESS;
          request.setAttribute("risultatoListaFornitori", risultatoListaFornitori);
    	  
      }else {
          target = FORWARD_ERROR;
          messageKey = "errors.alboFornitoriLista.error";
          this.aggiungiMessaggio(request, messageKey, wserpRdaRes.getMessaggio());
     }
            
    } catch (Exception e) {
      target = FORWARD_ERROR;
      messageKey = "errors.alboFornitoriLista.error";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey, e.getMessage());
    } catch (Throwable e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (messageKey != null) {
    	response.reset();
    }

    if (logger.isDebugEnabled()) {
    	logger.debug("GetWSERPAlboFornitoriAction: fine metodo");
    }

    return mapping.findForward(target);

  }

}
