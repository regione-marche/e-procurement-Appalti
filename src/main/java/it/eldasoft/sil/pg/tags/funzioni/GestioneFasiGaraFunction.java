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
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.decorators.trova.FormTrovaTag;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.sil.pg.bl.ControlliOepvManager;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.tags.gestori.plugin.GestorePopupFasiGara;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

/**
 * Funzione per l'inizializzazione della pagina 'Fasi di gara'
 *
 * @author Luca.Giacomazzo
 */
public class GestioneFasiGaraFunction extends AbstractFunzioneTag {

  public static final String    PARAM_WIZARD_PAGINA_ATTIVA          = "WIZARD_PAGINA_ATTIVA";

  private static final String   PROP_PAGINAZIONE                    = "it.eldasoft.sil.pg.fasi.paginazione";
  private static final String   PROP_PAGINAZIONE_ELENCHI_CATALOGHI  = "it.eldasoft.sil.pg.ritiro.paginazione";

  // Nome dell'oggetto presente in sessione contenente tutti i codici delle gare
  // per le quali e' stato eseguito il controllo delle ditte escluse o
  // vincitrici di altri lotti
  public static final String    SESSIONE_FASI_GARA_DITTE_MODIFICATE = "FASI_GARA_DITTE_MODIFICATE";

  // Step 1 del wizard fasi di gara
  public static final int       FASE_APERTURA_DOCUM_AMMINISTR       = 20;
  // Step 2 del wizard fasi di gara
  public static final int       FASE_SORTEGGIO_CONTROLLO_REQUISITI  = 30;
  // Step 3 del wizard fasi di gara (pagina a scheda)
  public static final int       FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA = 35;
  // Step 4 del wizard fasi di gara
  public static final int       FASE_ESITO_CONTROLLO_SORTEGGIATE    = 40;
  // Step 5 del wizard fasi di gara (pagina a scheda)
  public static final int       FASE_CONCLUSIONE_COMPROVA_REQUISITI = 45;
  // Step 6 del wizard fasi di gara
  public static final int       FASE_VALUTAZIONE_TECNICA            = 50;
  //Step 7 del wizard fasi di gara (pagina a scheda)
  public static final int       FASE_CHIUSURA_VALUTAZIONE_TECNICA   = 55;
  //Step 8 del wizard fasi di gara
  public static final int       FASE_APERTURA_OFFERTE_ECONOMICHE    = 60;
  //Step 9 del wizard fasi di gara
  public static final int       FASE_ASTA_ELETTRONICA               = 65;
  //Passo 10 del wizard fasi di gara: fase fittizia a cui non corrisponde nessun
  // valore ufficiale di FASGAR. Serve solo per visualizzare la pagina denominata
  // 'Asta elettronica' sempre all'interno del wizard delle fasi di gara
  public static final int       FASE_CALCOLO_AGGIUDICAZIONE         = 70;
  // Passo 11 del wizard fasi di gara: fase fittizia a cui non corrisponde nessun
  // valore ufficiale di FASGAR. Serve solo per visualizzare la pagina riassuntiva
  // denominata 'Aggiudicazione provvisoria' sempre all'interno del wizard delle
  // fasi di gara (pagina a scheda)
  public static final int       FASE_AGGIUDICAZIONE_PROVVISORIA     = 75;
  // Passo 12 del wizard fasi di gara (pagina a scheda)
  public static final int       FASE_AGGIUDICAZIONE_DEFINITIVA      = 80;

  private static final String[] TITOLO_FASI_GARA                    = new String[] {
    "Arrivo offerte", "Apertura doc. amministr.", "Sorteggio controllo requisiti",
    "Chiusura verifica doc. amministr.", "Esito controllo sorteggiate",
    "Conclusione comprova requisiti", "Valutazione tecnica","Chiusura valutazione tecnica",
    "Apertura off. economiche", "Asta elettronica", "Calcolo aggiudicazione",
    "Proposta di aggiudicazione"};


  private String paginaFasiGara = null;

  PgManagerEst1 pgManagerEst1=null;

  ControlliOepvManager controlliOepvManager = null;

  public GestioneFasiGaraFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    GestioneFasiGaraFunction.setPaginazione(pageContext,false);

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
        "tabellatiManager", pageContext, TabellatiManager.class);
    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        pageContext, PgManager.class);

     pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        pageContext, PgManagerEst1.class);

     controlliOepvManager = (ControlliOepvManager) UtilitySpring.getBean("controlliOepvManager",
         pageContext, ControlliOepvManager.class);

    MEPAManager mepaManager = (MEPAManager) UtilitySpring.getBean("mepaManager", pageContext, MEPAManager.class);

    String codiceGara = UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
    codiceGara = codiceGara.substring(codiceGara.indexOf(":") + 1);

    // Determino se la gara e' una gara a lotti omogenea e se e' una gara per
    // lavori
    Boolean isGaraLottiOmogenea = null;
    String isGaraPerLavori = null;
    boolean isGaraLottiConOffertaUnica = false;
    //La normativa non prevede più il controllo dei requisiti, quindi l flag
    //isSorteggioControlloRequisiti assume sempre il valore false, qualunque
    //sia il valore di compreq
    boolean isSorteggioControlloRequisiti = false;
    boolean isGaraTelematica = false;
    String compreq = null;
    String ricastae = null;
    Long iterga = null;
    String calcsome = null;

    paginaFasiGara = pageContext.getRequest().getParameter("paginaFasiGara");
    //String bustalotti = (String)params[0];
    String bustalotti = UtilityTags.getParametro(pageContext,"bustalotti");

    // Nell'ultima pagina dell'Apertura documentazione Amministrativa è possibile
    // attivare/disattivare le offete: l'aggiornamento della fase avviene
    // nel gestore di salvataggio, ma se tutto va bene occorre indicare una
    // variabile nel request in modo da permettere alla pagina di visualizzarsi
    // correttamente (in caso di attivazione offerte, si passa in automatico alla
    // pagina dell'apertura offerte)
    String chiusuraAperturaDocAmm = UtilityTags.getParametro(
        pageContext, "CHIUSURA_APERTURA_FASI");
    if (chiusuraAperturaDocAmm != null && chiusuraAperturaDocAmm.length() > 0) {
      String esitoOperazione = null;
      if ("ATTIVA".equals(chiusuraAperturaDocAmm)) {
        if (UtilityTags.checkProtection(pageContext,
            "PAGE.VIS.GARE.GARE-scheda.FASIGARA", true))
          esitoOperazione = "OFFERTE_ATTIVATE";
      } else if ("DISATTIVA".equals(chiusuraAperturaDocAmm)) {
        esitoOperazione = "OFFERTE_DISATTIVATE";
      }
      pageContext.setAttribute("RISULTATO", esitoOperazione);
    }


    try {
      Vector<?> obj = sqlManager.getVector(
          "select TIPTOR, TIPGEN, COMPREQ, OFFTEL, GARTEL, ITERGA, RIBCAL, CALCSOME from TORN, GARE "
              + "where TORN.CODGAR = GARE.CODGAR1 "
              + "and GARE.NGARA = ?", new Object[] { codiceGara });
      if (obj.get(0) != null) {
        String tmpTipoTornata = ((JdbcParametro) obj.get(0)).getStringValue();
        if ("1".equals(tmpTipoTornata))
          isGaraLottiOmogenea = Boolean.TRUE;
        else
          isGaraLottiOmogenea = Boolean.FALSE;

        pageContext.setAttribute("garaLottiOmogenea",
            isGaraLottiOmogenea.toString(), PageContext.REQUEST_SCOPE);
      }
      if (obj.get(1) != null) {
        String tmp1 = ((JdbcParametro) obj.get(1)).getStringValue();
        if ("1".equals(tmp1))
          isGaraPerLavori = "true";
        else
          isGaraPerLavori = "false";

        pageContext.setAttribute("garaPerLavori", isGaraPerLavori,
            PageContext.REQUEST_SCOPE);
      }
      if (obj.get(2) != null) {
        compreq = ((JdbcParametro) obj.get(2)).getStringValue();
        if("1".equals(compreq))

        pageContext.setAttribute("compreq", compreq,
            PageContext.REQUEST_SCOPE);
      }
      if (obj.get(3) != null) {
        String offtel = ((JdbcParametro) obj.get(3)).getStringValue();
        pageContext.setAttribute("offtel", offtel,
            PageContext.REQUEST_SCOPE);

      }
      if (obj.get(4) != null) {
        String garatelamtica = ((JdbcParametro) obj.get(4)).getStringValue();
        if("1".equals(garatelamtica))
          isGaraTelematica=true;
      }

      if (obj.get(5) != null) {
    	iterga = ((JdbcParametro) obj.get(5)).longValue();
        pageContext.setAttribute("iterga", iterga,
            PageContext.REQUEST_SCOPE);
      }

      if (obj.get(6) != null) {
        Long ribcal = ((JdbcParametro) obj.get(6)).longValue();
         pageContext.setAttribute("ribcal", ribcal,
             PageContext.REQUEST_SCOPE);
       }

      if (obj.get(7) != null) {
           calcsome = ((JdbcParametro) obj.get(7)).getStringValue();
           pageContext.setAttribute("calcoloSogliaAnomaliaExDLgs2017", calcsome,
               PageContext.REQUEST_SCOPE);
        }

      // Estrazione del campo GARE.GENERE con una query specifica, in modo da
      // estrarre tale campo SEMPRE dall'occorrenza complementare (se esistente),
      // altrimenti si estrae null
      Long genereGaraOccorrenzaComplementare = (Long) sqlManager.getObject(
  				"select GENERE " +
  				  "from GARE " +
  				 "where GARE.NGARA = GARE.CODGAR1 " +
  				   "and GARE.CODGAR1 = (select CODGAR1 from GARE where GARE.NGARA = ?)",
  				new Object[]{codiceGara});
    	if (genereGaraOccorrenzaComplementare != null &&
    			genereGaraOccorrenzaComplementare.longValue() == 3 && !"aperturaOffAggProvLottoOffUnica".equals(paginaFasiGara)){
    	  isGaraLottiConOffertaUnica = true;
    	}


    	pageContext.setAttribute("isGaraLottiConOffertaUnica",
            "" + isGaraLottiConOffertaUnica, PageContext.REQUEST_SCOPE);

    	ricastae = (String)sqlManager.getObject("select ricastae from torn,gare where ngara=? and codgar1=codgar", new Object[]{codiceGara});
    	pageContext.setAttribute("ricastae", ricastae,
            PageContext.REQUEST_SCOPE);
    } catch (SQLException s) {
      throw new JspException(
          "Errore durante la lettura del tipo di tornata della gara ", s);
    } catch (GestoreException e) {
      throw new JspException(
          "Errore durante la lettura dei dati della gara ", e);
    }



    //Verifica se alla gara è stato associato un elenco
    String select ="select elencoe from gare where ngara=?";
    if(isGaraLottiConOffertaUnica)
      select = "select ELENCOE " +
        "from GARE " +
        "where GARE.NGARA = GARE.CODGAR1 " +
        "and GARE.CODGAR1 = (select CODGAR1 from GARE where GARE.NGARA = ?)";
    try {
      String codiceElenco = (String)sqlManager.getObject(select,new Object[]{codiceGara});
      if(codiceElenco!=null && !"".equals(codiceElenco))
        pageContext.setAttribute("codiceElenco",
            codiceElenco, PageContext.REQUEST_SCOPE);
    } catch (SQLException e1) {
      throw new JspException(
          "Errore durante la lettura del campo ELENCOE di GARE ", e1);
    }
    //int faseGara = GestioneFasiGaraFunction.FASE_APERTURA_DOCUM_AMMINISTR;
    int stepWizardFasiGara = GestioneFasiGaraFunction.FASE_APERTURA_DOCUM_AMMINISTR;
    String codiceTornata = null;
    String codiceLotto = new String(codiceGara);

    // Determino se la gara e' a lotto unico, la fase di gara e modalita'
    // aggiudicazione della gara e Aggiud.con esclus.autom.(1) o manuale(2)
    // delle offerte anomale del lotto in analisi
    Boolean garaLottoUnico = null;
    Long modalitaAggiudicazioneGara = null;
    Long aggiudicazioneEsclusAutom = null;
    String aggiudicazioneProvvisoria = null;
    Double limmax=null;
    Double media = null;
    boolean visOffertaEco=true;
    Long faseGara =null;

    boolean isValtec = false;
    //Se gara a Lotto unico o Offerte distinte si considera GARE1.VALTEC, altrimenti
    //si deve controllare se fra i lotti ve n'è uno con Valtec =1
    String valtec=null;
    try {
      if(!isGaraLottiConOffertaUnica){
        valtec = (String)sqlManager.getObject("select valtec from gare1 where ngara=?", new Object[]{codiceGara});
      }else{
        // Conteggio del numero di lotti, di una gara divisa in lotti con
        // offerta unica con GARE1.VALTEC=1.
        Long numeroLottiVALTEC = (Long) sqlManager.getObject(
                "select count(*) from gare1 " +
                 "where codgar1 = ? " +
                   "and ngara!=codgar1 " +
                   "and valtec = '1'", new Object[]{codiceGara});

        // Se almeno un lotto di tale gara ha VALTEC=1, allora le fasi
        // FASE_APERTURA_OFFERTE_TECNICHE e FASE_CHIUSURA_VALUTAZIONE_TECNICA
        // devono essere visibile per tutti i lotti
        if(numeroLottiVALTEC != null && numeroLottiVALTEC.longValue() > 0)
          valtec="1";
      }
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura del campo GARE1.VALTEC", e);
    }
    if ("1".equals(valtec) )
      isValtec = true;
    pageContext.setAttribute("attivaValutazioneTec",
        isValtec, PageContext.REQUEST_SCOPE);

    try {
      Vector<?> datiGare1 = sqlManager.getVector("select ULTDETLIC, RIPTEC, RIPECO from gare1 where ngara=?", new Object[]{codiceGara});
      if(datiGare1!=null && datiGare1.size()>0){
        Long ULTDETLIC = SqlManager.getValueFromVectorParam(datiGare1, 0).longValue();
        Long RIPTEC = SqlManager.getValueFromVectorParam(datiGare1, 1).longValue();
        Long RIPECO = SqlManager.getValueFromVectorParam(datiGare1, 2).longValue();
        pageContext.setAttribute("ULTDETLIC",
            ULTDETLIC, PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("RIPTEC",
            RIPTEC, PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("RIPECO",
            RIPECO, PageContext.REQUEST_SCOPE);
      }



      Vector<?> datiLotto = sqlManager.getVector(
          "select CODGAR1, FASGAR, MODLICG, MODASTG, DITTAP, STEPGAR, CALCSOANG, LIMMAX, MEDIA, DETLICG from GARE " +
           "where GARE.NGARA = ?", new Object[] { codiceGara });
      if (datiLotto != null && datiLotto.size() > 0) {
        codiceTornata = ((JdbcParametro) datiLotto.get(0)).getStringValue();

        if (codiceTornata.startsWith("$"))
          garaLottoUnico = Boolean.TRUE;
        else
          garaLottoUnico = Boolean.FALSE;

        pageContext.setAttribute("garaLottoUnico", garaLottoUnico,
            PageContext.REQUEST_SCOPE);

        if (((JdbcParametro) datiLotto.get(1)).getValue() != null){
          faseGara =  ((JdbcParametro) datiLotto.get(1)).longValue();
          pageContext.setAttribute("faseGara", faseGara,
              PageContext.REQUEST_SCOPE);
        }

        if (((JdbcParametro) datiLotto.get(2)).getValue() != null)
          modalitaAggiudicazioneGara = (Long) ((JdbcParametro)
                  datiLotto.get(2)).getValue();

        if(isGaraLottiConOffertaUnica){
          // Conteggio del numero di lotti, di una gara divisa in lotti con
          // offerta unica, di tipo 'OEPV' (GARE.MODLICG = 6).
          Long numeroLottiOEPV = (Long) sqlManager.getObject(
                  "select count(*) from gare " +
                   "where codgar1 = ? " +
                     "and genere is null " +
                     "and modlicg = 6", new Object[]{codiceGara});
          // Se almeno un lotto di tale gara e' di tipo OEPV, allora la fase
          // FASE_APERTURA_OFFERTE_TECNICHE deve essere visibile per tutti i
          // lotti e quindi l'oggetto modalitaAggiudicazioneGara viene posto
          // comunque pari a new Long(6)
          if(numeroLottiOEPV != null && numeroLottiOEPV.longValue() > 0){
              modalitaAggiudicazioneGara = new Long(6);
              pageContext.setAttribute("numeroLottiOEPV",    numeroLottiOEPV,
                  PageContext.REQUEST_SCOPE);
          }
      }

        visOffertaEco = pgManagerEst1.gestioneOffertaEconomicaDaCostofisso(codiceGara, bustalotti);
        pageContext.setAttribute("visOffertaEco", new Boolean(visOffertaEco), PageContext.REQUEST_SCOPE);

        if (((JdbcParametro) datiLotto.get(5)).getValue() != null){
          stepWizardFasiGara = ((Long) ((JdbcParametro) datiLotto.get(5)).getValue()).intValue();  //faseGara = ((Long) ((JdbcParametro) datiLotto.get(1)).getValue()).intValue();
          int stepgar = stepWizardFasiGara;
          pageContext.setAttribute("stepgar", new Long(stepgar),
              PageContext.REQUEST_SCOPE);

          // Se FASGAR >=7 vi è il blocco in sola visualizzazione dei dati di
          // tutte le fasi di gare tranne le ultime due
          if (stepWizardFasiGara >= FASE_CALCOLO_AGGIUDICAZIONE){ //if (faseGara >= FASE_CALCOLO_AGGIUDICAZIONE){
	      	  pageContext.setAttribute("bloccoAggiudicazione", new Long(1),
	      	  		PageContext.REQUEST_SCOPE);
	      	  if(isGaraLottiConOffertaUnica && codiceGara.equals(codiceTornata)){
	      	  	// La condizione codiceGara.equals(codiceTornata) serve per distinguere
	      	  	// se si sta usando le fasi di gara per l'occorrenze complementare
	      	  	// (in cui codiceGara e codiceTornata coincidono) o per i
	      	  	// specifici lotti (in cui codiceGara e codiceTornata sono diversi)
	      	  	if(visOffertaEco)
	      	  	  stepWizardFasiGara = FASE_APERTURA_OFFERTE_ECONOMICHE;  //faseGara = FASE_APERTURA_OFFERTE_ECONOMICHE;
	      	  	else
	      	  	  stepWizardFasiGara = FASE_CHIUSURA_VALUTAZIONE_TECNICA;
	      	  }
	      } else{
	  	        pageContext.setAttribute("bloccoAggiudicazione", new Long(0),
	  	                PageContext.REQUEST_SCOPE);
	      }
          if(("aperturaDocAmm".equals(paginaFasiGara) || "aperturaOffAggProvOffUnica".equals(paginaFasiGara)) && stepWizardFasiGara > FASE_CONCLUSIONE_COMPROVA_REQUISITI){
              stepWizardFasiGara = FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA;
          }
        }else{
          //Nel caso di gara ad offerta unica con bustalotti=1 si considerano i lotti, quindi dopo che si attiva
          //la pagina Apertura offerte e aggiudicazione provvisoria i lotti hanno questi hanno stpegar=null, si deve
          //impostare il valore della fase minima, però c'è da gestire il fatto che gli step "Valutazione tecnica" e
          //"Chiusura valutazione tecnica" sono visibili solo se modalitaAggiudicazioneGara= 6 o isValtec=true
          if("aperturaOffAggProvLottoOffUnica".equals(paginaFasiGara)){
            if((modalitaAggiudicazioneGara!=null && modalitaAggiudicazioneGara.longValue()==6) || isValtec)
              stepWizardFasiGara = FASE_VALUTAZIONE_TECNICA;
            else
              stepWizardFasiGara = FASE_APERTURA_OFFERTE_ECONOMICHE;
          }

        }


	      if(modalitaAggiudicazioneGara != null)
	      	pageContext.setAttribute("modalitaAggiudicazioneGara",
            modalitaAggiudicazioneGara, PageContext.REQUEST_SCOPE);

        // MOD SS041209 - Se il campo MODASTG non è valorizzato, si comporta
	      // come se valesse 2, cioe' applica la verifica congruita'
        if (((JdbcParametro) datiLotto.get(3)).getValue() != null){
          aggiudicazioneEsclusAutom = (Long) ((JdbcParametro) datiLotto.get(3)).getValue();
          if (aggiudicazioneEsclusAutom.longValue() == 0)
        	  aggiudicazioneEsclusAutom = new Long(2);
        } else
      	  aggiudicazioneEsclusAutom = new Long(2);

        pageContext.setAttribute("aggiudicazioneEsclusAutom",
              aggiudicazioneEsclusAutom, PageContext.REQUEST_SCOPE);

        if (((JdbcParametro) datiLotto.get(4)).getValue() != null) {
        	aggiudicazioneProvvisoria = (String) ((JdbcParametro) datiLotto.get(4)).getValue();
            pageContext.setAttribute("aggiudicazioneProvvisoria",
            		aggiudicazioneProvvisoria, PageContext.REQUEST_SCOPE);
        }

        if (((JdbcParametro) datiLotto.get(6)).getValue() != null) {
        	String calcoloSogliaAnomalia = ((JdbcParametro) datiLotto.get(6)).getStringValue();
            pageContext.setAttribute("calcoloSogliaAnomalia",
            		calcoloSogliaAnomalia, PageContext.REQUEST_SCOPE);
        }

        Double mediaScarti = null;
        if (((JdbcParametro) datiLotto.get(7)).getValue() != null)
          limmax = ((JdbcParametro) datiLotto.get(7)).doubleValue();

        if (((JdbcParametro) datiLotto.get(8)).getValue() != null)
          media = ((JdbcParametro) datiLotto.get(8)).doubleValue();

        if(limmax!= null && limmax.doubleValue()!=0){
          if(media== null)
            media = new Double(0);
          mediaScarti = new Double(limmax.doubleValue() - media.doubleValue());
        }else
          mediaScarti = new Double(0);

        pageContext.setAttribute("mediaScarti",
            mediaScarti, PageContext.REQUEST_SCOPE);

        if (((JdbcParametro) datiLotto.get(9)).getValue() != null){
          pageContext.setAttribute("detlicg",
              ((JdbcParametro) datiLotto.get(9)).longValue(), PageContext.REQUEST_SCOPE);
        }
      }





    } catch (SQLException s) {
      throw new JspException(
          "Errore durante la lettura di dati supplementari del lotto", s);
    } catch (GestoreException e) {
      throw new JspException(
          "Errore durante la lettura di dati supplementari del lotto", e);
    }

    String paginaAttivaWizard = UtilityTags.getParametro(pageContext,
        GestioneFasiGaraFunction.PARAM_WIZARD_PAGINA_ATTIVA);
    String direzioneWizard = UtilityTags.getParametro(pageContext,
        GestioneFasiRicezioneFunction.PARAM_DIREZIONE_WIZARD);

    /*
    if("aperturaOffAggProvLottoOffUnica".equals(paginaFasiGara) && (direzioneWizard == null || "".equals(direzioneWizard)) &&
        (paginaAttivaWizard == null || "".equals(paginaAttivaWizard))){
      direzioneWizard = "AVANTI";
      paginaAttivaWizard = "" + stepWizardFasiGara;
    }
    */

    // Inizializzazione della variabile alla fase minima
    int wizardPaginaAttiva = stepWizardFasiGara;
    if(paginaAttivaWizard != null && paginaAttivaWizard.length() > 0)
    	wizardPaginaAttiva = this.calcoloStepWizard(UtilityNumeri.convertiIntero(
    			paginaAttivaWizard).intValue(), isGaraLottiConOffertaUnica,
    			modalitaAggiudicazioneGara,	direzioneWizard, isSorteggioControlloRequisiti,
    			isValtec, ricastae, visOffertaEco);
    paginaAttivaWizard = "" + wizardPaginaAttiva;


    //Gestione Offerta espressa mediante ribasso o prezzo
    if (wizardPaginaAttiva == FASE_APERTURA_OFFERTE_ECONOMICHE && !isGaraLottiConOffertaUnica) {
        pgManager.setDatiCalcoloRibasso(pageContext,sqlManager,codiceGara);
    	pgManager.getOFFAUM(pageContext, sqlManager, codiceGara);
    	pgManager.setDatiCalcoloImportoOfferto(pageContext,codiceGara);
    }



    if(modalitaAggiudicazioneGara != null &&
    			modalitaAggiudicazioneGara.longValue() != 6){
      if (wizardPaginaAttiva == FASE_APERTURA_OFFERTE_ECONOMICHE) {
        String numeroCifreDecimaliRibasso;
        try {
          numeroCifreDecimaliRibasso = this.pgManagerEst1.getNumeroDecimaliRibasso(codiceTornata);
        } catch (SQLException e) {
          throw new JspException("Errore durante la lettura del numero di decimali da usare per il ribasso della gara(NGARA = '" + codiceGara + ")", e);
        }
        pageContext.setAttribute("numeroCifreDecimaliRibasso",numeroCifreDecimaliRibasso);
      } else {
        pageContext.setAttribute("numeroCifreDecimaliPunteggioTecnico",
            tabellatiManager.getDescrTabellato("A1049", "1"));
      }
    } else {
      if (wizardPaginaAttiva == FASE_VALUTAZIONE_TECNICA ||
          wizardPaginaAttiva == FASE_APERTURA_OFFERTE_ECONOMICHE){
        pageContext.setAttribute("numeroCifreDecimaliPunteggioTecnico",
            tabellatiManager.getDescrTabellato("A1049", "1"));
        Double maxPunTecnico;
        try {
          maxPunTecnico = pgManager.getSommaPunteggioTecnico(codiceGara);
          if (maxPunTecnico == null)
            maxPunTecnico = new Double(-1000);

          if (maxPunTecnico != null)
            pageContext.setAttribute("punteggioTecnico",
                UtilityNumeri.convertiDouble(maxPunTecnico,
                    UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
                PageContext.REQUEST_SCOPE);
        } catch (SQLException e) {
          throw new JspException("Errore durante il calcolo della somma dei " +
              "punteggi tecnici della gara (NGARA = '" + codiceGara + ")", e);
        }
      }

      if (wizardPaginaAttiva == FASE_VALUTAZIONE_TECNICA){

        // Determino il totale del punteggio tecnico della gara
        Double sogliaTecnicaMinima = null;
        try {

          Double puntecSex[] = pgManager.getSommaPunteggiTecniciSez(codiceGara);
          if (puntecSex[0] != null)
            pageContext.setAttribute("punteggioTecnicoQualitativo",
                UtilityNumeri.convertiDouble(puntecSex[0],
                    UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
                        PageContext.REQUEST_SCOPE);
          if (puntecSex[1] != null)
            pageContext.setAttribute("punteggioTecnicoQuantitativo",
                UtilityNumeri.convertiDouble(puntecSex[1],
                    UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
                        PageContext.REQUEST_SCOPE);

          Long statocg = null;
          Vector datiGare1 = sqlManager.getVector("select mintec,riptec, ripcritec, statocg from gare1 where ngara=?", new Object[]{codiceGara});
          if(datiGare1!=null && datiGare1.size()>0){
            sogliaTecnicaMinima = SqlManager.getValueFromVectorParam(datiGare1, 0).doubleValue();
            Long riptec = SqlManager.getValueFromVectorParam(datiGare1, 1).longValue();
            Long ripcritec = SqlManager.getValueFromVectorParam(datiGare1, 2).longValue();
            statocg = SqlManager.getValueFromVectorParam(datiGare1, 3).longValue();
            if(riptec==null)
              riptec = new Long(3);

            String msg ="";
            msg += tabellatiManager.getDescrTabellato("A1144", riptec.toString());
            if((riptec.longValue() ==1 || riptec.longValue()==2) && ripcritec != null){
              msg += "&nbsp;&nbsp;&nbsp;&nbsp;<b>Criterio:</b> " + tabellatiManager.getDescrTabellato("A1145", ripcritec.toString());
            }
            pageContext.setAttribute("msgRiptec",msg,PageContext.REQUEST_SCOPE);

          }
          if("2".equals(bustalotti)) {
            Long conteggioStatocg1=(Long)sqlManager.getObject("select count(*) from gare1 where codgar1=? and statocg=1", new Object[]{codiceGara});
            if(conteggioStatocg1!=null && conteggioStatocg1.longValue()>0)
              statocg= new Long(1);
          }

          pageContext.setAttribute("STATOCG",statocg,PageContext.REQUEST_SCOPE);
          if (sogliaTecnicaMinima != null)
            pageContext.setAttribute("sogliaTecnicaMinima",
                UtilityNumeri.convertiDouble(sogliaTecnicaMinima,
                    UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
                PageContext.REQUEST_SCOPE);


        } catch (Exception e) {
          throw new JspException("Errore durante il calcolo della somma dei " +
              "punteggi tecnici della gara (NGARA = '" + codiceGara + ")", e);
        }

        String updateLista = pageContext.getRequest().getParameter("updateLista");
        if(!"1".equals(updateLista)){
          this.esistonoDitteConPunteggio(codiceGara,"TEC",pageContext);
          if(isGaraTelematica){
            try {
              boolean esitoControlloPunteggiSopraSogia = mepaManager.esitoControlloPunteggiTotaliDitteSogliaMinima(codiceGara, sogliaTecnicaMinima, new Long(1), null);
              pageContext.setAttribute("esitoControlloPunteggiTecSopraSogia", new Boolean(esitoControlloPunteggiSopraSogia),PageContext.REQUEST_SCOPE);
            } catch (SQLException e) {
              throw new JspException("Errore durante il controllo dei punteggi totali tecnici delle ditte rispetto alla soglia minima  " +
                  "della gara (NGARA = '" + codiceGara + ")", e);
            } catch (GestoreException e) {
              throw new JspException("Errore durante il controllo dei punteggi totali tecnici delle ditte rispetto alla soglia minima  " +
                  "della gara (NGARA = '" + codiceGara + ")", e);
            }
          }
        }

        try {
          boolean formatiDef = controlliOepvManager.checkQualuqueFormatoDefinito(codiceGara, new Long(1));
          if(formatiDef){
            pageContext.setAttribute("formatiTecniciDefiniti","true");
          }else{
            pageContext.setAttribute("formatiTecniciDefiniti","false");
          }
        } catch (SQLException e) {
            throw new JspException(
              "Errore durante la lettura del formato dei criteri ", e);
        }

      } else if(wizardPaginaAttiva == FASE_APERTURA_OFFERTE_ECONOMICHE) {
        String numeroCifreDecimaliRibasso;
        try {
          numeroCifreDecimaliRibasso = this.pgManagerEst1.getNumeroDecimaliRibasso(codiceTornata);
        } catch (SQLException e) {
          throw new JspException("Errore durante la lettura del numero di decimali da usare per il ribasso della gara(NGARA = '" + codiceGara + ")", e);
        }
        pageContext.setAttribute("numeroCifreDecimaliRibasso",numeroCifreDecimaliRibasso);

        try {
          // Determino il totale del punteggio economico della gara
          Double maxPunEconomico = pgManager.getSommaPunteggioEconomico(codiceGara);

          if (maxPunEconomico == null)
            maxPunEconomico = new Double(-1000);

          if (maxPunEconomico != null)
            pageContext.setAttribute("punteggioEconomico",
                UtilityNumeri.convertiDouble(maxPunEconomico,
                    UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
                        PageContext.REQUEST_SCOPE);

          Double sogliaEconomicaMinima = null;
           Vector datiGare1 = sqlManager.getVector("select mineco,ripeco, ripcrieco, statocg from gare1 where ngara=?", new Object[]{codiceGara});
          if(datiGare1!=null && datiGare1.size()>0){
            sogliaEconomicaMinima = SqlManager.getValueFromVectorParam(datiGare1, 0).doubleValue();
            Long ripeco = SqlManager.getValueFromVectorParam(datiGare1, 1).longValue();
            Long ripcrieco = SqlManager.getValueFromVectorParam(datiGare1, 2).longValue();
            Long statocg = SqlManager.getValueFromVectorParam(datiGare1, 3).longValue();
            if(ripeco==null)
              ripeco = new Long(3);

            String msg ="";
            msg += tabellatiManager.getDescrTabellato("A1144", ripeco.toString());
            if((ripeco.longValue() ==1 || ripeco.longValue()==2) && ripeco != null){
              msg += "&nbsp;&nbsp;&nbsp;&nbsp;<b>Criterio:</b> " + tabellatiManager.getDescrTabellato("A1145", ripcrieco.toString());
            }
            pageContext.setAttribute("msgRipeco",msg,PageContext.REQUEST_SCOPE);
            pageContext.setAttribute("STATOCG",statocg,PageContext.REQUEST_SCOPE);
          }
          if (sogliaEconomicaMinima != null)
            pageContext.setAttribute("sogliaEconomicaMinima",
                UtilityNumeri.convertiDouble(sogliaEconomicaMinima,
                    UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
                PageContext.REQUEST_SCOPE);

        } catch (Exception e) {
          throw new JspException("Errore durante il calcolo della somma dei " +
                "punteggi economici della gara (NGARA = '" + codiceGara + ")",
                e);
        }

        try {
          boolean formatiDef = controlliOepvManager.checkQualuqueFormatoDefinito(codiceGara, new Long(2));
          if(formatiDef){
            pageContext.setAttribute("formatiEconomiciDefiniti","true");
          }else{
            pageContext.setAttribute("formatiEconomiciDefiniti","false");
          }
        } catch (SQLException e) {
            throw new JspException(
              "Errore durante la lettura del formato dei criteri ", e);
        }

        /*
        try {
          boolean esistonoDitteConPunteggio = false;
          if(new Long(6).equals(modalitaAggiudicazioneGara))
            esistonoDitteConPunteggio = pgManagerEst1.esistonoDittePunteggioValorizzato(codiceGara, "ECO");
          pageContext.setAttribute("esistonoDitteConPunteggio", new Boolean(esistonoDitteConPunteggio),PageContext.REQUEST_SCOPE);

        } catch (SQLException e) {
          throw new JspException("Errore durante il controllo dell'esistenza di ditte con  " +
              "punteggi economici della gara (NGARA = '" + codiceGara + ")", e);
        }
        */
        this.esistonoDitteConPunteggio(codiceGara, "ECO", pageContext);
      }


      boolean vecchiaOepv;
      try {
        vecchiaOepv = controlliOepvManager.isVecchiaOepvFromNgara(codiceGara);
        if(vecchiaOepv){
          pageContext.setAttribute("isVecchiaOepv","true");
        }else{
          pageContext.setAttribute("isVecchiaOepv","false");
        }
      } catch (SQLException e) {
          throw new JspException(
            "Errore durante la lettura del formato dei criteri ", e);
      }

      if(vecchiaOepv == false){
        try {
          boolean formato50 = controlliOepvManager.checkFormato(codiceGara, new Long(50));
          if(formato50){
            pageContext.setAttribute("formato50","true");
          }else{
            pageContext.setAttribute("formato50","false");
          }
        } catch (SQLException e) {
            throw new JspException(
              "Errore durante la lettura del formato dei criteri ", e);
        }


        try {
          boolean formato51 = controlliOepvManager.checkFormato(codiceGara, new Long(51));
          if(formato51){
            pageContext.setAttribute("formato51","true");
          }else{
            pageContext.setAttribute("formato51","false");
          }
        } catch (SQLException e) {
            throw new JspException(
              "Errore durante la lettura del formato dei criteri ", e);
        }


        try {
          boolean formato52 = controlliOepvManager.checkFormato(codiceGara, new Long(52));
          if(formato52){
            pageContext.setAttribute("formato52","true");
          }else{
            pageContext.setAttribute("formato52","false");
          }
        } catch (SQLException e) {
            throw new JspException(
              "Errore durante la lettura del formato dei criteri ", e);
        }
      }

    }

    if (wizardPaginaAttiva == FASE_VALUTAZIONE_TECNICA ||
        wizardPaginaAttiva == FASE_APERTURA_OFFERTE_ECONOMICHE){

      try {
        String annoff = (String)sqlManager.getObject("select annoff from detmot where moties=104", null);
        pageContext.setAttribute("annoff",annoff);
      } catch (SQLException e) {
        throw new JspException("Errore nella lettura del campo ANNOF della tabella DETMOT)",
            e);
      }

    }

    if (wizardPaginaAttiva == FASE_VALUTAZIONE_TECNICA && isGaraTelematica && !isGaraLottiConOffertaUnica) {
      //Controlli bloccanti sui punteggi
      try{

        Long modlicg = (Long) sqlManager.getObject("select modlicg from gare where ngara = ?", new Object[] { codiceGara});
        if (modlicg != null && modlicg.longValue() == 6) {
          //Controllo soglie punteggi
          boolean risultato[] = mepaManager.controlloSogliePunteggiDitte(codiceGara, false, sqlManager, new StringBuilder(), new Long(wizardPaginaAttiva).toString(), "offerteEconomiche");
          if(!risultato[2]){
            pageContext.setAttribute("bloccoPunteggiNonTuttiValorizzati","true");
          }else if(!risultato[0]){
            pageContext.setAttribute("bloccoPunteggiFuoriIntervallo","true");
            pageContext.setAttribute("sogliaMinimaCriteriImpostata",risultato[1]);
          }
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo del punteggio tecnico ed economico", e);
      }
    }


    if (wizardPaginaAttiva == FASE_APERTURA_OFFERTE_ECONOMICHE && !isGaraLottiConOffertaUnica) {
      //Caricamento dati per impostare la label per il campo impoff
      try {
        String label="Importo offerto presentato dalla ditta";
        try {
          Vector<?> datiGare = sqlManager.getVector("select impsic, sicinc from gare where ngara=?",new Object[]{codiceGara});
          if(datiGare!=null && datiGare.size()>0){
            Double impsic = SqlManager.getValueFromVectorParam(datiGare, 0).doubleValue();
            String sicinc = SqlManager.getValueFromVectorParam(datiGare, 1).getStringValue();

            if(impsic!=null){
              if("1".equals(sicinc))
                label+=", comprensivo degli oneri sicurezza";
              else
                label+=", non comprensivo degli oneri sicurezza";
            }

          }
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore durante la lettura della tabella GARE", null, e);
        }


        pageContext.setAttribute("labelImpoff",label);
      } catch (GestoreException e) {
        throw new JspException("Errore durante la lettura della tabella GARE", e);
      }
    }

    // Gestione dell'avanzamento del wizard: la variabile paginaAttivaWizard viene
    // passata come argomento alla funzione gestioneAvanzamentoWizard per poterla
    // eventualmente aggiornare dall'omonimo metodo della classe
    // GestioneAggiudProvDefOffertaUnicaFunction che estende questa classe
    paginaAttivaWizard = this.gestioneAvanzamentoWizard(paginaAttivaWizard, pageContext,
        modalitaAggiudicazioneGara, iterga, isGaraLottiConOffertaUnica, isSorteggioControlloRequisiti,
        isValtec, ricastae, visOffertaEco);
    wizardPaginaAttiva = UtilityNumeri.convertiIntero(paginaAttivaWizard).intValue();

    // Gestione del filtro sulle ditte in base all'avanzamento del wizard
    this.gestioneFiltroFaseGara(wizardPaginaAttiva, pageContext);
    pageContext.setAttribute("paginaAttivaWizard", new Integer(paginaAttivaWizard),
        PageContext.REQUEST_SCOPE);

    // Nel request viene messo anche il valore che devono assumere i campi
    // GARE.FASGAR nel caso di salvataggio e DITG.FASGAR nel caso in cui la ditta venga esclusa
    Double d = new Double(Math.floor(new Long(wizardPaginaAttiva).doubleValue()/10));
    pageContext.setAttribute("fasGarPerEsclusioneDitta", new Long(d.longValue()),
    		PageContext.REQUEST_SCOPE);

    String updateLista = UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA);
    if (updateLista == null || updateLista.length() == 0) updateLista = "0";
    pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA,
        updateLista, PageContext.REQUEST_SCOPE);

    //Controlli per bloccare il campo congruo della pagina calcolo aggiudicazione
    if (wizardPaginaAttiva == FASE_CALCOLO_AGGIUDICAZIONE && "1".equals(updateLista)) {
      Boolean bloccoCongruo = new Boolean(false);
      if(aggiudicazioneEsclusAutom.longValue()==1){
        AggiudicazioneManager aggiudicazioneManager = (AggiudicazioneManager) UtilitySpring.getBean(
            "aggiudicazioneManager", pageContext, AggiudicazioneManager.class);
        try {
          // Inizializzazione delle varie HashMap utilizzate
          HashMap hMapTORN = new HashMap();
          HashMap hMapGARE = new HashMap();
          HashMap hMapParametri = new HashMap();
          Long nofval = (Long)sqlManager.getObject("select nofval from gare where ngara=?", new Object[]{codiceGara});
          String legregsic = (String)sqlManager.getObject("select legregsic from gare1 where ngara=?", new Object[]{codiceGara});
          bloccoCongruo = aggiudicazioneManager.initControlliAggiudicazione(codiceGara, codiceTornata, null, legregsic, null, nofval, hMapGARE, hMapTORN, hMapParametri);
        }catch (GestoreException e) {
          throw new JspException("Errore durante la lettura dei dati della gara per determinare se bloccare il campo congruo", e);
        } catch (SQLException e) {
          throw new JspException("Errore durante la lettura dei dati della gara per determinare se bloccare il campo congruo", e);
        }

      }
      pageContext.setAttribute("bloccoCongruo",bloccoCongruo);
    }


    //Nel caso di gara telematica ed offerta unica con bustalotti=2 si devono controllare i partgar di tutti
    // i lotti. Nello step dell'apertura doc amministrativa si deve controllare l'invoff
    if(isGaraTelematica && isGaraLottiConOffertaUnica && (wizardPaginaAttiva == FASE_VALUTAZIONE_TECNICA ||
        wizardPaginaAttiva == FASE_APERTURA_OFFERTE_ECONOMICHE || wizardPaginaAttiva == FASE_APERTURA_DOCUM_AMMINISTR)){
      if(wizardPaginaAttiva == FASE_APERTURA_DOCUM_AMMINISTR){
        boolean controlloInvoffLotti=true;
        try {
          Long conteggio = (Long) sqlManager.getObject("select count(ngara5) from ditg where codgar5=? and ngara5!= codgar5 and (invoff is null or invoff ='') and (fasgar is null or fasgar =0)",new Object[]{codiceGara});
          if(conteggio !=null && conteggio.longValue()>0)
            controlloInvoffLotti= false;
          pageContext.setAttribute("controlloInvoffLotti",
              "" + controlloInvoffLotti, PageContext.REQUEST_SCOPE);
        } catch (SQLException e) {
          throw new JspException(
              "Errore durante la lettura del campo INVOFF dei lotti ", e);
        }
      }else{
        boolean controlloPartgarLotti=true;
        try {
          Long conteggio = (Long) sqlManager.getObject("select count(ngara5) from ditg where codgar5=? and ngara5!= codgar5 and (partgar is null or partgar ='') and (fasgar is null or fasgar =0)",new Object[]{codiceGara});
          if(conteggio !=null && conteggio.longValue()>0)
            controlloPartgarLotti= false;
          pageContext.setAttribute("controlloPartgarLotti",
              "" + controlloPartgarLotti, PageContext.REQUEST_SCOPE);
        } catch (SQLException e) {
          throw new JspException(
              "Errore durante la lettura del campo PARTGAR dei lotti ", e);
        }
      }
    }

    if(isGaraTelematica && (wizardPaginaAttiva == FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA || wizardPaginaAttiva == FASE_VALUTAZIONE_TECNICA)) {
      try {
        boolean garaConcorsoProg=false;
        String isconcprog = (String) sqlManager.getObject("select isconcprog from torn,gare where ngara=? and codgar1=codgar",new Object[]{codiceGara});
        if(wizardPaginaAttiva == FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA) {
          if("1".equals(isconcprog)) {
            //Controlli sulle condizioni per attivare la gestione per gare concorso di progettazione
            if(new Long(2).equals(faseGara) || new Long(3).equals(faseGara) || new Long(4).equals(faseGara)) {
              Long conteggio = (Long) sqlManager.getObject("select count(idcom) from w_invcom where comkey2 = ? and comstato = '6' and comtipo = 'FS11B' and idprg='PA'",new Object[]{codiceGara});
              if(conteggio ==null || new Long(0).equals(conteggio)) {
                garaConcorsoProg=true;
              }
            }
          }

        }
        pageContext.setAttribute("garaConcorsoProg",
            "" + garaConcorsoProg, PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("isconcprog",
              "" + isconcprog, PageContext.REQUEST_SCOPE);
      } catch (SQLException e) {
        throw new JspException(
            "Errore durante la verifica delle condizioni di gara concorso progettazione", e);
      }
    }


    // Creazione del parametro con la chiave da passare alla pagina di controllo
    // delle autorizzazioni
    String inputFiltro = "CODGAR=T:".concat(codiceTornata);
    pageContext.setAttribute("inputFiltro", inputFiltro,
        PageContext.REQUEST_SCOPE);

    // Quando si apre in visualizzazione la lista delle ditte, si rimuove
    // l'eventuale oggetto con chiave
    // GestorePopupFasiGara.CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA
    // presente in sessione, perche', se presente, tutti i dati modificati in
    // esso contenuti sono stati appena salvati, oppure si sono annullate tutte
    // le modifiche
    if(((UtilityTags.SCHEDA_MODO_VISUALIZZA.equalsIgnoreCase(
    		UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO)) ||
    	  UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO) == null) &&
    	 this.getRequest().getSession().getAttribute(
    			GestorePopupFasiGara.CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA) != null))
    		this.getRequest().getSession().removeAttribute(
					GestorePopupFasiGara.CHIAVE_ULTERIORI_CAMPI_POPUP_FASI_RICEZOFF_GARA);

    return null;
  }

  /**
   * Metodo per determinare la pagina attiva del wizard
   *
   * @param faseGara
   * @param paginaAttivaWizard
   * @return
   */
 /* protected int setPaginaAttivaWizard(int faseGara, String paginaAttivaWizard,
  		boolean isGaraLottiConOffertaUnica){
    if(faseGara < GestioneFasiGaraFunction.FASE_APERTURA_DOCUM_AMMINISTR)
      faseGara = GestioneFasiGaraFunction.FASE_APERTURA_DOCUM_AMMINISTR;

    if(isGaraLottiConOffertaUnica && faseGara == FASE_AGGIUDICAZIONE_DEFINITIVA)
    	faseGara++;

    if (paginaAttivaWizard == null ||
    		(paginaAttivaWizard != null && paginaAttivaWizard.length() == 0))
      paginaAttivaWizard = "" + faseGara;

  	return UtilityNumeri.convertiIntero(paginaAttivaWizard).intValue();
  }*/

  /**
   * Setta nel request la property letta per la paginazione delle ditte
   *
   * @param pageContext context dell'applicativo
   * @param elenco
   */
  public static void setPaginazione(PageContext pageContext, boolean elenco) {
    Integer elementiPerPagina = null;
    if(elenco)
      elementiPerPagina = Integer.valueOf(ConfigManager.getValore(
          PROP_PAGINAZIONE_ELENCHI_CATALOGHI));
    else
      elementiPerPagina = Integer.valueOf(ConfigManager.getValore(
          PROP_PAGINAZIONE));
    pageContext.setAttribute(FormTrovaTag.CAMPO_RISULTATI_PER_PAGINA,
        elementiPerPagina, PageContext.REQUEST_SCOPE);
  }

  protected String gestioneAvanzamentoWizard(String paginaAttivaWizard,
      PageContext pageContext, Long modalitaAggiudicazioneGara, Long iterGara,
      boolean isGaraLottiConOffertaUnica, boolean isSorteggioControlloRequisiti,
      boolean isValtec, String ricastae, boolean visOffertaEco) {

  	int wizardPaginaAttiva = UtilityNumeri.convertiIntero(
  			paginaAttivaWizard).intValue();

  	List<String> listaPagineVisitate = new ArrayList<String>();
    List<String> listaPagineDaVisitare = new ArrayList<String>();

    int indicePartenza = 1;
    // Mapping fra la fase di gara attiva (paginaAttivaWizard) e il titolo
    // della fase stessa
    int indiceLimite = 1;
    int indiceFine = TITOLO_FASI_GARA.length;


    if("aperturaDocAmm".equals(paginaFasiGara) || "aperturaOffAggProvOffUnica".equals(paginaFasiGara)){
      indiceFine -= 6;
    }else if("aperturaOffAggProv".equals(paginaFasiGara) || "aperturaOffAggProvLottoOffUnica".equals(paginaFasiGara)){
      indicePartenza = 6;
    }else if("fasiGaraOffUnica".equals(paginaFasiGara))
      indiceFine -=3;

    switch(wizardPaginaAttiva) {
    case FASE_APERTURA_DOCUM_AMMINISTR:
      indiceLimite = 2;
      break;
    case FASE_SORTEGGIO_CONTROLLO_REQUISITI:
      indiceLimite = 3;
      break;
    case FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA:
      indiceLimite = 4;
      break;
    case FASE_ESITO_CONTROLLO_SORTEGGIATE:
      indiceLimite = 5;
      break;
    case FASE_CONCLUSIONE_COMPROVA_REQUISITI:
      indiceLimite = 6;
      break;
    case FASE_VALUTAZIONE_TECNICA:
      if("aperturaDocAmm".equals(paginaFasiGara) || "aperturaOffAggProvOffUnica".equals(paginaFasiGara))
        indiceLimite = 6;
      else
        indiceLimite = 7;
      break;
    case FASE_CHIUSURA_VALUTAZIONE_TECNICA:
      if("aperturaDocAmm".equals(paginaFasiGara) || "aperturaOffAggProvOffUnica".equals(paginaFasiGara))
        indiceLimite = 6;
      else
        indiceLimite = 8;
      break;
    case FASE_APERTURA_OFFERTE_ECONOMICHE:
      if("aperturaDocAmm".equals(paginaFasiGara) || "aperturaOffAggProvOffUnica".equals(paginaFasiGara))
        indiceLimite = 6;
      else
        indiceLimite = 9;
      break;
    case FASE_ASTA_ELETTRONICA:
      indiceLimite = 10;
      break;
    case FASE_CALCOLO_AGGIUDICAZIONE:
      if("aperturaDocAmm".equals(paginaFasiGara) || "aperturaOffAggProvOffUnica".equals(paginaFasiGara))
        indiceLimite = 6;
      else
        indiceLimite = 11;
      break;
    case FASE_AGGIUDICAZIONE_PROVVISORIA:
      indiceLimite = 12;
      break;
    //case FASE_AGGIUDICAZIONE_DEFINITIVA:
    //  indiceLimite = 10;
    //  break;
    }

    for(int i = indicePartenza; i < indiceLimite; i++)
      listaPagineVisitate.add(TITOLO_FASI_GARA[i]);


    for(int i = indiceLimite; i < indiceFine; i++)
   		listaPagineDaVisitare.add(TITOLO_FASI_GARA[i]);

    if((modalitaAggiudicazioneGara == null ||
    	 (modalitaAggiudicazioneGara != null &&
    		modalitaAggiudicazioneGara.longValue() != 6))
    		&& !isValtec){
      listaPagineVisitate.remove(TITOLO_FASI_GARA[6]);
      listaPagineDaVisitare.remove(TITOLO_FASI_GARA[6]);
      listaPagineVisitate.remove(TITOLO_FASI_GARA[7]);
      listaPagineDaVisitare.remove(TITOLO_FASI_GARA[7]);
    }
    if(!isSorteggioControlloRequisiti){
      listaPagineVisitate.remove(TITOLO_FASI_GARA[5]);
      listaPagineDaVisitare.remove(TITOLO_FASI_GARA[5]);
      listaPagineVisitate.remove(TITOLO_FASI_GARA[4]);
      listaPagineDaVisitare.remove(TITOLO_FASI_GARA[4]);
      listaPagineVisitate.remove(TITOLO_FASI_GARA[2]);
      listaPagineDaVisitare.remove(TITOLO_FASI_GARA[2]);
    }
    if(!"1".equals(ricastae)){
      listaPagineVisitate.remove(TITOLO_FASI_GARA[9]);
      listaPagineDaVisitare.remove(TITOLO_FASI_GARA[9]);
    }

    if(!visOffertaEco){
      listaPagineVisitate.remove(TITOLO_FASI_GARA[8]);
      listaPagineDaVisitare.remove(TITOLO_FASI_GARA[8]);
    }

    if(Long.valueOf(8).equals(iterGara)) {
        listaPagineVisitate.remove(TITOLO_FASI_GARA[10]);
        listaPagineDaVisitare.remove(TITOLO_FASI_GARA[10]);
        listaPagineVisitate.remove(TITOLO_FASI_GARA[11]);
        listaPagineDaVisitare.remove(TITOLO_FASI_GARA[11]);
    }

    pageContext.setAttribute("pagineVisitate", listaPagineVisitate);
    pageContext.setAttribute("pagineDaVisitare", listaPagineDaVisitare);

    return paginaAttivaWizard;
  }

  protected List<String> getTitoliWizard(){
  	ArrayList<String> lista = new ArrayList<String>();
  	for(int i=0; i<TITOLO_FASI_GARA.length; i++)
  		lista.add(TITOLO_FASI_GARA[i]);
  	return lista;
  }

  private void gestioneFiltroFaseGara(int faseGaraAttiva,
      PageContext pageContext) {
    StringBuffer result = new StringBuffer("");

    switch (faseGaraAttiva) {
    case GestioneFasiGaraFunction.FASE_APERTURA_DOCUM_AMMINISTR:
      result.append("and (DITG.INVOFF in ('0', '1') or DITG.INVOFF is null) "
          + "and (DITG.FASGAR > 1 or DITG.FASGAR = 0 or DITG.FASGAR is null)");
      break;
    case GestioneFasiGaraFunction.FASE_SORTEGGIO_CONTROLLO_REQUISITI:
      result.append("and (DITG.INVOFF in ('0', '1') or DITG.INVOFF is null) "
          + "and (DITG.FASGAR > 2 or DITG.FASGAR = 0 or DITG.FASGAR is null)");
      break;
    case GestioneFasiGaraFunction.FASE_ESITO_CONTROLLO_SORTEGGIATE:
      result.append("and DITG.ESTIMP = '1'");
      break;
    case GestioneFasiGaraFunction.FASE_VALUTAZIONE_TECNICA:
      result.append("and (DITG.INVOFF in ('0', '1') or DITG.INVOFF is null) "
          + "and (DITG.FASGAR > 4 or DITG.FASGAR = 0 or DITG.FASGAR is null)");
      break;
    case GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE:
      result.append("and (DITG.INVOFF in ('0', '1') or DITG.INVOFF is null) "
          + "and (DITG.FASGAR > 5 or DITG.FASGAR = 0 or DITG.FASGAR is null)");
      break;
    case GestioneFasiGaraFunction.FASE_CALCOLO_AGGIUDICAZIONE:
      result.append(" and DITG.STAGGI > 1");
      break;
    }

    pageContext.setAttribute("filtroFaseGara", result.toString(),
        PageContext.REQUEST_SCOPE);
  }



  protected int calcoloStepWizard(int faseGara, boolean isGaraLottiConOffertaUnica,
  		Long modalitaAggiudicazioneGara, String direzioneWizard, boolean isSorteggioControlloRequisiti,
  		boolean isValtec, String ricastae, boolean visOffertaEco ){

    if(direzioneWizard != null && direzioneWizard.length() > 0){
	  	if(direzioneWizard.equalsIgnoreCase("AVANTI"))
	  		return this.getStepWizardSuccessivo(faseGara, modalitaAggiudicazioneGara,
	  				isGaraLottiConOffertaUnica, isSorteggioControlloRequisiti,isValtec,ricastae, visOffertaEco);
	  	else if(direzioneWizard.equalsIgnoreCase("INDIETRO"))
	  		return this.getStepWizardPrecedente(faseGara, modalitaAggiudicazioneGara,
	  				isGaraLottiConOffertaUnica,isSorteggioControlloRequisiti, isValtec,ricastae, visOffertaEco );
	  	else return FASE_APERTURA_DOCUM_AMMINISTR;
  	} else return faseGara;
  }

  /**
   * @param faseGara
   * @param isGaraLottiConOffertaUnica
   * @return Ritorna lo step del wizard delle fasi di ricezione successiva a
   * 				 quella ricevuta dall'argomento
   */
  private int getStepWizardSuccessivo(int faseGara, Long modalitaAggiudicazioneGara,
  		boolean isGaraLottiConOffertaUnica, boolean isSorteggioControlloRequisiti,
  		boolean isValtec, String ricastae, boolean visOffEco){
  	int result = FASE_APERTURA_DOCUM_AMMINISTR;

  	if(isGaraLottiConOffertaUnica){
	  	if(faseGara < FASE_APERTURA_DOCUM_AMMINISTR)
	  		result = FASE_APERTURA_DOCUM_AMMINISTR;
	  	else if(faseGara > FASE_AGGIUDICAZIONE_DEFINITIVA)
	  		result = FASE_AGGIUDICAZIONE_DEFINITIVA;
	  	else {
	  		switch(faseGara){
	  		case FASE_APERTURA_DOCUM_AMMINISTR:
    	  		if(isSorteggioControlloRequisiti)
                   result = FASE_SORTEGGIO_CONTROLLO_REQUISITI;
               else
                   result = FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA;
	  		    break;
	  		case FASE_SORTEGGIO_CONTROLLO_REQUISITI:
	  			result = FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA;
	  			break;
	  		case FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA:
    	  		if(isSorteggioControlloRequisiti)
                  result = FASE_ESITO_CONTROLLO_SORTEGGIATE;
                else{
                  if((modalitaAggiudicazioneGara == null ||
                      (modalitaAggiudicazioneGara != null &&
                         modalitaAggiudicazioneGara.longValue() != 6)) &&
                         !isValtec)
                     result = FASE_APERTURA_OFFERTE_ECONOMICHE;
                 else
                     result = FASE_VALUTAZIONE_TECNICA;
                }
    	  		break;
	  		case FASE_ESITO_CONTROLLO_SORTEGGIATE:
	  			result = FASE_CONCLUSIONE_COMPROVA_REQUISITI;
	  			break;
	  		case FASE_CONCLUSIONE_COMPROVA_REQUISITI:
	  			if((modalitaAggiudicazioneGara == null ||
	  		    	 (modalitaAggiudicazioneGara != null &&
	  		    		modalitaAggiudicazioneGara.longValue() != 6)) &&
	  		    		!isValtec)
	  				result = FASE_APERTURA_OFFERTE_ECONOMICHE;
	  			else
	  				result = FASE_VALUTAZIONE_TECNICA;
	  			break;
	  		case FASE_VALUTAZIONE_TECNICA:
              if((modalitaAggiudicazioneGara == null ||
                (modalitaAggiudicazioneGara != null &&
                 modalitaAggiudicazioneGara.longValue() != 6)) &&
                 !isValtec)
                   result = FASE_APERTURA_OFFERTE_ECONOMICHE;
               else
                   result = FASE_CHIUSURA_VALUTAZIONE_TECNICA;
              break;
            case FASE_CHIUSURA_VALUTAZIONE_TECNICA:
              if(visOffEco)
                result = FASE_APERTURA_OFFERTE_ECONOMICHE;
              else{
                if("1".equals(ricastae))
                  result = FASE_ASTA_ELETTRONICA;
                else
                  result = FASE_CALCOLO_AGGIUDICAZIONE;
              }
              break;
	  		case FASE_APERTURA_OFFERTE_ECONOMICHE:
	  		  if("1".equals(ricastae))
                result = FASE_ASTA_ELETTRONICA;
	  		  else
                result = FASE_CALCOLO_AGGIUDICAZIONE;
                 break;
	  		case FASE_ASTA_ELETTRONICA:
                 result = FASE_CALCOLO_AGGIUDICAZIONE;
                 break;
	  		case FASE_CALCOLO_AGGIUDICAZIONE:
	  			result = FASE_AGGIUDICAZIONE_PROVVISORIA;
	  			break;
	  		case FASE_AGGIUDICAZIONE_PROVVISORIA:
	  			result = FASE_AGGIUDICAZIONE_DEFINITIVA;
	  			break;
	  		case FASE_AGGIUDICAZIONE_DEFINITIVA:
	  			result = FASE_AGGIUDICAZIONE_DEFINITIVA + 1; // Nelle gare a lotti con offerta unica esiste un ulteriore passo successivo. E' una pagina a scheda
	  			break;
	  		case (FASE_AGGIUDICAZIONE_DEFINITIVA + 1):
	  			result = FASE_AGGIUDICAZIONE_DEFINITIVA + 1;
	  			break;
	  		}
	  	}
  	} else {
  		if(faseGara < FASE_APERTURA_DOCUM_AMMINISTR)
	  		result = FASE_APERTURA_DOCUM_AMMINISTR;
	  	else if(faseGara > FASE_AGGIUDICAZIONE_DEFINITIVA)
	  		result = FASE_AGGIUDICAZIONE_DEFINITIVA;
	  	else {
	  		switch(faseGara){
	  		case FASE_APERTURA_DOCUM_AMMINISTR:
    	  		if(isSorteggioControlloRequisiti)
                  result = FASE_SORTEGGIO_CONTROLLO_REQUISITI;
                else
                  result = FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA;
               break;
	  		case FASE_SORTEGGIO_CONTROLLO_REQUISITI:
	  			result = FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA;
	  			break;
	  		case FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA:
    	  		if(isSorteggioControlloRequisiti)
                  result = FASE_ESITO_CONTROLLO_SORTEGGIATE;
                else{
                  if((modalitaAggiudicazioneGara == null ||
                      (modalitaAggiudicazioneGara != null &&
                         modalitaAggiudicazioneGara.longValue() != 6)) &&
                         !isValtec)
                     result = FASE_APERTURA_OFFERTE_ECONOMICHE;
                 else
                     result = FASE_VALUTAZIONE_TECNICA;
                }

	  			break;
	  		case FASE_ESITO_CONTROLLO_SORTEGGIATE:
	  			result = FASE_CONCLUSIONE_COMPROVA_REQUISITI;
	  			break;
	  		case FASE_CONCLUSIONE_COMPROVA_REQUISITI:
	  			if((modalitaAggiudicazioneGara == null ||
	  		    	 (modalitaAggiudicazioneGara != null &&
	  		    		modalitaAggiudicazioneGara.longValue() != 6)) &&
	  		    		!isValtec)
	  				result = FASE_APERTURA_OFFERTE_ECONOMICHE;
	  			else
	  				result = FASE_VALUTAZIONE_TECNICA;
	  			break;
	  		case FASE_VALUTAZIONE_TECNICA:
    	  		if((modalitaAggiudicazioneGara == null ||
    	  		  (modalitaAggiudicazioneGara != null &&
                   modalitaAggiudicazioneGara.longValue() != 6)) &&
                   !isValtec)
                     result = FASE_APERTURA_OFFERTE_ECONOMICHE;
                 else
                     result = FASE_CHIUSURA_VALUTAZIONE_TECNICA;
	  		    break;
	  		case FASE_CHIUSURA_VALUTAZIONE_TECNICA:
              if(visOffEco)
                result = FASE_APERTURA_OFFERTE_ECONOMICHE;
              else{
                if("1".equals(ricastae))
                  result = FASE_ASTA_ELETTRONICA;
                else
                  result = FASE_CALCOLO_AGGIUDICAZIONE;
              }
              break;
	  		case FASE_APERTURA_OFFERTE_ECONOMICHE:
    	  		if("1".equals(ricastae))
                   result = FASE_ASTA_ELETTRONICA;
               else
                   result = FASE_CALCOLO_AGGIUDICAZIONE;
	  		 	break;
	  		case FASE_ASTA_ELETTRONICA:
                result = FASE_CALCOLO_AGGIUDICAZIONE;
                break;
	  		case FASE_CALCOLO_AGGIUDICAZIONE:
	  			result = FASE_AGGIUDICAZIONE_PROVVISORIA;
	  			break;
	  		case FASE_AGGIUDICAZIONE_PROVVISORIA:
	  			result = FASE_AGGIUDICAZIONE_DEFINITIVA;
	  			break;
	  		case FASE_AGGIUDICAZIONE_DEFINITIVA: // Per prevenire possibili errori
	  			result = FASE_AGGIUDICAZIONE_DEFINITIVA;
	  			break;
	  		}
	  	}
  	}

  	return result;
  }

  /**
   * @param faseGara
   * @param isGaraLottiConOffertaUnica
   * @return Ritorna lo step del wizard delle fasi di ricezione offerte
   * 				 precedente a quella ricevuta dall'argomento
   */
  private int getStepWizardPrecedente(int faseGara, Long modalitaAggiudicazioneGara,
  		boolean isGaraLottiConOffertaUnica, boolean isSorteggioControlloRequisiti,
  		boolean isValtec, String ricastae, boolean visOffertaEco){
  	int result = FASE_AGGIUDICAZIONE_DEFINITIVA;

  	if(isGaraLottiConOffertaUnica){
	  	if(faseGara < FASE_APERTURA_DOCUM_AMMINISTR)
	  		result = FASE_APERTURA_DOCUM_AMMINISTR;
	  	else if(faseGara > FASE_AGGIUDICAZIONE_DEFINITIVA)
	  		result = FASE_AGGIUDICAZIONE_DEFINITIVA;
	  	else {
	  		switch(faseGara){
	  		case FASE_AGGIUDICAZIONE_DEFINITIVA:
	  			result = FASE_AGGIUDICAZIONE_PROVVISORIA;
	  			break;
	  		case FASE_AGGIUDICAZIONE_PROVVISORIA:
	  			result = FASE_CALCOLO_AGGIUDICAZIONE;
	  			break;
	  		case FASE_CALCOLO_AGGIUDICAZIONE:
	  			if(visOffertaEco)
	  		      result = FASE_APERTURA_OFFERTE_ECONOMICHE;
	  			else{
  	  			  if((modalitaAggiudicazioneGara == null ||
                      (modalitaAggiudicazioneGara != null &&
                         modalitaAggiudicazioneGara.longValue() != 6)) &&
                         !isValtec){
                   if(isSorteggioControlloRequisiti)
                     result = FASE_CONCLUSIONE_COMPROVA_REQUISITI;
                   else
                     result = FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA;
                 }else
                   result = FASE_CHIUSURA_VALUTAZIONE_TECNICA;
	  			}
	  			break;
	  		case FASE_APERTURA_OFFERTE_ECONOMICHE:
	  			if((modalitaAggiudicazioneGara == null ||
                    (modalitaAggiudicazioneGara != null &&
                       modalitaAggiudicazioneGara.longValue() != 6)) &&
                       !isValtec){
                 if(isSorteggioControlloRequisiti)
                   result = FASE_CONCLUSIONE_COMPROVA_REQUISITI;
                 else
                   result = FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA;
               }else
                 result = FASE_CHIUSURA_VALUTAZIONE_TECNICA;
               break;
	  		case FASE_CHIUSURA_VALUTAZIONE_TECNICA:
              result = FASE_VALUTAZIONE_TECNICA;
              break;
	  		case FASE_VALUTAZIONE_TECNICA:
	  			if(isSorteggioControlloRequisiti)
                  result = FASE_CONCLUSIONE_COMPROVA_REQUISITI;
                else
                  result = FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA;
                break;
	  		case FASE_CONCLUSIONE_COMPROVA_REQUISITI:
    	  		if(isSorteggioControlloRequisiti)
    	  		  result = FASE_ESITO_CONTROLLO_SORTEGGIATE;
                else
                  result = FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA;
	  		    break;
	  		case FASE_ESITO_CONTROLLO_SORTEGGIATE:
	  			result = FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA;
	  			break;
	  		case FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA:
	  			if(isSorteggioControlloRequisiti)
                  result = FASE_SORTEGGIO_CONTROLLO_REQUISITI;
                else
                  result = FASE_APERTURA_DOCUM_AMMINISTR;
               break;
	  		case FASE_SORTEGGIO_CONTROLLO_REQUISITI:
	  			result = FASE_APERTURA_DOCUM_AMMINISTR;
	  			break;
	  		case FASE_APERTURA_DOCUM_AMMINISTR:  // Per prevenire possibili errori
	  			result = FASE_APERTURA_DOCUM_AMMINISTR;
	  			break;
	  		}
	  	}
  	} else {
	  	if(faseGara < FASE_APERTURA_DOCUM_AMMINISTR)
	  		result = FASE_APERTURA_DOCUM_AMMINISTR;
	  	else if(faseGara > FASE_CALCOLO_AGGIUDICAZIONE)
	  		result = FASE_CALCOLO_AGGIUDICAZIONE;
	  	else {
	  		switch(faseGara){
	  		case FASE_CALCOLO_AGGIUDICAZIONE:
	  			if("1".equals(ricastae))
	  			  result = FASE_ASTA_ELETTRONICA;
	  			else
	  		      if(visOffertaEco)
	  		        result = FASE_APERTURA_OFFERTE_ECONOMICHE;
	  		      else{
	  		        if((modalitaAggiudicazioneGara == null ||
	                     (modalitaAggiudicazioneGara != null &&
	                        modalitaAggiudicazioneGara.longValue() != 6)) &&
	                        !isValtec){
	                  if(isSorteggioControlloRequisiti)
	                    result = FASE_CONCLUSIONE_COMPROVA_REQUISITI;
	                  else
	                    result = FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA;
	                }else
	                    result = FASE_CHIUSURA_VALUTAZIONE_TECNICA;
	  		      }
	  			break;
	  		case FASE_ASTA_ELETTRONICA:
    	  		if(visOffertaEco)
                  result = FASE_APERTURA_OFFERTE_ECONOMICHE;
                else{
                  if((modalitaAggiudicazioneGara == null ||
                       (modalitaAggiudicazioneGara != null &&
                          modalitaAggiudicazioneGara.longValue() != 6)) &&
                          !isValtec){
                    if(isSorteggioControlloRequisiti)
                      result = FASE_CONCLUSIONE_COMPROVA_REQUISITI;
                    else
                      result = FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA;
                  }else
                      result = FASE_CHIUSURA_VALUTAZIONE_TECNICA;
                }
	  		    break;
	  		case FASE_APERTURA_OFFERTE_ECONOMICHE:
	  			if((modalitaAggiudicazioneGara == null ||
	  		    	 (modalitaAggiudicazioneGara != null &&
	  		    		modalitaAggiudicazioneGara.longValue() != 6)) &&
	  		    		!isValtec){
	  			  if(isSorteggioControlloRequisiti)
	  			    result = FASE_CONCLUSIONE_COMPROVA_REQUISITI;
	  			  else
	  			    result = FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA;
	  			}else
	  				result = FASE_CHIUSURA_VALUTAZIONE_TECNICA;
	  			break;
	  		case FASE_CHIUSURA_VALUTAZIONE_TECNICA:
	  		  result = FASE_VALUTAZIONE_TECNICA;
	  		  break;
	  		case FASE_VALUTAZIONE_TECNICA:
	  			if(isSorteggioControlloRequisiti)
	  			  result = FASE_CONCLUSIONE_COMPROVA_REQUISITI;
                else
                  result = FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA;
                break;
	  		case FASE_CONCLUSIONE_COMPROVA_REQUISITI:
    	  		if(isSorteggioControlloRequisiti)
                  result = FASE_ESITO_CONTROLLO_SORTEGGIATE;
                else
                  result = FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA;
	  			break;
	  		case FASE_ESITO_CONTROLLO_SORTEGGIATE:
	  			result = FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA;
	  			break;
	  		case FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA:
	  		    if(isSorteggioControlloRequisiti)
                  result = FASE_SORTEGGIO_CONTROLLO_REQUISITI;
                else
                  result = FASE_APERTURA_DOCUM_AMMINISTR;
	  			break;
	  		case FASE_SORTEGGIO_CONTROLLO_REQUISITI:
	  			result = FASE_APERTURA_DOCUM_AMMINISTR;
	  			break;
	  		case FASE_APERTURA_DOCUM_AMMINISTR:  // Per prevenire possibili errori
	  			result = FASE_APERTURA_DOCUM_AMMINISTR;
	  			break;
	  		}
	  	}
  	}
  	return result;
  }

  void esistonoDitteConPunteggio(String ngara, String tipoPunteggio, PageContext pageContext) throws JspException{
    try {
      boolean esistonoDitteConPunteggio = this.pgManagerEst1.esistonoDittePunteggioValorizzato(ngara, tipoPunteggio);
      pageContext.setAttribute("esistonoDitteConPunteggio", new Boolean(esistonoDitteConPunteggio),PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
      throw new JspException("Errore durante il controllo dell'esistenza di ditte con  " +
          "punteggi tecnici della gara (NGARA = '" + ngara + ")", e);
    }
  }
}