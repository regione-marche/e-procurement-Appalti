/*
 * Created on 12/10/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore che effettua il controllo della valorizzazione di alcuni
 * campi. Se anche uno non risulta valorizzato, allora si deve riportare
 * un messaggio opportuno alla finestra popipPubblicaSuPortale.jsp
 *
 * @author Marcello Caminiti
 */
public class GestoreInvioInvitoAstaElettronica extends GestorePubblicaSuPortale {



  public GestoreInvioInvitoAstaElettronica(BodyTagSupportGene tag) {
    super(tag);
  }


  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    this.inizializzaManager(page);

    // lettura dei parametri di input
    String codgar = page.getRequest().getParameter("codgar");
    String ngara = page.getRequest().getParameter("ngara");
    String idconfi = page.getRequest().getParameter("idconfi");
    String select=null;
    Long genere = null;
    String messaggio = "";
    String controlloSuperato="SI";
    String MsgConferma = "";

    Long numOccorrenze=null;
    String valoreWSDM = null;

    boolean visualizzaDettaglioComunicazione=true;

    String integrazioneWSDM="0";


    try {
      
      boolean isIntegrazioneWSDMAttivaValida = gestioneWSDMManager.isIntegrazioneWSDMAttivaValida(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO, idconfi);
      if(isIntegrazioneWSDMAttivaValida)
        integrazioneWSDM="1";
    } catch (GestoreException e) {
      throw new JspException("Errore nella verifica dell'integrazione WSDM Protocollo per la gara " + codgar ,e);
    } catch (SQLException e) {
      throw new JspException("Errore nella verifica dell'integrazione WSDM Protocollo per la gara " + codgar ,e);
    }

    //String messaggio di conferma
    messaggio = "<b>Non è possibile procedere all'invio dell'invito all'asta elettronica.</b><br>";
    MsgConferma = "Confermi l'invio dell'invito all'asta elettronica?";
    MsgConferma += "<br>Se si procede con l'operazione, i dati attinenti alla definizione dell'asta elettronica (fasi di svolgimento, documentazione, ammissione delle ditte) diverranno disponibili in sola visualizzazione.";
    MsgConferma += "<br>Viene inoltre inviata comunicazione alle ditte invitate, secondo i dettagli riportati di seguito.";

    page.setAttribute("MsgConferma", MsgConferma, PageContext.REQUEST_SCOPE);


    if("".equals(ngara))
      ngara=null;

    try {

      //Controllo esistenza occorrenze su AEFASI
      select="select count(id) from aefasi where ngara=?";
      numOccorrenze =(Long)this.sqlManager.getObject(select, new Object[]{ngara});
      if(numOccorrenze==null || (numOccorrenze!=null && numOccorrenze.longValue()==0)){
        controlloSuperato = "NO";
        messaggio += "<br>Non è stato inserita alcuna fase di asta elettronica.";
      }else{

        //Controllo sulle date
        //Date aedinvit = (Date)this.sqlManager.getObject("select aedinvit from gare1 where ngara=?", new Object[]{ngara});
        Date datini = (Date)this.sqlManager.getObject("select datini from aefasi where ngara=? order by numfase", new Object[]{ngara});
        Calendar dataPartenza = Calendar.getInstance();
        dataPartenza.setTimeInMillis(UtilityDate.getDataOdiernaAsDate().getTime());
        Calendar dataSoglia = UtilityDate.addGiorniLavorativi(dataPartenza, 2);
        if(dataSoglia!=null){
          Calendar dataVerifica = Calendar.getInstance();
          dataVerifica.setTimeInMillis(datini.getTime());
          dataVerifica.set(Calendar.HOUR_OF_DAY, 0);
          dataVerifica.set(Calendar.MINUTE, 0);
          dataVerifica.set(Calendar.SECOND, 0);
          dataVerifica.set(Calendar.MILLISECOND, 0);
          if(!dataVerifica.after(dataSoglia)){
            controlloSuperato = "NO";
            messaggio += "<br>La data di apertura di ogni fase di asta elettronica deve essere successiva alla data corrente di almeno tre giorni lavorativi.";
          }
        }
      }

      //Controllo che la data di apertura di ogni fase non ricada in un giorno festivo
      List<?> listaDate = this.sqlManager.getListVector("select datini from aefasi where ngara=? order by numfase", new Object[]{ngara});
      if(listaDate!=null && listaDate.size()>0){
        Timestamp datini = null;
        Calendar data = Calendar.getInstance();
        for(int i=0;i<listaDate.size();i++){
          datini = SqlManager.getValueFromVectorParam(listaDate.get(i), 0).dataValue();
          data.setTimeInMillis(datini.getTime());
          if(!UtilityDate.isGiornoLavorativo(data)){
            controlloSuperato = "NO";
            messaggio += "<br>La data di apertura di ogni fase di asta elettronica deve ricadere in un giorno lavorativo.";
            break;
          }
        }
      }

      //controllo sull'esistenza dei documenti dell'invio (GRUPPO=12)
      select="select count(codgar) from documgara where codgar= ? and ngara=? and gruppo = ? ";
      Long gruppo= new Long(12);
      numOccorrenze = (Long) sqlManager.getObject(select, new Object[]{codgar, ngara,  gruppo});
      if(numOccorrenze== null || numOccorrenze.longValue()==0){
        controlloSuperato = "NO";
        messaggio += "<br>Non è stato inserito nessun documento relativo all'invito all'asta elettronica.";
      }else{
        //Controllo presenza allegato o url
        String condizioneGruppo="GRUPPO = 12 ";
        String desc = tabellatiManager.getDescrTabellato("A1108", "1");
        if(desc!=null && !"".equals(desc))
          desc = desc.substring(0,1);
        boolean gestioneUrl=false;
        if("1".equals(desc))
          gestioneUrl=true;
        String[] esito = this.controlloAllegatoUrl(ngara, codgar, genere, condizioneGruppo, gestioneUrl,"3");
        if("NO".equals(esito[0])){
          controlloSuperato = "NO";
          messaggio+=esito[1];
        }else{
          //Se specificato da configurazione il formato degli allegati, viene verificato che gli allegati di tipo=12 rispettino tutti il formato
          String formatoAllegati = ConfigManager.getValore(CostantiAppalti.FORMATO_ALLEGATI);
          if(formatoAllegati!=null && !"".equals(formatoAllegati)){
            select="select dignomdoc from documgara, w_docdig where codgar= ? and GRUPPO = 12 and documgara.idprg=w_docdig.idprg and documgara.iddocdg=w_docdig.iddocdig";
            List listaFileAllegati=this.sqlManager.getListVector(select, new Object[]{codgar});
            if(!this.pgManagerEst1.controlloAllegatiFormatoValido(listaFileAllegati,0,formatoAllegati)){
              controlloSuperato = "NO";
              messaggio += "<br>Ci sono dei documenti allegati all'invito che hanno un formato non valido.";
            }else{
              //Se abilitata la richiesta della firma, si deve controllare che non vi siano documenti in attesa di firma
              String richiestaFirma = ConfigManager.getValore(CostantiAppalti.PROP_RICHIESTA_FIRMA);
              if("1".equals(richiestaFirma)){
                select="select count(codgar) from documgara, w_docdig where codgar= ? and ngara=? and gruppo in (12,10) and documgara.idprg=w_docdig.idprg and documgara.iddocdg=w_docdig.iddocdig and digfirma = '1'";
                numOccorrenze = (Long) sqlManager.getObject(select, new Object[]{codgar, ngara});
                if(numOccorrenze!= null && numOccorrenze.longValue()>0){
                  controlloSuperato = "NO";
                  messaggio += "<br>Ci sono dei documenti di gara da pubblicare in attesa di firma.";
                }
              }
            }
          }

        }
      }


        String tipoWSDM=null;

        if("1".equals(integrazioneWSDM)){
          WSDMConfigurazioneOutType configurazione = this.gestioneWSDMManager.wsdmConfigurazioneLeggi("FASCICOLOPROTOCOLLO",idconfi);
          if (configurazione.isEsito()){
            tipoWSDM = configurazione.getRemotewsdm();
            page.setAttribute("tipoWSDM", tipoWSDM, PageContext.REQUEST_SCOPE);
          }
        }

        if("NO".equals(controlloSuperato))
          visualizzaDettaglioComunicazione = false;

        if(visualizzaDettaglioComunicazione){

          boolean delegaInvioMailDocumentaleAbilitata = false;

          boolean delegaInvioDocumentale = false;
          if("1".equals(integrazioneWSDM)){
            valoreWSDM = ConfigManager.getValore("pg.wsdm.invioMailPec."+idconfi);
            if(valoreWSDM!=null && "1".equals(valoreWSDM))
              delegaInvioMailDocumentaleAbilitata=true;

            if(delegaInvioMailDocumentaleAbilitata && ("PALEO".equals(tipoWSDM) || "JIRIDE".equals(tipoWSDM) || "ENGINEERING".equals(tipoWSDM) || "TITULUS".equals(tipoWSDM) || "SMAT".equals(tipoWSDM)))
              delegaInvioDocumentale= true;
          }

          //Si devono caricare le ditte della gara con AMMGAR=1 o null.
          HashMap gestioneDestinatari = this.gestioneDestinatari(ngara, codgar, genere, this.pgManager, delegaInvioDocumentale);
          if("NO".equals(gestioneDestinatari.get("controlloSuperato"))){
            controlloSuperato = "NO";
            messaggio += (String)gestioneDestinatari.get("messaggio");
          }
          List<?> listaDestinatari = (List<?>)gestioneDestinatari.get("listaDestinatari");
          page.setAttribute("listaDestinatari", listaDestinatari, PageContext.REQUEST_SCOPE);

          //Caricamento dei documenti di gruppo=12
          List<?> listaDocumenti = this.getListaDocumenti(genere, ngara, codgar, new Long(12));
          if(listaDocumenti!=null && listaDocumenti.size()>0){
            page.setAttribute("listaDocumenti", listaDocumenti, PageContext.REQUEST_SCOPE);
          }

          //Si controlla che la dimensione totale dei documenti non superi il limite consentito
          HttpSession session = page.getSession();
          String uffint = (String) session.getAttribute("uffint");
          String idcfg = null;
          //Valorizzazione di IDCFG
          try {
            String cenint = (String)sqlManager.getObject("select t.cenint from gare g,torn t where g.codgar1 = t.codgar and ngara=?", new Object[]{ngara});
            cenint = UtilityStringhe.convertiNullInStringaVuota(cenint);
            if(!"".equals(cenint)){
              idcfg = cenint;
            }else{
              idcfg = uffint;
            }
          } catch (SQLException sqle) {
            throw new GestoreException("Errore nella lettura di TORN.CENINT",null, sqle);
          }

          HashMap controlloDimensioneAllegati = this.controlloDimensioneAllegati(listaDocumenti, idcfg, this.fileAllegatoManager);
          if("NO".equals(controlloDimensioneAllegati.get("controlloSuperato"))){
            controlloSuperato = "NO";
            messaggio += (String)controlloDimensioneAllegati.get("messaggio");
          }
        }

      } catch (SQLException e) {
        throw new JspException("Errore il controllo dei campi obbligatori ", e);
      } catch (GestoreException e) {
        throw new JspException("Errore il controllo dei campi obbligatori ", e);
      }
      if("NO".equals(controlloSuperato)){
        page.setAttribute("controlloSuperato", controlloSuperato, PageContext.REQUEST_SCOPE);
        page.setAttribute("msg", messaggio, PageContext.REQUEST_SCOPE);
      }
      page.setAttribute("visualizzaDettaglioComunicazione", new Boolean(visualizzaDettaglioComunicazione), PageContext.REQUEST_SCOPE);
  }
}