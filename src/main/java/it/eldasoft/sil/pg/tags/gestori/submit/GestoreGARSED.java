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

import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

/**
 * Gestore per update dei dati della pagina sedute di gara
 *
 * @author Luca.Giacomazzo
 */
public class GestoreGARSED extends AbstractGestoreChiaveNumerica {

	@Override
  public String[] getAltriCampiChiave() {
		return new String[] { "NGARA" };
	}

	@Override
  public String getCampoNumericoChiave() {
		return "NUMSED";
	}

	@Override
  public String getEntita() {
		return "GARSED";
	}

	@Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
		JdbcParametro nGara = datiForm.getColumn("GARSED.NGARA").getValue();
		JdbcParametro numeroSeduta = datiForm.getColumn("GARSED.NUMSED")
				.getValue();

		// Delete delle entita figlie di GARSED: cancellazione delle persone
		// presenti alla i-esima seduta di gara
		try {
			this.getSqlManager().update(
					"delete from PERP where NGARA = ? and NUMSED = ?",
					new Object[] { nGara.getValue(), numeroSeduta.getValue() });
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nella cancellazione delle persone "
							+ "presenti alla seduta "
							+ numeroSeduta.getStringValue() + "della gara "
							+ nGara.getValue(), null, e);
		}

		// Delete delle entita figlie di GARSED: cancellazione delle persone
		// presenti alla i-esima seduta di gara
		try {
			this.getSqlManager().update(
					"delete from GARSEDSOSP where NGARA = ? and NUMSED = ?",
					new Object[] { nGara.getValue(), numeroSeduta.getValue() });
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nella cancellazione delle sospensioni "
							+ "della seduta "
							+ numeroSeduta.getStringValue() + "della gara "
							+ nGara.getValue(), null, e);
		}

		//Delete dell' entita figlia GARSEDPRES
		try {
          this.getSqlManager().update(
                  "delete from GARSEDPRES where NGARA = ? and NUMSED = ?",
                  new Object[] { nGara.getValue(), numeroSeduta.getValue() });
      } catch (SQLException e) {
          throw new GestoreException(
                  "Errore nella cancellazione dei componenti della commissione di gara presenti nella seduta "
                          + numeroSeduta.getStringValue() + "della gara "
                          + nGara.getValue(), null, e);
      }

		// La cancellazione dell'occorrenza in GARSED viene lasciata
		// alla classe AbstractGestoreEntita
	}

	@Override
  public void postDelete(DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
		super.preInsert(status, datiForm);

		DataColumn nGara = datiForm.getColumn("GARSED.NGARA");
		DataColumn numeroSeduta = datiForm.getColumn("GARSED.NUMSED");

		GestorePERPMultiplo.gestisciEntitaDaGARSED(this.getRequest(), status,
				datiForm, nGara, numeroSeduta);
		// L'aggiornamento dei dati di GARSED viene demandato
		// alla classe AbstractGestoreEntita

		AbstractGestoreChiaveNumerica gestoreMultiploGARSEDSOSP = new DefaultGestoreEntitaChiaveNumerica(
				"GARSEDSOSP", "NUMSOSP", new String[] { "NGARA", "NUMSED" },
				this.getRequest());
		this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
				gestoreMultiploGARSEDSOSP, "GARSEDSOSP", new DataColumn[] {
						datiForm.getColumn("GARSED.NGARA"),
						datiForm.getColumn("GARSED.NUMSED") }, null);

	}

	@Override
  public void postInsert(DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

		DataColumn nGara = datiForm.getColumn("GARSED.NGARA");
		DataColumn numeroSeduta = datiForm.getColumn("GARSED.NUMSED");

		GestorePERPMultiplo.gestisciEntitaDaGARSED(this.getRequest(), status,
				datiForm, nGara, numeroSeduta);
		// L'aggiornamento dei dati di GARSED viene demandato
		// alla classe AbstractGestoreEntita

		AbstractGestoreChiaveNumerica gestoreMultiploGARSEDSOSP = new DefaultGestoreEntitaChiaveNumerica(
				"GARSEDSOSP", "NUMSOSP", new String[] { "NGARA", "NUMSED" },
				this.getRequest());
		this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
				gestoreMultiploGARSEDSOSP, "GARSEDSOSP", new DataColumn[] {
						datiForm.getColumn("GARSED.NGARA"),
						datiForm.getColumn("GARSED.NUMSED") }, null);

	}

	@Override
  public void postUpdate(DataColumnContainer datiForm)
			throws GestoreException {
	}

}