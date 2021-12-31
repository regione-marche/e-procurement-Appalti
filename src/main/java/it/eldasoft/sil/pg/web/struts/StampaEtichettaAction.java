/*
 * Created on 07/mag/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.FileManager;
import it.eldasoft.gene.bl.FileManagerException;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.genmod.CompositoreException;
import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.bl.genmod.ServizioCompositoreProxy;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.genmod.ParametroComposizione;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Action per la stampa delle etichette del protocollo
 *
 * @author Luca.Giacomazzo
 */
public class StampaEtichettaAction extends DispatchActionBaseNoOpzioni {

  /** Logger */
  static Logger logger = Logger.getLogger(StampaEtichettaAction.class);

  private static final String PROP_PATH_RELATIVO_PROTOTIPO_ETICHETTA = "it.eldasoft.stampe.prototipoEtichetta.pathRelativo";

  private TabellatiManager tabellatiManager;
  private ModelliManager   modelliManager;
  private FileManager      fileManager;
  private PgManager        pgManager;

  public void setTabellatiManager(TabellatiManager tabellatiManager){
    this.tabellatiManager = tabellatiManager;
  }

  public void setModelliManager(ModelliManager modelliManager){
    this.modelliManager = modelliManager;
  }

  public void setFileManager(FileManager fileManager) {
    this.fileManager = fileManager;
  }

  public void setPgManager(PgManager pgManager) {
    this.pgManager = pgManager;
  }

  public ActionForward componiEtichetta(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if(logger.isDebugEnabled()) logger.debug("componiEtichetta: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK + "Stampa";
    String messageKey = null;

    String idApplicazione = ConfigManager.getValore(
        CostantiGenerali.PROP_ID_APPLICAZIONE);
    String moduloAttivo = (String) request.getSession().getAttribute(
        CostantiGenerali.MODULO_ATTIVO);

    Integer tipoProtocollo = UtilityNumeri.convertiIntero(
        request.getParameter("tipoProtocollo"));

    // Flag per capire se il browser dell'utente e' IE o meno
    boolean isBrowserIE = "1".equals(request.getParameter("isIE"));
    // Flag per determinare se la stampa con macro e' abilitata o meno
    boolean stampaConMacroAbilitata =
      tabellatiManager.getDescrTabellato("A1046", "0") == null;

    if(isBrowserIE && stampaConMacroAbilitata){
      etichettaConMacro(request, response, messageKey, idApplicazione,
          moduloAttivo, tipoProtocollo.intValue());
    } else {
      etichettaConCompositore(request, response, messageKey, idApplicazione,
          moduloAttivo, tipoProtocollo.intValue());
    }

    String barcode = request.getParameter("barcode");
    if("si".equals(barcode))
      target = CostantiGeneraliStruts.FORWARD_OK + "StampaDaBarcode";
      //request.setAttribute("barcode", "si");


    if(logger.isDebugEnabled()) logger.debug("componiEtichetta: fine metodo");
    return mapping.findForward(target);
  }

  private void etichettaConMacro(HttpServletRequest request, HttpServletResponse response,
      String messageKey, String idApplicazione, String moduloAttivo, int tipoProtocollo){

    if(logger.isDebugEnabled()) logger.debug("etichettaConMacro: inizio metodo");

    Date dataOdierna = new Date();
    String codiceGara = null;
    String codiceDitta = null;
    String numeroGara = null;

    // chiave della ditta nel formato DITG.CODGAR5=T:<codgar>;DITG.DITTAO=T:<dittao>;DITG.NGARA5=T:<ngara>
    String key = request.getParameter("chiave");
    String[] arrayCampiChiave = key.split(";");

    for(int i=0; i < arrayCampiChiave.length; i++){
      if(arrayCampiChiave[i].indexOf("CODGAR5") >= 0)
        codiceGara = arrayCampiChiave[i].substring(arrayCampiChiave[i].indexOf("=T:")+3);
      if(arrayCampiChiave[i].indexOf("DITTAO") >= 0)
        codiceDitta = arrayCampiChiave[i].substring(arrayCampiChiave[i].indexOf("=T:")+3);
      if(arrayCampiChiave[i].indexOf("NGARA5") >= 0)
        numeroGara = arrayCampiChiave[i].substring(arrayCampiChiave[i].indexOf("=T:")+3);
    }

    String pathPrototipoEtichetta =
        ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI) +
        ConfigManager.getValore(PROP_PATH_RELATIVO_PROTOTIPO_ETICHETTA);

    String pathStampeServer = this.pgManager.getPathStampeLatoServer();

    //String fileComposto = "";
    String nomeFileModello = null;
    String nomeFileParametri = "file_import.et";
    try {
      nomeFileModello = this.tabellatiManager.getDescrTabellato("A1046", "3");

      if(pathStampeServer != null){
        // File contenente l'etichetta da stampare e la macro
        File fileSorgente = new File(pathPrototipoEtichetta + nomeFileModello);
        // Creazione della sottodirectory in cui copiare l'etichetta da stampare
        File directoryDestinazione = new File(pathStampeServer + ("" + dataOdierna.getTime()));
        directoryDestinazione.mkdir();
        // Copia del file .doc nella cartella di destinazione
        FileUtils.copyFileToDirectory(fileSorgente, directoryDestinazione, false);

        // Lettura da DB dei valori da stampare nell'etichetta
        String[] valoriDB = this.pgManager.getValoriEtichettaProtocollo(
            codiceGara, codiceDitta, numeroGara, tipoProtocollo);

        // Creazione del file che contiene i valori dei campi per l'etichetta
        File fileOut = new File(directoryDestinazione.getAbsolutePath()+ "/" + nomeFileParametri);
        fileOut.createNewFile();
        List listaRighe = new ArrayList();
        listaRighe.add("ETICHETTA VERSIONE 6.0.4");
        listaRighe.add("campo1\t" + valoriDB[0]);
        listaRighe.add("campo2\t" + valoriDB[1]);
        listaRighe.add("campo3\t" + valoriDB[2]);
        listaRighe.add("campo4\t" + valoriDB[3]);
        listaRighe.add("campo5\t" + valoriDB[4]);
        listaRighe.add("campo6\t" + valoriDB[5]);
        listaRighe.add("campo7\t" + numeroGara);

        FileUtils.writeLines(fileOut, listaRighe);
      }
    } catch (DataAccessException e) {
      //target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      String logMessage = this.resBundleGenerale.getString(messageKey);
      logger.error(logMessage, e);
      // this.aggiungiMessaggio(request, messageKey);
      request.setAttribute("erroriStampa", logMessage);
    } catch (RemoteException r) {
      //target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.modelli.compositoreDisattivo";
      logger.error(this.resBundleGenerale.getString(messageKey), r);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      //target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    if(messageKey == null) {
      request.setAttribute("RISULTATO", "OK-stampa");
      request.setAttribute("nomeFile", nomeFileModello);
      request.setAttribute("subDir", new Long(dataOdierna.getTime()));
      request.setAttribute("pathFile", this.pgManager.getPathStampeLatoClient() +
          (dataOdierna.getTime() + "/"));
    } else
      request.setAttribute("RISULTATO", "KO-stampa");

    if(logger.isDebugEnabled()) logger.debug("etichettaConMacro: fine metodo");
  }

  private void etichettaConCompositore(HttpServletRequest request, HttpServletResponse response,
      String messageKey, String idApplicazione, String moduloAttivo, int tipoProtocollo){

    if(logger.isDebugEnabled()) logger.debug("etichettaConCompositore: inizio metodo");

    // chiave della ditta nel formato DITG.CODGAR5=T:<codgar>;DITG.DITTAO=T:<dittao>;DITG.NGARA5=T:<ngara>
    String key = request.getParameter("chiave");
    String[] arrayCampiChiave = key.split(";");

    String nomeCampiChiave = "";  // CODGAR5;NGARA5;DITTAO
    String[] valoriCampiChiave = new String[1];
    valoriCampiChiave[0] = "";

    for(int i=0; i < arrayCampiChiave.length; i++){
      String[] tmp = arrayCampiChiave[i].split("=T:");
      nomeCampiChiave += tmp[0].substring(tmp[0].indexOf(".")+1) + ";";
      valoriCampiChiave[0] = valoriCampiChiave[0] + tmp[1] + ";";
    }
    //Rimozione ultimo carattere ';'
    nomeCampiChiave = nomeCampiChiave.substring(0, nomeCampiChiave.length()-1);
    valoriCampiChiave[0] = valoriCampiChiave[0].substring(0, valoriCampiChiave[0].length()-1);

    // Inserimento del parametro tipoProtocollo da passare al compositore

    ParametroComposizione[] parametriComposizione = new ParametroComposizione[1];
    parametriComposizione[0] = new ParametroComposizione();
    parametriComposizione[0].setCodice("TIPSCAD");
    parametriComposizione[0].setValore("" + tipoProtocollo);

    int idSessione = this.modelliManager.insertParametriComposizione(parametriComposizione);

    String pathModelliOut =
        ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI) +
        ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI_OUTPUT);

    String pathStampeServer = this.pgManager.getPathStampeLatoServer();

    String fileComposto = "";
    try {
      String nomeFileModello = this.tabellatiManager.getDescrTabellato(
          "A1046", "2");

      ServizioCompositoreProxy servizio = new ServizioCompositoreProxy();
      servizio.setEndpoint(ConfigManager.getValore(CostantiGenModelli.PROP_URL_WEB_SERVICE));
      fileComposto = servizio.componiModelloConParametri(nomeFileModello,
          "DITG", nomeCampiChiave, valoriCampiChiave, idApplicazione,
          moduloAttivo, idSessione);

      if(pathStampeServer != null && pathModelliOut != null &&
          (fileComposto != null && fileComposto.length() > 0)){
        File fileIn = new File(pathModelliOut + fileComposto);
        File fileOut = new File(pathStampeServer + fileComposto);
        // Copia del file appena composto nella cartella predefinita per le stampe
        FileUtils.copyFile(fileIn, fileOut);
        // Cancellazione del file appena composto dalla cartella ...\Modelli\out\
        // (la cancellazione non usa la chiamata modelliManager.eliminaFileComposto)
        fileIn.delete();
      }
    } catch (CompositoreException e) {
      // Si è verificato l'errore di composizione
      //target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = e.getChiaveResourceBundle();
      if (e.getParametri() == null) {
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);
      } else if (e.getParametri().length == 1) {
        logger.error(UtilityStringhe.replaceParametriMessageBundle(
            this.resBundleGenerale.getString(messageKey), e.getParametri()), e);
        this.aggiungiMessaggio(request, messageKey, e.getParametri()[0]);
      } else {
        logger.error(UtilityStringhe.replaceParametriMessageBundle(
            this.resBundleGenerale.getString(messageKey), e.getParametri()), e);
        this.aggiungiMessaggio(request, messageKey, e.getParametri()[0],
            e.getParametri()[1]);
      }
    } catch (DataAccessException e) {
      //target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      String logMessage = this.resBundleGenerale.getString(messageKey);
      logger.error(logMessage, e);
      // this.aggiungiMessaggio(request, messageKey);
      request.setAttribute("erroriStampa", logMessage);
    } catch (RemoteException r) {
      //target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.modelli.compositoreDisattivo";
      logger.error(this.resBundleGenerale.getString(messageKey), r);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      //target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    } finally {
      // elimina i parametri utilizzati durante la composizione a prescindere
      // dal fatto che la composizione sia andata a buon fine o meno
      this.modelliManager.deleteParametriComposizione(idSessione);
    }

    if(messageKey == null) {
      request.setAttribute("RISULTATO", "OK-stampa");
      request.setAttribute("nomeFile", fileComposto);

      request.setAttribute("pathFile", this.pgManager.getPathStampeLatoClient());
    } else
      request.setAttribute("RISULTATO", "KO-stampa");

    if(logger.isDebugEnabled()) logger.debug("etichettaConCompositore: fine metodo");
  }

  public ActionForward downloadEtichetta(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
    if(logger.isDebugEnabled()) logger.debug("downloadEtichetta: inizio metodo");

    String target = null;
    String messageKey = null;
    String nomeCompletoFile = this.pgManager.getPathStampeLatoClient() +
        request.getParameter("nomeFile");

    try {
      fileManager.download(nomeCompletoFile, response);
    } catch (FileManagerException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = CostantiGenerali.RESOURCE_ERRORE_DOWNLOAD;
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if(logger.isDebugEnabled()) logger.debug("downloadEtichetta: fine metodo");
    return mapping.findForward(target);
  }

  public ActionForward cancellaEtichetta(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
    if(logger.isDebugEnabled()) logger.debug("cancellaEtichetta: inizio metodo");

    String target = null;
    String messageKey = null;
    String nomeCompletoFile = null;
    boolean stampaConMacroAbilitata =
      tabellatiManager.getDescrTabellato("A1046", "0") == null;
    try {
      if("1".equals(request.getParameter("isIE")) && stampaConMacroAbilitata){
        File directoryToDelete = new File(this.pgManager.getPathStampeLatoServer() +
            request.getParameter("subDir"));
        FileUtils.deleteDirectory(directoryToDelete);
        request.setAttribute("RISULTATO", "OK-annulla");
      } else {
        nomeCompletoFile = this.pgManager.getPathStampeLatoServer() +
          request.getParameter("nomeFile");
        File file = new File(nomeCompletoFile);
        file.delete();
        request.setAttribute("RISULTATO", "OK-annulla");
      }
    } catch (Throwable t) {
      //target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      request.setAttribute("RISULTATO", "KO-annulla");
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    String barcode = request.getParameter("barcode");
    if("si".equals(barcode))
      target = CostantiGeneraliStruts.FORWARD_OK + "CancellaEtichettaDaBarcode";

    if(logger.isDebugEnabled()) logger.debug("cancellaEtichetta: fine metodo");
    return mapping.findForward(target);
  }

}