package it.eldasoft.sil.pg.tags.funzioni;
/*
 * Created on 28/10/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */


import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
/**
 * Funzione per verificare se esistono messaggi FS2 e FS4 in Database
 *
 * @author Marcello Caminiti
 */
public class EsistonoComunicazioniIscrAggFunction extends AbstractFunzioneTag{
	public EsistonoComunicazioniIscrAggFunction() {
	    super(1, new Class[]{PageContext.class});
	  }

	@Override
  public String function(PageContext pageContext, Object[] params)
    throws JspException {
	      String ret="false";
		  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
		      "sqlManager", pageContext, SqlManager.class);


          try {
            String select="select count(idprg) from W_INVCOM where idprg = ? and comstato = ? and (comtipo = ? or comtipo = ?)";
			Long numRichieste = (Long)sqlManager.getObject(select, new Object[]{"PA","5","FS2","FS4"});
            if(numRichieste!=null && numRichieste.longValue()>0)
              ret="true";

		  } catch (SQLException e) {
		    throw new JspException("Errore nella determinazione delle richieste di iscrizione e di aggiornamento", e);
		  }
		return ret;
	}
}
