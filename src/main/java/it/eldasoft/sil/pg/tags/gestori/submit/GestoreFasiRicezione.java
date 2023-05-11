/*
 * Created on 06/nov/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import com.lowagie.text.DocumentException;

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
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AurManager;
import it.eldasoft.sil.pg.bl.ElencoOperatoriManager;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiRicezioneFunction;
import it.eldasoft.sil.pg.tags.gestori.plugin.GestorePopupFasiGara;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;
import pc_nicola.aur.WebServices.WsAURSoapProxy;

/**
 * Gestore non standard delle occorrenze dell'entita DITG presenti piu' volte
 * nelle pagine delle Fasi Ricezione domande e offerte (gare-pg-fasiRicezione.jsp)
 * la quale contiene le ditte che partecipano ad una gara d'appalto, nelle
 * diverse fasi di ricezione delle domande e offerte
 *
 * Questa classe NON e' un gestore standard e prepara i dati di ciascuna
 * occorrenza presente nella scheda e demanda alla classe DefaultGestoreEntita
 * le operazioni di insert, update e delete
 *
 * @author Luca.Giacomazzo
 */
public class GestoreFasiRicezione extends GestoreDITG {

  private AurManager aurManager;

  private ElencoOperatoriManager elencoOperatoriManager;

  private TabellatiManager tabellatiManager;

  private AbstractGestoreEntita gestoreDITG;


  /** Logger */
  static Logger            logger           = Logger.getLogger(GestoreFasiRicezione.class);

  @Override
  public String getEntita() {
    return "DITG";
  }

  public GestoreFasiRicezione() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestoreFasiRicezione(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per gestire diversi SQL
    this.aurManager = (AurManager) UtilitySpring.getBean("aurManager",
        this.getServletContext(), AurManager.class);
    // Estraggo il manager per gestire diversi SQL
    this.elencoOperatoriManager = (ElencoOperatoriManager) UtilitySpring.getBean("elencoOperatoriManager",
        this.getServletContext(), ElencoOperatoriManager.class);
    this.tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        this.getServletContext(), TabellatiManager.class);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer
      dataColumnContainer) throws GestoreException {
    super.preDelete(status, dataColumnContainer);

    String codiceDitta = dataColumnContainer.getString("DITG.DITTAO");
    String numeroGara =  dataColumnContainer.getString("DITG.NGARA5");
    String codiceTornata = dataColumnContainer.getString("DITG.CODGAR5");

    int livEvento = 1;
    String codEvento = "GA_ELIMINA_DITTA";
    String oggEvento = numeroGara;
    String descrEvento = "Eliminazione ditta dalla gara (cod."+ codiceDitta +")";
    String errMsgEvento = "";

    try{

      boolean isGaraLottiConOffertaUnica = false;
      String tmp = this.getRequest().getParameter("isGaraLottiConOffertaUnica");
      if(tmp == null)
          tmp = (String) this.getRequest().getAttribute("isGaraLottiConOffertaUnica");

      if("true".equals(tmp))
          isGaraLottiConOffertaUnica = true;

      if(isGaraLottiConOffertaUnica){
        this.getGeneManager().deleteTabelle(new String[]{"DITG"},
      "CODGAR5 = ? and DITTAO = ?", new Object[]{codiceTornata, codiceDitta});
      }else{
        this.getGeneManager().deleteTabelle(new String[]{"DITG"},
            "CODGAR5 = ? and DITTAO = ? and NGARA5 = ?",
                  new Object[]{codiceTornata, codiceDitta, numeroGara});
      }
    }finally{

      LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
      logEvento.setLivEvento(livEvento);
      logEvento.setOggEvento(oggEvento);
      logEvento.setCodEvento(codEvento);
      logEvento.setDescr(descrEvento);
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);

    }
 }

  @Override
  public void postDelete(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {

  	// Gestione dell'inserimento di una ditta in una gara
    //DefaultGestoreEntita gestoreDITG = new DefaultGestoreEntita("DITG", this.getRequest());
    if(this.getRequest()!=null)
      gestoreDITG = new GestoreEntita("DITG", this.getRequest());
    else
      gestoreDITG = new GestoreEntita("DITG", this.sqlManager,this.geneManager);

    String numeroGara =  dataColumnContainer.getString("DITG.NGARA5");

    //Long numeroFaseAttiva = new Long(UtilityStruts.getParametroString(this.getRequest(), GestioneFasiGaraFunction.PARAM_WIZARD_PAGINA_ATTIVA));
    Long numeroFaseAttiva = null;
    if(this.getRequest()!=null)
      numeroFaseAttiva = new Long(UtilityStruts.getParametroString(this.getRequest(), GestioneFasiGaraFunction.PARAM_WIZARD_PAGINA_ATTIVA));


    if(dataColumnContainer.isColumn("DPRDOM_FIT_NASCOSTO") && dataColumnContainer.getColumn("DPRDOM_FIT_NASCOSTO").isModified()){
      String dprdomFit = dataColumnContainer.getString("DPRDOM_FIT_NASCOSTO");
      if(dprdomFit!=null && !"".equals(dprdomFit)){
        //La stringa ha il formato GG/MM/AAAA HH:MM per poterne fare la conversione si devono aggiungere i secondi
        dprdomFit += ":00";
        Date dprdom = UtilityDate.convertiData(dprdomFit, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
        dataColumnContainer.setValue("DITG.DPRDOM", new Timestamp(dprdom.getTime()));
      }
    }

    if(dataColumnContainer.isColumn("DPROFF_FIT_NASCOSTO") && dataColumnContainer.getColumn("DPROFF_FIT_NASCOSTO").isModified()){
      String dproffFit = dataColumnContainer.getString("DPROFF_FIT_NASCOSTO");
      if(dproffFit!=null && !"".equals(dproffFit)){
        //La stringa ha il formato GG/MM/AAAA HH:MM per poterne fare la conversione si devono aggiungere i secondi
        dproffFit += ":00";
        Date dproff = UtilityDate.convertiData(dproffFit, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
        dataColumnContainer.setValue("DITG.DPROFF", new Timestamp(dproff.getTime()));

      }
    }

    boolean saltareTracciatura=false;
    if(dataColumnContainer.isColumn("SALTARE_TRACCIATURA")) {
      if("true".equals(dataColumnContainer.getString("SALTARE_TRACCIATURA")))
        saltareTracciatura=true;
    }

    Long genere = null;
    Long iterga = null;
    try {
      String select = "select iterga,genere from gare,torn where ngara = ? and codgar1=codgar";
      List datiGara = this.getSqlManager().getListVector( select,new Object[] { numeroGara });
      if(datiGara != null && datiGara.size() > 0){
        Vector dati = (Vector) datiGara.get(0);
        iterga = (Long) ((JdbcParametro) dati.get(0)).getValue();
        genere = (Long) ((JdbcParametro) dati.get(1)).getValue();
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura dei dati della gara", null,e);
    }

    Long acquisiz=null;
    if(dataColumnContainer.isColumn("DITG.ACQUISIZIONE")){
      acquisiz = dataColumnContainer.getLong("DITG.ACQUISIZIONE");
      if(new Long(9).equals(acquisiz)) {
        dataColumnContainer.addColumn("DITG.AMMGAR", JdbcParametro.TIPO_TESTO, "2");
      }
    }
    String dittainv= null;
    if(dataColumnContainer.isColumn("DITG.DITTAINV")){
      dittainv = dataColumnContainer.getString("DITG.DITTAINV");
    }


    super.preInsert(status, dataColumnContainer);


    String inserimentoDitteIterSemplificato="";
    if(this.getRequest()!=null) {
      inserimentoDitteIterSemplificato = this.getRequest().getParameter("inserimentoDitteIterSemplificato");
      if(inserimentoDitteIterSemplificato == null)
        inserimentoDitteIterSemplificato = (String) this.getRequest().getAttribute("inserimentoDitteIterSemplificato");
    }


    if ("SI".equals(inserimentoDitteIterSemplificato)){
      //Personalizzazione iter semplificato di gara
      //Se TIPGARG.GARE = 1 si deve valorizzare INVOFF a 1
      if(iterga!= null && iterga.longValue()==1)
        dataColumnContainer.addColumn("DITG.INVOFF", JdbcParametro.TIPO_NUMERICO, new Long(1));
    }

    String codiceGara =  dataColumnContainer.getString("DITG.CODGAR5");
    String ditta =  dataColumnContainer.getString("DITG.DITTAO");

    int livEvento = 1;
    String codEvento = "GA_AGGIUNGI_DITTA";
    String oggEvento = numeroGara;
    String descrEvento = "Inserimento ditta in gara mediante selezione da anagrafica (cod. "+ ditta +")";
    String errMsgEvento = "";
    if(new Long(8).equals(acquisiz))
      descrEvento += ". Inserimento successivo alla fase di prequalifica";
    else if(new Long(9).equals(acquisiz))
      descrEvento += ". Inserimento a gara in corso";

    if(dataColumnContainer.isColumn("GARARILANCIO")){
      codEvento = "GA_AGGIUNGI_DITTA_RIL";
      descrEvento = "Inserimento ditta in gara mediante selezione per rilancio (cod. "+ ditta +").";
    }
    try{

    //Il controllo sulla duplicazione della ditta viene fatto in GestoreDITG
    /*
    try {
      //Prima di inserire l'occorrenza si controlla se non esiste già in database
      //una occorrenza di DITG
      //Recupera il genere della gara per differenziare il msg di errore nel caso di elenchi
      String avvisoDittaDuplicata="gara";
      if(genere!= null && genere.longValue()==10){
        avvisoDittaDuplicata="elenco";
      }
      String select="select count(ngara5) from ditg where ngara5=? and codgar5=? and dittao=?";
      Long count = (Long) this.geneManager.getSql().getObject(
					select,new Object[] { numeroGara,codiceGara,ditta });
      if (count.longValue() == 0)	{
		 //Nella preInsert sono state inserite le occorrenze in DPRE e nella EDIT
		 //e sono stati popolati i campi per la DITG.
		 //Si effettua qui l'inserimento che prima veniva effettuato nella preInsert
		 //di modo che GestoreDITG sia standard
		  gestoreDITG.inserisci(status, dataColumnContainer);
		} else {
  		  throw new GestoreException("La ditta selezionata risulta già inserita in " + avvisoDittaDuplicata,
  		      "aggiungiDitta", new Object[] {avvisoDittaDuplicata}, null);
	  	}
	} catch (SQLException e) {
		throw new GestoreException("Errore nel controllo ditta duplicata", null, e);
	}
  */



    //Nella preInsert sono state inserite le occorrenze in DPRE e nella EDIT
    //e sono stati popolati i campi per la DITG.
    //Si effettua qui l'inserimento che prima veniva effettuato nella preInsert
    //di modo che GestoreDITG sia standard
     gestoreDITG.inserisci(status, dataColumnContainer);

	//Quando si inserisce una ditta in gara si deve in automatico popolare IMPRDOCG
	//a partire dalle occorrenze di DOCUMGARA, impostando SITUAZDOCI a 2 e PROVENI a 1
	pgManager.inserimentoDocumentazioneDitta(codiceGara, numeroGara, ditta);

    //Se si tratta di un Elenco Operatori occorre inserire la riga
	// per la categoria generica
    if(genere!= null && (genere.longValue()==10 || genere.longValue()==20)){
      try {
        String tipoElenco = (String) sqlManager.getObject("select tipoele from garealbo where ngara = ?", new Object[]{numeroGara});
        // Se si tratta di un Elenco Operatori occorre inserire anche una riga su iscrizcat
        // con categoria generica - codcat 0 - che l'utente non vede
        pgManager.inserimentoCategoriaGenerica(codiceGara, numeroGara, tipoElenco, ditta);
      } catch (SQLException e) {
        livEvento = 3;
        errMsgEvento = e.getMessage();
        throw new GestoreException(
            "Errore durante l'inserimento della riga categoria generica  ", null,  e);
      }
    }

    //String offertaRT = UtilityStruts.getParametroString(this.getRequest(), "offertaRT");
    String offertaRT = "";
    if(this.getRequest()!=null)
      offertaRT = UtilityStruts.getParametroString(this.getRequest(), "offertaRT");

	// Se la gara e' di tipo 'a lotti con offerta unica' bisogna associare la
    // ditta ad ogni lotto esistente, oltre alla occorrenza complementare in GARE
    boolean isGaraLottiConOffertaUnica = false;
    String tmp ="";
    if(this.getRequest()!=null) {
      tmp = this.getRequest().getParameter("isGaraLottiConOffertaUnica");
      if(tmp == null)
      	tmp = (String) this.getRequest().getAttribute("isGaraLottiConOffertaUnica");
    }

    if("true".equals(tmp))
    	isGaraLottiConOffertaUnica = true;

    if(isGaraLottiConOffertaUnica){
	    try {
	      String ammgar = null;
	      String partgar = null;
	      Long fasgar = null;
	      String invgar = null;

	        // Estrazione di NGARA e IMPAPP dei lotti della gara con offerta unica,
	    	// Per inserire la ditta in ciascun lotto
		    List listaLotti = this.sqlManager.getListVector(
		    		"select NGARA, IMPAPP from GARE " +
		    		 "where CODGAR1 = ? and NGARA <> CODGAR1 and GENERE is null",
		    		 new Object[]{numeroGara});

		    if(listaLotti != null && listaLotti.size() > 0){
		    	// Campi da valorizzare nell'inserimento della ditta per i diversi
		    	// lotti della gara
		    	String campiDaInserire = "DITG.CODGAR5,DITG.NOMIMO,DITG.NPROGG,DITG.DITTAO," +
		    			"DITG.NGARA5,DITG.IMPAPPD,DITG.NUMORDPL";

		    	HashMap hm = dataColumnContainer.getColonne();
		    	String[] campiDaRimuovere = new String[hm.size() - campiDaInserire.split(",").length];
		    	Set chiaviMap = hm.keySet();
		    	Iterator iter = chiaviMap.iterator();
		    	int indice = 0;

		    	// Ciclo sui campi presenti nel DataColumnContainer per determinare i
		    	// quelli da non inserire
		    	while(iter.hasNext()){
		    		String tmpChiaveMap = (String) iter.next();
		    		if(campiDaInserire.indexOf(tmpChiaveMap.toUpperCase()) < 0){
		    			campiDaRimuovere[indice] = tmpChiaveMap;
		    			indice++;
		    		}
		    	}
		    	// Rimozione dei campi diversi da quelli presenti nella variabile
		    	// campiDaInserire
		    	dataColumnContainer.removeColumns(campiDaRimuovere);

		    	for(int i=0; i < listaLotti.size(); i++){
		    		Vector lotto = (Vector) listaLotti.get(i);
		    		String tmpCodiceLotto = (String)((JdbcParametro) lotto.get(0)).getValue();
		    		Double tmpImpApp = (Double)((JdbcParametro) lotto.get(1)).getValue();
		    		dataColumnContainer.setValue("DITG.NGARA5", tmpCodiceLotto);
		    		dataColumnContainer.setValue("DITG.IMPAPPD", tmpImpApp);
		    		dataColumnContainer.addColumn("DITG.NCOMOPE", "1");
		    		if("1".equals(offertaRT)){
		    		  //Nel caso di presentazione offerta in RT si devono riportare nei lotti i valori di
		              //PARTGAR, AMMGAR e FASGAR della ditta del lotto corrispondente
		    		  String mandataria ="";
		    		  if(this.getRequest()!=null)
		    		    mandataria = UtilityStruts.getParametroString(this.getRequest(), "codiceDitta");
		              Vector datiDitta = this.sqlManager.getVector("select partgar, ammgar, fasgar, invgar from ditg where ngara5=? and codgar5=? and dittao=?",
		                  new Object[]{tmpCodiceLotto, codiceGara, mandataria});
		              invgar = null;
		              if(datiDitta!=null && datiDitta.size()>0){
		                partgar = SqlManager.getValueFromVectorParam(datiDitta, 0).stringValue();
		                ammgar = SqlManager.getValueFromVectorParam(datiDitta, 1).stringValue();
		                fasgar = SqlManager.getValueFromVectorParam(datiDitta, 2).longValue();
		                invgar = SqlManager.getValueFromVectorParam(datiDitta, 3).stringValue();
		                dataColumnContainer.addColumn("DITG.PARTGAR", partgar);
	                    dataColumnContainer.addColumn("DITG.AMMGAR", ammgar);
	                    dataColumnContainer.addColumn("DITG.FASGAR", fasgar);
	                    dataColumnContainer.addColumn("DITG.INVGAR", invgar);
	                    dataColumnContainer.addColumn("DITG.DITTAINV", dittainv);

		              }

		    		}else{
		    		  String invito="1";
		    		  if(new Long(9).equals(acquisiz)) {
		    		    invito = "2";
		    		    dataColumnContainer.addColumn("DITG.AMMGAR", "2");
		    		    dataColumnContainer.addColumn("DITG.ACQUISIZIONE", new Long(9));
		    		  }
		    		  dataColumnContainer.addColumn("DITG.INVGAR", invito);
		    		}
		    		gestoreDITG.inserisci(status, dataColumnContainer);
		    		/*
		    		if("1".equals(offertaRT)){
		    		  //Nel gestore DITG all'inserimento viene impostato INVGAR=1, ma si deve riportare in realtà il valore
		    		  //letto dal db
		    		  this.sqlManager.update("update DITG set INVGAR = ? where CODGAR5=? and NGARA5=? and DITTAO=?",
	                        new Object[]{invgar,codiceGara, tmpCodiceLotto,ditta});
		    		}
		    		*/
		    	}
		    }
	    } catch(SQLException s){
	        livEvento = 3;
	        errMsgEvento = s.getMessage();
	    	throw new GestoreException("Errore nell'inserimento della ditta nei " +
	    			"lotti della gara ", null, s);
	    }
    }

    //Gestione Presentazione offerta in RT
    if("1".equals(offertaRT)){
      String mandataria = "";
      if(this.getRequest()!=null)
        mandataria = UtilityStruts.getParametroString(this.getRequest(), "codiceDitta");
      if(mandataria!=null && !"".equals(mandataria)){
        Double faseGara = new Double(Math.floor(GestioneFasiRicezioneFunction.FASE_RICEZIONE_PLICHI/10));
        Long faseGaraLong = new Long(faseGara.longValue());

        try {
          /*
          this.sqlManager.update("update ditg set invoff=?, nproff=null, datoff=null, oraoff=null, mezoff=null, plioff=null, "
              + "notpoff=null, rtofferta=?, partgar=?, ribauo=null, impoff = null, puneco= null, impsicazi = null where ngara5=? "
              + "and codgar5=? and dittao=?", new Object[]{"2",ditta,"1",numeroGara,codiceGara,mandataria});




          DataColumn d1 = new DataColumn("V_DITGAMMIS.MOTIVESCL", new JdbcParametro(
              JdbcParametro.TIPO_DECIMALE, null));
          DataColumn d2 = new DataColumn("V_DITGAMMIS.AMMGAR", new JdbcParametro(
              JdbcParametro.TIPO_NUMERICO, new Long(2)));
          d2.setObjectOriginalValue(new Long(1));
          DataColumn d3 = new DataColumn("V_DITGAMMIS.DETMOTESCL", new JdbcParametro(
              JdbcParametro.TIPO_TESTO, null));

          this.gestioneDITGAMMIS(codiceGara, numeroGara, mandataria,
              faseGaraLong, new DataColumn[]{d1,d2,d3}, status);

          this.aggiornaStatoAmmissioneDITG(codiceGara, numeroGara, mandataria,
              faseGaraLong, new DataColumn[]{d1,d2,d3}, status);
           */

          this.escludiDittaOfferta(ditta, numeroGara, codiceGara, mandataria, faseGaraLong, status);

          if(dataColumnContainer.isColumn("OFFERTALOTTI")){
            String applicareOffertaALotti = dataColumnContainer.getString("OFFERTALOTTI");
            if("1".equals(applicareOffertaALotti)){
              List listaLotti = this.sqlManager.getListVector("select ngara from gare,ditg where codgar1=? and ngara <>? and codgar5=codgar1"
                  + " and ngara5=ngara and dittao=? and (ammgar=? or ammgar is null)", new Object[]{codiceGara,numeroGara,mandataria,"1"});
              if(listaLotti!=null && listaLotti.size()>0){
                String lotto = null;

                // Campi da valorizzare nell'inserimento della ditta per i diversi
                // lotti della gara
                String campiDaInserire = "DITG.CODGAR5,DITG.NOMIMO,DITG.NPROGG,DITG.DITTAO," +
                        "DITG.NGARA5,DITG.IMPAPPD,DITG.NUMORDPL,DITG.INVGAR,DITG.CATIMOK";

                HashMap hm = dataColumnContainer.getColonne();
                String[] campiDaRimuovere = new String[hm.size() - campiDaInserire.split(",").length];
                Set chiaviMap = hm.keySet();
                Iterator iter = chiaviMap.iterator();
                int indice = 0;

                // Ciclo sui campi presenti nel DataColumnContainer per determinare i
                // quelli da non inserire
                while(iter.hasNext()){
                    String tmpChiaveMap = (String) iter.next();
                    if(campiDaInserire.indexOf(tmpChiaveMap.toUpperCase()) < 0){
                        campiDaRimuovere[indice] = tmpChiaveMap;
                        indice++;
                    }
                }
                // Rimozione dei campi diversi da quelli presenti nella variabile
                // campiDaInserire
                dataColumnContainer.removeColumns(campiDaRimuovere);
                if(dataColumnContainer.isColumn("DITG.ACQUISIZIONE"))
                  dataColumnContainer.setValue("DITG.ACQUISIZIONE", new Long(5));
                else
                  dataColumnContainer.addColumn("DITG.ACQUISIZIONE", new Long(5));
                for(int i=0; i < listaLotti.size(); i++){

                  lotto = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).getStringValue();
                  dataColumnContainer.setValue("DITG.NGARA5", lotto);
                  gestoreDITG.inserisci(status, dataColumnContainer);
                  String codiceRaggruppamento = dataColumnContainer.getString("DITG.DITTAO");

                  pgManager.inserimentoDocumentazioneDitta(codiceGara, lotto, codiceRaggruppamento);
                  this.escludiDittaOfferta(codiceRaggruppamento, lotto, codiceGara, mandataria, faseGaraLong, status);
                }
              }
            }
          }
        } catch (SQLException e) {
          livEvento = 3;
          errMsgEvento = e.getMessage();
          throw new GestoreException("Errore nell'aggiornamento dei dati della mandataria del raggruppamento: " +
              ditta , null, e);
        }
      }
    }

    // Aggiornamento GARE.FASGAR e GARE.STEPGAR, solo se non si è già passati
    // alle fasi di gara, ovvero solo se FASGAR.GARE < 2
    if(!new Long(9).equals(acquisiz))
      pgManager.aggiornaFaseGara(numeroFaseAttiva, numeroGara, true);

    // Se l'operazione di insert e' andata a buon fine (cioe' nessuna
    // eccezione) inserisco nel request l'attributo RISULTATO valorizzato con
    // "OK", che permettera' alla popup di inserimento ditta di richiamare
    // il refresh della finestra padre e di chiudere se stessa
    if(this.getRequest()!=null)
      this.getRequest().setAttribute("RISULTATO", "OK");
    }catch(Exception e) {
      String msg=e.getMessage();
      throw e;
    }finally{
      boolean traccia = true;
      if((acquisiz != null && (acquisiz.intValue() == 3)) || saltareTracciatura){
        traccia = false;
      }
      if(traccia){
        LogEvento logEvento = null;
        if(this.getRequest()!=null)
          logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        else {
          logEvento = new LogEvento();
          logEvento.setCodApplicazione("PG");
        }
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(oggEvento);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(descrEvento);
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);

      }
    }
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer dataColumnContainer)
      throws GestoreException {

    //PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
    //    this.getServletContext(), PgManager.class);

    //AbstractGestoreEntita gestoreDITG = new DefaultGestoreEntita(this.getEntita(), this.getRequest());
    if(this.getRequest()!=null)
      gestoreDITG = new GestoreEntita(this.getEntita(), this.getRequest());
    else
      gestoreDITG = new GestoreEntita(this.getEntita(),this.sqlManager,this.geneManager);
    // Osservazione: si e' deciso di usare la classe DefaultGestoreEntita,
    // invece di creare la classe GestoreDITG come apposito gestore di entita',
    // perche' essa non avrebbe avuto alcuna logica di business

    int numeroDitte = 0;
    String numDitte = this.getRequest().getParameter("numeroDitte");
    if(numDitte != null && numDitte.length() > 0)
      numeroDitte =  UtilityNumeri.convertiIntero(
          this.getRequest().getParameter("numeroDitte")).intValue();

    Long numeroStepAttivo = new Long(UtilityStruts.getParametroString(
      this.getRequest(), GestioneFasiGaraFunction.PARAM_WIZARD_PAGINA_ATTIVA));
    // Il campo FASGAR, per compatibilita' con PWB e' sempre valorizzato come
    // il più grande intero che e' minore o uguale allo step attivo del wizard
    // diviso per 10
    Double faseGara = new Double(Math.floor(numeroStepAttivo.doubleValue()/10));
    Long faseGaraLong = new Long(faseGara.longValue());

    boolean isGaraLottiConOffertaUnica = false;
    String tmp = this.getRequest().getParameter("isGaraLottiConOffertaUnica");
    if(tmp == null)
    	tmp = (String) this.getRequest().getAttribute("isGaraLottiConOffertaUnica");

    if("true".equals(tmp))
    	isGaraLottiConOffertaUnica = true;


    boolean isGaraElenco = false;
    tmp = this.getRequest().getParameter("isGaraElenco");
    if(tmp == null)
        tmp = (String) this.getRequest().getAttribute("isGaraElenco");

    if("1".equals(tmp))
      isGaraElenco = true;

    boolean isGaraCatalogo = false;
    tmp = this.getRequest().getParameter("isGaraCatalogo");
    if(tmp == null)
        tmp = (String) this.getRequest().getAttribute("isGaraCatalogo");

    if("1".equals(tmp))
      isGaraCatalogo = true;

    //Si determina se è attiva l'integrazione AUR
    String integrazioneAUR="2";
    String urlWSAUR = ConfigManager.getValore("it.eldasoft.sil.pg.integrazioneAUR.ws.url");
    if(urlWSAUR != null && !"".equals(urlWSAUR)){
      integrazioneAUR ="1";
    }

    String codiceTornata = null;
    String codiceLotto = null;


    // Estrazione dalla sessione della HashMap usata per memorizzare gli oggetti
    // DataColumnContainer contenenti tutti i campi presenti nella popup per gli
    // ulteriori dettagli della ditta (anche se si è modificato un solo campo).
    // E' giusto ricordare che ogni DataColumnContainer contiene anche i campi
    // chiave dell'entita
    HashMap mappaCampiPopup = (HashMap) this.getRequest().getSession().getAttribute(
  			GestorePopupFasiGara.CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA);

    boolean isListaModificata = false;
    boolean isNUMORDPLModificato = false;
    for(int i = 1; i <= numeroDitte; i++) {
      DataColumnContainer dataColumnContainerDiRiga = new DataColumnContainer(
      		dataColumnContainer.getColumnsBySuffix("_" + i, false));

      String codiceDitta = dataColumnContainerDiRiga.getString("DITG.DITTAO");
      codiceTornata = dataColumnContainerDiRiga.getString("DITG.CODGAR5");
      codiceLotto = dataColumnContainerDiRiga.getString("DITG.NGARA5");
      Long motivoEsclusione = null;
      if (dataColumnContainerDiRiga.isColumn("V_DITGAMMIS.MOTIVESCL"))
        motivoEsclusione = dataColumnContainerDiRiga.getLong("V_DITGAMMIS.MOTIVESCL");

      String dettaglioMotivoEsclusione = null;
      if (dataColumnContainerDiRiga.isColumn("V_DITGAMMIS.DETMOTESCL"))
        dettaglioMotivoEsclusione = dataColumnContainerDiRiga.getString("V_DITGAMMIS.DETMOTESCL");

      boolean isDITGModificata = dataColumnContainerDiRiga.isModifiedTable("DITG");
      // Flag per indicare se i campi di DITG presenti solo nella popup
      // 'Ulteriori dettagli' sono stati modificati o meno
      boolean isDITGModificataPopup = false;
      boolean isDITGAMMISModificata = dataColumnContainerDiRiga.isModifiedTable("V_DITGAMMIS");
      boolean isDITGSTATIModificata = false;

      if(dataColumnContainerDiRiga.isColumn("DITG.NUMORDPL") && dataColumnContainerDiRiga.getColumn("DITG.NUMORDPL").isModified())
        isNUMORDPLModificato=true;

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
    		// DataColumnContainer che potrebbe contenere i dati modificati nella popup
    		if(mappaCampiPopup.containsKey(keyDITGSTATI))
    			isDITGSTATIModificata = true;
    	}


    if(dataColumnContainerDiRiga.isColumn("DPRDOM_FIT_NASCOSTO") && dataColumnContainerDiRiga.getColumn("DPRDOM_FIT_NASCOSTO").isModified()){
          String dprdomFit = dataColumnContainerDiRiga.getString("DPRDOM_FIT_NASCOSTO");
          if(dprdomFit!=null && !"".equals(dprdomFit)){
            //La stringa ha il formato GG/MM/AAAA HH:MM per poterne fare la conversione si devono aggiungere i secondi
            dprdomFit += ":00";
            Date dprdom = UtilityDate.convertiData(dprdomFit, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
            dataColumnContainerDiRiga.setValue("DITG.DPRDOM", new Timestamp(dprdom.getTime()));
            isDITGModificata=true;
          }
    }

    if(dataColumnContainerDiRiga.isColumn("DPROFF_FIT_NASCOSTO") && dataColumnContainerDiRiga.getColumn("DPROFF_FIT_NASCOSTO").isModified()){
      String dproffFit = dataColumnContainerDiRiga.getString("DPROFF_FIT_NASCOSTO");
      if(dproffFit!=null && !"".equals(dproffFit)){
        //La stringa ha il formato GG/MM/AAAA HH:MM per poterne fare la conversione si devono aggiungere i secondi
        dproffFit += ":00";
        Date dproff = UtilityDate.convertiData(dproffFit, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
        dataColumnContainerDiRiga.setValue("DITG.DPROFF", new Timestamp(dproff.getTime()));
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

      		this.aggiornaStatoAmmissioneDITG(codiceTornata, codiceLotto, codiceDitta,
      				faseGaraLong, dataColumnContainerDiRiga.getColumns(
      						"V_DITGAMMIS", 2), status);

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
            logevento.setDescr("Modifica stato ammissione ditta '" + codiceDitta + "'  nella fase "+descFase+" (valore assegnato: '"+tabellatoAmmgar+"')");
            logevento.setErrmsg("");
            LogEventiUtils.insertLogEventi(logevento);
      	}

      	if(isDITGSTATIModificata){
      		// Nel caso i campi della tabella DITGSTATI siano stati modificati,
      		// allora bisogna salvarli nella tabella stessa
      		this.gestioneDITGSTATI(codiceTornata, codiceLotto, codiceDitta,
      				numeroStepAttivo, faseGaraLong,
      				(DataColumnContainer) mappaCampiPopup.get(keyDITGSTATI), status);
      		// Rimozione dalla sessione dell'oggetto DatacolumnContainer specifico
      		// dell'entita' DITGSTATI
    			mappaCampiPopup.remove(keyDITGSTATI);
      	}

      	if(isDITGModificataPopup){
      		// Se nella popup sono stati modificati alcuni campi dell'entita' DITG,
      		// allora si estrae dalla sessione il DatacolumnContainer con i campi
      		// modificati nella popup 'Ulteriori dettagli' e li si aggiungono al
      		// DataColumnContainer, per poterli salvare
      		DataColumnContainer ulterioriCampiDITG = (DataColumnContainer)
      				mappaCampiPopup.get(keyDITG);
      		dataColumnContainerDiRiga.addColumns(ulterioriCampiDITG.getColumns("DITG", 2), false);
      		// Rimozione dalla sessione dell'oggetto DatacolumnContainer specifico
      		// dell'entita' DITG
      		mappaCampiPopup.remove(keyDITG);
      	}




      	//Nel caso di gare per elenco, se si inserisce il numero ordine
      	//si deve calcolare il numero di penalità
      	if((isGaraElenco || isGaraCatalogo) && numeroStepAttivo.longValue()== GestioneFasiRicezioneFunction.FASE_ELENCO_CONCORRENTI_ABILITATI &&
      	    dataColumnContainerDiRiga.isColumn("DITG.NUMORDPL") && dataColumnContainerDiRiga.getColumn("DITG.NUMORDPL").isModified()){

      	  Long valoreCampoOriginale = dataColumnContainerDiRiga.getColumn("DITG.NUMORDPL").getOriginalValue().longValue();
      	  if(valoreCampoOriginale==null){
      	    this.pgManager.updatePenalita(codiceTornata,codiceLotto, codiceDitta, status);
      	  }
      	}

      	super.preUpdate(status, dataColumnContainerDiRiga);
      	gestoreDITG.update(status, dataColumnContainerDiRiga);

        if(dataColumnContainerDiRiga.isColumn("ESCLUDI_DITTA_ALTRI_LOTTI") && dataColumnContainerDiRiga.getLong("ESCLUDI_DITTA_ALTRI_LOTTI").longValue() == 1) {
          switch(numeroStepAttivo.intValue()) {
          case GestioneFasiRicezioneFunction.FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE:
          case GestioneFasiRicezioneFunction.FASE_APERTURA_DOMANDE_DI_PARTECIPAZIONE:
          case GestioneFasiRicezioneFunction.FASE_ELENCO_DITTE_INVITATE:
          case GestioneFasiRicezioneFunction.FASE_RICEZIONE_PLICHI:
            try {
              // Esclusione della ditta dai lotti di gara non ancora esaminati
              pgManager.esclusioneDittaAltriLottiNonEsaminati(codiceDitta,
                  codiceTornata, codiceLotto, motivoEsclusione,
                  dettaglioMotivoEsclusione);
            } catch(SQLException s){
              throw new GestoreException("Errore nell'esclusione della ditta " +
                  "(codice ditta = " + codiceDitta + ") da altri lotti non " +
                  "ancora esaminati della gara " + codiceTornata, null, s);
            }
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
        if("1".equals(integrazioneAUR) && dataColumnContainerDiRiga.isColumn("V_DITGAMMIS.AMMGAR") && dataColumnContainerDiRiga.isModifiedColumn("V_DITGAMMIS.AMMGAR")&&
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
                  }*/
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
      if((isGaraElenco || isGaraCatalogo) && numeroStepAttivo.longValue()== GestioneFasiRicezioneFunction.FASE_ELENCO_CONCORRENTI_ABILITATI &&
          dataColumnContainerDiRiga.isModifiedTable("ISCRIZCAT")){
        Long altpen = dataColumnContainerDiRiga.getLong("ISCRIZCAT.ALTPEN");
        String notpen = dataColumnContainerDiRiga.getString("ISCRIZCAT.NOTPEN");
        String ultnot = dataColumnContainerDiRiga.getString("ISCRIZCAT.ULTNOT");
        try {
          this.sqlManager.update("update iscrizcat set altpen = ?, notpen=?, ultnot=? where codgar=? and ngara=? and codimp=? and codcat=? and tipcat=?",
              new Object[]{altpen,notpen,ultnot,codiceTornata,codiceLotto,codiceDitta,"0", new Long(1)});
        } catch (SQLException e) {
          throw new GestoreException("Errore nell'aggiornamento dei dati della " +
              "tabella ISCRIZCAT per la gara " + codiceLotto, null, e);
        }


      }

    }

    //Nel caso di elenco e nel caso in cui venga modificato il campo NUMORDPL si deve controllare
    //l'unicità del valore di tale campo fra gli operatori
    if((isGaraElenco || isGaraCatalogo) && numeroStepAttivo.longValue()== GestioneFasiRicezioneFunction.FASE_ELENCO_CONCORRENTI_ABILITATI && isNUMORDPLModificato){
      String select="select distinct(numordpl) from ditg where codgar5 = ? and ngara5 = ? and numordpl is not null";
      try {
        List listaNumeriOrdine = this.getSqlManager().getListVector(select, new Object[] {codiceTornata,codiceLotto });
        if( listaNumeriOrdine != null && listaNumeriOrdine.size()>0){
          StringBuffer buf = null;
          for (int i = 0; i < listaNumeriOrdine.size(); i++) {
            Long numordpl= SqlManager.getValueFromVectorParam(listaNumeriOrdine.get(i), 0).longValue();
            select = "select nprogg,nomimo from ditg where  codgar5=? and ngara5=? and numordpl = ? order by nprogg  asc,nomimo  asc,codgar5  asc,dittao  asc,ngara5  asc";
            List listaDatiDitte = this.getSqlManager().getListVector(select, new Object[] {codiceTornata,codiceLotto,numordpl });
            if(listaDatiDitte!=null && listaDatiDitte.size()>1){
              buf = new StringBuffer();
              buf.append("<br><ul>");
              for (int j = 0; j < listaDatiDitte.size(); j++) {
                buf.append("<li style=\"list-style-type: disc;margin-left: 30px;\" >");
                buf.append(SqlManager.getValueFromVectorParam(listaDatiDitte.get(j), 0).longValue().toString());
                buf.append(" - ");
                buf.append(SqlManager.getValueFromVectorParam(listaDatiDitte.get(j), 1).stringValue());
                buf.append("</li>");
              }
              buf.append("</ul>");
              // Aggiungo il messaggio al request
              UtilityStruts.addMessage(this.getRequest(), "warning",
                  "warnings.elenchi.operatri.numOrdineAssegnatoDuplicato",
                  new Object[] { numordpl.toString(),buf.toString() });
            }
          }
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nel controllo dell'unicità del numero d'ordine degli operatori",null,e);
      }
    }

    if(isListaModificata){
      String codiceGara = dataColumnContainer.getColumn(
          "DITG.NGARA5_1").getValue().getStringValue();
      pgManager.aggiornaFaseGara(numeroStepAttivo, codiceGara, true);
    }


    //Nel caso in cui ci siano operatori appena abilitati,al salvataggio,ne mostro la lista all'utente e
    //previo conferma nella lista abilito e invio la mail di notifica
    if((isGaraElenco || isGaraCatalogo) && numeroStepAttivo.longValue()== GestioneFasiRicezioneFunction.FASE_APERTURA_DOMANDE_DI_ISCRIZIONE)
    {
    String isInvioMailOperatoriAbilitati = "false";
    isInvioMailOperatoriAbilitati = this.getRequest().getParameter("isInvioMailOperatoriAbilitati");
    if("true".equals(isInvioMailOperatoriAbilitati))
    {
      String listaOperatoriAbilitati = this.getRequest().getParameter("listaOperatoriAbilitati");
      String numeroOperatoriAbilitati = this.getRequest().getParameter("numeroOperatoriAbilitati");
      String flagMailPec = this.getRequest().getParameter("flagMailPec");
      String oggettoMail = this.getRequest().getParameter("oggettoMail");
      String testoMail = this.getRequest().getParameter("testoMail");
      String mittenteMail = this.getRequest().getParameter("mittenteMail");
      String uffintAbilitato = (String)this.getRequest().getSession().getAttribute("uffint");
      String codiceGara = dataColumnContainer.getColumn("DITG.NGARA5_1").getValue().getStringValue();
      String integrazioneWSDM = this.getRequest().getParameter("integrazioneWSDM");
      String idcfg = null;
      //Valorizzazione di IDCFG
      try {
        String cenint = (String)sqlManager.getObject("select t.cenint from gare g,torn t where g.codgar1 = t.codgar and ngara=?", new Object[]{codiceGara});
        cenint = UtilityStringhe.convertiNullInStringaVuota(cenint);
        if(!"".equals(cenint)){
          idcfg = cenint;
        }else{
          idcfg = uffintAbilitato;
        }
      } catch (SQLException sqle) {
        throw new GestoreException("Errore nella lettura di TORN.CENINT",null, sqle);
      }

      HashMap<String,String> datiWSDM = null;
      if("1".equals(integrazioneWSDM)){
        //Popolamento contenitore dati per  WSDM
        datiWSDM = new  HashMap<String, String>();
        datiWSDM.put("classificadocumento",this.getRequest().getParameter("classificadocumento"));
        datiWSDM.put("tipodocumento",this.getRequest().getParameter("tipodocumento"));
        datiWSDM.put("oggettodocumento",this.getRequest().getParameter("oggettodocumento"));
        datiWSDM.put("descrizionedocumento", this.getRequest().getParameter("descrizionedocumento"));
        datiWSDM.put("mittenteinterno",this.getRequest().getParameter("mittenteinterno"));
        datiWSDM.put("indirizzomittente",this.getRequest().getParameter("indirizzomittente"));
        datiWSDM.put("mezzoinvio",this.getRequest().getParameter("mezzoinvio"));
        datiWSDM.put("mezzo",this.getRequest().getParameter("mezzo"));
        datiWSDM.put("codiceregistrodocumento",this.getRequest().getParameter("codiceregistrodocumento"));
        datiWSDM.put("inout",this.getRequest().getParameter("inout"));
        datiWSDM.put("idindice",this.getRequest().getParameter("idindice"));
        datiWSDM.put("idtitolazione",this.getRequest().getParameter("idtitolazione"));
        datiWSDM.put("idunitaoperativamittente",this.getRequest().getParameter("idunitaoperativamittente"));
        datiWSDM.put("inserimentoinfascicolo",this.getRequest().getParameter("inserimentoinfascicolo"));
        datiWSDM.put("codicefascicolo",this.getRequest().getParameter("codicefascicolo"));
        datiWSDM.put("oggettofascicolo",this.getRequest().getParameter("oggettofascicolo"));
        datiWSDM.put("classificafascicolo",this.getRequest().getParameter("classificafascicolo"));
        datiWSDM.put("descrizionefascicolo",this.getRequest().getParameter("descrizionefascicolo"));
        datiWSDM.put("annofascicolo",this.getRequest().getParameter("annofascicolo"));
        datiWSDM.put("numerofascicolo",this.getRequest().getParameter("numerofascicolo"));
        datiWSDM.put("username",this.getRequest().getParameter("username"));
        String password = this.getRequest().getParameter("password");
        if(password==null)
          password="";
        datiWSDM.put("password",password);
        datiWSDM.put("ruolo",this.getRequest().getParameter("ruolo"));
        datiWSDM.put("nome",this.getRequest().getParameter("nome"));
        datiWSDM.put("cognome",this.getRequest().getParameter("cognome"));
        datiWSDM.put("codiceuo",this.getRequest().getParameter("codiceuo"));
        datiWSDM.put("idutente",this.getRequest().getParameter("idutente"));
        datiWSDM.put("idutenteunop",this.getRequest().getParameter("idutenteunop"));
        datiWSDM.put("key1",this.getRequest().getParameter("key1"));
        datiWSDM.put("codiceaoo",this.getRequest().getParameter("codiceaoonuovo"));
        datiWSDM.put("codiceufficio",this.getRequest().getParameter("codiceufficionuovo"));
        datiWSDM.put("societa",this.getRequest().getParameter("societa"));
        datiWSDM.put("codiceGaralotto",this.getRequest().getParameter("codiceGaralotto"));
        datiWSDM.put("cig",this.getRequest().getParameter("cig"));

        datiWSDM.put("struttura",this.getRequest().getParameter("strutturaonuovo"));
        datiWSDM.put("supporto",this.getRequest().getParameter("supporto"));
        datiWSDM.put("tipofascicolo",this.getRequest().getParameter("tipofascicolonuovo"));
        datiWSDM.put("classificadescrizione",this.getRequest().getParameter("classificadescrizione"));
        datiWSDM.put("voce",this.getRequest().getParameter("voce"));
        datiWSDM.put("codiceaoodes",this.getRequest().getParameter("codiceaoodes"));
        datiWSDM.put("codiceufficiodes",this.getRequest().getParameter("codiceufficiodes"));
        datiWSDM.put("idconfi",this.getRequest().getParameter("idconfi"));
        datiWSDM.put("RUP",this.getRequest().getParameter("RUP"));
        datiWSDM.put("nomeRup",this.getRequest().getParameter("nomeRup"));
        datiWSDM.put("acronimoRup",this.getRequest().getParameter("acronimoRup"));
        datiWSDM.put("sottotipo",this.getRequest().getParameter("sottotipo"));
        datiWSDM.put("tipofirma",this.getRequest().getParameter("tipofirma"));
        datiWSDM.put("idunitaoperativamittenteDesc",this.getRequest().getParameter("idunitaoperativamittenteDesc"));
        datiWSDM.put("uocompetenza",this.getRequest().getParameter("uocompetenza"));
        datiWSDM.put("uocompetenzadescrizione",this.getRequest().getParameter("uocompetenzadescrizione"));
      }
      try {
        ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        boolean isOp114 = GeneManager.checkOP(this.getServletContext(), CostantiGenerali.OPZIONE_GESTIONE_PORTALE);
        int res = elencoOperatoriManager.inviaComunicazioneAbilitazione(profilo,codiceGara,listaOperatoriAbilitati, numeroOperatoriAbilitati,
            flagMailPec, oggettoMail, testoMail, mittenteMail,idcfg,isOp114, integrazioneWSDM, datiWSDM,  this.getRequest());

      } catch (SQLException e) {
        throw new GestoreException("Errore nella procedura di abilitazione degli operatori",null,e);
      }catch (DocumentException e) {
        throw new GestoreException("Errore nella procedura di abilitazione degli operatori",null,e);
      } catch (IOException e) {
        throw new GestoreException("Errore nella procedura di abilitazione degli operatori",null,e);
      }
    }
    }
  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {


  }

  public void escludiDittaOfferta(String ditta, String numeroGara, String codiceGara, String mandataria, Long faseGara,
      TransactionStatus status) throws SQLException, GestoreException{
    this.sqlManager.update("update ditg set invoff=?, nproff=null, dproff=null, datoff=null, oraoff=null, mezoff=null, plioff=null, "
        + "notpoff=null, rtofferta=?, partgar=?, ribauo=null, impoff = null, puneco= null, impsicazi = null, impmano= null where ngara5=? "
        + "and codgar5=? and dittao=?", new Object[]{"2",ditta,"1",numeroGara,codiceGara,mandataria});

    DataColumn d1 = new DataColumn("V_DITGAMMIS.MOTIVESCL", new JdbcParametro(
        JdbcParametro.TIPO_DECIMALE, null));
    DataColumn d2 = new DataColumn("V_DITGAMMIS.AMMGAR", new JdbcParametro(
        JdbcParametro.TIPO_NUMERICO, new Long(2)));
    d2.setObjectOriginalValue(new Long(1));
    DataColumn d3 = new DataColumn("V_DITGAMMIS.DETMOTESCL", new JdbcParametro(
        JdbcParametro.TIPO_TESTO, null));

    this.gestioneDITGAMMIS(codiceGara, numeroGara, mandataria,
        faseGara, new DataColumn[]{d1,d2,d3}, status);

    this.aggiornaStatoAmmissioneDITG(codiceGara, numeroGara, mandataria,
        faseGara, new DataColumn[]{d1,d2,d3}, status);
  }
}