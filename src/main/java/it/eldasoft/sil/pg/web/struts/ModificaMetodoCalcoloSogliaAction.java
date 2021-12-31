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

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;

import java.io.PrintWriter;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

public class ModificaMetodoCalcoloSogliaAction extends Action {

  static Logger                 logger          = Logger.getLogger(ModificaMetodoCalcoloSogliaAction.class);

  private SqlManager         sqlManager;

  private AggiudicazioneManager aggiudicazioneManager;

  private TabellatiManager tabellatiManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setAggiudicazioneManager(AggiudicazioneManager aggiudicazioneManager) {
    this.aggiudicazioneManager = aggiudicazioneManager;
  }

  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    logger.info("Apertura salvataggio metodo calcolo soglia anomalia: inizio");

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    ResourceBundle resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

    String ngara = request.getParameter("ngara");
    String isGaraDLGS2017 = request.getParameter("isGaraDLGS2017");
    String metsoglia = request.getParameter("metsoglia");
    String metcoeff = request.getParameter("metcoeff");
    Long metsogliaLong = new Long(metsoglia);
    Double metcoeffDouble= null;

    //variabili per tracciatura eventi
    String genericMsgErr = "Errore inaspettato durante la tracciatura su w_logeventi";
    int livEvento = 3;
    String errMsgEvento = genericMsgErr;

    TransactionStatus status = null;
    boolean commitTransaction = false;

    try{
      status = this.sqlManager.startTransaction();

      if(metcoeff!=null && !"".equals(metcoeff))
        metcoeffDouble = new Double(metcoeff);

      String update="update gare1 set metsoglia=?, metcoeff=? where ngara=?";
      this.sqlManager.update(update, new Object[]{metsogliaLong, metcoeffDouble, ngara});

    //best case
      livEvento = 1;
      errMsgEvento = "";
      commitTransaction = true;

    } catch (Exception ge) {
      commitTransaction = false;
      livEvento = 3;
      errMsgEvento = ge.getMessage();
    }finally{
      if (status != null) {
        if (commitTransaction) {
          this.sqlManager.commitTransaction(status);
        } else {
          this.sqlManager.rollbackTransaction(status);
        }
      }
      //Tracciatura eventi
      try {
        String descr="Assegnazione metodo calcolo anomalia ";
        String descrizioneMetsoglia = "";
        if(metsoglia!=null){
          descrizioneMetsoglia = tabellatiManager.getDescrTabellato("A1126", metsoglia.toString());
          descr+="(" + descrizioneMetsoglia;
          if(metcoeffDouble!=null){
            if(metcoeff.indexOf(".")>0)
              metcoeff=metcoeff.replace(".", ",");
            descr += " - " + metcoeff;
          }
          if("true".equals(isGaraDLGS2017))
            descr+=" - adeguato DLgs.56/2017";
          descr+=")";
        }
        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(ngara);
        logEvento.setCodEvento("GA_METODO_ANOMALIA");
        logEvento.setDescr(descr);
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        logger.error(genericMsgErr);
      }

      String esito="ok";
      if(livEvento==3)
        esito="nok";
      result.put("esito", esito);
    }

    out.print(result);
    out.flush();

    logger.info("Apertura salvataggio metodo calcolo soglia anomalia: fine");

    return null;

  }
}
