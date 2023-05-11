/*
 * Created on 19/05/2022
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
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetStatoGaraFunction extends AbstractFunzioneTag {

  public GetStatoGaraFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    String codgar = (String) params[1];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    Long cod_stato_gara = null;

    try {
      if(codgar!=null){
        String selectV_GARE_STATOESITO = "select codstato " +
        		"from v_gare_statoesito where codgar=?";

        cod_stato_gara = (Long) sqlManager.getObject(selectV_GARE_STATOESITO, new Object[] {codgar});
        
      }
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura della tabella v_gare_statoesito", e);
    }

    return String.valueOf(cod_stato_gara);
  }

}
