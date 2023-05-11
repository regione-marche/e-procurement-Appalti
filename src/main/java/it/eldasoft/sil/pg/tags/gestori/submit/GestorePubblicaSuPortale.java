/*
 * Created on 31/08/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.TransactionStatus;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lowagie.text.DocumentException;

import intra.regionemarche.ResultClass;
import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ControlliOepvManager;
import it.eldasoft.sil.pg.bl.GestioneATCManager;
import it.eldasoft.sil.pg.bl.GestioneProgrammazioneManager;
import it.eldasoft.sil.pg.bl.GestioneRegioneMarcheManager;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.ImportExportOffertaPrezziManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.bl.cifrabuste.CifraturaBusteManager;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloResType;
import it.maggioli.eldasoft.ws.dm.WSDMInviaMailType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAnagraficaType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoInType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;
import it.maggioli.eldasoft.ws.dm.WSDMTipoVoceRubricaType;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire la pubblicazione
 * su portale alice gare
 *
 * @author Marcello Caminiti
 */
public class GestorePubblicaSuPortale extends
    AbstractGestoreEntita {

  static Logger               logger         = Logger.getLogger(GestorePubblicaSuPortale.class);

  /** Manager per la gestione dell'integrazione con WSDM. */
  private GestioneWSDMManager gestioneWSDMManager;


  /** Manager per la gestione dei tabellati. */
  private TabellatiManager tabellatiManager;

  private CifraturaBusteManager cifraturaBusteManager;

  private GestioneRegioneMarcheManager gestioneRegioneMarcheManager;

  private ControlliOepvManager controlliOepvManager;

  private GestioneATCManager gestioneATCManager;

  private GenChiaviManager genChiaviManager;

  private GestioneProgrammazioneManager gestioneProgrammazioneManager;

  private ImportExportOffertaPrezziManager importExportOffertaPrezziManager;

  private PgManagerEst1 pgManagerEst1;

  public GestorePubblicaSuPortale() {
    super(false);
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);

    gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        this.getServletContext(), GestioneWSDMManager.class);


    tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        this.getServletContext(), TabellatiManager.class);

    cifraturaBusteManager = (CifraturaBusteManager) UtilitySpring.getBean("cifraturaBusteManager",
        this.getServletContext(), CifraturaBusteManager.class);

    gestioneRegioneMarcheManager = (GestioneRegioneMarcheManager) UtilitySpring.getBean("gestioneRegioneMarcheManager",
        this.getServletContext(), GestioneRegioneMarcheManager.class);

    controlliOepvManager = (ControlliOepvManager) UtilitySpring.getBean("controlliOepvManager",
        this.getServletContext(), ControlliOepvManager.class);

    gestioneATCManager = (GestioneATCManager) UtilitySpring.getBean("gestioneATCManager",
        this.getServletContext(), GestioneATCManager.class);

    genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);

    gestioneProgrammazioneManager = (GestioneProgrammazioneManager) UtilitySpring.getBean("gestioneProgrammazioneManager",
        this.getServletContext(), GestioneProgrammazioneManager.class);

    importExportOffertaPrezziManager = (ImportExportOffertaPrezziManager) UtilitySpring.getBean("importExportOffertaPrezziManager",
        this.getServletContext(), ImportExportOffertaPrezziManager.class);

    pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        this.getServletContext(), PgManagerEst1.class);

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

    int index_bando = -1;
    //variabili per tracciatura eventi
    int livEvento = 3;
    String codEvento = "";
    String oggEvento = "";
    String descrEvento = "";
    String descrGenere = "";
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
    String messageKey = "";
    String idconfi = UtilityStruts.getParametroString(this.getRequest(),"idconfi");
    String riservatezzaAttiva = ConfigManager.getValore("wsdm.applicaRiservatezza."+idconfi);
    String integrazioneWSDM = null;

    try{

        // lettura dei parametri di input
        // lettura dei parametri di input
        String codgar = datiForm.getString("CODGAR");
        String ngara = datiForm.getString("NGARA");
        Timestamp datpub = datiForm.getData("DATPUB");
        String bando = datiForm.getString("BANDO");
        if(datpub == null){
          datpub = new Timestamp(System.currentTimeMillis());
          Date data = new Date(datpub.getTime());
          data = DateUtils.truncate(data, Calendar.DATE);
          datpub = new Timestamp(data.getTime());
        }
  //    if ("1".equals(bando)){
        //Si inserisce una occorrenza su PUBBLI con la data passata dalla popup e con
        //TIPPUB=11 (Portale Alice gare)
        String gartel = null;
        String garavviso = null;
        String iterga = UtilityStruts.getParametroString(this.getRequest(),"iterga");
        String soloPunteggiTec = UtilityStruts.getParametroString(this.getRequest(),"soloPunteggiTec");
        String uuid = UtilityStruts.getParametroString(this.getRequest(),"uuid");

        Long genere = (Long) sqlManager.getObject("select genere from V_GARE_TORN where codgar = ?", new Object[]{codgar});
        boolean formularioCompletoAbilitato = false;

        ngara = UtilityStringhe.convertiNullInStringaVuota(ngara);
        int index_genere;

        Long genPubblicazione;
        try {
          genPubblicazione = (Long) sqlManager.getObject("select genere from V_GARE_GENERE where codgar = ? and genere <100 ", new Object[]{codgar});
          index_genere = genPubblicazione.intValue();
          switch (index_genere) {
          case 1:
          case 2:
          case 3:
            descrGenere = "gara";
            break;
          case 10:
            descrGenere = "elenco";
            break;
          case 11:
            descrGenere = "avviso";
            break;
          case 20:
            descrGenere = "catalogo";
            break;
          }
        } catch (SQLException gp) {
          throw new GestoreException("Errore nell'aggiornamento della gara", null, gp);
        }

        if(index_genere == 1 || index_genere == 3){
          oggEvento = codgar;
        }else{
          oggEvento = ngara;
        }

        Timestamp dteoff = null;
        Timestamp dtepar = null;
        String gestioneFascicoloWSDM = null;

        index_bando = Integer.parseInt(bando);
        switch (index_bando) {
        case 1:
          codEvento = "GA_PUBBLICA_BANDO";
          descrEvento = "Pubblicazione " + descrGenere + " su area pubblica di Portale Appalti";
          //Se la data inserita è >= rispetto a quella inserita nella popup, allora si deve bloccare il salvataggio
          try {
            //java.sql.Date dteoff = (java.sql.Date)this.sqlManager.getObject("select dteoff from torn where codgar=?", new Object[]{codgar});
            Vector<?> datiTorn = this.sqlManager.getVector("select dteoff,gartel,iterga,dtepar from torn where codgar=?", new Object[]{codgar});
            if(datiTorn!=null && datiTorn.size()>0){
              dteoff = SqlManager.getValueFromVectorParam(datiTorn, 0).dataValue();
              gartel = SqlManager.getValueFromVectorParam(datiTorn, 1).getStringValue();
              //Long iterga = SqlManager.getValueFromVectorParam(datiTorn, 2).longValue();
              dtepar = SqlManager.getValueFromVectorParam(datiTorn, 3).dataValue();
              if(datpub != null){
                Date data1 = new Date(datpub.getTime());
                if("1".equals(iterga)){
                  Date data2 = new Date(dteoff.getTime());
                  if(data1.compareTo(data2)>0){
                    livEvento = 3;
                    messageKey = "errors.gestoreException.*.erroreDataPubbl.presOfferta";
                    errMsgEvento = this.resBundleGenerale.getString(messageKey);
                    throw new GestoreException("La data di pubblicazione deve essere precedente o uguale alla data termine per la presentazione dell'offerta ", "erroreDataPubbl.presOfferta", new Exception());
                  }
                }else if("2".equals(iterga) || "4".equals(iterga) || "7".equals(iterga)){
                  Date data2 = new Date(dtepar.getTime());
                  if(data1.compareTo(data2)>0){
                    livEvento = 3;
                    messageKey = "errors.gestoreException.*.erroreDataPubbl.presPartecipazione";
                    errMsgEvento = this.resBundleGenerale.getString(messageKey);
                    throw new GestoreException("La data di pubblicazione deve essere precedente o uguale alla data termine per la presentazione delle domande di partecipazione", "erroreDataPubbl.presPartecipazione", new Exception());
                  }
                }
              }
              if("1".equals(gartel)){
                //Date dataOdierna = UtilityDate.getDataOdiernaAsDate();
                /*String dataOggi = UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_GG_MM_AAAA);
                Date dataOdierna = UtilityDate.convertiData(dataOggi, UtilityDate.FORMATO_GG_MM_AAAA);
                if(data1.compareTo(dataOdierna)<0){
                  livEvento = 3;
                  messageKey = "errors.gestoreException.*.erroreDataPubbl.dataCorrente";
                  errMsgEvento = this.resBundleGenerale.getString(messageKey);
                  throw new GestoreException("La data di pubblicazione deve essere successiva o uguale alla data corrente", "erroreDataPubbl.dataCorrente", new Exception());
                }*/
              }
            }

          } catch (SQLException e) {
            throw new GestoreException("Errore nella lettura della data TORN.DTEOFF ", null, e);
          }

          Date datsca = null;
          garavviso = UtilityStruts.getParametroString(this.getRequest(),"garavviso");
          if("1".equals(garavviso)) {
            datsca = (Date)this.sqlManager.getObject("select datsca from gareavvisi where ngara=?", new Object[]{ngara});
            if(datsca != null && datpub != null){
              Date data1 = new Date(datpub.getTime());
              if(data1.compareTo(datsca)>0) {
                livEvento = 3;
                messageKey = "errors.gestoreException.*.erroreDataPubbl.presPartecipazione";
                errMsgEvento = this.resBundleGenerale.getString(messageKey);
                throw new GestoreException("La data di pubblicazione deve essere precedente o uguale alla data scadenza", "erroreDataPubbl.dataScadenza", new Exception());
              }
            }
          }
          //if(!"1".equals(garavviso)){
            gestioneFascicoloWSDM = UtilityStruts.getParametroString(this.getRequest(),"gestioneFascicoloWSDM");
            if("1".equals(gestioneFascicoloWSDM)){
              try {
                Timestamp data = null;
                if(!"1".equals(garavviso))
                  data = this.getData(iterga, dteoff, dtepar);
                else{
                  if(datsca!=null)
                    data =  new Timestamp(datsca.getTime());
                }
                this.associazioneFascicoloWSDM(genPubblicazione,ngara,codgar,iterga,datpub,data,riservatezzaAttiva,null,idconfi);
              } catch (SQLException e) {
                livEvento = 3;
                errMsgEvento = "Errore nella creazione del Fascicolo";
                throw new GestoreException("Errore nella creazione del Fascicolo", null, e);
              } catch (IOException e) {
                livEvento = 3;
                errMsgEvento = "Errore nella creazione del Fascicolo";
                throw new GestoreException("Errore nella creazione del Fascicolo", null, e);
              }catch (DocumentException e) {
                this.getRequest().setAttribute("pubblicazioneEseguita", "Errori");
                livEvento = 3;
                errMsgEvento = "Errore nella creazione del Fascicolo";
                throw new GestoreException("Errore nella creazione del Fascicolo", null, e);
              }
            }
          //}


          //Integrazione Regione Marche pubblicazione bando
          if (ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TIPO)!=null
              && "1".equals(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TIPO))){
            String temaRegionale = UtilityStruts.getParametroString(this.getRequest(),"temaRegionale");
            String tipologiaBando = UtilityStruts.getParametroString(this.getRequest(),"tipologiaBando");
            String strutturaRegionale = UtilityStruts.getParametroString(this.getRequest(),"strutturaRegionale");
            String idBando = UtilityStruts.getParametroString(this.getRequest(),"bandoIdRegMarche");
            if(idBando!=null && !"".equals(idBando)){
              ResultClass risposta=this.gestioneRegioneMarcheManager.updateBando(ngara, codgar, genPubblicazione, datpub, new Long(strutturaRegionale), new Long(temaRegionale), new Long(tipologiaBando), "BANDO", new Long(idBando));
              if(!risposta.isResult()){
                String msg= risposta.getError();
                livEvento = 3;
                messageKey = "errors.gestoreException.*.ws.bandiRegioneMarche.updateBando.esitoNOK";
                errMsgEvento = this.resBundleGenerale.getString(messageKey);
                errMsgEvento= errMsgEvento.replace("{0}", msg);
                throw new GestoreException("L'aggiornamento del bando da parte del servizio remoto non e' andato a buon fine", "ws.bandiRegioneMarche.updateBando.esitoNOK", new Object[]{msg}, new Exception());
              }
            }else {
              ResultClass risposta=this.gestioneRegioneMarcheManager.insertBando(ngara, codgar, genPubblicazione, datpub, new Long(strutturaRegionale), new Long(temaRegionale), new Long(tipologiaBando), "BANDO");
              if(!risposta.isResult()){
                String msg= risposta.getError();
                livEvento = 3;
                messageKey = "errors.gestoreException.*.ws.bandiRegioneMarche.addBando.esitoNOK";
                errMsgEvento = this.resBundleGenerale.getString(messageKey);
                errMsgEvento= errMsgEvento.replace("{0}", msg);
                throw new GestoreException("L'inserimento del bando da parte del servizio remoto non e' andato a buon fine", "ws.bandiRegioneMarche.addBando.esitoNOK", new Object[]{msg}, new Exception());
              }
            }
          }


          //Integrazione ATC pubblicazione bando
          if (ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TIPO)!=null
              && "2".equals(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TIPO)) && (uuid==null || "".equals(uuid))){
            String strutturaATC = UtilityStruts.getParametroString(this.getRequest(),"strutturaATC");
            HashMap<String,String> risposta=this.gestioneATCManager.insertBando(ngara, codgar, genPubblicazione, datpub, strutturaATC, "BANDO");
            if(risposta!=null){
              if(risposta.get("msg")!=null && !"".equals(risposta.get("msg"))){
                String msg= risposta.get("msg");
                livEvento = 3;
                messageKey = "errors.gestoreException.*.ws.ATC.InternetSoluzioni.addOggetto.esitoNOK";
                errMsgEvento = this.resBundleGenerale.getString(messageKey);
                errMsgEvento= errMsgEvento.replace("{0}", msg);
                throw new GestoreException("L'inserimento del bando da parte del servizio remoto non e' andato a buon fine", "ws.ATC.InternetSoluzioni.addOggetto.esitoNOK", new Object[]{msg}, new Exception());
              }else{
                Integer newId= null;
                String insertGaruuid = "insert into garuuid(id,codgar,tipric,uuid,ngara) values(?,?,?,?,?)";
                String numLotti = risposta.get("numLotti");
                int numLottiInt = Integer.parseInt(numLotti);
                String codiceLotto=null;
                for(int i=0;i<=numLottiInt;i++){
                  codiceLotto=null;
                  newId= this.genChiaviManager.getNextId("GARUUID");
                  //Si deve prelevare il valore da mettere in uuid dalla risposta del servizio
                  if(i==0){
                    //Gestione dell'id della gara
                    if(risposta.get("idGara")!=null && !"".equals(risposta.get("idGara"))){
                      uuid= risposta.get("idGara");
                    }
                  }else{
                    //Gestione dell'id del lotto
                    if(risposta.get("idLotto" + i)!=null && !"".equals(risposta.get("idLotto" + i))){
                      uuid= risposta.get("idLotto" + i);
                    }
                    if(risposta.get("codiceLotto" + i)!=null && !"".equals(risposta.get("codiceLotto" + i))){
                      codiceLotto = risposta.get("codiceLotto" + i);
                    }
                  }
                  try {
                    this.sqlManager.update(insertGaruuid, new Object[] { newId,codgar,"ATCPAT_bando",uuid,codiceLotto});
                  } catch (SQLException e) {
                    livEvento = 3;
                    errMsgEvento = "Errore nell'inserimento id GARUUID";
                    throw new GestoreException("Errore nell'inserimento id GARUUID", null, e);
                  }
                }
              }
            }
          }

          Vector<DataColumn> elencoCampi1 = new Vector<DataColumn>();
          elencoCampi1.add(new DataColumn("PUBBLI.CODGAR9",
              new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar)));
          elencoCampi1.add(new DataColumn("PUBBLI.NUMPUB", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,null)));
          elencoCampi1.add(new DataColumn("PUBBLI.TIPPUB",
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(11))));
          elencoCampi1.add(new DataColumn("PUBBLI.DATPUB",
              new JdbcParametro(JdbcParametro.TIPO_DATA, datpub)));


          DataColumnContainer container1 = new DataColumnContainer(elencoCampi1);
          // si predispone il gestore per l'aggiornamento dell'entità
          DefaultGestoreEntitaChiaveNumerica gestore1 = new DefaultGestoreEntitaChiaveNumerica(
              "PUBBLI", "NUMPUB",new String[] { "CODGAR9" }, this.getRequest());

          container1.getColumn("PUBBLI.NUMPUB").setValue(
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null));
          gestore1.inserisci(status, container1);

          if("1".equals(gartel)){
            try {
              String gara=ngara;
              if(gara==null || "".equals(gara))
                gara= codgar;
              if(genPubblicazione!=null && genPubblicazione.longValue()==3)
                gara=codgar;
              Long fasgar = new Long(1);
              Long stepgar = new Long(10);
              if("2".equals(iterga) || "4".equals(iterga) || "7".equals(iterga)){
                fasgar = new Long(-5);
                stepgar = new Long(-50);
              }

              this.sqlManager.update("update GARE set FASGAR = ?, STEPGAR = ? where NGARA=?",new Object[]{fasgar, stepgar, gara});
            } catch (SQLException e) {
              throw new GestoreException("Errore nell'aggiornamento della gara", null, e);
            }
          }
          this.cancellazioneDatiNonUtilizzati(ngara, codgar, genPubblicazione, iterga);

          //Integrazione programmazione
          if(gestioneProgrammazioneManager.isAttivaIntegrazioneProgrammazione()){
            this.gestioneProgrammazioneManager.aggiornaRdaGara(codgar,null,null);
          }



          break;
        case 0:
        case 2:
          codEvento = "GA_PUBBLICA_ESITO";
          descrEvento = "Pubblicazione esito di gara su area pubblica di Portale Appalti";

          //Integrazione Regione Marche pubblicazione bando
          if (ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TIPO)!=null
              && "1".equals(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TIPO))){
            String temaRegionale = UtilityStruts.getParametroString(this.getRequest(),"temaRegionale");
            String tipologiaBando = UtilityStruts.getParametroString(this.getRequest(),"tipologiaBando");
            String strutturaRegionale = UtilityStruts.getParametroString(this.getRequest(),"strutturaRegionale");
            String idBando = UtilityStruts.getParametroString(this.getRequest(),"bandoIdRegMarche");
            if(idBando==null || "".equals(idBando)){
              ResultClass risposta = this.gestioneRegioneMarcheManager.insertBando(ngara, codgar, genPubblicazione, datpub, new Long(strutturaRegionale), new Long(temaRegionale), new Long(tipologiaBando), "ESITO");
              if(!risposta.isResult()){
                String msg= risposta.getError();
                livEvento = 3;
                messageKey = "errors.gestoreException.*.ws.bandiRegioneMarche.addBando.esitoNOK";
                errMsgEvento = this.resBundleGenerale.getString(messageKey);
                errMsgEvento = errMsgEvento.replace("{0}", msg);
                throw new GestoreException("L'inserimento del bando da parte del servizio remoto non e' andato a buon fine", "ws.bandiRegioneMarche.addBando.esitoNOK", new Object[]{msg}, new Exception());
              }
            }
          }

          //Integrazione ATC pubblicazione bando
          if (ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TIPO)!=null
              && "2".equals(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TIPO)) && (uuid==null || "".equals(uuid))){
            String strutturaATC = UtilityStruts.getParametroString(this.getRequest(),"strutturaATC");
            HashMap<String,String> risposta=this.gestioneATCManager.insertBando(ngara, codgar, genPubblicazione, datpub, strutturaATC, "ESITO");
            if(risposta!=null){
              if(risposta.get("msg")!=null && !"".equals(risposta.get("msg"))){
                String msg= risposta.get("msg");
                livEvento = 3;
                messageKey = "errors.gestoreException.*.ws.ATC.InternetSoluzioni.addOggetto.esitoNOK";
                errMsgEvento = this.resBundleGenerale.getString(messageKey);
                errMsgEvento= errMsgEvento.replace("{0}", msg);
                throw new GestoreException("L'inserimento del bando da parte del servizio remoto non e' andato a buon fine", "ws.ATC.InternetSoluzioni.addOggetto.esitoNOK", new Object[]{msg}, new Exception());
              }else{
                Integer newId= null;
                String insertGaruuid = "insert into garuuid(id,codgar,tipric,uuid,ngara) values(?,?,?,?,?)";
                String numLotti = risposta.get("numLotti");
                int numLottiInt = Integer.parseInt(numLotti);
                String codiceLotto=null;
                for(int i=0;i<=numLottiInt;i++){
                  codiceLotto=null;
                  newId= this.genChiaviManager.getNextId("GARUUID");
                  //Si deve prelevare il valore da mettere in uuid dalla risposta del servizio
                  if(i==0){
                    //Gestione dell'id della gara
                    if(risposta.get("idGara")!=null && !"".equals(risposta.get("idGara"))){
                      uuid= risposta.get("idGara");
                    }
                  }else{
                    //Gestione dell'id del lotto
                    if(risposta.get("idLotto" + i)!=null && !"".equals(risposta.get("idLotto" + i))){
                      uuid= risposta.get("idLotto" + i);
                    }
                    if(risposta.get("codiceLotto" + i)!=null && !"".equals(risposta.get("codiceLotto" + i))){
                      codiceLotto = risposta.get("codiceLotto" + i);
                    }
                  }
                  try {
                    this.sqlManager.update(insertGaruuid, new Object[] { newId,codgar,"ATCPAT_esito",uuid,codiceLotto});
                  } catch (SQLException e) {
                    livEvento = 3;
                    errMsgEvento = "Errore nell'inserimento id GARUUID";
                    throw new GestoreException("Errore nell'inserimento id GARUUID", null, e);
                  }
                }
              }
            }
          }


          String chiave = ngara;
          if(ngara==null || "".equals(ngara))
            chiave=codgar;
          if(index_genere==1){
            chiave = (String) this.sqlManager.getObject("SELECT min(NGARA) FROM gare WHERE codgar1 = ?", new Object[] {codgar});
          }
          Vector<DataColumn> elencoCampi2 = new Vector<DataColumn>();
          elencoCampi2.add(new DataColumn("PUBG.NGARA",
              new JdbcParametro(JdbcParametro.TIPO_TESTO, chiave)));
          elencoCampi2.add(new DataColumn("PUBG.NPUBG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,null)));
          elencoCampi2.add(new DataColumn("PUBG.TIPPUBG",
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(12))));
          elencoCampi2.add(new DataColumn("PUBG.DINPUBG",
              new JdbcParametro(JdbcParametro.TIPO_DATA, datpub)));


          DataColumnContainer container2 = new DataColumnContainer(elencoCampi2);
          // si predispone il gestore per l'aggiornamento dell'entità
          DefaultGestoreEntitaChiaveNumerica gestore2 = new DefaultGestoreEntitaChiaveNumerica(
              "PUBG", "NPUBG",new String[] { "NGARA" }, this.getRequest());

          container2.getColumn("PUBG.NPUBG").setValue(
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null));
          gestore2.inserisci(status, container2);
          break;
        case 3:
          integrazioneWSDM = UtilityStruts.getParametroString(this.getRequest(),"integrazioneWSDM");
          codEvento = "GA_PUBBLICA_INVITO";
          descrEvento = "Pubblicazione gara su area riservata di Portale Appalti";

          //Se la data inserita è >= rispetto a quella inserita nella popup, allora si deve bloccare il salvataggio
          Date data1 = null;
          Vector<?> datiTorn=null;
          try {
            datiTorn = this.sqlManager.getVector("select dteoff,gartel,dtepar from torn where codgar=?", new Object[]{codgar});
          } catch (SQLException e) {
            this.getRequest().setAttribute("pubblicazioneEseguita", "Errori");
            throw new GestoreException("Errore nella lettura della data TORN.DTEOFF ", null, e);
          }

          if(datiTorn!=null && datiTorn.size()>0){
            dteoff = SqlManager.getValueFromVectorParam(datiTorn, 0).dataValue();
            gartel = SqlManager.getValueFromVectorParam(datiTorn, 1).getStringValue();
            dtepar = SqlManager.getValueFromVectorParam(datiTorn, 2).dataValue();

            if("1".equals(integrazioneWSDM) && "1".equals(gartel)){
              codEvento = "GA_PUBBLICA_INVITO_PRO";
              descrEvento = "Pubblicazione gara su area riservata di Portale Appalti con protocollazione in corso";
            }

            if(datpub != null){
              data1 = new Date(datpub.getTime());
            }

            Date data2 = new Date(dteoff.getTime());
            if(data1.compareTo(data2)>0){
              this.getRequest().setAttribute("pubblicazioneEseguita", "Errori");
              livEvento = 3;
              messageKey = "errors.gestoreException.*.erroreDataPubbl.presOfferta";
              errMsgEvento = this.resBundleGenerale.getString(messageKey);
              throw new GestoreException("La data di pubblicazione deve essere precedente o uguale alla data termine per la presentazione dell'offerta ", "erroreDataPubbl.presOfferta", new Exception());
            }
            if("1".equals(gartel)){
              String dataOggi = UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_GG_MM_AAAA);
              Date dataOdierna = UtilityDate.convertiData(dataOggi, UtilityDate.FORMATO_GG_MM_AAAA);
              if(data1.compareTo(dataOdierna)<0){
                this.getRequest().setAttribute("pubblicazioneEseguita", "Errori");
                livEvento = 3;
                messageKey = "errors.gestoreException.*.erroreDataPubbl.dataCorrente";
                errMsgEvento = this.resBundleGenerale.getString(messageKey);
                throw new GestoreException("La data di pubblicazione deve essere successiva o uguale alla data corrente", "erroreDataPubbl.dataCorrente", new Exception());
              }
            }
          }

          //APPALTI-1172: se flag su Procedere con la generazione del pdf delle lavorazioni creare documento con lista lavorazioni e forniture
          if("on".equals(this.getRequest().getParameter("generaPDFLavorazioni"))) {
            String nomeFileExcel;
            try {
              nomeFileExcel = importExportOffertaPrezziManager.exportOffertaPrezzi(ngara, true, false,false,this.getRequest().getSession(),"2");
              if(nomeFileExcel != null && nomeFileExcel.length() > 0){
                File tempExcelFile = new File(System.getProperty("java.io.tmpdir") +
                        File.separator + nomeFileExcel);
                FileInputStream file = new FileInputStream(tempExcelFile);
                XSSFWorkbook workbook = new XSSFWorkbook(file);
                XSSFSheet sheet = workbook.getSheetAt(1);

                //creazione documento
                Document iText_xls_2_pdf = new Document();
                iText_xls_2_pdf.setPageSize(PageSize.A4.rotate());

                ByteArrayOutputStream pdfFile = new ByteArrayOutputStream();

                PdfWriter.getInstance(iText_xls_2_pdf, pdfFile);
                iText_xls_2_pdf.open();

                PdfPTable my_table = new PdfPTable(new float[] {1,2,3,1,1,1,1,1,1,1});
                my_table.setWidthPercentage(95);

                int index_date = -1;
                PdfPCell table_cell;
                BaseColor color = new BaseColor(153, 204, 238); // or red, green, blue, alpha
                Font head=new Font(FontFamily.HELVETICA,8.0f,Font.BOLD,BaseColor.BLUE);
                Font data=new Font(FontFamily.HELVETICA,8.0f,Font.NORMAL,BaseColor.BLACK);
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                for(int i=0; i<sheet.getLastRowNum()-importExportOffertaPrezziManager.FOGLIO_LAVORAZIONE_E_FORNITURE_ULTERIORI_RIGHE_DA_FORMATTARE;i++) {
                  if(i==0 || i >importExportOffertaPrezziManager.FOGLIO_LAVORAZIONE_E_FORNITURE__RIGA_INIZIALE-2) {
                    Chunk c = new Chunk("", data);
                    Row row = sheet.getRow(i);
                    for(int j=0; j<sheet.getRow(0).getLastCellNum();j++) {
                      Cell cell = row.getCell(j);
                      if(cell!=null) {
                        String datoT = "";
                        switch(evaluator.evaluateInCell(cell).getCellType()) {
                          case Cell.CELL_TYPE_STRING:
                            datoT=cell.getStringCellValue();
                            if(i==0 && datoT.contains("Data"))
                              index_date = j;
                            break;
                          case Cell.CELL_TYPE_NUMERIC:
                            Double datoN = cell.getNumericCellValue();
                            datoT = new DecimalFormat("#,##0.00").format(datoN);
                            if(j==index_date) {
                              Date datoD = cell.getDateCellValue();
                              datoT = df.format(datoD);
                            }
                            break;
                        }
                        if(i==0)
                          c = new Chunk(datoT, head);
                        else
                          c = new Chunk(datoT, data);
                      }

                      table_cell=new PdfPCell(new Phrase(c));

                      if(cell!=null) {
                        int alignment=cell.getCellStyle().getAlignment();
                        if(i!=0 && (alignment>0 && alignment<4))
                          table_cell.setHorizontalAlignment(alignment-1);
                      }
                      if(i==0) {
                        table_cell.setBackgroundColor(color);
                        table_cell.setHorizontalAlignment(1);
                        table_cell.setVerticalAlignment(5);
                      }
                      my_table.addCell(table_cell);
                     }
                   }
                }

                Font f=new Font(FontFamily.HELVETICA,16.0f,Font.BOLD,BaseColor.BLACK);
                Paragraph p = new Paragraph("Report lavorazioni e forniture della gara "+ngara, f);
                p.setSpacingAfter(20);
                p.setAlignment(1);
                iText_xls_2_pdf.add(p);
                iText_xls_2_pdf.add(my_table);
                iText_xls_2_pdf.close();

                file.close();

                //inserimento in db
                Long gruppo = 6L;
                Long tipologia = 6L;
                String descrizione = "Report lavorazioni e forniture";
                String allmail = "2";
                String dignomdoc = ngara+"_LavorazioniForniture.pdf";
                Long statodoc = 5L;
                Date datarilascio = data1;

                String insert ="insert into DOCUMGARA(CODGAR, NGARA, NORDDOCG, GRUPPO, TIPOLOGIA, DESCRIZIONE, ALLMAIL, NUMORD, IDPRG, IDDOCDG, STATODOC, DATARILASCIO) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

                Long nProgressivoW_DOCDIG = null;
                Vector<DataColumn> elencoCampiW_DOCDIG =null;

                String digkey1 = codgar;
                //Inserimento in ws_docdig
                String select = "SELECT MAX(iddocdig) FROM w_docdig WHERE idprg = 'PG'";
                nProgressivoW_DOCDIG = (Long) this.sqlManager.getObject(select, null);

                if (nProgressivoW_DOCDIG == null) {
                  nProgressivoW_DOCDIG = new Long(0);
                }

                nProgressivoW_DOCDIG++;

                elencoCampiW_DOCDIG = new Vector<DataColumn>();

                elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.IDPRG",
                    new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")));
                elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.IDDOCDIG",
                                new JdbcParametro(JdbcParametro.TIPO_NUMERICO, nProgressivoW_DOCDIG)));
                elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.DIGENT",
                                new JdbcParametro(JdbcParametro.TIPO_TESTO, "DOCUMGARA")));
                elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.DIGNOMDOC",
                                new JdbcParametro(JdbcParametro.TIPO_TESTO, dignomdoc)));
                elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.DIGOGG",
                                new JdbcParametro(JdbcParametro.TIPO_BINARIO, pdfFile)));

                DataColumnContainer containerW_DOCDIG = new DataColumnContainer(elencoCampiW_DOCDIG);

                containerW_DOCDIG.insert("W_DOCDIG", sqlManager);

                //Inserimento in DOCUMGARA
                Long iddocdg= nProgressivoW_DOCDIG;   //iddocdg

                select="select max(NORDDOCG) from DOCUMGARA where CODGAR = ?";
                Long norddocg = (Long)this.sqlManager.getObject(select, new Object[]{codgar});
                if (norddocg == null) {
                  norddocg = new Long(0);
                }
                norddocg = new Long(norddocg.longValue() + 1);

                select="select max(NUMORD) from DOCUMGARA where CODGAR = ?";
                Long numord = (Long)this.sqlManager.getObject(select, new Object[]{codgar});
                if (numord == null) {
                  numord = new Long(0);
                }
                numord = new Long(numord.longValue() + 1);

                this.sqlManager.update(insert, new Object[] {codgar,ngara,norddocg,gruppo,tipologia,descrizione,allmail,numord,"PG",iddocdg,statodoc,datarilascio});

                this.sqlManager.update(
                    "update W_DOCDIG set DIGKEY1=?, DIGKEY2=? where IDPRG=? and IDDOCDIG=?",
                    new Object[] {digkey1,  norddocg.toString(), "PG", nProgressivoW_DOCDIG });
              }

              pgManagerEst1.ricalcNumordDocGara(codgar, new Long(6));


            } catch (Exception e) {
              this.getRequest().setAttribute("pubblicazioneEseguita", "Errori");
              livEvento = 3;
              errMsgEvento = e.getMessage();
              throw new GestoreException("Errore nella generazione del report di lavorazioni e forniture ", null, e);
            }
          }

          //Si deve inviare la comunicazione
          if("1".equals(gartel)){
            ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
                CostantiGenerali.PROFILO_UTENTE_SESSIONE);
            String numDestinatari = UtilityStruts.getParametroString(this.getRequest(),"numElementiListaDestinatari");
            String numAllegati = UtilityStruts.getParametroString(this.getRequest(),"numElementiListaDoc");

            datiForm.setValue("DATPUB", data1);
            try {
              boolean gestioneTramiteTask= false;
              if("1".equals(integrazioneWSDM))
                gestioneTramiteTask=true;
              this.invioInvito(datiForm, profilo, numDestinatari, numAllegati, integrazioneWSDM,riservatezzaAttiva,gestioneTramiteTask,genPubblicazione,ngara,codgar,iterga,idconfi);
            } catch (SQLException e) {
              this.getRequest().setAttribute("pubblicazioneEseguita", "Errori");
              livEvento = 3;
              errMsgEvento = e.getMessage();
              throw new GestoreException("Errore nell'invio dell'invito", null, e);
            } catch (IOException e) {
              this.getRequest().setAttribute("pubblicazioneEseguita", "Errori");
              livEvento = 3;
              errMsgEvento = e.getMessage();
              throw new GestoreException("Errore nell'invio dell'invito", null, e);
            }catch(GestoreException e){
              this.getRequest().setAttribute("pubblicazioneEseguita", "Errori");
              errMsgEvento = e.getMessage();
              livEvento = 3;
              throw e;
            }catch (DocumentException e) {
              this.getRequest().setAttribute("pubblicazioneEseguita", "Errori");
              livEvento = 3;
              errMsgEvento = e.getMessage();
              throw new GestoreException("Errore nell'invio dell'invito", null, e);
            }
          }else{
            garavviso = UtilityStruts.getParametroString(this.getRequest(),"garavviso");
            if(!"1".equals(garavviso)){
              gestioneFascicoloWSDM = UtilityStruts.getParametroString(this.getRequest(),"gestioneFascicoloWSDM");
              if("1".equals(gestioneFascicoloWSDM)){
                try {
                  Timestamp data = this.getData(iterga, dteoff, dtepar);
                  this.associazioneFascicoloWSDM(genPubblicazione,ngara,codgar,iterga,datpub,data,riservatezzaAttiva,null,idconfi);
                } catch (SQLException e) {
                  this.getRequest().setAttribute("pubblicazioneEseguita", "Errori");
                  livEvento = 3;
                  errMsgEvento = "Errore nella creazione del Fascicolo";
                  throw new GestoreException("Errore nella creazione del Fascicolo", null, e);
                } catch (IOException e) {
                  this.getRequest().setAttribute("pubblicazioneEseguita", "Errori");
                  livEvento = 3;
                  errMsgEvento = "Errore nella creazione del Fascicolo";
                  throw new GestoreException("Errore nella creazione del Fascicolo", null, e);
                }catch (DocumentException e) {
                  this.getRequest().setAttribute("pubblicazioneEseguita", "Errori");
                  livEvento = 3;
                  errMsgEvento = "Errore nella creazione del Fascicolo";
                  throw new GestoreException("Errore nella creazione del Fascicolo", null, e);
                }
              }
            }
          }
          Long tippub = new Long(13);
          if("1".equals(integrazioneWSDM) && "1".equals(gartel))
            tippub = new Long(23);
          Vector<DataColumn> elencoCampi3 = new Vector<DataColumn>();
          elencoCampi3.add(new DataColumn("PUBBLI.CODGAR9",
              new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar)));
          elencoCampi3.add(new DataColumn("PUBBLI.NUMPUB", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,null)));
          elencoCampi3.add(new DataColumn("PUBBLI.TIPPUB",
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, tippub)));
          elencoCampi3.add(new DataColumn("PUBBLI.DATPUB",
              new JdbcParametro(JdbcParametro.TIPO_DATA, datpub)));
          DataColumnContainer container3 = new DataColumnContainer(elencoCampi3);
          // si predispone il gestore per l'aggiornamento dell'entità
          DefaultGestoreEntitaChiaveNumerica gestore3 = new DefaultGestoreEntitaChiaveNumerica(
              "PUBBLI", "NUMPUB",new String[] { "CODGAR9" }, this.getRequest());

          container3.getColumn("PUBBLI.NUMPUB").setValue(
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null));
          gestore3.inserisci(status, container3);
          if("1".equals(gartel)){
            try {
              String chiaveTmp = ngara;
              if(ngara==null || "".equals(ngara))
                chiaveTmp=codgar;
              this.sqlManager.update("update GARE set FASGAR = 1, STEPGAR = 10 where NGARA=?",new Object[]{chiaveTmp});
              this.sqlManager.update("update DITG set INVGAR = '1' where NGARA5 = ? and INVGAR is null and (FASGAR > -3 or FASGAR is null)",new Object[]{chiaveTmp});
            } catch (SQLException e) {
              this.getRequest().setAttribute("pubblicazioneEseguita", "Errori");
              throw new GestoreException("Errore nell'aggiornamento della gara", null, e);
            }
          }
          this.cancellazioneDatiNonUtilizzati(ngara, codgar, genPubblicazione, iterga);

          if(gestioneProgrammazioneManager.isAttivaIntegrazioneProgrammazione()){
            this.gestioneProgrammazioneManager.aggiornaRdaGara(codgar,null,null);
          }

          break;
        case 4:
          Vector<DataColumn> elencoCampi4 = new Vector<DataColumn>();
             elencoCampi4.add(new DataColumn("PUBG.NGARA",
                 new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
             elencoCampi4.add(new DataColumn("PUBG.NPUBG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,null)));
             elencoCampi4.add(new DataColumn("PUBG.TIPPUBG",
                 new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(14))));
             elencoCampi4.add(new DataColumn("PUBG.DINPUBG",
                 new JdbcParametro(JdbcParametro.TIPO_DATA, datpub)));


             DataColumnContainer container4 = new DataColumnContainer(elencoCampi4);
             // si predispone il gestore per l'aggiornamento dell'entità
             DefaultGestoreEntitaChiaveNumerica gestore4 = new DefaultGestoreEntitaChiaveNumerica(
                 "PUBG", "NPUBG",new String[] { "NGARA" }, this.getRequest());

             container4.getColumn("PUBG.NPUBG").setValue(
                 new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null));
             gestore4.inserisci(status, container4);
             break;
        case 5:
          integrazioneWSDM = UtilityStruts.getParametroString(this.getRequest(),"integrazioneWSDM");
          codEvento = "GA_PUBBLICA_INVITO_INCORSO";
          descrEvento = "Invio invito a gara in corso";
          livEvento = 1;
          errMsgEvento = "";

          //Si deve inviare la comunicazione

          ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
              CostantiGenerali.PROFILO_UTENTE_SESSIONE);
          String numDestinatari = UtilityStruts.getParametroString(this.getRequest(),"numElementiListaDestinatari");
          String numAllegati = UtilityStruts.getParametroString(this.getRequest(),"numElementiListaDoc");

          try {

            datiTorn = this.sqlManager.getVector("select dteoff,gartel,dtepar from torn where codgar=?", new Object[]{codgar});
            if(datiTorn!=null && datiTorn.size()>0){
              dteoff = SqlManager.getValueFromVectorParam(datiTorn, 0).dataValue();
              gartel = SqlManager.getValueFromVectorParam(datiTorn, 1).getStringValue();
              dtepar = SqlManager.getValueFromVectorParam(datiTorn, 2).dataValue();
            }

            boolean gestioneTramiteTask= false;
            if("1".equals(integrazioneWSDM))
              gestioneTramiteTask=true;
            if(numDestinatari!=null && !"".equals(numDestinatari)){
              int numDest = (new Long(numDestinatari)).intValue();
              descrEvento += " (cod.: ";
              for(int i= 0; i < numDest; i++){
                descrEvento += datiForm.getString("DITTA_" + i);
                if(i < numDest-1)
                  descrEvento +=", ";
              }
              if(descrEvento.length()>=500)
                descrEvento = descrEvento.substring(0, 499);
              descrEvento += ")";
            }

            this.invioInvito(datiForm, profilo, numDestinatari, numAllegati, integrazioneWSDM,riservatezzaAttiva,gestioneTramiteTask,genPubblicazione,ngara,codgar,iterga,idconfi);
          } catch (Exception e) {
            this.getRequest().setAttribute("pubblicazioneEseguita", "Errori");
            livEvento = 3;
            errMsgEvento = e.getMessage();
            throw new GestoreException("Errore nell'invio dell'invito", null, e);
          }


          try {
            this.sqlManager.update("update DITG set AMMGAR = null, INVGAR = '1' where CODGAR5 = ? and ACQUISIZIONE = 9 and AMMGAR = 2",new Object[]{codgar});
          } catch (SQLException e) {
            this.getRequest().setAttribute("pubblicazioneEseguita", "Errori");
            livEvento = 3;
            errMsgEvento = e.getMessage();
            throw new GestoreException("Errore nell'aggiornamento della gara", null, e);
          }
        }


        //Quando non si è nel caso di invio invito con gara in corso, va popolata CHIAVIBUSTE e gestire l'aggiornamento dello stato della documentazione
        if(!"5".equals(bando)) {
          // TODO: va gestito inserendo le opportune condizioni per ogni busta prevista, eventualmente anche differenziando le buste per i singoli lotti
          String descTabellato = null;
          boolean esisteQform = false;

          String chiaveTmp = ngara;
          if(ngara==null || "".equals(ngara))
            chiaveTmp=codgar;

          HttpSession session = this.getRequest().getSession();
          String profiloAttivo = (String) session.getAttribute("profiloAttivo");
          boolean moduloQFORMAttivo = geneManager.getProfili().checkProtec(profiloAttivo, "FUNZ", "VIS", "ALT.GENEWEB.QuestionariQForm");

          if("1".equals(gartel)){
            descTabellato = tabellatiManager.getDescrTabellato("A1122", "1");
            if(descTabellato== null || "".equals(descTabellato))
              descTabellato="2";
            else
              descTabellato = descTabellato.substring(0, 1);

            if("1".equals(descTabellato)){
              //Si inserisce una sola occorrenza in CHIAVIBUSTE per ogni tipo di busta

              if("1".equals(bando) && ("2".equals(iterga) || "4".equals(iterga) || "7".equals(iterga))){
                String pwd = datiForm.getString("PWD_A0");
                String pin = pwd + chiaveTmp;
                cifraturaBusteManager.inserisciBustaInChiavibuste(chiaveTmp, pin, "FS10A");
              }else{
                String pwdA = datiForm.getString("PWD_A");
                String pinA = null;
                if(pwdA!=null && !"".equals(pwdA))
                  pinA = pwdA + chiaveTmp;
                String pwdB = datiForm.getString("PWD_B");
                String pinB = null;
                if(pwdB!=null && !"".equals(pwdB))
                  pinB = pwdB + chiaveTmp;
                String pwdC = datiForm.getString("PWD_C");
                String pinC = null;
                if(!"true".equals(soloPunteggiTec))
                  pinC = pwdC + chiaveTmp;

                cifraturaBusteManager.popolaChiavibuste(chiaveTmp, pinA, pinB, pinC);
              }
            }

            //Si controlla l'esistenza delle occorrenze di qform se il modulo risulta attivo


            if(moduloQFORMAttivo) {
              boolean formularioCompletoAttivo = geneManager.getProfili().checkProtec(profiloAttivo, "FUNZ", "VIS", PgManagerEst1.QFORM_VOCEPROFILO_TUTTE_BUSTE);
              if(moduloQFORMAttivo && formularioCompletoAttivo ) {
                Long offtel =(Long)this.sqlManager.getObject("select offtel from torn where codgar=?", new Object[]{codgar});
                if(new Long(3).equals(offtel))
                  formularioCompletoAbilitato = true;
              }
              Long conteggio = null;
              String selectQfrom = "select count(*) from qform where entita='GARE' and key1=? and busta=? and stato = 1";
              if(("2".equals(iterga) || "4".equals(iterga) || "7".equals(iterga)) && !"3".equals(bando)){
                conteggio = (Long)this.sqlManager.getObject(selectQfrom, new Object[] {chiaveTmp, new Long(4)});
              }else {
                if(!formularioCompletoAbilitato)
                  conteggio = (Long)this.sqlManager.getObject(selectQfrom, new Object[] {chiaveTmp, new Long(1)});
                else {

                  if(new Long(3).equals(genere)) {
                    selectQfrom = "select count(*) from qform where entita='GARE' and key1=? and busta = ? and stato = 1";
                    Long conteggioAmm = (Long)this.sqlManager.getObject(selectQfrom, new Object[] {codgar, new Long(1)});
                    if(conteggioAmm!=null) {
                      conteggio = new Long(conteggioAmm.longValue());
                    }else {
                      selectQfrom = "select count(*) from qform where entita='GARE' and key1 like ? and busta in (?,?) and stato = 1";
                      Long conteggioTecEco = (Long)this.sqlManager.getObject(selectQfrom, new Object[] {codgar + "%", new Long(2), new Long(3)});
                      if(conteggioTecEco!=null)
                        conteggio = new Long(conteggioTecEco.longValue());
                    }
                  }else {
                    selectQfrom = "select count(*) from qform where entita='GARE' and key1=? and busta in (?,?,?) and stato = 1";
                    conteggio = (Long)this.sqlManager.getObject(selectQfrom, new Object[] {chiaveTmp, new Long(1), new Long(2), new Long(3)});
                  }
                }
              }
              if(conteggio !=null && conteggio.longValue()>0)
                esisteQform = true;
            }
          }else if((index_genere == 10 || index_genere==20) && moduloQFORMAttivo) {
            String selectQfrom = "select count(*) from qform where entita='GARE' and key1=? and stato = 1";
            Long conteggio = (Long)this.sqlManager.getObject(selectQfrom, new Object[] {chiaveTmp});
            if(conteggio !=null && conteggio.longValue()>0)
              esisteQform = true;
          }


          //Si deve aggiornare lo stato della documentazione di gara, impostando
          // STATODOC.DOCUMGARA=5
          try {
            String filtro="";
            Object[] parametri=null;
            if("0".equals(bando)){
                parametri =new Object[]{codgar,new Long(4),new Long(10),new Long(15)};
                filtro="where CODGAR=? and (GRUPPO = ? or GRUPPO = ? or GRUPPO = ?)";
                this.sqlManager.update("update DOCUMGARA set STATODOC = 5 " + filtro, parametri);
            }else if("3".equals(bando)){
              parametri =new Object[]{codgar,new Long(6), new Long(15),new Long(10),new Long(3)};
              filtro="where CODGAR=? and (GRUPPO = ? or GRUPPO = ? or GRUPPO = ? or GRUPPO = ?)";
              this.sqlManager.update("update DOCUMGARA set STATODOC = 5 " + filtro,parametri);
              if(esisteQform) {
                if(formularioCompletoAbilitato && new Long(3).equals(genere)) {
                  //Si devono considerare i lotti per il qform tecnico e quello economico
                  this.sqlManager.update("update QFORM set STATO = 5, datpub=? where entita='GARE' and key1=? and busta=1 and stato = 1",new Object[] {datpub, codgar});
                  this.sqlManager.update("update QFORM set STATO = 5, datpub=? where entita='GARE' and key1 like ? and (busta=2 or busta=3 ) and stato = 1",new Object[] {datpub, codgar + "%"});
                }else
                  this.sqlManager.update("update QFORM set STATO = 5, datpub=? where entita='GARE' and key1=? and (busta=1 or busta=2 or busta=3) and stato = 1",new Object[] {datpub, chiaveTmp});
              }
            }else{
              parametri =new Object[]{codgar,new Long(1),new Long(2),new Long(10),new Long(15),new Long(3)};
              if("2".equals(iterga) || "4".equals(iterga) || "7".equals(iterga)){
                filtro="where CODGAR=? and (GRUPPO = ? or GRUPPO = ? or GRUPPO = ? or GRUPPO = ? or (GRUPPO = ? and BUSTA = 4))";
              }else{
                  filtro="where CODGAR=? and (GRUPPO = ? or GRUPPO = ? or GRUPPO = ? or GRUPPO = ? or GRUPPO = ?)";
              }
              this.sqlManager.update("update DOCUMGARA set STATODOC = 5 " + filtro, parametri);
              if(esisteQform) {
                String updateQform = "update QFORM set STATO = 5, datpub=? where entita='GARE' and key1=? and busta=?";
                if("2".equals(iterga) || "4".equals(iterga) || "7".equals(iterga)){
                  this.sqlManager.update(updateQform,new Object[] {datpub, chiaveTmp, new Long(4)});
                }else {
                  if(formularioCompletoAbilitato && new Long(3).equals(genere)) {
                    //Si devono considerare i lotti per il qform tecnico e quello economico
                    this.sqlManager.update("update QFORM set STATO = 5, datpub=? where entita='GARE' and key1=? and busta = 1",new Object[] {datpub, codgar});
                    this.sqlManager.update("update QFORM set STATO = 5, datpub=? where entita='GARE' and key1 like ? and (busta=2 or busta=3 ) ",new Object[] {datpub, codgar + "%"});
                  }else if(index_genere == 10 || index_genere==20) {
                    updateQform = "update QFORM set STATO = 5, datpub=? where entita='GARE' and key1=?";
                    this.sqlManager.update(updateQform,new Object[] {datpub, chiaveTmp});
                  }else {
                    updateQform = "update QFORM set STATO = 5, datpub=? where entita='GARE' and key1=? and busta in (?,?,?)";
                    this.sqlManager.update(updateQform,new Object[] {datpub, chiaveTmp, new Long(1), new Long(2), new Long(3)});
                  }
                }
              }
            }
            filtro+=" and DATARILASCIO is null";
            Object[] newParametri = new Object[parametri.length + 1];
            newParametri[0]= datpub;
            for(int i=0;i<parametri.length;i++)
              newParametri[i+1]=parametri[i];
            this.sqlManager.update("update DOCUMGARA set DATARILASCIO = ? " + filtro, newParametri);

          } catch (SQLException e) {
            throw new GestoreException("Errore nell'aggiornamento dello stato dei documenti della gara", null, e);
          }

          try {
            this.sqlManager.update("update TORN set DULTAGG = ? where CODGAR=?",new Object[]{datpub,codgar});
          } catch (SQLException e) {
            throw new GestoreException("Errore nell'aggiornamento del campo TORN.DULTAGG", null, e);
          }

          //Se gara non OEPV si deve resettare il valore di GARE1.COSTOFISSO
          this.sqlManager.update("update gare1 set costofisso=null where gare1.ngara in (select g.ngara from gare g where gare1.ngara=g.ngara and g.codgar1=? and g.ngara!=g.codgar1 and g.modlicg!=6)", new Object[] {codgar});

          livEvento = 1;
          errMsgEvento = "";
          // setta l'operazione a completata, in modo da scatenare il reload della
          // pagina principale
          String chiave = codgar;
          if(ngara != null && ngara.length() > 0){
            chiave = ngara;
          }
          //this.getRequest().setAttribute("pubblicazioneEseguita", "1");
          if("1".equals(gartel) && "1".equals(descTabellato)){
            this.getRequest().setAttribute("codiceGara", chiave);
            ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
                CostantiGenerali.PROFILO_UTENTE_SESSIONE);
            this.getRequest().setAttribute("username", profilo.getNome());
            this.getRequest().setAttribute("usernameId", profilo.getId());
            String oggettoGara = (String) sqlManager.getObject("SELECT oggetto FROM v_gare_torn WHERE codgar=?", new Object[] {codgar});
            this.getRequest().setAttribute("oggettoGara", oggettoGara);
            String nomeTecnico = (String) sqlManager.getObject("SELECT NOMTEC FROM TECNI,TORN WHERE TORN.CODGAR = ? AND  CODTEC = TORN.CODRUP", new Object[] {codgar});
            this.getRequest().setAttribute("nomeTecnico", nomeTecnico);
            if(datiForm.isColumn("PWD_A0")){
              String pwdA0 = datiForm.getString("PWD_A0");
              this.getRequest().setAttribute("PWD_A0", pwdA0);
            }
            if(datiForm.isColumn("PWD_A")){
              String pwdA = datiForm.getString("PWD_A");
              this.getRequest().setAttribute("PWD_A", pwdA);
            }
            if(datiForm.isColumn("PWD_B")){
              String pwdB = datiForm.getString("PWD_B");
              this.getRequest().setAttribute("PWD_B", pwdB);
            }
            if(datiForm.isColumn("PWD_C")){
              String pwdC = datiForm.getString("PWD_C");
              this.getRequest().setAttribute("PWD_C", pwdC);
            }
            String url = this.getRequest().getRequestURL().toString();
            this.getRequest().setAttribute("url", url.substring(0, url.lastIndexOf("/")));
          }
        }
        this.getRequest().setAttribute("pubblicazioneEseguita", "1");
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      throw new GestoreException("Errore nell'aggiornamento dello stato dei documenti della gara", null, e);
    } finally{
      if(index_bando==0 || index_bando==1 || index_bando==3 || index_bando == 5){
        //Tracciatura eventi
        try {
          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(oggEvento);
          logEvento.setCodEvento(codEvento);
          logEvento.setDescr(descrEvento);
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          messageKey = "errors.logEventi.inaspettataException";
          logger.error(this.resBundleGenerale.getString(messageKey), le);
        }
      }
    }
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  //Viene inserita la comunicazione con stato=2
  public void invioInvito(DataColumnContainer datiForm, ProfiloUtente profilo, String numDestinatari, String numAllegati, String integrazioneWSDM, String riservatezzaAttiva,
      boolean gestioneTramiteTask,Long genereGara, String ngara,String codgar, String iterga, String idconfi) throws GestoreException, SQLException, IOException, DocumentException {

    String tipoWSDM = null;
    boolean abilitatoInvioMailDocumentale = false;
    boolean abilitatoInvioSingolo = false;
    String gestioneFascicoloWSDM="2";
    String oggettoDocumentoFittizio=null;

    if("1".equals(integrazioneWSDM)){
      abilitatoInvioMailDocumentale = this.gestioneWSDMManager.abilitatoInvioMailDocumentale("FASCICOLOPROTOCOLLO",idconfi);

      WSDMConfigurazioneOutType configurazione = gestioneWSDMManager.wsdmConfigurazioneLeggi("FASCICOLOPROTOCOLLO",idconfi);
      if (configurazione.isEsito())
        tipoWSDM = configurazione.getRemotewsdm();

      if(GestioneWSDMManager.getAbilitazioneInvioSingolo(idconfi))
        abilitatoInvioSingolo=true;

      if(gestioneTramiteTask){
        //gestioneFascicoloWSDM vale 1 quando è attiva la fascicolazione e non esiste il fascicolo in wsfascicolo
        // quindi si deve creare il fascicolo prima della gestione dell'invito
        gestioneFascicoloWSDM = UtilityStruts.getParametroString(this.getRequest(),"gestioneFascicoloWSDM");
        if("PALEO".equals(tipoWSDM) || "TITULUS".equals(tipoWSDM) || "FOLIUM".equals(tipoWSDM))
          oggettoDocumentoFittizio="Apertura fascicolo";
      }

    }

    if(abilitatoInvioSingolo){
      if(numDestinatari!=null && !"".equals(numDestinatari)){
        int numDest = (new Long(numDestinatari)).intValue();
        boolean insDocGareInWsallegati= true;
        Long idOccorrenzaTask=null;
        //L'occorrenza in GARPRO_WSDM va creata una sola volta
        boolean creazioneOccorrenzaTask=true;
        for(int i=0; i<numDest; i++){
          if(i==0 && gestioneTramiteTask && "1".equals(gestioneFascicoloWSDM))
            this.associazioneFascicoloWSDM(genereGara, ngara, codgar, iterga, null, null, riservatezzaAttiva,oggettoDocumentoFittizio,idconfi);
          else if(i>0){
            insDocGareInWsallegati= false;
            creazioneOccorrenzaTask=false;
          }

          idOccorrenzaTask=gestioneInvioInvito(datiForm,profilo,"1",i,numAllegati,integrazioneWSDM, tipoWSDM, abilitatoInvioMailDocumentale, abilitatoInvioSingolo,insDocGareInWsallegati,
              riservatezzaAttiva, gestioneTramiteTask,creazioneOccorrenzaTask,idconfi,idOccorrenzaTask);
        }
      }
    }else{
      if(gestioneTramiteTask && "1".equals(gestioneFascicoloWSDM))
        this.associazioneFascicoloWSDM(genereGara, ngara, codgar, iterga, null, null, riservatezzaAttiva,oggettoDocumentoFittizio,idconfi);

      gestioneInvioInvito(datiForm,profilo,numDestinatari,0,numAllegati,integrazioneWSDM, tipoWSDM, abilitatoInvioMailDocumentale, abilitatoInvioSingolo,true,riservatezzaAttiva,gestioneTramiteTask, true,idconfi,null);
    }
  }

  /**
   * Il metodo effettua l'invio invito. Nel caso in cui sia attiva l'integrazione col WSDM, si fa distinzione fra invio tramite task o invio diretto.
   * Nel caso di invio tramite task i dati necessari all'invio da parte del task vengono memorizzati nella tabella garpro_wsdm ed il riferimento alle
   * occorrenze di questa tabella vanno memorizzati nel campo idgarpro di W_INVCOM.
   * Nel caso di invio per singolo destinatario, si deve inserire una sola occorrenza in GARPRO_WSDM e tutte le occorrenze di W_INVCOM devono puntare
   * a questa unica occorrenza. Per mantenere l'informazione dell'id di GARPRO_WSDM per tutti i destinatari il metodo restituisce tale id
   * @param datiForm
   * @param profilo
   * @param numDestinatari
   * @param indiceDestinatario
   * @param numAllegati
   * @param integrazioneWSDM
   * @param tipoWSDM
   * @param abilitatoInvioMailDocumentale
   * @param abilitatoInvioSingolo
   * @param insDocGareInWsallegati
   * @param riservatezzaAttiva
   * @param gestioneTask
   * @param creazioneOccorrenzaTask
   * @param idconfi
   * @return Long
   * @throws SQLException
   * @throws GestoreException
   * @throws IOException
   * @throws DocumentException
   */
  private Long gestioneInvioInvito(DataColumnContainer datiForm, ProfiloUtente profilo, String numDestinatari, int indiceDestinatario, String numAllegati,
      String integrazioneWSDM, String tipoWSDM, boolean abilitatoInvioMailDocumentale, boolean abilitatoInvioSingolo, boolean insDocGareInWsallegati,  String riservatezzaAttiva,
      boolean gestioneTask, boolean creazioneOccorrenzaTask, String idconfi, Long idOccorrenzaTask) throws SQLException, GestoreException, IOException, DocumentException{

    FileAllegatoManager fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager",
        this.getServletContext(), FileAllegatoManager.class);

    Long idRet=null;

    //Inserimento in W_INVCOM
    Object[] parametri = new Object[15];
    String idprg = "PG";
    parametri[0] = idprg;       //IDPRG
    Long newIdcom = (Long) sqlManager.getObject("SELECT max(idcom) FROM w_invcom WHERE idprg=?", new Object[] {idprg});
    if (newIdcom == null) {
        newIdcom = new Long(0);
    }
    newIdcom = newIdcom + 1;
    parametri[1] = newIdcom;    //IDCOM
    String ngara = datiForm.getString("NGARA");
    Long genere = (Long) sqlManager.getObject("select genere from V_GARE_TORN where codgar = ?", new Object[]{datiForm.getString("CODGAR")});
    if(ngara==null || "".equals(ngara) || (genere!=null && genere.longValue()==3)){
      parametri[2] = "TORN";  //COMENT
      parametri[3] = datiForm.getString("CODGAR");   //COMKEY1
    }else{
      parametri[2] = "GARE";  //COMENT
      parametri[3] = datiForm.getString("NGARA");   //COMKEY1
    }
    parametri[4] = (long) profilo.getId(); //COMCODOPE
    Timestamp dataOdierna = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
    parametri[5] = dataOdierna; //COMDATINS
    parametri[6] = datiForm.getString("COMMITT"); //COMMITT
    String stato ="2";
    if(gestioneTask)
      stato ="14";
    parametri[7] = stato; //COMSTATO
    parametri[8] = datiForm.getString("COMINTEST"); //COMINTEST
    parametri[9] = datiForm.getString("COMMSGOGG");    //COMMSGOGG
    parametri[10] = datiForm.getString("COMMSGTES");  //COMMSGTES
    parametri[11] = (long) 2;   //COMPUB -- riservata
    parametri[12] = datiForm.getString("COMMSGTIP");
    parametri[13] = "1"; //COMNORISPONDI
    String uffintAbilitato = (String)this.getRequest().getSession().getAttribute("uffint");
    uffintAbilitato = UtilityStringhe.convertiNullInStringaVuota(uffintAbilitato);
    if("".equals(uffintAbilitato)){
      uffintAbilitato = CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD;
    }
    String cenint = null;
    //Valorizzazione di IDCFG
    try {
      if(ngara==null || "".equals(ngara) || (genere!=null && genere.longValue()==3)){
        cenint = (String)sqlManager.getObject("select t.cenint from torn t where t.codgar = ?", new Object[]{datiForm.getString("CODGAR")});
      }else{
        cenint = (String)sqlManager.getObject("select t.cenint from gare g,torn t where g.codgar1 = t.codgar and ngara = ?", new Object[]{ngara});
      }
      cenint = UtilityStringhe.convertiNullInStringaVuota(cenint);
      if(!"".equals(cenint)){
        parametri[14] = cenint;
      }else{
        parametri[14] = uffintAbilitato;
      }
    } catch (SQLException sqle) {
      throw new GestoreException("Errore nella determinazione di W_INVCOM.IDCFG",null, sqle);
    }

    this.sqlManager.update("INSERT INTO w_invcom(IDPRG,IDCOM,COMENT,COMKEY1,COMCODOPE,COMDATINS,"
        + "COMMITT,COMSTATO,COMINTEST,COMMSGOGG,COMMSGTES,COMPUB,COMMSGTIP,COMNORISPONDI,IDCFG)"
        + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", parametri);

    WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = null;
    WSDMProtocolloAnagraficaType[] destinatari = null;
    WSDMProtocolloAllegatoType[] allegati = null;
    //Nel caso di EASYDOC mi servono solamante gli allegati effettivi, senza il testo della comunicazione
    //WSDMProtocolloAllegatoType[] allegatiReali = null;
    Long idAllegatiNuovi[]= null;
    String inserimentoinfascicolo = null;
    String classificafascicolo = null;
    String oggettodocumento = null;
    String inout = null;
    String mezzoinvio = null;
    String codiceaoo = null;
    String codiceufficio = null;
    String struttura = null;
    String codicefascicolo = null;
    String annofascicolo = null;
    String numerofascicolo = null;
    Long isRiservatezza = null;
    String codiceregistrodocumento = null;
    String tipodocumento = null;
    String mittenteinterno = null;
    String idtitolazione = null;
    String idindice = null;
    String idunitaoperativamittente = null;
    String mezzo = null;
    String supporto = null;
    String livelloriservatezza = null;
    String classificadocumento = null;
    String classificadescrizione = null;
    String voce = null;
    String codiceaoodes = null;
    String codiceufficiodes = null;
    String sottotipo = null;
    String indirizzomittente = null;
    String RUP = null;
    String nomeRup = null;
    String acronimoRup = null;
    String cig = null;
    String uocompetenza = null;
    String uocompetenzadescrizione = null;
    if("1".equals(integrazioneWSDM)){
      //Popolamento Documento WSDM
      classificadocumento = UtilityStruts.getParametroString(this.getRequest(),"classificadocumento");
      tipodocumento = UtilityStruts.getParametroString(this.getRequest(),"tipodocumento");
      oggettodocumento = UtilityStruts.getParametroString(this.getRequest(),"oggettodocumento");
      //String descrizionedocumento = UtilityStruts.getParametroString(this.getRequest(),"descrizionedocumento");
      String descrizionedocumento = null;
      mittenteinterno = UtilityStruts.getParametroString(this.getRequest(),"mittenteinterno");
      indirizzomittente = UtilityStruts.getParametroString(this.getRequest(),"indirizzomittente");
      mezzoinvio = UtilityStruts.getParametroString(this.getRequest(),"mezzoinvio");
      mezzo = UtilityStruts.getParametroString(this.getRequest(),"mezzo");
      codiceregistrodocumento = UtilityStruts.getParametroString(this.getRequest(),"codiceregistrodocumento");
      inout = UtilityStruts.getParametroString(this.getRequest(),"inout");
      idindice = UtilityStruts.getParametroString(this.getRequest(),"idindice");
      idtitolazione = UtilityStruts.getParametroString(this.getRequest(),"idtitolazione");
      idunitaoperativamittente = UtilityStruts.getParametroString(this.getRequest(),"idunitaoperativamittente");
      inserimentoinfascicolo = UtilityStruts.getParametroString(this.getRequest(),"inserimentoinfascicolo");
      codicefascicolo = UtilityStruts.getParametroString(this.getRequest(),"codicefascicolo");
      String oggettofascicolo = UtilityStruts.getParametroString(this.getRequest(),"oggettofascicolonuovo");
      classificafascicolo = UtilityStruts.getParametroString(this.getRequest(),"classificafascicolonuovo");
      String descrizionefascicolo = UtilityStruts.getParametroString(this.getRequest(),"descrizionefascicolonuovo");
      annofascicolo = UtilityStruts.getParametroString(this.getRequest(),"annofascicolo");
      numerofascicolo = UtilityStruts.getParametroString(this.getRequest(),"numerofascicolo");
      codiceaoo =   UtilityStruts.getParametroString(this.getRequest(),"codiceaoonuovo");
      String societa =  UtilityStruts.getParametroString(this.getRequest(),"societa");
      String codiceGaralotto = UtilityStruts.getParametroString(this.getRequest(),"codicegaralotto");
      cig = UtilityStruts.getParametroString(this.getRequest(),"cig");
      codiceufficio  =   UtilityStruts.getParametroString(this.getRequest(),"codiceufficionuovo");
      String numeroallegati =  UtilityStruts.getParametroString(this.getRequest(),"numElementiListaDoc");
      supporto = UtilityStruts.getParametroString(this.getRequest(),"supporto");
      struttura = UtilityStruts.getParametroString(this.getRequest(),"strutturaonuovo");
      String tipofascicolo = UtilityStruts.getParametroString(this.getRequest(),"tipofascicolonuovo");
      livelloriservatezza = UtilityStruts.getParametroString(this.getRequest(),"livelloriservatezza");
      classificadescrizione = UtilityStruts.getParametroString(this.getRequest(),"classificadescrizione");
      voce = UtilityStruts.getParametroString(this.getRequest(),"voce");
      codiceaoodes = UtilityStruts.getParametroString(this.getRequest(),"codiceaoodes");
      codiceufficiodes = UtilityStruts.getParametroString(this.getRequest(),"codiceufficiodes");
      sottotipo = UtilityStruts.getParametroString(this.getRequest(),"sottotipo");
      RUP = UtilityStruts.getParametroString(this.getRequest(),"RUP");
      nomeRup = UtilityStruts.getParametroString(this.getRequest(),"nomeR");
      acronimoRup = UtilityStruts.getParametroString(this.getRequest(),"acronimoR");
      uocompetenza = UtilityStruts.getParametroString(this.getRequest(),"uocompetenza");
      uocompetenzadescrizione = UtilityStruts.getParametroString(this.getRequest(),"uocompetenzadescrizione");

      if("TITULUS".equals(tipoWSDM) && !abilitatoInvioMailDocumentale)
        tipodocumento=GestioneWSDMManager.TIPO_DOCUMENTO_GARA;
      else if("TITULUS".equals(tipoWSDM) && abilitatoInvioMailDocumentale)
        tipodocumento=GestioneWSDMManager.TIPO_DOCUMENTO_GARA_PEC;

      //Per TITULUS si deve sbiancare il contenuto della classifica del fascicolo
      if("TITULUS".equals(tipoWSDM))
        classificafascicolo = null;

      if(abilitatoInvioSingolo && !gestioneTask){
        //Si controlla se esiste l'occorrenza in WSFASCICOLO, se esiste e inserimentoinfascicolo = "SI_FASCICOLO_NUVO" si cambia il valore in
        //"SI"
        String entita=UtilityStruts.getParametroString(this.getRequest(),"entita");
        String key1=UtilityStruts.getParametroString(this.getRequest(),"key1");
        String key2=UtilityStruts.getParametroString(this.getRequest(),"key2");
        String key3=UtilityStruts.getParametroString(this.getRequest(),"key3");
        String key4=UtilityStruts.getParametroString(this.getRequest(),"key4");
        if(this.gestioneWSDMManager.esisteWSFascicolo(entita, key1, key2, key3, key4) && "SI_FASCICOLO_NUOVO".equals(inserimentoinfascicolo)){
          inserimentoinfascicolo = "SI_FASCICOLO_ESISTENTE";
          Long anno = null;

          if(codicefascicolo==null || "".equals(codicefascicolo)){
            Vector<?> datiFascicolo = this.gestioneWSDMManager.getDatiWsfascicolo(entita, key1, key2, key3, key4);
            if(datiFascicolo!=null && datiFascicolo.size()>0){
              codicefascicolo = SqlManager.getValueFromVectorParam(datiFascicolo, 0).stringValue();
              numerofascicolo = SqlManager.getValueFromVectorParam(datiFascicolo, 2).stringValue();
              anno = SqlManager.getValueFromVectorParam(datiFascicolo, 1).longValue();
              if(anno!=null)
                annofascicolo = Long.toString(anno.longValue());

            }
          }

          if(codicefascicolo==null || "".equals(codicefascicolo)){
            String username= UtilityStruts.getParametroString(this.getRequest(),"username");
            String password= UtilityStruts.getParametroString(this.getRequest(),"password");
            String ruolo= UtilityStruts.getParametroString(this.getRequest(),"ruolo");
            String nome= UtilityStruts.getParametroString(this.getRequest(),"nome");
            String cognome= UtilityStruts.getParametroString(this.getRequest(),"cognome");
            String codiceuo= UtilityStruts.getParametroString(this.getRequest(),"codiceuo");
            String idutente= UtilityStruts.getParametroString(this.getRequest(),"idutente");
            String idutenteunop= UtilityStruts.getParametroString(this.getRequest(),"idutenteunop");
            WSDMFascicoloResType wsdmFascicoloRes = this.gestioneWSDMManager.wsdmFascicoloMetadatiLeggi(username, password, ruolo, nome, cognome, codiceuo,
                idutente, idutenteunop, null, anno, numerofascicolo, "FASCICOLOPROTOCOLLO", classificafascicolo,idconfi);
            if (wsdmFascicoloRes.isEsito()) {
              if (wsdmFascicoloRes.getFascicolo() != null) {
                codicefascicolo = wsdmFascicoloRes.getFascicolo().getCodiceFascicolo();
              }
            }
          }

        }
      }else if(gestioneTask && "SI_FASCICOLO_NUOVO".equals(inserimentoinfascicolo)){
        //Se il fascicolo non esisteva, è stato creato immediatemente prima della chiamata di questo metodo, quindi si deve aggiornare
        inserimentoinfascicolo = "SI_FASCICOLO_ESISTENTE";
      }


      HashMap<String,Object> par = new HashMap<String,Object>();
      par.put("classificadocumento", classificadocumento);
      par.put("tipodocumento", tipodocumento);
      par.put("oggettodocumento", oggettodocumento);
      par.put("descrizionedocumento", descrizionedocumento);
      par.put("mittenteinterno", mittenteinterno);
      par.put("codiceregistrodocumento", codiceregistrodocumento);
      par.put("inout", inout);
      par.put("idindice", idindice);
      par.put("idtitolazione", idtitolazione);
      par.put("idunitaoperativamittente", idunitaoperativamittente);
      par.put("inserimentoinfascicolo", inserimentoinfascicolo);
      par.put("codicefascicolo", codicefascicolo);
      par.put("oggettofascicolo", oggettofascicolo);
      par.put("classificafascicolo", classificafascicolo);
      par.put("descrizionefascicolo", descrizionefascicolo);
      par.put("annofascicolo", annofascicolo);
      par.put("numerofascicolo", numerofascicolo);
      par.put("tipoWSDM", tipoWSDM);
      par.put("idprg", idprg);
      par.put("idcom", newIdcom);
      par.put("mezzo", mezzo);
      par.put("societa", societa);
      par.put("codiceGaralotto", codiceGaralotto);
      par.put("cig", cig);
      par.put("numeroallegati", new Long(numeroallegati));
      par.put("struttura", struttura);
      par.put("supporto", supporto);
      par.put("servizio", "FASCICOLOPROTOCOLLO");
      par.put("tipofascicolo", tipofascicolo);
      par.put("classificadescrizione", classificadescrizione);
      par.put("voce", voce);
      par.put(GestioneWSDMManager.LABEL_RUP, RUP);
      par.put(GestioneWSDMManager.LABEL_NOME_RUP, nomeRup);
      par.put(GestioneWSDMManager.LABEL_ACRONIMO_RUP, acronimoRup);
      par.put(GestioneWSDMManager.LABEL_SOTTOTIPO, sottotipo);
      par.put(GestioneWSDMManager.LABEL_UOCOMPETENZA, uocompetenza);

      if("JIRIDE".equals(tipoWSDM) && "1".equals(riservatezzaAttiva)){
        if("SI_FASCICOLO_NUOVO".equals(inserimentoinfascicolo) || "SI_FASCICOLO_ESISTENTE".equals(inserimentoinfascicolo)){
          par.put("livelloriservatezza", livelloriservatezza);
          if(genere != null && genere.intValue() != 10 && genere.intValue() != 11 && genere.intValue() != 20){
            isRiservatezza = new Long(1);
          }
        }
      }
      wsdmProtocolloDocumentoIn = this.gestioneWSDMManager.wsdmProtocolloDocumentoPopola(par,idconfi);
    }

    String destinatarioPrincipale="";
    //Inserimento destinatari in W_INVCOMDES
    if(numDestinatari!=null && !"".equals(numDestinatari)){
      int numDest = (new Long(numDestinatari)).intValue();
      if("1".equals(integrazioneWSDM))
        destinatari = new WSDMProtocolloAnagraficaType[numDest];

      int valorePartenza = 0;
      int valoreFine = numDest;
      if(abilitatoInvioSingolo){
        valorePartenza = indiceDestinatario;
        valoreFine = indiceDestinatario + 1;
      }


      for(int i= valorePartenza; i < valoreFine; i++){
        parametri = new Object[8];
        parametri[0] = idprg;   //IDPRG
        parametri[1] = newIdcom;    //IDCOM
        Long newIdcomdes = (Long) sqlManager.getObject("SELECT max(idcomdes) FROM w_invcomdes WHERE idprg=? AND idcom=?",
            new Object[]{idprg, newIdcom});
        if (newIdcomdes == null) {
            newIdcomdes = new Long(0);
        }
        newIdcomdes = newIdcomdes + 1;
        parametri[2] = newIdcomdes; //IDCOMDES
        parametri[3] = "IMPR";  //DESCODENT
        parametri[4] = datiForm.getString("DITTA_" + i); //DESCODSOG
        parametri[5] = datiForm.getString("MAIL_" + i);    //DESMAIL
        parametri[6] = datiForm.getString("INTESTAZIONE_" + i); //DESINTEST
        String tipo = datiForm.getString("TIPO_" + i);
        Long comtipma = new Long(2);
        if("PEC".equals(tipo))
          comtipma = new Long(1);
        parametri[7] = comtipma;    //COMTIPMA
        sqlManager.update("INSERT INTO w_invcomdes(IDPRG,IDCOM,IDCOMDES,DESCODENT,DESCODSOG,"
            + "DESMAIL,DESINTEST,COMTIPMA) VALUES(?,?,?,?,?,?,?,?)", parametri);

        if("1".equals(integrazioneWSDM)){
          int indiceArrayDestinatari = i;
          //nel caso di PALEO ed invio tramite documentale, si crea una comunicazione per ogni destinarario,
          //quindi esisterà sempre un solo elemento nel vettore destinatari.
          if(abilitatoInvioSingolo){
            indiceArrayDestinatari = 0;
          }
          destinatari[indiceArrayDestinatari] = new WSDMProtocolloAnagraficaType();
          if(!"FOLIUM".equals(tipoWSDM)){
            destinatari[indiceArrayDestinatari].setCodiceFiscale(datiForm.getString("CODFISC_" + i));//Codice fiscale
            destinatari[indiceArrayDestinatari].setPartitaIVA(datiForm.getString("PIVA_" + i));//Partita iva
          }else{
            destinatari[indiceArrayDestinatari].setCodiceFiscale("");
            destinatari[indiceArrayDestinatari].setPartitaIVA("");
          }
          String intestazione = datiForm.getString("INTESTAZIONE_" + i);
          destinatarioPrincipale+=intestazione;
          if(i<valoreFine-1)
            destinatarioPrincipale+= ", ";
          if(intestazione.indexOf("- Mandante")>0 || intestazione.indexOf("- Mandataria")>0){
            String datiRagioneSociale[] = intestazione.split("-");
            if(datiRagioneSociale!= null && datiRagioneSociale.length>2){
              intestazione = datiRagioneSociale[1];
              intestazione = intestazione.trim();
            }
          }
          destinatari[indiceArrayDestinatari].setCognomeointestazione(intestazione); //
          String indirizzoResidenza= datiForm.getString("INDIMP_" + i);
          String numeroCivico= datiForm.getString("NCIIMPC_" + i);
          if (indirizzoResidenza != null && !"".equals(indirizzoResidenza) && numeroCivico!=null){
            indirizzoResidenza += ", " + numeroCivico;
          }
          destinatari[indiceArrayDestinatari].setIndirizzoResidenza(indirizzoResidenza);
          destinatari[indiceArrayDestinatari].setComuneResidenza(datiForm.getString("LOCIMP_" + i));
          destinatari[indiceArrayDestinatari].setCodiceComuneResidenza(datiForm.getString("CODICIT_" + i));
          if(mezzoinvio!=null && !"".equals(mezzoinvio))
            destinatari[indiceArrayDestinatari].setMezzo(mezzoinvio);
          destinatari[indiceArrayDestinatari].setEmail(datiForm.getString("MAIL_" + i));
          destinatari[indiceArrayDestinatari].setEmailAggiuntiva(datiForm.getString("MAIL_AGGIUNTIVA_" + i));
          destinatari[indiceArrayDestinatari].setProvinciaResidenza(datiForm.getString("PROVINCIA_RES_" + i));
          destinatari[indiceArrayDestinatari].setCapResidenza(datiForm.getString("CAP_RES_" + i));
          destinatari[indiceArrayDestinatari].setTipoVoceRubrica(WSDMTipoVoceRubricaType.IMPRESA);
        }
      }
      if("1".equals(integrazioneWSDM) && !gestioneTask){
        wsdmProtocolloDocumentoIn.setDestinatari(destinatari);
        destinatarioPrincipale=this.gestioneWSDMManager.formattazioneDestinatarioPrincipale(destinatarioPrincipale);
      }
    }

    if("1".equals(integrazioneWSDM) && !gestioneTask)
      wsdmProtocolloDocumentoIn.setDestinatarioPrincipale(destinatarioPrincipale);

    // Invio mail mediante servizi di protocollazione per ENGINEERING
    if("1".equals(integrazioneWSDM) && !gestioneTask && abilitatoInvioMailDocumentale && ("ENGINEERING".equals(tipoWSDM) || "TITULUS".equals(tipoWSDM)
        || "SMAT".equals(tipoWSDM) || "URBI".equals(tipoWSDM) || "ENGINEERINGDOC".equals(tipoWSDM) || "LAPISOPERA".equals(tipoWSDM))){
      WSDMInviaMailType inviaMail = new WSDMInviaMailType();

        // Testo email
        String commsgtes = datiForm.getString("COMMSGTES");
        if (commsgtes == null){
          commsgtes = "[testo vuoto]";
        }
        inviaMail.setTestoMail(commsgtes);
        if("ENGINEERING".equals(tipoWSDM)){
          // Oggetto email
          inviaMail.setOggettoMail(oggettodocumento);
        }
        // Destinatari
        /*
        String selectW_INVCOMDESMail = "select desmail from w_invcomdes where idprg = ? and idcom = ?";
        List<?> datiW_INVCOMDESMail = this.sqlManager.getListVector(selectW_INVCOMDESMail, new Object[] { idprg, newIdcom });
        if (datiW_INVCOMDESMail != null && datiW_INVCOMDESMail.size() > 0) {
          String[] destinatariMail = new String[datiW_INVCOMDESMail.size()];
          for (int ides = 0; ides < datiW_INVCOMDESMail.size(); ides++) {
            destinatariMail[ides] = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDESMail.get(ides), 0).getValue();
          }
          inviaMail.setDestinatariMail(destinatariMail);
        }
        wsdmProtocolloDocumentoIn.setInviaMail(inviaMail);
        */
      this.gestioneWSDMManager.setDestinatariMail(idprg, newIdcom, inviaMail, wsdmProtocolloDocumentoIn);
    }

    //Inserimento allegati in W_DOCDIG
    //Nel caso sia presente l'integrazione WSDM si devono inviare a WSDM sia i documenti che vengono allegati alla comunicazione
    //(cioè i documenti di cui si sta eseguendo l'inserimento nella W_DOCDIG), sia i documenti stessi presi direttamente dalla W_DOCDIG
    int numeroAllegati = 1;
    int numAll = 0;
    int numAllegatiReali=0;
    if(numAllegati!=null && !"".equals(numAllegati)){
      numAll = (new Long(numAllegati)).intValue();
      numeroAllegati += 2 * numAll;
      numAllegatiReali = numAll;
      idAllegatiNuovi = new Long[numAll];
    }

    int indiceAllegati = 0;
    String posizioneAllegatoComunicazione = null;
    if("1".equals(integrazioneWSDM) && !gestioneTask){
      allegati = new WSDMProtocolloAllegatoType[numeroAllegati];
      posizioneAllegatoComunicazione = ConfigManager.getValore("wsdm.posizioneAllegatoComunicazione." + idconfi);
      if("1".equals(posizioneAllegatoComunicazione))
        indiceAllegati = 1;
    }

    String commsgtes = null;
    String commsgtip = null;
    byte[] contenutoPdf = null;
    String nomeFile=null;
    String estensioneFile = "pdf";
    String titoloFile = null;
    Long idAllegatoSintesi = null;
    if("1".equals(integrazioneWSDM) && !gestioneTask){
      commsgtip = datiForm.getString("COMMSGTIP");
      commsgtes = datiForm.getString("COMMSGTES");
      if (commsgtes == null){
        commsgtes = "[testo vuoto]";
      }
      String commsgogg = datiForm.getString("COMMSGOGG");

      //gestione allegato sintesi
      idAllegatoSintesi = gestioneWSDMManager.cancellaAllegatoSintesi(idprg,newIdcom);
      if(idAllegatoSintesi==null) {
        String key1 = UtilityStruts.getParametroString(this.getRequest(),"key1");
        String entita=UtilityStruts.getParametroString(this.getRequest(),"entita");
        HashMap<String, Object> ret = gestioneWSDMManager.aggiungiAllegatoSintesi(key1, cig, commsgogg, commsgtes, idprg, newIdcom, entita, this.getRequest());
        if(ret==null) {
          String messaggio = "Errore nella creazione del file di sintesi della comunicazione";
          throw new GestoreException("Errore nella protocollazione del fascicolo","wsdm.fascicoloprotocollo.protocollazione.error",new Object[]{messaggio}, new Exception());
        }else {
          idAllegatoSintesi = (Long)ret.get("idAllegatoSintesi");
          nomeFile = (String)ret.get("nomeFile");
          estensioneFile = (String)ret.get("estensioneFile");
          titoloFile = (String)ret.get("titoloFile");
          contenutoPdf = (byte[]) ret.get("pdf");
        }
      }else {
        Vector<?> datiAllegato = this.sqlManager.getVector("select dignomdoc, digdesdoc from  w_docdig where idprg=? and iddocdig=?", new Object[] {idprg,idAllegatoSintesi});
        if(datiAllegato!=null && datiAllegato.size()>0) {
          nomeFile = SqlManager.getValueFromVectorParam(datiAllegato, 0).getStringValue();
          titoloFile = SqlManager.getValueFromVectorParam(datiAllegato, 1).getStringValue();
          if(nomeFile.endsWith(".tsd"))
          estensioneFile = "tsd";
        }
        BlobFile digogg = fileAllegatoManager.getFileAllegato(idprg, idAllegatoSintesi);
        contenutoPdf = digogg.getStream();
      }

    }

    if(numAllegati!=null && !"".equals(numAllegati)){
      for(int i=0; i<numAll; i++){
        parametri = new Object[8];
        parametri[0] = idprg;   //IDPRG
        Long newIddocdig = (Long) sqlManager.getObject("SELECT max(iddocdig) FROM w_docdig WHERE idprg=?", new Object[]{idprg});
        if (newIddocdig == null) {
            newIddocdig = new Long(0);
        }
        newIddocdig = newIddocdig + 1;
        parametri[1] = newIddocdig; //IDDOCDIG
        parametri[2] = "W_INVCOM";  //DIGENT
        parametri[3] = "PG"; //DIGKEY1
        parametri[4] = newIdcom;    //DIGKEY2
        parametri[5] = datiForm.getString("NOME_" + i);    //DIGNOMDOC
        parametri[6] = datiForm.getString("DESCRIZIONE_"+ i);    //DIGDESDOC
        LobHandler lobHandler = new DefaultLobHandler(); // reusable object
        BlobFile fileAllegato = fileAllegatoManager.getFileAllegato(idprg, datiForm.getLong("IDDOCDIG_" + i));
        parametri[7] = new SqlLobValue(fileAllegato.getStream(), lobHandler);
        sqlManager.update("INSERT INTO w_docdig(IDPRG,IDDOCDIG,DIGENT,DIGKEY1,DIGKEY2,"
                    + "DIGNOMDOC,DIGDESDOC,DIGOGG) VALUES (?,?,?,?,?,?,?,?)", parametri);

        if("1".equals(integrazioneWSDM) && !gestioneTask){
          idAllegatiNuovi[i] = newIddocdig;
          String dignomdoc = datiForm.getString("NOME_" + i);
          String tipo = GestioneWSDMManager.getTipoFile(dignomdoc);
          //Documento già presente nella W_DOCDIG
          allegati[indiceAllegati + i] = new WSDMProtocolloAllegatoType();
          allegati[indiceAllegati + i].setNome(dignomdoc);
          allegati[indiceAllegati + i].setTitolo( datiForm.getString("DESCRIZIONE_"+ i));
          allegati[indiceAllegati + i].setTipo(tipo);
          allegati[indiceAllegati + i].setContenuto(fileAllegato.getStream());
          if("TITULUS".equals(tipoWSDM))
            allegati[indiceAllegati + i].setIdAllegato("W_DOCDIG|" + idprg + "|" + newIddocdig.toString());
          if("NUMIX".equals(tipoWSDM)) {
            allegati[indiceAllegati + i] = GestioneWSDMManager.popolaAllegatoInfo(dignomdoc,allegati[indiceAllegati + i]);
            if(indiceAllegati + i ==0 )
              allegati[indiceAllegati + i].setIsSealed(new Long(1));
          }
        }
      }
    }

    //Gestione allegato di sintesi senza integrazione
    if(!"1".equals(integrazioneWSDM)){
      String chiaveGara = ngara;
      String entitaGara = "GARE";
      if(chiaveGara==null || "".equals(chiaveGara) || (genere!=null && genere.longValue()==3)){
        entitaGara = "TORN";
        chiaveGara = datiForm.getString("CODGAR");   //COMKEY1
      }
      String cigGara=(String)this.sqlManager.getObject("select codcig from v_gare_torn where codice = ?", new Object[] {chiaveGara});
      String oggetto = datiForm.getString("COMMSGOGG");
      String testo = datiForm.getString("COMMSGTES");
      HashMap<String, Object> ret = gestioneWSDMManager.aggiungiAllegatoSintesi(chiaveGara, cigGara, oggetto, testo, idprg, newIdcom, entitaGara,this.getRequest());
      if(ret==null) {
        throw new GestoreException("Errore nella creazione della marca temporale dell'allegato di sintesi","marcaTemporale",null, new Exception());
      }
    }

    if("1".equals(integrazioneWSDM) && !gestioneTask){
      // Aggiunta del testo della comunicazione

      int indiceAllegatoTesto = numAll;
      if ("1".equals(posizioneAllegatoComunicazione))
        indiceAllegatoTesto = 0;
      if ("1".equals(commsgtip)) {
        commsgtes = "<!DOCTYPE html><html><body>" + commsgtes + "</body></html>";
        allegati[indiceAllegatoTesto] = new WSDMProtocolloAllegatoType();
        allegati[indiceAllegatoTesto].setNome("Comunicazione.html");
        allegati[indiceAllegatoTesto].setTipo("html");
        allegati[indiceAllegatoTesto].setTitolo("Testo della comunicazione");
        allegati[indiceAllegatoTesto].setContenuto(commsgtes.getBytes());
      } else {
        allegati[indiceAllegatoTesto] = new WSDMProtocolloAllegatoType();
        allegati[indiceAllegatoTesto].setNome(nomeFile);
        allegati[indiceAllegatoTesto].setTipo(estensioneFile);
        allegati[indiceAllegatoTesto].setTitolo(titoloFile);
        allegati[indiceAllegatoTesto].setContenuto(contenutoPdf);

      }
      if("TITULUS".equals(tipoWSDM))
        allegati[indiceAllegatoTesto].setIdAllegato("W_INVCOM|" + idprg + "|" + newIdcom.toString());

      if("NUMIX".equals(tipoWSDM)) {
        if(!"1".equals(commsgtip)) {
          allegati[indiceAllegatoTesto] = GestioneWSDMManager.popolaAllegatoInfo(nomeFile,allegati[indiceAllegatoTesto]);
        }
        if(indiceAllegatoTesto ==0 )
          allegati[indiceAllegatoTesto].setIsSealed(new Long(1));
      }
    }

    if("1".equals(integrazioneWSDM) && !gestioneTask){
      wsdmProtocolloDocumentoIn.setAllegati(allegati);

      String username = UtilityStruts.getParametroString(this.getRequest(),"username");
      String password = UtilityStruts.getParametroString(this.getRequest(),"password");
      String ruolo = UtilityStruts.getParametroString(this.getRequest(),"ruolo");
      String nome = UtilityStruts.getParametroString(this.getRequest(),"nome");
      String cognome = UtilityStruts.getParametroString(this.getRequest(),"cognome");
      String codiceuo = UtilityStruts.getParametroString(this.getRequest(),"codiceuo");
      String idutente = UtilityStruts.getParametroString(this.getRequest(),"idutente");
      String idutenteunop = UtilityStruts.getParametroString(this.getRequest(),"idutenteunop");

      if(password==null)
        password="";
      WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = this.gestioneWSDMManager.wsdmProtocolloInserisci(username, password,
          ruolo, nome, cognome, codiceuo, idutente, idutenteunop, codiceaoo, codiceufficio,  wsdmProtocolloDocumentoIn,idconfi);


      if (wsdmProtocolloDocumentoRes.isEsito()) {
        String numeroDocumento = null;
        if(!"LAPISOPERA".equals(tipoWSDM))
          numeroDocumento = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroDocumento();
        Long annoProtocollo = null;
        if(!"LAPISOPERA".equals(tipoWSDM))
          annoProtocollo = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getAnnoProtocollo();
        String numeroProtocollo = null;
        if("LAPISOPERA".equals(tipoWSDM))
          numeroProtocollo = GestioneWSDMManager.PREFISSO_COD_FASCICOLO_LAPISOPERA + wsdmProtocolloDocumentoRes.getProtocolloDocumento().getGenericS11();
        else
          numeroProtocollo = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroProtocollo();

        Timestamp dataProtocollo= this.gestioneWSDMManager.getDataProtocollo(wsdmProtocolloDocumentoRes);
        if(annoProtocollo==null && !"LAPISOPERA".equals(tipoWSDM)){
          annoProtocollo = this.gestioneWSDMManager.getAnnoFromDate(dataProtocollo);
        }

        String codgar= UtilityStruts.getParametroString(this.getRequest(),"codgar");
        HashMap<String, Object> datiGestioneComunicazione = new HashMap<String,Object>();
        datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_TIPO_WSDM, tipoWSDM);
        datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_ABILITATO_INVIO_MAIL_DOCUMENTALE, new Boolean(abilitatoInvioMailDocumentale));
        datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_NUMERO_DOCUMENTO, numeroDocumento);
        datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_ANNO_PROTOCOLLO, annoProtocollo);
        datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_NUMERO_PROTOCOLLO, numeroProtocollo);
        datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_DATA_PROTOCOLLO, dataProtocollo);
        datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_COMMSGETS, commsgtes);
        datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_INDIRIZZO_MITTENTE, indirizzomittente);
        datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_COMMSGTIP, datiForm.getString("COMMSGTIP"));
        datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_IDPRG, idprg);
        datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_IDCOM, newIdcom);
        datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_NUMERO_ALLEGATI_REALI, new Long(numAllegatiReali));
        datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_OGGETTO_DOCUMENTO, oggettodocumento);

        HashMap<String, Object> datiLogin = new HashMap<String,Object>();
        datiLogin.put(GestioneWSDMManager.LABEL_USERNAME, username);
        datiLogin.put(GestioneWSDMManager.LABEL_PASSWORD, password);
        datiLogin.put(GestioneWSDMManager.LABEL_RUOLO, ruolo);
        datiLogin.put(GestioneWSDMManager.LABEL_NOME, nome);
        datiLogin.put(GestioneWSDMManager.LABEL_COGNOME, cognome);
        datiLogin.put(GestioneWSDMManager.LABEL_CODICEUO, codiceuo);

        List<?> datiW_INVCOMDES = this.sqlManager.getListVector("select desmail, idcomdes from w_invcomdes where idprg = ? and idcom = ?", new Object[] { idprg, newIdcom });
        this.gestioneWSDMManager.gestioneComunicazioneDopoProtocollazioneSenzaTransazionePropria(codgar,datiGestioneComunicazione, datiLogin, datiW_INVCOMDES, allegati, idconfi);

        if(!abilitatoInvioSingolo){
          String updateTorn = "update torn set nproti = ? where codgar= ?" ;
          this.sqlManager.update(updateTorn, new Object[] {numeroProtocollo, codgar });
        }

        String entita = UtilityStruts.getParametroString(this.getRequest(),"entita");
        String key1 = UtilityStruts.getParametroString(this.getRequest(),"key1");
        // Salvataggio del riferimento al fascicolo
        if ("SI_FASCICOLO_NUOVO".equals(inserimentoinfascicolo)) {
          String codiceFascicoloNUOVO =null;
          Long annoFascicoloNUOVO = null;
          String numeroFascicoloNUOVO = null;
          if(!"ARCHIFLOWFA".equals(tipoWSDM) && !"FOLIUM".equals(tipoWSDM) && !"PRISMA".equals(tipoWSDM)){
            codiceFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getCodiceFascicolo();
            if (wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getAnnoFascicolo() != null) {
              annoFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getAnnoFascicolo();
            }else
              annoFascicoloNUOVO= this.gestioneWSDMManager.getAnnoFromDate(dataProtocollo);
            numeroFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getNumeroFascicolo();
          }else if("FOLIUM".equals(tipoWSDM)){
            codiceFascicoloNUOVO = classificafascicolo;
            annoFascicoloNUOVO = this.gestioneWSDMManager.getAnnoFromDate(dataProtocollo);
          }else if("ITALPROT".equals(tipoWSDM)){
            codiceFascicoloNUOVO = codicefascicolo;
            annoFascicoloNUOVO=new Long(annofascicolo);
         }else if("PRISMA".equals(tipoWSDM)){
            codiceFascicoloNUOVO= codicefascicolo;
            annoFascicoloNUOVO=new Long(annofascicolo);
            numeroFascicoloNUOVO=numerofascicolo;
          }else
            codiceFascicoloNUOVO= codicefascicolo;

          if("TITULUS".equals(tipoWSDM))
            classificafascicolo = classificadocumento;

          if("ENGINEERINGDOC".equals(tipoWSDM)) {
            codiceufficio = uocompetenza;
            codiceufficiodes = uocompetenzadescrizione;
          }

          this.gestioneWSDMManager.setWSFascicolo(entita, key1, null, null, null, codiceFascicoloNUOVO, annoFascicoloNUOVO,
              numeroFascicoloNUOVO, classificafascicolo,codiceaoo,codiceufficio,struttura,isRiservatezza,classificadescrizione,voce,codiceaoodes,codiceufficiodes);
        }
        //Salvatagio in WSDOCUMENTO
        Long idWSDocumento = this.gestioneWSDMManager.setWSDocumento(entita, key1, null, null, null, numeroDocumento, annoProtocollo, numeroProtocollo, oggettodocumento,inout);

        //Salvataggio della mail in WSALLEGATI
        this.gestioneWSDMManager.setWSAllegati("W_INVCOM", idprg, newIdcom.toString(), null, null, idWSDocumento);

        //Salvataggio degli allegati in WSALLEGATI
        if (newIdcom > 0) {
          for (int i = 0; i < numAll && insDocGareInWsallegati; i++) {
            Long iddocdig =datiForm.getLong("IDDOCDIG_" + i);
            this.gestioneWSDMManager.setWSAllegati("W_DOCDIG", idprg, iddocdig.toString(), null, null, idWSDocumento);
          }
          for (int i = 0; i < numAll; i++) {
            Long iddocdig =idAllegatiNuovi[i];
            this.gestioneWSDMManager.setWSAllegati("W_DOCDIG", idprg, iddocdig.toString(), null, null, idWSDocumento);
          }

          //Salvataggio allegato di sintesi
          this.gestioneWSDMManager.setWSAllegati("W_DOCDIG", idprg,idAllegatoSintesi.toString(), null, null, idWSDocumento);
        }


      }else{
        String messaggio = wsdmProtocolloDocumentoRes.getMessaggio();
        throw new GestoreException("Errore nella protocollazione del fascicolo","wsdm.fascicoloprotocollo.protocollazione.error",new Object[]{messaggio}, new Exception());
      }
    }else  if("1".equals(integrazioneWSDM) && gestioneTask){
      //Nel caso di abilitazione dell'invio singolo, la funzione viene chiamata una volta per ogni destinatario, ma l'inserimento in GARPRO_WSDM deve avvenire solo una volta
      //e ciò è controllato dalla variabile creazioneOccorrenzaTask
      Long idGarpro=null;

      if(creazioneOccorrenzaTask) {

        Long syscon = null;
        String wsdmLoginComune = ConfigManager.getValore(GestioneWSDMManager.PROP_WSDM_LOGIN_COMUNE+idconfi);
        if (wsdmLoginComune != null && "1".equals(wsdmLoginComune)) {
          syscon = new Long(-1);
        } else {
          ProfiloUtente profiloUtente = (ProfiloUtente) this.getRequest().getSession().getAttribute(
              CostantiGenerali.PROFILO_UTENTE_SESSIONE);
          syscon = new Long(profiloUtente.getId());;
        }

        //Codice gara da usare per GARPRO_WSDM
        String codiceGara = ngara;
        if(ngara==null || "".equals(ngara) || (genere!=null && genere.longValue()==3)){
          codiceGara = datiForm.getString("CODGAR");   //COMKEY1
        }

        //Scrittura su GARPRO_WSDM
        idGarpro= new Long(genChiaviManager.getNextId("GARPRO_WSDM"));

        this.sqlManager.update("insert into garpro_wsdm(id, syscon, ngara, tipologia, classifica, cod_reg," +
            " tipo_doc, mitt_int, classifica_tit, indice, uo_mittdest, mezzo, struttura, supporto, indirizzo_mitt, mezzo_invio,oggetto_mail,livello_ris,sottotipo)" +
            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
        new Object[]{idGarpro, syscon, codiceGara, new Long(1), classificadocumento, codiceregistrodocumento,
        tipodocumento, mittenteinterno, idtitolazione, idindice, idunitaoperativamittente,mezzo,struttura,supporto,indirizzomittente,
        mezzoinvio,oggettodocumento,livelloriservatezza,sottotipo});
        idRet=idGarpro;
      }else {
        idGarpro=idOccorrenzaTask;
        idRet=idOccorrenzaTask;
      }

      //Salvataggio in w_invcom del riferimento all'occorrenza in garpro_wsdm
      this.sqlManager.update("update w_invcom set idgarpro=? where idprg=? and idcom=?",new Object[]{idGarpro,idprg, newIdcom});
    }

    return idRet;
  }

  private void associazioneFascicoloWSDM(Long genereGara, String ngara, String codgar, String iterga, Timestamp datpub, Timestamp datFinePubb, String riservatezzaAttiva, String oggettoDocumentoFittizio, String idconfi) throws GestoreException, SQLException, IOException, DocumentException{

    String inserimentoinfascicolo = null;
    String classificafascicolo = null;
    String oggettodocumento = null;
    String tiposistemaremoto = UtilityStruts.getParametroString(this.getRequest(),"tiposistemaremoto");

    String username = UtilityStruts.getParametroString(this.getRequest(),"username");
    String password =UtilityStruts.getParametroString( this.getRequest(),"password");
    if(password== null)
      password="";
    String ruolo = UtilityStruts.getParametroString(this.getRequest(),"ruolo");
    String nome = UtilityStruts.getParametroString(this.getRequest(),"nome");
    String cognome = UtilityStruts.getParametroString(this.getRequest(),"cognome");
    String codiceuo = UtilityStruts.getParametroString(this.getRequest(),"codiceuo");
    String idutente = UtilityStruts.getParametroString(this.getRequest(),"idutente");
    String idutenteunop = UtilityStruts.getParametroString(this.getRequest(),"idutenteunop");

    //Popolamento Documento WSDM
    String classificadocumento = null;
    String tipodocumento = null;
    if(oggettoDocumentoFittizio!=null)
      oggettodocumento = oggettoDocumentoFittizio;
    else
      oggettodocumento = UtilityStruts.getParametroString(this.getRequest(),"oggettodocumento");
    String descrizionedocumento = null;
    String mittenteinterno = null;
    String codiceregistrodocumento = UtilityStruts.getParametroString(this.getRequest(),"codiceregistrodocumento");
    String inout = UtilityStruts.getParametroString(this.getRequest(),"inout");
    String idindice = UtilityStruts.getParametroString(this.getRequest(),"idindice");
    String idtitolazione = UtilityStruts.getParametroString(this.getRequest(),"idtitolazione");
    String idunitaoperativamittente = UtilityStruts.getParametroString(this.getRequest(),"idunitaoperativamittente");

    inserimentoinfascicolo = UtilityStruts.getParametroString(this.getRequest(),"inserimentoinfascicolo");
    String codicefascicolo = UtilityStruts.getParametroString(this.getRequest(),"codicefascicolo");
    String oggettofascicolo = UtilityStruts.getParametroString(this.getRequest(),"oggettofascicolonuovo");
    classificafascicolo = UtilityStruts.getParametroString(this.getRequest(),"classificafascicolonuovo");
    String descrizionefascicolo = UtilityStruts.getParametroString(this.getRequest(),"descrizionefascicolonuovo");
    String annofascicolo = UtilityStruts.getParametroString(this.getRequest(),"annofascicolo");
    String numerofascicolo = UtilityStruts.getParametroString(this.getRequest(),"numerofascicolo");

    String entita = UtilityStruts.getParametroString(this.getRequest(),"entita");
    String key1 = UtilityStruts.getParametroString(this.getRequest(),"key1");
    String servizio = UtilityStruts.getParametroString(this.getRequest(),"servizio");
    String codiceaoo =   UtilityStruts.getParametroString(this.getRequest(),"codiceaoonuovo");
    String idDocumento=null;
    String mezzo = UtilityStruts.getParametroString(this.getRequest(),"mezzo");
    String societa =  UtilityStruts.getParametroString(this.getRequest(),"societa");
    String codiceGaralotto = UtilityStruts.getParametroString(this.getRequest(),"codicegaralotto");
    String cig = UtilityStruts.getParametroString(this.getRequest(),"cig");
    String codiceufficio =   UtilityStruts.getParametroString(this.getRequest(),"codiceufficionuovo");
    String numeroallegati =  UtilityStruts.getParametroString(this.getRequest(),"numeroallegati");
    String struttura = UtilityStruts.getParametroString(this.getRequest(),"strutturaonuovo");
    String tipofascicolo = UtilityStruts.getParametroString(this.getRequest(),"tipofascicolonuovo");
    String classificadescrizione = UtilityStruts.getParametroString(this.getRequest(),"classificadescrizione");
    String voce = UtilityStruts.getParametroString(this.getRequest(),"voce");
    String codiceaoodes = UtilityStruts.getParametroString(this.getRequest(),"codiceaoodes");
    String codiceufficiodes = UtilityStruts.getParametroString(this.getRequest(),"codiceufficiodes");
    Long isRiservatezza = null;
    String nomeRup = UtilityStruts.getParametroString(this.getRequest(),"nomeR");
    String acronimoRup = UtilityStruts.getParametroString(this.getRequest(),"acronimoR");
    String uocompetenza = UtilityStruts.getParametroString(this.getRequest(),"uocompetenza");
    String uocompetenzadescrizione = UtilityStruts.getParametroString(this.getRequest(),"uocompetenzadescrizione");

    if("LAPISOPERA".equals(tiposistemaremoto))
      return;

    if("JIRIDE".equals(tiposistemaremoto) && "1".equals(riservatezzaAttiva) &&
        (genereGara != null && genereGara.intValue() != 10 && genereGara.intValue() != 11 && genereGara.intValue() != 20)){
      isRiservatezza = new Long(1);
    }

    if("PALEO".equals(tiposistemaremoto) || "TITULUS".equals(tiposistemaremoto) || "FOLIUM".equals(tiposistemaremoto)){
      WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = null;
      WSDMProtocolloAllegatoType[] allegati = null;
      if("FOLIUM".equals(tiposistemaremoto))
        inserimentoinfascicolo = "SI_FASCICOLO_ESISTENTE";
      else if("PALEO".equals(tiposistemaremoto))
        inserimentoinfascicolo = "SI_FASCICOLO_NUOVO";
      if(numeroallegati==null || "".equals(numeroallegati))
        numeroallegati="0";
      boolean iterga124 = false;
      if("1".equals(iterga) || "2".equals(iterga) || "4".equals(iterga))
        iterga124 = true;

      if("TITULUS".equals(tiposistemaremoto)){
        if(iterga124 || (genereGara.longValue()==10 || genereGara.longValue()==20 || genereGara.longValue()==11)){
          tipodocumento =GestioneWSDMManager.TIPO_DOCUMENTO_GARA_PUBBLIAZIONE_TITULUS;
          if(genereGara.longValue()==10 || genereGara.longValue()==20){
            tipodocumento = GestioneWSDMManager.TIPO_DOCUMENTO_ELENCO_PUBBLIAZIONE_TITULUS;
            inout ="OUT";
          }else if(genereGara.longValue()==11){
            tipodocumento= GestioneWSDMManager.TIPO_DOCUMENTO_AVVISO_PUBBLIAZIONE_TITULUS;
            inout ="OUT";
          }
          //Oggetto del documento deve avere almeno 30 caratteri

          if(oggettodocumento.length()<30){
            while(oggettodocumento.length()<30)
              oggettodocumento += "-";
          }
        }else{
          tipodocumento = GestioneWSDMManager.TIPO_DOCUMENTO_GARA_PUBBLIAZIONE_PALEO;
          inout ="INT";
        }
      }

      //Per TITULUS si deve sbiancare il contenuto della classifica del fascicolo
      if("TITULUS".equals(tiposistemaremoto)){
        classificadocumento = UtilityStruts.getParametroString(this.getRequest(),"classificadocumento");
        classificafascicolo = null;
      }
      HashMap<String, Object> par = new HashMap<String, Object>();
      if("FOLIUM".equals(tiposistemaremoto))
        par.put("classificadocumento", classificafascicolo);
      else
        par.put("classificadocumento", classificadocumento);
      par.put("tipodocumento", tipodocumento);
      par.put("oggettodocumento", oggettodocumento);
      par.put("descrizionedocumento", descrizionedocumento);
      par.put("mittenteinterno", mittenteinterno);
      par.put("codiceregistrodocumento", codiceregistrodocumento);
      par.put("inout", inout);
      par.put("idindice", idindice);
      par.put("idtitolazione", idtitolazione);
      par.put("idunitaoperativamittente", idunitaoperativamittente);
      par.put("inserimentoinfascicolo", inserimentoinfascicolo);
      par.put("codicefascicolo", codicefascicolo);
      par.put("oggettofascicolo", oggettofascicolo);
      par.put("classificafascicolo", classificafascicolo);
      par.put("descrizionefascicolo", descrizionefascicolo);
      par.put("annofascicolo", annofascicolo);
      par.put("numerofascicolo", numerofascicolo);
      par.put("tipoWSDM", tiposistemaremoto);
      par.put("idprg", null);
      par.put("idcom", null);
      par.put("mezzo", mezzo);
      par.put("societa", societa);
      par.put("codiceGaralotto", codiceGaralotto);
      par.put("cig", cig);
      par.put("servizio", servizio);
      par.put("numeroallegati", new Long(numeroallegati));
      par.put("struttura", struttura);
      par.put("supporto", null);
      par.put("tipofascicolo", tipofascicolo);
      par.put("classificadescrizione", classificadescrizione);
      par.put("voce", voce);
      wsdmProtocolloDocumentoIn = this.gestioneWSDMManager.wsdmProtocolloDocumentoPopola(par,idconfi);

      if("TITULUS".equals(tiposistemaremoto)){
        idDocumento="GARE|" + ngara;
        if(genereGara!=null && genereGara.longValue()==1)
          idDocumento="TORN|" + codgar;
        else if(genereGara!=null && genereGara.longValue()==3)
          idDocumento="GARE|" + codgar;
        wsdmProtocolloDocumentoIn.setIdDocumento(idDocumento);
        if(iterga124 || genereGara.longValue()==10 || genereGara.longValue()==20 || genereGara.longValue()==11){
         //Inserimento di un destinatario fittizio
          WSDMProtocolloAnagraficaType[] destinatari = new WSDMProtocolloAnagraficaType[1];
          destinatari[0] = new WSDMProtocolloAnagraficaType();
          destinatari[0].setCodiceFiscale("nd");
          destinatari[0].setCognomeointestazione("nd");
          wsdmProtocolloDocumentoIn.setDestinatari(destinatari);
        }

      }

      //Per FOLIUM si deve creare un destinatatio fittizio
      if("FOLIUM".equals(tiposistemaremoto)){
        WSDMProtocolloAnagraficaType[] destinatari = new WSDMProtocolloAnagraficaType[1];
        destinatari[0] = new WSDMProtocolloAnagraficaType();
        destinatari[0].setCodiceFiscale("");
        destinatari[0].setCognomeointestazione("nd");
        destinatari[0].setIndirizzoResidenza("nd");
        wsdmProtocolloDocumentoIn.setDestinatari(destinatari);
      }

      String idprg = null;
      Long iddocdig = null;


      // Aggiunta del testo della comunicazione

      //Si crea un allegato fittizio
      List<?> datiDoc= null;
      if("TITULUS".equals(tiposistemaremoto) && (iterga124 || (genereGara.longValue()==10 || genereGara.longValue()==20 || genereGara.longValue()==11))){
        //Si deve considerare il primo documento del gruppo=1
        String select="select descrizione, dignomdoc, d.IDPRG, d.IDDOCDG from DOCUMGARA d,W_DOCDIG w where CODGAR=? ";
        if( genereGara== null || genereGara.longValue()==2)
          select+=" and NGARA = '"+ ngara + "'";
        select+=" and GRUPPO = ? and d.IDPRG=w.IDPRG and d.IDDOCDG = w.IDDOCDIG order by NUMORD";
        datiDoc=this.sqlManager.getListVector(select, new Object[]{codgar,new Long(1)});
        if(datiDoc!=null && datiDoc.size()>0){
          allegati = new WSDMProtocolloAllegatoType[datiDoc.size()];
          String nomedoc = null;
          String tipo = null;
          String descrizione = null;
          FileAllegatoManager fileAllegatoManager = null;
          BlobFile digogg = null;
          for(int j=0; j < datiDoc.size(); j++){
            allegati[j] = new WSDMProtocolloAllegatoType();
            nomedoc = SqlManager.getValueFromVectorParam(datiDoc.get(j), 1).stringValue();
            allegati[j].setNome(nomedoc);
            tipo=GestioneWSDMManager.getTipoFile(nomedoc);
            allegati[j].setTipo(tipo);
            descrizione = SqlManager.getValueFromVectorParam(datiDoc.get(j), 0).stringValue();
            allegati[j].setTitolo(descrizione);
            idprg = SqlManager.getValueFromVectorParam(datiDoc.get(j), 2).getStringValue();
            iddocdig = SqlManager.getValueFromVectorParam(datiDoc.get(j), 3).longValue();
            fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager",
                this.getServletContext(), FileAllegatoManager.class);
            digogg = fileAllegatoManager.getFileAllegato(idprg, iddocdig);
            allegati[j].setContenuto(digogg.getStream());
            allegati[j].setIdAllegato("W_DOCDIG|" + idprg + "|" + iddocdig.toString());
          }
        }else{
          //Nel caso non ci sono allegati alla documentazione non si deve procedere
          //Va ricordato che esiste sicuramente un documento di gruppo 1, perchè altrimenti non sarebbe
          //stato possibile accedere alla funzione di pubblicazione.
          throw new GestoreException("Non e' possibile procedere con la creazione del fascicolo per mancanza di allegati al bando","errors.gestoreException.*.wsdm.fascicoloprotocollo.fascicoloinserisci.error.allegati",null, new Exception());
        }
      }else{
        allegati = new WSDMProtocolloAllegatoType[1];
        allegati[0] = new WSDMProtocolloAllegatoType();
        String commsgtes ="Apertura fascicolo";
        allegati[0].setNome("Fascicolo.pdf");
        allegati[0].setTipo("pdf");
        allegati[0].setTitolo(commsgtes);
        InputStream iccInputStream = new FileInputStream(getRequest().getSession(true).getServletContext().getRealPath("/WEB-INF/jrReport/sRGB_v4_ICC_preference.icc"));
        try {
          allegati[0].setContenuto(UtilityStringhe.string2PdfA(commsgtes,iccInputStream));
        } catch (com.itextpdf.text.DocumentException e) {
          throw new DocumentException(e);
        }
        if("TITULUS".equals(tiposistemaremoto))
          allegati[0].setIdAllegato(idDocumento + "|1");

        if("NUMIX".equals(tiposistemaremoto)) {
          allegati[0].setIsSealed(new Long(1));
        }
      }


      wsdmProtocolloDocumentoIn.setAllegati(allegati);

      WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = null;

      if("TITULUS".equals(tiposistemaremoto) && (iterga124 || (genereGara.longValue()==10 || genereGara.longValue()==20 || genereGara.longValue()==11))){
        Calendar calendarDatPub = Calendar.getInstance();
        calendarDatPub.setTime(new Date(datpub.getTime()));
        wsdmProtocolloDocumentoIn.setPubblicazioneDal(calendarDatPub);
        if(datFinePubb!=null){
          Calendar calendarFinePub = Calendar.getInstance();
          calendarFinePub.setTime(new Date(datFinePubb.getTime()));
          wsdmProtocolloDocumentoIn.setPubblicazioneAl(calendarFinePub);
        }
        //Mittente fittizzio
        WSDMProtocolloAnagraficaType[] mittente = new WSDMProtocolloAnagraficaType[1];
        mittente[0] = new WSDMProtocolloAnagraficaType();
        mittente[0].setCodiceFiscale("nd");
        mittente[0].setCognomeointestazione("nd");
        wsdmProtocolloDocumentoIn.setMittenti(mittente);
        wsdmProtocolloDocumentoRes = this.gestioneWSDMManager.wsdmProtocolloInserisci(username, password,
          ruolo, nome, cognome, codiceuo, idutente, idutenteunop, codiceaoo, codiceufficio, wsdmProtocolloDocumentoIn,idconfi);
      }else wsdmProtocolloDocumentoRes = this.gestioneWSDMManager.WSDMDocumentoInserisci(username, password,
          ruolo, nome, cognome, codiceuo, null, null, wsdmProtocolloDocumentoIn,"FASCICOLOPROTOCOLLO",codiceaoo, codiceufficio,idconfi);

      if (wsdmProtocolloDocumentoRes.isEsito()) {
        String numeroDocumento = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroDocumento();

        String numeroProtocollo = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroProtocollo();
        Long annoProtocollo = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getAnnoProtocollo();
        // Salvataggio del riferimento al fascicolo
        if ("SI_FASCICOLO_NUOVO".equals(inserimentoinfascicolo) || "FOLIUM".equals(tiposistemaremoto)) {
          String codiceFascicoloNUOVO = null;
          Long annoFascicoloNUOVO = null;
          String numeroFascicoloNUOVO = null;

          if(wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo()!=null){
            codiceFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getCodiceFascicolo();
            if (wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getAnnoFascicolo() != null) {
              annoFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getAnnoFascicolo();
            }else{
              Timestamp dataProtocollo= this.gestioneWSDMManager.getDataProtocollo(wsdmProtocolloDocumentoRes);
              annoFascicoloNUOVO = this.gestioneWSDMManager.getAnnoFromDate(dataProtocollo);
            }
            numeroFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getNumeroFascicolo();
          } else if("FOLIUM".equals(tiposistemaremoto)){
            codiceFascicoloNUOVO = classificafascicolo;
            Timestamp dataProtocollo= this.gestioneWSDMManager.getDataProtocollo(wsdmProtocolloDocumentoRes);
            annoFascicoloNUOVO = this.gestioneWSDMManager.getAnnoFromDate(dataProtocollo);
          }else
            codiceFascicoloNUOVO=codicefascicolo;

          if("TITULUS".equals(tiposistemaremoto))
            classificafascicolo = classificadocumento;

          this.gestioneWSDMManager.setWSFascicolo(entita, key1, null, null, null, codiceFascicoloNUOVO, annoFascicoloNUOVO,
              numeroFascicoloNUOVO, classificafascicolo,codiceaoo, codiceufficio,struttura,isRiservatezza,classificadescrizione,voce,codiceaoodes,codiceufficiodes);
        }

        //Salvatagio in WSDOCUMENTO
        if(oggettodocumento!=null && oggettodocumento.length()>2000)
          oggettodocumento = oggettodocumento.substring(0, 2000);
        Long idWSDocumento = this.gestioneWSDMManager.setWSDocumento(entita, key1, null, null, null, numeroDocumento, annoProtocollo, numeroProtocollo, oggettodocumento,inout);

        //Salvataggio degli allegati in WSALLEGATI
        if("TITULUS".equals(tiposistemaremoto) && (iterga124 || (genereGara.longValue()==10 || genereGara.longValue()==20))){
          for(int j=0; j < datiDoc.size(); j++){
            idprg = SqlManager.getValueFromVectorParam(datiDoc.get(j), 2).getStringValue();
            iddocdig = SqlManager.getValueFromVectorParam(datiDoc.get(j), 3).longValue();
            this.gestioneWSDMManager.setWSAllegati("W_DOCDIG", idprg, iddocdig.toString(), null, null, idWSDocumento);
          }

        }

      }else{
        String messaggio = wsdmProtocolloDocumentoRes.getMessaggio();
        throw new GestoreException("Errore nell'inserimento del documento","wsdm.fascicoloprotocollo.documentoinserisci.error",new Object[]{messaggio}, new Exception());
      }
    }else if ("SMAT".equals(tiposistemaremoto) || "ARCHIFLOWFA".equals(tiposistemaremoto) || "PRISMA".equals(tiposistemaremoto) || "ITALPROT".equals(tiposistemaremoto)){
      Date oggi = new Date();
      Long annoFascicoloNUOVO = null;
      String numeroFascicoloNUOVO=null;
      if("SMAT".equals(tiposistemaremoto)){
        annoFascicoloNUOVO = this.gestioneWSDMManager.getAnnoFromDate(new Timestamp(oggi.getTime()));
        classificafascicolo=null;
        codicefascicolo=key1;
      }else if("PRISMA".equals(tiposistemaremoto)){
        annoFascicoloNUOVO=new Long(annofascicolo);
        numeroFascicoloNUOVO=numerofascicolo;
      }else if("ITALPROT".equals(tiposistemaremoto)){
        annoFascicoloNUOVO=new Long(annofascicolo);
      }

      this.gestioneWSDMManager.setWSFascicolo(entita, key1, null, null, null, codicefascicolo, annoFascicoloNUOVO,
          numeroFascicoloNUOVO, classificafascicolo,null,null,struttura,isRiservatezza,null,null,null,null);
    }else{
      HashMap<String, Object> parWSDM = new HashMap<String, Object>();
      parWSDM.put(GestioneWSDMManager.LABEL_CLASSIFICA_FASCICOLO, classificafascicolo);
      parWSDM.put(GestioneWSDMManager.LABEL_DESCRIZIONE_FASCICOLO, descrizionefascicolo);
      parWSDM.put(GestioneWSDMManager.LABEL_OGGETTO_FASCICOLO, oggettofascicolo);
      parWSDM.put(GestioneWSDMManager.LABEL_STRUTTURA, struttura);
      parWSDM.put(GestioneWSDMManager.LABEL_TIPO_FASCICOLO, tipofascicolo);
      parWSDM.put(GestioneWSDMManager.LABEL_ACRONIMO_RUP, acronimoRup);
      parWSDM.put(GestioneWSDMManager.LABEL_NOME_RUP, nomeRup);
      parWSDM.put(GestioneWSDMManager.LABEL_USERNAME, username);
      parWSDM.put(GestioneWSDMManager.LABEL_PASSWORD, password);
      parWSDM.put(GestioneWSDMManager.LABEL_RUOLO, ruolo);
      parWSDM.put(GestioneWSDMManager.LABEL_NOME, nome);
      parWSDM.put(GestioneWSDMManager.LABEL_COGNOME, cognome);
      parWSDM.put(GestioneWSDMManager.LABEL_CODICEUO, codiceuo);
      parWSDM.put(GestioneWSDMManager.LABEL_ID_UTENTE, idutente);
      parWSDM.put(GestioneWSDMManager.LABEL_ID_UTENTE_UNITA_OPERATIVA, idutenteunop);
      parWSDM.put(GestioneWSDMManager.LABEL_UOCOMPETENZA, uocompetenza);
      parWSDM.put(GestioneWSDMManager.LABEL_DESCRIZIONE_UOCOMPETENZA, uocompetenzadescrizione);

      String messaggio = this.gestioneWSDMManager.setFascicolo(tiposistemaremoto, servizio, idconfi, entita, key1, isRiservatezza, parWSDM);
      if(messaggio!=null)
        throw new GestoreException("Errore nell'inserimento del fascicolo: " + messaggio,"wsdm.fascicoloprotocollo.fascicoloinserisci.error",new Object[]{messaggio}, new Exception());
    }

  }



  private Timestamp getData(String iterga, Timestamp dteoff, Timestamp dtepar){
    Timestamp ret = null;
    if("1".equals(iterga))
      ret=dteoff;
    else
      ret=dtepar;
    return ret;
  }

  /**
   * Cancellazione dei dati non coerenti col criterio di aggiudicazione della gara per tutti i lotti della gara
   * @param ngara
   * @param codgar
   * @param genere
   * @throws GestoreException
   */
  private void cancellazioneDatiNonUtilizzati(String ngara, String codgar, Long genere, String iterga ) throws GestoreException{
    try {
      Long modlicg=null;
      Long offtel=null;
      String valtec=null;
      offtel =(Long)this.sqlManager.getObject("select offtel from torn where codgar=?", new Object[]{codgar});
      String sezionitec = null;
      switch (genere.intValue()){
        case 2:{
          Vector<?> datiGara = this.sqlManager.getVector("select modlicg,valtec,sezionitec from gare,gare1 where gare.ngara=? and gare.ngara=gare1.ngara", new Object[]{ngara});
          if(datiGara!=null && datiGara.size()>0){
            modlicg= SqlManager.getValueFromVectorParam(datiGara, 0).longValue();
            valtec = SqlManager.getValueFromVectorParam(datiGara, 1).getStringValue();
            sezionitec = SqlManager.getValueFromVectorParam(datiGara, 2).getStringValue();
          }
          String nobustamm = (String)this.sqlManager.getObject("select nobustamm from torn where codgar=?", new Object[]{codgar});
          cancellaDatiEntita(ngara, modlicg,offtel,codgar, iterga,valtec,sezionitec,nobustamm);
          break;
        }
        case 1:
        case 3: {
          String select="select gare.ngara, modlicg,valtec,sezionitec from gare,gare1 where gare.codgar1=? and gare.ngara=gare1.ngara";
          if (genere.intValue() == 3)
            select += " and gare.ngara!=gare.codgar1";
          List<?> listaModlicgLotti = this.sqlManager.getListVector(select, new Object[]{ codgar});
          String lotto = null;
          if (listaModlicgLotti != null && listaModlicgLotti.size() > 0) {
            for (int i = 0; i < listaModlicgLotti.size(); i++) {
              lotto = SqlManager.getValueFromVectorParam(listaModlicgLotti.get(i), 0).stringValue();
              modlicg = SqlManager.getValueFromVectorParam(listaModlicgLotti.get(i), 1).longValue();
              valtec = SqlManager.getValueFromVectorParam(listaModlicgLotti.get(i), 2).getStringValue();
              sezionitec = SqlManager.getValueFromVectorParam(listaModlicgLotti.get(i), 3).getStringValue();
              cancellaDatiEntita(lotto, modlicg, offtel, codgar, iterga, valtec, sezionitec,null);
            }
          }
          if (genere.intValue() == 3) {
            Long conteggio = (Long) this.sqlManager.getObject("select count(ngara) from gare1 where codgar1=? and ngara!=codgar1 and sezionitec='1'", new Object[] {codgar});
            if (conteggio == null || new Long(0).equals(conteggio))
              this.sqlManager.update("update documgara set seztec = null where CODGAR=? and ngara is null and gruppo=3 and busta=2 ", new Object[]{codgar});
          }
        }
      }
      cancellaDatiGeneraliGara(codgar, genere, iterga, offtel, modlicg);
    } catch (SQLException e) {
      throw new GestoreException("Errore nella cancellazione dei dati non coerenti col criterio di aggiudicazione della gara" + ngara,null,e);
    }
  }

  /**
   *
   * @param ngara
   * @param modlicg
   * @param offtel
   * @param codgar
   * @param iterga
   * @param valtec
   * @param sezionitec
   * @param nobustamm
   * @throws SQLException
   * @throws GestoreException
   */
  private void cancellaDatiEntita(String ngara, Long modlicg, Long offtel, String codgar, String iterga, String valtec, String sezionitec,String nobustamm) throws SQLException, GestoreException{
    if(modlicg!=null && ((modlicg.longValue()!=5 && modlicg.longValue()!=6 && modlicg.longValue()!=14) ||
    ((new Long(6)).equals(modlicg) && (new Long(1)).equals(offtel)  && !this.controlliOepvManager.checkFormato(ngara,new Long(52))))){
      this.sqlManager.update("delete from gcap where ngara=?", new Object[]{ngara});
      this.sqlManager.update("delete from gcap_est where ngara=?", new Object[]{ngara});
      this.sqlManager.update("delete from garconfdati where ngara=?", new Object[]{ngara});
    }
    if (!(new Long(6)).equals(modlicg)) {
      this.sqlManager.update("delete from goev where ngara=?", new Object[]{ngara});
    } else {
      if (!"1".equals(sezionitec)) {
        this.sqlManager.update("update goev set seztec = null where ngara=?", new Object[]{ngara});
        this.sqlManager.update("update documgara set seztec = null where CODGAR=? and ngara=? and gruppo=3 and busta=2 and (gentel !='1' or gentel is null)", new Object[]{codgar,ngara});
      }
    }

    String chiave = ngara;
    if(chiave==null || "".equals(chiave) )
      chiave = codgar;

    if (!(new Long(6)).equals(modlicg) && !"1".equals(valtec)) {
      List<?> deleteList1 = this.sqlManager.getListVector("select IDPRG, IDDOCDG, NORDDOCG from documgara where ngara=? and gruppo = 3 and busta = 2", new Object[]{ngara});
      this.sqlManager.update("delete from documgara where ngara=? and gruppo = 3 and busta = 2", new Object[]{ngara});
      this.sqlManager.update("delete from qform  where key1=? and entita='GARE' and busta=2", new Object[]{chiave});
      cancellaListaDocumenti(deleteList1,codgar, true);
    }
    String costofisso =(String)this.sqlManager.getObject("select costofisso from gare1 where ngara = ?", new Object[]{ngara});
    if(costofisso!=null && "1".equals(costofisso) && modlicg.intValue()==6){
      this.sqlManager.update("delete from goev where goev.ngara=? and goev.tippar = 2", new Object[]{ngara});
      List<?> deleteList3 = this.sqlManager.getListVector("select IDPRG, IDDOCDG, NORDDOCG from documgara where ngara=? and gruppo = 3 and busta = 3 and (gentel is null or gentel <> '1')", new Object[]{ngara});
      this.sqlManager.update("delete from documgara where ngara=? and gruppo = 3 and busta = 3 and (gentel is null or gentel <> '1')", new Object[]{ngara});
      this.sqlManager.update("delete from qform  where key1=? and entita='GARE' and busta=3", new Object[]{chiave});
      cancellaListaDocumenti(deleteList3, codgar, true);
    }

    if(!"2".equals(iterga) && !"4".equals(iterga) && !"7".equals(iterga))
      this.sqlManager.update("delete from qform  where key1=? and entita='GARE' and busta=4", new Object[]{chiave});
    else if("7".equals(iterga)) {
      this.sqlManager.update("delete from qform  where key1=? and entita='GARE' and busta!=4", new Object[]{chiave});
      this.sqlManager.update("delete from qform  where key1 like ? and entita='GARE' and busta!=4", new Object[]{codgar + "%"});
    }

    if("1".equals(nobustamm)) {
      this.sqlManager.update("delete from documgara where ngara=? and gruppo = 3 and busta = 1", new Object[]{ngara});
      this.sqlManager.update("delete from qform where key1=? and entita='GARE' and busta=1", new Object[]{chiave});
    }

  }

  private void cancellaDatiGeneraliGara(String codgar, Long genere, String iterga, Long offtel, Long modlicg) throws SQLException, GestoreException{
    if(!"2".equals(iterga) && !"4".equals(iterga)  && !"7".equals(iterga)){
      List<?> deleteList2 = this.sqlManager.getListVector("select IDPRG, IDDOCDG, NORDDOCG from documgara where codgar=? and gruppo = 3 and busta = 4", new Object[]{codgar});
      this.sqlManager.update("delete from documgara where codgar=? and gruppo = 3 and busta = 4", new Object[]{codgar});
      cancellaListaDocumenti(deleteList2, codgar, true);
    }
    cancellaDocumentiTipologiaNonPrevista(codgar);
    if(genere != null && genere.intValue() == 1 || genere.intValue() == 3){
      //non ci sono lotti oepv
      String select="select count(*) from gare,gare1 where gare.codgar1=? and gare.ngara=gare1.ngara and (modlicg = 6 or valtec='1')";
      if (genere.intValue() == 3)
        select += " and gare.ngara!=gare.codgar1";
      Long countLottiNonOepv = (Long) this.sqlManager.getObject(select, new Object[]{codgar});
      if(countLottiNonOepv.intValue() == 0){
        List<?> deleteList3 = this.sqlManager.getListVector("select IDPRG, IDDOCDG, NORDDOCG from documgara where codgar=? and gruppo = 3 and busta = 2", new Object[]{codgar});
        this.sqlManager.update("delete from documgara where codgar=? and gruppo = 3 and busta = 2", new Object[]{codgar});
        cancellaListaDocumenti(deleteList3, codgar, true);
      }

      //tutti i lotti sono costofisso
      select = "select count(*) from gare1, gare where gare1.ngara = gare.ngara and gare.codgar1 = ? and (gare1.costofisso is null or gare1.costofisso<>'1')";
      if (genere.intValue() == 3)
        select += " and gare.ngara!=gare.codgar1";
      Long countLottiNotCostofisso = (Long) this.sqlManager.getObject(select, new Object[]{codgar});
      if(countLottiNotCostofisso.intValue() == 0){
        List<?> deleteList3 = this.sqlManager.getListVector("select IDPRG, IDDOCDG, NORDDOCG from documgara where codgar=? and gruppo = 3 and busta = 3 and (gentel is null or gentel <> '1')", new Object[]{codgar});
        this.sqlManager.update("delete from documgara where codgar=? and gruppo = 3 and busta = 3 and (gentel is null or gentel <> '1')", new Object[]{codgar});
        cancellaListaDocumenti(deleteList3, codgar, true);
      }
    }
  }

  private void cancellaListaDocumenti(List<?> deleteList, String codgar, Boolean deleteImprdocg) throws SQLException{
    String idprg=null;
    Long iddocdig=null;
    Long norddocg=null;
    for (int i = 0; i < deleteList.size(); i++) {
      idprg = (String) SqlManager.getValueFromVectorParam(deleteList.get(i), 0).getValue();
      iddocdig = (Long) SqlManager.getValueFromVectorParam(deleteList.get(i), 1).getValue();
      if(iddocdig!=null){
        this.sqlManager.update("delete from w_docdig where idprg=? and iddocdig = ?", new Object[]{idprg,iddocdig});
      }
      if(deleteImprdocg){
        norddocg = (Long) SqlManager.getValueFromVectorParam(deleteList.get(i), 2).getValue();
        this.sqlManager.update("delete from imprdocg where codgar=? and norddoci = ?", new Object[]{codgar,norddocg});
      }
    }
  }

  private void cancellaDocumentiTipologiaNonPrevista(String codgar) throws SQLException, GestoreException{

    String sqlw9cfPubb = "select count(*) from TORN left outer join GARE on TORN.CODGAR=GARE.CODGAR1 where TORN.CODGAR=?";
    Long tipoPubblicazione, gruppo;
    String clausolaWhereVis, clausolaWhereUlt;

    List<?> w9cfPubb = sqlManager.getListVector("select P.ID, P.NOME, P.CL_WHERE_VIS, P.CL_WHERE_ULT, P.GRUPPO from G1CF_PUBB P where exists (select * from DOCUMGARA D WHERE D.CODGAR = ? AND D.TIPOLOGIA = P.ID ) order by P.NUMORD", new Object[] {codgar});
    if (w9cfPubb != null && w9cfPubb.size() > 0) {
      for (int i = 0; i < w9cfPubb.size(); i++) {
          tipoPubblicazione = SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 0).longValue();
          clausolaWhereVis = (String) SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 2).getValue();
          clausolaWhereUlt = (String) SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 3).getValue();
          gruppo = SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 4).longValue();
          if(!new Long(5).equals(gruppo) || GeneManager.checkOP(this.getServletContext(), "OP129")){
          if (clausolaWhereVis != null && !clausolaWhereVis.equals("")) {
              clausolaWhereVis = " and (" + clausolaWhereVis + ")";
          }
          if (clausolaWhereUlt != null && !clausolaWhereUlt.equals("")) {
            clausolaWhereVis = clausolaWhereVis + " and (" + clausolaWhereUlt + ")";
          }
          Long pubbVisible = null;
          pubbVisible = (Long) sqlManager.getObject(sqlw9cfPubb + clausolaWhereVis, new Object[] {codgar});

          if (pubbVisible== null || pubbVisible.intValue() <= 0) {
            this.sqlManager.update("update documgara set isarchi='1' where codgar=? and tipologia = ?", new Object[]{codgar,tipoPubblicazione});
         }
        }
      }
    }
  }

}
