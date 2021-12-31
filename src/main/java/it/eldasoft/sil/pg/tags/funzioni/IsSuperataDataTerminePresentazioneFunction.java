/*
 * Created on 12/06/14
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

public class IsSuperataDataTerminePresentazioneFunction extends AbstractFunzioneTag {

  public IsSuperataDataTerminePresentazioneFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];
    String tipo = (String) params[2];

    //Se tipo=1 si considerano i campi dteoff e oteoff
    //Se tipo=2 si considereno i campi dtepar e otepar
    String campoData = "dteoff";
    String campoOra = "oteoff";
    if("2".equals(tipo)){
      campoData = "dtepar";
      campoOra = "otepar";
    }
    String isSuperataDataTerminePresentazione = "false";
    MEPAManager mepaManager = (MEPAManager) UtilitySpring.getBean("mepaManager", pageContext, MEPAManager.class);
    try {
      String esitoControllo [] = mepaManager.controlloDataConDataAttuale(ngara, campoData, campoOra);
      isSuperataDataTerminePresentazione =  esitoControllo[0];
      if("false".equals(isSuperataDataTerminePresentazione)){
        pageContext.setAttribute("dataScadenza", esitoControllo [1], PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("oraScadenza", esitoControllo [2], PageContext.REQUEST_SCOPE);
      }
    } catch (SQLException e) {
      throw new JspException("Errore durante il controllo se l'acquisizione delle offerte dal portale Appalti e' abilitata", e);
    }

    return isSuperataDataTerminePresentazione;
  }

}