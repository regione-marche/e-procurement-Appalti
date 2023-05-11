package it.eldasoft.sil.pg.tags.gestori.submit;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.web.struts.UploadMultiploForm;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

public class GestoreMEARTCAT extends AbstractGestoreChiaveIDAutoincrementante {

  static Logger logger = Logger.getLogger(GestoreMEARTCAT.class);

  public String getCampoNumericoChiave() {
    return "ID";
  }

  public String getEntita() {
    return "MEARTCAT";
  }

  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    if (logger.isDebugEnabled()) logger.debug("GestoreMEARTCAT-preInsert: inizio metodo");

    super.preInsert(status, datiForm);
    Long id = datiForm.getLong("MEARTCAT.ID");
    Timestamp dataOdierna = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
    datiForm.addColumn("MEARTCAT.DATINS", JdbcParametro.TIPO_DATA, dataOdierna);
    datiForm.addColumn("MEARTCAT.DATMOD", JdbcParametro.TIPO_DATA, dataOdierna);

    // Assegnazione automatica del codice articolo se l'utente non ha indicato
    // nulla
    String cod = datiForm.getString("MEARTCAT.COD");
    if (cod == null || (cod != null && "".equals(cod.trim()))) {
      cod = id.toString();
      datiForm.setValue("MEARTCAT.COD", cod);
    }

    // Verifica se il codice articolo è univoco
    String ngara = datiForm.getString("MEARTCAT.NGARA");
    if (this.isCodiceArticoloUnivoco(id, cod, ngara) == false) {
      throw new GestoreException("Il codice articolo indicato e' già assegnato. Utilizzare un codice articolo differente.",
          "codicearticoloduplicato", null);
    }

    if (logger.isDebugEnabled()) logger.debug("GestoreMEARTCAT-preInsert: fine metodo");
  }
  
  public void afterInsertEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    if (logger.isDebugEnabled()) logger.debug("GestoreMEARTCAT-afterInsertEntita: inizio metodo");
    this.gestisciAggiornamentiRecordSchedaMultiplaMEALLARTCAT(status, datiForm);
    if (logger.isDebugEnabled()) logger.debug("GestoreMEARTCAT-afterInsertEntita: fine metodo");
  }
  

  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

  }

  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    if (logger.isDebugEnabled()) logger.debug("GestoreMEARTCAT-preUpdate: inizio metodo");

    Long id = datiForm.getLong("MEARTCAT.ID");
    String ngara = datiForm.getString("MEARTCAT.NGARA");
    String cod = datiForm.getString("MEARTCAT.COD");

    Timestamp dataOdierna = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
    datiForm.addColumn("MEARTCAT.DATMOD", JdbcParametro.TIPO_DATA, dataOdierna);

    // Verifica se il codice articolo è univoco
    if (datiForm.isModifiedColumn("MEARTCAT.COD")) {
      if (this.isCodiceArticoloUnivoco(id, cod, ngara) == false) {
        throw new GestoreException("Il codice articolo indicato e' già assegnato. Utilizzare un codice articolo differente.",
            "codicearticoloduplicato", null);
      }
    }

    // Gestione cancellazione automatica dei facsimile se non è richiesta alcuna
    // certificazione
    if (datiForm.isModifiedColumn("MEARTCAT.OBBLCERTIF")) {
      if ("2".equals(datiForm.getString("MEARTCAT.OBBLCERTIF"))) {
        try {
          String deleteW_DOCDIG = "delete from w_docdig where w_docdig.idprg = ? and w_docdig.iddocdig in (select meallartcat.iddocdig from meallartcat where idartcat = ?)";
          this.getSqlManager().update(deleteW_DOCDIG, new Object[] { "PG", id });
          String deleteMEALLAARTCAT = "delete from meallartcat where idartcat = ?";
          this.getSqlManager().update(deleteMEALLAARTCAT, new Object[] { id });
        } catch (SQLException e) {
          throw new GestoreException("Errore nella cancellazione di W_DOCDIG", null, e);
        }
      }
    }

    // Gestione dei file multipli
    if ("1".equals(datiForm.getString("MEARTCAT.OBBLCERTIF"))) {
      this.gestisciAggiornamentiRecordSchedaMultiplaMEALLARTCAT(status, datiForm);
    }

    if (logger.isDebugEnabled()) logger.debug("GestoreMEARTCAT-preUpdate: fine metodo");
  }

  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

  /**
   * Verifica se il codice articolo è univoco.
   * 
   * @param id
   * @param cod
   * @param ngara
   * @return boolean
   * @throws GestoreException
   */
  public boolean isCodiceArticoloUnivoco(Long id, String cod, String ngara) throws GestoreException {

    boolean isCodiceArticoloUnivoco = true;
    try {
      String selectMEARTCAT = "select count(*) from meartcat where ngara = ? and cod = ? and id <> ?";
      Long contaMEARTCAT = (Long) this.sqlManager.getObject(selectMEARTCAT, new Object[] { ngara, cod, id });
      if (contaMEARTCAT != null && contaMEARTCAT.longValue() > 0) {
        isCodiceArticoloUnivoco = false;
      }

    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura dei dati della tabella MEARTCAT", null, e);
    }
    return isCodiceArticoloUnivoco;

  }

  /**
   * Gestione aggiornamento facsimile certificati.
   * 
   * @param status
   * @param datiForm
   * @throws GestoreException
   */
  public void gestisciAggiornamentiRecordSchedaMultiplaMEALLARTCAT(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    AbstractGestoreChiaveIDAutoincrementante gestoreMEALLARTCAT = new DefaultGestoreEntitaChiaveIDAutoincrementante("MEALLARTCAT", "ID", this.getRequest());

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", this.getServletContext(),
        TabellatiManager.class);

    String nomeCampoNumeroRecord = "NUMERO_MEALLARTCAT";
    String nomeCampoDelete = "DEL_MEALLARTCAT";
    String nomeCampoMod = "MOD_MEALLARTCAT";

    if (datiForm.isColumn(nomeCampoNumeroRecord)) {
      DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(datiForm.getColumns("MEALLARTCAT", 0));

      int numeroRecord = datiForm.getLong(nomeCampoNumeroRecord).intValue();

      // *** Controllo dimensione massima dei file in UPLOAD
      HashMap<?, ?> hm = ((UploadMultiploForm) this.getForm()).getFormFiles();
      long dimensioneTotale = 0;
      FormFile ff = null;
      for (int i = 1; i <= numeroRecord; i++) {
        ff = (FormFile) hm.get(new Long(i));
        if (ff != null && ff.getFileSize() > 0) dimensioneTotale += ff.getFileSize();
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
        DataColumnContainer newDataColumnContainer = new DataColumnContainer(tmpDataColumnContainer.getColumnsBySuffix("_" + indice, false));

        boolean deleteOccorrenza = newDataColumnContainer.isColumn(nomeCampoDelete)
            && "1".equals(newDataColumnContainer.getString(nomeCampoDelete));
        boolean updateOccorrenza = newDataColumnContainer.isColumn(nomeCampoMod)
            && "1".equals(newDataColumnContainer.getString(nomeCampoMod));

        // Rimozione dei campi fittizi (il campo per la marcatura della delete e
        // tutti gli eventuali campi passati come argomento)
        newDataColumnContainer.removeColumns(new String[] { "MEALLARTCAT." + nomeCampoDelete, "MEALLARTCAT." + nomeCampoMod });

        // E' stata richiesta la cancellazione della riga, se il campo chiave
        // numerica è diverso da NULL eseguo effettivamente la cancellazione del
        // record.
        Long id = newDataColumnContainer.getLong("MEALLARTCAT.ID");

        if (deleteOccorrenza) {
          if (id != null) {
            String idprg = newDataColumnContainer.getString("MEALLARTCAT.IDPRG");
            Long iddocdig = newDataColumnContainer.getLong("MEALLARTCAT.IDDOCDIG");
            this.cancellaW_DOCDIC(idprg, iddocdig);
            gestoreMEALLARTCAT.elimina(status, newDataColumnContainer);
          }
        } else if (updateOccorrenza) {
          // Se il campo chiave numerico è nullo significa che bisogna inserire
          // una nuova occorrenza nelle tabelle W_DOCDIG e MEALLARTCAT
          if (id == null) {
            Long iddocdig = this.inserisciW_DOCDIC(indice, "PG", "MEALLARTCAT");
            newDataColumnContainer.setValue("MEALLARTCAT.IDPRG", "PG");
            newDataColumnContainer.setValue("MEALLARTCAT.IDDOCDIG", iddocdig);
            newDataColumnContainer.setValue("MEALLARTCAT.IDARTCAT", datiForm.getLong("MEARTCAT.ID"));
            gestoreMEALLARTCAT.inserisci(status, newDataColumnContainer);
          } else {
            // In questo caso si tratta di aggiornare un record esistente
            String idprg = newDataColumnContainer.getString("MEALLARTCAT.IDPRG");
            Long iddocdig = newDataColumnContainer.getLong("MEALLARTCAT.IDDOCDIG");
            this.aggiornaW_DOCDIC(indice, idprg, iddocdig);
          }
        }
      }
    }
  }

  /**
   * Eliminazione occorrenza W_DOCDIG.
   * 
   * @param idprg
   * @param iddocdig
   * @throws GestoreException
   */
  private void cancellaW_DOCDIC(String idprg, Long iddocdig) throws GestoreException {
    try {
      String deleteW_DOCDIG = "delete from w_docdig where idprg = ? and iddocdig = ?";
      this.getSqlManager().update(deleteW_DOCDIG, new Object[] { idprg, iddocdig });
    } catch (SQLException e) {
      throw new GestoreException("Errore nella cancellazione di W_DOCDIG", null, e);
    }
  }

  /**
   * Inserimento in W_DOCDIG.
   * 
   * @param indice
   * @param idprg
   * @param digent
   * @return
   * @throws GestoreException
   */
  private Long inserisciW_DOCDIC(int indice, String idprg, String digent) throws GestoreException {

    String fname = null;
    Long iddocdig = null;
    try {
      HashMap<?, ?> hm = ((UploadMultiploForm) this.getForm()).getFormFiles();

      DataColumnContainer dccW_DOCDIG = new DataColumnContainer(new DataColumn[] { new DataColumn("W_DOCDIG.IDPRG", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, idprg)) });

      String selectMaxW_DOCDIG = "select max(iddocdig) from w_docdig where idprg = ?";
      iddocdig = (Long) this.sqlManager.getObject(selectMaxW_DOCDIG, new Object[] { idprg });
      if (iddocdig == null) iddocdig = new Long(0);
      iddocdig = new Long(iddocdig.longValue() + 1);
      dccW_DOCDIG.addColumn("W_DOCDIG.IDDOCDIG", JdbcParametro.TIPO_NUMERICO, iddocdig);
      dccW_DOCDIG.addColumn("W_DOCDIG.DIGENT", JdbcParametro.TIPO_TESTO, digent);

      // Gestione upload del file
      FormFile ff = (FormFile) hm.get(new Long(indice));
      if (ff != null) {
        fname = ff.getFileName();
        if (fname.length() != 0 && ff.getFileSize() > 0) {
          dccW_DOCDIG.addColumn("W_DOCDIG.DIGNOMDOC", JdbcParametro.TIPO_TESTO, fname);
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          baos.write(ff.getFileData());
          dccW_DOCDIG.addColumn("W_DOCDIG.DIGOGG", JdbcParametro.TIPO_BINARIO, baos);
        } else if (fname.length() != 0 && ff.getFileSize() == 0) {
          UtilityStruts.addMessage(this.getRequest(), "warning", "warnings.gare.documentazioneGare.uploadMultiplo.fileVuoto",
              new String[] { fname });
        }
      }
      dccW_DOCDIG.insert("W_DOCDIG", this.geneManager.getSql());

    } catch (FileNotFoundException e) {
      throw new GestoreException("File da caricare non trovato", "uploadMultiplo", new String[] { fname }, e);
    } catch (IOException e) {
      throw new GestoreException("Si è verificato un errore durante la scrittura del buffer per il salvataggio del file "
          + fname
          + " su database", "uploadMultiplo", new String[] { fname }, e);
    } catch (SQLException e) {
      throw new GestoreException("Si è verificato un errore durante l'aggiornamento della tabella W_DOCDIG", null, e);
    }

    return iddocdig;
  }

  /**
   * Aggiornamento di W_DOCDIG.
   * 
   * @param indice
   * @param idprg
   * @param iddocdig
   * @throws GestoreException
   */
  private void aggiornaW_DOCDIC(int indice, String idprg, Long iddocdig) throws GestoreException {
    String fname = null;
    try {
      HashMap<?, ?> hm = ((UploadMultiploForm) this.getForm()).getFormFiles();

      DataColumnContainer dccW_DOCDIG = new DataColumnContainer(new DataColumn[] { new DataColumn("W_DOCDIG.IDPRG", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, idprg)) });
      dccW_DOCDIG.addColumn("W_DOCDIG.IDDOCDIG", JdbcParametro.TIPO_NUMERICO, iddocdig);
      dccW_DOCDIG.getColumn("W_DOCDIG.IDPRG").setChiave(true);
      dccW_DOCDIG.getColumn("W_DOCDIG.IDDOCDIG").setChiave(true);
      dccW_DOCDIG.setValue("W_DOCDIG.IDPRG", idprg);
      dccW_DOCDIG.setValue("W_DOCDIG.IDDOCDIG", iddocdig);
      dccW_DOCDIG.setOriginalValue("W_DOCDIG.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, idprg));
      dccW_DOCDIG.setOriginalValue("W_DOCDIG.IDDOCDIG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, iddocdig));

      // Gestione upload del file
      FormFile ff = (FormFile) hm.get(new Long(indice));
      if (ff != null) {
        fname = ff.getFileName();
        if (fname.length() != 0 && ff.getFileSize() > 0) {
          dccW_DOCDIG.addColumn("W_DOCDIG.DIGNOMDOC", JdbcParametro.TIPO_TESTO, fname);
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          baos.write(ff.getFileData());
          dccW_DOCDIG.addColumn("W_DOCDIG.DIGOGG", JdbcParametro.TIPO_BINARIO, baos);
        } else if (fname.length() != 0 && ff.getFileSize() == 0) {
          UtilityStruts.addMessage(this.getRequest(), "warning", "warnings.gare.documentazioneGare.uploadMultiplo.fileVuoto",
              new String[] { fname });
        }
      }
      dccW_DOCDIG.update("W_DOCDIG", this.geneManager.getSql());

    } catch (FileNotFoundException e) {
      throw new GestoreException("File da caricare non trovato", "uploadMultiplo", new String[] { fname }, e);
    } catch (IOException e) {
      throw new GestoreException("Si è verificato un errore durante la scrittura del buffer per il salvataggio del file "
          + fname
          + " su database", "uploadMultiplo", new String[] { fname }, e);
    } catch (SQLException e) {
      throw new GestoreException("Si è verificato un errore durante l'aggiornamento della tabella W_DOCDIG", null, e);
    }
  }
}
