/*
 * Created on 28/05/2019
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.tasks;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloModificaResType;
import it.maggioli.eldasoft.ws.erp.WSERPAllegatoType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreResType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreType;
import it.maggioli.eldasoft.ws.erp.WSERPGaraType;
import it.maggioli.eldasoft.ws.erp.WSERPOdaType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class AggiornaERPStatoGaraManager {

  static Logger           logger = Logger.getLogger(AggiornaERPStatoGaraManager.class);

  private GestioneWSERPManager gestioneWSERPManager;

  private GestioneWSDMManager gestioneWSDMManager;

  private SqlManager      sqlManager;

  private FileAllegatoManager      fileAllegatoManager;

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  /**
   *Aggiornamento dello stato della gara
   * @throws JspException 
   *
   */
  public void updateStatoGara() throws GestoreException, JspException{

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato
    if (WebUtilities.isAppNotReady()) return;

    if (logger.isDebugEnabled())
      logger.debug("updateStatoGara: inizio metodo");
      
    
        //Data di Filtro - Sottraggo 6 mesi alla data odierna
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -6);

        Date filterDate = cal.getTime();

        //selezione quelli che hanno una rda...
        try {
          
        String filtroUffintWsdmgestioneERP = this.gestioneWSDMManager.getFiltroUffintFromValoreProp("wsdm.gestioneERP", "1");
        
        if(!GestioneWSDMManager.FILTRO_GRUPPO_VUOTO.equals(filtroUffintWsdmgestioneERP)){
        
        if(filtroUffintWsdmgestioneERP != null && !"".equals(filtroUffintWsdmgestioneERP)){
          filtroUffintWsdmgestioneERP=" and t.cenint " +filtroUffintWsdmgestioneERP;
        }
        
        String selezioneGareDaAggiornare = "select v.codgar,v.codice,v.stato,v.esito,r.numrda,f.codice,gc.impliq,gc.dverbc,gc.dcertu,g.codcig" +
        		" from v_gare_statoesito v" +
        		" INNER JOIN gare g ON v.codice = g.ngara" +
                " INNER JOIN torn t ON t.codgar = g.codgar1" +
        		" LEFT JOIN garecont gc ON gc.codimp = g.ditta" +
        		" and ((gc.ngara=g.ngara and gc.ncont=1) or (gc.ngara=g.codgar1 and (gc.ngaral is null or gc.ngaral=g.ngara)))" +
        		" INNER JOIN garerda r ON v.codgar = r.codgar and r.numrda is not null" +
        		" INNER JOIN wsfascicolo f ON v.codice = f.key1 and f.entita='GARE' and f.codice is not null" +
        		" WHERE v.stato is not null" +
        		" and (g.daatto is null or (g.daatto is not null and g.daatto > ?)) and (g.datneg is null or (g.datneg is not null and g.datneg > ?))" +
        		" and (exists(select e.ngara from pubg e where v.codice = e.ngara and e.tippubg = 12)" +
        		" or exists (select b.codgar9 from pubbli b where v.codgar = b.codgar9 and (b.tippub = 11 or b.tippub = 13)))" +
        		" " + filtroUffintWsdmgestioneERP;

        
          List<?> listaGareDaAgg = sqlManager.getListVector(selezioneGareDaAggiornare, new Object[] {filterDate,filterDate});

          if(listaGareDaAgg.size() > 0){

            for (int g = 0; g < listaGareDaAgg.size(); g++){
              String codgar = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 0).stringValue();
              codgar = UtilityStringhe.convertiNullInStringaVuota(codgar);
              String ngara = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 1).stringValue();
              ngara = UtilityStringhe.convertiNullInStringaVuota(ngara);
              String stato = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 2).stringValue();
              stato = UtilityStringhe.convertiNullInStringaVuota(stato);
              String esito = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 3).stringValue();
              esito = UtilityStringhe.convertiNullInStringaVuota(esito);
              String numeroRda = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 4).stringValue();
              numeroRda = UtilityStringhe.convertiNullInStringaVuota(numeroRda);
              String codiceFascicolo = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 5).stringValue();
              codiceFascicolo = UtilityStringhe.convertiNullInStringaVuota(codiceFascicolo);
              Double impliq = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 6).doubleValue();
              //selezione su....e se non e' presente il CIG?
              String codiceCIG = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 9).stringValue();
              String selezioneMaxDataLiq = "select max(data_atto) from FIN_LIQCIG_V01 where cig = ?";
              Date dataliq = (Date) sqlManager.getObject(selezioneMaxDataLiq, new Object[] {codiceCIG});
              
              if(!"".equals(esito)){
                stato = stato + "-" + esito;
              }
              stato = stato.toUpperCase();
              if(!"".equals(codiceFascicolo)){
                
                Long idconfi = gestioneWSDMManager.getWsdmConfigurazioneFromCodgar(codgar, "PG");
                WSDMFascicoloModificaResType wsdmFascicoloModificaRes = gestioneWSDMManager.wsdmFascicoloERPModifica(codiceFascicolo, stato, impliq, dataliq, idconfi.toString());
                if(!wsdmFascicoloModificaRes.isEsito()){
                  String errMsg = "Errore in aggiornamento dello stato della gara " + ngara + " - " + wsdmFascicoloModificaRes.getMessaggio();
                  logger.error(errMsg);
                }
              }
            }
            
          }
        }
        } catch (SQLException e) {
          String errMsg = "Errore in aggiornamento dello stato della gara. " + e.getMessage();
          logger.error(errMsg);
        }

      //}else{
        //AGGIORNAMENTO DELLO STATO DELLA GARA: ATTENZIONE NON E' PER TUTTI
        //Integrazione con WSERP
        String tipoWSERP = null;
        String urlWSERP = ConfigManager.getValore("wserp.erp.url");
        urlWSERP = UtilityStringhe.convertiNullInStringaVuota(urlWSERP);
        if(!"".equals(urlWSERP)){
          WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
          if(configurazione.isEsito()){
            tipoWSERP = configurazione.getRemotewserp();
          }
        }
        tipoWSERP = UtilityStringhe.convertiNullInStringaVuota(tipoWSERP);//lo lego al sistema remoto per questioni di "sicurezza"
        if("FNM".equals(tipoWSERP)){
        	
          //Data di Filtro - Sottraggo 6 mesi alla data odierna / ora un anno (APPALTI-880)
          date = new Date();
          cal = Calendar.getInstance();
          cal.setTime(date);
          cal.add(Calendar.YEAR, -1);  

          filterDate = cal.getTime();

          //selezione quelli che hanno una rda ANCHE IN GCAP?...
          //MOLTI PIU DATI A REGIME PER ORA SU POSTGRES (cast )

          String selezioneGareDaAggiornare = "select v.codgar, v.codice, v.stato, v.esito, r.numrda, v.codstato, g.ditta, v.genere," +
          		" i.cfimp, i.pivimp, g.codcig, r.esercizio, g1.numrdo, t.numavcp," +
          		" t.dteoff, t.desoff, g.dcomag, g.datneg, t.iterga, t.dpubav, t.dinvit, t.dtepar, g.esineg, g.iaggiu, g.ncomag " +
                  " from v_gare_statoesito v" +
                  " INNER JOIN gare g ON v.codice = g.ngara" +
                  " INNER JOIN gare1 g1 ON v.codgar = g1.codgar1 and g.ngara=g1.ngara" +
                  " INNER JOIN torn t ON v.codgar = t.codgar" +
                  " INNER JOIN garerda r ON v.codgar = r.codgar and r.numrda is not null" +
                  " LEFT JOIN impr i ON g.ditta = i.codimp" +
                  " WHERE v.stato is not null and v.genere = 2 " +
                  " and (g.daatto is null or (g.daatto is not null and g.daatto > ?)) and (g.datneg is null or (g.datneg is not null and g.datneg > ?))" +
                  " and (exists(select e.ngara from pubg e where v.codice = e.ngara and e.tippubg = 12)" +
                  " or exists (select b.codgar9 from pubbli b where v.codgar = b.codgar9 and (b.tippub = 11 or b.tippub = 13)))" +
                  " UNION" +
                  " select vl.codgar1, vl.codice, vl.stato, vl.esito, r.numrda, vl.codstato, g.ditta, cast(vl.genere as int)," +
          		" i.cfimp, i.pivimp, g.codcig, r.esercizio, g1.numrdo, t.numavcp," +
          		" t.dteoff, t.desoff, g.dcomag, g.datneg, t.iterga, t.dpubav, t.dinvit, t.dtepar,g.esineg, g.iaggiu, g.ncomag " +
          		  " from v_gare_statoesitolotti vl INNER JOIN gare g ON vl.codice = g.ngara" +
          		  " INNER JOIN gare1 g1 ON vl.codgar1 = g1.codgar1 and g.ngara=g1.ngara" +
          		  " INNER JOIN torn t ON vl.codgar1 = t.codgar INNER JOIN garerda r ON vl.codgar1 = r.codgar and r.numrda is not null" +
          		  " LEFT JOIN impr i ON g.ditta = i.codimp" +
          		  " WHERE vl.stato is not null and vl.genere = '300'" +
                  " and (g.daatto is null or (g.daatto is not null and g.daatto > ?)) and (g.datneg is null or (g.datneg is not null and g.datneg > ?))" +
          		  " and (exists(select e.ngara from pubg e where vl.codice = e.ngara and e.tippubg = 12)" +
          		  " or exists (select b.codgar9 from pubbli b where vl.codgar1 = b.codgar9 and (b.tippub = 11 or b.tippub = 13)))";

             try {
              List<?> listaGareDaAgg = sqlManager.getListVector(selezioneGareDaAggiornare, new Object[] {filterDate,filterDate,filterDate,filterDate});
              if(listaGareDaAgg.size() > 0){
                for (int g = 0; g < listaGareDaAgg.size(); g++){
                  String codgar = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 0).stringValue();
                  codgar = UtilityStringhe.convertiNullInStringaVuota(codgar);
                  String ngara = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 1).stringValue();
                  ngara = UtilityStringhe.convertiNullInStringaVuota(ngara);
                  String stato = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 2).stringValue();
                  stato = UtilityStringhe.convertiNullInStringaVuota(stato);
                  String esito = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 3).stringValue();
                  esito = UtilityStringhe.convertiNullInStringaVuota(esito);
                  String numeroRda = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 4).stringValue();
                  numeroRda = UtilityStringhe.convertiNullInStringaVuota(numeroRda);
                  Long codStato = (Long) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 5).getValue();
                  String dittaAggiudicataria = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 6).stringValue();
                  dittaAggiudicataria = UtilityStringhe.convertiNullInStringaVuota(dittaAggiudicataria);
                  Long genere = (Long) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 7).getValue();
                  String codiceFiscaleAggiudicatario = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 8).stringValue();
                  codiceFiscaleAggiudicatario = UtilityStringhe.convertiNullInStringaVuota(codiceFiscaleAggiudicatario);
                  String partitaIvaAggiudicatario = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 9).stringValue();
                  partitaIvaAggiudicatario = UtilityStringhe.convertiNullInStringaVuota(partitaIvaAggiudicatario);
                  String codiceCig = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 10).stringValue();
                  codiceCig = UtilityStringhe.convertiNullInStringaVuota(codiceCig);
                  String esercizio = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 11).stringValue();
                  esercizio = UtilityStringhe.convertiNullInStringaVuota(esercizio);
                  String numeroRdo = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 12).stringValue();
                  numeroRdo = UtilityStringhe.convertiNullInStringaVuota(numeroRdo);
                  String numeroAvcp = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 13).stringValue();
                  numeroAvcp = UtilityStringhe.convertiNullInStringaVuota(numeroAvcp);
                  Date dataPresOfferta = (Date) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g),14).getValue();
                  Date dataEsameOffertaAmm = (Date) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g),15).getValue();
                  Date dataAttoAgg = (Date) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g),16).getValue();
                  Date dataAnnullamento = (Date) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g),17).getValue();
                  Long iterga = (Long) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 18).getValue();
                  Date dataBandoAvviso = (Date) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g),19).getValue();
                  Date dataInvitoDitte = (Date) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g),20).getValue();
                  Date dataTerminiPartecipazione = (Date) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g),21).getValue();
                  Long esitoGaraNonAggiudicata = (Long) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 22).getValue();
                  Double importoAggiudicazione = null;
                  Object objImpAgg = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 23).getValue();
                  if (objImpAgg instanceof Long){
                      importoAggiudicazione = ((Long) objImpAgg).doubleValue();
                  }else{
                    if(objImpAgg instanceof Double){
                      importoAggiudicazione = (Double) objImpAgg;
                    }
                  }

                  if("FNM".equals(tipoWSERP)){
                      String numAttoAggiudicazione = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 24).stringValue();
                      numAttoAggiudicazione = UtilityStringhe.convertiNullInStringaVuota(numAttoAggiudicazione);

                      Date dataPubbli = null;
                      Date dataPubblicazione = null;//Data Avviso
                      Date dataInvito = null;//Data Invio R.O./Pubbl. bando
                      List<?> listaPubblicazioni = sqlManager.getListVector("select tippub,datpub from pubbli" +
                          " where codgar9 = ? order by tippub", new Object[] {codgar});
                      if(listaPubblicazioni.size() > 0){
                        for (int p = 0; p < listaPubblicazioni.size(); p++){
                          Long tippub = (Long) SqlManager.getValueFromVectorParam(listaPubblicazioni.get(p), 0).getValue();
                          Date datpub = (Date) SqlManager.getValueFromVectorParam(listaPubblicazioni.get(p),1).getValue();
                          if(new Long(11).equals(tippub)){
                            dataPubbli = datpub;
                            break;
                          }
                          if(new Long(13).equals(tippub)){
                            dataPubbli = datpub;
                            break;
                          }
                        }
                      }




                      if(new Long(2).equals(iterga) || new Long(4).equals(iterga)){//solo per ristrette (ITERGA=2/4)
                        dataPubblicazione = dataPubbli;
                        if(dataPubblicazione == null){
                          dataPubblicazione = dataBandoAvviso;
                        }
                      }

                      if(new Long(1).equals(iterga)){//per aperte (ITERGA=1)
                        dataInvito = dataPubbli;
                        if(dataInvito == null){
                          dataInvito= dataBandoAvviso;
                        }
                      }else{
                        dataInvito = dataInvitoDitte;
                      }




                      Date dataProroga = null;//se ci sono state delle rettifiche valorizzo la proroga
                      Long countRettif = (Long) sqlManager.getObject("select count(*) from garrettif" +
                            " where codgar = ? and tipter = ? and datter is not null", new Object[] {codgar,new Long(2)});
                      if(countRettif > 0){
                        dataProroga = dataPresOfferta;
                      }


                      Date dataEsameOffertaTecnica = null;
                      Date dataEsameOffertaEconomica = null;
                      //DITGEVENTI ACQUISIZIONI BUSTE
                      List<?> listaAcqBusteTecniche = sqlManager.getListVector("select DATFS11B from ditgeventi" +
                          " where codgar = ? and ngara = ? order by DATFS11B asc ", new Object[] {codgar,ngara});
                      if(listaAcqBusteTecniche.size() > 0){
                        for (int s = 0; s < listaAcqBusteTecniche.size(); s++){
                          Date dataAcqBT = (Date) SqlManager.getValueFromVectorParam(listaAcqBusteTecniche.get(s),0).getValue();
                          if(dataAcqBT!= null){
                            dataEsameOffertaTecnica = dataAcqBT;
                            break;
                          }
                        }
                      }

                      List<?> listaAcqBusteEconomiche = sqlManager.getListVector("select DATFS11C from ditgeventi" +
                          " where codgar = ? and ngara = ? order by DATFS11C asc ", new Object[] {codgar,ngara});

                      if(listaAcqBusteEconomiche.size() > 0){
                        for (int s = 0; s < listaAcqBusteEconomiche.size(); s++){
                          Date dataAcqBE = (Date) SqlManager.getValueFromVectorParam(listaAcqBusteEconomiche.get(s),0).getValue();
                          if(dataAcqBE!= null){
                            dataEsameOffertaEconomica = dataAcqBE;
                            break;
                          }
                        }
                      }

                      if(!"".equals(esito)){
                        stato = stato + "-" + esito;
                      }
                      stato = stato.toUpperCase();


                      //recupero delle credenziali univoche
                      Long i_syscon = new Long(-1);
                      String servizio ="WSERP";
                      String[] credenziali = gestioneWSERPManager.wserpGetLogin(i_syscon, servizio);
                      String username = credenziali[0];
                      String password = credenziali[1];

                      //....

                      WSERPOdaType[] odaArray = new WSERPOdaType[1];
                      WSERPOdaType oda = new WSERPOdaType();
                      oda.setCodiceFornitore(dittaAggiudicataria);
                      oda.setCfFornitore(codiceFiscaleAggiudicatario);
                      oda.setPivaFornitore(partitaIvaAggiudicatario);
                      oda.setCodiceRda(numeroRda);
                      //cambiare in seguito qui
                      oda.setPosizioneRda(esercizio);
                      oda.setIdLotto(stato);


                      odaArray[0] = oda;

                      WSERPGaraType datiGara = new WSERPGaraType();

                      datiGara.setCodiceCig(codiceCig);
                      datiGara.setNumeroAVCP(numeroAvcp);

                      if(dataPubblicazione != null){
                        Calendar caldataPubblicazione = Calendar.getInstance();
                        caldataPubblicazione.setTime(dataPubblicazione);
                        datiGara.setDataPubblicazione(caldataPubblicazione);
                      }

                      if(dataPresOfferta != null){
                        Calendar caldataPresOfferta = Calendar.getInstance();
                        caldataPresOfferta.setTime(dataPresOfferta);
                        datiGara.setDataPresOfferta(caldataPresOfferta);
                      }

                      if(dataProroga != null){
                        Calendar caldataProroga = Calendar.getInstance();
                        caldataProroga.setTime(dataProroga);
                        datiGara.setDataProroga(caldataProroga);
                      }

                      if(dataEsameOffertaAmm != null){
                        Calendar caldataEsameOffertaAmm = Calendar.getInstance();
                        caldataEsameOffertaAmm.setTime(dataEsameOffertaAmm);
                        datiGara.setDataEsameOffAmministrativa(caldataEsameOffertaAmm);
                      }

                      if(dataEsameOffertaTecnica != null){
                        Calendar caldataEsameOffTecnica = Calendar.getInstance();
                        caldataEsameOffTecnica.setTime(dataEsameOffertaTecnica);
                        datiGara.setDataEsameOffTecnica(caldataEsameOffTecnica);
                      }

                      if(dataEsameOffertaEconomica != null){
                        Calendar caldataEsameOffEconomica = Calendar.getInstance();
                        caldataEsameOffEconomica.setTime(dataEsameOffertaEconomica);
                        datiGara.setDataEsameOffEconomica(caldataEsameOffEconomica);
                      }

                      if(dataAttoAgg != null){
                        Calendar caldataAttoAgg = Calendar.getInstance();
                        caldataAttoAgg.setTime(dataAttoAgg);
                        datiGara.setDataAggiudicazione(caldataAttoAgg);
                      }

                      if(dataAnnullamento != null){
                        Calendar caldataAnnullamento = Calendar.getInstance();
                        caldataAnnullamento.setTime(dataAnnullamento);
                        datiGara.setDataAnnullamento(caldataAnnullamento);
                      }

                      if(esitoGaraNonAggiudicata != null){
                        //tab1cod= 'A1088'
                        String esitoGNA = null;
                        if(new Long(1).equals(esitoGaraNonAggiudicata)){
                          esitoGNA = "R";
                        }else{
                          if(new Long(2).equals(esitoGaraNonAggiudicata)){
                            esitoGNA = "D";
                          }else{
                            if(new Long(3).equals(esitoGaraNonAggiudicata)){
                              esitoGNA = "S";
                            }
                          }
                        }
                        datiGara.setEsitoNonAggiudicata(esitoGNA);
                      }



                      if(dataTerminiPartecipazione != null){
                        Calendar caldataTerminiPartecipazione = Calendar.getInstance();
                        caldataTerminiPartecipazione.setTime(dataTerminiPartecipazione);
                        datiGara.setDataPartecipazione(caldataTerminiPartecipazione);
                      }

                      if(dataInvito != null){
                        Calendar caldataInvito = Calendar.getInstance();
                        caldataInvito.setTime(dataInvito);
                        datiGara.setDataInvito(caldataInvito);
                      }

                      datiGara.setImportoAggiudicazione(importoAggiudicazione);
                      datiGara.setNumeroAttoAggiudicazione(numAttoAggiudicazione);




                      //si fa l'AGGIUDICA
                      WSERPRdaResType res = gestioneWSERPManager.wserpAggiudicaRdaGara(username, password, servizio, odaArray, datiGara);
                      if(res.isEsito()){
                        //tutto ok
                        //INVIO I DOCUMENTI...SOLO QUANDO STO AGGIORNANDO LO STATO A CONCLUSA e esito e' OK ovviamente
                        //e ovviamente non li ho già mandati
                        if(new Long(3).equals(codStato) && "".equals(numeroRdo)){
                          //selezione su v_gare_docditta con busta=1/2/3 dell'aggiudicatario
                          //anche con idprg='PG' o solo 'PA' ?
                          String selezioneDocDaInviare = "select w.digdesdoc, w.dignomdoc, w.idprg, w.iddocdig, d.busta, d.bustadesc, d.datarilascio " +
                          		" from v_gare_docditta d" +
                          		" join w_docdig w on d.iddocdg=w.iddocdig and d.idprg=w.idprg  " +
                          		" where d.codgar = ? and d.ngara = ? and d.codimp = ? and (d.busta = ? or d.busta = ? or d.busta = ?) ";

                          List<?> listaDatiDoc = sqlManager.getListVector(selezioneDocDaInviare,
                              new Object[] {codgar,ngara,dittaAggiudicataria,new Long(1),new Long(2),new Long(3)});
                          if(listaDatiDoc.size() > 0){
                            WSERPAllegatoType[] allegatoArray = new WSERPAllegatoType[listaDatiDoc.size()];
                            for (int d = 0; d < listaDatiDoc.size(); d++){
                              WSERPAllegatoType allegato = new WSERPAllegatoType();
                              String desdoc = SqlManager.getValueFromVectorParam(listaDatiDoc.get(d), 0).stringValue();
                              desdoc = UtilityStringhe.convertiNullInStringaVuota(desdoc);
                              String nomdoc = SqlManager.getValueFromVectorParam(listaDatiDoc.get(d), 1).stringValue();
                              nomdoc = UtilityStringhe.convertiNullInStringaVuota(nomdoc);
                              String idprg = SqlManager.getValueFromVectorParam(listaDatiDoc.get(d), 2).stringValue();
                              Long iddocdig = (Long) SqlManager.getValueFromVectorParam(listaDatiDoc.get(d), 3).getValue();
                              Long busta = (Long) SqlManager.getValueFromVectorParam(listaDatiDoc.get(d), 4).getValue();
                              String bustadesc = SqlManager.getValueFromVectorParam(listaDatiDoc.get(d), 5).stringValue();
                              Date dataRilascio = (Date) SqlManager.getValueFromVectorParam(listaDatiDoc.get(d), 6).getValue();
                              String strDataRicezione = UtilityDate.convertiData(dataRilascio, UtilityDate.FORMATO_AAAAMMGG);

                              BlobFile digogg = fileAllegatoManager.getFileAllegato(idprg, iddocdig);
                              allegato.setNome(nomdoc); //comprensivo di estensione
                              allegato.setContenuto(digogg.getStream());
                              allegato.setTitolo(bustadesc);
                              allegato.setPath(strDataRicezione);

                              allegatoArray[d] = allegato;
                            }

                              int ris = gestioneWSERPManager.wserpUploadFileAllegati(username, password, servizio, codgar, ngara, allegatoArray);
                              if(ris >= 0){
                                //aggiorno numrdo
                                this.gestioneWSERPManager.updNumeroRdo(tipoWSERP, codgar, ngara, "OK");
                              }



                          }


                        }//CODSTATO AGGIUDICATO



                      }else{
                        //BAD segnalo?
                        String errMsg = "Errore in aggiornamento dello stato della gara " + ngara + " - " + res.getMessaggio();
                        logger.error(errMsg);

                      }
                	  
                  }//FNM
                  

                }//for
              }

            } catch (SQLException e) {
              throw new GestoreException("Errore nella lettura delle gare da aggiornare ", null, e);
            } catch (IOException e) {
              throw new GestoreException("Errore nell'invio dei documenti dell'aggiudicatario ", null, e);
            }


        }//if FNM
        //cf24
        
        if("RAIWAY".equals(tipoWSERP)) {
        	
            //Data di Filtro - Sottraggo 6 mesi alla data odierna / ora un anno (APPALTI-880)
            date = new Date();
            cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MONTH, -6);
            filterDate = cal.getTime();
            
            String selezioneGareDaAggiornare = "select v.codgar, v.codice, v.stato, v.esito, r.numrda, v.codstato, g.ditta, v.genere," +
            		" i.cfimp, i.pivimp, g.codcig, r.esercizio, g1.numrdo, t.numavcp," +
            		" t.dteoff, t.desoff, g.dattoa, g.datneg, t.iterga, t.dpubav, t.dinvit, t.dtepar, g.esineg, g.iaggiu, g.ncomag " +
                    " from v_gare_statoesito v" +
                    " LEFT JOIN gare g ON v.codice = g.ngara" +
                    " LEFT JOIN gare1 g1 ON v.codgar = g1.codgar1 and g.ngara=g1.ngara" +
                    " INNER JOIN torn t ON v.codgar = t.codgar" +
                    " INNER JOIN garerda r ON v.codgar = r.codgar and r.numrda is not null" +
                    " LEFT JOIN impr i ON g.ditta = i.codimp" +
                    " WHERE v.stato is not null " +
                    " and (g.daatto is null or (g.daatto is not null and g.daatto > ?)) and (g.datneg is null or (g.datneg is not null and g.datneg > ?))" +
                    " and (exists(select e.ngara from pubg e where v.codice = e.ngara and e.tippubg = 12)" +
                    " or exists (select b.codgar9 from pubbli b where v.codgar = b.codgar9 and (b.tippub = 11 or b.tippub = 13)))";
            
            try {
                List<?> listaGareDaAgg = sqlManager.getListVector(selezioneGareDaAggiornare, new Object[] {filterDate,filterDate});
                if(listaGareDaAgg.size() > 0){
                  for (int g = 0; g < listaGareDaAgg.size(); g++){
                    String codgar = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 0).stringValue();
                    codgar = UtilityStringhe.convertiNullInStringaVuota(codgar);
                    String ngara = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 1).stringValue();
                    ngara = UtilityStringhe.convertiNullInStringaVuota(ngara);
                    String stato = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 2).stringValue();
                    stato = UtilityStringhe.convertiNullInStringaVuota(stato);
                    String esito = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 3).stringValue();
                    esito = UtilityStringhe.convertiNullInStringaVuota(esito);
                    String numeroRda = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 4).stringValue();
                    numeroRda = UtilityStringhe.convertiNullInStringaVuota(numeroRda);
                    Long codStato = (Long) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 5).getValue();
                    String dittaAggiudicataria = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 6).stringValue();
                    dittaAggiudicataria = UtilityStringhe.convertiNullInStringaVuota(dittaAggiudicataria);
                    Long genere = (Long) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 7).getValue();
                    String codiceFiscaleAggiudicatario = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 8).stringValue();
                    codiceFiscaleAggiudicatario = UtilityStringhe.convertiNullInStringaVuota(codiceFiscaleAggiudicatario);
                    String partitaIvaAggiudicatario = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 9).stringValue();
                    partitaIvaAggiudicatario = UtilityStringhe.convertiNullInStringaVuota(partitaIvaAggiudicatario);
                    String codiceCig = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 10).stringValue();
                    codiceCig = UtilityStringhe.convertiNullInStringaVuota(codiceCig);
                    String esercizio = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 11).stringValue();
                    esercizio = UtilityStringhe.convertiNullInStringaVuota(esercizio);
                    String numeroRdo = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 12).stringValue();
                    numeroRdo = UtilityStringhe.convertiNullInStringaVuota(numeroRdo);
                    String numeroAvcp = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 13).stringValue();
                    numeroAvcp = UtilityStringhe.convertiNullInStringaVuota(numeroAvcp);
                    Date dataPresOfferta = (Date) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g),14).getValue();
                    Date dataEsameOffertaAmm = (Date) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g),15).getValue();
                    Date dataAttoAgg = (Date) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g),16).getValue();
                    Date dataAnnullamento = (Date) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g),17).getValue();
                    Long iterga = (Long) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 18).getValue();
                    Date dataBandoAvviso = (Date) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g),19).getValue();
                    Date dataInvitoDitte = (Date) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g),20).getValue();
                    Date dataTerminiPartecipazione = (Date) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g),21).getValue();
                    Long esitoGaraNonAggiudicata = (Long) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 22).getValue();
                    Double importoAggiudicazione = null;
                    Object objImpAgg = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 23).getValue();
                    if (objImpAgg instanceof Long){
                        importoAggiudicazione = ((Long) objImpAgg).doubleValue();
                    }else{
                      if(objImpAgg instanceof Double){
                        importoAggiudicazione = (Double) objImpAgg;
                      }
                    }
                    
                	//cambiare anche sopra IdLotto per stato FNM
                	  
                      WSERPOdaType[] odaArray = new WSERPOdaType[1];
                      WSERPOdaType oda = new WSERPOdaType();
                      
                      //calcolo qui le ditte aggiudicatarie
                      List<?> listaImpreseDaAgg = sqlManager.getListVector("select codimp from impr i,gare g where g.ditta = i.codimp and codgar1=?", new Object[] {codgar});
                      if(listaImpreseDaAgg.size() > 0){
                    	  for (int m = 0; m < listaImpreseDaAgg.size(); m++){
                    		  dittaAggiudicataria = SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(m), 0).stringValue();
                    		  dittaAggiudicataria = UtilityStringhe.convertiNullInStringaVuota(dittaAggiudicataria);
                              if(!"".equals(dittaAggiudicataria)) {
                            	  //invoco il ws di creazione
                           	  
                                  WSERPFornitoreType fornitore = new WSERPFornitoreType();
                                  //recupero i dati da  DITTA, considerando che potrebbe anche essere una RTI
                                    Long tipimp = (Long)this.sqlManager.getObject("select tipimp from impr where codimp = ?", new Object[]{dittaAggiudicataria});
                                    if(tipimp != null && (tipimp.longValue()==3 || tipimp.longValue()==10)){
                                     String mandataria = (String)this.sqlManager.getObject("select coddic from ragimp" +
                                            " where codime9 = ? and impman = ? ", new Object[]{dittaAggiudicataria, "1"});
                                     dittaAggiudicataria= mandataria;
                                   }
                                    String selectIMPR = "select cfimp,pivimp,nomimp,indimp,locimp,nazimp,capimp,nciimp,proimp," +
                                          "emai2ip,telimp,emaiip,telcel,iscrcciaa,faximp,coorba,cgenimp,numiso,datiso,octiso" +
                                          " from impr where codimp = ?";
                                    List<?> datiIMPR = sqlManager.getListVector(selectIMPR, new Object[] { dittaAggiudicataria });
                                    if (datiIMPR != null && datiIMPR.size() > 0) {
                                      // Dati AC
                                      String cfimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 0).getValue();
                                      cfimp = UtilityStringhe.convertiNullInStringaVuota(cfimp);
                                      String pivimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 1).getValue();
                                      pivimp = UtilityStringhe.convertiNullInStringaVuota(pivimp);
                                      String nomimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 2).getValue();
                                      String indimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 3).getValue();
                                      indimp = UtilityStringhe.convertiNullInStringaVuota(indimp);
                                      String locimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 4).getValue();
                                      Long nazimp = (Long) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 5).getValue();
                                      String siglanaz = "";//"Non presente";
                                      if(nazimp != null){
                                        if(new Long(1).equals(nazimp)){
                                          siglanaz = "IT";
                                        }else{
                                          siglanaz = (String) this.sqlManager.getObject("select tab2d1 from tab2 where tab2cod= ?" +
                                                  " and tab2tip =? ", new Object[]{"G_z23",nazimp.toString()});
                                        }
                                      }
                                      String capimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 6).getValue();
                                      String nciimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 7).getValue();
                                      nciimp = UtilityStringhe.convertiNullInStringaVuota(nciimp);
                                      String proimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 8).getValue();
                                      proimp = UtilityStringhe.convertiNullInStringaVuota(proimp);
                                      String pecimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 9).getValue();
                                      pecimp = UtilityStringhe.convertiNullInStringaVuota(pecimp);
                                      if("".equals(pecimp)){
                                        pecimp = "";//"Non presente";
                                      }
                                      String telimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 10).getValue();
                                      String cellimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 12).getValue();
                                      telimp = UtilityStringhe.convertiNullInStringaVuota(telimp);
                                      cellimp = UtilityStringhe.convertiNullInStringaVuota(cellimp);
                                      if("".equals(telimp)){
                                        if("".equals(cellimp)){
                                          telimp = "";//"Non presente";
                                        }else{
                                          telimp = cellimp;
                                        }
                                      }
                                      String mailimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 11).getValue();
                                      mailimp = UtilityStringhe.convertiNullInStringaVuota(mailimp);
                                      if("".equals(mailimp)){
                                        mailimp = "";//"Non presente";
                                      }
                                      String iscrcciaa = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 13).getValue();
                                      iscrcciaa = UtilityStringhe.convertiNullInStringaVuota(iscrcciaa);
                                      Boolean iscrCCIAABool = false;
                                      if("1".equals(iscrcciaa)){
                                        iscrCCIAABool = true;
                                      }

                                      String faximp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 14).getValue();
                                      faximp = UtilityStringhe.convertiNullInStringaVuota(faximp);
                                      if("".equals(faximp)){
                                        faximp = "";//"Non presente";
                                      }

                                      String iban = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 15).getValue();
                                      iban = UtilityStringhe.convertiNullInStringaVuota(iban);
                                      
                                      String cgenimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 16).getValue();
                                      cgenimp = StringUtils.stripToEmpty(cgenimp);
                                      
                                      String numiso = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 17).getValue();
                                      Date datiso = (Date) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 18).getValue();
                                      Long octiso = (Long) SqlManager.getValueFromVectorParam(datiIMPR.get(0), 19).getValue();
                                      String orgCertificatore = null;
                                      if(octiso!= null) {
                                          orgCertificatore = (String) this.sqlManager.getObject("select tab1desc from tab1 where tab1cod= ?" +
                                                  " and tab1tip =? ", new Object[]{"Ag021",octiso});
                                      }
                                      

                                      fornitore.setCodiceFornitore(dittaAggiudicataria);
                                      fornitore.setCodiceFiscale(cfimp);
                                      fornitore.setPartitaIva(pivimp);
                                      fornitore.setRagioneSociale(nomimp);
                                      fornitore.setLocalita(locimp);
                                      fornitore.setIndirizzo(indimp);
                                      fornitore.setCap(capimp);
                                      fornitore.setProvincia(proimp);
                                      fornitore.setCivico(nciimp);
                                      fornitore.setPec(pecimp);
                                      fornitore.setTelefono(telimp);
                                      fornitore.setEmail(mailimp);
                                      fornitore.setNazionalita(siglanaz);
                                      /*
                                       codificaISO 3166 a 2 lettere
                                       tab2 G_z23
                                       */
                                      fornitore.setIscrizioneCCIAA(iscrCCIAABool);
                                      fornitore.setCellulare(cellimp);
                                      fornitore.setFax(faximp);
                                      
                                      if(numiso!=null) {
                                    	  fornitore.setNumIscrizoneISO(numiso);
                                      }
                                      
                                      if(datiso != null){
                                          Calendar caldataIso = Calendar.getInstance();
                                          caldataIso.setTime(datiso);
                                          fornitore.setDataIscrizioneISO(caldataIso);
                                      }
                                      if(octiso != null) {
                                    	 String octisoStr = octiso.toString();
                                    	 fornitore.setOctIscrizioneISO(octisoStr); 
                                      }
                                      //viene provvisoriamente usata url per la descr. org. certificatore
                                      if(orgCertificatore != null) {
                                    	 fornitore.setUrl(orgCertificatore); 
                                      }
                                      
                                      if(tipimp!=null) {
                                    	  fornitore.setTipologia(tipimp.toString());  
                                      }
                                      
                                      if(!"".equals(cgenimp)) {
                                    	 fornitore.setIdFornitore(cgenimp); 
                                      }
                                    }
                            	  
                            	  WSERPFornitoreResType wserpFornitoreRes = gestioneWSERPManager.wserpCreaFornitore(null, null, "WSERP", fornitore);
                            	  if(wserpFornitoreRes.isEsito()){
                            		  WSERPFornitoreType fornitoreERP = wserpFornitoreRes.getFornitore();
                            	      String idFornitore = fornitoreERP.getIdFornitore();
                                      idFornitore = UtilityStringhe.convertiNullInStringaVuota(idFornitore);
                                      if(!"".equals(idFornitore)){
                                    	  this.gestioneWSERPManager.updFornitore(tipoWSERP, dittaAggiudicataria, idFornitore, null);
                                      }
                            	  }
                              }//if ditta aggiudicataria
                    	  }
                      }
                          

                      
                      
                      
                      
                      
                      //invoco il ws aggiorna procedura
                      oda.setCodiceRda(numeroRda);
                      oda.setIdLotto(ngara);
                      oda.setNumeroGara(codgar);

                      odaArray[0] = oda;

                      WSERPGaraType datiGara = new WSERPGaraType();
                      
                      datiGara.setCodiceCig(codiceCig);
                      
                      if(!"".equals(esito)){
                          stato = stato + "-" + esito;
                      }
                      stato = stato.toUpperCase();
                      datiGara.setStato(stato);

                      if(dataAttoAgg != null){
	                      Calendar caldataAttoAgg = Calendar.getInstance();
	                      caldataAttoAgg.setTime(dataAttoAgg);
	                      datiGara.setDataAggiudicazione(caldataAttoAgg);
                      }
                      
                      
                      datiGara.setImportoAggiudicazione(importoAggiudicazione);
                      
                      WSERPRdaResType res = gestioneWSERPManager.wserpAggiudicaRdaGara(null, null, "WSERP", odaArray, datiGara);
                      if(res.isEsito()){
                          Timestamp ts = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
                          String tsStr = "OK-" + ts.toString();
                    	  this.gestioneWSERPManager.updNumeroRdo(tipoWSERP, codgar, ngara, tsStr);
                      }
            
                  }//for
                }

            } catch (SQLException e) {
                throw new GestoreException("Errore nella lettura delle gare da aggiornare ", null, e);
            }

        	
        	
        }
        



      //}//if

    if (logger.isDebugEnabled())
      logger.debug("updateStatoGara: fine metodo");
  }

}


