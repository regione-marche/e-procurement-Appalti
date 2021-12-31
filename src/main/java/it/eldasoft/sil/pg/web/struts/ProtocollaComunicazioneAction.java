package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMInserimentoInFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMInviaMailResType;
import it.maggioli.eldasoft.ws.dm.WSDMInviaMailType;
import it.maggioli.eldasoft.ws.dm.WSDMMailFormatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoInType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloInOutType;
import net.sf.json.JSONObject;

public class ProtocollaComunicazioneAction extends Action {

  private SqlManager          sqlManager;
  private GestioneWSDMManager gestioneWSDMManager;
  private FileAllegatoManager fileAllegatoManager;

  static Logger               logger = Logger.getLogger(ProtocollaComunicazioneAction.class);

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String ruolo = request.getParameter("ruolo");
    String nome = request.getParameter("nome");
    String cognome = request.getParameter("cognome");
    String codiceuo = request.getParameter("codiceuo");
    String idutente = request.getParameter("idutente");
    String idutenteunop = request.getParameter("idutenteunop");

    String classificadocumento = request.getParameter("classificadocumento");
    String tipodocumento = request.getParameter("tipodocumento");
    String oggettodocumento = request.getParameter("oggettodocumento");
    String descrizionedocumento = request.getParameter("descrizionedocumento");
    String mittenteinterno = request.getParameter("mittenteinterno");
    String indirizzoMittente = request.getParameter("indirizzomittente");
    String mezzoinvio = request.getParameter("mezzoinvio");
    String mezzo = request.getParameter("mezzo");
    String codiceregistrodocumento = request.getParameter("codiceregistrodocumento");
    String inout = request.getParameter("inout");
    String idindice = request.getParameter("idindice");
    String idtitolazione = request.getParameter("idtitolazione");
    String idunitaoperativamittente = request.getParameter("idunitaoperativamittente");

    String inserimentoinfascicolo = request.getParameter("inserimentoinfascicolo");
    String codicefascicolo = request.getParameter("codicefascicolo");
    String oggettofascicolo = request.getParameter("oggettofascicolo");
    String classificafascicolo = request.getParameter("classificafascicolo");
    String descrizionefascicolo = request.getParameter("descrizionefascicolo");
    String annofascicolo = request.getParameter("annofascicolo");
    String numerofascicolo = request.getParameter("numerofascicolo");
    String tipofascicolo = request.getParameter("tipofascicolo");
    String livelloRiservatezza = request.getParameter("livelloriservatezza");
    String isRiservatezzaAttiva = request.getParameter("isRiservatezzaAttiva");

    String entita = request.getParameter("entita");
    String key1 = request.getParameter("key1");
    String key2 = request.getParameter("key2");
    String key3 = request.getParameter("key3");
    String key4 = request.getParameter("key4");
    if ("".equals(key2))
      key2=null;
    if ("".equals(key3))
      key3=null;
    if ("".equals(key4))
      key4=null;

    String delegaInvioMail = request.getParameter("delegainviomail");
    String tipoWSDM = request.getParameter("tipowsdm");

    String idprg = request.getParameter("idprg");
    String idcfg = request.getParameter("idcfg");
    Long idcom = null;

    String codiceaoo =  request.getParameter("codiceaoo");
    String societa =  request.getParameter("societa");
    String codiceGaralotto = request.getParameter("codicegaralotto");
    String cig = request.getParameter("cig");
    String genereGara = request.getParameter("generegara");
    String codiceufficio = request.getParameter("codiceufficio");
    String numeroallegati = request.getParameter("numeroallegati");
    String struttura = request.getParameter("struttura");
    String supporto = request.getParameter("supporto");
    String classificaDescrizione = request.getParameter("classificadescrizione");
    String idconfi = request.getParameter("idconfi");
    String voce = request.getParameter("voce");
    String codiceaoodes = request.getParameter("codiceaoodes");
    String codiceufficiodes = request.getParameter("codiceufficiodes");
    String RUP = request.getParameter("RUP");
    String nomeRup = request.getParameter("nomeRup");
    String acronimoRup = request.getParameter("acronimoRup");
    String sottotipo = request.getParameter("sottotipo");
    String posAllegato = request.getParameter("posAllegato");

    boolean tabellatiInDb = this.gestioneWSDMManager.isTabellatiInDb();

    if (request.getParameter("idcom") != "" && !"".equals(request.getParameter("idcom"))) {
      idcom = new Long(request.getParameter("idcom"));

      WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = new WSDMProtocolloDocumentoInType();

      if("TITULUS".equals(tipoWSDM) && !"1".equals(delegaInvioMail)){
        if("10".equals(genereGara) || "20".equals(genereGara))
          tipodocumento=GestioneWSDMManager.TIPO_DOCUMENTO_ELENCO;
        else if("11".equals(genereGara))
          tipodocumento=GestioneWSDMManager.TIPO_DOCUMENTO_AVVISO;
        else
          tipodocumento=GestioneWSDMManager.TIPO_DOCUMENTO_GARA;
      }else if("TITULUS".equals(tipoWSDM) && "1".equals(delegaInvioMail)){
        if("10".equals(genereGara) || "20".equals(genereGara))
          tipodocumento=GestioneWSDMManager.TIPO_DOCUMENTO_ELENCO_PEC;
        else if("11".equals(genereGara))
          tipodocumento=GestioneWSDMManager.TIPO_DOCUMENTO_AVVISO_PEC;
        else
          tipodocumento=GestioneWSDMManager.TIPO_DOCUMENTO_GARA_PEC;
      }

      //Per TITULUS si deve sbiancare il contenuto della classifica del fascicolo
      if("TITULUS".equals(tipoWSDM))
        classificafascicolo = null;

      if("FOLIUM".equals(tipoWSDM)){
        String note= this.gestioneWSDMManager.getDescrizioneTabellato(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO, "note",idconfi,tabellatiInDb);
        codiceGaralotto=note + " " + codiceGaralotto;
      }

      // Dati generali dell'elemento documentale
      wsdmProtocolloDocumentoIn.setClassifica(classificadocumento);
      wsdmProtocolloDocumentoIn.setTipoDocumento(tipodocumento);
      wsdmProtocolloDocumentoIn.setOggetto(oggettodocumento);
      wsdmProtocolloDocumentoIn.setDescrizione(descrizionedocumento);
      wsdmProtocolloDocumentoIn.setMittenteInterno(mittenteinterno);
      wsdmProtocolloDocumentoIn.setInout(WSDMProtocolloInOutType.fromString(inout));
      wsdmProtocolloDocumentoIn.setCodiceRegistro(codiceregistrodocumento);
      Calendar cal = Calendar.getInstance();
      cal.setTime(UtilityDate.getDataOdiernaAsDate());
      wsdmProtocolloDocumentoIn.setData(cal);
      wsdmProtocolloDocumentoIn.setDataArrivo(cal);
      wsdmProtocolloDocumentoIn.setNumeroAllegati(new Long(numeroallegati));
      wsdmProtocolloDocumentoIn.setIdIndice(idindice);
      wsdmProtocolloDocumentoIn.setIdTitolazione(idtitolazione);
      wsdmProtocolloDocumentoIn.setIdUnitaOperativaMittente(idunitaoperativamittente);
      wsdmProtocolloDocumentoIn.setSocieta(societa);
      wsdmProtocolloDocumentoIn.setCodiceGaraLotto(codiceGaralotto);
      wsdmProtocolloDocumentoIn.setCig(cig);
      wsdmProtocolloDocumentoIn.setStruttura(struttura);
      wsdmProtocolloDocumentoIn.setSupporto(supporto);
      wsdmProtocolloDocumentoIn.setClassificaDescrizione(classificaDescrizione);
      wsdmProtocolloDocumentoIn.setVoce(voce);


      // Inserimento in fascicolo
      if ("NO".equals(inserimentoinfascicolo)) {
        wsdmProtocolloDocumentoIn.setInserimentoInFascicolo(WSDMInserimentoInFascicoloType.NO);
      }


      if ("SI_FASCICOLO_ESISTENTE".equals(inserimentoinfascicolo)) {
        wsdmProtocolloDocumentoIn.setInserimentoInFascicolo(WSDMInserimentoInFascicoloType.SI_FASCICOLO_ESISTENTE);
        WSDMFascicoloType wsdmFascicolo = new WSDMFascicoloType();
        wsdmFascicolo.setCodiceFascicolo(codicefascicolo);
        annofascicolo = UtilityStringhe.convertiNullInStringaVuota(annofascicolo);
        if (!"".equals(annofascicolo)) wsdmFascicolo.setAnnoFascicolo(new Long(annofascicolo));
        numerofascicolo = UtilityStringhe.convertiNullInStringaVuota(numerofascicolo);
        if (!"".equals(numerofascicolo)) wsdmFascicolo.setNumeroFascicolo(numerofascicolo);
        if("TITULUS".equals(tipoWSDM)){
          wsdmFascicolo.setOggettoFascicolo(oggettofascicolo);
        }
        if("ARCHIFLOWFA".equals(tipoWSDM) || "INFOR".equals(tipoWSDM))
          wsdmFascicolo.setClassificaFascicolo(classificafascicolo);
        if("PRISMA".equals(tipoWSDM))
          wsdmFascicolo.setStruttura(struttura);

        if("JIRIDE".equals(tipoWSDM)){
          wsdmFascicolo.setStruttura(struttura);
          String riservatezzaAttiva = ConfigManager.getValore("wsdm.applicaRiservatezza."+idconfi);
          if("1".equals(riservatezzaAttiva)){
            String selectW_INVCOMDES = "select ISRISERVA from WSFASCICOLO where key1 = ? and (entita = 'GARE' or entita = 'TORN')";
            String isRiserva = (String) this.sqlManager.getObject(selectW_INVCOMDES, new Object[] { key1 });
            if(isRiserva != null && isRiserva.equals("1")){
              wsdmProtocolloDocumentoIn.setLivelloRiservatezza(livelloRiservatezza);
            }
          }
        }

        wsdmProtocolloDocumentoIn.setFascicolo(wsdmFascicolo);
      }

      if("EASYDOC".equals(tipoWSDM)){
        String channelCode= this.gestioneWSDMManager.getcodiceTabellato("FASCICOLOPROTOCOLLO", "channelcode",idconfi,tabellatiInDb);
        wsdmProtocolloDocumentoIn.setChannelCode(channelCode);
      }

      if("JPROTOCOL".equals(tipoWSDM)){
        String strutt= this.gestioneWSDMManager.getcodiceTabellato("FASCICOLOPROTOCOLLO", "struttura",idconfi,tabellatiInDb);
        wsdmProtocolloDocumentoIn.setStruttura(strutt);
        String tipoassegnazione = this.gestioneWSDMManager.getcodiceTabellato("FASCICOLOPROTOCOLLO", "tipoassegnazione",idconfi,tabellatiInDb);
        wsdmProtocolloDocumentoIn.setTipoAssegnazione(tipoassegnazione);
      }

      if("JDOC".equals(tipoWSDM)){
        wsdmProtocolloDocumentoIn.setGenericS11(sottotipo);
        wsdmProtocolloDocumentoIn.setGenericS12(RUP);
      }

      if ("SI_FASCICOLO_NUOVO".equals(inserimentoinfascicolo)) {
        wsdmProtocolloDocumentoIn.setInserimentoInFascicolo(WSDMInserimentoInFascicoloType.SI_FASCICOLO_NUOVO);
        WSDMFascicoloType wsdmFascicolo = new WSDMFascicoloType();
        wsdmFascicolo.setOggettoFascicolo(oggettofascicolo);
        wsdmFascicolo.setClassificaFascicolo(classificafascicolo);
        wsdmFascicolo.setDescrizioneFascicolo(descrizionefascicolo);
        if("TITULUS".equals(tipoWSDM) || "SMAT".equals(tipoWSDM) || "ARCHIFLOWFA".equals(tipoWSDM)){
          wsdmFascicolo.setCodiceFascicolo(codicefascicolo);
        }
        if("PRISMA".equals(tipoWSDM)){
          wsdmFascicolo.setStruttura(struttura);
          wsdmFascicolo.setAnnoFascicolo(new Long(annofascicolo));
          wsdmFascicolo.setNumeroFascicolo(numerofascicolo);
        }
        if("JIRIDE".equals(tipoWSDM)){
          wsdmFascicolo.setTipo(tipofascicolo);
          wsdmFascicolo.setStruttura(struttura);
          wsdmProtocolloDocumentoIn.setLivelloRiservatezza(livelloRiservatezza);
        }
        if("JDOC".equals(tipoWSDM)){
          wsdmFascicolo.setGenericS11(acronimoRup);
          wsdmFascicolo.setGenericS12(nomeRup);
        }
        wsdmProtocolloDocumentoIn.setFascicolo(wsdmFascicolo);
      }

      boolean inserimentoFascicoloArchiflowfa=false;
      boolean inserimentoFascicoloFolium = false;
      boolean inserimentoFascicoloPrisma = false;
      if(("ARCHIFLOWFA".equals(tipoWSDM) || "FOLIUM".equals(tipoWSDM) || "PRISMA".equals(tipoWSDM)) && "SI_FASCICOLO_NUOVO".equals(inserimentoinfascicolo)){
        if("ARCHIFLOWFA".equals(tipoWSDM))
            inserimentoFascicoloArchiflowfa=true;
        if("FOLIUM".equals(tipoWSDM))
          inserimentoFascicoloFolium = true;
        if("PRISMA".equals(tipoWSDM))
          inserimentoFascicoloPrisma = true;
        wsdmProtocolloDocumentoIn.setInserimentoInFascicolo(WSDMInserimentoInFascicoloType.SI_FASCICOLO_ESISTENTE);
      }

      if("TITULUS".equals(tipoWSDM))
        wsdmProtocolloDocumentoIn.setIdDocumento("W_INVCOM|" + idprg + "|" + idcom.toString());


      List<?> datiW_INVCOMDES =this.gestioneWSDMManager.popolaDestinatariWSDM(idprg, idcom, tipoWSDM, mezzoinvio, wsdmProtocolloDocumentoIn);
      wsdmProtocolloDocumentoIn.setMezzo(mezzo);

      // Invio mail mediante servizi di protocollazione
      if("1".equals(delegaInvioMail) && ("ENGINEERING".equals(tipoWSDM) || "TITULUS".equals(tipoWSDM) || "SMAT".equals(tipoWSDM) || "URBI".equals(tipoWSDM))){
          WSDMInviaMailType inviaMail = new WSDMInviaMailType();
          // Testo email
          String commsgtes = (String) sqlManager.getObject("select commsgtes from w_invcom where idprg = ? and idcom = ?", new Object[] {
              idprg, idcom });
          inviaMail.setTestoMail(commsgtes);
          if("ENGINEERING".equals(tipoWSDM)){
            // Oggetto email
            inviaMail.setOggettoMail(oggettodocumento);
          }

          // Destinatari
          String selectW_INVCOMDESMail = "select desmail from w_invcomdes where idprg = ? and idcom = ?";
          List<?> datiW_INVCOMDESMail = this.sqlManager.getListVector(selectW_INVCOMDESMail, new Object[] { idprg, idcom });
          if (datiW_INVCOMDESMail != null && datiW_INVCOMDESMail.size() > 0) {
            String[] destinatariMail = new String[datiW_INVCOMDESMail.size()];
            for (int ides = 0; ides < datiW_INVCOMDESMail.size(); ides++) {
              destinatariMail[ides] = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDESMail.get(ides), 0).getValue();
            }
            inviaMail.setDestinatariMail(destinatariMail);
          }

          wsdmProtocolloDocumentoIn.setInviaMail(inviaMail);
      }

      // Allegati (viene sempre incluso un allegato generato con il contenuto del testo
      // della email, anche se il testo è vuoto)
      Vector<?> datiComunicazione = sqlManager.getVector("select commsgtes, commsgogg from w_invcom where idprg = ? and idcom = ?", new Object[] {
          idprg, idcom });
      String commsgtes = null;
      String commsgogg =   null;
      if(datiComunicazione!=null){
        commsgtes = SqlManager.getValueFromVectorParam(datiComunicazione, 0).getStringValue();
        commsgogg = SqlManager.getValueFromVectorParam(datiComunicazione, 1).getStringValue();
      }
      String selectW_DOCDIG = "select digdesdoc, dignomdoc, idprg, iddocdig from w_docdig where digent = ? and digkey1 = ? and digkey2 = ? order by iddocdig";
      List<?> datiW_DOCDIG = sqlManager.getListVector(selectW_DOCDIG, new Object[] { "W_INVCOM", idprg, idcom.toString() });

      int numeroAllegati = 1;
      int numAllegatiReali=0;
      if (datiW_DOCDIG != null && datiW_DOCDIG.size() > 0){
        numeroAllegati += datiW_DOCDIG.size();
        numAllegatiReali = datiW_DOCDIG.size();
      }

      WSDMProtocolloAllegatoType[] allegati = new WSDMProtocolloAllegatoType[numeroAllegati];

      //Nel caso di EASYDOC mi servono solamante gli allegati effettivi, senza il testo della comunicazione
      WSDMProtocolloAllegatoType[] allegatiReali = null;
      //if("EASYDOC".equals(tipoWSDM) && numAllegatiReali>0)
      //  allegatiReali =new WSDMProtocolloAllegatoType[numAllegatiReali];

      if (commsgtes == null){
        commsgtes = "[testo vuoto]";
      }
      String contenutoPdf = this.gestioneWSDMManager.getTestoComunicazioneFormattato(key1, cig, commsgogg, commsgtes);

      // Aggiunta dei documenti allegati
      //se posAllegato=1 allora il testo della comunicazione viene inserito come primo elemento, altrimenti si mette in coda
      //la variabile posAllegato viene valorizzata direttamente nel form di invio comunicazioni solo per TITULUS, negli altri
      //casi si deve leggere il valore della property "wsdm.posizioneAllegatoComunicazione".
      //Se la property vale 1 allora il testo della comunicazione viene messo come primo allegato, altrimenti rimane in coda
      int indiceAllegati = 0;
      String posizioneAllegatoComunicazione = ConfigManager.getValore("wsdm.posizioneAllegatoComunicazione." + idconfi);
      if(("TITULUS".equals(tipoWSDM) && "1".equals(posAllegato)) || (!"TITULUS".equals(tipoWSDM) && "1".equals(posizioneAllegatoComunicazione))){
        indiceAllegati = 1;
      }

      for (int i = 0; i < datiW_DOCDIG.size(); i++) {
        String digdesdoc = (String) SqlManager.getValueFromVectorParam(datiW_DOCDIG.get(i), 0).getValue();
        String dignomdoc = (String) SqlManager.getValueFromVectorParam(datiW_DOCDIG.get(i), 1).getValue();
        String w_docdig_idprg = (String) SqlManager.getValueFromVectorParam(datiW_DOCDIG.get(i), 2).getValue();
        Long w_docdig_iddocdig = (Long) SqlManager.getValueFromVectorParam(datiW_DOCDIG.get(i), 3).getValue();

        String tipo = "";
        int index = dignomdoc.lastIndexOf('.');
        if (index > 0) {
          tipo = dignomdoc.substring(index + 1);
        }
        allegati[indiceAllegati + i] = new WSDMProtocolloAllegatoType();
        allegati[indiceAllegati + i].setNome(dignomdoc);
        allegati[indiceAllegati + i].setTitolo(digdesdoc);
        allegati[indiceAllegati + i].setTipo(tipo);
        BlobFile digogg = fileAllegatoManager.getFileAllegato(w_docdig_idprg, w_docdig_iddocdig);
        allegati[indiceAllegati + i].setContenuto(digogg.getStream());
        if("TITULUS".equals(tipoWSDM))
          allegati[indiceAllegati + i].setIdAllegato("W_DOCDIG|" + w_docdig_idprg + "|" + w_docdig_iddocdig.toString());
      }

      // Aggiunta del testo della comunicazione
      int indiceAllegatoTesto = 0;
      if (("1".equals(posAllegato) && "TITULUS".equals(tipoWSDM)) || (!"TITULUS".equals(tipoWSDM) && "1".equals(posizioneAllegatoComunicazione)))
        indiceAllegatoTesto = 0;
      else
        indiceAllegatoTesto = numeroAllegati - 1;
      String commsgtip = (String) sqlManager.getObject("select commsgtip from w_invcom where idprg = ? and idcom = ?", new Object[] {
          idprg, idcom });
      if ("1".equals(commsgtip)) {
        commsgtes = "<!DOCTYPE html><html><body>" + commsgtes + "</body></html>";
        allegati[indiceAllegatoTesto] = new WSDMProtocolloAllegatoType();
        allegati[indiceAllegatoTesto].setNome("Comunicazione.html");
        allegati[indiceAllegatoTesto].setTipo("html");
        allegati[indiceAllegatoTesto].setTitolo("Testo della comunicazione");
        allegati[indiceAllegatoTesto].setContenuto(commsgtes.getBytes());
      } else {
        allegati[indiceAllegatoTesto] = new WSDMProtocolloAllegatoType();
        allegati[indiceAllegatoTesto].setNome("Comunicazione.pdf");
        allegati[indiceAllegatoTesto].setTipo("pdf");
        allegati[indiceAllegatoTesto].setTitolo("Testo della comunicazione");

        allegati[indiceAllegatoTesto].setContenuto(UtilityStringhe.string2Pdf(contenutoPdf));
      }
      if("TITULUS".equals(tipoWSDM))
        allegati[indiceAllegatoTesto].setIdAllegato("W_INVCOM|" + idprg + "|" + idcom.toString());

      wsdmProtocolloDocumentoIn.setAllegati(allegati);

      WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = this.gestioneWSDMManager.wsdmProtocolloInserisci(username, password,
          ruolo, nome, cognome, codiceuo, idutente, idutenteunop, codiceaoo, codiceufficio, wsdmProtocolloDocumentoIn,idconfi);

      result.put("esito", wsdmProtocolloDocumentoRes.isEsito());
      result.put("messaggio", wsdmProtocolloDocumentoRes.getMessaggio());

      if (wsdmProtocolloDocumentoRes.isEsito()) {
        String numeroDocumento = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroDocumento();
        Long annoProtocollo = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getAnnoProtocollo();
        String numeroProtocollo = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroProtocollo();

        Timestamp dataProtocollo= this.gestioneWSDMManager.getDataProtocollo(wsdmProtocolloDocumentoRes);
        if(annoProtocollo==null){
          annoProtocollo = this.gestioneWSDMManager.getAnnoFromDate(dataProtocollo);
        }

        result.put("numerodocumento", numeroDocumento);
        result.put("annoprotocollo", annoProtocollo);
        result.put("numeroprotocollo", numeroProtocollo);

        TransactionStatus status = null;
        boolean commitTransaction = false;
        String protocolloMail = null;
        String oggettoMail = null;
        String statoComunicazione = "2";
        String desstato = null;
        String msgErroreInvioMail = null;
        if("1".equals(delegaInvioMail) && ("PALEO".equals(tipoWSDM) || "JIRIDE".equals(tipoWSDM) || "ENGINEERING".equals(tipoWSDM) || "TITULUS".equals(tipoWSDM) || "ARCHIFLOW".equals(tipoWSDM) ||
            "SMAT".equals(tipoWSDM) || "ARCHIFLOWFA".equals(tipoWSDM) || "EASYDOC".equals(tipoWSDM) || "PRISMA".equals(tipoWSDM) || "URBI".equals(tipoWSDM) || "JPROTOCOL".equals(tipoWSDM) || "ITALPROT".equals(tipoWSDM))){
          WSDMInviaMailType parametriMailIn = new WSDMInviaMailType();
          parametriMailIn.setNumeroDocumento(numeroDocumento);
          parametriMailIn.setAnnoProtocollo(annoProtocollo);
          parametriMailIn.setNumeroProtocollo(numeroProtocollo);
          protocolloMail = numeroProtocollo;
          protocolloMail = UtilityStringhe.convertiNullInStringaVuota(protocolloMail);
          oggettoMail = oggettodocumento;
          oggettoMail = UtilityStringhe.convertiNullInStringaVuota(oggettoMail);
          if("PALEO".equals(tipoWSDM) || "JIRIDE".equals(tipoWSDM) || "ARCHIFLOW".equals(tipoWSDM) || "EASYDOC".equals(tipoWSDM) || "PRISMA".equals(tipoWSDM)){
            if("JIRIDE".equals(tipoWSDM)){
              protocolloMail = UtilityStringhe.fillLeft(protocolloMail, '0', 7);
            }
            oggettoMail = "Prot.N." + protocolloMail + "/" + annoProtocollo + " - " + oggettoMail;
          }
          parametriMailIn.setOggettoMail(oggettoMail);
          parametriMailIn.setTestoMail(commsgtes);
          parametriMailIn.setMittenteMail(indirizzoMittente);
          if ("1".equals(commsgtip))
            parametriMailIn.setFormatoMail(WSDMMailFormatoType.HTML);
          else
            parametriMailIn.setFormatoMail(WSDMMailFormatoType.TEXT);

          if ("ENGINEERING".equals(tipoWSDM) || "TITULUS".equals(tipoWSDM) || "SMAT".equals(tipoWSDM) || "URBI".equals(tipoWSDM)) {
            statoComunicazione = "10";
            desstato = "4";

            result.put("esitoInviaMail", "true");

          } else if ("PALEO".equals(tipoWSDM) || "ARCHIFLOW".equals(tipoWSDM) || "ARCHIFLOWFA".equals(tipoWSDM) || "ITALPROT".equals(tipoWSDM)) {
            if(("ARCHIFLOW".equals(tipoWSDM) || "ARCHIFLOWFA".equals(tipoWSDM)) && datiW_INVCOMDES.size() >0){
              //Si deve impostare il vettore dei destinatari
              String destinatari[] = new String[datiW_INVCOMDES.size()];
              for (int i = 0; i < datiW_INVCOMDES.size(); i++) {
                destinatari[i] = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 3).getValue();
              }
              parametriMailIn.setDestinatariMail(destinatari);
            }

            WSDMInviaMailResType wsdmInviaMailResType = this.gestioneWSDMManager.wsdmInviaMail(username, password, ruolo, nome, cognome, codiceuo, null, null, parametriMailIn,idconfi);
            if(wsdmInviaMailResType.isEsito()){
              statoComunicazione = "10";
              desstato = "4";
            }else{
              statoComunicazione = "11";
              desstato = "5";
              msgErroreInvioMail = wsdmInviaMailResType.getMessaggio();
            }

            result.put("esitoInviaMail", wsdmInviaMailResType.isEsito());
            result.put("messaggioInviaMail", msgErroreInvioMail);

          }else if ("JIRIDE".equals(tipoWSDM) || "EASYDOC".equals(tipoWSDM) || "PRISMA".equals(tipoWSDM) || "JPROTOCOL".equals(tipoWSDM)) {
            //JIRIDE si deve inviare una mail per ogni destinatario
            //anche per EAS_YDOC
            boolean invioMailOk = true;
            if (datiW_INVCOMDES != null && datiW_INVCOMDES.size() > 0) {
              WSDMInviaMailResType wsdmInviaMailResType = null;
              String updateW_INVCOMDES = "update w_invcomdes set desstato = ?, deserrore = ?, desdatinv = ? where idprg = ? and idcom = ? and idcomdes = ? ";
              if("EASYDOC".equals(tipoWSDM)){
                String mailChannelCode = this.gestioneWSDMManager.getcodiceTabellato("FASCICOLOPROTOCOLLO", "mailchannelcode",idconfi,tabellatiInDb);
                parametriMailIn.setMailChannelCode(mailChannelCode);
                String mailConfigurationCode=this.gestioneWSDMManager.getcodiceTabellato("FASCICOLOPROTOCOLLO", "mailconfigurationcode",idconfi,tabellatiInDb);
                parametriMailIn.setMailConfigurationCode(mailConfigurationCode);


                allegatiReali=this.gestioneWSDMManager.getAllegatiReali(allegati, numAllegatiReali,idconfi);
                parametriMailIn.setAllegati(allegatiReali);
              }
              for (int i = 0; i < datiW_INVCOMDES.size(); i++) {
                String delayPec = ConfigManager.getValore("wsdm.invioMailPec.delay."+idconfi);
                if(!"".equals(delayPec) && delayPec != null && i > 0){
                  TimeUnit.MILLISECONDS.sleep(Integer.parseInt(delayPec));
                }
                String desmail = (String) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 3).getValue();
                Long idcomdes = (Long) SqlManager.getValueFromVectorParam(datiW_INVCOMDES.get(i), 4).getValue();
                parametriMailIn.setDestinatariMail(new String[]{desmail});
                wsdmInviaMailResType = this.gestioneWSDMManager.wsdmInviaMail(username, password, ruolo, nome, cognome, codiceuo, null, null, parametriMailIn,idconfi);
                if(wsdmInviaMailResType.isEsito()){
                  msgErroreInvioMail = null;
                  desstato = "4";
                }else{
                  invioMailOk = false;
                  msgErroreInvioMail = wsdmInviaMailResType.getMessaggio();
                  desstato = "5";
                }

                result.put("esitoInviaMail", wsdmInviaMailResType.isEsito());
                result.put("messaggioInviaMail", wsdmInviaMailResType.getMessaggio());

                try {
                  status = this.sqlManager.startTransaction();
                  this.sqlManager.update(updateW_INVCOMDES, new Object[] {desstato, msgErroreInvioMail,  new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),idprg, idcom, idcomdes });
                  commitTransaction = true;
                } catch (Exception e) {
                  commitTransaction = false;
                } finally {
                  if (status != null) {
                    if (commitTransaction) {
                      this.sqlManager.commitTransaction(status);
                    } else {
                      this.sqlManager.rollbackTransaction(status);
                    }
                  }
                }
              }
              if(invioMailOk)
                statoComunicazione = "10";
              else
                statoComunicazione = "11";
            }
          }
        }

        // Salvataggio del numero protocollo nella comunicazione ed impostazione
        // dello stato a "In uscita"

        try {
          status = this.sqlManager.startTransaction();
          //Il campo COMMITT va aggiornato solo se indirizzoMittente è valorizzato
          Object parametri[]=null;
          String updateW_INVCOM = null;


          if("1".equals(delegaInvioMail) && ("JIRIDE".equals(tipoWSDM) || "ARCHIFLOW".equals(tipoWSDM) || "ARCHIFLOWFA".equals(tipoWSDM) || "EASYDOC".equals(tipoWSDM) || "PRISMA".equals(tipoWSDM) || "JPROTOCOL".equals(tipoWSDM))){
            if(idcfg != null && !"".equals(idcfg)){
              updateW_INVCOM = "update w_invcom set comstato = ?, comdatprot = ?, comnumprot = ?, committ = ?, idcfg = ? where idprg = ? and idcom = ?";
              parametri = new Object[]{statoComunicazione, dataProtocollo, numeroProtocollo, indirizzoMittente, idcfg, idprg, idcom };
            }else{
              updateW_INVCOM = "update w_invcom set comstato = ?, comdatprot = ?, comnumprot = ?, committ = ? where idprg = ? and idcom = ?";
              parametri = new Object[]{statoComunicazione, dataProtocollo, numeroProtocollo, indirizzoMittente, idprg, idcom };
            }
          }else{
            if(idcfg != null && !"".equals(idcfg)){
              updateW_INVCOM = "update w_invcom set comstato = ?, comdatprot = ?, comnumprot = ?, idcfg = ? where idprg = ? and idcom = ?";
              parametri = new Object[]{statoComunicazione, dataProtocollo,numeroProtocollo, idcfg, idprg, idcom };
            }else{
              updateW_INVCOM = "update w_invcom set comstato = ?, comdatprot = ?, comnumprot = ? where idprg = ? and idcom = ?";
              parametri = new Object[]{statoComunicazione, dataProtocollo,numeroProtocollo, idprg, idcom };
            }
          }

          Vector datiW_INVOMris = this.sqlManager.getVector("select IDCOMRIS, IDPRGRIS from W_INVCOM where IDPRG=? and IDCOM=?", new Object[]{idprg,idcom});
          if(datiW_INVOMris!=null && datiW_INVOMris.size()>0){
            Long idcomris  = (Long) SqlManager.getValueFromVectorParam(datiW_INVOMris, 0).getValue();
            String idprgris  = (String) SqlManager.getValueFromVectorParam(datiW_INVOMris, 1).getValue();
            if(idcomris != null && idprgris != null){
              Date comdatalet = (Date) sqlManager.getObject("select COMDATLET from w_invcom where IDPRG = ? and IDCOM = ?", new Object[] { idprgris, idcomris });
              if(comdatalet == null){
                this.sqlManager.update("update W_INVCOM set COMDATLET = ? where IDPRG = ? and IDCOM=?", new Object[] {
                    new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), idprgris, idcomris });
              }
            }
          }

          this.sqlManager.update(updateW_INVCOM, parametri);
          if("1".equals(delegaInvioMail) && ("PALEO".equals(tipoWSDM) || "ENGINEERING".equals(tipoWSDM) || "TITULUS".equals(tipoWSDM) || "ARCHIFLOW".equals(tipoWSDM) || "SMAT".equals(tipoWSDM)
              || "ARCHIFLOWFA".equals(tipoWSDM)  || "URBI".equals(tipoWSDM) || "ITALPROT".equals(tipoWSDM))){
            String updateW_INVCOMDES = "update w_invcomdes set desstato = ?, deserrore =?, desdatinv = ? where idprg = ? and idcom = ?";
            this.sqlManager.update(updateW_INVCOMDES, new Object[] {desstato, msgErroreInvioMail,  new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),idprg, idcom });
          }
          commitTransaction = true;
        } catch (Exception e) {
          commitTransaction = false;
        } finally {
          if (status != null) {
            if (commitTransaction) {
              this.sqlManager.commitTransaction(status);
            } else {
              this.sqlManager.rollbackTransaction(status);
            }
          }
        }

        // Salvataggio del riferimento al fascicolo
        if(inserimentoFascicoloArchiflowfa || inserimentoFascicoloFolium || inserimentoFascicoloPrisma)
          result.put("inserimentoinfascicolo", "SI_FASCICOLO_ESISTENTE");
        else
          result.put("inserimentoinfascicolo", inserimentoinfascicolo);
        result.put("statoComunicazione",statoComunicazione);
        if ("SI_FASCICOLO_NUOVO".equals(inserimentoinfascicolo)) {
          String codiceFascicoloNUOVO =null;
          Long annoFascicoloNUOVO = null;
          String numeroFascicoloNUOVO = null;
          String descrizioneFascicoloNUOVO = null;
          if(!inserimentoFascicoloArchiflowfa && !inserimentoFascicoloFolium && !inserimentoFascicoloPrisma ){
            codiceFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getCodiceFascicolo();

            if (wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getAnnoFascicolo() != null) {
              annoFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getAnnoFascicolo();
            }else{
              annoFascicoloNUOVO = this.gestioneWSDMManager.getAnnoFromDate(dataProtocollo);
            }
            numeroFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getNumeroFascicolo();
            descrizioneFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getDescrizioneFascicolo();
          }else if(inserimentoFascicoloFolium){
            codiceFascicoloNUOVO = classificafascicolo;
            annoFascicoloNUOVO = this.gestioneWSDMManager.getAnnoFromDate(dataProtocollo);
          }else if(inserimentoFascicoloPrisma){
            codiceFascicoloNUOVO= codicefascicolo;
            annoFascicoloNUOVO=new Long(annofascicolo);
            numeroFascicoloNUOVO=numerofascicolo;
          }else
            codiceFascicoloNUOVO= codicefascicolo;

          Long riservatezza = null;
          if(!"0".equals(isRiservatezzaAttiva)){
            riservatezza = new Long(1);
          }

          if("TITULUS".equals(tipoWSDM))
            classificafascicolo = classificadocumento;

          this.gestioneWSDMManager.setWSFascicolo(entita, key1, key2, key3, key4, codiceFascicoloNUOVO, annoFascicoloNUOVO,
              numeroFascicoloNUOVO, classificafascicolo, codiceaoo, codiceufficio,struttura,riservatezza,classificaDescrizione,voce,codiceaoodes,codiceufficiodes);
          result.put("codicefascicolo", codiceFascicoloNUOVO);
          result.put("annofascicolo", annoFascicoloNUOVO);
          result.put("numerofascicolo", numeroFascicoloNUOVO);
          result.put("descrizionefascicolo", descrizioneFascicoloNUOVO);
          if("JIRIDE".equals(tipoWSDM) && (codiceFascicoloNUOVO==null || "".equals(codiceFascicoloNUOVO))){
            logger.error("Il codice del fascicolo restituito dal servizio di protocollazione risulta nullo per l'occorrenza in WSFASCICOLO: ENTITA=" + entita + ", KEY1=" + key1);
          }
          if("JIRIDE".equals(tipoWSDM) && (codiceFascicoloNUOVO==null || "".equals(codiceFascicoloNUOVO))){
            logger.error("Il numero del fascicolo restituito dal servizio di protocollazione risulta nullo per l'occorrenza in WSFASCICOLO: ENTITA=" + entita + ", KEY1=" + key1);
          }
        }

        //Salvatagio in WSDOCUMENTO
        Long idWSDocumento = this.gestioneWSDMManager.setWSDocumento(entita, key1, key2, key3, key4, numeroDocumento, annoProtocollo, numeroProtocollo, oggettodocumento,inout);

        //Salvataggio della mail in WSALLEGATI
        this.gestioneWSDMManager.setWSAllegati("W_INVCOM", idprg, idcom.toString(), null, null, idWSDocumento);

        //Salvataggio degli allegati in WSALLEGATI
        if (numeroAllegati > 0) {
          for (int i = 0; i < datiW_DOCDIG.size(); i++) {
            Long iddocdig = (Long)SqlManager.getValueFromVectorParam(datiW_DOCDIG.get(i), 3).getValue();
            this.gestioneWSDMManager.setWSAllegati("W_DOCDIG", idprg, iddocdig.toString(), null, null, idWSDocumento);
          }
        }
      }
    }

    out.print(result);
    out.flush();

    return null;

  }

}
