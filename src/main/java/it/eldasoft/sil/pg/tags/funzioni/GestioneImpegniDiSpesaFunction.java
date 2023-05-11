/*
 * Created on 19-04-2016
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

/**
 * Funzione per inizializzare le sezioni degli impegni di spesa di una gara a
 * lotto unico o di un lotto di gara (Personalizzazione Autovie)
 *
 * @author Cristian.Febas
 */
public class GestioneImpegniDiSpesaFunction extends AbstractFunzioneTag {

  public GestioneImpegniDiSpesaFunction() {
    super(3, new Class[] { PageContext.class,String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String entita = (String) params[1];
    String chiave = (String) params[2];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String select="";

    Object[] param = new Object[1];
    if("GAREIDS".equals(entita)){
      select= "select CODGAR, NUMIDS, DATEMISS, NPROT, DATRICEZ, IMPIDS, NOTEIDS, PROGIDS "
        + "from GAREIDS "
        + "where GAREIDS.CODGAR = ? "
        + "order by GAREIDS.NUMIDS asc";
      param[0] = chiave;
    }

    try {

      List listaImpegniDiSpesa = sqlManager.getListVector( select, param);
      if (listaImpegniDiSpesa != null && listaImpegniDiSpesa.size() > 0)
        pageContext.setAttribute("impegniDiSpesa", listaImpegniDiSpesa, PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre gli impegniDiSpesa "
          + "della tabella " + entita + " con chiave " + chiave, e);
    }

    return null;
  }

}
