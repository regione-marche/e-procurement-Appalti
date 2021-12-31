/*
 * Created on 22/12/15
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che controlla FASGAR.GARE per verificare se sono rispettate le condizioni
 * specificate nei controlli javascript per la visualizzazione dei pulsanti per le funzioni di
 * acquisizioni da portale
 *
 * @author Marcello Caminiti
 */
public class EsisteBloccoCondizioniFasiAcquisizioniFunction extends AbstractFunzioneTag {

  public EsisteBloccoCondizioniFasiAcquisizioniFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    MEPAManager mepaManager = (MEPAManager) UtilitySpring.getBean("mepaManager",
        pageContext, MEPAManager.class);

    boolean ret= false;
    String ngara = (String) params[1];
    String fasgarCorrettaString= (String) params[2];
    Long fasgarCorretta = new Long(fasgarCorrettaString);

    try {
      ret = mepaManager.esisteBloccoCondizioniFasiAcquisizioni(ngara, fasgarCorretta);
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante i controlli preliminari sulla fase di gara ", e);
    }

    return new Boolean(ret).toString();

  }

}
