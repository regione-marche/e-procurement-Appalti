/*
 * Created on 28/05/2020
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
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che recupera i dati di collegamento nella lista degli ordini NSO
 *
 * @author Cristian.Febas
 */
public class GetNsoDatiListaOrdiniFunction extends AbstractFunzioneTag {

  public GetNsoDatiListaOrdiniFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String idOrdine = (String) params[1];
    idOrdine = UtilityStringhe.convertiNullInStringaVuota(idOrdine);
    if(!"".equals(idOrdine)){
      Long idO = new Long(idOrdine);
      String isPeriodoVariazione = "0";

      String selectOrdine = "select b.codord,c.codord,a.data_scadenza, a.data_limite_mod" +
      		" from nso_ordini a" +
      		" left join nso_ordini b on a.id_padre=b.id" +
      		" left join nso_ordini c on a.id_originario=c.id" +
      		" where a.id = ?";

      try {

        Vector<?> datiOrdine = sqlManager.getVector(selectOrdine,new Object[] { new Long(idO) });
        if (datiOrdine != null && datiOrdine.size() > 0){
          String codiceOrdineCollegato = SqlManager.getValueFromVectorParam(datiOrdine, 0).stringValue();
          pageContext.setAttribute("codiceOrdineCollegato",codiceOrdineCollegato,PageContext.REQUEST_SCOPE);
          String codiceOrdineOriginario = SqlManager.getValueFromVectorParam(datiOrdine, 1).stringValue();
          pageContext.setAttribute("codiceOrdineOriginario",codiceOrdineOriginario,PageContext.REQUEST_SCOPE);
          Date dataScadenza = (Date) SqlManager.getValueFromVectorParam(datiOrdine, 2).getValue();
          Date dataLimiteMod = (Date) SqlManager.getValueFromVectorParam(datiOrdine, 3).getValue();
          Date dataOggi = UtilityDate.getDataOdiernaAsDate();
          if(dataScadenza!=null && dataOggi.before(dataScadenza)){
            isPeriodoVariazione = "1";
          } else if(dataLimiteMod!=null && dataOggi.before(dataLimiteMod)) {
            isPeriodoVariazione = "1";
          }
          pageContext.setAttribute("isPeriodoVariazione",isPeriodoVariazione,PageContext.REQUEST_SCOPE);
        }

      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre i dati di collegamento dell'ordine", e);
      } catch (GestoreException ge) {
        throw new JspException("Errore nell'estrarre i dati di collegamento dell'ordine", ge);
      } catch (Exception ex) {
        throw new JspException("Errore nell'estrarre i dati di collegamento dell'ordine", ex);
      }

    }

    return null;
  }

}
