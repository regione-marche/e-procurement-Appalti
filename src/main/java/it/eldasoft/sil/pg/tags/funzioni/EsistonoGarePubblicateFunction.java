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
 * Funzione che controlla se esistono gare pubblicate nel portale
 *
 * @author Marcello Caminiti
 */
public class EsistonoGarePubblicateFunction extends AbstractFunzioneTag {

  public EsistonoGarePubblicateFunction() {
    super(2, new Class[] { PageContext.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
        "sqlManager", pageContext, SqlManager.class);

    String result = "NO";
    String entita=(String)params[1];

    try {
      String ent="V_GARE_TORN";
      if(!"V_GARE_TORN".equals(entita))
        ent = entita;
      /*
      String select="select count(*) from pubbli,v_gare_torn where (tippub=11 or tippub=13) and codgar9=codgar";
      if("V_GARE_ELEDITTE".equals(entita))
        select="select count(*) from pubbli,v_gare_eleditte where (tippub=11 or tippub=13) and codgar9=codgar";
      if("GAREAVVISI".equals(entita))
        select="select count(*) from pubbli,gareavvisi where (tippub=11 or tippub=13) and codgar9=codgar";
      */
      String select="select count(*) from pubbli," + ent + " where (tippub=11 or tippub=13 or tippub=23) and codgar9=codgar";
      Long numOccorrenze = (Long) sqlManager.getObject(select, null);
      if(numOccorrenze!=null && numOccorrenze.longValue()>0)
        result = "SI";
      else{
        if("V_GARE_TORN".equals(entita)){
          select="select count(*) from pubg where tippubg=12";
          numOccorrenze = (Long) sqlManager.getObject(select, null);
          if(numOccorrenze!=null && numOccorrenze.longValue()>0)
            result = "SI";
        }


      }

    } catch (SQLException e) {
        throw new JspException(
            "Errore durante il conteggio delle gare pubblicate su portale ",e);

    }


    return result;
  }

}
