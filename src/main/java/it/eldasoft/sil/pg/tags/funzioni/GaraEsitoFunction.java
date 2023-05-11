/*
 * Created on 04/03/13
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
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che ricava l'esito della gara
 *
 * @author Sara Santi
 */
public class GaraEsitoFunction extends AbstractFunzioneTag {

  public GaraEsitoFunction() {
    super(2, new Class[] { PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
        "sqlManager", pageContext, SqlManager.class);

    String statoEsito = "";

    String codice = (String)params[1];

    try {
      String select="select stato,esito from v_gare_statoesito where codice=?";
      Vector datiStatoEsito = sqlManager.getVector(select, new Object[]{codice});
      if(datiStatoEsito!=null && datiStatoEsito.size()>0){
        String stato = (String) SqlManager.getValueFromVectorParam(datiStatoEsito, 0).getValue();
        if (stato != null)
          statoEsito = stato;
        String esito = (String) SqlManager.getValueFromVectorParam(datiStatoEsito, 1).getValue();
        if (esito != null){
          if (stato != null)
            statoEsito += " ";
          statoEsito += esito;
        }
      }

    } catch (SQLException e) {
        throw new JspException(
            "Errore durante la selezione dell'esito della gara ",e);

    }


    return statoEsito;
  }

}
