/*
 * Created on 14-11-2012
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
 * Funzione per stabilire se per la tipologia considerata vi è più di un livello.
 *
 * @author Marcello Caminiti
 */
public class EsistonoLivelliIntermediCategoriaFunction extends AbstractFunzioneTag {

  public EsistonoLivelliIntermediCategoriaFunction() {
    super(2 , new Class[] { PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String tipoCategoria = (String) params[1];
    String ret ="false";

    String select="select max(numliv) from v_cais_tit where tiplavg=?";
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      Long numMaxLivelli = (Long)sqlManager.getObject(select,new Object[]{tipoCategoria});
      if(numMaxLivelli!= null && numMaxLivelli.longValue()>1)
        ret ="true";

    } catch (SQLException e) {
      throw new JspException("Errore nella lettura del campo numliv della view v_cais_tit)",e);
    }
    return ret;
  }

}
