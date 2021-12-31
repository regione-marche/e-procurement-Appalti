package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetWSERPLottiSenzaCarrelloAction extends Action {

  private SqlManager sqlManager;

  private GestioneWSERPManager gestioneWSERPManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }


  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String servizio = request.getParameter("servizio");
    if(servizio==null || "".equals(servizio)){
      servizio ="WSERP";
    }

    JSONArray jsonLottiDispArray = new JSONArray();


    ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long syscon = new Long(profilo.getId());
    String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);

    String username = credenziali[0];
    String password = credenziali[1];


    WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi(servizio);
    String tipoWSERP = configurazione.getRemotewserp();


    String codiceGara = request.getParameter("codiceGara");
    List<?> lottiDisponibili = null;

    if("SMEUP".equals(tipoWSERP)){
      lottiDisponibili = sqlManager.getListVector(
          "select ga.ngara,ga.codiga from gare ga" +
          " where ga.codgar1 = ? and ga.ngara<> ga.codgar1 and ga.modlicg in (5, 6, 14, 16)" +
          " and not exists (select gc.ngara from gcap gc where gc.ngara = ga.ngara )" +
          " order by ga.ngara",new Object[] { codiceGara });
    }else{
      lottiDisponibili = sqlManager.getListVector(
          "select ga.ngara,ga.codiga from gare ga" +
          " where ga.codgar1 = ? and ga.ngara<> ga.codgar1 and ga.modlicg in (5, 6, 14, 16)" +
          " order by ga.ngara",new Object[] { codiceGara });
    }


    if (lottiDisponibili != null && lottiDisponibili.size() > 0) {
      for (int i = 0; i < lottiDisponibili.size(); i++) {
        String ngara = (String) SqlManager.getValueFromVectorParam(lottiDisponibili.get(i), 0).getValue();
        String codiga = (String) SqlManager.getValueFromVectorParam(lottiDisponibili.get(i), 1).getValue();
        Object[] row = new Object[2];
        row[0] = ngara;
        row[1] = ngara;
        jsonLottiDispArray.add(row);
      }
    }

    out.print(jsonLottiDispArray);
    out.flush();

    return null;

  }
}

