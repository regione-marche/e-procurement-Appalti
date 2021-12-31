/*
 * Created on 01/10/15
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
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Viene restistuito l'elenco dei lotti di una gara aggiudicati per la ditta
 *
 * @author Marcello Caminiti
 */
public class GetFiltroElencoLottiAggiudicatiDittaFunction extends AbstractFunzioneTag {

  public GetFiltroElencoLottiAggiudicatiDittaFunction(){
    super(3, new Class[]{PageContext.class,String.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        pageContext, PgManagerEst1.class);

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String codgar = (String)params[1];
    String ditta = (String)params[2];
    String ret = null;
    try {
      ret =pgManagerEst1 .getElencoLottiAggiudicati(codgar, ditta);

      //Vengono letti i codici cig dei lotti, scartando i cig fittizzi
      String elencoCodcigLotti="";
      String select="select codcig from gare where codgar1=? and ditta=? and genere is null";
      List listaCigLotti = sqlManager.getListVector(select, new Object[]{codgar,ditta});
      if(listaCigLotti!=null && listaCigLotti.size()>0){
        String codcig=null;
        for(int i=0;i<listaCigLotti.size();i++){
          codcig = SqlManager.getValueFromVectorParam(listaCigLotti.get(i), 0).getStringValue() ;
          if(codcig != null && !"".equals(codcig) && !codcig.startsWith("#")){
            if(elencoCodcigLotti.length() > 1)
              elencoCodcigLotti+=",";
            elencoCodcigLotti+="'" + codcig +"'";
          }
        }

      }
      if("".equals(elencoCodcigLotti))
        elencoCodcigLotti=null;

      pageContext.setAttribute("elencoCig", elencoCodcigLotti, PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura ricerca dei lotti aggiudicati per la gara " + codgar + " per la ditta" + ditta, e);
    }

    return ret;
  }

}