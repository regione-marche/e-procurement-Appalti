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
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
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
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.bl.SmatManager;
import it.eldasoft.sil.pl.struts.gestori.GestoreAPPA;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore dell'entita' GARE: insert, update, delete
 *
 * @author Luca.Giacomazzo
 */
public class GestoreGARE extends AbstractGestoreEntita {

  /** Logger */
  static Logger            logger           = Logger.getLogger(GestoreGARE.class);

  /** Manager di PG */
  private PgManager        pgManager        = null;

  /** Manager per l'esecuzione di query */
  private SqlManager       sqlManager       = null;

  /** Manager per l'estrazione di dati tabellati */
  private TabellatiManager tabellatiManager = null;

  /** Manager di SMAT */
  private SmatManager  smatManager  = null;

  /** Manager di Autovie */
  private AutovieManager  autovieManager  = null;

  private GenChiaviManager genChiaviManager = null;

  private GeneManager geneManager = null;

  /** Manager Integrazione WSERP */
  private GestioneWSERPManager gestioneWSERPManager;

  private PgManagerEst1        pgManagerEst1        = null;

  @Override
  public String getEntita() {
    return "GARE";
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager di Piattaforma Gare
    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);
    // Estraggo il manager per eseguire query
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

    geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
        this.getServletContext(), GeneManager.class);

    pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        this.getServletContext(), PgManagerEst1.class);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    //Integrazione con WSERP
    String urlWSERP = ConfigManager.getValore("wserp.erp.url");
    if(urlWSERP != null && !"".equals(urlWSERP)){
      gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
      this.getServletContext(), GestioneWSERPManager.class);
      WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
      if(configurazione.isEsito()){
        String tipoWSERP = configurazione.getRemotewserp();
        String codiceLotto = datiForm.getString("GARE.NGARA");
        String codiceGara;
        try {
          codiceGara = (String)this.sqlManager.getObject("select codgar1 from gare where ngara=?", new Object[]{codiceLotto});
        } catch (SQLException sqle) {
          throw new GestoreException("Errore nella gestione della cancellazione delle occorrenze in GARE",null, sqle);
        }
        String linkrda = "";
        if("SMEUP".equals(tipoWSERP)){
          linkrda = "2";
        }else if("AVM".equals(tipoWSERP) || "FNM".equals(tipoWSERP)){
          Long countRda;
          try {
            countRda = (Long) sqlManager.getObject(
                "select count(*) from garerda where codgar = ?",new Object[] { codiceGara });
            if(new Long(0).equals(countRda)){
              linkrda = "2";
            }else{
              linkrda = "1";
            }
          } catch (SQLException e) {
            throw new GestoreException(
                "Errore nella lettura delle rda", null, e);
          }
        }//if tipoWSERP

        int res = this.gestioneWSERPManager.scollegaRda(codiceGara, codiceLotto, linkrda, null, null, this.getRequest());
        if(res < 0){
          throw new GestoreException(
              "Errore durante l'operazione di scollegamento delle RdA dalla gara",
              "scollegaRdaGara", null);
        }
      }
    }

    // Chiamo la funzione centralizzata per l'eliminazione della tornata, la
    // quale
    // effettua la cancellazione di tutte le entita' figlie
    pgManager.deleteGARE(datiForm.getString("GARE.NGARA"));

    //Se la gara non è a lotto unico si deve aggiornare TORN.IMPTOR
    this.AggiornaImportoTotaleTorn(datiForm,"DEL");

    //Se si sta cancellando un lotto di una gara con plico unico ed il lotto risulta aggiudicato in via definitiva
    //si deve gestire la cancellazione di GARECONT
    String ngara=datiForm.getString("GARE.NGARA");

    try {
      String codgar= (String)this.sqlManager.getObject("select codgar1 from gare where ngara=?", new Object[]{ngara});
      Long genere= (Long)this.sqlManager.getObject("select genere from gare where codgar1=? and ngara=codgar1", new Object[]{codgar});
      if(new Long(3).equals(genere) && !ngara.equals(codgar)){
        String ditta=(String)this.sqlManager.getObject("select ditta from gare where ngara=?", new Object[]{ngara});
        if (ditta != null && !"".equals(ditta)) {
          Long conteggio = (Long)this.sqlManager.getObject("select count(ngara) from garecont where ngara=? and codimp=? and ngaral=?", new Object[]{codgar,ditta, ngara});
          if (conteggio != null && conteggio.longValue()>0) {
            this.geneManager.deleteTabelle(new String[] { "GARECONT" }, "ngara=? and codimp=? and ngaral=?",
                new Object[] {codgar,ditta, ngara });
          } else {
            conteggio = (Long)this.sqlManager.getObject("select count(ngara) from gare where codgar1=? and ditta=? and ngara!=?", new Object[]{codgar,ditta,ngara});
            if (conteggio == null || new Long(0).equals(conteggio))
              this.geneManager.deleteTabelle(new String[] { "GARECONT" }, "ngara=? and codimp=? and ngaral is null",
                  new Object[] {codgar,ditta });
          }
        }
      }

    }catch (SQLException e) {
      throw new GestoreException("Errore nella gestione della cancellazione delle occorrenze in GARECONT",null, e);
    }
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    //variabili per tracciatura eventi, solo per la creazione di una gara a lotto unico
    int livEvento = 1;
    String codEvento = "GA_CREAZIONE_PROCEDURA";
    String oggEvento = "";
    String descrEvento = "Inserimento della gara";
    String errMsgEvento = "";

    String tipoGara = UtilityStruts.getParametroString(this.getRequest(),
        "tipoGara");

    try {
      if(datiForm.isColumn("GARE.CODCIG") && datiForm.getColumn("GARE.CODCIG").getValue().stringValue()!=null
            && !"".equals(datiForm.getColumn("GARE.CODCIG").getValue().stringValue())){

        String msg = pgManager.controlloUnicitaCIG(datiForm.getColumn("GARE.CODCIG").getValue().stringValue(),datiForm.getColumn("GARE.NGARA").getValue().stringValue());
        if(msg!=null){
          String descrizione = (String) this.sqlManager.getObject(
              "select tab1desc from tab1 where tab1cod = ? and tab1tip = ?",
              new Object[] { "A1151","1" });
          if(descrizione.substring(0, 1).equals("0")){
              UtilityStruts.addMessage(this.getRequest(), "warning",
                  "warnings.gare.codiceCIGDuplicato",
                  new Object[] {msg });
            }else{
              throw new GestoreException("Errore durante l'aggiornamento del campo GARE.CODCIG", "gare.codiceCIGDuplicato",new Object[] {msg },  new Exception());
          }
        }
      }
    } catch (SQLException e1) {
        throw new GestoreException("Errore nella lettura della campo tabellato A1151",null,e1);
    }

    try{

      // Flag per indicare se sti inserendo una nuova gara collegandola ad un
      // appalto oppure no
      boolean isInsertConCollegamentoAppalto = datiForm.isColumn("ISGARA_DA_APPALTO");
      String nuovoNGARA = null;
      Long nuovoCODIGA = null;


      //Controllo importo manodopera
      this.controlloImpmano(datiForm);

      try {
        if(datiForm.isColumn("GARE.PRECED") && datiForm.getString("GARE.PRECED") !=null && !"".equals(datiForm.getString("GARE.PRECED")) && geneManager.isCodificaAutomatica("GARE", "PRECED")){
          //Gestione della codifica automatica per i rilanci
          String preced =  datiForm.getString("GARE.PRECED");
          nuovoNGARA = pgManager.getNumeroGaraCodificaAutomatica(preced,null,"GARE","PRECED");
          datiForm.getColumn("GARE.NGARA").setChiave(true);
          datiForm.setValue("GARE.NGARA", nuovoNGARA);
        }else if (geneManager.isCodificaAutomatica("TORN", "CODGAR") && !this.getGeneManager().getProfili().checkProtec(
            (String) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "COLS", "VIS", "GARE.GARE.PRECED") ) {
          HashMap<String,String> hm = null;

          if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
            hm = pgManager.calcolaCodificaAutomatica("GARE", Boolean.TRUE, null, null);
          } else {
            String codiceGara = datiForm.getColumn("GARE.CODGAR1").getValue().getStringValue();
            hm = pgManager.calcolaCodificaAutomatica("GARE", Boolean.FALSE,
            		codiceGara, null);
          }
          nuovoNGARA =  hm.get("numeroGara");

          datiForm.getColumn("GARE.NGARA").setChiave(true);
          datiForm.setValue("GARE.NGARA", nuovoNGARA);
        } else {
          if (this.getGeneManager().getProfili().checkProtec(
              (String) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS", "ALT.GARE.PersonalizzazioneAutovie")) {
              //codifica realizzata per Personalizzazione autovie
            if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
              HashMap<String,String> hm = this.autovieManager.calcolaCodificaAutomatica("GARE", Boolean.TRUE, null);
              nuovoNGARA =  hm.get("numeroGara");
            } else {
              // Aprile 2016 ....si vuole  anche qui la codifica automatica
              /*String numeroGara = datiForm.getString("GARE.NGARA");
              nuovoNGARA =  numeroGara.trim();*/
              String codiceGara = datiForm.getColumn("GARE.CODGAR1").getValue().getStringValue();
              HashMap<String,Object> hm = this.autovieManager.calcolaCodificaAutomatica("LOTTO", Boolean.TRUE, codiceGara);
              nuovoNGARA =  (String)hm.get("numeroGara");
              nuovoCODIGA = (Long)  hm.get("codiga");

            }
            datiForm.getColumn("GARE.NGARA").setChiave(true);
            datiForm.setValue("GARE.NGARA", nuovoNGARA);
            datiForm.setValue("GARE.CODIGA", nuovoCODIGA);
            autovieManager.verificaEsistenzaNumeroGara(nuovoNGARA);
          } else {
            String numeroGaraDestinazione = datiForm.getString("GARE.NGARA");
            numeroGaraDestinazione = numeroGaraDestinazione.trim();
            datiForm.setValue("GARE.NGARA", numeroGaraDestinazione);
            pgManager.verificaPreliminareDatiCopiaGara(null, numeroGaraDestinazione, null,
                numeroGaraDestinazione, false);
          }
        }
      } catch(GestoreException e) {
        if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
          livEvento = 3;
          errMsgEvento = e.getMessage();
        }
        throw e;
      }

      if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico"))
        oggEvento = nuovoNGARA;

      //Controllo che  TORN.NUMAVCP sia numerico
      if (datiForm.isColumn("TORN.NUMAVCP")) {
        String numavcp = datiForm.getString("TORN.NUMAVCP");
        numavcp = UtilityStringhe.convertiNullInStringaVuota(numavcp);
        if (!"".equals(numavcp)) {
          if (!GestoreGARE.isNumeric(numavcp)) {
            if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
              livEvento = 3;
              errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.NGaraAnacNoNumerico");
            }
            throw new GestoreException("Il valore specificato per N.gara ANAC deve essere numerico","NGaraAnacNoNumerico");
          }
        }
      }

      try {
        controlloAccordoQuadro(datiForm,false);
      } catch(GestoreException e){
        if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
          livEvento = 3;
          errMsgEvento=e.getMessage();
        }
        throw e;
      }

      String lottoOffertaUnica = UtilityStruts.getParametroString(this.getRequest(), "LOTTO_OFFERTAUNICA");

      if (datiForm.isColumn("GARE.TEMESI")) {
  	    DataColumn temesi = datiForm.getColumn("GARE.TEMESI");
  	    // in fase di inserimento, il campo TEMESI non è editabile per il caso
  	    // lavori
  	    if (temesi.getValue() == null)
  	      temesi.setValue(new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
  	          new Long(1)));
      }
      try {
        String precut = tabellatiManager.getDescrTabellato("A1018", "1");
        datiForm.addColumn("GARE.PRECUT", new Long(precut));
      } catch (Throwable e) {
        if(tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")){
          livEvento = 3;
          errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.insert.getPrecut");
        }
        throw new GestoreException(
            "Errore nell'estrazione del numero di decimali per il calcolo anomalia",
            "getPrecut", e);
      }

      if (this.getGeneManager().getProfili().checkProtec(
          (String) this.getRequest().getSession().getAttribute(
              CostantiGenerali.PROFILO_ATTIVO), "COLS", "VIS",
          "GARE.GARE.CRITLICG") && this.getGeneManager().getProfili().checkProtec(
              (String) this.getRequest().getSession().getAttribute(
                  CostantiGenerali.PROFILO_ATTIVO), "COLS", "VIS",
              "GARE.GARE.DETLICG") && this.getGeneManager().getProfili().checkProtec(
                  (String) this.getRequest().getSession().getAttribute(
                      CostantiGenerali.PROFILO_ATTIVO), "COLS", "VIS",
                  "GARE.GARE.CALCSOANG")) {
        //Calcolo di MODLICG
        Long modlicg = datiForm.getLong("GARE.MODLICG");
        if (modlicg == null) {
          Long critlicg = datiForm.getLong("GARE.CRITLICG");
          Long detlicg = datiForm.getLong("GARE.DETLICG");
          String calcsoang = datiForm.getString("GARE.CALCSOANG");
          Long applegregg = datiForm.getLong("GARE.APPLEGREGG");
          if (critlicg == null || ("1".equals(critlicg) && detlicg == null && (calcsoang == null || "".equals(calcsoang)))) {
            if(tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")){
              livEvento = 3;
              errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.criterioAggiudicazionegNoDati");
            }
            throw new GestoreException("I dati per determinare il criterio di aggiudicazione non sono completi","criterioAggiudicazionegNoDati");
          }
          modlicg = pgManager.getMODLICG(critlicg, detlicg, calcsoang, applegregg);
          datiForm.setValue("GARE.MODLICG", modlicg);
        }
      }


      if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
        Long iterga = datiForm.getLong("TORN.ITERGA");
        if (iterga==null) {
          Long tipgarg = datiForm.getLong("GARE.TIPGARG");
          if (tipgarg==null) {
            if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
              livEvento = 3;
              errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.noTipoProcedura");
            }
            throw new GestoreException("Non è stato definito il tipo procedura","noTipoProcedura");
          }

          try {
            iterga = pgManager.getITERGA(tipgarg);
            datiForm.setValue("TORN.ITERGA", iterga);
          } catch (SQLException e) {
            livEvento = 3;
            errMsgEvento = "Errore nel calcolo di TORN.ITERGA";
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
              if(tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")){
                livEvento = 3;
                errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.noTipoProceduraTelematica");
              }
              throw new GestoreException("Il tipo procedura non è disponibile nella modalità telematica", "noTipoProceduraTelematica");
            }
          }
        }
      }

      // Gestione del codice CIG fittizio
      if (datiForm.isColumn("ESENTE_CIG")) {
    	  String esenteCig = datiForm.getString("ESENTE_CIG");
    	  if ("1".equals(esenteCig)) {
    		  int nextId = this.genChiaviManager.getNextId("GARE.CODCIG");
    		  String codCigFittizio = "#".concat(StringUtils.leftPad(""+nextId, 9, "0"));
    		  datiForm.setValue("GARE.CODCIG", codCigFittizio);
    	  }
      }

      // se è presente codgar1 allora si sta definendo un nuovo lotto di gara,
      // mentre se non è valorizzato si tratta di nuova gara a lotto unico
      DataColumn codgar1 = datiForm.getColumn("GARE.CODGAR1");

      //Controlli specifici dei lotti di gara, sia offerta unica che distinte
      if (codgar1.getValue().stringValue() != null && !"".equals(codgar1.getValue().stringValue())) {

        //Controllo che il campo CODIGA abbia un valore numerico e univoco su tutti i lotti della gara
        //Il controllo scatta solo nella pagina 'Dati generali'
        if (datiForm.isColumn("GARE.CODIGA"))
          controlloCodiga(datiForm, lottoOffertaUnica, codgar1);
      } else {
        if (lottoOffertaUnica == null) {
          //Inserimento documento "Offerta economica"
          Long offtel = null;
          if (datiForm.isColumn("TORN.OFFTEL"))
            offtel = datiForm.getLong("TORN.OFFTEL");
          if (new Long(1).equals(offtel)) {
            String ngara = datiForm.getString("GARE.NGARA");
            try {
              this.sqlManager.update("insert into documgara(codgar, ngara, norddocg, busta, gruppo, tipodoc, descrizione, obbligatorio, modfirma, valenza, gentel, numord) " +
                      "values(?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{"$"+ngara, ngara, new Long(1), new Long(3), new Long(3), new Long(1), "Offerta economica", "1", new Long(1), new Long(0), "1", new Long(1) });
            } catch (SQLException e) {
              if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
                livEvento = 3;
                errMsgEvento = "Errore nell'inserimento del documento 'Offerta economica'";
              }
              throw new GestoreException("Errore nell'inserimento del documento 'Offerta economica'", null, e);
            }
          }
        }
      }

      if ( (tipoGara == null || !"garaLottoUnico".equalsIgnoreCase(tipoGara)) && datiForm.isColumn("GARE.MODLICG") && datiForm.getLong("GARE.MODLICG")!=null && datiForm.getLong("GARE.MODLICG") == 17){
        if (!this.confrontoOffaumGaraTornata(datiForm))
          throw new GestoreException("Non è possibile modificare il criterio di aggiudicazione 'Prezzo più alto' perchè la gara non ammette offerte in aumento", "modificaLottoPrezzoPiuAlto");
      }

      if (!this.confrontoCritlicGaraTornata(datiForm, lottoOffertaUnica))
        throw new GestoreException("Il criterio di aggiudicazione del lotto non può essere Offerta economicamente più vantaggiosa","critlicgNoOEPV");

      if (!this.confrontoModlicGaraTornata(datiForm, lottoOffertaUnica))
        throw new GestoreException("La modalità di aggiudicazione del lotto non può essere Offerta a prezzi unitari","modlicgNoOFFPREZUNI");

      if (datiForm.isColumn("GARE.CODCIG") && datiForm.isModifiedColumn("GARE.CODCIG") && datiForm.getColumn("GARE.CODCIG").getValue().stringValue() != null
          && !"".equals(datiForm.getColumn("GARE.CODCIG").getValue().stringValue()) && datiForm.getColumn("GARE.CODCIG").getValue().stringValue().length()!= 10) {
        if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
          livEvento = 3;
          errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.controlloCodiceCIG");
        }
        throw new GestoreException("Il codice CIG specificato non è valido","controlloCodiceCIG");
      }

      if (datiForm.isColumn("TORN.CODCIGAQ") && datiForm.isModifiedColumn("TORN.CODCIGAQ") && datiForm.getColumn("TORN.CODCIGAQ").getValue().stringValue() != null
          && !"".equals(datiForm.getColumn("TORN.CODCIGAQ").getValue().stringValue()) && datiForm.getColumn("TORN.CODCIGAQ").getValue().stringValue().length() != 10) {
        if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
          livEvento = 3;
          errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.controlloCodiceCIG");
        }
        throw new GestoreException("Il codice CIG specificato non è valido","controlloCodiceCIG");
      }

      if (!this.confrontoValtecLottoTornata(datiForm, lottoOffertaUnica))
        throw new GestoreException("Non è possibile impostare la valutazione requisiti minimi mediante busta tecnica","controlloValtecLottiGara");

      if("SI".equalsIgnoreCase(lottoOffertaUnica) && datiForm.isColumn("GARE.CUPPRG") && datiForm.isModifiedColumn("GARE.CUPPRG") && datiForm.getColumn("GARE.CUPPRG").getValue().stringValue()!=null
          && !"".equals(datiForm.getColumn("GARE.CUPPRG").getValue().stringValue()) && datiForm.getColumn("GARE.CUPPRG").getValue().stringValue().length()!= 15)
        throw new GestoreException("Il codice CUP specificato non è valido","controlloCodiceCUP");

      if("SI".equalsIgnoreCase(lottoOffertaUnica) && datiForm.isColumn("GARE1.CODCUI") && datiForm.isModifiedColumn("GARE1.CODCUI") && datiForm.getColumn("GARE1.CODCUI").getValue().stringValue()!=null
          && !"".equals(datiForm.getColumn("GARE1.CODCUI").getValue().stringValue()) && (datiForm.getColumn("GARE1.CODCUI").getValue().stringValue().length()< 20 || datiForm.getColumn("GARE1.CODCUI").getValue().stringValue().length() >22))
        throw new GestoreException("Il codice CUI specificato non è valido","controlloCodiceCUI");

      // Gestione della sezione 'Categoria prevalente'
      GestoreCATG.gestisciEntitaDaGare(this.getRequest(), status, datiForm,
          datiForm.getColumn("GARE.NGARA"));
      // Gestione delle sezioni 'Ulteriori categorie'
      GestoreOPESMultiplo.gestisciEntitaDaGare(this.getRequest(), status,
          datiForm, datiForm.getColumn("GARE.NGARA"));

      if (lottoOffertaUnica == null) {

        try {
      	// inserimento dei codici CPV a partire dall'ultimo lotto di gara
  	    if (!isInsertConCollegamentoAppalto && codgar1.getValue().stringValue() != null) {
  	      this.insertDatiCPVUltimoLotto(codgar1.getValue().stringValue(),
    	          datiForm.getColumn("GARE.NGARA").getValue().stringValue(), status);
  	    }

  	    this.setContributiDaImportoBaseAsta(datiForm, codgar1.getValue().stringValue());

  	    if (codgar1.getValue().stringValue() == null) {
  	      // ...altrimenti devo inserirlo; in tal caso
  	      // si inizializza, essendo una gara a lotto unico, il codgar di torn
  	      codgar1.setValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, "$"
  	          + datiForm.getColumn("GARE.NGARA").getValue().stringValue()));
  	      datiForm.addColumn("TORN.CODGAR", JdbcParametro.TIPO_TESTO,
  	          codgar1.getValue());
  	      datiForm.getColumn("TORN.CODGAR").setChiave(true);
  	      datiForm.getColumn("TORN.CODGAR").setObjectOriginalValue(
  	          datiForm.getColumn("TORN.CODGAR").getValue());

  	      this.inserisci(status, datiForm, new GestoreTORN());
  	      // l'aggiornamento degli 'Atti autorizzativi' e dei permessi è demandata
  	      // al gestore di TORN
  	    }

  	    // Inizializzazione della percentuale della cauzione provvisoria
  	    this.initPGAROF(datiForm);

  	    // Calcolo della cauzione provvisoria
  	    this.calcolaGAROFF(datiForm);

        } catch(GestoreException e) {
          if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
            livEvento = 3;
            errMsgEvento = e.getMessage();
          }
          throw e;
        }

  	    if (isInsertConCollegamentoAppalto) {
  	      String codiceAppalto = datiForm.getString("GARE.CLAVOR");
  	      Long numeroAppalto = datiForm.getLong("GARE.NUMERA");
  	      try {
  	        // Se si sta inserendo una nuova gara dopo averla collegata ad un appalto
  	        // si va a valorizzare campi della scheda 'Altri dati'
  	        Vector<?> datiAppa = sqlManager.getVector(
  	            "select ANSS, ANOMSS, PROLAV, COMLAV, ALTCAT, LOCINT" +
  	            "  from APPA where CODLAV = ? and NAPPAL = ? ",
  	            new Object[]{codiceAppalto, numeroAppalto});
  	        if (datiAppa != null && datiAppa.size() > 0) {
  	          if (((JdbcParametro)datiAppa.get(0)).getValue() != null)
  	            datiForm.addColumn("GARE.NUMSSL", ((JdbcParametro)datiAppa.get(0)).getValue());
  	          if (((JdbcParametro)datiAppa.get(1)).getValue() != null)
  	            datiForm.addColumn("GARE.NOMSSL", ((JdbcParametro)datiAppa.get(1)).getValue());
  	          if (((JdbcParametro)datiAppa.get(2)).getValue() != null)
  	            datiForm.addColumn("GARE.PROSLA", ((JdbcParametro)datiAppa.get(2)).getValue());
  	          if (((JdbcParametro)datiAppa.get(3)).getValue() != null)
  	            datiForm.addColumn("GARE.LOCLAV", ((JdbcParametro)datiAppa.get(3)).getValue());
  	          if (((JdbcParametro)datiAppa.get(4)).getValue() != null){
  	            sqlManager.update(
  	                "insert into GARCPV (NGARA, NUMCPV, CODCPV, TIPCPV ) values  (?, ? , ?, ?)",
  	                new Object[]{datiForm.getString("GARE.NGARA"), new Long(1),
  	                    ((JdbcParametro)datiAppa.get(4)).getValue(), "1"});
  	          }
  	          if (((JdbcParametro)datiAppa.get(5)).getValue() != null)
  	                datiForm.addColumn("GARE.LOCINT", ((JdbcParametro)datiAppa.get(5)).getValue());
  	        }
  	        Vector<?> datiPeri = sqlManager.getVector(
  	            "select CUPPRG, CUPMST, CUIINT from PERI where CODLAV = ?",
  	             new Object[]{codiceAppalto});
  	        if (datiPeri != null && datiPeri.size() > 0) {
                if (((JdbcParametro)datiPeri.get(0)).getValue() != null)
                  datiForm.addColumn("GARE.CUPPRG", ((JdbcParametro)datiPeri.get(0)).getValue());
                if (((JdbcParametro)datiPeri.get(2)).getValue() != null){
                  String codcui = SqlManager.getValueFromVectorParam(datiPeri, 2).getStringValue();
                  if(codcui!=null && codcui.length()>22){codcui=codcui.trim().substring(0, 22);}
                  datiForm.addColumn("GARE1.CODCUI",codcui);}
                //if (((JdbcParametro)datiPeri.get(1)).getValue() != null)
                //  datiForm.addColumn("GARE.CUPMST", ((JdbcParametro)datiPeri.get(1)).getValue());
  	        }
  	      } catch (SQLException e) {
  	        if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
  	          livEvento = 3;
  	          errMsgEvento = "Errore nell'inizializzazione dei dati " +
  	                "della gara associata all'appalto (" + codiceAppalto + " - " +
  	                numeroAppalto;
  	        }
  	        throw new GestoreException("Errore nell'inizializzazione dei dati " +
  	            "della gara associata all'appalto (" + codiceAppalto + " - " +
  	            numeroAppalto, null, e);
  	      }
  	      //Aggiornamento dell'appalto associato
  	      try {
            this.sqlManager.update("update appa set faseappalto=? where codlav=? and nappal=?", new Object[]{"A",codiceAppalto,numeroAppalto});
          } catch (SQLException e) {
            if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
              livEvento = 3;
              errMsgEvento = "Errore nell'inizializzazione dei dati " +
                    "dell'appalto associato alla gara (" + codiceAppalto + " - " +
                    numeroAppalto;
            }
            throw new GestoreException("Errore nell'inizializzazione dei dati " +
                "dell'appalto associato alla gara (" + codiceAppalto + " - " +
                numeroAppalto, null, e);
          }
  	    }

  	    //Nel caso di personalizzazione SMAT, se la rda arriva dalla integrazione
  	    if (this.getGeneManager().getProfili().checkProtec((String) this.getRequest().getSession().getAttribute(
  	        CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS", "ALT.GARE.inserimentoRdaSMAT")){
  	      if ( datiForm.isColumn("GARE.NATTOG") && datiForm.isModifiedColumn("GARE.NATTOG")
  	            && datiForm.getColumn("GARE.NATTOG").getValue().stringValue()!=null
  	            && !"".equals(datiForm.getColumn("GARE.NATTOG").getValue().stringValue())) {

  	          String codgar = datiForm.getString("TORN.CODGAR");
  	          String ngara = datiForm.getString("GARE.NGARA");
  	          String numeroRda = datiForm.getString("GARE.NATTOG");
  	          numeroRda = UtilityStringhe.convertiNullInStringaVuota(numeroRda);

  	          if (!"".equals(numeroRda)) {
  	            int res=0;
  	            try {
  	              res = smatManager.insLavorazioniLotto(codgar, ngara, numeroRda);
  	            } catch(GestoreException e) {
  	              if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
  	                livEvento = 3;
  	                errMsgEvento = e.getMessage();
  	              }
  	              throw e;
  	            }

  	            if (res <0) {
  	              if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
  	                livEvento = 3;
  	                errMsgEvento = "Errore nell'inizializzazione delle lavorazioni " +
  	                    " della gara  (" + ngara +")";
  	              }
  	              throw new GestoreException("Errore nell'inizializzazione delle lavorazioni " +
  	                " della gara  (" + ngara +")", null);
  	            }

  	            try {
  	              res = smatManager.updRdaAnnullabile("GARE",numeroRda,ngara);
  	            } catch(GestoreException e) {
  	              if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
                      livEvento = 3;
                      errMsgEvento = e.getMessage();
                    }
                    throw e;
  	            }
  	            if (res <0) {
  	              if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
                      livEvento = 3;
                      errMsgEvento = "Errore nell'annullamento " +
                          " della RdA  (" + numeroRda +")";
                    }
  	              throw new GestoreException("Errore nell'annullamento " +
  	                " della RdA  (" + numeroRda +")", null);
  	            }
  	          }
  	      }
  	    }//fine personalizzazione SMAT

      } else insertDitteGara(datiForm);

      // Allineamento IMPAPPG nelle ditte della gara
      try {
        this.updateDitteGara(datiForm);
      } catch(GestoreException e) {
        if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
          livEvento = 3;
          errMsgEvento = e.getMessage();
        }
        throw e;
      }

      //Aggiornamento dell'importo totale della tornata tranne nel caso
      //di gara a lotto unico
      this.AggiornaImportoTotaleTorn(datiForm,"MOD");


      //Creazione dell'occorrenza nella tabella GARE1, estensione di GARE
      try {
        this.sqlManager.update("insert into GARE1 (ngara,codgar1) values(?,?)",
            new Object[]{datiForm.getString("GARE.NGARA"), datiForm.getString("GARE.CODGAR1")});
      } catch (SQLException e) {
        if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
          livEvento = 3;
          errMsgEvento = "Errore nella creazione dell'occorenza in GARE1";
        }
        throw new GestoreException("Errore nella creazione dell'occorenza in GARE1", null, e);
      }

      //Aggiornamento GARE1
      if (datiForm.isModifiedTable("GARE1")) {
        AbstractGestoreEntita gestoreGARE1 = new DefaultGestoreEntita("GARE1", this.getRequest());
        datiForm.setValue("GARE1.NGARA", datiForm.getString("GARE.NGARA"));
        datiForm.getColumn("GARE1.NGARA").setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, datiForm.getString("GARE.NGARA")));
        if (datiForm.isColumn("GARE1.ULTDETLIC"))
          datiForm.getColumn("GARE1.ULTDETLIC").setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(-1)));
        if (datiForm.isColumn("GARE1.AQOPER"))
          datiForm.getColumn("GARE1.AQOPER").setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(-1)));
        if (datiForm.isColumn("GARE1.AQNUMOPE"))
          datiForm.getColumn("GARE1.AQNUMOPE").setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(-1)));
        if (datiForm.isColumn("GARE1.AMMRIN"))
          datiForm.getColumn("GARE1.AMMRIN").setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, ""));
        if (datiForm.isColumn("GARE1.AMMOPZ"))
          datiForm.getColumn("GARE1.AMMOPZ").setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, ""));
        if (datiForm.isColumn("GARE1.IMPRIN"))
          datiForm.getColumn("GARE1.IMPRIN").setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_DECIMALE, null));
        if (datiForm.isColumn("GARE1.IMPALTRO"))
          datiForm.getColumn("GARE1.IMPALTRO").setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_DECIMALE, null));
        if (datiForm.isColumn("GARE1.CODCUI"))
          datiForm.getColumn("GARE1.CODCUI").setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, null));


        try {
          gestoreGARE1.update(status, datiForm);
        } catch(GestoreException e) {
          if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
            livEvento = 3;
            errMsgEvento = e.getMessage();
          }
          throw e;
        }
      }

      if (lottoOffertaUnica == null) {
        //Creazione dell'occorrenza nella tabella GARECONT, estensione di GARE
        try {
          this.sqlManager.update("insert into GARECONT (ngara,ncont) values(?,?)",
              new Object[]{datiForm.getString("GARE.NGARA"), new Long(1)});
        } catch (SQLException e) {
          if(tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")){
            livEvento = 3;
            errMsgEvento = "Errore nella creazione dell'occorenza in GARECONT";
          }
          throw new GestoreException("Errore nella creazione dell'occorenza in GARECONT", null, e);
        }
      }
      //Gestione lotti di gare ad offerte distinte
      String lottoOffertaDistinte = UtilityStruts.getParametroString(this.getRequest(), "lottoOfferteDistinte");
      if (("1".equals(lottoOffertaDistinte) || "SI".equalsIgnoreCase(lottoOffertaUnica)) && datiForm.isColumn("GARE.CLAVOR")) {
        HttpSession session = this.getRequest().getSession();
        String uffint = (String) session.getAttribute("uffint");
        if (uffint!=null && !"".equals(uffint)) {
          try {
            Long altrisog = (Long)this.sqlManager.getObject("select altrisog from torn where codgar=?", new Object[]{datiForm.getString("GARE.CODGAR1")});

            if (altrisog == null) {
              Vector<?> datiPeriUffint = sqlManager.getVector("select NOMEIN,CENINT from UFFINT, PERI where "
                  + "UFFINT.CODEIN = PERI.CENINT and PERI.CODLAV = ? ", new Object[]{datiForm.getString("GARE.CLAVOR")});
              if (datiPeriUffint != null && datiPeriUffint.size() > 0) {
                String iscuc = (String)this.sqlManager.getObject("select ISCUC from UFFINT where UFFINT.CODEIN=?", new Object[]{uffint});
                //String nomein = SqlManager.getValueFromVectorParam(datiPeriUffint, 0).getStringValue();
                String cenint = SqlManager.getValueFromVectorParam(datiPeriUffint, 1).getStringValue();
                if (cenint == null)
                  cenint="";
                if (!cenint.equals(uffint) && "1".equals(iscuc)) {
                  String codiceTecnico = (String)sqlManager.getObject(
                      "select CODTEC from G2TECN " +
                       "where G2TECN.CODLAV = ? " +
                         "and DTETEC is null and INCTEC=1 order by NUMTEC desc",
                      new Object[]{datiForm.getString("GARE.CLAVOR")});

                  this.sqlManager.update("update torn set altrisog=? where codgar=?", new Object[]{"2", datiForm.getString("GARE.CODGAR1")});
                  Long id = new Long(this.genChiaviManager.getNextId("GARALTSOG"));
                  this.sqlManager.update("insert into garaltsog(id,ngara,cenint,codrup) values(?,?,?,?)", new Object[]{id, datiForm.getString("GARE.NGARA"), cenint,codiceTecnico});
                } else if(cenint.equals(uffint) && "1".equals(iscuc)) {
                  this.sqlManager.update("update torn set altrisog=? where codgar=?", new Object[]{"1", datiForm.getString("GARE.CODGAR1")});
                }
              }
            }
          } catch (SQLException e) {
            throw new GestoreException("Errore nella gestione di TORN.ALTRISOG per la gara " + datiForm.getString("GARE.CODGAR1"), null, e);
          }
        }
      }

      if (datiForm.isColumn("GARE.CLAVOR") ) {
        Long esineg = null;
        if(datiForm.isColumn("GARE.ESINEG"))
          esineg = datiForm.getLong("GARE.ESINEG");
        else{
          //Nel caso di lotto di gara per gara a plico unico non è presente il campo ESINEG nella scheda, si deve prelevare il
          //valore del campo ESINEG della gara fittizia
          try {
            esineg = (Long)this.sqlManager.getObject("select esineg from gare where ngara=?", new Object[]{datiForm.getString("GARE.CODGAR1")});
          } catch (SQLException e) {
            throw new GestoreException("Errore nella lettura del campo GARE.ESINEG", null, e);
          }
        }

        if (esineg == null) {
          String clavor = datiForm.getString("GARE.CLAVOR");
          Long numera = datiForm.getLong("GARE.NUMERA");
          if(clavor!=null && !"".equals(clavor) && numera!=null){
            try {
              this.sqlManager.update("update gare set clavor = null, numera= null where ngara!=? and clavor=? and numera=?",
                  new Object[]{datiForm.getString("GARE.NGARA"),clavor,numera});
            } catch (SQLException e) {
              if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
                livEvento = 3;
                errMsgEvento = "Errore nello sbiancamento dei riferimenti al lavoro nelle gare con clavor= " + clavor +
                    ", numera= " + numera.toString() + " della gara annullata";
              }
              throw new GestoreException("Errore nello sbiancamento dei riferimenti al lavoro nelle gare con clavor= " + clavor +
                  ", numera= " + numera.toString() + " della gara annullata", null, e);
            }
          }
        }
      }
      if(isInsertConCollegamentoAppalto && "SI".equalsIgnoreCase(lottoOffertaUnica)){
        String codcpv = datiForm.getString("GARCPV.CODCPV");
        if(codcpv!=null && !"".equals(codcpv)) try {
          this.sqlManager.update(
              "insert into GARCPV (NGARA, NUMCPV, CODCPV, TIPCPV ) values  (?, ? , ?, ?)",
              new Object[]{datiForm.getString("GARE.NGARA"), new Long(1), codcpv, "1"});
        } catch (SQLException e) {
          throw new GestoreException("Errore nell'inserimento in GARCPV", null, e);
        }
        // Aggiornamento delle occorrenze complementari
        AbstractGestoreChiaveNumerica gestoreGARCPV = new DefaultGestoreEntitaChiaveNumerica(
            "GARCPV", "NUMCPV", new String[] { "NGARA" }, this.getRequest());
        this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
            gestoreGARCPV, "CPVCOMP",
            new DataColumn[] { datiForm.getColumn("GARE.NGARA") }, null);
        // Gestione delle sezioni 'Ordini di acquisto'
        AbstractGestoreChiaveIDAutoincrementante gestoreMultiploGARERDA = new DefaultGestoreEntitaChiaveIDAutoincrementante(
            "GARERDA", "ID", this.getRequest());
        this.gestisciAggiornamentiRecordSchedaMultiplaRda(status, datiForm,
            gestoreMultiploGARERDA, "GARERDA",
            new DataColumn[] {datiForm.getColumn("GARE.CODGAR1"),datiForm.getColumn("GARE.NGARA")}, null,null);

      }else if(!isInsertConCollegamentoAppalto && "SI".equalsIgnoreCase(lottoOffertaUnica)){
        // Gestione CPV
        datiForm.setValue("GARCPV.NGARA", datiForm.getString("GARE.NGARA"));
        this.updateGARCPV(status, datiForm);
      }

      //Gestione entita GARALTSOG
      try {
        pgManagerEst1.gestioneGaraltSog(datiForm);
      } catch(GestoreException e){
        if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
          livEvento = 3;
          errMsgEvento = e.getMessage();
        }
        throw e;
      }

      //Integrazione con WSERP
      String urlWSERP = ConfigManager.getValore("wserp.erp.url");
      if(urlWSERP != null && !"".equals(urlWSERP)){
        gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
            this.getServletContext(), GestioneWSERPManager.class);
          WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
          if(configurazione.isEsito()){
            String tipoWSERP = configurazione.getRemotewserp();
            if("FNM".equals(tipoWSERP)){
              if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
                String codcpv = this.getRequest().getParameter("codCPV");
                codcpv = UtilityStringhe.convertiNullInStringaVuota(codcpv);
                if(!"".equals(codcpv)){
                  try {
                    sqlManager.update(
                        "insert into GARCPV (NGARA, NUMCPV, CODCPV, TIPCPV ) values  (?, ? , ?, ?)",
                        new Object[]{datiForm.getString("GARE.NGARA"), new Long(1), codcpv, "1"});
                  } catch (SQLException sqle) {
                    throw new GestoreException("Errore nell'inizializzazione del codice CPV",null,sqle);
                  }
                }
              }
            }
          }
      }//integrazione WSERP

      if(datiForm.isColumn("GARE.PRECED") && datiForm.getString("GARE.PRECED") !=null && !"".equals(datiForm.getString("GARE.PRECED"))){
        String preced = datiForm.getString("GARE.PRECED");
        String selectFascicolo = "select entita, codice, anno, numero, classifica, codaoo, coduff, struttura, isriserva, desclassi, desvoce, desaoo, desuff from wsfascicolo where key1 =?";
        try {
          String chiave = preced;
          Long genere = null;
          Vector<?> datiGara = this.sqlManager.getVector("select genere,codgar from v_gare_genere where codice =?", new Object[]{ preced});
          if(datiGara!=null && datiGara.size()>0){
            genere = SqlManager.getValueFromVectorParam(datiGara, 0).longValue();
            if(new Long(300).equals(genere))
              chiave = SqlManager.getValueFromVectorParam(datiGara, 1).getStringValue();
          }
          Vector<?> datiFascicolo = this.sqlManager.getVector(selectFascicolo, new Object[]{chiave});
          if(datiFascicolo!=null && datiFascicolo.size()>0){
            String insert ="insert into wsfascicolo(id,entita, key1, codice, anno, numero, classifica, codaoo, coduff, struttura, isriserva, desclassi, desvoce, desaoo, desuff) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            Object[] par = new Object[15];
            par[0] = new Long(this.genChiaviManager.getNextId("WSFASCICOLO"));
            par[1] = ("".equals(SqlManager.getValueFromVectorParam(datiFascicolo, 0).getStringValue())) ? null : SqlManager.getValueFromVectorParam(datiFascicolo, 0).getStringValue();
            par[2] = datiForm.getString("GARE.NGARA");
            par[3] = ("".equals(SqlManager.getValueFromVectorParam(datiFascicolo, 1).getStringValue())) ? null : SqlManager.getValueFromVectorParam(datiFascicolo, 1).getStringValue();
            par[4] = ("".equals(SqlManager.getValueFromVectorParam(datiFascicolo, 2).longValue())) ? null : SqlManager.getValueFromVectorParam(datiFascicolo, 2).longValue();
            par[5] = ("".equals(SqlManager.getValueFromVectorParam(datiFascicolo, 3).getStringValue())) ? null : SqlManager.getValueFromVectorParam(datiFascicolo, 3).getStringValue();
            par[6] = ("".equals(SqlManager.getValueFromVectorParam(datiFascicolo, 4).getStringValue())) ? null : SqlManager.getValueFromVectorParam(datiFascicolo, 4).getStringValue();
            par[7] = ("".equals(SqlManager.getValueFromVectorParam(datiFascicolo, 5).getStringValue())) ? null : SqlManager.getValueFromVectorParam(datiFascicolo, 5).getStringValue();
            par[8] = ("".equals(SqlManager.getValueFromVectorParam(datiFascicolo, 6).getStringValue())) ? null : SqlManager.getValueFromVectorParam(datiFascicolo, 6).getStringValue();
            par[9] = ("".equals(SqlManager.getValueFromVectorParam(datiFascicolo, 7).getStringValue())) ? null : SqlManager.getValueFromVectorParam(datiFascicolo, 7).getStringValue();
            par[10] = ("".equals(SqlManager.getValueFromVectorParam(datiFascicolo, 8).getStringValue())) ? null : SqlManager.getValueFromVectorParam(datiFascicolo, 8).getStringValue();
            par[11] = ("".equals(SqlManager.getValueFromVectorParam(datiFascicolo, 9).getStringValue())) ? null : SqlManager.getValueFromVectorParam(datiFascicolo, 9).getStringValue();
            par[12] = ("".equals(SqlManager.getValueFromVectorParam(datiFascicolo, 10).getStringValue())) ? null : SqlManager.getValueFromVectorParam(datiFascicolo, 10).getStringValue();
            par[13] = ("".equals(SqlManager.getValueFromVectorParam(datiFascicolo, 11).getStringValue())) ? null : SqlManager.getValueFromVectorParam(datiFascicolo, 11).getStringValue();
            par[14] = ("".equals(SqlManager.getValueFromVectorParam(datiFascicolo, 12).getStringValue())) ? null : SqlManager.getValueFromVectorParam(datiFascicolo, 12).getStringValue();
            this.sqlManager.update(insert, par);

          }
        } catch (SQLException e) {
          throw new GestoreException("Errore nella copia delle informazioni del fascicolo della gara " + preced,null,e);
        }

      }

    } finally {
      //Tracciatura eventi
      try {
        if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico")) {
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
   * Imposta il contributo ditte della gara, ed eventualmente il contributo
   * stazione appaltante se gara a lotto unico
   *
   * @param datiForm
   * @param codgar1
   *        codice della gara
   * @throws GestoreException
   */
  private void setContributiDaImportoBaseAsta(DataColumnContainer datiForm,
      String codgar1) throws GestoreException {

    boolean garaLottoUnico = false;
    if (codgar1 == null || (codgar1.startsWith("$"))) garaLottoUnico = true;

    // nel caso di aggiornamento dell'importo a base di gara, aggiorno alcuni importi
    if (datiForm.isModifiedColumn("GARE.IMPAPP")) {

      Double impapp= datiForm.getColumn("GARE.IMPAPP").getValue().doubleValue();
      Double imprin= datiForm.getColumn("GARE1.IMPRIN").getValue().doubleValue();
      Double impserv= datiForm.getColumn("GARE1.IMPSERV").getValue().doubleValue();
      Double imppror= datiForm.getColumn("GARE1.IMPPROR").getValue().doubleValue();
      Double impaltro= datiForm.getColumn("GARE1.IMPALTRO").getValue().doubleValue();
      Double maxVal = pgManagerEst1.calcoloValoreMassimoStimato(impapp, imprin, impserv, imppror, impaltro);

      if (garaLottoUnico) {
        // solo per la gara a lotto unico si aggiorna ISTAUT
        Double importoContributo = this.pgManager.getContributoAutoritaStAppaltante(maxVal, "A1z02");
        datiForm.addColumn("TORN.ISTAUT", JdbcParametro.TIPO_DECIMALE,
            importoContributo);
        if (importoContributo == null) {
          // questo set serve a permettere l'annullamento del dato nel campo
          // corrispondente, in quanto si aggiunge una colonna, il cui valore se
          // è null non viene usato per insert/update
          datiForm.getColumn("TORN.ISTAUT").setOriginalValue(
              new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Double(1)));
        }
      }

      Double importoDitta = this.pgManager.getContributoAutoritaStAppaltante(maxVal, "A1z01");
      datiForm.addColumn("GARE.IDIAUT", JdbcParametro.TIPO_DECIMALE,
          importoDitta);
      if (importoDitta == null) {
        // questo set serve a permettere l'annullamento del dato nel campo
        // corrispondente, in quanto si aggiunge una colonna, il cui valore se è
        // null non viene usato per insert/update
        datiForm.getColumn("GARE.IDIAUT").setOriginalValue(
            new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Double(1)));
      }

    }
  }

  /**
   * Inserisce, se esistono, le righe dei dati CPV
   *
   * @param codgar1
   *        codice della gara
   * @param ngara
   *        codice del nuovo lotto di gara
   * @param status
   *        status della transazione
   * @throws GestoreException
   */
  private void insertDatiCPVUltimoLotto(String codgar1, String ngara,
      TransactionStatus status) throws GestoreException {
    try {
      String sqlUltimoLotto = "select NGARA "
          + "from GARE "
          + "where CODGAR1 = ? "
          + "order by NGARA desc";
      // mi interessa estrarre solo la prima riga, ovvero l'ultimo lotto
      // definito
      String ultimoLotto = (String) sqlManager.getObject(sqlUltimoLotto,
          new Object[] { codgar1 });

      if (ultimoLotto != null) {
        List<?> listaNumCPV = sqlManager.getListVector(
            "select NUMCPV from GARCPV where NGARA = ?",
            new Object[] { ultimoLotto });
        if (listaNumCPV != null) {
          DataColumn campoNgara = null;
          for (int i = 0; i < listaNumCPV.size(); i++) {
            // si estrae una riga alla volta, si modifica il campo chiave, e poi
            // si inserisce il record
            DataColumnContainer dccGARCPV = new DataColumnContainer(
                sqlManager,
                "GARCPV",
                "select * from GARCPV where NGARA = ? and NUMCPV = ?",
                new Object[] {
                    ultimoLotto,
                    SqlManager.getValueFromVectorParam(listaNumCPV.get(i), 0).longValue() });
            campoNgara = dccGARCPV.getColumn("GARCPV.NGARA");
            campoNgara.setValue(new JdbcParametro(JdbcParametro.TIPO_TESTO,
                ngara));
            this.inserisci(status, dccGARCPV,
                new DefaultGestoreEntitaChiaveNumerica("GARCPV", "NUMCPV",
                    new String[] { "NGARA" }, this.getRequest()));
          }
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'estrazione dei dati CPV dell'ultimo lotto e inserimento per il presente lotto di gara",
          "insertDatiCPVUltimoLotto", e);
    }
  }

  /**
   * Determina e valorizza la percentuale della cauzione provvisoria
   *
   * @param datiForm
   * @throws GestoreException
   */
  /*
  private void initPGAROF(DataColumnContainer datiForm) throws GestoreException {
    int tipgen = datiForm.getColumn("TORN.TIPGEN").getValue().longValue().intValue();
    String tabellato = PgManager.getTabellatoPercCauzioneProvvisoria(tipgen);
    String sql = "select tab1desc from tab1 where tab1cod = ? and tab1tip = 1";
    try {
      String descrPercentuale = (String) sqlManager.getObject(sql,
          new Object[] { tabellato });
      Double percentuale = UtilityNumeri.convertiDouble(descrPercentuale,
          UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE);
      datiForm.getColumn("GARE.PGAROF").setValue(
          new JdbcParametro(JdbcParametro.TIPO_DECIMALE, percentuale));
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'individuazione della percentuale della cauzione provvisoria",
          "getPercCauzProvv", e);
    }
  }
  */
  private void initPGAROF(DataColumnContainer datiForm) throws GestoreException {
    Long tipgen = datiForm.getColumn("TORN.TIPGEN").getValue().longValue();
    try {
      Double percentuale = pgManager.initPGAROF(tipgen);
      datiForm.getColumn("GARE.PGAROF").setValue(
          new JdbcParametro(JdbcParametro.TIPO_DECIMALE, percentuale));
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'individuazione della percentuale della cauzione provvisoria",
          "getPercCauzProvv", e);
    }
  }


  /**
   * Esegue il calcolo della cauzione provvisoria in base alla percentuale,
   * all'importo base di gara, ed al tipo di arrotondamento previsto
   *
   * @param datiForm
   * @throws GestoreException
   */
  private void calcolaGAROFF(DataColumnContainer datiForm)
      throws GestoreException {
    if (datiForm.isColumn("GARE.IMPAPP")
        && (datiForm.isModifiedColumn("GARE.IMPAPP") || !datiForm.isColumn("GARE.GAROFF"))) {
      /*
      double importoAppalto = 0;
      if (datiForm.getColumn("GARE.IMPAPP").getValue().doubleValue() != null)
        importoAppalto = datiForm.getColumn("GARE.IMPAPP").getValue().doubleValue().doubleValue();

      double percentuale = 0;
      if (datiForm.getColumn("GARE.PGAROF").getValue().doubleValue() != null)
        percentuale = datiForm.getColumn("GARE.PGAROF").getValue().doubleValue().doubleValue();

      int tipgen = datiForm.getColumn("TORN.TIPGEN").getValue().longValue().intValue();
      int numeroDecimali = this.pgManager.getArrotondamentoCauzioneProvvisoria(tipgen);
       */
      Double importoCauzioneProvvisoria = pgManager.calcolaGAROFF(datiForm.getColumn("GARE.IMPAPP").getValue().doubleValue(),
          datiForm.getColumn("GARE.PGAROF").getValue().doubleValue(), datiForm.getColumn("TORN.TIPGEN").getValue().longValue());
      datiForm.addColumn("GARE.GAROFF", JdbcParametro.TIPO_DECIMALE,importoCauzioneProvvisoria);
    }
  }

  @Override
  public void afterInsertEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    //Nel caso sia valorizzato il campo NGARAAQ e ne sia stato modificato il valore, si deve inserire
    //l'occorrenza in DITG.
    if(datiForm.isColumn("TORN.NGARAAQ") && datiForm.isModifiedColumn("TORN.NGARAAQ") && datiForm.getObject("TORN.NGARAAQ")!=null){
      this.insertDittaAccordoQuadro(status, datiForm);
    }

    String lottoOffertaUnica = UtilityStruts.getParametroString(this.getRequest(), "LOTTO_OFFERTAUNICA");
    if(lottoOffertaUnica==null){
      //Se iterga==6 si procede alla valorizzazione di elencoe se vi sono le condizioni
      Long iterga = datiForm.getLong("TORN.ITERGA");
      String ngara = datiForm.getColumn("GARE.NGARA").getValue().stringValue();
      if((iterga!=null && iterga.longValue()==6) || (!(iterga!=null && iterga.longValue()==6) &&
          this.geneManager.getProfili().checkProtec((String) this.getRequest().getSession().getAttribute(
              CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS", "ALT.GARE.AssociaGaraAElenco"))){
        try {
          if("1".equals(pgManager.getPresenzaElencoOperatori(ngara, "GARE"))){
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
              this.sqlManager.update("update gare set elencoe = ? where ngara = ? ", new Object[]{codiceElenco,ngara});
          }
        } catch (SQLException e) {
          throw new GestoreException("Errore nell'inizializzazione del campo elencoe " +
              " della gara  (" + ngara +")", null, e);
        }
      }



      //Aggiornamento GARE1
      if(datiForm.isModifiedTable("GARE1")){
        AbstractGestoreEntita gestoreGARE1 = new DefaultGestoreEntita("GARE1", this.getRequest());
        datiForm.setValue("GARE1.NGARA", ngara);
        datiForm.getColumn("GARE1.NGARA").setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara));
        gestoreGARE1.update(status, datiForm);
      }
    }
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

    this.verificaLancioSommaImportiCategorie(datiForm);
    this.verificaImportoBaseAstaSottoSoglia(datiForm);
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

	//Controllo importo manodopera
        this.controlloImpmano(datiForm);

          // Gestione del codice CIG fittizio
	if (datiForm.isColumn("ESENTE_CIG") && datiForm.isModifiedColumn("ESENTE_CIG")) {
		String esenteCig = datiForm.getString("ESENTE_CIG");
		String codCigFittizio = datiForm.getString("CODCIG_FIT");
		if ("1".equals(esenteCig)) {
			if (StringUtils.isEmpty(codCigFittizio) || " ".equals(codCigFittizio)) {
				int nextId = this.genChiaviManager.getNextId("GARE.CODCIG");
				codCigFittizio = "#".concat(StringUtils.leftPad(""+nextId, 9, "0"));
				datiForm.setValue("GARE.CODCIG", codCigFittizio);
			} else {
				datiForm.setValue("GARE.CODCIG", codCigFittizio);
			}
		}
	}

    try {
      if(datiForm.isColumn("GARE.CODCIG") && datiForm.getColumn("GARE.CODCIG").getValue().stringValue()!=null
            && !"".equals(datiForm.getColumn("GARE.CODCIG").getValue().stringValue())){

        String msg = pgManager.controlloUnicitaCIG(datiForm.getColumn("GARE.CODCIG").getValue().stringValue(),datiForm.getColumn("GARE.NGARA").getValue().stringValue());
        if(msg!=null){
          String descrizione = (String) this.sqlManager.getObject(
              "select tab1desc from tab1 where tab1cod = ? and tab1tip = ?",
              new Object[] { "A1151","1" });
          if(descrizione.substring(0, 1).equals("0")){
              UtilityStruts.addMessage(this.getRequest(), "warning",
                  "warnings.gare.codiceCIGDuplicato",
                  new Object[] {msg });
          }else{
            throw new GestoreException("Errore durante l'aggiornamento del campo GARE.CODCIG", "gare.codiceCIGDuplicato",new Object[] {msg },  new Exception());
          }
        }
      }
    } catch (SQLException e1) {
        throw new GestoreException("Errore nella lettura della campo tabellato A1151",null,e1);
    }


    this.controlloAccordoQuadro(datiForm,true);

    if (datiForm.isColumn("TORN.CODCIGAQ") && datiForm.isModifiedColumn("TORN.CODCIGAQ") && datiForm.getColumn("TORN.CODCIGAQ").getValue().stringValue()!=null
        && !"".equals(datiForm.getColumn("TORN.CODCIGAQ").getValue().stringValue()) && datiForm.getColumn("TORN.CODCIGAQ").getValue().stringValue().length()!= 10)
      throw new GestoreException("Il codice CIG specificato non è valido","controlloCodiceCIG");

    DataColumn codgar1 = datiForm.getColumn("GARE.CODGAR1");

    //Controllo che  TORN.NUMAVCP sia numerico
    if (datiForm.isColumn("TORN.NUMAVCP") && datiForm.isModifiedColumn("TORN.NUMAVCP")) {
      String numavcp = datiForm.getString("TORN.NUMAVCP");
      numavcp = UtilityStringhe.convertiNullInStringaVuota(numavcp);
      if (!"".equals(numavcp)) {
        if (!GestoreGARE.isNumeric(numavcp)) {
          throw new GestoreException("Il valore specificato per N.gara ANAC deve essere numerico","NGaraAnacNoNumerico");
        }
      }
    }

    datiForm.getColumn("GARE.NGARA").setChiave(true);


    if (this.getGeneManager().getProfili().checkProtec(
        (String) this.getRequest().getSession().getAttribute(
            CostantiGenerali.PROFILO_ATTIVO), "COLS", "VIS",
        "GARE.GARE.CRITLICG") && this.getGeneManager().getProfili().checkProtec(
            (String) this.getRequest().getSession().getAttribute(
                CostantiGenerali.PROFILO_ATTIVO), "COLS", "VIS",
            "GARE.GARE.DETLICG") && this.getGeneManager().getProfili().checkProtec(
                (String) this.getRequest().getSession().getAttribute(
                    CostantiGenerali.PROFILO_ATTIVO), "COLS", "VIS",
                "GARE.GARE.CALCSOANG")) {
      //Calcolo di MODLICG
      if (datiForm.isColumn("GARE.MODLICG") && datiForm.isColumn("GARE.CRITLICG") && datiForm.isColumn("GARE.DETLICG") && datiForm.isColumn("GARE.CALCSOANG")
          && datiForm.isColumn("GARE.APPLEGREGG")){
        Long modlicg = datiForm.getLong("GARE.MODLICG");
        if (modlicg== null) {
          Long critlicg = datiForm.getLong("GARE.CRITLICG");
          Long detlicg = datiForm.getLong("GARE.DETLICG");
          String calcsoang = datiForm.getString("GARE.CALCSOANG");
          Long applegregg = datiForm.getLong("GARE.APPLEGREGG");
          if (critlicg==null || ("1".equals(critlicg) && detlicg== null && (calcsoang==null || "".equals(calcsoang)))) {
            throw new GestoreException("I dati per determinare il criterio di aggiudicazione non sono completi","criterioAggiudicazionegNoDati");
          }

          modlicg = pgManager.getMODLICG(critlicg, detlicg, calcsoang, applegregg);
          datiForm.setValue("GARE.MODLICG", modlicg);
        }
      }

    }

    //calcolo ITERGA
    String tipoGara = UtilityStruts.getParametroString(this.getRequest(),
    "tipoGara");
    if (tipoGara != null && tipoGara.equalsIgnoreCase("garaLottoUnico") && datiForm.isColumn("TORN.ITERGA") && datiForm.isColumn("GARE.TIPGARG")) {
      Long iterga = datiForm.getLong("TORN.ITERGA");
      if (iterga==null) {
        Long tipgarg = datiForm.getLong("GARE.TIPGARG");
        if(tipgarg==null){
          throw new GestoreException("Non è stato definito il tipo procedura","noTipoProcedura");
        }

        try {
          iterga = pgManager.getITERGA(tipgarg);
          datiForm.setValue("TORN.ITERGA", iterga);
        } catch (SQLException e) {
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
            throw new GestoreException("Il tipo procedura non è disponibile nella modalità telematica", "noTipoProceduraTelematica");
          }
        }
      }
    }

  //Controlli che non devono essere eseguiti per lotto di gara con offerta unica
    String lottoOffertaUnica = UtilityStruts.getParametroString(this.getRequest(), "LOTTO_OFFERTAUNICA");

    if(datiForm.isColumn("GARE.CODCIG") && datiForm.isModifiedColumn("GARE.CODCIG") && datiForm.getColumn("GARE.CODCIG").getValue().stringValue()!=null
        && !"".equals(datiForm.getColumn("GARE.CODCIG").getValue().stringValue()) && datiForm.getColumn("GARE.CODCIG").getValue().stringValue().length()!= 10)
      throw new GestoreException("Il codice CIG specificato non è valido","controlloCodiceCIG");

    if(datiForm.isColumn("GARE.CUPPRG") && datiForm.isModifiedColumn("GARE.CUPPRG") && datiForm.getColumn("GARE.CUPPRG").getValue().stringValue()!=null
        && !"".equals(datiForm.getColumn("GARE.CUPPRG").getValue().stringValue()) && datiForm.getColumn("GARE.CUPPRG").getValue().stringValue().length()!= 15)
      throw new GestoreException("Il codice CUP specificato non è valido","controlloCodiceCUP");

    if(datiForm.isColumn("GARE1.CODCUI") && datiForm.isModifiedColumn("GARE1.CODCUI") && datiForm.getColumn("GARE1.CODCUI").getValue().stringValue()!=null
        && !"".equals(datiForm.getColumn("GARE1.CODCUI").getValue().stringValue()) && (datiForm.getColumn("GARE1.CODCUI").getValue().stringValue().length()< 20 || datiForm.getColumn("GARE1.CODCUI").getValue().stringValue().length() >22))
      throw new GestoreException("Il codice CUI specificato non è valido","controlloCodiceCUI");

    // Gestione della sezione 'Categoria prevalente'
    GestoreCATG.gestisciEntitaDaGare(this.getRequest(), status, datiForm,
        datiForm.getColumn("GARE.NGARA"));
    // Gestione delle sezioni 'Ulteriori categorie'
    GestoreOPESMultiplo.gestisciEntitaDaGare(this.getRequest(), status,
        datiForm, datiForm.getColumn("GARE.NGARA"));

    if (lottoOffertaUnica == null) {

	    this.setContributiDaImportoBaseAsta(datiForm, codgar1.getValue().stringValue());

	    // AGGIORNAMENTO TORN
	    datiForm.addColumn("TORN.CODGAR", JdbcParametro.TIPO_TESTO,
	        codgar1.getValue());
	    datiForm.getColumn("TORN.CODGAR").setChiave(true);
	    datiForm.getColumn("TORN.CODGAR").setObjectOriginalValue(
	        datiForm.getColumn("TORN.CODGAR").getValue());
	    this.update(status, datiForm, new GestoreTORN());

	    // Calcolo della cauzione provvisoria
	    this.calcolaGAROFF(datiForm);

	    // Gestione delle sezioni tratti di strada
	    AbstractGestoreChiaveNumerica gestoreGARSTR = new DefaultGestoreEntitaChiaveNumerica(
	        "GARSTR", "NUMSTR", new String[] { "NGARA" }, this.getRequest());
	    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
	        gestoreGARSTR, "TRSTRADA",
	        new DataColumn[] { datiForm.getColumn("GARE.NGARA") }, null);

	    //Gestione delle sezioni coperture assicurative
	    AbstractGestoreChiaveNumerica gestoreGARASS = new DefaultGestoreEntitaChiaveNumerica(
            "GARASS", "NUMASS", new String[] { "NGARA" }, this.getRequest());
        this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
            gestoreGARASS, "ASS",
            new DataColumn[] { datiForm.getColumn("GARE.NGARA") }, null);

      	//Gestione delle sezioni n cup
        AbstractGestoreChiaveIDAutoincrementante gestoreGARECUP = new DefaultGestoreEntitaChiaveIDAutoincrementante(
			"GARECUP", "ID", this.getRequest());
		this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
			gestoreGARECUP, "GARECUP", new DataColumn[] {datiForm.getColumn("GARE.NGARA")}, null);

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
     }else{
		//Gestione delle sezioni n cup
		 AbstractGestoreChiaveIDAutoincrementante gestoreGARECUP = new DefaultGestoreEntitaChiaveIDAutoincrementante(
		  "GARECUP", "ID", this.getRequest());
		 this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
		  gestoreGARECUP, "GARECUP", new DataColumn[] {datiForm.getColumn("GARE.NGARA")}, null);
    }

    // Gestione CPV
    this.updateGARCPV(status, datiForm);

    //Controlli specifici dei lotti di gara, sia offerta unica che distinte
    if(codgar1.getValue().stringValue() != null && !"".equals(codgar1.getValue().stringValue()) &&
        !codgar1.getValue().stringValue().startsWith("$")){
      //Controllo che il campo CODIGA abbia un valore numerico e univoco su tutti i lotti della gara
      //Il controllo scatta solo nella pagina 'Dati generali'
      if (datiForm.isColumn("GARE.CODIGA"))
        controlloCodiga(datiForm, lottoOffertaUnica, codgar1);
    }

    String garaLottoUnico = UtilityStruts.getParametroString(this.getRequest(),"tipoGara");
    if (garaLottoUnico == null || !garaLottoUnico.equalsIgnoreCase("true")){
      if( datiForm.isColumn("GARE.MODLICG")){
        Long modlicg = datiForm.getLong("GARE.MODLICG");
      if ((modlicg != null && modlicg.longValue()==17) && !this.confrontoOffaumGaraTornata(datiForm))
        throw new GestoreException("Non è possibile modificare il criterio di aggiudicazione 'Prezzo più alto' perchè la gara non ammette offerte in aumento", "modificaLottoPrezzoPiuAlto");
      }
    }

    if (!this.confrontoCritlicGaraTornata(datiForm, lottoOffertaUnica))
      throw new GestoreException("Il criterio di aggiudicazione del lotto non può essere Offerta economicamente più vantaggiosa","critlicgNoOEPV");

    if (!this.confrontoModlicGaraTornata(datiForm, lottoOffertaUnica))
      throw new GestoreException("La modalità di aggiudicazione del lotto non può essere Offerta a prezzi unitari","modlicgNoOFFPREZUNI");

    if(!this.confrontoValtecLottoTornata(datiForm, lottoOffertaUnica))
      throw new GestoreException("Non è possibile impostare la valutazione requisiti minimi mediante busta tecnica","controlloValtecLottiGara");

    // Gestione delle pubblicazioni esito
    AbstractGestoreChiaveNumerica gestorePUBG = new DefaultGestoreEntitaChiaveNumerica(
        "PUBG", "NPUBG", new String[] { "NGARA" }, this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
        gestorePUBG, "PUBESITO",
        new DataColumn[] { datiForm.getColumn("GARE.NGARA") }, null);

    // Allineamento IMPAPPG nelle ditte della gara
    this.updateDitteGara(datiForm);

    //Aggiornamento dell'importo totale della tornata tranne nel caso
    //di gara a lotto unico
    this.AggiornaImportoTotaleTorn(datiForm,"MOD");

    //Aggiornamento GARE1
    if(datiForm.isModifiedTable("GARE1")){
      AbstractGestoreEntita gestoreGARE1 = new DefaultGestoreEntita("GARE1", this.getRequest());
      gestoreGARE1.update(status, datiForm);
    }

    //Aggiornamento GARECONT
    if(datiForm.isModifiedTable("GARECONT")){
      if(datiForm.isColumn("GARECONT.CENINT") && datiForm.isColumn("TORN.CENINT")  && datiForm.isColumn("GARECONT.PCOESE") &&
          datiForm.isColumn("GARECONT.PCOFAT") && (datiForm.isModifiedColumn("GARECONT.PCOESE") || datiForm.isModifiedColumn("GARECONT.PCOFAT"))){
        if(datiForm.getLong("GARECONT.PCOESE")==null && datiForm.getLong("GARECONT.PCOFAT")==null)
          datiForm.setValue("GARECONT.CENINT", null);
        else{
          String cenint = datiForm.getString("TORN.CENINT");
          datiForm.setValue("GARECONT.CENINT", cenint);
        }

      }


      AbstractGestoreEntita gestoreGARECONT = new DefaultGestoreEntita("GARECONT", this.getRequest());
      gestoreGARECONT.update(status, datiForm);
    }

    //Gestione entita GARALTSOG
    pgManagerEst1.gestioneGaraltSog(datiForm);

    String descrErrMsg = "";
    String bloccoModificaPubblicazione = UtilityStruts.getParametroString(this.getRequest(), "bloccoModificaPubblicazione");
    if("TRUE".equals(bloccoModificaPubblicazione)){
      HashMap<String, DataColumn> hm = datiForm.getColonne();
      Iterator<?> it = hm.entrySet().iterator();

      boolean tracciareCampo=true;
      boolean modificheDaTracciare=false;
      while (it.hasNext()) {
          Entry<String, DataColumn> pair = (Entry<String, DataColumn>)it.next();
          if(pair.getValue().isModified()){
            //condizioni per non tracciare i campi:
            //campi fittizZi che contengo nel nome la stringa "FIT"
            //Campi fittizzi delle sezioni dinamiche che contengono nel nome le stringhe "INDICE_" e "MOD_"
            //Campi che contengono nei nomi le stringhe: "NGARA","TIPCPV","TIPLAVG","IMPAPP","CIG","NUMCPV"
            if(pair.getKey().indexOf("FIT")>=0 || pair.getKey().indexOf("INDICE_")>=0 || pair.getKey().indexOf("MOD_")>=0  ||
                pair.getKey().indexOf("NGARA")>=0 || pair.getKey().indexOf("TIPCPV")>=0 || pair.getKey().indexOf("TIPLAVG")>=0 ||
                pair.getKey().indexOf("IMPAPP")>=0 || pair.getKey().indexOf("CIG")>=0 ||
                pair.getKey().indexOf("IMPPER")>=0 || pair.getKey().indexOf("IMPCOR_RIB")>=0 || pair.getKey().indexOf("IMPMIS_RIB")>=0 ||
                pair.getKey().indexOf("NUMCPV")>=0 || pair.getKey().indexOf("GARE.GAROFF")>=0 || pair.getKey().indexOf("STATOGARA")>=0 
                || pair.getKey().indexOf("OGGCONT_")>=0 || pair.getKey().indexOf("_CAT_PRE_")>=0)
              tracciareCampo=false;
            else
              tracciareCampo=true;

            if(tracciareCampo){
              modificheDaTracciare = true;
              if(!"".equals(descrErrMsg)){
                descrErrMsg+="\n";
              }
              if(pair.getKey().indexOf("DEL_CPVCOMP")>0){
                //Nel caso di eliminazione di CPV complementari non ho le chiavi della occorrenza eliminata, quindi
                //il messaggio sarà generico
                descrErrMsg+= "Eliminata una occorrenza di CPVCOMP.";
              }else{
                JdbcParametro paramOrigin = pair.getValue().getOriginalValue();
                String valOriginale = "";
                if(paramOrigin!=null){
                  valOriginale = paramOrigin.toString();
                }
                JdbcParametro paramNuovo = pair.getValue().getValue();
                String valNuovo = "";
                if(paramNuovo!=null){
                  valNuovo = paramNuovo.toString();
                }
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
        logEvento.setOggEvento(datiForm.getColumn("GARE.NGARA").getValue().stringValue());
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

  @Override
  public void afterUpdateEntita(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
    //Nel caso sia valorizzato il campo NGARAAQ e ne sia stato modificato il valore, si deve inserire
    //l'occorrenza in DITG.
    if(impl.isColumn("TORN.NGARAAQ") && impl.isModifiedColumn("TORN.NGARAAQ") && impl.getObject("TORN.NGARAAQ")!=null){
      this.insertDittaAccordoQuadro(status, impl);
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
      Vector<DataColumn> colonneGARCPV = new Vector<DataColumn>();
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
        new DataColumn[] { datiForm.getColumn("GARE.NGARA") }, null);

  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
    this.verificaLancioSommaImportiCategorie(datiForm);
    this.verificaImportoBaseAstaSottoSoglia(datiForm);
  }

  /**
   * Verifica se si devono fare i controlli sulle somme degli importi categorie
   * appalto in base alla visibilità delle sezioni relative alla categoria
   * prevalente e l'ulteriore categoria
   *
   * @param datiForm
   *        container di dati ricevuti dalla pagina di partenza
   * @throws GestoreException
   */
  private void verificaLancioSommaImportiCategorie(DataColumnContainer datiForm)
      throws GestoreException {
    // Se le sezioni 'Categoria prevalente' e 'Categoria ulteriore' sono
    // visualizzabili da profilo si esegue il controllo della somma degli
    // importi delle categorie prevalenti con l'importo netto contrattuale
    if (datiForm.isColumn("GARE.IMPAPP")
        && datiForm.isColumn("NUMERO_CATEGORIE")
        && this.getGeneManager().getProfili().checkProtec(
            (String) this.getRequest().getSession().getAttribute(
                CostantiGenerali.PROFILO_ATTIVO), "SEZ", "VIS",
            "GARE.GARE-scheda.DATIGEN.CATG")
        && this.getGeneManager().getProfili().checkProtec(
            (String) this.getRequest().getSession().getAttribute(
                CostantiGenerali.PROFILO_ATTIVO), "SEZ", "VIS",
            "GARE.GARE-scheda.DATIGEN.OPES")) {
      // Il controllo della somma degli importi delle categorie deve essere
      // effettuato solo se si e' modificata la pagina dati generali e
      // non altre
    	Long tipgen = datiForm.getColumn("TORN.TIPGEN").getValue().longValue();
    	boolean oneriProgettVisibili= true;

    	//flag che serve per indicare se nel messaggio sul controllo seguente si deve adoperare la
    	//dicitura "categorie" oppure "prestazioni"
    	boolean categoriePresenti = true;
    	if (tipgen.intValue() != 1){
    		oneriProgettVisibili = false;
    		categoriePresenti = false;
    	}
    	GestoreAPPA.checkSommaImportiCategorie(datiForm, "GARE", "GARE.IMPAPP",
          "GARE.ONPRGE", "CATG", "OPES", this.getGeneManager(),
          this.getRequest(),oneriProgettVisibili,categoriePresenti);
    }
  }

  /**
   * Verifica, per le gare a tipo procedura ristretta, che l'importo base di gara
   * sia inferiore della soglia massima. In caso negativo viene emesso un
   * warning
   *
   * @param datiForm
   * @throws GestoreException
   */
  private void verificaImportoBaseAstaSottoSoglia(DataColumnContainer datiForm)
      throws GestoreException {
    if (datiForm.isColumn("GARE.TIPGARG") && datiForm.isColumn("GARE.IMPAPP") && datiForm.isColumn("GARE.CATIGA")) {
      // il controllo si esegue solo nella scheda dei dati generali della gara
      if (datiForm.getColumn("GARE.TIPGARG").getValue().longValue().longValue() == 3) {
        // il controllo si esegue solo se la gara con procedura ristretta
        try {
          String descrizione = (String) this.sqlManager.getObject(
              "select tab1desc from tab1 where tab1cod = ?",
              new Object[] { "A1027" });
          Double soglia = UtilityNumeri.convertiDouble(descrizione,
              UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE);
          Double importoBaseAsta = new Double(0);
          if (datiForm.getColumn("GARE.IMPAPP").getValue().doubleValue() != null)
            importoBaseAsta = datiForm.getColumn("GARE.IMPAPP").getValue().doubleValue();
          if (importoBaseAsta.doubleValue() >= soglia.doubleValue()) {
            UtilityStruts.addMessage(this.getRequest(), "warning",
                "warnings.gare.verificaImportoBaseAstaSottoSoglia",
                new Object[] { UtilityNumeri.convertiImporto(soglia, 2) });

          }
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore nell'estrazione della soglia per procedura ristretta semplificata",
              "getSogliaBaseAsta", e);
        }
      }
    }
  }

  /**
   * Aggiorna l'importo base di gara in tutte le ditte della gara
   *
   * @param datiForm
   *        contenitore con i dati del form
   *
   * @throws GestoreException
   */
  private void updateDitteGara(DataColumnContainer datiForm)
      throws GestoreException {
    if (datiForm.isModifiedColumn("GARE.IMPAPP")) {
      String sql = "update DITG set IMPAPPD=? where NGARA5=?";

      Object[] params = new Object[2];
      params[0] = datiForm.getDouble("GARE.IMPAPP");
      params[1] = datiForm.getString("GARE.NGARA");
      try {
        this.sqlManager.update(sql, params);
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore durante l'allineamento dell'importo a base di gara per le ditte della gara '"
                + params[1]
                + "'", "allineaImpBaseAstaDitteGara", null, e);
      }
    }
  }


  /**
   * Inserimento delle occorrenze in DITG relative al nuovo lotto
   * per tutte le ditte che risultano già inserite in gara
   *
   * @param datiForm
   *        contenitore con i dati del form
   *
   * @throws GestoreException
   */
  private void insertDitteGara(DataColumnContainer datiForm)
      throws GestoreException {

	  String codiceGara = datiForm.getString("GARE.CODGAR1");

      Object[] params = new Object[2];
      params[0] = codiceGara;
      params[1] = codiceGara;
      String ngara = datiForm.getString("GARE.NGARA");
      pgManager.inizializzaDitteLottiOffertaUnica(codiceGara, ngara, false);

  }


  /**
   * Controlli sul campo CODIGA (solo per lotti di gara):
   * valore numerico nel caso di lotto di gara a offerta unica e con criterio di aggiudicazione Offerta prezzi
   * valore univoco su tutti i lotti della gara.
   *
   * @param datiForm
   * @param lottoOffertaUnica
   *        codgar1
   * @throws GestoreException
   */
  private void controlloCodiga(DataColumnContainer datiForm,
      String lottoOffertaUnica, DataColumn codgar1) throws GestoreException {

    String codiga = datiForm.getString("GARE.CODIGA");

    if(!UtilityNumeri.isAValidNumber(codiga, UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE) || ('0' == codiga.charAt(0) && codiga.length() > 1)){
      throw new GestoreException("Il campo Lotto deve contenere solo valori numerici","codigaNumerico");
    }


    String select="select count(*) from gare where codgar1 = ? and codiga = ? and ngara <> ?";
    String ngara=datiForm.getString("GARE.NGARA");
    try {
      Long numCodiga = (Long) sqlManager.getObject(
          select, new Object[] { codgar1.getValue().stringValue(),codiga, ngara});
      if (numCodiga!= null && numCodiga.longValue()>0){
        throw new GestoreException("Il valore specificato nel campo Lotto è già presente nella gara","unicitaCODIGA");
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nel controllo di unicità " +
          "del campo codiga", null, e);
    }
  }


  /**
   * Solo per i lotti delle gare ad offerta unica viene effettuato il seguente controllo:
   * se TORN.CRITLIC = 1 e GARE.CRITLICG == 2  ->  return false
   * in tutti gli altri casi ritorna true
   *
   * @param datiForm
   * @param lottoOffertaUnica
   * @ret boolean
   *
   * @throws GestoreException
   */
  private boolean confrontoCritlicGaraTornata(DataColumnContainer datiForm,String lottoOffertaUnica) throws GestoreException{
    boolean ret=true;

    if (lottoOffertaUnica!= null && datiForm.isColumn("GARE.CRITLICG")){
      Long critlicLotto = datiForm.getLong("GARE.CRITLICG");
      String codgar1=datiForm.getString("GARE.CODGAR1");
      String select = "select critlic from torn where codgar = ?";
      try {
        Long critlic = (Long) sqlManager.getObject(select,
            new Object[] { codgar1 });
        if(critlicLotto!= null && critlic!=null && critlic.longValue()!=2  && critlicLotto.longValue() == 2){
          ret = false;
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore durante la lettura del criterio di aggiudicazione della gara", null, e);
      }
    }
    return ret;
  }

    //CF240610 controllo la modalità dei lotti di una gara Off.Unica a ribasso
  private boolean confrontoModlicGaraTornata(DataColumnContainer datiForm,String lottoOffertaUnica) throws GestoreException{
    boolean ret=true;

  if (lottoOffertaUnica!= null && datiForm.isColumn("GARE.MODLICG")){
      Long modlicLotto = datiForm.getLong("GARE.MODLICG");
      String codgar1=datiForm.getString("GARE.CODGAR1");
      String select = "select modlic from torn where codgar = ?";
      try {
        Long modlic = (Long) sqlManager.getObject(select,
            new Object[] { codgar1 });
        if(modlicLotto!= null && modlic!=null && (modlic.longValue()==1||modlic.longValue()==13)  && (modlicLotto.longValue() == 5||modlicLotto.longValue() == 14)){
          ret = false;
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore durante la lettura della modalità di aggiudicazione della gara", null, e);
      }
    }
    return ret;
  }

  //CF240610 controllo la modalità dei lotti di una gara Off.Unica a ribasso
  private boolean confrontoOffaumGaraTornata(DataColumnContainer datiForm) throws GestoreException{
    boolean ret=true;
    String codgar1=datiForm.getString("GARE.CODGAR1");
    String select = "select offaum from torn where codgar = ?";
    try {
      String offaum = (String) sqlManager.getObject(select,
          new Object[] { codgar1 });
      if(!"1".equals(offaum)){
        ret = false;
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore durante la lettura della modalità di aggiudicazione della gara", null, e);
    }
    return ret;
  }
  /**
   * Se la gara non è una gara a lotto unico viene aggiornato l'importo TORN.IMPTOR con la somma
   * degli importi di tutti i lotti
   *
   * @param datiForm
   * @param modalita, valori assunti MOD e DEL

   * @throws GestoreException
   */
  private void AggiornaImportoTotaleTorn(DataColumnContainer datiForm,String modalita) throws GestoreException{
    String numeroLotto = datiForm.getString("GARE.NGARA");
    String codgar = null;
    if(datiForm.isColumn("GARE.CODGAR1"))
      codgar = datiForm.getString("GARE.CODGAR1");
    else{
      try {
        codgar = (String) sqlManager.getObject("select codgar1 from gare where ngara = ?", new Object[]{numeroLotto});
      } catch (SQLException e) {
        throw new GestoreException("Errore durante la lettura del campo codgar1 della gara", null, e);
      }
    }

    if(codgar.startsWith("$"))
      return;

    //Va effettuato l'aggiornamento anche quando si modificano gli importi dei rinnovi
    Double importoLotto= null;
    Double importoRinnoviLotto=null;
    boolean aggiornamento=false;
    if("MOD".equals(modalita)){
      if(datiForm.isModifiedColumn("GARE.IMPAPP") || datiForm.isModifiedColumn("GARE1.IMPRIN") || datiForm.isModifiedColumn("GARE1.IMPSERV")
          || datiForm.isModifiedColumn("GARE1.IMPPROR") || datiForm.isModifiedColumn("GARE1.IMPALTRO")){

          importoLotto= datiForm.getDouble("GARE.IMPAPP");
          if (importoLotto == null)
            importoLotto = new Double(0);

        if( datiForm.isModifiedColumn("GARE1.IMPRIN") || datiForm.isModifiedColumn("GARE1.IMPSERV")
            || datiForm.isModifiedColumn("GARE1.IMPPROR") || datiForm.isModifiedColumn("GARE1.IMPALTRO")){
          Double imprinLotto= datiForm.getDouble("GARE1.IMPRIN");
          Double imposervLotto= datiForm.getDouble("GARE1.IMPSERV");
          Double impprorLotto= datiForm.getDouble("GARE1.IMPPROR");
          Double impaltroLotto= datiForm.getDouble("GARE1.IMPALTRO");
          importoRinnoviLotto = pgManagerEst1.calcoloValoreMassimoStimato(importoLotto, imprinLotto, imposervLotto, impprorLotto, impaltroLotto);
        }
        aggiornamento=true;
      }

    }
    if (aggiornamento||"DEL".equals(modalita)) {
      //Non va eseguito l'aggiornamento nel caso di lotto unico


      pgManagerEst1.setImportoTotaleTorn(codgar, numeroLotto, importoLotto, importoRinnoviLotto);

    }

  }

  /**
   * Solo per i lotti delle gare ad offerta unica viene effettuato il seguente controllo:
   * se TORN.VALTEC != 1 per il lotto di gara non si può impostare GARE1.VALTEC=1
   *
   *
   * @param datiForm
   * @param lottoOffertaUnica
   * @ret boolean
   *
   * @throws GestoreException
   */
  private boolean confrontoValtecLottoTornata(DataColumnContainer datiForm,String lottoOffertaUnica) throws GestoreException{
    boolean ret=true;

    if (lottoOffertaUnica!= null && datiForm.isColumn("GARE1.VALTEC")){
      String valtecLotto = datiForm.getString("GARE1.VALTEC");
      String codgar1=datiForm.getString("GARE.CODGAR1");
      String select = "select valtec from torn where codgar = ?";
      try {
        String valtecTornata = (String) sqlManager.getObject(select,
            new Object[] { codgar1 });
        if(!"1".equals(valtecTornata) && "1".equals(valtecLotto)){
          ret = false;
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore durante la lettura di TORN.VALTEC della gara" + codgar1, null, e);
      }
    }
    return ret;
  }

  /*
   * Vengono eseguiti i controlli relativi al collegamento ad un accordo quadro.
   * In inserimento non si ha ancora l'associazione, quindi non è necessario effettuare il controllo sulle ditte e
   * sulle lavorazioni
   */
  private void controlloAccordoQuadro(DataColumnContainer datiForm, boolean controlloDitte) throws GestoreException{
      try {

        //Non è possibile modificare il CENINT se valorizzato NGARAAQ e ci sono occorrenze in GARALTSOG
        if(datiForm.isColumn("TORN.NGARAAQ") && datiForm.getString("TORN.NGARAAQ") !=null && !"".equals(datiForm.getString("TORN.NGARAAQ"))){
          String ngaraaq = datiForm.getString("TORN.NGARAAQ");
          Long conteggioSoggetti = (Long)this.sqlManager.getObject("select count(id) from garaltsog where ngara=? ",
              new Object[]{ngaraaq});
          if(conteggioSoggetti!= null && conteggioSoggetti.longValue()>0){
            String cenint = datiForm.getString("TORN.CENINT");
            Long numSoggettiCenint = new Long(0);
            if(cenint!=null && !"".equals(cenint)){
              numSoggettiCenint= (Long)this.sqlManager.getObject("select count(id) from garaltsog where ngara=? and cenint=?",
                  new Object[]{ngaraaq,cenint});
              if(numSoggettiCenint==null)
                numSoggettiCenint = new Long(0);
              if(cenint!=null && !"".equals(cenint) && numSoggettiCenint.longValue()==0)
                throw new GestoreException("La stazione appaltante non rientra tra i soggetti qualificati a ricorrere all'accordo quadro",
                    "riferimentoAccordoQuadro.SoggettiAssociati",
                    null, new Exception());
            }else
              throw new GestoreException("Specificare la stazione appaltante selezionandola tra i soggetti qualificati a ricorrere all'accordo quadro",
                  "riferimentoAccordoQuadro.StazioneAppaltante",
                  null, new Exception());
          }

          //Se per l'accordo quadro è attivo il controllo spesa (CONTSPE.GARECONT = '1'), si deve fare il controllo si verifica che
          //l'importo a base di gara dell'adesione è inferiore o uguale al residuo da impegnare disponibile per la stazione appaltante dell'adesione stessa
          Vector datiAccordoQuadro = this.sqlManager.getVector("select contspe,gara,ncont from V_GARE_ACCORDIQUADRO where ngara=?", new Object[]{ngaraaq});
          String contspe = null;
          String garaAccQuadro=null;
          Long ncont = null;
          if(datiAccordoQuadro!=null && datiAccordoQuadro.size()>0){
            contspe  = SqlManager.getValueFromVectorParam(datiAccordoQuadro, 0).getStringValue();
            garaAccQuadro = SqlManager.getValueFromVectorParam(datiAccordoQuadro, 1).getStringValue();
            ncont = SqlManager.getValueFromVectorParam(datiAccordoQuadro, 2).longValue();
          }
          if("1".equals(contspe)){
            Double impapp = datiForm.getDouble("GARE.IMPAPP");
            Double impaut = null;
            Double impimp = null;
            String cenint = datiForm.getString("TORN.CENINT");
            Vector datiSpese = this.sqlManager.getVector("select impaut,impimp from v_spese_adesioni where ngara=? and ncont=? and cenint = ? ", new Object[]{garaAccQuadro, ncont, cenint});
            if(datiSpese!=null && datiSpese.size()>0){
              impaut = SqlManager.getValueFromVectorParam(datiSpese, 0).doubleValue();
              impimp = SqlManager.getValueFromVectorParam(datiSpese, 1).doubleValue();
              if(impaut==null)
                impaut = new Double(0);
              if(impimp==null)
                impimp = new Double(0);
              if(impaut.doubleValue()==0){
                throw new GestoreException("Non è possibile procedere poichè per la stazione appaltante non è specificato l'importo autorizzato dell'accordo quadro ",
                    "riferimentoAccordoQuadro.ImportoAutorizzato",
                    null, new Exception());
              }
              if(impapp == null)
                impapp = new Double(0);
              BigDecimal bdImportoDisponibile = BigDecimal.valueOf(new Double(impaut.doubleValue() - impimp.doubleValue())).setScale(2, BigDecimal.ROUND_HALF_UP);
              if(impapp.doubleValue() > bdImportoDisponibile.doubleValue()){
                String impautS = UtilityNumeri.convertiImporto(impaut, 2);
                String impimpS = UtilityNumeri.convertiImporto(impimp, 2);
                Double residuo = new Double(impaut.doubleValue() - impimp.doubleValue());
                String residuoS  = UtilityNumeri.convertiImporto(residuo, 2);
                throw new GestoreException("Non è possibile procedere poichè non l'importo autorizzato dell'accordo quadro non è specificato",
                    "riferimentoAccordoQuadro.ControlloImportoABaseGara",new Object[]{impautS, impimpS,residuoS}, new Exception());
              }
            }else{
              throw new GestoreException("Non è possibile procedere poichè per la stazione appaltante non è specificato l'importo autorizzato dell'accordo quadro",
                  "riferimentoAccordoQuadro.ImportoAutorizzato",
                  null, new Exception());
            }
          }
        }
        if(controlloDitte){
          if(datiForm.isColumn("TORN.NGARAAQ") && datiForm.isModifiedColumn("TORN.NGARAAQ")){
            Long conteggioDitte = (Long)this.sqlManager.getObject("select count(dittao) from ditg where codgar5=? ",
                new Object[]{datiForm.getString("GARE.CODGAR1")});
            if(conteggioDitte!= null && conteggioDitte.longValue()>0){
              throw new GestoreException("Non e possibile modificare il riferimento all'accordo quadro poichè vi sono delle ditte in gara","riferimentoAccordoQuadro.DitteAssociate",
                  null, new Exception());
            }
            Long conteggioLavorazioni = (Long)this.sqlManager.getObject("select count(ngara) from gcap where ngara=? and contafaq is not null",
                new Object[]{datiForm.getString("GARE.NGARA")});
            if(conteggioLavorazioni!= null && conteggioLavorazioni.longValue()>0){
              throw new GestoreException("Non e possibile modificare il riferimento all'accordo quadro poichè sono definite delle lavorazioni riferite all'accordo quadro stesso","riferimentoAccordoQuadro.LavorazioniAssociate",
                  null, new Exception());
            }
          }
        }
        if(datiForm.isColumn("TORN.ALTRISOG") && datiForm.isColumn("TORN.ISADESIONE")){
          Long altrisog = datiForm.getLong("TORN.ALTRISOG");
          String isAdesione = datiForm.getString("TORN.ISADESIONE");
          if((new Long(2)).equals(altrisog) && isAdesione!=null && "1".equals(isAdesione))
            throw new GestoreException("Non e possibile modificare il riferimento all'accordo quadro poichè altrisog è valorizzato","riferimentoAccordoQuadro.Altrisog",
                null, new Exception());
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore durante il controllo sulla presenza delle ditte della gara",null,e);
      }


  }

  public void insertDittaAccordoQuadro(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

    String ditta = null;
    String nomima = null;
    String ngaraaq = impl.getString("TORN.NGARAAQ");

    String ngara = impl.getString("GARE.NGARA");
    String codgar1 = impl.getString("GARE.CODGAR1");

    Long aqoper = null;
    try {
      aqoper = (Long)this.sqlManager.getObject("select aqoper from gare1 where ngara=?", new Object[]{ngaraaq});
    } catch (SQLException e) {
      throw new GestoreException("Errore durante la lettura del campo TORN.AQOPER dell'accordo quadro",null,e);
    }

    if(new Long(1).equals(aqoper)){
      Vector<?> datiDitta;
      try {
        datiDitta = this.sqlManager.getVector("select ditta, nomima from gare where ngara =?", new Object[]{ngaraaq});
        if(datiDitta!=null && datiDitta.size()>0){
          ditta = SqlManager.getValueFromVectorParam(datiDitta, 0).stringValue();
          nomima = SqlManager.getValueFromVectorParam(datiDitta, 1).stringValue();
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore durante la lettura dei dati della ditta aggiudicataria dell'accordo quadro",null,e);
      }

      if(ditta!=null){
        this.insertDitta(ngara, codgar1, ditta, nomima, ngaraaq, status);
      }
    } else if(new Long(2).equals(aqoper)) {
      try {
        List<?> datiDITGAQ = this.sqlManager.getListVector("select dittao,nomimp from ditgaq,impr where ngara=? and dittao=codimp order by numord", new Object[]{ngaraaq});
        if (datiDITGAQ != null) {
          for (int i = 0; i < datiDITGAQ.size(); i++) {
            ditta = SqlManager.getValueFromVectorParam(datiDITGAQ.get(i), 0).getStringValue();
            nomima = SqlManager.getValueFromVectorParam(datiDITGAQ.get(i), 1).getStringValue();
            if (ditta!=null) {
              this.insertDitta(ngara, codgar1, ditta, nomima, ngaraaq, status);
            }
          }
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore durante la lettura dei dati della tabella DITGAQ",null,e);
      }
    }

    if (impl.isColumn("TORN.GARTEL")) {
      String gartel = impl.getString("TORN.GARTEL");
      if ("1".equals(gartel)) {
        this.pgManager.aggiornaFaseGara(new Long(-30), ngara, false);
      }
    }
  }

  private void insertDitta(String ngara,String codgar1,String ditta,String nomima,String ngaraaq, TransactionStatus status) throws GestoreException{
    Vector<DataColumn> elencoCampi = new Vector<DataColumn>();
    elencoCampi.add(new DataColumn("DITG.NGARA5", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
    elencoCampi.add(new DataColumn("DITG.CODGAR5", new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar1)));
    elencoCampi.add(new DataColumn("DITG.DITTAO", new JdbcParametro(JdbcParametro.TIPO_TESTO, ditta)));
    elencoCampi.add(new DataColumn("DITG.NOMIMO", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomima)));
    elencoCampi.add(new DataColumn("DITG.NPROGG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1))));
    elencoCampi.add(new DataColumn("DITG.NUMORDPL", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1))));
    elencoCampi.add(new DataColumn("DITG.ACQUISIZIONE", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(6))));

    DataColumnContainer containerDITG = new DataColumnContainer(elencoCampi);
    GestoreDITG gestoreDITG = new GestoreDITG();
    gestoreDITG.setRequest(this.getRequest());
    gestoreDITG.inserisci(status, containerDITG);
    // Inizializzazione documenti della ditta
    this.pgManager.inserimentoDocumentazioneDitta(codgar1, ngara, ditta);
    //Nel caso di consorzio si devono copiare le occorrenze di RAGDET
    try {
      List<?> listaOccorrenzeDaCopiare = this.geneManager.getSql().getListHashMap(
          "select * from RAGDET where NGARA = ? and CODIMP = ?",
          new Object[] { ngaraaq, ditta});
      if (listaOccorrenzeDaCopiare != null && listaOccorrenzeDaCopiare.size() > 0) {
        Long maxNumdic = null;
        long newNumdic = 1;
        DataColumnContainer campiDaCopiare = new DataColumnContainer(this.geneManager.getSql(),
            "RAGDET", "select * from RAGDET", new Object[] {});
        for (int row = 0; row < listaOccorrenzeDaCopiare.size(); row++) {
          campiDaCopiare.setValoriFromMap(
              (HashMap<?,?>) listaOccorrenzeDaCopiare.get(row), true);
          campiDaCopiare.getColumn("RAGDET.CODIMP").setChiave(true);
          campiDaCopiare.getColumn("RAGDET.CODDIC").setChiave(true);
          campiDaCopiare.getColumn("RAGDET.NUMDIC").setChiave(true);
          campiDaCopiare.setValue("RAGDET.NGARA", ngara);

          // Si deve calcolare il valore di NUMDIC
          maxNumdic = (Long) this.sqlManager.getObject(
              "select max(NUMDIC) from RAGDET where CODIMP= ? and CODDIC=?",
              new Object[] {
                  ((HashMap<?,?>) listaOccorrenzeDaCopiare.get(row)).get("CODIMP").toString(),
                  ((HashMap<?,?>) listaOccorrenzeDaCopiare.get(row)).get("CODDIC").toString() });

          if (maxNumdic != null && maxNumdic.longValue() > 0)
            newNumdic = maxNumdic.longValue() + 1;
          campiDaCopiare.setValue("RAGDET.NUMDIC", new Long(newNumdic));

          campiDaCopiare.insert("RAGDET", this.geneManager.getSql());
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nell'inserimento delle ditte del consorzio per la gara " + ngara,null,e);
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

  private void controlloImpmano(DataColumnContainer datiForm) throws GestoreException {
    if(datiForm.isColumn("GARE1.IMPMANO")){
      Double impmano=datiForm.getDouble("GARE1.IMPMANO");
      Double impapp=datiForm.getDouble("GARE.IMPAPP");
      Double impnrl=datiForm.getDouble("GARE.IMPNRL");
      Double impsic=datiForm.getDouble("GARE.IMPSIC");

      if(impmano==null)
        impmano= new Double(0);
      if(impapp==null)
        impapp= new Double(0);
      if(impnrl==null)
        impnrl= new Double(0);
      if(impsic==null)
        impsic= new Double(0);

      if(impmano.doubleValue() > impapp.doubleValue() - impnrl.doubleValue() - impsic.doubleValue()){
        throw new GestoreException("Il costo manodopera non deve superare l'importo a base di gara soggetto a ribasso","controlloImportoManodopera",
            null, new Exception());
      }
    }
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
                if (newDataColumnContainer.getLong(gestore.getCampoNumericoChiave()) != null){
                  gestore.elimina(status, newDataColumnContainer);
                }

          // altrimenti e' stato eliminato un nuovo record non ancora inserito
          // ma predisposto nel form per l'inserimento
        } else {
          if (updateOccorrenza) {
            if (newDataColumnContainer.getLong(gestore.getCampoNumericoChiave()) == null){
              newDataColumnContainer.setValue("GARERDA.NGARA", dataColumnContainer.getString("GARE.NGARA"));
              gestore.inserisci(status, newDataColumnContainer);
              if("CAV".equals(tipoWSERP)){

                String codgar  = newDataColumnContainer.getString("GARERDA.CODGAR");
                String codiceRda  = newDataColumnContainer.getString("GARERDA.NUMRDA");
                String codiceCarrello  = newDataColumnContainer.getString("GARERDA.CODCARR");
                String esercizio  = newDataColumnContainer.getString("GARERDA.ESERCIZIO");
                String codiceLotto = "";

                String servizio = "WSERP";
                ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
                    CostantiGenerali.PROFILO_UTENTE_SESSIONE);
                Long syscon = new Long(profilo.getId());
                String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);
                String username = credenziali[0];
                String password = credenziali[1];

                WSERPRdaResType wserpRdaRes = this.gestioneWSERPManager.wserpAssociaRdaGara(username, password, servizio, codiceCarrello, codiceRda, null, codiceLotto, true);

                if(wserpRdaRes.isEsito()){
                    Long idRda  = newDataColumnContainer.getLong("GARERDA.ID");
                    //lettura deal ws
                    WSERPRdaType rdaSearch = new WSERPRdaType();
                    rdaSearch.setCodiceRda(codiceRda);
                    rdaSearch.setTipoRdaErp(esercizio);
                    WSERPRdaResType dettaglioRdares= this.gestioneWSERPManager.wserpDettaglioRda(username, password, servizio, rdaSearch);
                    if(dettaglioRdares.isEsito()){
                      this.gestioneWSERPManager.insPosizioniRda(username, password, servizio, codgar, codiceLotto, idRda, dettaglioRdares.getRdaArray());
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


}


