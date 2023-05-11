/*
 * Created on 16/01/2023
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
import java.util.ArrayList;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che verifica se il prodotto è stato assegnato
 *
 * @author Alex.Mancini
 */
public class IsProdottoAssegnato extends AbstractFunzioneTag {

  public IsProdottoAssegnato() {
    super(3, new Class[] { PageContext.class,String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ngara = (String) params[1];
    ngara = UtilityStringhe.convertiNullInStringaVuota(ngara);
    String contafStr = (String) params[2];
    contafStr = UtilityStringhe.convertiNullInStringaVuota(contafStr);
    String isAssegnato = "false";


    String selectQtaordinata = "SELECT qtaordinata"
        + " FROM v_gcap_dpre"
        + " WHERE ngara = ?"
        + " AND contaf = ?";

    if(!"".equals(ngara) && !"".equals(contafStr)){
      Long contaf = Long.valueOf(contafStr);

      List datiQtaordinata = new ArrayList();
      try {
        datiQtaordinata = sqlManager.getListVector(selectQtaordinata,new Object[] { ngara,contaf });

        if (datiQtaordinata != null && datiQtaordinata.size() > 0){
          boolean trovato = false;
          for (int i = 0; i < datiQtaordinata.size() && !trovato; i++) {
            Double qtaordinata = SqlManager.getValueFromVectorParam(datiQtaordinata.get(i), 0).doubleValue();
            if(qtaordinata != null) {
              trovato = true;
            }
          }
          if(trovato) {
            isAssegnato = "true";
          }
        }
        pageContext.setAttribute("isAssegnato",isAssegnato,PageContext.REQUEST_SCOPE);

      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre della quantita ordinata dell'articolo", e);
      } catch (GestoreException ge) {
        throw new JspException("Errore nell'estrarre della quantita ordinata dell'articolo", ge);
      }

    }

    return null;
  }

}
