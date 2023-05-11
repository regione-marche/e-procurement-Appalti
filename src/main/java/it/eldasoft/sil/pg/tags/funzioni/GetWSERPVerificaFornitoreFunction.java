/*
 * Created on 08-10-2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.xml.rpc.ServiceException;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreResType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreType;

/**
 * Funzione che estrae i dati delle rda (o delle rda) collegate alla gara
 * @author Cristian Febas
 */
public class GetWSERPVerificaFornitoreFunction extends AbstractFunzioneTag {
	
  
  
  public  GetWSERPVerificaFornitoreFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    
    String esitoControlli = "";
    String tipoWSERP = null;

    try {    
      GestioneWSERPManager gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager", pageContext, GestioneWSERPManager.class);
      WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
      if(configurazione.isEsito()){
        tipoWSERP = configurazione.getRemotewserp();
      }
      
      ProfiloUtente profilo = (ProfiloUtente) pageContext.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = new Long(profilo.getId());
      String[] credenziali = gestioneWSERPManager.wserpGetLogin(syscon, "WSERP");
  
      String username = credenziali[0];
      String password = credenziali[1];
      
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      String ditta = (String) params[1];
      
      if (ditta != null) {
      
        String selectIMPR = "select cfimp,pivimp,nomimp,indimp,locimp,nazimp,capimp,nciimp,proimp," +
                "emai2ip,telimp,emaiip,telcel,cgenimp,iscrcciaa,faximp,coorba" +
                " from impr where codimp = ?";
        List<?> datiIMPR = sqlManager.getListVector(selectIMPR, new Object[] { ditta });
        if (datiIMPR != null && datiIMPR.size() > 0) {
          
            String cfimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 0).getValue();
            cfimp = UtilityStringhe.convertiNullInStringaVuota(cfimp);
            String pivimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 1).getValue();
            pivimp = UtilityStringhe.convertiNullInStringaVuota(pivimp);
            String nomimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 2).getValue();
            String cgenimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 13).getValue();
            cgenimp = UtilityStringhe.convertiNullInStringaVuota(cgenimp);
  
            if("ATAC".equals(tipoWSERP)){
              if(!"".equals(cfimp) || !"".equals(pivimp)){
                WSERPFornitoreType fornitoreSearch = new WSERPFornitoreType();
                fornitoreSearch.setIdFornitore(cgenimp);
                fornitoreSearch.setRagioneSociale(nomimp);
                
                WSERPFornitoreResType wserpFornitoreRes = gestioneWSERPManager.wserpDettaglioFornitore(username, password, "WSERP", cfimp, pivimp, fornitoreSearch );
                if(wserpFornitoreRes.isEsito() == true) {
                  esitoControlli = "PIVA_OK";
                }
                else {
                  esitoControlli = wserpFornitoreRes.getMessaggio();                  
                }
                  
              }
            }
      
          }
        
        }
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura dei dati del fornitore", e);       
    } catch (GestoreException e) {
      throw new JspException("Errore durante il recupero delle credenziali per il s.o.", e);      
    }
    return esitoControlli;
  }
}
