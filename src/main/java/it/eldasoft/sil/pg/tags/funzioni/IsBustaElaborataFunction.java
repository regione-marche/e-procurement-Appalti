/*
 * Created on 29/01/16
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
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che legge il valore di W_INVCOM associato ad una ditta e restituisce
 *  "NonEsiste" se non è presente l'occorrenza, "No" se lo stato è "5" o"7", "Si"
 *  altrimenti.
 *
 * @author Stefano.Sabbadin
 */
public class IsBustaElaborataFunction extends AbstractFunzioneTag {

  public IsBustaElaborataFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class });
  }


  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];
    String ditta = (String) params[2];
    String busta = (String) params[3];

    String IsBustaElaborata = "false";

    if (ngara != null) {
      MEPAManager mepaManager = (MEPAManager) UtilitySpring.getBean("mepaManager", pageContext, MEPAManager.class);
      try {

        IsBustaElaborata = mepaManager.IsBustaElaborata(ngara, ditta, busta);

      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo delle buste elaborate", e);
      }
    }

    return IsBustaElaborata;
  }

}