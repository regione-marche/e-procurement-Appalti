/*
 * Created on 23/10/17
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
 * Controlla se è abilitata la funzione Fascicolo documentale
 *
 * @author Marcello Caminiti
 */
public class IsFascicoloDocumentaleValidoFunction extends AbstractFunzioneTag {

  public IsFascicoloDocumentaleValidoFunction(){
    super(3, new Class[]{PageContext.class,String.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String isFascicoloDocumentaleAbilitato="0";
    String codiceGara = (String) params[1];
    String idconfi = (String) params[2];

    GestioneWSDMManager gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        pageContext, GestioneWSDMManager.class);

    Logger logger = Logger.getLogger(IsFascicoloDocumentaleValidoFunction.class);

    try {
      boolean isIntegrazioneWSDMAttivaValida = gestioneWSDMManager.isFascicoloDocumentaleAbilitato(codiceGara,idconfi);
      if(isIntegrazioneWSDMAttivaValida)
        isFascicoloDocumentaleAbilitato="1";
    } catch (GestoreException e) {

        UtilityStruts.addMessage(this.getRequest(), "error",
            "wsdmconfigurazione.configurazioneleggi.remote.error",new Object[]{": servizi di protocollazione o di archiviazione documentale non attivi"});
        logger.error("Errore nella verifica dell'abilitazione della Funzione documentale per la gara " + codiceGara ,e);

    } catch (SQLException e) {
      UtilityStruts.addMessage(this.getRequest(), "error",
          "wsdmconfigurazione.configurazioneleggi.remote.error",new Object[]{": servizi di protocollazione o di archiviazione documentale non attivi"});
      logger.error("Errore nella verifica dell'abilitazione della Funzione documentale per la gara " + codiceGara ,e);
    }

    return isFascicoloDocumentaleAbilitato;

  }

}