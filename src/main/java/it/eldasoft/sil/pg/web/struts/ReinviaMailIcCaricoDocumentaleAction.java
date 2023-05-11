package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.dm.WSDMInviaMailResType;
import it.maggioli.eldasoft.ws.dm.WSDMInviaMailType;
import it.maggioli.eldasoft.ws.dm.WSDMMailFormatoType;
import net.sf.json.JSONObject;

public class ReinviaMailIcCaricoDocumentaleAction extends Action {

  private SqlManager          sqlManager;
  private GestioneWSDMManager gestioneWSDMManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

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

    String username = request.getParameter("username");
    String ruolo = request.getParameter("ruolo");
    String password = request.getParameter("password");
    String nome = request.getParameter("nome");
    String cognome = request.getParameter("cognome");
    String codiceuo = request.getParameter("codiceuo");
    String idprg = request.getParameter("idprg");
    String idcom = request.getParameter("idcom");
    String entitaWSDM = request.getParameter("entitaWSDM");
    String chiaveWSDM = request.getParameter("chiaveWSDM");
    String tipoWSDM = request.getParameter("tipowsdm");
    String idconfi = request.getParameter("idconfi");

    //Dati comunicazione
    String selectW_INVCOM = "select comnumprot, commsgtes, commsgtip, committ from w_invcom where idprg = ? and idcom = ?";
    Vector datiW_INVCOM = this.sqlManager.getVector(selectW_INVCOM, new Object[]{idprg,new Long(idcom) });
    if(datiW_INVCOM!=null){
      String numeroProtocollo = SqlManager.getValueFromVectorParam(datiW_INVCOM, 0).getStringValue();
      String commsgtes = SqlManager.getValueFromVectorParam(datiW_INVCOM, 1).getStringValue();
      String commsgtip = SqlManager.getValueFromVectorParam(datiW_INVCOM, 2).getStringValue();
      String committ = SqlManager.getValueFromVectorParam(datiW_INVCOM, 3).getStringValue();

      // Dati documento
      String selectWSDOCUMENTO = "select numerodoc, annoprot, oggetto from wsdocumento where entita=? and key1=? and numeroprot = ?";
      Vector datiWSDOCUMENTO = this.sqlManager.getVector(selectWSDOCUMENTO, new Object[]{entitaWSDM,chiaveWSDM,numeroProtocollo });

      // Destinatari
      String selectW_INVCOMDES = "select desmail, idcomdes from w_invcomdes where idprg = ? and idcom = ? and desstato='5'";
      List<?> datiW_INVCOMDES = this.sqlManager.getListVector(selectW_INVCOMDES, new Object[] { idprg, idcom });

      if(datiWSDOCUMENTO!=null && datiW_INVCOMDES!=null && datiW_INVCOMDES.size() >0){
        String numerodoc =  SqlManager.getValueFromVectorParam(datiWSDOCUMENTO, 0).getStringValue();
        Long annoprot =  SqlManager.getValueFromVectorParam(datiWSDOCUMENTO, 1).longValue();
        String oggettoMail =  SqlManager.getValueFromVectorParam(datiWSDOCUMENTO, 2).getStringValue();
        String statoComunicazione = null;
        String desstato = null;
        TransactionStatus status = null;
        String msgErroreInvioMail = null;
        boolean commitTransaction = false;

        WSDMInviaMailType parametriMailIn = new WSDMInviaMailType();
        parametriMailIn.setNumeroDocumento(numerodoc);
        parametriMailIn.setAnnoProtocollo(annoprot);
        parametriMailIn.setNumeroProtocollo(numeroProtocollo);
        String protocolloMail = numeroProtocollo;
        protocolloMail = UtilityStringhe.convertiNullInStringaVuota(protocolloMail);
        oggettoMail = UtilityStringhe.convertiNullInStringaVuota(oggettoMail);
        if("PALEO".equals(tipoWSDM) || "JIRIDE".equals(tipoWSDM) || "ARCHIFLOW".equals(tipoWSDM) || "EASYDOC".equals(tipoWSDM) || "PRISMA".equals(tipoWSDM) ){
          if("JIRIDE".equals(tipoWSDM)){
            protocolloMail = UtilityStringhe.fillLeft(protocolloMail, '0', 7);
          }
          oggettoMail = "Prot.N." + protocolloMail + "/" + annoprot + " - " + oggettoMail;
        }
        parametriMailIn.setOggettoMail(oggettoMail);
        parametriMailIn.setTestoMail(commsgtes);
        parametriMailIn.setMittenteMail(committ);
        if ("1".equals(commsgtip))
          parametriMailIn.setFormatoMail(WSDMMailFormatoType.HTML);
        else
          parametriMailIn.setFormatoMail(WSDMMailFormatoType.TEXT);


        if ("PALEO".equals(tipoWSDM) || "ARCHIFLOW".equals(tipoWSDM) || "ITALPROT".equals(tipoWSDM)) {
          if("ARCHIFLOW".equals(tipoWSDM) && datiW_INVCOMDES.size() >0){
            //Si deve impostare il vettore dei destinatari
            String destinatari[] = new String[datiW_INVCOMDES.size()];
            for (int i = 0; i < datiW_INVCOMDES.size(); i++) {
              destinatari[i] = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 0).getValue();
            }
            parametriMailIn.setDestinatariMail(destinatari);
          }

          WSDMInviaMailResType wsdmInviaMailResType = this.gestioneWSDMManager.wsdmInviaMail(username, password, ruolo, nome, cognome, codiceuo, null, null, parametriMailIn,idconfi);
          if(wsdmInviaMailResType.isEsito()){
            statoComunicazione = "10";
            desstato = "4";
          }else{
            statoComunicazione = "11";
            desstato = "5";
            msgErroreInvioMail = wsdmInviaMailResType.getMessaggio();
          }

          result.put("esitoInviaMail", wsdmInviaMailResType.isEsito());
          result.put("messaggioInviaMail", msgErroreInvioMail);

        }else if ("JIRIDE".equals(tipoWSDM) || "EASYDOC".equals(tipoWSDM) || "PRISMA".equals(tipoWSDM) || "JPROTOCOL".equals(tipoWSDM) || "ARCHIFLOWFA".equals(tipoWSDM)) {
          //JIRIDE si deve inviare una mail per ogni destinatario
          //anche per EAS_YDOC
          boolean invioMailOk = true;
          if (datiW_INVCOMDES != null && datiW_INVCOMDES.size() > 0) {
            WSDMInviaMailResType wsdmInviaMailResType = null;
            String updateW_INVCOMDES = "update w_invcomdes set desstato = ?, deserrore = ?, desdatinv = ? where idprg = ? and idcom = ? and idcomdes = ? ";

            for (int i = 0; i < datiW_INVCOMDES.size(); i++) {
              String delayPec = ConfigManager.getValore("wsdm.invioMailPec.delay."+idconfi);
              if(!"".equals(delayPec) && delayPec != null && i > 0){
                  TimeUnit.MILLISECONDS.sleep(Integer.parseInt(delayPec));
              }
              String desmail = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 0).getValue();
              Long idcomdes = (Long) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 1).getValue();
              parametriMailIn.setDestinatariMail(new String[]{desmail});
              wsdmInviaMailResType = this.gestioneWSDMManager.wsdmInviaMail(username, password, ruolo, nome, cognome, codiceuo, null, null, parametriMailIn,idconfi);
              if(wsdmInviaMailResType.isEsito()){
                msgErroreInvioMail = null;
                desstato = "4";
              }else{
                invioMailOk = false;
                msgErroreInvioMail = wsdmInviaMailResType.getMessaggio();
                desstato = "5";
              }

              result.put("esitoInviaMail", wsdmInviaMailResType.isEsito());
              result.put("messaggioInviaMail", wsdmInviaMailResType.getMessaggio());

              try {
                status = this.sqlManager.startTransaction();
                this.sqlManager.update(updateW_INVCOMDES, new Object[] {desstato, msgErroreInvioMail,  new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),idprg, idcom, idcomdes });
                commitTransaction = true;
              } catch (Exception e) {
                commitTransaction = false;
              } finally {
                if (status != null) {
                  if (commitTransaction) {
                    this.sqlManager.commitTransaction(status);
                  } else {
                    this.sqlManager.rollbackTransaction(status);
                  }
                }
              }
            }
            if(invioMailOk)
              statoComunicazione = "10";
            else
              statoComunicazione = "11";
          }
        }

        try {
          status = this.sqlManager.startTransaction();
          String updateW_INVCOM = "update w_invcom set comstato = ? where idprg = ? and idcom = ?";
          this.sqlManager.update(updateW_INVCOM, new Object[]{statoComunicazione, idprg, idcom });

          if("PALEO".equals(tipoWSDM) || "ARCHIFLOW".equals(tipoWSDM) || "ARCHIFLOWFA".equals(tipoWSDM) || "ITALPROT".equals(tipoWSDM)){
            String updateW_INVCOMDES = "update w_invcomdes set desstato = ?, deserrore =?, desdatinv = ? where idprg = ? and idcom = ?";
            this.sqlManager.update(updateW_INVCOMDES, new Object[] {desstato, msgErroreInvioMail,  new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),idprg, idcom });
          }
          commitTransaction = true;
        } catch (Exception e) {
          commitTransaction = false;
        } finally {
          if (status != null) {
            if (commitTransaction) {
              this.sqlManager.commitTransaction(status);
            } else {
              this.sqlManager.rollbackTransaction(status);
            }
          }
        }
      }else if(datiW_INVCOMDES!=null && datiW_INVCOMDES.size() >0){
        result.put("esitoInviaMail", "NoDestinatari");
      }
    }



    out.print(result);
    out.flush();

    return null;

  }

}
