/*
 * Created on 11/Set/09
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
 * La funzione verifica se per la gara in esame sono presenti
 * occorrenze di DPUN con PUNTEG valorizzato. In questo caso
 * la funzione restituisce "VERO"
 *
 * @author Marcello Caminiti
 */
public class EsistonoPunteggiDitteFunction extends AbstractFunzioneTag {

  public EsistonoPunteggiDitteFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ngara = (String) params[1];
    String codiceGara = (String) params[2];

    //if (codiceGara.equals(ngara)) offertaUnica= true;
    String blocco= "FALSO";
    try {
    	if (ngara != null && !"".equals(ngara)){
    		//Si deve controllare se la gara è una gara divisa in lotti con offerta unica.
 		   boolean offertaUnica = false;

 		   String parametro="";
    		String select = "select bustalotti from v_gare_torn,gare where codgar = ? and v_gare_torn.codgar=gare.ngara";

    		Long bustalotti = (Long) sqlManager.getObject(
					select, new Object[] { codiceGara });

    		if(bustalotti != null && bustalotti.intValue() == 2) offertaUnica= true;

		    //Nel caso di gara divisa in lotti con offerta unica il controllo deve essere effettuato su tutti i lotti di gara
		    if (offertaUnica) {
		    	select="select count(dpun.ngara) from dpun,gare where dpun.ngara=gare.ngara and gare.codgar1 = ? and dpun.punteg is not null";
		    	parametro=codiceGara;
		    }
		    else {
		    	select="select count(ngara) from dpun where ngara = ? and punteg is not null";
		    	parametro=ngara;
		    }

		    Long numOccorrenze = (Long) sqlManager.getObject(
		    		select, new Object[] { parametro});

		    if (numOccorrenze.longValue() >0)
		    	blocco = "VERO";
    	}

    }catch (SQLException e) {
        throw new JspException(
                "Errore durante la valutazione dei punteggi delle ditte ", e);
          }

    return blocco;
      }

}