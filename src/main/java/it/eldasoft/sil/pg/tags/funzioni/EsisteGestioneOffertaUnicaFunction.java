/*
 * Created on 29-03-2017
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che controlla se vi sono dei lotti aggiudicati in via definitiva
 * @author Marcello Caminiti
 */
public class EsisteGestioneOffertaUnicaFunction extends AbstractFunzioneTag {

  public EsisteGestioneOffertaUnicaFunction() {
    super(3, new Class[] { PageContext.class,String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        pageContext, PgManagerEst1.class);

    String codgar = (String) GeneralTagsFunction.cast("string", params[1]);
    String bustalotti= (String) GeneralTagsFunction.cast("string", params[2]);
    String ret="true";

    if (codgar != null && codgar.length()>0){
      try {
        boolean gestioneOffEco = pgManagerEst1.gestioneOffertaEconomicaDaCostofisso(codgar, bustalotti);
        if(!gestioneOffEco)
          ret = "false";
      } catch (SQLException e) {
          throw new JspException(
              "Errore durante la lettura del campo GARE1.COSTOFISSO per la gara " + codgar, e);
      } catch (GestoreException e) {
        throw new JspException(
            "Errore durante la lettura del campo GARE1.COSTOFISSO per la gara " + codgar, e);
      }
    }
    return ret;
  }
}
