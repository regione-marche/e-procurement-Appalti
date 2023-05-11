package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetWSLoginAction extends Action {

  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONArray jsonArray = new JSONArray();

    Long syscon = null;
    String wsdmLoginComune = null;

    String servizio = request.getParameter("servizio");
    String idconfi = null;
    String filtroConfi = "";
    
    List<Object> parameters = new ArrayList<Object>();
    if("WSERP".equals(servizio) || "WSERP_L190".equals(servizio)){
      syscon = new Long(-1);
    }else{
      if ("FASCICOLOPROTOCOLLO".equals(servizio) || "DOCUMENTALE".equals(servizio)){
        idconfi = request.getParameter("idconfi");
        filtroConfi = " and idconfiwsdm = ? " ;
      }
      wsdmLoginComune = ConfigManager.getValore(GestioneWSDMManager.PROP_WSDM_LOGIN_COMUNE+idconfi);
      if (wsdmLoginComune != null && "1".equals(wsdmLoginComune)) {
        syscon = new Long(-1);
      } else {
        syscon = new Long(request.getParameter("syscon"));
      }
    }
    parameters.add(syscon);
    parameters.add(servizio);
    if(!"".equals(filtroConfi)) 
      parameters.add(idconfi);
    
   
    List<?> datiWSLogin = sqlManager.getVector(
        "select username, password, ruolo, nome, cognome, codiceuo, idutente, idutenteunop from wslogin where syscon = ? and servizio = ?" + filtroConfi, 
        parameters.toArray());
    if (datiWSLogin != null && datiWSLogin.size() > 0) {
      String username = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 0).getValue();
      String password = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 1).getValue();

      /*
      String passwordDecoded = null;
      if (password != null && password.trim().length() > 0) {
        ICriptazioneByte passwordICriptazioneByte = null;
        passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
            ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), password.getBytes(),
            ICriptazioneByte.FORMATO_DATO_CIFRATO);
        passwordDecoded = new String(passwordICriptazioneByte.getDatoNonCifrato());
      }
      */
      String passwordDecoded=GestioneWSDMManager.decodificaPassword(password);

      String ruolo = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 2).getValue();
      String nome = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 3).getValue();
      String cognome = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 4).getValue();
      String codiceuo = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 5).getValue();
      String idutente = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 6).getValue();
      String idutenteunop = (String) SqlManager.getValueFromVectorParam(datiWSLogin, 7).getValue();

      jsonArray.add(new Object[] { username, passwordDecoded, ruolo, nome, cognome, codiceuo, idutente, idutenteunop, wsdmLoginComune });
    }

    out.print(jsonArray);
    out.flush();

    return null;

  }

}
