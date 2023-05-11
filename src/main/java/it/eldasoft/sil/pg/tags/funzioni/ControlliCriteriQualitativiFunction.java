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

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class ControlliCriteriQualitativiFunction extends AbstractFunzioneTag {

  public ControlliCriteriQualitativiFunction() {
    super(5, new Class[] { PageContext.class, String.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    String ngara = (String) params[1];
    String codgar = (String) params[2];
    String esitoControllo = "ok";
    String bustalotti = (String) params[3];
    String isValutazioneCommissione = (String) params[4];
    List<?> listaLotti = null;

    try {
      if ("2".equals(bustalotti)) {
        listaLotti = sqlManager.getListVector("select g.ngara from gare g, gare1 g1 where g.codgar1=? and g.ngara=g1.ngara and sezionitec = '1'", new Object[] {ngara});
      } else {
        listaLotti = sqlManager.getListVector("select ngara from gare where ngara=?", new Object[] {ngara});
      }
      if (listaLotti != null) {
        String lotto = null;
        ControlloAllineamentoG1cridefG1crivalFunction functionCriteri = new ControlloAllineamentoG1cridefG1crivalFunction();
        ControlloGiudizioCommissioneFunction functionCommissione = new ControlloGiudizioCommissioneFunction();
        String esitoControlloLotto = null;
        for (int i = 0; i < listaLotti.size(); i++) {
          lotto = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).getStringValue();
          esitoControlloLotto = functionCriteri.function(pageContext, new Object[]{pageContext, lotto, "1", "1"});
          if (!"ok".equals(esitoControlloLotto)) {
            if ("true".equals(isValutazioneCommissione)) {
              esitoControlloLotto = functionCommissione.function(pageContext, new Object[]{pageContext, lotto, "1", codgar, "1"});
              if (!"ok".equals(esitoControlloLotto)) {
                esitoControllo = "nok-commissione";
                break;
              }
            } else {
              esitoControllo = "nok";
              break;

            }
          }

        }
      }
    } catch (SQLException e) {
      throw new JspException("Errore durante il controllo sull'esistenza di occorrenze di G1CRIVAL associate a G1CRIDEF", e);
    }
    return esitoControllo;
  }

}