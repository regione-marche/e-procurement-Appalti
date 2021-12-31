/*
 * Created on 12/06/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiRicezioneFunction;
import it.eldasoft.sil.pg.tags.gestori.submit.GestoreDITG;
import it.eldasoft.sil.pg.tags.gestori.submit.GestoreFasiRicezione;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneDocument;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneType;
import it.eldasoft.utils.utility.UtilityDate;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.xmlbeans.XmlException;
import org.springframework.transaction.TransactionStatus;

public class AcquisisciOfferteDaPortaleAction extends Action {

  static Logger               logger = Logger.getLogger(AcquisisciOfferteDaPortaleAction.class);

  private SqlManager          sqlManager;

  private GeneManager         geneManager;

  private PgManager           pgManager;

  private PgManagerEst1       pgManagerEst1;

  private MEPAManager         mepaManager;

  private String selectW_DOCDIG = "select idprg, iddocdig from w_docdig where digent = ? and idprg = ? and digkey1 = ?";

  private TransactionStatus status = null;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  public void setPgManager(PgManager pgManager) {
    this.pgManager = pgManager;
  }

  public void setPgManagerEst1(PgManagerEst1 pgManagerEst1) {
    this.pgManagerEst1 = pgManagerEst1;
  }

  public void setMepaManager(MEPAManager mepaManager) {
    this.mepaManager = mepaManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    logger.info("Acquisizione offerte da portale Appalti: inizio");

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();
    ResourceBundle resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);


    boolean commit = true;

    int numeroAcquisizioni = 0;
    int numeroAcquisizioniErrore = 0;

    String ngara = request.getParameter("ngara");

    String codgar1 = (String) this.sqlManager.getObject("select codgar1 from gare where ngara = ?", new Object[] { ngara });
    Long iterga = null;
    Long offtel = null;
    Vector<?> datiTorn = this.sqlManager.getVector("select iterga,offtel from torn where codgar = ?", new Object[] { codgar1 });
    if(datiTorn !=null && datiTorn.size()>0){
      iterga = SqlManager.getValueFromVectorParam(datiTorn, 0).longValue();
      offtel = SqlManager.getValueFromVectorParam(datiTorn, 1).longValue();
    }
    String dbFunctionDateToString = sqlManager.getDBFunction("DATETIMETOSTRING",
        new String[] { "comdatprot" });
    String selectW_INVCOM = "select idprg, idcom, comkey1, comnumprot, " + dbFunctionDateToString + ", comkey3 from w_invcom where comkey2 = ? and comstato = '5' and comtipo = 'FS11' order by comkey1, comkey3";
    String selectW_PUSER = "select userkey1 from w_puser where usernome = ?";

    String selectDITG = "select count(*) from ditg where codgar5 = ? and ngara5 = ? and (ammgar is null or ammgar = '1') and dittao = ? and ncomope=?";
    String selectRaggruppamentoDITG = "select dittao from ditg where codgar5 = ? and ngara5 = ? and (ammgar is null or ammgar = '1') and "
        + "dittao=(select codime9 from ragimp where coddic =? and impman='1' and codime9=dittao) and ncomope = ? ";
    String selectDITGNcomopeMinimo = "select invoff, rtofferta from ditg where codgar5 = ? and ngara5 = ? and dittao = ? and ncomope != ? order by ncomope";
    String selectRaggruppamentoDITGNcomopeMinimo = "select ncomope,dittao,invoff from ditg where codgar5 = ? and ngara5 = ? and "
        + "dittao=(select codime9 from ragimp where coddic =? and impman='1' and codime9=dittao) and ncomope != ? order by ncomope";
    String updateW_INVCOM = "update w_invcom set comstato = ? where idprg = ? and idcom = ?";
    String updateDITG = "update ditg set datoff = ?, oraoff = ?, mezoff = 5, invoff='1', nproff=?, dproff=? where codgar5 = ? and ngara5 = ? and dittao = ?";

    //variabili per tracciatura eventi
    String genericMsgErr = "Errore inaspettato durante la tracciatura su w_logeventi";
    int livEvento = 1;
    String errMsgEvento = "";
    //Tracciatura eventi
    try {
      LogEvento logEvento = LogEventiUtils.createLogEvento(request);
      logEvento.setLivEvento(livEvento);
      logEvento.setOggEvento(ngara);
      logEvento.setCodEvento("GA_ACQUISIZIONE_OFFERTE_INIT");
      logEvento.setDescr("Inizio acquisizione offerte da portale Appalti");
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);
    } catch (Exception le) {
      logger.error(genericMsgErr, le);
    }

    String messageKey="";
    String messaggioPerLog ="";
    boolean erroreGestito=false;
    Long genere = null;

    try{
      //Controllo che da profilo sia abilitata l'acquisizione di acquisizione, che l'utente abbia i permessi di modifica
      //e che la fase della gara sia quella attesa
      if (!this.geneManager.getProfili().checkProtec(
          (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS",
          "ALT.GARE.FASIRICEZIONE.AcquisisciOfferteDaPortale") || !this.pgManager.getAutorizzatoModificaGara("V_GARE_TORN",
              "CODGAR", codgar1, true, "2", request)
          || this.mepaManager.esisteBloccoCondizioniFasiAcquisizioni(ngara, new Long(1))) {
        erroreGestito=true;
        messageKey ="errors.gestoreException.*.acquisizione.OfferteDaPortale.DatiIncosistenti";
        messaggioPerLog = resBundleGenerale.getString(messageKey);
        throw new GestoreException("Non é possibile procedere con l'acquisizione delle offerte da portale Appalti perché dati inconsistenti.","acquisizione.OfferteDaPortale.DatiIncosistenti" , new Exception());
      }

      //Controllo che la data termine presentazione offerte non sia passata
      String esitoControllo[] = mepaManager.controlloDataConDataAttuale(ngara,"dteoff","oteoff");
      if("false".equals(esitoControllo[0])){
        erroreGestito=true;
        messageKey ="errors.gestoreException.*.acquisizione.OfferteDaPortale.TerminiNonScaduti";
        messaggioPerLog = resBundleGenerale.getString(messageKey);
        messaggioPerLog = messaggioPerLog.replace("{0}", esitoControllo[1]);
        messaggioPerLog = messaggioPerLog.replace("{1}", esitoControllo[2]);
        throw new GestoreException("Non é possibile procedere con l'acquisizione delle offerte da portale Appalti perché termini non scaduti.","acquisizione.OfferteDaPortale.TerminiNonScaduti", new Object[]{esitoControllo[1],esitoControllo[2]} , new Exception());
      }

      // Si determina se la gara è ad Offerta unica
      genere = (Long) this.sqlManager.getObject("select genere from gare where ngara=?", new Object[] { ngara });

      List<?> datiW_INVCOM = this.sqlManager.getListVector(selectW_INVCOM, new Object[] { ngara });
      if (datiW_INVCOM != null && datiW_INVCOM.size() > 0) {
        Double faseGara = new Double(Math.floor(GestioneFasiRicezioneFunction.FASE_RICEZIONE_PLICHI / 10));
        Long faseGaraLong = new Long(faseGara.longValue());
        String codiceDittaEventi=null;
        int esitoAquisizione=1;

        String comkey3=null;
        String invoff = null;
        String rtofferta = null;
        String gestione = null; // valori gestiti: "S" standard, "R" solo inserimento RT
        boolean acquisito = false;
        boolean esisteDITG = false;

        for (int i = 0; i < datiW_INVCOM.size(); i++) {
          try {
            //variabili per tracciatura eventi
            livEvento = 3;
            errMsgEvento = genericMsgErr;
            status = this.sqlManager.startTransaction();
            acquisito = false;
            esisteDITG = false;

            String w_invcom_idprg = (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 0).getValue();
            Long w_invcom_idcom = (Long) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 1).getValue();
            String comkey1 = (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 2).getValue();
            String userkey1 = (String) this.sqlManager.getObject(selectW_PUSER, new Object[] { comkey1 });
            String comnumprot = (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 3).getValue();
            Timestamp comdatprotTimestamp=null;
            String comdatprotString = (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 4).getValue();
            if(comdatprotString!=null && !"".equals(comdatprotString)){
              comdatprotTimestamp = new Timestamp(UtilityDate.convertiData(comdatprotString, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS).getTime());
            }
            comkey3 = (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 5).getValue();
            if (iterga != null && iterga.longValue() != 1) {


              if (userkey1 != null) {
                //Si cerca se vi è una ditta in gara con stesso progressivo della comunicazione (comkey3 = ncomope)
                Long conteggio = (Long) this.sqlManager.getObject(selectDITG, new Object[] { codgar1, ngara, userkey1,comkey3 });
                if (conteggio != null && conteggio.longValue() > 0) {
                  gestione  = "S";
                  //Si devono estrarre i dati NCOMOPE, INVOFF  e RTOFFERTA di ditg per determinare come gestire l'offerta,
                  // perchè nel caso di offerta unica, c'è la possibilità di presentare più offerte
                  if(new Long(3).equals(genere)){
                    Vector<?> datiDitta = this.sqlManager.getVector("select invoff, rtofferta from ditg where codgar5 = ? and ngara5 = ? and dittao = ?", new Object[] { codgar1, ngara, userkey1 });
                    if(datiDitta!=null && datiDitta.size()>0){
                      invoff = SqlManager.getValueFromVectorParam(datiDitta, 0).getStringValue();
                      rtofferta = SqlManager.getValueFromVectorParam(datiDitta, 1).getStringValue();

                      if(invoff=="")
                        invoff=null;
                      if(rtofferta=="")
                        rtofferta=null;

                      if(invoff == null && rtofferta == null)
                        gestione  = "S";
                      else
                        gestione  = "RT";

                    }

                  }

                  Object[] ret = this.gestioneRT(userkey1, userkey1, w_invcom_idprg, w_invcom_idcom, ngara, codgar1, gestione, comnumprot, comdatprotTimestamp, comkey3, iterga, faseGaraLong, offtel,request);
                  acquisito = ((Boolean)ret[0]).booleanValue();
                  esisteDITG = ((Boolean)ret[1]).booleanValue();
                  codiceDittaEventi = (String)ret[2];

                } else {
                  // Se l'impresa partecipa come mandataria di un RT allora si
                  // deve estrarre il codice della RTI
                  List<?> listaDatiRaggruppamento = this.sqlManager.getListVector(selectRaggruppamentoDITG, new Object[] { codgar1, ngara,
                      userkey1,comkey3 });
                  if(!new Long(3).equals(genere)){
                    // Se l'impresa è presente come mandataria in più RT allora è
                    // un errore per lotto unico
                    if (listaDatiRaggruppamento != null && listaDatiRaggruppamento.size() == 1) {
                      userkey1 = (String) SqlManager.getValueFromVectorParam(listaDatiRaggruppamento.get(0), 0).getValue();
                      esisteDITG = true;
                    }
                  }else{
                    //Offerta unica
                    if (listaDatiRaggruppamento != null && listaDatiRaggruppamento.size() > 0) {
                      userkey1 = (String) SqlManager.getValueFromVectorParam(listaDatiRaggruppamento.get(0), 0).getValue();
                      esisteDITG = true;
                    }else{
                      //Non è stata trovata una corrispondenza ne fra le ditte singole ne fra i RT aventi come mandataria la ditta della comunicazione
                      //aventi comkey3 = numope, si deve cercare fra le ditte con numope < comkey3
                      listaDatiRaggruppamento = null;
                      List<?> listaDatiDitta = this.sqlManager.getListVector(selectDITGNcomopeMinimo, new Object[] { codgar1, ngara, userkey1,comkey3 });
                      String codiceDittaInv = userkey1;
                      if (listaDatiDitta != null && listaDatiDitta.size() > 0) {
                        //Trovata una ditta non RT con numope < comkey3
                        invoff = SqlManager.getValueFromVectorParam(listaDatiDitta.get(0), 0).getStringValue();
                        rtofferta = SqlManager.getValueFromVectorParam(listaDatiDitta.get(0), 1).getStringValue();

                        if(invoff=="")
                          invoff=null;
                        if(rtofferta=="")
                          rtofferta=null;

                        if(invoff == null && rtofferta == null)
                          gestione  = "S";
                        else {
                          gestione  = "RT";
                        }

                      }else{
                        //Trovata un RT con numope < comkey3
                        listaDatiRaggruppamento = this.sqlManager.getListVector(selectRaggruppamentoDITGNcomopeMinimo, new Object[] { codgar1, ngara,
                            userkey1,comkey3 });
                        if (listaDatiRaggruppamento != null && listaDatiRaggruppamento.size() > 0) {
                          String ncompopeMinimo = (String) SqlManager.getValueFromVectorParam(listaDatiRaggruppamento.get(0), 0).getValue();
                          String codiceRT = (String) SqlManager.getValueFromVectorParam(listaDatiRaggruppamento.get(0), 1).getValue();
                          invoff = (String) SqlManager.getValueFromVectorParam(listaDatiRaggruppamento.get(0), 2).getValue();
                          Vector<?> datiMandataria = this.sqlManager.getVector("select rtofferta from ditg where codgar5 = ? and ngara5 = ? "
                              + "and dittao=? and ncomope=?", new Object[]{codgar1, ngara,codiceRT,ncompopeMinimo});
                          if(datiMandataria!=null && datiMandataria.size()>0){
                            rtofferta = SqlManager.getValueFromVectorParam(datiMandataria, 0).getStringValue();
                          }
                          codiceDittaInv = codiceRT;
                          if(invoff=="")
                            invoff=null;
                          if(rtofferta=="")
                            rtofferta=null;

                          if(invoff == null && rtofferta == null)
                            gestione  = "S";
                          else {
                            gestione  = "RT";
                          }
                        }
                      }
                      if((listaDatiDitta!=null && listaDatiDitta.size()>0) || (listaDatiRaggruppamento != null && listaDatiRaggruppamento.size() > 0)){
                        Object[] ret = this.gestioneRT(userkey1, codiceDittaInv, w_invcom_idprg, w_invcom_idcom, ngara, codgar1, gestione, comnumprot, comdatprotTimestamp, comkey3, iterga, faseGaraLong, offtel, request);
                        acquisito = ((Boolean)ret[0]).booleanValue();
                        esisteDITG = ((Boolean)ret[1]).booleanValue();
                        codiceDittaEventi = (String)ret[2];
                      }

                    }
                  }
                }
              }

              if (esisteDITG) {
                // Lettura della data e dell'ora dal file XML
                TipoPartecipazioneDocument document = mepaManager.estrazioneDocumentMessaggioFS10_FS11(selectW_DOCDIG, w_invcom_idprg,
                    w_invcom_idcom.toString());
                if (document != null) {
                  Calendar dataPresentazione = document.getTipoPartecipazione().getDataPresentazione();
                  if (dataPresentazione != null) {
                    Date datoff = dataPresentazione.getTime();
                    String datoffFormat = UtilityDate.convertiData(datoff, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
                    if (datoffFormat != null) {
                      String oraoff = datoffFormat.substring(11);
                      this.sqlManager.update(updateDITG, new Object[] { datoff, oraoff, comnumprot, comdatprotTimestamp,  codgar1, ngara, userkey1 });

                      acquisito = true;
                      codiceDittaEventi=userkey1;
                    }
                  }
                  //Nel caso di bustalotti=1 si deve gestire l'aggiornamento della partecipazione dei lotti
                  TipoPartecipazioneType tipoPartecipazione = document.getTipoPartecipazione();
                  GestoreDITG gestoreDITG = new GestoreDITG();
                  gestoreDITG.setRequest(request);
                  mepaManager.gestioneLottiOffertaUnica(ngara, userkey1, null, gestoreDITG, tipoPartecipazione.getCodiceLottoArray(), faseGaraLong, status, "AGG",false,"FS11",iterga);

                }
              }
            } else if (iterga != null && iterga.longValue() == 1) {
              boolean gestioneComponenti = false;
              String codiceRaggruppamento = "";
              String codimp = "";
              String datiControllo[] = pgManager.controlloEsistenzaDittaElencoGara(userkey1, ngara, "$" + ngara, null);
              if ("1".equals(datiControllo[0]) || "2".equals(datiControllo[0])) {
                // Ditta già presente in gara
                String msg = "La ditta " + comkey1 + " è già presente in gara o direttamente o come mandataria di una RTI";
                logger.error(msg);
              } else {
                // Se l'impresa si presenta come RTI se ne deve gestire
                // l'inserimento in anagrafica
                codimp = userkey1;
                TipoPartecipazioneDocument document = mepaManager.estrazioneDocumentMessaggioFS10_FS11(selectW_DOCDIG, w_invcom_idprg,
                    w_invcom_idcom.toString());
                if (document != null) {
                  String ragioneSociale = (String) sqlManager.getObject("select nomest from impr where codimp=?", new Object[] { codimp });
                  if (ragioneSociale.length() > 61) ragioneSociale = ragioneSociale.substring(0, 60);
                  if (document.getTipoPartecipazione().getRti()) {
                    // Inserimento in anagrafica della RTI
                    String nomeRTI = document.getTipoPartecipazione().getDenominazioneRti();
                    Double quotaMandataria = null;
                    if (document.getTipoPartecipazione().isSetQuotaMandataria())
                      quotaMandataria = new Double(document.getTipoPartecipazione().getQuotaMandataria());
                    codiceRaggruppamento = geneManager.calcolaCodificaAutomatica("IMPR", "CODIMP");
                    String nomeRTITroncato = nomeRTI;
                    if (nomeRTITroncato.length() > 61) nomeRTITroncato = nomeRTITroncato.substring(0, 60);
                    Long tipimp = (Long) sqlManager.getObject("select tipimp from impr where codimp=?", new Object[] { codimp });
                    Long tipimpNewRTI = null;
                    if (tipimp != null && tipimp.longValue() <= 5)
                      tipimpNewRTI = new Long(3);
                    else if (tipimp != null && tipimp.longValue() > 5) tipimpNewRTI = new Long(10);
                    sqlManager.update("insert into IMPR (CODIMP,NOMIMP,NOMEST,TIPIMP) values(?,?,?,?)", new Object[] { codiceRaggruppamento,
                        nomeRTITroncato, nomeRTI, new Long(tipimpNewRTI) });
                    sqlManager.update("insert into RAGIMP (IMPMAN,CODDIC,NOMDIC,CODIME9,QUODIC) values(?,?,?,?,?)", new Object[] { "1",
                        codimp, ragioneSociale, codiceRaggruppamento, new Double(quotaMandataria) });
                    gestioneComponenti = true;
                    codimp = codiceRaggruppamento;
                    ragioneSociale = nomeRTITroncato;
                  } else {
                    // Si controlla se l'impresa è un consorzio
                    Long tipoImpresa = (Long) this.sqlManager.getObject("select tipimp from impr where codimp=?", new Object[] { userkey1 });
                    if (tipoImpresa != null && (tipoImpresa.longValue() == 2 || tipoImpresa.longValue() == 11)) {
                      gestioneComponenti = true;
                      codiceRaggruppamento = userkey1;
                    }
                  }
                  // Inserimento delle componenti del consorzio/RTI
                  ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
                  TipoPartecipazioneType tipoPartecipazione = document.getTipoPartecipazione();
                  String vettorePartecipazioneLotti[] = tipoPartecipazione.getCodiceLottoArray();
                  if (gestioneComponenti) {
                    // Si controlla se vi sono partecipanti
                    if (tipoPartecipazione.isSetPartecipantiRaggruppamento()) {
                      Long tipoImpresa = new Long(2);
                      if (tipoPartecipazione.isSetDenominazioneRti()) tipoImpresa = new Long(3);
                      pgManager.gestionePartecipanti(tipoPartecipazione.getPartecipantiRaggruppamento(), codiceRaggruppamento, tipoImpresa,
                          profilo.getId(), ngara, userkey1);
                    }
                  }
                  // Inserimento in DITG
                  Vector<DataColumn> elencoCampi = new Vector<DataColumn>();
                  elencoCampi.add(new DataColumn("DITG.NGARA5", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
                  elencoCampi.add(new DataColumn("DITG.CODGAR5", new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar1)));
                  elencoCampi.add(new DataColumn("DITG.DITTAO", new JdbcParametro(JdbcParametro.TIPO_TESTO, codimp)));
                  elencoCampi.add(new DataColumn("DITG.NOMIMO", new JdbcParametro(JdbcParametro.TIPO_TESTO, ragioneSociale)));
                  elencoCampi.add(new DataColumn("DITG.NPROGG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
                  elencoCampi.add(new DataColumn("DITG.NUMORDPL", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
                  elencoCampi.add(new DataColumn("DITG.NPROFF", new JdbcParametro(JdbcParametro.TIPO_TESTO, comnumprot)));
                  elencoCampi.add(new DataColumn("DITG.DPROFF", new JdbcParametro(JdbcParametro.TIPO_DATA, comdatprotTimestamp)));
                  elencoCampi.add(new DataColumn("DITG.NCOMOPE", new JdbcParametro(JdbcParametro.TIPO_TESTO, comkey3)));

                  Calendar dataPresentazione = document.getTipoPartecipazione().getDataPresentazione();
                  if (dataPresentazione != null) {
                    Date datoff = dataPresentazione.getTime();
                    String datoffFormat = UtilityDate.convertiData(datoff, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
                    if (datoffFormat != null) {
                      String oraoff = datoffFormat.substring(11);
                      elencoCampi.add(new DataColumn("DITG.DATOFF", new JdbcParametro(JdbcParametro.TIPO_DATA, datoff)));
                      elencoCampi.add(new DataColumn("DITG.ORAOFF", new JdbcParametro(JdbcParametro.TIPO_TESTO, oraoff)));
                      elencoCampi.add(new DataColumn("DITG.MEZOFF", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Long(5))));
                      elencoCampi.add(new DataColumn("DITG.INVOFF", new JdbcParametro(JdbcParametro.TIPO_TESTO, "1")));
                    }
                  }
                  DataColumnContainer containerDITG = new DataColumnContainer(elencoCampi);
                  GestoreDITG gestoreDITG = new GestoreDITG();
                  gestoreDITG.setRequest(request);
                  gestoreDITG.inserisci(status, containerDITG);
                  // Inizializzazione documenti della ditta
                  pgManager.inserimentoDocumentazioneDitta(codgar1, ngara, codimp);

                  // Se la gara è ad offerta unica si devono inserire le ditte
                  // per ogni lotto
                  mepaManager.gestioneLottiOffertaUnica(ngara, codimp, containerDITG, gestoreDITG,vettorePartecipazioneLotti,faseGaraLong,status,"INS",false,"FS11",iterga);
                  acquisito = true;
                  codiceDittaEventi=codimp;
                }
              }
            }

            esitoAquisizione=1;
            if (acquisito) {
              //Aggiornamento di DITGEVENTI
              Date dataCorrente=new Date();
              Long numOccorrenze=(Long)this.sqlManager.getObject("select count(ngara) from DITGEVENTI where DITGEVENTI.NGARA=? and DITGEVENTI.DITTAO=? and DITGEVENTI.CODGAR=?", new Object[]{ngara,codiceDittaEventi,codgar1});
              if(numOccorrenze!=null && numOccorrenze.longValue()>0){
                String sqlUpdate="update ditgeventi set DATFS11=? where ngara=? and dittao=? and codgar=?";
                this.sqlManager.update(sqlUpdate, new Object[]{new Timestamp(dataCorrente.getTime()),ngara,codiceDittaEventi,codgar1});
              }else{
                String sqlInsert="insert into ditgeventi(NGARA,DITTAO,CODGAR,DATFS11) values(?,?,?,?)";
                this.sqlManager.update(sqlInsert, new Object[]{ngara,codiceDittaEventi, codgar1, new Timestamp(dataCorrente.getTime())});
              }


              this.sqlManager.update(updateW_INVCOM, new Object[] { "6", w_invcom_idprg, w_invcom_idcom });
              numeroAcquisizioni++;
            } else {
              this.sqlManager.update(updateW_INVCOM, new Object[] { "7", w_invcom_idprg, w_invcom_idcom });
              numeroAcquisizioniErrore++;
              esitoAquisizione=3;
            }

            //Tracciatura acquisizione singolo messaggio
            LogEvento logEvento = LogEventiUtils.createLogEvento(request);
            logEvento.setLivEvento(esitoAquisizione);
            logEvento.setOggEvento(ngara);
            logEvento.setCodEvento("GA_ACQUISIZIONE_OFFERTE");
            logEvento.setDescr("Acquisizione offerta ditta " + codiceDittaEventi + " (cod.operatore " + comkey1 + ", id.comunicazione " + w_invcom_idcom + ")" );
            logEvento.setErrmsg("");
            LogEventiUtils.insertLogEventi(logEvento);

          } finally {
            if (status != null) {
              try {
                if (commit == true) {
                  livEvento = 1;
                  errMsgEvento = "";
                  this.sqlManager.commitTransaction(status);
                } else {
                  this.sqlManager.rollbackTransaction(status);
                }
              } catch (SQLException e) {

              }
            }
          }
        }
      }

      try {
        //variabili per tracciatura eventi
        livEvento = 3;
        errMsgEvento = genericMsgErr;
        status = this.sqlManager.startTransaction();

        if (iterga != null && iterga.longValue() != 1) {


          // Esclusione ditte che non hanno presentato offerta
          String selectCONTA_W_INVCOM = "select count(*) from w_invcom where comkey2 = ? and comstato in ('5','7','9') and comtipo = 'FS11'";
          String selectDITG_ESCLUSIONE = "select dittao from ditg where codgar5 = ? and ngara5 = ? and datoff is null and (ammgar is null or ammgar = '1')";
          String updateDITG_ESCLUSIONE = "update ditg set invoff = '2', ammgar = '2', fasgar = 1, numordpl = null where codgar5 = ? and ngara5 = ? and dittao = ?";

          Long contaW_INVCOM = (Long) this.sqlManager.getObject(selectCONTA_W_INVCOM, new Object[] { ngara });
          if (contaW_INVCOM == null || (contaW_INVCOM != null && contaW_INVCOM.longValue() == 0)) {
            List<?> datiDITG_ESCLUSIONE = this.sqlManager.getListVector(selectDITG_ESCLUSIONE, new Object[] { codgar1, ngara });
            if (datiDITG_ESCLUSIONE != null && datiDITG_ESCLUSIONE.size() > 0) {
              GestoreDITG gestoreDITG = new GestoreDITG();
              gestoreDITG.setRequest(request);

              for (int iEsclusione = 0; iEsclusione < datiDITG_ESCLUSIONE.size(); iEsclusione++) {
                String dittao_esclusione = (String) SqlManager.getValueFromVectorParam(datiDITG_ESCLUSIONE.get(iEsclusione), 0).getValue();
                if (genere != null && genere.longValue() == 3){
                  // Se la gara è ad offerta unica si deve procedere all'esclusione dei
                  // lotti
                  updateDITG_ESCLUSIONE = "update ditg set invoff = '2', ammgar = '2', numordpl = null where codgar5 = ? and dittao = ?";
                  this.sqlManager.update(updateDITG_ESCLUSIONE, new Object[] { codgar1, dittao_esclusione });
                  updateDITG_ESCLUSIONE = "update ditg set fasgar = 1 where codgar5 = ? and dittao = ? and (partgar is null or partgar!=2)";
                  this.sqlManager.update(updateDITG_ESCLUSIONE, new Object[] { codgar1, dittao_esclusione });
                }else
                  this.sqlManager.update(updateDITG_ESCLUSIONE, new Object[] { codgar1, ngara, dittao_esclusione });

                // Inserimento/aggiornamento in DITGAMMIS
                gestoreDITG.gestioneDITGAMMIS(codgar1, ngara, dittao_esclusione, new Long(1), status);
              }
            }
          }
        }

        //Si deve calcolare il numero ordine secondo il criterio della data di presentazione offerta
        String isGaraLottiConOffertaUnica = "false";
        if(new Long(3).equals(genere))
          isGaraLottiConOffertaUnica = "true";

        String isProceduraAggiudicazioneAperta = "false";
        if(new Long(1).equals(iterga))
          isProceduraAggiudicazioneAperta = "true";

        Long paginaAttiva = new Long(GestioneFasiRicezioneFunction.FASE_RICEZIONE_PLICHI);

        pgManagerEst1.setNumeroOrdine(ngara, codgar1, isGaraLottiConOffertaUnica, isProceduraAggiudicazioneAperta, "false",paginaAttiva.toString(), 4);

      } finally {
        if (status != null) {
          try {
            if (commit == true) {
              livEvento = 1;
              errMsgEvento = "";
              this.sqlManager.commitTransaction(status);
            } else {
              this.sqlManager.rollbackTransaction(status);
            }
          } catch (SQLException e) {

          }
        }
      }
    } catch (GestoreException ge) {
      livEvento = 3;
      if(erroreGestito){
        errMsgEvento = messaggioPerLog;
        result.put("Esito", "Errore");
        result.put("MsgErrore", errMsgEvento);
      }else
        errMsgEvento = ge.getMessage();

      throw ge;
    }finally{
      //Tracciatura eventi
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(ngara);
        logEvento.setCodEvento("GA_ACQUISIZIONE_OFFERTE_FINE");
        logEvento.setDescr("Fine acquisizione offerte da portale Appalti:" +
            numeroAcquisizioni + " acquisizioni, " +
            numeroAcquisizioniErrore + " acquisizioni con errore");
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        logger.error(genericMsgErr, le);
      }

    logger.info("Acquisizione offerte da portale Appalti, numero acquisizioni: " + numeroAcquisizioni);
    logger.info("Acquisizione offerte da portale Appalti, numero acquisizioni con errore: " + numeroAcquisizioniErrore);

    result.put("ngara", ngara);
    result.put("numeroAcquisizioni", numeroAcquisizioni);
    result.put("numeroAcquisizioniErrore", numeroAcquisizioniErrore);

    out.print(result);
    out.flush();

    }

    logger.info("Acquisizione offerte da portale Appalti: fine");

    return null;
  }


  /**
   * Il metodo gestisce i raggruppamenti temporanei.<br>
   * Nel caso di gestione = "S" l'eventuale inserimento del raggruppamento temporaneo comporta anche l'esclusione della ditta da cui RT deriva,<br>
   * se invece gestione = "R", si inserisce il raggruppamento temporaneo senza effettuare l'esclusione.<br>
   * Il vettore di ritorno contiene i seguenti valori nei rispettivi indici<br>
   * 0 acquisito<br>
   * 1 esisteDITG<br>
   * 2 codiceDittaEventi<br>
   *
   * @param userkey1
   * @param codiceDittaInv
   * @param w_invcom_idprg
   * @param w_invcom_idcom
   * @param ngara
   * @param codgar1
   * @param gestione
   * @param comnumprot
   * @param comdatprotTimestamp
   * @param comkey3
   * @param iterga
   * @param faseGaraLong
   * @param offtel
   * @param request
   * @return Object[]
   * @throws SQLException
   * @throws IOException
   * @throws XmlException
   * @throws GestoreException
   */
  private final Object[] gestioneRT(String userkey1, String codiceDittaInv, String w_invcom_idprg, Long w_invcom_idcom, String ngara, String codgar1, String gestione, String comnumprot,
      Timestamp comdatprotTimestamp, String comkey3, Long iterga, Long faseGaraLong, Long offtel, final HttpServletRequest request) throws SQLException, IOException, XmlException, GestoreException{

    boolean acquisito = false;
    boolean esisteDITG = true;
    String codiceDittaEventi=null;

    Object[] ret=new Object[3];

    String codimp = userkey1;
    TipoPartecipazioneDocument document = mepaManager.estrazioneDocumentMessaggioFS10_FS11(selectW_DOCDIG, w_invcom_idprg,
        w_invcom_idcom.toString());
    if (document != null) {
      String ragioneSociale = (String) sqlManager.getObject("select nomest from impr where codimp=?", new Object[] { codimp });
      if (ragioneSociale.length() > 61) ragioneSociale = ragioneSociale.substring(0, 60);

      String codiceRaggruppamento = "";

      if (document.getTipoPartecipazione().getRti()){
        // Inserimento in anagrafica della RTI
        String nomeRTI = document.getTipoPartecipazione().getDenominazioneRti();
        Double quotaMandataria = null;
        if (document.getTipoPartecipazione().isSetQuotaMandataria())
          quotaMandataria = new Double(document.getTipoPartecipazione().getQuotaMandataria());
        codiceRaggruppamento = geneManager.calcolaCodificaAutomatica("IMPR", "CODIMP");
        String nomeRTITroncato = nomeRTI;
        if (nomeRTITroncato.length() > 61) nomeRTITroncato = nomeRTITroncato.substring(0, 60);
        Long tipimp = (Long) sqlManager.getObject("select tipimp from impr where codimp=?", new Object[] { codimp });
        Long tipimpNewRTI = null;
        if ((tipimp == null) || (tipimp != null && tipimp.longValue() <= 5))
          tipimpNewRTI = new Long(3);
        else if (tipimp != null && tipimp.longValue() > 5) tipimpNewRTI = new Long(10);
        sqlManager.update("insert into IMPR (CODIMP,NOMIMP,NOMEST,TIPIMP) values(?,?,?,?)", new Object[] {
            codiceRaggruppamento, nomeRTITroncato, nomeRTI, new Long(tipimpNewRTI) });
        sqlManager.update("insert into RAGIMP (IMPMAN,CODDIC,NOMDIC,CODIME9,QUODIC) values(?,?,?,?,?)", new Object[] { "1",
            codimp, ragioneSociale, codiceRaggruppamento, new Double(quotaMandataria) });

        codimp = codiceRaggruppamento;
        ragioneSociale = nomeRTITroncato;


        // Inserimento delle componenti del consorzio/RTI
        ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        TipoPartecipazioneType tipoPartecipazione = document.getTipoPartecipazione();
        // Si controlla se vi sono partecipanti
        if (tipoPartecipazione.isSetPartecipantiRaggruppamento()) {
          Long tipoImpresa = new Long(2);
          if (tipoPartecipazione.isSetDenominazioneRti()) tipoImpresa = new Long(3);
          pgManager.gestionePartecipanti(tipoPartecipazione.getPartecipantiRaggruppamento(), codiceRaggruppamento, tipoImpresa,
              profilo.getId(), ngara, userkey1);
        }

        if(gestione  == "S")
          request.setAttribute("offertaRT", "1");

        // Inserimento in DITG
        Vector<DataColumn> elencoCampi = new Vector<DataColumn>();
        elencoCampi.add(new DataColumn("DITG.NGARA5", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
        elencoCampi.add(new DataColumn("DITG.CODGAR5", new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar1)));
        elencoCampi.add(new DataColumn("DITG.DITTAO", new JdbcParametro(JdbcParametro.TIPO_TESTO, codimp)));
        elencoCampi.add(new DataColumn("DITG.NOMIMO", new JdbcParametro(JdbcParametro.TIPO_TESTO, ragioneSociale)));
        elencoCampi.add(new DataColumn("DITG.NPROGG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
        elencoCampi.add(new DataColumn("DITG.NUMORDPL", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
        elencoCampi.add(new DataColumn("DITG.NPROFF", new JdbcParametro(JdbcParametro.TIPO_TESTO, comnumprot)));
        elencoCampi.add(new DataColumn("DITG.DPROFF", new JdbcParametro(JdbcParametro.TIPO_DATA, comdatprotTimestamp)));
        elencoCampi.add(new DataColumn("DITG.ACQUISIZIONE", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(5))));
        elencoCampi.add(new DataColumn("DITG.NCOMOPE", new JdbcParametro(JdbcParametro.TIPO_TESTO, comkey3)));
        elencoCampi.add(new DataColumn("DITG.DITTAINV", new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceDittaInv)));
        //Nel caso di gara telematica solo upload non va impostato il partgr
        if(!new Long(2).equals(offtel))
          elencoCampi.add(new DataColumn("DITG.PARTGAR", new JdbcParametro(JdbcParametro.TIPO_TESTO, "1")));

        Calendar dataPresentazione = document.getTipoPartecipazione().getDataPresentazione();
        if (dataPresentazione != null) {
          Date datoff = dataPresentazione.getTime();
          String datoffFormat = UtilityDate.convertiData(datoff, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
          if (datoffFormat != null) {
            String oraoff = datoffFormat.substring(11);
            elencoCampi.add(new DataColumn("DITG.DATOFF", new JdbcParametro(JdbcParametro.TIPO_DATA, datoff)));
            elencoCampi.add(new DataColumn("DITG.ORAOFF", new JdbcParametro(JdbcParametro.TIPO_TESTO, oraoff)));
            elencoCampi.add(new DataColumn("DITG.MEZOFF", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Long(5))));
            elencoCampi.add(new DataColumn("DITG.INVOFF", new JdbcParametro(JdbcParametro.TIPO_TESTO, "1")));
          }
        }

        DataColumnContainer containerDITG = new DataColumnContainer(elencoCampi);
        GestoreDITG gestoreDITG = new GestoreDITG();
        gestoreDITG.setRequest(request);
        gestoreDITG.inserisci(status, containerDITG);
        // Inizializzazione documenti della ditta
        pgManager.inserimentoDocumentazioneDitta(codgar1, ngara, codimp);

        mepaManager.gestioneLottiOffertaUnica(ngara, codimp,containerDITG, gestoreDITG, tipoPartecipazione.getCodiceLottoArray(),faseGaraLong,status, "INS",false,"FS11",iterga);

        // Esclusione della ditta mandataria
        if(gestione  == "S"){
          GestoreFasiRicezione gfr = new GestoreFasiRicezione();
          gfr.setRequest(request);
          gfr.escludiDittaOfferta(codimp, ngara, codgar1, codiceDittaInv, faseGaraLong, status);
        }
        acquisito = true;
        esisteDITG = false;
        codiceDittaEventi = codimp;


      }else{
        // Si controlla se l'impresa è un consorzio
        Long tipoImpresa = (Long) this.sqlManager.getObject("select tipimp from impr where codimp=?", new Object[] { userkey1 });
        if (tipoImpresa != null && (tipoImpresa.longValue() == 2 || tipoImpresa.longValue() == 11)) {
          esisteDITG = true;
          TipoPartecipazioneType tipoPartecipazione = document.getTipoPartecipazione();
          if (tipoPartecipazione.isSetPartecipantiRaggruppamento()) {
            tipoImpresa = new Long(2);
            ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);

            pgManager.gestionePartecipanti(tipoPartecipazione.getPartecipantiRaggruppamento(), userkey1, tipoImpresa,
                profilo.getId(), ngara, userkey1);
          }
        }

      }
      // }
    }

    ret[0] = new Boolean(acquisito);
    ret[1] = new Boolean(esisteDITG);
    ret[2] = codiceDittaEventi;

    return ret;
  }

}
