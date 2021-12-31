package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetWSDMDescrizioneTabellatoPrismaAction extends Action {

  private GestioneWSDMManager gestioneWSDMManager;

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject json = new JSONObject();


    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String ruolo = request.getParameter("ruolo");
    String nome = request.getParameter("nome");
    String cognome = request.getParameter("cognome");
    String codiceuo = request.getParameter("codiceuo");
    String idutente = request.getParameter("idutente");
    String idutenteunop = request.getParameter("idutenteunop");
    String servizio = request.getParameter("servizio");
    String classifica = request.getParameter("classifica");
    String idconfi = request.getParameter("idconfi");

    List<String[]> listaClassifiche = this.gestioneWSDMManager.getValoriClassifiche(username, password, ruolo, nome, cognome, codiceuo,
        idutente, idutenteunop, servizio, classifica,null,null,idconfi);
    if(listaClassifiche!=null && listaClassifiche.size()==1){
      String [] classificaPrisma=listaClassifiche.get(0);
      json.put("classificafascicolodescrizione",classificaPrisma[1]);
    }

    out.println(json);
    out.flush();

    return null;

  }

}
