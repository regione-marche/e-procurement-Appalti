/*
 * Created on 10/04/20
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
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.LeggiPubblicazioniManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che determina se una tipologia di documenti è abilitata per la gara
 *
 * @author Marcello.Caminiti
 */
public class IsGruppoDocumentazioneVisibileFunction extends AbstractFunzioneTag {

  public IsGruppoDocumentazioneVisibileFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class});
  }


  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String codgar = (String) params[1];
    String gruppo = (String) params[2];


    LeggiPubblicazioniManager leggiPubblicazioniManager = (LeggiPubblicazioniManager) UtilitySpring.getBean("leggiPubblicazioniManager",
        pageContext, LeggiPubblicazioniManager.class);

    String isGruppoVisibile = "false";

    if (codgar != null) {

      try {
        if(leggiPubblicazioniManager.isGruppoVisibile(codgar, new Long(gruppo)))
          isGruppoVisibile = "true";

      } catch (SQLException e) {
        throw new JspException("Errore durante la verifica che sia abilitata la gestione della tipologia di documenti relativa alla delibera a contrarre per la gara " + codgar, e);
      } catch (GestoreException e) {
        throw new JspException("Errore durante la verifica che sia abilitata la gestione della tipologia di documenti relativa alla delibera a contrarre per la gara " + codgar, e);
      }
    }

    return isGruppoVisibile;
  }

}