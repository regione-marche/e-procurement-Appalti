package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPAllegatoType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;

import java.io.PrintWriter;
import java.util.ArrayList;
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

public class GetWSERPListaAllegatiRdaAction extends Action {

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

    String codice = request.getParameter("codice");
    String genere = request.getParameter("genere");

    JSONObject result = new JSONObject();

    int totalAllegati = 0;
    int totalAllegatiRda = 0;
    int totalAfterFilterAllegatiRda = 0;
    List<HashMap<String, Object>> hMapAllegatiRda = new ArrayList<HashMap<String, Object>>();
    WSERPRdaResType wserpRdaRes = new WSERPRdaResType();

    String tipoWSERP =null;
    WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
    if(configurazione.isEsito()){
      tipoWSERP = configurazione.getRemotewserp();
      tipoWSERP = UtilityStringhe.convertiNullInStringaVuota(tipoWSERP);
    }

      ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = new Long(profilo.getId());
      String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);

      String username = credenziali[0];
      String password = credenziali[1];
      String selRdaGara = null;

      Long countRda = new Long(0);

      String linkrda = "";

      WSERPRdaType[] rdaArray= null;

      if("1".equals(genere) || "3".equals(genere)){
        if("AVM".equals(tipoWSERP) || "UGOVPA".equals(tipoWSERP)){
          countRda = (Long) sqlManager.getObject(
              "select count(*) from garerda where codgar = ?",new Object[] { codice });
        }

        if("AVM".equals(tipoWSERP)){
          if(new Long(0).equals(countRda)){
            linkrda = "2";
            selRdaGara = "select distinct(codrda) from gcap" +
            " where ngara in (select ngara from gare where codgar1 = ?) order by codrda";
          }else{
            linkrda = "1";
            selRdaGara = "select distinct(numrda) from garerda" +
            " where codgar = ? order by numrda";
          }
          List<Vector> rdaGara = sqlManager.getListVector(selRdaGara, new Object[] { codice });
          rdaArray= new WSERPRdaType[rdaGara.size()];
          if(rdaGara.size() > 0){
            for (int i = 0; i < rdaGara.size(); i++) {
              String codRda = (String) SqlManager.getValueFromVectorParam(rdaGara.get(i), 0).getValue();
              WSERPRdaType rda = new WSERPRdaType();
              rda.setCodiceRda(codRda);
              rda.setDescrizione("Doc. rda: " + codRda);
              rdaArray[i] = rda;
            }
          }

        }else{
          if(new Long(0).equals(countRda)){
            linkrda = "2";
            selRdaGara = "select codrda,posrda,codcarr,codvoc from gcap" +
            " where ngara in (select ngara from gare where codgar1 = ?) order by codcarr,codrda,posrda";
          }else{
            linkrda = "1";
            selRdaGara = "select numrda,posrda from garerda" +
            " where codgar = ? order by numrda,posrda";
          }
          List<Vector> rdaGara = sqlManager.getListVector(selRdaGara, new Object[] { codice });
          rdaArray= new WSERPRdaType[rdaGara.size()];
          if(rdaGara.size() > 0){
            for (int i = 0; i < rdaGara.size(); i++) {
              String codRda = (String) SqlManager.getValueFromVectorParam(rdaGara.get(i), 0).getValue();
              String posRda = (String) SqlManager.getValueFromVectorParam(rdaGara.get(i), 1).getValue();

              WSERPRdaType rda = new WSERPRdaType();
              rda.setCodiceRda(codRda);
              rda.setPosizioneRda(posRda);
              if("2".equals(linkrda)){
                String codCarr = (String) SqlManager.getValueFromVectorParam(rdaGara.get(i), 2).getValue();
                rda.setCodiceCarrello(codCarr);
                String codvoc = (String) SqlManager.getValueFromVectorParam(rdaGara.get(i), 3).getValue();
                rda.setDescrizione(codRda + "/" + posRda + " - " + codvoc);
              }else{
                rda.setDescrizione(codRda + "/" + posRda);
              }

              rdaArray[i] = rda;
            }
          }

        }

      }else{
        if("AVM".equals(tipoWSERP) || "UGOVPA".equals(tipoWSERP)){
          countRda = (Long) sqlManager.getObject(
              "select count(*) from garerda where codgar in (select codgar1 from gare where ngara = ?)",new Object[] { codice });
        }
        
        if("AMIU".equals(tipoWSERP)){
            countRda = (Long) sqlManager.getObject(
                "select count(*) from garerda where codgar = ?",new Object[] { codice });
        }


        if("AVM".equals(tipoWSERP)){
          if(new Long(0).equals(countRda)){
            linkrda = "2";
            selRdaGara = "select distinct(codrda) from gcap where ngara = ? order by codrda";
          }else{
            linkrda = "1";
            selRdaGara = "select distinct(numrda) from garerda" +
            " where codgar in (select codgar1 from gare where ngara = ?) order by numrda";
          }

          List<Vector> rdaGara = sqlManager.getListVector(selRdaGara, new Object[] { codice });
          rdaArray= new WSERPRdaType[rdaGara.size()];
          if(rdaGara.size() > 0){
            for (int i = 0; i < rdaGara.size(); i++) {
              String codRda = (String) SqlManager.getValueFromVectorParam(rdaGara.get(i), 0).getValue();
              WSERPRdaType rda = new WSERPRdaType();
              rda.setCodiceRda(codRda);
              rda.setDescrizione("Doc. rda: " + codRda);
              rdaArray[i] = rda;
            }
          }

        }else{
          if(new Long(0).equals(countRda)){
            linkrda = "2";
            selRdaGara = "select codrda,posrda,codcarr,codvoc from gcap where ngara = ? order by codcarr,codrda,posrda";
          }else{
            linkrda = "1";
            if("AMIU".equals(tipoWSERP)){
                selRdaGara = "select numrda,posrda from garerda where codgar = ? order by numrda,posrda";
            }else {
                selRdaGara = "select numrda,posrda from garerda" +
                        " where codgar in (select codgar1 from gare where ngara = ?) order by numrda,posrda";
            }
          }

          List<Vector> rdaGara = sqlManager.getListVector(selRdaGara, new Object[] { codice });
          rdaArray= new WSERPRdaType[rdaGara.size()];
          if(rdaGara.size() > 0){
            for (int i = 0; i < rdaGara.size(); i++) {
              String codRda = (String) SqlManager.getValueFromVectorParam(rdaGara.get(i), 0).getValue();
              String posRda = (String) SqlManager.getValueFromVectorParam(rdaGara.get(i), 1).getValue();

              WSERPRdaType rda = new WSERPRdaType();
              rda.setCodiceRda(codRda);
              rda.setPosizioneRda(posRda);
              if("2".equals(linkrda)){
                String codCarr = (String) SqlManager.getValueFromVectorParam(rdaGara.get(i), 2).getValue();
                rda.setCodiceCarrello(codCarr);
                String codvoc = (String) SqlManager.getValueFromVectorParam(rdaGara.get(i), 3).getValue();
                rda.setDescrizione(codRda + "/" + posRda + " - " + codvoc);
              }else{
                rda.setDescrizione(codRda + "/" + posRda);
              }

              rdaArray[i] = rda;
            }
          }

        }

      }

      wserpRdaRes  = this.gestioneWSERPManager.wserpListaFilesRda(username, password, servizio, rdaArray);

      //riformulare esito qui
    result.put("esito", wserpRdaRes.isEsito());
    result.put("messaggio", wserpRdaRes.getMessaggio());



    if (wserpRdaRes.isEsito()) {
      if (wserpRdaRes.getRdaArray()!= null) {
          WSERPRdaType[] rdaFilesArray = wserpRdaRes.getRdaArray();
          for (int r = 0; r < rdaFilesArray.length; r++) {
            WSERPRdaType rda = rdaFilesArray[r];
            String codiceCarrello = rda.getCodiceCarrello();
            String codiceRda = rda.getCodiceRda();
            String descrRda = rda.getDescrizione();
            if(rda.getAllegatoArray() != null){
              WSERPAllegatoType[] allegatiArray = rda.getAllegatoArray();
              for (int a = 0; a < allegatiArray.length; a++) {
                HashMap<String, Object> hMap = new HashMap<String, Object>();
                if(codiceRda!= null && allegatiArray[a] != null){
                	if("AMIU".equals(tipoWSERP)){
                        hMap.put("titoloallegato", codiceRda);
                        hMap.put("tipoallegato", allegatiArray[a].getTipo());
                        hMap.put("nomeallegato", allegatiArray[a].getNome());
                        hMap.put("idfile", allegatiArray[a].getPath());
                	}else {
                        hMap.put("codiceCarrello", codiceCarrello);
                        hMap.put("codiceRda", codiceRda);
                        hMap.put("descrRda", descrRda);
                        hMap.put("titolo", allegatiArray[a].getTitolo());
                        hMap.put("nome", allegatiArray[a].getNome());
                        hMap.put("tipo", allegatiArray[a].getTipo());
                        hMap.put("path", allegatiArray[a].getPath());
                	}
                  hMapAllegatiRda.add(hMap);
                }

              }
            }
          }

      }
    }

    if (hMapAllegatiRda != null && hMapAllegatiRda.size() > 0) {
      totalAllegatiRda = hMapAllegatiRda.size();
      totalAfterFilterAllegatiRda = hMapAllegatiRda.size();
    }

    if("AMIU".equals(tipoWSERP)) {
        result.put("iTotalRecordsALLEGATI", totalAllegatiRda);
        result.put("iTotalDisplayRecordsALLEGATI", totalAfterFilterAllegatiRda);
        result.put("dataALLEGATI", hMapAllegatiRda);
    }else {
        result.put("iTotalRecords", totalAllegatiRda);
        result.put("iTotalDisplayRecords", totalAfterFilterAllegatiRda);
        result.put("data", hMapAllegatiRda);
    }


    out.print(result);
    out.flush();

    return null;

  }
}

