/*
 * Created on 16/mar/2018
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.bl;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.utility.UtilityFiscali;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;


import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;


public class ControlloDati190Manager {

  java.sql.Date nowTime = new java.sql.Date(Calendar.getInstance().getTime().getTime());
  
  static Logger      logger = Logger.getLogger(ControlloDati190Manager.class);
  
  private SqlManager sqlManager;
  private GenChiaviManager genChiaviManager;
  private PgManager pgManager;
  
  private static String queryAppa = "select dconsd, dult, itotaleliqui from appa where codlav = ? and nappal = ?";
  private static String queryDitg = "select dittao, rtofferta from ditg where ngara5 = ? order by nprogg";
  private static String queryGarecont = "select dverbc, dcertu, ngaral, impliq, impqua from garecont gc where ((gc.codimp=? or gc.codimp is null) and " +
  		"((gc.ngara=? and gc.ncont=1) or (gc.ngara=? and (gc.ngaral is null or gc.ngaral=?))))";
  private static String queryTorn = "select accqua, dinvit, dpubav, cenint, altrisog, dteoff, iterga from torn where codgar = ?" ;
  private static String queryImpr = "select pivimp, cfimp, nomest, tipimp, nazimp, nomimp from impr where codimp = ?";
  private static String queryGare = "select dattoa, datneg, not_gar, codcig, codgar1, clavor, numera, ditta, iaggiu from gare where ngara = ?" ;
  private static String queryRagimp = "select coddic, impman from ragimp where codime9 = ?";
  private static String queryCig = "select ngara from gare where codcig = ? and ngara != ?";
  private static String queryUffint = "select cfein, nomein from uffint where codein = ?";
  private static String queryInsert = "insert into garcontr190 (ID,NGARA,CODGAR,DATACONTR,TITOLO,MESSAGGIO) values (?,?,?,?,?,?)";
  //private static String queryDelete = "delete from garcontr190 where ngara = ?";
  private static String queryDeleteByCodgar = "delete from garcontr190 where codgar = ?";
  
  
  /**
   * Set SqlManager 
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
      this.sqlManager = sqlManager;
  }

  /**
   * @param genChiaviManager the genChiaviManager to set
   */
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
      this.genChiaviManager = genChiaviManager;
  }
  
  /**
   * Set PgManager
   *
   * @param pglManager
   */
  public void setPgManager(PgManager pgManager) {
      this.pgManager = pgManager;
  }
  
  public HashMap<String,Object> controllaDati(String codgar) throws SQLException{
    
    if (logger.isDebugEnabled())
      logger.debug("controllaDati190SingolaGara: inizio metodo");

    HashMap<String,Object> rispostaDef = new HashMap<String,Object>();
    ArrayList<Object> listaErroriGara = new ArrayList<Object>();
    ArrayList<Object> lotti = new ArrayList<Object>();  
    ArrayList<String> codiga = new ArrayList<String>();
    String titoloMessaggio;
    String corpoMessaggio;
    
    this.sqlManager.update(queryDeleteByCodgar, new Object[] { codgar });
    String ngara = codgar.replace("$", "");
    
    String queryIsGaraLotti= "select ngara, genere from gare where ngara = ?";    
    String queryLotti = "select ngara, codiga from gare where codgar1 = ? and (genere is null or genere != 3) order by codiga";
    
    Long genere = null;
    String ngaraEstratta = null;
    
    rispostaDef.put("ngara", ngara);
    Vector<JdbcParametro> DatiBusta = this.sqlManager.getVector(queryIsGaraLotti, new Object[] { ngara });
    if(DatiBusta!=null && DatiBusta.size()>0){
      ngaraEstratta= (String) (DatiBusta.get(0)).getValue();
      genere= (Long) (DatiBusta.get(1)).getValue();
      }
    
    if(ngaraEstratta != null && (genere == null || genere != 3)){
      //GARA A LOTTO UNICO
      listaErroriGara.add((Object) new Object[] {"lotto unico:" ,controllaDatiGare(ngaraEstratta)});
      rispostaDef.put("GaraLotti", false);
    }
    else{
      //GARA DIVISA A LOTTI
      //SE "ngaraEstratta" E' NULL ALLORA SI TRATTA DI UNA GARA A PLICHI DISTINTI 
      rispostaDef.put("GaraLotti", true);
      List<?> datiLotti = this.sqlManager.getListVector(queryLotti, new Object[] { ngara });
      if(datiLotti.size()<= 0){
        ngaraEstratta=null;
        ArrayList<Object> errori = new ArrayList<Object>();
        titoloMessaggio = "Gara divisa in lotti";
        corpoMessaggio = "Non sono dettagliati i lotti della gara";
        errori.add((Object) new Object[]{titoloMessaggio,corpoMessaggio});
        listaErroriGara.add((Object) new Object[] {"-" ,errori});
        int index = genChiaviManager.getNextId("GARCONTR190");
        this.sqlManager.update(queryInsert,new Object[] {index,ngaraEstratta,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});
      }
      for(int i = 0; i < datiLotti.size(); i++ ){
        lotti.add((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 0).getValue());
        codiga.add((String) SqlManager.getValueFromVectorParam(datiLotti.get(i), 1).getValue());
      }
      for(int i = 0; i < lotti.size(); i++ ){
        listaErroriGara.add((Object) new Object[] { codiga.get(i) ,controllaDatiGare((String)lotti.get(i))});
      }
      rispostaDef.put("codigaLotti", codiga);
    }
    rispostaDef.put("liste", listaErroriGara);
    
    if (logger.isDebugEnabled())
      logger.debug("controllaDati190SingolaGara: fine metodo");
    
    return rispostaDef;
  }
  
  public ArrayList<Object> controllaDatiGare(String ngara) throws SQLException{
    
    String titoloMessaggio;
    String corpoMessaggio;
    ArrayList<Object> risposta = new ArrayList<Object>();
   
    int index;
   
    //dati tabella gare
    String codgar = null;
    Date gareDattoa = null;
    Date gareDatneg = null;
    String gareNotGar = null;
    String gareCodcig = null;
    String gareClavor = null;
    Long gareNumera = null;
    String gareDitta = null;
    Double gareIaggiu = null;
    
    //dati tabella torn
    Date  tornDinvit = null;
    Date tornDpubav = null;
    String tornCenint = null;
    Long tornAltrisog = new Long(0);
    String tornAccqua = null;
    Date tornDteoff = null;
    Long tornIterga = new Long(0);
    
    //dati tabella impr
    String imprPivimp = null;
    String imprCfimp = null;
    String imprNomest = null;
    Long imprTipimp = new Long(0);
    Long imprNazimp = null;
    String imprNomimp = null;
    
    //dati tabella garecont
    Date garecontDverbc = null;
    Date garecontDcertu = null;
    String garecontNgaral = null;
    Double garecontImpliq = null;
    Double garecontImpqua = null;
    
    //dati tabella ditg
    String ditgDittao = null;
    String ditgRtofferta = null;
    
    //dati tabella ragimp
    String ragimpImpman = null;
    String ragimpCodicc = null;
    
    //dati uffint
    String uffintCfein = null;
    String uffintNomein = null;
    
    //dati appa
    Date appaDconsd = null;
    Date appaDult = null;
    Double appaItotaleliqui = null;
    
    Vector<JdbcParametro> datiGara = this.sqlManager.getVector(queryGare, new Object[] { ngara });
    
    if(datiGara!=null && datiGara.size()>0){
      gareDattoa = (Date) (datiGara.get(0)).getValue();
      gareDatneg = (Date) (datiGara.get(1)).getValue();
      gareNotGar = (String) (datiGara.get(2)).getValue();
      gareCodcig = (String) (datiGara.get(3)).getValue();
      codgar =(String) (datiGara.get(4)).getValue();
      gareClavor = (String) (datiGara.get(5)).getValue();
      gareNumera = (Long) (datiGara.get(6)).getValue();
      gareDitta = (String)(datiGara.get(7)).getValue();
      gareIaggiu = (Double)(datiGara.get(8)).getValue();
    }
    //logger.info("TABELLA GARE : dattoa= " + gareDattoa + ", dat_neg= " + gareDatneg + ", not_gar= " + gareNotGar + ", cod_cig= " + gareCodcig + ", " + gareDitta + ". ");
    
    Vector<JdbcParametro> datiTorn = this.sqlManager.getVector(queryTorn, new Object[] { codgar });
    if(datiTorn!=null && datiTorn.size()>0){
      tornAccqua = (String) (datiTorn.get(0)).getValue();
      tornDinvit = (Date) (datiTorn.get(1)).getValue();
      tornDpubav = (Date) (datiTorn.get(2)).getValue();
      tornCenint = (String) (datiTorn.get(3)).getValue();
      tornAltrisog = (Long) (datiTorn.get(4)).getValue();
      tornDteoff = (Date) (datiTorn.get(5)).getValue();
      tornIterga = (Long) (datiTorn.get(6)).getValue();
      if(tornIterga==null)
        tornIterga=new Long(2);
    }
    //logger.info("TABELLA TORN : ACCQUA= " + tornAccqua + ", DINVIT= " + tornDinvit + ", DPUBAV= " + tornDpubav + ", CENINT= " + tornCenint + ", ALTRISOG=" + tornAltrisog + ", DATEOFF= " + tornDteoff +".");
    
    Vector<JdbcParametro> datiGarecont = sqlManager.getVector(queryGarecont, new Object[] { gareDitta,ngara,codgar,ngara});
    
    if(datiGarecont!=null && datiGarecont.size()>0){
      garecontDverbc = (Date) (datiGarecont.get(0)).getValue();
      garecontDcertu = (Date) (datiGarecont.get(1)).getValue();
      garecontNgaral = (String) (datiGarecont.get(2)).getValue();
      garecontImpliq = (Double) (datiGarecont.get(3)).getValue();
      garecontImpqua = (Double) (datiGarecont.get(4)).getValue();
    }
    //logger.info("TABELLA GARECONT : DVERBC= " + garecontDverbc + ", DCERTU= " + garecontDcertu + ", NGARAL= " + garecontNgaral + ", IMPLIQ= " + garecontImpliq + ".");
    
    boolean nessunaDataValorizzata = true;
    java.util.Date utilDate = (java.util.Date) new GregorianCalendar(2012,11,1).getTime();
    Date dic2012 =  new java.sql.Date(utilDate.getTime());
    
    int year = Calendar.getInstance().get(Calendar.YEAR);
    java.util.Date utilThisyear = (java.util.Date) new GregorianCalendar(year - 1,0,1).getTime();
    Date thisYear =  new java.sql.Date(utilThisyear.getTime());
    
    
    
    if(tornDpubav != null){
      if(tornIterga == 1 || tornIterga == 2 || tornIterga == 4 ){
        nessunaDataValorizzata = false;
        if(tornDpubav.before(dic2012)){
          titoloMessaggio = "Anno di riferimento";
          corpoMessaggio = "La data di pubblicazione è precedente a dicembre 2012, quindi la gara non è di interesse ai fini dell'adempimento L.190/2012";
        risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
        return risposta;}
      }
    }
    if(tornDinvit != null){
      if(!(tornIterga == 1)){
        nessunaDataValorizzata = false;
        if(tornDinvit.before(dic2012)){
          titoloMessaggio = "Anno di riferimento";
          corpoMessaggio = "La data di invio invito è precedente a dicembre 2012, quindi la gara non è di interesse ai fini dell'adempimento L.190/2012";
          risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
          return risposta;
        }
      }
    }
    if(gareDatneg != null){
      nessunaDataValorizzata = false;
      if(gareDatneg.before(dic2012)){
        titoloMessaggio = "Anno di riferimento";
        corpoMessaggio = "La data di esito gara non aggiudicata è precedente a dicembre 2012, quindi la gara non è di interesse ai fini dell'adempimento L.190/2012";
        risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
        return risposta;}
      
      if(gareDatneg.before(thisYear)){
        titoloMessaggio = "Anno di riferimento";
        corpoMessaggio = "La data di esito gara non aggiudicata è precedente al " +(year-1) + ", quindi la gara non è di interesse per l'adempimento L.190/2012";
        risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
        return risposta;}
    }
    
  //**********************CONTROLLI SULLA DATA DELL'ATTO DI AGGIUDICAZIONE**************************//
    
    Date ultimazioneContratto;
    Date inizioContratto;
    Double importoLiquidato = null;
    if(!"1".equals(tornAccqua) && !(tornAltrisog != null && (tornAltrisog == 2 || tornAltrisog == 3)) && (gareClavor != null)){
      Vector<JdbcParametro> datiAppa = this.sqlManager.getVector(queryAppa, new Object[] { gareClavor , gareNumera});
      if(datiAppa!=null && datiAppa.size()>0){
        appaDconsd = (Date) (datiAppa.get(0)).getValue();
        appaDult = (Date) (datiAppa.get(1)).getValue();
        appaItotaleliqui = (Double) (datiAppa.get(2)).getValue();
      }
      inizioContratto = appaDconsd;
      ultimazioneContratto = appaDult;
      importoLiquidato = appaItotaleliqui;
    }
    else{
      inizioContratto = garecontDverbc;
      ultimazioneContratto = garecontDcertu;
      importoLiquidato = garecontImpliq;
    }
    
    if(gareDattoa != null){
      nessunaDataValorizzata = false;
      if(gareDattoa.before(dic2012)){
        titoloMessaggio = "Anno di riferimento";
        corpoMessaggio = "La data atto aggiudicazione è precedente a dicembre 2012, quindi la gara non è di interesse ai fini dell'adempimento L.190/2012";
        risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
        return risposta;}
      
      String corpoMessaggioData="ultimazione contratto";
      Date ultimazioneContrattoControllo = ultimazioneContratto;
      if(inizioContratto == null && ("1".equals(tornAccqua) || (tornAltrisog != null && (tornAltrisog == 2 || tornAltrisog == 3)))){
        ultimazioneContrattoControllo = gareDattoa;
        corpoMessaggioData = "atto aggiudicazione";
        }
      if(ultimazioneContrattoControllo != null && ultimazioneContrattoControllo.before(thisYear)){
        titoloMessaggio = "Anno di riferimento";
        corpoMessaggio = "La data " + corpoMessaggioData + " è precedente al " +(year-1) + ", quindi la gara non è di interesse ai fini dell'adempimento L.190/2012";
        risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
        return risposta;}
    }
    
    if(nessunaDataValorizzata){
      titoloMessaggio = "Anno di riferimento";
      corpoMessaggio = "Non è specificata la data atto aggiudicazione nè la data di esito gara non aggiudicata";
      if(!(tornIterga == 1)){
        corpoMessaggio += " nè di invio invito";
      }
      if(tornIterga == 1 || tornIterga == 2 || tornIterga == 4){
        corpoMessaggio += " nè di pubblicazione del bando";
      }
      risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
      index = genChiaviManager.getNextId("GARCONTR190");
      //String queryInsert = "insert into garcontr190 (ID,NGARA,CODGARA,DATACONTR,TITOLO,MESSAGGIO) values (?,?,?,?,?,?)";
      this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});
      }
        
  //**********************CONTROLLI SUI DATI GENERALI DELLA GARA**************************//
    
   if(gareCodcig == null){
     
     titoloMessaggio = "Dati generali mancanti o non validi";
     corpoMessaggio = "Codice CIG non valorizzato";
     index = genChiaviManager.getNextId("GARCONTR190");
     risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio})); 
     //String queryInsert = "insert into garcontr190 (ID,NGARA,CODGARA,DATACONTR,TITOLO,MESSAGGIO) values (?,?,?,?,?,?)";
     this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});
     
   }else{
     if(!pgManager.controlloCodiceCIG(gareCodcig)){
       
       titoloMessaggio = "Dati generali mancanti o non validi";
       corpoMessaggio = "Codice CIG non valido";
       index = genChiaviManager.getNextId("GARCONTR190");
       risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
       this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});
     }
     else{
       ArrayList<String> GareStessoCig = new ArrayList<String>();
       List<?> codiciCig = this.sqlManager.getListVector(queryCig, new Object[] { gareCodcig ,ngara });
       for(int i = 0; i < codiciCig.size(); i++ ){
         String temp = (String) SqlManager.getValueFromVectorParam(codiciCig.get(i), 0).getValue();
         GareStessoCig.add(temp);
       }
       if(GareStessoCig.size() > 0){
         String temp = "";
         for(int i = 0; i < GareStessoCig.size(); i++ ){
           if(i+1 == GareStessoCig.size()) { 
             temp = temp + GareStessoCig.get(i);
             }
           else{
             temp = temp + GareStessoCig.get(i) + ", ";
             }
         }
         titoloMessaggio = "Dati generali mancanti o non validi";
         corpoMessaggio = "Ci sono altre gare con lo stesso codice CIG (cod.gara:" + temp + ")";
         risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio})); 
         index = genChiaviManager.getNextId("GARCONTR190");
         this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});

       }
     }
   }
   
   if(gareNotGar == null){
     titoloMessaggio = "Dati generali mancanti o non validi";
     corpoMessaggio = "Oggetto della gara o lotto di gara non valorizzato";
     risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
     index = genChiaviManager.getNextId("GARCONTR190");
     this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});

   }
   if(tornCenint == null){
     titoloMessaggio = "Dati generali mancanti o non validi";
     corpoMessaggio = "Riferimento alla stazione appaltante non valorizzato";
     risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
     index = genChiaviManager.getNextId("GARCONTR190");
     this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});

   }else{
     Vector<JdbcParametro> datiUffint = this.sqlManager.getVector(queryUffint, new Object[] { tornCenint });
     if(datiUffint!=null && datiUffint.size()>0){
       uffintCfein = (String) (datiUffint.get(0)).getValue();
       uffintNomein = (String) (datiUffint.get(1)).getValue();
     }
     if(uffintCfein == null || (!UtilityFiscali.isValidCodiceFiscale(uffintCfein) && !UtilityFiscali.isValidPartitaIVA(uffintCfein))){
       titoloMessaggio = "Dati generali mancanti o non validi";
       corpoMessaggio = "Codice fiscale stazione appaltante non valorizzato o con formato non valido";
       risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
       index = genChiaviManager.getNextId("GARCONTR190");
       this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});

     }
     if(uffintNomein == null){
       titoloMessaggio = "Dati generali mancanti o non validi";
       corpoMessaggio = "Denominazione stazione appaltante non valorizzata";
       risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
       index = genChiaviManager.getNextId("GARCONTR190");
       this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});
     }
   }
   
   //**********************CONTROLLI SUI PARTECIPANTI ALLA GARA**************************//
   
   java.util.Date todayUtils = new java.util.Date();
   Date today =  new Date(todayUtils.getTime());
   
   //bando aggiudicato o data termine offerte superata
   if(gareDatneg == null && (gareDattoa != null || (tornDteoff != null && (tornDteoff.before(today) && !DateUtils.isSameDay(tornDteoff,today))))){
    boolean partecipantiDefiniti = false;
    //cerco la lista delle ditte parteciapnti
    List<?> ditg = this.sqlManager.getListVector(queryDitg, new Object[] { ngara });
    for(int i = 0; i < ditg.size(); i++ ){
      ditgDittao = (String) SqlManager.getValueFromVectorParam(ditg.get(i), 0).getValue();
      ditgRtofferta = (String) SqlManager.getValueFromVectorParam(ditg.get(i), 1).getValue();
      if(ditgRtofferta ==  null){partecipantiDefiniti = true;
      Vector<JdbcParametro> datiImpr = this.sqlManager.getVector(queryImpr, new Object[] { ditgDittao });
      if(datiImpr!=null && datiImpr.size()>0){
        imprPivimp = (String) (datiImpr.get(0)).getValue();
        imprCfimp = (String) (datiImpr.get(1)).getValue();
        imprNomest = (String) (datiImpr.get(2)).getValue();
        imprTipimp = (Long) (datiImpr.get(3)).getValue();
        imprNazimp = (Long) (datiImpr.get(4)).getValue();
        imprNomimp = (String) (datiImpr.get(5)).getValue();
      }
      String nomeImpr=imprNomimp;
      if(imprNomimp==null)
        imprNomimp=ditgDittao;
      
      //logger.info("TABELLA IMPR : PIVIMP= " + imprPivimp + ", CFIMP= " + imprCfimp + ", NOME= " + imprNomest + ", TIPIMP= " + imprTipimp + ".");
        
      //**********************CONTROLLI SE E' UN RT**************************//
      if((imprTipimp != null) && (imprTipimp == 3 || imprTipimp == 10)){
        
          boolean trovataMandataria = false;
          
          String NomeRagg = nomeImpr;
          
          List<?> datiRagimp = this.sqlManager.getListVector(queryRagimp, new Object[] { ditgDittao });
          
          if(datiRagimp.size() < 2){
            titoloMessaggio = "Ditte partecipanti alla gara";
            corpoMessaggio = "Raggruppamento '"+NomeRagg+"' con meno di due componenti";             
            risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
            index = genChiaviManager.getNextId("GARCONTR190");
            this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});
          }

          for(int n = 1; n <= datiRagimp.size(); n++ ){
            ragimpCodicc = (String) SqlManager.getValueFromVectorParam(datiRagimp.get(n-1), 0).getValue();
            ragimpImpman = (String) SqlManager.getValueFromVectorParam(datiRagimp.get(n-1), 1).getValue();
            
            if(ragimpImpman != null && ragimpImpman.equals("1")){trovataMandataria = true;}
            datiImpr = this.sqlManager.getVector(queryImpr, new Object[] { ragimpCodicc });
            if(datiImpr!=null && datiImpr.size()>0){
              imprPivimp = (String) (datiImpr.get(0)).getValue();
              imprCfimp = (String) (datiImpr.get(1)).getValue();
              imprNomest = (String) (datiImpr.get(2)).getValue();
              imprTipimp = (Long) (datiImpr.get(3)).getValue();
              imprNazimp = (Long) (datiImpr.get(4)).getValue();
              imprNomimp = (String) (datiImpr.get(5)).getValue();
            }
            
            String nomeCompRag=imprNomimp;
            if(imprNomimp==null)
              nomeCompRag=ragimpCodicc;

            //logger.info("TABELLA GARE : PIVIMP= " + imprPivimp + ", CFIMP= " + imprCfimp + ", NOMEST= " + imprNomest + ", TIPIMP= " + imprTipimp + "NAZIMP=" + imprNazimp);
            if(imprNomest == null){
              titoloMessaggio = "Ditte partecipanti alla gara";
              corpoMessaggio = "Ragione sociale della ditta '" + nomeCompRag  + "', componente il raggruppamento '" + NomeRagg + "',  non valorizzata";
              risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
              index = genChiaviManager.getNextId("GARCONTR190");
              this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});

            }
            
            if((imprNazimp == null || imprNazimp == 1) && (imprCfimp == null || (!UtilityFiscali.isValidCodiceFiscale(imprCfimp) && !UtilityFiscali.isValidPartitaIVA(imprCfimp))) &&  (imprPivimp == null || !UtilityFiscali.isValidPartitaIVA(imprPivimp))){
              titoloMessaggio = "Ditte partecipanti alla gara";
              corpoMessaggio = "Codice fiscale o partita Iva della ditta '" + nomeCompRag  + "', componente il raggruppamento '" + NomeRagg + "',  non valorizzati o con formato non valido";             
              risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
              index = genChiaviManager.getNextId("GARCONTR190");
              this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});
            }
            else{
              if(imprNazimp != null && imprNazimp != 1 && (imprCfimp == null &&  imprPivimp == null)){
                titoloMessaggio = "Ditte partecipanti alla gara";
                corpoMessaggio = "Codice fiscale o partita Iva della ditta '" + nomeImpr + "' non valorizzati";  
                risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
                index = genChiaviManager.getNextId("GARCONTR190");
                this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});
              }
            }

          }
          
          if(!trovataMandataria){
            titoloMessaggio = "Ditte partecipanti alla gara";
            corpoMessaggio = "Raggruppamento '"+NomeRagg+"' senza mandataria";  
            risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
            index = genChiaviManager.getNextId("GARCONTR190");
            this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});
          }
        }
      
        //**********************CONTROLLI SE NON E' UN RAGGRUPPAMENTO**************************//
      
        else{
          if(imprNomest == null){
            titoloMessaggio = "Ditte partecipanti alla gara";
            corpoMessaggio = "Ragione sociale della ditta '" + nomeImpr + "' non valorizzata";  
            risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
            index = genChiaviManager.getNextId("GARCONTR190");
            this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});

          }
          if((imprNazimp == null || imprNazimp == 1) && (imprCfimp == null || (!UtilityFiscali.isValidCodiceFiscale(imprCfimp) && !UtilityFiscali.isValidPartitaIVA(imprCfimp))) &&  (imprPivimp == null || !UtilityFiscali.isValidPartitaIVA(imprPivimp))){
            titoloMessaggio = "Ditte partecipanti alla gara";
            corpoMessaggio = "Codice fiscale o partita Iva della ditta '" + nomeImpr + "' non valorizzati o con formato non valido";  
            risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
            index = genChiaviManager.getNextId("GARCONTR190");
            this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});
          } 
          else{
            if(imprNazimp != null && imprNazimp != 1 && (imprCfimp == null &&  imprPivimp == null)){
              titoloMessaggio = "Ditte partecipanti alla gara";
              corpoMessaggio = "Codice fiscale o partita Iva della ditta '" + nomeImpr + "' non valorizzati";  
              risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
              index = genChiaviManager.getNextId("GARCONTR190");
              this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});
            }
          }
        }
      }
     }
    if(!partecipantiDefiniti){
      titoloMessaggio = "Ditte partecipanti alla gara";
      corpoMessaggio = "Non sono specificate le ditte partecipanti alla gara";  
      risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
      index = genChiaviManager.getNextId("GARCONTR190");
      //String queryInsert = "insert into garcontr190 (ID,NGARA,CODGARA,DATACONTR,TITOLO,MESSAGGIO) values (?,?,?,?,?,?)";
      this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});
      }
   }
   
   //**********************CONTROLLI SOLO SE GARA AGGIUDICATA**************************//
   String corpoMessaggioImporto="";   
   if(gareDattoa != null && gareDatneg == null){
     if(gareDitta == null){
       titoloMessaggio = "Dati di aggiudicazione e contratto mancanti o non validi";
       corpoMessaggio = "Ditta aggiudicataria non valorizzata";
       risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
       index = genChiaviManager.getNextId("GARCONTR190");
       this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});
    
     }
     if(gareIaggiu == null||gareIaggiu < 0){
       titoloMessaggio = "Dati di aggiudicazione e contratto mancanti o non validi";
       corpoMessaggioImporto="non valorizzato";
       if(gareIaggiu!=null && gareIaggiu<0){
         corpoMessaggioImporto="negativo";
       }
       corpoMessaggio = "Importo di aggiudicazione " + corpoMessaggioImporto;
       risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
       index = genChiaviManager.getNextId("GARCONTR190");
       this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});
    
     }
     if("1".equals(tornAccqua) && (garecontImpqua == null || garecontImpqua < 0 )){
       titoloMessaggio = "Dati di aggiudicazione e contratto mancanti o non validi";
       corpoMessaggioImporto="non valorizzato";
       if(garecontImpqua!=null && garecontImpqua<0){
         corpoMessaggioImporto="negativo";
       }
       corpoMessaggio = "Importo complessivo dell'accordo quadro " + corpoMessaggioImporto;
       risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
       index = genChiaviManager.getNextId("GARCONTR190");
       this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});
    
     }
   }
   if(ultimazioneContratto != null){
     if(inizioContratto == null ){
       titoloMessaggio = "Dati di aggiudicazione e contratto mancanti o non validi";
       corpoMessaggio = "Data inizio esecuzione contratto non valorizzata";
       risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
       index = genChiaviManager.getNextId("GARCONTR190");
       this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});  
     
     }
     else{
       if(inizioContratto.after(ultimazioneContratto)){
         titoloMessaggio = "Dati di aggiudicazione e contratto mancanti o non validi";
         corpoMessaggio = "Data inizio esecuzione contratto successiva a quella di ultimazione";
         risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
         index = genChiaviManager.getNextId("GARCONTR190");
         this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});
      
       }
     }
   }
   if(importoLiquidato != null && importoLiquidato < 0){
     titoloMessaggio = "Dati di aggiudicazione e contratto mancanti o non validi";
     corpoMessaggio = "Importo liquidato negativo";
     risposta.add((Object) (new Object[] {titoloMessaggio,corpoMessaggio}));
     index = genChiaviManager.getNextId("GARCONTR190");
     this.sqlManager.update(queryInsert,new Object[] {index,ngara,codgar,new java.sql.Timestamp(System.currentTimeMillis()),titoloMessaggio,corpoMessaggio});
  
   }
    return risposta;
  } 
}
