/*
 * Created on 13/03/2020
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
 * Funzione per inizializzare le sezioni degli ordinanti
 *
 * @author Cristian.Febas
 */
public class GestioneOrdinantiNsoFunction extends AbstractFunzioneTag {

  public GestioneOrdinantiNsoFunction() {
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
    if("NSO_ORDINANTI".equals(entita)){
      select= "select ID,NSO_ORDINI_ID,TIPO,CODEIN,NOMEIN,ENDPOINT,VIA,CITTA,CAP,CODNAZ,PIVA"
        + " from NSO_ORDINANTI "
        + " where NSO_ORDINI_ID = ? "
        + " order by ID,TIPO asc";
      param[0] = chiave;

      try {
        List listaOrdinanti = sqlManager.getListVector(
            select, param);

        if (listaOrdinanti != null && listaOrdinanti.size() > 0)
          pageContext.setAttribute("ordinanti", listaOrdinanti,
              PageContext.REQUEST_SCOPE);
      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre gli ordinanti "
            + "della tabella " + entita + " con chiave "
            + chiave, e);
      }
    }

    if("NSO_PUNTICONS".equals(entita)){
      select= "select ID,NSO_ORDINI_ID,COD_PUNTO_CONS,INDIRIZZO,LOCALITA,CAP,CITTA,CODNAZ," +
      		"ALTRE_INDIC,ALTRO_PUNTO_CONS,CONS_DOMICILIO"
        + " from NSO_PUNTICONS "
        + " where NSO_ORDINI_ID = ? "
        + " order by ID asc";
      param[0] = chiave;

      try {
        List listaPuntiConsegna = sqlManager.getListVector(select, param);

        if (listaPuntiConsegna != null && listaPuntiConsegna.size() > 0)
          pageContext.setAttribute("punticons", listaPuntiConsegna,
              PageContext.REQUEST_SCOPE);
      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre i punti di consegna "
            + "della tabella " + entita + " con chiave "
            + chiave, e);
      }

    }

    if("NSO_ALLEGATI".equals(entita)){
      select= "select a.ID,a.NSO_ORDINI_ID,a.NPROGR,a.TIPODOC,a.DESCRIZIONE," +
      		" a.IDPRG,a.IDDOCDIG,w.DIGNOMDOC,a.DATARILASCIO,a.DATASCADENZA" +
      		" from NSO_ALLEGATI a left join W_DOCDIG w" +
      		" on a.IDPRG = w.IDPRG  and a.IDDOCDIG = w.IDDOCDIG" +
      		" where a.NSO_ORDINI_ID = ?" +
      		" order by ID asc";
      param[0] = chiave;

      try {
        List listaAllegati = sqlManager.getListVector(select, param);

        if (listaAllegati != null && listaAllegati.size() > 0)
          pageContext.setAttribute("allegati", listaAllegati,
              PageContext.REQUEST_SCOPE);
      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre gli allegati "
            + "della tabella " + entita + " con chiave "
            + chiave, e);
      }

    }


    return null;
  }

}
