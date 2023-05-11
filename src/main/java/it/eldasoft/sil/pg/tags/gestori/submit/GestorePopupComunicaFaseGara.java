/*
 * Created on 11/10/12
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
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.bl.erp.RaiwayWSERPManager;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per valorizzare il campo GARE.ESINEG
 *
 * @author Marcello.Caminiti
 */
public class GestorePopupComunicaFaseGara extends AbstractGestoreEntita {

  /** Manager Integrazione WSERP */
  private GestioneWSERPManager gestioneWSERPManager;

  public GestorePopupComunicaFaseGara() {
    super(false);
  }

  @Override
  public String getEntita() {
    return "GARE";
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
    String ngara = datiForm.getString("NGARA");
    String codgar = datiForm.getString("CODGARA");
    String isLottoOffDistinte = datiForm.getString("ISLOTTOOFFDISTINTE");
    String isOffertaUnica = datiForm.getString("ISOFFERTAUNICA");

    String isLottoOffunica = datiForm.getString("ISLOTTOOFFUNICA");

    String isOfferteDistinte = datiForm.getString("ISOFFERTEDISTINTE");
    
    String tipoWSERP = "";
    
    WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
    if(configurazione.isEsito()){
  	  tipoWSERP = configurazione.getRemotewserp();
      
    	//inserire la parte relativa ad aggiorna/Procedura
      RaiwayWSERPManager raiwayWSERPManager = (RaiwayWSERPManager) UtilitySpring.getBean("raiwayWSERPManager",
              this.getServletContext(), RaiwayWSERPManager.class);
      HashMap datiMask = new HashMap();
      datiMask.put("codgar", codgar);
      datiMask.put("ngara", ngara);
      int esitoComunicazioneRda = 0;
      WSERPRdaResType wserpRdaRes;
	try {
		wserpRdaRes = raiwayWSERPManager.inviaDatiProcedura(null, null, datiMask);
	      if(!wserpRdaRes.isEsito()){
	          esitoComunicazioneRda++ ;
	            UtilityStruts.addMessage(this.getRequest(), "error",
	                "wserp.erp.aggiornaProceduraERP.error",
	                new Object[] { wserpRdaRes.getMessaggio()});
	      }else{
	          Timestamp ts = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
	          String tsStr = "OK-" + ts.toString();
	      	  this.gestioneWSERPManager.updNumeroRdo(tipoWSERP, codgar, ngara, tsStr);

	      }
	        if(esitoComunicazioneRda > 0){
	            this.getRequest().setAttribute("RISULTATO", "CALCOLOINFO");
	          }else{
	            this.getRequest().setAttribute("RISULTATO", "CALCOLOINFO");
	          }

	} catch (GestoreException e) {
        this.getRequest().setAttribute("RISULTATO", "ERRORI");
        throw e;
	} catch (SQLException sqle) {
        this.getRequest().setAttribute("RISULTATO", "ERRORI");
        throw new GestoreException(
                "ono intercorsi degli errori durante l'aggiornamento della procedura ad ERP",null);
	}

    	
    }


  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
