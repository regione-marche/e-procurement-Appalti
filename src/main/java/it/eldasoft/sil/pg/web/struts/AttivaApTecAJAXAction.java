/*
 * Created on 12/06/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.sicurezza.DatoBase64;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import net.sf.json.JSONObject;

public class AttivaApTecAJAXAction extends Action {

  static Logger       logger = Logger.getLogger(AttivaApTecAJAXAction.class);

  private MEPAManager mepaManager;

  private PgManager             pgManager;

  private GeneManager         geneManager;

  private SqlManager          sqlManager;

  public void setmepaManager(MEPAManager mepaManager) {
    this.mepaManager = mepaManager;
  }

  public void setpgManager(PgManager pgManager) {
    this.pgManager = pgManager;
  }

  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    logger.info("Apertura busta tecnica: inizio");

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    ResourceBundle resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

    JSONObject result = new JSONObject();
    String ngara = request.getParameter("ngara");
    String dittao = request.getParameter("dittao");
    String password = request.getParameter("password");
    String pswCryptata = request.getParameter("pswCryptata");
    String comtipo = "FS11B";
    Long fasgarSuccessivo = new Long(5);
    Long stepgarSuccessivo = new Long(50);
    Long numeroAcquisizioni = new Long(0);
    Long numeroAcquisizioniErrore = new Long(0);
    Long numeroAcquisizioniScartate = new Long(0);
    boolean autorizzatoModifica = true;
    String sez = request.getParameter("sez");

    //variabili per tracciatura eventi
    String genericMsgErr = "Errore inaspettato durante la tracciatura su w_logeventi";
    int livEvento = 3;
    String errMsgEvento = genericMsgErr;

    String messaggioPerLog = "";
    boolean erroreGestito = false;
    String codEventoInit = "GA_APERTURA_BUSTA_TEC_INIT";
    String descrInit = "Inizio apertura busta tecnica";
    if ("1".equals(sez)) {
      codEventoInit = "GA_APERTURA_BUSTA_TQUALI_INIT";
      descrInit = "Inizio apertura buste tecniche sezione qualitativa";
      comtipo = "FS11B-QL";
    } else if ("2".equals(sez)) {
      codEventoInit = "GA_APERTURA_BUSTA_TQUANTI_INIT";
      descrInit = "Inizio apertura buste tecniche sezione quantitativa";
      comtipo = "FS11B-QN";
    }

    try{
      //Tracciatura eventi
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setLivEvento(1);
        logEvento.setOggEvento(ngara);
        logEvento.setCodEvento(codEventoInit);
        logEvento.setDescr(descrInit);
        logEvento.setErrmsg("");
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        logger.error(genericMsgErr);
      }

      String messageKey="";

      //Controllo che da profilo sia abilitata l'acquisizione di acquisizione, che l'utente abbia i permessi di modifica
      //e che la fase della gara sia quella attesa

      Long fasgar = (Long) sqlManager.getObject("select fasgar from gare where ngara = ?", new Object[] { ngara });

      if (!this.geneManager.getProfili().checkProtec(
          (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS",
          "ALT.GARE.FASIGARA.AttivaAperturaTecniche") || !this.pgManager.getAutorizzatoModificaGara("V_GARE_TORN",
              "CODGAR", ngara, false, "2", request)
          || (this.mepaManager.esisteBloccoCondizioniFasiAcquisizioni(ngara, fasgarSuccessivo) && fasgar!=null )) {
        erroreGestito=true;
        messageKey ="errors.gestoreException.*.acquisizione.BustaTec.DatiIncosistenti";
        messaggioPerLog = resBundleGenerale.getString(messageKey);
        logger.error(messaggioPerLog);
        result.put("Esito", "Errore");
        result.put("MsgErrore", messaggioPerLog);
        errMsgEvento = messaggioPerLog;
      }


      try{
		  if(pswCryptata.equals("true")){
			DatoBase64 base64 = new DatoBase64(password,DatoBase64.FORMATO_BASE64);
			ICriptazioneByte criptatore = FactoryCriptazioneByte.getInstance(
				FactoryCriptazioneByte.CODICE_CRIPTAZIONE_ADVANCED, base64.getByteArrayDatoAscii(),
				ICriptazioneByte.FORMATO_DATO_CIFRATO);
			base64 = new DatoBase64(criptatore.getDatoNonCifrato(),
				DatoBase64.FORMATO_ASCII);
			password = new String(criptatore.getDatoNonCifrato());
			password = password.substring(password.indexOf("_")+1, password.lastIndexOf("_"));
		  }
      }catch(Exception e) {
        result.put("Esito", "ErrorePwdNonCorretta");
        livEvento = 3;
        errMsgEvento = "Password inserita non corretta";
        result.put("MsgErrore", errMsgEvento);
      }

      if(!erroreGestito){
        HashMap<String, Object> hMap = mepaManager.aperturaDocumentazioneProcedureTelematiche(ngara, comtipo, dittao, fasgarSuccessivo,
            stepgarSuccessivo, password, request);
          numeroAcquisizioni = (Long) hMap.get("numeroAcquisizioni");
          numeroAcquisizioniErrore = (Long) hMap.get("numeroAcquisizioniErrore");
          numeroAcquisizioniScartate = (Long) hMap.get("numeroAcquisizioniScartate");
          request.getSession().setAttribute("passBusteB", password);


        //best case
          livEvento = 1;
          errMsgEvento = "";
      }



    } catch (GestoreException ge) {
      livEvento = 3;
      errMsgEvento = ge.getMessage();
      if (errMsgEvento.indexOf("Password inserita non corretta") >= 0)
        result.put("Esito", "ErrorePwdNonCorretta");
      else
        result.put("Esito", "Errore");
      result.put("MsgErrore", errMsgEvento);
      logger.error(messaggioPerLog);
    } finally {
      //Tracciatura eventi
      try {
        String codEventoFine = "GA_APERTURA_BUSTA_TEC_FINE";
        String descrFine = "Fine apertura busta tecnica: ";
        if ("1".equals(sez)) {
          codEventoFine = "GA_APERTURA_BUSTA_TQUALI_FINE";
          descrFine = "Fine apertura busta tecnica sezione qualitativa: ";
        } else if ("2".equals(sez)) {
          codEventoFine = "GA_APERTURA_BUSTA_TQUANTI_FINE";
          descrFine = "Fine apertura busta tecnica sezione quantitativa: ";
        }
        descrFine += numeroAcquisizioni + " buste aperte, "
            + numeroAcquisizioniErrore + " buste con errore, "
            + numeroAcquisizioniScartate + " buste non aperte perché appartenenti a ditte escluse dalla gara, "
            + "utente con diritto sulla modifica della gara:" + (new Boolean(autorizzatoModifica)).toString();

        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(ngara);
        logEvento.setCodEvento(codEventoFine);
        logEvento.setDescr(descrFine);
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        logger.error(genericMsgErr);
      }

      logger.info("Apertura busta tecnica (gara "+ngara+"): numero acquisizioni " + numeroAcquisizioni);
      logger.info("Apertura busta tecnica (gara "+ngara+"): numero acquisizioni con errore " + numeroAcquisizioniErrore);
      logger.info("Apertura busta tecnica (gara "+ngara+"): non acquisite perchè escluse dalla gara " + numeroAcquisizioniScartate);

      result.put("ngara", ngara);
      result.put("dittao", dittao);
      result.put("numeroAcquisizioni", numeroAcquisizioni);
      result.put("numeroAcquisizioniErrore", numeroAcquisizioniErrore);
      result.put("numeroAcquisizioniScartate", numeroAcquisizioniScartate);
      result.put("autorizzatoModifica", (new Boolean(autorizzatoModifica)).toString());
    }

    out.print(result);
    out.flush();

    logger.info("Apertura busta tecnica: fine");

    return null;
  }
}
