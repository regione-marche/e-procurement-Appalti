/*
 * Created on 30-giu-2023
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

import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che estrae la lista degli elenchi per creare le opzioni di un menu a tendina
 *
 * @author Manuel Bridda
 */
public class GetFiltriDestinatariComunicazioniGenericheFunction extends AbstractFunzioneTag {

  public GetFiltriDestinatariComunicazioniGenericheFunction() {
    super(2, new Class[] { PageContext.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    Logger logger = Logger.getLogger(GetFiltriDestinatariComunicazioniGenericheFunction.class);
    
    try {
      List listaNgaraElenchi = sqlManager.getListVector("select garealbo.ngara from garealbo join gare on garealbo.ngara=gare.ngara where gare.genere=10 order by garealbo.ngara",null);
      
      List listaAbilitazioni = sqlManager.getListVector("select tab1tip, tab1desc from tab1 where tab1cod='A1075' order by tab1tip",null);

      pageContext.setAttribute("listaElenchi", listaNgaraElenchi,
          PageContext.REQUEST_SCOPE);
      
      pageContext.setAttribute("listaAbilitazioni", listaAbilitazioni,
          PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
      UtilityStruts.addMessage(this.getRequest(), "error",
          "errors.gestoreException.*.erroreLetturaDB", null);
      logger.error("Errore nel prelievo dei dati per il popolamento dei filtri della comunicazione generica", e);
    }

    return null;
  }

}