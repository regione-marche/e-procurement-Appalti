/*
 * Created on 09-08-2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

/**
 * Gestore adoperato nella lista w_invcom-lista-ComunicazioniFS12-Da-Portale.jsp
 * per estrarre il codgar a partire dal valore di ngara contenuto in un JdbcParametro
 *
 * @author Caminiti
 */
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetCodgar1Function extends AbstractFunzioneTag {

  public GetCodgar1Function() {
    super(2, new Class[]{PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String codiceGara = null;
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

     String numeroGara = (String) params[1];

      try {
        codiceGara = (String) sqlManager.getObject(
            "select codgar1 from gare,torn where ngara= ? and codgar1=codgar", new Object[]{numeroGara});
      } catch (SQLException s){
        throw new JspException("Errore durante la lettura del codice di gara " +
              "(GARE.CODGAR1)", s);
      }

    if(codiceGara != null && codiceGara.length() > 0)
      return codiceGara;
    else
      return "";
  }
}