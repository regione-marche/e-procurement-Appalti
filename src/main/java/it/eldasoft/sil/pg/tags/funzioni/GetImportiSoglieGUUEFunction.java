/*
 * Created on 14/03/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Estrae le soglie comunitarie per Gare dal tabellato A1019
 *
 * @author Stefano.Sabbadin
 */
public class GetImportiSoglieGUUEFunction extends AbstractFunzioneTag {

  public GetImportiSoglieGUUEFunction() {
    super(1, new Class[] { PageContext.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        pageContext, TabellatiManager.class);

    String descrizione = tabellatiManager.getDescrTabellato("A1125", "1");
    String sogliaLavori = PgManager.getNumeroFromInizioDescrizione(descrizione);

    descrizione = tabellatiManager.getDescrTabellato("A1125", "2");
    String sogliaFornitureServizi = PgManager.getNumeroFromInizioDescrizione(descrizione);



    return sogliaLavori + "#" + sogliaFornitureServizi;
  }

}