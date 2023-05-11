/*
 * Created on 07-07-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.TempFileUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiRicezioneFunction;
import it.eldasoft.sil.pg.web.struts.UploadMultiploForm;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoType;
import it.maggioli.eldasoft.ws.erp.WSERPAllegatoType;

/**
 * Gestore per update dei dati della pagina della documentazione
 *
 * @author Marcello Caminiti
 */
public class GestoreDocumentazioneGara extends AbstractGestoreEntita {

  /** Logger */
  static Logger logger = Logger.getLogger(GestoreDocumentazioneGara.class);

  /** Manager per la gestione dell'integrazione con WSDM. */
  private GestioneWSDMManager gestioneWSDMManager;

  /** Manager per la gestione dell'integrazione con WSERP. */
  private GestioneWSERPManager gestioneWSERPManager;

  @Override
  public String getEntita() {
    return "GARE";
  }

  public GestoreDocumentazioneGara() {
    super(false);
  }

  public GestoreDocumentazioneGara(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    if (logger.isDebugEnabled()) {
      logger.debug("GestoreDocumentazioneGara: preUpdate: inizio metodo");
    }

    String tipoDoc = UtilityStruts.getParametroString(this.getRequest(),
        "filtroDocumentazione");

    String modoQform = UtilityStruts.getParametroString(this.getRequest(),
        "modoQform");

    if(modoQform!=null && !"".equals(modoQform)) {
      if("INSQFORM".equals(modoQform) || "INSQFORM_RETT".equals(modoQform)) {
        GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
            this.getServletContext(), GenChiaviManager.class);

        //GESTIONE QFORM
        Long idModello = datiForm.getLong("QFORM.IDMODELLO");
        try {
          if("INSQFORM".equals(modoQform)){
            String oggettoModello = (String)this.sqlManager.getObject("select oggetto from qformlib where id=?", new Object[] {idModello});
            datiForm.addColumn("QFORM.OGGETTO", JdbcParametro.TIPO_TESTO, oggettoModello);
          }else {
            String archivioMod = UtilityStruts.getParametroString(this.getRequest(),"archivioMod");
            if("SI".equals(archivioMod)) {
              String oggettoModello = (String)this.sqlManager.getObject("select oggetto from qformlib where id=?", new Object[] {idModello});
              datiForm.setValue("QFORM.OGGETTO", oggettoModello);
            }
            datiForm.setOriginalValue("QFORM.OGGETTO", new JdbcParametro(JdbcParametro.TIPO_TESTO,""));
          }
          Long id = new Long(genChiaviManager.getNextId("QFORM"));

          //datiForm.setValue("QFORM.OGGETTO", oggettoModello);
          datiForm.setValue("QFORM.ID", id);
          datiForm.setOriginalValue("QFORM.ENTITA", new JdbcParametro(JdbcParametro.TIPO_TESTO,""));
          datiForm.setOriginalValue("QFORM.KEY1", new JdbcParametro(JdbcParametro.TIPO_TESTO,""));
          datiForm.setOriginalValue("QFORM.DESCRIZIONE", new JdbcParametro(JdbcParametro.TIPO_TESTO,""));
          datiForm.setOriginalValue("QFORM.TITOLO", new JdbcParametro(JdbcParametro.TIPO_TESTO,""));
          datiForm.setOriginalValue("QFORM.BUSTA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,null));
          datiForm.setOriginalValue("QFORM.STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,null));
          datiForm.setOriginalValue("QFORM.TIPOLOGIA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,null));
          datiForm.setOriginalValue("QFORM.IDMODELLO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,null));
          String dultaggmod = datiForm.getString("DULTAGGMOD_FIT");
          if(dultaggmod!=null && !"".equals(dultaggmod)) {
            Date d= UtilityDate.convertiData(dultaggmod,UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
            datiForm.addColumn("QFORM.DULTAGGMOD", d);
          }
          datiForm.removeColumns(new String[] {"DULTAGGMOD_FIT"});
          datiForm.insert("QFORM", sqlManager);
          this.getRequest().setAttribute("RISULTATO", "OK");
          if("INSQFORM_RETT".equals(modoQform)) {
            this.getRequest().setAttribute("idQformRett", id);

          }

          Long busta = datiForm.getLong("QFORM.BUSTA");
          if(new Long(1).equals(busta) || new Long(4).equals(busta)) {
            //Va cancellato il documento DGUE
            String ngara=datiForm.getString("QFORM.KEY1");
            String codgar = (String)this.sqlManager.getObject("select codgar1 from gare where ngara=?",new Object[] {ngara});
            this.sqlManager.update("delete from documgara where codgar=? and gruppo=3 and busta=? and IDSTAMPA = 'DGUE' ", new Object[] {codgar,busta});
          }
        } catch (SQLException e) {
          throw new GestoreException("Errore nell'inserimento del formulario", null, e);
        }
      }else {
        if(datiForm.isModifiedTable("QFORM")) {
          try {
            datiForm.update("QFORM", sqlManager);
          } catch (SQLException e) {
            throw new GestoreException("Errore nell'aggiornamento del formulario", null, e);
          }
        }
      }

    }else {
      String suffisso = null;

      if (tipoDoc != null) {
        if ("1".equals(tipoDoc)) {
          suffisso = "DOCUMGARA";
        } else if ("2".equals(tipoDoc)) {
          suffisso = "DOCUMREQ";
        } else if ("3".equals(tipoDoc) || "8".equals(tipoDoc)) {
          suffisso = "DOCUMCONC";
        } else if ("4".equals(tipoDoc)) {
          suffisso = "DOCUMESITO";
        } else if ("5".equals(tipoDoc)) {
          suffisso = "DOCUMTRASP";
        } else if ("6".equals(tipoDoc) || "12".equals(tipoDoc)) {
          suffisso = "DOCUMINVITI";
        }else if ("10".equals(tipoDoc) || "15".equals(tipoDoc)) {
          suffisso = "ATTI";
        }else if ("11".equals(tipoDoc)) {
          suffisso = "ALLEGATI";
        }
      }

      // Gestione personalizzata delle sezioni dinamiche
      AbstractGestoreChiaveNumerica gestoreDOCUMGARA = new DefaultGestoreEntitaChiaveNumerica(
          "DOCUMGARA", "NORDDOCG", new String[] {"CODGAR" }, this.getRequest());
      this.gestisciAggiornamentiRecordSchedaMultiplaDOCUMGARA(status, datiForm,
          gestoreDOCUMGARA, suffisso,
          new DataColumn[] {datiForm.getColumn("GARE.CODGAR1") }, null);

      String codgar = datiForm.getString("GARE.CODGAR1");

      PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
          this.getServletContext(), PgManager.class);

      // Il gestore viene adoperato dalla pagina "Documentazione di gara" e
      // dallo step invito delle fasi ricezione.
      // In quest'ultimo nella stessa pagina nel caso di offerte distinte si
      // hanno i documenti dei concorrenti relativi
      // alla gara e relativi ai lotti, entrambi appartententi al gruppo 3, ma
      // per comodità è stato definito il tipoDoc=8
      // per indicare i documenti dei concorrenti relativi al lotto, ma che in
      // db corrispondono sempre a gruppo=3
      if ("3".equals(tipoDoc) || "8".equals(tipoDoc)) {


        // Cancellazione delle occorrenze di imprdocg che non hanno
        // corrispondenza in DOCUMGARA

        String ngara = null;
        // List listaLotti = null;

        // Nel caso di chiamata dalla pagina torn-pg-documentazione.jsp
        // (offerta unica e offerte distinte) non è
        // presente il campo "GARE.NGARA", quindi ngara rimane null
        // Nel caso della pagina inviti delle fasi ricezione, il campo
        // GARE.NGARA è valorizzato anche nei casi di offerta unica
        // e offerte distine, quindi lo si deve sbiancare in questi casi
        if (datiForm.isColumn("GARE.NGARA")) {
          ngara = datiForm.getString("GARE.NGARA");
          if (datiForm.isColumn("WIZARD_PAGINA_ATTIVA") && !"8".equals(tipoDoc)) {
            // Se tipodoc=8, si sta considerando la documentazione del
            // lotto ed è giusto che NGARA rimanga valorizzato
            ngara = null;
          }
        }

        pgManager.updateImprdocgDaDocumgara(codgar, ngara);
      }

      ////////////////////////////////////////////////////////////////////////////////
      //Ricalcolo NUMORD.DOCUMGARA
      Long gruppo = new Long(tipoDoc);
      PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
      this.getServletContext(), PgManagerEst1.class);

      pgManagerEst1.ricalcNumordDocGara(codgar, gruppo);
      ////////////////////////////////////////////////////////////////////////////////

    //Gestione integrazione MDGUE
      String urlMDGUE = ConfigManager.getValore(CostantiAppalti.PROP_INTEGRAZIONE_MDGUE_URL);
      if(urlMDGUE!=null && !"".equals(urlMDGUE) && ("1".equals(tipoDoc) || "6".equals(tipoDoc))) {
        boolean saltareControllo=false;
        //Non deve scattare il controllo nello step "Invito" delle fasi ricezione per nella sottopagina "Dati dell'invito"
        if("1".equals(tipoDoc)) {
          int stepAttivo =0;
          if(UtilityStruts.getParametroString(
              this.getRequest(), GestioneFasiGaraFunction.PARAM_WIZARD_PAGINA_ATTIVA) !=null &&
              !"".equals(UtilityStruts.getParametroString(
                  this.getRequest(), GestioneFasiGaraFunction.PARAM_WIZARD_PAGINA_ATTIVA))) {
            stepAttivo = new Long(UtilityStruts.getParametroString(
                this.getRequest(), GestioneFasiGaraFunction.PARAM_WIZARD_PAGINA_ATTIVA)).intValue();
            if(GestioneFasiRicezioneFunction.FASE_INVITI==stepAttivo)
              saltareControllo=true;
          }

        }
        try {
          if(!saltareControllo) {
            Vector<?> datiTorn = this.sqlManager.getVector("select gartel, offtel, iterga from torn where codgar=?", new Object[] {codgar});
            if(datiTorn != null && datiTorn.size() >0) {
              String gartel = SqlManager.getValueFromVectorParam(datiTorn, 0).getStringValue();
              Long offtel = SqlManager.getValueFromVectorParam(datiTorn, 1).longValue();
              Long iterga = SqlManager.getValueFromVectorParam(datiTorn, 2).longValue();
              if("1".equals(gartel) && !(new Long (3)).equals(offtel)) {
                Long gruppoDGUE= new Long(1);
                if("6".equals(tipoDoc))
                  gruppoDGUE = new Long(6);
                Long busta= new Long(1);
                if((new Long(2).equals(iterga) || new Long(4).equals(iterga)) && "1".equals(tipoDoc))
                  busta= new Long(4);

                String ngara = null;
                if (datiForm.isColumn("GARE.NGARA"))
                  ngara = datiForm.getString("GARE.NGARA");

                pgManagerEst1.gestioneDocDGUEConcorrenti(codgar, ngara, gruppoDGUE, busta, false);

                if (datiForm.isColumn("WIZARD_PAGINA_ATTIVA") && !"8".equals(tipoDoc)) {
                  // Se tipodoc=8, si sta considerando la documentazione del
                  // lotto ed è giusto che NGARA rimanga valorizzato
                  ngara = null;
                }
                pgManager.updateImprdocgDaDocumgara(codgar, ngara);

              }
            }
          }
        } catch (SQLException e) {
          throw new GestoreException("Errore nell'inserimento dell'occorrenza dei documenti dei concorrenti per MDGUE", null, e);
        }
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debug("GestoreDocumentazioneGara: preUpdate: fine metodo");
    }
  }


  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

  /**
   * Gestisce le operazioni di update, insert, delete dei dettagli dei record di una scheda multipla sull'entità DOCUMGARE e.<br>
   * Questo codice funziona se si presuppone di avere una pagina con un elenco di record a scheda multipla,
   * <ul>
   * <li>la cui chiave è una parte fissa comune più un progressivo</li>
   * <li>i dati da aggiornare sono appartenenti ad una sola entità</li>
   * </ul>
   *
   * @param status
   *        stato della transazione
   * @param dataColumnContainer
   *        container di partenza da cui filtrare i record
   * @param gestore
   *        gestore a chiave numerica per l'aggiornamento del record di una scheda multipla
   * @param suffisso
   *        suffisso da concatenare a "NUMERO_" per ottenere il campo che indica il numero di occorrenze presenti nel container
   * @param valoreChiave
   *        parte non numerica della chiave dell'entità, per la valorizzazione in fase di inserimento se i dati non sono presenti nel
   *        container
   * @param campiDaNonAggiornare
   *        elenco eventuale di ulteriori campi fittizi da eliminare prima di eseguire l'aggiornamento nel DB
   *
   * @throws GestoreException
   */
  public void gestisciAggiornamentiRecordSchedaMultiplaDOCUMGARA(
      TransactionStatus status, DataColumnContainer dataColumnContainer,
      AbstractGestoreChiaveNumerica gestore, String suffisso,
      DataColumn[] valoreChiave, String[] campiDaNonAggiornare)
      throws GestoreException {

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
        "tabellatiManager", this.getServletContext(), TabellatiManager.class);

    gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        this.getServletContext(), GestioneWSDMManager.class);

    gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
            this.getServletContext(), GestioneWSERPManager.class);

    String nomeCampoNumeroRecord = "NUMERO_" + suffisso;
    String nomeCampoDelete = "DEL_" + suffisso;
    String nomeCampoMod = "MOD_" + suffisso;

    // Gestione delle pubblicazioni bando solo se esiste la colonna con il
    // numero di occorrenze
    if (dataColumnContainer.isColumn(nomeCampoNumeroRecord)) {

      // Estraggo dal dataColumnContainer tutte le occorrenze dei campi
      // dell'entità definita per il gestore
      DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(
          dataColumnContainer.getColumns(gestore.getEntita(), 0));

      int numeroRecord = dataColumnContainer.getLong(nomeCampoNumeroRecord).intValue();

      // Ciclo per determinare la dimensione totale dei file da uploadare
      HashMap hm = ((UploadMultiploForm) this.getForm()).getFormFiles();
      long dimTotale = 0;
      String nomeAllegatoDocumentale = null;
      String nomeAllegatoWSERP = null;
      FormFile ff = null;
      String fname = null;
      for (int i = 1; i <= numeroRecord; i++) {
        ff = (FormFile) hm.get(new Long(i));
        if (ff != null && ff.getFileSize() > 0) {
          dimTotale += ff.getFileSize();
        }
      }

      // Aggiungo i file generati automaticamente
      // per ora solo per i documenti di invito
      if ("DOCUMINVITI".equals(suffisso)) {
        for (int i = 1; i <= numeroRecord; i++) {
          String nomeFileGenerato = dataColumnContainer.getString("NOMEDOCGEN_"
              + i);
          if (StringUtils.isNotBlank(nomeFileGenerato)) {
            try {
              byte[] fileGenerato = TempFileUtilities.getTempFile(nomeFileGenerato);
              if (fileGenerato != null && fileGenerato.length > 0) {
                dimTotale += fileGenerato.length;
              }
            } catch (IOException ex) {
              throw new GestoreException(
                  "Si è verificato un problema nella lettura del file temporaneo "
                      + nomeFileGenerato, "uploadMultiplo.fileTempNonTrovato",
                  new String[] {nomeFileGenerato }, ex);
            }
          }
        }
      }

      String dimTotaleTabellatoStringa = tabellatiManager.getDescrTabellato(
          "A1072", "1");
      if (dimTotaleTabellatoStringa == null
          || "".equals(dimTotaleTabellatoStringa)) {
        throw new GestoreException(
            "Non è presente il tabellato A1072 per determinare la dimensione "
                + "massima totale dell'upload dei file",
            "uploadMultiplo.noTabellato", null);
      }
      int pos = dimTotaleTabellatoStringa.indexOf("(");
      if (pos < 1) {
        throw new GestoreException(
            "Non è possibile determinare dal tabellato A1072 la dimensione "
                + "massima totale dell'upload dei file",
            "uploadMultiplo.noValore", null);
      }
      dimTotaleTabellatoStringa = dimTotaleTabellatoStringa.substring(0,
          pos - 1);
      dimTotaleTabellatoStringa = dimTotaleTabellatoStringa.trim();
      double dimTotaleTabellatoByte = Math.pow(2, 20)
          * Double.parseDouble(dimTotaleTabellatoStringa);
      if (dimTotale > dimTotaleTabellatoByte) {
        throw new GestoreException(
            "La dimensione totale dei file da salvare ha superato il limite consentito "
                + "di "
                + dimTotaleTabellatoStringa
                + " MB", "uploadMultiplo.overflowMultiplo",
            new String[] {dimTotaleTabellatoStringa }, null);
      }

      for (int i = 1; i <= numeroRecord; i++) {

        DataColumnContainer newDataColumnContainer = new DataColumnContainer(
            tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

        boolean deleteOccorrenza = newDataColumnContainer.isColumn(nomeCampoDelete)
            && "1".equals(newDataColumnContainer.getString(nomeCampoDelete));
        boolean updateOccorrenza = newDataColumnContainer.isColumn(nomeCampoMod)
            && "1".equals(newDataColumnContainer.getString(nomeCampoMod));

        // Rimozione dei campi fittizi (il campo per la marcatura della
        // delete e
        // tutti gli eventuali campi passati come argomento)
        newDataColumnContainer.removeColumns(new String[] {
            gestore.getEntita() + "." + nomeCampoDelete,
            gestore.getEntita() + "." + nomeCampoMod });

        if (campiDaNonAggiornare != null) {
          for (int j = 0; j < campiDaNonAggiornare.length; j++) {
            campiDaNonAggiornare[j] = gestore.getEntita()
                + "."
                + campiDaNonAggiornare[j];
          }
          newDataColumnContainer.removeColumns(campiDaNonAggiornare);
        }

        if (deleteOccorrenza) {
          // Se è stata richiesta l'eliminazione e il campo chiave
          // numerica e'
          // diverso da null eseguo l'effettiva eliminazione del
          // record
          if (newDataColumnContainer.getLong(gestore.getCampoNumericoChiave()) != null) {
            // Devo cancellare l'occorrenza in W_DOCDIG
            try {
              this.getSqlManager().update(
                  "delete from W_DOCDIG where IDPRG = ? and IDDOCDIG = ?",
                  new Object[] {"PG",
                      newDataColumnContainer.getLong("DOCUMGARA.IDDOCDG") });
              this.getSqlManager().update(
                  "delete from WSALLEGATI where KEY2 = ?",
                  new Object[] {newDataColumnContainer.getLong("DOCUMGARA.IDDOCDG") });
            } catch (SQLException e) {
              throw new GestoreException(
                  "Errore nella cancellazione di W_DOCDIG", null, e);
            }

            gestore.elimina(status, newDataColumnContainer);
            // altrimenti e' stato eliminato un nuovo record non
            // ancora inserito
            // ma predisposto nel form per l'inserimento
          }
        } else {
          if (updateOccorrenza) {
            // si settano tutti i campi chiave con i valori
            // ereditati dal
            // chiamante
            for (int z = 0; z < gestore.getAltriCampiChiave().length; z++) {
              if (newDataColumnContainer.getColumn(
                  gestore.getAltriCampiChiave()[z]).getValue().getValue() == null) {
                newDataColumnContainer.getColumn(
                    gestore.getAltriCampiChiave()[z]).setValue(
                    valoreChiave[z].getValue());
              }
            }

            if(newDataColumnContainer.isColumn("DOCUMGARA.URLDOC")
                && newDataColumnContainer.getString("DOCUMGARA.URLDOC") != null && !"".equals(newDataColumnContainer.getString("DOCUMGARA.URLDOC"))){
              String url = newDataColumnContainer.getString("DOCUMGARA.URLDOC");
              if(!PgManager.validazioneURL(url)){
                throw new GestoreException("Errore nella validazione dell'Url del documento", "upload.validate.wrongUrlFormat");
              }

            }

            if (newDataColumnContainer.getLong(gestore.getCampoNumericoChiave()) == null) {
              gestore.inserisci(status, newDataColumnContainer);
              // operazione che ha senso solo per i gruppi 1,3 e 4
              // :
              if (new Long(1).equals(newDataColumnContainer.getLong("DOCUMGARA.GRUPPO"))
                  | new Long(3).equals(newDataColumnContainer.getLong("DOCUMGARA.GRUPPO"))
                  | new Long(4).equals(newDataColumnContainer.getLong("DOCUMGARA.GRUPPO"))
                  | new Long(5).equals(newDataColumnContainer.getLong("DOCUMGARA.GRUPPO"))
                  | new Long(6).equals(newDataColumnContainer.getLong("DOCUMGARA.GRUPPO"))
                  | new Long(10).equals(newDataColumnContainer.getLong("DOCUMGARA.GRUPPO"))
                  | new Long(11).equals(newDataColumnContainer.getLong("DOCUMGARA.GRUPPO"))
                  | new Long(12).equals(newDataColumnContainer.getLong("DOCUMGARA.GRUPPO"))
                  | new Long(15).equals(newDataColumnContainer.getLong("DOCUMGARA.GRUPPO"))) {

                // Gestione w_DOCDIG
                long numDoc = this.gestioneW_DOCDIG(newDataColumnContainer,
                    dataColumnContainer, i, suffisso);
                // Valorizzo il campo IDDOCDG.DOCUMGARA con
                // IDDOCDIG.W_DOCDIG
                if (numDoc > 0) {
                  String select = "select max(NORDDOCG) from DOCUMGARA where CODGAR=?";
                  try {
                    Long nProgressivoDOCUMGARA = (Long) this.getSqlManager().getObject(
                        select,
                        new Object[] {valoreChiave[0].getValue().stringValue() });
                    if (nProgressivoDOCUMGARA != null
                        && nProgressivoDOCUMGARA.longValue() > 0) {
                      this.getSqlManager().update(
                          "update DOCUMGARA set IDDOCDG = ? where CODGAR=? and NORDDOCG=?",
                          new Object[] {new Long(numDoc),
                              valoreChiave[0].getValue().stringValue(),
                              nProgressivoDOCUMGARA });

                      // Valorizzo DIGENT.W_DOCDIG con
                      // "DOCUMGARA", e i campi
                      // DIGKEY1.W_DOCDIG,
                      // DIGKEY2.W_DOCDIG,
                      // DIGKEY3.W_DOCDIG
                      // con i valori dei campi chiave di
                      // DOCUMGARA
                      this.getSqlManager().update(
                          "update W_DOCDIG set DIGENT=?, DIGKEY1=?, DIGKEY2=? where IDPRG=? and IDDOCDIG=?",
                          new Object[] {"DOCUMGARA",
                              valoreChiave[0].getValue().stringValue(),
                              nProgressivoDOCUMGARA.toString(), "PG",
                              new Long(numDoc) });
                    }

                  } catch (SQLException e) {
                    throw new GestoreException(
                        "Errore nel valorizzare il riferimento a W_DOCDIG in DOCUMGARA",
                        null, e);
                  }
                }

              }
            } else {
              //Se è stato cambiato il valore del campo NGARA si cancellano le relative occorrenze della tabella IMPRDOCG
              //perchè significa che prima le il documento era associato ad un lotto, modificando NGARA lo si vuole associare
              //ad un solo lotto. La gestione dell'inserimento in IMPRDOCG viene effettuato con updateImprdocgDaDocumgara
              if(newDataColumnContainer.isColumn("DOCUMGARA.NGARA") && newDataColumnContainer.isModifiedColumn("DOCUMGARA.NGARA")){
                String codgar = newDataColumnContainer.getString("DOCUMGARA.CODGAR");
                Long norddocg = newDataColumnContainer.getLong("DOCUMGARA.NORDDOCG");
                try {
                  this.getSqlManager().update(
                      "delete from IMPRDOCG where CODGAR = ? and NORDDOCI = ? and PROVENI = ?",
                      new Object[] {codgar, norddocg, new Long(1)});

                } catch (SQLException e) {
                  throw new GestoreException(
                      "Errore nella cancellazione di IMPRDOCG", null, e);
                }

              }

              gestore.update(status, newDataColumnContainer);
              // operazione che ha senso solo per i gruppi 1,3 e 4
              // :
              if (new Long(1).equals(newDataColumnContainer.getLong("DOCUMGARA.GRUPPO"))
                  | new Long(3).equals(newDataColumnContainer.getLong("DOCUMGARA.GRUPPO"))
                  | new Long(4).equals(newDataColumnContainer.getLong("DOCUMGARA.GRUPPO"))
                  | new Long(5).equals(newDataColumnContainer.getLong("DOCUMGARA.GRUPPO"))
                  | new Long(6).equals(newDataColumnContainer.getLong("DOCUMGARA.GRUPPO"))
                  | new Long(10).equals(newDataColumnContainer.getLong("DOCUMGARA.GRUPPO"))
                  | new Long(11).equals(newDataColumnContainer.getLong("DOCUMGARA.GRUPPO"))
                  | new Long(12).equals(newDataColumnContainer.getLong("DOCUMGARA.GRUPPO"))
                  | new Long(15).equals(newDataColumnContainer.getLong("DOCUMGARA.GRUPPO"))) {

                // Se è valorizzato il campo DOCUMGARA.URLDOC si
                // deve cancellare l'occorrenza in W_DOCDIG
                ff = (FormFile) hm.get(new Long(i));
                if (ff != null) {
                  fname = ff.getFileName();
                }
                nomeAllegatoDocumentale = this.getRequest().getParameter("getdocumentoallegato_nomeallegato_" + i);
                nomeAllegatoWSERP = this.getRequest().getParameter("getdocumentoallegatoWSERP_nomeallegato_" + i);

                Long idDocumento = dataColumnContainer.getLong("W_DOCDIG.IDDOCDIG_"
                    + i);
                boolean cancellareAllegato = false;
                if (idDocumento == null) {
                  if (dataColumnContainer.getColumn("W_DOCDIG.IDDOCDIG_" + i).getOriginalValue().longValue() != null) {
                    cancellareAllegato = true;
                  }
                }
                if ((newDataColumnContainer.isColumn("DOCUMGARA.URLDOC")
                    && newDataColumnContainer.getString("DOCUMGARA.URLDOC") != null && !"".equals(newDataColumnContainer.getString("DOCUMGARA.URLDOC")))
                    || (newDataColumnContainer.getLong("DOCUMGARA.IDDOCDG") != null
                        && (fname == null || fname.length() == 0) && (nomeAllegatoDocumentale == null || nomeAllegatoDocumentale.length() == 0) && (nomeAllegatoWSERP == null || nomeAllegatoWSERP.length() == 0) && cancellareAllegato)) {
                  try {
                    this.getSqlManager().update(
                        "delete from W_DOCDIG where IDPRG = ? and IDDOCDIG = ?",
                        new Object[] {"PG",
                            newDataColumnContainer.getLong("DOCUMGARA.IDDOCDG") });
                    this.getSqlManager().update(
                        "delete from WSALLEGATI where KEY2 = ?",
                        new Object[] {newDataColumnContainer.getLong("DOCUMGARA.IDDOCDG") });
                    this.getSqlManager().update(
                        "update DOCUMGARA set IDDOCDG = null where CODGAR=? and NORDDOCG =?",
                        new Object[] {
                            newDataColumnContainer.getString("DOCUMGARA.CODGAR"),
                            newDataColumnContainer.getLong("DOCUMGARA.NORDDOCG") });

                  } catch (SQLException e) {
                    throw new GestoreException(
                        "Errore nella cancellazione di W_DOCDIG", null, e);
                  }
                } else {

                	if(cancellareAllegato && (fname.length() > 0 || (nomeAllegatoDocumentale != null && nomeAllegatoDocumentale.length() > 0) || (nomeAllegatoWSERP != null && nomeAllegatoWSERP.length() > 0)) && (dataColumnContainer.getString("W_DOCDIG.DIGNOMDOC_" + i) != null && !"".equals(dataColumnContainer.getString("W_DOCDIG.DIGNOMDOC_" + i)))){
                    dataColumnContainer.getColumn("W_DOCDIG.IDDOCDIG_" + i).setValue(dataColumnContainer.getColumn("W_DOCDIG.IDDOCDIG_" + i).getOriginalValue());
                  }
                  // Gestione w_DOCDIG
                  long numDoc = this.gestioneW_DOCDIG(newDataColumnContainer,
                      dataColumnContainer, i, suffisso);
                  if (numDoc > 0) {

                    Long nProgressivoDOCUMGARA = newDataColumnContainer.getLong("DOCUMGARA.NORDDOCG");

                    if (nProgressivoDOCUMGARA != null
                        && nProgressivoDOCUMGARA > 0) {

                      newDataColumnContainer.getColumn("DOCUMGARA.IDDOCDG").setObjectOriginalValue(
                          null);
                      newDataColumnContainer.setValue("DOCUMGARA.IDDOCDG",
                          new Long(numDoc));
                      gestore.update(status, newDataColumnContainer);

                      // Valorizzo DIGENT.W_DOCDIG con
                      // "DOCUMGARA", e i campi
                      // DIGKEY1.W_DOCDIG,
                      // DIGKEY2.W_DOCDIG,
                      // DIGKEY3.W_DOCDIG
                      // con i valori dei campi chiave di
                      // DOCUMGARA
                      try {
                        this.getSqlManager().update(
                            "update W_DOCDIG set DIGENT=?, DIGKEY1=?, DIGKEY2=? where IDPRG=? and IDDOCDIG=?",
                            new Object[] {"DOCUMGARA",
                                valoreChiave[0].getValue().stringValue(),
                                nProgressivoDOCUMGARA.toString(), "PG",
                                new Long(numDoc) });
                      } catch (SQLException e) {
                        throw new GestoreException(
                            "Errore nel valorizzare il riferimento a DOCUMGARA in W_DOCDIG",
                            null, e);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
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
  private long gestioneW_DOCDIG(DataColumnContainer datiForm,
      DataColumnContainer datiFormOriginale, int indice, String suffisso)
      throws GestoreException {

    long ret = 0;
    byte[] file = null;
    String nomeFile = null;

    String nomeallegatodocumentale = null;
    byte contenutoallegatodocumentale[] = null;
    Long idDocumentale = null;

    String nomeallegato = this.getRequest().getParameter("getdocumentoallegato_nomeallegato_" + indice);
    if(nomeallegato!= null && nomeallegato.length()>0){
      String username = this.getRequest().getParameter("getdocumentoallegato_username_" + indice);
      String password = this.getRequest().getParameter("getdocumentoallegato_password_" + indice);
      String ruolo = this.getRequest().getParameter("getdocumentoallegato_ruolo_" + indice);
      String annoprotocollostringa = this.getRequest().getParameter("getdocumentoallegato_annoprotocollo_" + indice);
      Long annoprotocollo = null;
      if(annoprotocollostringa != null && annoprotocollostringa.length()>0){annoprotocollo = new Long(annoprotocollostringa);}
      String numeroprotocollo = this.getRequest().getParameter("getdocumentoallegato_numeroprotocollo_" + indice);

      String nome = this.getRequest().getParameter("getdocumentoallegato_nome_" + indice);
      String cognome = this.getRequest().getParameter("getdocumentoallegato_cognome_" + indice);
      String codiceuo = this.getRequest().getParameter("getdocumentoallegato_codiceuo_" + indice);
      String idutente = this.getRequest().getParameter("getdocumentoallegato_idutente_" + indice);
      String idutenteunop = this.getRequest().getParameter("getdocumentoallegato_idutenteunop_" + indice);
      String tipoallegato = this.getRequest().getParameter("getdocumentoallegato_tipoallegato_" + indice);
      String numerodocumento = this.getRequest().getParameter("getdocumentoallegato_numerodocumento_" + indice);
      String servizio = this.getRequest().getParameter("getdocumentoallegato_servizio_" + indice);
      String idconfi = this.getRequest().getParameter("idconfi");

      WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes;

      if(numeroprotocollo != null && numeroprotocollo.length()>0 && annoprotocollo != null){
          wsdmProtocolloDocumentoRes = gestioneWSDMManager.wsdmProtocolloLeggi(username, password, ruolo,
          nome, cognome, codiceuo, idutente, idutenteunop, annoprotocollo, numeroprotocollo, servizio, idconfi);
      }else{
          wsdmProtocolloDocumentoRes = gestioneWSDMManager.wsdmDocumentoLeggi(username, password, ruolo,
          nome, cognome, codiceuo, idutente, idutenteunop, numerodocumento, servizio, idconfi);
      }

      if (wsdmProtocolloDocumentoRes.isEsito()) {
        WSDMProtocolloDocumentoType wsdmProtocolloDocumento = wsdmProtocolloDocumentoRes.getProtocolloDocumento();
        if(numerodocumento == null || numerodocumento.length() == 0){
          numerodocumento = wsdmProtocolloDocumento.getNumeroDocumento();
        }
        try {
          idDocumentale = (Long) this.getSqlManager().getObject("select id from WSDOCUMENTO where NUMERODOC = ? ", new Object[]{numerodocumento});
        } catch (SQLException e) {
            throw new GestoreException(
              "Errore nella lettura dell'id del documento", null, e);
        }
        if (wsdmProtocolloDocumento != null) {
          if (wsdmProtocolloDocumento.getAllegati() != null) {
            WSDMProtocolloAllegatoType[] allegati = wsdmProtocolloDocumento.getAllegati();
            for (int a = 0; a < allegati.length; a++) {
              if (nomeallegato.equals(allegati[a].getNome()) && tipoallegato.equals(allegati[a].getTipo())) {
                nomeallegatodocumentale = allegati[a].getNome();
                contenutoallegatodocumentale =  allegati[a].getContenuto();
              }
            }
          }
        }
      }else{
        throw new GestoreException(
            "Errore nell'interrogazione del servizio", wsdmProtocolloDocumentoRes.getMessaggio());
      }
    }

    String nomeallegatoWSERP = this.getRequest().getParameter("getdocumentoallegatoWSERP_nomeallegato_" + indice);
    if(nomeallegatoWSERP!= null && nomeallegatoWSERP.length()>0){

        String servizioWSERP ="WSERP";

        ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
                CostantiGenerali.PROFILO_UTENTE_SESSIONE);
            Long syscon = new Long(profilo.getId());
            String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizioWSERP);

            String usernameWSERP = credenziali[0];
            String passwordWSERP = credenziali[1];

        String titoloallegatoWSERP = this.getRequest().getParameter("getdocumentoallegatoWSERP_titoloallegato_" + indice);
        String idfileWSERP = this.getRequest().getParameter("getdocumentoallegatoWSERP_idfile_" + indice);

        WSERPAllegatoType allegato = gestioneWSERPManager.wserpLeggiAllegato(usernameWSERP, passwordWSERP, nomeallegatoWSERP, servizioWSERP, idfileWSERP);

        nomeallegatodocumentale = allegato.getNome();
        contenutoallegatodocumentale =  allegato.getContenuto();
    }

    try {
      Long numDoc = datiForm.getLong("DOCUMGARA.IDDOCDG");
      boolean inserimento = true;

      if (numDoc != null && numDoc > 0) {
        inserimento = false;
      }

      DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(
          datiFormOriginale.getColumns("W_DOCDIG", 0));

      DataColumnContainer newDataColumnContainer = new DataColumnContainer(
          tmpDataColumnContainer.getColumnsBySuffix("_" + indice, false));

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

      // Aggiungo i file generati automaticamente
      // per ora solo per i documenti di invito
      if ("DOCUMINVITI".equals(suffisso)) {
        nomeFile = datiFormOriginale.getString("NOMEDOCGEN_" + indice);
      }
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
        //Nel caso l'allegato sia stato scaricato dal documentale
        if(nomeallegatodocumentale != null ){
          nomeFile = nomeallegatodocumentale;
          if (contenutoallegatodocumentale != null) {
            file = contenutoallegatodocumentale;
              controlliFile = checkFileToAdd(nomeFile, file);
          } else {
            salvareW_DOCDIG = false;
          }
        }else{
          // Estraggo le informazioni per il file di cui si è effettuato
          // l'upload
          HashMap hm = ((UploadMultiploForm) this.getForm()).getFormFiles();
          FormFile ff = (FormFile) hm.get(new Long(indice));
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
      }


      if (!controlliFile) {
        salvareW_DOCDIG = false;
        ret = -1;
        //Anche se non è stato valorizzato il file, si deve potere gestire il salvataggio quando si modifica il campo
        //digfirma, salvataggio che non include il file allegato
        if (datiFormOriginale.isModifiedColumn("W_DOCDIG.DIGFIRMA_" + indice)) {
          newDataColumnContainer.update("W_DOCDIG", this.geneManager.getSql());
        }

      }

      if (salvareW_DOCDIG) {
        addFileToColumnContainer(newDataColumnContainer, nomeFile, file);
        if (datiFormOriginale.isModifiedColumn("W_DOCDIG.DIGDESDOC_" + indice)) {

          newDataColumnContainer.getColumn("W_DOCDIG.DIGDESDOC").setObjectOriginalValue(
              null);
          String desc = datiFormOriginale.getString("W_DOCDIG.DIGDESDOC_"
              + indice);
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

            //salvataggio dell'occorrenza inwsallegati se il file proviene dal documentale
            if (nomeallegato != null && nomeallegato.length()>0 && idDocumentale != null){
              GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
                  this.getServletContext(), GenChiaviManager.class);
              Long id = new Long(genChiaviManager.getNextId("WSALLEGATI"));
              try {
                this.getSqlManager().update("insert into wsallegati (id,entita,key1,key2,idwsdoc) values(?,?,?,?,?)", new Object[]{id,"W_DOCDIG","PG",new Long(numDoc),idDocumentale});
              } catch (SQLException e) {
                throw new GestoreException(
                    "Errore nell'inserimento su wsallegati", null, e);
              }
            }

          } else {
            ret = -1;
          }
        } else {
          newDataColumnContainer.update("W_DOCDIG", this.geneManager.getSql());
          //se non sto effettuando un inserimento
          if (nomeallegato == null || nomeallegato.length()==0){
          this.getSqlManager().update(
              "delete from WSALLEGATI where KEY2 = ?",
              new Object[] { numDoc });
          }
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
    }else if(!FileAllegatoManager.isEstensioneFileAmmessa(nomeFile)){
      salvareW_DOCDIG = false;
      throw new GestoreException(
          "Il file selezionato utilizza una estensione non ammessa", "upload.estensioneNonAmmessa",
          new String[] {nomeFile }, null);
    }

    return salvareW_DOCDIG;
  }
}
