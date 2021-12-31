/*
 * Created on 19-nov-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore per update dei dati della pagina commissione
 *
 * @author Luca.Giacomazzo
 */
public class GestoreCommissione extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "GARE";
  }

  /* (non-Javadoc)
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#preDelete(org.springframework.transaction.TransactionStatus, it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  /* (non-Javadoc)
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postDelete(it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  /* (non-Javadoc)
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#preInsert(org.springframework.transaction.TransactionStatus, it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  /* (non-Javadoc)
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postInsert(it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  /* (non-Javadoc)
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#preUpdate(org.springframework.transaction.TransactionStatus, it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    DataColumn nGara = datiForm.getColumn("GARE.NGARA");

    DefaultGestoreEntita gestoreTORN = new DefaultGestoreEntita("TORN",
        this.getRequest());

    // Aggiornamento di TORN.MODAST e TORN.MODGAR
    gestoreTORN.update(status, datiForm);

    if(datiForm.isColumn("GARE1.NOTCOMM") && datiForm.isModifiedColumn("GARE1.NOTCOMM")){
      String notcomm = datiForm.getString("GARE1.NOTCOMM");
      String ngara = datiForm.getString("GARE.NGARA");
      String updateGARE1 = "update gare1 set notcomm = ? where ngara = ?";
      try {
        this.sqlManager.update(updateGARE1, new Object[] {notcomm, ngara});
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nell' update di GARE1.NOTCOMM",null, e);
      }
    }
    if(datiForm.isColumn("GARE1.NRICHNOMINAMIT") && datiForm.isModifiedColumn("GARE1.NRICHNOMINAMIT")){
      String nrichnominamit = datiForm.getString("GARE1.NRICHNOMINAMIT");
      String ngara = datiForm.getString("GARE.NGARA");
      String updateGARE1 = "update gare1 set nrichnominamit = ? where ngara = ?";
      try {
        this.sqlManager.update(updateGARE1, new Object[] {nrichnominamit, ngara});
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nell' update di GARE1.NRICHNOMINAMIT",null, e);
      }
    }
    if(datiForm.isColumn("GARE1.DRICHNOMINAMIT") && datiForm.isModifiedColumn("GARE1.DRICHNOMINAMIT")){
      Timestamp drichnominamit = datiForm.getData("GARE1.DRICHNOMINAMIT");
      Date dataRichNominaMIT = new Date(drichnominamit.getTime());
      String ngara = datiForm.getString("GARE.NGARA");
      String updateGARE1 = "update gare1 set drichnominamit = ? where ngara = ?";
      try {
        this.sqlManager.update(updateGARE1, new Object[] {dataRichNominaMIT, ngara});
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nell' update di GARE1.DRICHNOMINAMIT",null, e);
      }
    }
    if(datiForm.isColumn("GARE1.DLETCOM") && datiForm.isModifiedColumn("GARE1.DLETCOM")){
      Timestamp dletcom = datiForm.getData("GARE1.DLETCOM");
      Date dataletcom = new Date(dletcom.getTime());
      String ngara = datiForm.getString("GARE.NGARA");
      String updateGARE1 = "update gare1 set dletcom = ? where ngara = ?";
      try {
        this.sqlManager.update(updateGARE1, new Object[] {dataletcom, ngara});
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nell' update di GARE1.DLETCOM",null, e);
      }
    }
    if(datiForm.isColumn("GARE1.NPLETCOM") && datiForm.isModifiedColumn("GARE1.NPLETCOM")){
      String npletcom = datiForm.getString("GARE1.NPLETCOM");
      String ngara = datiForm.getString("GARE.NGARA");
      String updateGARE1 = "update gare1 set npletcom = ? where ngara = ?";
      try {
        this.sqlManager.update(updateGARE1, new Object[] {npletcom, ngara});
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nell' update di GARE1.NPLETCOM",null, e);
      }
    }

    GestoreGFOFMultiplo.gestisciEntitaDaGARE(this.getRequest(), status,
        datiForm, nGara, this.getServletContext());
    // L'aggiornamento dei dati di GARE viene demandato alla classe
    // AbstractGestoreEntita

    // Gestione dei verbali della commissione
		AbstractGestoreChiaveNumerica gestoreMultiploCOMMVERB = new DefaultGestoreEntitaChiaveNumerica(
				"COMMVERB", "NUM", new String[] { "NGARA" }, this.getRequest());
		this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
				gestoreMultiploCOMMVERB, "COMMVERB",
				new DataColumn[] { datiForm.getColumn("GARE.NGARA") }, null);

  }

  /* (non-Javadoc)
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postUpdate(it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

}