/*
 * Created on 31-Ott-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.tags.gestori.plugin.GestorePopupFasiGara;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;

/**
 * Funzione per l'inizializzazione della pagina 'Ricezione domande offerte di gara'
 *
 * @author Luca.Giacomazzo
 */
public class GestioneFasiRicezioneFunction extends AbstractFunzioneTag {

  public static final String PARAM_WIZARD_PAGINA_ATTIVA = "WIZARD_PAGINA_ATTIVA";

  public static final String PARAM_DIREZIONE_WIZARD 		= "DIREZIONE_WIZARD";

  // Nome dell'oggetto presente in sessione contenente tutti i codici delle gare
  // per le quali e' stato eseguito il controllo delle ditte escluse o vincitrici
  // di altri lotti
  public static final String SESSIONE_RICEZIONE_DOMANDE_OFFERTE_DITTE_MODIFICATE =
          "RICEZIONE_DOMANDE_OFFERTE_DITTE_MODIFICATE";

  // Step 1 del wizard ricezione offerte
  public static final int FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE = -50;
  // Step 2 del wizard ricezione offerte
  public static final int FASE_APERTURA_DOMANDE_DI_PARTECIPAZIONE  = -40;
  // Step 3 del wizard ricezione offerte
  public static final int FASE_ELENCO_DITTE_INVITATE							 = -30;
  // Step 4 del wizard ricezione offerte: scheda riassuntiva dei primi tre
  // passi delle fasi di ricezione, per poter comporre dei verbali
  public static final int FASE_INVITI															 = -25;
  // Step 5 del wizard ricezione offerte
  public static final int FASE_RICEZIONE_PLICHI                    = 10;
  // Step 6 del wizard ricezione offerte: fase fittizia a cui non corrisponde
  // nessuna valore ufficiale di FASGAR applicato alle fasi di
  // ricezione offerte. (Nelle fasi di gara invece, il valore 20
  // corrisponde alla prima fase denominata 'Apertura documentazione
  // amministrativa').
  // Nelle fasi di ricezione serve solo per visualizzare la pagina
  // riassuntiva denominata 'Chiusura ricezione offerte' sempre
  // all'interno del wizard delle fasi di ricezione offerte
  public static final int FASE_CHIUSURA_RICEZIONE_OFFERTE          = 20;

  private static final String[] TITOLO_FASI_RICEZIONE = new String[]{
      "Ricezione domande di partecipaz.",
      "Apertura domande di partecipaz.",
      "Elenco ditte da invitare",
      "Invito",
      "Ricezione plichi",
      "Chiusura ricezione offerte" };

  //Per le FASI ISCRIZIONE
  //Step 1 del wizard iscrizione
  public static final int FASE_RICEZIONE_DOMANDE_DI_ISCRIZIONE = -50;
  // Step 2 del wizard iscrizione
  public static final int FASE_APERTURA_DOMANDE_DI_ISCRIZIONE  = -40;
  // Step 3 del wizard iscrizione
  public static final int FASE_ELENCO_CONCORRENTI_ABILITATI= -30;

  private static final String[] TITOLO_FASI_ISCRIZIONE = new String[]{
    "Ricezione domande di iscrizione",
    "Apertura domande di iscrizione",
    "Elenco operatori abilitati"};

  public GestioneFasiRicezioneFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
        pageContext, GeneManager.class);

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        pageContext, PgManager.class);

    String codiceGara = UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
    codiceGara = codiceGara.substring(codiceGara.indexOf(":") + 1);

    if(codiceGara == null || codiceGara.length() == 0){
    	codiceGara = UtilityTags.getParametro(pageContext,
          UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT);
      codiceGara = codiceGara.substring(codiceGara.indexOf(":") + 1);
    }

    Boolean isGaraLottiOmogenea = null;
    boolean isGaraLottiConOffertaUnica = false;
    String isGaraPerLavori = null;
    int	gareTipgarg = -1;
    int gareIterga = -1;
    Double importoTotaleBaseDAsta = null;

    //isProceduraAggiudicazioneAperta è true quando iterga=1
    boolean isProceduraAggiudicazioneAperta = false;
    //isProceduraNegoziata è true quando iterga=3,5,6,8
    boolean isProceduraNegoziata = false;
    //isProceduraRistretta è true quando iterga =2,4
    boolean isProceduraRistretta = false;
    boolean isIndagineMercato = false;

    boolean isGaraUsoAlbo = false;
    boolean isGareElenco = false;
    boolean isCatalogoEle = false;

    //int faseGara = FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE;
    int stepWizardRicezOfferte = FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE;
    String codiceTornata = null;
    String codiceLotto = new String(codiceGara);
    String paginaAttivaWizard = null;
    String direzioneWizard = null;
    String codiceElenco = null;
    Double importoTornata = null;
    String tipgen=null;
    String isProceduraTelematica = UtilityTags.getParametro(
        pageContext, "isProceduraTelematica");

    //Long bustalotti = null;

    // Nell'ultima pagina delle fasi di ricezione è possibile
    // attivare/disattivare le fasi di gara: l'aggiornamento della fase avviene
    // nel gestore di salvataggio, ma se tutto va bene occorre indicare una
    // variabile nel request in modo da permettere alla pagina di visualizzarsi
    // correttamente (in caso di attivazione fasi, si passa in automatico alla
    // pagina delle fasi di gara)
    String chiusuraFasiRicezione = UtilityTags.getParametro(
        pageContext, "CHIUSURA_FASI_RICEZIONE");
    if (chiusuraFasiRicezione != null && chiusuraFasiRicezione.length() > 0) {
      String esitoOperazione = null;
      if ("ATTIVA".equals(chiusuraFasiRicezione)) {
        if (UtilityTags.checkProtection(pageContext,
            "PAGE.VIS.GARE.GARE-scheda.FASIGARA", true))
          esitoOperazione = "FASI_GARA_ATTIVATE";
      } else if ("DISATTIVA".equals(chiusuraFasiRicezione)) {
        esitoOperazione = "FASI_GARA_DISATTIVATE";
      }
      pageContext.setAttribute("RISULTATO", esitoOperazione);
    }

    // Determino se la gara e' una gara a lotti omogenea e se e' una gara per lavori
    try {
      Vector<?> obj = sqlManager.getVector(
          "select TORN.TIPTOR, TORN.TIPGEN, GARE.TIPGARG, GARE.GENERE, GARE.ELENCOE, GARE.IMPAPP, TORN.IMPTOR, GARE.CARRELLO, " +
           "TORN.DTEPAR, TORN.OTEPAR, TORN.ITERGA, GARE.BUSTALOTTI, GARE.PRECED, TORN.CALCSOME from TORN, GARE " +
           "where TORN.CODGAR = GARE.CODGAR1 " +
             "and GARE.NGARA = ?", new Object[]{codiceGara});
      if(obj.get(0) != null){
        String tmpTipoTornata = ((JdbcParametro) obj.get(0)).getStringValue();
        if("1".equals(tmpTipoTornata))
          isGaraLottiOmogenea = Boolean.TRUE;
        else
          isGaraLottiOmogenea = Boolean.FALSE;

        pageContext.setAttribute("garaLottiOmogenea",
        		isGaraLottiOmogenea.toString(), PageContext.REQUEST_SCOPE);
      }
      if(obj.get(1) != null){
        String tmp1 = ((JdbcParametro) obj.get(1)).getStringValue();
        tipgen=tmp1;

        if("1".equals(tmp1))
          isGaraPerLavori = "true";
        else
          isGaraPerLavori = "false";

        pageContext.setAttribute("garaPerLavori", isGaraPerLavori,
        		PageContext.REQUEST_SCOPE);
      }

      if(obj.get(2) != null){
        Long tmp2 = (Long) ((JdbcParametro) obj.get(2)).getValue();
        if (tmp2 != null){
          gareTipgarg = tmp2.intValue();
        }
      }

      if(obj.get(10) != null){
        Long tmp10 = (Long) ((JdbcParametro) obj.get(10)).getValue();
        if (tmp10 != null){
          gareIterga = tmp10.intValue();

          switch(gareIterga){
          case 1:
            isProceduraAggiudicazioneAperta = true;
              break;
          case 3:
          case 5:
          case 6:
          case 8:
              isProceduraNegoziata = true;
              break;
          case 7:
            isIndagineMercato=true;
            break;
          default:
            isProceduraRistretta = true;
            break;

          }

          pageContext.setAttribute("isProceduraAggiudicazioneAperta",
              new Boolean(isProceduraAggiudicazioneAperta),
              PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("isProceduraNegoziata",
                  new Boolean(isProceduraNegoziata),
                  PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("isProceduraRistretta",
              new Boolean(isProceduraRistretta),
              PageContext.REQUEST_SCOPE);

          pageContext.setAttribute("isIndagineMercato",
              new Boolean(isIndagineMercato),
              PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("iterga",tmp10,PageContext.REQUEST_SCOPE);

        }

      }

      if(obj.get(3) != null){
      	Long tmp3 = (Long) ((JdbcParametro) obj.get(3)).getValue();
      	if(tmp3 != null && tmp3.longValue() == 3)
      		isGaraLottiConOffertaUnica = true;
      	if(tmp3 != null && tmp3.longValue() == 10){
      	  isGareElenco = true;
      	  stepWizardRicezOfferte = FASE_RICEZIONE_DOMANDE_DI_ISCRIZIONE;
      	}
      	if(tmp3 != null && tmp3.longValue() == 20){
      	  isCatalogoEle = true;
          stepWizardRicezOfferte = FASE_RICEZIONE_DOMANDE_DI_ISCRIZIONE;
        }




      	pageContext.setAttribute("isGaraLottiConOffertaUnica",
      			"" + isGaraLottiConOffertaUnica, PageContext.REQUEST_SCOPE);
      }

      if(obj.get(4) != null){
        String tmpEleOpEco = ((JdbcParametro) obj.get(4)).getStringValue();
        String catiga="";
        String numeroClassifica="";
        String isFoglia = "";
        String tipoCategoria = "";

        if(tmpEleOpEco != null && !"".equals(tmpEleOpEco)){
          isGaraUsoAlbo = true;
          codiceElenco = tmpEleOpEco;

          Vector<?> datiCatg = sqlManager.getVector("select catiga,numcla from catg where ngara=? and ncatg=1",
              new Object[] { codiceGara });
          if(datiCatg!=null && datiCatg.size()>0){
            catiga = ((JdbcParametro) datiCatg.get(0)).getStringValue();
            Long numcla = ((JdbcParametro) datiCatg.get(1)).longValue();

            if (catiga==null)
              catiga="";

            if(!"".equals(catiga)){
              isFoglia = (String) sqlManager.getObject("select isfoglia from v_cais_tit where caisim = ?", new Object[]{catiga});
              Long tiplavg = (Long)sqlManager.getObject("select tiplavg from cais where caisim = ?", new Object[]{catiga});
              if(tiplavg!=null)
                tipoCategoria = tiplavg.toString();
            }

            if(numcla!=null)
              numeroClassifica = numcla.toString();

          }

        }

        pageContext.setAttribute("isGaraUsoAlbo",
              "" + isGaraUsoAlbo, PageContext.REQUEST_SCOPE);
        if(codiceElenco!=null && !"".equals(codiceElenco))
          pageContext.setAttribute("codiceElenco",
              codiceElenco, PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("codiceCategoriaPrev",
            ""  + catiga, PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("tipoCategoria",
            ""  + tipoCategoria, PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("classifica",
            ""  + numeroClassifica, PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("isFoglia",
            ""  + isFoglia, PageContext.REQUEST_SCOPE);

        pageContext.setAttribute("tipoGara",
            ""  + tipgen, PageContext.REQUEST_SCOPE);

        // Valore del campo GARE.IMPAPP, necessario
        importoTotaleBaseDAsta = (Double) ((JdbcParametro) obj.get(5)).getValue();

        //Valore del campo TORN.IMPTOR
        importoTornata = (Double) ((JdbcParametro) obj.get(6)).getValue();

      }

      if(obj.get(7) != null){
        String carrello = ((JdbcParametro) obj.get(7)).getStringValue();
        String integrazioneAUR="2";
        if(carrello != null && !"".equals(carrello))
          integrazioneAUR="1";
          pageContext.setAttribute("integrazioneAUR", integrazioneAUR, PageContext.REQUEST_SCOPE);

      }

      if(obj.get(8) != null){
        Date data = (Date) ((JdbcParametro) obj.get(8)).getValue();

        if(data != null){
          pageContext.setAttribute("dataTermineRichiestaPartecipazione",
              UtilityDate.convertiData(data, UtilityDate.FORMATO_GG_MM_AAAA));
        }

      }

      if(obj.get(9) != null){
        String ora = (String) ((JdbcParametro) obj.get(9)).getValue();

        if(ora!=null && !"".equals(ora))
          pageContext.setAttribute("oraTermineRichiestaPartecipazione",ora);

      }

      if(obj.get(12) != null){
        String preced = (String) ((JdbcParametro) obj.get(12)).getValue();

        if(preced!=null && !"".equals(preced))
          pageContext.setAttribute("preced",preced);

      }

      if(obj.get(13) != null){
          String calcsome = (String) ((JdbcParametro) obj.get(13)).getValue();

          if(calcsome!=null && !"".equals(calcsome))
            pageContext.setAttribute("calcoloSogliaAnomaliaExDLgs2017",calcsome);

        }

    } catch(SQLException s){
      throw new JspException("Errore durante la lettura del tipo di tornata della gara ", s);
    } catch (GestoreException e) {
      throw new JspException("Errore durante la lettura del numero di classifica della categoria prevalente ", e);
    }

    GestioneFasiGaraFunction.setPaginazione(pageContext,isGareElenco || isCatalogoEle);

    // Determino se la gara e' a lotto unico, la fase di gara e modalita' aggiudicazione
    // della gara e Aggiud.con esclus.autom.(1) o manuale(2) delle offerte
    // anomale del lotto in analisi
    Boolean garaLottoUnico = null;
    Long aggiudicazioneEsclusAutom  = null;
    try {
      Vector<?> datiLotto = sqlManager.getVector(
          "select CODGAR1, FASGAR, MODASTG, STEPGAR from GARE where GARE.NGARA = ?",
          new Object[]{codiceGara});
      if(datiLotto != null){
        codiceTornata = ((JdbcParametro) datiLotto.get(0)).getStringValue();

        if(codiceTornata.startsWith("$"))
          garaLottoUnico = Boolean.TRUE;
        else
          garaLottoUnico = Boolean.FALSE;

        pageContext.setAttribute("garaLottoUnico", garaLottoUnico.toString(),
        		PageContext.REQUEST_SCOPE);

        Long fasgar = ((JdbcParametro) datiLotto.get(1)).longValue();
        pageContext.setAttribute("faseGara", fasgar,
            PageContext.REQUEST_SCOPE);

        Long tmpStepGara = (Long)((JdbcParametro) datiLotto.get(3)).getValue();
        if(isGareElenco || isCatalogoEle){
          if(tmpStepGara != null && tmpStepGara.intValue() != 0 && tmpStepGara != null && tmpStepGara.intValue() != 0)
            stepWizardRicezOfferte = tmpStepGara.intValue();
          else
            stepWizardRicezOfferte = FASE_RICEZIONE_DOMANDE_DI_ISCRIZIONE;
        }else{
          if(tmpStepGara != null && tmpStepGara.intValue() != 0 && tmpStepGara != null && tmpStepGara.intValue() != 0){
            //faseGara = tmpFaseGara.intValue();
            stepWizardRicezOfferte = tmpStepGara.intValue();
            //if(faseGara > FASE_CHIUSURA_RICEZIONE_OFFERTE) faseGara = FASE_CHIUSURA_RICEZIONE_OFFERTE;
            if(stepWizardRicezOfferte > FASE_CHIUSURA_RICEZIONE_OFFERTE)
              stepWizardRicezOfferte = FASE_CHIUSURA_RICEZIONE_OFFERTE;

            //Se FASGAR >= FASE_CALCOLO_AGGIUDICAZIONE vi e' il blocco in sola visualizzazione dei dati della pagina
            if (tmpStepGara.intValue() >= GestioneFasiGaraFunction.FASE_CALCOLO_AGGIUDICAZIONE)
                pageContext.setAttribute("bloccoAggiudicazione", new Long(1),
                      PageContext.REQUEST_SCOPE);
            else
                pageContext.setAttribute("bloccoAggiudicazione", new Long(0),
                      PageContext.REQUEST_SCOPE);
          } else {
            if(isProceduraAggiudicazioneAperta){
              //faseGara = FASE_RICEZIONE_PLICHI;
              stepWizardRicezOfferte = FASE_RICEZIONE_PLICHI;
            } else if(isProceduraNegoziata){
              //faseGara = FASE_ELENCO_DITTE_INVITATE;
              stepWizardRicezOfferte = FASE_ELENCO_DITTE_INVITATE;
            } else {
              //faseGara = FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE;
              stepWizardRicezOfferte = FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE;
            }
          }
        }


        if(((JdbcParametro) datiLotto.get(2)).getValue() != null){
          aggiudicazioneEsclusAutom = (Long) ((JdbcParametro) datiLotto.get(2)).getValue();
          pageContext.setAttribute("aggiudicazioneEsclusAutom",
          		aggiudicazioneEsclusAutom, PageContext.REQUEST_SCOPE);
        }
      }
    } catch(SQLException s){
      throw new JspException("Errore durante la lettura di dati supplementari " +
      		"del lotto", s);
    } catch (GestoreException e) {
      throw new JspException("Errore durante la lettura di dati supplementari " +
          "del lotto", e);
    }

    paginaAttivaWizard = UtilityTags.getParametro(pageContext,
        GestioneFasiRicezioneFunction.PARAM_WIZARD_PAGINA_ATTIVA);
    direzioneWizard = UtilityTags.getParametro(pageContext,
        GestioneFasiRicezioneFunction.PARAM_DIREZIONE_WIZARD);

    // Inizializzazione della variabile alla fase minima
    int wizardPaginaAttiva = 0;
    if(isGareElenco || isCatalogoEle){  //Gare ELENCO
      wizardPaginaAttiva = FASE_RICEZIONE_DOMANDE_DI_ISCRIZIONE;
      if(paginaAttivaWizard == null || (paginaAttivaWizard != null && paginaAttivaWizard.length() == 0)){
        paginaAttivaWizard = "" + stepWizardRicezOfferte;
        wizardPaginaAttiva = stepWizardRicezOfferte;
      }else {
        wizardPaginaAttiva = this.calcoloStepWizardIscrizione(UtilityNumeri.convertiIntero(
            paginaAttivaWizard).intValue(), direzioneWizard);
          paginaAttivaWizard = "" + wizardPaginaAttiva;
      }

    }else{ //Tutte le altre tipologie
      wizardPaginaAttiva = FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE;
      if(paginaAttivaWizard == null || (paginaAttivaWizard != null && paginaAttivaWizard.length() == 0)){
          if(isProceduraAggiudicazioneAperta && stepWizardRicezOfferte < FASE_RICEZIONE_PLICHI){ //if(isProceduraAggiudicazioneAperta && faseGara < FASE_RICEZIONE_PLICHI){
              paginaAttivaWizard = "" + FASE_RICEZIONE_PLICHI;
              wizardPaginaAttiva = FASE_RICEZIONE_PLICHI;
          } else if((isProceduraNegoziata)
                  && stepWizardRicezOfferte < FASE_ELENCO_DITTE_INVITATE){ // && faseGara < FASE_ELENCO_DITTE_INVITATE){
              paginaAttivaWizard = "" + FASE_ELENCO_DITTE_INVITATE;
              wizardPaginaAttiva = FASE_ELENCO_DITTE_INVITATE;
          } else {
              paginaAttivaWizard = "" + stepWizardRicezOfferte; //faseGara;
              wizardPaginaAttiva = stepWizardRicezOfferte; //faseGara;
          }
      } else {
          wizardPaginaAttiva = this.calcoloStepWizard(UtilityNumeri.convertiIntero(
                  paginaAttivaWizard).intValue(), direzioneWizard);
          paginaAttivaWizard = "" + wizardPaginaAttiva;
      }

      // Caricamento dati per la pagina di riepilogo 'Chiusura ricezione offerte'
      if(wizardPaginaAttiva == FASE_CHIUSURA_RICEZIONE_OFFERTE)
        this.getSintesiFaseRicezione(codiceGara, pageContext, sqlManager, isProceduraTelematica);
      // Caricamento dati per la pagina di riepilogo 'Inviti'
      else if(wizardPaginaAttiva == FASE_INVITI){
        this.getSintesiFaseInviti(codiceGara, pageContext, sqlManager, isProceduraTelematica, isProceduraRistretta);

        // Set nel request variabile per settare la visibilita' delle diverse
        // sezioni presenti nella fase 'Inviti'.
          pageContext.setAttribute("isVisibileSezEstremiInvito", Boolean.TRUE, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("isVisibileSezTerminiPresOfferta", Boolean.TRUE, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("isVisibileSezAperturaPlichi", Boolean.TRUE, PageContext.REQUEST_SCOPE);

          //if(gareTipgarg == 1 || gareTipgarg == 3 || gareTipgarg == 5 || gareTipgarg == 13){
          if (isProceduraAggiudicazioneAperta  || isProceduraNegoziata) {
            pageContext.setAttribute("isVisibileSezRiepilogoDitte", Boolean.FALSE, PageContext.REQUEST_SCOPE);
            pageContext.setAttribute("isVisibileSezVerificaRequisiti", Boolean.FALSE, PageContext.REQUEST_SCOPE);
            pageContext.setAttribute("isVisibileSezAttoAmmissioneEsclusione", Boolean.FALSE, PageContext.REQUEST_SCOPE);
          } else {
            pageContext.setAttribute("isVisibileSezRiepilogoDitte", Boolean.TRUE, PageContext.REQUEST_SCOPE);
            pageContext.setAttribute("isVisibileSezVerificaRequisiti", Boolean.TRUE, PageContext.REQUEST_SCOPE);
            pageContext.setAttribute("isVisibileSezAttoAmmissioneEsclusione", Boolean.TRUE, PageContext.REQUEST_SCOPE);
          }
      }
    }

    // Gestione dell'avanzamento del wizard
    this.gestioneAvanzamentoWizard(wizardPaginaAttiva,
        isProceduraAggiudicazioneAperta, isProceduraNegoziata,
        pageContext,isGareElenco,isCatalogoEle,isIndagineMercato );

    // Gestione del filtro sulle ditte in base all'avanzamento del wizard
    if (isGareElenco || isCatalogoEle) {
      String filtro="";
      if (wizardPaginaAttiva == GestioneFasiRicezioneFunction.FASE_APERTURA_DOMANDE_DI_ISCRIZIONE)
        filtro=" and (DITG.FASGAR > -5 or DITG.FASGAR = 0 or DITG.FASGAR is null)";
      else if(wizardPaginaAttiva == GestioneFasiRicezioneFunction.FASE_ELENCO_CONCORRENTI_ABILITATI)
        filtro=" and (DITG.AMMGAR = 1 or DITG.AMMGAR is NULL) and DITG.ABILITAZ = 1";
      pageContext.setAttribute("filtroFaseRicezione", filtro,PageContext.REQUEST_SCOPE);
    } else {
      pageContext.setAttribute("filtroFaseRicezione",
          this.gestioneFiltroFaseRicezione(wizardPaginaAttiva, pageContext),
                  PageContext.REQUEST_SCOPE );
    }

    pageContext.setAttribute("paginaAttivaWizard", new Integer(wizardPaginaAttiva),
    		PageContext.REQUEST_SCOPE);

    //Nel caso di gara elenco si deve controllo se vi sono comunicazioni da processare
    //inviate dal portale alice
    if((isGareElenco || isCatalogoEle) && wizardPaginaAttiva == GestioneFasiRicezioneFunction.FASE_RICEZIONE_DOMANDE_DI_ISCRIZIONE){
      String select="select count(idprg) from W_INVCOM where idprg = ? and comstato = ? and (comtipo = ? or comtipo = ?) and comkey2 = ?";
      try {
        Long numComunicazioni = (Long)sqlManager.getObject(select,
            new Object[]{"PA","5","FS2","FS4",codiceGara});
        if(numComunicazioni!= null && numComunicazioni.longValue()>0){
          pageContext.setAttribute("comunicazioniPortale","SI",PageContext.REQUEST_SCOPE );
        }
      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura delle comunicazioni inviate dal portale ", e);
      }
    }


    //nel caso di gare ad offerta unica l'importo da prendere in considerazione per
    //determinare il numero di ditte iscritte e del numero minime ditte da
    //iscrivere nella gara è IMPTOR.TORN (CRISTIAN deve fare sapere se è definitivo)
    if(isGaraLottiConOffertaUnica){
      importoTotaleBaseDAsta = importoTornata;
    }


    // Gestione del numero di ditte iscritte e del numero minime ditte da
    // iscrivere nella gara in funzione del campo TORN.TIPGARG
    if(codiceElenco != null){
      String whereFasiRicezione = this.gestioneFiltroFaseRicezione(wizardPaginaAttiva, pageContext);

      try {
        Long vet[] = pgManager.getNumeroMinimoDitte(gareTipgarg, tipgen, importoTotaleBaseDAsta, codiceGara, whereFasiRicezione);
        if (vet!= null && vet.length>1){
          pageContext.setAttribute("numeroMinimoOperatori",
              vet[0], PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("numeroOperatoriSelezionati",
              vet[1], PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("numSupDitteSel",
              vet[2], PageContext.REQUEST_SCOPE);
        }
      } catch (GestoreException e) {
        throw new JspException("Errore durante la lettura del numero minimo " +
            "degli operatori da selezionare", e);
      }

      ProfiloUtente profiloUtente = (ProfiloUtente) this.getRequest().getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      String modalitaSelezione = pgManager.getModalitaSelezioneDitteElenco(profiloUtente);
      pageContext.setAttribute("modalitaSelezioneDitteElenco", modalitaSelezione, PageContext.REQUEST_SCOPE);
    }


    // Nel request viene messo anche il valore che deve assumere i campi GARE.FASGAR
    // o DITG.FASGAR nel caso in cui la ditta venga esclusa
    Double d = new Double(Math.floor(new Long(wizardPaginaAttiva).doubleValue()/10));
    pageContext.setAttribute("fasGarPerEsclusioneDitta", new Long(d.longValue()),
    		PageContext.REQUEST_SCOPE);

    String updateLista = UtilityTags.getParametro(pageContext,
    		UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA);
    if (updateLista == null || updateLista.length() == 0) updateLista = "0";
    pageContext.setAttribute(UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA, updateLista,
        PageContext.REQUEST_SCOPE);

    // Nella fase di ricezione delle offerte in modalita' modifica e' necessario
    // confrontare la data di arrivo offerte DITG.DATOFF con la data di termine
    // ricezione offerte, che è data sempre da DTEOFF.TORN. Solo nel caso di lotti di gara,
    // si considera DTEOFF.GARE se valorizzato.
    if (wizardPaginaAttiva == FASE_RICEZIONE_PLICHI ) {
      try {
        if(garaLottoUnico.booleanValue()){
          Date data = null;
          String ora = null;

          Vector<?> datiTorn = sqlManager.getVector(
              "select DTEOFF,OTEOFF from TORN where codgar = ?", new Object[]{"$"+codiceLotto});

          if (datiTorn.get(0) != null)
            data = (Date) ((JdbcParametro) datiTorn.get(0)).getValue();

          if (data != null) {
            pageContext.setAttribute("dataTerminePresentazioneOfferta",
                UtilityDate.convertiData(data, UtilityDate.FORMATO_GG_MM_AAAA));
          }

          if (datiTorn.get(1) != null)
            ora = (String) ((JdbcParametro) datiTorn.get(1)).getValue();

          if (ora != null && !"".equals(ora))
            pageContext.setAttribute("oraTerminePresentazioneOfferta",ora);

        } else if (isGaraLottiConOffertaUnica) {
          Date data = null;
          String ora = null;

          Vector<?> datiTorn = sqlManager.getVector(
              "select DTEOFF,OTEOFF from TORN where codgar = ?", new Object[]{codiceTornata});

          if (datiTorn.get(0) != null)
            data = (Date) ((JdbcParametro) datiTorn.get(0)).getValue();

          if (data != null) {
          	pageContext.setAttribute("dataTerminePresentazioneOfferta",
          			UtilityDate.convertiData(data, UtilityDate.FORMATO_GG_MM_AAAA));
          }

          if (datiTorn.get(1) != null)
            ora = (String) ((JdbcParametro) datiTorn.get(1)).getValue();

          if (ora != null && !"".equals(ora))
        	pageContext.setAttribute("oraTerminePresentazioneOfferta",ora);

        } else {
          Date data = null;
          String ora = null;

          Vector<?> datiGare = sqlManager.getVector(
            "select DTEOFF,OTEOFF from GARE where ngara = ?", new Object[]{codiceGara});

          if(datiGare.get(0) != null)
            data = (Date) ((JdbcParametro) datiGare.get(0)).getValue();

          if(data != null){
            pageContext.setAttribute("dataTerminePresentazioneOfferta",
               UtilityDate.convertiData(data, UtilityDate.FORMATO_GG_MM_AAAA));

            if(datiGare.get(1) != null)
              ora = (String) ((JdbcParametro) datiGare.get(1)).getValue();

            if(ora != null && !"".equals(ora))
              pageContext.setAttribute("oraTerminePresentazioneOfferta",ora);

          } else {
            Vector<?> datiTorn = sqlManager.getVector(
               "select DTEOFF,OTEOFF from TORN where codgar = ?", new Object[]{codiceTornata});

            if (datiTorn.get(0) != null)
              data = (Date) ((JdbcParametro) datiTorn.get(0)).getValue();

            if (data != null) {
                pageContext.setAttribute("dataTerminePresentazioneOfferta",
                   UtilityDate.convertiData(data, UtilityDate.FORMATO_GG_MM_AAAA));
                if (datiTorn.get(1) != null)
                  ora = (String) ((JdbcParametro) datiTorn.get(1)).getValue();

                if (ora != null && !"".equals(ora))
                  pageContext.setAttribute("oraTerminePresentazioneOfferta",ora);
             }
          }
        }
      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura della data di e dell'ora di " +
                "termine ricezione offerte", e);
      }
    }

    //Per le gare ad elenco, nello step dell'apertura delle domande di iscrizione in modifica,
    //carico nel request GAREALBO.TIPOLOGIA e GAREALBO.TCONGELAMENTO, che verranno adoperati
    //nei javascript
    if ((isGareElenco || isCatalogoEle) && wizardPaginaAttiva == FASE_APERTURA_DOMANDE_DI_ISCRIZIONE ) {
      try {
        Vector<?> datiGAREALBO = sqlManager.getVector("select tipologia,tcongelamento,valiscr,apprin from garealbo " +
            " where codgar = ? and ngara = ? ", new Object[]{codiceTornata,codiceGara});
        if (datiGAREALBO.get(0) != null) {
          Long tipologia = ((JdbcParametro) datiGAREALBO.get(0)).longValue();
          Long valiscr = ((JdbcParametro) datiGAREALBO.get(2)).longValue();
          Long apprin = ((JdbcParametro) datiGAREALBO.get(3)).longValue();
          pageContext.setAttribute("tipologiaElenco",tipologia, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("giorniValidita",valiscr, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("tipoRinnovo",apprin, PageContext.REQUEST_SCOPE);
          if (tipologia != null && tipologia.longValue() == 2) {
            if (datiGAREALBO.get(1) != null) {
              Long tempoCongelamento = ((JdbcParametro) datiGAREALBO.get(1)).longValue();
              if (tempoCongelamento != null)
                pageContext.setAttribute("tempoCongelamento",tempoCongelamento.toString(), PageContext.REQUEST_SCOPE);
            }
          }
        }
      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura della tipologia di elenco " +
            "e del tempo di congelamento", e);
      } catch (GestoreException e) {
        throw new JspException("Errore durante la lettura della tipologia di elenco", e);
      }
    }

    if ((isGareElenco || isCatalogoEle) && wizardPaginaAttiva == FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE ) {
      try {
        String iscrirt = (String)sqlManager.getObject("select iscrirt from garealbo where codgar = ? and ngara = ? ",
            new Object[]{codiceTornata,codiceGara});
        pageContext.setAttribute("iscrizioneRT",iscrirt, PageContext.REQUEST_SCOPE);

      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura della tabella GAREALBO ", e);
      }

    }

    if(isGareElenco || isCatalogoEle){
      try {
        String tipoele = (String)sqlManager.getObject("select tipoele from garealbo where codgar = ? and ngara = ? ",
            new Object[]{codiceTornata,codiceGara});
        pageContext.setAttribute("tipoele",tipoele, PageContext.REQUEST_SCOPE);

      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura della tabella GAREALBO ", e);
      }
    }

    // Relativamente al controllo ditte escluse o vincitrici in altri lotti
    // della gara (Vedi analisi: paragrafo 9.4.1)
    HashMap gareModificate = null;
    try {
      HttpSession sessione = pageContext.getSession();
      if(sessione.getAttribute(GestioneFasiRicezioneFunction.SESSIONE_RICEZIONE_DOMANDE_OFFERTE_DITTE_MODIFICATE) != null)
        gareModificate = (HashMap) sessione.getAttribute(
            GestioneFasiRicezioneFunction.SESSIONE_RICEZIONE_DOMANDE_OFFERTE_DITTE_MODIFICATE);
      else {
        gareModificate = new HashMap();
        sessione.setAttribute(GestioneFasiRicezioneFunction.SESSIONE_RICEZIONE_DOMANDE_OFFERTE_DITTE_MODIFICATE,
            gareModificate);
      }

      if((! garaLottoUnico.booleanValue()) &&
         UtilityNumeri.convertiIntero(paginaAttivaWizard).intValue() <= 1 &&
         isGaraLottiOmogenea.booleanValue()){

        /* Il parameter presente nella jsp 'ditteVincitrici_escluseDaAltriLotti'
         * viene valorizzato solo se viene premuto il pulsante di modifica.
         * Quindi il fatto che tale parameter non sia presente nel request o non
         * sia valorizzato indica che:
         * - non e' mai stata richiesta l'apertura in modifica della lista stessa;
         * - e' necessario verificare la presenza in sessione del codice del
         *   lotto attuale nell'oggetto presente in sessione
         *   GestioneFasiGaraFunction.SESSIONE_RICEZIONE_DOMANDE_OFFERTE_DITTE_MODIFICATE;
         * - e' necessario verificare se fra le ditte del lotto attuale ci sono
         *   ditte escluse in altri lotti (per motivi diversi da "Vincitrice di
         *   altri lotti"): se si, inserire nel request l'attributo
         *   confermaDitteVincitrici_O_EscluseDaAltriLotti per chiedere la
         *   conferma dell'esclusione dal lotto attuale di ditte escluse in altri
         *   lotti (ma non vincitrici di altri lotti);
         * Se il parametro e' valorizzato:
         * - se vale "1", allora il client ha dato conferma l'esclusione dal lotto
         *   attuale di ditte escluse in altri lotti (ma non vincitrici di altri lotti)
         * - se vale "2", allora o il client non ha dato conferma di esclusione
         *   dal lotto attuale di ditte escluse in altri lotti (ma non vincitrici
         *   di altri lotti), oppure l'esclusione e' gia' stata eseguita almeno
         *   una volta.
         *
         * Comunque l'esclusione dal lotto attuale della ditta vincitrice di altri
         * lotti avviene ogni volta che si apre la lista in modifica.
         */
        String ditteVincitrici_escluseDaAltriLotti =
            this.getRequest().getParameter("ditteVincitrici_escluseDaAltriLotti");
        if (ditteVincitrici_escluseDaAltriLotti == null ||
            (ditteVincitrici_escluseDaAltriLotti != null &&
                ditteVincitrici_escluseDaAltriLotti.length() == 0)) {
          if (gareModificate.get(codiceLotto) == null) {
            if (pgManager.verificheEsclusioneDitteAltriLotti(codiceTornata, codiceLotto))
              pageContext.setAttribute("confermaDitteVincitrici_O_EscluseDaAltriLotti", "1");
          }
        } else {
          boolean aggiornaDitteEscluseDaAltriLotti = "1".equals(ditteVincitrici_escluseDaAltriLotti);
          // aggiornamento dei dati su DB per le ditte vincitrici o escluse
          // in altri lotti
          pgManager.updateDitteEscluseVincitriciAltriLotti(codiceTornata,
              codiceLotto, aggiornaDitteEscluseDaAltriLotti);

          // Aggiorna l'hash map in sessione con il codice della gara a cui sono
          // stati appena aggiornati i dati
          gareModificate.put(codiceLotto, codiceLotto);
        }
      }
    } catch (GestoreException g) {
      throw new JspException(g.getMessage(), g);
    }


    //Nel caso di gara telematica ed offerta unica con bustalotti=2 si devono controllare i partgar di tutti
    // i lotti
    //Poichè il controllo serve pure per l'apertura del dettaglio ammissione lotti, non si considera la condizione su bustalotti
    if("true".equals(isProceduraTelematica) && isGaraLottiConOffertaUnica && wizardPaginaAttiva == FASE_APERTURA_DOMANDE_DI_PARTECIPAZIONE){
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

    // Creazione del parametro con la chiave da passare alla pagina di controllo
    // delle autorizzazioni
    String inputFiltro = "CODGAR=T:".concat(codiceTornata);
    pageContext.setAttribute("inputFiltro", inputFiltro, PageContext.REQUEST_SCOPE);

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

    //Valori in sessione adoperati per il filtro della pagina di selezione operatori economici
    HttpSession sessione = pageContext.getSession();
    sessione.setAttribute("filtro", null);
    sessione.setAttribute("filtroSpecifico", null);
    sessione.setAttribute("filtroZoneAtt", null);
    sessione.setAttribute("filtroAffidatariEsclusi", null);
    sessione.setAttribute("modalitaFiltroCategorie", null);
    sessione.setAttribute("applicatoFiltroInOr", null);
    sessione.setAttribute("elencoUlterioriCategorie", null);
    sessione.setAttribute("elencoNumcla", null);
    sessione.setAttribute("elencoTiplavgUltCategorie", null);
    sessione.setAttribute("prevalenteSelezionata", null);
    sessione.setAttribute("elencoIdFiltriSpecifici", null);
    sessione.setAttribute("elencoIdZoneAttivita", null);
    sessione.setAttribute("elencoAffidatariEsclusi", null);

    return null;
  }

  /**
   * Gestione dei titoli dei vari passi del wizard, distinguendo fra quelli
   * gia' visitati e quelli da visitare
   *
   * @param paginaAttivaWizard
   * @param isProceduraAggiudicazioneAperta
   * @param pageContext
   */
  private void gestioneAvanzamentoWizard(int paginaAttivaWizard,
      boolean isProceduraAggiudicazioneAperta, boolean isProceduraNegoziata,
      PageContext pageContext, boolean isGaraElenco, boolean isCatalogoEle, boolean isIndagineMercato){
    List<String> listaPagineVisitate = new ArrayList<String>();
    List<String> listaPagineDaVisitare = new ArrayList<String>();
    int indicePartenza = 0;
    // Mapping fra la fase di gara attiva (paginaAttivaWizard) e il titolo
    // della fase stessa
    int indiceLimite = 0;
    if (!isGaraElenco && !isCatalogoEle){
      switch(paginaAttivaWizard) {
        case FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE:
          indiceLimite = 1;
          break;
        case FASE_APERTURA_DOMANDE_DI_PARTECIPAZIONE:
          indiceLimite = 2;
          break;
        case FASE_ELENCO_DITTE_INVITATE:
          indiceLimite = 3;
          break;
        case FASE_INVITI:
          indiceLimite = 4;
          break;
        case FASE_RICEZIONE_PLICHI:
          indiceLimite = 5;
          break;
        case FASE_CHIUSURA_RICEZIONE_OFFERTE:
          indiceLimite = 6;
          break;
        }

      if(isProceduraAggiudicazioneAperta)
        indicePartenza = 4;
      else if(isProceduraNegoziata)
          indicePartenza = 2;
      int indiceFine = TITOLO_FASI_RICEZIONE.length;
      if(isIndagineMercato)
        indiceFine = 2; //Si visualizzano solo le pagine ricezione domande e apertura domande di partecipazione

      for(int i = indicePartenza; i < indiceLimite; i++)
        listaPagineVisitate.add(TITOLO_FASI_RICEZIONE[i]);
      for(int i = indiceLimite; i < indiceFine; i++)
          listaPagineDaVisitare.add(TITOLO_FASI_RICEZIONE[i]);

    }else{
      switch(paginaAttivaWizard) {
        case FASE_RICEZIONE_DOMANDE_DI_ISCRIZIONE:
          indiceLimite = 1;
          break;
        case FASE_APERTURA_DOMANDE_DI_ISCRIZIONE:
          indiceLimite = 2;
          break;
        case FASE_ELENCO_CONCORRENTI_ABILITATI:
          indiceLimite = 3;
          break;
      }

      int indiceFine = TITOLO_FASI_ISCRIZIONE.length;

      for(int i = indicePartenza; i < indiceLimite; i++)
        listaPagineVisitate.add(TITOLO_FASI_ISCRIZIONE[i]);
      for(int i = indiceLimite; i < indiceFine; i++)
          listaPagineDaVisitare.add(TITOLO_FASI_ISCRIZIONE[i]);

    }

    pageContext.setAttribute("pagineVisitate", listaPagineVisitate);
    pageContext.setAttribute("pagineDaVisitare", listaPagineDaVisitare);
  }

  private String gestioneFiltroFaseRicezione(int faseGaraAttiva, PageContext pageContext){
    StringBuffer result = new StringBuffer("");

    switch (faseGaraAttiva){
    case GestioneFasiRicezioneFunction.FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE:
      result.append("and (DITG.ACQUISIZIONE is null or (DITG.ACQUISIZIONE <> 5  and DITG.ACQUISIZIONE <> 8 and DITG.ACQUISIZIONE <> 9))");
      break;
    case GestioneFasiRicezioneFunction.FASE_CHIUSURA_RICEZIONE_OFFERTE:
      result.append("and (DITG.ACQUISIZIONE is null or DITG.ACQUISIZIONE <> 5)");
      break;
    case GestioneFasiRicezioneFunction.FASE_APERTURA_DOMANDE_DI_PARTECIPAZIONE:
      result.append("and (DITG.FASGAR > -5 or DITG.FASGAR = 0 or DITG.FASGAR is null) and (DITG.ACQUISIZIONE is null or (DITG.ACQUISIZIONE <> 5  and DITG.ACQUISIZIONE <> 8 and DITG.ACQUISIZIONE <> 9))");
      break;
    case GestioneFasiRicezioneFunction.FASE_ELENCO_DITTE_INVITATE:
      result.append("and (DITG.FASGAR > -4 or DITG.FASGAR = 0 or DITG.FASGAR is null) and (DITG.ACQUISIZIONE is null or DITG.ACQUISIZIONE <> 5 )");
      break;
    case GestioneFasiRicezioneFunction.FASE_RICEZIONE_PLICHI:
      result.append("and (DITG.FASGAR > -3 or DITG.FASGAR = 0 or DITG.FASGAR is null) and DITG.RTOFFERTA is null");
      break;
    }

    return result.toString();
  }

  private void getSintesiFaseRicezione(String codiceGara, PageContext pageContext,
      SqlManager sqlManager, String isProceduraTelematica) throws JspException{
    try {
      Long numeroDittePartecipanti = (Long) sqlManager.getObject(
          "select count(*) from DITG " +
           "where NGARA5 = ? " +
           "and (ACQUISIZIONE IS NULL OR (ACQUISIZIONE <> 5 AND ACQUISIZIONE <> 8 AND ACQUISIZIONE <> 9))", new Object[]{codiceGara});

      Long numeroDitteInvitate = (Long) sqlManager.getObject(
          "select count(*) from DITG " +
           "where NGARA5 = ? " +
             "and (INVGAR IS NULL OR INVGAR <> ?) " +
             "and (FASGAR IS NULL OR FASGAR >= ?)" +
             "and (ACQUISIZIONE IS NULL OR ACQUISIZIONE <> 5)",
            new Object[]{codiceGara, new Long(2),
          	new Long(FASE_ELENCO_DITTE_INVITATE/10)});

      Long numeroDitteConOfferta = null;

      if("true".equals(isProceduraTelematica)){
        numeroDitteConOfferta = (Long) sqlManager.getObject(
          "select count(*) from DITG " +
           "where NGARA5 = ? " +
             "and (INVOFF = ?) ",
            new Object[]{codiceGara, new Long(1)});
        if(numeroDitteConOfferta!=null && numeroDitteConOfferta.longValue()==0){
          //Si deve distinguere fra il caso in cui non ci sia nessuna ditta con INVOFF
          //valorizzato ed il caso invece in cui siano tutti 2, nel primo caso numeroDitteConOfferta=null
          //nel secondo caso numeroDitteConOfferta=0
          Long numDitteSenzaOfferta = (Long) sqlManager.getObject(
              "select count(*) from DITG " +
              "where NGARA5 = ? " +
                "and (INVOFF = ?) ",
               new Object[]{codiceGara, new Long(2)});
          if(numDitteSenzaOfferta!=null && numDitteSenzaOfferta.longValue()>0)
            numeroDitteConOfferta= new Long(0);
          else
            numeroDitteConOfferta= null;

        }

      }else{
        numeroDitteConOfferta = (Long) sqlManager.getObject(
            "select count(*) from DITG " +
             "where NGARA5 = ? " +
             "and (INVOFF IS NULL OR INVOFF <> ?) " +
               "and (FASGAR IS NULL OR FASGAR > ?)",
              new Object[]{codiceGara, new Long(2),
              new Long(FASE_ELENCO_DITTE_INVITATE/10)});
      }
      Long numeroDitteAmmesse = (Long) sqlManager.getObject(
          "select count(*) from DITG " +
           "where NGARA5 = ? " +
             "and (INVOFF IS NULL OR INVOFF <> ?) " +
             "and (FASGAR IS NULL OR FASGAR > 1)",
             new Object[]{codiceGara, new Long(2)});

      pageContext.setAttribute("numeroDittePartecipanti", numeroDittePartecipanti, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("numeroDitteInvitate", numeroDitteInvitate, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("numeroDitteConOfferta", numeroDitteConOfferta, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("numeroDitteAmmesse", numeroDitteAmmesse, PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre i dati di sintesi relativi " +
            "alle ditte inserite in gara ", e);
    }
  }

  private void getSintesiFaseInviti(String codiceGara, PageContext pageContext,
      SqlManager sqlManager, String isProceduraTelematica, boolean isProceduraRistretta) throws JspException{
    try {
    	// Numero ditte che hanno presentato domanda di partecipazione
      Long numeroDittePartecipanti = (Long) sqlManager.getObject(
      		"select count(*) from DITG where NGARA5 = ? and (acquisizione is null or (acquisizione <>5 and acquisizione<>8 and acquisizione<>9))", new Object[]{codiceGara});

      // Numero ditte escluse
      Long numeroDitteEscluse = (Long) sqlManager.getObject(
      		"select count(*) from DITG where NGARA5 = ? and AMMGAR = ? " +
      		"and (FASGAR is not null and FASGAR <= ?)" +
      		  "and rtofferta is null",
      		new Object[]{codiceGara, new Long(2),
      		    new Long(FASE_ELENCO_DITTE_INVITATE/10)});

      // Numero ditte da invitare
      Long numeroDitteDaInvitare = (Long) sqlManager.getObject(
          "select count(*) from DITG " +
           "where NGARA5 = ? " +
             "and (INVGAR <> ? OR INVGAR is NULL) " +
             "and (FASGAR IS NULL OR FASGAR > ?) " +
             "and (acquisizione is null or acquisizione <>5)",
          new Object[]{codiceGara, new Long(2),
            	new Long(FASE_ELENCO_DITTE_INVITATE/10)});

      // Di cui numero ditte ammesse con riserva
      String selectNumDitteAmmConRiserva="select  count(dg.dittao) from DITG dg, DITGAMMIS da" +
               " where dg.NGARA5 = ? and dg.ngara5=da.ngara and dg.dittao=da.dittao" +
               " and (dg.INVGAR <> ? or dg.INVGAR is NULL) and da.ammgar = ?" +
               " and (dg.acquisizione is null or dg.acquisizione <>5) and da.fasgar<=?";
      Long numeroDiCuiDitteAmmesseConRiserva =(Long) sqlManager.getObject(selectNumDitteAmmConRiserva,
          new Object[]{codiceGara, new Long(2), new Long(3), new Long(-3)});

      Long numeroDitteInvito = null;
      if("true".equals(isProceduraTelematica) && isProceduraRistretta){
        numeroDitteInvito = (Long) sqlManager.getObject(
            "select count(*) from DITG where NGARA5 = ? and (acquisizione = ? or acquisizione = ?)",
              new Object[]{codiceGara, new Long(8), new Long(9)});
      }

      pageContext.setAttribute("numeroDittePartecipanti", numeroDittePartecipanti,
      		PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("numeroDitteEscluse", numeroDitteEscluse,
      		PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("numeroDitteDaInvitare", numeroDitteDaInvitare,
      		PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("numeroDiCuiDitteAmmesseConRiserva",
      		numeroDiCuiDitteAmmesseConRiserva, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("numeroDitteInvito", numeroDitteInvito, PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre i dati di sintesi relativi " +
            "alle fase 'Inviti'", e);
    }
  }

  private int calcoloStepWizard(int faseGara, String direzioneWizard){
  	if(direzioneWizard != null && direzioneWizard.length() > 0){
	  	if(direzioneWizard.equalsIgnoreCase("AVANTI"))
	  		return this.getStepWizardSuccessivo(faseGara);
	  	else if(direzioneWizard.equalsIgnoreCase("INDIETRO"))
	  		return this.getStepWizardPrecedente(faseGara);
	  	else return FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE;
  	} else return faseGara;
  }

  /**
   *
   * @param faseGara
   * @return Ritorna la fase di ricezione successiva a quella ricevuta dall'argomento
   */
  private int getStepWizardSuccessivo(int faseGara){
  	int result = FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE;

  	if(faseGara < FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE)
  		result = FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE;
  	else if(faseGara > FASE_CHIUSURA_RICEZIONE_OFFERTE)
  		result = FASE_CHIUSURA_RICEZIONE_OFFERTE;
  	else {
  		switch(faseGara){
  		case FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE:
  			result = FASE_APERTURA_DOMANDE_DI_PARTECIPAZIONE;
  			break;
  		case FASE_APERTURA_DOMANDE_DI_PARTECIPAZIONE:
  			result = FASE_ELENCO_DITTE_INVITATE;
  			break;
  		case FASE_ELENCO_DITTE_INVITATE:
  			result = FASE_INVITI;
  			break;
  		case FASE_INVITI:
  			result = FASE_RICEZIONE_PLICHI;
  			break;
  		case FASE_RICEZIONE_PLICHI:
  			result = FASE_CHIUSURA_RICEZIONE_OFFERTE;
  			break;
  		case FASE_CHIUSURA_RICEZIONE_OFFERTE:  // Per prevenire casi di errore
  			result = FASE_CHIUSURA_RICEZIONE_OFFERTE;
  			break;
  		}
  	}

  	return result;
  }

  /**
   * @param faseGara
   * @return Ritorna la fase di ricezione precedente a quella ricevuta dall'argomento
   */
  private int getStepWizardPrecedente(int faseGara){
  	int result = FASE_CHIUSURA_RICEZIONE_OFFERTE;

  	if(faseGara < FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE)
  		result = FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE;
  	else if(faseGara > FASE_CHIUSURA_RICEZIONE_OFFERTE)
  		result = FASE_CHIUSURA_RICEZIONE_OFFERTE;
  	else {
  		switch(faseGara){
  		case FASE_CHIUSURA_RICEZIONE_OFFERTE:
  			result = FASE_RICEZIONE_PLICHI;
  			break;
  		case FASE_RICEZIONE_PLICHI:
  			result = FASE_INVITI;
  			break;
  		case FASE_INVITI:
  			result = FASE_ELENCO_DITTE_INVITATE;
  			break;
  		case FASE_ELENCO_DITTE_INVITATE:
  			result = FASE_APERTURA_DOMANDE_DI_PARTECIPAZIONE;
  			break;
  		case FASE_APERTURA_DOMANDE_DI_PARTECIPAZIONE:
  			result = FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE;
  			break;
  		case FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE:  // Per prevenire casi di errore
  			result = FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE;
  			break;
  		}
  	}
  	return result;
  }

  private int calcoloStepWizardIscrizione(int faseGara, String direzioneWizard){
    if(direzioneWizard != null && direzioneWizard.length() > 0){
        if(direzioneWizard.equalsIgnoreCase("AVANTI"))
            return this.getStepWizardSuccessivoIscrizione(faseGara);
        else if(direzioneWizard.equalsIgnoreCase("INDIETRO"))
            return this.getStepWizardPrecedenteIscrizione(faseGara);
        else return FASE_RICEZIONE_DOMANDE_DI_ISCRIZIONE;
    } else return faseGara;
  }

  /**
   *
   * @param faseGara
   * @return Ritorna la fase di ricezione successiva a quella ricevuta dall'argomento
   *         per la pagina dell'iscrizione
   */
  private int getStepWizardSuccessivoIscrizione(int faseGara){
    int result = FASE_RICEZIONE_DOMANDE_DI_ISCRIZIONE;

    if(faseGara < FASE_RICEZIONE_DOMANDE_DI_ISCRIZIONE)
        result = FASE_RICEZIONE_DOMANDE_DI_ISCRIZIONE;
    else if(faseGara > FASE_ELENCO_CONCORRENTI_ABILITATI)
        result = FASE_ELENCO_CONCORRENTI_ABILITATI;
    else {
        switch(faseGara){
        case FASE_RICEZIONE_DOMANDE_DI_ISCRIZIONE:
            result = FASE_APERTURA_DOMANDE_DI_ISCRIZIONE;
            break;
        case FASE_APERTURA_DOMANDE_DI_ISCRIZIONE:
            result = FASE_ELENCO_CONCORRENTI_ABILITATI;
            break;
        case FASE_ELENCO_CONCORRENTI_ABILITATI:
            result = FASE_ELENCO_CONCORRENTI_ABILITATI;
            break;
        }
    }

    return result;
  }

  /**
   * @param faseGara
   * @return Ritorna la fase di ricezione precedente a quella ricevuta dall'argomento
   *         per la pagina dell'iscrizione
   */
  private int getStepWizardPrecedenteIscrizione(int faseGara){
    int result = FASE_ELENCO_CONCORRENTI_ABILITATI;

    if(faseGara < FASE_RICEZIONE_DOMANDE_DI_ISCRIZIONE)
        result = FASE_RICEZIONE_DOMANDE_DI_ISCRIZIONE;
    else if(faseGara > FASE_ELENCO_CONCORRENTI_ABILITATI)
        result = FASE_ELENCO_CONCORRENTI_ABILITATI;
    else {
        switch(faseGara){
        case FASE_ELENCO_CONCORRENTI_ABILITATI:
            result = FASE_APERTURA_DOMANDE_DI_ISCRIZIONE;
            break;
        case FASE_APERTURA_DOMANDE_DI_ISCRIZIONE:
            result = FASE_RICEZIONE_DOMANDE_DI_ISCRIZIONE;
            break;
        case FASE_RICEZIONE_DOMANDE_DI_ISCRIZIONE:
            result = FASE_RICEZIONE_DOMANDE_DI_ISCRIZIONE;
            break;
        }
    }
    return result;
  }

}