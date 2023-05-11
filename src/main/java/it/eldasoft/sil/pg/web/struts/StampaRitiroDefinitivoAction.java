/*
 * Created on 13/mag/09
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
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.genmod.ParametroComposizione;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
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
 * Action per la stampa della lista definitiva delle ditte ritirate
 * 
 * @author Luca.Giacomazzo
 */
public class StampaRitiroDefinitivoAction extends DispatchActionBaseNoOpzioni {

  /** Logger */
  static Logger logger = Logger.getLogger(StampaRitiroDefinitivoAction.class);
  
  private TabellatiManager tabellatiManager;
  private FileManager      fileManager;
  private ModelliManager   modelliManager;
  private PgManager        pgManager;

  public void setTabellatiManager(TabellatiManager tabellatiManager){
    this.tabellatiManager = tabellatiManager;
  }
  
  public void setFileManager(FileManager fileManager) {
    this.fileManager = fileManager;
  }

  public void setModelliManager(ModelliManager modelliManager){
    this.modelliManager = modelliManager; 
  }

  public void setPgManager(PgManager pgManager) {
    this.pgManager = pgManager;
  }
  
  /**
   * Metodo per la composizione della lista definitiva dei plichi ritirati
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward stampaRitiroDefinitivo(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    
    String target = CostantiGeneraliStruts.FORWARD_OK + "Stampa";
    String messageKey = null;

    String idApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_ID_APPLICAZIONE);
    String moduloAttivo = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
    int idSessione = -1;
    
    String nomeCampiChiave = "NGARA;DITTAO";
    String[] valoriCampiChiave = new String[]{"A;B"}; // Valori fittizzi dei campi chiave
    
    ProfiloUtente profiloUtente = (ProfiloUtente)
        request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    
    // Lettura dal request della condizione di where con cui e'
    // stata aperta la lista a video
    String tipoProtocollo = request.getParameter("tipprot");
    String dataProtocollo = request.getParameter("datap");
    String operatoreDataProtocollo = request.getParameter("operatoreDatap");
    
    String fileComposto = "";

    List listaParametriComposizioneModelli = new ArrayList();
    ParametroComposizione paramTipoProtocollo = null;
    ParametroComposizione paramDataProtocollo = null; 
    ParametroComposizione paramOperatoreDataProtocollo = null;
    ParametroComposizione paramIdUtente = new ParametroComposizione();
    paramIdUtente.setCodice("IDUTENTE");
    paramIdUtente.setDescrizione("Id utente");
    paramIdUtente.setValore("" + profiloUtente.getId());
    listaParametriComposizioneModelli.add(paramIdUtente);

    try {
      if(tipoProtocollo != null && !tipoProtocollo.equals("")){
        paramTipoProtocollo = new ParametroComposizione();
        paramTipoProtocollo.setCodice("TIPPROT");
        paramTipoProtocollo.setDescrizione("Tipo Protocollo");
        paramTipoProtocollo.setValore(tipoProtocollo);
        listaParametriComposizioneModelli.add(paramTipoProtocollo);
      }

      if(dataProtocollo != null && !dataProtocollo.equals("") && 
          operatoreDataProtocollo != null && !operatoreDataProtocollo.equals("")){
        paramDataProtocollo = new ParametroComposizione();
        paramDataProtocollo.setCodice("DATAP");
        paramDataProtocollo.setDescrizione("Data Protocollo");
        paramDataProtocollo.setValore(dataProtocollo);

        paramOperatoreDataProtocollo = new ParametroComposizione();
        paramOperatoreDataProtocollo.setCodice("OPEDATAP");
        paramOperatoreDataProtocollo.setDescrizione("Operatore data Protocollo");
        paramOperatoreDataProtocollo.setValore(operatoreDataProtocollo);

        listaParametriComposizioneModelli.add(paramDataProtocollo);
        listaParametriComposizioneModelli.add(paramOperatoreDataProtocollo);
      }

      ParametroComposizione[] parametriComposizioneModello = (ParametroComposizione[]) 
          listaParametriComposizioneModelli.toArray(new ParametroComposizione[]{});
      idSessione = this.modelliManager.insertParametriComposizione(
          parametriComposizioneModello);

      String pathModelliOut = ConfigManager.getValore(
          CostantiGenModelli.PROP_PATH_MODELLI + 
          CostantiGenerali.SEPARATORE_PROPERTIES + 
          idApplicazione +
          CostantiGenerali.SEPARATORE_PROPERTIES + 
          moduloAttivo) +
          ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI_OUTPUT);
      String pathStampeServer = this.pgManager.getPathStampeLatoServer();
      String nomeFileModello = this.tabellatiManager.getDescrTabellato("A1046", "1");

      ServizioCompositoreProxy servizio = new ServizioCompositoreProxy();
      servizio.setEndpoint(ConfigManager.getValore(CostantiGenModelli.PROP_URL_WEB_SERVICE));
      fileComposto = servizio.componiModelloConParametri(nomeFileModello, "V_DITTE_PRIT",
          nomeCampiChiave, valoriCampiChiave, idApplicazione, moduloAttivo, idSessione);
      
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
      if(tipoProtocollo != null)
        request.setAttribute("tipprot", tipoProtocollo);
      if(dataProtocollo != null){
        request.setAttribute("datap", dataProtocollo);
        request.setAttribute("operatoreDatap", operatoreDataProtocollo);
      }

      // elimina i parametri utilizzati durante la composizione a prescindere
      // dal fatto che la composizione sia andata a buon fine o meno
      if(idSessione < 0)
        this.modelliManager.deleteParametriComposizione(idSessione);
    }

    if (messageKey == null){
      request.setAttribute("RISULTATO", "OK-stampa");
      request.setAttribute("nomeFile", fileComposto);

      request.setAttribute("pathFile", this.pgManager.getPathStampeLatoClient());
    } else
      request.setAttribute("RISULTATO", "KO-stampa");

    if(logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

  public ActionForward downloadRitiroDefinitivo(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
    if(logger.isDebugEnabled())
      logger.debug("downloadRitiroDefinitivo: inizio metodo");

    String target = null;
    String messageKey = null;
    
    String nomeCompletoFile = this.pgManager.getPathStampeLatoClient()
        + request.getParameter("nomeFile");

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

    if(logger.isDebugEnabled())
      logger.debug("downloadRitiroDefinitivo: fine metodo");

    return mapping.findForward(target);
  }

  public ActionForward aggiornaRitiroDefinitivo(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("aggiornaRititoDefinitivo: inizio metodo");

    String messageKey = null;
    String target = CostantiGeneraliStruts.FORWARD_OK + "Aggiorna";

    String tipoProtocollo = request.getParameter("tipprot");
    String dataProtocollo = request.getParameter("datap");
    String operatoreDataProtocollo = request.getParameter("operatoreDatap");

    ProfiloUtente profiloUtente = (ProfiloUtente)
      request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    String nomeCompletoFile = this.pgManager.getPathStampeLatoServer() +
        request.getParameter("nomeFile");  
    
    try {
      this.pgManager.updateRitiroDefinitivo(tipoProtocollo, dataProtocollo,
          operatoreDataProtocollo, profiloUtente.getId());
      
      File file = new File(nomeCompletoFile);
      file.delete();

      request.setAttribute("RISULTATO", "OK-aggiorna");
    } catch (SQLException e) {
      //target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      request.setAttribute("RISULTATO", "KO-aggiorna");
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable e) {
      //target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      request.setAttribute("RISULTATO", "KO-aggiorna");
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (logger.isDebugEnabled()) logger.debug("aggiornaRititoDefinitivo: fine metodo");
    return mapping.findForward(target);
  }

  public ActionForward annullaRitiroDefinitivo(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if(logger.isDebugEnabled()) logger.debug("annullaRitiroDefinitivo: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK + "Annulla";
    String messageKey = null;
    String nomeCompletoFile = this.pgManager.getPathStampeLatoServer() +
      request.getParameter("nomeFile");  

    try {
      File file = new File(nomeCompletoFile);
      file.delete();
  
      request.setAttribute("RISULTATO", "OK-annulla");
    } catch (Throwable t) {
      //target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      request.setAttribute("RISULTATO", "KO-annulla");
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }
    if(logger.isDebugEnabled()) logger.debug("annullaRitiroDefinitivo: fine metodo");
    return mapping.findForward(target);
  }

}