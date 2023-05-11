/*
 * Created on 30/08/13
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.maggioli.eldasoft.avcp.l190.dataset.AggregatoType;
import it.maggioli.eldasoft.avcp.l190.dataset.PubblicazioneDocument;
import it.maggioli.eldasoft.avcp.l190.dataset.PubblicazioneDocument.Pubblicazione;
import it.maggioli.eldasoft.avcp.l190.dataset.PubblicazioneDocument.Pubblicazione.Data;
import it.maggioli.eldasoft.avcp.l190.dataset.PubblicazioneDocument.Pubblicazione.Data.Lotto;
import it.maggioli.eldasoft.avcp.l190.dataset.PubblicazioneDocument.Pubblicazione.Data.Lotto.Aggiudicatari;
import it.maggioli.eldasoft.avcp.l190.dataset.PubblicazioneDocument.Pubblicazione.Data.Lotto.Aggiudicatari.AggiudicatarioRaggruppamento;
import it.maggioli.eldasoft.avcp.l190.dataset.PubblicazioneDocument.Pubblicazione.Data.Lotto.Partecipanti;
import it.maggioli.eldasoft.avcp.l190.dataset.PubblicazioneDocument.Pubblicazione.Data.Lotto.Partecipanti.Raggruppamento;
import it.maggioli.eldasoft.avcp.l190.dataset.PubblicazioneDocument.Pubblicazione.Data.Lotto.StrutturaProponente;
import it.maggioli.eldasoft.avcp.l190.dataset.PubblicazioneDocument.Pubblicazione.Data.Lotto.TempiCompletamento;
import it.maggioli.eldasoft.avcp.l190.dataset.PubblicazioneDocument.Pubblicazione.Metadata;
import it.maggioli.eldasoft.avcp.l190.dataset.RuoloType;
import it.maggioli.eldasoft.avcp.l190.dataset.SceltaContraenteType;
import it.maggioli.eldasoft.avcp.l190.dataset.SingoloType;
import it.maggioli.eldasoft.avcp.l190.indici.IndiciDocument;
import it.maggioli.eldasoft.avcp.l190.indici.IndiciDocument.Indici;
import it.maggioli.eldasoft.avcp.l190.indici.IndiciDocument.Indici.Indice;
import it.maggioli.eldasoft.avcp.l190.indici.IndiciDocument.Indici.Indice.Dataset;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpSession;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlValidationError;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard che si occupa di gestire la funzionalità di export dei
 * dati di un adempimento
 *
 * @author Marcello Caminiti
 */
public class GestorePopupExportDatiAnticor extends AbstractGestoreEntita {

  static Logger               logger         = Logger.getLogger(GestorePopupExportDatiAnticor.class);
  private static final String nomeFileIndice = "indice_dataset.xml";
  private static final String encoding       = "UTF-8";

  @Override
  public String getEntita() {
    return "ANTICORLOTTI";
  }

  public GestorePopupExportDatiAnticor() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupExportDatiAnticor(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer dataColumnContainer)
      throws GestoreException {
  }

  @SuppressWarnings("unchecked")
  @Override
  public void preInsert(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {

    String idString = UtilityStruts.getParametroString(this.getRequest(), "id");
    Long id = new Long(idString);
    String isIntegrazionePortale = UtilityStruts.getParametroString(this.getRequest(),"isIntegrazionePortale");
    String url = dataColumnContainer.getString("URL");
    if (!"true".equals(isIntegrazionePortale)) {
      try {
        this.sqlManager.update("update anticor set urlsito=? where id=?", new Object[]{url,id});
      } catch (SQLException e) {
        this.getRequest().setAttribute("erroreOperazione", "1");
        throw new GestoreException(
            "Errore durante l'aggiornamento della url di pubblicazione ", "urlPubbl", e);
      }
    }


    String nomeFileLotto = null;
    String nomeFileZip = null;
    String urlXmlIndice = null;
    String urlXmlLotto = null;
    String percorsoAVCP = ConfigManager.getValore("it.eldasoft.sil.pg.avcp");
    if (percorsoAVCP == null || "".equals(percorsoAVCP)) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException(
          "Non è valorizzata la property del percorso della cartella AVCP ",
          "cartellaAVCP", new Exception());
    }

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
        "tabellatiManager", this.getServletContext(), TabellatiManager.class);

    Long progressivoIndice = new Long(0);
    String dataOdierna = UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_AAAAMMGG);
    GregorianCalendar oggi = new GregorianCalendar();
    dataOdierna += (oggi.get(Calendar.HOUR_OF_DAY) < 10
        ? ("0" + oggi.get(Calendar.HOUR_OF_DAY))
        : ("" + oggi.get(Calendar.HOUR_OF_DAY)))
        + ((oggi.get(Calendar.MINUTE)) < 10
            ? ("0" + (oggi.get(Calendar.MINUTE)))
            : ("" + oggi.get(Calendar.MINUTE)))
        + ((oggi.get(Calendar.SECOND)) < 10
            ? ("0" + (oggi.get(Calendar.SECOND)))
            : ("" + oggi.get(Calendar.SECOND)));

    // Estrazione dati da ANNORIF
    String select = "select annorif,titolo, estratto, datapubbl, entepubbl, dataagg, urlsito, licenza from anticor where id=?";
    Vector<JdbcParametro> datiAnticor = null;
    try {
      datiAnticor = this.sqlManager.getVector(select, new Object[] { id });
    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException(
          "Errore durante la lettura dei dati di anticor ", "datiAnticor", e);
    }
    if (datiAnticor != null && datiAnticor.size() > 0) {
      Long annorif = SqlManager.getValueFromVectorParam(datiAnticor, 0).longValue();
      String titolo = SqlManager.getValueFromVectorParam(datiAnticor, 1).getStringValue();
      String estratto = SqlManager.getValueFromVectorParam(datiAnticor, 2).getStringValue();
      Timestamp datapubbl = SqlManager.getValueFromVectorParam(datiAnticor, 3).dataValue();
      String entepubbl = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
          datiAnticor, 4).getStringValue());
      String urlsito = SqlManager.getValueFromVectorParam(datiAnticor, 6).getStringValue();
      String licenza = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
          datiAnticor, 7).getStringValue());
      Calendar c = Calendar.getInstance();

      //Si deve prelevare il codice fiscale dell'ufficio intestatario
      String codfisUffInt = null;
      String ufficioIntestatario=null;
      HttpSession session = this.getRequest().getSession();
      if ( session != null) {
        ufficioIntestatario = (String) session.getAttribute("uffint");
      }
      if (ufficioIntestatario != null && !"".equals(ufficioIntestatario)) {
        try {
          codfisUffInt = (String) this.sqlManager.getObject("select cfein from uffint where codein=?", new Object[]{ufficioIntestatario});
        } catch (SQLException e) {
          this.getRequest().setAttribute("erroreOperazione", "1");
          throw new GestoreException(
              "Errore durante la lettura del codice fiscale dell'ufficio intestatario ", "cfAnticorlotti", e);
        }
        if (codfisUffInt == null || "".equals(codfisUffInt)) {
          this.getRequest().setAttribute("erroreOperazione", "1");
          throw new GestoreException(
            "Codice fiscale Ufficio Intestatario " + ufficioIntestatario + " non valorizzato" , "UfficioInt.cfNull", new Exception());
        }

        percorsoAVCP += codfisUffInt;
        //Se non esiste la sottocartella dell'ufficio intestatario la si deve creare
        File file = new File(percorsoAVCP);
        if (!file.exists()) {
           if (!file.mkdir()){
             this.getRequest().setAttribute("erroreOperazione", "1");
             throw new GestoreException(
               "Errore nella creazione della cartella per l'Ufficio intestatario " + ufficioIntestatario, "cartellaAVCP.UfficioInt", new Exception());
           }
        }
        percorsoAVCP+="/";
      }

      if (!urlsito.endsWith("/")) urlsito += "/";

      //urlXmlIndice = urlsito + annorif.toString() + "/" + nomeFileIndice;
      urlXmlIndice = urlsito + nomeFileIndice;
      /*
      if(codfisUffInt != null && !"".equals(codfisUffInt))
        urlXmlIndice+= codfisUffInt + "/";
      urlXmlIndice+= annorif.toString() + "/" + nomeFileIndice;
      */
      nomeFileZip = annorif.toString() + "_" + dataOdierna + ".zip";

      ZipOutputStream zipOut;

      try {
        zipOut = new ZipOutputStream(new FileOutputStream(percorsoAVCP
            + nomeFileZip));
      } catch (FileNotFoundException e) {
        this.getRequest().setAttribute("erroreOperazione", "1");
        throw new GestoreException("Errore durante la creazione del file ZIP ",
            "creazioneXMLAVCP.creazioneZIP", e);
      }

      // Lettura dei dati dei lotti
      select = "select cig, codfiscprop, denomprop, oggetto, sceltacontr, lotti.id, datainizio, dataultimazione, impsommeliq, impaggiudic, stato" +
            "  from anticorlotti lotti, anticor a where a.id=? and a.id=lotti.idanticor and pubblica='1'  order by cig ";
      List<Vector<JdbcParametro>> listaAnticorlotti = null;

      try {
        listaAnticorlotti = this.sqlManager.getListVector(select, new Object[] { id });
      } catch (SQLException e) {
        cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
        this.getRequest().setAttribute("erroreOperazione", "1");
        throw new GestoreException(
            "Errore durante la lettura dei dati di anticorlotti ",
            "creazioneXMLAVCP.datiAnticorlotti", e);
      }

      IndiciDocument indiciDocument = IndiciDocument.Factory.newInstance();
      indiciDocument.documentProperties().setEncoding(encoding);
      Indici indici = indiciDocument.addNewIndici();

      // si modifica l'header introducendo i namespace previsti dal tracciato
      XmlCursor curInd = indici.newCursor();
      curInd.toFirstContentToken();
      curInd.insertNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
      curInd.insertAttributeWithValue("noNamespaceSchemaLocation",
          "http://www.w3.org/2001/XMLSchema-instance",
          "http://dati.anticorruzione.it/schema/datasetIndiceAppaltiL190.xsd");
      curInd.dispose();

      Indice indice = indici.addNewIndice();
      it.maggioli.eldasoft.avcp.l190.indici.IndiciDocument.Indici.Metadata metadataIndice = indici.addNewMetadata();
      metadataIndice.setTitolo(titolo);
      metadataIndice.setAbstract(estratto);
      if (datapubbl != null) {
        c.setTimeInMillis(datapubbl.getTime());
      } else {
        datapubbl = new Timestamp(c.getTime().getTime());
      }
      metadataIndice.setDataPubblicazioneIndice(c);
      metadataIndice.setEntePubblicatore(entepubbl);
      c = Calendar.getInstance();
      metadataIndice.setDataUltimoAggiornamentoIndice(c);
      metadataIndice.setAnnoRiferimento(annorif.intValue());
      metadataIndice.setUrlFile(urlXmlIndice);
      metadataIndice.setLicenza(licenza);

      if (listaAnticorlotti != null && listaAnticorlotti.size() > 0) {

        String cig = null;
        String codfiscprop = null;
        String denomprop = null;
        String oggetto = null;
        Long sceltacontr = null;
        Double impaggiudic = null;
        Timestamp datainizio = null;
        Timestamp dataultimazione = null;
        Double impsommeliq = null;
        Long idanticorlotti = null;
        List<?> listaPartecipanti = null;
        String descTabellato = null;
        List<?> listaAggiudicatari = null;

        for (int i = 0; i < listaAnticorlotti.size(); i++) {
          cig = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
              listaAnticorlotti.get(i), 0).getStringValue());
          codfiscprop = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
              listaAnticorlotti.get(i), 1).getStringValue());
          denomprop = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
              listaAnticorlotti.get(i), 2).getStringValue());
          oggetto = StringUtils.left(SqlManager.getValueFromVectorParam(
              listaAnticorlotti.get(i), 3).getStringValue(),250);
          sceltacontr = SqlManager.getValueFromVectorParam(
              listaAnticorlotti.get(i), 4).longValue();
          idanticorlotti = SqlManager.getValueFromVectorParam(
              listaAnticorlotti.get(i), 5).longValue();
          datainizio = SqlManager.getValueFromVectorParam(
              listaAnticorlotti.get(i), 6).dataValue();
          dataultimazione = SqlManager.getValueFromVectorParam(
              listaAnticorlotti.get(i), 7).dataValue();
          impsommeliq = SqlManager.getValueFromVectorParam(
              listaAnticorlotti.get(i), 8).doubleValue();
          impaggiudic = SqlManager.getValueFromVectorParam(
              listaAnticorlotti.get(i), 9).doubleValue();

          PubblicazioneDocument document = PubblicazioneDocument.Factory.newInstance();
          document.documentProperties().setEncoding(encoding);
          Pubblicazione pubblicazione = document.addNewPubblicazione();

          // si modifica l'header introducendo i namespace previsti dal tracciato
          XmlCursor curPubbl = pubblicazione.newCursor();
          curPubbl.toFirstContentToken();
          curPubbl.insertNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
          curPubbl.insertAttributeWithValue("schemaLocation",
              "http://www.w3.org/2001/XMLSchema-instance",
              "legge190_1_0 http://dati.anticorruzione.it/schema/datasetAppaltiL190.xsd");
          curPubbl.insertNamespace("legge190", "legge190_1_0");
          curPubbl.dispose();

          // Sezione METADATA
          Metadata metadata = pubblicazione.addNewMetadata();
          metadata.setTitolo(titolo);
          metadata.setAbstract(estratto);
          if (datapubbl != null) c.setTimeInMillis(datapubbl.getTime());
          metadata.setDataPubblicazioneDataset(c);
          metadata.setEntePubblicatore(entepubbl);
          c = Calendar.getInstance();
          metadata.setDataUltimoAggiornamentoDataset(c);
          metadata.setAnnoRiferimento(annorif.intValue());
          urlXmlLotto = urlsito + cig.replace("#", "_") + ".xml";
          metadata.setUrlFile(urlXmlLotto);
          XmlString xmlString = XmlString.Factory.newInstance();
          xmlString.setStringValue(licenza);
          metadata.setLicenza(xmlString);

          // Sezione DATA
          Data datiPubblicazione = pubblicazione.addNewData();
          // Sezione LOTTO
          Lotto lotto = datiPubblicazione.addNewLotto();
          if (cig != null && (cig.startsWith("NOCIG") || cig.startsWith("$") || cig.startsWith("#")))
            lotto.setCig("0000000000");
          else
            lotto.setCig(cig);

          // Sezione PROPONENTE
          StrutturaProponente proponente = lotto.addNewStrutturaProponente();
          proponente.setCodiceFiscaleProp(codfiscprop);
          proponente.setDenominazione(denomprop);
          lotto.setOggetto(oggetto);
          descTabellato = tabellatiManager.getDescrTabellato("A2044",
              sceltacontr.toString());
          lotto.setSceltaContraente(SceltaContraenteType.Enum.forString(descTabellato));

          //if(stato!=null && stato.longValue()==2){
            // SEZIONE PARTECIPANTI
            Partecipanti partecipanti = lotto.addNewPartecipanti();
            select = "select id, tipo from anticorpartecip where idanticorlotti=? order by id";

            try {
              listaPartecipanti = sqlManager.getListVector(select,
                  new Object[] { new Long(idanticorlotti) });
            } catch (SQLException e) {
              cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
              this.getRequest().setAttribute("erroreOperazione", "1");
              throw new GestoreException(
                  "Errore durante la lettura dei dati di ANTICORPATECIP ",
                  "creazioneXMLAVCP.datiPartecipanti", e);
            }

            if (listaPartecipanti != null && listaPartecipanti.size() > 0) {
              Long idPartecip = null;
              Long tipo = null;
              String ragsoc = null;
              String codfiscPartecip = null;
              String idfiscest = null;
              Long ruolo = null;
              Vector<JdbcParametro> datiImprese = null;
              List<?> listaDatiImprese = null;
              for (int j = 0; j < listaPartecipanti.size(); j++) {
                idPartecip = SqlManager.getValueFromVectorParam(
                    listaPartecipanti.get(j), 0).longValue();
                tipo = SqlManager.getValueFromVectorParam(
                    listaPartecipanti.get(j), 1).longValue();
                if (tipo.longValue() != 2) {
                  // Gestione partecipante singolo
                  try {
                    datiImprese = sqlManager.getVector(
                        "select ragsoc, codfisc, idfiscest from anticorditte where idanticorpartecip=?",
                        new Object[] { idPartecip });
                  } catch (SQLException e) {
                    cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
                    this.getRequest().setAttribute("erroreOperazione", "1");
                    throw new GestoreException(
                        "Errore durante la lettura dei dati di ANTICORDITTE ",
                        "creazioneXMLAVCP.datiPartecipantiDITTE", e);
                  }
                  if (datiImprese != null && datiImprese.size() > 0) {
                    ragsoc = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
                        datiImprese, 0).getStringValue());
                    codfiscPartecip = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
                        datiImprese, 1).getStringValue());
                    idfiscest = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
                        datiImprese, 2).getStringValue());
                    SingoloType partecipante = partecipanti.addNewPartecipante();
                    partecipante.setRagioneSociale(ragsoc);
                    if (codfiscPartecip != null)
                      partecipante.setCodiceFiscale(codfiscPartecip);
                    else
                      partecipante.setIdentificativoFiscaleEstero(idfiscest);
                  }
                } else {
                  // Gestione raggruppamenti
                  try {
                    listaDatiImprese = sqlManager.getListVector(
                        "select ragsoc, codfisc, idfiscest, ruolo from anticorditte where idanticorpartecip=?",
                        new Object[] { idPartecip });
                  } catch (SQLException e) {
                    cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
                    this.getRequest().setAttribute("erroreOperazione", "1");
                    throw new GestoreException(
                        "Errore durante la lettura dei dati di ANTICORDITTE ",
                        "creazioneXMLAVCP.datiPartecipantiDITTE", e);
                  }
                  if (listaDatiImprese != null && listaDatiImprese.size() > 0) {
                    Raggruppamento raggruppamento = partecipanti.addNewRaggruppamento();
                    for (int k = 0; k < listaDatiImprese.size(); k++) {
                      ragsoc = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
                          listaDatiImprese.get(k), 0).getStringValue());
                      codfiscPartecip = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
                          listaDatiImprese.get(k), 1).getStringValue());
                      idfiscest = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
                          listaDatiImprese.get(k), 2).getStringValue());
                      ruolo = SqlManager.getValueFromVectorParam(
                          listaDatiImprese.get(k), 3).longValue();
                      AggregatoType aggregato = raggruppamento.addNewMembro();
                      if (codfiscPartecip != null)
                        aggregato.setCodiceFiscale(codfiscPartecip);
                      else
                        aggregato.setIdentificativoFiscaleEstero(idfiscest);
                      aggregato.setRagioneSociale(ragsoc);
                      if (ruolo != null) {
                        descTabellato = tabellatiManager.getDescrTabellato(
                            "A1094", ruolo.toString());
                        aggregato.setRuolo(RuoloType.Enum.forString(descTabellato));
                      }
                    }
                  }
                }

              }
            }

            // SEZIONE AGGIUDICATARI
            Aggiudicatari aggiudicatari = lotto.addNewAggiudicatari();
            select = "select id, tipo from anticorpartecip where idanticorlotti=? and aggiudicataria = ? order by id";
            try {
              listaAggiudicatari = sqlManager.getListVector(select,
                  new Object[] { new Long(idanticorlotti), "1" });
            } catch (SQLException e) {
              cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
              this.getRequest().setAttribute("erroreOperazione", "1");
              throw new GestoreException(
                  "Errore durante la lettura dei dati di ANTICORPATECIP a ANTICORDITTE per gli aggiudicatari",
                  "creazioneXMLAVCP.datiPartecipantiAgg", e);
            }

            List<?> listaDatiImprese = null;
            if (listaAggiudicatari != null && listaAggiudicatari.size() > 0) {
              Long idPartecip = null;
              Long tipo = null;
              Vector<JdbcParametro> datiImprese = null;
              String ragsoc = null;
              String codfiscPartecip = null;
              String idfiscest = null;
              Long ruolo = null;

              for (int j = 0; j < listaAggiudicatari.size(); j++) {
                idPartecip = SqlManager.getValueFromVectorParam(
                    listaAggiudicatari.get(j), 0).longValue();
                tipo = SqlManager.getValueFromVectorParam(
                    listaAggiudicatari.get(j), 1).longValue();
                if (tipo.longValue() != 2) {
                  // Gestione partecipante singolo
                  try {
                    datiImprese = sqlManager.getVector(
                        "select ragsoc, codfisc, idfiscest from anticorditte where idanticorpartecip=?",
                        new Object[] { idPartecip });
                  } catch (SQLException e) {
                    cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
                    this.getRequest().setAttribute("erroreOperazione", "1");
                    throw new GestoreException(
                        "Errore durante la lettura dei dati di ANTICORDITTE ",
                        "creazioneXMLAVCP.datiPartecipantiDITTE", e);
                  }
                  if (datiImprese != null && datiImprese.size() > 0) {
                    ragsoc = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
                        datiImprese, 0).getStringValue());
                    codfiscPartecip = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
                        datiImprese, 1).getStringValue());
                    idfiscest = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
                        datiImprese, 2).getStringValue());

                    SingoloType partecipante = aggiudicatari.addNewAggiudicatario();
                    partecipante.setRagioneSociale(ragsoc);
                    if (codfiscPartecip != null)
                      partecipante.setCodiceFiscale(codfiscPartecip);
                    else
                      partecipante.setIdentificativoFiscaleEstero(idfiscest);


                  }
                }else{
                  // Gestione raggruppamenti
                  try {
                    listaDatiImprese = sqlManager.getListVector(
                        "select ragsoc, codfisc, idfiscest, ruolo from anticorditte where idanticorpartecip=?",
                        new Object[] { idPartecip });
                  } catch (SQLException e) {
                    cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
                    this.getRequest().setAttribute("erroreOperazione", "1");
                    throw new GestoreException(
                        "Errore durante la lettura dei dati di ANTICORDITTE ",
                        "creazioneXMLAVCP.datiPartecipantiDITTE", e);
                  }
                  if (listaDatiImprese != null && listaDatiImprese.size() > 0) {
                    AggiudicatarioRaggruppamento raggruppamentoAgg = aggiudicatari.addNewAggiudicatarioRaggruppamento();
                    for (int k = 0; k < listaDatiImprese.size(); k++) {
                      ragsoc = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
                          listaDatiImprese.get(k), 0).getStringValue());
                      codfiscPartecip = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
                          listaDatiImprese.get(k), 1).getStringValue());
                      idfiscest = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(
                          listaDatiImprese.get(k), 2).getStringValue());
                      ruolo = SqlManager.getValueFromVectorParam(
                          listaDatiImprese.get(k), 3).longValue();
                      AggregatoType aggregato = raggruppamentoAgg.addNewMembro();
                      if (codfiscPartecip != null)
                        aggregato.setCodiceFiscale(codfiscPartecip);
                      else
                        aggregato.setIdentificativoFiscaleEstero(idfiscest);
                      aggregato.setRagioneSociale(ragsoc);
                      if (ruolo != null) {
                        descTabellato = tabellatiManager.getDescrTabellato(
                            "A1094", ruolo.toString());
                        aggregato.setRuolo(RuoloType.Enum.forString(descTabellato));
                      }

                    }
                  }
                }
              }

            }
          //}else{
          //  // SEZIONE PARTECIPANTI
          //  Partecipanti partecipanti = lotto.addNewPartecipanti();
          //  // SEZIONE AGGIUDICATARI
          //  Aggiudicatari aggiudicatari = lotto.addNewAggiudicatari();
          //}

          // SEZIONE IMPORTO AGGIUDICAZIONE
          if (impaggiudic != null)
            lotto.setImportoAggiudicazione(new BigDecimal(
                impaggiudic.toString()));
          else
            lotto.setImportoAggiudicazione(new BigDecimal(0));

          // SEZIONE TEMPI COMPLETAMENTO
          TempiCompletamento tempi = lotto.addNewTempiCompletamento();
          if (datainizio != null) {
            c.setTimeInMillis(datainizio.getTime());
            tempi.setDataInizio(c);
          }
          if (dataultimazione != null) {
            c.setTimeInMillis(dataultimazione.getTime());
            tempi.setDataUltimazione(c);
          }

          // SEZIONE IMPORTI LIQUIDAZIONE
          if (impsommeliq != null)
            lotto.setImportoSommeLiquidate(new BigDecimal(
                impsommeliq.toString()));
          else
            lotto.setImportoSommeLiquidate(new BigDecimal(0));

          // Validazione del xml del singolo lotto
          ArrayList<XmlValidationError> xmlValidationError = null;
          ;
          try {
            xmlValidationError = validaMsgXML(document);
          } catch (TransformerException e) {
            cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
            this.getRequest().setAttribute("erroreOperazione", "1");
            throw new GestoreException("Errore nella fase di validazione XML",
                "creazioneXMLAVCP.validazioneXMLLotto", e);
          } catch (IOException e) {
            cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
            this.getRequest().setAttribute("erroreOperazione", "1");
            throw new GestoreException("Errore nella fase di validazione XML",
                "creazioneXMLAVCP.validazioneXMLLotto", e);
          }
          if (xmlValidationError != null && xmlValidationError.size() > 0) {
            // Validazione XML non passata: gestione degli errori di validazione
            // per portarli a video
            logger.error("Errore nella fase di validazione XML del lotto con cig="
                + cig
                + " . Di seguito i dettagli del messaggio oggetto dell'errore.");
            for (int z = 0; z < xmlValidationError.size(); z++) {
              String msg = "";
              if (xmlValidationError.get(z).getOffendingQName() != null)
                msg = xmlValidationError.get(z).getOffendingQName().toString();
              logger.error(" "
                  + (z + 1)
                  + ") "
                  + msg
                  + " "
                  + xmlValidationError.get(z).getMessage());
            }
            cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
            this.getRequest().setAttribute("erroreOperazione", "1");
            throw new GestoreException("Errore nella fase di validazione XML",
                "creazioneXMLAVCP.validazioneXMLLotto", new Exception());
          } else {
            // Inserimento nell'indice il riferimento al file XML del lotto
            progressivoIndice = new Long(progressivoIndice.longValue() + 1);
            Dataset dataset = indice.addNewDataset();
            dataset.setId("ID_" + progressivoIndice.toString());
            dataset.setDataUltimoAggiornamento(c = Calendar.getInstance());
            dataset.setLinkDataset(urlXmlLotto);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
              document.save(baos);
              nomeFileLotto = cig.replace("#", "_") + ".xml";

              // Inserimento nel file zip del file xml relativo al singolo lotto
              zipOut.setMethod(ZipOutputStream.DEFLATED);
              zipOut.setLevel(Deflater.DEFAULT_COMPRESSION);
              zipOut.putNextEntry(new ZipEntry(nomeFileLotto));
              zipOut.write(baos.toByteArray(), 0, baos.size());
              // Chiusura dell'attuale entry nello ZipOutputStream
              zipOut.closeEntry();
              zipOut.flush();

            } catch (IOException e) {
              cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
              this.getRequest().setAttribute("erroreOperazione", "1");
              throw new GestoreException("Errore nella creazione del file zip",
                  "creazioneXMLAVCP.creazioneZIP", e);
            }
          }

        }


      }else{
        //Non ci sono lotti
        PubblicazioneDocument document = PubblicazioneDocument.Factory.newInstance();
        document.documentProperties().setEncoding(encoding);
        Pubblicazione pubblicazione = document.addNewPubblicazione();

        // si modifica l'header introducendo i namespace previsti dal tracciato
        XmlCursor curPubbl = pubblicazione.newCursor();
        curPubbl.toFirstContentToken();
        curPubbl.insertNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        curPubbl.insertAttributeWithValue("schemaLocation",
            "http://www.w3.org/2001/XMLSchema-instance",
            "legge190_1_0 http://dati.anticorruzione.it/schema/datasetAppaltiL190.xsd");
        curPubbl.insertNamespace("legge190", "legge190_1_0");
        curPubbl.dispose();

        // Sezione METADATA
        Metadata metadata = pubblicazione.addNewMetadata();
        metadata.setTitolo(titolo);
        metadata.setAbstract(estratto);
        if (datapubbl != null) c.setTimeInMillis(datapubbl.getTime());
        metadata.setDataPubblicazioneDataset(c);
        metadata.setEntePubblicatore(entepubbl);
        c = Calendar.getInstance();
        metadata.setDataUltimoAggiornamentoDataset(c);
        metadata.setAnnoRiferimento(annorif.intValue());
        urlXmlLotto = urlsito +  "dataset.xml";
        metadata.setUrlFile(urlXmlLotto);
        XmlString xmlString = XmlString.Factory.newInstance();
        xmlString.setStringValue(licenza);
        metadata.setLicenza(xmlString);

        //Sezione dati vuota
        pubblicazione.addNewData();

        //validazione
        ArrayList<XmlValidationError> xmlValidationError = null;
        try {
          xmlValidationError = validaMsgXML(document);
        } catch (TransformerException e) {
          cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
          this.getRequest().setAttribute("erroreOperazione", "1");
          throw new GestoreException("Errore nella fase di validazione XML",
              "creazioneXMLAVCP.validazioneXMLLotto", e);
        } catch (IOException e) {
          cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
          this.getRequest().setAttribute("erroreOperazione", "1");
          throw new GestoreException("Errore nella fase di validazione XML",
              "creazioneXMLAVCP.validazioneXMLLotto", e);
        }
        if (xmlValidationError != null && xmlValidationError.size() > 0) {
          // Validazione XML non passata: gestione degli errori di validazione
          // per portarli a video
          logger.error("Errore nella fase di validazione XML del dataset senza lotti."
              + " . Di seguito i dettagli del messaggio oggetto dell'errore.");
          for (int z = 0; z < xmlValidationError.size(); z++) {
            String msg = "";
            if (xmlValidationError.get(z).getOffendingQName() != null)
              msg = xmlValidationError.get(z).getOffendingQName().toString();
            logger.error(" "
                + (z + 1)
                + ") "
                + msg
                + " "
                + xmlValidationError.get(z).getMessage());
          }
          cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
          this.getRequest().setAttribute("erroreOperazione", "1");
          throw new GestoreException("Errore nella fase di validazione XML",
              "creazioneXMLAVCP.validazioneXMLLotto", new Exception());
        } else {
          // Inserimento nell'indice il riferimento al file XML del lotto
          progressivoIndice = new Long(progressivoIndice.longValue() + 1);
          Dataset dataset = indice.addNewDataset();
          dataset.setId("ID_" + progressivoIndice.toString());
          dataset.setDataUltimoAggiornamento(c = Calendar.getInstance());
          dataset.setLinkDataset(urlXmlLotto);

          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          try {
            document.save(baos);
            nomeFileLotto = "dataset.xml";
            // Inserimento nel file zip del file xml relativo al singolo lotto
            zipOut.setMethod(ZipOutputStream.DEFLATED);
            zipOut.setLevel(Deflater.DEFAULT_COMPRESSION);
            zipOut.putNextEntry(new ZipEntry(nomeFileLotto));
            zipOut.write(baos.toByteArray(), 0, baos.size());
            // Chiusura dell'attuale entry nello ZipOutputStream
            zipOut.closeEntry();
            zipOut.flush();

          } catch (IOException e) {
            cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
            this.getRequest().setAttribute("erroreOperazione", "1");
            throw new GestoreException("Errore nella creazione del file zip",
                "creazioneXMLAVCP.creazioneZIP", e);
          }
        }
      }


      // Validazione del xml dell'indice
      ArrayList<XmlValidationError> xmlValidationError = null;

      try {
        xmlValidationError = validaMsgXML(indiciDocument);
      } catch (TransformerException e) {
        cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
        this.getRequest().setAttribute("erroreOperazione", "1");
        throw new GestoreException("Errore nella fase di validazione XML",
            "creazioneXMLAVCP.validazioneXMLLotto", e);
      } catch (IOException e) {
        cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
        this.getRequest().setAttribute("erroreOperazione", "1");
        throw new GestoreException("Errore nella fase di validazione XML",
            "creazioneXMLAVCP.validazioneXMLLotto", e);
      }
      if (xmlValidationError != null && xmlValidationError.size() > 0) {
        // Validazione XML non passata: gestione degli errori di validazione
        // per portarli a video
        logger.error("Errore nella fase di validazione XML del file di indice. Di seguito i dettagli del messaggio oggetto dell'errore.");
        for (int z = 0; z < xmlValidationError.size(); z++) {
          String msg = "";
          if (xmlValidationError.get(z).getOffendingQName() != null)
            msg = xmlValidationError.get(z).getOffendingQName().toString();
          logger.error(" "
              + (z + 1)
              + ") "
              + msg
              + " "
              + xmlValidationError.get(z).getMessage());
        }
        cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
        this.getRequest().setAttribute("erroreOperazione", "1");
        throw new GestoreException("Errore nella fase di validazione XML",
            "creazioneXMLAVCP.validazioneXMLLotto", new Exception());
      } else {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
          indiciDocument.save(baos);

          // Inserimento nel file zip del file xml relativo all'indice
          zipOut.setMethod(ZipOutputStream.DEFLATED);
          zipOut.setLevel(Deflater.DEFAULT_COMPRESSION);
          zipOut.putNextEntry(new ZipEntry(nomeFileIndice));
          zipOut.write(baos.toByteArray(), 0, baos.size());

          // Chiusura dell'attuale entry nello ZipOutputStream
          zipOut.closeEntry();
          zipOut.flush();

          // Aggiornamento di ANTICOR
          String update = "update ANTICOR set DATAPUBBL =?, DATAAGG = ?, NOMEFILE =?, ESPORTATO = ? where id=?";
          this.sqlManager.update(
              update,
              new Object[] { new Date(datapubbl.getTime()),
                  UtilityDate.getDataOdiernaAsDate(), nomeFileZip, "1", id });

        } catch (IOException e) {
          cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
          this.getRequest().setAttribute("erroreOperazione", "1");
          throw new GestoreException("Errore nella creazione del file zip",
              "creazioneXMLAVCP.creazioneZIP", e);
        } catch (SQLException e) {
          cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
          this.getRequest().setAttribute("erroreOperazione", "1");
          throw new GestoreException("Errore nell'aggiornamento di ANTICOR",
              "aggiornamentoAnticor", e);
        }
      }

      try {
        zipOut.close();
      } catch (IOException e) {
        cancellazioneFile(zipOut, percorsoAVCP + nomeFileZip);
        this.getRequest().setAttribute("erroreOperazione", "1");
        throw new GestoreException("Errore nella chiusura del file zip",
            "creazioneXMLAVCP.chiusuraZIP", e);
      }
    }

    // Se tutto è andato bene setto nel request il parametro operazioneEseguita
    // = 1
    this.getRequest().setAttribute("operazioneEseguita", "1");
  }

  @Override
  public void preUpdate(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer)
      throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  /**
   * Validazione del messaggio XML prodotto prima dell'invio al proxy.
   *
   * @param oggettoDoc
   *        xml object
   * @return Ritorna un ArrayList di XmlValidationError nel caso la validazione
   *         del messaggio non sia rispettata.
   * @throws GestoreException
   *         GestoreException
   * @throws TransformerException
   *         TransformerException
   * @throws IOException
   *         IOException
   */
  public final ArrayList<XmlValidationError> validaMsgXML(
      final XmlObject oggettoDoc) throws GestoreException,
      TransformerException, IOException {
    ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    oggettoDoc.save(baos);
    baos.close();
    XmlOptions validationOptions = new XmlOptions();
    validationOptions.setErrorListener(validationErrors);
    oggettoDoc.validate(validationOptions);

    if (validationErrors.size() > 0) {
      return validationErrors;
    } else {
      return null;
    }
  }

  private void cancellazioneFile(ZipOutputStream zipout, String nomeFile) {
    try {
      zipout.close();
    } catch (IOException e) {

    }
    File file = new File(nomeFile);
    if (file.isFile()) file.delete();
  }
}