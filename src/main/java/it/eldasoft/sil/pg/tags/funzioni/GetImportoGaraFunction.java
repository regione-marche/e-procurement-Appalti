/*
 * Created on 04/10/17
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

/**
 * Funzione che restituisce il GARE.IMPAPP o TORN.IMPTOR (nel caso di gara ad offerta unica)
 *
 * @author Marcello Caminiti
 */
public class GetImportoGaraFunction extends AbstractFunzioneTag {

  public GetImportoGaraFunction(){
    super(3, new Class[]{PageContext.class, String.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    String codiceGara = (String) params[1];
    String isGaraOffUnica = (String) params[2];

    try {
      String select="select impapp from gare where ngara=?";
      if("true".equals(isGaraOffUnica))
        select="select IMPTOR from TORN where CODGAR = ?";
      Double importoGara = (Double) sqlManager.getObject(select, new Object[]{codiceGara});

      if(importoGara == null)
        importoGara = new Double(0);
      pageContext.setAttribute("importoGara", importoGara, PageContext.REQUEST_SCOPE);


    } catch (SQLException e) {
      throw new JspException("Errore nell'estrazione dell'importo a base " +
            "di gara della gara " + codiceGara, e);
    }
    return null;
  }

}