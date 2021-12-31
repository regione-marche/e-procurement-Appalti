/*
 * Created on 08/08/14
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per inizializzare il primo atto autorizzativo in caso di
 * collegamento con rda
 *
 * @author Cristian Febas
 */
public class InizializzaAttoAutorizzativoFunction extends AbstractFunzioneTag {

  public InizializzaAttoAutorizzativoFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String codgar = (String) pageContext.getAttribute("codgar");
    String ngara = (String) pageContext.getAttribute("ngara");
    String numeroRda = pageContext.getRequest().getParameter("numeroRda");

    String selectRda="select data_approvazione,descrizione,valore,tus from v_smat_rda where numero_rda = ?";
    Vector datiRda;
    try {
      datiRda = sqlManager.getVector(selectRda, new Object[] { numeroRda });
      if (datiRda != null && datiRda.size()>0){
        Date dataApprRda = (Date) ((JdbcParametro)datiRda.get(0)).getValue();
        String oggettoRda = (String) ((JdbcParametro) datiRda.get(1)).getValue();
        Object objImpRda = ((JdbcParametro) datiRda.get(2)).getValue();
        Double importoRda = null;
        if (objImpRda instanceof Long){
          importoRda = ((Long) objImpRda).doubleValue();
        }else{
          if(objImpRda instanceof Double){
            importoRda = (Double) objImpRda;
          }
        }
        Long tusRda = (Long) ((JdbcParametro) datiRda.get(3)).getValue();
    //per inizializzare riuso le variabili già utilizzate nella iniz. da Appr, con i case già gestiti
        pageContext.setAttribute("initNATTOT",numeroRda, PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("initDATTOT",UtilityDate.convertiData(dataApprRda, UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("initTATTOT","4", PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("initDESTOR",oggettoRda, PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("initTUS",tusRda, PageContext.REQUEST_SCOPE);

      }
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre i dati della RdA da collegare alla gara " + ngara, e);
    }

    return null;
  }

}