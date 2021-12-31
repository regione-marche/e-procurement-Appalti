/*
 * Created on 02/03/2021
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

import org.apache.commons.lang.StringUtils;

public class EsistonoDitteStatoAmmissioneSpecificoFunction extends AbstractFunzioneTag {

  public EsistonoDitteStatoAmmissioneSpecificoFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String codgar = (String) params[1];
    String step = (String) params[2];
    String statoAmmissione = (String) params[3];

    String esistonoDitteStatoAmmissioneSpecifico= "false";

    if (codgar != null && step!=null && !"".equals(step)) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      try {
        String selectStatoSpecifico = "select count(dittao) from v_ditgammis where codgar=? and fasgar=? and ammgar=?";
        Long stepLong = new Long(step);
        Long fase = null;
        if(stepLong.longValue()== GestioneFasiGaraFunction.FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA)
          fase = new Long (2);
        else if(stepLong.longValue()== GestioneFasiGaraFunction.FASE_CONCLUSIONE_COMPROVA_REQUISITI)
          fase = new Long (4);
        else if(stepLong.longValue()== GestioneFasiGaraFunction.FASE_VALUTAZIONE_TECNICA)
          fase = new Long (5);
        else if(stepLong.longValue()== GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE)
          fase = new Long (6);
        else if(stepLong.longValue()==GestioneFasiRicezioneFunction.FASE_APERTURA_DOMANDE_DI_PARTECIPAZIONE)
          fase = new Long (-4);
        
        statoAmmissione = StringUtils.stripToEmpty(statoAmmissione);
        if(!"".equals(statoAmmissione)) {
            Long conteggio = (Long) sqlManager.getObject(selectStatoSpecifico, new Object[] {codgar,fase,statoAmmissione});
            if (conteggio != null && conteggio.longValue() > 0) {
              esistonoDitteStatoAmmissioneSpecifico = "true";
            }
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo del campo ammissione delle ditte della gara", e);
      }
    }

    return esistonoDitteStatoAmmissioneSpecifico;
  }

}