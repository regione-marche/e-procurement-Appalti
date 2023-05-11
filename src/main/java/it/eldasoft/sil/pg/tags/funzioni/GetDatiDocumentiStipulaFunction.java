/*
 * Created on 27/04/2021
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
 * Funzione che recupera i dati di ogni documento di stipula
 *
 * @author Cristian.Febas
 */
public class GetDatiDocumentiStipulaFunction extends AbstractFunzioneTag {

  public GetDatiDocumentiStipulaFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String idDocStipula = (String) params[1];
    idDocStipula = UtilityStringhe.convertiNullInStringaVuota(idDocStipula);
    String vis = null;
    if(!"".equals(idDocStipula)){
      Long idDS = new Long(idDocStipula);

      String selectDocStipula = "select visibilita,statodoc" +
      		" from G1DOCSTIPULA" +
      		" where id = ?";

      try {

        Vector<?> datiDocStipula = sqlManager.getVector(selectDocStipula,new Object[] { new Long(idDS) });
        if (datiDocStipula != null && datiDocStipula.size() > 0){
          Long visibilita = SqlManager.getValueFromVectorParam(datiDocStipula, 0).longValue();
          pageContext.setAttribute("visibilita",visibilita,PageContext.REQUEST_SCOPE);
          Long statodoc = SqlManager.getValueFromVectorParam(datiDocStipula, 1).longValue();
          pageContext.setAttribute("statodoc",statodoc,PageContext.REQUEST_SCOPE);
        }

      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre i dati del documento di stipula", e);
      } catch (GestoreException ge) {
        throw new JspException("Errore nell'estrarre i dati del documento di stipula", ge);
      } catch (Exception ex) {
        throw new JspException("Errore nell'estrarre i dati del documento di stipula", ex);
      }

    }

    return vis;
  }

}
