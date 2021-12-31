/*
 * Created on 08-ott-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.integrazioni.WsdmConfigManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AutovieManager;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.bl.SmatManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoType;
import it.maggioli.eldasoft.ws.erp.WSERPAllegatoType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore dell'entita' TORN: insert, update, delete.<br>
 * Inoltre è il gestore che viene richiamato in fase di aggiornamento della gara
 * a lotto unico o lotto di gara (entità GARE), per aggiornare i dati della
 * tabella padre.
 *
 * @author Luca.Giacomazzo
 */
public class GestoreTORN extends AbstractGestoreEntita {

  /** Logger */
  static Logger      logger     = Logger.getLogger(GestoreTORN.class);

  /** Manager di PG */
  private PgManager  pgManager  = null;

  private PgManagerEst1  pgManagerEst1  = null;

  /** Manager di esecuzione query SQL */
  private SqlManager sqlManager = null;

  /** Manager per l'estrazione di dati tabellati */
  private TabellatiManager tabellatiManager = null;

  @Override
  public String getEntita() {
    return "TORN";
  }

  /** Manager di SMAT */
  private SmatManager  smatManager  = null;

  /** Manager di Autovie */
  private AutovieManager  autovieManager  = null;

  /** Manager di W_GENCHIAVI */
  private GenChiaviManager genChiaviManager = null;

  /** Manager Integrazione WSERP */
  private GestioneWSERPManager gestioneWSERPManager;

  /** Manager Integrazione WSDM */
  private GestioneWSDMManager gestioneWSDMManager;

  /** Manager Integrazione WSDM */
  private WsdmConfigManager wsdmConfigManager;


  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#setRequest(javax.servlet.http.HttpServletRequest)
   */
  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager di Piattaforma Gare
    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);
    pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        this.getServletContext(), PgManagerEst1.class);
    // Estraggo il manager SQL
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
    // Estraggo il manager per eseguire query sui tabellati
    tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
        "tabellatiManager", this.getServletContext(), TabellatiManager.class);
    // Estraggo il manager di SMAT
    smatManager = (SmatManager) UtilitySpring.getBean("smatManager",
        this.getServletContext(), SmatManager.class);
    // Estraggo il manager di Autovie
    autovieManager = (AutovieManager) UtilitySpring.getBean("autovieManager",
        this.getServletContext(), AutovieManager.class);
    genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);
    wsdmConfigManager = (WsdmConfigManager) UtilitySpring.getBean("wsdmConfigManager",
        this.getServletContext(), WsdmConfigManager.class);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    //variabili per tracciatura eventi
    int livEvento = 1;
    String codEvento = "GA_ELIMINAZIONE_PROCEDURA";
    String oggEvento = "";
    String descrEvento = "Eliminazione della gara";
    String errMsgEvento = "";


    try{
      // Chiamo la funzione centralizzata per l'eliminazione della tornata, la
      // quale
      // effettua la cancellazione di tutte le entita' figlie
      try{
        //Nel caso di gara a lotto unico si deve riportare in oggEvento NGARA,
        //altrimenti CODGAR
        String codgar = datiForm.getString("TORN.CODGAR");
        if(codgar.indexOf("$")==0)
          oggEvento = codgar.substring(1);
        else
          oggEvento = codgar;

        //Integrazione con WSERP
        String urlWSERP = ConfigManager.getValore("wserp.erp.url");
        if(urlWSERP != null && !"".equals(urlWSERP)){
          gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
              this.getServletContext(), GestioneWSERPManager.class);
          WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
          if(configurazione.isEsito()){
            String tipoWSERP = configurazione.getRemotewserp();
            String linkrda = null;
            if("SMEUP".equals(tipoWSERP)){
              linkrda = "2";
            }else if("AVM".equals(tipoWSERP) || "UGOVPA".equals(tipoWSERP) || "FNM".equals(tipoWSERP)){
              Long countGareRda = (Long)this.sqlManager.getObject("select count(numrda) from garerda where codgar = ? and numrda is not null", new Object[]{codgar});
              if(countGareRda >0 ){
                linkrda = "1";
              }else{
                linkrda = "2";
              }
            }

              String codiceTornata = datiForm.getString("TORN.CODGAR");
              // Determino i codici dei lotti della tornata in cancellazione
              List<?> listaLottiTornata = this.sqlManager.getListVector(
                  "select ngara from gare where codgar1 = ?",
                  new Object[] { codiceTornata });

              if (listaLottiTornata != null && listaLottiTornata.size() > 0) {
                for (int i = 0; i < listaLottiTornata.size(); i++) {
                  Vector<?> codiceLotto = (Vector<?>) listaLottiTornata.get(i);
                  String codiceGara= ((JdbcParametro) codiceLotto.get(0)).getStringValue();
                  int res = this.gestioneWSERPManager.scollegaRda(codiceTornata, codiceGara, linkrda, null, null, this.getRequest());
                  if(res < 0){
                    throw new GestoreException(
                        "Errore durante l'operazione di scollegamento delle RdA dalla gara",
                        "scollegaRdaGara", null);
                  }
                }
              }

          }
        }//integrazione WSERP

        pgManager.deleteTORN(datiForm.getString("TORN.CODGAR"));

      }catch(GestoreException e){
        livEvento = 3;
        errMsgEvento=e.getMessage();
        throw e;
      } catch (SQLException e) {
        throw new GestoreException("Errore nella lettura dei lotti della gara", null, e);
      }
    }finally{
      //Tracciatura eventi
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(oggEvento);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(descrEvento);
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        String messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
      }

    }
  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postDelete(it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#preInsert(org.springframework.transaction.TransactionStatus,
   *      it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {


  //variabili per tracciatura eventi
    int livEvento = 1;
    String codEvento = "GA_CREAZIONE_PROCEDURA";
    String oggEvento = "";
    String descrEvento = "Inserimento della gara";
    String errMsgEvento = "";

    String tipoGara = UtilityStruts.getParametroString(this.getRequest(),
        "tipoGara");
    String idconfi = UtilityStruts.getParametroString(this.getRequest(),
        "idconfi");
    try{


    try{
      if(geneManager.isCodificaAutomatica("TORN", "CODGAR")){
        if(datiForm.getString("TORN.CODGAR") == null ||
              (datiForm.getString("TORN.CODGAR") != null &&
                  datiForm.getString("TORN.CODGAR").length() == 0)){
          HashMap hm = pgManager.calcolaCodificaAutomatica("TORN", null, null,
          		null);
          datiForm.getColumn("TORN.CODGAR").setChiave(true);
          datiForm.setValue("TORN.CODGAR", hm.get("codiceGara"));
        }
      } else {
        if (this.getGeneManager().getProfili().checkProtec(
            (String) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS", "ALT.GARE.PersonalizzazioneAutovie")) {
            //Personalizzazione autovie
          if(datiForm.getString("TORN.CODGAR") == null ||
              (datiForm.getString("TORN.CODGAR") != null &&
                  datiForm.getString("TORN.CODGAR").length() == 0)){
            HashMap hm = autovieManager.calcolaCodificaAutomatica("TORN", null, null);
            datiForm.getColumn("TORN.CODGAR").setChiave(true);
            datiForm.setValue("TORN.CODGAR", hm.get("codiceGara"));
          }
        }else{
          String codiceGaraDestinazione = datiForm.getString("TORN.CODGAR");
          codiceGaraDestinazione = codiceGaraDestinazione.trim();
          datiForm.setValue("TORN.CODGAR", codiceGaraDestinazione);
          pgManager.verificaPreliminareDatiCopiaGara(null, codiceGaraDestinazione,
            null, null, false);
        }
      }
    }catch(GestoreException e){
      if(!"garaLottoUnico".equalsIgnoreCase(tipoGara)){
        livEvento = 3;
        errMsgEvento = e.getMessage();
      }
      throw e;
    }

    if(!"garaLottoUnico".equalsIgnoreCase(tipoGara))
      oggEvento = datiForm.getString("TORN.CODGAR");

    //Controllo che  TORN.NUMAVCP sia numerico
    if(datiForm.isColumn("TORN.NUMAVCP")){
      String numavcp = datiForm.getString("TORN.NUMAVCP");
      numavcp = UtilityStringhe.convertiNullInStringaVuota(numavcp);
      if(!"".equals(numavcp)){
        if(!this.isNumeric(numavcp)){
          if(!"garaLottoUnico".equalsIgnoreCase(tipoGara)){
            livEvento = 3;
            errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.NGaraAnacNoNumerico");
          }
          throw new GestoreException("Il valore specificato per N.gara ANAC deve essere numerico","NGaraAnacNoNumerico");
        }
      }
    }

    //Calcolo di MODLICG
    if(this.getGeneManager().getProfili().checkProtec(
        (String) this.getRequest().getSession().getAttribute(
            CostantiGenerali.PROFILO_ATTIVO), "COLS", "VIS",
        "GARE.TORN.CRITLIC") && this.getGeneManager().getProfili().checkProtec(
            (String) this.getRequest().getSession().getAttribute(
                CostantiGenerali.PROFILO_ATTIVO), "COLS", "VIS",
            "GARE.TORN.DETLIC") && this.getGeneManager().getProfili().checkProtec(
                (String) this.getRequest().getSession().getAttribute(
                    CostantiGenerali.PROFILO_ATTIVO), "COLS", "VIS",
                "GARE.TORN.CALCSOAN")){

      if(datiForm.isColumn("TORN.MODLIC")){
        Long modlic = datiForm.getLong("TORN.MODLIC");
        if(modlic== null){
          Long critlic = datiForm.getLong("TORN.CRITLIC");
          Long detlic = datiForm.getLong("TORN.DETLIC");
          String calcsoan = datiForm.getString("TORN.CALCSOAN");
          Long applegreg = datiForm.getLong("TORN.APPLEGREG");
          if(critlic==null || ("1".equals(critlic) && detlic== null && (calcsoan==null || "".equals(calcsoan)))){
            if(!"garaLottoUnico".equalsIgnoreCase(tipoGara)){
              livEvento = 3;
              errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.criterioAggiudicazionegNoDati");
            }
            throw new GestoreException("I dati per determinare il criterio di aggiudicazione non sono completi","criterioAggiudicazionegNoDati");
          }
          modlic = pgManager.getMODLICG(critlic, detlic, calcsoan, applegreg);
          datiForm.setValue("TORN.MODLIC", modlic);
        }
      }
    }

    //calcolo ITERGA
    if(datiForm.isColumn("TORN.TIPGAR")){
      Long iterga = datiForm.getLong("TORN.ITERGA");
      if(iterga==null){
        Long tipgar = datiForm.getLong("TORN.TIPGAR");
        if(tipgar==null){
          if(!"garaLottoUnico".equalsIgnoreCase(tipoGara)){
            livEvento = 3;
            errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.noTipoProcedura");
          }
          throw new GestoreException("Non è stato definito il tipo procedura","noTipoProcedura");
        }
        try {
          iterga = pgManager.getITERGA(tipgar);
          datiForm.setValue("TORN.ITERGA", iterga);
        } catch (SQLException e) {
          if(!"garaLottoUnico".equalsIgnoreCase(tipoGara)){
            livEvento = 3;
            errMsgEvento = "Errore nel calcolo di TORN.ITERGA";
          }
          throw new GestoreException(
              "Errore nel calcolo di TORN.ITERGA",null, e);
        }
      }
    }


    // Controllo tipo di procedura per le gare telematiche
    if (datiForm.isColumn("TORN.ITERGA") && datiForm.isColumn("TORN.GARTEL")) {
      Long iterga = datiForm.getLong("TORN.ITERGA");
      String gartel = datiForm.getString("TORN.GARTEL");

      if (gartel != null && "1".equals(gartel)) {
        if (iterga != null) {
          if (iterga.longValue() == 1 || iterga.longValue() == 2 || iterga.longValue() == 3 || iterga.longValue() == 4 || iterga.longValue() == 5 || iterga.longValue() == 6) {
            // Procedure ammesse
          } else {
            // Procedure non ammesse
            if(!"garaLottoUnico".equalsIgnoreCase(tipoGara)){
              livEvento = 3;
              errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.noTipoProceduraTelematica");
            }
            throw new GestoreException("Il tipo procedura non è disponibile nella modalità telematica", "noTipoProceduraTelematica");
          }
        }
      }
    }

    // se il profilo prevede un filtro nel suo codice, si imposta il campo
    // nascosto della tabella TORN
    Integer filtroProfilo = UtilityNumeri.convertiIntero((String)
        this.getRequest().getSession().getAttribute(
            CostantiGenerali.FILTRO_PROFILO_ATTIVO));
    if (filtroProfilo != null) {
      datiForm.addColumn("TORN.PROFILOWEB", JdbcParametro.TIPO_NUMERICO,
          new Long(filtroProfilo.intValue()));
    }

    try{
      // Gestione delle sezioni 'Atti autorizzativi'
      AbstractGestoreChiaveNumerica gestoreGARATT = new DefaultGestoreEntitaChiaveNumerica(
          "GARATT", "NUMATT", new String[] { "CODGAR" }, this.getRequest());
      this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
          gestoreGARATT, "ATAU",
          new DataColumn[] { datiForm.getColumn("TORN.CODGAR") }, null);




      String codiceGara=datiForm.getString("TORN.CODGAR");
      // Gestione delle sezioni 'Impegni di spesa'
      AbstractGestoreChiaveNumerica gestoreGAREIDS = new DefaultGestoreEntitaChiaveNumerica(
          "GAREIDS", "NUMIDS", new String[] { "CODGAR" }, this.getRequest());
      this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
          gestoreGAREIDS, "IDS",
          new DataColumn[] { datiForm.getColumn("TORN.CODGAR") }, null);

      //Gestione Ulteriori referenti incaricati
      AbstractGestoreChiaveNumerica gestoreGARTECNI = new DefaultGestoreEntitaChiaveNumerica(
         "GARTECNI", "NUMTEC", new String[] { "CODGAR" }, this.getRequest());
      this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
         gestoreGARTECNI, "ULTREFINC",
         new DataColumn[] { datiForm.getColumn("TORN.CODGAR") }, null);



      // CF11082014 se ho inserimento da rda devo creare il primo lotto e agganciargli le lavorazioni
      if (this.getGeneManager().getProfili().checkProtec(
          (String) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS", "ALT.GARE.inserimentoRdaSMAT")) {
        if (datiForm.isColumn("TORN.NATTOT")
            && datiForm.isModifiedColumn("TORN.NATTOT")
            && datiForm.getColumn("TORN.NATTOT").getValue().stringValue() != null
            && !"".equals(datiForm.getColumn("TORN.NATTOT").getValue().stringValue())) {
          // inserisco il primo lotto
          HashMap hm = null;
          String nuovoNGARA = null;
          codiceGara = datiForm.getColumn("TORN.CODGAR").getValue().getStringValue();
          codiceGara = UtilityStringhe.convertiNullInStringaVuota(codiceGara);
          //if (geneManager.isCodificaAutomatica("TORN", "CODGAR")) {
          if (!"".equals(codiceGara)) {
            hm = smatManager.calcolaCodificaAutomaticaLotto(codiceGara, null);
            nuovoNGARA = (String) hm.get("numeroGara");
            String numeroRda = datiForm.getString("TORN.NATTOT");
            numeroRda = UtilityStringhe.convertiNullInStringaVuota(numeroRda);

            int res = smatManager.insPrimoLotto(codiceGara, nuovoNGARA, numeroRda, datiForm);
            if (res >= 0) {
              String nomeCampoNumeroRecord = "NUMERO_ATAU";
              if (datiForm.isColumn(nomeCampoNumeroRecord)) {
                DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(datiForm.getColumns("GARATT", 0));
                int numeroRecord = datiForm.getLong(nomeCampoNumeroRecord).intValue();
                for (int i = 1; i <= numeroRecord; i++) {
                  DataColumnContainer newDataColumnContainer = new DataColumnContainer(tmpDataColumnContainer.getColumnsBySuffix("_" + i,
                      false));
                  if (newDataColumnContainer.isColumn("GARATT.NATTOT")) {
                    String nattot = newDataColumnContainer.getString("GARATT.NATTOT");
                    nattot = UtilityStringhe.convertiNullInStringaVuota(nattot);
                    if(!"".equals(nattot)){
                      res = smatManager.insLavorazioniLotto(codiceGara, nuovoNGARA, nattot);
                      if(res <0){
                        throw new GestoreException("Errore nell'inizializzazione delle lavorazioni " +
                        " del lotto (" + nuovoNGARA +")", null);
                      }
                      res = smatManager.updRdaAnnullabile("GARATT",nattot,codiceGara);
                      if(res <0){
                        throw new GestoreException("Errore nell'annullamento " +
                        " della RdA  (" + nattot +")", null);
                      }
                    }
                  }
                }
              }
              res = smatManager.insLavorazioniLotto(codiceGara, nuovoNGARA, numeroRda);
              if(res <0){
                throw new GestoreException("Errore nell'inizializzazione delle lavorazioni " +
                " del lotto (" + nuovoNGARA +")", null);
              }
            }
            res = smatManager.updLotto(codiceGara, nuovoNGARA);
            if(res <0){
              throw new GestoreException("Errore nell'aggiornamento " +
              " del lotto (" + nuovoNGARA +")", null);
            }

            Double imptor = smatManager.updImportoTotaleTorn(codiceGara);
            if (datiForm.isColumn("TORN.IMPTOR")) {
              datiForm.setValue("TORN.IMPTOR",imptor);
            }

            if(!"".equals(numeroRda)){
              res = smatManager.updRdaAnnullabile("TORN",numeroRda,codiceGara);
              if(res <0){
                throw new GestoreException("Errore nell'annullamento " +
                " della RdA  (" + numeroRda +")", null);
              }
            }

          }

        }
      }

      // si aggiorna anche ISTAUT
      if (datiForm.isColumn("V_GARE_IMPORTI.VALMAX") && datiForm.isColumn("TORN.IMPTOR")) {
        Double importoContributo = this.pgManager.getContributoAutoritaStAppaltante(
            datiForm.getColumn("V_GARE_IMPORTI.VALMAX").getValue().doubleValue(), "A1z02");
        datiForm.addColumn("TORN.ISTAUT", JdbcParametro.TIPO_DECIMALE,
            importoContributo);
        if (importoContributo == null) {
          // questo set serve a permettere l'annullamento del dato nel campo
          // corrispondente, in quanto si aggiunge una colonna, il cui valore
          // se è null non viene usato per insert/update
          datiForm.getColumn("TORN.ISTAUT").setOriginalValue(
              new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Double(1)));
        }
      }

      // INSERIMENTO PERMESSI DI ACCESSO ALLA GARA
      this.inserisciPermessi(datiForm, "CODGAR", new Integer(2));

      ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);

      //Aggiornamento G_PERMESSI.MERUOLO
      if(datiForm.isColumn("TORN.GARTEL") && "1".equals(datiForm.getString("TORN.GARTEL"))){
        String codgar = datiForm.getString("TORN.CODGAR");
        Long idUtente = new Long(profilo.getId());
        String ruoloME = profilo.getRuoloUtenteMercatoElettronico();
        Long meruolo=new Long(2);
        if(ruoloME!=null && !"".equals(ruoloME))
          meruolo= new Long(ruoloME);
        try {
          pgManager.updateMeruoloG_Permessi( codgar,idUtente,meruolo);
        } catch (SQLException e1) {
          throw new GestoreException("Errore nell'aggiornamento del campo G_PERMESSI.MERUOLO", null, e1);
        }
      }

    //Valorizzazione del campo UREGA
      if(datiForm.isColumn("TORN.UREGA") && this.getGeneManager().getProfili().checkProtec(
          (String) this.getRequest().getSession().getAttribute(
              CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS",
          "ALT.GARE.CodiceUrega")){

        Long sysuffapp=null;
        //ProfiloUtente profilo = (ProfiloUtente)  this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        try {

          Long syscon = new Long(profilo.getId());

          sysuffapp = (Long)this.sqlManager.getObject("select sysuffapp from usrsys where syscon=?", new Object[]{syscon});
        } catch (SQLException e) {
          throw new GestoreException("Errore nella lettura del campo USERSYS.SYSUFFAPP", null, e);
        }
        if(sysuffapp==null)
          throw new GestoreException("Non è valorizzato l'ufficio di appartenenza dell'utente", "codiceUrega.noUff",null, new Exception());

      String codiceTabellato = tabellatiManager.getDescrTabellato("A1097", sysuffapp.toString());
        if(codiceTabellato==null || "".equals(codiceTabellato))
          throw new GestoreException("Non è valorizzato il codice nel tabellato A1097 corrispondente a SYSUFFAPP=" + sysuffapp.toString(),
              "codiceUrega.noTabellato",null, new Exception());

        //Il codice del tabellato corrisponde ai caratteri 4 e 5 dei primi 5 caratteri del codice.
        //I primi tre caratteri si calcolano facendo il max+1 sui primi tre caratteri dei codici urega in db a parità dei caratteri 4 e 5

        String subStringPrimi3Caratteri = sqlManager.getDBFunction("substr",
            new String[] { "urega", "1", "3" });

        String subStringUltimi2Caratteri = sqlManager.getDBFunction("substr",
            new String[] { "urega", "4", "2" });

        String stringToInt = sqlManager.getDBFunction("strtoint",
            new String[] { subStringPrimi3Caratteri, "4", "2" });

        String select = "select max(" +  stringToInt + ") from torn where " + subStringUltimi2Caratteri + " = ? ";
        Long maxContatore = null;
        try {
          maxContatore = (Long) this.sqlManager.getObject(select, new Object[]{codiceTabellato});
        } catch (SQLException e) {
          throw new GestoreException("Errore nella generazione del contatore per il codice urega", null, e);
        }
         if(maxContatore==null){
           maxContatore = new Long(0);
         }
         maxContatore = new Long(maxContatore.longValue()+1);
        String contatore = maxContatore.toString();
        contatore = UtilityStringhe.fillLeft(contatore, '0', 3);
        String codiceUrega= contatore + codiceTabellato;
        GregorianCalendar dataOdierna = new GregorianCalendar();
        int anno = dataOdierna.get(Calendar.YEAR);
        codiceUrega += Integer.toString(anno) + "P";

        GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
            this.getServletContext(), GenChiaviManager.class);
        Long contatoreUrega = new Long(genChiaviManager.getNextId("TORN.UREGA"));
        contatore = contatoreUrega.toString();
        contatore = UtilityStringhe.fillLeft(contatore, '0', 5);
        codiceUrega += contatore;
        datiForm.setValue("TORN.UREGA", codiceUrega);
      }

      //Gestione particolare per Gara divisa in lotti con offerta unica
      if (datiForm.isColumn("V_GARE_TORN.TIPOLOGIA") && datiForm.getLong("V_GARE_TORN.TIPOLOGIA").longValue() == 3) {
        Long aqoper = null;
        if(datiForm.isColumn("TORN.AQOPER"))
          aqoper = datiForm.getLong("TORN.AQOPER");
        Long altrisog = null;
        if(datiForm.isColumn("TORN.ALTRISOG"))
          altrisog = datiForm.getLong("TORN.ALTRISOG");
        if((aqoper!=null && aqoper.longValue()==2) || (altrisog!=null && altrisog.longValue()==3))
          datiForm.setValue("TORN.MODCONT", new Long(1));
          try {
              datiForm.addColumn("GARE.NGARA", JdbcParametro.TIPO_TESTO,
                      datiForm.getString("TORN.CODGAR"));
              datiForm.getColumn("GARE.NGARA").setChiave(true);
              datiForm.addColumn("GARE.CODGAR1", JdbcParametro.TIPO_TESTO,
                      datiForm.getString("TORN.CODGAR"));
              datiForm.addColumn("GARE.GENERE", JdbcParametro.TIPO_NUMERICO,
                      new Long(3));
              datiForm.getColumn("GARE.TEMESI").setObjectOriginalValue(null);
              datiForm.getColumn("GARE.MODASTG").setObjectOriginalValue(null);
              datiForm.getColumn("GARE.SICINC").setObjectOriginalValue(null);
              datiForm.getColumn("GARE.BUSTALOTTI").setObjectOriginalValue(null);
              datiForm.insert("GARE", this.geneManager.getSql());

              this.sqlManager.update("insert into GARE1 (ngara,codgar1) values(?,?)",
                    new Object[]{datiForm.getString("TORN.CODGAR"), datiForm.getString("TORN.CODGAR")});

              this.sqlManager.update("insert into GARECONT (ngara,ncont) values(?,?)",
                  new Object[]{datiForm.getString("TORN.CODGAR"), new Long(1)});

          } catch (SQLException e) {
              throw new GestoreException("Errore nel salvataggio dei dati in GARE per la gara ad offerta unica",
                        null, e);
          }

          if (datiForm.isColumn("CATG.CATIGA")) {
              // Gestione della sezione 'Categoria prevalente'
              GestoreCATG.gestisciEntitaDaGare(this.getRequest(), status, datiForm,
                  datiForm.getColumn("TORN.CODGAR"));

              // Gestione delle sezioni 'Ulteriori categorie'
              AbstractGestoreChiaveNumerica gestoreOPES = new DefaultGestoreEntitaChiaveNumerica(
                  "OPES", "NOPEGA", new String[] { "NGARA3" }, this.getRequest());
              this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
                      gestoreOPES, "OPES",
                  new DataColumn[] { datiForm.getColumn("TORN.CODGAR") }, null);
          }

          //Aggiornamento della gara complementare
          this.updateLottiGara(datiForm,"INSERT");

          //Inserimento documentazione busta economica per gara telemetica e offerta unica
          Long offtel=null;
          if (datiForm.isColumn("TORN.OFFTEL"))
            offtel= datiForm.getLong("TORN.OFFTEL");
          if(new Long(1).equals(offtel)){
            String codgar = datiForm.getString("TORN.CODGAR");
            try {
              this.sqlManager.update("insert into documgara(codgar, ngara, norddocg, busta, gruppo, tipodoc, descrizione, obbligatorio, modfirma, valenza, gentel, numord) " +
                      "values(?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{codgar, null, new Long(1), new Long(3), new Long(3), new Long(1), "Offerta economica", "1", new Long(1), new Long(0), "1", new Long(1) });
            } catch (SQLException e) {
              throw new GestoreException("Errore nell'inserimento del documento 'Offerta economica'", null, e);
            }
          }

      }

      //inizializzazione del campo IDCOMMALBO
      this.setALBOCOMM(datiForm);

      //Inizializzazione del campo MODREA, ma solo per lotto unico e per offerte distinte o offerta unica(ma non per i lotti)
      if(datiForm.isColumn("TORN.MODREA") && datiForm.isColumn("TORN.ACCQUA") && datiForm.isColumn("TORN.ALTRISOG")
          && (datiForm.isColumn("TORN.TIPGAR") || datiForm.isColumn("GARE.TIPGARG"))) {
        Long tipgar = null;
        if(datiForm.isColumn("TORN.TIPGAR"))
          tipgar = datiForm.getLong("TORN.TIPGAR");
        else
          tipgar = datiForm.getLong("GARE.TIPGARG");
        String modrea = this.pgManagerEst1.getModrea(datiForm.getString("TORN.ACCQUA"), datiForm.getLong("TORN.ALTRISOG"), tipgar);
        datiForm.setValue("TORN.MODREA", modrea);
      }

    }catch(GestoreException e){
      if(!"garaLottoUnico".equalsIgnoreCase(tipoGara)){
        livEvento = 3;
        errMsgEvento = e.getMessage();
        throw e;
      }
    }

    }finally{
      //Tracciatura eventi
      try {
        if(!"garaLottoUnico".equalsIgnoreCase(tipoGara)){
          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(oggEvento);
          logEvento.setCodEvento(codEvento);
          logEvento.setDescr(descrEvento);
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        }
      } catch (Exception le) {
        String messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
      }

    }









  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postInsert(it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

    String gartel = null;
    String codgar = datiForm.getString("TORN.CODGAR");
    if (datiForm.isColumn("TORN.GARTEL")) {
      gartel = datiForm.getString("TORN.GARTEL");
    }
    //Controllo su presenza punto istruttore per la gara
    if (gartel != null && "1".equals(gartel)) {
      try {
        String codiceMsg = null;
        Long numeroPuntiOrdinante = (Long)this.sqlManager.getObject("select count(numper) from g_permessi where codgar=? and meruolo=?", new Object[]{codgar,new Long(1)});
        if(numeroPuntiOrdinante == null || new Long(0).equals(numeroPuntiOrdinante)){
          codiceMsg = "warnings.gare.controlloPuntoOrdinante";
        }
        String descTabA1133 = tabellatiManager.getDescrTabellato("A1133", "1");
        if(descTabA1133!=null && descTabA1133.length()>0)
          descTabA1133 = descTabA1133.substring(0, 1);
        if("1".equals(descTabA1133)){
          Long numeroPuntiIstruttore = (Long)this.sqlManager.getObject("select count(numper) from g_permessi where codgar=? and meruolo=?", new Object[]{codgar,new Long(2)});
          if(numeroPuntiIstruttore == null || new Long(0).equals(numeroPuntiIstruttore)){
            if(numeroPuntiOrdinante == null || new Long(0).equals(numeroPuntiOrdinante)){
              codiceMsg = "warnings.gare.controlloPuntoOrdinantePuntiIstruttore";
            }else{
              codiceMsg = "warnings.gare.controlloPuntiIstruttore";
            }
          }
        }
        if(codiceMsg != null && codiceMsg.length() > 0){
          UtilityStruts.addMessage(this.getRequest(), "warning",codiceMsg, null);
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nella lettura della tabella G_PERMESSI per la gara " + codgar, null,e);
      }
    }


  }

  @Override
  public void afterInsertEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    String codiceGara = datiForm.getColumn("TORN.CODGAR").getValue().stringValue();
    String uffint = datiForm.getColumn("TORN.CENINT").getValue().stringValue();
    Long idconfi = null;
    try{
      idconfi = wsdmConfigManager.getWsdmConfigurazione(uffint, "PG");
      Long genere = (Long)this.sqlManager.getObject("select genere from v_gare_torn where codgar=? ", new Object[]{codiceGara});
      if(genere!=null && genere.longValue()==3){
        //Se iterga==6 si procede alla valorizzazione di elencoe se vi sono le condizioni
        Long iterga = datiForm.getLong("TORN.ITERGA");
        if(iterga!=null && iterga.longValue()==6 || (!(iterga!=null && iterga.longValue()==6) &&
            this.geneManager.getProfili().checkProtec((String) this.getRequest().getSession().getAttribute(
                CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS", "ALT.GARE.AssociaGaraAElenco")) ){
           if("1".equals(pgManager.getPresenzaElencoOperatori(codiceGara, "TORN"))){
              Long tipgen = datiForm.getColumn("TORN.TIPGEN").getValue().longValue();
              String catiga = null;
              if(datiForm.isColumn("CATG.CATIGA"))
                catiga = datiForm.getColumn("CATG.CATIGA").getValue().getStringValue();
              boolean considerareCatalogo=true;
              if(!(iterga!=null && iterga.longValue()==6) &&
                  this.geneManager.getProfili().checkProtec((String) this.getRequest().getSession().getAttribute(
                      CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS", "ALT.GARE.AssociaGaraAElenco"))
                considerareCatalogo=false;
              String codiceElenco = pgManager.getCodiceElencoDaAssociare(catiga,tipgen,considerareCatalogo);
              if(codiceElenco!=null)
                this.sqlManager.update("update gare set elencoe = ? where ngara = ? ", new Object[]{codiceElenco,codiceGara});
            }

        }
        if(datiForm.isModifiedTable("GARE1")){
          AbstractGestoreEntita gestoreGARE1 = new DefaultGestoreEntita("GARE1", this.getRequest());
          datiForm.setValue("GARE1.NGARA", codiceGara);
          datiForm.getColumn("GARE1.NGARA").setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceGara));
          gestoreGARE1.update(status, datiForm);
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nell'inizializzazione del campo elencoe " +
          " della gara  (" + codiceGara +")", null, e);
    }


    // Gestione delle sezioni 'Richieste di acquisto'
    //Si deve valorizzare il campo GARERDA.CODGAR dei record delle sezioni multiple, che in inserimento è vuoto
    //poichè il valore di codgar non è presente nella jsp, ma viene determinato dopo
    int numRecord = datiForm.getLong("NUMERO_GARERDA").intValue();
    for (int i = 1; i <= numRecord; i++) {
      if(datiForm.isColumn("MOD_GARERDA_" + i) && "1".equals(datiForm.getString("MOD_GARERDA_" + i))){
        datiForm.addColumn("GARERDA.CODGAR_" + i, JdbcParametro.TIPO_TESTO, codiceGara);
      }
    }

    AbstractGestoreChiaveIDAutoincrementante gestoreMultiploGARERDA = new DefaultGestoreEntitaChiaveIDAutoincrementante(
        "GARERDA", "ID", this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultiplaRda(status, datiForm,
        gestoreMultiploGARERDA, "GARERDA",
        new DataColumn[] {datiForm.getColumn("TORN.CODGAR")}, null, ""+idconfi);
  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#preUpdate(org.springframework.transaction.TransactionStatus,
   *      it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    String idconfi = (String) this.getRequest().getAttribute("idconfi");

    String codgar = datiForm.getString("TORN.CODGAR");
    if(datiForm.isColumn("GARE.BUSTALOTTI") && datiForm.isModifiedColumn("GARE.BUSTALOTTI")){
      try {
        Long conteggio = (Long)this.sqlManager.getObject("select count(codgar) from imprdocg where codgar=?", new Object[]{codgar});
        if (conteggio!=null && conteggio.longValue()>0 )
          throw new GestoreException("Non e possibile modificare la Modalità presentazione buste poichè vi sono dei documenti delle ditte","modificaBustalotti",
              null, new Exception());
      } catch (SQLException e) {
        throw new GestoreException("Errore nel conteggio dei documenti delle ditte",null,e);
      }
    }

    //Controllo che  TORN.NUMAVCP sia numerico
    if(datiForm.isColumn("TORN.NUMAVCP") && datiForm.isModifiedColumn("TORN.NUMAVCP")){
      String numavcp = datiForm.getString("TORN.NUMAVCP");
      numavcp = UtilityStringhe.convertiNullInStringaVuota(numavcp);
      if(!"".equals(numavcp)){
        if(!this.isNumeric(numavcp)){
          throw new GestoreException("Il valore specificato per N.gara ANAC deve essere numerico","NGaraAnacNoNumerico");
        }
      }
    }

    //Controllo sulla modifica del campo TORN.MODCONT
    if(datiForm.isColumn("TORN.MODCONT") && datiForm.isModifiedColumn("TORN.MODCONT") && datiForm.getLong("GARE.GENERE")!=null && datiForm.getLong("GARE.GENERE").longValue()==3){
      try {
        Long conteggioAppalti = (Long)this.sqlManager.getObject("select count(ngara) from gare where codgar1=? and clavor is not null and numera is not null",
            new Object[]{datiForm.getString("TORN.CODGAR")});
        if(conteggioAppalti!= null && conteggioAppalti.longValue()>0){
          throw new GestoreException("Non e possibile modificare il campo 'Modalità stipula contratto' poichè vi sono dei lotti associati a degli appalti","modificaModcont.LottiConAppalti",
              null, new Exception());
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nel controllo dei dati della gara",null,e);
      }
      try {
        Long conteggioGarecont = (Long)this.sqlManager.getObject("select count(ncont) from garecont where ngara=? and codimp is not null ",
            new Object[]{datiForm.getString("TORN.CODGAR")});
        if(conteggioGarecont!= null && conteggioGarecont.longValue()>0){
          throw new GestoreException("Non e possibile modificare il campo 'Modalità stipula contratto' poichè vi sono dei contratti definiti per la gara","modificaModcont.ContrattiDefiniti",
              null, new Exception());
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nel controllo dei dati della gara",null,e);
      }
      //Se il controllo è andato a buon fine si devono cancellare le occorrenze di GARECONT
      try {
        this.sqlManager.update("delete from garecont where ngara=?", new Object[]{datiForm.getString("TORN.CODGAR")} );
      } catch (SQLException e) {
        throw new GestoreException("Errore nella cancellazione delle occorrenze di GARECONT relative alla gara:" + datiForm.getString("TORN.CODGAR"),null,e);
      }
    }
    if(this.getGeneManager().getProfili().checkProtec(
        (String) this.getRequest().getSession().getAttribute(
            CostantiGenerali.PROFILO_ATTIVO), "COLS", "VIS",
        "GARE.TORN.CRITLIC") && this.getGeneManager().getProfili().checkProtec(
            (String) this.getRequest().getSession().getAttribute(
                CostantiGenerali.PROFILO_ATTIVO), "COLS", "VIS",
            "GARE.TORN.DETLIC") && this.getGeneManager().getProfili().checkProtec(
                (String) this.getRequest().getSession().getAttribute(
                    CostantiGenerali.PROFILO_ATTIVO), "COLS", "VIS",
                "GARE.TORN.CALCSOAN")){
      //Calcolo di MODLICG
      if(datiForm.isColumn("TORN.MODLIC") && datiForm.isColumn("TORN.CRITLIC") && datiForm.isColumn("TORN.DETLIC") && datiForm.isColumn("TORN.CALCSOAN")
          && datiForm.isColumn("TORN.APPLEGREG")){
        Long modlic = datiForm.getLong("TORN.MODLIC");
        if(modlic== null){
          Long critlic = datiForm.getLong("TORN.CRITLIC");
          Long detlic = datiForm.getLong("TORN.DETLIC");
          String calcsoan = datiForm.getString("TORN.CALCSOAN");
          Long applegreg = datiForm.getLong("TORN.APPLEGREG");
          if(critlic==null || ("1".equals(critlic) && detlic== null && (calcsoan==null || "".equals(calcsoan)))){
            throw new GestoreException("I dati per determinare il criterio di aggiudicazione non sono completi","criterioAggiudicazionegNoDati");
          }
          modlic = pgManager.getMODLICG(critlic, detlic, calcsoan, applegreg);
          datiForm.setValue("TORN.MODLIC", modlic);
        }
      }
    }

    //calcolo ITERGA
    if(datiForm.isColumn("TORN.TIPGAR") && datiForm.isColumn("TORN.ITERGA")){
      Long iterga = datiForm.getLong("TORN.ITERGA");
      if(iterga==null){
        Long tipgar = datiForm.getLong("TORN.TIPGAR");
        if(tipgar==null){
          throw new GestoreException("Non è stato definito il tipo procedura","noTipoProcedura");
        }
        try {
          iterga = pgManager.getITERGA(tipgar);
          datiForm.setValue("TORN.ITERGA", iterga);
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore nel calcolo di TORN.ITERGA",null, e);
        }
      }
    }
    if(datiForm.isColumn("TORN.ITERGA") && datiForm.isColumn("TORN.DINVIT")){
      Long iterga = datiForm.getLong("TORN.ITERGA");
      if(iterga != null && iterga.intValue() == 1){
        datiForm.setValue("TORN.DINVIT", "");
      }
    }

    String gartel = null;
    // Controllo tipo di procedura per le gare telematiche
    if (datiForm.isColumn("TORN.ITERGA") && datiForm.isColumn("TORN.GARTEL")) {
      Long iterga = datiForm.getLong("TORN.ITERGA");
      gartel = datiForm.getString("TORN.GARTEL");

      if (gartel != null && "1".equals(gartel)) {
        if (iterga != null) {
          if (iterga.longValue() == 1 || iterga.longValue() == 2 || iterga.longValue() == 3 || iterga.longValue() == 4 || iterga.longValue() == 5 || iterga.longValue() == 6) {
            // Procedure ammesse
          } else {
            // Procedure non ammesse
            throw new GestoreException("Il tipo procedura non è disponibile nella modalità telematica", "noTipoProceduraTelematica");
          }

          //Si deve impedire di cambiare il procedura da negoziata senza bando ad aperta o ristretta se sono presenti ditta in gara
          if ((datiForm.isColumn("TORN.TIPGAR") && datiForm.isModifiedColumn("TORN.TIPGAR")) || (datiForm.isColumn("GARE.TIPGARG") && datiForm.isModifiedColumn("GARE.TIPGARG"))) {
            Long itergaOrig = datiForm.getColumn("TORN.ITERGA").getOriginalValue().longValue();
            if((new Long(3).equals(itergaOrig) || new Long(5).equals(itergaOrig) || new Long(6).equals(itergaOrig)) && !(new Long(3).equals(iterga) || new Long(5).equals(iterga) || new Long(6).equals(iterga)) ){
              try {
                Long conteggioDitte = (Long)this.sqlManager.getObject("select count(dittao) from ditg where codgar5=?", new Object[]{codgar});
                if(conteggioDitte!=null && conteggioDitte.longValue()>0)
                  throw new GestoreException("Non è possibile modificare il tipo della gara, vi sono delle ditte in gara", "modificaTipgar.ditteInGara");
              } catch (SQLException e) {
                throw new GestoreException("Errore nella lettura della tabella DITG per la gara " + codgar, null,e);
              }
            }
          }
        }

      }
    }

    //Controllo su presenza punto istruttore per la gara
    if (gartel != null && "1".equals(gartel)) {
      try {
        String codiceMsg = null;
        Long numeroPuntiOrdinante = (Long)this.sqlManager.getObject("select count(numper) from g_permessi where codgar=? and meruolo=?", new Object[]{codgar,new Long(1)});
        if(numeroPuntiOrdinante == null || new Long(0).equals(numeroPuntiOrdinante)){
          codiceMsg = "warnings.gare.controlloPuntoOrdinante";
        }
        String descTabA1133 = tabellatiManager.getDescrTabellato("A1133", "1");
        if(descTabA1133!=null && descTabA1133.length()>0)
          descTabA1133 = descTabA1133.substring(0, 1);
        if("1".equals(descTabA1133)){
          Long numeroPuntiIstruttore = (Long)this.sqlManager.getObject("select count(numper) from g_permessi where codgar=? and meruolo=?", new Object[]{codgar,new Long(2)});
          if(numeroPuntiIstruttore == null || new Long(0).equals(numeroPuntiIstruttore)){
            if(numeroPuntiOrdinante == null || new Long(0).equals(numeroPuntiOrdinante)){
              codiceMsg = "warnings.gare.controlloPuntoOrdinantePuntiIstruttore";
            }else{
              codiceMsg = "warnings.gare.controlloPuntiIstruttore";
            }
          }
        }
        if(codiceMsg != null){
          UtilityStruts.addMessage(this.getRequest(), "warning",codiceMsg, null);
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nella lettura della tabella G_PERMESSI per la gara " + codgar, null,e);
      }
    }

    try {
      String garaLottoUnico = UtilityStruts.getParametroString(this.getRequest(), "garaLottoUnico");
      if (datiForm.isColumn("TORN.OFFAUM") && datiForm.isModifiedColumn("TORN.OFFAUM")
          && "2".equals(datiForm.getString("TORN.OFFAUM")) && !"true".equalsIgnoreCase(garaLottoUnico)){
        Long lottiPrezzoPiuAlto = (Long)this.sqlManager.getObject("select count(*) from gare where codgar1 = ? and modlicg = 17", new Object[]{codgar});
        if(lottiPrezzoPiuAlto!=null && lottiPrezzoPiuAlto.longValue()>0){
          Long offtel=null;
          if (datiForm.isColumn("TORN.OFFTEL"))
            offtel= datiForm.getLong("TORN.OFFTEL");
          Long modlicg=null;
          if (datiForm.isColumn("TORN.MODLIC"))
            modlicg= datiForm.getLong("TORN.MODLIC");
          if(new Long(1).equals(offtel) && new Long(6).equals(modlicg)){
            throw new GestoreException("Non è possibile impostare il criterio di aggiundicazione a 'offerta economicamente più vantaggiosa' perchè sono presenti dei lotti con criterio di aggiudicazione 'Prezzo più alto'", "modificaOffaum.prezzoPiuAlto.oepv");
          }else{
            throw new GestoreException("Non è possibile modificare il flag 'Ammesse offerte in aumento?' perchè sono presenti dei lotti con criterio di aggiudicazione 'Prezzo più alto'", "modificaOffaum.prezzoPiuAlto");
          }
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della tabella GARE per la gara " + codgar, null,e);
    }


    datiForm.getColumn("TORN.CODGAR").setChiave(true);


    // Gestione delle sezioni 'Atti autorizzativi'
    AbstractGestoreChiaveNumerica gestoreGARATT = new DefaultGestoreEntitaChiaveNumerica(
        "GARATT", "NUMATT", new String[] { "CODGAR" }, this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
        gestoreGARATT, "ATAU",
        new DataColumn[] { datiForm.getColumn("TORN.CODGAR") }, null);

    // Gestione delle sezioni 'Oridini di acquisto'
    AbstractGestoreChiaveIDAutoincrementante gestoreMultiploGARERDA = new DefaultGestoreEntitaChiaveIDAutoincrementante(
        "GARERDA", "ID", this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultiplaRda(status, datiForm,
        gestoreMultiploGARERDA, "GARERDA",
        new DataColumn[] {datiForm.getColumn("TORN.CODGAR")}, null,idconfi);

    // Gestione delle sezioni 'Impegni di spesa'
    AbstractGestoreChiaveNumerica gestoreGAREIDS = new DefaultGestoreEntitaChiaveNumerica(
        "GAREIDS", "NUMIDS", new String[] { "CODGAR" }, this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
        gestoreGAREIDS, "IDS",
        new DataColumn[] { datiForm.getColumn("TORN.CODGAR") }, null);

    //Gestione Ulteriori referenti incaricati
    AbstractGestoreChiaveNumerica gestoreGARTECNI = new DefaultGestoreEntitaChiaveNumerica(
        "GARTECNI", "NUMTEC", new String[] { "CODGAR" }, this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
        gestoreGARTECNI, "ULTREFINC",
        new DataColumn[] { datiForm.getColumn("TORN.CODGAR") }, null);

    if(this.getGeneManager().getProfili().checkProtec((String) this.getRequest().getSession().getAttribute(
        CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS", "ALT.GARE.inserimentoRdaSMAT")){
      String nomeCampoNumeroRecord = "NUMERO_ATAU";
      String nomeCampoDelete = "DEL_ATAU";
      String nomeCampoMod = "MOD_ATAU";
      String codiceLotto = null;
      if (datiForm.isColumn(nomeCampoNumeroRecord)) {
        DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(datiForm.getColumns("GARATT", 0));
        String codiceGara = datiForm.getString("TORN.CODGAR");
        try {
          if(codgar.indexOf("$")==0){
            codiceLotto = (String) sqlManager.getObject(
                "select ngara from GARE where codgar1 = ? ", new Object[] { codiceGara });
          }else{
            codiceLotto = (String) sqlManager.getObject(
                "select ngara from GARE where codgar1 = ? and codiga='1' ", new Object[] { codiceGara });
          }
        } catch (SQLException e) {
          throw new GestoreException("Errore nell'inizializzazione delle lavorazioni " +
              " della gara  (" + codiceGara +")", null, e);
        }

        int numeroRecord = datiForm.getLong(nomeCampoNumeroRecord).intValue();
        int res = 0;
        for (int i = 1; i <= numeroRecord; i++) {
          DataColumnContainer newDataColumnContainer = new DataColumnContainer(tmpDataColumnContainer.getColumnsBySuffix("_" + i,false));
          if (newDataColumnContainer.isColumn("GARATT.NATTOT")) {
            String nattot = newDataColumnContainer.getString("GARATT.NATTOT");
            nattot = UtilityStringhe.convertiNullInStringaVuota(nattot);
            String oNattot = newDataColumnContainer.getColumn("GARATT.NATTOT").getOriginalValue().getStringValue();

            boolean deleteOccorrenza = newDataColumnContainer.isColumn(nomeCampoDelete)
                && "1".equals(newDataColumnContainer.getString(nomeCampoDelete));
            boolean updateOccorrenza = newDataColumnContainer.isColumn(nomeCampoMod)
                && "1".equals(newDataColumnContainer.getString(nomeCampoMod));

            if(deleteOccorrenza){
              //sono in mod. eliminazione occ. scheda multipla
              res = smatManager.delLavorazioniLotto(codiceGara, codiceLotto, oNattot);
            }else if(updateOccorrenza){
              if (newDataColumnContainer.getLong("GARATT.NUMATT") == null){
                //sono in mod. inserimento occ. scheda multipla
                res = smatManager.insLavorazioniLotto(codiceGara, codiceLotto, nattot);
              }else{
                //sono in mod. aggiornamento occ. scheda multipla (elimino/inserisco)
                res = smatManager.delLavorazioniLotto(codiceGara, codiceLotto, oNattot);
                res = smatManager.insLavorazioniLotto(codiceGara, codiceLotto, nattot);
              }
              if(!"".equals(nattot)){
                res = smatManager.updRdaAnnullabile("GARATT",nattot,codgar);
                if(res <0){
                  throw new GestoreException("Errore nell'annullamento " +
                  " della RdA  (" + nattot +")", null);
                }
              }
            }
          }
        }
        res = smatManager.updLotto(codiceGara, codiceLotto);
        Double imptor = smatManager.updImportoTotaleTorn(codiceGara);
        if (datiForm.isColumn("TORN.IMPTOR")) {
          datiForm.setValue("TORN.IMPTOR",imptor);
        }
      }
    }


    // si aggiorna anche ISTAUT
    if (datiForm.isColumn("V_GARE_IMPORTI.VALMAX") && datiForm.isColumn("TORN.IMPTOR")) {
      Double importoContributo = this.pgManager.getContributoAutoritaStAppaltante(
          datiForm.getColumn("V_GARE_IMPORTI.VALMAX").getValue().doubleValue(), "A1z02");
      datiForm.addColumn("TORN.ISTAUT", JdbcParametro.TIPO_DECIMALE,
          importoContributo);
      if (importoContributo == null) {
        // questo set serve a permettere l'annullamento del dato nel campo
        // corrispondente, in quanto si aggiunge una colonna, il cui valore se è null non viene usato per insert/update
        datiForm.getColumn("TORN.ISTAUT").setOriginalValue(
            new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Double(1)));
      }
    }

    // Allineamento dei dati del bando in tutti i lotti se si è nella pagina dei
    // dati generali di TORN (ed è presente ad esempio il campo TORN.DPUBAV).
    // Se invece si chiama preUpdate da GARE, allora non si deve eseguire
    // updateLottiGara
    //Poichè per la gestione delle gare a lotti con offerte uniche inserisco
    //il campo NGARA.GARE, il controllo se siamo su TORN lo effettuo su CODGAR1
    if (!datiForm.isColumn("GARE.CODGAR1") && datiForm.isColumn("TORN.DPUBAV")){
      this.updateLottiGara(datiForm,"UPDATE");
    }
    //Si deve aggiornare il valore di AQOPER dei lotti
    if(datiForm.isColumn("TORN.AQOPER") && datiForm.isModifiedColumn("TORN.AQOPER")){
      Long aqoper = datiForm.getLong("TORN.AQOPER");
      Long aqoperOriginale = datiForm.getColumn("TORN.AQOPER").getOriginalValue().longValue();
      if(aqoper==null || (aqoper!=null && aqoper.longValue()==1) || (aqoperOriginale==null && aqoper!=null && aqoper.longValue()==2)){
        Long aqnumope = datiForm.getLong("TORN.AQNUMOPE");
        try {
          this.sqlManager.update("update gare1 set aqoper=?, aqnumope=? where codgar1=? and ngara!=codgar1", new Object[]{aqoper,aqnumope,codgar});
        } catch (SQLException e) {
          throw new GestoreException("Errore nell'aggiornamento dei valori di GARE1.AQOPER e GARE1.AQNUMOPE dei lotti",
              null, e);
        }

      }
    }

    // Gestione delle sezioni 'Pubblicazioni' nel tab "Pubblicazione bando': è
    // stato associato questo gestore nel tab delle pubblicazioni, ma in realtà
    // di TORN non va aggiornato niente bensì si aggiornano le occorrenze nella
    // tabella PUBBLI
    AbstractGestoreChiaveNumerica gestorePUBBLI = new DefaultGestoreEntitaChiaveNumerica(
        "PUBBLI", "NUMPUB", new String[] { "CODGAR9" }, this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
        gestorePUBBLI, "PUBBANDO",
        new DataColumn[] { datiForm.getColumn("TORN.CODGAR") }, null);


    //Gestione delle sezioni coperture assicurative
    if(datiForm.isColumn("GARE.GENERE") && datiForm.getLong("GARE.GENERE")!= null && datiForm.getLong("GARE.GENERE").longValue() == 3){
      AbstractGestoreChiaveNumerica gestoreGARASS = new DefaultGestoreEntitaChiaveNumerica(
          "GARASS", "NUMASS", new String[] { "NGARA" }, this.getRequest());
      this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
          gestoreGARASS, "ASS",
          new DataColumn[] { datiForm.getColumn("GARE.NGARA") }, null);
    }

    //Gestione delle sezioni n cup
    if(datiForm.isColumn("GARE.GENERE") && datiForm.getLong("GARE.GENERE")!= null && datiForm.getLong("GARE.GENERE").longValue() == 3){
      AbstractGestoreChiaveIDAutoincrementante gestoreGARECUP = new DefaultGestoreEntitaChiaveIDAutoincrementante(
          "GARECUP", "ID", this.getRequest());
      this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
          gestoreGARECUP, "GARECUP", new DataColumn[] {datiForm.getColumn("GARE.NGARA")}, null);
    }

    //Aggiornamento date pubblicazione
    if (datiForm.isColumn("NUMERO_PUBBANDO")) {
      this.updateDatePubblicazione(datiForm);
    }


    //Gestione particolare per Gara divisa in lotti con offerta unica
    if (datiForm.isColumn("V_GARE_TORN.TIPOLOGIA") && datiForm.getLong("V_GARE_TORN.TIPOLOGIA").longValue() == 3) {
    	datiForm.addColumn("GARE.NGARA", JdbcParametro.TIPO_TESTO,
				datiForm.getString("TORN.CODGAR"));
		datiForm.getColumn("GARE.NGARA").setChiave(true);
		try {
			datiForm.update("GARE", this.geneManager.getSql());
		} catch (SQLException e) {
			throw new GestoreException("Errore nel salvataggio dei dati in GARE",
			          null, e);
		}

		//Aggiornamenti per la pagina "Dati generali"
		if (datiForm.isColumn("CATG.CATIGA")) {
			// Gestione della sezione 'Categoria prevalente'
		    GestoreCATG.gestisciEntitaDaGare(this.getRequest(), status, datiForm,
		        datiForm.getColumn("TORN.CODGAR"));

		 // Gestione delle sezioni 'Ulteriori categorie'
		    AbstractGestoreChiaveNumerica gestoreOPES = new DefaultGestoreEntitaChiaveNumerica(
		        "OPES", "NOPEGA", new String[] { "NGARA3" }, this.getRequest());
		    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
		    		gestoreOPES, "OPES",
		        new DataColumn[] { datiForm.getColumn("TORN.CODGAR") }, null);

		    //Cancello le eventuali categorie sul lotto
		    try {
          this.gestisciAggiornamentiCategorieLotti(status, datiForm,
                      gestoreOPES, "OPES",
                  new DataColumn[] { datiForm.getColumn("TORN.CODGAR") }, null,datiForm.getColumn("TORN.CODGAR").getValue().stringValue());
        } catch (SQLException e) {
              throw new GestoreException("Errore nell'aggiornamento di OPES",
              null, e);
        }

		}

		//Aggiornamenti per la pagina "Altri Dati"
		if (datiForm.isColumn("GARCPV.CODCPV")) {
			//Gestione CPV
			this.updateGARCPV(status, datiForm);
		}

	    //Controllo sulla modifica del campo ELENCOE
	    if(datiForm.isColumn("GARE.ELENCOE") && datiForm.isModifiedColumn("GARE.ELENCOE")){
	      //Si controlla se vi sono delle ditte inserite da elenco
	      if(pgManagerEst1.esistonoDitteInGara(datiForm.getString("GARE.NGARA"))){
            Long iterga=null;
            if(datiForm.isColumn("TORN.ITERGA"))
              iterga= datiForm.getLong("TORN.ITERGA");
            String msgTipoElencoCatalogo="l'elenco";
            if(new Long(6).equals(iterga))
              msgTipoElencoCatalogo=" catalogo";
            throw new GestoreException("Non è possibile modificare il riferimento all'elenco poichè vi sono delle ditte in gara","modificaElencoe.DitteAssociate",
                new Object[]{msgTipoElencoCatalogo}, new Exception());
          }
	    }
    }
    //Integrazione Istituto zooprofilattico Piemonte
    if (datiForm.isColumn("GARE.CLIV1") && datiForm.isModifiedColumn("GARE.CLIV1")) {
      Long cliv1 = datiForm.getLong("GARE.CLIV1");
      Long valoreTipforn = null;
      if(cliv1!= null)
        valoreTipforn = new Long(98);
      if(!datiForm.isColumn("TORN.TIPFORN"))
        datiForm.addColumn("TORN.TIPFORN", JdbcParametro.TIPO_NUMERICO);
      datiForm.setValue("TORN.TIPFORN", valoreTipforn);
      datiForm.setOriginalValue("TORN.TIPFORN", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(100)));

    }
    if(datiForm.isColumn("V_GARE_TORN.TIPOLOGIA")){
      Long tipologiaGara = datiForm.getLong("V_GARE_TORN.TIPOLOGIA");
      if(tipologiaGara!=null && tipologiaGara.longValue()==3 && sqlManager.isTable("V_GARE_OUT")){
        try {
          sqlManager.update("update gare set bustalotti=? where ngara=?", new Object[]{new Long(2), datiForm.getString("TORN.CODGAR")});
        } catch (SQLException e) {
          throw new GestoreException("Errore nell'aggiornamento di GARE.BUSTALOTTI",
              null, e);
        }
      }
    }
    //Aggiornamento GARE1
    if(datiForm.isModifiedTable("GARE1")){
      AbstractGestoreEntita gestoreGARE1 = new DefaultGestoreEntita("GARE1", this.getRequest());
      gestoreGARE1.update(status, datiForm);
    }

    //Se si modifica il campo TORN.CENINT o il campo TORN.ALTRISOG nel caso di gara non a lotto unico
    //per ogni lotto si deve cancellare l'eventuale occorrenza di GARALTSOG
    //Se sono stati modificati i valori di ALTRISOG e ACCQUA ed i valori iniziali erano rispettivamente
    //3 e 1, si devono cancellare le occorrenze i GARALTSOG.
    //Nel caso di gare ad offerte distinte si devono cancellare le occorrenze per ogni lotto
    if((codgar.indexOf("$")<0 && datiForm.isColumn("TORN.CENINT") && datiForm.isModifiedColumn("TORN.ALTRISOG") &&
        (datiForm.isModifiedColumn("TORN.CENINT") || datiForm.isModifiedColumn("TORN.ALTRISOG"))) ||
        ((codgar.indexOf("$")==0 || datiForm.isColumn("GARE.GENERE")) && (datiForm.isColumn("TORN.ALTRISOG") &&
        (datiForm.isModifiedColumn("TORN.ALTRISOG") ||
        (datiForm.isColumn("TORN.ACCQUA") && datiForm.isModifiedColumn("TORN.ACCQUA") && !datiForm.isModifiedColumn("TORN.ALTRISOG") && datiForm.getDouble("TORN.ALTRISOG")==null))))){
      try {
        List lotti = sqlManager.getListVector("select ngara from gare where codgar1=? and ngara!=codgar1", new Object[]{codgar});
        if(lotti!=null){
          String lotto = null;
          for(int i=0;i<lotti.size();i++){
            lotto = SqlManager.getValueFromVectorParam(lotti.get(i), 0).getStringValue();
            this.sqlManager.update("delete from garaltsog where ngara=?", new Object[]{lotto});
          }
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'aggiornamento di GARALTSOG",
            null, e);
      }
    }

    //Inizializzazione del campo MODREA, ma solo per lotto unico e per offerte distinte o offerta unica (ma non per i lotti)
    /*
    if(datiForm.isColumn("TORN.MODREA") && (datiForm.getString("TORN.MODREA") == null || "".equals(datiForm.getString("TORN.MODREA"))) && datiForm.isColumn("TORN.ACCQUA") && datiForm.isColumn("TORN.ALTRISOG")
        && (datiForm.isColumn("TORN.TIPGAR") || datiForm.isColumn("GARE.TIPGARG")) && (datiForm.isModifiedColumn("TORN.ALTRISOG") || datiForm.isModifiedColumn("TORN.ACCQUA") ||
            (datiForm.isColumn("TORN.TIPGAR") && datiForm.isModifiedColumn("TORN.TIPGAR")) || (datiForm.isColumn("GARE.TIPGARG") && datiForm.isModifiedColumn("GARE.TIPGARG")))) {
    */
    if(datiForm.isColumn("TORN.MODREA") && datiForm.isColumn("TORN.ACCQUA") && datiForm.isColumn("TORN.ALTRISOG") && (datiForm.isColumn("TORN.TIPGAR") || datiForm.isColumn("GARE.TIPGARG"))
        && ((datiForm.getString("TORN.MODREA") == null || "".equals(datiForm.getString("TORN.MODREA"))) || ( datiForm.getString("TORN.MODREA") != null &&  !"".equals(datiForm.getString("TORN.MODREA"))
        && (datiForm.isModifiedColumn("TORN.ALTRISOG") || datiForm.isModifiedColumn("TORN.ACCQUA") ||
            (datiForm.isColumn("TORN.TIPGAR") && datiForm.isModifiedColumn("TORN.TIPGAR")) || (datiForm.isColumn("GARE.TIPGARG") && datiForm.isModifiedColumn("GARE.TIPGARG")))))) {
      Long tipgar = null;
      if(datiForm.isColumn("TORN.TIPGAR"))
        tipgar = datiForm.getLong("TORN.TIPGAR");
      else
        tipgar = datiForm.getLong("GARE.TIPGARG");
      String modrea = this.pgManagerEst1.getModrea(datiForm.getString("TORN.ACCQUA"), datiForm.getLong("TORN.ALTRISOG"), tipgar);
      datiForm.setValue("TORN.MODREA", modrea);
    }

    //Integrazione con WSERP
    String urlWSERP = ConfigManager.getValore("wserp.erp.url");
    if(urlWSERP != null && !"".equals(urlWSERP)){
      gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
          this.getServletContext(), GestioneWSERPManager.class);
      WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
      if(configurazione.isEsito()){
        String tipoWSERP = configurazione.getRemotewserp();
        tipoWSERP = UtilityStringhe.convertiNullInStringaVuota(tipoWSERP);
        if("AVM".equals(tipoWSERP) || "UGOVPA".equals(tipoWSERP)){
          //nel caso in cui si modifica TORN.ACCQUA occorre verificare che non siano presenti le rda complementari...
          if(datiForm.isColumn("TORN.ACCQUA") && datiForm.isModifiedColumn("TORN.ACCQUA")){
            String accqua = datiForm.getString("TORN.ACCQUA");
            accqua = UtilityStringhe.convertiNullInStringaVuota(accqua);
            if("1".equals(accqua)){
              try {
                Long countRdaGcap = (Long) this.sqlManager.getObject(
                    "select count(*) from GARE,GCAP where CODGAR1 = ? and GARE.NGARA=GCAP.NGARA and CODRDA is not null",
                      new Object[] { codgar });
                if(!new Long(0).equals(countRdaGcap)){
                  throw new GestoreException(
                      "Prima di finalizzare la gara alla conclusione di accordo quadro occorre eliminare le rda in lista lavorazioni",
                      "finalizzaAcqRdaGcap", null);
                }
              } catch (SQLException e) {
                throw new GestoreException("Errore nella lettura delle rda in GCAP", null, e);
              }
            }else{
              try {
                Long countRdaGarerda = (Long) this.sqlManager.getObject(
                    "select count(*) from GARERDA where CODGAR = ? and NUMRDA is not null",
                      new Object[] { codgar });
                if(!new Long(0).equals(countRdaGarerda)){
                  throw new GestoreException(
                      "Prima di finalizzare la gara alla conclusione di accordo quadro occorre eliminare le rda in lista lavorazioni",
                      "finalizzaAcqRdaGarerda", null);
                }
              } catch (SQLException e) {
                throw new GestoreException("Errore nella lettura delle rda in GARERDA", null, e);
              }
            }
          }
        }
      }
    }//if wserp


    String descrErrMsg = "";
    String bloccoModificaPubblicazione = UtilityStruts.getParametroString(this.getRequest(), "bloccoModificaPubblicazione");
    String paginaAltriDatiTorn = UtilityStruts.getParametroString(this.getRequest(), "paginaAltriDatiTorn");
    String paginaDatiGenTorn = UtilityStruts.getParametroString(this.getRequest(), "paginaDatiGenTorn");
    if("TRUE".equals(bloccoModificaPubblicazione) && ("si".equals(paginaAltriDatiTorn) || "si".equals(paginaDatiGenTorn))){
      HashMap<String, DataColumn> hm = datiForm.getColonne();
      Iterator it = hm.entrySet().iterator();
      boolean tracciareCampo=true;
      boolean modificheDaTracciare=false;
      while (it.hasNext()) {
          Entry<String, DataColumn> pair = (Entry<String, DataColumn>)it.next();
          if(pair.getValue().isModified()){
            //condizioni per non tracciare i campi:
            //campi fittizZi che contengo nel nome la stringa "FIT"
        	//Campi fittizzi delle sezioni dinamiche che contengono nel nome le stringhe "INDICE_" e "MOD_"
            if(pair.getKey().indexOf("FIT")>=0 || pair.getKey().indexOf("INDICE_")>=0 || pair.getKey().indexOf("MOD_")>=0||
            		pair.getKey().indexOf("CAIS.TIPLAVG")>=0 || pair.getKey().indexOf("STATOGARA")>=0 || pair.getKey().indexOf("OGGCONT_")>=0)
              tracciareCampo=false;
            else
              tracciareCampo=true;

            if(tracciareCampo){
              modificheDaTracciare = true;
              if(!"".equals(descrErrMsg)){
                descrErrMsg+="\n";
              }
              if(pair.getKey().indexOf("DEL_ULTREFINC")>0){
                  //Nel caso di eliminazione di tecnici ulteriori non ho le chiavi della occorrenza eliminata, quindi
                  //il messaggio sarà generico
                  descrErrMsg+= "Eliminata una occorrenza di GARTECNI.";
              }else{
                  String valOriginale = pair.getValue().getOriginalValue().toString();
                  String valNuovo = pair.getValue().getValue().toString();
                  if("".equals(valOriginale)){valOriginale = "NULL";}
                  if("".equals(valNuovo)){valNuovo = "NULL";}
                  descrErrMsg+= pair.getKey() + ": ";
                  descrErrMsg+= "valore vecchio= " + valOriginale + ", ";
                  descrErrMsg+= "valore nuovo= " + valNuovo;
              }
            }
          }
      }

      if(modificheDaTracciare){
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(1);
        logEvento.setOggEvento(datiForm.getColumn("TORN.CODGAR").getValue().stringValue());
        logEvento.setCodEvento("GA_MODIFICA_PROCEDURA_PUB");
        logEvento.setDescr("Modifica dati gara o lotto di gara dopo pubblicazione su portale Appalti.");
        logEvento.setErrmsg("Dettaglio modifiche:\n" + descrErrMsg);
        LogEventiUtils.insertLogEventi(logEvento);
      }
    }

    try {
      this.controlloCategoriaElenco(datiForm, "CATG.CATIGA", "1,3,4,5,11,12");
      this.controlloCategoriaElenco(datiForm, "CATG.NUMCLA", "4,5,12");
    } catch (SQLException e) {
      throw new GestoreException("Errore nel controllo della categoria",null, e);
    }

  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postUpdate(it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  /**
   * Aggiorna alcuni campi di tutti i lotti della gara in base ai dati della
   * gara stessa (tabella TORN). Per le gare divise a lotti con offerta unica
   * vi è l'aggiornamento anche in inserimento, ma solo per la gara complementare.
   *
   * @param datiForm
   *        contenitore con i dati del form
   * @param modo
   *        può essere "INSERT" o "UPDATE"
   *
   * @throws GestoreException
   */
  private void updateLottiGara(DataColumnContainer datiForm, String modo)
      throws GestoreException {
	  String sql = "update GARE set DPUBAVG=?, DFPUBAG=?, NAVVIGG=?, DAVVIGG=?, DIBANDG=?, TATTOG=?, DATTOG=?, NATTOG=?, NPROAG=?, TIPGARG=?, TIPNEG=? where CODGAR1=?";

    Object[] params = new Object[12];
    Object[] params_gara_offertaunica = null;
    Object[] params_lotti_offertaunica = null;

    params[0] = datiForm.getData("TORN.DPUBAV");
    params[1] = datiForm.getData("TORN.DFPUBA");
    params[2] = datiForm.getString("TORN.NAVVIG");
    params[3] = datiForm.getData("TORN.DAVVIG");
    params[4] = datiForm.getData("TORN.DIBAND");
    params[5] = datiForm.getLong("TORN.TATTOT");
    params[6] = datiForm.getData("TORN.DATTOT");
    params[7] = datiForm.getString("TORN.NATTOT");
    params[8] = datiForm.getString("TORN.NPROAT");
    params[9] = datiForm.getLong("TORN.TIPGAR");
    params[10] = datiForm.getLong("TORN.TIPNEG");
    params[11] = datiForm.getString("TORN.CODGAR");

    try {
      this.sqlManager.update(sql, params);
      //Aggiorna sempre RIBCAL e MODASTG sulla gara complementare
      //Aggiorna RIBCAL sui lotti solo se con offerta prezzi e solo nel caso la gara complementare sia con offerta prezzi
      //Non aggiorna mai MODASTG sui lotti
      if (datiForm.isColumn("V_GARE_TORN.TIPOLOGIA") && datiForm.getLong("V_GARE_TORN.TIPOLOGIA").longValue() == 3) {
        sql = "update GARE set MODASTG=?,RIBCAL=? WHERE CODGAR1=? AND GENERE=3";
        params_gara_offertaunica = new Object[3];
        params_gara_offertaunica[0] = datiForm.getLong("GARE.MODASTG");
        params_gara_offertaunica[1] = datiForm.getLong("GARE.RIBCAL");
        params_gara_offertaunica[2] = datiForm.getString("TORN.CODGAR");
        this.sqlManager.update(sql, params_gara_offertaunica);
        Long modalitaAggiudicazione =  datiForm.getLong("TORN.MODLIC");
        if (modalitaAggiudicazione == null)
          modalitaAggiudicazione = new Long(0);
        if (modalitaAggiudicazione.longValue() == 5 || modalitaAggiudicazione.longValue() == 14 || modalitaAggiudicazione.longValue() == 16){
          sql = "update GARE set RIBCAL=? WHERE CODGAR1=? AND GENERE is NULL AND MODLICG in (5,14)";
          params_lotti_offertaunica = new Object[2];
          params_lotti_offertaunica[0] = datiForm.getLong("GARE.RIBCAL");
          params_lotti_offertaunica[1] = datiForm.getString("TORN.CODGAR");
          this.sqlManager.update(sql, params_lotti_offertaunica);
        }
        if("UPDATE".equals(modo)){
          //Gestione aggiornamento SICINC dei lotti
          Long detlic =  datiForm.getLong("TORN.DETLIC");
          Long critlic =  datiForm.getLong("TORN.CRITLIC");
          if((detlic!=null && (detlic.longValue()==3 || detlic.longValue()==4)) || (critlic!=null && critlic.longValue()==2)){
            String sicinc =  datiForm.getString("GARE.SICINC");
            sql = "update GARE set SICINC=? WHERE CODGAR1=? AND GENERE is NULL AND (MODLICG in (5,14,6) or (DETLICG=4 and MODLICG in (1,13,17)))";
            params_lotti_offertaunica = new Object[2];
            params_lotti_offertaunica[0] = sicinc;
            params_lotti_offertaunica[1] = datiForm.getString("TORN.CODGAR");
            this.sqlManager.update(sql, params_lotti_offertaunica);
          }
        }

      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'aggiornamento dei lotti della gara '"
              + params[9]
              + "'", "updateLottiGara", null, e);
    }
  }

  /**
   * Aggiorna sia la prima occorrenza dei CPV, sia le n occorrenze successive
   *
   * @param status
   * @param datiForm
   * @throws GestoreException
   */
  private void updateGARCPV(TransactionStatus status,
      DataColumnContainer datiForm) throws GestoreException {
    // Aggiornamento dell'occorrenza principale
    if (datiForm.isModifiedColumn("GARCPV.CODCPV")) {
      Vector colonneGARCPV = new Vector();
      colonneGARCPV.add(datiForm.getColumn("GARCPV.NGARA"));
      colonneGARCPV.add(datiForm.getColumn("GARCPV.NUMCPV"));
      colonneGARCPV.add(datiForm.getColumn("GARCPV.CODCPV"));
      colonneGARCPV.add(datiForm.getColumn("GARCPV.TIPCPV"));

      DefaultGestoreEntitaChiaveNumerica gestoreGARCPV = new DefaultGestoreEntitaChiaveNumerica(
          "GARCPV", "NUMCPV", new String[] { "NGARA" }, this.getRequest());
      if (datiForm.getColumn("GARCPV.NUMCPV").getValue().getValue() == null)
        gestoreGARCPV.inserisci(status, new DataColumnContainer(colonneGARCPV));
      else
        gestoreGARCPV.update(status, new DataColumnContainer(colonneGARCPV));
    }

    // Aggiornamento delle occorrenze complementari
    AbstractGestoreChiaveNumerica gestoreGARCPV = new DefaultGestoreEntitaChiaveNumerica(
        "GARCPV", "NUMCPV", new String[] { "NGARA" }, this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
        gestoreGARCPV, "CPVCOMP",
        new DataColumn[] { datiForm.getColumn("TORN.CODGAR") }, null);

  }

  /**
   * Viene effettuato effettuato l'aggiornamento delle date di pubblicazione seguendo il
   * criterio di priorità: Bando GUUE,Bando GURI e Albo pretorio stazione appaltante
   *
   * @param datiForm
   *
   * @throws GestoreException
   */
  private void updateDatePubblicazione(DataColumnContainer datiForm) throws GestoreException {

    String selectNUumpub="select min(numpub) from pubbli where CODGAR9 = ? and tippub =?";
    String codiceGara=datiForm.getString("TORN.CODGAR");
    String updateTorn="update torn set DIBAND=?,DAVVIG=?,DPUBAV=?,DFPUBA=? where codgar=?";
    String updateGare = "update gare set DIBANDG=?,DPUBAVG=?,DFPUBAG=?,DAVVIGG=? where codgar1=?";
    boolean bandoPresente=false;
    String select=null;

    try {
      Timestamp diband = null;
      Timestamp davvig = null;
      Timestamp dpubav = null;
      Timestamp dfpuba = null;
      Long numpub = null;

      numpub = (Long)this.sqlManager.getObject(selectNUumpub, new Object[]{codiceGara,new Long(3)});
      if (numpub!= null && numpub.longValue()>0){
        bandoPresente = true;
        select="select DINPUB,DATPUB from pubbli where codgar9=? and numpub=?";
        Vector dati = this.sqlManager.getVector(select, new Object[]{codiceGara,numpub});
        diband = SqlManager.getValueFromVectorParam(dati, 0).dataValue();
        davvig = SqlManager.getValueFromVectorParam(dati, 0).dataValue();
        dpubav = SqlManager.getValueFromVectorParam(dati, 1).dataValue();
      }

      numpub = (Long)this.sqlManager.getObject(selectNUumpub, new Object[]{codiceGara,new Long(4)});
      if (numpub!= null && numpub.longValue()>0 && !bandoPresente){
        bandoPresente = true;
        select="select DINPUB,DATPUB from pubbli where codgar9=? and numpub=?";
        Vector dati = this.sqlManager.getVector(select, new Object[]{codiceGara,numpub});
        davvig = SqlManager.getValueFromVectorParam(dati, 0).dataValue();
        dpubav = SqlManager.getValueFromVectorParam(dati, 1).dataValue();
      }

      numpub = (Long)this.sqlManager.getObject(selectNUumpub, new Object[]{codiceGara,new Long(2)});
      if (numpub!= null && numpub.longValue()>0 && !bandoPresente){
        bandoPresente = true;
        select="select DATPUB,DATFIPUB from pubbli where codgar9=? and numpub=?";
        Vector dati = this.sqlManager.getVector(select, new Object[]{codiceGara,numpub});
        davvig = SqlManager.getValueFromVectorParam(dati, 0).dataValue();
        dpubav = SqlManager.getValueFromVectorParam(dati, 0).dataValue();
        dfpuba = SqlManager.getValueFromVectorParam(dati, 1).dataValue();
      }

      this.sqlManager.update(updateTorn, new Object[]{diband,davvig,dpubav,dfpuba,codiceGara});
      this.sqlManager.update(updateGare, new Object[]{diband,dpubav,dfpuba,davvig,codiceGara});
    } catch (SQLException e) {
      throw new GestoreException("Errore nel salvataggio dei dati in GARE",
          null, e);
    }
  }
  /**
   * Inizializza il collegamento con l'albo per la commissione
   * @param page
   * @param sqlManager
   * @throws SQLException
   */
  private void setALBOCOMM(DataColumnContainer datiForm)
  throws GestoreException {

    String selectAlboComm="select count(*) from commalbo ";
    Long countAlboComm;
    long countAC = 0;
    try {
      countAlboComm = (Long) this.sqlManager.getObject(selectAlboComm, new Object[] { });
      countAC = countAlboComm.longValue();
    } catch (SQLException e) {
      throw new GestoreException("Errore nell'aggiornamento di TORN", null, e);
    }
    if(countAC > 0){
      datiForm.setValue("TORN.IDCOMMALBO", new Long(1));
    }

  }

  /*
   * Verifico che una stringa rappresenti un numeor
   */
  private static boolean isNumeric(String str) {

    boolean numerico = true;
    char[] seq = str.toCharArray();

    for (int i=0; i< seq.length; i++) {
      try {
        Integer.parseInt(Character.toString(seq[i]));
      } catch (Exception e) {
        numerico = false;
      }
    }

    return numerico;
  }

  private void gestisciAggiornamentiRecordSchedaMultiplaRda(
      TransactionStatus status, DataColumnContainer dataColumnContainer,
      AbstractGestoreChiaveIDAutoincrementante gestore, String suffisso,
      DataColumn[] valoreChiave, String[] campiDaNonAggiornare, String idconfi)
      throws GestoreException {

    ///////////////////////////////////////////////////////////
    // ATTENZIONE: METODO CON UTILIZZO ID AUTOINCREMENTANTE!!!!
    ///////////////////////////////////////////////////////////

    //Integrazione con WSERP
    String tipoWSERP = null;
    String urlWSERP = ConfigManager.getValore("wserp.erp.url");
    if(urlWSERP != null && !"".equals(urlWSERP)){
      gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
          this.getServletContext(), GestioneWSERPManager.class);
      WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
      if(configurazione.isEsito()){
        tipoWSERP = configurazione.getRemotewserp();
      }
    }
    tipoWSERP = UtilityStringhe.convertiNullInStringaVuota(tipoWSERP);


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

      // Sabbadin 07/12/2011: spostato fuori dal ciclo questo controllo in modo
      // da fare una volta sola la verifica e l'append dell'entita' (SE
      // NECESSARIA) al nome di campo da non aggiornare
      if (campiDaNonAggiornare != null) {
        for (int j = 0; j < campiDaNonAggiornare.length; j++)
          if (campiDaNonAggiornare[j].indexOf('.') == -1)
            campiDaNonAggiornare[j] = gestore.getEntita()
                + "."
                + campiDaNonAggiornare[j];
      }

      for (int i = 1; i <= numeroRecord; i++) {
        DataColumnContainer newDataColumnContainer = new DataColumnContainer(
            tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

        boolean deleteOccorrenza = newDataColumnContainer.isColumn(nomeCampoDelete)
            && "1".equals(newDataColumnContainer.getString(nomeCampoDelete));
        boolean updateOccorrenza = newDataColumnContainer.isColumn(nomeCampoMod)
            && "1".equals(newDataColumnContainer.getString(nomeCampoMod));

        // Rimozione dei campi fittizi (il campo per la marcatura della delete e
        // tutti gli eventuali campi passati come argomento)
        newDataColumnContainer.removeColumns(new String[] {
            gestore.getEntita() + "." + nomeCampoDelete,
            gestore.getEntita() + "." + nomeCampoMod});

        if (campiDaNonAggiornare != null) {
          newDataColumnContainer.removeColumns(campiDaNonAggiornare);
        }

        if (deleteOccorrenza) {
          // Se è stata richiesta l'eliminazione e il campo chiave ID incrementante e'
          // diverso da null eseguo l'effettiva eliminazione del record
              if("AVM".equals(tipoWSERP) || "UGOVPA".equals(tipoWSERP) || "CAV".equals(tipoWSERP)){
                if (newDataColumnContainer.getLong(gestore.getCampoNumericoChiave()) != null){
                  String codgar  = newDataColumnContainer.getString("GARERDA.CODGAR");
                  String codiceRda  = newDataColumnContainer.getString("GARERDA.NUMRDA");
                  String posizioneRda  = newDataColumnContainer.getString("GARERDA.POSRDA");
                  int res = this.gestioneWSERPManager.scollegaRda(codgar, null, "1", codiceRda, posizioneRda, this.getRequest());
                  if(res >= 0){
                    gestore.elimina(status, newDataColumnContainer);
                  }else{
                    throw new GestoreException(
                        "Errore durante l'operazione di scollegamento delle RdA dalla gara",
                        "scollegaRdaGara", null);
                  }
                }
              }else{//non c'e' integrazione WSERP
                if (newDataColumnContainer.getLong(gestore.getCampoNumericoChiave()) != null){
                  gestore.elimina(status, newDataColumnContainer);
                }
              }

          // altrimenti e' stato eliminato un nuovo record non ancora inserito
          // ma predisposto nel form per l'inserimento
        } else {
          if (updateOccorrenza) {
            if (newDataColumnContainer.getLong(gestore.getCampoNumericoChiave()) == null){
              gestore.inserisci(status, newDataColumnContainer);
              //integrazione ERP vs WSDM
              if(dataColumnContainer.isColumn("INTEGRAZIONE_ERPvsWSDM")){
                String integrazioneERPvsWSDM  = dataColumnContainer.getString("INTEGRAZIONE_ERPvsWSDM");
                if(integrazioneERPvsWSDM == null || "".equals(integrazioneERPvsWSDM)){
                  integrazioneERPvsWSDM = (String) this.getRequest().getAttribute("INTEGRAZIONE_ERPvsWSDM");
                }
                integrazioneERPvsWSDM = UtilityStringhe.convertiNullInStringaVuota(integrazioneERPvsWSDM);
                if("1".equals(integrazioneERPvsWSDM)){
                  gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
                      this.getServletContext(), GestioneWSDMManager.class);
                  String codgar  = newDataColumnContainer.getString("GARERDA.CODGAR");
                  boolean isFascicolazioneAttiva = false;
                  try {
                    isFascicolazioneAttiva = gestioneWSDMManager.isApplicaFascicolazioneAbilitato(codgar,idconfi);
                  } catch (SQLException e) {
                    throw new GestoreException("Errore nella lettura della fascicolazione attiva", null, e);
                  }
                  String numeroRda = (String) newDataColumnContainer.getColumn("GARERDA.NUMRDA").getValue().getValue();
                  if(!"".equals(numeroRda)){
                    //leggo i dati della rda:
                    WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoResType = gestioneWSDMManager.wsdmDocumentoERPLeggi(numeroRda, idconfi.toString());
                    WSDMProtocolloDocumentoType wsdmProtocolloDocumento = wsdmProtocolloDocumentoResType.getProtocolloDocumento();

                    String classifica = wsdmProtocolloDocumento.getClassifica();
                    String oggetto = wsdmProtocolloDocumento.getOggetto();

                    if (wsdmProtocolloDocumento.getFascicolo() != null) {
                      WSDMFascicoloType fascicolo = wsdmProtocolloDocumento.getFascicolo();
                      Long annofasc = fascicolo.getAnnoFascicolo();
                      String numerofascicolo = fascicolo.getNumeroFascicolo();
                      String codicefascicolo = fascicolo.getCodiceFascicolo();

                      String ngara = codgar.substring(1);

                      try {
                        if(isFascicolazioneAttiva){
                          //inserisco in WSFASCICOLO
                          Long isRiservatezza = null;
                          WSDMConfigurazioneOutType configurazione = null;
                          try {
                            configurazione = gestioneWSDMManager.wsdmConfigurazioneLeggi("FASCICOLOPROTOCOLLO",idconfi);
                          } catch (GestoreException e) {
                            //non applico la riservatezza
                          }
                          if (configurazione != null && configurazione.isEsito()){
                            String tipoWSDM = configurazione.getRemotewsdm();
                            if("JIRIDE".equals(tipoWSDM)){
                              String riservatezzaAttiva = ConfigManager.getValore("wsdm.applicaRiservatezza."+idconfi);
                              if("1".equals(riservatezzaAttiva)){
                                isRiservatezza = new Long(1);
                              }
                            }
                          }
                          gestioneWSDMManager.setWSFascicolo("GARE", ngara, null, null, null, codicefascicolo, annofasc,
                              numerofascicolo, classifica,null,null,null,isRiservatezza,null,null,null,null);
                        }
                        //Salvatagio in WSDOCUMENTO
                        this.gestioneWSDMManager.setWSDocumento("GARE", ngara, null, null, null, numeroRda, null, null, oggetto,"INT");
                      } catch (SQLException e) {
                        throw new GestoreException("Errore nel salvataggio del numero Rda nella gara", null, e);
                      }
                    }
                  }
                }
              }//integrazione ERP vs WSDM
              if("FNM".equals(tipoWSERP) || "CAV".equals(tipoWSERP)){

                Long iterga  =  dataColumnContainer.getLong("TORN.ITERGA");
                String codgar  = newDataColumnContainer.getString("GARERDA.CODGAR");
                String codiceRda  = newDataColumnContainer.getString("GARERDA.NUMRDA");
                String codiceCarrello  = newDataColumnContainer.getString("GARERDA.CODCARR");
                String esercizio  = newDataColumnContainer.getString("GARERDA.ESERCIZIO");
                //solo per FNM
                codiceCarrello = esercizio;
                String codiceLotto = codgar.substring(1);

                String servizio = "WSERP";
                ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
                    CostantiGenerali.PROFILO_UTENTE_SESSIONE);
                Long syscon = new Long(profilo.getId());
                String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);
                String username = credenziali[0];
                String password = credenziali[1];

                WSERPRdaResType wserpRdaRes = this.gestioneWSERPManager.wserpAssociaRdaGara(username, password, servizio, codiceCarrello, codiceRda, null, codiceLotto, true);

                if(wserpRdaRes.isEsito()){
                  if("FNM".equals(tipoWSERP)){
                    //Carico gli allegati
                    WSERPRdaType[] rdaArray= new WSERPRdaType[1];
                    WSERPRdaType rda = new WSERPRdaType();
                    rda.setCodiceRda(codiceRda);
                    rda.setEsercizio(esercizio);
                    rdaArray[0] = rda;
                    wserpRdaRes  = this.gestioneWSERPManager.wserpListaFilesRda(username, password, servizio, rdaArray);
                    //riuso le stesse strutture
                    rdaArray = wserpRdaRes.getRdaArray();
                    rda = rdaArray[0];//eventualmente ciclare
                    WSERPAllegatoType[] allegatoArray = rda.getAllegatoArray();
                    if(allegatoArray != null){
                      for(int d = 0 ; d < allegatoArray.length; d++){
                        WSERPAllegatoType allegato = allegatoArray[d];
                        this.gestioneWSERPManager.gestioneFileAllegati(allegato, codgar, codiceLotto, null, iterga);
                      }//for
                    }
                  }
                  if("CAV".equals(tipoWSERP)){
                    Long idRda  = newDataColumnContainer.getLong("GARERDA.ID");
                    //lettura deal ws
                    WSERPRdaType rdaSearch = new WSERPRdaType();
                    rdaSearch.setCodiceRda(codiceRda);
                    rdaSearch.setTipoRdaErp(esercizio);
                    WSERPRdaResType dettaglioRdares= this.gestioneWSERPManager.wserpDettaglioRda(username, password, servizio, rdaSearch);
                    if(dettaglioRdares.isEsito()){
                      this.gestioneWSERPManager.insPosizioniRda(username, password, servizio, codgar, codiceLotto, idRda, dettaglioRdares.getRdaArray());
                    }
                  }
                }else{
                  throw new GestoreException(
                      "Errore durante l'operazione di collegamento delle RdA alla gara",
                      "collegaRdaGara", null);
                }
              }//FNM
            }
            else{
              gestore.update(status, newDataColumnContainer);
            }

          }
        }
      }
    }
  }

  private void gestisciAggiornamentiCategorieLotti(
      TransactionStatus status, DataColumnContainer dataColumnContainer,
      AbstractGestoreChiaveNumerica gestore, String suffisso,
      DataColumn[] valoreChiave, String[] campiDaNonAggiornare,String ngara)
      throws GestoreException, SQLException {

    DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(
        dataColumnContainer.getColumns(gestore.getEntita(), 0));

    String nomeCampoNumeroRecord = "NUMERO_" + suffisso;
    String nomeCampoDelete = "DEL_" + suffisso;

    if (dataColumnContainer.isColumn(nomeCampoNumeroRecord)) {
      int numeroRecord = dataColumnContainer.getLong(nomeCampoNumeroRecord).intValue();
      String nameRecord = null;
      for (int i = 1; i <= numeroRecord; i++) {
        DataColumnContainer newDataColumnContainer = new DataColumnContainer(
            tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

        boolean deleteOccorrenza = newDataColumnContainer.isColumn(nomeCampoDelete)
        && "1".equals(newDataColumnContainer.getString(nomeCampoDelete));

        if(deleteOccorrenza){
          String categoria = (String) newDataColumnContainer.getColumn("OPES.CATOFF").getValue().getValue();

          List listaLotti = sqlManager.getListVector("select ngara from gare where codgar1 = ? and codgar1 <> ngara",
                  new Object[] { ngara });
          if (listaLotti != null && listaLotti.size() > 0) {
            String ngaraLotto = null;
            for (int j = 0; j < listaLotti.size(); j++) {

              ngaraLotto = (String) SqlManager.getValueFromVectorParam(listaLotti.get(j), 0).getValue();
              this.sqlManager.update("delete from opes where ngara3=? and catoff = ?", new Object[]{ngaraLotto,categoria});
              this.sqlManager.update("delete from catg where ngara=? and catiga = ?", new Object[]{ngaraLotto,categoria});
            }
          }
        }
      }
    }
  }

  private void controlloCategoriaElenco(DataColumnContainer datiForm, String colonna, String tipoalgo) throws GestoreException, SQLException {
    if(datiForm.isColumn("GARE.NGARA")){
      String codiceLotto = datiForm.getString("GARE.NGARA");
      if( datiForm.isColumn(colonna) && datiForm.isModifiedColumn(colonna)){//if modificato categoria prevalente
        String elencoe = (String)this.sqlManager.getObject("select elencoe from gare where ngara=?", new Object[]{codiceLotto});
        if(elencoe != null){
          Long ditteDaElenco = (Long)this.sqlManager.getObject("select count(*) from ditg where ngara5=? and acquisizione = 3", new Object[]{codiceLotto});
          Long criterioRotazione = (Long)this.sqlManager.getObject("select count(*) from garealbo where ngara = ? and tipoalgo in (" + tipoalgo + ")", new Object[]{elencoe});
          if(ditteDaElenco != null && ditteDaElenco > 0 &&  criterioRotazione != null && criterioRotazione > 0 ){
            if("CATG.CATIGA".equals(colonna)){
              throw new GestoreException("Non è possibile modificare la categoria prevalente","controlloCategoriaElenco");
            }
            if("CATG.NUMCLA".equals(colonna)){
              throw new GestoreException("Non è possibile modificare la classifica della categoria prevalente","controlloCategoriaElencoClassifica");
            }
          }
        }
      }
    }
  }


}