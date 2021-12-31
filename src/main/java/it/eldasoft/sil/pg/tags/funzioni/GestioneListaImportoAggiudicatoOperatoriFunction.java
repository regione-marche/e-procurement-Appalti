/*
 * Created on 23 dec 2013
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
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per inizializzare i dati della pagina lista-ImportoAggiudicatoOperatori.jsp
 *
 * @author Marcello Caminiti
 */
public class GestioneListaImportoAggiudicatoOperatoriFunction extends AbstractFunzioneTag {

  public GestioneListaImportoAggiudicatoOperatoriFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ngara = ((String) params[0]);
    try {
      String dbFunctionDateToString = sqlManager.getDBFunction("DATETIMETOSTRING",
          new String[] { "CTRLDATA" });
      Vector datiGarealbo = sqlManager.getVector("select " + dbFunctionDateToString + ", CTRLIMP, CTRLGG from garealbo where ngara=?", new Object[]{ngara});
      if(datiGarealbo!=null && datiGarealbo.size()>0){
        String ctrldata = SqlManager.getValueFromVectorParam(datiGarealbo, 0).getStringValue();
        Double ctrlimp = SqlManager.getValueFromVectorParam(datiGarealbo, 1).doubleValue();
        Long ctrlgg = SqlManager.getValueFromVectorParam(datiGarealbo, 2).longValue();
        if(ctrldata!=null)
          ctrldata = UtilityDate.convertiData(UtilityDate.convertiData(ctrldata, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);

        String ctrlimpString="";
        if(ctrlimp!=null)
          ctrlimpString = UtilityNumeri.convertiImporto(ctrlimp, 2);

        if(ctrlgg!=null){
          Date dataOggi = new Date();
          Calendar dataCalcolata = Calendar.getInstance();
          dataCalcolata.setTime(dataOggi);
          dataCalcolata.add(Calendar.DAY_OF_MONTH, ctrlgg.intValue() * -1 + 1);
          pageContext.setAttribute("dataOggi",  UtilityDate.convertiData(dataOggi, UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("dataCalcolata",  UtilityDate.convertiData(dataCalcolata.getTime(), UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);

        }

        pageContext.setAttribute("ctrldata", ctrldata, PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("ctrlimp", ctrlimpString, PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("ctrlgg", ctrlgg, PageContext.REQUEST_SCOPE);
      }
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura della tabella GAREALBO", e);
    } catch (GestoreException e) {
      throw new JspException("Errore durante la lettura della tabella GAREALBO", e);
    }

    return null;
  }

}
