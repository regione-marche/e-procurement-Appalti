/*
 * Created on 04/03/13
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
 * Funzione che controlla se la gara è pubblicata nel portale
 * Inoltre restituisce la variabile tipoPubblicazione con i seguenti valori:
 * 1 per pubbli con tippub = 11 o 13
 * 1.1 per pubbli con tippub = 23
 * 2 per pubg con tippub = 12
 *
 * @author Marcello Caminiti
 */
public class GaraPubblicataSuPortaleFunction extends AbstractFunzioneTag {

  public GaraPubblicataSuPortaleFunction() {
    super(2, new Class[] { PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
        "sqlManager", pageContext, SqlManager.class);

    String result = "NO";

    String codgar = (String)params[1];

    try {

      Long numOccorrenze = null;
      String select="select count(*) from pubg where ngara=? and tippubg=?";
      select="select count(*) from pubg,gare where gare.codgar1=? and pubg.ngara=gare.ngara and tippubg=?";
      numOccorrenze = (Long) sqlManager.getObject(select, new Object[]{codgar,new Long(12)});

      if(numOccorrenze!=null && numOccorrenze.longValue()>=1){
        result = "SI";
        pageContext.setAttribute("tipoPubblicazione", "2",PageContext.REQUEST_SCOPE);
      }else{
        select="select count(*) from pubbli where codgar9=? and tippub=? ";
        numOccorrenze = (Long) sqlManager.getObject(select, new Object[]{codgar,new Long(23)});

        if(numOccorrenze!=null && numOccorrenze.longValue()>=1){
          result = "SI";
          pageContext.setAttribute("tipoPubblicazione", "1.1",PageContext.REQUEST_SCOPE);
        }else{
          select="select count(*) from pubbli where codgar9=? and (tippub=? or tippub=?)";
          numOccorrenze = (Long) sqlManager.getObject(select, new Object[]{codgar,new Long(11),new Long(13)});
          if(numOccorrenze!=null && numOccorrenze.longValue()>=1){
            result = "SI";
            pageContext.setAttribute("tipoPubblicazione", "1",PageContext.REQUEST_SCOPE);
          }
        }
      }

    } catch (SQLException e) {
        throw new JspException(
            "Errore durante la verifica che la gara sia  pubblicata su portale ",e);

    }


    return result;
  }

}
