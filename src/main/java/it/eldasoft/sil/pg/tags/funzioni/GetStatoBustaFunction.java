/*
 * Created on 08/01/21
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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che ritorna lo stato della comunicazione di una ditta
 *
 * @author Marcello Caminiti
 */
public class GetStatoBustaFunction extends AbstractFunzioneTag {

  public GetStatoBustaFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class });
  }


  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];
    String ditta = (String) params[2];
    String busta = (String) params[3];

    String stato = null;

    if (ngara != null) {
      MEPAManager mepaManager = (MEPAManager) UtilitySpring.getBean("mepaManager",
          pageContext, MEPAManager.class);
      try {

        stato = mepaManager.GetStatoBusta(ngara, ditta, busta);
        if("".equals(stato))
          stato = null;

      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo delle buste elaborate", e);
      }
    }

    return stato;
  }

}