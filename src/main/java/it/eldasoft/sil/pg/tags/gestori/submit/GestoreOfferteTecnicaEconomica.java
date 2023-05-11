    /*
     * Created on 30/nov/09
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
import java.util.HashMap;
import java.util.Vector;

import org.springframework.transaction.TransactionStatus;

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
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiRicezioneFunction;
import it.eldasoft.sil.pg.tags.gestori.plugin.GestorePopupFasiGara;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

/**
 * Gestore non standard delle occorrenze dell'entita DITG presenti piu' volte
 * nelle pagine Offerta tecnica ed economica della ditta per una gara a lotti
 * con offerta unica (dettaglioOfferteDitta-OffertaUnicaLotti.jsp) la quale
 * contiene le ditte che partecipano ad una gara d'appalto, nelle diverse
 * fasi della gara stessa
 *
 * Questa classe NON e' un gestore standard e prepara i dati di ciascuna
 * occorrenza presente nella scheda e demanda alla classe DefaultGestoreEntita
 * le operazioni di insert, update e delete
 *
 * @author Luca.Giacomazzo
 */
public class GestoreOfferteTecnicaEconomica extends GestoreDITG {

  @Override
  public String getEntita() {
    return "DITG";
  }

  public GestoreOfferteTecnicaEconomica() {
    super(false);
  }

  public GestoreOfferteTecnicaEconomica(boolean isGestoreStandard) {
    super(isGestoreStandard);
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

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        this.getServletContext(), TabellatiManager.class);

    // Osservazione: si e' deciso di usare la classe DefaultGestoreEntita,
    // invece di creare la classe GestoreDITG come apposito gestore di entita',
    // perche' essa non avrebbe avuto alcuna logica di business
    int numeroDitte = 0;
    String numDitte = this.getRequest().getParameter("numeroDitte");
    if(numDitte != null && numDitte.length() > 0)
      numeroDitte =  UtilityNumeri.convertiIntero(numDitte).intValue();

    String isOffertaPerLotto = this.getRequest().getParameter("isOffertaPerLotto");

    String dettaglioPartecipazioneDittaPerLotti = this.getRequest().getParameter("dettaglioPartecipazioneDittaPerLotti");
    String dettaglioAmmissioneDittaPerLotti = this.getRequest().getParameter("dettaglioAmmissioneDittaPerLotti");
    String dettaglioInvioOffertoDittaPerLotti = this.getRequest().getParameter("dettaglioInvioOffertoDittaPerLotti");
    Long fasgar=null;

    Long numeroStepAttivo = new Long(UtilityStruts.getParametroString(
        this.getRequest(), GestioneFasiGaraFunction.PARAM_WIZARD_PAGINA_ATTIVA));
      // Il campo FASGAR, per compatibilita' con PWB e' sempre valorizzato come
      // il più grande intero che e' minore o uguale allo step attivo del wizard
      // diviso per 10
      Double faseGara = new Double(Math.floor(numeroStepAttivo.doubleValue()/10));
      Long faseGaraLong = new Long(faseGara.longValue());

    String codiceTornata = null;
    String codiceLotto = null;
    Long bustalotti = null;
    boolean listaDitgModificata= false;
    boolean aggiornareFasgarComplementarePerBustalotti2 = false;

    // Nel caso di dettaglio di partecipazione lanciato dallo step Apertura
    // domande di partecipazione, si deve controllare se la ditta ha presentato
    // offerta in RT, ed in caso positivo si deve bloccare il salvataggio.
    if("true".equals(dettaglioPartecipazioneDittaPerLotti) && numeroStepAttivo.longValue() == GestioneFasiRicezioneFunction.FASE_APERTURA_DOMANDE_DI_PARTECIPAZIONE){
      String numeroGara = this.getRequest().getParameter("numeroGara");
      String ditta = this.getRequest().getParameter("ditta");
      String codiceGara = this.getRequest().getParameter("codiceGara");
      try {
        String rtofferta = (String)this.sqlManager.getObject("select rtofferta from ditg where ngara5=? and codgar5=? and dittao=?",
            new Object[]{numeroGara, codiceGara, ditta});
        if(rtofferta!=null && !"".equals(rtofferta))
          throw new GestoreException(
              "La ditta risulta aver presentato offerta in raggruppamento temporaneo.Non è pertanto possibile modificare i dati.", "dettaglioPartecipazione.NoModificaOffertaRT"  , new Exception());
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella lettura del campo DITG.RTOFFERTA per determinare se la ditta ha presentato offerta in RT",   null, e);
      }
    }

    // Estrazione dalla sessione della HashMap usata per memorizzare gli oggetti
    // DataColumnContainer contenenti tutti i campi presenti nella popup per gli
    // ulteriori dettagli della ditta (anche se si è modificato un solo campo).
    // E' giusto ricordare che ogni DataColumnContainer contiene anche i campi
    // chiave dell'entita
    HashMap mappaCampiPopup = (HashMap) this.getRequest().getSession().getAttribute(
            GestorePopupFasiGara.CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA);


    String modGaraInversa=this.getRequest().getParameter("modGaraInversa");

    Long amminversa = null;;
    Long staggi = null;
    String dittap = null;
    String dittAggaDef = null;

    for (int i = 1; i <= numeroDitte; i++) {
      DataColumnContainer dataColumnContainerDiRiga = new DataColumnContainer(
            impl.getColumnsBySuffix("_" + i, false));

      String codiceDitta = dataColumnContainerDiRiga.getString("DITG.DITTAO");
      codiceTornata = dataColumnContainerDiRiga.getString("DITG.CODGAR5");
      codiceLotto = dataColumnContainerDiRiga.getString("NGARA5fit");
      // Aggiungo il campo DITG.NGARA5 nel DataColumnContainer, perche' nella
      // pagina, essendo il campo non modificabile e visibile, al salvataggio
      // non e' presente request. Per inserire il valore di tale campo nel
      // request e' stato aggiunto un campo fittizio con nome NGARA_+ suffisso

      // Aggiungo il campo DITG.NGARA5 nel DataColumnContainer, perche' nella
      // pagina, essendo il campo non modificabile e visibile, al salvataggio
      // non e' presente request. Per inserire il valore di tale campo nel
      // request e' stato aggiunto un campo fittizio con nome NGARA_+ suffisso
      dataColumnContainerDiRiga.addColumn("DITG.NGARA5", codiceLotto);
      dataColumnContainerDiRiga.getColumn("DITG.NGARA5").setChiave(true);
      dataColumnContainerDiRiga.getColumn("DITG.NGARA5").setObjectOriginalValue(codiceLotto);

      if(bustalotti == null) try {
        bustalotti = (Long)this.sqlManager.getObject("select bustalotti from gare where ngara=?", new Object[]{codiceTornata});
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella lettura del campo GARE.BUSTALOTTI della gara complementare",   null, e);
      }
      if(fasgar==null){
        try {
          fasgar = (Long)this.sqlManager.getObject("select fasgar from gare where ngara=?", new Object[]{codiceTornata});
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore nella lettura del campo GARE.FASGAR della gara complementare",   null, e);
        }
      }

      boolean isDITGModificata = dataColumnContainerDiRiga.isModifiedTable("DITG");
      // Flag per indicare se i campi di DITG presenti solo nella popup
      // 'Ulteriori dettagli' sono stati modificati o meno
      boolean isDITGModificataPopup = false;
      boolean isDITGAMMISModificata = dataColumnContainerDiRiga.isModifiedTable("V_DITGAMMIS");
      boolean isDITGSTATIModificata = false;

      if(isDITGModificata)
        listaDitgModificata=true;

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

      //if (dataColumnContainerDiRiga.isModifiedTable(this.getEntita())) {
      if(isDITGModificata || isDITGAMMISModificata || isDITGModificataPopup || isDITGSTATIModificata)   {
        boolean isDittaEsclusaGaraComplementare=false;
        if("true".equals(isOffertaPerLotto)){
          //Si deve valutare se a livello di gara la ditta è stata eliminata nella fase corrente.
          //Se ciò accade non si deve aggiornare lo stato di ditg.ammgar e ditg.fasgar
          try {
            String ammgarGaraComplementare = (String)this.sqlManager.getObject("select ammgar from ditg where codgar5=? and " +
            		"ngara5=? and dittao = ? and fasgar=?", new Object[]{codiceTornata,codiceTornata,codiceDitta, faseGaraLong});
            if("2".equals(ammgarGaraComplementare) || "6".equals(ammgarGaraComplementare))
              isDittaEsclusaGaraComplementare= true;
          } catch (SQLException e) {
            throw new GestoreException(
                "Errore nella lettura del campo DITG.AMMGAR della gara complementare",   null, e);
          }
        }

        boolean gestioneDettagioPartecipazione =  "true".equals(dettaglioPartecipazioneDittaPerLotti) && dataColumnContainerDiRiga.isModifiedColumn("DITG.PARTGAR")
            && "2".equals(dataColumnContainerDiRiga.getString("DITG.PARTGAR"));

        boolean gestioneDettagioInvioOff = "true".equals(dettaglioInvioOffertoDittaPerLotti) && dataColumnContainerDiRiga.isModifiedColumn("DITG.INVOFF");

        //Per la pagina del dettaglio partecipazione della ditta per i lotti si deve forzare la gestione di DITGAMMIS
        if(isDITGAMMISModificata || gestioneDettagioPartecipazione || gestioneDettagioInvioOff ||
            (dataColumnContainerDiRiga.isColumn("DITG.REQMIN") && dataColumnContainerDiRiga.isModifiedColumn("DITG.REQMIN"))){
                // Nel caso i campi della vista V_DITGAMMIS siano stati modificati,
                // allora, oltre a salvarli nella tabella DITGAMMIS, bisogna allineare
                // alcuni campi della DITG. In particolare i campi da allineare sono:
                // AMMGAR, MOTIES, ANNOFF, FASGAR
                Long fase = faseGaraLong;
                if(gestioneDettagioInvioOff)
                  fase = new Long(1);

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

                this.gestioneDITGAMMIS(codiceTornata, codiceLotto, codiceDitta,
                    fase, dataColumnContainerDiRiga.getColumns(
                                "V_DITGAMMIS", 2), status);

                if(gestioneDettagioPartecipazione || gestioneDettagioInvioOff  ){

                  if(!gestioneDettagioInvioOff){
                    //Si forza il valore del campo V_DITGAMMIS.AMMGAR per fare scattare la gestione nella funzione aggiornaStatoAmmissioneDITG
                    dataColumnContainerDiRiga.setValue("V_DITGAMMIS.AMMGAR", new Long(2));
                    //cancellazione delle occorrenze di DITGAMMIS con fase successiva a quella corrente
                    try {
                      this.sqlManager.update("delete from ditgammis where CODGAR=? and NGARA=? and DITTAO=? and FASGAR>?", new Object[]{
                          codiceTornata,codiceLotto, codiceDitta,fase});
                    } catch (SQLException e) {
                      throw new GestoreException(
                          "Errore nella cancellazione delle occorrenze di DITGAMMIS",   null, e);
                    }
                  }
                }
                if(!"true".equals(isOffertaPerLotto) || ("true".equals(isOffertaPerLotto) && !isDittaEsclusaGaraComplementare)){
                  // Allineamento dello stato di ammissione dal campo DITGAMMIS.AMMGAR
                  // al campo DITG.AMMGAR (per compatibilita' con PWB e con i testi tipo)
                  this.aggiornaStatoAmmissioneDITG(codiceTornata, codiceLotto, codiceDitta,
                      fase, dataColumnContainerDiRiga.getColumns(
                                  "V_DITGAMMIS", 2), status);
                }
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
        //Gestione del caso della modifica del partgar dalla pagina del dettaglio di partecipazione ai lotti,quando viene assegnato un valore !=2,
        //che non si riesce a fare scattare richiamando la aggiornaStatoAmmissioneDITG
        //Si deve controllare se la ditta è stata esclusa a livello di lotto in una fase successiva a quella attuale, ed eventualmente se ne deve impostare lo stato di ammissione ed anche
        //la fase nella ditta a livello di lotto.
        //Se la ditta non è esclusa a livello di gara ed non ci sono occorrenze in v_ditgammis relative all'esclusione, allora si sbiancano i il fasgar e l'ammgar della ditta.
        if("true".equals(dettaglioPartecipazioneDittaPerLotti) && dataColumnContainerDiRiga.isModifiedColumn("DITG.PARTGAR") && !"2".equals(dataColumnContainerDiRiga.getString("DITG.PARTGAR")) && !"6".equals(dataColumnContainerDiRiga.getString("DITG.PARTGAR"))){
          Long fasgarDittaGaraCompl=null;
          String ammgarDittaGaraCompl=null;
          boolean aggiornamentoForzato = false;
          try {
            Vector datiDittaGaraCompl = this.sqlManager.getVector("select fasgar,ammgar from ditg where ngara5=? and codgar5=? and dittao=? and (ammgar='2') and fasgar>?",
              new Object[]{ codiceTornata,codiceTornata,codiceDitta,faseGaraLong});
            if(datiDittaGaraCompl!= null && datiDittaGaraCompl.size()>0){
              fasgarDittaGaraCompl = (Long)((JdbcParametro) datiDittaGaraCompl.get(0)).getValue();
              ammgarDittaGaraCompl = (String)((JdbcParametro) datiDittaGaraCompl.get(1)).getValue();
            }

            Long ammgarV_DITGAMMIS = dataColumnContainerDiRiga .getLong("V_DITGAMMIS.AMMGAR");
            if(!"2".equals(ammgarDittaGaraCompl) && (ammgarV_DITGAMMIS==null || (ammgarV_DITGAMMIS.longValue()!=2 && ammgarV_DITGAMMIS.longValue()!= 6 && ammgarV_DITGAMMIS.longValue()!= 9))){
              fasgarDittaGaraCompl = null;
              ammgarDittaGaraCompl = null;
              aggiornamentoForzato = true;
            }

          } catch (SQLException e) {
            throw new GestoreException("Errore nell'allineamento dei dati della ditg per un lotto di una gara ad offerta unica", null, e);
          }

          if(fasgarDittaGaraCompl!= null || aggiornamentoForzato){
            dataColumnContainerDiRiga.addColumn("DITG.AMMGAR", JdbcParametro.TIPO_TESTO);
            dataColumnContainerDiRiga.getColumn("DITG.AMMGAR").setObjectValue(ammgarDittaGaraCompl);
            dataColumnContainerDiRiga.getColumn("DITG.AMMGAR").setObjectOriginalValue("-1");
            // Aggiornamento del campo DITG.FASGAR con la fase da cui si sta
            // escludendo la ditta (aggiornamento forzato settando a null
            // l'originalValue)
            if(dataColumnContainerDiRiga.isColumn("DITG.FASGAR")){
                dataColumnContainerDiRiga.getColumn("DITG.FASGAR").setObjectValue(fasgarDittaGaraCompl);
                dataColumnContainerDiRiga.getColumn("DITG.FASGAR").setObjectOriginalValue(new Long(1));
            } else {
                dataColumnContainerDiRiga.addColumn("DITG.FASGAR", JdbcParametro.TIPO_NUMERICO);
                dataColumnContainerDiRiga.getColumn("DITG.FASGAR").setObjectValue(fasgarDittaGaraCompl);
                dataColumnContainerDiRiga.getColumn("DITG.FASGAR").setObjectOriginalValue(new Long(1));
            }
          }
        }
        if(numeroStepAttivo.longValue() == GestioneFasiRicezioneFunction.FASE_APERTURA_DOMANDE_DI_PARTECIPAZIONE ){
          DataColumn campoDITG_PARTGAR = dataColumnContainerDiRiga.getColumn("DITG.PARTGAR");
          if("2".equals(campoDITG_PARTGAR.getValue().getStringValue())){
            if(!dataColumnContainerDiRiga.isColumn("DITG.INVGAR"))
              dataColumnContainerDiRiga.addColumn("DITG.INVGAR", JdbcParametro.TIPO_TESTO);
            dataColumnContainerDiRiga.getColumn("DITG.INVGAR").setObjectValue(null);
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


        gestoreDITG.update(status, dataColumnContainerDiRiga);

        if(!"true".equals(dettaglioPartecipazioneDittaPerLotti) && !"true".equals(dettaglioAmmissioneDittaPerLotti) && !"true".equals(dettaglioInvioOffertoDittaPerLotti)){
          // Aggiornamento del FASGAR di GARE del lotto in modifica
          pgManager.aggiornaFaseGara(numeroStepAttivo, codiceLotto, false);
          //Se bustalotti=2 si deve aggiornare anche la fase della gara complementare
          if(bustalotti!=null && bustalotti.longValue()==2){
            aggiornareFasgarComplementarePerBustalotti2 =true;
        }

      }
    }

    //Nel caso della pagina del dettaglio di partecipazione ditta per lotti non si aggiorna la fase dei lotti
    //ma la sola fase della gara e solo se la fase < 5.
    //Se la pagina viene aperta dalla fase "Apertura domande di partecipazione" , si aggiorna se la fase è < 2
    //Lo stesso vale per la pagina del dettaglio ammissione ditta ai lotti
    if((listaDitgModificata && "true".equals(dettaglioPartecipazioneDittaPerLotti)) ||
        ("true".equals(dettaglioAmmissioneDittaPerLotti) && isDITGAMMISModificata)){
      int valoreFaseConfronto =5;
      if(numeroStepAttivo.longValue() == GestioneFasiRicezioneFunction.FASE_APERTURA_DOMANDE_DI_PARTECIPAZIONE)
        valoreFaseConfronto =2;
      if(fasgar!=null && fasgar.longValue()<valoreFaseConfronto ){
        Long valoreStepAggiornamento = pgManager.getStepAttivo(numeroStepAttivo, codiceTornata);
        pgManager.aggiornaFaseGara(valoreStepAggiornamento, codiceTornata, false);
      }
    }

    //Gestione gara inversa
    if( isDITGModificata && "true".equals(modGaraInversa)){
      amminversa = dataColumnContainerDiRiga.getLong("DITG.AMMINVERSA");
      staggi = dataColumnContainerDiRiga.getLong("DITG.STAGGI");
      dittap = dataColumnContainerDiRiga.getString("GARE.DITTAP");
      dittAggaDef = dataColumnContainerDiRiga.getString("GARE.DITTA");
      if( dittap!=null && !"".equals(dittap) && (dittAggaDef==null || "".equals(dittAggaDef)) && new Long(2).equals(amminversa) && (new Long(4)).equals(staggi) ) {
        GestoreFasiGara gestoreFasiGara = new GestoreFasiGara();
        gestoreFasiGara.setRequest(this.getRequest());
        try {
          gestoreFasiGara.annullaAggiudicazionePrimeGaraInversa(codiceTornata, codiceLotto);
        } catch (SQLException e) {
          throw new GestoreException("Errore nell'annullamento dell'aggiudicazione provvisoria  " +
              "(codice ditta = " + codiceDitta + ") della gara " + codiceLotto, null, e);
        }
      }
    }

   }
    //Aggiornameno fase della gara complementare quando bustalotti=2
    if(aggiornareFasgarComplementarePerBustalotti2){
      Long valoreFasgarAggiornamento = pgManager.getStepAttivo(numeroStepAttivo, codiceTornata);
      pgManager.aggiornaFaseGara(valoreFasgarAggiornamento, codiceTornata, false);
    }
  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

}