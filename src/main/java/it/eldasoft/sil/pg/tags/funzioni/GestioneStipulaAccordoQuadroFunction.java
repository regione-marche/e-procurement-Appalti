/*
 * Created on 31/05/13
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
 * Funzione per inizializzare la pagina gare-pg-stipula-accordo-quadro.jsp
 *
 * @author Marcello Caminiti
 */
public class GestioneStipulaAccordoQuadroFunction extends AbstractFunzioneTag {

  public GestioneStipulaAccordoQuadroFunction() {
    super(2, new Class[] { PageContext.class,String.class,String.class,String.class,String.class });
  }

  @SuppressWarnings("unchecked")
  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String ngara = (String)params[1];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);


    Long aqoper = null;
    try {
      aqoper = (Long)sqlManager.getObject("select aqoper from gare1 where ngara=?", new Object[]{ngara});
      pageContext.setAttribute("aqoper", aqoper, PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException(
          "Errore nella lettura del campo TORN.AQOPER", e);
    }

    if(new Long(2).equals(aqoper)){

      try {
        List listaDitteAggiudicatarie = sqlManager.getListVector("select aq.id, aq.ngara, aq.dittao, i.nomest, aq.iaggiu, d.ricsub, aq.ridiso, "
            + "aq.impgar, aq.nquiet, aq.dquiet, aq.istcre, aq.indist, d.ribauo, d.impoff, aq.banapp, aq.coorba, aq.codbic from ditgaq aq, impr i, ditg d "
            + "where aq.ngara=? and aq.dittao = i.codimp and aq.ngara=d.ngara5 and aq.dittao=d.dittao order by aq.numord", new Object[]{ngara});
        pageContext.setAttribute("listaDitteAggiudicatarie", listaDitteAggiudicatarie,
            PageContext.REQUEST_SCOPE);
        long numeroDitteAggiudicatarie=0;
        if(listaDitteAggiudicatarie!=null & listaDitteAggiudicatarie.size()>0){
          numeroDitteAggiudicatarie=listaDitteAggiudicatarie.size();
        }
        pageContext.setAttribute("numeroDitteAggiudicatarie", new Long(numeroDitteAggiudicatarie),
            PageContext.REQUEST_SCOPE);
      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre i dati di DITGAQ "
            + "della gara "
            + ngara, e);
      }

    }

    return null;
  }

}
