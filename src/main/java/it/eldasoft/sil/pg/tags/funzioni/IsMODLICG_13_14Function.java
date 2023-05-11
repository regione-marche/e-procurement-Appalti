/*
 * Created on 05-Giu-2009
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
 * Viene fatto il controllo che la gara abbia MODLICG= 13 o 14
 * Nel caso di gare ad offerta unica, il controllo diventa che
 * esista almeno un lotto che rispetti il criterio
 *
 * @author Marcello Caminiti
 */
public class IsMODLICG_13_14Function extends AbstractFunzioneTag {

  public IsMODLICG_13_14Function() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String result = "false";
    String numeroGara = (String) params[1];
    String garaOffertaUnica = (String) params[2];

    if (numeroGara != null && numeroGara.length() > 0) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

      try {
        if(!"true".equals(garaOffertaUnica)){
          Long modlicg = (Long) sqlManager.getObject(
              "select MODLICG from GARE where NGARA = ?",
              new Object[] { numeroGara });
          if(modlicg!=null && (modlicg.longValue()==13 || modlicg.longValue()==14))
            result = "true";
        }else{
          Long conteggioLotti = (Long) sqlManager.getObject(
              "select count(NGARA) from GARE where CODGAR1= ? and NGARA!=CODGAR1 and (MODLICG=13 or MODLICG=14)",
              new Object[] { numeroGara });
          if(conteggioLotti!=null && conteggioLotti.longValue()>0)
            result = "true";
        }
      } catch (SQLException s) {
        throw new JspException("Errore durante la lettura della fase di gara "
            + "(GARE.FASGAR)", s);
      }

    }
    return result;
  }
}