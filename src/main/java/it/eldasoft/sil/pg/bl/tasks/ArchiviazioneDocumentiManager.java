/*
 * Created on 10/09/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.tasks;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.ConfigurazionePerUffintUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.bl.SFTPManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.maggioli.eldasoft.ws.dm.*;
import net.schmizz.sshj.xfer.FileSystemFile;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static it.eldasoft.gene.utils.ConfigurazionePerUffintUtils.getConfigurazionePerUfficioIntestatario;
import static it.eldasoft.gene.utils.ConfigurazionePerUffintUtils.getOptConfigurazionePerUfficioIntestatario;

public class ArchiviazioneDocumentiManager {

  static Logger           logger = Logger.getLogger(ArchiviazioneDocumentiManager.class);

  private SqlManager      sqlManager;

  private TabellatiManager      tabellatiManager;

  private GestioneWSDMManager      gestioneWSDMManager;

  private GenChiaviManager      genChiaviManager;

  private FileAllegatoManager fileAllegatoManager;

  private PgManagerEst1 pgManagerEst1;

  public static final String KEY1 = "key1Doc";
  public static final String KEY2 = "key2Doc";
  public static final String IDDOC = "idDoc";
  public static final String IDARCHIVIAZIONE = "idArchiviazione";
  public static final String ENTITA = "entita";
  public static final String PROVENIENZA = "provenienza";
  public static final String UFFINT = "uffint";

  /**
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
  *
  * @param tabellatiManager
  */
 public void setTabellatiManager(TabellatiManager tabellatiManager) {
   this.tabellatiManager = tabellatiManager;
 }


 /**
 *
 * @param GestioneWSDMManager
 */
public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
  this.gestioneWSDMManager = gestioneWSDMManager;
}

/**
 * @param genChiaviManager the genChiaviManager to set
* */
public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
}

public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
  this.fileAllegatoManager = fileAllegatoManager;
}

public void setPgManagerEst1(PgManagerEst1 pgManagerEst1) {
  this.pgManagerEst1 = pgManagerEst1;
}

 /**


  /**
   *Trasferimento al documentale
   *
   */
  public int trasferisciAlDocumentale(String codice, Long genere, Long idDoc, String key1Doc, String key2Doc, String provenienza, HashMap<String,Object> datiWSDM)
    throws GestoreException{

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato ed inoltre deve esistere l'integrazione con il Portale Alice (OP114)
    if (WebUtilities.isAppNotReady()) return 0;

    int esitoTrasferimentoDocumentale = 0;

    String documentiAssociatiDB = ConfigManager.getValore("it.eldasoft.documentiAssociatiDB");
    String pathDocumentiAssociati = ConfigManager.getValore("it.eldasoft.documentiAssociati");

    if (logger.isDebugEnabled())
      logger.debug("trasferisciAlDocumentale: inizio metodo");

      Long idArchiviazione = (Long) datiWSDM.get("idArchiviazione");
      String codgara = (String) datiWSDM.get("codgara");
      String classificadocumento = (String) datiWSDM.get("classificadocumento");
      String codiceregistrodocumento = (String) datiWSDM.get("codiceregistrodocumento");
      String tipodocumento = (String) datiWSDM.get("tipodocumento");
      String mittenteInterno = (String) datiWSDM.get("mittenteInterno");
      String idindice = (String) datiWSDM.get("idindice");
      String idtitolazione = (String) datiWSDM.get("idtitolazione");
      String idunitaoperativadestinataria = (String) datiWSDM.get("idunitaoperativadestinataria");
      if("".equals(idunitaoperativadestinataria))
        idunitaoperativadestinataria =null;
      String oggettodocumento = (String) datiWSDM.get("oggettodocumento");
      String username = (String) datiWSDM.get("username");
      String password = (String) datiWSDM.get("password");
      String ruolo = (String) datiWSDM.get("ruolo");
      String nome = (String) datiWSDM.get("nome");
      String cognome = (String) datiWSDM.get("cognome");
      String codiceuo = (String) datiWSDM.get("codiceuo");
      String idutente = (String) datiWSDM.get("idutente");
      String idutenteunop = (String) datiWSDM.get("idutenteunop");
      String mezzo = (String) datiWSDM.get("mezzo");
      String inserimentoinfascicolo = "NO";
      String codicefascicolo = null;
      String numerofascicolo = null;
      Long annofascicolo = null;
      String codaoo=null;
      String codiceufficio=null;
      String struttura=(String) datiWSDM.get("struttura");
      String selDatiSpecifici = "";
      String supporto=(String) datiWSDM.get("supporto");
      String tipoCollegamento=(String) datiWSDM.get("tipoCollegamento");
      String idconfi = (String) datiWSDM.get("idconfi");
      String classificafascicolo=null;
      String classificaDescrizione = null;
      String associaDocumentiProtocollo = ConfigManager.getValore("wsdm.associaDocumentiProtocollo."+ idconfi);
      String sottotipo = (String) datiWSDM.get("sottotipo");
      String entita=(String) datiWSDM.get("entita");

      String tipofirma=null;
      String idunitaoperativamittente=null;
      String idunitaoperativamittenteDesc = null;

      String RUP = null;
      String nomeRup = null;
      String acronimoRup = "";

      String voce = null;

      String inout = null;
      Long gruppo = null;
      String tipologiaDesc = null;
      String codimp = null;

      String oggetto = "gara";
      if(!"G1STIPULA".equals(entita)) {
        if (genere.equals(new Long(10))) {
            oggetto = "elenco operatori";
        } else if (genere.equals(new Long(20))) {
            oggetto = "catalogo elettronico";
        }
      }

      String entitaAllegato = null;
      String entitaFascicolo = null;

      if("G1STIPULA".equals(entita)) {
        entitaFascicolo = entita;
      }else {
        if (genere.equals(new Long(1))) {
          entitaFascicolo = "TORN";
        }else{
          entitaFascicolo = "GARE";
        }
      }

      String doctel = null;
      try {

          if (!"".equals(key1Doc) && !"".equals(key2Doc)) {
            WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = new WSDMProtocolloDocumentoInType();
            // Dati generali dell'elemento documentale
            wsdmProtocolloDocumentoIn.setClassifica(classificadocumento);
            wsdmProtocolloDocumentoIn.setTipoDocumento(tipodocumento);
            /* PEr le gare
             * 1 = Doc Gara
             * 2 = Doc Comunicazioni pubbliche
             * 3 = Doc Ditta
             * 4 = Doc Associati su FileSystem
             * 5 = Doc Com alla Ditta
             * 6 = Doc Com Dalla ditta
             * 7 = Doc Associati in DB
             */

            /* PEr le stipule
             * 1 = Doc Contratto
             * 4 = Doc Associati su FileSystem
             * 5 = Doc Com alla Ditta
             * 6 = Doc Com Dalla ditta
             * 7 = Doc Associati in DB
             */


            String idDocumentale=null;
            if (!"".equals(provenienza)) {
              int codProvenienza = Integer.parseInt(provenienza);
              switch(codProvenienza) {
                case 1:
                  inout = "INT";
                  if("G1STIPULA".equals(entita)) {
                    idDocumentale = "G1DOCSTIPULA|";
                    String condizioneAppend = this.sqlManager.getDBFunction("concat",  new String[] {"'Documento contratto  - '" , "t.tab1desc" });
                    selDatiSpecifici = "select " + condizioneAppend + ", titolo" +
                        " from V_GARE_DOCSTIPULA w join tab1 t on t.tab1tip=w.visibilita join g1docstipula d on  w.id=d.id" +
                        " where w.idprg = ? and w.iddocdig = ? and t.tab1cod='A1182'";
                  }else {
                    idDocumentale = "DOCUMGARA|";
                    selDatiSpecifici = "select d.gruppo, d.descrizione, " +
                          "d.codgar, d.ngara, d.norddocg" +
                          " from documgara d join w_docdig w on d.iddocdg=w.iddocdig and d.idprg=w.idprg and digent='DOCUMGARA'" +
                          " and w.idprg = ? and w.iddocdig = ? ";
                  }

                  break;
                case 2:
                  inout = "INT";
                  idDocumentale = "W_INVCOM|";
                  selDatiSpecifici = "select cast('Comunicazione pubblica' as varchar(100)) as tipologia ," +
                  this.sqlManager.getDBFunction("concat",  new String[] {this.sqlManager.getDBFunction("concat",  new String[] {"i.commsgogg" , "' - '" }) , "w.DIGDESDOC" }) + ", " +
                        " i.idprg, i.idcom " +
                        " from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + " = w.digkey2" +
                        " and i.idprg = w.digkey1 and w.digent='W_INVCOM' and w.idprg = ? and w.iddocdig = ? ";
                  break;
                case 3:
                  inout = "INT";
                  idDocumentale = "IMPRDOCG|";
                  selDatiSpecifici = "select coalesce(d.bustadesc,'Documentazione presentata dalle ditte') as tipologia, d.descrizione, d.codimp, " +
                        " d.ngara, d.norddoci, d.proveni, d.doctel, d.busta " +
                        " from v_gare_docditta d join w_docdig w on d.iddocdg=w.iddocdig and d.idprg=w.idprg " +
                        " and w.idprg = ? and w.iddocdig = ? ";
                  break;
                case 4:
                  inout = "INT";
                  idDocumentale = "C0OGGASS|";
                  selDatiSpecifici = "select cast('Documento associato' as varchar(100)) as tipologia,c0atit,c0acod from c0oggass where c0aprg = ? and c0acod = ? ";
                  break;
                case 5:
                  inout = "INT";
                  idDocumentale = "W_INVCOM|";
                  selDatiSpecifici = "select cast('Documento inviato alla ditta' as varchar(100)) as tipologia," +
                  this.sqlManager.getDBFunction("concat",  new String[] {this.sqlManager.getDBFunction("concat",  new String[] {"i.commsgogg" , "' - '" }) , "w.DIGDESDOC" }) + ", " +
                  " i.idprg, i.idcom " +
                  " from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + " = w.digkey2" +
                  " and w.digkey1 = i.idprg and w.digent in ('W_INVCOM','W_INVCOMDES') " +
                  " and w.idprg = ? and w.iddocdig = ? ";
                  break;
                case 6:
                  inout = "INT";
                  idDocumentale = "W_INVCOM|";
                  selDatiSpecifici = "select cast('Documento inviato dalla ditta' as varchar(100)) as tipologia," +
                  this.sqlManager.getDBFunction("concat",  new String[] {this.sqlManager.getDBFunction("concat",  new String[] {"i.commsgogg" , "' - '" }) , "w.DIGDESDOC" }) + ", " +
                  " i.idprg, i.idcom, i.commodello " +
                  " from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + " = w.digkey1" +
                  " and i.idprg = w.idprg and w.digent='W_INVCOM' " +
                  " and w.idprg = ? and w.iddocdig = ? ";
                  break;
                case 7:
                  inout = "INT";
                  idDocumentale = "C0OGGASS|";
                  selDatiSpecifici = "select  cast('Documento associato' as varchar(100)) as GRUPPO, c.c0atit as DESCRIZIONE, c.c0acod from c0oggass c" +
                            " join w_docdig w on CAST(c.c0acod AS VARCHAR(10)) = w.digkey1 and c.c0aprg = w.idprg and w.digent='C0OGGASS' and w.idprg = ? and w.iddocdig = ? ";
                  break;
                case 8:
                  inout = "INT";
                  idDocumentale = "W_INVCOM|";
                  selDatiSpecifici = "select  distinct cast('Documentazione presentata dalle ditte' as varchar(100)) as tipologia, W.DIGDESDOC,"+
                      " i.idprg, i.idcom " +
                      " from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + " = w.digkey1" +
                      " and i.idprg = w.idprg and w.digent='W_INVCOM' " +
                      " and w.idprg = ? and w.iddocdig = ? ";
                  break;
                case 9:
                  inout = "INT";
                  idDocumentale = "G1DOCUMATTI|";
                  selDatiSpecifici = "select  distinct cast('Pubblicazione atti e offerte' as varchar(100)) as tipologia, W.DIGDESDOC,"+
                          " da.id " +
                          " from g1documatti da join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"da.id"}) + " = w.digkey1" +
                          " and da.idprg = w.idprg and w.iddocdig = da.iddocdig and w.digent='G1DOCUMATTI' " +
                          " and w.idprg = ? and w.iddocdig = ? ";
                  break;
              }
            }
            if(!"".equals(selDatiSpecifici)){
              Vector<?> datiDoc = this.sqlManager.getVector(selDatiSpecifici, new Object[]{key1Doc,new Long(key2Doc)});
              if(datiDoc != null){
                if("1".equals(provenienza)){
                  if("G1STIPULA".equals(entita)) {
                    tipologiaDesc = (String)SqlManager.getValueFromVectorParam(datiDoc, 0).getValue();
                  }else {
                    gruppo = (Long) SqlManager.getValueFromVectorParam(datiDoc, 0).getValue();
                    if(gruppo != null){
                      switch(gruppo.intValue()) {
                      case 1: tipologiaDesc = "Documenti del bando/avviso"; break;
                      case 2:
                          if("10".equals(genere) || "20".equals(genere)){
                            tipologiaDesc = "Requisiti degli operatori"; break;
                          }else{
                            tipologiaDesc = "Requisiti dei concorrenti"; break;
                          }
                      case 3:
                          if("10".equals(genere) || "20".equals(genere)){
                            tipologiaDesc = "Fac-simile documento richiesto agli operatori"; break;
                          }else{
                            tipologiaDesc = "Fac-simile documento richiesto ai concorrenti"; break;
                          }
                      case 4: tipologiaDesc = "Documento dell'esito"; break;
                      case 5: tipologiaDesc = "Documento per la trasparenza"; break;
                      case 6 : tipologiaDesc = "Documento dell'invito a presentare offerta"; break;
                      case 10: tipologiaDesc = "Atto o documento"; break;
                      case 11: tipologiaDesc = "Documento allegato all'ordine di acquisto"; break;
                      case 12: tipologiaDesc = "Documento dell'invito all'asta elettronica"; break;

                      default:
                        tipologiaDesc = "Documentazione " + oggetto; break;
                      }
                    }else{
                      tipologiaDesc= "Documentazione " + oggetto;
                    }
                  }

                }else{
                  tipologiaDesc = SqlManager.getValueFromVectorParam(datiDoc, 0).getStringValue();
                }

                oggettodocumento = SqlManager.getValueFromVectorParam(datiDoc, 1).getStringValue();
                oggettodocumento = tipologiaDesc + " - " + oggettodocumento;

                if("3".equals(provenienza)){
                  codimp = SqlManager.getValueFromVectorParam(datiDoc, 2).getStringValue();
                  codimp = UtilityStringhe.convertiNullInStringaVuota(codimp);
                  if(codimp!=null){
                    Vector<?> datiImpr  = this.sqlManager.getVector("select cfimp,tipimp from impr where codimp = ? ", new Object[]{codimp});
                    if (datiImpr != null && datiImpr.size()>0) {
                      String cfimp = (String) SqlManager.getValueFromVectorParam(datiImpr, 0).getValue();
                      Long tipimp = (Long) SqlManager.getValueFromVectorParam(datiImpr, 1).getValue();
                      if (new Long(3).equals(tipimp) || new Long(10).equals(tipimp)) {
                        //la ditta è un raggruppamento allora prendo i dati della mandataria
                        codimp = (String)this.sqlManager.getObject("select coddic from ragimp where codime9 = ? and impman='1'", new Object[]{codimp});
                        cfimp = (String)this.sqlManager.getObject("select cfimp from impr where codimp = ? ", new Object[]{codimp});
                      }
                      cfimp = UtilityStringhe.convertiNullInStringaVuota(cfimp);
                      if(!"".equals(cfimp)){
                        oggettodocumento = oggettodocumento + " (C.F. " + cfimp + ")";
                      }
                    }
                  }
                  doctel = SqlManager.getValueFromVectorParam(datiDoc, 6).getStringValue();
                }

                Long commodello = null;
                if("6".equals(provenienza)){
                  commodello= (Long) SqlManager.getValueFromVectorParam(datiDoc, 4).getValue();
                }

                String tipoWSDM =null;
                WSDMConfigurazioneOutType configurazione = this.gestioneWSDMManager.wsdmConfigurazioneLeggi("DOCUMENTALE",idconfi);
                if(configurazione.isEsito())
                  tipoWSDM = configurazione.getRemotewsdm();

                String ngaraStipula=null;
                String cig=null;
                String oggettoGara = null;
                if("G1STIPULA".equals(entita)) {
                  oggettoGara = this.pgManagerEst1.getOggettoGara(codgara, null,null, true);
                  ngaraStipula = (String)sqlManager.getObject("select ngara from g1stipula where id=?", new Object[]{new Long(codgara)});
                  /*
                  if("JIRIDE".equals(tipoWSDM)) {
                    oggettodocumento = codice + "(rif.gara: " + ngaraStipula + ") - " + oggettoGara;
                  }
                  */
                }else {
                  cig = (String)sqlManager.getObject("select codcig from v_gare_torn where codgar=?", new Object[]{codgara});
                  oggettoGara = this.pgManagerEst1.getOggettoGara(codice, codgara, genere);
                }

                if("TITULUS".equals(tipoWSDM)){
                  oggettodocumento += ": " + oggettoGara;
                  int genereGara = genere.intValue();
                  switch(genereGara) {
                      case 10:
                        oggettodocumento += " - Codice elenco:" ;
                          break;
                      case 11:
                        oggettodocumento += " - Codice avviso:";
                          break;
                      case 20:
                        oggettodocumento += " - Codice catalogo:";
                          break;
                      default:
                        oggettodocumento += " - Codice gara:";

                  }
                  oggettodocumento+= " " + codice;
                  if(genereGara!=10 && genereGara!=11 && genereGara!=20)
                    oggettodocumento += " - CIG:" + cig;

                }else if("ENGINEERINGDOC".equals(tipoWSDM)) {
                  oggettodocumento = codice + " - " + oggettodocumento;
                }

                //Per l'integrazione DOCER adopero alcuni campi di GARDOC_JOBS per contentere dei dati che non sono allineati col nome del campo:
                //struttura conterrà idunitaoperativamittenteDesc, ossia la descrizione del tabellato unità operativa mittente
                //supporto conterrà idunitaoperativamittente, ossia il valore del tabellato unità operativa mittente
                //mezzo conterrà tipofirma
                if("DOCER".equals(tipoWSDM)){
                  idunitaoperativamittente = supporto;
                  idunitaoperativamittenteDesc = struttura;
                  tipofirma = mezzo;
                  supporto=null;
                  struttura = null;
                  mezzo = null;
                }

                wsdmProtocolloDocumentoIn.setOggetto(oggettodocumento);
                //In analogia alle preesistenti casistiche non mettiamo la descrizione nell'elemento documentale
                //wsdmProtocolloDocumentoIn.setDescrizione(descrizionedocumento);
                wsdmProtocolloDocumentoIn.setMittenteInterno(mittenteInterno);
                wsdmProtocolloDocumentoIn.setInout(WSDMProtocolloInOutType.fromString(inout));
                wsdmProtocolloDocumentoIn.setCodiceRegistro(codiceregistrodocumento);
                Calendar cal = Calendar.getInstance();
                cal.setTime(UtilityDate.getDataOdiernaAsDate());
                wsdmProtocolloDocumentoIn.setData(cal);
                wsdmProtocolloDocumentoIn.setIdIndice(idindice);
                wsdmProtocolloDocumentoIn.setIdTitolazione(idtitolazione);
                String unitaOperativaMittente=null;
                if("PRISMA".equals(tipoWSDM) || "INFOR".equals(tipoWSDM) || "HALLEY".equals(tipoWSDM)){
                  unitaOperativaMittente = idunitaoperativadestinataria;
                }

                if("ARCHIFLOWFA".equals(tipoWSDM) || "PRISMA".equals(tipoWSDM) || "LAPISOPERA".equals(tipoWSDM))
                  wsdmProtocolloDocumentoIn.setStruttura(struttura);

                wsdmProtocolloDocumentoIn.setIdUnitaOperativaMittente(unitaOperativaMittente);
                wsdmProtocolloDocumentoIn.setIdUnitaOperativaDestinataria(idunitaoperativadestinataria);
                wsdmProtocolloDocumentoIn.setSupporto(supporto);

                if("TITULUS".equals(tipoWSDM)){
                  //Si imposta l'id del documento
                  if("1".equals(provenienza)){
                    //Documentazione di gara
                    String codgar = SqlManager.getValueFromVectorParam(datiDoc, 2).getStringValue();
                    codgar = UtilityStringhe.convertiNullInStringaVuota(codgar);
                    Long nordocg = SqlManager.getValueFromVectorParam(datiDoc, 4).longValue();
                    idDocumentale += codgar + "|";
                    if(nordocg!=null)
                      idDocumentale += nordocg.toString();
                  }else if("3".equals(provenienza)){
                    //Documenti presentati dalle ditte
                    codimp = SqlManager.getValueFromVectorParam(datiDoc, 2).getStringValue();
                    codimp = UtilityStringhe.convertiNullInStringaVuota(codimp);
                    String ngara = SqlManager.getValueFromVectorParam(datiDoc, 3).getStringValue();
                    ngara = UtilityStringhe.convertiNullInStringaVuota(ngara);
                    Long nordoci = SqlManager.getValueFromVectorParam(datiDoc, 4).longValue();
                    Long proveni = SqlManager.getValueFromVectorParam(datiDoc, 5).longValue();
                    idDocumentale += ngara + "|" + codimp + "|";
                    if(nordoci!=null)
                      idDocumentale += nordoci.toString() + "|";
                    else
                      idDocumentale += "|";
                    if(proveni!=null)
                      idDocumentale += proveni.toString();
                  }else if("4".equals(provenienza) || "7".equals(provenienza)){
                    //Documenti associati
                    Long c0acod = SqlManager.getValueFromVectorParam(datiDoc, 2).longValue();
                    if(c0acod!=null)
                      idDocumentale += c0acod.toString();
                  }else if ("9".equals(provenienza)){
                    Long idG1DOCUMATTI = SqlManager.getValueFromVectorParam(datiDoc, 2).longValue();
                    if (idG1DOCUMATTI != null)
                      idDocumentale += idG1DOCUMATTI.toString();
                  }else{
                    //Comunicazione in ingresso, uscita e pubbliche
                    String idprg = SqlManager.getValueFromVectorParam(datiDoc, 2).getStringValue();
                    idprg = UtilityStringhe.convertiNullInStringaVuota(idprg);
                    Long idcom = SqlManager.getValueFromVectorParam(datiDoc, 3).longValue();
                    idDocumentale += idprg + "|";
                    if(idcom!=null)
                      idDocumentale += idcom.toString();
                  }
                  wsdmProtocolloDocumentoIn.setIdDocumento(idDocumentale);

                }


                if("JDOC".equals(tipoWSDM) || "ARCHIFLOW".equals(tipoWSDM)){
                  String select = "select nometei,cogtei from torn, tecni where codgar = ? and codrup=codtec";
                  Vector<?> datiRup =  sqlManager.getVector(select, new Object[]{codgara});
                  if(datiRup!=null && datiRup.size()>0){
                    String nomeR = SqlManager.getValueFromVectorParam(datiRup, 0).getStringValue();
                    String cognomeR = SqlManager.getValueFromVectorParam(datiRup, 1).getStringValue();
                    if(nomeR==null)
                      nomeR="";
                    if(cognomeR==null)
                      cognomeR="";
                   nomeRup = cognomeR + " " + nomeR;
                   if(nomeR.length()>0)
                     acronimoRup +=nomeR.substring(0, 1);
                   if(cognomeR.length()>0)
                     acronimoRup +=cognomeR.substring(0, 1);
                  }
                  wsdmProtocolloDocumentoIn.setGenericS12(nomeRup);
                  if("JDOC".equals(tipoWSDM)){
                    wsdmProtocolloDocumentoIn.setGenericS11(sottotipo);
                    wsdmProtocolloDocumentoIn.setGenericS45(acronimoRup);
                  }

                }

                //CALCOLO qui se la fascicolazione risulta abilitata
                String valAbilitazioneFascicolazione = ConfigManager.getValore("pg.wsdm.applicaFascicolazione."+idconfi);
                if("1".equals(valAbilitazioneFascicolazione)){

                    //ocorre recuperare sia il codice che il numero del fascicolo,in quanto abbiamo il caso di ENGINEERING
                    //che non mi restituisce in creazione il codice ma il numero
                    String selectFascicolo = "select wsf.codice,wsf.numero,wsf.anno, wsf.codaoo, wsf.coduff, wsf.classifica,wsf.desclassi,wsf.desvoce from wsfascicolo wsf ,v_gare_genere v" +
                    " where wsf.key1= v.codice and  wsf.entita = ? and v.codgar = ?";
                    if("G1STIPULA".equals(entitaFascicolo))
                      selectFascicolo = "select codice,numero,anno, codaoo, coduff, classifica,desclassi,desvoce from wsfascicolo where entita = ? and key1= ?  ";
                    Vector fascicolo = sqlManager.getVector(selectFascicolo, new Object[] { entitaFascicolo, codgara });
                    if (fascicolo != null && fascicolo.size() > 0){
                      codicefascicolo = (String) SqlManager.getValueFromVectorParam(fascicolo, 0).getValue();
                      numerofascicolo = (String) SqlManager.getValueFromVectorParam(fascicolo, 1).getValue();
                      annofascicolo = (Long) SqlManager.getValueFromVectorParam(fascicolo, 2).getValue();
                      codaoo = (String) SqlManager.getValueFromVectorParam(fascicolo, 3).getValue();
                      codiceufficio = (String) SqlManager.getValueFromVectorParam(fascicolo, 4).getValue();
                      classificafascicolo = (String) SqlManager.getValueFromVectorParam(fascicolo, 5).getValue();
                      classificaDescrizione = (String) SqlManager.getValueFromVectorParam(fascicolo, 6).getValue();
                      voce = (String) SqlManager.getValueFromVectorParam(fascicolo, 7).getValue();
                    }
                    codicefascicolo = UtilityStringhe.convertiNullInStringaVuota(codicefascicolo);
                    numerofascicolo = UtilityStringhe.convertiNullInStringaVuota(numerofascicolo);
                    if("".equals(codicefascicolo) && "".equals(numerofascicolo)){
                      inserimentoinfascicolo = "SI_FASCICOLO_NUOVO";
                    }else{
                      inserimentoinfascicolo = "SI_FASCICOLO_ESISTENTE";
                    }
                }

                if("ENGINEERINGDOC".equals(tipoWSDM)){
                  wsdmProtocolloDocumentoIn.setGenericS31(codiceufficio);
                }

                // Inserimento in fascicolo
                if ("NO".equals(inserimentoinfascicolo)) {
                  wsdmProtocolloDocumentoIn.setInserimentoInFascicolo(WSDMInserimentoInFascicoloType.NO);
                }
                if ("SI_FASCICOLO_ESISTENTE".equals(inserimentoinfascicolo)) {
                  wsdmProtocolloDocumentoIn.setInserimentoInFascicolo(WSDMInserimentoInFascicoloType.SI_FASCICOLO_ESISTENTE);
                  WSDMFascicoloType wsdmFascicolo = new WSDMFascicoloType();
                  wsdmFascicolo.setCodiceFascicolo(codicefascicolo);
                  if (annofascicolo != null) wsdmFascicolo.setAnnoFascicolo(new Long(annofascicolo));
                  numerofascicolo = UtilityStringhe.convertiNullInStringaVuota(numerofascicolo);
                  if (!"".equals(numerofascicolo)) wsdmFascicolo.setNumeroFascicolo(numerofascicolo);
                  if("TITULUS".equals(tipoWSDM)){
                    //si deve impostare l'oggetto del fascicolo
                    wsdmFascicolo.setOggettoFascicolo(oggettoGara);
                  }
                  if("ARCHIFLOWFA".equals(tipoWSDM) || "INFOR".equals(tipoWSDM) || "DOCER".equals(tipoWSDM) || "ITALPROT".equals(tipoWSDM) || "ENGINEERINGDOC".equals(tipoWSDM)){
                    wsdmFascicolo.setClassificaFascicolo(classificafascicolo);
                  }
                  if("JIRIDE".equals(tipoWSDM) || "PRISMA".equals(tipoWSDM)  || "INFOR".equals(tipoWSDM)){
                    wsdmFascicolo.setStruttura(struttura);
                  }
                  if("JDOC".equals(tipoWSDM)){
                    wsdmFascicolo.setGenericS11(acronimoRup);
                    wsdmFascicolo.setGenericS12(nomeRup);
                  }
                  wsdmProtocolloDocumentoIn.setFascicolo(wsdmFascicolo);
                }

                if("JPROTOCOL".equals(tipoWSDM)){
                  boolean tabellatiInDB = this.gestioneWSDMManager.isTabellatiInDb();
                  String strutt= this.gestioneWSDMManager.getcodiceTabellato(GestioneWSDMManager.SERVIZIO_DOCUMENTALE, "struttura",idconfi,tabellatiInDB);
                  wsdmProtocolloDocumentoIn.setStruttura(strutt);
                  String tipoassegnazione = this.gestioneWSDMManager.getcodiceTabellato(GestioneWSDMManager.SERVIZIO_DOCUMENTALE, "tipoassegnazione",idconfi,tabellatiInDB);
                  wsdmProtocolloDocumentoIn.setTipoAssegnazione(tipoassegnazione);
                }

                if("ARCHIFLOW".equals(tipoWSDM)){
                  boolean tabellatiInDB = this.gestioneWSDMManager.isTabellatiInDb();
                  String strutt= this.gestioneWSDMManager.getcodiceTabellato(GestioneWSDMManager.SERVIZIO_DOCUMENTALE, "struttura",idconfi,tabellatiInDB);
                  wsdmProtocolloDocumentoIn.setStruttura(strutt);
                  String divisione = this.gestioneWSDMManager.getcodiceTabellato(GestioneWSDMManager.SERVIZIO_DOCUMENTALE, "divisione",idconfi,tabellatiInDB);
                  wsdmProtocolloDocumentoIn.setGenericS42(divisione);
                }

                //Valorizzazione dei nuovi campi: societa,codicegaralotto,cig e destinatarioprincipale
                String cenint=null;
                if("G1STIPULA".equals(entita)) {
                  cenint=(String)sqlManager.getObject("select cenint from torn,gare where ngara=? and codgar=codgar1",new Object[]{ngaraStipula});
                }else {
                  cenint=(String)sqlManager.getObject("select cenint from torn where codgar=?",new Object[]{codgara});
                }
                wsdmProtocolloDocumentoIn.setSocieta(cenint);
                wsdmProtocolloDocumentoIn.setDestinatarioPrincipale(cenint);
                if(!"G1STIPULA".equals(entita)) {
                  if (genere.equals(new Long(1)))
                    wsdmProtocolloDocumentoIn.setCodiceGaraLotto(codgara);
                  else
                    wsdmProtocolloDocumentoIn.setCodiceGaraLotto(codice);
                }

                wsdmProtocolloDocumentoIn.setCig(cig);
                wsdmProtocolloDocumentoIn.setMezzo(mezzo);

                wsdmProtocolloDocumentoIn.setClassificaDescrizione(classificaDescrizione);
                wsdmProtocolloDocumentoIn.setVoce(voce);

                if("DOCER".equals(tipoWSDM)){
                  wsdmProtocolloDocumentoIn.setGenericS11(tipofirma);
                  wsdmProtocolloDocumentoIn.setGenericS21(idunitaoperativamittenteDesc);
                  wsdmProtocolloDocumentoIn.setGenericS22(idunitaoperativamittente);
                  wsdmProtocolloDocumentoIn.setGenericS31(idunitaoperativamittenteDesc);
                  wsdmProtocolloDocumentoIn.setGenericS32(idunitaoperativamittente);
                }
                //ALLEGATI
                //Ogni documento rappresenta un solo allegato
                String selectAllegato = null;
                List<?> datiAllegato = null;
                String tipo = "";
                WSDMProtocolloAllegatoType[] allegati = new WSDMProtocolloAllegatoType[1];
                if("4".equals(provenienza) && !documentiAssociatiDB.equals("1")){
                    selectAllegato = "select c0atit, c0anomogg from c0oggass where c0aprg = ? and c0acod = ?";
                    datiAllegato = sqlManager.getVector(selectAllegato, new Object[] {key1Doc, key2Doc});
                    WSDMProtocolloAllegatoType allegato = new WSDMProtocolloAllegatoType();
                    if(datiAllegato!= null){
                      String descFileAssociato = (String) SqlManager.getValueFromVectorParam(datiAllegato, 0).getValue();
                      descFileAssociato =UtilityStringhe.convertiNullInStringaVuota(descFileAssociato);
                      String nomeFileAssociato = (String) SqlManager.getValueFromVectorParam(datiAllegato, 1).getValue();
                      nomeFileAssociato =UtilityStringhe.convertiNullInStringaVuota(nomeFileAssociato);
                      int index = nomeFileAssociato.lastIndexOf('.');
                      if (index > 0) {
                        tipo = nomeFileAssociato.substring(index + 1);
                      }
                      File fileAssociato = new File(pathDocumentiAssociati + "/" + nomeFileAssociato);
                      byte[] contenuto = FileUtils.readFileToByteArray(fileAssociato);
                      allegato.setNome(nomeFileAssociato);
                      allegato.setTitolo(oggettodocumento);
                      allegato.setTipo(tipo);
                      allegato.setContenuto(contenuto);
                      if("TITULUS".equals(tipoWSDM))
                        allegato.setIdAllegato("C0OGGASS|" + key2Doc);
                      if("NUMIX".equals(tipoWSDM)) {
                        allegato = GestioneWSDMManager.popolaAllegatoInfo(nomeFileAssociato,allegato);
                        allegato.setIsSealed(new Long(1));
                      }
                     allegati[0] = allegato;
                    }
                }else{
                  selectAllegato = "select digdesdoc, dignomdoc, idprg, iddocdig from w_docdig where idprg = ? and iddocdig = ?";
                  datiAllegato = sqlManager.getVector(selectAllegato, new Object[] { key1Doc, key2Doc });
                  WSDMProtocolloAllegatoType allegato = new WSDMProtocolloAllegatoType();
                  if(datiAllegato!= null){
                    String digdesdoc = (String) SqlManager.getValueFromVectorParam(datiAllegato, 0).getValue();
                    digdesdoc =UtilityStringhe.convertiNullInStringaVuota(digdesdoc);
                    String dignomdoc = (String) SqlManager.getValueFromVectorParam(datiAllegato, 1).getValue();
                    dignomdoc =UtilityStringhe.convertiNullInStringaVuota(dignomdoc);
                    int index = dignomdoc.lastIndexOf('.');
                    if (index > 0) {
                      tipo = dignomdoc.substring(index + 1);
                    }
                    String w_docdig_idprg = (String) SqlManager.getValueFromVectorParam(datiAllegato, 2).getValue();
                    Long w_docdig_iddocdig = (Long) SqlManager.getValueFromVectorParam(datiAllegato, 3).getValue();
                    allegato.setNome(dignomdoc);
                    allegato.setTitolo(oggettodocumento);
                    allegato.setTipo(tipo);
                    BlobFile digogg;
                      digogg = fileAllegatoManager.getFileAllegato(w_docdig_idprg, w_docdig_iddocdig);
                    allegato.setContenuto(digogg.getStream());
                    if("TITULUS".equals(tipoWSDM))
                      allegato.setIdAllegato("W_DOCDIG|" + key1Doc + "|" + key2Doc);
                    if("NUMIX".equals(tipoWSDM)) {
                      allegato = GestioneWSDMManager.popolaAllegatoInfo(dignomdoc,allegato);
                      allegato.setIsSealed(new Long(1));
                    }
                    allegati[0] = allegato;
                  }
                }

                wsdmProtocolloDocumentoIn.setAllegati(allegati);
                  String passwordDecoded = null;
                  if (password != null && password.trim().length() > 0) {
                    ICriptazioneByte passwordICriptazioneByte = null;
                    try {
                      passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
                          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), password.getBytes(),
                          ICriptazioneByte.FORMATO_DATO_CIFRATO);
                    } catch (CriptazioneException e) {
                      ;//messaggio di eccezione
                    }
                    passwordDecoded = new String(passwordICriptazioneByte.getDatoNonCifrato());
                  }else{
                    passwordDecoded = "";
                  }

                  Long stato_archiviazione = new Long(3);
                  String esito = null;

                  //Nel caso di NUMIX va impostato un destinatario, si è deciso di prendere i dati dalla stazione Appaltante
                  if("NUMIX".equals(tipoWSDM)){
                    WSDMProtocolloAnagraficaType[] destinatari = new WSDMProtocolloAnagraficaType[1];
                    destinatari[0] = new WSDMProtocolloAnagraficaType();
                    String intestazione=null;
                    String codfisc=null;
                    String piva=null;
                    String email=null;
                    Vector<?> datiUffint = this.sqlManager.getVector("select nomein, cfein, ivaein, emai2in, emaiin from uffint where codein=?", new Object[] { cenint });
                    if(datiUffint!=null && datiUffint.size()>0){
                      intestazione = (String) SqlManager.getValueFromVectorParam(datiUffint,0).getValue();
                      codfisc = (String) SqlManager.getValueFromVectorParam(datiUffint,1).getValue();
                      piva = (String) SqlManager.getValueFromVectorParam(datiUffint,2).getValue();
                      email = (String) SqlManager.getValueFromVectorParam(datiUffint,3).getValue();
                      if(email == null || "".equals(email))
                        email = (String) SqlManager.getValueFromVectorParam(datiUffint,4).getValue();
                    }
                    destinatari[0].setCognomeointestazione(intestazione);
                    destinatari[0].setCodiceFiscale(codfisc);
                    destinatari[0].setPartitaIVA(piva);
                    destinatari[0].setEmail(email);

                    wsdmProtocolloDocumentoIn.setDestinatari(destinatari);

                    if("3".equals(provenienza) && "1".equals(doctel) && !genere.equals(new Long(10)) && !genere.equals(new Long(20))){
                      Long busta = SqlManager.getValueFromVectorParam(datiDoc, 7).longValue();
                      codimp = SqlManager.getValueFromVectorParam(datiDoc, 2).getStringValue();
                      codimp = UtilityStringhe.convertiNullInStringaVuota(codimp);
                      Vector<?> datiDitg = this.getDatiProtocolloDitta(codice,codgara , codimp, busta);
                      if(datiDitg!=null && datiDitg.size()>0){
                        String numProt = SqlManager.getValueFromVectorParam(datiDitg, 0).stringValue();
                        String dataProt = SqlManager.getValueFromVectorParam(datiDitg, 2).stringValue();
                        if(numProt!=null && dataProt!=null){
                          Date date = UtilityDate.convertiData(dataProt, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
                          Calendar calendar = Calendar.getInstance();
                          calendar.setTime(date);
                          wsdmProtocolloDocumentoIn.setGenericS11(numProt);
                          wsdmProtocolloDocumentoIn.setGenericD11(calendar);
                        }
                      }

                    }
                  }

                  boolean fromFS12Rettifica="6".equals(provenienza) && (new Long(13).equals(commodello) || new Long(18).equals(commodello));
                  if("JIRIDE".equals(tipoWSDM) && (("3".equals(provenienza) && "1".equals(doctel)) || fromFS12Rettifica ) && "2".equals(associaDocumentiProtocollo) && !genere.equals(new Long(10)) && !genere.equals(new Long(20))){
                    Long busta = null;
                    String idprgFS12 = null;
                    Long idcomFS12 = null;
                    if("3".equals(provenienza)) {
                      SqlManager.getValueFromVectorParam(datiDoc, 7).longValue();
                      codimp = SqlManager.getValueFromVectorParam(datiDoc, 2).getStringValue();
                      codimp = UtilityStringhe.convertiNullInStringaVuota(codimp);
                    } else if (fromFS12Rettifica){
                       idprgFS12 = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(datiDoc, 2).getStringValue());
                       idcomFS12 = SqlManager.getValueFromVectorParam(datiDoc, 3).longValue();
                    }
                    WSDMAggiungiAllegatiResType aggiungiAllegatiResType = this.aggiungiAllegati(codice,codgara , codimp, busta,allegati, key1Doc, key2Doc, username, ruolo, idArchiviazione,idconfi,idprgFS12,idcomFS12);
                    if(aggiungiAllegatiResType!=null){
                      if(aggiungiAllegatiResType.isEsito()){
                        stato_archiviazione = new Long(22);
                        esitoTrasferimentoDocumentale = 0;
                      }else{
                        stato_archiviazione = new Long(23);
                        esito = aggiungiAllegatiResType.getMessaggio();
                        esitoTrasferimentoDocumentale = -1;
                      }
                    }
                  }else{
                    WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = this.gestioneWSDMManager.WSDMDocumentoInserisci(username, passwordDecoded,
                        ruolo, nome, cognome, codiceuo, idutente, idutenteunop, wsdmProtocolloDocumentoIn, "DOCUMENTALE",codaoo, codiceufficio,idconfi);

                    if(wsdmProtocolloDocumentoRes.isEsito()){
                      String numeroDocumento = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroDocumento();
                      //Per JIRIDE se attivata la property associaDocumentiProtocollo si deve procedere all'associazione dei documenti presentati dalle ditte
                      //al protocollo
                      if("JIRIDE".equals(tipoWSDM) && (("3".equals(provenienza)  && "1".equals(doctel)) || fromFS12Rettifica) && "1".equals(associaDocumentiProtocollo) && !genere.equals(new Long(10)) && !genere.equals(new Long(20))){
                        Long busta = null;
                        String idprgFS12=null;
                        Long idcomFS12=null;
                        if("3".equals(provenienza)) {
                          busta = SqlManager.getValueFromVectorParam(datiDoc, 7).longValue();
                          codimp = SqlManager.getValueFromVectorParam(datiDoc, 2).getStringValue();
                        } else if(fromFS12Rettifica) {
                          idprgFS12 = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(datiDoc, 2).getStringValue());
                          idcomFS12 = SqlManager.getValueFromVectorParam(datiDoc, 3).longValue();
                        }
                        codimp = UtilityStringhe.convertiNullInStringaVuota(codimp);
                        WSDMDocumentoCollegaResType wdmDocumentoCollegaResType = this.collegaDocumenti(codice,codgara , codimp, tipoCollegamento, busta, numeroDocumento, null, null, username, ruolo,idconfi,idprgFS12,idcomFS12);
                        if(wdmDocumentoCollegaResType!=null){
                          if(wdmDocumentoCollegaResType.isEsito()){
                            stato_archiviazione = new Long(20);
                          }else{
                            stato_archiviazione = new Long(21);
                            esito = wdmDocumentoCollegaResType.getMessaggio();
                          }
                        }
                      }

                      //Salvatagio in WSDOCUMENTO
                      Long annoProt=null;
                      String numProt=null;
                      if("INFOR".equals(tipoWSDM) || "JPROTOCOL".equals(tipoWSDM) || "ITALPROT".equals(tipoWSDM) || "NUMIX".equals(tipoWSDM)){
                        numProt = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroProtocollo();
                        annoProt = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getAnnoProtocollo();
                      }
                      String chiave=codice;
                      if("G1STIPULA".equals(entita))
                        chiave=codgara;
                      Long idWSDocumento = this.gestioneWSDMManager.setWSDocumento(entitaFascicolo, chiave, null, null, null, numeroDocumento, annoProt, numProt, oggettodocumento,inout);

                      if("4".equals(provenienza)){
                        entitaAllegato = "C0OGGASS";
                      }else{
                        entitaAllegato = "W_DOCDIG";
                      }

                      this.gestioneWSDMManager.setWSAllegati(entitaAllegato, key1Doc, key2Doc, null, null, idWSDocumento);
                      esitoTrasferimentoDocumentale = 0;

                    }else{
                      //caso di errore
                      esito = wsdmProtocolloDocumentoRes.getMessaggio();
                      esitoTrasferimentoDocumentale = -1;
                      stato_archiviazione = new Long(2);
                    }
                  }

                  //Aggiorno lo stato della elaborazione positivo
                  this.sqlManager.update("update gardoc_wsdm set stato_archiviazione = ?, esito = ? where id= ? and id_archiviazione = ?",
                      new Object[]{ stato_archiviazione, esito, idDoc, idArchiviazione });
              }
            }

          }//if identificativi doc

      } catch (SQLException e) {
        throw new GestoreException("Errore nella lettura della lista dei documenti da trasferire al documentale", null, e);
      } catch (IOException e) {
        throw new GestoreException("Errore nella lettura della lista dei documenti da trasferire al documentale", null, e);
      }

    if (logger.isDebugEnabled())
      logger.debug("trasferisciAlDocumentale: fine metodo");

    return esitoTrasferimentoDocumentale;

  }


  /**
   *Trasferimento al documentale
   * @throws IOException
   * @throws SQLException
   *
   */
  public int trasferisciAreaFTPCOS(HashMap<String, Object> doc, SFTPManager sftp)
    throws GestoreException, IOException, SQLException{

    String idprg = (String)doc.get(this.KEY1);
    String iddocdig =(String) doc.get(this.KEY2);
    Long idDoc = (Long)doc.get(this.IDDOC);
    Long idArchiviazione = (Long)doc.get(this.IDARCHIVIAZIONE);
    String entita = (String)doc.get(this.ENTITA);
    String provenienza = (String)doc.get(this.PROVENIENZA);
    String uffint = (String) doc.get(this.UFFINT);

    try{
      if (logger.isDebugEnabled())
        logger.debug("trasferisciAreaFTPCOS: inizio metodo");

      String alias_sp = ConfigManager.getValore("cos.sftp.aliasSp");
      String alias_da = ConfigManager.getValore("cos.sftp.aliasDa");
      String pathBase = ConfigManager.getValore("cos.sftp.pathBase");
      if((alias_sp == null || "".equals(alias_sp)) || (alias_da == null || "".equals(alias_da)) || (pathBase == null || "".equals(pathBase))){
        throw new GestoreException("Errore nella configurazione dell'integrazione COS: aliasSp o aliasDa non valorizzati", null);
      }
      String base = pathBase + alias_sp + "/" + alias_da + "/Documenti/";
      if (logger.isDebugEnabled())
        logger.debug("trasferisciAreaFTPCOS: path = " + base);

      sftp.existsDirs(base);
      int esito = 1;

        try {
          String select = "";
          if ("4".equals(provenienza)) {
            select = "select c0anomogg from c0oggass where c0aprg = ? and c0acod = ?";
          }else{
            select = "select dignomdoc from w_docdig where idprg = ? and iddocdig = ?";
          }
          String nomeFile = (String) this.sqlManager.getObject(select, new Object[] { idprg, iddocdig });

          BlobFile bf = null;
          byte[] bytes;

          if ("4".equals(provenienza)) {
            // associato su filesystem
            String pathDocumentiAssociati = ConfigManager.getValore("it.eldasoft.documentiAssociati");
            File fileAssociato = new File(pathDocumentiAssociati + "/" + nomeFile);
            bytes = FileUtils.readFileToByteArray(fileAssociato);
          } else {
            bf = fileAllegatoManager.getFileAllegato(idprg, new Long(iddocdig));
            bytes = bf.getStream();
          }
          String impronta = getImpronta(bytes);
          String estensioni = "";
          ArrayList<String> formatiCOS = new ArrayList<String>();
          String formati = getOptConfigurazionePerUfficioIntestatario("conservazione.formatiConsentiti",uffint);
          if(StringUtils.isBlank(formati))
            formati=ConfigManager.getValore("uploadFile.estensioniAmmesse").replace(";",",").toLowerCase();
          String formatifirmadigitale = getOptConfigurazionePerUfficioIntestatario("conservazione.formatiConsentitiFirmaDigitale",uffint);
          if (formatifirmadigitale != null) {
            if (!formatifirmadigitale.equals("")) {
              // devo conservare tutte le estensioni
              formati = formati + "," + formatifirmadigitale;
            }
          }
          if (formati != null) {
            formatiCOS = new ArrayList<String>(Arrays.asList(formati.split(",")));
          }
          String[] ve = nomeFile.split("\\.");
          for (int j = ve.length - 1; j > 0; j--) {
            String vej = ve[j].toLowerCase();
            if (formatiCOS.contains(vej)) {
              estensioni = "." + vej + estensioni;
            } else {
              break;
            }
          }
          int numerofile = 1;
          Long lastCosNomeFile= (Long) this.sqlManager.getObject("select count(*) from gardoc_wsdm where cos_nome_file like ?" ,
              new Object[] {impronta + "%"});
          if(lastCosNomeFile>0) {
            numerofile += lastCosNomeFile;
          }
          String newNomeFile = impronta + "-" + numerofile + estensioni;
          try {
            String nomeTempFile = impronta + "-" + numerofile + ".tmp";
            sftp.Put(bytes, base + nomeTempFile);

            File tempFile = File.createTempFile("tmp", null, null);
            sftp.Get(base + nomeTempFile, new FileSystemFile(tempFile));

            byte[] b2 = FileUtils.readFileToByteArray(tempFile);
            String improntaCheck = getImpronta(b2);
            if (impronta.equals(improntaCheck)) {
              sftp.Rename(base + nomeTempFile, base + newNomeFile);
            }
          } catch (Exception ex) {
            logger.error("Errore durante il trasferimento del documento su area FTP: " + ex);
            this.updateStatoEsitoNegativoCOS(idDoc, idArchiviazione,"Errore nel trasferimento del documento su area FTP: " + ex);
            esito = 0;
          }

          this.updateStatoArchiviazioneDocumento(idDoc, idArchiviazione,new Long(4), newNomeFile);

        } catch (Exception ex) {
          logger.error("Errore durante il trasferimento del documento su area FTP: " + ex);
          this.updateStatoEsitoNegativoCOS(idDoc, idArchiviazione,"Errore nel trasferimento del documento su area FTP: " + ex);
          esito = 0;
        }

        return esito;

    }catch(Exception ex){
      logger.error("Errore in fase di connessione: " + ex);
      this.updateStatoEsitoNegativoCOS(idDoc, idArchiviazione,"Errore in fase di connessione: " + ex);
      return 0;
    }

  }

  public String getImpronta(byte[] buffer) throws NoSuchAlgorithmException, IOException {
    int count;
    MessageDigest digest = MessageDigest.getInstance("SHA-256");

    java.io.BufferedInputStream bis = new java.io.BufferedInputStream(new java.io.ByteArrayInputStream(buffer));
    while ((count = bis.read(buffer)) > 0) {
      digest.update(buffer, 0, count);
    }
    bis.close();

    byte[] hash = digest.digest();
    return javax.xml.bind.DatatypeConverter.printHexBinary(hash);
  }

  public Long insertJobArchiviazioneDocumenti(Long syscon, String codgar, String entita, String tipoWSDM, String classifica, String codiceRegistro, String tipoDocumento, String mittInterno,
      String classificaTitolazione, String indice, String unitaOperativaMittente, String mezzo, String struttura, String supporto, Long tipo_archiviazione, String sottotipo, Long sysdet) {
    Long idArchiviazione = null;
    try {
      idArchiviazione = new Long(genChiaviManager.getNextId("GARDOC_JOBS"));
        //aggiorno flag di esecuzione nella lista record da elaborare
        this.sqlManager.update("insert into gardoc_jobs(id_archiviazione, syscon, codgara, data_inserimento, da_processare, tipo_archiviazione, classifica, cod_reg," +
                " tipo_doc, mitt_int, classifica_tit, indice, uo_mittdest, mezzo, struttura, supporto,sottotipo, entita, sysdet)" +
                " values(?,?,?,?,'1',?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
            new Object[]{idArchiviazione, syscon, codgar, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), tipo_archiviazione, classifica, codiceRegistro,
            tipoDocumento, mittInterno, classificaTitolazione, indice, unitaOperativaMittente,mezzo,struttura,supporto,sottotipo,entita,sysdet});

    } catch (Exception e) {
        logger.error("archiviazioneDocumentiGara.insertJob : errore durante l'inserimento del job di archiviazione documenti per la gara " + codgar, e);
    }
    return idArchiviazione;
  }

  public boolean insertDettJobArchiviazioneDocumenti(Long syscon, String idRichiesta, String key1, String key2, String provenienza , String documentiAssociatiDB ) {
    try {
        Long id_doc = new Long(genChiaviManager.getNextId("GARDOC_WSDM"));

        String entita = "W_DOCDIG";
        ;
        if("4".equals(provenienza)&& !documentiAssociatiDB.equals("1")){
          entita = "C0OGGASS";
        }

        this.sqlManager.update("insert into gardoc_wsdm(id, id_archiviazione, entita, key1, key2, provenienza, stato_archiviazione) values(?,?,?,?,?,?,?)",
            new Object[]{id_doc, new Long(idRichiesta), entita, key1, key2, provenienza, new Long(1)});
        return true;
    } catch (Exception e) {
        logger.error("archiviazioneDocumentiGara.insertJob : errore durante l'inserimento del job di archiviazione documenti per la gara " + "id_doc", e);
    }
    return false;
  }

  public Long updateJobArchiviazioneDocumenti(Long id_archiviazione, String da_processare, int numDocEsitoNegativo, String esito, String codgar, String codice, Long genere, Long sysconRichiesta) {
    //idArchiviazione, statoGardocJobs, numDocEsitoNegativo, messaggioApp, codgara , codice, genere
    String msgGenere = "";
    String message = "";
    if(genere == null) {
      msgGenere = "della stipula  ";
    }else if (new Long(10).equals(genere)){
      msgGenere = "dell' elenco operatori ";
    }else if(new Long(20).equals(genere)){
      msgGenere = "del catalogo elettronico ";
    }else{
      msgGenere = "della gara ";
    }

    try {

      if(numDocEsitoNegativo >0){
        message = "La richiesta di archiviazione per i documenti " + msgGenere + codice + " ha prodotto errori su " + numDocEsitoNegativo + " documenti." +
        "\r\n" + "Per consultarne il dettaglio accedere alla maschera di archiviazione documenti.";
      }else{
        message = "E' stato effettuato correttamente il trasferimento al documentale per i documenti " + msgGenere + codice;
      }

      this.sqlManager.update("update gardoc_jobs set da_processare = ?, esito = ?, data_creazione = ? where id_archiviazione = ?",
            new Object[]{da_processare, esito, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), id_archiviazione});

      //inserisco il messaggio
      String insertW_MESSAGE_IN = "insert into w_message_in (message_id, message_date, message_subject, message_body, message_sender_syscon, message_recipient_syscon, message_recipient_read) values (?,?,?,?,?,?,?)";
      Long maxMessageIdIn = (Long) this.sqlManager.getObject("select max(message_id) from w_message_in", new Object[] {});
      if (maxMessageIdIn == null) maxMessageIdIn = new Long(0);
      maxMessageIdIn = new Long(maxMessageIdIn.longValue() + 1);
      this.sqlManager.update(insertW_MESSAGE_IN, new Object[] { maxMessageIdIn, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),
              message, null, 50, sysconRichiesta, new Long(0) });


    } catch (Exception e) {
      String msg = "archiviazioneDocumentiGara.insertJob : errore durante l'aggiornamento del job di archiviazione documenti per la";
      if(genere == null)
        msg +="la stipula" + codice;
      else
        msg +="la gara" + codgar;
      logger.error(msg, e);
    }
    return id_archiviazione;
  }


  public void updateStatoArchiviazioneDocumento(Long idDoc, Long idArchiviazione, Long statoArchiviazione) throws SQLException{
    this.sqlManager.update("update gardoc_wsdm set stato_archiviazione = ?, esito = null where id= ? and id_archiviazione = ?",
                      new Object[]{ new Long(statoArchiviazione), idDoc, idArchiviazione });
  }

  public void updateStatoArchiviazioneDocumento(Long idDoc, Long idArchiviazione, Long statoArchiviazione, String cos_nome_file) throws SQLException{
      this.sqlManager.update("update gardoc_wsdm set stato_archiviazione = ?, cos_nome_file = ?, esito = null where id= ? and id_archiviazione = ?",
                        new Object[]{ new Long(statoArchiviazione), cos_nome_file, idDoc, idArchiviazione });
  }
  public void updateStatoArchiviazioneIndiceDocumento(Long idDoc, Long idArchiviazione, Long statoArchiviazione, String cos_indice) throws SQLException{
    this.sqlManager.update("update gardoc_wsdm set stato_archiviazione = ?, cos_indice = ?, esito = null where id= ? and id_archiviazione = ?",
                      new Object[]{ new Long(statoArchiviazione), cos_indice, idDoc, idArchiviazione });
  }
  //imposta lo stato di archiviazione in blocco guardando il file di indice di riferimento
  public void updateStatoArchiviazionePacchettoCOS(Long statoArchiviazione, String cos_pid, String cos_indice) throws SQLException{
      this.sqlManager.update("update gardoc_wsdm set stato_archiviazione = ?, cos_pid = ? where cos_indice = ?",
                        new Object[]{ new Long(statoArchiviazione), cos_pid, cos_indice });
  }

  public void updateEsitoDocumentoCOS(String esito, String cos_indice, String cos_nome_file) throws SQLException{
      this.sqlManager.update("update gardoc_wsdm set esito = ? where cos_indice = ? and cos_nome_file = ?",
                        new Object[]{ esito, cos_indice, cos_nome_file });
  }

  public void updateUIDDocumentoCOS(String UID, String cos_indice, String cos_nome_file) throws SQLException{
      this.sqlManager.update("update gardoc_wsdm set cos_uid = ? where cos_indice = ? and cos_nome_file = ?",
                        new Object[]{ UID, cos_indice, cos_nome_file });
  }

  public void updateStatoEsitoNegativoCOS(Long idDoc, Long idArchiviazione, String esito) throws SQLException{
    this.sqlManager.update("update gardoc_wsdm set esito = ?, stato_archiviazione= 2 where id = ? and id_archiviazione = ?",
                      new Object[]{ esito, idDoc, idArchiviazione });
  }

  public void updateStatoEsito(Long stato, String esito, Long idDoc, Long idArchiviazione) throws SQLException{
    this.sqlManager.update("update gardoc_wsdm set stato_archiviazione= ?, esito = ? where id = ? and id_archiviazione = ?",
                      new Object[]{stato, esito, idDoc, idArchiviazione });
  }

  public void updateStatoEsitoGlobale(Long stato, String esito, String cos_indice, Long idDoc) throws SQLException, GestoreException{
        this.sqlManager.update("update gardoc_wsdm set stato_archiviazione= ?, cos_indice = ?, esito = ? where id = ?",
                          new Object[]{stato, cos_indice, esito, idDoc});
  }

  public void updateStatoArchiviazioneDocumentoDIGIP(Long idDoc, Long idArchiviazione, Long statoArchiviazione, String nomeSIP, String uuidSIP, String esito) throws SQLException{
    this.sqlManager.update("update gardoc_wsdm set stato_archiviazione = ?, nome_sip = ?, id_sip=?, esito = ? where id= ? and id_archiviazione = ?",
            new Object[]{statoArchiviazione, nomeSIP, uuidSIP, esito, idDoc, idArchiviazione });
  }
  public void updateArchiviatoConChiaveDIGIP(Long idDoc, Long idArchiviazione, String nomesip, String chiave) throws SQLException{
    this.sqlManager.update("update gardoc_wsdm set stato_archiviazione = 4, nome_sip = ?, chiave_sip=? where id= ? and id_archiviazione = ?",
            new Object[]{nomesip, chiave, idDoc, idArchiviazione });
  }
  public void updateStatoFascicolazioneDocumentiDIGIP(String idFascicolo, String chiaveFascicolo, String codgar) throws SQLException{
    this.sqlManager.update("update gardoc_wsdm set id_fascicolo=?, chiave_fascicolo=?, stato_archiviazione=6, esito=null where stato_archiviazione in (5,6,9) and chiave_sip is not null and id_archiviazione in (select w.id_archiviazione from gardoc_jobs j, gardoc_wsdm w where j.codgara=? and j.tipo_archiviazione = 6)",
            new Object[]{idFascicolo,chiaveFascicolo, codgar });
  }
  public void updateStatoNegativoFascicolazioneDocumentiDIGIP(String chiaveFascicolo,String esito, String codgar) throws SQLException{
    this.sqlManager.update("update gardoc_wsdm set chiave_fascicolo=?, stato_archiviazione=9, esito=? where stato_archiviazione in (5,6,9) and chiave_sip is not null and id_archiviazione in (select w.id_archiviazione from gardoc_jobs j, gardoc_wsdm w where j.codgara=? and j.tipo_archiviazione = 6)",
            new Object[]{chiaveFascicolo,esito, codgar });
  }

  public void insertMsg(String messaggio, Long destinatario) throws SQLException, GestoreException{
    String insertW_MESSAGE_IN = "insert into w_message_in (message_id, message_date, message_subject, message_body, message_sender_syscon, message_recipient_syscon, message_recipient_read) values (?,?,?,?,?,?,?)";
    Long maxMessageIdIn = (Long) this.sqlManager.getObject("select max(message_id) from w_message_in", new Object[] {});
    if (maxMessageIdIn == null) maxMessageIdIn = new Long(0);
    maxMessageIdIn = new Long(maxMessageIdIn.longValue() + 1);
    this.sqlManager.update(insertW_MESSAGE_IN, new Object[] { maxMessageIdIn, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),
        messaggio, null, 50, destinatario, new Long(0) });
}

  /**
   * Funzione valida solo per JIRIDE
   * @param ngara
   * @param codgar
   * @param codDitta
   * @param tipoCollegamento
   * @param busta
   * @param numeroDoc
   * @param idprg
   * @param iddocdig
   * @param username
   * @param ruolo
   * @return WSDMDocumentoCollegaResType
   * @throws GestoreException
   */
  public WSDMDocumentoCollegaResType  collegaDocumenti(String ngara, String codgar, String codDitta, String tipoCollegamento, Long busta, String numeroDoc,
      String idprg, String iddocdig, String username, String ruolo, String idconfi, String idprgFS12, Long idcomFS12) throws GestoreException{
    WSDMDocumentoCollegaResType  ret = null;
    String selectNumeoDocumento="select numerodoc from wsdocumento where numeroprot=? and annoprot = ? and inout=?";
    String selectDitg="select nprdom, dprdom from ditg where ngara5=? and dittao=?";
    if(!new Long(4).equals(busta))
      selectDitg="select nproff, dproff from ditg where ngara5=? and dittao=?";
    try {
      //Se numeroDoc è nullo, lo si deve ricavare
      if(numeroDoc==null || "".equals(numeroDoc)){
        numeroDoc = (String)this.sqlManager.getObject("select d.numerodoc from wsdocumento d, wsallegati a where "
            + "a.entita= ? and a.key1= ? and a.key2= ? and a.idwsdoc = d.id", new Object[]{"W_DOCDIG", idprg, iddocdig});
      }
      if(numeroDoc!=null && !"".equals(numeroDoc)){
        String chiaveGara=ngara;
        Long genere = (Long)this.sqlManager.getObject("select genere from v_gare_genere where codgar=?", new Object[]{codgar});
        if(new Long(3).equals(genere))
          chiaveGara = codgar;
        Vector dati = null;
        if(StringUtils.isNotBlank(idprgFS12) && idcomFS12!=null) {
          dati = this.sqlManager.getVector("select COMNUMPROT, COMDATPROT from W_INVCOM where idprg=? and idcom=?",new Object[]{idprgFS12, idcomFS12});
        }else {
          dati = this.sqlManager.getVector(selectDitg, new Object[]{chiaveGara, codDitta});
        }
        if(dati!=null && dati.size()>0){
          String numProt = SqlManager.getValueFromVectorParam(dati, 0).stringValue();
          Timestamp dataProt = SqlManager.getValueFromVectorParam(dati, 1).dataValue();
          if(numProt!=null && dataProt!=null){
            Date date = new Date(dataProt.getTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int anno = calendar.get(Calendar.YEAR);
            String numDoc =(String)this.sqlManager.getObject(selectNumeoDocumento, new Object[]{numProt, new Long(anno), "IN"});
            ret = this.gestioneWSDMManager.WSDMDocumentoCollega(username, ruolo, this.gestioneWSDMManager.SERVIZIO_DOCUMENTALE, numeroDoc, numDoc, tipoCollegamento,idconfi);
          }
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della lista dei documenti da trasferire al documentale", null, e);
    }catch (Exception e) {
      throw new GestoreException("Errore nella chiamata al servizio di collegamento dei documenti", null, e);
    }

    return ret;
  }

  /**
   * Funzione valida solo per JIRIDE
   * @param ngara
   * @param codgar
   * @param codDitta
   * @param tipoCollegamento
   * @param busta
   * @param numeroDoc
   * @param idprg
   * @param iddocdig
   * @param username
   * @param ruolo
   * @return WSDMDocumentoCollegaResType
   * @throws GestoreException
   */
  public WSDMAggiungiAllegatiResType  aggiungiAllegati (String ngara, String codgar, String codDitta, Long busta, WSDMProtocolloAllegatoType[] allegati,
      String idprg, String iddocdig, String username, String ruolo, Long idArchiviazione,String idconfi, String idprgFS12, Long idcomFS12) throws GestoreException{
    WSDMAggiungiAllegatiResType  ret = null;

    String selectNumeroDocumento="select numerodoc from wsdocumento where numeroprot=? and annoprot = ? and inout=?";

    try {
      //Se numeroDoc è nullo, lo si deve ricavare

        Vector<?> dati = null;
        if(StringUtils.isNotBlank(idprgFS12) && idcomFS12!=null) {
          dati = this.sqlManager.getVector("select COMNUMPROT, COMDATPROT from W_INVCOM where idprg=? and idcom=?",new Object[]{idprgFS12, idcomFS12});
        } else{
          dati = getDatiProtocolloDitta(ngara, codgar, codDitta, busta);
        }
        if(dati!=null && dati.size()>0){
          String numProt = SqlManager.getValueFromVectorParam(dati, 0).stringValue();
          Timestamp dataProt = SqlManager.getValueFromVectorParam(dati, 1).dataValue();
          if(numProt!=null && dataProt!=null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dataProt.getTime());
            int annoProt = calendar.get(Calendar.YEAR);
            String numDoc =(String)this.sqlManager.getObject(selectNumeroDocumento, new Object[]{numProt, new Long(annoProt), "IN"});
            ret = this.gestioneWSDMManager.WSDMAggiungiAllegati(username, ruolo, GestioneWSDMManager.SERVIZIO_DOCUMENTALE, numDoc,annoProt,numProt,allegati,idconfi);
            Long idWSDoc =(Long)this.sqlManager.getObject("select id from wsdocumento where numerodoc = ? and annoprot = ?", new Object[]{numDoc, new Long(annoProt)});
            if(ret.isEsito()){
              //Aggiorno lo stato della elaborazione positivo
              this.gestioneWSDMManager.setWSAllegati("W_DOCDIG", idprg, iddocdig, null, null, idWSDoc);
            }
          }
        }
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della lista dei documenti da trasferire al documentale", null, e);
    }catch (Exception e) {
      throw new GestoreException("Errore nella chiamata al servizio di collegamento dei documenti", null, e);
    }

    return ret;
  }

  /**
   * Vengono estratti i campi nprdom, dprdom (o nproff, dproff nel caso di busta=4) della ditta
   * @param ngara
   * @param codgar
   * @param codDitta
   * @param busta
   * @return Vector<?>
   * @throws SQLException
   */
  private Vector<?> getDatiProtocolloDitta(String ngara, String codgar, String codDitta, Long busta) throws SQLException {
    String dbFunctionDateToString = sqlManager.getDBFunction("DATETIMETOSTRING",
        new String[] { "dprdom" });
    String selectDitg="select nprdom, dprdom, " + dbFunctionDateToString + " from ditg where ngara5=? and dittao=?";
    if(!new Long(4).equals(busta)) {
      dbFunctionDateToString = sqlManager.getDBFunction("DATETIMETOSTRING",
          new String[] { "dproff" });
      selectDitg="select nproff, dproff, " + dbFunctionDateToString + " from ditg where ngara5=? and dittao=?";
    }
    String chiaveGara=ngara;
    Long genere = (Long)this.sqlManager.getObject("select genere from v_gare_genere where codgar=?", new Object[]{codgar});
    if(new Long(3).equals(genere))
      chiaveGara = codgar;
    Vector<?> datiDITG = this.sqlManager.getVector(selectDitg, new Object[]{chiaveGara, codDitta});
    return datiDITG;

  }

  public JSONObject getListaDocumenti(String codgar, String genere, String key1, String stipula, String dittaoFiltro, boolean filtroDocumentiGara, boolean escludiEML) throws SQLException, GestoreException {

    String documentiAssociatiDB = ConfigManager.getValore("it.eldasoft.documentiAssociatiDB");

    JSONObject result = new JSONObject();
    int total = 0;
    int totalAfterFilter = 0;
    String oggetto = "gara";
    Long idStipula=null;
    int documentiDaArchiviareDocumentale = 0;
    int documentiDaArchiviareCOS = 0;
    int documentiNonArchiviabiliCOS = 0;

    boolean skipDocumentiDitte = StringUtils.isEmpty(dittaoFiltro) && filtroDocumentiGara;

    try {
      String queryDocGara = "";
      String queryDocComunicazioni = "";
      String queryDocDitta = "";
      String queryDocAssociati = "";
      String queryDocComAllaDitta = "";
      String queryDocComDallaDitta = "";
      String queryDocRiepilogoBuste = "";
      String queryDocPubAttiOfferte = "";
      String queryDocStipula = "";
      String queryDocAssociatiStipula = "";
      String queryDocComAllaDittaStipula = "";
      String queryDocComDallaDittaStipula = "";

      if("true".equals(stipula)) {
        idStipula = new Long(key1);
        oggetto = "stipula contratto";
        String condizioneAppend = this.sqlManager.getDBFunction("concat",  new String[] {"'Documentazione '" , "'" + oggetto + "'" });

        String castDATA="DATE";
        if("POS".equals(this.sqlManager.getTipoDB()))
          castDATA="TIMESTAMP";
        else if("MSQ".equals(this.sqlManager.getTipoDB()))
          castDATA="DATETIME";

        String condizioneAppend1 = this.sqlManager.getDBFunction("concat",  new String[] {"'Documento contratto - '" , "t.tab1desc" });

        //Documentazione contratto
        queryDocStipula = "select " + condizioneAppend + " as ARGOMENTO, " + condizioneAppend1 + " as GRUPPO,  cast(null as varchar(20)) as LOTTO," +
            " cast(null as varchar(16)) as CFDITTA, CASE WHEN d.visibilita = 3 THEN vg.codimp ELSE cast(null as varchar(120)) END as DITTA, " +
            " TITOLO as DESCRIZIONE, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, " +
            this.sqlManager.getDBFunction("datetimetostring",  new String[] {"CASE WHEN d.visibilita=3 THEN i.comdatins ELSE cast(null as " + castDATA + ") END"})+" as DATA, CASE WHEN d.visibilita = 3 THEN i.comnumprot ELSE cast(null as varchar(20)) END AS NUMEROPROTOCOLLO, w.digtipdoc as TIPODOCUMENTO " +
            " from G1DOCSTIPULA  d join V_GARE_DOCSTIPULA w on w.id=d.id join tab1 t on t.tab1tip=d.visibilita " +
            " join V_GARE_STIPULA vg on w.idstipula=vg.id left join w_invcom i on i.idcom = (select max (idcom) from w_invcom where comkey2=vg.codstipula and COMMSGOGG = 'Conferma invio documenti' group by comkey1,comkey2) "+
            " where d.idstipula = ? and w.idprg is not null and w.IDDOCDIG is not null and t.tab1cod='A1182' order by d.visibilita, numord";

        //DOCUMENTI ASSOCIATI
        String sqlSintassi= this.sqlManager.getDBFunction("inttostr",  new String[] {"g.id"});
        if (documentiAssociatiDB.equals("1")) {
          queryDocAssociatiStipula = "select " + condizioneAppend + " as ARGOMENTO," +
                " cast('Documento associato' as varchar(100)) as GRUPPO, cast(null as varchar(20)) as LOTTO, " +
          "cast(null as varchar(16)) as CFDITTA, cast(null as varchar(120)) as DITTA, " +
          "c.c0atit as DESCRIZIONE , w.idprg, w.iddocdig, c.c0anomogg as DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, coalesce(" +
          this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) +"," +
          this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) +"," +
          this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"}) + ") as DATA, w.digtipdoc as TIPODOCUMENTO " +
          "from c0oggass c join G1STIPULA g on c.c0akey1=" + sqlSintassi + " join w_docdig w on cast(c.c0acod as varchar(10))=w.digkey1 and c.c0aprg=w.idprg and w.digent='C0OGGASS' " +
          "where g.id = ? and c.c0aent in ('G1STIPULA','G1DOCSTIPULA') " ;

        } else {
          queryDocAssociatiStipula = "select " + condizioneAppend + " as ARGOMENTO," +
                  " cast('Documento associato' as varchar(100)) as GRUPPO, cast(null as varchar(20)) as LOTTO, " +
            "cast(null as varchar(16)) as CFDITTA, cast(null as varchar(120)) as DITTA, " +
            "c.c0atit as DESCRIZIONE, c.c0aprg as IDPRG, c.c0acod as IDDOCDIG, c.c0anomogg as DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, coalesce(" +
            this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) +"," +
            this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) +"," +
            this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"}) + ") as DATA " +
            "from c0oggass c join G1STIPULA g on c.c0akey1= " + sqlSintassi +
            "where g.id = ? and c.c0aent in ('G1STIPULA','G1DOCSTIPULA') " ;

        }

        //Documenti inviati alla ditta
        String filtroEML = "(w.digent = 'W_INVCOM' or (w.digent='W_INVCOMDES' and w.digkey3="+this.sqlManager.getDBFunction("inttostr",  new String[] {"id.idcomdes"})+"))";
        if(escludiEML)
          filtroEML = "w.digent = 'W_INVCOM'";

        queryDocComAllaDittaStipula = "select DISTINCT 'Documentazione delle ditte' as ARGOMENTO," +
            " cast('Documento inviato alla ditta' as varchar(100))   as GRUPPO, cast(null as varchar(20)) as LOTTO, " +
           " cast(null as varchar(16)) as CFDITTA, id.descodsog as DITTA, " +
           this.sqlManager.getDBFunction("concat",  new String[] {this.sqlManager.getDBFunction("concat",  new String[] {"i.commsgogg" , "' - '" }) , "w.DIGDESDOC" }) + " as DESCRIZIONE," +
           " w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, " +
            this.sqlManager.getDBFunction("datetimetostring",  new String[] {"id.DESDATINV"}) + " as DATA, id.idcom as IDCOM, DESCODENT, " +
            "i.comnumprot as NUMEROPROTOCOLLO, w.digtipdoc as TIPODOCUMENTO "+
          "from w_invcom i join (select DISTINCT idprg,idcom,descodsog,descodent,DESSTATO,MIN(DESDATINV)  as DESDATINV,idcomdes from w_invcomdes group by idprg,idcom,descodsog,descodent,DESSTATO,idcomdes) id on id.idprg = i.idprg and id.idcom = i.idcom " +
          "join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + "=w.digkey2 and w.digkey1=i.idprg " +
          "and "+filtroEML+" join g1stipula g on i.comkey1= g.codstipula "  +
          "where g.id = ? and i.compub <> '1' and (COMTIPO is null or COMTIPO<>'FS12') and (id.DESSTATO = '2' or id.DESSTATO = '4') order by DITTA,IDCOM,IDDOCDIG";


        //Documenti inviati dalla ditta

        queryDocComDallaDittaStipula = "select cast('Documentazione delle ditte' as varchar(100)) as ARGOMENTO, cast('Documento ricevuto dalla ditta' as varchar(100)) as GRUPPO, " +
            "cast(null as varchar(20)) as LOTTO, cast(null as varchar(16)) as CFDITTA, comkey1 as DITTA, " +
          this.sqlManager.getDBFunction("concat",  new String[] {this.sqlManager.getDBFunction("concat",  new String[] {"i.commsgogg" , "' - '" }) , "w.DIGDESDOC" }) + " as DESCRIZIONE, " +
          "w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, " +
          this.sqlManager.getDBFunction("datetimetostring",  new String[] {"i.COMDATINS"}) + " as DATA, " +
          "i.comnumprot as NUMEROPROTOCOLLO, w.digtipdoc as TIPODOCUMENTO "+
          "from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + " = w.digkey1 and i.idprg = w.idprg and w.digent = 'W_INVCOM' " +
          "join g1stipula g on i.comkey2=g.codstipula " +
          "where g.id = ? and i.compub <> '1' and COMTIPO='FS12' and COMSTATO='3' and COMENT = 'G1STIPULA' order by ditta,i.idcom,iddocdig";


      }else{
        String condizioneAppend = this.sqlManager.getDBFunction("concat",  new String[] {"'Documentazione '" , "'" + oggetto + "'" });
          //Documentazione di gara
          queryDocGara = "select " + condizioneAppend + " as ARGOMENTO, GRUPPO, NGARA as LOTTO," +
          		" cast(null as varchar(16)) as CFDITTA, cast(null as varchar(120)) as DITTA, " +
          		" DESCRIZIONE, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, isarchi as ARCHIVIATO, " +
          this.sqlManager.getDBFunction("datetimetostring",  new String[] {"DATARILASCIO"}) + " as DATA, w.digtipdoc as TIPODOCUMENTO  " +
          "from documgara d join w_docdig w on d.iddocdg=w.iddocdig and d.idprg=w.idprg and digent='DOCUMGARA' " +
          "where d.codgar = ? and d.idprg is not null and d.iddocdg is not null order by gruppo, numord, norddocg";

          //Comunicazioni pubbliche
          queryDocComunicazioni = "select " + this.sqlManager.getDBFunction("concat",  new String[] {"'Documentazione '" ,"'" + oggetto + "'"}) + " as ARGOMENTO, cast('Comunicazione pubblica' as varchar(100)) as GRUPPO, g.codiga as LOTTO," +
                " cast(null as varchar(16)) as CFDITTA, cast(null as varchar(120)) as DITTA, " +
                this.sqlManager.getDBFunction("concat",  new String[] {this.sqlManager.getDBFunction("concat",  new String[] {"i.commsgogg" , "' - '" }) , "w.DIGDESDOC" }) + " as DESCRIZIONE," +
          		" w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, (case when comstato='12' then '1' else cast(null as varchar(2)) end) as ARCHIVIATO, " +
                 this.sqlManager.getDBFunction("datetimetostring",  new String[] {"i.COMDATAPUB"}) + " as DATA, w.digtipdoc as TIPODOCUMENTO " +
          "from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + "=w.digkey2 and i.idprg=w.digkey1 and w.digent='W_INVCOM' join gare g on i.comkey1=g.ngara " +
          "where g.codgar1 = ? and i.compub = '1' and (i.comstato='3' or i.comstato='12') order by i.idcom,w.iddocdig";


          //Riepilogo busta domanda di partecipazione (APP-874)
          queryDocRiepilogoBuste = "select distinct cast('Documentazione presentata dalle ditte' as varchar(100)) as ARGOMENTO, CASE WHEN COMTIPO='FS10' THEN 'Riepilogo busta domanda di partecipazione' WHEN COMTIPO='FS11' THEN 'Riepilogo busta offerta' ELSE 'Notifica invio rinuncia offerta' END as GRUPPO, comkey2 as LOTTO,"+
              " cast(null as varchar(16)) as CFDITTA,  CASE WHEN COMTIPO='FS14' THEN comkey1 ELSE dittao END as DITTA, comkey2, W.DIGDESDOC as DESCRIZIONE, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO,"+
              " CASE WHEN COMTIPO='FS10' THEN "+this.sqlManager.getDBFunction("datetimetostring",  new String[] {"DRICIND"})+" WHEN COMTIPO='FS11' THEN "+this.sqlManager.getDBFunction("datetimetostring",  new String[] {"DATOFF"})+" ELSE CAST(null as VARCHAR(10)) END AS DATA,  CASE WHEN COMTIPO='FS10' THEN ORADOM WHEN COMTIPO='FS11' THEN ORAOFF ELSE CAST(NULL as VARCHAR(8)) END AS ORA, "+
              " CASE WHEN COMTIPO='FS14' THEN "+this.sqlManager.getDBFunction("datetimetostring",  new String[] {"DATRINUNCIA"}) + " ELSE CAST(NULL as VARCHAR(30)) END as DATRINUNCIA, i.comnumprot as NUMEROPROTOCOLLO, comtipo, w.digtipdoc as TIPODOCUMENTO"+
              " from ditg left outer join ragimp on (codime9=dittao and impman='1')"+
              " inner join w_puser on (userkey1=dittao or (userkey1=coddic and impman='1'))"+
              " inner join w_invcom i on (comkey1=usernome and comkey2=ngara5 and (comkey3 = ncomope or comkey3 is null))"+
              " join w_docdig w on (CAST(i.idcom as varchar(12)) = w.digkey1 and w.digent='W_INVCOM') "+
              " where ((COMTIPO in('FS10','FS11') and upper(DIGNOMDOC) LIKE 'RIEPILOGO_BUSTE.PDF%') or COMTIPO='FS14') and COMSTATO='6' and ditg.codgar5 = ? "+
              " and (CASE WHEN COMTIPO='FS10' THEN DRICIND WHEN COMTIPO='FS11' THEN DATOFF ELSE DATRINUNCIA END is not null) " + (StringUtils.isNotEmpty(dittaoFiltro) ? "and (CASE WHEN COMTIPO='FS14' THEN comkey1 ELSE dittao END =?) " : "") + "order by comkey2, comtipo, ditta";

          //Documenti presentati dalla ditta
          queryDocDitta = "select " + this.sqlManager.getDBFunction("concat",  new String[] {"'Documentazione presentata dalle ditte '" ,  "d.ngara" }) + " as ARGOMENTO, coalesce(d.bustadesc,'Documento presentato dalla ditta') as GRUPPO, d.ngara as LOTTO," +
                " cast(null as varchar(16)) as CFDITTA, d.codimp as DITTA, " +
          		" d.descrizione as DESCRIZIONE, d.IDPRG, d.iddocdg as IDDOCDIG, d.DIGNOMDOC , (case when docannul='1' then '3' else cast(null as varchar(2)) end) as ARCHIVIATO, " +
          		this.sqlManager.getDBFunction("datetimetostring",  new String[] {"d.datarilascio"})  + " as DATA , d.orarilascio as ORA  , d.doctel as DOCTEL, i.uuid as UUID, w.digtipdoc as TIPODOCUMENTO " +
          "from v_gare_docditta d join w_docdig w on d.iddocdg=w.iddocdig and d.idprg=w.idprg " +
          "join imprdocg i on d.codgar = i.codgar and d.ngara = i.ngara and d.codimp = i.codimp and d.norddoci = i.norddoci and d.proveni = i.proveni " +
          "where d.codgar = ? and d.idprg is not null and d.iddocdg is not null " + (StringUtils.isNotEmpty(dittaoFiltro) ? "and d.codimp=? " : "") + "order by d.codgar, d.ngara, d.bustaord, d.numord, d.codimp, d.norddoci";

          if (documentiAssociatiDB.equals("1")) {
              queryDocAssociati = "select " + this.sqlManager.getDBFunction("concat",  new String[] {"'Documentazione '" ,  "'" + oggetto + "'" }) + " as ARGOMENTO," +
              		" cast('Documento associato' as varchar(100)) as GRUPPO, c.c0akey1 as LOTTO, " +
              "cast(null as varchar(16)) as CFDITTA, cast(null as varchar(120)) as DITTA, " +
              "c.c0atit as DESCRIZIONE , w.idprg, w.iddocdig, c.c0anomogg as DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, coalesce(" +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) +"," +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) +"," +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"}) + ") as DATA, w.digtipdoc as TIPODOCUMENTO " +
              "from c0oggass c join gare g on c.c0akey1=g.ngara join w_docdig w on cast(c.c0acod as varchar(10))=w.digkey1 and c.c0aprg=w.idprg and w.digent='C0OGGASS' " +
              "where g.codgar1 = ? and c.c0aent in ('GARE','GCAP','GOEV','GARSED','GARECONT','GAREAVVISI') union " +

              "select " + this.sqlManager.getDBFunction("concat",  new String[] {"'Documentazione '" ,  "'" + oggetto + "'" }) + " as ARGOMENTO," +
              " cast('Documento associato' as varchar(100)) as GRUPPO, c.c0akey1 as LOTTO, " +
              "cast(null as varchar(16)) as CFDITTA, cast(null as varchar(120)) as DITTA, " +
              "c.c0atit as DESCRIZIONE , w.idprg, w.iddocdig, c.c0anomogg as DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, coalesce(" +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) +"," +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) +"," +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"}) +") as DATA, w.digtipdoc as TIPODOCUMENTO " +
              "from c0oggass c join torn t on c.c0akey1=t.codgar join w_docdig w on cast(c.c0acod as varchar(10))=w.digkey1 and c.c0aprg=w.idprg and w.digent='C0OGGASS' " +
              "where t.codgar = ? and c.c0aent  = 'TORN' ";


          } else {
              queryDocAssociati = "select " + this.sqlManager.getDBFunction("concat",  new String[] {"'Documentazione '" , "'" + oggetto + "'" }) + " as ARGOMENTO," +
              		" cast('Documento associato' as varchar(100)) as GRUPPO, c.c0akey1 as LOTTO, " +
              "cast(null as varchar(16)) as CFDITTA, cast(null as varchar(120)) as DITTA, " +
              "c.c0atit as DESCRIZIONE, c.c0aprg as IDPRG, c.c0acod as IDDOCDIG, c.c0anomogg as DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, coalesce(" +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) +"," +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) +"," +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"}) + ") as DATA " +
              "from c0oggass c join gare g on c.c0akey1=g.ngara " +
              "where g.codgar1 = ? and c.c0aent in ('GARE','GCAP','GOEV','GARSED','GARECONT','GAREAVVISI') union " +


              "select " + this.sqlManager.getDBFunction("concat",  new String[] {"'Documentazione '" , "'" + oggetto + "'" }) + " as ARGOMENTO," +
              " cast('Documento associato' as varchar(100)) as GRUPPO, c.c0akey1 as LOTTO, " +
              "cast(null as varchar(16)) as CFDITTA, cast(null as varchar(120)) as DITTA, " +
              "c.c0atit as DESCRIZIONE, c.c0aprg as IDPRG, c.c0acod as IDDOCDIG, c.c0anomogg as DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, coalesce(" +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) +"," +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) +"," +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"}) + ") as DATA " +
              "from c0oggass c join torn t on c.c0akey1=t.codgar " +
              "where t.codgar = ? and c.c0aent  = 'TORN'";

          }

        //Documenti inviati alla ditta

          String filtroEML = "(w.digent = 'W_INVCOM' or (w.digent='W_INVCOMDES' and w.digkey3="+this.sqlManager.getDBFunction("inttostr",  new String[] {"id.idcomdes"})+"))";
          if(escludiEML) filtroEML  = "w.digent = 'W_INVCOM'";

          queryDocComAllaDitta = "select DISTINCT " + this.sqlManager.getDBFunction("concat",  new String[] {"'Documentazione '" , "'" + oggetto + "'" }) + " as ARGOMENTO," +
          		" cast('Documento inviato alla ditta' as varchar(100))   as GRUPPO, comkey1 as LOTTO, " +
               " cast(null as varchar(16)) as CFDITTA, id.descodsog as DITTA, " +
               this.sqlManager.getDBFunction("concat",  new String[] {this.sqlManager.getDBFunction("concat",  new String[] {"i.commsgogg" , "' - '" }) , "w.DIGDESDOC" }) + " as DESCRIZIONE," +
          	   " w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, " +
          		this.sqlManager.getDBFunction("datetimetostring",  new String[] {"id.DESDATINV"}) + " as DATA, id.idcom as IDCOM, DESCODENT, " +
          		"i.comnumprot as NUMEROPROTOCOLLO, w.digtipdoc as TIPODOCUMENTO "+
          "from w_invcom i join (select DISTINCT idprg,idcom,descodsog,descodent,DESSTATO,MIN(DESDATINV) as DESDATINV,idcomdes from w_invcomdes group by idprg,idcom,descodsog,descodent,DESSTATO,idcomdes) id on id.idprg = i.idprg and id.idcom = i.idcom " +
          "join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + "=w.digkey2 and w.digkey1=i.idprg and "+filtroEML+" join gare g on i.comkey1=g.ngara " +
          "where g.codgar1 = ? and i.compub <> '1' and (COMTIPO is null or COMTIPO<>'FS12') and (id.DESSTATO = '2' or id.DESSTATO = '4') " + (StringUtils.isNotEmpty(dittaoFiltro) ? "and id.descodsog=? " : "") + "order by DITTA,IDCOM,IDDOCDIG";


          String whereFS12rettNonSbustate = " and not exists (select 1 from w_invcom x where x.comstato='5' and ((i.commodello=13 and x.comtipo='FS11B') or (i.commodello=18 and x.comtipo='FS11C')) and x.idprg=i.idprg and i.comkey1=x.comkey1 and (i.comkey3=x.comkey2 or (i.comkey3 is null and i.comkey2=x.comkey2))) ";
          //Documenti inviati dalla ditta
          if ("1".equals(genere)) {
            queryDocComDallaDitta = "select cast('Documentazione presentata dalle ditte' as varchar(100)) as ARGOMENTO, cast('Documento inviato dalla ditta' as varchar(100)) as GRUPPO, comkey2 as LOTTO, " +
            "cast(null as varchar(16)) as CFDITTA, comkey1 as DITTA, " +
            this.sqlManager.getDBFunction("concat",  new String[] {this.sqlManager.getDBFunction("concat",  new String[] {"i.commsgogg" , "' - '" }) , "w.DIGDESDOC" }) + " as DESCRIZIONE, " +
            "w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, " +
            this.sqlManager.getDBFunction("datetimetostring",  new String[] {"i.COMDATINS"}) + " as DATA, " +
            "i.comnumprot as NUMEROPROTOCOLLO, w.digtipdoc as TIPODOCUMENTO "+
            "from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + " = w.digkey1 and i.idprg = w.idprg and w.digent = 'W_INVCOM' " +
            "join torn t on (i.comkey2=t.codgar) " +
            "where t.codgar = ? and i.compub <> '1' and COMTIPO='FS12' "+whereFS12rettNonSbustate+" and COMSTATO='3' and COMENT IS NULL " + (StringUtils.isNotEmpty(dittaoFiltro) ? "and comkey1=? " : "") + "order by ditta,i.idcom,w.iddocdig";
          }else{
            queryDocComDallaDitta = "select cast('Documentazione presentata dalle ditte' as varchar(100)) as ARGOMENTO, cast('Documento inviato dalla ditta' as varchar(100)) as GRUPPO, comkey2 as LOTTO, " +
            "cast(null as varchar(16)) as CFDITTA, comkey1 as DITTA, " +
            this.sqlManager.getDBFunction("concat",  new String[] {this.sqlManager.getDBFunction("concat",  new String[] {"i.commsgogg" , "' - '" }) , "w.DIGDESDOC" }) + " as DESCRIZIONE, " +
            "w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, " +
            this.sqlManager.getDBFunction("datetimetostring",  new String[] {"i.COMDATINS"}) + " as DATA, " +
            "i.comnumprot as NUMEROPROTOCOLLO, w.digtipdoc as TIPODOCUMENTO "+
            "from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + " = w.digkey1 and i.idprg = w.idprg and w.digent = 'W_INVCOM' " +
            "join gare g on i.comkey2=g.ngara " +
            "where g.codgar1 = ? and i.compub <> '1' and COMTIPO='FS12' "+whereFS12rettNonSbustate+" and COMSTATO='3' and COMENT IS NULL " + (StringUtils.isNotEmpty(dittaoFiltro) ? "and comkey1=? " : "") + "order by ditta,i.idcom,iddocdig";
          }

          //Documenti pubblicazione atti e offerte
          queryDocPubAttiOfferte = "select cast('Pubblicazione atti e offerte' as varchar(100)) as ARGOMENTO, cast('Pubblicazione atti e offerte' as varchar(100)) as GRUPPO, " +
          "cast(null as varchar(16)) as CFDITTA, da.dittao as DITTA, g.NGARA as LOTTO, " +
          "w.DIGDESDOC" + " as DESCRIZIONE, " + "w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, da.isarchi as ARCHIVIATO, " +
          this.sqlManager.getDBFunction("datetimetostring",  new String[] {"g1.dpubatti"}) + " as DATA " +
          "from g1documatti da join w_docdig w on da.idprg = w.idprg and da.iddocdig = w.iddocdig and w.digent = 'G1DOCUMATTI' " +
          "join gare g on da.ngara=g.ngara join gare1 g1 on g.ngara=g1.ngara " +
          "where g.codgar1 = ? and da.statodoc=5 order by g.codiga,ditta,w.iddocdig";
      }

      List<HashMap> hmDocGara = new ArrayList<HashMap>();
      if("true".equals(stipula)) {
        hmDocGara = sqlManager.getListHashMap(queryDocStipula, new Object[] { idStipula });
      } else if (StringUtils.isNotEmpty(dittaoFiltro)){
        //skip se cerco i documenti di una ditta
      }else {
        hmDocGara = sqlManager.getListHashMap(queryDocGara, new Object[] { codgar });
      }
        if(hmDocGara !=null && !hmDocGara.isEmpty()){
            for(int j=0; j<hmDocGara.size();j++) {
              HashMap hmj = hmDocGara.get(j);
              JdbcParametro jdbcLotto = (JdbcParametro) hmj.get("LOTTO");
              if (jdbcLotto != null) {
                String lotto = jdbcLotto.stringValue();
                String codiga = (String)this.sqlManager.getObject("select codiga from gare where ngara = ? ", new Object[]{lotto});
                hmj.put("LOTTO", new JdbcParametro(JdbcParametro.TIPO_TESTO, codiga));
              }
              //inserisco gli attributi per wsallegati e per gardoc_wsdm
              String idprg = null;
              Long iddocdig = null;
              String numero_protocollo = null;
              JdbcParametro jdbcIdprg = (JdbcParametro) hmj.get("IDPRG");
              if (jdbcIdprg != null){
                idprg = jdbcIdprg.stringValue();
              }

              JdbcParametro jdbcIddocdig = (JdbcParametro) hmj.get("IDDOCDIG");
              if (jdbcIddocdig != null){
                iddocdig = jdbcIddocdig.longValue();
              }

              JdbcParametro jdbcDitta = (JdbcParametro) hmj.get("DITTA");
              if (jdbcDitta != null) {
                String codimp = jdbcDitta.stringValue();
                String codPUser = (String)this.sqlManager.getObject("select userkey1 from w_puser where usernome = ? and userent = 'IMPR'", new Object[]{codimp});
                if (codPUser != null) {
                    codimp = codPUser;
                }
                //verifico se la ditta è un raggruppamento
                String getImpr = "select codimp, tipimp, cfimp, nomest from impr where codimp = ?";
                List<?> listaImpresa = this.sqlManager.getListVector(getImpr, new Object[]{codimp});
                if (listaImpresa != null && listaImpresa.size()>0) {
                    String cod = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 0).getStringValue();
                    Long tipimp = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 1).longValue();
                    String nomeImpresa = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).getStringValue();
                    if (tipimp != null && (new Long(3).equals(tipimp) || new Long(10).equals(tipimp))) {
                        //la ditta è un raggruppamento allora prendo i dati della mandataria
                        cod = (String)this.sqlManager.getObject("select coddic from ragimp where codime9 = ? and impman='1'", new Object[]{cod});
                        listaImpresa = this.sqlManager.getListVector(getImpr, new Object[]{cod});
                        if (listaImpresa != null && listaImpresa.size()>0) {
                            nomeImpresa += "(mandataria " + SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).getStringValue() + ")";
                        }
                    }
                    hmj.put("DITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeImpresa));
                    hmj.put("CFDITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, SqlManager.getValueFromVectorParam(listaImpresa.get(0), 2).getStringValue()));
                }else{
                  JdbcParametro jdbcMail = (JdbcParametro) hmj.get("MAIL");
                  if (jdbcMail != null) {
                    String mail = jdbcMail.stringValue();
                    hmj.remove("MAIL");
                    hmj.put("DITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, mail));
                  }
                }//listaImprese
              }

              JdbcParametro jdbcNumeroProtocollo= (JdbcParametro) hmj.get("NUMEROPROTOCOLLO");
              if (jdbcNumeroProtocollo != null)
                numero_protocollo = jdbcNumeroProtocollo.stringValue();

              Object[] datiComuni = getDatiComuniArchiviazione("W_DOCDIG",idprg,iddocdig.toString());
              hmj.put("IDWSDOC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[0]));
              hmj.put("STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[1]));
              hmj.put("STATO_COS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[2]));
              hmj.put("ESITO", new JdbcParametro(JdbcParametro.TIPO_TESTO, datiComuni[3]));
              hmj.put("PROVENIENZA", new JdbcParametro(JdbcParametro.TIPO_TESTO, "1"));
              hmj.put("NUM_PROT", new JdbcParametro(JdbcParametro.TIPO_TESTO, numero_protocollo));

              hmDocGara.set(j, hmj);
            }//for
          }

          if(!"true".equals(stipula)) {
            List<HashMap> hmDocComunicazioni = sqlManager.getListHashMap(queryDocComunicazioni, new Object[] { codgar });
            if(hmDocComunicazioni.size() >0 ){
              for(int j=0; j<hmDocComunicazioni.size();j++) {
                HashMap hmj = hmDocComunicazioni.get(j);

                //inserisco gli attributi per wsallegati e per gardoc_wsdm
                String idprg = null;
                Long iddocdig = null;

                JdbcParametro jdbcIdprg = (JdbcParametro) hmj.get("IDPRG");
                if (jdbcIdprg != null){
                  idprg = jdbcIdprg.stringValue();
                }

                JdbcParametro jdbcIddocdig = (JdbcParametro) hmj.get("IDDOCDIG");
                if (jdbcIddocdig != null){
                  iddocdig = jdbcIddocdig.longValue();
                }

                Object[] datiComuni = getDatiComuniArchiviazione("W_DOCDIG",idprg,iddocdig.toString());
                hmj.put("IDWSDOC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[0]));
                hmj.put("STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[1]));
                hmj.put("STATO_COS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[2]));
                hmj.put("ESITO", new JdbcParametro(JdbcParametro.TIPO_TESTO, datiComuni[3]));
                hmj.put("PROVENIENZA", new JdbcParametro(JdbcParametro.TIPO_TESTO, "2"));
                hmj.put("NUM_PROT", new JdbcParametro(JdbcParametro.TIPO_TESTO, null));

                hmDocComunicazioni.set(j, hmj);

                hmDocGara.add(hmDocComunicazioni.get(j));
              }
            }
          }

          //DOCUMENTI ASSOCIATI
          List<HashMap> hmDocAssociati = null;
          if("true".equals(stipula)) {
            hmDocAssociati = sqlManager.getListHashMap(queryDocAssociatiStipula, new Object[] { idStipula });
          } else if (StringUtils.isNotEmpty(dittaoFiltro)){
            //skip se cerco i documenti di una ditta
          }else {
            hmDocAssociati = sqlManager.getListHashMap(queryDocAssociati, new Object[] { codgar,codgar });
          }

          if(hmDocAssociati != null && !hmDocAssociati.isEmpty()){
            for(int j=0; j<hmDocAssociati.size();j++) {
              HashMap hmj = hmDocAssociati.get(j);
              JdbcParametro jdbcLotto = (JdbcParametro) hmj.get("LOTTO");
              if (jdbcLotto != null) {
                String lotto = jdbcLotto.stringValue();
                String codiga = (String)this.sqlManager.getObject("select codiga from gare where ngara = ? ", new Object[]{lotto});
                hmj.put("LOTTO", new JdbcParametro(JdbcParametro.TIPO_TESTO, codiga));
              }

              //inserisco gli attributi per wsallegati e per gardoc_wsdm
              String idprg = null;
              Long iddocdig = null;

              JdbcParametro jdbcIdprg = (JdbcParametro) hmj.get("IDPRG");
              if (jdbcIdprg != null){
                idprg = jdbcIdprg.stringValue();
              }

              JdbcParametro jdbcIddocdig = (JdbcParametro) hmj.get("IDDOCDIG");
              if (jdbcIddocdig != null){
                iddocdig = jdbcIddocdig.longValue();
              }
              HashMap hmAllegato = new HashMap();

              if (documentiAssociatiDB.equals("1")) {
                Object[] datiComuni = getDatiComuniArchiviazione("W_DOCDIG",idprg,iddocdig.toString());
                hmj.put("IDWSDOC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[0]));
                hmj.put("STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[1]));
                hmj.put("STATO_COS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[2]));
                hmj.put("ESITO", new JdbcParametro(JdbcParametro.TIPO_TESTO, datiComuni[3]));
                hmj.put("PROVENIENZA", new JdbcParametro(JdbcParametro.TIPO_TESTO, "7"));
                hmj.put("NUM_PROT", new JdbcParametro(JdbcParametro.TIPO_TESTO, null));
              }else{
                Object[] datiComuni = getDatiComuniArchiviazione("C0OGGASS",idprg,iddocdig.toString());
                hmj.put("IDWSDOC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[0]));
                hmj.put("STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[1]));
                hmj.put("STATO_COS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[2]));
                hmj.put("ESITO", new JdbcParametro(JdbcParametro.TIPO_TESTO, datiComuni[3]));
                hmj.put("PROVENIENZA", new JdbcParametro(JdbcParametro.TIPO_TESTO, "4"));
                hmj.put("NUM_PROT", new JdbcParametro(JdbcParametro.TIPO_TESTO, null));
              }
              hmDocAssociati.set(j, hmj);

            }//for
          }
          if(hmDocAssociati != null && !hmDocAssociati.isEmpty()){
            for(int j=0; j<hmDocAssociati.size();j++) {
              hmDocGara.add(hmDocAssociati.get(j));
            }
          }

          if(!"true".equals(stipula) && !skipDocumentiDitte) {
            //riepilogo busta domanda di partecipazione
            List<HashMap> hmDocRiepilogoBuste = null;

            Object [] parametri = new Object[] {codgar};
            if (StringUtils.isNotEmpty(dittaoFiltro)){
              parametri = new Object[] {codgar, dittaoFiltro};
            }
            hmDocRiepilogoBuste = sqlManager.getListHashMap(queryDocRiepilogoBuste, parametri);

            if(hmDocRiepilogoBuste.size() >0 ){
              for(int j=0; j<hmDocRiepilogoBuste.size();j++) {
                HashMap hmj = hmDocRiepilogoBuste.get(j);

                JdbcParametro jdbcLotto = (JdbcParametro) hmj.get("LOTTO");
                if (jdbcLotto != null) {
                  String lotto = jdbcLotto.stringValue();
                  String codiga = (String)this.sqlManager.getObject("select codiga from gare where ngara = ? ", new Object[]{lotto});
                  hmj.put("LOTTO", new JdbcParametro(JdbcParametro.TIPO_TESTO, codiga));
                }

                JdbcParametro jdbcDitta = (JdbcParametro) hmj.get("DITTA");
                if (jdbcDitta != null) {
                  String codimp = jdbcDitta.stringValue();

                  String codPUser = (String)this.sqlManager.getObject("select userkey1 from w_puser where usernome = ? and userent = 'IMPR'", new Object[]{codimp});
                  if (codPUser != null) {
                      codimp = codPUser;
                  } else {
                    ;
                  }

                  //verifico se la ditta è un raggruppamento
                  String getImpr = "select codimp, tipimp, cfimp, nomest from impr where codimp = ?";
                  List<?> listaImpresa = this.sqlManager.getListVector(getImpr, new Object[]{codimp});
                  if (listaImpresa != null && listaImpresa.size()>0) {
                      String cod = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 0).getStringValue();
                      Long tipimp = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 1).longValue();
                      String nomeImpresa = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).getStringValue();
                      if (tipimp != null && (new Long(3).equals(tipimp) || new Long(10).equals(tipimp))) {
                          //la ditta è un raggruppamento allora prendo i dati della mandataria
                          cod = (String)this.sqlManager.getObject("select coddic from ragimp where codime9 = ? and impman='1'", new Object[]{cod});
                          listaImpresa = this.sqlManager.getListVector(getImpr, new Object[]{cod});
                          if (listaImpresa != null && listaImpresa.size()>0) {
                              nomeImpresa += "(mandataria " + SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).getStringValue() + ")";
                          }
                      }
                      hmj.put("DITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeImpresa));
                      hmj.put("CFDITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, SqlManager.getValueFromVectorParam(listaImpresa.get(0), 2).getStringValue()));
                  }else{
                    JdbcParametro jdbcMail = (JdbcParametro) hmj.get("MAIL");
                    if (jdbcMail != null) {
                      String mail = jdbcMail.stringValue();
                      hmj.remove("MAIL");
                      hmj.put("DITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, mail));
                    }
                  }//listaImprese
                }

                String data = null;
                JdbcParametro jdbcDatrinuncia = (JdbcParametro) hmj.get("DATRINUNCIA");
                if(jdbcDatrinuncia.getValue() != null) {
                  data = jdbcDatrinuncia.getStringValue();
                  hmj.remove("ORA");
                  hmj.put("DATA", new JdbcParametro(JdbcParametro.TIPO_TESTO, data));
                }
                else {
                  JdbcParametro jdbcData = (JdbcParametro) hmj.get("DATA");
                  JdbcParametro jdbcOra = (JdbcParametro) hmj.get("ORA");
                  if (jdbcData.getValue() != null) {
                    data = jdbcData.stringValue();
                    if (jdbcOra.getValue() != null) {
                      String ora = jdbcOra.stringValue();
                      data = data.substring(0, 11) + ora;
                      if(ora.length()==5)
                        data += ":00";
                    }

                    hmj.remove("ORA");
                    hmj.put("DATA", new JdbcParametro(JdbcParametro.TIPO_TESTO, data));
                  }
                }

                //inserisco gli attributi per wsallegati e per gardoc_wsdm
                String idprg = null;
                Long iddocdig = null;
                String numero_protocollo = null;
                JdbcParametro jdbcIdprg = (JdbcParametro) hmj.get("IDPRG");
                if (jdbcIdprg != null){
                  idprg = jdbcIdprg.stringValue();
                }

                JdbcParametro jdbcIddocdig = (JdbcParametro) hmj.get("IDDOCDIG");
                if (jdbcIddocdig != null){
                  iddocdig = jdbcIddocdig.longValue();
                }

                JdbcParametro jdbcNumeroProtocollo= (JdbcParametro) hmj.get("NUMEROPROTOCOLLO");
                if (jdbcNumeroProtocollo != null){
                  numero_protocollo = jdbcNumeroProtocollo.stringValue();
                }

                Object[] datiComuni = getDatiComuniArchiviazione("W_DOCDIG",idprg,iddocdig.toString());
                if(numero_protocollo!=null)
                  datiComuni[1] = 3L;
                hmj.put("IDWSDOC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[0]));
                hmj.put("STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[1]));
                hmj.put("STATO_COS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[2]));
                hmj.put("ESITO", new JdbcParametro(JdbcParametro.TIPO_TESTO, datiComuni[3]));
                hmj.put("PROVENIENZA", new JdbcParametro(JdbcParametro.TIPO_TESTO, "8"));
                hmj.put("NUM_PROT", new JdbcParametro(JdbcParametro.TIPO_TESTO, numero_protocollo));

                hmDocRiepilogoBuste.set(j, hmj);

                hmDocGara.add(hmDocRiepilogoBuste.get(j));
              }//for
            }
          //documenti presentati dalle ditte
          List<HashMap> hmDocDitta = sqlManager.getListHashMap(queryDocDitta, parametri);
          if(hmDocDitta.size() >0 ){
            String selecStatoComunicazione = "select i.comstato from w_invcom i,w_docdig d where d.idprg=? and d.digkey3=? and i.idprg=d.idprg and CAST(i.idcom as VARCHAR(12))=d.digkey1";
            for(int j=0; j<hmDocDitta.size();j++) {
              HashMap hmj = hmDocDitta.get(j);

              String idprg = null;
              Long iddocdig = null;
              JdbcParametro jdbcIdprg = (JdbcParametro) hmj.get("IDPRG");
              if (jdbcIdprg != null){
                idprg = jdbcIdprg.stringValue();
              }

              JdbcParametro jdbcIddocdig = (JdbcParametro) hmj.get("IDDOCDIG");
              if (jdbcIddocdig != null){
                iddocdig = jdbcIddocdig.longValue();
              }

              JdbcParametro jdbcDoctel = (JdbcParametro) hmj.get("DOCTEL");
              JdbcParametro jdbcUuid = (JdbcParametro) hmj.get("UUID");
              if(jdbcDoctel!=null && jdbcUuid != null) {
                String doctel = jdbcDoctel.stringValue();
                String uuid = jdbcUuid.stringValue();
                if("1".equals(doctel) && uuid!=null && !"".equals(uuid)) {
                  String comstato = (String)this.sqlManager.getObject(selecStatoComunicazione, new Object[]{idprg,uuid});
                  if("13".equals(comstato) || "20".equals(comstato))
                    continue;
                }
              }

              JdbcParametro jdbcLotto = (JdbcParametro) hmj.get("LOTTO");
              if (jdbcLotto != null) {
                String lotto = jdbcLotto.stringValue();
                String codiga = (String)this.sqlManager.getObject("select codiga from gare where ngara = ? ", new Object[]{lotto});
                hmj.put("LOTTO", new JdbcParametro(JdbcParametro.TIPO_TESTO, codiga));
              }

              JdbcParametro jdbcDitta = (JdbcParametro) hmj.get("DITTA");
              if (jdbcDitta != null) {
                String codimp = jdbcDitta.stringValue();
                //verifico se la ditta è un raggruppamento
                String getImpr = "select codimp, tipimp, cfimp, nomest from impr where codimp = ?";
                List<?> listaImpresa = this.sqlManager.getListVector(getImpr, new Object[]{codimp});
                if (listaImpresa != null && listaImpresa.size()>0) {
                    String cod = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 0).getStringValue();
                    Long tipimp = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 1).longValue();
                    String nomeImpresa = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).getStringValue();
                    if (tipimp != null && (new Long(3).equals(tipimp) || new Long(10).equals(tipimp))) {
                        //la ditta è un raggruppamento allora prendo i dati della mandataria
                        cod = (String)this.sqlManager.getObject("select coddic from ragimp where codime9 = ? and impman='1'", new Object[]{cod});
                        listaImpresa = this.sqlManager.getListVector(getImpr, new Object[]{cod});
                        if (listaImpresa != null && listaImpresa.size()>0) {
                            nomeImpresa += "(mandataria " + SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).getStringValue() + ")";
                        }
                    }
                    hmj.put("DITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeImpresa));
                    hmj.put("CFDITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, SqlManager.getValueFromVectorParam(listaImpresa.get(0), 2).getStringValue()));
                    //hmDocDitta.set(j, hmj);
                }
              }

              JdbcParametro jdbcData = (JdbcParametro) hmj.get("DATA");
              JdbcParametro jdbcOra = (JdbcParametro) hmj.get("ORA");
              if (jdbcData.getValue() != null) {
                String data = jdbcData.stringValue();
                if (jdbcOra.getValue() != null) {
                  String ora = jdbcOra.stringValue();
                  data = data.substring(0, 11) + ora;
                  if(ora.length()==5)
                    data += ":00";
                }

                hmj.remove("ORA");
                hmj.put("DATA", new JdbcParametro(JdbcParametro.TIPO_TESTO, data));
              }

                //inserisco gli attributi per wsallegati e per gardoc_wsdm
                Object[] datiComuni = getDatiComuniArchiviazione("W_DOCDIG",idprg,iddocdig.toString());
                hmj.put("IDWSDOC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[0]));
                hmj.put("STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[1]));
                hmj.put("STATO_COS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[2]));
                hmj.put("ESITO", new JdbcParametro(JdbcParametro.TIPO_TESTO, datiComuni[3]));
                hmj.put("PROVENIENZA", new JdbcParametro(JdbcParametro.TIPO_TESTO, "3"));
                hmj.put("NUM_PROT", new JdbcParametro(JdbcParametro.TIPO_TESTO, null));

                hmDocDitta.set(j, hmj);

              hmDocGara.add(hmDocDitta.get(j));


            }//for
          }//hmDocDitta
          }

          List<HashMap> hmDocComAllaDitta = null;
          if("true".equals(stipula)) {
            hmDocComAllaDitta = sqlManager.getListHashMap(queryDocComAllaDittaStipula, new Object[] { idStipula });
          } else if(skipDocumentiDitte) {
            // non estraggo i documenti delle ditte
          }else {
            Object [] parametri = new Object[] {codgar};
            if (StringUtils.isNotEmpty(dittaoFiltro)){
              parametri = new Object[] {codgar, dittaoFiltro};
            }
            hmDocComAllaDitta = sqlManager.getListHashMap(queryDocComAllaDitta, parametri);
          }

          if(hmDocComAllaDitta != null && hmDocComAllaDitta.size() >0 ){
            for(int j=0; j<hmDocComAllaDitta.size();j++) {
              HashMap hmj = hmDocComAllaDitta.get(j);

              JdbcParametro jdbcLotto = (JdbcParametro) hmj.get("LOTTO");
              if (jdbcLotto != null) {
                String lotto = jdbcLotto.stringValue();
                String codiga = (String)this.sqlManager.getObject("select codiga from gare where ngara = ? ", new Object[]{lotto});
                hmj.put("LOTTO", new JdbcParametro(JdbcParametro.TIPO_TESTO, codiga));
              }


              JdbcParametro jdbcDitta = (JdbcParametro) hmj.get("DITTA");

              if (jdbcDitta != null) {
                String codimp = jdbcDitta.stringValue();
                JdbcParametro jdbcDescodent = (JdbcParametro) hmj.get("DESCODENT");
                String entita=jdbcDescodent.stringValue();

                //verifico se la ditta è un raggruppamento
                String getDatiDest = "select codimp, cfimp, nomest, tipimp from impr where codimp = ?";
                if("TECNI".equals(entita))
                  getDatiDest = "select codtec, cftec, nomtec from tecni where codtec = ?";
                List<?> listaImpresa = this.sqlManager.getListVector(getDatiDest, new Object[]{codimp});
                if (listaImpresa != null && listaImpresa.size()>0) {
                    String cod = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 0).getStringValue();
                    Long tipimp = null;
                    if("IMPR".equals(entita))
                      tipimp = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).longValue();
                    String nomeImpresa = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 2).getStringValue();
                    if (tipimp != null && (new Long(3).equals(tipimp) || new Long(10).equals(tipimp))) {
                        //la ditta è un raggruppamento allora prendo i dati della mandataria
                        cod = (String)this.sqlManager.getObject("select coddic from ragimp where codime9 = ? and impman='1'", new Object[]{cod});
                        listaImpresa = this.sqlManager.getListVector(getDatiDest, new Object[]{cod});
                        if (listaImpresa != null && listaImpresa.size()>0) {
                            nomeImpresa += "(mandataria " + SqlManager.getValueFromVectorParam(listaImpresa.get(0), 2).getStringValue() + ")";
                        }
                    }
                    nomeImpresa = UtilityStringhe.convertiNullInStringaVuota(nomeImpresa);
                    if("".equals(nomeImpresa)){
                      JdbcParametro jdbcIdcom = (JdbcParametro) hmj.get("IDCOM");
                      if (jdbcIdcom != null) {
                        Long idcom = jdbcIdcom.longValue();
                        String email = (String) this.sqlManager.getObject("select desmail from w_invcomdes where idcom=? and descodent is null and descodsog is null", new Object[]{idcom});
                        hmj.remove("IDCOM");
                        hmj.put("DITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, email));
                      }
                    }else{
                      hmj.put("DITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeImpresa));
                      hmj.put("CFDITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, SqlManager.getValueFromVectorParam(listaImpresa.get(0), 1).getStringValue()));
                    }
                }else{
                  JdbcParametro jdbcIdcom = (JdbcParametro) hmj.get("IDCOM");
                  if (jdbcIdcom != null) {
                    Long idcom = jdbcIdcom.longValue();
                    String email = (String) this.sqlManager.getObject("select desmail from w_invcomdes where idcom=? and descodent is null and descodsog is null", new Object[]{idcom});
                    hmj.remove("IDCOM");
                    hmj.put("DITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, email));
                  }
                }//listaImprese
              }

              //inserisco gli attributi per wsallegati e per gardoc_wsdm
              String idprg = null;
              Long iddocdig = null;
              String numero_protocollo = null;
              JdbcParametro jdbcIdprg = (JdbcParametro) hmj.get("IDPRG");
              if (jdbcIdprg != null){
                idprg = jdbcIdprg.stringValue();
              }

              JdbcParametro jdbcIddocdig = (JdbcParametro) hmj.get("IDDOCDIG");
              if (jdbcIddocdig != null){
                iddocdig = jdbcIddocdig.longValue();
              }

              JdbcParametro jdbcNumeroProtocollo= (JdbcParametro) hmj.get("NUMEROPROTOCOLLO");
              if (jdbcNumeroProtocollo != null)
                numero_protocollo = jdbcNumeroProtocollo.stringValue();

              Object[] datiComuni = getDatiComuniArchiviazione("W_DOCDIG",idprg,iddocdig.toString());
              hmj.put("IDWSDOC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[0]));
              hmj.put("STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[1]));
              hmj.put("STATO_COS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[2]));
              hmj.put("ESITO", new JdbcParametro(JdbcParametro.TIPO_TESTO, datiComuni[3]));
              hmj.put("PROVENIENZA", new JdbcParametro(JdbcParametro.TIPO_TESTO, "5"));
              hmj.put("NUM_PROT", new JdbcParametro(JdbcParametro.TIPO_TESTO, numero_protocollo));

              hmDocComAllaDitta.set(j, hmj);

              hmDocGara.add(hmDocComAllaDitta.get(j));
            }//for
          }//ditta

          //Documenti inviati dalla ditta

          List<HashMap> hmDocComDallaDitta = null;
          if("true".equals(stipula)) {
            hmDocComDallaDitta = sqlManager.getListHashMap(queryDocComDallaDittaStipula, new Object[] { idStipula });
          } else if(skipDocumentiDitte) {
            // non estraggo i documenti delle ditte
          }else {
            Object [] parametri = new Object[] {codgar};
            if (StringUtils.isNotEmpty(dittaoFiltro)){
              parametri = new Object[] {codgar, dittaoFiltro};
            }
            hmDocComDallaDitta = sqlManager.getListHashMap(queryDocComDallaDitta, parametri);
          }

          if(hmDocComDallaDitta != null && hmDocComDallaDitta.size() >0 ){
            for(int j=0; j<hmDocComDallaDitta.size();j++) {
              HashMap hmj = hmDocComDallaDitta.get(j);

              JdbcParametro jdbcLotto = (JdbcParametro) hmj.get("LOTTO");
              if (jdbcLotto != null) {
                String lotto = jdbcLotto.stringValue();
                String codiga = (String)this.sqlManager.getObject("select codiga from gare where ngara = ? ", new Object[]{lotto});
                hmj.put("LOTTO", new JdbcParametro(JdbcParametro.TIPO_TESTO, codiga));
              }

              JdbcParametro jdbcDitta = (JdbcParametro) hmj.get("DITTA");
              if (jdbcDitta != null) {
                String codimp = jdbcDitta.stringValue();

                String codPUser = (String)this.sqlManager.getObject("select userkey1 from w_puser where usernome = ? and userent = 'IMPR'", new Object[]{codimp});
                if (codPUser != null) {
                    codimp = codPUser;
                }

                //verifico se la ditta è un raggruppamento
                String getImpr = "select codimp, tipimp, cfimp, nomest from impr where codimp = ?";
                List<?> listaImpresa = this.sqlManager.getListVector(getImpr, new Object[]{codimp});
                if (listaImpresa != null && listaImpresa.size()>0) {
                    String cod = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 0).getStringValue();
                    Long tipimp = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 1).longValue();
                    String nomeImpresa = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).getStringValue();
                    if (tipimp != null && (new Long(3).equals(tipimp) || new Long(10).equals(tipimp))) {
                        //la ditta è un raggruppamento allora prendo i dati della mandataria
                        cod = (String)this.sqlManager.getObject("select coddic from ragimp where codime9 = ? and impman='1'", new Object[]{cod});
                        listaImpresa = this.sqlManager.getListVector(getImpr, new Object[]{cod});
                        if (listaImpresa != null && listaImpresa.size()>0) {
                            nomeImpresa += "(mandataria " + SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).getStringValue() + ")";
                        }
                    }
                    hmj.put("DITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeImpresa));
                    hmj.put("CFDITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, SqlManager.getValueFromVectorParam(listaImpresa.get(0), 2).getStringValue()));
                }else{
                  JdbcParametro jdbcMail = (JdbcParametro) hmj.get("MAIL");
                  if (jdbcMail != null) {
                    String mail = jdbcMail.stringValue();
                    hmj.remove("MAIL");
                    hmj.put("DITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, mail));
                  }
                }//listaImprese
              }

              //inserisco gli attributi per wsallegati e per gardoc_wsdm
              String idprg = null;
              Long iddocdig = null;
              String numero_protocollo = null;
              JdbcParametro jdbcIdprg = (JdbcParametro) hmj.get("IDPRG");
              if (jdbcIdprg != null){
                idprg = jdbcIdprg.stringValue();
              }

              JdbcParametro jdbcIddocdig = (JdbcParametro) hmj.get("IDDOCDIG");
              if (jdbcIddocdig != null){
                iddocdig = jdbcIddocdig.longValue();
              }

              JdbcParametro jdbcNumeroProtocollo= (JdbcParametro) hmj.get("NUMEROPROTOCOLLO");
              if (jdbcNumeroProtocollo != null)
                numero_protocollo = jdbcNumeroProtocollo.stringValue();

              Object[] datiComuni = getDatiComuniArchiviazione("W_DOCDIG",idprg,iddocdig.toString());
              hmj.put("IDWSDOC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[0]));
              hmj.put("STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[1]));
              hmj.put("STATO_COS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[2]));
              hmj.put("ESITO", new JdbcParametro(JdbcParametro.TIPO_TESTO, datiComuni[3]));
              hmj.put("PROVENIENZA", new JdbcParametro(JdbcParametro.TIPO_TESTO, "6"));
              hmj.put("NUM_PROT", new JdbcParametro(JdbcParametro.TIPO_TESTO, numero_protocollo));

              hmDocComDallaDitta.set(j, hmj);

              hmDocGara.add(hmDocComDallaDitta.get(j));
            }//for


          }

        if(!"true".equals(stipula) && StringUtils.isEmpty(dittaoFiltro) && !skipDocumentiDitte) {
          List<HashMap> hmDocPubAtti = sqlManager.getListHashMap(queryDocPubAttiOfferte, new Object[]{codgar});
          if (hmDocPubAtti.size() > 0) {
            for (int j = 0; j < hmDocPubAtti.size(); j++) {
              HashMap hmj = hmDocPubAtti.get(j);

              JdbcParametro jdbcDitta = (JdbcParametro) hmj.get("DITTA");
              if (jdbcDitta != null) {
                String codimp = jdbcDitta.stringValue();

                String codPUser = (String) this.sqlManager.getObject("select userkey1 from w_puser where usernome = ? and userent = 'IMPR'", new Object[]{codimp});
                if (codPUser != null) {
                  codimp = codPUser;
                }

                // verifico se la ditta ï¿½ un raggruppamento
                String getImpr = "select codimp, tipimp, cfimp, nomest from impr where codimp = ?";
                List<?> listaImpresa = this.sqlManager.getListVector(getImpr, new Object[]{codimp});
                if (listaImpresa != null && listaImpresa.size() > 0) {
                  String cod = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 0).getStringValue();
                  Long tipimp = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 1).longValue();
                  String nomeImpresa = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3)
                          .getStringValue();
                  if (tipimp != null && (new Long(3).equals(tipimp) || new Long(10).equals(tipimp))) {
                    // la ditta ï¿½ un raggruppamento allora prendo i dati della mandataria
                    cod = (String) this.sqlManager.getObject("select coddic from ragimp where codime9 = ? and impman='1'", new Object[]{cod});
                    listaImpresa = this.sqlManager.getListVector(getImpr, new Object[]{cod});
                    if (listaImpresa != null && listaImpresa.size() > 0) {
                      nomeImpresa += "(mandataria " + SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).getStringValue() + ")";
                    }
                  }
                  hmj.put("DITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeImpresa));
                  hmj.put("CFDITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, SqlManager.getValueFromVectorParam(listaImpresa.get(0), 2).getStringValue()));
                } else {
                  JdbcParametro jdbcMail = (JdbcParametro) hmj.get("MAIL");
                  if (jdbcMail != null) {
                    String mail = jdbcMail.stringValue();
                    hmj.remove("MAIL");
                    hmj.put("DITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, mail));
                  }
                } // listaImprese
              }

              JdbcParametro jdbcLotto = (JdbcParametro) hmj.get("LOTTO");
              if (jdbcLotto != null) {
                String lotto = jdbcLotto.stringValue();
                String codiga = (String) this.sqlManager.getObject("select codiga from gare where ngara = ? ", new Object[]{lotto});
                hmj.put("LOTTO", new JdbcParametro(JdbcParametro.TIPO_TESTO, codiga));
              }

              // inserisco gli attributi per wsallegati e per gardoc_wsdm
              String idprg = null;
              Long iddocdig = null;
              String numero_protocollo = null;
              JdbcParametro jdbcIdprg = (JdbcParametro) hmj.get("IDPRG");
              if (jdbcIdprg != null) {
                idprg = jdbcIdprg.stringValue();
              }

              JdbcParametro jdbcIddocdig = (JdbcParametro) hmj.get("IDDOCDIG");
              if (jdbcIddocdig != null) {
                iddocdig = jdbcIddocdig.longValue();
              }

              JdbcParametro jdbcNumeroProtocollo= (JdbcParametro) hmj.get("NUMEROPROTOCOLLO");
              if (jdbcNumeroProtocollo != null)
                numero_protocollo = jdbcNumeroProtocollo.stringValue();

              Object[] datiComuni = getDatiComuniArchiviazione("W_DOCDIG",idprg,iddocdig.toString());
              hmj.put("IDWSDOC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[0]));
              hmj.put("STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[1]));
              hmj.put("STATO_COS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, datiComuni[2]));
              hmj.put("ESITO", new JdbcParametro(JdbcParametro.TIPO_TESTO, datiComuni[3]));
              hmj.put("PROVENIENZA", new JdbcParametro(JdbcParametro.TIPO_TESTO, "9"));
              hmj.put("NUM_PROT", new JdbcParametro(JdbcParametro.TIPO_TESTO, numero_protocollo));

              hmDocPubAtti.set(j, hmj);

              hmDocGara.add(hmDocPubAtti.get(j));
            } // for
          }
        }


          if (hmDocGara != null && hmDocGara.size() > 0) {
            total = hmDocGara.size();
            totalAfterFilter = hmDocGara.size();
          }
          String uffint = null;
          if(!"true".equals(stipula)) {
            uffint = (String) sqlManager.getObject("select cenint from torn where codgar=?", new Object[]{codgar});
          }

          ArrayList<String> formatiCOS = new ArrayList<String>();
          ArrayList<String> formatiCOSFirmaDigitale = new ArrayList<String>();
          String _formati = getOptConfigurazionePerUfficioIntestatario("conservazione.formatiConsentiti",uffint);
          if(StringUtils.isBlank(_formati))
            _formati=ConfigManager.getValore("uploadFile.estensioniAmmesse").replace(";",",").toLowerCase();
          if (_formati != null) {
            formatiCOS = new ArrayList<String>(Arrays.asList(_formati.split(",")));
          }
          String _formatiFirmaDigitale = getOptConfigurazionePerUfficioIntestatario("conservazione.formatiConsentitiFirmaDigitale",uffint);
          if (_formatiFirmaDigitale != null) {
            formatiCOSFirmaDigitale = new ArrayList<String>(Arrays.asList(_formatiFirmaDigitale.split(",")));
          }
          for (int o = 0; o < hmDocGara.size(); o++) {
            HashMap hm = hmDocGara.get(o);
            Long stato = null;
            JdbcParametro parametrodata = (JdbcParametro) hm.get("DATA");
            if (parametrodata.getValue() == null) {
              stato = new Long(-1);
            }

            String nomeFile = ((JdbcParametro) hm.get("DIGNOMDOC")).stringValue();
            Boolean fine = false;
            while (true)
            {
                if (nomeFile != null) {
                    if (nomeFile.indexOf(".") >= 0) {
                        String estensione = nomeFile.substring(nomeFile.lastIndexOf(".")).toLowerCase().replace(".","");
                        if (!formatiCOS.contains(estensione) && formatiCOSFirmaDigitale.contains(estensione)) {
                          //estensione per firma digitale,la ignoro e vado avanti
                          nomeFile = nomeFile.substring(0,nomeFile.lastIndexOf("."));
                        }
                        else
                        {
                            //qui mi aspetto l'estensione originale
                            if (!formatiCOS.contains(estensione))
                            {
                                stato = new Long(-2);
                            }
                            break;
                        }
                    } else break;
                } else break;
            }
            // se non esiste la data o l'estensione non e' ammessa, override stato
            if (stato != null) {
              hm.put("STATO_COS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, stato));
            }
            //APP-1330
            if("".equals(hm.get("IDWSDOC").toString()) && ("".equals(hm.get("STATO").toString()) || "2".equals(hm.get("STATO").toString()) || "23".equals(hm.get("STATO").toString())))
              documentiDaArchiviareDocumentale++;
            if( "".equals(hm.get("STATO_COS").toString()) || "2".equals(hm.get("STATO_COS").toString()) || "7".equals(hm.get("STATO_COS").toString()) || "8".equals(hm.get("STATO_COS").toString()))
              documentiDaArchiviareCOS++;
            if("-1".equals(hm.get("STATO_COS").toString()) || "-2".equals(hm.get("STATO_COS").toString()))
              documentiNonArchiviabiliCOS++;
          }

          result.put("iTotalRecords", total);
          result.put("iTotalDisplayRecords", totalAfterFilter);
          result.put("data", hmDocGara);
          result.put("documentiDaArchiviareDocumentale", documentiDaArchiviareDocumentale);
          result.put("documentiDaArchiviareCOS", documentiDaArchiviareCOS);
          result.put("documentiNonArchiviabiliCOS", documentiNonArchiviabiliCOS);

    } catch (SQLException e) {
      logger.error(e);
      throw e;
    } catch (GestoreException e) {
      logger.error(e);
      throw e;
    }

    return result;
  }

  public String getSistemaConservazione(String codiceGara) throws SQLException {
    String uffint = (String) this.sqlManager.getObject("select cenint from torn where codgar=?", new Object[]{codiceGara});
    String configVal = ConfigurazionePerUffintUtils.getOptConfigurazionePerUfficioIntestatario("conservazione.modalita",uffint);
    return org.apache.commons.lang3.StringUtils.isNotBlank(configVal) ? configVal : "0";
  }

  /**
   * Restituisce 0: idwsdoc; 1:stato archiviazione wsdm; 2: stato archiviazione cos; 3: esito
   * @param entita
   * @param idprg
   * @param iddocdig
   * @return
   * @throws SQLException
   */
  private Object[] getDatiComuniArchiviazione(String entita, String idprg, String iddocdig) throws SQLException {
    Object[] result = new Object[4];

    Vector<?> datiAllegati = this.sqlManager.getVector("select ws.idwsdoc from wsallegati ws  where ws.key1 = ? and ws.key2 = ? and ws.entita=?", new Object[]{idprg,iddocdig,entita});
    if (datiAllegati != null && datiAllegati.size()>0) {
      result[0] = SqlManager.getValueFromVectorParam(datiAllegati, 0).getValue();
    }

    String selectGARDOC_WSDM = null;
    selectGARDOC_WSDM = "select gwsdm1.stato_archiviazione,gwsdm1.esito from gardoc_wsdm gwsdm1 join gardoc_jobs job on gwsdm1.id_archiviazione = job.id_archiviazione where job.tipo_archiviazione = 2 and gwsdm1.entita=? and gwsdm1.key1 = ? and gwsdm1.key2 = ? order by gwsdm1.id_archiviazione desc";
    List<?> listaDatiGardocWsdm = this.sqlManager.getListVector(selectGARDOC_WSDM, new Object[]{entita,idprg,iddocdig});

    if (listaDatiGardocWsdm != null && !listaDatiGardocWsdm.isEmpty()) {
      result[1] = SqlManager.getValueFromVectorParam(listaDatiGardocWsdm.get(0), 0).getValue();
      result[3] = SqlManager.getValueFromVectorParam(listaDatiGardocWsdm.get(0), 1).getStringValue();
    }
    String selectGARDOC_COS = "select gwsdm1.stato_archiviazione,gwsdm1.esito from gardoc_wsdm gwsdm1 join gardoc_jobs job on gwsdm1.id_archiviazione = job.id_archiviazione where job.tipo_archiviazione in (3,6) and gwsdm1.entita=? and gwsdm1.key1 = ? and gwsdm1.key2 = ? order by gwsdm1.id_archiviazione desc";
    List<?> listaDatiGardocCOS = this.sqlManager.getListVector(selectGARDOC_COS,
            new Object[] {entita, idprg, iddocdig});

    if (listaDatiGardocCOS != null && !listaDatiGardocCOS.isEmpty()) {
      result[2] = SqlManager.getValueFromVectorParam(listaDatiGardocCOS.get(0), 0).getValue();
      result[3] = SqlManager.getValueFromVectorParam(listaDatiGardocCOS.get(0), 1).getStringValue();
    }
    return result;
  }

}

