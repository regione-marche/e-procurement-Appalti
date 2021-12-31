/*
 * Created on 21-11-2012
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
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Viene effettuato il conteggio delle occorrenze visualizzate nella pagina iscrizcat-listaScheda.jsp
 * quando si è in modifica. Se il numero di tali occorrenze è maggiore della soglia, allora la funzione
 * ritorno "Si"
 *
 * @author Marcello Caminiti
 */
public class isNumOccorrenzeCategorieElencoOltreSogliaFunction extends AbstractFunzioneTag {

  public isNumOccorrenzeCategorieElencoOltreSogliaFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String where = (String)params[1];
    Long soglia = Long.parseLong((String)params[2]);
    Long numOccorrenze = null;
    String ret = "No";

    try {

      String select="select count(caisim) from V_ISCRIZCAT_TIT where " + where ;
      numOccorrenze = (Long) sqlManager.getObject(select, null);
    } catch (SQLException s){
      throw new JspException("Errore durante il conteggio del numero di categorie dell'elenco " , s);
    }
    if(numOccorrenze!= null && numOccorrenze.longValue()>soglia.longValue())
      ret="Si";

    return ret;
  }

}