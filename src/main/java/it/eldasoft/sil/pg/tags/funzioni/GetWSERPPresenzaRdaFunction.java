/*
 * Created on 15/t/09
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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * La funzione verifica se i lotti della gara hanno il carrello associato
 * ed eventualmente visualizza il menu per comunicare l'esito di gara
 *
 * @author Cristian Febas
 */
public class GetWSERPPresenzaRdaFunction extends AbstractFunzioneTag {

  public GetWSERPPresenzaRdaFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String codiceGara = (String) params[1];
    String ngara = (String) params[2];
    String tipoWSERP = (String) params[3];
    String presenzaRda = "false";
    Long countRdaGcap = new Long(0);
    Long countRdaGarerda = new Long(0);
    try {
      if("SMEUP".equals(tipoWSERP) || "UGOVPA".equals(tipoWSERP)){
        countRdaGcap = (Long) sqlManager.getObject("select count(*) from gcap where ngara = ? " +
            "and (codcarr is not null or codrda is not null) ", new Object[] { ngara });
        countRdaGarerda = (Long) sqlManager.getObject("select count(*) from garerda where codgar = ? " +
            "and (codcarr is not null or numrda is not null) ", new Object[] { codiceGara });

      }else{
        if("AVM".equals(tipoWSERP) || "CAV".equals(tipoWSERP)){
          countRdaGcap = (Long) sqlManager.getObject("select count(*) from gcap where ngara = ? " +
              "and (codrda is not null) ", new Object[] { ngara });
          countRdaGarerda = (Long) sqlManager.getObject("select count(*) from garerda where codgar = ? " +
              "and (numrda is not null) ", new Object[] { codiceGara });
        }
      }
      if(countRdaGarerda > 0){
        presenzaRda = "1";
      }
      if(countRdaGcap > 0){
        presenzaRda = "2";
      }

    }catch (SQLException e) {
        throw new JspException(
                "Errore durante la verifica delle associazioni del carrello alla gara ", e);
    }

    return presenzaRda;
  }

}