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
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
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
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.SetProfiloAction;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestionePATManager;
import it.eldasoft.sil.pg.bl.GestioneRegioneMarcheManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.bl.ScpManager;
import it.eldasoft.sil.pg.bl.scp.LoginResult;
import it.eldasoft.sil.pg.bl.scp.PubblicaAttoEntry;
import it.eldasoft.sil.pg.bl.scp.PubblicazioneAttoResult;
import it.eldasoft.sil.pg.bl.scp.ValidateEntry;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.sil.pg.tags.funzioni.EsisteSchedaANACPubblicataFunction;
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
  
  private PgManager pgManager;
  
  private PgManagerEst1 pgManagerEst1;
  
  private GenChiaviManager genChiaviManager;


  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);
    pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        this.getServletContext(), PgManagerEst1.class);
    genChiaviManager=(GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);
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

    HashMap<String,Object> hMap = new HashMap<String,Object>();
    // lettura dei parametri di input
    String forzaRettifica = StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
        "forza"));
    String modalitaAmministratore = StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
        "modalitaAmministratore"));
    String esisteSchedaRettificaAperta = StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
        "esisteSchedaRettificaAperta"));
    
    boolean skip=false;
    if("true".equals(modalitaAmministratore) && !"true".equals(forzaRettifica)) {//redirect alla modalità amministatore (viene inietta appena prima del submit) senza fare calcoli
      skip=true;
      return;
    }
    
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
   
    //variabili per tracciatura eventi
    String messageKey = null;
    int livEvento = 3;
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
    String descrTermineVisualizzato = "";
    String codGara = "";
    //Gestione integrazione Regione Marche
    String tipoGara=StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
        "tipoGara"));
    String bandoPubblicato=StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
        "pubblicazionePortaleBando"));
    String invitoPubblicato=StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
            "pubblicazionePortaleInvito"));

    String iterga = StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
        "iterga"));

    boolean modificaAperturaPlichi=false;
    boolean creataSchedaRettifica=false;
    Object[] gestioni = new Object[] {termineVisualizzatoIntero};
    if(termineVisualizzatoIntero==2) {
      List<Object> listaGestione = new ArrayList<Object>();
      if(isChanged(datiForm.getData("DTEOFF"),datiForm.getData("DTEOFF_ORIG")) || isChanged(datiForm.getString("OTEOFF"),datiForm.getString("OTEOFF_ORIG"))
         || isChanged(datiForm.getData("DTERMRICHCPO"),datiForm.getData("DTERMRICHCPO_ORIG")) || isChanged(datiForm.getData("DTERMRISPCPO"),datiForm.getData("DTERMRISPCPO_ORIG"))) 
        listaGestione.add(2);//gestisco 2 solo se è presente ed è variato almeno tra i campi tra DTEOFF,OTEOFF,DTERMRICHCPO,DTERMRISPCPO
      if(isChanged(datiForm.getData("DESOFF"),datiForm.getData("DESOFF_ORIG")) || isChanged(datiForm.getString("OESOFF"),datiForm.getString("OESOFF_ORIG"))) {
        listaGestione.add(3);//gestisco 3 solo se è presente ed è variato DESOFF o OESOFF
        modificaAperturaPlichi=true;
      }
      gestioni=listaGestione.toArray(); //in seguito alla APP-1539, vengono gestite le rettifiche di termini per la presentazione offerte e per l'apertura plichi nella stessa maschera!
    }
    for(int i=0;i<gestioni.length;i++) {
      termineVisualizzatoIntero = (int) gestioni[i];
      String codEvento="GA_RETTIFICA_TERMINI";
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
          hMap.put("DTEPAR",data);
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
          hMap.put("DTEOFF",data);
          break;
        case 3:
          //Temini apertura plichi
          descrTermineVisualizzato = "Rettifica termini di apertura dei plichi";
          data = datiForm.getData("DESOFF");
          data_orig = datiForm.getData("DESOFF_ORIG");
          ora = datiForm.getString("OESOFF");
          ora_orig = datiForm.getString("OESOFF_ORIG");
          break;
        }
        
        boolean controlliScritturaG1NPARET = (isChanged(data,data_orig) || isChanged(ora,ora_orig));//se è variato almeno un dato utile alla rettifica tramite G1NPARET

        boolean scritturaInG1NPARET = false;
        Vector dati = null;
        
        if((controlliScritturaG1NPARET || modificaAperturaPlichi) && !"true".equals(forzaRettifica)) {//se è variato un dato utile alla rettifica tramite G1NPARET, dobbiamo controllare se siamo nel caso in cui scrivere in G1NPARET e se non stiamo forzando la rettifica
          EsisteSchedaANACPubblicataFunction function = new EsisteSchedaANACPubblicataFunction();
          boolean esisteBandoANACPubblicato = Boolean.parseBoolean(function.esisteSchedaANACPubblicata(sqlManager, codgar));
          
          
          //-tra le schede pubblicate c’è una scheda relativa al bando STATONPA.G1NPASCHEDA = 60 G1CF_NPA.RETTERM  valorizzato
          //-non si tratta di rettifica dei termini di presentazione offerta (2) o dei termini di apertura plichi (3) di una procedura ristretta (ITERGA.TORN = 2,4)
          if(esisteBandoANACPubblicato && !(("2".equals(iterga) || "4".equals(iterga)) && (termineVisualizzatoIntero==2 || termineVisualizzatoIntero==3))) {
            //per recuperare l'ultimo idAvviso valido staccato da ANAC
            dati = this.sqlManager.getVector("select g1cf_npa.id, g1npascheda.id, g1npascheda.uuid_eform, g1cf_npa.retterm, g1npascheda.statonpa, g1npascheda.idprg_espd, g1npascheda.iddoc_espd, g1npascheda.idschedanpa"
                + " from g1npascheda, g1npaform, g1cf_npa where g1npascheda.idform=g1npaform.id and g1cf_npa.id=g1npaform.idcfnpa and "
                + " (g1npascheda.statonpa=60 or (g1npascheda.statonpa=1 and g1npascheda.idschedanparet is not null)) and g1npaform.codgar=? and g1cf_npa.retterm is not null order by g1npascheda.id desc", new Object[] {codgar});
            
            if(dati!=null && dati.size()>0){
             String retterm = SqlManager.getValueFromVectorParam(dati, 3).stringValue();
              //    -Se retterm=2 o se è retterm=1 e si tratta di rettifica delle domande di partecipazione o delle domande di offerta
             scritturaInG1NPARET = "2".equals(retterm) || (termineVisualizzatoIntero!=3 && "1".equals(retterm)); 
             if(((scritturaInG1NPARET || ("2".equals(retterm) && modificaAperturaPlichi))) && !"true".equals(esisteSchedaRettificaAperta) && !creataSchedaRettifica){//se non abbiamo già verificato la presenza della scheda di rettifica. Se ho creato con i=0, non devo passare qui
               Long countSchedeRettificaInCorso = (Long) sqlManager.getObject("select count(1) from g1npascheda, g1npaform where g1npascheda.idform=g1npaform.id and g1npascheda.idschedanparet is not null "
                   + "and g1npascheda.statonpa not in (34,50,60) and g1npaform.codgar=?", new Object[] {codgar});
               if(countSchedeRettificaInCorso>0) {
                 this.getRequest().setAttribute("esisteSchedaRettificaAperta", "true");
                 skip=true;
                 return;
               }
             }
            }
          }
        }
      
        if(scritturaInG1NPARET) {
          creataSchedaRettifica=true;
          codEvento="GA_RETTIFICA_TERMINI_ANAC";
          descrTermineVisualizzato+=" - operazione preliminare a rettifica dati ANAC";
          //creo la scheda di rettifica a partire dall'ultima scheda pubblicata o bloccata in pubblicazione 
          Long idcfnpa = SqlManager.getValueFromVectorParam(dati, 0).longValue();
          Long idschedanpa = SqlManager.getValueFromVectorParam(dati, 1).longValue();
          Long statonpa = SqlManager.getValueFromVectorParam(dati, 4).longValue();
          String idprg_espd = SqlManager.getValueFromVectorParam(dati, 5).stringValue();
          Long iddoc_espd = SqlManager.getValueFromVectorParam(dati, 6).longValue();
          String identificativoNPA = SqlManager.getValueFromVectorParam(dati, 7).stringValue();
          
          DataColumnContainer g1NPARET = new DataColumnContainer(new DataColumn[] {
              new DataColumn("G1NPARET.TIPORET", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, 1))});
          
          if(termineVisualizzatoIntero==1) {
            g1NPARET.addColumn("G1NPARET.DTEPAR", new JdbcParametro(JdbcParametro.TIPO_DATA, data));
            g1NPARET.addColumn("G1NPARET.OTEPAR", new JdbcParametro(JdbcParametro.TIPO_TESTO, ora));
            g1NPARET.addColumn("G1NPARET.DTERMRICHCDP", new JdbcParametro(JdbcParametro.TIPO_DATA, data1));
            g1NPARET.addColumn("G1NPARET.DTERMRISPCDP", new JdbcParametro(JdbcParametro.TIPO_DATA, data2));
          } else if(termineVisualizzatoIntero==2) {
            g1NPARET.addColumn("G1NPARET.DTEOFF", new JdbcParametro(JdbcParametro.TIPO_DATA, data));
            g1NPARET.addColumn("G1NPARET.OTEOFF", new JdbcParametro(JdbcParametro.TIPO_TESTO, ora));
            g1NPARET.addColumn("G1NPARET.DTERMRICHCPO", new JdbcParametro(JdbcParametro.TIPO_DATA, data1));
            g1NPARET.addColumn("G1NPARET.DTERMRISPCPO", new JdbcParametro(JdbcParametro.TIPO_DATA, data2));
          } else if(termineVisualizzatoIntero==3) {
            g1NPARET.addColumn("G1NPARET.DESOFF", new JdbcParametro(JdbcParametro.TIPO_DATA, data));
            g1NPARET.addColumn("G1NPARET.OESOFF", new JdbcParametro(JdbcParametro.TIPO_TESTO, ora));
          }
          
          if(new Long(1).equals(statonpa)) {//se stato 1, aggiorno i dati in g1nparet 
            Long idSchedaRett = (Long) this.sqlManager.getObject("select id from g1nparet where idnpascheda=? order by id desc", new Object[] {idschedanpa});
            g1NPARET.addColumn("G1NPARET.ID", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idSchedaRett));
            
            if(i==0) { //la delete e la insert le faccio solo al primo giro
              this.sqlManager.update("delete from G1NPARET where id=?", new Object[] {idSchedaRett});

              g1NPARET.addColumn("G1NPARET.IDNPASCHEDA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idschedanpa));
              g1NPARET.insert("G1NPARET", this.sqlManager);
            }else {//qui entro solo se gestioni={2,3} e sono nel secondo giro
              g1NPARET.getColumn("G1NPARET.ID").setChiave(true);
              g1NPARET.setOriginalValue("G1NPARET.ID", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idSchedaRett));
              g1NPARET.update("G1NPARET", this.sqlManager);
            }
            
          }else {//altrimenti faccio gli inserimenti
            Long idForm = new Long(genChiaviManager.getNextId("G1NPAFORM"));
            DataColumnContainer g1NPAFORM = new DataColumnContainer(new DataColumn[] {
                new DataColumn("G1NPAFORM.ID", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idForm)),
                new DataColumn("G1NPAFORM.CODGAR", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codgar)),
                new DataColumn("G1NPAFORM.IDCFNPA", new JdbcParametro(JdbcParametro.TIPO_TESTO, idcfnpa))
                });
            g1NPAFORM.insert("G1NPAFORM", this.sqlManager);
            
            Long idScheda = new Long(genChiaviManager.getNextId("G1NPASCHEDA"));
            String versioneNpa = pgManager.getVersioneNpaCorrente(); 
            DataColumnContainer g1NPASCHEDA = new DataColumnContainer(new DataColumn[] {
                    new DataColumn("G1NPASCHEDA.ID", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idScheda)),
                    new DataColumn("G1NPASCHEDA.IDFORM", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idForm)),
                    new DataColumn("G1NPASCHEDA.STATONPA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, 1)),
                    new DataColumn("G1NPASCHEDA.VERNPA", new JdbcParametro(JdbcParametro.TIPO_TESTO, versioneNpa)),
                    new DataColumn("G1NPASCHEDA.IDSCHEDANPARET", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idschedanpa)),
                    new DataColumn("G1NPASCHEDA.IDPRG_ESPD", new JdbcParametro(JdbcParametro.TIPO_TESTO, idprg_espd)),
                    new DataColumn("G1NPASCHEDA.IDDOC_ESPD", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, iddoc_espd)),
                    new DataColumn("G1NPASCHEDA.IDSCHEDANPA", new JdbcParametro(JdbcParametro.TIPO_TESTO, identificativoNPA))
                    });
            g1NPASCHEDA.insert("G1NPASCHEDA", this.sqlManager);
            
            Long idG1NPARET = new Long(genChiaviManager.getNextId("G1NPARET"));
                g1NPARET.addColumn("G1NPARET.ID", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idG1NPARET));
                g1NPARET.addColumn("G1NPARET.IDNPASCHEDA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idScheda));
                
            g1NPARET.insert("G1NPARET", this.sqlManager);
            
          }
          
          this.getRequest().setAttribute("rettificaDaEseguireSuANAC", "1");
          
        }else {//gestione "vecchia"
          
          hMap.put("iterga",iterga);
          hMap.put("bandoPubblicato",bandoPubblicato);
          hMap.put("invitoPubblicato",invitoPubblicato);
          hMap.put("termineVisualizzatoIntero",termineVisualizzatoIntero);
          hMap.put("codgar",codgar);
          hMap.put("tipoGara",tipoGara);
          hMap.put("data",data); 
          hMap.put("data_orig",data_orig);
          hMap.put("ora",ora);
          hMap.put("ora_orig",ora_orig);
          hMap.put("data1",data1);
          hMap.put("data2",data2);
          hMap.put("genere",genere);
          
          pgManagerEst1.gestioneRettificheIntegrazioni(hMap);
          
          String updateTorn=(String)hMap.get("updateTorn");
          Object[] parametri=(Object[])hMap.get("parametri");
          messageKey=(String)hMap.get("messageKey");
          errMsgEvento=(String)hMap.get("errMsgEvento");
          if(hMap.get("exception")!=null)
            throw (GestoreException) hMap.get("exception");
        
          this.sqlManager.update(updateTorn, parametri);
    
          String insert="insert into garrettif(ID,CODGAR,TIPTER,DATTER,ORATER,DRICHC,DRISPC,DRETTIF) values(?,?,?,?,?,?,?,?)";
  
          Long id = new Long(genChiaviManager.getNextId("GARRETTIF"));
          Date oggi = UtilityDate.getDataOdiernaAsDate();
          this.sqlManager.update(insert, new Object[]{id, codgar, termineVisualizzatoIntero, data_orig, ora_orig, data1_orig, data2_orig, new Timestamp(oggi.getTime())});
    
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
        }
        livEvento = 1 ;
        errMsgEvento = "";
      } catch (SQLException e) {
        throw new GestoreException("Errore nella storicizzazione della rettifica dei termini di gara", null, e);
      }finally{
        //Tracciatura eventi
        if(!skip) {
          try {
            LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
            logEvento.setLivEvento(livEvento);
            logEvento.setOggEvento(codGara);
            logEvento.setCodEvento(codEvento);
            logEvento.setDescr(descrTermineVisualizzato);
            logEvento.setErrmsg(errMsgEvento);
            LogEventiUtils.insertLogEventi(logEvento);
          } catch (Exception le) {
            messageKey = "errors.logEventi.inaspettataException";
            logger.error(this.resBundleGenerale.getString(messageKey), le);
          }
        }
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
  
  private boolean isChanged(Object valNuovo, Object valOriginale) {
    return (valNuovo==null && valOriginale!=null) || (valNuovo!=null && !valNuovo.equals(valOriginale));//se il dato è cambiato
  }

}
