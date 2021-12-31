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

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

/**
 * Controlla se esiste l'integrazione con WSDM per il documentale
 *
 * @author Marcello Caminiti
 */
public class EsisteIntegrazioneWSDNDocumentaleFunction extends AbstractFunzioneTag {

  public EsisteIntegrazioneWSDNDocumentaleFunction(){
    super(3, new Class[]{PageContext.class, String.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String codiceGara = (String) params[1];
    String idconfi = (String) params[2];
    Logger logger = Logger.getLogger(EsisteIntegrazioneWSDNFunction.class);
    String integrazioneWSDM="0";
    GestioneWSDMManager gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        pageContext, GestioneWSDMManager.class);

    try {
      boolean isIntegrazioneWSDMAttivaValida = gestioneWSDMManager.isIntegrazioneWSDMAttivaValida(GestioneWSDMManager.SERVIZIO_DOCUMENTALE,idconfi);
      if(isIntegrazioneWSDMAttivaValida)
        integrazioneWSDM="1";
    } catch (GestoreException e) {
      UtilityStruts.addMessage(this.getRequest(), "error",
            "wsdmconfigurazione.configurazioneleggi.remote.error",new Object[]{": servizio di archiviazione documentale non attivo"});
        logger.error("Errore nella verifica dell'integrazione WSDM Protocollo per la gara " + codiceGara ,e);
    } catch (SQLException e) {
      UtilityStruts.addMessage(this.getRequest(), "error",
          "wsdmconfigurazione.configurazioneleggi.remote.error",new Object[]{": servizio di archiviazione documentale non attivo"});
      logger.error("Errore nella verifica dell'integrazione WSDM Protocollo per la gara " + codiceGara ,e);
    }

    return integrazioneWSDM;

  }

}