/*
 * Created on 01/02/2023
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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per il conteggio del numero dei documenti da firmare.
 *
 * @author Alex Mancini
 */
public class GetNumDocumentiDaFirmareFunction extends AbstractFunzioneTag {

  public GetNumDocumentiDaFirmareFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    String idstipula = (String) params[1];

    Long conteggioDigFirma = Long.valueOf(0);
    Long conteggioFormato = Long.valueOf(0);

    String selectDigFirma = "select count(vdoc.digfirma) "
        + "from V_GARE_DOCSTIPULA vdoc "
        + "where vdoc.idprg = 'PG' "
        + "and vdoc.iddocdig is not null "
        + "and vdoc.idstipula = ? "
        + "and vdoc.digFirma = '1' ";
    
    String selectFormato = "select count(formato) "
        + "from V_GARE_DOCSTIPULA "
        + "where FORMATO = 1 "
        + "and (visibilita is null or (visibilita is not null and visibilita <> 3)) "
        + "and iddocdig is null "
        + "and idstipula = ? ";

    try {
      conteggioDigFirma = (Long) sqlManager.getObject(selectDigFirma, new Object[]{idstipula});
      conteggioFormato = (Long) sqlManager.getObject(selectFormato, new Object[]{idstipula});
      pageContext.getRequest().setAttribute("conteggioFormato", conteggioFormato.toString());
      
    } catch (SQLException e) {
      throw new JspException("Errore nel conteggio dei documenti da firmare ", e);
    }

    return conteggioDigFirma.toString();
  }

}
