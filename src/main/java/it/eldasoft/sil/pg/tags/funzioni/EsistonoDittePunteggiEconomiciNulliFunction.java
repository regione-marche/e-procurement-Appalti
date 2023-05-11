/*
 * Created on 02/03/16
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
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class EsistonoDittePunteggiEconomiciNulliFunction extends AbstractFunzioneTag {

  public EsistonoDittePunteggiEconomiciNulliFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];
    String isGaraLottiConOffertaUnica = (String) params[2];
    String controlloTuttiPunteggiPresenti=(String) params[3];

    String esistonoDittePunteggiEconomiciNulli = "false";
    String punteggiTuttiValorizzati ="si";

    if (ngara != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      Object parametri[] = null;
      try {

        String selectDITG = "select count(*) from ditg,gare where ngara5 = ? and puneco is null "
          + " and gare.ngara=ditg.ngara5 and gare.modlicg=6"
          + "and (DITG.INVOFF in ('0', '1') or DITG.INVOFF is null) "
          + "and (DITG.FASGAR > 6 or DITG.FASGAR = 0 or DITG.FASGAR is null)";

        //nel caso di offerta unica i punteggi si trovano nelle ditte associate ai lotti, si
        //devono escludere le ditte associate alla gara fittizia
        // Si devono escludere i lotti con costofisso=1
        if("true".equals(isGaraLottiConOffertaUnica)){
          selectDITG = "select count(ngara5) from ditg,gare, gare1 where codgar5 = ? and ngara5!=codgar5 "
            + "and gare1.ngara = gare.ngara and (costofisso is null or costofisso <>'1') and puneco is null "
        	+ "and gare.ngara=ditg.ngara5 and gare.modlicg=6 and (ditg.fasgar > 6 or ditg.fasgar=0 or ditg.fasgar is null) "
            + "and dittao in (select d.dittao from ditg d where d.codgar5=? and d.ngara5=d.codgar5 "
            + "and (d.INVOFF in ('0', '1') or d.INVOFF is null) "
            + "and (d.FASGAR > 6 or d.FASGAR = 0 or d.FASGAR is null))";
          parametri = new Object[]{ngara,ngara};
        }else{
          parametri = new Object[]{ngara};
        }

        Long conteggio = (Long) sqlManager.getObject(selectDITG, parametri);

        if (conteggio != null && conteggio.longValue() > 0) {
          esistonoDittePunteggiEconomiciNulli = "true";
        }else{
          if("true".equals(controlloTuttiPunteggiPresenti) && !"true".equals(isGaraLottiConOffertaUnica)){
            selectDITG = "select dittao from ditg where ngara5 = ? and (fasgar > 6 or fasgar=0 or fasgar is null)";
            Long ripeco = (Long)sqlManager.getObject("select ripeco from gare1 where ngara=?", new Object[]{ngara});
            if(new Long(1).equals(ripeco) || new Long(2).equals(ripeco)){
              List listaDitte = sqlManager.getListVector(selectDITG, new Object[]{ngara});
              if(listaDitte!= null && listaDitte.size()>0){
                MEPAManager mepaManager = (MEPAManager) UtilitySpring.getBean("mepaManager", pageContext, MEPAManager.class);
                for(int i=0;i<listaDitte.size();i++){
                  String ditta = SqlManager.getValueFromVectorParam(listaDitte.get(i), 0).getStringValue();
                  if(!mepaManager.controlloCriteriTuttiValorizzati(ngara, ditta, new Long(2))){
                    punteggiTuttiValorizzati = "no";
                    break;
                  }
                }
              }
            }
            pageContext.setAttribute("punteggiTuttiValorizzati",punteggiTuttiValorizzati, PageContext.REQUEST_SCOPE);
          }
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo del campo punteggio economico delle ditte della gara", e);
      }
    }

    return esistonoDittePunteggiEconomiciNulli;
  }

}