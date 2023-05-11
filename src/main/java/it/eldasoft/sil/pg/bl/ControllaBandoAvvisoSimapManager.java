/*
 * Created on 17/apr/2018
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.bl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.simap.ws.EsitoSimapWS;
import it.eldasoft.simap.ws.xmlbeans.AuthorityType;
import it.eldasoft.simap.ws.xmlbeans.AvvisoAggiudicazioneDocument;
import it.eldasoft.simap.ws.xmlbeans.AvvisoAggiudicazioneType;
import it.eldasoft.simap.ws.xmlbeans.AvvisoPreinformazioneDocument;
import it.eldasoft.simap.ws.xmlbeans.AvvisoPreinformazioneType;
import it.eldasoft.simap.ws.xmlbeans.AvvisoProfiloCommittenteDocument;
import it.eldasoft.simap.ws.xmlbeans.AvvisoProfiloCommittenteType;
import it.eldasoft.simap.ws.xmlbeans.BandoGaraDocument;
import it.eldasoft.simap.ws.xmlbeans.BandoGaraType;
import it.eldasoft.simap.ws.xmlbeans.CPVType;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityFiscali;


public class ControllaBandoAvvisoSimapManager {

  static Logger               logger                         = Logger.getLogger(ControllaBandoAvvisoSimapManager.class);

  private static final String PROP_BANDO_AVVISO_SIMAP_WS_URL = "it.eldasoft.bandoavvisosimap.ws.url";

  private static final String ERRORI_NON_BLOCCANTI_KEY        = "erroriNonBloccanti";
  private static final String ERRORI_BLOCCANTI_KEY            = "erroriBloccanti";
  private static final String ERRORI_NON_BLOCCANTI_TEL_KEY = "erroriNonBloccantiTel";
  private static final String ERRORI_NON_BLOCCANTI_FAX_KEY = "erroriNonBloccantiFax";

  private static final String CTR_AUTHORITY                  = "Il riferimento alla stazione appaltante non e' valorizzato";
  private static final String CTR_OGGETTO                    = "L'oggetto della gara non e' valorizzato";
  private static final String CTR_OGGETTO_AVVISO             = "L'oggetto dell'avviso non e' valorizzato";
  private static final String CTR_TYPE_CONTRACT              = "Il tipo di appalto non e' valorizzato";
  //messaggi temporanei da modificare
  private static final String CTR_TORN_TIPGAR                = "Il tipo procedura non è valorizzato";
  private static final String CTR_GARE_TIPGAR                = "Il tipo procedura non è valorizzato";
  private static final String CTR_CPV                        = "Il codice CPV non è valorizzato";
  private static final String CTR_CPV_LOTTO                  = "Il codice CPV non è valorizzato nei lotti ";
  private static final String CTR_CIG                        = "Il codice CIG non è valorizzato";
  private static final String CTR_CIG_LOTTO                  = "Il codice CIG non è valorizzato nei lotti ";
  private static final String CTR_DATTOA                     = "La data atto aggiudicazione non è valorizzata";
  private static final String CTR_DATTOA_LOTTO               = "La data atto aggiudicazione non è valorizzata nei lotti ";
  private static final String CTR_TEUTIL                     = "La durata del contratto non è valorizzata";
  private static final String CTR_TEUTIL_LOTTO               = "La durata del contratto non è valorizzata nei lotti ";
  private static final String CTR_CODNUTS                    = "Il codice NUTS non è valorizzato";
  private static final String CTR_AVVISO_TIPOAVV             = "La tipologia dell'avviso non è valorizzata";
  private static final String CTR_GOEV_LOTTI                 = "Non sono definiti correttamente i criteri di valutazione OEPV. Verificare i lotti ";
  private static final String CTR_GOEV_GARA                  = "Non sono definiti correttamente i criteri di valutazione OEPV";
  private static final String CTR_TROPPI_LOTTI               = "La gara ha più di 1000 lotti";
  private static final String CTR_NESSUN_LOTTO               = "Non sono stati definiti i lotti della gara";
  private static final String CTR_PCOPRE_DATA                = "Alcuni dati del punto di contatto per la presentazione della domanda di partecipazione non sono valorizzati (verificare indirizzo, e-mail e URL)";
  private static final String CTR_PCOPRE_NOMPUN              = "La denominazione del punto di contatto per la presentazione della domanda di partecipazione non è valorizzata";
  private static final String CTR_PCODOC_DATA                = "Alcuni dati del punto di contatto per ulteriori informazioni non sono valorizzati (verificare indirizzo, e-mail e URL)";
  private static final String CTR_PCODOC_NOMPUN              = "La denominazione del punto di contatto per ulteriori informazioni non è valorizzata";
  private static final String CTR_PCOOFF_DATA                = "Alcuni dati del punto di contatto per presentazione offerte non sono valorizzati (verificare indirizzo, e-mail e URL)";
  private static final String CTR_PCOOFF_NOMPUN              = "La denominazione del punto di contatto per presentazione offerte non è valorizzata";

  private static final String CTR_OFFLOT                     = "La modalità di presentazione delle offerte per i lotti di gara non è valorizzata";
  private static final String CTR_NOFDIT                     = "Il numero massimo lotti per cui presentare offerta non è valorizzato";
  private static final String CTR_NGADIT                     = "Il numero massimo lotti aggiudicabili da una ditta non è valorizzato";
  private static final String CTR_AMMRIN                     = "Il flag 'Oggetto di rinnovo' non è valorizzato";
  private static final String CTR_DESRIN                     = "La descrizione dei rinnovi non è valorizzata";
  private static final String CTR_AMMRIN_LOTTI               = "Il flag 'Oggetto di rinnovo' non è valorizzato nei lotti ";
  private static final String CTR_DESRIN_LOTTI               = "La descrizione dei rinnovi non è valorizzata nei lotti ";
  private static final String CTR_AMMVAR                     = "Il flag 'Ammissibilità di varianti' non è valorizzato";
  private static final String CTR_AMMOPZ                     = "Il flag 'Ricorso a opzioni' non è valorizzato";
  private static final String CTR_AMMOPZ_LOTTI               = "Il flag 'Ricorso a opzioni' non è valorizzato nei lotti ";
  private static final String CTR_DESOPZ                     = "La descrizione opzioni non è valorizzata";
  private static final String CTR_DESOPZ_LOTTI               = "La descrizione opzioni non è valorizzata nei lotti ";
  private static final String CTR_APFINFC                    = "Il flag 'Appalto connesso ad un progetto finanziato dai fondi comunitari' non è valorizzato";
  private static final String CTR_PROGEU                     = "La descrizione progetto finanziato dai fondi comunitari non è valorizzata";
  private static final String CTR_ACCAPUB                    = "Il flag 'Appalto rientra nel campo di applicazione dell'accordo sugli appalti pubblici' non è valorizzato";
  private static final String CTR_TEPAR                      = "Il termine per la presentazione della domanda di partecipazione non è valorizzato";
  private static final String CTR_TEOFF                      = "Il termine per la presentazione dell'offerta non è valorizzato";
  private static final String CTR_ESOFF                      = "La data di apertura delle offerte non è valorizzata";
  private static final String CTR_MOTACC                     = "La giustificazione procedura accelerata non è valorizzata";

  private static final String CTR_AQDURATA                   = "La durata dell'accordo quadro non è valorizzata";

  private static final String CTR_COMPONENTI_RT              = "La ditta aggiudicataria è un raggruppamento per cui devono essere specificate almeno due ditte componenti";
  private static final String CTR_COMPONENTI_LOTTI_RT        = "La ditta aggiudicataria è un raggruppamento per cui devono essere specificate almeno due ditte componenti. Verificare i lotti ";

  private static final String CTR_LOTTI_IMPR_AGG_CF_PIVA     = "Il codice fiscale o la partita Iva della ditta aggiudicataria o delle sue componenti, nel caso di raggruppamento, non sono valorizzati o non hanno formato valido. Verificare i lotti ";
  private static final String CTR_LOTTI_IMPR_AGG_LOC         = "La provincia o il comune sede della ditta aggiudicataria o delle sue componenti, nel caso di raggruppamento, non sono valorizzati. Verificare i lotti ";
  private static final String CTR_GARA_IMPR_AGG_CF_PIVA      = "Il codice fiscale o la partita Iva della ditta aggiudicataria o delle sue componenti, nel caso di raggruppamento, non sono valorizzati o non hanno formato valido";
  private static final String CTR_GARA_IMPR_AGG_LOC          = "La provincia o il comune sede della ditta aggiudicataria o delle sue componenti, nel caso di raggruppamento, non sono valorizzati";
  private static final String CTR_GARA_IMPR_AGG_TEL          = "Il numero di telefono della ditta aggiudicataria o delle sue componenti, nel caso di raggruppamento, non ha un formato valido (es.: +39 12345678, +39 12345678-0001, +39 12345678/79/80/81)";
  private static final String CTR_LOTTI_IMPR_AGG_TEL         = "Il numero di telefono della ditta aggiudicataria o delle sue componenti, nel caso di raggruppamento, non ha un formato valido (es.: +39 12345678, +39 12345678-0001, +39 12345678/79/80/81). Verificare i lotti ";
  private static final String CTR_GARA_IMPR_AGG_FAX          = "Il fax della ditta aggiudicataria o delle sue componenti, nel caso di raggruppamento, non ha un formato valido (es.: +39 12345678, +39 12345678-0001, +39 12345678/79/80/81)";
  private static final String CTR_LOTTI_IMPR_AGG_FAX         = "Il fax della ditta aggiudicataria o delle sue componenti, nel caso di raggruppamento, non ha un formato valido (es.: +39 12345678, +39 12345678-0001, +39 12345678/79/80/81). Verificare i lotti ";

  private static final String CTR_GARA_NON_AGG               = "La gara non è aggiudicata nè ha esito negativo";
  private static final String CTR_LOTTI_NON_AGG              = "Nessun lotto della gara risulta aggiudicato né ha esito negativo";
  private static final String CTR_GARA_TITLE                 = "L'oggetto della gara non è valorizzato";
  private static final String CTR_LOTTO_TITLE                = "L'oggetto della gara non è valorizzato nei lotti ";

  private static final String CTR_AUTHORITY_NOMEIN           = "La denominazione della stazione appaltante non e' valorizzata";
  private static final String CTR_AUTHORITY_CFPI             = "Il codice fiscale e la partita Iva della stazione appaltante sono entrambi non valorizzati";
  private static final String CTR_AUTHORITY_CF_VALIDO        = "Il codice fiscale della stazione appaltante non ha un formato valido";
  private static final String CTR_AUTHORITY_PI_VALIDO        = "La partita Iva della stazione appaltante non ha un formato valido";

  private SqlManager          sqlManager;
  private PgManager           pgManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  public void setPgManager(PgManager pgManager) {
    this.pgManager = pgManager;
  }


  /**
   *
   * @param codgar
   * @param formulario
   * @param username
   * @param password
   * @return
   * @throws Exception
   * @throws Throwable
   */
  public HashMap<String,Object> controllaBandoAvvisoSimap(String codgar, String formulario, BigInteger sottotipo,
      String username, String password) throws Exception, Throwable {

    if (logger.isDebugEnabled())
      logger.debug("controllaBandoAvvisoSimap: inizio metodo");
    HashMap<String,Object> response = new HashMap<String,Object>();
    EsitoSimapWS esitoSimapWS = new EsitoSimapWS();
    ArrayList<String> erroriBloccanti = new ArrayList<String>();
    ArrayList<String> erroriNonBloccanti = new ArrayList<String>();

    String url = ConfigManager.getValore(PROP_BANDO_AVVISO_SIMAP_WS_URL);
    if (url == null || "".equals(url)) {
      throw new GestoreException(
          "L'indirizzo per la connessione al web service non e' definito",
          "inviabandoavvisosimap.ws.url");
    }
    try {
      if ("FS1".equals(formulario)) {
        response = this.getXMLAvvisoPreinformazione(codgar,sottotipo);
        erroriBloccanti = (ArrayList<String>) response.get(ERRORI_BLOCCANTI_KEY);
        erroriNonBloccanti = (ArrayList<String>) response.get(ERRORI_NON_BLOCCANTI_KEY);
      } else if ("FS2".equals(formulario)) {
        response = this.getXMLBandoGara(codgar);
        erroriBloccanti = (ArrayList<String>) response.get(ERRORI_BLOCCANTI_KEY);
        erroriNonBloccanti = (ArrayList<String>) response.get(ERRORI_NON_BLOCCANTI_KEY);
      } else if ("FS3".equals(formulario)) {
        response = this.getXMLAvvisoAggiudicazione(codgar);
        erroriBloccanti = (ArrayList<String>) response.get(ERRORI_BLOCCANTI_KEY);
        erroriNonBloccanti = (ArrayList<String>) response.get(ERRORI_NON_BLOCCANTI_KEY);
      } else if ("FS8".equals(formulario)) {
        response = this.getXMLAvvisoProfiloCommittente(codgar);
        erroriBloccanti = (ArrayList<String>) response.get(ERRORI_BLOCCANTI_KEY);
        erroriNonBloccanti = (ArrayList<String>) response.get(ERRORI_NON_BLOCCANTI_KEY);
      }
    } catch (Throwable t) {
      throw t;
    }
    if (logger.isDebugEnabled())
      logger.debug("controllaBandoAvvisoSimap: fine metodo");
    return response;
  }


  /**
   * Restituisce XML contenente i dati dell'avviso di preinformazione
   *
   * @param codgar
   * @return
   * @throws SQLException
   * @throws IOException
   * @throws Exception
   */
  private HashMap<String,Object> getXMLAvvisoPreinformazione(String codgar,BigInteger sottotipo)
      throws SQLException, IOException, Exception {

    if (logger.isDebugEnabled())
      logger.debug("getXMLAvvisoPreinformazione: inizio metodo");

    HashMap<String,Object> response = new HashMap<String,Object>();
    String xml = null;
    ArrayList<String> erroriBloccanti = new ArrayList();
    ArrayList<String> erroriNonBloccanti = new ArrayList();

    AvvisoPreinformazioneDocument avvisoPreinformazioneDocument = AvvisoPreinformazioneDocument.Factory.newInstance();
    avvisoPreinformazioneDocument.documentProperties().setEncoding("UTF-8");
    AvvisoPreinformazioneType avvisoPreinformazione = avvisoPreinformazioneDocument.addNewAvvisoPreinformazione();

    Long genere = this.getGENERE(codgar);
    if(codgar.indexOf("$")>=0){
      Long genereTmp = this.getGENEREGARE(codgar.substring(1));
      if(genereTmp!= null && genereTmp.longValue()==11)
        genere = new Long(11);
    }

    //controllo se la gara ha più di mille lotti
    int numeroLotti = this.getNumeroLotti(codgar);
    if(numeroLotti > 1000){
      erroriBloccanti.add(CTR_TROPPI_LOTTI);
    }
    if(numeroLotti <= 0){
      erroriBloccanti.add(CTR_NESSUN_LOTTO);
    }



    String selectTORNGARE = null;

    switch (genere.intValue()) {
    case 1:
      selectTORNGARE = "select torn.cenint, "
          + " torn.destor, "
          + " torn.tipgen, "
          + " torn.codnuts, "
          + " torn.pcodoc, "
          + " torn.pcopre, "
          + " torn.tipgar, "
          //NEW ENTRIES
          + " torn.offlot, "
          + " torn.nofdit, "
          + " torn.ngadit, "
          + " torn.accappub, "
          + " torn.ammvar, "
          + " torn.apfinfc, "
          + " torn.progeu, "
          + " torn.dtepar, "
          + " torn.otepar "
          + " from torn where codgar = ?";
      break;
    case 3:
      selectTORNGARE = "select torn.cenint, "
          + " torn.destor, "
          + " torn.tipgen, "
          + " torn.codnuts, "
          + " torn.pcodoc, "
          + " torn.pcopre, "
          + " torn.tipgar, "
          //NEW ENTRIES
          + " torn.offlot, "
          + " torn.nofdit, "
          + " torn.ngadit, "
          + " torn.accappub, "
          + " torn.ammvar, "
          + " torn.apfinfc, "
          + " torn.progeu, "
          + " torn.dtepar, "
          + " torn.otepar "
          + " from torn where codgar = ?";
      break;
    case 2:
      selectTORNGARE = "select torn.cenint, "
          + " gare.not_gar, "
          + " torn.tipgen, "
          + " torn.codnuts, "
          + " torn.pcodoc, "
          + " torn.pcopre, "
          + " gare.tipgarg, "
          //NEW ENTRIES
          + " torn.offlot, "
          + " torn.nofdit, "
          + " torn.ngadit, "
          + " torn.accappub, "
          + " torn.ammvar, "
          + " torn.apfinfc, "
          + " torn.progeu, "
          + " torn.dtepar, "
          + " torn.otepar "
          + " from torn, gare where torn.codgar = gare.codgar1"
          + " and torn.codgar = ?";
      break;
    case 11:
      selectTORNGARE = "select torn.cenint, "
        + " gareavvisi.oggetto, "
        + " gareavvisi.tipoapp, "
        + " torn.codnuts, "
        + " torn.pcodoc, "
        + " torn.pcopre, "
        + " gareavvisi.tipoavv "
        + " from torn, gareavvisi where torn.codgar = gareavvisi.codgar"
        + " and torn.codgar = ?";
    break;
    }

    List datiTORNGARE = sqlManager.getVector(selectTORNGARE,
        new Object[] { codgar });

    if (datiTORNGARE != null && datiTORNGARE.size() > 0) {
      HashMap<String,Object> mapTemp;

      // Amministrazione
      String codein = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
          0).getValue();
      if (codein != null) {
        AuthorityType authority = AuthorityType.Factory.newInstance();
        mapTemp= new HashMap<String,Object>();
        mapTemp = this.getAuthority(codein);
        erroriBloccanti.addAll((ArrayList<String>) mapTemp.get(ERRORI_BLOCCANTI_KEY));
        erroriNonBloccanti.addAll((ArrayList<String>) mapTemp.get(ERRORI_NON_BLOCCANTI_KEY));
      } else {
        erroriBloccanti.add(CTR_AUTHORITY);
      }

      //controllo il campo not_gar
      if(genere != 11){
        erroriBloccanti.addAll(this.getControlloNOTGAR(codgar, genere));
      }

      // Titolo del contratto e descrizione breve
      if(genere != null && genere != 2){
        String title = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
            1).getValue();
        if (title != null) {
        } else {
          if(genere!= null && genere.longValue()==11)
            erroriBloccanti.add(CTR_OGGETTO_AVVISO);
          else
            erroriBloccanti.add(CTR_OGGETTO);
        }
      }

      //controllo se la gara è OEPV e se sono valorizzati correttamente i criteti di valutazione
      mapTemp = this.controlloGoevGara(codgar,genere);
      String tempString = ((String) mapTemp.get(ERRORI_BLOCCANTI_KEY));
      if(tempString != null)erroriBloccanti.add(tempString);


      // Tipo di contratto
      Long type_contract = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 2).getValue();
      if (type_contract == null) {
        erroriBloccanti.add(CTR_TYPE_CONTRACT);
      }

      //controllo errori CPV
      ArrayList erroriTemp = this.getCPVGara(codgar, genere);
      erroriNonBloccanti.addAll(erroriTemp);

      // Sito o luogo principale di esecuzione: codice NUTS
      String site_nuts = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 3).getValue();
      if (site_nuts == null) {
        erroriNonBloccanti.add(CTR_CODNUTS);
      }

      //punto di contatto pcopre
      Long pcopre = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 5).getValue();
      if (pcopre != null) {
        String nompun = this.getNOMPUN(codein, pcopre);
        if (nompun != null) {
        }else{
          erroriBloccanti.add(CTR_PCOPRE_NOMPUN);
        }
        //punto di contatto pcopre
        if(this.getPunticonError(codein, pcopre)){
          erroriNonBloccanti.add(CTR_PCOPRE_DATA);
        };
      }

      //punto di contatto pcodoc
      Long pcodoc = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 4).getValue();
      if (pcodoc != null) {
        String nompun = this.getNOMPUN(codein, pcodoc);
        if (nompun != null) {
        }else{
          erroriBloccanti.add(CTR_PCODOC_NOMPUN);
        }
        //punto di contatto pcodoc
        if(this.getPunticonError(codein, pcodoc)){
          erroriNonBloccanti.add(CTR_PCODOC_DATA);
        };
      }

      //Controllo su tipgar
      Long tipo = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 6).getValue();
      if (tipo != null) {
      }else{
        if(genere.longValue()==2)
          erroriBloccanti.add(CTR_GARE_TIPGAR);
        if(genere.longValue()==3 || genere.longValue()==1)
          erroriBloccanti.add(CTR_TORN_TIPGAR);
        if(genere.longValue()==11)
          erroriBloccanti.add(CTR_AVVISO_TIPOAVV);
      }

      if(genere!=null && genere.longValue()!=11){

        //INSERIRE QUI I NUOVI CONTROLLI NON BLOCCANTI
        //SOLO SE LA GARA NON E' AVVISO

        // Offlot
        Long offlot = null;
        if(genere == 1 || genere == 3){
          offlot = (Long) SqlManager.getValueFromVectorParam(
              datiTORNGARE, 7).getValue();
          if (offlot == null) {
            erroriNonBloccanti.add(CTR_OFFLOT);
          }
        }

        // nofdit
        if(offlot != null && offlot == 2){
          Long nofdit = (Long) SqlManager.getValueFromVectorParam(
              datiTORNGARE, 8).getValue();
          if (nofdit == null) {
            erroriNonBloccanti.add(CTR_NOFDIT);
          }
        }

        // ngadit
        if(offlot != null && (offlot == 2 || offlot == 3)){
          Long ngadit = (Long) SqlManager.getValueFromVectorParam(
              datiTORNGARE, 9).getValue();
          if (ngadit == null) {
            erroriNonBloccanti.add(CTR_NGADIT);
          }
        }

        ArrayList<String> temp = getAmmrinDesrin(codgar, genere);
        erroriNonBloccanti.addAll(temp);

        // ammvar
        String ammvar = (String) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 11).getValue();
        if (ammvar == null) {
          erroriNonBloccanti.add(CTR_AMMVAR);
        }

        //desopz
        HashMap<String,ArrayList> tempMap = getAmmopzDesopz(codgar, genere);
        temp = tempMap.get("DESOPZ");
        erroriNonBloccanti.addAll(temp);

        // apfinfc
        String apfinfc = (String) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 12).getValue();
        if (apfinfc == null) {
          erroriNonBloccanti.add(CTR_APFINFC);
        }

        // progeu
        if(apfinfc != null && apfinfc.equals("1")){
          String progeu = (String) SqlManager.getValueFromVectorParam(
              datiTORNGARE, 13).getValue();
          if (progeu == null) {
            erroriNonBloccanti.add(CTR_PROGEU);
          }
        }

        // accapub
        String accapub = (String) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 10).getValue();
        if (accapub == null) {
          erroriNonBloccanti.add(CTR_ACCAPUB);
        }

       // dtepar
        if(BigInteger.valueOf(3).compareTo(sottotipo) == 0 || sottotipo == BigInteger.valueOf(3)){
          Date dtepar = (Date) SqlManager.getValueFromVectorParam(
              datiTORNGARE, 14).getValue();
          String otepar = (String) SqlManager.getValueFromVectorParam(
              datiTORNGARE, 15).getValue();
          if (dtepar == null || otepar == null) {
            erroriNonBloccanti.add(CTR_TEPAR);
          }
        }
      }
    }

    ByteArrayOutputStream baosAvvisoPreinformazione = new ByteArrayOutputStream();
    avvisoPreinformazioneDocument.save(baosAvvisoPreinformazione);
    xml = baosAvvisoPreinformazione.toString();
    baosAvvisoPreinformazione.close();

    response.put(ERRORI_BLOCCANTI_KEY, erroriBloccanti);
    response.put(ERRORI_NON_BLOCCANTI_KEY, erroriNonBloccanti);
    response.put("xml",xml);

    if (logger.isDebugEnabled())
      logger.debug("getXMLAvvisoPreinformazione: fine metodo");

    return response;
  }


  /**
   * Restituisce XML contenente i dati del bando di gara
   *
   * @param codgar
   * @return
   * @throws SQLException
   * @throws IOException
   */
  private HashMap<String,Object> getXMLBandoGara(String codgar) throws SQLException,
      IOException, Exception {

    if (logger.isDebugEnabled())
      logger.debug("getXMLBandoGara: inizio metodo");

    HashMap<String,Object> response = new HashMap<String,Object>();
    String xml = null;
    ArrayList<String> erroriBloccanti = new ArrayList();
    ArrayList<String> erroriNonBloccanti = new ArrayList();

    BandoGaraDocument bandoGaraDocument = BandoGaraDocument.Factory.newInstance();
    bandoGaraDocument.documentProperties().setEncoding("UTF-8");
    BandoGaraType bandoGara = bandoGaraDocument.addNewBandoGara();

    Long genere = this.getGENERE(codgar);
    if(codgar.indexOf("$")>=0){
      Long genereTmp = this.getGENEREGARE(codgar.substring(1));
      if(genereTmp!= null && genereTmp.longValue()==11)
        genere = new Long(11);
    }

    //controllo se la gara ha più di mille lotti
    int numeroLotti = this.getNumeroLotti(codgar);
    if(numeroLotti > 1000){
      erroriBloccanti.add(CTR_TROPPI_LOTTI);
    }
    if(numeroLotti <= 0){
      erroriBloccanti.add(CTR_NESSUN_LOTTO);
    }


    String selectTORNGARE = null;

    if(genere == 2){
      selectTORNGARE = "select torn.cenint, "
        + " gare.not_gar, "
        + " torn.tipgen, "
        + " torn.codnuts, "
        + " torn.pcodoc, "
        + " torn.pcopre, "
        + " torn.pcooff, "
        + " gare.tipgarg, "
        //NEW ENTRIES
        + " torn.offlot, "
        + " torn.nofdit, "
        + " torn.ngadit, "
        + " torn.accappub, "
        + " torn.ammvar, "
        + " torn.apfinfc, "
        + " torn.progeu, "
        + " torn.iterga, "
        + " torn.dtepar, "
        + " torn.otepar, "
        + " torn.dteoff, "
        + " torn.oteoff, "
        + " torn.desoff, " //nuovo
        + " torn.oesoff, " //nuovo
        + " torn.prourg, "
        + " torn.motacc, "
        + " torn.accqua, "
        + " torn.aqdurata "
        + " from torn, gare where torn.codgar = gare.codgar1 and torn.codgar = ?";}
      else{
        selectTORNGARE = "select torn.cenint, "
          + " torn.destor, "
          + " torn.tipgen, "
          + " torn.codnuts, "
          + " torn.pcodoc, "
          + " torn.pcopre, "
          + " torn.pcooff, "
          + " torn.tipgar, "
          //NEW ENTRIES
          + " torn.offlot, "
          + " torn.nofdit, "
          + " torn.ngadit, "
          + " torn.accappub, "
          + " torn.ammvar, "
          + " torn.apfinfc, "
          + " torn.progeu, "
          + " torn.iterga, "
          + " torn.dtepar, "
          + " torn.otepar, "
          + " torn.dteoff, "
          + " torn.oteoff, "
          + " torn.desoff, " //nuovo
          + " torn.oesoff, " //nuovo
          + " torn.prourg, "
          + " torn.motacc, "
          + " torn.accqua, "
          + " torn.aqdurata "
          + " from torn where torn.codgar = ?";}

    List datiTORNGARE = sqlManager.getVector(selectTORNGARE,
        new Object[] { codgar });

    if (datiTORNGARE != null && datiTORNGARE.size() > 0) {

      HashMap<String,Object> mapTemp;

      // Amministrazione
      String codein = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
          0).getValue();
      if (codein != null) {
        AuthorityType authority = AuthorityType.Factory.newInstance();
        mapTemp= new HashMap<String,Object>();
        mapTemp = this.getAuthority(codein);
        erroriBloccanti.addAll((ArrayList<String>) mapTemp.get(ERRORI_BLOCCANTI_KEY));
        erroriNonBloccanti.addAll((ArrayList<String>) mapTemp.get(ERRORI_NON_BLOCCANTI_KEY));
      } else {
        erroriBloccanti.add(CTR_AUTHORITY);
      }

      // Titolo del contratto e descrizione breve
      if(genere != 2){
        String title = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
            1).getValue();
        if (title != null) {
        } else {
            erroriBloccanti.add(CTR_OGGETTO);
        }
      }

      //controllo il campo not_gar
      erroriBloccanti.addAll(this.getControlloNOTGAR(codgar, genere));

      //controllo se la gara è OEPV e se sono valorizzati correttamente i criteti di valutazione
      mapTemp = this.controlloGoevGara(codgar,genere);
      String tempString = ((String) mapTemp.get(ERRORI_BLOCCANTI_KEY));
      if(tempString != null)erroriBloccanti.add(tempString);

      // Tipo di contratto
      Long type_contract = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 2).getValue();
      if (type_contract == null) {
        erroriBloccanti.add(CTR_TYPE_CONTRACT);
      }

      //controllo errori CPV
      ArrayList erroriTemp = this.getCPVGara(codgar, genere);
      erroriNonBloccanti.addAll(erroriTemp);

      // Sito o luogo principale di esecuzione: codice NUTS
      String site_nuts = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 3).getValue();
      if (site_nuts == null) {
        erroriNonBloccanti.add(CTR_CODNUTS);
      }

      //punto di contatto
      Long pcopre = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 5).getValue();
      if (pcopre != null) {
        String nompun = this.getNOMPUN(codein, pcopre);
        if (nompun != null) {
        }else{
          erroriBloccanti.add(CTR_PCOPRE_NOMPUN);
        }
        if(this.getPunticonError(codein, pcopre)){
          erroriNonBloccanti.add(CTR_PCOPRE_DATA);
        };
      }

      //punto di contatto
      Long pcodoc = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 4).getValue();
      if (pcodoc != null) {
        String nompun = this.getNOMPUN(codein, pcodoc);
        if (nompun != null) {
        }else{
          erroriBloccanti.add(CTR_PCODOC_NOMPUN);
        }
        if(this.getPunticonError(codein, pcodoc)){
          erroriNonBloccanti.add(CTR_PCODOC_DATA);
        };
      }

      //punto di contatto
      Long pcooff = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 6).getValue();
      if (pcooff != null) {
        String nompun = this.getNOMPUN(codein, pcooff);
        if (nompun != null) {
        }else{
          erroriBloccanti.add(CTR_PCOOFF_NOMPUN);
        }
        if(this.getPunticonError(codein, pcooff)){
          erroriNonBloccanti.add(CTR_PCOOFF_DATA);
        };
      }




      //Controllo su tipgar
      Long tipo = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 7).getValue();
      if (tipo != null) {
      }else{
        if(genere.longValue()==2)
          erroriBloccanti.add(CTR_GARE_TIPGAR);
        if(genere.longValue()==3 || genere.longValue()==1)
          erroriBloccanti.add(CTR_TORN_TIPGAR);
        if(genere.longValue()==11)
          erroriBloccanti.add(CTR_AVVISO_TIPOAVV);
      }

      //controllo errori CIG
      erroriTemp = this.getCIGGara(codgar, genere);
      erroriBloccanti.addAll(erroriTemp);

      //INSERIRE QUI I NUOVI CONTROLLI NON BLOCCANTI
      //SOLO SE LA GARA NON E' AVVISO


      // Offlot
      Long offlot = null;
      if(genere == 1 || genere == 3){
        offlot = (Long) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 8).getValue();
        if (offlot == null) {
          erroriNonBloccanti.add(CTR_OFFLOT);
        }
      }

      // nofdit
      if(offlot != null && offlot == 2){
        Long nofdit = (Long) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 9).getValue();
        if (nofdit == null) {
          erroriNonBloccanti.add(CTR_NOFDIT);
        }
      }

      // ngadit
      if(offlot != null && (offlot == 2 || offlot == 3)){
        Long ngadit = (Long) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 10).getValue();
        if (ngadit == null) {
          erroriNonBloccanti.add(CTR_NGADIT);
        }
      }

      // ammrin
      // desrin
      ArrayList<String> temp = getAmmrinDesrin(codgar, genere);
      erroriNonBloccanti.addAll(temp);

      // ammvar
      String ammvar = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 12).getValue();
      if (ammvar == null) {
        erroriNonBloccanti.add(CTR_AMMVAR);
      }

      //ammopz
      //desopz
      HashMap<String,ArrayList> tempMap = getAmmopzDesopz(codgar, genere);
      temp = tempMap.get("DESOPZ");
      erroriNonBloccanti.addAll(temp);
      temp = tempMap.get("AMMOPZ");
      erroriNonBloccanti.addAll(temp);

      // apfinfc
      String apfinfc = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 13).getValue();
      if (apfinfc == null) {
        erroriNonBloccanti.add(CTR_APFINFC);
      }

      // progeu
      if(apfinfc != null && apfinfc.equals("1")){
        String progeu = (String) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 14).getValue();
        if (progeu == null) {
          erroriNonBloccanti.add(CTR_PROGEU);
        }
      }

      // accapub
      String accapub = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 11).getValue();
      if (accapub == null) {
        erroriNonBloccanti.add(CTR_ACCAPUB);
      }

      Long iterga = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 15).getValue();

      // dtepar
      if(iterga != null &&(iterga == 2 || iterga == 4)){
        Date dtepar = (Date) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 16).getValue();
        String otepar = (String) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 17).getValue();
        if (dtepar == null || otepar == null) {
          erroriNonBloccanti.add(CTR_TEPAR);
        }
      }

      // dteoff oteoff
      if(iterga != null &&(iterga == 1)){
        Date dteoff = (Date) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 18).getValue();
        String oteoff = (String) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 19).getValue();
        if (dteoff == null || oteoff == null) {
          erroriNonBloccanti.add(CTR_TEOFF);
        }
      }

      // oesoff desoff
      if(iterga != null &&(iterga == 1)){
        Date desoff = (Date) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 20).getValue();
        String oesoff = (String) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 21).getValue();
        if (desoff == null || oesoff == null) {
          erroriNonBloccanti.add(CTR_ESOFF);
        }
      }

      //prourg
      String prourg = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 22).getValue();
      //motacc
      if(prourg != null && prourg.equals("1")){
        String motacc = (String) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 23).getValue();
        if(motacc == null){
          erroriNonBloccanti.add(CTR_MOTACC);
        }
      }

      //accqua
      String accqua = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 24).getValue();

      //teutil
      if(accqua == null || accqua.equals("2")){
        erroriTemp = this.getTeutilGara(codgar, genere);
        erroriNonBloccanti.addAll(erroriTemp);
      }

      //aqdurata
      if(accqua != null && accqua.equals("1")){
        Long aqdurata = (Long) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 25).getValue();
        if(aqdurata == null){
          erroriNonBloccanti.add(CTR_AQDURATA);
        }
      }
    }


    ByteArrayOutputStream baosBandoGara = new ByteArrayOutputStream();
    bandoGaraDocument.save(baosBandoGara);
    xml = baosBandoGara.toString();
    baosBandoGara.close();

    response.put(ERRORI_BLOCCANTI_KEY, erroriBloccanti);
    response.put(ERRORI_NON_BLOCCANTI_KEY, erroriNonBloccanti);
    response.put("xml",xml);

    if (logger.isDebugEnabled()) logger.debug("getXMLBandoGara: fine metodo");

    return response;
  }


  /**
   * Restituisce XML contenente i dati dell'avviso di aggiudicazione
   *
   * @param codgar
   * @return
   * @throws SQLException
   * @throws IOException
   */
  private HashMap<String,Object> getXMLAvvisoAggiudicazione(String codgar) throws SQLException,
      IOException, Exception {

    if (logger.isDebugEnabled())
      logger.debug("getXMLAvvisoAggiudicazione: inizio metodo");

    HashMap<String,Object> response = new HashMap<String,Object>();
    String xml = null;
    ArrayList<String> erroriBloccanti = new ArrayList<String>();
    ArrayList<String> erroriNonBloccanti = new ArrayList<String>();

    AvvisoAggiudicazioneDocument avvisoAggiudicazioneDocument = AvvisoAggiudicazioneDocument.Factory.newInstance();
    avvisoAggiudicazioneDocument.documentProperties().setEncoding("UTF-8");
    AvvisoAggiudicazioneType avvisoAggiudicazione = avvisoAggiudicazioneDocument.addNewAvvisoAggiudicazione();

    Long genere = this.getGENERE(codgar);
    if(codgar.indexOf("$")>=0){
      Long genereTmp = this.getGENEREGARE(codgar.substring(1));
      if(genereTmp!= null && genereTmp.longValue()==11)
        genere = new Long(11);
    }

    //controllo se la gara ha più di mille lotti
    int numeroLotti = this.getNumeroLotti(codgar);
    if(numeroLotti > 1000){
      erroriBloccanti.add(CTR_TROPPI_LOTTI);
    }
    if(numeroLotti <= 0){
      erroriBloccanti.add(CTR_NESSUN_LOTTO);
    }

    String selectTORNGARE = null;

    if(genere == 2){
      selectTORNGARE = "select torn.cenint, "
        + " gare.not_gar, "
        + " torn.tipgen, "
        + " torn.codnuts, "
        + " gare.tipgarg, "
        //NEW ENTRIES
        + " torn.accappub, "
        + " torn.apfinfc, "
        + " torn.progeu, "
        + " torn.prourg, "
        + " torn.motacc "
        + " from torn, gare where torn.codgar = gare.codgar1"
        + " and torn.codgar = ?";}
      else{
        selectTORNGARE = "select torn.cenint, "
          + " torn.destor, "
          + " torn.tipgen, "
          + " torn.codnuts, "
          + " torn.tipgar, "
          //NEW ENTRIES
          + " torn.accappub, "
          + " torn.apfinfc, "
          + " torn.progeu, "
          + " torn.prourg, "
          + " torn.motacc "
          + " from torn where torn.codgar = ?";
      }
    List datiTORNGARE = sqlManager.getVector(selectTORNGARE,
        new Object[] { codgar });

    if (datiTORNGARE != null && datiTORNGARE.size() > 0) {

      HashMap<String,Object> mapTemp;

      // Amministrazione
      String codein = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
          0).getValue();
      if (codein != null) {
        AuthorityType authority = AuthorityType.Factory.newInstance();
        mapTemp= new HashMap<String,Object>();
        mapTemp = this.getAuthority(codein);
        erroriBloccanti.addAll((ArrayList<String>) mapTemp.get(ERRORI_BLOCCANTI_KEY));
        erroriNonBloccanti.addAll((ArrayList<String>) mapTemp.get(ERRORI_NON_BLOCCANTI_KEY));
      } else {
        erroriBloccanti.add(CTR_AUTHORITY);
      }

      // oggetto della gara a lotti
      if(genere != 2){
        String destor = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
            1).getValue();
        if (destor == null) {
            erroriBloccanti.add(CTR_OGGETTO);
        }
      }

      //controllo se la gara è OEPV e se sono valorizzati correttamente i criteti di valutazione
      mapTemp = this.controlloGoevGara(codgar,genere);
      String tempString = ((String) mapTemp.get(ERRORI_BLOCCANTI_KEY));
      if(tempString != null)erroriBloccanti.add(tempString);

      // Tipo di contratto
      Long type_contract = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 2).getValue();
      if (type_contract == null) {
        erroriBloccanti.add(CTR_TYPE_CONTRACT);
      }

      //controllo errori CPV
      ArrayList erroriTemp = this.getCPVGara(codgar, genere);
      erroriNonBloccanti.addAll(erroriTemp);

      // Sito o luogo principale di esecuzione: codice NUTS
      String site_nuts = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 3).getValue();
      if (site_nuts == null) {
        erroriNonBloccanti.add(CTR_CODNUTS);
      }

      //controllo il campo not_gar
      erroriBloccanti.addAll(this.getControlloNOTGAR(codgar, genere));

      //Controllo su tipgar
      Long tipo = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 4).getValue();
      if (tipo != null) {
      }else{
        if(genere.longValue()==2)
          erroriBloccanti.add(CTR_GARE_TIPGAR);
        if(genere.longValue()==3 || genere.longValue()==1)
          erroriBloccanti.add(CTR_TORN_TIPGAR);
        if(genere.longValue()==11)
          erroriBloccanti.add(CTR_AVVISO_TIPOAVV);
      }

      //desopz
      //ammopz
      HashMap<String,ArrayList> tempMap = getAmmopzDesopz(codgar, genere);
      ArrayList<String >temp = tempMap.get("DESOPZ");
      erroriNonBloccanti.addAll(temp);
      temp = tempMap.get("AMMOPZ");
      erroriNonBloccanti.addAll(temp);

      // apfinfc
      String apfinfc = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 6).getValue();
      if (apfinfc == null) {
        erroriNonBloccanti.add(CTR_APFINFC);
      }

      // progeu
      if(apfinfc != null && apfinfc.equals("1")){
        String progeu = (String) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 7).getValue();
        if (progeu == null) {
          erroriNonBloccanti.add(CTR_PROGEU);
        }
      }

      // accapub
      String accapub = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 5).getValue();
      if (accapub == null) {
        erroriNonBloccanti.add(CTR_ACCAPUB);
      }

      //prourg
      String prourg = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 8).getValue();
      //motacc
      if(prourg != null && prourg.equals("1")){
        String motacc = (String) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 9).getValue();
        if(motacc == null){
          erroriNonBloccanti.add(CTR_MOTACC);
        }
      }

      //dattoa
      erroriTemp = this.getDattoaGara(codgar, genere);
      erroriNonBloccanti.addAll(erroriTemp);

      //controllo se i dati relativi alla ditta aggiudicataria sono ok
      HashMap<String,ArrayList<String>> mapTemp2 = this.controlloDatiImpr(codgar, genere);
      ArrayList<String> tempList = (mapTemp2.get(ERRORI_BLOCCANTI_KEY));
      if(tempList != null)erroriBloccanti.addAll(tempList);
      tempList = (mapTemp2.get(ERRORI_NON_BLOCCANTI_KEY));
      if(tempList != null)erroriNonBloccanti.addAll(tempList);

    }

    ByteArrayOutputStream baosAvvisoAggiudicazione = new ByteArrayOutputStream();
    avvisoAggiudicazioneDocument.save(baosAvvisoAggiudicazione);
    xml = baosAvvisoAggiudicazione.toString();
    baosAvvisoAggiudicazione.close();

    response.put(ERRORI_BLOCCANTI_KEY, erroriBloccanti);
    response.put(ERRORI_NON_BLOCCANTI_KEY, erroriNonBloccanti);
    response.put("xml",xml);

    if (logger.isDebugEnabled())
      logger.debug("getXMLAvvisoAggiudicazione: fine metodo");

    return response;

  }


  /**
   * Restituisce XML con i dati dell'avviso sul profilo di committente
   *
   * @param codgar
   * @return
   * @throws SQLException
   * @throws IOException
   * @throws Exception
   */
  private HashMap<String,Object> getXMLAvvisoProfiloCommittente(String codgar)
      throws SQLException, IOException, Exception {

    if (logger.isDebugEnabled())
      logger.debug("getXMLAvvisoProfiloCommittente: inizio metodo");

    HashMap<String,Object> response = new HashMap<String,Object>();
    String xml = null;
    ArrayList<String> erroriBloccanti = new ArrayList();
    ArrayList<String> erroriNonBloccanti = new ArrayList();

    AvvisoProfiloCommittenteDocument avvisoProfiloCommittenteDocument = AvvisoProfiloCommittenteDocument.Factory.newInstance();
    avvisoProfiloCommittenteDocument.documentProperties().setEncoding("UTF-8");
    AvvisoProfiloCommittenteType avvisoProfiloCommittente = avvisoProfiloCommittenteDocument.addNewAvvisoProfiloCommittente();

    Long genere = this.getGENERE(codgar);
    if(codgar.indexOf("$")>=0){
      Long genereTmp = this.getGENEREGARE(codgar.substring(1));
      if(genereTmp!= null && genereTmp.longValue()==11)
        genere = new Long(11);
    }


    String selectTORNGARE = null;

    switch (genere.intValue()) {
    case 1:
      selectTORNGARE = "select torn.cenint, "
        + " torn.codnuts, "
        + " torn.pcodoc, "
        + " torn.destor "
        + " from torn where codgar = ?";
      break;
    case 3:
      selectTORNGARE = "select torn.cenint, "
        + " torn.codnuts, "
        + " torn.pcodoc, "
        + " torn.destor "
        + " from torn where codgar = ?";
      break;
    case 2:
      selectTORNGARE = "select torn.cenint, "
        + " torn.codnuts, "
        + " torn.pcodoc "
        + " from torn where codgar = ?";
      break;
    case 11:
      selectTORNGARE = "select torn.cenint, "
        + " torn.codnuts, "
        + " torn.pcodoc, "
        + " gareavvisi.oggetto "
        + " from torn, gareavvisi where torn.codgar = gareavvisi.codgar "
        + " and torn.codgar = ?";
    break;
    }

    List datiTORNGARE = sqlManager.getVector(selectTORNGARE,
        new Object[] { codgar });

    if (datiTORNGARE != null && datiTORNGARE.size() > 0) {

      // Amministrazione
      String codein = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
          0).getValue();
      if (codein != null) {
        AuthorityType authority = AuthorityType.Factory.newInstance();
        HashMap mapTemp = new HashMap<String,Object>();
        mapTemp= new HashMap<String,Object>();
        mapTemp = this.getAuthority(codein);
        erroriBloccanti.addAll((ArrayList<String>) mapTemp.get(ERRORI_BLOCCANTI_KEY));
        erroriNonBloccanti.addAll((ArrayList<String>) mapTemp.get(ERRORI_NON_BLOCCANTI_KEY));
      } else {
        erroriBloccanti.add(CTR_AUTHORITY);
      }

      //controllo il campo not_gar
      if(genere != 11){
        erroriBloccanti.addAll(this.getControlloNOTGAR(codgar, genere));
      }

      // Sito o luogo principale di esecuzione: codice NUTS
      String site_nuts = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 1).getValue();
      if (site_nuts == null) {
        erroriNonBloccanti.add(CTR_CODNUTS);
      }

      //controllo errori CPV
      ArrayList erroriTemp = this.getCPVGara(codgar, genere);
      erroriNonBloccanti.addAll(erroriTemp);

      //punto di contatto
      Long pcodoc = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 2).getValue();
      if (pcodoc != null) {
        String nompun = this.getNOMPUN(codein, pcodoc);
        if (nompun != null) {
        }else{
          erroriBloccanti.add(CTR_PCODOC_NOMPUN);
        }
        // controllo dei dati sulla tabella punticon
        if(this.getPunticonError(codein, pcodoc)){
          erroriNonBloccanti.add(CTR_PCODOC_DATA);
        };
      }



      if(genere == 11){
        String oggetto = (String) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 3).getValue();
        if(oggetto == null){
          erroriBloccanti.add(CTR_OGGETTO_AVVISO);
        }
      }
      if(genere == 3 || genere == 1){
        String oggetto = (String) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 3).getValue();
        if(oggetto == null){
          erroriBloccanti.add(CTR_OGGETTO);
        }
      }
    }

    ByteArrayOutputStream baosAvvisoProfiloCommittente = new ByteArrayOutputStream();
    avvisoProfiloCommittenteDocument.save(baosAvvisoProfiloCommittente);
    xml = baosAvvisoProfiloCommittente.toString();
    baosAvvisoProfiloCommittente.close();

    response.put(ERRORI_BLOCCANTI_KEY, erroriBloccanti);
    response.put(ERRORI_NON_BLOCCANTI_KEY, erroriNonBloccanti);
    response.put("xml",xml);

    if (logger.isDebugEnabled())
      logger.debug("getXMLAvvisoProfiloCommittente: fine metodo");

    return response;

  }


  /* FUNZIONI*UTILI****FUNZIONI*UTILI****FUNZIONI*UTILI****FUNZIONI*UTILI****FUNZIONI*UTILI****FUNZIONI*UTILI****FUNZIONI*UTILI */


  private HashMap<String,ArrayList<String>> controlloDatiImpr(String codgar, Long genere) throws SQLException, GestoreException{
    String selectDittaAggiudicataria = "select gare.codiga, gare.ditta, gare.esineg, gare.ngara, gare1.aqoper from gare, gare1 where gare.codgar1 = ? and gare.ngara = gare1.ngara and (gare.genere is null or gare.genere != 3) order by gare.NGARA";
    String selectRagimp = "select coddic, impman from ragimp where codime9 = ?";
    String selectImpr = "select tipimp, nomimp from impr where impr.codimp = ?";
    ArrayList<String> erroriBloccanti = new ArrayList<String>();
    ArrayList<String> erroriNonBloccanti = new ArrayList<String>();
    boolean isFirstBloccCFIVA = true;
    boolean isFirstNonBloccLoc = true;
    boolean isFirstNonBloccTel = true;
    boolean isFirstNonBloccFax = true;
    boolean isAggiudicata = false;
    boolean isFirstErrorRagg = true;
    String erroreImpresaAggiudicatrice = CTR_LOTTI_NON_AGG;
    String erroreGeneraleBloccanteCFIVA = CTR_LOTTI_IMPR_AGG_CF_PIVA;
    String erroreGeneraleNonBloccanteLoc = CTR_LOTTI_IMPR_AGG_LOC;
    String erroreGeneraleNonBloccanteTel = CTR_LOTTI_IMPR_AGG_TEL;
    String erroreGeneraleNonBloccanteFax = CTR_LOTTI_IMPR_AGG_FAX;
    String erroreComponentiRaggr = CTR_COMPONENTI_LOTTI_RT;
    //gara a lotti plichi distinti
      List lottiNGARA = sqlManager.getListVector(selectDittaAggiudicataria,
          new Object[] { codgar });
      if (lottiNGARA != null && lottiNGARA.size() > 0) {
        //ESEGUIRE IL CONTROLLO SUI DATI DI OGNI IMPRESA DI OGNI LOTTO DI GARA
        for (int i = 0; i < lottiNGARA.size(); i++) {
          boolean erroreBloccanteLotto = false;
          boolean erroreNonBloccanteLotto = false;
          boolean erroreNonBloccanteTelLotto = false;
          boolean erroreNonBloccanteFaxLotto = false;

          String codiga = (String) SqlManager.getValueFromVectorParam(
              lottiNGARA.get(i), 0).getValue();
          String ditta = (String) SqlManager.getValueFromVectorParam(
              lottiNGARA.get(i), 1).getValue();
          Long esineg = (Long) SqlManager.getValueFromVectorParam(
              lottiNGARA.get(i), 2).getValue();
          String ngara = (String) SqlManager.getValueFromVectorParam(
              lottiNGARA.get(i), 3).getValue();
          Long aqoper = (Long) SqlManager.getValueFromVectorParam(
              lottiNGARA.get(i), 4).getValue();

          if(ditta != null && ditta.length()>0){
            isAggiudicata = true;
            List impresaAgg = sqlManager.getVector(selectImpr,
                new Object[] { ditta });
            if (impresaAgg != null && impresaAgg.size() > 0) {
             Long tipimp = (Long) SqlManager.getValueFromVectorParam(impresaAgg,
                 0).getValue();
             String nomimp = (String) SqlManager.getValueFromVectorParam(impresaAgg,
                 1).getValue();

             if(tipimp != null && (tipimp == 3 || tipimp == 10)){
               List impreseRT = sqlManager.getListVector(selectRagimp,
                   new Object[] { ditta });
               if (impreseRT != null && impreseRT.size() > 0) {

                 for (int j = 0; j < impreseRT.size(); j++) {
                   String coddic = (String) SqlManager.getValueFromVectorParam(
                       impreseRT.get(j), 0).getValue();
                   HashMap<String,Boolean> temp = controllaDatiImpresa(coddic);
                   if(temp.get(ERRORI_BLOCCANTI_KEY)){
                     erroreBloccanteLotto = true;
                   }
                   if(temp.get(ERRORI_NON_BLOCCANTI_KEY)){
                     erroreNonBloccanteLotto = true;
                   }
                   if(temp.get(ERRORI_NON_BLOCCANTI_TEL_KEY)){
                     erroreNonBloccanteTelLotto = true;
                   }
                   if(temp.get(ERRORI_NON_BLOCCANTI_FAX_KEY)){
                     erroreNonBloccanteFaxLotto = true;
                   }
                  }
                 if(impreseRT.size() < 2){
                   if(genere == 2){
                   erroriBloccanti.add(CTR_COMPONENTI_RT);
                   }
                   else{
                     if(!isFirstErrorRagg){
                       erroreComponentiRaggr = erroreComponentiRaggr + ", ";
                     }
                     isFirstErrorRagg = false;
                     erroreComponentiRaggr = erroreComponentiRaggr + codiga;
                   }
                 }
                }
              } else{
               HashMap<String,Boolean> temp = controllaDatiImpresa(ditta);
               if(temp.get(ERRORI_BLOCCANTI_KEY)){
                 erroreBloccanteLotto = true;
               }
               if(temp.get(ERRORI_NON_BLOCCANTI_KEY)){
                 erroreNonBloccanteLotto = true;
               }
               if(temp.get(ERRORI_NON_BLOCCANTI_TEL_KEY)){
                 erroreNonBloccanteTelLotto = true;
               }
               if(temp.get(ERRORI_NON_BLOCCANTI_FAX_KEY)){
                 erroreNonBloccanteFaxLotto = true;
               }
             }
             if(aqoper != null && aqoper.intValue()==2){
               List datiDitgaq = sqlManager.getListVector(
                   "select dittao from ditgaq where ngara = ?",
                   new Object[] { ngara });
               if(datiDitgaq != null && datiDitgaq.size()>0){
                 for(int j=0;j<datiDitgaq.size();j++){
                   String dittao = (String) SqlManager.getValueFromVectorParam(datiDitgaq.get(j),0).getValue();
                   Long tipimpAq = (Long) sqlManager.getObject(
                       "select tipimp from impr where codimp = ?",
                       new Object[] { dittao });
                   if(tipimpAq != null && (tipimpAq == 10 || tipimpAq == 3)){
                     List listRagimp = sqlManager.getListVector("select coddic from ragimp where codime9 = ?",
                         new Object[] { dittao });
                     for(int k=0; k<listRagimp.size(); k++) {
                       String dittaRT  = (String) SqlManager.getValueFromVectorParam(listRagimp.get(k), 0).getValue();
                       HashMap<String,Boolean> temp = controllaDatiImpresa(dittaRT);
                       if(temp.get(ERRORI_BLOCCANTI_KEY)){
                         erroreBloccanteLotto = true;
                       }
                       if(temp.get(ERRORI_NON_BLOCCANTI_KEY)){
                         erroreNonBloccanteLotto = true;
                       }
                       if(temp.get(ERRORI_NON_BLOCCANTI_TEL_KEY)){
                         erroreNonBloccanteTelLotto = true;
                       }
                       if(temp.get(ERRORI_NON_BLOCCANTI_FAX_KEY)){
                         erroreNonBloccanteFaxLotto = true;
                       }
                     }
                   }else{
                     HashMap<String,Boolean> temp = controllaDatiImpresa(dittao);
                     if(temp.get(ERRORI_BLOCCANTI_KEY)){
                       erroreBloccanteLotto = true;
                     }
                     if(temp.get(ERRORI_NON_BLOCCANTI_KEY)){
                       erroreNonBloccanteLotto = true;
                     }
                     if(temp.get(ERRORI_NON_BLOCCANTI_TEL_KEY)){
                       erroreNonBloccanteTelLotto = true;
                     }
                     if(temp.get(ERRORI_NON_BLOCCANTI_FAX_KEY)){
                       erroreNonBloccanteFaxLotto = true;
                     }
                   }
                 }
               }
             }
            }
          }
          else{
            if(esineg == null){
              if(genere == 2){
                erroriBloccanti.add(CTR_GARA_NON_AGG);
              }
            }else{
              isAggiudicata = true;
            }
          }
          if(erroreBloccanteLotto){
            if(genere == 2){
              erroriBloccanti.add(CTR_GARA_IMPR_AGG_CF_PIVA);
            }
            else{
              if(!isFirstBloccCFIVA){
                erroreGeneraleBloccanteCFIVA = erroreGeneraleBloccanteCFIVA + ", ";
              }
              isFirstBloccCFIVA = false;
              erroreGeneraleBloccanteCFIVA = erroreGeneraleBloccanteCFIVA + codiga;
            }
          }
          if(erroreNonBloccanteLotto){
            if(genere == 2){
              erroriNonBloccanti.add(CTR_GARA_IMPR_AGG_LOC);
            }
            else{
              if(!isFirstNonBloccLoc){
                erroreGeneraleNonBloccanteLoc = erroreGeneraleNonBloccanteLoc + ", ";
              }
              isFirstNonBloccLoc = false;
              erroreGeneraleNonBloccanteLoc = erroreGeneraleNonBloccanteLoc + codiga;
            }
          }
          if(erroreNonBloccanteTelLotto){
            if(genere == 2){
              erroriNonBloccanti.add(CTR_GARA_IMPR_AGG_TEL);
            }
            else{
              if(!isFirstNonBloccTel){
                erroreGeneraleNonBloccanteTel = erroreGeneraleNonBloccanteTel + ", ";
              }
              isFirstNonBloccTel = false;
              erroreGeneraleNonBloccanteTel = erroreGeneraleNonBloccanteTel + codiga;
            }
          }
          if(erroreNonBloccanteFaxLotto){
            if(genere == 2){
              erroriNonBloccanti.add(CTR_GARA_IMPR_AGG_FAX);
            }
            else{
              if(!isFirstNonBloccFax){
                erroreGeneraleNonBloccanteFax = erroreGeneraleNonBloccanteFax + ", ";
              }
              isFirstNonBloccFax = false;
              erroreGeneraleNonBloccanteFax = erroreGeneraleNonBloccanteFax + codiga;
            }
          }
        }
        if(genere != 2){
          if(!isFirstNonBloccLoc){
            erroriNonBloccanti.add(erroreGeneraleNonBloccanteLoc);
          }
          if(!isFirstNonBloccTel){
            erroriNonBloccanti.add(erroreGeneraleNonBloccanteTel);
          }
          if(!isFirstNonBloccFax){
            erroriNonBloccanti.add(erroreGeneraleNonBloccanteFax);
          }
          if(!isFirstBloccCFIVA){
            erroriBloccanti.add(erroreGeneraleBloccanteCFIVA);
          }
          if(!isAggiudicata){
            erroriBloccanti.add(erroreImpresaAggiudicatrice);
          }
          if(!isFirstErrorRagg){
            erroriBloccanti.add(erroreComponentiRaggr);
          }
        }
      }
    HashMap<String,ArrayList<String>> risposta = new HashMap<String,ArrayList<String>>();
    risposta.put(ERRORI_BLOCCANTI_KEY,erroriBloccanti);
    risposta.put(ERRORI_NON_BLOCCANTI_KEY,erroriNonBloccanti);
    return risposta;
  }

  private HashMap<String,Boolean> controllaDatiImpresa(String codiceImpresa) throws SQLException, GestoreException{
    String selectImpr = "select impr.CFIMP, impr.pivimp, impr.nazimp, impr.locimp, impr.proimp, impr.telimp, impr.faximp from impr where impr.codimp = ?";

    HashMap risposta = new HashMap<String,Boolean>();
    risposta.put(ERRORI_BLOCCANTI_KEY, false);
    risposta.put(ERRORI_NON_BLOCCANTI_KEY, false);
    risposta.put(ERRORI_NON_BLOCCANTI_FAX_KEY, false);
    risposta.put(ERRORI_NON_BLOCCANTI_TEL_KEY, false);

    List impresaAgg = sqlManager.getVector(selectImpr,
        new Object[] { codiceImpresa });
    if (impresaAgg != null && impresaAgg.size() > 0) {
       String imprCfimp = (String) SqlManager.getValueFromVectorParam(impresaAgg,
            0).getValue();
       String imprPivimp = (String) SqlManager.getValueFromVectorParam(impresaAgg,
           1).getValue();
       Long imprNazimp = (Long) SqlManager.getValueFromVectorParam(impresaAgg,
           2).getValue();
       String imprLocimpr = (String) SqlManager.getValueFromVectorParam(impresaAgg,
           3).getValue();

       if(imprNazimp == null || imprNazimp == 1){
         if((imprCfimp == null || (!UtilityFiscali.isValidCodiceFiscale(imprCfimp) && !UtilityFiscali.isValidPartitaIVA(imprCfimp))) &&  (imprPivimp == null || !UtilityFiscali.isValidPartitaIVA(imprPivimp))){
         risposta.put(ERRORI_BLOCCANTI_KEY, true);
         }
         String proimp = (String) SqlManager.getValueFromVectorParam(impresaAgg,
             4).getValue();
         if(proimp == null){
           risposta.put(ERRORI_NON_BLOCCANTI_KEY, true);
         }

       }
       else{
         if(imprNazimp != null && imprNazimp != 1 && (imprCfimp == null &&  imprPivimp == null)){
             risposta.put(ERRORI_BLOCCANTI_KEY, true);
         }
       }
       if(imprLocimpr == null){
         risposta.put(ERRORI_NON_BLOCCANTI_KEY, true);
       }

       String imprTelimp = (String) SqlManager.getValueFromVectorParam(impresaAgg,
           5).getValue();
       String imprFaximp = (String) SqlManager.getValueFromVectorParam(impresaAgg,
           6).getValue();

       if(!validazionePhoneFax(imprTelimp)){
         risposta.put(ERRORI_NON_BLOCCANTI_TEL_KEY, true);
       };
       if(!validazionePhoneFax(imprFaximp)){
         risposta.put(ERRORI_NON_BLOCCANTI_FAX_KEY, true);
       };
    }
    return risposta;
  }



  /**
   * Ricava i codici CPV per la gara:
   *
   * Se la gara (non il lotto) è di genere 2 si
   * devono considerare i codici CPV dell'unico lotto associato.
   * @param codgar
   * @param genere
   * @return
   * @throws SQLException
   */
  private ArrayList<String> getCPVGara(String codgar, Long genere) throws SQLException {

    if (logger.isDebugEnabled()) logger.debug("getCPVGara: inizio metodo");
    ArrayList<String> errori = new ArrayList<String>();

    String select ="select ngara, codiga from gare where codgar1 = ?";
    if(genere == 3){
      select += " and ngara != codgar1";
    }
    List lottiNGARA = sqlManager.getListVector(select, new Object[] { codgar });
    String errorMessage = CTR_CPV_LOTTO;
    boolean isFirst = true;
    if (lottiNGARA != null && lottiNGARA.size() > 0) {
      for (int i = 0; i < lottiNGARA.size(); i++) {
        String ngara = (String) SqlManager.getValueFromVectorParam(
            lottiNGARA.get(i), 0).getValue();
        String codiga = (String) SqlManager.getValueFromVectorParam(
            lottiNGARA.get(i), 1).getValue();
        if(!this.getControlloCPVLotto(ngara)){
          if(genere == 1 || genere == 3){
            if(!isFirst){
              errorMessage = errorMessage + ", ";
            }
            errorMessage = errorMessage + codiga;
          }
          else{
            errorMessage = CTR_CPV;
          }
          isFirst = false;
        }
      }
      if(!isFirst){
        errori.add(errorMessage);
      }
    }

    if (logger.isDebugEnabled()) logger.debug("getCPVGara: fine metodo");
    return errori;
  }

  /**
   * Ricava i codici CIG per la gara:
   *
   * Se la gara (non il lotto) è di genere 2 si
   * devono considerare i codici CPV dell'unico lotto associato.
   * Se la gara (non il lotto) è di genere 3 si
   * devono considerare i CPV collegati alla riga fittizia
   * @param codgar
   * @param genere
   * @return
   * @throws SQLException
   */
  private ArrayList<String> getCIGGara(String codgar, Long genere) throws SQLException {

    if (logger.isDebugEnabled()) logger.debug("getCIGGara: inizio metodo");
    ArrayList<String> errori = new ArrayList<String>();

    CPVType cpvLotto = CPVType.Factory.newInstance();

      List lottiNGARA = sqlManager.getListVector("select codcig, codiga from gare where codgar1 = ? and (genere is null or genere != 3) order by NGARA",
          new Object[] { codgar });
      String errorMessage = CTR_CIG_LOTTO;
      boolean isFirst = true;
      if (lottiNGARA != null && lottiNGARA.size() > 0) {
        for (int i = 0; i < lottiNGARA.size(); i++) {
          String codcig = (String) SqlManager.getValueFromVectorParam(
              lottiNGARA.get(i), 0).getValue();
          String codiga = (String) SqlManager.getValueFromVectorParam(
              lottiNGARA.get(i), 1).getValue();
          if(codcig == null){
            if(genere != 2){
              if(!isFirst){
                errorMessage = errorMessage + ", ";
              }
              errorMessage = errorMessage + codiga;
            }
            else{
              errorMessage = CTR_CIG;
            }
            isFirst = false;
          }
        }
        if(!isFirst){
          errori.add(errorMessage);
        }
      }
    if (logger.isDebugEnabled()) logger.debug("getCIGGara: fine metodo");
    return errori;
  }

  /**
   * Ricava i codici CIG per la gara:
   *
   * Se la gara (non il lotto) è di genere 2 si
   * devono considerare i codici CPV dell'unico lotto associato.
   * Se la gara (non il lotto) è di genere 3 si
   * devono considerare i CPV collegati alla riga fittizia
   * @param codgar
   * @param genere
   * @return
   * @throws SQLException
   */
  private ArrayList<String> getAmmrinDesrin(String codgar, Long genere) throws SQLException {

    if (logger.isDebugEnabled()) logger.debug("getAmmrinDesrinGara: inizio metodo");
    ArrayList<String> errori = new ArrayList<String>();

    CPVType cpvLotto = CPVType.Factory.newInstance();

      List lottiNGARA = sqlManager.getListVector("select gare1.desrin, gare1.ammrin, gare.codiga from gare, gare1 where gare.codgar1 = ? and gare.ngara = gare1.ngara and (gare.genere is null or gare.genere != 3) order by GARE.NGARA",
          new Object[] { codgar });
      String errorDesrin = CTR_DESRIN_LOTTI;
      String errorAmmrin = CTR_AMMRIN_LOTTI;
      boolean isFirstDesrin = true;
      boolean isFirstAmmrin = true;
      if (lottiNGARA != null && lottiNGARA.size() > 0) {
        for (int i = 0; i < lottiNGARA.size(); i++) {
          String desrin = (String) SqlManager.getValueFromVectorParam(
              lottiNGARA.get(i), 0).getValue();
          String ammrin = (String) SqlManager.getValueFromVectorParam(
              lottiNGARA.get(i), 1).getValue();
          String codiga = (String) SqlManager.getValueFromVectorParam(
              lottiNGARA.get(i), 2).getValue();
          if(ammrin == null){
            if(genere != 2){
              if(!isFirstAmmrin){
                errorAmmrin = errorAmmrin + ", ";
              }
              errorAmmrin = errorAmmrin + codiga;
            }
            else{
              errorAmmrin = CTR_AMMRIN;
            }
            isFirstAmmrin = false;
          }
          else{
            if(ammrin.equals("1") && desrin == null){
              if(genere != 2){
                if(!isFirstDesrin){
                  errorDesrin = errorDesrin + ", ";
                }
                errorDesrin = errorDesrin + codiga;
              }
              else{
                errorDesrin = CTR_DESRIN;
              }
              isFirstDesrin = false;
            }
          }
        }
        if(!isFirstAmmrin){
          errori.add(errorAmmrin);
        }
        if(!isFirstDesrin){
          errori.add(errorDesrin);
        }
      }
    if (logger.isDebugEnabled()) logger.debug("getAmmrinDesrinGara: fine metodo");
    return errori;
  }

  /**
   * Ricava i codici CIG per la gara:
   *
   * Se la gara (non il lotto) è di genere 2 si
   * devono considerare i codici CPV dell'unico lotto associato.
   * Se la gara (non il lotto) è di genere 3 si
   * devono considerare i CPV collegati alla riga fittizia
   * @param codgar
   * @param genere
   * @return
   * @throws SQLException
   */
  private HashMap<String,ArrayList> getAmmopzDesopz(String codgar, Long genere) throws SQLException {

    if (logger.isDebugEnabled()) logger.debug("getAmmopzDesopz: inizio metodo");
    ArrayList<String> erroriDesopz = new ArrayList<String>();
    ArrayList<String> erroriAmmopz = new ArrayList<String>();
    HashMap<String,ArrayList> errori = new HashMap<String,ArrayList>();
    CPVType cpvLotto = CPVType.Factory.newInstance();

      List lottiNGARA = sqlManager.getListVector("select gare1.desopz, gare1.ammopz, gare.codiga from gare, gare1 where gare.codgar1 = ? and gare.ngara = gare1.ngara and (gare.genere is null or gare.genere != 3)",
          new Object[] { codgar });
      String errorDesopz = CTR_DESOPZ_LOTTI;
      String errorAmmopz = CTR_AMMOPZ_LOTTI;
      boolean isFirstDesopz = true;
      boolean isFirstAmmopz = true;
      if (lottiNGARA != null && lottiNGARA.size() > 0) {
        for (int i = 0; i < lottiNGARA.size(); i++) {
          String desopz = (String) SqlManager.getValueFromVectorParam(
              lottiNGARA.get(i), 0).getValue();
          String ammopz = (String) SqlManager.getValueFromVectorParam(
              lottiNGARA.get(i), 1).getValue();
          String codiga = (String) SqlManager.getValueFromVectorParam(
              lottiNGARA.get(i), 2).getValue();
          if(ammopz == null){
            if(genere != 2){
              if(!isFirstAmmopz){
                errorAmmopz = errorAmmopz + ", ";
              }
              errorAmmopz = errorAmmopz + codiga;
            }
            else{
              errorAmmopz = CTR_AMMOPZ;
            }
            isFirstAmmopz = false;
          }
          else{
            if(ammopz.equals("1") && desopz == null){
              if(genere != 2){
                if(!isFirstDesopz){
                  errorDesopz = errorDesopz + ", ";
                }
                errorDesopz = errorDesopz + codiga;
              }
              else{
                errorDesopz = CTR_DESOPZ;
              }
              isFirstDesopz = false;
            }
          }
        }
        if(!isFirstDesopz){
          erroriDesopz.add(errorDesopz);
        }
        if(!isFirstAmmopz){
          erroriAmmopz.add(errorAmmopz);
        }
      }
    if (logger.isDebugEnabled()) logger.debug("getAmmopzDesopz: fine metodo");
    errori.put("AMMOPZ", erroriAmmopz);
    errori.put("DESOPZ", erroriDesopz);
    return errori;
  }



  /**
   * Ricava i codici TEUTIL per la gara:
   *
   * Se la gara (non il lotto) è di genere 2 si
   * devono considerare i codici CPV dell'unico lotto associato.
   * Se la gara (non il lotto) è di genere 3 si
   * devono considerare i CPV collegati alla riga fittizia
   * @param codgar
   * @param genere
   * @return
   * @throws SQLException
   */
  private ArrayList<String> getTeutilGara(String codgar, Long genere) throws SQLException {

    if (logger.isDebugEnabled()) logger.debug("getTeutilGara: inizio metodo");
    ArrayList<String> errori = new ArrayList<String>();

     String queryDurata = "select teutil, codiga from gare where codgar1 = ?";
     if(genere == 3){
       queryDurata = "select teutil, codiga from gare where codgar1 = ? and genere = 3";
     }
      List lottiNGARA = sqlManager.getListVector(queryDurata,
          new Object[] { codgar });
      String errorMessage = CTR_TEUTIL_LOTTO;
      boolean isFirst = true;
      if (lottiNGARA != null && lottiNGARA.size() > 0) {
        for (int i = 0; i < lottiNGARA.size(); i++) {
          Long teutil = (Long) SqlManager.getValueFromVectorParam(
              lottiNGARA.get(i), 0).getValue();
          String codiga = (String) SqlManager.getValueFromVectorParam(
              lottiNGARA.get(i), 1).getValue();
          if(teutil == null){
            if(genere == 1){
              if(!isFirst){
                errorMessage = errorMessage + ", ";
              }
              errorMessage = errorMessage + codiga;
            }
            else{
              errorMessage = CTR_TEUTIL;
            }
            isFirst = false;
          }
        }
        if(!isFirst){
          errori.add(errorMessage);
        }
      }
    if (logger.isDebugEnabled()) logger.debug("getTeutilGara: fine metodo");
    return errori;
  }


  /**
   * Ricava i codici CPV per la gara:
   *
   * Se la gara (non il lotto) è di genere 2 si
   * devono considerare i codici CPV dell'unico lotto associato.
   * Se la gara (non il lotto) è di genere 3 si
   * devono considerare i CPV collegati alla riga fittizia
   * @param codgar
   * @param genere
   * @return
   * @throws SQLException
   */
  private ArrayList<String> getDattoaGara(String codgar, Long genere) throws SQLException {

    if (logger.isDebugEnabled()) logger.debug("getDattoaGara: inizio metodo");
    ArrayList<String> errori = new ArrayList<String>();
    String errorMessage = CTR_DATTOA_LOTTO;
    boolean isFirst= true;

    List lottiNGARA = sqlManager.getListVector("select ngara, codiga from gare where codgar1 = ? and (genere is null or genere != 3) order by NGARA",
        new Object[] { codgar });
    if (lottiNGARA != null && lottiNGARA.size() > 0) {
      for (int i = 0; i < lottiNGARA.size(); i++) {
        String ngara = (String) SqlManager.getValueFromVectorParam(
            lottiNGARA.get(i), 0).getValue();
        String codiga = (String) SqlManager.getValueFromVectorParam(
            lottiNGARA.get(i), 1).getValue();
        if(!this.getControlloDattoaLotto(ngara)){
          if(genere == 1){
            if(!isFirst){
              errorMessage = errorMessage + ", ";
            }
            errorMessage = errorMessage + codiga;
            }
          else{
            errori.add(CTR_DATTOA);
            return errori;
          }
          isFirst = false;
        }
      }
      if(!isFirst){
        errori.add(errorMessage);
      }
    }
    if (logger.isDebugEnabled()) logger.debug("getDattoaGara: fine metodo");
    return errori;
  }


  /**
   * Controlla se sono valorizzati i criteri di valutazione,
   * se gara divisa in lotti eseguo il controllo per ogni lotto
   *
   * @param codgar
   * @param genere
   * @return
   * @throws SQLException
   */
  private HashMap<String,Object> controlloGoevGara(String codgar, Long genere) throws SQLException {

    if (logger.isDebugEnabled()) logger.debug("controlloGoevGara: inizio metodo");

    boolean trovatoErroreBloccante = false;
    boolean trovatoErroreNonBloccante = false;
    String messaggioErroreBloccante = null;
    HashMap<String,Object> errors = new HashMap<String,Object>();

    messaggioErroreBloccante = CTR_GOEV_LOTTI;

      List lottiNGARA = sqlManager.getListVector("select modlicg, ngara, codiga from gare where codgar1 = ? and (genere is null or genere != 3) order by NGARA",
          new Object[] { codgar });
      if (lottiNGARA != null && lottiNGARA.size() > 0) {
        for (int i = 0; i < lottiNGARA.size(); i++) {
          Long modlicg = (Long) SqlManager.getValueFromVectorParam(
              lottiNGARA.get(i), 0).getValue();
          String ngara = (String) SqlManager.getValueFromVectorParam(
              lottiNGARA.get(i), 1).getValue();
          String codiga = (String) SqlManager.getValueFromVectorParam(
              lottiNGARA.get(i), 2).getValue();
          if(modlicg != null && modlicg == 6){

            if ((new Long(2)).equals(genere)) {
              boolean erroreTrovato = this.controlloGoevLotto(ngara);
              if(erroreTrovato){
                //c'è un errore in questo lotto
                trovatoErroreBloccante = true;
                messaggioErroreBloccante = CTR_GOEV_GARA;
              }
              if(trovatoErroreBloccante){errors.put(ERRORI_BLOCCANTI_KEY,messaggioErroreBloccante);}
              return errors;
            }
            else{
              boolean erroreTrovato = this.controlloGoevLotto(ngara);
              if(erroreTrovato){
                //c'è un errore in questo lotto
                if(trovatoErroreBloccante){
                  messaggioErroreBloccante = messaggioErroreBloccante + ", ";
                }
                trovatoErroreBloccante = true;
                messaggioErroreBloccante = messaggioErroreBloccante + codiga;
              }
            }
          }
        }
      }

    if(trovatoErroreBloccante){errors.put(ERRORI_BLOCCANTI_KEY,messaggioErroreBloccante);}
    if (logger.isDebugEnabled()) logger.debug("controlloGoevGara: fine metodo");

    return errors;
  }

  /**
   *
   * @param ngara
   * @return
   * @throws SQLException
   */
  private boolean controlloGoevLotto(String ngara) throws SQLException {
    Double maxPunTecnico = pgManager.getSommaPunteggioTecnico(ngara);
    Double maxPunEconomico = pgManager.getSommaPunteggioEconomico(ngara);
    if(maxPunTecnico == null){
      return true;
    }
    if(maxPunEconomico == null){
      if(maxPunTecnico != 100){
        return true;
        }
    }else{
      if((maxPunTecnico + maxPunEconomico) != 100){
        return true;
      }
    }
    return false;
  }


  /**
   * Estrae il campo GENERE:
   * <ul>
   * <li>1 – gara divisa in lotti con offerte distinte</li>
   * <li>2 – gara a lotto unico</li>
   * <li>3 – gara divisa in lotti con offerta unica</li>
   * </ul>
   *
   * @param codgar
   * @return
   * @throws SQLException
   */
  private Long getGENERE(String codgar) throws SQLException {
    Long genere = (Long) sqlManager.getObject(
        "select genere from v_gare_torn where codgar = ?",
        new Object[] { codgar });
    return genere;
  }


  /**
   * Estrae il campo GENERE dell'entita GARE:
   * <ul>
   * <li>1 – gara divisa in lotti con offerte distinte</li>
   * <li>2 – gara a lotto unico</li>
   * <li>3 – gara divisa in lotti con offerta unica</li>
   * <li>11 – Avvisi</li>
   * </ul>
   *
   * @param codgar
   * @return
   * @throws SQLException
   */
  private Long getGENEREGARE(String ngara) throws SQLException {
    Long genere = (Long) sqlManager.getObject(
        "select genere from gare where ngara = ?",
        new Object[] { ngara });
    return genere;
  }


  /**
   *
   * @param codgar
   * @return
   * @throws SQLException
   */
    private String getNOMPUN(String codein, Long pcodoc) throws SQLException {
      String nompun = (String) sqlManager.getObject(
          "select nompun from punticon where codein = ? and numpun = ?",
          new Object[] { codein, pcodoc });
      return nompun;
    }



    /**
     *
     * @param codein
     * @param pcodoc
     * @return
     * @throws SQLException
     */
    private boolean getPunticonError(String codein, Long pcodoc) throws SQLException {

    String selectPunticonData = "select citein, proein, codnaz, emaiin, indweb from punticon where codein = ? and numpun = ?";
    boolean trovatoErrore = false;
    String errore = "Alcune voci relative al punto di contatto non sono state inserite ";
    //String colonneNonCompilate ="( ";
    List datiGARE = sqlManager.getVector(selectPunticonData,
        new Object[] { codein, pcodoc });
    if (datiGARE != null && datiGARE.size() > 0) {
      String citein = (String) SqlManager.getValueFromVectorParam(datiGARE, 0).getValue();
      if(citein == null ){
        trovatoErrore = true;
        //colonneNonCompilate = colonneNonCompilate + "citein"
        }
      String proein = (String) SqlManager.getValueFromVectorParam(datiGARE, 1).getValue();
      if(proein == null ){
        trovatoErrore = true;
        }
      Long codnaz = (Long) SqlManager.getValueFromVectorParam(datiGARE, 2).getValue();
      if(codnaz == null ){
        trovatoErrore = true;
        }
      String emaiin = (String) SqlManager.getValueFromVectorParam(datiGARE, 3).getValue();
      if(emaiin == null ){
        trovatoErrore = true;
        }
      String indweb = (String) SqlManager.getValueFromVectorParam(datiGARE, 4).getValue();
      if(indweb == null ){
        trovatoErrore = true;
        }
      }
    return trovatoErrore;
  }


  /**
   * Restituisce la concatenazione (ciclando su tutti i lotti) del luogo di
   * esecuzione costituito da provincia, comune e localita'
   *
   * @param codgar
   * @return
   * @throws SQLException
   */
  private String getSITELABEL(String codgar) throws SQLException {

    String site_label = "";

    String selectGARE = "select prosla, loclav, nomssl from gare where codgar1 = ? and"
        + " (genere is null or genere in (1,2)) "
        + " order by ngara ";
    List datiGARE = sqlManager.getListVector(selectGARE,
        new Object[] { codgar });
    if (datiGARE != null && datiGARE.size() > 0) {
      for (int i = 0; i < datiGARE.size(); i++) {
        if (i > 0 && site_label != "") site_label += ", ";
        String provincia = (String) SqlManager.getValueFromVectorParam(
            datiGARE.get(i), 0).getValue();
        String comune = (String) SqlManager.getValueFromVectorParam(
            datiGARE.get(i), 1).getValue();
        String localita = (String) SqlManager.getValueFromVectorParam(
            datiGARE.get(i), 2).getValue();

        if (provincia != null) site_label += provincia;
        if (comune != null) site_label += " - " + comune;
        if (localita != null) site_label += " - " + localita;
      }
    }
    return site_label;
  }

  /**
   * Gestione oggetto CPV (Principale e complementare) per un lotto
   *
   * @param ngara
   * @return
   * @throws SQLException
   */
  private boolean getControlloCPVLotto(String ngara) throws SQLException {
    String selectGARCPV = "select codcpv from garcpv where ngara = ? and tipcpv = '1' order by numcpv";
    String cpvMain = (String) sqlManager.getObject(selectGARCPV,
        new Object[] { ngara });
    if (cpvMain == null) {
      return false;
    }
    else{
      return true;
    }
  }


  /**
   * Gestione oggetto CPV (Principale e complementare) per un lotto
   *
   * @param ngara
   * @return
   * @throws SQLException
   */
  private boolean getControlloDattoaLotto(String ngara) throws SQLException {
    String selectDattoa = "select dattoa, esineg, ditta from gare where ngara = ?";
    List dati = sqlManager.getVector(selectDattoa,
        new Object[] { ngara });
    if (dati != null && dati.size() > 0) {
      Date dattoa = (Date) SqlManager.getValueFromVectorParam(dati, 0).getValue();
      Long esineg = (Long) SqlManager.getValueFromVectorParam(dati, 1).getValue();
      String ditta = (String) SqlManager.getValueFromVectorParam(dati, 2).getValue();
      if (esineg == null && dattoa == null && ditta != null) {
        return false;
      }
      else return true;
    }
    return true;
  }

  /**
   * Gestione dell'amministrazione aggiudicatrice
   * (ufficio intestatario)
   *
   * @param codein
   * @return
   * @throws SQLException
   */
  private HashMap<String,Object> getAuthority(String codein) throws SQLException,
      Exception {

    if (logger.isDebugEnabled()) logger.debug("getAuthority: inizio metodo");

    HashMap<String,Object> response = new HashMap<String,Object>();
    ArrayList<String> erroriBloccanti = new ArrayList<String>();
    ArrayList<String> erroriNonBloccanti = new ArrayList<String>();
    AuthorityType authority = AuthorityType.Factory.newInstance();

    String selectUFFINT = "select nomein, "
        + "cfein, "
        + "ivaein "
        + "from uffint where codein = ?";

    List datiUFFINT = sqlManager.getVector(selectUFFINT,
        new Object[] { codein });

    if (datiUFFINT != null && datiUFFINT.size() > 0) {
      String nomein = (String) SqlManager.getValueFromVectorParam(datiUFFINT, 0).getValue();
      if (nomein != null) {
        authority.setNOMEIN(nomein);
      } else {
        erroriBloccanti.add(CTR_AUTHORITY_NOMEIN);
      }

      String cfein = (String) SqlManager.getValueFromVectorParam(datiUFFINT, 1).getValue();
      if (cfein != null) {
        if (UtilityFiscali.isValidPartitaIVA(cfein) || UtilityFiscali.isValidCodiceFiscale(cfein)){
          authority.setCFEIN(cfein);
        } else {
          erroriBloccanti.add(CTR_AUTHORITY_CF_VALIDO);
        }
      }
      String ivaein = (String) SqlManager.getValueFromVectorParam(datiUFFINT, 2).getValue();
      if (ivaein != null) {
        if (UtilityFiscali.isValidPartitaIVA(ivaein)){
          authority.setIVAEIN(ivaein);
        } else {
          erroriBloccanti.add(CTR_AUTHORITY_PI_VALIDO);
        }
      }
      if (cfein == null && ivaein == null) {
        erroriBloccanti.add(CTR_AUTHORITY_CFPI);
      }
    }
    response.put(ERRORI_BLOCCANTI_KEY, erroriBloccanti);
    response.put(ERRORI_NON_BLOCCANTI_KEY, erroriNonBloccanti);

    if (logger.isDebugEnabled()) logger.debug("getAuthority: fine metodo");
    return response;
  }

  /**
   * Effettua la conversione del campo TIPOAPP in TIPGEN, considerando
   * che se tipoapp presenta la combinazione di più valori viene presa
   * in considerazione solo un valore
   * es. tipoapp=110 (Lavori + Forniture) viene restituito solo il valore
   * 1(Lavori)
   *
   * @param codgar
   * @return
   * @throws SQLException
   * @throws IOException
   * @throws Exception
   */
  private Long getTipgenDaTipoapp(Long tipoapp) {
    Long ret = null;
    if(tipoapp.longValue()>=100)
      ret=new Long(1);
    else if(tipoapp.longValue()>=10 && tipoapp.longValue()<100)
      ret=new Long(2);
    else if(tipoapp.longValue()<10)
      ret=new Long(3);
    return ret;
  }

  /**
   * Effettua la conversione del campo TIPOAPP in TIPGEN, considerando
   * che se tipoapp presenta la combinazione di più valori viene presa
   * in considerazione solo un valore
   * es. tipoapp=110 (Lavori + Forniture) viene restituito solo il valore
   * 1(Lavori)
   *
   * @param codgar
   * @return
   * @throws SQLException
   * @throws IOException
   * @throws Exception
   */
    private int getNumeroLotti(String codgar) throws SQLException {
      String queryCount = "select ngara from gare where codgar1 = ? and (genere is null or genere != 3)";
      List count = sqlManager.getListVector(queryCount,
          new Object[] { codgar });
      return count.size();
    }

    private ArrayList<String> getControlloNOTGAR(String codgar, Long genere) throws SQLException{
      ArrayList<String> errors = new ArrayList<String>();
      String MessageError = CTR_LOTTO_TITLE;
      boolean isFirst = true;
      String selectGARE = "select codiga, "
          + " not_gar "
          + " from gare where codgar1 = ?  and (genere is null or genere in (1,2)) "
          + " order by ngara";
      if(genere == null || genere == 11){
        return errors;
      }
      List datiGARE = sqlManager.getListVector(selectGARE,
          new Object[] { codgar });

      if (datiGARE != null && datiGARE.size() > 0) {

        for (int i = 0; i < datiGARE.size(); i++) {

          String codiga = (String) SqlManager.getValueFromVectorParam(
              datiGARE.get(i), 0).getValue();
          String title = (String) SqlManager.getValueFromVectorParam(
              datiGARE.get(i), 1).getValue();

          if (title == null) {
            if(genere == 1 || genere == 3){
              if(!isFirst){MessageError = MessageError + ", ";}
              MessageError = MessageError + codiga;
            }
            else{
              MessageError = CTR_OGGETTO;
            }
            isFirst = false;
          }
        }
        if(!isFirst){
          errors.add(MessageError);
        }
      }
      return errors;
  }

  public static boolean validazionePhoneFax(String phoneFax) throws GestoreException {

    boolean res = true;
    try {
      // Pattern XSD -->
      // (\+\d{1,3}\s\d+(\-\d+)*((\s)?/(\s)?(\+\d{1,3}\s)?\d+(\-\d+)*)*)
      String regex = "(\\+[0-9]{1,3} [0-9]+(\\-[0-9]+)*(( )?/( )?(\\+[0-9]{1,3} )?[0-9]+(\\-[0-9]+)*)*)";

      if (phoneFax != null && !"".equals(phoneFax)) {
        if (!phoneFax.matches(regex) || phoneFax.length()>100) {
          res = false;
        }
      }
    } catch (PatternSyntaxException pse) {
      throw new GestoreException("Errore in validazione del numero OJS", "validazionePhoneFax", pse);
    }
    return res;
  }

}

