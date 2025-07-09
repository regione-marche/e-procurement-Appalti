/*
 * Created on 10/12/2024
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.erp;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreResType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreType;
import it.maggioli.eldasoft.ws.erp.WSERPGaraType;
import it.maggioli.eldasoft.ws.erp.WSERPIndirizzoType;
import it.maggioli.eldasoft.ws.erp.WSERPOdaType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import net.sf.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class ConiWSERPManager {
  /** Logger */
  static Logger               logger                = Logger.getLogger(ConiWSERPManager.class);

  private SqlManager          sqlManager;

  private GestioneWSERPManager gestioneWSERPManager;

  /** Manager con funzionalita' generali */
  private GeneManager               geneManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }

  /**
   * @param geneManager
   *        geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  //Aggiudica RDA
  public WSERPRdaResType  inviaDatiAggiudicazione (String username, String password, String codgar, String ngara) throws Exception{

    WSERPRdaResType wserpRdaRes = new WSERPRdaResType();

    String genereSql = "select genere from v_gare_genere where codgar = ? ";
    Long genere = (Long) this.sqlManager.getObject(genereSql, new Object[]{codgar});

    String selezioneGareDaAggiornare = "select r.numrda, g.codcig, "
        + "g.iaggiu, i.cgenimp, i.tipimp, ra.coddic "
        + "from gare g "
        + "inner join garerda r on g.codgar1 = r.codgar and r.numrda is not null ";
    if (Long.valueOf(1).equals(genere) || Long.valueOf(3).equals(genere)) {
      selezioneGareDaAggiornare += "and g.ngara = r.ngara ";
    }
    selezioneGareDaAggiornare += "inner join torn t on t.codgar = g.codgar1 "
        + "left join tecni tc on t.codrup = tc.codtec "
        + "inner join impr i on i.codimp = g.ditta "
        + "left join ragimp ra on ra.codime9 = g.ditta and ra.impman='1' "
        + "where g.codgar1 = ? and g.ngara = ? "
        + "order by r.numrda ";
    List<?> listaGareDaAgg = sqlManager.getListVector(selezioneGareDaAggiornare, new Object[] {codgar,ngara});
    if(!listaGareDaAgg.isEmpty()){
      String selNumRdo = "select numrdo from gare1 where codgar1 = ? and ngara = ? ";
      String numRdoComplessivo = (String) this.sqlManager.getObject(selNumRdo, new Object[] {codgar, ngara });
      numRdoComplessivo = StringUtils.stripToEmpty(numRdoComplessivo);
      for (int g = 0; g < listaGareDaAgg.size(); g++){
        WSERPOdaType oda = new WSERPOdaType();
        String numrda = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 0).stringValue());
        String codcig = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 1).stringValue());
        Double iaggiu = checkDoubleValue(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 2).getValue());
        String cgenimp = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 3).stringValue());
        Long tipimp = (Long) SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 4).getValue();
        String coddic = UtilityStringhe.convertiNullInStringaVuota(SqlManager.getValueFromVectorParam(listaGareDaAgg.get(g), 5).stringValue());
        
        if(tipimp != null && (Long.valueOf(3).equals(tipimp) || Long.valueOf(10).equals(tipimp))){
          String recuperoCgenimpRti = "select i.cgenimp "
              + "from ragimp ra "
              + "inner join impr i on i.codimp = ra.coddic "
              + "where ra.coddic = ? ";
          cgenimp = UtilityStringhe.convertiNullInStringaVuota((String) sqlManager.getObject(recuperoCgenimpRti, new Object[] {coddic}));
        }

        oda.setCodiceRda(numrda);
        String codgarDaPassare = codgar;
        if(codgar != null && codgar.contains("$")) {
          codgarDaPassare = codgar.replace("$", "");
        }
        oda.setNumeroGara(codgarDaPassare);
        oda.setIdLotto(ngara);
        oda.setCodiceFornitore(cgenimp);
        oda.setCodiceCig(codcig);
        oda.setImporto(iaggiu);

        WSERPOdaType[] odaArray = new WSERPOdaType[1];
        odaArray[0] = oda;
        
       
        String numrdaKO = numrda+"-KO";
        
        if(numRdoComplessivo.indexOf(numrda)<0) {
           wserpRdaRes = gestioneWSERPManager.wserpAggiudicaRdaGara(username, password, "WSERP", odaArray, null);
           if(!numRdoComplessivo.isEmpty()) {
        	   numRdoComplessivo +=";";
           }
           if(!wserpRdaRes.isEsito()) {
        	   numRdoComplessivo += numrdaKO;
           }else {
               //wserpRdaRes.getMessaggio contiene il numero ODA
        	   numRdoComplessivo +=numrda+"-"+wserpRdaRes.getMessaggio();
           }
        }
 
        if(numRdoComplessivo.indexOf(numrdaKO)>=0) {
        	wserpRdaRes = gestioneWSERPManager.wserpAggiudicaRdaGara(username, password, "WSERP", odaArray, null);
            if(!wserpRdaRes.isEsito()) {
         	   ;
            }else {
                //wserpRdaRes.getMessaggio contiene il numero ODA
         	   String rdaoda =numrda+"-"+wserpRdaRes.getMessaggio();
         	   numRdoComplessivo = numRdoComplessivo.replace(numrdaKO, rdaoda);
            }
        }

      }//for numrda
      this.gestioneWSERPManager.updNumeroRdo("CONI", codgar, ngara, numRdoComplessivo);
    }

    return wserpRdaRes;

  }

  public Double checkDoubleValue(Object value) {
    Double valueDouble = null;
    if (value instanceof Number) {
      valueDouble = ((Number) value).doubleValue();
    }
    return valueDouble;
  }

  //Crea aggiorna fornitore
  public WSERPFornitoreResType  inviaDatiFornitore (String username, String password, String dittaAggiudicataria, String ngara) throws GestoreException, SQLException{

    WSERPFornitoreResType wserpFornitoreRes = new WSERPFornitoreResType();
    WSERPFornitoreType fornitore = new WSERPFornitoreType();
    //recupero i dati da  DITTA
    if (dittaAggiudicataria != null) {
      //devo controllare se la gara è un accordo quadro
      Boolean isAccQuadro = Boolean.FALSE;
      List<String> ditteAggiudicatarie = new ArrayList<>();
      String controlloAccQuadro = "select dittao from ditgaq where ngara = ?";
      List<String> ditteAccQuadro = this.sqlManager.getListVector(controlloAccQuadro, new Object[]{ngara});
      if(ditteAccQuadro != null && !ditteAccQuadro.isEmpty()){
        isAccQuadro = Boolean.TRUE;
        for (int i = 0; i < ditteAccQuadro.size(); i++) {
          ditteAggiudicatarie.add((String) SqlManager.getValueFromVectorParam(ditteAccQuadro.get(i), 0).getValue());
        }
      }
      if(Boolean.FALSE.equals(isAccQuadro)) {
        ditteAggiudicatarie.add(dittaAggiudicataria);
      }
      for (int i = 0; i < ditteAggiudicatarie.size(); i++) {
        String ditta = ditteAggiudicatarie.get(i);
        //Utilizzo una lista cosi' da poter gestire una eventuale RTI verticale
        List<String> ditteAggiudicatarieRTI = new ArrayList<>();
        //recupero i dati da  DITTA, considerando che potrebbe anche essere una RTI
        Vector<?> datiImpr = this.sqlManager.getVector("select tipimp,tiprti from impr where codimp = ?", new Object[]{ditta});
        Long tipimp = null;
        if(datiImpr != null && !datiImpr.isEmpty()) {
          tipimp = (Long)sqlManager.getValueFromVectorParam(datiImpr, 0).getValue();
          Long tiprti = (Long)sqlManager.getValueFromVectorParam(datiImpr, 1).getValue();

          if(tipimp != null && (Long.valueOf(3).equals(tipimp) || Long.valueOf(10).equals(tipimp))){
            if(tiprti != null && Long.valueOf(1).equals(tiprti)) {
              List<String> rtiVerticale = this.sqlManager.getListVector("select coddic from ragimp" +
                  " where codime9 = ? ", new Object[]{ditta});
              if(rtiVerticale != null && !rtiVerticale.isEmpty()){
                for (int x = 0; x < rtiVerticale.size(); x++) {
                  ditteAggiudicatarieRTI.add((String) SqlManager.getValueFromVectorParam(rtiVerticale.get(x), 0).getValue());
                }
              }
            } else {
              String mandataria = (String)this.sqlManager.getObject("select coddic from ragimp" +
                  " where codime9 = ? and impman = ? ", new Object[]{ditta, "1"});
              ditteAggiudicatarieRTI.add(mandataria);
            }
          } else {
            ditteAggiudicatarieRTI.add(ditta);
          }
        }

        for (int y = 0; y < ditteAggiudicatarieRTI.size(); y++) {
          String dittaRTI = ditteAggiudicatarieRTI.get(y);

          String selectIMPR = "select cgenimp,nomest,tipimp,natgiui,cfimp,pivimp,cognome,nome,pronas,cnatec,dnatec,inctec,oggsoc,indimp,nciimp,proimp,capimp,locimp,codcit,nazimp,telimp,faximp,telcel,emaiip,emai2ip,indweb " +
              "from impr where codimp = ?";
          Vector<?> datiIMPR = sqlManager.getVector(selectIMPR, new Object[] { dittaRTI });
          if (datiIMPR != null && !datiIMPR.isEmpty()) {
            String cgenimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 0).getValue();
            cgenimp = StringUtils.stripToEmpty(cgenimp);
            String nomest = (String) SqlManager.getValueFromVectorParam(datiIMPR, 1).getValue();
            nomest = StringUtils.stripToEmpty(nomest);
            Long tipimp1 = (Long) SqlManager.getValueFromVectorParam(datiIMPR, 2).getValue();
            Long natgiui = (Long) SqlManager.getValueFromVectorParam(datiIMPR, 3).getValue();
            String cfimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 4).getValue();
            cfimp = StringUtils.stripToEmpty(cfimp);
            String pivimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 5).getValue();
            pivimp = StringUtils.stripToEmpty(pivimp);
            String cognome = (String) SqlManager.getValueFromVectorParam(datiIMPR, 6).getValue();
            cognome = StringUtils.stripToEmpty(cognome);
            String nome = (String) SqlManager.getValueFromVectorParam(datiIMPR, 7).getValue();
            nome = StringUtils.stripToEmpty(nome);
            String pronas = (String) SqlManager.getValueFromVectorParam(datiIMPR, 8).getValue();
            pronas = StringUtils.stripToEmpty(pronas);
            String cnatec = (String) SqlManager.getValueFromVectorParam(datiIMPR, 9).getValue();
            cnatec = StringUtils.stripToEmpty(cnatec);
            //String dnatec = (String) SqlManager.getValueFromVectorParam(datiIMPR, 10).getValue();
            //dnatec = StringUtils.stripToEmpty(dnatec);
            Long inctec = (Long) SqlManager.getValueFromVectorParam(datiIMPR, 11).getValue();
            String oggsoc = (String) SqlManager.getValueFromVectorParam(datiIMPR, 12).getValue();
            oggsoc = StringUtils.stripToEmpty(oggsoc);
            String indimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 13).getValue();
            indimp = StringUtils.stripToEmpty(indimp);
            String nciimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 14).getValue();
            nciimp = StringUtils.stripToEmpty(nciimp);
            String proimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 15).getValue();
            proimp = StringUtils.stripToEmpty(proimp);
            String capimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 16).getValue();
            capimp = StringUtils.stripToEmpty(capimp);
            String locimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 17).getValue();
            locimp = StringUtils.stripToEmpty(locimp);
            String codcit = (String) SqlManager.getValueFromVectorParam(datiIMPR, 18).getValue();
            codcit = StringUtils.stripToEmpty(codcit);
            //Long nazimp = (Long) SqlManager.getValueFromVectorParam(datiIMPR, 19).getValue();
            String telimp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 20).getValue();
            telimp = StringUtils.stripToEmpty(telimp);
            String faximp = (String) SqlManager.getValueFromVectorParam(datiIMPR, 21).getValue();
            faximp = StringUtils.stripToEmpty(faximp);
            String telcel = (String) SqlManager.getValueFromVectorParam(datiIMPR, 22).getValue();
            telcel = StringUtils.stripToEmpty(telcel);
            String emaiip = (String) SqlManager.getValueFromVectorParam(datiIMPR, 23).getValue();
            emaiip = StringUtils.stripToEmpty(emaiip);
            String emai2ip = (String) SqlManager.getValueFromVectorParam(datiIMPR, 24).getValue();
            emai2ip = StringUtils.stripToEmpty(emai2ip);
            String indweb = (String) SqlManager.getValueFromVectorParam(datiIMPR, 25).getValue();
            indweb = StringUtils.stripToEmpty(indweb);

            /*
            if(!"".equals(cgenimp)) {
              fornitore.setIdFornitore(cgenimp); 
            }
             */
            fornitore.setCodiceFornitore(dittaRTI);
            fornitore.setRagioneSociale(nomest);
            //if(tipimp1!=null) {
            fornitore.setTipologia("I");//tipimp1.toString()
            //}
            String descFormaGiuridica = "";
            if(natgiui != null) {
              descFormaGiuridica = (String) sqlManager.getObject("select tab1desc from tab1 " +
                  " where tab1cod = ? and tab1tip = ? ", new Object[] {"G_043",natgiui
              });
            }
            fornitore.setFormaGiuridica(descFormaGiuridica);
            fornitore.setCodiceFiscale(cfimp);
            fornitore.setPartitaIva(pivimp);
            fornitore.setCognome(cognome);
            fornitore.setNome(nome);
            fornitore.setProvinciaNascita(pronas);
            fornitore.setComuneNascita(cnatec);
            String descTitolo = "";
            if(inctec != null) {
              descTitolo = (String) sqlManager.getObject("select tab1desc from tab1 " +
                  " where tab1cod = ? and tab1tip = ? ", new Object[] {"Ag004",inctec
              });
            }
            fornitore.setTitolo(descTitolo);
            fornitore.setOggettoSociale(oggsoc);

            WSERPIndirizzoType indirizzo = new WSERPIndirizzoType();

            indirizzo.setIndirizzo(indimp);
            indirizzo.setNumCivico(nciimp);
            String descrizioneProvincia = "";
            if(proimp != null) {
              descrizioneProvincia = (String) sqlManager.getObject("select tab3desc from tab3 " +
                  " where tab3cod = ? and tab3tip = ? ", new Object[] {"Agx15",proimp
              });
            }
            indirizzo.setProvincia(descrizioneProvincia);
            indirizzo.setCap(capimp);
            indirizzo.setComune(locimp);
            indirizzo.setCodIstatComune(codcit);

            String descrizioneNazione = "";
            if(natgiui != null) {
              descrizioneNazione = (String) sqlManager.getObject("select tab1desc from tab1 " +
                  " where tab1cod = ? and tab1tip = ? ", new Object[] {"Ag010",natgiui
              });
            }
            /*
            if(nazimp != null){
              if(Long.valueOf(1).equals(nazimp)){
                siglanaz = "IT";
              }else{
                siglanaz = (String) this.sqlManager.getObject("select codiso from g_nazioni where codice =? ",
                    new Object[]{nazimp});
              }
            }*/

            indirizzo.setNazione(descrizioneNazione);
            indirizzo.setTelefono(telimp);
            indirizzo.setFax(faximp);
            indirizzo.setCellulare(telcel);
            indirizzo.setEmail(emaiip);
            indirizzo.setPec(emai2ip);
            indirizzo.setUrl(indweb);
            
            WSERPIndirizzoType[] indirizzoArray = new WSERPIndirizzoType[1];
            indirizzoArray[0] = indirizzo;
            fornitore.setIndirizziArray(indirizzoArray);

            wserpFornitoreRes = gestioneWSERPManager.wserpCreaFornitore(username,password, "WSERP", fornitore);
          }
        }
      }
    }
    return wserpFornitoreRes;

  }

}


