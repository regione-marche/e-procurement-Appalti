package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.sil.pg.web.struts.EseguiOperazioniConversazioneAction;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetDiscDestFunction extends AbstractFunzioneTag {

  public GetDiscDestFunction() {
    super(2, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

      if (params[1] != null ) {
        Long discid_p = new Long((String) params[1]);
        
        ProfiloUtente profilo = (ProfiloUtente) pageContext.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        Long syscon = new Long(profilo.getId());

        try {
          Long discdesttype = (Long) sqlManager.getObject("select discdesttype from w_discuss_p where discid_p = ?", new Object[] { discid_p });
          // Destinatari alcuni
          if (EseguiOperazioniConversazioneAction.DESTINATARI_ALCUNI.equals(discdesttype)) {
            Long count = (Long) sqlManager.getObject("select count(1) from W_DISCDEST where discid_p = ? and discid = -1", new Object[] { discid_p});
            pageContext.setAttribute("countDestinatariAlcuni", count, PageContext.REQUEST_SCOPE);
          }else {
            Long count = (Long) sqlManager.getObject("select count(1) from g_permessi join usrsys on g_permessi.syscon=usrsys.syscon join w_discuss_p on"
                + "((w_discuss_p.disckey1=CAST(g_permessi.idstipula as varchar(12)) and w_discuss_p.discent='G1STIPULA') or w_discuss_p.disckey1=g_permessi.codgar or "+sqlManager.getDBFunction("concat",  new String[] {"'$'","w_discuss_p.disckey1"})+"=g_permessi.codgar)"
                + " where usrsys.email is not null and usrsys.email <> '' and w_discuss_p.discid_p=? and g_permessi.syscon<> ?", new Object[] {discid_p,syscon});
            pageContext.setAttribute("countDestinatariTutti", count, PageContext.REQUEST_SCOPE);
          }
        } catch (SQLException e) {
          throw new JspException("Errore nella lettura del numero dei destinatari della discussione", e);
        }
      }
    }


    return null;

  }
}
