/*
 * Created on 11/05/2021
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
import java.sql.Timestamp;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Funzione che recupera i dati di stipula
 *
 * @author Cristian.Febas
 */
public class GetDatiStipulaFunction extends AbstractFunzioneTag {

  public GetDatiStipulaFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String idStipula = (String) params[1];
    idStipula = UtilityStringhe.convertiNullInStringaVuota(idStipula);
    if(!"".equals(idStipula)){
      Long idS = Long.valueOf(idStipula);

      String selectStipula = "select codstipula,syscon,assegnatario,stato,id_padre,id_originario from g1stipula where id = ?";

      try {

        Vector<?> datiStipula = sqlManager.getVector(selectStipula,new Object[] { idS });
        if (datiStipula != null && datiStipula.size() > 0){
         String codStipula = SqlManager.getValueFromVectorParam(datiStipula, 0).stringValue();
          pageContext.setAttribute("codStipula",codStipula,PageContext.REQUEST_SCOPE);
          Long syscon = SqlManager.getValueFromVectorParam(datiStipula, 1).longValue();
          pageContext.setAttribute("creatore",syscon,PageContext.REQUEST_SCOPE);
          Long assegnatario = SqlManager.getValueFromVectorParam(datiStipula, 2).longValue();
          pageContext.setAttribute("assegnatario",assegnatario,PageContext.REQUEST_SCOPE);
          Long stato = SqlManager.getValueFromVectorParam(datiStipula, 3).longValue();
          pageContext.setAttribute("stato",stato,PageContext.REQUEST_SCOPE);
          Long id_padre = SqlManager.getValueFromVectorParam(datiStipula, 4).longValue();
          pageContext.setAttribute("idpadre",id_padre,PageContext.REQUEST_SCOPE);
          Long id_originario = SqlManager.getValueFromVectorParam(datiStipula, 5).longValue();
          pageContext.setAttribute("idoriginario",id_originario,PageContext.REQUEST_SCOPE);

          if(stato!=null && stato.longValue()==6) {
            Vector<?> datiG1aggisto = sqlManager.getVector("select ditta, dattoa, nomest from g1aggisto, impr where idstipula=? and ditta=codimp",new Object[] { idS });
            if (datiG1aggisto != null && datiG1aggisto.size() > 0){
              String ditta = SqlManager.getValueFromVectorParam(datiG1aggisto, 0).stringValue();
              Timestamp dattoa = SqlManager.getValueFromVectorParam(datiG1aggisto, 1).dataValue();
              String dattoaString=null;
              if(dattoa!=null)
                dattoaString=UtilityDate.convertiData(dattoa, UtilityDate.FORMATO_GG_MM_AAAA);
              String nomest = SqlManager.getValueFromVectorParam(datiG1aggisto, 2).stringValue();
              Object iaggiu = sqlManager.getObject("select sum(iaggiu) from g1aggisto where idstipula=?",new Object[] { idS });
              pageContext.setAttribute("dittaG1aggisto",ditta,PageContext.REQUEST_SCOPE);
              pageContext.setAttribute("dattoaG1aggisto",dattoaString,PageContext.REQUEST_SCOPE);
              pageContext.setAttribute("nomestG1aggisto",nomest,PageContext.REQUEST_SCOPE);
              pageContext.setAttribute("iaggiuG1aggisto",iaggiu,PageContext.REQUEST_SCOPE);
            }
          }
        }

      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre i dati della stipula", e);
      } catch (GestoreException ge) {
        throw new JspException("Errore nell'estrarre i dati della stipula", ge);
      } catch (Exception ex) {
        throw new JspException("Errore nell'estrarre i dati della stipula", ex);
      }

    }

    return null;
  }

}
