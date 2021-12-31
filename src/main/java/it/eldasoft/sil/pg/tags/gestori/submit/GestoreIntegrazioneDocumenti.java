/*
 * Created on 17-07-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoType;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore per la popup di inserimento della documentazione
 *
 * @author Marcello Caminiti
 */
public class GestoreIntegrazioneDocumenti extends AbstractGestoreEntita {

  /** Logger */
  static Logger logger = Logger.getLogger(GestoreIntegrazioneDocumenti.class);

  @Override
  public String getEntita() {
    return "DOCUMGARA";
  }

  public GestoreIntegrazioneDocumenti() {
    super(false);
  }

  public GestoreIntegrazioneDocumenti(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }


  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }


  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("GestoreIntegrazioneDocumenti: preInsert: inizio metodo");

    String codiceGara = UtilityStruts.getParametroString(this.getRequest(), "codgar1");
    String numeroGara = UtilityStruts.getParametroString(this.getRequest(), "ngara");
    String tipoDoc = UtilityStruts.getParametroString(this.getRequest(), "tipoDoc");
    String tipologia = UtilityStruts.getParametroString(this.getRequest(), "tipologia");
    String idconfi = UtilityStruts.getParametroString(this.getRequest(), "idconfi");
    Long tipologiaLong = null;
    if(tipologia != null){
      tipologiaLong = Long.parseLong(tipologia);
    }

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
        "tabellatiManager", this.getServletContext(), TabellatiManager.class);
    
    GestioneWSDMManager gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        this.getServletContext(), GestioneWSDMManager.class);

    boolean inserimentoW_DOCDIG= false;
    ByteArrayOutputStream baos = null;
    //variabili per tracciatura eventi
    String messageKey = null;
    int livEvento = 3;
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
    String codGara = "";
    Long progressivo=null;
    Timestamp  dataRilascio = null;
    boolean fileDocumentale = false;
    
    if (datiForm.isColumn("DIGNOMDOC")
        && datiForm.getString("DIGNOMDOC") != null
        && !datiForm.getString("DIGNOMDOC").trim().equals("")){
      String nomeFileForm = this.getForm().getSelezioneFile().getFileName();
      if(nomeFileForm == null || nomeFileForm.length() == 0){
        fileDocumentale = true;
        inserimentoW_DOCDIG=true;
      }
    }
    
    try{

      //Gestione inserimento su W_DOCDIG
      if (datiForm.isColumn("DIGNOMDOC")
          && datiForm.getString("DIGNOMDOC") != null
          && !datiForm.getString("DIGNOMDOC").trim().equals("") && !fileDocumentale) {

        inserimentoW_DOCDIG=true;

          String dimMassimaTabellatoStringa = tabellatiManager.getDescrTabellato("A1072", "1");
          if(dimMassimaTabellatoStringa==null || "".equals(dimMassimaTabellatoStringa)){
            throw new GestoreException("Non è presente il tabellato A1072 per determinare la dimensione "
                + "massima dell'upload del file", "upload.noTabellato", null);
          }
          int pos = dimMassimaTabellatoStringa.indexOf("(");
          if (pos<1){
            livEvento = 3;
            messageKey = "errors.gestoreException.*.upload.noValore";
            errMsgEvento = this.resBundleGenerale.getString(messageKey);
            throw new GestoreException("Non è possibile determinare dal tabellato A1072 la dimensione "
                + "massima dell'upload del file", "upload.noValore", null);
          }
          dimMassimaTabellatoStringa = dimMassimaTabellatoStringa.substring(0, pos-1);
          dimMassimaTabellatoStringa = dimMassimaTabellatoStringa.trim();
          double dimMassimaTabellatoByte = Math.pow(2, 20) * Double.parseDouble(dimMassimaTabellatoStringa);
          if(this.getForm().getSelezioneFile().getFileSize() == 0 ){
            livEvento = 3;
            messageKey = "errors.gestoreException.*.upload.fileVuoto";
            errMsgEvento = this.resBundleGenerale.getString(messageKey);
            throw new GestoreException("Il file specificato è vuoto. Per continuare specificare un altro file",
                "upload.fileVuoto", null, null);
          }else if(this.getForm().getSelezioneFile().getFileSize()> dimMassimaTabellatoByte){
            livEvento = 3;
            messageKey = "errors.gestoreException.*.upload.overflow";
            errMsgEvento = this.resBundleGenerale.getString(messageKey);
            throw new GestoreException("Il file selezionato ha una dimensione "
                + "superiore al massimo consentito (" + dimMassimaTabellatoStringa + " MB)" , "upload.overflow", new String[] { dimMassimaTabellatoStringa + " MB" },null);
          }

      }


      long newIDDOCDIG=1;

      try {
        progressivo = (Long) this.getSqlManager().getObject(
            "select max(NORDDOCG) from DOCUMGARA where CODGAR=?",new Object[]{codiceGara});

        if (progressivo==null)
          progressivo = new Long (0);

        progressivo = new Long(progressivo.longValue() + 1);

      }catch(SQLException e){
        throw new GestoreException("Errore nel calcolo del progressivo di DOCUMGARA", null, e);
      }


      //Inserimento in DOCUMGARA
      String sql="insert into DOCUMGARA(CODGAR,NGARA,NORDDOCG,GRUPPO,FASGAR,REQCAP,VALENZA,TIPODOC,CONTESTOVAL,STATODOC,OBBLIGATORIO,DESCRIZIONE,BUSTA,FASELE,MODFIRMA,URLDOC,ALLMAIL,DATARILASCIO) " +
              "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

      String ngara = numeroGara;
      String bustalotti = UtilityStruts.getParametroString(this.getRequest(), "bustalotti");
      if("1".equals(bustalotti))
        ngara = datiForm.getString("NGARA");


      Long fasgar = null;
      if (datiForm.isColumn("FASGAR") && datiForm.getLong("FASGAR")!=null)
        fasgar = datiForm.getLong("FASGAR");

      Long reqcap = null;
      if (datiForm.isColumn("REQCAP") && datiForm.getLong("REQCAP")!=null)
        reqcap = datiForm.getLong("REQCAP");

      Long valenza = null;
      if (datiForm.isColumn("VALENZA") && datiForm.getLong("VALENZA")!=null)
        valenza = datiForm.getLong("VALENZA");

      Long tipodoc = null;
      if (datiForm.isColumn("TIPODOC") && datiForm.getLong("TIPODOC")!=null)
        tipodoc = datiForm.getLong("TIPODOC");

      Long contestoval = null;
      if (datiForm.isColumn("CONTESTOVAL") && datiForm.getLong("CONTESTOVAL")!=null)
        contestoval = datiForm.getLong("CONTESTOVAL");

      Long statodoc = new Long(5);

      String obbligatorio = null;
      if (datiForm.isColumn("OBBLIGATORIO") && datiForm.getString("OBBLIGATORIO")!=null)
        obbligatorio = datiForm.getString("OBBLIGATORIO");

      String descrizione = null;
      if (datiForm.isColumn("DESCRIZIONE") && datiForm.getString("DESCRIZIONE")!=null)
        descrizione = datiForm.getString("DESCRIZIONE");

      Long busta= null;
      if (datiForm.isColumn("BUSTA") && datiForm.getLong("BUSTA")!=null)
        busta = datiForm.getLong("BUSTA");

      Long faseIscrizioneElenco = null;
      if (datiForm.isColumn("FASELE") && datiForm.getLong("FASELE")!=null)
        faseIscrizioneElenco = datiForm.getLong("FASELE");

      Long modFirma= null;
      if (datiForm.isColumn("MODFIRMA") && datiForm.getLong("MODFIRMA")!=null)
        modFirma = datiForm.getLong("MODFIRMA");

      String urldoc=null;
      if (datiForm.isColumn("URLDOC") && datiForm.getString("URLDOC")!=null)
        urldoc = datiForm.getString("URLDOC");

      String allmail=null;
      if (datiForm.isColumn("ALLMAIL") && datiForm.getString("ALLMAIL")!=null)
        allmail = datiForm.getString("ALLMAIL");

      if (datiForm.isColumn("DATARILASCIO") && datiForm.getData("DATARILASCIO")!=null)
        dataRilascio = datiForm.getData("DATARILASCIO");

      Object parametri[] = new Object[18];
      parametri[0]=codiceGara;
      parametri[1]=ngara;
      parametri[2]= progressivo;
      parametri[3]=new Long(tipoDoc);
      parametri[4]=fasgar;
      parametri[5]=reqcap;
      parametri[6]=valenza;
      parametri[7]=tipodoc;
      parametri[8]=contestoval;
      parametri[9]=statodoc;
      parametri[10]=obbligatorio;
      parametri[11]=descrizione;
      parametri[12]=busta;
      parametri[13]=faseIscrizioneElenco;
      parametri[14]=modFirma;
      parametri[15]=urldoc;
      parametri[16]=allmail;
      parametri[17]=dataRilascio;

      try {
        this.getSqlManager().update(sql, parametri);
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'inserimento in DOCUMGARA", null, e);
      }

      //Inserimento in W_DOCDIG
      try{
        if(inserimentoW_DOCDIG) {
            String nomeFile="";
            String nomeallegato=null;
            Long idDocumentale = null;
            //Si deve calcolare il valore di IDDOCDIG
            Long maxIDDOCDIG = (Long) this.geneManager.getSql().getObject(
                    "select max(IDDOCDIG) from W_DOCDIG where IDPRG= ?",
                    new Object[] {"PG"} );

            if (maxIDDOCDIG != null && maxIDDOCDIG.longValue()>0)
              newIDDOCDIG = maxIDDOCDIG.longValue() + 1;
            
            if(!fileDocumentale){
              
              baos = new ByteArrayOutputStream();
              baos.write(this.getForm().getSelezioneFile().getFileData());
              
              int len = datiForm.getString("DIGNOMDOC").length();
              int posizioneBarra = datiForm.getString("DIGNOMDOC").lastIndexOf("\\");
              nomeFile=datiForm.getString("DIGNOMDOC").substring(posizioneBarra+1,len).toUpperCase();
              }else{
                nomeallegato = this.getRequest().getParameter("getdocumentoallegato_nomeallegato");
                String username = this.getRequest().getParameter("getdocumentoallegato_username");
                String password = this.getRequest().getParameter("getdocumentoallegato_password");
                String ruolo = this.getRequest().getParameter("getdocumentoallegato_ruolo");
                String annoprotocollostringa = this.getRequest().getParameter("getdocumentoallegato_annoprotocollo");
                Long annoprotocollo = null;
                if(annoprotocollostringa != null && annoprotocollostringa.length()>0){annoprotocollo = new Long(annoprotocollostringa);}
                String numeroprotocollo = this.getRequest().getParameter("getdocumentoallegato_numeroprotocollo");
                String nome = this.getRequest().getParameter("getdocumentoallegato_nome");
                String cognome = this.getRequest().getParameter("getdocumentoallegato_cognome");
                String codiceuo = this.getRequest().getParameter("getdocumentoallegato_codiceuo");
                String idutente = this.getRequest().getParameter("getdocumentoallegato_idutente");
                String idutenteunop = this.getRequest().getParameter("getdocumentoallegato_idutenteunop");
                String tipoallegato = this.getRequest().getParameter("getdocumentoallegato_tipoallegato");
                String numerodocumento = this.getRequest().getParameter("getdocumentoallegato_numerodocumento");
                
                String servizio = this.getRequest().getParameter("getdocumentoallegato_servizio");
                
                WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes;
                
                if(numeroprotocollo != null && numeroprotocollo.length()>0 && annoprotocollo != null){
                    wsdmProtocolloDocumentoRes = gestioneWSDMManager.wsdmProtocolloLeggi(username, password, ruolo,
                    nome, cognome, codiceuo, idutente, idutenteunop, annoprotocollo, numeroprotocollo, servizio,idconfi);
                }else{
                    wsdmProtocolloDocumentoRes = gestioneWSDMManager.wsdmDocumentoLeggi(username, password, ruolo,
                    nome, cognome, codiceuo, idutente, idutenteunop, numerodocumento, servizio,idconfi);
                }
                if (wsdmProtocolloDocumentoRes.isEsito()) {
                  WSDMProtocolloDocumentoType wsdmProtocolloDocumento = wsdmProtocolloDocumentoRes.getProtocolloDocumento();
                  if(numerodocumento == null || numerodocumento.length() == 0){
                    numerodocumento = wsdmProtocolloDocumento.getNumeroDocumento();
                  }
                  try {
                    idDocumentale = (Long) this.getSqlManager().getObject("select id from WSDOCUMENTO where NUMERODOC = ? ", new Object[]{numerodocumento});
                  } catch (SQLException e) {
                      throw new GestoreException(
                        "Errore nella lettura dell'id del documento", null, e);
                  }
                  if (wsdmProtocolloDocumento != null) {
                    if (wsdmProtocolloDocumento.getAllegati() != null) {
                      WSDMProtocolloAllegatoType[] allegati = wsdmProtocolloDocumento.getAllegati();
                      for (int a = 0; a < allegati.length; a++) {
                        if (nomeallegato.equals(allegati[a].getNome()) && tipoallegato.equals(allegati[a].getTipo())) {
                          nomeFile = allegati[a].getNome();
                          byte[] temp =  allegati[a].getContenuto();
                          baos = new ByteArrayOutputStream();
                          baos.write(temp);
                        }
                      }
                    }
                  }
                }
              }
              DataColumnContainer dcc = new DataColumnContainer("W_DOCDIG.IDPRG=T:");
              dcc.addColumn("W_DOCDIG.IDPRG", JdbcParametro.TIPO_TESTO,"PG");
              dcc.getColumn("W_DOCDIG.IDPRG").setChiave(true);
              dcc.addColumn("W_DOCDIG.IDDOCDIG", JdbcParametro.TIPO_DECIMALE,new Long(newIDDOCDIG));
              dcc.getColumn("W_DOCDIG.IDDOCDIG").setChiave(true);
              dcc.addColumn("W_DOCDIG.DIGENT", JdbcParametro.TIPO_TESTO,"DOCUMGARA");
              dcc.addColumn("W_DOCDIG.DIGKEY1", JdbcParametro.TIPO_TESTO,codiceGara);
              if(progressivo!=null)
                dcc.addColumn("W_DOCDIG.DIGKEY2", JdbcParametro.TIPO_TESTO,progressivo.toString());
              dcc.addColumn("W_DOCDIG.DIGNOMDOC", JdbcParametro.TIPO_TESTO,nomeFile);
              dcc.addColumn("W_DOCDIG.DIGOGG", JdbcParametro.TIPO_BINARIO,baos);
              if(datiForm.isColumn("DIGFIRMA")){
                String digfirma = datiForm.getString("DIGFIRMA");
                dcc.addColumn("W_DOCDIG.DIGFIRMA", JdbcParametro.TIPO_TESTO,digfirma);
              }
              dcc.insert("W_DOCDIG", sqlManager);
              if (nomeallegato != null && nomeallegato.length()>0 && idDocumentale != null){
                GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
                    this.getServletContext(), GenChiaviManager.class);
                Long id = new Long(genChiaviManager.getNextId("WSALLEGATI"));
                this.getSqlManager().update("insert into wsallegati (id,entita,key1,key2,idwsdoc) values(?,?,?,?,?)", new Object[]{id,"W_DOCDIG","PG",new Long(newIDDOCDIG),idDocumentale});
              }
        }
      }catch (FileNotFoundException e) {
        throw new GestoreException("File da caricare non trovato", "upload", e);
      } catch (IOException e) {
        livEvento = 3;
        messageKey = "errors.gestoreException.*.upload";
        errMsgEvento = this.resBundleGenerale.getString(messageKey);
        throw new GestoreException(
            "Si è verificato un errore durante la scrittura del buffer per il salvataggio del file allegato su DB",
            "upload", e);
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'inserimento in W_DOCDIG", null, e);
      }

      //Aggiornamento di DOCUMGARA con i riferimenti a W_DOCDIG
      if(inserimentoW_DOCDIG){
        try {
          this.getSqlManager().update("update DOCUMGARA set IDPRG=?, IDDOCDG=? where CODGAR=? and NORDDOCG=?",
              new Object[] { "PG",new Long(newIDDOCDIG),codiceGara,progressivo});
        } catch (SQLException e) {
          throw new GestoreException("Errore nel valorizzare il riferimento a W_DOCDIG in DOCUMGARA", null, e);
        }
      }

      if("3".equals(tipoDoc)){
        //Allineamento occorrenze di IMPRDOCG
        PgManager pgManager = (PgManager) UtilitySpring.getBean(
            "pgManager", this.getServletContext(), PgManager.class);

        pgManager.updateImprdocgDaDocumgara(codiceGara, numeroGara);
      }

      //Aggiornamento del campo TORN.DULTAGG
      Date oggi = UtilityDate.getDataOdiernaAsDate();
      try {
        this.sqlManager.update("update TORN set DULTAGG = ? where CODGAR=?",new Object[]{oggi,codiceGara});
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'aggiornamento del campo TORN.DULTAGG", null, e);
      }

      livEvento = 1 ;
      errMsgEvento = "";
      // Se l'operazione di insert e' andata a buon fine (cioe' nessuna
      // eccezione) inserisco nel request l'attributo RISULTATO valorizzato con
      // "OK", che permettera' alla popup di inserimento documentazione di richiamare
      // il refresh della finestra padre e di chiudere se stessa

      this.getRequest().setAttribute("documentoInserito", "OK");

    }finally{
      //Tracciatura eventi
      try {
        numeroGara = UtilityStringhe.convertiNullInStringaVuota(numeroGara);
        if(!"".equals(numeroGara)){
          Long genere = (Long) sqlManager.getObject("select genere from V_GARE_GENERE where codice = ?", new Object[]{numeroGara});
          if(genere != null){
            if(new Long(100).equals(genere)){
              codGara = codiceGara;
            }else{
              codGara = numeroGara;
            }
          }
        }

        String descrTipoDoc = tabellatiManager.getDescrTabellato("A1064", tipoDoc);
        descrTipoDoc = "Integrazione " + descrTipoDoc + " (id.doc.: " + progressivo;
        if(dataRilascio!=null){
          descrTipoDoc += " - data pubblicazione: " + UtilityDate.convertiData(new Date(dataRilascio.getTime()), UtilityDate.FORMATO_GG_MM_AAAA);
        }
        descrTipoDoc+=")";
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(codGara);
        logEvento.setCodEvento("GA_INTEGRA_DOC");
        logEvento.setDescr(descrTipoDoc);
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
      }
    }

    ////////////////////////////////////////////////////////////////////////////////
    //Ricalcolo NUMORD.DOCUMGARA
    Long gruppo = new Long(tipoDoc);
    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        this.getServletContext(), PgManagerEst1.class);

    pgManagerEst1.ricalcNumordDocGara(codiceGara, gruppo);
    ////////////////////////////////////////////////////////////////////////////////

    if (logger.isDebugEnabled())
      logger.debug("GestoreIntegrazioneDocumenti: preInsert: fine metodo");
  }


  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

  }


  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

}