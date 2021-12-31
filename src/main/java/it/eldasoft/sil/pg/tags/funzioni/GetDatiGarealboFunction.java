/*
 * Created on 28-09-2017
 *
 /*
 * Created on 17-apr-2009
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
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoMoney;
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che estrae i dati di GAREALBO a partire da NGARA.
 *
 * @author Marcello caminiti
 */
public class GetDatiGarealboFunction extends AbstractFunzioneTag {

  public GetDatiGarealboFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String codice = (String) GeneralTagsFunction.cast("string", params[1]);

    try {
      Vector datiGarealbo = sqlManager.getVector("select ctrlaggiu,ctrlimpga,ctrlimp,ctrlgg from garealbo where ngara=? and codgar= ?",
          new Object[] { codice, "$" + codice });
      if(datiGarealbo!=null && datiGarealbo.size()>0){
        Long ctrlaggiu = SqlManager.getValueFromVectorParam(datiGarealbo, 0).longValue();
        String ctrlimpga = SqlManager.getValueFromVectorParam(datiGarealbo, 1).getStringValue();
        Double ctrlimp = SqlManager.getValueFromVectorParam(datiGarealbo, 2).doubleValue();
        String importo = new GestoreCampoMoney().getValorePerVisualizzazione(ctrlimp == null
            ? ""  : ctrlimp.toString());
        Long periodo = SqlManager.getValueFromVectorParam(datiGarealbo, 3).longValue();
        pageContext.setAttribute("ctrlaggiu", ctrlaggiu, PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("ctrlimpga", ctrlimpga);
        pageContext.setAttribute("ctrlimp", ctrlimp, PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("ctrlimpValorePerVisualizzazione", importo);
        pageContext.setAttribute("ctrlimpValorePeriodo", periodo);
      }


    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura della tabella GAREALBO", e);
    } catch (GestoreException e) {
      throw new JspException(
          "Errore durante la lettura della tabella GAREALBO", e);
    }


    return null;
  }
}
