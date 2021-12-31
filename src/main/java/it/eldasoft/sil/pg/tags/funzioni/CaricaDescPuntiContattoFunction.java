/*
 * Created on 04/03/11
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
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che estrae il valore di CATG.CATIGA
 *
 * @author Marcello Caminiti
 */
public class CaricaDescPuntiContattoFunction extends AbstractFunzioneTag {

  public CaricaDescPuntiContattoFunction() {
    super(4, new Class[] { PageContext.class,String.class, String.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String codice = (String) GeneralTagsFunction.cast("string", params[1]);
    String entita = (String) GeneralTagsFunction.cast("string", params[2]);
    String select="";
    String ret="";



    try {
      if("TORN".equals(entita)){
        select="select cenint,PCOPRE,PCODOC,PCOOFF,PCOGAR from torn where codgar=?";
        Vector datiTorn = sqlManager.getVector(select, new Object[]{codice});
        if(datiTorn!=null && datiTorn.size()>0){
          String cenint = SqlManager.getValueFromVectorParam(datiTorn, 0).getStringValue();
          if(cenint!=null && !"".equals(cenint)){
            select="select nompun from punticon where codein=? and numpun=?";
            for(int i=0;i<4;i++){
              Long progressivo = SqlManager.getValueFromVectorParam(datiTorn, i+1).longValue();
              if(progressivo!=null){
                String desc = (String)sqlManager.getObject(select, new Object[]{cenint,progressivo});
                switch (i){
                  case 0:{
                    pageContext.setAttribute("initNOMPUN_PCOPRE", desc,
                        PageContext.REQUEST_SCOPE);
                    break;
                  }
                  case 1:{
                    pageContext.setAttribute("initNOMPUN_PCODOC", desc,
                        PageContext.REQUEST_SCOPE);
                    break;
                  }
                  case 2:{
                    pageContext.setAttribute("initNOMPUN_PCOOFF", desc,
                        PageContext.REQUEST_SCOPE);
                    break;
                  }
                  case 3:{
                    pageContext.setAttribute("initNOMPUN_PCOGAR", desc,
                        PageContext.REQUEST_SCOPE);
                    break;
                  }
                }
              }
            }

          }
        }
      }else if("GARECONT".equals(entita)){
        //Ricerca di mercato, il campo CENINT si trova su MERIC
        String idMeric = (String) GeneralTagsFunction.cast("string", params[3]);
        String cenint = null;
        if(idMeric!=null && !"".equals(idMeric)){
          cenint = (String)sqlManager.getObject("select cenint from meric where id=?", new Object[]{new Long(idMeric)});
        }else{
          cenint = (String)sqlManager.getObject("select cenint from torn where codgar=?", new Object[]{codice});
          if(codice.startsWith("$"))
            codice = codice.substring(1);
        }
        if(cenint!=null){
          select="select PCOESE,PCOFAT from garecont where ngara=? and ncont=1";
          Vector datiGARECONT = sqlManager.getVector(select, new Object[]{codice});
          if(datiGARECONT!=null && datiGARECONT.size()>0){
            if(cenint!=null && !"".equals(cenint)){
              select="select nompun from punticon where codein=? and numpun=?";
              for(int i=0;i<2;i++){
                Long progressivo = SqlManager.getValueFromVectorParam(datiGARECONT, i).longValue();
                if(progressivo!=null){
                  String desc = (String)sqlManager.getObject(select, new Object[]{cenint,progressivo});
                  switch (i){
                    case 0:{
                      pageContext.setAttribute("initNOMPUN_PCOESE", desc,
                          PageContext.REQUEST_SCOPE);
                      break;
                    }
                    case 1:{
                      pageContext.setAttribute("initNOMPUN_PCOFAT", desc,
                          PageContext.REQUEST_SCOPE);
                      break;
                    }
                  }
                }
              }
              pageContext.setAttribute("initCENINT", cenint,
                  PageContext.REQUEST_SCOPE);
            }
          }
        }
       }
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura dei punti di contatto ", e);
    }catch (GestoreException e) {
      throw new JspException(
          "Errore durante la lettura dei punti di contatto ", e);
    }


    return ret;

  }

}
