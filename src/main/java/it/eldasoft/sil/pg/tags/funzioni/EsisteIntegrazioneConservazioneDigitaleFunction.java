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

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.utils.ConfigurazionePerUffintUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.sil.pg.bl.tasks.ArchiviazioneDocumentiManager;
import it.eldasoft.utils.spring.UtilitySpring;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;

/**
 * Controlla se esiste l'integrazione con il sistema di conservazione digitale per la SA.
 *
 * @author Manuel Bridda
 */
public class EsisteIntegrazioneConservazioneDigitaleFunction extends AbstractFunzioneTag {

  public EsisteIntegrazioneConservazioneDigitaleFunction(){
    super(2, new Class[]{PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String codiceGara = (String) params[1];
    Logger logger = Logger.getLogger(EsisteIntegrazioneConservazioneDigitaleFunction.class);

    String integrazioneConservazione="0";
    ArchiviazioneDocumentiManager archiviazioneDocumentiManager = (ArchiviazioneDocumentiManager) UtilitySpring.getBean("archiviazioneDocumentiManager",
        pageContext, ArchiviazioneDocumentiManager.class);

    try {
      integrazioneConservazione = archiviazioneDocumentiManager.getSistemaConservazione(codiceGara);
    } catch (SQLException e) {
      UtilityStruts.addMessage(this.getRequest(), "error",
          "errors.generico",new Object[]{"Errore nella verifica dell'integrazione del sistema di conservazione digitale per la gara" + codiceGara});
      logger.error("Errore nella verifica dell'integrazione del sistema di conservazione digitale per la gara " + codiceGara ,e);
    }

    return integrazioneConservazione;

  }
}