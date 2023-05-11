package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestoreW_DISCALL extends AbstractGestoreChiaveNumerica {

  /** Logger */
  static Logger logger = Logger.getLogger(GestoreW_DISCALL.class);

  @Override
  public String[] getAltriCampiChiave() {
    return new String[] { "DISCID_P", "DISCID" };
  }

  @Override
  public String getCampoNumericoChiave() {
    return "ALLNUM";
  }

  @Override
  public String getEntita() {
    return "W_DISCALL";
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
    // gestita al salvataggio del dettaglio dell'allegato (sia in inserimento
    // che in modifica),
    // la visualizzazione della lista degli allegati invece della scheda di
    // dettaglio stesso
    this.getRequest().setAttribute("salvataggioOK", "true");
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
    // gestita al salvataggio del dettaglio dell'allegato (sia in inserimento
    // che in modifica),
    // la visualizzazione della lista degli allegati invece della scheda di
    // dettaglio stesso
    this.getRequest().setAttribute("salvataggioOK", "true");
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("GestoreW_DISCALL: preInsert: inizio metodo");

    super.preInsert(status, datiForm);

    if (datiForm.isColumn("FILEDAALLEGARE")
        && datiForm.getString("FILEDAALLEGARE") != null
        && !datiForm.getString("FILEDAALLEGARE").trim().equals("")) {
      ByteArrayOutputStream baos = null;

      TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", this.getServletContext(),
          TabellatiManager.class);

      String dimMaxSingoloFileStringa = tabellatiManager.getDescrTabellato("A1072", "1");
      if (dimMaxSingoloFileStringa == null || "".equals(dimMaxSingoloFileStringa)) {
        throw new GestoreException("Non è presente il tabellato A1072 per determinare la dimensione " + "massima dell'upload del file",
            "upload.noTabellato", null);
      }
      int pos = dimMaxSingoloFileStringa.indexOf("(");
      if (pos < 1) {
        throw new GestoreException("Non è possibile determinare dal tabellato A1072 la dimensione " + "massima dell'upload del file",
            "upload.noValore", null);
      }
      dimMaxSingoloFileStringa = dimMaxSingoloFileStringa.substring(0, pos - 1);
      dimMaxSingoloFileStringa = dimMaxSingoloFileStringa.trim();
      double dimMaxSingoloFileByte = Math.pow(2, 20) * Double.parseDouble(dimMaxSingoloFileStringa);

      try {

        if (this.getForm().getSelezioneFile().getFileSize() == 0) {
          throw new GestoreException("Il file specificato è vuoto. Per continuare specificare un altro file", "upload.fileVuoto", null,
              null);
        } else if (this.getForm().getSelezioneFile().getFileSize() > dimMaxSingoloFileByte) {
          throw new GestoreException("La dimensione del file da allegare supera il limite consentito "
              + "di "
              + dimMaxSingoloFileStringa
              + " MB", "upload.overflow", new String[] { dimMaxSingoloFileStringa }, null);
        } else if (!FileAllegatoManager.isEstensioneFileAmmessa(this.getForm().getSelezioneFile().getFileName())) {
          throw new GestoreException("Il file selezionato da caricare ha un'estensione non accettata", "upload.estensioneNonAmmessa",
              new String[] { this.getForm().getSelezioneFile().getFileName() }, null);
        } else {

          baos = new ByteArrayOutputStream();
          baos.write(this.getForm().getSelezioneFile().getFileData());
          datiForm.addColumn("W_DISCALL.ALLSTREAM", JdbcParametro.TIPO_BINARIO, baos);
        }

      } catch (FileNotFoundException e) {
        throw new GestoreException("File da caricare non trovato", "upload", e);
      } catch (IOException e) {
        throw new GestoreException("Si è verificato un errore durante la scrittura del buffer per il salvataggio del file allegato su DB",
            "upload", e);
      }
    }

    if (logger.isDebugEnabled()) logger.debug("GestoreW_DISCALL preInsert: fine metodo");

  }
}
