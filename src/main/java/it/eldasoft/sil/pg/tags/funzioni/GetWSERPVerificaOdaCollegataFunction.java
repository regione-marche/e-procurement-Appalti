/*
 * Created on 04-01-2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPOdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPOdaType;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che controlla se un affidamento derivato ha un ordine di acquisto collegato
 *
 * @author Alex Mancini
 */
public class GetWSERPVerificaOdaCollegataFunction extends AbstractFunzioneTag {

  public GetWSERPVerificaOdaCollegataFunction() {
    super(2 , new Class[] { PageContext.class, String.class});
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    /*STATI ESISTEODACOLLEGATA:
    *   1: ESITO OK CON UN RISULTATO POSITIVO DALLA CHIAMATA SI_ODA (NON POSSO CANCELLARE LA GARA)
    *   2: ESITO OK MA CON RISULTATO NEGATIVO DELLA CHIAMATA NO_ODA (POSSO CANCELLARE LA GARA)
    *   3: ESITO KO LA CHIAMATA E' ANDATA IN ERRORE (NON POSSO CANCELLARE LA GARA)
    *   4: NON E' PRESENTE NESSUN CIG (POSSO CANCELLARE LA GARA)
    */
	  
	String codice = (String)params[1];
    String esisteOdaCollegata= "4";

    try {
      String tipoWSERP = null;
      GestioneWSERPManager gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager", pageContext, GestioneWSERPManager.class);
      WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
      if(configurazione.isEsito()){
        tipoWSERP = configurazione.getRemotewserp();
      }

      ProfiloUtente profilo = (ProfiloUtente) pageContext.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = Long.valueOf(profilo.getId());
      String[] credenziali = gestioneWSERPManager.wserpGetLogin(syscon, "WSERP");

      String username = credenziali[0];
      String password = credenziali[1];

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      if ("ATAC".equals(tipoWSERP) && codice.indexOf("$")>=0) {
    	  String ngara = codice.substring(1);
          Vector<?> datiAffidamentoDerivato = null;
          String selAffidamentoDerivato = "SELECT CODCIG,SEGUEN FROM GARE WHERE NGARA = ?";
          datiAffidamentoDerivato = sqlManager.getVector(selAffidamentoDerivato, new Object[] { ngara });
          if (datiAffidamentoDerivato != null && datiAffidamentoDerivato.size() > 0) {
              String codcig = (String) SqlManager.getValueFromVectorParam(datiAffidamentoDerivato, 0).getValue();
              codcig = UtilityStringhe.convertiNullInStringaVuota(codcig);
              String seguen = (String) SqlManager.getValueFromVectorParam(datiAffidamentoDerivato, 1).getValue();
              seguen = UtilityStringhe.convertiNullInStringaVuota(seguen);
              if (!"".equals(codcig) && !"".equals(seguen)) {
            	  String selPresRda = "select count(*) from gcap where ngara = ? and codrda is not null";
            	  Long resPresRda = (Long) sqlManager.getObject(selPresRda, new Object[] { ngara });
            	  if (resPresRda != null && resPresRda > Long.valueOf(0)) {
                      WSERPOdaType wserpOdaType = new WSERPOdaType();
                      wserpOdaType.setCodiceCig(codcig);
                      WSERPOdaResType wserpOdaResType = gestioneWSERPManager.wserpDettaglioOda(username, password, "WSERP", wserpOdaType);
                      if(wserpOdaResType.isEsito()) {
                        if(wserpOdaResType.getMessaggio().equals("SI_ODA")) {
                          esisteOdaCollegata = "1";
                        } else {
                          esisteOdaCollegata = "2";
                        }
                      } else {
                        esisteOdaCollegata = "3";
                      }
            	  }
              }
          }
      }
      
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura dei dati della gara", e);       
    } catch (GestoreException e) {
      throw new JspException("Errore durante il recupero delle credenziali per il sistema remoto", e);      
    }
    return esisteOdaCollegata;
  }

}
