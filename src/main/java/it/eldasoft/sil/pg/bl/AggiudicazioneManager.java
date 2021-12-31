/*
 * Created on 23/lug/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

package it.eldasoft.sil.pg.bl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityMath;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Classe di gestione delle funzionalita' inerenti all'algoritmo di
 * aggiudicazione di una gara
 *
 * @author Stefano.Cestaro
 */
public class AggiudicazioneManager {

  private static final Date     DATA_DLgs_50_2016       = UtilityDate.convertiData("20/04/2016", UtilityDate.FORMATO_GG_MM_AAAA);
  private static final Date     DATA_DLgs_56_2017       = UtilityDate.convertiData("20/05/2017", UtilityDate.FORMATO_GG_MM_AAAA);
  private static final Date     DATA_DL_32_2019         = UtilityDate.convertiData("19/04/2019", UtilityDate.FORMATO_GG_MM_AAAA);

  private static final String   stringa_DLgs_50_2016                = "DLGS50_2016";
  private static final String   stringa_DLgs_56_2017                = "DLGS56_2017";
  private static final String   stringa_DL_32_2019                  = "DL32_2019";
  public static final String   stringaSogliaNorma_REG_SIC           = "LR13_2019";
  public static final String   stringaSogliaNorma_DL_32_2019_S      = "DL32_2019_S";
  public static final String   stringaSogliaNorma_DL_32_2019_G      = "DL32_2019_G";

  private static final int      DLgs_163_2006    = 0;
  private static final int      DLgs_50_2016     = 1;
  private static final int      DLgs_56_2017     = 2;
  private static final int      DL_32_2019       = 3;

    /** Logger */
	static Logger logger = Logger.getLogger(AggiudicazioneManager.class);

	/** Manager SQL per le operazioni su database */
	private SqlManager sqlManager;

	private PgManager pgManager;

	private GenChiaviManager genChiaviManager;

	private ControlliOepvManager controlliOepvManager;

    private PgManagerEst1 pgManagerEst1;

	/**
	 * Set SqlManager
	 *
	 * @param sqlManager
	 */
	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	public void setPgManager(PgManager pgManager) {
		this.pgManager = pgManager;
	}

	public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
      this.genChiaviManager = genChiaviManager;
    }

	public void setControlliOepvManager(ControlliOepvManager controlliOepvManager) {
	  this.controlliOepvManager = controlliOepvManager;
	}

   public void setPgManagerEst1(PgManagerEst1 pgManagerEst1) {
     this.pgManagerEst1 = pgManagerEst1;
   }


	/**
	 * Funzione principale per la prima parte dell'aggiudicazione. Consiste nel
	 * calcolo della soglia di anomalia richiamato dal pulsante "Calcolo soglia
	 * anomalia".
	 * La funzione restituisce normalmente una stringa vuota, tranne nel caso
	 * di modlic=13 e 14 se non vengon rispettate delle condizioni sul numero
	 * minimo di offerte e sull'importo della gara
	 *
	 * @param impl
	 * @throws GestoreException
	 * @ret    String
	 */
	public String[] aggiudicazioneFaseA(DataColumnContainer impl)
			throws GestoreException {

		String codgar = impl.getString("CODGAR1");
		String ngara = impl.getString("NGARA");
		String legRegSic = impl.getString("LEGREGSIC");
		Long precut = impl.getLong("PRECUT");

		String[] ret = new String[4];
		Long nsorte = null;
		Double alainf = null;
		Double alasup = null;
		Long metpunti=null;

		if (impl.isColumn("NSORTE")){
		  nsorte = impl.getLong("NSORTE");
		  alainf = impl.getDouble("ALAINF");
		  alasup = impl.getDouble("ALASUP");
		}



		if (logger.isDebugEnabled())
			logger.debug("aggiudicazioneFaseA(" + codgar + "," + ngara
					+ "): inizio metodo");

		// Inizializzazione delle varie HashMap utilizzate
		HashMap hMapTORN = new HashMap();
		HashMap hMapGARE = new HashMap();
		HashMap hMapParametri = new HashMap();

		// Inizializzazione dei campi di DITG
		this.inizializzaDITG(ngara);

		// Inizializzazione valori letti da TORN
		this.inizializzaTORN(codgar, hMapTORN);

		// Inizializzazione valori letti da GARE
		this.inizializzaGARE(ngara, hMapGARE);

		// Inizializzazione valori letti da GARE1 estensesione di GARE
	    this.inizializzaGareDaGare1(ngara, hMapGARE);

	    //Nel caso di calcolo soglia anomalia si deve considerare il valore di escauto impostato nella pagina, non il valore in db
	    if(impl.isColumn("ESCAUTOFIT")){
	      hMapGARE.put("escauto",  impl.getLong("ESCAUTOFIT"));
	    }

		// Inizializza nella HashMap hMapGARE il valore del campo LEGREGSIC
		this.inizializzaLEGREGSIC(ngara, hMapGARE, legRegSic);

        // Inizializza nella HashMap hMapGARE il valore del campo PRECUT
        this.inizializzaPRECUT(ngara, hMapGARE, precut);

        // Inizializza nella HashMap hMapGARE il valore del campo NSORTE,ALAINF e ALASUP
        this.inizializzaSorteggio(ngara, hMapGARE, nsorte,alainf,alasup);

		// Inizializza nella HashMap hMapParametri il correttivo di gara
		this.inizializzaCORGAR(ngara, hMapTORN, hMapGARE, hMapParametri);

		// Inizializza nella HashMap hMapParametri la tipologia della gara
        this.inizializzaTipologiaGara(codgar, hMapParametri);

        // Iniziazizza nella HashMap hMapParametri la data Pubblicazione su portale
        this.inizializzaDataPubblicazionePortale(codgar, hMapParametri);

        // Inizializza la varibile per stabilire si tratta della normativa DLgs.50/2016
        this.inizializzaNormativa(hMapTORN, hMapGARE, hMapParametri);

        this.inizializzaGestionePrezzo(hMapParametri,ngara);

		// Inizializza nella HashMap hMapParametri i punteggi
		// massimi tecnico ed economico per le gare di tipo
		// "Offerta economicamente più vantaggiosa"
		this.inizializzaPunteggiMassimi(ngara, hMapGARE, hMapParametri);

		// Controllo se la modalità di aggiudicazione è valorizzato ed il
		// suo calcolo è supportato
		if (this.controllaModalitaAggiudicazione(ngara)) {

			// Controllo se il tipo di procedura è valorizzato
			if (this.controllaTipoProcedura(ngara)) {

				// Controllo delle ditte ammesse in gara
				// Si procederà solo se ci sono ditte ammesse in gara
				if (this.esistonoDitte(ngara,	"(AMMGAR <> '2' or AMMGAR is null) and (MOTIES < 99 or MOTIES is null)")) {

				  //Nel caso di calcolo soglia di anomalia il campo lo si può valorizzare dalla maschera, quindi sovrascrivo il valore in
				  //hMapGARE
				  if(impl.isColumn("METPUNTI")){
			          metpunti = impl.getLong("METPUNTI");
			          hMapGARE.remove("metpunti");
			          hMapGARE.put("metpunti", metpunti);
				  }

				  boolean gestionePuneco = true;
				  if (hMapGARE.get("costofisso") != null){
                    String costofisso = ((String)hMapGARE.get("costofisso")).toString();
                    if("1".equals(costofisso))
                      gestionePuneco=false;
				  }

				    if (this.esistonoDitteRiammesse(ngara,gestionePuneco))
						this.settaMotiesANull(ngara);

					this.settaRibassoAZero(ngara);
					this.settaPunteggiAZero(ngara);

					// Forzatura dei campi AMMGAR e INVOFF ancora nulli al valore 1
					this.settaAMMAGAR_INVOFF(ngara);

					hMapParametri.put("numeroDitteAmmesse",	this.conteggioDitte(ngara,
							"AMMGAR <> '2' and (MOTIES < 99 or MOTIES is null)"));
					hMapParametri.put("numeroDitteRiammesse", this.conteggioDitteRiammesse(ngara));

                    //Se nuova modalità vengono inizializzati i parametri per il metodo di calcolo
                    this.inizializzaMetodoCalcoloSogliaAnomalia( hMapParametri,impl);

					this.inizializzaMinimoPerCorrettivo(ngara, codgar, hMapTORN, hMapGARE, hMapParametri);
                    ret = this.inizializzaFlags(hMapTORN, hMapGARE, hMapParametri);
                    this.gestioneCalcoloSogliaAnomalia(ngara, codgar, legRegSic, hMapGARE, hMapTORN, hMapParametri);

                    String selectDITG = this.impostaSelectDittePerCalcoloStaggi(hMapGARE, hMapParametri);

					this.settaStaggi(ngara, hMapTORN, hMapGARE, hMapParametri,
							selectDITG, 0, false, true);
					this.esclusioneEventualeVincitriceFaseA(codgar, ngara,
							hMapTORN, hMapGARE, hMapParametri);
					this.aggiornaImplGAREFaseA(ngara, impl, hMapParametri,hMapGARE);

				}	else {
					String message = "Non ci sono ditte ammesse alla gara";
					throw new GestoreException(message,
							"aggiudicazioneFaseA.ControlloDitteAmmesse");
				}
			} else {
				String message = "Per questo tipo di procedura non è previsto alcun calcolo";
				throw new GestoreException(message,
						"aggiudicazioneFaseA.ControlloTipoProcedura");
			}

		} else {
			String message = "Per questo criterio di aggiudicazione non è previsto alcun calcolo";
			throw new GestoreException(message,
					"aggiudicazioneFaseA.ControlloModalitaAggiudicazione");
		}

		if (logger.isDebugEnabled())
			logger.debug("aggiudicazioneFaseA(" + codgar + "," + ngara
					+ "): fine metodo");
		return ret;
	}

	/**
	 * Funzione principale per il calcolo dell'aggiudicazione provvisoria.
	 * Consiste nel calcolo dell'impresa vincitrice richiamato dal pulsante
	 * "Aggiudicazione"
	 *
	 * @param impl
	 * @throws GestoreException
	 */
	public void aggiudicazioneFaseB(DataColumnContainer impl,
			HashMap hMapConteggi, boolean aggiudTuttiLotti) throws GestoreException {

		String codgar = impl.getString("CODGAR1");
		String ngara = impl.getString("NGARA");

		if (logger.isDebugEnabled())
			logger.debug("aggiudicazioneFaseB(" + codgar + "," + ngara
					+ "): inizio metodo");

		String legRegSic = impl.getString("LEGREGSIC");
		Long precut = impl.getLong("PRECUT");
		Double limmax = impl.getDouble("LIMMAX");
		Long nofval = impl.getLong("NOFVAL");

		// Eventuali codici codici ditta per la prima e la seconda  aggiudicataria
		// Sono valorizzati nel caso in cui si presenti il caso di ditte parimerito

		String primaAggiudicatariaSelezionata = null;
		if(impl.isColumn("PRIMAAGGIUDICATARIASELEZIONATA"))
		  primaAggiudicatariaSelezionata = impl.getString("PRIMAAGGIUDICATARIASELEZIONATA");

        String ultimeAggiudicatarieSelezionate = null;
        if(impl.isColumn("ULTIMEAGGIUDICATARIESELEZIONATE"))
          ultimeAggiudicatarieSelezionate = impl.getString("ULTIMEAGGIUDICATARIESELEZIONATE");
        String sorteggioParimerito = null;
        if(impl.isColumn("SORTEGGIOPARIMERITO"))
          sorteggioParimerito = impl.getString("SORTEGGIOPARIMERITO");


		// Inizializzazione delle varie HashMap utilizzate
		HashMap hMapTORN = new HashMap();
		HashMap hMapGARE = new HashMap();
		HashMap hMapParametri = new HashMap();

		this.initControlliAggiudicazione(ngara, codgar, precut, legRegSic, limmax, nofval, hMapGARE, hMapTORN, hMapParametri);

		// Controllo se esistono ditte dopo il calcolo della soglia di anomalia
		if (this.esistonoDitte(ngara, "staggi > 1")) {

		    // Se in modalità manuale ripristina gli stati (DITG.STAGGI) a fronte dei
			// possibili cambiamenti sul campo CONGRUITA' effettuati dall'utente
			String selectDITG = "select dittao,ribauo,staggi,puntec,puneco,staggiali from ditg "
					+ "where staggi > 1 and ngara5 = ?";

			long modlicg = 0;
			if (hMapGARE.get("modlicg") != null)
				modlicg = ((Long) hMapGARE.get("modlicg")).longValue();
			if (modlicg == 6 || modlicg == 17) {
				selectDITG += " order by ribauo desc, staggi";
				if(modlicg == 6 ){
    			  String abilitataGestioneCriteriPrezzo = "0";
    			  boolean esistonoCriteriIsnprz = false;
    			  if (hMapParametri.get("abilitataGestioneCriteriPrezzo") != null)
    				  abilitataGestioneCriteriPrezzo = ((String) hMapParametri.get("abilitataGestioneCriteriPrezzo"));
                  if (hMapParametri.get("esistonoCriteriIsnprz") != null)
                    esistonoCriteriIsnprz = (((Boolean) hMapParametri.get("esistonoCriteriIsnprz"))).booleanValue();
                  if("1".equals(abilitataGestioneCriteriPrezzo) && esistonoCriteriIsnprz){
                    selectDITG= selectDITG.replace("puntec", "puntalt");
                   selectDITG= selectDITG.replace("puneco", "puntprz");
                      }else
                        selectDITG=this.cambiaSelectPunteggiConRiparametrati(selectDITG, hMapGARE, false);
  				 }
			} else {
				selectDITG += " order by ribauo, staggi";
			}
			this.settaStaggi(ngara, hMapTORN, hMapGARE, hMapParametri, selectDITG, 0,
					true, false);

			// Setto a "2" il valore del campo CONGRUO
			this.settaCongruoADue(ngara);

			//Si controlla se tutte le ditte ammesse alla gara sono anomale
            Long numDitteAnomale = this.conteggioDitte(ngara,"AMMGAR <> '2' and (MOTIES < 99 or MOTIES is null) and STAGGI <> 6 and STAGGI <> 10");
            Long numeroDitteAmmesse = null;
            if (hMapParametri.get("numeroDitteAmmesse") != null)
                numeroDitteAmmesse = ((Long) hMapParametri.get("numeroDitteAmmesse")).longValue();
            if(numDitteAnomale.equals(numeroDitteAmmesse))
              hMapConteggi.put("numeroDitteAnomale","TUTTE" );

            Long aqoper = null;
            if (hMapGARE.get("aqoper") != null)
              aqoper = ((Long) hMapGARE.get("aqoper"));

            long selpar = 0;
            if (hMapTORN.get("selpar") != null)
              selpar = ((Long) hMapTORN.get("selpar")).longValue();

              if(!new Long(2).equals(aqoper)){
                //Gestione del sorteggio nel caso si passi dalla popup delle parimerito
                //Si rientra in questo caso nel caso di selezione di prima classificata
                if(!aggiudTuttiLotti && "1".equals(sorteggioParimerito)){
                  String listaParimerito = null;
                  if(impl.isColumn("LISTAPARIMERITO"))
                    listaParimerito = impl.getString("LISTAPARIMERITO");
                  //chiamata alla funzione di selezione casuale fra le parimerito
                  HashMap hMapRisultatoSelezione = new HashMap();
                  this.selezioneCasualeParimerito(sorteggioParimerito, listaParimerito, 0, hMapRisultatoSelezione);
                  if(hMapRisultatoSelezione.get("selezione1")!=null){
                    primaAggiudicatariaSelezionata = (String)hMapRisultatoSelezione.get("selezione1");
                  }else{
                    String message = "Errore nella selezione automatica della prima aggiudicataria "
                        + "mediante selezione casuale";
                    throw new GestoreException(message,null);
                  }
                  hMapConteggi.put("selAutParimerito", "1"); //Indica che è stata selezionata in automatico la prima classificata
                }

                // Ricerca della prima aggiudicataria
    			// Se e' valorizzata la variabile primaAggiudicatariaSelezionata
    			// significa che e' stato chiesto all'utente di scegliere la prima
    			// aggiudicataria da una lista parimerito. In questo caso non è più
    			// necessario calcolare la prima aggiudicataria ma si considera valida
    			// quella indicata dall'utente.
    			if (primaAggiudicatariaSelezionata != null) {
    				this.aggiornaStatoAggiudicazione(ngara, primaAggiudicatariaSelezionata,
    						new Long(4));
    				hMapParametri.put("numeroPrimeParimerito", new Long(1));
    				hMapParametri.put("listaPrimeParimerito",
    						"'" + primaAggiudicatariaSelezionata + "'");
    				hMapParametri.put("primaAggiudicatariaSelezionata",
    						primaAggiudicatariaSelezionata);
    				if("1".equals(legRegSic)){
    				  long modastg = 0;
    			      if (hMapGARE.get("modastg") != null)
    			        modastg = ((Long) hMapGARE.get("modastg")).longValue();

    			      boolean isCalcoloSogliaAnomaliaApplicabile = true;
    			      if (hMapParametri.get("isCalcoloSogliaAnomaliaApplicabile") != null)
    			        isCalcoloSogliaAnomaliaApplicabile = ((Boolean) hMapParametri.get(
    			                  "isCalcoloSogliaAnomaliaApplicabile")).booleanValue();

    			      boolean isEsclusioneAutomaticaApplicabile = true;
    			      if (hMapParametri.get("isEsclusioneAutomaticaApplicabile") != null)
    			        isEsclusioneAutomaticaApplicabile = ((Boolean) hMapParametri.get(
    			                  "isEsclusioneAutomaticaApplicabile")).booleanValue();
    			      if(modastg == 1 && isCalcoloSogliaAnomaliaApplicabile && isEsclusioneAutomaticaApplicabile && numDitteAnomale.equals(numeroDitteAmmesse))
    			        this.aggiornaCampiCongruita(ngara, primaAggiudicatariaSelezionata, "1");

    				}
    			} else {
    				this.settaPrimaAggiudicataria(ngara, hMapGARE, hMapParametri);
    			}

    			hMapConteggi.put("numeroPrimeParimerito", hMapParametri.get(
    					"numeroPrimeParimerito"));
    			hMapConteggi.put("listaPrimeParimerito", hMapParametri.get(
    					"listaPrimeParimerito"));
    			hMapConteggi.put("primaAggiudicatariaSelezionata", hMapParametri.get(
    					"primaAggiudicatariaSelezionata"));



    			long numeroPrimeParimerito = 0;
    			if (hMapParametri.get("numeroPrimeParimerito") != null) {
    				numeroPrimeParimerito = ((Long)
    						hMapParametri.get("numeroPrimeParimerito")).longValue();
    			}

    			//Gestione del sorteggio delle parimerito dalla procedura di aggiudicazione su tutti i lotti
                //Si rientra in questo caso nel caso di selezione di prima classificata
                if(aggiudTuttiLotti && numeroPrimeParimerito > 1 && selpar == 1){
                  String listaParimerito = (String)hMapParametri.get(
                      "listaPrimeParimerito");
                  //chiamata alla funzione di selezione casuale fra le parimerito
                  HashMap hMapRisultatoSelezione = new HashMap();
                  this.selezioneCasualeParimerito("1", listaParimerito, 0, hMapRisultatoSelezione);
                  if(hMapRisultatoSelezione.get("selezione1")!=null){
                    primaAggiudicatariaSelezionata = (String)hMapRisultatoSelezione.get("selezione1");
                  }else{
                    String message = "Errore nella selezione automatica della prima aggiudicataria "
                        + "mediante selezione casuale";
                    throw new GestoreException(message,null);
                  }

                  this.aggiornaStatoAggiudicazione(ngara, primaAggiudicatariaSelezionata,
                      new Long(4));
                  hMapParametri.put("primaAggiudicatariaSelezionata",primaAggiudicatariaSelezionata);
                  hMapParametri.put("numeroPrimeParimerito", new Long(1));
                  hMapConteggi.put("numeroPrimeParimerito", new Long(1));
                  numeroPrimeParimerito =1;
                  hMapConteggi.put("selAutParimerito", "1"); //Indica che è stata selezionata in automatico la prima classificata
                }


    			if (numeroPrimeParimerito == 1) {
    			  this.esclusioneEventualeVincitriceFaseB(codgar, ngara, hMapTORN, hMapGARE, hMapParametri);
                  this.calcolaImportoAggiudicazione(ngara, hMapTORN, hMapGARE,
                          hMapParametri);
                  this.aggiornaImplGAREFaseB(ngara, impl, hMapParametri, false, hMapTORN,hMapGARE);



    			} else if (numeroPrimeParimerito > 1) {

    				String message = "Il calcolo dell'aggiudicazione ha identificato come "
    						+ "prima classificata più di una ditta";
    				throw new GestoreException(message,
    						"aggiudicazioneFaseB.PrimeParimerito");
    			}
              } else {
                //Caso di accordo quadro con più operatori (AQOPER=2)
                //Non è prevista l'identificazione della prima e seconda aggiudicata, ma il calcolo si limita a individuare
                //gli operatori che hanno presentato un'offerta congrua e ad etichettarli come 'prima ditta classificata'.
                //Il numero di operatori selezionati è pari al valore specificato in AQNUMOPE, se è nullo si considerano
                //tutti gli operatori congrui
                //Va gestito il caso di ultime parimerito, per cui si effettuano dei controlli preliminari, se non superati
                //si deve procedere alla selezione manuale fra le ditte parimerito
                String esitoControllo = "OK";

                //Gestione del sorteggio parimerito nel caso si passi dalla popup delle parimerito
                if(!aggiudTuttiLotti && "3".equals(sorteggioParimerito)){
                  String listaParimerito = null;
                  if(impl.isColumn("LISTAPARIMERITO"))
                    listaParimerito = impl.getString("LISTAPARIMERITO");
                  String numParimeritoDaSelezionare = null;
                  if(impl.isColumn("NUMEROULTIMEPARIMERITODASELEZIONARE"))
                    numParimeritoDaSelezionare = impl.getString("NUMEROULTIMEPARIMERITODASELEZIONARE");
                  int numElementiSel =0;
                  if(numParimeritoDaSelezionare!="" && !"".equals(numParimeritoDaSelezionare))
                    numElementiSel = Integer.parseInt(numParimeritoDaSelezionare);
                  //chiamata alla funzione di selezione casuale fra le parimerito
                  HashMap hMapRisultatoSelezione = new HashMap();
                  this.selezioneCasualeParimerito(sorteggioParimerito, listaParimerito, numElementiSel, hMapRisultatoSelezione);
                  if(hMapRisultatoSelezione.get("listaParimeritoSelezionate")!=null){
                    ultimeAggiudicatarieSelezionate = (String)hMapRisultatoSelezione.get("listaParimeritoSelezionate");
                    hMapConteggi.put("selAutParimerito", "3"); //Indica che é stata selezionate in automatico di più parimerito
                  }
                }


                if(ultimeAggiudicatarieSelezionate==null || "".equals(ultimeAggiudicatarieSelezionate))
                  esitoControllo = this.initControlliAggiudicazioneAccordiQuadroPiuOperatori(ngara, hMapGARE, hMapParametri, hMapTORN);

                hMapConteggi.put("aqoper", hMapGARE.get("aqoper"));
                hMapConteggi.put("esitoControlloAggiudicazioneAccordiQuadroPiuOperatori", esitoControllo);

                //Gestione del sorteggio parimerito nel caso di funzione aggiudicazione su tutti i lotti
                if(!esitoControllo.equals("OK") && aggiudTuttiLotti && selpar==1){
                  String listaParimerito =(String)hMapParametri.get("listaUltimeParimerito");
                  int numParimeritoDaSelezionare = ((Long)hMapParametri.get("numeroParimeritoDaSelezionare")).intValue();
                  //chiamata alla funzione di selezione casuale fra le parimerito
                  HashMap hMapRisultatoSelezione = new HashMap();
                  this.selezioneCasualeParimerito("3", listaParimerito, numParimeritoDaSelezionare, hMapRisultatoSelezione);
                  if(hMapRisultatoSelezione.get("listaParimeritoSelezionate")!=null){
                    ultimeAggiudicatarieSelezionate = (String)hMapRisultatoSelezione.get("listaParimeritoSelezionate");
                    hMapConteggi.put("selAutParimerito", "3"); //Indica che é stata selezionate in automatico di più parimerito
                    esitoControllo = "OK";
                  }
                }

                if(esitoControllo.equals("OK")){
                  Double ribauoParimerito = null;
                  if(impl.isColumn("RIBAUOPARIMERITO"))
                    ribauoParimerito = impl.getDouble("RIBAUOPARIMERITO");
                  else
                    ribauoParimerito= (Double)hMapParametri.get("ribauoParimerito");

                  this.settaAggiudicatarie(ngara, ultimeAggiudicatarieSelezionate, ribauoParimerito, hMapGARE, hMapParametri, hMapTORN);
                  this.esclusioneEventualeVincitriceFaseB(codgar, ngara, hMapTORN,
                      hMapGARE, hMapParametri);
                  this.calcolaImportoAggiudicazione(ngara, hMapTORN, hMapGARE,
                          hMapParametri);
                  this.aggiornaImplGAREFaseB(ngara, impl, hMapParametri, false, hMapTORN,hMapGARE);
                  hMapConteggi.put("numeroDitteAggiudicatarie", hMapParametri.get("numeroDitteAggiudicatarie"));
                }else{
                  hMapConteggi.put("ribauoParimerito", hMapParametri.get(
                      "ribauoParimerito"));
                  hMapConteggi.put("listaUltimeParimerito", hMapParametri.get(
                          "listaUltimeParimerito"));
                  hMapConteggi.put("ultimaAggiudicatariaSelezionata", hMapParametri.get(
                          "ultimaAggiudicatariaSelezionata"));
                  hMapConteggi.put("numeroParimeritoDaSelezionare", hMapParametri.get("numeroParimeritoDaSelezionare"));

                  String message = "Il calcolo dell'aggiudicazione ha identificato come "
                      + "ultima classificata più di una ditta";
                  throw new GestoreException(message,
                      "aggiudicazioneFaseB.UltimeParimerito");
                }
              }

              if(aggiudTuttiLotti && hMapParametri.get("garaInversa") != null && "1".equals(hMapParametri.get("garaInversa"))){
                try {
                  Long conteggio=(Long)this.sqlManager.getObject("select count(dittao) from ditg where ngara5=? and staggi=? and amminversa is null", new Object[]{ngara, new Long(4)});
                  hMapConteggi.put("numDittapSenzaAmminversa", conteggio);
                } catch (SQLException e) {
                  throw new GestoreException("Errore nella lettura del valore di DITG.AMMINVERSA per le ditte aggiudicatarie in via provvisoria.",null, e);
                }
              }

		} else {
			String message = "Per aggiudicare la gara attivare prima il calcolo "
					+ "della soglia anomalia";
			throw new GestoreException(message,
					"aggiudicazioneFaseB.ControlloDitteAmmesse");
		}

		if (logger.isDebugEnabled())
			logger.debug("aggiudicazioneFaseB(" + codgar + "," + ngara
					+ "): fine metodo");
	}

	/**
	 * Metodo per l'aggiudicazione definitiva di una gara invocata dalla pagina
	 * 'Aggiudicazione definitiva'
	 *
	 * @param impl
	 * @param hMapConteggi
	 * @throws GestoreException
	 * @throws SQLException
	 */
	public void aggiudicazioneDefinitiva(DataColumnContainer impl,
			HashMap hMapConteggi) throws GestoreException, SQLException {

		String codgar = impl.getString("CODGAR1");
		String ngara = impl.getString("NGARA");

		if (logger.isDebugEnabled())
			logger.debug("aggiudicazioneDefinitiva(" + codgar + "," + ngara
					+ "): " + "inizio metodo");

		// Eventuali codici ditta per la prima e la seconda aggiudicataria
		// Sono valorizzati nel caso in cui si presenti il caso di ditte parimerito
		String primaAggiudicatariaSelezionata = impl.getString(
				"PRIMAAGGIUDICATARIASELEZIONATA");

		// Inizializzazione delle varie HashMap utilizzate
		HashMap hMapTORN = new HashMap();
		HashMap hMapGARE = new HashMap();
		HashMap hMapGARECONT = new HashMap();
		HashMap hMapParametri = new HashMap();

		hMapParametri.put("primaAggiudicatariaSelezionata",
				primaAggiudicatariaSelezionata);

		// Inizializzazione valori letti da TORN
		this.inizializzaTORN(codgar, hMapTORN);

		// Inizializzazione valori letti da GARE
		this.inizializzaGARE(ngara, hMapGARE);

		// Inizializzazione valori letti da GARE1 estensesione di GARE
	    this.inizializzaGareDaGare1(ngara, hMapGARE);

		long tipgen = 0;
		long modlicg = 0;
		double impgar = 0;
		double iaggiu = 0;
		double ribauo = 0;
		double riboepv = 0;
		String ridiso = "2";
		String accqua = null;

		if (hMapTORN.get("tipgen") != null)
			tipgen = ((Long) hMapTORN.get("tipgen")).longValue();
		if (hMapGARE.get("modlicg") != null)
			modlicg = ((Long) hMapGARE.get("modlicg")).longValue();
		if (hMapGARE.get("ridiso") != null)
			ridiso = (String) hMapGARE.get("ridiso");
		if (hMapTORN.get("accqua") != null)
		  accqua = (String) hMapTORN.get("accqua");

		this.esclusioneEventualeVincitriceFaseB(codgar, ngara, hMapTORN, hMapGARE,
				hMapParametri);
		this.calcolaImportoAggiudicazione(ngara, hMapTORN, hMapGARE, hMapParametri);

		if (hMapParametri.get("iaggiu") != null)
			iaggiu = ((Double) hMapParametri.get("iaggiu")).doubleValue();
		if (hMapParametri.get("ribauo") != null)
			ribauo = ((Double) hMapParametri.get("ribauo")).doubleValue();

		// Calcolo il valore del ribasso dall'importo offerto
		// In questa modalità "Offerta Economicamente più vantaggiosa" il
		// campo RIBAUO riporta il punteggio e non il ribasso
		if (modlicg == 6) {
			double impapp = 0;
			double onprge = 0;
			double impsic = 0;
			double impnrl = 0;
			String sicinc = null;
			double impoff = 0;
			String onsogrib= "";

			if (hMapGARE.get("impapp") != null)
				impapp = ((Double) hMapGARE.get("impapp")).doubleValue();
			if (hMapGARE.get("onprge") != null)
				onprge = ((Double) hMapGARE.get("onprge")).doubleValue();
			if (hMapGARE.get("impsic") != null)
				impsic = ((Double) hMapGARE.get("impsic")).doubleValue();
			if (hMapGARE.get("impnrl") != null)
				impnrl = ((Double) hMapGARE.get("impnrl")).doubleValue();
			if (hMapGARE.get("sicinc") != null)
				sicinc = (String) hMapGARE.get("sicinc");
			if (hMapParametri.get("impoff") != null)
				impoff = ((Double) hMapParametri.get("impoff")).doubleValue();
			if (hMapGARE.get("onsogrib") != null){
			  if(hMapGARE.get("onsogrib") instanceof Character)
		        onsogrib = ((Character) hMapGARE.get("onsogrib")).toString();
		      else
		        onsogrib =(String)hMapGARE.get("onsogrib");
			}
			if (hMapParametri.get("ribauo") != null) {
				riboepv = ((Double) hMapParametri.get("riboepv")).doubleValue();
			}
		}

		Long modcont = null;
		if (hMapTORN.get("modcont") != null)
		  modcont = (Long) hMapTORN.get("modcont");

		// Se la gara e' a lotti con offerta unica, allora si mantengono allienati
		// i dati relativi all'atto contrattuale con le gare vinte dalla stessa
		// ditta
		String dittao = (String) hMapParametri.get("primaAggiudicatariaSelezionata");
		Long genere = null;
		try {
			genere = (Long) this.sqlManager.getObject(
			    "select GENERE from GARE where CODGAR1 = ? and NGARA = ?",
			    new Object[]{codgar,codgar});

			if(genere!= null && 3==genere.longValue() && (new Long(2)).equals(modcont)){

			    Vector tmpVector = this.sqlManager.getVector(
						"select TIATTO, NREPAT, DAATTO, RIDISO, NQUIET, DQUIET, ISTCRE, "
						+ "INDIST, DRICZDOCCR, TAGGEFF, NAGGEFF, DAGGEFF, DCOMDITTAGG, "
						+ "NCOMDITTAGG, DCOMDITTNAG, NCOMDITTNAG, NMAXIMO "
						+ "from GARE where CODGAR1 = ? and NGARA <> ? and GENERE is null "
						+ "and DITTA = ? and FASGAR = ? ",
						new Object[] {codgar,	ngara, dittao, new Long(
								GestioneFasiGaraFunction.FASE_AGGIUDICAZIONE_DEFINITIVA/10)});

				if (tmpVector != null) {
					//Nel caso di gara a lotti con offerta unica si deve prelevare il
					//valore di RIDISO dei lotti gia' aggiudicati
					ridiso = tmpVector.get(3).toString();
					this.sqlManager.update(
							"update GARE set TIATTO = ?, NREPAT = ?, DAATTO = ?, RIDISO = ?,"
									+ "NQUIET = ?, DQUIET = ?, ISTCRE = ?, INDIST = ?, "
									+ "DRICZDOCCR=?, TAGGEFF=?, NAGGEFF=?, DAGGEFF=?, DCOMDITTAGG=?,"
									+ "NCOMDITTAGG=?, DCOMDITTNAG=?, NCOMDITTNAG=?, NMAXIMO=? "
									+ "where CODGAR1 = ? and NGARA = ?",
							new Object[]{
								((JdbcParametro) tmpVector.get(0)).getValue(),
								((JdbcParametro) tmpVector.get(1)).getValue(),
								((JdbcParametro) tmpVector.get(2)).getValue(),
								((JdbcParametro) tmpVector.get(3)).getValue(),
								((JdbcParametro) tmpVector.get(4)).getValue(),
								((JdbcParametro) tmpVector.get(5)).getValue(),
								((JdbcParametro) tmpVector.get(6)).getValue(),
								((JdbcParametro) tmpVector.get(7)).getValue(),
								((JdbcParametro) tmpVector.get(8)).getValue(),
								((JdbcParametro) tmpVector.get(9)).getValue(),
								((JdbcParametro) tmpVector.get(10)).getValue(),
								((JdbcParametro) tmpVector.get(11)).getValue(),
								((JdbcParametro) tmpVector.get(12)).getValue(),
								((JdbcParametro) tmpVector.get(13)).getValue(),
								((JdbcParametro) tmpVector.get(14)).getValue(),
								((JdbcParametro) tmpVector.get(15)).getValue(),
								((JdbcParametro) tmpVector.get(16)).getValue(),codgar,ngara});
				} else {
					ridiso = "2";
					this.sqlManager.update(
							"update GARE set TIATTO = null, NREPAT = null, DAATTO = null,"
									+ "RIDISO = null, NQUIET = null, DQUIET = null, ISTCRE = null,"
									+ "INDIST = null, DRICZDOCCR=null, TAGGEFF=null, NAGGEFF=null, "
									+ "DAGGEFF=null, DCOMDITTAGG=null, NCOMDITTAGG=null, DCOMDITTNAG=null, "
									+ "NCOMDITTNAG=null, NMAXIMO=null where CODGAR1 = ? and NGARA = ?",
							new Object[] { codgar, ngara });
				}

				/*
				//Si devono cancelare le occorrenze di garecont con codimp nullo oppure associato a ditte che non sono aggiudicatarie
				//definitive di nessun lotto della gara
				this.sqlManager.update(
                    "delete from garecont where ngara=? and codimp is null",
                    new Object[] { codgar });

				List listaGarecont = this.sqlManager.getListVector("select codimp from garecont where ngara=?", new Object[]{codgar});
				if(listaGarecont!= null && listaGarecont.size()>0){
				  for (int i = 0; i < listaGarecont.size(); i++) {
				    String ditta= SqlManager.getValueFromVectorParam(listaGarecont.get(i), 0).stringValue();
				    Long numeroLottiAggiudicatiDitta= (Long)this.sqlManager.getObject("select count(ngara) from gare where codgar1=? and ditta=?", new Object[]{codgar,ditta});
				    if(numeroLottiAggiudicatiDitta==null || (numeroLottiAggiudicatiDitta!=null && numeroLottiAggiudicatiDitta.longValue()==0))
    				    this.sqlManager.update(
    	                    "delete from garecont where ngara=? and codimp =?",
    	                    new Object[] { codgar, ditta});
				  }
				}

				//Se non esiste già un'occorrenza di GARECONT con NGARA uguale a quello dell'occorrenza complementare
                //e CODIMP uguale a quello della ditta aggiudicataria la devo creare.
                Long count=(Long)this.sqlManager.getObject("select count(ngara) from garecont where ngara=? and codimp=?", new Object[]{codgar,dittao});
                if(count==null || ( count!=null && count.longValue()==0)){
                  Long ncont=(Long)this.sqlManager.getObject("select max(ncont) from garecont where ngara=?", new Object[]{codgar});
                  if(ncont==null)
                    ncont=new Long(1);
                  else
                    ncont = new Long (ncont.longValue() + 1);

                  this.sqlManager.update(
                      "insert into garecont(ngara,ncont,codimp) values(?,?,?)",
                      new Object[] { codgar, ncont,dittao });
                }
                */
			}
		} catch (SQLException s) {
			throw new GestoreException("Errore nell'aggiornamento dei dati dell'atto "
				+ "contrattuale della gara in analisi (CODGAR = "	+ codgar + ", NGARA = "
				+ ngara	+ ") al fine di mantenerli allienati con l'atto contrattuale "
				+ "stipulato con la ditta (Codice ditta = "	+ dittao + ") vincitrice "
				+ "di altri lotti della gara", null, s);
		}

        long aqoper=0;
        if("1".equals(accqua)){
          if (hMapGARE.get("aqoper") != null)
            aqoper = ((Long) hMapGARE.get("aqoper")).longValue();
		  //Per gare non ad offerta unica l'occorrenza su GARECONT esiste sicuramente, quindi si può effettuare l'aggiornamento di
		  //GARECONT.IMPQUA in questo momento, per le gare ad offerta unica non è detto che esista l'occorrenza di GARECONT, quindi
		  //si deve agire in un momento successivo
		  if(genere== null || (genere!= null && 3!=genere.longValue())){
    		  try {
                this.sqlManager.update(
                        "update GARECONT set IMPQUA=? where NGARA = ? and NCONT=?",
                        new Object[] { (Double) hMapGARE.get("impapp"), ngara, new Long(1) });
              } catch (SQLException e) {
                throw new GestoreException("Errore nell'aggiornamento del campo GARECONT.IMPQUA(NGARA = " + ngara + ","
                    + " NCONT = 1) ", null, e);
              }

              // Inizializzazione valori letti da GARECONT
              this.inizializzaGarecontNoOU(ngara, hMapGARECONT);
              String contspe = null;
              if(aqoper==1){
                String esecscig = null;
                Long altrisog =null;
                if (hMapTORN.get("altrisog") != null)
                  altrisog = ((Long) hMapTORN.get("altrisog")).longValue();
                if (hMapGARE.get("esecscig") != null)
                  esecscig = (String) hMapGARE.get("esecscig");
                try {
                  if(esecscig == null && !(new Long(3).equals(altrisog))){
                    esecscig = "1";
                  }
                  if(!"1".equals(esecscig)){
                    contspe = this.getValoreInizializzazioneContspe();
                  }
                  this.sqlManager.update(
                      "update GARECONT set ESECSCIG = ?, CONTSPE = ? where NGARA = ? and NCONT=?",
                      new Object[] { esecscig, contspe, ngara, new Long(1) });
                } catch (SQLException e) {
                  throw new GestoreException("Errore nell'aggiornamento del campo GARECONT.ESECSCIG(NGARA = " + ngara + ","
                      + " NCONT = 1) ", null, e);
                }

              }else{
                contspe = this.getValoreInizializzazioneContspe();
                this.sqlManager.update(
                    "update GARECONT set ESECSCIG = ?,  CONTSPE = ? where NGARA = ? and NCONT=?",
                    new Object[] { null, contspe, ngara, new Long(1) });
              }
		  }

  		  if(hMapGARE.get("impapp")!=null)
  		    iaggiu = ((Double) hMapGARE.get("impapp")).doubleValue();
  		  else
  		    iaggiu=0;

		}else{
		  if(genere== null || (genere!= null && 3!=genere.longValue())){
            //se non e' acordo quadro sbianco Esecuzione contratto stesso cig
            this.sqlManager.update(
                "update GARECONT set ESECSCIG = ?, CONTSPE = ? where NGARA = ? and NCONT = ?",
                new Object[] { null, null, ngara, new Long(1) });
		  }
		}

		if(aqoper==2){
		  hMapParametri.put("impgar", null);
		}else{
		   double ribcauz =0;
		   if (modlicg == 6) {
			   ribcauz = riboepv;
		   }else {
			   ribcauz = ribauo;
		   }
		  impgar = this.calcolaImportoGaranzia(iaggiu, ribcauz, tipgen,
              ridiso.equals("1"));
          hMapParametri.put("impgar", new Double(impgar));
		}

		this.aggiornaImplGAREFaseB(ngara, impl, hMapParametri, true, hMapTORN,hMapGARE);

		//Nel caso di OEPV senza busta economica si deve gestire l'inserimento in DPRE
		String costofisso = (String)hMapGARE.get("costofisso");
		if (modlicg == 6 && "1".equals(costofisso)) {
		  String ditta = impl.getString("GARE.DITTA");
		  //Cancellazione delle eventuali occorrenze di DPRE
		  try {
            this.sqlManager.update("delete from dpre where ngara=? and dittao=?", new Object[]{ngara, ditta});
            List listaDatiGCAP = this.sqlManager.getListVector("select contaf, prezun, quanti from gcap where ngara=? and prezun is not null"
                + " and sogrib=?", new Object[]{ngara, "2"});
            if(listaDatiGCAP!=null && listaDatiGCAP.size()>0){
              Long contaf=null;
              Double prezun = null;
              Double quanti = null;
              Object parametri[] = new Object[5];
              parametri[0] = ngara;
              parametri[1] = ditta;
              for(int i=0;i<listaDatiGCAP.size();i++){
                contaf = SqlManager.getValueFromVectorParam(listaDatiGCAP.get(i),0 ).longValue();
                prezun = this.pgManager.getValoreImportoToDouble(SqlManager.getValueFromVectorParam(listaDatiGCAP.get(i),1 ).getValue());
                quanti = this.pgManager.getValoreImportoToDouble(SqlManager.getValueFromVectorParam(listaDatiGCAP.get(i),2 ).getValue());
                parametri[2] = contaf;
                parametri[3] = prezun;
                parametri[4] = new Double(UtilityMath.round(prezun.doubleValue() * quanti.doubleValue(), 2));
                this.sqlManager.update("insert into dpre(ngara,dittao,contaf,preoff,impoff) values(?,?,?,?,?)", parametri);
              }
            }
          } catch (SQLException e) {
            throw new GestoreException("Errore nell'aggiornamento della TABELLA DPRE per la ditta aggiudicataria (DITTA = " + ditta + ")"
                + "della gara (NGARA = " + ngara + ")", null, e);
          }
		}

		if (logger.isDebugEnabled())
			logger.debug("aggiudicazioneDefinitiva(" + codgar + "," + ngara + "): "
					+ "fine metodo");
	}

	/**
	 * Funzione per annullare il calcolo dell'aggiudicazione, invocata dalla
	 * fase di gara "Calcolo aggiudicazione".
	 *
	 * @param impl
	 * @throws GestoreException
	 */
	public void annullaCalcoloAggiudicazione(String codiceGara,
			String numeroGara, boolean isGaraLottiConOffertaUnica,
			Long bustalotti, boolean gestioneOffEconomica)
			throws GestoreException {

		String codgar = codiceGara; // impl.getString("CODGAR1");
		String ngara = numeroGara; // impl.getString("NGARA");

		if (logger.isDebugEnabled())
			logger.debug("annullaCalcoloAggiudicazione(" + codgar + "," + ngara
					+ "): inizio metodo");

		// Per le gare a lotti con offerta unica l'operazione deve essere eseguita
		// per tutti i lotti della gara; per gli altri tipi di gare, l'aggiornamento
		// riguarda il singolo lotto
		List listaLotti = null;

        //Caso BUSTALOTTI=2 - gara a lotti con plico unico e offerta unica
		if (isGaraLottiConOffertaUnica) {
			try {
				listaLotti = this.sqlManager.getListVector(
					"select NGARA from GARE where CODGAR1 = ? and GENERE is null",
					new Object[] { codgar });
			} catch (SQLException e) {
				throw new GestoreException(
					"Errore durante la selezione dei lotti "
					+ "(della gara con plico unico) a cui applicare l'annullamento "
					+ "del calcolo aggiudicazione", null, e);
			}

			if (listaLotti != null && listaLotti.size() > 0) {
				Long faseAggiornamento = new Long(GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE/10);
				Long stepgarAggiornamento = new Long(GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE);
				if(new Long(2).equals(bustalotti) && !gestioneOffEconomica){
				  faseAggiornamento = new Long(GestioneFasiGaraFunction.FASE_CHIUSURA_VALUTAZIONE_TECNICA/10);
	              stepgarAggiornamento = new Long(GestioneFasiGaraFunction.FASE_CHIUSURA_VALUTAZIONE_TECNICA);
				}
			    for (int i = 0; i < listaLotti.size(); i++) {
					Vector tmp = (Vector) listaLotti.get(i);
					String tmpNGARA = ((JdbcParametro) tmp.get(0)).getStringValue();

					this.annullaCalcoloAggiudicazioneLotto(tmpNGARA, codgar, bustalotti, gestioneOffEconomica);

					// Aggiornamento del campo GARE.FASGAR dell'occorrenza
					// complementare
					try {
						this.sqlManager.update(
							"update GARE set FASGAR = ?, STEPGAR = ? where CODGAR1 = ? "
							+ "and NGARA = CODGAR1 and GENERE = 3",	new Object[] {
							    faseAggiornamento,stepgarAggiornamento,codgar });
					} catch (SQLException e) {
						throw new GestoreException(
							"Errore nell'aggiornamento del campo GARE.FASGAR per l'occorrenza "
							+ "complementare di una gara a lotti con plico unico", null, e);
					}
				}
			}
		} else {
		  this.annullaCalcoloAggiudicazioneLotto(ngara, codgar, bustalotti, gestioneOffEconomica);
		}

		if (logger.isDebugEnabled())
			logger.debug("annullaCalcoloAggiudicazione(" + codgar + "," + ngara
					+ "): fine metodo");
	}

	/**
	 * Aggiornamenti del singolo lotto di gara o dell'unico lotto di gara per
	 * annullamento del calcolo aggiudicazione
	 *
	 * @param ngara
	 * @param codgar
	 * @param bustalotti
	 * @param
	 * @throws GestoreException
	 */
	private void annullaCalcoloAggiudicazioneLotto(String ngara, String codgar, Long bustalotti, boolean gestioneOffEconomica)
			throws GestoreException {
		// Inizializzazione delle varie HashMap utilizzate
		HashMap hMapTORN = new HashMap();
		HashMap hMapGARE = new HashMap();
		HashMap hMapParametri = new HashMap();

		// Inizializzazione dei campi di DITG
		this.inizializzaDITG(ngara);

		// Il campo STAGGI.DITG vede essere posto a NULL
		try {
			this.sqlManager.update("update DITG set STAGGI = null, STAGGIALI = null where NGARA5 = ?",
					new Object[] { ngara });

			this.sqlManager.update("delete from DITGSTATI where NGARA = ? and FASGAR = ? and STEPGAR = ? ",
                new Object[] { ngara, new Long(7), new Long(70)});

		} catch (SQLException e) {
			throw new GestoreException(
				"Errore durante lo sbiancamento del campo DITG.STAGGI",
				"inizializzaDITG", e);
		}

		// Inizializzazione valori letti da TORN
		this.inizializzaTORN(codgar, hMapTORN);
		this.esclusioneEventualeVincitriceFaseA(codgar, ngara, hMapTORN, hMapGARE,
				hMapParametri);
		this.aggiornaImplGAREannullaCalcolo(ngara, codgar,bustalotti, true, gestioneOffEconomica);
		//Nel caso di bustalotti=1 si sta annullando il calcolo di aggiudicazione del singolo lotto, però se non vi sono
		//altri lotti aggiudicati si deve ripristinare a 6 la fase della gara complementare
		if(bustalotti!=null && bustalotti.longValue()==1){
		  this.aggiornaImplGAREannullaCalcolo(codgar, codgar,bustalotti,true, true);
		}
	}

	/**
	 * Verifica la modalità di aggiudicazione. Al momento solo alcune modalità
	 * di aggiudicazione sono supportate dal calcolo:
	 * <ul>
	 * <li>1 Miglior ribasso
	 * <li>5 Miglior offerta prezzi
	 * <li>13 Prezzo più basso (ribasso su elenco prezzi)
	 * <li>14 Prezzo più basso (offerta a prezzi unitari)
	 * <li>6 Offerta economicamente più vantaggiosa (OEPV) [12/06/2009]
	 * </ul>
	 *
	 * @param ngara
	 * @return
	 *            <ul>
	 *            <li>TRUE: modalità di aggiudicazione SUPPORTATA
	 *            <li>FALSE: modalità di aggiudicazione NON SUPPORTATA
	 *            </ul>
	 * @throws GestoreException
	 */
	public boolean controllaModalitaAggiudicazione(String ngara)
			throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("controllaModalitaAggiudicazione(" + ngara
					+ "): inizio metodo");

		boolean result = false;
		String selectGare = "select modlicg from gare where ngara = ?";

		try {
			List datiGare = this.sqlManager.getListVector(selectGare,
					new Object[] { ngara });
			if (datiGare != null && datiGare.size() > 0) {
				Long modlicg = SqlManager.getValueFromVectorParam(
						datiGare.get(0), 0).longValue();

				if (modlicg != null) {
					switch (modlicg.intValue()) {
					case 1:
					case 5:
					case 13:
					case 14:
					case 6:
					case 17:
						result = true;
						break;
					}
				}
			}
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore durante il controllo del criterio di aggiudicazione",
					"controllaModalitaAggiudicazione", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("controllaModalitaAggiudicazione(" + ngara
					+ "): fine metodo");

		return result;
	}

	/**
	 * Controllo se l'aggiudicazione provvisoria è stata eseguita: il controllo
	 * avviene cercando se esiste la ditta prima aggiudicataria fra le ditte
	 * della gara o del lotto di gara (DITG.STAGGI = 4)
	 *
	 * @param ngara
	 * @return Ritorna true se l'aggiudicazione provvisoria è stata eseguita,
	 *         false altrimenti
	 * @throws GestoreException
	 */
	public boolean controlloAggiudicazioneProvvisoriaEseguita(String ngara)
			throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("controlloAggiudicazioneProvvisoriaEseguita(" + ngara
					+ "): inizio metodo");

		boolean result = false;
		try {
			Long esisteDittaPrimaAggiudicataria = (Long) this.sqlManager.getObject(
					"select count(*) from DITG  where NGARA5 = ? and STAGGI = 4 ",
					new Object[] { ngara });
			if (esisteDittaPrimaAggiudicataria != null
					&& esisteDittaPrimaAggiudicataria.longValue() > 0)
				result = true;
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore durante il controllo se e' stata eseguita l'aggiudicazione "
					+ "provvisoria", "controlloAggiudicazioneProvvisoriaEseguita", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("controlloAggiudicazioneProvvisoriaEseguita(" + ngara
					+ "): fine metodo");

		return result;
	}

	/**
	 * Verifica se il tipo di procedura (TIPGARG.GARE) è valorizzato
	 *
	 * @param ngara
	 * @return
	 *            <ul>
	 *            <li>TRUE: è definito il tipo di procedura
	 *            <li>FALSE: non è definito il tipo di procedura
	 *            </ul>
	 * @throws GestoreException
	 */
	public boolean controllaTipoProcedura(String ngara) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("controllaTipoProcedura(" + ngara + "): inizio metodo");

		boolean result = false;
		String selectGare = "select tipgarg from gare where ngara = ?";

		try {
			List datiGare = this.sqlManager.getListVector(selectGare,
					new Object[] { ngara });
			if (datiGare != null && datiGare.size() > 0) {
				Long tipgarg = SqlManager.getValueFromVectorParam(
						datiGare.get(0), 0).longValue();
				if (tipgarg != null)
					result = true;
			}
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore durante il controllo del Tipo di Procedura",
					"controllaTipoProcedura", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("controllaTipoProcedura(" + ngara + "): fine metodo");

		return result;
	}

	/**
	 * Inizializza alcuni campi della tabella DITG
	 *
	 * @param ngara
	 * @throws GestoreException
	 */
	private void inizializzaDITG(String ngara) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("inizializzaDITG(" + ngara + "): inizio metodo");

		try {
			this.sqlManager.update(
					"update DITG set STAGGI = 0, STAGGIALI = 0, CONGRUO = '0', CONGMOT = null, RIBIMP =null "
							+ "where NGARA5 = ?",
					new Object[] { ngara });
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore durante l'aggiornamento iniziale della lista delle ditte "
							+ "partecipanti alla gara", "inizializzaDITG", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("inizializzaDITG(" + ngara + "): fine metodo");
	}

	/**
	 * Inizializza alcuni dati ricavati dalla tabella TORN
	 *
	 * @param codgar
	 * @param hMap
	 * @throws GestoreException
	 */
	public void inizializzaTORN(String codgar, HashMap hMap)
			throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("inizializzaTORN(" + codgar + "): inizio metodo");

		try {
			List datiTORN = this.sqlManager.getListVector(
					"select TIPGAR, MODLIC, TIPTOR, CORGAR, OFFAUM, TIPGEN, DINVIT, ITERGA, ACCQUA, "
							+ "IMPTOR, MODCONT, SELPAR, INVERSA, ALTRISOG from TORN where CODGAR = ?",
					new Object[] { codgar });
			if (datiTORN != null && datiTORN.size() > 0) {

				Long tipgar = SqlManager.getValueFromVectorParam(
						datiTORN.get(0), 0).longValue();
				if (tipgar == null)
					tipgar = new Long(0);
				hMap.put("tipgar", tipgar);

				Long modlic = SqlManager.getValueFromVectorParam(
						datiTORN.get(0), 1).longValue();
				if (modlic == null)
					modlic = new Long(0);
				hMap.put("modlic", modlic);

				String tiptor = SqlManager.getValueFromVectorParam(
						datiTORN.get(0), 2).toString();
				if (tiptor == null)
					tiptor = "";
				hMap.put("tiptor", tiptor);

				Double corgar = SqlManager.getValueFromVectorParam(
						datiTORN.get(0), 3).doubleValue();
				if (corgar == null)
					corgar = new Double(0);
				hMap.put("corgar", corgar);

				String offaum = SqlManager.getValueFromVectorParam(
						datiTORN.get(0), 4).toString();
				if (offaum == null)
					offaum = "";
				hMap.put("offaum", offaum);

				Long tipgen = SqlManager.getValueFromVectorParam(
						datiTORN.get(0), 5).longValue();
				if (tipgen == null)
					tipgen = new Long(1);
				if (tipgen != null
						&& (tipgen.longValue() < 1 || tipgen.longValue() > 3))
					tipgen = new Long(1);
				hMap.put("tipgen", tipgen);

                Timestamp dinvit = SqlManager.getValueFromVectorParam(
                    datiTORN.get(0), 6).dataValue();
                hMap.put("dinvit", dinvit);

                Long iterga = SqlManager.getValueFromVectorParam(
                    datiTORN.get(0),7).longValue();
                if (iterga == null)
                  iterga = new Long(0);
                hMap.put("iterga", iterga);

                String accqua = SqlManager.getValueFromVectorParam(
                    datiTORN.get(0), 8).toString();
                if (accqua == null)
                  accqua = "";
                hMap.put("accqua", accqua);

                Double imptor = SqlManager.getValueFromVectorParam(
                    datiTORN.get(0), 9).doubleValue();
                if (imptor == null)
                  imptor = new Double(0);
                hMap.put("imptor", imptor);

                Long modcont = SqlManager.getValueFromVectorParam(
                    datiTORN.get(0), 10).longValue();
                if (modcont == null)
                  modcont = new Long(0);
                hMap.put("modcont", modcont);

                Long selpar =  SqlManager.getValueFromVectorParam(
                    datiTORN.get(0), 11).longValue();
                if (selpar == null)
                  selpar = new Long(0);
                hMap.put("selpar", selpar);

                String inversa = SqlManager.getValueFromVectorParam(
                    datiTORN.get(0), 12).toString();
                if (inversa == null)
                  inversa = "";
                hMap.put("inversa", inversa);

                Long altrisog = SqlManager.getValueFromVectorParam(
                    datiTORN.get(0), 13).longValue();
                if (altrisog == null)
                  altrisog = new Long(0);
                hMap.put("altrisog", altrisog);
			}
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nella lettura e nell'inizializzazione dei parametri della tornata",
					"inizializzaTORN", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("inizializzaTORN(" + codgar + "): fine metodo");
	}

	/**
	 * Inizializza alcuni dati ricavati dall'entità GARE
	 *
	 * @param ngara
	 * @param hMap
	 * @throws GestoreException
	 */
	public void inizializzaGARE(String ngara, HashMap hMap)
			throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("inizializzaGARE(" + ngara + "): inizio metodo");

		String selectGARE = "select tipgarg, modlicg, impapp, modastg, impsic, "
			+	"sicinc, corgar1, limmax, nofval, onprge, impnrl, ridiso, calcsoang, "
			+   "dpubavg, onsogrib, detlicg "
			+	"from gare where ngara = ?";

		try {
			List datiGARE = this.sqlManager.getListVector(selectGARE,
					new Object[] { ngara });
			if (datiGARE != null && datiGARE.size() > 0) {

				Long tipgarg = SqlManager.getValueFromVectorParam(
						datiGARE.get(0), 0).longValue();
				if (tipgarg == null)
					tipgarg = new Long(0);
				hMap.put("tipgarg", tipgarg);

				Long modlicg = SqlManager.getValueFromVectorParam(
						datiGARE.get(0), 1).longValue();
				if (modlicg == null)
					modlicg = new Long(0);
				hMap.put("modlicg", modlicg);

				Double impapp = SqlManager.getValueFromVectorParam(
						datiGARE.get(0), 2).doubleValue();
				if (impapp == null)
					impapp = new Double(0);
				hMap.put("impapp", impapp);

				Long modastg = SqlManager.getValueFromVectorParam(
						datiGARE.get(0), 3).longValue();
				if (modastg == null || modastg.longValue() == 0)
					modastg = new Long(2);
				hMap.put("modastg", modastg);

				Double impsic = SqlManager.getValueFromVectorParam(
						datiGARE.get(0), 4).doubleValue();
				if (impsic == null)
					impsic = new Double(0);
				hMap.put("impsic", impsic);

				String sicinc = SqlManager.getValueFromVectorParam(
						datiGARE.get(0), 5).toString();
				if (sicinc == null)
					sicinc = "1";
				hMap.put("sicinc", sicinc);

				Double corgar1 = SqlManager.getValueFromVectorParam(
						datiGARE.get(0), 6).doubleValue();
				if (corgar1 == null)
					corgar1 = new Double(0);
				hMap.put("corgar1", corgar1);

				Double limmax = SqlManager.getValueFromVectorParam(
						datiGARE.get(0), 7).doubleValue();
				if (limmax == null)
					limmax = new Double(0);
				hMap.put("limmax", limmax);

				Long nofval = SqlManager.getValueFromVectorParam(
						datiGARE.get(0), 8).longValue();
				if (nofval == null)
					nofval = new Long(0);
				hMap.put("nofval", nofval);

				Double onprge = SqlManager.getValueFromVectorParam(
						datiGARE.get(0), 9).doubleValue();
				if (onprge == null)
					onprge = new Double(0);
				hMap.put("onprge", onprge);

				Double impnrl = SqlManager.getValueFromVectorParam(
						datiGARE.get(0), 10).doubleValue();
				if (impnrl == null)
					impnrl = new Double(0);
				hMap.put("impnrl", impnrl);

				String ridiso = SqlManager.getValueFromVectorParam(
						datiGARE.get(0), 11).getStringValue();
				if (ridiso == null
						|| (ridiso != null && (ridiso.equals("") || ridiso
								.equals("0"))))
					ridiso = "2";
				hMap.put("ridiso", ridiso);

				String calcsoang = SqlManager.getValueFromVectorParam(
						datiGARE.get(0), 12).getStringValue();
				hMap.put("calcsoang", calcsoang);

                Timestamp dpubavg = SqlManager.getValueFromVectorParam(
                    datiGARE.get(0), 13).dataValue();
                hMap.put("dpubavg", dpubavg);

                String onsogrib = SqlManager.getValueFromVectorParam(
                    datiGARE.get(0), 14).toString();
                hMap.put("onsogrib", onsogrib);

                Long detlicg = SqlManager.getValueFromVectorParam(
                    datiGARE.get(0), 15).longValue();
                if (detlicg == null)
                  detlicg = new Long(0);
                hMap.put("detlicg", detlicg);
    			}
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nella lettura e nell'inizializzazione dei parametri della gara",
					"inizializzaGARE", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("inizializzaGARE(" + ngara + "): fine metodo");
	}

	/**
     * Inizializza alcuni dati ricavati dall'entità GARE
     *
     * @param ngara
     * @param hMap
     * @throws GestoreException
     */
    public void inizializzaGareDaGare1(String ngara, HashMap hMap)
            throws GestoreException {

        if (logger.isDebugEnabled())
            logger.debug("inizializzaGareDaGare1(" + ngara + "): inizio metodo");

        String selectGARE1 = "select ultdetlic, costofisso, riptec, ripeco, metpunti, aqoper, aqnumope, escauto  "
            +   "from gare1 where ngara = ?";

        try {
            List datiGARE1 = this.sqlManager.getListVector(selectGARE1,
                    new Object[] { ngara });
            if (datiGARE1 != null && datiGARE1.size() > 0) {

              Long ultdetlic = SqlManager.getValueFromVectorParam(
                  datiGARE1.get(0), 0).longValue();
              hMap.put("ultdetlic", ultdetlic);

              String costofisso = SqlManager.getValueFromVectorParam(
                  datiGARE1.get(0), 1).stringValue();
              if(costofisso==null)
                costofisso="";
              hMap.put("costofisso", costofisso);

              Long riptec = SqlManager.getValueFromVectorParam(
                  datiGARE1.get(0), 2).longValue();
              hMap.put("riptec", riptec);

              Long ripeco = SqlManager.getValueFromVectorParam(
                  datiGARE1.get(0), 3).longValue();
              hMap.put("ripeco", ripeco);

              Long metpunti = SqlManager.getValueFromVectorParam(
                  datiGARE1.get(0), 4).longValue();
              hMap.put("metpunti", metpunti);

              Long aqoper = SqlManager.getValueFromVectorParam(
                  datiGARE1.get(0), 5).longValue();
              hMap.put("aqoper", aqoper);

              Long aqnumope = SqlManager.getValueFromVectorParam(
                  datiGARE1.get(0), 6).longValue();
              hMap.put("aqnumope", aqnumope);

              Long escauto = SqlManager.getValueFromVectorParam(
                  datiGARE1.get(0), 7).longValue();
              hMap.put("escauto", escauto);

            }
        } catch (SQLException e) {
            throw new GestoreException(
                    "Errore nella lettura e nell'inizializzazione dei parametri della gara",
                    "inizializzaGARE", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("inizializzaGareDaGare1(" + ngara + "): fine metodo");
    }


    /**
     * Inizializza alcuni dati ricavati dall'entità GARECONT
     *
     * @param ngara
     * @param hMap
     * @throws GestoreException
     */
    public void inizializzaGarecontNoOU(String ngara, HashMap hMap)
            throws GestoreException {

        if (logger.isDebugEnabled())
            logger.debug("inizializzaGarecont(" + ngara + "): inizio metodo");

        String selectGARECONT = "select esecscig  "
            +   "from garecont where ngara = ? and ncont = ? ";

        try {
            List datiGARECONT = this.sqlManager.getListVector(selectGARECONT,
                    new Object[] { ngara, new Long(1)});
            if (datiGARECONT != null && datiGARECONT.size() > 0) {
              String esecscig = SqlManager.getValueFromVectorParam(datiGARECONT.get(0), 0).stringValue();
              hMap.put("esecscig", esecscig);
            }
        } catch (SQLException e) {
            throw new GestoreException(
                    "Errore nella lettura e nell'inizializzazione dei parametri della gara",
                    "inizializzaGARECONT", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("inizializzaGarecont(" + ngara + "): fine metodo");
    }


    /**
     * Inizializza il valore del campo GARE1.LEGREGSIC
     *
     * @param ngara
     * @param hMapGARE
     * @param legRegSic
     * @throws GestoreException
     */
    private void inizializzaLEGREGSIC(String ngara, HashMap hMapGARE, String legRegSic)
            throws GestoreException {

        if (logger.isDebugEnabled())
            logger.debug("inizializzaLEGREGSIC(" + ngara + "): inizio metodo");

          hMapGARE.put("legRegSic", legRegSic);

        if (logger.isDebugEnabled())
            logger.debug("inizializzaLEGREGSIC(" + ngara + "): fine metodo");
    }

    /**
	 * Inizializza il valore del campo GARE.PRECUT
	 *
	 * @param ngara
	 * @param hMapGARE
	 * @param precut
	 * @throws GestoreException
	 */
	private void inizializzaPRECUT(String ngara, HashMap hMapGARE, Long precut)
			throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("inizializzaPRECUT(" + ngara + "): inizio metodo");

		if (precut == null)
			precut = new Long(0);
		hMapGARE.put("precut", precut);


		if (logger.isDebugEnabled())
			logger.debug("inizializzaPRECUT(" + ngara + "): fine metodo");
	}

	/**
	 * Inizializza il correttivo di gara nella HashMap hMapParametri con la
	 * chiave "correttivoGara"
	 *
	 * @param ngara
	 * @param hMapTORN
	 * @param hMapGARE
	 * @param hMapParametri
	 * @throws GestoreException
	 */
	private void inizializzaCORGAR(String ngara, HashMap hMapTORN,
			HashMap hMapGARE, HashMap hMapParametri) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("inizializzaCORGAR(" + ngara + "): inizio metodo");

		long tipgen = 0;

		if (hMapTORN.get("tipgen") != null) {
			tipgen = ((Long) hMapTORN.get("tipgen")).longValue();
		}

		// Si ricava da corgar.torn o da codgar1.gare
		// Per i valori di TIPGEN.TORN 2 o 3 si deve considerare
		// il correttivo della tabella GARE (corgar1.gare) mentre negli altri
		// casi si deve considerare il correttivo della TORN (corgar.torn).
		if (tipgen == 1) {
			hMapParametri.put("corgar", hMapTORN.get("corgar"));
		} else {
			hMapParametri.put("corgar", hMapGARE.get("corgar1"));
		}

		if (logger.isDebugEnabled())
			logger.debug("inizializzaCORGAR(" + ngara + "): fine metodo");

	}


	/**
	 * Set nella HashMap hMapParametri due flag: uno di applicabilita' del calcolo
	 * soglia anomalia e uno per applicabilita' dell'esclusione automatica.
	 * Questi due flag vengono usati per gare con modlicg = 13 e 14 (che
	 * implicitamente hanno il campo GARE.CALCSOANG = 1) e modastg = 1
	 *
     * Aggiunto il ritorno di un array contenente l'esito del controllo
     * per gestire il msg di avviso al termine della funzione
     * di calcolo soglia anomalia:
     * ret[0]   tipo di esito. Vuoto se non si rientra nei casi in cui il calcolo della soglia anomalia non può essere fatto
     * ret[1]   numero ditte soglia
     * ret[2]   tipo di gara (lavori, forniture, servizi)
     * ret[3]   importo soglia
     * ret[4]   il confronto esclude o meno l'importo soglia (< e non <=)
	 *
	 * @param hMapTORN
	 * @param hMapGARE
	 * @param hMapParametri
	 * @throws GestoreException
	 */
	private String[] inizializzaFlags(HashMap hMapTORN, HashMap hMapGARE,
			HashMap hMapParametri) throws GestoreException {
		if (logger.isDebugEnabled())
			logger.debug("inizializzaFlags: inizio metodo");

        long modlicg = 0;
        long numeroDitteAmmesse = 0;
        long tipgen = 0;
        long modastg = 0;
        long numMinDitteCalcoloSoglia = 0;
        long numMinDitteCalcoloSogliaImporto = 0;
        double importoCalcoloSoglia=0;
        boolean isSogliaEstremoEscluso = false;

        boolean isCalcoloSogliaAnomaliaApplicabile = true;
		boolean isEsclusioneAutomaticaApplicabile = false;
		String[] ret = new String[5];
		boolean modalitaDL2016 = false;
		boolean modalitaDL2017 = false;
		boolean modalitaDL2019 = false;
		double importoGara =0;
		boolean modalitaDLCalcoloGraduatoria=false;
		boolean controlloOPEVNumDitteNormativaSuperato=true;
		long numMinDitteCalcoloSogliaOEPV = 0;
		String calcsoang = null;

		String descrizioneTabellato = this.leggiDescrizioneDaTab1("A1160", new Long(1));
        if (descrizioneTabellato != null) {
          boolean troncamento =false;
          descrizioneTabellato = descrizioneTabellato.substring(0, 1);
          if("2".equals(descrizioneTabellato))
            troncamento=true;
          hMapParametri.put("troncamento", new Boolean(troncamento));
        }

        if (hMapGARE.get("modlicg") != null)
          modlicg = ((Long) hMapGARE.get("modlicg")).longValue();


        if(hMapGARE.get("calcsoang") != null)
            calcsoang = (String) hMapGARE.get("calcsoang");

        if (hMapParametri.get("modalitaDLCalcoloGraduatoria") != null)
          modalitaDLCalcoloGraduatoria = ((Boolean) hMapParametri.get("modalitaDLCalcoloGraduatoria")).booleanValue();

        if (hMapParametri.get("numeroDitteAmmesse") != null)
          numeroDitteAmmesse = ((Long) hMapParametri.get("numeroDitteAmmesse")).longValue();

        if (hMapParametri.get("numMinDitteCalcoloSoglia") != null)
          numMinDitteCalcoloSoglia = ((Long) hMapParametri.get("numMinDitteCalcoloSoglia")).longValue();

        if(modlicg == 6){


          if (hMapParametri.get("controlloOPEVNumDitteNormativaSuperato") != null)
            controlloOPEVNumDitteNormativaSuperato = ((Boolean)hMapParametri.get("controlloOPEVNumDitteNormativaSuperato")).booleanValue();
          if (hMapParametri.get("numMinDitteCalcoloSogliaOEPV") != null)
            numMinDitteCalcoloSogliaOEPV = ((Long) hMapParametri.get("numMinDitteCalcoloSogliaOEPV")).longValue();
        }

        String legRegSic = "";
        if (hMapGARE.get("legRegSic") != null)
          legRegSic = ( (String) hMapGARE.get("legRegSic"));

        if ((modlicg == 13 || modlicg == 14) && !modalitaDLCalcoloGraduatoria){
          if (hMapTORN.get("tipgen") != null)
  			tipgen = ((Long) hMapTORN.get("tipgen")).longValue();
          if (hMapGARE.get("modastg") != null)
            modastg = ((Long) hMapGARE.get("modastg")).longValue();

          if (hMapParametri.get("numMinDitteCalcoloSogliaImporto") != null)
            numMinDitteCalcoloSogliaImporto = ((Long) hMapParametri.get("numMinDitteCalcoloSogliaImporto")).longValue();
          if (hMapParametri.get("importoCalcoloSoglia") != null)
            importoCalcoloSoglia = ((Double) hMapParametri.get("importoCalcoloSoglia")).doubleValue();
          if (hMapParametri.get("isSogliaEstremoEscluso") != null)
            isSogliaEstremoEscluso = ((Boolean) hMapParametri.get("isSogliaEstremoEscluso")).booleanValue();
          if (hMapParametri.get("modalitaDL2016") != null)
            modalitaDL2016 = ((Boolean) hMapParametri.get("modalitaDL2016")).booleanValue();
          if (hMapParametri.get("modalitaDL2017") != null)
            modalitaDL2017 = ((Boolean) hMapParametri.get("modalitaDL2017")).booleanValue();
          if (hMapParametri.get("modalitaDL2019") != null)
            modalitaDL2019 = ((Boolean) hMapParametri.get("modalitaDL2019")).booleanValue();
          if (hMapParametri.get("importoGara") != null)
            importoGara = ((Double) hMapParametri.get("importoGara")).doubleValue();
          String escautoString=null;
          if (hMapGARE.get("escauto") != null){
            Long escauto = ( (Long) hMapGARE.get("escauto"));
            escautoString = escauto.toString();
          }

          if(numeroDitteAmmesse < numMinDitteCalcoloSoglia){
            ret[0] = "ControlloNumeroDitteNonSuperato";
            ret[1] = new Long(numMinDitteCalcoloSoglia).toString();
            isCalcoloSogliaAnomaliaApplicabile = false;
          }else if((modlicg == 13 || modlicg == 14) && modastg == 1 && !modalitaDL2016 && !modalitaDL2017 && !modalitaDL2019 && !"1".equals(legRegSic) &&
              numeroDitteAmmesse < numMinDitteCalcoloSogliaImporto &&
              ((!isSogliaEstremoEscluso && importoGara<=importoCalcoloSoglia) || (isSogliaEstremoEscluso && importoGara<importoCalcoloSoglia))){
            if (this.getModalitaCalcoloGareSottoSoglia() == 0) {
              ret[0] = "ControlloNumeroDitteImportoNonSuperato";
              ret[1] = new Long(numMinDitteCalcoloSogliaImporto).toString();
              ret[2] = new Long(tipgen).toString();
              ret[3] = new Double(importoCalcoloSoglia).toString();
              ret[4] = new Boolean(isSogliaEstremoEscluso).toString();
              isCalcoloSogliaAnomaliaApplicabile = false;
            }
          }else if((modlicg == 13 || modlicg == 14) && modastg == 1 && (modalitaDL2016 || modalitaDL2017 || modalitaDL2019 || "1".equals(legRegSic)) &&
              numeroDitteAmmesse < numMinDitteCalcoloSogliaImporto && importoGara<importoCalcoloSoglia){
              ret[0] = "ControlloNumeroDitteImportoNonSuperatoDL2016";
              ret[1] = new Long(numMinDitteCalcoloSogliaImporto).toString();
              ret[2] = new Long(tipgen).toString();
              ret[3] = new Double(importoCalcoloSoglia).toString();
          }else if((modlicg == 13 || modlicg == 14) && modastg == 1 && (modalitaDL2016 || modalitaDL2017 || modalitaDL2019 || "1".equals(legRegSic)) &&
              importoGara>=importoCalcoloSoglia){
              ret[0] = "ControlloImportoSopraSoglia";
              ret[1] = null;
              ret[2] = new Long(tipgen).toString();
              ret[3] = new Double(importoCalcoloSoglia).toString();
          }

          if (modastg ==1 && !modalitaDL2016 && !modalitaDL2017 && !modalitaDL2019 && !"1".equals(legRegSic) &&
                  numeroDitteAmmesse >= numMinDitteCalcoloSogliaImporto &&
                  ((!isSogliaEstremoEscluso && importoGara <= importoCalcoloSoglia) || (isSogliaEstremoEscluso && importoGara < importoCalcoloSoglia))){
    		isEsclusioneAutomaticaApplicabile = true;
          }else if(modastg ==1 && (modalitaDL2016 || modalitaDL2017 || modalitaDL2019 || "1".equals(legRegSic)) && numeroDitteAmmesse >= numMinDitteCalcoloSogliaImporto &&
              importoGara < importoCalcoloSoglia){
            isEsclusioneAutomaticaApplicabile = true;
          }
          if("1".equals(escautoString) || "2".equals(escautoString))
            isEsclusioneAutomaticaApplicabile = true;
          else if("3".equals(escautoString) || "4".equals(escautoString))
            isEsclusioneAutomaticaApplicabile = false;

        }else if(modlicg == 6 && "1".equals(calcsoang) && !controlloOPEVNumDitteNormativaSuperato){
          ret[0] = "ControlloNumeroDitteNonSuperato";
          ret[1] = new Long(numMinDitteCalcoloSogliaOEPV).toString();
        }else if(modalitaDLCalcoloGraduatoria && numeroDitteAmmesse < numMinDitteCalcoloSoglia){//In questo modo viene tracciato in W_LOGEVENTI il fatto che si sta applicando il calcolo graduatoria
          ret[0] = "ControlloNumeroDitteNonSuperato";
          ret[1] = new Long(numMinDitteCalcoloSoglia).toString();
        }

		hMapParametri.put("isCalcoloSogliaAnomaliaApplicabile", new Boolean(isCalcoloSogliaAnomaliaApplicabile));
		hMapParametri.put("isEsclusioneAutomaticaApplicabile", new Boolean(isEsclusioneAutomaticaApplicabile));

		if (logger.isDebugEnabled())
			logger.debug("inizializzaFlags: fine metodo");

		return ret;
	}

	/**
	 * Inizializza i valori massimi per i punteggi tecnico ed econimico nel caso
	 * di una gara "Offerta economicamente più vantaggiosa"
	 *
	 * @param ngara
	 * @param hMapTORN
	 * @param hMapGARE
	 * @param hMapParametri
	 *            Lista delle chiavi presenti nella HashMap hMapParametri
	 *            <ul>
	 *            <li>sommaPunteggioMassimoTecnico --> punteggio massimo
	 *            tecnico
	 *            <li>sommaPunteggioMassimoEconomico --> punteggio massimo
	 *            economico
	 *            </ul>
	 *
	 *
	 * @throws GestoreException
	 */
	private void inizializzaPunteggiMassimi(String ngara, HashMap hMapGARE,
			HashMap hMapParametri) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("inizializzaPunteggiMassimi(" + ngara
					+ "): inizio metodo");

		long modlicg = 0;
		if (hMapGARE.get("modlicg") != null)
			modlicg = ((Long) hMapGARE.get("modlicg")).longValue();

		Double sommaPunteggioMassimoTecnico = null;
		Double sommaPunteggioMassimoEconomico = null;

		if (modlicg == 6) {

		  String abilitataGestioneCriteriPrezzo = "0";
          if (hMapParametri.get("abilitataGestioneCriteriPrezzo") != null)
            abilitataGestioneCriteriPrezzo = ((String) hMapParametri.get("abilitataGestioneCriteriPrezzo"));

          boolean esistonoCriteriIsnprz = false;

		    // Somma del punteggio massimo tecnico
			String selectPunteggioMassimoTecnico =
				"select sum(maxpun) from goev where ngara=? and tippar=1 and maxpun is not null and (LIVPAR = 1 or LIVPAR = 3) ";

			 if("1".equals(abilitataGestioneCriteriPrezzo)){
	              if (hMapParametri.get("esistonoCriteriIsnprz") != null)
	                esistonoCriteriIsnprz = (((Boolean) hMapParametri.get("esistonoCriteriIsnprz"))).booleanValue();
	              if(esistonoCriteriIsnprz)
	                selectPunteggioMassimoTecnico =
	                "select sum(maxpun) from goev where ngara=? and maxpun is not null and (LIVPAR = 1 or LIVPAR = 3) and (tippar=1 or (tippar=2 and isnoprz='1'))";
	            }

			try {

			  Object sommaPunteggiTecnici = this.sqlManager.getObject(selectPunteggioMassimoTecnico, new Object[] { ngara });
			  sommaPunteggioMassimoTecnico = pgManager.getValoreImportoToDouble(sommaPunteggiTecnici);
			} catch (SQLException e) {
				throw new GestoreException(
						"Errore nel calcolo del somma del punteggio massimo tecnico",
						"sommaPunteggioMassimoTecnico", e);
			}

			// Somma del punteggio massimo economico
			String selectPunteggioMassimoEconomico =
				"select sum(maxpun) from goev where ngara=? and tippar=2 and maxpun is not null and (LIVPAR = 1 or LIVPAR = 3)";

			if("1".equals(abilitataGestioneCriteriPrezzo) && esistonoCriteriIsnprz)
              selectPunteggioMassimoEconomico =
                "select sum(maxpun) from goev where ngara=? and maxpun is not null and (LIVPAR = 1 or LIVPAR = 3) and tippar=2 and (isnoprz='2' or isnoprz is null or isnoprz='')";

			try {

			  Object sommaPunteggiEconomici = this.sqlManager.getObject(selectPunteggioMassimoEconomico, new Object[] { ngara });
			  sommaPunteggioMassimoEconomico = pgManager.getValoreImportoToDouble(sommaPunteggiEconomici);
			} catch (SQLException e) {
				throw new GestoreException(
						"Errore nel calcolo del somma del punteggio massimo economico",
						"sommaPunteggioMassimoEconomico", e);
			}
		}

		hMapParametri.put("sommaPunteggioMassimoTecnico", sommaPunteggioMassimoTecnico);
		hMapParametri.put("sommaPunteggioMassimoEconomico", sommaPunteggioMassimoEconomico);

		if (logger.isDebugEnabled())
			logger.debug("inizializzaPunteggiMassimi(" + ngara
					+ "): fine metodo");
	}


	/**
	 * Verifica se ci sono ditte ammesse alla gara
	 *
	 * @param ngara
	 * @param filtro
	 * @return
	 *            <ul>
	 *            <li>TRUE: ci sono ditte ammesse
	 *            <li>FALSE: non ci sono ditte ammesse
	 *            </ul>
	 * @throws GestoreException
	 */
	public boolean esistonoDitte(String ngara, String filtro)
			throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("esistonoDitteAmmesse(" + ngara + "): inizio metodo");

		boolean result = false;
		Long numeroDitteAmmesse = this.conteggioDitte(ngara, filtro);
        if (numeroDitteAmmesse != null
            && numeroDitteAmmesse.longValue() > 0)
        result = true;

        if (logger.isDebugEnabled())
			logger.debug("esistonoDitteAmmesse(" + ngara + "): fine metodo");

		return result;
	}

	/**
	 * Determino se la gara non si trova in una fase di gara precedente alla
	 * fase di aggiudicazione
	 *
	 * @return Ritorna true se la fara di gara e' >= 6, false altrimenti
	 * @throws GestoreException
	 */
	public boolean isFaseDiGaraAggiudicazione(String ngara)
			throws GestoreException {
		boolean result = false;

		if (logger.isDebugEnabled())
			logger.debug("isFaseDiGaraAggiudicazione(" + ngara + "): inizio metodo");

		String selectFaseDigara = "select fasgar from gare where ngara = ?";

		try {
			Long faseDiGara = (Long) this.sqlManager.getObject(
					selectFaseDigara, new Object[] { ngara });
			if (faseDiGara != null) {
				if (faseDiGara.intValue() >= GestioneFasiGaraFunction.FASE_CALCOLO_AGGIUDICAZIONE/10) {
					result = true;
				}
			}
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nella verifica della fase di gara attiva per la "
							+ "gara in analisi (codgar = " + ", ngara = "
							+ ngara, "isFaseDiGaraAggiudicazione", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("isFaseDiGaraAggiudicazione(" + ngara
					+ "): fine metodo");

		return result;
	}

	/**
	 * Conteggio delle ditte ammesse. Il conteggio avviene considerando le ditte
	 * ammesse alla gara. Se necessario è possibile aggiungere ulteriori
	 * condizioni di filtro (per esempio sullo stato aggiudicazione)
	 *
	 * @param ngara
	 * @param filtro
	 * @return
	 * @throws GestoreException
	 */
	public Long conteggioDitte(String ngara, String filtro)
			throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("conteggioDitte(" + ngara + "," + filtro
					+ "): inizio metodo");

		String selectDitteAmmesse = "select count(*) from ditg where ngara5 = ?";
		Long numeroDitteAmmesse = new Long(0);

		if (filtro != null)
			selectDitteAmmesse += " and " + filtro;

		try {
			List ret = this.sqlManager.getListVector(selectDitteAmmesse,
					new Object[] { ngara });
			if (ret != null && ret.size() > 0) {
				numeroDitteAmmesse = SqlManager.getValueFromVectorParam(
						ret.get(0), 0).longValue();
			}
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nel conteggio delle ditte ammesse alla gara",
					"conteggioDitteAmmesse", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("conteggioDitte(" + ngara + "," + filtro	+ "): fine metodo");

		return numeroDitteAmmesse;
	}

	/**
	 * Controlla se ci sono ditte riammesse.
	 *
	 * @param ngara
	 * @param controlloPuneco
	 * @return
	 *            <ul>
	 *            <li>TRUE: ci sono ditte riammesse
	 *            <li>FALSE: non ci sono ditte riammesse
	 *            </ul>
	 * @throws GestoreException
	 */
	public boolean esistonoDitteRiammesse(String ngara, boolean controlloPuneco) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("esistonoDitteRiammesse(" + ngara + "): inizio metodo");

		boolean result = false;

		String selectMODLICG = "select modlicg from gare where ngara = ?";
		String selectDitteRiammesse = null;

		try {
			Long modlicg = (Long) this.sqlManager.getObject(selectMODLICG,
					new Object[] { ngara });

			if (modlicg.equals(new Long(6))) {
				selectDitteRiammesse = "select count(*) from ditg where ngara5 = ? "
						+ "and (((ammgar <> '2' or ammgar is null) and ";
				if(controlloPuneco)
				  selectDitteRiammesse += "(puntec is null or puneco is null)) ";
				else
				  selectDitteRiammesse += "puntec is null) ";
				selectDitteRiammesse += "or (moties = 100))";
			} else {
				selectDitteRiammesse = "select count(*) from ditg where ngara5 = ? "
						+ "and (((ammgar <> '2' or ammgar is null) and ribauo is null) or (moties = 100))";
			}

			List ret = this.sqlManager.getListVector(selectDitteRiammesse,
					new Object[] { ngara });
			if (ret != null && ret.size() > 0) {
				Long numeroDitteRiammesse = SqlManager.getValueFromVectorParam(
						ret.get(0), 0).longValue();
				if (numeroDitteRiammesse != null
						&& numeroDitteRiammesse.longValue() > 0)
					result = true;
			}
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nella verifica delle ditte riammesse alla gara",
					"esistonoDitteRiammesse", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("esistonoDitteRiammesse(" + ngara + "): fine metodo");
		return result;
	}

	/**
	 * Conteggio delle ditte riammesse alla gara
	 *
	 * @param ngara
	 * @return
	 * @throws GestoreException
	 */
	public Long conteggioDitteRiammesse(String ngara) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("conteggioDitteRiammesse(" + ngara	+ "): inizio metodo");

		String selectDitteRiammesse = "select count(*) from ditg where ngara5 = ? "
				+ "and ((ammgar <> '2' and ribauo is null) or (moties = 100))";

		Long numeroDitteRiammesse = new Long(0);

		try {
			List ret = this.sqlManager.getListVector(selectDitteRiammesse,
					new Object[] { ngara });
			if (ret != null && ret.size() > 0) {
				numeroDitteRiammesse = SqlManager.getValueFromVectorParam(
						ret.get(0), 0).longValue();
			}
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nel conteggio delle ditte riammesse alla gara",
					"conteggioDitteRiammesse", e);
		}
		if (logger.isDebugEnabled())
			logger.debug("conteggioDitteRiammesse(" + ngara + "): fine metodo");

		return numeroDitteRiammesse;
	}

	/**
	 * Setta il campo MOTIES a null per il caso delle ditte riammesse
	 *
	 * @param ngara
	 * @throws GestoreException
	 */
	private void settaMotiesANull(String ngara) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("settaMotiesANull(" + ngara + "): inizio metodo");

		String updateRibassoAZero = "update ditg set moties=null where ngara5=? "
				+ "and ((ammgar <> '2' and ribauo is null) or (moties = 100))";

		try {
			this.sqlManager.update(updateRibassoAZero, new Object[] { ngara });
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore durante l'aggiornamento del motivo di esclusione per le ditte riammesse alla gara",
					"settaMotiesANull", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("settaMotiesANull(" + ngara + "): fine metodo");
	}

	/**
	 * Setta a 0 il valore del ribasso (RIBAUO.DITG) per le ditte riammesse che
	 * hanno un ribasso nullo
	 *
	 * @param ngara
	 * @throws GestoreException
	 */
	private void settaRibassoAZero(String ngara) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("settaRibassoAZero(" + ngara + "): inizio metodo");

		String updateRibassoAZero =
			"update ditg set ribauo=0, ammgar='1',invoff='1' where  "
				+ "(ammgar<>'2' or ammgar is null) and ngara5=? and (ribauo is null)";

		try {
			this.sqlManager.update(updateRibassoAZero, new Object[] { ngara });
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore durante l'aggiornamento del ribasso per le ditte il cui ribasso è nullo",
					"settaRibassoAZero", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("settaRibassoAZero(" + ngara + "): fine metodo");
	}

	/**
	 * Setta a 0 il valore del punteggio tecnico (PUNTEC.DITG) o del punteggio
	 * econimico (PUNECO.DITG) per le ditte riamesse che hanno uno o emtrambi i
	 * parametri nulli
	 *
	 * @param ngara
	 * @throws GestoreException
	 */
	private void settaPunteggiAZero(String ngara) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("settaPunteggiAZero(" + ngara + "): inizio metodo");

		// Punteggio tecnico
		String updatePunteggioTecnicoAZero =
			"update ditg set puntec=0, puntecrip=0, ammgar='1',invoff='1' where  "
			+ " (ammgar<>'2' or ammgar is null) and ngara5=? and (puntec is null)";

		try {
			this.sqlManager.update(updatePunteggioTecnicoAZero,
					new Object[] { ngara });
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore durante l'aggiornamento del punteggio tecnico per le ditte il cui punteggio tecnico è nullo",
					"settaPunteggioTecnicoAZero", e);
		}

		// Punteggio economico
		String updatePunteggioEconomicoAZero =
			"update ditg set puneco=0,punecorip=0,ammgar='1',invoff='1' where  "
			+ " (ammgar<>'2' or ammgar is null) and ngara5=? and (puneco is null)";

		try {
			this.sqlManager.update(updatePunteggioEconomicoAZero,
					new Object[] { ngara });
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore durante l'aggiornamento del punteggio economico per le ditte il cui punteggio economico è nullo",
					"settaPunteggioEconomicoAZero", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("settaPunteggiAZero(" + ngara + "): fine metodo");
	}

	/**
	 * Si forza a 1 i campi AMMGAR  che sono rimasti nulli (E' una
	 * forzatura!!!)
	 *
	 * @param ngara
	 * @throws GestoreException
	 */
	private void settaAMMAGAR_INVOFF(String ngara) throws GestoreException {
		if (logger.isDebugEnabled())
			logger.debug("settaAMMAGAR_INVOFF(" + ngara + "): inizio metodo");

		String updateAMMGAR = "update ditg set ammgar='1' where ammgar is null and ngara5 = ? ";
		String updateINVOFF = "update ditg set invoff='1' where invoff is null and ngara5 = ? and ammgar='1'";
		try {
			this.sqlManager.update(updateAMMGAR, new Object[] { ngara });
			this.sqlManager.update(updateINVOFF, new Object[] { ngara });
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore durante l'aggiornamento di AMMGAR e INVOFF da null al valore 1",
					"settaAMMAGAR_INVOFF", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("settaAMMAGAR_INVOFF(" + ngara + "): fine metodo");
	}

	/**
	 * Inizializza i parametri nella hashmap hMapParametri
	 *
	 * @param hMapTORN
	 * @param hMapGARE
	 * @param hMapParametri Lista delle chiavi presenti nella HashMap hMapParametri
	 *            <ul>
	 *            <li>numeroDitteAmmesse --> numero ditte ammesse alla gara (si
	 *            intendono le ditte con offerte valide)
	 *            <li>numeroDitteRiammesse --> numero ditte riammesse alla gara
	 *            <li>minimoPerCorrettivo --> valore minimo per la gestione del
	 *            correttivo di gara
	 *            <li>percentualeDitteDaEscludere --> percentuale ditte da
	 *            escludere (calcolo delle ali)
	 *            <li>numeroDitte --> numero di ditte che rientrano nel calcolo
	 *            <li>corgar --> correttivo di gara
	 *            <li>mediaRibasso --> valore della media del ribasso
	 *            <li>limiteAnomalia --> valore del limite anomalia nel calcolo
	 *            della media corretta
	 *            </ul>
	 * @throws GestoreException
	 */
	private void inizializzaMinimoPerCorrettivo(String ngara, String codgar, HashMap hMapTORN,
			HashMap hMapGARE, HashMap hMapParametri) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("inizializzaMinimoPerCorrettivo(" + ngara
					+ "): inizio metodo");

		Long minimoPerCorrettivo = new Long(0);
        int modlicg = 0;
        int iterga = 0;
        long numeroDitteAmmesse = 0;
		double corgar = 0;
		double impapp = 0;
        long tipgen = 0;
        long modastg = 0;
        Date dpubavg = null;
        Date dinvit = null;
        boolean garaDLgs_50_2016 = false;
        boolean garaDLgs_56_2017 = false;
        boolean garaDL_2019 = false;
        double imptor = 0;
        long tipologiaGara =0;
        double importoGara=0;
        boolean modalitaDLCalcoloGraduatoria=false;

		// *** Valore minimo per la gestione del correttivo ***
		if (hMapParametri.get("numeroDitteAmmesse") != null)
			numeroDitteAmmesse = ((Long) hMapParametri.get("numeroDitteAmmesse")).longValue();
		if (hMapParametri.get("corgar") != null)
			corgar = ((Double) hMapParametri.get("corgar")).doubleValue();
        if (hMapGARE.get("modlicg") != null)
          modlicg = ((Long) hMapGARE.get("modlicg")).intValue();
		if (hMapGARE.get("impapp") != null)
			impapp = ((Double) hMapGARE.get("impapp")).doubleValue();
        if (hMapGARE.get("dpubavg") != null)
            dpubavg = (Date) hMapGARE.get("dpubavg");
        if (hMapTORN.get("iterga") != null)
          iterga = ((Long) hMapTORN.get("iterga")).intValue();
		if (hMapTORN.get("tipgen") != null)
			tipgen = ((Long) hMapTORN.get("tipgen")).longValue();
        if (hMapTORN.get("dinvit") != null)
          dinvit = ((Date) hMapTORN.get("dinvit"));
        if (hMapGARE.get("modastg") != null)
          modastg = ((Long) hMapGARE.get("modastg")).longValue();
        if (hMapTORN.get("imptor") != null)
          imptor = ((Double) hMapTORN.get("imptor")).doubleValue();
        if (hMapParametri.get("tipologiaGara") != null)
          tipologiaGara = ((Long) hMapParametri.get("tipologiaGara")).longValue();

        if(tipologiaGara==1 || tipologiaGara==3)
          importoGara = imptor;
        else
          importoGara = impapp;

        hMapParametri.put("importoGara", new Double(importoGara));

        String legRegSic = "";
        if (hMapGARE.get("legRegSic") != null)
          legRegSic = ( (String) hMapGARE.get("legRegSic"));

		if (modlicg == 13 || modlicg == 14) {
		  if (hMapParametri.get("modalitaDL2016") != null)
		    garaDLgs_50_2016 = ((Boolean) hMapParametri.get("modalitaDL2016")).booleanValue();

		  if (hMapParametri.get("modalitaDL2017") != null)
		    garaDLgs_56_2017 = ((Boolean) hMapParametri.get("modalitaDL2017")).booleanValue();

		  if (hMapParametri.get("modalitaDL2019") != null)
            garaDL_2019 = ((Boolean) hMapParametri.get("modalitaDL2019")).booleanValue();

		  //Recupera soglie europee: se DL50/2016, parametro A1125, altrimenti A1019
          int modalita=0;
          if(garaDLgs_50_2016 || garaDLgs_56_2017 || garaDL_2019 || "1".equals(legRegSic)){
            modalita=1;
          }
          Object[] soglia = this.getImportoSogliaPerGara(tipgen,dpubavg,dinvit,iterga,modalita,codgar);
          Double importoSogliaPerGara = (Double) soglia[0];
          Boolean isSogliaEstremoEscluso = false;
          if(!garaDLgs_50_2016 && !garaDLgs_56_2017 && !garaDL_2019 && !"1".equals(legRegSic)){
            isSogliaEstremoEscluso = (Boolean) soglia[1];
          }

          String codiceTabellato = "A2063";
          int numeroVoce=1;
		   //Si verifica se è DLgs.50/2016
		  if(garaDLgs_50_2016 || garaDLgs_56_2017 || garaDL_2019 || "1".equals(legRegSic)){
		    codiceTabellato = "A1135";
		    if (importoGara < importoSogliaPerGara.doubleValue())
		      numeroVoce=2;
		  }

		    String descrizioneTabellato = this.leggiDescrizioneDaTab1(codiceTabellato, new Long(numeroVoce));
			if (descrizioneTabellato != null) {
			  descrizioneTabellato = descrizioneTabellato.substring(0, 1);
			  minimoPerCorrettivo = new Long(descrizioneTabellato);
				//Parametro per gestire il msg di avviso nel calcolo soglia anomalia
		        hMapParametri.put("numMinDitteCalcoloSoglia", minimoPerCorrettivo);
		        if(numeroDitteAmmesse < minimoPerCorrettivo.longValue() && (garaDLgs_50_2016 || garaDLgs_56_2017 || garaDL_2019 ||"1".equals(legRegSic) ))
		          modalitaDLCalcoloGraduatoria=true;
			} else {
				String message = "Non trovato il parametro (" + codiceTabellato + ") delle offerte ammesse, " +
						"cioè il limite per far scattare il correttivo";
				throw new GestoreException(message,	"inizializzaParametri." + codiceTabellato);
			}

			hMapParametri.put("modalitaDLCalcoloGraduatoria", modalitaDLCalcoloGraduatoria);

			if(garaDL_2019){
	            if(modalitaDLCalcoloGraduatoria)
	              hMapParametri.put("sogliaNorma", this.stringaSogliaNorma_DL_32_2019_G);
	            else
	              hMapParametri.put("sogliaNorma", this.stringaSogliaNorma_DL_32_2019_S);
			}

			if(!modalitaDLCalcoloGraduatoria){
    			// Adeguamento DLgs 153/2008
    			if (modastg == 1){
                  if(!garaDLgs_50_2016 && !garaDLgs_56_2017 && !garaDL_2019 && !"1".equals(legRegSic)){
                    if (this.getModalitaCalcoloGareSottoSoglia() == 0) {
                      //Se si applicano le soglie europee, il confronto è con il < e non con il <=
                      if (importoSogliaPerGara != null && numeroDitteAmmesse < 10
                           && ((!isSogliaEstremoEscluso.booleanValue() && importoGara <= importoSogliaPerGara.doubleValue()) ||
                               (isSogliaEstremoEscluso.booleanValue() && importoGara < importoSogliaPerGara.doubleValue())))
                          minimoPerCorrettivo = new Long(10);
                    }
                    //Parametri per gestire il msg di avviso nel calcolo soglia anomalia
                    hMapParametri.put("isSogliaEstremoEscluso", isSogliaEstremoEscluso);
                  }
                  //Parametri per gestire il msg di avviso nel calcolo soglia anomalia
                  hMapParametri.put("numMinDitteCalcoloSogliaImporto", new Long(10));
                  hMapParametri.put("importoCalcoloSoglia", importoSogliaPerGara);
    			}

    			String descrizioneTabellatoA1163 = this.leggiDescrizioneDaTab1("A1163", new Long(1));
    			if (descrizioneTabellatoA1163 != null) {
    			  descrizioneTabellatoA1163 = descrizioneTabellatoA1163.substring(0, 1);
    			  boolean taglioAliEffettivo = false;
    			  if("1".equals(descrizioneTabellatoA1163))
    			    taglioAliEffettivo = true;
    			  hMapParametri.put("taglioAliEffettivo", new Boolean(taglioAliEffettivo));
    			}else {
                  String message = "Non trovato il parametro (A1163) per stabilire, " +
                      "se applicare il taglio delle ali 'fittizio' o 'effettivo'";
              throw new GestoreException(message, "inizializzaParametri.A1163");
          }
			}
		}else if(modlicg == 6){
		  boolean isGaraDL_2019 = this.isDL2019(hMapTORN, hMapGARE, hMapParametri);
		  boolean controlloOPEVNumDitteNormativaSuperato=true;
		  if(isGaraDL_2019){
		    Object[] soglia = this.getImportoSogliaPerGara(tipgen,dpubavg,dinvit,iterga,1,codgar);
		    Double importoSogliaPerGara = (Double) soglia[0];
		    int numeroVoce=1;
		    if (importoGara < importoSogliaPerGara.doubleValue())
              numeroVoce=2;
		    String descrizioneTabellato = this.leggiDescrizioneDaTab1("A1156", new Long(numeroVoce));
            if (descrizioneTabellato != null) {
              descrizioneTabellato = descrizioneTabellato.substring(0, 1);
              Long numDitteNormativa = new Long(descrizioneTabellato);
               if(numeroDitteAmmesse < numDitteNormativa.longValue()){
                 controlloOPEVNumDitteNormativaSuperato=false;
                 hMapParametri.put("numMinDitteCalcoloSogliaOEPV", numDitteNormativa);
               }
            } else {
                String message = "Non trovato il parametro (A1156) del numero minimo per procedure sopra/sotto soglia";
                throw new GestoreException(message, "inizializzaParametri.A1156");
            }
		  }
		  hMapParametri.put("controlloOPEVNumDitteNormativaSuperato", controlloOPEVNumDitteNormativaSuperato);

		}

		if ((modlicg == 1 || modlicg == 5 || modalitaDLCalcoloGraduatoria) && corgar == 0)
			minimoPerCorrettivo = new Long(numeroDitteAmmesse + 1);
		hMapParametri.put("minimoPerCorrettivo", minimoPerCorrettivo);

		// *** percentuale ditte da escludere ***
		Double percentualeDitteDaEscludere = new Double(0);
		if ((modlicg == 13 || modlicg == 14) && !modalitaDLCalcoloGraduatoria) {
			if (numeroDitteAmmesse >= minimoPerCorrettivo.longValue()) {

				if(garaDLgs_56_2017){
    				Long riga= new Long(1);
    				long metsoglia = 0;
    				if (hMapParametri.get("metodoCalcoloSogliaAnomalia") != null)
    	               metsoglia = ((Long) hMapParametri.get("metodoCalcoloSogliaAnomalia")).longValue();
    				if(metsoglia==2)
    				  riga= new Long(2);
    				else if(metsoglia==5)
                      riga= new Long(3);
    				String descrizioneA2065 = this.leggiDescrizioneDaTab1("A2065",
    				    new Long(riga));
    				if (descrizioneA2065 != null) {
    				  descrizioneA2065 = descrizioneA2065.split(" ")[0];
    				    percentualeDitteDaEscludere = new Double(descrizioneA2065);
    					if (percentualeDitteDaEscludere.doubleValue() >= 50) {
    						percentualeDitteDaEscludere = new Double(0);
    						String message = "Percentuale (A2065) per il calcolo delle ALI troppo grande";
    						throw new GestoreException(message,
    								"inizializzaParametri.A2065TroppoGrande");
    					} else {
    						hMapParametri.put("percentualeDitteDaEscludere",
    								percentualeDitteDaEscludere);
    					}
    				} else {
    					String message = "Non trovato il parametro (A2065) della percentuale per il calcolo delle ALI";
    					throw new GestoreException(message,
    							"inizializzaParametri.A2065");
    				}
				}else{
				  hMapParametri.put("percentualeDitteDaEscludere",
	                    new Double(10));
				}

			}
		}

		if (logger.isDebugEnabled())
			logger.debug("inizializzaMinimoPerCorrettivo(" + ngara
					+ "): fine metodo");
	}

	/**
     *Se si rientra nel caso di DLgs.50/2016 allora vengono inizializzati i parametri
     *per il metodo di calcolo
     *
     * @param hMapParametri
     * @param impl
     *
     * @throws GestoreException
     */
	private void inizializzaMetodoCalcoloSogliaAnomalia(HashMap hMapParametri,
	    DataColumnContainer impl) throws GestoreException{

      boolean valorizzaMediasca= true;
      boolean modalitaDL2016 = false;
      boolean modalitaDL2017 = false;
      boolean modalitaManuale =true;

      if (hMapParametri.get("modalitaDL2016") != null)
        modalitaDL2016 = ((Boolean) hMapParametri.get("modalitaDL2016")).booleanValue();

      if (hMapParametri.get("modalitaDL2017") != null)
        modalitaDL2017 = ((Boolean) hMapParametri.get("modalitaDL2017")).booleanValue();
      String isGaraDLGS2017 = "false";

      if(modalitaDL2016 || modalitaDL2017){
        if (modalitaDL2017)
          isGaraDLGS2017 = "true";
        Long metsoglia = impl.getLong("GARE1.METSOGLIA");
        Double metcoeff = impl.getDouble("GARE1.METCOEFF");

        if (hMapParametri.get("modalitaManuale") != null)
          modalitaManuale = ((Boolean) hMapParametri.get("modalitaManuale")).booleanValue();
        //Si deve eseguire il metodo di calcolo della soglia solo se questo non è valorizzato e modalità automatica
        if(metsoglia==null && !modalitaManuale){
          Object valori[] = this.getMetodoCalcoloSoglia(isGaraDLGS2017);
          metsoglia = (Long)valori[0];
          if(valori[1]!=null)
            metcoeff = (Double)valori[1];
          impl.setValue("GARE1.METSOGLIA", metsoglia);
        }

        if(!(new Long(1)).equals(metsoglia) && !(new Long(5)).equals(metsoglia))
          valorizzaMediasca = false;
        hMapParametri.put("metodoCalcoloSogliaAnomalia",metsoglia);
        hMapParametri.put("coefficenteMetodoCalcoloSogliaAnomalia",metcoeff);
      }
      hMapParametri.put("valorizzaMediasca",new Boolean(valorizzaMediasca));
	}

	/**
     *Calcolo dei parametri metosoglia e metcoedd adoperati nel calcolo soglia anomalia nel caso di nuova modalità
     *
     * @return Object[], posizione 0 metsoglia, posizione 1 metcoeff
     *
     * @throws GestoreException
     */
	public Object[] getMetodoCalcoloSoglia(String isModalitaDL2017) throws GestoreException{
	  Long metsoglia=null;
	  Double metcoeff=null;
      String codiceTabellato;

      Random r =  new Random();
      int rand = r.nextInt(5) + 1;
      metsoglia = new Long(rand);
      if(rand==5){
        r =  new Random();
        if ("true".equals(isModalitaDL2017)){
          rand = r.nextInt(4) + 1;
          codiceTabellato="A1140";
        }else{
          rand = r.nextInt(5) + 1;
          codiceTabellato="A1127";
        }
        try {
          String valoreString = (String)sqlManager.getObject("select tab1desc from tab1 where tab1cod=? and tab1tip=? ", new Object[]{codiceTabellato,new Long(rand)});
          if(valoreString!=null)
            metcoeff = UtilityNumeri.convertiDouble(valoreString, UtilityNumeri.FORMATO_DOUBLE_CON_VIRGOLA_DECIMALE);
          else
            throw new GestoreException("Non trovato il parametro ("+codiceTabellato+") per il coefficente da usare nel METODO E del calcolo soglia anomalia", "inizializzaParametri.A1127", new Object[] { codiceTabellato }, new Exception());

        } catch (SQLException e) {
          throw new GestoreException("Errore nella lettura del tabellato "+codiceTabellato, "leggiDescrizioneDaTab1",new Object[] { codiceTabellato, new Long(rand) }, e);
        }

      }
      return new Object[]{metsoglia,metcoeff};
	}


	/**
	 * Ritorna il valore (0 o 1) che definisce la modalità di calcolo
	 * dell'aggiudicazione per gare sotto soglia ed inferiori a 10 ditte (III
	 * decreto corrett. 163/2006)
	 *
	 * @return
	 * <ul>
	 * <li>0 - Applica miglior ribasso
	 * <li>1 - Applica calcolo soglia di anomalia
	 * </ul>
	 * @throws GestoreException
	 */
	private long getModalitaCalcoloGareSottoSoglia() throws GestoreException {

		long result = 0;

		String message = "Non trovato il parametro (A1048) della modalità di calcolo per le gare sotto soglia ed inferiori a 10 ditte (III decreto corrett.163/2006)";

		try {
			String selectA1048 = "select tab1desc from tab1 where tab1cod = ? and tab1tip = ?";
			String descrizioneA1048 = (String) sqlManager.getObject(
					selectA1048, new Object[] { "A1048", new Long(1) });

			if (descrizioneA1048 != null && descrizioneA1048.length() > 0) {
				descrizioneA1048 = descrizioneA1048.substring(0, 1);
				if (descrizioneA1048 == null) {
					result = 0;
				} else {
					if (descrizioneA1048.equals("1")) {
						result = 1;
					} else {
						result = 0;
					}
				}
			}
		} catch (Exception e) {
			throw new GestoreException(message, "inizializzaParametri.A1048");
		}

		return result;
	}

	/**
	 * Ritorna l'importo limite della gara in funzione del tipo di gara (lavori,
	 * forniture e servizi) a partire dalla descrizione del tabellato A1019
	 * Col parametro modalita = 1 si introduce la nuova modalita per DLgs.50/2016
	 * che adopera il tabellato A1125
	 *
	 * @param tipgen
	 * @param dpubavg
	 * @param dinvit
	 * @param iterga
	 * @param modalita
	 * @return Ritorna l'importo limite della gara in funzione del tipo di gara
	 * @throws GestoreException
	 */
	public Object[] getImportoSogliaPerGara(long tipgen, Date dpubavg, Date dinvit, int iterga, int modalita, String codgar) throws GestoreException {
		Object[] result = new Object[2];
		//Di default controllo con estremo incluso
		result[1] = new Boolean(false);
		String tabellato = "A1019";
		if(modalita==1){
		  try {
            String settore = (String) sqlManager.getObject("select SETTORE from TORN where CODGAR = ?",new Object[] {codgar});
            if("S".equals(settore)){
              tabellato = "A1154";
            }else{
              tabellato = "A1125";
            }
          } catch (SQLException e) {
              throw new GestoreException("Errore nella lettura del campo TORN.SETTORE", "inizializzaParametri." + tabellato);
          }
		}
		String message = "Non trovato il parametro (" + tabellato + ") dell'importo limite per le gare";
		Date dataControllo = null;

		if (tipgen == 3)
			tipgen = 2;

		try {
		    if(modalita==0){
  		    //Recupera le date di inizio e fine validità delle soglie europee al posto delle soglie stabilite dalla normativa
  		    //Se la pubblicazione bando è compresa tra 14/05/2011 e 31/12/2013 oppure è nulla, si applicano le soglie europee
  		    List listTab = this.sqlManager.getListVector("select TAB1DESC from TAB1 WHERE TAB1COD = 'A1019' and TAB1TIP in (5,6,7)",
  		                        new Object[] {});
              if (listTab != null && listTab.size() > 0) {
                Date dataSogliaInizio = null;
                Date dataSogliaFine = null;
                for (int i = 0; i < 2; i++) {
                  String descTab = SqlManager.getValueFromVectorParam(listTab.get(i), 0).toString();
                  descTab = descTab.substring(0, descTab.indexOf(' '));
                  if (descTab.length() > 0) {
                    if (i == 0)
                      dataSogliaInizio = UtilityDate.convertiData(descTab, UtilityDate.FORMATO_GG_MM_AAAA);
                    else
                      dataSogliaFine = UtilityDate.convertiData(descTab, UtilityDate.FORMATO_GG_MM_AAAA);
                  }
                }
                // Fa il controllo sulla data invito per le procedure negoziate senza bando, altrimenti
                // sulla data pubblicazione bando
                if (iterga == 3 || iterga == 5 || iterga == 6)
                  dataControllo = dinvit;
                else
                  dataControllo = dpubavg;
                if ((dataSogliaInizio != null && dataSogliaFine != null) &&
                      (dataControllo == null || (dataSogliaInizio.before(dataControllo) && dataSogliaFine.after(dataControllo)))){
                  //Considera le soglie europee
                  if (tipgen == 2)
                    tipgen = 4;
                  else
                    tipgen = 3;
                  //Legge il parametro che stabilisce se includere o meno l'estremo nel confronto
                  String descTab = SqlManager.getValueFromVectorParam(listTab.get(2), 0).toString();
                  descTab = descTab.substring(0, descTab.indexOf(' '));
                  if (descTab.length() > 0)
                    if ("0".equals(descTab))
                      //Estremo escluso - Default per le soglie europee
                      result[1] = new Boolean(true);
                }
              }
		    }
			String descrTab1 = (String) sqlManager.getObject(
					"select TAB1DESC from TAB1 where TAB1COD = ? and TAB1TIP = ?",
							new Object[] { tabellato, new Long(tipgen) });
			if (descrTab1 != null && descrTab1.length() > 0) {
				String tmp = descrTab1.substring(0, descrTab1.indexOf(' '));
				if (tmp.length() > 0)
					result[0] = new Double(UtilityNumeri.convertiDouble(
						UtilityStringhe.replace(tmp, ",", "."),
						UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE).doubleValue());
				else {
					throw new GestoreException(message,	"inizializzaParametri." + tabellato);
				}
			} else {
				throw new GestoreException(message,	"inizializzaParametri." + tabellato);
			}
		} catch (SQLException e) {
			throw new GestoreException(message, "inizializzaParametri." + tabellato);
		}
		return result;
	}

	/**
	 * Legge la descrizione (tab1desc) dal tabellato TAB1
	 *
	 * @param tab1cod
	 * @param tab1tip
	 * @return
	 * @throws GestoreException
	 */
	private String leggiDescrizioneDaTab1(String tab1cod, Long tab1tip)
			throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("leggiDescrizioneDaTab1(" + tab1cod + "," + tab1tip
					+ "): inizio metodo");

		try {
			List ret = this.sqlManager.getListVector(
					"select tab1desc from tab1 where tab1cod = ? and tab1tip = ?",
							new Object[] { tab1cod, tab1tip });
			if (ret != null && ret.size() > 0) {
				return SqlManager.getValueFromVectorParam(ret.get(0), 0).toString();
			}
		} catch (SQLException e) {
			throw new GestoreException("Errore nella lettura del tabellato "
					+ tab1cod + "," + tab1tip, "leggiDescrizioneDaTab1",
					new Object[] { tab1cod, tab1tip }, e);
		}

		if (logger.isDebugEnabled())
			logger.debug("leggiDescrizioneDaTab1(" + tab1cod + "," + tab1tip
					+ "): fine metodo");

		return null;
	}

	/**
	 * Funzione di calcolo della soglia di anomalia
	 *
	 * @param ngara
	 * @param hMapTORN
	 * @param hMapGARE
	 * @param hMapParametri
	 * @throws GestoreException
	 */
	private void calcoloSogliaAnomalia(String ngara, String codgar, HashMap hMapTORN,
			HashMap hMapGARE, HashMap hMapParametri) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("calcoloSogliaAnomalia(" + ngara + "): inizio metodo");

		long modlicg = 0;
		if (hMapGARE.get("modlicg") != null)
			modlicg = ((Long) hMapGARE.get("modlicg")).longValue();

		long numeroDitteAmmesse = 0;
		if (hMapParametri.get("numeroDitteAmmesse") != null)
			numeroDitteAmmesse = ((Long) hMapParametri.get("numeroDitteAmmesse")).longValue();

		long minimoPerCorrettivo = 0;
		if (hMapParametri.get("minimoPerCorrettivo") != null)
			minimoPerCorrettivo = ((Long) hMapParametri.get("minimoPerCorrettivo")).longValue();

		boolean modalitaDL2016 = false;
		if (hMapParametri.get("modalitaDL2016") != null)
		  modalitaDL2016 = ((Boolean) hMapParametri.get("modalitaDL2016")).booleanValue();

		boolean modalitaDL2017 = false;
        if (hMapParametri.get("modalitaDL2017") != null)
          modalitaDL2017 = ((Boolean) hMapParametri.get("modalitaDL2017")).booleanValue();

        boolean modalitaDL2019 = false;
        if (hMapParametri.get("modalitaDL2019") != null)
          modalitaDL2019 = ((Boolean) hMapParametri.get("modalitaDL2019")).booleanValue();

        boolean modalitaDLCalcoloGraduatoria=false;
        if (hMapParametri.get("modalitaDLCalcoloGraduatoria") != null)
          modalitaDLCalcoloGraduatoria = ((Boolean) hMapParametri.get("modalitaDLCalcoloGraduatoria")).booleanValue();

        String legRegSic = "";
        if (hMapGARE.get("legRegSic") != null)
          legRegSic = ( (String) hMapGARE.get("legRegSic"));

		Double ribauoImpEscSupLimite = null;
        Double ribauoImpEscInfLimite = null;

		// 15/06/2009
		// Aggiornamento del valore di ribauo con la somma dei punteggi tecnico
		// ed economico
		if (modlicg == 6) {
			String dittao;
			Double puntec = null;
			Double puneco = null;
			Double sommaPunteggi = null;
			String abilitataGestioneCriteriPrezzo = "0";
			boolean esistonoCriteriIsnprz = false;
            if (hMapParametri.get("abilitataGestioneCriteriPrezzo") != null){
              abilitataGestioneCriteriPrezzo = ((String) hMapParametri.get("abilitataGestioneCriteriPrezzo"));
              if (hMapParametri.get("esistonoCriteriIsnprz") != null)
                esistonoCriteriIsnprz = (((Boolean) hMapParametri.get("esistonoCriteriIsnprz"))).booleanValue();
            }

			Double puntprz = null;
			Double puntalt = null;
			Object punteggiObj = null;
			String selectDITG = "select dittao,puntec,puneco from ditg where ngara5 = ?";
			String updateDITG = "update ditg set ribauo = ? where ngara5=? and dittao=?";

			selectDITG=this.cambiaSelectPunteggiConRiparametrati(selectDITG, hMapGARE, true);

			try {
				List datiDITG = this.sqlManager.getListVector(selectDITG,
						new Object[] { ngara });
				if (datiDITG != null && datiDITG.size() > 0) {
				  String selectPunprz = "select sum(punteg) from goev g, dpun d where g.ngara=? and g.ngara=d.ngara and g.necvan=d.necvan and dittao=? and tippar=2 "
                      + "and livpar in (1,3) and (ISNOPRZ = '2' or ISNOPRZ is null or ISNOPRZ ='')";
				  String selectPuntalt = "select sum(punteg) from goev g, dpun d where g.ngara=? and g.ngara=d.ngara and g.necvan=d.necvan and dittao=? "
                      + "and livpar in (1,3) and ((tippar=2 and ISNOPRZ = '1') or tippar=1)";
                  for (int i = 0; i < datiDITG.size(); i++) {
						dittao = SqlManager.getValueFromVectorParam(datiDITG.get(i), 0).toString();
						puntec = SqlManager.getValueFromVectorParam(datiDITG.get(i), 1).doubleValue();
						puneco = SqlManager.getValueFromVectorParam(datiDITG.get(i), 2).doubleValue();

						if (puntec == null)
							puntec = new Double(0);
						if (puneco == null)
							puneco = new Double(0);
						sommaPunteggi = new Double(puntec.doubleValue()	+ puneco.doubleValue());

						if("1".equals(abilitataGestioneCriteriPrezzo)){
						  //Gestione nuovi campi PUNTPRZ e PUNTALT
						  if(esistonoCriteriIsnprz){
    						  punteggiObj = this.sqlManager.getObject(selectPunprz, new Object[]{ngara,dittao});
    						  puntprz = pgManager.getValoreImportoToDouble(punteggiObj);
    						  punteggiObj = this.sqlManager.getObject(selectPuntalt, new Object[]{ngara,dittao});
    						  puntalt = pgManager.getValoreImportoToDouble(punteggiObj);
    					  }else{
                            puntprz = null;
                            puntalt = null;
                          }
						  this.sqlManager.update("update ditg set ribauo = ?, puntprz=?, puntalt=? where ngara5=? and dittao=?", new Object[]{sommaPunteggi, puntprz, puntalt, ngara, dittao});
						}else
						  this.sqlManager.update(updateDITG, new Object[]{sommaPunteggi, ngara, dittao});
					}
				}
			} catch (SQLException e) {
				throw new GestoreException(
						"Errore nell'aggiornamento del ribasso come somma dei punteggi tecnico ed economico",
						"calcoloSogliaAnomalia.aggiornaRibasso", e);
			}
		}

		if ((modlicg == 13 || modlicg == 14) && !modalitaDLCalcoloGraduatoria
			&& (numeroDitteAmmesse >= minimoPerCorrettivo)) {

			// Riconteggio delle ditte ammesse escludendo quelle con stato
			// di aggiudicazione pari ad 1 (fuori limite)
			hMapParametri.put("numeroDitte", this.conteggioDitte(ngara,
					" AMMGAR <> '2' and (MOTIES < 99 or MOTIES is null) and STAGGI <> 1 "));
			long numeroDitte = 0;
			if (hMapParametri.get("numeroDitte") != null)
				numeroDitte = ((Long) hMapParametri.get("numeroDitte")).longValue();

			double percentualeDitteDaEscludere = 0;
			if (hMapParametri.get("percentualeDitteDaEscludere") != null)
				percentualeDitteDaEscludere = ((Double) hMapParametri.get(
						"percentualeDitteDaEscludere")).doubleValue();

			Double ribauoSuperiore = new Double(0);
			Double ribauoInferiore = new Double(0);
			Double rigaLimiteEsclusione = new Double(0);
			numeroDitteAmmesse = numeroDitte;

			if (numeroDitteAmmesse >= minimoPerCorrettivo) {
			    Double ribauo;
	            String dittao;
			  // Rileggo la lista delle ditte ammesse escludendo quelle fuori
              // limite
			  String selectDitg = "select dittao,ribauo from ditg where  ammgar<>'2' and "
                + " ngara5 = ? and staggi <> 1 order by ribauo, staggi";

				try {
					List datiDITG = this.sqlManager.getListVector(selectDitg,
							new Object[] { ngara });
					if (datiDITG != null && datiDITG.size() > 0) {
					  int indiceRigaInferiore = 0;
					  int indiceRigaSuperiore = 0;

					    // Calcolo il numero della riga contenente il ribasso (ribauo) limite
		                // oltre il quale devono essere escluse le ditte
		                rigaLimiteEsclusione = new Double(Math.ceil(numeroDitteAmmesse
		                        * percentualeDitteDaEscludere / 100));

		                // Calcolo del numero di riga corrispondente alle ALI
                      // Indice riga di esclusione ALA superiore
                      indiceRigaSuperiore = rigaLimiteEsclusione.intValue() - 1;
                      ribauoSuperiore = SqlManager.getValueFromVectorParam(
                              datiDITG.get(indiceRigaSuperiore), 1).doubleValue();
                      if (ribauoSuperiore == null)
                          ribauoSuperiore = new Double(0);

                      // Indice riga di esclusione ALA inferiore
                      indiceRigaInferiore = (new Long(numeroDitteAmmesse)).intValue()
                              - rigaLimiteEsclusione.intValue();
                      ribauoInferiore = SqlManager.getValueFromVectorParam(
                              datiDITG.get(indiceRigaInferiore), 1).doubleValue();
                      if (ribauoInferiore == null)
                          ribauoInferiore = new Double(0);

                      String descTab=null;
                      if(modalitaDL2019 && !"1".equals(legRegSic)){
                        descTab = "0";
                      } else{
                        String codTabellato = "A1107";
                        Long tab1tip = new Long(1);
                        if("1".equals(legRegSic)){
                          codTabellato = "A1116";
                          tab1tip = new Long(6);
                        }else if(modalitaDL2016 || modalitaDL2017){
                          codTabellato = "A1134";
                        }
                        descTab = this.leggiDescrizioneDaTab1(codTabellato,tab1tip);
                        if(descTab==null || "".equals(descTab)){
                          String message = "Non trovato il parametro (" + codTabellato + ") per la gestione delle offerte uguali ricadenti nelle ALI";
                          throw new GestoreException(message,"inizializzaParametri." + codTabellato);
                        }else{
                          descTab = descTab.substring(0, 1);
                        }
                      }

  					  if(modalitaDL2016 || modalitaDL2017 || modalitaDL2019 || "1".equals(legRegSic)){
  					    if(!"0".equals(descTab) && !"1".equals(descTab) && !"2".equals(descTab))
  					      descTab= "0";
  					    hMapParametri.put("tabellatoA1134", descTab);
  					  }else{
    					  if(!"0".equals(descTab))
                          descTab= "1";
  					  }

					  Double ribauoDittaEsclusaSuperiore = null;
					  Double ribauoDittaEsclusaInferiore = null;

						for (int iDitte = 0; iDitte < datiDITG.size(); iDitte++) {
							dittao = SqlManager.getValueFromVectorParam(
									datiDITG.get(iDitte), 0).toString();
							ribauo = SqlManager.getValueFromVectorParam(
									datiDITG.get(iDitte), 1).doubleValue();
							if (ribauo == null)
								ribauo = new Double(0);

							// Gestione ALA superiore
							// if (iDitte > indiceRigaSuperiore &&
							// ribauo.doubleValue() ==
							// ribauoSuperiore.doubleValue()) {
							// ribauoSuperioreLimite = ribauo;
							// }
							if("0".equals(descTab) || "1".equals(descTab)){
    							//La gestione delle ALI per DLgs.50/2016 coincide con la vecchia modalità quando il tabellato assume valori '0' e '1'
							  boolean CondizionePerImpostareAli = false;

    							if (iDitte>indiceRigaSuperiore && ribauo.doubleValue() == ribauoSuperiore.doubleValue()) {
    							  ribauoImpEscSupLimite=ribauo;
    							}


    							  if("0".equals(descTab)){
        							  if(iDitte <= indiceRigaSuperiore && ribauoDittaEsclusaSuperiore!=null && ribauo.doubleValue() == ribauoDittaEsclusaSuperiore.doubleValue()){
        							    indiceRigaSuperiore++;
        							  }else{
        							    ribauoDittaEsclusaSuperiore = ribauo;
        							  }
    							  }

    							  CondizionePerImpostareAli = iDitte <= indiceRigaSuperiore || ribauo.doubleValue() == ribauoSuperiore.doubleValue();



    							if (CondizionePerImpostareAli) {
    								ribauoSuperiore = ribauo;
    								// Setta lo stato di aggiudicazione a 2 - ala superiore
    								this.aggiornaStatoAggiudicazione(ngara, dittao,	new Long(2));

    							}

    							// Gestione ALA inferiore per Regione Sicilia
    							if (iDitte < indiceRigaInferiore && ribauo.doubleValue() == ribauoInferiore.doubleValue()) {
    							  ribauoImpEscInfLimite=ribauo;
                                }


                                if("0".equals(descTab))
                                  CondizionePerImpostareAli = false;
                                else
                                  CondizionePerImpostareAli = iDitte >= indiceRigaInferiore || ribauo.doubleValue() == ribauoInferiore.doubleValue();


    							if (CondizionePerImpostareAli) {
    								ribauoInferiore = ribauo;
    								// Setta lo stato di aggiudicazione a 7 - ala inferiore
    								this.aggiornaStatoAggiudicazione(ngara, dittao,	new Long(7));

    							}
							}else{
							  //Nuova modalità conteggio ALI per DLgs.50/2016

							  //Ala inferiore
							  if(iDitte <= indiceRigaSuperiore){
							    this.aggiornaStatoAggiudicazione(ngara, dittao,  new Long(2));

							  }

							  //Ala superiorreù
							  if(iDitte >= indiceRigaInferiore){
                                this.aggiornaStatoAggiudicazione(ngara, dittao,  new Long(7));

							  }
							}
						}


						if("0".equals(descTab)){
    						//Nuova gestione dell'ALA inferiore (No regione sicilia)
						      for (int iDitte = datiDITG.size() - 1; iDitte >=0; iDitte--) {
    	                          dittao = SqlManager.getValueFromVectorParam(
    	                                  datiDITG.get(iDitte), 0).toString();
    	                          ribauo = SqlManager.getValueFromVectorParam(
    	                                  datiDITG.get(iDitte), 1).doubleValue();
    	                          if (ribauo == null)
    	                              ribauo = new Double(0);
    	                          boolean CondizionePerImpostareAli = false;

    	                          // Gestione ALA inferiore
	                          if(iDitte >= indiceRigaInferiore && ribauoDittaEsclusaInferiore!=null && ribauo.doubleValue() == ribauoDittaEsclusaInferiore.doubleValue()){
	                            indiceRigaInferiore--;
	                          }else{
	                            ribauoDittaEsclusaInferiore = ribauo;
	                          }
	                          CondizionePerImpostareAli = iDitte >= indiceRigaInferiore || ribauo.doubleValue() == ribauoInferiore.doubleValue();


	                          if (CondizionePerImpostareAli) {
	                              ribauoInferiore = ribauo;
	                              // Setta lo stato di aggiudicazione a 7 - ala inferiore
	                              this.aggiornaStatoAggiudicazione(ngara, dittao, new Long(7));

	                          }
	                      }

						}

					}




				} catch (SQLException e) {
					throw new GestoreException(
							"Errore nella gestione ed esclusione delle ditte appartenenti alle ALI inferiore e superiore",
							"calcoloSogliaAnomalia.FuoriALI", e);
				}
			}
		}

		// Riconteggio delle ditte ammesse escludendo quelle con stato
		// di aggiudicazione pari ad 1 (fuori limite), 2 (ala superiore) e 7
		// (ala inferiore)
		Long numeroDitte = this.conteggioDitte(ngara,
				" AMMGAR <> '2' and (MOTIES < 99 or MOTIES is null) and (STAGGI not in (1,2,7))");
		hMapParametri.put("numeroDitte", numeroDitte);

		hMapParametri.put("ribauoImpEscSupLimite", ribauoImpEscSupLimite);
		hMapParametri.put("ribauoImpEscInfLimite", ribauoImpEscInfLimite);
		//Numero ali inferiori
		Long numAli = this.conteggioDitte(ngara,
            " AMMGAR <> '2' and (MOTIES < 99 or MOTIES is null) and STAGGI =7");
		hMapParametri.put("numAliInf", numAli);
		//Numero ali superiori
        numAli = this.conteggioDitte(ngara,
            " AMMGAR <> '2' and (MOTIES < 99 or MOTIES is null) and STAGGI =2");
		hMapParametri.put("numAliSup", numAli);

		if (logger.isDebugEnabled())
			logger.debug("calcoloSogliaAnomalia(" + ngara + "): fine metodo");
	}

	/**
	 * Calcolo della media dei ribassi
	 *
	 * @param ngara
	 * @param hMapTORN
	 * @param hMapGARE
	 * @param hMapParametri
	 * @param controlloAli
	 *        true nel calcolo nel calcolo vengono escluse le ali
	 *        false si considerano tutte le ditte
	 * @throws GestoreException
	 */
	private void calcolaMedia(String ngara, HashMap hMapTORN, HashMap hMapGARE,
			HashMap hMapParametri, boolean controlloAli) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("calcolaMedia(" + ngara + "): inizio metodo");

		double mediaRibasso = 0;

		long numeroDitteAmmesse = 0;
		if (hMapParametri.get("numeroDitteAmmesse") != null)
			numeroDitteAmmesse = ((Long) hMapParametri
					.get("numeroDitteAmmesse")).longValue();

		long numeroDitte = 0;
		if (hMapParametri.get("numeroDitte") != null)
			numeroDitte = ((Long) hMapParametri.get("numeroDitte")).longValue();

		long modlicg = 0;
		if (hMapGARE.get("modlicg") != null)
			modlicg = ((Long) hMapGARE.get("modlicg")).longValue();

		long minimoPerCorrettivo = 0;
		if (hMapParametri.get("minimoPerCorrettivo") != null)
			minimoPerCorrettivo = ((Long) hMapParametri
					.get("minimoPerCorrettivo")).longValue();

        boolean modalitaDLCalcoloGraduatoria=false;
        if (hMapParametri.get("modalitaDLCalcoloGraduatoria") != null)
          modalitaDLCalcoloGraduatoria = ((Boolean) hMapParametri.get("modalitaDLCalcoloGraduatoria")).booleanValue();

        int precut = 0;
        if (hMapGARE.get("precut") != null)
            precut = ((Long) hMapGARE.get("precut")).intValue();



		if (numeroDitte > 0) {
			// Il calcolo viene eseguito se dopo aver escluse
			// le ditte anomale ci sono ancora ditte sulle quali
			// eseguire il calcolo
			if ((modlicg != 6 && numeroDitteAmmesse >= minimoPerCorrettivo)
					|| (modlicg == 1 || modlicg == 5 || modlicg == 17 || modalitaDLCalcoloGraduatoria)) {

				String selectTotaleRibauo = null;
				if(controlloAli){
				  selectTotaleRibauo = "select sum(ribauo) from ditg where ammgar<>'2' and "
                    + " ngara5 = ? and staggi not in (1,2,7)";
				}else{
				  selectTotaleRibauo = "select sum(ribauo) from ditg where ammgar<>'2' and "
	                    + " ngara5 = ?";

				}

				try {

				  Object sum = this.sqlManager.getObject(selectTotaleRibauo, new Object[] { ngara });
				  double sommaRibasso = 0;
				  if(sum instanceof Double)
		            sommaRibasso = new Double((Double)sum).doubleValue();
		          else
		            sommaRibasso = new Double(((Long) sum)).doubleValue();

			      mediaRibasso = sommaRibasso / numeroDitte;

			      sommaRibasso= this.valoreArrotondatoTroncato(sommaRibasso, precut, hMapParametri);
                  hMapParametri.put("sommaRib", new Double(sommaRibasso));

				} catch (SQLException e) {
					throw new GestoreException(
							"Errore nella calcolo della media del ribasso",
							"calcolaMedia", e);
				}
			}
		}


		mediaRibasso = this.valoreArrotondatoTroncato(mediaRibasso, precut, hMapParametri);
        hMapParametri.put("mediaRibasso", new Double(mediaRibasso));

		if (logger.isDebugEnabled())
			logger.debug("calcolaMedia(" + ngara + "): fine metodo");

	}

	/**
	 * Calcolo della media corretta in funzione del correttivo calcolato
	 *
	 * @param ngara
	 * @param hMapTORN
	 * @param hMapGARE
	 * @param hMapParametri
	 * @throws GestoreException
	 */
	private void calcoloMediaCorretta(String ngara, HashMap hMapTORN,
			HashMap hMapGARE, HashMap hMapParametri) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("calcoloMediaCorretta(" + ngara + "): inizio metodo");

		long numeroDitteAmmesse = 0;
		if (hMapParametri.get("numeroDitteAmmesse") != null)
			numeroDitteAmmesse = ((Long) hMapParametri.get(
					"numeroDitteAmmesse")).longValue();

		long numeroDitte = 0;
		if (hMapParametri.get("numeroDitte") != null)
			numeroDitte = ((Long) hMapParametri.get("numeroDitte")).longValue();

		int modlicg = 0;
		if (hMapGARE.get("modlicg") != null)
			modlicg = ((Long) hMapGARE.get("modlicg")).intValue();

		long minimoPerCorrettivo = 0;
		if (hMapParametri.get("minimoPerCorrettivo") != null)
			minimoPerCorrettivo = ((Long) hMapParametri.get(
					"minimoPerCorrettivo")).longValue();

		double corgar = 0;
		if (hMapParametri.get("corgar") != null)
			corgar = ((Double) hMapParametri.get("corgar")).doubleValue();

		double mediaRibasso = 0;
		if (hMapParametri.get("mediaRibasso") != null)
			mediaRibasso = ((Double) hMapParametri.get("mediaRibasso")).doubleValue();

		int precut = 0;
		if (hMapGARE.get("precut") != null)
			precut = ((Long) hMapGARE.get("precut")).intValue();

		boolean modalitaDL2016 = false;
		boolean modalitaDL2017 = false;
		boolean modalitaDLCalcoloGraduatoria=false;
		if (hMapParametri.get("modalitaDLCalcoloGraduatoria") != null)
		  modalitaDLCalcoloGraduatoria = ((Boolean) hMapParametri.get("modalitaDLCalcoloGraduatoria")).booleanValue();

		long metsoglia = 0;
		double metcoeff =0;

		if((modlicg==13 || modlicg==14) && !modalitaDLCalcoloGraduatoria){

		  if (hMapParametri.get("modalitaDL2016") != null)
		    modalitaDL2016 = ((Boolean) hMapParametri.get("modalitaDL2016")).booleanValue();
		  if (hMapParametri.get("modalitaDL2017") != null)
            modalitaDL2017 = ((Boolean) hMapParametri.get("modalitaDL2017")).booleanValue();

         if(modalitaDL2016 || modalitaDL2017){
           if (hMapParametri.get("metodoCalcoloSogliaAnomalia") != null)
             metsoglia = ((Long) hMapParametri.get("metodoCalcoloSogliaAnomalia")).longValue();
           if(metsoglia==5){
             if (hMapParametri.get("coefficenteMetodoCalcoloSogliaAnomalia") != null)
               metcoeff = ((Double) hMapParametri.get("coefficenteMetodoCalcoloSogliaAnomalia")).doubleValue();
           }
         }
       }

		long numeroDitteMediate = 0;
		double totaleRibasso = 0;
		double ribauo = 0;
		double mediaScarti = 0;
		double limiteAnomalia = 0;

		if (numeroDitte > 0) {
			if (modlicg != 6 && modlicg != 17 && numeroDitteAmmesse >= minimoPerCorrettivo) {

				String selectDITG =
					"select ribauo from ditg where ammgar<>'2' and "
					+ " ngara5 = ? and staggi not in (1,2,7) order by ribauo, staggi";

				try {
					List datiDITG = this.sqlManager.getListVector(selectDITG,
							new Object[] { ngara });
					if (datiDITG != null && datiDITG.size() > 0) {
						for (int i = 0; i < datiDITG.size(); i++) {
							ribauo = 0;
							if (SqlManager.getValueFromVectorParam(
									datiDITG.get(i), 0).doubleValue() != null) {
								ribauo = SqlManager.getValueFromVectorParam(
	                                        datiDITG.get(i), 0).doubleValue().doubleValue();
							}

							switch (modlicg) {
							case 1:
							case 5:
								if (ribauo < 0) {
									numeroDitteMediate += 1;
									totaleRibasso += ribauo * corgar / 100;
									totaleRibasso = UtilityMath.round(totaleRibasso, 9);
								}
								break;

							case 13:
							case 14:
								if(!modalitaDLCalcoloGraduatoria){
    							    if (ribauo < mediaRibasso) {
    									numeroDitteMediate += 1;
    									totaleRibasso += ribauo - mediaRibasso;
                                        totaleRibasso = UtilityMath.round(totaleRibasso, 9);
    								}
								}else{
								  if (ribauo < 0) {
                                    numeroDitteMediate += 1;
                                    totaleRibasso += ribauo * corgar / 100;
                                    totaleRibasso = UtilityMath.round(totaleRibasso, 9);
                                  }
								}
							default:
								break;
							}
						}

  						if (numeroDitteMediate > 0) {
  						    boolean mediaCorretta = true;
  						    if (mediaCorretta){
  						      mediaScarti = this.valoreArrotondatoTroncato(UtilityMath.round(totaleRibasso / numeroDitteMediate,9), precut, hMapParametri);
  						      if(metsoglia==5)
  						        limiteAnomalia = mediaRibasso + this.valoreArrotondatoTroncato(UtilityMath.round(mediaScarti * metcoeff,9), precut, hMapParametri);
  						      else
  						        limiteAnomalia = mediaRibasso + mediaScarti;
  						    }

  						} else {
  							limiteAnomalia = mediaRibasso;
  						}

					}
				} catch (SQLException e) {
					throw new GestoreException(
							"Errore nella calcolo della media corretta",
							"calcoloMediaCorretta", e);
				}
			}
			limiteAnomalia = this.valoreArrotondatoTroncato(limiteAnomalia, precut, hMapParametri);

		}

		hMapParametri.put("limiteAnomalia", new Double(limiteAnomalia));
		hMapParametri.put("mediaScarti", new Double(mediaScarti));

		if (logger.isDebugEnabled())
			logger.debug("calcoloMediaCorretta(" + ngara + "): fine metodo");
	}

    /**
     * Calcolo della media corretta per Regione Sicilia (...rif. normativi)
     *
     * @param ngara
     * @param hMapTORN
     * @param hMapGARE
     * @param hMapParametri
     * @throws GestoreException
     */
    private void calcoloMediaCorrettaSicilia(String ngara, HashMap hMapTORN,
            HashMap hMapGARE, HashMap hMapParametri) throws GestoreException {

        if (logger.isDebugEnabled())
            logger.debug("calcoloMediaCorretta(" + ngara + "): inizio metodo");

        long numeroDitteAmmesse = 0;
        if (hMapParametri.get("numeroDitteAmmesse") != null)
            numeroDitteAmmesse = ((Long) hMapParametri.get(
                    "numeroDitteAmmesse")).longValue();

        long numeroDitte = 0;
        if (hMapParametri.get("numeroDitte") != null)
            numeroDitte = ((Long) hMapParametri.get("numeroDitte")).longValue();

        int modlicg = 0;
        if (hMapGARE.get("modlicg") != null)
            modlicg = ((Long) hMapGARE.get("modlicg")).intValue();

        double mediaRibasso = 0;
        if (hMapParametri.get("mediaRibasso") != null)
            mediaRibasso = ((Double) hMapParametri.get("mediaRibasso")).doubleValue();

        long minimoPerCorrettivo = 0;
        if (hMapParametri.get("minimoPerCorrettivo") != null)
            minimoPerCorrettivo = ((Long) hMapParametri.get(
                    "minimoPerCorrettivo")).longValue();

        int precut = 0;
        if (hMapGARE.get("precut") != null)
            precut = ((Long) hMapGARE.get("precut")).intValue();

        long numeroDitteMediate = 0;
        double totaleRibasso = 0;
        double somma = 0;
        double ribauo = 0;
        double limiteAnomalia = 0;
        double mediaScarti =0;

        if (numeroDitte > 0) {
            if (modlicg != 6 && numeroDitteAmmesse >= minimoPerCorrettivo) {

                String selectDITG =
                    "select sum(ribauo) from ditg where ammgar<>'2' and "
                    + " ngara5 = ? and staggi <>1 order by ribauo, staggi";

                try {
                    /*
                    List datiDITG = this.sqlManager.getListVector(selectDITG,
                            new Object[] { ngara });
                    if (datiDITG != null && datiDITG.size() > 0) {
                        for (int i = 0; i < datiDITG.size(); i++) {
                            ribauo = 0;
                            if (SqlManager.getValueFromVectorParam(
                                    datiDITG.get(i), 0).doubleValue() != null) {
                                ribauo = SqlManager.getValueFromVectorParam(
                                        datiDITG.get(i), 0).doubleValue().doubleValue();
                            }
                                    totaleRibasso += ribauo ;
                        }
                    }
                    */
                  Object sum = this.sqlManager.getObject(selectDITG, new Object[] { ngara });
                  if(sum instanceof Double)
                    totaleRibasso = new Double((Double)sum).doubleValue();
                  else
                    totaleRibasso = new Double(((Long) sum)).doubleValue();

                  if(totaleRibasso < 0 ){
                    somma = - totaleRibasso;
                  }else{
                    somma = totaleRibasso;
                  }
                  int primaCifra = (int)(somma*10 - (Math.floor(somma)*10));
                  int verificaParita = primaCifra & 1;
                  double cento = 100.00;
                  mediaScarti = mediaRibasso * primaCifra / cento;
                  mediaScarti = this.valoreArrotondatoTroncato(mediaScarti, precut, hMapParametri);
                  if(verificaParita == 0) {
                    //pari
                    limiteAnomalia = mediaRibasso + mediaScarti;
                  }else{
                    //dispari
                    limiteAnomalia = mediaRibasso - mediaScarti;
                  }
                } catch (SQLException e) {
                    throw new GestoreException(
                            "Errore nella calcolo della media corretta",
                            "calcoloMediaCorretta", e);
                }
            }
            limiteAnomalia = this.valoreArrotondatoTroncato(limiteAnomalia, precut, hMapParametri);

        }
        hMapParametri.put("limiteAnomalia", new Double(limiteAnomalia));
        hMapParametri.put("mediaScarti", new Double(mediaScarti));

        if (logger.isDebugEnabled())
            logger.debug("calcoloMediaCorretta(" + ngara + "): fine metodo");
    }

    /**
	 * Definisce lo stato di aggiudicazione (staggi.ditg) per tutte le ditte
	 * ammesse alla gara in funzione dei vari parametri calcolati in precedenza
	 *
	 * @param ngara
	 * @param hMapTORN
	 * @param hMapGARE
	 * @param hMapParametri
	 * @param rigaDaSettare:
	 *            <ul>
	 *            <li>se 0 controlla tutte le ditte
	 *            <li>se > 0 setta lo stato di aggiudicazione solo per la riga
	 *            passata
	 *            </ul>
	 * @param soloAggiudicatarie:
	 *            <ul>
	 *            <li>se TRUE considera solo le ditte aggiudicatarie (prima e/o
	 *            seconda)
	 *            <li> se FALSE considera tutte le ditte
	 *            </ul>
	 * @param ancheCongruo:
	 *            <ul>
	 *            <li>se TRUE imposta nella DITG anche i campi di congruità
	 *            <li>se FALSE non imposta i campi di congruità
	 *            </ul>
	 * @throws GestoreException
	 */
	public void settaStaggi(String ngara, HashMap hMapTORN, HashMap hMapGARE,
			HashMap hMapParametri, String selectDITG, int rigaDaSettare,
			boolean soloAggiudicatarie, boolean ancheCongruo)
			throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("settaStaggi(" + ngara + "): inizio metodo");

		long numeroDitteAmmesse = 0;
		if (hMapParametri.get("numeroDitteAmmesse") != null)
			numeroDitteAmmesse = ((Long) hMapParametri.get("numeroDitteAmmesse")).longValue();

		long numeroDitte = 0;
		if (hMapParametri.get("numeroDitte") != null)
			numeroDitte = ((Long) hMapParametri.get("numeroDitte")).longValue();

		int modlicg = 0;
		if (hMapGARE.get("modlicg") != null)
			modlicg = ((Long) hMapGARE.get("modlicg")).intValue();

		long minimoPerCorrettivo = 0;
		if (hMapParametri.get("minimoPerCorrettivo") != null)
			minimoPerCorrettivo = ((Long) hMapParametri.get("minimoPerCorrettivo")).longValue();

		double limiteAnomalia = 0;
		if (hMapParametri.get("limiteAnomalia") != null)
			limiteAnomalia = ((Double) hMapParametri.get("limiteAnomalia")).doubleValue();

		long modastg = 0;
		if (hMapGARE.get("modastg") != null)
			modastg = ((Long) hMapGARE.get("modastg")).longValue();

		String calcsoang = null;
		if(hMapGARE.get("calcsoang") != null)
			calcsoang = (String) hMapGARE.get("calcsoang");

		double sommaPunteggioMassimoTecnico = 0;
		if (hMapParametri.get("sommaPunteggioMassimoTecnico") != null)
			sommaPunteggioMassimoTecnico = ((Double)
					hMapParametri.get("sommaPunteggioMassimoTecnico")).doubleValue();

		double sommaPunteggioMassimoEconomico = 0;
		if (hMapParametri.get("sommaPunteggioMassimoEconomico") != null)
			sommaPunteggioMassimoEconomico = ((Double)
					hMapParametri.get("sommaPunteggioMassimoEconomico")).doubleValue();

		boolean modalitaDLCalcoloGraduatoria=false;
		if (hMapParametri.get("modalitaDLCalcoloGraduatoria") != null)
		  modalitaDLCalcoloGraduatoria = ((Boolean)hMapParametri.get("modalitaDLCalcoloGraduatoria")).booleanValue();

		boolean controlloOPEVNumDitteNormativaSuperato=true;
		if (hMapParametri.get("controlloOPEVNumDitteNormativaSuperato") != null)
		  controlloOPEVNumDitteNormativaSuperato = ((Boolean)hMapParametri.get("controlloOPEVNumDitteNormativaSuperato")).booleanValue();

		boolean taglioAliEffettivo = false;
		if (hMapParametri.get("taglioAliEffettivo") != null)
		  taglioAliEffettivo = ((Boolean)hMapParametri.get("taglioAliEffettivo")).booleanValue();

		String legRegSic = null;
		if(hMapGARE.get("legRegSic") != null)
		  legRegSic = (String) hMapGARE.get("legRegSic");

		double ribauo = 0;
		String dittao;
		long staggi = 0;
		boolean ok = false;
		double puntec = 0;
		double puneco = 0;
		long staggiali = 0;
		boolean esclusioneAli =false; //Indica se escludere le ali nell'aggiornamento di STAGGI, adoperata per modlicg=13 e 14

		if (numeroDitte > 0) {
			try {
				List datiDITG = this.sqlManager.getListVector(selectDITG,
						new Object[] { ngara });
				if (datiDITG != null && datiDITG.size() > 0) {
					for (int i = 0; i < datiDITG.size(); i++) {
						dittao = SqlManager.getValueFromVectorParam(
								datiDITG.get(i), 0).stringValue();

						ribauo = 0;
						if (SqlManager.getValueFromVectorParam(datiDITG.get(i),
								1).doubleValue() != null) {
							ribauo = SqlManager.getValueFromVectorParam(
									datiDITG.get(i), 1).doubleValue().doubleValue();
						}
						staggi = 0;
						if (SqlManager.getValueFromVectorParam(datiDITG.get(i),
								2).longValue() != null) {
							staggi = SqlManager.getValueFromVectorParam(
									datiDITG.get(i), 2).longValue().longValue();
						}
						staggiali = 0;
                        if (SqlManager.getValueFromVectorParam(datiDITG.get(i),
                                5).longValue() != null) {
                          staggiali = SqlManager.getValueFromVectorParam(
                                    datiDITG.get(i), 5).longValue().longValue();
                        }
						ok = false;

						/*if (rigaDaSettare > 0) {
							if (rigaDaSettare == i) {
								ok = true;
							}
						} else {*/
						if (soloAggiudicatarie == false) {
							ok = true;
						} else {
							if (staggi == 4 || staggi == 5) {
								ok = true;
							}
						}
						//}

						esclusioneAli =false;

						if (ok == true) {
							if (numeroDitteAmmesse >= minimoPerCorrettivo) {
								boolean condizione = false;
								boolean condizioneGraduatoria=false;
								switch (modlicg) {
								case 1:
								case 5:
									if (ribauo >= limiteAnomalia){
										condizione = true;
										condizioneGraduatoria = true;
									}
									break;

								case 6:
									if("1".equals(calcsoang) && controlloOPEVNumDitteNormativaSuperato){
										puntec = 0;
										if (SqlManager.getValueFromVectorParam(
												datiDITG.get(i), 3).doubleValue() != null) {
											puntec = SqlManager.getValueFromVectorParam(
															datiDITG.get(i), 3).doubleValue().doubleValue();
										}

										puneco = 0;
										if (SqlManager.getValueFromVectorParam(
												datiDITG.get(i), 4).doubleValue() != null) {
											puneco = SqlManager.getValueFromVectorParam(
															datiDITG.get(i), 4).doubleValue().doubleValue();
										}

										if (puntec >= (sommaPunteggioMassimoTecnico * ((double) 4 / (double) 5))
												&& puneco >= (sommaPunteggioMassimoEconomico * ((double) 4 / (double) 5))) {
											condizione = false;
										} else {
											condizione = true;
										}
									} else{
										condizione = true;
										condizioneGraduatoria = true;
									}
									break;

								case 13:
								case 14:
									if(!modalitaDLCalcoloGraduatoria){
								      //Se taglioAliEffettivo= true (A1163) non si devono considerare le ali
									  if(taglioAliEffettivo && (new Long(2).equals(staggiali) || new Long(7).equals(staggiali)))
									    esclusioneAli= true;
									  if (((ribauo > limiteAnomalia && !"1".equals(legRegSic)) || (ribauo >= limiteAnomalia && "1".equals(legRegSic))) && !esclusioneAli)
										condizione = true;
									}else{
									  //Si applica la stessa condizione per modlicg=1 o modlicg=5
									  if (ribauo >= limiteAnomalia){
                                        condizione = true;
                                        condizioneGraduatoria = true;
									  }
									}
								    break;

							    case 17:
                                  condizione = true;
                                  condizioneGraduatoria = true;
                                  break;
								default:
									break;
								}

								if (condizione == true) {
									// Ditta non anomala
									Long stato= new Long(6);
									if(condizioneGraduatoria)
									  stato= new Long(10);
								    this.aggiornaStatoAggiudicazione(ngara,	dittao, stato);
									if ((modastg == 2 || modastg == 1) && ancheCongruo == true)
										this.aggiornaCampiCongruita(ngara, dittao, "1");
								} else {
									// Ditta anomala
								    //non va fatto l'aggiornamento nel caso di esclusione delle ali (condizione da applicare solo per modlicg= 13 e 14)
									if(!esclusioneAli){
    								    this.aggiornaStatoAggiudicazione(ngara,	dittao, new Long(3));
    									if ((modastg == 2 || modastg == 1) && ancheCongruo == true)
    										this.aggiornaCampiCongruita(ngara, dittao, "0");
									}
								}
							} else {
								// Ditta non anomala
								this.aggiornaStatoAggiudicazione(ngara, dittao,	new Long(10));
								if ((modastg == 2 || modastg == 1) && ancheCongruo == true)
									this.aggiornaCampiCongruita(ngara, dittao, "1");
							}
						}
					}
				}
			} catch (SQLException e) {
				throw new GestoreException(
						"Errore nell'aggiornamento dello stato di aggiudicazione",
						"settaStaggi.MolteDitte", e);
			}
		}

		if (logger.isDebugEnabled())
			logger.debug("settaStaggi(" + ngara + "): fine metodo");
	}

	/**
	 * Aggiornamento dei campi di congruità per la ditta presente in DITG
	 *
	 * @param ngara
	 * @param dittao
	 * @param congruo
	 * @throws GestoreException
	 */
	private void aggiornaCampiCongruita(String ngara, String dittao,
			String congruo) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("aggiornaCampiCongruita(" + ngara + "," + dittao + ","
					+ congruo + "): inizio metodo");

		String updateDITG = "update ditg set congruo = ?, congmot = null where ngara5=? and dittao=?";
		try {
			this.sqlManager.update(updateDITG, new Object[] { congruo, ngara,	dittao});
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore durante l'aggiornamento dei valori di congruità per la ditta "
							+ dittao, "aggiornaCampiCongruita",
					new Object[] { dittao }, e);
		}

		if (logger.isDebugEnabled())
			logger.debug("aggiornaCampiCongruita(" + ngara + "," + dittao + ","
					+ congruo + "): fine metodo");

	}

	/**
	 * Aggiorna lo stato aggiudicazione (STAGGI.DITG)
	 *
	 * @param ngara
	 * @param dittao
	 * @param staggi
	 * @throws GestoreException
	 */
	private void aggiornaStatoAggiudicazione(String ngara, String dittao,
			Long staggi) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("aggiornaStatoAggiudicazione(" + ngara + "," + dittao
					+ "," + staggi + "): inizio metodo");

		String updateDITG = "update ditg set staggi=? where ngara5=? and dittao=?";
		if( (new Long(2)).equals(staggi) || (new Long(7)).equals(staggi) )
		  updateDITG = "update ditg set staggi=?, staggiali=? where ngara5=? and dittao=?";
		try {
		  if( (new Long(2)).equals(staggi) || (new Long(7)).equals(staggi) )
		    this.sqlManager.update(updateDITG, new Object[] { staggi, staggi, ngara, dittao});
		  else
		    this.sqlManager.update(updateDITG, new Object[] { staggi, ngara, dittao});
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore durante l'aggiornamento dello stato aggiudicazione per la " +
					"ditta " + dittao, "settaStatoAggiudicazione",
					new Object[] { dittao }, e);
		}

		if (logger.isDebugEnabled())
			logger.debug("aggiornaStatoAggiudicazione(" + ngara + "," + dittao
					+ "," + staggi + "): fine metodo");
	}

	/**
	 * Esclusione eventuale impresa vincitrice (fase A)
	 *
	 * @param codgar
	 * @param ngara
	 * @param hMapTORN
	 * @param hMapGARE
	 * @param hMapParametri
	 * @throws GestoreException
	 */
	private void esclusioneEventualeVincitriceFaseA(String codgar,
			String ngara, HashMap hMapTORN, HashMap hMapGARE,
			HashMap hMapParametri) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("esclusioneEventualeVincitriceFaseA(" + codgar + ","
					+ ngara + "): inizio metodo");

		try {
			List datiDITG = this.sqlManager.getListVector(
					"select DITTAO from DITG where NGARA5 = ? and MOTIES = 98",
					new Object[] { ngara });
			if (datiDITG != null && datiDITG.size() > 0) {
				String dittao = SqlManager.getValueFromVectorParam(
						datiDITG.get(0), 0).stringValue();

				if (codgar.substring(1, 1) == "$")
					hMapTORN.put("tiptor", "2");
				if (dittao != null && (dittao != null && !"".equals(dittao))) {
					this.settaDittaVincitrice(codgar, ngara, dittao, new Long(0), hMapTORN);
				}
			}
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nella esclusione dell'eventuale ditte vincitrice",
					"esclusioneEventualeVincitriceFaseA", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("esclusioneEventualeVincitriceFaseA(" + codgar + ","
					+ ngara + "): fine metodo");
	}

	/**
	 * Esclusione eventuale impresa vincitrice (fase B)
	 *
	 * @param codgar
	 * @param ngara
	 * @param hMapTORN
	 * @param hMapGARE
	 * @param hMapParametri
	 * @throws GestoreException
	 */
	private void esclusioneEventualeVincitriceFaseB(String codgar,
			String ngara, HashMap hMapTORN, HashMap hMapGARE,
			HashMap hMapParametri) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("esclusioneEventualeVincitriceFaseB(" + codgar + ", "
					+ ngara + "): inizio metodo");

		String dittao = null;
		String selectDITG = "Select dittao from ditg where ngara5=? and moties=98";
		try {
			List datiDITG = this.sqlManager.getListVector(selectDITG,
					new Object[] { ngara });
			if (datiDITG != null && datiDITG.size() > 0) {
				dittao = SqlManager.getValueFromVectorParam(datiDITG.get(0), 0)
						.stringValue();
			}
			String primaAggiudicataria = (String)
					hMapParametri.get("primaAggiudicatariaSelezionata");

			if (codgar.substring(1, 1) == "$")
				hMapTORN.put("tiptor", "2");

			if (dittao == null || (dittao != null && "".equals(dittao))
					|| (dittao != null && !dittao.equals(primaAggiudicataria))) {
				this.settaDittaVincitrice(codgar, ngara, primaAggiudicataria,
						new Long(98), hMapTORN);
			}

			// Se era gia' stata settata una ditta vincitrice, diversa da quella
			// attuale,
			// lo staggi di tale ditta va sbiancato
			if (dittao != null && (dittao != null && !"".equals(dittao))
					&& !dittao.equals(primaAggiudicataria)) {
				this.settaDittaVincitrice(codgar, ngara, dittao, new Long(0),
						hMapTORN);
			}
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nella esclusione dell'eventuale ditte vincitrice",
					"esclusioneEventualeVincitriceFaseB", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("esclusioneEventualeVincitriceFaseB(" + codgar + ", "
					+ ngara + "): fine metodo");
	}

	/**
	 * Setta ditta vincitrice
	 *
	 * @param codgar
	 * @param ngara
	 * @param dittao
	 * @param moties
	 * @param hMapTORN
	 * @throws GestoreException
	 */
	private void settaDittaVincitrice(String codgar, String ngara,
			String dittao, Long moties, HashMap hMapTORN)
			throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("settaDittaVincitrice(" + codgar + ", " + ngara + ", "
					+ dittao + ", " + moties + "): inizio metodo");

		String tiptor = hMapTORN.get("tiptor").toString();

		try {
			this.sqlManager.update(
					"update DITG set MOTIES = ? where NGARA5 = ? and DITTAO = ?",
					new Object[] { moties, ngara, dittao });

		} catch (SQLException e) {
			throw new GestoreException(
					"Errore durante l'aggiornamento dei dati della ditta "+ dittao,
					"settaDittaVincitrice",	new Object[] { dittao }, e);
		}

		if ((tiptor == null) || (tiptor != null && !"2".equals(tiptor))) {
			// Se gara divisa a lotti omogenea
			String selectDITG = "select DITG.NGARA5, GARE.FASGAR from DITG, GARE "
					+ "where DITG.NGARA5 = GARE.NGARA "
					+ "and DITG.NGARA5 <> ? "
					+ "and DITG.CODGAR5 = ? "
					+ "and DITG.DITTAO = ? " + "and GARE.DITTA is null";
			try {
				List datiDITG = this.sqlManager.getListVector(selectDITG,
						new Object[] { ngara, codgar, dittao });
				if (datiDITG != null && datiDITG.size() > 0) {
					String annoff = this.leggiNoteDittaVincitrice(ngara, dittao);
					for (int i = 0; i < datiDITG.size(); i++) {
						String ngaraDITG = SqlManager.getValueFromVectorParam(
								datiDITG.get(i), 0).stringValue();

						Long fasGar = SqlManager.getValueFromVectorParam(
						    datiDITG.get(i), 1).longValue();
                        Long valoreFaGar = PgManager.checkFaseGaraPerEsclusione(fasGar);

                        if (moties.longValue() == 98) {
							// Ricavo il numero massimo di gare aggiudicabili da una ditta
							long ngadit = 1;
							try {
								List datiNgadit = this.sqlManager.getListVector(
										"select NGADIT from TORN where CODGAR = ?",
										new Object[]{codgar});
								if (datiNgadit != null && datiNgadit.size() > 0) {
									if (SqlManager.getValueFromVectorParam(
											datiNgadit.get(0), 0).longValue() != null) {
										ngadit = SqlManager.getValueFromVectorParam(
														datiNgadit.get(0), 0).longValue().longValue();
									}
								}
							} catch (SQLException e) {
								throw new GestoreException(
										"Errore nel calcolo del numero massimo di gare aggiudicabili",
										"settaDittaVincitrice.MassimoGareAggiudicabili",
										e);
							}

							// Controllo se la ditta si è aggiudicata il numero
							// massimo di gare
							long conteggio = 0;
							try {
								List datiConteggio = this.sqlManager.getListVector(
										"select count(*) from DITG where CODGAR5 = ? "
										+ "and DITTAO = ? and MOTIES = 98",
										new Object[] { codgar, dittao });
								if (datiConteggio != null && datiConteggio.size() > 0) {
									if (SqlManager.getValueFromVectorParam(
											datiConteggio.get(0), 0).longValue() != null) {
										conteggio = SqlManager.getValueFromVectorParam(
												datiConteggio.get(0), 0).longValue().longValue();
									}
								}
							} catch (SQLException e) {
								throw new GestoreException(
										"Errore durante il controllo del numero di gare aggiudicate "
										+ "dalla ditta " + dittao,
										"settaDittaVincitrice.NumeroGareAggiudicate",
										new Object[] { dittao }, e);
							}

							if (conteggio >= ngadit) {
								String update = "update DITG set AMMGAR = '2', FASGAR = ?, "
										+ "RIBAUO = null, IMPOFF = null, IMPOFF1 = null, PUNTEC = null, PUNTECRIP = null"
										+ "PUNECO = null, PUNECORIP = null, MOTIES = 99, ANNOFF = ? where DITTAO = ? "
										+ "and NGARA5 = ?";
								try {
									this.sqlManager.update(update,
											new Object[] { valoreFaGar, annoff,	dittao, ngaraDITG });

						            this.pgManager.aggiornaDITGAMMIS(codgar, ngaraDITG, dittao,
					                    valoreFaGar, new Long(2), new Long(99), null, true, true, true);

								} catch (SQLException e) {
									throw new GestoreException(
											"Errore durante l'aggiornamento dei dati della ditta "
													+ dittao,	"settaDittaVincitrice",
											new Object[] { dittao }, e);
								}
							}
						} else {
							String update = "update DITG set AMMGAR = '1', FASGAR = null, "
									+ "MOTIES = 100 where DITTAO = ? and NGARA5 = ?";
							try {
								this.sqlManager.update(update, new Object[]{dittao, ngaraDITG });

								this.pgManager.aggiornaDITGAMMIS(codgar, ngaraDITG, dittao,
								    valoreFaGar, new Long(1), new Long(100), null, true, true, true);

							} catch (SQLException e) {
								throw new GestoreException(
										"Errore durante l'aggiornamento dei dati della ditta "
										+ dittao,	"settaDittaVincitrice",	new Object[]{ dittao }, e);
							}
						}
					}
				}
			} catch (SQLException e) {
				throw new GestoreException(
						"Errore durante l'aggiornamento dei dati della ditta " + dittao,
						"settaDittaVincitrice",	new Object[] { dittao }, e);
			}
		}

		if (logger.isDebugEnabled())
			logger.debug("settaDittaVincitrice(" + codgar + ", " + ngara + ", "
					+ dittao + ", " + moties + "): fine metodo");
	}

	/**
	 * Lettura del campo note (Annoff) della ditta vincitrice
	 *
	 * @param ngara
	 * @param dittao
	 * @return
	 * @throws GestoreException
	 */
	private String leggiNoteDittaVincitrice(String ngara, String dittao)
			throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("leggiNoteDittaVincitrice(" + ngara + ", " + dittao
					+ "): inizio metodo");

		String result = "";
		String selectDITG = "select annoff from ditg where ngara5 = ? and dittao = ?";

		try {
			List datiDITG = this.sqlManager.getListVector(selectDITG,
					new Object[]{ngara, dittao});
			if (datiDITG != null && datiDITG.size() > 0) {
				result = SqlManager.getValueFromVectorParam(
						datiDITG.get(0), 0).stringValue();
			}
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore della lettura del motivo di esclusione della ditta "
					+ dittao, "leggiAnnoffDittaVincitrice",	new Object[] { dittao }, e);
		}

		if (logger.isDebugEnabled())
			logger.debug("leggiNoteDittaVincitrice(" + ngara + ", " + dittao
					+ "): fine metodo");

		return result;
	}

	/**
	 *
	 * @param impl
	 * @param hMapParametri
	 * @throws GestoreException
	 */
	private void aggiornaImplGAREFaseA(String ngara, DataColumnContainer impl,
			HashMap hMapParametri, HashMap hMapGare) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("aggiornaImplGAREFaseA(" + ngara + "): inizio metodo");

		Long longFasGar = new Long(
				GestioneFasiGaraFunction.FASE_CALCOLO_AGGIUDICAZIONE/10);
		Long longStepGar = new Long(
				GestioneFasiGaraFunction.FASE_CALCOLO_AGGIUDICAZIONE);

		impl.setValue("GARE.DITTAP", null);
		impl.setValue("GARE.RIBPRO", null);
		impl.setValue("GARE.IAGPRO", null);
		// Si sbiancano anche i campi relativi all'aggiudicazione definitiva
		impl.setValue("GARE.NOMIMA", null);
		impl.setValue("GARE.IMPGAR", null);
		impl.setValue("GARE.DITTA", null);
		impl.setValue("GARE.RIBAGG", null);
		impl.setValue("GARE.IAGGIU", null);

		impl.setValue("GARE.FASGAR", longFasGar);
		impl.setValue("GARE.STEPGAR", longStepGar);
		impl.setValue("GARE.LIMMAX", hMapParametri
				.get("limiteAnomalia"));
		impl.getColumn("GARE.LIMMAX").setObjectOriginalValue(null);
		impl.setValue("GARE.LIMMIN", hMapParametri
				.get("ribassoLimite"));
		impl.setValue("GARE.NOFVAL", hMapParametri
				.get("numeroDitteAmmesse"));
		impl.getColumn("GARE.NOFVAL").setObjectOriginalValue(null);

		Long numeroDitte = (Long)hMapParametri.get("numeroDitte");
		Double mediaRibasso = (Double)hMapParametri.get("mediaRibasso");

		/*
		if (hMapParametri.get("valorizzaMediasca") != null){
          if( ((Boolean) hMapParametri.get("valorizzaMediasca")).booleanValue())
            impl.addColumn("GARE1.MEDIASCA", hMapParametri.get("mediaScarti"));
		}
		*/
		if (hMapParametri.get("valorizzaMediasca") == null || (hMapParametri.get("valorizzaMediasca") != null && ((Boolean) hMapParametri.get("valorizzaMediasca")).booleanValue())){
          impl.addColumn("GARE1.MEDIASCA", hMapParametri.get("mediaScarti"));
        }
		if(hMapGare.get("escauto")!=null){
		  impl.addColumn("GARE1.ESCAUTO", hMapGare.get("escauto"));
		  if(impl.isColumn("GARE1.ESCAUTOFIT"))
		    impl.removeColumns(new String[]{"GARE1.ESCAUTOFIT"});
		}


		impl.addColumn("GARE1.MEDIAIMP", JdbcParametro.TIPO_DECIMALE,null);
		impl.addColumn("GARE1.SOGLIAIMP", JdbcParametro.TIPO_DECIMALE,null);
		impl.addColumn("GARE1.SOMMARIB", JdbcParametro.TIPO_DECIMALE,null);

		impl.addColumn("GARE1.SOGLIA1", JdbcParametro.TIPO_DECIMALE,null);
		impl.addColumn("GARE1.SOGLIAVAR", JdbcParametro.TIPO_DECIMALE,null);
		impl.addColumn("GARE1.MEDIARAP", JdbcParametro.TIPO_DECIMALE,null);
		impl.addColumn("GARE1.SOGLIANORMA", JdbcParametro.TIPO_TESTO,null);

		boolean modalitaDL2016 = false;
		if (hMapParametri.get("modalitaDL2016") != null)
		  modalitaDL2016 = ((Boolean) hMapParametri.get("modalitaDL2016")).booleanValue();

		boolean modalitaDL2017 = false;
        if (hMapParametri.get("modalitaDL2017") != null)
          modalitaDL2017 = ((Boolean) hMapParametri.get("modalitaDL2017")).booleanValue();

        boolean modalitaDL2019 = false;
        if (hMapParametri.get("modalitaDL2019") != null)
          modalitaDL2019 = ((Boolean) hMapParametri.get("modalitaDL2019")).booleanValue();

        boolean modalitaDLCalcoloGraduatoria=false;
        if (hMapParametri.get("modalitaDLCalcoloGraduatoria") != null)
          modalitaDLCalcoloGraduatoria = ((Boolean) hMapParametri.get("modalitaDLCalcoloGraduatoria")).booleanValue();

		if ((modalitaDL2016 || modalitaDL2017) && !modalitaDLCalcoloGraduatoria){

		   impl.setValue("GARE1.METCOEFF", hMapParametri
               .get("coefficenteMetodoCalcoloSogliaAnomalia"));
		   int metodo = ((Long) hMapParametri.get("metodoCalcoloSogliaAnomalia")).intValue();
	        switch (metodo) {
	          case 2:
	            impl.setValue("GARE1.SOMMARIB", hMapParametri.get("sommaRib"));
	            break;
	          case 3:
	            numeroDitte = null;
                break;
	          case 4:
                numeroDitte = null;
                if(modalitaDL2016)
                  mediaRibasso = null;
                impl.setValue("GARE1.MEDIAIMP", hMapParametri.get("mediaRibassiAssoluti"));
                impl.setValue("GARE1.SOGLIAIMP", hMapParametri.get("decrementoMedia"));
                break;
	        }
	        if(metodo!=5){
	          impl.setValue("GARE1.METCOEFF",null);
	          impl.getColumn("GARE1.METCOEFF").setObjectOriginalValue(-1000.00);
	        }
		}else if (modalitaDL2019){
		  if(!modalitaDLCalcoloGraduatoria){
    		  impl.setValue("GARE1.SOMMARIB", hMapParametri.get("sommaRib"));
    		  long numDitteAmmesse = 0;
    		  if(hMapParametri.get("numeroDitteAmmesse")!=null)
    		    numDitteAmmesse = ((Long)hMapParametri.get("numeroDitteAmmesse")).longValue();
    		  if(numDitteAmmesse >= 15){
                impl.setValue("GARE1.SOGLIA1", hMapParametri.get("soglia1"));
                impl.setValue("GARE1.SOGLIAVAR", hMapParametri.get("sogliavar"));
    		  }else{
    		    impl.setValue("GARE1.MEDIARAP", hMapParametri.get("mediarap"));
    		  }
    	  }
		  impl.setValue("GARE1.SOGLIANORMA", hMapParametri.get("sogliaNorma"));
		}else{
		  //impl.setValue("GARE1.METSOGLIA", null);
		  //impl.setValue("GARE1.METCOEFF",null);
		  //impl.getColumn("GARE1.METCOEFF").setObjectOriginalValue(-1000.00);

		  impl.getColumn("GARE1.MEDIAIMP").setObjectOriginalValue(0.00);
		  impl.getColumn("GARE1.SOGLIAIMP").setObjectOriginalValue(0.00);
		  impl.getColumn("GARE1.SOMMARIB").setObjectOriginalValue(0.00);
		}

		impl.setValue("GARE.NOFMED", numeroDitte);
		impl.getColumn("GARE.NOFMED").setObjectOriginalValue(null);
		impl.setValue("GARE.MEDIA", mediaRibasso);
		impl.getColumn("GARE.MEDIA").setObjectOriginalValue(null);


		int modlicg = 0;
        if (hMapGare.get("modlicg") != null)
           modlicg = ((Long) hMapGare.get("modlicg")).intValue();

        long metsoglia = 0;
        if (hMapParametri.get("metodoCalcoloSogliaAnomalia") != null)
           metsoglia = ((Long) hMapParametri.get("metodoCalcoloSogliaAnomalia")).longValue();

        String calcsoang = null;
        if(hMapGare.get("calcsoang") != null)
            calcsoang = (String) hMapGare.get("calcsoang");

        long numMinDitteCalcoloSoglia=0;
        if (hMapParametri.get("numMinDitteCalcoloSoglia") != null)
          numMinDitteCalcoloSoglia = ((Long) hMapParametri.get("numMinDitteCalcoloSoglia")).longValue();

        Long numeroDitteAmmesse = null;
        if (hMapParametri.get("numeroDitteAmmesse") != null)
            numeroDitteAmmesse = ((Long) hMapParametri.get("numeroDitteAmmesse")).longValue();

        if(modlicg!=6 && metsoglia!=3 && metsoglia!=4 && "1".equals(calcsoang) && numeroDitteAmmesse>=numMinDitteCalcoloSoglia){
          Long numAliInf=null;
          if (hMapParametri.get("numAliInf") != null && ((Long) hMapParametri.get("numAliInf")).longValue()!=0)
            numAliInf = ((Long) hMapParametri.get("numAliInf"));
          impl.addColumn("GARE1.NOFALAINF", numAliInf);
          impl.getColumn("GARE1.NOFALAINF").setOriginalValue(null);
          Long numAliSup=null;
          if (hMapParametri.get("numAliSup") != null && ((Long) hMapParametri.get("numAliSup")).longValue()!=0)
            numAliSup = ((Long) hMapParametri.get("numAliSup"));
          impl.addColumn("GARE1.NOFALASUP", numAliSup);
          impl.getColumn("GARE1.NOFALASUP").setOriginalValue(null);
        }

        if("1".equals(calcsoang) && modlicg!=6 && mediaRibasso!=null && mediaRibasso.doubleValue()!=0){
          Boolean troncamento = (Boolean)hMapParametri.get("troncamento");
          Long sogliacalc = new Long(2);
          if(!troncamento)
            sogliacalc = new Long(1);
          impl.addColumn("GARE1.SOGLIACALC",sogliacalc);
          impl.getColumn("GARE1.SOGLIACALC").setOriginalValue(null);
        }

        String legRegSic = "";
        if (hMapGare.get("legRegSic") != null)
          legRegSic = ( (String) hMapGare.get("legRegSic"));
        if("1".equals(legRegSic)){
          impl.setValue("GARE1.SOGLIANORMA", this.stringaSogliaNorma_REG_SIC);
          impl.setValue("GARE1.SOMMARIB", hMapParametri.get("sommaRib"));
        }

		// Nel caso di gara a lotti con offerta unica bisogna aggiornare il FASGAR
		// dell'occorrenza complementare. L'istruzione java per tale aggiornamento
		// viene eseguita sempre, ma l'update avviene solo per gare con campo
		// GARE.GENERE = 3 and CODGAR1 = NGARA and NGARA = 'codice della gara di
		// interesse'
		try {
			this.sqlManager.update("update GARE set FASGAR = ?, STEPGAR = ? "
				+ "where CODGAR1 = (select CODGAR1 from GARE where NGARA = ? ) "
				+  " and GENERE = 3", new Object[]{longFasGar, longStepGar, ngara});
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nell'aggiornamento del campo GARE.FASGAR per l'occorrenza "
							+ "complementare di una gara a lotti con plico unico",
					"aggiornaImplGAREFaseB", e);
		}

		//Si deve gestire lo sbiancamento dei dati di GARECONT visto che si cancellano i dati dell'aggiudicazione definitiva

		try {
          String ditta = (String)this.sqlManager.getObject("select ditta from gare where ngara=?", new Object[]{ngara});
		  this.sqlManager.update("update gare set ditta=null where ngara=?",  new Object[]{ngara});
          this.allineamentoGarecontSbiancamentoDitta(impl.getString("GARE.CODGAR1"), ngara,ditta);
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore nell'aggiornamento della tabella GARECONT",
              "aggiornaImplGAREFaseA", e);
        }

		if (logger.isDebugEnabled())
			logger.debug("aggiornaImplGAREFaseA(" + ngara + "): fine metodo");
	}

	/**
	 * Aggiornamento di alcuni dati dell'entità GARE alla fine della fase di
	 * aggiudicazione.
	 *
	 * @param impl
	 * @param hMapParametri
	 * @throws GestoreException
	 */
	private void aggiornaImplGAREFaseB(String ngara, DataColumnContainer impl,
			HashMap hMapParametri, boolean isAggiudicazioneDefinitiva, HashMap hMapTORN, HashMap hMapGARE)
			throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("aggiornaImplGAREFaseB(" + ngara + "): inizio metodo");

		String dittao = (String) hMapParametri
				.get("primaAggiudicatariaSelezionata");
		String nomimo = null;
		Double ribauo = null;
		Double riboepv = null;

		String selectDITG = "select nomimo, ribauo, riboepv from ditg where ngara5 = ? and dittao = ?";
		try {
			List datiDITG = this.sqlManager.getListVector(selectDITG,
					new Object[] { ngara, dittao });
			if (datiDITG != null && datiDITG.size() > 0) {
				nomimo = SqlManager.getValueFromVectorParam(
						datiDITG.get(0), 0).stringValue();
				ribauo = SqlManager.getValueFromVectorParam(
						datiDITG.get(0), 1).doubleValue();
				riboepv= SqlManager.getValueFromVectorParam(
                    datiDITG.get(0), 2).doubleValue();
			}
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nella selezione delle ditta prima vincitrice",
					"aggiornaImplGAREFaseB", e);
		}

		String codiceGara = impl.getString("GARE.CODGAR1");

		if (isAggiudicazioneDefinitiva) {
			impl.setValue("GARE.FASGAR", new Long(
					GestioneFasiGaraFunction.FASE_AGGIUDICAZIONE_DEFINITIVA/10));
			impl.setValue("GARE.NOMIMA", nomimo);
			impl.setValue("GARE.DITTA", dittao);
			impl.setValue("GARE.RIBAGG", hMapParametri.get("ribauo"));
			impl.setValue("GARE.RIBOEPV", hMapParametri.get("riboepv"));
			impl.setValue("GARE.IAGGIU", hMapParametri.get("iaggiu"));
			impl.setValue("GARE.IMPGAR", hMapParametri.get("impgar"));
			long aqoper=0;
	        if (hMapGARE.get("aqoper") != null)
	          aqoper = ((Long) hMapGARE.get("aqoper")).longValue();
			try {
    			//Si deve aggiornare il campo CODIMP di GARECONT, tranne nel caso di lotti di gara ad offerta unica
			  Long genere = (Long) this.sqlManager.getObject("select genere from gare where ngara=?", new Object[]{codiceGara});
			  if(genere==null || (genere!=null && genere.longValue()!=3)){
			    //Lettura del campo COORBA,CODBIC di impr della ditta aggiudicataria
		          String coorba = null;
		          String codbic = null;
		          Vector datiBancari = this.sqlManager.getVector("select coorba,codbic from impr where codimp=?", new Object[]{dittao});
		          if(datiBancari!=null && datiBancari.size()>0){
		            coorba=SqlManager.getValueFromVectorParam(datiBancari, 0).stringValue();
		            codbic=SqlManager.getValueFromVectorParam(datiBancari, 1).stringValue();
		          }
		          Long iterga = (Long)hMapTORN.get("iterga");
		          if(aqoper==2){
		            this.sqlManager.update("update GARECONT set CODIMP = ? where NGARA = ? and NCONT = ?",
		                new Object[]{dittao, ngara, new Long(1) });
		          }else{
    		          String update="update GARECONT set CODIMP = ?, COORBA = ?, CODBIC = ?  where NGARA = ? and NCONT = ?";
    		          if(iterga!=null && iterga.longValue()==6)
                        update="update GARECONT set CODIMP = ?, COORBA = ?, CODBIC = ?, STATO=2 where NGARA = ? and NCONT = ?";
    		          this.sqlManager.update(
    		              update, new Object[]{dittao, coorba, codbic, ngara, new Long(1) });
		          }
		          //Se gara telematica ed è valorizzato ivalav si devono cancellare le occorrenze di GAREIVA ed inserire una nuova riga
		          if(iterga!=null && iterga.longValue()==6 && aqoper!=2){
		            this.calcoImportoIva(ngara, (Double)hMapParametri.get("iaggiu"));
	              }
			  }
			} catch (SQLException e) {
	            throw new GestoreException(
	                    "Errore nell'aggiornamento della tabella GARECONT",
	                    "aggiornaImplGAREFaseB", e);
	        }
		} else {
		    impl.setValue("GARE.FASGAR", new Long(
					GestioneFasiGaraFunction.FASE_CALCOLO_AGGIUDICAZIONE/10));
			impl.setValue("GARE.DITTAP", dittao);
			impl.setValue("GARE.RIBPRO", ribauo);
			impl.setValue("GARE.IAGPRO", hMapParametri.get("iaggiu"));
			// Si sbiancano anche i campi relativi all'aggiudicazione definitiva
			impl.setValue("GARE.DITTA", null);
			impl.setValue("GARE.NOMIMA", null);
			impl.setValue("GARE.RIBAGG", null);
			impl.setValue("GARE.IAGGIU", null);

			try {
			  String ditta = (String)this.sqlManager.getObject("select ditta from gare where ngara=?", new Object[]{ngara});
	          this.sqlManager.update("update gare set ditta=null where ngara=?",  new Object[]{ngara});
			  this.allineamentoGarecontSbiancamentoDitta(impl.getString("GARE.CODGAR1"), ngara,ditta);
            } catch (SQLException e) {
              throw new GestoreException(
                  "Errore nell'aggiornamento della tabella GARECONT",
                  "aggiornaImplGAREFaseB", e);
            }

			try {
  			this.sqlManager.update("delete from DITGAQ where NGARA = ?",
  	              new Object[] { ngara});
			 } catch (SQLException e) {
	              throw new GestoreException(
	                  "Errore nell'aggiornamento della tabella DITGAQ",
	                  "aggiornaImplGAREFaseB", e);
	            }
		}

		// Nel caso di gara a lotti con offerta unica bisogna aggiornare il
		// FASGAR dell'occorrenza complementare solo se tutti i lotti, a meno del lotto
		// corrente, sono aggiudicati definitavamente. L'istruzione java per
		// tale aggiornamento viene eseguita sempre, ma l'update avviene solo per
		// gare con campo GARE.GENERE = 3 and CODGAR1 = NGARA and NGARA = 'codice
		// della gara di interesse'
		////////////////////////////////
		// Inoltre nel caso di aggiudicazione provvisoria, si deve impostare il
		//FASGAR della gara complementare a 'FASE_CALCOLO_AGGIUDICAZIONE'

		// if(isAggiudicazioneDefinitiva){
		try {
			Long numeroLotti = (Long) this.sqlManager.getObject(
							"select count(*) from GARE where CODGAR1 = ? and GENERE is null",
							new Object[] { codiceGara });
			Long numeroLottiAggiudicatiDef = (Long) this.sqlManager.getObject(
					"select count(*) from GARE where CODGAR1 = ? and GENERE is null "
							+ "and (FASGAR = 8 or ESINEG > 0)", new Object[] { codiceGara });
			if (numeroLotti != null && numeroLottiAggiudicatiDef != null &&
					((numeroLotti.longValue() - 1) == numeroLottiAggiudicatiDef.longValue()
							&& isAggiudicazioneDefinitiva) || !isAggiudicazioneDefinitiva){
				Long longFasGar = impl.getLong("GARE.FASGAR");
				Long longStepGar = null;
				if(longFasGar != null)
					longStepGar = new Long(longFasGar.longValue() * 10);

				this.sqlManager.update(
						"update GARE set FASGAR = ?, STEPGAR = ? "
					+  "where CODGAR1 = (select CODGAR1 from GARE where NGARA = ? ) "
						+ " and GENERE = 3", new Object[]{longFasGar, longStepGar, ngara });
			}
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nell'aggiornamento del campo GARE.FASGAR per l'occorrenza "
							+ "complementare di una gara a lotti con plico unico",
					"aggiornaImplGAREFaseB", e);
		}
		// }
		if (logger.isDebugEnabled())
			logger.debug("aggiornaImplGAREFaseB(" + ngara + "): fine metodo");
	}

	/**
	 *
	 * @param impl
	 * @param hMapParametri
	 * @throws GestoreException
	 */
	private void aggiornaImplGAREannullaCalcolo(String ngara, String codgar, Long bustalotti, boolean aggiornaFase, boolean gestioneOffertaEco)
			throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("aggiornaImplGAREannullaCalcolo(" + ngara
					+ "): inizio metodo");

		Long fasgar = new Long(GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE/10);
		Long stepgar = new Long(GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE);
		if(!gestioneOffertaEco){
		  fasgar = new Long(GestioneFasiGaraFunction.FASE_CHIUSURA_VALUTAZIONE_TECNICA/10);
	      stepgar = new Long(GestioneFasiGaraFunction.FASE_CHIUSURA_VALUTAZIONE_TECNICA);
		}

		try {
		  if(aggiornaFase){
		    //Se asta elettronica non si deve aggiornare la fase
		    String ricastae = (String)this.sqlManager.getObject("select ricastae from torn where codgar=?", new Object[]{codgar});
		    if("1".equals(ricastae))
		      aggiornaFase = false;
		  }
		  if(aggiornaFase){
    		if((new Long(1)).equals(bustalotti) && codgar.equals(ngara)){
    	          //Nel caso di bustalotti=1 si deve aggiornare fasgar della gara complementare con il max dei fasgar dei lotti
    	          Long maxFasgar = (Long)this.sqlManager.getObject("select max(fasgar) from gare where codgar1=? and codgar1!=ngara", new Object[]{codgar});

    	          if(maxFasgar!=null){
    	            fasgar = maxFasgar;
    	            //stepgar = new Long (maxFasgar.longValue() * 10);
    	            stepgar = (Long)this.sqlManager.getObject("select max(stepgar) from gare where codgar1=? and codgar1!=ngara", new Object[]{codgar});
    	          }
    		  }

    		  this.sqlManager.update(
                  "update GARE set DITTAP = null, RIBPRO = null, "
                          + "IAGPRO = null, LIMMAX = null, LIMMIN = null, "
                          + "NOFVAL = null, NOFMED = null, MEDIA = null, FASGAR = ?, "
                          + "STEPGAR = ?, NSORTE = null, ALAINF = null, ALASUP = null where NGARA = ? and CODGAR1 = ?",
                  new Object[] {fasgar, stepgar, ngara, codgar});
		  }else{
		    this.sqlManager.update(
                "update GARE set DITTAP = null, RIBPRO = null, "
                        + "IAGPRO = null, LIMMAX = null, LIMMIN = null, "
                        + "NOFVAL = null, NOFMED = null, MEDIA = null, "
                        + "NSORTE = null, ALAINF = null, ALASUP = null where NGARA = ? and CODGAR1 = ?",
                new Object[] {ngara, codgar});
		  }

		  this.sqlManager.update(
              "update GARE1 set MEDIASCA = null, SOMMARIB = null, "
                      + "MEDIAIMP = null, SOGLIAIMP = null, NOFALAINF = null, NOFALASUP = null,"
                      + "SOGLIA1 = null, SOGLIAVAR = null, MEDIARAP = null, SOGLIANORMA = null,"
                      + "LEGREGSIC = null, ESCAUTO = null where NGARA = ? and CODGAR1 = ?",
              new Object[] {ngara, codgar});


		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nell'aggiornamento dei campi relativi all'aggiudicazione " +
					"provvisoria e relativi all'aggiudicazione defintiva del lotto " +
					ngara + " della gara " + codgar +
					" durante l'annullamento del calcolo aggiudicazione", null, e);
		}

		if (logger.isDebugEnabled())
			logger.debug("aggiornaImplGAREannullaCalcolo(" + ngara
					+ "): fine metodo");
	}

	/**
	 * Imposta a "2" il valore del campo CONGRUO nel caso in cui il valore
	 * originale sia nullo o 0
	 *
	 * @param ngara
	 * @throws GestoreException
	 */
	private void settaCongruoADue(String ngara) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("settaCongruoADue(" + ngara + "): inizio metodo");

		String updateDITG = "update ditg set congruo='2' where ngara5 = ? "
				+ "and staggi > 1 and (congruo is null or congruo = '0')";

		try {
			this.sqlManager.update(updateDITG, new Object[] { ngara });
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore durante l'aggiornamento della lista delle ditte partecipanti alla gara",
					"settaCongruoADue", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("settaCongruoADue(" + ngara + "): due metodo");

	}

	/**
	 * Imposta la prima vincitrice (staggi = 4) o se esistono prime
	 * aggiudicatarie parimerito ricava la lista con i codici delle ditte
	 *
	 * @param ngara
	 * @param hMapGARE
	 * @throws GestoreException
	 */
	private void settaPrimaAggiudicataria(String ngara, HashMap hMapGARE,
			HashMap hMapParametri) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("settaPrimaAggiudicataria(" + ngara + "): inizio metodo");

		String dittao = null;
		Long staggi = new Long(0);
		String congruo = null;
		Double ribauo = null;
		Double ribauoPrimaVincitrice = null;
		String listaPrimeParimerito = null;
		String primaAggiudicataria = null;
		long numeroPrimeParimerito = 0;

		boolean isCalcoloSogliaAnomaliaApplicabile = true;
		if (hMapParametri.get("isCalcoloSogliaAnomaliaApplicabile") != null)
			isCalcoloSogliaAnomaliaApplicabile = ((Boolean) hMapParametri.get(
					"isCalcoloSogliaAnomaliaApplicabile")).booleanValue();

		boolean isEsclusioneAutomaticaApplicabile = true;
		if (hMapParametri.get("isEsclusioneAutomaticaApplicabile") != null)
			isEsclusioneAutomaticaApplicabile = ((Boolean) hMapParametri.get(
					"isEsclusioneAutomaticaApplicabile")).booleanValue();

		long modastg = 0;
		if (hMapGARE.get("modastg") != null)
			modastg = ((Long) hMapGARE.get("modastg")).longValue();

		long modlicg = 0;
		if (hMapGARE.get("modlicg") != null)
			modlicg = ((Long) hMapGARE.get("modlicg")).longValue();

        String legRegSic = "";
        if (hMapGARE.get("legRegSic") != null)
          legRegSic = ( (String) hMapGARE.get("legRegSic"));

        String costofisso = ((String)hMapGARE.get("costofisso")).toString();

        String importoOffertaCongiunta = "";
        String selectImportoOffertaCongiunta = "";
        if(hMapGARE.get("ultdetlic") != null && !"1".equals(costofisso)){
          importoOffertaCongiunta = "IMPOFF - " + this.sqlManager.getDBFunction("isnull",  new String[] {"IMPPERM", "0" });
          importoOffertaCongiunta += " + " + this.sqlManager.getDBFunction("isnull",  new String[] {"IMPCANO", "0" });
          selectImportoOffertaCongiunta = ", "+ importoOffertaCongiunta;
        }

		String selectDITG = "select dittao, staggi, congruo, ribauo"+selectImportoOffertaCongiunta+" from ditg "
				+ "where ngara5 = ? " + "and staggi > 1 ";

		if (hMapParametri.get("garaInversa") != null && "1".equals(hMapParametri.get("garaInversa"))){
	        //si devono escludere le ditte con amminversa='2'
	        selectDITG +=" and (amminversa != 2 or amminversa is null)";
	    }

        if(hMapGARE.get("ultdetlic") != null && !"1".equals(costofisso)){
            selectDITG += " order by " + importoOffertaCongiunta + ", staggi";
        }else if (modlicg == 6 || modlicg == 17) {
            selectDITG += " order by ribauo desc, staggi";
        } else {
            selectDITG += " order by ribauo, staggi";
        }

		try {
			List datiDITG = this.sqlManager.getListVector(selectDITG,
					new Object[] { ngara });
			if (datiDITG != null && datiDITG.size() > 0) {
			    boolean flag_anomale = false;
			    for (int i = 0; i < datiDITG.size(); i++) {
					dittao = SqlManager.getValueFromVectorParam(
							datiDITG.get(i), 0).stringValue();
					staggi = SqlManager.getValueFromVectorParam(
							datiDITG.get(i), 1).longValue();
					congruo = SqlManager.getValueFromVectorParam(
							datiDITG.get(i), 2).stringValue();
			        if(hMapGARE.get("ultdetlic") != null && !"1".equals(costofisso)){
                      ribauo = SqlManager.getValueFromVectorParam(
                          datiDITG.get(i), 4).doubleValue();
			        } else {
	                    ribauo = SqlManager.getValueFromVectorParam(
                            datiDITG.get(i), 3).doubleValue();
			        }
					if(modlicg == 1 || modlicg == 5 || modlicg == 17){
						if("1".equals(congruo)){
						    if (numeroPrimeParimerito == 0) {
								numeroPrimeParimerito++;
								ribauoPrimaVincitrice = ribauo;
								primaAggiudicataria = dittao;
								listaPrimeParimerito = "'" + primaAggiudicataria + "'";
							} else{
							      if (ribauo.equals(ribauoPrimaVincitrice)) {
    								numeroPrimeParimerito++;
    								listaPrimeParimerito += ",'" + dittao + "'";
    							  }
							}
						}
					} else if(modlicg == 13 || modlicg == 14 ){
						// Controllare il valore di GARE.MODLICG = 13 o 14 equivale a
						// controllare che GARE.CALCSOANG sia uguale a 1

						if(modastg == 1 && isCalcoloSogliaAnomaliaApplicabile && isEsclusioneAutomaticaApplicabile){
                            // Gara o lotto di gara con GARE.CALCSOANG = 1 e nella condizione
                            // di applicabilita' del calcolo soglia anomalia e di applicabilita'
                            // dell'esclusione automatica
						    // Considera la prima ditta Congrua e Non anomala

						  //L.R.Sicilia n.14/2015
						  if ("1".equals(legRegSic)) {
						    //Se tutte le offerte sono anomale, perchè la soglia di anomalia è inferiore a tutte le offerte in gara,
						    // aggiudica alla ditta anomala con ribasso più vicino alla soglia di anomalia, ovvero all'ultima ditta anomala in classifica,
						    // senza considerare la congruità dell'offerta, che sarà sicuramente a 'no' perchè si è nel caso di esclusione automatica.
	                          if(new Long(3).equals(staggi)){//se anomala
	                              if (ribauoPrimaVincitrice == null || (ribauoPrimaVincitrice !=null
	                                    && ((hMapGARE.get("ultdetlic") == null && ribauo > ribauoPrimaVincitrice) || (hMapGARE.get("ultdetlic") != null && !"1".equals(costofisso) && ribauo < ribauoPrimaVincitrice)))){
	                                  numeroPrimeParimerito = 1;
	                                  ribauoPrimaVincitrice = ribauo;
	                                  primaAggiudicataria = dittao;
	                                  listaPrimeParimerito = "'" + primaAggiudicataria + "'";
	                              } else if (ribauo.equals(ribauoPrimaVincitrice)) {
	                                  numeroPrimeParimerito++;
	                                  listaPrimeParimerito += ",'" + dittao + "'";
	                                }

	                                flag_anomale=true;
	                          }
	                          //Se ci sono ditte non anomale, resetta le variabili inizializzate sopra con i dati delle ditte anomale
	                          if(new Long(6).equals(staggi)){
	                           if(flag_anomale == true){
	                             flag_anomale=false;
	                             //resetto variabili
                                 numeroPrimeParimerito = 0;
                                 ribauoPrimaVincitrice = null;
                                 primaAggiudicataria = null;
                                 listaPrimeParimerito = null;
                               }
	                          }
						  }

                          if("1".equals(congruo) && (new Long(6).equals(staggi) ||  new Long(10).equals(staggi))){
                                if (numeroPrimeParimerito == 0) {
                                    numeroPrimeParimerito++;
                                    ribauoPrimaVincitrice = ribauo;
                                    primaAggiudicataria = dittao;
                                    listaPrimeParimerito = "'" + primaAggiudicataria + "'";
                                } else if (ribauo.equals(ribauoPrimaVincitrice) ) {
                                    numeroPrimeParimerito++;
                                    listaPrimeParimerito += ",'" + dittao + "'";
                                }

                            }else{




                            }


						} else {
							// Gara o lotto di gara con GARE.CALCSOANG = 1 e nella condizione
							// di applicabilita' del calcolo soglia anomalia e di non applicabilita'
							// dell'esclusione automatica
                            // oppure di non applicabilità del calcolo soglia anomalia
						    // oppure è previsto da gara di non fare mai l'esclusione automatica
						    // Considera la prima ditta Congrua
							if("1".equals(congruo)){
								if (numeroPrimeParimerito == 0 ) {
									numeroPrimeParimerito++;
									ribauoPrimaVincitrice = ribauo;
									primaAggiudicataria = dittao;
									listaPrimeParimerito = "'" + primaAggiudicataria + "'";
								} else if (ribauo.equals(ribauoPrimaVincitrice) ) {
									numeroPrimeParimerito++;
									listaPrimeParimerito += ",'" + dittao + "'";
								  }


							}
						}
					} else if(modlicg == 6){
						// Gara o lotto di gara OEPV
						// Se GARE.CALCSOANG = 1, allora vince la prima ditta congrua (anche
						// se anomala) con punteggio piu' alto.
						// Se GARE.CALCSOANG = 2, allora vince la prima ditta congrua con
						// punteggio piu' alto.
						// A prescindere dal valore di GARE.CALCSOANG, considera la prima ditta Congrua
						if("1".equals(congruo)){
							if (numeroPrimeParimerito == 0 ) {
								numeroPrimeParimerito++;
								ribauoPrimaVincitrice = ribauo;
								primaAggiudicataria = dittao;
								listaPrimeParimerito = "'" + primaAggiudicataria + "'";

							} else if (ribauo!=null &&  ribauo.equals(ribauoPrimaVincitrice) ) {
								numeroPrimeParimerito++;
								listaPrimeParimerito += ",'" + dittao + "'";

							  }


						}
					}
				}//fine ciclo ditte
			    //Nel caso di Regione Sicilia e si rientra nel caso di esclusione automatica con
			    //tutte le ditte anomale, si imposta congruo a 1 per la prima ditta aggiudicataria
			    //perchè per l'aggiudicazione definitiva si considerano solo le ditte con congruo=1
			    if(numeroPrimeParimerito == 1 && flag_anomale){
			      this.aggiornaCampiCongruita(ngara, primaAggiudicataria, "1");
			    }
			}


			// Se esiste una sola ditta prima aggiudicataria allora si procede a
			// settare lo stato a prima ditta vincitrice (STAGGI = 4)
			if (numeroPrimeParimerito == 1) {
				this.aggiornaStatoAggiudicazione(ngara, primaAggiudicataria,
						new Long(4));
				hMapParametri.put("numeroPrimeParimerito", new Long(1));
				hMapParametri.put("listaPrimeParimerito", listaPrimeParimerito);
				hMapParametri.put("primaAggiudicatariaSelezionata",
						primaAggiudicataria);

			} else {
				hMapParametri.put("numeroPrimeParimerito", new Long(
						numeroPrimeParimerito));
				hMapParametri.put("listaPrimeParimerito", listaPrimeParimerito);
				hMapParametri.put("primaAggiudicatariaSelezionata", null);
			}


		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nel calcolo della prima aggiudicataria",
					"settaPrimaAggiudicataria", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("settaPrimaAggiudicataria(" + ngara + "): fine metodo");
	}

	/**
	 * Imposta la seconda vincitrice (staggi = 5) o se esistono ditte seconde
	 * vincitrici parimerito ricava la lista con i codice delle ditte
	 *
	 * @param ngara
	 * @param hMapGARE
	 * @throws GestoreException
	 */
	private void settaSecondaAggiudicataria(String ngara, HashMap hMapGARE,
			HashMap hMapParametri) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("settaSecondaAggiudicataria(" + ngara + "): inizio metodo");

		String dittao = null;
		Long staggi = new Long(0);
		String congruo = null;
		Double ribauo = null;
		Double ribauoSecondaVincitrice = null;
		String listaSecondeParimerito = null;
		String secondaAggiudicataria = null;
		long numeroSecondeParimerito = 0;

		boolean isCalcoloSogliaAnomaliaApplicabile = true;
		if (hMapParametri.get("isCalcoloSogliaAnomaliaApplicabile") != null)
			isCalcoloSogliaAnomaliaApplicabile = ((Boolean) hMapParametri.get(
					"isCalcoloSogliaAnomaliaApplicabile")).booleanValue();

		boolean isEsclusioneAutomaticaApplicabile = true;
		if (hMapParametri.get("isEsclusioneAutomaticaApplicabile") != null)
			isEsclusioneAutomaticaApplicabile = ((Boolean) hMapParametri.get(
					"isEsclusioneAutomaticaApplicabile")).booleanValue();

		long modastg = 0;
		if (hMapGARE.get("modastg") != null)
			modastg = ((Long) hMapGARE.get("modastg")).longValue();

		long modlicg = 0;
		if (hMapGARE.get("modlicg") != null)
			modlicg = ((Long) hMapGARE.get("modlicg")).longValue();

        String importoOffertaCongiunta = "";
        String selectImportoOffertaCongiunta = "";
        String costofisso ="";
        if (hMapGARE.get("costofisso") != null)
          costofisso = ((String) hMapGARE.get("costofisso")).toString();

        if(hMapGARE.get("ultdetlic") != null && !"1".equals(costofisso)){
          importoOffertaCongiunta = "IMPOFF - " + this.sqlManager.getDBFunction("isnull",  new String[] {"IMPPERM", "0" });
          importoOffertaCongiunta += " + " + this.sqlManager.getDBFunction("isnull",  new String[] {"IMPCANO", "0" });
          selectImportoOffertaCongiunta = ", "+ importoOffertaCongiunta;
        }

        String selectDITG = "select dittao, staggi, congruo, ribauo"+selectImportoOffertaCongiunta+" from ditg "
				+ "where ngara5 = ? " + "and staggi > 1 " + "and staggi <> 4 ";

        if (hMapParametri.get("garaInversa") != null && "1".equals(hMapParametri.get("garaInversa"))){
          //si devono escludere le ditte con amminversa='2'
          selectDITG +=" and (amminversa != 2 or amminversa is null)";
        }

        if(hMapGARE.get("ultdetlic") != null && !"1".equals(costofisso)){
          selectDITG += " order by " + importoOffertaCongiunta + ", staggi";
        }else if (modlicg == 6 || modlicg == 17) {
            selectDITG += " order by ribauo desc, staggi";
        } else {
            selectDITG += " order by ribauo, staggi";
        }

		try {
			List datiDITG = this.sqlManager.getListVector(selectDITG,
					new Object[] { ngara });
			if (datiDITG != null && datiDITG.size() > 0) {
				for (int i = 0; i < datiDITG.size(); i++) {
					dittao = SqlManager.getValueFromVectorParam(
							datiDITG.get(i), 0).stringValue();
					staggi = SqlManager.getValueFromVectorParam(
							datiDITG.get(i), 1).longValue();
					congruo = SqlManager.getValueFromVectorParam(
							datiDITG.get(i), 2).stringValue();
                    if(hMapGARE.get("ultdetlic") != null && !"1".equals(costofisso)){
                      ribauo = SqlManager.getValueFromVectorParam(
                          datiDITG.get(i), 4).doubleValue();
                    } else {
                        ribauo = SqlManager.getValueFromVectorParam(
                            datiDITG.get(i), 3).doubleValue();
                    }

					if(modlicg == 1 || modlicg == 5){
						if("1".equals(congruo)){
							if (numeroSecondeParimerito == 0) {
								numeroSecondeParimerito++;
								ribauoSecondaVincitrice = ribauo;
								secondaAggiudicataria = dittao;
								listaSecondeParimerito = "'" + secondaAggiudicataria + "'";
							} else if (ribauo.equals(ribauoSecondaVincitrice)) {
								numeroSecondeParimerito++;
								listaSecondeParimerito += ",'" + dittao + "'";
							}
						}
					} else if(modlicg == 13 || modlicg == 14 ){
						// Controllare il valore di GARE.MODLICG = 13 o 14 equivale a
						// controllare che GARE.CALCSOANG sia uguale a 1

                        if(modastg == 1 && isCalcoloSogliaAnomaliaApplicabile && isEsclusioneAutomaticaApplicabile){
                          // Gara o lotto di gara con GARE.CALCSOANG = 1 e nella condizione
                          // di applicabilita' del calcolo soglia anomalia e di applicabilita'
                          // dell'esclusione automatica
                          // Considera la prima ditta Congrua e Non anomala
                          if("1".equals(congruo) && new Long(6).equals(staggi)){
                            if (numeroSecondeParimerito == 0) {
                              numeroSecondeParimerito++;
                              ribauoSecondaVincitrice = ribauo;
                              secondaAggiudicataria = dittao;
                              listaSecondeParimerito = "'" + secondaAggiudicataria + "'";
                            } else if (ribauo.equals(ribauoSecondaVincitrice)) {
                              numeroSecondeParimerito++;
                              listaSecondeParimerito += ",'" + dittao + "'";
                            }
                          }
                        } else {
                          // Gara o lotto di gara con GARE.CALCSOANG = 1 e nella condizione
                          // di applicabilita' del calcolo soglia anomalia e di non applicabilita'
                          // dell'esclusione automatica
                          // oppure di non applicabilità del calcolo soglia anomalia
                          // oppure è previsto da gara di non fare mai l'esclusione automatica
                          // Considera la prima ditta Congrua
                          if("1".equals(congruo)){
                            if (numeroSecondeParimerito == 0) {
                              numeroSecondeParimerito++;
                              ribauoSecondaVincitrice = ribauo;
                              secondaAggiudicataria = dittao;
                              listaSecondeParimerito = "'" + secondaAggiudicataria + "'";
                            } else if (ribauo.equals(ribauoSecondaVincitrice)) {
                                numeroSecondeParimerito++;
                                listaSecondeParimerito += ",'" + dittao + "'";
                            }
                          }
                        }
					} else if(modlicg == 6){
						// Gara o lotto di gara OEPV
						// Se GARE.CALCSOANG = 1, allora la seconda ditta aggiudicataria e'
						// la seconda ditta congrua (anche se anomala) con punteggio piu' alto.
						// Se GARE.CALCSOANG = 2, allora la seconda ditta aggiudicataria e'
						// la seconda ditta congrua con punteggio piu' alto.
						// A prescindere dal valore di GARE.CALCSOANG il codice e' lo stesso
						if("1".equals(congruo)){
							if (numeroSecondeParimerito == 0) {
								numeroSecondeParimerito++;
								ribauoSecondaVincitrice = ribauo;
								secondaAggiudicataria = dittao;
								listaSecondeParimerito = "'" + secondaAggiudicataria + "'";
							} else if (ribauo!=null && ribauo.equals(ribauoSecondaVincitrice)) {
								numeroSecondeParimerito++;
								listaSecondeParimerito += ",'" + dittao + "'";
							}
						}
					}
				}
			}

			// Se esiste una sola ditta seconda aggiudicataria allora si procede a
			// settare lo stato a seconda ditta vincitrice (STAGGI = 5)
			if (numeroSecondeParimerito == 1) {
				this.aggiornaStatoAggiudicazione(ngara, secondaAggiudicataria,
						new Long(5));
				hMapParametri.put("numeroSecondeParimerito", new Long(1));
				hMapParametri.put("listaSecondeParimerito",
						listaSecondeParimerito);
				hMapParametri.put("secondaAggiudicatariaSelezionata",
						secondaAggiudicataria);
			} else {
				hMapParametri.put("numeroSecondeParimerito", new Long(
						numeroSecondeParimerito));
				hMapParametri.put("listaSecondeParimerito",
						listaSecondeParimerito);
				hMapParametri.put("secondaAggiudicatariaSelezionata", null);
			}

		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nel calcolo della seconda aggiudicataria",
					"settaSecondaAggiudicataria", e);
		}

		if (logger.isDebugEnabled())
			logger.debug("settaSecondaAggiudicataria(" + ngara + "): fine metodo");
	}

	/**
	 * Calcolo dell'importo di aggiudicazione, del ribasso e dell'importo di
	 * garanzia per la ditta prima vincitrice
	 *
	 * @param ngara
	 * @param hMapTORN
	 * @param hMapGARE
	 * @param hMapParametri
	 * @throws GestoreException
	 */
	public void calcolaImportoAggiudicazione(String ngara, HashMap hMapTORN,
			HashMap hMapGARE, HashMap hMapParametri) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("calcolaImportiAggiudicazioneGaranzia(" + ngara
					+ "): inizio metodo");

		int modlicg = 0;
		if (hMapGARE.get("modlicg") != null)
			modlicg = ((Long) hMapGARE.get("modlicg")).intValue();

		double impapp = 0;
		if (hMapGARE.get("impapp") != null)
			impapp = ((Double) hMapGARE.get("impapp")).doubleValue();

		double impsic = 0;
		if (hMapGARE.get("impsic") != null)
			impsic = ((Double) hMapGARE.get("impsic")).doubleValue();

		String sicinc = null;
		if (hMapGARE.get("sicinc") != null)
			sicinc = (String) hMapGARE.get("sicinc");

		double onprge = 0;
		if (hMapGARE.get("onprge") != null)
			onprge = ((Double) hMapGARE.get("onprge")).doubleValue();

		double impnrl = 0;
		if (hMapGARE.get("impnrl") != null)
			impnrl = ((Double) hMapGARE.get("impnrl")).doubleValue();

		String onsogrib = "";
        if (hMapGARE.get("onsogrib") != null){
          //onsogrib = (String) hMapGARE.get("onsogrib");
          if(hMapGARE.get("onsogrib") instanceof Character)
            onsogrib = ((Character) hMapGARE.get("onsogrib")).toString();
          else
            onsogrib = (String) hMapGARE.get("onsogrib");
        }

        int detlicg =0;
        if (hMapGARE.get("detlicg") != null)
          detlicg = ((Long) hMapGARE.get("detlicg")).intValue();

        String costofisso = (String)hMapGARE.get("costofisso");

		double impoff = 0;
		double iaggiu = 0;
		double ribauo = 0;
		double impcal = 0;
		double impperm = 0;
		double impcano = 0;
		double riboepv = 0;
		String codgar = null;

		String selectDITG = "select ribauo, impoff, impperm, impcano, riboepv, codgar5 from ditg where ngara5 = ? and dittao = ?";
		try {
			List datiDITG = this.sqlManager.getListVector(selectDITG,
					new Object[] {ngara, hMapParametri.get("primaAggiudicatariaSelezionata") });
			if (datiDITG != null && datiDITG.size() > 0) {
				if (SqlManager.getValueFromVectorParam(
						datiDITG.get(0), 0).doubleValue() != null) {
					ribauo = SqlManager.getValueFromVectorParam(
							datiDITG.get(0), 0).doubleValue().doubleValue();
					hMapParametri.put("ribauo", new Double(ribauo));
				}
				if (SqlManager.getValueFromVectorParam(
						datiDITG.get(0), 1).doubleValue() != null) {
					impoff = SqlManager.getValueFromVectorParam(
							datiDITG.get(0), 1).doubleValue().doubleValue();
					hMapParametri.put("impoff", new Double(impoff));
				}
				if (SqlManager.getValueFromVectorParam(
                    datiDITG.get(0), 2).doubleValue() != null) {
				  impperm = SqlManager.getValueFromVectorParam(
                            datiDITG.get(0), 2).doubleValue().doubleValue();
                }
				if (SqlManager.getValueFromVectorParam(
                    datiDITG.get(0), 3).doubleValue() != null) {
				  impcano = SqlManager.getValueFromVectorParam(
                            datiDITG.get(0), 3).doubleValue().doubleValue();
                }
                if (SqlManager.getValueFromVectorParam(datiDITG.get(0), 4).doubleValue() != null) {
                  riboepv = SqlManager.getValueFromVectorParam(datiDITG.get(0), 4).doubleValue().doubleValue();
                  hMapParametri.put("riboepv", new Double(riboepv));
                }
                if (SqlManager.getValueFromVectorParam(datiDITG.get(0), 5).stringValue() != null) {
                  codgar = SqlManager.getValueFromVectorParam(datiDITG.get(0), 5).stringValue();
                }
			}
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nel calcolo degli importi di aggiudicazione e di garanzia",
					"calcolaImportiAggiudicazioneGaranzia", e);
		}

        try {
          int oepvCalcolo=1;
          //oepvCalcolo=1   importo
          //oepvCalcolo=2   ribasso
          //oepvCalcolo=3   nè importo nè ribasso - costofisso
          if (modlicg==6){
            if("1".equals(costofisso)){
              oepvCalcolo=3;
            }else if (modlicg==6 && !controlliOepvManager.isVecchiaOepvFromNgara(ngara)){
              if(controlliOepvManager.checkFormato(ngara, new Long(51))){
                oepvCalcolo=2;
                ribauo = riboepv;
              }else if(!controlliOepvManager.checkFormato(ngara, new Long(50)) && !controlliOepvManager.checkFormato(ngara, new Long(52))){
                oepvCalcolo=3;
              }
            }else if (modlicg==6 && controlliOepvManager.isVecchiaOepvFromNgara(ngara)){
              if(riboepv != 0){
                oepvCalcolo=2;
                ribauo=riboepv;
              }
            }
          }
          if(modlicg==6 && oepvCalcolo==3){
    		  iaggiu = UtilityMath.round(impapp, 2);
          }else if(modlicg==5 || modlicg==14 || modlicg== 16 || (modlicg==6 && oepvCalcolo==1) || ((modlicg==1 || modlicg == 13 || modlicg == 17)&& detlicg==4)){
              if(sicinc != null && "2".equals(sicinc))
                impoff += impsic;
              iaggiu = UtilityMath.round(impoff, 2);
              if(modlicg==6){
            	 riboepv = this.calcolaRIBAUO(impapp, onprge, impsic, impnrl, sicinc,impoff,onsogrib);
                 String cifreRibasso=pgManagerEst1.getNumeroDecimaliRibasso(codgar);
                 if(cifreRibasso!=null && !"".equals(cifreRibasso)){
                   riboepv = (Double)UtilityNumeri.arrotondaNumero(riboepv, new Integer(cifreRibasso));
                 }
              }
          }else if(modlicg==1 || modlicg== 13 || modlicg == 17 || (modlicg==6 && oepvCalcolo==2)){
    		  if("1".equals(onsogrib))
                impcal = (impapp - impsic - impnrl) * (1 + ribauo / 100)
                  + impsic + impnrl;
              else
                impcal = (impapp - onprge - impsic - impnrl) * (1 + ribauo / 100)
                  + onprge + impsic + impnrl;
    		  iaggiu = UtilityMath.round(impcal, 2);
    		  if(modlicg==6){
    			  riboepv = ribauo;
    		  }
          }
        }catch (SQLException e) {
          throw new GestoreException(
              "Errore nel calcolo degli importi di aggiudicazione e di garanzia",
              "calcolaImportiAggiudicazioneGaranzia", e);
        }
		if(hMapGARE.get("ultdetlic")!=null && !"1".equals(costofisso)){
		  iaggiu =  iaggiu -impperm + impcano;
		  iaggiu = UtilityMath.round(iaggiu, 2);
		}
		hMapParametri.put("iaggiu", new Double(iaggiu));
		if(modlicg==6){
			hMapParametri.put("riboepv", new Double(riboepv));
		}

		if (logger.isDebugEnabled())
			logger.debug("calcolaImportiAggiudicazioneGaranzia(" + ngara
					+ "): fine metodo");

	}

	/**
	 * Funzione che calcola l'importo a Garanzia secondo Art. 30 comma 2 della
	 * legge Merloni
	 *
	 * @param iaggiu
	 * @param ribauo
	 * @param tipgen
	 * @param ridiso
	 * @return
	 * @throws GestoreException
	 */
	public double calcolaImportoGaranzia(double iaggiu, double ribauo,
			long tipgen, boolean ridiso) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("calcolaImportoGaranzia: inizio metodo");
		double importoGaranzia = 0;

		if (iaggiu > 0) {
			double percauz = calcoloPercentualeCauzione(ribauo);
			importoGaranzia = UtilityMath.round((iaggiu * percauz) / 100, 2);
		}

		if (ridiso)
			importoGaranzia = UtilityMath.round(importoGaranzia / 2, 2);

		if (logger.isDebugEnabled())
			logger.debug("calcolaImportoGaranzia: fine metodo");

		return importoGaranzia;
	}

	/**
	 *
	 * @param ribauo
	 * @return
	 */
	public static double calcoloPercentualeCauzione(double ribauo) {
		double percauz = 10;
		double ribasso = 0;
		if(ribauo < 0){
    		ribasso = UtilityMath.round(Math.abs(ribauo), 9);
    		ribasso = ribasso - 10;
    		if (ribasso > 0)
    			percauz += ribasso;
    		ribasso = ribasso - 10;
    		if (ribasso > 0)
    			percauz += ribasso;
		}
		return percauz;
	}

	/**
	 * Funzione per sbiancare il campo MOTIES della ditta vincitrice. Richiamata
	 * dalla fase di gara "Verifica Congruità"
	 *
	 * @param codiceGara
	 * @param numeroGara
	 * @throws GestoreException
	 */
	public void sbiancaMotiesDittaVincitrice(String codiceGara,
			String numeroGara) throws GestoreException {

		if (logger.isDebugEnabled())
			logger.debug("sbiancaMotiesDittaVincitrice(" + codiceGara + ","
					+ numeroGara + "): inizio metodo");

		// Inizializzazione delle varie HashMap utilizzate
		HashMap hMapTORN = new HashMap();
		HashMap hMapGARE = new HashMap();
		HashMap hMapParametri = new HashMap();

		// Inizializzazione valori letti da TORN
		this.inizializzaTORN(codiceGara, hMapTORN);

		this.esclusioneEventualeVincitriceFaseA(codiceGara, numeroGara,
				hMapTORN, hMapGARE, hMapParametri);

		if (logger.isDebugEnabled())
			logger.debug("sbiancaMotiesDittaVincitrice(" + codiceGara + ","
					+ numeroGara + "): fine metodo");
	}



	/**
	 * Metodo per il calcolo di RIBAUO.
	 *
	 * @param impapp
	 * @param onprge
	 * @param impsic
	 * @param impnrl
	 * @param sicinc
	 * @param impoff
	 * @return ribauo
	 */
	public double calcolaRIBAUO(double impapp, double onprge, double impsic,
			double impnrl, String sicinc, double impoff,String onsogrib) {
		double ribauo = 0;

		double den = 0;
		//den = impapp - impsic - onprge - impnrl;
		den = impapp - impsic - impnrl;
		if(!"1".equals(onsogrib))
		  den -= onprge;

		if (den <= 0) {
			ribauo = 0;
		} else {
			ribauo = impoff - impapp;
			if (sicinc != null && "2".equals(sicinc))
				ribauo += impsic;
			ribauo = ribauo * 100 / den;
		}
		return ribauo;
	}

	/**
     * Inizializza il valore dei campi GARE.NSORTE, GARE.ALAINF, GARE.ALASUP
     *
     * @param ngara
     * @param hMapGARE
     * @param nsorte
     * @param alainf
     * @param alasup
     * @throws GestoreException
     */
    private void inizializzaSorteggio(String ngara, HashMap hMapGARE, Long nsorte, Double alainf, Double alasup)
            throws GestoreException {

        if (logger.isDebugEnabled())
            logger.debug("inizializzaSorteggio(" + ngara + "): inizio metodo");

        if (nsorte == null)
          nsorte = new Long(0);
        hMapGARE.put("nsorte", nsorte);

        if (alainf == null)
          alainf = new Double(0);
        hMapGARE.put("alainf", alainf);

        if (alasup == null)
          alasup = new Double(0);
        hMapGARE.put("alasup", alasup);

        if (logger.isDebugEnabled())
            logger.debug("inizializzaSorteggio(" + ngara + "): fine metodo");
    }


    /**
     * Vengono eseguite le inizializzazzioni necessarie per l'aggiudicazione provvisoria.
     * Inoltre se vi sono le condizioni per l'esclusione automatica viene restituito true.
     *
     * @param ngara
     * @param codgar
     * @param precut
     * @param limmax
     * @param nofval
     * @param hMapGARE
     * @param hMapTORN
     * @param hMapParametri
     *
     * @return true, se vi è l'esclusione automatica
     *         false, altrimenti
     *
     * @throws GestoreException
     */
    public Boolean initControlliAggiudicazione(String ngara, String codgar,Long precut, String legRegSic, Double limmax, Long nofval, HashMap hMapGARE,
        HashMap hMapTORN, HashMap hMapParametri) throws GestoreException{
      Boolean ret=null;

      // Inizializzazione valori letti da TORN
      this.inizializzaTORN(codgar, hMapTORN);

      // Inizializzazione valori letti da GARE
      this.inizializzaGARE(ngara, hMapGARE);

      // Inizializzazione valori letti da GARE1 estensesione di GARE
      this.inizializzaGareDaGare1(ngara, hMapGARE);

      // Inizializza nella HashMap hMapGARE il valore del campo LEGREGSIC
      this.inizializzaLEGREGSIC(ngara, hMapGARE, legRegSic);

      // Inizializza nella HashMap hMapGARE il valore del campo PRECUT
      this.inizializzaPRECUT(ngara, hMapGARE, precut);

      // Inizializza nella HashMap hMapParametri il correttivo di gara
      this.inizializzaCORGAR(ngara, hMapTORN, hMapGARE, hMapParametri);

      // Inizializza nella HashMap hMapParametri la tipologia della gara
      this.inizializzaTipologiaGara(codgar, hMapParametri);

      // Iniziazizza nella HashMap hMapParametri la data Pubblicazione su portale
      this.inizializzaDataPubblicazionePortale(codgar, hMapParametri);

      // Inizializza la varibile per stabilire si tratta della normativa DLgs.50/2016
      this.inizializzaNormativa(hMapTORN, hMapGARE, hMapParametri);

      this.inizializzaGestionePrezzo(hMapParametri,ngara);

      // Inizializza nella HashMap hMapParametri i punteggi
      // massimi tecnico ed economico per le gare di tipo
      // "Offerta economicamente più vantaggiosa"
      this.inizializzaPunteggiMassimi(ngara, hMapGARE, hMapParametri);

      hMapParametri.put("garaInversa", hMapTORN.get("inversa"));


      // Controllo se esistono ditte dopo il calcolo della soglia di anomalia
      if (this.esistonoDitte(ngara, "staggi > 1")) {
          hMapParametri.put("limiteAnomalia", limmax);
          hMapParametri.put("numeroDitteAmmesse", nofval);
          hMapParametri.put("numeroDitte", this.conteggioDitte(ngara, "staggi > 1"));
          this.inizializzaMinimoPerCorrettivo(ngara,codgar, hMapTORN, hMapGARE,
                  hMapParametri);

          // Inizializza nella HashMap hMapParametri i flag booleani per applicabilita'
          // del calcolo soglia anomalia e per applicabilita' esclusione automatica
          // (entrambi funzione di n.offerte valide, gara per lavori/fornitoure/servizi
          // e importo della gara)
          this.inizializzaFlags(hMapTORN, hMapGARE, hMapParametri);
      }

      if (hMapParametri.get("isEsclusioneAutomaticaApplicabile") != null)
        ret = ((Boolean) hMapParametri.get("isEsclusioneAutomaticaApplicabile"));

      return ret;
    }

    /**
     * Controlla se la data di pubblicazione è successiva alla data che indica la nuova modalità
     * di calcolo della soglia di anomalia DLgs.50/2016.
     * Viene controllata la data di pubblicazione per stabilire la modalità di calcolo della soglia
     * di anomalia, considerando le date stabilite per legge per discriminare la modalità
     *
     * @param iterga
     * @param dinvit
     * @param dpubavg
     * @param datpub
     *
     * @return int
     *         0 per DLgs.163/2006
     *         1 per DLgs.50/2016
     *         2 per DLgs.56/2017
     *         3 per DL 32/2019
     */
    public int getLeggeCalcoloSoglia(Long iterga, Date dinvit, Date dpubavg,Date datpub ){
      int ret=this.DLgs_163_2006;
      Date dataControllo = null;
      if (iterga == 3 || iterga == 4 || iterga == 5 || iterga == 6)
        dataControllo = dinvit;
      else{
        if(datpub!=null)
          dataControllo = datpub;
        else
          dataControllo = dpubavg;
      }

      if(dataControllo == null)
        ret = this.DL_32_2019;
      else{
        dataControllo = UtilityDate.convertiData(UtilityDate.convertiData(dataControllo, UtilityDate.FORMATO_GG_MM_AAAA), UtilityDate.FORMATO_GG_MM_AAAA);
        boolean usateDateDb=true;
        boolean trovataNormativa =false;
        try {
          List listavalori = this.sqlManager.getListVector("select tab2d1, tab2d2 from tab2 where tab2cod=? order by tab2nord,tab2tip", new Object[]{"A1z10"});
          if(listavalori!=null && listavalori.size()>0){
            //Si assume che se presenti i valori di tab2d1 per il tabellato A1z10 assumano i valori: DLGS50_2016, DLGS56_2017 e DL32_2019
            String normativaDaTabInizio = null;
            String normativaDaTabFine = null;
            String dataDaTab = null;
            Date dataInizio = null;
            Date dataFine = null;
            for(int i=0; i < listavalori.size(); i++){
              if(i==0)
                normativaDaTabInizio = SqlManager.getValueFromVectorParam(listavalori.get(i), 0).stringValue();
              else
                normativaDaTabInizio = normativaDaTabFine;
              if(this.controlloStringaNormativa(normativaDaTabInizio)){
                if(i==0)
                  dataDaTab=SqlManager.getValueFromVectorParam(listavalori.get(i), 1).stringValue();
                if((dataDaTab!=null && !"".equals(dataDaTab) && i==0) || dataFine!=null){
                  if(i==0)
                    dataInizio = UtilityDate.convertiData(dataDaTab, UtilityDate.FORMATO_GG_MM_AAAA);
                  else
                    dataInizio = dataFine;
                  if(i < listavalori.size() - 1){
                    normativaDaTabFine = SqlManager.getValueFromVectorParam(listavalori.get(i + 1), 0).stringValue();
                    if( this.controlloStringaNormativa(normativaDaTabFine)){
                      dataDaTab=SqlManager.getValueFromVectorParam(listavalori.get(i + 1), 1).stringValue();
                      if(dataDaTab!=null && !"".equals(dataDaTab)){
                        dataFine = UtilityDate.convertiData(dataDaTab, UtilityDate.FORMATO_GG_MM_AAAA);
                        if((dataControllo.after(dataInizio) && dataControllo.before(dataFine)) || dataControllo.equals(dataInizio)){
                          ret=this.codificaStringaInNormativa(normativaDaTabInizio);
                          trovataNormativa=true;
                          break;
                        }
                      }else{
                        //Campo TAB5DESC vuoto
                        usateDateDb=false;
                        break;
                      }
                    }else{
                      //Codice nel campo TAB5TIP non presenta un valore valido
                      usateDateDb=false;
                      break;
                    }
                  }else{
                    //dataFine = null;
                    //Non è stato specificata una data di inizio di una normativa successiva nel tabellato
                    if(dataControllo.after(dataInizio) || dataControllo.equals(dataInizio)){
                      ret=this.codificaStringaInNormativa(normativaDaTabInizio);
                      trovataNormativa=true;
                      break;
                    }
                  }

                }else{
                  //Campo TAB5DESC vuoto
                  usateDateDb=false;
                  break;
                }
              }else{
                //Codice nel campo TAB5TIP non presenta un valore valido
                usateDateDb=false;
                break;
              }
            }
          }else{
            //Non ci sono occorrenze nel db
            usateDateDb=false;
          }

        } catch (Exception e) {
          //Se ci sono errori nella lettura del tabellato non si deve bloccare l'esecuzione
          usateDateDb=false;
        }


        if(!usateDateDb || !trovataNormativa){
          if((dataControllo.after(this.DATA_DLgs_50_2016) && dataControllo.before(this.DATA_DLgs_56_2017)) || dataControllo.equals(this.DATA_DLgs_50_2016))
            ret = this.DLgs_50_2016;
          else if((dataControllo.after(this.DATA_DLgs_56_2017) && dataControllo.before(DATA_DL_32_2019)) || dataControllo.equals(this.DATA_DLgs_56_2017))
            ret = this.DLgs_56_2017;
          else if(dataControllo.after(this.DATA_DL_32_2019) || dataControllo.equals(this.DATA_DL_32_2019))
            ret = this.DL_32_2019;
        }
      }

      return ret;
    }


    /**
     * Verifica che il valore passato come parametro corrisponda ad una delle costanti:
     * DLGS50_2016, DLGS56_2017 e DL32_2019
     *
     * @param valore
     * @return boolean
     */
    private boolean controlloStringaNormativa(String valore){
      boolean esito=false;
      if(this.stringa_DLgs_50_2016.equals(valore) || this.stringa_DLgs_56_2017.equals(valore) || this.stringa_DL_32_2019.equals(valore))
        esito=true;
      return esito;
    }

    /**
     * Se il valore passato come argomento corrisponde ad una delle costanti DLGS50_2016, DLGS56_2017 e DL32_2019,
     * viene restituito il corrispondente valore della normativa
     *
     * @param valore
     * @return int
     */
    private int codificaStringaInNormativa(String valore){
      int ret=-1;
      if(this.stringa_DLgs_50_2016.equals(valore))
        ret=this.DLgs_50_2016;
      else if(this.stringa_DLgs_56_2017.equals(valore))
        ret=this.DLgs_56_2017;
      else if(this.stringa_DL_32_2019.equals(valore))
        ret=this.DL_32_2019;

      return ret;
    }


    /**
     * Viene eseguito il calcolo della soglia di anomalia sia per vecchia che nuova modalità.
     *
     * @param ngara
     * @param codgar
     * @param legRegSic
     * @param hMapGARE
     * @param hMapTORN
     * @param hMapParametri
     *
     *
     * @throws GestoreException
     */
    private void gestioneCalcoloSogliaAnomalia(String ngara, String codgar, String legRegSic, HashMap hMapGARE,
        HashMap hMapTORN, HashMap hMapParametri) throws GestoreException{

      if (logger.isDebugEnabled())
        logger.debug("gestioneCalcoloSogliaAnomalia(" + ngara + "): inizio metodo");

      boolean modalitaDL2016 = false;
      boolean modalitaDL2017 = false;
      boolean modalitaDL2019 = false;
      boolean modalitaDLCalcoloGraduatoria=false;

      int modlicg = 0;
      String filtroStaggiAli=",2,7";
      if (hMapGARE.get("modlicg") != null){
         modlicg = ((Long) hMapGARE.get("modlicg")).intValue();
        if(modlicg==13 || modlicg==14){
          if (hMapParametri.get("modalitaDL2016") != null){
           modalitaDL2016 = ((Boolean) hMapParametri.get("modalitaDL2016")).booleanValue();
          }
          if (hMapParametri.get("modalitaDL2017") != null){
            modalitaDL2017 = ((Boolean) hMapParametri.get("modalitaDL2017")).booleanValue();
           }
          if (hMapParametri.get("modalitaDL2019") != null){
            modalitaDL2019 = ((Boolean) hMapParametri.get("modalitaDL2019")).booleanValue();
           }
          if (hMapParametri.get("modalitaDLCalcoloGraduatoria") != null){
            modalitaDLCalcoloGraduatoria = ((Boolean) hMapParametri.get("modalitaDLCalcoloGraduatoria")).booleanValue();
           }
        }
      }

      if("1".equals(legRegSic) && !modalitaDLCalcoloGraduatoria){
        //Regione Sicilia, modalità dal 31/09/2019
        this.aggiornaImplGAREannullaCalcolo(ngara, codgar, null, false, true);

        //esclusione ali (10% per normativa DLGS2016 e precedente, tabellato A2065 per DLGS2017): stesso criterio della normativa nazionale
        this.calcoloSogliaAnomalia(ngara, codgar, hMapTORN, hMapGARE, hMapParametri);

        //calcolo media ribassi dopo esclusione ali: stesso criterio della normativa nazionale
        this.calcolaMedia(ngara, hMapTORN, hMapGARE, hMapParametri, true);


        this.calcoloSogliaRegSicilia(ngara, hMapTORN, hMapGARE, hMapParametri);
      }else if((!modalitaDL2016 && !modalitaDL2017 && !modalitaDL2019) || modalitaDLCalcoloGraduatoria){
        //Gestione vecchia modalità
        this.calcoloSogliaAnomalia(ngara, codgar, hMapTORN, hMapGARE, hMapParametri);
        this.calcolaMedia(ngara, hMapTORN, hMapGARE, hMapParametri, true);
        this.calcoloMediaCorretta(ngara, hMapTORN, hMapGARE, hMapParametri);

      }else if(modalitaDL2016 || modalitaDL2017){
        //Per le gare pubblicate dopo il 19/04/2016 il calcolo della soglia di anomalia prevede 5 algoritmi differenti
        //in base al valore del parametro metodoCalcoloSogliaAnomalia

        this.aggiornaImplGAREannullaCalcolo(ngara, codgar, null, false, true);

        int metodo = ((Long) hMapParametri.get("metodoCalcoloSogliaAnomalia")).intValue();
        switch (metodo) {
          case 1:
            //esclusione ali (10% per normativa DLGS2016 e precedente, tabellato A2065 per DLGS2017): stesso criterio della normativa precedente
            this.calcoloSogliaAnomalia(ngara, codgar, hMapTORN, hMapGARE, hMapParametri);
            //calcolo media ribassi dopo esclusione ali: stesso criterio della normativa precedente
            this.calcolaMedia(ngara, hMapTORN, hMapGARE, hMapParametri, true);
            //calcolo media scarti dei ribassi superiori alla media dopo esclusione ali e calcolo limiteAnomalia: stesso criterio della normativa precedente
            this.calcoloMediaCorretta(ngara, hMapTORN, hMapGARE, hMapParametri);
            break;
          case 2:
            //esclusione ali (10% per normativa DLGS2016 e precedente, tabellato A2065 per DLGS2017): stesso criterio della normativa precedente
            this.calcoloSogliaAnomalia(ngara, codgar, hMapTORN, hMapGARE, hMapParametri);
            //calcolo media ribassi dopo esclusione ali: stesso criterio della normativa precedente
            this.calcolaMedia(ngara, hMapTORN, hMapGARE, hMapParametri, true);
            //calcolo somma ribassi delle ditte ammesse in gara
            this.calcolaSommaRibassi(ngara, hMapGARE, hMapParametri);
            break;
          case 3:
            //calcolo media ribassi: stesso criterio della normativa precedente ma senza considerare il filtro su staggi
            //Poichè non c'è l'esclusione delle ali, numDitte deve coincidere con numeroDitteAmmesse
            hMapParametri.put("numeroDitte", hMapParametri.get("numeroDitteAmmesse"));
            this.calcolaMedia(ngara, hMapTORN, hMapGARE, hMapParametri, false);
            long numeroDitte = 0;
            if (hMapParametri.get("numeroDitte") != null)
                numeroDitte = ((Long) hMapParametri.get("numeroDitte")).longValue();

            //soglia anomalia = media ribassi + 20% (15% nel caso di DLGS2017)
            int percentuale= 20;
            if(modalitaDL2017)
              percentuale= 15;
            this.calcolaSogliaAnomalia(percentuale, hMapParametri, hMapGARE, numeroDitte);

            break;
          case 4:
            if(modalitaDL2016){
              //Poichè non c'è l'esclusione delle ali, numDitte deve coincidere con numeroDitteAmmesse
              hMapParametri.put("numeroDitte", hMapParametri.get("numeroDitteAmmesse"));
              //Calcolo soglia anomali a partire dai ribassi assoluti delle ditte
              this.calcolaSogliaDaRibassiAssoluti(ngara, codgar, hMapGARE, hMapParametri);
            }else{
              //Stesso calcolo del caso 3 per DLGS2016
              hMapParametri.put("numeroDitte", hMapParametri.get("numeroDitteAmmesse"));
              this.calcolaMedia(ngara, hMapTORN, hMapGARE, hMapParametri, false);
              numeroDitte = 0;
              if (hMapParametri.get("numeroDitte") != null)
                  numeroDitte = ((Long) hMapParametri.get("numeroDitte")).longValue();

            //soglia anomalia = media ribassi + 10%
              this.calcolaSogliaAnomalia(10, hMapParametri, hMapGARE, numeroDitte);
            }
            break;
          case 5:
            //esclusione ali 10%: stesso criterio della normativa precedente
            this.calcoloSogliaAnomalia(ngara, codgar, hMapTORN, hMapGARE, hMapParametri);
            //calcolo media ribassi dopo esclusione ali: stesso criterio della normativa precedente
            this.calcolaMedia(ngara, hMapTORN, hMapGARE, hMapParametri, true);
            //calcolo media scarti dei ribassi superiori alla media dopo esclusione ali:  stesso criterio della normativa precedente,
            //calcolo limiteAnomalia: stesso criterio della normativa precedente poi moltiplicato per il coefficente
            this.calcoloMediaCorretta(ngara, hMapTORN, hMapGARE, hMapParametri);
            break;
        }
      }else if(modalitaDL2019){
        this.aggiornaImplGAREannullaCalcolo(ngara, codgar, null, false, true);

        //esclusione ali (10% per normativa DLGS2016 e precedente, tabellato A2065 per DLGS2017): stesso criterio della normativa precedente
        this.calcoloSogliaAnomalia(ngara, codgar, hMapTORN, hMapGARE, hMapParametri);

        //calcolo media ribassi dopo esclusione ali: stesso criterio della normativa precedente
        this.calcolaMedia(ngara, hMapTORN, hMapGARE, hMapParametri, true);

        //calcolo media scarti dei ribassi superiori alla media dopo esclusione ali: stesso criterio della normativa precedente
        this.calcoloMediaCorretta(ngara, hMapTORN, hMapGARE, hMapParametri);

        //calcolo limite anomalia: nuovo metodo
        this.calcoloSogliaAnomaliaDL2019(ngara, hMapTORN, hMapGARE, hMapParametri);
      }

      if (logger.isDebugEnabled())
        logger.debug("gestioneCalcoloSogliaAnomalia(" + ngara + "): fine metodo");
    }

    /**
     * Viene eseguito il calcolo della somma dei ribassi da cui si ricava lasoglia di anomalia secondo la nuova modalità,
     * in riferimento al Metodo B (METSOGLIA.GARE1 = 2).
     *
     * @param ngara
     * @param hMapGARE
     * @param hMapParametri
     *
     *
     * @throws GestoreException
     */
    private void calcolaSommaRibassi(String ngara, HashMap hMapGARE, HashMap hMapParametri) throws GestoreException{
      if (logger.isDebugEnabled())
        logger.debug("calcolaSommaRibassi(" + ngara + "): inizio metodo");

      double mediaRibasso = 0;
      double limiteAnomalia = 0;
      double sommaRibasso = 0;
      long numeroDitte = 0;

      String descTabA1132 = this.leggiDescrizioneDaTab1("A1132",new Long(1));
      if(descTabA1132==null || "".equals(descTabA1132)){
        String message = "Non trovato il parametro (A1132) per stabilire il criterio con cui fare la somma dei ribassi delle ditte ammesse in gara ";
        throw new GestoreException(message,"inizializzaParametri.A1132");
      }else{
        descTabA1132 = descTabA1132.substring(0, 1);
      }

      if (hMapParametri.get("numeroDitte") != null)
          numeroDitte = ((Long) hMapParametri.get("numeroDitte")).longValue();

      if (numeroDitte > 0) {
        int precut = 0;
        if (hMapGARE.get("precut") != null)
            precut = ((Long) hMapGARE.get("precut")).intValue();

        if (hMapParametri.get("mediaRibasso") != null)
            mediaRibasso = ((Double) hMapParametri.get("mediaRibasso")).doubleValue();

        String selectTotaleRibauo = "select sum(ribauo) from ditg where ammgar<>'2' and "
            + " ngara5 = ? and staggi <> 1";
        if("2".equals(descTabA1132))
          selectTotaleRibauo = "select sum(ribauo) from ditg where ammgar<>'2' and "
              + " ngara5 = ? and staggi not in (1,2,7)";
        try{
          Object sum = this.sqlManager.getObject(selectTotaleRibauo, new Object[] { ngara });
          if(sum instanceof Double)
            sommaRibasso = new Double((Double)sum).doubleValue();
          else
            sommaRibasso = new Double(((Long) sum)).doubleValue();

          sommaRibasso=this.valoreArrotondatoTroncato(sommaRibasso, precut, hMapParametri);
          double somma = 0;
          if(sommaRibasso < 0 ){
            somma = - sommaRibasso;
          }else{
            somma = sommaRibasso;
          }
          int primaCifra = (int)(somma*10 - (Math.floor(somma)*10));
          int verificaParita = primaCifra & 1;
          double cento = 100.00;
          if(verificaParita == 0 || primaCifra==0) {
            //pari
            limiteAnomalia = mediaRibasso;
          }else{
            //dispari
            limiteAnomalia = mediaRibasso * (1 - primaCifra / cento);
          }

          limiteAnomalia = this.valoreArrotondatoTroncato(limiteAnomalia, precut, hMapParametri);


        }catch (SQLException e) {
          throw new GestoreException("Errore nel calcolo della soglia di anomalia con la modalità DL2016",
              "aggiudicazioneFaseA", e);
        }
      }

      hMapParametri.put("limiteAnomalia", new Double(limiteAnomalia));
      hMapParametri.put("sommaRib", new Double(sommaRibasso));

      if (logger.isDebugEnabled())
        logger.debug("calcolaSommaRibassi(" + ngara + "): fine metodo");
    }

    /**
     * Viene eseguito il calcolo della soglia di anomalia secondo la nuova modalità,
     * in riferimento al Metodo E (METSOGLIA.GARE1 = 5).
     *
     * @param ngara
     * @param codgar
     * @param hMapGARE
     * @param hMapParametri
     *
     *
     * @throws GestoreException
     */
    private void calcolaSogliaDaRibassiAssoluti(String ngara, String codgar, HashMap hMapGARE, HashMap hMapParametri) throws GestoreException{
      double impapp = 0;
      double impsic = 0;
      double impnrl = 0;
      double onprge = 0;
      String onsogrib= "";
      String sicinc = null;
      double mediaRibassiAssoluti = 0;
      double decrementoMedia = 0;
      double limiteAnomalia = 0;

      if (logger.isDebugEnabled())
        logger.debug("calcolaSogliaDaRibassiAssoluti(" + ngara + "): inizio metodo");

      if (hMapGARE.get("impapp") != null)
          impapp = ((Double) hMapGARE.get("impapp")).doubleValue();
      if (hMapGARE.get("onprge") != null)
          onprge = ((Double) hMapGARE.get("onprge")).doubleValue();
      if (hMapGARE.get("impsic") != null)
          impsic = ((Double) hMapGARE.get("impsic")).doubleValue();
      if (hMapGARE.get("impnrl") != null)
          impnrl = ((Double) hMapGARE.get("impnrl")).doubleValue();
      if (hMapGARE.get("sicinc") != null)
          sicinc = (String) hMapGARE.get("sicinc");

      if (hMapGARE.get("onsogrib") != null){
        if(hMapGARE.get("onsogrib") instanceof Character)
          onsogrib = ((Character) hMapGARE.get("onsogrib")).toString();
        else
          onsogrib =(String)hMapGARE.get("onsogrib");
      }

      double somma = impapp - impsic - impnrl;
      if(!"1".equals(onsogrib))
        somma = somma - onprge;

      double ribassoAssoluto = 0;
      String select = "select ribauo, dittao from ditg where ammgar<>'2' and "
          + " ngara5 = ?";

      long numeroDitteAmmesse = 0;
      if (hMapParametri.get("numeroDitteAmmesse") != null)
          numeroDitteAmmesse = ((Long) hMapParametri.get("numeroDitteAmmesse")).longValue();

      long minimoPerCorrettivo = 0;
      if (hMapParametri.get("minimoPerCorrettivo") != null)
          minimoPerCorrettivo = ((Long) hMapParametri.get("minimoPerCorrettivo")).longValue();

      int precut = 0;
      if (hMapGARE.get("precut") != null)
          precut = ((Long) hMapGARE.get("precut")).intValue();

      try{
        if(numeroDitteAmmesse >= minimoPerCorrettivo){
          List ret = this.sqlManager.getListVector(select, new Object[] { ngara });
          if (ret != null && ret.size() > 0) {
            Double ribauo = null;
            String dittao = null;
            Double ribimp = null;
            double sommaRib = 0;
            int numDitte = 0;
            for (int i = 0; i < ret.size(); i++) {
                ribauo = SqlManager.getValueFromVectorParam(ret.get(i), 0).doubleValue();
                dittao = SqlManager.getValueFromVectorParam(ret.get(i), 1).getStringValue();
                numDitte++;
                ribassoAssoluto = somma * ribauo /100.00;
                ribimp = this.valoreArrotondatoTroncato(ribassoAssoluto, precut, hMapParametri);
                sommaRib += ribimp.doubleValue();
                this.sqlManager.update("update ditg set ribimp = ? where ngara5=? and codgar5=? and dittao=?", new Object[]{ribimp, ngara,codgar, dittao});
            }
            mediaRibassiAssoluti = sommaRib / numDitte;
            mediaRibassiAssoluti = this.valoreArrotondatoTroncato(mediaRibassiAssoluti, precut, hMapParametri);
            decrementoMedia = mediaRibassiAssoluti * (1 - 20/100.00);
            decrementoMedia = this.valoreArrotondatoTroncato(decrementoMedia, precut, hMapParametri);
            if(somma!=0)
              limiteAnomalia = decrementoMedia * 100.00 / somma;

            limiteAnomalia = this.valoreArrotondatoTroncato(limiteAnomalia, precut, hMapParametri);

          }
        }
      }catch (SQLException e) {
        throw new GestoreException("Errore nel calcolo della soglia di anomalia con la modalità DL2016",
            "aggiudicazioneFaseA", e);
      }
      hMapParametri.put("mediaRibassiAssoluti", new Double(mediaRibassiAssoluti));
      hMapParametri.put("decrementoMedia", new Double(decrementoMedia));
      hMapParametri.put("limiteAnomalia", new Double(limiteAnomalia));

      if (logger.isDebugEnabled())
        logger.debug("calcolaSogliaDaRibassiAssoluti(" + ngara + "): fine metodo");
    }

    /**
     * Viene individuata la tipologia della gara
     *
     * @param codgar
     * @param hMap
     * @throws GestoreException
     */
    private void inizializzaTipologiaGara(String codgar, HashMap hMap)
            throws GestoreException {

        if (logger.isDebugEnabled())
            logger.debug("inizializzaTipologiaGara(" + codgar + "): inizio metodo");

        String select = "select genere from v_gare_torn where codgar = ?";

        try {
          Long genere = (Long)this.sqlManager.getObject(select, new Object[]{codgar});
          hMap.put("tipologiaGara", genere);
        } catch (SQLException e) {
            throw new GestoreException(
                    "Errore nella lettura della tipologia della gara",
                    "inizializzaGARE", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("inizializzaTipologiaGara(" + codgar + "): fine metodo");
    }

    private void inizializzaNormativa(HashMap hMapTORN, HashMap hMapGARE, HashMap hMapParametri) throws GestoreException
    {
      int iterga = 0;
      Date dpubavg = null;
      Date dinvit = null;
      Date datpub = null;
      boolean valorizzaMediasca= true;
      boolean modalitaDL2016 = false;
      boolean modalitaDL2017 = false;
      boolean modalitaDL2019 = false;
      boolean modalitaManuale = true;
      int modlicg = 0;
      String legRegSic = "";

      if (hMapGARE.get("modlicg") != null)
         modlicg = ((Long) hMapGARE.get("modlicg")).intValue();

      if (hMapGARE.get("legRegSic") != null)
        legRegSic = ((String) hMapGARE.get("legRegSic")).toString();

      if (hMapGARE.get("dpubavg") != null)
        dpubavg = (Date) hMapGARE.get("dpubavg");
      if (hMapTORN.get("iterga") != null)
        iterga = ((Long) hMapTORN.get("iterga")).intValue();
      if (hMapTORN.get("dinvit") != null)
        dinvit = ((Date) hMapTORN.get("dinvit"));
      if (hMapParametri.get("datpub") != null)
        datpub = ((Date) hMapParametri.get("datpub"));


      if((modlicg==13 || modlicg==14) && !"1".equals(legRegSic)){
        int ret=this.getLeggeCalcoloSoglia(new Long(iterga), dinvit, dpubavg, datpub);
        if(ret==1)
          modalitaDL2016= true;
        else if(ret==2)
          modalitaDL2017= true;
        else if(ret==3)
          modalitaDL2019= true;
      }
      if(modalitaDL2016 || modalitaDL2017){
        String modalita=this.isModalitaManuale();
        if("2".equals(modalita))
          modalitaManuale=false;
      }

      hMapParametri.put("modalitaDL2016", new Boolean(modalitaDL2016));
      hMapParametri.put("modalitaDL2017", new Boolean(modalitaDL2017));
      hMapParametri.put("modalitaDL2019", new Boolean(modalitaDL2019));
      hMapParametri.put("modalitaManuale", new Boolean(modalitaManuale));
    }

    /**
     * Cancellazione delle occorrenze di GARECONT con codimp=null e con codimp relativo a ditte non aggiudicatarie
     * in alcun lotto
     *
     * @param codgar
     * throws SQLException
     * @throws GestoreException
     */
    public void cancellaGarecontOffertaUnica(String codgar) throws SQLException, GestoreException{
      this.sqlManager.update(
          "delete from garecont where ngara=? and codimp is null",
          new Object[] { codgar });

      List listaGarecont = this.sqlManager.getListVector("select codimp from garecont where ngara=?", new Object[]{codgar});
      if(listaGarecont!= null && listaGarecont.size()>0){
        for (int i = 0; i < listaGarecont.size(); i++) {
          String ditta= SqlManager.getValueFromVectorParam(listaGarecont.get(i), 0).stringValue();
          Long numeroLottiAggiudicatiDitta= (Long)this.sqlManager.getObject("select count(ngara) from gare where codgar1=? and ditta=?", new Object[]{codgar,ditta});
          if(numeroLottiAggiudicatiDitta==null || (numeroLottiAggiudicatiDitta!=null && numeroLottiAggiudicatiDitta.longValue()==0)){
            //elimino le figlie di garecont
            Long ncont = (Long) this.sqlManager.getObject("select garecont.ncont from garecont,garattiagg " +
                  " where garecont.ngara = garattiagg.ngara and garecont.ncont = garattiagg.ncont " +
                  " and garecont.ngara = ? and garecont.codimp = ? ",
                  new Object[] { codgar, ditta});

            if(ncont !=  null){
              this.sqlManager.update(
                  "delete from garattiagg where ngara = ? and ncont = ?",
                  new Object[] { codgar, ncont});
            }

            this.sqlManager.update(
                  "delete from garecont where ngara=? and codimp =?",
                  new Object[] { codgar, ditta});
          }
        }
      }
    }

    public void allineamentoGarecontSbiancamentoDitta(String codgar, String ngara, String ditta) throws SQLException, GestoreException{
      Long genere = (Long)this.sqlManager.getObject("select genere from gare where codgar1=? and ngara=codgar1", new Object[]{codgar});
      if(genere==null){
        this.sqlManager.update("update garecont set impqua=null, codimp=null, coorba=null, codbic=null, banapp=null where ngara=? and ncont=?",
            new Object[]{ngara, new Long(1) });
      }else if(genere.longValue()==3){
        Long modcont = null;
        String accqua = null;
        Vector datiTorn = this.sqlManager.getVector("select modcont, accqua from torn where codgar=?", new Object[]{codgar});
        if(datiTorn!=null && datiTorn.size()>0){
          modcont = SqlManager.getValueFromVectorParam(datiTorn, 0).longValue();
          accqua = SqlManager.getValueFromVectorParam(datiTorn, 1).stringValue();
        }
        if((new Long(1)).equals(modcont)){
          this.sqlManager.update("update garecont set impqua=null, codimp=null, coorba=null, codbic=null, banapp=null  where ngara=? and ngaral=?",
              new Object[]{codgar,ngara });
        }else if((new Long(2)).equals(modcont)){
          this.cancellaGarecontOffertaUnica(codgar);
          if(ditta!=null && !"".equals(ditta) && "1".equals(accqua)){
            Double impapp=null;
            Object importo = this.sqlManager.getObject("select sum(impapp) from gare where codgar1=? and ditta=?", new Object[]{codgar, ditta});
            if(importo!=null){
              if (importo instanceof Long)
                impapp = new Double(((Long) importo));
              else if (importo instanceof Double)
                impapp = new Double((Double) importo);
            }
            this.sqlManager.update("update garecont set impqua =? where ngara=? and codimp=?", new Object[]{impapp, codgar, ditta});
          }
        }
      }
    }

    /**
     * Il metodo viene richiamato dopo avere fatto il controllo sulla presenza delle parimerito da scegliere manualmente.
     * In caso di presenza di parimerito nella variabile ultimeAggiudicatariaSelezionate ho la lista della parimerito,
     * e in ribauoParimerito il valore comune del ribasso delle parimerito.
     *
     * @param ngara
     * @param ultimeAggiudicatariaSelezionate
     * @param ribauoParimerito
     * @param hMapGARE
     * @param hMapParametri
     * @param hMapTORN
     * @throws GestoreException
     */
    private void settaAggiudicatarie(String ngara, String ultimeAggiudicatariaSelezionate, Double ribauoParimerito, HashMap hMapGARE,
            HashMap hMapParametri, HashMap hMapTORN) throws GestoreException {

        if (logger.isDebugEnabled())
            logger.debug("settaAggiudicatarie(" + ngara + "): inizio metodo");

        String dittao = null;
        Double ribauo = null;

        long numeroDitteAggiudicatarie = 0;

        long aqnumope = 0;
        if (hMapGARE.get("aqnumope") != null)
          aqnumope = ((Long) hMapGARE.get("aqnumope")).longValue();

        String costofisso = ((String)hMapGARE.get("costofisso")).toString();

        String selectDITG = getSqlDittePerAggiudicazioneAccordiQuadroPiuOperatori(hMapGARE,hMapParametri);

        try {
            List datiDITG = this.sqlManager.getListVector(selectDITG,
                    new Object[] { ngara });
            if (datiDITG != null && datiDITG.size() > 0) {
              String ditteSelezionate[]= null;
              if(aqnumope>0 && ultimeAggiudicatariaSelezionate!=null && !"".equals(ultimeAggiudicatariaSelezionate)){
                ditteSelezionate = ultimeAggiudicatariaSelezionate.split(",");
              }
              for (int i = 0; i < datiDITG.size(); i++) {
                  dittao = SqlManager.getValueFromVectorParam(
                          datiDITG.get(i), 0).stringValue();
                  if(hMapGARE.get("ultdetlic") != null && !"1".equals(costofisso)){
                    ribauo = SqlManager.getValueFromVectorParam(
                        datiDITG.get(i), 2).doubleValue();
                  } else {
                      ribauo = SqlManager.getValueFromVectorParam(
                          datiDITG.get(i), 1).doubleValue();
                  }
                  if(aqnumope==0){
                    //non è stato specificato il numero di operatori, quindi si prendono tutti gli operatori della lista senza fare controlli
                    this.aggiornaStatoAggiudicazione(ngara, dittao,
                        new Long(4));
                    numeroDitteAggiudicatarie++;
                  }if(!ribauo.equals(ribauoParimerito) && numeroDitteAggiudicatarie<aqnumope){
                    //ancora non è stato raggiunto il numero di operatori richiesto in aqnumope e non si è nel caso di parimerito
                    this.aggiornaStatoAggiudicazione(ngara, dittao,
                        new Long(4));
                    numeroDitteAggiudicatarie++;
                  }else if(ribauo.equals(ribauoParimerito)){
                    //Si è nel caso di parimerito e si devono elaborare solo le ditte che sono state selezionate dall'utente
                    for(int j=0; j < ditteSelezionate.length; j++){
                      if(dittao.equals(ditteSelezionate[j])){
                        this.aggiornaStatoAggiudicazione(ngara, dittao,
                            new Long(4));
                        numeroDitteAggiudicatarie++;
                        break;
                      }
                    }
                  }
                  if(i==0){
                    //Si valorizza il parametro "primaAggiudicatariaSelezionata" con la prima
                    //ditta della lista, in modo che si mantenga la compatibilità con tutte
                    //le operazioni successive per l'aggiudicazione
                    hMapParametri.put("primaAggiudicatariaSelezionata",dittao);
                  }

            }//fine ciclo ditte
           }
           hMapParametri.put("numeroDitteAggiudicatarie", new Long(numeroDitteAggiudicatarie));
        } catch (SQLException e) {
            throw new GestoreException(
                    "Errore nel calcolo delle aggiudicatarie",
                    "settaAggiudicatarie", new Object[]{ngara},e);
        }

        if (logger.isDebugEnabled())
            logger.debug("settaAggiudicatarie(" + ngara + "): fine metodo");
    }


    /**
     * Si effettua il controllo delle ditte congrue per individuare l'eventuale presenza delle
     * ultime parimerito, i cui codici vengono inseriti nella lista listaUltimeParimerito, così come
     * viene memorizzato in ribauoParimerito il valore comune del ribasso
     * @param ngara
     * @param hMapGARE
     * @param hMapParametri
     * @param hMapTORN
     * @return String
     * @throws GestoreException
     */
    private String initControlliAggiudicazioneAccordiQuadroPiuOperatori(String ngara, HashMap hMapGARE,
        HashMap hMapParametri, HashMap hMapTORN) throws GestoreException {

      if (logger.isDebugEnabled())
          logger.debug("initControlliAggiudicazioneAccordiQuadroPiuOperatori(" + ngara + "): inizio metodo");

      String dittao = null;
      Double ribauo = null;
      Double ribauoSuccessivo = null;
      Double ribauoParimerito = null;
      String ultimaDitta = null;
      long numeroDitteAggiudicatarieOK = 0;
      long numeroDitteParimerito = 1;
      String listaUltimeParimerito= null;
      String esitoControllo="OK";
      long aqnumope = 0;
      long numeroParimeritoDaSelezionare=0;
      boolean ultimeParimeritoFineCiclo = false;

      if (hMapGARE.get("aqnumope") != null)
        aqnumope = ((Long) hMapGARE.get("aqnumope")).longValue();

      String costofisso = (String)hMapGARE.get("costofisso");

      if (aqnumope >0){
        long modlicg = 0;
        if (hMapGARE.get("modlicg") != null)
            modlicg = ((Long) hMapGARE.get("modlicg")).longValue();

        long numeroDitteIndividuate=0;

        String selectDITG = getSqlDittePerAggiudicazioneAccordiQuadroPiuOperatori(hMapGARE,hMapParametri);

        try {
            List datiDITG = this.sqlManager.getListVector(selectDITG,
                    new Object[] { ngara });
            if (datiDITG != null && datiDITG.size() > 0) {
              int i = 0;
              while(i < datiDITG.size()){
                    dittao = SqlManager.getValueFromVectorParam(
                            datiDITG.get(i), 0).stringValue();

                    if(hMapGARE.get("ultdetlic") != null && !"1".equals(costofisso)){
                      ribauo = SqlManager.getValueFromVectorParam(
                          datiDITG.get(i), 2).doubleValue();
                    } else {
                        ribauo = SqlManager.getValueFromVectorParam(
                            datiDITG.get(i), 1).doubleValue();
                    }
                    if(numeroDitteIndividuate < aqnumope){
                      //Si deve controllare se vi sono ditte con punteggio uguale a quella attuale
                      int numeroParimerito = 0;
                      String dittaoSuccessiva = null;
                      listaUltimeParimerito = "'" + dittao + "'";

                      for (int j = i + 1; j < datiDITG.size(); j++) {
                        dittaoSuccessiva = SqlManager.getValueFromVectorParam(
                            datiDITG.get(j), 0).stringValue();
                        if(hMapGARE.get("ultdetlic") != null && !"1".equals(costofisso)){
                          ribauoSuccessivo = SqlManager.getValueFromVectorParam(
                              datiDITG.get(j), 2).doubleValue();
                        } else {
                          ribauoSuccessivo = SqlManager.getValueFromVectorParam(
                                datiDITG.get(j), 1).doubleValue();
                        }
                        if(ribauo.equals(ribauoSuccessivo)){
                          ribauoParimerito = ribauo;
                          if(j == datiDITG.size() - 1){
                            numeroParimerito+=2;
                            numeroDitteIndividuate+=numeroParimerito;
                            ultimeParimeritoFineCiclo=true;
                          }else
                            numeroParimerito++;
                          listaUltimeParimerito += ",'" + dittaoSuccessiva + "'";
                        }else{
                          if(numeroParimerito>0){
                            numeroParimerito++;
                            numeroDitteIndividuate+=numeroParimerito;
                            if(numeroDitteIndividuate < aqnumope)
                              numeroDitteAggiudicatarieOK += numeroParimerito;
                          }else{
                            numeroDitteIndividuate++;
                            listaUltimeParimerito = null;
                            numeroDitteAggiudicatarieOK++;
                          }
                          break;
                        }
                      }
                      if(numeroParimerito==0){
                        i++;
                      }else{
                        i=i+numeroParimerito;
                      }
                    }else if(numeroDitteIndividuate == aqnumope){
                      //Si riescono ad individuare tanti operatori quanti specificati in aqnumope senza cadere nel caso di ultime parimerito
                      break;
                    }else if(numeroDitteIndividuate > aqnumope){
                      //Non Si riescono ad individuare tanti operatori quanti specificati in aqnumope poichè vi é il caso di ultime parimerito
                      numeroParimeritoDaSelezionare = aqnumope - numeroDitteAggiudicatarieOK;
                      break;
                    }

              }//fine ciclo ditte
            }

            if(numeroDitteIndividuate > aqnumope){
              if(ultimeParimeritoFineCiclo)
                numeroParimeritoDaSelezionare = aqnumope - numeroDitteAggiudicatarieOK;
              //Lista delle parimerito, nella forma: 'aaa','bbb'
              hMapParametri.put("listaUltimeParimerito", listaUltimeParimerito);
              //Valore del ribasso(o altro parametro) comune alle parimerito
              hMapParametri.put("ribauoParimerito", ribauoParimerito);
              //Numero di ditte che devono essere selezionate per arrivare ad un numero di ditte pari a aqnumope
              hMapParametri.put("numeroParimeritoDaSelezionare", numeroParimeritoDaSelezionare);
              esitoControllo = "NOK";
            }

        } catch (SQLException e) {
            throw new GestoreException(
                    "Errore nel calcolo delle parimerito",
                    "calcoloParimerito", new Object[]{ngara},e);
        }
      }

      if (logger.isDebugEnabled())
          logger.debug("initControlliAggiudicazioneAccordiQuadroPiuOperatori(" + ngara + "): fine metodo");

      return esitoControllo;
    }


    /**
     * Viene costruita la select per l'estrazione delle ditte congrue per l'aggiudicazione
     * provvisoria per accordi quadro con più operatori
     *
     * @param hMapGARE
     * @param hMapParametri
     * @return String
     */
    private String getSqlDittePerAggiudicazioneAccordiQuadroPiuOperatori(HashMap hMapGARE, HashMap hMapParametri){

      long modlicg = 0;
      if (hMapGARE.get("modlicg") != null)
          modlicg = ((Long) hMapGARE.get("modlicg")).longValue();

      String costofisso = (String)hMapGARE.get("costofisso");
      String importoOffertaCongiunta = "";
      String selectImportoOffertaCongiunta = "";

      if(hMapGARE.get("ultdetlic") != null && !"1".equals(costofisso)){
        importoOffertaCongiunta = "IMPOFF - " + this.sqlManager.getDBFunction("isnull",  new String[] {"IMPPERM", "0" });
        importoOffertaCongiunta += " + " + this.sqlManager.getDBFunction("isnull",  new String[] {"IMPCANO", "0" });
        selectImportoOffertaCongiunta = ", "+ importoOffertaCongiunta;
      }

      String selectDITG = "select dittao, ribauo"+selectImportoOffertaCongiunta+" from ditg "
          + "where ngara5 = ? and staggi > 1 and congruo='1'";

      if (hMapParametri.get("garaInversa") != null && "1".equals(hMapParametri.get("garaInversa"))){
        //si devono escludere le ditte con amminversa='2'
        selectDITG +=" and (amminversa != 2 or amminversa is null)";
      }

      if(hMapGARE.get("ultdetlic") != null && !"1".equals(costofisso)){
          selectDITG += " order by " + importoOffertaCongiunta + ", staggi, numordpl";
      }else if (modlicg == 6 || modlicg == 17) {
          selectDITG += " order by ribauo desc, staggi, numordpl";
      } else {
          selectDITG += " order by ribauo, staggi, numordpl";
      }

      return selectDITG;
    }

    /**
    * Vengono annullati i dati relativi all'aggiudicazione definitiva della gara/lotto
    *
    * @param ngara
    * @param codgar
    * @throws GestoreException
    */
   private void aggiornaImplGAREannullaAggiudicazione(String ngara, String codgar)
           throws GestoreException {

       if (logger.isDebugEnabled())
           logger.debug("aggiornaImplGAREannullaAggiudicazione(" + ngara
                   + "): inizio metodo");

       Long fasgar = new Long(GestioneFasiGaraFunction.FASE_CALCOLO_AGGIUDICAZIONE/10);
       Long stepgar = new Long(GestioneFasiGaraFunction.FASE_CALCOLO_AGGIUDICAZIONE);

       try {
          this.sqlManager.update(
                     "update GARE set NOMIMA = null, IMPGAR = null, DITTA = null, "
                             + "RIBAGG = null, RIBOEPV = null, IAGGIU = null, FASGAR = ?, "
                             + "STEPGAR = ? where NGARA = ? and CODGAR1 = ?",
                     new Object[] {fasgar, stepgar, ngara, codgar});


         this.sqlManager.update("delete from DITGAQ where NGARA = ?",
             new Object[] { ngara});

       } catch (SQLException e) {
           throw new GestoreException(
                   "Errore nello sbiancamento dei campi relativi all'aggiudicazione " +
                   "defintiva del lotto della gara " +   ngara , null, e);
       }

       if (logger.isDebugEnabled())
           logger.debug("aggiornaImplGAREannullaAggiudicazione(" + ngara
                   + "): fine metodo");
   }

   /**
    * Funzione per annullare il calcolo dell'aggiudicazione, invocata dalla
    * fase di gara "Calcolo aggiudicazione".
    *
    * @param codiceGara
    * @param numeroGara
    * @param isGaraLottiConOffertaUnica
    * @throws GestoreException
    */
   public void annullaAggiudicazioneDefinitiva(String codiceGara,
       String numeroGara, boolean isGaraLottiConOffertaUnica)
       throws GestoreException {

   String codgar = codiceGara;
   String ngara = numeroGara;

   if (logger.isDebugEnabled())
       logger.debug("annullaAggiudicazioneDefinitiva(" + codgar + "," + ngara
               + "): inizio metodo");

   Long modcont = null;
   String accqua = null;
   Long aqoper = null;
   Vector datiTorn=null;
   try {
     datiTorn = this.sqlManager.getVector("select modcont, accqua from torn where codgar=?", new Object[]{codgar});
     if(datiTorn!=null && datiTorn.size()>0){
       modcont = SqlManager.getValueFromVectorParam(datiTorn, 0).longValue();
       accqua = SqlManager.getValueFromVectorParam(datiTorn, 1).stringValue();
     }
     aqoper=(Long)this.sqlManager.getObject("select aqoper from gare1 where ngara=?", new Object[]{ngara});

   } catch (SQLException e) {
     throw new GestoreException(
         "Errore durante la lettura dei campi modcont e accqua della gara "
         + ngara + " a cui applicare l'annullamento "
         + "del calcolo aggiudicazione", null, e);
   }


   //Nel caso di offerta unica si deve aggiornare anche la fase della gare complementare
   try {
     if (isGaraLottiConOffertaUnica) {
       Long fasgar = new Long(GestioneFasiGaraFunction.FASE_CALCOLO_AGGIUDICAZIONE/10);
       this.sqlManager.update("update gare set fasgar =? where ngara=? ", new Object[]{fasgar, codgar});
     }
   }catch (SQLException e) {
     throw new GestoreException(
         "Errore durante l'aggiornamento della fase di gara delle gara complementare della gara " + codgar, null, e);
   }

   try {
     this.sqlManager.update("update gare1 set iaggiuini = null, ribaggini = null  where ngara=? ", new Object[]{ngara});

   }catch (SQLException e) {
     throw new GestoreException(
         "Errore durante l'aggiornamento di GARE1 a seguito dell'annullamento dell'aggiudicazione definitiva per la gara" + ngara, null, e);
   }

   try{
     //String ditta = (String)this.sqlManager.getObject("select ditta from gare where ngara=?", new Object[]{ngara});
     String ditta = null;

     //Decremento del numero di aggiudicazioni
     Vector datiGara = this.sqlManager.getVector("select elencoe,dattoa,ditta from gare where ngara=?", new Object[]{ngara});
     if(datiGara!=null && datiGara.size()>0){
       String elenco = SqlManager.getValueFromVectorParam(datiGara, 0).stringValue();
       Timestamp dattoa = SqlManager.getValueFromVectorParam(datiGara, 1).dataValue();
       ditta = SqlManager.getValueFromVectorParam(datiGara, 2).stringValue();
       String chiaveGara= ngara;
       if(isGaraLottiConOffertaUnica){
         chiaveGara= codgar;
         elenco = (String)this.sqlManager.getObject("select elencoe from gare where ngara=?", new Object[]{chiaveGara});
       }
       if(elenco!=null && !"".equals(elenco) && dattoa!=null){
         String codiceElenco="$"+elenco;
         Long tipoalgo = (Long)sqlManager.getObject("select tipoalgo from garealbo where ngara=?", new Object[]{elenco});
         Long tipoGara = (Long)sqlManager.getObject("select tipgen from torn where codgar=?", new Object[]{codgar});
         Vector datiCatg = sqlManager.getVector("select catiga, numcla from catg where ngara=? and ncatg=1", new Object[] { chiaveGara });
         String catiga=null;
         Long numcla=null;
         String modo="DEL";
         if(datiCatg!=null && datiCatg.size()>0){
           catiga = (String)((JdbcParametro) datiCatg.get(0)).getValue();
           numcla = (Long)((JdbcParametro) datiCatg.get(1)).getValue();
         }

         String codiceDitta = this.pgManager.getDittaSelezionataDaElenco(ditta,chiaveGara);
         String stazioneAppaltante = (String)this.sqlManager.getObject("select cenint from torn where codgar=?", new Object[]{codgar});
         if(codiceDitta!=null){
           this.pgManager.aggiornaNumAggiudicazioni(codiceElenco, elenco, codiceDitta, catiga, tipoGara, numcla, stazioneAppaltante, tipoalgo, modo);
         }
         if(aqoper!=null && aqoper.longValue()==2){
           //Gestione decremento per le occorrenze in DITGAQ
           this.pgManager.aggiornaNumAggiudicazioniDITGAQ(codiceElenco, elenco, chiaveGara, ngara, ditta, catiga, tipoGara, numcla, stazioneAppaltante, tipoalgo, modo,1);
         }
       }
     }

     this.aggiornaImplGAREannullaAggiudicazione(ngara,codgar);



     if (!isGaraLottiConOffertaUnica) {
       //Caso GARA lotto unico - sbiancamento dati GARECONT
       this.sqlManager.update("update garecont set impqua=null, codimp=null, coorba=null, codbic=null, banapp=null  where ngara=? and ncont=?",
             new Object[]{ngara, new Long(1) });
     }else{
       if((new Long(1)).equals(modcont)){
         this.sqlManager.update("update garecont set impqua=null, codimp=null, coorba=null, codbic=null, banapp=null  where ngara=? and ngaral=?",
             new Object[]{codgar,ngara });
       }else if((new Long(2)).equals(modcont)){
         this.cancellaGarecontOffertaUnica(codgar);

         if(ditta!=null && !"".equals(ditta) && "1".equals(accqua)){
           Double impapp=null;
           Object importo = this.sqlManager.getObject("select sum(impapp) from gare where codgar1=? and ditta=?", new Object[]{codgar, ditta});
           if(importo!=null){
             if (importo instanceof Long)
               impapp = new Double(((Long) importo));
             else if (importo instanceof Double)
               impapp = new Double((Double) importo);
           }
           this.sqlManager.update("update garecont set impqua =? where ngara=? and codimp=?", new Object[]{impapp, codgar, ditta});
         }
       }


     }
   }catch(SQLException e) {
     throw new GestoreException(
         "Errore durante l'aggiornamento di GARECONT a seguito dell'annullamento dell'aggiudicazione definitiva", null, e);
   }

   if (logger.isDebugEnabled())
       logger.debug("annullaAggiudicazioneDefinitiva(" + codgar + "," + ngara
               + "): fine metodo");
}


   /**
    * Funzione per eseguire il calcolo dell'iva per popolare GAREIVA e aggiornare IMPTOT e IMPIVA di GARECONT
    *
    * @param ngara
    * @param iaggiu
    *
    * @throws SQLException
    */
   public void calcoImportoIva (String ngara, Double iaggiu) throws SQLException{
     Long ivalav = (Long) this.sqlManager.getObject("select ivalav from gare where ngara=?", new Object[]{ngara});
     //Cancello GAREIVA e sbianco GARECONT.IMPTOT e GARECONT.IMPIVA a prescindere se si effettua il calcolo o meno
     this.sqlManager.update("delete from gareiva where ngara=?", new Object[]{ngara});
     this.sqlManager.update("update GARECONT set IMPTOT = null, IMPIVA = null where NGARA = ? and NCONT = ?", new Object[]{ngara, new Long(1)});
     String valoreIvaString ="";
     Long tab1tipIva = null;
     Double imponib = iaggiu;
     Double impiva = null;
     Double imptot = imponib;
     if(ivalav!=null){
       if(ivalav.longValue()==0){
         valoreIvaString ="0";
         tab1tipIva = new Long(0);
       }else{
         Vector datiTabellato =this.sqlManager.getVector("select tab1desc,tab1tip from tab1 where tab1cod=? and tab1desc=?", new Object[]{"G_055",ivalav.toString()});
         if(datiTabellato!=null && datiTabellato.size()>0){
           valoreIvaString = (String) ((JdbcParametro) datiTabellato.get(0)).getValue();
           tab1tipIva = (Long) ((JdbcParametro) datiTabellato.get(1)).getValue();
         }
       }
       if(valoreIvaString!=null && !"".equals(valoreIvaString)){
         Double perciva = new Double(valoreIvaString);
         if(imponib==null)
           imponib = new Double(0);
         impiva =new Double(imponib.doubleValue() * perciva.doubleValue() / 100);
         impiva = new Double(UtilityNumeri.convertiDouble(impiva, UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 2));
         imptot = new Double(imptot.doubleValue() + impiva.doubleValue());
         this.sqlManager.update("insert into gareiva(id,ngara,ncont,perciva,imponib,impiva) values(?,?,?,?,?,?)",
             new Object[]{new Long(genChiaviManager.getNextId("GAREIVA")),ngara, new Long(1),tab1tipIva,imponib,impiva});
       }
     }
     this.sqlManager.update("update GARECONT set IMPTOT = ?, IMPIVA = ? where NGARA = ? and NCONT = ?", new Object[]{imptot,impiva,ngara, new Long(1)});
   }


   /**
    * Funzione per eseguire l'estrazione casuale nella lista passata come argomento.
    * Se tipoSelezione = 1 si estrae un solo elemento
    * Se tipoSelezione = 2 si estraggono due elementi
    * Se tipoSelezione = 3 si estraggono un numero di elementi specificato in numElementi
    *
    * @param tipoSelezione
    * @param lista
    * @param numElementi
    * @param hMapDatiSelezione
    *
    */
   public void selezioneCasualeParimerito(String tipoSelezione, String lista, long numElementi, HashMap hMapDatiSelezione){
     if(lista!=null && !"".equals(lista)){
       StringBuffer listaModificabile = new StringBuffer(lista);
       if("3".equals(tipoSelezione)){
         String listaParimeritoSelezionate = "";
         String dittaSel="";
         for(int j=0;j<numElementi;j++){
           if(j>0)
             listaParimeritoSelezionate+=",";
           dittaSel = this.selezioneCasualeElementoDaStringa(listaModificabile);
           listaParimeritoSelezionate += dittaSel;
         }
         hMapDatiSelezione.put("listaParimeritoSelezionate", listaParimeritoSelezionate);
       }else {
         String primaSelezione = this.selezioneCasualeElementoDaStringa(listaModificabile);
         hMapDatiSelezione.put("selezione1", primaSelezione);
         if("2".equals(tipoSelezione)){
           String secondaSelezione = this.selezioneCasualeElementoDaStringa(listaModificabile);
           hMapDatiSelezione.put("selezione2", secondaSelezione);
         }
       }
     }
   }


   /**
    * Funzione che estrae un elemento dalla lista e crea una nuova lista privato di tale
    * elemento
    *
    * @param lista
    * @return elemento estratto dalla lista
    */
   private String selezioneCasualeElementoDaStringa(StringBuffer lista){
     String ret="";
     String listaModificabile = lista.toString();
     if(listaModificabile!=null && !"".equals(listaModificabile)){
       Random random = new Random();
       String elementi[] = listaModificabile.split(",");
       if(elementi.length>1){
         int i = random.nextInt(elementi.length - 1);
         ret = elementi[i];
         listaModificabile = listaModificabile.replace(ret , "");
         if(i==0)
           listaModificabile = listaModificabile.replaceFirst(",", "");
         else if(i == elementi.length - 1)
           listaModificabile = listaModificabile.substring(0, listaModificabile.length() - 1);
         else
           listaModificabile = listaModificabile.replace(",,", ",");
         lista.delete(0, lista.length());
         lista.append(listaModificabile);
         ret=ret.replaceAll("'", "");
       }else{
         //La lista è formata da un solo elemento
         ret = elementi[0];
         ret=ret.replaceAll("'", "");
         lista.delete(0, lista.length());
       }
     }
     return ret;
   }

   /**
    * Funzione che calcolo la soglia di anomali
    *
    * @param percentuale
    * @paramh MapParametri
    * @param hMapGARE
    * @param numeroDitte
    *
    */
   private void calcolaSogliaAnomalia(int percentuale,HashMap hMapParametri, HashMap hMapGARE, long numeroDitte){
     double mediaRibasso=0;
     if (hMapParametri.get("mediaRibasso") != null){
       mediaRibasso = ((Double) hMapParametri.get("mediaRibasso")).doubleValue();;
     }
     double limiteAnomalia =0;
     if(numeroDitte>0){
       limiteAnomalia = mediaRibasso * (1 + percentuale/100.00);
       int precut = 0;
       if (hMapGARE.get("precut") != null)
         precut = ((Long) hMapGARE.get("precut")).intValue();

       limiteAnomalia = this.valoreArrotondatoTroncato(limiteAnomalia, precut, hMapParametri);

     }
     hMapParametri.put("limiteAnomalia", new Double(limiteAnomalia));
   }

   /**
    * Funzione che controlla se il numero di ditte ammesse in gara è superiore al valore specificato nel tabellato
    * specificato come riferimento
    *
    * @param ngara
    * @return Vettore composto da due elementi, il primo è un valore booleano rappresentante l'esito del controllo, il
    * secondo il valore della soglia contenuto nel tabellato A1135
    *
    *@throws SQLException,GestoreException
    */
   public Object[] controlloNumDitteAmmesseSopraSoglia(String ngara,String tabellato, int numeroVoce) throws SQLException, GestoreException{
     boolean esitoControlloNumDitte= true;
     Long numDitteGara = (Long)sqlManager.getObject("select count(dittao) from ditg where ngara5=? and " +
         "(AMMGAR <> '2' or AMMGAR is null) and (MOTIES < 99 or MOTIES is null)", new Object[]{ngara});
     if(numDitteGara==null)
       numDitteGara = new Long(0);

     String descTabellatoValoreConfronto = this.leggiDescrizioneDaTab1(tabellato, new Long(numeroVoce));
     Long valoreConfronto = new Long(0);
     if(descTabellatoValoreConfronto!=null && !"".equals(descTabellatoValoreConfronto)){
       int pos = descTabellatoValoreConfronto.indexOf("-");
       if(pos<0)
         pos=1;
       descTabellatoValoreConfronto = descTabellatoValoreConfronto.substring(0, pos);
       descTabellatoValoreConfronto=descTabellatoValoreConfronto.trim();
       valoreConfronto = new Long(descTabellatoValoreConfronto);
     }

     if(numDitteGara.longValue() < valoreConfronto.longValue())
       esitoControlloNumDitte = false;
      return new Object[]{new Boolean(esitoControlloNumDitte),descTabellatoValoreConfronto};
   }


   /**
    * Funzione che controlla se il numero di ditte che hanno inviato l'offerta è superiore al valore specificato nel tabellato
    * specificato come riferimento
    *
    * @param ngara
    * @return Vettore composto da due elementi, il primo è un valore booleano rappresentante l'esito del controllo, il
    * secondo il valore della soglia contenuto nel tabellato A1135
    *
    *@throws SQLException,GestoreException
    */
   public Object[] controlloNumDitteInvoffSopraSoglia(String ngara,String tabellato, int numeroVoce) throws SQLException, GestoreException{
     boolean esitoControlloNumDitte= true;
     Long numDitteGara = (Long)sqlManager.getObject("select count(dittao) from ditg where ngara5=? and (INVOFF = '1' or INVOFF is null) ", new Object[]{ngara});
     if(numDitteGara==null)
       numDitteGara = new Long(0);

     String descTabellatoValoreConfronto = this.leggiDescrizioneDaTab1(tabellato, new Long(numeroVoce));
     Long valoreConfronto = new Long(0);
     if(descTabellatoValoreConfronto!=null && !"".equals(descTabellatoValoreConfronto)){
       int pos = descTabellatoValoreConfronto.indexOf("-");
       if(pos<0)
         pos=1;
       descTabellatoValoreConfronto = descTabellatoValoreConfronto.substring(0, pos);
       descTabellatoValoreConfronto=descTabellatoValoreConfronto.trim();
       valoreConfronto = new Long(descTabellatoValoreConfronto);
     }

     if(numDitteGara.longValue() < valoreConfronto.longValue())
       esitoControlloNumDitte = false;
      return new Object[]{new Boolean(esitoControlloNumDitte),descTabellatoValoreConfronto};
   }

   /**
    * Funzione che nel caso di OEPV e riparametrazione tecnica o economica sostituisce nella select
    * i campi dei punteggi con quelli riparametrati
    *
    * @param select
    * @param hMapGARE
    * @param forzareRiparametrati
    * @return String
    *
    */
   private String cambiaSelectPunteggiConRiparametrati(String select, HashMap hMapGARE, boolean forzareRiparametrati){
     String newSelect=select;
     int modlicg = 0;
     if (hMapGARE.get("modlicg") != null)
        modlicg = ((Long) hMapGARE.get("modlicg")).intValue();

     if (modlicg == 6) {
       int metpunti=0;
       if (hMapGARE.get("metpunti") != null)
         metpunti = ((Long) hMapGARE.get("metpunti")).intValue();

       if(metpunti==2 || forzareRiparametrati){
         int riptec = 0;
         if (hMapGARE.get("riptec") != null)
           riptec = ((Long) hMapGARE.get("riptec")).intValue();

         int ripeco = 0;
         if (hMapGARE.get("ripeco") != null)
           ripeco = ((Long) hMapGARE.get("ripeco")).intValue();

         if(riptec==1 || riptec==2)
           newSelect =newSelect.replace("puntec", "puntecrip");

         if(ripeco==1 || ripeco==2)
           newSelect =newSelect.replace("puneco", "punecorip");
       }
     }
     return newSelect;
   }

   /**
    * Metodo che legge il tabellato A1149 per determinare se è attiva la gestione prezzo per i criteri economici
    *
    * @param hMapParametri
    * @param ngara
    * @throws GestoreException
    */
   private void inizializzaGestionePrezzo(HashMap hMapParametri, String ngara) throws GestoreException {
    String valore;
    try {
      valore = (String)sqlManager.getObject("select tab1desc from tab1 where tab1cod=? and tab1tip=?", new Object[]{"A1149", new Long(1)});
      if(valore!=null && !"".equals(valore)){
        valore = valore.substring(0, 1);
        hMapParametri.put("abilitataGestioneCriteriPrezzo", valore);
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella lettura del tabellato A1149", null, e);
    }

    if("1".equals(valore)){
      Boolean esistonoCriteriIsnprz=new Boolean(false);
      try {
        Long conteggio = (Long)sqlManager.getObject("select count(ngara) from goev where ngara=? and ISNOPRZ = '1'",new Object[]{ngara});
        if(conteggio!=null && conteggio.longValue()>0)
          esistonoCriteriIsnprz = new Boolean(true);
        hMapParametri.put("esistonoCriteriIsnprz", esistonoCriteriIsnprz);
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nel conteggio delle occorrenze di GOEV con ISNPRZ='1'", null, e);
      }
    }
   }

   /**
    * Viene letto il valore del tabellato A1128, che indica se la modalita di impostazione
    * del metodo di calcolo soglia è manuale o automatico
    * @return
    * @throws GestoreException
    */
   public String isModalitaManuale() throws GestoreException{
     String result="";
     String descTabellato = this.leggiDescrizioneDaTab1("A1128", new Long(1));
     if(descTabellato!=null && !"".equals(descTabellato)){
       descTabellato = descTabellato.substring(0, descTabellato.indexOf(' '));
       if (descTabellato.length() > 0 && ("0".equals(descTabellato)))
           result="1";
       else if (descTabellato.length() > 0 && ("1".equals(descTabellato)))
         result="2";
     }
     return result;
   }

   public String impostaSelectDittePerCalcoloStaggi(HashMap hMapGARE, HashMap hMapParametri){

     String selectDITG = "select dittao,ribauo,staggi,puntec,puneco,staggiali " +
             "from ditg where ammgar<>'2' and " +
             "ngara5 = ? and staggi <> 1";

     int modlicg = 0;
     if (hMapGARE.get("modlicg") != null)
        modlicg = ((Long) hMapGARE.get("modlicg")).intValue();

     if (modlicg == 6 || modlicg == 17) {
         selectDITG += " order by ribauo desc, staggi";
         String abilitataGestioneCriteriPrezzo = "0";
         boolean esistonoCriteriIsnprz = false;
         if (hMapParametri.get("abilitataGestioneCriteriPrezzo") != null)
           abilitataGestioneCriteriPrezzo = ((String) hMapParametri.get("abilitataGestioneCriteriPrezzo"));
         if (hMapParametri.get("esistonoCriteriIsnprz") != null)
           esistonoCriteriIsnprz = (((Boolean) hMapParametri.get("esistonoCriteriIsnprz"))).booleanValue();
         if("1".equals(abilitataGestioneCriteriPrezzo) && esistonoCriteriIsnprz){
           selectDITG= selectDITG.replace("puntec", "puntalt");
           selectDITG= selectDITG.replace("puneco", "puntprz");
         }else
           selectDITG = this.cambiaSelectPunteggiConRiparametrati(selectDITG, hMapGARE, false);

     } else {
         selectDITG += " order by ribauo, staggi";
     }
     return selectDITG;
   }

   /**
    * Calcolo della soglia anomalia secondo la normativa DL 32/2019
    * @param ngara
    * @param hMapTORN
    * @param hMapGARE
    * @param hMapParametri
    * @throws GestoreException
    */
   private void calcoloSogliaAnomaliaDL2019(String ngara, HashMap hMapTORN,
       HashMap hMapGARE, HashMap hMapParametri) throws GestoreException {
     Long numeroDitteAmmesse = null;
     if (hMapParametri.get("numeroDitteAmmesse") != null){
         numeroDitteAmmesse = ((Long) hMapParametri.get("numeroDitteAmmesse")).longValue();
         int precut = 0;
         if (hMapGARE.get("precut") != null)
           precut = ((Long) hMapGARE.get("precut")).intValue();

         double mediaRibasso =0;
         if(hMapParametri.get("mediaRibasso") != null)
           mediaRibasso = ((Double)hMapParametri.get("mediaRibasso")).doubleValue();

         double mediaScarti = 0;
         if(hMapParametri.get("mediaScarti") != null)
           mediaScarti = ((Double)hMapParametri.get("mediaScarti")).doubleValue();

         double limiteAnomalia = 0;
         if(numeroDitteAmmesse.longValue()>=15){
           int X = this.getValoreDaA1157(hMapParametri);

           double Y = mediaScarti * X / 100.00;
           Y = this.valoreArrotondatoTroncato(UtilityMath.round(Y,9), precut, hMapParametri)  * -1;

           double soglia1 = this.valoreArrotondatoTroncato(UtilityMath.round(mediaRibasso + mediaScarti,9), precut, hMapParametri);
           String descrizioneTabellato = this.leggiDescrizioneDaTab1("A1159", new Long(1));
           if(descrizioneTabellato!=null){
             descrizioneTabellato = descrizioneTabellato.substring(0, 1);
           }else {
             String message = "Non trovato il parametro (A1159) per stabilire il "
                 + "criterio con cui fare il calcolo della soglia di anomalia";
               throw new GestoreException(message, "inizializzaParametri.A1159");
           }
           if("1".equals(descrizioneTabellato))
             limiteAnomalia = soglia1 + Y;
           else
             limiteAnomalia = soglia1 * (1 - Y / 100.00);

           hMapParametri.put("soglia1", new Double(soglia1));
           hMapParametri.put("sogliavar", new Double(Y));

         }else{
           double mediarap = mediaScarti / mediaRibasso ;
           mediarap = UtilityMath.round(mediarap,9);
           if(mediarap <= 0.15){
             limiteAnomalia = mediaRibasso * (1 + 0.2);
           }else{
             limiteAnomalia = mediaRibasso + mediaScarti;
           }
           hMapParametri.put("mediarap", new Double(mediarap));
         }

         limiteAnomalia = this.valoreArrotondatoTroncato(UtilityMath.round(limiteAnomalia,9), precut, hMapParametri);

         hMapParametri.put("limiteAnomalia", new Double(limiteAnomalia));
     }
   }


   /**
    * Calcolo della soglia anomalia per la regione Sicilia secondo L.R. Sicilia n.13/2019
    * @param ngara
    * @param hMapTORN
    * @param hMapGARE
    * @param hMapParametri
    * @throws GestoreException
    */
   private void calcoloSogliaRegSicilia(String ngara, HashMap hMapTORN,
       HashMap hMapGARE, HashMap hMapParametri) throws GestoreException {
     Long numeroDitteAmmesse = null;
     if (hMapParametri.get("numeroDitteAmmesse") != null){
         numeroDitteAmmesse = ((Long) hMapParametri.get("numeroDitteAmmesse")).longValue();
         int precut = 0;
         if (hMapGARE.get("precut") != null)
           precut = ((Long) hMapGARE.get("precut")).intValue();

         double mediaRibasso =0;
         if(hMapParametri.get("mediaRibasso") != null)
           mediaRibasso = ((Double)hMapParametri.get("mediaRibasso")).doubleValue();

         double sommaRib =0;
         if(hMapParametri.get("sommaRib") != null)
           sommaRib = ((Double)hMapParametri.get("sommaRib")).doubleValue();


         double limiteAnomalia = 0;
         int cifre[] = this.getPrimeDueCifredecimali(sommaRib);
         int X=cifre[0];
         int Y=cifre[1];

         if(X==0){
           limiteAnomalia = mediaRibasso;
         }else{
           int verificaParita = Y & 1;
           double cento = 100.00;
           if(verificaParita == 0) {
             //pari
             limiteAnomalia = mediaRibasso - mediaRibasso * X / cento;
           }else{
             //dispari
             limiteAnomalia = mediaRibasso + mediaRibasso * X / cento;
           }
         }

         limiteAnomalia = this.valoreArrotondatoTroncato(UtilityMath.round(limiteAnomalia,9), precut, hMapParametri);
         hMapParametri.put("limiteAnomalia", new Double(limiteAnomalia));
     }
   }

   /**
    * Si considerano le prime due cifre decimali della somma dei ribassi. A seconda dell'interpretazione
    * adottata (parametro A1157), se ne considera il prodotto (default) o direttamente il valore.
    *
    * @param hMapParametri
    * @return int
    * @throws GestoreException
    */
   private int getValoreDaA1157(HashMap hMapParametri) throws GestoreException{
     int val = 0;

     String descrizioneTabellato = this.leggiDescrizioneDaTab1("A1157", new Long(1));
     if(descrizioneTabellato!=null){
       descrizioneTabellato = descrizioneTabellato.substring(0, 1);
     }else {
       String message = "Non trovato il parametro (A1157) per stabilire il "
           + "criterio con cui fare il calcolo della soglia di anomalia";
         throw new GestoreException(message, "inizializzaParametri.A1157");
     }

     double sommaRib = 0.0;
     double sommaRibModulo = 0;
     if(hMapParametri.get("sommaRib") != null)
       sommaRib = ((Double)hMapParametri.get("sommaRib")).doubleValue();

     int cifre[] =this.getPrimeDueCifredecimali(sommaRib);
     if("0".equals(descrizioneTabellato)){
       String numero = String.valueOf(cifre[0]) + String.valueOf(cifre[1]);
       val = (new Integer(numero)).intValue();
     }else{
       val = cifre[0] * cifre[1];
     }
     return val;
   }


   /**
    * Il metodo restituisce le prime 2 cifre decimali dell'importo passato come parametro
    * @param importo
    * @return int[]
    *   indice 0 prima cifra
    *   indice 1 seconda cifra
    */
   private int[] getPrimeDueCifredecimali(double importo){
     double importoModulo = 0;

     if(importo < 0 ){
       importoModulo = - importo;
     }else{
       importoModulo = importo;
     }

     double primoProdottoPer10 = importoModulo * 10;
     BigDecimal bdprimoProdottoPer10 = BigDecimal.valueOf( new Double(primoProdottoPer10)).setScale(9, BigDecimal.ROUND_HALF_UP);
     primoProdottoPer10 = bdprimoProdottoPer10.doubleValue();
     int primaCifra = (int)(primoProdottoPer10 - (Math.floor(importoModulo)*10));
     double secondoProdottoPer10 = primoProdottoPer10 * 10;
     BigDecimal bdsecondoProdottoPer10 = BigDecimal.valueOf( new Double(secondoProdottoPer10)).setScale(9, BigDecimal.ROUND_HALF_UP);
     secondoProdottoPer10=bdsecondoProdottoPer10.doubleValue();
     int secondaCifra = (int)(secondoProdottoPer10 - (Math.floor(primoProdottoPer10)*10));

     return new int[]{primaCifra,secondaCifra};
   }

   /**
    * Il metodo controlla se per la gara si deve applicare la normativa DL.32/2019
    * @param hMapTORN
    * @param hMapGARE
    * @param hMapParametri
    * @return boolean
    * @throws GestoreException
    */
   private boolean isDL2019(HashMap hMapTORN, HashMap hMapGARE, HashMap hMapParametri) throws GestoreException
   {
     boolean result=false;
     int iterga = 0;
     Date dpubavg = null;
     Date dinvit = null;
     Date datpub = null;

     if (hMapGARE.get("dpubavg") != null)
       dpubavg = (Date) hMapGARE.get("dpubavg");
     if (hMapTORN.get("iterga") != null)
       iterga = ((Long) hMapTORN.get("iterga")).intValue();
     if (hMapTORN.get("dinvit") != null)
       dinvit = ((Date) hMapTORN.get("dinvit"));
     if (hMapParametri.get("datpub") != null)
       datpub = ((Date) hMapParametri.get("datpub"));

     int ret=this.getLeggeCalcoloSoglia(new Long(iterga), dinvit, dpubavg, datpub);
     if(ret==3)
       result=true;

     return result;
   }


   /**
    * Viene caricata la data pubblicazione su portale nel parametro datpub dell'hashMap passato come parametro
    * @param codgar
    * @param hMapParametri
    * @throws GestoreException
    */
   private void inizializzaDataPubblicazionePortale(String codgar, HashMap hMapParametri) throws GestoreException
   {
     if (logger.isDebugEnabled())
       logger.debug("inizializzaDataPubblicazionePortale(" + codgar + "): inizio metodo");

       try {
         Date datpub=(Date)this.sqlManager.getObject("select datpub from pubbli where codgar9=? and tippub=?", new Object[]{codgar, new Long(11)});
         hMapParametri.put("datpub", datpub);

       } catch (SQLException e) {
         throw new GestoreException("Errore nella lettura del data di pubblicazione su portale dalla tabella PUBBLI per la gara " + codgar, null, e);
       }

       if (logger.isDebugEnabled())
         logger.debug("inizializzaDataPubblicazionePortale(" + codgar + "): fine metodo");
   }

   /**
    * Viene applicato il troncamento o l'arrotondamento al numero di cifre decimali specificato come parametro.
    * Il parametro se indica se fare l'arrotondamento o il troncamento è stato caricato precedentemente in
    * hMapParametri con chiave "troncamento"
    *
    * @param valore
    * @param cifreDec
    * @param hMapParametri
    * @return double
    */
   private double valoreArrotondatoTroncato(double valore, int cifreDec, HashMap hMapParametri ){
     Boolean troncamento=null;
     if (hMapParametri.get("troncamento") != null)
       troncamento = (Boolean) hMapParametri.get("troncamento");

     if(troncamento.booleanValue()){
       BigDecimal bg= BigDecimal.valueOf(
           new Double(valore))
           .setScale(cifreDec, RoundingMode.DOWN);
       valore=bg.doubleValue();

     }else
       valore = UtilityMath.round(valore, cifreDec);

     return valore;
   }

   public String getValoreInizializzazioneContspe() throws GestoreException{
     String ret=null;
     String desc = this.leggiDescrizioneDaTab1("A1115", new Long(7));
     if(desc!=null && desc.length()>0)
       ret = desc.substring(0, 1);

     return ret;
   }
}