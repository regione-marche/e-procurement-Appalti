package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.maggioli.eldasoft.ws.dm.WSDMAccountEmailType;
import it.maggioli.eldasoft.ws.dm.WSDMListaAccountEmailResType;
import it.maggioli.eldasoft.ws.dm.WSDMListaUfficiResType;
import it.maggioli.eldasoft.ws.dm.WSDMUfficioType;
import net.sf.json.JSONObject;

public class GetWSDMListaUfficiAction extends Action {
  private static final String PROP_WSDM_JIRIDE_INDIRIZZO_MITTENTE = "indirizzomittente";
  private static final String PROP_WSDM_JIRIDE_MITTENTE_INTERNO   = "mittenteinterno";
  private static final String PROP_WSDM_JIRIDE_STURTTURA          = "struttura";
  private static final String PROP_WSDM_TITULUS_UFFICI            = "uffici";

  private GestioneWSDMManager gestioneWSDMManager;
  private SqlManager          sqlManager;

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

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

    JSONObject result = new JSONObject();
    int total = 0;
    List<HashMap<String, Object>> hMap = new ArrayList<HashMap<String,Object>>();

    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String servizio = request.getParameter("servizio");
    String codiceAoo = request.getParameter("codiceaoo");
    String ruolo = request.getParameter("ruolo");
    String idconfi = request.getParameter("idconfi");
    String idprofiloutente = request.getParameter("idprofiloutente");
    String tipo = request.getParameter("tipo");
    String gara = request.getParameter("gara");

    if(username!=null && !"".equals(username)){

      // Filtro in funzione della tipologia di utente.
      // Se l'utente e' un responsabile (accede a tutte le commesse) non si
      // deve applicare alcun filtro ma si devono ricavare tutti i valori ad
      // esso associato.
      // Se l'utente NON e' un responsabile bisogna inviare, al servizio, il
      // nome dell'utente che servira' come filtro per ricavare gli uffici
      // (mittenteinterno) e le caselle di posta in gestione
      // (indirizzomittente).
      String utente = null;
      String sysabg = null;
      if(PROP_WSDM_JIRIDE_INDIRIZZO_MITTENTE.equals(tipo) || PROP_WSDM_JIRIDE_MITTENTE_INTERNO.equals(tipo) || PROP_WSDM_JIRIDE_STURTTURA.equals(tipo)) {
        sysabg = (String) sqlManager.getObject("select sysabg from usrsys where syscon = ?", new Object[] { idprofiloutente });
        if (!"A".equals(sysabg)) {
          utente = (String) sqlManager.getObject("select syslogin from usrsys where syscon = ?", new Object[] { idprofiloutente });
        }
      }

      WSDMListaUfficiResType wsdmUfficiRes = null;
      if (PROP_WSDM_JIRIDE_MITTENTE_INTERNO.equals(tipo) || PROP_WSDM_TITULUS_UFFICI.equals(tipo) || PROP_WSDM_JIRIDE_STURTTURA.equals(tipo)) {
        wsdmUfficiRes = this.gestioneWSDMManager.wsdmGetListaUffici(username, password, servizio,codiceAoo, ruolo,null, utente,idconfi);
        if (wsdmUfficiRes.isEsito()) {
          if (wsdmUfficiRes.getListaUffici() != null) {

            WSDMUfficioType[] listaUffici = wsdmUfficiRes.getListaUffici();
              if (listaUffici != null && listaUffici.length > 0) {
                total = listaUffici.length;
                for (int a = 0; a < listaUffici.length; a++) {
                  HashMap<String, Object> hMapUfficio = new HashMap<String, Object>();
                  hMapUfficio.put("codice", listaUffici[a].getCodiceUfficio());
                  hMapUfficio.put("descrizione", listaUffici[a].getDescrizioneUfficio());
                  hMap.add(hMapUfficio);
                }
              }

            }
        }
        result.put("esito",wsdmUfficiRes.isEsito());
        result.put("messaggio",wsdmUfficiRes.getMessaggio());

      } else {
        // Indirizzo mittente, si ricava la lista degli account email

        // 12/03/2021, se e' richiesto il controllo avanzato email e
        // l'utente applicativo e' "standard" ossia non amministratore
        // si deve applicare il filtro aggiuntivo secondo questa regola
        // controllando l'indirizzo email dell'ufficio intestatario (UFFINT)
        // collegato alla gara (CODGAR) secondo questa regola:
        // 1. se l'indirizzo email PEC (UFFINT_EMAI2IN) e' "null", non fare
        // nulla, non serve applicare alcun ulteriore filtro,
        // 2. se l'indirizzo email PEC (UFFINT_EMAI2IN) e' valorizzato ed e'
        // compreso tra quelle restituiti dal servizio, utilizzare solo
        // quello
        // 3. se l'indirizzo email PEC (UFFINT_EMAI2IN) e' valorizzato ma
        // NON e' compreso tra quelli restituiti dal servizio, bloccare
        // l'utente

        String emailControllo = null;
        String controllopec = ConfigManager.getValore("wsdm.tabellatiJiride.pecUffint." + idconfi);

        if (!"A".equals(sysabg)) {
          if (controllopec != null && "1".equals(controllopec)) {
            String emai2in = (String) sqlManager.getObject(
                "select emai2in from uffint where codein in (select cenint from torn where codgar = ?)", new Object[] { gara });
            if (emai2in != null && !"".equals(emai2in.trim())) {
              emailControllo = emai2in.trim();
            }
          }
        }

        WSDMListaAccountEmailResType wsdmListaAccountEmailRes = gestioneWSDMManager.wsdmListaAccountEmail(username,
            password, servizio, ruolo, null, null, null, null, null, utente, emailControllo, idconfi);

        if (wsdmListaAccountEmailRes != null) {
          if (wsdmListaAccountEmailRes.isEsito()) {
            WSDMAccountEmailType[] listaAccountEmail = wsdmListaAccountEmailRes.getListaAccountEmail();
            if (listaAccountEmail != null && listaAccountEmail.length > 0) {
              for (int e = 0; e < listaAccountEmail.length; e++) {
                HashMap<String, Object> hMapUfficio = new HashMap<String, Object>();
                hMapUfficio.put("codice", listaAccountEmail[e].getEmailAddress());
                hMapUfficio.put("descrizione", listaAccountEmail[e].getDes() + " (" + listaAccountEmail[e].getEmailAddress() + ")");
                hMap.add(hMapUfficio);
              }
            }
          }
        }
        result.put("esito",wsdmListaAccountEmailRes.isEsito());
        result.put("messaggio",wsdmListaAccountEmailRes.getMessaggio());

      }

      result.put("iTotalRecords", total);
      result.put("data", hMap);
    } else {
      result.put("esito",false);
      result.put("messaggio", "NoUtente");
      result.put("iTotalRecords", new Long(0));
      result.put("data", null);
    }

    out.print(result);
    out.flush();

    return null;

  }

}
