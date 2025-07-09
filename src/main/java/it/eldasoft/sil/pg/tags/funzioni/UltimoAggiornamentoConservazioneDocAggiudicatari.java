/*
 * Created on 22/03/23
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
 * Funzione per ricercare l'ultima richiesta di conservazione dei documenti dell'aggiudicatario della gara
 *
 * @author Manuel Bridda
 */
public class UltimoAggiornamentoConservazioneDocAggiudicatari extends AbstractFunzioneTag {

  public UltimoAggiornamentoConservazioneDocAggiudicatari() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String codgar = (String) params[1];
    String dataoracreaString = sqlManager.getDBFunction("DATETIMETOSTRING",
        new String[] { "data_creazione" });
    String selectGardoc="select da_processare,"+dataoracreaString +" from gardoc_jobs where codgara = ? and tipo_archiviazione= 4 order by id_archiviazione desc";
    try {
      Vector<?> datiGardocInCorso = sqlManager.getVector(selectGardoc, new Object[] { codgar });
      if (datiGardocInCorso != null && datiGardocInCorso.size()>0){
        String da_processare = SqlManager.getValueFromVectorParam(datiGardocInCorso, 0).stringValue();
        if("1".equals(da_processare))
          pageContext.setAttribute("richiestaInCorso", true, PageContext.REQUEST_SCOPE);  
        else {
          Vector<?> datiGardoc = sqlManager.getVector(selectGardoc, new Object[] { codgar });
          if (datiGardoc != null && datiGardoc.size()>0){
            String dataCreazione = SqlManager.getValueFromVectorParam(datiGardoc, 1).stringValue();
            Date datacreaDate = UtilityDate.convertiData(dataCreazione, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
            dataCreazione = UtilityDate.convertiData(datacreaDate, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
            pageContext.setAttribute("ultimaConservazione", dataCreazione, PageContext.REQUEST_SCOPE);
          }
        }   
      }
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre i dati da gardoc_jobs per la gara " + codgar, e);
    } catch (GestoreException e) {
      throw new JspException("Errore nell'estrarre i dati da gardoc_jobs per la gara " + codgar, e);
    }

    return null;
  }

}