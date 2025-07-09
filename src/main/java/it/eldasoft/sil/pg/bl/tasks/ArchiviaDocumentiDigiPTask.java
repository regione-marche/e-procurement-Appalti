package it.eldasoft.sil.pg.bl.tasks;

import it.appaltiecontratti.digip.datatypes.*;
import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.SFTPManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import net.schmizz.sshj.xfer.FileSystemFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.xmlbeans.XmlOptions;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static it.eldasoft.gene.utils.ConfigurazionePerUffintUtils.getOptConfigurazionePerUfficioIntestatario;

/**
 *
 * @author Manuel Bridda
 */
public class ArchiviaDocumentiDigiPTask {

  static Logger                                 logger = Logger.getLogger(ArchiviaDocumentiDigiPTask.class);

  private CreazioneArchivioDocumentiGaraManager creazioneArchivioDocumentiGaraManager;

  private ArchiviazioneDocumentiManager         archiviazioneDocumentiManager;

  private SqlManager                            sqlManager;

  private FileAllegatoManager                   fileAllegatoManager;

  private AnalizzaArchiviazioneDocumentiManager analizzaArchiviazioneDocumentiManager;

  public void setCreazioneArchivioDocumentiGaraManager(CreazioneArchivioDocumentiGaraManager creazioneArchivioDocumentiGaraManager) {
    this.creazioneArchivioDocumentiGaraManager = creazioneArchivioDocumentiGaraManager;
  }

  public void setArchiviazioneDocumentiManager(ArchiviazioneDocumentiManager archiviazioneDocumentiManager) {
    this.archiviazioneDocumentiManager = archiviazioneDocumentiManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }


  public void setAnalizzaArchiviazioneDocumentiManager(AnalizzaArchiviazioneDocumentiManager analizzaArchiviazioneDocumentiManager) {
    this.analizzaArchiviazioneDocumentiManager = analizzaArchiviazioneDocumentiManager;
  }

  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  private final List<String> provenienzaEntrata = Arrays.asList("3","6","8");
  private final List<String> provenienzaUscita = Arrays.asList("1","2","5","9");
  private final List<String> provenienzaInterna = Arrays.asList("4","7");

  /**
   * archiviazione ed esportazione dei documenti di gara/elenco/catalogo
   *
   */
  public void archiviaDocumenti() throws GestoreException {
    if (!WebUtilities.isAppNotReady()) {
      letturaRapportiVersamento();
      fascicolaDocumenti();
      trasferisciDocumenti();
    }
  }

  private void trasferisciDocumenti() throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("Avvio archiviazione dei documenti di gara/elenco/catalogo");

    HashMap<String, Object> params = new HashMap<>();
    params.put("versione", "1.6");

    // Archiviazione documentazione di gara/elenco/catalogo
    Long idArchiviazione = null;
    Long sysconRichiesta = null;
    Long idDoc = null;
    String codgara = null;
    String codice = null;
    String key1Doc = null;
    String key2Doc = null;
    String provenienza = null;
    Long genere = null;


    // INIZIO GESTIONE TRASFERIMENTO A DIGIP
    if (logger.isDebugEnabled())
      logger.debug("Gestione trasferimento a DIGIP: avvio trasferimento ad area FTP");

    List<Long> docInseriti = new ArrayList<>();
    List<Long> docNonInseriti = new ArrayList<>();

    String selRichiesteDaElaborare = "select j.id_archiviazione, j.codgara, j.syscon, j.classifica, j.cod_reg,"
            + " j.tipo_doc, j.mitt_int, j.indice, j.classifica_tit, j.uo_mittdest "
            + " from gardoc_jobs j"
            + " where j.tipo_archiviazione = ? and j.da_processare = ?  ";

    String selDocumentiDaElaborare = "select w.id, w.key1, w.key2, w.provenienza"
            + " from gardoc_wsdm w where w.id_archiviazione = ? and (w.stato_archiviazione = ? ) ";

    List listaRichiesteDaElaborare = null;
    List listaDocumentiDaElaborare = null;
    try {
      listaRichiesteDaElaborare = sqlManager.getListVector(selRichiesteDaElaborare, new Object[]{new Long(6), "1"});

      if (listaRichiesteDaElaborare != null && !listaRichiesteDaElaborare.isEmpty()) {
        for (int h = 0; h < listaRichiesteDaElaborare.size(); h++) {
          genere = null;
          idArchiviazione = SqlManager.getValueFromVectorParam(listaRichiesteDaElaborare.get(h), 0).longValue();
          codgara = SqlManager.getValueFromVectorParam(listaRichiesteDaElaborare.get(h), 1).getStringValue();
          sysconRichiesta = SqlManager.getValueFromVectorParam(listaRichiesteDaElaborare.get(h), 2).longValue();


          String selectCodiceGara = "select codice,genere from v_gare_genere where codgar = ? and genere < 100";
          Vector<?> datiGareGenere = sqlManager.getVector(selectCodiceGara, new Object[]{codgara});
          if (datiGareGenere != null && !datiGareGenere.isEmpty()) {
            codice = SqlManager.getValueFromVectorParam(datiGareGenere, 0).stringValue();
            codice = UtilityStringhe.convertiNullInStringaVuota(codice);
            genere = (Long) SqlManager.getValueFromVectorParam(datiGareGenere, 1).getValue();
          }

          listaDocumentiDaElaborare = sqlManager.getListVector(selDocumentiDaElaborare, new Object[]{idArchiviazione,
                  1L});

          int numDocEsitoNegativo = 0;
          String messaggioApp = null;
          String statoGardocJobs = "2";

          String uffint = (String) sqlManager.getObject("select cenint from torn where codgar=?", new Object[]{codgara});

          getConfig(params, uffint);
          params.put("nomeIndiceSIP",getOptConfigurazionePerUfficioIntestatario("digip.sftp.nomeIndiceSIP",uffint));

          SFTPManager sftp = new SFTPManager();
          sftp.connect(uffint);

          if (listaDocumentiDaElaborare != null && !listaDocumentiDaElaborare.isEmpty()) {
            for (Object o : listaDocumentiDaElaborare) {
              idDoc = SqlManager.getValueFromVectorParam(o, 0).longValue();
              key1Doc = SqlManager.getValueFromVectorParam(o, 1).getStringValue();
              key1Doc = UtilityStringhe.convertiNullInStringaVuota(key1Doc);
              key2Doc = SqlManager.getValueFromVectorParam(o, 2).getStringValue();
              key2Doc = UtilityStringhe.convertiNullInStringaVuota(key2Doc);
              provenienza = SqlManager.getValueFromVectorParam(o, 3).getStringValue();
              provenienza = UtilityStringhe.convertiNullInStringaVuota(provenienza);

              String select = "select dignomdoc from w_docdig where idprg = ? and iddocdig = ?";
              if ("4".equals(provenienza)) {
                select = "select c0anomogg from c0oggass where c0aprg = ? and c0acod = ?";
              }
              String nomeFile = (String) this.sqlManager.getObject(select, new Object[]{key1Doc, key2Doc});
              BlobFile bf;
              byte[] bytes;

              String chiave = "";
              if ("4".equals(provenienza)) {
                // associato su filesystem
                String pathDocumentiAssociati = ConfigManager.getValore("it.eldasoft.documentiAssociati");
                File fileAssociato = new File(pathDocumentiAssociati + "/" + nomeFile);
                bytes = FileUtils.readFileToByteArray(fileAssociato);
                chiave = nomeFile.split("_")[0];
              } else {
                bf = fileAllegatoManager.getFileAllegato(key1Doc, new Long(key2Doc));
                bytes = bf.getStream();
                chiave=key1Doc + StringUtils.leftPad(key2Doc, 10, '0');
              }
              params.put("fileBytes", bytes);
              params.put("nomeFile", nomeFile);
              params.put("idprg", key1Doc);
              params.put("iddocdig", key2Doc);
              params.put("codgar", codgara);
              params.put("genere", genere);
              params.put("provenienza", provenienza);
              params.put("chiave", chiave);


              try {
                byte[] file = creaPacchettoSIP(params);

                sftp.existsDirs("SIP/");
                String nomeSIP = UUID.randomUUID() + ".zip";

                String impronta = archiviazioneDocumentiManager.getImpronta(file);
                if (logger.isDebugEnabled()) logger.debug("Trasferisco il file.");
                sftp.Put(file, "SIP/" + nomeSIP);

                if (logger.isDebugEnabled()) logger.debug("Prelevo il file di per verificarne l'hash.");
                File tempFile = File.createTempFile("tmp", null, null);
                sftp.Get("SIP/" + nomeSIP, new FileSystemFile(tempFile));

                byte[] b2 = FileUtils.readFileToByteArray(tempFile);
                String improntaCheck = archiviazioneDocumentiManager.getImpronta(b2);
                if (impronta.equals(improntaCheck)) {
                  archiviazioneDocumentiManager.updateArchiviatoConChiaveDIGIP(idDoc, idArchiviazione, nomeSIP, (String) params.get("chiaveSIP"));
                  docInseriti.add(idDoc);
                  if (logger.isDebugEnabled()) logger.debug("File trasferito correttamente.");
                } else {
                  archiviazioneDocumentiManager.updateStatoEsito(2L, "Errore nel trasferimento del documento su area FTP: file non presente in area FTP", idDoc, idArchiviazione);
                  docNonInseriti.add(idDoc);
                  sftp.Remove("SIP/" + nomeSIP);
                  if (logger.isDebugEnabled()) logger.debug("File " + "SIP/" + nomeSIP + " rimosso");
                }
              } catch (Exception ex) {
                archiviazioneDocumentiManager.updateStatoEsito(2L, "Errore nel trasferimento del documento su area FTP: " + ex, idDoc, idArchiviazione);
                docNonInseriti.add(idDoc);
                sftp = new SFTPManager();
                sftp.connect(uffint);
              }
            }
          }
          sftp.disconnect();
          if (!docNonInseriti.isEmpty()) {
            // aggiungere altre precisazioni
            messaggioApp = "La richiesta non risulta elaborata correttamente";
          }

          // aggiorno la riga di archiviazione
          archiviazioneDocumentiManager.updateJobArchiviazioneDocumenti(idArchiviazione, statoGardocJobs, numDocEsitoNegativo,
                  messaggioApp, codgara, codice, genere, sysconRichiesta);

          // aggiorno la riga di archiviazione
          if (genere == null)//stipule
            logger.debug("Archiviazione di una stipula verso DigiP?! codgara=" + codgara);
            //analizzaArchiviazioneDocumentiManager.analizzaEntita(null,new Long(codgara)); //teoricamente non entrerò mai qui, non si possono archiviare doc. di stipule a DIGIP (12/04/2023)
          else//gare/avvisi/elenchi
            analizzaArchiviazioneDocumentiManager.analizzaEntita(codgara, null);

          String errMsg = null;
          int livEvento = 1;
          if (!docNonInseriti.isEmpty()) {
            errMsg = "Trasferimento ad area ftp fallito per i seguenti documenti (GARDOC_WSDM.ID): " + docNonInseriti;
            livEvento = 3;
          }
          try {
            LogEvento logEvento = new LogEvento();
            logEvento.setCodApplicazione("PG");
            logEvento.setOggEvento(codgara.replace("$", ""));
            logEvento.setLivEvento(livEvento);
            logEvento.setCodEvento("GA_DIGIP_TRASFERIMENTOFTP");
            logEvento.setDescr("Trasferimento ad area FTP (GARDOC_JOBS.ID_ARCHIVIAZIONE=" + idArchiviazione + ")");
            logEvento.setErrmsg(errMsg);
            LogEventiUtils.insertLogEventi(logEvento);
          } catch (Exception le) {
            logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
          }
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della lista dei documenti da trasferire a DIGIP", null, e);
    } catch (Exception e) {
      throw new GestoreException("Errore nella creazione del pacchetto SIP da trasferire a DIGIP", null, e);
    }
    if (logger.isDebugEnabled()) logger.debug("Gestione trasferimento a DIGIP: fine trasferimento in area FTP");
  }

  private void letturaRapportiVersamento() throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("Avvio lettura di versamento dei documenti per DigiP");

    List<String> gareInErrore = new ArrayList<>();
    List<String> gareOk = new ArrayList<>();

    String selDocumentiDaVerificare = "select j.codgara, w.id, j.id_archiviazione, w.nome_sip"
            + " from gardoc_wsdm w, gardoc_jobs j where w.id_archiviazione = j.id_archiviazione and j.tipo_archiviazione = ? and (w.stato_archiviazione = ? ) order by j.codgara";
    try {
      List listaRichiesteDaVerificare = sqlManager.getListVector(selDocumentiDaVerificare, new Object[]{6L, 4L});
      List<String> files = new ArrayList<>();
      if (listaRichiesteDaVerificare != null && !listaRichiesteDaVerificare.isEmpty()) {
        String oldCodgar = null;
        String uffint = null;
        String oldUffint = null;
        SFTPManager sftp = new SFTPManager();
        for (Object o : listaRichiesteDaVerificare) {
          try {
            String codgara = SqlManager.getValueFromVectorParam(o, 0).getStringValue();
            Long idgardocwsdm = SqlManager.getValueFromVectorParam(o, 1).longValue();
            Long idarchigardocwsdm = SqlManager.getValueFromVectorParam(o, 2).longValue();
            String nomeSip = SqlManager.getValueFromVectorParam(o, 3).stringValue();

            String codiceGara = codgara.startsWith("$")? codgara.substring(1) : codgara;

            if (codgara != null && !codgara.equals(oldCodgar)) {//se cambia codgar, verifico se cambia uffint
              oldCodgar = codgara;
              uffint = (String) sqlManager.getObject("select cenint from torn where codgar=?", new Object[]{codgara});
            }
            if (uffint != null && !uffint.equals(oldUffint)) {//cambio connessione se cambia l'uffint: potrebbe avere una configurazione diversa
              if (oldUffint != null) {
                sftp.disconnect();
                sftp = new SFTPManager();
              }
              oldUffint = uffint;
              sftp.connect(uffint);
              files = sftp.GetFiles("RDV");
              if (logger.isDebugEnabled()) logger.debug("Lettura della directory ftp avvenuta con successo.");
            }
            String nomeRDC = "RDC_" + nomeSip + ".csv";
            if (files.contains(nomeRDC)) {
              File tempFile = File.createTempFile("tmp", null, null);
              sftp.Get("RDV/" + nomeRDC, new FileSystemFile(tempFile));

              BufferedReader br = new BufferedReader(new FileReader(tempFile));
              String headerLine = br.readLine();
              String[] headers = headerLine.split(",");
              // Trova l'indice delle colonne "Codice_Esito" e "Messaggio"
              int codiceEsitoIndex = -1;
              int messaggioIndex = -1;
              int id_sipIndex = -1;
              int nome_sipIndex = -1;
              for (int i = 0; i < headers.length; i++) {
                if ("\"Codice_Esito\"".equals(headers[i])) {
                  codiceEsitoIndex = i;
                } else if ("\"Messaggio\"".equals(headers[i])) {
                  messaggioIndex = i;
                } else if ("\"ID_SIP\"".equals(headers[i])) {
                  id_sipIndex = i;
                } else if ("\"Nome_SIP\"".equals(headers[i])) {
                  nome_sipIndex = i;
                }
              }
              // Verifica se entrambe le colonne sono state trovate
              if (codiceEsitoIndex != -1 && messaggioIndex != -1) {
                // Leggi la prima riga di dati
                String dataLine = br.readLine();
                String[] values = dataLine.split(",");
                if ("\"ERROR\"".equals(values[codiceEsitoIndex])) {
                  archiviazioneDocumentiManager.updateStatoEsito(7L, values[messaggioIndex], idgardocwsdm, idarchigardocwsdm);
                  sftp.Remove("RDV/" + nomeRDC);
                  if (logger.isDebugEnabled()) logger.debug("Rapporto letto, esito negativo - documento rimosso dall'area FTP e occorrenza in db aggiornata");
                  if(!gareInErrore.contains(codiceGara))
                    gareInErrore.add(codiceGara);
                } else if ("\"OK\"".equals(values[codiceEsitoIndex]) || "\"WARN\"".equals(values[codiceEsitoIndex])) {
                  String idSIP = values[id_sipIndex];
                  String nomeSIP = values[nome_sipIndex];
                  String esito = null;
                  if ("\"WARN\"".equals(values[codiceEsitoIndex]))
                    esito = "Errore non bloccante sul documento caricato: " + values[messaggioIndex];
                  if (StringUtils.isNotBlank(idSIP) && StringUtils.isNotBlank(nomeSIP)) {
                    idSIP=idSIP.substring(1, idSIP.length() - 1);
                    nomeSIP=nomeSIP.substring(1, nomeSIP.length() - 1);
                    archiviazioneDocumentiManager.updateStatoArchiviazioneDocumentoDIGIP(idgardocwsdm, idarchigardocwsdm, 5L, nomeSIP , idSIP , esito);
                    sftp.Remove("RDV/" + nomeRDC);
                    if (files.contains("RDV_"+idSIP + ".xml"))
                      sftp.Remove("RDV/RDV_"+idSIP+".xml");//faccio pulizia se non servono più
                    if (files.contains("RDV_"+idSIP + ".xml.p7m"))
                      sftp.Remove("RDV/RDV_"+ idSIP+".xml.p7m");//faccio pulizia se non servono più
                    if(!gareOk.contains(codiceGara))
                      gareOk.add(codiceGara);
                  }
                }
              } else {
                archiviazioneDocumentiManager.updateStatoEsito(7L, "Esito di elaborazione non trovato", idgardocwsdm, idarchigardocwsdm);
                sftp.Remove("RDV/" + nomeRDC);
                if(!gareInErrore.contains(codiceGara))
                  gareInErrore.add(codiceGara);
              }
            }
          } catch (IOException e) {
            throw new GestoreException("Errore nella lettura dei rapporti di versamento DIGIP", null, e);
          }
        }
      }
      if(!gareOk.isEmpty() || !gareInErrore.isEmpty()) {
        try {
          LogEvento logEvento = new LogEvento();
          logEvento.setCodApplicazione("PG");
          logEvento.setLivEvento(gareInErrore.isEmpty()?1:3);
          logEvento.setCodEvento("GA_DIGIP_LETTURAVERSAMENTI");
          logEvento.setDescr("Lettura esiti di versamento per conservazione DigiP");
          logEvento.setErrmsg((!gareOk.isEmpty()?"Documenti trasferiti in area FTP per le seguenti procedure: "+String.join(",", gareOk) : "")
                  + (!gareInErrore.isEmpty()?"\nTrasferimenti in area FTP falliti per le seguenti procedure: " + String.join(",", gareInErrore) : ""));
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }
      }
    } catch (Exception e) {
      throw new GestoreException("Errore nella gestione del processo di lettura dei rapporti di versamento DIGIP", null, e);
    }
    if (logger.isDebugEnabled())
      logger.debug("Fine lettura di versamento dei documenti per DigiP");
  }

  private void fascicolaDocumenti() throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("Avvio fascicolazione dei documenti per DigiP");

    List<String> gareInErrore = new ArrayList<>();
    List<String> gareOk = new ArrayList<>();
    HashMap<String, Object> params = new HashMap<>();
    params.put("versione", "2.0");

    String selDocumentiDaVerificare = "select distinct j.codgara, t.cenint, v.codice, f.codice, v.oggetto, t.idappaltonpa, t.idpianinpa, v.codcig from gardoc_wsdm w join gardoc_jobs j on w.id_archiviazione = j.id_archiviazione join torn t on t.codgar=j.codgara " +
            "join v_gare_torn v on v.codgar = j.codgara  left join wsfascicolo f on v.codice=f.key1 where j.tipo_archiviazione = ? and w.stato_archiviazione in (5,9) and w.chiave_sip is not null order by j.codgara";
    try {
      List gareDaFascicolare = sqlManager.getListVector(selDocumentiDaVerificare, new Object[]{6L});
      if (gareDaFascicolare != null && !gareDaFascicolare.isEmpty()) {
        String codiceGara="";
        for (Object o : gareDaFascicolare) {
          try {
            String codgara = SqlManager.getValueFromVectorParam(o, 0).getStringValue();
            String uffint = SqlManager.getValueFromVectorParam(o, 1).stringValue();
            codiceGara = SqlManager.getValueFromVectorParam(o, 2).stringValue();
            String codiceFascicolo = SqlManager.getValueFromVectorParam(o, 3).stringValue();
            String oggettoGara = SqlManager.getValueFromVectorParam(o, 4).stringValue();
            String idAppalto = SqlManager.getValueFromVectorParam(o, 5).stringValue();
            String idPiani = SqlManager.getValueFromVectorParam(o, 6).stringValue();
            String cig = SqlManager.getValueFromVectorParam(o, 7).stringValue();

            getConfig(params, uffint);
            params.put("descClassificazione", getOptConfigurazionePerUfficioIntestatario("digip.sftp.classificazione", uffint));
            params.put("tempoConservazione", getOptConfigurazionePerUfficioIntestatario("digip.sftp.tempoConservazione", uffint));
            params.put("urlFascicolazione",getOptConfigurazionePerUfficioIntestatario("digip.sftp.fascicolazione.url",uffint));
            params.put("loginDigiP",getOptConfigurazionePerUfficioIntestatario("conservazione.sftp.login",uffint));
            params.put("passwordDigiP",getOptConfigurazionePerUfficioIntestatario("conservazione.sftp.password",uffint));
            params.put("codgar", codgara);
            params.put("codiceFascicolo", codiceFascicolo);
            params.put("codiceGara", codiceGara);
            params.put("idAppalto", idAppalto);
            params.put("idPiani", idPiani);
            params.put("cig", cig);
            params.put("oggetto", oggettoGara);

            String selDocumentiDaFascicolare = "select w.chiave_sip, w.stato_archiviazione, w.chiave_fascicolo from gardoc_wsdm w join gardoc_jobs j on w.id_archiviazione = j.id_archiviazione " +
                    "where j.codgara=? and j.tipo_archiviazione = ? and w.stato_archiviazione in (5,6,9) and w.chiave_sip is not null order by stato_archiviazione desc";
            List documenti = sqlManager.getListVector(selDocumentiDaFascicolare, new Object[]{codgara, 6L});
            if (documenti != null && !documenti.isEmpty()) {
              params.put("datiDocumenti", documenti);
              Long statoArchiviazione = SqlManager.getValueFromVectorParam(documenti.get(0), 1).longValue();
              boolean fascicoloNuovo= new Long(5).equals(statoArchiviazione);
              if(!fascicoloNuovo){
                params.put("chiaveFascicolo",SqlManager.getValueFromVectorParam(documenti.get(0), 2).stringValue());
              }
              params.put("fascicoloNuovo", fascicoloNuovo);
              params.put("bytesPacchetto",creaPacchettoAIC(params));
              String xmlString = inviaPacchetto(params);
              if(StringUtils.isNotBlank(xmlString)){
                // Creazione di un Document XML
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new ByteArrayInputStream(xmlString.getBytes()));

                // Normalizzare il documento
                document.getDocumentElement().normalize();

                // Estrazione dei valori dei tag
                NodeList esitoGeneraleList = document.getElementsByTagName("EsitoGenerale");
                if (esitoGeneraleList.getLength() > 0) {
                  Element esitoGenerale = (Element) esitoGeneraleList.item(0);
                  String codiceEsito = getTagValue("CodiceEsito", esitoGenerale);
                  if("POSITIVO".equals(codiceEsito)){
                    String idSIP = getTagValue("IdSIP", document);
                    archiviazioneDocumentiManager.updateStatoFascicolazioneDocumentiDIGIP(idSIP, (String) params.get("chiaveFascicolo"),codgara);
                    gareOk.add(codiceGara);
                  }else {
                    String codiceErrore = getTagValue("CodiceErrore", esitoGenerale);
                    String messaggioErrore = getTagValue("MessaggioErrore", esitoGenerale);
                    archiviazioneDocumentiManager.updateStatoNegativoFascicolazioneDocumentiDIGIP((String) params.get("chiaveFascicolo"),codiceErrore+" - "+messaggioErrore,codgara);
                    gareInErrore.add(codiceGara);
                  }
                }
              }
            }
          } catch (Exception e) {
            gareInErrore.add(codiceGara);
            logger.error("Errore nella fascicolazione dei documenti per DigiP: "+e);
          }
        }
      }
      if(!gareOk.isEmpty() || !gareInErrore.isEmpty()) {
        try {
          LogEvento logEvento = new LogEvento();
          logEvento.setCodApplicazione("PG");
          logEvento.setLivEvento(gareInErrore.isEmpty()?1:3);
          logEvento.setCodEvento("GA_DIGIP_FASCICOLAZIONE");
          logEvento.setDescr("Fascicolazione documenti per conservazione DigiP");
          logEvento.setErrmsg((!gareOk.isEmpty()?"Fascicolazione riuscita per le seguenti procedure: "+String.join(",", gareOk) : "")
                  + (!gareInErrore.isEmpty()?"\nFallita per le seguenti procedure: " + String.join(",", gareInErrore) : ""));
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        }
      }
    } catch (Exception e) {
      throw new GestoreException("Errore nella gestione del processo di fascicolazione dei documenti DIGIP", null, e);
    }

    if (logger.isDebugEnabled())
      logger.debug("Fine fascicolazione dei documenti per DigiP");
  }

  private String inviaPacchetto(HashMap<String, Object> params) throws GestoreException, IOException, CriptazioneException {
    String xmlString = null;
    FormDataMultiPart multipart = new FormDataMultiPart();
    multipart.field("VERSIONE", (String) params.get("versione"), MediaType.TEXT_PLAIN_TYPE);
    multipart.field("LOGINNAME", validatePropertyValorizzata("loginDigiP", params), MediaType.TEXT_PLAIN_TYPE);
    ICriptazioneByte icb = FactoryCriptazioneByte.getInstance(ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
            validatePropertyValorizzata("passwordDigiP", params).getBytes(), it.eldasoft.utils.sicurezza.ICriptazioneByte.FORMATO_DATO_CIFRATO);
    String passwordDecoded = new String(icb.getDatoNonCifrato());
    multipart.field("PASSWORD",passwordDecoded, MediaType.TEXT_PLAIN_TYPE);

    // Convertire il byte[] in un InputStream per l'XML
    byte[] xmlBytes = (byte[]) params.get("bytesPacchetto");
    ByteArrayInputStream xmlInputStream = new ByteArrayInputStream(xmlBytes);
    StreamDataBodyPart xmlPart = new StreamDataBodyPart("XMLSIP", xmlInputStream, "xmlsip.xml", MediaType.APPLICATION_XML_TYPE);
    multipart.bodyPart(xmlPart);

    Client client = ClientBuilder.newBuilder().register(org.glassfish.jersey.media.multipart.MultiPartFeature.class).build();
    client.property(ClientProperties.CONNECT_TIMEOUT, 60000);

    //nuovo fascicolo
    String path = "/modificaFascicoloInformatico";
    if((boolean) params.get("fascicoloNuovo"))
      path="/versamentoFascicoloInformatico";
    WebTarget target = client.target(validatePropertyValorizzata("urlFascicolazione", params)+path);
    Invocation.Builder requestBuilder = target.request(MediaType.APPLICATION_XML_TYPE);

    Response response = requestBuilder.post(Entity.entity(multipart, multipart.getMediaType()));
    if(Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())){
      xmlString = response.readEntity(String.class);
    }

    // Cleanup delle risorse
    multipart.close();
    xmlInputStream.close();
    client.close();

    return xmlString;
  }

  private static String getTagValue(String tag, Document document) {
    NodeList nodeList = document.getElementsByTagName(tag);
    if (nodeList.getLength() > 0) {
      return nodeList.item(0).getTextContent();
    }
    return "N/A"; // Se il tag non esiste
  }
  // Metodo per ottenere il valore di un nodo specifico
  private static String getTagValue(String tag, Element element) {
    NodeList nodeList = element.getElementsByTagName(tag);
    if (nodeList.getLength() > 0) {
      return nodeList.item(0).getTextContent();
    }
    return "N/A"; // Se il tag non esiste
  }

  private void getConfig(HashMap<String, Object> params, String uffint) {
    params.put("ambiente", getOptConfigurazionePerUfficioIntestatario("digip.sftp.ambiente",uffint));
    params.put("struttura", getOptConfigurazionePerUfficioIntestatario("digip.sftp.struttura",uffint));
    params.put("ente", getOptConfigurazionePerUfficioIntestatario("digip.sftp.ente",uffint));
    params.put("userid", getOptConfigurazionePerUfficioIntestatario("digip.sftp.userid",uffint));
    params.put("tipoRegistro", getOptConfigurazionePerUfficioIntestatario("digip.sftp.tipoRegistro",uffint));
  }

  private List<CampiDocumento> getDocumentDataByProvenienza(String codgar, String key1, String key2, String provenienza, Long genere) throws Exception {
    List data = null;
    String oggetto = "gara";
    if (genere != null) {
      if (new Long(10).equals(genere)) {
        oggetto = "elenco operatori";
      } else if (new Long(20).equals(genere)) {
        oggetto = "catalogo elettronico";
      }else if (new Long(11).equals(genere)) {
        oggetto = "avviso";
      }
    }
    String where = " w.idprg=? and w.iddocdig=? and ";
    if("4".equals(provenienza) || "7".equals(provenienza))
      where = " c.c0aprg = ? and c.c0acod=? and ";
    switch (provenienza) {
      case "1": //DOCUMGARA
        data = this.sqlManager.getListVector(creazioneArchivioDocumentiGaraManager.getQueryDocGara().replace("where", "where"+where), new Object[]{key1,new Long(key2),codgar});
        break;
      case "2": //COMUNICAZIONI
        data = this.sqlManager.getListVector(creazioneArchivioDocumentiGaraManager.getQueryDocComunicazioni().replace("where", "where"+where), new Object[]{key1,new Long(key2),codgar});
        break;
      case "3": //GARE DOC DITTA
        data = this.sqlManager.getListVector(creazioneArchivioDocumentiGaraManager.getQueryDocDitta().replace("where", "where"+where), new Object[]{key1,new Long(key2),codgar});
        break;
      case "4":// //C0ASS DA FS
        data = this.sqlManager.getListVector(String.join( "where"+where, creazioneArchivioDocumentiGaraManager.getQueryDocAssociatiFS().split("where")), new Object[] {key1,new Long(key2),codgar,key1,new Long(key2),codgar});
        break;
      case "5": //COMUNICAZIONI ALLA DITTA
        data = this.sqlManager.getListVector(creazioneArchivioDocumentiGaraManager.getQueryDocComAllaDitta(false).replace("where", "where"+where), new Object[]{key1,new Long(key2),codgar});
        break;
      case "6": //COMUNICAZIONI DALLA DITTA
        if(new Long(1).equals(genere))
          data = this.sqlManager.getListVector(creazioneArchivioDocumentiGaraManager.getQueryDocComDallaDittaTorn().replace("where", "where"+where), new Object[]{key1,new Long(key2),codgar});
        else
          data = this.sqlManager.getListVector(creazioneArchivioDocumentiGaraManager.getQueryDocComDallaDittaGare().replace("where", "where"+where), new Object[]{key1,new Long(key2),codgar});
        break;
      case "7": //C0ASS DA DB
        data = this.sqlManager.getListVector(String.join( "where"+where, creazioneArchivioDocumentiGaraManager.getQueryDocAssociatiDB().split("where")), new Object[]{key1,new Long(key2),codgar,key1,new Long(key2),codgar});
        break;
      case "8": //RIEPILOGO BUSTE
        data = this.sqlManager.getListVector(creazioneArchivioDocumentiGaraManager.getQueryDocRiepilogoBuste().replace("where", "where"+where), new Object[]{key1,new Long(key2),codgar});
        break;
      case "9": //PUBB ATTI OFFERTE
        data = this.sqlManager.getListVector(creazioneArchivioDocumentiGaraManager.getQueryDocPubbAtti().replace("where", "where"+where), new Object[]{key1,new Long(key2),codgar});
        break;
    }

    List<CampiDocumento> listaCampiDocumento = new ArrayList<>();
    if(data != null && !data.isEmpty())
      for(int i=0; i<data.size();i++){
        listaCampiDocumento.add(creazioneArchivioDocumentiGaraManager.popolaCampiDocumento(data, i, provenienza, oggetto));
      }
    return listaCampiDocumento;
  }

  private String validatePropertyValorizzata(String prop, HashMap<String,Object> hm) throws GestoreException {
    String value = (String) hm.get(prop);
    if(StringUtils.isBlank(value)){
      throw new GestoreException("Non risulta valorizzata la property: "+prop,null);
    }
    return value;
  }


  private byte[] creaPacchettoSIP(HashMap<String, Object> params) throws Exception {

    String codgar= (String) params.get("codgar");
    Long genere = (Long) params.get("genere");
    String provenienza = (String) params.get("provenienza");
    String nomeFile = (String) params.get("nomeFile");
    String idprg = (String) params.get("idprg");
    String iddocdig = (String) params.get("iddocdig");

    byte[] bytes = (byte[]) params.get("fileBytes");

    //inizializzazione data documento
    List<CampiDocumento> listacampi = getDocumentDataByProvenienza(codgar,idprg,iddocdig,provenienza,genere);
    CampiDocumento campi = listacampi.get(0);

    //dati aggiuntivi da recuperare
    Map<String, String> infoAmm = creazioneArchivioDocumentiGaraManager.getInfoAmm(false,codgar);
    NominativiPerMetaXml nominativi = new NominativiPerMetaXml(sqlManager);
    Map<String,String> nom =  nominativi.getNominativo(codgar,false);
    Map<String,Object> infoFile = creazioneArchivioDocumentiGaraManager.getEstensionePerIdentificativoFormato(nomeFile);
    String identificativoDelFormato = (String) infoFile.get("estensione");
    byte[] improntaBytes = creazioneArchivioDocumentiGaraManager.getImpronta(bytes);

    String dataStr = (String) campi.get("data");
    String numeroProtocollo = (String) campi.get("protocollo");
    Calendar data = Calendar.getInstance();
    if (dataStr.isEmpty()) {
      data.setTimeInMillis( System.currentTimeMillis() );
    } else {
      data.setTime(UtilityDate.convertiData(dataStr, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
    }

    String chiaveDocumento = idprg + iddocdig + "_" + nomeFile;

    // Creazione del xml
    UnitaDocumentariaDocument unita = UnitaDocumentariaDocument.Factory.newInstance();
    UnitaDocumentariaDocument.UnitaDocumentaria unitaDoc = unita.addNewUnitaDocumentaria();

    //INTESTAZIONE
    IntestazioneDocumentariaType intestazione = unitaDoc.addNewIntestazione();
    intestazione.setVersione((String) params.get("versione"));
    //versatore
    VersatoreType versatore = intestazione.addNewVersatore();
    versatore.setAmbiente(validatePropertyValorizzata("ambiente",params));
    versatore.setEnte(validatePropertyValorizzata("ente",params));
    versatore.setStruttura(validatePropertyValorizzata("struttura",params));
    versatore.setUserID(validatePropertyValorizzata("userid",params));
    //chiave
    ChiaveDocumentariaType chiave = intestazione.addNewChiave();
    chiave.setNumero((String) params.get("chiave"));
    chiave.setAnno(data.get(Calendar.YEAR));
    chiave.setTipoRegistro(validatePropertyValorizzata("tipoRegistro",params));
    params.put("chiaveSIP",params.get("chiave") +"|"+ data.get(Calendar.YEAR)+"|"+validatePropertyValorizzata("tipoRegistro",params)); //mi serve per l'unità archivistica
    //tipologiaUnitaDocumentaria
    intestazione.setTipologiaUnitaDocumentaria((String) campi.get("gruppo"));
    //codiceUnitaOrganizzativa -
    //intestazione.setCodiceUnitaOrganizzativa(null); non valorizzare

    //PROFILO UNITA DOCUMENTARIA
    ProfiloUnitaDocumentariaType profilo = unitaDoc.addNewProfiloUnitaDocumentaria();
    profilo.setOggetto((String) campi.get("descrizione"));
    profilo.setData(data);
    //profilo.setCartaceo(); omettere
    //profilo.setParoleChiaveArray(); omettere
    profilo.setRiservato(true);
    //profilo.setTempoDiConservazione(); omettere
    //profilo.setNote(); omettere

    //ID UNITA DOCUMENTARIA
    IdDocType id = unitaDoc.addNewIdUnitaDocumentaria();
    //impronta
    IdDocType.ImprontaCrittograficaDelDocumento impronta = id.addNewImprontaCrittograficaDelDocumento();
    impronta.setAlgoritmo("SHA-256");
    impronta.setImpronta(improntaBytes);
    id.setIdentificativo(chiaveDocumento);
    //id.setSegnatura(); omettere

    //DATI DI REGISTRAZIONE
    DatiDiRegistrazioneType datiRegistrazione = unitaDoc.addNewDatiDiRegistrazione();
    //tipologia di flusso
    if (provenienzaUscita.contains(provenienza)) {
      datiRegistrazione.setTipologiaDiFlusso(TipologiaDiFlussoType.U);
    }else if (provenienzaInterna.contains(provenienza)) {
      datiRegistrazione.setTipologiaDiFlusso(TipologiaDiFlussoType.I);
    }else if (provenienzaEntrata.contains(provenienza)){
      datiRegistrazione.setTipologiaDiFlusso(TipologiaDiFlussoType.E);
    }
    //tipoRegistro
    TipoRegistroType tipoRegistro = datiRegistrazione.addNewTipoRegistro();
    if(StringUtils.isNotBlank(numeroProtocollo)){
      ProtocolloType protocollo = tipoRegistro.addNewProtocolloOrdinarioProtocolloEmergenza();
      protocollo.setTipoRegistro("ProtocolloOrdinario\\ProtocolloEmergenza");
      protocollo.setDataProtocollazioneDocumento(data);
      protocollo.setOraProtocollazioneDocumento(data);
      protocollo.setNumeroProtocolloDocumento(StringUtils.leftPad(numeroProtocollo, 7, '0'));
      protocollo.setCodiceRegistro("PG");
    }else{
      NoProtocolloType noprotocollo = tipoRegistro.addNewRepertorioRegistro();
      noprotocollo.setTipoRegistro("Repertorio\\Registro");
      noprotocollo.setDataRegistrazioneDocumento(data);
      noprotocollo.setOraRegistrazioneDocumento(data);
      noprotocollo.setNumeroRegistrazioneDocumento(idprg + iddocdig);
      noprotocollo.setCodiceRegistro("PG");
    }

    //SOGGETTI
    SoggettiDocumentariaType soggetti = unitaDoc.addNewSoggetti();
    //ruolo amm
    RuoloDocumentariaType ruoloAmm = soggetti.addNewRuolo();
    TipoSoggetto1DocumentariaType tiposoggetto =  ruoloAmm.addNewAmministrazioneCheEffettuaLaRegistrazione();
    tiposoggetto.setTipoRuolo("Amministrazione Che Effettua La Registrazione");
    PAIType pai = tiposoggetto.addNewPAI();

    CodiceIPAType codiceIPA =  pai.addNewIPAAmm();
    codiceIPA.setDenominazione(infoAmm.get("denominazione"));
    codiceIPA.setCodiceIPA(infoAmm.get("codiceIPA"));

    CodiceIPAType codiceAOO = pai.addNewIPAAOO();
    codiceAOO.setDenominazione(infoAmm.get("denominazione"));
    codiceAOO.setCodiceIPA(infoAmm.get("codiceIPA"));

    pai.setIndirizziDigitaliDiRiferimentoArray(creazioneArchivioDocumentiGaraManager.getInfoAmmIndirizziDigitaliDiRiferimento(infoAmm));

    //ruolo mittente
    RuoloDocumentariaType ruoloMittente = soggetti.addNewRuolo();
    TipoSoggetto32Type mitt = ruoloMittente.addNewMittente();
    mitt.setTipoRuolo("Mittente");
    if(provenienzaEntrata.contains(provenienza)){
      String cfDitta = UtilityStringhe.convertiNullInStringaVuota( (String) campi.get("cfDitta") );
      String[] dati = creazioneArchivioDocumentiGaraManager.getDatiImpr(cfDitta);
      if (dati[0]!=null) {
        if ("6".equals(dati[0])){
          PFDocumentariaType pf = mitt.addNewPF();
          pf.setCognome(dati[2]);
          pf.setNome(dati[3]);
        } else {
          PGType pg = mitt.addNewPG();
          pg.setDenominazioneOrganizzazione(dati[4]);
          pg.setCodiceFiscalePartitaIva(dati[1]);
        }
      }
    }else{
      PGType pg = mitt.addNewPG();
      pg.setDenominazioneOrganizzazione(nom.get("nomein"));
      pg.setCodiceFiscalePartitaIva(nom.get("cfein"));
    }
    //ruolo destinatari (solo comunicazione alla ditta / dalla ditta
    if("6".equals(provenienza)) {
      RuoloDocumentariaType ruolodestinario = soggetti.addNewRuolo();
      TipoSoggetto31Type dest = ruolodestinario.addNewDestinatario();
      dest.setTipoRuolo("Destinatario");
      PGType pg =  dest.addNewPG();
      pg.setDenominazioneOrganizzazione(nom.get("nomein"));
      pg.setCodiceFiscalePartitaIva(nom.get("cfein"));
    } else if("5".equals(provenienza)){
      for (CampiDocumento campiDocumento : listacampi) {
        RuoloDocumentariaType ruolodestinario = soggetti.addNewRuolo();
        TipoSoggetto31Type dest = ruolodestinario.addNewDestinatario();
        dest.setTipoRuolo("Destinatario");
        if ("TECNI".equals(campiDocumento.get("entita"))) {
          Vector datiTecnico = sqlManager.getVector("select cogtei,nometei from tecni where codtec = ?", new Object[]{campiDocumento.get("descodsog")});
          if (datiTecnico != null && !datiTecnico.isEmpty()) {
            PFDocumentariaType pf = dest.addNewPF();
            pf.setCognome(SqlManager.getValueFromVectorParam(datiTecnico, 0).getStringValue());
            pf.setNome(SqlManager.getValueFromVectorParam(datiTecnico, 1).getStringValue());
          }
        } else {
          String[] dati = creazioneArchivioDocumentiGaraManager.getDatiImpr((String) campiDocumento.get("cfDitta"));
          if (dati[0] != null) {
            if ("6".equals(dati[0])) {
              PFDocumentariaType pf = dest.addNewPF();
              pf.setCognome(dati[2]);
              pf.setNome(dati[3]);
            } else {
              PGType pg = dest.addNewPG();
              pg.setDenominazioneOrganizzazione(dati[4]);
              pg.setCodiceFiscalePartitaIva(dati[1]);
            }
          }
        }
      }
    }

    //DOCUMENTI COLLEGATI
    //TODO

    //DOCUMENTO PRINCIPALE
    DocumentoType documento = unitaDoc.addNewDocumentoPrincipale();
    documento.setIDDocumento(chiaveDocumento);
    documento.setTipoDocumento((String) campi.get("gruppo"));
    //profilo documento
    ProfiloDocumentoType profiloDoc = documento.addNewProfiloDocumento();
    profiloDoc.setDescrizione((String) campi.get("descrizione"));
    //struttura originale
    StrutturaType struttura = documento.addNewStrutturaOriginale();
    struttura.setTipoStruttura("DocumentoGenerico");
    //componenti (unico)
    StrutturaType.Componenti componenti = struttura.addNewComponenti();
    ComponenteType componente = componenti.addNewComponente();
    componente.setID("1");
    componente.setOrdinePresentazione(1);
    componente.setTipoComponente("Contenuto");
    componente.setTipoSupportoComponente(TipoSupportoType.FILE);
    componente.setNomeComponente(nomeFile);
    componente.setFormatoFileVersato(identificativoDelFormato);
    componente.setHashVersato(javax.xml.bind.DatatypeConverter.printHexBinary(improntaBytes));
    componente.setAlgoritmo("SHA-256");

    XmlOptions options = new XmlOptions();
    options.setSavePrettyPrint();
    options.setSavePrettyPrintIndent(2);

    // Salvataggio dell'XML in memoria
    ByteArrayOutputStream xmlOutputStream = new ByteArrayOutputStream();
    unita.save(xmlOutputStream, options);
    byte[] xmlBytes = xmlOutputStream.toByteArray();

    // Creazione dello ZIP in memoria
    ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(zipOutputStream);
    zos.setMethod(ZipOutputStream.DEFLATED);
    zos.setLevel(Deflater.DEFAULT_COMPRESSION);

    zos.putNextEntry(new ZipEntry(validatePropertyValorizzata("nomeIndiceSIP",params)));
    zos.write(xmlBytes);
    zos.closeEntry();

    zos.putNextEntry(new ZipEntry("1"));
    zos.write(bytes);
    zos.flush();
    zos.closeEntry();
    zos.close();

    return  zipOutputStream.toByteArray();
  }

  private byte[] creaPacchettoAIC(HashMap<String, Object> params) throws Exception {

    String codgar= (String) params.get("codgar");
    String codiceGara = (String) params.get("codiceGara");
    String codiceFascicolo = (String) params.get("codiceFascicolo");
    String idAppalto = (String) params.get("idAppalto");
    String idPiani = (String) params.get("idPiani");
    String cig = (String) params.get("cig");

    String chiave = codiceGara;
    String indice = codiceGara;
    String progressivo = "1";
    if(StringUtils.isNotBlank(codiceFascicolo)) {
      chiave = codiceFascicolo;
      indice = codiceFascicolo.split("/")[0];
      if(codiceFascicolo.split("/").length > 3) {
        progressivo = codiceFascicolo.split("/")[3];
      }
    }
    String chiavePregressa = (String) params.get("chiaveFascicolo");
    if(StringUtils.isNotBlank(chiavePregressa))
      chiave=chiavePregressa;
    else
      params.put("chiaveFascicolo",chiave);

    //dati aggiuntivi da recuperare
    Map<String, String> infoAmm = creazioneArchivioDocumentiGaraManager.getInfoAmm(false,codgar);
    Calendar data = Calendar.getInstance();
    data.setTimeInMillis(System.currentTimeMillis());
    List datepub = sqlManager.getListVector("select g.dacqcig, p.datpub from pubbli p left join gare g on g.codgar1=p.codgar9 where p.codgar9=? and (g.dacqcig is not null or p.datpub is not null) order by g.dacqcig,p.datpub", new Object[]{codgar});
    if(datepub!=null && !datepub.isEmpty()) {
      Date dacqcig = SqlManager.getValueFromVectorParam(datepub.get(0),0).dataValue();
      Date datpub = SqlManager.getValueFromVectorParam(datepub.get(0),1).dataValue();
      if (dacqcig!=null) {
        data.setTimeInMillis(dacqcig.getTime());
      } else if (datpub!=null) {
        data.setTimeInMillis(datpub.getTime());
      }
    }

    // Creazione del xml
    UnitaArchivisticaDocument unita = UnitaArchivisticaDocument.Factory.newInstance();
    UnitaArchivisticaDocument.UnitaArchivistica unitaArch = unita.addNewUnitaArchivistica();

    //INTESTAZIONE
    IntestazioneArchivisticaType intestazione = unitaArch.addNewIntestazione();
    intestazione.setVersione((String) params.get("versione"));
    //versatore
    VersatoreType versatore = intestazione.addNewVersatore();
    versatore.setAmbiente(validatePropertyValorizzata("ambiente",params));
    versatore.setEnte(validatePropertyValorizzata("ente",params));
    versatore.setStruttura(validatePropertyValorizzata("struttura",params));
    versatore.setUserID(validatePropertyValorizzata("userid",params));

    intestazione.setChiave(chiave);
    intestazione.setTipologiaUnitaArchivistica("Fascicolo");
    //PROFILO AGGREGAZIONE
    ProfiloAggregazioneType profiloAgg = unitaArch.addNewProfiloAggregazione();
    AggregazioneType agg = profiloAgg.addNewAggregazionePrincipale();
    //IdAgg
    IdAggType idAgg = agg.addNewIdAgg();
    idAgg.setTipoAggregazione(TipoAggregazioneType.FASCICOLO);
    idAgg.setIdAggregazione(chiave);
    agg.setTipologiaFascicolo(TipologiaFascicoloType.PROCEDIMENTO_AMMINISTRATIVO);
    //chiave descrittiva
    ChiaveDescrittivaType chiaveDescr = agg.addNewChiaveDescrittiva();
    chiaveDescr.setOggetto((String) params.get("oggetto"));
    chiaveDescr.setParoleChiaveArray(new String[]{codiceGara});
    //classificazione
    ClassificazioneType classificazione = agg.addNewClassificazione();
    classificazione.setIndiceDiClassificazione(indice);
    classificazione.setDescrizione(validatePropertyValorizzata("descClassificazione",params));
    classificazione.setProgressivo(progressivo);

    DatiSpecificiArchivisticaType datiSpecifici = agg.addNewDatiSpecifici();
    datiSpecifici.setVersioneDatiSpecifici("1.0");
    datiSpecifici.setCodiceInterno(codiceGara);
    if(StringUtils.isNotBlank(idAppalto))
      datiSpecifici.setIdAppalto(idAppalto);
    if(StringUtils.isNotBlank(idPiani))
      datiSpecifici.setIdPianificazione(idPiani);
    if(StringUtils.isNotBlank(cig))
      datiSpecifici.setCIG(cig);
    //datiSpecifici.setIdContratto();

    //INDICE DOCUMENTI
    IndiceDocumentiType indiceDocumenti = agg.addNewIndiceDocumenti();
    List documenti = (List) params.get("datiDocumenti");
    for(Object v : documenti) {
      ChiaveArchivisticaType ca = indiceDocumenti.addNewChiave();
      String chiaveComposta = SqlManager.getValueFromVectorParam(v, 0).getStringValue();
      String[] chiaveScomposta = chiaveComposta.split("\\|");
      ca.setNumero(chiaveScomposta[0]);
      ca.setAnno(Integer.parseInt(chiaveScomposta[1]));
      ca.setTipoRegistro(chiaveScomposta[2]);
    }

    //STRUTTURA AGGREGAZIONE
    StrutturaAggregazioneType struttura = unitaArch.addNewStrutturaAggregazione();
      //soggetti
    SoggettiArchivisticaType soggetti = struttura.addNewSoggetti();
    RuoloArchivisticaType ruolo = soggetti.addNewRuolo();
    TipoSoggetto4Type tipsog = ruolo.addNewRUP();
    tipsog.setTipoRuolo("RUP");
    RUPType rup = tipsog.addNewRUP();
    rup.setCognome(infoAmm.get("cognome"));
    rup.setNome(infoAmm.get("nome"));
    CodiceIPAType ipa = rup.addNewIPAAmm();
    ipa.setDenominazione(infoAmm.get("denominazione"));
    ipa.setCodiceIPA(infoAmm.get("codiceIPA"));
    CodiceIPAType ipao = rup.addNewIPAAOO();
    ipao.setDenominazione(infoAmm.get("denominazione"));
    ipao.setCodiceIPA(infoAmm.get("codiceIPA"));
    CodiceIPAType ipau = rup.addNewIPAUOR();
    ipau.setDenominazione(infoAmm.get("denominazione"));
    ipau.setCodiceIPA(infoAmm.get("codiceIPA"));
    rup.setIndirizziDigitaliDiRiferimentoArray(creazioneArchivioDocumentiGaraManager.getInfoAmmIndirizziDigitaliDiRiferimento(infoAmm));

      //assegnazione
    AssegnazioneType ass = struttura.addNewAssegnazione();
    TipoAssegnazioneType tipass = ass.addNewTipoAssegnazione();
    AssType asst = tipass.addNewPerCompetenza();
    asst.setTipoAssegnazioneRuolo("Per Competenza");
    ASType as = asst.addNewSoggettoAssegnatario();
    CodiceIPAType ipa2 = as.addNewIPAAmm();
    ipa2.setDenominazione(infoAmm.get("denominazione"));
    ipa2.setCodiceIPA(infoAmm.get("codiceIPA"));
    CodiceIPAType ipao2 = as.addNewIPAAOO();
    ipao2.setDenominazione(infoAmm.get("denominazione"));
    ipao2.setCodiceIPA(infoAmm.get("codiceIPA"));
    CodiceIPAType ipau2 = as.addNewIPAUOR();
    ipau2.setDenominazione(infoAmm.get("denominazione"));
    ipau2.setCodiceIPA(infoAmm.get("codiceIPA"));
    as.setIndirizziDigitaliDiRiferimentoArray(creazioneArchivioDocumentiGaraManager.getInfoAmmIndirizziDigitaliDiRiferimento(infoAmm));
    asst.setDataInizioAssegnazione(data);
    asst.setOraInizioAssegnazione(data);

    unitaArch.setDataApertura(data);
    //unitaArch.setDataChiusura();
    unitaArch.setTempoDiConservazione(Integer.parseInt(validatePropertyValorizzata("tempoConservazione",params)));

    Calendar sysdate = Calendar.getInstance();
    sysdate.setTimeInMillis(System.currentTimeMillis());
    unitaArch.setDataCreazioneIndiceSIP(sysdate);

    XmlOptions options = new XmlOptions();
    options.setSavePrettyPrint();
    options.setSavePrettyPrintIndent(2);

    // Salvataggio dell'XML in memoria
    ByteArrayOutputStream xmlOutputStream = new ByteArrayOutputStream();
    unita.save(xmlOutputStream, options);
    byte[] xmlBytes = xmlOutputStream.toByteArray();
    return xmlBytes;
  }
}
