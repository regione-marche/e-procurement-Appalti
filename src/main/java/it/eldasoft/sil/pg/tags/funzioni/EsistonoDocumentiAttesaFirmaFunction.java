/*
 * Created on 11-12-2013
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
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per il controllo dell'esistenza di documenti in attesa di firma
 *
 * @author Marcello Caminiti
 */
public class EsistonoDocumentiAttesaFirmaFunction extends AbstractFunzioneTag {

  public EsistonoDocumentiAttesaFirmaFunction() {
    super(3, new Class[] {PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String codgar = (String) params[1];
    String gruppoString = (String) params[2];
    Long gruppo = new Long(gruppoString);
    String ret = "false";

    String select="select count(codgar) from documgara, w_docdig where codgar= ? and gruppo = ? "
        + "and documgara.idprg=w_docdig.idprg and documgara.iddocdg=w_docdig.iddocdig and digfirma ='1'";

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    try {
      String richiestaFirma = ConfigManager.getValore(CostantiAppalti.PROP_RICHIESTA_FIRMA);
      if("1".equals(richiestaFirma)){
        Long conteggio = (Long) sqlManager.getObject(select, new Object[] {codgar, gruppo });
        if(conteggio!=null && conteggio.longValue()>0)
          ret="true";
      }
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura di mail e pec)", e);
    }

    return ret;
  }

}
