/*
 * Created on 02/02/2023
 *
 * Copyright (c) Maggioli S.p.A. - Divisione EldaSoft
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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per la raccolta dei documenti da firmare.
 *
 * @author Alex Mancini
 */
public class GetNomiDocumentiDaFirmareFunction extends AbstractFunzioneTag {

  public GetNomiDocumentiDaFirmareFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    String idstipula = (String) params[1];

    String select = "select dignomdoc "
        + "from V_GARE_DOCSTIPULA vdoc "
        + "where vdoc.idprg = 'PG' "
        + "and vdoc.idstipula = ? "
        + "and vdoc.digFirma = '1' ";
    List nomiDoc = new ArrayList();

    String nomiConcatenati = "Accesso alla firma digitale dei file:\n";
    try {
      nomiDoc = sqlManager.getListVector(select, new Object[]{idstipula});
      if (nomiDoc != null && !nomiDoc.isEmpty()) {
        for (int i = 0; i < nomiDoc.size(); i++) {
          nomiConcatenati += SqlManager.getValueFromVectorParam(nomiDoc.get(i), 0).getStringValue()+"\n";
        }
      }
    } catch (SQLException e) {
      throw new JspException("Errore nel conteggio dei documenti da firmare ", e);
    }

    return nomiConcatenati;
  }

}
