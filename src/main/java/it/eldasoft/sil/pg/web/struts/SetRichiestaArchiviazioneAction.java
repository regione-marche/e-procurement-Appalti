package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.tasks.ArchiviazioneDocumentiManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloInType;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloResType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoInType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class SetRichiestaArchiviazioneAction extends Action {

  private SqlManager sqlManager;

  private ArchiviazioneDocumentiManager archiviazioneDocumentiManager;

  private GestioneWSDMManager gestioneWSDMManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }


  public void setArchiviazioneDocumentiManager(ArchiviazioneDocumentiManager archiviazioneDocumentiManager) {
    this.archiviazioneDocumentiManager = archiviazioneDocumentiManager;
  }

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    Long syscon = null;
    Long tipo_archiviazione = null;
    Long _idRichiesta = null;

    String codgar = request.getParameter("codgar");
    String codice = request.getParameter("codice");

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

    String descrizionedocumento = request.getParameter("descrizionedocumento");
    String mittenteinterno = request.getParameter("mittenteinterno");
    String indirizzoMittente = request.getParameter("indirizzomittente");
    String mezzoinvio = request.getParameter("mezzoinvio");
    String codiceregistrodocumento = request.getParameter("codiceregistrodocumento");
    String inout = request.getParameter("inout");
    String idindice = request.getParameter("idindice");
    String idtitolazione = request.getParameter("idtitolazione");
    String idunitaoperativadestinataria = request.getParameter("idunitaoperativadestinataria");

    String inserimentoinfascicolo = request.getParameter("inserimentoinfascicolo");
    String codicefascicolo = request.getParameter("codicefascicolo");
    String oggettofascicolo = request.getParameter("oggettofascicolo");
    String classificafascicolo = request.getParameter("classificafascicolo");
    String descrizionefascicolo = request.getParameter("descrizionefascicolo");
    String annofascicolo = request.getParameter("annofascicolo");
    String numerofascicolo = request.getParameter("numerofascicolo");

    String delegaInvioMail = request.getParameter("delegainviomail");
    String tipoWSDM = request.getParameter("tipowsdm");
    String servizio = request.getParameter("servizio");
    String genere = request.getParameter("genere");

    String codiceaoo =  request.getParameter("codiceaoonuovo");
    String mezzo = request.getParameter("mezzo");
    String codiceufficio =  request.getParameter("codiceufficionuovo");
    String struttura =  request.getParameter("struttura");
    String tipofascicolo =  request.getParameter("tipofascicolo");
    String isRiservatezzaAttiva =  request.getParameter("isRiservatezzaAttiva");
    String idconfi =  request.getParameter("idconfi");
    String supporto = request.getParameter("supporto");

    String classificaDescrizione = request.getParameter("classificadescrizione");
    String voce = request.getParameter("voce");
    String codiceaoodes = request.getParameter("codiceaoodes");
    String codiceufficiodes = request.getParameter("codiceufficiodes");
    String nomeRup = request.getParameter("nomeRup");
    String acronimoRup = request.getParameter("acronimoRup");
    String sottotipo = request.getParameter("sottotipo");

    String entita="GARE";
    if("1".equals(genere))
      entita="TORN";


    if("TITULUS".equals(tipoWSDM)){
      if("10".equals(genere) || "20".equals(genere))
        tipodocumento=this.gestioneWSDMManager.TIPO_DOCUMENTO_ELENCO_ARCHIVIAZIONE;
      else if("11".equals(genere))
        tipodocumento=this.gestioneWSDMManager.TIPO_DOCUMENTO_AVVISO_ARCHIVIAZIONE;
      else
        tipodocumento=this.gestioneWSDMManager.TIPO_DOCUMENTO_GARA_ARCHIVIAZIONE;
      inout ="INT";
    }

    //Per TITULUS si deve sbiancare il contenuto della classifica del fascicolo
    if("TITULUS".equals(tipoWSDM))
      classificafascicolo = null;

    //CALCOLO qui se la fascicolazione risulta abilitata

    String valAbilitazioneFascicolazione = ConfigManager.getValore("pg.wsdm.applicaFascicolazione." + idconfi);
    if("1".equals(valAbilitazioneFascicolazione)){
      boolean esisteFascicoloTitulus=false;
      boolean esisteFascicoloSMAT=false;
      boolean esisteFascicoloPrisma=false;
      boolean esisteFascicoloArchiflowfa=false;
      String fromWhere = "from wsfascicolo wsf ,v_gare_genere v" +
          " where wsf.key1= v.codice and  wsf.entita = ? and v.codgar = ?";
      if(!"ENGINEERINGDOC".equals(tipoWSDM) && !"SMAT".equals(tipoWSDM) && !"ARCHIFLOWFA".equals(tipoWSDM)){
        String selectFascicolo = "select wsf.codice,wsf.numero " + fromWhere;
        //ocorre recuperare sia il codice che il numero del fascicolo,in quanto abbiamo il caso di ENGINEERING
        //che non mi restituisce in creazione il codice ma il numero
        Vector fascicolo = sqlManager.getVector(selectFascicolo, new Object[] { entita, codgar});
        if (fascicolo != null && fascicolo.size() > 0){
          if(!"PRISMA".equals(tipoWSDM)){
            codicefascicolo = (String) SqlManager.getValueFromVectorParam(fascicolo, 0).getValue();
            numerofascicolo = (String) SqlManager.getValueFromVectorParam(fascicolo, 1).getValue();
          }
          if("TITULUS".equals(tipoWSDM))
            esisteFascicoloTitulus=true;
          if("PRISMA".equals(tipoWSDM))
            esisteFascicoloPrisma=true;
        }
      }else if("SMAT".equals(tipoWSDM) || "ARCHIFLOWFA".equals(tipoWSDM)){
        String selectFascicolo = "select wsf.codice " + fromWhere;
        String codiceWsfascicolo = (String)sqlManager.getObject(selectFascicolo, new Object[] { entita, codgar});
        if(codiceWsfascicolo!=null && !"".equals(codiceWsfascicolo)){
          if("SMAT".equals(tipoWSDM))
            esisteFascicoloSMAT=true;
          else
            esisteFascicoloArchiflowfa=true;
        }
      }

      codicefascicolo = UtilityStringhe.convertiNullInStringaVuota(codicefascicolo);
      numerofascicolo = UtilityStringhe.convertiNullInStringaVuota(numerofascicolo);
      //Nel caso di TITULUS il codice fascicolo viene inizializzato col codice gara, quindi si deve creare il fascicolo
      //anche se il codice fascicolo è valorizzato, ma non è presente l"occorrenza in wsfascicolo
      if(("".equals(codicefascicolo) && "".equals(numerofascicolo)) || ("TITULUS".equals(tipoWSDM) && !esisteFascicoloTitulus) || ("SMAT".equals(tipoWSDM) && !esisteFascicoloSMAT)
          || ("PRISMA".equals(tipoWSDM) && !esisteFascicoloPrisma) || ("ARCHIFLOWFA".equals(tipoWSDM) && !esisteFascicoloArchiflowfa)){
        inserimentoinfascicolo = "SI_FASCICOLO_NUOVO";
      //CREO IL FASCICOLO
        if("PALEO".equals(tipoWSDM) || "TITULUS".equals(tipoWSDM)){
          String oggettodocumento = "Apertura fascicolo";
          if("TITULUS".equals(tipoWSDM))
            oggettodocumento +=  request.getParameter("oggettodocumento");
          WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = null;
          WSDMProtocolloAllegatoType[] allegati = null;
          inserimentoinfascicolo = "SI_FASCICOLO_NUOVO";

          HashMap<String, Object> par = new HashMap<String, Object>();
          par.put("classificadocumento", classificadocumento);
          par.put("tipodocumento", tipodocumento);
          par.put("oggettodocumento", oggettodocumento);
          par.put("descrizionedocumento", descrizionedocumento);
          par.put("mittenteinterno", mittenteinterno);
          par.put("codiceregistrodocumento", codiceregistrodocumento);
          par.put("inout", inout);
          par.put("idindice", idindice);
          par.put("idtitolazione", idtitolazione);
          par.put("idunitaoperativamittente", idunitaoperativadestinataria);
          par.put("inserimentoinfascicolo", inserimentoinfascicolo);
          par.put("codicefascicolo", codicefascicolo);
          par.put("oggettofascicolo", oggettofascicolo);
          par.put("classificafascicolo", classificafascicolo);
          par.put("descrizionefascicolo", descrizionefascicolo);
          par.put("annofascicolo", annofascicolo);
          par.put("numerofascicolo", numerofascicolo);
          par.put("tipoWSDM", tipoWSDM);
          par.put("idprg", null);
          par.put("idcom", null);
          par.put("mezzo", null);
          par.put("societa", null);
          par.put("codiceGaralotto", null);
          par.put("cig", null);
          par.put("numeroallegati", new Long(0));
          par.put("struttura", struttura);
          par.put("tipofascicolo", null);
          par.put("classificaDescrizione", classificaDescrizione);
          par.put("voce", voce);
          wsdmProtocolloDocumentoIn = this.gestioneWSDMManager.wsdmProtocolloDocumentoPopola(par,idconfi);

          String idDocumento = null;
          if("TITULUS".equals(tipoWSDM)){
            idDocumento="GARE|" + codice;
            if(genere!=null && "1".equals(genere))
              idDocumento="TORN|" + codgar;
            else if(genere!=null && "3".equals(genere))
              idDocumento="GARE|" + codgar;
            wsdmProtocolloDocumentoIn.setIdDocumento(idDocumento);
          }

          allegati = new WSDMProtocolloAllegatoType[1];
          // Aggiunta del testo della comunicazione
          String commsgtes ="Apertura fascicolo";
          allegati[0] = new WSDMProtocolloAllegatoType();
          if("PALEO".equals(tipoWSDM)){
            allegati[0].setNome("Comunicazione.pdf");
            allegati[0].setTipo("pdf");
            allegati[0].setTitolo("Testo della comunicazione");
          }else{
            allegati[0].setNome("Fascicolo.pdf");
            allegati[0].setTipo("pdf");
            allegati[0].setTitolo("Apertura fascicolo");
            allegati[0].setIdAllegato(idDocumento + "|1");
          }
          allegati[0].setContenuto(UtilityStringhe.string2Pdf(commsgtes));

          wsdmProtocolloDocumentoIn.setAllegati(allegati);

          WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = this.gestioneWSDMManager.WSDMDocumentoInserisci(username, password,
              ruolo, nome, cognome, codiceuo, null, null, wsdmProtocolloDocumentoIn,"DOCUMENTALE",codiceaoo,codiceufficio,idconfi);

          if (wsdmProtocolloDocumentoRes.isEsito()) {
            String numeroDocumento = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroDocumento();

            // Salvataggio del riferimento al fascicolo
            if ("SI_FASCICOLO_NUOVO".equals(inserimentoinfascicolo)) {
              String codiceFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getCodiceFascicolo();
              Long annoFascicoloNUOVO = null;
              if (wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getAnnoFascicolo() != null) {
                annoFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getAnnoFascicolo();
              }
              String numeroFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getNumeroFascicolo();

              if("TITULUS".equals(tipoWSDM))
                classificafascicolo = classificadocumento;

              this.gestioneWSDMManager.setWSFascicolo(entita, codice, null, null, null, codiceFascicoloNUOVO, annoFascicoloNUOVO,
                  numeroFascicoloNUOVO, classificafascicolo,codiceaoo,codiceufficio,null,null,classificaDescrizione,voce,codiceaoodes,codiceufficiodes);
            }

            //Salvataggio in WSDOCUMENTO
            this.gestioneWSDMManager.setWSDocumento(entita, codgar, null, null, null, numeroDocumento, null, null, oggettodocumento,inout);

          }else{
            String messaggio = wsdmProtocolloDocumentoRes.getMessaggio();
            throw new GestoreException("Errore nell'inserimento del documento","wsdm.fascicoloprotocollo.documentoinserisci.error",new Object[]{messaggio}, new Exception());
          }

        }else if(!"ENGINEERINGDOC".equals(tipoWSDM) && !"SMAT".equals(tipoWSDM) && !"PRISMA".equals(tipoWSDM) && !"ARCHIFLOWFA".equals(tipoWSDM)){
          WSDMFascicoloInType wsdmFascicoloIn = new WSDMFascicoloInType();
          wsdmFascicoloIn.setClassificaFascicolo(classificafascicolo);
          wsdmFascicoloIn.setDescrizioneFascicolo(descrizionefascicolo);
          wsdmFascicoloIn.setOggettoFascicolo(oggettofascicolo);
          if("JIRIDE".equals(tipoWSDM)){
            wsdmFascicoloIn.setTipo(tipofascicolo);
            wsdmFascicoloIn.setStruttura(struttura);
          }
          if("JDOC".equals(tipoWSDM)){
            wsdmFascicoloIn.setGenericS11(acronimoRup);
            wsdmFascicoloIn.setGenericS12(nomeRup);
          }
          WSDMFascicoloResType wsdmFascicoloRes = this.gestioneWSDMManager.WSDMFasciloInserisci(username, password,
              ruolo, nome, cognome, codiceuo, idutente, idutenteunop, wsdmFascicoloIn,servizio, codiceaoo, codiceufficio,idconfi);
          if(wsdmFascicoloRes.isEsito()){
            // Salvataggio del riferimento al fascicolo
            String codiceFascicoloNUOVO = wsdmFascicoloRes.getFascicolo().getCodiceFascicolo();
            Long annoFascicoloNUOVO = null;
            if (wsdmFascicoloRes.getFascicolo().getAnnoFascicolo() != null) {
              annoFascicoloNUOVO = wsdmFascicoloRes.getFascicolo().getAnnoFascicolo();
            }
            String numeroFascicoloNUOVO = wsdmFascicoloRes.getFascicolo().getNumeroFascicolo();
            Long riservatezza = null;
            if(!"0".equals(isRiservatezzaAttiva)){
              riservatezza = new Long(1);
            }
            this.gestioneWSDMManager.setWSFascicolo(entita, codice, null, null, null, codiceFascicoloNUOVO, annoFascicoloNUOVO,
                numeroFascicoloNUOVO, classificafascicolo,null,null,struttura,riservatezza,null,null,null,null);

          }else{
            String messaggio = wsdmFascicoloRes.getMessaggio();
            throw new GestoreException("Errore nell'inserimento del fascicolo","wsdm.fascicoloprotocollo.fascicoloinserisci.error",new Object[]{messaggio}, new Exception());
          }
        }else if("SMAT".equals(tipoWSDM) || "PRISMA".equals(tipoWSDM) || "ARCHIFLOWFA".equals(tipoWSDM)){
          Long annoFascicoloNUOVO=null;
          if("SMAT".equals(tipoWSDM)){
            Date oggi = new Date();
            annoFascicoloNUOVO=this.gestioneWSDMManager.getAnnoFromDate(new Timestamp(oggi.getTime()));
            codicefascicolo=codice;
          }else{
            if(annofascicolo!=null && !"".equals(annofascicolo))
              annoFascicoloNUOVO=new Long(annofascicolo);
          }

          this.gestioneWSDMManager.setWSFascicolo(entita, codice, null, null, null, codicefascicolo, annoFascicoloNUOVO,
              numerofascicolo, classificafascicolo,null,null,struttura,null,null,null,null,null);
        }

      }
    }

    syscon = null;
    tipo_archiviazione = new Long(2);

    String wsdmLoginComune = ConfigManager.getValore(GestioneWSDMManager.PROP_WSDM_LOGIN_COMUNE+idconfi);
    if (wsdmLoginComune != null && "1".equals(wsdmLoginComune)) {
      syscon = new Long(-1);
    } else {
      ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      syscon = new Long(profiloUtente.getId());;
    }





    //INSERIMENTO in GARDOC_JOBS
    _idRichiesta = archiviazioneDocumentiManager.insertJobArchiviazioneDocumenti(syscon, codgar, tipoWSDM, classificadocumento, codiceregistrodocumento,
        tipodocumento, mittenteinterno, idtitolazione, idindice, idunitaoperativadestinataria,mezzo, struttura,supporto,tipo_archiviazione,sottotipo);
    result.put("idRichiesta", _idRichiesta);

    out.println(result);
    out.flush();


    return null;
  }


}
