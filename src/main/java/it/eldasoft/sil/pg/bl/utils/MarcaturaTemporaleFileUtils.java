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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.properties.ConfigManager;
import it.maggioli.eldasoft.digitaltimestamp.client.EDigitalTimestamp;
import it.maggioli.eldasoft.digitaltimestamp.client.IDigitalTimestamp;
import it.maggioli.eldasoft.digitaltimestamp.model.ITimeStampResult;
import it.maggioli.eldasoft.digitaltimestamp.model.info.BaseTimeStampInfo;
import it.maggioli.eldasoft.digitaltimestamp.model.info.MaggioliTimeStampInfo;

public class MarcaturaTemporaleFileUtils {
  static Logger logger = Logger.getLogger(MarcaturaTemporaleFileUtils.class);

  public static HashMap<String,Object> creaMarcaTemporale(byte[] file, Long idcom, String comkey1, String entita, HttpServletRequest request) {
    HashMap<String,Object> ret = new HashMap<String,Object>();
    String urlAccesso = ConfigManager.getValore("marcaturaTemp.url");
    String urlProvider = ConfigManager.getValore("marcaturaTemp.provider.url");
    String tipo = ConfigManager.getValore("marcaturaTemp.provider.tipo");
    String username = ConfigManager.getValore("marcaturaTemp.provider.username");
    String password = ConfigManager.getValore("marcaturaTemp.provider.password");
    
    String tipoProcedimentoMarcatura = StringUtils.stripToEmpty(ConfigManager.getValore("marcaturaTemp.tipologia"));
    
    String kongAuthUrl = ConfigManager.getValore("keycloak.realms.baseurl")+ConfigManager.getValore("keycloak.progettosegreteria.subpath");
    String kongAuthClientId = ConfigManager.getValore("keycloak.progettosegreteria.client.id");
    String kongAuthClientSecret = ConfigManager.getValore("keycloak.progettosegreteria.client.secret");

    int livEvento = 1;
    String codEvento = "INVCOM_MARCATEMP";
    String oggEvento = idcom.toString();
    String descrEvento = "Marcatura temporale della comunicazione (rif. " + entita + " - " + comkey1 + ")";
    String errMsgEvento = "";

    try {

      BaseTimeStampInfo baseInfo = BaseTimeStampInfo.DigitalTimeStampInfoBuilder.newDigitalTimeStampInfo()
          .withProviderPassword(password)
          .withProviderType(tipo)
          .withProviderUrl(urlProvider)
          .withProviderUsername(username)
          .withUrlServizio(urlAccesso)
          .build();
      IDigitalTimestamp client = null;
      // 3 == vecchio, 1 == nuovo (auth kong), 2 == blockchain?
      if ("3".equals(tipoProcedimentoMarcatura) || "".equals(tipoProcedimentoMarcatura)) {
        client = EDigitalTimestamp.MAGGIOLI_OLD(baseInfo);
      } else if("1".equals(tipoProcedimentoMarcatura)) {
        client = EDigitalTimestamp.MAGGIOLI(
            MaggioliTimeStampInfo.DigitalTimeStampInfoBuilder.newDigitalTimeStampInfo()
            .withBaseInfo(baseInfo)
            .withAuthUrl(kongAuthUrl)
            .withAuthClientId(kongAuthClientId)
            .withAuthClientSecret(kongAuthClientSecret)
            .withAuthType(2)
            .build()
            );
      } else {
        throw new RuntimeException("The Provider '" + tipoProcedimentoMarcatura + "' is not supported yet"); 
      }

      String filePath = StrutsUtilities.getTempDir() + File.separatorChar + UUID.randomUUID().toString();
      ITimeStampResult timeStampResult = client.getTimestampedFile(file, filePath);
      
      if(timeStampResult.getResult()) {
        ret.put("file", timeStampResult.getFile());
        ret.put("esito","OK");
      }else {
        livEvento = 3;
        errMsgEvento = timeStampResult.getErrorMessage();
        ret.put("esito","NOK");
        String messaggio ="Errore in fase di marcatura temporale. ErrorCode = " + timeStampResult.getErrorCode() + ". ErrorMessage=" + errMsgEvento;
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

