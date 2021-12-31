package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ValidatorManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestoreW_DOCDIG extends AbstractGestoreChiaveNumerica {

  /** Logger */
  static Logger logger = Logger.getLogger(GestoreW_DOCDIG.class);

  @Override
  public String[] getAltriCampiChiave() {
    return new String[] { "IDPRG" };
  }

  @Override
  public String getCampoNumericoChiave() {
    return "IDDOCDIG";
  }

  @Override
  public String getEntita() {
    return "W_DOCDIG";
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
    //gestita al salvataggio del dettaglio dell'allegato (sia in inserimento che in modifica),
    //la visualizzazione della lista degli allegati invece della scheda di dettaglio stesso
    this.getRequest().setAttribute("salvataggioOK", "true");
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
    //gestita al salvataggio del dettaglio dell'allegato (sia in inserimento che in modifica),
    //la visualizzazione della lista degli allegati invece della scheda di dettaglio stesso
    this.getRequest().setAttribute("salvataggioOK", "true");
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {


    if (logger.isDebugEnabled())
      logger.debug("GestoreW_DOCDIG: preInsert: inizio metodo");

    super.preInsert(status, datiForm);

    if (datiForm.isColumn("FILEDAALLEGARE")
        && datiForm.getString("FILEDAALLEGARE") != null
        && !datiForm.getString("FILEDAALLEGARE").trim().equals("")) {
      ByteArrayOutputStream baos = null;

      TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
          "tabellatiManager", this.getServletContext(), TabellatiManager.class);

      MailManager mailManager = (MailManager) UtilitySpring.getBean(
          "mailManager", this.getServletContext(), MailManager.class);

      String dimMaxSingoloFileStringa = tabellatiManager.getDescrTabellato("A1072", "1");
      if(dimMaxSingoloFileStringa==null || "".equals(dimMaxSingoloFileStringa)){
        throw new GestoreException("Non è presente il tabellato A1072 per determinare la dimensione "
            + "massima dell'upload del file", "upload.noTabellato", null);
      }
      int pos = dimMaxSingoloFileStringa.indexOf("(");
      if (pos<1){
        throw new GestoreException("Non è possibile determinare dal tabellato A1072 la dimensione "
            + "massima dell'upload del file", "upload.noValore", null);
      }
      dimMaxSingoloFileStringa = dimMaxSingoloFileStringa.substring(0, pos-1);
      dimMaxSingoloFileStringa = dimMaxSingoloFileStringa.trim();
      double dimMaxSingoloFileByte = Math.pow(2, 20) * Double.parseDouble(dimMaxSingoloFileStringa);

      //Vi è il limite sulla dimensione totale dei file?
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
          "sqlManager", this.getServletContext(), SqlManager.class);

      ValidatorManager validatorManager = (ValidatorManager) UtilitySpring.getBean("validatorManager",
          this.getServletContext(), ValidatorManager.class);

      Long iddocdigCorrente = datiForm.getLong("W_DOCDIG.IDDOCDIG");
      String entita = datiForm.getString("W_DOCDIG.DIGENT");
      String chiave1 = datiForm.getString("W_DOCDIG.DIGKEY1");
      String chiave2 = datiForm.getString("W_DOCDIG.DIGKEY2");

      long dimTotaleAllegati = 0;
      double dimMaxTotaleFileByte=0;
      boolean eseguireControlloDimTotale=false;
      String dimMaxTotaleFileStringa= null;
      try {
        String idcfg = (String)sqlManager.getObject("select idcfg from w_invcom where idprg=? and idcom=?", new Object[]{chiave1,chiave2});
        if(idcfg==null || "".equals(idcfg))
          idcfg = CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD;
        dimMaxTotaleFileStringa = validatorManager.getDimensioneMassimaFile(idcfg);
      }  catch (SQLException e) {
        throw new GestoreException("Errore nella lettura del campo W_INVOCM.IDCFG", null, e);
      }

      //Si deve determinare la dimensione massima dei file già allegati e di quello che si sta allegando
      if(dimMaxTotaleFileStringa!= null && !"".equals(dimMaxTotaleFileStringa)){
        dimMaxTotaleFileStringa = dimMaxTotaleFileStringa.trim();
        eseguireControlloDimTotale=true;
        dimMaxTotaleFileByte = Math.pow(2, 20) * Double.parseDouble(dimMaxTotaleFileStringa);


        try {

          List listaW_DOCDIG = sqlManager.getListVector("select IDDOCDIG,IDPRG  from W_DOCDIG where W_DOCDIG.DIGENT = ? AND W_DOCDIG.DIGKEY1 = ? AND W_DOCDIG.DIGKEY2 = ? and " +
          		"W_DOCDIG.IDDOCDIG <> ?", new Object[]{entita, chiave1, chiave2, iddocdigCorrente});
          if(listaW_DOCDIG!=null && listaW_DOCDIG.size()>0){

            FileAllegatoManager fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean(
                "fileAllegatoManager", this.getServletContext(), FileAllegatoManager.class);

            for(int i=0;i<listaW_DOCDIG.size();i++){
              Long iddocdig = (Long)SqlManager.getValueFromVectorParam(listaW_DOCDIG.get(i), 0).getValue();
              String idprg = (String)SqlManager.getValueFromVectorParam(listaW_DOCDIG.get(i), 1).getValue();
              BlobFile fileAllegatoBlob = fileAllegatoManager.getFileAllegato(idprg, iddocdig);

              if(fileAllegatoBlob!=null)
                dimTotaleAllegati += fileAllegatoBlob.getStream().length;

            }
          }
          if(this.getForm().getSelezioneFile().getFileSize() > 0 )
            dimTotaleAllegati += this.getForm().getSelezioneFile().getFileSize();
        } catch (SQLException e) {
          throw new GestoreException("Errore nella lettura degli allegati della comunicazione(W_DOCDIG.DIGOGG)", null);
        } catch (IOException e) {
          throw new GestoreException("Errore nella lettura degli allegati della comunicazione(W_DOCDIG.DIGOGG)", null);
        }
      }


      try {

        if(this.getForm().getSelezioneFile().getFileSize() == 0 ){
          throw new GestoreException("Il file specificato è vuoto. Per continuare specificare un altro file",
              "upload.fileVuoto", null, null);
        }else if(this.getForm().getSelezioneFile().getFileSize()> dimMaxSingoloFileByte){  //Controllo sulla dimensione massima del singolo file
          throw new GestoreException("La dimensione del file da allegare supera il limite consentito "
              + "di " + dimMaxSingoloFileStringa + " MB" , "upload.overflow", new String[] { dimMaxSingoloFileStringa },null);
        }else if(eseguireControlloDimTotale && dimTotaleAllegati> dimMaxTotaleFileByte){  //Controllo sulla dimensione totale massima di tutti gli allegati
          throw new GestoreException("La dimensione totale dei file da allegare supera il limite consentito dal server di posta "
              + "di " + dimMaxTotaleFileStringa + " MB" , "upload.overflowMultiplo", new String[] { dimMaxTotaleFileStringa },null);
        }else if (!FileAllegatoManager.isEstensioneFileAmmessa(this.getForm().getSelezioneFile().getFileName())) { //Controllo sulle estensioni ammesse al nome del file
          throw new GestoreException("Il file selezionato da caricare ha un'estensione non accettata",
              "upload.estensioneNonAmmessa", new String[]{this.getForm().getSelezioneFile().getFileName()}, null);
        } else {

          baos = new ByteArrayOutputStream();
          baos.write(this.getForm().getSelezioneFile().getFileData());
          datiForm.addColumn("W_DOCDIG.DIGOGG", JdbcParametro.TIPO_BINARIO,
              baos);
        }

      } catch (FileNotFoundException e) {
        throw new GestoreException("File da caricare non trovato", "upload", e);
      } catch (IOException e) {
        throw new GestoreException(
            "Si è verificato un errore durante la scrittura del buffer per il salvataggio del file allegato su DB",
            "upload", e);
      }
    }

    if (logger.isDebugEnabled())
      logger.debug("GestoreW_DOCDIG: preInsert: fine metodo");

  }



}
