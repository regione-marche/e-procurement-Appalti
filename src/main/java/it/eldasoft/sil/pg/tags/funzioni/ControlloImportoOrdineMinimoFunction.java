/*
 * Created on 18-09-2014
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
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per eseguire il controllo dell'importo ordine minimo
 *
 * @author Marcello Caminiti
 */
public class ControlloImportoOrdineMinimoFunction extends AbstractFunzioneTag {



  public ControlloImportoOrdineMinimoFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @SuppressWarnings("unchecked")
  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String id = (String) params[1];
    String ditta = (String) params[2];
    Boolean controlliSuperati=null;

    MEPAManager mepaManager = (MEPAManager) UtilitySpring.getBean("mepaManager",
        pageContext, MEPAManager.class);

    HashMap<String, Object> esitoControlli = null;
    try {
      esitoControlli= mepaManager.controlloImportoMinimo(new Long(id),ditta);
    }  catch (SQLException e) {
      pageContext.setAttribute("erroreOperazione","1",PageContext.REQUEST_SCOPE);
      throw new JspException("Errore nel controllo dei dati", e);
    }

    if(esitoControlli!=null){
      pageContext.setAttribute("messaggi",esitoControlli.get("msg"),PageContext.REQUEST_SCOPE);
      controlliSuperati = (Boolean)esitoControlli.get("esito");
      pageContext.setAttribute("nessunOrdineminImpostato",esitoControlli.get("nessunOrdineminImpostato"),PageContext.REQUEST_SCOPE);
    }

    return controlliSuperati.toString();
  }





}