/*
 * Created on 22/10/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.tags.bl.AnagraficaManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che effettua il controllo della valorizzazione di alcuni
 * campi. Se anche uno non risulta valorizzato, allora si deve riportare
 * un messaggio opportuno alla finestra popupPubblicaSuPortale.jsp
 *
 * @author Cristian Febas
 */
public class ControlloRegistrazioneIscrizionePortaleFunction extends AbstractFunzioneTag {

  public ControlloRegistrazioneIscrizionePortaleFunction() {
    super(2, new Class[] { PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

   AnagraficaManager anagraficaManager = (AnagraficaManager) UtilitySpring.getBean(
        "anagraficaManager", pageContext, AnagraficaManager.class);

    // lettura dei parametri di input
    //String modalita = pageContext.getRequest().getParameter("modalita");
    String codimp = pageContext.getRequest().getParameter("codice");

    String messaggio=null;
    String controlloSuperato="SI";


    try {
      messaggio = anagraficaManager.controlliImpresaRegistrabile(codimp,true);
    } catch (SQLException e) {
      throw new JspException("Errore nel controllo dei campi obbligatori ", e);
    }
    if(messaggio!=null){
      controlloSuperato = "NO";
      pageContext.setAttribute("msg", messaggio, PageContext.REQUEST_SCOPE);
    }

    return controlloSuperato;
  }





}