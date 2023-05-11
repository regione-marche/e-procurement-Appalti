/*
 * Created on 04-04-2012
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

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
/**
 * Funzione che elimina dalla sessione la variabile "filtroDitte"
 * e sbianca i dati dell'archivio popup-trova-filtroditte.jsp
 * 
 * @author Marcello Caminiti
 */
public class PulisciSessioneFiltroDitteFunction extends AbstractFunzioneTag {

  public PulisciSessioneFiltroDitteFunction() {
    super(1, new Class[] { Object.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    HttpSession sessione = pageContext.getSession();
    sessione.removeAttribute("trovaDITG");
    sessione.removeAttribute("filtroDitte");
    
    return null;
  }
}
