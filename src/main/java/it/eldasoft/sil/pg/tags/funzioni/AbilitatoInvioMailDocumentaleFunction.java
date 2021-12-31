/*
 * Created on 22-01-2016
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
 * Viene effettuato il controllo sull'esistenza delle condizioni per l'invio mail tramite
 * documentale
 *
 * @author Marcello Caminiti
 */

public class AbilitatoInvioMailDocumentaleFunction extends AbstractFunzioneTag {

  public AbilitatoInvioMailDocumentaleFunction() {
    super(2, new Class[] { PageContext.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String result= "false";
    GestioneWSDMManager gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        pageContext, GestioneWSDMManager.class);

    Logger logger = Logger.getLogger(AbilitatoInvioMailDocumentaleFunction.class);
    
    String idconfi = (String) params[1];
    
    try {
      if(gestioneWSDMManager.abilitatoInvioMailDocumentale("FASCICOLOPROTOCOLLO",idconfi))
        result = "true";

    } catch (GestoreException e) {
      UtilityStruts.addMessage(this.getRequest(), "error",
          "wsdmconfigurazione.configurazioneleggi.remote.error",new Object[]{": Servizio di protocollazione non attivo"});
      logger.error("Errore nella verifica delle condizioni per l'invio della mail tramite documentale, servizio di protocollazione non attivo", e);
    }

    return result;
  }
}
