package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPPosizioneRdaType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;
import net.sf.json.JSONObject;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class SetWSERPPosRdaInGaraAction extends Action {

    private GestioneWSERPManager gestioneWSERPManager;

    public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
        this.gestioneWSERPManager = gestioneWSERPManager;
    }

    @Override public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception {
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
        String codiceRda = request.getParameter("codiceRda");
        String codice = request.getParameter("codice");
        String arrmultikey = request.getParameter("arrmultikey");
        arrmultikey = UtilityStringhe.convertiNullInStringaVuota(arrmultikey);
        String linkrda = request.getParameter("linkrda");
        linkrda = UtilityStringhe.convertiNullInStringaVuota(linkrda);
        String uffint = request.getParameter("uffint");
        ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        Long syscon = new Long(profilo.getId());
        String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);

        String username = credenziali[0];
        String password = credenziali[1];

        WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi(servizio);
        String tipoWSERP = configurazione.getRemotewserp();
        if("RAIWAY".equals(tipoWSERP)){
            WSERPRdaType erpSearch= new WSERPRdaType();
            erpSearch.setCodiceRda(codiceRda);
            WSERPRdaResType wserpRdaRes = this.gestioneWSERPManager.wserpListaRda(username, password, servizio, erpSearch);
            if(!wserpRdaRes.isEsito()){
                throw new GestoreException("Si e' verificato un errore durante la associazione delle RdA: " + wserpRdaRes.getMessaggio(),
                    "wserp.erp.associarda.remote.error", null);
            }else{
                WSERPRdaType[] rdaArray = wserpRdaRes.getRdaArray();
                if(!"".equals(arrmultikey)){
                    String[] multikey = arrmultikey.split(";");
                    int index = 0;
                    WSERPPosizioneRdaType[] posizioneArray = new WSERPPosizioneRdaType[multikey.length];
                    for (String mkey : multikey) {
                        boolean found = false;
                        for (int i = 0; i < rdaArray[0].getPosizioneRdaArray().length && !found ; i++) {
                            if (rdaArray[0].getPosizioneRdaArray(i).getPosizioneRiferimento().equals(mkey)){
                                posizioneArray[index] = rdaArray[0].getPosizioneRdaArray(i);
                                found = true;
                                index ++;
                            }
                        }
                    }
                    rdaArray[0].setPosizioneRdaArray(posizioneArray);
                    this.gestioneWSERPManager.insCollegamentoRda(request, username, password, servizio, codiceGara, codice, rdaArray, linkrda, uffint);
                    result.put("Esito", "0");
                }
            }
        }
        out.print(result);
        out.flush();
        return null;
    }
}
