package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPAllegatoType;
import it.maggioli.eldasoft.ws.erp.WSERPOdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPOdaType;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetWSERPListaAllegatiOdaAction extends Action {

  private GestioneWSERPManager gestioneWSERPManager;

  private SqlManager sqlManager;

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
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

    String servizio = request.getParameter("servizio");
    if(servizio==null || "".equals(servizio))
      servizio ="WSERP";

    JSONObject result = new JSONObject();

    int totalAllegati = 0;
    int totalAllegatiRda = 0;
    int totalAfterFilterAllegatiRda = 0;
    List<HashMap<String, Object>> hMapAllegatiRda = new ArrayList<HashMap<String, Object>>();
    WSERPOdaResType wserpOdaRes = new WSERPOdaResType();

    String tipoWSERP =null;
    WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
    if(configurazione.isEsito()){
      tipoWSERP = configurazione.getRemotewserp();
      tipoWSERP = UtilityStringhe.convertiNullInStringaVuota(tipoWSERP);
    }
    
    if("INARCASSA".equals(tipoWSERP)) {
      Long id = Long.valueOf(request.getParameter("id"));
    

      ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = new Long(profilo.getId());
      String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);

      String username = credenziali[0];
      String password = credenziali[1];
      String selRdaGara = null;

      Long countRda = Long.valueOf(0);

      String linkrda = "";

      WSERPOdaType oda = new WSERPOdaType();
      WSERPOdaType[] odaArray = null;
      
      String datiDaG1stupila = "select ngara, numoda, codstipula from g1stipula where id = ? ";
      Vector<?> getDatiDaG1stupila = sqlManager.getVector(datiDaG1stupila, new Object[] { id });
      if(getDatiDaG1stupila!=null && !getDatiDaG1stupila.isEmpty()){
        String ngara = (String) SqlManager.getValueFromVectorParam(getDatiDaG1stupila, 0).getValue();
        String numoda = (String) SqlManager.getValueFromVectorParam(getDatiDaG1stupila, 1).getValue();
        String codstipula = (String) SqlManager.getValueFromVectorParam(getDatiDaG1stupila, 2).getValue();
        
        Long genere = (Long) sqlManager.getObject("select genere from v_gare_genere where codice = ?", new Object[] { ngara });
      
        oda.setCodiceOda(numoda);
        if("INARCASSA".equals(tipoWSERP) && numoda != null) {
          odaArray = new WSERPOdaType[1];
          odaArray[0] = oda;
          wserpOdaRes  = this.gestioneWSERPManager.wserpListaFilesOda(username, password, servizio, odaArray);
          
        }
      }
    } else if("CONI".equals(tipoWSERP)) {

      String codgar = request.getParameter("id");
    

      ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = new Long(profilo.getId());
      String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);

      String username = credenziali[0];
      String password = credenziali[1];
      
      
      String datiDaGare1 = "select numrdo from gare1 where codgar1 = ? ";
      String numOda = (String) sqlManager.getObject(datiDaGare1, new Object[] { codgar });
      List<String> listaOda = new ArrayList<>();
      if(numOda!=null && !numOda.isEmpty()){
        //Caso rda multipla es. 
        //  rda1-oda1;rda2-oda2
        //1000000031-2000000081;1000000040-2000000082
        if(numOda.contains(";")) {
          String[] rdoMultiple = numOda.split(";");
          listaOda.addAll(Arrays.asList(rdoMultiple));
        } else { //Caso con gara con una sola rda-oda 1000000031-2000000081
          listaOda.add(numOda);
        }

        List<WSERPOdaType> wserpOdaTypes = new ArrayList<>();
        for (int i = 0; i < listaOda.size(); i++) {
          String numoda = listaOda.get(i);
          WSERPOdaType oda = new WSERPOdaType();
          WSERPOdaType[] odaArray = null;
          oda.setCodiceOda(numoda.split("-")[1]);
          //oda.setCodiceRda(numoda.split("-")[0]);
          //Per CONI non usiamo il codice RDA per risalire all'ODA quindi per la successiva chiamata al download 
          // setto il codice ODA all'interno del codice RDA altrimenti, al download devo farmi la query 
          // per estrarre il numrdo da gare1 e ciclarmi il risultato splittato per trovarmi il codice ODA corrispondente al codice RDA
          // quindi per evitare questo giro, dato che il codice RDA in questo caso non viene usato lo sovrascrivo con il codice ODA
          oda.setCodiceRda(numoda.split("-")[1]);
          if(!"KO".equals(oda.getCodiceOda())) {
              odaArray = new WSERPOdaType[1];
              odaArray[0] = oda;
              WSERPOdaResType wserpOdaReslist = this.gestioneWSERPManager.wserpListaFilesOda(username, password, servizio, odaArray);
              if(wserpOdaReslist != null && wserpOdaReslist.isEsito() 
                  && wserpOdaReslist.getOdaArray() != null  && wserpOdaReslist.getOdaArray().length > 0) {
                wserpOdaTypes.addAll(Arrays.asList(wserpOdaReslist.getOdaArray()));
              }
          }
        }
        wserpOdaRes.setEsito(true);
        wserpOdaRes.setOdaArray(wserpOdaTypes.toArray(new WSERPOdaType[0]));
        
      }
    
    }

      //riformulare esito qui
    result.put("esito", wserpOdaRes.isEsito());
    result.put("messaggio", wserpOdaRes.getMessaggio());

    if (wserpOdaRes.isEsito()) {
      if (wserpOdaRes.getOdaArray()!= null) {
          WSERPOdaType[] odaFilesArray = wserpOdaRes.getOdaArray();
          for (int r = 0; r < odaFilesArray.length; r++) {
            WSERPOdaType odaType = odaFilesArray[r];
            String codiceCarrello = odaType.getCodiceCarrello();
            String codiceRda = odaType.getCodiceRda();
            String descrRda = odaType.getDescrizione();
            if(odaType.getAllegatoArray() != null){
              WSERPAllegatoType[] allegatiArray = odaType.getAllegatoArray();
              for (int a = 0; a < allegatiArray.length; a++) {
                HashMap<String, Object> hMap = new HashMap<String, Object>();
                if(codiceRda!= null && allegatiArray[a] != null){
                  hMap.put("titoloallegato", codiceRda);
                  hMap.put("tipoallegato", allegatiArray[a].getTipo());
                  hMap.put("nomeallegato", allegatiArray[a].getNome());
                  hMap.put("idfile", allegatiArray[a].getPath());

                  hMapAllegatiRda.add(hMap);
                }

              }
            }
          }

      }
    }

    if (hMapAllegatiRda != null && !hMapAllegatiRda.isEmpty()) {
      totalAllegatiRda = hMapAllegatiRda.size();
      totalAfterFilterAllegatiRda = hMapAllegatiRda.size();
    }

   
    result.put("iTotalRecordsALLEGATI", totalAllegatiRda);
    result.put("iTotalDisplayRecordsALLEGATI", totalAfterFilterAllegatiRda);
    result.put("dataALLEGATI", hMapAllegatiRda);
    
    
    out.print(result);
    out.flush();

    return null;

  }
}

