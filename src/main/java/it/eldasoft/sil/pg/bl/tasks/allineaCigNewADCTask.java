/*
 * Created on 21/08/2024
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.tasks;

import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.nadc.NewAdcManager;
import it.eldasoft.utils.properties.ConfigManager;

import org.apache.log4j.Logger;

public class allineaCigNewADCTask {

  static Logger           logger = Logger.getLogger(allineaCigNewADCTask.class);
  
  private NewAdcManager      newAdcManager;

  public void setNewAdcManager(NewAdcManager newAdcManager) {
    this.newAdcManager = newAdcManager;
  }

  public void allineaCigNewADC() throws GestoreException {
    if (WebUtilities.isAppNotReady()) return;

    if (logger.isDebugEnabled())
      logger.debug("allineaCigNewADCTask: inizio metodo");

    try {
      String baseUrlNewAdc = ConfigManager.getValore("nadc.rai.ws.baseUrl");
      if(baseUrlNewAdc != null && !baseUrlNewAdc.isEmpty()) {
        newAdcManager.allineaCig();
      } else {
        throw new Exception("La URL di RAI NEW ADC non è configurata.");
      }
    } catch (Exception e) {
      logger.error("Errore durante l'allineamento dei cig PCP " + e.getMessage(), e);
    }
    
    if (logger.isDebugEnabled())
      logger.debug("allineaCigNewADCTask: fine metodo");
  }




}


