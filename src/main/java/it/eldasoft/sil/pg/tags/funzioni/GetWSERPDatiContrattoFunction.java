/*
 * Created on 22/Agosto/2019
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
import java.util.Date;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * La funzione verifica se sono presenti i dati di fine contratto
 * e se non lo sono permette di nasconfere la fx di invio dati aggiudicazione ad ERP
 *
 * @author Cristian Febas
 */
public class GetWSERPDatiContrattoFunction extends AbstractFunzioneTag {

  public GetWSERPDatiContrattoFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ngara = (String) params[1];
    String isDatiContratto = "false";

    try {
      Vector<?> datiContratto = sqlManager.getVector(
          "select ga.codcig, gc.ngara, gc.ncont, gc.dverbc, gc.dcertu" +
          " from gare ga, garecont gc" +
          " where gc.codimp = ga.ditta" +
          " and ((gc.ngara=ga.ngara and gc.ncont=1) or (gc.ngara=ga.codgar1 and (gc.ngaral is null or gc.ngaral=ga.ngara)))" +
          " and ga.ditta is not null and ga.ngara = ?",new Object[] {ngara});

      if(datiContratto!=null && datiContratto.size()>0){
        Date dataInizioContratto = (Date) SqlManager.getValueFromVectorParam(datiContratto,3).getValue();
        Date dataFineContratto = (Date) SqlManager.getValueFromVectorParam(datiContratto,4).getValue();
        if(dataInizioContratto != null && dataFineContratto != null ){
          isDatiContratto = "true";
        }
      }

    }catch (SQLException e) {
        throw new JspException(
                "Errore durante la verifica dei dati del contratto ", e);
    }

    return isDatiContratto;
  }

}