/*
 * Created on 12/10/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import intra.regionemarche.ResultClass;
import intra.regionemarche.StrutturaClass;
import intra.regionemarche.TemiRegionaliClass;
import intra.regionemarche.TipiProceduraClass;
import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ControlliOepvManager;
import it.eldasoft.sil.pg.bl.GestioneATCManager;
import it.eldasoft.sil.pg.bl.GestioneProgrammazioneManager;
import it.eldasoft.sil.pg.bl.GestioneRegioneMarcheManager;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.bl.InviaDatiRichiestaCigManager;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.bl.ValidatorManager;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityFiscali;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;

/**
 * Gestore che effettua il controllo della valorizzazione di alcuni
 * campi. Se anche uno non risulta valorizzato, allora si deve riportare
 * un messaggio opportuno alla finestra popipPubblicaSuPortale.jsp
 *
 * @author Marcello Caminiti
 */
public class GestorePubblicaSuPortale extends AbstractGestorePreload {

  SqlManager sqlManager = null;
  PgManager pgManager = null;
  ValidatorManager validatorManager= null;
  TabellatiManager tabellatiManager = null;
  MEPAManager mepaManager = null;
  FileAllegatoManager fileAllegatoManager = null;
  GestioneWSDMManager gestioneWSDMManager = null;
  GestioneWSERPManager gestioneWSERPManager = null;
  PgManagerEst1 pgManagerEst1 = null;
  GestioneRegioneMarcheManager gestioneRegioneMarcheManager=null;
  ControlliOepvManager controlliOepvManager = null;
  GestioneATCManager gestioneATCManager=null;
  InviaDatiRichiestaCigManager inviaDatiRichiestaCigManager=null;
  GeneManager geneManager = null;
  GestioneProgrammazioneManager gestioneProgrammazioneManager = null;

  private static final String pattern_Formato_Ora = "[0-9][0-9]:[0-9][0-9]";
  private static final String pattern_Ora = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

  public GestorePubblicaSuPortale(BodyTagSupportGene tag) {
    super(tag);
  }


  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }

  public void inizializzaManager(PageContext page){
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);

    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        page, PgManager.class);

    tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        page, TabellatiManager.class);

    mepaManager = (MEPAManager) UtilitySpring.getBean("mepaManager",
        page, MEPAManager.class);

    validatorManager = (ValidatorManager) UtilitySpring.getBean("validatorManager",
        page, ValidatorManager.class);

    fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean(
        "fileAllegatoManager", page.getServletContext(), FileAllegatoManager.class);

    gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        page, GestioneWSDMManager.class);

    gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
        page, GestioneWSERPManager.class);

    controlliOepvManager = (ControlliOepvManager) UtilitySpring.getBean("controlliOepvManager",
        page, ControlliOepvManager.class);


    pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1", page, PgManagerEst1.class);

    gestioneRegioneMarcheManager = (GestioneRegioneMarcheManager)UtilitySpring.getBean("gestioneRegioneMarcheManager",
        page, GestioneRegioneMarcheManager.class);

    gestioneATCManager = (GestioneATCManager)UtilitySpring.getBean("gestioneATCManager",
        page, GestioneATCManager.class);

    inviaDatiRichiestaCigManager = (InviaDatiRichiestaCigManager)UtilitySpring.getBean("inviaDatiRichiestaCigManager",
        page, InviaDatiRichiestaCigManager.class);

    geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
        page, GeneManager.class);
    
    gestioneProgrammazioneManager = (GestioneProgrammazioneManager) UtilitySpring.getBean("gestioneProgrammazioneManager",
        page, GestioneProgrammazioneManager.class);
  }

  class Soggetto {
    private String dittao;
    private String codComponente;
    private String nomimo;
    private String nomeComposto;
    private String codfisc;
    private String indimp;
    private String nciimp;
    private String locimp;
    private String codcit;
    private String piva;
    private Long nazimp;
    private String proimp;
    private String capimp;
    private String isgruppoiva;

    public void setDittao(String dittao) {
      this.dittao=dittao;
    }

    public void setCodComponente(String codComponente) {
      this.codComponente=codComponente;
    }

    public void setNomimo(String nomimo) {
      this.nomimo=nomimo;
    }
    public void setNomeComposto(String nomeComposto) {
      this.nomeComposto=nomeComposto;
    }
    public void setCodfisc(String codfisc) {
      this.codfisc=codfisc;
    }
    public void setIndimp(String indimp) {
      this.indimp=indimp;
    }
    public void setNciimp(String nciimp) {
      this.nciimp=nciimp;
    }
    public void setLocimp(String locimp) {
      this.locimp=locimp;
    }
    public void setCodcit(String codcit) {
      this.codcit=codcit;
    }
    public void setPiva(String piva) {
      this.piva=piva;
    }
    public void setNazimp(Long nazimp) {
      this.nazimp=nazimp;
    }
    public void setProimp(String proimp) {
      this.proimp=proimp;
    }
    public void setCapimp(String capimp) {
      this.capimp=capimp;
    }
    public void setIsgruppoiva(String isgruppoiva) {
      this.isgruppoiva=isgruppoiva;
    }

    public String getDittao() {
      return this.dittao;
    }

    public String getCodComponente() {
      return this.codComponente;
    }

    public String getNomimo() {
      return this.nomimo;
    }

    public String getNomeComposto() {
      return this.nomeComposto;
    }

    public String getCodfisc() {
      return this.codfisc;
    }

    public String getIndimp() {
      return this.indimp;
    }

    public String getNciimp() {
      return this.nciimp;
    }

    public String getLocimp() {
      return this.locimp;
    }

    public String getCodcit() {
      return this.codcit;
    }

    public String getPiva() {
      return this.piva;
    }

    public Long getNazimp() {
      return this.nazimp;
    }

    public String getProimp() {
      return this.proimp;
    }

    public String getCapimp() {
      return this.capimp;
    }

    public String getIsgruppoiva() {
      return this.isgruppoiva;
    }

  }
  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    this.inizializzaManager(page);

    // lettura dei parametri di input
    String codgar = page.getRequest().getParameter("codgar");
    String ngara = page.getRequest().getParameter("ngara");
    String bando = page.getRequest().getParameter("bando");
    String valtec = page.getRequest().getParameter("valtec");
    String idconfi = page.getRequest().getParameter("idconfi");
    String select=null;
    Long genere = null;
    String chiave=null;
    Long gentip = null;
    String messaggio = "";
    String controlloSuperato="SI";
    String MsgConferma = "";
    //Double imptor = null;
    Double importo = null;
    Long iterga = null;
    Timestamp dtepar=null;
    Timestamp dteoff=null;
    Timestamp dinvit=null;
    boolean dteparVisibile= false;
    boolean dteoffVisibile=false;
    boolean dinvitVisibile=false;
    Long numOccorrenze=null;
    String oteoff=null;
    String valoreWSDM = null;
    String valore = null;
    Timestamp desoff=null;
    String oesoff = null;
    String otepar = null;
    String gartel= null;
    Long offtel = null;
    boolean visualizzaDettaglioComunicazione=true;
    String ricastae=null;
    Double aeribmin = null;
    Double aeimpmin = null;
    Long aemodvis = null;
    boolean formatoOteoffValido=true;
    boolean formatoOesoffValido=true;
    boolean costofisso = false;
    String cenint=null;
    String codrup=null;
    String accqua=null;

    String integrazioneWSDM="0";
    String integrazioneWSERP="0";
    String tipoWSERP = "";
    String uuid=(String)page.getRequest().getAttribute("uuid");
    String profilo = (String) page.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);
    Long bustalotti=null;
    boolean sezionitec = false;
    String nobustamm = null;
    Long modlic = null;
    boolean moduloQFORMAttivo = geneManager.getProfili().checkProtec(profilo, "FUNZ", "VIS", "ALT.GENEWEB.QuestionariQForm");
    boolean formularioCompletoAbilitato = false;
    boolean noDocInvito = false;
    boolean isRicercaMercatoNegoziata = false;

    try {
      boolean isIntegrazioneWSDMAttivaValida = gestioneWSDMManager.isIntegrazioneWSDMAttivaValida(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO,idconfi);
      if(isIntegrazioneWSDMAttivaValida)
        integrazioneWSDM="1";
    } catch (GestoreException e) {
      throw new JspException("Errore nella verifica dell'integrazione WSDM Protocollo per la gara " + codgar ,e);
    } catch (SQLException e) {
      throw new JspException("Errore nella verifica dell'integrazione WSDM Protocollo per la gara " + codgar ,e);
    }

    String urlWSERP = ConfigManager.getValore("wserp.erp.url");
    if(urlWSERP != null && !"".equals(urlWSERP)){
      integrazioneWSERP ="1";
      WSERPConfigurazioneOutType configurazione;
      try {
        configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
        if (configurazione.isEsito()) {
          tipoWSERP = configurazione.getRemotewserp();
        }
      } catch (GestoreException e) {
        UtilityStruts.addMessage(page.getRequest(), "error",
            "wserpconfigurazione.erp.configurazioneleggi.remote.error",new Object[]{":\r\n Configurazione integrazione con sistema ERP non corretta"});
      }
    }

    if("".equals(ngara))
      ngara=null;

    if (codgar != null){
      try {
        //Se elenco operatori, la variabile resta nulla
        gentip = (Long) sqlManager.getObject("select genere from V_GARE_TORN where codgar = ?", new Object[]{codgar});
      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura del genere della gara ", e);
      }
    }

    //bando = 1 --> pubblicazione bando
    //bando = 3 --> pubblicazione area riservata (procedura negoziata)
    //bando = 0 --> pubblicazione esito
    //bando = 5 --> invio invito a gara in corso

    if ("1".equals(bando) || "3".equals(bando) || "0".equals(bando) || "5".equals(bando)){
      //Determino il tipo di gara
      if (ngara == null || "".equals(ngara)){
        try {
          genere = (Long) sqlManager.getObject(
              "select genere from GARE where ngara = ?", new Object[]{codgar});
        } catch (SQLException e) {
          throw new JspException("Errore durante la lettura del genere della gara ", e);
        }
      }else if (ngara != null){
        try {
          genere = (Long) sqlManager.getObject(
              "select genere from GARE where ngara = ?", new Object[]{ngara});
        } catch (SQLException e) {
          throw new JspException("Errore durante la lettura del genere della gara ", e);
        }
      }

      try {
        Vector datiTorn = sqlManager.getVector(
            "select gartel,offtel,iterga,ricastae from TORN where codgar = ?", new Object[]{codgar});
        if(datiTorn!=null && datiTorn.size()>0){
          gartel = SqlManager.getValueFromVectorParam(datiTorn,0).getStringValue();
          offtel = SqlManager.getValueFromVectorParam(datiTorn,1).longValue();
          iterga = SqlManager.getValueFromVectorParam(datiTorn,2).longValue();
          if(Long.valueOf(8).equals(iterga)) {
        	  isRicercaMercatoNegoziata=true;
          }

          ricastae = SqlManager.getValueFromVectorParam(datiTorn,3).getStringValue();
        }
      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura dei campi gartel,offtel,iterga della gara ", e);
      } catch (GestoreException e) {
        throw new JspException("Errore durante la lettura dei campi gartel,offtel,iterga della gara ", e);
      }
    }

    if ("1".equals(bando) || "3".equals(bando) || "0".equals(bando) ){
      boolean formularioCompletoAttivo = geneManager.getProfili().checkProtec(profilo, "FUNZ", "VIS", PgManagerEst1.QFORM_VOCEPROFILO_TUTTE_BUSTE);
      if(moduloQFORMAttivo && formularioCompletoAttivo && new Long(3).equals(offtel)) {
        formularioCompletoAbilitato = true;
      }

      boolean pubBandoATC=false;
      if (ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TIPO)!=null
          && "2".equals(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TIPO)) && (uuid==null || "".equals(uuid) ))
        pubBandoATC=true;

      boolean pubBandoRegioneMarche=false;
      ResultClass result = null;
      Integer idBando = null;
      if (ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TIPO)!=null
          && "1".equals(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TIPO)))
        pubBandoRegioneMarche=true;

      boolean esisteUrlPubblicazione = true;
      boolean esisteUserPubblicazione=true;
      boolean esisteTokenPubblicazione=true;
      if(pubBandoATC ||pubBandoRegioneMarche){
        if (ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_URL)==null
            || "".equals(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_URL))){
          controlloSuperato = "NO";
          esisteUrlPubblicazione =false;
         }
        if(pubBandoATC){
          if (ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_USER)==null
              || "".equals(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_USER))){
            controlloSuperato = "NO";
            esisteUserPubblicazione =false;
           }
          if (ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TOKEN)==null
              || "".equals(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TOKEN))){
            controlloSuperato = "NO";
            esisteTokenPubblicazione =false;
           }
        }
      }
      if(pubBandoRegioneMarche && esisteUrlPubblicazione){
        String chiaveGara=ngara;
        if(new Long(1).equals(gentip) || new Long(3).equals(gentip) || ngara==null || "".equals(ngara))
          chiaveGara=codgar;
        try{
          result = this.gestioneRegioneMarcheManager.getBando(chiaveGara);
        }catch(GestoreException e){
          throw new JspException("Errore nel controllo dell'esistenza del bando sul sito istituzionale della Regione Marche ", e);
        }
        if(result.isResult() && result.getBando()!=null ){
          idBando = result.getBando().getBandoID();
          page.setAttribute("bandoIdRegMarche", idBando, PageContext.REQUEST_SCOPE);
        }
      }

      try {

      //String messaggio di conferma
        String msgBando = "";
        messaggio = "<b>Non è possibile procedere con la pubblicazione";

        if ("3".equals(bando)){
          MsgConferma = "Confermi la pubblicazione della gara su area riservata del portale Appalti?";
          messaggio += " della gara su area riservata del portale Appalti.</b><br>";
        } else if ("0".equals(bando)){
          MsgConferma = "Confermi la pubblicazione dell'esito di gara sul portale Appalti?";
          messaggio += " dell'esito di gara sul portale Appalti.</b><br>";
        } else if ("1".equals(bando) && genere!=null && genere.longValue()==11) {
          MsgConferma = "Confermi la pubblicazione dell'avviso sul portale Appalti?";
          messaggio += " dell'avviso sul portale Appalti.</b><br>";
        } else if ("1".equals(bando) && genere!=null && genere.longValue()==10) {
          MsgConferma = "Confermi la pubblicazione dell'elenco operatori economici sul portale Appalti?";
          messaggio += " dell'elenco operatori economici sul portale Appalti.</b><br>";
        } else if ("1".equals(bando) && genere!=null && genere.longValue()==20) {
          MsgConferma = "Confermi la pubblicazione del catalogo elettronico sul portale Appalti?";
          messaggio += " del catalogo elettronico sul portale Appalti.</b><br>";
        }else {
          MsgConferma = "Confermi la pubblicazione della gara sul portale Appalti?";
          messaggio += " della gara sul portale Appalti.</b><br>";
        }


        String desctabA1153 = tabellatiManager.getDescrTabellato("A1153", "1");
        if(desctabA1153!=null && !"".equals(desctabA1153) && !new Long(10).equals(genere) && !new Long(20).equals(genere) && !new Long(11).equals(genere))
          desctabA1153 = desctabA1153.substring(0,1);
        if(("1".equals(gartel) || "1".equals(desctabA1153)) && ("1".equals(bando) || "3".equals(bando))){
          msgBando = " (dati generali, documentazione";
          if("1".equals(gartel) && ("3".equals(bando) || ("1".equals(bando) && new Long(1).equals(iterga))))
            msgBando += ", criteri di valutazione e lavorazioni";
          msgBando += ")";
          MsgConferma += "<br>Se si procede con l'operazione, i dati attinenti alla definizione della gara" + msgBando + " diverranno disponibili in sola visualizzazione.";
          if("1".equals(gartel) && "3".equals(bando))
                                MsgConferma += "<br>Viene inoltre inviata comunicazione alle ditte invitate, secondo i dettagli riportati di seguito.";
        }else{
          MsgConferma += "<br>Se si procede con l'operazione, i documenti che verranno pubblicati diverranno disponibili in sola visualizzazione.";
        }
        if ("1".equals(gartel) && ("1".equals(bando) || "3".equals(bando))){
          //Lettura del tabellato A1122 per stabilire se è attiva la cifratura delle buste
          String desc = tabellatiManager.getDescrTabellato("A1122", "1");
          if(desc!=null && !"".equals(desc)){
            desc = desc.substring(0,1);
            page.setAttribute("cifraturaBuste", desc, PageContext.REQUEST_SCOPE);
          }
          nobustamm = (String) this.sqlManager.getObject("select nobustamm from torn where codgar=?",new Object[] {codgar});
          page.setAttribute("nobustamm", nobustamm, PageContext.REQUEST_SCOPE);
        }

        if("1".equals(integrazioneWSDM)){
          if((pubBandoRegioneMarche  || pubBandoATC ) && (("0".equals(bando) && idBando==null) || "1".equals(bando))){
            MsgConferma += "<br>Con la pubblicazione su portale Appalti si procede anche";
            MsgConferma += "<span id='msgFascicolo' style='display: none;'> all'apertura del fascicolo documentale e</span>";
            MsgConferma += " alla pubblicazione sul sito istituzionale.";
          }else{
            MsgConferma += "<span id='msgFascicolo' style='display: none;'><br>Con la pubblicazione su portale Appalti si procede anche all'apertura del fascicolo documentale.</span>";
          }
         }else{
            if((pubBandoRegioneMarche  || pubBandoATC )&& (("0".equals(bando) && idBando ==null) || "1".equals(bando)))
              MsgConferma += "<br>Con la pubblicazione su portale Appalti si procede anche alla pubblicazione sul sito istituzionale.";

         }

        page.setAttribute("MsgConferma", MsgConferma, PageContext.REQUEST_SCOPE);

        if(ngara==null)
          bustalotti=(Long)this.sqlManager.getObject("select bustalotti from gare where ngara=?", new Object[]{codgar});

        if("3".equals(bando) && (new Long(2).equals(iterga) || new Long(4).equals(iterga) || new Long(7).equals(iterga))){
          String chiaveTmp = ngara;
          if (ngara==null)
            chiaveTmp = codgar;
          String esitoControllo [] = mepaManager.controlloDataConDataAttuale(chiaveTmp, "DTEPAR", "OTEPAR");
          if(esitoControllo[0]=="false"){
            messaggio += "<br>Non sono superati i termini di presentazione domanda di partecipazione ";
            page.setAttribute("controlloSuperato", "NO", PageContext.REQUEST_SCOPE);
            page.setAttribute("msg", messaggio, PageContext.REQUEST_SCOPE);
            return;
          }
          if("1".equals(gartel)){
            String selectAmmgar = "select count(codgar5) from ditg where codgar5=? and ammgar is null and acquisizione <> 8 ";
            if(ngara==null){
              //Si è nel caso di offerta unica, nel caso di bustalotti=1 si deve considerare la gara complementare
              if(bustalotti!=null)
                selectAmmgar = "select count(codgar5) from ditg where codgar5=? and ammgar is null and codgar5=ngara5 and acquisizione <> 8";
            }
            Long conteggioAmmgar = (Long)this.sqlManager.getObject(selectAmmgar, new Object[]{codgar});
            if(conteggioAmmgar!= null && conteggioAmmgar.longValue()>0){
              controlloSuperato = "NO";
              messaggio += "<br>Ci sono delle ditte in gara per cui non è stato ancora valorizzato il campo 'Ammissione' ";
            }
            //Si deve controllare che non vi siano ditte ammesse con riserva
            String selectAmmriserva = "select count(ngara) from v_ditgammis where codgar=? and fasgar=? and ammgar=?";
            Long conteggioAmmissioneRiserva = (Long)this.sqlManager.getObject(selectAmmriserva, new Object[]{codgar,new Long(-4),new Long(3)});
            if(conteggioAmmissioneRiserva!= null && conteggioAmmissioneRiserva.longValue()>0){
              String msg="<br>Ci sono delle ditte ammesse con riserva alla gara";
              if(bustalotti!=null)
                msg = "<br>Ci sono delle ditte ammesse con riserva alla gara o ai singoli lotti ";
              controlloSuperato = "NO";
              messaggio += msg;
            }
            if(new Long(2).equals(bustalotti)){
              String selectpartgar = "select count(codgar5) from ditg where codgar5 = ? and partgar is null and fasgar is null and ngara5 != codgar5";
              Long conteggioPartgar = (Long)this.sqlManager.getObject(selectpartgar, new Object[]{codgar});
              if(conteggioPartgar!= null && conteggioPartgar.longValue()>0){
                controlloSuperato = "NO";
                messaggio += "<br>Ci sono dei lotti per cui non è stato ancora valorizzato il campo 'Partecipa' ";
              }
            }
            EsistonoAcquisizioniOfferteDaElaborareFunction function = new EsistonoAcquisizioniOfferteDaElaborareFunction();

            String esito = function.function(page, new Object[]{page,chiaveTmp,"FS10A"});
            if("true".equals(esito)){
              controlloSuperato = "NO";
              messaggio += "<br>Ci sono acquisizioni della busta prequalifica relative alla gara ancora da elaborare";
            }

            //Controllo sui documenti di prequalifica
            Long conteggioDocPrequalifica=(Long)this.sqlManager.getObject("select count(codgar) from documgara where codgar=? and gruppo=3 and busta=4 and statodoc is null", new Object[]{codgar});
            if(conteggioDocPrequalifica!=0 && conteggioDocPrequalifica.longValue()>0){
              controlloSuperato = "NO";
              messaggio += "<br>Non è possibile integrare documenti relativi alla busta di prequalifica perchè la fase di prequalifica è conclusa";
            }
            //Controllo sui documenti di prequalifica nel caso di qform
            if(formularioCompletoAbilitato) {
              String chiaveQform = ngara;
              Long conteggioQuestionari=null;

              conteggioQuestionari = (Long)this.sqlManager.getObject("select count(*) from qform where entita='GARE' and key1=? and busta=? and stato = 7", new Object[] {chiaveQform, new Long(4)});
              if(conteggioQuestionari!=0 && conteggioQuestionari.longValue()>0){
                controlloSuperato = "NO";
                messaggio += "<br>Non è possibile rettificare q-form relativi alla busta di prequalifica perchè la fase di prequalifica è conclusa";
              }
            }


            if("NO".equals(controlloSuperato)){
              page.setAttribute("controlloSuperato", "NO", PageContext.REQUEST_SCOPE);
              page.setAttribute("msg", messaggio, PageContext.REQUEST_SCOPE);
              return;
            }
          }
        }

        //Nel caso di offerte distinte ed offerta unica si deve controllare che vi sia almeno un lotto
        if (ngara == null || (gentip != null && (gentip.longValue() == 1 || gentip.longValue() == 3))){
          select="select count(ngara) from gare where codgar1=?";
            if(gentip.longValue() == 3)
              select+=" and ngara!=codgar1";
          Long conteggioLotti = (Long)this.sqlManager.getObject(select, new Object[]{codgar})  ;
          if(conteggioLotti==null || conteggioLotti.longValue()==0){
            controlloSuperato = "NO";
            messaggio += "<br>Non sono stati definiti i lotti della gara";
          }
        }
        //Controllo campo oggetto
        String messaggioDet = " della gara.";
        if(genere!=null && genere.longValue()==11){
          select="select OGGETTO from GAREAVVISI where NGARA = ?";
          chiave=ngara;
          messaggioDet = " dell'avviso.";
        }else if (ngara == null || (gentip != null && gentip.longValue() == 1) || (genere!=null && genere.longValue()==3)){ //Gara divisa a lotti con offerte distinte o offerta unica o lotto di gara con offerte distinte (caso di negoziata senza bando)
        //if (ngara == null && (gentip != null && (gentip.longValue() == 1 || gentip.longValue() == 3))){ //Gara divisa a lotti con offerte distinte o offerta unica o lotto di gara con offerte distinte (caso di negoziata senza bando)
          select="select DESTOR from torn where codgar = ?";
          chiave=codgar;
        }else if( ngara!=null && genere!=null && (genere.longValue()==10 || genere.longValue()==20)){//gare ad elenco
          select="select OGGETTO from garealbo where codgar = '" + codgar + "' and ngara = ?";
          chiave=ngara;
          messaggioDet = " dell'elenco.";
          if(genere.longValue()==20)
            messaggioDet = " del catalogo.";
        }else{ //gara a lotto unico
          select="select NOT_GAR from gare where ngara = ?";
          chiave = ngara;
        }
        valore = (String) sqlManager.getObject(select, new Object[]{chiave});
        if (valore == null || "".equals(valore.trim())) {
          controlloSuperato = "NO";
          messaggio += "<br>Non è stato inserito l'oggetto" + messaggioDet;
        }

        //Nel caso di lotto di gara e di gara ad offerta unica si controlla anche la descrizione di tutti i lotti
        if (gentip != null && (gentip.longValue() == 1 || gentip.longValue() == 3)){
          select="select NOT_GAR,ngara from gare where codgar1 = ?";
          if(gentip != null && gentip.longValue() == 3)
            select += " and ngara <> codgar1";

          List listaOggetti = sqlManager.getListVector(select, new Object[]{codgar});
          if(listaOggetti!=null && listaOggetti.size()>0){
            for (int i = 0; i < listaOggetti.size(); i++) {
              String oggettoLotto = SqlManager.getValueFromVectorParam(listaOggetti.get(i),0).getStringValue();
              if(oggettoLotto==null || "".equals(oggettoLotto.trim())){
                controlloSuperato = "NO";
                String codiceLotto = SqlManager.getValueFromVectorParam(listaOggetti.get(i),1).getStringValue();
                messaggio += "<br>Non è stato inserito l'oggetto del lotto " + codiceLotto +".";
              }
            }
          }
        }


        if(!"0".equals(bando) && !new Long(10).equals(genere) && !new Long(20).equals(genere) && !new Long(11).equals(genere)){ // Si escludono dal controllo gli elenchi, i cataloghi e gli avvisi
          if (ngara == null || (gentip != null && gentip.longValue() == 1) || (genere!=null && genere.longValue()==3)){ //Gara divisa a lotti con offerte distinte o offerta unica
            select="select TIPGAR, CRITLIC, MODLIC from torn where codgar = ?";
          }else{
            select="select TIPGARG, CRITLICG, MODLICG from gare where ngara = ?";
          }
          Vector datiGara = sqlManager.getVector(select, new Object[]{chiave});

          if(datiGara!=null && datiGara.size()>0){
            //controllo sul tipo procedura della gara
            Long tipgar = SqlManager.getValueFromVectorParam(datiGara, 0).longValue();
            if (tipgar == null) {
              controlloSuperato = "NO";
              messaggio += "<br>Non è stato inserito il tipo procedura della gara.";
            }
            //controllo sul criterio di aggiudicazione della gara
            Long critlic = SqlManager.getValueFromVectorParam(datiGara, 1).longValue();
            modlic = SqlManager.getValueFromVectorParam(datiGara, 2).longValue();
            if ((critlic == null || modlic==null) && "1".equals(gartel) && !new Long(7).equals(iterga)) {
              controlloSuperato = "NO";
              messaggio += "<br>Non è stato inserito il criterio di aggiudicazione della gara.";
            }
          }

          //Nel caso di lotto di gara e di gara ad offerta unica si controllano anche i dati di tutti i lotti corrispondenti ai campi di sopra
          if (gentip != null && (gentip.longValue() == 1 || gentip.longValue() == 3)){
            select="select TIPGARG, CRITLICG, MODLICG,ngara from gare where codgar1 = ?";
            if(gentip != null && gentip.longValue() == 3)
              select += " and ngara <> codgar1";

            List listaOggetti = sqlManager.getListVector(select, new Object[]{codgar});
            if(listaOggetti!=null && listaOggetti.size()>0){
              String msgTipoProcedura="";
              String msgCriterioAgg = "";
              Long tipgarLotto = null;
              Long critlicLotto = null;
              Long modlicLotto = null;
              String codiceLotto = null;
              for (int i = 0; i < listaOggetti.size(); i++) {
                tipgarLotto = SqlManager.getValueFromVectorParam(listaOggetti.get(i),0).longValue();
                if(tipgarLotto==null ){
                  controlloSuperato = "NO";
                  codiceLotto = SqlManager.getValueFromVectorParam(listaOggetti.get(i),3).getStringValue();
                  msgTipoProcedura += "<br>Non è stato inserito il tipo procedura del lotto " + codiceLotto +".";
                }
                critlicLotto = SqlManager.getValueFromVectorParam(listaOggetti.get(i),1).longValue();
                modlicLotto = SqlManager.getValueFromVectorParam(listaOggetti.get(i),2).longValue();
                if(((critlicLotto==null || modlicLotto ==null) && "1".equals(gartel) ) && !new Long(7).equals(iterga)){
                  controlloSuperato = "NO";
                  codiceLotto = SqlManager.getValueFromVectorParam(listaOggetti.get(i),3).getStringValue();
                  msgCriterioAgg += "<br>Non è stato inserito il tipo criterio di aggiudicazione del lotto " + codiceLotto +".";
                }
              }
              if(msgTipoProcedura.length()>0)
                messaggio += msgTipoProcedura;
              if(msgCriterioAgg.length()>0)
                messaggio += msgCriterioAgg;
            }
          }


        }

        select = "select cenint,codrup,imptor,dteoff,dtepar,dinvit,oteoff,desoff,oesoff,otepar,altrisog,modcont,aeribmin, aeimpmin, aemodvis, modrea, codnuts, accqua, aqdurata, aqtempo  from torn where codgar = ?";
        Vector datiTORN = sqlManager.getVector(select,
            new Object[]{codgar});

        if(datiTORN!=null){
          //Controllo Stazione appaltante
          cenint = ((JdbcParametro) datiTORN.get(0)).getStringValue();
          if (cenint == null || "".equals(cenint)) {
            controlloSuperato = "NO";
            messaggio += "<br>Non è stata inserita la stazione appaltante.";
          }
          //Controllo RUP
          codrup = ((JdbcParametro) datiTORN.get(1)).getStringValue();
          if (codrup == null || "".equals(codrup)) {
            controlloSuperato = "NO";
            if (genere!=null && genere.longValue()==10)
              messaggio += "<br>Non è stato inserito il responsabile dell'elenco.";
            else if (genere!=null && genere.longValue()==20)
              messaggio += "<br>Non è stato inserito il responsabile del catalogo.";
            else
              messaggio += "<br>Non è stato inserito il responsabile unico procedimento.";
          }

          //imptor = ((JdbcParametro) datiTORN.get(2)).doubleValue();
          //iterga = ((JdbcParametro) datiTORN.get(3)).longValue();
          dteoff = ((JdbcParametro) datiTORN.get(3)).dataValue();
          dtepar = ((JdbcParametro) datiTORN.get(4)).dataValue();
          dinvit = ((JdbcParametro) datiTORN.get(5)).dataValue();
          oteoff = ((JdbcParametro) datiTORN.get(6)).stringValue();
          desoff = ((JdbcParametro) datiTORN.get(7)).dataValue();
          oesoff = ((JdbcParametro) datiTORN.get(8)).stringValue();
          otepar = ((JdbcParametro) datiTORN.get(9)).stringValue();
          accqua = ((JdbcParametro) datiTORN.get(17)).stringValue();

          //Controllo per modalità stipula contratto
          if("1".equals(gartel) && genere!= null && genere.longValue()==3){
            Long altrisog = ((JdbcParametro) datiTORN.get(10)).longValue();
            Long modcont = ((JdbcParametro) datiTORN.get(11)).longValue();
            if(altrisog!=null && altrisog.longValue()==2 && (modcont!=null && modcont.longValue()==2)){
              Long numeroLotti = (Long)this.sqlManager.getObject("select count(ngara) from gare where codgar1=? and ngara!=codgar1", new Object[]{codgar});
              List listaLotti = this.sqlManager.getListVector("select cenint,count(*) from gare left outer join garaltsog on gare.ngara=garaltsog.ngara "
                  + "where gare.ngara<>gare.codgar1 and codgar1=? group by gare.codgar1,cenint", new Object[]{codgar});
              if(listaLotti!=null && listaLotti.size()>0){
                if(listaLotti.size()>1){
                  controlloSuperato = "NO";
                  messaggio += "<br>Non è stata impostata correttamente la modalità di stipula contratto: poichè i lotti della gara non sono tutti per lo stesso soggetto aderente, la modalità di stipula contratto deve essere per lotto e non per aggiudicatario.";
                }else{
                  //Se l'unica occorrenza trovata ha un cenint != null e il count ha un valore != dal numero di lotti, allora non si può procedere
                  String cenintLotto = SqlManager.getValueFromVectorParam(listaLotti.get(0), 0).getStringValue();
                  if(cenintLotto!= null && !"".equals(cenintLotto)){
                    Long conteggioCenint = SqlManager.getValueFromVectorParam(listaLotti.get(0), 1).longValue();
                    if(!numeroLotti.equals(conteggioCenint)){
                      controlloSuperato = "NO";
                      messaggio += "<br>Non è stata impostata correttamente la modalità di stipula contratto: poichè i lotti della gara non sono tutti per lo stesso soggetto aderente, la modalità di stipula contratto deve essere per lotto e non per aggiudicatario.";
                    }
                  }
                }

              }
            }
          }


          //Controlli per asta elettronica
          if("1".equals(ricastae)){
            String gara = ngara;
            if("".equals(gara) || gara == null)
              gara = codgar;
            Long ribcal=(Long)sqlManager.getObject("select ribcal from gare where ngara=?", new Object[]{gara});

            aeribmin = ((JdbcParametro)datiTORN.get(12)).doubleValue();
            aeimpmin = ((JdbcParametro)datiTORN.get(13)).doubleValue();
            aemodvis = ((JdbcParametro)datiTORN.get(14)).longValue();

            Long conteggioLottiRibcal1 = null;
            Long conteggioLottiRibcal2 = null;
            if(new Long(3).equals(genere)){
              conteggioLottiRibcal1 = (Long)sqlManager.getObject("select count(ngara) from gare where codgar1=? and ngara!=codgar1 and ribcal=1", new Object[]{gara});
              conteggioLottiRibcal2 = (Long)sqlManager.getObject("select count(ngara) from gare where codgar1=? and ngara!=codgar1 and ribcal=2", new Object[]{gara});
            }

            if(aeribmin==null && ((new Long(3).equals(genere) && conteggioLottiRibcal1!=null && conteggioLottiRibcal1.longValue()>0) || (!new Long(3).equals(genere) && new Long(1).equals(ribcal)))){
              controlloSuperato = "NO";
              messaggio+="<br>Non è stato inserito lo scarto minimo del ribasso di rilancio per l'asta elettronica";
            }
            if(aeimpmin==null && ((new Long(3).equals(genere) && conteggioLottiRibcal2!=null && conteggioLottiRibcal2.longValue()>0) || (!new Long(3).equals(genere) && new Long(2).equals(ribcal)))){
              controlloSuperato = "NO";
              messaggio+="<br>Non è stato inserito lo scarto minimo dell'importo di rilancio per l'asta elettronica";
            }
            if(aemodvis==null ){
              controlloSuperato = "NO";
              messaggio+="<br>Non è stato inserito il criterio di visualizzazione classificazione ditte su portale per l'asta elettronica";
            }

          }

        }

        boolean lottoUnico = false;
        if(!(genere!=null && genere.longValue()==11)){
            if(ngara==null  || (gentip != null && gentip.longValue() == 1) || (genere!=null && genere.longValue()==3)){//Gara divisa a lotti con offerte distinte o offerta unica
              //importo = imptor;
              importo = new Double(1);
            }else if( ngara!=null && genere!=null && (genere.longValue()==10 || genere.longValue()==20)){//gare ad elenco
              importo = new Double(1);
            }else{ //gara a lotto unico
              select="select IMPAPP from gare where ngara = ?";
              importo = (Double) sqlManager.getObject(select, new Object[]{ngara});
              lottoUnico=true;
            }
          //Se indagine di mercato, non fa il controllo sull'importo
          if (!(Long.valueOf(7).equals(iterga))){
            //controllo sull'importo
            if(importo==null){
              controlloSuperato = "NO";
              messaggio += "<br>Non è stato inserito l'importo a base di gara.";
            }else{
              if(lottoUnico == true && (new Long(1)).equals(offtel) && importo.longValue()<=0 && ("1".equals(bando) || "3".equals(bando)) ){
                controlloSuperato = "NO";
                messaggio += "<br>Non è stato inserito l'importo a base di gara (specificare un valore maggiore di 0).";
              }
            }

            //Nel caso di lotto di gara e di gara ad offerta unica si controlla anche l'importo di tutti i lotti
            if (ngara == null || (gentip != null && (gentip.longValue() == 1 || gentip.longValue() == 3))){
              select="select impapp,ngara from gare where codgar1 = ?";
              if(gentip != null && gentip.longValue() == 3)
                select += " and ngara <> codgar1";

              List listaOggetti = sqlManager.getListVector(select, new Object[]{codgar});
              if(listaOggetti!=null && listaOggetti.size()>0){
                for (int i = 0; i < listaOggetti.size(); i++) {
                  Double importoLotto = SqlManager.getValueFromVectorParam(listaOggetti.get(i),0).doubleValue();
                  if(importoLotto==null ){
                    controlloSuperato = "NO";
                    String codiceLotto = SqlManager.getValueFromVectorParam(listaOggetti.get(i),1).getStringValue();
                    messaggio += "<br>Non è stato inserito l'importo a base di gara del lotto " + codiceLotto +".";
                  }else{
                    if(((new Long(1)).equals(offtel) && importoLotto.longValue()<=0)) {
                      controlloSuperato = "NO";
                      String codiceLotto = SqlManager.getValueFromVectorParam(listaOggetti.get(i),1).getStringValue();
                      messaggio += "<br>Non è stato inserito l'importo a base di gara del lotto " + codiceLotto +" (specificare un valore maggiore di 0).";
                    }
                  }
                }
              }
            }
          }

          if(!"0".equals(bando)){
            if( ngara!=null && genere!=null && (genere.longValue()==10 || genere.longValue()==20)){ //gare ad elenco
              //Per le gare ad elenco la data termine partecipazione non è da controllare, quindi
              //valorizzo le variabili in modo che i controlli siano sempre superati
              dteoff = new Timestamp(Calendar.getInstance().getTime().getTime());
              dtepar = dteoff;
              oteoff=" ";
            }

            if(iterga != null) {
              if(iterga.longValue()==3 || iterga.longValue()==5 || iterga.longValue()==6 || isRicercaMercatoNegoziata)
                iterga= new Long(3);

              //condizioni per la visibilità di DTEOFF - procedura aperta o negoziata senza bando
              //nel caso di bando=3 si è esteso alle procedure ristrette, quindi va fatto il controllo
              //su dteoff
              if (iterga.longValue()==1 || iterga.longValue()==3 || ("3".equals(bando) && (iterga.longValue()==2 || iterga.longValue()==4)))
                dteoffVisibile = true;
              else
                //condizioni per la visibilità di DTEPAR - procedura ristretta
                dteparVisibile = true;
              //condizioni per la visibilità di DINVIT - procedura negoziata senza bando
              if (iterga.longValue()==3 || ("3".equals(bando) && (iterga.longValue()==2 || iterga.longValue()==4)))
                dinvitVisibile = true;
            }

            if(dteparVisibile){
              if(dtepar==null ){
                controlloSuperato = "NO";
                messaggio += "<br>Non è stata inserita la data termine per la presentazione della domanda di partecipazione.";
              }
              if(otepar==null || "".equals(otepar))  {
                  controlloSuperato = "NO";
                  messaggio += "<br>Non è stata inserita l'ora di termine per la presentazione della domanda di partecipazione.";
              }else{
                if ( !otepar.matches(pattern_Formato_Ora) || !otepar.matches(pattern_Ora)){
                  controlloSuperato = "NO";
                  messaggio += "<br>L'ora di termine per la presentazione della domanda di partecipazione non ha un formato valido ('hh:mm').";
                }
              }
            }
            if(dinvit==null && dinvitVisibile){
              controlloSuperato = "NO";
              messaggio += "<br>Non è stata inserita la data di invito alle ditte.";
            }
            if(dteoffVisibile){
              if(dteoff==null){
                controlloSuperato = "NO";
                messaggio += "<br>Non è stata inserita la data di termine per la presentazione dell'offerta.";
              }
              if((oteoff==null || "".equals(oteoff))) {
                controlloSuperato = "NO";
                messaggio += "<br>Non è stata inserita l'ora di termine per la presentazione dell'offerta.";
              }else{
                if ( !oteoff.matches(pattern_Formato_Ora) || !oteoff.matches(pattern_Ora)){
                  formatoOteoffValido=false;
                  controlloSuperato = "NO";
                  messaggio += "<br>L'ora di termine per la presentazione dell'offerta non ha un formato valido ('hh:mm').";
                }
              }
            }

            if(oesoff!=null && !"".equals(oesoff) && (!oesoff.matches(pattern_Formato_Ora) || !oesoff.matches(pattern_Ora))){
              formatoOesoffValido = false;
              controlloSuperato = "NO";
              messaggio += "<br>L'ora di apertura plichi non ha un formato valido ('hh:mm').";
            }

            if("3".equals(bando) || ("1".equals(bando) && iterga!=null && iterga.longValue()==1)){
              if(desoff!=null && (oesoff == null || "".equals(oesoff))){
                controlloSuperato = "NO";
                messaggio += "<br>Non è stata inserita l'ora di apertura plichi.";
              }else{
                if(desoff!=null && oesoff != null && !"".equals(oesoff) && dteoffVisibile && dteoff!=null && oteoff!=null && !"".equals(oteoff)
                    && formatoOteoffValido && formatoOesoffValido){
                  Calendar dataApertura = Calendar.getInstance();
                  dataApertura.setTime(new Date(desoff.getTime()));
                  String oraApertura[] = oesoff.split(":");
                  dataApertura.set(Calendar.HOUR_OF_DAY,Integer.parseInt(oraApertura[0]));
                  dataApertura.set(Calendar.MINUTE,Integer.parseInt(oraApertura[1]));

                  Calendar dataTermine = Calendar.getInstance();
                  dataTermine.setTime(new Date(dteoff.getTime()));
                  String oraTermine[] = oteoff.split(":");
                  dataTermine.set(Calendar.HOUR_OF_DAY,Integer.parseInt(oraTermine[0]));
                  dataTermine.set(Calendar.MINUTE,Integer.parseInt(oraTermine[1]));
                  //if(dataTermine.before(dataApertura)){
                  if(dataApertura.compareTo(dataTermine)<0){
                    controlloSuperato = "NO";
                    messaggio += "<br>La data di apertura plichi deve essere successiva o uguale alla data di termine presentazione dell'offerta.";
                  }
                }
              }
            }

            if (ngara == null || (gentip != null && gentip.longValue() == 1) || (genere!=null && genere.longValue()==3)){
              //Se la gara è di tipo OEPV, vi deve essere almeno un lotto OEPV
              select="select count(ngara) from gare where codgar1 = ? and modlicg=?";
              if(genere != null && genere.longValue() == 3)
                select += " and ngara <> codgar1";
              Long conteggioLotti=(Long)this.sqlManager.getObject(select, new Object[]{codgar,new Long(6)});
              if(new Long(6).equals(modlic) && (conteggioLotti==null || (conteggioLotti!=null && conteggioLotti.longValue()==0))){
                controlloSuperato = "NO";
                messaggio += "<br>Non è presente nessun lotto con criterio di aggiudicazione 'Offerta economicamente più vantaggiosa' come invece indicato per la gara.";
              }else if(modlic!=null && modlic.longValue()!=6 && conteggioLotti!=null && conteggioLotti.longValue()>0){
                messaggio += "<br>Sono presenti dei lotti con criterio di aggiudicazione 'Offerta economicamente più vantaggiosa' ma lo stesso criterio non è impostato per la gara.";
                controlloSuperato = "NO";
              }
            }



            if ("1".equals(gartel) && (("1".equals(bando)  && new Long(1).equals(iterga)) || "3".equals(bando))){
              HashMap<String,String> ret = this.controlloPunteggi(ngara, codgar, genere, offtel);
              String msgCriteri = ret.get("msgRitorno");
              if(msgCriteri!=null){
                controlloSuperato = "NO";
                messaggio += msgCriteri;
              }
              String costofissoString =ret.get("costofissoTot");
              if(costofissoString!=null && "true".equals(costofissoString)){
                costofisso = true;
              }

              String sezionitecString =ret.get("sezionitecTot");
              if(sezionitecString!=null && "true".equals(sezionitecString)){
                sezionitec = true;
              }

            }
          }

          if (!new Long(2).equals(genere) && !new Long(2).equals(gentip)){
            Long conteggioLotti=(Long)this.sqlManager.getObject("select count(ngara) from gare1 where codgar1 = ? and ngara <> codgar1 and valtec=?", new Object[]{codgar,"1"});
            if("1".equals(valtec)){
              if(conteggioLotti==null || new Long(0).equals(conteggioLotti)) {
                controlloSuperato = "NO";
                messaggio += "<br>Non è presente nessun lotto per cui è prevista la valutazione dei requisiti mediante busta tecnica come invece indicato per la gara.";
              }
            }else{
              if(conteggioLotti!=null && conteggioLotti.longValue()>0) {
                controlloSuperato = "NO";
                messaggio += "<br>Sono presenti dei lotti per cui è prevista la valutazione dei requisiti mediante busta tecnica ma la stessa proprietà non è impostata per la gara.";
              }
            }
          }
        }



        if ("1".equals(bando) || "3".equals(bando) ||  "0".equals(bando)){
          String tipo="BANDO";
          if( "3".equals(bando))
            tipo="INVITO";
          else if( "0".equals(bando))
            tipo="ESITO";
          HashMap<String,String> risposta=pgManagerEst1.controlloDatiBloccantiInvioSCP(ngara, codgar, cenint, codrup, tipo, gentip, profilo);
          if(risposta!=null){
            if("SI".equals(risposta.get("erroriBloccanti"))){
                controlloSuperato = "NO";
                messaggio += risposta.get("msgErroriBloccanti");
            }
          }
        }


        String tipoWSDM=null;

        if("1".equals(integrazioneWSDM)){
          WSDMConfigurazioneOutType configurazione = gestioneWSDMManager.wsdmConfigurazioneLeggi("FASCICOLOPROTOCOLLO",idconfi);
          if (configurazione.isEsito()){
            tipoWSDM = configurazione.getRemotewsdm();
            page.setAttribute("tipoWSDM", tipoWSDM, PageContext.REQUEST_SCOPE);
          }
        }

        if ("1".equals(gartel) && (("1".equals(bando)  && (Long.valueOf(1).equals(iterga) || isRicercaMercatoNegoziata)) || "3".equals(bando))){
          //Controllo presenza lista lavorazioni (non si applica ad offerte distinte in quanto non gestite come gare telematiche)
          String desc = tabellatiManager.getDescrTabellato("A1110", "1");
          if(desc!=null && !"".equals(desc))
            desc = desc.substring(0,1);
            Long ribcal=null;
            String descA1162 = tabellatiManager.getDescrTabellato("A1162", "1");
            if(descA1162!=null && descA1162.length()>0)
              descA1162 = descA1162.substring(0, 1);
            if(lottoUnico){
              //modlic = (Long)sqlManager.getObject("select modlicg from gare where ngara=?", new Object[]{ngara});
              Vector datiGara = sqlManager.getVector("select modlicg,ribcal from gare where ngara=?", new Object[]{ngara});
              if(datiGara!=null){
                modlic = SqlManager.getValueFromVectorParam(datiGara, 0).longValue();
                ribcal = SqlManager.getValueFromVectorParam(datiGara, 1).longValue();
              }
              Long conteggio = null;
              conteggio = (Long)this.sqlManager.getObject("select count(*) from GCAP where GCAP.NGARA = ? and GCAP.DITTAO is null",
            		  new Object[]{ngara});
              if (conteggio==null || conteggio.longValue()==0){
            	  if((new Long(5).equals(modlic) || new Long(14).equals(modlic)) && ("1".equals(desc) || isRicercaMercatoNegoziata || (Long.valueOf(3)).equals(ribcal) )){
                    controlloSuperato = "NO";
                    messaggio +=  "<br>Non sono state definite le lavorazioni e forniture oggetto dell'offerta a prezzi unitari.";
                }
              } else {
	              if(new Long(5).equals(modlic) || new Long(14).equals(modlic) || new Long(6).equals(modlic) || (new Long(3)).equals(ribcal)){
	                  //Si deve controllare che per ogni lavorazione siano valorizzati l'unità di misura e la quantità
	                  String controlliGCAP[] = this.controlloDatiGcap(ngara, false,(new Long(3)).equals(ribcal),accqua,descA1162);
	                  if("NO".equals(controlliGCAP[0])){
	                    controlloSuperato = "NO";
	                    messaggio +=  controlliGCAP[1];
	                  }
	              }
	              if(isRicercaMercatoNegoziata) {
                   Long countDateConsegna = null;
                   countDateConsegna = (Long)this.sqlManager.getObject("select count(*) from GCAP where GCAP.NGARA = ? and GCAP.DATACONS is null",
	                		  new Object[]{ngara});

	        	    if(countDateConsegna!=null && countDateConsegna.longValue()>0){
	                    controlloSuperato = "NO";
	                    messaggio +=  "<br>Non sono state definite le date di consegna per tutte le lavorazioni.";
	        	    }

                   Long countPrezziUnitari = null;
                   countPrezziUnitari = (Long)this.sqlManager.getObject("select count(*) from GCAP where GCAP.NGARA = ? and GCAP.PREZUN is null",
	                		  new Object[]{ngara});

	        	    if(countPrezziUnitari!=null && countPrezziUnitari.longValue()>0){
	                    controlloSuperato = "NO";
	                    messaggio +=  "<br>Non sono stati definiti i prezzi unitari a base di gara per tutte le lavorazioni.";
	        	    }

	              }
              }
            }else{
              List listaLotti = this.sqlManager.getListVector("select ngara,modlicg,ribcal from gare where codgar1<>ngara and codgar1=? and modlicg in (6,5,14)", new Object[]{codgar});
              if(listaLotti!=null && listaLotti.size()>0){
                String lotto=null;
                Long conteggio = null;
                modlic = null;
                for(int i=0;i<listaLotti.size();i++){
                  lotto = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).getStringValue();
                  modlic = SqlManager.getValueFromVectorParam(listaLotti.get(i), 1).longValue();
                  ribcal = SqlManager.getValueFromVectorParam(listaLotti.get(i), 2).longValue();
                  conteggio = (Long)this.sqlManager.getObject("select count(*)  from GCAP where GCAP.NGARA = ? and GCAP.DITTAO is null",
                      new Object[]{lotto});
                  if (conteggio==null || conteggio.longValue()==0){
                	  if((new Long(5).equals(modlic) || new Long(14).equals(modlic)) && ("1".equals(desc) || (new Long(3)).equals(ribcal) )){
	                      controlloSuperato = "NO";
	                      messaggio +=  "<br>Non sono state definite le lavorazioni e forniture oggetto dell'offerta a prezzi unitari per il lotto " + lotto + ".";
	                  }
                  }else{
                    //Si deve controllare che per ogni lavorazione siano valorizzati l'unità di misura e la quantità
                    String controlliGCAP[] = this.controlloDatiGcap(lotto,true,(new Long(3)).equals(ribcal),accqua,descA1162);
                    if("NO".equals(controlliGCAP[0])){
                      controlloSuperato = "NO";
                      messaggio +=  controlliGCAP[1];
                    }
                  }
                }
              }
            }


        }

        if("1".equals(integrazioneWSERP) && "AVM".equals(tipoWSERP)){
          //Verifica che non sia presente una situazione mista
          if(lottoUnico){
            Long countWithRda = (Long)this.sqlManager.getObject("select count(*) from GCAP where GCAP.NGARA = ? and CODRDA is not null",
                new Object[]{ngara});
            Long countWithoutRda = (Long)this.sqlManager.getObject("select count(*) from GCAP where GCAP.NGARA = ? and CODRDA is null",
                new Object[]{ngara});
            if(countWithRda != null && countWithoutRda != null && countWithRda > 0 && countWithoutRda > 0){
              controlloSuperato = "NO";
              messaggio +=  "<br>" + "Nelle lavorazioni e' presente una situazione mista: lavorazioni definite con rda e lavorazioni senza rda";

            }

          }else{
            List listaLotti = this.sqlManager.getListVector("select ngara,modlicg from gare where codgar1<>ngara and codgar1=?", new Object[]{codgar});
            if(listaLotti!=null && listaLotti.size()>0){
              String lotto=null;
              for(int i=0;i<listaLotti.size();i++){
                lotto = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).getStringValue();
                modlic = SqlManager.getValueFromVectorParam(listaLotti.get(i), 1).longValue();
                Long countWithRda = (Long)this.sqlManager.getObject("select count(*) from GCAP where GCAP.NGARA = ? and CODRDA is not null",
                    new Object[]{lotto});
                Long countWithoutRda = (Long)this.sqlManager.getObject("select count(*) from GCAP where GCAP.NGARA = ? and CODRDA is null",
                    new Object[]{lotto});
                if(countWithRda != null && countWithoutRda != null && countWithRda > 0 && countWithoutRda > 0){
                  controlloSuperato = "NO";
                  messaggio +=  "<br>" + "Nelle lavorazioni del lotto " + lotto + " e' presente una situazione mista: lavorazioni definite con rda e lavorazioni senza rda";
                }
              }
            }
          }

          //Verifica corrispondenza articoli su SAP
          ProfiloUtente profiloUt = (ProfiloUtente) page.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
          Long syscon = new Long(profiloUt.getId());
          String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, "WSERP");
          String username = credenziali[0];
          String password = credenziali[1];
          if(lottoUnico){
            Long countLav = (Long)this.sqlManager.getObject("select count(*) from GCAP where GCAP.NGARA = ?",
                new Object[]{ngara});
            if(countLav>0){
              String controlliGCAP[] = gestioneWSERPManager.verificaIntegrazioneArticoli(username, password, "WSERP", ngara);
              if("NO".equals(controlliGCAP[0])){
                controlloSuperato = "NO";
                //messaggio +=  controlliGCAP[1]; mettiamo un messaggio generico
                messaggio += "<br>" + "Alcune lavorazioni non risultano collegate ad articoli dell'ERP.";
              }else if("ERR".equals(controlliGCAP[0])){
                messaggio += "<br>" + "Si e' verificato un errore durante la lettura degli articoli dell'ERP";
              }
            }
          }else{
            List listaLotti = this.sqlManager.getListVector("select ngara,modlicg from gare where codgar1<>ngara and codgar1=?", new Object[]{codgar});
            if(listaLotti!=null && listaLotti.size()>0){
              String lotto=null;
              for(int i=0;i<listaLotti.size();i++){
                lotto = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).getStringValue();
                modlic = SqlManager.getValueFromVectorParam(listaLotti.get(i), 1).longValue();
                Long countLav = (Long)this.sqlManager.getObject("select count(*)  from GCAP where GCAP.NGARA = ?",
                    new Object[]{lotto});
                if(countLav>0){
                  String controlliGCAP[] = gestioneWSERPManager.verificaIntegrazioneArticoli(username, password, "WSERP", ngara);
                  if("NO".equals(controlliGCAP[0])){
                    controlloSuperato = "NO";
                    //messaggio +=  controlliGCAP[1]; mettiamo un messaggio generico
                    messaggio += "<br>" + "Alcune lavorazioni non risultano collegate ad articoli dell'ERP:";
                  }else if("ERR".equals(controlliGCAP[0])){
                    messaggio += "<br>" + "Si e' verificato un errore durante la lettura degli articoli dell'ERP";
                  }
                }
              }
            }

          }//a lotti
        }//end WSERP

        List listaDocumentiGara = null;
        if ("1".equals(bando) || "3".equals(bando) ||  "0".equals(bando)){
          //Controllo sulla valorizzazione del campoDESCRIZIONE.DOCUMGARA
          //bando=0 si controllano i gruppi 1,2,3,4,5
          //bando=1 si controllano i gruppi 1,2,3
          //bando=3 si controllano i gruppi 3,6

          String condizionwGruppo="GRUPPO in (1,2,3,4,5)";
          if("1".equals(bando)){
            if(iterga != null && (iterga.longValue()==2 || iterga.longValue()==4)){
              condizionwGruppo="((GRUPPO in (1,2)) or (GRUPPO = 3 and BUSTA = 4))";}
            else{
              condizionwGruppo="GRUPPO in (1,2,3)";}
            }
          else if("3".equals(bando))
            condizionwGruppo="GRUPPO in (3,6)";

          select="select DESCRIZIONE from DOCUMGARA where CODGAR=? and " +  condizionwGruppo + " order by norddocg";
          listaDocumentiGara = sqlManager.getListVector(
              select, new Object[] { codgar});

          boolean descrizionePresente=true;
          String descrizione = null;
          for (int i = 0; i < listaDocumentiGara.size(); i++) {
            descrizione = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(listaDocumentiGara.get(i),0).getStringValue());
            if(descrizione==null){
              descrizionePresente=false;
              break;
            }
          }
          if(!descrizionePresente){
            controlloSuperato = "NO";
            messaggio +=  "<br>Per tutti i documenti deve essere specificata la descrizione.";
          }
        }

        //controllo documenti di gara
        //if ("1".equals(bando) || ("3".equals(bando) && "1".equals(gartel)) || "0".equals(bando)){
        if ("1".equals(bando) || "3".equals(bando)  || "0".equals(bando)){
          //Controllo tabellato A1108 per la gestione dell'url nei documenti di gara


          String desc = tabellatiManager.getDescrTabellato("A1108", "1");
          if(desc!=null && !"".equals(desc))
            desc = desc.substring(0,1);
          boolean gestioneUrl=false;
          if("1".equals(desc))
            gestioneUrl=true;

          select="select count(codgar) from documgara where codgar= ? and statodoc is null";
          //and IDDOCDG is not null";
          if(gentip != null && new Long(3).equals(gentip))
            select += " and ngara is null";

          String gruppo="GRUPPO = 1";
          String condizioneGruppo="GRUPPO in (1,10,15)";
          if("0".equals(bando)){
            gruppo="GRUPPO = 4";
            condizioneGruppo="GRUPPO in (4,10,15)";
          }else if("3".equals(bando)){
            gruppo="GRUPPO = 6";
            condizioneGruppo="GRUPPO in (6,10,15)";
          }
          String codizioneTipologie = this.tipologieVisibiliGruppo(codgar, gruppo);
          numOccorrenze = (Long) sqlManager.getObject(select + " and " + gruppo + codizioneTipologie, new Object[]{codgar});
          if((numOccorrenze== null || numOccorrenze.longValue()==0) && ("1".equals(bando) || "0".equals(bando) || ("3".equals(bando) && "1".equals(gartel)))){
           controlloSuperato = "NO";
            if("0".equals(bando))
              messaggio += "<br>Non è stato inserito nessun documento relativo all'esito di gara.";
            else if("3".equals(bando)) {
              messaggio += "<br>Non è stato inserito nessun documento relativo all'invito.";
              noDocInvito = true;
            }
            else{
              if(genere!=null && genere.longValue()==11)
                messaggio += "<br>Non è stato inserito nessun documento relativo al bando o avviso.";
              else
                messaggio += "<br>Non è stato inserito nessun documento relativo al bando o avviso.";
            }
          }

          //Controllo presenza allegato o url
          codizioneTipologie = this.tipologieVisibiliGruppo(codgar, condizioneGruppo);
          String[] esito = this.controlloAllegatoUrl(ngara, codgar, genere, condizioneGruppo + codizioneTipologie, gestioneUrl,bando);
          if("NO".equals(esito[0])){
            controlloSuperato = "NO";
            messaggio+=esito[1];
          }

          if("1".equals(bando) || "0".equals(bando) || "3".equals(bando)){
            //Controllo sulla richiesta di firma degli allrgati
            select="select count(codgar) from documgara, w_docdig where codgar= ? and " + condizioneGruppo + codizioneTipologie + " and documgara.idprg=w_docdig.idprg and documgara.iddocdg=w_docdig.iddocdig and digfirma ='1'";
            numOccorrenze = (Long) sqlManager.getObject(select, new Object[]{codgar});
            if(numOccorrenze!= null && numOccorrenze.longValue()>0){
              controlloSuperato = "NO";
              if("0".equals(bando))
                messaggio += "<br>Ci sono dei documenti di gara da pubblicare in attesa di firma.";
              else if("3".equals(bando))
                messaggio += "<br>Ci sono dei documenti di gara da pubblicare in attesa di firma.";
              else{
                if(genere!=null && (genere.longValue()==11 || genere.longValue()==10 || genere.longValue()==20))
                  messaggio += "<br>Ci sono dei documenti da pubblicare in attesa di firma.";
                else
                  messaggio += "<br>Ci sono dei documenti di gara da pubblicare in attesa di firma.";
              }
            }
          }

          //Controllo sulla data provvedimento
          String dataOggi = UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_GG_MM_AAAA);
          Date dataOdierna = UtilityDate.convertiData(dataOggi, UtilityDate.FORMATO_GG_MM_AAAA);
          select="select count(codgar) from documgara where codgar= ? and " + condizioneGruppo + codizioneTipologie + " and dataprov is not null and dataprov > ?";
          numOccorrenze = (Long) sqlManager.getObject(select, new Object[]{codgar, dataOdierna});
          if(numOccorrenze!= null && numOccorrenze.longValue()>0){
            controlloSuperato = "NO";
            messaggio += "<br>Ci sono dei documenti da pubblicare con data provvedimento successiva alla data corrente";
          }

          //Se specificato da configurazione il formato degli allegati, viene verificato che gli allegati di tipo=6 rispettino tutti il formato
          String formatoAllegati = ConfigManager.getValore(CostantiAppalti.FORMATO_ALLEGATI);
          if(formatoAllegati!=null && !"".equals(formatoAllegati) && "3".equals(bando)){
            select="select dignomdoc from documgara, w_docdig where codgar= ? and GRUPPO = 6 and documgara.idprg=w_docdig.idprg and documgara.iddocdg=w_docdig.iddocdig";
            select += " and (idstampa != 'DGUE' or idstampa is null)";
            List listaFileAllegati=this.sqlManager.getListVector(select, new Object[]{codgar});
            /*
            int numFormatoCorretto=0;
            if(listaFileAllegati!=null && listaFileAllegati.size()>0){
              String formatiValidi[] = formatoAllegati.split(";");
              String nomeFile=null;
              String estensione=null;
              for(int i=0;i<listaFileAllegati.size();i++){
                nomeFile=SqlManager.getValueFromVectorParam(listaFileAllegati.get(i), 0).getStringValue();
                estensione = nomeFile.substring(nomeFile.lastIndexOf(".")+1);
                for(int j=0;j<formatiValidi.length;j++){
                  if((formatiValidi[j].toUpperCase()).equals(estensione.toUpperCase())){
                    numFormatoCorretto++;
                    break;
                  }
                }
              }
            }*/
            ;
            if(!this.pgManagerEst1.controlloAllegatiFormatoValido(listaFileAllegati,0,formatoAllegati)){
              controlloSuperato = "NO";
              messaggio += "<br>Ci sono dei documenti allegati all'invito che hanno un formato non valido.";
            }
          }

          //Se attiva l'integrazione MDGUE e ci sono dei documenti con IDSTAMPA='DGUE', gli allegati posso essere solo xml
          String urlMDGUE = ConfigManager.getValore(CostantiAppalti.PROP_INTEGRAZIONE_MDGUE_URL);
          if(urlMDGUE!=null && !"".equals(urlMDGUE) ) {
            Long gruppoDGUE= new Long(1);
            if("3".equals(bando))
              gruppoDGUE = new Long(6);
            select="select dignomdoc from documgara, w_docdig where codgar= ? and GRUPPO = ? and documgara.idprg=w_docdig.idprg and documgara.iddocdg=w_docdig.iddocdig";
              select +=" and idstampa = 'DGUE' and (isarchi='2' or isarchi is null)";
            List<?> listaFileAllegati=this.sqlManager.getListVector(select, new Object[]{codgar,gruppoDGUE});
            if(!this.pgManagerEst1.controlloAllegatiFormatoValido(listaFileAllegati,0,"xml")){
              controlloSuperato = "NO";
              messaggio += "<br>Ci sono dei documenti di gara con integrazione M-DGUE che hanno un formato non valido, non hanno estensione xml.";
            }
          }

          //Controllo documenti allegati (Solo in caso di pubblicazione area riservata)
          if ("1".equals(gartel) && "3".equals(bando)) {
        	  String inizializzaAllmail = tabellatiManager.getDescrTabellato("A1173", "1");
        	  if(inizializzaAllmail!=null && !"".equals(inizializzaAllmail))
        		  inizializzaAllmail = inizializzaAllmail.substring(0,1);
        	  if("1".equals(inizializzaAllmail) && !noDocInvito) {
        		  //Deve esserci almeno un documento dell'invito da allegare alla mail
        		  Long nDocInvitoAllegatoMail=new Long(0);
        		  select="select count(*) from documgara where codgar=? and allmail='1' and gruppo=6";
        		  nDocInvitoAllegatoMail=(Long)this.sqlManager.getObject(select, new Object[]{codgar});
        		  if(nDocInvitoAllegatoMail<new Long(1)){
        			  controlloSuperato = "NO";
        			  messaggio += "<br>Non ci sono documenti relativi all'invito che vengono allegati alla comunicazione. Indicarne almeno uno da allegare";
        		  }
          }}

          //Controlli sui documenti per gare telematiche
          if("1".equals(gartel) && (("1".equals(bando) && iterga.longValue()==1) || "3".equals(bando))){
            if(!formularioCompletoAbilitato) {
              Object parametri[]=null;
              Object parametriTec[]=null;
              modlic = null;
              boolean controlloDocTecSuperto=true;
              //Controllo documenti per la busta tecnica
              if(lottoUnico){
                select="select count(codgar) from DOCUMGARA where CODGAR=? and NGARA = ? and gruppo=? and busta=? ";
                parametri = new Object[] { codgar,ngara, new Long(3), new Long(2) };
                parametriTec = new Object[] { codgar,ngara, new Long(3), new Long(2) };
                modlic = (Long)sqlManager.getObject("select modlicg from gare where ngara=?", new Object[]{ngara});
              }else{
                select="select count(codgar) from DOCUMGARA where CODGAR=? and gruppo=? and busta=? ";
                parametri = new Object[] { codgar, new Long(3), new Long(2) };
                parametriTec = new Object[] { codgar, new Long(3), new Long(2) };
                modlic = (Long)sqlManager.getObject("select modlic from torn where codgar=?", new Object[]{codgar});
              }
              select+=" and (isarchi is null or isarchi <>'1') ";
              String selectSenzaContesto = select;
              String selectDocumenti = select + " and CONTESTOVAL is null";
              if(!(new Long(1)).equals(bustalotti)){
                selectSenzaContesto+= " and CONTESTOVAL is null ";
                if(new Long(6).equals(modlic) || "1".equals(valtec)){
                  Long conteggio = (Long)sqlManager.getObject(selectSenzaContesto, parametri);
                  if(conteggio==null || (conteggio!=null && conteggio.longValue()==0)){
                    controlloSuperato = "NO";
                    conteggio = (Long)sqlManager.getObject(select, parametri);
                    if(conteggio==null || (conteggio!=null && conteggio.longValue()==0)){
                      messaggio +=  "<br>Non è stato inserito nessun documento richiesto ai concorrenti per la busta tecnica.";
                      controlloDocTecSuperto=false;
                    }else{
                      messaggio +=  "<br>Tutti i documenti richiesti ai concorrenti per la busta tecnica hanno l'indicazione del contesto di validità. Deve esserne inserito almeno uno valido per qualsiasi tipo di operatore.";
                    }
                  }
                }
                //Controllo per le gare con valutazione tecnica anonima
                if(lottoUnico && new Long(6).equals(modlic)) {
                  String anonimatec= (String)sqlManager.getObject("select anonimatec from gare1 where ngara=?", new Object[]{ngara});
                  if("1".equals(anonimatec)) {
                    String selectDocumentiGareAnonime = select + " and MODFIRMA = 1";
                    Long conteggio = (Long)sqlManager.getObject(selectDocumentiGareAnonime, parametri);
                    if(conteggio!=null && conteggio.longValue() > 0){
                      controlloSuperato = "NO";
                      messaggio +=  "<br>Ci sono dei documenti richiesti per la busta tecnica per cui è prevista la firma digitale. Essendo prevista la valutazione anonima della busta tecnica, non è possibile indicare tale formato";
                    }
                  }
                }
                if(lottoUnico)
                  parametri[3]=new Long(3);
                else
                  parametri[2]=new Long(3);
                Long conteggio = (Long)sqlManager.getObject(selectSenzaContesto, parametri);
                if((conteggio==null || (conteggio!=null && conteggio.longValue()==0)) && !costofisso){
                  controlloSuperato = "NO";
                  conteggio = (Long)sqlManager.getObject(select, parametri);
                  if((conteggio==null || (conteggio!=null && conteggio.longValue()==0))){
                    messaggio +=  "<br>Non è stato inserito nessun documento richiesto ai concorrenti per la busta economica.";
                  }else{
                    messaggio +=  "<br>Tutti i documenti richiesti ai concorrenti per la busta economica hanno l'indicazione del contesto di validità. Deve esserne inserito almeno uno valido per qualsiasi tipo di operatore.";
                  }
                }
              }else{
                //nel caso di bustalotti=1 si devono controllare i documenti sia a livello di gara che di lotto
                select="select count(codgar) from DOCUMGARA where CODGAR=? and gruppo=? and busta=? and ngara is null and (isarchi is null or isarchi <>'1')";
                selectSenzaContesto = select + " and CONTESTOVAL is null ";
                //busta tecnica
                parametri = new Object[] { codgar, new Long(3), new Long(2) };
                if(new Long(6).equals(modlic) || "1".equals(valtec)){
                  Long conteggio = (Long)sqlManager.getObject(select, parametri);
                  if(conteggio==null || (conteggio!=null && conteggio.longValue()==0)){
                    //Si deve ciclare a livello di lotti
                    if(!pgManager.controlloDocumentazioneLotti(codgar, true, new Long(2),true, false,false)){
                      controlloSuperato = "NO";
                      messaggio +=  "<br>Non è stato inserito nessun documento richiesto ai concorrenti per la busta tecnica (oppure non è stato inserito per tutti i lotti della gara).";
                      controlloDocTecSuperto = false;
                    }else{
                      if(!pgManager.controlloDocumentazioneLotti(codgar, true, new Long(2),true, false,true)){
                        controlloSuperato = "NO";
                        messaggio +=  "<br>Tutti i documenti richiesti ai concorrenti per la busta tecnica (oppure tutti i documenti relativi a qualche lotto), hanno l'indicazione del contesto di validità. Deve esserne inserito almeno uno valido per qualsiasi tipo di operatore.";
                      }
                    }
                  }else{
                    Long conteggioSenzaContesto = (Long)sqlManager.getObject(selectSenzaContesto, parametri);
                    if(conteggioSenzaContesto==null || (conteggioSenzaContesto!=null && conteggioSenzaContesto.longValue()==0)){
                      controlloSuperato = "NO";
                      messaggio +=  "<br>Tutti i documenti richiesti ai concorrenti per la busta tecnica (oppure tutti i documenti relativi a qualche lotto), hanno l'indicazione del contesto di validità. Deve esserne inserito almeno uno valido per qualsiasi tipo di operatore.";
                    }
                  }
                }
                //busta economica
                //Se tutti i lotti sono senza criteri economici si salta il controllo sulla busta economica
                if(!costofisso){
                  parametri = new Object[] { codgar, new Long(3), new Long(3) };
                  Long conteggio = (Long)sqlManager.getObject(select, parametri);
                  if(conteggio==null || (conteggio!=null && conteggio.longValue()==0)){
                    //Si deve controllare che per i lotti che hanno criteri economici sia presente la busta economica
                    if(!pgManager.controlloDocumentazioneLotti(codgar, false, new Long(3), true, true, false)){
                      controlloSuperato = "NO";
                      messaggio +=  "<br>Non è stato inserito nessun documento richiesto ai concorrenti per la busta economica (oppure non è stato inserito per tutti i lotti della gara).";
                    }else{
                      if(!pgManager.controlloDocumentazioneLotti(codgar, false, new Long(3), true, true, true)){
                        controlloSuperato = "NO";
                        messaggio +=  "<br>Tutti i documenti richiesti ai concorrenti per la busta economica (oppure tutti i documenti relativi a qualche lotto), hanno l'indicazione del contesto di validità. Deve esserne inserito almeno uno valido per qualsiasi tipo di operatore.";
                      }
                    }
                  }else{
                    Long conteggioSenzaContesto = (Long)sqlManager.getObject(selectSenzaContesto, parametri);
                    if(conteggioSenzaContesto==null || (conteggioSenzaContesto!=null && conteggioSenzaContesto.longValue()==0)){
                      controlloSuperato = "NO";
                      messaggio +=  "<br>Tutti i documenti richiesti ai concorrenti per la busta economica (oppure tutti i documenti relativi a qualche lotto), hanno l'indicazione del contesto di validità. Deve esserne inserito almeno uno valido per qualsiasi tipo di operatore.";
                    }
                  }
                }
              }

              //Controlli sezioni busta tecnica
              if (sezionitec && controlloDocTecSuperto) {
                //Controllo che tutti i documenti abbiano SEZTEC valorizzato

                String selectSeztec = selectDocumenti + " and seztec is null";
                Long conteggioDoc = (Long) sqlManager.getObject(selectSeztec, parametriTec);
                if (conteggioDoc != null && conteggioDoc.longValue() > 0) {
                  controlloSuperato = "NO";
                  messaggio +=  "<br>Non è stata specificata la sezione, qualitativa o quantitativa, in tutti i documenti richiesti ai concorrenti per la busta tecnica.";
                } else {
                  //Controllo che ci sia almeno un documento con SEZTEC = 1 e almeno uno con SEZTEC =2
                  if (lottoUnico) {
                    selectSeztec = selectDocumenti + " and seztec = 1";
                    Long conteggiosez1 = (Long) sqlManager.getObject(selectSeztec, parametriTec);
                    selectSeztec = selectDocumenti + " and seztec = 2";
                    Long conteggiosez2 = (Long) sqlManager.getObject(selectSeztec, parametriTec);
                    if (conteggiosez1 == null || new Long(0).equals(conteggiosez1)) {
                      controlloSuperato = "NO";
                      messaggio +=  "<br>Non è stato inserito nessun documento richiesto ai concorrenti per la sezione qualitativa della busta tecnica.";
                    }
                    if (conteggiosez2 == null || new Long(0).equals(conteggiosez2)) {
                      controlloSuperato = "NO";
                      messaggio +=  "<br>Non è stato inserito nessun documento richiesto ai concorrenti per la sezione quantitativa della busta tecnica.";
                    }
                  } else {
                    //Controllo su documenti definiti a livello di gara
                    selectSeztec = selectDocumenti + " and seztec = 1 and ngara is null";
                    Long conteggiosez1 = (Long) sqlManager.getObject(selectSeztec, parametriTec);
                    if (conteggiosez1 == null || new Long(0).equals(conteggiosez1)) {
                      selectSeztec = selectDocumenti + " and seztec = 1 and ngara in (select g1.ngara from gare1 g1 where g1.ngara = ngara and sezionitec='1')";
                      conteggiosez1 = (Long) sqlManager.getObject(selectSeztec, parametriTec);
                      if (conteggiosez1 == null || new Long(0).equals(conteggiosez1)) {
                        controlloSuperato = "NO";
                        messaggio +=  "<br>Non è stato inserito nessun documento richiesto ai concorrenti per la sezione qualitativa della busta tecnica.";
                      }
                    }
                    selectSeztec = selectDocumenti + " and seztec = 2 and ngara is null";
                    Long conteggiosez2 = (Long) sqlManager.getObject(selectSeztec, parametriTec);
                    if (conteggiosez2 == null || new Long(0).equals(conteggiosez2)) {
                      selectSeztec = selectDocumenti + " and seztec = 2 and ngara in (select g1.ngara from gare1 g1 where g1.ngara = ngara and sezionitec='1')";
                      conteggiosez2 = (Long) sqlManager.getObject(selectSeztec, parametriTec);
                      if (conteggiosez2 == null || new Long(0).equals(conteggiosez2)) {
                        controlloSuperato = "NO";
                        messaggio +=  "<br>Non è stato inserito nessun documento richiesto ai concorrenti per la sezione quantitativa della busta tecnica.";
                      }
                    }
                  }
                }
              }
            }else {
              //Controlli qfrom
              String chiaveQform = ngara;
              Long conteggioQuestionari=null;
              if(!lottoUnico)
                chiaveQform =codgar;

              String selectBuste = "select count(*) from qform where entita='GARE' and key1=? and busta=? and (stato = 1 or stato = 5)";

              //Controlli sulla presenza del qform per la busta amministrativa
              if(!"1".equals(nobustamm)) {
                conteggioQuestionari = (Long)this.sqlManager.getObject(selectBuste, new Object[] {chiaveQform, new Long(1)});
                if(new Long(0).equals(conteggioQuestionari) || conteggioQuestionari == null) {
                  controlloSuperato = "NO";
                  messaggio +=  "<br>Non è stato associato un q-form nei documenti richiesti ai concorrenti per la busta amministrativa.";
                }
              }


              //Controlli sulla presenza del qform per la busta tecnica
              if(new Long(6).equals(modlic) || "1".equals(valtec)){
                if(new Long(3).equals(genere)) {
                  //Si devono controllare i qform dei lotti
                  String esitoControllo[] = this.controlloQformLotti(chiaveQform, new Long(2));
                  if("NO".equals(esitoControllo[0]) ) {
                    controlloSuperato = "NO";
                    messaggio += esitoControllo[1];
                  }
                }else {
                  conteggioQuestionari = (Long)this.sqlManager.getObject(selectBuste, new Object[] {chiaveQform, new Long(2)});
                  if(new Long(0).equals(conteggioQuestionari) || conteggioQuestionari == null) {
                    controlloSuperato = "NO";
                    messaggio +=  "<br>Non è stato associato un q-form nei documenti richiesti ai concorrenti per la busta tecnica.";
                  }
                }
              }

              //Controlli sulla presenza del qform per la busta economica
              if(!costofisso){
                if(new Long(3).equals(genere)) {
                  //Si devono controllare i qform dei lotti
                  String esitoControllo[] = this.controlloQformLotti(chiaveQform, new Long(3));
                  if("NO".equals(esitoControllo[0]) ) {
                    controlloSuperato = "NO";
                    messaggio += esitoControllo[1];
                  }
                }else {
                  conteggioQuestionari = (Long)this.sqlManager.getObject(selectBuste, new Object[] {chiaveQform, new Long(3)});
                  if(new Long(0).equals(conteggioQuestionari) || conteggioQuestionari == null) {
                    controlloSuperato = "NO";
                    messaggio +=  "<br>Non è stato associato un q-form nei documenti richiesti ai concorrenti per la busta economica.";
                  }
                }
              }

            }
          }else if(formularioCompletoAbilitato && "1".equals(gartel) && ("1".equals(bando) && (iterga.longValue()==2) || iterga.longValue()==4 || iterga.longValue()==7)){
            //Controlli sulla presenza del qform per la busta di prequalifica
            String chiaveQform = ngara;
            Long conteggioQuestionari=null;
            if(!lottoUnico)
              chiaveQform =codgar;

            conteggioQuestionari = (Long)this.sqlManager.getObject("select count(*) from qform where entita='GARE' and key1=? and busta=? and (stato = 1 or stato = 5)", new Object[] {chiaveQform, new Long(4)});
            if(new Long(0).equals(conteggioQuestionari) || conteggioQuestionari == null) {
              controlloSuperato = "NO";
              messaggio +=  "<br>Non è stato associato un q-form nei documenti richiesti ai concorrenti per la busta di prequalifica.";
            }

          }

          //Controllo sull'esistenza di ditte in gara con INVGAR=1
          if ("1".equals(gartel) && "3".equals(bando)) {
            String selectConteggio = "select count(ngara5) from ditg where ngara5=? and invgar='1'";
            String gara = ngara;
            if (!lottoUnico)
              gara = codgar;
            Long conteggio = (Long)this.sqlManager.getObject(selectConteggio,
                new Object[]{gara});
            if (conteggio != null && conteggio.longValue() == 0) {
              controlloSuperato = "NO";
              messaggio +=  "<br>Non ci sono operatori da invitare alla gara.";
            } else {

              String countAmmgar ="select count(*) from ditg where ngara5 = ? and (ammgar = '1' or ammgar is null)";
              Long conteggioAmmgar = (Long) sqlManager.getObject(countAmmgar, new Object[]{gara});
              if (conteggio.intValue() != conteggioAmmgar.intValue()) {
                controlloSuperato = "NO";
                messaggio += "<br>Si è verificata un'incongruenza nei dati: ci sono ditte escluse in fase di invito che risultano erroneamente ancora ammesse alla gara.";
              }
            }
          }



          //Per gara telematica e bando=3 si devono caricare le ditte della gara con AMMGAR=1 o null.
          if("NO".equals(controlloSuperato))
            visualizzaDettaglioComunicazione = false;

          if("3".equals(bando) && visualizzaDettaglioComunicazione && "1".equals(gartel)){

            boolean delegaInvioMailDocumentaleAbilitata = false;

            boolean delegaInvioDocumentale = false;
            if("1".equals(integrazioneWSDM)){
              valoreWSDM = ConfigManager.getValore("pg.wsdm.invioMailPec."+idconfi);
              if(valoreWSDM!=null && "1".equals(valoreWSDM))
                delegaInvioMailDocumentaleAbilitata=true;

              if(delegaInvioMailDocumentaleAbilitata && ("PALEO".equals(tipoWSDM) || "JIRIDE".equals(tipoWSDM) || "ENGINEERING".equals(tipoWSDM) || "TITULUS".equals(tipoWSDM)
                  || "SMAT".equals(tipoWSDM) || "ENGINEERINGDOC".equals(tipoWSDM)))
                delegaInvioDocumentale= true;
            }
            HttpSession session = page.getSession();
            String uffint = (String) session.getAttribute("uffint");
            HashMap gestioneDestinatari = this.gestioneDestinatari(ngara, codgar, genere, pgManager, delegaInvioDocumentale, false);
            if("NO".equals(gestioneDestinatari.get("controlloSuperato"))){
              controlloSuperato = "NO";
              messaggio += (String)gestioneDestinatari.get("messaggio");
            }
            List listaDestinatari = (List)gestioneDestinatari.get("listaDestinatari");
            page.setAttribute("listaDestinatari", listaDestinatari, PageContext.REQUEST_SCOPE);

            //Caricamento dei documenti di gruppo=6
            List listaDocumenti = this.getListaDocumenti(genere, ngara, codgar, new Long(6));
            if(listaDocumenti!=null && listaDocumenti.size()>0){
              page.setAttribute("listaDocumenti", listaDocumenti, PageContext.REQUEST_SCOPE);
            }

            //Si controlla che la dimensione totale dei documenti non superi il limite consentito

            String idcfg = null;
            cenint = null;
            //Valorizzazione di IDCFG
            try {
              if(ngara==null || "".equals(ngara) || (genere!=null && genere.longValue()==3)){
                cenint = (String)sqlManager.getObject("select t.cenint from torn t where t.codgar = ?", new Object[]{codgar});
              }else{
                cenint = (String)sqlManager.getObject("select t.cenint from gare g,torn t where g.codgar1 = t.codgar and ngara = ?", new Object[]{ngara});
              }
              cenint = UtilityStringhe.convertiNullInStringaVuota(cenint);
              if(!"".equals(cenint)){
                idcfg = cenint;
              }else{
                idcfg = uffint;
              }
            } catch (SQLException sqle) {
              throw new GestoreException("Errore nella lettura di TORN.CENINT",null, sqle);
            }

            HashMap controlloDimensioneAllegati = this.controlloDimensioneAllegati(listaDocumenti, idcfg, fileAllegatoManager);
            if("NO".equals(controlloDimensioneAllegati.get("controlloSuperato"))){
              controlloSuperato = "NO";
              messaggio += (String)controlloDimensioneAllegati.get("messaggio");
            }
          }
          
          //Integrazione programmazione: nel caso di lotto di gara e di gara ad offerta unica si controlla anche il collegamento delle RdA
          if (gentip != null && (gentip.longValue() == 1 || gentip.longValue() == 3) && gestioneProgrammazioneManager.isAttivaIntegrazioneProgrammazione()){
            select="select numrda from garerda where codgar = ? and ngara is null and numrda not in (select numrda from garerda where codgar = ? and ngara is not null)";

            boolean rdaCollegate = true;
            List<?> listaRdA = sqlManager.getListVector(select, new Object[]{codgar,codgar});
            if(listaRdA!=null && listaRdA.size()>0){
              for (int i = 0; i < listaRdA.size(); i++) {
                //String numrda = (String) SqlManager.getValueFromVectorParam(listaRdA.get(i), 0).getValue();
                controlloSuperato = "NO";
                rdaCollegate=false;
                }
              if(!rdaCollegate)
                messaggio += "<br>Ci sono RdA/RdI associate alla gara che non sono state collegate ad alcun lotto.";
              }
            }


       }

      } catch (SQLException e) {
        throw new JspException("Errore il controllo dei campi obbligatori ", e);
      } catch (GestoreException e) {
        throw new JspException("Errore il controllo dei campi obbligatori ", e);
      }

      try{
        if(pubBandoRegioneMarche || pubBandoATC)  {
          if(!esisteUrlPubblicazione){
            messaggio += "<br>Non risulta configurata l'URL di accesso al servizio di pubblicazione sul sito istituzionale";
          }
          if(!esisteUserPubblicazione){
            messaggio += "<br>Non risulta configurato l'User di accesso al servizio di pubblicazione sul sito istituzionale";
          }
          if(!esisteTokenPubblicazione){
            messaggio += "<br>Non risulta configurato il Token di accesso al servizio di pubblicazione sul sito istituzionale";
          }
          if("1".equals(bando)){
            if(genere!=null && (genere.longValue()==11 || genere.longValue()==10 || genere.longValue()==20)){
               if (ConfigManager.getValore(CostantiAppalti.PROP_WS_PORTALEAPPALTI_AVVISI_URL)==null
                  || "".equals(ConfigManager.getValore(CostantiAppalti.PROP_WS_PORTALEAPPALTI_AVVISI_URL))){
                controlloSuperato = "NO";
                messaggio += "<br>Non risulta configurata l'URL del portale Appalti di accesso al dettaglio di avvisi,elenchi e cataloghi";
               }
            }else{
               if (ConfigManager.getValore(CostantiAppalti.PROP_WS_PORTALEAPPALTI_BANDI_URL)==null
                   || "".equals(ConfigManager.getValore(CostantiAppalti.PROP_WS_PORTALEAPPALTI_BANDI_URL))){
                 controlloSuperato = "NO";
                 messaggio += "<br>Non risulta configurata l'URL del portale Appalti di accesso al dettaglio dei bandi di gara";
               }
            }
          }
          if("0".equals(bando) && (ConfigManager.getValore(CostantiAppalti.PROP_WS_PORTALEAPPALTI_ESITI_URL)==null
              || "".equals(ConfigManager.getValore(CostantiAppalti.PROP_WS_PORTALEAPPALTI_ESITI_URL)))){
            controlloSuperato = "NO";
            messaggio += "<br>Non risulta configurata l'URL del portale Appalti di accesso al dettaglio degli esiti di gara";
          }
          if(pubBandoRegioneMarche && esisteUrlPubblicazione){
            List listaTabellatiRegioneMarche[] = this.getTabellatiRegioneMarche();
            page.setAttribute("listaStruttureRegionali", listaTabellatiRegioneMarche[0], PageContext.REQUEST_SCOPE);
            page.setAttribute("listaTemiRegionali", listaTabellatiRegioneMarche[1], PageContext.REQUEST_SCOPE);
            page.setAttribute("listaTipologieProcedure", listaTabellatiRegioneMarche[2], PageContext.REQUEST_SCOPE);
            if(result.isResult() && result.getBando()!=null ){
              if("1".equals(bando)){
                page.setAttribute("idTemaRegionale", new Long(result.getBando().getTemaRegionaleID()), PageContext.REQUEST_SCOPE);
                page.setAttribute("idTipologiaBando", new Long(result.getBando().getTipologiaProcedura()), PageContext.REQUEST_SCOPE);
                page.setAttribute("idStrutturaRegionale", new Long(result.getBando().getStrutturaRiferimentoID()), PageContext.REQUEST_SCOPE);
              }
            }
          }else if(pubBandoATC && esisteUrlPubblicazione && esisteUserPubblicazione && esisteTokenPubblicazione){
            List listaStruttureATC = this.getListaStuttureATC();
            page.setAttribute("listaStruttureATC", listaStruttureATC, PageContext.REQUEST_SCOPE);
          }
         }
      }catch(GestoreException e){
        throw new JspException("Errore il controllo dei campi obbligatori ", e);
      }




      if("NO".equals(controlloSuperato)){
        page.setAttribute("controlloSuperato", controlloSuperato, PageContext.REQUEST_SCOPE);
        page.setAttribute("msg", messaggio, PageContext.REQUEST_SCOPE);
      }else if(("0".equals(bando) || "1".equals(bando) || "3".equals(bando)) && (new Long(1).equals(gentip) || new Long(2).equals(gentip) || new Long(3).equals(gentip) ) ){
        try{
          //Controlli non bloccanti
          messaggio="";

          if("0".equals(bando)){
            if( gentip!= null && (gentip.longValue()==3 || gentip.longValue() == 1)){
              Long lottiNonAggiudicati = (Long)sqlManager.getObject("select count(codgar1) from gare where codgar1=? and ngara!=codgar1 and ditta is null and esineg is null", new Object[]{codgar});
              if(lottiNonAggiudicati!= null && lottiNonAggiudicati.longValue()>0){
                controlloSuperato = "WARNING";
                messaggio += "<br>Alcuni lotti della gara non sono stati aggiudicati.";
              }
            }else{
              //Gara a lotto unico
              String ditta = (String)sqlManager.getObject("select ditta from gare where ngara=?", new Object[]{ngara});
              if(ditta==null || "".equals(ditta)){
                controlloSuperato = "WARNING";
                messaggio += "<br>La gara non è stata aggiudicata.";
              }
            }
          }

          if("1".equals(gartel) && ("1".equals(bando) || "3".equals(bando)  || "0".equals(bando))){
            //Si controlla l'esistenza delle occorrenze di qform se il modulo risulta attivo

            if(moduloQFORMAttivo) {
              String esito[] = this.controlloQform(ngara, codgar, bando, iterga, formularioCompletoAbilitato, nobustamm, modlic, valtec, costofisso, genere);
              if(esito!=null) {
                controlloSuperato = esito[0];
                messaggio += esito[1];
              }
            }

          }

          if("WARNING".equals(controlloSuperato)){
            messaggio = "<br><br><font color='#0000FF'><b>ATTENZIONE:</b>" + messaggio + "</font>";
            page.setAttribute("controlloSuperato", controlloSuperato, PageContext.REQUEST_SCOPE);
            page.setAttribute("msg", messaggio, PageContext.REQUEST_SCOPE);
          }

        }catch (Exception e) {
          throw new JspException("Errore durante la lettura del campo DITTA ", e);
        }
      }
      page.setAttribute("visualizzaDettaglioComunicazione", new Boolean(visualizzaDettaglioComunicazione), PageContext.REQUEST_SCOPE);
      page.setAttribute("iterga", iterga, PageContext.REQUEST_SCOPE);
      page.setAttribute("soloPunteggiTec", costofisso, PageContext.REQUEST_SCOPE);
      page.setAttribute("isRicercaMercatoNegoziata", isRicercaMercatoNegoziata ,PageContext.REQUEST_SCOPE);
    }else if("5".equals(bando)) {
      //if("5".equals(bando) && visualizzaDettaglioComunicazione && "1".equals(gartel)){
      MsgConferma="Confermi l'invio dell'invito alle ditte inserite in gara successivamente alla pubblicazione in area riservata?"
          + "<br>A tali ditte viene inviata una comunicazione secondo i dettagli riportati di seguito.";

      page.setAttribute("MsgConferma", MsgConferma, PageContext.REQUEST_SCOPE);

      messaggio = "<b>Non è possibile procedere con l'invio dell'invito alle ditte inserite in gara successivamente alla pubblicazione in area riservata.</b><br>";

      try {

        String chiaveTmp = ngara;
        if (ngara==null)
          chiaveTmp = codgar;
        String esitoControllo [] = mepaManager.controlloDataConDataAttuale(chiaveTmp, "DTEOFF", "OTEOFF");
        if(esitoControllo[0]=="true"){
          messaggio += "<br>Sono superati i termini di presentazione dell'offerta ";
          page.setAttribute("controlloSuperato", "NO", PageContext.REQUEST_SCOPE);
          page.setAttribute("msg", messaggio, PageContext.REQUEST_SCOPE);
          return;
        }
        boolean delegaInvioMailDocumentaleAbilitata = false;

        boolean delegaInvioDocumentale = false;
        if("1".equals(integrazioneWSDM)){

          String tipoWSDM=null;


          WSDMConfigurazioneOutType configurazione = gestioneWSDMManager.wsdmConfigurazioneLeggi("FASCICOLOPROTOCOLLO",idconfi);
          if (configurazione.isEsito()){
            tipoWSDM = configurazione.getRemotewsdm();
            page.setAttribute("tipoWSDM", tipoWSDM, PageContext.REQUEST_SCOPE);
          }


          valoreWSDM = ConfigManager.getValore("pg.wsdm.invioMailPec."+idconfi);
          if(valoreWSDM!=null && "1".equals(valoreWSDM))
            delegaInvioMailDocumentaleAbilitata=true;

          if(delegaInvioMailDocumentaleAbilitata && ("PALEO".equals(tipoWSDM) || "JIRIDE".equals(tipoWSDM) || "ENGINEERING".equals(tipoWSDM) || "TITULUS".equals(tipoWSDM)
              || "SMAT".equals(tipoWSDM) || "ENGINEERINGDOC".equals(tipoWSDM)))
            delegaInvioDocumentale= true;
        }
        HttpSession session = page.getSession();
        String uffint = (String) session.getAttribute("uffint");
        HashMap gestioneDestinatari = this.gestioneDestinatari(ngara, codgar, genere, pgManager, delegaInvioDocumentale, true);
        if("NO".equals(gestioneDestinatari.get("controlloSuperato"))){
          controlloSuperato = "NO";
          messaggio += (String)gestioneDestinatari.get("messaggio");
        }

        List listaDestinatari = (List)gestioneDestinatari.get("listaDestinatari");
        if(listaDestinatari==null || (listaDestinatari!=null && listaDestinatari.size()==0)) {
          controlloSuperato = "NO";
          messaggio += "<br>Non vi sono ditte inserite a gara in corso.";
        }
        page.setAttribute("listaDestinatari", listaDestinatari, PageContext.REQUEST_SCOPE);

        //Caricamento dei documenti di gruppo=6
        List listaDocumenti = this.getListaDocumenti(genere, ngara, codgar, new Long(6));
        if(listaDocumenti!=null && listaDocumenti.size()>0){
          page.setAttribute("listaDocumenti", listaDocumenti, PageContext.REQUEST_SCOPE);
        }

        //Si controlla che la dimensione totale dei documenti non superi il limite consentito

        String idcfg = null;
        cenint = null;
        //Valorizzazione di IDCFG
        try {
          if(ngara==null || "".equals(ngara) || (genere!=null && genere.longValue()==3)){
            cenint = (String)sqlManager.getObject("select t.cenint from torn t where t.codgar = ?", new Object[]{codgar});
          }else{
            cenint = (String)sqlManager.getObject("select t.cenint from gare g,torn t where g.codgar1 = t.codgar and ngara = ?", new Object[]{ngara});
          }
          cenint = UtilityStringhe.convertiNullInStringaVuota(cenint);
          if(!"".equals(cenint)){
            idcfg = cenint;
          }else{
            idcfg = uffint;
          }
        } catch (SQLException sqle) {
          throw new GestoreException("Errore nella lettura di TORN.CENINT",null, sqle);
        }

        HashMap controlloDimensioneAllegati = this.controlloDimensioneAllegati(listaDocumenti, idcfg, fileAllegatoManager);
        if("NO".equals(controlloDimensioneAllegati.get("controlloSuperato"))){
          controlloSuperato = "NO";
          messaggio += (String)controlloDimensioneAllegati.get("messaggio");
        }
      }catch (Exception sqle) {
        throw new JspException("Errore nei controlli preliminari dell'invio invito a gara in corso", sqle);
      }
        page.setAttribute("visualizzaDettaglioComunicazione", new Boolean(visualizzaDettaglioComunicazione), PageContext.REQUEST_SCOPE);
        page.setAttribute("iterga", iterga, PageContext.REQUEST_SCOPE);
        if("NO".equals(controlloSuperato)){
          page.setAttribute("controlloSuperato", controlloSuperato, PageContext.REQUEST_SCOPE);
          page.setAttribute("msg", messaggio, PageContext.REQUEST_SCOPE);
        }
      }
    //}
  }

  /**
   * Si controllano i dati anagrafici dell'impresa, se non sono presenti ne la mail ne la pec si costruisce il messaggio che avvisa
   * che la ditta non verrà inserita fra i destinatari.
   * Si caricano i dati della ditta nella lista
   * Nel caso di raggruppamento, i dati che vengono passati si riferiscono alla mandataria
   *
   * @param dittao
   * @param codComponente
   * @param nomimo
   * @param nomeComposto
   * @param codfisc
   * @param indimp
   * @param nciimp
   * @param locimp
   * @param codcit
   * @param piva
   * @param saltareControlloPivaNulla
   * @param nazimp
   * @param proimp
   * @param capimp
   * @param pgManager
   * @param hm
   * @param listaDestinatari
   * @param abilitazioneInvioMailDocumentale
   * @return boolean true se una ditta non ha ne mail ne pec valorizzati
   * @throws GestoreException,SQLException
   */
  private HashMap gestioneSoggDest(Soggetto soggetto, boolean saltareControlloPivaNulla,
      PgManager pgManager , HashMap hm, List listaDestinatari, boolean abilitazioneInvioMailDocumentale ) throws GestoreException, SQLException {

    String dittao = soggetto.getDittao();
    String codComponente = soggetto.getCodComponente();
    String nomimo =soggetto.getNomimo();
    String nomeComposto = soggetto.getNomeComposto();
    String codfisc = soggetto.getCodfisc();
    String indimp = soggetto.getIndimp();
    String nciimp = soggetto.getNciimp();
    String locimp = soggetto.getLocimp();
    String codcit = soggetto.getCodcit();
    String piva = soggetto.getPiva();
    Long nazimp = soggetto.getNazimp();
    String proimp = soggetto.getProimp();
    String capimp = soggetto.getCapimp();
    String isgruppoiva = soggetto.getIsgruppoiva();

    if("1".equals(isgruppoiva))
      saltareControlloPivaNulla=false;

    HashMap esitoControlli = new HashMap();
    String style = "<li style=\"list-style-type: disc;margin-left: 30px;\" >";

    StringBuffer bufMail = (StringBuffer)hm.get("mail");
    StringBuffer bufNome = (StringBuffer)hm.get("nome");
    StringBuffer bufCodfisc = (StringBuffer)hm.get("codfisc");
    StringBuffer bufPiva = (StringBuffer)hm.get("piva");
    StringBuffer bufCodfiscValido = (StringBuffer)hm.get("codfiscValido");
    StringBuffer bufPivaValida = (StringBuffer)hm.get("pivaValida");
    StringBuffer bufCodfiscDuplicato = (StringBuffer)hm.get("codfiscDuplicato");
    StringBuffer bufPivaDuplicata = (StringBuffer)hm.get("pivaDuplicata");
    StringBuffer bufMailDuplicata = (StringBuffer)hm.get("mailDuplicata");
    StringBuffer bufPecDuplicata = (StringBuffer)hm.get("pecDuplicata");

    String mailFax[] = new String[4];
    String intestazione="";
    if("".equals(codComponente)){
      mailFax = pgManager.getMailFax(dittao);
      intestazione = nomimo;
    }else{
      mailFax = pgManager.getMailFax(codComponente);
      intestazione = nomeComposto;
    }

    boolean mailValorizzata = true;
    boolean ragsocValorizzata=true;
    boolean codfiscValorizzato=true;
    boolean codfiscValidi=true;
    boolean pivaValorizzata=true;
    boolean pivaValide=true;
    boolean codfiscUnico=true;
    boolean pivaUnica=true;
    boolean mailUnica = true;
    boolean pecUnica = true;
    boolean controllareCodfisc=false;
    boolean controllarePiva=false;
    boolean mandataria= false;
    if(codComponente!=null && !"".equals(codComponente))
      mandataria= true;

    //Controllo sulla mail
    String email = mailFax[0];
    String Pec = mailFax[1];
    email = UtilityStringhe.convertiNullInStringaVuota(email);
    Pec = UtilityStringhe.convertiNullInStringaVuota(Pec);
    String desmail="";
    String tipo="";

    if (("".equals(email) && "".equals(Pec) && !abilitazioneInvioMailDocumentale) || (abilitazioneInvioMailDocumentale && "".equals(Pec))) {
      // La ditta non va inserita fra i soggetti destinatari
      // creare il messaggio da visualizzare a video
      bufMail.append(style);
      bufMail.append(dittao);
      bufMail.append(" - ");
      bufMail.append(intestazione);
      bufMail.append("</li>");
      mailValorizzata = false;
    }else{
      desmail = email;
      tipo = "E-mail";
      if (Pec != null && !"".equals(Pec)) {
        desmail = Pec;
        tipo = "PEC";
      }
      //listaDestinatari.add(((new Object[] {intestazione, desmail, tipo, dittao})));
    }

    boolean italiano=true;
    if(nazimp !=null && nazimp.longValue()!=1)
      italiano=false;

    //Controllo valorizzazione del codice fiscale
    if(codComponente==null || "".equals(codComponente) || mandataria){
      controllareCodfisc=true;
      if(codfisc==null || "".equals(codfisc)){
        codfiscValorizzato=false;
        bufCodfisc.append(style);
        bufCodfisc.append(dittao);
        bufCodfisc.append(" - ");
        if(mandataria)
          bufCodfisc.append(nomeComposto);
        else
          bufCodfisc.append(nomimo);
        bufCodfisc.append("</li>");
      }
    }

    //Controllo valorizzazione partita iva
    if(!saltareControlloPivaNulla && ((codComponente==null || "".equals(codComponente)) || mandataria)){
      controllarePiva=true;
      if(piva==null || "".equals(piva)){
        pivaValorizzata=false;
        bufPiva.append(style);
        bufPiva.append(dittao);
        bufPiva.append(" - ");
        if(mandataria)
          bufPiva.append(nomeComposto);
        else
          bufPiva.append(nomimo);
        bufPiva.append("</li>");
      }
    }else if(saltareControlloPivaNulla && ((codComponente==null || "".equals(codComponente)) || mandataria) && piva!=null && !"".equals(piva)){
      controllarePiva=true;
    }

    String chiaveImpr=dittao;
    if( mandataria)
      chiaveImpr=codComponente;

    //Controllo da verificare solo se ditta non registrata su portale
    Long conteggioWpuser=(Long)this.sqlManager.getObject("select count(iduser) from w_puser where userkey1=? and userent=?", new Object[]{chiaveImpr,"IMPR"});
    if(conteggioWpuser == null || (conteggioWpuser!=null && conteggioWpuser.longValue()==0)){

      //Controllo sulla valorizzazione della ragione sociale
      if(nomimo==null || "".equals(nomimo)){
        ragsocValorizzata = false;
        bufNome.append(style);
        if(!mandataria)
          bufNome.append(dittao);
        else{
          bufNome.append(codComponente);
          bufNome.append(" - ");
          bufNome.append(nomeComposto);
        }
        bufNome.append("</li>");
      }



      //controlli sul codice fiscale
      if(codfiscValorizzato && controllareCodfisc){
        //Controllo validità codice fiscale
        boolean cfValido =true;
        if(italiano){
          if("1234567890".indexOf(codfisc.charAt(0))>=0)
            cfValido = UtilityFiscali.isValidPartitaIVA(codfisc);
          else
            cfValido = UtilityFiscali.isValidCodiceFiscale(codfisc);
        }
        if(!cfValido){
          codfiscValidi = false;
          bufCodfiscValido.append(style);
          bufCodfiscValido.append(dittao);
          bufCodfiscValido.append(" - ");
          if(mandataria)
            bufCodfiscValido.append(nomeComposto);
          else
            bufCodfiscValido.append(nomimo);
          bufCodfiscValido.append("</li>");
        }

        //Controllo unicità codice fiscale
        String selectControlloCodfisc="select count(codimp) from impr where cfimp=? and codimp<>?";
        Long conteggioCodfisc = (Long)this.sqlManager.getObject("select count(codimp) from impr where cfimp=? and codimp<>?", new Object[]{codfisc,chiaveImpr});
        if(conteggioCodfisc!=null && conteggioCodfisc.longValue()>0){
          codfiscUnico=false;
          bufCodfiscDuplicato.append(style);
          bufCodfiscDuplicato.append(dittao);
          bufCodfiscDuplicato.append(" - ");
          if(mandataria)
            bufCodfiscDuplicato.append(nomeComposto);
          else
            bufCodfiscDuplicato.append(nomimo);
          bufCodfiscDuplicato.append("</li>");
        }
      }

      //Controlli sulla partita iva
      if(pivaValorizzata && controllarePiva){
        //Controllo validità partita iva
        if(!UtilityFiscali.isValidPartitaIVA(piva, italiano)){
          pivaValide = false;
          bufPivaValida.append(style);
          bufPivaValida.append(dittao);
          bufPivaValida.append(" - ");
          if(mandataria)
            bufPivaValida.append(nomeComposto);
          else
            bufPivaValida.append(nomimo);
          bufPivaValida.append("</li>");
        }

        //Controllo unicità partita iva
        if(!"1".equals(isgruppoiva)) {
          Long conteggioPiva = (Long)this.sqlManager.getObject("select count(codimp) from impr where pivimp=? and codimp<>?", new Object[]{piva,chiaveImpr});
          if(conteggioPiva!=null && conteggioPiva.longValue()>0){
            pivaUnica=false;
            bufPivaDuplicata.append(style);
            bufPivaDuplicata.append(dittao);
            bufPivaDuplicata.append(" - ");
            if(mandataria)
              bufPivaDuplicata.append(nomeComposto);
            else
              bufPivaDuplicata.append(nomimo);
            bufPivaDuplicata.append("</li>");
          }
        }
      }


      if(mailValorizzata){
      //Controllo unicità mail
        Long conteggioMail = (Long)this.sqlManager.getObject("select count(codimp) from impr where emaiip=? and codimp<>?", new Object[]{email,chiaveImpr});
        if(conteggioMail!=null && conteggioMail.longValue()>0){
          mailUnica=false;
          bufMailDuplicata.append(style);
          bufMailDuplicata.append(dittao);
          bufMailDuplicata.append(" - ");
          if(mandataria)
            bufMailDuplicata.append(nomeComposto);
          else
            bufMailDuplicata.append(nomimo);
          bufMailDuplicata.append("</li>");
        }

        Long conteggioPec = (Long)this.sqlManager.getObject("select count(codimp) from impr where emai2ip=? and codimp<>?", new Object[]{Pec,chiaveImpr});
        if(conteggioPec!=null && conteggioPec.longValue()>0){
          pecUnica=false;
          bufPecDuplicata.append(style);
          bufPecDuplicata.append(dittao);
          bufPecDuplicata.append(" - ");
          if(mandataria)
            bufPecDuplicata.append(nomeComposto);
          else
            bufPecDuplicata.append(nomimo);
          bufPecDuplicata.append("</li>");
        }
      }
    }

    listaDestinatari.add(((new Object[] {intestazione, desmail, tipo, dittao, codfisc, indimp, nciimp, locimp, codcit,piva, email, proimp, capimp})));
    hm.put("mail", bufMail);
    hm.put("nome", bufNome);
    hm.put("codfisc", bufCodfisc);
    hm.put("piva", bufPiva);
    hm.put("codfiscValido", bufCodfiscValido);
    hm.put("pivaValida", bufPivaValida);
    hm.put("codfiscDuplicato", bufCodfiscDuplicato);
    hm.put("pivaDuplicata", bufPivaDuplicata);
    hm.put("mailDuplicata", bufMailDuplicata);
    hm.put("pecDuplicata", bufPecDuplicata);

    esitoControlli.put("mailValorizzata", new Boolean(mailValorizzata));
    esitoControlli.put("ragsocValorizzata", new Boolean(ragsocValorizzata));
    esitoControlli.put("codfiscValorizzato", new Boolean(codfiscValorizzato));
    esitoControlli.put("codfiscValidi", new Boolean(codfiscValidi));
    esitoControlli.put("pivaValorizzata", new Boolean(pivaValorizzata));
    esitoControlli.put("pivaValide", new Boolean(pivaValide));
    esitoControlli.put("codfiscUnico", new Boolean(codfiscUnico));
    esitoControlli.put("pivaUnica", new Boolean(pivaUnica));
    esitoControlli.put("mailUnica", new Boolean(mailUnica));
    esitoControlli.put("pecUnica", new Boolean(pecUnica));

    return esitoControlli;
  }

  /**
   * Si controlla che siano stati valorizzati i criteri tecnici e che il punteggio totale sia 100.
   * Si verifica inoltre se esistono i criteri economici.
   * Ritorna un hashmap con i seguenti valori
   * "msgRitorno",  il messaggio di controllo non superato;
   * "costofissoTot",  che vale 'true' nel caso di gara a lotti se tutti i lotti hanno costofisso=1, negli altri casi se costofisso della gara=1
   *
   * @param ngara
   * @param codgar
   * @param genere
   * @return HashMap<String,String> contenente contenuto l'eventuale messaggio per il controllo non superto,
   *                  il risultato sul controllo sull'esistenza dei criteri economici e se è abilitato sezionitec per la gara
   * @throws GestoreException,SQLException
   */

  private HashMap<String,String> controlloPunteggi(String ngara, String codgar, Long genere, Long offtel) throws SQLException, GestoreException{
    HashMap<String, String> ret = new HashMap<String, String>();
    String msgRitorno = "";
    String costofissoRet = "false";
    String sezionitecRet = "true";
    String select = "select g.modlicg,g.ngara,g1.costofisso, g1.sezionitec from gare g, gare1 g1 where g.ngara=? and g.ngara=g1.ngara";
    Object parametri[] = {ngara};
    if (genere != null && genere.longValue() == 3) {
      select = "select g.modlicg, g.ngara, g1.costofisso, g1.sezionitec from gare g, gare1 g1 where g.codgar1=? and g.ngara = g1.ngara and g.ngara!=g.codgar1 order by g.ngara";
      parametri[0] = codgar;
    }
    List<?> listaModlicg = sqlManager.getListVector(select, parametri);
    if (listaModlicg != null && listaModlicg.size() > 0) {
      Long modlicg = null;
      String codiceLotto = null;
      Long riptec = null;
      Long ripeco = null;
      Long ripcritec = null;
      Long ripcrieco = null;
      boolean soglieMinimeImpostate = false;
      String costofisso = null;
      boolean costofissoTmp = true;
      boolean sezionitecTmp = false;
      String sezionitec = null;
      String selectSeztec = "select count(necvan) from goev where ngara=? and tippar=1 and (livpar=1 or livpar=3) and seztec is null";
      for (int i = 0; i < listaModlicg.size(); i++) {
        modlicg = SqlManager.getValueFromVectorParam(listaModlicg.get(i), 0).longValue();
        codiceLotto = SqlManager.getValueFromVectorParam(listaModlicg.get(i), 1).getStringValue();
        costofisso = SqlManager.getValueFromVectorParam(listaModlicg.get(i), 2).getStringValue();
        sezionitec = SqlManager.getValueFromVectorParam(listaModlicg.get(i), 3).getStringValue();
        if (modlicg != null && modlicg.longValue() == 6) {
          soglieMinimeImpostate = false;

          Double maxPunTecnico = pgManager.getSommaPunteggioTecnico(codiceLotto);
          Double maxPunEconomico = pgManager.getSommaPunteggioEconomico(codiceLotto);

          if(maxPunTecnico== null){
            if(genere!=null && genere.longValue()==3){
              msgRitorno += "<br>Non sono stati specificati i criteri di valutazione tecnici del lotto " + codiceLotto + " ed i relativi punteggi massimi.";
            }else{
              msgRitorno += "<br>Non sono stati specificati i criteri di valutazione tecnici della gara ed i relativi punteggi massimi.";
            }
          }

          if(!"1".equals(costofisso)){
            //Se sono presenti le occorrenze dei punteggi economici, si deve controllare che siano presenti i relativi punteggi
            if(maxPunEconomico==null){
              if(genere!=null && genere.longValue()==3){
                msgRitorno += "<br>Non sono stati specificati i criteri di valutazione economici del lotto " + codiceLotto + " ed i relativi punteggi massimi.";
              }else{
                msgRitorno += "<br>Non sono stati specificati i criteri di valutazione economici della gara ed i relativi punteggi massimi.";
              }
            }
            costofissoTmp=false;
          }
          if(maxPunTecnico!=null) {
            BigDecimal punteggioTot = BigDecimal.valueOf(maxPunTecnico);
            if(!"1".equals(costofisso) && maxPunEconomico!=null){
              BigDecimal bigMaxpunEco= BigDecimal.valueOf(maxPunEconomico);
              punteggioTot = punteggioTot.add(bigMaxpunEco);
            }
            if(new BigDecimal(100).compareTo(punteggioTot)!=0){
              if(genere!=null && genere.longValue()==3){
                msgRitorno += "<br>La somma dei punteggi massimi dei criteri di valutazione del lotto " + codiceLotto + " non è 100.";
              }else{
                msgRitorno += "<br>La somma dei punteggi massimi dei criteri di valutazione non è 100.";
              }
            }
          }
          if(offtel == 1 && !"1".equals(costofisso)){
            List<?> listaCriteriEconomici;
            listaCriteriEconomici = sqlManager.getListVector("select g1cridef.formato from g1cridef, goev where g1cridef.ngara = goev.ngara and goev.tippar = 2  and g1cridef.ngara = ? and g1cridef.necvan = goev.necvan", new Object[]{codiceLotto});
            if(listaCriteriEconomici!=null && listaCriteriEconomici.size()>0){
              boolean trovatoFormatoDefinito = false;
              for(int n=0;n<listaCriteriEconomici.size();n++){
                Long formato =  (Long) SqlManager.getValueFromVectorParam(listaCriteriEconomici.get(n), 0).getValue();
                if(formato != null && formato != 100){trovatoFormatoDefinito = true;}
              }
              if(!trovatoFormatoDefinito){
                if(genere!=null && genere.longValue()==3){
                  msgRitorno += "<br>Non è stato specificato nessun criterio di valutazione economico per il lotto " + codiceLotto + " con formato diverso da 'Non definito'.";
                }else{
                  msgRitorno += "<br>Non è stato specificato nessun criterio di valutazione economico con formato diverso da 'Non definito'.";
                }
              }
            }
            Long ultdetlic = (Long)sqlManager.getObject("select ultdetlic from gare1 where ngara=?", new Object[]{codiceLotto});
            if(ultdetlic != null){
              if(!this.controlliOepvManager.checkFormato(codiceLotto, new Long(50)) && !this.controlliOepvManager.checkFormato(codiceLotto, new Long(52))){
                if(genere!=null && genere.longValue()==3){
                  msgRitorno += "<br>Non è stato specificato nessun criterio di valutazione economico per il lotto " + codiceLotto + " con formato 'Offerta complessiva espressa mediante importo o prezzi unitari', necessario nel caso di offerta congiunta.";
                }else{
                  msgRitorno += "<br>Non è stato specificato nessun criterio di valutazione economico con formato 'Offerta complessiva espressa mediante importo o prezzi unitari', necessario nel caso di offerta congiunta.";
                }
              }
            }
          }

          //se RIPTEC, RIPECO.GARE1 = 2 & RIPCRITEC, RIPCRIECO.GARE1 = 1 non ci devono essere occorrenze in GOEV con MINPUN valorizzato
          Vector<?> datiGare1 = sqlManager.getVector("select riptec, ripeco, ripcritec, ripcrieco from gare1 where ngara=?", new Object[]{codiceLotto});
          if(datiGare1!=null && datiGare1.size()>0){
            riptec = SqlManager.getValueFromVectorParam(datiGare1, 0).longValue();
            ripeco = SqlManager.getValueFromVectorParam(datiGare1, 1).longValue();
            ripcritec = SqlManager.getValueFromVectorParam(datiGare1, 2).longValue();
            ripcrieco = SqlManager.getValueFromVectorParam(datiGare1, 3).longValue();
            String tipoCriteri= "";
            if(new Long(2).equals(riptec) && new Long(1).equals(ripcritec)){
              Long numSoglieMinImpostate = (Long)sqlManager.getObject("select count(ngara) from goev where ngara=? and tippar=? and minpun is not null", new Object[]{codiceLotto, new Long(1)});
              if(numSoglieMinImpostate!=null && numSoglieMinImpostate.longValue()>0){
                soglieMinimeImpostate=true;
                tipoCriteri+="tecnici";
              }
            }
            if(new Long(2).equals(ripeco) && new Long(1).equals(ripcrieco)){
              Long numSoglieMinImpostate = (Long)sqlManager.getObject("select count(ngara) from goev where ngara=? and tippar=? and minpun is not null", new Object[]{codiceLotto, new Long(2)});
              if(numSoglieMinImpostate!=null && numSoglieMinImpostate.longValue()>0){
                soglieMinimeImpostate=true;
                if(!"".equals(tipoCriteri))
                  tipoCriteri+= " ed ";
                tipoCriteri+="economici";
              }
            }
            if(soglieMinimeImpostate){
              if(genere!=null && genere.longValue()==3){
                msgRitorno += "<br>La definizione dei criteri di valutazione " + tipoCriteri+ " del lotto " + codiceLotto + " presenta un'incoerenza: non si possono impostare soglie minime sui singoli criteri se prevista la riparametrazione sui soli punteggi totali da attivare prima del controllo sulla soglia minima.";
              }else{
                msgRitorno += "<br>La definizione dei criteri di valutazione " + tipoCriteri+ " presenta un'incoerenza: non si possono impostare soglie minime sui singoli criteri se prevista la riparametrazione sui soli punteggi totali da attivare prima del controllo sulla soglia minima.";
              }
            }
          }

          //Si deve fare il controllo per i soli criteri economici che non siano tutti di tipo 'altri elementi' (ISNOPRZ.GOEV= '1'), solo per LIVPAR.GOEV = 1,3
          if(!"1".equals(costofisso) && !pgManagerEst1.esistonoCriteriEconomiciPrezzo(codiceLotto)) {
            if (genere != null && genere.longValue() == 3) {
              msgRitorno += "<br>Non è stato specificato nessun criterio di valutazione economico per il lotto " + codiceLotto + " che, ai fini del calcolo soglia anomalia, sia relativo al prezzo.";
            } else {
              msgRitorno += "<br>Non è stato specificato nessun criterio di valutazione economico che, ai fini del calcolo soglia anomalia, sia relativo al prezzo.";
            }
          }
          //gestione delle sezioni qualitativa/quantitativa della busta tecnica
          if ("1".equals(sezionitec)) {
            sezionitecTmp = true;
            Long conteggio = (Long) this.sqlManager.getObject(selectSeztec, new Object[]{codiceLotto});
            if (conteggio != null && conteggio.longValue() > 0) {
              msgRitorno += "<br>Non è stata specificata la sezione, qualitativa o quantitativa, in tutti i criteri di valutazione tecnici";
              if (genere != null && genere.longValue() == 3) {
                msgRitorno += " del lotto " + codiceLotto;
              }
              msgRitorno += ".";
            }
          }
        } else {
          costofissoTmp = false;
        }
      }
      if(costofissoTmp)
        costofissoRet = "true";

      if(!sezionitecTmp)
        sezionitecRet = "false";
    }
    if("".equals(msgRitorno))
      msgRitorno = null;

    ret.put("msgRitorno", msgRitorno);
    ret.put("costofissoTot", costofissoRet);
    ret.put("sezionitecTot", sezionitecRet);
    return ret;
  }

  /**
   * Viene eseguito il controllo che per tutte le lavorazioni della gara/lotto siano valorizzati
   * i campi GCAP.CODVOC,GCAP.UNIMIS e GCAP.QUANTI e che GCAP.QUANTI>0
   *
   * @param gara codice della gara/lotto
   * @param lotto (true se si tratta di un lotto)
   * @param ribassoPesato (true se si devono fare i controlli per ribasso pesato)
   * @param accqua
   * @param A1162
   * @return String[] String[0] esito ("SI" o "NO")
   *                  String[1] messaggio dell'errore
   */
  private String[] controlloDatiGcap(String gara, boolean lotto, boolean ribassoPesato, String accqua, String A1162) throws SQLException, GestoreException{
    String controlloSuperato = "SI";
    String msg ="";
    String[] controlloCampi={"(codvoc is null or codvoc='')","(unimis is null or unimis='')","(quanti is null or quanti = 0)","prezun is null","peso is null and sogrib <> '1'"};
    String[] esitoCampi={"Non è stato specificato il codice lavorazione","Non è stata specificata l'unità di misura","Non è stata specificata la quantità",
        "Non è stato specificato il prezzo unitario","Non è stato specificato il peso"};
    int len = 3;
    if(ribassoPesato)
      len = 5;
    for(int i=0;i<len;i++){
	    String select ="select count(*) from gcap where ngara=? and dittao is null and " + controlloCampi[i];
	    Long conteggioGcap = (Long)this.sqlManager.getObject(select, new Object[]{gara});
	    if(conteggioGcap!=null && conteggioGcap.longValue()>0){
	        controlloSuperato = "NO";
	        msg +=  "<br>" + esitoCampi[i] + " per tutte le lavorazioni e forniture";
	        if(lotto)
	          msg +=  " del lotto " + gara ;
	        if(i==2){
	        	msg += " (specificare un valore diverso da 0)";
	        }
	        msg+=".";
	    }
    }

    if(controlloSuperato == "SI" && "1".equals(A1162)){
      String select="select distinct CODVOC from gcap where  ngara=? group by codvoc having count(codvoc)>1";
      Vector codvocDuplicati = this.sqlManager.getVector(select, new Object[]{gara});
      if(codvocDuplicati!=null && codvocDuplicati.size()>0){
        controlloSuperato = "NO";
        msg +=  "<br>Sono presenti lavorazioni e forniture con uguale codice";
        if(lotto)
          msg +=  " nel lotto " + gara ;
        msg+=".";
      }
    }

    if(controlloSuperato == "SI" && ribassoPesato){
      //Si deve controllare che l'importo totale soggetto a ribasso derivante dai prezzi unitari sia pari a quello indicato nella gara
      //il controllo va eseguito se accqua <> '1'
      if(!"1".equals(accqua)){
        Object importoSoggRibassoObj = sqlManager.getObject("select sum(quanti * prezun) from gcap where ngara=? and dittao is null and sogrib='2'", new Object[]{gara});
        Double importoSoggRibasso= pgManagerEst1.getImportoDaObject(importoSoggRibassoObj);
        Vector datiGare = sqlManager.getVector("select onsogrib,impapp - coalesce(impsic,0) - coalesce(impnrl,0), impapp - coalesce(impnrl,0) - coalesce(impsic,0) -  coalesce(onprge,0) from gare where ngara=?", new Object[]{gara});
        Double importoGara= new Double(0);
        if(datiGare!=null && datiGare.size()>0){
          String onsogrib = SqlManager.getValueFromVectorParam(datiGare, 0).getStringValue();
          if("1".equals(onsogrib))
            importoGara = SqlManager.getValueFromVectorParam(datiGare, 1).doubleValue();
          else
            importoGara = SqlManager.getValueFromVectorParam(datiGare, 2).doubleValue();
        }
        //Object importoGaraObj = sqlManager.getObject("select impapp - coalesce(impsic,0) - coalesce(impnrl,0) from gare where ngara=?", new Object[]{gara});

        //if(importoGaraObj!=null)
        //  importoGara= pgManagerEst1.getImportoDaObject(importoGaraObj);
        if(importoGara == null || UtilityNumeri.confrontaDouble(importoSoggRibasso.doubleValue(), importoGara.doubleValue(), 2) != 0){
          controlloSuperato = "NO";
          if(lotto)
            msg +=  "<br>L'importo totale soggetto a ribasso derivante dai prezzi unitari delle lavorazioni e forniture del lotto " + gara + " non è pari a quello posto a base di gara.";
          else
            msg +=  "<br>L'importo totale soggetto a ribasso derivante dai prezzi unitari delle lavorazioni e forniture non è pari a quello posto a base di gara.";

        }
      }

      //La somma dei pesi di tutte le lavorazioni sia pari a 100
      Object pesoObj = sqlManager.getObject("select sum(coalesce(peso,0)) from gcap where ngara=? and dittao is null", new Object[]{gara});
      Double peso= pgManagerEst1.getImportoDaObject(pesoObj);
      if(UtilityNumeri.confrontaDouble(peso.doubleValue(), new Double(100), 1) != 0){
        controlloSuperato = "NO";
        if(lotto){
          msg +=  "<br>La somma dei pesi delle lavorazioni e forniture del lotto " + gara + " non è 100.";
        }else{
          msg +=  "<br>La somma dei pesi delle lavorazioni e forniture non è 100.";
        }
      }

    }
    return new String[]{controlloSuperato,msg};
  }

  @SuppressWarnings("rawtypes")
  public List getListaDocumenti(Long genere, String ngara, String codgar, Long gruppo) throws SQLException{
    String select="select descrizione, dignomdoc, d.IDPRG, d.IDDOCDG from DOCUMGARA d,W_DOCDIG w where CODGAR=? ";
    if( genere== null || genere.longValue()!=3)
      select+=" and NGARA = '"+ ngara + "'";
    select+=" and GRUPPO = ? and d.IDPRG=w.IDPRG and d.IDDOCDG = w.IDDOCDIG and allmail=? order by numord,norddocg";
    List listaDocumenti = sqlManager.getListVector(select, new Object[]{codgar,gruppo ,"1"});
    return listaDocumenti;
  }

  public HashMap gestioneDestinatari(String ngara, String codgar, Long genere, PgManager pgManager, boolean delegaInvioDocumentale, boolean garaPubblicata) throws SQLException, GestoreException{

    HashMap risultato =  new HashMap();
    String controlloSuperato ="SI";
    String messaggio = "";

    if( genere!= null&& genere.longValue()==3)
      ngara=codgar;
    String select="select dittao,nomimp,tipimp,cfimp,indimp,nciimp,locimp,codcit,pivimp,nazimp,proimp,capimp,isgruppoiva from ditg,impr where codgar5=? and ngara5=? and dittao = codimp ";
    if(!garaPubblicata)
      select += "and (ammgar='1' or ammgar is null)";
    else
      select += "and ammgar='2' and acquisizione=9";
    List listaDittao = sqlManager.getListVector(select, new Object[]{codgar,ngara});
    List listaDestinatari = new Vector();
    if(listaDittao!=null && listaDittao.size()>0){
      HashMap hashMapBufferMessaggi =  new HashMap();
      HashMap hashMapEsitoControlli =  new HashMap();
      StringBuffer bufMail = new StringBuffer("<br><ul>");
      StringBuffer bufPresenzaNome = new StringBuffer("<br><ul>");
      StringBuffer bufCodfisc = new StringBuffer("<br><ul>");
      StringBuffer bufPiva = new StringBuffer("<br><ul>");
      StringBuffer bufCodfiscValido = new StringBuffer("<br><ul>");
      StringBuffer bufPivaValida = new StringBuffer("<br><ul>");
      StringBuffer bufCodfiscDuplicato = new StringBuffer("<br><ul>");
      StringBuffer bufPivaDuplicata = new StringBuffer("<br><ul>");
      StringBuffer bufMailDuplicata = new StringBuffer("<br><ul>");
      StringBuffer bufPecDuplicata = new StringBuffer("<br><ul>");
      StringBuffer bufMandatariaPresente = new StringBuffer("<br><ul>");

      hashMapBufferMessaggi.put("mail", bufMail);
      hashMapBufferMessaggi.put("nome", bufPresenzaNome);
      hashMapBufferMessaggi.put("codfisc", bufCodfisc);
      hashMapBufferMessaggi.put("piva", bufPiva);
      hashMapBufferMessaggi.put("codfiscValido", bufCodfiscValido);
      hashMapBufferMessaggi.put("pivaValida", bufPivaValida);
      hashMapBufferMessaggi.put("codfiscDuplicato", bufCodfiscDuplicato);
      hashMapBufferMessaggi.put("pivaDuplicata", bufPivaDuplicata);
      hashMapBufferMessaggi.put("mailDuplicata", bufMailDuplicata);
      hashMapBufferMessaggi.put("pecDuplicata", bufPecDuplicata);


      String descTabellatoTipo1 = tabellatiManager.getDescrTabellato("G_045", "1");
      String descTabellatoTipo2 = tabellatiManager.getDescrTabellato("G_045", "2");
      if (descTabellatoTipo1 != null && descTabellatoTipo1.length()>1)
        descTabellatoTipo1 = descTabellatoTipo1.substring(0, 1);
      if (descTabellatoTipo2 != null && descTabellatoTipo2.length()>1)
        descTabellatoTipo2 = descTabellatoTipo2.substring(0, 1);


      String dittao = null;
      String nomimp = null;
      Long tipologiaImpresa =null;
      String codfisc =  null;
      String indimp = null;
      String nciimp = null;
      String locimp = null;
      String codcit = null;
      String piva = null;
      Long nazimp = null;
      String proimp = null;
      String capimp=null;
      String isgruppoiva=null;

      boolean controlloPresenzaMailSupertato=true;
      boolean ragioneSocialePresente=true;
      boolean controlloPresenzaMandatarie=true;
      boolean controlloCodfiscValorizzatoSuperato=true;
      boolean controlloPivaValorizzataSuperato=true;
      boolean controlloCodfiscValidiSuperato=true;
      boolean controlloPivaValideSuperato=true;
      boolean controlloCodfiscUnicoSuperato=true;
      boolean controlloPivaUnicaSuperato=true;
      boolean controlloMailUnicaSuperato=true;
      boolean controlloPecUnicaSuperato=true;
      boolean saltareControlloPivaNulla = false;
      Soggetto soggetto=null;

      boolean bt=false;
      for(int i=0;i<listaDittao.size();i++){
        dittao = SqlManager.getValueFromVectorParam(listaDittao.get(i), 0).stringValue();
        nomimp = SqlManager.getValueFromVectorParam(listaDittao.get(i), 1).stringValue();
        tipologiaImpresa = SqlManager.getValueFromVectorParam(listaDittao.get(i), 2).longValue();
        codfisc =  SqlManager.getValueFromVectorParam(listaDittao.get(i), 3).getStringValue();
        indimp = SqlManager.getValueFromVectorParam(listaDittao.get(i), 4).stringValue();
        nciimp = SqlManager.getValueFromVectorParam(listaDittao.get(i), 5).stringValue();
        locimp = SqlManager.getValueFromVectorParam(listaDittao.get(i), 6).stringValue();
        codcit = SqlManager.getValueFromVectorParam(listaDittao.get(i), 7).stringValue();
        piva = SqlManager.getValueFromVectorParam(listaDittao.get(i), 8).stringValue();
        nazimp = SqlManager.getValueFromVectorParam(listaDittao.get(i), 9).longValue();
        proimp = SqlManager.getValueFromVectorParam(listaDittao.get(i), 10).stringValue();
        capimp = SqlManager.getValueFromVectorParam(listaDittao.get(i), 11).stringValue();
        isgruppoiva = SqlManager.getValueFromVectorParam(listaDittao.get(i), 12).stringValue();

        if(tipologiaImpresa==null)
          tipologiaImpresa= new Long(0);
        if(nomimp == null)
          nomimp ="";
        saltareControlloPivaNulla = false;


        //if ("3".equals(tipologiaImpresa) || "10".equals(tipologiaImpresa)) {
        if (tipologiaImpresa.longValue()==3 || tipologiaImpresa.longValue() == 10) {
          //int numMandatarie=0;
          String selectComponenti= "select CODDIC, nomimp, tipimp, CFIMP, indimp,nciimp,locimp,codcit,pivimp,nazimp,proimp,capimp,isgruppoiva  from RAGIMP,IMPR where CODIME9 = ? and CODDIC=CODIMP and IMPMAN='1'";
          List listaComponenti = sqlManager.getListVector(selectComponenti,new Object[]{ dittao });
          //Si deve controllare la ragione sociale anche del raggruppamento
          if(nomimp==null || "".equals(nomimp)){
            bufPresenzaNome.append("<li style=\"list-style-type: disc;margin-left: 30px;\" >");
            bufPresenzaNome.append(dittao);
            ragioneSocialePresente=false;
          }
          if (listaComponenti != null && listaComponenti.size() == 1){
            for (int k = 0; k< listaComponenti.size(); k++) {
              String codComponente = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 0).getStringValue();
              String nomeComponente = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 1).getStringValue();
              Long tipimpMan = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 2).longValue();
              codfisc =  SqlManager.getValueFromVectorParam(listaComponenti.get(k), 3).getStringValue();
              indimp =  SqlManager.getValueFromVectorParam(listaComponenti.get(k), 4).getStringValue();
              nciimp =  SqlManager.getValueFromVectorParam(listaComponenti.get(k), 5).getStringValue();
              locimp =  SqlManager.getValueFromVectorParam(listaComponenti.get(k), 6).getStringValue();
              codcit =  SqlManager.getValueFromVectorParam(listaComponenti.get(k), 7).getStringValue();
              piva = SqlManager.getValueFromVectorParam(listaComponenti.get(k), 8).getStringValue();
              nazimp = SqlManager.getValueFromVectorParam(listaComponenti.get(k), 9).longValue();
              proimp = SqlManager.getValueFromVectorParam(listaComponenti.get(k), 10).stringValue();
              capimp = SqlManager.getValueFromVectorParam(listaComponenti.get(k), 11).stringValue();
              isgruppoiva = SqlManager.getValueFromVectorParam(listaComponenti.get(k), 12).stringValue();

              saltareControlloPivaNulla=this.saltareControlloObbligPiva(tipimpMan, descTabellatoTipo1, descTabellatoTipo2);

              String nomeComposto = nomimp+" - "+nomeComponente+" - Mandataria";
              //numMandatarie += 1;
              soggetto = new Soggetto();
              soggetto.setDittao(dittao);
              soggetto.setCodComponente(codComponente);
              soggetto.setNomimo(nomeComponente);
              soggetto.setNomeComposto(nomeComposto);
              soggetto.setCodfisc(codfisc);
              soggetto.setIndimp(indimp);
              soggetto.setNciimp(nciimp);
              soggetto.setLocimp(locimp);
              soggetto.setCodcit(codcit);
              soggetto.setPiva(piva);
              soggetto.setNazimp(nazimp);
              soggetto.setProimp(proimp);
              soggetto.setCapimp(capimp);
              soggetto.setIsgruppoiva(isgruppoiva);
              hashMapEsitoControlli=gestioneSoggDest(soggetto, saltareControlloPivaNulla, pgManager ,hashMapBufferMessaggi, listaDestinatari,delegaInvioDocumentale  );
              bt=((Boolean)hashMapEsitoControlli.get("mailValorizzata")).booleanValue();
              if(!bt)
                controlloPresenzaMailSupertato=false;
              bt=((Boolean)hashMapEsitoControlli.get("ragsocValorizzata")).booleanValue();
              if(!bt)
                ragioneSocialePresente=false;
              bt=((Boolean)hashMapEsitoControlli.get("codfiscValorizzato")).booleanValue();
              if(!bt)
                controlloCodfiscValorizzatoSuperato=false;
              bt=((Boolean)hashMapEsitoControlli.get("codfiscValidi")).booleanValue();
              if(!bt)
                controlloCodfiscValidiSuperato=false;
              bt=((Boolean)hashMapEsitoControlli.get("pivaValorizzata")).booleanValue();
              if(!bt)
                controlloPivaValorizzataSuperato=false;
              bt=((Boolean)hashMapEsitoControlli.get("pivaValide")).booleanValue();
              if(!bt)
                controlloPivaValideSuperato=false;
              bt=((Boolean)hashMapEsitoControlli.get("codfiscUnico")).booleanValue();
              if(!bt)
                controlloCodfiscUnicoSuperato=false;
              bt=((Boolean)hashMapEsitoControlli.get("pivaUnica")).booleanValue();
              if(!bt)
                controlloPivaUnicaSuperato=false;
              bt=((Boolean)hashMapEsitoControlli.get("mailUnica")).booleanValue();
              if(!bt)
                controlloMailUnicaSuperato=false;
              bt=((Boolean)hashMapEsitoControlli.get("pecUnica")).booleanValue();
              if(!bt)
                controlloPecUnicaSuperato=false;

              /*
              else{
                suffisso = "Mandante";
              }*/

              //per ogni ditta componente
              //if(gestioneSoggDest(dittao ,codComponente,nomeComponente ,nomeComposto, flagMan, codfisc, indimp, nciimp, locimp, codcit, piva, saltareControlloPivaNulla, nazimp, pgManager ,hashMapBufferMessaggi, listaDestinatari,delegaInvioDocumentale  ))

            }
          }else{
          //Controllo sulla presenza della mandataria
            //if(numMandatarie==0 || numMandatarie>1){
            bufMandatariaPresente.append("<li style=\"list-style-type: disc;margin-left: 30px;\" >");
            bufMandatariaPresente.append(dittao);
            bufMandatariaPresente.append(" - ");
            bufMandatariaPresente.append(nomimp);
            bufMandatariaPresente.append("</li>");
            controlloPresenzaMandatarie=false;
          }
        }else{
           saltareControlloPivaNulla = this.saltareControlloObbligPiva(tipologiaImpresa, descTabellatoTipo1, descTabellatoTipo2);

          //per le ditte singole
          //if( gestioneSoggDest(dittao ,"" ,nomimp ,"", "", codfisc, indimp, nciimp, locimp, codcit, piva, saltareControlloPivaNulla, nazimp, pgManager ,hashMapBufferMessaggi, listaDestinatari, delegaInvioDocumentale  ))
          soggetto = new Soggetto();
          soggetto.setDittao(dittao);
          soggetto.setCodComponente("");
          soggetto.setNomimo(nomimp);
          soggetto.setNomeComposto("");
          soggetto.setCodfisc(codfisc);
          soggetto.setIndimp(indimp);
          soggetto.setNciimp(nciimp);
          soggetto.setLocimp(locimp);
          soggetto.setCodcit(codcit);
          soggetto.setPiva(piva);
          soggetto.setNazimp(nazimp);
          soggetto.setProimp(proimp);
          soggetto.setCapimp(capimp);
          soggetto.setIsgruppoiva(isgruppoiva);

          hashMapEsitoControlli=gestioneSoggDest(soggetto, saltareControlloPivaNulla,  pgManager ,hashMapBufferMessaggi, listaDestinatari, delegaInvioDocumentale );
          bt=((Boolean)hashMapEsitoControlli.get("mailValorizzata")).booleanValue();
          if(!bt)
            controlloPresenzaMailSupertato=false;
          bt=((Boolean)hashMapEsitoControlli.get("ragsocValorizzata")).booleanValue();
          if(!bt)
            ragioneSocialePresente=false;
          bt=((Boolean)hashMapEsitoControlli.get("codfiscValorizzato")).booleanValue();
          if(!bt)
            controlloCodfiscValorizzatoSuperato=false;
          bt=((Boolean)hashMapEsitoControlli.get("codfiscValidi")).booleanValue();
          if(!bt)
            controlloCodfiscValidiSuperato=false;
          bt=((Boolean)hashMapEsitoControlli.get("pivaValorizzata")).booleanValue();
          if(!bt)
            controlloPivaValorizzataSuperato=false;
          bt=((Boolean)hashMapEsitoControlli.get("pivaValide")).booleanValue();
          if(!bt)
            controlloPivaValideSuperato=false;
          bt=((Boolean)hashMapEsitoControlli.get("codfiscUnico")).booleanValue();
          if(!bt)
            controlloCodfiscUnicoSuperato=false;
          bt=((Boolean)hashMapEsitoControlli.get("pivaUnica")).booleanValue();
          if(!bt)
            controlloPivaUnicaSuperato=false;
          bt=((Boolean)hashMapEsitoControlli.get("mailUnica")).booleanValue();
          if(!bt)
            controlloMailUnicaSuperato=false;
          bt=((Boolean)hashMapEsitoControlli.get("pecUnica")).booleanValue();
          if(!bt)
            controlloPecUnicaSuperato=false;
        }
      }
      if(!controlloPresenzaMailSupertato){
        bufMail.append("</ul>");
        controlloSuperato = "NO";
        if(!delegaInvioDocumentale)
          messaggio += "<br>Le seguenti ditte non hanno un indirizzo PEC o E-mail specificato in anagrafica:" + bufMail.toString();
        else
          messaggio += "<br>Le seguenti ditte non hanno un indirizzo PEC specificato in anagrafica:" + bufMail.toString();
      }
      //Non è presente la mandataria per i raggruppamenti
      if(!controlloPresenzaMandatarie){
        bufMandatariaPresente.append("</ul>");
        controlloSuperato = "NO";
        messaggio += "<br>I seguenti raggruppamenti non hanno specificato la mandataria:" + bufMandatariaPresente.toString();
      }
      if(!ragioneSocialePresente){
        bufPresenzaNome.append("</ul>");
        controlloSuperato = "NO";
        messaggio += "<br>Le seguenti ditte non hanno la ragione sociale valorizzata:" + bufPresenzaNome.toString();
      }
      if(!controlloCodfiscValorizzatoSuperato){
        bufCodfisc.append("</ul>");
        controlloSuperato = "NO";
        messaggio += "<br>Le seguenti ditte non hanno il codice fiscale valorizzato:" + bufCodfisc.toString();
      }
      if(!controlloPivaValorizzataSuperato){
        bufPiva.append("</ul>");
        controlloSuperato = "NO";
        messaggio += "<br>Le seguenti ditte non hanno la partita iva valorizzata:" + bufPiva.toString();
      }
      if(!controlloCodfiscValidiSuperato){
        bufCodfiscValido.append("</ul>");
        controlloSuperato = "NO";
        messaggio += "<br>Le seguenti ditte hanno il codice fiscale con formato non valido:" + bufCodfiscValido.toString();
      }

      if(!controlloPivaValideSuperato){
        bufPivaValida.append("</ul>");
        controlloSuperato = "NO";
        messaggio += "<br>Le seguenti ditte hanno la partita iva con formato non valido:" + bufPivaValida.toString();
      }

      if(!controlloCodfiscUnicoSuperato){
        bufCodfiscDuplicato.append("</ul>");
        controlloSuperato = "NO";
        messaggio += "<br>Le seguenti ditte hanno il codice fiscale duplicato in anagrafica:" + bufCodfiscDuplicato.toString();
      }

      if(!controlloPivaUnicaSuperato){
        bufPivaDuplicata.append("</ul>");
        controlloSuperato = "NO";
        messaggio += "<br>Le seguenti ditte hanno la partita iva duplicata in anagrafica:" + bufPivaDuplicata.toString();
      }
      if(!controlloMailUnicaSuperato){
        bufMailDuplicata.append("</ul>");
        controlloSuperato = "NO";
        messaggio += "<br>Le seguenti ditte hanno l'indirizzo E-mail duplicato in anagrafica:" + bufMailDuplicata.toString();
      }
      if(!controlloPecUnicaSuperato){
        bufPecDuplicata.append("</ul>");
        controlloSuperato = "NO";
        messaggio += "<br>Le seguenti ditte hanno l'indirizzo PEC duplicato in anagrafica:" + bufPecDuplicata.toString();
      }
    }
    risultato.put("controlloSuperato", controlloSuperato);
    risultato.put("messaggio", messaggio);
    risultato.put("listaDestinatari", listaDestinatari);
    return risultato;
  }

  /**
   * Se la ditta non è registrata su portale vengono effettuati dei controlli sull'anagrafica della ditta
   * @param dittao
   * @return
   */
  private HashMap controlliAnagraficaDitta(String dittao){
    HashMap risultato =  new HashMap();
    String controlloSuperato ="SI";
    String messaggio = null;

    risultato.put("controlloSuperato", controlloSuperato);
    risultato.put("messaggio", messaggio);
    return risultato;
  }

  public HashMap controlloDimensioneAllegati(List listaDocumenti, String idcfg, FileAllegatoManager fileAllegatoManager) throws GestoreException{
    long dimTotaleAllegati = 0;
    double dimMaxTotaleFileByte=0;
    boolean eseguireControlloDimTotale=false;
    String controlloSuperato ="SI";
    String messaggio="";
    HashMap risultato = new HashMap();

    String dimMaxTotaleFileStringa = validatorManager.getDimensioneMassimaFile(idcfg);

    if(dimMaxTotaleFileStringa!= null && !"".equals(dimMaxTotaleFileStringa)){
      dimMaxTotaleFileStringa = dimMaxTotaleFileStringa.trim();
      eseguireControlloDimTotale=true;
      dimMaxTotaleFileByte = Math.pow(2, 20) * Double.parseDouble(dimMaxTotaleFileStringa);
      try {
        if(listaDocumenti!=null && listaDocumenti.size()>0){

          for(int i=0;i<listaDocumenti.size();i++){
            Long iddocdig = (Long)SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 3).getValue();
            String idprg = (String)SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 2).getValue();
            BlobFile fileAllegatoBlob = fileAllegatoManager.getFileAllegato(idprg, iddocdig);

            if(fileAllegatoBlob!=null)
              dimTotaleAllegati += fileAllegatoBlob.getStream().length;

          }
        }
      } catch (IOException e) {
        throw new GestoreException("Errore nella lettura degli allegati della comunicazione(W_DOCDIG.DIGOGG)", null,e);
      }
    }
    if(eseguireControlloDimTotale && dimTotaleAllegati> dimMaxTotaleFileByte){  //Controllo sulla dimensione totale massima di tutti gli allegati
      controlloSuperato = "NO";
      messaggio =  "<br>La dimensione totale dei file da allegare supera il limite consentito dal server di posta di " + dimMaxTotaleFileStringa + " MB";
    }
    risultato.put("controlloSuperato", controlloSuperato);
    risultato.put("messaggio", messaggio);
    return risultato;
  }


  /**
   * Si controlla che per i documenti dei gruppi passati come parametri siano presenti il file(o l'url se previsto da configurazione)
   *
   * @param ngara
   * @param codgar
   * @param genere
   * @param condizioneGruppo
   * @param gestioneUrl
   * @param bando
   * @return String[2]
   *    il primo elemento contiene l'esito del controllo(SI, NO)
   *    il secondo elemento contiene il messaggio del controllo
   * @throws GestoreException,SQLException
   */
  public String[] controlloAllegatoUrl(String ngara, String codgar, Long genere, String condizioneGruppo, boolean gestioneUrl, String bando) throws SQLException, GestoreException{
    //Tutti le righe di documenti di gara del gruppo 1 o 6 devono avere il documento associato
    //nel caso di gruppo 1 se è attiva la gestione dell'url, può non esserci l'allegato, ma deve esserci l'url
    boolean filePresente=true;
    String select;
    List listaDocumentiGara= null;
    String controlloSuperato="SI";
    String messaggio="";

    select="select IDPRG, IDDOCDG, URLDOC from DOCUMGARA where CODGAR=? and " + condizioneGruppo + " and statodoc is null order by norddocg";
    listaDocumentiGara = sqlManager.getListVector(
        select, new Object[] { codgar});

    String urldoc = null;
    for (int i = 0; i < listaDocumentiGara.size(); i++) {
      urldoc = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(listaDocumentiGara.get(i),2).getStringValue());
      List listaTmp = sqlManager.getListVector("select DIGNOMDOC from W_DOCDIG where IDPRG = ? and IDDOCDIG = ?",
          new Object[] {SqlManager.getValueFromVectorParam(listaDocumentiGara.get(i),0).getStringValue(),
          SqlManager.getValueFromVectorParam(listaDocumentiGara.get(i),1).longValue()});
      if(listaTmp!=null && listaTmp.size()>0){
        String nomedoc= SqlManager.getValueFromVectorParam(listaTmp.get(0),0).getStringValue();
        if(nomedoc == null || "".equals(nomedoc)){
          filePresente = false;
          //break;
        }
      }else{
        filePresente = false;
        //break;
      }
      if(!filePresente){
        //Nel caso di bando=0 e 1 e attiva la gestione dell'url del tabellato A1108 per segnalare errore si deve
        //controllare anche il campo URLDOC
        //if(("1".equals(bando) || "0".equals(bando)) && gestioneUrl && urldoc!=null)
        if(gestioneUrl && urldoc!=null){
          filePresente = true;
          if(!PgManager.validazioneURL(urldoc)){
            controlloSuperato = "NO";
            messaggio += "<br>Per tutti i documenti di gara da pubblicare le url di pubblicazione specificate devono essere valide.";
            break;
          }
        }else
          break;
      }
    }

    if(!filePresente){
      controlloSuperato = "NO";
      String messaggioLotti="";
      //Differenzia il msg nel caso di gara divisa in lotti con offerte distinte
      if(ngara == null && genere == null)
        messaggioLotti=" e dei singoli lotti";
      if("0".equals(bando)){
        messaggio += "<br>Per tutti i documenti di gara da pubblicare deve essere specificato l'allegato";
        if(gestioneUrl)
          messaggio += " o l'url di pubblicazione";
        messaggio += ".";
      }else if("3".equals(bando)){
        messaggio += "<br>Per tutti i documenti di gara da pubblicare deve essere specificato l'allegato";
        if(gestioneUrl)
          messaggio += " o l'url di pubblicazione";
        messaggio += ".";
      }else{
        if(genere!=null && genere.longValue()==11)
          messaggio +=  "<br>Per tutti i documenti deve essere specificato l'allegato";
        else
          messaggio +=  "<br>Per tutti i documenti di gara" + messaggioLotti + " da pubblicare deve essere specificato l'allegato";
        if("1".equals(bando) && gestioneUrl)
          messaggio += " o l'url di pubblicazione";
        messaggio +=  ".";
      }

    }
    return new String[]{controlloSuperato,messaggio};
  }

  private List[] getTabellatiRegioneMarche() throws GestoreException{
    List ret[] = new List[3];
    ret[0] = new Vector();
    ret[1] = new Vector();
    ret[2] = new Vector();

    StrutturaClass struttureRegionali[] = this.gestioneRegioneMarcheManager.getStruttureRegionali();
    if(struttureRegionali!=null && struttureRegionali.length>0){
      String descrizione =null;
      Long idStruttura=null;
      for(int i=0;i<struttureRegionali.length;i++){
        descrizione = struttureRegionali[i].getDescrizione();
        idStruttura = new Long(struttureRegionali[i].getStrutturaID());
        ret[0].add(new Object[] {descrizione, idStruttura});
      }

    }


    TemiRegionaliClass temiRegionali[] = this.gestioneRegioneMarcheManager.getTemiRegionali();
    if(temiRegionali!=null && temiRegionali.length>0){
      String nomeTemi =null;
      Long idTemi=null;
      for(int i=0;i<temiRegionali.length;i++){
        nomeTemi = temiRegionali[i].getNome();
        idTemi = new Long(temiRegionali[i].getTemaRegionaleID());
        ret[1].add(new Object[] {nomeTemi, idTemi});
      }

    }

    TipiProceduraClass tipologieProcedure[] = this.gestioneRegioneMarcheManager.getTipologieProcedure();
    if(tipologieProcedure!=null && tipologieProcedure.length>0){
      String nomeTipologie =null;
      Long idTipologie=null;
      for(int i=0;i<tipologieProcedure.length;i++){
        nomeTipologie = tipologieProcedure[i].getNome();
        idTipologie = new Long(tipologieProcedure[i].getTipoProceduraID());
        ret[2].add(new Object[] {nomeTipologie, idTipologie});
      }

    }


    return ret;
  }

  private List getListaStuttureATC() throws GestoreException{
    List ret = new Vector();
    List<Map<String, String>> strutture = this.gestioneATCManager.getStrutture();
    if(strutture!=null && strutture.size()>0){
      String id=null;
      String nome=null;
      for(int i=0;i<strutture.size();i++){
        id = strutture.get(i).get("id");
        nome = strutture.get(i).get("nome_ufficio");
        ret.add(new Object[] {id, nome});
      }

    }

    return ret;
  }

  private boolean saltareControlloObbligPiva(Long tipimp,String liberoProf, String ImpresaSociale){
    boolean saltareControlloPivaNulla=false;
    if((liberoProf!=null && "1".equals(liberoProf) && tipimp!=null && tipimp.longValue()==6)||(ImpresaSociale!=null && "1".equals(ImpresaSociale) && tipimp!=null && tipimp.longValue()==13)){
      saltareControlloPivaNulla = true;
    }
    return saltareControlloPivaNulla;
  }

  /*prende in input una stringa con l'elenco dei gruppi sul quale deve essere fatta la selezione e restituisce
   * la stessa lista con gli identificativi delle tipologie invece dei gruppi
   */
  public String tipologieVisibiliGruppo(String codgar, String condizioneGruppo) throws SQLException, GestoreException{

    String select = "select count(*) from TORN left outer join GARE on TORN.CODGAR=GARE.CODGAR1 where TORN.CODGAR=?";
    Long tipoPubblicazione;
    String clausolaWhereVis,clausolaWhereUlt;
    String tipologie = "";
    String response = "";
    List<?> w9cfPubb = sqlManager.getListVector("select ID, CL_WHERE_VIS, CL_WHERE_ULT from G1CF_PUBB where " + condizioneGruppo + " order by NUMORD", new Object[] {});
    if (w9cfPubb != null && w9cfPubb.size() > 0) {
      for (int i = 0; i < w9cfPubb.size(); i++) {
        tipoPubblicazione = SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 0).longValue();
        clausolaWhereVis = (String) SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 1).getValue();
        clausolaWhereUlt = (String) SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 2).getValue();
        if (clausolaWhereVis != null && !clausolaWhereVis.equals("")) {
          clausolaWhereVis = " and (" + clausolaWhereVis + ")";
        }
        if (clausolaWhereUlt != null && !clausolaWhereUlt.equals("")) {
          clausolaWhereVis = clausolaWhereVis + " and (" + clausolaWhereUlt + ")";
        }
        Long counter = (Long) sqlManager.getObject(select + clausolaWhereVis, new Object[] {codgar});

        if (counter!= null && counter.intValue() > 0) {
            if(!"".equals(tipologie)){
             tipologie+=",";
            }
          tipologie+=tipoPubblicazione;
        }
      }
    }
    if("".equals(tipologie)){
      response = "";
    }else{
      response = " and (TIPOLOGIA is null or TIPOLOGIA in (" + tipologie + "))";
    }
    return response;
  }

  /**
   * Viene eseguito il controllo sulla modifica del modello associato al qform
   * @param ngara
   * @param codgar
   * @param bando
   * @param iterga
   * @param controlloCompleto
   * @param nobustamm
   * @param modlic
   * @param valtec
   * @param costofisso
   * @param genere
   * @return String[]
   * @throws SQLException
   * @throws GestoreException
   */
  private String[] controlloQform(String ngara, String codgar, String bando, Long iterga, boolean controlloCompleto, String nobustamm, Long modlic, String valtec, boolean costofisso, Long genere) throws SQLException, GestoreException {
    String ret[] =  new String[]{"",""};
    String chiaveQform = ngara;
    if(ngara==null || "".equals(ngara))
      chiaveQform=codgar;
    String dbDultaggString = sqlManager.getDBFunction("DATETIMETOSTRING",
        new String[] { "l.dultagg" });
    String dbdultaggmodString = sqlManager.getDBFunction("DATETIMETOSTRING",
        new String[] { "q.dultaggmod" });
    //String selectQform="select " + dbDultaggString + ", " + dbdultaggmodString + ", q.key1 from qformlib l, qform q where q.entita='GARE' and q.key1=? and q.busta=? and q.idmodello=l.id  and q.stato=1";
    Long bustaVet[]=null;
    if("3".equals(bando) || (!"3".equals(bando) && !new Long(2).equals(iterga) && !new Long(4).equals(iterga) && !new Long(7).equals(iterga))) {
      if(controlloCompleto) {
        boolean bustaAmm= true;
        boolean bustaTec= true;
        boolean bustaEco= true;
        int dim=3;
        if(costofisso) {
          bustaEco= false;
          dim--;
        }
        if("1".equals(nobustamm)) {
          bustaAmm = false;
          dim--;
        }
        if(!new Long(6).equals(modlic) && !"1".equals(valtec)) {
          bustaTec = false;
          dim--;
        }
        bustaVet = new Long[dim];

        if(bustaAmm)
          bustaVet[0]=new Long(1);
        if(bustaTec) {
          int indice=0;
          if(bustaAmm)
            indice=1;
          bustaVet[indice]=new Long(2);
        }
        if(bustaEco) {
          int indice=0;
          if(bustaAmm && bustaTec)
            indice = 2;
          else if(bustaAmm || bustaTec)
            indice = 1;
          bustaVet[indice]=new Long(3);
        }
        //bustaVet = new Long[] {new Long(1), new Long(2), new Long(3)};
      }else
        bustaVet = new Long[] {new Long(1)};

    }else {
      bustaVet = new Long[] {new Long(4)};

    }
    Long busta= null;
    String descBusta = "";
    for(int i=0; i < bustaVet.length;i++) {
      busta = bustaVet[i];
      List<?> datiQform= null;
      if(new Long(3).equals(genere) && (new Long(2).equals(busta) || new Long(3).equals(busta))) {
        //qform associati ai lotti
        String selectQform="select " + dbDultaggString + ", " + dbdultaggmodString + ", q.key1 from qformlib l, qform q where q.entita='GARE' and q.key1 like ? and q.busta=? and q.idmodello=l.id  and q.stato=1";
        selectQform += "order by q.key1";
        datiQform= this.sqlManager.getListVector(selectQform, new Object[] {codgar + "%", busta});
      }else {
        String selectQform="select " + dbDultaggString + ", " + dbdultaggmodString + ", q.key1 from qformlib l, qform q where q.entita='GARE' and q.key1=? and q.busta=? and q.idmodello=l.id  and q.stato=1";
        datiQform= this.sqlManager.getListVector(selectQform, new Object[] {chiaveQform, busta});
      }
      if(datiQform!=null && datiQform.size()>0) {
        for(int j=0; j < datiQform.size(); j++) {
          String dultaggStringValue = SqlManager.getValueFromVectorParam(datiQform.get(j), 0).getStringValue();
          String dultaggmodStringValue = SqlManager.getValueFromVectorParam(datiQform.get(j), 1).getStringValue();
          String lotto= "";
          if(dultaggStringValue!=null && !"".equals(dultaggStringValue) && dultaggmodStringValue!=null && !"".equals(dultaggmodStringValue)) {
            Date dultagg = UtilityDate.convertiData(dultaggStringValue, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
            Date dultaggmod = UtilityDate.convertiData(dultaggmodStringValue, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
            if(dultagg.after(dultaggmod)){
              descBusta = tabellatiManager.getDescrTabellato("A1013", busta.toString());
              String dultaggString = UtilityDate.convertiData(new Date(dultagg.getTime()), UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
              if(new Long(3).equals(genere) && (new Long(2).equals(busta) || new Long(3).equals(busta))) {
                lotto = SqlManager.getValueFromVectorParam(datiQform.get(j), 2).getStringValue();
                lotto = "del lotto " + lotto + " ";
              }
              ret[0] = "WARNING";
              ret[1] += "<br>Il modello da  cui deriva il Q-form associato alla busta '" + descBusta + "' " + lotto + "è stato aggiornato successivamente alla creazione del Q-form " + dultaggString;
            }
          }
        }
      }
    }
    return ret;
  }

  /**
   * Si verifica se esiste il qform per tutti i lotti della gara per la busta specificata
   * Viene restituito un vettore, che in posizione 0 ha "SI" o "No" a seconda che l'esito si positivo o meno.
   * In posizione 1 c'è l'eventuale messaggio
   * @param chiave
   * @param busta
   * @return String[]
   * @throws SQLException
   */
  private String[] controlloQformLotti(String chiave, Long busta) throws SQLException {
    String controlloSuperato = "SI";
    String messaggio = "";
    String bustaStringa = "economica";
    if(new Long(2).equals(busta))
      bustaStringa = "tecnica";
    String selectBuste = "select ngara from gare where codgar1=? and ngara!=codgar1 and not exists(select id from qform where entita='GARE' and key1=ngara and busta=? and (stato = 1 or stato = 5))";
    if(new Long(2).equals(busta))
      selectBuste += " and (modlicg=6 or not exists(select gare1.valtec from gare1 where  gare1.ngara=gare.ngara))";
    else
      selectBuste += " and not exists(select gare1.costofisso from gare1 where gare1.ngara=gare.ngara and gare1.costofisso='1')";
    selectBuste += " order by ngara";
    List<?> LottiSenzaQform = this.sqlManager.getListVector(selectBuste, new Object[] {chiave, busta});
    if(LottiSenzaQform!=null && LottiSenzaQform.size()>0) {
      controlloSuperato = "NO";
      String lotto = null;
      for(int i=0;i<LottiSenzaQform.size();i++) {
        lotto = SqlManager.getValueFromVectorParam(LottiSenzaQform.get(i), 0).getStringValue();
        messaggio +=  "<br>Non è stato associato un q-form nei documenti richiesti ai concorrenti per la busta " + bustaStringa + " del lotto " + lotto + ".";
      }
    }
    return new String[]{controlloSuperato,messaggio};
  }
}