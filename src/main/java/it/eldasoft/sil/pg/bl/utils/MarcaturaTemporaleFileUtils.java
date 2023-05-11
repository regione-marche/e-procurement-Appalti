/*
 * Created on 4 giu 2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.utils;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.properties.ConfigManager;
import it.maggioli.eldasoft.digitaltimestamp.beans.DigitalTimeStampInfo;
import it.maggioli.eldasoft.digitaltimestamp.beans.DigitalTimeStampResult;
import it.maggioli.eldasoft.digitaltimestamp.client.DigitalTimeStampClient;

public class MarcaturaTemporaleFileUtils {
  static Logger logger = Logger.getLogger(MarcaturaTemporaleFileUtils.class);

  public static HashMap<String,Object> creaMarcaTemporale(byte[] file, Long idcom, String comkey1, String entita, HttpServletRequest request) {
    HashMap<String,Object> ret = new HashMap<String,Object>();
    String urlAccesso = ConfigManager.getValore("marcaturaTemp.url");
    String urlProvider = ConfigManager.getValore("marcaturaTemp.provider.url");
    String tipo = ConfigManager.getValore("marcaturaTemp.provider.tipo");
    String username = ConfigManager.getValore("marcaturaTemp.provider.username");
    String password = ConfigManager.getValore("marcaturaTemp.provider.password");

    int livEvento = 1;
    String codEvento = "INVCOM_MARCATEMP";
    String oggEvento = idcom.toString();
    String descrEvento = "Marcatura temporale della comunicazione (rif. " + entita + " - " + comkey1 + ")";
    String errMsgEvento = "";

    try {
      DigitalTimeStampInfo parametri = new DigitalTimeStampInfo();
      parametri.setUrlServizio(urlAccesso);
      parametri.setProviderUrl(urlProvider);
      parametri.setProviderUsername(username);
      parametri.setProviderPassword(password);
      parametri.setProviderType(tipo);
      DigitalTimeStampClient client = new DigitalTimeStampClient(parametri );
      String filePath = StrutsUtilities.getTempDir() + File.separatorChar + UUID.randomUUID().toString();
      DigitalTimeStampResult res = client.digitalTimeStampFile(file, filePath);
      if(res.getResult()) {
        ret.put("file", res.getFile());
        ret.put("esito","OK");
        //pdf = res.getFile();
        //nomeAllegato+=".tsd";
        //descrizioneAllegato += " con marcatura temporale";
      }else {
        livEvento = 3;
        errMsgEvento = res.getErrorMessage();
        ret.put("esito","NOK");
        String messaggio ="Errore in fase di marcatura temporale. ErrorCode = " + res.getErrorCode() + ". ErrorMessage=" + errMsgEvento;
        logger.error(messaggio);
      }
    }catch (Exception e) {
      livEvento = 3;
      errMsgEvento = e.getMessage();
      ret.put("esito","NOK");
      logger.error(e.getMessage());
    }finally {
      LogEvento logEvento = null;
      if(request!=null)
        logEvento = LogEventiUtils.createLogEvento(request);
      else {
        logEvento = new LogEvento();
        logEvento.setCodApplicazione("PG");
      }
      logEvento.setLivEvento(livEvento);
      logEvento.setOggEvento(oggEvento);
      logEvento.setCodEvento(codEvento);
      logEvento.setDescr(descrEvento);
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);
    }
    return ret;
  }


}

