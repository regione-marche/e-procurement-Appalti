/*
 * Created on 26/06/18
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class EsistonoComunicazioniFS10_FS11_Stato9Function extends AbstractFunzioneTag {

  public EsistonoComunicazioniFS10_FS11_Stato9Function() {
    super(2, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];

    boolean esistonoComunicazioniFs10 = false;
    boolean esistonoComunicazioniFs11 = false;

    if (ngara != null) {
      PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1", pageContext, PgManagerEst1.class);
      MEPAManager mepaManager = (MEPAManager) UtilitySpring.getBean("mepaManager", pageContext, MEPAManager.class);
      String esitoControllo [] =null;
      String campoData="";
      String campoOra="";
      //Si deve fare il controllo sullo stato 9 solo se sono scaduti i termini
      try {
        //FS10
        campoData="DTEPAR";
        campoOra="OTEPAR";
        esitoControllo = mepaManager.controlloDataConDataAttuale(ngara, campoData, campoOra);
        if(esitoControllo[0]=="true"){
          esistonoComunicazioniFs10 = pgManagerEst1.esistonoComunicazioni(ngara, "FS10","9");
          if(esistonoComunicazioniFs10) {
            UtilityStruts.addMessage(this.getRequest(), "error",
                "error.busteFS10DaProtocollare",null);
            return "";
          }
        }

        //Fs11
        campoData="DTEOFF";
        campoOra="OTEOFF";
        esitoControllo = mepaManager.controlloDataConDataAttuale(ngara, campoData, campoOra);
        if(esitoControllo[0]=="true"){
          esistonoComunicazioniFs11 = pgManagerEst1.esistonoComunicazioni(ngara, "FS11","9");
          if(esistonoComunicazioniFs11)
            UtilityStruts.addMessage(this.getRequest(), "error",
                "error.busteFS11DaProtocollare",null);
        }
      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo della presenza di buste da protocollare", e);
      }
    }

    return "";
  }

}