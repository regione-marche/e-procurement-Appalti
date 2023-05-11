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

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

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
