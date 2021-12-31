package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class EscludiAggiudicatarioUscenteAction extends ActionBaseNoOpzioni {

  protected static final String FORWARD_ERRORE  = "esclagguscerror";
  protected static final String FORWARD_SUCCESS = "esclagguscsuccess";

  static Logger                 logger          = Logger.getLogger(EscludiAggiudicatarioUscenteAction.class);

  private SqlManager            sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("EscludiAggiudicatarioUscente: inizio metodo");


    String filtroAffidatariEsclusi = null;
    String elencoAffidatariEsclusi = null;

    String dittaEsclusa = request.getParameter("dittaEsclusa");
    String listaElencoAffidatariEsclusi = request.getParameter("elencoAffidatariEsclusi");

    String creaRigeneraFiltro =  request.getParameter("creaRigeneraFiltro");
    String ngara = request.getParameter("ngara");
    String garaElenco = request.getParameter("garaElenco");


    if(dittaEsclusa != null && dittaEsclusa != ""){
      filtroAffidatariEsclusi = " codice not in (";
      elencoAffidatariEsclusi="";

      if("true".equals(creaRigeneraFiltro)){
        filtroAffidatariEsclusi = filtroAffidatariEsclusi + "'"+dittaEsclusa+"'";
        elencoAffidatariEsclusi+=dittaEsclusa;

      }else{
        //Integro il filtro
        String arrayElencoAffidatariEsclusi[]= listaElencoAffidatariEsclusi.split(",");
        if(arrayElencoAffidatariEsclusi!= null && arrayElencoAffidatariEsclusi.length>0){
          for (int i = 0; i < arrayElencoAffidatariEsclusi.length; i++) {
            String codImpresa = arrayElencoAffidatariEsclusi[i];
            if(!"".equals(elencoAffidatariEsclusi)){
              filtroAffidatariEsclusi = filtroAffidatariEsclusi+=",";
              elencoAffidatariEsclusi+=",";
            }
            filtroAffidatariEsclusi = filtroAffidatariEsclusi + "'"+codImpresa+"'";
            elencoAffidatariEsclusi+=codImpresa;
          }
          filtroAffidatariEsclusi = filtroAffidatariEsclusi + "," + "'" +dittaEsclusa+"'";
          elencoAffidatariEsclusi+=","+dittaEsclusa;

        }
      }

      filtroAffidatariEsclusi =" and " + filtroAffidatariEsclusi + ")";

    }




    HttpSession sessione = request.getSession();
    sessione.setAttribute("filtroAffidatariEsclusi", filtroAffidatariEsclusi);
    sessione.setAttribute("elencoAffidatariEsclusi", elencoAffidatariEsclusi);

    request.setAttribute("ngara", ngara);
    request.setAttribute("garaElenco", garaElenco);
    request.setAttribute("RISULTATO", "OK");


    String target = FORWARD_SUCCESS;

    if (logger.isDebugEnabled())
      logger.debug("EscludiAggiudicatarioUscente: fine metodo");

    return mapping.findForward(target);

  }

}
