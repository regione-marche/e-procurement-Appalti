/*
 * Created on 18-03-2016
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
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Vengono estratti i valori di GARE1.METSOGLIA e GARE1.METCOEFF
 *
 *
 * @author Marcello Caminiti
 */
public class GetMetodoCalcoloSogliaFunction extends AbstractFunzioneTag {

  public GetMetodoCalcoloSogliaFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String select = null;
    String ngara = (String)params[1];
    String isGaraOffertaUnica= (String)params[2];
    boolean valoriTuttiUguali=true;
    if (ngara != null && ngara.length() > 0) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);
      select="select metsoglia, metcoeff from gare1 where ngara=?";
      if("true".equals(isGaraOffertaUnica)){
        select="select g1.metsoglia, g1.metcoeff from gare1 g1, gare g where g.codgar1=? and g1.ngara=g.ngara and (g.modlicg=13 or g.modlicg=14)";
      }
      try {
        List<?> lista = sqlManager.getListVector(select, new Object[]{ngara});
        if(lista!=null && lista.size()>0){
          Long metsoglia = null;
          Long tmp = null;
          Double metcoeff = null;
          for(int i=0;i<lista.size();i++){
            tmp = SqlManager.getValueFromVectorParam(lista.get(i), 0).longValue();
            if(i==0 && tmp!=null){
              metsoglia = new Long(tmp.longValue());
              metcoeff = SqlManager.getValueFromVectorParam(lista.get(i), 1).doubleValue();
            }
            if(metsoglia!=null && tmp!=null && metsoglia.longValue()!=tmp.longValue() && "true".equals(isGaraOffertaUnica)){
              valoriTuttiUguali = false;
              metsoglia=null;
              metcoeff = null;
              break;
            }
          }
          if(valoriTuttiUguali){
            pageContext.getRequest().setAttribute("metsoglia", metsoglia);
            pageContext.getRequest().setAttribute("metcoeff", metcoeff);
            pageContext.getRequest().setAttribute("valoriTuttiUguali", valoriTuttiUguali);
          }
        }

      } catch (SQLException e) {
        throw new JspException("Errore nella lettura dei valori  di GARE1.METSOGLIA e GARE1.METCOEFF per la gara"
            + ngara, e);
      } catch (GestoreException e) {
        throw new JspException("Errore nella lettura dei valori  di GARE1.METSOGLIA e GARE1.METCOEFF per la gara"
            + ngara, e);
      }
    }
    return null;
  }

}