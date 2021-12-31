package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class SetWSERPRdaInGaraAction extends Action {

  private GestioneWSERPManager gestioneWSERPManager;

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

    JSONObject result = new JSONObject();

    String servizio = request.getParameter("servizio");
    if(servizio==null || "".equals(servizio)){
      servizio ="WSERP";
    }

    String codiceGara = request.getParameter("codgar");
    String codiceLotto = request.getParameter("codice");
    //Nel caso di SmeUp la key e' IDLOTTO, negli altri casi e' rda
    String key = request.getParameter("key");
    String key0 = request.getParameter("key0");
    String key1 = request.getParameter("key1");
    String key2 = request.getParameter("key2");
    String arrmultikey = request.getParameter("arrmultikey");
    arrmultikey = UtilityStringhe.convertiNullInStringaVuota(arrmultikey);
    String linkrda = request.getParameter("linkrda");
    linkrda = UtilityStringhe.convertiNullInStringaVuota(linkrda);
    String uffint = request.getParameter("uffint");

    String codiceCarrello = null;
    String codiceRda = null;
    String posizioneRda = null;
    String esercizio = null;

    //Se tutto va bene con il metodo ERP (per OGNI RDA)
    //INSERISCO IN LAVORAZIONI

    ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long syscon = new Long(profilo.getId());
    String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);

    String username = credenziali[0];
    String password = credenziali[1];


    WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi(servizio);
    String tipoWSERP = configurazione.getRemotewserp();
    if("SMEUP".equals(tipoWSERP) || "UGOVPA".equals(tipoWSERP)){
      codiceCarrello =  key0;
      codiceRda =  key1;
      posizioneRda =  key2;
      codiceCarrello = UtilityStringhe.convertiNullInStringaVuota(codiceCarrello);
      codiceRda = UtilityStringhe.convertiNullInStringaVuota(codiceRda);

      if(!"".equals(codiceCarrello) || !"".equals(codiceRda) ){
        WSERPRdaType erpSearch= new WSERPRdaType();
        if (!"".equals(codiceCarrello)){//SmeUp e U-Gov
          //devo fare la chiamata filtrata
          erpSearch.setCodiceCarrello(codiceCarrello);

          if("UGOVPA".equals(tipoWSERP)){
            if(!"".equals(uffint)){
              erpSearch.setDivisione(uffint);
            }
          }
        }//if carrello

        WSERPRdaResType wserpRdaRes = this.gestioneWSERPManager.wserpListaRda(username, password, servizio, erpSearch);
        if(!wserpRdaRes.isEsito()){
          throw new GestoreException("Si e' verificato un errore durante la associazione delle RdA: " + wserpRdaRes.getMessaggio(),
              "wserp.erp.associarda.remote.error", null);
        }else{
          WSERPRdaType[] rdaArray = wserpRdaRes.getRdaArray();
          wserpRdaRes = this.gestioneWSERPManager.wserpAssociaRdaGara(username, password, servizio, codiceCarrello, codiceRda, posizioneRda, codiceLotto, true);
          if(!wserpRdaRes.isEsito()){
            throw new GestoreException("Si e' verificato un errore durante la associazione delle RdA: " + wserpRdaRes.getMessaggio(),
                "wserp.erp.associarda.remote.error", null);
          }else{
            //effettuo il caricamento delle lavorazioni o il popolamento nei dati gen
            this.gestioneWSERPManager.insCollegamentoRda(request, username, password, servizio, codiceGara, codiceLotto, rdaArray, linkrda, uffint);
          }
        }//if esito
      }//if carrello

    }else{
      if(!"".equals(arrmultikey)){
        String[] multikey = arrmultikey.split(";");
        for (int k = 0; k < multikey.length; k++) {
          String mkey = multikey[k];
          String[] mkeys = mkey.split("_");
          codiceRda =  mkeys[0];
          posizioneRda =  mkeys[1];
          esercizio =  mkeys[1];
          if(!"".equals(codiceCarrello) || !"".equals(codiceRda) ){
            WSERPRdaType erpSearch= new WSERPRdaType();
            if (codiceRda != null){
              //AVM
              if("AVM".equals(tipoWSERP)){
                erpSearch.setCodiceRda(codiceRda);
                erpSearch.setPosizioneRda(posizioneRda);
                codiceLotto = codiceLotto;
              }
              if("FNM".equals(tipoWSERP) ){
                erpSearch.setCodiceRda(codiceRda);
                erpSearch.setEsercizio(esercizio);
                codiceLotto = codiceRda + "-" + esercizio;
                codiceCarrello = esercizio;
              }


            }

            WSERPRdaResType wserpRdaRes = this.gestioneWSERPManager.wserpListaRda(username, password, servizio, erpSearch);
            if(!wserpRdaRes.isEsito()){
              throw new GestoreException("Si e' verificato un errore durante la associazione delle RdA: " + wserpRdaRes.getMessaggio(),
                  "wserp.erp.associarda.remote.error", null);
            }else{
              WSERPRdaType[] rdaArray = wserpRdaRes.getRdaArray();
              if(!"FNM".equals(tipoWSERP)){
                wserpRdaRes = this.gestioneWSERPManager.wserpAssociaRdaGara(username, password, servizio, codiceCarrello, codiceRda, posizioneRda, codiceLotto, true);
                if(!wserpRdaRes.isEsito()){
                  if("AVM".equals(tipoWSERP)){
                    result.put("Esito", "2");
                    result.put("MsgErrore", wserpRdaRes.getMessaggio());
                  }else{
                    throw new GestoreException("Si e' verificato un errore durante la associazione delle RdA: " + wserpRdaRes.getMessaggio(),
                        "wserp.erp.associarda.remote.error", null);
                  }
                }else{
                  if("AVM".equals(tipoWSERP)){
                    //effettuo il caricamento delle lavorazioni o il popolamento nei dati gen
                    this.gestioneWSERPManager.insCollegamentoRda(request, username, password, servizio, codiceGara, codiceLotto, rdaArray, linkrda, uffint);
                    result.put("Esito", "0");
                  }
                }
              }else{
                //FNM recupero i dati basilari della tornata per il lotto

                String tipgarStr = request.getParameter("tipgar");
                tipgarStr = UtilityStringhe.convertiNullInStringaVuota(tipgarStr);
                Long tipgar = new Long(tipgarStr);

                //CONTROLLARE BENE QUI
                String tipoAppaltoStr = request.getParameter("tipoAppalto");
                tipoAppaltoStr = UtilityStringhe.convertiNullInStringaVuota(tipoAppaltoStr);
                Long tipoAppalto = new Long(tipoAppaltoStr);

                //effettuo il caricamento delle lavorazioni o il popolamento nei dati gen
                String[] resInsLotti = this.gestioneWSERPManager.insLottiDaProcedimenti(request, username, password, servizio, codiceGara, tipoAppalto, tipgar, rdaArray, linkrda, uffint);

                  if("0".equals(resInsLotti[0])){
                    result.put("Esito", "0");
                  }else{
                    result.put("Esito", "2");
                    result.put("MsgErrore", resInsLotti[1]);
                  }
              }

            }

          }//||
        }//for
      }//multikey

    }//tipo AVM...






    out.print(result);
    out.flush();

    return null;

  }
}

