/*
 * Created on 18/09/20
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.spring.UtilitySpring;

public class ControlliValiditaFunzAnnullamentoFunction extends AbstractFunzioneTag {

  public ControlliValiditaFunzAnnullamentoFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class  });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String ngara = (String) params[1];
    String tipo = (String) params[2];
    String genere = (String) params[3];
    boolean controlliSuperati = true;
    String esito = "OK";

    try {

      ProfiloUtente profiloUtente = (ProfiloUtente) this.getRequest().getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
          "sqlManager", pageContext, SqlManager.class);
      String select = null;

      OpzioniUtente opzioniUtente = new OpzioniUtente( profiloUtente.getFunzioniUtenteAbilitate());
      if(!opzioniUtente.isOpzionePresente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA)){
        controlliSuperati = false;
      }  else{

        select="select fasgar from gare where ngara=?";
        Long fasgar = (Long)sqlManager.getObject(select, new Object[]{ngara});

        if(fasgar==null){
          controlliSuperati = false;
        }else{
          if("1".equals(tipo) && (fasgar.longValue() < 1 || fasgar.longValue() >= 5))
            controlliSuperati = false;
          else if("2".equals(tipo) && fasgar.longValue() >= 1)
              controlliSuperati = false;
        }
      }
      if(!controlliSuperati)
        esito = "NOK";
      else{
        select="select count(*) from w_invcom where comkey2=? and comtipo = 'FS11' and comstato in (6,7)";
        if("2".equals(tipo))
          select="select count(*) from w_invcom where comkey2=? and comtipo = 'FS10' and comstato in (6,7)";
        Long conteggio = (Long) sqlManager.getObject(select, new Object[]{ngara});
        if(conteggio == null || new Long(0).equals(conteggio))
          esito = "NO-BUSTE";
        else{
          if("1".equals(tipo)){

            if("2".equals(genere))
              select="select count(*) from w_invcom where comkey2=? and comtipo in ('FS11B','FS11C')  and comstato in (6,7,8,13,16,17)";
            else
              select="select count(*) from w_invcom,gare  where codgar1=? and codgar1 != ngara and comkey2=ngara and comtipo in ('FS11B','FS11C')  and comstato in (6,7,8,13,16,17)";
          }else{
            select="select count(*) from w_invcom where comkey2=? and comtipo = 'FS11' and comstato in (6,7)";
          }

          conteggio = (Long) sqlManager.getObject(select, new Object[]{ngara});
          if(conteggio!=null && conteggio.longValue()>0)
            esito = "BUSTE-AC";
        }

      }


    } catch (Exception e) {
      String msg = "Errore nel controllo dei permessi per l'apertura della popoup di annullamento ";
      if("1".equals(tipo))
        msg += "ricezione dei plichi";
      else
        msg += "ricezione domande di partecipazione";
      throw new JspException(msg, e);
    }

    return esito;
  }

}
