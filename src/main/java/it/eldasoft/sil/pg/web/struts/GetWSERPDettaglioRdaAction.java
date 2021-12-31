package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;
import it.maggioli.eldasoft.ws.erp.WSERP_ServiceLocator;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.XML;

public class GetWSERPDettaglioRdaAction extends Action {

  static Logger               logger                    = Logger.getLogger(GetWSERPDettaglioRdaAction.class);

  private static final String PROP_WSERP_URL            = "wserp.erp.url";

  private SqlManager          sqlManager;

  private GestioneWSERPManager gestioneWSERPManager;

  public SqlManager getSqlManager() {
    return this.sqlManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public GestioneWSERPManager getGestioneWSERPManager() {
    return this.gestioneWSERPManager;
  }

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }


  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    String messaggio = "";
    boolean esito = false;
    JSONObject result = new JSONObject();

    try {

      String url = ConfigManager.getValore(PROP_WSERP_URL);
      ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = new Long(profilo.getId());
      String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, "WSERP");

      String utente = credenziali[0];
      String password = credenziali[1];


      WSERP_ServiceLocator wserp_ServiceLocator = new WSERP_ServiceLocator();
      wserp_ServiceLocator.setWSERPImplPortEndpointAddress(url);

      String tiporda = request.getParameter("tiporda");
      String codicerda = request.getParameter("codicerda");

      WSERPRdaType input = new WSERPRdaType();

      input.setTipoRdaErp(tiporda);
      input.setCodiceRda(codicerda);

      WSERPRdaResType output = wserp_ServiceLocator.getWSERPImplPort().WSERPDettaglioRda(utente, password, input);

      if (output.isEsito()) {
        esito = true;
        messaggio = output.getMessaggio();

        StringWriter xml = new StringWriter();
        JAXBContext jc = JAXBContext.newInstance(WSERPRdaResType.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(new JAXBElement<WSERPRdaResType>(new QName("uri", "local"), WSERPRdaResType.class, output), xml);

        org.json.JSONObject json = XML.toJSONObject(xml.toString());
        org.json.JSONObject xmltojson = json.getJSONObject("ns2:local");

        result.put("xmltojson", xmltojson.toString());

      } else {
        esito = false;
        messaggio = output.getMessaggio();
      }

    } catch (Exception e) {
      esito = false;
      messaggio = e.getMessage();
    }

    if (messaggio == null) {
      messaggio = "";
    }
    result.put("esito", esito);
    result.put("messaggio", messaggio);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();
    out.print(result);
    out.flush();

    return null;
  }

}
