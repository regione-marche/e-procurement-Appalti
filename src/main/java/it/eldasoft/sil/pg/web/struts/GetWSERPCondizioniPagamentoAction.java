package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPCondizionePagamentoResType;
import it.maggioli.eldasoft.ws.erp.WSERPCondizionePagamentoType;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetWSERPCondizioniPagamentoAction extends Action {

  private GestioneWSERPManager gestioneWSERPManager;

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }


  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONArray jsonArray = new JSONArray();

    String servizio = request.getParameter("servizio");
    if(servizio==null || "".equals(servizio))
      servizio ="WSERP";

    String tipoWSERP =null;
    WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
    if(configurazione.isEsito()){
      tipoWSERP = configurazione.getRemotewserp();
    }

    if("TPER".equals(tipoWSERP)){
      ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = new Long(profilo.getId());
      String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);

      String username = credenziali[0];
      String password = credenziali[1];


      WSERPCondizionePagamentoResType wserpCondizionePagamentoRes = gestioneWSERPManager.wserpListaCondizioniPagamento(username, password, servizio);

      if(wserpCondizionePagamentoRes.isEsito()){
        WSERPCondizionePagamentoType[] wserpCondizionePagamentoArray = wserpCondizionePagamentoRes.getCondizionePagamentoArray();
        if (wserpCondizionePagamentoArray != null && wserpCondizionePagamentoArray.length > 0) {

          for (int k = 0; k < wserpCondizionePagamentoArray.length; k++) {
            WSERPCondizionePagamentoType condPag_k = wserpCondizionePagamentoArray[k];
              String codice = condPag_k.getCodice();
              String descrizione = condPag_k.getDescrizione();
              Object[] row = new Object[2];
              row[0] = codice;
              row[1] = descrizione;

              jsonArray.add(row);
          }
        }

      }else{
        ;
      }

    }

    out.println(jsonArray);
    out.flush();
    return null;
  }

}
