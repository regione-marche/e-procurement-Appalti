/*
 * Created on 09/09/21
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
 * Funzione che controlla se la gara risulta collegata ad una richiesta cig
 *
 * @author Cristian Febas
 */
public class EsisteRichiestaCigGaraFunction extends AbstractFunzioneTag {

  public EsisteRichiestaCigGaraFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
        "sqlManager", pageContext, SqlManager.class);

    String result = "NO";

    String contestoSimog = (String)params[1];
    String codice = (String)params[2];

    try {

      Long numOccorrenze = null;
      if("GARA".equals(contestoSimog)) {
          String select="select count(*) from w3gara where codgar=? ";
          numOccorrenze = (Long) sqlManager.getObject(select, new Object[]{codice});
          if(numOccorrenze!=null && numOccorrenze.longValue()>=1){
            result = "SI";
          }else{
            select="select count(*) from w3smartcig where codgar=?";
            numOccorrenze = (Long) sqlManager.getObject(select, new Object[]{codice});
            if(numOccorrenze!=null && numOccorrenze.longValue()>=1){
              result = "SI";
            }
          }
    	  
      }else {
    	  if("LOTTO".equals(contestoSimog)) {
              String select="select count(*) from w3lott where ngara=? ";
              numOccorrenze = (Long) sqlManager.getObject(select, new Object[]{codice});
              if(numOccorrenze!=null && numOccorrenze.longValue()>=1){
            	  result = "SI";
              }
    		  
    	  }
      }

    } catch (SQLException e) {
        throw new JspException(
            "Errore durante la verifica che la gara sia  collegata ad una richiesta cig ",e);

    }


    return result;
  }

}
