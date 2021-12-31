/*
 * Created on 28/07/17
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
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class EsistonoLavorazioniOEPVDittePunteggioValorizzatoFunction extends AbstractFunzioneTag {

  public EsistonoLavorazioniOEPVDittePunteggioValorizzatoFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String selezionelottiLavorazioniOEPV = (String) params[1];
    String tipo = (String) params[2];
    String esito = "no";

    if (selezionelottiLavorazioniOEPV != null) {
      SqlManager sqlgManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      try{
        List listaLotti = sqlgManager.getListVector(selezionelottiLavorazioniOEPV, null);
        if(listaLotti!=null && listaLotti.size()>0){
          String lotto=null;
          if("1".equals(tipo))
            tipo="TEC";
          else
            tipo="ECO";
          boolean controlloPunteggio;
          PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1", pageContext, PgManagerEst1.class);
          for(int i=0;i<listaLotti.size();i++){
            lotto = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).getStringValue();
            controlloPunteggio= pgManagerEst1.esistonoDittePunteggioValorizzato(lotto, tipo);
            if(controlloPunteggio){
              esito = "si";
              break;
            }
          }
        }
      }catch (SQLException e) {
        throw new JspException("Errore durante il controllo dei punteggi delle ditte", e);
      }
    }

    return esito;
  }

}