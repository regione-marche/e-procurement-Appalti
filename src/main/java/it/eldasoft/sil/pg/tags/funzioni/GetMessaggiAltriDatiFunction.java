package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetMessaggiAltriDatiFunction extends AbstractFunzioneTag {

  public GetMessaggiAltriDatiFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))) {

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

      if (params[1] != null && params[2] != null) {
        Long discid_p = new Long((String) params[1]);
        Long discid = new Long((String) params[2]);

        try {
          // Numero allegato
          Long numeroAllegati = (Long) sqlManager.getObject("select count(allnum) from w_discall where discid_p = ? and discid = ?",
              new Object[] { discid_p, discid });
          if (numeroAllegati == null) {
            numeroAllegati = new Long(0);
          }
          pageContext.setAttribute("numeroAllegati", numeroAllegati, PageContext.REQUEST_SCOPE);

          // Messaggio letto per l'utente corrente
          String messaggioLetto = "false";
          ProfiloUtente profiloUtente = (ProfiloUtente) pageContext.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
          Long cnt = (Long) sqlManager.getObject("select count(*) from w_discread where discid_p = ? and discid = ? and discmessope = ?",
              new Object[] { discid_p, discid, new Long(profiloUtente.getId()) });
          if (cnt != null && cnt.longValue() > 0) {
            messaggioLetto = "true";
          }
          pageContext.setAttribute("messaggioLetto", messaggioLetto, PageContext.REQUEST_SCOPE);
        } catch (SQLException e) {
          throw new JspException("Errore nella lettura di altri dati del messaggio", e);
        }
      }
    }

    return null;

  }
}
