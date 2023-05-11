/*
 * Created on 09/03/15
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import intra.regionemarche.BandoClass;
import intra.regionemarche.ResultClass;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.SetProfiloAction;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneATCManager;
import it.eldasoft.sil.pg.bl.GestioneRegioneMarcheManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.bl.ScpManager;
import it.eldasoft.sil.pg.bl.scp.LoginResult;
import it.eldasoft.sil.pg.bl.scp.PubblicaAttoEntry;
import it.eldasoft.sil.pg.bl.scp.PubblicazioneAttoResult;
import it.eldasoft.sil.pg.bl.scp.ValidateEntry;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.internetsoluzioni.www.WebService.ResponseWS;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire la storicizzazione
 * della rettifica
 *
 * @author Marcello Caminiti
 */
public class GestorePopupRettificaTermini extends
    AbstractGestoreEntita {

  public GestorePopupRettificaTermini() {
    super(false);
  }

  /** Logger Log4J di classe */
  static Logger          logger = Logger.getLogger(SetProfiloAction.class);

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;

  private ScpManager scpManager;

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
    scpManager = (ScpManager) UtilitySpring.getBean("scpManager",
        this.getServletContext(), ScpManager.class);
  }

  @Override
  public String getEntita() {
    return "TORN";
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    // lettura dei parametri di input
    String codgar = StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
      "codgar"));
    Timestamp data = null;
    Timestamp data_orig = null;
    String ora = null;
    String ora_orig = null;
    Timestamp data1 = null;
    Timestamp data1_orig = null;
    Timestamp data2 = null;
    Timestamp data2_orig = null;
    String termineVisualizzato = StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
      "termineVisualizzato"));
    int termineVisualizzatoIntero = Integer.parseInt(termineVisualizzato);
    String updateTorn="";
    Object parametri[]=null;
    //variabili per tracciatura eventi
    String messageKey = null;
    int livEvento = 3;
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
    String descrTermineVisualizzato = "";
    String codGara = "";
    String tipologiaScp = "";
    //Gestione integrazione Regione Marche
    String tipoGara=StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
        "tipoGara"));
    String bandoPubblicato=StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
        "pubblicazionePortaleBando"));

    String iterga = StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
        "iterga"));
    boolean gestioneRegioneMarche=false;
    if (ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TIPO)!=null
        && "1".equals(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TIPO))){
      if("TRUE".equals(bandoPubblicato))
        gestioneRegioneMarche=true;
    }

    GestioneATCManager gestioneATCManager=null;
    boolean gestioneATC=false;
    if (ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TIPO)!=null
        && "2".equals(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TIPO))
        && ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_USER)!=null
        && !"".equals(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_USER))
        && ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TOKEN)!=null
        && !"".equals(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TOKEN))
        && ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_URL)!=null
        && !"".equals(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_URL))){
      if("TRUE".equals(bandoPubblicato))
        gestioneATC=true;

      gestioneATCManager = (GestioneATCManager) UtilitySpring.getBean("gestioneATCManager",
          this.getServletContext(), GestioneATCManager.class);
    }

    try {

      codGara = codgar;
      String genere = null;
      Vector datiGenerali = sqlManager.getVector("select genere, codice from V_GARE_GENERE where codgar = ?", new Object[]{codgar});
      if (datiGenerali != null) {
        genere = datiGenerali.get(0).toString();
        String codice = datiGenerali.get(1).toString();
        if("2".equals(genere)){
          codGara = codice;
        }
      }
      Response risultatoScp;

      switch (termineVisualizzatoIntero) {
      case 1:
        //Termini presentazione domanda di partecipazione
        descrTermineVisualizzato = "Rettifica termini di presentazione domanda di partecipazione";
        data = datiForm.getData("DTEPAR");
        data_orig = datiForm.getData("DTEPAR_ORIG");
        ora = datiForm.getString("OTEPAR");
        ora_orig = datiForm.getString("OTEPAR_ORIG");
        data1 = datiForm.getData("DTERMRICHCDP");
        data1_orig = datiForm.getData("DTERMRICHCDP_ORIG");
        data2 =datiForm.getData("DTERMRISPCDP");
        data2_orig =datiForm.getData("DTERMRISPCDP_ORIG");
        if("2".equals(iterga) || "4".equals(iterga) || "7".equals(iterga)){
          if(gestioneRegioneMarche){
            ResultClass risposta=gestioneAggiornamentoBando(codgar,tipoGara,data,data_orig,ora,ora_orig);
            if(risposta!=null && !risposta.isResult()){
              String msg=risposta.getError();
              messageKey = "errors.gestoreException.*.ws.bandiRegioneMarche.updateBando.esitoNOK";
              errMsgEvento = this.resBundleGenerale.getString(messageKey);
              errMsgEvento= errMsgEvento.replace("{0}", msg);
              throw new GestoreException("L'aggiornamento del bando da parte del servizio remoto non e' andato a buon fine", "ws.bandiRegioneMarche.updateBando.esitoNOK", new Object[]{msg}, new Exception());
            }
          }
          if(gestioneATC ){
            ResponseWS risposta=gestioneATCManager.updateTerminiBando(codgar,tipoGara,data,data_orig,ora,ora_orig);
            if(risposta!=null && risposta.getMessage()!=null){
              String msg=risposta.getMessage();
              messageKey = "errors.gestoreException.*.ws.ATC.InternetSoluzioni.updateBando.esitoNOK";
              errMsgEvento = this.resBundleGenerale.getString(messageKey);
              errMsgEvento= errMsgEvento.replace("{0}", msg);
              throw new GestoreException("L'aggiornamento del bando da parte del servizio remoto non e' andato a buon fine", "ws.ATC.InternetSoluzioni.updateBando.esitoNOK", new Object[]{msg}, new Exception());
            }
          }
        }
        risultatoScp = gestioneAggiornamentoScp(codgar,"2,3",genere,termineVisualizzatoIntero,datiForm);
        if(risultatoScp != null){
          int esito = risultatoScp.getStatus();
          switch (esito) {
          case 200:
            break;
          case 400:
          default:
            PubblicazioneAttoResult risultatoPubblicazione = risultatoScp.readEntity(PubblicazioneAttoResult.class);
            errMsgEvento = "Errore nell'invio dell'aggiornamento ad SCP:";
            ArrayList<ValidateEntry> validate = (ArrayList) risultatoPubblicazione.getValidate();
            for(int n = 0;n<validate.size();n++){
              if("E".equals(validate.get(n).getTipo())){
                  errMsgEvento += validate.get(n).getNome() + ": " + validate.get(n).getMessaggio() + "; ";
              }
            }
            throw new GestoreException("L'aggiornamento del atto SCP da parte del servizio remoto non e' andato a buon fine", "invioAttiScp.errorResponse", new Object[]{errMsgEvento}, new Exception());
          }
        }
        updateTorn = "update torn set DTEPAR=?, OTEPAR=?, DTERMRICHCDP=?, DTERMRISPCDP=? where codgar=?";
        parametri=new Object[]{data,ora,data1,data2,codgar};
        break;
      case 2:
        // Termini presentazione domanda di offerta
        descrTermineVisualizzato = "Rettifica termini di presentazione dell' offerta";
        data = datiForm.getData("DTEOFF");
        data_orig = datiForm.getData("DTEOFF_ORIG");
        ora = datiForm.getString("OTEOFF");
        ora_orig = datiForm.getString("OTEOFF_ORIG");
        data1 = datiForm.getData("DTERMRICHCPO");
        data1_orig = datiForm.getData("DTERMRICHCPO_ORIG");
        data2 =datiForm.getData("DTERMRISPCPO");
        data2_orig =datiForm.getData("DTERMRISPCPO_ORIG");
        if(!("2".equals(iterga) || "4".equals(iterga))){
          if(gestioneRegioneMarche){
            ResultClass risposta=gestioneAggiornamentoBando(codgar,tipoGara,data,data_orig,ora,ora_orig);
            if(risposta!=null && !risposta.isResult()){
              String msg=risposta.getError();
              messageKey = "errors.gestoreException.*.ws.bandiRegioneMarche.updateBando.esitoNOK";
              errMsgEvento = this.resBundleGenerale.getString(messageKey);
              errMsgEvento= errMsgEvento.replace("{0}", msg);
              throw new GestoreException("L'aggiornamento del bando da parte del servizio remoto non e' andato a buon fine", "ws.bandiRegioneMarche.updateBando.esitoNOK", new Object[]{msg}, new Exception());
            }
          }
          if(gestioneATC){
            ResponseWS risposta=gestioneATCManager.updateTerminiBando(codgar,tipoGara,data,data_orig,ora,ora_orig);
            if(risposta!=null && risposta.getMessage()!=null){
              String msg=risposta.getMessage();
              messageKey = "errors.gestoreException.*.ws.ATC.InternetSoluzioni.updateBando.esitoNOK";
              errMsgEvento = this.resBundleGenerale.getString(messageKey);
              errMsgEvento= errMsgEvento.replace("{0}", msg);
              throw new GestoreException("L'aggiornamento del bando da parte del servizio remoto non e' andato a buon fine", "ws.ATC.InternetSoluzioni.updateBando.esitoNOK", new Object[]{msg}, new Exception());
            }
          }
        }
        if(!"1".equals(iterga)){
          tipologiaScp = "6";
        }else{
          tipologiaScp = "2,3";
        }
        risultatoScp = gestioneAggiornamentoScp(codgar,tipologiaScp,genere,termineVisualizzatoIntero,datiForm);
        if(risultatoScp != null){
          int esito = risultatoScp.getStatus();
          switch (esito) {
          case 200:
            break;
          case 400:
          default:
            PubblicazioneAttoResult risultatoPubblicazione = risultatoScp.readEntity(PubblicazioneAttoResult.class);
            errMsgEvento = "Errore nell'invio dell'aggiornamento ad SCP:";
            ArrayList<ValidateEntry> validate = (ArrayList) risultatoPubblicazione.getValidate();
            for(int n = 0;n<validate.size();n++){
              if("E".equals(validate.get(n).getTipo())){
                  errMsgEvento += validate.get(n).getNome() + ": " + validate.get(n).getMessaggio() + "; ";
              }
            }
            throw new GestoreException("L'aggiornamento del atto SCP da parte del servizio remoto non e' andato a buon fine", "invioAttiScp.errorResponse", new Object[]{errMsgEvento}, new Exception());
          }
        }
        updateTorn = "update torn set DTEOFF=?, OTEOFF=?, DTERMRICHCPO=?, DTERMRISPCPO=? where codgar=?";
        parametri=new Object[]{data,ora,data1,data2,codgar};
        break;
      case 3:
        //Temini apertura plichi
        descrTermineVisualizzato = "Rettifica termini di apertura dei plichi";
        data = datiForm.getData("DESOFF");
        data_orig = datiForm.getData("DESOFF_ORIG");
        ora = datiForm.getString("OESOFF");
        ora_orig = datiForm.getString("OESOFF_ORIG");
        updateTorn = "update torn set DESOFF=?, OESOFF=? where codgar=?";
        parametri=new Object[]{data,ora,codgar};
        break;
      }
      this.sqlManager.update(updateTorn, parametri);

      String insert="insert into garrettif(ID,CODGAR,TIPTER,DATTER,ORATER,DRICHC,DRISPC,DRETTIF) values(?,?,?,?,?,?,?,?)";
      GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
          this.getServletContext(), GenChiaviManager.class);
      Long id = new Long(genChiaviManager.getNextId("GARRETTIF"));
      Date oggi = UtilityDate.getDataOdiernaAsDate();
      this.sqlManager.update(insert, new Object[]{id, codgar, termineVisualizzatoIntero, data_orig, ora_orig, data1_orig, data2_orig, new Timestamp(oggi.getTime())});

      //Aggiornamento del campo TORN.DULTAGG
      PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
          this.getServletContext(), PgManagerEst1.class);
      String pubblicazioneBandoPortale = pgManagerEst1.esisteBloccoPubblicazionePortale(codgar, "BANDO", false);
      String pubblicazioneEsitoPortale = pgManagerEst1.esisteBloccoPubblicazionePortale(codgar, "ESITO", true);
      if("TRUE".equals(pubblicazioneBandoPortale) || "TRUE".equals(pubblicazioneEsitoPortale)){
        this.sqlManager.update("update TORN set DULTAGG = ? where CODGAR=?",new Object[]{oggi,codgar});
      }
      /*
      Long occorrenzePubbli = (Long)this.sqlManager.getObject("select count(codgar9) from pubbli where codgar9=? and tippub in (11,13)", new Object[]{codgar});
      Long occorrenzePug = (Long)this.sqlManager.getObject("select count(ngara) from pubg where ngara in (select ngara from gare where codgar1=?) and tippubg=12", new Object[]{codgar});
      if((occorrenzePubbli!=null && occorrenzePubbli.longValue()>0) || (occorrenzePug!=null && occorrenzePug.longValue()>0)){
        this.sqlManager.update("update TORN set DULTAGG = ? where CODGAR=?",new Object[]{oggi,codgar});
      }
      */

      livEvento = 1 ;
      errMsgEvento = "";
    } catch (SQLException e) {
      throw new GestoreException("Errore nella storicizzazione della rettifica dei termini di gara", null, e);
    }finally{
      //Tracciatura eventi
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(codGara);
        logEvento.setCodEvento("GA_RETTIFICA_TERMINI");
        logEvento.setDescr(descrTermineVisualizzato);
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
      }
    }
    // setta l'operazione a completata, in modo da scatenare il reload della
    // pagina principale
    this.getRequest().setAttribute("rettificaEseguita", "1");
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  private ResultClass gestioneAggiornamentoBando(String codgar, String tipoGara, Timestamp data, Timestamp dataOrig, String ora, String oraOrig) throws GestoreException{
    ResultClass risposta = null;
    if(!data.equals(dataOrig) || !ora.equals(oraOrig)){
      GestioneRegioneMarcheManager gestioneRegioneMarcheManager=(GestioneRegioneMarcheManager) UtilitySpring.getBean("gestioneRegioneMarcheManager",
          this.getServletContext(), GestioneRegioneMarcheManager.class);
      String chiaveGara=codgar;
      if(!"1".equals(tipoGara) && !"3".equals(tipoGara)) //in questi casi la chiave ha una forma del tipo: $...
        chiaveGara=chiaveGara.substring(1);
      ResultClass result = gestioneRegioneMarcheManager.getBando(chiaveGara);
      if(result.isResult() && result.getBando().getBandoID()!=null){
        TimeZone tzUTC = TimeZone.getTimeZone("UTC");
        Calendar cal = Calendar.getInstance(tzUTC);
        Date dataScadenza = new Date(data.getTime());
        try {
          dataScadenza= gestioneRegioneMarcheManager.setTimeZone(dataScadenza, tzUTC);
        } catch (ParseException e) {
          throw new GestoreException("Si e' verificato un errore nell'impostazione del fuso orario UTC" , null,  e);
        }
        String oraScadenza = ora.substring(0, 2);
        String minutiScadenza = ora.substring(3);
        cal.setTime(dataScadenza);
        cal.add(Calendar.HOUR, new Integer(oraScadenza).intValue());
        cal.add(Calendar.MINUTE, new Integer(minutiScadenza).intValue());
        BandoClass bando = result.getBando();
        bando.setDataScadenza(cal);
        risposta=gestioneRegioneMarcheManager.updateBando(bando);
      }
    }
    return risposta;
  }


  private Response gestioneAggiornamentoScp(String codgar, String tipologie, String genere, int termineVisualizzatoIntero, DataColumnContainer datiForm) throws GestoreException, SQLException{

    String select = "select tipologia from GARATTISCP where CODGAR = '" + codgar + "' and TIPOLOGIA in (" + tipologie + ")";
    Long tipologia = (Long) sqlManager.getObject(select, new Object[]{});
    String entita = "pubblicazioni";
    String url = ConfigManager.getValore(ScpManager.PROP_WS_PUBBLICAZIONI_URL);
    Response risultato = null;
    Response accesso;
    if(tipologia != null){
    try {
      accesso = scpManager.getLogin(entita);
      LoginResult resultAccesso = accesso.readEntity(LoginResult.class);
      if (resultAccesso.isEsito()) {
          String token = resultAccesso.getToken();
          PubblicaAttoEntry pubblicazione = new PubblicaAttoEntry();
          scpManager.valorizzaAtto(pubblicazione, codgar, "", tipologia, genere);

          String date = null;
          if(termineVisualizzatoIntero == 1){
            Timestamp time = datiForm.getData("DTEPAR");
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            date = formatter.format(new Date(time.getTime()));
          }else{
            Timestamp time = datiForm.getData("DTEOFF");
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            date = formatter.format(new Date(time.getTime()));
          }
          if(scpManager.verificaEsistenzaValore(date)){
            pubblicazione.setDataScadenza(date);

            Client client = ClientBuilder.newClient();
            WebTarget webTarget = client.target(url).path("Atti/Pubblica").queryParam("token", token).queryParam("modalitaInvio", "2");
            risultato = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(pubblicazione), Response.class);
          }
      }
      return risultato;

    } catch (CriptazioneException e) {
      throw new GestoreException("Si e' verificato un errore nella decifratura delle credenziali SCP" , null,  e);
    } catch (ParseException e) {
      throw new GestoreException("Si e' verificato un errore nella connessione al servizio SCP" , null,  e);
    } catch (GestoreException e) {
      throw new GestoreException("Verificare i parametri per la connessione al servizio di pubblicazione" , null,  e);
    }

  }
    return null;

  }

}
