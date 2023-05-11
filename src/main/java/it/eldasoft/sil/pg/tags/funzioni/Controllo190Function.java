/*
 * Created on 16/mar/2018
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ControlloDati190Manager;
import it.eldasoft.utils.spring.UtilitySpring;


public class Controllo190Function extends AbstractFunzioneTag  {

  public Controllo190Function() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  static Logger      logger = Logger.getLogger(ControlloDati190Manager.class);
  
  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    String result = "0";
    // TODO Auto-generated method stub
    ControlloDati190Manager controlMan = (ControlloDati190Manager) UtilitySpring.getBean("controlloDati190Manager",
        pageContext, ControlloDati190Manager.class);
    
    try {
      HashMap errori = controlMan.controllaDati((String) params[1]);
      pageContext.setAttribute("ngara", errori.get("ngara"));
      pageContext.setAttribute("GaraLotti", errori.get("GaraLotti"));
      pageContext.setAttribute("listaGare", errori.get("liste"));
    } catch (SQLException e) {
      logger.info("Si è verificato un errore nel tentativo di eseguire il controllo sulla validità dei dati");
    }
    return result;
  }

}
