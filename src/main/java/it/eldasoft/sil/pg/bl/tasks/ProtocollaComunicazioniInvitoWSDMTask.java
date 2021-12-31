package it.eldasoft.sil.pg.bl.tasks;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Task per la protocollazione delle comunicazioni dell'invito in stato 14 e 15, associate alle occorrenze di GARPRO_WSDM
 *
 * @author Marcello Caminiti
 */
public class ProtocollaComunicazioniInvitoWSDMTask {

  static Logger                                 logger = Logger.getLogger(ProtocollaComunicazioniInvitoWSDMTask.class);

  private SqlManager                            sqlManager;

  private GestioneWSDMManager                   gestioneWSDMManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  public void protocollaComunicazioni() throws GestoreException, IOException, SQLException {

    if (!WebUtilities.isAppNotReady()) {
      if (logger.isDebugEnabled())
        logger.debug("Avvio task protocollazione delle comunicazioni dell'invito per le gare");

      final String idprg="PG";
      Long idcom = null;
      String ngara=null;
      String commsgtes = null;
      String commsgtip =null;
      String codgara = null;
      String classificaDocumento = null;
      String codiceRegistroDocumento = null;
      String tipoDocumento = null;
      String mittenteInterno = null;
      String idIndice = null;
      String idtitolazione = null;
      String idUnitaOperativaDestinataria = null;
      String username = null;
      String password = null;
      String passwordDecoded = null;
      String ruolo = null;
      String nome = null;
      String cognome = null;
      String codiceuo = null;
      String idutente = null;
      String idutenteunop = null;
      String mezzo = null;
      String struttura = null;
      String supporto = null;
      String indirizzoMittente=null;
      String mezzoinvio = null;
      String oggettoMail = null; // oggettodocumento
      String livelloRiservatezza = null;
      String codiceFascicolo = null;
      Long annoFascicolo = null;
      String numeroFascicolo = null;
      String classificaFascicolo = null;
      String codaoo = null;
      String coduff = null;
      HashMap<String, Object> datiWSDM = null;
      HashMap<String, Object> datiProtocollo =null;
      String numeroProtocollo = null;
      Long genere = null;
      HashMap<String, Object> datiGestioneComunicazione = null;
      HashMap<String, Object> datiLogin = null;
      String numeroDocumento=null;
      Long annoProtocollo = null;
      Timestamp dataProtocollo = null;
      Long numeroallegati = null;
      List<?> listaW_INVCOMDES = null;
      WSDMProtocolloAllegatoType[] allegati = null;
      String classificadescrizione = null;
      String voce =null;
      String sottotipo =null;
      String acronimoRup =null;
      String RUP =null;
      String nomeRup =null;
      String commsgogg = null;

      String selectGareConComunicazioniDaProcessare="select distinct(comkey1) from w_invcom where idprg=? and comstato = ? order by comkey1";
      List listaGareConComunicazioni=null;
      try {
        listaGareConComunicazioni = sqlManager.getListVector(selectGareConComunicazioniDaProcessare, new Object[]{idprg, "14"});
      } catch (SQLException e1) {
        logger.error("Errore nella lettura delle comunicazioni dell'invito da processare ");
      }
      if(listaGareConComunicazioni!=null && listaGareConComunicazioni.size()>0){

        int livEventoPubblicazione=1;
        String errMsgEventoPubblicazione="";
        int livEventoProtocollazione=1;
        String errMsgEventoProtocollazione="";
        String msgDescr="";
        String tipoWSDM =null;

        String selComunicazioniDaElaborare = "select w.idcom, w.commsgtes, w.commsgtip, gr.codgar1, g.classifica as classifica_doc, g.cod_reg, "
            +"g.tipo_doc, g.mitt_int, g.indice, g.classifica_tit, g.uo_mittdest, "
            +"l.username, l.password, l.ruolo, l.nome, l.cognome,l.codiceuo,l.idutente,l.idutenteunop, g.mezzo, g.struttura, "
            +"g.supporto, g.indirizzo_mitt, g.mezzo_invio, g.oggetto_mail, g.livello_ris, g.sottotipo, w.commsgogg "
            +"from w_invcom w, garpro_wsdm g, wslogin l, gare gr "
            +"where w.comkey1 = ? and w.comkey1=g.ngara and g.syscon = l.syscon and l.servizio=? and w.comkey1 = gr.ngara "
            +"and w.idprg=? and tipologia = ? and w.comstato = '14' and l.idconfiwsdm = ? order by g.ngara";


        String selectFascicolo = "select codice, anno, numero, classifica, codaoo, coduff, desclassi,desvoce  from wsfascicolo wsf where key1= ? and entita = ? ";

        List listaDatiComunicazione = null;
        Vector datiFascicolo = null;

        Object rispostaSingolaProtocollazione[]=null;
        boolean comunicazioniGaraProcessateTuttePositivamente=true;
        Vector datiGare_genere = null;

        String inserimentoinfascicolo = null;
        String applicaFascicolazione = null;
        boolean abilitatoInvioMailDocumentale = false;
        boolean abilitatoInvioSingolo = false;
        String idconfi = null;

        for (int i=0; i< listaGareConComunicazioni.size(); i++){
          ngara = SqlManager.getValueFromVectorParam(listaGareConComunicazioni.get(i), 0).getStringValue();

            datiGare_genere = this.sqlManager.getVector("select codgar, genere from v_gare_genere where codice = ?", new Object[]{ngara});
            if(datiGare_genere!=null && datiGare_genere.size()>0){
              codgara = SqlManager.getValueFromVectorParam(datiGare_genere, 0).getStringValue();
              genere = SqlManager.getValueFromVectorParam(datiGare_genere, 1).longValue();
            }
            String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
            idconfi = ""+this.gestioneWSDMManager.getWsdmConfigurazioneFromCodgar(codgara, codapp);
            boolean integrazioneWSDMAttiva = this.gestioneWSDMManager.isIntegrazioneWSDMAttivaValida(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO,idconfi);

            if(integrazioneWSDMAttiva){

            WSDMConfigurazioneOutType configurazione = this.gestioneWSDMManager.wsdmConfigurazioneLeggi(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO,idconfi);
            if(configurazione.isEsito())
              tipoWSDM = configurazione.getRemotewsdm();
            abilitatoInvioMailDocumentale = this.gestioneWSDMManager.abilitatoInvioMailDocumentale(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO,idconfi);

            inserimentoinfascicolo =GestioneWSDMManager.INSERIMENTO_FASCICOLO_ESISTENTE;
            applicaFascicolazione=ConfigManager.getValore(GestioneWSDMManager.PROP_WSDM_APPLICAFASCICOLAZIONE+idconfi);
            if(!"1".equals(applicaFascicolazione))
              inserimentoinfascicolo="NO";

            abilitatoInvioSingolo = GestioneWSDMManager.getAbilitazioneInvioSingolo(idconfi);
            //si deve verificare se è attiva la configurazione del WSDM
            if(!this.gestioneWSDMManager.isIntegrazioneWSDMAttivaValida(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO,idconfi))
              continue;
            listaDatiComunicazione = sqlManager.getListVector(selComunicazioniDaElaborare,
                new Object[] {ngara, GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO, idprg, new Long(1), idconfi });


            if (listaDatiComunicazione != null && listaDatiComunicazione.size() > 0) {


              for (int h = 0; h < listaDatiComunicazione.size(); h++) {
                comunicazioniGaraProcessateTuttePositivamente=true;
                idcom = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 0).longValue();
                commsgtes = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 1).getStringValue();
                commsgtip = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 2).getStringValue();
                codgara = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 3).getStringValue();
                classificaDocumento = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 4).getStringValue();
                codiceRegistroDocumento = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 5).getStringValue();
                tipoDocumento = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 6).getStringValue();
                mittenteInterno = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 7).getStringValue();
                idIndice = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 8).getStringValue();
                idtitolazione = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 9).getStringValue();
                idUnitaOperativaDestinataria = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 10).getStringValue();
                username = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 11).getStringValue();
                password = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 12).getStringValue();
                ruolo = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 13).getStringValue();
                nome = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 14).getStringValue();
                cognome = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 15).getStringValue();
                codiceuo = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 16).getStringValue();
                idutente = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 17).getStringValue();
                idutenteunop = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 18).getStringValue();
                mezzo = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 19).getStringValue();
                struttura = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 20).getStringValue();
                supporto = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 21).getStringValue();
                indirizzoMittente = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 22).getStringValue();
                mezzoinvio = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 23).getStringValue();
                oggettoMail = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 24).getStringValue();
                livelloRiservatezza = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 25).getStringValue();
                sottotipo = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 26).getStringValue();
                commsgogg = SqlManager.getValueFromVectorParam(listaDatiComunicazione.get(h), 27).getStringValue();
                codiceFascicolo = null;
                annoFascicolo = null;
                numeroFascicolo = null;
                classificaFascicolo = null;
                codaoo = null;
                coduff = null;
                classificadescrizione = null;
                voce =null;

                if(!"NO".equals(inserimentoinfascicolo)){
                  try {
                    datiFascicolo=sqlManager.getVector(selectFascicolo, new Object[]{ngara,"GARE"});
                  } catch (SQLException e) {
                    logger.error("Errore nella lettura dei dati di wsfascicolo per la gara " + ngara + " " + e.getMessage());
                  }
                  if(datiFascicolo!=null && datiFascicolo.size()>0){
                    codiceFascicolo = SqlManager.getValueFromVectorParam(datiFascicolo, 0).getStringValue();
                    annoFascicolo = SqlManager.getValueFromVectorParam(datiFascicolo, 1).longValue();
                    numeroFascicolo = SqlManager.getValueFromVectorParam(datiFascicolo, 2).getStringValue();
                    classificaFascicolo = SqlManager.getValueFromVectorParam(datiFascicolo, 3).getStringValue();
                    codaoo = SqlManager.getValueFromVectorParam(datiFascicolo, 4).getStringValue();
                    coduff = SqlManager.getValueFromVectorParam(datiFascicolo, 5).getStringValue();
                    classificadescrizione = SqlManager.getValueFromVectorParam(datiFascicolo, 6).getStringValue();
                    voce = SqlManager.getValueFromVectorParam(datiFascicolo, 7).getStringValue();
                  }
                }


                try {
                  passwordDecoded = GestioneWSDMManager.decodificaPassword(password);
                } catch (CriptazioneException e1) {
                  throw new GestoreException("Errore nella decodifica della password contenuta in WSLOGIN  " + password + ". " + e1.getMessage() ,null,e1);
                }

                datiWSDM = new HashMap<String, Object>();
                datiWSDM.put(GestioneWSDMManager.LABEL_IDCOM, idcom);
                datiWSDM.put(GestioneWSDMManager.LABEL_COMMSGETS, commsgtes);
                datiWSDM.put(GestioneWSDMManager.LABEL_COMMSGOGG, commsgogg);
                datiWSDM.put(GestioneWSDMManager.LABEL_COMMSGTIP, commsgtip);
                datiWSDM.put(GestioneWSDMManager.LABEL_CLASSIFICA_DOCUMENTO, classificaDocumento);
                datiWSDM.put(GestioneWSDMManager.LABEL_CODICE_REGISTRO_DOCUMENTO, codiceRegistroDocumento);
                datiWSDM.put(GestioneWSDMManager.LABEL_TIPO_DOCUMENTO, tipoDocumento);
                datiWSDM.put(GestioneWSDMManager.LABEL_MITTENTE_INTERNO, mittenteInterno);
                datiWSDM.put(GestioneWSDMManager.LABEL_ID_INDICE, idIndice);
                datiWSDM.put(GestioneWSDMManager.LABEL_ID_TITOLAZIONE, idtitolazione);
                datiWSDM.put(GestioneWSDMManager.LABEL_ID_UNITA_OPERATIVA_DESTINATARIA, idUnitaOperativaDestinataria);
                datiWSDM.put(GestioneWSDMManager.LABEL_USERNAME, username);
                datiWSDM.put(GestioneWSDMManager.LABEL_PASSWORD, passwordDecoded);
                datiWSDM.put(GestioneWSDMManager.LABEL_RUOLO, ruolo);
                datiWSDM.put(GestioneWSDMManager.LABEL_NOME, nome);
                datiWSDM.put(GestioneWSDMManager.LABEL_COGNOME, cognome);
                datiWSDM.put(GestioneWSDMManager.LABEL_CODICEUO, codiceuo);
                datiWSDM.put(GestioneWSDMManager.LABEL_ID_UTENTE, idutente);
                datiWSDM.put(GestioneWSDMManager.LABEL_ID_UTENTE_UNITA_OPERATIVA, idutenteunop);
                datiWSDM.put(GestioneWSDMManager.LABEL_MEZZO, mezzo);
                datiWSDM.put(GestioneWSDMManager.LABEL_STRUTTURA, struttura);
                datiWSDM.put(GestioneWSDMManager.LABEL_SUPPORTO, supporto);
                datiWSDM.put(GestioneWSDMManager.LABEL_INDIRIZZO_MITTENTE, indirizzoMittente);
                datiWSDM.put(GestioneWSDMManager.LABEL_MEZZO_INVIO, mezzoinvio);
                datiWSDM.put(GestioneWSDMManager.LABEL_OGGETTO_DOCUMENTO, oggettoMail);
                datiWSDM.put(GestioneWSDMManager.LABEL_LIVELLO_RISERVATEZZA, livelloRiservatezza);
                datiWSDM.put(GestioneWSDMManager.LABEL_CODICE_FASCICOLO, codiceFascicolo);
                datiWSDM.put(GestioneWSDMManager.LABEL_ANNO_FASCICOLO, annoFascicolo);
                datiWSDM.put(GestioneWSDMManager.LABEL_NUMERO_FASCICOLO, numeroFascicolo);
                datiWSDM.put(GestioneWSDMManager.LABEL_CLASSIFICA_FASCICOLO, classificaFascicolo);
                datiWSDM.put(GestioneWSDMManager.LABEL_CODAOO, codaoo);
                datiWSDM.put(GestioneWSDMManager.LABEL_CODICE_UFFICIO, coduff);
                datiWSDM.put(GestioneWSDMManager.LABEL_CLASSIFICA_DESRIZIONE, classificadescrizione);
                datiWSDM.put(GestioneWSDMManager.LABEL_VOCE, voce);
                datiWSDM.put(GestioneWSDMManager.LABEL_IDCONFI, idconfi);
                datiWSDM.put(GestioneWSDMManager.LABEL_SOTTOTIPO, sottotipo);

                try{
                  rispostaSingolaProtocollazione = gestioneWSDMManager.protocollaComunicazioniWSDM(ngara, codgara, genere, tipoWSDM, abilitatoInvioMailDocumentale,
                      abilitatoInvioSingolo, inserimentoinfascicolo, datiWSDM);
                  if(rispostaSingolaProtocollazione!=null){
                    if("NOK".equals(rispostaSingolaProtocollazione[0])){
                      comunicazioniGaraProcessateTuttePositivamente = false;
                      errMsgEventoProtocollazione = (String)rispostaSingolaProtocollazione[1];
                      livEventoProtocollazione=3;
                    }
                    else{
                      livEventoProtocollazione=1;
                      errMsgEventoProtocollazione="";

                      datiProtocollo = (HashMap<String,Object>)rispostaSingolaProtocollazione[2];
                      numeroProtocollo = (String)datiProtocollo.get(GestioneWSDMManager.LABEL_NUMERO_PROTOCOLLO);
                      numeroDocumento = (String)datiProtocollo.get(GestioneWSDMManager.LABEL_NUMERO_DOCUMENTO);
                      annoProtocollo = (Long)datiProtocollo.get(GestioneWSDMManager.LABEL_ANNO_PROTOCOLLO);
                      dataProtocollo = (Timestamp)datiProtocollo.get(GestioneWSDMManager.LABEL_DATA_PROTOCOLLO);
                      numeroallegati = (Long)datiProtocollo.get(GestioneWSDMManager.LABEL_NUMERO_ALLEGATI_REALI);

                      datiGestioneComunicazione = new HashMap<String,Object>();
                      datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_TIPO_WSDM, tipoWSDM);
                      datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_ABILITATO_INVIO_MAIL_DOCUMENTALE, new Boolean(abilitatoInvioMailDocumentale));
                      datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_NUMERO_DOCUMENTO, numeroDocumento);
                      datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_ANNO_PROTOCOLLO, annoProtocollo);
                      datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_NUMERO_PROTOCOLLO, numeroProtocollo);
                      datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_DATA_PROTOCOLLO, dataProtocollo);
                      datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_COMMSGETS, commsgtes);
                      datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_INDIRIZZO_MITTENTE, indirizzoMittente);
                      datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_COMMSGTIP, commsgtip);
                      datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_IDPRG, idprg);
                      datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_IDCOM, idcom);
                      datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_NUMERO_ALLEGATI_REALI, numeroallegati);
                      datiGestioneComunicazione.put(GestioneWSDMManager.LABEL_OGGETTO_DOCUMENTO, oggettoMail);

                      datiLogin = new HashMap<String,Object>();
                      datiLogin.put(GestioneWSDMManager.LABEL_USERNAME, username);
                      datiLogin.put(GestioneWSDMManager.LABEL_PASSWORD, passwordDecoded);
                      datiLogin.put(GestioneWSDMManager.LABEL_RUOLO, ruolo);
                      datiLogin.put(GestioneWSDMManager.LABEL_NOME, nome);
                      datiLogin.put(GestioneWSDMManager.LABEL_COGNOME, cognome);
                      datiLogin.put(GestioneWSDMManager.LABEL_CODICEUO, codiceuo);


                      listaW_INVCOMDES = (List<?>)rispostaSingolaProtocollazione[3];
                      //La lista listaW_INVCOMDES contiene vettori di dati di W_INVCOMDES, in cui desmail e idcomdes sono in posizione 3 e 4.
                      //Si deve creare una nuova lista di vettori, in cui i 2 campi siano in posizione 0 e 1.
                      List<Vector<JdbcParametro>> datiW_INVCOMDES = null;
                      if(listaW_INVCOMDES!=null){
                        datiW_INVCOMDES = new ArrayList<Vector<JdbcParametro>>();
                        JdbcParametro desmail=null;
                        JdbcParametro idcomdes = null;
                        for(int j=0; j<listaW_INVCOMDES.size(); j++){
                          Vector<JdbcParametro> vet = new Vector<JdbcParametro>(2);
                          desmail = SqlManager.getValueFromVectorParam(listaW_INVCOMDES.get(j), 3);
                          idcomdes = SqlManager.getValueFromVectorParam(listaW_INVCOMDES.get(j), 4);
                          vet.add(desmail);
                          vet.add(idcomdes);
                          datiW_INVCOMDES.add(vet);
                        }
                      }

                      allegati = (WSDMProtocolloAllegatoType[])rispostaSingolaProtocollazione[4];

                      gestioneWSDMManager.gestioneComunicazioneDopoProtocollazione(codgara, datiGestioneComunicazione, datiLogin, datiW_INVCOMDES, allegati,idconfi);

                    }
                  }
                }catch(GestoreException e){
                  comunicazioniGaraProcessateTuttePositivamente = false;
                  livEventoProtocollazione=3;
                  errMsgEventoProtocollazione=e.getMessage();
                  gestioneWSDMManager.impostaStatoComunicazione(idprg, idcom, new Long(15));
                }finally{
                  try {
                    msgDescr="Protocollazione comunicazione (id. PG - " + idcom.toString() ;
                    if(numeroProtocollo!=null && !"".equals(numeroProtocollo))
                      msgDescr+=", n.prot.: " + numeroProtocollo;
                     msgDescr+=")";
                    LogEvento logEvento = new LogEvento();
                    logEvento.setCodApplicazione(idprg);
                    logEvento.setLivEvento(livEventoProtocollazione);
                    logEvento.setOggEvento(ngara);
                    logEvento.setCodEvento("PRO_INVCOM");
                    logEvento.setDescr(msgDescr);
                    logEvento.setErrmsg(errMsgEventoProtocollazione);
                    LogEventiUtils.insertLogEventi(logEvento);
                  } catch (Exception le) {
                    logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
                  }
                }
              }// for listaDatiComunicazione
            } // if listaDatiComunicazione
            else {
              //Non vi sono occorrenze in garpro_wsdm, non vi è nessuna comunicazione da processare
              comunicazioniGaraProcessateTuttePositivamente = false;

            }

          if(comunicazioniGaraProcessateTuttePositivamente){
            livEventoPubblicazione=1;
            errMsgEventoPubblicazione="";
            try {
              gestioneWSDMManager.aggiornamentoGara(codgara,numeroProtocollo,abilitatoInvioSingolo);
            } catch (SQLException e) {
              livEventoPubblicazione=3;
              errMsgEventoPubblicazione=e.getMessage();
            }finally{
              try {
                LogEvento logEvento = new LogEvento();
                logEvento.setCodApplicazione(idprg);
                logEvento.setLivEvento(livEventoPubblicazione);
                logEvento.setOggEvento(ngara);
                logEvento.setCodEvento("GA_PUBBLICA_INVITO");
                logEvento.setDescr("Pubblicazione gara su area riservata di Portale Appalti");
                logEvento.setErrmsg(errMsgEventoPubblicazione);
                LogEventiUtils.insertLogEventi(logEvento);
              } catch (Exception le) {
                logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
              }
            }
          }
        }
        } // for listaGareConComunicazioni
      }
      if (logger.isDebugEnabled())
        logger.debug("Fine task protocollazione delle comunicazioni dell'invito per le gare");



    }
  }

}
