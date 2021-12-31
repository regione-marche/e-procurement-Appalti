/*
 * Created on 23/04/15
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

/**
 * Viene letto il tipo di WSDM attivo, per il servizio indicato.
 * Se richiesto da parametro è possibile inserire fra i dati del request
 * il messaggio di errore
 *
 * @author Marcello Caminiti
 */
public class GetTipoWSDMFunction extends AbstractFunzioneTag {

  public GetTipoWSDMFunction(){
    super(4, new Class[]{PageContext.class, String.class, String.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String servizio = (String) params[1];
    String tracciaturaMessaggio = (String) params[2];
    String idconfi = (String) params[3];
    
    Logger logger = Logger.getLogger(GetTipoWSDMFunction.class);
    String tipoWSDM="";
    GestioneWSDMManager gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        pageContext, GestioneWSDMManager.class);

    try {
      tipoWSDM = gestioneWSDMManager.getTipoWSDM(servizio,idconfi);
    } catch (GestoreException e) {
      if("SI".equals(tracciaturaMessaggio))

        UtilityStruts.addMessage(this.getRequest(), "error",
            "wsdmconfigurazione.configurazioneleggi.remote.error",null);

      logger.error("Errore nella lettura del tipo di integrazione WSDM attiva ",e);
    }

    return tipoWSDM;

  }

}