/*
 * Created on 21-09-2015
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
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Estrae il campo GARE.BUSTALOTTI necessario nella pagina torn-pagine-scheda.jsp
 * per rendere visibile o meno dei tab
 *
 *
 * @author Marcello Caminiti
 */
public class GetBustalottiFunction extends AbstractFunzioneTag {

  public GetBustalottiFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String result = "0";
    String codiceGara = UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);

    if(codiceGara==null || "".equals(codiceGara))
      codiceGara = (String) params[1];

    if (codiceGara != null && codiceGara.length() > 0) {
      String select="select bustalotti from gare where ngara=(select codgar1 from gare where ngara=?)";
      if(codiceGara.indexOf("CODGAR")>0){
        select = "select bustalotti from gare,torn where ngara=codgar and codgar=?";
      }
      codiceGara = codiceGara.substring(codiceGara.indexOf(":") + 1);

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);

      Long bustalotti = null;
      try {
        bustalotti = (Long) sqlManager.getObject(select, new Object[] { codiceGara });
      } catch (SQLException s) {
        throw new JspException("Errore durante la lettura del campo BUSTALOTTI della gara "
            + codiceGara, s);
      }

      if (bustalotti != null)
        result = String.valueOf(bustalotti.intValue());
      else
        result = "";
    }
    return result;
  }

}