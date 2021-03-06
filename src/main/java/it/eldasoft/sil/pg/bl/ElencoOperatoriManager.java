/*
 * Created on 12/12/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import static it.eldasoft.sil.pg.bl.PgManager.NOME_FILE_RINNOVO_ISCRIZIONE;
import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.portgare.datatypes.RinnovoIscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.maggioli.eldasoft.ws.dm.WSDMInviaMailType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAnagraficaType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoInType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

import com.lowagie.text.DocumentException;

/**
 * Manager che raccoglie le funzionalitą per elenco operatori
 *
 * @author Cristian.Febas
 */
public class ElencoOperatoriManager {

  /** Logger */
  static Logger       logger = Logger.getLogger(ElencoOperatoriManager.class);

  /** Manager SQL per le operazioni su database */
  private SqlManager  sqlManager;

	private PgManager pgManager;

	/** Manager per l'interrogazione del campo blob della w_DOCDIG */
  private FileAllegatoManager fileAllegatoManager;


  /** Manager per la gestione dell'integrazione con WSDM */
  private GestioneWSDMManager gestioneWSDMManager = null;

  /**
   * Set SqlManager
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

	/**
   * Set PgManager
   *
   * @param pgManager
   */
  public void setPgManager(PgManager pgManager) {
    this.pgManager = pgManager;
  }

	/**
   * Set FileAllegatoManager
   *
   * @param fileAllegatoManager
   */
  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }


  /**
   * Invia la comunicazione via mail/pec degli operatori che sono appena stati abilitati
   * @param profilo (utente)
   * @param codiceGara (codice elenco)
   * @param listaOperatoriAbilitati
   * @param numeroOperatoriAbilitati
   * @param flagMailPec (flag che indica l'opzione usata (1 = mail;2= PEC))
   * @param oggettoMail
   * @param testoMail
   * @param mittenteMail
   * @param isOp114
   * @param integrazioneWSDM
   * @param datiWSDM
   * @return eventuale esito comunicazione
   * @throws GestoreException
   * @throws IOException
   * @throws DocumentException
   */


  public int inviaComunicazioneAbilitazione(ProfiloUtente profilo, String codiceGara, String listaOperatoriAbilitati,
      String numeroOperatoriAbilitati, String flagMailPec, String oggettoMail, String testoMail, String mittenteMail,
      String idcfg, boolean isOp114,  String integrazioneWSDM, HashMap<String,String> datiWSDM) throws SQLException, GestoreException, DocumentException, IOException {

      if(logger.isDebugEnabled())
      logger.debug("inviaComunicazioneAbilitazione: inizio metodo");

    listaOperatoriAbilitati = listaOperatoriAbilitati.replace("(", "");
    listaOperatoriAbilitati = listaOperatoriAbilitati.replace(")", "");
    listaOperatoriAbilitati = listaOperatoriAbilitati.replace("'", "");

    if (listaOperatoriAbilitati != null) {
      //String codice ="";
      //String tipologiaImpresa = "";
      //String codiceRaggruppamento = "";
      String[] ditteAbilitate = UtilityStringhe.deserializza(listaOperatoriAbilitati, ',');
      int numeroAbilitate = ditteAbilitate.length;
      Vector<Vector> ListaDestinatari = new Vector<Vector>();

      String tipodocumento =  null;
      String oggettodocumento =null;
      //String descrizionedocumento =null;
      //String mittenteinterno = null;
      //String codiceregistrodocumento = null;
      String inout = null;
      String inserimentoinfascicolo =  null;
      //String codicefascicolo =  null;
      //String oggettofascicolo =  null;
      String classificafascicolo =  null;
      //String descrizionefascicolo =  null;
      //String annofascicolo =  null;
      //String numerofascicolo =  null;
      //String idindice =  null;
      //String idtitolazione =  null;
      //String idunitaoperativamittente =  null;
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
      String classificadocumento = null;
      String RUP = null;
      String nomeRup = null;
      String acronimoRup = null;
      String sottotipo = null;

      String tipoWSDM = null;
      if("1".equals(integrazioneWSDM)){
        WSDMConfigurazioneOutType configurazione = gestioneWSDMManager.wsdmConfigurazioneLeggi("FASCICOLOPROTOCOLLO",datiWSDM.get("idconfi"));
        if (configurazione.isEsito())
          tipoWSDM = configurazione.getRemotewsdm();
      }

      WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = null;


      for (int i = 0; i < numeroAbilitate; i++) {

        Vector<String> datiDestinatario = new Vector<String>();
        String eMailPec ="";
        HashMap<String,String> datiImpr = this.gestioneWSDMManager.getDatiImpresa(ditteAbilitate[i]);
        if(datiImpr!=null){
          datiDestinatario.add(0, datiImpr.get("codice"));
          String email= datiImpr.get("emaiip");
          String pec = datiImpr.get("emai2ip");
          if ("1".equals(flagMailPec)) {
            eMailPec = email;
          } else {
            eMailPec = pec;
          }
          eMailPec = UtilityStringhe.convertiNullInStringaVuota(eMailPec);
          datiDestinatario.add(1, eMailPec);
          datiDestinatario.add(2, datiImpr.get("cognomeIntestazione"));
          if(!"FOLIUM".equals(tipoWSDM))
            datiDestinatario.add(3, datiImpr.get("codiceFiscale"));
          else
            datiDestinatario.add(3, "");

          datiDestinatario.add(4, datiImpr.get("indirizzoResidenza"));
          datiDestinatario.add(5, datiImpr.get("comuneResidenza"));
          datiDestinatario.add(6, datiImpr.get("codiceComuneResidenza"));
          if(!"FOLIUM".equals(tipoWSDM))
            datiDestinatario.add(7, datiImpr.get("piva"));
          else
            datiDestinatario.add(7, "");
          datiDestinatario.add(8, email);
          datiDestinatario.add(9, datiImpr.get("proimp"));
          datiDestinatario.add(10, datiImpr.get("capimp"));
          ListaDestinatari.add(datiDestinatario);
        }

      }
      boolean abilitatoInvioMailDocumentale = false;

      if("1".equals(integrazioneWSDM))
        abilitatoInvioMailDocumentale = gestioneWSDMManager.abilitatoInvioMailDocumentale("FASCICOLOPROTOCOLLO",datiWSDM.get("idconfi"));

      if (ListaDestinatari != null) {
        // eventualmente sarą una nuova funzione per le operazioni di inserimento
        String idprg = "PG";
        String insertW_INVCOM = "insert into w_invcom(IDPRG,IDCOM,COMENT,COMKEY1,COMKEY2,COMKEY3,COMKEY4,COMKEY5,COMCODOPE,COMDATINS,"
            + "COMMITT,COMSTATO,COMINTEST,COMMSGOGG,COMMSGTES,COMMODELLO,COMPUB,COMTIPO,COMDATASTATO,COMDATAPUB,COMTIPMA,COMMSGTIP,IDCFG)"
            + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] parametri = new Object[23];
        String oggetto = oggettoMail;
        String testomail = testoMail;
        parametri[0] = idprg;
        parametri[2] = "GARE";
        parametri[3] = codiceGara;
        parametri[4] = null;
        parametri[5] = null;
        parametri[6] = null;
        parametri[7] = null;
        Long syscon = new Long(profilo.getId());
        parametri[8] = syscon;
        //usrIns = (String) this.sqlManager.getObject("select sysute from usrsys where syscon = ?", new Object[] {syscon });
        Timestamp dataOdierna = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
        parametri[9] = dataOdierna;
        parametri[10] = mittenteMail;
        parametri[11] = "2";//"1" per bozza ;
        parametri[12] = "1";
        if(abilitatoInvioMailDocumentale)
          parametri[12] = "2";
        parametri[13] = oggetto;
        parametri[14] = testomail;
        parametri[15] = null;
        parametri[16] = null;
        if(isOp114)
          parametri[16]= new Long(2);
        parametri[17] = null;
        parametri[18] = null;
        parametri[19] = null;
        parametri[20] = null;
        parametri[21] = "2";
        parametri[22] = idcfg;
        Long newIdcom = (Long) sqlManager.getObject("select max(idcom) from w_invcom where idprg=?", new Object[] {idprg });
        if (newIdcom == null) {
          newIdcom = new Long(0);
        }
        newIdcom = new Long(newIdcom.longValue() + 1);
        parametri[1] = newIdcom;
        this.sqlManager.update(insertW_INVCOM, parametri);


        boolean inserimentoFascicoloArchiflowfa=false;
        boolean inserimentoFascicoloFolium = false;
        boolean inserimentoFascicoloPrisma = false;

        if("1".equals(integrazioneWSDM)){
           //Popolamento contenitore dati per  WSDM
           classificadocumento = datiWSDM.get("classificadocumento");
           tipodocumento =  datiWSDM.get("tipodocumento");
           oggettodocumento = datiWSDM.get("oggettodocumento");
           //descrizionedocumento = datiWSDM.get("descrizionedocumento");
           //mittenteinterno = datiWSDM.get("mittenteinterno");
           //codiceregistrodocumento = datiWSDM.get("codiceregistrodocumento");
           inout =  datiWSDM.get("inout");
           //idindice = datiWSDM.get("idindice");
           //idtitolazione = datiWSDM.get("idtitolazione");
           //idunitaoperativamittente =datiWSDM.get("idunitaoperativamittente");
           inserimentoinfascicolo =  datiWSDM.get("inserimentoinfascicolo");
           //codicefascicolo =  datiWSDM.get("codicefascicolo");
           //oggettofascicolo =  datiWSDM.get("oggettofascicolo");
           classificafascicolo =  datiWSDM.get("classificafascicolo");
           //descrizionefascicolo =  datiWSDM.get("descrizionefascicolo");
           //annofascicolo =  datiWSDM.get("annofascicolo");
           //numerofascicolo =  datiWSDM.get("numerofascicolo");

           //Per TITULUS si deve sbiancare il contenuto della classifica del fascicolo
           if("TITULUS".equals(tipoWSDM))
             classificafascicolo = null;

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
           if("".equals(codiceaoo))
             codiceaoo=null;
           codiceufficio = datiWSDM.get("codiceufficio");
           if("".equals(codiceufficio))
             codiceufficio=null;
           struttura =  datiWSDM.get("struttura");

           if("TITULUS".equals(tipoWSDM) && !abilitatoInvioMailDocumentale)
             tipodocumento=this.gestioneWSDMManager.TIPO_DOCUMENTO_ELENCO;
           else if("TITULUS".equals(tipoWSDM) && abilitatoInvioMailDocumentale)
             tipodocumento=this.gestioneWSDMManager.TIPO_DOCUMENTO_ELENCO_PEC;

           if("JDOC".equals(tipoWSDM)){
             RUP = datiWSDM.get("RUP");
             nomeRup = datiWSDM.get("nomeRup");
             acronimoRup = datiWSDM.get("acronimoRup");
             sottotipo = datiWSDM.get("sottotipo");
           }

           HashMap<String,Object> par = new HashMap<String,Object>();
           par.put("classificadocumento", classificadocumento);
           par.put("tipodocumento", tipodocumento);
           par.put("oggettodocumento", oggettodocumento);
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
           par.put("mezzo", datiWSDM.get("mezzo"));
           par.put("societa", datiWSDM.get("societa"));
           par.put("codiceGaralotto", datiWSDM.get("codiceGaralotto"));
           par.put("cig", datiWSDM.get("cig"));
           par.put("servizio","FASCICOLOPROTOCOLLO");
           par.put("numeroallegati", new Long(0));
           par.put("struttura", struttura);
           par.put("supporto", datiWSDM.get("supporto"));
           par.put("tipofascicolo", datiWSDM.get("tipofascicolo") );
           classificadescrizione = datiWSDM.get("classificadescrizione");
           par.put("classificadescrizione", classificadescrizione );
           voce = datiWSDM.get("voce");
           par.put("voce", voce );
           par.put("sottotipo", sottotipo );
           par.put("RUP", RUP );
           par.put("nomeRup", nomeRup );
           par.put("acronimoRup", acronimoRup );
           wsdmProtocolloDocumentoIn = this.gestioneWSDMManager.wsdmProtocolloDocumentoPopola(par,datiWSDM.get("idconfi"));

           if("TITULUS".equals(tipoWSDM)){
             codiceaoodes = datiWSDM.get("codiceaoodes");
             codiceufficiodes = datiWSDM.get("codiceufficiodes");
           }


           if(("ARCHIFLOWFA".equals(tipoWSDM) || "FOLIUM".equals(tipoWSDM) || "PRISMA".equals(tipoWSDM)) && "SI_FASCICOLO_NUOVO".equals(inserimentoinfascicolo)){
             if("ARCHIFLOWFA".equals(tipoWSDM))
               inserimentoFascicoloArchiflowfa=true;
             if("FOLIUM".equals(tipoWSDM))
               inserimentoFascicoloFolium = true;
             if("PRISMA".equals(tipoWSDM))
               inserimentoFascicoloPrisma = true;
           }

        }


        String insertW_INVCOMDES = "insert into w_invcomdes(IDPRG,IDCOM,IDCOMDES,DESCODENT,DESCODSOG,DESMAIL,DESTESTO,DESIDDOCDIG,DESDATINV,DESDATINV_S,DESSTATO,DESERRORE,DESINTEST,COMTIPMA)"
            + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String codiceDitta_i = null;
        String mailDitta_i = null;
        String ragsocDitta_i = null;
        parametri = new Object[14];

        if (ditteAbilitate != null && ditteAbilitate.length > 0) {
          String descodent = "IMPR";
          Long comtipma = new Long(2);
          if("2".equals(flagMailPec))
            comtipma = new Long(1);
          WSDMProtocolloAnagraficaType[] destinatari = new WSDMProtocolloAnagraficaType[ListaDestinatari.size()];
          String destinatarioPrincipale="";

          for (int i = 0; i < ListaDestinatari.size(); i++) {
            codiceDitta_i = (String) (ListaDestinatari.get(i)).get(0);
            mailDitta_i = (String) (ListaDestinatari.get(i)).get(1);
            ragsocDitta_i = (String) (ListaDestinatari.get(i)).get(2);
            parametri[0] = idprg;
            parametri[1] = newIdcom;
            parametri[3] = descodent;
            parametri[4] = codiceDitta_i;
            parametri[5] = mailDitta_i;
            parametri[6] = null;
            parametri[7] = null;
            parametri[8] = null;
            parametri[9] = null;
            parametri[10] = null;
            parametri[11] = null;
            parametri[12] = ragsocDitta_i;
            parametri[13] = comtipma;
            Long newIdcomdes = (Long) sqlManager.getObject("select max(idcomdes) from w_invcomdes where idprg = ? and idcom = ?",
                new Object[] {idprg, newIdcom });
            if (newIdcomdes == null) {
              newIdcomdes = new Long(0);
            }
            newIdcomdes = new Long(newIdcomdes.longValue() + 1);
            parametri[2] = newIdcomdes;

            sqlManager.update(insertW_INVCOMDES, parametri);

            if("1".equals(integrazioneWSDM)){
              //Destinatari WSDM
              destinatari[i] = new WSDMProtocolloAnagraficaType();
              if(!"FOLIUM".equals(tipoWSDM)){
                destinatari[i].setCodiceFiscale((String) (ListaDestinatari.get(i)).get(3));
                destinatari[i].setPartitaIVA((String) (ListaDestinatari.get(i)).get(7));
              }else{
                destinatari[i].setCodiceFiscale("");
                destinatari[i].setPartitaIVA("");;
              }
              destinatari[i].setIndirizzoResidenza((String) (ListaDestinatari.get(i)).get(4));
              destinatari[i].setComuneResidenza((String) (ListaDestinatari.get(i)).get(5));
              destinatari[i].setCodiceComuneResidenza((String) (ListaDestinatari.get(i)).get(6));
              destinatari[i].setCognomeointestazione(ragsocDitta_i);
              destinatarioPrincipale+=ragsocDitta_i;
              if(i<ListaDestinatari.size()-1)
                destinatarioPrincipale+= ", ";
              if(mezzoinvio!=null && !"".equals(mezzoinvio))
                destinatari[i].setMezzo(mezzoinvio);
              destinatari[i].setEmail(mailDitta_i);
              destinatari[i].setEmailAggiuntiva((String) (ListaDestinatari.get(i)).get(8));
              destinatari[i].setProvinciaResidenza((String) (ListaDestinatari.get(i)).get(9));
              destinatari[i].setCapResidenza((String) (ListaDestinatari.get(i)).get(10));
            }
          }
          if("1".equals(integrazioneWSDM)){
            wsdmProtocolloDocumentoIn.setDestinatari(destinatari);
            destinatarioPrincipale=this.gestioneWSDMManager.formattazioneDestinatarioPrincipale(destinatarioPrincipale);
            wsdmProtocolloDocumentoIn.setDestinatarioPrincipale(destinatarioPrincipale);
          }
        }

        if("1".equals(integrazioneWSDM)){
          // Invio mail mediante servizi di protocollazione per ENGINEERING
          if(abilitatoInvioMailDocumentale && ("ENGINEERING".equals(tipoWSDM) || "TITULUS".equals(tipoWSDM) || "SMAT".equals(tipoWSDM) || "URBI".equals(tipoWSDM))){
            WSDMInviaMailType inviaMail = new WSDMInviaMailType();
            if("ENGINEERING".equals(tipoWSDM)){
              // Oggetto email
              inviaMail.setOggettoMail(oggettodocumento);
            }
            // Testo email
            inviaMail.setTestoMail(testomail);
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

          if (testomail == null){
            testomail = "[testo vuoto]";
          }
          String contenutoPdf = this.gestioneWSDMManager.getTestoComunicazioneFormattato(key1, null, oggettoMail, testoMail);

          //Come unico allegato c'č il corpo della comunicazione
          WSDMProtocolloAllegatoType[] allegati = new WSDMProtocolloAllegatoType[1];
          // Aggiunta del testo della comunicazione

          allegati[0] = new WSDMProtocolloAllegatoType();
          allegati[0].setNome("Comunicazione.pdf");
          allegati[0].setTipo("pdf");
          allegati[0].setTitolo("Testo della comunicazione");
          allegati[0].setContenuto(UtilityStringhe.string2Pdf(contenutoPdf));
          if("TITULUS".equals(tipoWSDM))
            allegati[0].setIdAllegato("W_INVCOM|" + idprg + "|" + newIdcom.toString());

          wsdmProtocolloDocumentoIn.setAllegati(allegati);

          WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = this.gestioneWSDMManager.wsdmProtocolloInserisci(username, password,
                ruolo, nome, cognome, codiceuo, idutente, idutenteunop,codiceaoo, codiceufficio, wsdmProtocolloDocumentoIn, datiWSDM.get("idconfi"));

          if (wsdmProtocolloDocumentoRes.isEsito()) {
            String numeroDocumento = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroDocumento();
            Long annoProtocollo = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getAnnoProtocollo();
            String numeroProtocollo = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroProtocollo();
            String indirizzoMittente = datiWSDM.get("indirizzomittente");

            Timestamp dataProtocollo= this.gestioneWSDMManager.getDataProtocollo(wsdmProtocolloDocumentoRes);
            if(annoProtocollo==null)
              annoProtocollo = this.gestioneWSDMManager.getAnnoFromDate(dataProtocollo);

            datiWSDM.put("numeroDocumento", numeroDocumento);
            datiWSDM.put("annoProtocollo", annoProtocollo.toString());
            datiWSDM.put("numeroProtocollo", numeroProtocollo);
            datiWSDM.put("oggettodocumento", oggettodocumento);
            datiWSDM.put("testoMail", testoMail);
            datiWSDM.put("indirizzomittente", indirizzoMittente);
            datiWSDM.put("formatoMail", "2");

            String statoComunicazione =  this.gestioneWSDMManager.wsdmInvioMailEAggiornamentoDb(idprg, "FASCICOLOPROTOCOLLO", newIdcom.toString(), datiWSDM,allegati,0);

            // Salvataggio del numero protocollo nella comunicazione ed impostazione
            // dello stato a "In uscita"
            //Il campo COMMITT va aggiornato solo se indirizzoMittente č valorizzato

            Object param[]=null;
            String updateW_INVCOM = null;

            if(abilitatoInvioMailDocumentale && ("JIRIDE".equals(tipoWSDM)) || "ARCHIFLOW".equals(tipoWSDM) || "ARCHIFLOWFA".equals(tipoWSDM)){
              updateW_INVCOM = "update w_invcom set comstato = ?, comdatprot = ?, comnumprot = ?, committ = ? where idprg = ? and idcom = ?";
              param = new Object[]{statoComunicazione, dataProtocollo,
                  numeroProtocollo, indirizzoMittente, idprg,  newIdcom };
            }else{
              updateW_INVCOM = "update w_invcom set comstato = ?, comdatprot = ?, comnumprot = ? where idprg = ? and idcom = ?";
              param = new Object[]{statoComunicazione, dataProtocollo,
                  numeroProtocollo, idprg,  newIdcom };
            }
            this.sqlManager.update(updateW_INVCOM, param);

            // Salvataggio del riferimento al fascicolo
            if ("SI_FASCICOLO_NUOVO".equals(inserimentoinfascicolo) ) {
              String codiceFascicoloNUOVO =null;
              Long annoFascicoloNUOVO = null;
              String numeroFascicoloNUOVO = null;
              if(!inserimentoFascicoloArchiflowfa && !inserimentoFascicoloFolium && !inserimentoFascicoloPrisma){
                codiceFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getCodiceFascicolo();
                if (wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getAnnoFascicolo() != null)
                  annoFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getAnnoFascicolo();
                else
                  annoFascicoloNUOVO = this.gestioneWSDMManager.getAnnoFromDate(dataProtocollo);
                numeroFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getNumeroFascicolo();
              }else if(inserimentoFascicoloFolium){
                codiceFascicoloNUOVO = classificafascicolo;
                annoFascicoloNUOVO = this.gestioneWSDMManager.getAnnoFromDate(dataProtocollo);
              }else if(inserimentoFascicoloPrisma){
                codiceFascicoloNUOVO= datiWSDM.get("codicefascicolo");
                annoFascicoloNUOVO=new Long(datiWSDM.get("annofascicolo"));
                numeroFascicoloNUOVO=datiWSDM.get("numerofascicolo");
              }else
                codiceFascicoloNUOVO= datiWSDM.get("codicefascicolo");

              if("TITULUS".equals(tipoWSDM))
                classificafascicolo = classificadocumento;

              //String descrizioneFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getDescrizioneFascicolo();
              this.gestioneWSDMManager.setWSFascicolo(entita, key1, null, null, null, codiceFascicoloNUOVO, annoFascicoloNUOVO,
                  numeroFascicoloNUOVO, classificafascicolo,codiceaoo, codiceufficio,struttura,null,classificadescrizione,voce,codiceaoodes,codiceufficiodes);
            }

            //Salvatagio in WSDOCUMENTO
            Long idWSDocumento = this.gestioneWSDMManager.setWSDocumento(entita, key1, null, null, null, numeroDocumento, annoProtocollo, numeroProtocollo, oggettodocumento,inout);

            //Salvataggio della mail in WSALLEGATI
            this.gestioneWSDMManager.setWSAllegati("W_INVCOM", idprg, newIdcom.toString(), null, null, idWSDocumento);
          }else{
            String messaggio = wsdmProtocolloDocumentoRes.getMessaggio();
            throw new GestoreException("Errore nella protocollazione del fascicolo","wsdm.fascicoloprotocollo.protocollazione.error",new Object[]{messaggio}, new Exception());
          }
        }
      }
    }
    if(logger.isDebugEnabled())
      logger.debug("inviaComunicazioneAbilitazione: fine metodo");


    return 0;
  }

	/**
	 * Gestione di un rinnovo iscrizione ad elenco contenuto nella comunicazione
	 * FS3 individuata da idcom
	 *
	 * @param idcom
	 * @param user
	 * @param ngara
	 * @param dataIns
	 * @throws GestoreException
	 *
	 */
	public void rinnovoIscrizione(Long idcom, String user, String ngara, String dataIns) throws GestoreException {

		String select;

		//Vengono letti i documenti associati ad ogni occorrenza di di W_INVCOM
		select = "SELECT idprg, iddocdig FROM w_docdig WHERE digent = ? AND digkey1 = ? AND idprg = ? AND dignomdoc = ? ";
		String digent = "W_INVCOM";
		String idprgW_DOCDIG = "PA";

		Vector<?> datiW_DOCDIG;
		try {
			datiW_DOCDIG = this.sqlManager.getVector(select, new Object[]{digent, idcom.toString(), idprgW_DOCDIG, NOME_FILE_RINNOVO_ISCRIZIONE});
		} catch (SQLException e) {
			logger.error("Errore nella lettura della tabella W_DOCDIG della richiesta: IDCOM = " + idcom.toString(), e);
			throw new GestoreException("Errore nella lettura della tabella W_DOCDIG della richiesta: IDCOM = " + idcom.toString(), null, e);
		}

		String idprgW_INVCOM = null;
		Long iddocdig = null;

		if (datiW_DOCDIG != null) {

			if (((JdbcParametro) datiW_DOCDIG.get(0)).getValue() != null) {
				idprgW_INVCOM = ((JdbcParametro) datiW_DOCDIG.get(0)).getStringValue();
			}
			try {
				if (((JdbcParametro) datiW_DOCDIG.get(1)).getValue() != null) {
					iddocdig = ((JdbcParametro) datiW_DOCDIG.get(1)).longValue();
				}
			} catch (GestoreException e) {
				logger.error("Errore nella lettura della tabella W_DOCDIG della richiesta: IDCOM=" + idcom.toString(), e);
				throw e;
			}

			// Lettura del file xml immagazzinato nella tabella W_DOCDIG
			BlobFile fileAllegato = null;
			try {
				fileAllegato = this.fileAllegatoManager.getFileAllegato(idprgW_INVCOM, iddocdig);
			} catch (Exception e) {
				logger.error("Errore nella lettura del file allegato presente nella tabella W_DOCDIG della richiesta: IDCOM = " + idcom.toString(), e);
				throw new GestoreException("Errore nella lettura del file allegato presente nella tabella W_DOCDIG della richiesta: IDCOM = " + idcom.toString(), null, e);
			}
			String xml;
			if (fileAllegato != null && fileAllegato.getStream() != null) {
				xml = new String(fileAllegato.getStream());
				this.rinnovoIscrizioneElencoOperatori(idcom, user, ngara, xml, dataIns);
				this.pgManager.aggiornaStatoW_INVOCM(idcom, "6", "");
			} else {
				this.pgManager.aggiornaStatoW_INVOCM(idcom, "7", "");
			}
		} else {
			this.pgManager.aggiornaStatoW_INVOCM(idcom, "7", "");
		}
	}

	/**
	 * Viene eseguito il rinnovo iscrizione ad un elenco
	 *
	 * @param codiceDitta
	 * @param xml
	 * @param idcom
	 * @param user
	 * @param ngara
	 * @param dataIns
	 * @throws GestoreException
	 */
	private void rinnovoIscrizioneElencoOperatori(Long idcom, String user,
					String ngara, String xml, String dataIns) throws GestoreException {

		RinnovoIscrizioneImpresaElencoOperatoriDocument document;
		try {
			document = RinnovoIscrizioneImpresaElencoOperatoriDocument.Factory.parse(xml);

			Calendar dataPresentazione = document.getRinnovoIscrizioneImpresaElencoOperatori().getDataPresentazione();
			java.util.Date campoData = null;
			String oraConSecondi = null;
			if (dataPresentazione != null) {
				campoData = dataPresentazione.getTime();
				String data = UtilityDate.convertiData(campoData, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
				//ottengo una stringa nel formato GG/MM/AAAA HH:MI:SS
				if (data != null && data.length() > 15) {
					oraConSecondi = data.substring(11);
				}
			}

			String codiceGara = "$" + ngara;

			//Inserimento documenti
			this.pgManager.insertDocumenti(document, codiceGara, user, ngara, campoData, oraConSecondi,idcom);

			//Aggiornamento dati relativi all'operatore
			String select = "SELECT apprin FROM garealbo WHERE codgar = ? AND ngara = ?";
			Long aprin = (Long) this.sqlManager.getObject(select, new Object[]{codiceGara, ngara});

			select = "SELECT abilitaz FROM ditg WHERE codgar5 = ? AND ngara5 = ? AND dittao = ?";
			Long abilitaz = (Long) this.sqlManager.getObject(select, new Object[]{codiceGara, ngara, user});

			Long RINNOVO_AUTOMATICO = (long) 2;
			Long RINNOVO_DA_VERIFICARE = (long) 2;
			Long SOSPESO_PER_ISCRIZIONE_SCADUTA = (long) 8;

			List<Object> params = new ArrayList<Object>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("UPDATE ditg SET dricrin = ?, strin = ?");
			Timestamp dataInserimento = new Timestamp(UtilityDate.convertiData(dataIns,
					UtilityDate.FORMATO_GG_MM_AAAA).getTime());
			params.add(dataInserimento);
			params.add(RINNOVO_DA_VERIFICARE);

			if (aprin != null && aprin.equals(RINNOVO_AUTOMATICO)) {
				params.add(dataInserimento);
				sbQuery.append(", dscad = ?");
				if (abilitaz != null && abilitaz.equals(SOSPESO_PER_ISCRIZIONE_SCADUTA)) {
					params.add(null);
					params.add((long) 1);
					sbQuery.append(", dsospe = ?, abilitaz = ?");
				}
			}
			params.add(codiceGara);
			params.add(ngara);
			params.add(user);
			sbQuery.append(" WHERE codgar5 = ? AND ngara5 = ? AND dittao = ?");
			this.sqlManager.update(sbQuery.toString(), params.toArray());
		} catch (XmlException e) {
			logger.error("Errore nella lettura del file XML", e);
			throw new GestoreException("Errore nella lettura del file XML", null, e);
		} catch (SQLException e) {
			logger.error("Errore nella lettura dei dati di rinnovo iscrizione", e);
			throw new GestoreException("Errore nella lettura dei dati di rinnovo iscrizione", null, e);
		}
	}

	/**
     * Viene eseguito il calcolo dell'importo aggiudicato nel periodo per gli operatori dell'elenco
     * che rispettano le condizioni per l'inserimento in gara tramite la selezione da elenco
     *
     * @param elenco
     * @throws Exception
     */
	public void conteggioImportoAggiudicatoNelPeriodo(String elenco) throws Exception {

	  //si sbianca il campo DITG.IAGGIULELE di tutti gli operatori dell'elenco
      this.sqlManager.update("update ditg set iaggiuele=null where ngara5=?", new Object[]{elenco});

	  Long ctrlgg=null;
	  String ctrlele=null;

	  Vector datiGarealbo = this.sqlManager.getVector("select ctrlgg, ctrlele from garealbo where ngara=?", new Object[]{elenco});
	  if(datiGarealbo!=null && datiGarealbo.size()>0){
	    ctrlgg = SqlManager.getValueFromVectorParam(datiGarealbo, 0).longValue();
	    ctrlele = SqlManager.getValueFromVectorParam(datiGarealbo, 1).getStringValue();
	  }

      String ditta = null;
      String oggi = null;
      Double importo =null;
      Object parametri[]=null;

	  //Lista degli operatori su cui effettuare il calcolo
      String selectImportoDitte = "select dittao,sum(iaggiu) from ditg, gare, torn " +
      "where ditg.ngara5=? and ditg.abilitaz=1 and ditg.dabilitaz is not null and ditg.numordpl is not null " +
      "and codgar1=codgar and (accqua is null or accqua='2') " +
      "and dattoa is not null and dattoa <= # and dattoa > # - ? " +
      "and (ditta = ditg.dittao or exists(select codime9 from ragimp where codime9=ditta and coddic=ditg.dittao and impman='1'))";

	  if("1".equals(ctrlele)){
	      parametri = new Object[4];
	      selectImportoDitte += " and ((elencoe=? and exists (select dittao from ditg d1 where d1.ngara5=ngara and (d1.dittao=ditta or d1.rtofferta=ditta) and d1.acquisizione=3))"+
	        " or exists(select ngara from gare gg where gg.genere=3 and gg.codgar1=codgar and gg.elencoe=?"+
	        " and exists (select dittao from ditg d2 where d2.ngara5=gg.ngara and (d2.dittao=gare.ditta or d2.rtofferta=gare.ditta) and d2.acquisizione=3)))";
	  }else{
	      parametri = new Object[2];
	  }
	  selectImportoDitte += " group by dittao";
	  parametri[0]=elenco;
	  parametri[1]=new Integer(ctrlgg.intValue());
	  if("1".equals(ctrlele)){
	      parametri[2]=elenco;
          parametri[3]=elenco;
	  }

	  Object importoObj = null;
	  oggi = UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_GG_MM_AAAA);
	  String dbFunctionStringToDate = sqlManager.getDBFunction("stringtodate",
	            new String[] { oggi });
      selectImportoDitte = selectImportoDitte.replaceAll("#", dbFunctionStringToDate);

      //Forza parametri sessione di default per pb di lentezza di esecuzione della select
      if (it.eldasoft.utils.sql.comp.SqlManager.DATABASE_ORACLE.equalsIgnoreCase(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE))) {
            sqlManager.execute("alter session set nls_sort = 'BINARY'");
            sqlManager.execute("alter session set nls_comp = 'BINARY'");
      }
      List listaImportoDitte = this.sqlManager.getListVector(selectImportoDitte, parametri);
      for(int i=0; i<listaImportoDitte.size();i++){
        ditta=SqlManager.getValueFromVectorParam(listaImportoDitte.get(i), 0).getStringValue();
        importo =  SqlManager.getValueFromVectorParam(listaImportoDitte.get(i), 1).doubleValue();
        this.sqlManager.update("update ditg set iaggiuele=? where ngara5=? and dittao=?", new Object[]{importo,elenco,ditta});
    }

	  //Aggiornamento della data di esecuzione della funzione
	  Timestamp dataOraoggi = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
      this.sqlManager.update("update garealbo set ctrldata=? where ngara=?", new Object[]{dataOraoggi,elenco});
	}
}