package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetDiscussioneAltriDatiFunction extends AbstractFunzioneTag {

  public GetDiscussioneAltriDatiFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))) {

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

      if (params[1] != null) {
        Long discid_p = new Long((String) params[1]);

        try {

          // Numero totale messaggi
          Long numeroMessaggi = (Long) sqlManager.getObject("select count(discid) from w_discuss where discid_p = ? and discmesspubbl = ?",
              new Object[] { discid_p, "1" });
          if (numeroMessaggi == null) {
            numeroMessaggi = new Long(0);
          }
          pageContext.setAttribute("numeroMessaggi", numeroMessaggi, PageContext.REQUEST_SCOPE);

          // Numero totale messaggi non letti per l'utente corrente
          String selectMessaggiLettiUtente = "select count(w_discuss.discid) from w_discuss, w_discread "
              + " where w_discuss.discid_p = ? "
              + " and w_discuss.discmesspubbl = ? "
              + " and w_discuss.discid_p = w_discread.discid_p "
              + " and w_discuss.discid = w_discread.discid "
              + " and w_discread.discmessope = ?";

          ProfiloUtente profiloUtente = (ProfiloUtente) pageContext.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);

          Long numeroMessaggiLettiUtente = (Long) sqlManager.getObject(selectMessaggiLettiUtente, new Object[] { discid_p, "1",
              new Long(profiloUtente.getId()) });
          if (numeroMessaggiLettiUtente == null) {
            numeroMessaggiLettiUtente = new Long(0);
          }
          pageContext.setAttribute("numeroMessaggiLettiUtente", numeroMessaggiLettiUtente, PageContext.REQUEST_SCOPE);

          // Numero messaggi non letti per l'utente corrente
          Long numeroMessaggioNonLettiUtente = new Long(numeroMessaggi.longValue() - numeroMessaggiLettiUtente.longValue());
          if (numeroMessaggioNonLettiUtente.longValue() < 0) {
            numeroMessaggioNonLettiUtente = new Long(0);
          }
          pageContext.setAttribute("numeroMessaggioNonLettiUtente", numeroMessaggioNonLettiUtente, PageContext.REQUEST_SCOPE);

          // Data ultimo aggiornamento
          String dataAggString = sqlManager.getDBFunction("DATETIMETOSTRING",
        	        new String[] { "discmessins" });
          
          String dataAggiornamento = (String) sqlManager.getObject(
              "select max(" + dataAggString + ") from w_discuss where discid_p = ? and discmesspubbl = ?", new Object[] { discid_p, "1" });
          Date dataaggDate = UtilityDate.convertiData(dataAggiornamento, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
          if (dataaggDate != null) {
            pageContext.setAttribute("dataAggiornamento",
                UtilityDate.convertiData(dataaggDate, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS), PageContext.REQUEST_SCOPE);
          } else {
            pageContext.setAttribute("dataaggDate", "", PageContext.REQUEST_SCOPE);
          }

        } catch (SQLException e) {
          throw new JspException("Errore nella lettura di altri dati della discussione", e);
        }
      }
    }

    return null;

  }
}
