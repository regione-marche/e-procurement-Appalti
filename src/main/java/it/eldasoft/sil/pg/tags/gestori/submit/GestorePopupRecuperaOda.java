/*
 * Created on 23/05/24
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPOdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPOdaType;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore della popup recupera ODA
 * delle rda in gara
 *
 * @author Alex Mancini
 */

public class GestorePopupRecuperaOda extends
AbstractGestoreEntita {

  /** Logger */
  static Logger logger = Logger.getLogger(GestorePopupRecuperaOda.class);

  /** Manager Integrazione WSERP */
  private GestioneWSERPManager gestioneWSERPManager;

  @Override
  public String getEntita() {
    return "G1STIPULA";
  }

  public GestorePopupRecuperaOda() {
    super(false);
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    
    gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
        this.getServletContext(), GestioneWSERPManager.class);

    // lettura dei parametri di input
    Long idStipula = datiForm.getLong("ID");
    String codStipula = datiForm.getString("CODSTIPULA");
    String idPadre = datiForm.getString("NGARA");
    String numeroOda = datiForm.getString("NUMODA");
    
    if (logger.isDebugEnabled())
      logger.debug("GestorePopupRecuperaOda: Inizio ");

    try {
      String tipoWSERP = null;
      WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
      if(configurazione.isEsito()){
        tipoWSERP = configurazione.getRemotewserp();
      }

      ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = Long.valueOf(profilo.getId());
      String[] credenziali = gestioneWSERPManager.wserpGetLogin(syscon, "WSERP");

      String username = credenziali[0];
      String password = credenziali[1];

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", this.getServletContext(), SqlManager.class);
      if ("INARCASSA".equals(tipoWSERP)) {
        WSERPOdaType wserpOdaType = new WSERPOdaType();
        wserpOdaType.setCodiceOda(numeroOda);
        WSERPOdaResType wserpOdaResType = gestioneWSERPManager.wserpDettaglioOda(username, password, "WSERP", wserpOdaType);
        if(wserpOdaResType.isEsito()) {
          String aggiornaG1stipula = "update g1stipula set numoda = ? where id = ?";
          sqlManager.update(aggiornaG1stipula,new Object[] { numeroOda, idStipula });
          this.getRequest().setAttribute("ricercaODASuccess", "1");
        } else {          
          throw new Exception(wserpOdaResType.getMessaggio());
        }
      }
    } catch (Exception e) {
      this.getRequest().setAttribute("messageError", e.getMessage());
    }
    if (logger.isDebugEnabled())
      logger.debug("GestorePopupRecuperaOda: Fine");
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}