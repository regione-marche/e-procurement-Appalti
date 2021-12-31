/*
 * Created on 10/07/17
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
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class ControlloValorizzazioneValnumFunction extends AbstractFunzioneTag {

  public ControlloValorizzazioneValnumFunction() {
    super(2, new Class[] { PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];

    String esitoControllo = "ok";
    String selectConteggioG1CrivalValnumNullo="select count(c.id) from g1cridef c, goev g where c.ngara=? and g.ngara = c.ngara and g.necvan = c.necvan and g.tippar = 2 and c.formato in (50,51,52) "
        + "and exists (select v.id from g1crival v where v.idcridef=c.id and v.ngara=? and v.dittao=? and v.valnum is null)";


    if (ngara != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      String select="select dittao from ditg where ngara5=? and (INVOFF in ('0', '1') or INVOFF is null) "
          + "and (FASGAR > 5 or FASGAR = 0 or FASGAR is null) order by NUMORDPL asc,NOMIMO  asc";

      try {
        //Si devono prendere in considerazione per i controlli solo le ditte presenti nella lista dell'offerte economica
        List<?> listaDitteGara=sqlManager.getListVector(select, new Object[]{ngara});
        if(listaDitteGara!=null && listaDitteGara.size()>0){
          String ditta = null;

          Long conteggio=null;
          for(int i=0;i<listaDitteGara.size();i++){
            ditta = SqlManager.getValueFromVectorParam(listaDitteGara.get(i), 0).getStringValue();
            conteggio = (Long) sqlManager.getObject(selectConteggioG1CrivalValnumNullo, new Object[] {ngara, ngara,ditta});
            if (conteggio != null && conteggio.longValue() > 0) {
              esitoControllo = "nok";
              break;
            }
          }
        }
      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo sull'esistenza di occorrenze di G1CRIVAL associate a G1CRIDEF", e);
      }
    }
    return esitoControllo;
  }

}