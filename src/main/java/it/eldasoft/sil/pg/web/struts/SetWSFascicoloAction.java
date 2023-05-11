package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class SetWSFascicoloAction extends Action {

  private GestioneWSDMManager gestioneWSDMManager;

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    String entita = request.getParameter("entita");
    String key1 = request.getParameter("key1");
    String key2 = request.getParameter("key2");
    String key3 = request.getParameter("key3");
    String key4 = request.getParameter("key4");

    String codice = request.getParameter("codice");
    Long anno=null;
    String annoString = request.getParameter("anno");
    if(annoString!=null && !"".equals(annoString))
      anno = new Long(annoString);
    String numero = request.getParameter("numero");
    String classifica = request.getParameter("classifica");
    String codiceAoo = request.getParameter("codiceAOO");
    String codiceUfficio = request.getParameter("codiceUfficio");
    String struttura = request.getParameter("struttura");
    String isRiservatezzaAttiva = request.getParameter("isRiservatezzaAttiva");
    String classificadescrizione = request.getParameter("classificadescrizione");
    String voce = request.getParameter("voce");
    String codiceAooDes = request.getParameter("desaoo");
    String codiceUfficioDes = request.getParameter("desuff");
    Long riservatezza = null;
    if(!"0".equals(isRiservatezzaAttiva)){
      riservatezza = new Long(1);
    }
    this.gestioneWSDMManager.setWSFascicolo(entita, key1, key2, key3, key4, codice, anno, numero,classifica,codiceAoo,codiceUfficio,
        struttura,riservatezza,classificadescrizione,voce,codiceAooDes,codiceUfficioDes);

    return null;

  }

}
