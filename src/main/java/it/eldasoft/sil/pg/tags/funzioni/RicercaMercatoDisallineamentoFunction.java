package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.utils.spring.UtilitySpring;
import java.util.HashMap;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class RicercaMercatoDisallineamentoFunction extends AbstractFunzioneTag {

  public RicercaMercatoDisallineamentoFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object params[]) throws JspException {

    try {

      String meric_id_s = (String) params[1];
      Long meric_id = null;
      if (meric_id_s != null && meric_id_s != "") {
        meric_id = new Long((String) params[1]);
      }
      String codimp = (String) params[2];

      MEPAManager mepaManager = (MEPAManager) UtilitySpring.getBean("mepaManager", pageContext, MEPAManager.class);

      HashMap<?, ?> infoControlli = mepaManager.controlloDisallineamentoProdotti(meric_id, codimp);
      pageContext.setAttribute("listaControlloArticoli", infoControlli.get("listaControlloArticoli"));
      pageContext.setAttribute("numeroWarning", infoControlli.get("numeroWarning"));
      pageContext.setAttribute("numeroErrori", infoControlli.get("numeroErrori"));

    } catch (GestoreException e) {
      throw new JspException("Errore nella funzione di controllo degli articoli e dei prodotti della ricerca di mercato", e);
    }

    return null;

  }

}