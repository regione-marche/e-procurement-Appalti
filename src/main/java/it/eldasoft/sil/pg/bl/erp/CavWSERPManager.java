/*
 * Created on 23/11/2020
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.erp;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.utility.UtilityMath;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreResType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreType;
import it.maggioli.eldasoft.ws.erp.WSERPGaraType;
import it.maggioli.eldasoft.ws.erp.WSERPOdaType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class CavWSERPManager {
  /** Logger */
  static Logger               logger                = Logger.getLogger(CavWSERPManager.class);

  private SqlManager          sqlManager;

  private GestioneWSERPManager gestioneWSERPManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }


   public WSERPFornitoreResType  inviaDatiFornitore (String username, String password, HashMap datiMask) throws GestoreException, SQLException{
     WSERPFornitoreResType wserpFornitoreRes = new WSERPFornitoreResType();
     String idFornitore = (String)datiMask.get("idFornitore");
     String gruppoConti = (String)datiMask.get("gruppoConti");
     String condizioniPagamento = (String)datiMask.get("condizioniPagamento");
     String modalitaPagamento = (String)datiMask.get("modalitaPagamento");
     String ditta = (String)datiMask.get("ditta");

       WSERPFornitoreType fornitore = new WSERPFornitoreType();
       //recupero i dati da  DITTA
       if (ditta != null) {
         Long tipimp = (Long)this.sqlManager.getObject("select tipimp from impr where codimp = ?", new Object[]{ditta});
         if(tipimp != null && (tipimp.longValue()==3 || tipimp.longValue()==10)){
          String mandataria = (String)this.sqlManager.getObject("select coddic from ragimp" +
                 " where codime9 = ? and impman = ? ", new Object[]{ditta, "1"});
          String dittaRTI = ditta;
          ditta= mandataria;
        }
         String selectIMPR = "select cfimp,pivimp,nomimp,indimp,locimp,nazimp,capimp,nciimp,proimp," +
               "emai2ip,telimp,emaiip,telcel,iscrcciaa,faximp,coorba" +
               " from impr where codimp = ?";
         List<?> datiIMPR = sqlManager.getListVector(selectIMPR, new Object[] { ditta });
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
               siglanaz = (String) this.sqlManager.getObject("select tab2tip from tab2 where tab2cod= ?" +
                       " and tab2d1 =? ", new Object[]{"UBUY1",nazimp.toString()});
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

             fornitore.setCodiceFornitore(ditta);
             if(!"".equals(idFornitore)){
               fornitore.setIdFornitore(idFornitore);
             }
             fornitore.setIban(iban);

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
           fornitore.setGruppoConti(gruppoConti);
           fornitore.setIscrizioneCCIAA(iscrCCIAABool);
           fornitore.setCellulare(cellimp);
           fornitore.setFax(faximp);
           fornitore.setCondizionePagamento(condizioniPagamento);
           fornitore.setModalitaPagamento(modalitaPagamento);
         }
         //chiamo il Crea Fornitore

         wserpFornitoreRes = gestioneWSERPManager.wserpCreaFornitore(username, password, "WSERP", fornitore);

       }


     return wserpFornitoreRes;
   }


   public WSERPRdaResType  inviaDatiContratto (String username, String password, HashMap datiMask) throws GestoreException, SQLException{
     WSERPRdaResType wserpRdaRes = new WSERPRdaResType();
     String codgar = (String)datiMask.get("codgar");
     String ngara = (String)datiMask.get("ngara");
     String codcig = (String)datiMask.get("codcig");
     String codcigaq = (String)datiMask.get("codcigaq");
     String ditta = (String)datiMask.get("ditta");
     String idFornitore = (String)datiMask.get("idFornitore");
     String tipoContratto = (String)datiMask.get("tipoContratto");
     String oggettoGara = (String)datiMask.get("oggettoGara");
     Double impapp = (Double)datiMask.get("impapp");
     Double iaggiu = (Double)datiMask.get("iaggiu");
     Double ribagg = (Double)datiMask.get("ribagg");
     Double riboepv = (Double)datiMask.get("riboepv");
     Long modlicg = (Long)datiMask.get("modlicg");
     String numeroRepertorioContratto = (String)datiMask.get("nrepat");
     Date dataContratto = (Date)datiMask.get("daatto");
     
     String[] listaDitte = null;
     String dittaRTI = "";
     String linkrda = "";
     int esitoComunicazioneRda = 0;

     WSERPGaraType datiGara = new WSERPGaraType();

       Vector<?> datiFascicolo = this.sqlManager.getVector("select anno,numero from WSFASCICOLO where key1 = ?", new Object[] { ngara });
       if(datiFascicolo!=null && datiFascicolo.size()>0){
         String codiceFascicolo = "";
         Long annoFascicolo = (Long) SqlManager.getValueFromVectorParam(datiFascicolo, 0).getValue();
         if(annoFascicolo!=null){
           codiceFascicolo += annoFascicolo.toString();
         }
         String numeroFascicolo = (String) SqlManager.getValueFromVectorParam(datiFascicolo, 1).getValue();
         numeroFascicolo = UtilityStringhe.convertiNullInStringaVuota(numeroFascicolo);
         codiceFascicolo += "/" + numeroFascicolo;
         codiceFascicolo = UtilityStringhe.convertiNullInStringaVuota(codiceFascicolo);
         if(!"".equals(codiceFascicolo)){
           datiGara.setCodiceFascicolo(codiceFascicolo);
         }
       }
       datiGara.setCodiceCig(codcig);
       datiGara.setCodiceCigMaster(codcigaq);
       oggettoGara = UtilityStringhe.convertiNullInStringaVuota(oggettoGara);
       if(oggettoGara.length()>120){
         oggettoGara = oggettoGara.substring(0, 119);
       }

       //provvisorio definire oggettogara
       datiGara.setDefinizioneCig(oggettoGara);

       //selezionare anche CUPPRG e poi va trasmesso 
       
       Vector<?> datiGareAppa  = this.sqlManager.getVector(
           "select gare.clavor,gare.numera,appa.dult,coalesce(appa.dinlav,appa.dconsd),appa.datult,appa.sivca_inv,appa.sivca_int, gare.cupprg" +
           " from GARE,APPA where GARE.CLAVOR = APPA.CODLAV and GARE.NUMERA=APPA.NAPPAL and GARE.NGARA = ?", new Object[] { ngara });
       if(datiGareAppa!=null && datiGareAppa.size()>0){
         String codiceLavoro = (String) SqlManager.getValueFromVectorParam(datiGareAppa, 0).getValue();
         Long numeroAppalto = (Long) SqlManager.getValueFromVectorParam(datiGareAppa, 1).getValue();
         datiGara.setCodiceLavoro(codiceLavoro);
         //provvisorio definire numeroAppalto
         if(numeroAppalto!=null){
           datiGara.setNumeroAppalto(numeroAppalto);
         }
         Date dataUlt = (Date) SqlManager.getValueFromVectorParam(datiGareAppa, 2).getValue();
         if(dataUlt != null){
           Calendar calDataUltimazione = Calendar.getInstance();
           calDataUltimazione.setTime(dataUlt);
           datiGara.setDataUltimazione(calDataUltimazione);
         }
         Date dataInizioVal = (Date) SqlManager.getValueFromVectorParam(datiGareAppa, 3).getValue();
         if(dataInizioVal != null){
           Calendar calDataInizioValidita = Calendar.getInstance();
           calDataInizioValidita.setTime(dataInizioVal);
           datiGara.setDataInizioValidita(calDataInizioValidita);
         }

         Date dataFineVal = (Date) SqlManager.getValueFromVectorParam(datiGareAppa, 4).getValue();
         if(dataFineVal != null){
           Calendar calDataFineValidita = Calendar.getInstance();
           calDataFineValidita.setTime(dataFineVal);
           datiGara.setDataFineValidita(calDataFineValidita);
         }
         String codiceSivcaInvestimento = (String) SqlManager.getValueFromVectorParam(datiGareAppa, 5).getValue();
         datiGara.setCodiceSivcaInvestimento(codiceSivcaInvestimento);
         String codiceSivcaIntervento = (String) SqlManager.getValueFromVectorParam(datiGareAppa, 6).getValue();
         datiGara.setCodiceSivcaIntervento(codiceSivcaIntervento);
         
         String codiceCup = (String) SqlManager.getValueFromVectorParam(datiGareAppa, 7).getValue();
         if(!"".equals(StringUtils.stripToEmpty(codiceCup))) {
           String[] cupArray = new String[1];
           cupArray[0] = codiceCup;
           datiGara.setCodiceCupArray(cupArray);
         }

         datiGara.setImportoAggiudicazione(iaggiu);
         if (new Long(6).equals(modlicg)) {
           datiGara.setRibassoAggiudicazione(riboepv);
         }else{
           datiGara.setRibassoAggiudicazione(ribagg);
         }

         datiGara.setTipoContratto(tipoContratto);
         datiGara.setNumeroProgressivoContratto(new Long(1));
         datiGara.setCodiceFornitore(idFornitore);

       }
       
       Vector<?> datiGarecont  = this.sqlManager.getVector(
               "select gc.nprotcoorba,gc.dprotcoorba from GARECONT gc,GARE ga"
               + " where gc.codimp = ga.ditta and ((gc.ngara=ga.ngara and gc.ncont=1) or (gc.ngara=ga.codgar1 and (gc.ngaral is null or gc.ngaral=ga.ngara)))"
               + " and ga.ditta is not null and ga.ngara= ? and ga.ditta = ?", new Object[] { ngara, ditta });
       
       if(datiGarecont!=null && datiGarecont.size()>0){
           String nprotcoorba = (String) SqlManager.getValueFromVectorParam(datiGarecont, 0).getValue();
           nprotcoorba = UtilityStringhe.fillLeft(nprotcoorba, '0', 7);
           datiGara.setNumeroProtocolloCcd(nprotcoorba);
           Date dprotcoorba = (Date) SqlManager.getValueFromVectorParam(datiGarecont, 1).getValue();
           if(dprotcoorba != null){
             Calendar calDprotcoorba = Calendar.getInstance();
             calDprotcoorba.setTime(dprotcoorba);
             datiGara.setDataProtocolloCcd(calDprotcoorba);
           }
       }
       
       if(dataContratto != null){
           Calendar calDataContratto = Calendar.getInstance();
           calDataContratto.setTime(dataContratto);
           datiGara.setDataContratto(calDataContratto);
       }
       numeroRepertorioContratto = UtilityStringhe.fillLeft(numeroRepertorioContratto, '0', 7);
       datiGara.setNumeroRepertorioContratto(numeroRepertorioContratto);
       
       
       listaDitte = new String[1];
       listaDitte[0] = ditta;
       if(listaDitte != null){
         for(int i=0;i<listaDitte.length;i++){
           ditta = listaDitte[i];
           //inizio integrazione ERP
           if(ditta != null){
             Long tipimp = (Long)this.sqlManager.getObject("select tipimp from impr where codimp = ?", new Object[]{ditta});
             if(tipimp != null && (new Long(3).equals(tipimp) || new Long(10).equals(tipimp))){
              String mandataria = (String)this.sqlManager.getObject("select coddic from ragimp" +
                     " where codime9 = ? and impman = ? ", new Object[]{ditta, "1"});
              dittaRTI = ditta;
              ditta= mandataria;
             }
             //CAV caso in cui si ha la lista delle lavorazioni
             List listaProdottiAggiudicataria = null;
             if(dittaRTI!=null && !"".equals(dittaRTI)){
               datiGara.setRti(true);
               //nel caso in cui l'aggiudicataria sia una RTI ..devo mandare i dati della mandataria
               //ma poi devo recuperare la lista lavori e forniture dalla rti
               listaProdottiAggiudicataria = this.sqlManager.getListVector(
                   "select CODGAR,NGARA,CODVOC,VOCE,UNIMISEFF,QUANTIEFF,PREOFF,CODCARR,CODRDA,POSRDA,CONTAF,DATACONS" +
                   " from V_GCAP_DPRE where CODGAR = ? and NGARA= ? and COD_DITTA = ?" +
                   " order by codrda,posrda ", new Object[] { codgar,ngara,dittaRTI });
             }else{
               listaProdottiAggiudicataria = this.sqlManager.getListVector(
                   "select CODGAR,NGARA,CODVOC,VOCE,UNIMISEFF,QUANTIEFF,PREOFF,CODCARR,CODRDA,POSRDA,CONTAF,DATACONS" +
                   " from V_GCAP_DPRE where CODGAR = ? and NGARA= ? and COD_DITTA = ?" +
                   " order by codrda,posrda", new Object[] { codgar,ngara,ditta });
             }
             if (listaProdottiAggiudicataria != null && listaProdottiAggiudicataria.size() > 0) {
               linkrda = "2";


                    //controllo sul completamento dei prezzi offerti
                   boolean controlloPrezziOfferti = true;
                   Double impCalcolato = new Double(0);
                   for (int k = 0; k < listaProdottiAggiudicataria.size(); k++) {
                     Vector vectProdottiAgg = (Vector) listaProdottiAggiudicataria.get(k);
                     Double quantieff = null;
                     Object quantieffObj = ((JdbcParametro) vectProdottiAgg.get(5)).getValue();
                     if(quantieffObj != null){
                       if (quantieffObj instanceof Long){
                         quantieff = ((Long) quantieffObj).doubleValue();
                       }else{
                         if(quantieffObj instanceof Double){
                           quantieff = (Double) quantieffObj;
                         }
                       }
                     }else{
                       controlloPrezziOfferti = false;
                       wserpRdaRes.setMessaggio("wserp.erp.mancataValorizzazioneRda.error");
                       break;
                     }

                     Double preoff = null;
                     Object preoffObj = ((JdbcParametro) vectProdottiAgg.get(6)).getValue();
                     if(preoffObj != null){
                       if (preoffObj instanceof Long){
                         preoff = ((Long) preoffObj).doubleValue();
                       }else{
                         if(preoffObj instanceof Double){
                           preoff = (Double) preoffObj;
                         }
                       }
                     }else{
                       controlloPrezziOfferti = false;
                       wserpRdaRes.setMessaggio("wserp.erp.mancataValorizzazioneRda.error");
                       break;
                     }

                     impCalcolato = impCalcolato + quantieff*preoff;
                   }//for di controllo

                   impCalcolato = UtilityMath.round(impCalcolato, 2);
                   Double tollerance = new Double(0.001);
                   if(Math.abs(impCalcolato-iaggiu)> tollerance){
                     controlloPrezziOfferti = true;
                     wserpRdaRes.setMessaggio("wserp.erp.mancataValorizzazioneRda.error");

                   }

                   if(controlloPrezziOfferti){
                     WSERPOdaType[] odaArray = new WSERPOdaType[listaProdottiAggiudicataria.size()];
                     for (int k = 0; k < listaProdottiAggiudicataria.size(); k++) {
                       Vector vectProdottiAgg = (Vector) listaProdottiAggiudicataria.get(k);
                       //String codgar = ((JdbcParametro) tmp.get(0)).getStringValue();
                       //String ngara = ((JdbcParametro) tmp.get(1)).getStringValue();
                       String codvoc = ((JdbcParametro) vectProdottiAgg.get(2)).getStringValue();
                       String voce = ((JdbcParametro) vectProdottiAgg.get(3)).getStringValue();
                       String unimiseff = ((JdbcParametro) vectProdottiAgg.get(4)).getStringValue();
                       Double quantieff = null;
                       Object quantieffObj = ((JdbcParametro) vectProdottiAgg.get(5)).getValue();
                       if (quantieffObj instanceof Long){
                         quantieff = ((Long) quantieffObj).doubleValue();
                       }else{
                         if(quantieffObj instanceof Double){
                           quantieff = (Double) quantieffObj;
                         }
                       }
                       Double preoff = null;
                       Object preoffObj = ((JdbcParametro) vectProdottiAgg.get(6)).getValue();
                       if (preoffObj instanceof Long){
                         preoff = ((Long) preoffObj).doubleValue();
                       }else{
                         if(preoffObj instanceof Double){
                           preoff = (Double) preoffObj;
                         }
                       }
                       String idLotto = ((JdbcParametro) vectProdottiAgg.get(7)).getStringValue();
                       String codiceRda = ((JdbcParametro) vectProdottiAgg.get(8)).getStringValue();
                       String posizioneRda = ((JdbcParametro) vectProdottiAgg.get(9)).getStringValue();
                       Long contaf = (Long) ((JdbcParametro) vectProdottiAgg.get(10)).getValue();
                       Date dataConsegna = (Date) ((JdbcParametro) vectProdottiAgg.get(11)).getValue();

                       WSERPOdaType oda = new WSERPOdaType();
                       oda.setCodiceFornitore(idFornitore);
                       oda.setQuantita(quantieff);
                         Vector<?> datiUlterioriRda = this.sqlManager.getVector("select wbe,cdc,contocoge from GARERDA g,GAREPOSRDA p" +
                               " where g.ID = p.RDA_ID and p.NUMRDA = ? and p.POSRDA = ? ", new Object[] { codiceRda,posizioneRda });
                         if(datiUlterioriRda!=null && datiUlterioriRda.size()>0){
                           String wbe = (String) SqlManager.getValueFromVectorParam(datiUlterioriRda, 0).getValue();
                           String cdc = (String) SqlManager.getValueFromVectorParam(datiUlterioriRda, 1).getValue();
                           String contocoge = (String) SqlManager.getValueFromVectorParam(datiUlterioriRda, 2).getValue();
                           oda.setWbs(wbe);
                           oda.setCentroCosto(cdc);
                           oda.setContoCoge(contocoge);
                         }
                       Vector<?> datiFornitoreAggiudicatario  = this.sqlManager.getVector(
                           "select CFIMP,PIVIMP,CGENIMP from IMPR where CODIMP = ?", new Object[] { ditta });
                       if(datiFornitoreAggiudicatario!=null && datiFornitoreAggiudicatario.size()>0){
                         String cfFornitore = (String) SqlManager.getValueFromVectorParam(datiFornitoreAggiudicatario, 0).getValue();
                         oda.setCfFornitore(cfFornitore);
                         String pivaFornitore = (String) SqlManager.getValueFromVectorParam(datiFornitoreAggiudicatario, 1).getValue();
                         oda.setPivaFornitore(pivaFornitore);
                       }

                       oda.setCodiceProdotto(codvoc);
                       oda.setCodiceRda(codiceRda);
                       oda.setPosizioneRda(posizioneRda);
                       oda.setDescrizione(voce);
                       //oda.setIdLotto(idLotto);
                       //oda.setNumeroOrdine(numeroOrdine);
                       oda.setPrezzo(preoff);
                       //Qui occorre fare delle verifiche di identificazione univoca della posizione rda
                       this.sqlManager.update(
                           "update gareposrda set prezzorib = ? where numrda = ? and posrda = ? ",
                           new Object[] { preoff, codiceRda, posizioneRda });
                       oda.setUm(unimiseff);
                       oda.setNumeroGara(ngara);
                       oda.setCod(contaf);
                       if(dataConsegna != null){
                         Calendar calDataCons = Calendar.getInstance();
                         calDataCons.setTime(dataConsegna);
                         oda.setDataConsegna(calDataCons);
                       }

                       odaArray[k] = oda;

                     }//for aggiudicazione

                       wserpRdaRes = this.gestioneWSERPManager.wserpAggiudicaRdaGara(username, password, "WSERP", odaArray, datiGara);

                   }


             }else{
               linkrda = "1";
               List listaRdaGara = null;
               Long genere = (Long) sqlManager.getObject("select genere from v_gare_genere where codgar = ? " +
                       "and codice = ?  ", new Object[] { codgar,ngara });

               //Qui la selezione va fatta su GAREPOSRDA del primo contratto
               if(Long.valueOf(300).equals(genere)) {
                   listaRdaGara  = this.sqlManager.getListVector(
                           "select r.CODGAR,p.NUMRDA,p.POSRDA,r.DATACONS,r.LUOGOCONS," +
                           "r.CODCARR,r.CODVOC,r.VOCE,r.UNIMIS,r.CODCAT,r.PERCIVA,r.ID,r.QUANTI,r.PREZUN," +
                           "p.WBE,p.CDC,p.CONTOCOGE,p.DESCRIZIONE,p.CODARTICOLO,p.QUANTITA,p.PREZZO,p.UNIMIS " +
                           " from GARERDA r,GAREPOSRDA p where r.ID=p.RDA_ID and r.CODGAR = ? and r.NGARA = ?" +
                           " order by r.numrda,r.posrda", new Object[] { codgar,ngara });
               }else {
                   listaRdaGara  = this.sqlManager.getListVector(
                           "select r.CODGAR,p.NUMRDA,p.POSRDA,r.DATACONS,r.LUOGOCONS," +
                           "r.CODCARR,r.CODVOC,r.VOCE,r.UNIMIS,r.CODCAT,r.PERCIVA,r.ID,r.QUANTI,r.PREZUN," +
                           "p.WBE,p.CDC,p.CONTOCOGE,p.DESCRIZIONE,p.CODARTICOLO,p.QUANTITA,p.PREZZO,p.UNIMIS " +
                           " from GARERDA r,GAREPOSRDA p where r.ID=p.RDA_ID and r.CODGAR = ?" +
                           " order by r.numrda,r.posrda", new Object[] { codgar });
               }

               if(listaRdaGara != null && listaRdaGara.size() > 0){
                 linkrda = "1";
                 int nRda = listaRdaGara.size();
                 if(nRda == 0){
                   Vector vectRdaGara = (Vector) listaRdaGara.get(0);
                   String numRda = ((JdbcParametro) vectRdaGara.get(1)).getStringValue();
                   String posRda = ((JdbcParametro) vectRdaGara.get(2)).getStringValue();
                   Date dataCons= (Date) ((JdbcParametro) vectRdaGara.get(3)).getValue();
                   String luogoCons = ((JdbcParametro) vectRdaGara.get(4)).getStringValue();
                   String idLotto = ((JdbcParametro) vectRdaGara.get(5)).getStringValue();
                   String codvoc = ((JdbcParametro) vectRdaGara.get(6)).getStringValue();
                   String voce = ((JdbcParametro) vectRdaGara.get(7)).getStringValue();
                   String um = ((JdbcParametro) vectRdaGara.get(8)).getStringValue();
                   String codCat = ((JdbcParametro) vectRdaGara.get(9)).getStringValue();
                   Long perciva = (Long) ((JdbcParametro) vectRdaGara.get(10)).getValue();
                   Long contaf = (Long) ((JdbcParametro) vectRdaGara.get(11)).getValue();
                   Double quantita = null;
                   Object quantitaObj = ((JdbcParametro) vectRdaGara.get(12)).getValue();
                   if (quantitaObj instanceof Long){
                     quantita = ((Long) quantitaObj).doubleValue();
                   }else{
                     if(quantitaObj instanceof Double){
                       quantita = (Double) quantitaObj;
                     }
                   }
                   Double prezzo = null;
                   Object prezzoObj = ((JdbcParametro) vectRdaGara.get(13)).getValue();
                   if (prezzoObj instanceof Long){
                     prezzo = ((Long) prezzoObj).doubleValue();
                   }else{
                     if(prezzoObj instanceof Double){
                       prezzo = (Double) prezzoObj;
                     }
                   }
                   Double prezzoPosizione = null;
                   Object prezzoPosizioneObj = ((JdbcParametro) vectRdaGara.get(14)).getValue();
                   if (prezzoPosizioneObj instanceof Long){
                     prezzoPosizione = ((Long) prezzoPosizioneObj).doubleValue();
                   }else{
                     if(prezzoPosizioneObj instanceof Double){
                       prezzoPosizione = (Double) prezzoPosizioneObj;
                     }
                   }


                   if(iaggiu != null){
                     WSERPOdaType[] odaArray = new WSERPOdaType[1];
                     WSERPOdaType oda = new WSERPOdaType();
                     if(quantita != null && !new Double(0).equals(quantita)){
                       oda.setQuantita(quantita);
                       Double impRda = iaggiu/quantita;
                       impRda = UtilityMath.round(impRda, 2);
                       oda.setPrezzo(impRda);
                     }else{
                       oda.setQuantita(quantita);
                       oda.setPrezzo(iaggiu);
                     }
                     oda.setCodiceFornitore(idFornitore);
                     oda.setCodiceRda(numRda);
                     oda.setPosizioneRda(posRda);
                     oda.setNumeroGara(ngara);
                     if(dataCons != null){
                       Calendar calDataCons = Calendar.getInstance();
                       calDataCons.setTime(dataCons);
                       oda.setDataConsegna(calDataCons);
                     }

                     oda.setIdLotto(idLotto);
                     codvoc= UtilityStringhe.convertiNullInStringaVuota(codvoc);
                     if("".equals(codvoc)){
                       codvoc="#";
                     }
                     oda.setCodiceProdotto(codvoc);
                     oda.setDescrizione(voce);
                     oda.setUm(um);
                     oda.setCod(contaf);

                       Vector<?> datiUlterioriRda = this.sqlManager.getVector("select wbe,cdc,contocoge from GARERDA g,GAREPOSRDA p" +
                             " where g.ID = p.RDA_ID and p.NUMRDA = ? and p.POSRDA = ?", new Object[] { numRda,posRda });
                       if(datiUlterioriRda!=null && datiUlterioriRda.size()>0){
                         String wbe = (String) SqlManager.getValueFromVectorParam(datiUlterioriRda, 0).getValue();
                         String cdc = (String) SqlManager.getValueFromVectorParam(datiUlterioriRda, 1).getValue();
                         String contocoge = (String) SqlManager.getValueFromVectorParam(datiUlterioriRda, 2).getValue();
                         oda.setWbs(wbe);
                         oda.setCentroCosto(cdc);
                         oda.setContoCoge(contocoge);
                       }

                     //AGGIUDICA
                     odaArray[0] = oda;

                     wserpRdaRes = this.gestioneWSERPManager.wserpAggiudicaRdaGara(username, password, "WSERP", odaArray, datiGara);

                     //settare codiceRda, idFornitore M
                     //CASO UNA RDA

                   }

                 }else{

                   if(iaggiu != null){

                     WSERPOdaType[] odaArrayAvm = new WSERPOdaType[listaRdaGara.size()];
                     for (int k = 0; k < listaRdaGara.size(); k++) {
                       Vector vectRdaGara = (Vector) listaRdaGara.get(k);
                       String numRda = ((JdbcParametro) vectRdaGara.get(1)).getStringValue();
                       String posRda = ((JdbcParametro) vectRdaGara.get(2)).getStringValue();
                       Date dataCons= (Date) ((JdbcParametro) vectRdaGara.get(3)).getValue();
                       String luogoCons = ((JdbcParametro) vectRdaGara.get(4)).getStringValue();
                       String idLotto = ((JdbcParametro) vectRdaGara.get(5)).getStringValue();
                       String codvoc = ((JdbcParametro) vectRdaGara.get(6)).getStringValue();
                       String voce = ((JdbcParametro) vectRdaGara.get(7)).getStringValue();
                       String um = ((JdbcParametro) vectRdaGara.get(8)).getStringValue();
                       String codCat = ((JdbcParametro) vectRdaGara.get(9)).getStringValue();
                       Long perciva = (Long) ((JdbcParametro) vectRdaGara.get(10)).getValue();
                       Long contaf = (Long) ((JdbcParametro) vectRdaGara.get(11)).getValue();
                       Double quantita = null;
                       Object quantitaObj = ((JdbcParametro) vectRdaGara.get(12)).getValue();
                       if (quantitaObj instanceof Long){
                         quantita = ((Long) quantitaObj).doubleValue();
                       }else{
                         if(quantitaObj instanceof Double){
                           quantita = (Double) quantitaObj;
                         }
                       }
                       Double prezzo = null;
                       Object prezzoObj = ((JdbcParametro) vectRdaGara.get(13)).getValue();
                       if (prezzoObj instanceof Long){
                         prezzo = ((Long) prezzoObj).doubleValue();
                       }else{
                         if(prezzoObj instanceof Double){
                           prezzo = (Double) prezzoObj;
                         }
                       }
                       String wbePosizione = ((JdbcParametro) vectRdaGara.get(14)).getStringValue();
                       String cdcPosizione = ((JdbcParametro) vectRdaGara.get(15)).getStringValue();
                       String contocogePosizione = ((JdbcParametro) vectRdaGara.get(16)).getStringValue();
                       String descrizionePosizione = ((JdbcParametro) vectRdaGara.get(17)).getStringValue();
                       String codArticoloPosizione = ((JdbcParametro) vectRdaGara.get(18)).getStringValue();

                       Double quantitaPosizione = null;
                       Object quantitaPosizioneObj = ((JdbcParametro) vectRdaGara.get(19)).getValue();
                       if (quantitaPosizioneObj instanceof Long){
                         quantitaPosizione = ((Long) quantitaPosizioneObj).doubleValue();
                       }else{
                         if(quantitaPosizioneObj instanceof Double){
                           quantitaPosizione = (Double) quantitaPosizioneObj;
                         }
                       }
                       Double prezzoPosizione = null;
                       Object prezzoPosizioneObj = ((JdbcParametro) vectRdaGara.get(20)).getValue();
                       if (prezzoPosizioneObj instanceof Long){
                         prezzoPosizione = ((Long) prezzoPosizioneObj).doubleValue();
                       }else{
                         if(prezzoPosizioneObj instanceof Double){
                           prezzoPosizione = (Double) prezzoPosizioneObj;
                         }
                       }
                       String umPosizione = ((JdbcParametro) vectRdaGara.get(21)).getStringValue();

                       Double impRda = prezzoPosizione; //importo unitario perche ora passo la quantita
                       Double quozRip = new Double(0);
                       if(impapp!= null){
                         quozRip = (impapp-iaggiu)/impapp;
                       }
                       if(impRda==null){
                         impRda= new Double(0);
                       }
                       Double impAggiudicazioneRipartito = impRda*(1-quozRip);
                       impAggiudicazioneRipartito = UtilityMath.round(impAggiudicazioneRipartito, 2);
                       WSERPOdaType[] odaArray = new WSERPOdaType[1];
                       WSERPOdaType oda = new WSERPOdaType();
                         oda.setCodiceFornitore(idFornitore);
                       oda.setQuantita(quantita);
                       oda.setPrezzo(impAggiudicazioneRipartito);
                       //Qui occorre fare delle verifiche di identificazione univoca della posizione rda
                       this.sqlManager.update(
                           "update gareposrda set prezzorib = ? where numrda = ? and posrda = ? ",
                           new Object[] { impAggiudicazioneRipartito, numRda, posRda });
                       oda.setCodiceRda(numRda);
                       oda.setPosizioneRda(posRda);
                       oda.setNumeroGara(ngara);
                       oda.setWbs(wbePosizione);
                       oda.setCentroCosto(cdcPosizione);
                       oda.setContoCoge(contocogePosizione);
                       oda.setDescrizione(descrizionePosizione);
                       codArticoloPosizione= UtilityStringhe.convertiNullInStringaVuota(codArticoloPosizione);
                       if("".equals(codArticoloPosizione)){
                         codArticoloPosizione="#";
                       }
                       oda.setCodiceProdotto(codArticoloPosizione);
                       oda.setQuantita(quantitaPosizione);
                       oda.setUm(umPosizione);

                       //AGGIUDICA
                         odaArrayAvm[k] = oda;

                     }//for

                     //AGGIUDICA
                       wserpRdaRes = this.gestioneWSERPManager.wserpAggiudicaRdaGara(username, password, "WSERP", odaArrayAvm, datiGara);

                   }//if iaggiu

                 } //if nrda
               }

             }//if  linkrda 1/2

             return wserpRdaRes;

           }//fine if ditta

         }//for ditte

       }



     return wserpRdaRes;
   }




  }


