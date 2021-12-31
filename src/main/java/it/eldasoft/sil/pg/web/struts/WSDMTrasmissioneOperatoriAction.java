package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.maggioli.eldasoft.ws.dm.WSDMLoginAttrType;
import it.maggioli.eldasoft.ws.dm.WSDMOperatoreType;
import it.maggioli.eldasoft.ws.dm.WSDMTrasmissioneDocumentoType;
import it.maggioli.eldasoft.ws.dm.WSDMTrasmissioneResType;
import it.maggioli.eldasoft.ws.dm.WSDMTrasmissioneUtenteType;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class WSDMTrasmissioneOperatoriAction extends Action {

  private GestioneWSDMManager gestioneWSDMManager;
  private SqlManager sqlManager;

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

    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String servizio = request.getParameter("servizio");
    String ruolo = request.getParameter("ruolo");
    String nome = request.getParameter("nome");
    String cognome = request.getParameter("cognome");
    String codiceUo = request.getParameter("codiceuo");
    String ruoloOp = request.getParameter("ruoloOp");
    String nomeOp = request.getParameter("nomeOp");
    String cognomeOp = request.getParameter("cognomeOp");
    String codiceUoOp = request.getParameter("codiceuoOp");
    String tipoTrasmissione = request.getParameter("tipoTrasmissione");
    String vettIdWsdoc = request.getParameter("vettIdWsdoc");
    String ngara = request.getParameter("ngara");
    String idconfi = request.getParameter("idconfi");

    int livEvento = 1;
    String errMsgEvento = null;
    String msgIdDoc=" - id.doc.:";

    WSDMLoginAttrType loginAttr = new WSDMLoginAttrType();
    loginAttr.setNome(nome);
    loginAttr.setCognome(cognome);
    loginAttr.setRuolo(ruolo);
    loginAttr.setCodiceUO(codiceUo);

    WSDMOperatoreType operatoreType = new WSDMOperatoreType();
    operatoreType.setNome(nomeOp);
    operatoreType.setCognome(cognomeOp);
    operatoreType.setRuolo(ruoloOp);
    operatoreType.setCodiceUO(codiceUoOp);

    try{
      WSDMTrasmissioneUtenteType[] trasmissioneUtente = new WSDMTrasmissioneUtenteType[1];
      trasmissioneUtente[0] = new WSDMTrasmissioneUtenteType();
      trasmissioneUtente[0].setTipoTrasmissione(tipoTrasmissione);
      trasmissioneUtente[0].setOperatore(operatoreType);

      List listaWsdocumento = this.sqlManager.getListVector("select numerodoc from wsdocumento where id in (" + vettIdWsdoc + ")", null);
      if(listaWsdocumento!=null && listaWsdocumento.size()>0){
        WSDMTrasmissioneDocumentoType[] trasmissioneDoc = new WSDMTrasmissioneDocumentoType[listaWsdocumento.size()];
        String numeroDocumento = null;
        for(int i=0; i< listaWsdocumento.size(); i++ ){
          trasmissioneDoc[i] = new WSDMTrasmissioneDocumentoType();
          numeroDocumento = SqlManager.getValueFromVectorParam(listaWsdocumento.get(i), 0).getStringValue();
          if(i>0)
            msgIdDoc += ",";
          msgIdDoc += numeroDocumento ;
          trasmissioneDoc[i].setNumeroDocumento(numeroDocumento);
          trasmissioneDoc[i].setUtenti(trasmissioneUtente);
        }

        WSDMTrasmissioneResType trasmissioneRes = this.gestioneWSDMManager.WSDMTrasmissione(username, password, servizio, loginAttr, trasmissioneDoc, idconfi);
        result.put("esito",trasmissioneRes.isEsito());
        result.put("messaggio",trasmissioneRes.getMessaggio());
        if(!trasmissioneRes.isEsito()){
          livEvento = 3;
          errMsgEvento = trasmissioneRes.getMessaggio();
        }
      }else{
        livEvento = 3;
        errMsgEvento = "Non è stato possibile trovare il numero documento dei documenti selezionati";
        result.put("esito",new Boolean(false));
        result.put("messaggio","Non è stato possibile trovare il numero documento dei documenti selezionati");
      }
    }catch(Exception e){
      livEvento = 3;
      errMsgEvento = e.getMessage();
      throw e;
    }finally{
      String descr ="Trasmissione elemento documentale a operatore interno (operatore '" + nomeOp + ", " + cognomeOp + ", "+ ruoloOp + " , " +  codiceUoOp +  ", " + tipoTrasmissione + "'" + msgIdDoc + ")";
      LogEvento logEvento = LogEventiUtils.createLogEvento(request);
      logEvento.setLivEvento(livEvento);
      logEvento.setOggEvento(ngara);
      logEvento.setCodEvento("GA_WSDM_TRASMETTI_DOC");
      logEvento.setDescr(descr);
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);
    }

    out.print(result);
    out.flush();

    return null;

  }

}
