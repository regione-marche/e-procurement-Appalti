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
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
/**
 * Funzione per il calcolo del numero di ids associati
 * ad uno specifico utente
 *
 * @author Cristian Febas
 */
public class GetNumeroIdsUtenteFunction extends AbstractFunzioneTag{
	public GetNumeroIdsUtenteFunction() {
	    super(2, new Class[]{PageContext.class});
	  }

	@Override
  public String function(PageContext pageContext, Object[] params)
    throws JspException {

	  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
	        pageContext, SqlManager.class);

      ProfiloUtente profilo = (ProfiloUtente) pageContext.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);

      Long idUtente = new Long(profilo.getId());
      String abilitazioneGare = profilo.getAbilitazioneGare();

	    String msg = "Consulta Ids";
        String numIdsUtente = "0";

	    if(!"A".equals(abilitazioneGare)){
	        String selectUtenti_Ids="select count(*) from v_lista_ids where id_utente = ?" +
	        		" and (flag_respingi is null or flag_respingi = 2) and (flag_evadi is null or flag_evadi = 2)  and (flag_annulla is null or flag_annulla = 2) ";
	        try {
	          Long nIdsUtente = (Long) sqlManager.getObject(selectUtenti_Ids, new Object[]{idUtente});
	          if (nIdsUtente!= null){
	            numIdsUtente = nIdsUtente.toString();
	          }

	        } catch (SQLException e) {
	          throw new JspException(
	              "Errore durante la lettura nel numero ids associati all'utente loggato!", e);
	        }
	        msg = msg + "("+numIdsUtente+")";
	    }
	    pageContext.setAttribute("msgConsultaIds", msg, PageContext.REQUEST_SCOPE);
	    return numIdsUtente;
	  }


}
