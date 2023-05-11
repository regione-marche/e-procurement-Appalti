/*
 * Created on 04/06/18
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
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per inizializzare le sezioni delle richieste di acquisto
 *
 */
public class GestioneRichiesteAcquistoFunction extends AbstractFunzioneTag {

  public GestioneRichiesteAcquistoFunction() {
    super(3, new Class[] { PageContext.class,String.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
      String codgar = (String) params[1];
      codgar = UtilityStringhe.convertiNullInStringaVuota(codgar);
      String ngara = (String) params[2];
      ngara = UtilityStringhe.convertiNullInStringaVuota(ngara);


    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
    pageContext, SqlManager.class);
    String select = "";
    List listaRichiesteAcquisto = null;

    try {

      if(!"".equals(ngara)){
        select="select ID, CODGAR, DATCRE, DATRIL, NUMRDA, POSRDA, DATACONS, LUOGOCONS," +
        " CODVOC, VOCE, UNIMIS, CODCAT, PERCIVA, CODCARR, QUANTI, PREZUN, ESERCIZIO, NGARA, STRUTTURA" +
        " from GARERDA where CODGAR=? and NGARA =? order by id";
        listaRichiesteAcquisto = sqlManager.getListVector(
            select, new Object[]{codgar,ngara});
      }else{
        select="select ID, CODGAR, DATCRE, DATRIL, NUMRDA, POSRDA, DATACONS, LUOGOCONS," +
        " CODVOC, VOCE, UNIMIS, CODCAT, PERCIVA, CODCARR, QUANTI, PREZUN, ESERCIZIO, NGARA, STRUTTURA" +
        " from GARERDA where CODGAR=? order by id";
        listaRichiesteAcquisto = sqlManager.getListVector(
            select, new Object[]{codgar});
      }

      if (listaRichiesteAcquisto != null && listaRichiesteAcquisto.size() > 0)
        pageContext.setAttribute("listaRichiesteAcquisto", listaRichiesteAcquisto,
            PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre le richieste di acquisto "
          + "della tabella GARERDA con chiave "
          + codgar, e);
    }

    return null;
  }

}
