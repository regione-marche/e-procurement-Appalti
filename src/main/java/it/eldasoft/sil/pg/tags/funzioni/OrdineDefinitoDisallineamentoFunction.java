package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class OrdineDefinitoDisallineamentoFunction extends AbstractFunzioneTag {

  public OrdineDefinitoDisallineamentoFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object params[]) throws JspException {

    try {

      String ngara = (String) params[1];

      MEPAManager mepaManager = (MEPAManager) UtilitySpring.getBean("mepaManager", pageContext, MEPAManager.class);

      HashMap<?, ?> infoControlli = mepaManager.controlloDisallineamentoProdottiGARE(ngara);
      pageContext.setAttribute("listaControlloArticoli", infoControlli.get("listaControlloArticoli"));
      pageContext.setAttribute("numeroWarning", infoControlli.get("numeroWarning"));
      pageContext.setAttribute("numeroErrori", infoControlli.get("numeroErrori"));
    } catch (GestoreException e) {
      throw new JspException("Errore nella funzione di controllo degli articoli e dei prodotti della ricerca di mercato", e);
    }

    return null;

  }

}