/*
 * Created on 31/05/13
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
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per inizializzare i campi fittizzi NPLETTCOMESCLOFTE, DPLETTCOMESCLOFTE, NPLETTCOMESCLOFEC e DPLETTCOMESCLOFEC
 * con i valori dei campi GARESTATI.NPLETTCOMESCL e GARESTATI.DPLETTCOMESCL
 *
 * @author Marcello Caminiti
 */
public class GestioneAggiudicazioneProvvisoriaFunction extends AbstractFunzioneTag {

  public GestioneAggiudicazioneProvvisoriaFunction() {
    super(5, new Class[] { PageContext.class,String.class,String.class,String.class,String.class });
  }

  @SuppressWarnings("unchecked")
  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String codgar = (String)params[1];
    String ngara = (String)params[2];
    String fasgarEc = (String)params[4];
    Long stepGarEc = null;


    if(fasgarEc!=null && !"".equals(fasgarEc)){
      stepGarEc = new Long(((new Long(fasgarEc)).longValue()*10));
      String select="select NPLETTCOMESCL, DPLETTCOMESCL from GARESTATI where CODGAR=? and NGARA=? " +
          "and FASGAR=? and STEPGAR=?";

      String data=null;

      try {
       data=null;
        Vector<JdbcParametro> datiGarastati = sqlManager.getVector(select, new Object[] { codgar,ngara,new Long(fasgarEc),stepGarEc });
        if (datiGarastati != null && datiGarastati.size() > 0){
          String nprot = (String) (datiGarastati.get(0)).getValue();
          Date dprot = (Date) (datiGarastati.get(1)).getValue();
          if(dprot!=null)
            data = UtilityDate.convertiData(dprot, UtilityDate.FORMATO_GG_MM_AAAA);
          pageContext.setAttribute("nprotEc", nprot,
              PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("dprotEc", data,
              PageContext.REQUEST_SCOPE);
        }

      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre i dati di GARESTATI "
            + "della gara "
            + ngara, e);
      }
    }


    Long aqoper = null;
    String iaggiuinivalorizzato="NO";
    Long ultdetlic = null;
    try {
      //aqoper = (Long)sqlManager.getObject("select aqoper from gare1 where ngara=?", new Object[]{ngara});
      Vector datiGare1 = sqlManager.getVector("select aqoper,iaggiuini,ultdetlic from gare1 where ngara=?", new Object[]{ngara});
      if(datiGare1!=null && datiGare1.size()>0){
        aqoper=SqlManager.getValueFromVectorParam(datiGare1, 0).longValue();
        Double iaggiuini=SqlManager.getValueFromVectorParam(datiGare1, 1).doubleValue();
        ultdetlic=SqlManager.getValueFromVectorParam(datiGare1,2).longValue();
        if(iaggiuini!=null && !"2".equals(aqoper))
          iaggiuinivalorizzato="SI";
      }
      pageContext.setAttribute("aqoper", aqoper, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("iaggiuinivalorizzato", iaggiuinivalorizzato, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("ultdetlic", ultdetlic, PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException(
          "Errore nella lettura del campo GARE1.AQOPER", e);
    } catch (GestoreException e) {
      throw new JspException(
          "Errore nella lettura del campo GARE1.AQOPER", e);
    }

    try {
      Vector<JdbcParametro> datiGare = sqlManager.getVector("select impsic, modlicg from gare where ngara=?", new Object[] { ngara });
      if (datiGare != null && datiGare.size() > 0){
        Double impsic = (Double) (datiGare.get(0)).getValue();
        Long modlicg = (Long) (datiGare.get(1)).getValue();
        pageContext.setAttribute("impsic", impsic,
            PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("modlicg", modlicg,
            PageContext.REQUEST_SCOPE);
      }
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre i dati di GARE "
          + "della gara " + ngara, e);

    }

    if(new Long(2).equals(aqoper)){



      try {
        List listaDitteAggiudicatarie = sqlManager.getListVector("select aq.id, aq.ngara, aq.dittao, i.nomest, aq.ribagg, aq.puntot, aq.iaggiu, d.impoff, "
            + "d.impperm, d.impcano, d.ricsub, aq.ridiso, aq.impgar, aq.banapp, aq.coorba, aq.codbic, aq.ribaggini, aq.iaggiuini from ditgaq aq, impr i, ditg d where aq.ngara=? and aq.dittao = i.codimp and "
            + "aq.ngara=d.ngara5 and aq.dittao=d.dittao order by aq.numord", new Object[]{ngara});
        pageContext.setAttribute("listaDitteAggiudicatarie", listaDitteAggiudicatarie,
            PageContext.REQUEST_SCOPE);
        long numeroDitteAggiudicatarie=0;
        if(listaDitteAggiudicatarie!=null & listaDitteAggiudicatarie.size()>0){
          numeroDitteAggiudicatarie=listaDitteAggiudicatarie.size();
        }
        pageContext.setAttribute("numeroDitteAggiudicatarie", new Long(numeroDitteAggiudicatarie),
            PageContext.REQUEST_SCOPE);
      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre i dati di DITGAQ "
            + "della gara "
            + ngara, e);
      }

    }

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
        "tabellatiManager", pageContext, TabellatiManager.class);

    String descrizioneA1132 = tabellatiManager.getDescrTabellato("A1132", "1");
    if(descrizioneA1132!=null && !"".equals(descrizioneA1132))
      descrizioneA1132 = descrizioneA1132.substring(0, 1);
    this.getRequest().setAttribute("descrizioneA1132", descrizioneA1132);

    return null;
  }

}
