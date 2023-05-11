/*
 * Created on 28/07/2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.utils.spring.UtilitySpring;

public class EsistonoAcquisizioniOfferteInStatoDaElaborareFunction extends AbstractFunzioneTag {

  public EsistonoAcquisizioniOfferteInStatoDaElaborareFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];
    String tipoOfferta = (String) params[2];
    String valoriStato=(String) params[3];
    String esistonoAcquisizioniOfferteDaElaborare = "false";

    if (ngara != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      MEPAManager mepaManager = (MEPAManager) UtilitySpring.getBean("mepaManager", pageContext, MEPAManager.class);

      //
      TransactionStatus status = null;
      boolean commitTransaction = true;

      try {
        status = sqlManager.startTransaction();

        //gestione esclusione/riammissione buste in stato 5 e 8
        mepaManager.impostaComunicazioniAScartate(ngara,"OFFERTE");
        mepaManager.impostaComunicazioniDaRiaquisire(ngara);
        //gestione esclusione/riammissione buste in stato 13 e 20
        mepaManager.impostaComunicazioniAnonimeAScartate(ngara, "OFFERTE");
        mepaManager.impostaComunicazioniAnonimeDaRiaquisire(ngara);
      } catch (SQLException e) {
        commitTransaction = false;
        throw new JspException("Errore durante l'esclusione/riammissione delle buste in stato 5(13) e 8(20)", e);
      }finally {
        if (status != null) {
          if (commitTransaction) {
            try {
              sqlManager.commitTransaction(status);
            } catch (Exception e) {

            }
          } else {
            try {
              sqlManager.rollbackTransaction(status);

            } catch (Exception e) {

            }
          }
        }
      }

      try {
        String selectW_INVCOM = "select count(*) from w_invcom where comtipo = ? and comstato in (" + valoriStato + ") and comkey2 = ?";
        Long conteggio = (Long) sqlManager.getObject(selectW_INVCOM, new Object[] {tipoOfferta, ngara});

        if (conteggio != null && conteggio.longValue() > 0) {
          esistonoAcquisizioniOfferteDaElaborare = "true";
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo delle procedura telematica", e);
      }
    }

    return esistonoAcquisizioniOfferteDaElaborare;
  }

}