package it.eldasoft.sil.w3.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

public class GetListaUtentiDelegabiliFunction extends AbstractFunzioneTag {

  public GetListaUtentiDelegabiliFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
    	
    	 ProfiloUtente profilo = (ProfiloUtente) pageContext.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    	 HttpSession session = pageContext.getSession();
    	 String uffint = (String) session.getAttribute("uffint");
    	 //String codrup=  params[0].toString();
    	 uffint = StringUtils.stripToEmpty(uffint);
         // Ricavo la lista
         List<?> listaUsers;
         
         if("".equals(uffint)) {
           listaUsers = sqlManager.getListVector(
               "select syscon, sysute from usrsys where sysdisab=0 and "
               +"syscon not in (select id_collaboratore from w9deleghe where cfrup = ? and id_collaboratore IS NOT NULL and codein IS NULL) and syscon <> ? order by sysute asc",
                      new Object[] {profilo.getCodiceFiscale(),profilo.getId()});
         }else {
           
             listaUsers = sqlManager.getListVector(
                 "select a.syscon, b.sysute from usr_ein a join usrsys b on a.syscon = b.syscon where a.codein = ?  and b.sysdisab=0 and "
                 +"a.syscon not in (select id_collaboratore from w9deleghe where cfrup = ? and codein = ? and id_collaboratore IS NOT NULL) and a.syscon <> ? order by b.sysute asc",
                        new Object[] { uffint, profilo.getCodiceFiscale(), uffint, profilo.getId() });  
         }
         
         pageContext.setAttribute("listaDelegabili", listaUsers, PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
      throw new JspException(
          "Errore nell'estrazione dei dati per la popolazione della/e dropdown list",
          e);
    }
    return null;
  }

}
