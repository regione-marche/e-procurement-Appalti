/*
 * Created on 14-12-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per la popup per ricaricare i dati di un lotto di un Adempimento presente in bko.
 *
 * @author Marcello Caminiti
 */
public class GestorePopupAggiungiAppaltoAdempimento extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "ANTICORLOTTI";
  }

  public GestorePopupAggiungiAppaltoAdempimento() {
    super(false);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    Long idAnticor = datiForm.getLong("ANTICOR.ID");
    String codCig = datiForm.getString("CIG");
    Long annorif = null;
    String ufficioIntestatario = null;
    String cfResponsabile = null;
    
    HttpSession session = this.getRequest().getSession();
    if (session != null) {
      ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute("uffint")));
    }

    ProfiloUtente profiloUtente = (ProfiloUtente) session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    if ("U".equals(profiloUtente.getAbilitazioneGare())) {
        cfResponsabile = profiloUtente.getCodiceFiscale().toUpperCase();
    }
    
    try {
      annorif = (Long) this.sqlManager.getObject(
          "select annorif from anticor where id=?",
          new Object[] { idAnticor });

      // Si deve controllare che in anticorlotti non esista già un lotto con
      // uguale CIG per l'anno di riferimento
      Long count = null;
      String codiceErrore = "CIG.dupl";
      String select = "select count(lotti.id) from anticor a, anticorlotti lotti where idanticor=a.id and annorif=? and upper(cig)=?";
      if (StringUtils.isNotEmpty(ufficioIntestatario)) {
        select += " and codein='" + ufficioIntestatario + "'";
        codiceErrore="CIG.duplUff";
      }
      if (StringUtils.isNotEmpty(cfResponsabile)) {
    	  select += " and UPPER(CODFISRESP)='" + cfResponsabile.toUpperCase() + "' ";
      }
      
      count = (Long) this.sqlManager.getObject(select, new Object[] {
          annorif, codCig.toUpperCase() });
      if (count != null && count.longValue() > 0) {
        this.getRequest().setAttribute("erroreOperazione", "1");
        throw new GestoreException("Esiste già un lotto con codice CIG="
            + codCig
            + " per l'anno di riferimento "
            + annorif.toString(), codiceErrore, new Object[] { codCig,
            annorif.toString()}, new Exception());
      }

    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException("Errore nella lettura di ANTICOR.ANNORIF",
          null, e);
    }

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);

    try {
      HashMap<?,?> ret = pgManager.insertLottiAdempimento(annorif, codCig, idAnticor, null, null, ufficioIntestatario, "1",null,true,cfResponsabile);
      String codiciCigDuplicati = (String)ret.get("cigDuplicati");
      if (StringUtils.isNotEmpty(codiciCigDuplicati)) {
        UtilityStruts.addMessage(this.getRequest(), "warning",
            "warnings.adempimenti.CIG.duplicato",
            new Object[] { codiciCigDuplicati });
      }

      String risultato = (String)ret.get("risultato");
      if ("2".equals(risultato)) {
        this.getRequest().setAttribute("LottiNonImportati", "1");
        return;
      }

    } catch(GestoreException e ) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw e;
    }

    this.getRequest().setAttribute("operazioneEseguita", "1");
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

}