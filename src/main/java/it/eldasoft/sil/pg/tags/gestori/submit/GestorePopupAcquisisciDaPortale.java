/*
 * Created on 25/10/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
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
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.tags.funzioni.ControlloVariazioniAggiornamentoDaPortaleFunction;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoAnagraficaImpresaDocument;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoIscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.sil.portgare.datatypes.CategoriaType;
import it.eldasoft.sil.portgare.datatypes.IscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.sil.portgare.datatypes.ListaCategorieIscrizioneType;
import it.eldasoft.sil.portgare.datatypes.RecapitiType;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.springframework.transaction.TransactionStatus;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire l'acquisizione
 * dal portale Alice
 *
 * @author Marcello Caminiti
 */
public class GestorePopupAcquisisciDaPortale extends GestoreFasiRicezione {

  static Logger               logger                              = Logger.getLogger(GestorePopupAcquisisciDaPortale.class);
  private static final String nomeFileXML_Iscrizione              = "dati_iscele.xml";
  private static final String nomeFileXML_Aggiornamento           = "dati_aggisc.xml";
  //private static final String nomeFileXML_IscrizioneImpresa       = "dati_reg.xml";
  private static final String nomeFileXML_AggiornamentoAnagrafica = "dati_agganag.xml";

  private FileAllegatoManager fileAllegatoManager;
  private GenChiaviManager genChiaviManager;
  private TabellatiManager tabellatiManager;

  public GestorePopupAcquisisciDaPortale() {
    super(false);
  }

  @Override
  public String getEntita() {
    return "DITG";
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);

    this.fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager",
        this.getServletContext(), FileAllegatoManager.class);

    this.genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);

    this.tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        this.getServletContext(), TabellatiManager.class);
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

    if (logger.isDebugEnabled())
      logger.debug("GestorePopupAcquisisciDaPortale: preInsert: inizio metodo");

    String  ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    String registraImpr = UtilityStruts.getParametroString(this.getRequest(),"registraImpr");

    if(registraImpr==null || (registraImpr!=null && "0".equals(registraImpr)))
      this.iscrizione(datiForm, ngara, status, fileAllegatoManager, pgManager);
    else if(registraImpr!=null && "1".equals(registraImpr)){
      try{
        if(this.pgManager.getCodificaAutomaticaPerPortaleAttiva()){
          this.registrazione(datiForm,status,fileAllegatoManager);
        }else {
          //Se non è attiva la codifica automatica per IMPR e TEIM non si può procedere
          this.getRequest().setAttribute("erroreAcquisizione", "1");
          this.getRequest().setAttribute("msg", "<br>" + this.pgManager.messaggioCodAutNonPresente);
        }
      }catch(GestoreException e){
        this.getRequest().setAttribute("erroreAcquisizione", "1");
        throw e;
      }

    }

    if (logger.isDebugEnabled())
      logger.debug("GestorePopupAcquisisciDaPortale: preInsert: fine metodo");
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {


  }



  public void aggiornaStatoW_INVOCM(Long idcom, String stato) throws GestoreException{
    //Aggiornamento dello stato delle occorrenze per le quali in w_invcom è richiesta l' Iscrizione in elenco
    try {
      this.getSqlManager().update(
          "update W_INVCOM set COMSTATO = ?, COMDATASTATO = ? where IDPRG=? and IDCOM=?",
          new Object[] { stato,new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),"PA",idcom.toString()});
    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreAcquisizione", "1");
      throw new GestoreException("Errore nell'aggiornamento dell'entità W_INVCOM", null, e);
    }
  }

  /**
   * Si controlla se la categoria è già presente in CATE, se non è presente viene inserita
   * altrimenti viene aggiornata, oppure viene gestita l'eliminazione
   *
   * @param codiceCategoria
   * @param codiceDitta
   * @param classificaMax
   * @param modo
   * @param tipoCategoria
   *
   * @throws GestoreException
   *
   *
   */
  private void gestioneCategoriaInCate(String codiceCategoria, String codiceDitta, int classificaMax, String modo, Long tipoCategoria) throws GestoreException{
    String select;

    try {
      if("INS-UPDATE".equals(modo)){
        select="select count(CODIMP1) from CATE where CODIMP1 = ? and CATISC = ?";
        Long numCate = (Long)this.getSqlManager().getObject(select, new Object[]{codiceDitta,codiceCategoria});

        Long numcla=null;
        if (classificaMax != 0)
          numcla = new Long(classificaMax);

        //Gestione valorizzazione campo IMPSIC in base al tipo della categoria e ai relativi valori
        //di TAB2(G_z07, G_z08, G_z11)
        Double importo = null;
        if(numcla!=null)
          importo = this.pgManager.getImportoIscrizioneCategoria(tipoCategoria.intValue(), numcla);

        if(numCate==null || numCate.longValue()==0){
          this.getSqlManager().update(
              "insert into CATE(CODIMP1,CATISC,NUMCLA,IMPISC) values(?,?,?,?)",
              new Object[] { codiceDitta,codiceCategoria, numcla,importo});
        }else {
          this.getSqlManager().update(
              "update CATE set NUMCLA = ?, IMPISC =? where CODIMP1 = ? and CATISC = ?",
              new Object[] { numcla,importo,codiceDitta,codiceCategoria});
        }

      }else if("DELETE".equals(modo)){
        this.getSqlManager().update(
            "delete from CATE where CODIMP1 = ? and CATISC = ?",
            new Object[] { codiceDitta,codiceCategoria});
      }
    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreAcquisizione", "1");
      throw new GestoreException("Errore nell'aggiornamento dell'entità CATE", null, e);
    }
  }








  /**
   * Viene eseguito l'iscrizione dell'elenco iscritto
   *
   * @param ngara
   * @param codiceDitta
   * @param status
   * @param xml
   * @param pgManager
   * @param idcom
   * @param user
   * @param fileAllegatoManager
   * @param aggAnagrafica, true viene eseguito l'aggiornamento dell'anagrafica, false non viene eseguito l'aggiornamento
   *
   * @throws GestoreException
   *
   *
   */
  public void iscrizioneElencoOperatori(String ngara, String codiceDitta, TransactionStatus status,
      String xml,PgManager pgManager, Long idcom, String user,FileAllegatoManager fileAllegatoManager, boolean aggAnagrafica) throws GestoreException{

    IscrizioneImpresaElencoOperatoriDocument document;
    try {
      document = IscrizioneImpresaElencoOperatoriDocument.Factory.parse(xml);
      String ragioneSociale = document.getIscrizioneImpresaElencoOperatori().getDatiImpresa().getImpresa().getRagioneSociale();

      //Se è presente la denominazione RTI allora si deve gestire l'inserimento della RT in IMPR e la mandataria
      //ed in gare viene inserita la RT
      boolean gestioneComponenti= false;
      String codiceRaggruppamento = null;
      String nomeATITroncato = null;
      String codiceDittaOriginale=codiceDitta;
      if(document.getIscrizioneImpresaElencoOperatori().isSetDenominazioneRTI()){
        String nomeRTI = document.getIscrizioneImpresaElencoOperatori().getDenominazioneRTI();
        //Valutazione del tipo impresa della RT da creare.
        Long tipimp = (Long)this.sqlManager.getObject("select tipimp from impr where codimp = ? ",new Object[] { codiceDitta });
        if(tipimp!= null && tipimp.longValue()<=5)
          tipimp=new Long(3);
        else if(tipimp!= null && tipimp.longValue()>5)
          tipimp=new Long(10);

        //Inserimento della RTI
        codiceRaggruppamento = this.getGeneManager().calcolaCodificaAutomatica("IMPR","CODIMP");
        nomeATITroncato= nomeRTI;
        if(nomeATITroncato.length()>61)
          nomeATITroncato = nomeATITroncato.substring(0, 60);
        sqlManager.update("insert into IMPR (CODIMP,NOMIMP,NOMEST,TIPIMP) values(?,?,?,?)",
            new Object[]{codiceRaggruppamento,nomeATITroncato,nomeRTI,tipimp});
        String nomdic = ragioneSociale;
        if(nomdic.length()>61)
          nomdic = nomdic.substring(0, 60);
        sqlManager.update("insert into RAGIMP (IMPMAN,CODDIC,NOMDIC,CODIME9) values(?,?,?,?)",
            new Object[]{"1",codiceDitta,nomdic,codiceRaggruppamento});

        gestioneComponenti=true;

      }else{
        Long tipoImpresa= (Long)this.sqlManager.getObject("select tipimp from impr where codimp=?", new Object[]{codiceDitta});
        if(tipoImpresa!=null && (tipoImpresa.longValue()==2 || tipoImpresa.longValue()==11)){
          gestioneComponenti=true;
          codiceRaggruppamento = codiceDitta;
        }
      }

      //Inserimento delle componenti del consorzio/RTI
      if(gestioneComponenti && document.getIscrizioneImpresaElencoOperatori().isSetPartecipantiRaggruppamento()){
        ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        Long tipoImpresa = new Long(2);
        if(document.getIscrizioneImpresaElencoOperatori().isSetDenominazioneRTI())
          tipoImpresa = new Long(3);
        pgManager.gestionePartecipanti(document.getIscrizioneImpresaElencoOperatori().getPartecipantiRaggruppamento(),codiceRaggruppamento,tipoImpresa,profilo.getId(),ngara, codiceDitta);
      }
      if(document.getIscrizioneImpresaElencoOperatori().isSetDenominazioneRTI()){
        //Nel caso in cui si ha la gestione della RTI, nella DITG devo inserire i dati della RTI appena creata
        codiceDitta = codiceRaggruppamento;
        ragioneSociale=nomeATITroncato;
      }

      Calendar dataPresentazione = document.getIscrizioneImpresaElencoOperatori().getDataPresentazione();
      Date campoData=null;
      String ora=null;
      String oraConSecondi=null;
      if(dataPresentazione!=null){
        campoData = dataPresentazione.getTime();
        String data = UtilityDate.convertiData(campoData,UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
        //ottengo una stringa nel formato GG/MM/AAAA HH:MI:SS
        if(data!= null && data.length()>15){
          ora = data.substring(11, 16);
          oraConSecondi = data.substring(11);
        }

      }
      //Inserimento su DITG
      //campi chiave di ditg
      Vector elencoCampi = new Vector();
      elencoCampi.add(new DataColumn("DITG.NGARA5",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
      elencoCampi.add(new DataColumn("DITG.CODGAR5",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, null)));
      elencoCampi.add(new DataColumn("DITG.DITTAO",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceDitta)));

      //campi che si devono inserire perchè adoperati nel gestore
      //GestoreFasiRicezione
      if(ragioneSociale.length()>61)
        ragioneSociale = ragioneSociale.substring(0, 60);
      elencoCampi.add(new DataColumn("DITG.NOMIMO",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, ragioneSociale)));
      elencoCampi.add(new DataColumn("DITG.NPROGG",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
      elencoCampi.add(new DataColumn("DITG.NUMORDPL",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
      elencoCampi.add(new DataColumn("DITG.ACQUISIZIONE",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(2))));
      elencoCampi.add(new DataColumn("DITG.DRICIND",
          new JdbcParametro(JdbcParametro.TIPO_DATA, campoData)));
      elencoCampi.add(new DataColumn("DITG.ORADOM",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, ora)));
      elencoCampi.add(new DataColumn("DITG.MEZDOM",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(5))));

      if(document.getIscrizioneImpresaElencoOperatori().isSetRequisitiCoordinatoreSicurezza()){
        String coordsic="1";
        if(!document.getIscrizioneImpresaElencoOperatori().getRequisitiCoordinatoreSicurezza())
          coordsic="2";
        elencoCampi.add(new DataColumn("DITG.COORDSIC",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, coordsic)));
      }
      DataColumnContainer containerDITG = new DataColumnContainer(elencoCampi);

      super.preInsert(status, containerDITG);


      //L'aggiornamento anagrafico viene eseguito manualmente dall'utente, dalla
      //lista delle comunicazioni sui messaggi FS5.
      //Aggiornamento dati IMPR
      //this.aggiornaDitta(document, codiceDitta,"UPDATE");

      if(aggAnagrafica){
          this.AggiornamentoAnagrafica(codiceDittaOriginale, user, fileAllegatoManager);
      }

      //Inserimento su ISCRIZCAT
      ListaCategorieIscrizioneType listaCategorieIscrizione = document.getIscrizioneImpresaElencoOperatori().getCategorieIscrizione();
      if(listaCategorieIscrizione!=null){
        String tipoImpresa = document.getIscrizioneImpresaElencoOperatori().getDatiImpresa().getImpresa().getTipoImpresa();
        String classificaMinString = null;
        String classificaMaxString = null;
        String ultinf = null;
        for (int j = 0; j < listaCategorieIscrizione.sizeOfCategoriaArray(); j++) {
          CategoriaType datoCodificato = listaCategorieIscrizione.getCategoriaArray(j);
          String categoria = datoCodificato.getCategoria();
          //int classificaMax = 0;
          Long classificaMax = null;
          if(datoCodificato.isSetClassificaMassima()){
            classificaMaxString = datoCodificato.getClassificaMassima();
            if(classificaMaxString!=null && !"".equals(classificaMaxString))
              classificaMax = new Long(classificaMaxString);
          }

          //int classificaMin = 0;
          Long classificaMin = null;
          if(datoCodificato.isSetClassificaMinima()){
            classificaMinString = datoCodificato.getClassificaMinima();
            if(classificaMinString!=null && !"".equals(classificaMinString))
              classificaMin = new Long(classificaMinString);
          }

          if(categoria!=null){
            ultinf = null;
            if(datoCodificato.isSetNota())
              ultinf = datoCodificato.getNota();

            this.insertIscrizcat(ngara, "$" + ngara, codiceDitta, categoria, classificaMax, classificaMin, ultinf, pgManager, status, tipoImpresa,"FS2");

            //Se la categoria è di livello intermedio non si deve inserire in ISCRIZCLASSI
            String isfoglia = pgManager.isfoglia(categoria);

            if("1".equals(isfoglia)){
              //Inserimento in ISCRIZCLASSI
              Long tiplavg = (Long)sqlManager.getObject("select TIPLAVG from CAIS where CAISIM = ?",new Object[]{categoria});
              /*
              Long classificaMinLong = new Long(classificaMin);
              if(classificaMinLong.longValue()==0)
                classificaMinLong = null;
              Long classificaMaxLong = new Long(classificaMax);
              if(classificaMaxLong.longValue()==0)
                classificaMaxLong = null;
              */
              pgManager.updateIscrizclassi("INS", "$" + ngara, ngara, codiceDitta, categoria, tiplavg, classificaMin, classificaMax, false);
            }

          }
        }
      }

      //Aggiornamento Stazione appaltante
      //this.aggiornaStazioneAppaltante(document, "$" + ngara);

      //Inserimento documenti
      try {
				//String numeroProtocollo = pgManager.insertDocumenti(document, "$" + ngara, codiceDitta, ngara, campoData, oraConSecondi, idcom);
                Object datiProtocollo[] = pgManager.insertDocumenti(document, "$" + ngara, codiceDitta, ngara, campoData, oraConSecondi, idcom);
                String numeroProtocollo = (String)datiProtocollo[0];
				Timestamp dataProtocollo = (Timestamp)datiProtocollo[1];
                this.sqlManager.update("update ditg set nprdom=?, dprdom=? where ngara5=? and codgar5=? and dittao=?",
				    new Object[]{numeroProtocollo, dataProtocollo, ngara,"$" + ngara, codiceDitta});
			} catch (GestoreException e) {
				this.getRequest().setAttribute("erroreAcquisizione", "1");
        throw new GestoreException("Errore nell'acquisizione della documentazione", null, e);
			}

      //Aggiornamento dello stato delle occorrenze per le quali in w_invcom è richiesta l' Iscrizione in elenco
      this.aggiornaStatoW_INVOCM(idcom,"6");

    } catch (XmlException e) {
      this.getRequest().setAttribute("erroreAcquisizione", "1");
      throw new GestoreException("Errore nella lettura del file XML", null, e);
    } catch (SQLException e){
      this.getRequest().setAttribute("erroreAcquisizione", "1");
      throw new GestoreException("Errore nella lettura dei dati delle categorie", null, e);
    }


  }

  /**
   * Viene eseguito l'aggiornamento dell'elenco iscritto
   *
   * @param ngara
   * @param codiceDitta
   * @param codiceDittaUser
   * @param status
   * @param xml
   * @param pgManager
   * @param idcom
   * @param user
   * @param fileAllegatoManager
   * @param aggAnagrafica, true viene eseguito l'aggiornamento dell'anagrafica, false non viene eseguito l'aggiornamento
   * @param aggCategorie, true viene eseguito l'aggiornamento delle categorie, false non viene eseguito l'aggiornamento
   *
   * @throws GestoreException
   *
   *
   */
  public void aggiornamentoIscrizioneElencoOperatori(String ngara, String codiceDitta, String codiceDittaUser, TransactionStatus status,
      String xml,PgManager pgManager, Long idcom, String user,FileAllegatoManager fileAllegatoManager,
      boolean aggAnagrafica, boolean aggCategorie) throws GestoreException{

    //IscrizioneImpresaElencoOperatoriDocument document;
    AggiornamentoIscrizioneImpresaElencoOperatoriDocument document;
    try {
      document = AggiornamentoIscrizioneImpresaElencoOperatoriDocument.Factory.parse(xml);
      //String ragioneSociale = document.getAggiornamentoIscrizioneImpresaElencoOperatori().getDatiImpresa().getImpresa().getRagioneSociale();

      //L'aggiornamento anagrafico viene eseguito manualmente dall'utente, dalla
      //lista delle comunicazioni sui messaggi FS5.
      //Aggiornamento dati IMPR
      //this.aggiornaDitta(document, codiceDitta,"UPDATE");

      if(aggAnagrafica){
        if(codiceDittaUser!=null && !"".equals(codiceDittaUser) && !codiceDitta.equals(codiceDittaUser))
          this.AggiornamentoAnagrafica(codiceDittaUser, user, fileAllegatoManager);
        else
          this.AggiornamentoAnagrafica(codiceDitta, user, fileAllegatoManager);
      }

      //Gestione ISCRIZCAT
      if(aggCategorie)
        this.gestioneCategorie(document, ngara, codiceDitta, pgManager, status);

      //Aggiornamento Stazione appaltante
      //this.aggiornaStazioneAppaltante(document, "$" + ngara);

      //Inserimento documenti
      Calendar dataPresentazione = document.getAggiornamentoIscrizioneImpresaElencoOperatori().getDataPresentazione();
      Date campoData=null;
      String ora=null;
      if(dataPresentazione!=null){
        campoData = dataPresentazione.getTime();
        String data = UtilityDate.convertiData(campoData,UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
        //ottengo una stringa nel formato GG/MM/AAAA HH:MI:SS
        if(data!= null && data.length()>15){
          ora = data.substring(11);
        }

      }

      try {
				pgManager.insertDocumenti(document, "$" + ngara, codiceDitta, ngara, campoData, ora, idcom);
			} catch (GestoreException e) {
				this.getRequest().setAttribute("erroreAcquisizione", "1");
        throw new GestoreException("Errore nell'acquisizione della documentazione", null, e);
			}

      //Aggiornamento campo coordinatore sicurezza
      String coordsic=null;
      if(document.getAggiornamentoIscrizioneImpresaElencoOperatori().isSetRequisitiCoordinatoreSicurezza()){
        if(document.getAggiornamentoIscrizioneImpresaElencoOperatori().getRequisitiCoordinatoreSicurezza())
          coordsic="1";
        else
          coordsic="2";
      }
      try {
        this.sqlManager.update("update ditg set coordsic=? where ngara5=? and dittao=?", new Object[]{coordsic,ngara,codiceDitta});
      } catch (SQLException e) {
        this.getRequest().setAttribute("erroreAcquisizione", "1");
        throw new GestoreException("Errore nell'acquisizione deli requisiti del coordinatore sicurezza per la ditta " + codiceDitta + " della gara " + ngara, null, e);
      }


      //Aggiornamento dello stato delle occorrenze per le quali in w_invcom è richiesta l' Iscrizione in elenco
      this.aggiornaStatoW_INVOCM(idcom,"6");

    } catch (XmlException e) {
      this.getRequest().setAttribute("erroreAcquisizione", "1");
      throw new GestoreException("Errore nella lettura del file XML", null, e);
    }
  }

  /**
   * Viene effettuata l'iscrizione\aggiornamento ad elenco
   *
   * @param datiForm
   * @param ngara
   * @param status
   * @param fileAllegatoManager
   * @param pgManager
   *
   * @throws GestoreException
   *
   *
   */
  private void iscrizione(DataColumnContainer datiForm,String ngara,TransactionStatus status,
      FileAllegatoManager fileAllegatoManager, PgManager pgManager) throws GestoreException{

    String select=null;
    boolean errori=false;
    String MsgControlloRichiedente="";

    //Occorrenze per le quali in w_invcom è richiesta l' Iscrizione in elenco

    String idprg="PA";
    String comstato= "5";
    String comtipo="FS2";

    //variabili per tracciatura eventi
    int livEvento = 1;
    String codEvento = "";
    String oggEvento = "";
    String descrEvento = "";
    String errMsgEvento = "";

    Long iddocdigGaracquisiz = null;
    String selectEsistenzaFS4="select count(IDPRG ) from w_invcom where idprg='PA' and COMKEY2 = ? and COMTIPO = 'FS4' and COMKEY1 = ? and COMSTATO = '5' and IDCOM < ?";
    String selectW_docdig="select w.iddocdig from w_docdig w, garacquisiz g where w.digent = ? and w.digkey1 = " + sqlManager.getDBFunction("inttostr",  new String[] {"g.idcom"}) +
        "  and w.idprg = ? and w.dignomdoc = ? and w.idprg=g.idprg and g.ngara=? and g.codimp=? and g.stato=?";
    Vector datiComunicazioneGaracquisiz = null;
    BlobFile fileAllegatoGaracquisiz = null;
    String xmlGarecquisiz=null;
    AggiornamentoIscrizioneImpresaElencoOperatoriDocument document = null;
    AggiornamentoIscrizioneImpresaElencoOperatoriDocument documentGareacquisiz = null;
    ListaCategorieIscrizioneType listaCategorieIscrizione = null;
    ListaCategorieIscrizioneType listaCategorieIscrizioneAcquisiz = null;
    boolean aggiornareCategorie= true;
    boolean saltareAcquisizione= false;
    Long numOccorrenzeFs4=null;
    boolean inserimentoGaracquisiz=false;
    String insertGaraquisiz = "insert into GARACQUISIZ(id,ngara,codimp,idprg,idcom,stato,logmsg) values(?,?,?,?,?,?,?)";
    int idGaraquisiz=0;
    String messaggioCategorie="";
    String codiceDittaUser=null;

    for(int indice = 0; indice <2; indice++){
      select="select IDCOM,COMKEY1 from w_invcom where idprg = ? and comstato = ? and comtipo = ? and comkey2 = ? order by IDCOM";
      if(indice==1)
        comtipo = "FS4";


      List listaIDCOM = null;
      try {
        listaIDCOM = sqlManager.getListVector(select,
            new Object[] { idprg, comstato,comtipo, ngara});
      } catch (SQLException e) {
        this.getRequest().setAttribute("erroreAcquisizione", "1");
        throw new GestoreException("Errore nella lettura della tabella W_INVCOM ", null, e);
      }
      if (listaIDCOM != null && listaIDCOM.size() > 0) {


        String profiloAttivo = (String) this.getRequest().getSession().getAttribute("profiloAttivo");

        String descrEventoParteComune="";
        if(comtipo == "FS2"){
          codEvento = "GA_ACQUISIZIONE_ISCRIZIONE";
          if("PG_GARE_ELEDITTE".equalsIgnoreCase(profiloAttivo))
            descrEventoParteComune = "Acquisizione iscrizione a elenco operatori da portale Appalti";
          else
            descrEventoParteComune = "Acquisizione iscrizione a catalogo da portale Appalti";
        }else{
          codEvento = "GA_ACQUISIZIONE_INTEGRAZIONE";
          if("PG_GARE_ELEDITTE".equalsIgnoreCase(profiloAttivo))
            descrEventoParteComune = "Acquisizione integrazione dati/documenti a iscrizione elenco operatori da portale Appalti";
          else
            descrEventoParteComune = "Acquisizione integrazione dati/documenti a iscrizione catalogo da portale Appalti";
        }
        oggEvento = ngara;
        for (int i = 0; i < listaIDCOM.size(); i++) {
          try{
            livEvento = 1;
            errMsgEvento ="";
            Long idcom = SqlManager.getValueFromVectorParam(listaIDCOM.get(i), 0).longValue();
            String user = SqlManager.getValueFromVectorParam(listaIDCOM.get(i), 1).getStringValue();

            //Determinazione del codice dell'impresa
            select="select USERKEY1 from w_puser where USERNOME = ? ";
            String codiceDitta;
            try {
              codiceDitta = (String)sqlManager.getObject(select, new Object[]{user});
            } catch (SQLException e2) {
              this.getRequest().setAttribute("erroreAcquisizione", "1");
              throw new GestoreException("Errore nella lettura della tabella W_PUSER ", null, e2);
            }

            //Nel caso di FS4 si deve controllare che l'impresa sia presente in gara sia direttamente che come
            //mandataria di una RT
            if("FS4".equals(comtipo)){
              codiceDittaUser = codiceDitta;
              String datiControllo[] = pgManager.controlloEsistenzaDittaElencoGara(codiceDitta, ngara, "$" + ngara,null);
              if("0".equals(datiControllo[0]) || "2".equals(datiControllo[0])){
                errori=true;
                MsgControlloRichiedente="<br>&nbsp;La ditta <b>" + user + "</b> che ha fatto richiesta dell'aggiornamento";
                if("0".equals(datiControllo[0]))
                  MsgControlloRichiedente+=" non è presente fra gli operatori.";
                else if("2".equals(datiControllo[0]))
                  MsgControlloRichiedente+=" è presente come mandataria in più raggruppamenti temporanei.";
                livEvento = 3;
                errMsgEvento=MsgControlloRichiedente;
                continue;
              }else
                codiceDitta = datiControllo[1];

            }

            descrEvento = descrEventoParteComune + "(cod. ditta " + codiceDitta + ")";

            //Vengono letti i documenti associati ad ogni occorrenza di di W_INVCOM
            select="select idprg,iddocdig from w_docdig where DIGENT = ? and digkey1 = ? and idprg = ? and dignomdoc = ? ";
            String digent="W_INVCOM";
            String idprgW_DOCDIG="PA";

            Vector datiW_DOCDIG = null;
            try {
              if(indice==0){
                datiW_DOCDIG = sqlManager.getVector(select,
                    new Object[]{digent, idcom.toString(), idprgW_DOCDIG, nomeFileXML_Iscrizione});
              }else{
                datiW_DOCDIG = sqlManager.getVector(select,
                    new Object[]{digent, idcom.toString(), idprgW_DOCDIG, nomeFileXML_Aggiornamento});
              }
            } catch (SQLException e) {
              this.getRequest().setAttribute("erroreAcquisizione", "1");
              throw new GestoreException("Errore nella lettura della tabella W_DOCDIG ", null, e);
            }
            String idprgW_INVCOM = null;
            Long iddocdig = null;
            if(datiW_DOCDIG != null ){
              if(((JdbcParametro)datiW_DOCDIG.get(0)).getValue() != null)
                idprgW_INVCOM = ((JdbcParametro) datiW_DOCDIG.get(0)).getStringValue();

              if(((JdbcParametro)datiW_DOCDIG.get(1)).getValue() != null)
              iddocdig = ((JdbcParametro) datiW_DOCDIG.get(1)).longValue();


              //Lettura del file xml immagazzinato nella tabella W_DOCDIG
              BlobFile fileAllegato = null;
              try {
                fileAllegato = fileAllegatoManager.getFileAllegato(idprgW_INVCOM,iddocdig);
              } catch (Exception e) {
                this.getRequest().setAttribute("erroreAcquisizione", "1");
                throw new GestoreException("Errore nella lettura del file allegato presente nella tabella W_DOCDIG", null, e);
              }
              String xml=null;
              if(fileAllegato!=null && fileAllegato.getStream()!=null){
                xml = new String(fileAllegato.getStream());
                if (indice==0){
                  //Acquisizione FS2
                  this.iscrizioneElencoOperatori(ngara, codiceDitta, status, xml, pgManager, idcom, user,fileAllegatoManager,true);
                }else{
                  //Acquisizione FS4
                  //Condizioni per la gestione dell'acquisizione posticipate delle variazioni delle categorie
                  boolean condizioniSaltoAggCategorie = false;
                  select = "select abilitaz, numordpl from ditg where ngara5=? and dittao=?";
                  Vector datiDITG=null;;
                  try {
                    datiDITG = sqlManager.getVector(select, new Object[]{ngara,codiceDitta});
                  } catch (SQLException e) {
                    throw new GestoreException("Errore nella lettura dei dati dell'operatore " + codiceDitta + " dell'elenco " + ngara, null, e);
                  }
                  if(datiDITG!=null && datiDITG.size()>0){
                    Long abilitaz = SqlManager.getValueFromVectorParam(datiDITG, 0).longValue();
                    Long numordpl = SqlManager.getValueFromVectorParam(datiDITG, 1).longValue();
                    if(new Long(1).equals(abilitaz) || numordpl!=null)
                      condizioniSaltoAggCategorie = true;
                  }
                  saltareAcquisizione=false;
                  inserimentoGaracquisiz=false;
                  aggiornareCategorie = true;
                  if(condizioniSaltoAggCategorie){
                    try {
                      //Controllo presenza FS4 precedenti non processate, eventualmente si salta l'acquisizione
                      numOccorrenzeFs4 = (Long) sqlManager.getObject(selectEsistenzaFS4, new Object[] { ngara,user,idcom });
                      if(numOccorrenzeFs4!= null && numOccorrenzeFs4.longValue()>0){
                        saltareAcquisizione=true;
                      }else{
                        datiComunicazioneGaracquisiz = sqlManager.getVector(selectW_docdig, new Object[]{"W_INVCOM", "PA",nomeFileXML_Aggiornamento,ngara, codiceDitta, new Long(1)});
                        if(datiComunicazioneGaracquisiz!= null && datiComunicazioneGaracquisiz.size()>0){
                          iddocdigGaracquisiz = ((JdbcParametro) datiComunicazioneGaracquisiz.get(0)).longValue();
                          try {
                            fileAllegatoGaracquisiz = fileAllegatoManager.getFileAllegato(idprgW_INVCOM,iddocdigGaracquisiz);
                            if(fileAllegatoGaracquisiz!=null && fileAllegatoGaracquisiz.getStream()!=null){
                              xmlGarecquisiz = new String(fileAllegatoGaracquisiz.getStream());
                              document = AggiornamentoIscrizioneImpresaElencoOperatoriDocument.Factory.parse(xml);
                              documentGareacquisiz = AggiornamentoIscrizioneImpresaElencoOperatoriDocument.Factory.parse(xmlGarecquisiz);
                              listaCategorieIscrizione = document.getAggiornamentoIscrizioneImpresaElencoOperatori().getCategorieIscrizione();
                              listaCategorieIscrizioneAcquisiz = documentGareacquisiz.getAggiornamentoIscrizioneImpresaElencoOperatori().getCategorieIscrizione();
                              if(!listaCategorieIscrizione.xmlText().equals(listaCategorieIscrizioneAcquisiz.xmlText())){
                                //Sono presenti delle variazioni alle categorie, essendo presente una occorrenza in GARACQUSIZ, non si può procedere con l'acquisizione della FS4
                                saltareAcquisizione=true;
                              }else{
                                //Non ci sono variazioni per le categorie rispetto a quelle in sospeso in GARACQUISIZ, si può acquisire, senza però aggiornare le categorie
                                saltareAcquisizione=false;
                                aggiornareCategorie=false;
                              }
                            }
                          } catch (Exception e) {
                            throw new GestoreException("Errore nella lettura del file allegato presente nella tabella W_DOCDIG dell'operatore " + codiceDitta + " dell'elenco " + ngara, null, e);
                          }
                        }else{
                          //Non è presente alcuna richiesta di aggiornamento delle categorie pendenti in GARACQUISIZ, posso acquisire, ma non aggiornare le categorie
                          saltareAcquisizione=false;
                          aggiornareCategorie=false;
                          //Nel caso in cui nel file xml non ci sono variazioni nelle categorie allora non si deve fare l'inserimento in GARACQUISIZ
                          try {
                            document = AggiornamentoIscrizioneImpresaElencoOperatoriDocument.Factory.parse(xml);
                            ControlloVariazioniAggiornamentoDaPortaleFunction functionControlloVariazioni = new ControlloVariazioniAggiornamentoDaPortaleFunction();
                            messaggioCategorie = functionControlloVariazioni.controlloCategorie(document, ngara, codiceDitta, sqlManager, tabellatiManager, pgManager);
                            if(messaggioCategorie!=null && !"".equals(messaggioCategorie))
                              inserimentoGaracquisiz=true;
                          } catch (JspException e) {
                            throw new GestoreException("Errore nella creazione del log delle variazioni per le categorie dell'operatore " + codiceDitta + " dell'elenco " + ngara, null, e);
                          } catch (XmlException e) {
                            throw new GestoreException("Errore nella creazione del log delle variazioni per le categorie dell'operatore " + codiceDitta + " dell'elenco " + ngara, null, e);
                          }

                        }
                      }
                    } catch (SQLException e) {
                      throw new GestoreException("Errore nella lettura dei dati della W_DOCDIG dell'operatore " + codiceDitta + " dell'elenco " + ngara, null, e);
                    }
                  }
                  if(!saltareAcquisizione){
                    this.aggiornamentoIscrizioneElencoOperatori(ngara, codiceDitta, codiceDittaUser, status, xml, pgManager, idcom, user,fileAllegatoManager,true,aggiornareCategorie);
                    if(inserimentoGaracquisiz){
                      //Si deve popolare la tabella GARACQUISIZ
                      try {
                        document = AggiornamentoIscrizioneImpresaElencoOperatoriDocument.Factory.parse(xml);
                        ControlloVariazioniAggiornamentoDaPortaleFunction functionControlloVariazioni = new ControlloVariazioniAggiornamentoDaPortaleFunction();
                        messaggioCategorie = functionControlloVariazioni.controlloCategorie(document, ngara, codiceDitta, sqlManager, tabellatiManager, pgManager);
                      } catch (JspException e) {
                        throw new GestoreException("Errore nella creazione del log delle variazioni per le categorie dell'operatore " + codiceDitta + " dell'elenco " + ngara, null, e);
                      } catch (XmlException e) {
                        throw new GestoreException("Errore nella creazione del log delle variazioni per le categorie dell'operatore " + codiceDitta + " dell'elenco " + ngara, null, e);
                      }
                      idGaraquisiz = genChiaviManager.getNextId("GARACQUISIZ");
                      descrEvento = descrEvento.replace("(cod. ditta", ", con elaborazione delle categorie posticipata (cod. ditta");
                      try {
                        sqlManager.update(insertGaraquisiz, new Object[]{new Long(idGaraquisiz),ngara,codiceDitta,idprgW_DOCDIG,idcom,new Long(1),messaggioCategorie});
                      } catch (SQLException e) {
                        throw new GestoreException("Errore nella scrittura sulla tabella GARACQUISIZ",null, e);
                      }

                    }
                  }
                }

              }else{
                livEvento = 3;
                errMsgEvento="Errore nella lettura del file XML";
                errori=true;
                this.aggiornaStatoW_INVOCM(idcom,"7");
              }

            }else{
              livEvento = 3;
              errMsgEvento="Errore nella lettura dei documenti associati alla comunicazione";
              errori=true;
              this.aggiornaStatoW_INVOCM(idcom,"7");
            }
          }catch(GestoreException e){
            livEvento = 3;
            errMsgEvento = e.getMessage();
            throw e;
          }finally {
            //Tracciatura eventi
            try {
              if(!saltareAcquisizione){
                LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
                logEvento.setLivEvento(livEvento);
                logEvento.setOggEvento(oggEvento);
                logEvento.setCodEvento(codEvento);
                logEvento.setDescr(descrEvento);
                logEvento.setErrmsg(errMsgEvento);
                LogEventiUtils.insertLogEventi(logEvento);
              }
            } catch (Exception le) {
              String messageKey = "errors.logEventi.inaspettataException";
              logger.error(this.resBundleGenerale.getString(messageKey), le);
            }
          }
        }
      }
    }


    if(errori){
      this.getRequest().setAttribute("erroreAcquisizione", "1");
      if(!"".equals(MsgControlloRichiedente))
        this.getRequest().setAttribute("msg", MsgControlloRichiedente);
    }else{
      this.getRequest().setAttribute("acquisizioneEseguita", "1");
    }

  }


  /**
   * Viene effettuata la registrazione al portale
   *
   * @param datiForm
   * @param status
   * @param fileAllegatoManager
   *
   * @throws GestoreException
   *
   *
   */

  private void registrazione(DataColumnContainer datiForm,TransactionStatus status,
      FileAllegatoManager fileAllegatoManager) throws GestoreException{

    boolean errori=false;

    //Occorrenze per le quali in w_invcom è richiesta la registrazione

    Long idcom = new Long (UtilityStruts.getParametroString(this.getRequest(),"idcom"));

    Map messaggio= new HashMap();
    try{
      errori = pgManager.insertRegistrazionePortale(fileAllegatoManager,idcom,messaggio,"5");


    if(errori){
      if(messaggio.get("tipo")!= null && "DUPL-BACKOFFICE".equals(messaggio.get("tipo"))){
        this.getRequest().setAttribute("msg", messaggio.get("valore"));
      }
      this.getRequest().setAttribute("erroreAcquisizione", "1");
      //this.aggiornaStatoW_INVOCM(idcom, "7");
    }else{
      this.getRequest().setAttribute("acquisizioneEseguita", "1");
    }
    }catch(GestoreException e){
      if(messaggio.get("tipo")!= null && "DUPL-Portale".equals(messaggio.get("tipo"))){
        this.getRequest().setAttribute("msg", messaggio.get("valore"));
      }
      throw e;
    }


  }

  /**
   * Viene effettuato il controllo della presenza del messaggio FS5 associato
   * alla ditta.
   *
   * @param codiceDitta
   * @param codiceUtente
   * @param fileAllegatoManager
   *
   * @throws GestoreException
   *
   *
   */
  public boolean AggiornamentoAnagrafica(String codiceDitta, String codiceUtente,FileAllegatoManager fileAllegatoManager) throws GestoreException{

    String idprg="PA";
    String comstato= "5";
    String comtipo="FS5";
    String select="";
    Long idcom = null;
    boolean errori=false;
    Timestamp comDataStato = null;
    
    //variabili per tracciatura eventi
    int livEvento = 1;
    String codEvento = "GA_ACQUISIZIONE_AGGANAG";
    String oggEvento = "";
    String descrEvento = "Acquisizione aggiornamento anagrafico da portale Appalti";
    String errMsgEvento = "";
    
    try{
    select="select IDCOM,comdatastato from w_invcom where idprg = ? and comstato = ? and comtipo = ? and COMKEY1 = ?";

    try {
      Vector datiW_INVCOM = sqlManager.getVector(select, new Object[] { idprg, comstato,comtipo, codiceUtente});
      if(datiW_INVCOM!= null){
        if(((JdbcParametro)datiW_INVCOM.get(0)).getValue() != null)
          idcom = ((JdbcParametro) datiW_INVCOM.get(0)).longValue();
        if(((JdbcParametro)datiW_INVCOM.get(1)).getValue() != null)
          comDataStato = ((JdbcParametro) datiW_INVCOM.get(1)).dataValue();
      }
      //idcom = (Long)sqlManager.getObject(select, new Object[] { idprg, comstato,comtipo, codiceUtente});
    } catch (SQLException e) {
      livEvento = 3;
      errMsgEvento = e.getMessage();
      throw new GestoreException("Errore nella lettura della tabella W_INVCOM ", null, e);
    }
    if (idcom != null ) {


        //Vengono letti i documenti associati ad ogni occorrenza di di W_INVCOM
        select="select idprg,iddocdig from w_docdig where DIGENT = ? and digkey1 = ? and idprg = ? and dignomdoc = ? ";
        String digent="W_INVCOM";
        String idprgW_DOCDIG="PA";

        Vector datiW_DOCDIG = null;
        try {
          datiW_DOCDIG = sqlManager.getVector(select,
                new Object[]{digent, idcom.toString(), idprgW_DOCDIG, nomeFileXML_AggiornamentoAnagrafica});

        } catch (SQLException e) {
          livEvento = 3;
          errMsgEvento = e.getMessage();
          throw new GestoreException("Errore nella lettura della tabella W_DOCDIG ", null, e);
        }
        String idprgW_INVCOM = null;
        Long iddocdig = null;
        if(datiW_DOCDIG != null ){
          if(((JdbcParametro)datiW_DOCDIG.get(0)).getValue() != null)
            idprgW_INVCOM = ((JdbcParametro) datiW_DOCDIG.get(0)).getStringValue();

          if(((JdbcParametro)datiW_DOCDIG.get(1)).getValue() != null)
          iddocdig = ((JdbcParametro) datiW_DOCDIG.get(1)).longValue();

          HashMap parametri = new HashMap();
          parametri.put("idprg", idprgW_INVCOM);
          parametri.put("iddocdig", iddocdig);

          //Lettura del file xml immagazzinato nella tabella W_DOCDIG
          BlobFile fileAllegato = null;
          try {
            fileAllegato = fileAllegatoManager.getFileAllegato(parametri);
          } catch (Exception e) {
            livEvento = 3;
            errMsgEvento = e.getMessage();
            throw new GestoreException("Errore nella lettura del file allegato presente nella tabella W_DOCDIG", null, e);
          }
          String xml=null;
          if(fileAllegato!=null && fileAllegato.getStream()!=null){
            xml = new String(fileAllegato.getStream());
            AggiornamentoAnagraficaImpresaDocument document;
            try {
              document = AggiornamentoAnagraficaImpresaDocument.Factory.parse(xml);
            } catch (XmlException e) {
              livEvento = 3;
              errMsgEvento = e.getMessage();
              throw new GestoreException("Errore nella lettura del file XML ",null, e);
            }

            String msgImpresa=pgManager.controlloDatiImpresa(document, codiceDitta);


            //Controllo Legale Rappresentante
            String msgLegale = pgManager.controlloDatiReferenti( document, codiceDitta, "LEGALE");

            //Controllo Direttore Tecnico
            String msgDirettore = pgManager.controlloDatiReferenti( document, codiceDitta, "DIRETTORE");

            //Controllo Azionista
            //String msgAzionista = pgManager.controlloDatiReferenti( document, codiceDitta, "AZIONISTA");

            //Controllo Soggetti con altre cariche o qualifiche
            String msgAltreCariche = pgManager.controlloDatiReferenti( document, codiceDitta, "ALTRECARICHE");

            //Controllo Collaboratore
            String msgCollaboratore = pgManager.controlloDatiReferenti( document, codiceDitta, "COLLABORATORE");

            String msg = msgImpresa + msgLegale + msgDirettore + msgAltreCariche + msgCollaboratore;

            boolean impostaStatoAperta = false;
            if(msg!=null && !"".equals(msg)){
            //Si controlla se è variata la pec o ci sono modifiche ai referenti
              //Informazione che serve per impostare lo stato della nota ad 'aperto'
              RecapitiType recapiti = document.getAggiornamentoAnagraficaImpresa().getDatiImpresa().getImpresa().getRecapiti();
              String pecMsg = "";
              String pecDb = "";
              if(recapiti!=null){
                pecMsg = recapiti.getPec();
                try {
                  pecDb = (String)sqlManager.getObject("select emai2ip from impr where codimp = ?", new Object[]{codiceDitta});
                } catch (SQLException e) {
                  livEvento = 3;
                  errMsgEvento = e.getMessage();
                  throw new GestoreException("Errore nella lettura del campo IMPR.EMAI2IP",null, e);
                }
                if(pecDb==null)
                  pecDb="";
                if(pecMsg==null)
                  pecMsg="";
              }
              if(!pecMsg.equals(pecDb) || !"".equals(msgLegale) || !"".equals(msgDirettore) || !"".equals(msgAltreCariche) || !"".equals(msgCollaboratore))
                impostaStatoAperta = true;
            }

            pgManager.aggiornaDitta(document, codiceDitta, "UPDATE");

            if(msg!=null && !"".equals(msg)){
              // Operatore (utente di USRSYS che ha avuto accesso all'applicativo)
              ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
                  CostantiGenerali.PROFILO_UTENTE_SESSIONE);
              java.sql.Date campoData = new java.sql.Date(comDataStato.getTime());
              pgManager.InserisciVariazioni(msg, codiceDitta,"INS",profilo,campoData,impostaStatoAperta);
            }

            this.aggiornaStatoW_INVOCM(idcom,"6");
          }else{

            errori=true;
            this.aggiornaStatoW_INVOCM(idcom,"7");
          }


        }else{

          errori=true;
          this.aggiornaStatoW_INVOCM(idcom,"7");
        }

      }
    }finally{
      try {
        LogEvento logEvento = new LogEvento();
        logEvento.setCodApplicazione("PG");
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(oggEvento);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(descrEvento + " (cod.operatore: " + codiceUtente + ", id.comunicazione: " + idcom + ")");
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
      }
    }
    return errori;


  }



  /**
   * Gestione delle categorie in ISCRIZCAT
   *
   * @param document
   * @param ngara
   * @param codiceDitta
   * @param pgManager
   * @param status
   *
   * @throws GestoreException
   *
   *
   */
  public void gestioneCategorie(AggiornamentoIscrizioneImpresaElencoOperatoriDocument document, String ngara, String codiceDitta,
      PgManager pgManager,TransactionStatus status) throws GestoreException{
    String select="";
    String codgar= "$" + ngara;
    boolean categoriaTrovata = false;

    select="select codcat,tipcat from iscrizcat where ngara=? and codgar=? and codimp=? and codcat<>'0' order by codcat";


    try {
      //Categorie in DB
      List listaDatiCategorie = sqlManager.getListVector(select, new Object[]{ngara, codgar, codiceDitta});

      //Categorie provenienti da Portale
      ListaCategorieIscrizioneType listaCategorieIscrizione = document.getAggiornamentoIscrizioneImpresaElencoOperatori().getCategorieIscrizione();

      ListaCategorieIscrizioneType listaNuovaCategorieIscrizione = document.getAggiornamentoIscrizioneImpresaElencoOperatori().getCategorieIscrizione();

      String tipoImpresa = document.getAggiornamentoIscrizioneImpresaElencoOperatori().getDatiImpresa().getImpresa().getTipoImpresa();

      //Ricerca delle categorie che sono presenti in db e che verranno cancellate o modidificate
      if(listaDatiCategorie != null && listaDatiCategorie.size()>0){
        for (int i = 0; i < listaDatiCategorie.size(); i++) {
          Vector tmp = (Vector) listaDatiCategorie.get(i);
          String tmpCodcat = ((JdbcParametro) tmp.get(0)).getStringValue();
          Long tipcat = ((JdbcParametro) tmp.get(1)).longValue();
          categoriaTrovata = false;

          if(listaCategorieIscrizione != null && listaCategorieIscrizione.sizeOfCategoriaArray()>0){
            String ultinf = null;
            for (int j = 0; j < listaCategorieIscrizione.sizeOfCategoriaArray(); j++) {
              CategoriaType datoCodificato = listaCategorieIscrizione.getCategoriaArray(j);
              String categoria = datoCodificato.getCategoria();
              if(tmpCodcat.equals(categoria)){

                categoriaTrovata = true;
                Long infnumclass = null;
                if (datoCodificato.isSetClassificaMinima())
                  infnumclass = new Long(datoCodificato.getClassificaMinima());

                Long supnumclass = null;
                if(datoCodificato.isSetClassificaMassima())
                  supnumclass =  new Long(datoCodificato.getClassificaMassima());

                ultinf = null;
                if(datoCodificato.isSetNota())
                  ultinf = datoCodificato.getNota();

                select="update iscrizcat set infnumclass = ?, supnumclass = ?, ultinf=? where codcat=? and ngara=? and codgar=? and codimp=? and tipcat=?";
                this.getSqlManager().update(select, new Object[] { infnumclass,supnumclass,ultinf,categoria,ngara, codgar, codiceDitta,tipcat});

                //Se la categoria non è foglia non si deve aggiornare ISCRIZCLASSI
                String isfoglia = pgManager.isfoglia(categoria);
                if("1".equals(isfoglia)){
                  //Aggiornamento di ISCRIZCLASSI
                  Long tiplavg = (Long)this.getSqlManager().getObject("select tiplavg from cais where caisim = ?", new Object[]{tmpCodcat});
                  Long numordpl = (Long)this.getSqlManager().getObject("select numordpl " +
                      "from ditg where codgar5 = ? and ngara5=? and dittao=?", new Object[]{codgar,ngara,codiceDitta});

                  boolean calcoloInviti=false;
                  if(numordpl!=null)
                    calcoloInviti = true;

                  //Aggiornamento ISCRIZCLASSI
                  pgManager.updateIscrizclassi("UPD",codgar, ngara,codiceDitta,categoria,tiplavg,infnumclass,supnumclass,calcoloInviti);
                }
                listaNuovaCategorieIscrizione.removeCategoria(j);

                break;
              }

            }
          }

          if(!categoriaTrovata){
            select="delete from iscrizcat where codcat=? and ngara=? and codgar=? and codimp=?";
            this.getSqlManager().update(select, new Object[] { tmpCodcat,ngara, codgar, codiceDitta});

            //Se la categoria non è foglia non si deve eliminare da ISCRIZCLASSI
            String isfoglia = pgManager.isfoglia(tmpCodcat);
            if("1".equals(isfoglia)){
              //Eliminazione della relativa occorrenza in ISCRIZCLASSI
              Long tiplavg = (Long)this.getSqlManager().getObject("select tiplavg from cais where caisim = ?", new Object[]{tmpCodcat});
              pgManager.updateIscrizclassi("DEL",codgar, ngara,codiceDitta,tmpCodcat,tiplavg,null,null,false);
            }

          }
        }
      }

      //Ricerca delle nuove categorie da inserire
      if(listaNuovaCategorieIscrizione!= null && listaNuovaCategorieIscrizione.sizeOfCategoriaArray()>0){
        String classificaMinString=null;
        String classificaMaxString=null;
        String ultinf=null;
        for (int j = 0; j < listaNuovaCategorieIscrizione.sizeOfCategoriaArray(); j++) {
          CategoriaType datoCodificato = listaNuovaCategorieIscrizione.getCategoriaArray(j);
          String categoria = datoCodificato.getCategoria();

          /*
          int classificaMax = 0;
          int classificaMin = 0;
          */
          Long classificaMax = null;
          Long classificaMin = null;

          if(datoCodificato.isSetClassificaMassima()){
            classificaMaxString = datoCodificato.getClassificaMassima();
            if(classificaMaxString!=null && !"".equals(classificaMaxString))
              classificaMax = new Long(classificaMaxString);
          }

          if(datoCodificato.isSetClassificaMinima()){
           classificaMinString = datoCodificato.getClassificaMinima();
           if(classificaMinString!=null && !"".equals(classificaMinString))
             classificaMin = new Long(classificaMinString);
          }

          ultinf=null;
          if(datoCodificato.isSetNota())
            ultinf = datoCodificato.getNota();

          this.insertIscrizcat(ngara, codgar, codiceDitta, categoria, classificaMax, classificaMin, ultinf, pgManager, status,tipoImpresa,"FS4");


          //Se la categoria non è foglia non si deve inserire ISCRIZCLASSI
          String isfoglia = pgManager.isfoglia(categoria);
          if("1".equals(isfoglia)){
            //Inserimento in ISCRIZCLASSI
            Long numordpl = (Long)this.getSqlManager().getObject("select numordpl " +
                "from ditg where codgar5 = ? and ngara5=? and dittao=?", new Object[]{codgar,ngara,codiceDitta});

            boolean calcoloInviti=false;
            if(numordpl!=null)
              calcoloInviti = true;

            /*
            Long classificaMinLong = new Long(classificaMin);
            if(classificaMinLong.longValue()==0)
              classificaMinLong = null;

            Long classificaMaxLong = new Long(classificaMax);
            if(classificaMaxLong.longValue()==0)
              classificaMaxLong = null;
            */

            Long tiplavg = (Long)this.getSqlManager().getObject("select tiplavg from cais where caisim = ?", new Object[]{categoria});
            pgManager.updateIscrizclassi("INS",codgar, ngara,codiceDitta,categoria,tiplavg,classificaMin,classificaMax,calcoloInviti);
          }
        }

      }

    } catch (SQLException e) {
      throw new GestoreException("Errore nell'aggiornamento delle categorie d'iscrizione", null, e);
    }
  }


  /**
   * Gestione inserimento in ISCRIZCAT
   *
   * @param ngara
   * @param codgar
   * @param codimp
   * @param codcat
   * @param supnumclass
   * @param infnumclass
   * @param ultInfo
   * @param pgManager
   * @param status
   * @param tipoImpresa
   * @param tipoMessaggio
   *
   * @throws GestoreException
   *
   *
   */
  private void insertIscrizcat(String ngara, String codgar, String codimp, String codcat, Long supnumclass, Long infnumclass,
      String ultInfo, PgManager pgManager,TransactionStatus status, String tipoImpresa,String tipoMessaggio) throws GestoreException{

    String select="";

    try {
      //Si deve ricavare il tipo della categoria
      select="select TIPLAVG from CAIS where CAISIM = ?";
      Long tiplavg = (Long)sqlManager.getObject(select,
          new Object[]{codcat});

      Long numPenalita=null;
      if("FS4".equals(tipoMessaggio)){
        Long numordpl = (Long)this.getSqlManager().getObject("select numordpl " +
        		"from ditg where codgar5 = ? and ngara5=? and dittao=?", new Object[]{codgar,ngara,codimp});
        if(numordpl!=null){
          numPenalita=pgManager.getNumeroPenalita(codgar, ngara, codimp, codcat, tiplavg,null,"ISCRIZCAT",null,true);
          //Aggiornamento ISCRIZUFF in base al calcolo di numero inviti virtuali
          pgManager.aggInvitiVirtualiIscrizuff(codgar, ngara, codcat, codimp, tiplavg);
        }
      }

      /*
      Long classificaMax=null;
      Long classificaMin=null;

      if(supnumclass != 0)
        classificaMax = new Long(supnumclass);

      if(infnumclass != 0)
        classificaMin = new Long(infnumclass);
      */

      select ="insert into iscrizcat(ngara,codgar,codimp,codcat,tipcat,supnumclass,infnumclass,invpen,ultinf) "+
        "values(?,?,?,?,?,?,?,?,?)";
      this.getSqlManager().update(select, new Object[] { ngara,codgar, codimp, codcat,tiplavg,supnumclass,infnumclass,numPenalita, ultInfo});

    } catch (SQLException e) {
      throw new GestoreException("Errore nell'aggiornamento delle categorie d'iscrizione", null, e);
    }

  }

}
