/*
 * Created on 3 dic 2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.spring.UtilitySpring;


public class GetNumeroFascicoliDaCreare extends AbstractFunzioneTag {


  public GetNumeroFascicoliDaCreare() {
    super(2, new Class[]{PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {


    GestioneWSDMManager gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        pageContext, GestioneWSDMManager.class);

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String idconfi = (String) params[1];


    String esito="0";

    try {
      String elencoUffintFiltro="";
      String elencoUffint="";
      List<?> listaUff = sqlManager.getListVector("select codein from WSDMCONFIUFF where idconfi = ?", new Object[] {new Long(idconfi)});
      if(listaUff!=null && listaUff.size()>0) {
       for(int i=0; i<listaUff.size(); i++ ) {
         if(i > 0) {
           elencoUffint +=", ";
           elencoUffintFiltro +=",";
         }
         elencoUffintFiltro += "'" + SqlManager.getValueFromVectorParam(listaUff.get(i), 0).getStringValue() + "'";
         elencoUffint += SqlManager.getValueFromVectorParam(listaUff.get(i), 0).getStringValue();
       }
       if(elencoUffintFiltro.length()>1)
         elencoUffintFiltro = "(" + elencoUffintFiltro + ")";
      }

      Long conteggioGare = gestioneWSDMManager.getConteggioFascicoliMancanti("GARE",elencoUffintFiltro);
      Long conteggioAvvisi = gestioneWSDMManager.getConteggioFascicoliMancanti("AVVISI",elencoUffintFiltro);
      Long conteggioElenchi = gestioneWSDMManager.getConteggioFascicoliMancanti("ELENCHI",elencoUffintFiltro);
      Long conteggioCataloghi = gestioneWSDMManager.getConteggioFascicoliMancanti("CATALOGHI",elencoUffintFiltro);
      Long conteggioRilanci = gestioneWSDMManager.getConteggioFascicoliMancanti("RILANCI",elencoUffintFiltro);

      long conteggioTot=0;
      if((conteggioGare!=null && conteggioGare.longValue()>0) || (conteggioAvvisi!=null && conteggioAvvisi.longValue()>0)
          || (conteggioElenchi!=null && conteggioElenchi.longValue()>0) || (conteggioCataloghi!=null && conteggioCataloghi.longValue()>0)
          || (conteggioRilanci!=null && conteggioRilanci.longValue()>0)) {
        esito="1";
        if(conteggioGare!=null)
          conteggioTot+=conteggioGare.longValue();
        if(conteggioAvvisi!=null)
          conteggioTot+=conteggioAvvisi.longValue();
        if(conteggioElenchi!=null)
          conteggioTot+=conteggioElenchi.longValue();
        if(conteggioCataloghi!=null)
          conteggioTot+=conteggioCataloghi.longValue();
        if(conteggioRilanci!=null)
          conteggioTot+=conteggioRilanci.longValue();
      }
      pageContext.getRequest().setAttribute("conteggioGare", conteggioGare);
      pageContext.getRequest().setAttribute("conteggioAvvisi", conteggioAvvisi);
      pageContext.getRequest().setAttribute("conteggioElenchi", conteggioElenchi);
      pageContext.getRequest().setAttribute("conteggioCataloghi", conteggioCataloghi);
      pageContext.getRequest().setAttribute("conteggioRilanci", conteggioRilanci);
      pageContext.getRequest().setAttribute("conteggioTot", new Long(conteggioTot));

      if(elencoUffint=="")
        elencoUffint = null;
      pageContext.getRequest().setAttribute("elencoUffint", elencoUffint);

      if(elencoUffintFiltro=="")
        elencoUffintFiltro = null;
      pageContext.getRequest().setAttribute("elencoUffintFiltro", elencoUffintFiltro);

    } catch (SQLException e) {
      throw new JspException(
          "Errore durante il conteggio delle gare senza fascicolo ", e);
    }
    return esito;
  }


}
