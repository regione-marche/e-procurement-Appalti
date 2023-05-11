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

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import com.lowagie.text.DocumentException;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
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
public class GestorePubblicaStipula extends
    AbstractGestoreEntita {

  static Logger               logger         = Logger.getLogger(GestorePubblicaStipula.class);
  /** Manager per la gestione dei tabellati. */
  private TabellatiManager tabellatiManager;


  private GenChiaviManager genChiaviManager;

  public GestorePubblicaStipula() {
    super(false);
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);

    tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        this.getServletContext(), TabellatiManager.class);

      genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);

  }

  @Override
  public String getEntita() {
    return "G1STIPULA";
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

        String codstipula = UtilityStruts.getParametroString(this.getRequest(),"codstipula");
	  	String idconfi = UtilityStruts.getParametroString(this.getRequest(),"idconfi");
	  	String messageKey = null;
	  	String select_iddoc = "select id,statodoc,visibilita from g1docstipula where idstipula = ?";
	  	String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
	  	String listaDoc ="";
	  	Integer livEvento = 1;
	  	String descrLog = "";
	  	int docImpostatiA3=0;

        // lettura dei parametri di input
        Long idStipula = datiForm.getLong("ID");
        Long statoStipula = datiForm.getLong("STATO");
        Long opzInvio = datiForm.getLong("OPZINVIO");
        /*
        Timestamp datpub = datiForm.getData("DATPUB");
        if(datpub == null){
          datpub = new Timestamp(System.currentTimeMillis());
          Date data = new Date(datpub.getTime());
          data = DateUtils.truncate(data, Calendar.DATE);
          datpub = new Timestamp(data.getTime());
        }
         */

        ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);


        try {

          if(opzInvio==1)
        	  gestioneInvioAggiudicatarioContraente(datiForm,profilo,idStipula,"1",idconfi,codstipula);
          else if(opzInvio==2) {
            String creaFascicolo = UtilityStruts.getParametroString(this.getRequest(),"creaFascicolo");
            if("si".equals(creaFascicolo)) {
              String messaggio;
              try {
                messaggio = creazioneFasciolo(idStipula,idconfi,codstipula);
                if(messaggio !=null && !"".equals(messaggio))
                  throw new GestoreException("Errore nella creazione del fascicolo","wsdm.fascicoloprotocollo.fascicoloinserisci.error",new Object[] {messaggio},new Exception());
              } catch (Exception e) {
                logger.error("Errore nell'inserimento del fascicolo per la spipula " + idStipula + " " +  e.getMessage());
                throw new GestoreException("Errore nella creazione del fascicolo","wsdm.fascicoloprotocollo.fascicoloinserisci.remote.error",e);
              }

            }
          }

          List<?> listaDocL=this.sqlManager.getListVector(select_iddoc, new Object[] {idStipula});

          for (int i = 0; i < listaDocL.size(); i++) {
        	  String ids_prog = (((Vector<?>) listaDocL.get(i)).get(0)).toString();
        	  String statoDoc = (((Vector<?>) listaDocL.get(i)).get(1)).toString();
        	  String visibilita = (((Vector<?>) listaDocL.get(i)).get(2)).toString();
        	  //Verifico se è un caso da update
        	  if("1".equals(statoDoc) && ("2".equals(visibilita) || "3".equals(visibilita))) {
              	if(!listaDoc.equals("")){listaDoc = listaDoc+", ";}
              	listaDoc = listaDoc + ids_prog;
        	  }
          }

          //Preparo la descrizione del log
          if ("".equals(listaDoc)) {
        	  if(opzInvio==1) {
        		  descrLog = "Pubblicazione stipula contratto su portale Appalti con comunicazione(Non sono presenti documenti da pubblicare).";
        	  }else {
        		  descrLog = "Pubblicazione stipula contratto su portale Appalti senza comunicazione(Non sono presenti documenti da pubblicare).";
        	  }
          }else {
        	  if(opzInvio==1) {
        		  descrLog = "Pubblicazione stipula contratto su portale Appalti con comunicazione(id. doc. pubblicati: " + listaDoc + ").";
        	  }else {
        		  descrLog = "Pubblicazione stipula contratto su portale Appalti senza comunicazione(id. doc. pubblicati: " + listaDoc + ").";
        	  }
              this.sqlManager.update("update g1docstipula set statodoc = ? where idstipula = ? and statodoc= ? and visibilita = ?",
                      new Object[] {new Long(2), idStipula, new Long(1), new Long(2)});

              docImpostatiA3 = this.sqlManager.update("update g1docstipula set statodoc = ? where idstipula = ? and statodoc= ? and visibilita = ?",
                      new Object[] {new Long(3), idStipula, new Long(1), new Long(3)});
          }

          Date dataOdierna = UtilityDate.getDataOdiernaAsDate();

          if(statoStipula<Long.valueOf(3)){
              this.sqlManager.update("update G1STIPULA set DATPUB = ?, stato = ?  where ID = ? and DATPUB is null",
            		  new Object[] {dataOdierna, Long.valueOf(3), idStipula});
          }
          else {
        	  if (docImpostatiA3>0) {
                  this.sqlManager.update("update G1STIPULA set stato = ?  where ID = ?",
                		  new Object[] {Long.valueOf(3), idStipula});
                  this.sqlManager.update("update G1STIPULA set DATPUB = ? where ID = ? and DATPUB is null",
                      new Object[] {dataOdierna, idStipula});
        	  }
          }


          this.getRequest().setAttribute("pubblicazioneEseguita", "1");

          errMsgEvento = "";

        } catch (SQLException e) {
          errMsgEvento = "Errore nell'invio all'aggiudicatario contraente";
          livEvento = 3;
          listaDoc = "";
          this.getRequest().setAttribute("pubblicazioneEseguita", "Errori");
          throw new GestoreException("Errore nell'invio all'aggiudicatario contraente", null, e);
        } catch (IOException e) {
          errMsgEvento = "Errore nell'invio all'aggiudicatario contraente";
          livEvento = 3;
          listaDoc = "";
          this.getRequest().setAttribute("pubblicazioneEseguita", "Errori");
          throw new GestoreException("Errore nell'invio all'aggiudicatario contraente", null, e);
        } catch (DocumentException e) {
          errMsgEvento = "Errore nell'invio all'aggiudicatario contraente";
          livEvento = 3;
          listaDoc = "";
          this.getRequest().setAttribute("pubblicazioneEseguita", "Errori");
          throw new GestoreException("Errore nell'invio all'aggiudicatario contraente", null, e);
        }
        finally {

        //Tracciatura eventi
        try {
          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(String.valueOf(codstipula));
          logEvento.setCodEvento("GA_STIPULA_PUBBLICA");
          logEvento.setDescr(descrLog);
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          messageKey = "errors.logEventi.inaspettataException";
          logger.error(this.resBundleGenerale.getString(messageKey), le);
        }
        }

  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }




  private void gestioneInvioAggiudicatarioContraente(DataColumnContainer datiForm, ProfiloUtente profilo, Long idStipula, String numDestinatari, String idconfi, String codstipula) throws SQLException, GestoreException, IOException, DocumentException{

    FileAllegatoManager fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager",
        this.getServletContext(), FileAllegatoManager.class);

    GestioneWSDMManager gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        this.getServletContext(), GestioneWSDMManager.class);


    String integrazioneWSDM = this.getRequest().getParameter("integrazioneWSDM");
    String classificadocumento = this.getRequest().getParameter("classificadocumento");
    String tipodocumento =  this.getRequest().getParameter("tipodocumento");
    String oggettodocumento =this.getRequest().getParameter("oggettodocumento");
    String descrizionedocumento = this.getRequest().getParameter("descrizionedocumento");
    String mittenteinterno = this.getRequest().getParameter("mittenteinterno");
    String indirizzoMittente = this.getRequest().getParameter("indirizzomittente");
    String mezzoinvio = this.getRequest().getParameter("mezzoinvio");
    String mezzo = this.getRequest().getParameter("mezzo");
    String codiceregistrodocumento = this.getRequest().getParameter("codiceregistrodocumento");
    String inout = this.getRequest().getParameter("inout");
    String idindice = this.getRequest().getParameter("idindice");
    String idtitolazione = this.getRequest().getParameter("idtitolazione");
    String idunitaoperativamittente = this.getRequest().getParameter("idunitaoperativamittente");
    String inserimentoinfascicolo =  this.getRequest().getParameter("inserimentoinfascicolo");
    String codicefascicolo =  this.getRequest().getParameter("codicefascicolo");
    String oggettofascicolo =  this.getRequest().getParameter("oggettofascicolonuovo");
    String classificafascicolo =  this.getRequest().getParameter("classificafascicolonuovo");
    String descrizionefascicolo =  this.getRequest().getParameter("descrizionefascicolonuovo");
    String annofascicolo =  this.getRequest().getParameter("annofascicolo");
    String numerofascicolo =  this.getRequest().getParameter("numerofascicolo");
    String username = this.getRequest().getParameter("username");
    String password = this.getRequest().getParameter("password");

    String ruolo = this.getRequest().getParameter("ruolo");
    String nome = this.getRequest().getParameter("nome");
    String cognome = this.getRequest().getParameter("cognome");
    String codiceuo = this.getRequest().getParameter("codiceuo");
    String idutente = this.getRequest().getParameter("idutente");
    String idutenteunop = this.getRequest().getParameter("idutenteunop");
    String entita = "G1STIPULA";
    String key1 = this.getRequest().getParameter("key1");
    String codiceaoo = this.getRequest().getParameter("codiceaoonuovo");
    String codiceufficio = this.getRequest().getParameter("codiceufficionuovo");
    String struttura = this.getRequest().getParameter("strutturaonuovo");
    String societa = this.getRequest().getParameter("societa");
    String codiceGaralotto =  this.getRequest().getParameter("codiceGaralotto");
    String cig = this.getRequest().getParameter("cig");
    String supporto = this.getRequest().getParameter("supporto");
    String tipofascicolo = this.getRequest().getParameter("tipofascicolonuovo");
    String classificadescrizione = this.getRequest().getParameter("classificadescrizione");
    String voce = this.getRequest().getParameter("voce");
    String codiceaoodes = this.getRequest().getParameter("codiceaoodes");
    String codiceufficiodes = this.getRequest().getParameter("codiceufficiodes");
    String RUP = this.getRequest().getParameter("RUP");
    String nomeRup = this.getRequest().getParameter("nomeRup");
    String acronimoRup = this.getRequest().getParameter("acronimoRup");
    String sottotipo = this.getRequest().getParameter("sottotipo");
    String tipofirma = this.getRequest().getParameter("tipofirma");
    String idunitaoperativamittenteDesc = this.getRequest().getParameter("idunitaoperativamittenteDesc");

    String tipoWSDM = null;
    if("1".equals(integrazioneWSDM)){
      WSDMConfigurazioneOutType configurazione = gestioneWSDMManager.wsdmConfigurazioneLeggi("FASCICOLOPROTOCOLLO",idconfi);
      if (configurazione.isEsito())
        tipoWSDM = configurazione.getRemotewsdm();
    }

    WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = null;

    boolean abilitatoInvioMailDocumentale = false;

    if("1".equals(integrazioneWSDM))
      abilitatoInvioMailDocumentale = gestioneWSDMManager.abilitatoInvioMailDocumentale("FASCICOLOPROTOCOLLO",idconfi);

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
    parametri[2] = "G1STIPULA";  //COMENT
    String codStipula = (String) sqlManager.getObject("SELECT codstipula FROM g1stipula WHERE id=?", new Object[] {idStipula});
    parametri[3] = codStipula;  //COMKEY1
    parametri[4] = (long) profilo.getId(); //COMCODOPE
    Timestamp dataOdierna = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
    parametri[5] = dataOdierna; //COMDATINS
    parametri[6] = datiForm.getString("COMMITT"); //COMMITT
    String stato ="2";
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
    String cenint = "";
    if(!"".equals(cenint)){
      parametri[14] = cenint;
    }else{
      parametri[14] = uffintAbilitato;
    }

    this.sqlManager.update("INSERT INTO w_invcom(IDPRG,IDCOM,COMENT,COMKEY1,COMCODOPE,COMDATINS,"
        + "COMMITT,COMSTATO,COMINTEST,COMMSGOGG,COMMSGTES,COMPUB,COMMSGTIP,COMNORISPONDI,IDCFG)"
        + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", parametri);

    if("1".equals(integrazioneWSDM)){
      if(password==null)
        password="";

      if("".equals(codiceaoo))
        codiceaoo=null;

      if("".equals(codiceufficio))
        codiceufficio=null;

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
      par.put("idunitaoperativamittente",idunitaoperativamittente);
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
      par.put("servizio","FASCICOLOPROTOCOLLO");
      par.put("numeroallegati", new Long(0));
      par.put("struttura", struttura);
      par.put("supporto", supporto);
      par.put("tipofascicolo", tipofascicolo );
      par.put("classificadescrizione", classificadescrizione );
      par.put("voce", voce );
      par.put("sottotipo", sottotipo );
      par.put("RUP", RUP );
      par.put("nomeRup", nomeRup );
      par.put("acronimoRup", acronimoRup );
      par.put("tipofirma", tipofirma);
      par.put("idunitaoperativamittenteDesc", idunitaoperativamittenteDesc);
      wsdmProtocolloDocumentoIn = gestioneWSDMManager.wsdmProtocolloDocumentoPopola(par,idconfi);
    }

    String destinatarioPrincipale="";
    //Inserimento destinatari in W_INVCOMDES
    if(numDestinatari!=null && !"".equals(numDestinatari)){
      int numDest = (new Long(numDestinatari)).intValue();

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
        parametri[4] = datiForm.getString("DITTA"); //DESCODSOG
        parametri[5] = datiForm.getString("MAIL");    //DESMAIL
        parametri[6] = datiForm.getString("INTESTAZIONE"); //DESINTEST
        String tipo = datiForm.getString("TIPO");
        Long comtipma = new Long(2);
        if("PEC".equals(tipo))
          comtipma = new Long(1);
        parametri[7] = comtipma;    //COMTIPMA
        sqlManager.update("INSERT INTO w_invcomdes(IDPRG,IDCOM,IDCOMDES,DESCODENT,DESCODSOG,"
            + "DESMAIL,DESINTEST,COMTIPMA) VALUES(?,?,?,?,?,?,?,?)", parametri);

        if("1".equals(integrazioneWSDM)){
          //Destinatari WSDM
          Vector<String> datiDestinatario = new Vector<String>();
          HashMap<String,String> datiImpr = gestioneWSDMManager.getDatiImpresa(datiForm.getString("DITTA"));
          if(datiImpr!=null){
            datiDestinatario.add(0, datiImpr.get("codice"));
            String email= datiImpr.get("emaiip");
            String pec = datiImpr.get("emai2ip");
            String emailPec="";
            if(pec== null)
              emailPec=email;
            else
              emailPec=pec;
            emailPec = UtilityStringhe.convertiNullInStringaVuota(emailPec);
            datiDestinatario.add(1, emailPec);
            datiDestinatario.add(2, datiImpr.get("cognomeIntestazione"));
            datiDestinatario.add(3, datiImpr.get("codiceFiscale"));

            datiDestinatario.add(4, datiImpr.get("indirizzoResidenza"));
            datiDestinatario.add(5, datiImpr.get("comuneResidenza"));
            datiDestinatario.add(6, datiImpr.get("codiceComuneResidenza"));
            datiDestinatario.add(7, datiImpr.get("piva"));
            datiDestinatario.add(8, pec);
            datiDestinatario.add(9, datiImpr.get("proimp"));
            datiDestinatario.add(10, datiImpr.get("capimp"));
          }

          WSDMProtocolloAnagraficaType[] destinatari = new WSDMProtocolloAnagraficaType[1];
          int i=0;
          destinatari[i] = new WSDMProtocolloAnagraficaType();
          destinatari[i].setCodiceFiscale(datiDestinatario.get(3));
          destinatari[i].setPartitaIVA(datiDestinatario.get(7));

          destinatari[i].setIndirizzoResidenza(datiDestinatario.get(4));
          destinatari[i].setComuneResidenza(datiDestinatario.get(5));
          destinatari[i].setCodiceComuneResidenza(datiDestinatario.get(6));
          destinatari[i].setCognomeointestazione(datiForm.getString("INTESTAZIONE"));
          destinatarioPrincipale+=datiForm.getString("INTESTAZIONE");
          if(mezzoinvio!=null && !"".equals(mezzoinvio))
            destinatari[i].setMezzo(mezzoinvio);
          destinatari[i].setEmail(datiForm.getString("MAIL"));
          destinatari[i].setEmailAggiuntiva(datiDestinatario.get(8));
          destinatari[i].setProvinciaResidenza(datiDestinatario.get(9));
          destinatari[i].setCapResidenza(datiDestinatario.get(10));
          destinatari[i].setTipoVoceRubrica(WSDMTipoVoceRubricaType.IMPRESA);

          wsdmProtocolloDocumentoIn.setDestinatari(destinatari);
          destinatarioPrincipale=gestioneWSDMManager.formattazioneDestinatarioPrincipale(destinatarioPrincipale);
          wsdmProtocolloDocumentoIn.setDestinatarioPrincipale(destinatarioPrincipale);
        }

    }

    //gestione allegato di sintesi


    if("1".equals(integrazioneWSDM)){

      //gestione allegato sintesi
      Long idAllegatoSintesi = gestioneWSDMManager.cancellaAllegatoSintesi(idprg,newIdcom);
      String nomeFile=null;
      String estensioneFile = "pdf";
      String titoloFile = null;
      byte[] pdf = null;
      if(idAllegatoSintesi==null) {
        HashMap<String, Object> ret = gestioneWSDMManager.aggiungiAllegatoSintesi(codstipula, null, datiForm.getString("COMMSGOGG"), datiForm.getString("COMMSGTES"), idprg, newIdcom, entita, this.getRequest());
        idAllegatoSintesi = (Long)ret.get("idAllegatoSintesi");
        nomeFile = (String)ret.get("nomeFile");
        estensioneFile = (String)ret.get("estensioneFile");
        titoloFile = (String)ret.get("titoloFile");
        pdf = (byte[]) ret.get("pdf");
      }else {
        Vector<?> datiAllegato = this.sqlManager.getVector("select dignomdoc, digdesdoc from  w_docdig where idprg=? and iddocdig=?", new Object[] {idprg,idAllegatoSintesi});
        if(datiAllegato!=null && datiAllegato.size()>0) {
          nomeFile = SqlManager.getValueFromVectorParam(datiAllegato, 0).getStringValue();
          titoloFile = SqlManager.getValueFromVectorParam(datiAllegato, 1).getStringValue();
          if(nomeFile.endsWith(".tsd"))
          estensioneFile = "tsd";
        }
        BlobFile digogg = fileAllegatoManager.getFileAllegato(idprg, idAllegatoSintesi);
        pdf = digogg.getStream();
      }

      //Come unico allegato c'è il corpo della comunicazione
      WSDMProtocolloAllegatoType[] allegati = new WSDMProtocolloAllegatoType[1];
      // Aggiunta del testo della comunicazione

      allegati[0] = new WSDMProtocolloAllegatoType();
      allegati[0].setNome(nomeFile);
      allegati[0].setTipo(estensioneFile);
      allegati[0].setTitolo(titoloFile);
      allegati[0].setContenuto(pdf);
      if("TITULUS".equals(tipoWSDM))
        allegati[0].setIdAllegato("W_INVCOM|" + idprg + "|" + newIdcom.toString());

      if("NUMIX".equals(tipoWSDM)) {
        allegati[0] = GestioneWSDMManager.popolaAllegatoInfo(nomeFile,allegati[0]);
        allegati[0].setIsSealed(new Long(1));
      }

      wsdmProtocolloDocumentoIn.setAllegati(allegati);

      WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = gestioneWSDMManager.wsdmProtocolloInserisci(username, password,
            ruolo, nome, cognome, codiceuo, idutente, idutenteunop,codiceaoo, codiceufficio, wsdmProtocolloDocumentoIn, idconfi);

      if (wsdmProtocolloDocumentoRes.isEsito()) {
        String numeroDocumento = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroDocumento();
        Long annoProtocollo = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getAnnoProtocollo();
        String numeroProtocollo = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroProtocollo();

        Timestamp dataProtocollo= gestioneWSDMManager.getDataProtocollo(wsdmProtocolloDocumentoRes);
        if(annoProtocollo==null)
          annoProtocollo = gestioneWSDMManager.getAnnoFromDate(dataProtocollo);

        HashMap<String,String> datiWSDM = new HashMap<String,String>();
        datiWSDM.put("numeroDocumento", numeroDocumento);
        datiWSDM.put("annoProtocollo", annoProtocollo.toString());
        datiWSDM.put("numeroProtocollo", numeroProtocollo);
        datiWSDM.put("oggettodocumento", oggettodocumento);
        datiWSDM.put("testoMail", datiForm.getString("COMMSGTES"));
        datiWSDM.put("indirizzomittente", indirizzoMittente);
        datiWSDM.put("formatoMail", "2");
        datiWSDM.put("idconfi", idconfi);
        datiWSDM.put("username", username);
        datiWSDM.put("password", password);
        datiWSDM.put("ruolo", ruolo);
        datiWSDM.put("nome", nome);
        datiWSDM.put("cognome", cognome);
        datiWSDM.put("codiceuo", codiceuo);

        String statoComunicazione =  gestioneWSDMManager.wsdmInvioMailEAggiornamentoDb(idprg, "FASCICOLOPROTOCOLLO", newIdcom.toString(), datiWSDM,allegati,0);

        // Salvataggio del numero protocollo nella comunicazione ed impostazione
        // dello stato a "In uscita"
        //Il campo COMMITT va aggiornato solo se indirizzoMittente è valorizzato

        Object param[]=null;
        String updateW_INVCOM = null;

        if(abilitatoInvioMailDocumentale && ("JIRIDE".equals(tipoWSDM)) || "ARCHIFLOW".equals(tipoWSDM) || "ARCHIFLOWFA".equals(tipoWSDM)){
          updateW_INVCOM = "update w_invcom set comstato = ?, comdatprot = ?, comnumprot = ?, committ = ? where idprg = ? and idcom = ?";
          param = new Object[]{statoComunicazione, dataProtocollo,
              numeroProtocollo, indirizzoMittente, idprg,  newIdcom };
        }else{
          updateW_INVCOM = "update w_invcom set comstato = ?, comdatprot = ?, comnumprot = ? where idprg = ? and idcom = ?";
          param = new Object[]{statoComunicazione, dataProtocollo,
              numeroProtocollo, idprg,  newIdcom };
        }
        this.sqlManager.update(updateW_INVCOM, param);

        // Salvataggio del riferimento al fascicolo
        if ("SI_FASCICOLO_NUOVO".equals(inserimentoinfascicolo) ) {
          String codiceFascicoloNUOVO =null;
          Long annoFascicoloNUOVO = null;
          String numeroFascicoloNUOVO = null;

          codiceFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getCodiceFascicolo();
          if (wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getAnnoFascicolo() != null)
            annoFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getAnnoFascicolo();
          else
            annoFascicoloNUOVO = gestioneWSDMManager.getAnnoFromDate(dataProtocollo);
          numeroFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getNumeroFascicolo();



          //String descrizioneFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getDescrizioneFascicolo();
          gestioneWSDMManager.setWSFascicolo(entita, key1, null, null, null, codiceFascicoloNUOVO, annoFascicoloNUOVO,
              numeroFascicoloNUOVO, classificafascicolo,codiceaoo, codiceufficio,struttura,null,classificadescrizione,voce,codiceaoodes,codiceufficiodes);
        }

        //Salvatagio in WSDOCUMENTO
        Long idWSDocumento = gestioneWSDMManager.setWSDocumento(entita, key1, null, null, null, numeroDocumento, annoProtocollo, numeroProtocollo, oggettodocumento,inout);

        //Salvataggio della mail in WSALLEGATI
        gestioneWSDMManager.setWSAllegati("W_INVCOM", idprg, newIdcom.toString(), null, null, idWSDocumento);

        //Salvataggio della mail in WSALLEGATI dell'allegato di sintesi
        gestioneWSDMManager.setWSAllegati("W_DOCDIG", idprg, idAllegatoSintesi.toString(), null, null, idWSDocumento);
      }else{
        String messaggio = wsdmProtocolloDocumentoRes.getMessaggio();
        throw new GestoreException("Errore nella protocollazione del fascicolo","wsdm.fascicoloprotocollo.protocollazione.error",new Object[]{messaggio}, new Exception());
      }
    }else {
      //gestione allegato sintesi
      HashMap<String, Object> ret = gestioneWSDMManager.aggiungiAllegatoSintesi(codstipula, null, datiForm.getString("COMMSGOGG"), datiForm.getString("COMMSGTES"), idprg, newIdcom, entita,this.getRequest());
      if(ret==null) {
        throw new GestoreException("Errore nella creazione della marca temporale dell'allegato di sintesi","marcaTemporale",null, new Exception());
      }

    }


  }

  private String creazioneFasciolo(Long idStipula, String idconfi, String codstipula) throws Exception {
    GestioneWSDMManager gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        this.getServletContext(), GestioneWSDMManager.class);

    String username = this.getRequest().getParameter("username");
    String password = this.getRequest().getParameter("password");
    String ruolo = this.getRequest().getParameter("ruolo");
    String nome = this.getRequest().getParameter("nome");
    String cognome = this.getRequest().getParameter("cognome");
    String codiceuo = this.getRequest().getParameter("codiceuo");
    String idutente = this.getRequest().getParameter("idutente");
    String idutenteunop = this.getRequest().getParameter("idutenteunop");

    String oggettofascicolo =  this.getRequest().getParameter("oggettofascicolonuovo");
    String classificafascicolo =  this.getRequest().getParameter("classificafascicolonuovo");
    String descrizionefascicolo =  this.getRequest().getParameter("descrizionefascicolonuovo");
    String tipofascicolo = this.getRequest().getParameter("tipofascicolonuovo");
    String nomeRup = this.getRequest().getParameter("nomeRup");
    String acronimoRup = this.getRequest().getParameter("acronimoRup");
    String struttura = this.getRequest().getParameter("strutturaonuovo");

    String tiposistemaremoto = this.getRequest().getParameter("tiposistemaremoto");
    String entita = this.getRequest().getParameter("entita");
    String key1 = this.getRequest().getParameter("key1");
    String genereGara = this.getRequest().getParameter("genereGara");
    String servizio = this.getRequest().getParameter("servizio");
    String uocompetenza = this.getRequest().getParameter("uocompetenza");
    String uocompetenzadescrizione = this.getRequest().getParameter("uocompetenzadescrizione");

    String riservatezzaAttiva = ConfigManager.getValore("wsdm.applicaRiservatezza."+idconfi);

    Long isRiservatezza = null;
    if("JIRIDE".equals(tiposistemaremoto) && "1".equals(riservatezzaAttiva) && !"10".equals(genereGara) && !"11".equals(genereGara) && !"20".equals(genereGara) && !"G1STIPULA".equals(entita)){
      isRiservatezza = new Long(1);
    }

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

    String messaggio = gestioneWSDMManager.setFascicolo(tiposistemaremoto, servizio, idconfi, entita, key1, isRiservatezza, parWSDM);

   return messaggio;

  }

}
