/*
 * Created on 10/05/2021
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
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che recupera la descrizione utente 
 *
 * @author Cristian.Febas
 */
public class GetDescrizioneUtenteStipulaFunction extends AbstractFunzioneTag {

  public GetDescrizioneUtenteStipulaFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String utente = (String) params[1];
    utente = UtilityStringhe.convertiNullInStringaVuota(utente);
    String descrizioneUtente = null;
    if(!"".equals(utente)){
      Long syscon = Long.valueOf(utente);

      String selectDescrUtente = "select sysute from usrsys where syscon = ?";

      try {
    	  descrizioneUtente = (String)sqlManager.getObject(selectDescrUtente, new Object[] {syscon});
        } catch (SQLException s) {
          throw new JspException("Errore durante la lettura della descrizione dell'utente " + syscon,
              s);
        }     

      return descrizioneUtente;

    }

    return null;
    
  }

}
