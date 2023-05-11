/*
 * Created on 22/08/2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.commons.web.TempFileUtilities;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.springframework.transaction.TransactionStatus;


/**
 * Gestore per la documentazione di gara in archivio
 *
 * @author Cristian Febas
 */
public class GestoreARCHDOCG extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "ARCHDOCG";
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

  /**
   * Si eliminano in cascata anche tutte le occorrenze di W_DOCDIG
   *
   *
   */
  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

      // Devo cancellare l'occorrenza in W_DOCDIG
      try {
        Long iddocdg = (Long)sqlManager.getObject("select iddocdg from ARCHDOCG where CODARCH = ? and NUMDOCG = ? and IDPRG = ?",
            new Object[]{datiForm.getString("ARCHDOCG.CODARCH"),datiForm.getLong("ARCHDOCG.NUMDOCG"),"PG"});
        if(iddocdg != null){
          this.getSqlManager().update(
              "delete from W_DOCDIG where IDPRG = ? and IDDOCDIG = ?",
              new Object[] {"PG", iddocdg});
        }

      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella cancellazione di W_DOCDIG", null, e);
      }

  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    String select="select coalesce(max(numdocg),0) from archdocg ";
    try {
      Long numdocgMax = (Long)sqlManager.getObject(select, new Object[]{});
      numdocgMax=new Long(numdocgMax.longValue()+1);

      datiForm.setValue("ARCHDOCG.NUMDOCG", numdocgMax);

      String strNumdocg = numdocgMax.toString();
      String codarch = datiForm.getString("ARCHDOCG.CODARCH");

        // Gestione w_DOCDIG
        long numDoc = this.gestioneW_DOCDIG(datiForm);
        if (numDoc > 0) {
          datiForm.setValue("ARCHDOCG.IDPRG", "PG");
          datiForm.setValue("ARCHDOCG.IDDOCDG", new Long(numDoc));
          this.getSqlManager().update(
              "update W_DOCDIG set DIGENT=?, DIGKEY1=?, DIGKEY2=? where IDPRG=? and IDDOCDIG=?",
              new Object[] {"ARCHDOCG", codarch, strNumdocg, "PG",  new Long(numDoc) });
        }

    } catch (SQLException e) {
      throw new GestoreException("Errore nell'aggiornamento dei riferimenti del file allegato!", null, e);
    }

    //Verifico che non vi sia la contemporanea valorizzazione di IDSTAMPA e IDDOCDG
    //(se prevista la generazione automatica del pdf non è possibile anche allegare un documento).
    Long iddocdg =  datiForm.getLong("ARCHDOCG.IDDOCDG");
    String idstampa = datiForm.getString("ARCHDOCG.IDSTAMPA");
    idstampa = UtilityStringhe.convertiNullInStringaVuota(idstampa);
    if(!"".equals(idstampa) && iddocdg!=null){
      throw new GestoreException(
          "Non è possibile allegare un documento e anche specificare l'identificativo per la generazione automatica",
          "ArchivioDoc.DatiInconsistenti",null);
    }

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

      Long numdocg = datiForm.getLong("ARCHDOCG.NUMDOCG");
      String strNumdocg = numdocg.toString();
      String codarch = datiForm.getString("ARCHDOCG.CODARCH");

      // Gestione w_DOCDIG
      long numDoc = this.gestioneW_DOCDIG(datiForm);
      if (numDoc > 0) {
        datiForm.setValue("ARCHDOCG.IDPRG", "PG");
        datiForm.setValue("ARCHDOCG.IDDOCDG", new Long(numDoc));
        try {
          this.getSqlManager().update(
              "update W_DOCDIG set DIGENT=?, DIGKEY1=?, DIGKEY2=? where IDPRG=? and IDDOCDIG=?",
              new Object[] {"ARCHDOCG", codarch, strNumdocg, "PG",  new Long(numDoc) });
        } catch (SQLException e) {
          throw new GestoreException("Errore nell'aggiornamento dei riferimenti del file allegato!", null, e);
        }
      }

      //Verifico che non vi sia la contemporanea valorizzazione di IDSTAMPA e IDDOCDG
      //(se prevista la generazione automatica del pdf non è possibile anche allegare un documento).
      Long iddocdg =  datiForm.getLong("ARCHDOCG.IDDOCDG");
      String idstampa = datiForm.getString("ARCHDOCG.IDSTAMPA");
      idstampa = UtilityStringhe.convertiNullInStringaVuota(idstampa);
      if(!"".equals(idstampa) && iddocdg!=null){
        throw new GestoreException(
            "Non è possibile allegare un documento e anche specificare l'identificativo per la generazione automatica",
            "ArchivioDoc.DatiInconsistenti");
      }

  }


  /**
   * Gestisce le operazioni di update, insert, sulla tabella W_DOCDIG
   *
   * @param datiForm
   *        container in cui sono stai filtrati i record
   * @param datiFormOriginale
   *        container di partenza da cui filtrare i record
   * @param indice
   *        indice della sezione dinamica che si sta analizzando
   * @param suffisso
   *        suffisso da concatenare a "NUMERO_" per ottenere il campo che indica il numero di occorrenze presenti nel container
   *
   * @throws GestoreException
   *
   * @ret 0 se sono in update, >0 se sono in inserimento, il valore di IDDOCDG della nuova occorrenza inserita -1 se non si è effettuata
   *      nessuna operazione
   *
   */
  private long gestioneW_DOCDIG(DataColumnContainer datiForm)
      throws GestoreException {

    long ret = 0;
    byte[] file = null;
    String nomeFile = null;

    try {
      Long numDoc = datiForm.getLong("ARCHDOCG.IDDOCDG");
      boolean inserimento = true;

      if (numDoc != null && numDoc > 0) {
        inserimento = false;
      }

      DataColumnContainer newDataColumnContainer = new DataColumnContainer(
          datiForm.getColumns("W_DOCDIG", 0));

      newDataColumnContainer.getColumn("W_DOCDIG.IDPRG").setChiave(true);

      if (inserimento) {
        Long nProgressivoW_DOCDIG = (Long) this.getSqlManager().getObject(
            "select max(IDDOCDIG) from W_DOCDIG where IDPRG = 'PG' ", null);

        if (nProgressivoW_DOCDIG == null) {
          nProgressivoW_DOCDIG = new Long(0);
        }

        numDoc = nProgressivoW_DOCDIG + 1;
      }

      if (inserimento) {
        ret = numDoc;
      }

      newDataColumnContainer.getColumn("W_DOCDIG.IDDOCDIG").setChiave(true);

      boolean controlliFile = false;
      boolean salvareW_DOCDIG = true;

      if (StringUtils.isNotBlank(nomeFile)) {
        try {
          file = TempFileUtilities.getTempFile(nomeFile);
          controlliFile = checkFileToAdd(nomeFile, file);
        } catch (IOException ex) {
          salvareW_DOCDIG = false;
          ret = -1;
          throw new GestoreException(
              "Si è verificato un problema nella lettura del file temporaneo "
                  + nomeFile, "uploadMultiplo.fileTempNonTrovato",
              new String[] {nomeFile }, ex);
        }
      } else {
        // Estraggo le informazioni per il file di cui si è effettuato
        // l'upload
        UploadFileForm mf = getForm();
        FormFile ff = mf.getSelezioneFile();

        if (ff != null) {
          file = ff.getFileData();
          nomeFile = ff.getFileName();
          if (StringUtils.isNotBlank(nomeFile)) {
            controlliFile = checkFileToAdd(nomeFile, file);
          }
        } else {
          salvareW_DOCDIG = false;
        }
      }

      if (!controlliFile) {
        salvareW_DOCDIG = false;
        ret = -1;
      }

      if (salvareW_DOCDIG) {

        addFileToColumnContainer(newDataColumnContainer, nomeFile, file);

        if (datiForm.isModifiedColumn("W_DOCDIG.DIGDESDOC")) {
          newDataColumnContainer.getColumn("W_DOCDIG.DIGDESDOC").setObjectOriginalValue(null);
          String desc = datiForm.getString("W_DOCDIG.DIGDESDOC");
          newDataColumnContainer.setValue("W_DOCDIG.DIGDESDOC", desc);
        }

        if (inserimento) {

          // Si deve inserire solo se la descrizione ed il nome del
          // file sono valorizzati
          if ((newDataColumnContainer.getString("W_DOCDIG.DIGNOMDOC") != null && !"".equals(newDataColumnContainer.getString("W_DOCDIG.DIGNOMDOC")))
              || (newDataColumnContainer.getString("W_DOCDIG.DIGDESDOC") != null && !"".equals(newDataColumnContainer.getString("W_DOCDIG.DIGDESDOC")))) {

            newDataColumnContainer.getColumn("W_DOCDIG.IDPRG").setObjectOriginalValue(
                null);
            newDataColumnContainer.getColumn("W_DOCDIG.IDDOCDIG").setObjectOriginalValue(
                null);
            newDataColumnContainer.setValue("W_DOCDIG.IDPRG", "PG");
            newDataColumnContainer.setValue("W_DOCDIG.IDDOCDIG", numDoc);
            newDataColumnContainer.insert("W_DOCDIG", this.geneManager.getSql());

          } else {
            ret = -1;
          }
        } else {
          newDataColumnContainer.update("W_DOCDIG", this.geneManager.getSql());
        }
      }
    } catch (FileNotFoundException e) {
      throw new GestoreException("File da caricare non trovato",
          "uploadMultiplo", new String[] {nomeFile }, e);
    } catch (IOException e) {
      throw new GestoreException(
          "Si è verificato un errore durante la scrittura del buffer per il salvataggio del file "
              + nomeFile
              + " su DB", "uploadMultiplo", new String[] {nomeFile }, e);
    } catch (SQLException e) {
      throw new GestoreException(
          "Si è verificato un errore durante l'aggiornamento della tabella W_DOCDIG",
          null, e);
    }
    return ret;
  }

  /**
   * Popola una nuova colonna del column contaniner con il ByteArrayOutputStream del file da salvare
   *
   */
  private void addFileToColumnContainer(
      DataColumnContainer newDataColumnContainer, String nomeFile, byte[] file)
      throws IOException, GestoreException {

    newDataColumnContainer.getColumn("W_DOCDIG.DIGNOMDOC").setObjectOriginalValue(
        null);
    newDataColumnContainer.setValue("W_DOCDIG.DIGNOMDOC", nomeFile);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    baos.write(file);
    newDataColumnContainer.addColumn("W_DOCDIG.DIGOGG",
        JdbcParametro.TIPO_BINARIO, baos);
  }


  /**
   * Popola una nuova colonna del column contaniner con il ByteArrayOutputStream del file da salvare
   *
   * @param nomeFile
   *        il nome del file
   * @param file
   *        il file
   * @return true se i controlli sono andati a buon fine
   * @throws GestoreException 
   */
  private boolean checkFileToAdd(String nomeFile, byte[] file) throws GestoreException {

    boolean salvareW_DOCDIG = true;

    if (file == null || file.length == 0) {
      salvareW_DOCDIG = false;
      UtilityStruts.addMessage(this.getRequest(), "warning",
          "warnings.gare.documentazioneGare.uploadMultiplo.fileVuoto",
          new String[] {nomeFile });
    }
    if (StringUtils.isBlank(nomeFile)) {
      salvareW_DOCDIG = false;
      UtilityStruts.addMessage(this.getRequest(), "warning",
          "warnings.gare.documentazioneGare.uploadMultiplo.nomeFileVuoto",
          new String[] {nomeFile });
    }
    if (!FileAllegatoManager.isEstensioneFileAmmessa(nomeFile)) {
      salvareW_DOCDIG = false;
      throw new GestoreException(
          "Il file selezionato utilizza una estensione non ammessa", "upload.estensioneNonAmmessa",
          new String[] {nomeFile }, null);
    }


    return salvareW_DOCDIG;
  }
}
