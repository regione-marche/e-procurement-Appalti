/*
 * Created on 17/lug/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.sil.pg.bl.AurManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.sil.pg.tags.gestori.plugin.GestorePopupFasiGara;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;
import pc_nicola.aur.WebServices.WsAURSoapProxy;

/**
 * Gestore non standard delle occorrenze dell'entita DITG presenti piu' volte
 * nelle pagine delle Fasi Gare (gare-pg-fasi.jsp) la quale contiene le ditte
 * che partecipano ad una gara d'appalto, nelle diverse fasi della gara stessa
 *
 * Questa classe NON e' un gestore standard e prepara i dati di ciascuna
 * occorrenza presente nella scheda e demanda alla classe DefaultGestoreEntita
 * le operazioni di insert, update e delete
 *
 * @author Luca.Giacomazzo
 */
public class GestoreFasiGara extends GestoreDITG {

  private AurManager aurManager;

  private AggiudicazioneManager aggManager;

  private TabellatiManager tabellatiManager;

  @Override
  public String getEntita() {
    return "DITG";
  }

  public GestoreFasiGara() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestoreFasiGara(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per gestire diversi SQL
    this.aurManager = (AurManager) UtilitySpring.getBean("aurManager",
        this.getServletContext(), AurManager.class);

    this.aggManager = (AggiudicazioneManager) UtilitySpring.getBean("aggiudicazioneManager",
        this.getServletContext(), AggiudicazioneManager.class);

    this.tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        this.getServletContext(), TabellatiManager.class);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

  	AbstractGestoreEntita gestoreDITG = new DefaultGestoreEntita(
        this.getEntita(), this.getRequest());
    // Osservazione: si e' deciso di usare la classe DefaultGestoreEntita,
    // invece di creare la classe GestoreDITG come apposito gestore di entita',
    // perche' essa non avrebbe avuto alcuna logica di business

    int numeroDitte = 0;
    String numDitte = this.getRequest().getParameter("numeroDitte");
    if(numDitte != null && numDitte.length() > 0)
      numeroDitte =  UtilityNumeri.convertiIntero(numDitte).intValue();

    int numeroDitteTotali = 0;
    String numDitteTotali = this.getRequest().getParameter("numeroDitteTotali");
    if(numDitteTotali != null && numDitteTotali.length() > 0)
      numeroDitteTotali =  UtilityNumeri.convertiIntero(numDitteTotali).intValue();

    Long numeroStepAttivo = new Long(UtilityStruts.getParametroString(
      this.getRequest(), GestioneFasiGaraFunction.PARAM_WIZARD_PAGINA_ATTIVA));
    Long faseGaraLong = null;
    // Il campo FASGAR, per compatibilita' con PWB e' sempre valorizzato come
    // il più grande intero che e' minore o uguale allo step attivo del wizard
    // diviso per 10
    if(numeroStepAttivo!=GestioneFasiGaraFunction.FASE_ASTA_ELETTRONICA){
      Double faseGara = new Double(Math.floor(numeroStepAttivo.doubleValue()/10));
      faseGaraLong = new Long(faseGara.longValue());
    }else{
      faseGaraLong = new Long(7);
    }

    boolean isGaraLottiConOffertaUnica = false;
    String tmp = this.getRequest().getParameter("isGaraLottiConOffertaUnica");
    if(tmp == null)
    	tmp = (String) this.getRequest().getAttribute("isGaraLottiConOffertaUnica");

    if("true".equals(tmp))
    	isGaraLottiConOffertaUnica = true;

    boolean isListaModificata = false;
    String codiceTornata = null;
    String codiceLotto = null;

    //Si determina se è attiva l'integrazione AUR
    String integrazioneAUR="2";
    String urlWSAUR = ConfigManager.getValore("it.eldasoft.sil.pg.integrazioneAUR.ws.url");
    if(urlWSAUR != null && !"".equals(urlWSAUR)){
      integrazioneAUR ="1";
    }

    if(numeroStepAttivo==GestioneFasiGaraFunction.FASE_CALCOLO_AGGIUDICAZIONE){
      String dittaProv = this.getRequest().getParameter("dittaProv");
      if(dittaProv!=null && !"".equals(dittaProv)){
        throw new GestoreException("Non e' possibile procedere perche' la gara risulta aggiudicata in via provvisoria", "aggiudicazioneFaseA.ControlloAggiudicazioneProvvisoria", null, null);
      }
    }


    String modGaraInversa=this.getRequest().getParameter("modGaraInversa");
    String dittap=this.getRequest().getParameter("dittaProv");
    String dittAggaDef=this.getRequest().getParameter("dittAggaDef");

    // Estrazione dalla sessione della HashMap usata per memorizzare gli oggetti
    // DataColumnContainer contenenti tutti i campi presenti nella popup per gli
    // ulteriori dettagli della ditta (anche se si è modificato un solo campo).
    // E' giusto ricordare che ogni DataColumnContainer contiene anche i campi
    // chiave dell'entita
    HashMap mappaCampiPopup = (HashMap) this.getRequest().getSession().getAttribute(
  			GestorePopupFasiGara.CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA);

    for (int i = 1; i <= numeroDitte; i++) {
      DataColumnContainer dataColumnContainerDiRiga = new DataColumnContainer(
      		impl.getColumnsBySuffix("_" + i, false));

      String codiceDitta = dataColumnContainerDiRiga.getString("DITG.DITTAO");
      codiceTornata = dataColumnContainerDiRiga.getString("DITG.CODGAR5");
      codiceLotto = dataColumnContainerDiRiga.getString("DITG.NGARA5");
      Long motivoEsclusione = dataColumnContainerDiRiga.getLong("V_DITGAMMIS.MOTIVESCL");
      String dettaglioMotivoEsclusione = dataColumnContainerDiRiga.getString("V_DITGAMMIS.DETMOTESCL");

      boolean isDITGModificata = dataColumnContainerDiRiga.isModifiedTable("DITG");
      // Flag per indicare se i campi di DITG presenti solo nella popup
      // 'Ulteriori dettagli' sono stati modificati o meno
      boolean isDITGModificataPopup = false;
      boolean isDITGAMMISModificata = dataColumnContainerDiRiga.isModifiedTable("V_DITGAMMIS");
      boolean isDITGSTATIModificata = false;

      // Campo chiave dell'entita' DITG con cui cercare in sessione eventuali
      // record modificati di tale entita' dalla popup 'Ulteriori dettagli'
      String keyDITG = codiceTornata + ";" + codiceDitta + ";" + codiceLotto;
      if(mappaCampiPopup != null && (! mappaCampiPopup.isEmpty())){
     		// Costruzione della chiave con cui reperire nella HashMap il
     		// DataColumnContainer che potrebbe contenere i dati modificati nella popup
     		if(mappaCampiPopup.containsKey(keyDITG))
     			isDITGModificataPopup = true;
     	}

      // Campo chiave dell'entita' DITGSTATI con cui cercare in sessione eventuali
      // record modificati di tale entita' dalla popup 'Ulteriori dettagli'

      String keyDITGSTATI = keyDITG + ";" + numeroStepAttivo;
    	if(mappaCampiPopup != null && (! mappaCampiPopup.isEmpty())){
    		// Costruzione della chiave con cui reperire nella HashMap il
    		// DataColumnContainer che potrebbe contenere i dati modificati nella
    		// popup dell'entita' DITGSTATI
    		if(mappaCampiPopup.containsKey(keyDITGSTATI))
    			isDITGSTATIModificata = true;
    	}

    	if(dataColumnContainerDiRiga.isColumn("DPROFF_FIT_NASCOSTO") && dataColumnContainerDiRiga.isColumn("DPROFF_FIT_MODIFICATO") && "SI".equals(dataColumnContainerDiRiga.getString("DPROFF_FIT_MODIFICATO"))){
          String dproffFit = dataColumnContainerDiRiga.getString("DPROFF_FIT_NASCOSTO");
          if(dproffFit!=null && !"".equals(dproffFit)){
            //La stringa ha il formato GG/MM/AAAA HH:MM per poterne fare la conversione si devono aggiungere i secondi
            dproffFit += ":00";
            Date dproff = UtilityDate.convertiData(dproffFit, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
            dataColumnContainerDiRiga.addColumn("DITG.DPROFF", new Timestamp(dproff.getTime()));
            isDITGModificata=true;
          }else{
            dataColumnContainerDiRiga.addColumn("DITG.DPROFF", JdbcParametro.TIPO_DATA, null);
            dataColumnContainerDiRiga.setOriginalValue("DITG.DPROFF", new JdbcParametro(JdbcParametro.TIPO_DATA, new Timestamp(1000)));
            isDITGModificata=true;
          }
        }

    if(isDITGModificata || isDITGAMMISModificata || isDITGSTATIModificata || isDITGModificataPopup) {
        isListaModificata = true;

      	if(isDITGAMMISModificata){
      		// Nel caso i campi della vista V_DITGAMMIS siano stati modificati,
      		// allora, oltre a salvarli nella tabella DITGAMMIS, bisogna allineare
      		// alcuni campi della DITG. In particolare i campi da allineare sono:
      		// AMMGAR, MOTIES, ANNOFF, FASGAR
      		this.gestioneDITGAMMIS(codiceTornata, codiceLotto, codiceDitta,
      				faseGaraLong, dataColumnContainerDiRiga.getColumns(
      						"V_DITGAMMIS", 2), status);

      		// Allineamento dello stato di ammissione dal campo DITGAMMIS.AMMGAR
      		// al campo DITG.AMMGAR (per compatibilita' con PWB e con i testi tipo)
      		this.aggiornaStatoAmmissioneDITG(codiceTornata, codiceLotto, codiceDitta,
      				faseGaraLong, dataColumnContainerDiRiga.getColumns(
      						"V_DITGAMMIS", 2), status);
      	}

      	if(isDITGSTATIModificata){
      		// Nel caso i campi della tabella DITGSTATI siano stati modificati,
      		// allora bisogna salvarli nella tabella stessa
      		this.gestioneDITGSTATI(codiceTornata, codiceLotto, codiceDitta,
      				numeroStepAttivo, faseGaraLong,
      				(DataColumnContainer) mappaCampiPopup.get(keyDITGSTATI), status);
      		// Rimozione dalla sessione dell'oggetto DatacolumnContainer
    			mappaCampiPopup.remove(keyDITGSTATI);
      	}

      	// Con l'istruzione this.aggiornaStatoAmmissioneDITG si e' effettuato
    		// l'allineamento dello stato di ammissione dal campo DITGAMMIS.AMMGAR
    		// al campo DITG.AMMGAR. Ora si aggiorna lo stesso DITG.AMMGAR in
      	// funzione del campo DITG.PARTGAR, secondo il seguente schema:
      	//
      	// | DITGAMMIS.AMMGAR | DITG.PARTGAR | DITG.AMMGAR |
				// -------------------------------------------------
				// |       Si         |      Si      |     Si      |
				// |       Si         |      No      |     No      |
				// |       No         |      No      |     No      |
				// Il caso mancante non ha significato
      	// In questo modo viene fatto un doppio aggiornamento di DITG.AMMGAR:
      	// questo ha semplificato molto la logica con cui si sarebbe dovuto
      	// aggiornare il campo
      	if(numeroStepAttivo.longValue() == GestioneFasiGaraFunction.FASE_VALUTAZIONE_TECNICA ||
      			numeroStepAttivo.longValue() == GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE ||
      			numeroStepAttivo.longValue() == GestioneFasiGaraFunction.FASE_ASTA_ELETTRONICA){
      		// Allineamento dello stato di ammissione dal campo DITG.AMMGAR nel
      		// caso in cui il campo DITG.PARTGAR sia stato modificato:
      		DataColumn campoDITG_PARTGAR = dataColumnContainerDiRiga.getColumn("DITG.PARTGAR");
      		DataColumn campoV_DITGAMMIS_AMMGAR = dataColumnContainerDiRiga.getColumn("V_DITGAMMIS.AMMGAR");
      		if("2".equals(campoDITG_PARTGAR.getValue().getStringValue()) ||
                "2".equals(campoV_DITGAMMIS_AMMGAR.getValue().getStringValue()) ||
                "6".equals(campoV_DITGAMMIS_AMMGAR.getValue().getStringValue()) ||
                "9".equals(campoV_DITGAMMIS_AMMGAR.getValue().getStringValue())){
      		    dataColumnContainerDiRiga.addColumn("DITG.AMMGAR", JdbcParametro.TIPO_TESTO);
      			dataColumnContainerDiRiga.getColumn("DITG.AMMGAR").setObjectValue("2");
      			dataColumnContainerDiRiga.getColumn("DITG.AMMGAR").setObjectOriginalValue(null);
        		// Aggiornamento del campo DITG.FASGAR con la fase da cui si sta
      			// escludendo la ditta (aggiornamento forzato settando a null
      			// l'originalValue)
      			if(dataColumnContainerDiRiga.isColumn("DITG.FASGAR")){
      				dataColumnContainerDiRiga.getColumn("DITG.FASGAR").setObjectValue(faseGaraLong);
        			dataColumnContainerDiRiga.getColumn("DITG.FASGAR").setObjectOriginalValue(null);
      			} else {
        			dataColumnContainerDiRiga.addColumn("DITG.FASGAR", JdbcParametro.TIPO_NUMERICO);
        			dataColumnContainerDiRiga.getColumn("DITG.FASGAR").setObjectValue(faseGaraLong);
        			dataColumnContainerDiRiga.getColumn("DITG.FASGAR").setObjectOriginalValue(null);
      			}
      		} else {

        		// Aggiornamento del campo DITG.FASGAR con la fase da cui si sta
      			// escludendo la ditta (aggiornamento forzato settando a null
      			// l'originalValue)
      			Long   valoreDITG_FASGAR = null;
      			try {
      			  valoreDITG_FASGAR = (Long)this.sqlManager.getObject(
                            "select FASGAR from ditg where CODGAR5=? and NGARA5=? and DITTAO=?",
                            new Object[]{codiceTornata, codiceLotto, codiceDitta});
                } catch (SQLException e) {
                    throw new GestoreException(
                            "Errore nella lettura dei dati originali di FASGAR " +
                            "dalla tabella DITG relativi alla ditta con codice '" + codiceDitta +
                            "' che partecipa alla gara con NGARA = '" + codiceLotto +
                            "'", null, e);
                }
                if(valoreDITG_FASGAR!= null && faseGaraLong.longValue() == valoreDITG_FASGAR.longValue()){
                  dataColumnContainerDiRiga.addColumn("DITG.AMMGAR", JdbcParametro.TIPO_TESTO);
                  dataColumnContainerDiRiga.getColumn("DITG.AMMGAR").setObjectValue("1");
                  dataColumnContainerDiRiga.getColumn("DITG.AMMGAR").setObjectOriginalValue(null);
                  if(dataColumnContainerDiRiga.isColumn("DITG.FASGAR")){
                      dataColumnContainerDiRiga.getColumn("DITG.FASGAR").setObjectValue(null);
                      dataColumnContainerDiRiga.getColumn("DITG.FASGAR").setObjectOriginalValue(new Long(1));
                  } else {
                      dataColumnContainerDiRiga.addColumn("DITG.FASGAR", JdbcParametro.TIPO_NUMERICO);
                      dataColumnContainerDiRiga.getColumn("DITG.FASGAR").setObjectValue(null);
                      dataColumnContainerDiRiga.getColumn("DITG.FASGAR").setObjectOriginalValue(new Long(1));
                  }
                }

      		}
      	}

      	if(isDITGModificataPopup){
      		// Se nella popup sono stati modificati alcuni campi dell'entita' DITG,
      		// allora si estrae dalla sessione il DataColumnContainer con i campi
      		// modificati nella popup 'Ulteriori dettagli' e li si aggiungono al
      		// DataColumnContainer, per salvarli
      		DataColumnContainer ulterioriCampiDITG = (DataColumnContainer)
      				mappaCampiPopup.get(keyDITG);
      		dataColumnContainerDiRiga.addColumns(ulterioriCampiDITG.getColumns("DITG", 2), false);
      		// Rimozione dalla sessione dell'oggetto DatacolumnContainer specifico
      		// dell'entita' DITG
      		mappaCampiPopup.remove(keyDITG);
      	}

      	//Gestione gara inversa
      	if( isDITGModificata && "true".equals(modGaraInversa)){
        	Long amminversa = dataColumnContainerDiRiga.getLong("DITG.AMMINVERSA");
        	Long staggi = dataColumnContainerDiRiga.getLong("DITG.STAGGI");
          if( dittap!=null && !"".equals(dittap) && (dittAggaDef==null || "".equals(dittAggaDef)) && new Long(2).equals(amminversa) && (new Long(4)).equals(staggi) ) {
            try {
              this.annullaAggiudicazionePrimeGaraInversa(codiceTornata, codiceLotto);
            } catch (SQLException e) {
              throw new GestoreException("Errore nell'annullamento dell'aggiudicazione provvisoria  " +
                  "(codice ditta = " + codiceDitta + ") della gara " + codiceLotto, null, e);
            }
          }
      	}


      	gestoreDITG.update(status, dataColumnContainerDiRiga);
        //isListaModificata = true;



        if (dataColumnContainerDiRiga.getLong("ESCLUDI_DITTA_ALTRI_LOTTI").longValue() == 1) {
        	PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
              this.getServletContext(), PgManager.class);

          switch(numeroStepAttivo.intValue()) {
          case GestioneFasiGaraFunction.FASE_APERTURA_DOCUM_AMMINISTR:

            try {
              // Esclusione della ditta dai lotti di gara non ancora esaminati
              pgManager.esclusioneDittaAltriLottiNonEsaminati(codiceDitta,
                  codiceTornata, codiceLotto, motivoEsclusione, dettaglioMotivoEsclusione);
            } catch(SQLException s){
              throw new GestoreException("Errore nell'esclusione della ditta " +
                  "(codice ditta = " + codiceDitta + ") da altri lotti della " +
                  "gara non ancora esaminati", null, s);
            }
          case GestioneFasiGaraFunction.FASE_ESITO_CONTROLLO_SORTEGGIATE:
          case GestioneFasiGaraFunction.FASE_VALUTAZIONE_TECNICA:
          case GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE:
            try{
              // Esclusione della ditta dai lotti di gara non ancora esaminati
              pgManager.esclusioneDittaAltriLottiNonEsaminati(codiceDitta,
                codiceTornata, codiceLotto, motivoEsclusione, dettaglioMotivoEsclusione);
            } catch(SQLException s){
              throw new GestoreException("Errore nell'esclusione della ditta " +
                  "(codice ditta = " + codiceDitta + ") da altri lotti della " +
                  "gara non ancora esaminati", null, s);
            }
            break;
          case GestioneFasiGaraFunction.FASE_SORTEGGIO_CONTROLLO_REQUISITI:
            break;
          }
        }

        //Per AUR si deve richiamare il webservice per Escludere e riammattere il fornitore
        //alla variazione di V_DITGAMMIS.AMMGAR, prendendo in considerazione solo
        //i valori =1(SI),2(NO)
        //Decisione presa con Cristian: per adesso trascurare la situazione di errore con
        //la gestione del rollback delle chiamate già effettuate
        Long acquisizione = null;
        if(dataColumnContainerDiRiga.isColumn("DITG.ACQUISIZIONE"))
          acquisizione = dataColumnContainerDiRiga.getLong("DITG.ACQUISIZIONE");
        boolean ammgarModified = dataColumnContainerDiRiga.isModifiedColumn("V_DITGAMMIS.AMMGAR");
        if(ammgarModified){
          Long ammgar = dataColumnContainerDiRiga.getLong("V_DITGAMMIS.AMMGAR");
          String tabellatoAmmgar;
          if(ammgar == null){
            tabellatoAmmgar = "";
          }else{
            tabellatoAmmgar = tabellatiManager.getDescrTabellato("A1054", ammgar.toString());
          }
          String descFase = tabellatiManager.getDescrTabellato("A1011",  faseGaraLong.toString());
          LogEvento logevento = LogEventiUtils.createLogEvento(this.getRequest());
          logevento.setLivEvento(1);
          logevento.setOggEvento(codiceLotto);
          logevento.setCodEvento("GA_MODIFICA_AMMISSIONE");
          logevento.setDescr("Modifica stato ammissione ditta '" + codiceDitta + "' nella fase "+descFase+" (valore assegnato: '"+tabellatoAmmgar+"')");
          logevento.setErrmsg("");
          LogEventiUtils.insertLogEventi(logevento);
        }
        if("1".equals(integrazioneAUR) && dataColumnContainerDiRiga.isColumn("V_DITGAMMIS.AMMGAR") && ammgarModified &&
            acquisizione != null && acquisizione.longValue()==12){
          Long ammgar = dataColumnContainerDiRiga.getLong("V_DITGAMMIS.AMMGAR");
          if(ammgar==null)
            ammgar = new Long(1);
          if(ammgar.longValue()==2 || ammgar.longValue()==1){
            Long ammgarOriginale = (Long)dataColumnContainerDiRiga.getColumn("V_DITGAMMIS.AMMGAR").getOriginalValue().getValue();
            int azione = 0;
            if(ammgar.longValue()==2)
              azione=1;
            else if(ammgarOriginale!=null && ammgar.longValue()==1 && ammgarOriginale.longValue()==2)
              azione=2;
            if(azione>0){

              //AurManager aurManager = (AurManager) UtilitySpring.getBean("aurManager",
              //    this.getServletContext(), AurManager.class);

              Long tipoAzione = new Long (azione);

              String urlWsAUR = ConfigManager.getValore("it.eldasoft.sil.pg.integrazioneAUR.ws.url");
              WsAURSoapProxy proxy=new WsAURSoapProxy(urlWsAUR);

              //Il codice Fornitore lo ottengo dal codice ditta, poichè
              //il codice ditta è nella forma: "A" + codice fornitore AUR
              String codiceFornitoreAUR = codiceDitta.substring(1);
              HashMap mappaDatiWebService = new HashMap();
              mappaDatiWebService.put("proxy",proxy);
              mappaDatiWebService.put("fornitore", codiceFornitoreAUR);
              mappaDatiWebService.put("codiceGara", codiceTornata);
              mappaDatiWebService.put("codiceLotto", codiceLotto);
              mappaDatiWebService.put("tipoAzione", tipoAzione);
              mappaDatiWebService.put("tipoEsclusione", motivoEsclusione);
              mappaDatiWebService.put("descrizioneEsclusione", dettaglioMotivoEsclusione);

              //Nel caso di gara ad offerta unica, si deve richiamare il webservice per tutti i lotti
              //tranne per la gara fittizia
              //poichè quando si acquisisce da AUR tutte le forniture vengono associate al lotto con
              //catiga = 1, richiamo il servizio solo per tale lotto.
              if(isGaraLottiConOffertaUnica){
                try {
                  /*
                  List listaCodiceLotti = this.sqlManager.getListVector(
                      "select NGARA from GARE where CODGAR1=? and NGARA<>?",
                      new Object[]{codiceTornata, codiceLotto});
                  if(listaCodiceLotti != null && listaCodiceLotti.size() > 0){
                    for(int j=0; j < listaCodiceLotti.size(); j++){
                      Vector tmpVect = (Vector) listaCodiceLotti.get(j);
                      String ngaraLotto = (String) ((JdbcParametro) tmpVect.get(0)).getValue();
                      mappaDatiWebService.put("codiceLotto", ngaraLotto);
                      aurManager.setEsclusioneFornitore(mappaDatiWebService);
                    }
                  }
                  */
                  String ngaraLotto = (String)this.sqlManager.getObject(
                      "select NGARA from GARE where CODGAR1=? and CODIGA=?",
                      new Object[]{codiceTornata, "1"});
                  if(ngaraLotto!=null && !"".equals(ngaraLotto)){
                    mappaDatiWebService.put("codiceLotto", ngaraLotto);
                    aurManager.setEsclusioneFornitore(mappaDatiWebService);
                  }

                } catch (SQLException e) {
                  throw new GestoreException("Errore nella chiamata al web service AUR_SetFornitoreEscluso per i lotti di una gara ad offerta unica","errors.aur.EsclusioneFornitore",e);
                }
              }else{
                try {
                  aurManager.setEsclusioneFornitore(mappaDatiWebService);
                } catch (SQLException e) {
                  throw new GestoreException("Errore nella chiamata al web service AUR_SetFornitoreEscluso","errors.aur.EsclusioneFornitore",e);
                }
              }

            }
          }
        }


      }
    }




    if (isListaModificata && numeroStepAttivo != GestioneFasiGaraFunction.FASE_ASTA_ELETTRONICA) {
    	PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
          this.getServletContext(), PgManager.class);

    	boolean eseguireAggiornamentoFase = true;
    	Long bustalotti = null;

    	try {
          //Long faseGaraAttuale = (Long)this.sqlManager.getObject("select fasgar from gare where ngara=?", new Object[]{codiceLotto});
          Vector datiGara = this.sqlManager.getVector("select fasgar,bustalotti from gare where ngara=?", new Object[]{codiceLotto});
          if(datiGara!=null && datiGara.size()>0){
            Long faseGaraAttuale = SqlManager.getValueFromVectorParam(datiGara, 0).longValue();
            bustalotti = SqlManager.getValueFromVectorParam(datiGara, 1).longValue();
            if("true".equals(modGaraInversa)){
              if(faseGaraAttuale!=null && faseGaraAttuale.longValue()>2)
                eseguireAggiornamentoFase=false;
            }else{
              if(bustalotti==null)
                bustalotti = (Long)this.sqlManager.getObject("select bustalotti from gare where ngara=? ", new Object[]{codiceTornata});
              if(numeroStepAttivo < GestioneFasiGaraFunction.FASE_VALUTAZIONE_TECNICA){
                if((!isGaraLottiConOffertaUnica || (isGaraLottiConOffertaUnica && bustalotti!=null && bustalotti.longValue()==1)) && faseGaraAttuale!=null && faseGaraAttuale.longValue()>=5)
                  eseguireAggiornamentoFase=false;
              }
            }
          }

        } catch (SQLException e) {
          throw new GestoreException("Errore nella lettura della fase di gara",null,e);
        }

    	if(eseguireAggiornamentoFase) {
    	  // Aggiornamento del FASGAR del lotto in modifica
          pgManager.aggiornaFaseGara(numeroStepAttivo, codiceLotto, false);
          Long valoreFasgarAggiornamento =  numeroStepAttivo;
          if(bustalotti!=null){
            valoreFasgarAggiornamento = pgManager.getStepAttivo(numeroStepAttivo, codiceTornata);
            // Aggiornamento del FASGAR dell'occorrenza complementare
            pgManager.aggiornaFaseGara(valoreFasgarAggiornamento, codiceTornata, false);
          }
    	}

    }else if(isListaModificata && numeroStepAttivo == GestioneFasiGaraFunction.FASE_ASTA_ELETTRONICA) {
      try {
        this.sqlManager.update("update gare set stepgar=? where ngara=?", new Object[]{new Long(GestioneFasiGaraFunction.FASE_ASTA_ELETTRONICA),codiceLotto});
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'aggiornamento dello STEPGAR della gara " + codiceLotto,null,e);
      }
    }


  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }






  public void annullaAggiudicazionePrimeGaraInversa(String codiceTornata, String codiceLotto) throws SQLException, GestoreException {
    this.sqlManager.update("update gare set dittap=null, ribpro=null, iagpro=null where ngara=? ", new Object[]{codiceLotto});

    String legRegSic = null;
    Long precut = null;
    Double limmax = null;
    Long nofval = null;

    Vector datiGara = this.sqlManager.getVector("select  LEGREGSIC, PRECUT, LIMMAX, NOFVAL  from gare, gare1 where gare.ngara=gare.ngara and gare.ngara=?", new Object[]{codiceLotto});
    if(datiGara!=null && datiGara.size()>0){
      legRegSic = SqlManager.getValueFromVectorParam(datiGara, 0).stringValue();
      precut = SqlManager.getValueFromVectorParam(datiGara, 1).longValue();
      limmax = SqlManager.getValueFromVectorParam(datiGara, 2).doubleValue();
      nofval = SqlManager.getValueFromVectorParam(datiGara, 3).longValue();
    }

    HashMap hMapTORN = new HashMap();
    HashMap hMapGARE = new HashMap();
    HashMap hMapParametri = new HashMap();

    this.aggManager.initControlliAggiudicazione(codiceLotto, codiceTornata, precut, legRegSic, limmax, nofval, hMapGARE, hMapTORN, hMapParametri);

    int modlicg = 0;
    if (hMapGARE.get("modlicg") != null)
       modlicg = ((Long) hMapGARE.get("modlicg")).intValue();

    boolean modalitaDL2016 = false;
    boolean modalitaDL2017 = false;
    boolean modalitaDLCalcoloGraduatoria=false;

    if (hMapParametri.get("modalitaDLCalcoloGraduatoria") != null)
      modalitaDLCalcoloGraduatoria = ((Boolean) hMapParametri.get("modalitaDLCalcoloGraduatoria")).booleanValue();

    if (hMapParametri.get("modalitaDL2016") != null)
      modalitaDL2016 = ((Boolean) hMapParametri.get("modalitaDL2016")).booleanValue();
    if (hMapParametri.get("modalitaDL2017") != null)
      modalitaDL2017 = ((Boolean) hMapParametri.get("modalitaDL2017")).booleanValue();

    if((modalitaDL2016 || modalitaDL2017) && (modlicg==13 || modlicg ==14) && !modalitaDLCalcoloGraduatoria){
      String descTab = this.tabellatiManager.getDescrTabellato("A1134", "1");
      if(descTab!=null && !"".equals(descTab)){
        descTab = descTab.substring(0, 1);
        if(!"0".equals(descTab) && !"1".equals(descTab) && !"2".equals(descTab))
          descTab= "2";
        hMapParametri.put("tabellatoA1134", descTab);
      }
    }

    String selectDITG = this.aggManager.impostaSelectDittePerCalcoloStaggi(hMapGARE, hMapParametri);

    this.aggManager.settaStaggi(codiceLotto, hMapTORN, hMapGARE, hMapParametri,
            selectDITG, 0, true, false);

  }
}