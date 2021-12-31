/*
 * Created on 08/ott/2020
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;


public class IsValutazioneCommissioneFunction extends AbstractFunzioneTag{


  public IsValutazioneCommissioneFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    ProfiloUtente profilo = (ProfiloUtente) pageContext.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    
    Long syscon = new Long(profilo.getId());
    
    String result = "false";
    String ngara = (String) params[1];
    String codiceTecnicoUtente = null;
    String nomeTecnicoUtente = null;
    
    String selectCountGfof = "select count(*) from gfof where ngara2 = ? and espgiu = '1'";
    String selectCodiceTecnicoUtente = "select tecni.codtec, tecni.nomtec from tecni, usrsys, gfof where gfof.ngara2 = ? and usrsys.syscon = ? and usrsys.syscf = tecni.cftec and gfof.codfof = tecni.codtec and (usrsys.sysdisab is null or usrsys.sysdisab != '1')";

    try {
      Long count = (Long) sqlManager.getObject(selectCountGfof, new Object[] { ngara });
      if (count != null && count.intValue()>0) {
        Vector datiTecni = sqlManager.getVector(selectCodiceTecnicoUtente, new Object[] { ngara, syscon });
        if(datiTecni != null && datiTecni.size()>0){
          codiceTecnicoUtente = SqlManager.getValueFromVectorParam(datiTecni, 0).stringValue();
          nomeTecnicoUtente = SqlManager.getValueFromVectorParam(datiTecni, 1).stringValue();
        }
        this.getRequest().setAttribute("nomeTecnicoUtente", nomeTecnicoUtente);
        this.getRequest().setAttribute("codiceTecnicoUtente", codiceTecnicoUtente);
        result = "true";
      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura dei dati sulla commissione di gara", e);
    } catch (GestoreException e) {
      throw new JspException("Errore durante la lettura dei dati sulla commissione di gara", e);
    }

    return result;
  }
  
}
