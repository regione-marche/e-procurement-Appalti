/*
 * Created on 19-02-2019
 *
  * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che determina se la selezione degli operatori economici
 * da elenco risulta automatica da configurazione
 *
 * @author Cristian Febas
 */
public class IsGaraConSelezioneAutomaticaDitteFunction extends AbstractFunzioneTag {

  public IsGaraConSelezioneAutomaticaDitteFunction() {
    super(1, new Class[] { PageContext.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        pageContext, TabellatiManager.class);

    String selezioneAutomatica = "false";
    List<Tabellato> listaTabellato = tabellatiManager.getTabellato("A1101");
    if (listaTabellato != null && listaTabellato.size() > 0) {
      String descrTab = (listaTabellato.get(0)).getDescTabellato();
      if (descrTab != null && (descrTab.startsWith("1") || descrTab.startsWith("2") ))
        selezioneAutomatica = "true";
    }

    return selezioneAutomatica;
  }

}
