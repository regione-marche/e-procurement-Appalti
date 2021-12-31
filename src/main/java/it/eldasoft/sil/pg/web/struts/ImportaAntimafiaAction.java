/*
 * Created on 14/feb/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.xmlbeans.XmlException;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.VersioneManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.importData.CampoType;
import it.eldasoft.importData.DatiAliceDocument;
import it.eldasoft.importData.DatiAliceType;
import it.eldasoft.importData.OggettoType;
import it.eldasoft.importData.RecordType;
import it.eldasoft.sil.pg.db.domain.VerificaInterdizioneAntimafia;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Action per la gestione dell'import del file in formato XML per verifica e accertamenti antimafia.
 *
 * @author Sara.Santi
 */
public class ImportaAntimafiaAction extends ActionBaseNoOpzioni {

  static Logger      logger = Logger.getLogger(ImportaAntimafiaAction.class);

  /** Manager generico per l'accesso ai dati */
  private SqlManager        sqlManager;
  private GeneManager       geneManager;
  private VersioneManager   versioneManager;


  /**
   * @param sqlManager
   *        sqlManager da settare internamente alla classe.
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   * @param geneManager
   *        geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  /**
   * @param versioneManager
   *        versioneManager da settare internamente alla classe.
   */
  public void setVersioneManager(VersioneManager versioneManager) {
    this.versioneManager = versioneManager;
  }


  /*
   * (non-Javadoc)
   *
   * @see
   * it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts
   * .action.ActionMapping, org.apache.struts.action.ActionForm,
   * javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    String messageKey = null;
    String target = null;
    TransactionStatus transazione = null;
    boolean aggiornamentoCompletato = false;

    ArrayList reportDitte = new ArrayList();
    VerificaInterdizioneAntimafia recordDitta = null;

    DatiAliceDocument document;
    UploadFileForm formXML = (UploadFileForm) form;

    try {
      document = DatiAliceDocument.Factory.parse(IOUtils.toString(formXML.getSelezioneFile().getInputStream(),"UTF-8"));
      DatiAliceType root = document. getDatiAlice();
      String tipoRichiesta = root.getTipo();

      if ("VERIFICA INTERDIZIONE".equals(tipoRichiesta)) {

        for (int i = 0; i < root.sizeOfOggettoArray(); i++) {
          OggettoType oggetto = root.getOggettoArray(i);

          //Legge i record dell'oggetto
          for (int j = 0; j < oggetto.sizeOfRecordArray(); j++) {
            RecordType record = oggetto.getRecordArray(j);
            String recordNome = record.getNome();
            //Non considera il primo record contenente la versione del DB sorgente
            if (!"ELDAVER".equals(recordNome)){
              String nomimp=null;
              String codiceFiscale=null;
              String partitaIva=null;
              String locimp=null;

              //Legge i campi del record
              for (int z = 0; z < record.sizeOfCampoArray(); z++) {

                CampoType campo = record.getCampoArray(z);
                String campoNome = campo.getNome();

                if ("NOMIMP".equals(campoNome)) {
                  nomimp = campo.getValore();
                } else if ("CFIMP".equals(campoNome)){
                  codiceFiscale =  campo.getValore();
                } else if ("PIVIMP".equals(campoNome)){
                  partitaIva =  campo.getValore();
                } else if ("LOCIMP".equals(campoNome)){
                  locimp = campo.getValore();
                }
              }

              List listDitta = null;
              boolean dittaNonInArchivio = false;

              //Verifica se la ditta è presente in DB ed eventualmente se è interdetta
              //Se sia il codice fiscale che la partita Iva sono nulli,si assume che la ditta non sia presente in archivio
              if (("".equals(codiceFiscale) || codiceFiscale == null) && ("".equals(partitaIva) || partitaIva == null)) {
                dittaNonInArchivio=true;
              } else {
                listDitta = sqlManager.getListVector(
                        "select nomimp, cfimp, pivimp, locimp, interd  from impr where cfimp=? or pivimp=?",
                        new Object[] {codiceFiscale, partitaIva});
                if (listDitta == null || listDitta.size() == 0) {
                  //Codice fiscale o partita Iva valorizzata ma la ditta non è presente in archivio
                  dittaNonInArchivio=true;
                }
              }

              if (dittaNonInArchivio) {
                recordDitta = new VerificaInterdizioneAntimafia();
                recordDitta.setNomimp(nomimp);
                recordDitta.setNomimp_db(null);
                recordDitta.setCfimp(codiceFiscale);
                recordDitta.setPivimp(partitaIva);
                recordDitta.setLocimp(locimp);
                recordDitta.setIn_archivio(2);
                recordDitta.setInterdetta(null);
                reportDitte.add(recordDitta);
              } else {
                  //Inserisce tutte le ditte presenti in DB con uguale codice fiscale o partita Iva
                  for (int t = 0; t < listDitta.size(); t++) {
                    recordDitta = new VerificaInterdizioneAntimafia();
                    List campiDitta = (Vector) listDitta.get(t);
                    recordDitta.setNomimp(nomimp);
                    recordDitta.setNomimp_db((String) ((JdbcParametro) campiDitta.get(0)).getValue());
                    recordDitta.setCfimp((String) ((JdbcParametro) campiDitta.get(1)).getValue());
                    recordDitta.setPivimp((String) ((JdbcParametro) campiDitta.get(2)).getValue());
                    recordDitta.setLocimp((String) ((JdbcParametro) campiDitta.get(3)).getValue());
                    recordDitta.setIn_archivio(1);
                    String tmp = (String)((JdbcParametro) campiDitta.get(4)).getValue();
                    if("1".equals(tmp)){
                      recordDitta.setInterdetta(Boolean.TRUE);
                    } else if ("2".equals(tmp)) {
                      recordDitta.setInterdetta(Boolean.FALSE);
                    } else {
                      recordDitta.setInterdetta(null);
                    }
                    reportDitte.add(recordDitta);
                  }
              }
            }
          }
        }
        request.setAttribute("listaDitte", reportDitte);
        target="successVerifica";

      } else if ("ACCERTAMENTO ANTIMAFIA".equals(tipoRichiesta)){

        //E' possibile importare solo se configurata la codifica automatica per l'anagrafica impresa e l'anagrafica tecnici impresa
        if (geneManager.isCodificaAutomatica("IMPR", "CODIMP") &&
              geneManager.isCodificaAutomatica("TEIM", "CODTIM")) {

          transazione = this.sqlManager.startTransaction();

          String codiceImpresaPrincipale=null;
          String nomeImpresa=null;
          String modalita=null;
          boolean codiceImpresaVuoto=false;
          boolean codiceTecnicoVuoto=false;
          String codiceFiscaleDitta=null;
          String partitaIvaDitta=null;
          String codiceFiscaleTecnico=null;
          boolean versioneDbDiversa=false;
          String versioneDbDestinazione=null;
          String versioneDbSorgente=null;

          //Legge gli oggetti
          for (int i = 0; i < root.sizeOfOggettoArray() && !codiceImpresaVuoto && !codiceTecnicoVuoto && !versioneDbDiversa; i++) {
            OggettoType oggetto = root.getOggettoArray(i);
            String codiceImpresa=null;
            String codiceImpresaOld=null;
            String impresaInterdetta=null;
            String codiceTecnico=null;
            String codiceTecnicoOld=null;

            //Legge i record dell'oggetto
            for (int j = 0; j < oggetto.sizeOfRecordArray() && !codiceImpresaVuoto && !codiceTecnicoVuoto && !versioneDbDiversa; j++) {
              RecordType record = oggetto.getRecordArray(j);
              String recordNome = record.getNome();
              Vector valoriCampi = new Vector();
              DefaultGestoreEntita gestore = new DefaultGestoreEntita(recordNome, request);

              //Legge i campi del record
              for (int z = 0; z < record.sizeOfCampoArray(); z++) {

                CampoType campo = record.getCampoArray(z);

                DataColumn campoColumn = creaCampo(campo, recordNome);
                valoriCampi.add(campoColumn);

              }
              DataColumnContainer contenitore = new DataColumnContainer(valoriCampi);

              if ("ELDAVER".equals(recordNome)) {
                versioneDbDestinazione = versioneManager.getVersione((String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO));
                versioneDbSorgente = contenitore.getString("NUMVER");
                //Visualizza msg di errore e interrompe l'import se il DB destinazione è diverso da quello sorgente
                if (versioneDbDestinazione != null && !"".equals(versioneDbDestinazione)
                    && versioneDbSorgente != null && !"".equals(versioneDbSorgente)
                    && versioneDbSorgente.equals(versioneDbDestinazione))
                  versioneDbDiversa=false;
                else
                  versioneDbDiversa=true;
              } else if ("TEIM".equals(recordNome)) {
                //Gestione inserimento archivio tecnici impresa
                //Verifica se il tecnico esiste già in archivio mediante il codice fiscale, altrimenti genera il codice con la codifica automatica
                //Se trova più tecnici con codice fiscale uguale, fallisce l'import
                codiceFiscaleTecnico = contenitore.getString("CFTIM");
                codiceTecnico = this.calcolaCodiceTecnico(codiceFiscaleTecnico);
                if (codiceTecnico == null || "".equals(codiceTecnico)){
                  codiceTecnicoVuoto=true;
                } else {
                  //Cancella l'occ. per poi reinserirla
                  try {
                    this.sqlManager.update("delete from teim where codtim=?", new Object[] { codiceTecnico});
                  } catch (SQLException e) {
                    throw new GestoreException(
                        "Errore durante l'eliminazione della riga della tabella TEIM", null,  e);
                  }
                  //Recupera il vecchio codice e aggiorna i tecnici dell'impresa
                  codiceTecnicoOld = contenitore.getString("CODTIM");
                  sqlManager.update("update impleg set codleg=? where codimp2=? and codleg=?",
                      new Object[] {codiceTecnico, codiceImpresa, codiceTecnicoOld});
                  sqlManager.update("update impdte set coddte=? where codimp3=? and coddte=?",
                      new Object[] {codiceTecnico, codiceImpresa, codiceTecnicoOld});
                  sqlManager.update("update impazi set codtec=? where codimp4=? and codtec=?",
                      new Object[] {codiceTecnico, codiceImpresa, codiceTecnicoOld});
                  sqlManager.update("update g_impcol set codtec=? where codimp=? and codtec=?",
                      new Object[] {codiceTecnico, codiceImpresa, codiceTecnicoOld});

                  contenitore.addColumn("TEIM.CODTIM", JdbcParametro.TIPO_TESTO, codiceTecnico);
                  gestore.inserisci(transazione, contenitore);
               }
              } else {
                //Gestione inserimento dell'impresa e delle entità figlie
                String campoChiave=null;
                if ("IMPR".equals(recordNome)) {
                  //Verifica se la ditta è già presente in archivio. Altrimenti ne genera il nuovo codice mediante la codifica automatica
                  //Si assume che il record IMPR sia sempre il primo nel file XML
                  codiceFiscaleDitta=contenitore.getString("CFIMP");
                  partitaIvaDitta=contenitore.getString("PIVIMP");
                  String[] datiImpresa = this.calcolaCodiceImpresa(codiceFiscaleDitta,partitaIvaDitta);
                  codiceImpresa = datiImpresa[0];
                  //Se il codice impresa è vuoto, significa che il codice fiscale/partita Iva non sono univoci e quindi blocca l'import
                  if (codiceImpresa == null || "".equals(codiceImpresa)){
                    codiceImpresaVuoto=true;
                  } else {
                    impresaInterdetta = datiImpresa[1];
                    //Cancella la ditta, se già presente in archivio, e le occ. figlie, in modo da sovrascriverne completamente i dati.
                    //Nel caso di IMPR non utilizza la funzione 'deleteTabelle' per evitare di cancellare i documenti associati o altro
                    try {
                      this.sqlManager.update("delete from impr where codimp=?", new Object[] { codiceImpresa});
                    } catch (SQLException e) {
                      throw new GestoreException(
                          "Errore durante l'eliminazione della riga della tabella IMPR", null,  e);
                    }
                    geneManager.deleteTabelle(new String[] { "IMPLEG" }, "codimp2 = ? ", new Object[] { codiceImpresa });
                    geneManager.deleteTabelle(new String[] { "IMPDTE" }, "codimp3 = ? ", new Object[] { codiceImpresa });
                    geneManager.deleteTabelle(new String[] { "IMPAZI" }, "codimp4 = ? ", new Object[] { codiceImpresa });
                    geneManager.deleteTabelle(new String[] { "IMPIND" }, "codimp5 = ? ", new Object[] { codiceImpresa });
                    geneManager.deleteTabelle(new String[] { "RAGIMP" }, "codime9 = ? ", new Object[] { codiceImpresa });
                    geneManager.deleteTabelle(new String[] { "G_IMPCOL" }, "codimp = ? ", new Object[] { codiceImpresa });
                    campoChiave="CODIMP";
                    //Aggiorna il contenitore con il valore del campo 'Interdetta' dell'occ. cancellata
                    contenitore.addColumn("IMPR.INTERD", JdbcParametro.TIPO_TESTO, impresaInterdetta);
                    if (i==1) {
                      //Memorizza codice e nome impresa principale (la prima riportata nel file xml) per il caso si tratti di un raggruppamento
                      codiceImpresaPrincipale = codiceImpresa;
                      nomeImpresa = contenitore.getString("NOMIMP");
                      //Memorizza se l'impresa principale è stata inserita o aggiornata per differenziare il messaggio finale
                      modalita = datiImpresa[2];
                     } else {
                        //Aggiorna i riferimenti in RAGIMP nel caso di impresa componente un raggruppamento
                        codiceImpresaOld = contenitore.getString("CODIMP");
                        sqlManager.update("update ragimp set coddic=? where codime9=? and coddic=?",
                                new Object[] {codiceImpresa, codiceImpresaPrincipale, codiceImpresaOld});
                     }
                  }
                } else if ("IMPLEG".equals(recordNome)) {
                  campoChiave="CODIMP2";
                } else if ("IMPDTE".equals(recordNome)) {
                  campoChiave="CODIMP3";
                } else if ("IMPAZI".equals(recordNome)) {
                  campoChiave="CODIMP4";
                } else if ("IMPIND".equals(recordNome)) {
                  campoChiave="CODIMP5";
                } else if ("RAGIMP".equals(recordNome)) {
                  campoChiave="CODIME9";
                } else if ("IMPANTIMAFIA".equals(recordNome)) {
                  campoChiave="CODIMP";
                } else if ("G_IMPCOL".equals(recordNome)){
                  campoChiave="CODIMP";
                }

                if (!codiceImpresaVuoto) {
                  contenitore.addColumn(recordNome + "." + campoChiave, JdbcParametro.TIPO_TESTO, codiceImpresa);
                  if ("IMPANTIMAFIA".equals(recordNome)) {
                    //Calcola il numero progressivo per la nuova occ. in IMPANTIMAFIA
                    DefaultGestoreEntitaChiaveNumerica gestore1 = new DefaultGestoreEntitaChiaveNumerica(recordNome, "NUMANT", new String[]{"CODIMP"}, request);
                    gestore1.preInsert(transazione, contenitore);
                  }
                  gestore.inserisci(transazione, contenitore);
                }
              }
            }
          }
          if (versioneDbDiversa) {
            target="errore";
            messageKey = "errors.importAntimafia.versioneDbDiversa";
            this.aggiungiMessaggio(request, messageKey, versioneDbDestinazione, versioneDbSorgente);
            logger.error(UtilityStringhe.replaceParametriMessageBundle(
                this.resBundleGenerale.getString(messageKey), new String[]{versioneDbDestinazione, versioneDbSorgente}));
          } else if (codiceImpresaVuoto) {
            target="errore";
            messageKey = "errors.importAntimafia.CfPiDittaNonUnivoco";
            this.aggiungiMessaggio(request, messageKey, codiceFiscaleDitta, partitaIvaDitta);
            logger.error(UtilityStringhe.replaceParametriMessageBundle(
                this.resBundleGenerale.getString(messageKey), new String[]{codiceFiscaleDitta, partitaIvaDitta}));
          } else if (codiceTecnicoVuoto) {
            target="errore";
            messageKey = "errors.importAntimafia.CfTecnicoNonUnivoco";
            this.aggiungiMessaggio(request, messageKey, codiceFiscaleTecnico);
            logger.error(UtilityStringhe.replaceParametriMessageBundle(
                this.resBundleGenerale.getString(messageKey), new String[] {codiceFiscaleTecnico}));
          } else {
            aggiornamentoCompletato = true;
            target="successAccertamento";
            if ("INS".equals(modalita))
              messageKey = "info.importAntimafia.dittaInserita";
            else
              messageKey = "info.importAntimafia.dittaAggiornata";
            this.aggiungiMessaggio(request, messageKey, nomeImpresa);
          }
       } else {
          //Visualizza msg di errore perchè non è configurata la codifica automatica per l'anagrafica impresa o l'anagrafica tecnici impresa
          target="errore";
          messageKey = "errors.importAntimafia.noCodificaAutomatica";
          this.aggiungiMessaggio(request, messageKey);
          logger.error(this.resBundleGenerale.getString(messageKey));
        }
      }

    } catch (XmlException e) {
      target="errore";
      messageKey = "errors.importAntimafia.letturafile";
      this.aggiungiMessaggio(request, messageKey);
    } catch (SQLException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      this.aggiungiMessaggio(request, messageKey);
      logger.error(this.resBundleGenerale.getString(messageKey), e);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    } finally {
      if (transazione != null) {
        try {
          if (aggiornamentoCompletato)
            sqlManager.commitTransaction(transazione);
          else
            sqlManager.rollbackTransaction(transazione);
          } catch (SQLException e1) {
          }
      }
    }

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }

  private String[] calcolaCodiceImpresa(String codiceFiscale, String partitaIva) throws GestoreException{
    String[] datiImpresa = new String[3];

    //Verifica se la ditta esiste già in archivio. Controllo su codice fiscale o partita IVA.
    //Se entrambi i campi sono nulli, l'impresa viene inserita
    if (("".equals(codiceFiscale) || codiceFiscale == null) && ("".equals(partitaIva) || partitaIva == null)) {
      //Calcola il nuovo codice impresa mediante la codifica automatica
      datiImpresa[0] = geneManager.calcolaCodificaAutomatica("IMPR", "CODIMP");
      datiImpresa[1] = null;
      datiImpresa[2] = "INS";
    } else {
      try {
        List listImpresa = sqlManager.getListVector(
              "select codimp,interd from impr where cfimp=? or pivimp=?", new Object[] { codiceFiscale, partitaIva});
        if(listImpresa!= null && listImpresa.size() > 0){
          //Se trova più occ. con uguale codice fiscale o partita iva restituisce l'array vuoto
          if (listImpresa.size() == 1) {
            datiImpresa[0] = (String) ((JdbcParametro) ((Vector) listImpresa.get(0)).get(0)).getValue();
            datiImpresa[1] = (String) ((JdbcParametro) ((Vector) listImpresa.get(0)).get(1)).getValue();
            datiImpresa[2] = "UPD";
          }
        } else {
          //Calcola il nuovo codice impresa mediante la codifica automatica
          datiImpresa[0] = geneManager.calcolaCodificaAutomatica("IMPR", "CODIMP");
          datiImpresa[1] = null;
          datiImpresa[2] = "INS";
        }

      } catch (SQLException e) {
        throw new GestoreException(
            "Errore durante la verifica dell'esistenza della ditta in archivio", null, e);
      }
    }

    return datiImpresa;
  }

  private String calcolaCodiceTecnico(String codiceFiscale) throws GestoreException{
    String codiceTecnico = null;

    //Verifica se il tecnico esiste già in archivio. Controllo su codice fiscale. Se nullo, il tecnico viene inserito
    if ("".equals(codiceFiscale) || codiceFiscale == null) {
      //Calcola il nuovo codice tecnico mediante la codifica automatica
      codiceTecnico = geneManager.calcolaCodificaAutomatica("TEIM", "CODTIM");
    } else {
      try {
        List listTecnici = sqlManager.getListVector(
              "select codtim from teim where cftim=?", new Object[] { codiceFiscale });
        if(listTecnici!= null && listTecnici.size() > 0){
          //Se trova più occ. con uguale codice fiscale restituisce l'array vuoto
          if (listTecnici.size() == 1) {
            codiceTecnico = (String) ((JdbcParametro) ((Vector) listTecnici.get(0)).get(0)).getValue();
          }
        } else {
          //Calcola il nuovo codice tecnico mediante la codifica automatica
          codiceTecnico = geneManager.calcolaCodificaAutomatica("TEIM", "CODTIM");
        }

      } catch (SQLException e) {
        throw new GestoreException(
            "Errore durante la verifica dell'esistenza del tecnico dell'impresa in archivio", null, e);
      }
    }

    return codiceTecnico;
  }

  /**
   * Crea il DataColumn dal Campo (xml)
   *
   * @param campo
   * @param tabella
   * @return
   * @throws GestoreException
   */
  private DataColumn creaCampo(CampoType campo, String record) {

    char tipo = campo.getTipo().charAt(0);
    String campoNome = campo.getNome();
    Object valoreCampo = null;

    switch (tipo) {
    case JdbcParametro.TIPO_DATA:
      valoreCampo = UtilityDate.convertiData(campo.getValore(),
          UtilityDate.FORMATO_GG_MM_AAAA);
      break;
    case JdbcParametro.TIPO_DECIMALE:
      valoreCampo = UtilityNumeri.convertiDouble(campo.getValore(),
          UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE);
      break;
    case JdbcParametro.TIPO_NUMERICO:
      valoreCampo = UtilityNumeri.convertiIntero(campo.getValore());
      break;
    case JdbcParametro.TIPO_TESTO:
      valoreCampo = campo.getValore();
      break;
    }

    JdbcParametro valore = new JdbcParametro(tipo, valoreCampo);

    DataColumn campoDb = new DataColumn(record + "." + campoNome, valore);

    if (campo.getChiave()) {
      campoDb.setChiave(true);
    }
    return campoDb;
  }


}


