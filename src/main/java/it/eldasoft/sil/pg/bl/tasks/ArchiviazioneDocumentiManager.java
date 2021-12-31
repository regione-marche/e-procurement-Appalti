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
import it.maggioli.eldasoft.ws.conf.WSDMTabellatoElementoType;
import it.maggioli.eldasoft.ws.conf.WSDMTabellatoType;
import it.maggioli.eldasoft.ws.dm.WSDMAggiungiAllegatiResType;
import it.maggioli.eldasoft.ws.dm.WSDMDocumentoCollegaResType;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMInserimentoInFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoInType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloInOutType;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import net.schmizz.sshj.xfer.FileSystemFile;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

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

      String RUP = null;
      String nomeRup = null;
      String acronimoRup = "";

      String voce = null;

      String inout = null;
      Long gruppo = null;
      String tipologiaDesc = null;
      String codimp = null;

      String oggetto = "gara";
      if (genere.equals(new Long(10))) {
          oggetto = "elenco operatori";
      } else if (genere.equals(new Long(20))) {
          oggetto = "catalogo elettronico";
      }

      String entitaAllegato = null;
      String entitaFascicolo = null;

      if (genere.equals(new Long(1))) {
        entitaFascicolo = "TORN";
      }else{
        entitaFascicolo = "GARE";
      }

      String doctel = null;

      try {

          if (!"".equals(key1Doc) && !"".equals(key2Doc)) {
            WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = new WSDMProtocolloDocumentoInType();
            // Dati generali dell'elemento documentale
            wsdmProtocolloDocumentoIn.setClassifica(classificadocumento);
            wsdmProtocolloDocumentoIn.setTipoDocumento(tipodocumento);
            /*
             * 1 = Doc Gara
             * 2 = Doc Comunicazioni pubbliche
             * 3 = Doc Ditta
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
                  idDocumentale = "DOCUMGARA|";
                  selDatiSpecifici = "select d.gruppo, d.descrizione, " +
                        "d.codgar, d.ngara, d.norddocg" +
                        " from documgara d join w_docdig w on d.iddocdg=w.iddocdig and d.idprg=w.idprg and digent='DOCUMGARA'" +
                        " and w.idprg = ? and w.iddocdig = ? ";
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
                  " and w.digkey1 = i.idprg and w.digent='W_INVCOM' " +
                  " and w.idprg = ? and w.iddocdig = ? ";
                  break;
                case 6:
                  inout = "INT";
                  idDocumentale = "W_INVCOM|";
                  selDatiSpecifici = "select cast('Documento inviato dalla ditta' as varchar(100)) as tipologia," +
                  this.sqlManager.getDBFunction("concat",  new String[] {this.sqlManager.getDBFunction("concat",  new String[] {"i.commsgogg" , "' - '" }) , "w.DIGDESDOC" }) + ", " +
                  " i.idprg, i.idcom " +
                  " from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + " = w.digkey1" +
                  " and i.idprg = w.idprg and w.digent='W_INVCOM' " +
                  " and w.idprg = ? and w.iddocdig = ? ";
                  break;
                case 7:
                  inout = "INT";
                  idDocumentale = "C0OGGASS|";
                  selDatiSpecifici = "select  cast('Documento associato' as varchar(100)) as GRUPPO, c.c0atit as DESCRIZIONE, c.c0acod from c0oggass c" +
                            " join w_docdig w on c.c0acod = w.digkey1 and c.c0aprg = w.idprg and w.digent='C0OGGASS' and w.idprg = ? and w.iddocdig = ? ";
                  break;

              }
            }
            if(!"".equals(selDatiSpecifici)){
              Vector<?> datiDoc = this.sqlManager.getVector(selDatiSpecifici, new Object[]{key1Doc,new Long(key2Doc)});
              if(datiDoc != null){
                if("1".equals(provenienza)){
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
                    case 10: tipologiaDesc = "Atto o documento art.29 c.1 DLgs.50/2016"; break;
                    case 11: tipologiaDesc = "Documento allegato all'ordine di acquisto"; break;
                    case 12: tipologiaDesc = "Documento dell'invito all'asta elettronica"; break;

                    default:
                      tipologiaDesc = "Documentazione " + oggetto; break;
                    }
                  }else{
                    tipologiaDesc= "Documentazione " + oggetto;
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


                String tipoWSDM =null;
                WSDMConfigurazioneOutType configurazione = this.gestioneWSDMManager.wsdmConfigurazioneLeggi("DOCUMENTALE",idconfi);
                if(configurazione.isEsito())
                  tipoWSDM = configurazione.getRemotewsdm();

                String cig = (String)sqlManager.getObject("select codcig from v_gare_torn where codgar=?", new Object[]{codgara});

                String oggettoGara = this.pgManagerEst1.getOggettoGara(codice, codgara, genere);
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
                if("PRISMA".equals(tipoWSDM) || "INFOR".equals(tipoWSDM)){
                  unitaOperativaMittente = idunitaoperativadestinataria;
                }

                if("ARCHIFLOWFA".equals(tipoWSDM) || "PRISMA".equals(tipoWSDM))
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


                if("JDOC".equals(tipoWSDM)){
                  String select = "select nometei,cogtei from torn, tecni where codgar = ? and codrup=codtec";
                  Vector datiRup =  sqlManager.getVector(select, new Object[]{codgara});
                  if(datiRup!=null && datiRup.size()>0){
                    String nomeR = SqlManager.getValueFromVectorParam(datiRup, 0).getStringValue();
                    String cognomeR = SqlManager.getValueFromVectorParam(datiRup, 1).getStringValue();
                    if(nomeR==null)
                      nomeR="";
                    if(cognomeR==null)
                      cognomeR="";
                   RUP = nomeR + " " + cognomeR;
                   nomeRup = RUP;
                   if(nomeR.length()>0)
                     acronimoRup +=nomeR.substring(0, 1);
                   if(cognomeR.length()>0)
                     acronimoRup +=cognomeR.substring(0, 1);
                  }
                  wsdmProtocolloDocumentoIn.setGenericS11(sottotipo);
                  wsdmProtocolloDocumentoIn.setGenericS12(RUP);

                }

                //CALCOLO qui se la fascicolazione risulta abilitata
                String valAbilitazioneFascicolazione = ConfigManager.getValore("pg.wsdm.applicaFascicolazione."+idconfi);
                if("1".equals(valAbilitazioneFascicolazione)){
                  if("ENGINEERINGDOC".equals(tipoWSDM)){
                    //Si deve caricare il codice del fascicolo dal file di configurazione dei tabellati
                    if (configurazione.getTabellati() != null) {
                      WSDMTabellatoType[] wsdmTabellati = configurazione.getTabellati();
                      if (wsdmTabellati != null && wsdmTabellati.length > 0) {
                        for (int t = 0; t < wsdmTabellati.length; t++) {
                          if ("idfolder".equals(wsdmTabellati[t].getNome())) {
                            WSDMTabellatoElementoType[] elementi = wsdmTabellati[t].getElementi();
                            if (elementi != null && elementi.length > 0) {
                              for (int e = 0; e < elementi.length; e++) {
                                codicefascicolo = elementi[e].getCodice();
                                inserimentoinfascicolo = "SI_FASCICOLO_ESISTENTE";
                                break;
                              }
                            }
                            break;
                          }
                        }
                      }
                    }
                  }else{
                    //ocorre recuperare sia il codice che il numero del fascicolo,in quanto abbiamo il caso di ENGINEERING
                    //che non mi restituisce in creazione il codice ma il numero
                    String selectFascicolo = "select wsf.codice,wsf.numero,wsf.anno, wsf.codaoo, wsf.coduff, wsf.classifica,wsf.desclassi,wsf.desvoce from wsfascicolo wsf ,v_gare_genere v" +
                    " where wsf.key1= v.codice and  wsf.entita = ? and v.codgar = ?";
                    Vector fascicolo = sqlManager.getVector(selectFascicolo, new Object[] { entitaFascicolo, codgara});
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
                  if("ARCHIFLOWFA".equals(tipoWSDM) || "INFOR".equals(tipoWSDM)){
                    wsdmFascicolo.setClassificaFascicolo(classificafascicolo);
                  }
                  if("JIRIDE".equals(tipoWSDM) || "PRISMA".equals(tipoWSDM)){
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

                //Valorizzazione dei nuovi campi: societa,codicegaralotto,cig e destinatarioprincipale
                String cenint=(String)sqlManager.getObject("select cenint from torn where codgar=?",new Object[]{codgara});
                wsdmProtocolloDocumentoIn.setSocieta(cenint);
                wsdmProtocolloDocumentoIn.setDestinatarioPrincipale(cenint);
                if (genere.equals(new Long(1)))
                  wsdmProtocolloDocumentoIn.setCodiceGaraLotto(codgara);
                else
                  wsdmProtocolloDocumentoIn.setCodiceGaraLotto(codice);

                wsdmProtocolloDocumentoIn.setCig(cig);
                wsdmProtocolloDocumentoIn.setMezzo(mezzo);

                wsdmProtocolloDocumentoIn.setClassificaDescrizione(classificaDescrizione);
                wsdmProtocolloDocumentoIn.setVoce(voce);

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

                  if("JIRIDE".equals(tipoWSDM) && "3".equals(provenienza) && "1".equals(doctel) && "2".equals(associaDocumentiProtocollo) && !genere.equals(new Long(10)) && !genere.equals(new Long(20))){
                    Long busta = SqlManager.getValueFromVectorParam(datiDoc, 7).longValue();
                    codimp = SqlManager.getValueFromVectorParam(datiDoc, 2).getStringValue();
                    codimp = UtilityStringhe.convertiNullInStringaVuota(codimp);
                    WSDMAggiungiAllegatiResType aggiungiAllegatiResType = this.aggiungiAllegati(codice,codgara , codimp, busta,allegati, key1Doc, key2Doc, username, ruolo, idArchiviazione,idconfi);
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
                      if("JIRIDE".equals(tipoWSDM) && "3".equals(provenienza) && "1".equals(doctel) && "1".equals(associaDocumentiProtocollo) && !genere.equals(new Long(10)) && !genere.equals(new Long(20))){
                        Long busta = SqlManager.getValueFromVectorParam(datiDoc, 7).longValue();
                        codimp = SqlManager.getValueFromVectorParam(datiDoc, 2).getStringValue();
                        codimp = UtilityStringhe.convertiNullInStringaVuota(codimp);
                        WSDMDocumentoCollegaResType wdmDocumentoCollegaResType = this.collegaDocumenti(codice,codgara , codimp, tipoCollegamento, busta, numeroDocumento, null, null, username, ruolo,idconfi);
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
                      if("INFOR".equals(tipoWSDM) || "JPROTOCOL".equals(tipoWSDM) || "ITALPROT".equals(tipoWSDM)){
                        numProt = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroProtocollo();
                        annoProt = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getAnnoProtocollo();
                      }
                      Long idWSDocumento = this.gestioneWSDMManager.setWSDocumento(entitaFascicolo, codice, null, null, null, numeroDocumento, annoProt, numProt, oggettodocumento,inout);

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
  public int trasferisciAreaFTPCOS(HashMap<String, Object> doc)
    throws GestoreException, IOException, SQLException{

    String idprg = (String)doc.get(this.KEY1);
    String iddocdig =(String) doc.get(this.KEY2);
    Long idDoc = (Long)doc.get(this.IDDOC);
    Long idArchiviazione = (Long)doc.get(this.IDARCHIVIAZIONE);
    String entita = (String)doc.get(this.ENTITA);
    String provenienza = (String)doc.get(this.PROVENIENZA);

    try{
      if (logger.isDebugEnabled())
        logger.debug("trasferisciAreaFTPCOS: inizio metodo");

      SFTPManager sftp = new SFTPManager();
      String alias_sp = ConfigManager.getValore("cos.sftp.aliasSp");
      String alias_da = ConfigManager.getValore("cos.sftp.aliasDa");
      String pathBase = ConfigManager.getValore("cos.sftp.pathBase");
      if((alias_sp == null || "".equals(alias_sp)) || (alias_da == null || "".equals(alias_da)) || (pathBase == null || "".equals(pathBase))){
        throw new GestoreException("Errore nella configurazione dell'integrazione COS: aliasSp o aliasDa non valorizzati", null);
      }
      String base = pathBase + alias_sp + "/" + alias_da + "/Documenti/";
      if (logger.isDebugEnabled())
        logger.debug("trasferisciAreaFTPCOS: path = " + base);
      sftp.connect();
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
          String percorso = "";
          String consentiti = ",}.]{_[)(-";

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
          String formati = ConfigManager.getValore("cos.formatiConsentiti");
          String formatifirmadigitale = ConfigManager.getValore("cos.formatiConsentitiFirmaDigitale");
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
          List<String> files = sftp.GetFiles(base);
          for (int j = 0; j < files.size(); j++) {
            String jfile = files.get(j);
            if (jfile.contains(impronta)) {
              numerofile++;
            }
          }
          String newNomeFile = impronta + "-" + numerofile + estensioni;
          percorso = base + newNomeFile;
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

  public Long insertJobArchiviazioneDocumenti(Long syscon, String codgar, String tipoWSDM, String classifica, String codiceRegistro, String tipoDocumento, String mittInterno,
      String classificaTitolazione, String indice, String unitaOperativaMittente, String mezzo, String struttura, String supporto, Long tipo_archiviazione, String sottotipo) {
    Long idArchiviazione = null;
    try {
      idArchiviazione = new Long(genChiaviManager.getNextId("GARDOC_JOBS"));
        //aggiorno flag di esecuzione nella lista record da elaborare
        this.sqlManager.update("insert into gardoc_jobs(id_archiviazione, syscon, codgara, data_inserimento, da_processare, tipo_archiviazione, classifica, cod_reg," +
                " tipo_doc, mitt_int, classifica_tit, indice, uo_mittdest, mezzo, struttura, supporto,sottotipo)" +
                " values(?,?,?,?,'1',?,?,?,?,?,?,?,?,?,?,?,?)",
            new Object[]{idArchiviazione, syscon, codgar, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), tipo_archiviazione, classifica, codiceRegistro,
            tipoDocumento, mittInterno, classificaTitolazione, indice, unitaOperativaMittente,mezzo,struttura,supporto,sottotipo});

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
    if (new Long(10).equals(genere)){
      msgGenere = "l' elenco operatori ";
    }else if(new Long(20).equals(genere)){
      msgGenere = "il catalogo elettronico ";
    }else{
      msgGenere = "la gara ";
    }

    try {

      if(numDocEsitoNegativo >0){
        message = "La richiesta di archiviazione dei documenti per " + msgGenere + codice + " ha prodotto errori su " + numDocEsitoNegativo + " documenti." +
        "\r\n" + "Per consultarne il dettaglio accedere alla maschera di archiviazione documenti.";
      }else{
        message = "E' stata effettuata correttamente la richiesta di archiviazione dei documenti per " + msgGenere + codice;
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
        logger.error("archiviazioneDocumentiGara.insertJob : errore durante l'aggiornamento del job di archiviazione documenti per la gara " + codgar, e);
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
      String idprg, String iddocdig, String username, String ruolo, String idconfi) throws GestoreException{
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
        Vector datiDITG = this.sqlManager.getVector(selectDitg, new Object[]{chiaveGara, codDitta});
        if(datiDITG!=null && datiDITG.size()>0){
          String numProt = SqlManager.getValueFromVectorParam(datiDITG, 0).stringValue();
          Timestamp dataProt = SqlManager.getValueFromVectorParam(datiDITG, 1).dataValue();
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
      String idprg, String iddocdig, String username, String ruolo, Long idArchiviazione,String idconfi) throws GestoreException{
    WSDMAggiungiAllegatiResType  ret = null;

    String selectNumeroDocumento="select numerodoc from wsdocumento where numeroprot=? and annoprot = ? and inout=?";
    String selectDitg="select nprdom, dprdom from ditg where ngara5=? and dittao=?";
    if(!new Long(4).equals(busta))
      selectDitg="select nproff, dproff from ditg where ngara5=? and dittao=?";
    try {
      //Se numeroDoc è nullo, lo si deve ricavare
        String chiaveGara=ngara;
        Long genere = (Long)this.sqlManager.getObject("select genere from v_gare_genere where codgar=?", new Object[]{codgar});
        if(new Long(3).equals(genere))
          chiaveGara = codgar;
        Vector datiDITG = this.sqlManager.getVector(selectDitg, new Object[]{chiaveGara, codDitta});
        if(datiDITG!=null && datiDITG.size()>0){
          String numProt = SqlManager.getValueFromVectorParam(datiDITG, 0).stringValue();
          Timestamp dataProt = SqlManager.getValueFromVectorParam(datiDITG, 1).dataValue();
          if(numProt!=null && dataProt!=null){
            Date date = new Date(dataProt.getTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
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

}

