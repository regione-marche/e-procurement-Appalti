/*
 * Created on 27/01/2017
 *
 * Copyright (c) Maggioli S.p.A. - Divisione EldaSoft
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

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per il calcolo del numero di aggiornamenti delle categorie per una ditta in sospeso.
 *
 * @author M. Caminiti
 */
public class GetVariazioniAggiornamentiCategorieSospesoFunction extends AbstractFunzioneTag {

  public GetVariazioniAggiornamentiCategorieSospesoFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    String codice = (String) params[1];
    String ditta = (String) params[2];

    String select = "select id,logmsg from garacquisiz where ngara = ? and codimp = ? and stato = ? ";
    String messaggio = null;
    Long identificativo = null;
    try {
      Vector dati = sqlManager.getVector(select, new Object[]{codice, ditta, new Long(1)});
      if(dati!=null && dati.size()>0){
        identificativo = SqlManager.getValueFromVectorParam(dati, 0).longValue();
        messaggio = SqlManager.getValueFromVectorParam(dati, 1).stringValue();
      }
      pageContext.setAttribute("identificativo",identificativo, PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura della tabella GARACQUISIZ", e);
    } catch (GestoreException e) {
      throw new JspException("Errore nella lettura della tabella GARACQUISIZ", e);
    }

    return messaggio;
  }

}
