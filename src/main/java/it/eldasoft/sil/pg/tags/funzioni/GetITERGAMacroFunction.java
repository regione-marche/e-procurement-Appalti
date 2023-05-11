/*
 * Created on 09-02-2013
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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che estrae il campo ITERGA di TORN
 * e ritorna la macro tipologia di procedura:
 * 1=procedura aperta
 * 2=procedura ristretta
 * 3=procedura negoziata
 *
 * Nel request metto il valore di ITERGA
 *
 * @author Marcello Caminiti
 */
public class GetITERGAMacroFunction extends AbstractFunzioneTag {

  public GetITERGAMacroFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String result="";
    String chiave = (String) GeneralTagsFunction.cast("string", params[1]);

    if (chiave != null && chiave.length()>0){
      String codice = chiave.substring(chiave.indexOf(":") + 1);
      if (codice != null && codice.length()>0){
        String select="";
        if(chiave.indexOf("NGARA")>0)
          select="select iterga from gare,torn where ngara=? and codgar1=codgar";
        else
          select="select iterga from torn where codgar = ?";
        try {


        Long iterga = (Long) sqlManager.getObject(select, new Object[] { codice });
      	if(iterga != null) {
            switch(iterga.intValue()){
            case 1:
              result = "1";
              break;
            case 3:
            case 5:
            case 6:
            case 8:
                result = "3";
                break;
            case 7:
              result = "7";
              break;
            default:
              result = "2";
              break;
            }
            pageContext.setAttribute("iterga", iterga);
      	  }
        } catch (SQLException e) {
          throw new JspException(
              "Errore durante la lettura del tipo della gara ", e);
        }
      }
    }
    return result;
  }
}
