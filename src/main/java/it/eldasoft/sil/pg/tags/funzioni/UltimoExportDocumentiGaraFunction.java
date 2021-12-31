/*
 * Created on 26/02/16
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
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per ricercare l'ultima richiesta di export dei documenti della gara
 *
 * @author Mirco Franzoni
 */
public class UltimoExportDocumentiGaraFunction extends AbstractFunzioneTag {

  public UltimoExportDocumentiGaraFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String codgar = (String) params[1];
    String dataorainsString = sqlManager.getDBFunction("DATETIMETOSTRING",
        new String[] { "data_inserimento" });
    String selectGardoc="select syscon, " + dataorainsString + " from gardoc_jobs where codgara = ? and tipo_archiviazione= 1 order by id_archiviazione desc";
    Vector<?> datiGardoc;
    try {
    	datiGardoc = sqlManager.getVector(selectGardoc, new Object[] { codgar });
    	if (datiGardoc != null && datiGardoc.size()>0){
    		Long syscon = SqlManager.getValueFromVectorParam(datiGardoc, 0).longValue();
    		String utente = (String)sqlManager.getObject("select sysute from usrsys where syscon = ? ", new Object[] { syscon });
    		String dataInserimento = SqlManager.getValueFromVectorParam(datiGardoc, 1).stringValue();
            Date datainsDate = UtilityDate.convertiData(dataInserimento, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
            dataInserimento = UtilityDate.convertiData(datainsDate, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
    		pageContext.setAttribute("ultimoExport", utente + " il " + dataInserimento, PageContext.REQUEST_SCOPE);
    	}
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre i dati da gardoc_jobs per la gara " + codgar, e);
    } catch (GestoreException e) {
      throw new JspException("Errore nell'estrarre i dati da gardoc_jobs per la gara " + codgar, e);
    }

    return null;
  }

}