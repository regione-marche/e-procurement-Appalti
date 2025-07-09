package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.DispatchActionAjaxLogged;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.tasks.ChiudiFascicoloManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.maggioli.eldasoft.ws.dm.*;
import net.sf.json.JSONObject;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;

public class WSDMChiudiFascicoloAction extends DispatchActionAjaxLogged {

  private SqlManager sqlManager;

  private GestioneWSDMManager gestioneWSDMManager;

  private ChiudiFascicoloManager chiudiFascicoloManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }
  public void setChiudiFascicoloManager(ChiudiFascicoloManager chiudiFascicoloManager) {
    this.chiudiFascicoloManager = chiudiFascicoloManager;
  }

  public final ActionForward check(final ActionMapping mapping,
                                   final ActionForm form, final HttpServletRequest request,
                                   final HttpServletResponse response) throws GestoreException, SQLException, IOException {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String codicefascicolo = request.getParameter("codicefascicolo");
    String beforeDate = UtilityDate.convertiData(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000), UtilityDate.FORMATO_GG_MM_AAAA);

    JSONObject result = new JSONObject();
    Long ischiuso = (Long) sqlManager.getObject("select count(1) from wsfascicolo where codice=? and ischiuso='1'", new Object[] {codicefascicolo});
    if(ischiuso>0){
      result.put("esito", "KO");
      result.put("errors", "Impossibile procedere alla chiusura del fascicolo: il fascicolo è già chiuso");
    }else {
      result = chiudiFascicoloManager.check(beforeDate, codicefascicolo, false);
    }

    out.println(result);
    out.flush();

    return null;
  }

  public final ActionForward chiudi(final ActionMapping mapping, final ActionForm form,
                                     final HttpServletRequest request,
                                     final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String ruolo = request.getParameter("ruolo");
    String nome = request.getParameter("nome");
    String cognome = request.getParameter("cognome");
    String codiceuo = request.getParameter("codiceuo");
    String idutente = request.getParameter("idutente");
    String idutenteunop = request.getParameter("idutenteunop");
    String codicefascicolo = request.getParameter("codicefascicolo");
    String servizio = request.getParameter("servizio");
    String codiceaoo = request.getParameter("codiceaoonuovo");
    String codiceufficio = request.getParameter("codiceufficionuovo");
    String idconfi = request.getParameter("idconfi");
    String key1= request.getParameter("key1");


    String wsdmLoginComune = ConfigManager.getValore(GestioneWSDMManager.PROP_WSDM_LOGIN_COMUNE + idconfi);
    if ("1".equals(wsdmLoginComune)) {
      String[] credenziali = this.gestioneWSDMManager.recuperaCredenzialiComuni(servizio, idconfi);
      username = credenziali[0];
      password = credenziali[1];
    }

    WSDMOperatoreType wsdmOperatore = new WSDMOperatoreType();
    wsdmOperatore.setNome(nome);
    wsdmOperatore.setCognome(cognome);
    wsdmOperatore.setRuolo(ruolo);
    wsdmOperatore.setCodiceUO(codiceuo);

    WSDMFascicoloChiudiInType wsdmFascicoloChiudiIn = new WSDMFascicoloChiudiInType();
    wsdmFascicoloChiudiIn.setCodiceFascicolo(codicefascicolo);
    wsdmFascicoloChiudiIn.setCustode(wsdmOperatore);

    WSDMFascicoloChiudiResType wsdmFascicoloRes = this.gestioneWSDMManager.WSDMFascicoloChiudi(username, password,
            ruolo, nome, cognome, codiceuo, idutente, idutenteunop, wsdmFascicoloChiudiIn, servizio, codiceaoo, codiceufficio, idconfi);

    if (wsdmFascicoloRes.isEsito()) {
      TransactionStatus status = null;
      try {
        status = this.sqlManager.startTransaction();
        sqlManager.update("update wsfascicolo set ischiuso='1' where codice=?", new Object[]{codicefascicolo});
        this.sqlManager.commitTransaction(status);
      }catch (Exception e) {
        sqlManager.rollbackTransaction(status);
        result.put("esito", "KO");
        result.put("messaggio", "Si è verificato un errore durante la chiusura del fascicolo: " + e);
      }
      result.put("esito", "OK");

      LogEvento logEventoRES = LogEventiUtils.createLogEvento(request);
      logEventoRES.setCodEvento("GA_WSDM_CHIUDI_FASCICOLO");
      logEventoRES.setLivEvento(1);
      logEventoRES.setOggEvento(key1);
      logEventoRES.setDescr("Chiusura fascicolo");
      LogEventiUtils.insertLogEventi(logEventoRES);
    } else {
      String messaggio = wsdmFascicoloRes.getMessaggio();
      result.put("esito", "KO");
      result.put("messaggio", "Si è verificato un errore durante la chiusura del fascicolo: " + messaggio);
    }
    out.println(result);
    out.flush();

    return null;
  }

}
