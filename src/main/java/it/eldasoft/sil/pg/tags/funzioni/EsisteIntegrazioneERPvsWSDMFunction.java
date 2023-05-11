/*
 * Created on 08/05/19
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;

import it.eldasoft.gene.bl.integrazioni.WsdmConfigManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

/**
 * Controlla se esiste l'integrazione con ERP tramite WSDM
 *
 * @author C.F.
 */
public class EsisteIntegrazioneERPvsWSDMFunction extends AbstractFunzioneTag {

  public EsisteIntegrazioneERPvsWSDMFunction(){
    super(2, new Class[]{PageContext.class,String.class});
  }

  public  static final String SERVIZIO_DOCUMENTALE                    = "DOCUMENTALE";
  private static final String PROP_WSDM_DOCUMENTALE_URL                = "wsdm.documentale.url.";


  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    Logger logger = Logger.getLogger(EsisteIntegrazioneERPvsWSDMFunction.class);
    String integrazioneERPvsWSDM="0";
    
    GestioneWSDMManager gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        pageContext, GestioneWSDMManager.class);
    
    WsdmConfigManager wsdmConfigManager = (WsdmConfigManager) UtilitySpring.getBean("wsdmConfigManager",
        pageContext, WsdmConfigManager.class);
    
    String servizio = GestioneWSDMManager.SERVIZIO_DOCUMENTALE;
    Long idconfi = null;
    try {
      
      String idconfiParam = (String) params[1];
      if(idconfiParam!=null && !"".equals(idconfiParam)){
        idconfi = new Long(idconfiParam);
      }else{
        HttpSession session = this.getRequest().getSession();
        if (session != null) {
          String ufficioIntestatario = ((String) session.getAttribute("uffint"));
          ufficioIntestatario = UtilityStringhe.convertiNullInStringaVuota(ufficioIntestatario);
          if(!"".equals(ufficioIntestatario)){
            idconfi = wsdmConfigManager.getWsdmConfigurazione(ufficioIntestatario, "PG");
          }
        }
      }
      
      if(idconfi!=null){
        String url = ConfigManager.getValore(PROP_WSDM_DOCUMENTALE_URL+idconfi);
        url = UtilityStringhe.convertiNullInStringaVuota(url);
  
        if(!"".equals(url)){
          WSDMConfigurazioneOutType config = gestioneWSDMManager.wsdmConfigurazioneLeggi(servizio,idconfi.toString());
          if (config.isEsito()){
            String tipoWSDM = config.getRemotewsdm();
            tipoWSDM = UtilityStringhe.convertiNullInStringaVuota(tipoWSDM);
            if("JIRIDE".equals(tipoWSDM)){
                String wsdmGestioneERP = ConfigManager.getValore("wsdm.gestioneERP."+idconfi);
                integrazioneERPvsWSDM = UtilityStringhe.convertiNullInStringaVuota(wsdmGestioneERP);
                pageContext.setAttribute("idconfi", idconfi.toString());
            }
          }
        }
      }

    } catch (GestoreException e) {
      UtilityStruts.addMessage(this.getRequest(), "error",
            "wsdmconfigurazione.configurazioneleggi.remote.error",new Object[]{": servizio documentale non attivo"});

      logger.error("Errore nella verifica dell'integrazione ERP tramite WSDM " ,e);
    } catch (SQLException e) {
      logger.error("Errore nella verifica dell'integrazione ERP tramite WSDM " ,e);
    }

    return integrazioneERPvsWSDM;

  }

}