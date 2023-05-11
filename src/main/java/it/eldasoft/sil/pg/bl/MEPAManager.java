/*
 * Created on 02/04/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.TransactionStatus;

import com.lowagie.text.DocumentException;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.cifrabuste.CifraturaBusteManager;
import it.eldasoft.sil.pg.bl.utils.ListaDocumentiPortaleUtilities;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.sil.pg.tags.gestori.submit.GestoreDITG;
import it.eldasoft.sil.portgare.datatypes.AttributoGenericoType;
import it.eldasoft.sil.portgare.datatypes.BustaEconomicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaDocument;
import it.eldasoft.sil.portgare.datatypes.BustaTecnicaType;
import it.eldasoft.sil.portgare.datatypes.DocumentazioneBustaDocument;
import it.eldasoft.sil.portgare.datatypes.DocumentoType;
import it.eldasoft.sil.portgare.datatypes.GestioneProdottiDocument;
import it.eldasoft.sil.portgare.datatypes.GestioneProdottiType;
import it.eldasoft.sil.portgare.datatypes.ListaComponentiOffertaType;
import it.eldasoft.sil.portgare.datatypes.ListaCriteriValutazioneType;
import it.eldasoft.sil.portgare.datatypes.ListaDocumentiType;
import it.eldasoft.sil.portgare.datatypes.ListaProdottiType;
import it.eldasoft.sil.portgare.datatypes.OffertaEconomicaType;
import it.eldasoft.sil.portgare.datatypes.OffertaTecnicaType;
import it.eldasoft.sil.portgare.datatypes.ProdottoType;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneDocument;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.maggioli.eldasoft.security.SymmetricEncryptionUtils;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.maggioli.eldasoft.ws.dm.WSDMInviaMailType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAnagraficaType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoInType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;
import it.maggioli.eldasoft.ws.dm.WSDMTipoVoceRubricaType;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * Classe di gestione delle funzionalita' inerenti il Mercato Elettronico
 *
 * @author Marcello Caminiti
 */
public class MEPAManager {

  /** Logger */
  static Logger               logger                = Logger.getLogger(MEPAManager.class);

  /** Manager SQL per le operazioni su database */
  private SqlManager          sqlManager;

  private FileAllegatoManager fileAllegatoManager;

  private GenChiaviManager    genChiaviManager;

  private PgManager           pgManager;

  private PgManagerEst1           pgManagerEst1;

  private AggiudicazioneManager    aggiudicazioneManager;

  private GestioneWSDMManager gestioneWSDMManager;

  private ControlliOepvManager controlliOepvManager;

  private CifraturaBusteManager cifraturaBusteManager;

  private static final String nomeFileXML_FS7           = "dati_prodotti.xml";
  private static final String nomeFileXML_FS8           = "dati_varprodotti.xml";

  private static final String estensioneFileFirmato = ".pdf.p7m";

  private static final String INSERT_W_INVCOM = "INSERT INTO w_invcom(IDPRG,IDCOM,COMENT,COMKEY1,COMKEY2,COMCODOPE,COMDATINS,"
					+ "COMMITT,COMSTATO,COMINTEST,COMMSGOGG,COMMSGTES,COMPUB,COMMSGTIP,IDCFG)"
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String INSERT_W_INVCOMDES = "INSERT INTO w_invcomdes(IDPRG,IDCOM,IDCOMDES,DESCODENT,DESCODSOG,"
					+ "DESMAIL,DESINTEST,COMTIPMA) VALUES(?,?,?,?,?,?,?,?)";

	private static final String INSERT_W_DOCDIG = "INSERT INTO w_docdig(IDPRG,IDDOCDIG,DIGENT,DIGKEY1,DIGKEY2,"
					+ "DIGNOMDOC,DIGDESDOC,DIGOGG) VALUES (?,?,?,?,?,?,?,?)";

	private static final String GET_MAX_W_INVCOM = "SELECT max(idcom) FROM w_invcom WHERE idprg=?";

	private static final String GET_MAX_W_INVCOMDES = "SELECT max(idcomdes) FROM w_invcomdes WHERE idprg=? AND idcom=?";

	private static final String GET_MAX_W_DOCDIG = "SELECT max(iddocdig) FROM w_docdig WHERE idprg=?";

	private static final String UPDATE_GARECONT_FOR_ODA = "UPDATE garecont SET stato=?, dattra=?, iddocdg=?, idprg=? WHERE ngara=? AND ncont=?";

	private static final String UPDATE_GARECONT_FROM_WSDM = "UPDATE garecont SET nproat=? WHERE ngara=? AND ncont=?";

	private static final String selectW_INVCOM = "select idprg, idcom, comkey1, comkeysess, comstato, comkey3 from w_invcom where comkey2 = ? and (comstato = '5' or comstato='13') and comtipo = ?";

	private static final String selectW_INVCOM_FILTRO_COMKEY3 = "select idprg, idcom, comkey1, comkeysess, comstato, comkey3 from w_invcom where comkey2 = ? and (comstato = '5' or comstato='13') and comtipo = ? and comkey3 = ?";

	private static final String selectW_INVCOM_SCARTATE = "select count(*) from w_invcom where comkey2 = ? and (comstato = '8' or comstato='20') and comtipo = ?";

	private static final String selectW_INVCOM_SCARTATE_FILTRO_COMKEY3 = "select count(*) from w_invcom where comkey2 = ? and (comstato = '8' or comstato='20') and comtipo = ? and comkey3 = ?";

	private static final String selectW_INVCOM_STEP2 = "select idprg, idcom, comkey1, comkeysess, comstato, comkey3 from w_invcom where comkey2 = ? and comstato = '16' and comtipo = ?";

	private static final String selectW_INVCOM_FILTRO_COMKEY3_STEP2 = "select idprg, idcom, comkey1, comkeysess, comstato, comkey3 from w_invcom where comkey2 = ? and comstato = '16' and comtipo = ? and comkey3 = ?";

	private static final String selectW_DOCDIG = "select idprg, iddocdig from w_docdig where digent = ? and idprg = ? and digkey1 = ? and digkey3 is null order by iddocdig";

	private static final String selectW_PUSER = "select userkey1 from w_puser where usernome = ?";

	private static final String selectDITG = "select count(*) from ditg where codgar5 = ? and ngara5 = ? and (invgar is null or invgar = '1') and dittao = ? and (ncomope = ?  or ncomope is null)";

	private static final String selectDITG_FS10A = "select count(*) from ditg where codgar5 = ? and ngara5 = ? and dittao = ? and (ncomope = ?  or ncomope is null)";

	private static final String selectRaggruppamentoDITG = "select dittao from ditg where codgar5 = ? and ngara5 = ? and (invgar is null or invgar = '1') and "+
      "dittao=(select codime9 from ragimp where coddic =? and impman='1' and codime9=dittao) and (ncomope = ?  or ncomope is null)";

	private static final String selectOfftel = "select offtel from torn where codgar=?";

	private static final String selectDatiDitg = "select ammgar,fasgar,datoff,oraoff,idanonimo from ditg where codgar5 = ? and ngara5 = ? and dittao = ?";

	private static final String selectDatiDitg_FS10A = "select ammgar,fasgar,dricind,oradom from ditg where codgar5 = ? and ngara5 = ? and dittao = ?";

	private static final String selectDatiDitgOffUnica = "select d1.ammgar,d1.fasgar,d2.datoff,d2.oraoff from ditg d1, ditg d2 where d1.codgar5 = ? and d1.ngara5 = ? and d1.dittao = ?"
	      + " and d1.codgar5=d2.codgar5 and d2.codgar5=d2.ngara5 and d2.dittao = d1.dittao";

    private static final String selectRtofferta = "select rtofferta from ditg where codgar5 = ? and ngara5 = ? and dittao = ? and (ncomope = ?  or ncomope is null) ";

    private static final String selectCodiceRT =  "select dittao from ditg where codgar5 = ? and ngara5 = ? and dittainv = ? and (ncomope = ?  or ncomope is null) ";

	//private static final String selectAllegatiOrdine = "select descrizione, dignomdoc, d.IDPRG, d.IDDOCDG from DOCUMGARA d,W_DOCDIG w where CODGAR=? and NGARA = ? and GRUPPO = ? and d.IDPRG=w.IDPRG and d.IDDOCDG = w.IDDOCDIG and allmail=? order by norddocg";

    static {
      // si imposta il provider sicurezza da utilizzare
      BouncyCastleProvider provider = new BouncyCastleProvider();
      String name = provider.getName();
      synchronized (Security.class) {
          Security.removeProvider(name); // remove old instance
          Security.addProvider(provider);
      }
  }

  /**
   * Set SqlManager
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   *
   * @param fileAllegatoManager
   */
  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  /**
   *
   * @param genChiaviManager
   */
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  /**
  *
  * @param pgManager
  */
 public void setPgManager(PgManager pgManager) {
   this.pgManager = pgManager;
 }

 /**
 *
 * @param aggiudicazioneManager
 */
  public void setAggiudicazioneManager(AggiudicazioneManager aggiudicazioneManager) {
    this.aggiudicazioneManager = aggiudicazioneManager;
  }

   /**
   *
   * @param gestioneWSDMManager
   */
   public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
     this.gestioneWSDMManager = gestioneWSDMManager;
   }

   /**
   *
   * @param cifraturaBusteManager
   */
   public void setCifraturaBusteManager(CifraturaBusteManager cifraturaBusteManager) {
     this.cifraturaBusteManager = cifraturaBusteManager;
   }

   /**
   *
   * @param cifraturaBusteManager
   */
   public void setControlliOepvManager(ControlliOepvManager controlliOepvManager) {
     this.controlliOepvManager = controlliOepvManager;
   }


   public void setPgManagerEst1(PgManagerEst1 pgManagerEst1) {
     this.pgManagerEst1 = pgManagerEst1;
   }

  /**
   * Gestione dell'inserimento in un catalogo elettronico dei prodotti contenuti
   * nella comunicazione FS7 individuata da idcom
   *
   * @param idcom
   * @param userkey
   * @param comtipo
   *
   * @throws GestoreException
   *
   */
  public void insertProdotti(Long idcom, String userkey, String comtipo) throws GestoreException {
    String select = null;
    Date dataPresentazione = null;
    String codiceCatalogo = null;

    if (userkey != null && !"".equals(userkey)) {

      // Vengono letti i documenti associati ad ogni occorrenza di di W_INVCOM
      select = "select idprg,iddocdig from w_docdig where DIGENT = ? and digkey1 = ? and idprg = ? and dignomdoc = ? ";
      String digent = "W_INVCOM";
      String idprgW_DOCDIG = "PA";

      Vector datiW_DOCDIG = null;
      String nomeFileXML = nomeFileXML_FS7;
      if("FS8".equals(comtipo))
        nomeFileXML = nomeFileXML_FS8;

      try {
        datiW_DOCDIG = sqlManager.getVector(select, new Object[] { digent, idcom.toString(), idprgW_DOCDIG, nomeFileXML });

      } catch (SQLException e) {
        logger.error("Errore nella lettura della tabella W_DOCDIG della richiesta: IDCOM=" + idcom.toString(), e);
        throw new GestoreException("Errore nella lettura della tabella W_DOCDIG della richiesta: IDCOM=" + idcom.toString(), null, e);

      }
      String idprgW_INVCOM = null;
      Long iddocdig = null;
      if (datiW_DOCDIG != null) {
        if (((JdbcParametro) datiW_DOCDIG.get(0)).getValue() != null)
          idprgW_INVCOM = ((JdbcParametro) datiW_DOCDIG.get(0)).getStringValue();

        try {
          if (((JdbcParametro) datiW_DOCDIG.get(1)).getValue() != null) iddocdig = ((JdbcParametro) datiW_DOCDIG.get(1)).longValue();
        } catch (GestoreException e) {
          logger.error("Errore nella lettura della tabella W_DOCDIG della richiesta: IDCOM=" + idcom.toString(), e);
          throw e;
        }

        // Lettura del file xml immagazzinato nella tabella W_DOCDIG
        BlobFile fileAllegato = null;
        try {
          fileAllegato = fileAllegatoManager.getFileAllegato(idprgW_INVCOM, iddocdig);
        } catch (Exception e) {
          logger.error("Errore nella lettura del file allegato presente nella tabella W_DOCDIG della richiesta: IDCOM=" + idcom.toString(),
              e);
          throw new GestoreException("Errore nella lettura del file allegato presente nella tabella W_DOCDIG della richiesta: IDCOM="
              + idcom.toString(), null, e);
        }
        String xml = null;
        if (fileAllegato != null && fileAllegato.getStream() != null) {
          xml = new String(fileAllegato.getStream());
          GestioneProdottiDocument document;
          try {
            document = GestioneProdottiDocument.Factory.parse(xml);
            GestioneProdottiType gestioneProdotti = document.getGestioneProdotti();
            dataPresentazione = gestioneProdotti.getDataPresentazione().getTime();
            codiceCatalogo = gestioneProdotti.getCodiceCatalogo();
            ListaProdottiType listaProdotti = null;
            // Inserimento Prodotti
            if("FS7".equals(comtipo)){
              listaProdotti = gestioneProdotti.getInserimenti();
              gestioneProdotti(listaProdotti, codiceCatalogo, userkey, "INS",comtipo,idcom);
            }
            // Aggiornamento Prodotti
            listaProdotti = gestioneProdotti.getAggiornamenti();
            gestioneProdotti(listaProdotti, codiceCatalogo, userkey, "AGG",comtipo,idcom);
            // Archiviazione Prodotti
            if("FS7".equals(comtipo)){
              listaProdotti = gestioneProdotti.getArchiviazioni();
              gestioneProdotti(listaProdotti, codiceCatalogo, userkey, "ARC",comtipo,idcom);
            }
          } catch (XmlException e) {
            logger.error("Errore durante l'elaborazione della richiesta: IDCOM=" + idcom.toString(), e);
            throw new GestoreException("Errore durante l'elaborazione della richiesta: IDCOM=" + idcom.toString(), null, e);
          } catch (GestoreException e) {
            logger.error("Errore durante l'elaborazione della richiesta: IDCOM=" + idcom.toString(), e);
            throw e;
          } catch (Exception e) {
            logger.error("Errore durante l'elaborazione della richiesta: IDCOM=" + idcom.toString(), e);
            throw new GestoreException("Errore durante l'elaborazione della richiesta: IDCOM=" + idcom.toString(), null, e);
          }
        }
      }
      // Gestione file firmato digitalmente, si deve inserire una occorrenza in
      // IMPRDOCG
      try {
        select = "select iddocdig from w_docdig where DIGENT = ? and digkey1 = ? and idprg = ? and dignomdoc like ?";
        iddocdig = (Long) sqlManager.getObject(select,
            new Object[] { digent, idcom.toString(), idprgW_DOCDIG, "%" + estensioneFileFirmato });

        if (iddocdig != null) {

          Long maxNorddoci = null;

          maxNorddoci = (Long) sqlManager.getObject("select max(NORDDOCI) from IMPRDOCG where CODGAR=? and CODIMP=?", new Object[] {
              "$" + codiceCatalogo, userkey });

          if (maxNorddoci == null) maxNorddoci = new Long(0);

          maxNorddoci = new Long(maxNorddoci.longValue() + 1);
          String oraRilascio = null;
          if (dataPresentazione != null) {
            Format formatter = new SimpleDateFormat("HH:mm:ss");
            oraRilascio = formatter.format(dataPresentazione);
          }

          sqlManager.update(
              "insert into IMPRDOCG(CODGAR,CODIMP,NORDDOCI,NGARA,PROVENI,IDPRG,DATARILASCIO,DESCRIZIONE,ORARILASCIO,DOCTEL,SITUAZDOCI) values(?,?,?,?,?,?,?,?,?,?,?)",
              new Object[] { "$" + codiceCatalogo, userkey, maxNorddoci, codiceCatalogo, new Long(2), "PG", dataPresentazione,
                  "Aggiornamento prodotti a catalogo", oraRilascio,"1", new Long(2) });

          Long maxIddocdig = (Long) sqlManager.getObject("select max(iddocdig)+1 from w_docdig where idprg=?", new Object[] { "PG" });

          // Aggiornamento della w_docdig contenente il file firmato
          // digitalmente
          String update = "update w_docdig set idprg=?, digent=?, digkey1=?, digkey2=?, iddocdig = ? " + "where idprg=? and iddocdig=?";
          sqlManager.update(update, new Object[] { "PG", "IMPRDOCG", "$" + codiceCatalogo, userkey, maxIddocdig, "PA", iddocdig });

          sqlManager.update("update imprdocg set iddocdg=? where codgar=? and ngara=? and codimp=? and norddoci=? and proveni=?", new Object[] {
              maxIddocdig, "$" + codiceCatalogo, codiceCatalogo, userkey, maxNorddoci, new Long(2) });

          //Aggironamento WSALLEGATI
          this.gestioneWSALLEGATI(idcom, "PG", maxIddocdig);
        }

      } catch (SQLException e) {
        logger.error("Errore nella gestione del file firmato digitalmente della comunicazione:" + idcom, e);
        throw new GestoreException("Errore nella gestione del file firmato digitalmente della comunicazione:" + idcom, null, e);
      }
      aggiornaStatoW_INVOCM(idcom, "6");

    }
  }

  /**
   * Gestione dei prodotti provenienti da una comunicazione da Portale Alice
   *
   * @param listaProdotti
   * @param codiceCatalogo
   * @param ditta
   * @param modo
   * @param tipoMessaggio
   *
   * @throws GestoreException
   *
   */
  private void gestioneProdotti(ListaProdottiType listaProdotti, String codiceCatalogo, String ditta, String modo, String tipoMessaggio, Long idComunicazione) throws GestoreException {
    if (listaProdotti != null && listaProdotti.sizeOfProdottoArray() > 0) {
      ProdottoType prodotto = null;
      long idarticolo = 0;
      long idprodotto = 0;
      Long idProdottoLong;
      String marcaProdottoProduttore;
      String codiceProdottoFornitore;
      String nome;
      String codiceProdottoProduttore;
      String descrizione;
      String dimensioni;
      double quantitaUMPrezzo;
      String aliquotaIVA;
      double prezzoUnitario;
      double quantitaUMAcquisto;
      double prezzoUnitarioPerAcquisto;
      int garanzia;
      int tempoConsegna;
      Calendar dataScadenzaOfferta;
      Long stato = null;
      String statoString = null;
      DocumentoType immagine;
      ListaDocumentiType certificazioniRichieste;
      ListaDocumentiType schedeTecniche;
      // Vector datiQualitativi = null;
      Date dataOdierna = null;

      for (int i = 0; i < listaProdotti.sizeOfProdottoArray(); i++) {
        prodotto = listaProdotti.getProdottoArray(i);
        idarticolo = prodotto.getIdArticolo();
        idprodotto = prodotto.getIdProdotto();
        marcaProdottoProduttore = prodotto.getMarcaProdottoProduttore();
        codiceProdottoProduttore = prodotto.getCodiceProdottoProduttore();
        nome = prodotto.getNomeCommerciale();
        codiceProdottoFornitore = prodotto.getCodiceProdottoFornitore();
        descrizione = prodotto.getDescrizioneAggiuntiva();
        dimensioni = prodotto.getDimensioni();
        quantitaUMPrezzo = prodotto.getQuantitaUMPrezzo();
        aliquotaIVA = prodotto.getAliquotaIVA();
        prezzoUnitario = prodotto.getPrezzoUnitario();
        quantitaUMAcquisto = prodotto.getQuantitaUMAcquisto();
        garanzia = prodotto.getGaranzia();
        prezzoUnitarioPerAcquisto = prodotto.getPrezzoUnitarioPerAcquisto();
        tempoConsegna = prodotto.getTempoConsegna();
        dataScadenzaOfferta = prodotto.getDataScadenzaOfferta();
        immagine = prodotto.getImmagine();
        certificazioniRichieste = prodotto.getCertificazioniRichieste();
        schedeTecniche = prodotto.getSchedeTecniche();
        statoString = prodotto.getStato();
        if (statoString != null && !"".equals(statoString)) stato = Long.parseLong(statoString);

        if ("INS".equals(modo)) {
          idProdottoLong = new Long(genChiaviManager.getNextId("MEISCRIZPROD"));
        } else {
          idProdottoLong = new Long(idprodotto);
        }

        Vector<DataColumn> elencoCampiMEISCRIZPROD = new Vector<DataColumn>();
        elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.ID", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idProdottoLong)));
        //if (!"ARC".equals(modo)) {
          elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.CODGAR", new JdbcParametro(JdbcParametro.TIPO_TESTO, "$"
              + codiceCatalogo)));
          elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.CODIMP", new JdbcParametro(JdbcParametro.TIPO_TESTO, ditta)));
          elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceCatalogo)));
          elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.IDARTCAT", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(
              idarticolo))));
          elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.MARCAPRODUT", new JdbcParametro(JdbcParametro.TIPO_TESTO,
              marcaProdottoProduttore)));
          elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.CODPRODUT", new JdbcParametro(JdbcParametro.TIPO_TESTO,
              codiceProdottoProduttore)));
          elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.NOME", new JdbcParametro(JdbcParametro.TIPO_TESTO, nome)));
          elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.CODOE", new JdbcParametro(JdbcParametro.TIPO_TESTO,
              codiceProdottoFornitore)));
          elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.DESCAGG", new JdbcParametro(JdbcParametro.TIPO_TESTO, descrizione)));
          elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.DIMENSIONI", new JdbcParametro(JdbcParametro.TIPO_TESTO, dimensioni)));
          elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.QUNIMISPRZ", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Double(
              quantitaUMPrezzo))));
          if (aliquotaIVA != null && !"".equals(aliquotaIVA)) {
            elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.PERCIVA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(
                aliquotaIVA))));
          }
          elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.PRZUNIT", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Double(
              prezzoUnitario))));
          elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.QUNIMISACQ", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Double(
              quantitaUMAcquisto))));
          elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.GARANZIA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(
              garanzia))));
          elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.PRZUNITPROD", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Double(
              prezzoUnitarioPerAcquisto))));
          elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.TEMPOCONS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(
              tempoConsegna))));
          elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.DATSCADOFF", new JdbcParametro(JdbcParametro.TIPO_DATA,
              new java.sql.Date(dataScadenzaOfferta.getTime().getTime()))));
        //}

        DataColumnContainer containerMEISCRIZPROD = null;

        if ("INS".equals(modo)) {
          // Inserimento di un prodotto
          elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, stato)));

          containerMEISCRIZPROD = new DataColumnContainer(elencoCampiMEISCRIZPROD);
          dataOdierna = UtilityDate.getDataOdiernaAsDate();
          containerMEISCRIZPROD.addColumn("MEISCRIZPROD.DATINS", new Timestamp(dataOdierna.getTime()));
          containerMEISCRIZPROD.addColumn("MEISCRIZPROD.DATMOD", new Timestamp(dataOdierna.getTime()));

          try {
            containerMEISCRIZPROD.insert("MEISCRIZPROD", sqlManager);
          } catch (SQLException e) {
            logger.error("Errore nell'inserimento del prodotto per il catalogo " + codiceCatalogo, e);
            throw new GestoreException("Errore nell'inserimento del prodotto per il catalogo " + codiceCatalogo, null, e);
          }

          Long idProdottoStoricizzato = gestioneStorico(idProdottoLong,modo,containerMEISCRIZPROD,UtilityDate.convertiData(dataOdierna, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));

          try {
            //gestioneDocumenti(idProdottoLong, immagine, new Long(1),idProdottoStoricizzato);
            if (immagine != null) insertDocumento(idProdottoLong, immagine, new Long(1), idProdottoStoricizzato,true, idComunicazione);
          } catch (GestoreException e) {
            logger.error("Errore nell'inserimento dell'immagine di un nuovo prodotto per il catalogo " + codiceCatalogo, e);
            throw e;
          } catch (Exception e) {
            logger.error("Errore nell'inserimento dell'immagine di un nuovo prodotto per il catalogo " + codiceCatalogo, e);
            throw new GestoreException("Errore nell'inserimento dell'immagine di un nuovo prodotto per il catalogo " + codiceCatalogo,
                null, e);
          }

          try {
            //gestioneDocumenti(idProdottoLong, certificazioniRichieste, new Long(2),idProdottoStoricizzato);
            if (certificazioniRichieste != null) insertDocumenti(idProdottoLong, certificazioniRichieste, new Long(2),idProdottoStoricizzato, idComunicazione);
          } catch (GestoreException e) {
            logger.error("Errore nell'inserimento delle certificazioni di un nuovo prodotto per il catalogo " + codiceCatalogo, e);
            throw e;
          } catch (Exception e) {
            logger.error("Errore nell'inserimento delle certificazioni di un nuovo prodotto per il catalogo " + codiceCatalogo, e);
            throw new GestoreException("Errore nell'inserimento delle certificazioni di un nuovo prodotto per il catalogo "
                + codiceCatalogo, null, e);
          }

          try {
            //gestioneDocumenti(idProdottoLong, schedeTecniche, new Long(3),idProdottoStoricizzato);
            if (schedeTecniche != null) insertDocumenti(idProdottoLong, schedeTecniche, new Long(3),idProdottoStoricizzato, idComunicazione);
          } catch (GestoreException e) {
            logger.error("Errore nell'inserimento delle schede tecniche di un nuovo prodotto per il catalogo " + codiceCatalogo, e);
            throw e;
          } catch (Exception e) {
            logger.error("Errore nell'inserimento delle schede tecniche di un nuovo prodotto per il catalogo " + codiceCatalogo, e);
            throw new GestoreException("Errore nell'inserimento delle schede tecniche di un nuovo prodotto per il catalogo "
                + codiceCatalogo, null, e);
          }

        } else if ("AGG".equals(modo)) {
          // Modifica di un prodotto

          elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, stato)));
          containerMEISCRIZPROD = new DataColumnContainer(elencoCampiMEISCRIZPROD);
          containerMEISCRIZPROD.getColumn("MEISCRIZPROD.ID").setObjectOriginalValue(idProdottoLong);
          containerMEISCRIZPROD.getColumn("MEISCRIZPROD.ID").setChiave(true);
          dataOdierna = UtilityDate.getDataOdiernaAsDate();
          containerMEISCRIZPROD.addColumn("MEISCRIZPROD.DATMOD", new Timestamp(dataOdierna.getTime()));
          try {
            containerMEISCRIZPROD.update("MEISCRIZPROD", sqlManager);
          } catch (SQLException e) {
            logger.error("Errore nella modifica del prodotto:" + idprodotto + " per il catalogo " + codiceCatalogo, e);
            throw new GestoreException("Errore nella modifica del prodotto:" + idprodotto + " per il catalogo " + codiceCatalogo, null, e);
          } catch (Exception e) {
            logger.error("Errore nella modifica del prodotto:" + idprodotto + " per il catalogo " + codiceCatalogo, e);
            throw new GestoreException("Errore nella modifica del prodotto:" + idprodotto + " per il catalogo " + codiceCatalogo, null, e);
          }

          Long idProdottoStoricizzato = gestioneStorico(idProdottoLong,modo,containerMEISCRIZPROD,UtilityDate.convertiData(dataOdierna, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));

          if("FS7".equals(tipoMessaggio)){
            try {
              gestioneDocumenti(idProdottoLong, immagine, new Long(1),idProdottoStoricizzato, idComunicazione);
            } catch (GestoreException e) {
              logger.error("Errore nell'inserimento dell'immagine del prodotto:" + idprodotto + " per il catalogo " + codiceCatalogo, e);
              throw e;
            } catch (Exception e) {
              logger.error("Errore nell'inserimento dell'immagine del prodotto:" + idprodotto + " per il catalogo " + codiceCatalogo, e);
              throw new GestoreException("Errore nell'inserimento dell'immagine del prodotto:"
                  + idprodotto
                  + " per il catalogo "
                  + codiceCatalogo, null, e);
            }

            try {
              gestioneDocumenti(idProdottoLong, certificazioniRichieste, new Long(2),idProdottoStoricizzato, idComunicazione);
            } catch (GestoreException e) {
              logger.error("Errore nell'inserimento delle certificazioni del prodotto:" + idprodotto + " per il catalogo " + codiceCatalogo,
                  e);
              throw e;
            } catch (Exception e) {
              logger.error("Errore nell'inserimento delle certificazioni del prodotto:" + idprodotto + " per il catalogo " + codiceCatalogo,
                  e);
              throw new GestoreException("Errore nell'inserimento delle certificazioni del prodotto:"
                  + idprodotto
                  + " per il catalogo "
                  + codiceCatalogo, null, e);
            }

            try {
              gestioneDocumenti(idProdottoLong, schedeTecniche, new Long(3),idProdottoStoricizzato, idComunicazione);
            } catch (GestoreException e) {
              logger.error("Errore nell'inserimento delle schede tecniche del prodotto:" + idprodotto + " per il catalogo " + codiceCatalogo,
                  e);
              throw e;
            } catch (Exception e) {
              logger.error("Errore nell'inserimento delle schede tecniche del prodotto:" + idprodotto + " per il catalogo " + codiceCatalogo,
                  e);
              throw new GestoreException("Errore nell'inserimento delle schede tecniche del prodotto:"
                  + idprodotto
                  + " per il catalogo "
                  + codiceCatalogo, null, e);
            }

            //Storicizzazione dell'immagine
            this.archiviazioneDocumenti(idProdottoLong, idProdottoStoricizzato, new Long(1));
            //Storicizzazione delle certificazioni
            this.archiviazioneDocumenti(idProdottoLong, idProdottoStoricizzato, new Long(2));
            //Storicizzazione delle schede tecniche
            this.archiviazioneDocumenti(idProdottoLong, idProdottoStoricizzato, new Long(3));
          }
        } else {
          // Eliminazione/archiviazione di un prodotto

          try {

            elencoCampiMEISCRIZPROD.add(new DataColumn("MEISCRIZPROD.STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(5))));

            containerMEISCRIZPROD = new DataColumnContainer(elencoCampiMEISCRIZPROD);
            containerMEISCRIZPROD.getColumn("MEISCRIZPROD.ID").setObjectOriginalValue(idProdottoLong);
            containerMEISCRIZPROD.getColumn("MEISCRIZPROD.ID").setChiave(true);
            dataOdierna = UtilityDate.getDataOdiernaAsDate();
            containerMEISCRIZPROD.addColumn("MEISCRIZPROD.DATMOD", new Timestamp(dataOdierna.getTime()));

            containerMEISCRIZPROD.update("MEISCRIZPROD", sqlManager);
          } catch (SQLException e) {
            logger.error("Errore nell'archiviazione del prodotto:" + idprodotto + " per il catalogo " + codiceCatalogo, e);
            throw new GestoreException("Errore nell'archiviazione del prodotto:" + idprodotto + " per il catalogo " + codiceCatalogo, null,
                e);
          } catch (Exception e) {
            logger.error("Errore nell'archiviazione del prodotto:" + idprodotto + " per il catalogo " + codiceCatalogo, e);
            throw new GestoreException("Errore nell'archiviazione del prodotto:" + idprodotto + " per il catalogo " + codiceCatalogo, null,
                e);
          }

          Long idProdottoStoricizzato = gestioneStorico(idProdottoLong,modo,containerMEISCRIZPROD,UtilityDate.convertiData(dataOdierna, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
          //Storicizzazione dell'immagine
          this.archiviazioneDocumenti(idProdottoLong, idProdottoStoricizzato, new Long(1));
          //Storicizzazione delle certificazioni
          this.archiviazioneDocumenti(idProdottoLong, idProdottoStoricizzato, new Long(2));
          //Storicizzazione delle schede tecniche
          this.archiviazioneDocumenti(idProdottoLong, idProdottoStoricizzato, new Long(3));
        }
      }
    }
  }

  /**
   * Gestione dello storico del prodotto(inserimento in MESTOISCRIZPROD)
   *
   * @param idProdotto
   * @param modo
   * @param campiMeiscrizprod
   * @param dataModifica
   *
   * @throws GestoreException
   *
   *
   */
  private Long gestioneStorico(Long idProdotto, String modo,DataColumnContainer campiMeiscrizprod, String dataModifica) throws GestoreException {

    Long id = new Long(genChiaviManager.getNextId("MESTOISCRIZPROD"));
    Date datmod = null;
    Timestamp datfin = null;
    if (dataModifica != null && !"".equals(dataModifica)){
      datmod = UtilityDate.convertiData(dataModifica, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
      datfin = new Timestamp(datmod.getTime());
    }

    //Nel caso di aggiornamento e archiviazione prima si deve chiudere il record relativo ad idProdotto valorizzando la datfin
    if("AGG".equals(modo) || "ARC".equals(modo)){
      try {
        sqlManager.update("update mestoiscrizprod set datfin=? where idiscrizprod=? and datfin is null", new Object[]{datfin, idProdotto});
      } catch (SQLException e) {
        logger.error("Errore nella chiusura dello storico per il prodotto:" + idProdotto.toString(), e);
        throw new GestoreException("Errore nella chiusura dello storico per il prodotto:" + idProdotto.toString(), null, e);
      }
    }

    Vector<DataColumn> elencoCampiMESTOISCRIZPROD = new Vector<DataColumn>();
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.ID", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, id)));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.IDISCRIZPROD", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
        campiMeiscrizprod.getLong("MEISCRIZPROD.ID"))));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.CODGAR", new JdbcParametro(JdbcParametro.TIPO_TESTO,
        campiMeiscrizprod.getString("MEISCRIZPROD.CODGAR"))));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.CODIMP", new JdbcParametro(JdbcParametro.TIPO_TESTO,
        campiMeiscrizprod.getString("MEISCRIZPROD.CODIMP"))));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO,
        campiMeiscrizprod.getString("MEISCRIZPROD.NGARA"))));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.IDARTCAT", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
        campiMeiscrizprod.getLong("MEISCRIZPROD.IDARTCAT"))));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.QUNIMISPRZ", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
        campiMeiscrizprod.getDouble("MEISCRIZPROD.QUNIMISPRZ"))));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.QUNIMISACQ", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
        campiMeiscrizprod.getDouble("MEISCRIZPROD.QUNIMISACQ"))));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.TEMPOCONS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
        campiMeiscrizprod.getLong("MEISCRIZPROD.TEMPOCONS"))));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.MARCAPRODUT", new JdbcParametro(JdbcParametro.TIPO_TESTO,
        campiMeiscrizprod.getString("MEISCRIZPROD.MARCAPRODUT"))));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.CODPRODUT", new JdbcParametro(JdbcParametro.TIPO_TESTO,
        campiMeiscrizprod.getString("MEISCRIZPROD.CODPRODUT"))));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.NOME", new JdbcParametro(JdbcParametro.TIPO_TESTO,
        campiMeiscrizprod.getString("MEISCRIZPROD.NOME"))));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.CODOE", new JdbcParametro(JdbcParametro.TIPO_TESTO,
        campiMeiscrizprod.getString("MEISCRIZPROD.CODOE"))));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.PRZUNIT", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
        campiMeiscrizprod.getDouble("MEISCRIZPROD.PRZUNIT"))));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.PRZUNITPROD", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
        campiMeiscrizprod.getDouble("MEISCRIZPROD.PRZUNITPROD"))));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.DESCAGG", new JdbcParametro(JdbcParametro.TIPO_INDEFINITO,
        campiMeiscrizprod.getObject("MEISCRIZPROD.DESCAGG"))));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.DIMENSIONI", new JdbcParametro(JdbcParametro.TIPO_TESTO,
        campiMeiscrizprod.getString("MEISCRIZPROD.DIMENSIONI"))));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.GARANZIA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
        campiMeiscrizprod.getLong("MEISCRIZPROD.GARANZIA"))));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.PERCIVA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
        campiMeiscrizprod.getLong("MEISCRIZPROD.PERCIVA"))));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.DATSCADOFF", new JdbcParametro(JdbcParametro.TIPO_DATA,
        campiMeiscrizprod.getData("MEISCRIZPROD.DATSCADOFF"))));
    elencoCampiMESTOISCRIZPROD.add(new DataColumn("MESTOISCRIZPROD.STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
        campiMeiscrizprod.getLong("MEISCRIZPROD.STATO"))));


    DataColumnContainer containerMESTOISCRIZPROD = new DataColumnContainer(elencoCampiMESTOISCRIZPROD);
    containerMESTOISCRIZPROD.addColumn("MESTOISCRIZPROD.DATINI", datmod);

    try {
      containerMESTOISCRIZPROD.insert("MESTOISCRIZPROD", sqlManager);
    } catch (SQLException e) {
      logger.error("Errore nella scrittura nella tabella  MESTOISCRIZPROD del prodotto:" + idProdotto.toString(), e);
      throw new GestoreException("Errore nella scrittura nella tabella  MESTOISCRIZPROD del prodotto:" + idProdotto.toString(), null, e);
    } catch (Exception e) {
      logger.error("Errore nella scrittura nella tabella  MESTOISCRIZPROD del prodotto:" + idProdotto.toString(), e);
      throw new GestoreException("Errore nella scrittura nella tabella  MESTOISCRIZPROD del prodotto:" + idProdotto.toString(), null, e);
    }
    return id;
  }

  /**
   * Gestione dei documenti di un prodotto
   *
   * @param idProdotto
   * @param listaDocumenti
   * @param tipo
   * @param idProdottoStoricizzato
   *
   * @throws GestoreException
   *
   *
   */
  private void gestioneDocumenti(Long idProdotto, Object listaDocumenti, Long tipo, Long idProdottoStoricizzato, Long idComunicazione) throws GestoreException {
    //Se listaDocumenti è vuota, se in MEALLISCRIZPROD vi sono occorrenze vanno eliminate tutte
    if(listaDocumenti==null){
      try {
        Long conteggio = (Long)this.sqlManager.getObject("select count(id) from w_docdig a,mealliscrizprod b where a.idprg=b.idprg " +
        		"and a.iddocdig = b. iddocdig and b.tipo=? and b.idiscrizprod=?", new Object[]{tipo, idProdotto});
        if (conteggio!=null && conteggio.longValue()>0){
          this.sqlManager.update("delete from mealliscrizprod where idiscrizprod=? and tipo=?", new Object[]{idProdotto, tipo});
          this.sqlManager.update("delete from meallprod where idprod=? and tipo=?", new Object[]{idProdottoStoricizzato, tipo});
        }
      } catch (SQLException e) {
        logger.error("Errore durante l'eliminazione dei documenti di tipo:" + tipo.toString() +  " del prodotto: " + idProdotto.toString(), e);
        throw new GestoreException("Errore durante l'eliminazione dei documenti di tipo:" + tipo.toString() +  " del prodotto: " + idProdotto.toString(), null, e);
      }
    }else{
      if (tipo.longValue() == 1) {
        String azione = this.valutaAzioneDocumento(idProdotto, (DocumentoType) listaDocumenti, tipo);
        if("INS".equals(azione)|| "AGG".equals(azione)){
          if( "AGG".equals(azione)){
            //Cancellazione vecchia occorrenza da MEALLISCRIZPROD
            this.cancellazioneDocumento(((DocumentoType) listaDocumenti).getNomeFile(), idProdotto, tipo);
          }
          insertDocumento(idProdotto, (DocumentoType) listaDocumenti, tipo, idProdottoStoricizzato,false,idComunicazione);

        }
      } else {
        ListaDocumentiType listaDoc = (ListaDocumentiType) listaDocumenti;
        if (listaDoc.sizeOfDocumentoArray() > 0) {
          for (int i = 0; i < listaDoc.sizeOfDocumentoArray(); i++){
            String azione = this.valutaAzioneDocumento(idProdotto, listaDoc.getDocumentoArray(i), tipo);
            if("INS".equals(azione)|| "AGG".equals(azione)){
              if( "AGG".equals(azione)){
                //Cancellazione vecchia occorrenza da MEALLISCRIZPROD
                this.cancellazioneDocumento(listaDoc.getDocumentoArray(i).getNomeFile(), idProdotto, tipo);
              }

              insertDocumento(idProdotto, listaDoc.getDocumentoArray(i), tipo, idProdottoStoricizzato,false, idComunicazione);
            }

          }
        }
      }
      this.eliminazioneDocumenti(idProdotto, listaDocumenti, tipo);
    }

  }

  /**
   * Inserimento di più documenti in un prodotto
   *
   * @param idProdotto
   * @param listaDoc
   * @param tipo
   * @param idProdottoStoricizzato
   *
   * @throws GestoreException
   *
   *
   */
  private void insertDocumenti(Long idProdotto, ListaDocumentiType listaDoc, Long tipo, Long idProdottoStoricizzato, Long idComunicazione) throws GestoreException {
    if (listaDoc.sizeOfDocumentoArray() > 0) {
      for (int i = 0; i < listaDoc.sizeOfDocumentoArray(); i++)
        insertDocumento(idProdotto, listaDoc.getDocumentoArray(i), tipo, idProdottoStoricizzato,true, idComunicazione);
    }
  }


  /**
   * Viene effettuato l'inserimento di un documento in W_DOCDIG e
   * MEALLISCRIZPROD. Se storicizzazione è true si inserisce pure in
   * MEALLPROD
   *
   * @param idProdotto
   * @param documento
   * @param tipo
   * @param idProdottoStoricizzaro
   * @param storicizzazione
   *
   * @throws GestoreException
   *
   */
  private void insertDocumento(Long idProdotto, DocumentoType documento, Long tipo, Long idProdottoStoricizzaro,boolean storicizzazione,Long idComunicazione) throws GestoreException {

    // Inserimento in W_DOCDIG
    String nomeFile = documento.getNomeFile();
    byte[] file = documento.getFile();
    if (file != null) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
        baos.write(file);
      } catch (IOException e) {
        logger.error("Errore durante la lettura del file " + nomeFile + " del prodotto:" + idProdotto.toString(), e);
        throw new GestoreException("Errore durante la lettura del file " + nomeFile + " del prodotto:" + idProdotto.toString(), null, e);
      } catch (Exception e) {
        logger.error("Errore durante la lettura del file " + nomeFile + " del prodotto:" + idProdotto.toString(), e);
        throw new GestoreException("Errore durante la lettura del file " + nomeFile + " del prodotto:" + idProdotto.toString(), null, e);
      }

      try {
        Long newIDDOCDIG = new Long(1);
        // Si deve calcolare il valore di IDDOCDIG
        Long maxIDDOCDIG = (Long) this.sqlManager.getObject("select max(IDDOCDIG) from W_DOCDIG where IDPRG= ?", new Object[] { "PG" });

        if (maxIDDOCDIG != null && maxIDDOCDIG.longValue() > 0) newIDDOCDIG = maxIDDOCDIG.longValue() + 1;

        Vector<DataColumn> elencoCampiW_DOCDIG = new Vector<DataColumn>();

        elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")));
        elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.IDDOCDIG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, newIDDOCDIG)));
        elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.DIGENT", new JdbcParametro(JdbcParametro.TIPO_TESTO, "MEALLISCRIZPROD")));
        elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.DIGNOMDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeFile)));
        elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.DIGOGG", new JdbcParametro(JdbcParametro.TIPO_BINARIO, baos)));
        logger.info("documento.getFirmacheck(): "+documento.getFirmacheck());
        if(StringUtils.isNotBlank(documento.getFirmacheck())){
        	elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.FIRMACHECK", new JdbcParametro(JdbcParametro.TIPO_TESTO, documento.getFirmacheck())));
        	if(documento.getFirmacheckts()!=null) {
        		elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.FIRMACHECKTS", new JdbcParametro(JdbcParametro.TIPO_DATA_FULL, documento.getFirmacheckts().getTime())));
        	}
        }
        DataColumnContainer containerW_DOCDIG = new DataColumnContainer(elencoCampiW_DOCDIG);

        containerW_DOCDIG.insert("W_DOCDIG", sqlManager);

        // Inserimento in MEALLISCRIZPROD
        Long idMEALLISCRIZPROD = new Long(genChiaviManager.getNextId("MEALLISCRIZPROD"));
        Vector<DataColumn> elencoCampiMEALLISCRIZPROD = new Vector<DataColumn>();

        elencoCampiMEALLISCRIZPROD.add(new DataColumn("MEALLISCRIZPROD.ID", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
            idMEALLISCRIZPROD)));
        elencoCampiMEALLISCRIZPROD.add(new DataColumn("MEALLISCRIZPROD.IDISCRIZPROD", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
            idProdotto)));
        elencoCampiMEALLISCRIZPROD.add(new DataColumn("MEALLISCRIZPROD.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")));
        elencoCampiMEALLISCRIZPROD.add(new DataColumn("MEALLISCRIZPROD.IDDOCDIG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
            newIDDOCDIG)));
        elencoCampiMEALLISCRIZPROD.add(new DataColumn("MEALLISCRIZPROD.TIPO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, tipo)));

        DataColumnContainer containerMEALLISCRIZPROD = new DataColumnContainer(elencoCampiMEALLISCRIZPROD);

        containerMEALLISCRIZPROD.insert("MEALLISCRIZPROD", sqlManager);


        if(storicizzazione){
          //Inserimento in MEALLPROD
          Long idMEALLPROD = new Long(genChiaviManager.getNextId("MEALLPROD"));
          Vector<DataColumn> elencoCampiMEALLPROD = new Vector<DataColumn>();

          elencoCampiMEALLPROD.add(new DataColumn("MEALLPROD.ID", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
              idMEALLPROD)));
          elencoCampiMEALLPROD.add(new DataColumn("MEALLPROD.IDPROD", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
              idProdottoStoricizzaro)));
          elencoCampiMEALLPROD.add(new DataColumn("MEALLPROD.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")));
          elencoCampiMEALLPROD.add(new DataColumn("MEALLPROD.IDDOCDIG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
              newIDDOCDIG)));
          elencoCampiMEALLPROD.add(new DataColumn("MEALLPROD.TIPO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, tipo)));

          DataColumnContainer containerMEALLPROD = new DataColumnContainer(elencoCampiMEALLPROD);

          containerMEALLPROD.insert("MEALLPROD", sqlManager);
        }


        this.sqlManager.update("update w_docdig set digkey1=? where idprg=? and iddocdig=?", new Object[] { idMEALLISCRIZPROD.toString(),
            "PG", newIDDOCDIG });
      } catch (SQLException e) {
        String msg = "Errore nell'inserimento ";
        if (tipo.longValue() == 1)
          msg += "dell'immagine ";
        else if (tipo.longValue() == 2)
          msg += "della certificazione ";
        else
          msg += "della scheda tecnica ";

        msg += " del prodotto:" + idProdotto.toString();

        logger.error(msg, e);
        throw new GestoreException(msg, null, e);
      } catch (Exception e) {
        String msg = "Errore nell'inserimento ";
        if (tipo.longValue() == 1)
          msg += "dell'immagine ";
        else if (tipo.longValue() == 2)
          msg += "della certificazione ";
        else
          msg += "della scheda tecnica ";

        msg += " del prodotto:" + idProdotto.toString();

        logger.error(msg, e);
        throw new GestoreException(msg, null, e);
      }
    }

  }

  /**
   * Viene aggiornato lo stato di una occorrenza di W_INVCOM.
   *
   * @param idcom
   * @param stato
   *
   * @throws GestoreException
   */
  public void aggiornaStatoW_INVOCM(Long idcom, String stato) throws GestoreException {

    try {
      sqlManager.update("update W_INVCOM set COMSTATO = ?, COMDATASTATO = ? where IDPRG=? and IDCOM=?", new Object[] { stato,
          new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), "PA", idcom.toString() });
    } catch (SQLException e) {
      throw new GestoreException("Errore nell'aggiornamento dello stato dell'entità W_INVCOM", null, e);
    }

  }

  /**
   * Controllo disallineamento prodotto.
   * Metodo utilizzato per richiamare il controllo dalla maschera dell'ordine.
   * @param ngara
   * @return
   * @throws GestoreException
   */
  public HashMap<String, Object> controlloDisallineamentoProdottiGARE(String ngara) throws GestoreException {

    HashMap<String, Object> infoControllo = new HashMap<String, Object>();

    try {
      List<?> datiGARE = this.sqlManager.getVector("select idric, ditta from gare where ngara = ?", new Object[] { ngara });
      if (datiGARE != null && datiGARE.size() > 0) {
        Long idric = (Long) SqlManager.getValueFromVectorParam(datiGARE, 0).getValue();
        String ditta = (String) SqlManager.getValueFromVectorParam(datiGARE, 1).getValue();
        infoControllo = this.controlloDisallineamentoProdotti(idric, ditta);
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore durante il controllo di disallineamento dei prodotti", null, e);
    }

    return infoControllo;
  }

  /**
   * Controllo disallineamento prodotti.
   * Metodo utilizzato per richiamare il controllo dalla ricerca di mercato
   *
   * @param meric_id
   * @throws GestoreException
   */
  public HashMap<String, Object> controlloDisallineamentoProdotti(Long meric_id, String codimp) throws GestoreException {

    HashMap<String, Object> infoControllo = new HashMap<String, Object>();

    List<Object> listaControlloArticoli = new Vector<Object>();
    int numeroErrori = 0;
    int numeroWarning = 0;

    try {

      String selectMERICART = "select mericart.id, " // 0
          + " mericart.idartcat, " // 1
          + " meartcat.descr," // 2
          + " meartcat.colore " // 3
          + " from mericart, meartcat "
          + " where mericart.idartcat = meartcat.id "
          + " and mericart.idric = ?";

      String selectMERICPROD = "select mericprod.id, " // 0
          + " mericprod.idprod, " // 1
          + " mericprod.codimp, " // 2
          + " mericprod.acquista, " // 3
          + " v_meprodotti.nome, " // 4
          + " v_meprodotti.datfin, " // 5
          + " v_meprodotti.datscadoff, " // 6
          + " v_meprodotti.idiscrizprod " // 7
          + " from mericprod, v_meprodotti "
          + " where mericprod.idprod = v_meprodotti.id "
          + " and mericprod.idricart = ? ";

      String selectV_SEL_MEPRODOTTI = "select distinct v_sel_meprodotti.id, " // 0
          + " v_sel_meprodotti.nome, " // 1
          + " v_sel_meprodotti.idiscrizprod " // 2
          + " from v_sel_meprodotti, ditg "
          + " where v_sel_meprodotti.idartcat = ? "
          + " and v_sel_meprodotti.stato = 4 "
          + " and v_sel_meprodotti.datscadoff > ? "
          + " and ditg.dittao = v_sel_meprodotti.codimp "
          + " and ditg.abilitaz = 1";

      // Lettura di tutti gli articoli della ricerca di mercato
      List<?> datiMERICART = this.sqlManager.getListVector(selectMERICART, new Object[] { meric_id });
      if (datiMERICART != null && datiMERICART.size() > 0) {
        for (int iART = 0; iART < datiMERICART.size(); iART++) {

          List<Object> listaControlloProdotti = new Vector<Object>();
          List<Object> listaControlloOperatori = new Vector<Object>();

          Long mericart_id = (Long) SqlManager.getValueFromVectorParam(datiMERICART.get(iART), 0).getValue();
          Long mericart_idartcat = (Long) SqlManager.getValueFromVectorParam(datiMERICART.get(iART), 1).getValue();
          String meartcat_descr = (String) SqlManager.getValueFromVectorParam(datiMERICART.get(iART), 2).getValue();
          String meartcat_colore = (String) SqlManager.getValueFromVectorParam(datiMERICART.get(iART), 3).getValue();

          if (meartcat_colore != null) {
            meartcat_descr += " (" + meartcat_colore + ")";
          }

          // Lettura di tutti i prodotti dell'articolo
          List<?> datiMERICPROD = this.sqlManager.getListVector(selectMERICPROD, new Object[] { mericart_id });

          // Controllo dei prodotti gia' presenti nella valutazione prodotti
          // Si controllano:
          // - data fine validita' del prodotto
          // - data validita' dell'offerta
          // - abilitazione dell'operatore
          if (datiMERICPROD != null && datiMERICPROD.size() > 0) {
            for (int iPROD = 0; iPROD < datiMERICPROD.size(); iPROD++) {
              Long mericprod_id = (Long) SqlManager.getValueFromVectorParam(datiMERICPROD.get(iPROD), 0).getValue();
              Long mericprod_idprod = (Long) SqlManager.getValueFromVectorParam(datiMERICPROD.get(iPROD), 1).getValue();
              String mericprod_codimp = (String) SqlManager.getValueFromVectorParam(datiMERICPROD.get(iPROD), 2).getValue();
              String mericprod_acquista = (String) SqlManager.getValueFromVectorParam(datiMERICPROD.get(iPROD), 3).getValue();

              // Gestione del tipo errore
              // Se il prodotto e' selezionato nella bozza ordine i messaggi di
              // controllo
              // devono essere bloccanti (di tipo 'E') altrimenti non bloccanti
              // (di tipo 'W').
              String tipoControllo = null;
              if (mericprod_acquista != null
                  && mericprod_acquista.equals("1")
                  && mericprod_codimp != null
                  && codimp != null
                  && mericprod_codimp.equals(codimp)) {
                tipoControllo = "E";
              } else {
                tipoControllo = "W";
              }

              String v_meprodotti_nome = (String) SqlManager.getValueFromVectorParam(datiMERICPROD.get(iPROD), 4).getValue();
              Date v_meprodotti_datfin = (Date) SqlManager.getValueFromVectorParam(datiMERICPROD.get(iPROD), 5).getValue();
              Date v_meprodotti_datscadoff = (Date) SqlManager.getValueFromVectorParam(datiMERICPROD.get(iPROD), 6).getValue();
              Long v_meprodotti_idiscrizprod = (Long) SqlManager.getValueFromVectorParam(datiMERICPROD.get(iPROD), 7).getValue();

              // Data fine validita' del prodotto
              if (v_meprodotti_datfin != null) {
                listaControlloProdotti.add(((new Object[] { tipoControllo, v_meprodotti_nome, "il prodotto e' stato aggiornato" })));
                if (tipoControllo.equals("E")) {
                  numeroErrori++;
                } else {
                  numeroWarning++;
                }
              }

              // Data validita' dell'offerta
              Timestamp dataOdierna = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
              if (v_meprodotti_datscadoff != null && v_meprodotti_datscadoff.before(dataOdierna)) {
                listaControlloProdotti.add(((new Object[] { tipoControllo, v_meprodotti_nome,
                    "per il prodotto indicato e' scaduta la validita' dell'offerta" })));
                if (tipoControllo.equals("E")) {
                  numeroErrori++;
                } else {
                  numeroWarning++;
                }
              }

              // Controllo abilitazione operatore
              String codcata = (String) this.sqlManager.getObject("select codcata from meric where id = ?", new Object[] { meric_id });
              String selectDITG = "select abilitaz from ditg where dittao = ? and ngara5 = ? and codgar5 = ?";
              Long abilitaz = (Long) sqlManager.getObject(selectDITG, new Object[] { mericprod_codimp, codcata, "$" + codcata });
              if (abilitaz != null && !abilitaz.equals(new Long(1))) {
                String nomest = (String) this.sqlManager.getObject("select nomest from impr where codimp = ?",
                    new Object[] { mericprod_codimp });
                listaControlloOperatori.add(((new Object[] { tipoControllo, v_meprodotti_nome,
                    "l'operatore " + nomest + " non e' abilitato al catalogo" })));
                if (tipoControllo.equals("E")) {
                  numeroErrori++;
                } else {
                  numeroWarning++;
                }
              }
            }
          }

          // Confronto della lista dei prodotti gia' presenti nella
          // valutazione prodotti con la lista dei prodotti selezionabili
          // Se ci sono nuovo prodotti selezionabili si deve dare notifica.
          List<Object> listaNuoviProdotti = new Vector<Object>();
          List<?> datiV_SEL_MEPRODOTTI = this.sqlManager.getListVector(selectV_SEL_MEPRODOTTI, new Object[] { mericart_idartcat,
              new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()) });
          if (datiV_SEL_MEPRODOTTI != null && datiV_SEL_MEPRODOTTI.size() > 0 && datiMERICPROD != null && datiMERICPROD.size() > 0) {
            for (int iSEL = 0; iSEL < datiV_SEL_MEPRODOTTI.size(); iSEL++) {
              String v_sel_meprodotti_nome = (String) SqlManager.getValueFromVectorParam(datiV_SEL_MEPRODOTTI.get(iSEL), 1).getValue();
              Long v_sel_meprodotti_idiscrizprod = (Long) SqlManager.getValueFromVectorParam(datiV_SEL_MEPRODOTTI.get(iSEL), 2).getValue();

              boolean prodottoTrovato = false;

              if (datiMERICPROD != null && datiMERICPROD.size() > 0) {
                for (int iPROD = 0; iPROD < datiMERICPROD.size(); iPROD++) {
                  Long v_meprodotti_idiscrizprod = (Long) SqlManager.getValueFromVectorParam(datiMERICPROD.get(iPROD), 7).getValue();
                  if (v_meprodotti_idiscrizprod.equals(v_sel_meprodotti_idiscrizprod)) prodottoTrovato = true;
                }
              }

              if (!prodottoTrovato) {
                listaNuoviProdotti.add(((new Object[] { v_sel_meprodotti_nome })));
                numeroWarning++;
              }
            }
          }

          if (!listaControlloProdotti.isEmpty() || !listaControlloOperatori.isEmpty() || !listaNuoviProdotti.isEmpty()) {
            listaControlloArticoli.add(((new Object[] { meartcat_descr, listaControlloProdotti, listaControlloOperatori,
                listaNuoviProdotti })));
          }

        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore durante il controllo di disallineamento dei prodotti", null, e);
    }

    infoControllo.put("listaControlloArticoli", listaControlloArticoli);
    infoControllo.put("numeroErrori", new Long(numeroErrori));
    infoControllo.put("numeroWarning", new Long(numeroWarning));

    return infoControllo;
  }

  /**
   * Annullamento della valutazione prodotti.
   *
   * @param meric_id
   * @throws GestoreException
   */
  public void annullaValutazioneProdotti(Long meric_id) throws GestoreException {

    try {
      String updateMERICPROD = "update mericprod set acquista = null where idricart in (select id from mericart where idric = ?)";
      this.sqlManager.update(updateMERICPROD, new Object[] { meric_id });

      String updateMERIC = "update meric set datval = null where id = ?";
      this.sqlManager.update(updateMERIC, new Object[] { meric_id });

    } catch (SQLException e) {
      throw new GestoreException("Errore durante l'annullamento della valutazione prodotti", null, e);
    }

  }

  /**
   * Allineamento dell'intero carrello articoli/prodotti di una ricerca di
   * mercato.
   *
   * @param meric_id
   * @throws SQLException
   */
  public void valutazioneProdotti(Long meric_id) throws GestoreException {

    try {
      String selectMERICART = "select mericart.id, mericart.idartcat, mericart.quanti from mericart where idric = ?";
      List<?> datiMERICART = this.sqlManager.getListVector(selectMERICART, new Object[] { meric_id });
      if (datiMERICART != null && datiMERICART.size() > 0) {
        for (int i = 0; i < datiMERICART.size(); i++) {
          Long mericart_id = (Long) SqlManager.getValueFromVectorParam(datiMERICART.get(i), 0).getValue();
          Long meartcat_id = (Long) SqlManager.getValueFromVectorParam(datiMERICART.get(i), 1).getValue();
          Double mericart_quanti = (Double) SqlManager.getValueFromVectorParam(datiMERICART.get(i), 2).getValue();

          // Allineamento del singolo prodotto
          this.valutazioneProdotto(mericart_id, meartcat_id, mericart_quanti);

          // Aggiornamento della data di valutazione prodotti
          Timestamp dataValutazioneProdotti = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
          String updateMERIC = "update meric set datval = ? where id = ?";
          this.sqlManager.update(updateMERIC, new Object[] { dataValutazioneProdotti, meric_id });

        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore durante l'allineamento prodotti", null, e);
    }

  }

  /**
   * Gestione allineamento dei prodotti per un singolo articolo.
   *
   * @param mericart_id
   * @param mericart_idartcat
   * @param mericart_quanti
   * @throws SQLException
   */
  private void valutazioneProdotto(Long mericart_id, Long meartcat_id, Double mericart_quanti) throws GestoreException {
    try {
      // Lettura informazioni sul carrello articoli/prodotti gia' caricato
      String selectMERICPROD = "select id, idprod from mericprod where idricart = ?";
      List<?> datiMERICPROD = this.sqlManager.getListVector(selectMERICPROD, new Object[] { mericart_id });

      // Lettura informazioni sugli articoli/prodotti presenti nelle tabelle di
      // iscrizione delle imprese fornitrici
      String codcata = (String) this.sqlManager.getObject(
          "select codcata from meric, mericart where meric.id = mericart.idric and mericart.id = ?", new Object[] { mericart_id });

      Long przunitper = (Long)this.sqlManager.getObject(
          "select przunitper from meartcat where meartcat.id = ?", new Object[] { meartcat_id });

      // Per la selezione dei prodotti da caricare utilizzo
      // la vista V_SEL_MEPRODOTTI che presenta, sulla tabella
      // MESTOISCRIZPROD dello storico, i prodotti in linea.

      String selectMEISCRIZPROD = "select v_sel_meprodotti.codimp, " // 0
          + " v_sel_meprodotti.qunimisacq, " // 1
          + " v_sel_meprodotti.przunitprod, " // 2
          + " v_sel_meprodotti.perciva, " // 3
          + " v_sel_meprodotti.id " // 4
          + " from v_sel_meprodotti, ditg "
          + " where v_sel_meprodotti.idartcat = ? "
          + " and v_sel_meprodotti.stato = 4 "
          + " and v_sel_meprodotti.datscadoff > ? "
          + " and ditg.dittao = v_sel_meprodotti.codimp "
          + " and ditg.ngara5 = ? "
          + " and ditg.codgar5 = ? "
          + " and ditg.abilitaz = 1";

      List<?> datiMEISCRIZPROD = this.sqlManager.getListVector(selectMEISCRIZPROD, new Object[] { meartcat_id,
          new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), codcata, "$" + codcata });

      // Cancellazione dei prodotti della ricerca di mercato non piu' presenti
      // tra
      // i prodotti forniti dalle
      // imprese fornitrici. La cancellazione procede se il carrello
      // articoli/prodotti è popolato
      if (datiMERICPROD != null && datiMERICPROD.size() > 0) {
        for (int i = 0; i < datiMERICPROD.size(); i++) {
          // Identificativo del prodotto nella ricerca di mercato
          Long mericprod_id = (Long) SqlManager.getValueFromVectorParam(datiMERICPROD.get(i), 0).getValue();

          // Identificativo del prodotto offerto dall'impresa fornitrice
          Long mericprod_idprod = (Long) SqlManager.getValueFromVectorParam(datiMERICPROD.get(i), 1).getValue();

          boolean prodottoTrovato = false;
          if (datiMEISCRIZPROD != null && datiMEISCRIZPROD.size() > 0) {
            for (int j = 0; j < datiMEISCRIZPROD.size(); j++) {
              Long meiscrizprod_id = (Long) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(j), 4).getValue();
              if (mericprod_idprod.equals(meiscrizprod_id)) prodottoTrovato = true;
            }
          }

          // Se non e' stato trovato un prodotto corrispondente si procedere
          // alla
          // cancellazione
          // del prodotto stesso dalla ricerca di mercato.
          if (!prodottoTrovato) this.sqlManager.update("delete from mericprod where id = ?", new Object[] { mericprod_id });

        }
      }

      // Inserimento dei nuovi prodotti o aggiornamento dei prodotti esistenti
      if (datiMEISCRIZPROD != null && datiMEISCRIZPROD.size() > 0) {
        for (int i = 0; i < datiMEISCRIZPROD.size(); i++) {
          // Codice dell'impresa fornitrice
          String meiscrizprod_codimp = (String) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 0).getValue();

          // Quantita' di unita' di misura indicata dall'impresa fornitrice
          Double meiscrizprod_qunimisacq = (Double) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 1).getValue();

          // Prezzo unitario indicato dall'impresa fornitrice
          Double meiscrizprod_przunitprod = (Double) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 2).getValue();

          // Percentuale IVA indicato dall'impresa fornitrice
          Long meiscrizprod_perciva = (Long) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 3).getValue();

          // Identificativo univoco del prodotto
          Long meiscrizprod_id = (Long) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 4).getValue();

          // Gestione della quantita' e del prezzo offerto
          Double mericprod_quanti = null;
          Double mericprod_preoff = null;
          if(przunitper!=null && przunitper.longValue()==4){
            if (mericart_quanti != null){
              mericprod_quanti = mericart_quanti;
              mericprod_preoff = new Double(meiscrizprod_przunitprod.doubleValue() * mericprod_quanti.doubleValue());
            }
          }else{
            if (mericart_quanti != null
                && mericart_quanti.doubleValue() > 0
                && meiscrizprod_qunimisacq != null
                && meiscrizprod_qunimisacq.doubleValue() > 0) {
              double quanti_tmp = (Math.ceil(mericart_quanti.doubleValue() / meiscrizprod_qunimisacq.doubleValue()))
                  * meiscrizprod_qunimisacq.doubleValue();
              mericprod_quanti = new Double(quanti_tmp);
              mericprod_preoff = new Double(meiscrizprod_przunitprod.doubleValue() * mericprod_quanti.doubleValue());
            }
          }


          // Verifica, sulla base del codice univoco del prodotto, se esiste il
          // prodotto stesso nella ricerca di mercato
          Long cnt = (Long) this.sqlManager.getObject("select count(*) from mericprod where idprod = ? and idricart = ?", new Object[] {
              meiscrizprod_id, mericart_id });
          if (cnt != null && cnt.longValue() > 0) {
            // Il prodotto e' gia' presente nella ricerca di mercato: si procede
            // al suo aggiornamento
            String updateMERICPROD = "update mericprod set quanti = ?, preoff = ?, perciva = ? where idprod = ? and idricart = ?";
            this.sqlManager.update(updateMERICPROD, new Object[] { mericprod_quanti, mericprod_preoff, meiscrizprod_perciva,
                meiscrizprod_id, mericart_id });
          } else {
            // Il prodotto non e' ancora presente nella ricerca di mercato: si
            // procede al suo inserimento
            Long mericprod_id = new Long(genChiaviManager.getNextId("MERICPROD"));
            String insertMERICPROD = "insert into mericprod (id, idricart, idprod, codimp, quanti, preoff, perciva) values (?,?,?,?,?,?,?)";
            this.sqlManager.update(insertMERICPROD, new Object[] { mericprod_id, mericart_id, meiscrizprod_id, meiscrizprod_codimp,
                mericprod_quanti, mericprod_preoff, meiscrizprod_perciva });

          }
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore durante l'allineamento del prodotto", null, e);
    }
  }


  /**
   * Viene effettuato effettuato uun confronto fra il documento contenuto nel file XML proveniente dal portale
   * e il contenuto del back office, per capire se il file va inserito, oppure nel caso sia già presente se è
   * stato modificato e si deve aggiornare
   *
   * @param idProdotto
   * @param documento
   * @param tipo
   * @return String   INS - inserimento
   *                  AGG - aggiornamento
   *                  NIENTE - nessuna operazione
   *
   * @throws GestoreException
   *
   */
  private String valutaAzioneDocumento(Long idProdotto, DocumentoType documento, Long tipo) throws GestoreException{
    String risposta="";
    String nomeFileXml = documento.getNomeFile();
    try {
      List<?> listaNomiFile = this.sqlManager.getListVector("select dignomdoc, a.idprg, a.iddocdig from w_docdig a,mealliscrizprod b where a.idprg=b.idprg " +
          "and a.iddocdig = b. iddocdig and b.tipo=? and b.idiscrizprod=?", new Object[]{tipo, idProdotto});
      if (listaNomiFile == null || (listaNomiFile != null && listaNomiFile.size()==0)) {
        //Valutazione se il documento proveniente da portale è nuovo
        risposta="INS";
      }else if(listaNomiFile != null && listaNomiFile.size()>0){
        byte[] documentoAllegatoXml = documento.getFile();
        for (int i = 0; i < listaNomiFile.size(); i++) {
          String nomeFileBackOffice = (String) SqlManager.getValueFromVectorParam(listaNomiFile.get(i), 0).getValue();
          if(nomeFileXml.equals(nomeFileBackOffice)){
            risposta = "NIENTE";
            //Valutazione se il documento è stato modificato, si deve confrontare il contenuto del documento presente nel file xml e il corrispondente valore in db
            String idprg = (String) SqlManager.getValueFromVectorParam(listaNomiFile.get(i), 1).getValue();
            Long iddocdig = (Long) SqlManager.getValueFromVectorParam(listaNomiFile.get(i), 2).getValue();
            BlobFile documentoAllegatoBackOffice = null;
            try {
              documentoAllegatoBackOffice = fileAllegatoManager.getFileAllegato(idprg, iddocdig);
            } catch (Exception e) {
              logger.error("Errore nella lettura del file allegato con idprg=" + idprg + " iddocdig=" + iddocdig.toString() +
                  " e presente nella tabella W_DOCDIG relativo al prodotto=" + idProdotto.toString(),e);
              throw new GestoreException("Errore nella lettura del file allegato con idprg=" + idprg + " iddocdig=" + iddocdig.toString() +
                  " e presente nella tabella W_DOCDIG relativo al prodotto=" + idProdotto.toString(), null, e);
            }
            byte[] contenutoDocumentoAllegatoBackOffice = null;
            ByteArrayInputStream inputStreamBackOffice = null;
            ByteArrayInputStream inputStreamXml = null;
            if (documentoAllegatoBackOffice != null && documentoAllegatoBackOffice.getStream() != null) {
              contenutoDocumentoAllegatoBackOffice = documentoAllegatoBackOffice.getStream();
              inputStreamBackOffice = new ByteArrayInputStream(contenutoDocumentoAllegatoBackOffice);
            }
            inputStreamXml = new ByteArrayInputStream(documentoAllegatoXml);
            try {
              if(!IOUtils.contentEquals(inputStreamBackOffice, inputStreamXml)){
                //Il contenuto dei file è variato, si deve aggiornare
                risposta = "AGG";
                break;
              }
            } catch (IOException e) {
              logger.error("Errore durante la valutazione della variazione del contenuto del file " + nomeFileBackOffice +
                  " relativo al prodotto=" + idProdotto.toString(),e);
              throw new GestoreException("Errore durante la valutazione della variazione del contenuto del file " + nomeFileBackOffice +
                  " relativo al prodotto=" + idProdotto.toString(), null, e);
            }

            break;
          }
        }
        if("".equals(risposta)){
          //Il file contenuto nel file xml non è presente nel back office, quindi si deve inserire
          risposta="INS";
        }
      }

    } catch (SQLException e) {
      logger.error("Errore durante la lettura del file " + nomeFileXml + " di tipo:"+ tipo.toString() +" del prodotto:" + idProdotto.toString(), e);
      throw new GestoreException("Errore durante la lettura del file " + nomeFileXml + " di tipo:"+ tipo.toString() +" del prodotto:" + idProdotto.toString(), null, e);
    }
    return risposta;
  }


  /**
   * Viene controllato se vi sono dei documenti del backoffice che non sono presenti fra quelli contenuti nel file xml
   * prodotto dal Portale, ed eventualmente li elimina da MEALLISCRIZPROD
   *
   * @param idProdotto
   * @param listaDocumenti
   * @param tipo
   *
   * @throws GestoreException
   *
   */
  private void eliminazioneDocumenti(Long idProdotto, Object listaDocumenti, Long tipo) throws GestoreException{
    try {
      List<?> listaNomiFile = this.sqlManager.getListVector("select dignomdoc, b.id from w_docdig a,mealliscrizprod b where a.idprg=b.idprg " +
          "and a.iddocdig = b. iddocdig and b.tipo=? and b.idiscrizprod=?", new Object[]{tipo, idProdotto});
      if(listaNomiFile != null && listaNomiFile.size()>0){
        for (int i = 0; i < listaNomiFile.size(); i++) {
          String nomeDocumentoBackOffice = (String) SqlManager.getValueFromVectorParam(listaNomiFile.get(i), 0).getValue();
          Long idMealliscrizprod = (Long) SqlManager.getValueFromVectorParam(listaNomiFile.get(i), 1).getValue();
          if(tipo.longValue()==1){
            String nomeDocumentoXml = ((DocumentoType) listaDocumenti).getNomeFile();
            if(!nomeDocumentoBackOffice.equals(nomeDocumentoXml)){
              this.sqlManager.update("delete from mealliscrizprod where id=?", new Object[]{idMealliscrizprod});
            }
          }else{
            ListaDocumentiType listaDoc = (ListaDocumentiType) listaDocumenti;
            if (listaDoc.sizeOfDocumentoArray() > 0) {
              boolean documentoTrovato=false;
              for (int j = 0; j < listaDoc.sizeOfDocumentoArray(); j++){
                String nomeDocumentoXml = listaDoc.getDocumentoArray(j).getNomeFile();
                if(nomeDocumentoBackOffice.equals(nomeDocumentoXml)){
                  documentoTrovato=true;
                  break;
                }
              }
              if(!documentoTrovato){
                this.sqlManager.update("delete from mealliscrizprod where id=?", new Object[]{idMealliscrizprod});
              }
            }
          }
        }
      }
    } catch (SQLException e) {
      logger.error("Errore durante il processo di cancellazione dei documenti di tipo:" + tipo.toString() + " del prodotto:" + idProdotto.toString(), e);
      throw new GestoreException("Errore durante il processo di cancellazione dei documenti di tipo:" + tipo.toString() + " del prodotto:" + idProdotto.toString(), null, e);
    }
  }

  /**
   * Inserimento documenti in MEALLPROD
   *
   * @param idProdotto
   * @param idProdottoStoricizzato
   * @param tipo
   *
   * @throws GestoreException
   *
   */
  private void archiviazioneDocumenti(Long idProdotto,Long idProdottoStoricizzato,Long tipo) throws GestoreException{
    String select="select idprg, iddocdig from mealliscrizprod where idiscrizprod=? and tipo=?";
    String insertMeallprod="insert into meallprod(id,idprod,idprg,iddocdig,tipo) values(?,?,?,?,?)";
    try {
      List<?> listaDatiDocumenti = this.sqlManager.getListVector(select,new Object[]{idProdotto, tipo});
      if(listaDatiDocumenti!=null & listaDatiDocumenti.size()>0){
        for (int i = 0; i < listaDatiDocumenti.size(); i++) {
          String idprg = (String) SqlManager.getValueFromVectorParam(listaDatiDocumenti.get(i), 0).getValue();
          Long iddocdig = (Long) SqlManager.getValueFromVectorParam(listaDatiDocumenti.get(i), 1).getValue();
          Long idMEALLPROD = new Long(genChiaviManager.getNextId("MEALLPROD"));
          this.sqlManager.update(insertMeallprod, new Object[]{idMEALLPROD,idProdottoStoricizzato,idprg,iddocdig,tipo});

        }
      }
    } catch (SQLException e) {
      logger.error("Errore durante la storicizzazione in MEALLPROD del documento di tipo " + tipo.toString()+ " del prodotto:" + idProdotto.toString(), e);
      throw new GestoreException("Errore durante la storicizzazione in MEALLPROD del documento di tipo " + tipo.toString()+ " del prodotto:" + idProdotto.toString(), null, e);
    }
  }


  /**
   * Viene eliminata una occorrenza da MEALLISCRIZPROD
   *
   * @param nomeFile
   * @param idProdotto
   * @param tipo
   *
   * @throws GestoreException
   *
   */
  private void cancellazioneDocumento(String nomeFile, Long idProdotto, Long tipo)throws GestoreException{
    String delete="delete from mealliscrizprod a where idiscrizprod=? and tipo=? and idprg=? and iddocdig in " +
    		"(select iddocdig from w_docdig b where  digent=? and idprg=? and a.iddocdig = b.iddocdig and dignomdoc=?)";
    try {
      this.sqlManager.update(delete, new Object[]{idProdotto, tipo, "PG", "MEALLISCRIZPROD", "PG", nomeFile});
    } catch (SQLException e) {
      logger.error("Errore durante la cancellazione in MEALLISCRIZPROD del documento:" + nomeFile + " di tipo: " + tipo.toString() + " del prodotto:" + idProdotto.toString(), e);
      throw new GestoreException("Errore durante la cancellazione in MEALLISCRIZPROD del documento:" + nomeFile + " di tipo: " + tipo.toString() + " del prodotto:" + idProdotto.toString(), null, e);
    }
  }



  /**
   * Gestione delle documentazione per le procedure telematiche.
   * E' stata introdotta la gestione delle comunicazioni con stato=13, che indica
   * la situazione di buste già acquisite correttemante in precedenza e che devono
   * essere processate ripetendo solo i controlli iniziali e impostando lo stato a 6.
   *
   * @param ngara
   * @param comtipo
   * @param dittao
   * @param fasgarSuccessivo
   * @param stepgarSuccessivo
   * @param password
   * @param request
   * @return HashMap<String, Object>
   * @throws GestoreException
   * @throws GeneralSecurityException
   * @throws ParseException
   * @throws JspException
   * @throws NumberFormatException
   */
  public HashMap<String, Object> aperturaDocumentazioneProcedureTelematiche(String ngara, String comtipo, String dittao, Long fasgarSuccessivo, Long stepgarSuccessivo,
      String password, HttpServletRequest request) throws GestoreException, GeneralSecurityException, ParseException, NumberFormatException, JspException {

    HashMap<String, Object> hMap = new HashMap<String, Object>();

    TransactionStatus status = null;
    boolean commitTransaction = false;

    int numeroAcquisizioni = 0;
    int numeroAcquisizioniErrore = 0;
    int numeroAcquisizioniScartate = 0;

    String nomeCampo = null;
    String codEvento = null;
    String descEvento = null;

    String comtipoChiamataFunz = comtipo;
    String sez=null;
    if ("FS11B-QL".equals(comtipo)) {
      comtipo = "FS11B";
      sez = "1";
    } else if ("FS11B-QN".equals(comtipo)) {
      comtipo = "FS11B";
      sez = "2";
    }

    String anonima = request.getParameter("anonima");
    boolean decodificaIdanonimo=false;
    try {

      String codgar1 = (String) this.sqlManager.getObject("select codgar1 from gare where ngara = ?", new Object[] {ngara});
      Long bustalotti = (Long)this.sqlManager.getObject("select bustalotti from gare where ngara=?", new Object[]{codgar1});

      String isconcprog=null;
      if("FS11B".equals(comtipo) && !"1".equals(anonima) && sez==null) {
        isconcprog = (String) this.sqlManager.getObject("select isconcprog from torn where codgar = ?", new Object[] {codgar1});
        if("1".equals(isconcprog))
          decodificaIdanonimo = true;
      }

      List<?> datiW_INVCOM  = null;
      boolean gestioneSingolaDitta= false;
      String selectComScartate=selectW_INVCOM_SCARTATE;
      Object param[] = null;
      if(dittao!=null && !"".equals(dittao)){
        gestioneSingolaDitta=true;
        String ncomope = (String) this.sqlManager.getObject("select ncomope from ditg where ngara5=? and dittao=?", new Object[]{ngara,dittao});
        String select = selectW_INVCOM_FILTRO_COMKEY3;
        if ("2".equals(sez))
          select = selectW_INVCOM_FILTRO_COMKEY3_STEP2;
        selectComScartate=selectW_INVCOM_SCARTATE_FILTRO_COMKEY3;
        param = new Object[] {ngara, comtipo, ncomope};
        datiW_INVCOM = this.sqlManager.getListVector(select, param);
      } else {
        String select = selectW_INVCOM;
        if ("2".equals(sez))
          select = selectW_INVCOM_STEP2;
        param = new Object[] {ngara, comtipo};
        datiW_INVCOM = this.sqlManager.getListVector(select, param);
      }

      if("FS11B".equals(comtipo) && "1".equals(anonima)) {
        //Le comunicazioni vengono scartate con operazioni eseguite precedentemente, quindi per sapere
        //il numero di comunicazione scartate(in stato 8 e 20), devo leggere direttamente dal db
        Long conteggioComunicazioniScartate = (Long)this.sqlManager.getObject(selectComScartate, param);
        if(conteggioComunicazioniScartate!=null)
          numeroAcquisizioniScartate = conteggioComunicazioniScartate.intValue();
      }

      if (datiW_INVCOM != null && datiW_INVCOM.size() > 0) {

        Cipher decoder = null;
        String comstato= null;
        String garaChiavibuste = ngara;
        if(bustalotti!= null && bustalotti.longValue()==1)
          garaChiavibuste = codgar1;

        String pin = password + garaChiavibuste;

        int esitoAquisizione=0;


        String userkey1 = null;
        String w_invcom_comkey1 = null;
        Long w_invcom_idcom = null;
        String comkey3=null;
        for (int i = 0; i < datiW_INVCOM.size(); i++) {

          if("FS10A".equals(comtipo)){
            nomeCampo="DATFS10A";
            codEvento="GA_APERTURA_BUSTA_PREQ";
            descEvento = "Apertura busta prequalifica";
          }else if("FS11A".equals(comtipo)){
            nomeCampo="DATFS11A";
            codEvento="GA_APERTURA_BUSTA_AMM";
            descEvento = "Apertura busta amministrativa";
          }else if("FS11B".equals(comtipo)){

            if ("1".equals(sez)) {
              nomeCampo="DATFS11B1";
              codEvento="GA_APERTURA_BUSTA_TQUALI";
              descEvento = "Apertura busta tecnica sezione qualitativa";
            } else if ("2".equals(sez)) {
              nomeCampo="DATFS11B";
              codEvento="GA_APERTURA_BUSTA_TQUANTI";
              descEvento = "Apertura busta tecnica sezione quantitativa";
            } else {
              nomeCampo="DATFS11B";
              if("1".equals(anonima)) {
                codEvento="GA_APERTURA_BUSTA_TEC_ANO";
                descEvento = "Apertura busta tecnica anonima";
              }else {
                codEvento="GA_APERTURA_BUSTA_TEC";
                descEvento = "Apertura busta tecnica";
              }

           }
          }else if("FS11C".equals(comtipo)){
            nomeCampo="DATFS11C";
            codEvento="GA_APERTURA_BUSTA_ECO";
            descEvento = "Apertura busta economica";
          }

          try {
            commitTransaction = false;
            status = this.sqlManager.startTransaction();

            boolean acquisito = false;
            boolean esisteDITG = false;
            boolean saltareAcquisizione = false;
            String idanonimo = null;

            String w_invcom_idprg = (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 0).getValue();
            w_invcom_idcom = (Long) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 1).getValue();
            w_invcom_comkey1 = (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 2).getValue();
            String w_invcom_comkeysess = (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 3).getValue();
            comstato = (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 4).getValue();
            comkey3  = (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 5).getValue();

            userkey1 = (String) this.sqlManager.getObject(selectW_PUSER, new Object[] { w_invcom_comkey1 });
            decoder = cifraturaBusteManager.getDecifratoreBuste(pin, garaChiavibuste, comtipo, w_invcom_comkeysess, w_invcom_comkey1);


            //Nel caso di bustalotti=1 si devono prendere i dati dall'occorrenza complementare di gare
            Object par[] = new Object[4];
            par[0] = codgar1;
            if((new Long(1)).equals(bustalotti))
              par[1] = codgar1;
            else
              par[1] = ngara;
            par[2] = userkey1;
            par[3] = comkey3;

            if (userkey1 != null) {
              Long conteggio = null;
              if(!"FS10A".equals(comtipo))
                conteggio = (Long) this.sqlManager.getObject(selectDITG,par);
              else
                conteggio = (Long) this.sqlManager.getObject(selectDITG_FS10A,par);

              if (conteggio != null && conteggio.longValue() > 0)
                esisteDITG = true;
              else{
                //Se l'impresa partecipa come mandataria di un RT allora si deve estrarre il codice della RTI
                //Se l'impresa è presente come mandataria in più RT allora è un errore
                List<?> listaCodiciRaggruppamento= this.sqlManager.getListVector(selectRaggruppamentoDITG,par);
                if(listaCodiciRaggruppamento!=null && listaCodiciRaggruppamento.size()==1){
                  userkey1 = (String) SqlManager.getValueFromVectorParam(listaCodiciRaggruppamento.get(0), 0).getValue();
                  esisteDITG = true;
                }
              }
            }

            if (esisteDITG) {
              String ammgar = null;
              Long fasgar = null;
              Timestamp data =null;
              String ora = null;

              String selectDatiDITG = selectDatiDitg;
              if("FS10A".equals(comtipo))
                selectDatiDITG = selectDatiDitg_FS10A;
              else if("FS11B".equals(comtipo) || "FS11C".equals(comtipo)){
                Long genere = (Long)this.sqlManager.getObject("select genere from v_gare_genere where codgar=? and codice=codgar", new Object[]{codgar1});
                if((new Long(3)).equals(genere) && (new Long(1)).equals(bustalotti))
                  selectDatiDITG=selectDatiDitgOffUnica;
              }

              Vector<?> datiDitg = this.sqlManager.getVector(selectDatiDITG, new Object[]{codgar1, ngara, userkey1});
              if(datiDitg!=null && datiDitg.size()>0){
                //ammgar = (String)this.sqlManager.getObject(selectAmmgar, new Object[] { codgar1, ngara, userkey1 });
                ammgar = SqlManager.getValueFromVectorParam(datiDitg, 0).getStringValue();
                fasgar = SqlManager.getValueFromVectorParam(datiDitg, 1).longValue();
                data = SqlManager.getValueFromVectorParam(datiDitg, 2).dataValue();
                ora = SqlManager.getValueFromVectorParam(datiDitg, 3).stringValue();
                if(decodificaIdanonimo && datiDitg.size()>4)
                  idanonimo=SqlManager.getValueFromVectorParam(datiDitg, 4).stringValue();
              }
              int faseConfronto = 2;
              if("FS11B".equals(comtipo))
                faseConfronto = 5;
              else if("FS11C".equals(comtipo))
                faseConfronto = 6;
              else if("FS10A".equals(comtipo))
                faseConfronto = -4;
              if("2".equals(ammgar) && fasgar!=null && fasgar.longValue() < faseConfronto){
                //Si deve verificare se la ditta è la mandataria di un raggruppamento creato per la presentazione dell'offerta,
                //se lo è allora si deve prendere in considerazione il raggruppamento

                String codiceRT = (String)this.sqlManager.getObject(selectCodiceRT, par);
                if(codiceRT!=null && !"".equals(codiceRT)){
                  datiDitg = this.sqlManager.getVector(selectDatiDITG, new Object[]{codgar1, ngara, codiceRT});
                  if(datiDitg!=null && datiDitg.size()>0){
                    ammgar = SqlManager.getValueFromVectorParam(datiDitg, 0).getStringValue();
                    fasgar = SqlManager.getValueFromVectorParam(datiDitg, 1).longValue();
                    data = SqlManager.getValueFromVectorParam(datiDitg, 2).dataValue();
                    ora = SqlManager.getValueFromVectorParam(datiDitg, 3).stringValue();
                    if(decodificaIdanonimo && datiDitg.size()>4)
                      idanonimo=SqlManager.getValueFromVectorParam(datiDitg, 4).stringValue();
                  }
                  if("2".equals(ammgar) && fasgar!=null && fasgar.longValue() < faseConfronto)
                    saltareAcquisizione =true;
                  else
                    userkey1 = codiceRT;
                } else
                  saltareAcquisizione =true;
              }

              if(!saltareAcquisizione && (!gestioneSingolaDitta || (gestioneSingolaDitta && dittao.equals(userkey1))) && ("5".equals(comstato) || "16".equals(comstato))){
                // Lettura delle informazioni da W_DOCDIG
                List<?> datiW_DOCDIG = this.sqlManager.getVector(selectW_DOCDIG, new Object[] { "W_INVCOM", w_invcom_idprg, w_invcom_idcom.toString() });
                if (datiW_DOCDIG != null && datiW_DOCDIG.size() > 0) {
                  String w_docdig_idprg = (String) SqlManager.getValueFromVectorParam(datiW_DOCDIG, 0).getValue();
                  Long w_docdig_iddocdig = (Long) SqlManager.getValueFromVectorParam(datiW_DOCDIG, 1).getValue();

                  BlobFile w_docdig_digogg = null;
                  w_docdig_digogg = this.fileAllegatoManager.getFileAllegato(w_docdig_idprg, w_docdig_iddocdig);
                  if (w_docdig_digogg != null && w_docdig_digogg.getStream() != null) {
                    Long offtel = (Long)this.sqlManager.getObject(selectOfftel, new Object[] { codgar1 });
                    ListaDocumentiType listaDocumenti =null;
                    OffertaEconomicaType offertaEconomica=null;
                    ListaCriteriValutazioneType listaCriteriValutazioneTec = null;
                    String xml = new String(w_docdig_digogg.getStream());
                    if(new Long(1).equals(offtel) && "FS11C".equals(comtipo)){
                      listaDocumenti =  BustaEconomicaDocument.Factory.parse(xml).getBustaEconomica().getDocumenti();
                      offertaEconomica = BustaEconomicaDocument.Factory.parse(xml).getBustaEconomica().getOfferta();
                    }else{
                      if(new Long(1).equals(offtel) && controlliOepvManager.checkQualuqueFormatoDefinito(ngara, new Long(1))&& "FS11B".equals(comtipo)){
                        //BustaTecnicaType busta =  BustaTecnicaType.Factory.parse(xml);
                        BustaTecnicaType busta = BustaTecnicaDocument.Factory.parse(xml).getBustaTecnica();
                        listaDocumenti =  busta.getDocumenti();
                        OffertaTecnicaType offTec = busta.getOfferta();
                        listaCriteriValutazioneTec =  offTec.getListaCriteriValutazione();
                      }else{
                        listaDocumenti =  DocumentazioneBustaDocument.Factory.parse(xml).getDocumentazioneBusta().getDocumenti();
                      }
                    }
                    if (data != null) {
                      this.inserimentoDocumentidaXML(listaDocumenti, codgar1, userkey1, ngara, new Date(data.getTime()), ora, comtipoChiamataFunz, decoder, w_invcom_idcom.toString());
                      //Gestione offerta economica
                      if(new Long(1).equals(offtel) && "FS11C".equals(comtipo)){
                        acquisito = this.inserimentoOffertadaXML(ngara,codgar1,userkey1,offertaEconomica,decoder);
                      }else if(new Long(3).equals(offtel) && "FS11C".equals(comtipo)){
                        acquisito = this.inserimentoOffertadaJson(listaDocumenti,ngara,codgar1,userkey1,w_invcom_idcom.toString(),decoder);
                      }else{
                        if(new Long(1).equals(offtel) && controlliOepvManager.checkQualuqueFormatoDefinito(ngara, new Long(1)) && "FS11B".equals(comtipo)){
                          acquisito = inserimentoOfferteTecnicheDaXML(ngara, codgar1, userkey1, listaCriteriValutazioneTec, decoder, 1, sez);
                        }else{
                          acquisito = true;
                        }
                      }
                    }
                  }
                }
              }
            }


            esitoAquisizione=0;

            if ("5".equals(comstato) || "16".equals(comstato)) {
              if (acquisito) {
                //Aggiornamento di DITGEVENTI
                this.aggiornaDitgEventi(ngara, codgar1, userkey1, nomeCampo);
                String stato = "6";
                if ("1".equals(sez)) {
                  stato = "16";
                }else if("FS11B".equals(comtipo) && "1".equals(anonima))
                  stato = "13";
                aggiornaStatoW_INVOCM(w_invcom_idcom, stato);
                numeroAcquisizioni++;
                commitTransaction = true;
                esitoAquisizione=1;
              }else if(saltareAcquisizione){
                ///Da questa porzione di codice non si passa più, poiché le comunicazioni vengono scartate in fasi precedenti(es. apertura domande di partecipazione)!!!!
                aggiornaStatoW_INVOCM(w_invcom_idcom,"8");
                numeroAcquisizioniScartate++;
                commitTransaction = true;
              }else {
                //Si deve aggiornare lo stato della comunicazione solo nel caso di processamento di tutte le ditte o
                //nel caso di ditta singola, quando si sta elaborando la comunicazione della ditta in oggetto
                if((!gestioneSingolaDitta || (gestioneSingolaDitta && dittao.equals(userkey1)))){
                  String stato = "7";
                  if ("1".equals(sez)) {
                    stato = "17";
                  }
                  aggiornaStatoW_INVOCM(w_invcom_idcom, stato);
                  numeroAcquisizioniErrore++;
                  commitTransaction = true;
                  esitoAquisizione = 3;
                }
              }
            }else if("13".equals(comstato)){
              //Le comunicazioni in tale stato è sicuro che rispettano le condizioni per essere acquisite, quindi si deve sicuramente aggiornare lo stato
              //si deve tenere solo conto del fatto che si sta facendo acquisiszione singola o in blocco
              if((!gestioneSingolaDitta || (gestioneSingolaDitta && dittao.equals(userkey1)))){
                //Aggiornamento DITGEVENTI
                this.aggiornaDitgEventi(ngara, codgar1, userkey1, nomeCampo);
                //Nel caso di stato=13 si deve solamente aggiornare lo stato
                aggiornaStatoW_INVOCM(w_invcom_idcom,"6");
                if(decodificaIdanonimo && idanonimo!= null) {
                  idanonimo = ScaricaDocumentiManager.decifraIdAnonimo(idanonimo);
                  this.sqlManager.update("update ditg set idanonimo=? where ngara5=? and dittao=? and codgar5=?", new Object[] {idanonimo, ngara,userkey1,codgar1});
                }
                numeroAcquisizioni++;
                commitTransaction = true;
                esitoAquisizione=1;
              }
            }


            //Aggiornamento della fase della gara per bustalotti=1 perchè con l'apertura della fase delle offerte economiche viene impostata solo la
            //fase dei lotti e non quella della gara
            if("FS11C".equals(comtipo) && (new Long(1)).equals(bustalotti)){
              this.sqlManager.update("update GARE set FASGAR = ?, STEPGAR = ? where NGARA = ?", new Object[]{new Long(6), new Long(60),codgar1});
            }

            //Tracciatura acquisizione singolo messaggio
            if(esitoAquisizione==1 || esitoAquisizione==3){
              descEvento += " ditta " + userkey1 + " (cod.operatore " + w_invcom_comkey1 + ", id.comunicazione " + w_invcom_idcom + ") ";
              LogEvento logEvento = LogEventiUtils.createLogEvento(request);
              logEvento.setLivEvento(esitoAquisizione);
              logEvento.setOggEvento(ngara);
              logEvento.setCodEvento(codEvento);
              logEvento.setDescr(descEvento );
              logEvento.setErrmsg("");
              LogEventiUtils.insertLogEventi(logEvento);
            }
          }catch(Exception e){
            //L'eccezione non deve essere riemessa, in quanto si ha un ciclo che deve proseguire
            //Si deve tracciare l'errore
            //Tranne per il caso di password sbagliata, visto che la password è sempre la stessa per tutte le buste, quindi va bloccata
            //l'esecuzione riemettendo l'eccezione
            descEvento += " ditta " + userkey1 + " (cod.operatore " + w_invcom_comkey1 + ", id.comunicazione " + w_invcom_idcom + ") ";
            LogEvento logEvento = LogEventiUtils.createLogEvento(request);
            logEvento.setLivEvento(3);
            logEvento.setOggEvento(ngara);
            logEvento.setCodEvento(codEvento);
            logEvento.setDescr(descEvento );
            logEvento.setErrmsg(e.getMessage());
            LogEventiUtils.insertLogEventi(logEvento);
            //Si incrementa il numero di acquisizioni con errore
            numeroAcquisizioniErrore++;
            if(e.getMessage().indexOf("Password inserita non corretta")>=0)
              throw new GestoreException(e.getMessage(), null, e);
          }finally {
            if (status != null) {
              if (commitTransaction) {
                try {
                  this.sqlManager.commitTransaction(status);
                } catch (Exception e) {

                }
              } else {
                try {
                  this.sqlManager.rollbackTransaction(status);
                  aggiornaStatoW_INVOCM(w_invcom_idcom,"7");
                  this.sqlManager.commitTransaction(status);
                } catch (Exception e) {

                }
              }
            }
          }

        }
      }



    } catch (SQLException e) {
      throw new GestoreException("Errore nella gestione della documentazione per la procedura telematica", null, e);
    }

    hMap.put("numeroAcquisizioni", new Long(numeroAcquisizioni));
    hMap.put("numeroAcquisizioniErrore", new Long(numeroAcquisizioniErrore));
    hMap.put("numeroAcquisizioniScartate", new Long(numeroAcquisizioniScartate));

    return hMap;

  }


  /**
   * Inserimento dei documenti provenienti da XML del portale Appalti.
   *
   * @param listaDocumenti
   * @param codgar
   * @param codimp
   * @param ngara
   * @param data
   * @param ora
   * @param bustaFS
   * @throws GestoreException
   */
  private void inserimentoDocumentidaXML(ListaDocumentiType listaDocumenti, String codgar, String codimp, String ngara, Date data, String ora,
      String bustaFS, Cipher decoder,String idCom) throws GestoreException {

    if (listaDocumenti != null) {
      String sezTec = null;
      if ("FS11B-QL".equals(bustaFS)) {
        bustaFS = "FS11B";
        sezTec = "1";
      } else if ("FS11B-QN".equals(bustaFS)) {
        bustaFS = "FS11B";
        sezTec = "2";
      }
      String nomeFile = null;
      DocumentoType documento = null;
      String updateIMPRDOCG = "update IMPRDOCG set IDPRG = ?, IDDOCDG = ?, DATARILASCIO = ?, ORARILASCIO = ?, SITUAZDOCI = ?, UUID =? where CODGAR=? and CODIMP=? and NORDDOCI=? and PROVENI=?";
      String uuid=null;

      Iterator<DocumentoType> iterator = ListaDocumentiPortaleUtilities.getIteratore(listaDocumenti.getDocumentoArray());
      while(iterator.hasNext()) {
        documento = iterator.next();
        nomeFile = documento.getNomeFile();
        if(CostantiAppalti.nomeFileQestionario.equals(nomeFile))
          continue;

        uuid = documento.getUuid();
        long id = -1;
        if (documento.isSetId()) id = documento.getId();

        //Se nel file XML è presente l'id, vuol dire che l'occorrenza in
        // IMPRDOCG non va inserita, ma aggiornata. Nel caso di busta tecnica, se attiva la gestione
        //delle sezioni qualitative e quantitative, si devono elaborare solo i documenti relativi alla sezione in elaborazione
        boolean elaborare = true;
        if (("1".equals(sezTec) || "2".equals(sezTec)) && id > 0) {
          try {
            Long conteggio = (Long) this.sqlManager.getObject("select count(d.codgar) from documgara d, imprdocg i where i.codgar=? and "
              + "i.norddoci=? and i.codgar=d.codgar  and i.norddoci=d.norddocg and d.seztec = ?", new Object[] {codgar, new Long(id), new Long(sezTec)});
            if (conteggio == null || new Long(0).equals(conteggio))
              elaborare = false;
          } catch (SQLException e) {
            throw new GestoreException("Errore nella lettura della sezione tecnica del documento  della ditta " + codimp + " per la gara " + ngara + "("  + e.getMessage() + ")", null, e);
          }
        }

        //Nel caso di gestione delle sezioni tecniche i documenti inseriti da portale vanno inseriti una sola volta, dedido di inserirli nell'elaborazione delle sezioni qualitative,
        //quindi quando si processa l'acquisizione delle buste quantitative, vanno scartati i dodumenti con id<0
        if (elaborare && "2".equals(sezTec) && id <0) {
          elaborare = false;
        }

        if (elaborare) {
          String descrizione = documento.getDescrizione();
          byte[] file = this.pgManager.getFileFromDocumento(documento,idCom);
          if(file==null){
            throw new GestoreException("Errore nell'acquisizione della documentazione della ditta " + codimp + " per la gara " + ngara + ". Viene fatto riferimento a allegati non disponibili", null, new Exception());
          }

          try {
            if (decoder != null)
              file = SymmetricEncryptionUtils.translate(decoder, file);
          } catch (GeneralSecurityException e) {
            throw new GestoreException("Errore nella decriptazione del documento " + nomeFile + " della ditta " + codimp + " per la gara " + ngara + "("  + e.getMessage() + ")", null, e);
          }

          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          try {
            baos.write(file);
          } catch (IOException e) {
            throw new GestoreException("Errore nella scrittura del documento " + nomeFile + " su w_docdig della ditta " + codimp + " per la gara " + ngara + "("  + e.getMessage() + ")", null, e);
          }

          try {
            // Inserimento in W_DOCDIG
            String selectW_DOCDIG_MAX = "select max(IDDOCDIG) from W_DOCDIG where IDPRG = 'PA'";
            Long nProgressivoW_DOCDIG = (Long) sqlManager.getObject(selectW_DOCDIG_MAX, null);
            if (nProgressivoW_DOCDIG == null) nProgressivoW_DOCDIG = new Long(0);
            Long newProgressivoW_DOCDIG = new Long(nProgressivoW_DOCDIG.longValue() + 1);

            Vector<DataColumn> vectorW_DOCDIG = new Vector<DataColumn>();
            vectorW_DOCDIG.add(new DataColumn("W_DOCDIG.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, "PA")));
            vectorW_DOCDIG.add(new DataColumn("W_DOCDIG.IDDOCDIG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, newProgressivoW_DOCDIG)));
            vectorW_DOCDIG.add(new DataColumn("W_DOCDIG.DIGENT", new JdbcParametro(JdbcParametro.TIPO_TESTO, "IMPRDOCG")));
            vectorW_DOCDIG.add(new DataColumn("W_DOCDIG.DIGNOMDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeFile)));
            vectorW_DOCDIG.add(new DataColumn("W_DOCDIG.DIGOGG", new JdbcParametro(JdbcParametro.TIPO_BINARIO, baos)));
            if(StringUtils.isNotBlank(documento.getFirmacheck())){
            	logger.info("firmacheck impostato nella busta.xml");
            	vectorW_DOCDIG.add(new DataColumn("W_DOCDIG.FIRMACHECK", new JdbcParametro(JdbcParametro.TIPO_TESTO, Boolean.valueOf(documento.getFirmacheck())?"1":"2")));
            	if(documento.getFirmacheckts()!=null) {
            		vectorW_DOCDIG.add(new DataColumn("W_DOCDIG.FIRMACHECKTS", new JdbcParametro(JdbcParametro.TIPO_DATA_FULL, documento.getFirmacheckts().getTime())));
            	}

            }
            DataColumnContainer dataColumnContainerW_DOCDIG = new DataColumnContainer(vectorW_DOCDIG);
            dataColumnContainerW_DOCDIG.insert("W_DOCDIG", sqlManager);

            // Inserimento in IMPRDOCG
            Long progressivo = null;
            if (id > 0) {
              // Se nel file XML è presente l'id, vuol dire che l'occorrenza in
              // IMPRDOCG non va inserita
              progressivo = new Long(id);

              //Se bustalotti=1 si deve aggiornare solo la imprdocg del lotto
              Long bustalotti = (Long)sqlManager.getObject("select bustalotti from gare where ngara=? and codgar1=ngara", new Object[]{codgar});
              if((new Long(1)).equals(bustalotti) && (bustaFS.equals("FS11B") || bustaFS.equals("FS11C") ))
                sqlManager.update(
                    updateIMPRDOCG + " and NGARA=?",
                    new Object[] { "PA", newProgressivoW_DOCDIG, data, ora, new Long(2), uuid, codgar, codimp, progressivo, new Long(1), ngara });
              else
                sqlManager.update(updateIMPRDOCG, new Object[] { "PA", newProgressivoW_DOCDIG, data, ora, new Long(2), uuid, codgar, codimp, progressivo, new Long(1) });
            } else {
              String selectIMPRDOCG_MAX = "select max(NORDDOCI) from IMPRDOCG where CODGAR=? and CODIMP=?";
              Long nProgressivoIMPRDOCG = (Long) sqlManager.getObject(selectIMPRDOCG_MAX, new Object[] { codgar, codimp });
              if (nProgressivoIMPRDOCG == null) nProgressivoIMPRDOCG = new Long(0);

              String selectDOCUMGARA_MAX = "select max(NORDDOCG) from DOCUMGARA where CODGAR=?";
              Long nProgressivoDOCUMGARA = (Long) sqlManager.getObject(selectDOCUMGARA_MAX, new Object[] { codgar });
              if (nProgressivoDOCUMGARA == null) nProgressivoDOCUMGARA = new Long(0);

              progressivo = nProgressivoDOCUMGARA;
              if (nProgressivoIMPRDOCG.longValue() > nProgressivoDOCUMGARA.longValue()) progressivo = nProgressivoIMPRDOCG;

              progressivo = new Long(progressivo.longValue() + 1);
              Long proveni = new Long(2);

              Vector<DataColumn> vectorIMPRDOCG = new Vector<DataColumn>();
              vectorIMPRDOCG.add(new DataColumn("IMPRDOCG.CODGAR", new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar)));
              vectorIMPRDOCG.add(new DataColumn("IMPRDOCG.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
              vectorIMPRDOCG.add(new DataColumn("IMPRDOCG.CODIMP", new JdbcParametro(JdbcParametro.TIPO_TESTO, codimp)));
              vectorIMPRDOCG.add(new DataColumn("IMPRDOCG.NORDDOCI", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, progressivo)));
              vectorIMPRDOCG.add(new DataColumn("IMPRDOCG.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, "PA")));
              vectorIMPRDOCG.add(new DataColumn("IMPRDOCG.IDDOCDG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, newProgressivoW_DOCDIG)));
              vectorIMPRDOCG.add(new DataColumn("IMPRDOCG.SITUAZDOCI", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(2))));
              vectorIMPRDOCG.add(new DataColumn("IMPRDOCG.PROVENI", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, proveni)));
              vectorIMPRDOCG.add(new DataColumn("IMPRDOCG.DESCRIZIONE", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, descrizione)));
              vectorIMPRDOCG.add(new DataColumn("IMPRDOCG.DATARILASCIO", new JdbcParametro(JdbcParametro.TIPO_DATA, data)));
              vectorIMPRDOCG.add(new DataColumn("IMPRDOCG.ORARILASCIO", new JdbcParametro(JdbcParametro.TIPO_TESTO, ora)));
              vectorIMPRDOCG.add(new DataColumn("IMPRDOCG.DOCTEL", new JdbcParametro(JdbcParametro.TIPO_TESTO, "1")));
              vectorIMPRDOCG.add(new DataColumn("IMPRDOCG.UUID", new JdbcParametro(JdbcParametro.TIPO_TESTO, uuid)));

              // Busta
              Long busta = null;
              if (bustaFS != null) {
                if (bustaFS.equals("FS11A")) {
                  busta = new Long(1);
                } else if (bustaFS.equals("FS11B")) {
                  busta = new Long(2);
                } else if (bustaFS.equals("FS11C")) {
                  busta = new Long(3);
                }else if (bustaFS.equals("FS10A")) {
                  busta = new Long(4);
                }
              }
              vectorIMPRDOCG.add(new DataColumn("IMPRDOCG.BUSTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, busta)));

              DataColumnContainer dataColumnContainerIMPRDOCG = new DataColumnContainer(vectorIMPRDOCG);
              dataColumnContainerIMPRDOCG.insert("IMPRDOCG", sqlManager);

            }

            // Aggiornamento di W_DOCDIG con il riferimento a IMPRDOCG
            sqlManager.update("update W_DOCDIG set DIGKEY1 = ?, DIGKEY2 = ?, DIGKEY3 = ? where IDPRG=? and IDDOCDIG=?",
                new Object[] { codgar, progressivo.toString(), codimp, "PA", newProgressivoW_DOCDIG });
            logger.info("All OK");
          } catch (SQLException e) {
        	  logger.error("All KO",e);
            throw new GestoreException("Errore nell'acquisizione della documentazione della ditta " + codimp + " per la gara " + ngara, null, e);
          }
        }
      }
    }
  }

  /**
   *
   * @param profilo
   * @param nGara
   * @param codGar
   * @param nomeEntita
   * @param nCont
   * @param codImpr
   * @param ragioneSocialeOperatore
   * @param destinatarioMail
   * @param oggettoMail
   * @param abilitaIntestazioneVariabile
   * @param testoInHtml
   * @param testoMail
   * @param mittenteMail
   * @param idcfg
   * @param flagMailPec
   * @param nomeFile
   * @param baos
   * @param integrazioneWSDM
   * @param datiWSDM
   * @param listaDocumenti
   * @param request
   * @throws SQLException
   * @throws GestoreException
   * @throws DocumentException
   * @throws IOException
   */
	public void inviaComunicazioneOrdine(ProfiloUtente profilo, String nGara, String codGar,
					String nomeEntita, Long nCont, String codImpr,
					String ragioneSocialeOperatore, String destinatarioMail, String oggettoMail,
					String abilitaIntestazioneVariabile, String testoInHtml,
					String testoMail, String mittenteMail, String idcfg, String flagMailPec,
					String nomeFile, byte[] baos, String integrazioneWSDM,
					HashMap<String,String> datiWSDM, List listaDocumenti,HttpServletRequest request) throws SQLException, GestoreException, DocumentException, IOException {

		if (logger.isDebugEnabled()) {
			logger.debug("inviaComunicazioneOrdine: inizio metodo");
		}

		//String classificadocumento = null;
	      String tipodocumento =  null;
	      String oggettodocumento =null;
	      //String descrizionedocumento =null;
	      //String mittenteinterno = null;
	      //String codiceregistrodocumento = null;
	      String inout = null;
          //String idindice =  null;
          //String idtitolazione =  null;
          //String idunitaoperativamittente =  null;
	      String inserimentoinfascicolo =  null;
	      //String codicefascicolo =  null;
	      //String oggettofascicolo =  null;
	      String classificafascicolo =  null;
	      //String descrizionefascicolo =  null;
	      //String annofascicolo =  null;
	      //String numerofascicolo =  null;
	      String username = null;
	      String password = null;
	      String ruolo = null;
	      String nome = null;
	      String cognome = null;
	      String codiceuo = null;
	      String idutente = null;
	      String idutenteunop = null;
	      String entita = "GARE";
	      String key1 = null;
	      String mezzoinvio = null;
	      String codiceaoo = null;
	      String codiceufficio = null;
	      String struttura = null;
	      String voce = null;
	      String classificadescrizione = null;
	      String codiceaoodes = null;
	      String codiceufficiodes = null;
	      WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = null;
	      String RUP = null;
	      String nomeRup = null;
	      String acronimoRup = null;
	      String sottotipo = null;
	    boolean abilitatoInvioMailDocumentale = false;
	    String tipoWSDM = null;
	    String idconfi = null;
	    String uocompetenza = null;
	    String uocompetenzadescrizione = null;

	    if("1".equals(integrazioneWSDM)){
	      idconfi = datiWSDM.get("idconfi");
	      abilitatoInvioMailDocumentale = gestioneWSDMManager.abilitatoInvioMailDocumentale("FASCICOLOPROTOCOLLO",idconfi);
	      WSDMConfigurazioneOutType configurazione = gestioneWSDMManager.wsdmConfigurazioneLeggi("FASCICOLOPROTOCOLLO",idconfi);

          if (configurazione.isEsito())
            tipoWSDM = configurazione.getRemotewsdm();
	    }

		//		Aggiornamento di W_INVCOM
		Object[] parametri = new Object[15];
		String idprg = "PG";
		parametri[0] = idprg;		//IDPRG
		Long newIdcom = (Long) sqlManager.getObject(GET_MAX_W_INVCOM, new Object[] {idprg});
		if (newIdcom == null) {
			newIdcom = new Long(0);
		}
		newIdcom = newIdcom + 1;
		parametri[1] = newIdcom;	//IDCOM
		parametri[2] = nomeEntita;	//COMENT
		parametri[3] = nGara;	//COMKEY1
		if (nomeEntita.equals("GARECONT")) {
			parametri[4] = nCont; //COMKEY2
		} else {
			parametri[4] = null; //COMKEY2
		}
		parametri[5] = (long) profilo.getId(); //COMCODOPE
		Timestamp dataOdierna = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
		parametri[6] = dataOdierna; //COMDATINS
		parametri[7] = mittenteMail; //COMMITT
		parametri[8] = "2"; //COMSTATO
		parametri[9] = abilitaIntestazioneVariabile; //COMINTEST
		parametri[10] = oggettoMail;	//COMMSGOGG
		parametri[11] = testoMail;	//COMMSGTES
		parametri[12] = (long) 2;	//COMPUB -- riservata
		parametri[13] = testoInHtml;
		parametri[14] = idcfg;
		this.sqlManager.update(INSERT_W_INVCOM, parametri);

		HashMap<String,String> datiImpr = this.gestioneWSDMManager.getDatiImpresa(codImpr);
		String DESINTEST = null;
		String DESCODSOG = null;
		if(datiImpr!=null){
		  DESINTEST = datiImpr.get("cognomeIntestazione");
		  DESCODSOG = datiImpr.get("codice");
		}

//		Aggiornamento di W_INVCOMDES
		parametri = new Object[8];
		parametri[0] = idprg;	//IDPRG
		parametri[1] = newIdcom;	//IDCOM
		Long newIdcomdes = (Long) sqlManager.getObject(GET_MAX_W_INVCOMDES, new Object[]{idprg, newIdcom});
		if (newIdcomdes == null) {
			newIdcomdes = new Long(0);
		}
		newIdcomdes = newIdcomdes + 1;
		parametri[2] = newIdcomdes;	//IDCOMDES
		parametri[3] = "IMPR";	//DESCODENT
		//parametri[4] = codImpr;	//DESCODSOG
		parametri[4] = DESCODSOG;  //DESCODSOG
		parametri[5] = destinatarioMail;	//DESMAIL
		//parametri[6] = ragioneSocialeOperatore;	//DESINTEST
		parametri[6] = DESINTEST; //DESINTEST
		parametri[7] = (long) 2;	//COMTIPMA
		if("1".equals(flagMailPec))
		  parametri[7] = (long) 1;    //COMTIPMA
		sqlManager.update(INSERT_W_INVCOMDES, parametri);

//		Aggiornamento di W_DOCDIG
		//Si aggiorna la W_DOCDIG con il file passato come parametro al metodo
		parametri = new Object[8];
		parametri[0] = idprg;	//IDPRG
		Long newIddocdig = (Long) sqlManager.getObject(GET_MAX_W_DOCDIG, new Object[]{idprg});
		if (newIddocdig == null) {
			newIddocdig = new Long(0);
		}
		newIddocdig = newIddocdig + 1;
		String descDocumento ="Documento d'ordine";
		parametri[1] = newIddocdig;	//IDDOCDIG
		parametri[2] = "W_INVCOM";	//DIGENT
		parametri[3] = "PG"; //DIGKEY1
		parametri[4] = newIdcom;	//DIGKEY2
		parametri[5] = nomeFile;	//DIGNOMDOC
		parametri[6] = descDocumento;	//DIGDESDOC
		LobHandler lobHandler = new DefaultLobHandler(); // reusable object
		parametri[7] = new SqlLobValue(baos, lobHandler);
		sqlManager.update(INSERT_W_DOCDIG, parametri);


		int numeroAllegati = 2;
        int numAll = 0;
        Long idAllegatiNuovi[]= null;
        int numeroAllegatiRali = 1;
        //List listaDocumenti = sqlManager.getListVector(selectAllegatiOrdine, new Object[]{codGar, nGara, new Long(11),"1"});
        if(listaDocumenti!=null && listaDocumenti.size()>0){
          numAll = listaDocumenti.size();
          numeroAllegati += 2 * numAll;
          idAllegatiNuovi = new Long[numAll];
          numeroAllegatiRali+=numAll;
        }

        //Allegati relativi al file passato come parametro al metodo
        WSDMProtocolloAllegatoType[] allegati = null;
        //si si deve leggere il valore della property "wsdm.posizioneAllegatoComunicazione".
        //Se la property vale 1 allora il testo della comunicazione viene messo come primo allegato, altrimenti rimane in coda
        String posizioneAllegatoComunicazione = null;
        int indiceAllegati = 0;
        if("1".equals(integrazioneWSDM)){
          allegati = new WSDMProtocolloAllegatoType[numeroAllegati];
          posizioneAllegatoComunicazione = ConfigManager.getValore("wsdm.posizioneAllegatoComunicazione." + idconfi);

          if("1".equals(posizioneAllegatoComunicazione)){
            indiceAllegati = 1;
          }

          String tipo = "";
          int index = nomeFile.lastIndexOf('.');
          if (index > 0) {
            tipo = nomeFile.substring(index + 1);
          }

          allegati[indiceAllegati] = new WSDMProtocolloAllegatoType();
          allegati[indiceAllegati].setNome(nomeFile);
          allegati[indiceAllegati].setTitolo( descDocumento);
          allegati[indiceAllegati].setTipo(tipo);
          allegati[indiceAllegati].setContenuto(baos);
          if("TITULUS".equals(tipoWSDM))
            allegati[indiceAllegati].setIdAllegato("W_DOCDIG|PG|" + newIddocdig.toString());

          if("NUMIX".equals(tipoWSDM)) {
            allegati[indiceAllegati] = GestioneWSDMManager.popolaAllegatoInfo(nomeFile,allegati[indiceAllegati]);
            if(indiceAllegati ==0 )
              allegati[indiceAllegati].setIsSealed(new Long(1));
          }
        }
        //Aggiornamento della W_DOCDIG con gli allegati all'ordine (DOCUMGARA=11)
        //Nel caso sia presente l'integrazione WSDM si devono inviare a WSDM sia i documenti che vengono allegati alla comunicazione
        //(cioè i documenti di cui si sta eseguendo l'inserimento nella W_DOCDIG), sia i documenti stessi presi direttamente dalla W_DOCDIG
        if(numAll>0){
          for(int i=0; i<numAll; i++){
            parametri = new Object[8];
            parametri[0] = idprg;   //IDPRG
            Long newIdDocdig = (Long) sqlManager.getObject("SELECT max(iddocdig) FROM w_docdig WHERE idprg=?", new Object[]{idprg});
            if (newIdDocdig == null) {
                newIdDocdig = new Long(0);
            }
            newIdDocdig = newIdDocdig + 1;
            parametri[1] = newIdDocdig; //IDDOCDIG
            parametri[2] = "W_INVCOM";  //DIGENT
            parametri[3] = "PG"; //DIGKEY1
            parametri[4] = newIdcom;    //DIGKEY2
            parametri[5] = SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 1).stringValue();    //DIGNOMDOC
            parametri[6] = SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 0).stringValue();    //DIGDESDOC
            BlobFile fileAllegato;
            try {
              lobHandler = new DefaultLobHandler();
              fileAllegato = fileAllegatoManager.getFileAllegato(idprg,SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 3).longValue());
              parametri[7] = new SqlLobValue(fileAllegato.getStream(), lobHandler);
            } catch (IOException e) {
              throw new GestoreException("Errore nella lettura degli allegati all'ordine",null, e);
            }

            sqlManager.update(INSERT_W_DOCDIG, parametri);

            if("1".equals(integrazioneWSDM)){
              idAllegatiNuovi[i] = newIdDocdig;
              String dignomdoc = SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 1).stringValue();
              String tipo = "";
              int index = dignomdoc.lastIndexOf('.');
              if (index > 0) {
                tipo = dignomdoc.substring(index + 1);
              }
              //Documento già presente nella W_DOCDIG
              allegati[indiceAllegati + 1 + i] = new WSDMProtocolloAllegatoType();
              allegati[indiceAllegati + 1 + i].setNome(dignomdoc);
              allegati[indiceAllegati + 1 + i].setTitolo( SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 0).stringValue());
              allegati[indiceAllegati + 1 + i].setTipo(tipo);
              allegati[indiceAllegati + 1 + i].setContenuto(fileAllegato.getStream());
              if("TITULUS".equals(tipoWSDM))
                allegati[indiceAllegati + 1 + i].setIdAllegato("W_DOCDIG|PG|" + newIdDocdig.toString());
              if("NUMIX".equals(tipoWSDM)) {
                allegati[indiceAllegati + 1 + i] = GestioneWSDMManager.popolaAllegatoInfo(dignomdoc,allegati[indiceAllegati + 1 + i]);
              }
            }
          }
        }

        if(!"1".equals(integrazioneWSDM)){
          HashMap<String, Object> ret = gestioneWSDMManager.aggiungiAllegatoSintesi(nGara, null, oggettoMail, testoMail, idprg, newIdcom, entita,request);
          if(ret==null) {
            throw new GestoreException("Errore nella creazione della marca temporale dell'allegato di sintesi","marcaTemporale",null, new Exception());
          }
        }

        parametri = new Object[6];
		parametri[0] = 4;	//STATO
		parametri[1] = dataOdierna;	//DATTRA
		parametri[2] = newIddocdig;	//IDDOCDG
		parametri[3] = idprg; //IDPRG
		parametri[4] = nGara;	//NGARA
		parametri[5] = nCont;	//NCONT
		sqlManager.update(UPDATE_GARECONT_FOR_ODA, parametri);

		//Integrazione WSDM
		if("1".equals(integrazioneWSDM)){

		  // Aggiunta del testo della comunicazione
          if (testoMail == null){
            testoMail = "[testo vuoto]";
          }

          //gestione allegato sintesi
          byte[] contenutoPdf = null;
          Long idAllegatoSintesi = gestioneWSDMManager.cancellaAllegatoSintesi(idprg,newIdcom);
          String nomeFileSintesi=null;
          String estensioneFile = "pdf";
          String titoloFile = null;
          if(idAllegatoSintesi==null) {
            HashMap<String, Object> ret = gestioneWSDMManager.aggiungiAllegatoSintesi(nGara, null, oggettoMail, testoMail, idprg, newIdcom, entita, request);
            if(ret==null) {
              String messaggio = "Errore nella creazione del file di sintesi della comunicazione";
              throw new GestoreException("Errore nella protocollazione del fascicolo","wsdm.fascicoloprotocollo.protocollazione.error",new Object[]{messaggio}, new Exception());
            }else {
              idAllegatoSintesi = (Long)ret.get("idAllegatoSintesi");
              nomeFileSintesi = (String)ret.get("nomeFile");
              estensioneFile = (String)ret.get("estensioneFile");
              titoloFile = (String)ret.get("titoloFile");
              contenutoPdf = (byte[]) ret.get("pdf");
            }
          }else {
            Vector<?> datiAllegato = this.sqlManager.getVector("select dignomdoc, digdesdoc from  w_docdig where idprg=? and iddocdig=?", new Object[] {idprg,idAllegatoSintesi});
            if(datiAllegato!=null && datiAllegato.size()>0) {
              nomeFileSintesi = SqlManager.getValueFromVectorParam(datiAllegato, 0).getStringValue();
              titoloFile = SqlManager.getValueFromVectorParam(datiAllegato, 1).getStringValue();
              if(nomeFile.endsWith(".tsd"))
              estensioneFile = "tsd";
            }
            BlobFile digogg = fileAllegatoManager.getFileAllegato(idprg, idAllegatoSintesi);
            contenutoPdf = digogg.getStream();
          }

          int indiceAllegatoTesto = numAll + 1;
          if ("1".equals(posizioneAllegatoComunicazione))
            indiceAllegatoTesto = 0;

          if ("1".equals(testoInHtml)) {
            testoMail = "<!DOCTYPE html><html><body>" + testoMail + "</body></html>";
            allegati[indiceAllegatoTesto] = new WSDMProtocolloAllegatoType();
            allegati[indiceAllegatoTesto].setNome("Comunicazione.html");
            allegati[indiceAllegatoTesto].setTipo("html");
            allegati[indiceAllegatoTesto].setTitolo("Testo della comunicazione");
            allegati[indiceAllegatoTesto].setContenuto(testoMail.getBytes());
          } else {
            allegati[indiceAllegatoTesto] = new WSDMProtocolloAllegatoType();
            allegati[indiceAllegatoTesto].setNome(nomeFileSintesi);
            allegati[indiceAllegatoTesto].setTipo(estensioneFile);
            allegati[indiceAllegatoTesto].setTitolo(titoloFile);
            allegati[indiceAllegatoTesto].setContenuto(contenutoPdf);
          }
          if("TITULUS".equals(tipoWSDM))
            allegati[indiceAllegatoTesto].setIdAllegato("W_INVCOM|" + idprg + "|" + newIdcom.toString());

          if("NUMIX".equals(tipoWSDM)) {
            if(!"1".equals(testoInHtml)) {
              allegati[indiceAllegatoTesto] = GestioneWSDMManager.popolaAllegatoInfo(nomeFileSintesi,allegati[indiceAllegatoTesto]);
            }
            if(indiceAllegatoTesto ==0 )
              allegati[indiceAllegatoTesto].setIsSealed(new Long(1));
          }

          //Popolamento contenitore dati per  WSDM
           String classificadocumento = datiWSDM.get("classificadocumento");
           tipodocumento =  datiWSDM.get("tipodocumento");
           oggettodocumento = datiWSDM.get("oggettodocumento");
           //descrizionedocumento = datiWSDM.get("descrizionedocumento");
           //mittenteinterno = datiWSDM.get("mittenteinterno");
           //codiceregistrodocumento = datiWSDM.get("codiceregistrodocumento");
           inout =  datiWSDM.get("inout");
           //idindice = datiWSDM.get("idindice");
           //idtitolazione = datiWSDM.get("idtitolazione");
           //idunitaoperativamittente = datiWSDM.get("idunitaoperativamittente");
           inserimentoinfascicolo =  datiWSDM.get("inserimentoinfascicolo");
           //codicefascicolo =  datiWSDM.get("codicefascicolo");
           //oggettofascicolo =  datiWSDM.get("oggettofascicolo");
           classificafascicolo =  datiWSDM.get("classificafascicolo");
           //descrizionefascicolo =  datiWSDM.get("descrizionefascicolo");
           //annofascicolo =  datiWSDM.get("annofascicolo");
           //numerofascicolo =  datiWSDM.get("numerofascicolo");

           username = datiWSDM.get("username");
           password = datiWSDM.get("password");
           ruolo = datiWSDM.get("ruolo");
           nome = datiWSDM.get("nome");
           cognome = datiWSDM.get("cognome");
           codiceuo = datiWSDM.get("codiceuo");
           idutente = datiWSDM.get("idutente");
           idutenteunop = datiWSDM.get("idutenteunop");
           key1 = datiWSDM.get("key1");
           mezzoinvio = datiWSDM.get("mezzoinvio");
           codiceaoo = datiWSDM.get("codiceaoo");
           codiceufficio = datiWSDM.get("codiceufficio");
           struttura = datiWSDM.get("struttura");
           classificadescrizione = datiWSDM.get("classificadescrizione");
           voce = datiWSDM.get("voce");

           if("TITULUS".equals(tipoWSDM) && !abilitatoInvioMailDocumentale)
             tipodocumento="UBUY - Avvisi e comunicazioni";
           else if("TITULUS".equals(tipoWSDM) && abilitatoInvioMailDocumentale)
             tipodocumento="UBUY - Avvisi e comunicazioni (PEC)";

           //Per TITULUS si deve sbiancare il contenuto della classifica del fascicolo
           if("TITULUS".equals(tipoWSDM)){
             classificafascicolo = null;
             codiceaoodes = datiWSDM.get("codiceaoodes");
             codiceufficiodes = datiWSDM.get("codiceufficiodes");
           }

           if("JDOC".equals(tipoWSDM)){
             RUP = datiWSDM.get("RUP");
             nomeRup = datiWSDM.get("nomeRup");
             acronimoRup = datiWSDM.get("acronimoRup");
             sottotipo = datiWSDM.get("sottotipo");
           }

           uocompetenza = datiWSDM.get(GestioneWSDMManager.LABEL_UOCOMPETENZA);
           uocompetenza = datiWSDM.get(GestioneWSDMManager.LABEL_DESCRIZIONE_UOCOMPETENZA);

           boolean inserimentoFascicoloArchiflowfa=false;
           boolean inserimentoFascicoloFolium = false;
           boolean inserimentoFascicoloPrisma = false;
           boolean inserimentoFascicoloItalprot = false;

           HashMap<String,Object> par = new HashMap<String,Object>();
           par.put("tipodocumento", tipodocumento);
           par.put("oggettodocumento", oggettodocumento);
           par.put("classificadocumento", datiWSDM.get("classificadocumento"));
           par.put("descrizionedocumento", datiWSDM.get("descrizionedocumento"));
           par.put("mittenteinterno", datiWSDM.get("mittenteinterno"));
           par.put("codiceregistrodocumento", datiWSDM.get("codiceregistrodocumento"));
           par.put("inout", inout);
           par.put("idindice", datiWSDM.get("idindice"));
           par.put("idtitolazione", datiWSDM.get("idtitolazione"));
           par.put("idunitaoperativamittente", datiWSDM.get("idunitaoperativamittente"));
           par.put("inserimentoinfascicolo", inserimentoinfascicolo);
           par.put("codicefascicolo", datiWSDM.get("codicefascicolo"));
           par.put("oggettofascicolo", datiWSDM.get("oggettofascicolo"));
           par.put("classificafascicolo", classificafascicolo);
           par.put("descrizionefascicolo", datiWSDM.get("descrizionefascicolo"));
           par.put("annofascicolo", datiWSDM.get("annofascicolo"));
           par.put("numerofascicolo", datiWSDM.get("numerofascicolo"));
           par.put("tipoWSDM", tipoWSDM);
           par.put("idprg", idprg);
           par.put("idcom", newIdcom);
           par.put("mezzo",  datiWSDM.get("mezzo"));
           par.put("societa", datiWSDM.get("societa"));
           par.put("codiceGaralotto", datiWSDM.get("codiceGaralotto"));
           par.put("cig", datiWSDM.get("cig"));
           par.put("struttura", struttura);
           par.put("supporto", datiWSDM.get("supporto"));
           par.put("numeroallegati",new Long(numAll+1));
           par.put("servizio", datiWSDM.get("servizio"));
           par.put("tipofascicolo", datiWSDM.get("tipofascicolo"));
           par.put("classificadescrizione", datiWSDM.get("classificadescrizione"));
           par.put("voce", datiWSDM.get("voce"));
           par.put("idconfi", datiWSDM.get("idconfi"));
           par.put("RUP", datiWSDM.get("RUP"));
           par.put("nomeRup", datiWSDM.get("nomeRup"));
           par.put("acronimoRup", datiWSDM.get("acronimoRup"));
           par.put("sottotipo", datiWSDM.get("sottotipo"));
           par.put(GestioneWSDMManager.LABEL_UOCOMPETENZA, uocompetenza);
           wsdmProtocolloDocumentoIn = this.gestioneWSDMManager.wsdmProtocolloDocumentoPopola(par,datiWSDM.get("idconfi"));

           if(("ARCHIFLOWFA".equals(tipoWSDM) || "FOLIUM".equals(tipoWSDM) || "PRISMA".equals(tipoWSDM) || "ITALPROT".equals(tipoWSDM))  && "SI_FASCICOLO_NUOVO".equals(inserimentoinfascicolo)){
             if("ARCHIFLOWFA".equals(tipoWSDM))
               inserimentoFascicoloArchiflowfa=true;
             if("FOLIUM".equals(tipoWSDM))
               inserimentoFascicoloFolium=true;
             if("PRISMA".equals(tipoWSDM))
               inserimentoFascicoloPrisma = true;
             if("ITALPROT".equals(tipoWSDM))
               inserimentoFascicoloItalprot = true;
           }

           //Destinatario
          if(datiImpr!=null){
             WSDMProtocolloAnagraficaType[] destinatari = new WSDMProtocolloAnagraficaType[1];
             destinatari[0] = new WSDMProtocolloAnagraficaType();
             if(!"FOLIUM".equals(tipoWSDM)){
               destinatari[0].setCodiceFiscale(datiImpr.get("codiceFiscale"));
               destinatari[0].setPartitaIVA(datiImpr.get("piva"));
             }else{
               destinatari[0].setCodiceFiscale("");
               destinatari[0].setPartitaIVA("");
             }
             destinatari[0].setIndirizzoResidenza(datiImpr.get("indirizzoResidenza"));
             destinatari[0].setComuneResidenza(datiImpr.get("comuneResidenza"));
             destinatari[0].setCodiceComuneResidenza(datiImpr.get("codiceComuneResidenza"));
             destinatari[0].setCognomeointestazione(datiImpr.get("cognomeIntestazione"));
             if(mezzoinvio!=null && !"".equals(mezzoinvio))
               destinatari[0].setMezzo(mezzoinvio);
             destinatari[0].setEmail(destinatarioMail);
             destinatari[0].setEmailAggiuntiva(datiImpr.get("emaiip"));
             destinatari[0].setProvinciaResidenza(datiImpr.get("proimp"));
             destinatari[0].setCapResidenza(datiImpr.get("capimp"));
             destinatari[0].setTipoVoceRubrica(WSDMTipoVoceRubricaType.IMPRESA);

             wsdmProtocolloDocumentoIn.setDestinatari(destinatari);
             wsdmProtocolloDocumentoIn.setDestinatarioPrincipale(datiImpr.get("cognomeIntestazione"));
           }

           wsdmProtocolloDocumentoIn.setAllegati(allegati);


           if(abilitatoInvioMailDocumentale && ("ENGINEERING".equals(tipoWSDM) || "ENGINEERINGDOC".equals(tipoWSDM) || "TITULUS".equals(tipoWSDM) || "SMAT".equals(tipoWSDM) || "URBI".equals(tipoWSDM) || "LAPISOPERA".equals(tipoWSDM))){
             WSDMInviaMailType inviaMail = new WSDMInviaMailType();
             // Testo email
             inviaMail.setTestoMail(testoMail);
             if("ENGINEERING".equals(tipoWSDM)){
               // Oggetto email
               inviaMail.setOggettoMail(oggettodocumento);
             }
             // Destinatari
             String selectW_INVCOMDESMail = "select desmail from w_invcomdes where idprg = ? and idcom = ?";
             List<?> datiW_INVCOMDESMail = this.sqlManager.getListVector(selectW_INVCOMDESMail, new Object[] { idprg, newIdcom });
             if (datiW_INVCOMDESMail != null && datiW_INVCOMDESMail.size() > 0) {
               String[] destinatariMail = new String[datiW_INVCOMDESMail.size()];
               for (int ides = 0; ides < datiW_INVCOMDESMail.size(); ides++) {
                 destinatariMail[ides] = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDESMail.get(ides), 0).getValue();
               }
               inviaMail.setDestinatariMail(destinatariMail);
             }
             wsdmProtocolloDocumentoIn.setInviaMail(inviaMail);
           }

           WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = this.gestioneWSDMManager.wsdmProtocolloInserisci(username, password,
               ruolo, nome, cognome, codiceuo, idutente, idutenteunop, codiceaoo, codiceufficio, wsdmProtocolloDocumentoIn,datiWSDM.get("idconfi"));

           if (wsdmProtocolloDocumentoRes.isEsito()) {
             String numeroDocumento = null;
             if(!"LAPISOPERA".equals(tipoWSDM))
               numeroDocumento = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroDocumento();
             Long annoProtocollo = null;
             if(!"LAPISOPERA".equals(tipoWSDM))
               annoProtocollo = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getAnnoProtocollo();
             String numeroProtocollo = null;
             if("LAPISOPERA".equals(tipoWSDM))
               numeroProtocollo = GestioneWSDMManager.PREFISSO_COD_FASCICOLO_LAPISOPERA + wsdmProtocolloDocumentoRes.getProtocolloDocumento().getGenericS11();
             else
               numeroProtocollo = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroProtocollo();
             String indirizzoMittente = datiWSDM.get("indirizzomittente");

             Timestamp dataProtocollo= this.gestioneWSDMManager.getDataProtocollo(wsdmProtocolloDocumentoRes);
             String annoProtocolloString=null;
             if(annoProtocollo==null && !"LAPISOPERA".equals(tipoWSDM)) {
               annoProtocollo= this.gestioneWSDMManager.getAnnoFromDate(dataProtocollo);
               annoProtocolloString = annoProtocollo.toString();
             }

             datiWSDM.put("numeroDocumento", numeroDocumento);
             datiWSDM.put("annoProtocollo", annoProtocolloString);
             datiWSDM.put("numeroProtocollo", numeroProtocollo);
             datiWSDM.put("oggettodocumento", oggettodocumento);
             datiWSDM.put("testoMail", testoMail);
             datiWSDM.put("indirizzomittente", indirizzoMittente);
             datiWSDM.put("formatoMail", testoInHtml);
             String statoComunicazione =  this.gestioneWSDMManager.wsdmInvioMailEAggiornamentoDb(idprg, "FASCICOLOPROTOCOLLO", newIdcom.toString(), datiWSDM,allegati,numeroAllegatiRali);

             // Salvataggio del numero protocollo nella comunicazione e nella gara, impostazione
             // dello stato a "In uscita"
             //Il campo COMMITT va aggiornato solo se indirizzoMittente è valorizzato

             Object param[]=null;
             String updateW_INVCOM = null;

             if(abilitatoInvioMailDocumentale && ("JIRIDE".equals(tipoWSDM) || "ARCHIFLOW".equals(tipoWSDM) || "ARCHIFLOWFA".equals(tipoWSDM) || "PRISMA".equals(tipoWSDM) || "JPROTOCOL".equals(tipoWSDM))){
               updateW_INVCOM = "update w_invcom set comstato = ?, comdatprot = ?, comnumprot = ?, committ = ? where idprg = ? and idcom = ?";
               param = new Object[]{statoComunicazione, dataProtocollo,
                   numeroProtocollo, indirizzoMittente, idprg,  newIdcom };
             }else{
               updateW_INVCOM = "update w_invcom set comstato = ?, comdatprot = ?, comnumprot = ? where idprg = ? and idcom = ?";
               param = new Object[]{statoComunicazione, dataProtocollo,
                   numeroProtocollo, idprg,  newIdcom };
             }
            this.sqlManager.update(updateW_INVCOM, param);

             this.sqlManager.update(UPDATE_GARECONT_FROM_WSDM, new Object[] { numeroProtocollo, nGara, nCont});

             // Salvataggio del riferimento al fascicolo
             if ("SI_FASCICOLO_NUOVO".equals(inserimentoinfascicolo)) {
               String codiceFascicoloNUOVO = null;
               Long annoFascicoloNUOVO = null;
               String numeroFascicoloNUOVO = null;
               if(!inserimentoFascicoloArchiflowfa && !inserimentoFascicoloFolium && !inserimentoFascicoloPrisma && !inserimentoFascicoloItalprot){
                 codiceFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getCodiceFascicolo();
                 if (wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getAnnoFascicolo() != null) {
                   annoFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getAnnoFascicolo();
                 }else{
                   annoFascicoloNUOVO = this.gestioneWSDMManager.getAnnoFromDate(dataProtocollo);
                 }
                 numeroFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getNumeroFascicolo();
               }else if(inserimentoFascicoloFolium){
                 codiceFascicoloNUOVO = classificafascicolo;
                 annoFascicoloNUOVO = this.gestioneWSDMManager.getAnnoFromDate(dataProtocollo);
               }else if(inserimentoFascicoloPrisma){
                 codiceFascicoloNUOVO= datiWSDM.get("codicefascicolo");
                 annoFascicoloNUOVO=new Long(datiWSDM.get("annofascicolo"));
                 numeroFascicoloNUOVO=datiWSDM.get("numerofascicolo");
               }else if(inserimentoFascicoloItalprot){
                 codiceFascicoloNUOVO= datiWSDM.get("codicefascicolo");
                 annoFascicoloNUOVO=new Long(datiWSDM.get("annofascicolo"));
               } else
                 codiceFascicoloNUOVO= datiWSDM.get("codicefascicolo");

               if("TITULUS".equals(tipoWSDM))
                 classificafascicolo = classificadocumento;

               if("ENGINEERINGDOC".equals(tipoWSDM)) {
                 codiceufficio = uocompetenza;
                 codiceufficiodes = uocompetenzadescrizione;
               }

               this.gestioneWSDMManager.setWSFascicolo(entita, key1, null, null, null, codiceFascicoloNUOVO, annoFascicoloNUOVO,
                   numeroFascicoloNUOVO, classificafascicolo,codiceaoo, codiceufficio,struttura,null,classificadescrizione,voce,codiceaoodes,codiceufficiodes);
             }
             //Salvatagio in WSDOCUMENTO
             Long idWSDocumento = this.gestioneWSDMManager.setWSDocumento(entita, key1, null, null, null, numeroDocumento, annoProtocollo, numeroProtocollo, oggettodocumento,inout);

             //Salvataggio della mail in WSALLEGATI
             this.gestioneWSDMManager.setWSAllegati("W_INVCOM", idprg, newIdcom.toString(), null, null, idWSDocumento);

             //Salvataggio dell'allegato in WSALLEGATI
             this.gestioneWSDMManager.setWSAllegati("W_DOCDIG", idprg, newIddocdig.toString(), null, null, idWSDocumento);

             //Salvataggio degli allegati all'ordine (DOCUMGARA=6) in WSALLEGATI
             if (newIdcom > 0) {
               for (int i = 0; i < numAll; i++) {
                 Long iddocdig =SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 3).longValue();
                 this.gestioneWSDMManager.setWSAllegati("W_DOCDIG", idprg, iddocdig.toString(), null, null, idWSDocumento);
               }
               for (int i = 0; i < numAll; i++) {
                 Long iddocdig =idAllegatiNuovi[i];
                 this.gestioneWSDMManager.setWSAllegati("W_DOCDIG", idprg, iddocdig.toString(), null, null, idWSDocumento);
               }

               //Inserimento allegato di sintesi
               this.gestioneWSDMManager.setWSAllegati("W_DOCDIG", idprg, idAllegatoSintesi.toString(), null, null, idWSDocumento);

             }

           }else{
             String messaggio = wsdmProtocolloDocumentoRes.getMessaggio();
             throw new GestoreException("Errore nella protocollazione del fascicolo","wsdm.fascicoloprotocollo.protocollazione.error",new Object[]{messaggio}, new Exception());
           }
        }

		if (logger.isDebugEnabled()) {
			logger.debug("inviaComunicazioneOrdine: fine metodo");
		}
	}

	/**
	   * Controllo importo ordine minimo.
	   *
	   * @param idRicerca
	   * @param ditta
	   * @return HashMap
	   * @throws SQLException
	   */
	public HashMap controlloImportoMinimo(Long idRicerca, String ditta) throws SQLException{

	  HashMap valoriRitorno = new HashMap();
      StringBuffer msgRet=new StringBuffer();
      String codiceCatalogo = (String)this.sqlManager.getObject("select codcata from meric where id =?", new Object[]{idRicerca});
      Boolean controlloSuperato = true;
      String nessunOrdineMinimoImpostato = "SI";

      //Lista delle categorie con ordine minimo impostato
      List<?>listaCategorieOrdineMinimo = this.sqlManager.getListVector("select distinct nopega,catoff from opes where ngara3=? " +
            "and ordmin is not null", new Object[] {codiceCatalogo});
      if(listaCategorieOrdineMinimo!=null && listaCategorieOrdineMinimo.size()>0){
        for(int i=0;i<listaCategorieOrdineMinimo.size();i++){
          //Verifica se ci sono prodotti nell'ordine appartenenti alla categoria
          Long nopega = (Long)SqlManager.getValueFromVectorParam(listaCategorieOrdineMinimo.get(i), 0).getValue();
          String catoff = (String)SqlManager.getValueFromVectorParam(listaCategorieOrdineMinimo.get(i), 1).getValue();
          String sqlProdottiCategoria = "select distinct codimp,idprod,codoe,nome,preoff from meartcat,v_odaprod " +
           "where meartcat.id=v_odaprod.idartcat and v_odaprod.idric=? and v_odaprod.codimp= ? and nopega in " +
           "(select nopega from opes where opes.ngara3=meartcat.ngara and opes.catoff in (select caisim " +
           "from cais where caisim=? or codliv1=? or codliv2=? or codliv3=? or codliv4=? ))";
          List<?> listaProdotti = this.sqlManager.getListVector(sqlProdottiCategoria, new Object[]{idRicerca,ditta,catoff,catoff,catoff,catoff,catoff});
          if(listaProdotti!=null && listaProdotti.size()>0){
        	  nessunOrdineMinimoImpostato = "NO";
              //Si estrae l'ordine minimo della categoria
              Double ordmin = (Double) this.sqlManager.getObject("select ordmin from opes where ngara3=? and nopega=?", new Object[]{codiceCatalogo,nopega });
              //Si deve confrontare l'importo ordine minimo con l'importo totale ordinate degli articoli dell'ordine che vi fanno riferimento
	          Double importoOffertoTotale = null;
	          Object importoOfferto = this.sqlManager.getObject("select sum(preoff) from ("+ sqlProdottiCategoria +") prodottiCategoria group by codimp",
	                new Object[]{idRicerca, ditta, catoff,catoff,catoff,catoff,catoff});
	          if(importoOfferto!=null && importoOfferto instanceof Double)
	            importoOffertoTotale = (Double)importoOfferto;
	          else if(importoOfferto!=null && importoOfferto instanceof Long)
	            importoOffertoTotale = new Double((Long)importoOfferto);
	          if(importoOffertoTotale!= null && importoOffertoTotale.doubleValue()<ordmin.doubleValue()){
	            controlloSuperato = false;
	            if(msgRet.length()>1)
	              msgRet.append("\n");
	            msgRet.append("Categoria: " + catoff);
	            String descat =  (String)this.sqlManager.getObject("select descat from cais where caisim=?",new Object[]{catoff});
	            if(descat!=null)
	              msgRet.append(" - " + descat);
	            msgRet.append("\n");
	            msgRet.append("  Importo ordine minimo: " + UtilityNumeri.convertiImporto(ordmin, 2) + " euro\n");
	            msgRet.append("  Importo ordine: " + UtilityNumeri.convertiImporto(importoOffertoTotale, 2) + " euro\n");
	              String codice = null;
	              String nome = null;
	              msgRet.append("  Prodotti dell'ordine:\n");
	              for(int k=0;k<listaProdotti.size();k++){
		            codice = (String)SqlManager.getValueFromVectorParam(listaProdotti.get(k), 2).getValue();
		            nome = (String)SqlManager.getValueFromVectorParam(listaProdotti.get(k), 3).getValue();
	                if(k>0)
	                  msgRet.append("\n");
	                if (codice!=null){
		                  msgRet.append("   " + codice);
	                }
	                if (nome!=null){
	                	if (codice!=null){
			                  msgRet.append(" - ");
	                	} else {
	                		msgRet.append("   ");
	                	}
	                  msgRet.append(nome);
	                }
	              }
	              msgRet.append("\n");
	          	}
	        }
         }
      }

	  valoriRitorno.put("esito", controlloSuperato);
	  valoriRitorno.put("msg", msgRet);
	  valoriRitorno.put("nessunOrdineminImpostato", nessunOrdineMinimoImpostato);

	  return valoriRitorno;
	}

	/**
     * Viene estratto l'importo ordine minimo da OPES
     *
     * @param codiceCategoria
     * @param codiceCatalogo
     * @return Double
     * @throws SQLException
     */
	public Double getOrdineMin(Object codiceCategoria, String codiceCatalogo) throws SQLException{
	  Double ret = null;
	  if(codiceCategoria!=null){
	    ret = (Double)this.sqlManager.getObject("select ordmin from opes where ngara3=? and catoff=?", new Object[]{codiceCatalogo,(String)codiceCategoria});
	  }
	  return ret;
	}


	/**
     * Viene inserita inserita l'offerta Economica proveniente dai messaggi xml della busta FS11C
     *
     * @param ngara
     * @param codgar
     * @param codimp
     * @param offertaEconomica
     * @return boolean
     * @throws GestoreException
	 * @throws JspException
	 * @throws NumberFormatException
	 * @throws ParseException
     */
	private boolean inserimentoOffertadaXML(String ngara,String codgar, String codimp,OffertaEconomicaType offertaEconomica,
	    Cipher decoder) throws GestoreException, NumberFormatException, JspException, ParseException{
	  Long modlicg=null;
	  Long ribcal=null;
	  String sicinc=null;
	  String onsogrib=null;
	  Double ribasso = null;
	  Double importoOfferto = null;
	  Vector<DataColumn> elencoCampiDITG = new Vector<DataColumn>();
	  try {
        List<?> datiGare=datiGare = this.sqlManager.getVector("select modlicg, ribcal, sicinc, onsogrib from gare where ngara=?",
            new Object[] {ngara });
        if (datiGare != null && datiGare.size() > 0) {
          modlicg = (Long) SqlManager.getValueFromVectorParam(datiGare, 0).getValue();
          ribcal = (Long) SqlManager.getValueFromVectorParam(datiGare, 1).getValue();
          sicinc = (String) SqlManager.getValueFromVectorParam(datiGare, 2).getValue();
          onsogrib = (String) SqlManager.getValueFromVectorParam(datiGare, 3).getValue();
        }

        byte[] datoCifrato = null;
        byte[] datoDecifrato = null;

        //Si deve calcolare l'importo offerto
        double importoBaseGara;
        if(offertaEconomica.isSetImportoBaseGara())
          importoBaseGara=offertaEconomica.getImportoBaseGara();
        else{
          importoBaseGara=0;
        }
        double importoSicurezza;
        if(offertaEconomica.isSetImportoSicurezza())
          importoSicurezza=offertaEconomica.getImportoSicurezza();
        else{
          importoSicurezza=0;
        }
        double importoNonSoggettoRibasso;
        if(offertaEconomica.isSetImportoNonSoggettoRibasso())
          importoNonSoggettoRibasso=offertaEconomica.getImportoNonSoggettoRibasso();
        else{
          importoNonSoggettoRibasso=0;
        }
        double importoOneriProgettazione;
        if(offertaEconomica.isSetImportoOneriProgettazione())
          importoOneriProgettazione=offertaEconomica.getImportoOneriProgettazione();
        else{
          importoOneriProgettazione=0;
        }

        if((new Long(5).equals(modlicg) || new Long(14).equals(modlicg)) && (new Long(1).equals(ribcal) || new Long(3).equals(ribcal))){
          if(offertaEconomica.isSetPercentualeRibasso() && !offertaEconomica.isSetPercentualeRibassoCifrato())
            ribasso =  new Double(-offertaEconomica.getPercentualeRibasso());
          else if(offertaEconomica.isSetPercentualeRibassoCifrato() && decoder!=null){
            datoCifrato = offertaEconomica.getPercentualeRibassoCifrato();
            datoDecifrato = SymmetricEncryptionUtils.translate(decoder, datoCifrato);
            ribasso = Double.valueOf(new String(datoDecifrato));
            ribasso = new Double(-ribasso.doubleValue());
          }else if(offertaEconomica.isSetPercentualeAumento() && !offertaEconomica.isSetPercentualeAumentoCifrato())
            ribasso =  new Double(offertaEconomica.getPercentualeAumento());
          else if(offertaEconomica.isSetPercentualeAumentoCifrato() && decoder!=null){
            datoCifrato = offertaEconomica.getPercentualeAumentoCifrato();
            datoDecifrato = SymmetricEncryptionUtils.translate(decoder, datoCifrato);
            ribasso = Double.valueOf(new String(datoDecifrato));
          }else{
            logger.error("Errore nell'acquisizione dell'offerta economica della gara " + ngara + " per la ditta " + codimp +". Nel tracciato xml non e' presente il ribasso");
            return false;
          }

          if(offertaEconomica.isSetImportoOfferto() && !offertaEconomica.isSetImportoOffertoCifrato())
            importoOfferto= new Double(offertaEconomica.getImportoOfferto());
          else if (offertaEconomica.isSetImportoOffertoCifrato() && decoder!=null) {
            datoCifrato = offertaEconomica.getImportoOffertoCifrato();
            datoDecifrato = SymmetricEncryptionUtils.translate(decoder, datoCifrato);
            importoOfferto = Double.valueOf(new String(datoDecifrato));
          }else{
            //Nel caso di ribasso pesato non si entrerà mai in questa casistica
            importoOfferto = pgManager.calcolaImportoOfferto(importoBaseGara, importoSicurezza, importoNonSoggettoRibasso, importoOneriProgettazione, ribasso, sicinc, onsogrib);
          }

          elencoCampiDITG.add(new DataColumn("DITG.IMPOFF", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, importoOfferto)));
          elencoCampiDITG.add(new DataColumn("DITG.RIBAUO", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, ribasso)));
        }else  if((new Long(5).equals(modlicg) || new Long(14).equals(modlicg) || new Long(1).equals(modlicg) || new Long(13).equals(modlicg) || new Long(17).equals(modlicg)) && new Long(2).equals(ribcal)){

          if(offertaEconomica.isSetImportoOfferto() && !offertaEconomica.isSetImportoOffertoCifrato())
            importoOfferto= new Double(offertaEconomica.getImportoOfferto());
          else if (offertaEconomica.isSetImportoOffertoCifrato() && decoder!=null) {
            datoCifrato = offertaEconomica.getImportoOffertoCifrato();
            datoDecifrato = SymmetricEncryptionUtils.translate(decoder, datoCifrato);
            importoOfferto = Double.valueOf(new String(datoDecifrato));
          }else{
            logger.error("Errore nell'acquisizione dell'offerta economica della gara " + ngara + " per la ditta " + codimp +". Nel tracciato xml non e' presente l'importo offerto");
            return false;
          }
          ribasso=new Double(this.aggiudicazioneManager.calcolaRIBAUO(importoBaseGara, importoOneriProgettazione, importoSicurezza, importoNonSoggettoRibasso, sicinc, importoOfferto, onsogrib));
          String cifreRibasso=this.pgManagerEst1.getNumeroDecimaliRibasso(codgar);
          if(cifreRibasso!=null && !"".equals(cifreRibasso)){
            Number ribassoNumber = UtilityNumeri.arrotondaNumero(ribasso, new Integer(cifreRibasso));
            ribasso = new Double(ribassoNumber.doubleValue());
          }

          elencoCampiDITG.add(new DataColumn("DITG.IMPOFF", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, importoOfferto)));
          elencoCampiDITG.add(new DataColumn("DITG.RIBAUO", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, ribasso)));
        }else if((new Long(1).equals(modlicg) || new Long(13).equals(modlicg) || new Long(17).equals(modlicg)) && new Long(1).equals(ribcal)){
          if(offertaEconomica.isSetPercentualeRibasso() && !offertaEconomica.isSetPercentualeRibassoCifrato())
            ribasso =  new Double(-offertaEconomica.getPercentualeRibasso());
          else if(offertaEconomica.isSetPercentualeRibassoCifrato() && decoder!=null){
            datoCifrato = offertaEconomica.getPercentualeRibassoCifrato();
            datoDecifrato = SymmetricEncryptionUtils.translate(decoder, datoCifrato);
            ribasso = Double.valueOf(new String(datoDecifrato));
            ribasso = new Double(-ribasso.doubleValue());
          }else if(offertaEconomica.isSetPercentualeAumento() && !offertaEconomica.isSetPercentualeAumentoCifrato())
            ribasso =  new Double(offertaEconomica.getPercentualeAumento());
          else if(offertaEconomica.isSetPercentualeAumentoCifrato() && decoder!=null){
            datoCifrato = offertaEconomica.getPercentualeAumentoCifrato();
            datoDecifrato = SymmetricEncryptionUtils.translate(decoder, datoCifrato);
            ribasso = Double.valueOf(new String(datoDecifrato));
          }else{
            logger.error("Errore nell'acquisizione dell'offerta economica della gara " + ngara + " per la ditta " + codimp +". Nel tracciato xml non e' presente il ribasso");
            return false;
          }
          elencoCampiDITG.add(new DataColumn("DITG.RIBAUO", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, ribasso)));
        }else  if(new Long(6).equals(modlicg)){
          if(controlliOepvManager.isVecchiaOepv(codgar)){
            if(offertaEconomica.isSetImportoOfferto() && !offertaEconomica.isSetImportoOffertoCifrato())
              importoOfferto= new Double(offertaEconomica.getImportoOfferto());
            else if (offertaEconomica.isSetImportoOffertoCifrato() && decoder!=null) {
              datoCifrato = offertaEconomica.getImportoOffertoCifrato();
              datoDecifrato = SymmetricEncryptionUtils.translate(decoder, datoCifrato);
              importoOfferto = Double.valueOf(new String(datoDecifrato));
            }else{
              logger.error("Errore nell'acquisizione dell'offerta economica della gara " + ngara + " per la ditta " + codimp +". Nel tracciato xml non e' presente l'importo offerto");
              return false;
            }
            elencoCampiDITG.add(new DataColumn("DITG.IMPOFF", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, importoOfferto)));
          }else{
            boolean esito = false;
            esito=this.inserimentoOfferteTecnicheDaXML(ngara, codgar, codimp, offertaEconomica.getListaCriteriValutazione(), decoder, 2, null);
            if(!esito)
            return false;
          }
        }
        Double costiSicurezza = null;
        if(offertaEconomica.isSetCostiSicurezzaAziendali())
          costiSicurezza = new Double(offertaEconomica.getCostiSicurezzaAziendali());

        Double importoPermuta = null;
        if(offertaEconomica.isSetImportoOffertoPerPermuta() && !offertaEconomica.isSetImportoOffertoPerPermutaCifrato()){
          importoPermuta = new Double(offertaEconomica.getImportoOffertoPerPermuta());
        }else if(offertaEconomica.isSetImportoOffertoPerPermutaCifrato() && decoder!=null){
          datoCifrato = offertaEconomica.getImportoOffertoPerPermutaCifrato();
          datoDecifrato = SymmetricEncryptionUtils.translate(decoder, datoCifrato);
          importoPermuta = Double.valueOf(new String(datoDecifrato));
        }
	    elencoCampiDITG.add(new DataColumn("DITG.IMPPERM", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
            importoPermuta)));

	    Double importoCanoneAssistenza= null;
        if(offertaEconomica.isSetImportoOffertoPerCanoneAssistenza() && !offertaEconomica.isSetImportoOffertoPerCanoneAssistenzaCifrato()){
          importoCanoneAssistenza = new Double(offertaEconomica.getImportoOffertoPerCanoneAssistenza());
        }else if(offertaEconomica.isSetImportoOffertoPerCanoneAssistenzaCifrato() && decoder!=null){
          datoCifrato = offertaEconomica.getImportoOffertoPerCanoneAssistenzaCifrato();
          datoDecifrato = SymmetricEncryptionUtils.translate(decoder, datoCifrato);
          importoCanoneAssistenza = Double.valueOf(new String(datoDecifrato));
        }
        elencoCampiDITG.add(new DataColumn("DITG.IMPCANO", new JdbcParametro(JdbcParametro.TIPO_DECIMALE,
            importoCanoneAssistenza)));

        Double costiManodopera = null;
        Double costiManodoperaPercentuale = null;
        if(offertaEconomica.isSetImportoManodopera())
          costiManodopera = new Double(offertaEconomica.getImportoManodopera());
        if(offertaEconomica.isSetPercentualeManodopera()){
          costiManodoperaPercentuale = new Double(offertaEconomica.getPercentualeManodopera());
        }

        //Aggiornamento DITG
        elencoCampiDITG.add(new DataColumn("DITG.IMPSICAZI", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, costiSicurezza)));
        elencoCampiDITG.add(new DataColumn("DITG.IMPMANO", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, costiManodopera)));
        elencoCampiDITG.add(new DataColumn("DITG.PERCMANO", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, costiManodoperaPercentuale)));
        elencoCampiDITG.add(new DataColumn("DITG.PARTGAR", new JdbcParametro(JdbcParametro.TIPO_TESTO, "1")));
        elencoCampiDITG.add(new DataColumn("DITG.NGARA5", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
        elencoCampiDITG.add(new DataColumn("DITG.CODGAR5", new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar)));
        elencoCampiDITG.add(new DataColumn("DITG.DITTAO", new JdbcParametro(JdbcParametro.TIPO_TESTO, codimp)));

        DataColumnContainer containerDITG = new DataColumnContainer(elencoCampiDITG);
        containerDITG.getColumn("DITG.NGARA5").setObjectOriginalValue(ngara);
        containerDITG.getColumn("DITG.NGARA5").setChiave(true);
        containerDITG.getColumn("DITG.CODGAR5").setObjectOriginalValue(codgar);
        containerDITG.getColumn("DITG.CODGAR5").setChiave(true);
        containerDITG.getColumn("DITG.DITTAO").setObjectOriginalValue(codimp);
        containerDITG.getColumn("DITG.DITTAO").setChiave(true);

        containerDITG.update("DITG", sqlManager);

        //Gestione Import prezzi unitari
        if(new Long(5).equals(modlicg) || new Long(14).equals(modlicg) || (new Long(6).equals(modlicg) && ((!controlliOepvManager.isVecchiaOepv(codgar) && controlliOepvManager.checkFormato(ngara,new Long(52))) || controlliOepvManager.isVecchiaOepv(codgar)))){
          Long conteggioGcap = (Long)this.sqlManager.getObject("select count(*)  from GCAP where GCAP.NGARA = ? and GCAP.DITTAO is null", new Object[]{ngara});
          if(conteggioGcap!=null && conteggioGcap.longValue()>0){
            //Cancellazione DPRE
            this.sqlManager.update("delete from dpre where ngara=? and dittao=?", new Object[]{ngara,codimp});
            //Cancellazione di XDPRE
            if (this.sqlManager.isTable("XDPRE")){
              this.sqlManager.update("delete from xdpre where xngara=? and xdittao=?", new Object[]{ngara,codimp});
            }
            ListaComponentiOffertaType listaComponentiOfferta= offertaEconomica.getListaComponentiDettaglio();
            if(listaComponentiOfferta!=null && listaComponentiOfferta.sizeOfComponenteDettaglioArray()>0){
              int id;
              Double prezzoUnitario = null;
              Double importo = null;
              Double qta = null;
              Date dataConsegnaOfferta = null;
              Long tipologia = null;
              String note =null;
              String codiceCampoAggiuntivo=null;
              Object valoreCampoAggiuntivo=null;
              String insertXDPRE = null;
              String insertXDPREValori = null;
              String codiceTabellato =null;
              Object parametri[]= null;
              Object parametriXDPRE[]= null;
              Double ribassoPesato = null;
              Double ribassoPrezzoUnit = null;
              Boolean isRicercaMercatoNegoziata = false;
              String insertDPRE = "insert into dpre(ngara,contaf,dittao,preoff,impoff";
              String insertDPREValori = "values(?,?,?,?,?";
              Long iterga = (Long) this.sqlManager.getObject("select t.iterga from gare g,torn t where g.codgar1=t.codgar and g.ngara=?", new Object[]{ngara});
              //nel caso di ricerca di mercato negoziata copio quanti.gcap->qtaordinabile.dpre
              //cf060422
              if(Long.valueOf(8).equals(iterga)) {
          		isRicercaMercatoNegoziata = true;
          	  }
              if( new Long(3).equals(ribcal)){
                //Gestione ribasso pesato
                insertDPRE += ",perrib,ribpeso";
                insertDPREValori += ",?,?";
                parametri = new Object[7];
              }else{
                  if(isRicercaMercatoNegoziata) {
                      insertDPRE += ",qtaordinabile";
                      insertDPREValori += ",?";
                      insertDPRE += ",datacons";
                      insertDPREValori += ",?";
                      insertDPRE += ",tipologia";
                      insertDPREValori += ",?";
                      insertDPRE += ",note";
                      insertDPREValori += ",?";
                	  parametri = new Object[9];
                  }else {
                	  parametri = new Object[5];
                  }
              }
              insertDPRE +=")";
              insertDPREValori +=")";
              for(int i=0;i<listaComponentiOfferta.sizeOfComponenteDettaglioArray();i++){
            	Boolean isRigaOffertaCaricabile=true;
                id=listaComponentiOfferta.getComponenteDettaglioArray(i).getId();
                if(listaComponentiOfferta.getComponenteDettaglioArray(i).isSetQuantitaOfferta()) {
                	Double quantitaOfferta = listaComponentiOfferta.getComponenteDettaglioArray(i).getQuantitaOfferta();
                    if(quantitaOfferta != null && Double.valueOf(0).equals(quantitaOfferta)) {
                    	isRigaOffertaCaricabile=false;
                    }
                }
                if(isRigaOffertaCaricabile) {
                    if(listaComponentiOfferta.getComponenteDettaglioArray(i).isSetPrezzoUnitario() && !listaComponentiOfferta.getComponenteDettaglioArray(i).isSetPrezzoUnitarioCifrato())
                        prezzoUnitario= new Double(listaComponentiOfferta.getComponenteDettaglioArray(i).getPrezzoUnitario());
                      else if (listaComponentiOfferta.getComponenteDettaglioArray(i).isSetPrezzoUnitarioCifrato() && decoder!=null) {
                        datoCifrato = listaComponentiOfferta.getComponenteDettaglioArray(i).getPrezzoUnitarioCifrato();
                        datoDecifrato = SymmetricEncryptionUtils.translate(decoder, datoCifrato);
                        prezzoUnitario = Double.valueOf(new String(datoDecifrato));
                      }else
                        prezzoUnitario = null;
                      if(listaComponentiOfferta.getComponenteDettaglioArray(i).isSetPrezzoUnitario() && !listaComponentiOfferta.getComponenteDettaglioArray(i).isSetPrezzoUnitarioCifrato())
                        importo= new Double(listaComponentiOfferta.getComponenteDettaglioArray(i).getImporto());
                      else if (listaComponentiOfferta.getComponenteDettaglioArray(i).isSetImportoCifrato() && decoder!=null) {
                        datoCifrato = listaComponentiOfferta.getComponenteDettaglioArray(i).getImportoCifrato();
                        datoDecifrato = SymmetricEncryptionUtils.translate(decoder, datoCifrato);
                        importo = Double.valueOf(new String(datoDecifrato));
                      }else
                        importo = null;


                      if( new Long(3).equals(ribcal)){
                        //Gestione ribasso pesato
                        if(listaComponentiOfferta.getComponenteDettaglioArray(i).isSetRibasso() && !listaComponentiOfferta.getComponenteDettaglioArray(i).isSetRibassoCifrato())
                          ribassoPrezzoUnit= new Double(-listaComponentiOfferta.getComponenteDettaglioArray(i).getRibasso());
                        else if (listaComponentiOfferta.getComponenteDettaglioArray(i).isSetRibassoCifrato() && decoder!=null) {
                          datoCifrato = listaComponentiOfferta.getComponenteDettaglioArray(i).getRibassoCifrato();
                          datoDecifrato = SymmetricEncryptionUtils.translate(decoder, datoCifrato);
                          ribassoPrezzoUnit = Double.valueOf(new String(datoDecifrato));
                          ribassoPrezzoUnit = new Double(-ribassoPrezzoUnit.doubleValue());
                        }else
                          ribassoPrezzoUnit = null;

                        if(listaComponentiOfferta.getComponenteDettaglioArray(i).isSetRibassoPesato() && !listaComponentiOfferta.getComponenteDettaglioArray(i).isSetRibassoPesatoCifrato())
                          ribassoPesato= new Double(-listaComponentiOfferta.getComponenteDettaglioArray(i).getRibassoPesato());
                        else if (listaComponentiOfferta.getComponenteDettaglioArray(i).isSetRibassoPesatoCifrato() && decoder!=null) {
                          datoCifrato = listaComponentiOfferta.getComponenteDettaglioArray(i).getRibassoPesatoCifrato();
                          datoDecifrato = SymmetricEncryptionUtils.translate(decoder, datoCifrato);
                          ribassoPesato = Double.valueOf(new String(datoDecifrato));
                          ribassoPesato = new Double(-ribassoPesato.doubleValue());
                        }else
                          ribassoPesato = null;
                      }

                      if(isRicercaMercatoNegoziata) {
                    		//Attenzione a Oracle
                    		qta = (Double) this.sqlManager.getObject("select quanti from gcap where ngara=? and contaf=?", new Object[]{ngara,Long.valueOf(id)});
                    		if(listaComponentiOfferta.getComponenteDettaglioArray(i).isSetDataConsegnaOfferta()) {
                    			Calendar calDataConsegnaOfferta = listaComponentiOfferta.getComponenteDettaglioArray(i).getDataConsegnaOfferta();
                    			if(calDataConsegnaOfferta!=null) {
                    				dataConsegnaOfferta = calDataConsegnaOfferta.getTime();
                    			}
                    		}
                    		if(listaComponentiOfferta.getComponenteDettaglioArray(i).isSetTipo()) {
                    			String tipo = listaComponentiOfferta.getComponenteDettaglioArray(i).getTipo();
                    			tipo = StringUtils.stripToEmpty(tipo);
                    			if(!"".equals(tipo)) {
                    				tipologia = Long.valueOf(tipo);
                    			}
                    		}
                    		if(listaComponentiOfferta.getComponenteDettaglioArray(i).isSetNote()) {
                    			note = listaComponentiOfferta.getComponenteDettaglioArray(i).getNote();
                    		}
                      }

                      parametri[0]= ngara;
                      parametri[1]= new Long(id);
                      parametri[2]= codimp;
                      parametri[3]= prezzoUnitario;
                      parametri[4]= importo;
                      if( new Long(3).equals(ribcal)){
                        parametri[5]= ribassoPrezzoUnit;
                        parametri[6]= ribassoPesato;
                      }else {
                          if(isRicercaMercatoNegoziata) {
                          	parametri[5]= qta;
                          	parametri[6]= dataConsegnaOfferta;
                          	parametri[7]= tipologia;
                          	parametri[8]= note;
                          }
                      }

                      this.sqlManager.update(insertDPRE + " " + insertDPREValori,parametri);

                      //Inserimento campi aggiuntivi XDPRE se presenti nel xml
                      AttributoGenericoType vettoreComponenteDettaglio[] = listaComponentiOfferta.getComponenteDettaglioArray(i).getAttributoAggiuntivoArray();
                      if(vettoreComponenteDettaglio!=null && vettoreComponenteDettaglio.length>0){
                        insertXDPRE = "insert into xdpre(xngara,xcontaf,xdittao";
                        insertXDPREValori = "values(?,?,?";
                        parametriXDPRE = new Object[vettoreComponenteDettaglio.length + 3];
                        parametriXDPRE[0]= ngara;
                        parametriXDPRE[1]= new Long(id);
                        parametriXDPRE[2]= codimp;
                        for(int j=0;j<vettoreComponenteDettaglio.length;j++){
                          codiceCampoAggiuntivo=vettoreComponenteDettaglio[j].getCodice();
                          if(vettoreComponenteDettaglio[j].isSetValoreData()){
                            valoreCampoAggiuntivo = vettoreComponenteDettaglio[j].getValoreData().getTime();
                          }else if(vettoreComponenteDettaglio[j].isSetValoreNumerico()){
                            double valoreTmp = vettoreComponenteDettaglio[j].getValoreNumerico();
                            valoreCampoAggiuntivo = new Double(valoreTmp);
                          }else if(vettoreComponenteDettaglio[j].isSetValoreStringa()){
                            if(vettoreComponenteDettaglio[j].getValoreStringa()=="" || vettoreComponenteDettaglio[j].isNil())
                              valoreCampoAggiuntivo = null;
                            else{
                              //Da portale arrivano come stringa anche i valori tabellati, che invece sono numerici,
                              //quindi si deve controllare se al campo è associato un tabellato, ed in questo caso
                              //si deve convertire in intero
                              codiceTabellato = (String)this.sqlManager.getObject("select dyncam_tab from dyncam_gen where dynent_name=? and dyncam_name=?", new Object[]{"XDPRE",codiceCampoAggiuntivo.toUpperCase()});
                              if(codiceTabellato!=null && !"".equals(codiceTabellato))
                                valoreCampoAggiuntivo = new Long(vettoreComponenteDettaglio[j].getValoreStringa().trim());
                              else
                                valoreCampoAggiuntivo = vettoreComponenteDettaglio[j].getValoreStringa();
                            }
                          }else
                            valoreCampoAggiuntivo = null;
                          insertXDPRE+="," + codiceCampoAggiuntivo;
                          insertXDPREValori+=",?";
                          parametriXDPRE[j+3]= valoreCampoAggiuntivo;
                        }
                        insertXDPRE+=") ";
                        insertXDPREValori+=")";
                        this.sqlManager.update(insertXDPRE + insertXDPREValori,parametriXDPRE);
                      }
                }//riga offerta caricabile

              }//for
            }
          }

        }

      } catch (SQLException e) {
        throw new GestoreException("Errore nell'acquisizione dell'offerta economica", null, e);
      } catch (GeneralSecurityException e) {
        throw new GestoreException("Errore nell'acquisizione dell'offerta economica", null, e);
      }
      return true;
	}

	/**
	 * Viene acquisita l'offerta con i valori presenti nel file json del questionario prodotto dal portale
	 * @param listaDocumenti
	 * @param ngara
	 * @param codgar
	 * @param codimp
	 * @param idCom
	 * @param decoder
	 * @return boolean
	 * @throws GestoreException
	 * @throws GeneralSecurityException
	 */
	private boolean inserimentoOffertadaJson(ListaDocumentiType listaDocumenti, String ngara,String codgar, String codimp, String idCom,Cipher decoder) throws GestoreException, GeneralSecurityException{
	  Double ribasso = null;
      Double importoOfferto = null;
	  byte[] file = null;
	  boolean esito = false;
	  if (listaDocumenti != null) {
	    DocumentoType documento = null;
	    String nomeFile= null;
	    for (int j = 0; j < listaDocumenti.sizeOfDocumentoArray(); j++) {
	        documento = listaDocumenti.getDocumentoArray(j);
	        nomeFile = documento.getNomeFile();
	        if(CostantiAppalti.nomeFileQestionario.equals(nomeFile)){
	          file = this.pgManager.getFileFromDocumento(documento,idCom);
	          if(file==null){
	            throw new GestoreException("Errore nell'acquisizione della documentazione della ditta " + codimp + " per la gara " + ngara + ". Viene fatto riferimento a allegati non disponibili", null, new Exception());
	          }

	          break;
	        }
	    }
	  }

	  if(file != null && file.length>0) {
	    if(decoder!=null)
	      file = SymmetricEncryptionUtils.translate(decoder, file);
	    //Conversione in json della stringa
	    String json = new String(file);
        JSONObject jsonOggetto = (JSONObject)JSONSerializer.toJSON(json);
        JSONObject surveyType = ((JSONObject)jsonOggetto.get(CostantiAppalti.sezioneDatiQuestionario));

        JSONObject result = ((JSONObject)surveyType.get("result"));
        if(result.get(CostantiAppalti.importoOffertoQuestionario) instanceof Double)
          importoOfferto = result.getDouble(CostantiAppalti.importoOffertoQuestionario);
        else if(result.get(CostantiAppalti.importoOffertoQuestionario) instanceof Integer)
          importoOfferto = new Double(result.getInt(CostantiAppalti.importoOffertoQuestionario)).doubleValue();
        else {
          importoOfferto = null;
        }
        if(result.get(CostantiAppalti.ribassoOffertoQuestionario) instanceof Double)
          ribasso = result.getDouble(CostantiAppalti.ribassoOffertoQuestionario);
        else if(result.get(CostantiAppalti.ribassoOffertoQuestionario) instanceof Integer)
          ribasso = new Double(result.getInt(CostantiAppalti.ribassoOffertoQuestionario)).doubleValue();
        else {
          ribasso = null;
        }
        if (ribasso!=null && ribasso.doubleValue()!= 0) {
          BigDecimal bdImportoOfferto = BigDecimal.valueOf(ribasso.doubleValue()).setScale(5, BigDecimal.ROUND_HALF_UP);
          ribasso = bdImportoOfferto.negate().doubleValue();
        }
        esito = true;

        Vector<DataColumn> elencoCampiDITG = new Vector<DataColumn>();
        try {
          Long modlicg =(Long)this.sqlManager.getObject("select modlicg from gare where ngara=?", new Object[] {ngara});
          elencoCampiDITG.add(new DataColumn("DITG.IMPOFF", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, importoOfferto)));
          String campoRiabsso="DITG.RIBAUO";
          if(new Long(6).equals(modlicg))
            campoRiabsso="DITG.RIBOEPV ";
          DataColumn colRibasso = new DataColumn(campoRiabsso, new JdbcParametro(JdbcParametro.TIPO_DECIMALE, ribasso));
          if(ribasso==null)
            colRibasso.setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Double(0)));
          elencoCampiDITG.add(colRibasso);

          //Aggiornamento DITG
          elencoCampiDITG.add(new DataColumn("DITG.PARTGAR", new JdbcParametro(JdbcParametro.TIPO_TESTO, "1")));
          elencoCampiDITG.add(new DataColumn("DITG.NGARA5", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
          elencoCampiDITG.add(new DataColumn("DITG.CODGAR5", new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar)));
          elencoCampiDITG.add(new DataColumn("DITG.DITTAO", new JdbcParametro(JdbcParametro.TIPO_TESTO, codimp)));

          DataColumnContainer containerDITG = new DataColumnContainer(elencoCampiDITG);
          containerDITG.getColumn("DITG.NGARA5").setObjectOriginalValue(ngara);
          containerDITG.getColumn("DITG.NGARA5").setChiave(true);
          containerDITG.getColumn("DITG.CODGAR5").setObjectOriginalValue(codgar);
          containerDITG.getColumn("DITG.CODGAR5").setChiave(true);
          containerDITG.getColumn("DITG.DITTAO").setObjectOriginalValue(codimp);
          containerDITG.getColumn("DITG.DITTAO").setChiave(true);

          containerDITG.update("DITG", sqlManager);

        } catch (SQLException e) {
          throw new GestoreException("Errore nell'acquisizione dell'offerta economica", null, e);
        }
	  }

      return esito;
    }

	/**
     * Viene eseguito il controllo che i punteggi dei criteri
     * ricadano nell'intervallo definito da MINPUN e MAXPUN.
     * Inoltre si controlla che se è stato definito un punteggio in DPUN, allora
     * vi devono essere per ogni ditta tante occorrenze in DPUN quante solo le occorrenze
     * di GOEV
     *
     * @param ngara
     * @param isGaraOffUnica
     * @param sqlManager
     * @param messaggio
     * @param fase
     * @param suffissoMessaggi
     * @return boolean [] { indice 0: punteggi tutti all'interno dell'intervallo?
     *                      indice 1: sogli minima per i criteri impostata?
     *                      indice 2: criteri in DPUN tutti valorizzati?
     *                    }
     *
     * @throws JspException
     */
	public boolean[] controlloSogliePunteggiDitte(String ngara, boolean isGaraOffUnica, SqlManager sqlManager, StringBuilder messaggio, String fase, String suffissoMessaggi) throws JspException {

	     boolean retIntervalloPunteggi = true;
	     boolean criteriTuttiValorizzati = true;
	     Long tipoCriterio = new Long(1);
	     boolean sogliaMinimaCriteriImpostata = false;

	     if(fase!=null && new Long(fase).longValue()==GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE)
	       tipoCriterio= new Long(2);

	     try {
	       String selectDITG = "select dittao from ditg where ngara5=? and (INVOFF <> '2' or INVOFF is null) and "
	         + "(AMMGAR <> '2' or AMMGAR is null) and (MOTIES < 99 or MOTIES is null)";
	       List<?> listaDitte = sqlManager.getListVector(selectDITG, new Object[] { ngara });
	       if (listaDitte != null && listaDitte.size() > 0) {
	         String ditta=null;
	         boolean controlloPunteggioMinimoTecnicoSuperato = true;
	         boolean controlloPunteggioMinimoEconomicoSuperato = true;
             boolean controlloPunteggioMassimoSuperato = true;

             String selectPunteggiCriteri = "select d.punteg, g.maxpun from goev g, dpun d where g.ngara=d.ngara and g.necvan=d.necvan and g.ngara=? and d.dittao=?";
	         for (int j = 0; j < listaDitte.size(); j++) {
	           ditta=SqlManager.getValueFromVectorParam(listaDitte.get(j), 0).getStringValue();

	           List<?> listaPunteggi= sqlManager.getListVector(selectPunteggiCriteri, new Object[] { ngara,ditta });

	           if (listaPunteggi != null && listaPunteggi.size() > 0 && criteriTuttiValorizzati) {
	             Double punteggioMassimo = null;
	             Double punteggio = null;

	             if(!isGaraOffUnica && !this.controlloCriteriTuttiValorizzati(ngara, ditta, tipoCriterio)){
	               criteriTuttiValorizzati=false;
	               break;
	             }else{
	               //Controllo punteggi dei criteri della ditta rispetto al valore massimo
	               for (int i = 0; i < listaPunteggi.size(); i++) {
	                   Vector<?> punteggi = (Vector<?>) listaPunteggi.get(i);
	                   punteggio = ((JdbcParametro) punteggi.get(0)).doubleValue();
	                   punteggioMassimo = ((JdbcParametro) punteggi.get(1)).doubleValue();

	                   if (punteggioMassimo != null && punteggio != null && punteggio.doubleValue() > punteggioMassimo.doubleValue()) {
	                     controlloPunteggioMassimoSuperato = false;
	                   }
	                 }

	             }
	           }
	         }

	         if(!criteriTuttiValorizzati && !isGaraOffUnica){
	           messaggio.append(UtilityTags.getResource("errors.gestoreException.*." + suffissoMessaggi +".ControlloPresenzaPunteggiCriteri",null,
	                 false));
	         }else{
	           //Controllo punteggi tecnici dei criteri delle ditte rispetto ai rispettivi valori tecnici minimi
               boolean esito[]=this.esitoControlloPunteggiCriteriDitteSogliaMinima(ngara, new Long(1),null);
               controlloPunteggioMinimoTecnicoSuperato = esito[0];
               sogliaMinimaCriteriImpostata = esito[1];

               //Controllo punteggi economici dei criteri delle ditte rispetto ai rispettivi valori economici minimi, da fare quando la fase è quella tecnica
               if(fase!=null && new Long(fase).longValue()==GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE){
                 esito=this.esitoControlloPunteggiCriteriDitteSogliaMinima(ngara, new Long(2),null);
                 controlloPunteggioMinimoEconomicoSuperato = esito[0];
                 if(!sogliaMinimaCriteriImpostata)
                 sogliaMinimaCriteriImpostata = esito[1];
               }

	           if(!controlloPunteggioMinimoTecnicoSuperato || !controlloPunteggioMinimoEconomicoSuperato || !controlloPunteggioMassimoSuperato){
                 if (!isGaraOffUnica) {
                   String par[] = new String[]{""};
                   if(sogliaMinimaCriteriImpostata)
                     par[0]=" o inferiore alla sua soglia minima";
                   messaggio.append(UtilityTags.getResource("errors.gestoreException.*." + suffissoMessaggi + ".ControlloPunteggiCriteri", par,
                         false));

                 }
                 retIntervalloPunteggi = false;
               }
	         }
          }
	    } catch (SQLException e) {
	      throw new JspException("Errore durante il controllo del punteggio dei criteri", e);
	    } catch (GestoreException e) {
	      throw new JspException("Errore durante il controllo del punteggio dei criteri", e);
	    }

	    boolean ret[] = new boolean[3];
	    ret[0]=retIntervalloPunteggi;
	    ret[1]=sogliaMinimaCriteriImpostata;
	    ret[2]=criteriTuttiValorizzati;
	    return ret;
	  }

	public void gestioneWSALLEGATI(Long idComunicazione,String idprg, Long idWdocdig) throws SQLException{
	//Caricamento dati per l'inserimento in WSALLEGATI

        Vector<?> datiInvcom = this.sqlManager.getVector("select comnumprot,comdatprot from w_invcom where idprg='PA' and idcom=?", new Object[]{idComunicazione});
        if(datiInvcom!=null && datiInvcom.size()>0){
          try {
            String comnumprot = SqlManager.getValueFromVectorParam(datiInvcom, 0).stringValue();
            Timestamp comdatprot = SqlManager.getValueFromVectorParam(datiInvcom, 1).dataValue();
            if(comnumprot!=null && comdatprot!= null){
              Calendar dataProt = Calendar.getInstance();
              dataProt.setTime(new Date(comdatprot.getTime()));
              int annoProtocollo=dataProt.get(Calendar.YEAR);
              Long idDocumentoProtocollo = (Long) this.sqlManager.getObject("select id from wsdocumento where numeroprot=? and annoprot=?", new Object[]{comnumprot,new Long(annoProtocollo)});
              int idWsallegati = this.genChiaviManager.getNextId("WSALLEGATI");
              this.sqlManager.update("insert into wsallegati(id, entita, key1, key2, idwsdoc) values(?,?,?,?,?)",
                  new Object[]{idWsallegati, "W_DOCDIG", idprg, idWdocdig.toString(),idDocumentoProtocollo});
            }
          } catch (GestoreException e) {
            // riemetto il messaggio dell'eccezione in seguito al controllo fallito
            throw new SQLException(e.getMessage());
          }

        }
    }


	/**
     * Viene eseguito per la gara/lotto passato come parametro il controllo all'esistenza
     * di comunicazioni di tipo FS11A','FS11B' e 'FS11C'(nel caso si consideri l'offerta,
     * altrimenti FS10) con stato=5 associate a ditte non ammese.
     * Se queste esistono ne viene impostato lo stato a 8
     *
     * @param ngara
     * @param tipo ("OFFERTE", "OFFERTE_ECO_LOTTI_DISTINTI", "DOC_AMM", "DOMANDE")
     *
     * @throws SQLException
     */
	public void impostaComunicazioniAScartate(String ngara, String tipo) throws SQLException{

	 this.impostaComunicazioniA(ngara, tipo, 8);

    }


	/**
     * Viene eseguito per la gara/lotto passato come parametro il controllo all'esistenza
     * di comunicazioni di tipo 'FS11B' e 'FS11C' in stato 8 associate a ditte ammese.
     * Se queste esistono ne viene impostato lo stato a 5
     *
     * @param ngara
     * @param tipo
     *
     * @throws SQLException
     */
    public void impostaComunicazioniDaRiaquisire(String ngara) throws SQLException{

     this.impostaComunicazioniA(ngara, "OFFERTE", 5);

    }

    /**
     * Viene eseguito per la gara/lotto passato come parametro il controllo all'esistenza
     * di comunicazioni di tipo FS11A','FS11B' e 'FS11C'(nel caso si consideri l'offerta,
     * altrimenti FS10) con stato=13 associate a ditte non ammese.
     * Se queste esistono ne viene impostato lo stato a 20
     *
     * @param ngara
     * @param tipo ("OFFERTE", "OFFERTE_ECO_LOTTI_DISTINTI", "DOC_AMM", "DOMANDE")
     *
     * @throws SQLException
     */
    public void impostaComunicazioniAnonimeAScartate(String ngara, String tipo) throws SQLException{

     this.impostaComunicazioniA(ngara, tipo, 20);

    }


    /**
     * Viene eseguito per la gara/lotto passato come parametro il controllo all'esistenza
     * di comunicazioni di tipo 'FS11B' e 'FS11C' in stato 20 associate a ditte ammese.
     * Se queste esistono ne viene impostato lo stato a 13
     *
     * @param ngara
     * @param tipo
     *
     * @throws SQLException
     */
    public void impostaComunicazioniAnonimeDaRiaquisire(String ngara) throws SQLException{

     this.impostaComunicazioniA(ngara, "OFFERTE", 13);

    }

    /**
     * Viene eseguito per il lotto di una gara a lotti plico unico con offerte distinte
     * passato come parametro il controllo all'esistenza di comunicazioni di tipo 'FS11B'
     * e 'FS11C' in stato 8 associate a ditte ammese.
     * Se queste esistono ne viene impostato lo stato a 5
     *
     * @param ngara
     * @param tipo
     *
     * @throws SQLException
     */
    public void impostaComunicazioniDaRiaquisireLottoPlicoUnicoOfferteDistinte(String ngara) throws SQLException{

     this.impostaComunicazioniA(ngara, "OFFERTE_ECO_LOTTI_DISTINTI", 5);

    }


    /**
     * Viene eseguito per la gara/lotto passato come parametro il controllo all'esistenza
     * di comunicazioni di tipo FS11A','FS11B' e 'FS11C'(nel caso si consideri l'offerta,
     * altrimenti FS10) da elaborare associate a ditte non ammese nel caso di stato da impostare pari a 8.
     * Se queste esistono ne viene impostato lo stato a 8.
     * Nel caso di stato di impostare pari a 5, si cercano solo le comunicazioni 'FS11B' e 'FS11C'
     * con stato posto a 'scartate' associate a ditte ammesse in gara.
     * Se queste esistono ne viene impostato lo stato a 8.
     * Le comunicazioni di tipo 'FS11A' vengono analizzate solo per applicare lo stato a 8 nel caso non
     * vi sia la corrispondente occorrenza di ditg
     * il tipo OFFERTE_ECO_LOTTI_DISTINTI indica il caso in cui la funzione va lanciata da un lotto di gara
     * ad offerta unica, lotti distinti, per cui le informazioni sulle ditte vanno prese a livello di lotto e non di gara.
     * E' stata ampliata la gestione per includere gli stati 13 e 20, in particolare lo stato 13 ha la stessa gestione dello stato 5,
     * mentre lo stato 20 ha la stessa gestione dello stato 8.
     *
     * @param ngara
     * @param tipo ("OFFERTE", "OFFERTE_ECO_LOTTI_DISTINTI", "DOC_AMM", "DOMANDE")
     * @param statoDaImpostare (8 o 5)
     *
     * @throws SQLException
     */
    private void impostaComunicazioniA(String ngara, String tipo, int statoDaImpostare) throws SQLException{

     String codgar1 = (String) this.sqlManager.getObject("select codgar1 from gare where ngara = ?", new Object[] {ngara});
     Long bustalotti = (Long)this.sqlManager.getObject("select bustalotti from gare where ngara=?", new Object[]{codgar1});
     boolean fs10a = false;

     //Si segue la stesso algoritmo usato in aperturaDocumentazioneProcedureTelematiche, quindi di controlla se vi sono comunicazioni attive
     //per un determinato tipo, e per ognuna si controlla se la ditta è ammessa alla gara(considerando tutte le casistiche, RT, RTOFFERTA), se
     //non lo è si aggiorna lo stato della comunicazione a 8

     int statoIniziale=5;
     if(statoDaImpostare==5)
       statoIniziale=8;
     else if(statoDaImpostare==13)
       statoIniziale=20;
     else if(statoDaImpostare==20)
       statoIniziale=13;
     Object parametriW_invcom[] = new Object[1];
     //Nel caso di bustalotti=1, la busta FS11A è associata alla gara, mentre le buste FS11B e FS11C sono associate ai lotti.
     //Nel caso di chiamata della funziona dall'attivazione dell'apertura/chiusura della documentazione amministrativa, ngara è
     //quello della gara, quindi si deve tenere conto che invece FS11B e FS11C sono associati ai lotti
     String selecDatitW_INVCOM = "select idcom, comkey1, comtipo, comkey3 from w_invcom where comkey2 = ? and comstato = " + Integer.toString(statoIniziale) + " and comtipo ";
     if((new Long(1)).equals(bustalotti) && "DOC_AMM".equals(tipo)){
       selecDatitW_INVCOM = "select idcom, comkey1, comtipo, comkey3 from w_invcom where comkey2 like ? and comstato = " + Integer.toString(statoIniziale) + " and comtipo ";
       parametriW_invcom[0] = ngara + "%";
     }else
       parametriW_invcom[0] = ngara ;

     if("OFFERTE".equals(tipo) || "OFFERTE_ECO_LOTTI_DISTINTI".equals(tipo) || "DOC_AMM".equals(tipo)){
       selecDatitW_INVCOM += "in ('FS11A','FS11B','FS11C')";
     }else{
       selecDatitW_INVCOM += "= 'FS10A'";
       fs10a = true;
     }

     String selectAmmgar = "select ammgar from ditg where codgar5 = ? and ngara5 = ? and dittao = ? ";

     List<?> datiW_INVCOM = this.sqlManager.getListVector(selecDatitW_INVCOM, parametriW_invcom);
     if (datiW_INVCOM != null && datiW_INVCOM.size() > 0) {
       boolean esisteDITG = false;
       String comtipo=null;
       String selectConteggio = MEPAManager.selectDITG;
       if(fs10a)
         selectConteggio = MEPAManager.selectDITG_FS10A;

       String comkey3 = null;

       for (int i = 0; i < datiW_INVCOM.size(); i++) {
         esisteDITG = false;
         Long w_invcom_idcom = (Long) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 0).getValue();
         String w_invcom_comkey1 = (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 1).getValue();
         comtipo = (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 2).getValue();
         String userkey1 = (String) this.sqlManager.getObject(selectW_PUSER, new Object[] { w_invcom_comkey1 });
         comkey3 = (String) SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 3).getValue();

         //Nel caso di bustalotti=1 si devono prendere i dati dall'occorrenza complementare di gare, tranne nel caso
         // di tipo= OFFERTE_ECO_LOTTI_DISTINTI
         Object par[] = new Object[4];
         par[0] = codgar1;
         if((new Long(1)).equals(bustalotti) && !"OFFERTE_ECO_LOTTI_DISTINTI".equals(tipo))
           par[1] = codgar1;
         else
           par[1] = ngara;
         par[2] = userkey1;
         par[3] = comkey3;

         if (userkey1 != null) {
           Long conteggio = (Long) this.sqlManager.getObject(selectConteggio,par);
           if (conteggio != null && conteggio.longValue() > 0){
             esisteDITG = true;
             //Se l'imprea ha presentato offerta in RT, allora la ditta risulta essere in gara, ma è stata
             //esclusa, per inserire un RT. Allora in questo caso si deve andare a prendere lo stato di ammissione
             //non della ditta, ma del RT ottenuto dalla ditta.
             List<?> listaCodiciRaggruppamento= this.sqlManager.getListVector(selectRaggruppamentoDITG,par);
             if(listaCodiciRaggruppamento!=null && listaCodiciRaggruppamento.size()==1){
               par[2] = SqlManager.getValueFromVectorParam(listaCodiciRaggruppamento.get(0), 0).getValue();
             }
           }else{
             //Se l'impresa partecipa come mandataria di un RT allora si deve estrarre il codice della RTI
             //Se l'impresa è presente come mandataria in più RT allora è un errore
             List<?> listaCodiciRaggruppamento= this.sqlManager.getListVector(selectRaggruppamentoDITG,par);
             if(listaCodiciRaggruppamento!=null && listaCodiciRaggruppamento.size()==1){
               par[2] = SqlManager.getValueFromVectorParam(listaCodiciRaggruppamento.get(0), 0).getValue();
               esisteDITG = true;
             }
           }
         }

         if (esisteDITG && !"FS11A".equals(comtipo)) {
           Object par1[] = new Object[3];
           par1[0] = par[0];
           par1[1] = par[1];
           par1[2] = par[2];

           String ammgar =(String)this.sqlManager.getObject(selectAmmgar, par1);
           if(ammgar==null)
             ammgar="";
           if(("2".equals(ammgar) && (statoDaImpostare == 8 || statoDaImpostare == 20)) || (("1".equals(ammgar) || "".equals(ammgar) ) && (statoDaImpostare == 5 || statoDaImpostare == 13))){
             this.sqlManager.update("update W_INVCOM set COMSTATO = ?, COMDATASTATO = ? where IDPRG=? and IDCOM=?", new Object[] { Integer.toString(statoDaImpostare),
                 new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), "PA", w_invcom_idcom });
           }
         }else if(!esisteDITG && (statoDaImpostare==8 || statoDaImpostare==20)){
           //Se l'imprese non partecipa alla gara allora si deve impostare lo stato della comunicazione ad 8
           this.sqlManager.update("update W_INVCOM set COMSTATO = ?, COMDATASTATO = ? where IDPRG=? and IDCOM=?", new Object[] { Integer.toString(statoDaImpostare),
               new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), "PA", w_invcom_idcom });
         }

       }
     }

    }


	/**
     * Viene controllato se la data odierna è superiore alla data + ora termine presentazione offerte( o apertura plichi)
     *
     * @param ngara
     * @return String[], in posizione 0 c'è l'esito del controllo
     *                                1 il valore del campo data
     *                                2 il valore del campo ora
     *
     * @throws SQLException
     */
	public String[] controlloDataConDataAttuale(String gara, String campoData, String campoOra) throws SQLException{

	  String isSuperataData = "false";
	  String dataRet="";
	  String oraRet="";
	  String ret[] = {"","",""};

	  if (gara != null) {

	      String selectTORN = "select torn." + campoData + ", torn." + campoOra + " from torn, gare where torn.codgar = gare.codgar1 and gare.ngara = ?";
	      List<?> datiTORN = sqlManager.getVector(selectTORN, new Object[] { gara });
	      if (datiTORN == null || datiTORN.size() == 0) {
	        //Se la funzione è lanciata da un gara a lotti plichi distinti, viene passato il codgar al suo posto
	        selectTORN = "select torn." + campoData + ", torn." + campoOra + " from torn where torn.codgar = ?";
	        datiTORN = sqlManager.getVector(selectTORN, new Object[] { gara });
	      }
	      if (datiTORN != null && datiTORN.size() > 0) {
	        Date data = (Date) SqlManager.getValueFromVectorParam(datiTORN, 0).getValue();
	        String ora = (String) SqlManager.getValueFromVectorParam(datiTORN, 1).getValue();
	        if (ora == null) {
	          ora = "00:00";
            }

	        if (data != null ) {
	          Date dataOdierna = new Date();

	          Calendar calendar = Calendar.getInstance();
	          calendar.setTime(data);

	          String hrs = ora.substring(0, 2);
	          String min = ora.substring(3);
	          Long lhrs = new Long(hrs);
	          Long lmin = new Long(min);

	          GregorianCalendar dataTmp = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),  calendar.get(Calendar.DAY_OF_MONTH), lhrs.intValue(), lmin.intValue());
	          Date dataTerminePresentazioneOfferte = new Date(dataTmp.getTimeInMillis());

	          if (dataTerminePresentazioneOfferte != null) {
	            if (dataOdierna.after(dataTerminePresentazioneOfferte)) {
	              isSuperataData = "true";
	            }
	          }

	          dataRet = UtilityDate.convertiData(data, UtilityDate.FORMATO_GG_MM_AAAA);
	          oraRet= ora;

	        }else
	          isSuperataData = "true";
	      }


	  }
	  ret[0] = isSuperataData;
	  if("false".equals(isSuperataData)){
	    ret[1] = dataRet;
	    ret[2] = oraRet;
	  }
	  return ret;

	}

	/**
     * Viene controllato che FASGAR.GARE coincida con quello atteso
     *
     * @param ngara
     * @param faseGaraAspettata
     * @return boolean
     *
     * @throws SQLException
     */
	public boolean esisteBloccoCondizioniFasiAcquisizioni(String ngara, Long faseGaraAspettata) throws SQLException{

	  boolean ret= false;
	  String select = "select fasgar from gare where ngara=?";
	  Long fasgar = (Long)sqlManager.getObject(select, new Object[]{ngara});
	  if(!faseGaraAspettata.equals(fasgar))
	    ret= true;

	  return ret;
	}

	/**
     * Viene controllato che per la gara esistano ditte che hanno presentato l'offerta
     *
     * @param ngara
     * @return boolean
     *
     * @throws SQLException
     */
	public boolean esistonoDittePresentataOfferta(String ngara) throws SQLException{
	  boolean esistonoDittePresentataOfferta = false;
	  if (ngara != null) {
	    String selectDITG = "select count(*) from ditg where ngara5 = ? and (invoff <> '2' or invoff is null) and (fasgar > -2 or fasgar is null)";
	    Long conteggio = (Long) sqlManager.getObject(selectDITG, new Object[] {ngara});

	    if (conteggio != null && conteggio.longValue() > 0) {
	         esistonoDittePresentataOfferta = true;
	    }
	  }
	  return esistonoDittePresentataOfferta;
	}

	/**
     * Viene estratto il document dei messaggio FS11 e FS10
     *
     * @param selectW_DOCDIG
     * @param w_invcom_idprg
     * @param w_invcom_idcom
     * @return TipoPartecipazioneDocument
     *
     * @throws SQLException, IOException, XmlException
     */
	public TipoPartecipazioneDocument estrazioneDocumentMessaggioFS10_FS11(String selectW_DOCDIG, String w_invcom_idprg, String w_invcom_idcom)
	    throws SQLException, IOException, XmlException {
	  TipoPartecipazioneDocument document = null;
	  List<?> datiW_DOCDIG = this.sqlManager.getVector(selectW_DOCDIG, new Object[] { "W_INVCOM", w_invcom_idprg, w_invcom_idcom.toString() });
	  if (datiW_DOCDIG != null && datiW_DOCDIG.size() > 0) {
	    String w_docdig_idprg = (String) SqlManager.getValueFromVectorParam(datiW_DOCDIG, 0).getValue();
	    Long w_docdig_iddocdig = (Long) SqlManager.getValueFromVectorParam(datiW_DOCDIG, 1).getValue();
	    BlobFile w_docdig_digogg = null;
	    w_docdig_digogg = fileAllegatoManager.getFileAllegato(w_docdig_idprg, w_docdig_iddocdig);
	    if (w_docdig_digogg != null && w_docdig_digogg.getStream() != null) {
	      String xml = new String(w_docdig_digogg.getStream());
	      document = TipoPartecipazioneDocument.Factory.parse(xml);
	    }
	  }
      return document;
	}


	/**
     * Gestione della partecipazione della ditta ai lotti per una gara ad offerta unica, in base all'informazione prelevata dai messaggi
     * inviati da portale
     *
     * @param ngara
     * @param ditta
     * @param containerDITG
     * @param gestoreDITG
     * @param vettorePartecipazioneLotti,
     * @param faseGaraLong
     * @param status
     * @param modo
     * @param tipoMessaggio
     * @param iterga
     *
     * @throws SQLException, GestoreException
     */
	public void gestioneLottiOffertaUnica(String ngara, String ditta, DataColumnContainer containerDITG, GestoreDITG gestoreDITG,
	      String vettorePartecipazioneLotti[], Long faseGaraLong, TransactionStatus status, String modo, boolean aggiornamentoInvgar, String tipoMessaggio,
	      Long iterga)
	      throws SQLException, GestoreException {
	    // Se la gara è ad offerta unica si devono inserire le ditte per ogni lotto
	    Vector<?> datiGare = this.sqlManager.getVector("select genere,bustalotti from gare where ngara=?", new Object[] { ngara });
	    if(datiGare!=null && datiGare.size()>0){
	      Long genere = SqlManager.getValueFromVectorParam(datiGare, 0).longValue();
	      Long bustalotti = SqlManager.getValueFromVectorParam(datiGare, 1).longValue();
	      if (genere != null && genere.longValue() == 3) {
	        List<?> listaLotti = this.sqlManager.getListVector("select NGARA, IMPAPP from GARE "
	            + "where CODGAR1 = ? and NGARA <> CODGAR1 and GENERE is null", new Object[] { ngara });
	        if (listaLotti != null && listaLotti.size() > 0) {
	          if("INS".equals(modo)){
	            Long offtel=(Long)this.sqlManager.getObject("select offtel from torn where codgar=?", new Object[]{ngara});
	            String campiRimuovere[] = null;
	            if("FS11".equals(tipoMessaggio)){
	              campiRimuovere = new String[]{"DITG.DATOFF", "DITG.ORAOFF", "DITG.MEZOFF"};
	              if((new Long(1).equals(iterga) || !new Long(1).equals(offtel)) && containerDITG.isColumn("DITG.INVOFF"))
	                containerDITG.setValue("DITG.INVOFF", null);
	            }else
	              campiRimuovere = new String[]{"DITG.DATOFF", "DITG.ORAOFF", "DITG.MEZOFF", "DITG.INVOFF"};
	            containerDITG.removeColumns(campiRimuovere);
	            containerDITG.addColumn("DITG.IMPAPPD", JdbcParametro.TIPO_NUMERICO);
	          }
	          boolean eseguireUpdate=true;
	          for (int j = 0; j < listaLotti.size(); j++) {
	            eseguireUpdate =true;
	            String partgar = null;
	            String invoff=null;
	            Vector<?> lotto = (Vector<?>) listaLotti.get(j);
	            String tmpCodiceLotto = (String) ((JdbcParametro) lotto.get(0)).getValue();
	            Double tmpImpApp = (Double) ((JdbcParametro) lotto.get(1)).getValue();
	            if("INS".equals(modo)){
	              containerDITG.setValue("DITG.NGARA5", tmpCodiceLotto);
	              containerDITG.setValue("DITG.IMPAPPD", tmpImpApp);
	            }
	            if((new Long(1)).equals(bustalotti) && vettorePartecipazioneLotti!=null){
	              //Gestione inizializzazione partgar
	              partgar="2";
	              invoff="2";

	              for(int i=0;i<vettorePartecipazioneLotti.length;i++){
	                if(vettorePartecipazioneLotti[i].equals(tmpCodiceLotto)){
	                  partgar="1";
	                  break;
	                }
	              }
	              if("FS11".equals(tipoMessaggio) && "1".equals(partgar)  && (new Long(2).equals(iterga) || new Long(4).equals(iterga))){
	                //Con la nuova gestione delle offerte si può avere il caso di creare una nuova RT in questa fase, senza esclusione della mandataria
	                //per cui ci può essere il caso di non avere il lotto per la ditta, in questo caso la gestione che segue non si deve applicare
	                Long numLotto = (Long)this.sqlManager.getObject("select count(ngara5) from ditg where ngara5=? and dittao=?", new Object[]{tmpCodiceLotto,ditta});
	                if(numLotto!=null && numLotto.longValue() > 0){
  	                  String partgarDb = (String)this.sqlManager.getObject("select partgar from ditg where ngara5=? and dittao=?", new Object[]{tmpCodiceLotto,ditta});
    	              if("1".equals(partgarDb))
    	                invoff="1";
	                }else
	                  invoff="1";

	              }else if("FS11".equals(tipoMessaggio) && "1".equals(partgar))
                    invoff="1";

	              if("INS".equals(modo)){
	                if(("2".equals(partgar) && "FS10".equals(tipoMessaggio)) || ("2".equals(invoff) && "FS11".equals(tipoMessaggio)) ){
	                  containerDITG.addColumn("DITG.AMMGAR", JdbcParametro.TIPO_TESTO,"2");
	                  containerDITG.addColumn("DITG.FASGAR", JdbcParametro.TIPO_NUMERICO,faseGaraLong);
	                }else{
	                  if(containerDITG.isColumn("DITG.AMMGAR"))
	                    containerDITG.removeColumns(new String[] { "DITG.DATOFF", "DITG.AMMGAR", "DITG.FASGAR" });
	                }
	                if("FS11".equals(tipoMessaggio)){
	                  if(containerDITG.isColumn("DITG.INVOFF"))
	                    containerDITG.setValue("DITG.INVOFF", invoff);
	                  else
	                    containerDITG.addColumn("DITG.INVOFF", JdbcParametro.TIPO_TESTO,invoff);

	                }
	                if(!("FS11".equals(tipoMessaggio) && (new Long(2).equals(iterga) || new Long(4).equals(iterga)) ))
	                  containerDITG.addColumn("DITG.PARTGAR", JdbcParametro.TIPO_NUMERICO,partgar);
	              }
	            }

	            if("INS".equals(modo)){
	              gestoreDITG.inserisci(status, containerDITG);
	              //Nel gestoreDITG viene impostato in inserimento INVAGR='1', però se partgar='2' c'è
	              //necessità di impostare invgar='2', quindi devo fare l'update
	              if(aggiornamentoInvgar && "2".equals(partgar)){
	                this.sqlManager.update("update DITG set INVGAR = null where CODGAR5=? and NGARA5=? and DITTAO=?",
	                    new Object[]{ngara, tmpCodiceLotto,ditta});
	              }

	              if("2".equals(invoff) && "FS11".equals(tipoMessaggio)){
                    gestoreDITG.gestioneDITGAMMIS(ngara, tmpCodiceLotto, ditta, new Long(1), status);
                  }
	            }else if ("AGG".equals(modo) && ((new Long(1)).equals(bustalotti))){
	              String updateDitg= "update DITG set PARTGAR = '" + partgar +"' ";
	              String whereDitg = "where CODGAR5=? and NGARA5=? and DITTAO=?";
	              Object par[] = new Object[]{ngara, tmpCodiceLotto,ditta};
	              if("FS11".equals(tipoMessaggio)){
	                if(new Long(2).equals(iterga) || new Long(4).equals(iterga))
	                  updateDitg= "update DITG set INVOFF = '" + invoff + "' ";
	                else
	                  updateDitg+= ", INVOFF = '" + invoff + "' ";
	              }

	              if(("2".equals(partgar) && "FS10".equals(tipoMessaggio)) || ("2".equals(invoff) && "FS11".equals(tipoMessaggio)) ){
	                String fase = null;
	                if(faseGaraLong!=null)
	                  fase = faseGaraLong.toString();
	                updateDitg += ", AMMGAR='2', FASGAR = " + fase + " ";
	                if(aggiornamentoInvgar)
	                  updateDitg += ", INVGAR = null ";
	              }
	              updateDitg += whereDitg;
	              //Se la ditta è già stata esclusa nelle fasi precedenti, nel caso di FS11 non si deve procedere con l'aggiornamento
	              if("2".equals(invoff) && "FS11".equals(tipoMessaggio)){
	                Long faseLotto = (Long)this.sqlManager.getObject("select fasgar from ditg where ngara5=? and dittao=?", new Object[]{tmpCodiceLotto,ditta});
	                if(faseLotto!=null && faseLotto.longValue()<1)
	                  eseguireUpdate = false;
	              }
	              if(eseguireUpdate){
	                this.sqlManager.update(updateDitg, par);
	                if("2".equals(invoff) && "FS11".equals(tipoMessaggio)){
	                  gestoreDITG.gestioneDITGAMMIS(ngara, tmpCodiceLotto, ditta, new Long(1), status);
	                }
	              }
	            }

	          }
	        }
	      }
	    }
	  }

	/**
     * Viene riportato per i lotti lo stato di partecipazione e ammissione corrispondente della mandataria
     *
     * @param ngara
     * @param ditta
     * @param containerDITG
     * @param gestoreDITG
     * @param status
     *
     * @throws SQLException, GestoreException
     */
    public void gestioneLottiOffertaUnicaDaMandataria(String ngara, String ditta, DataColumnContainer containerDITG, GestoreDITG gestoreDITG,
          TransactionStatus status)
          throws SQLException, GestoreException {
        // Se la gara è ad offerta unica si devono inserire le ditte per ogni lotto
        Long genere = (Long)this.sqlManager.getObject("select genere from gare where ngara=?", new Object[] { ngara });

        if (genere != null && genere.longValue() == 3) {
          List listaLotti = this.sqlManager.getListVector("select NGARA, IMPAPP from GARE "
              + "where CODGAR1 = ? and NGARA <> CODGAR1 and GENERE is null", new Object[] { ngara });
          if (listaLotti != null && listaLotti.size() > 0) {
            containerDITG.removeColumns(new String[] { "DITG.DATOFF", "DITG.ORAOFF", "DITG.MEZOFF", "DITG.INVOFF" });
            containerDITG.addColumn("DITG.IMPAPPD", JdbcParametro.TIPO_NUMERICO);

            String ammgar = null;
            String partgar = null;
            Long fasgar = null;
            String invgar = null;

            for (int j = 0; j < listaLotti.size(); j++) {
              Vector lotto = (Vector) listaLotti.get(j);
              String tmpCodiceLotto = (String) ((JdbcParametro) lotto.get(0)).getValue();
              Double tmpImpApp = (Double) ((JdbcParametro) lotto.get(1)).getValue();

              containerDITG.setValue("DITG.NGARA5", tmpCodiceLotto);
              containerDITG.setValue("DITG.IMPAPPD", tmpImpApp);
              Vector datiDitta = this.sqlManager.getVector("select partgar, ammgar, fasgar, invgar from ditg where ngara5=? and codgar5=? and dittao=?",
                  new Object[]{tmpCodiceLotto, ngara, ditta});
              invgar = null;
              if(datiDitta!=null && datiDitta.size()>0){
                partgar = SqlManager.getValueFromVectorParam(datiDitta, 0).stringValue();
                ammgar = SqlManager.getValueFromVectorParam(datiDitta, 1).stringValue();
                fasgar = SqlManager.getValueFromVectorParam(datiDitta, 2).longValue();
                invgar = SqlManager.getValueFromVectorParam(datiDitta, 3).stringValue();
                containerDITG.addColumn("DITG.PARTGAR", partgar);
                containerDITG.addColumn("DITG.AMMGAR", ammgar);
                containerDITG.addColumn("DITG.FASGAR", fasgar);
              }
              gestoreDITG.inserisci(status, containerDITG);
              //Nel gestore DITG all'inserimento viene impostato INVGAR=1, ma si deve riportare in realtà il valore
              //letto dal db
              this.sqlManager.update("update DITG set INVGAR = ? where CODGAR5=? and NGARA5=? and DITTAO=?",
                    new Object[]{invgar,ngara, tmpCodiceLotto,containerDITG.getString("DITG.DITTAO")});
            }
          }
        }
      }

    /**
     * Viene controllato se per una ditta di una gara è stata eseguita l'acquisizione, ed eventualmente
     * ne ritorna lo stato
     *
     * @param ngara
     * @param ditta
     * @param busta
     * @return String che assume i seguenti valori: Si
     *                                              No,
     *                                              NonEsiste
     *                                              false
     *
     * @throws SQLException
     */
    public String IsBustaElaborata(String ngara, String ditta, String busta) throws SQLException{
      String IsBustaElaborata = "false";
      if (ngara != null) {

            String comstato = this.GetStatoBusta(ngara, ditta, busta);

            if (comstato == null || "".equals(comstato))
              IsBustaElaborata = "NonEsiste";
            else if("5".equals(comstato) || "7".equals(comstato) || "13".equals(comstato) || "17".equals(comstato))
              IsBustaElaborata = "No";
            else
              IsBustaElaborata = "Si";
      }
      return IsBustaElaborata;
    }

    /**
     * Viene ritornato lo stato di una busta per una ditta
     *
     * @param ngara
     * @param ditta
     * @param busta
     * @return String
     *
     * @throws SQLException
     */
    public String GetStatoBusta(String ngara, String ditta, String busta) throws SQLException{
      String stato = null;
      if (ngara != null) {
        String selectbuste = "select comstato from ditg "
            + "left outer join ragimp on (codime9=dittao and impman='1') "
            + "inner join w_puser on (userkey1=dittao or (userkey1=coddic and impman='1')) "
            + "inner join w_invcom on (comkey1=usernome and comkey2=ngara5 and (comkey3 = ncomope or comkey3 is null) and comtipo=?) "
            + "where ngara5=? and dittao=?";

            stato = (String) sqlManager.getObject(selectbuste, new Object[] {busta, ngara, ditta});

      }
      return stato;
    }


    /**
     * Si controlla che per ogni occorrenza in GOEV vi sia il corrispondente valore in DPUN
     * @param ngara
     * @param ditta
     * @param tipoCriterio
     * @return
     * @throws SQLException
     */
    public boolean controlloCriteriTuttiValorizzati(String ngara, String ditta, Long tipoCriterio) throws SQLException{
      boolean criteriTuttiValorizzati=true;
      //Si deve controllare che se è presente una occorrenza in DPUN, allora ve ne deve essere una per ogni occorrenza di GOEV
      Long numeroOccorrenzeGoev = (Long)sqlManager.getObject("select count(ngara) from goev g where ngara=? and tippar=?", new Object[]{ngara, tipoCriterio});
      Long numOccorrenzeDpun = (Long)sqlManager.getObject("select count(d.ngara) from goev g, dpun d where g.ngara=d.ngara and " +
          " g.necvan=d.necvan and g.ngara=? and d.dittao=? and tippar=?",
          new Object[]{ngara, ditta, tipoCriterio});
      if(numOccorrenzeDpun.longValue() > 0 && numeroOccorrenzeGoev.longValue() > numOccorrenzeDpun.longValue())
        criteriTuttiValorizzati=false;
      return criteriTuttiValorizzati;
    }


    /**
     * Viene controllato che i punteggi totali (o tecnici o economici) delle ditte siano superiori alla soglia minima (tecnica o economica)
     * @param ngara
     * @param sogliaMinima
     * @param tipoPunteggi
     * @param elencoDitte
     * @return  boolean
     * @throws SQLException
     * @throws GestoreException
     */
    public boolean esitoControlloPunteggiTotaliDitteSogliaMinima(String ngara, Double sogliaMinima, Long tipoPunteggi, ArrayList<String> elencoDitte) throws SQLException, GestoreException{
     boolean esito= true;
     String selectRiparam = "select ### from gare1 where ngara=?";
     String selectDitte = "select ###,dittao from DITG where ngara5=? and (fasgar is null or fasgar > ?)";
     Long fase = null;

     if(tipoPunteggi.longValue()==1){
       selectRiparam=selectRiparam.replace("###", "riptec");
       fase = new Long(5);
     }else{
       selectRiparam=selectRiparam.replace("###", "ripeco");
       fase = new Long(6);
     }

     Long riparam=(Long)this.sqlManager.getObject(selectRiparam, new Object[]{ngara});
     /*
     Vector datiGare1 = this.sqlManager.getVector(selectRiparam, new Object[]{ngara});
     if(datiGare1!=null && datiGare1.size()>0){
       riparam = SqlManager.getValueFromVectorParam(datiGare1, 0).longValue();
       riparamCrit = SqlManager.getValueFromVectorParam(datiGare1, 1).longValue();
     }
     */

     if(riparam==null || new Long(1).equals(riparam) || new Long(3).equals(riparam)){
       if(tipoPunteggi.longValue()==1)
         selectDitte = selectDitte.replace("###", "puntec");
       else
         selectDitte = selectDitte.replace("###", "puneco");
     }else if(new Long(2).equals(riparam)){
       if(tipoPunteggi.longValue()==1)
         selectDitte = selectDitte.replace("###", "puntecrip");
       else
         selectDitte = selectDitte.replace("###", "punecorip");
     }

     List<?> listaDitte = sqlManager.getListVector(selectDitte, new Object[] { ngara,fase });

     if (listaDitte != null && listaDitte.size() > 0) {
       Double punteggioDitta = null;
       String ditta = null;
       for (int i = 0; i < listaDitte.size(); i++) {
         punteggioDitta = SqlManager.getValueFromVectorParam(listaDitte.get(i), 0).doubleValue();
         ditta = SqlManager.getValueFromVectorParam(listaDitte.get(i), 1).getStringValue();
         if (punteggioDitta != null && sogliaMinima != null && punteggioDitta.doubleValue()<sogliaMinima.doubleValue()) {
           esito = false;
           if(elencoDitte!=null){
             elencoDitte.add(ditta);
           }
         }
       }
     }

     return esito;
    }

    /**
     * Viene controllato che i punteggi (o tecnici o economici) dei criteri delle ditte siano superiori alla soglia minima (tecnica o economica)
     * L'esito di tale controllo viene impostato nel primo elemento restituito dal metodo. Nel secondo elemento del vettore restituito viene
     * impostata l'informazione sulla presenza o meno della soglia minima
     * Nel caso in cui GARE1.RIPTEC(RIPECO)=2 e GARE1.RIPCRITEC(RIPCRIECO)=2 non si effettua il controllo.
     * @param ngara
     * @param tipoPunteggi
     * @param elencoDitte
     * @return boolean[]
     * @throws SQLException
     * @throws GestoreException
     */
    public boolean[] esitoControlloPunteggiCriteriDitteSogliaMinima(String ngara, Long tipoPunteggi, ArrayList elencoDitte) throws SQLException, GestoreException{
      boolean controlloPunteggioMinimoSuperato= true;
      boolean sogliaMinimaCriteriImpostata=false;
      String selectRiparam = "select ###,*** from gare1 where ngara=?";
      String selectDitte = "select dittao from DITG where ngara5=? and (fasgar is null or fasgar > ?)";
      String selectPunteggiCriteri = "select d.###, g.minpun from goev g, dpun d where g.ngara=d.ngara and g.necvan=d.necvan and g.ngara=? and d.dittao=? "
          + " and g.tippar=?";
      Long riparam=null;
      Long riparamCrit=null;
      Long fase=null;

      if(tipoPunteggi.longValue()==1){
        selectRiparam=selectRiparam.replace("###", "riptec");
        selectRiparam=selectRiparam.replace("***", "ripcritec");
        fase = new Long(5);
      }else{
        selectRiparam=selectRiparam.replace("###", "ripeco");
        selectRiparam=selectRiparam.replace("***", "ripcrieco");
        fase = new Long(6);
      }

      Vector<?> datiGare1 = this.sqlManager.getVector(selectRiparam, new Object[]{ngara});
      if(datiGare1!=null && datiGare1.size()>0){
        riparam = SqlManager.getValueFromVectorParam(datiGare1, 0).longValue();
        riparamCrit = SqlManager.getValueFromVectorParam(datiGare1, 1).longValue();
      }

      if(riparam==null || new Long(1).equals(riparam) || new Long(3).equals(riparam)){
        selectPunteggiCriteri = selectPunteggiCriteri.replace("###", "punteg");
      }else if(new Long(2).equals(riparam)){
        if(new Long(2).equals(riparamCrit) || new Long(3).equals(riparamCrit))
          selectPunteggiCriteri = selectPunteggiCriteri.replace("###", "puntegrip");
        else
          selectPunteggiCriteri = selectPunteggiCriteri.replace("###", "punteg");
      }

      //Nel caso in cui GARE1.RIPTEC(RIPECO)=2 e GARE1.RIPCRITEC(RIPCRIECO)=2 non si effettua il controllo.
      if(!(new Long(2).equals(riparam) && new Long(1).equals(riparamCrit))){
        List<?> listaDitte = sqlManager.getListVector(selectDitte, new Object[] { ngara,fase });
        if (listaDitte != null && listaDitte.size() > 0) {
          String ditta = null;
          for (int i = 0; i < listaDitte.size(); i++) {
            ditta = SqlManager.getValueFromVectorParam(listaDitte.get(i), 0).getStringValue();

            List<?> listaPunteggi= sqlManager.getListVector(selectPunteggiCriteri, new Object[] { ngara,ditta, tipoPunteggi});

            if (listaPunteggi != null && listaPunteggi.size() > 0) {
              Double punteggioMinimo = null;
              Double punteggio = null;

              for (int j = 0; j < listaPunteggi.size(); j++) {
                punteggio = SqlManager.getValueFromVectorParam(listaPunteggi.get(j), 0).doubleValue();
                punteggioMinimo = SqlManager.getValueFromVectorParam(listaPunteggi.get(j), 1).doubleValue();
                if(punteggioMinimo!=null)
                  sogliaMinimaCriteriImpostata=true;
                if (punteggioMinimo != null && punteggio != null && punteggioMinimo.doubleValue() > punteggio.doubleValue()) {
                  controlloPunteggioMinimoSuperato = false;
                  if(elencoDitte!=null){
                    if(!elencoDitte.contains(ditta))
                      elencoDitte.add(ditta);
                  }
                }

              }
            }
          }
        }
      }
      boolean ret[]={controlloPunteggioMinimoSuperato,sogliaMinimaCriteriImpostata};
      return ret;
    }

    private boolean inserimentoOfferteTecnicheDaXML(String ngara,String codgar, String ditta, ListaCriteriValutazioneType listaCriteriValutazione,Cipher decoder, Integer tippar, String sezioneTec) throws SQLException, GestoreException, GeneralSecurityException, ParseException{

      //despar darà da sostiturire con idcridef
      ArrayList<Long> listaPunteggi = new ArrayList<Long>();
      String select = "select g1cridef.id from g1cridef, goev  where g1cridef.ngara = ? and g1cridef.ngara = goev.ngara and g1cridef.necvan = goev.necvan and goev.tippar = ? and g1cridef.formato != 100";
      //Nel caso di busta tecnica con attiva la gestione delle sezioni, si devono filtrare i criteri da acquisire con quelli della sezione in elaborazione
      if ("1".equals(sezioneTec)) {
        select += " and goev.seztec=1";
      } else if ("2".equals(sezioneTec)) {
        select += " and goev.seztec=2";
      }
      List<?> listaPunteggiTemp= sqlManager.getListVector(select, new Object[] { ngara,tippar });
      for(int i = 0; i<listaPunteggiTemp.size(); i++){
        Long idCriterio = (Long) SqlManager.getValueFromVectorParam(listaPunteggiTemp.get(i), 0).getValue();
        listaPunteggi.add(idCriterio);
      }
      if (listaPunteggi == null || listaPunteggi.size() ==  0) {
        return true;
      }else{

        ArrayList<Long> listaPunteggiFiltro = new ArrayList<Long>(listaPunteggi);

        //controllo se ci sono offerte relative  a tutti i criteri
        for(int i = 0; i< listaCriteriValutazione.sizeOfCriterioValutazioneArray(); i++){
          AttributoGenericoType attr = listaCriteriValutazione.getCriterioValutazioneArray(i);
          String idCriterio = attr.getCodice();
          Long idCridef = Long.parseLong(idCriterio);
          listaPunteggi.remove(idCridef);
        }
        if(listaPunteggi.size() > 0){
          logger.error("Errore nell'acquisizione dei valori offerti per i criteri di valutazione della gara: alcuni criteri non sono presenti nell'offerta presentata");
          return false;
        }

        boolean criterioDaAcquisire = true;
        //parsing dell'xml
        for(int i = 0; i< listaCriteriValutazione.sizeOfCriterioValutazioneArray(); i++){
          AttributoGenericoType attr = listaCriteriValutazione.getCriterioValutazioneArray(i);
          int type = attr.getTipo();

          String valoreStg = null;
          Double valoreNum = null;
          Calendar valoreDat = null;
          criterioDaAcquisire = true;

          //nel caso di busta tecnica si devono considerare solo i criteri relativi alle sezioni in elaborazione
          String idCriterio = attr.getCodice();
          Long idCridef = Long.parseLong(idCriterio);

          //Nel caso di busta tecnica con attiva la gestione delle sezioni, si devono filtrare i criteri da acquisire con quelli della sezione in elaborazione
          if (sezioneTec!=null && !"".equals(sezioneTec)) {
           if (listaPunteggiFiltro.indexOf(idCridef) == -1) {
             criterioDaAcquisire = false;
           }
          }

          if (criterioDaAcquisire) {
            Long necvan = (Long) sqlManager.getObject("select g1cridef.necvan from g1cridef where g1cridef.id = ? ",new Object[] { idCridef });

            if(attr.isSetValoreCifrato()){
              byte[] cifrato = attr.getValoreCifrato();
              byte[] datoDecifrato = SymmetricEncryptionUtils.translate(decoder, cifrato);

              if(type == 3 || type == 4){
                valoreStg = new String(datoDecifrato);
              }
              if(type == 1){
                valoreStg = new String(datoDecifrato);
                valoreDat = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date date = sdf.parse(valoreStg);
                valoreDat.setTime(date);
              }
              if(type == 2 || type == 5 ||type == 6 ||type == 50 ||type == 51 ||type == 52){
                valoreNum = Double.valueOf(new String(datoDecifrato));
              }
            }else{
              valoreStg = attr.getValoreStringa();
              valoreDat = attr.getValoreData();
              valoreNum = attr.getValoreNumerico();
            }

            if(type == 3 || type == 4){
              if(valoreStg.length() >= 2000){valoreStg=valoreStg.substring(0,2000);}
              String insertG1Crival = "insert into G1CRIVAL(id,ngara,necvan,dittao,idcridef,valstg) values(?,?,?,?,?,?)";
              Long idCrival = (long) this.genChiaviManager.getNextId("G1CRIVAL");
              this.sqlManager.update(insertG1Crival, new Object[]{ idCrival,ngara,necvan,ditta,idCridef,valoreStg});
            }
            if(type == 1){
              String insertG1Crival = "insert into G1CRIVAL(id,ngara,necvan,dittao,idcridef,valdat) values(?,?,?,?,?,?)";
              Long idCrival = (long) this.genChiaviManager.getNextId("G1CRIVAL");
              this.sqlManager.update(insertG1Crival, new Object[]{ idCrival,ngara,necvan,ditta,idCridef,valoreDat.getTime()});
            }
            if(type == 2 || type == 5 ||type == 6 ||type == 50 ||type == 51 ||type == 52){
              String insertG1Crival = "insert into G1CRIVAL(id,ngara,necvan,dittao,idcridef,valnum) values(?,?,?,?,?,?)";
              Long idCrival = (long) this.genChiaviManager.getNextId("G1CRIVAL");
              this.sqlManager.update(insertG1Crival, new Object[]{ idCrival,ngara,necvan,ditta,idCridef,valoreNum});
              if(type == 50 || type == 52){
                String insertImpoff = "update ditg set impoff = ? where ngara5 = ? and codgar5 = ? and dittao = ?";
                this.sqlManager.update(insertImpoff, new Object[]{ valoreNum,ngara,codgar,ditta });
              }else if(type==51){
                valoreNum = (-1)*valoreNum;
                this.sqlManager.update("update ditg set riboepv = ? where ngara5 = ? and codgar5 = ? and dittao = ?", new Object[]{ valoreNum,ngara,codgar,ditta });
              }

            }
          }
        }
      }
        return true;
    }

    /**
     * Viene aggiornata la DITGEVENTI
     * @param ngara
     * @param codgar1
     * @param userkey1
     * @param nomeCampo
     * @throws SQLException
     */
    private void aggiornaDitgEventi(String ngara, String codgar1, String userkey1, String nomeCampo) throws SQLException{
      Date dataCorrente=new Date();
      Long numOccorrenze=(Long)this.sqlManager.getObject("select count(ngara) from DITGEVENTI where DITGEVENTI.NGARA=? and DITGEVENTI.DITTAO=? and DITGEVENTI.CODGAR=?", new Object[]{ngara,userkey1,codgar1});
      if(numOccorrenze!=null && numOccorrenze.longValue()>0){
        String sqlUpdate="update ditgeventi set " + nomeCampo + "=? where ngara=? and dittao=? and codgar=?";
        this.sqlManager.update(sqlUpdate, new Object[]{new Timestamp(dataCorrente.getTime()),ngara,userkey1,codgar1});
      }else{
        String sqlInsert="insert into ditgeventi(NGARA,DITTAO,CODGAR, " + nomeCampo + ") values(?,?,?,?)";
        this.sqlManager.update(sqlInsert, new Object[]{ngara,userkey1,codgar1, new Timestamp(dataCorrente.getTime())});
      }
    }
}