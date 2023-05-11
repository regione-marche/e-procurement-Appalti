/*
 * Created on 17-mag-2021
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
import java.util.HashMap;
import java.util.List;

import org.apache.struts.upload.FormFile;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.web.struts.UploadMultiploForm;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Gestore per update dei dati della pagina documentazione di contratto
 *
 * @author Riccardo.peruzzo
 */
public class GestoreG1DOCUMOD extends AbstractGestoreChiaveIDAutoincrementante {

	private static final Object[] Boolean = null;
	@Override
  public String getCampoNumericoChiave() {
		return "ID";
	}

	@Override
  public String getEntita() {
		return "G1DOCUMOD";
	}

	@Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
		Long id = datiForm.getLong("G1DOCUMOD.ID");
		
        try {

            List listaW_DOCDIG = sqlManager.getListVector("select Iddocdg,idprg from G1ARCDOCUMOD where iddocumod = ? ", new Object[]{id});
            if(listaW_DOCDIG!=null && listaW_DOCDIG.size()>0){

              for(int i=0;i<listaW_DOCDIG.size();i++){
                Long iddocdig = (Long)SqlManager.getValueFromVectorParam(listaW_DOCDIG.get(i), 0).getValue();
                String idprg = (String)SqlManager.getValueFromVectorParam(listaW_DOCDIG.get(i), 1).getValue();
                //delete
                String query = " delete from W_DOCDIG where IDPRG = ? AND IDDOCDIG = ? ";
                sqlManager.update(query, new Object[]{idprg,iddocdig});
              }
            }
          } catch (SQLException e) {
            throw new GestoreException("Errore nella lettura degli allegati (W_DOCDIG)", null);
          }
		
		
	}

	@Override
  public void postDelete(DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
		super.preInsert(status, datiForm);
	}

	@Override
	public void afterInsertEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
		Long id = datiForm.getLong("G1DOCUMOD.ID");
		
	    int numeroRecord = datiForm.getLong("NUMERO_G1ARCDOCUMOD").intValue();
	    for (int i = 1; i <= numeroRecord; i++) {
	      if(datiForm.isColumn("MOD_G1ARCDOCUMOD_" + i) && "1".equals(datiForm.getString("MOD_G1ARCDOCUMOD_" + i))){
	        datiForm.addColumn("G1ARCDOCUMOD.IDDOCUMOD_" + i, JdbcParametro.TIPO_NUMERICO, id);
	      }
	    }
		
		AbstractGestoreChiaveIDAutoincrementante gestoreMultiploG1ARCDOCUMOD = new DefaultGestoreEntitaChiaveIDAutoincrementante(
		          "G1ARCDOCUMOD", "ID", this.getRequest());

		 this.gestisciAggiornamentiRecordSchedaMultiplaG1ARCDOCUMOD(status, datiForm,
		          gestoreMultiploG1ARCDOCUMOD);
	}

	@Override
  public void postInsert(DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
		
		AbstractGestoreChiaveIDAutoincrementante gestoreMultiploG1ARCDOCUMOD = new DefaultGestoreEntitaChiaveIDAutoincrementante(
		          "G1ARCDOCUMOD", "ID", this.getRequest());

		 this.gestisciAggiornamentiRecordSchedaMultiplaG1ARCDOCUMOD(status, datiForm,
		          gestoreMultiploG1ARCDOCUMOD);

	}

	@Override
  public void postUpdate(DataColumnContainer datiForm)
			throws GestoreException {
	}
	/**
	   * Gestione multischeda G1ARCDOCUMOD.
	   *
	   * @param status
	   * @param datiForm
	   * @throws GestoreException
	   */
	  private void gestisciAggiornamentiRecordSchedaMultiplaG1ARCDOCUMOD(TransactionStatus status, DataColumnContainer datiForm,
			  AbstractGestoreChiaveIDAutoincrementante gestoreMultiploG1ARCDOCUMOD)
	      throws GestoreException {

	    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", this.getServletContext(),
	        TabellatiManager.class);

	    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1", this.getServletContext(),
	        PgManagerEst1.class);

	    String nomeCampoNumeroRecord = "NUMERO_G1ARCDOCUMOD";
	    String nomeCampoDelete = "DEL_G1ARCDOCUMOD";
	    String nomeCampoMod = "MOD_G1ARCDOCUMOD";

	    if (datiForm.isColumn(nomeCampoNumeroRecord)) {
	      DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(datiForm.getColumns("G1ARCDOCUMOD", 0));

	      int numeroRecord = datiForm.getLong(nomeCampoNumeroRecord).intValue();
	      
	   // *** Controllo dimensione massima dei file in UPLOAD
	      HashMap<?, ?> hm = ((UploadMultiploForm) this.getForm()).getFormFiles();
	      
	      boolean[] filePres = new boolean[numeroRecord];
	      
	      long dimensioneTotale = 0;
	      FormFile ff = null;
	      for (int i = 1; i <= numeroRecord; i++) {
	        ff = (FormFile) hm.get(new Long(i));
	        if (ff != null && ff.getFileSize() > 0) {
	        	dimensioneTotale += ff.getFileSize();
	        	filePres[i-1] = true;
	        	
	        }
	      }

	      String dimensioneTotaleTabellatoStringa = tabellatiManager.getDescrTabellato("A1072", "1");
	      if (dimensioneTotaleTabellatoStringa == null || "".equals(dimensioneTotaleTabellatoStringa)) {
	        throw new GestoreException("Non è presente il tabellato A1072 per determinare la dimensione massima totale dell'upload dei file",
	            "uploadMultiplo.noTabellato", null);
	      }

	      int pos = dimensioneTotaleTabellatoStringa.indexOf("(");
	      if (pos < 1) {
	        throw new GestoreException("Non è possibile determinare dal tabellato A1072 la dimensione massima totale dell'upload dei file",
	            "uploadMultiplo.noValore", null);
	      }

	      dimensioneTotaleTabellatoStringa = dimensioneTotaleTabellatoStringa.substring(0, pos - 1);
	      dimensioneTotaleTabellatoStringa = dimensioneTotaleTabellatoStringa.trim();
	      double dimensioneTotaleTabellatoByte = Math.pow(2, 20) * Double.parseDouble(dimensioneTotaleTabellatoStringa);
	      if (dimensioneTotale > dimensioneTotaleTabellatoByte) {
	        throw new GestoreException("La dimensione totale dei file da salvare ha superato il limite consentito di "
	            + dimensioneTotaleTabellatoStringa
	            + " MB", "uploadMultiplo.overflowMultiplo", new String[] { dimensioneTotaleTabellatoStringa }, null);
	      }
	      // *** Fine controllo dimensione massima file in upload

			for (int indice = 1; indice <= numeroRecord; indice++) {
				DataColumnContainer newDataColumnContainer = new DataColumnContainer(
						tmpDataColumnContainer.getColumnsBySuffix("_" + indice, false));

				boolean deleteOccorrenza = newDataColumnContainer.isColumn(nomeCampoDelete)
						&& "1".equals(newDataColumnContainer.getString(nomeCampoDelete));
				boolean updateOccorrenza = newDataColumnContainer.isColumn(nomeCampoMod)
						&& "1".equals(newDataColumnContainer.getString(nomeCampoMod));

				// Rimozione dei campi fittizi (il campo per la marcatura della delete e
				// tutti gli eventuali campi passati come argomento)
				newDataColumnContainer.removeColumns(
						new String[] { "G1ARCDOCUMOD." + nomeCampoDelete, "G1ARCDOCUMOD." + nomeCampoMod });

				// E' stata richiesta la cancellazione della riga, se il campo chiave
				// numerica è diverso da NULL eseguo effettivamente la cancellazione del
				// record.
				Long id = newDataColumnContainer.getLong("G1ARCDOCUMOD.ID");

				if (deleteOccorrenza) {
					if (id != null) {
						String idprg = newDataColumnContainer.getString("G1ARCDOCUMOD.IDPRG");
						Long iddocdig = newDataColumnContainer.getLong("G1ARCDOCUMOD.IDDOCDG");
						pgManagerEst1.cancellaW_DOCDIG(idprg, iddocdig);
						gestoreMultiploG1ARCDOCUMOD.elimina(status, newDataColumnContainer);
					}
				} else if (updateOccorrenza) {
					// Se il campo chiave numerico è nullo significa che bisogna inserire
					// una nuova occorrenza nelle tabelle W_DOCDIG e MEALLARTCAT
					if (id == null) {
						// select max su ordine
						Long maxNprogrOrdine = null;
						try {
							maxNprogrOrdine = (Long) this.sqlManager.getObject(
									"select chiave from w_genchiavi where tabella='G1ARCDOCUMOD'", new Object[] {});
							if (maxNprogrOrdine != null) {
								maxNprogrOrdine = maxNprogrOrdine + new Long(1);
							}

						} catch (SQLException e) {
							throw new GestoreException(
									"Errore nella determinazione del max progressivo degli allegati dell'ordine", null);
						}

						String maxNprogrOrdineStr = maxNprogrOrdine.toString();
						if (filePres[indice - 1]) {
							Long iddocdig = pgManagerEst1.inserisciW_DOCDIG(this.getRequest(), this.getForm(), indice,
									"PG", "G1ARCDOCUMOD", maxNprogrOrdineStr);
							newDataColumnContainer.setValue("G1ARCDOCUMOD.IDDOCDG", iddocdig);
						}
						newDataColumnContainer.setValue("G1ARCDOCUMOD.IDPRG", "PG");
						newDataColumnContainer.setValue("G1ARCDOCUMOD.IDDOCUMOD", datiForm.getLong("G1DOCUMOD.ID"));
						newDataColumnContainer.setValue("G1ARCDOCUMOD.ID", maxNprogrOrdine);
						gestoreMultiploG1ARCDOCUMOD.inserisci(status, newDataColumnContainer);
					} else {
						// In questo caso si tratta di aggiornare un record esistente
						String idprg = newDataColumnContainer.getString("G1ARCDOCUMOD.IDPRG");
						Long iddocdig = newDataColumnContainer.getLong("G1ARCDOCUMOD.IDDOCDG");
						String idStr = id.toString();

						if (iddocdig == null) {
							if (filePres[indice - 1]) {
								iddocdig = pgManagerEst1.inserisciW_DOCDIG(this.getRequest(), this.getForm(), indice, "PG",
										"G1ARCDOCUMOD", idStr);
								newDataColumnContainer.setValue("G1ARCDOCUMOD.IDDOCDG", iddocdig);
							}
						} else {
							pgManagerEst1.aggiornaW_DOCDIG(this.getRequest(), this.getForm(), indice, idprg, iddocdig);
						}
						gestoreMultiploG1ARCDOCUMOD.update(status, newDataColumnContainer);
					}
				}
			}
	    }
	  }

}