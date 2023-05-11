/*
 * Created on 29/03/2022
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
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class RaiwayWSERPManager {
  /** Logger */
  static Logger               logger                = Logger.getLogger(RaiwayWSERPManager.class);

  private SqlManager          sqlManager;

  private GestioneWSERPManager gestioneWSERPManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }


  public WSERPRdaResType  inviaDatiProcedura (String username, String password, HashMap datiMask) throws GestoreException, SQLException{

	  WSERPRdaResType wserpRdaRes = new WSERPRdaResType();
	  
      WSERPOdaType[] odaArray = new WSERPOdaType[1];
      WSERPOdaType oda = new WSERPOdaType();

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
	     
	     
         String selezioneGareDaAggiornare = "select v.codgar, v.codice, v.stato, v.esito, r.numrda, v.codstato, g.ditta, v.genere," +
           		" i.cfimp, i.pivimp, g.codcig, r.esercizio, g1.numrdo, t.numavcp," +
           		" t.dteoff, t.desoff, g.dattoa, g.datneg, t.iterga, t.dpubav, t.dinvit, t.dtepar, g.esineg, g.iaggiu, g.ncomag " +
                   " from v_gare_statoesito v" +
                   " LEFT JOIN gare g ON v.codice = g.ngara" +
                   " LEFT JOIN gare1 g1 ON v.codgar = g1.codgar1 and g.ngara=g1.ngara" +
                   " INNER JOIN torn t ON v.codgar = t.codgar" +
                   " INNER JOIN garerda r ON v.codgar = r.codgar and r.numrda is not null" +
                   " LEFT JOIN impr i ON g.ditta = i.codimp" +
                   " WHERE v.codgar = ? and v.codice =? " +
                   " and (exists(select e.ngara from pubg e where v.codice = e.ngara and e.tippubg = 12)" +
                   " or exists (select b.codgar9 from pubbli b where v.codgar = b.codgar9 and (b.tippub = 11 or b.tippub = 13)))" +
                   " UNION" +
                   " select vl.codgar1, vl.codice, vl.stato, vl.esito, r.numrda, vl.codstato, g.ditta, cast(vl.genere as int)," +
           		" i.cfimp, i.pivimp, g.codcig, r.esercizio, g1.numrdo, t.numavcp," +
           		" t.dteoff, t.desoff, g.dattoa, g.datneg, t.iterga, t.dpubav, t.dinvit, t.dtepar,g.esineg, g.iaggiu, g.ncomag " +
           		  " from v_gare_statoesitolotti vl INNER JOIN gare g ON vl.codice = g.ngara" +
           		  " INNER JOIN gare1 g1 ON vl.codgar1 = g1.codgar1 and g.ngara=g1.ngara" +
           		  " INNER JOIN torn t ON vl.codgar1 = t.codgar INNER JOIN garerda r ON vl.codgar1 = r.codgar and r.numrda is not null" +
           		  " LEFT JOIN impr i ON g.ditta = i.codimp" +
           		  " WHERE vl.codgar1 = ? and vl.codice =? and  vl.stato is not null and vl.genere = '300'" +
           		  " and (exists(select e.ngara from pubg e where vl.codice = e.ngara and e.tippubg = 12)" +
           		  " or exists (select b.codgar9 from pubbli b where vl.codgar1 = b.codgar9 and (b.tippub = 11 or b.tippub = 13)))";
         List<?> listaGareDaAgg = sqlManager.getListVector(selezioneGareDaAggiornare, new Object[] {codgar,ngara,codgar,ngara});
         if(listaGareDaAgg.size() > 0){
        	 
             for (int g = 0; g < listaGareDaAgg.size(); g++){
                 String stato = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 2).stringValue();
                 stato = UtilityStringhe.convertiNullInStringaVuota(stato);
                 String esito = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 3).stringValue();
                 esito = UtilityStringhe.convertiNullInStringaVuota(esito);
                 String numeroRda = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 4).stringValue();
                 numeroRda = UtilityStringhe.convertiNullInStringaVuota(numeroRda);
                 String codiceCig = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 10).stringValue();
                 codiceCig = UtilityStringhe.convertiNullInStringaVuota(codiceCig);
                 Date dataAttoAgg = (Date) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g),16).getValue();
                 Double importoAggiudicazione = null;
                 Object objImpAgg = SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 23).getValue();
                 if (objImpAgg instanceof Long){
                     importoAggiudicazione = ((Long) objImpAgg).doubleValue();
                 }else{
                   if(objImpAgg instanceof Double){
                     importoAggiudicazione = (Double) objImpAgg;
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
                 
                 wserpRdaRes = gestioneWSERPManager.wserpAggiudicaRdaGara(null, null, "WSERP", odaArray, datiGara);
                 
             }
        	 
         }else {
        	 wserpRdaRes.setMessaggio("La gara non risulta in uno stato da aggiornare!");
         }
	  
	  return wserpRdaRes;
	  
  }

  
  public WSERPFornitoreResType  inviaDatiFornitore (String username, String password, HashMap datiMask) throws GestoreException, SQLException{

	  WSERPFornitoreResType wserpFornitoreRes = new WSERPFornitoreResType();
	  String idFornitore = (String)datiMask.get("idFornitore");
	  String dittaAggiudicataria = (String)datiMask.get("ditta");
	  
	  WSERPFornitoreType fornitore = new WSERPFornitoreType();
       //recupero i dati da  DITTA
       if (dittaAggiudicataria != null) {
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
              *
             codificaISO 3166 a 2 lettere
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
             if(orgCertificatore != null) {
           	 fornitore.setOctIscrizioneISO(orgCertificatore); 
             }
             
             if(tipimp!=null) {
           	  fornitore.setTipologia(tipimp.toString());  
             }
             
             if(!"".equals(cgenimp)) {
           	 fornitore.setIdFornitore(cgenimp); 
             }
             
             wserpFornitoreRes = gestioneWSERPManager.wserpCreaFornitore(null, null, "WSERP", fornitore);

           }//if dati IMPR
    	   
    	   
       }//if ditta aggiudicataria
	  
	  
	  return wserpFornitoreRes;
	  
  }
	  



 }


