package it.eldasoft.sil.pg.tags.funzioni;

import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang3.StringUtils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetListaGareCollegateFunction extends AbstractFunzioneTag {
  public GetListaGareCollegateFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    
    GestioneWSDMManager gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        pageContext, GestioneWSDMManager.class);
    
    try {
      final ProfiloUtente profiloUtente = (ProfiloUtente) pageContext.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      String filtroLivelloUtente = FiltroLivelloUtenteFunction.getFiltroLivelloUtente("V_GARE_TORN", profiloUtente);
      if(StringUtils.isNotBlank(filtroLivelloUtente))
        filtroLivelloUtente = "CAST(CASE WHEN "+filtroLivelloUtente+" THEN 'true' ELSE 'false' END as VARCHAR(5)), "; //SEPR-1373
      else
        filtroLivelloUtente = "CAST('true' as VARCHAR(5)), ";

      String sql = "select g.ngara,"+filtroLivelloUtente+"g.codgar1, V_GARE_TORN.codice, V_GARE_TORN.genere, t.gartel from V_GARE_TORN, gare g, gare gg, torn t where g.codgar1=V_GARE_TORN.codgar and (g.ngarapa = gg.ngara or g.preced = gg.ngara) and t.codgar=g.codgar1 and gg.codgar1=? order by g.ngara";

      List<Vector> result = sqlManager.getListVector(sql, new Object[] {params[1]});
      if(result!=null && result.size()>0) {
        for(int i=0; i<result.size();i++) {
        String codgar1 = SqlManager.getValueFromVectorParam(result.get(i), 2).stringValue();
        result.get(i).add(gestioneWSDMManager.getWsdmConfigurazioneFromCodgar(codgar1, "PG"));
        }
      }
      this.getRequest().setAttribute("listaGareEAbilitazione", result);
      
    } catch (Exception e) {
      throw new JspException("Errore nella lettura delle gare collegate" ,
          e);
    }

    return null;

  }
}
