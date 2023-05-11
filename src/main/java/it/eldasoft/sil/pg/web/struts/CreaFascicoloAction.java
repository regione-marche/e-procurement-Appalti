package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.properties.ConfigManager;
import net.sf.json.JSONObject;

public class CreaFascicoloAction extends Action {


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

    JSONObject result = new JSONObject();

    int livEvento =1;
    String codEvento = "GA_WSDM_CREA_FASCICOLO";
    String oggEvento = "";
    String descrEvento = "Creazione fascicolo documentale";
    String errMsgEvento = "";

    try {
      String username = request.getParameter("username");
      String password = request.getParameter("password");
      String ruolo = request.getParameter("ruolo");
      String nome = request.getParameter("nome");
      String cognome = request.getParameter("cognome");
      String codiceuo = request.getParameter("codiceuo");
      String idutente = request.getParameter("idutente");
      String idutenteunop = request.getParameter("idutenteunop");

      String codicefascicolo = request.getParameter("codicefascicolo");
      String oggettofascicolo = request.getParameter("oggettofascicolo");
      String classificafascicolo = request.getParameter("classificafascicolo");
      String classificadescrizione = request.getParameter("classificadescrizione");
      String descrizionefascicolo = request.getParameter("descrizionefascicolo");
      String tipofascicolo = request.getParameter("tipofascicolo");
      String annofascicolo = request.getParameter("annofascicolo");
      String struttura = request.getParameter("struttura");
      String nomeRup = request.getParameter("nomeRup");
      String acronimoRup = request.getParameter("acronimoRup");
      String numeroFascicolo = request.getParameter("numerofascicolo");

      String tipoWSDM = request.getParameter("tipowsdm");
      String entita = request.getParameter("entita");
      String key1 = request.getParameter("key1");
      String idconfi = request.getParameter("idconfi");
      String genereGara = request.getParameter("genereGara");
      String servizio = request.getParameter("servizio");
      String uocompetenza = request.getParameter("uocompetenza");
      String uocompetenzadescrizione = request.getParameter("uocompetenzadescrizione");

      String riservatezzaAttiva = ConfigManager.getValore("wsdm.applicaRiservatezza."+idconfi);
      oggEvento = key1;

      Long isRiservatezza = null;
      if("JIRIDE".equals(tipoWSDM) && "1".equals(riservatezzaAttiva) && !"10".equals(genereGara) && !"11".equals(genereGara) && !"20".equals(genereGara) && !"G1STIPULA".equals(entita)){
        isRiservatezza = new Long(1);
      }

      String messaggio = null;
      if("ITALPROT".equals(tipoWSDM) || "LAPISOPERA".equals(tipoWSDM)) {
        codEvento = "GA_WSDM_ASSOCIA_FASCICOLO";
        descrEvento = "Associazione fascicolo documentale";
        Long annoFascicoloLong=new Long(annofascicolo);
        this.gestioneWSDMManager.setWSFascicolo(entita, key1, null, null, null, codicefascicolo, annoFascicoloLong,
            numeroFascicolo, classificafascicolo, null, null,struttura,null,classificadescrizione,null,null,null);
      }else {
        HashMap<String, Object> parWSDM = new HashMap<String, Object>();
        parWSDM.put(GestioneWSDMManager.LABEL_CLASSIFICA_FASCICOLO, classificafascicolo);
        parWSDM.put(GestioneWSDMManager.LABEL_DESCRIZIONE_FASCICOLO, descrizionefascicolo);
        parWSDM.put(GestioneWSDMManager.LABEL_OGGETTO_FASCICOLO, oggettofascicolo);
        parWSDM.put(GestioneWSDMManager.LABEL_STRUTTURA, struttura);
        parWSDM.put(GestioneWSDMManager.LABEL_TIPO_FASCICOLO, tipofascicolo);
        parWSDM.put(GestioneWSDMManager.LABEL_ACRONIMO_RUP, acronimoRup);
        parWSDM.put(GestioneWSDMManager.LABEL_NOME_RUP, nomeRup);
        parWSDM.put(GestioneWSDMManager.LABEL_USERNAME, username);
        parWSDM.put(GestioneWSDMManager.LABEL_PASSWORD, password);
        parWSDM.put(GestioneWSDMManager.LABEL_RUOLO, ruolo);
        parWSDM.put(GestioneWSDMManager.LABEL_NOME, nome);
        parWSDM.put(GestioneWSDMManager.LABEL_COGNOME, cognome);
        parWSDM.put(GestioneWSDMManager.LABEL_CODICEUO, codiceuo);
        parWSDM.put(GestioneWSDMManager.LABEL_ID_UTENTE, idutente);
        parWSDM.put(GestioneWSDMManager.LABEL_ID_UTENTE_UNITA_OPERATIVA, idutenteunop);
        parWSDM.put(GestioneWSDMManager.LABEL_UOCOMPETENZA, uocompetenza);
        parWSDM.put(GestioneWSDMManager.LABEL_DESCRIZIONE_UOCOMPETENZA, uocompetenzadescrizione);

        messaggio = this.gestioneWSDMManager.setFascicolo(tipoWSDM, servizio, idconfi, entita, key1, isRiservatezza, parWSDM);
      }
      if(messaggio==null || "".equals(messaggio)){
        result.put("esito", "OK");
      }else{
        livEvento =3;
        result.put("esito", "NOK");
        result.put("msg", messaggio);
        errMsgEvento = messaggio;
      }


    } catch (Exception e) {
      livEvento =3;
      errMsgEvento = e.getMessage();
      throw e;
    }finally{
      LogEvento logevento = LogEventiUtils.createLogEvento(request);
      logevento.setLivEvento(livEvento);
      logevento.setOggEvento(oggEvento);
      logevento.setCodEvento(codEvento);
      logevento.setDescr(descrEvento);
      logevento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logevento);
    }

    out.println(result);
    out.flush();

    return null;

  }
}
