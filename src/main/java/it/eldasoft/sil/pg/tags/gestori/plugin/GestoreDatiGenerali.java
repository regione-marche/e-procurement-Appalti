/*
 * Created on 16-lug-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.dm.WSDMCampoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAnagraficaType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoType;
import it.maggioli.eldasoft.ws.dm.WSDMRigaType;
import it.maggioli.eldasoft.ws.dm.WSDMTabellaType;
import it.maggioli.eldasoft.ws.erp.WSERPAnagraficaType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore di plugin dei dati generali di GARE
 *
 * @author Francesco.DeFilippis
 */
public class GestoreDatiGenerali extends AbstractGestorePreload {

  SqlManager sqlManager = null;

  TabellatiManager tabellatiManager = null;

  PgManager pgManager = null;

  PgManagerEst1 pgManagerEst1 = null;

  private GeneManager geneManager = null;

  private GestioneWSDMManager gestioneWSDMManager = null;

  private GestioneWSERPManager gestioneWSERPManager = null;

  public GestoreDatiGenerali(BodyTagSupportGene tag) {
    super(tag);
  }

  /**
   * Vengono caricati i valori della denominazione stazione appaltante (da
   * UFFINT) e cognome e nome tecnico (da TECNI) che non possono essere caricati
   * direttamente nella pagina dato che la chiave e legata a TORN e la pagina è
   * basata su GARE
   */
  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);

    tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        page, TabellatiManager.class);

    // Estraggo il manager di Piattaforma Gare
    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        page, PgManager.class);

    pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        page, PgManagerEst1.class);

    geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
        page, GeneManager.class);

    gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        page, GestioneWSDMManager.class);

    gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
        page, GestioneWSERPManager.class);

    HashMap key = null;
    String oggettoEvento = null;

    String modo = (String) page.getAttribute(
        UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO, PageContext.REQUEST_SCOPE);

    try {

      String descA1138 = tabellatiManager.getDescrTabellato("A1138", "1");
      if(descA1138!=null && !"".equals(descA1138)){
        descA1138=descA1138.substring(0, 1);
        page.setAttribute("tabellatoA1138", descA1138, PageContext.REQUEST_SCOPE);
      }
      if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modo)) {
        // carico la chiave dell'occorrenza
        key = UtilityTags.stringParamsToHashMap(
            (String) page.getAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA,
                PageContext.REQUEST_SCOPE), null);
        String ngara = ((JdbcParametro) key.get("GARE.NGARA")).getStringValue();
        oggettoEvento = ngara;
        String codgar = (String) sqlManager.getObject(
            "select codgar1 from gare where gare.ngara = ?",
            new Object[] { ngara });

        // carico NOMEIN.UFFINT tramite la chiave di collegamento CENINT.TORN
        String nomein = (String) sqlManager.getObject(
            "select nomein from uffint,torn where uffint.codein = torn.cenint and torn.codgar = ?",
            new Object[] { codgar });

        page.setAttribute("denominazione", nomein, PageContext.REQUEST_SCOPE);

        // carico NOMTEC.TECNI tramite CODRUP.TORN
        String nomtec = (String) sqlManager.getObject(
            "select nomtec from tecni,torn where tecni.codtec = torn.codrup and torn.codgar = ?",
            new Object[] { codgar });
        page.setAttribute("tecnico", nomtec, PageContext.REQUEST_SCOPE);

        // creo il parametro con la chiave da passare alla pagina di controllo
        // delle autorizzazioni
        String inputFiltro = "CODGAR=T:" + codgar;
        page.setAttribute("inputFiltro", inputFiltro, PageContext.REQUEST_SCOPE);

        //Se in modifica di un lotto di gara, verifica se CODIGA è valorizzato per rendere o meno modificabile il campo nella form
        if ("false".equals(page.getAttribute("garaLottoUnico", PageContext.REQUEST_SCOPE)) &&
            UtilityTags.SCHEDA_MODO_MODIFICA.equals(modo)){

          //Si devono caricare AQOPER, AQNUMOPE della gara
          String sqlTorn = "SELECT AQOPER, AQNUMOPE FROM TORN WHERE CODGAR = ?";
          Vector datiTorn = sqlManager.getVector(sqlTorn,
              new Object[] {codgar});

          page.setAttribute("initAQOPER",
              ((JdbcParametro) datiTorn.get(0)).getValue(), PageContext.REQUEST_SCOPE);
          page.setAttribute("initAQNUMOPE",
              ((JdbcParametro) datiTorn.get(1)).getValue(), PageContext.REQUEST_SCOPE);

        }

      } else {
        // Stringa valorizzata con la concatenazione del codice del lavoro e del
        // numero appalto relativi all'appalto selezionato, al quale l'utente
        // ha scelta di associare la nuova gara a lotto unico o il nuovo lotto
        // di gara
        String chiaveAppalto = page.getRequest().getParameter("chiaveRiga");
        String tipoAppalto = page.getRequest().getParameter("tipoAppalto");

        String numeroRda = page.getRequest().getParameter("numeroRda");
        numeroRda = UtilityStringhe.convertiNullInStringaVuota(numeroRda);

        String divisione = page.getRequest().getParameter("divisione");
        divisione = UtilityStringhe.convertiNullInStringaVuota(divisione);

        String integrazioneERPvsWSDM = page.getRequest().getParameter("integrazioneERPvsWSDM");
        integrazioneERPvsWSDM = UtilityStringhe.convertiNullInStringaVuota(integrazioneERPvsWSDM);
        String integrazioneWSERP="0";
        String tipoWSERP = "";
        String urlWSERP = ConfigManager.getValore("wserp.erp.url");
        urlWSERP = UtilityStringhe.convertiNullInStringaVuota(urlWSERP);
        if(!"".equals(urlWSERP)){
          integrazioneWSERP ="1";

          try {
            WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
            if (configurazione.isEsito()) {
              tipoWSERP = configurazione.getRemotewserp();
            }
          } catch (GestoreException e) {
            UtilityStruts.addMessage(page.getRequest(), "error",
                "wserpconfigurazione.erp.configurazioneleggi.remote.error",new Object[]{":\r\n Configurazione integrazione con sistema ERP non corretta"});
          }
        }

        String preced = page.getRequest().getParameter("preced");

        if(preced!=null && !"".equals(preced)){
           this.setDatiInitPerRilancio(page, sqlManager, chiaveAppalto, preced, tipoAppalto);

        }else if ("false".equals(page.getAttribute("garaLottoUnico",
            PageContext.REQUEST_SCOPE))) {
          // predisposizione dei dati di inizializzazione letti da TORN
          this.setDatiInitDaTorn(page, sqlManager);

          //Inizializzazioni da effettuare nel caso in cui il lotto di gara
          //non sia relativo ad una gara divisa in con offerte distinte
          if ("false".equals(page.getAttribute("lottoOffertaUnica",
                  PageContext.REQUEST_SCOPE))) {
	          // predispozizione dei dati di inizializzazione letti da CATG
	          this.setDatiInitDaCatg(page, sqlManager);

	          if(chiaveAppalto != null && chiaveAppalto.length() > 0){
	            // Inizializzazione dei campi della scheda dati generali di GARE per
	            // la creazione di un nuovo lotto di gara
	            this.setDatiInitDaAppa(page, sqlManager, chiaveAppalto, tipoAppalto, "lottoOfferteDistinte", integrazioneWSERP);
	          }
          }else if ("true".equals(page.getAttribute("lottoOffertaUnica",
                  PageContext.REQUEST_SCOPE))) {
        	  // Inizializzazione della percentuale della cauzione provvisoria
        	  this.initPGAROF(page, sqlManager,tipoAppalto);

        	  //inizializzazione del campo modastg
        	  this.initMODASTG(page, sqlManager);

        	  //inizializzazione del campo SICINC
              this.initSICINC(page, sqlManager);

              if(chiaveAppalto != null && chiaveAppalto.length() > 0){
                // Inizializzazione dei campi della scheda dati generali di GARE per
                // la creazione di un nuovo lotto di gara
                this.setDatiInitDaAppa(page, sqlManager, chiaveAppalto, tipoAppalto,"lottoOffertaUnica", integrazioneWSERP);
              }
          }
        } else if ("true".equals(page.getAttribute("garaLottoUnico",
            PageContext.REQUEST_SCOPE))) {
          if(chiaveAppalto != null && chiaveAppalto.length() > 0){
            // Inizializzazione dei campi della scheda dati generali di GARE per
            // la creazione di una nuova gara a lotto unico dal wizard di creazione
            // o dalla lista dei lotti di una gara o dalla scheda di un lotto di
            // gara
            this.setDatiInitDaAppa(page, sqlManager, chiaveAppalto, tipoAppalto,"garaLottoUnico", integrazioneWSERP);
            boolean datiAttoDaAppaPresenti=this.setDatiInitDaAppaAttoAutorizzativo(page, sqlManager, chiaveAppalto);
            //Inizializzazione dei dati della gara con i dati dell'approvazione
            this.setDatiInitDaAppr(page, sqlManager, chiaveAppalto,datiAttoDaAppaPresenti);

          }
          if(!"".equals(numeroRda)){
            //Inizializzazione dei dati della gara con i dati della rda
            String idconfi = page.getRequest().getParameter("idconfi");
            if("1".equals(integrazioneERPvsWSDM)){
              this.setDatiInitDaERPvsWSDM(page, sqlManager, numeroRda, idconfi);
            }else{
              if("1".equals(integrazioneWSERP) && "FNM".equals(tipoWSERP)){
                this.setDatiInitDaWSERP(page, sqlManager, numeroRda, divisione);
                ;//importo i dati
              }else{
                this.setDatiInitDaRda(page, sqlManager, numeroRda);
              }
            }


          }
        }
      }

      if ((!UtilityTags.SCHEDA_MODO_VISUALIZZA.equals(modo)) && "true".equals(page.getAttribute("lottoOffertaUnica",
              PageContext.REQUEST_SCOPE))){
    	//Carico nella pagina, solo per lotti di gara con offerta unica
          //l'arrotondamento da applicare nel calcolo della cauzione provvisoria
    	  int tipoAppalto;
    	  String codgar;
    	  if (UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modo)){
    		  String tipgen = page.getRequest().getParameter("tipoAppalto");
    		  tipoAppalto = Integer.parseInt(tipgen);
              HashMap keyParent = UtilityTags.stringParamsToHashMap(
                  (String) page.getAttribute(
                      UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT,
                      PageContext.REQUEST_SCOPE), null);
              codgar = ((JdbcParametro) keyParent.get("TORN.CODGAR")).getStringValue();
    	  }else{
    	    // carico la chiave dell'occorrenza
    	        key = UtilityTags.stringParamsToHashMap(
    	            (String) page.getAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA,
    	                PageContext.REQUEST_SCOPE), null);
    	        String ngara = ((JdbcParametro) key.get("GARE.NGARA")).getStringValue();

    	        codgar = (String) sqlManager.getObject(
    	                "select codgar1 from gare where gare.ngara = ?",
    	                new Object[] { ngara });

    	        //prelevo il valore di tipgen
    	        Long tipgen = (Long) sqlManager.getObject(
    	                "select tipgen from torn where codgar = ?",
    	                new Object[] { codgar });
    	        tipoAppalto = tipgen.intValue();
    	  }
    	  oggettoEvento = codgar;
    	  int numeroDecimali = pgManager.getArrotondamentoCauzioneProvvisoria(tipoAppalto);
    	  page.setAttribute("numeroDecimali", new Long(numeroDecimali), PageContext.REQUEST_SCOPE);

    	  //Carico i valori per l'inizializzazione del campo IDIAUT
    	  //this.getScaglioni(page, sqlManager,"A1z01");
    	  List listaScaglioni = this.pgManagerEst1.getScaglioni("A1z01");
    	  page.setAttribute("listaScaglioni", listaScaglioni, PageContext.REQUEST_SCOPE);

          //inizializzazione del campo ribcal - sia in inserimento del lotto che in modifica
          this.initRIBCAL(page, sqlManager, codgar);
      }

      String log = (String) page.getAttribute("log");
      if("true".equals(log)){
        try{
          LogEvento logEvento = LogEventiUtils.createLogEvento((HttpServletRequest) page.getRequest());
          logEvento.setLivEvento(1);
          logEvento.setOggEvento(oggettoEvento);
          logEvento.setCodEvento("GA_ACCESSO_PROCEDURA");
          logEvento.setDescr("Accesso al dettaglio della gara");
          logEvento.setErrmsg("");
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          String messageKey = "errors.logEventi.inaspettataException";
        }
      }

    } catch (SQLException e) {
      throw new JspException(
          "Errore in fase di esecuzione delle select di inizializzazione", e);
    } catch (GestoreException e) {
    	throw new JspException(
    	  "Errore in fase di esecuzione delle select per determinare il numero di decimali per il calcolo della cauzione", e);
	}
  }

  /**
   * Estrazione dei dati per inizializzare la scheda dei dati generali di una
   * gara lotto unico o di un lotto di gara, che è stato associato ad un appalto
   *
   * @param page
   * @param sqlManager
   * @param chiaveAppalto
   * @param tipoAppalto
   * @param tipoLotto
   * @throws SQLException
   * @throws GestoreException
   * @throws JspException
   */
  private void setDatiInitDaAppa(PageContext page, SqlManager sqlManager,
      String chiaveAppalto, String tipoAppalto, String tipoLotto, String integrazioneWSERP) throws SQLException, GestoreException, JspException {

    String codiceLavoro = chiaveAppalto.split(";")[0];
    int numeroAppalto = UtilityNumeri.convertiIntero(
            chiaveAppalto.split(";")[1]).intValue();

    // Inizializzazinone dei campi GARE.CLAVOR e GARE.NUMERA
    page.setAttribute("initCLAVOR", codiceLavoro, PageContext.REQUEST_SCOPE);
    page.setAttribute("initNUMERA", new Long(numeroAppalto), PageContext.REQUEST_SCOPE);

    // Inizializzazione del campo TORN.TIPGEN uguale a tipoAppalto
    page.setAttribute("initTIPGEN", tipoAppalto, PageContext.REQUEST_SCOPE);

    // Estrazione dei dati da APPA
    // C.F. Maggio 2010 Personalizzo il set di campi per codCliente=1153-ASPI
    String setDatiAppa="";
    String codiceCliente = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_CLIENTE);

    if (codiceCliente == "1153") {
      setDatiAppa="select NOTAPP, IMPLAV, IMPLAS, IMPLAT1, IMPLASE, IMPLASC, IMPLAC, " +
      " IMPLAM, IMPLAE, IMPNRL, IMPNRM,  IMPNRE,  IMPNRC,  ONPRGE, " +
      " IVALAV, TUTULT, NURICH, ONSOGRIB, TUTULTUM, CODCIG, DACQCIG, FLAG_ACQ, IDGARA, ALTCAT, SOMMEURG " +
 " from APPA where CODLAV = ?  and NAPPAL = ?";
    } else {
      setDatiAppa="select NOTAPP, IMPLAV, IMPLAS, IMPLAT1, IMPLASE, IMPLASC, IMPLAC, " +
      " IMPLAM, IMPLAE, IMPNRL, IMPNRM,  IMPNRE,  IMPNRC,  ONPRGE, " +
      " IVALAV, TUTULT, CODCUA, ONSOGRIB, TUTULTUM, CODCIG, DACQCIG, FLAG_ACQ, IDGARA, ALTCAT, SOMMEURG " +
 " from APPA where CODLAV = ?  and NAPPAL = ?";
    }

    Vector datiAppa = sqlManager.getVector(setDatiAppa,
        new Object[]{codiceLavoro, new Long(numeroAppalto)});

    if(datiAppa != null && datiAppa.size() > 0){
      Object obj1, obj2 = null;
      Double doubleTmp = null;

      // Inizializzazione del campo NOT_GAR uguale a APPA.NOTAPP
      obj1 = ((JdbcParametro) datiAppa.get(0)).getValue();
      if(obj1 != null)
        page.setAttribute("initNOT_GAR", obj1, PageContext.REQUEST_SCOPE);

      // Inizializzazione del campo IMPAPP uguale a APPA.IMPLAV
      obj1 = ((JdbcParametro) datiAppa.get(1)).getValue();
      if(obj1 != null)
        page.setAttribute("initIMPAPP", obj1, PageContext.REQUEST_SCOPE);

      // Inizializzazione del campo IMPSIC uguale a APPA.IMPLAS
      obj1 = ((JdbcParametro) datiAppa.get(2)).getValue();
      if(obj1 != null)
        page.setAttribute("initIMPSIC", obj1, PageContext.REQUEST_SCOPE);

      // Inizializzazione del campo IMPSMI uguale a APPA.IMPLAT1 + APPA.IMPLASE
      obj1 = ((JdbcParametro) datiAppa.get(3)).getValue();
      if(obj1 == null)
        obj1 = new Double(0);
      obj2 = ((JdbcParametro) datiAppa.get(4)).getValue();
      if(obj2 == null)
        obj2 = new Double(0);
      doubleTmp = new Double(((Double) obj1).doubleValue() + ((Double) obj2).doubleValue());
      if(doubleTmp.doubleValue() != 0)
        page.setAttribute("initIMPSMI", doubleTmp, PageContext.REQUEST_SCOPE);

      // Inizializzazione del campo IMPSCO uguale a APPA.IMPLASC
      obj1 = ((JdbcParametro) datiAppa.get(5)).getValue();
      if(obj1 != null)
        page.setAttribute("initIMPSCO", obj1, PageContext.REQUEST_SCOPE);

      // Inizializzazione del campo IMPCOR uguale a APPA.IMPLAC
      obj1 = ((JdbcParametro) datiAppa.get(6)).getValue();
      if(obj1 != null)
        page.setAttribute("initIMPCOR", obj1, PageContext.REQUEST_SCOPE);

      // Inizializzazione del campo IMPMIS uguale a APPA.IMPLAM + APPA.IMPLAE
      obj1 = ((JdbcParametro) datiAppa.get(7)).getValue();
      if(obj1 == null)
        obj1 = new Double(0);
      obj2 = ((JdbcParametro) datiAppa.get(8)).getValue();
      if(obj2 == null)
        obj2 = new Double(0);
      doubleTmp = new Double(((Double) obj1).doubleValue() + ((Double) obj2).doubleValue());
      if(doubleTmp.doubleValue() != 0)
        page.setAttribute("initIMPMIS", doubleTmp, PageContext.REQUEST_SCOPE);

      // Inizializzazione del campo IMPNRL uguale a APPA.IMPNRL
      obj1 = ((JdbcParametro) datiAppa.get(9)).getValue();
      if(obj1 != null)
        page.setAttribute("initIMPNRL", obj1, PageContext.REQUEST_SCOPE);

      // Inizializzazione del campo IMPNRM uguale a APPA.IMPNRM + APPA.IMPNRE
      obj1 = ((JdbcParametro) datiAppa.get(10)).getValue();
      if(obj1 == null)
        obj1 = new Double(0);
      obj2 = ((JdbcParametro) datiAppa.get(11)).getValue();
      if(obj2 == null)
        obj2 = new Double(0);
      doubleTmp = new Double(((Double) obj1).doubleValue() + ((Double) obj2).doubleValue());
      if(doubleTmp.doubleValue() != 0)
        page.setAttribute("initIMPNRM", doubleTmp, PageContext.REQUEST_SCOPE);

      // Inizializzazione del campo IMPNRC uguale a APPA.IMPNRC
      obj1 = ((JdbcParametro) datiAppa.get(12)).getValue();
      if(obj1 != null)
        page.setAttribute("initIMPNRC", obj1, PageContext.REQUEST_SCOPE);

      // Inizializzazione del campo ONPRGE uguale a APPA.ONPRGE
      obj1 = ((JdbcParametro) datiAppa.get(13)).getValue();
      if(obj1 != null)
        page.setAttribute("initONPRGE", obj1, PageContext.REQUEST_SCOPE);

//      (S.Santi 27.02.12) Tolto inizializzazione campo IVALAV da APPA
//      // Inizializzazione del campo IVALAV uguale a APPA.IVALAV
//      obj1 = ((JdbcParametro) datiAppa.get(14)).getValue();
//      if(obj1 != null)
//        page.setAttribute("initIVALAV", obj1, PageContext.REQUEST_SCOPE);

      if(!"lottoOffertaUnica".equals(tipoLotto)){
        // Inizializzazione del campo TEUTIL uguale a APPA.TUTULT
        obj1 = ((JdbcParametro) datiAppa.get(15)).getValue();
        if(obj1 != null)
          page.setAttribute("initTEUTIL", obj1, PageContext.REQUEST_SCOPE);

        // Inizializzazione unità di misura per il tempo utile
        obj1 = ((JdbcParametro) datiAppa.get(18)).getValue();
        if(obj1 != null)
          page.setAttribute("initTEMESI", obj1, PageContext.REQUEST_SCOPE);
      }

      // Inizializzazione del campo CODCUA uguale a APPA.CODCUA (campo non gestito in gare)
      obj1 = ((JdbcParametro) datiAppa.get(16)).getValue();
      if(obj1 != null)
        page.setAttribute("initCODCUA", obj1, PageContext.REQUEST_SCOPE);

      // Inizializzazione del campo ONSOGRIB uguale a ONSOGRIB
      obj1 = ((JdbcParametro) datiAppa.get(17)).getValue();
      page.setAttribute("initONSOGRIB", obj1, PageContext.REQUEST_SCOPE);
      page.setAttribute("initONSOGRIBDaAppa", "true", PageContext.REQUEST_SCOPE);

      // Inizializzazione del campo CODCIG uguale a CODCIG
      obj1 = ((JdbcParametro) datiAppa.get(19)).getValue();
      page.setAttribute("initCODCIG", obj1, PageContext.REQUEST_SCOPE);

      // Inizializzazione del campo DACQCIG uguale a DACQCIG
      String data = null;
      obj1 = ((JdbcParametro) datiAppa.get(20)).getValue();
      if(obj1!=null)
        data = UtilityDate.convertiData((Date)obj1, UtilityDate.FORMATO_GG_MM_AAAA);
      page.setAttribute("initDACQCIG", data, PageContext.REQUEST_SCOPE);

      // Inizializzazione del campo ACCQUA con FLAG_ACQ
      obj1 = ((JdbcParametro) datiAppa.get(21)).getValue();
      page.setAttribute("initACCQUA", obj1, PageContext.REQUEST_SCOPE);

      // Inizializzazione del campo NUMAVCP con IDGARA
      obj1 = ((JdbcParametro) datiAppa.get(22)).getValue();
      page.setAttribute("initNUMAVCP", obj1, PageContext.REQUEST_SCOPE);

      // Inizializzazione del campo CODCPV di GARCPV  con ALTCAT di APPA
      obj1 = ((JdbcParametro) datiAppa.get(23)).getValue();
      if(obj1!=null && "lottoOffertaUnica".equals(tipoLotto)){
        page.setAttribute("initCODCPV", obj1, PageContext.REQUEST_SCOPE);
      }

      // Inizializzazione del campo SOMMAUR di TORN  con SOMMEURG di APPA
      obj1 = ((JdbcParametro) datiAppa.get(24)).getValue();
      page.setAttribute("initSOMMAUR", obj1, PageContext.REQUEST_SCOPE);

    }

    String codiceTecnico = null;
    String nomeTecnico =  null;
    // Estrazione dati da G2TECN
    Vector datiG2TECN = sqlManager.getVector(
        "select CODTEC, NOMTEC from G2TECN " +
         "where G2TECN.CODLAV = ? " +
           "and DTETEC is null and INCTEC=1 order by NUMTEC desc",
        new Object[]{codiceLavoro});
    if(datiG2TECN != null && datiG2TECN.size() > 0){
      codiceTecnico = ((JdbcParametro) datiG2TECN.get(0)).getStringValue();
      nomeTecnico =  ((JdbcParametro) datiG2TECN.get(1)).getStringValue();
    }




    //Estrazione dei dati da PERI
    Long altrisog=null;
    HttpSession session = page.getSession();
    String uffint = (String) session.getAttribute("uffint");
    if ("true".equals(page.getAttribute("garaLottoUnico", PageContext.REQUEST_SCOPE)) ){
      String cenint = null;
      String nomein = null;
      String iscuc = null;
      if(uffint==null || "".equals(uffint)){
        Vector datiPeri = sqlManager.getVector(
            "select CENINT from PERI where CODLAV = ? ",
            new Object[]{codiceLavoro});
        if(datiPeri != null && datiPeri.size() > 0){
          if(datiPeri.get(0) != null){
            cenint = ((JdbcParametro) datiPeri.get(0)).stringValue();
            if(cenint != null){
              /*
              String nomein = (String) sqlManager.getObject(
                  "select NOMEIN from UFFINT, PERI where UFFINT.CODEIN = PERI.CENINT and PERI.CODLAV = ? ",
                  new Object[]{codiceLavoro});
              */
              Vector datiUffint = sqlManager.getVector("select NOMEIN,ISCUC from UFFINT, PERI where "
                  + "UFFINT.CODEIN = PERI.CENINT and PERI.CODLAV = ? ", new Object[]{codiceLavoro});
              if(datiUffint!=null && datiUffint.size()>0){
                page.setAttribute("initCENINT", cenint, PageContext.REQUEST_SCOPE);
                nomein = SqlManager.getValueFromVectorParam(datiUffint, 0).getStringValue();
                page.setAttribute("initNOMEIN", nomein, PageContext.REQUEST_SCOPE);
                iscuc = SqlManager.getValueFromVectorParam(datiUffint, 1).getStringValue();
                page.setAttribute("initISCUC", iscuc, PageContext.REQUEST_SCOPE);
              }
            }
          }
        }

      }else{
        page.setAttribute("initCENINT", uffint, PageContext.REQUEST_SCOPE);
        /*
        String nomein = (String) sqlManager.getObject(
            "select NOMEIN from UFFINT where UFFINT.CODEIN =  ? ",
            new Object[]{uffint});
       */
        Vector datiUffint = sqlManager.getVector("select NOMEIN,ISCUC from UFFINT where UFFINT.CODEIN=?",
            new Object[]{uffint});
        if(datiUffint!=null && datiUffint.size()>0){
          nomein = SqlManager.getValueFromVectorParam(datiUffint, 0).getStringValue();
          page.setAttribute("initNOMEIN", nomein, PageContext.REQUEST_SCOPE);
          iscuc = SqlManager.getValueFromVectorParam(datiUffint, 1).getStringValue();
          page.setAttribute("initISCUC", iscuc, PageContext.REQUEST_SCOPE);
        }

        //Inizializzazione ALTRISOG
        Vector datiPeriUffint = sqlManager.getVector("select NOMEIN,CENINT from UFFINT, PERI where "
            + "UFFINT.CODEIN = PERI.CENINT and PERI.CODLAV = ? ", new Object[]{codiceLavoro});
        if(datiPeriUffint!=null && datiPeriUffint.size()>0){
          nomein = SqlManager.getValueFromVectorParam(datiPeriUffint, 0).getStringValue();
          cenint = SqlManager.getValueFromVectorParam(datiPeriUffint, 1).getStringValue();
          if(cenint==null)
            cenint="";
          if(!cenint.equals(uffint) && "1".equals(iscuc)){
            altrisog = new Long(2);
            page.setAttribute("initALTRISOG", "2", PageContext.REQUEST_SCOPE);
            page.setAttribute("initCenintGaraltsog", cenint, PageContext.REQUEST_SCOPE);
            page.setAttribute("initNomeinGaraltsog",nomein,PageContext.REQUEST_SCOPE);
            page.setAttribute("initCodrupGaraltsog", codiceTecnico,PageContext.REQUEST_SCOPE);
            page.setAttribute("initNomtecGaraltsog", nomeTecnico,PageContext.REQUEST_SCOPE);
          }else if(cenint.equals(uffint) && "1".equals(iscuc)){
            page.setAttribute("initALTRISOG", "1", PageContext.REQUEST_SCOPE);
            altrisog = new Long(1);
          }
        }


      }

      if("1".equals(integrazioneWSERP)){
        String tipoWSERP = "";
        try {
          WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
          if (configurazione.isEsito()) {
            tipoWSERP = configurazione.getRemotewserp();
          }
        } catch (GestoreException e) {
          UtilityStruts.addMessage(page.getRequest(), "error",
              "wserpconfigurazione.erp.configurazioneleggi.remote.error",new Object[]{":\r\n Configurazione integrazione con sistema ERP non corretta"});
        }

        if("CAV".equals(tipoWSERP)){
          //se c'e' integrazione con LFS importo le rda (CAV)
          this.setDatiInitDaAppaRda(page, sqlManager, codiceLavoro, Long.valueOf(numeroAppalto));
        }
      }

    }else{
      HashMap keyParent = UtilityTags.stringParamsToHashMap(
          (String) page.getAttribute(
              UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT,
              PageContext.REQUEST_SCOPE), null);
      String codgar = ((JdbcParametro) keyParent.get("TORN.CODGAR")).getStringValue();
     if(codgar!=null){
        Long altrisogTorn = (Long)sqlManager.getObject("select altrisog from torn where codgar=?", new Object[]{codgar});
        if(altrisogTorn!=null && altrisogTorn.longValue()==2 && uffint!=null && !"".equals(uffint)){
          //Inizializzazione ALTRISOG
          Vector datiPeriUffint = sqlManager.getVector("select NOMEIN,CENINT from UFFINT, PERI where "
              + "UFFINT.CODEIN = PERI.CENINT and PERI.CODLAV = ? ", new Object[]{codiceLavoro});
          if(datiPeriUffint!=null && datiPeriUffint.size()>0){
            String nomein = SqlManager.getValueFromVectorParam(datiPeriUffint, 0).getStringValue();
            String cenint = SqlManager.getValueFromVectorParam(datiPeriUffint, 1).getStringValue();
            if(cenint==null)
              cenint="";
            if(!cenint.equals(uffint)){
              page.setAttribute("initCenintGaraltsog", cenint, PageContext.REQUEST_SCOPE);
              page.setAttribute("initNomeinGaraltsog",nomein,PageContext.REQUEST_SCOPE);
              page.setAttribute("initCodrupGaraltsog", codiceTecnico,PageContext.REQUEST_SCOPE);
              page.setAttribute("initNomtecGaraltsog", nomeTecnico,PageContext.REQUEST_SCOPE);
            }
          }

        }

      }

    }

    // Estrazione dati da G2TECN
    if(altrisog == null || altrisog.longValue()!=2){
      if(codiceTecnico != null && nomeTecnico != null){
        page.setAttribute("initCODRUP", codiceTecnico, PageContext.REQUEST_SCOPE);
        page.setAttribute("initNOMTEC1", nomeTecnico, PageContext.REQUEST_SCOPE);
      }
    }

    if(!"lottoOffertaUnica".equals(tipoLotto)){
      //Estrazione dei dati da CATAPP per inizializzare i campi di CATG
      Vector datiCatapp = sqlManager.getVector(
          "select CATAPP.CATIGA, CAIS.DESCAT, CAIS.ACONTEC, CAIS.QUAOBB," +
                " CAIS.TIPLAVG, CATAPP.NCATG, CATAPP.IMPBASG, CATAPP.NUMCLA, " +
                " CATAPP.IMPIGA, V_CAIS_TIT.ISFOGLIA " +
            "from CATAPP, CAIS, V_CAIS_TIT " +
           "where CATAPP.CODLAV = ? " +
            " and catapp.nappal = ? " +
            " and CAIS.CAISIM = CATAPP.CATIGA "+
            " and CAIS.CAISIM = V_CAIS_TIT.CAISIM",
            new Object[]{codiceLavoro, new Long(numeroAppalto)});
      if(datiCatapp != null && datiCatapp.size() > 0)
        page.setAttribute("initCATG", datiCatapp, PageContext.REQUEST_SCOPE);

      //Estrazione dei dati da ULTAPP per inizializzare i campi di OPES
      String tipoNullPos = "";
      if ("POS".equals(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE))){
         tipoNullPos = "::text";
      }
      List datiUltapp = sqlManager.getListVector(
          "select ULTAPP.CATOFF, CAIS.DESCAT, CAIS.ACONTEC, CAIS.QUAOBB," +
                " CAIS.TIPLAVG, null" + tipoNullPos + ", ULTAPP.NOPEGA, ULTAPP.IMPAPO, ULTAPP.NUMCLU, " +
                " ULTAPP.ISCOFF, ULTAPP.ACONTEC, ULTAPP.QUAOBB, ULTAPP.DESCOP, V_CAIS_TIT.ISFOGLIA " +
            "from ULTAPP, CAIS, V_CAIS_TIT " +
           "where ULTAPP.CODLAV = ? " +
            " and ULTAPP.NAPPAL = ? " +
            " and CAIS.CAISIM = ULTAPP.CATOFF " +
            " and V_CAIS_TIT.CAISIM = CAIS.CAISIM " +
        "order by ULTAPP.NOPEGA asc",
            new Object[]{codiceLavoro, new Long(numeroAppalto)});
      if(datiUltapp != null && datiUltapp.size() > 0)
        page.setAttribute("initOPES", datiUltapp, PageContext.REQUEST_SCOPE);
    }else{
      Vector<?> datiPeri = sqlManager.getVector("select CUPPRG, CUIINT from PERI where CODLAV = ? ",
          new Object[]{codiceLavoro});
      String cupprg = null;
      String codcui = null;
      if (((JdbcParametro)datiPeri.get(0)).getValue() != null)
        cupprg = (String) sqlManager.getValueFromVectorParam(datiPeri, 0).getValue();
      page.setAttribute("initCUPPRG", cupprg, PageContext.REQUEST_SCOPE);
      if (((JdbcParametro)datiPeri.get(1)).getValue() != null)
        codcui = (String) sqlManager.getValueFromVectorParam(datiPeri, 1).getValue();
      if(codcui!=null && codcui.length()>22){codcui = codcui.trim().substring(0, 22);}
      page.setAttribute("initCODCUI", codcui, PageContext.REQUEST_SCOPE);

      if("1".equals(integrazioneWSERP)){
        String tipoWSERP = "";
        try {
          WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
          if (configurazione.isEsito()) {
            tipoWSERP = configurazione.getRemotewserp();
          }
        } catch (GestoreException e) {
          UtilityStruts.addMessage(page.getRequest(), "error",
              "wserpconfigurazione.erp.configurazioneleggi.remote.error",new Object[]{":\r\n Configurazione integrazione con sistema ERP non corretta"});
        }

        if("CAV".equals(tipoWSERP)){
          //se c'e' integrazione con LFS importo le rda (CAV)
          this.setDatiInitDaAppaRda(page, sqlManager, codiceLavoro, Long.valueOf(numeroAppalto));
        }
      }

    }
  }


  /**
   * Legge dalla tabella TORN i dati per memorizzarli nel request in modo da
   * essere utilizzati poi nella pagina
   *
   * @param page
   * @param sqlManager
   * @throws SQLException
   */
  private void setDatiInitDaTorn(PageContext page, SqlManager sqlManager)
      throws SQLException {
    // definisco tutte le impostazioni di default da proporre per i campi
    // della GARA a partire dai dati di TORN nel caso di lotto di gara
    String sqlTorn = "SELECT CODGAR, TATTOT, DATTOT, NATTOT, NPROAT, "
        + "DTEPAR, OTEPAR, TIPGAR, TIPNEG, MODLIC, MODGAR, NAVVIG, "
        + "DAVVIG, DPUBAV, DFPUBA, DIBAND, CORGAR, DTEOFF, OTEOFF, "
        + "DINDOC, DESDOC, DESOFF, OESOFF, OGGCONT, CRITLIC, DETLIC, CALCSOAN, "
        + "DTERMRICHCDP, DTERMRISPCDP,DTERMRICHCPO,DTERMRISPCPO,APPLEGREG, ITERGA, "
        + "CONTOECO, VALTEC, ULTDETLIC, ALTRISOG, AQOPER, AQNUMOPE, ACCQUA, TIPLAV, MODMANO FROM TORN WHERE CODGAR = ?";
    HashMap keyParent = UtilityTags.stringParamsToHashMap(
        (String) page.getAttribute(
            UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT,
            PageContext.REQUEST_SCOPE), null);
    Vector datiTorn = sqlManager.getVector(sqlTorn,
        new Object[] { ((JdbcParametro) keyParent.get("TORN.CODGAR")).getStringValue() });

    Date dataAppoggioPerConversioni = null;
    page.setAttribute("parentCODGAR",
        ((JdbcParametro) datiTorn.get(0)).getValue(), PageContext.REQUEST_SCOPE);
    page.setAttribute("initTATTOT",
        ((JdbcParametro) datiTorn.get(1)).getValue(), PageContext.REQUEST_SCOPE);
    dataAppoggioPerConversioni = null;
    if (datiTorn.get(2) != null)
      dataAppoggioPerConversioni = (Date) ((JdbcParametro) datiTorn.get(2)).getValue();
    page.setAttribute("initDATTOT", UtilityDate.convertiData(
        dataAppoggioPerConversioni, UtilityDate.FORMATO_GG_MM_AAAA),
        PageContext.REQUEST_SCOPE);
    page.setAttribute("initNATTOT",
        ((JdbcParametro) datiTorn.get(3)).getValue(), PageContext.REQUEST_SCOPE);
    page.setAttribute("initNPROAT",
        ((JdbcParametro) datiTorn.get(4)).getValue(), PageContext.REQUEST_SCOPE);
    dataAppoggioPerConversioni = null;


    page.setAttribute("initTIPGAR",
        ((JdbcParametro) datiTorn.get(7)).getValue(), PageContext.REQUEST_SCOPE);
    page.setAttribute("initTIPNEG",
        ((JdbcParametro) datiTorn.get(8)).getValue(), PageContext.REQUEST_SCOPE);
    page.setAttribute("initMODLIC",
        ((JdbcParametro) datiTorn.get(9)).getValue(), PageContext.REQUEST_SCOPE);
    page.setAttribute("initMODGAR",
        ((JdbcParametro) datiTorn.get(10)).getValue(),
        PageContext.REQUEST_SCOPE);
    page.setAttribute("initNAVVIG",
        ((JdbcParametro) datiTorn.get(11)).getValue(),
        PageContext.REQUEST_SCOPE);
    dataAppoggioPerConversioni = null;
    if (datiTorn.get(12) != null)
      dataAppoggioPerConversioni = (Date) ((JdbcParametro) datiTorn.get(12)).getValue();
    page.setAttribute("initDAVVIG", UtilityDate.convertiData(
        dataAppoggioPerConversioni, UtilityDate.FORMATO_GG_MM_AAAA),
        PageContext.REQUEST_SCOPE);
    dataAppoggioPerConversioni = null;
    if (datiTorn.get(13) != null)
      dataAppoggioPerConversioni = (Date) ((JdbcParametro) datiTorn.get(13)).getValue();
    page.setAttribute("initDPUBAV", UtilityDate.convertiData(
        dataAppoggioPerConversioni, UtilityDate.FORMATO_GG_MM_AAAA),
        PageContext.REQUEST_SCOPE);
    dataAppoggioPerConversioni = null;
    if (datiTorn.get(14) != null)
      dataAppoggioPerConversioni = (Date) ((JdbcParametro) datiTorn.get(14)).getValue();
    page.setAttribute("initDFPUBA", UtilityDate.convertiData(
        dataAppoggioPerConversioni, UtilityDate.FORMATO_GG_MM_AAAA),
        PageContext.REQUEST_SCOPE);
    dataAppoggioPerConversioni = null;
    if (datiTorn.get(15) != null)
      dataAppoggioPerConversioni = (Date) ((JdbcParametro) datiTorn.get(15)).getValue();
    page.setAttribute("initDIBAND", UtilityDate.convertiData(
        dataAppoggioPerConversioni, UtilityDate.FORMATO_GG_MM_AAAA),
        PageContext.REQUEST_SCOPE);
    page.setAttribute("initCORGAR",
        ((JdbcParametro) datiTorn.get(16)).getValue(),
        PageContext.REQUEST_SCOPE);
    dataAppoggioPerConversioni = null;

    page.setAttribute("initOGGCONT",
        ((JdbcParametro) datiTorn.get(23)).getValue(),
        PageContext.REQUEST_SCOPE);
    page.setAttribute("initCRITLIC",
        ((JdbcParametro) datiTorn.get(24)).getValue(),
        PageContext.REQUEST_SCOPE);
    page.setAttribute("initDETLIC",
        ((JdbcParametro) datiTorn.get(25)).getValue(),
        PageContext.REQUEST_SCOPE);
    page.setAttribute("initCALCSOAN",
        ((JdbcParametro) datiTorn.get(26)).getValue(),
        PageContext.REQUEST_SCOPE);


    page.setAttribute("initAPPLEGREG",
        ((JdbcParametro) datiTorn.get(31)).getValue(),
        PageContext.REQUEST_SCOPE);
    page.setAttribute("initITERGA",
        ((JdbcParametro) datiTorn.get(32)).getValue(), PageContext.REQUEST_SCOPE);
    page.setAttribute("initCONTECO",
        ((JdbcParametro) datiTorn.get(33)).getValue(), PageContext.REQUEST_SCOPE);
    page.setAttribute("initVALTEC",
        ((JdbcParametro) datiTorn.get(34)).getValue(), PageContext.REQUEST_SCOPE);
    page.setAttribute("initULTDETLIC",
        ((JdbcParametro) datiTorn.get(35)).getValue(), PageContext.REQUEST_SCOPE);
    page.setAttribute("initALTRISOG",
        ((JdbcParametro) datiTorn.get(36)).getValue(), PageContext.REQUEST_SCOPE);
    page.setAttribute("initAQOPER",
        ((JdbcParametro) datiTorn.get(37)).getValue(), PageContext.REQUEST_SCOPE);
    page.setAttribute("initAQNUMOPE",
        ((JdbcParametro) datiTorn.get(38)).getValue(), PageContext.REQUEST_SCOPE);
    page.setAttribute("initACCQUATorn",
        ((JdbcParametro) datiTorn.get(39)).getValue(), PageContext.REQUEST_SCOPE);
    page.setAttribute("initTIPLAV",
        ((JdbcParametro) datiTorn.get(40)).getValue(), PageContext.REQUEST_SCOPE);
    page.setAttribute("initMODMANO",
        ((JdbcParametro) datiTorn.get(41)).getValue(), PageContext.REQUEST_SCOPE);
  }

  /**
   * Legge dalla tabella CATG i dati per memorizzarli nel request in modo da
   * essere utilizzati poi nella pagina
   *
   * @param page
   * @param sqlManager
   * @throws SQLException
   */
  private void setDatiInitDaCatg(PageContext page, SqlManager sqlManager)
      throws SQLException {
    // definisco tutte le impostazioni di default da proporre per i campi
    // della CATG a partire dall'ultimo lotto di gara esistente
    String sqlUltimoLotto = "select NGARA "
        + "from GARE "
        + "where CODGAR1 = ? "
        + "order by NGARA desc";
    HashMap keyParent = UtilityTags.stringParamsToHashMap(
        (String) page.getAttribute(
            UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT,
            PageContext.REQUEST_SCOPE), null);
    // mi interessa estrarre solo la prima riga, ovvero l'ultimo lotto definito
    String ultimoLotto = (String) sqlManager.getObject(
        sqlUltimoLotto,
        new Object[] { ((JdbcParametro) keyParent.get("TORN.CODGAR")).getStringValue() });

    if (ultimoLotto != null) {
      String sqlCatg = "select CATG.CATIGA , CAIS.DESCAT "
          + "from CATG, CAIS "
          + "where CATG.NGARA = ? and NCATG = 1 "
          + "and CATG.CATIGA = CAIS.CAISIM";
      // mi interessa estrarre solo la riga relativa all'ultimo lotto
      Vector datiCatg = sqlManager.getVector(sqlCatg,
          new Object[] { ultimoLotto });
      if (datiCatg != null) {
        page.setAttribute("initCATIGA",
            ((JdbcParametro) datiCatg.get(0)).getValue(),
            PageContext.REQUEST_SCOPE);
        page.setAttribute("initDESCAT",
            ((JdbcParametro) datiCatg.get(1)).getValue(),
            PageContext.REQUEST_SCOPE);
      }
    }
  }

  /**
   * Determina la percentuale della cauzione provvisoria e memorizza
   * il valore nel request in modo da essere utilizzati poi nella pagina
   *
   * @param page
   * @param sqlManager
   * @param tipoAppalto
   * @throws SQLException
   */
  private void initPGAROF(PageContext page, SqlManager sqlManager,String tipoAppalto) throws SQLException {
    /*
    int tipgen = Integer.parseInt(tipoAppalto);
    String tabellato = PgManager.getTabellatoPercCauzioneProvvisoria(tipgen);

    TabellatiManager tabellatiManager = (TabellatiManager)
    	UtilitySpring.getBean("tabellatiManager", page, TabellatiManager.class);

    String descrPercentuale = tabellatiManager.getDescrTabellato(tabellato, "1");
    Double percentuale = UtilityNumeri.convertiDouble(descrPercentuale,
        UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE);
    */
    Long tipgen = new Long(tipoAppalto);
    Double percentuale = pgManager.initPGAROF(tipgen);
    page.setAttribute("initPGAROF", percentuale, PageContext.REQUEST_SCOPE);
  }

  /**
   * Determina MODASTG della gara complentare e memorizza il valore
   * nel request in modo da essere utilizzati poi nella pagina
   *
   * @param page
   * @param sqlManager
   * @throws SQLException
   */
  private void initMODASTG(PageContext page, SqlManager sqlManager)
  throws SQLException {
	  String sql = "SELECT MODASTG FROM GARE WHERE NGARA = ?";
	    HashMap keyParent = UtilityTags.stringParamsToHashMap(
	        (String) page.getAttribute(
	            UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT,
	            PageContext.REQUEST_SCOPE), null);

	    Long modastg = (Long) sqlManager.getObject(sql,
	            new Object[] { ((JdbcParametro) keyParent.get("TORN.CODGAR")).getStringValue() });

	    page.setAttribute("initMODASTG",
	    		modastg,
	              PageContext.REQUEST_SCOPE);
  }

  /**
   * Determina RIBCAL della gara complentare e memorizza il valore
   * nel request in modo da essere utilizzati poi nella pagina
   * Recupera il valore con il seguente criterio:
   * considera il valore dei lotti, se ne esiste almeno uno con Offerta prezzi,
   * altrimenti considera quello della gara complementare se è con Offerta prezzi,
   * altrimenti lascia il valore nullo.
   *
   * @param page
   * @param sqlManager
   * @throws SQLException
   */
  private void initRIBCAL(PageContext page, SqlManager sqlManager, String codgar)
  throws SQLException {

        String sql = "SELECT RIBCAL FROM GARE WHERE CODGAR1 = ? AND GENERE is NULL AND MODLICG in (5,14,16)";
	    Long ribcal = (Long) sqlManager.getObject(sql,
	            new Object[] { codgar });

	    if (ribcal == null){
	      sql = "SELECT RIBCAL FROM GARE,TORN WHERE NGARA = ? AND CODGAR1=CODGAR AND MODLIC in (5,14,16)";
	      ribcal = (Long) sqlManager.getObject(sql,
               new Object[] { codgar });
	    }

	      page.setAttribute("initRIBCAL",
	    		ribcal,
	              PageContext.REQUEST_SCOPE);
  }

  /**
   * Determina gli importi degli scaglioni della Stazione appaltante
   * e li carica nel request in modo da essere utilizzati poi nella pagina
   *
   * @param page
   * @param sqlManager
   * @param tabellato
   * @throws SQLException
   */
  private void getScaglioni(PageContext page, SqlManager sqlManager, String tabellato)
      throws SQLException {

  	TabellatiManager tabellatiManager = (TabellatiManager)
  		UtilitySpring.getBean("tabellatiManager", page, TabellatiManager.class);

  	List lista = tabellatiManager.getTabellato(tabellato);
  	if (lista != null && lista.size() > 0) {
  		String importoContributo = null;
  		String descImportoMassimoGara = null;
  		int posizioneSpazio = -1;
  		Double importoEstratto = null;
  		double importoMassimoGara ;
  		List listaScaglioni = new ArrayList(lista.size());
  		for (int i = 0; i < lista.size(); i++) {
  			importoContributo = ((Tabellato) lista.get(i)).getDatoSupplementare();
  			descImportoMassimoGara = ((Tabellato) lista.get(i)).getDescTabellato();
  			posizioneSpazio = descImportoMassimoGara.indexOf(' ');
  			importoEstratto = null;
  			if (posizioneSpazio > 0)
  				importoEstratto = UtilityNumeri.convertiDouble(
  						UtilityStringhe.replace(descImportoMassimoGara.substring(0,
  								posizioneSpazio), ",", "."),
  								UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE);

  			// l'ultima riga non indica il limite massimo, perchè non esiste,
  			// per cui si fissa un limite massimo più alto possibile in modo
  			// da essere sempre sotto
	      if (importoEstratto != null)
	        importoMassimoGara = importoEstratto.doubleValue();
	      else
	        importoMassimoGara = Double.MAX_VALUE;

	      if (importoContributo == null) importoContributo = "";

	      Tabellato singoloScaglione = new Tabellato();
	      singoloScaglione.setTipoTabellato(importoContributo);
	      singoloScaglione.setDescTabellato(Double.toString(importoMassimoGara));
	      listaScaglioni.add(singoloScaglione);
  		}
      page.setAttribute("listaScaglioni", listaScaglioni, PageContext.REQUEST_SCOPE);
    }
  }

  /**
   * Estrazione dei dati di APPR per inizializzare la scheda dei dati generali di una
   * gara lotto unico o di un lotto di gara, che è stato associato ad un appalto
   *
   * @param page
   * @param sqlManager
   * @param chiaveAppalto
   * @param datiAttoInseritiDaAppa
   * @throws SQLException
   * @throws GestoreException
   */
  private void setDatiInitDaAppr(PageContext page, SqlManager sqlManager,
      String chiaveAppalto, boolean datiAttoInseritiDaAppa) throws SQLException, GestoreException {
    String codiceLavoro = chiaveAppalto.split(";")[0];

    String select="select max(naprpr) from appr where codlav=? and ddappr is not null";
    Long naprpr = (Long) sqlManager.getObject(select,
        new Object[] { codiceLavoro });
    if(naprpr!= null && naprpr.longValue()>0){
      select="select TDAPPR1,NDAPPR,DDAPPR from appr where codlav=? and naprpr=?";
      Vector datiAppr = sqlManager.getVector(select,
          new Object[] { codiceLavoro,naprpr });
      if (datiAppr!= null && datiAppr.size()>0){
        Date dataAppoggioPerConversioni = null;
        Long initTDAPPR =  this.getTATTOG((JdbcParametro) datiAppr.get(0));
        String numero = ((JdbcParametro) datiAppr.get(1)).getStringValue();
        dataAppoggioPerConversioni = null;
        if (datiAppr.get(2) != null)
          dataAppoggioPerConversioni = (Date) ((JdbcParametro) datiAppr.get(2)).getValue();
        String dataString =  UtilityDate.convertiData(
            dataAppoggioPerConversioni, UtilityDate.FORMATO_GG_MM_AAAA);

        page.setAttribute("initTDAPPR",initTDAPPR, PageContext.REQUEST_SCOPE);
        page.setAttribute("initNDAPPR",numero, PageContext.REQUEST_SCOPE);
        page.setAttribute("initDDAPPR", dataString,PageContext.REQUEST_SCOPE);
        if(datiAttoInseritiDaAppa){
          //Si devono caricare i dati nella sezione dinamica di GARATT, ma prima controllo che si possa inserire
          //un elemento nella sezione
          boolean inserimentoGaratt= false;
          if(this.geneManager.getProfili().checkProtec(
              (String) page.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS", "INS.GARE.GARE-scheda.DATIGEN.INS-ATAU")){
            inserimentoGaratt= true;

          }
          page.setAttribute("initGaratt", new Boolean(inserimentoGaratt),PageContext.REQUEST_SCOPE);
        }
      }
    }
  }

  /**
   * Estrazione dei dati di APPA relativi all'atto autorizzativo per inizializzare la scheda
   * dei dati generali di una gara lotto unico o di un lotto di gara, che è stato associato
   * ad un appalto. Inoltre il metodo ritorna true se almeno uno dei campi di APPA presi
   * in considerazione è valorizzato.
   *
   * @param page
   * @param sqlManager
   * @param chiaveAppalto
   * @return boolean
   * @throws SQLException
   * @throws GestoreException
   */
  private boolean setDatiInitDaAppaAttoAutorizzativo(PageContext page, SqlManager sqlManager,
      String chiaveAppalto) throws SQLException, GestoreException {
    String codiceLavoro = chiaveAppalto.split(";")[0];
    int numeroAppalto = UtilityNumeri.convertiIntero(
            chiaveAppalto.split(";")[1]).intValue();
    boolean valoriAppaPresenti = false;

    String select="select ATTAUTTIPO,ATTAUTNUMERO,ATTAUTDATA from appa where codlav=? and nappal=?";
    Vector datiAppa = sqlManager.getVector(select,
        new Object[] { codiceLavoro,numeroAppalto });
    if (datiAppa!= null && datiAppa.size()>0){
      Date dataAppoggioPerConversioni = null;
      Long initTDAPPA =  this.getTATTOG((JdbcParametro) datiAppa.get(0));
      page.setAttribute("initTDAPPA",initTDAPPA, PageContext.REQUEST_SCOPE);
      String numero = ((JdbcParametro) datiAppa.get(1)).getStringValue();
      page.setAttribute("initNDAPPA",numero, PageContext.REQUEST_SCOPE);
      dataAppoggioPerConversioni = null;
      if (datiAppa.get(2) != null)
        dataAppoggioPerConversioni = (Date) ((JdbcParametro) datiAppa.get(2)).getValue();
      page.setAttribute("initDDAPPA", UtilityDate.convertiData(
          dataAppoggioPerConversioni, UtilityDate.FORMATO_GG_MM_AAAA),
          PageContext.REQUEST_SCOPE);
      if(initTDAPPA!=null || dataAppoggioPerConversioni!=null || (numero!=null && !"".equals(numero)))
        valoriAppaPresenti=true;
      page.setAttribute("initAttoDaAppa", new Boolean(valoriAppaPresenti),PageContext.REQUEST_SCOPE);
    }
    return valoriAppaPresenti;
  }

  /**
   * Estrazione dei dati di RDA per inizializzare la scheda dei dati generali di una
   * gara lotto unico o di un lotto di gara, che è stato associato ad una rda
   *
   * @param page
   * @param sqlManager
   * @param numeroRda
   * @throws SQLException
   * @throws GestoreException
   */
  private void setDatiInitDaRda(PageContext page, SqlManager sqlManager, String numeroRda)
    throws SQLException, GestoreException {
      String selectRda="select data_approvazione,descrizione,valore,tus from v_smat_rda where numero_rda = ?";

      Vector datiRda = sqlManager.getVector(selectRda, new Object[] { numeroRda });
      if (datiRda != null && datiRda.size()>0){
        Date dataApprRda = (Date) ((JdbcParametro)datiRda.get(0)).getValue();
        String oggettoRda = (String) ((JdbcParametro) datiRda.get(1)).getValue();
        Object objImpRda = ((JdbcParametro) datiRda.get(2)).getValue();
        Double importoRda = null;
        if (objImpRda instanceof Long){
          importoRda = ((Long) objImpRda).doubleValue();
        }else{
          if(objImpRda instanceof Double){
            importoRda = (Double) objImpRda;
          }
        }
        Long tusRda = (Long) ((JdbcParametro) datiRda.get(3)).getValue();
    //per inizializzare riuso le variabili già utilizzate nella iniz. da Appr, con i case già gestiti
        page.setAttribute("initNDAPPR",numeroRda, PageContext.REQUEST_SCOPE);
        page.setAttribute("initDDAPPR",UtilityDate.convertiData(dataApprRda, UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);
        page.setAttribute("initTDAPPR","4", PageContext.REQUEST_SCOPE);

        page.setAttribute("initNOT_GAR",oggettoRda, PageContext.REQUEST_SCOPE);
        page.setAttribute("initIMPAPP",importoRda, PageContext.REQUEST_SCOPE);
        page.setAttribute("initTUS",tusRda, PageContext.REQUEST_SCOPE);

      }

  }

  /**
   * Estrazione dei dati di RDA per inizializzare la scheda dei dati generali di una
   * gara lotto unico o di un lotto di gara, che è stato associato ad una rda
   * CON I DATI PERSONALIZZATI
   * @param page
   * @param sqlManager
   * @param numeroRda
   * @throws SQLException
   * @throws GestoreException
   * @throws JspException
   */
  private void setDatiInitDaERPvsWSDM(PageContext page, SqlManager sqlManager, String numeroRda, String idconfi)
    throws SQLException, GestoreException, JspException {


    WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = this.gestioneWSDMManager.wsdmDocumentoERPLeggi(numeroRda,idconfi);
    if(!wsdmProtocolloDocumentoRes.isEsito()){
      String wsdmMsg = wsdmProtocolloDocumentoRes.getMessaggio();
      throw new JspException("Errore nella lettura Rda: " + wsdmMsg, null);
    }
    page.setAttribute("initERPvsWSDM", "1", PageContext.REQUEST_SCOPE);

    WSDMProtocolloDocumentoType wsdmProtocolloDocumento = wsdmProtocolloDocumentoRes.getProtocolloDocumento();
    String oggettoGara = wsdmProtocolloDocumento.getOggetto();
    page.setAttribute("initNOT_GAR",oggettoGara, PageContext.REQUEST_SCOPE);
    WSDMTabellaType[] datiPersonalizzati = wsdmProtocolloDocumento.getDatiPersonalizzati();

    if(datiPersonalizzati !=null){
      for(int i=0; i < datiPersonalizzati.length; i++){
        WSDMTabellaType datoP = datiPersonalizzati[i];
        String nome = datoP.getNome();//RDA verifica
        if("rda".equals(nome)){
          page.setAttribute("initNUMRDA",numeroRda, PageContext.REQUEST_SCOPE);
          boolean inserimentoGarerda= true;
          page.setAttribute("initGarerda", new Boolean(inserimentoGarerda),PageContext.REQUEST_SCOPE);
        }
        WSDMRigaType[] righe = datoP.getRiga();
        for(int j=0; j < righe.length; j++){
          WSDMRigaType riga = righe[j];
          WSDMCampoType[] campi = riga.getCampo();
          WSDMCampoType campo;
          if(campi!=null){
            for(int k=0; k < campi.length; k++){
              campo = campi[k];
              String nomeCampo = campo.getNome();
              if("rup".equals(nomeCampo)){
                String valRup = campo.getValore();
                WSDMProtocolloAnagraficaType protocolloAnagraficaType = gestioneWSDMManager.WSDMAnagraficaLeggi(valRup,idconfi);
                //cerco su TECNI
                if(protocolloAnagraficaType != null && protocolloAnagraficaType.getCodiceFiscale().length() != 0){
                Vector<?> datiTecnico = sqlManager.getVector("select codtec,nomtec from tecni where cftec = ?", new Object[] { protocolloAnagraficaType.getCodiceFiscale() });
                if (datiTecnico != null && datiTecnico.size() > 0) {
                  String codtec = (String) ((JdbcParametro) datiTecnico.get(0)).getValue();
                  String nomtec = (String) ((JdbcParametro) datiTecnico.get(1)).getValue();
                  page.setAttribute("initCODRUP",codtec, PageContext.REQUEST_SCOPE);
                  page.setAttribute("initNOMTEC1",nomtec, PageContext.REQUEST_SCOPE);
                  }
                else{
                  String codtec = null;
                  if (geneManager.isCodificaAutomatica("TECNI", "CODTEC")) {
                    // Setto il codice impresa come chiave altrimenti non ritorna sulla riga
                    // giusta
                    TransactionStatus status = this.sqlManager.startTransaction();
                    this.sqlManager.commitTransaction(status);
                    status = this.sqlManager.startTransaction();
                    codtec = geneManager.calcolaCodificaAutomatica("TECNI","CODTEC");
                    this.sqlManager.commitTransaction(status);
                    String intestazione = protocolloAnagraficaType.getCognomeointestazione();
                    int index = protocolloAnagraficaType.getCognomeointestazione().indexOf(" ");
                    status = this.sqlManager.startTransaction();
                    if(index > 0){
                      String cogtei = protocolloAnagraficaType.getCognomeointestazione().substring(0, index);
                      String nometei = protocolloAnagraficaType.getCognomeointestazione().substring(index+1, intestazione.length());
                      sqlManager.update("insert into tecni(codtec,nometei,cogtei,nomtec,cftec) values (?,?,?,?,?)", new Object[] {codtec,nometei,cogtei,intestazione,protocolloAnagraficaType.getCodiceFiscale()});
                    }else{
                      sqlManager.update("insert into tecni(codtec,cogtei,nomtec,cftec) values (?,?,?,?)", new Object[] {codtec,intestazione,intestazione,protocolloAnagraficaType.getCodiceFiscale()});
                    }
                    this.sqlManager.commitTransaction(status);
                    page.setAttribute("initCODRUP",codtec, PageContext.REQUEST_SCOPE);
                    page.setAttribute("initNOMTEC1",intestazione, PageContext.REQUEST_SCOPE);
                  }
                  else{
                    String messaggioErrore   = "Per poter procedere alla creazione della gara"
                      + " occorre impostare la codifica automatica attiva";
                    throw new JspException(messaggioErrore, null);
                    }
                  }
                }
              }

              if("dec".equals(nomeCampo)){
                String valDec = campo.getValore();
                WSDMProtocolloAnagraficaType protocolloAnagraficaType = gestioneWSDMManager.WSDMAnagraficaLeggi(valDec,idconfi);
                //cerco su TECNI
                if(protocolloAnagraficaType != null && protocolloAnagraficaType.getCodiceFiscale().length() != 0){
                Vector<?> datiTecnico = sqlManager.getVector("select codtec,nomtec from tecni where cftec = ?", new Object[] { protocolloAnagraficaType.getCodiceFiscale() });
                if (datiTecnico != null && datiTecnico.size() > 0) {
                  String codtec = (String) ((JdbcParametro) datiTecnico.get(0)).getValue();
                  String nomtec = (String) ((JdbcParametro) datiTecnico.get(1)).getValue();
                  page.setAttribute("initCODTEC",codtec, PageContext.REQUEST_SCOPE);
                  page.setAttribute("initNOMTEC",nomtec, PageContext.REQUEST_SCOPE);
                  boolean inserimentoGartecni= true;
                  page.setAttribute("initGartecni", new Boolean(inserimentoGartecni),PageContext.REQUEST_SCOPE);
                  }
                else{
                  String codtec = null;
                  if (geneManager.isCodificaAutomatica("TECNI", "CODTEC")) {
                    // Setto il codice impresa come chiave altrimenti non ritorna sulla riga
                    // giusta
                    TransactionStatus status = this.sqlManager.startTransaction();
                    this.sqlManager.commitTransaction(status);
                    status = this.sqlManager.startTransaction();
                    codtec = geneManager.calcolaCodificaAutomatica("TECNI","CODTEC");
                    this.sqlManager.commitTransaction(status);
                    String intestazione = protocolloAnagraficaType.getCognomeointestazione();
                    int index = protocolloAnagraficaType.getCognomeointestazione().indexOf(" ");
                    status = this.sqlManager.startTransaction();
                    if(index > 0){
                      String cogtei = protocolloAnagraficaType.getCognomeointestazione().substring(0, index);
                      String nometei = protocolloAnagraficaType.getCognomeointestazione().substring(index+1, intestazione.length());
                      sqlManager.update("insert into tecni(codtec,nometei,cogtei,nomtec,cftec) values (?,?,?,?,?)", new Object[] {codtec,nometei,cogtei,intestazione,protocolloAnagraficaType.getCodiceFiscale()});
                    }else{
                      sqlManager.update("insert into tecni(codtec,cogtei,nomtec,cftec) values (?,?,?,?)", new Object[] {codtec,intestazione,intestazione,protocolloAnagraficaType.getCodiceFiscale()});
                      }
                    this.sqlManager.commitTransaction(status);
                    page.setAttribute("initCODTEC",codtec, PageContext.REQUEST_SCOPE);
                    page.setAttribute("initNOMTEC",intestazione, PageContext.REQUEST_SCOPE);
                    boolean inserimentoGartecni= true;
                    page.setAttribute("initGartecni", new Boolean(inserimentoGartecni),PageContext.REQUEST_SCOPE);
                    }
                  else{
                    String messaggioErrore   = "Per poter procedere alla creazione della gara"
                      + " occorre impostare la codifica automatica attiva";
                    throw new JspException(messaggioErrore, null);
                  }
                  }
                }
              }

              if("importo".equals(nomeCampo)){
                String valImporto = campo.getValore();
                valImporto = UtilityStringhe.convertiNullInStringaVuota(valImporto);
                if(!"".equals(valImporto)){
                  Double impapp = new Double(valImporto);
                  page.setAttribute("initIMPAPP",impapp, PageContext.REQUEST_SCOPE);
                }
              }

              if("oneri".equals(nomeCampo)){
                String valOneri = campo.getValore();
                valOneri = UtilityStringhe.convertiNullInStringaVuota(valOneri);
                if(!"".equals(valOneri)){
                  Double impsic = new Double(valOneri);
                  page.setAttribute("initIMPSIC",impsic, PageContext.REQUEST_SCOPE);
                }
              }

              if("smart_cig".equals(nomeCampo)){
                String valSmartCig = campo.getValore();
                page.setAttribute("initCODCIG",valSmartCig, PageContext.REQUEST_SCOPE);
              }


              if("duvri".equals(nomeCampo)){
                String valDuvri = campo.getValore();
                valDuvri = UtilityStringhe.convertiNullInStringaVuota(valDuvri);
                valDuvri = valDuvri.toUpperCase();
                Long tus = null;
                if(!"".equals(valDuvri)){

                  if("SI".equals(valDuvri)){
                    tus = new Long(2);
                  }
                  if("NO".equals(valDuvri)){
                    tus = new Long(1);
                  }

                  page.setAttribute("initTUS",tus, PageContext.REQUEST_SCOPE);
                }
              }
            }//for
          }

        }


      }




    }else{
      //controllo: bloccare wizard
    }


  }


  /**
   * Estrazione dei dati dei Procedimenti Rda da ERP per inizializzare la scheda dei dati generali di una
   * gara lotto unico o di un lotto di gara
   * @param page
   * @param sqlManager
   * @param numeroRda
   * @throws SQLException
   * @throws GestoreException
   * @throws JspException
   */
  private void setDatiInitDaWSERP(PageContext page, SqlManager sqlManager, String numeroRda, String esercizio)
    throws SQLException, GestoreException, JspException {

    ProfiloUtente profilo = (ProfiloUtente) page.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    Long syscon = new Long(profilo.getId());
    String profiloAttivo = (String) page.getSession().getAttribute("profiloAttivo");
    String servizio = page.getRequest().getParameter("servizio");
    if(servizio==null || "".equals(servizio))
      servizio ="WSERP";

    String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);
    String username = credenziali[0];
    String password = credenziali[1];
    WSERPRdaType erpSearch = new WSERPRdaType();
    //imposto il num Rda come filtro ed esercizio obbligatorio,per ora metto fisso
    erpSearch.setCodiceRda(numeroRda);
    erpSearch.setEsercizio(esercizio);
    WSERPRdaResType wserpRdaRes = this.gestioneWSERPManager.wserpListaRda(username, password, servizio, erpSearch );

    WSERPRdaType[] rdaArray = wserpRdaRes.getRdaArray();

    if(rdaArray!= null){
      WSERPRdaType rda = rdaArray[0];

      String codProcedimento = rda.getCodiceRda();
      codProcedimento = UtilityStringhe.convertiNullInStringaVuota(codProcedimento);
      esercizio = rda.getEsercizio();
      esercizio = UtilityStringhe.convertiNullInStringaVuota(esercizio);
      String newCodGara = codProcedimento + "-" + esercizio;
      page.setAttribute("initNGARA", newCodGara, PageContext.REQUEST_SCOPE);
      String oggetto = rda.getOggetto();
      page.setAttribute("initNOT_GAR", oggetto, PageContext.REQUEST_SCOPE);
      //page.setAttribute("initCODCARR",codProcedimento, PageContext.REQUEST_SCOPE);
      page.setAttribute("initNUMRDA",codProcedimento, PageContext.REQUEST_SCOPE);
      page.setAttribute("initESERCIZIO",esercizio, PageContext.REQUEST_SCOPE);
      boolean inserimentoGarerda= true;
      page.setAttribute("initGarerda", new Boolean(inserimentoGarerda),PageContext.REQUEST_SCOPE);

      String natura = rda.getNatura();
      Long tipgen = null;
      if("L".equals(natura)){
        tipgen = new Long(1);
      }else{
        if("F".equals(natura)){
          tipgen = new Long(2);
        }else{
          if("S".equals(natura)){
            tipgen = new Long(3);
          }
        }
      }
      page.setAttribute("initTIPGEN", tipgen, PageContext.REQUEST_SCOPE);

      String sceltaContraente = rda.getSceltaContraente();
      sceltaContraente = UtilityStringhe.convertiNullInStringaVuota(sceltaContraente);

      if(!"".equals(sceltaContraente)){
        //verifica sul profilo
        String valoreA1z03 = (String) this.sqlManager.getObject(
            "select tab2d2 from tab2 where tab2cod='A1z03' and tab2d1= ? ", new Object[]{profiloAttivo});
        valoreA1z03 = UtilityStringhe.convertiNullInStringaVuota(valoreA1z03);
        if (!"".equals(valoreA1z03) && valoreA1z03.contains(",")) {
          String vetValori[] = valoreA1z03.split(",");
          for (int z=0; z < vetValori.length; z++ ) {
            if(sceltaContraente.equals(vetValori[z])){
              String tipgar = UtilityStringhe.convertiNullInStringaVuota(sceltaContraente);
              if(!"".equals(tipgar)){
                page.setAttribute("initTIPGARG", tipgar, PageContext.REQUEST_SCOPE);
                Long iterga = this.pgManager.getITERGA(new Long(tipgar));
                page.setAttribute("initITERGAG", iterga, PageContext.REQUEST_SCOPE);
              }
              break;
            }
          }
        }
      }


      String codCritlic = rda.getCriterioAggiudicazione();
      Long critlic = null;
      if("A".equals(codCritlic) || "C".equals(codCritlic)){
        critlic = new Long(1);
        if("C".equals(codCritlic)){
          page.setAttribute("initMODASTG", new Long(1), PageContext.REQUEST_SCOPE);
        }else{
          page.setAttribute("initMODASTG", new Long(2), PageContext.REQUEST_SCOPE);
        }

      }
      if("B".equals(codCritlic)){
        critlic = new Long(2);
      }
      page.setAttribute("initCRITLICG", critlic, PageContext.REQUEST_SCOPE);


      String esenteCig = rda.getIsStrumentale();
      if("S".equals(esenteCig)){
        page.setAttribute("initIsStrumentale", "1" , PageContext.REQUEST_SCOPE);
      }

      String codCpv = rda.getCodCpv();
      page.setAttribute("initCODCPV", codCpv, PageContext.REQUEST_SCOPE);

      String durataContratto = rda.getCodiceArticolo();
      durataContratto = UtilityStringhe.convertiNullInStringaVuota(durataContratto);
      durataContratto = durataContratto.trim();
      page.setAttribute("initTEUTIL", durataContratto, PageContext.REQUEST_SCOPE);

      String divisione = rda.getDivisione();
      page.setAttribute("initCENINT", divisione, PageContext.REQUEST_SCOPE);
      String nomein = (String) sqlManager.getObject("select nomein from uffint where codein = ?", new Object[] { divisione });
      nomein = UtilityStringhe.convertiNullInStringaVuota(nomein);
      if(!"".equals(nomein)){
        page.setAttribute("initNOMEIN", nomein, PageContext.REQUEST_SCOPE);
      }

      Double impapp = rda.getValoreStimato();
      page.setAttribute("initIMPAPP",impapp, PageContext.REQUEST_SCOPE);
      Double impsic = rda.getImportoSicurezza();
      page.setAttribute("initIMPSIC",impsic, PageContext.REQUEST_SCOPE);

      Double impRinnovi = rda.getImportoRinnovi();
      if(impRinnovi!=null && impRinnovi > new Double(0)){
        page.setAttribute("initAMMRIN","1", PageContext.REQUEST_SCOPE);
        page.setAttribute("initIMPRIN", impRinnovi, PageContext.REQUEST_SCOPE);
      }
      Double impAltro = rda.getQuantita();
      if(impAltro!=null && impAltro > new Double(0) ){
        page.setAttribute("initAMMOPZ","1", PageContext.REQUEST_SCOPE);
        page.setAttribute("initIMPALTRO",impAltro, PageContext.REQUEST_SCOPE);
      }


      WSERPAnagraficaType[] wserpAnagraficaArray = rda.getTecniciArray();

      if(wserpAnagraficaArray !=null){
        for(int i=0; i < wserpAnagraficaArray.length; i++){
          //verifico che non sia nullo perche' l'array ha lunghezza fissa
          // ma potrebbe avere caselle vuote
          WSERPAnagraficaType wserpAnagrafica = wserpAnagraficaArray[i];
          if(wserpAnagrafica != null){
            String tipoAnagrafica = wserpAnagrafica.getTipo();
            if("T".equals(tipoAnagrafica)){
              Long incarico = wserpAnagrafica.getIncarico();
              //selezione su TECNI
              String codice = wserpAnagrafica.getCodiceFiscale();
              String[] datiTecnico = this.getDatiTecnico(codice);
              String codtec = datiTecnico[0];
              String nomtec = datiTecnico[1];

              if(new Long(1).equals(incarico)){
               //RUP
                page.setAttribute("initCODRUP",codtec, PageContext.REQUEST_SCOPE);
                page.setAttribute("initNOMTEC1",nomtec, PageContext.REQUEST_SCOPE);
              }

              if(new Long(4).equals(incarico)){
                page.setAttribute("initCODRPROGR",codtec, PageContext.REQUEST_SCOPE);
                page.setAttribute("initNOMRPROGR",nomtec, PageContext.REQUEST_SCOPE);
                boolean inserimentoGartecni= true;
                page.setAttribute("initGartecni", new Boolean(inserimentoGartecni),PageContext.REQUEST_SCOPE);
              }

              if(new Long(2).equals(incarico)){
                page.setAttribute("initCODDEC",codtec, PageContext.REQUEST_SCOPE);
                page.setAttribute("initNOMDEC",nomtec, PageContext.REQUEST_SCOPE);
                boolean inserimentoGartecni= true;
                page.setAttribute("initGartecni", new Boolean(inserimentoGartecni),PageContext.REQUEST_SCOPE);
              }

              if(new Long(5).equals(incarico)){
                page.setAttribute("initCODRO",codtec, PageContext.REQUEST_SCOPE);
                page.setAttribute("initNOMRO",nomtec, PageContext.REQUEST_SCOPE);
                boolean inserimentoGartecni= true;
                page.setAttribute("initGartecni", new Boolean(inserimentoGartecni),PageContext.REQUEST_SCOPE);
              }

            }

          }

        }

      }

    }


    page.setAttribute("initGestioneUnicaERP", "1", PageContext.REQUEST_SCOPE);
  }

  /**
   * Estrazione dei dati delle Rda da LFS per inizializzare la scheda dei dati generali di una
   * gara lotto unico o di un lotto di gara
   * @param page
   * @param sqlManager
   * @param numeroRda
   * @throws SQLException
   * @throws GestoreException
   * @throws JspException
   */
  private void setDatiInitDaAppaRda(PageContext page, SqlManager sqlManager, String codiceLavoro, Long numeroAppalto)
    throws SQLException, GestoreException, JspException {

    if (this.sqlManager.isTable("APPARDA")){
      List datiAppaRda = sqlManager.getListVector(
          "select CODICERDA,OGGETTO,TIPORDA from APPARDA " +
           "where CODLAV = ? AND NAPPAL = ? AND NPROAT = ?",
            new Object[]{codiceLavoro, numeroAppalto,new Long(1)});
      if(datiAppaRda != null && datiAppaRda.size() > 0){
        String elencoNumRda="";
        String elencoVoceRda="";
        String elencoTipoRda="";
        for(int i=0; i < datiAppaRda.size(); i++){
          String codiceRda = (String) SqlManager.getValueFromVectorParam(datiAppaRda.get(i), 0).getValue();
          codiceRda = UtilityStringhe.convertiNullInStringaVuota(codiceRda);
          String oggetto = (String) SqlManager.getValueFromVectorParam(datiAppaRda.get(i), 1).getValue();
          oggetto = UtilityStringhe.convertiNullInStringaVuota(oggetto);
          oggetto = StringEscapeUtils.escapeJavaScript(oggetto);
          String tipoRda = (String) SqlManager.getValueFromVectorParam(datiAppaRda.get(i), 2).getValue();
          tipoRda = UtilityStringhe.convertiNullInStringaVuota(tipoRda);
          if(i==0){
            elencoNumRda = codiceRda;
            elencoVoceRda = oggetto;
            elencoTipoRda = tipoRda;
          }else{
            elencoNumRda = elencoNumRda+";"+codiceRda;
            elencoVoceRda = elencoVoceRda+";"+oggetto;
            elencoTipoRda = elencoTipoRda+";"+tipoRda;
          }

        }
        boolean inserimentoFromApparda= true;
        page.setAttribute("initFromApparda", new Boolean(inserimentoFromApparda),PageContext.REQUEST_SCOPE);
        if(inserimentoFromApparda){
          page.setAttribute("initNUMRDA",elencoNumRda, PageContext.REQUEST_SCOPE);
          page.setAttribute("initVOCE",elencoVoceRda, PageContext.REQUEST_SCOPE);
          page.setAttribute("initTIPORDA",elencoTipoRda, PageContext.REQUEST_SCOPE);
        }
      }
    }
  }


  /**
   * Determina SICINC della gara complentare e memorizza il valore
   * nel request in modo da essere utilizzati poi nella pagina
   *
   * @param page
   * @param sqlManager
   * @throws SQLException
   */
  private void initSICINC(PageContext page, SqlManager sqlManager)
  throws SQLException {
      String sql = "SELECT SICINC FROM GARE WHERE NGARA = ?";
        HashMap keyParent = UtilityTags.stringParamsToHashMap(
            (String) page.getAttribute(
                UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT,
                PageContext.REQUEST_SCOPE), null);

        String sicinc = (String) sqlManager.getObject(sql,
                new Object[] { ((JdbcParametro) keyParent.get("TORN.CODGAR")).getStringValue() });

        page.setAttribute("initSICINC",sicinc,PageContext.REQUEST_SCOPE);
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }

  /**
   * Metodo per determinare il valore da assegnare al campo GARE.TATTOG (associato
   * al tabellato A2045) confrontando la descrizione del tabellato A2040 (
   *
   * @param campoAPPR_TDAPPR1
   * @return Ritorna il valore del campo TAB1TIP per il tabellato A2045 da assegnare
   * al campo GARE.TATTOG
   * @throws GestoreException
   */
  private Long getTATTOG(JdbcParametro campoAPPR_TDAPPR1) throws GestoreException{
    Long result = null;

    if(campoAPPR_TDAPPR1.getValue() != null){
      String descrTabA2040Originale = tabellatiManager.getDescrTabellato("A2040",
          campoAPPR_TDAPPR1.getStringValue()).trim();
      String descrTabA2040 = descrTabA2040Originale.toUpperCase();

      List listaTabellatoA2045 = tabellatiManager.getTabellato("A2045");

      boolean trovataDescrNelTabellatoA2045 = false;
      for(int i=0; i < listaTabellatoA2045.size() && !trovataDescrNelTabellatoA2045; i++){
        Tabellato tabellato = (Tabellato) listaTabellatoA2045.get(i);
        if(descrTabA2040.equals(tabellato.getDescTabellato().trim().toUpperCase())){
          result = new Long(tabellato.getTipoTabellato());
          trovataDescrNelTabellatoA2045 = true;
        }
      }
      if(! trovataDescrNelTabellatoA2045){
        // Nel tabellato A2045 non e' stata trovata nessuna occorrenza con
        // descrizione uguale ad una delle descrizioni del tabellato A2040
        // (dopo aver fatto trim e upperCase di entrambi i campi).
        // Si prosegue con l'inserimento della occorrenza del tabellato A2040
        // nel tabellato A2045
        try {
          Long tab1tip = (Long) this.sqlManager.getObject(
              "select max(TAB1TIP) from TAB1 where TAB1COD = ?",
              new Object[]{"A2045"});

          tab1tip = new Long(tab1tip.longValue() + 1);
          TransactionStatus status = this.sqlManager.startTransaction();
          this.sqlManager.update(
            "insert into TAB1 (TAB1COD, TAB1TIP, TAB1DESC, TAB1NORD) values (?, ?, ?, ?)",
            new Object[]{"A2045", tab1tip, descrTabA2040Originale, new Double(0)});
          this.sqlManager.commitTransaction(status);
          result = tab1tip;

        } catch (SQLException e1) {
          throw new GestoreException("Errore nell'inserimento di una " +
              "nuova occorrenza per il tabellato A2045", null, e1);
        }
      }
    }
    return result;
  }

  /**
   * Metodo per determinare codice e intestazione del tecnico
   *
   * @param codice fiscale
   * @return Ritorna codice e intestazione del tecnico
   * @throws GestoreException
   * @throws SQLException
   */
  private String[] getDatiTecnico(String codice) throws GestoreException, SQLException{
    String[] datiTecnico = new String[2];
    Vector<?> vectTecnico = this.sqlManager.getVector("select codtec,nomtec from tecni where cftec = ?", new Object[]{codice});
    if(vectTecnico!=null && vectTecnico.size()>0){
      String codtec =  SqlManager.getValueFromVectorParam(vectTecnico, 0).getStringValue();
      String nomtec =  SqlManager.getValueFromVectorParam(vectTecnico, 1).getStringValue();
      datiTecnico[0] = codtec;
      datiTecnico[1] = nomtec;
    }
    return datiTecnico;
  }

  private void setDatiInitPerRilancio(PageContext page, SqlManager sqlManager,
      String chiaveAppalto, String preced, String tipoAppalto) throws SQLException, GestoreException {
    Vector<?> datiGara = sqlManager.getVector("select RIBCAL, MODLICG, IMPMIS, IMPNRM, IMPSMI, IMPCOR, IMPNRC,IMPSCO, ONPRGE, IMPAPP, IMPNRL, IMPSIC, ONSOGRIB, NOT_GAR, CRITLICG from GARE where NGARA=?", new Object[]{preced});
    if(datiGara!=null && datiGara.size()>0){
      Long ribcalPadre = SqlManager.getValueFromVectorParam(datiGara, 0).longValue();
      Long modlicg = SqlManager.getValueFromVectorParam(datiGara, 1).longValue();
      Double impmis = SqlManager.getValueFromVectorParam(datiGara, 2).doubleValue();
      Double impnrm = SqlManager.getValueFromVectorParam(datiGara, 3).doubleValue();
      Double impsmi = SqlManager.getValueFromVectorParam(datiGara, 4).doubleValue();
      Double impcor = SqlManager.getValueFromVectorParam(datiGara, 5).doubleValue();
      Double impnrc = SqlManager.getValueFromVectorParam(datiGara, 6).doubleValue();
      Double impsco = SqlManager.getValueFromVectorParam(datiGara, 7).doubleValue();
      Double onprge = SqlManager.getValueFromVectorParam(datiGara, 8).doubleValue();
      Double impapp = SqlManager.getValueFromVectorParam(datiGara, 9).doubleValue();
      Double impnrl = SqlManager.getValueFromVectorParam(datiGara, 10).doubleValue();
      Double impsic = SqlManager.getValueFromVectorParam(datiGara, 11).doubleValue();
      String onsogrib = SqlManager.getValueFromVectorParam(datiGara, 12).getStringValue();
      String notgar = SqlManager.getValueFromVectorParam(datiGara, 13).getStringValue();
      Long critlicgPadre = SqlManager.getValueFromVectorParam(datiGara, 14).longValue();

      //Inizializzazione CRITLICG
      Long critlicg = new Long(1);
      if(new Long(3).equals(critlicgPadre))
        critlicg = new Long(3);
      page.setAttribute("initCRITLICG",critlicg,PageContext.REQUEST_SCOPE);

      Long numformato51 = null;
      //Inizializzazione DETLICG
      Long detlicg=null;
      if(modlicg!=null && modlicg.longValue()==6){
        numformato51 = (Long)this.sqlManager.getObject("select count(id) formato from g1cridef where ngara=? and formato=51", new Object[]{preced});
        if(numformato51!=null && numformato51.longValue()>0){
          if("1".equals(tipoAppalto))
            detlicg=new Long(2);
          else
            detlicg=new Long(5);
        }else{
          detlicg=new Long(4);
        }
      }
      else if(new Long(1).equals(ribcalPadre) && "1".equals(tipoAppalto))
        detlicg = new Long(2);
      else if(new Long(1).equals(ribcalPadre) && ("2".equals(tipoAppalto) || "3".equals(tipoAppalto)))
        detlicg = new Long(5);
      else if(new Long(2).equals(ribcalPadre))
        detlicg = new Long(4);
      page.setAttribute("initDETLICG",detlicg,PageContext.REQUEST_SCOPE);

      //Inizializzazione MODLICG
      page.setAttribute("initMODLICG", new Long(1),PageContext.REQUEST_SCOPE);

      //Inizializzazione RIBCAL
      Long ribcal=null;
      if(!new Long(6).equals(modlicg))
        ribcal=ribcalPadre;
      else{
        if(numformato51!=null && numformato51.longValue()>0)
          ribcal=new Long(1);
        else
          ribcal=new Long(2);
      }
      page.setAttribute("initRIBCAL",ribcal,PageContext.REQUEST_SCOPE);

      //Inizializzazione RICMANO
      page.setAttribute("initRICMANO","2",PageContext.REQUEST_SCOPE);

      //Inizializzazione COMPREQ
      page.setAttribute("initCOMPREQ","2",PageContext.REQUEST_SCOPE);

      //Inizializzazione VALTEC
      page.setAttribute("initVALTEC","2",PageContext.REQUEST_SCOPE);

      //Inizializzazione IMPMIS
      page.setAttribute("initIMPMIS",impmis,PageContext.REQUEST_SCOPE);

      //Inizializzazione IMPNRM
      page.setAttribute("initIMPNRM",impnrm,PageContext.REQUEST_SCOPE);

      //Inizializzazione IMPSMI
      page.setAttribute("initIMPSMI",impsmi,PageContext.REQUEST_SCOPE);

      //Inizializzazione IMPCOR
      page.setAttribute("initIMPCOR",impcor,PageContext.REQUEST_SCOPE);

      //Inizializzazione IMPNRC
      page.setAttribute("initIMPNRC",impnrc,PageContext.REQUEST_SCOPE);

      //Inizializzazione IMPSCO
      page.setAttribute("initIMPSCO",impsco,PageContext.REQUEST_SCOPE);

      //Inizializzazione ONPRGE
      page.setAttribute("initONPRGE",onprge,PageContext.REQUEST_SCOPE);

      //Inizializzazione IMPAPP
      page.setAttribute("initIMPAPP",impapp,PageContext.REQUEST_SCOPE);

      //Inizializzazione IMPNRL
      page.setAttribute("initIMPNRL",impnrl,PageContext.REQUEST_SCOPE);

      //Inizializzazione IMPSIC
      page.setAttribute("initIMPSIC",impsic,PageContext.REQUEST_SCOPE);

      //Inizializzazione ONSOGRIB
      page.setAttribute("initONSOGRIB",onsogrib,PageContext.REQUEST_SCOPE);

      //Inizializzazione PRECED
      page.setAttribute("initPRECED",preced,PageContext.REQUEST_SCOPE);

      //Inizializzazione NOT_GAR
      page.setAttribute("initNOT_GAR",notgar,PageContext.REQUEST_SCOPE);
      String chiaveCategorie=preced;
      Vector<?> datiV_GARE_GENERE = this.sqlManager.getVector("select genere,codgar from v_gare_genere where codice=?", new Object[]{preced});
      if(datiV_GARE_GENERE!=null && datiV_GARE_GENERE.size()>0){
        Long genere =  SqlManager.getValueFromVectorParam(datiV_GARE_GENERE, 0).longValue();
        if(genere!=null && genere.longValue()==300)
          chiaveCategorie=SqlManager.getValueFromVectorParam(datiV_GARE_GENERE, 1).getStringValue();
      }
      String codgar = SqlManager.getValueFromVectorParam(datiV_GARE_GENERE, 1).getStringValue();
      Vector<?> datiTorn = this.sqlManager.getVector("select settore,prerib, cenint, nomein from torn left join uffint on codein=cenint where codgar=?", new Object[]{codgar});
      String settore = null;
      Long prerib = null;
      String cenint = null;
      String nomein = null;
      if(datiTorn!=null && datiTorn.size()>0){
        settore = SqlManager.getValueFromVectorParam(datiTorn, 0).getStringValue();
        prerib = SqlManager.getValueFromVectorParam(datiTorn, 1).longValue();
        cenint = SqlManager.getValueFromVectorParam(datiTorn, 2).getStringValue();
        nomein = SqlManager.getValueFromVectorParam(datiTorn, 3).getStringValue();
      }
      if(settore != null){
        page.setAttribute("initSETTORE",settore,PageContext.REQUEST_SCOPE);
      }
      if(prerib != null){
        page.setAttribute("initPrerib",prerib,PageContext.REQUEST_SCOPE);
      }
      if(cenint != null && !"".equals(cenint)){
        page.setAttribute("initCENINT",cenint,PageContext.REQUEST_SCOPE);
      }
      if(nomein != null && !"".equals(nomein)){
        page.setAttribute("initNOMEIN",nomein,PageContext.REQUEST_SCOPE);
      }

      //Estrazione dei dati da CATG dalla gare padre
      Vector<JdbcParametro> datiCatg = sqlManager.getVector(
          "select CATG.CATIGA, CAIS.DESCAT, CAIS.ACONTEC, CAIS.QUAOBB," +
                " CAIS.TIPLAVG, CATG.NGARA, CATG.IMPBASG, CATG.NUMCLA, " +
                " CATG.IMPIGA, V_CAIS_TIT.ISFOGLIA " +
            "from CATG, CAIS, V_CAIS_TIT " +
           "where CATG.NGARA = ? " +
            " and CAIS.CAISIM = CATG.CATIGA "+
            " and CAIS.CAISIM = V_CAIS_TIT.CAISIM",
            new Object[]{chiaveCategorie});
      if(datiCatg != null && datiCatg.size() > 0){
        //Si deve sbiancare il valore CATG.NGARA in modo che al salvataggio verrà sostituito con quello della nuova gara
        datiCatg.set(5, new JdbcParametro(JdbcParametro.TIPO_TESTO,null));
        page.setAttribute("initCATG", datiCatg, PageContext.REQUEST_SCOPE);
      }

      //Estrazione dei dati da OPES dalla gare padre
      List<Vector> listaCategorieUlteriori = sqlManager.getListVector(
          "select OPES.CATOFF, CAIS.DESCAT, CAIS.ACONTEC, CAIS.QUAOBB, CAIS.TIPLAVG, " +
                 "OPES.NGARA3, OPES.NOPEGA, OPES.IMPAPO,  OPES.NUMCLU, OPES.ISCOFF, " +
                 "OPES.ACONTEC, OPES.QUAOBB, OPES.DESCOP, V_CAIS_TIT.ISFOGLIA " +
            "from OPES, CAIS, V_CAIS_TIT " +
           "where OPES.NGARA3 = ? " +
             "and OPES.CATOFF = CAIS.CAISIM " +
             "and CAIS.CAISIM = V_CAIS_TIT.CAISIM  " +
           "order by OPES.NOPEGA asc", new Object[]{chiaveCategorie});
      if(listaCategorieUlteriori != null && listaCategorieUlteriori.size() > 0){
        if(listaCategorieUlteriori!=null && listaCategorieUlteriori.size()>0){
          //Si deve sbiancare il valore OPES.NGARA3 in modo che al salvataggio verrà sostituito con quello della nuova gara
          Vector riga=null;
          for(int i=0;i<listaCategorieUlteriori.size();i++){
            riga=listaCategorieUlteriori.get(i);
            riga.set(5, new JdbcParametro(JdbcParametro.TIPO_TESTO,null));
            listaCategorieUlteriori.set(i, riga);
          }
        }
        page.setAttribute("initOPES", listaCategorieUlteriori, PageContext.REQUEST_SCOPE);
      }
    }
  }
}
