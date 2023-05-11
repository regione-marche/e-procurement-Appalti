package it.eldasoft.sil.pg.bl.tasks;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.SFTPManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sign.DigitalSignatureChecker;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.schmizz.sshj.xfer.FileSystemFile;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Task per la generazione del file di indice per i documenti caricati sul sistema di conservazione digitale Maggioli (COS)
 */
public class GenerazioneInvioIndiceCOSTask {

  static Logger                         logger = Logger.getLogger(GenerazioneInvioIndiceCOSTask.class);

  private static final int LIMITE_CARATTERI = 250;

  // private CreazioneArchivioDocumentiGaraManager creazioneArchivioDocumentiGaraManager;
  private ArchiviazioneDocumentiManager archiviazioneDocumentiManager;

  private SqlManager                    sqlManager;

  private FileAllegatoManager           fileAllegatoManager;

  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  // public void setCreazioneArchivioDocumentiGaraManager(
  // CreazioneArchivioDocumentiGaraManager creazioneArchivioDocumentiGaraManager) {
  // this.creazioneArchivioDocumentiGaraManager = creazioneArchivioDocumentiGaraManager;
  // }
  public void setArchiviazioneDocumentiManager(ArchiviazioneDocumentiManager archiviazioneDocumentiManager) {
    this.archiviazioneDocumentiManager = archiviazioneDocumentiManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   * archiviazione ed esportazione dei documenti di gara/elenco/catalogo
   *
   * @throws GestoreException
   */
  public void generazioneInvioIndiceCOS() throws GestoreException {
    if (!WebUtilities.isAppNotReady()) {

      DigitalSignatureChecker dsc = new DigitalSignatureChecker();
      SFTPManager sftp = new SFTPManager();
      String alias_sp = ConfigManager.getValore("cos.sftp.aliasSp");
      String alias_da = ConfigManager.getValore("cos.sftp.aliasDa");
      String pathBase = ConfigManager.getValore("cos.sftp.pathBase");
      String pathBasePrefisso = ConfigManager.getValore("cos.sftp.pathBasePrefisso");
      String maxRigheIndiceString = ConfigManager.getValore("cos.maxRigheIndice");
      Integer maxRigheIndice = 0;
      if(!"".equals(maxRigheIndiceString) && maxRigheIndiceString != null){
        maxRigheIndice = Integer.parseInt(maxRigheIndiceString);
      }
      if(pathBase == null){
        pathBase = "";
      }
      String base = pathBase + alias_sp + "/" + alias_da
      // + "/Documenti/";
          + "/";
      // {Alias-SP}_{Alias-DA}_index_yyyyMMdd
      String index_filename_temp = "/"
        + alias_sp
        + "_"
        + alias_da
        + "_index_"
        + UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_AAAAMMGG)
        + "-1"
        + ".part";

      String index_filename = "/"
        + alias_sp
        + "_"
        + alias_da
        + "_index_"
        + UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_AAAAMMGG)
        + "-1"
        + ".dat";

      Long idArchiviazione = null;
      Long sysconRichiesta = null;
      Long idDoc = null;
      String codgara = null;
      String codice = null;
      String key1Doc = null;
      String key2Doc = null;
      String provenienza = null;
      Long genere = null;
      // INIZIO GESTIONE TRASFERIMENTO A COS
      if (logger.isDebugEnabled())
        logger.debug("Avvio generazione ed invio file di indice COS");

      String cos_selRichiesteDaElaborare = "select j.id_archiviazione, j.codgara, j.syscon, j.classifica, j.cod_reg,"
          + " j.tipo_doc, j.mitt_int, j.indice, j.classifica_tit, j.uo_mittdest "
          + " from gardoc_jobs j"
          + " where j.id_archiviazione = ?  ";

      String cos_selDocumentiDaElaborare = "select w.id, w.key1, w.key2, w.provenienza, w.id_archiviazione, w.cos_nome_file"
          + " from gardoc_wsdm w where (w.stato_archiviazione = ? ) order by w.id";

      List cos_listaRichiesteDaElaborare = null;
      List cos_listaDocumentiDaElaborare = null;
      
      Boolean hasDoneSomething = false;
      HashMap<Long, List<String>> hmapSysconGare = new HashMap<Long, List<String>>();
      List<Long> idDocNonInseriti = new ArrayList<Long>();
      
  

      try{
        sftp.connect();

        try {

          // tutti i documenti in stato 4=caricati su ftp cos
          cos_listaDocumentiDaElaborare = sqlManager.getListVector(cos_selDocumentiDaElaborare, new Object[] {new Long(4) });
          
          //tengo traccia degli id elaborati per un eventuale rollback
          List<Long> idinseriti = new ArrayList<Long>();
          
          ArrayList<HashMap<String,Object>> documenti = new ArrayList<HashMap<String,Object>>();
          String indice = "";
          
          String todaysIndexAlias = alias_sp
              + "_"
              + alias_da
              + "_index_"
              + UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_AAAAMMGG);
          
          int numeroIndice = 1;
          List<String> files = sftp.GetFiles(base);
          for (int j = 0; j < files.size(); j++) {
            String jfile = files.get(j);
            if (jfile.contains(todaysIndexAlias)) {
              numeroIndice++;
            }
          }
          
       // {Alias-SP}_{Alias-DA}_index_yyyyMMdd
          index_filename_temp = "/"
            + alias_sp
            + "_"
            + alias_da
            + "_index_"
            + UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_AAAAMMGG)
            + "-"
            +numeroIndice
            + ".part";

          index_filename = "/"
            + alias_sp
            + "_"
            + alias_da
            + "_index_"
            + UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_AAAAMMGG)
            + "-"
            +numeroIndice
            + ".dat";

          if (cos_listaDocumentiDaElaborare != null && cos_listaDocumentiDaElaborare.size() > 0) {
            hasDoneSomething = true;
            sftp.CreateDirs(base);
            //prelevo tutti i documenti: devo verificare che il documento sia ancora presente in area ftp. 
            //Il record con stato_archiviazione = 2 è condizione necessaria ma non sufficiente (APPALTI-879)
            List<String> filePresenti = sftp.GetFiles(base+ "Documenti/");
            
            for (int p = 0; p < cos_listaDocumentiDaElaborare.size() && p < maxRigheIndice; p++) { 
              HashMap<String, Object> datiWSDM = new HashMap<String, Object>();
              idDoc = SqlManager.getValueFromVectorParam(cos_listaDocumentiDaElaborare.get(p), 0).longValue();

              // seleziono riga gardoc_jobs per questa riga di gardoc_wsdm
              Long _idArchiviazione = SqlManager.getValueFromVectorParam(cos_listaDocumentiDaElaborare.get(p), 4).longValue();
              String newNomeFile = SqlManager.getValueFromVectorParam(cos_listaDocumentiDaElaborare.get(p), 5).stringValue();
              cos_listaRichiesteDaElaborare = sqlManager.getListVector(cos_selRichiesteDaElaborare,
                  new Object[] {new Long(_idArchiviazione) });
              int h = 0;
              idArchiviazione = SqlManager.getValueFromVectorParam(cos_listaRichiesteDaElaborare.get(h), 0).longValue();
              codgara = SqlManager.getValueFromVectorParam(cos_listaRichiesteDaElaborare.get(h), 1).getStringValue();
              String codgar = codgara;
              sysconRichiesta = SqlManager.getValueFromVectorParam(cos_listaRichiesteDaElaborare.get(h), 2).longValue();

            //verifica presenza
              if(!filePresenti.contains(newNomeFile)) {
                this.archiviazioneDocumentiManager.updateStatoEsito(new Long(8),"File non presente in area FTP al momento della creazione dell'indice", idDoc, _idArchiviazione);
                List<String> listaGare = new ArrayList<String>();
                if(hmapSysconGare.containsKey(sysconRichiesta)) {
                  listaGare = hmapSysconGare.get(sysconRichiesta);
                }
                listaGare.add(codgar);
                hmapSysconGare.put(sysconRichiesta, listaGare);
                idDocNonInseriti.add(idDoc);
                if (logger.isDebugEnabled())
                  logger.debug("File non presente in area FTP: "+idDoc);
                continue;
              }
              String selectCodiceGara = "select codice,genere from v_gare_genere where codgar = ? and genere < 100";
              Vector<?> datiGareGenere = sqlManager.getVector(selectCodiceGara, new Object[] {codgara });
              if (datiGareGenere != null && datiGareGenere.size() > 0) {
                codice = SqlManager.getValueFromVectorParam(datiGareGenere, 0).stringValue();
                codice = UtilityStringhe.convertiNullInStringaVuota(codice);
                genere = (Long) SqlManager.getValueFromVectorParam(datiGareGenere, 1).getValue();
              }

              key1Doc = SqlManager.getValueFromVectorParam(cos_listaDocumentiDaElaborare.get(p), 1).getStringValue();
              key1Doc = UtilityStringhe.convertiNullInStringaVuota(key1Doc);
              key2Doc = SqlManager.getValueFromVectorParam(cos_listaDocumentiDaElaborare.get(p), 2).getStringValue();
              key2Doc = UtilityStringhe.convertiNullInStringaVuota(key2Doc);
              provenienza = SqlManager.getValueFromVectorParam(cos_listaDocumentiDaElaborare.get(p), 3).getStringValue();
              provenienza = UtilityStringhe.convertiNullInStringaVuota(provenienza);

              HashMap<String,Object> documento = new HashMap<String,Object>();
              documento.put(ArchiviazioneDocumentiManager.KEY1, key1Doc);
              documento.put(ArchiviazioneDocumentiManager.KEY2, key2Doc);
              documento.put(ArchiviazioneDocumentiManager.IDDOC, idDoc);
              documento.put(ArchiviazioneDocumentiManager.IDARCHIVIAZIONE, idArchiviazione);
              documenti.add(documento);


              String documentiAssociatiDB = ConfigManager.getValore("it.eldasoft.documentiAssociatiDB");

              String oggetto = "gara";

              try {

                String condizioneAppend = this.sqlManager.getDBFunction("concat", new String[] {"'Documentazione '", "'" + oggetto + "'" });
                // Documentazione di gara

                String indiceClassificazione = null;
                if(genere != null && (genere.intValue() == 1 || genere.intValue() == 3)){
                  indiceClassificazione = (String) this.sqlManager.getObject("select destor from torn where codgar = ?", new Object[] {codgar});
                }else{
                  indiceClassificazione = (String) this.sqlManager.getObject("select not_gar from gare where codgar1 = ?", new Object[] {codgar});
                }
                if (indiceClassificazione == null || "".equals(indiceClassificazione)) {
                  indiceClassificazione = "n.d.";
                }else{
                  indiceClassificazione = escape(indiceClassificazione);
                }


                List<HashMap> hmtecniuffint = sqlManager.getListHashMap("select tecni.nomtec, tecni.cftec, uffint.nomein "
                    + ",uffint.codipa "
                    + "from tecni "
                    + "join torn on tecni.codtec = torn.codrup "
                    + "join uffint on torn.cenint = uffint.codein "
                    + "where torn.codgar = ?", new Object[] {codgar });
                if (hmtecniuffint.size() > 0) {
                  HashMap item = hmtecniuffint.get(0);
                  String nomtec = escape(((JdbcParametro) item.get("NOMTEC")).stringValue());
                  String cftec = escape(((JdbcParametro) item.get("CFTEC")).stringValue());
                  String responsabileUo = limitaCaratteri(cftec + "; " + nomtec + "; RUP");
                  String nomein = escape(((JdbcParametro) item.get("NOMEIN")).stringValue());
                  JdbcParametro codipa = ((JdbcParametro) item.get("CODIPA"));

                  String codipaTemp = "";
                  if (codipa != null) {
                    if (codipa.stringValue() != null) {
                      codipaTemp = escape(codipa.stringValue()) + "; ";
                    }
                  }
                  String ufficioResponsabile = limitaCaratteri(codipaTemp + nomein);

                    try {

                      String tipologia;
                      String descrizione;
                      Long gruppo;
                      String nomeFile;
                      String data;
                      String archiviato;
                      String dataDoc;
                      String argomento;
                      String soggettiEsterni;
                      String numeroRegistrazione;

                      String rifFascicolo = codgar.replace("$", "");

                        tipologia = "";
                        descrizione = "";
                        nomeFile = "";
                        data = "";
                        archiviato = "";
                        dataDoc = "";
                        argomento = "";
                        soggettiEsterni = "";

                        String idprg = (String) documento.get(ArchiviazioneDocumentiManager.KEY1);
                        String iddocdig = (String) documento.get(ArchiviazioneDocumentiManager.KEY2);
                        numeroRegistrazione = idprg+iddocdig;

                      //documentazione della gara (DOCUMGARA)
                        if(provenienza.equals("1")){

                          String select ="select DESCRIZIONE, GRUPPO, DIGNOMDOC, ISARCHI, DATARILASCIO " +
                                "from documgara d, w_docdig w where d.iddocdg = ? and d.idprg = ? and d.iddocdg=w.iddocdig and d.idprg=w.idprg ";

                          List datiDoc = sqlManager.getListVector(select, new Object[] { iddocdig,idprg });

                          if (datiDoc != null && datiDoc.size() > 0) {
                              argomento = "Documentazione di gara/elenco operatori/avviso";
                              descrizione = SqlManager.getValueFromVectorParam(datiDoc.get(0), 0).stringValue();
                              gruppo = (Long) SqlManager.getValueFromVectorParam(datiDoc.get(0), 1).getValue();
                              nomeFile = SqlManager.getValueFromVectorParam(datiDoc.get(0), 2).stringValue();
                              archiviato = SqlManager.getValueFromVectorParam(datiDoc.get(0), 3).stringValue();
                              java.sql.Date dataTemp = (Date) SqlManager.getValueFromVectorParam(datiDoc.get(0), 4).getValue();
                              dataDoc = UtilityDate.convertiData(dataTemp, UtilityDate.FORMATO_GG_MM_AAAA);
                              soggettiEsterni = "interno";

                              switch (gruppo.intValue()) {
                              case 1:
                                tipologia = "Documento del bando/avviso";
                                break;
                              case 3:
                                if (genere == 10 || genere == 20) {
                                  tipologia = "Fac-simile documento richiesti agli operatori";
                                  break;
                                } else {
                                  tipologia = "Fac-simile documento richiesti ai concorrenti";
                                  break;
                                }
                              case 4:
                                tipologia = "Documento dell'esito";
                                break;
                              case 5:
                                tipologia = "Documento per la trasparenza";
                                break;
                              case 6:
                                tipologia = "Documento dell'invito a presentare offerta";
                                break;
                              case 10:
                                tipologia = "Atto o documento art.29 c.1 DLgs.50/2016";
                                break;
                              case 11:
                                tipologia = "Documento allegato all'ordine di acquisto";
                                break;
                              case 12:
                                tipologia = "Documento dell'invito all'asta elettronica";
                                break;
                              default:
                                tipologia =  gruppo.toString();
                                break;
                              }
                          }

                        }

                        //comunicazioni pubbliche (W_INVCOM)
                        if(provenienza.equals("2")){

                          String select ="select COMMSGOGG, DIGDESDOC, DIGNOMDOC, COMDATAPUB, COMSTATO " +
                                "from w_invcom i, w_docdig w where w.iddocdig = ? and w.digkey1 = ? and i.idcom=w.digkey2 and i.idprg=w.idprg ";

                          List datiDoc = sqlManager.getListVector(select, new Object[] { iddocdig,idprg });

                          if (datiDoc != null && datiDoc.size() > 0) {
                              tipologia=  "Comunicazione pubblica";
                              argomento = "Documentazione di gara/elenco operatori/avviso";
                              descrizione = SqlManager.getValueFromVectorParam(datiDoc.get(0), 0).stringValue();
                              descrizione = descrizione + " " + SqlManager.getValueFromVectorParam(datiDoc.get(0), 1).stringValue();
                              nomeFile = SqlManager.getValueFromVectorParam(datiDoc.get(0), 2).stringValue();
                              java.sql.Date dataTemp = (Date) SqlManager.getValueFromVectorParam(datiDoc.get(0), 3).getValue();
                              dataDoc = UtilityDate.convertiData(dataTemp, UtilityDate.FORMATO_GG_MM_AAAA);
                              String comstato = SqlManager.getValueFromVectorParam(datiDoc.get(0), 0).stringValue();
                              if("12".equals(comstato)){
                                archiviato = "1";
                              }
                              soggettiEsterni = "interno";
                          }

                        }

                        //documenti dell'impresa (IMPRCODG)
                        if(provenienza.equals("3")){

                          String select ="select d.CODIMP, d.BUSTADESC, d.DESCRIZIONE, d.DIGNOMDOC, d.DATARILASCIO, d.ORARILASCIO " +
                                "from v_gare_docditta d, w_docdig w where d.iddocdg = ? and d.idprg = ? and d.iddocdg=w.iddocdig and d.idprg=w.idprg ";

                          List datiDoc = sqlManager.getListVector(select, new Object[] { iddocdig,idprg });

                          String nomeImpresa = "";
                          String codFiscImpresa = "";

                          if (datiDoc != null && datiDoc.size() > 0) {
                              argomento = "Documentazione delle ditte";
                              String codimp = SqlManager.getValueFromVectorParam(datiDoc.get(0), 0).stringValue();
                              tipologia = SqlManager.getValueFromVectorParam(datiDoc.get(0), 1).stringValue();
                              descrizione = SqlManager.getValueFromVectorParam(datiDoc.get(0), 2).stringValue();
                              nomeFile = SqlManager.getValueFromVectorParam(datiDoc.get(0), 3).stringValue();
                              java.sql.Date dataTemp = (Date) SqlManager.getValueFromVectorParam(datiDoc.get(0), 4).getValue();
                              dataDoc = UtilityDate.convertiData(dataTemp, UtilityDate.FORMATO_GG_MM_AAAA);
                              String ora = SqlManager.getValueFromVectorParam(datiDoc.get(0), 5).stringValue();
                              dataDoc = dataDoc + ora;

                              // verifico se la ditta è un raggruppamento
                              String getImpr = "select codimp, tipimp, cfimp, nomest, nomimp from impr where codimp = ?";
                              List<?> listaImpresa = this.sqlManager.getListVector(getImpr, new Object[] {codimp });
                              if (listaImpresa != null && listaImpresa.size() > 0) {
                                String cod = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 0).getStringValue();
                                Long tipimp = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 1).longValue();
                                codFiscImpresa = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 2).getStringValue();
                                nomeImpresa = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).getStringValue();
                                if (tipimp != null && (new Long(3).equals(tipimp) || new Long(10).equals(tipimp))) {
                                  // la ditta è un raggruppamento allora prendo i dati della mandataria
                                  cod = (String) this.sqlManager.getObject("select coddic from ragimp where codime9 = ? and impman='1'",
                                      new Object[] {cod });
                                  listaImpresa = this.sqlManager.getListVector(getImpr, new Object[] {cod });
                                  if (listaImpresa != null && listaImpresa.size() > 0) {
                                    nomeImpresa += "(mandataria " + SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).getStringValue() + ")";
                                    codFiscImpresa = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 2).getStringValue();
                                  }
                                }
                              }
                              soggettiEsterni = escape(codFiscImpresa + ", "+ nomeImpresa);
                            }
                        }

                        //documentazione associata alla gara su filesystem (C0OGGAS)
                        if(provenienza.equals("4") || provenienza.equals("7")){

                          String select = "";
                          if(provenienza.equals("4")){
                            select ="select C0ATIT,C0ANOMOGG,C0ADATTO,C0ADAT,C0ADPROT from c0oggass c where c.c0acod = ? and c.c0aprg = ?";
                          }else{
                            select ="select C0ATIT,C0ANOMOGG,C0ADATTO,C0ADAT,C0ADPROT from c0oggass c, w_docdig w where w.iddocdig = ? and w.idprg = ? and w.digkey1 = c.c0acod and c0aprg = w.idprg";
                          }

                          List datiDoc = sqlManager.getListVector(select, new Object[] { iddocdig,idprg });

                          if (datiDoc != null && datiDoc.size() > 0) {
                              argomento = "Documentazione di gara/elenco operatori/avviso";
                              tipologia = "Documento associato";
                              descrizione = SqlManager.getValueFromVectorParam(datiDoc.get(0), 0).stringValue();
                              nomeFile = SqlManager.getValueFromVectorParam(datiDoc.get(0), 1).stringValue();
                              java.sql.Date dataTemp = (Date) SqlManager.getValueFromVectorParam(datiDoc.get(0), 2).getValue();
                              dataDoc = UtilityDate.convertiData(dataTemp, UtilityDate.FORMATO_GG_MM_AAAA);
                              if(dataDoc == null || "".equals(dataDoc)){
                                dataTemp = (Date) SqlManager.getValueFromVectorParam(datiDoc.get(0), 3).getValue();
                                dataDoc = UtilityDate.convertiData(dataTemp, UtilityDate.FORMATO_GG_MM_AAAA);
                                if(dataDoc == null || "".equals(dataDoc)){
                                  dataTemp = (Date) SqlManager.getValueFromVectorParam(datiDoc.get(0), 4).getValue();
                                  dataDoc = UtilityDate.convertiData(dataTemp, UtilityDate.FORMATO_GG_MM_AAAA);
                                }
                              }
                              soggettiEsterni = "interno";
                          }

                          numeroRegistrazione = iddocdig;

                        }

                        //documentazione della gara (DOCUMGARA)
                        if(provenienza.equals("5")){

                          String select ="select COMMSGOGG, DIGDESDOC, DIGNOMDOC, COMDATINS, IDCOM " +
                                "from w_invcom i, w_docdig w where w.iddocdig = ? and w.digkey1 = ? and i.idcom=w.digkey2 and i.idprg=w.idprg ";

                          List datiDoc = sqlManager.getListVector(select, new Object[] { iddocdig,idprg });

                          if (datiDoc != null && datiDoc.size() > 0) {
                              argomento = "Documentazione delle ditte";
                              tipologia = "Documento inviato alla ditta";
                              descrizione = SqlManager.getValueFromVectorParam(datiDoc.get(0), 0).stringValue();
                              descrizione = descrizione + " " + SqlManager.getValueFromVectorParam(datiDoc.get(0), 1).stringValue();
                              nomeFile = SqlManager.getValueFromVectorParam(datiDoc.get(0), 2).stringValue();
                              java.sql.Date dataTemp = (Date) SqlManager.getValueFromVectorParam(datiDoc.get(0), 3).getValue();
                              dataDoc = UtilityDate.convertiData(dataTemp, UtilityDate.FORMATO_GG_MM_AAAA);
                              Long idcom = (Long) SqlManager.getValueFromVectorParam(datiDoc.get(0), 4).getValue();
                              List listaDestinatari = sqlManager.getListVector("select distinct(descodsog) from w_invcomdes where idcom = ? and idprg = ?", new Object[] { idcom,idprg });
                              if (listaDestinatari != null && listaDestinatari.size() > 0) {
                                for (int m = 0; m < listaDestinatari.size(); m++) {
                                  String nomeImpresa = "";
                                  String codFiscImpresa = "";
                                  String codimp = SqlManager.getValueFromVectorParam(listaDestinatari.get(m), 0).stringValue();
                                  // verifico se la ditta ï¿½ un raggruppamento
                                  String getImpr = "select codimp, tipimp, cfimp, nomest, nomimp from impr where codimp = ?";
                                  List<?> listaImpresa = this.sqlManager.getListVector(getImpr, new Object[] {codimp });
                                  if (listaImpresa != null && listaImpresa.size() > 0) {
                                    String cod = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 0).getStringValue();
                                    Long tipimp = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 1).longValue();
                                    codFiscImpresa = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 2).getStringValue();
                                    nomeImpresa = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).getStringValue();
                                    if (tipimp != null && (new Long(3).equals(tipimp) || new Long(10).equals(tipimp))) {
                                      // la ditta ï¿½ un raggruppamento allora prendo i dati della mandataria
                                      cod = (String) this.sqlManager.getObject("select coddic from ragimp where codime9 = ? and impman='1'",
                                          new Object[] {cod });
                                      listaImpresa = this.sqlManager.getListVector(getImpr, new Object[] {cod });
                                      if (listaImpresa != null && listaImpresa.size() > 0) {
                                        nomeImpresa += "(mandataria " + SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).getStringValue() + ")";
                                        codFiscImpresa = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 2).getStringValue();
                                      }
                                    }
                                  }
                                  if(!"".equals(soggettiEsterni)){
                                    soggettiEsterni = soggettiEsterni + "|n.d.|";
                                  }
                                soggettiEsterni = soggettiEsterni + escape(codFiscImpresa + ", "+ nomeImpresa);
                              }
                            }
                          }
                        }

                        //documentazione riservate alla ditta (W_INVCOM)
                        if(provenienza.equals("6")){

                          String select ="select COMMSGOGG, DIGDESDOC, DIGNOMDOC, COMDATINS, COMKEY1 " +
                          "from w_invcom i, w_docdig w where w.iddocdig = ? and w.idprg = ? and i.idcom=w.digkey1 and i.idprg=w.idprg";

                          List datiDoc = sqlManager.getListVector(select, new Object[] { iddocdig,idprg });

                          String nomeImpresa = "";
                          String codFiscImpresa = "";

                          if (datiDoc != null && datiDoc.size() > 0) {
                            argomento = "Documentazione delle ditte";
                            tipologia = "Documento inviato dalla ditta";
                            descrizione = SqlManager.getValueFromVectorParam(datiDoc.get(0), 0).stringValue();
                            descrizione = descrizione + " " + SqlManager.getValueFromVectorParam(datiDoc.get(0), 1).stringValue();
                            nomeFile = SqlManager.getValueFromVectorParam(datiDoc.get(0), 2).stringValue();
                            java.sql.Date dataTemp = (Date) SqlManager.getValueFromVectorParam(datiDoc.get(0), 3).getValue();
                            dataDoc = UtilityDate.convertiData(dataTemp, UtilityDate.FORMATO_GG_MM_AAAA);
                            String username = SqlManager.getValueFromVectorParam(datiDoc.get(0), 4).stringValue();
                            String codimp = (String)this.sqlManager.getObject("select userkey1 from w_puser where usernome = ?", new Object[]{username});

                              // verifico se la ditta ï¿½ un raggruppamento
                              String getImpr = "select codimp, tipimp, cfimp, nomest, nomimp from impr where codimp = ?";
                              List<?> listaImpresa = this.sqlManager.getListVector(getImpr, new Object[] {codimp });
                              if (listaImpresa != null && listaImpresa.size() > 0) {
                                String cod = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 0).getStringValue();
                                Long tipimp = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 1).longValue();
                                nomeImpresa = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).getStringValue();
                                codFiscImpresa = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 2).getStringValue();
                                if (tipimp != null && (new Long(3).equals(tipimp) || new Long(10).equals(tipimp))) {
                                  // la ditta ï¿½ un raggruppamento allora prendo i dati della mandataria
                                  cod = (String) this.sqlManager.getObject("select coddic from ragimp where codime9 = ? and impman='1'",
                                      new Object[] {cod });
                                  listaImpresa = this.sqlManager.getListVector(getImpr, new Object[] {cod });
                                  if (listaImpresa != null && listaImpresa.size() > 0) {
                                    nomeImpresa += "(mandataria " + SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).getStringValue() + ")";
                                    codFiscImpresa = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 2).getStringValue();
                                  }
                                }
                              }
                              soggettiEsterni = escape(codFiscImpresa + ", "+ nomeImpresa);
                            }
                        }

                        String dataRegistrazione = escape(dataDoc).substring(0, 10);


                        String versione = "1";
                        String oggettoDescr = escape(descrizione);
                        String eldaver_string = "";
                        String eldaver_sql = "select numver from eldaver where codapp='PG'";
                        Object eldaver = this.sqlManager.getObject(eldaver_sql, new Object[] {});
                        if (eldaver != null) {
                          eldaver_string = eldaver.toString();
                        }
                        String trasmittente = "Appalti&Contratti - vers. " + eldaver_string;

                        String riferimenti = "n.d.";

                        BlobFile bf = null;
                        byte[] bytes;

                        if (provenienza.equals("4")) {
                          // associato su filesystem
                          String pathDocumentiAssociati = ConfigManager.getValore("it.eldasoft.documentiAssociati");
                          File fileAssociato = new File(pathDocumentiAssociati + "/" + nomeFile);
                          bytes = FileUtils.readFileToByteArray(fileAssociato);
                        } else {
                          bf = fileAllegatoManager.getFileAllegato(idprg, new Long(iddocdig));
                          bytes = bf.getStream();
                        }
                        String impronta = getImpronta(bytes);

                        nomeFile = null;
                        if (soggettiEsterni.equals("")) {
                          soggettiEsterni = "interno";
                        }

                        String annoRegistrazione = dataRegistrazione.substring(6, dataRegistrazione.length());
                        String idUnivocoPersistente = "AGC: " + numeroRegistrazione + "/" + annoRegistrazione;
                        String dataChiusura = dataRegistrazione;
                        String rigaIndice = "";
                        rigaIndice += pathBasePrefisso + base + "Documenti/"
                            + newNomeFile
                            + "|"
                            + ufficioResponsabile
                            + "|"
                            + indiceClassificazione
                            + "|"
                            + tipologia
                            + "|"
                            + dataRegistrazione
                            + "|"
                            + numeroRegistrazione
                            + "|"
                            + idUnivocoPersistente
                            + "|"
                            + trasmittente
                            + "|"
                            + impronta
                            + "|"
                            + versione
                            + "|"
                            + responsabileUo
                            + "|"
                            + rifFascicolo
                            + "|"
                            + oggettoDescr
                            + "|"
                            + dataChiusura
                            + "|"
                            + soggettiEsterni
                            + "|"
                            + riferimenti;

                        rigaIndice += System.getProperty("line.separator");
                        if (logger.isDebugEnabled())
                          logger.debug("Inizio : "+idDoc);
                        if (!rigaIndice.equals("")) {
                          indice += rigaIndice;
                        }
                        idinseriti.add((Long)documento.get(ArchiviazioneDocumentiManager.IDDOC));

                    } catch (Exception ex) {
                      logger.error("Errore durante la scrittura del file di indice COS " + ex.getMessage());
                    }
                }
              } catch (SQLException e) {
                // throw new JspException("Errore durante la selezione dei documenti della gara da archiviare", e);
                logger.error("Errore durante la selezione dei documenti della gara da archiviare " + e.getMessage());
              }
            } // for documenti da elaborare
            boolean success = false;
            byte[] bytesIndice = indice.getBytes();
            String impronta = getImpronta(bytesIndice);
            if (logger.isDebugEnabled()) logger.debug("Trasferisco il file di indice temporaneo.");
            sftp.Put(bytesIndice, base + index_filename_temp);
            
            if (logger.isDebugEnabled()) logger.debug("Prelevo il file di indice per verificarne l'hash.");
            File tempFile = File.createTempFile("tmp", null, null);
            sftp.Get(base + index_filename_temp, new FileSystemFile(tempFile));

            byte[] b2 = FileUtils.readFileToByteArray(tempFile);
            String improntaCheck = getImpronta(b2);
            if (impronta.equals(improntaCheck)) {
              if (logger.isDebugEnabled()) logger.debug("Hash corretto, rinomino il file.");
              sftp.Rename(base + index_filename_temp.substring(1), base + index_filename);
              success = true;
              if (logger.isDebugEnabled()) logger.debug("File di indice trasferito correttamente.");
            }else {
              if (logger.isDebugEnabled()) logger.debug("Hash NON corretto.");
              sftp.Remove(base + index_filename_temp);
              if (logger.isDebugEnabled()) logger.debug("File "+index_filename_temp.substring(1)+" rimosso");
            }
            
            if(success) {
              //aggiorno a stato archiviazione 5 tutti i record presenti nel file di indice
              for(int idIndex=0; idIndex<idinseriti.size(); idIndex++) {
                  try {
                    this.archiviazioneDocumentiManager.updateStatoEsitoGlobale(new Long(5), null, index_filename.replace("/", ""), idinseriti.get(idIndex));
                  }catch(SQLException e) {
                    logger.error("Errore nell'update della gardoc_wsdm: "+e.getMessage());
                  }     
              }
              if (logger.isDebugEnabled()) logger.debug("Update a stato archiviazione = 5 eseguito.");
            }
           }
        
        } catch (SQLException e) {
          throw new GestoreException("Errore nella lettura della lista dei documenti da trasferire a COS", null, e);
        } catch (Exception ex) {
          logger.error("Generazione file di indice COS: " + ex.getMessage());
        }finally {
          List<String> distinctGare = new ArrayList<String>();
          for(Long key : hmapSysconGare.keySet()) {
            distinctGare.addAll(hmapSysconGare.get(key));
          }
        //inserisco il messaggio
          if(hasDoneSomething) {
          writeMsg(hmapSysconGare, new Long(5), index_filename.replace("/", ""),
              "E' stato creato il file di indice per la conservazione digitale dei documenti delle seguenti gare: ");
          }
          String evento = "";
          int livEvento = 1;
          List gareCoinvolte = sqlManager.getListVector("select distinct(gj.codgara) from gardoc_wsdm gw join gardoc_jobs gj on gw.id_archiviazione=gj.id_archiviazione where "
              + "gw.cos_indice = ? and gw.stato_archiviazione = ? order by gj.codgara asc",new Object[] {index_filename.replace("/", ""), new Long(5)});
          if(gareCoinvolte != null && gareCoinvolte.size() >0) {
          evento = "Il task di creazione del file di indice ("+index_filename.replace("/", "")+") ha elaborato le richieste di archiviazione per le seguenti gare: ";
          for(int i=0; i< gareCoinvolte.size();i++) {
            if(i>0) 
              evento += ", ";
            evento += SqlManager.getValueFromVectorParam(gareCoinvolte.get(i), 0).stringValue().replace("$", "");
            }
          }
          if(idDocNonInseriti.size()>0) {
            livEvento=3;
            if(!"".equals(evento))
                evento += ". ";
            else
              evento = "Il file di indice non è stato elaborato. ";
            evento += "\nAlcuni documenti erano marcati come presenti in area ftp, ma al momento dell'elaborazione non sono stati trovati (GARDOC_WSDM.ID:  ";
            for(int j=0; j<idDocNonInseriti.size();j++) {
              if(j>0) 
                evento += ", ";
              evento += idDocNonInseriti.get(j);
            }
            evento += "). ";
            if(hmapSysconGare!=null) {
              
              distinctGare = new ArrayList(new LinkedHashSet(distinctGare));
              
              evento += "Controllare le seguenti gare: "+distinctGare.toString().replaceAll("\\[|\\]|\\$", "");
             
            }
          }
          if(!hasDoneSomething) {
            if(cos_listaDocumentiDaElaborare != null && cos_listaDocumentiDaElaborare.size()>0)
              evento = "Non è stata fatta alcuna elaborazione poiché file di indice è già presente in area ftp.";
            else
              evento = "Nessun documento da elaborare.";
          }
          try {
            LogEvento logEvento = new LogEvento();
            logEvento.setCodApplicazione("PG");
            logEvento.setOggEvento(null);
            logEvento.setLivEvento(livEvento);
            logEvento.setCodEvento("GA_COS_CREAZIONEINDICE");
            logEvento.setDescr("Creazione file di indice");
            logEvento.setErrmsg(evento);
            LogEventiUtils.insertLogEventi(logEvento);
          } catch (Exception le) {
            logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
          }
        }
        try {
          sftp.disconnect();
        }catch(Exception e) {}


        if (logger.isDebugEnabled())
          logger.debug("Fine generazione ed invio file di indice COS");

        sftp = new SFTPManager();
        sftp.connect();
        if (logger.isDebugEnabled())
          logger.debug("Avvio controllo esiti COS");
        
        List<String> indiciTrasferiti = new ArrayList<String>();
        HashMap<String,List<String>> indiciNonTrasferiti = new HashMap<String,List<String>>();
        Boolean elaborazioni = false;
        
        try {
          List<String> files = sftp.GetFiles(base);
          if (logger.isDebugEnabled()) logger.debug("Lettura della directory ftp avvenuta con successo.");
          List listaIndiciNonTrasferiti = sqlManager.getListVector("select distinct(cos_indice) from gardoc_wsdm where stato_archiviazione = ?",new Object[] {new Long(5)});
         
          if (listaIndiciNonTrasferiti != null && listaIndiciNonTrasferiti.size() > 0) {
            for (int n = 0; n < listaIndiciNonTrasferiti.size(); n++) {
              String indice = SqlManager.getValueFromVectorParam(listaIndiciNonTrasferiti.get(n), 0).stringValue();

              for (int i = 0; i < files.size(); i++) {
                String fileIndice = files.get(i);
                if (fileIndice.contains(indice) && (fileIndice.contains("ko_") || fileIndice.contains("ok_"))) {
                  elaborazioni = true;
                  //prendo i PID del file di indice che ancora non è stato trasferito a cos
                  String PID = fileIndice.substring(fileIndice.lastIndexOf("_") + 1);

                  for (int j = 0; j < files.size(); j++) {
                    String fileVersamento = files.get(j);
                    if (fileVersamento.contains("versamento_" + PID)) {

                      File tempFile = File.createTempFile("tmp", null, null);
                      sftp.Get(base + fileVersamento, new FileSystemFile(tempFile));
                      byte[] bytes = FileUtils.readFileToByteArray(tempFile);
                      byte[] v = dsc.getContent(bytes);
                      String xml = new String(v);

                      try {
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        InputSource is = new InputSource(new StringReader(xml));
                        Document root = builder.parse(is);
                        root.getDocumentElement().normalize();

                        if(fileIndice.contains("ko_" + PID)){
                          if (logger.isDebugEnabled()) logger.debug("File indice ko. Inizio gestione");
                          //trasferimento fallito
                          File tempdat = File.createTempFile("tmpdat", null, null);
                          sftp.Get(base + fileIndice, new FileSystemFile(tempdat));
                          byte[] v2 = FileUtils.readFileToByteArray(tempdat);
                          String dat = new String(v2);
                          String[] dat_lines = dat.split("\n");

                          // prendo il nome del file di indice originale
                          indice = fileIndice.substring(0, fileIndice.lastIndexOf("."));
                          this.archiviazioneDocumentiManager.updateStatoArchiviazionePacchettoCOS(new Long(7), PID, indice);
                          
                          if (logger.isDebugEnabled()) logger.debug("File indice ko. Record aggiornati allo stato 7");
                          
                          //inserisco il messaggio
                          writeMsg(null, new Long(7), indice,
                              "Il versamento dell'indice "+indice+" non è andato a buon fine. Controllare le seguenti gare: ");
                          
                          indiciNonTrasferiti.put(indice, new ArrayList<String>());

                          NodeList nl = root.getElementsByTagName("errore");
                          for (int k = 0; k < nl.getLength(); k++) {
                            Node d = nl.item(k);

                            String errore = d.getTextContent();
                            if (errore.length() > 4) {
                              List<String> errori = indiciNonTrasferiti.get(indice);
                              errori.add(errore);
                              indiciNonTrasferiti.put(indice, errori);
                              if (errore.substring(0, 4).toLowerCase().equals("riga")) {
                                int numriga = Integer.parseInt(errore.substring(errore.indexOf(" ") + 1, errore.indexOf(":")));
                                String linea_in_indice = dat_lines[numriga - 1];
                                String fname = linea_in_indice.substring(0, linea_in_indice.indexOf("|"));
                                fname = fname.substring(fname.lastIndexOf("/") + 1);
                                this.archiviazioneDocumentiManager.updateEsitoDocumentoCOS(errore, indice, fname);
                                
                              }
                            }
                          }
                        }
                        if(fileIndice.contains("ok_" + PID)){
                          if (logger.isDebugEnabled()) logger.debug("File indice ok. Inizio gestione");
                          // andato a buon fine
                          // prendo il nome del file di indice originale
                          indice = fileIndice.substring(0, fileIndice.lastIndexOf("."));
                          this.archiviazioneDocumentiManager.updateStatoArchiviazionePacchettoCOS(new Long(6), PID, indice);
                          
                          if (logger.isDebugEnabled()) logger.debug("File indice ok. Record aggiornati allo stato 6");
                          
                          //inserisco il messaggio
                          writeMsg(null, new Long(6), indice,
                              "Il versamento dell'indice "+indice+" si è concluso correttamente per le seguenti  gare: ");
                          
                          indiciTrasferiti.add(indice);

                          NodeList nl = root.getElementsByTagName("documento");
                          for (int k = 0; k < nl.getLength(); k++) {
                            Node d = nl.item(k);
                            String UID = d.getAttributes().getNamedItem("UID").getTextContent();
                            String fname = ((Element) d).getElementsByTagName("file").item(0).getAttributes().getNamedItem("fileName").getTextContent();
                            this.archiviazioneDocumentiManager.updateUIDDocumentoCOS(UID, indice, fname);
                          }
                        }
                      }catch (Exception ex) {
                        logger.error("Controllo esiti COS: " + ex.getMessage());
                      }
                    }
                  }
                }
              }
            }
          }
        } catch (Exception ex) {
          logger.error("Controllo esiti (COS): " + ex.getMessage());
        }
        finally {
          String evento = "";
          int livEvento = 1;
          if(elaborazioni) {
            evento = "";
            if(indiciTrasferiti.size()>0) {
              evento += "Versamento completato correttamente per i seguenti file di indice: ";
              for(int j=0; j<indiciTrasferiti.size();j++) {
                if(j>0) 
                  evento += ", ";
                evento += indiciTrasferiti.get(j);
              }
              evento += "\n";
            }
            if(indiciNonTrasferiti.keySet().size()>0) {
              livEvento=3;
              evento += "Versamento fallito per i seguenti file di indice: ";
              for(String indice : indiciNonTrasferiti.keySet()) {
                evento +="\n-"+indice+": "+indiciNonTrasferiti.get(indice).toString();
              }
            }
          }else {
            evento = "Nessun rapporto di versamento da elaborare.";
          }
          try {
            LogEvento logEvento = new LogEvento();
            logEvento.setCodApplicazione("PG");
            logEvento.setOggEvento(null);
            logEvento.setLivEvento(livEvento);
            logEvento.setCodEvento("GA_COS_LETTURAVERSAMENTO");
            logEvento.setDescr("Lettura dei rapporti di versamento");
            logEvento.setErrmsg(evento);
            LogEventiUtils.insertLogEventi(logEvento);
          } catch (Exception le) {
            logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
          }
        }
        sftp.disconnect();

      }catch (Exception ex) {
        logger.error("Controllo esiti (COS): Errore in fase di connessione al servizio " + ex.getMessage());
      }
      if (logger.isDebugEnabled())
        logger.debug("Fine controllo esiti COS");

    }
  }

  public String escape(String s) {
    if(s==null)
      return "";
    s = s.replace('|', '-').replace(';', '-');
    s = s.replaceAll("\\r\\n|\\r|\\n", "-");
    s = limitaCaratteri(s);
    return s;
  }

  public String limitaCaratteri(String s){
    int i = GenerazioneInvioIndiceCOSTask.LIMITE_CARATTERI;
    if(s.length()>= i){
      s = s.substring(0, i);
    }
    return s;
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
  
  public void writeMsg(HashMap<Long, List<String>> hmapErrori, Long stato_archivazione, String cos_indice, String msg1) throws SQLException {
    try {
        List destinatari = sqlManager.getListVector("select distinct(gj.syscon) from gardoc_wsdm gw join gardoc_jobs gj on gw.id_archiviazione=gj.id_archiviazione where "
        + "gw.cos_indice = ? and gw.stato_archiviazione = ? order by gj.syscon asc",new Object[] {cos_indice,stato_archivazione});
        
        if (destinatari != null && destinatari.size() > 0) {
          for (int d = 0; d < destinatari.size(); d++) {
            try {
            Long destinatario = SqlManager.getValueFromVectorParam(destinatari.get(d), 0).longValue();
            List gareCoinvolte = sqlManager.getListVector("select distinct(gj.codgara) from gardoc_wsdm gw join gardoc_jobs gj on gw.id_archiviazione=gj.id_archiviazione where "
                + "gw.cos_indice = ? and gw.stato_archiviazione = ? and gj.syscon = ? order by gj.codgara asc",new Object[] {cos_indice,stato_archivazione,destinatario});
            String messaggio = msg1;
            if (gareCoinvolte != null && gareCoinvolte.size() > 0) {
              for (int g = 0; g < gareCoinvolte.size(); g++) {
                if(g>0) {
                  messaggio += ", ";
                }
                messaggio += SqlManager.getValueFromVectorParam(gareCoinvolte.get(g), 0).stringValue().replace("$", "");
              }
            }
            if(hmapErrori!=null) {
              List gareConErrori = hmapErrori.get(destinatario);
              if(gareConErrori != null && gareConErrori.size()>0) {
                List<String> distinctGareConErrori = new ArrayList(new LinkedHashSet(gareConErrori));
                String errori = distinctGareConErrori.toString().replaceAll("\\[|\\]|\\$", "");
                messaggio  += "\nAlcuni documenti ("+gareConErrori.size()+") non sono stati riportati nel file di indice in seguito a errori. Controllare le seguenti gare: "+errori;
              }
              hmapErrori.remove(destinatario);
            }
            //inserisco messaggio per le richieste di archiviazione
            this.archiviazioneDocumentiManager.insertMsg(messaggio, destinatario);
            //rimuovo dalla mappa i destinatari raggiunti
            
            }catch(SQLException e) {
              logger.error("Errore nell'inserimento dei messaggi: "+e.getMessage());
            } catch (GestoreException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }   
          }
        }
        if(hmapErrori!=null && hmapErrori.size()>0) {
          //se ho dei documenti scartati con destinatari non raggiunti prima
          for(Long destinatario : hmapErrori.keySet()) {
            String messaggio = "Nessun documento è stato inserito nel file di indice. Controllare le seguenti gare: ";
              List gareConErrori = hmapErrori.get(destinatario);
              if(gareConErrori != null && gareConErrori.size()>0) {
                List<String> distinctGareConErrori = new ArrayList(new LinkedHashSet(gareConErrori));
                String errori = distinctGareConErrori.toString().replaceAll("\\[|\\]|\\$", "");
                messaggio  += errori;
              }
              this.archiviazioneDocumentiManager.insertMsg(messaggio, destinatario);
            }
          }
        }catch(SQLException | GestoreException e) {
          logger.error("Errore nell'inserimento dei messaggi: "+e.getMessage());
        }
  }

}
