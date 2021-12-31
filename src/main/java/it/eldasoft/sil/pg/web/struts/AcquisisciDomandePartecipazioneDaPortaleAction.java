/*
 * Created on 28/06/2016
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
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiRicezioneFunction;
import it.eldasoft.sil.pg.tags.gestori.submit.GestoreDITG;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneDocument;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneType;
import it.eldasoft.utils.utility.UtilityDate;

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
import org.springframework.transaction.TransactionStatus;

public class AcquisisciDomandePartecipazioneDaPortaleAction extends Action {

  static Logger               logger = Logger.getLogger(AcquisisciDomandePartecipazioneDaPortaleAction.class);

  private SqlManager          sqlManager;

  private GeneManager         geneManager;

  private PgManager           pgManager;

  private MEPAManager         mepaManager;

  private GestioneWSDMManager gestioneWSDMManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  public void setPgManager(PgManager pgManager) {
    this.pgManager = pgManager;
  }

  public void setMepaManager(MEPAManager mepaManager) {
    this.mepaManager = mepaManager;
  }

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    logger.info("Acquisizione domande di partecipazione da portale Appalti: inizio");

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();
    ResourceBundle resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

    TransactionStatus status = null;
    boolean commit = true;

    int numeroAcquisizioni = 0;
    int numeroAcquisizioniErrore = 0;

    String ngara = request.getParameter("ngara");
    String idconfi = request.getParameter("idconfi");

    String codgar1 = (String) this.sqlManager.getObject("select codgar1 from gare where ngara = ?", new Object[] { ngara });

    String dbFunctionDateToString = sqlManager.getDBFunction("DATETIMETOSTRING",
        new String[] { "comdatprot" });
    String selectW_INVCOM = "select idprg, idcom, comkey1, comnumprot, " + dbFunctionDateToString + ", comkey3 from w_invcom where comkey2 = ? and comstato = '5' and comtipo = 'FS10' order by comdatastato";
    String selectW_PUSER = "select userkey1 from w_puser where usernome = ?";
    String selectW_DOCDIG = "select idprg, iddocdig from w_docdig where digent = ? and idprg = ? and digkey1 = ?";
    String updateW_INVCOM = "update w_invcom set comstato = ? where idprg = ? and idcom = ?";
    String comkey3 = null;

    //variabili per tracciatura eventi
    String genericMsgErr = "Errore inaspettato durante la tracciatura su w_logeventi";
    int livEvento = 1;
    String errMsgEvento = "";
    //Tracciatura eventi
    try {
      LogEvento logEvento = LogEventiUtils.createLogEvento(request);
      logEvento.setLivEvento(livEvento);
      logEvento.setOggEvento(ngara);
      logEvento.setCodEvento("GA_ACQUISIZIONE_DOMANDE_INIT");
      logEvento.setDescr("Inizio acquisizione domande di partecipazione da portale Appalti");
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);
    } catch (Exception le) {
      logger.error(genericMsgErr, le);
    }

    String messageKey="";
    String messaggioPerLog ="";
    boolean erroreGestito=false;
    try{
      //Controllo che da profilo sia abilitata l'acquisizione di acquisizione, che l'utente abbia i permessi di modifica
      //e che la fase della gara sia quella attesa
      if (!this.geneManager.getProfili().checkProtec(
          (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS",
          "ALT.GARE.FASIRICEZIONE.AcquisisciDomandePartecipazioneDaPortale") || !this.pgManager.getAutorizzatoModificaGara("V_GARE_TORN",
              "CODGAR", codgar1, true, "2", request)
          || this.mepaManager.esisteBloccoCondizioniFasiAcquisizioni(ngara, new Long(-5))) {
        erroreGestito=true;
        messageKey ="errors.gestoreException.*.acquisizione.DomandePartecipazioneDaPortale.DatiIncosistenti";
        messaggioPerLog = resBundleGenerale.getString(messageKey);
        throw new GestoreException("Non é possibile procedere con l'acquisizione delle domande di partecipazione da portale Appalti perché dati inconsistenti.","acquisizione.DomandePartecipazioneDaPortale.DatiIncosistenti" , new Exception());
      }

      //Controllo che la data termine presentazione domande non sia passata
      String esitoControllo[] = mepaManager.controlloDataConDataAttuale(ngara,"dtepar","otepar");
      if("false".equals(esitoControllo[0])){
        erroreGestito=true;
        messageKey ="errors.gestoreException.*.acquisizione.DomandePartecipazioneDaPortale.TerminiNonScaduti";
        messaggioPerLog = resBundleGenerale.getString(messageKey);
        messaggioPerLog = messaggioPerLog.replace("{0}", esitoControllo[1]);
        messaggioPerLog = messaggioPerLog.replace("{1}", esitoControllo[2]);
        throw new GestoreException("Non é possibile procedere con l'acquisizione delle domande di partecipazione da portale Appalti perché termini non scaduti.","acquisizione.DomandePartecipazioneDaPortale.TerminiNonScaduti", new Object[]{esitoControllo[1],esitoControllo[2]} , new Exception());
      }

      List<?> datiW_INVCOM = this.sqlManager.getListVector(selectW_INVCOM, new Object[] { ngara });
      if (datiW_INVCOM != null && datiW_INVCOM.size() > 0) {
        Double faseGara = new Double(Math.floor(GestioneFasiRicezioneFunction.FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE / 10));
        Long faseGaraLong = new Long(faseGara.longValue());
        Date dataCorrente=null;
        int esitoAquisizione=1;
        Long iterga = (Long)this.sqlManager.getObject("select iterga from torn where codgar=?", new Object[]{ngara});
        for (int i = 0; i < datiW_INVCOM.size(); i++) {
          try {
            //variabili per tracciatura eventi
            livEvento = 3;
            errMsgEvento = genericMsgErr;
            status = this.sqlManager.startTransaction();
            boolean acquisito = false;

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
                  Vector elencoCampi = new Vector();
                  elencoCampi.add(new DataColumn("DITG.NGARA5", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
                  elencoCampi.add(new DataColumn("DITG.CODGAR5", new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar1)));
                  elencoCampi.add(new DataColumn("DITG.DITTAO", new JdbcParametro(JdbcParametro.TIPO_TESTO, codimp)));
                  elencoCampi.add(new DataColumn("DITG.NOMIMO", new JdbcParametro(JdbcParametro.TIPO_TESTO, ragioneSociale)));
                  elencoCampi.add(new DataColumn("DITG.NPROGG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
                  elencoCampi.add(new DataColumn("DITG.NUMORDPL", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
                  elencoCampi.add(new DataColumn("DITG.NCOMOPE", new JdbcParametro(JdbcParametro.TIPO_TESTO, comkey3)));
                  String integrazioneWSDM="0";

                  try {
                    boolean isIntegrazioneWSDMAttivaValida = gestioneWSDMManager.isIntegrazioneWSDMAttivaValida(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO, idconfi);
                    if(isIntegrazioneWSDMAttivaValida)
                      integrazioneWSDM="1";
                  }catch (SQLException e) {
                    throw new GestoreException("Errore nella verifica dell'integrazione WSDM Protocollo per la gara " + codgar1, null, e);
                  }

                  if("1".equals(integrazioneWSDM)){
                    elencoCampi.add(new DataColumn("DITG.NPRDOM", new JdbcParametro(JdbcParametro.TIPO_TESTO, comnumprot)));
                    elencoCampi.add(new DataColumn("DITG.DPRDOM", new JdbcParametro(JdbcParametro.TIPO_DATA, comdatprotTimestamp)));
                  }

                  Calendar dataPresentazione = document.getTipoPartecipazione().getDataPresentazione();
                  if (dataPresentazione != null) {
                    Date dricind = dataPresentazione.getTime();
                    String datoffFormat = UtilityDate.convertiData(dricind, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
                    if (datoffFormat != null) {
                      String oradom = datoffFormat.substring(11);
                      elencoCampi.add(new DataColumn("DITG.DRICIND", new JdbcParametro(JdbcParametro.TIPO_DATA, dricind)));
                      elencoCampi.add(new DataColumn("DITG.ORADOM", new JdbcParametro(JdbcParametro.TIPO_TESTO, oradom)));
                      elencoCampi.add(new DataColumn("DITG.MEZDOM", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Long(5))));
                    }
                  }
                  DataColumnContainer containerDITG = new DataColumnContainer(elencoCampi);
                  GestoreDITG gestoreDITG = new GestoreDITG();
                  gestoreDITG.setRequest(request);
                  gestoreDITG.inserisci(status, containerDITG);
                  // Inizializzazione documenti della ditta
                  pgManager.inserimentoDocumentazioneDitta(codgar1, ngara, codimp);

                  //Salvataggio della data DATFS10
                  dataCorrente=new Date();
                  Long numOccorrenze=this.geneManager.countOccorrenze("DITGEVENTI", "DITGEVENTI.NGARA=? and DITGEVENTI.DITTAO=? and DITGEVENTI.CODGAR=?", new Object[]{ngara,codimp,codgar1});
                  if(numOccorrenze!=null && numOccorrenze.longValue()>0){
                    this.sqlManager.update("update ditgeventi set DATFS10=? where ngara=? and dittao=? and codgar=?", new Object[]{dataCorrente,ngara,codimp,codgar1});
                  }else{
                    this.sqlManager.update("insert into ditgeventi(NGARA,DITTAO,CODGAR, DATFS10) values(?,?,?,?)", new Object[]{ngara,codimp,codgar1, new Timestamp(dataCorrente.getTime())});
                  }
                  // Se la gara è ad offerta unica si devono inserire le ditte
                  // per ogni lotto
                  mepaManager.gestioneLottiOffertaUnica(ngara, codimp, containerDITG, gestoreDITG,vettorePartecipazioneLotti,faseGaraLong,status,"INS",true,"FS10",iterga);
                  acquisito = true;
                }
              }

              esitoAquisizione=1;
              if (acquisito) {
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
              logEvento.setCodEvento("GA_ACQUISIZIONE_DOMANDE");
              logEvento.setDescr("Acquisizione domanda partecipazione ditta " + codimp + " (cod.operatore " + comkey1 + ", id.comunicazione " + w_invcom_idcom + ")" );
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
        logEvento.setCodEvento("GA_ACQUISIZIONE_DOMANDE_FINE");
        logEvento.setDescr("Fine acquisizione domande di partecipazione da portale Appalti:" +
            numeroAcquisizioni + " acquisizioni, " +
            numeroAcquisizioniErrore + " acquisizioni con errore");
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        logger.error(genericMsgErr, le);
      }

    logger.info("Acquisizione domande di partecipazione da portale Appalti, numero acquisizioni: " + numeroAcquisizioni);
    logger.info("Acquisizione domande di partecipazione da portale Appalti, numero acquisizioni con errore: " + numeroAcquisizioniErrore);

    result.put("ngara", ngara);
    result.put("numeroAcquisizioni", numeroAcquisizioni);
    result.put("numeroAcquisizioniErrore", numeroAcquisizioniErrore);

    out.print(result);
    out.flush();

    }

    logger.info("Acquisizione domande di partecipazione da portale Appalti: fine");

    return null;
  }



}
