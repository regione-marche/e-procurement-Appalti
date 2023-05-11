/*
 * Created on 26/nov/09
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
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

/**
 * Inizializzazione delle pagine a lista per le gare a lotti con offerta unica
 * per visualizzare e inserire le offerte tecniche/economiche di una ditta per
 * i diversi lotti
 *
 * Questa classe si ispira alla classe GestioneFasiGaraFunction
 *
 * @author Luca.Giacomazzo
 */
public class GestioneOfferteTecnicheEconomicheDittaFunction extends
	GestioneFasiGaraFunction {

	public GestioneOfferteTecnicheEconomicheDittaFunction(){
		super();
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

    this.pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        pageContext, PgManagerEst1.class);

    // Chiave della ditta che e' nella forma
    // key='DITG.CODGAR5=T:00070;DITG.DITTAO=T:0170233059;DITG.NGARA5=T:00070';
    String chiaveDitta = UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);

    String[] tmp = chiaveDitta.split(";");

    String codiceGara  = tmp[0].substring(tmp[0].indexOf(":")+1);
    String codiceDitta = tmp[1].substring(tmp[0].indexOf(":")+1);
    //String numeroGara  = tmp[2].substring(tmp[0].indexOf(":")+1);

    // Determino se la gara e' una gara a lotti omogenea e se e' una gara per
    // lavori
    Boolean isGaraLottiOmogenea = Boolean.FALSE;
    String isGaraPerLavori = "false";
    boolean isGaraLottiConOffertaUnica = true;

    pageContext.setAttribute("garaLottiOmogenea",
        isGaraLottiOmogenea.toString(), PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("garaPerLavori", isGaraPerLavori,
        PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("isGaraLottiConOffertaUnica",
    		"" + isGaraLottiConOffertaUnica, PageContext.REQUEST_SCOPE);

    //Variabile che indica se è attiva la modalità di offerta tecnica o economica per lotto
    String isOffertaPerLotto = UtilityTags.getParametro(pageContext,"isOffertaPerLotto");
    if(((HttpServletRequest)pageContext.getRequest()).getParameter("isOffertaPerLotto") != null)
      isOffertaPerLotto = ((HttpServletRequest)
              pageContext.getRequest()).getParameter("isOffertaPerLotto");
    if(isOffertaPerLotto == null || "".equals(isOffertaPerLotto))
      isOffertaPerLotto = "false";

    pageContext.setAttribute("isOffertaPerLotto", isOffertaPerLotto,
        PageContext.REQUEST_SCOPE);

    boolean visOffertaEco = true;
    String visOffertaEcoString = UtilityTags.getParametro(pageContext,"visOffertaEco");
    if(!"true".equals(visOffertaEcoString))
      visOffertaEco= false;

    String numeroGara  = "";
    if(!"true".equals(isOffertaPerLotto))
      numeroGara  = tmp[2].substring(tmp[0].indexOf(":"));
    else
      numeroGara  = tmp[1].substring(tmp[0].indexOf(":"));

    int faseGara = GestioneFasiGaraFunction.FASE_APERTURA_DOCUM_AMMINISTR;
    //String codiceTornata = null;
    //String codiceLotto = new String(codiceGara);
    Boolean isGaraTelematica = Boolean.FALSE;
    //Gara telematica?
    try {
      Vector datiTorn = sqlManager.getVector("select gartel,offtel from torn where codgar=?", new Object[]{codiceGara});
      if(datiTorn!=null && datiTorn.size()>0){
        String gartel = SqlManager.getValueFromVectorParam(datiTorn, 0).getStringValue();
        if("1".equals(gartel)){
          isGaraTelematica = Boolean.TRUE;
        }
        pageContext.setAttribute("isGaraTelematica", isGaraTelematica,
            PageContext.REQUEST_SCOPE);

        Long offtel = SqlManager.getValueFromVectorParam(datiTorn, 1).longValue();
        pageContext.setAttribute("offtel", offtel,
            PageContext.REQUEST_SCOPE);
      }


    } catch (SQLException e1) {
      throw new JspException(
          "Errore durante la lettura dei campi di TORN", e1);
    } catch (GestoreException e) {
      throw new JspException(
          "Errore durante la lettura dei campi di TORN", e);
    }

    // Determino se la gara e' a lotto unico, la fase di gara e modalita'
    // aggiudicazione della gara e Aggiud.con esclus.autom.(1) o manuale(2)
    // delle offerte anomale del lotto in analisi
    Boolean garaLottoUnico = Boolean.FALSE;
    Long modalitaAggiudicazioneGara = null;
    Long aggiudicazioneEsclusAutom = null;
    String aggiudicazioneProvvisoria = null;
    Long bustalotti = null;
    String ricastae = null;

    // Conteggio del numero di lotti, di una gara divisa in lotti con
    // offerta unica, di tipo 'OEPV' (GARE.MODLICG = 6).
    Long numeroLottiOEPV = new Long(0);

    try {
      Vector datiLotto = sqlManager.getVector(
          "select CODGAR1, FASGAR, MODLICG, MODASTG, DITTAP, BUSTALOTTI from GARE " +
           "where GARE.NGARA = ?", new Object[]{codiceGara});
      if (datiLotto != null && datiLotto.size() > 0) {
        /*codiceTornata = ((JdbcParametro) datiLotto.get(0)).getStringValue();
        if (codiceTornata.startsWith("$"))
        	garaLottoUnico = Boolean.TRUE;
        else
          garaLottoUnico = Boolean.FALSE;*/

        pageContext.setAttribute("garaLottoUnico", garaLottoUnico,
            PageContext.REQUEST_SCOPE);


        if (((JdbcParametro) datiLotto.get(1)).getValue() != null){
          faseGara = ((Long) ((JdbcParametro) datiLotto.get(1)).getValue()).intValue();

          // Se FASGAR >=7 vi è il blocco in sola visualizzazione dei dati di
          // tutte le fasi di gare tranne le ultime due
            if (faseGara >= 7)
              pageContext.setAttribute("bloccoAggiudicazione", new Long(1),
                    PageContext.REQUEST_SCOPE);
            else
              pageContext.setAttribute("bloccoAggiudicazione", new Long(0),
                    PageContext.REQUEST_SCOPE);
            pageContext.setAttribute("faseGara", faseGara,
                PageContext.REQUEST_SCOPE);
        }



	      if (((JdbcParametro) datiLotto.get(2)).getValue() != null)
	        modalitaAggiudicazioneGara = (Long) ((JdbcParametro)
	        		datiLotto.get(2)).getValue();


      	// Conteggio del numero di lotti di una gara divisa in lotti con
      	// offerta unica di tipo Miglior offerta prezzi e Offerta
      	// economicamente più vantaggiosa (GARE.MODLIGC in (5, 14, 16, 6))
      	Long numeroLottiMigliorOffertaPrezzi = new Long(0);

      	// Conteggio del numero di lotti di una gara divisa in lotti con
        // offerta unica con presentazione offerte mediante importo (GARE.DETLICG =4)
        Long numeroLottiOfferteImporto = new Long(0);

        // Conteggio del numero di lotti di una gara divisa in lotti con
        // offerta unica con GARE1.VALTEC=1
        Long numeroLottiVALTEC = new Long(0);
        Long numeroLottiOEPVMigliorOffertaPrezziValtec = new Long(0);

      	Long numeroLotti =  (Long) sqlManager.getObject(
      			"select count(*) from gare " +
     			 "where codgar1 = ? " +
     			   "and genere is null ", new Object[]{codiceGara});

	      if(isGaraLottiConOffertaUnica){
	      	String chiaveSelect=codiceGara;
	      	if("true".equals(isOffertaPerLotto))
	      	  chiaveSelect=numeroGara;

	        if(numeroLotti != null && numeroLotti.longValue() > 0){
	        	String select ="select count(*) from gare " +
                    "where codgar1 = ? " +
                    "and genere is null " +
                    "and modlicg = 6";
	        	if("true".equals(isOffertaPerLotto))
	        	  select= select.replace("codgar1", "ngara");
	          numeroLottiOEPV = (Long) sqlManager.getObject(select, new Object[]{chiaveSelect});
	        	// Se almeno un lotto di tale gara e' di tipo OEPV, allora la fase
	        	// FASE_APERTURA_OFFERTE_TECNICHE deve essere visibile per tutti i
	        	// lotti e quindi l'oggetto modalitaAggiudicazioneGara viene posto
	        	// comunque pari a new Long(6)
	        	if(numeroLottiOEPV != null && numeroLottiOEPV.longValue() > 0)
	        		modalitaAggiudicazioneGara = new Long(6);
	        	else
	        		numeroLottiOEPV = new Long(0);

	        	select ="select count(*) from gare " +
                    "where codgar1 = ? " +
                    "and genere is null " +
                    "and modlicg in (5, 14, 16)";
                if("true".equals(isOffertaPerLotto))
                  select= select.replace("codgar1", "ngara");
	        	numeroLottiMigliorOffertaPrezzi = (Long) sqlManager.getObject(select, new Object[]{chiaveSelect});
	        	if(numeroLottiMigliorOffertaPrezzi == null)
	        		numeroLottiMigliorOffertaPrezzi = new Long(0);

	        	select ="select count(*) from gare " +
                    "where codgar1 = ? " +
                    "and genere is null " +
                    "and detlicg=4";
                if("true".equals(isOffertaPerLotto))
                  select= select.replace("codgar1", "ngara");
	        	numeroLottiOfferteImporto = (Long) sqlManager.getObject(select, new Object[]{chiaveSelect});
                if(numeroLottiOfferteImporto == null)
                  numeroLottiOfferteImporto = new Long(0);

                select ="select count(*) from gare1 " +
                    "where codgar1 = ? " +
                    "and ngara!=codgar1 " +
                    "and valtec = '1'";
                if("true".equals(isOffertaPerLotto))
                  select ="select count(*) from gare1 " +
                      "where ngara = ? " +
                      "and valtec = '1'";
                numeroLottiVALTEC = (Long) sqlManager.getObject(select, new Object[]{chiaveSelect});

                if(numeroLottiVALTEC==null)
                  numeroLottiVALTEC=new Long(0);
                //Nel caso vi siano dei lotti con VALTEC valorizzati, si deve particolarizzare la ricerca sui lotti OEPV e Miglior Offerta Prezzi
                if (numeroLottiVALTEC.longValue()>0){
                  if(!"true".equals(isOffertaPerLotto)){
                    numeroLottiOEPVMigliorOffertaPrezziValtec = (Long) sqlManager.getObject(
                        "select count(*) from gare g,gare1 g1 " +
                            "where g.codgar1 = ? and g.ngara=g1.ngara " +
                              "and g.genere is null " +
                              "and g.modlicg in (5, 6, 14, 16) and g1.valtec='1'", new Object[]{codiceGara});
                    if(numeroLottiOEPVMigliorOffertaPrezziValtec==null)
                      numeroLottiOEPVMigliorOffertaPrezziValtec = new Long(0);
                  }else
                    numeroLottiOEPVMigliorOffertaPrezziValtec = new Long(1);

                }
	      	}
        }

	      pageContext.setAttribute("numeroLottiMigliorOffertaPrezzi",
	      		numeroLottiMigliorOffertaPrezzi, PageContext.REQUEST_SCOPE);
	      pageContext.setAttribute("numeroLottiOEPV",	numeroLottiOEPV,
	      		PageContext.REQUEST_SCOPE);
	      pageContext.setAttribute("numeroLotti",	numeroLotti,
	      		PageContext.REQUEST_SCOPE);
	      pageContext.setAttribute("numeroLottiOfferteImporto",  numeroLottiOfferteImporto,
              PageContext.REQUEST_SCOPE);
	      pageContext.setAttribute("numeroLottiValtec",  numeroLottiVALTEC,
              PageContext.REQUEST_SCOPE);
	      pageContext.setAttribute("numeroLottiOEPVMigliorOffertaPrezziValtec",  numeroLottiOEPVMigliorOffertaPrezziValtec,
              PageContext.REQUEST_SCOPE);

	      if(modalitaAggiudicazioneGara != null)
	      	pageContext.setAttribute("modalitaAggiudicazioneGara",
            modalitaAggiudicazioneGara, PageContext.REQUEST_SCOPE);

        if (((JdbcParametro) datiLotto.get(3)).getValue() != null) {
          aggiudicazioneEsclusAutom = (Long) ((JdbcParametro) datiLotto.get(3)).getValue();
          pageContext.setAttribute("aggiudicazioneEsclusAutom",
              aggiudicazioneEsclusAutom, PageContext.REQUEST_SCOPE);
        }

        if (((JdbcParametro) datiLotto.get(4)).getValue() != null) {
        	aggiudicazioneProvvisoria = (String) ((JdbcParametro) datiLotto.get(4)).getValue();
            pageContext.setAttribute("aggiudicazioneProvvisoria",
            		aggiudicazioneProvvisoria, PageContext.REQUEST_SCOPE);
          }

        if (((JdbcParametro) datiLotto.get(5)).getValue() != null){
          bustalotti = ((Long) ((JdbcParametro) datiLotto.get(5)).getValue()).longValue();
          pageContext.setAttribute("bustalotti",
              bustalotti, PageContext.REQUEST_SCOPE);
        }
      }
      ricastae = (String)sqlManager.getObject("select ricastae from torn,gare where ngara=? and codgar1=codgar", new Object[]{codiceGara});
    } catch (SQLException s) {
      throw new JspException(
          "Errore durante la lettura di dati supplementari del lotto", s);
    }

    boolean isValtec = false;
    boolean isValtecLotto = false;
    //Se gara a Lotto unico o Offerte distinte si considera GARE1.VALTEC, altrimenti
    //si deve controllare se fra i lotti ve n'è uno con Valtec =1
    //Nel caso di offerta per lotto serve sapere se attiva la valutazione tecnica per il singolo lotto,
    if(!isGaraLottiConOffertaUnica || "true".equals(isOffertaPerLotto)){
      String valtec=null;
      try {
        valtec = (String)sqlManager.getObject("select valtec from gare1 where ngara=?", new Object[]{numeroGara});
      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura del campo GARE1.VALTEC", e);
      }
      if ("1".equals(valtec)){
        if(!"true".equals(isOffertaPerLotto))
          isValtec = true;
        else
          isValtecLotto=true;

        pageContext.setAttribute("attivaValutazioneTec",
            new Boolean(isValtecLotto), PageContext.REQUEST_SCOPE);
      }


    }

    String paginaAttivaWizard = UtilityTags.getParametro(pageContext,
        GestioneFasiGaraFunction.PARAM_WIZARD_PAGINA_ATTIVA);
    String direzioneWizard = UtilityTags.getParametro(pageContext,
        GestioneFasiRicezioneFunction.PARAM_DIREZIONE_WIZARD);

    if(((HttpServletRequest)pageContext.getRequest()).getParameter("paginaAttivaWizard") != null)
    	paginaAttivaWizard = ((HttpServletRequest)
    			pageContext.getRequest()).getParameter("paginaAttivaWizard");

    if(((HttpServletRequest)pageContext.getRequest()).getParameter("direzioneWizard") != null)
    	direzioneWizard = ((HttpServletRequest)
    			pageContext.getRequest()).getParameter("direzioneWizard");

    int wizardPaginaAttiva=0;
    //CF 060611
    //...
    if(paginaAttivaWizard.equals(new String("50")))
      wizardPaginaAttiva = 50;
    if(paginaAttivaWizard.equals(new String("60")))
      wizardPaginaAttiva = 60;

    if (wizardPaginaAttiva == FASE_APERTURA_OFFERTE_ECONOMICHE) {
      String numeroCifreDecimaliRibasso;
      try {
        numeroCifreDecimaliRibasso = this.pgManagerEst1.getNumeroDecimaliRibasso(codiceGara);
      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura del numero di decimali da usare per il ribasso della gara(NGARA = '" + numeroGara + ")", e);
      }
      pageContext.setAttribute("numeroCifreDecimaliRibasso",numeroCifreDecimaliRibasso);
    }

    if (wizardPaginaAttiva == FASE_VALUTAZIONE_TECNICA ||
        wizardPaginaAttiva == FASE_APERTURA_OFFERTE_ECONOMICHE)
      pageContext.setAttribute("numeroCifreDecimaliPunteggioTecnico",
          tabellatiManager.getDescrTabellato("A1049", "1"));

    if (wizardPaginaAttiva == FASE_VALUTAZIONE_TECNICA){
      // Determino il totale del punteggio tecnico della gara
      try {
        Double maxPunTecnico = null;
        if("true".equals(isOffertaPerLotto)){
          //Nel caso di di offerta per lotto nella chiave vi sono solo CODGAR5 e NGARA5

          maxPunTecnico = pgManager.getSommaPunteggioTecnico(numeroGara);
        }else{
          maxPunTecnico = pgManager.getSommaPunteggioTecnico(codiceGara);
        }

        if (maxPunTecnico == null)
          maxPunTecnico = new Double(-1000);

        if (maxPunTecnico != null)
          pageContext.setAttribute("punteggioTecnico",
              UtilityNumeri.convertiDouble(maxPunTecnico,
                  UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
              PageContext.REQUEST_SCOPE);

        Double sogliaTecnicaMinima = (Double)sqlManager.getObject("select mintec from gare1 where ngara=?", new Object[]{numeroGara});
        if (sogliaTecnicaMinima != null)
          pageContext.setAttribute("sogliaTecnicaMinima",
              UtilityNumeri.convertiDouble(sogliaTecnicaMinima,
                  UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
              PageContext.REQUEST_SCOPE);

        Double puntecSex[] = pgManager.getSommaPunteggiTecniciSez(numeroGara);
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

      } catch (SQLException e) {
        throw new JspException("Errore durante il calcolo della somma dei " +
            "punteggi tecnici della gara (NGARA = '" + codiceGara + ")", e);
      }
      if("true".equals(isOffertaPerLotto)){
        this.esistonoDitteConPunteggio(numeroGara, "TEC", pageContext);
      }
      if(numeroLottiOEPV!=null && numeroLottiOEPV.longValue()>0){
        //Si controlla se vi sono dei lotti OEPV con attiva la riparametrazione tecnica
        String chiavePerControllo = codiceGara;
        boolean tuttiLotti=true;
        if("true".equals(isOffertaPerLotto)){
          chiavePerControllo = numeroGara;
          tuttiLotti=false;
          Long riptec=null;
          Long statocg=null;
          try {
            Vector<?> datiGare1 = sqlManager.getVector("select riptec, ripcritec, statocg from gare1 where ngara=?", new Object[]{numeroGara});
            if(datiGare1!=null && datiGare1.size()>0){
              riptec = SqlManager.getValueFromVectorParam(datiGare1, 0).longValue();
              Long ripcritec = SqlManager.getValueFromVectorParam(datiGare1, 1).longValue();
              statocg = SqlManager.getValueFromVectorParam(datiGare1, 2).longValue();
              if(riptec==null)
                riptec = new Long(3);

              String msg ="";
              msg += tabellatiManager.getDescrTabellato("A1144", riptec.toString());
              if((riptec.longValue() ==1 || riptec.longValue()==2) && ripcritec != null){
                msg += "&nbsp;&nbsp;&nbsp;&nbsp;<b>Criterio:</b> " + tabellatiManager.getDescrTabellato("A1145", ripcritec.toString());
              }
              pageContext.setAttribute("msgRiptec",msg,PageContext.REQUEST_SCOPE);
            }
          } catch (Exception e) {
            throw new JspException("Errore nella lettura del campo GARE1.RIPECO",e);
          }
          pageContext.setAttribute("RIPTEC",riptec, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("STATOCG",statocg, PageContext.REQUEST_SCOPE);
        }
        pageContext.setAttribute("lottiOEPVRiparamTecPresenti",new Boolean(this.riparametrazioneAttiva(chiavePerControllo, sqlManager, "TEC",tuttiLotti)),PageContext.REQUEST_SCOPE);

      }
    } else if(wizardPaginaAttiva == FASE_APERTURA_OFFERTE_ECONOMICHE) {
      try {
        // Determino il totale del punteggio economico della gara
        Double maxPunEconomico=null;
        if("true".equals(isOffertaPerLotto)){
          maxPunEconomico = pgManager.getSommaPunteggioEconomico(numeroGara);
        }else{
          maxPunEconomico = pgManager.getSommaPunteggioEconomico(codiceGara);
        }
        if (maxPunEconomico == null)
          maxPunEconomico = new Double(-1000);

        if (maxPunEconomico != null)
          pageContext.setAttribute("punteggioEconomico",
              UtilityNumeri.convertiDouble(maxPunEconomico,
                  UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
                      PageContext.REQUEST_SCOPE);

        Double sogliaEconomicaMinima = (Double)sqlManager.getObject("select mineco from gare1 where ngara=?", new Object[]{numeroGara});
        if (sogliaEconomicaMinima != null)
          pageContext.setAttribute("sogliaEconomicaMinima",
              UtilityNumeri.convertiDouble(sogliaEconomicaMinima,
                  UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
              PageContext.REQUEST_SCOPE);


      } catch (SQLException e) {
        throw new JspException("Errore durante il calcolo della somma dei " +
              "punteggi economici della gara (NGARA = '" + codiceGara + ")",
              e);
      }
      if("true".equals(isOffertaPerLotto)){
        this.esistonoDitteConPunteggio(numeroGara, "ECO", pageContext);
      }
      if(numeroLottiOEPV!=null && numeroLottiOEPV.longValue()>0){
        //Si controlla se vi sono dei lotti OEPV con attiva la riparametrazione economica
        boolean tuttiLotti=true;
        String chiavePerControllo = codiceGara;
        if("true".equals(isOffertaPerLotto)){
          chiavePerControllo = numeroGara;
          tuttiLotti=false;
          Long ripeco = null;
          Long statocg=null;
          try {
            Vector<?> datiGare1 = sqlManager.getVector("select ripeco, ripcrieco, statocg from gare1 where ngara=?", new Object[]{numeroGara});
            if(datiGare1!=null && datiGare1.size()>0){
              ripeco = SqlManager.getValueFromVectorParam(datiGare1, 0).longValue();
              Long ripcrieco = SqlManager.getValueFromVectorParam(datiGare1, 1).longValue();
              statocg = SqlManager.getValueFromVectorParam(datiGare1, 2).longValue();
              if(ripeco==null)
                ripeco = new Long(3);

              String msg ="";
              msg += tabellatiManager.getDescrTabellato("A1144", ripeco.toString());
              if((ripeco.longValue() ==1 || ripeco.longValue()==2) && ripeco != null){
                msg += "&nbsp;&nbsp;&nbsp;&nbsp;<b>Criterio:</b> " + tabellatiManager.getDescrTabellato("A1145", ripcrieco.toString());
              }
              pageContext.setAttribute("msgRipeco",msg,PageContext.REQUEST_SCOPE);
            }
          } catch (Exception e) {
            throw new JspException("Errore nella lettura del campo GARE1.RIPECO",e);
          }
          pageContext.setAttribute("RIPECO",ripeco, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("STATOCG",statocg, PageContext.REQUEST_SCOPE);
        }
        pageContext.setAttribute("lottiOEPVRiparamEcoPresenti",new Boolean(this.riparametrazioneAttiva(chiavePerControllo, sqlManager, "ECO",tuttiLotti)),PageContext.REQUEST_SCOPE);

      }
    }

    // Gestione del filtro sulle ditte in base all'avanzamento del wizard
    String filtroFaseGara = this.gestioneFiltroFaseGara(wizardPaginaAttiva, pageContext,isOffertaPerLotto,bustalotti);
    pageContext.setAttribute("paginaAttivaWizard", paginaAttivaWizard,
        PageContext.REQUEST_SCOPE);

    String updateLista = UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA);
    if (updateLista == null || updateLista.length() == 0) updateLista = "0";
    pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA,
        updateLista, PageContext.REQUEST_SCOPE);

    // Creazione del parametro con la chiave da passare alla pagina di
    // controllo delle autorizzazioni
    String inputFiltro = "CODGAR=T:".concat(codiceGara);
    pageContext.setAttribute("inputFiltro", inputFiltro,
        PageContext.REQUEST_SCOPE);

    //Lettura del campo OFFAUM.TORN
    pgManager.getOFFAUM(pageContext, sqlManager, codiceGara);

    //Gestione punteggi tecnici ed economici dei singoli lotti
    if (!"true".equals(isOffertaPerLotto) && (wizardPaginaAttiva == FASE_VALUTAZIONE_TECNICA || wizardPaginaAttiva == FASE_APERTURA_OFFERTE_ECONOMICHE)){
      //String filtroFaseGara = pageContext.getAttribute("filtroFaseGara").toString();
      //String select="select ngara5 from ditg where CODGAR5 = ? and NGARA5 <> ? and DITTAO = ? AND (DITG.FASGAR IS NULL OR DITG.FASGAR > 4 )";
      String select="select ngara5 from ditg where CODGAR5 = ? and NGARA5 <> ? and DITTAO = ?";
      select += filtroFaseGara + " order by ngara5";

      List listaDitte = null;

      if (wizardPaginaAttiva == FASE_VALUTAZIONE_TECNICA){
        select = select.replace("#DITG.NGARA5#", "?");
        filtroFaseGara = filtroFaseGara.replaceAll("#DITG.NGARA5#","'" + numeroGara + "'");
        try {
          listaDitte = sqlManager.getListVector(select, new Object[]{codiceGara,numeroGara,codiceDitta,numeroGara,numeroGara,numeroGara,numeroGara});
        } catch (SQLException e) {
          throw new JspException("Errore durante il calcolo dei punteggi tecnici di tutti i lotti)",
              e);
        }
      }else if(wizardPaginaAttiva == FASE_APERTURA_OFFERTE_ECONOMICHE) {
        select = select.replace("#DITG.NGARA5#", "?");
        filtroFaseGara = filtroFaseGara.replaceAll("#DITG.NGARA5#","'" + numeroGara + "'");
        try {
          listaDitte = sqlManager.getListVector(select, new Object[]{codiceGara,numeroGara,codiceDitta,numeroGara,numeroGara});
        } catch (SQLException e) {
          throw new JspException("Errore durante il calcolo dei punteggi economici di tutti i lotti)",
              e);
        }
      }
      try {
      if(listaDitte!= null && listaDitte.size()>0){
        Double punteggioMassimo = null;
        List listaPunteggi = new ArrayList();
        List listaSoglie = new ArrayList();
        for (int i = 0; i < listaDitte.size(); i++) {
          Vector ditta = (Vector) listaDitte.get(i);
          String numeroGaraDitta = ((JdbcParametro) ditta.get(0)).getStringValue();
          Double soglia = null;
          if (wizardPaginaAttiva == FASE_VALUTAZIONE_TECNICA){
            punteggioMassimo = pgManager.getSommaPunteggioTecnico(numeroGaraDitta);
            soglia = (Double)sqlManager.getObject("select mintec from gare1 where ngara=?", new Object[]{numeroGaraDitta});
          }else if(wizardPaginaAttiva == FASE_APERTURA_OFFERTE_ECONOMICHE) {
            punteggioMassimo = pgManager.getSommaPunteggioEconomico(numeroGaraDitta);
            soglia = (Double)sqlManager.getObject("select mineco from gare1 where ngara=?", new Object[]{numeroGaraDitta});
          }
          if(punteggioMassimo==null)
            listaPunteggi.add(new Double(-1000));
          else
            listaPunteggi.add(UtilityNumeri.convertiDouble(punteggioMassimo,
              UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3));

          if(soglia!= null)
            listaSoglie.add(UtilityNumeri.convertiDouble(soglia,
                UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3));
          else
            listaSoglie.add(soglia);
        }
        pageContext.setAttribute("listaPunteggi", listaPunteggi,
            PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("listaSoglie", listaSoglie,
            PageContext.REQUEST_SCOPE);
      }
      } catch (SQLException e) {
        throw new JspException("Errore durante il calcolo dei punteggi delle ditte)",
            e);
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

    //Viene impostata la where per la pagina dettaglioOfferteDitta-OffertaUnicaLotti.jsp
    String whereDITG="DITG.CODGAR5 = #DITG.CODGAR5# and DITG.NGARA5 <> #DITG.NGARA5# and DITG.DITTAO = #DITG.DITTAO#";
    //Nel caso di offerta per lotti, devo presentare tutte le ditte per il lotto in esame
    if("true".equals(isOffertaPerLotto))
      whereDITG="DITG.CODGAR5 = #DITG.CODGAR5# and DITG.NGARA5 = #DITG.NGARA5#";
    whereDITG+=filtroFaseGara;

    if("true".equals(isOffertaPerLotto)){
      String filtroDitte = (String)pageContext.getSession().getAttribute("filtroDitte");
      if(filtroDitte!=null && !"".equals(filtroDitte))
        whereDITG+=filtroDitte;
    }


    pageContext.setAttribute("whereDITG", whereDITG,
        PageContext.REQUEST_SCOPE);

    return null;
	}

	private String gestioneFiltroFaseGara(int faseGaraAttiva,
      PageContext pageContext, String isOffertaPerLotto, Long bustalotti) {
    StringBuffer result = new StringBuffer("");

    switch (faseGaraAttiva) {
    case GestioneFasiGaraFunction.FASE_VALUTAZIONE_TECNICA:
      if("false".equals(isOffertaPerLotto)){
        result.append(" and (DITG.NGARA5 in (select NGARA from GARE " +
        		"where NGARA <> #DITG.NGARA5# and CODGAR1 = #DITG.NGARA5# and " +
        		"GENERE is null and MODLICG = 6) or DITG.NGARA5 in (select NGARA from GARE1 " +
                "where NGARA <> #DITG.NGARA5# and CODGAR1 = #DITG.NGARA5# and " +
                "NGARA!=CODGAR1 and VALTEC = '1')) and" +
                "(DITG.FASGAR > 4 or DITG.FASGAR = 0 or DITG.FASGAR is null)");
      }else{
        result.append(" and (DITG.FASGAR > 4 or DITG.FASGAR = 0 or DITG.FASGAR is null)");
      }
      break;
    case GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE:
      if("false".equals(isOffertaPerLotto)){
        result.append(" and DITG.NGARA5 in (select G.NGARA from GARE G, GARE1 G1 " +
         "where G.NGARA <> #DITG.NGARA5# and G.CODGAR1 = #DITG.NGARA5# and G.NGARA=G1.NGARA and " +
         "G.GENERE is null and(G1.COSTOFISSO is null or G1.COSTOFISSO <> '1'))" +
         " and (DITG.FASGAR > 5 or DITG.FASGAR = 0 or DITG.FASGAR is null)");

      }else{
        result.append(" and (DITG.FASGAR > 5 or DITG.FASGAR = 0 or DITG.FASGAR is null)");
      }
     break;
    }

    return result.toString();
  }

	private boolean riparametrazioneAttiva(String codiceGara, SqlManager sqlManager, String tipoRiparametrazione, boolean tuttiLotti) throws JspException{
	  boolean ret=false;
	  String select = "select count(g.ngara) from gare g,gare1 g1 where g.codgar1 = ? and g.ngara=g1.ngara and g.genere is null and g.modlicg = 6 and ";
	  if(!tuttiLotti)
	    select = "select count(g.ngara) from gare g,gare1 g1 where g.ngara = ? and g.ngara=g1.ngara and g.genere is null and g.modlicg = 6 and ";
	  if("TEC".equals(tipoRiparametrazione))
	    select += " (g1.riptec=1 or g1.riptec=2)";
	  else
	    select += " (g1.ripeco=1 or g1.ripeco=2)";

	  Long numeroLottiOEPVRiparametrati=null;
      try {
        numeroLottiOEPVRiparametrati = (Long) sqlManager.getObject(select, new Object[]{codiceGara});
      } catch (SQLException e) {
        throw new JspException("Errore nella lettura dei dati della gara " + " per determinare se esistono lotti con attiva la riparametrazione)",e);
      }
	  if(numeroLottiOEPVRiparametrati!=null && numeroLottiOEPVRiparametrati.longValue()>0)
	    ret=true;
	  return ret;
	}
}