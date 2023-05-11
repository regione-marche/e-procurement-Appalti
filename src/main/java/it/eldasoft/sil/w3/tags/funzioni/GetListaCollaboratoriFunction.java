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

public class GetListaCollaboratoriFunction extends AbstractFunzioneTag {

  public GetListaCollaboratoriFunction() {
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
         uffint = StringUtils.stripToEmpty(uffint);
         // Ricavo la lista
         List<?> listaRUP;

         String toAppend = "";
         String archiviFiltrati = ConfigManager.getValore("it.eldasoft.associazioneUffintAbilitata.archiviFiltrati");
         boolean tecniFiltrato = (archiviFiltrati.indexOf("TECNI") != -1);
        
         if(!"".equals(uffint)) {
           if(tecniFiltrato) {
             toAppend = "and CGENTEI = '"+uffint+"'";
           }
           listaRUP = sqlManager.getListVector(
               "select codtec, nomtec, cftec  from tecni where (cftec in (select cfrup from w9deleghe where id_collaboratore = ? and codein = ?) or cftec = ?  and nomtec is not null and cftec is not null) "+toAppend+" order by nomtec",
                    new Object[] { profilo.getId(), uffint, profilo.getCodiceFiscale()});
         }else {
           listaRUP = sqlManager.getListVector(
               "select codtec, nomtec, cftec  from tecni where cftec in (select cfrup from w9deleghe where id_collaboratore = ?) or cftec = ? and nomtec is not null and cftec is not null order by nomtec",
                    new Object[] { profilo.getId(), profilo.getCodiceFiscale()});
         }
         pageContext.setAttribute("listaRUP", listaRUP, PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
      throw new JspException(
          "Errore nell'estrazione dei dati per la popolazione della dropdown list",
          e);
    }
    return null;
  }

}
